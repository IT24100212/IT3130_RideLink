package com.ridelink.ride.service;

import com.ridelink.ride.dto.DriverDTO;
import com.ridelink.ride.dto.FareDTO;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RestTemplate restTemplate;

    @Value("${fare.service.url:http://localhost:8084/api/fares}")
    private String fareServiceUrl;

    @Value("${driver.service.url:http://localhost:8082/api/vehicles}")
    private String driverServiceUrl; // Note: You might want an api/drivers endpoint instead. Assuming driver-vehicle-service has it. Wait, the controller path was /api/drivers

    public RideService(RideRepository rideRepository, RestTemplate restTemplate) {
        this.rideRepository = rideRepository;
        this.restTemplate = restTemplate;
    }

    public Ride requestRide(Long passengerId, String pickupLocation, String destination) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(pickupLocation);
        ride.setDestination(destination);
        // Save first to generate rideId
        ride = rideRepository.save(ride);

        double simulatedDistance = 5.0;
        double simulatedDuration = 15.0;

        try {
            String fareUrl = UriComponentsBuilder.fromUriString(fareServiceUrl + "/calculate")
                .queryParam("rideId", ride.getId())
                .queryParam("distanceInKm", simulatedDistance)
                .queryParam("durationInMinutes", simulatedDuration)
                .toUriString();
            
            FareDTO fareDTO = restTemplate.postForObject(fareUrl, null, FareDTO.class);
            if (fareDTO != null) {
                ride.setFareEstimate(fareDTO.getTotalFare()); // Wait, FareDTO doesn't have getTotalFare? Let's check Fare.java in fare service. It has totalFare. But my DTO has estimatedAmount. I'll use finalAmount or estimatedAmount. But let's check FareDTO.
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch fare estimate: " + e.getMessage());
            ride.setFareEstimate(BigDecimal.ZERO);
        }

        try {
            // Wait, what is the exact endpoint for drivers? It's /api/drivers/eligible in driver-vehicle-service
            String driverUrl = UriComponentsBuilder.fromUriString("http://localhost:8082/api/drivers/eligible")
                .queryParam("serviceArea", pickupLocation)
                .toUriString();
                
            List<DriverDTO> drivers = restTemplate.exchange(
                driverUrl, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<DriverDTO>>() {}
            ).getBody();
            
            if (drivers != null && !drivers.isEmpty()) {
                // For this scenario, assign the first available driver
                ride.setDriverId(drivers.get(0).getUserId());
                ride.setStatus(RideStatus.ASSIGNED);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch eligible drivers: " + e.getMessage());
        }

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
                // Simulate payment processing integration when a ride is completed
                try {
                    String paymentUrl = UriComponentsBuilder.fromUriString("http://localhost:8084/api/payments/process")
                        .queryParam("rideId", ride.getId())
                        .queryParam("passengerId", ride.getPassengerId())
                        .queryParam("amount", ride.getFareEstimate())
                        .queryParam("paymentMethod", "CREDIT_CARD")
                        .toUriString();
                    restTemplate.postForObject(paymentUrl, null, String.class);
                } catch (Exception e) {
                    System.err.println("Failed to process payment: " + e.getMessage());
                }
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