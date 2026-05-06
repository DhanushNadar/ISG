package com.example.bloodbank.config;

import com.example.bloodbank.repository.UserRepository;
import com.example.bloodbank.repository.PatientPortalCredentialRepository;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;
    private final PatientPortalCredentialRepository patientPortalCredentialRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var userOpt = repository.findByEmail(username);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
            
            var patientOpt = patientPortalCredentialRepository.findByAadhaarNumber(username);
            if (patientOpt.isPresent()) {
                return User.builder()
                        .email(patientOpt.get().getAadhaarNumber())
                        .password(patientOpt.get().getPassword())
                        .role(Role.PATIENT)
                        .build();
            }
            
            throw new UsernameNotFoundException("User not found: " + username);
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();
                UserDetails userDetails = userDetailsService().loadUserByUsername(username);
                if (passwordEncoder().matches(password, userDetails.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
                } else {
                    throw new BadCredentialsException("Invalid credentials");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
