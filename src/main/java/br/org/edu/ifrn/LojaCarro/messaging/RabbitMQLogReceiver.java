
package br.org.edu.ifrn.LojaCarro.messaging;

import br.org.edu.ifrn.LojaCarro.client.UsuarioClient;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.services.LogService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@ConditionalOnProperty(
    name = "app.log.legacy-receiver.enabled",
    havingValue = "true"
)
@Component
public class RabbitMQLogReceiver {

    private static final String HOST = "54.20.84.206";
    private static final int PORTA = 5672;
    private static final String USUARIO = "aluno";
    private static final String SENHA = "12345";
    private static final String FILA = "dupla_maria_lucas";

    private Connection connection;
    private Channel channel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private LogService logService;

    @Autowired
    private UsuarioClient usuarioClient;

    // Inicia o recebimento quando a aplicação Spring Boot iniciar
    @PostConstruct
    public void iniciarRecebimento() {

        try {
            ConnectionFactory factory = new ConnectionFactory();

            factory.setHost(HOST);
            factory.setPort(PORTA);
            factory.setUsername(USUARIO);
            factory.setPassword(SENHA);

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(FILA, true, false, false, null);
            channel.basicQos(1);

            DeliverCallback callback = (consumerTag, delivery) -> {

                String mensagem = new String(
                        delivery.getBody(),
                        StandardCharsets.UTF_8
                );

                try {
                    JsonNode json = objectMapper.readTree(mensagem);

                    String modulo = json.path("modulo").asText();
                    String operacao = json.path("operacao").asText();
                    String entidade = json.path("entidade").asText();
                    long entidadeId = json.path("entidadeId").asLong();
                    long usuarioId = json.path("usuarioId").asLong();
                    String dataHora = json.path("dataHora").asText();
                    String descricao = json.path("descricao").asText();

                    // Busca o usuário responsável pela operação
                    Usuario usuario = usuarioClient.buscarUsuario(usuarioId);

                    if (usuario == null) {
                        throw new RuntimeException(
                                "Usuário não encontrado: " + usuarioId
                        );
                    }

                    // Registra o log no banco de dados
                    logService.registrar(usuario, descricao);

                    System.out.println("\n========== LOG RECEBIDO E SALVO ==========");
                    System.out.println("Módulo: " + modulo);
                    System.out.println("Operação: " + operacao);
                    System.out.println("Entidade: " + entidade);
                    System.out.println("ID: " + entidadeId);
                    System.out.println("Usuário: " + usuarioId);
                    System.out.println("Data e hora: " + dataHora);
                    System.out.println("Descrição: " + descricao);
                    System.out.println("Dados: " + json.path("dados"));
                    System.out.println("LOG REGISTRADO NO BANCO!");
                    System.out.println("==========================================\n");

                    // Confirma somente depois de salvar no banco
                    channel.basicAck(
                            delivery.getEnvelope().getDeliveryTag(),
                            false
                    );

                } catch (Exception e) {

                    System.err.println(
                            "Erro ao processar ou salvar log: " + e.getMessage()
                    );

                    // Recoloca a mensagem na fila para tentar novamente
                    channel.basicNack(
                            delivery.getEnvelope().getDeliveryTag(),
                            false,
                            true
                    );
                }
            };

            channel.basicConsume(FILA, false, callback, consumerTag -> {});

            System.out.println(
                    "RabbitMQ: aguardando mensagens na fila " + FILA
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Não foi possível iniciar o receptor do RabbitMQ", e
            );
        }
    }

    // Fecha a conexão quando a aplicação for encerrada
    @PreDestroy
    public void finalizarRecebimento() {

        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }

            if (connection != null && connection.isOpen()) {
                connection.close();
            }

            System.out.println("Conexão com RabbitMQ encerrada.");

        } catch (Exception e) {
            System.err.println(
                    "Erro ao encerrar conexão: " + e.getMessage()
            );
        }
    }
}
