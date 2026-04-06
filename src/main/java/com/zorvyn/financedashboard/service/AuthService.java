package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.request.LoginRequest;
import com.zorvyn.financedashboard.dto.request.RegisterRequest;
import com.zorvyn.financedashboard.dto.response.AuthResponse;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.enums.Role;
import com.zorvyn.financedashboard.enums.UserStatus;
import com.zorvyn.financedashboard.repository.UserRepository;
import com.zorvyn.financedashboard.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + request.email());
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.VIEWER)         // all new users start as VIEWER
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {

        // This throws BadCredentialsException if wrong - handled by GlobalExceptionHandler
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}