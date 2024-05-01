# Запуск 
Чтобы запускать сервер локально в докере необходимо создать файл `.env` со следующим содержимым:

```text
COMPOSE_PROFILES=dev
CHAT_TOKEN=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
SERVER_HOST=localhost:8080
SERVER_PROTOCOL=http
SERVER_WEBSOCKET_PROTOCOL=ws
```

Для запуска можно воспользоваться следующим скриптом и запустить его прямо в idea через две зеленые стрелки слева от скрипта
```shell
docker system prune -a -f
docker compose --profile dev up -d  
docker compose down   
docker compose pull  
./gradlew build
```

```shell
# Пересборка образа
docker-compose build
docker-compose up -d
```

дефолтные данные для входа 

login: `admin`
password: `pass`