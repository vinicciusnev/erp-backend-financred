package com.financred.financred.controller;

import com.financred.financred.controller.dto.response.EmprestimoParcelasResponseDTO;
import com.financred.financred.controller.dto.response.EmprestimoResponseDTO;
import com.financred.financred.controller.dto.request.EmprestimoRequestDTO;
import com.financred.financred.controller.dto.request.QuitarEmprestimoRequestDTO;
import com.financred.financred.controller.dto.request.QuitarParcelaRequestDTO;
import com.financred.financred.controller.dto.response.SimulacaoEmprestimoResponseDTO;
import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void solicitarEmprestimo(@RequestBody EmprestimoRequestDTO request) {
        emprestimoService.solicitaEmprestimo(request);
    }

    @PostMapping("/simular")
    public ResponseEntity<SimulacaoEmprestimoResponseDTO> simularEmprestimo(@RequestBody EmprestimoRequestDTO request) {
        return ResponseEntity.ok(emprestimoService.simularEmprestimo(request));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarTodosEmprestimos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarDoCliente(Authentication auth) {
        return ResponseEntity.ok(emprestimoService.listarPorCliente(auth));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarDoCliente(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.listarPorCliente(id));
    }

    @GetMapping("/me/status")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorStatus(@RequestParam StatusEmprestimo status, Authentication auth) {
        return ResponseEntity.ok(emprestimoService.listarPorStatus(status, auth));
    }

    @GetMapping("/{id}/parcelas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<EmprestimoParcelasResponseDTO>> listarParcelas(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(emprestimoService.listarParcelasPorEmprestimoEUsuario(id, auth));
    }

    @PostMapping("/parcelas/quitar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> quitarParcela(@RequestBody QuitarParcelaRequestDTO dto) {
        emprestimoService.quitarParcela(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/quitar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> quitarEmprestimo(@RequestBody QuitarEmprestimoRequestDTO dto) {
        emprestimoService.quitarEmprestimo(dto);
        return ResponseEntity.ok().build();
    }
}