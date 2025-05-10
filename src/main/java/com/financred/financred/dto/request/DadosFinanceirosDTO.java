package com.financred.financred.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosFinanceirosDTO {
    @DecimalMin(value = "0.0", message = "Renda deve ser um número positivo")
    private BigDecimal renda;

    private String profissao;

    private String empresa;

    private ContaBancariaDTO contaBancaria;
}
