package com.financred.financred.controller;

import com.financred.financred.dto.reponse.EmprestimoResponseDTO;
import com.financred.financred.dto.request.EmprestimoRequestDTO;
import com.financred.financred.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    @Autowired
    private final EmprestimoService emprestimoService;

    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public void solicitarEmprestimo(@RequestBody EmprestimoRequestDTO request) {
        emprestimoService.solicitaEmprestimo(request);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarTodosEmprestimos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @GetMapping("/cliente/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorCliente(@PathVariable Long id,
                                                                        Principal principal) {
        // Apenas ADMIN ou o próprio cliente pode ver seus empréstimos
        if (!principal.getName().equals(String.valueOf(id)) &&
                !principal.toString().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(emprestimoService.listarPorCliente(id));
    }
}