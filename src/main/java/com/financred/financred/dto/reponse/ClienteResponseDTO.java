package com.financred.financred.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClienteResponseDTO {
    private String nome;
    private String cpf;
    private LocalDate nascimento;
    private String email;
}