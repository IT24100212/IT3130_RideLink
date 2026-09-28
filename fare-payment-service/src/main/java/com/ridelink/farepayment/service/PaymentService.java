package com.ridelink.farepayment.service;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment processPayment(Long rideId, Long passengerId, BigDecimal amount, String paymentMethod) {
        Payment payment = Payment.builder()
                .rideId(rideId)
                .passengerId(passengerId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status("COMPLETED")
                .build();

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByPassenger(Long passengerId) {
        return paymentRepository.findByPassengerId(passengerId);
    }

    public List<Payment> getPaymentsByRide(Long rideId) {
        return paymentRepository.findByRideId(rideId);
    }
}