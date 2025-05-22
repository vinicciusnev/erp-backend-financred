package com.financred.financred.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "historico_pagamentos", schema = "financred")
public class HistoricoPagamentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcela_id")
    private EmprestimoParcelas parcela;

    @Column(name = "valor_pago", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @Column(name = "forma_pagamento", nullable = false, length = 50)
    private String formaPagamento;

    @Column(name = "status_pagamento", nullable = false, length = 50)
    private String statusPagamento;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.dataPagamento == null) {
            this.dataPagamento = this.createdAt;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
