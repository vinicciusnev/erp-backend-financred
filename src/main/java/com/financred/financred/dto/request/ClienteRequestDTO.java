package com.financred.financred.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClienteRequestDTO {
    private String nome;
    private String cpf;
    private LocalDate nascimento;
    private String email;
    private String senha;
}
