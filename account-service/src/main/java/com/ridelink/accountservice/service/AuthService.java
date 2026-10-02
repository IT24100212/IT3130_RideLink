package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.AuthResponse;
import com.ridelink.accountservice.dto.DriverRegisterRequest;
import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.PassengerRegisterRequest;

public interface AuthService {

    AuthResponse registerPassenger(PassengerRegisterRequest request);

    AuthResponse registerDriver(DriverRegisterRequest request);

    AuthResponse login(LoginRequest request);
}
