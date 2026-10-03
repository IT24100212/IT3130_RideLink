package com.ridelink.farepayment.service;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FareService {

    private static final BigDecimal BASE_RATE = new BigDecimal("50.00");
    private static final BigDecimal PER_KM_RATE = new BigDecimal("80.00");
    private static final BigDecimal PER_MINUTE_RATE = new BigDecimal("5.00");

    private final FareRepository fareRepository;

    public FareService(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    public Fare calculateAndSaveFare(String rideId, double distanceInKm, double durationInMinutes) {
        BigDecimal baseFare = BASE_RATE;
        BigDecimal distanceFare = PER_KM_RATE.multiply(BigDecimal.valueOf(distanceInKm));
        BigDecimal timeFare = PER_MINUTE_RATE.multiply(BigDecimal.valueOf(durationInMinutes));
        BigDecimal totalFare = baseFare.add(distanceFare).add(timeFare);

        Fare fare = new Fare(rideId, baseFare, distanceFare, timeFare, totalFare, "LKR");

        return fareRepository.save(fare);
    }

    public Fare getFareByRideId(String rideId) {
        return fareRepository.findByRideId(rideId)
                .orElseThrow(() -> new RuntimeException("Fare not found for ride ID: " + rideId));
    }
}