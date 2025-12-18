# SysDesign8

This project is a simple banking backend used for a systems design exercise. It demonstrates a Spring Boot application with JPA, Flyway migrations, a PostgreSQL database (run via Docker Compose), and HTTP JSON APIs for accounts, transactions, recurring payments and basic analytics.

## Prerequisites
- Java 17+ (or the project's configured Java version)
- Gradle (the wrapper `./gradlew` is included)
- Docker and Docker Compose (to run the PostgreSQL database)

## Quick start
1. Make sure nothing is listening on port 5432 on your machine. If something is, stop it or change the Docker Compose port mapping.

2. Start the database with Docker Compose (from project root):

```bash
# start postgres defined in docker/docker-compose.yml detached
docker compose -f docker/docker-compose.yml up -d --force-recreate

# show compose services and ports
docker compose -f docker/docker-compose.yml ps
```

3. Build and run the Spring Boot application locally (the app will pick up DB settings from `src/main/resources/application.yaml`):

```bash
# build (skip tests for faster iteration)
./gradlew clean build -x test

# run the app
./gradlew bootRun
```

4. The application runs by default on http://localhost:8080

If you prefer to run the app in Docker (not included here), build the jar and create a Dockerfile that runs it, or use `./gradlew bootJar` and a small container image.

## Notes about the DB
- If the container logs say "PostgreSQL Database directory appears to contain a database; Skipping initialization" it means a database directory already exists in the named volume. That's okay if it contains your schema/data.
- If Flyway migrations didn't run or `flyway_schema_history` is empty while tables exist, that indicates data was seeded outside of Flyway (or a previous init created the schema). Confirm the migrations and Flyway configuration in `src/main/resources/application.yaml`.

## API: quick curl examples
- Base URL: http://localhost:8080/api
- Many endpoints require a user header: `X-User-Id: <userId>`

1) List accounts for a user
```bash
curl -v -H "X-User-Id: 42" \
  http://localhost:8080/api/accounts
```
Expected: 200 OK, JSON array of account objects (AccountResponse DTO)

2) Get account balance
```bash
curl -v http://localhost:8080/api/accounts/1/balance
```
Expected: 200 OK, body is a number/string with the balance (e.g. "1006.55")

3) Get transactions for an account (paginated)
```bash
curl -v "http://localhost:8080/api/accounts/1/transactions?page=0&size=20"
```
Expected: 200 OK, JSON array of `TransactionResponse` DTOs.

4) Make a transfer
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
Expected: 200 OK, JSON TransactionResponse for the created transaction.

5) Make a payment
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
Expected: 200 OK, JSON TransactionResponse for the created transaction.

6) Lock/unlock account
```bash
curl -v -X POST \
  -H "Content-Type: application/json" \
  -d '{"lock": true}' \
  http://localhost:8080/api/accounts/1/lock
```
Expected: 200 OK, AccountResponse with `isLocked: true`.

7) Setup recurring payment
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
Expected: 200 OK, created RecurringPayment object (JSON)

8) Get monthly statement (YYYY-MM)
```bash
curl -v -H "X-User-Id: 42" \
  http://localhost:8080/api/statements/2025-12
```
Expected: 200 OK, JSON StatementResponse { month: "2025-12", transactions: [ ... TransactionResponse ... ] }

9) Get spending analytics for user
```bash
curl -v -H "X-User-Id: 42" \
  http://localhost:8080/api/analytics/spending
```
Expected: 200 OK, JSON SpendingAnalyticsResponse { totalSpent: <number>, byType: {"TRANSFER": .., "PAYMENT": .. } }

10) Get audit trail for a transaction
```bash
curl -v http://localhost:8080/api/audit/123
```
Expected: 200 OK, JSON array of AuditLogResponse entries (id, action, userId, details, timestamp)

## Troubleshooting tips
- If you see: "Failed to configure a DataSource: 'url' attribute is not specified" — ensure the app has correct DB properties and the right Spring profile is active or the DB is running.
- If Docker commands fail with permission denied on /var/run/docker.sock, run the Docker Compose command with `sudo` or add your user to the `docker` group (recommended to adjust system permissions).
- If Flyway reports "Unsupported Database: PostgreSQL 15.15", ensure you use a compatible Flyway/Postgres combination and that the Flyway configuration in `application.yaml` is correct.

