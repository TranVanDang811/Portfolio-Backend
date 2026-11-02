package com.portfolioBackend.backend.common.config;

import com.portfolioBackend.backend.User.entity.User;
import com.portfolioBackend.backend.User.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${admin.username:admin}")
    String adminUsername;

    @NonFinal
    @Value("${admin.password:admin}")
    String adminPassword;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            log.info("Checking initial application setup...");

            // Initialize unique default Admin user if not exists
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User user = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .enabled(true)
                        .role("ADMIN")
                        .build();

                userRepository.save(user);
                log.warn("Default admin account created with username: '{}'. Please ensure password is set securely in production.", adminUsername);
            } else {
                log.info("Admin account '{}' already exists. Skipping creation.", adminUsername);
            }

            log.info("Application initialization completed.");
        };
    }
}