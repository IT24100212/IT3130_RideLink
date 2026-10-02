package com.ridelink.accountservice.security;

import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TEST_EXPIRATION_MS = 3600000; // 1 hour

    private User sampleUser;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(TEST_SECRET, TEST_EXPIRATION_MS);

        sampleUser = User.builder()
                .id("user_test_999")
                .firstName("Test")
                .lastName("User")
                .email("test.user@ridelink.com")
                .phoneNumber("+94771234567")
                .passwordHash("hashed")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Generate token and validate claims")
    void testGenerateTokenAndValidate() {
        String token = jwtTokenProvider.generateToken(sampleUser);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo("user_test_999");
        assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo("test.user@ridelink.com");
        assertThat(jwtTokenProvider.getRoleFromToken(token)).isEqualTo(Role.PASSENGER);
    }

    @Test
    @DisplayName("Invalid token validation returns false")
    void testInvalidTokenValidation() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.structure")).isFalse();
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("Expired token validation returns false")
    void testExpiredTokenValidation() throws InterruptedException {
        // Create token provider with 1 ms expiration
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(TEST_SECRET, 1);
        String token = shortLivedProvider.generateToken(sampleUser);

        Thread.sleep(10); // Wait for token to expire

        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }
}
