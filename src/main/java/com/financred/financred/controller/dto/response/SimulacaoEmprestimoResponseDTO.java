package com.financred.financred.controller.dto.response;

import java.math.BigDecimal;

public record SimulacaoEmprestimoResponseDTO(
        BigDecimal valorParcela,
        Integer numeroParcelas,
        BigDecimal totalComJuros,
        BigDecimal valorJuros,
        BigDecimal taxaJuros
) {}

