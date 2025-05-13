package com.financred.financred.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ClienteAuthDTO {
    private final Long id;
    private final String email;
    private final String nome;
}

