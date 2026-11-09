package br.edu.ifrn.contabil.dto;

import java.util.Map;

public class LogMensagemDTO {

    private String modulo;
    private String operacao;
    private String entidade;
    private Long entidadeId;
    private Long usuarioId;
    private String dataHora;
    private String descricao;
    private Map<String, Object> dados;

    public LogMensagemDTO() {
    }

    public LogMensagemDTO(
            String modulo,
            String operacao,
            String entidade,
            Long entidadeId,
            Long usuarioId,
            String dataHora,
            String descricao,
            Map<String, Object> dados) {

        this.modulo = modulo;
        this.operacao = operacao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.usuarioId = usuarioId;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.dados = dados;
    }

    public String getModulo() {
        return modulo;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getEntidade() {
        return entidade;
    }

    public Long getEntidadeId() {
        return entidadeId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public Map<String, Object> getDados() {
        return dados;
    }
}