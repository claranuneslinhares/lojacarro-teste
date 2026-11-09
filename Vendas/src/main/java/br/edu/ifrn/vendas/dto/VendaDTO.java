
package br.edu.ifrn.vendas.dto;

import java.math.BigDecimal;

public class VendaDTO {

    private Long carroId;
    private Long usuarioId;
    private BigDecimal valor;

    public VendaDTO() {
    }

    public Long getCarroId() {
        return carroId;
    }

    public void setCarroId(Long carroId) {
        this.carroId = carroId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
