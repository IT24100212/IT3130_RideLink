package com.ridelink.ride.model;

public enum RideStatus {
    REQUESTED,   // Passenger created the request
    ASSIGNED,    // System assigned an eligible driver
    ACCEPTED,    // Driver accepted the ride
    IN_PROGRESS, // Ride is currently happening
    COMPLETED,   // Ride finished, awaiting payment
    CANCELLED    // Ride aborted by either party
}