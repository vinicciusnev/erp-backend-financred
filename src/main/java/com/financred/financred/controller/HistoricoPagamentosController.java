package com.financred.financred.controller;


import com.financred.financred.dto.reponse.HistoricoPagamentoResponseDTO;
import com.financred.financred.service.HistoricoEmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historico")
@RequiredArgsConstructor
public class HistoricoPagamentosController {

    private final HistoricoEmprestimoService historicoEmprestimoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<HistoricoPagamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(historicoEmprestimoService.listarTodos());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<HistoricoPagamentoResponseDTO>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(historicoEmprestimoService.listarPorCliente(idCliente));
    }
}
