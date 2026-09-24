# ttrpg-vault
Backend for a self-hosted TTRPG manager. Java, Spring-Boot

---

## Tech Stack

* **Language:** Java 25
* **Framework:** Spring Boot 3, Spring Security 6, Spring Data JPA
* **Database:** PostgreSQL 16
* **JWT Engine:** `io.jsonwebtoken:jjwt` (v0.12.x)
* **Build Tool:** Maven

---

## Project Structure

```Plaintext
com.ttrp.manager

├── config/             # Spring Security, App Properties & Beans

├── controller/         # REST API Controllers

├── dto/                # Request / Response DTOs & Records

├── entity/             # JPA Entities (User, RefreshToken, etc.)

├── helper/             # Custom Annotations (@CurrentUser) & JWT Filters

├── mapper/             # Entity <-> DTO / Record Mappers

├── repository/         # Spring Data JPA Repositories

└── service/            # Business Logic & Service Implementations
```

---

## Prerequisites

Ensure you have the following installed locally:

* [JDK 25](https://adoptium.net/)
* [Docker & Docker Compose](https://www.docker.com/)
* [Maven](https://maven.apache.org/) (or use the included `./mvnw` wrapper)

---

## Quick Start

### 1. Start the PostgreSQL Container

Run the local PostgreSQL container configured with default development credentials:

```bash
docker run -d \
  --name postgres-dev \
  -p 5432:5432 \
  -e POSTGRES_DB=dev_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:16-alpine
```

### 2. Environment Configuration
Copy the .example.env and rename it to .env or create a .env file in the root directory (or rely on the defaults mapped in application.yml):

Properties

```bash
DB_URL=jdbc:postgresql://localhost:5432/dev_db
DB_USER=postgres
DB_PWD=postgres

JWT_SECRET=-generate-256-bit-secret-
JWT_ACCESS_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_MS=90000000

CORS_ALLOWED_ORIGIN=http://localhost:3000,http://localhost:5173
```

**Note**: JWT_SECRET must be a valid 256-bit secret string. Generate it in your console with:
```bash
openssl rand -base64 32
```


### 3. Build & Run
```Bash
./mvnw clean spring-boot:run
```
The application starts by default on **http://localhost:8080**.

---

## API Modules
ModuleBase | Path | Description
--- | --- | ---
Authentication | /api/auth | User registration, login, and token rotation

---

## Contributing and Branching Rules
Please refer to [CONTRIBUTING.md](CONTRIBUTING.md) for branch naming conventions, commit message formats, and the pull request workflow.



