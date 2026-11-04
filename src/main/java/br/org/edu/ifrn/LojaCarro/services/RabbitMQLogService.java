
package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.dto.LogMensagemDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class RabbitMQLogService {

    private static final String HOST = "15.228.215.36";
    private static final int PORTA = 5672;
    private static final String USUARIO = "aluno";
    private static final String SENHA = "12345";
    private static final String FILA = "dupla_maria_lucas";

    @Autowired
    private ObjectMapper objectMapper;

    public void enviarLog(LogMensagemDTO mensagem) {

        try {
            String json = objectMapper.writeValueAsString(mensagem);

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORTA);
            factory.setUsername(USUARIO);
            factory.setPassword(SENHA);

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(
                        FILA, true, false, false, null
                );

                channel.basicPublish(
                        "",
                        FILA,
                        null,
                        json.getBytes(StandardCharsets.UTF_8)
                );

                System.out.println("LOG ENVIADO: " + json);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao enviar log para o RabbitMQ", e
            );
        }
    }
}
