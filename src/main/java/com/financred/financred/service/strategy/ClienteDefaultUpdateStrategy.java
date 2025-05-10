package com.financred.financred.service.strategy;

import com.financred.financred.dto.request.ClienteRequestDTO;
import com.financred.financred.model.Cliente;

public class ClienteDefaultUpdateStrategy {

    public void updateClienteFromDto(Cliente cliente, ClienteRequestDTO dto) {
        if (dto.getNomeCompleto() != null) cliente.setNomeCompleto(dto.getNomeCompleto());
        if (dto.getCpf() != null) cliente.setCpf(dto.getCpf());
        if (dto.getDataNascimento() != null) cliente.setDataNascimento(dto.getDataNascimento());
        if (dto.getEmail() != null) cliente.setEmail(dto.getEmail());
        if (dto.getSenha() != null) cliente.setSenha(dto.getSenha());

        if (dto.getTelefone() != null) cliente.setTelefone(dto.getTelefone());

        if (dto.getEndereco() != null) {
            if (dto.getEndereco().getRua() != null) cliente.setRua(dto.getEndereco().getRua());
            if (dto.getEndereco().getNumero() != null) cliente.setNumero(dto.getEndereco().getNumero());
            if (dto.getEndereco().getComplemento() != null) cliente.setComplemento(dto.getEndereco().getComplemento());
            if (dto.getEndereco().getBairro() != null) cliente.setBairro(dto.getEndereco().getBairro());
            if (dto.getEndereco().getCidade() != null) cliente.setCidade(dto.getEndereco().getCidade());
            if (dto.getEndereco().getEstado() != null) cliente.setEstado(dto.getEndereco().getEstado());
            if (dto.getEndereco().getCep() != null) cliente.setCep(dto.getEndereco().getCep());
        }

        if (dto.getDadosFinanceiros() != null) {
            if (dto.getDadosFinanceiros().getRenda() != null) cliente.setRendaMensal(dto.getDadosFinanceiros().getRenda());
            if (dto.getDadosFinanceiros().getProfissao() != null) cliente.setProfissao(dto.getDadosFinanceiros().getProfissao());
            if (dto.getDadosFinanceiros().getEmpresa() != null) cliente.setEmpresa(dto.getDadosFinanceiros().getEmpresa());

            if (dto.getDadosFinanceiros().getContaBancaria() != null) {
                if (dto.getDadosFinanceiros().getContaBancaria().getBanco() != null)
                    cliente.setBanco(dto.getDadosFinanceiros().getContaBancaria().getBanco());
                if (dto.getDadosFinanceiros().getContaBancaria().getAgencia() != null)
                    cliente.setAgencia(dto.getDadosFinanceiros().getContaBancaria().getAgencia());
                if (dto.getDadosFinanceiros().getContaBancaria().getConta() != null)
                    cliente.setConta(dto.getDadosFinanceiros().getContaBancaria().getConta());
            }
        }
    }

}
