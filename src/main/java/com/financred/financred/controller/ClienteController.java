package com.financred.financred.controller;

import com.financred.financred.dto.reponse.ClienteResponseDTO;
import com.financred.financred.dto.request.ClienteRequestDTO;
import com.financred.financred.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> criarCliente(@RequestBody ClienteRequestDTO clienteDTO) {
        clienteService.save(clienteDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClienteResponseDTO> listarClientes() {
        return clienteService.listarTodos();
    }
}