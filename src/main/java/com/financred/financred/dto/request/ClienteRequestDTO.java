package com.financred.financred.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nomeCompleto;

    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    private String cpf;

    @Email(message = "Email inválido")
    private String email;

    private String telefone;
    private LocalDate dataNascimento;
    private String senha;
    private EnderecoDTO endereco;
    private DadosFinanceirosDTO dadosFinanceiros;
}