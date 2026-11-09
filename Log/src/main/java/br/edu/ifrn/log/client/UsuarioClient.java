package br.edu.ifrn.log.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public String buscarNome(Long id) {

        try {
            Map<String, Object> usuario = restClient.get()
                    .uri("/usuario/{id}", id)
                    .retrieve()
                    .body(Map.class);

            if (usuario != null && usuario.get("nome") != null) {
                return usuario.get("nome").toString();
            }

        } catch (Exception e) {
            System.err.println(
                    "Não foi possível consultar o usuário: "
                            + e.getMessage()
            );
        }

        return "Usuário ID " + id;
    }
}