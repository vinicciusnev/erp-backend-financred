package com.financred.financred.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmprestimoRequestDTO {
    private Long id;
    private BigDecimal valorSolicitado;
    private Integer parcelas;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal juros;
    private String observacao;
    private String tipoEmprestimo;
    private ClienteRequestDTO cliente;
}
