package com.financred.financred.controller.dto.request;


import java.time.LocalDate;

public record QuitarParcelaRequestDTO(Long idEmprestimo, LocalDate dataVencimento) { }
