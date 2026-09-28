package com.ridelink.ride.service;

import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.stereotype.Service;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public Ride requestRide(Long passengerId, String pickupLocation, String destination) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(pickupLocation);
        ride.setDestination(destination);
        // Status defaults to REQUESTED via the @PrePersist method in your Ride entity
        return rideRepository.save(ride);
    }

    public Ride assignDriver(String rideId, Long driverId) {
        Ride ride = getRideById(rideId);
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalStateException("Ride must be in REQUESTED state to assign a driver.");
        }
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);
        return rideRepository.save(ride);
    }

    public Ride updateRideStatus(String rideId, RideStatus newStatus) {
        Ride ride = getRideById(rideId);

        switch (newStatus) {
            case ACCEPTED:
                if (ride.getStatus() != RideStatus.ASSIGNED) throw new IllegalStateException("Ride must be ASSIGNED to be ACCEPTED.");
                break;
            case IN_PROGRESS:
                if (ride.getStatus() != RideStatus.ACCEPTED) throw new IllegalStateException("Ride must be ACCEPTED to be IN_PROGRESS.");
                break;
            case COMPLETED:
                if (ride.getStatus() != RideStatus.IN_PROGRESS) throw new IllegalStateException("Ride must be IN_PROGRESS to be COMPLETED.");
                break;
            case CANCELLED:
                if (ride.getStatus() == RideStatus.COMPLETED) throw new IllegalStateException("Cannot cancel a COMPLETED ride.");
                break;
            default:
                throw new IllegalArgumentException("Invalid status transition.");
        }

        ride.setStatus(newStatus);
        return rideRepository.save(ride);
    }

    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));
    }
}