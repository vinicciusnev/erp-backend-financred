package com.financred.financred.dto.reponse;

import com.financred.financred.enums.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoResponseDTO {
    private Long id;
    private BigDecimal valorSolicitado;
    private BigDecimal totalComJuros;
    private BigDecimal valorJuros;
    private BigDecimal taxaMultaAtraso;
    private BigDecimal taxaJuros;
    private Integer numeroParcelas;
    private StatusEmprestimo status;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long clienteId;
    private String tipoEmprestimo;
    private String observacao;
    private LocalDate dataSolicitacao;
    private LocalDate dataAprovacao;
    private String aprovadoPor;
}