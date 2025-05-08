package com.financred.financred.model;

import com.financred.financred.enums.StatusEmprestimo;
import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "emprestimo", schema = "financred")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_solicitado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "status_emprestimo", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "numero_parcelas")
    private Integer numeroParcelas;

    @Column(name = "total_com_juros", precision = 12, scale = 2)
    private BigDecimal totalComJuros;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Column(name = "valor_juros", precision = 5, scale = 4)
    private BigDecimal juros;

    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL)
    private List<EmprestimoParcelas> parcelas;
}