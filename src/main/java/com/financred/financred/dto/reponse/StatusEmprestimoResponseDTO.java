package com.financred.financred.dto.reponse;

import com.financred.financred.enums.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusEmprestimoResponseDTO {
    private Long idEmprestimo;
    private StatusEmprestimo statusEmprestimo;
    private String observacao;
}
