package com.financred.financred.infra.service;

import com.financred.financred.model.Cliente;
import com.financred.financred.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente getUserWithRole(String login) {
        return clienteRepository.findByEmail(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
