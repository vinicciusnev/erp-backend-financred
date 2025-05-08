package com.financred.financred.service;

import com.financred.financred.dto.reponse.AuthResponseDTO;
import com.financred.financred.dto.reponse.RegisterResponseDTO;
import com.financred.financred.dto.request.LoginRequestDTO;
import com.financred.financred.dto.request.RegisterRequestDTO;
import com.financred.financred.enums.Role;
import com.financred.financred.exception.InvalidCredentialsException;
import com.financred.financred.infra.service.TokenService;
import com.financred.financred.model.Cliente;
import com.financred.financred.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthResponseDTO autenticar(LoginRequestDTO request) {
        Cliente cliente = clienteRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (!passwordEncoder.matches(request.senha(), cliente.getSenha())) {
            throw new InvalidCredentialsException("E-mail ou senha incorretos. Verifique suas credenciais, e tente novamente.");
        }

        String token = tokenService.generateToken(request.email());

        return new AuthResponseDTO(cliente.getEmail(), token, cliente.getRole().toString());
    }

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (Boolean.TRUE.equals(clienteRepository.existsByEmail(request.email()))) {
            throw new RuntimeException("Usuário já cadastrado!");
        }

        Cliente newCliente = new Cliente();

        newCliente.setCpf(request.cpf());
        newCliente.setEmail(request.email());
        newCliente.setRole(Role.CLIENTE);
        newCliente.setSenha(passwordEncoder.encode(request.senha()));
        newCliente.setNomeCompleto(request.nomeCompleto());

        clienteRepository.save(newCliente);

        String token = tokenService.generateToken(request.email());
        return new RegisterResponseDTO(token, Role.CLIENTE.toString());
    }
}