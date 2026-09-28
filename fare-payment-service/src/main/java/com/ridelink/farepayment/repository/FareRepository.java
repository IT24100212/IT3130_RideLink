package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {
    Optional<Fare> findByRideId(Long rideId);
}