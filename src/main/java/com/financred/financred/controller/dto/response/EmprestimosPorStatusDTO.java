package com.financred.financred.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprestimosPorStatusDTO {
    private String status;
    private Long quantidade;
}
