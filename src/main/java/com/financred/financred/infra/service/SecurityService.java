package com.financred.financred.infra.service;

import com.financred.financred.controller.dto.response.ClienteAuthDTO;
import com.financred.financred.enums.Role;
import com.financred.financred.model.Cliente;
import com.financred.financred.repository.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class SecurityService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente getUser(String login) {
        return clienteRepository
                .findByEmail(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));
    }

    public ClienteAuthDTO getUserBasicInfo(String login) {
        Cliente cliente = getUser(login);
        return new ClienteAuthDTO(
                cliente.getId(),
                cliente.getEmail(),
                cliente.getNomeCompleto()
        );
    }

    public Role getUserWithRole(String login) {
        Cliente cliente = getUser(login);
        return cliente.getRole();
    }
}
