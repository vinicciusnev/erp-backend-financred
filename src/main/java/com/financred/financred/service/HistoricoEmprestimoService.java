package com.financred.financred.service;

import com.financred.financred.dto.reponse.HistoricoPagamentoResponseDTO;
import com.financred.financred.model.HistoricoPagamentos;
import com.financred.financred.repository.HistoricoPagamentosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HistoricoEmprestimoService {

    private final HistoricoPagamentosRepository historicoPagamentosRepository;

    public List<HistoricoPagamentoResponseDTO> listarTodos() {
        return historicoPagamentosRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }


    public List<HistoricoPagamentoResponseDTO> listarPorCliente(Long idCliente) {
        return historicoPagamentosRepository.findByEmprestimoClienteIdOrderByDataPagamentoDesc(idCliente)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private HistoricoPagamentoResponseDTO toDTO(HistoricoPagamentos historicoPagamentos) {
        return HistoricoPagamentoResponseDTO.builder()
                    .valorPago(historicoPagamentos.getValorPago())
                    .dataPagamento(LocalDate.from(historicoPagamentos.getDataPagamento()))
                    .formaPagamento(historicoPagamentos.getFormaPagamento())
                    .statusPagamento(historicoPagamentos.getStatusPagamento())
                    .observacao(historicoPagamentos.getObservacao())
                    .tipoEmprestimo(historicoPagamentos.getEmprestimo().getTipoEmprestimo())
                    .numeroParcela(historicoPagamentos.getParcela() != null ? historicoPagamentos.getParcela().getNumeroParcela() : null)
                .build();
    }
}
