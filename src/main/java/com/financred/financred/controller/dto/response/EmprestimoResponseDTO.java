package com.financred.financred.controller.dto.response;

import com.financred.financred.enums.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoResponseDTO {
    private String id;
    private BigDecimal valorSolicitado;
    private BigDecimal totalComJuros;
    private BigDecimal valorJuros;
    private BigDecimal taxaMultaAtraso;
    private BigDecimal taxaJuros;
    private Integer numeroParcelas;
    private StatusEmprestimo status;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String clienteId;
    private String emailCliente;
    private String tipoEmprestimo;
    private String observacao;
    private LocalDate dataSolicitacao;
    private LocalDate dataAprovacao;
    private List<ParcelasEmprestimosResponseDTO> parcelas;
}