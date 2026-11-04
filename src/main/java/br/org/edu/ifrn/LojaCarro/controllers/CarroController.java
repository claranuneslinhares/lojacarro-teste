
package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.client.UsuarioClient;
import br.org.edu.ifrn.LojaCarro.dto.CarroRequest;
import br.org.edu.ifrn.LojaCarro.dto.LogMensagemDTO;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.services.AutorizacaoService;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import br.org.edu.ifrn.LojaCarro.services.LogService;
import br.org.edu.ifrn.LojaCarro.services.RabbitMQLogService;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carro")
@CrossOrigin(origins = "*")
public class CarroController {

    @Autowired
    private CarroService carroService;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private AutorizacaoService autorizacaoService;

    @Autowired
    private LogService logService;

    @Autowired
    private RabbitMQLogService rabbitMQLogService;

    // Monta e envia uma mensagem de log para o RabbitMQ
    private void enviarLogCarro(
            Carro carro,
            Long usuarioId,
            String operacao) {

        Map<String, Object> dados = new HashMap<>();

        dados.put("modelo", carro.getModelo());
        dados.put("ano", carro.getAno());
        dados.put("preco", carro.getPreco());

        LogMensagemDTO mensagem = new LogMensagemDTO(
                "CARRO",
                operacao,
                "Carro",
                carro.getId(),
                usuarioId,
                Instant.now().toString(),
                operacao + " de carro - ID=" + carro.getId(),
                dados
        );

        rabbitMQLogService.enviarLog(mensagem);
    }

    // Salvar carro
    @PostMapping("/salvar")
    public ResponseEntity<Carro> salvarCarro(
            @RequestBody CarroRequest request) {

        Usuario usuario = autorizacaoService.verificarPermissao(
                request.getUsuarioId(),
                "CADASTRAR"
        );

        Carro carro = new Carro();
        carro.setModelo(request.getModelo());
        carro.setAno(request.getAno());
        carro.setPreco(request.getPreco());

        Carro savedCarro = carroService.save(carro);

        enviarLogCarro(
                savedCarro,
                request.getUsuarioId(),
                "CADASTRO"
        );

        return ResponseEntity.ok(savedCarro);
    }

    // Atualizar carro por ID
    @PutMapping("/{id}")
    public ResponseEntity<Carro> atualizarCarro(
            @PathVariable Long id,
            @RequestBody CarroRequest request) {

        Usuario usuario = autorizacaoService.verificarPermissao(
                request.getUsuarioId(),
                "ATUALIZAR"
        );

        Carro carro = new Carro();
        carro.setId(id);
        carro.setModelo(request.getModelo());
        carro.setAno(request.getAno());
        carro.setPreco(request.getPreco());

        Carro updatedCarro = carroService.update(carro);

        enviarLogCarro(
                updatedCarro,
                request.getUsuarioId(),
                "ATUALIZACAO"
        );

        return ResponseEntity.ok(updatedCarro);
    }

    // Deletar carro por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCarro(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = autorizacaoService.verificarPermissao(
                usuarioId,
                "EXCLUIR"
        );

        Optional<Carro> carroOptional = carroService.findById(id);

        if (carroOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Carro carro = carroOptional.get();

        carroService.deleteById(id);

        enviarLogCarro(
                carro,
                usuarioId,
                "EXCLUSAO"
        );

        return ResponseEntity.noContent().build();
    }

    // Pesquisar carro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Carro> pesquisarCarroPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = autorizacaoService.verificarPermissao(
                usuarioId,
                "LISTAR"
        );

        Optional<Carro> carro = carroService.findById(id);

        if (carro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        logService.registrar(
                usuario,
                "CONSULTA DE CARRO - ID=" + id
        );

        return ResponseEntity.ok(carro.get());
    }

    // Testar comunicação com o serviço de usuários
    @GetMapping("/testeUsuario/{id}")
    public ResponseEntity<Usuario> testeUsuario(
            @PathVariable Long id) {

        Usuario usuario = usuarioClient.buscarUsuario(id);

        return ResponseEntity.ok(usuario);
    }

    // Listar todos os carros
    @GetMapping("/listarCarros")
    public ResponseEntity<List<Carro>> pesquisarTodosCarros(
            @RequestParam Long usuarioId) {

        Usuario usuario = autorizacaoService.verificarPermissao(
                usuarioId,
                "LISTAR"
        );

        List<Carro> carros = carroService.findAll();

        logService.registrar(
                usuario,
                "LISTAGEM DE CARROS"
        );

        return ResponseEntity.ok(carros);
    }

    // Pesquisar carro pelo modelo
    @PostMapping(
            value = "/getCarro",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Carro> pesquisarCarroPorModelo(
            @RequestBody String modelo) {

        Optional<Carro> carro =
                carroService.findByModelo(modelo.trim());

        return carro.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Deletar carro pelo modelo
    @PostMapping(
            value = "/deleteCarro",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> deletarCarroPorModelo(
            @RequestBody String modelo) {

        Carro carro = carroService.deleteByModelo(modelo.trim());

        enviarLogCarro(
                carro,
                null,
                "EXCLUSAO"
        );

        return ResponseEntity.ok(
                "Carro deletado: " + carro.getModelo()
        );
    }

    // Atualizar preço pelo modelo
    @PostMapping(
            value = "/updateCarro",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Carro> atualizarCarroPorModelo(
            @RequestBody String payload) {

        String[] partes = payload.split(",", 2);

        if (partes.length < 2) {
            throw new CarroException(
                    "Payload inválido. Use modelo,preco."
            );
        }

        String modelo = partes[0].trim();
        double preco = parsePreco(partes[1].trim());

        Carro updatedCarro =
                carroService.updateByModelo(modelo, preco);

        enviarLogCarro(
                updatedCarro,
                null,
                "ATUALIZACAO"
        );

        return ResponseEntity.ok(updatedCarro);
    }

    // Teste simples
    @PostMapping(
            value = "/teste",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> bomDia(
            @RequestBody String nome) {

        return ResponseEntity.ok("Bom dia, " + nome.trim());
    }

    // Converter preço para número
    private double parsePreco(String preco) {
        try {
            return Double.parseDouble(preco);
        } catch (NumberFormatException ex) {
            throw new CarroException("Preço inválido: " + preco);
        }
    }
}
