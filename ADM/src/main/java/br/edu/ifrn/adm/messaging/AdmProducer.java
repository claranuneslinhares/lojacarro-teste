package br.edu.ifrn.adm.messaging;

import br.edu.ifrn.adm.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdmProducer {

    private final RabbitTemplate rabbitTemplate;

    public AdmProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarMensagem(String modulo, String mensagem) {

        String fila = obterFila(modulo);

        rabbitTemplate.convertAndSend(fila, mensagem);

        System.out.println(
                "Mensagem enviada pelo ADM para " + modulo);
    }

    private String obterFila(String modulo) {

        return switch (modulo.toLowerCase()) {

            case "carro" -> RabbitMQConfig.FILA_CARRO;

            case "usuario" -> RabbitMQConfig.FILA_USUARIO;

            case "vendas" -> RabbitMQConfig.FILA_VENDAS;

            case "testdrive" -> RabbitMQConfig.FILA_TESTDRIVE;

            case "contabil" -> RabbitMQConfig.FILA_CONTABIL;

            default -> throw new IllegalArgumentException(
                    "Módulo não reconhecido.");
        };
    }
}