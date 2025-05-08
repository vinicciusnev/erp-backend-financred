package com.financred.financred.util;


public class ParcelaUtil {

    // Método para calcular o valor das parcelas
    public static double calcularParcelas(double valorTotal, int quantidadeParcelas, double taxaJuros) {
        double jurosMensal = taxaJuros / 100;
        return valorTotal * (jurosMensal * Math.pow(1 + jurosMensal, quantidadeParcelas)) / (Math.pow(1 + jurosMensal, quantidadeParcelas) - 1);
    }
}