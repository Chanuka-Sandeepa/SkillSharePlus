package com.example.skillshareplus.config;

import com.example.skillshareplus.security.jwt.AuthTokenFilter;
import com.example.skillshareplus.security.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class that handles authentication and authorization.
 * Configures JWT-based authentication, password encoding, and endpoint security.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthTokenFilter authTokenFilter;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthTokenFilter authTokenFilter) {
        this.userDetailsService = userDetailsService;
        this.authTokenFilter = authTokenFilter;
    }

    /**
     * Configures the authentication provider for the application.
     * Sets up:
     * - Custom user details service for user lookup
     * - BCrypt password encoder for secure password handling
     * 
     * This provider is responsible for:
     * - Loading user details during authentication
     * - Validating passwords using the configured encoder
     * - Integrating with Spring Security's authentication system
     *
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // User details service to fetch user details
        authProvider.setPasswordEncoder(passwordEncoder()); // Password encoder for password encryption
        return authProvider;
    }

    /**
     * Configures the authentication manager for the application.
     * The authentication manager is responsible for:
     * - Processing authentication requests
     * - Coordinating with the authentication provider
     * - Managing the authentication process
     * 
     * This bean is essential for:
     * - Login operations
     * - Token-based authentication
     * - Security context management
     *
     * @param authConfig AuthenticationConfiguration to build the manager
     * @return Configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Bean to configure password encoder using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for the application.
     * Sets up:
     * - CSRF protection (disabled for JWT)
     * - Stateless session management
     * - Endpoint access rules:
     *   - Public access to /api/auth/**
     *   - Admin-only access to /api/admin/**
     *   - Authenticated access for all other endpoints
     * - JWT token filter integration
     *
     * @param http HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Disable CSRF as we're using JWT for authentication
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No session management as we're using stateless authentication (JWT)
            .and()
            .authorizeRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Permit access to authentication endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only allow admin access to admin endpoints
                .anyRequest().authenticated() // All other requests need authentication
            );

        // Adding the authentication provider and token filter
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
