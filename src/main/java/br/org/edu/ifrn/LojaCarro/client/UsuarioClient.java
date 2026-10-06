package br.org.edu.ifrn.LojaCarro.client;

import br.org.edu.ifrn.LojaCarro.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public Usuario buscarUsuario(Long id) {

        return restClient.get()
                .uri("/usuario/{id}", id)
                .retrieve()
                .body(Usuario.class);
    }
}