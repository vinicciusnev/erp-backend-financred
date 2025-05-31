package com.financred.financred.controller.dto.request;

import lombok.Data;

@Data
public class EnderecoDTO {
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}