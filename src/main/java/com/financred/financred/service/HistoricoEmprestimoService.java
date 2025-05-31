package com.financred.financred.service;

import com.financred.financred.controller.dto.response.ClienteAuthDTO;
import com.financred.financred.controller.dto.response.HistoricoPagamentoResponseDTO;
import com.financred.financred.model.HistoricoPagamentos;
import com.financred.financred.repository.HistoricoPagamentosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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


    public List<HistoricoPagamentoResponseDTO> listarPorCliente(Authentication authentication) {
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();

        return historicoPagamentosRepository.findByEmprestimoClienteIdOrderByDataPagamentoDesc(clienteAuth.getId())
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
