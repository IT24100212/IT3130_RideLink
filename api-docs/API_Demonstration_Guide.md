# RideLink Microservices - API Demonstration Guide

This guide provides step-by-step instructions for demonstrating and testing the complete **RideLink System** using **Swagger UI** and **Postman**.

---

## 🚀 Microservice Ports & Swagger UI URLs

| Service Name | Port | Base URL | Swagger UI URL | OpenAPI Specification |
| :--- | :---: | :--- | :--- | :--- |
| **Driver & Vehicle Service** | `8080` | `http://localhost:8080` | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | `http://localhost:8080/v3/api-docs` |
| **Ride Management Service** | `8081` | `http://localhost:8081` | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | `http://localhost:8081/v3/api-docs` |
| **Fare & Payment Service** | `8082` | `http://localhost:8082` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | `http://localhost:8082/v3/api-docs` |

---

## 🖥️ 1. Demonstrating via Swagger UI

1. Start all 3 Spring Boot microservices.
2. Open your web browser and navigate to any of the Swagger UI URLs:
   - **Driver & Vehicle Service:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **Ride Management Service:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
   - **Fare & Payment Service:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
3. Expand any API operation (e.g. `POST /api/fares/calculate`), click **Try it out**, fill in the parameters, and click **Execute**.

---

## 📬 2. Demonstrating via Postman

### Importing the Collection
1. Open **Postman**.
2. Click **Import** in the top left.
3. Select the [`RideLink_Postman_Collection.json`](file:///c:/Users/user/IT3130_RideLink/api-docs/RideLink_Postman_Collection.json) file located inside `api-docs/`.
4. Click **Import**.

---

## 🛠️ Step-by-Step API Execution Order

### Step 1: Driver & Vehicle Management (`http://localhost:8080`)
- **Register Driver**: `POST /api/drivers`
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+94771234567",
    "serviceArea": "Colombo",
    "active": true
  }
  ```
- **Register Vehicle**: `POST /api/vehicles`
  ```json
  {
    "make": "Toyota",
    "model": "Prius",
    "licensePlate": "CAB-1234",
    "type": "CAR"
  }
  ```
- **Fetch Drivers**: `GET /api/drivers`

---

### Step 2: Ride Request & Assignment (`http://localhost:8081`)
- **Request a Ride**: `POST /api/rides?passengerId=101&pickupLocation=Colombo&destination=Kandy`
- **Assign Driver**: `PUT /api/rides/{rideId}/assign?driverId=201`
- **Update Ride Status**: `PUT /api/rides/{rideId}/status?status=ACCEPTED`
- **Get Ride Details**: `GET /api/rides/{rideId}`

---

### Step 3: Fare Calculation & Payment (`http://localhost:8082`)
- **Calculate Fare**: `POST /api/fares/calculate?rideId=1&distanceInKm=12.5&durationInMinutes=25`
- **Get Fare**: `GET /api/fares/ride/1`
- **Process Payment**: `POST /api/payments/process?rideId=1&passengerId=101&amount=1175.00&paymentMethod=CARD`
- **Fetch Payments by Passenger**: `GET /api/payments/passenger/101`
- **Fetch Payments by Ride**: `GET /api/payments/ride/1`
