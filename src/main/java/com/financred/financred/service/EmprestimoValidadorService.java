package com.financred.financred.service;

import com.financred.financred.dto.reponse.ClienteContextDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class EmprestimoValidadorService {

    public Optional<String> validar(ClienteContextDTO ctx) {
        Map<Predicate<ClienteContextDTO>, String> regras = Map.of(
                c -> c.cliente().getScore() < 500, "Cliente com score inferior a 500.",
                c -> c.valorParcela().compareTo(c.comprometimentoMaximo()) > 0, "Valor da parcela excede 30% da renda mensal.",
                ClienteContextDTO::hasEmAberto, "Cliente possui empréstimo aprovado em aberto."
        );

        return regras.entrySet().stream()
                .filter(entry -> entry.getKey().test(ctx))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}