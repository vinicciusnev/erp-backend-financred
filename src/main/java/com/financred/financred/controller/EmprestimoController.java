package com.financred.financred.controller;

import com.financred.financred.dto.reponse.EmprestimoParcelasResponseDTO;
import com.financred.financred.dto.reponse.EmprestimoResponseDTO;
import com.financred.financred.dto.request.EmprestimoRequestDTO;
import com.financred.financred.dto.request.QuitarEmprestimoRequestDTO;
import com.financred.financred.dto.request.QuitarParcelaRequestDTO;
import com.financred.financred.enums.StatusEmprestimo;
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

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorCliente(@PathVariable Long id, Principal principal) {

        List<EmprestimoResponseDTO> emprestimoResponseDtos = emprestimoService.listarPorCliente(id, principal);

        return ResponseEntity.ok(emprestimoResponseDtos);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorStatus(@RequestParam StatusEmprestimo statusEmprestimo, Principal principal) {

        List<EmprestimoResponseDTO> emprestimoResponseDtos = emprestimoService.listarPorStatus(statusEmprestimo, principal);

        return ResponseEntity.ok(emprestimoResponseDtos);
    }


    @GetMapping("{emprestimoId}/parcelas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<EmprestimoParcelasResponseDTO>> listarParcelasPorEmprestimo(@PathVariable Long emprestimoId, Principal principal) {

        List<EmprestimoParcelasResponseDTO> parcelas = emprestimoService.listarParcelasPorEmprestimoEUsuario(emprestimoId, principal);

        return ResponseEntity.ok(parcelas);
    }

    @PutMapping("/quitar-parcela")
    public ResponseEntity<?> quitarParcela(@RequestBody QuitarParcelaRequestDTO request) {
        emprestimoService.quitarParcela(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/quitar-emprestimo")
    public ResponseEntity<?> quitarEmprestimo(@RequestBody QuitarEmprestimoRequestDTO request) {
        emprestimoService.quitarEmprestimo(request);
        return ResponseEntity.ok().build();
    }
}