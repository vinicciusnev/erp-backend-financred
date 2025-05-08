package com.financred.financred.model;

import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.enums.StatusParcela;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "emprestimo_parcelas", schema = "financred")
public class EmprestimoParcelas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_parcela", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;

    @Column(name = "status_parcela", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusParcela status;

    @Column(name = "valor_parcela_com_juros", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcelaComJuros;

    @Column(name = "valor_juros", precision = 5, scale = 4)
    private BigDecimal juros;

    @Column(name = "dias_atraso")
    private Integer diasAtraso;
}