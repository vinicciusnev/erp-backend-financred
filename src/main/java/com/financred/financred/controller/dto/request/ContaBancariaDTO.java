package com.financred.financred.controller.dto.request;

import lombok.Data;

@Data
public class ContaBancariaDTO {
    private String banco;
    private String agencia;
    private String conta;
}
