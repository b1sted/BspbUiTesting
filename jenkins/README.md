<h3 align="center">Jenkins VDS</h3>

<p align="center">
    <strong>
    Сервер CI/CD на базе Jenkins, развернутый в Docker за reverse-proxy Nginx и Cloudflare.<br>
    Подробности (права доступа, iptables, port knocking) — в <a href="DOCS.md">DOCS.md</a>.
    </strong>
</p>

## Структура директории

```
jenkins
├── docker-compose.yml
├── nginx/
│   └── default.conf
├── systemd/
│   └── jenkins-compose.service
├── knockd/
│   └── knockd.conf.example
└── pipeline-example/
    └── Jenkinsfile.example
```

## Куда что кладётся на сервере

| Из репозитория                         | Куда                                          | Зачем                         |
|----------------------------------------|-----------------------------------------------|-------------------------------|
| `docker-compose.yml`                   | `/opt/jenkins/docker-compose.yml`             | `docker compose up -d`        |
| `nginx/default.conf`                   | `/opt/jenkins/nginx/default.conf`             | Монтируется в контейнер nginx |
| `systemd/jenkins-compose.service`      | `/etc/systemd/system/jenkins-compose.service` | Автозапуск стека при загрузке |
| `knockd/knockd.conf.example`           | `/etc/knockd.conf` (с реальными портами)      | Port knocking для SSH         |
| `pipeline-example/Jenkinsfile.example` | Вставляется в Pipeline script в Jenkins UI    | Рабочий pipeline              |

Итоговый `/opt/jenkins`:

```
/opt/jenkins
├── docker-compose.yml       # Из директории
├── jenkins_home/            # Bind mount, создать заранее вручную (см. DOCS.md)
└── nginx/
    ├── default.conf         # Из директории
    └── ssl/                 # Вручную, в git не хранится
        ├── cloudflare.crt
        └── cloudflare.key
```

`jenkins_home` — обычная директория на хосте (`./jenkins_home:/var/jenkins_home`), не именованный volume.

Права (`user:docker` + setgid) нужно выставить **на оба уровня** — и на `/opt/jenkins`, и на `jenkins_home`. Сделать это нужно **до** `docker compose up`: иначе Docker создаст `jenkins_home` сам, от `root:root`, без нужного setgid-бита. См. [DOCS.md → «Права доступа»](DOCS.md#права-доступа).

## Быстрый старт

```bash
git clone https://github.com/b1sted/BspbUiTesting.git /tmp/repo

mkdir -p /opt/jenkins
chown user:docker /opt/jenkins
chmod g+s /opt/jenkins

cp /tmp/repo/jenkins/docker-compose.yml /opt/jenkins/
cp -r /tmp/repo/jenkins/nginx /opt/jenkins/
cd /opt/jenkins

# jenkins_home наследует group от /opt/jenkins, но setgid явно не наследуется — см. DOCS.md → «Права доступа»
mkdir jenkins_home
chown user:docker jenkins_home
chmod g+s jenkins_home

# Подставить свои <UID>:<GID> в docker-compose.yml
# Положить сертификаты Cloudflare Origin CA в nginx/ssl/

docker compose up -d
```

Вне `/opt/jenkins`:

```bash
# Автозапуск через systemd
sudo cp /tmp/repo/jenkins/systemd/jenkins-compose.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now jenkins-compose.service

# Опционально: port knocking для SSH
sudo cp /tmp/repo/jenkins/knockd/knockd.conf.example /etc/knockd.conf
# Отредактировать /etc/knockd.conf — заменить плейсхолдеры на реальные порты
```

Отдельно — наполнить workspace job'а `BspbUiTesting` (job должен быть уже создан в Jenkins UI, см. [DOCS.md → «Пример пайплайна Jenkins»](DOCS.md#пример-пайплайна-jenkins)). Jenkins код сам не тянет (`skipDefaultCheckout()`), поэтому актуальность workspace — забота человека/скрипта. Проще всего сделать сам workspace git-клоном и потом просто `git pull`:

```bash
git clone https://github.com/b1sted/BspbUiTesting.git /opt/jenkins/jenkins_home/workspace/BspbUiTesting

# Перед каждым запуском job'а:
cd /opt/jenkins/jenkins_home/workspace/BspbUiTesting && git pull
```

Гонять `git pull` можно перед каждым запуском не глядя — если изменений нет, он просто ответит «Already up to date.» и ничего не сделает. А вот если пропустить его именно в тот раз, когда изменения были, job тихо прогонит тесты по старому коду.

Альтернатива — копировать из уже склонированного `/tmp/repo` (`cp -r /tmp/repo/. .../BspbUiTesting/`). Но `cp` не подчищает workspace от файлов, которые пропали из новой версии проекта — это придётся делать вручную. `git pull` делает это сам.

## Что внутри

| Компонент | Назначение                                                                             |
|-----------|----------------------------------------------------------------------------------------|
| `jenkins` | CI/CD-сервер, доступен только внутри docker-сети (8080)                                |
| `nginx`   | reverse proxy + TLS, единственная точка входа снаружи (80/443, только с IP Cloudflare) |

## Job BspbUiTesting

Запускается над содержимым `/opt/jenkins/jenkins_home/workspace/BspbUiTesting`. Полноценный CI (Docker-агент, `./gradlew clean test`, отчёт Allure), но доставка кода в workspace — ручная: в пайплайне стоит `skipDefaultCheckout()`, актуальность кода поддерживается вручную (`git pull`/`rsync`). Подробнее — [DOCS.md → «Пример пайплайна Jenkins»](DOCS.md#пример-пайплайна-jenkins).

Директория `jenkins/` (конфигурация CI-сервера) попадает в workspace вместе со всем репозиторием при чекауте, но пайплайн её не читает.

Перед деплоем на боевой сервер — обязательно раздел про безопасность в [DOCS.md](DOCS.md).
