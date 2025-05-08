package com.financred.financred.controller;

import com.financred.financred.dto.reponse.AuthResponseDTO;
import com.financred.financred.dto.reponse.RegisterResponseDTO;
import com.financred.financred.dto.request.LoginRequestDTO;
import com.financred.financred.dto.request.RegisterRequestDTO;
import com.financred.financred.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
