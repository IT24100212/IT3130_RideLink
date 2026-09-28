package com.ridelink.ride.controller;

import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    // 1. Passenger requests a new ride
    @PostMapping
    public ResponseEntity<Ride> requestRide(@RequestParam Long passengerId,
                                            @RequestParam String pickupLocation,
                                            @RequestParam String destination) {
        return ResponseEntity.ok(rideService.requestRide(passengerId, pickupLocation, destination));
    }

    // 2. System assigns a driver to the requested ride
    @PutMapping("/{rideId}/assign")
    public ResponseEntity<Ride> assignDriver(@PathVariable String rideId,
                                             @RequestParam Long driverId) {
        return ResponseEntity.ok(rideService.assignDriver(rideId, driverId));
    }

    // 3. Update the ride status (e.g., ACCEPTED, IN_PROGRESS, COMPLETED)
    @PutMapping("/{rideId}/status")
    public ResponseEntity<Ride> updateStatus(@PathVariable String rideId,
                                             @RequestParam RideStatus status) {
        return ResponseEntity.ok(rideService.updateRideStatus(rideId, status));
    }

    // 4. Fetch ride details
    @GetMapping("/{rideId}")
    public ResponseEntity<Ride> getRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }
}