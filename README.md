# SysDesign8

Цей проект є простим банківським бекендом, що використовується для вправи з проектування систем. Він демонструє застосування Spring Boot з JPA, міграціями Flyway, базою даних PostgreSQL (яка запускається за допомогою Docker Compose) та HTTP JSON API для рахунків, транзакцій, періодичних платежів та базової аналітики.

## Попередні вимоги
- Java 17+ (або версія Java, налаштована для проекту)
- Gradle (включено обгортку `./gradlew`)
- Docker та Docker Compose (для запуску бази даних PostgreSQL)

## Швидкий старт
1. Переконайтеся, що нічого не слухає на порту 5432 на вашій машині. Якщо щось слухає, зупиніть це або змініть відображення порту Docker Compose.

2. Запустіть базу даних за допомогою Docker Compose (з кореневої папки проекту):

```bash
# запустити postgres, визначений у docker/docker-compose.yml, у фоновому режимі
docker compose -f docker/docker-compose.yml up -d --force-recreate

# показати служби та порти compose
docker compose -f docker/docker-compose.yml ps
```

3. Побудуйте та запустіть застосунок Spring Boot локально (застосунок підхопить налаштування бази даних з `src/main/resources/application.yaml`):

```bash
# збірка (пропустити тести для швидшої ітерації)
./gradlew clean build -x test

# запустити застосунок
./gradlew bootRun
```

4. Застосунок за замовчуванням працює на http://localhost:8080

Якщо ви надаєте перевагу запуску застосунку в Docker (не включено тут), побудуйте jar-файл та створіть Dockerfile, який його запускає, або використовуйте `./gradlew bootJar` та невеликий образ контейнера.

## Примітки щодо бази даних
- Якщо журнали контейнера повідомляють "Директорія бази даних PostgreSQL, здається, містить базу даних; Пропуск ініціалізації", це означає, що директорія бази даних вже існує в зазначеному томі. Це нормально, якщо вона містить вашу схему/дані.
- Якщо міграції Flyway не були виконані або `flyway_schema_history` порожня, тоді як таблиці існують, це вказує на те, що дані були заповнені поза межами Flyway (або попередня ініціалізація створила схему). Підтвердіть міграції та налаштування Flyway у `src/main/resources/application.yaml`.

## API: швидкі приклади curl
- Базовий URL: http://localhost:8080/api
- Багато кінцевих точок вимагають заголовок користувача: `X-User-Id: <userId>`

1) Список рахунків для користувача
```bash
curl -v -H "X-User-Id: 42" \
  http://localhost:8080/api/accounts
```
Очікується: 200 OK, JSON масив об'єктів рахунків (DTO AccountResponse)

2) Отримати баланс рахунку
```bash
curl -v http://localhost:8080/api/accounts/1/balance
```
Очікується: 200 OK, тіло є числом/рядком з балансом (наприклад, "1006.55")

3) Отримати транзакції для рахунку (пагіновані)
```bash
curl -v "http://localhost:8080/api/accounts/1/transactions?page=0&size=20"
```
Очікується: 200 OK, JSON масив DTO `TransactionResponse`.

4) Зробити переказ
```bash
curl -v -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 50.00,
    "description": "Transfer to savings",
    "userId": 42
  }' \
  http://localhost:8080/api/transfers
```
Очікується: 200 OK, JSON TransactionResponse для створеної транзакції.

5) Зробити платіж
```bash
curl -v -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "recipientAccount": "5005",
    "amount": 25.50,
    "description": "Utility bill",
    "userId": 42
  }' \
  http://localhost:8080/api/payments
```
Очікується: 200 OK, JSON TransactionResponse для створеної транзакції.

6) Заблокувати/розблокувати рахунок
```bash
curl -v -X POST \
  -H "Content-Type: application/json" \
  -d '{"lock": true}' \
  http://localhost:8080/api/accounts/1/lock
```
Очікується: 200 OK, AccountResponse з `isLocked: true`.

7) Налаштування періодичного платежу
```bash
curl -v -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "amount": 1000.00,
    "frequency": "monthly",
    "nextPaymentDate": "2025-12-04",
    "recipientAccount": "5005"
  }' \
  http://localhost:8080/api/recurring/setup
```
Очікується: 200 OK, створений об'єкт RecurringPayment (JSON)

8) Отримати щомісячну виписку (YYYY-MM)
```bash
curl -v -H "X-User-Id: 42" \
  http://localhost:8080/api/statements/2025-12
```
Очікується: 200 OK, JSON StatementResponse { month: "2025-12", transactions: [ ... TransactionResponse ... ] }

9) Отримати аналітику витрат для користувача
```bash
curl -v -H "X-User-Id: 42" \
  http://localhost:8080/api/analytics/spending
```
Очікується: 200 OK, JSON SpendingAnalyticsResponse { totalSpent: <number>, byType: {"TRANSFER": .., "PAYMENT": .. } }

10) Отримати журнал аудиту для транзакції
```bash
curl -v http://localhost:8080/api/audit/123
```
Очікується: 200 OK, JSON масив записів AuditLogResponse (id, action, userId, details, timestamp)

## Поради щодо усунення неполадок
- Якщо ви бачите: "Не вдалося налаштувати DataSource: атрибут 'url' не вказано" — переконайтеся, що у застосунку правильні властивості бази даних, активний правильний профіль Spring або база даних запущена.
- Якщо команди Docker завершуються з помилкою доступу до /var/run/docker.sock, запустіть команду Docker Compose з `sudo` або додайте свого користувача до групи `docker` (рекомендується відрегулювати системні дозволи).
- Якщо Flyway повідомляє "Непідтримувана база даних: PostgreSQL 15.15", переконайтеся, що ви використовуєте сумісну комбінацію Flyway/Postgres і що налаштування Flyway у `application.yaml` правильні.
