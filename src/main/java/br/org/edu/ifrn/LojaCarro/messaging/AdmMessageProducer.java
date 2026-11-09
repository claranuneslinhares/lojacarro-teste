package br.org.edu.ifrn.LojaCarro.messaging;

import br.org.edu.ifrn.LojaCarro.config.AdmRabbitConfig;
import br.org.edu.ifrn.LojaCarro.dto.MensagemAdmDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdmMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AdmMessageProducer(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void notificarAdm(
            String tipoEvento,
            String mensagem) {

        try {
            MensagemAdmDTO dto = new MensagemAdmDTO(
                    "Carro",
                    tipoEvento,
                    mensagem
            );

            String json = objectMapper.writeValueAsString(dto);

            rabbitTemplate.convertAndSend(
                    "",
                    AdmRabbitConfig.FILA_ADM,
                    json
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao enviar mensagem para o ADM.", e
            );
        }
    }
}
