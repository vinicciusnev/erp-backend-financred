package com.financred.financred.model;

import com.financred.financred.enums.StatusEmprestimo;
import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "emprestimo", schema = "financred")
public class
Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_solicitado", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorSolicitado;

    @Column(name = "total_com_juros", precision = 12, scale = 2)
    private BigDecimal totalComJuros;

    @Column(name = "valor_juros", precision = 10, scale = 4)
    private BigDecimal valorJuros;

    @Column(name = "taxa_multa_atraso", precision = 10, scale = 4)
    private BigDecimal taxaMultaAtraso;

    @Column(name = "taxa_juros", precision = 10, scale = 4)
    private BigDecimal taxaJuros;

    @Column(name = "numero_parcelas")
    private Integer numeroParcelas;

    @Column(name = "status_emprestimo", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo statusEmprestimo;

    @Column(name = "tipo_emprestimo", length = 50)
    private String tipoEmprestimo;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "data_aprovacao")
    private LocalDate dataAprovacao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "aprovado_por", length = 100)
    private String aprovadoPor;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL)
    private List<EmprestimoParcelas> parcelas;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}