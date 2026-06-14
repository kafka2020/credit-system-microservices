# Credit System Microservices

Домашнее задание по курсу **Нетология Java-разработчик**.
Тема: **Брокеры сообщений: Kafka & RabbitMQ**

## Архитектура

```
Пользователь
    │
    │ POST /api/credit/apply
    ▼
┌─────────────────────┐      Kafka: credit-applications      ┌──────────────────────────┐
│  credit-api-service │ ──────────────────────────────────► │ credit-processor-service │
│  (Spring Boot)      │                                      │  (Spring Boot)           │
│  port: 8080         │ ◄────────────────────────────────── │                          │
│  + PostgreSQL       │      RabbitMQ: credit.decision.queue │  Логика одобрения/отказа │
└─────────────────────┘                                      └──────────────────────────┘
```

### Поток данных
1. Клиент отправляет `POST /api/credit/apply` → **credit-api-service**
2. Сервис сохраняет заявку в PostgreSQL со статусом `PROCESSING`
3. Отправляет `CreditApplicationEvent` в **Kafka** (топик `credit-applications`)
4. **credit-processor-service** получает событие из Kafka
5. Принимает решение: платёж не должен превышать 50% дохода
6. Отправляет `CreditDecisionEvent` в **RabbitMQ** (очередь `credit.decision.queue`)
7. **credit-api-service** получает решение из RabbitMQ
8. Обновляет статус заявки в БД (`APPROVED` или `REJECTED`)
9. Клиент опрашивает `GET /api/credit/status/{id}` для получения результата

## Запуск

### Требования
- Docker и Docker Compose
- Порты: 8080, 5432, 9092, 5672, 15672

### Одна команда:
```bash
docker-compose up --build
```

После запуска:
- **API**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (login: `credit_user` / `credit_pass`)

## API

### Оформить заявку на кредит
```http
POST http://localhost:8080/api/credit/apply
Content-Type: application/json

{
  "amount": 500000,
  "termMonths": 24,
  "monthlyIncome": 80000,
  "currentDebtLoad": 10000,
  "creditScore": 720
}
```
**Ответ:**
```json
{"applicationId": 1}
```

### Получить статус заявки
```http
GET http://localhost:8080/api/credit/status/1
```
**Ответ:**
```json
{"applicationId": "1", "status": "APPROVED"}
```

## Логика одобрения кредита

> Ежемесячный платёж + текущая кредитная нагрузка ≤ 50% от дохода

Месячный платёж рассчитывается по формуле аннуитетного платежа при ставке 15% годовых.

**Пример одобрения:** доход 80 000 ₽, кредит 500 000 ₽ на 24 мес → платёж ≈ 24 241 ₽, нагрузка 10 000 ₽ → итого 34 241 ₽ (42.8%) ✅

**Пример отказа:** доход 40 000 ₽, нагрузка 25 000 ₽, кредит 2 000 000 ₽ на 12 мес → платёж ≈ 180 415 ₽ → итого 205 415 ₽ >> 50% ❌

## Стек технологий

| Компонент | Технология |
|---|---|
| Язык | Java 17 |
| Фреймворк | Spring Boot 3.2 |
| Брокер 1 | Apache Kafka |
| Брокер 2 | RabbitMQ |
| БД | PostgreSQL 16 |
| Миграции | Flyway |
| Контейнеры | Docker / Docker Compose |
| Сборка | Maven |
