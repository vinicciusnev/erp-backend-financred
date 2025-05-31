package com.financred.financred.controller.dto.request;

public record RegisterRequestDTO(String email,
                                 String nomeCompleto,
                                 String cpf,
                                 String senha) {}