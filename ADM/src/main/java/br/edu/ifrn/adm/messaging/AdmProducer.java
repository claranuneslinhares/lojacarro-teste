
package br.edu.ifrn.adm.messaging;

import br.edu.ifrn.adm.config.RabbitMQConfig;
import br.edu.ifrn.adm.dto.MensagemAdmDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdmProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AdmProducer(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void enviarMensagem(String modulo, String mensagem) {

        String fila = obterFila(modulo);

        try {
            MensagemAdmDTO dto = new MensagemAdmDTO();

            dto.setModuloOrigem("ADM");
            dto.setTipoEvento("COMANDO");
            dto.setMensagem(mensagem);

            String json = objectMapper.writeValueAsString(dto);

            rabbitTemplate.convertAndSend("", fila, json);

            System.out.println(
                    "Mensagem enviada pelo ADM para " + modulo
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao enviar mensagem para " + modulo, e
            );
        }
    }

    private String obterFila(String modulo) {

        return switch (modulo.toLowerCase()) {

            case "carro" -> RabbitMQConfig.FILA_CARRO;
            case "usuario" -> RabbitMQConfig.FILA_USUARIO;
            case "vendas" -> RabbitMQConfig.FILA_VENDAS;
            case "testdrive" -> RabbitMQConfig.FILA_TESTDRIVE;
            case "contabil" -> RabbitMQConfig.FILA_CONTABIL;

            default -> throw new IllegalArgumentException(
                    "Módulo não reconhecido: " + modulo
            );
        };
    }
}
