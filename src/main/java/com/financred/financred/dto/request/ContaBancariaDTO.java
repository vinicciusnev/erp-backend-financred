package com.financred.financred.dto.request;

import lombok.Data;

@Data
public class ContaBancariaDTO {
    private String banco;
    private String agencia;
    private String conta;
}
