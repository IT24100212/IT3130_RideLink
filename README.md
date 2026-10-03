# IT3130 - RideLink Microservices Project

Welcome to the **RideLink** project repository! This project implements a modern, scalable backend for a ride-sharing platform using a microservices architecture. It satisfies all requirements for the IT3130 course, including distributed security, inter-service communication, and MongoDB integration.

## Microservices Architecture

The system consists of 4 loosely coupled Spring Boot microservices:
1. **Account Service (Port 8081):** Handles User/Passenger/Driver registration, authentication, and JWT token issuance.
2. **Driver & Vehicle Service (Port 8082):** Manages driver statuses, vehicle profiles, and driver eligibility based on location.
3. **Ride Management Service (Port 8083):** The core orchestrator. Manages ride requests, assigns drivers, and updates ride statuses.
4. **Fare & Payment Service (Port 8084):** Calculates ride fare estimates based on distance and duration, and simulates payment processing.

## Tech Stack
* **Java 17** & **Spring Boot 3**
* **MongoDB** (Spring Data MongoDB)
* **Spring Security & JJWT** for stateless authentication
* **JUnit 5 & Mockito** for unit testing
* **Swagger/OpenAPI** for API documentation

---

## Getting Started

### 1. Prerequisites
- **Java 17** installed
- **Maven** (Included via `./mvnw` wrapper)
- **MongoDB** running locally on default port `27017`

### 2. Database Setup
Ensure your local MongoDB instance is running. The microservices are configured to auto-create their respective databases:
- `ridelink_account_db`
- `ridelink_driver_db`
- `ridelink_ride_db`
- `ridelink_fare_db`

### 3. Running the Microservices
You can run each microservice independently. Open a terminal for each service and run the following commands:

**Account Service:**
```bash
cd account-service
./mvnw spring-boot:run
```

**Driver & Vehicle Service:**
```bash
cd driver-vehicle-service
./mvnw spring-boot:run
```

**Ride Management Service:**
```bash
cd ride-management-service
./mvnw spring-boot:run
```

**Fare & Payment Service:**
```bash
cd fare-payment-service
./mvnw spring-boot:run
```

### 4. Running the Test Suite
Meaningful unit tests testing the core business logic (Services) are implemented with Mockito. To run the tests for all microservices:
```bash
cd account-service && ./mvnw test
cd ../driver-vehicle-service && ./mvnw test
cd ../ride-management-service && ./mvnw test
cd ../fare-payment-service && ./mvnw test
```

---

## API Documentation (Swagger)
Once the services are running, you can explore their endpoints via Swagger UI in your browser:
* **Account Service:** http://localhost:8081/swagger-ui.html
* **Driver Service:** http://localhost:8082/swagger-ui.html
* **Ride Service:** http://localhost:8083/swagger-ui.html
* **Fare Service:** http://localhost:8084/swagger-ui.html

## Security (JWT)
All endpoints (except registration and login) are secured. To authenticate:
1. Register/Login via the **Account Service** to receive a JWT Token.
2. For all subsequent requests to *any* service, include the token in the HTTP Header:
   `Authorization: Bearer <your-jwt-token>`
3. Inter-service calls (like Ride Service calling Fare Service) will automatically intercept and propagate this token.
