package com.financred.financred.repository;

import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.model.Cliente;
import com.financred.financred.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByClienteId(Long clienteId);
    List<Emprestimo> findByClienteCpf(String cpf);
    List<Emprestimo> findByStatusEmprestimo(StatusEmprestimo status);
    List<Emprestimo> findByDataSolicitacaoBetween(LocalDate inicio, LocalDate fim);
    Optional<Emprestimo> findByClienteAndStatusEmprestimo(Cliente cliente, StatusEmprestimo status);
}
