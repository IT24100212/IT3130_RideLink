# RideLink: Microservices Architecture Technical Report

**Course:** IT3130  
**Project:** RideLink Backend Development  
**Group:** [Your Group Name/ID]  

---

## 1. Introduction
*Describe the purpose of the RideLink platform. Mention that it was transitioned from a monolithic structure to a microservices architecture to improve scalability, fault tolerance, and independent deployability.*

## 2. Microservices Architecture Design
*(Expand this section with details on why these specific boundaries were chosen).*
The architecture is decomposed into four independent Spring Boot microservices:
1. **Account Service (Port 8081):** Manages user registration, profiles, and Authentication.
2. **Driver & Vehicle Service (Port 8082):** Handles driver statuses, live locations, and vehicle data.
3. **Ride Management Service (Port 8083):** The central orchestrator that manages ride lifecycles and assigns drivers.
4. **Fare & Payment Service (Port 8084):** Computes ride costs based on distance/time and simulates payment transactions.

## 3. Database Strategy (MongoDB)
*Explain why MongoDB (NoSQL) was chosen over a relational database (e.g., flexibility with JSON documents, high write-throughput for driver locations).*
- **Decentralized Data:** Each microservice connects to its own independent MongoDB database (`ridelink_account_db`, `ridelink_driver_db`, etc.).
- **Data Isolation:** Enforces the microservices pattern where services do not share a database, but rather communicate via APIs.

## 4. Inter-Service Communication
*Discuss how the microservices talk to each other.*
- **Synchronous Communication:** Spring's `RestTemplate` is utilized for synchronous REST API calls. 
- **Workflow Example:** When a passenger requests a ride, the **Ride Service** makes an HTTP POST request to the **Fare Service** for a price estimate, and an HTTP GET request to the **Driver Service** to find available drivers in the pickup area.

## 5. Security Implementation (JWT)
*Explain the security model in detail (Section 6.2 of the brief).*
- **Stateless Authentication:** JSON Web Tokens (JWT) are used instead of session cookies.
- **Token Generation:** The Account Service validates credentials and issues a signed JWT containing the user's ID and Role.
- **Distributed Validation:** All other services possess the shared `jwt.secret` and use a custom `JwtAuthenticationFilter` to cryptographically verify incoming requests without needing to query the Account Service database.
- **Token Propagation:** A `ClientHttpRequestInterceptor` is configured on the `RestTemplate` to forward the user's `Authorization: Bearer <token>` header during inter-service API calls.

## 6. Testing Strategy
*Detail the unit testing approach (Section 6.3 of the brief).*
- **Frameworks Used:** JUnit 5 and Mockito.
- **Scope:** Unit tests isolate the core business logic within the `@Service` layer.
- **Mocking:** `Mockito` is used to mock repository interfaces (e.g., `RideRepository`, `FareRepository`), allowing tests to run incredibly fast without requiring a live MongoDB connection.

## 7. API Documentation
*Mention how the APIs are documented.*
- **Swagger/OpenAPI:** Integrated via `springdoc-openapi-starter-webmvc-ui`. It automatically generates interactive UI documentation at `/swagger-ui.html` for every microservice, allowing frontend teams to easily explore and test endpoints.

## 8. Conclusion
*Summarize the success of the implementation, mentioning that all core workflows (registration, ride requesting, driver assignment, fare calculation, and payment) function securely across the distributed architecture.*
