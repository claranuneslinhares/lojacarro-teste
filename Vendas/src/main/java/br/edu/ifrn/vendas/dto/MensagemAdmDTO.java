package br.edu.ifrn.vendas.dto;

public class MensagemAdmDTO {

    private String moduloOrigem;
    private String tipoEvento;
    private String mensagem;

    public MensagemAdmDTO() {
    }

    public MensagemAdmDTO(
            String moduloOrigem,
            String tipoEvento,
            String mensagem) {

        this.moduloOrigem = moduloOrigem;
        this.tipoEvento = tipoEvento;
        this.mensagem = mensagem;
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
