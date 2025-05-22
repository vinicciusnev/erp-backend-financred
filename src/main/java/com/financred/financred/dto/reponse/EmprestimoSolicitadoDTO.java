package com.financred.financred.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprestimoSolicitadoDTO {
    private Long id;
    private LocalDate dataInicio;
}
