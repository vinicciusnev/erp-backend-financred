package com.financred.financred.controller;

import com.financred.financred.controller.dto.response.ClienteResponseDTO;
import com.financred.financred.controller.dto.request.ClienteRequestDTO;
import com.financred.financred.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarCliente(@RequestBody ClienteRequestDTO clienteDTO) {
        clienteService.save(clienteDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id, @RequestBody ClienteRequestDTO dto) {
        clienteService.updateInfo(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ClienteResponseDTO> getPerfil(Authentication authentication) {
        return ResponseEntity.ok(clienteService.buscarPorId(authentication));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> atualizarPerfil(@RequestBody ClienteRequestDTO dto, Authentication authentication) {
        clienteService.updateInfo(authentication, dto);
        return ResponseEntity.ok().build();
    }
}