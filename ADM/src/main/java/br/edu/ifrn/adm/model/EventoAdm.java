package br.edu.ifrn.adm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_adm")
public class EventoAdm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moduloOrigem;

    private String tipoEvento;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    private LocalDateTime dataRecebimento;

    public EventoAdm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuloOrigem() {
        return moduloOrigem;
    }

    public void setModuloOrigem(String moduloOrigem) {
        this.moduloOrigem = moduloOrigem;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(LocalDateTime dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }
}