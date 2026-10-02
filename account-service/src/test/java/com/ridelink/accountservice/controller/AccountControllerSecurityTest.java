package com.ridelink.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.accountservice.config.SecurityConfig;
import com.ridelink.accountservice.dto.UpdateProfileRequest;
import com.ridelink.accountservice.dto.UpdateRoleRequest;
import com.ridelink.accountservice.dto.UpdateStatusRequest;
import com.ridelink.accountservice.dto.UserResponse;
import com.ridelink.accountservice.exception.GlobalExceptionHandler;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.repository.UserRepository;
import com.ridelink.accountservice.security.CustomAccessDeniedHandler;
import com.ridelink.accountservice.security.CustomUserDetailsService;
import com.ridelink.accountservice.security.JwtAuthenticationEntryPoint;
import com.ridelink.accountservice.security.JwtAuthenticationFilter;
import com.ridelink.accountservice.security.JwtTokenProvider;
import com.ridelink.accountservice.security.UserPrincipal;
import com.ridelink.accountservice.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class AccountControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    private UserPrincipal createPrincipal(String id, String email, Role role) {
        return new UserPrincipal(
                id,
                email,
                "password",
                role,
                AccountStatus.ACTIVE,
                Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.name())
                )
        );
    }

    @Test
    @DisplayName("Requirement 9: Unauthorized request without JWT token returns HTTP 401 UNAUTHORIZED")
    void testGetProfileWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/accounts/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("Requirement 6: Get profile with authenticated user returns HTTP 200 OK")
    void testGetProfileAuthenticatedSuccess() throws Exception {
        UserPrincipal principal = createPrincipal("user123", "john.doe@example.com", Role.PASSENGER);
        UserResponse response = new UserResponse(
                "user123", "John", "Doe", "john.doe@example.com", "+94771234567",
                Role.PASSENGER, AccountStatus.ACTIVE, Instant.now(), Instant.now()
        );

        when(accountService.getCurrentUserProfile("user123")).thenReturn(response);

        mockMvc.perform(get("/api/accounts/me")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("PASSENGER"));
    }

    @Test
    @DisplayName("Requirement 7: Update profile with authenticated user returns HTTP 200 OK")
    void testUpdateProfileAuthenticatedSuccess() throws Exception {
        UserPrincipal principal = createPrincipal("user123", "john.doe@example.com", Role.PASSENGER);
        UpdateProfileRequest request = new UpdateProfileRequest("Johnny", "Doeman", "+94779998888");
        UserResponse response = new UserResponse(
                "user123", "Johnny", "Doeman", "john.doe@example.com", "+94779998888",
                Role.PASSENGER, AccountStatus.ACTIVE, Instant.now(), Instant.now()
        );

        when(accountService.updateCurrentUserProfile(eq("user123"), any(UpdateProfileRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/accounts/me")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.phoneNumber").value("+94779998888"));
    }

    @Test
    @DisplayName("Requirement 10: Role-based authorization - PASSENGER cannot update status (returns HTTP 403 FORBIDDEN)")
    void testPassengerCannotUpdateStatusReturnsForbidden() throws Exception {
        UserPrincipal passengerPrincipal = createPrincipal("passenger123", "pass@example.com", Role.PASSENGER);
        UpdateStatusRequest request = new UpdateStatusRequest(AccountStatus.SUSPENDED);

        mockMvc.perform(patch("/api/accounts/user123/status")
                        .with(user(passengerPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("Requirement 10: Role-based authorization - DRIVER cannot update role (returns HTTP 403 FORBIDDEN)")
    void testDriverCannotUpdateRoleReturnsForbidden() throws Exception {
        UserPrincipal driverPrincipal = createPrincipal("driver123", "driver@example.com", Role.DRIVER);
        UpdateRoleRequest request = new UpdateRoleRequest(Role.ADMIN);

        mockMvc.perform(patch("/api/accounts/user123/role")
                        .with(user(driverPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("Requirement 12: Account status update by ADMIN returns HTTP 200 OK")
    void testAdminCanUpdateStatusSuccess() throws Exception {
        UserPrincipal adminPrincipal = createPrincipal("admin123", "admin@ridelink.com", Role.ADMIN);
        UpdateStatusRequest request = new UpdateStatusRequest(AccountStatus.SUSPENDED);

        UserResponse response = new UserResponse(
                "user123", "John", "Doe", "john.doe@example.com", "+94771234567",
                Role.PASSENGER, AccountStatus.SUSPENDED, Instant.now(), Instant.now()
        );

        when(accountService.updateAccountStatus("user123", AccountStatus.SUSPENDED)).thenReturn(response);

        mockMvc.perform(patch("/api/accounts/user123/status")
                        .with(user(adminPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }

    @Test
    @DisplayName("Account role update by ADMIN returns HTTP 200 OK")
    void testAdminCanUpdateRoleSuccess() throws Exception {
        UserPrincipal adminPrincipal = createPrincipal("admin123", "admin@ridelink.com", Role.ADMIN);
        UpdateRoleRequest request = new UpdateRoleRequest(Role.DRIVER);

        UserResponse response = new UserResponse(
                "user123", "John", "Doe", "john.doe@example.com", "+94771234567",
                Role.DRIVER, AccountStatus.ACTIVE, Instant.now(), Instant.now()
        );

        when(accountService.updateAccountRole("user123", Role.DRIVER)).thenReturn(response);

        mockMvc.perform(patch("/api/accounts/user123/role")
                        .with(user(adminPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("DRIVER"));
    }
}
