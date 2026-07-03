package com.oms.auth.controller;

import com.oms.auth.dto.AuthResponse;
import com.oms.auth.dto.LoginRequest;
import com.oms.auth.dto.RegisterRequest;
import com.oms.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
