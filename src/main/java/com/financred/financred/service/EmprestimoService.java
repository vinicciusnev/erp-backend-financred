package com.financred.financred.service;


import com.financred.financred.dto.reponse.EmprestimoResponseDTO;
import com.financred.financred.dto.request.EmprestimoRequestDTO;
import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.model.Cliente;
import com.financred.financred.model.Emprestimo;
import com.financred.financred.repository.ClienteRepository;
import com.financred.financred.repository.EmprestimoParcelasRepository;
import com.financred.financred.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoParcelasRepository emprestimoParcelasRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    @Qualifier("customRabbitTemplate")
    private RabbitTemplate rabbitTemplate;


    public void solicitaEmprestimo(EmprestimoRequestDTO request) {
        Cliente cliente = clienteRepository.findByCpf(request.getCliente().getCpf())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Emprestimo emprestimo = Emprestimo.builder()
                .valorSolicitado(request.getValorSolicitado())
                .dataSolicitacao(LocalDate.now())
                .status(StatusEmprestimo.SOLICITADO)
                .cliente(cliente)
                .numeroParcelas(request.getParcelas())
                .build();

        emprestimoRepository.save(emprestimo);

        rabbitTemplate.convertAndSend("verificar-emprestimo.ex", "", request);
    }

    public void updateEmprestimo(EmprestimoRequestDTO request) {
        Emprestimo emprestimo = new Emprestimo();
//        emprestimo.setCliente(request.getCliente());
//        emprestimoRepository.save(request);

    }

    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoResponseDTO> listarPorCliente(Long idCliente) {
        return emprestimoRepository.findByClienteId(idCliente)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoResponseDTO> listarPorStatus(StatusEmprestimo status) {
        return emprestimoRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoResponseDTO> listarPorDataSolicitacao(LocalDate inicio, LocalDate fim) {
        return emprestimoRepository.findByDataSolicitacaoBetween(inicio, fim)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void atualizarStatus(Emprestimo emprestimo, StatusEmprestimo status) {
        emprestimo.setStatus(status);
        emprestimoRepository.save(emprestimo);
    }

    private EmprestimoResponseDTO toDTO(Emprestimo emprestimo) {
        return EmprestimoResponseDTO.builder()
                .id(emprestimo.getId())
                .valorSolicitado(emprestimo.getValorSolicitado())
                .dataSolicitacao(emprestimo.getDataSolicitacao())
                .status(emprestimo.getStatus())
                .dataInicio(emprestimo.getDataInicio())
                .dataFim(emprestimo.getDataFim())
                .juros(emprestimo.getJuros())
                .clienteId(emprestimo.getCliente().getId())
                .build();
    }
}