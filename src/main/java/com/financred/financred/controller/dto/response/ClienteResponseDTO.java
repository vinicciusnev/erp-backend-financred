package com.financred.financred.controller.dto.response;

import com.financred.financred.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@Builder
public class ClienteResponseDTO {
    private String id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate nascimento;
    private String email;
    private String telefone;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private BigDecimal renda;
    private String profissao;
    private String empresa;
    private String banco;
    private String agencia;
    private String conta;
    private Long score;
    private Role role;
}