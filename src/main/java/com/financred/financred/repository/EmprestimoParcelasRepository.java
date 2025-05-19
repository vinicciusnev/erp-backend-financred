package com.financred.financred.repository;

import com.financred.financred.enums.StatusParcela;
import com.financred.financred.model.EmprestimoParcelas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmprestimoParcelasRepository extends JpaRepository<EmprestimoParcelas, Long> {
    Optional<EmprestimoParcelas> findByEmprestimoId(Long emprestimoId);
    Optional<EmprestimoParcelas> findByStatusParcela(String statusParcela);
    Optional<List<EmprestimoParcelas>> findByStatusParcelaAndEmprestimoId(StatusParcela statusParcela, Long emprestimoId);
    Optional<EmprestimoParcelas> findByDataVencimentoAndEmprestimoId(LocalDate dataVencimento, Long emprestimoId);
}
