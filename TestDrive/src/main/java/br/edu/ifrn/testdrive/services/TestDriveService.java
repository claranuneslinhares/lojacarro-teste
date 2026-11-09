package br.edu.ifrn.testdrive.services;

import br.edu.ifrn.testdrive.client.CarroClient;
import br.edu.ifrn.testdrive.client.UsuarioClient;
import br.edu.ifrn.testdrive.dto.LogMensagemDTO;
import br.edu.ifrn.testdrive.dto.TestDriveDTO;
import br.edu.ifrn.testdrive.messaging.RabbitMQLogService;
import br.edu.ifrn.testdrive.model.TestDrive;
import br.edu.ifrn.testdrive.repository.TestDriveRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestDriveService {

    private final TestDriveRepository repository;
    private final CarroClient carroClient;
    private final UsuarioClient usuarioClient;
    private final RabbitMQLogService logService;

    public TestDriveService(
            TestDriveRepository repository,
            CarroClient carroClient,
            UsuarioClient usuarioClient,
            RabbitMQLogService logService) {

        this.repository = repository;
        this.carroClient = carroClient;
        this.usuarioClient = usuarioClient;
        this.logService = logService;
    }

    public TestDrive cadastrar(TestDriveDTO dto) {

        validar(dto);

        TestDrive testDrive = new TestDrive();

        testDrive.setCarroId(dto.getCarroId());
        testDrive.setUsuarioId(dto.getUsuarioId());
        testDrive.setDataHora(dto.getDataHora());
        testDrive.setObservacoes(dto.getObservacoes());
        testDrive.setStatus("AGENDADO");

        TestDrive salvo = repository.save(testDrive);

        enviarLog("CADASTRO", salvo);

        return salvo;
    }

    public List<TestDrive> listarTodos() {
        return repository.findAll();
    }

    public TestDrive buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Test drive não encontrado."));
    }

    public TestDrive atualizar(Long id, TestDriveDTO dto) {

        validar(dto);

        TestDrive testDrive = buscarPorId(id);

        if ("CANCELADO".equals(testDrive.getStatus())) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar um test drive cancelado.");
        }

        testDrive.setCarroId(dto.getCarroId());
        testDrive.setUsuarioId(dto.getUsuarioId());
        testDrive.setDataHora(dto.getDataHora());
        testDrive.setObservacoes(dto.getObservacoes());

        TestDrive atualizado = repository.save(testDrive);

        enviarLog("ATUALIZACAO", atualizado);

        return atualizado;
    }

    public TestDrive cancelar(Long id) {

        TestDrive testDrive = buscarPorId(id);

        testDrive.setStatus("CANCELADO");

        TestDrive cancelado = repository.save(testDrive);

        enviarLog("CANCELAMENTO", cancelado);

        return cancelado;
    }

    public void excluir(Long id) {

        TestDrive testDrive = buscarPorId(id);

        repository.delete(testDrive);

        enviarLog("EXCLUSAO", testDrive);
    }

    private void validar(TestDriveDTO dto) {

        if (dto.getCarroId() == null || dto.getUsuarioId() == null) {
            throw new IllegalArgumentException(
                    "Carro e usuário são obrigatórios.");
        }

        if (dto.getDataHora() == null ||
                !dto.getDataHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "A data do test drive deve ser futura.");
        }

        if (!carroClient.existeCarro(dto.getCarroId())) {
            throw new IllegalArgumentException("Carro não encontrado.");
        }

        if (!usuarioClient.existeUsuario(dto.getUsuarioId())) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
    }

    private void enviarLog(String operacao, TestDrive testDrive) {

        Map<String, Object> dados = new HashMap<>();

        dados.put("carroId", testDrive.getCarroId());
        dados.put("usuarioId", testDrive.getUsuarioId());
        dados.put("dataHora", testDrive.getDataHora());
        dados.put("status", testDrive.getStatus());

        LogMensagemDTO mensagem = new LogMensagemDTO(
                "TestDrive",
                operacao,
                "TestDrive",
                testDrive.getId(),
                testDrive.getUsuarioId(),
                LocalDateTime.now().toString(),
                "Operação " + operacao + " realizada no test drive.",
                dados
        );

        logService.enviarLog(mensagem);
    }
}