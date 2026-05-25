# Bank Cards REST API

Backend-приложение на Java (Spring Boot) для управления банковскими картами, переводами и ролевым доступом.

## Технологический стек
* **Java 17** / **Spring Boot 3.2.5**
* **Spring Security** (JWT Authentication)
* **Spring Data JPA** / **PostgreSQL**
* **Liquibase** (Миграции БД)
* **Docker Compose**
* **Springdoc OpenAPI (Swagger UI)**

## Быстрый запуск (Dev-среда)

### 1. Клонирование репозитория и сборка
```bash
git clone https://github.com/nosteam595/bank_rest.git
cd bank_rest
./mvnw clean package -DskipTests
```

### 2. Запуск инфраструктуры через Docker
Команда поднимет базу данных PostgreSQL. Миграции Liquibase выполнятся автоматически при старте приложения.
```bash
docker-compose up -d
```

### 3. Запуск приложения
```bash
./mvnw spring-boot:run
```
Приложение будет доступно по адресу: `http://localhost:8080`

## Документация API
* **Swagger UI (Интерактивная документация):** `http://localhost:8080/swagger-ui.html`
* Для работы с тестами в Swagger необходимо ввести токен, возвращаемый после успешного логина.
* **Спецификация OpenAPI:** Файл доступен в корне проекта: `docs/openapi.yaml`

## Учетные записи по умолчанию
* **Администратор:** login: `admin` / password: `admin123`
* **Пользователь:** login: `user1` / password: `user123`
