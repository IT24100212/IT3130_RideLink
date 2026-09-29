package com.ridelink.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.accountservice.dto.AuthResponse;
import com.ridelink.accountservice.dto.DriverRegisterRequest;
import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.PassengerRegisterRequest;
import com.ridelink.accountservice.exception.GlobalExceptionHandler;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Requirement 1: Passenger registration HTTP 201 Created")
    void testRegisterPassengerApiSuccess() throws Exception {
        PassengerRegisterRequest request = new PassengerRegisterRequest(
                "John", "Doe", "john.doe@example.com", "+94771234567", "Password@123"
        );

        AuthResponse authResponse = new AuthResponse(
                "jwt_token_123", "id123", "John", "Doe", "john.doe@example.com", Role.PASSENGER, AccountStatus.ACTIVE
        );

        when(authService.registerPassenger(any(PassengerRegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register/passenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt_token_123"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("PASSENGER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Requirement 2: Driver registration HTTP 201 Created")
    void testRegisterDriverApiSuccess() throws Exception {
        DriverRegisterRequest request = new DriverRegisterRequest(
                "Jane", "Smith", "jane.smith@example.com", "+94777654321", "SecurePass@123"
        );

        AuthResponse authResponse = new AuthResponse(
                "driver_jwt_456", "driverId456", "Jane", "Smith", "jane.smith@example.com", Role.DRIVER, AccountStatus.ACTIVE
        );

        when(authService.registerDriver(any(DriverRegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("driver_jwt_456"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.role").value("DRIVER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Requirement 4: Login API HTTP 200 OK")
    void testLoginApiSuccess() throws Exception {
        LoginRequest request = new LoginRequest("john.doe@example.com", "Password@123");

        AuthResponse authResponse = new AuthResponse(
                "login_jwt_token", "id123", "John", "Doe", "john.doe@example.com", Role.PASSENGER, AccountStatus.ACTIVE
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login_jwt_token"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Requirement 11: Validation failure returns HTTP 400 BAD_REQUEST with field error details")
    void testValidationFailureReturns400() throws Exception {
        // Missing firstName, invalid email, short password (< 8 chars)
        PassengerRegisterRequest invalidRequest = new PassengerRegisterRequest(
                "", "Doe", "not-an-email", "+94771234567", "short"
        );

        mockMvc.perform(post("/api/auth/register/passenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
