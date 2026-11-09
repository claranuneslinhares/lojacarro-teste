package br.edu.ifrn.contabil.messaging;

import br.edu.ifrn.contabil.dto.LogMensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQLogService {

    private static final String FILA = "dupla_maria_lucas";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitMQLogService(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void enviarLog(LogMensagemDTO mensagem) {

        try {
            String json = objectMapper.writeValueAsString(mensagem);

            rabbitTemplate.convertAndSend("", FILA, json);

            System.out.println("Log enviado para o RabbitMQ!");

        } catch (Exception e) {
            System.out.println("Não foi possível enviar o log: "
                    + e.getMessage());
        }
    }
}