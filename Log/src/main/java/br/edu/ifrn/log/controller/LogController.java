package br.edu.ifrn.log.controller;

import br.edu.ifrn.log.client.UsuarioClient;
import br.edu.ifrn.log.dto.LogMensagemDTO;
import br.edu.ifrn.log.model.Log;
import br.edu.ifrn.log.repository.LogRepository;
import br.edu.ifrn.log.services.LogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@CrossOrigin(origins = "*")
public class LogController {

    private final LogRepository logRepository;
    private final LogService logService;
    private final UsuarioClient usuarioClient;

    public LogController(
            LogRepository logRepository,
            LogService logService,
            UsuarioClient usuarioClient) {

        this.logRepository = logRepository;
        this.logService = logService;
        this.usuarioClient = usuarioClient;
    }

    @GetMapping
    public ResponseEntity<List<Log>> listar() {

        return ResponseEntity.ok(
                logRepository.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Log> buscarPorId(@PathVariable Long id) {

        return logRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Log> registrar(
            @RequestBody LogMensagemDTO mensagem) {

        if (mensagem.getUsuarioId() != null
                && (mensagem.getNomeUsuario() == null
                || mensagem.getNomeUsuario().isBlank())) {

            mensagem.setNomeUsuario(
                    usuarioClient.buscarNome(mensagem.getUsuarioId())
            );
        }

        Log log = logService.registrar(mensagem);

        return ResponseEntity.ok(log);
    }
}