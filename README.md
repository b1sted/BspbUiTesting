<h3 align="center">BspbUiTesting</h3>

<p align="center">
    <strong>
    Автоматическое тестирование UI сайта bspb.ru, разработанное на Java 21, Selenium, Junit.
    </strong>
</p>

## Стек
- Java 21
- Selenium
- Junit
- Gradle (DSL Groovy)
- Jenkins

## CI/CD

Конфигурация CI-сервера (Jenkins в Docker, Nginx, systemd, port knocking) — в [`jenkins/`](./jenkins/README.md).

К прогону тестов и самому проекту отношения не имеет, нужна только при развёртывании/обслуживании VDS.
