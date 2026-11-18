package br.org.edu.ifrn.LojaCarro.dto;

import br.org.edu.ifrn.LojaCarro.model.SituacaoCarro;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CarroRequest(
        @NotBlank @Size(max = 80) String marca,
        @NotBlank @Size(max = 120) String modelo,
        @NotNull @Min(1886) @Max(2100) Integer anoFabricacao,
        @NotNull @Min(1886) @Max(2100) Integer anoModelo,
        @NotBlank @Size(max = 50) String cor,
        @NotNull @PositiveOrZero Integer quilometragem,
        @NotBlank @Size(max = 50) String combustivel,
        @NotBlank @Size(max = 50) String transmissao,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal preco,
        @NotBlank @Size(max = 2000) String descricao,
        SituacaoCarro situacao
) {
}