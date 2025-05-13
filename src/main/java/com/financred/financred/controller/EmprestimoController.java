package com.financred.financred.controller;

import com.financred.financred.dto.reponse.ClienteAuthDTO;
import com.financred.financred.dto.reponse.EmprestimoResponseDTO;
import com.financred.financred.dto.request.EmprestimoRequestDTO;
import com.financred.financred.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    @Autowired
    private final EmprestimoService emprestimoService;

    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @PostMapping
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
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) ((Authentication) principal).getPrincipal();

        if (!clienteAuth.getId().equals(id) &&
                ((Authentication) principal).getAuthorities()
                        .stream()
                        .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        List<EmprestimoResponseDTO> emprestimoResponseDtos = emprestimoService.listarPorCliente(id);

        return ResponseEntity.ok(emprestimoResponseDtos);
    }

    @PutMapping("/quitar-parcela")
    public ResponseEntity<?> quitarParcela(@RequestBody LocalDate dataVencimento, Long idEmprestimo) {
        emprestimoService.quitarParcela(dataVencimento, idEmprestimo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/quitar-emprestimo")
    public ResponseEntity<?> quitarEmprestimo(@RequestBody Long idEmprestimo) {
        emprestimoService.quitarEmprestimo(idEmprestimo);
        return ResponseEntity.ok().build();
    }
}