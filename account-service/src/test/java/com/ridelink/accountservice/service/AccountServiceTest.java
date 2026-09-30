package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.UpdateProfileRequest;
import com.ridelink.accountservice.dto.UserResponse;
import com.ridelink.accountservice.exception.ResourceNotFoundException;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import com.ridelink.accountservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+94771234567")
                .passwordHash("hashed_pw")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Requirement 6: Get profile with authenticated user ID")
    void testGetCurrentUserProfileSuccess() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(sampleUser));

        UserResponse response = accountService.getCurrentUserProfile("user123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getRole()).isEqualTo(Role.PASSENGER);
        assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        verify(userRepository, times(1)).findById("user123");
    }

    @Test
    @DisplayName("Requirement 7: Update profile of user")
    void testUpdateCurrentUserProfileSuccess() {
        UpdateProfileRequest request = new UpdateProfileRequest("Johnny", "Doeman", "+94779998888");

        when(userRepository.findById("user123")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = accountService.updateCurrentUserProfile("user123", request);

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Johnny");
        assertThat(response.getLastName()).isEqualTo("Doeman");
        assertThat(response.getPhoneNumber()).isEqualTo("+94779998888");

        verify(userRepository, times(1)).findById("user123");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Requirement 8: Account not found throws ResourceNotFoundException")
    void testAccountNotFoundThrowsException() {
        when(userRepository.findById("nonexistent_id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById("nonexistent_id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found with ID: nonexistent_id");

        verify(userRepository, times(1)).findById("nonexistent_id");
    }

    @Test
    @DisplayName("Requirement 12: Account status update succeeds")
    void testAccountStatusUpdateSuccess() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = accountService.updateAccountStatus("user123", AccountStatus.SUSPENDED);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(sampleUser.getStatus()).isEqualTo(AccountStatus.SUSPENDED);

        verify(userRepository, times(1)).findById("user123");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Account role update succeeds")
    void testAccountRoleUpdateSuccess() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = accountService.updateAccountRole("user123", Role.DRIVER);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(Role.DRIVER);
        assertThat(sampleUser.getRole()).isEqualTo(Role.DRIVER);

        verify(userRepository, times(1)).findById("user123");
        verify(userRepository, times(1)).save(sampleUser);
    }
}
