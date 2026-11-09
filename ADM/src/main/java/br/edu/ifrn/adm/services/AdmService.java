package br.edu.ifrn.adm.services;

import br.edu.ifrn.adm.dto.MensagemAdmDTO;
import br.edu.ifrn.adm.messaging.AdmProducer;
import br.edu.ifrn.adm.model.EventoAdm;
import br.edu.ifrn.adm.repository.EventoAdmRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdmService {

    private final EventoAdmRepository repository;
    private final AdmProducer producer;

    public AdmService(
            EventoAdmRepository repository,
            AdmProducer producer) {

        this.repository = repository;
        this.producer = producer;
    }

    public EventoAdm registrarEvento(MensagemAdmDTO dto) {

        EventoAdm evento = new EventoAdm();

        evento.setModuloOrigem(dto.getModuloOrigem());
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setMensagem(dto.getMensagem());
        evento.setDataRecebimento(LocalDateTime.now());

        return repository.save(evento);
    }

    public List<EventoAdm> listarEventos() {
        return repository.findAll();
    }

    public EventoAdm buscarEvento(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Evento não encontrado."));
    }

    public void enviarMensagem(String modulo, String mensagem) {

        producer.enviarMensagem(modulo, mensagem);
    }
}