package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.dto.LogMensagemDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQLogService {

    private static final String FILA = "dupla_maria_lucas";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;

    public RabbitMQLogService(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = mapper;
    }

    public void enviarLog(LogMensagemDTO mensagem) {
        try {
            rabbitTemplate.convertAndSend("", FILA, mapper.writeValueAsString(mensagem));
        } catch (JsonProcessingException erro) {
            throw new IllegalStateException("Falha ao serializar o registro de auditoria.", erro);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ConfiguracaoFila {

        @Bean
        Queue filaAuditoriaLojaCarro() {
            return new Queue(FILA, true);
        }
    }
}