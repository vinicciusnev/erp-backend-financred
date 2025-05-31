package com.financred.financred.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private long totalClientes;
    private long totalEmprestimos;
    private long totalEmprestimosAtivos;
    private BigDecimal valorTotalEmprestimos;
    private BigDecimal valorTotalEmprestimosAtrasados;
    private List<EmprestimosPorStatusDTO> emprestimosPorStatus;
    private List<EmprestimosPorMesDTO> emprestimosPorMes;
}



