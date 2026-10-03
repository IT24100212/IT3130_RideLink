package com.ridelink.farepayment.service;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(String rideId, Long passengerId, BigDecimal amount, String paymentMethod) {
        Payment payment = new Payment(rideId, passengerId, amount, paymentMethod, "COMPLETED");
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByPassenger(Long passengerId) {
        return paymentRepository.findByPassengerId(passengerId);
    }

    public List<Payment> getPaymentsByRide(String rideId) {
        return paymentRepository.findByRideId(rideId);
    }
}