
package br.edu.ifrn.testdrive.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public boolean existeUsuario(Long id) {

        if (id == null) {
            return false;
        }

        try {
            restClient.get()
                    .uri("/usuario/{id}", id)
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
