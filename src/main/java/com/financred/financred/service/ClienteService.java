package com.financred.financred.service;

import com.financred.financred.dto.reponse.ClienteResponseDTO;
import com.financred.financred.dto.request.ClienteRequestDTO;
import com.financred.financred.enums.Role;
import com.financred.financred.model.Cliente;
import com.financred.financred.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(ClienteRequestDTO dto) {
        Cliente cliente = Cliente.builder()
                .nomeCompleto(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .dataNascimento(dto.getNascimento())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .role(Role.CLIENTE)
                .build();

        clienteRepository.save(cliente);
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return mapToResponse(cliente);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    private ClienteResponseDTO mapToResponse(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .nome(cliente.getNomeCompleto())
                .cpf(cliente.getCpf())
                .email(cliente.getEmail())
                .nascimento(cliente.getDataNascimento())
                .build();
    }
}