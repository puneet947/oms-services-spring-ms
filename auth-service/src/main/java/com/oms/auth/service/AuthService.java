package com.oms.auth.service;

import com.oms.auth.dto.AuthResponse;
import com.oms.auth.dto.LoginRequest;
import com.oms.auth.dto.RegisterRequest;
import com.oms.auth.entity.User;
import com.oms.auth.repository.UserRepository;
import com.oms.auth.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        // Initialize demo user if not exists
        if (userRepository.findByUsername("demo").isEmpty()) {
            User demoUser = new User("demo", passwordEncoder.encode("password"));
            userRepository.save(demoUser);
        }
    }

    public AuthResponse register(RegisterRequest request) {
        require(request.username(), "username");
        require(request.password(), "password");

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = new User(request.username(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return tokenFor(request.username());
    }

    public AuthResponse login(LoginRequest request) {
        require(request.username(), "username");
        require(request.password(), "password");

        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return tokenFor(request.username());
    }

    private AuthResponse tokenFor(String username) {
        return new AuthResponse(jwtService.issueToken(username), "Bearer", jwtService.expirationSeconds());
    }

    private void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
    }
}
