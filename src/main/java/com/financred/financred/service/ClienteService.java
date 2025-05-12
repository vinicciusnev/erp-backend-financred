package com.financred.financred.service;

import com.financred.financred.dto.reponse.ClienteResponseDTO;
import com.financred.financred.dto.request.ClienteRequestDTO;
import com.financred.financred.enums.Role;
import com.financred.financred.model.Cliente;
import com.financred.financred.repository.ClienteRepository;
import com.financred.financred.service.strategy.ClienteDefaultUpdateStrategy;
import lombok.RequiredArgsConstructor;
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

    public void save(ClienteRequestDTO request) {
        Cliente cliente = Cliente.builder()
                .nomeCompleto(request.getNomeCompleto())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(Role.CLIENTE)
                .build();

        logger.info("Cliente criado com sucesso!" + cliente);

        clienteRepository.save(cliente);
    }

    public void updateInfo(Long id, ClienteRequestDTO request) {
        Cliente clienteExistente = clienteRepository.getReferenceById(id);

        ClienteDefaultUpdateStrategy clienteDefaultUpdateStrategy = new ClienteDefaultUpdateStrategy();
        clienteDefaultUpdateStrategy.updateClienteFromDto(clienteExistente, request);

        clienteRepository.save(clienteExistente);
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