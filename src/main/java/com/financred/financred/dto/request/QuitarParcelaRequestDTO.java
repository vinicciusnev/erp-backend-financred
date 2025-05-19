package com.financred.financred.dto.request;


import java.time.LocalDate;

public record QuitarParcelaRequestDTO(LocalDate dataVencimento, Long idEmprestimo) { }
