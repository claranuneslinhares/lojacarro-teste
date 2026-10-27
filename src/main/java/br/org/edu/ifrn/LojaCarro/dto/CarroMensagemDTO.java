
package br.org.edu.ifrn.LojaCarro.dto;

public class CarroMensagemDTO {

    private Long id;
    private String modelo;
    private Integer ano;
    private Double preco;
    private long timestamp;

    public CarroMensagemDTO(
            Long id,
            String modelo,
            Integer ano,
            Double preco) {

        this.id = id;
        this.modelo = modelo;
        this.ano = ano;
        this.preco = preco;
        this.timestamp = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public Double getPreco() {
        return preco;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
