package com.example.skillshareplus.config;

import com.example.skillshareplus.model.User;
import com.example.skillshareplus.model.Role;
import com.example.skillshareplus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.admin.email}")
    private String adminEmail;

    @Value("${app.init.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName("Admin")
                    .lastName("User")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created with email: " + adminEmail);
        }
    }
}