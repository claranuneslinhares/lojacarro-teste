package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.dto.CarroRequest;
import br.org.edu.ifrn.LojaCarro.dto.LogMensagemDTO;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.model.SituacaoCarro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class CarroService {

    private static final Logger log = LoggerFactory.getLogger(CarroService.class);

    private final CarroRepository repository;
    private final FotoCarroService fotos;
    private final RabbitMQLogService auditoria;

    public CarroService(
            CarroRepository repository,
            FotoCarroService fotos,
            RabbitMQLogService auditoria
    ) {
        this.repository = repository;
        this.fotos = fotos;
        this.auditoria = auditoria;
    }

    public Carro cadastrar(CarroRequest dados, MultipartFile foto, Long usuarioId) {
        validarAnos(dados);
        String arquivo = fotos.salvar(foto);
        return persistir(new Carro(dados, arquivo), arquivo, "CADASTRO", usuarioId);
    }

    public Carro atualizar(Long id, CarroRequest dados, MultipartFile foto, Long usuarioId) {
        validarAnos(dados);
        Carro carro = buscarParaGestao(id);
        String arquivo = foto == null || foto.isEmpty() ? null : fotos.salvar(foto);

        if (arquivo == null && carro.getArquivoFoto() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Inclua uma foto para atualizar este veículo."
            );
        }

        carro.atualizar(dados, arquivo);
        return persistir(carro, arquivo, "ATUALIZACAO", usuarioId);
    }

    public void desativar(Long id, Long usuarioId) {
        Carro carro = buscarParaGestao(id);
        carro.desativar();
        persistir(carro, null, "EXCLUSAO_LOGICA", usuarioId);
    }

    public List<Carro> listarGestao() {
        return repository.findAll();
    }

    public Carro buscarParaGestao(Long id) {
        return repository.findById(id).orElseThrow(this::naoEncontrado);
    }

    public Carro buscarNoCatalogo(Long id) {
        return repository.findByIdAndSituacaoAndArquivoFotoIsNotNull(id, SituacaoCarro.DISPONIVEL)
                .orElseThrow(this::naoEncontrado);
    }

    public List<Carro> listar(
            String marca, String modelo, Integer ano, BigDecimal precoMin, BigDecimal precoMax
    ) {
        if (precoMin != null && precoMax != null && precoMin.compareTo(precoMax) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O preço mínimo deve ser menor ou igual ao máximo."
            );
        }

        return repository.buscarCatalogo(
                SituacaoCarro.DISPONIVEL, limpar(marca), limpar(modelo), ano, precoMin, precoMax
        );
    }

    private Carro persistir(Carro carro, String arquivoNovo, String operacao, Long usuarioId) {
        Carro salvo;
        try {
            salvo = repository.saveAndFlush(carro);
        } catch (RuntimeException erro) {
            fotos.excluirAposFalha(arquivoNovo, erro);
            throw erro;
        }

        try {
            auditoria.enviarLog(new LogMensagemDTO(
                    "CARRO", operacao, "Carro", salvo.getId(), usuarioId,
                    Instant.now().toString(), operacao + " de veículo",
                    Map.of("modelo", salvo.getModelo(), "situacao", salvo.getSituacao().name())
            ));
        } catch (AmqpException | IllegalStateException erro) {
            log.warn("Veículo {} gravado. Falha ao publicar auditoria de {}.",
                    salvo.getId(), operacao, erro);
        }

        return salvo;
    }

    private void validarAnos(CarroRequest dados) {
        int fabricacao = dados.anoFabricacao();
        int modelo = dados.anoModelo();

        if (modelo < fabricacao || modelo > fabricacao + 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O ano do modelo deve ser o de fabricação ou o seguinte."
            );
        }
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado.");
    }
}