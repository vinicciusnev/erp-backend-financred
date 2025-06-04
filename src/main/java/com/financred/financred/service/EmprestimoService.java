package com.financred.financred.service;

import com.financred.financred.controller.dto.response.*;
import com.financred.financred.controller.dto.request.EmprestimoRequestDTO;
import com.financred.financred.controller.dto.request.QuitarEmprestimoRequestDTO;
import com.financred.financred.controller.dto.request.QuitarParcelaRequestDTO;
import com.financred.financred.enums.StatusEmprestimo;
import com.financred.financred.enums.StatusParcela;
import com.financred.financred.exception.AcessoNegadoException;
import com.financred.financred.exception.QuitacaoEmprestimoException;
import com.financred.financred.exception.ValidacaoEmprestimoException;
import com.financred.financred.model.Cliente;
import com.financred.financred.model.Emprestimo;
import com.financred.financred.model.EmprestimoParcelas;
import com.financred.financred.model.HistoricoPagamentos;
import com.financred.financred.repository.ClienteRepository;
import com.financred.financred.repository.EmprestimoParcelasRepository;
import com.financred.financred.repository.EmprestimoRepository;
import com.financred.financred.repository.HistoricoPagamentosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
    private final HistoricoPagamentosRepository historicoPagamentosRepository;
    private final EmprestimoValidadorService validador;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Qualifier("customRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    public void solicitaEmprestimo(EmprestimoRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Emprestimo emprestimo = Emprestimo.builder()
                .valorSolicitado(request.getValorSolicitado())
                .dataSolicitacao(LocalDate.now())
                .statusEmprestimo(StatusEmprestimo.SOLICITADO)
                .cliente(cliente)
                .numeroParcelas(request.getParcelas())
                .tipoEmprestimo(request.getTipoEmprestimo())
                .taxaJuros(new BigDecimal("0.04"))
                .observacao(request.getObservacao())
                .taxaMultaAtraso(new BigDecimal("0.02"))
                .build();

        emprestimoRepository.save(emprestimo);

        EmprestimoSolicitadoDTO dto = new EmprestimoSolicitadoDTO(emprestimo.getId(), request.getDataInicio());
        rabbitTemplate.convertAndSend("verificar-emprestimo.ex", "", dto);
    }

    @Transactional(noRollbackFor = ValidacaoEmprestimoException.class)
    public void updateEmprestimo(Long emprestimoId, LocalDate dataInicio) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

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

        ClienteContextDTO clienteContextDTO = new ClienteContextDTO(cliente, valorParcela, hasEmAberto, comprometimentoMaximo);

        Optional<String> erroValidacao = validador.validar(clienteContextDTO);

        if (erroValidacao.isPresent()) {
            emprestimo.setStatusEmprestimo(StatusEmprestimo.REPROVADO);
            emprestimo.setObservacao(erroValidacao.get());
            emprestimoRepository.save(emprestimo);

            StatusEmprestimoResponseDTO statusEmprestimoResponseDTO  = new StatusEmprestimoResponseDTO(
                    emprestimo.getId(),
                    emprestimo.getStatusEmprestimo(),
                    emprestimo.getObservacao()
            );
            messagingTemplate.convertAndSend("/topic/status-emprestimo/" + cliente.getId(), statusEmprestimoResponseDTO);

            throw new ValidacaoEmprestimoException(erroValidacao.get());
        }

        emprestimo.setStatusEmprestimo(StatusEmprestimo.APROVADO);
        emprestimo.setDataInicio(dataInicio);
        emprestimo.setDataFim(dataInicio.plusMonths(parcelas));
        emprestimo.setValorJuros(valorJuros);
        emprestimo.setTaxaJuros(taxaJuros);
        emprestimo.setTotalComJuros(totalComJuros);
        emprestimo.setDataAprovacao(LocalDate.now());
        emprestimo.setAprovadoPor("AUTOMATICO");

        List<EmprestimoParcelas> listaParcelas = IntStream.rangeClosed(1, parcelas)
                .mapToObj(i -> EmprestimoParcelas.builder()
                        .valorParcela(valorParcela)
                        .statusParcela(StatusParcela.PENDENTE)
                        .diasAtraso(0)
                        .numeroParcela(i)
                        .dataVencimento(dataInicio.plusMonths(i - 1))
                        .emprestimo(emprestimo)
                        .build())
                .collect(Collectors.toList());

        emprestimo.setParcelas(listaParcelas);
        emprestimoRepository.save(emprestimo);

        StatusEmprestimoResponseDTO dto = new StatusEmprestimoResponseDTO(
                emprestimo.getId(),
                emprestimo.getStatusEmprestimo(),
                emprestimo.getObservacao()
        );
        messagingTemplate.convertAndSend("/topic/status-emprestimo/" + cliente.getId(), dto);
    }

    public SimulacaoEmprestimoResponseDTO simularEmprestimo(EmprestimoRequestDTO request) {
        BigDecimal taxaJuros = new BigDecimal("0.04");
        BigDecimal valorParcela = calcularValorParcelaComJuros(request.getValorSolicitado(), request.getParcelas(), taxaJuros);
        BigDecimal totalComJuros = valorParcela.multiply(BigDecimal.valueOf(request.getParcelas()));
        BigDecimal valorJuros = totalComJuros.subtract(request.getValorSolicitado());

        return new SimulacaoEmprestimoResponseDTO(
                valorParcela,
                request.getParcelas(),
                totalComJuros,
                valorJuros,
                taxaJuros.multiply(new BigDecimal("100"))
        );
    }

    private BigDecimal calcularValorParcelaComJuros(BigDecimal valor, int parcelas, BigDecimal taxaMensal) {
        BigDecimal umMaisJuros = BigDecimal.ONE.add(taxaMensal);
        BigDecimal fator = umMaisJuros.pow(parcelas);
        return valor.multiply(taxaMensal).multiply(fator)
                .divide(fator.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_EVEN);
    }

    @Transactional
    public void quitarParcela(QuitarParcelaRequestDTO request) {
        EmprestimoParcelas parcela = emprestimoParcelasRepository.findByDataVencimentoAndEmprestimoId(request.dataVencimento(), request.idEmprestimo())
                .orElseThrow(() -> new EntityNotFoundException("Parcela não encontrada"));

        if (parcela.getStatusParcela() == StatusParcela.PAGA) {
            throw new QuitacaoEmprestimoException("A parcela já foi quitada anteriormente.");
        }

        parcela.setStatusParcela(StatusParcela.PAGA);
        parcela.setDataPagamento(LocalDate.now());

        emprestimoParcelasRepository.save(parcela);

        Emprestimo emprestimo = parcela.getEmprestimo();

        HistoricoPagamentos historicoPagamentos = HistoricoPagamentos.builder()
                .emprestimo(emprestimo)
                .parcela(parcela)
                .valorPago(parcela.getValorParcela())
                .formaPagamento("PIX")
                .statusPagamento("PAGO")
                .build();

        historicoPagamentosRepository.save(historicoPagamentos);
    }

    @Transactional
    public void quitarEmprestimo(QuitarEmprestimoRequestDTO dto) {
        Emprestimo emprestimo = emprestimoRepository.findById(dto.idEmprestimo())
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

        List<EmprestimoParcelas> parcelas = emprestimoParcelasRepository.findByEmprestimoId(dto.idEmprestimo())
                .orElseThrow(() -> new EntityNotFoundException("Parcela não encontrada"));

        boolean todasPagas = parcelas.stream()
                .allMatch(p -> p.getStatusParcela() == StatusParcela.PAGA);

        if (todasPagas) {
            throw new QuitacaoEmprestimoException("Este empréstimo já foi quitado. Todas as parcelas estão pagas.");
        }

        List<EmprestimoParcelas> atrasadas = parcelas.stream()
                .filter(p -> p.getStatusParcela() == StatusParcela.ATRASADA)
                .toList();

        List<EmprestimoParcelas> pendentes = parcelas.stream()
                .filter(p -> p.getStatusParcela() == StatusParcela.PENDENTE)
                .toList();

        List<EmprestimoParcelas> pagas = parcelas.stream()
                .filter(p -> p.getStatusParcela() == StatusParcela.PAGA)
                .toList();

        if (!atrasadas.isEmpty()) {
            throw new QuitacaoEmprestimoException("Há parcelas atrasadas para este empréstimo. Renegocie primeiro para prosseguir com a quitação do emprestimo");
        }

        if (pendentes.isEmpty()) {
            throw new EntityNotFoundException("Não há parcelas pendentes para este empréstimo.");
        }

        BigDecimal totalPago = pagas.stream()
                .map(EmprestimoParcelas::getValorParcela)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorRestante = emprestimo.getTotalComJuros().subtract(totalPago);

        HistoricoPagamentos historicoPagamentos = HistoricoPagamentos.builder()
                .emprestimo(emprestimo)
                .valorPago(valorRestante)
                .formaPagamento("PIX")
                .statusPagamento("PAGO")
                .observacao("Emprestimo quitado por completo")
                .build();

        historicoPagamentosRepository.save(historicoPagamentos);

        for (EmprestimoParcelas parcela : pendentes) {
            parcela.setStatusParcela(StatusParcela.PAGA);
            parcela.setDataPagamento(LocalDate.now());
        }

        emprestimo.setStatusEmprestimo(StatusEmprestimo.QUITADO);
        emprestimo.setDataFim(LocalDate.now());

        emprestimoRepository.save(emprestimo);
    }

    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoResponseDTO> listarPorCliente(Authentication authentication) {
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();
        Long idCliente = clienteAuth.getId();

        List<Emprestimo> emprestimos = emprestimoRepository.findByClienteId(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Não há registros de emprestimos para o cliente"));

        return emprestimos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoResponseDTO> listarPorCliente(Long id) {
        List<Emprestimo> emprestimos = emprestimoRepository.findByClienteId(id)
                .orElseThrow(() -> new EntityNotFoundException("Não há registros de emprestimos para o cliente"));

        return emprestimos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoParcelasResponseDTO> listarParcelasPorEmprestimoEUsuario(Long emprestimoId, Authentication authentication) {
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<EmprestimoParcelas> emprestimoParcelas = emprestimoParcelasRepository.findByEmprestimoId(emprestimoId)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

        if (!emprestimoParcelas.get(0).getEmprestimo().getCliente().getId().equals(clienteAuth.getId()) && !isAdmin) {
            throw new AcessoNegadoException("Usuário não autorizado");
        }

        return emprestimoParcelas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoResponseDTO> listarPorStatus(StatusEmprestimo status, Authentication authentication) {
        ClienteAuthDTO clienteAuth = (ClienteAuthDTO) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Emprestimo> emprestimos = emprestimoRepository.findByStatusEmprestimo(status)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

        if (!emprestimos.get(0).getCliente().getId().equals(clienteAuth.getId()) && !isAdmin) {
            throw new AcessoNegadoException("Usuário não autorizado");
        }

        return emprestimos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private EmprestimoResponseDTO toDTO(Emprestimo emprestimo) {
        return EmprestimoResponseDTO.builder()
                .id(emprestimo.getId().toString())
                .valorSolicitado(emprestimo.getValorSolicitado())
                .totalComJuros(emprestimo.getTotalComJuros())
                .valorJuros(emprestimo.getValorJuros())
                .taxaMultaAtraso(emprestimo.getTaxaMultaAtraso().multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN))
                .taxaJuros(emprestimo.getTaxaJuros().multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN))
                .numeroParcelas(emprestimo.getNumeroParcelas())
                .status(emprestimo.getStatusEmprestimo())
                .dataInicio(emprestimo.getDataInicio())
                .dataFim(emprestimo.getDataFim())
                .clienteId(emprestimo.getCliente().getId().toString())
                .emailCliente(emprestimo.getCliente().getEmail())
                .tipoEmprestimo(emprestimo.getTipoEmprestimo())
                .observacao(emprestimo.getObservacao())
                .dataSolicitacao(emprestimo.getDataSolicitacao())
                .dataAprovacao(emprestimo.getDataAprovacao())
                .parcelas(mapParcelas(emprestimo.getParcelas()))
                .build();
    }

    private EmprestimoParcelasResponseDTO toDTO(EmprestimoParcelas parcela) {
        return EmprestimoParcelasResponseDTO.builder()
                .id(parcela.getId().toString())
                .valorParcela(parcela.getValorParcela())
                .valorJuros(parcela.getValorJuros())
                .statusParcela(parcela.getStatusParcela())
                .diasAtraso(parcela.getDiasAtraso())
                .numeroParcela(parcela.getNumeroParcela())
                .multa(parcela.getMulta())
                .observacao(parcela.getObservacao())
                .emprestimoId(parcela.getEmprestimo().getId().toString())
                .dataVencimento(parcela.getDataVencimento())
                .dataPagamento(parcela.getDataPagamento())
                .createdAt(parcela.getCreatedAt())
                .updatedAt(parcela.getUpdatedAt())
                .build();
    }

    private List<ParcelasEmprestimosResponseDTO> mapParcelas(List<EmprestimoParcelas> parcelas) {
        return parcelas.stream().map(p -> new ParcelasEmprestimosResponseDTO(
                p.getId().toString(),
                p.getEmprestimo().getId().toString(),
                p.getNumeroParcela().toString(),
                p.getStatusParcela(),
                p.getDataVencimento(),
                p.getValorParcela(),
                p.getMulta() != null ? p.getMulta() : BigDecimal.valueOf(0.00)
        )).toList();
    }
}