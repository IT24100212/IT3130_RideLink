package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByPassengerId(Long passengerId);
    List<Payment> findByRideId(String rideId);
}