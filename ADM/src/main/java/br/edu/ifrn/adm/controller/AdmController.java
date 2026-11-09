package br.edu.ifrn.adm.controller;

import br.edu.ifrn.adm.dto.MensagemAdmDTO;
import br.edu.ifrn.adm.model.EventoAdm;
import br.edu.ifrn.adm.services.AdmService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adm")
@CrossOrigin("*")
public class AdmController {

    private final AdmService service;

    public AdmController(AdmService service) {
        this.service = service;
    }

    @PostMapping("/mensagem/{modulo}")
    public String enviarMensagem(
            @PathVariable String modulo,
            @RequestBody Map<String, String> corpo) {

        String mensagem = corpo.get("mensagem");

        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException(
                    "A mensagem não pode ser vazia.");
        }

        service.enviarMensagem(modulo, mensagem);

        return "Mensagem enviada para o módulo " + modulo;
    }

    @GetMapping("/eventos")
    public List<EventoAdm> listarEventos() {
        return service.listarEventos();
    }

    @GetMapping("/eventos/{id}")
    public EventoAdm buscarEvento(@PathVariable Long id) {
        return service.buscarEvento(id);
    }
}