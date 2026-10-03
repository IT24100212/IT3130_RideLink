package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.service.FareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareService fareService;

    // Explicit constructor replaces Lombok
    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<Fare> calculateFare(
            @RequestParam String rideId,
            @RequestParam double distanceInKm,
            @RequestParam double durationInMinutes) {
        Fare fare = fareService.calculateAndSaveFare(rideId, distanceInKm, durationInMinutes);
        return ResponseEntity.ok(fare);
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<Fare> getFareByRideId(@PathVariable String rideId) {
        Fare fare = fareService.getFareByRideId(rideId);
        return ResponseEntity.ok(fare);
    }
}