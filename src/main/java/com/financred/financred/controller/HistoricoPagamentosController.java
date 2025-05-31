package com.financred.financred.controller;


import com.financred.financred.controller.dto.response.HistoricoPagamentoResponseDTO;
import com.financred.financred.service.HistoricoEmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historico-pagamentos")
@RequiredArgsConstructor
public class HistoricoPagamentosController {

    private final HistoricoEmprestimoService historicoEmprestimoService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HistoricoPagamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(historicoEmprestimoService.listarTodos());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<HistoricoPagamentoResponseDTO>> listarDoCliente(Authentication auth) {
        return ResponseEntity.ok(historicoEmprestimoService.listarPorCliente(auth));
    }
}