package com.portfolioBackend.backend.auth.service;

import com.portfolioBackend.backend.User.entity.User;
import com.portfolioBackend.backend.User.repository.UserRepository;
import com.portfolioBackend.backend.auth.dto.AuthenticationRequest;
import com.portfolioBackend.backend.auth.dto.AuthenticationResponse;
import com.portfolioBackend.backend.auth.repository.InvalidatedTokenRepository;
import com.portfolioBackend.backend.auth.service.impl.AuthenticationServiceImpl;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "signerKey", "1234567890123456789012345678901234567890123456789012345678901234");
        ReflectionTestUtils.setField(authenticationService, "validDuration", 7200L);
        ReflectionTestUtils.setField(authenticationService, "refreshableDuration", 86400L);

        sampleUser = User.builder()
                .id(java.util.UUID.randomUUID())
                .username("admin")
                .password("$2a$10$hashedPasswordSample")
                .build();
    }

    @Test
    @DisplayName("Test 1: Authenticate success with valid credentials")
    void authenticate_Success() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("admin")
                .password("Admin@123")
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Admin@123", sampleUser.getPassword())).thenReturn(true);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertTrue(response.isAuthenticated());
        verify(userRepository, times(1)).findByUsername("admin");
        verify(passwordEncoder, times(1)).matches("Admin@123", sampleUser.getPassword());
    }

    @Test
    @DisplayName("Test 2: Authenticate fail when password does not match")
    void authenticate_WrongPassword_ThrowsAppException() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("admin")
                .password("WrongPassword")
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", sampleUser.getPassword())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));
        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Test 3: Authenticate fail when user not found")
    void authenticate_UserNotFound_ThrowsAppException() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("nonexistent")
                .password("Password123")
                .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}
