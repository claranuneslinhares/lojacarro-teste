
package br.edu.ifrn.vendas.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class CarroClient {

    private final RestClient restClient;

    public CarroClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    public boolean existeCarro(Long carroId, Long usuarioId) {

        if (carroId == null || usuarioId == null) {
            return false;
        }

        try {
            restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/carro/{id}")
                            .queryParam("usuarioId", usuarioId)
                            .build(carroId))
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (RestClientResponseException e) {

            if (e.getStatusCode().is4xxClientError()) {
                return false;
            }

            throw e;
        }
    }
}
