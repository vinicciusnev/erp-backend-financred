package com.financred.financred.dto.reponse;

import com.financred.financred.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@Builder
public class ClienteResponseDTO {
    private String nomeCompleto;
    private String cpf;
    private LocalDate nascimento;
    private String email;
    private String telefone;

    // Endereço
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    // Dados financeiros
    private BigDecimal renda;
    private String profissao;
    private String empresa;
    private String banco;
    private String agencia;
    private String conta;

    // Outros
    private Long score;
    private Role role;
}