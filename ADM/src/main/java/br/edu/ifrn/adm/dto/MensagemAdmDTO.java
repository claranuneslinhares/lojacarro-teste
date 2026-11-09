package br.edu.ifrn.adm.dto;

public class MensagemAdmDTO {

    private String moduloOrigem;
    private String tipoEvento;
    private String mensagem;

    public MensagemAdmDTO() {
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
}