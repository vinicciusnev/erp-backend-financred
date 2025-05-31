package com.financred.financred.service.mappers;

import com.financred.financred.controller.dto.request.ClienteRequestDTO;
import com.financred.financred.controller.dto.request.ContaBancariaDTO;
import com.financred.financred.controller.dto.request.DadosFinanceirosDTO;
import com.financred.financred.controller.dto.request.EnderecoDTO;
import com.financred.financred.model.Cliente;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClienteMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClienteFromDto(ClienteRequestDTO dto, @MappingTarget Cliente cliente);

    @Mapping(target = "rua", source = "rua")
    @Mapping(target = "numero", source = "numero")
    @Mapping(target = "complemento", source = "complemento")
    @Mapping(target = "bairro", source = "bairro")
    @Mapping(target = "cidade", source = "cidade")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "cep", source = "cep")
    void updateEnderecoFromDto(EnderecoDTO dto, @MappingTarget Cliente cliente);

    @Mapping(target = "rendaMensal", source = "renda")
    @Mapping(target = "profissao", source = "profissao")
    @Mapping(target = "empresa", source = "empresa")
    void updateDadosFinanceirosFromDto(DadosFinanceirosDTO dto, @MappingTarget Cliente cliente);

    @Mapping(target = "banco", source = "banco")
    @Mapping(target = "agencia", source = "agencia")
    @Mapping(target = "conta", source = "conta")
    void updateContaBancariaFromDto(ContaBancariaDTO dto, @MappingTarget Cliente cliente);

    @AfterMapping
    default void mapNestedFields(ClienteRequestDTO dto, @MappingTarget Cliente cliente) {
        if (dto.getEndereco() != null) {
            updateEnderecoFromDto(dto.getEndereco(), cliente);
        }

        if (dto.getDadosFinanceiros() != null) {
            updateDadosFinanceirosFromDto(dto.getDadosFinanceiros(), cliente);

            if (dto.getDadosFinanceiros().getContaBancaria() != null) {
                updateContaBancariaFromDto(dto.getDadosFinanceiros().getContaBancaria(), cliente);
            }
        }
    }
}

