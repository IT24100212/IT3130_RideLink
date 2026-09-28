package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPassengerId(Long passengerId);
    List<Payment> findByRideId(Long rideId);
}