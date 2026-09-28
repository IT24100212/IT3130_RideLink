package com.ridelink.ride.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    // Notice these are just Longs, NOT @ManyToOne, to keep databases isolated
    private Long passengerId;

    private Long driverId;

    private String pickupLocation; // Can be a simulated coordinate or place name

    private String destination;

    private RideStatus status;

    private BigDecimal fareEstimate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ride() {
        this.createdAt = LocalDateTime.now();
        this.status = RideStatus.REQUESTED; // Default state when created
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public BigDecimal getFareEstimate() {
        return fareEstimate;
    }

    public void setFareEstimate(BigDecimal fareEstimate) {
        this.fareEstimate = fareEstimate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
