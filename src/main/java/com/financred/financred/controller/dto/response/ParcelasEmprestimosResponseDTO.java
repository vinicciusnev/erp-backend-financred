package com.financred.financred.controller.dto.response;

import com.financred.financred.enums.StatusParcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelasEmprestimosResponseDTO(
        String id,
        String emprestimoId,
        String numeroParcela,
        StatusParcela status,
        LocalDate dataVencimento,
        BigDecimal valorParcela,
        BigDecimal multa
) {
}
