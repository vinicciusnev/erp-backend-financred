package com.financred.financred.listeners;

import com.financred.financred.config.RabbitConfiguration;
import com.financred.financred.controller.dto.response.EmprestimoSolicitadoDTO;
import com.financred.financred.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VerificaEmprestimoListener {

    private final EmprestimoService emprestimoService;

    @RabbitListener(queues = RabbitConfiguration.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void processMessage(EmprestimoSolicitadoDTO dto) {
        emprestimoService.updateEmprestimo(dto.getId(), dto.getDataInicio());
    }
}
