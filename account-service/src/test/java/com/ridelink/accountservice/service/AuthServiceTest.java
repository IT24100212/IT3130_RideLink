package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.AuthResponse;
import com.ridelink.accountservice.dto.DriverRegisterRequest;
import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.PassengerRegisterRequest;
import com.ridelink.accountservice.exception.DuplicateEmailException;
import com.ridelink.accountservice.exception.InvalidAccountStatusException;
import com.ridelink.accountservice.exception.InvalidCredentialsException;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import com.ridelink.accountservice.security.JwtTokenProvider;
import com.ridelink.accountservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+94771234567")
                .passwordHash("encoded_password")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Requirement 1: Passenger registration success")
    void testPassengerRegistrationSuccess() {
        PassengerRegisterRequest request = new PassengerRegisterRequest(
                "John", "Doe", "john.doe@example.com", "+94771234567", "Password@123"
        );

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("jwt_mock_token");

        AuthResponse response = authService.registerPassenger(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt_mock_token");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getRole()).isEqualTo(Role.PASSENGER);
        assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(passwordEncoder, times(1)).encode("Password@123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtTokenProvider, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Requirement 2: Driver registration success")
    void testDriverRegistrationSuccess() {
        DriverRegisterRequest request = new DriverRegisterRequest(
                "Jane", "Smith", "jane.smith@example.com", "+94777654321", "SecurePass@123"
        );

        User driverUser = User.builder()
                .id("driver123")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+94777654321")
                .passwordHash("encoded_driver_password")
                .role(Role.DRIVER)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass@123")).thenReturn("encoded_driver_password");
        when(userRepository.save(any(User.class))).thenReturn(driverUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("driver_jwt_token");

        AuthResponse response = authService.registerDriver(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("driver_jwt_token");
        assertThat(response.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(response.getRole()).isEqualTo(Role.DRIVER);
        assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        verify(userRepository, times(1)).existsByEmail("jane.smith@example.com");
        verify(passwordEncoder, times(1)).encode("SecurePass@123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Requirement 3: Duplicate email registration throws DuplicateEmailException")
    void testDuplicateEmailRegistration() {
        PassengerRegisterRequest request = new PassengerRegisterRequest(
                "John", "Doe", "john.doe@example.com", "+94771234567", "Password@123"
        );

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerPassenger(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email is already registered: john.doe@example.com");

        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Requirement 4: Login success with valid credentials")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("john.doe@example.com", "Password@123");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password@123", "encoded_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(sampleUser)).thenReturn("valid_login_token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("valid_login_token");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getRole()).isEqualTo(Role.PASSENGER);
        assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Requirement 5: Login with invalid password throws InvalidCredentialsException")
    void testLoginWithInvalidPassword() {
        LoginRequest request = new LoginRequest("john.doe@example.com", "WrongPassword");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", "encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");

        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Login with non-existent email throws InvalidCredentialsException")
    void testLoginWithNonExistentEmail() {
        LoginRequest request = new LoginRequest("unknown@example.com", "Password@123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("Login with suspended account throws InvalidAccountStatusException")
    void testLoginWithSuspendedAccount() {
        sampleUser.setStatus(AccountStatus.SUSPENDED);
        LoginRequest request = new LoginRequest("john.doe@example.com", "Password@123");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password@123", "encoded_password")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidAccountStatusException.class)
                .hasMessageContaining("Account is suspended");
    }

    @Test
    @DisplayName("Login with inactive account throws InvalidAccountStatusException")
    void testLoginWithInactiveAccount() {
        sampleUser.setStatus(AccountStatus.INACTIVE);
        LoginRequest request = new LoginRequest("john.doe@example.com", "Password@123");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password@123", "encoded_password")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidAccountStatusException.class)
                .hasMessageContaining("Account is inactive");
    }
}
