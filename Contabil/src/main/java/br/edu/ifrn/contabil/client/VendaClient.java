package br.edu.ifrn.contabil.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class VendaClient {

    private final RestClient restClient;

    public VendaClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public boolean existeVenda(Long id) {

        try {
            restClient.get()
                    .uri("/vendas/{id}", id)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}