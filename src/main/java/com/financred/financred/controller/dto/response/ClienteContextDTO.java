package com.financred.financred.controller.dto.response;

import com.financred.financred.model.Cliente;

import java.math.BigDecimal;

public record ClienteContextDTO(Cliente cliente, BigDecimal valorParcela, boolean hasEmAberto, BigDecimal comprometimentoMaximo) {}

