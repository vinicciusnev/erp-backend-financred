package com.financred.financred.model;

import com.financred.financred.enums.StatusParcela;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "emprestimo_parcelas", schema = "financred")
public class EmprestimoParcelas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_parcela", precision = 10, scale = 4, nullable = false)
    private BigDecimal valorParcela;

    @Column(name = "valor_juros", precision = 10, scale = 4)
    private BigDecimal valorJuros;

    @Column(name = "status_parcela", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusParcela statusParcela;

    @Column(name = "dias_atraso")
    private Integer diasAtraso;

    @Column(name = "numero_parcela")
    private Integer numeroParcela;

    @Column(name = "multa", precision = 10, scale = 4)
    private BigDecimal multa;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}