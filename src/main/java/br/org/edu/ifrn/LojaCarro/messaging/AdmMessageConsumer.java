package br.org.edu.ifrn.LojaCarro.messaging;

import br.org.edu.ifrn.LojaCarro.config.AdmRabbitConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AdmMessageConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(AdmMessageConsumer.class);

    @RabbitListener(queues = AdmRabbitConfig.FILA_MODULO)
    public void receberMensagemDoAdm(String mensagem) {
        log.info("Mensagem recebida do ADM: {}", mensagem);
    }
}
