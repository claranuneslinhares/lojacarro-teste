
package br.edu.ifrn.vendas.services;
import br.edu.ifrn.vendas.messaging.AdmMessageProducer;
import br.edu.ifrn.vendas.client.CarroClient;
import br.edu.ifrn.vendas.client.UsuarioClient;
import br.edu.ifrn.vendas.dto.LogMensagemDTO;
import br.edu.ifrn.vendas.dto.VendaDTO;
import br.edu.ifrn.vendas.messaging.RabbitMQLogService;
import br.edu.ifrn.vendas.model.Venda;
import br.edu.ifrn.vendas.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VendaService {

    private final VendaRepository repository;
    private final CarroClient carroClient;
    private final UsuarioClient usuarioClient;
    private final RabbitMQLogService logService;
    private final AdmMessageProducer admProducer;

    public VendaService(
            VendaRepository repository,
            CarroClient carroClient,
            UsuarioClient usuarioClient,
            RabbitMQLogService logService,
            AdmMessageProducer admProducer) {

        this.repository = repository;
        this.carroClient = carroClient;
        this.usuarioClient = usuarioClient;
        this.logService = logService;
        this.admProducer = admProducer;
    }

    public Venda cadastrar(VendaDTO dto) {
        validar(dto);

        Venda venda = new Venda();
        venda.setCarroId(dto.getCarroId());
        venda.setUsuarioId(dto.getUsuarioId());
        venda.setValor(dto.getValor());
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatus("CONCLUIDA");

        Venda salva = repository.save(venda);

        admProducer.notificarAdm(
            "CADASTRO",
            "Venda cadastrada. ID: " + salva.getId()
        );

        enviarLog("CADASTRO", salva);

        return salva;
    }

    public List<Venda> listarTodas() {
        return repository.findAll();
    }

    public Venda buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Venda não encontrada."));
    }

    public Venda atualizar(Long id, VendaDTO dto) {
        validar(dto);

        Venda venda = buscarPorId(id);

        if ("CANCELADA".equalsIgnoreCase(venda.getStatus())) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar uma venda cancelada.");
        }

        venda.setCarroId(dto.getCarroId());
        venda.setUsuarioId(dto.getUsuarioId());
        venda.setValor(dto.getValor());

        Venda atualizada = repository.save(venda);

        admProducer.notificarAdm(
            "ATUALIZACAO",
            "Venda atualizada. ID: " + atualizada.getId()
        );

        enviarLog("ATUALIZACAO", atualizada);

        return atualizada;
    }

    public Venda cancelar(Long id) {
        Venda venda = buscarPorId(id);

        venda.setStatus("CANCELADA");

        Venda cancelada = repository.save(venda);
            admProducer.notificarAdm(
        "CANCELAMENTO",
        "Venda cancelada. ID: " + cancelada.getId()
);
        enviarLog("CANCELAMENTO", cancelada);

        return cancelada;
    }

    public void excluir(Long id) {
        Venda venda = buscarPorId(id);

        repository.delete(venda);

        enviarLog("EXCLUSAO", venda);
        admProducer.notificarAdm(
        "EXCLUSAO",
        "Venda excluída. ID: " + id
);
    }

    private void validar(VendaDTO dto) {

        if (dto == null ||
                dto.getCarroId() == null ||
                dto.getUsuarioId() == null) {

            throw new IllegalArgumentException(
                    "Carro e usuário são obrigatórios.");
        }

        if (dto.getValor() == null ||
                dto.getValor().signum() <= 0) {

            throw new IllegalArgumentException(
                    "O valor da venda deve ser maior que zero.");
        }

        if (!usuarioClient.existeUsuario(dto.getUsuarioId())) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado.");
        }

        if (!carroClient.existeCarro(
                dto.getCarroId(), dto.getUsuarioId())) {

            throw new IllegalArgumentException(
                    "Carro não encontrado ou usuário sem permissão.");
        }
    }

    private void enviarLog(String operacao, Venda venda) {

        Map<String, Object> dados = new HashMap<>();

        dados.put("carroId", venda.getCarroId());
        dados.put("usuarioId", venda.getUsuarioId());
        dados.put("valor", venda.getValor());
        dados.put("status", venda.getStatus());

        LogMensagemDTO mensagem = new LogMensagemDTO(
                "Vendas",
                operacao,
                "Venda",
                venda.getId(),
                venda.getUsuarioId(),
                LocalDateTime.now().toString(),
                "Operação " + operacao + " realizada na venda.",
                dados
        );

        logService.enviarLog(mensagem);
    }
}
