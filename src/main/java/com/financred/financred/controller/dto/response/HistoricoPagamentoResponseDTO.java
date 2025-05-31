package com.financred.financred.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class HistoricoPagamentoResponseDTO {
    private BigDecimal valorPago;
    private LocalDate dataPagamento;
    private String formaPagamento;
    private String statusPagamento;
    private String observacao;
    private String tipoEmprestimo;
    private Integer numeroParcela;
}
