package br.edu.ifrn.contabil.services;

import br.edu.ifrn.contabil.client.VendaClient;
import br.edu.ifrn.contabil.dto.LancamentoDTO;
import br.edu.ifrn.contabil.dto.LogMensagemDTO;
import br.edu.ifrn.contabil.messaging.AdmMessageProducer;
import br.edu.ifrn.contabil.messaging.RabbitMQLogService;
import br.edu.ifrn.contabil.model.Lancamento;
import br.edu.ifrn.contabil.repository.LancamentoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LancamentoService {

    private final LancamentoRepository repository;
    private final VendaClient vendaClient;
    private final RabbitMQLogService logService;
    private final AdmMessageProducer admProducer;

    public LancamentoService(
            LancamentoRepository repository,
            VendaClient vendaClient,
            RabbitMQLogService logService,
            AdmMessageProducer admProducer) {

        this.repository = repository;
        this.vendaClient = vendaClient;
        this.logService = logService;
        this.admProducer = admProducer;
    }

    public Lancamento cadastrar(LancamentoDTO dto) {

        validar(dto);

        Lancamento lancamento = new Lancamento();

        lancamento.setVendaId(dto.getVendaId());
        lancamento.setTipo(dto.getTipo().toUpperCase());
        lancamento.setValor(dto.getValor());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setDataLancamento(LocalDateTime.now());

        Lancamento salvo = repository.save(lancamento);

        admProducer.notificarAdm(
            "CADASTRO",
            "Lançamento contábil cadastrado. ID: " + salvo.getId()
        );

        enviarLog("CADASTRO", salvo);

        return salvo;
    }

    public List<Lancamento> listarTodos() {
        return repository.findAll();
    }

    public Lancamento buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Lançamento não encontrado."));
    }

    public Lancamento atualizar(Long id, LancamentoDTO dto) {

        validar(dto);

        Lancamento lancamento = buscarPorId(id);

        lancamento.setVendaId(dto.getVendaId());
        lancamento.setTipo(dto.getTipo().toUpperCase());
        lancamento.setValor(dto.getValor());
        lancamento.setDescricao(dto.getDescricao());

        Lancamento atualizado = repository.save(lancamento);

        admProducer.notificarAdm(
            "ATUALIZACAO",
            "Lançamento contábil atualizado. ID: " + atualizado.getId()
        );

        enviarLog("ATUALIZACAO", atualizado);

        return atualizado;
    }

    public void excluir(Long id) {

        Lancamento lancamento = buscarPorId(id);

        repository.delete(lancamento);

        admProducer.notificarAdm(
            "EXCLUSAO",
            "Lançamento contábil excluído. ID: " + id
        );

        enviarLog("EXCLUSAO", lancamento);
    }

    private void validar(LancamentoDTO dto) {

        if (dto.getTipo() == null ||
                (!dto.getTipo().equalsIgnoreCase("ENTRADA")
                        && !dto.getTipo().equalsIgnoreCase("SAIDA"))) {

            throw new IllegalArgumentException(
                    "O tipo deve ser ENTRADA ou SAIDA.");
        }

        if (dto.getValor() == null ||
                dto.getValor().signum() <= 0) {

            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero.");
        }

        if (dto.getVendaId() != null &&
                !vendaClient.existeVenda(dto.getVendaId())) {

            throw new IllegalArgumentException("Venda não encontrada.");
        }
    }

    private void enviarLog(String operacao, Lancamento lancamento) {

        Map<String, Object> dados = new HashMap<>();

        dados.put("vendaId", lancamento.getVendaId());
        dados.put("tipo", lancamento.getTipo());
        dados.put("valor", lancamento.getValor());
        dados.put("descricao", lancamento.getDescricao());

        LogMensagemDTO mensagem = new LogMensagemDTO(
                "Contabil",
                operacao,
                "Lancamento",
                lancamento.getId(),
                null,
                LocalDateTime.now().toString(),
                "Operação " + operacao + " realizada no lançamento contábil.",
                dados
        );

        logService.enviarLog(mensagem);
    }
}