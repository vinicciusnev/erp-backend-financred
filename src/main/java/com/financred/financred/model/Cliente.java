package com.financred.financred.model;

import com.financred.financred.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cliente", schema = "financred")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nome_completo")
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(name="data_nascimento")
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "renda_mensal", precision = 10, scale = 2)
    private BigDecimal rendaMensal;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Emprestimo> emprestimos;

    @Min(0)
    @Max(1000)
    @Column(nullable = false)
    private Long score;
}