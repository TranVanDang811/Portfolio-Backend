package com.portfolioBackend.backend.auth.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.portfolioBackend.backend.auth.dto.*;
import com.portfolioBackend.backend.auth.entity.InvalidatedToken;
import com.portfolioBackend.backend.auth.repository.InvalidatedTokenRepository;
import com.portfolioBackend.backend.auth.service.AuthenticationService;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.User.entity.User;
import com.portfolioBackend.backend.User.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshableDuration;

    // Verify token validity
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            return IntrospectResponse.builder().valid(false).build();
        }
        return IntrospectResponse.builder().valid(true).build();
    }

    // Authenticate user credentials and generate JWT tokens
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);
        var refreshToken = generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    // Logout and invalidate token
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    // Verify JWT signature and expiration
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(refreshableDuration, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    // Refresh access token using valid refresh token
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        var refreshToken = generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant now = Instant.now();
        Instant refreshExpiry = now.plus(refreshableDuration, ChronoUnit.SECONDS);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("portfolio.com")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(refreshExpiry))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create refresh token", e);
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    // Generate JWT access token
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant now = Instant.now();
        Instant expiration = now.plus(validDuration, ChronoUnit.SECONDS);

        String role = user.getRole() != null ? user.getRole() : "ADMIN";
        String scope = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("portfolio.com")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", scope);

        if (user.getId() != null) {
            claimsBuilder.claim("userId", user.getId().toString());
        }

        JWTClaimsSet jwtClaimsSet = claimsBuilder.build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }
}