package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Explicit constructor replaces Lombok
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(
            @RequestParam String rideId,
            @RequestParam Long passengerId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {
        Payment payment = paymentService.processPayment(rideId, passengerId, amount, paymentMethod);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Payment>> getPaymentsByPassenger(@PathVariable Long passengerId) {
        List<Payment> payments = paymentService.getPaymentsByPassenger(passengerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<Payment>> getPaymentsByRide(@PathVariable String rideId) {
        List<Payment> payments = paymentService.getPaymentsByRide(rideId);
        return ResponseEntity.ok(payments);
    }
}