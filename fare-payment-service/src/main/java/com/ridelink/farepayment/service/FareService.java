package com.ridelink.farepayment.service;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FareService {

    private final FareRepository fareRepository;

    private static final BigDecimal BASE_RATE = new BigDecimal("50.00");
    private static final BigDecimal PER_KM_RATE = new BigDecimal("80.00");
    private static final BigDecimal PER_MINUTE_RATE = new BigDecimal("5.00");

    public Fare calculateAndSaveFare(Long rideId, double distanceInKm, double durationInMinutes) {
        BigDecimal baseFare = BASE_RATE;
        BigDecimal distanceFare = PER_KM_RATE.multiply(BigDecimal.valueOf(distanceInKm));
        BigDecimal timeFare = PER_MINUTE_RATE.multiply(BigDecimal.valueOf(durationInMinutes));
        BigDecimal totalFare = baseFare.add(distanceFare).add(timeFare);

        Fare fare = Fare.builder()
                .rideId(rideId)
                .baseFare(baseFare)
                .distanceFare(distanceFare)
                .timeFare(timeFare)
                .totalFare(totalFare)
                .currency("LKR")
                .build();

        return fareRepository.save(fare);
    }

    public Fare getFareByRideId(Long rideId) {
        return fareRepository.findByRideId(rideId)
                .orElseThrow(() -> new RuntimeException("Fare not found for ride ID: " + rideId));
    }
}