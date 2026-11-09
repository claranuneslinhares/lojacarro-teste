package br.edu.ifrn.adm.messaging;

import br.edu.ifrn.adm.config.RabbitMQConfig;
import br.edu.ifrn.adm.dto.MensagemAdmDTO;
import br.edu.ifrn.adm.services.AdmService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AdmConsumer {

    private final ObjectMapper objectMapper;
    private final AdmService service;

    public AdmConsumer(
            ObjectMapper objectMapper,
            AdmService service) {

        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_ADM)
    public void receberMensagem(String mensagem) {

        try {
            MensagemAdmDTO dto = objectMapper.readValue(
                    mensagem,
                    MensagemAdmDTO.class
            );

            service.registrarEvento(dto);

            System.out.println(
                    "Mensagem recebida pelo ADM: " + mensagem);

        } catch (Exception e) {
            System.out.println(
                    "Erro ao processar mensagem no ADM: "
                            + e.getMessage());

            throw new RuntimeException(e);
        }
    }
}