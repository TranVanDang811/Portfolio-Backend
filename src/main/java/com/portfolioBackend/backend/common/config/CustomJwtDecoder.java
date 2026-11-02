package com.portfolioBackend.backend.common.config;


import com.portfolioBackend.backend.auth.dto.IntrospectRequest;
import com.portfolioBackend.backend.auth.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    // Secret signer key loaded from configuration
    @Value("${jwt.signerKey}")
    private String signerKey;

    // Inject AuthenticationService for token introspection
    // @Lazy breaks circular dependency: SecurityConfig -> CustomJwtDecoder -> AuthenticationService -> PasswordEncoder -> SecurityConfig
    @Autowired
    @Lazy
    private AuthenticationService authenticationService;

    // Lazily initialized NimbusJwtDecoder instance
    private NimbusJwtDecoder nimbusJwtDecoder = null;


    private static final Logger logger = LoggerFactory.getLogger(CustomJwtDecoder.class);

    // Override decode() from JwtDecoder to introspect and decode JWT
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Introspect token validity
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());

            // Throw JwtException if token is invalid
            if (!response.isValid()) {
                logger.warn("Token invalid: {}", token);
                throw new JwtException("Token invalid");
            }
        } catch (JOSEException | ParseException e) {
            logger.error("Error during token introspection", e);
            // Propagate token verification failures
            throw new JwtException(e.getMessage());
        }

        // Decode and return JWT claims
        return nimbusJwtDecoder.decode(token);
    }

    @PostConstruct
    public void init() {
        if (signerKey == null || signerKey.isBlank()) {
            throw new IllegalStateException("JWT signerKey is missing or empty");
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
        nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}