package com.portfolioBackend.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_AUTH_ENDPOINTS = {
            "/auth/token",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refresh",
            "/users/forgot-password",
            "/users/reset-password"
    };

    private final CustomJwtDecoder customJwtDecoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final Environment env;

    public SecurityConfig(
            CustomJwtDecoder customJwtDecoder,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            Environment env
    ) {
        this.customJwtDecoder = customJwtDecoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(cors -> cors.configurationSource(request -> {

                    CorsConfiguration corsConfig = new CorsConfiguration();

                    String allowedOrigins = env.getProperty("cors.allowed-origins", "http://localhost:5173,http://localhost:3000");
                    Arrays.stream(allowedOrigins.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .forEach(corsConfig::addAllowedOrigin);

                    corsConfig.addAllowedMethod("*");
                    corsConfig.addAllowedHeader("*");
                    corsConfig.setAllowCredentials(true);

                    corsConfig.addExposedHeader("Authorization");
                    corsConfig.addExposedHeader("X-Total-Count");

                    return corsConfig;
                }))

                .authorizeHttpRequests(request -> {

                    // CORS preflight
                    request.requestMatchers(HttpMethod.OPTIONS, "/**")
                            .permitAll();

                    // Authentication endpoints
                    request.requestMatchers(
                            HttpMethod.POST,
                            PUBLIC_AUTH_ENDPOINTS
                    ).permitAll();

                    // Swagger / OpenAPI documentation
                    request.requestMatchers(
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html"
                    ).permitAll();

                    // Admin APIs (Must come BEFORE public /api/** rules)
                    request.requestMatchers(
                            "/api/admin/**"
                    ).authenticated();

                    // Public page view recording & contact submission
                    request.requestMatchers(
                            HttpMethod.POST,
                            "/api/page-views",
                            "/api/contacts"
                    ).permitAll();

                    // Public portfolio read APIs
                    request.requestMatchers(
                            HttpMethod.GET,
                            "/api/**",
                            "/project-images/**"
                    ).permitAll();

                    // All other requests require authentication
                    request.anyRequest().authenticated();

                })

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                .csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                jwtGrantedAuthoritiesConverter
        );

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}