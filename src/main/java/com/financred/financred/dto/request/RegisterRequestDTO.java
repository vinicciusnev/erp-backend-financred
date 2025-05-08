package com.financred.financred.dto.request;

public record RegisterRequestDTO(String email,
                                 String nomeCompleto,
                                 String cpf,
                                 String senha) {}