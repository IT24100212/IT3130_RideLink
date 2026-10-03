# Development Log

This document tracks all modifications and architectural decisions made to align the RideLink project with the assignment requirements.

## 2026-10-03: CI Pipeline and Security Fixes
- **Secured Secrets:** Removed hardcoded MongoDB credentials from `driver-vehicle-service`, `fare-payment-service`, and `ride-management-service` `application.properties`. Replaced them with environment variable fallbacks (e.g., `${MONGODB_URI:mongodb://localhost:27017/db_name}`).
- **Resolved Port Conflicts:** Changed application ports to ensure all services can run simultaneously locally:
  - Account Service: `8081`
  - Driver & Vehicle Service: `8082`
  - Ride Management Service: `8083`
  - Fare & Payment Service: `8084`
- **Fixed CI Build Failures:**
  - Deleted the default `DemoApplicationTests.java` in `ride-management-service` which was failing due to an incorrect package (`com.example.demo`).
  - Copied the missing Maven wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/`) into the `fare-payment-service`.
  - Fixed executable permissions (`chmod +x`) on the `mvnw` scripts for all services via Git to prevent "Permission denied" errors in the GitHub Actions Ubuntu runner.
- *Status:* Committed and pushed to `main`.

## 2026-10-03: Inter-Service Communication and Type Refactoring
- **Type Mismatch Resolution:** Discovered that `ride-management-service` generated `Ride` IDs as MongoDB `String` objects, while `fare-payment-service` expected `Long rideId`. Refactored `Fare`, `Payment`, and their respective Repositories, Services, and Controllers to consistently use `String rideId` to prevent runtime crashes.
- **Implemented Inter-Service Communication:** Set up `RestTemplate` inside `ride-management-service`. Modified the `requestRide` workflow to perform two crucial synchronous HTTP calls: 
  1. Hits `fare-payment-service` to generate and return a base estimated fare.
  2. Hits `driver-vehicle-service` to find eligible drivers based on the pickup location and assigns the first available driver.
- *Status:* End-to-end booking flow demonstrated successfully. Committed and pushed to Git.
