package com.ridelink.farepayment.service;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FareServiceTest {

    @Mock
    private FareRepository fareRepository;

    @InjectMocks
    private FareService fareService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateAndSaveFare_ShouldCalculateCorrectly() {
        Fare mockFare = new Fare("ride123", BigDecimal.valueOf(5.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(2.5), BigDecimal.valueOf(17.5), "USD");
        
        when(fareRepository.save(any(Fare.class))).thenReturn(mockFare);

        // Assuming Base=5.0, DistanceRate=2.0, TimeRate=0.5
        // Distance=5km, Time=5mins
        Fare fare = fareService.calculateAndSaveFare("ride123", 5.0, 5.0);

        assertNotNull(fare);
        assertEquals("ride123", fare.getRideId());
        verify(fareRepository, times(1)).save(any(Fare.class));
    }

    @Test
    void getFareByRideId_WhenFound_ShouldReturnFare() {
        Fare mockFare = new Fare();
        mockFare.setRideId("ride99");

        when(fareRepository.findByRideId("ride99")).thenReturn(Optional.of(mockFare));

        Fare result = fareService.getFareByRideId("ride99");

        assertNotNull(result);
        assertEquals("ride99", result.getRideId());
    }

    @Test
    void getFareByRideId_WhenNotFound_ShouldThrowException() {
        when(fareRepository.findByRideId("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            fareService.getFareByRideId("invalid");
        });
    }
}
