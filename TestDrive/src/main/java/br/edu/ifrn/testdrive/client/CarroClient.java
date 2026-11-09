package br.edu.ifrn.testdrive.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CarroClient {

    private final RestClient restClient;

    public CarroClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    public boolean existeCarro(Long id) {
        try {
            restClient.get()
                    .uri("/carro/{id}", id)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}