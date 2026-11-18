package br.org.edu.ifrn.LojaCarro.model;

import br.org.edu.ifrn.LojaCarro.dto.CarroRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carro")
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80)
    private String marca;

    @Column(length = 120)
    private String modelo;

    private Integer anoFabricacao;

    @Column(name = "ano")
    private Integer anoModelo;

    @Column(length = 50)
    private String cor;

    private Integer quilometragem;

    @Column(length = 50)
    private String combustivel;

    @Column(length = 50)
    private String transmissao;

    @Column(precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(length = 2000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SituacaoCarro situacao;

    @JsonIgnore
    @Column(length = 100)
    private String arquivoFoto;

    protected Carro() {
    }

    public Carro(CarroRequest dados, String arquivoFoto) {
        this.situacao = SituacaoCarro.DISPONIVEL;
        atualizar(dados, arquivoFoto);
    }

    public void atualizar(CarroRequest dados, String novaFoto) {
        marca = dados.marca().strip();
        modelo = dados.modelo().strip();
        anoFabricacao = dados.anoFabricacao();
        anoModelo = dados.anoModelo();
        cor = dados.cor().strip();
        quilometragem = dados.quilometragem();
        combustivel = dados.combustivel().strip();
        transmissao = dados.transmissao().strip();
        preco = dados.preco();
        descricao = dados.descricao().strip();

        if (dados.situacao() != null) {
            situacao = dados.situacao();
        } else if (situacao == null) {
            situacao = SituacaoCarro.DISPONIVEL;
        }

        if (novaFoto != null) {
            arquivoFoto = novaFoto;
        }
    }

    public void desativar() {
        situacao = SituacaoCarro.INATIVO;
    }

    public Long getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getAnoFabricacao() { return anoFabricacao; }
    public Integer getAnoModelo() { return anoModelo; }
    public Integer getAno() { return anoModelo; }
    public String getCor() { return cor; }
    public Integer getQuilometragem() { return quilometragem; }
    public String getCombustivel() { return combustivel; }
    public String getTransmissao() { return transmissao; }
    public BigDecimal getPreco() { return preco; }
    public String getDescricao() { return descricao; }
    public SituacaoCarro getSituacao() { return situacao; }

    @JsonIgnore
    public String getArquivoFoto() {
        return arquivoFoto;
    }

    public String getFotoUrl() {
        return arquivoFoto == null ? null : "/carro/" + id + "/foto";
    }
}