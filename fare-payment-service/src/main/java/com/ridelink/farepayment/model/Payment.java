package com.ridelink.farepayment.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rideId;

    @Column(nullable = false)
    private Long passengerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMethod; // e.g., CASH, CARD, WALLET

    @Column(nullable = false)
    private String status; // e.g., PENDING, COMPLETED, FAILED

    private LocalDateTime paymentTime;

    @PrePersist
    protected void onCreate() {
        this.paymentTime = LocalDateTime.now();
    }
}