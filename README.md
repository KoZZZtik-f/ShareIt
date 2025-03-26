# ShareIt

### Сервис для аренды вещей между пользователями

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.5-green)
![MySQL](https://img.shields.io/badge/MySQL-8-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📌 О проекте
**ShareIt** — это бэкенд-приложение для шеринга вещей. Пользователи могут:
- Добавлять вещи в аренду
- Бронировать вещи на определённые даты
- Оставлять отзывы после аренды
- Искать доступные вещи

Аналог Airbnb, но для любых предметов (дрели, велосипеды, книги и т.д.).

---

## 🛠 Технологии
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **Hibernate**
- **MySQL 8** (или H2 для тестов)
- **Lombok**
- **Maven**


---

## 🚀 Запуск проекта

### 1. Требования
- Установленные:
  - JDK 17+
  - Maven 3.8+
  - MySQL 8+ (или Docker)

### 2. Настройка БД
```sql
CREATE DATABASE shareit;
CREATE USER 'shareit_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON shareit.* TO 'shareit_user'@'localhost';
FLUSH PRIVILEGES;
```

# 📚 API Endpoints

## Пользователи (`/users`)

| Метод    | Эндпоинт          | Описание                          |
|----------|-------------------|-----------------------------------|
| `POST`   | `/`               | Создать пользователя              |
| `PATCH`  | `/{userId}`       | Обновить пользователя             |
| `GET`    | `/{userId}`       | Получить пользователя по ID       |
| `GET`    | `/`               | Получить всех пользователей       |
| `DELETE` | `/{userId}`       | Удалить пользователя              |

---

## Вещи (`/items`)

| Метод    | Эндпоинт                   | Описание                              |
|----------|----------------------------|---------------------------------------|
| `POST`   | `/`                        | Добавить вещь                         |
| `PATCH`  | `/{itemId}`                | Обновить вещь                         |
| `GET`    | `/{itemId}`                | Получить вещь по ID                   |
| `GET`    | `/`                        | Получить все вещи пользователя        |
| `GET`    | `/search?text={query}`     | Поиск вещей                          |
| `POST`   | `/{itemId}/comment`        | Добавить комментарий                  |

---

## Бронирования (`/bookings`)

| Метод    | Эндпоинт                                      | Описание                                      |
|----------|-----------------------------------------------|-----------------------------------------------|
| `POST`   | `/`                                           | Создать бронирование                         |
| `PATCH`  | `/{bookingId}?approved={true/false}`          | Подтвердить/отклонить бронирование           |
| `GET`    | `/{bookingId}`                                | Получить бронирование по ID                  |
| `GET`    | `/?state={state}&from={from}&size={size}`     | Получить бронирования пользователя           |
| `GET`    | `/owner?state={state}&from={from}&size={size}`| Получить бронирования владельца              |

### Параметры для бронирований

- **state**: Фильтр по статусу бронирования:
  - `ALL` (по умолчанию)
  - `CURRENT`
  - `PAST`
  - `FUTURE`
  - `WAITING`
  - `REJECTED`
  
- **from**: Начальная позиция (по умолчанию `0`)
- **size**: Количество элементов (по умолчанию `10`)

### Примеры запросов:
```http
GET /bookings?state=PAST&from=0&size=5
GET /bookings/owner?state=FUTURE

