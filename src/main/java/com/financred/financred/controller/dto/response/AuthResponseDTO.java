package com.financred.financred.controller.dto.response;

public record AuthResponseDTO(String email, String token, String role, Long idCliente) {}
