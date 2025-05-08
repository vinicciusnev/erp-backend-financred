package com.financred.financred.dto.reponse;

import com.financred.financred.enums.StatusEmprestimo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmprestimoResponseDTO {
    private Long id;
    private BigDecimal valorSolicitado;
    private LocalDate dataSolicitacao;
    private StatusEmprestimo status;
    private Long clienteId;
    private Integer parcelas;
    private BigDecimal valorParcela;
    private BigDecimal totalComJuros;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal juros;

}