
package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.services.AutorizacaoService;
import br.org.edu.ifrn.LojaCarro.client.UsuarioClient;
import br.org.edu.ifrn.LojaCarro.services.LogService;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.dto.CarroRequest;
import br.org.edu.ifrn.LojaCarro.dto.CarroMensagemDTO;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import br.org.edu.ifrn.LojaCarro.enviar;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    // Converte os dados do carro para JSON e envia ao RabbitMQ
    private void enviarCarro(Carro carro) {
        try {
            CarroMensagemDTO dto = new CarroMensagemDTO(
                    carro.getId(),
                    carro.getModelo(),
                    carro.getAno(),
                    carro.getPreco()
            );

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(dto);

            enviar.enviarMensagem(json);

        } catch (Exception e) {
            System.err.println(
                    "Erro ao enviar carro para o RabbitMQ: "
                            + e.getMessage()
            );
        }
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

        enviarCarro(savedCarro);

        logService.registrar(
                usuario,
                "CADASTRO DE CARRO - ID=" + savedCarro.getId()
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

        enviarCarro(updatedCarro);

        logService.registrar(
                usuario,
                "ATUALIZAÇÃO DE CARRO - ID=" + id
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

        // Busca os dados antes da exclusão para poder enviá-los
        Optional<Carro> carroOptional = carroService.findById(id);

        if (carroOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Carro carro = carroOptional.get();

        carroService.deleteById(id);

        enviarCarro(carro);

        logService.registrar(
                usuario,
                "EXCLUSÃO DE CARRO - ID=" + id
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

        enviarCarro(carro);

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

        enviarCarro(updatedCarro);

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

    private double parsePreco(String preco) {
        try {
            return Double.parseDouble(preco);
        } catch (NumberFormatException ex) {
            throw new CarroException("Preço inválido: " + preco);
        }
    }
}
