package com.financred.financred.dto.reponse;

import com.financred.financred.model.Cliente;

import java.math.BigDecimal;

public record ClienteContextDTO(Cliente cliente, BigDecimal valorParcela, boolean hasEmAberto, BigDecimal comprometimentoMaximo) {}

