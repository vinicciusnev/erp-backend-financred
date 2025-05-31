package com.financred.financred.controller.dto.response;

import com.financred.financred.enums.StatusParcela;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoParcelasResponseDTO {

    private String id;
    private BigDecimal valorParcela;
    private BigDecimal valorJuros;
    private StatusParcela statusParcela;
    private Integer diasAtraso;
    private Integer numeroParcela;
    private BigDecimal multa;
    private String observacao;
    private String emprestimoId;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
