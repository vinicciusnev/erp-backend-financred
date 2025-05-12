package com.financred.financred.service;


import com.financred.financred.dto.reponse.ClienteContext;
import com.financred.financred.dto.reponse.EmprestimoResponseDTO;
import com.financred.financred.dto.reponse.EmprestimoSolicitadoDTO;
import com.financred.financred.dto.request.EmprestimoRequestDTO;
import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.enums.StatusParcela;
import com.financred.financred.model.Cliente;
import com.financred.financred.model.Emprestimo;
import com.financred.financred.model.EmprestimoParcelas;
import com.financred.financred.repository.ClienteRepository;
import com.financred.financred.repository.EmprestimoParcelasRepository;
import com.financred.financred.repository.EmprestimoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoParcelasRepository emprestimoParcelasRepository;
    private final ClienteRepository clienteRepository;
    private final EmprestimoValidadorService validador;

    @Autowired
    @Qualifier("customRabbitTemplate")
    private RabbitTemplate rabbitTemplate;


    public void solicitaEmprestimo(EmprestimoRequestDTO request) {
        Cliente cliente = clienteRepository.findByCpf(request.getCliente().getCpf())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Emprestimo emprestimo = Emprestimo.builder()
                .valorSolicitado(request.getValorSolicitado())
                .dataSolicitacao(LocalDate.now())
                .statusEmprestimo(StatusEmprestimo.SOLICITADO)
                .cliente(cliente)
                .numeroParcelas(request.getParcelas())
                .tipoEmprestimo(request.getTipoEmprestimo())
                .taxaJuros(request.getJuros())
                .observacao(request.getObservacao())
                .taxaMultaAtraso(new BigDecimal("0.02")) // exemplo de valor
                .build();

        emprestimoRepository.save(emprestimo);

        EmprestimoSolicitadoDTO dto = new EmprestimoSolicitadoDTO(emprestimo.getId());
        rabbitTemplate.convertAndSend("verificar-emprestimo.ex", "", dto);
    }

    @Transactional
    public void updateEmprestimo(Long emprestimoId) {
        LocalDate hoje = LocalDate.now();

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        Cliente cliente = emprestimo.getCliente();
        BigDecimal valor = emprestimo.getValorSolicitado();
        int parcelas = emprestimo.getNumeroParcelas();
        BigDecimal renda = cliente.getRendaMensal();
        BigDecimal taxaJuros = new BigDecimal("0.04");

        BigDecimal valorParcela = calcularValorParcelaComJuros(valor, parcelas, taxaJuros);
        BigDecimal comprometimentoMaximo = renda.multiply(new BigDecimal("0.30"));
        BigDecimal totalComJuros = valorParcela.multiply(BigDecimal.valueOf(parcelas));
        BigDecimal valorJuros = totalComJuros.subtract(valor);

        boolean hasEmAberto = cliente.getEmprestimos().stream()
                .anyMatch(e -> e.getStatusEmprestimo().equals(StatusEmprestimo.APROVADO));

        ClienteContext ctx = new ClienteContext(cliente, valorParcela, hasEmAberto, comprometimentoMaximo);

        Optional<String> erroValidacao = validador.validar(ctx);
        if (erroValidacao.isPresent()) {
            emprestimo.setStatusEmprestimo(StatusEmprestimo.REPROVADO);
            emprestimo.setObservacao(erroValidacao.get());
            emprestimoRepository.save(emprestimo);

            throw new RuntimeException(erroValidacao.get());
        }

        emprestimo.setStatusEmprestimo(StatusEmprestimo.APROVADO);
        emprestimo.setDataInicio(hoje);
        emprestimo.setDataFim(hoje.plusMonths(parcelas));
        emprestimo.setValorJuros(valorJuros);
        emprestimo.setTaxaJuros(taxaJuros);
        emprestimo.setTotalComJuros(totalComJuros);
        emprestimo.setDataAprovacao(hoje);
        emprestimo.setAprovadoPor("AUTOMATICO");

        List<EmprestimoParcelas> listaParcelas = IntStream.rangeClosed(1, parcelas)
                .mapToObj(i -> EmprestimoParcelas.builder()
                        .valorParcela(valorParcela)
                        .statusParcela(StatusParcela.PENDENTE)
                        .diasAtraso(0)
                        .numeroParcela(i)
                        .dataVencimento(hoje.plusMonths(i))
                        .emprestimo(emprestimo)
                        .build())
                .collect(Collectors.toList());

        emprestimo.setParcelas(listaParcelas);

        emprestimoRepository.save(emprestimo);
    }

    private BigDecimal calcularValorParcelaComJuros(BigDecimal valor, int parcelas, BigDecimal taxaMensal) {
        BigDecimal umMaisJuros = BigDecimal.ONE.add(taxaMensal);
        BigDecimal fator = umMaisJuros.pow(parcelas);
        return valor.multiply(taxaMensal).multiply(fator)
                .divide(fator.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_EVEN);
    }

    @Transactional
    public void quitarParcela(LocalDate dataVencimento, Long idEmprestimo) {
        EmprestimoParcelas parcela = emprestimoParcelasRepository.findByDataVencimentoAndEmprestimoId(dataVencimento, idEmprestimo)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (parcela.getStatusParcela() == StatusParcela.PAGA) {
            throw new RuntimeException("A parcela já foi quitada.");
        }

        parcela.setStatusParcela(StatusParcela.PAGA);

        emprestimoParcelasRepository.save(parcela);
    }

    @Transactional
    public void quitarEmprestimo(Long idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        List<EmprestimoParcelas> emprestimoParcelas = emprestimoParcelasRepository.findByStatusParcelaAndEmprestimoId(StatusParcela.PENDENTE.toString(), idEmprestimo)
                .orElseThrow(() -> new RuntimeException("Parcelas pendentes não encontradas"));

        if (emprestimoParcelas.isEmpty()) {
            throw new RuntimeException("Não há parcelas pendentes para este empréstimo.");
        }

        for (EmprestimoParcelas parcela : emprestimoParcelas) {
            parcela.setStatusParcela(StatusParcela.PAGA);
            parcela.setDataPagamento(LocalDate.now());
        }

        emprestimo.setStatusEmprestimo(StatusEmprestimo.QUITADO);
        emprestimo.setDataFim(LocalDate.now());

        emprestimoRepository.save(emprestimo);
        emprestimoParcelasRepository.saveAll(emprestimoParcelas);
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
        return emprestimoRepository.findByStatusEmprestimo(status)
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
        emprestimo.setStatusEmprestimo(status);
        emprestimoRepository.save(emprestimo);
    }

    private EmprestimoResponseDTO toDTO(Emprestimo emprestimo) {
        return EmprestimoResponseDTO.builder()
                .id(emprestimo.getId())
                .valorSolicitado(emprestimo.getValorSolicitado())
                .totalComJuros(emprestimo.getTotalComJuros())
                .valorJuros(emprestimo.getValorJuros())
                .taxaMultaAtraso(emprestimo.getTaxaMultaAtraso())
                .taxaJuros(emprestimo.getTaxaJuros())
                .numeroParcelas(emprestimo.getNumeroParcelas())
                .status(emprestimo.getStatusEmprestimo())
                .dataInicio(emprestimo.getDataInicio())
                .dataFim(emprestimo.getDataFim())
                .clienteId(emprestimo.getCliente().getId())
                .tipoEmprestimo(emprestimo.getTipoEmprestimo())
                .observacao(emprestimo.getObservacao())
                .dataSolicitacao(emprestimo.getDataSolicitacao())
                .dataAprovacao(emprestimo.getDataAprovacao())
                .aprovadoPor(emprestimo.getAprovadoPor())
                .build();
    }
}