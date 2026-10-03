package com.ridelink.ride.service;

import com.ridelink.ride.dto.FareDTO;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RideService rideService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignDriver_WhenRequested_ShouldAssign() {
        Ride ride = new Ride();
        ride.setId("ride1");
        ride.setStatus(RideStatus.REQUESTED);

        when(rideRepository.findById("ride1")).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Ride updatedRide = rideService.assignDriver("ride1", 99L);

        assertEquals(RideStatus.ASSIGNED, updatedRide.getStatus());
        assertEquals(99L, updatedRide.getDriverId());
    }

    @Test
    void assignDriver_WhenNotRequested_ShouldThrowException() {
        Ride ride = new Ride();
        ride.setId("ride2");
        ride.setStatus(RideStatus.IN_PROGRESS);

        when(rideRepository.findById("ride2")).thenReturn(Optional.of(ride));

        assertThrows(IllegalStateException.class, () -> {
            rideService.assignDriver("ride2", 99L);
        });
    }

    @Test
    void updateRideStatus_ValidTransition_ShouldUpdate() {
        Ride ride = new Ride();
        ride.setId("ride3");
        ride.setStatus(RideStatus.ASSIGNED);

        when(rideRepository.findById("ride3")).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Ride updatedRide = rideService.updateRideStatus("ride3", RideStatus.ACCEPTED);

        assertEquals(RideStatus.ACCEPTED, updatedRide.getStatus());
    }
}
