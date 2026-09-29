# RideLink — Account Service (`account-service`)

The **Account Service** is an independent, backend-only microservice developed for the **RideLink** application (IT3130 Application Development Group Assignment). It is solely responsible for user management, identity verification, role-based authorization, profile updates, and JWT issuance across the RideLink platform.

---

## Table of Contents
1. [Project Purpose & Responsibilities](#project-purpose--responsibilities)
2. [Technology Stack](#technology-stack)
3. [Prerequisites](#prerequisites)
4. [Architecture & Design](#architecture--design)
5. [Database Configuration (MongoDB)](#database-configuration-mongodb)
6. [Environment Variables & Configuration](#environment-variables--configuration)
7. [How to Run the Service](#how-to-run-the-service)
8. [How to Run Tests](#how-to-run-tests)
9. [Swagger UI & API Documentation](#swagger-ui--api-documentation)
10. [REST API Endpoints Specification](#rest-api-endpoints-specification)
11. [Sample Test Accounts & Data](#sample-test-accounts--data)
12. [Git Branch & Development Notes](#git-branch--development-notes)

---

## Project Purpose & Responsibilities

The Account Service is an autonomous microservice that manages the lifecycle of users in the RideLink ecosystem.

### Core Business Responsibilities:
1. **Passenger Registration**: Self-service registration for passengers with input validation and encrypted credential storage.
2. **Driver Registration**: Driver account creation with validated profile data.
3. **Authentication & Login**: Verifies credentials and checks account statuses (active, suspended, inactive).
4. **JWT Issuance & Verification**: Issues signed HMAC-SHA256 JWT tokens with custom claims (`userId`, `email`, `role`, `status`).
5. **Profile Management**: Viewing (`GET /me`) and updating (`PUT /me`) authenticated user profile details.
6. **Account Administration**: Administrator endpoints to fetch accounts by ID (`GET /{id}`) and modify account statuses (`PATCH /{id}/status`) and roles (`PATCH /{id}/role`).
7. **Role-Based Access Control (RBAC)**: Enforces role boundaries (`PASSENGER`, `DRIVER`, `ADMIN`) via Spring Security.
8. **Consistent Error Handling**: Centralized `@RestControllerAdvice` error handler returning predictable JSON error payloads.

---

## Technology Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java | 17 (or 21+) |
| **Framework** | Spring Boot | 3.3.4 |
| **Web Layer** | Spring Web (MVC) | 6.1.13 |
| **Data Layer** | Spring Data MongoDB | 4.3.4 |
| **Database** | MongoDB | 6.0+ / Atlas |
| **Security** | Spring Security | 6.3.3 |
| **Token Handling** | JJWT (Java JWT) | 0.12.6 |
| **Validation** | Jakarta Bean Validation (Hibernate Validator) | 3.0+ |
| **API Documentation** | Springdoc OpenAPI (Swagger UI) | 2.6.0 |
| **Testing** | JUnit 5, Mockito, AssertJ, Spring Security Test | Latest |
| **Build Tool** | Apache Maven (Maven Wrapper included) | 3.9+ |

---

## Prerequisites

- **Java Development Kit (JDK)**: Version 17, 21, or 24 installed.
- **MongoDB**: Local MongoDB instance running on port `27017` or a MongoDB Atlas connection URI.
- **Maven**: Bundled with Maven Wrapper (`./mvnw` or `mvnw.cmd`).

---

## Architecture & Design

The service follows **Clean Layered Architecture** adhering to **SOLID** principles:

```
com.ridelink.accountservice
├── config/             # Spring Security, OpenAPI, Mongo Auditing configurations
├── controller/         # REST Controllers (AuthController, AccountController)
├── dto/                # Request & Response Data Transfer Objects with Bean Validation
├── exception/          # Custom domain exceptions & GlobalExceptionHandler (@RestControllerAdvice)
├── model/              # MongoDB Documents (User) and Enums (Role, AccountStatus)
├── repository/         # Spring Data MongoDB Repository (UserRepository)
├── security/           # JWT Provider, Auth Filter, EntryPoint, AccessDeniedHandler, UserPrincipal
└── service/            # Business logic interfaces and service implementations
    └── impl/
```

- **Separation of Concerns**: Controllers only handle HTTP concerns and mapping; all business rules and password encoding reside in the service layer.
- **Data Encapsulation**: DTOs prevent direct exposure of MongoDB documents.
- **Stateless Authentication**: Uses stateless JWT authentication without HTTP sessions.

---

## Database Configuration (MongoDB)

In accordance with microservice design best practices:
- **Dedicated Data Store**: The Account Service owns its own database called `ridelink_account_db`.
- **No Shared Databases**: This service never queries or joins data from other RideLink microservices.
- **Unique Indexes**: The `email` field is indexed with `unique = true`.

### Connection Configuration (`application.properties`):
```properties
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/ridelink_account_db}
spring.data.mongodb.database=${MONGODB_DATABASE:ridelink_account_db}
spring.data.mongodb.auto-index-creation=true
```

---

## Environment Variables & Configuration

No secrets or credentials are hard-coded in the source code. Environment variables can override default properties:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `PORT` | HTTP server port | `8081` |
| `MONGODB_URI` | MongoDB connection URI | `mongodb://localhost:27017/ridelink_account_db` |
| `MONGODB_DATABASE` | Database name | `ridelink_account_db` |
| `JWT_SECRET` | HMAC-SHA256 Secret (min 256 bits) | *Default 256-bit dev key* |
| `JWT_EXPIRATION_MS` | JWT expiration time in milliseconds | `86400000` (24 hours) |

---

## How to Run the Service

Navigate to the `account-service` directory:

### On Windows:
```powershell
cd account-service
.\mvnw.cmd spring-boot:run
```

### On Linux / macOS:
```bash
cd account-service
./mvnw spring-boot:run
```

Once started, the service will be available on:
```
http://localhost:8081
```

---

## How to Run Tests

The test suite covers unit tests, service tests, JWT validation tests, and Spring Security / MockMvc integration tests without requiring an active MongoDB connection.

```powershell
# From the account-service directory:
.\mvnw.cmd clean test
```

### Test Coverage Summary:
1. **Passenger registration success** (`AuthServiceTest`, `AuthControllerTest`)
2. **Driver registration success** (`AuthServiceTest`, `AuthControllerTest`)
3. **Duplicate email rejection** (`AuthServiceTest`)
4. **Login success with valid credentials** (`AuthServiceTest`, `AuthControllerTest`)
5. **Login rejection with invalid password** (`AuthServiceTest`)
6. **Get profile with authenticated user** (`AccountServiceTest`, `AccountControllerSecurityTest`)
7. **Update profile** (`AccountServiceTest`, `AccountControllerSecurityTest`)
8. **Account not found error handling** (`AccountServiceTest`)
9. **Unauthorized request rejection (HTTP 401)** (`AccountControllerSecurityTest`)
10. **Role-based authorization enforcement (HTTP 403)** (`AccountControllerSecurityTest`)
11. **Validation failure handling (HTTP 400)** (`AuthControllerTest`)
12. **Account status update** (`AccountServiceTest`, `AccountControllerSecurityTest`)

---

## Swagger UI & API Documentation

Springdoc OpenAPI is fully integrated. Interactive API testing is accessible via web browser:

- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

### Testing with JWT in Swagger UI:
1. Open [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html).
2. Call `POST /api/auth/login` or `POST /api/auth/register/passenger` with valid credentials.
3. Copy the returned `token` value from the response body.
4. Click the **Authorize** button at the top right of the Swagger UI page.
5. Enter the token into the **Value** box (the "Bearer " prefix is automatically added).
6. Click **Authorize** then **Close**.
7. Now all protected endpoints (e.g. `/api/accounts/me`) can be executed directly within Swagger UI!

---

## REST API Endpoints Specification

### 1. Authentication Endpoints

#### Register Passenger
- **Method & Path**: `POST /api/auth/register/passenger`
- **Auth**: Public (None)
- **Status**: `201 Created`
- **Request Body**:
  ```json
  {
    "firstName": "Kasun",
    "lastName": "Perera",
    "email": "kasun.perera@example.com",
    "phoneNumber": "+94771234567",
    "password": "Password@123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": "66f91f7d8a7c2b3e4f1a2b3c",
    "firstName": "Kasun",
    "lastName": "Perera",
    "email": "kasun.perera@example.com",
    "role": "PASSENGER",
    "status": "ACTIVE"
  }
  ```

#### Register Driver
- **Method & Path**: `POST /api/auth/register/driver`
- **Auth**: Public (None)
- **Status**: `201 Created`
- **Request Body**:
  ```json
  {
    "firstName": "Nimal",
    "lastName": "Fernando",
    "email": "nimal.fernando@example.com",
    "phoneNumber": "+94779876543",
    "password": "DriverPass@123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": "66f91f7d8a7c2b3e4f1a2b3d",
    "firstName": "Nimal",
    "lastName": "Fernando",
    "email": "nimal.fernando@example.com",
    "role": "DRIVER",
    "status": "ACTIVE"
  }
  ```

#### Login
- **Method & Path**: `POST /api/auth/login`
- **Auth**: Public (None)
- **Status**: `200 OK`
- **Request Body**:
  ```json
  {
    "email": "kasun.perera@example.com",
    "password": "Password@123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": "66f91f7d8a7c2b3e4f1a2b3c",
    "firstName": "Kasun",
    "lastName": "Perera",
    "email": "kasun.perera@example.com",
    "role": "PASSENGER",
    "status": "ACTIVE"
  }
  ```

---

### 2. Account & Profile Endpoints

#### Get Current Profile
- **Method & Path**: `GET /api/accounts/me`
- **Auth**: `Bearer <JWT>`
- **Status**: `200 OK`
- **Response**:
  ```json
  {
    "id": "66f91f7d8a7c2b3e4f1a2b3c",
    "firstName": "Kasun",
    "lastName": "Perera",
    "email": "kasun.perera@example.com",
    "phoneNumber": "+94771234567",
    "role": "PASSENGER",
    "status": "ACTIVE",
    "createdAt": "2026-09-29T10:00:00Z",
    "updatedAt": "2026-09-29T10:00:00Z"
  }
  ```

#### Update Current Profile
- **Method & Path**: `PUT /api/accounts/me`
- **Auth**: `Bearer <JWT>`
- **Status**: `200 OK`
- **Request Body**:
  ```json
  {
    "firstName": "Kasun",
    "lastName": "Silva",
    "phoneNumber": "+94770001122"
  }
  ```

#### Get Account by ID
- **Method & Path**: `GET /api/accounts/{id}`
- **Auth**: `Bearer <JWT>` (Admin or Self)
- **Status**: `200 OK`

#### Update Account Status (Admin Only)
- **Method & Path**: `PATCH /api/accounts/{id}/status`
- **Auth**: `Bearer <JWT>` (Requires `ROLE_ADMIN`)
- **Status**: `200 OK`
- **Request Body**:
  ```json
  {
    "status": "SUSPENDED"
  }
  ```

#### Update Account Role (Admin Only)
- **Method & Path**: `PATCH /api/accounts/{id}/role`
- **Auth**: `Bearer <JWT>` (Requires `ROLE_ADMIN`)
- **Status**: `200 OK`
- **Request Body**:
  ```json
  {
    "role": "DRIVER"
  }
  ```

---

### 3. Error Response Format

All error responses adhere to a consistent standard:

```json
{
  "timestamp": "2026-09-29T11:45:00.123Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Validation failed for one or more fields",
  "path": "/api/auth/register/passenger",
  "errors": {
    "email": "Email must be a valid email address",
    "password": "Password must be at least 8 characters long"
  }
}
```

---

## Sample Test Accounts & Data

When testing via Postman or Swagger UI:

| User Type | Email | Password | Role | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Passenger** | `passenger@ridelink.com` | `Pass@12345` | `PASSENGER` | `ACTIVE` |
| **Driver** | `driver@ridelink.com` | `Driver@12345` | `DRIVER` | `ACTIVE` |
| **Admin** | `admin@ridelink.com` | `Admin@12345` | `ADMIN` | `ACTIVE` |

---

## Git Branch & Development Notes

- **Branch Name**: `feature/IT23667150-account-service`
- **Microservice Directory**: `account-service/`
- **Commit History**: Structured into logical, granular commits matching each architectural layer:
  - Configuration & Maven setup
  - Domain models & Repository
  - DTOs & Validation
  - Spring Security & JWT
  - Exceptions & GlobalExceptionHandler
  - Services implementation
  - REST Controllers & OpenAPI documentation
  - Unit, Service, Security, and WebMvc tests
  - Documentation & README
