package com.financred.financred.service;

import com.financred.financred.controller.dto.response.ClienteAuthDTO;
import com.financred.financred.controller.dto.response.ClienteResponseDTO;
import com.financred.financred.controller.dto.request.ClienteRequestDTO;
import com.financred.financred.enums.Role;
import com.financred.financred.model.Cliente;
import com.financred.financred.repository.ClienteRepository;
import com.financred.financred.service.mappers.ClienteMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final Logger logger = Logger.getLogger(ClienteService.class.getName());

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteMapper clienteMapper;

    @Transactional
    public void save(ClienteRequestDTO request) {
        Optional<Cliente> existingCliente = clienteRepository.findByCpf(request.getCpf());

        if (existingCliente.isPresent()) {
            throw new RuntimeException("Já existe um cliente com este CPF.");
        }

        Cliente newCliente = Cliente.builder()
                .nomeCompleto(request.getNomeCompleto())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(Role.ROLE_CLIENTE)
                .build();

        clienteRepository.save(newCliente);
    }

    @Transactional
    public void updateInfo(Authentication authentication, ClienteRequestDTO request) {
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();
        Long idCliente = clienteAuth.getId();

        Cliente clienteExistente = clienteRepository.getReferenceById(idCliente);

        clienteMapper.updateClienteFromDto(request, clienteExistente);

        clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void updateInfo(Long id, ClienteRequestDTO request) {
        Cliente clienteExistente = clienteRepository.getReferenceById(id);

        clienteMapper.updateClienteFromDto(request, clienteExistente);

        clienteRepository.save(clienteExistente);
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO buscarPorId(Authentication authentication) {
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();

        Cliente cliente = clienteRepository.findById(clienteAuth.getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return mapToResponse(cliente);
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
                .id(cliente.getId().toString())
                .nomeCompleto(cliente.getNomeCompleto())
                .cpf(cliente.getCpf())
                .email(cliente.getEmail())
                .nascimento(cliente.getDataNascimento())
                .telefone(cliente.getTelefone())
                .role(cliente.getRole())
                .score(cliente.getScore())
                .renda(cliente.getRendaMensal())
                .profissao(cliente.getProfissao())
                .empresa(cliente.getEmpresa())
                .banco(cliente.getBanco())
                .agencia(cliente.getAgencia())
                .conta(cliente.getConta())
                .rua(cliente.getRua())
                .numero(cliente.getNumero())
                .complemento(cliente.getComplemento())
                .bairro(cliente.getBairro())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .cep(cliente.getCep())
                .build();
    }
}