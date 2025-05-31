package com.financred.financred.service;

import com.financred.financred.controller.dto.response.ClienteAuthDTO;
import com.financred.financred.controller.dto.response.DashboardStatsDTO;
import com.financred.financred.controller.dto.response.EmprestimosPorMesDTO;
import com.financred.financred.controller.dto.response.EmprestimosPorStatusDTO;
import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.enums.StatusParcela;
import com.financred.financred.model.Emprestimo;
import com.financred.financred.model.EmprestimoParcelas;
import com.financred.financred.repository.ClienteRepository;
import com.financred.financred.repository.EmprestimoParcelasRepository;
import com.financred.financred.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoParcelasRepository emprestimoParcelasRepository;

    public DashboardStatsDTO getDashboardStats(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<Emprestimo> emprestimos;
        List<EmprestimoParcelas> emprestimoParcelas;
        long totalClientes = 0;

        if (isAdmin) {
            emprestimos = emprestimoRepository.findByStatusEmprestimoNot(StatusEmprestimo.REPROVADO);
            emprestimoParcelas = emprestimoParcelasRepository.findByStatusParcela(StatusParcela.ATRASADA);
            totalClientes = clienteRepository.count();
        } else {
            ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();
            Long idCliente = clienteAuth.getId();

            emprestimos = emprestimoRepository
                    .findByClienteIdAndStatusEmprestimoNot(idCliente, StatusEmprestimo.REPROVADO)
                    .orElse(Collections.emptyList());

            emprestimoParcelas = emprestimoParcelasRepository.findByStatusParcelaAndEmprestimo_Cliente_Id(StatusParcela.ATRASADA, idCliente)
                    .orElse(Collections.emptyList());
        }

        long totalEmprestimos = emprestimos.size();
        long totalAtivos = emprestimos.stream()
                .filter(e -> e.getStatusEmprestimo() == StatusEmprestimo.APROVADO)
                .count();

        BigDecimal totalValor = emprestimos.stream()
                .map(Emprestimo::getTotalComJuros)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<EmprestimosPorStatusDTO> emprestimosPorStatus = emprestimos.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStatusEmprestimo().toString(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new EmprestimosPorStatusDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        List<EmprestimosPorMesDTO> emprestimosPorMes = emprestimos.stream()
                .filter(e -> e.getDataAprovacao() != null)
                .collect(Collectors.groupingBy(
                        e -> traduzirMes(e.getDataAprovacao().getMonth()),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Emprestimo::getTotalComJuros,
                                BigDecimal::add
                        )
                ))
                .entrySet().stream()
                .map(entry -> new EmprestimosPorMesDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        BigDecimal valorTotalEmprestimosAtrasados = emprestimoParcelas.stream()
                .map(EmprestimoParcelas::getValorParcela)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardStatsDTO(
                totalClientes,
                totalEmprestimos,
                totalAtivos,
                totalValor,
                valorTotalEmprestimosAtrasados,
                emprestimosPorStatus,
                emprestimosPorMes
        );
    }

    private String traduzirMes(Month mes) {
        return mes.getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
    }
}
