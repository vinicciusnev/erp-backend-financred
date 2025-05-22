package com.financred.financred.repository;

import com.financred.financred.model.HistoricoPagamentos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoPagamentosRepository extends JpaRepository<HistoricoPagamentos, Long> {
    List<HistoricoPagamentos> findByEmprestimoClienteIdOrderByDataPagamentoDesc(Long idCliente);
}
