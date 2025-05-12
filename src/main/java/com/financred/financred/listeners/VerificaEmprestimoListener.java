package com.financred.financred.listeners;

import com.financred.financred.dto.reponse.EmprestimoSolicitadoDTO;
import com.financred.financred.dto.request.EmprestimoRequestDTO;
import com.financred.financred.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VerificaEmprestimoListener {

    private final EmprestimoService emprestimoService;

    @RabbitListener(queues = "verificar-emprestimo.queue")
    public void processMessage(EmprestimoSolicitadoDTO dto) {
        emprestimoService.updateEmprestimo(dto.getId());
    }
}
