<h3 align="center">Настройка VDS с Jenkins</h3>

<p align="center">
    <strong>
    Краткий обзор — в <a href="README.md">README.md</a>.
    </strong>
</p>
<p align="center">
    <a href="#обозначения">Обозначения</a> •
    <a href="#права-доступа">Права доступа</a> •
    <a href="#обратный-прокси">Обратный прокси</a> •
    <a href="#автозапуск">Автозапуск</a> • <br>
    <a href="#файрвол">Файрвол</a> •
    <a href="#защита-ssh">Защита SSH</a> •
    <a href="#пример-пайплайна-jenkins">Пример пайплайна Jenkins</a>
</p>
<hr>

## Обозначения

| Плейсхолдер                             | Что это                                                                                                           |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `user`                                  | Пользователь на сервере с `sudo`, не root; под ним же работает Docker и Jenkins-контейнер (`user: "<UID>:<GID>"`) |
| `<UID>:<GID>`                           | ID пользователя и группы `docker` — `id -u user`, `getent group docker`                                           |
| `<ip_сервера>`, `<port>`, `<port1/2/3>` | Заменить на реальные значения                                                                                     |

## Права доступа

Права выставляются не только на `jenkins_home`, но и на родительский `/opt/jenkins` — иначе setgid не действует на весь путь и часть новых файлов/директорий может унаследовать не ту группу.

```
drwxrwsr-x  3 user docker 4096 Jul  6 13:00 /opt/jenkins
drwxrwsr-x  4 user docker 4096 Jul  6 13:28 jenkins_home
```

Обеим директориям нужны группа `docker` и setgid-бит (`s` в правах группы). GID группы `docker` должен совпадать со вторым числом в `user: "<UID>:<GID>"` из docker-compose.yml. Setgid нужен, чтобы файлы, которые Jenkins создаёт внутри `jenkins_home`, наследовали группу `docker`, а не основную группу процесса.

```bash
chown user:docker /opt/jenkins
chmod g+s /opt/jenkins

chown user:docker jenkins_home
chmod g+s jenkins_home
```

Setgid-бит не наследуется автоматически на дочерние директории. Родитель с `g+s` гарантирует только то, что *новая* директория, созданная внутри него, унаследует группу `docker`. Сам setgid на эту новую директорию всё равно нужно выставлять отдельно.

## Обратный прокси

Конфиг: [`nginx/default.conf`](./nginx/default.conf).

Терминирует TLS (сертификат Cloudflare Origin CA) и проксирует на `http://jenkins:8080` внутри docker-сети — порт 8080 наружу не публикуется.

Сертификат Cloudflare Origin CA валиден только между Cloudflare и origin-сервером — браузер напрямую его не примет, это ожидаемо. Кладётся в `/opt/jenkins/nginx/ssl/`, в git не хранится.

## Автозапуск

Файл: [`systemd/jenkins-compose.service`](./systemd/jenkins-compose.service).

Обеспечивает автозапуск стека после перезагрузки/старта Docker daemon.

```bash
sudo cp systemd/jenkins-compose.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now jenkins-compose.service
```

## Файрвол

Docker-compose поднимает `jenkins` и `nginx` в общей docker-сети. В примерах ниже она обозначена как `<bridge_сеть>` / `172.18.0.0/16` — на другом сервере имя и подсеть будут другими, посмотреть свои: `docker network ls`, `docker network inspect <имя_сети>`.

Единственная точка входа — Nginx (80/443, только с IP Cloudflare). Порт агентов `50000` наружу не открыт.

### 1. Политики по умолчанию

```bash
iptables -P INPUT DROP
iptables -P FORWARD DROP
iptables -P OUTPUT ACCEPT
```

### 2. IPset для сетей Cloudflare

```bash
ipset create cloudflare hash:net

for ip in $(curl -s https://www.cloudflare.com/ips-v4); do
    ipset add cloudflare "$ip"
done
```

### 3. Цепочка INPUT

```bash
iptables -A INPUT -i lo -j ACCEPT
iptables -A INPUT -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT
iptables -A INPUT -p icmp --icmp-type echo-request -m limit --limit 1/sec --limit-burst 5 -j ACCEPT
iptables -A INPUT -p tcp -m set --match-set cloudflare src -m multiport --dports 80,443 -j ACCEPT
iptables -A INPUT -m limit --limit 5/min --limit-burst 5 -j LOG --log-level warn --log-prefix "IPTABLES-DROP: "
```

SSH (22) сюда не включён — доступ через port knocking, см. [ниже](#защита-ssh).

### 4. Docker-цепочки (автоматические)

Не редактируются руками — Docker генерирует их сам на основе `ports:` при пересоздании контейнера:

```yaml
# nginx
ports:
  - "80:80"
  - "443:443"       # -> DOCKER: ACCEPT tcp dpt:80/443 to <IP контейнера nginx в docker-сети>
```

`jenkins` в эту цепочку правил не добавляет (только `expose`, без `ports:`).

> **Если появятся внешние агенты Jenkins:** не открывать `50000:50000` на весь интернет.
> - статические IP агентов — точечный ipset;
> - непредсказуемые IP — WebSocket-агенты (Jenkins ≥ 2.217, `Inbound TCP Agent Protocol/4` поверх HTTPS/443), отдельный порт не нужен;
> - либо VPN.

### 5. Цепочка DOCKER-USER

Разрешает контейнерам исходящий DNS и HTTP(S) (нужно Jenkins для плагинов и образов). `172.17.0.0/16` — стандартная сеть `docker0`; `<docker_подсеть>` — подсеть compose-сети из шага выше (`docker network inspect <имя_сети>`), подставить вместо неё реальную:

```bash
iptables -A DOCKER-USER -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT

iptables -A DOCKER-USER -s 172.17.0.0/16 -p udp --dport 53 -j ACCEPT
iptables -A DOCKER-USER -s 172.17.0.0/16 -p tcp --dport 53 -j ACCEPT
iptables -A DOCKER-USER -s 172.17.0.0/16 -p tcp -m multiport --dports 80,443 -j ACCEPT

iptables -A DOCKER-USER -s <docker_подсеть> -p udp --dport 53 -j ACCEPT
iptables -A DOCKER-USER -s <docker_подсеть> -p tcp --dport 53 -j ACCEPT
iptables -A DOCKER-USER -s <docker_подсеть> -p tcp -m multiport --dports 80,443 -j ACCEPT

iptables -A DOCKER-USER -j RETURN
```

### 6. Сохранение правил

```bash
apt install iptables-persistent ipset-persistent
netfilter-persistent save
```

### Проверка после изменений

```bash
sudo iptables -L DOCKER -n -v --line-numbers   # Правило пропало?
sudo ss -tlnp | grep <порт>                    # Порт слушается на хосте?
nc -zv <ip_сервера> <порт>                     # Доступен снаружи (не из локальной сети)
```

Правила `DOCKER*` обновляются только при пересоздании контейнера:

```bash
docker compose up -d --force-recreate <service>
```

`restart` недостаточно — Docker перечитывает `ports:` только при пересоздании.

## Защита SSH

Конфиг: [`knockd/knockd.conf.example`](./knockd/knockd.conf.example).

Порт 22 закрыт по умолчанию, открывается после "стука" в заданную последовательность портов. После правильной последовательности `knockd` временно (на `cmd_timeout` сек) добавляет разрешающее правило в `INPUT` для IP клиента; `stop_command` убирает его по истечении времени.

```bash
knock <ip_сервера> <port1> <port2> <port3>
ssh user@<ip_сервера>
```

## Пример пайплайна Jenkins

Рабочий pipeline: [`pipeline-example/Jenkinsfile.example`](./pipeline-example/Jenkinsfile.example). В репозитории — для истории; исполняется как содержимое поля **Pipeline script** в job'е `BspbUiTesting` (без SCM).

### Требование к workspace

Workspace job'а: `/opt/jenkins/jenkins_home/workspace/BspbUiTesting`. Должен содержать проект целиком — а проект как репозиторий включает в себя и `jenkins/`:

```
BspbUiTesting/            (= workspace)
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── README.md
├── src/
│   └── ...
└── jenkins/              # Присутствует, т.к. чекаутится весь репозиторий, но роли не играет
```

Присутствие или отсутствие `jenkins/` в workspace никак не влияет на прогон — шаги пайплайна к ней не обращаются (это конфигурация самого CI-сервера, а не часть тестируемого проекта).

В пайплайне стоит `skipDefaultCheckout()` — Jenkins не тянет код сам. Сборка и тесты при этом полноценные: агент в Docker, `./gradlew clean test`, отчёт в Allure. Ручной шаг только один — доставка актуального кода в workspace (`git pull`/`rsync`). Если тесты гоняются по старой версии — проверяйте сначала это.

### Требования к окружению

Плагин **Allure Jenkins Plugin**, инструмент `allure-cli` (Manage Jenkins → Tools), Docker-агент с доступом к `docker.sock`.

При изменении пайплайна править в двух местах: Jenkins UI (исполняемая версия) и `pipeline-example/Jenkinsfile.example` в репозитории.
