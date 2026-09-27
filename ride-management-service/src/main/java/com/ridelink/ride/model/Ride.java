package com.ridelink.ride.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Notice these are just Longs, NOT @ManyToOne, to keep databases isolated
    @Column(nullable = false)
    private Long passengerId;

    @Column(nullable = true) // Nullable because it's empty when first requested
    private Long driverId;

    @Column(nullable = false)
    private String pickupLocation; // Can be a simulated coordinate or place name

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    private BigDecimal fareEstimate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = RideStatus.REQUESTED; // Default state when created
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Add standard Getters and Setters here
}
