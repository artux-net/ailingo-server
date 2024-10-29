# Ailingo (backend)

Интеллектуальная платформа изучения естественных языков

### prod среда
<a href="https://app.artux.net/ailingo/swagger-ui/index.html">
    <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white" />
</a>

### [frontend](https://ailingo.artux.net/)

## Запуск без Docker образа
Чтобы запускать сервер без создания образа, нужно поднять только БД, 
для этого перейти в директорию dev, открыть `docker-compose-local.yaml` и там прожать зеленую стрелку напротив `services:`
Это действие поднимает контейнер базы данных, и приложение сможет к ней подключиться.
Далее находим файл `AilingoServerApplication.kt` и запускаем функцию `main()`.
Приложение подключается к базе данных и выполняет миграции, расположенные в его ресурсах - scr/main/resources/db/**
После запуска приложения Swagger UI доступен по ссылке http://localhost:8080/ailingo/swagger-ui/index.html

Данные для подключения к PostgreSQL:
```yaml
url: jdbc:postgresql://localhost:5432/lingo
username: lingo
password: lingo
```
Затем, можно посмотреть какие данные хранятся сейчас в БД. 
Справа в IDE выбрать Datasources и добавить там подключение с настройками выше.
Открыть схему `public`.

## Запуск c Docker образом
Для запуска можно воспользоваться следующим скриптом и запустить его прямо в IDEA через две зеленые стрелки слева от скрипта (IDEA почему-то выполняет в обратном порядке, с конца)
```shell
cd dev
docker system prune -a -f
docker compose --profile dev up
docker compose down   
docker compose pull  
./gradlew build
```

```shell
# Пересборка образа
cd dev
docker-compose build
docker-compose up
```

# Авторизация
Данные для входа 

```
login: `admin`
password: `pass`
```