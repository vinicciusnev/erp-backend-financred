package com.financred.financred.controller;

import com.financred.financred.controller.dto.response.AuthResponseDTO;
import com.financred.financred.controller.dto.response.RegisterResponseDTO;
import com.financred.financred.controller.dto.request.LoginRequestDTO;
import com.financred.financred.controller.dto.request.RegisterRequestDTO;
import com.financred.financred.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO auth = authService.autenticar(request);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO registerResponseDTO = authService.register(request);
        return ResponseEntity.ok(registerResponseDTO);
    }
}
