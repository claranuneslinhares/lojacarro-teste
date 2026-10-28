
package br.org.edu.ifrn.LojaCarro;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class enviar {
    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();

        String json = "{"
                + "\"id\": 20,"
                + "\"modelo\": \"Civic\","
                + "\"ano\": 2010,"
                + "\"preco\": 25000.0,"
                + "\"timestamp\": " + timestamp
                + "}";

        enviarMensagem(json);
    }

    private static final String HOST = "10.209.1.81";
    private static final int PORTA = 5672;
    private static final String USUARIO = "admin";
    private static final String SENHA = "admin";
    private static final String FILA = "dupla_maria_lucas";

    public static void enviarMensagem(String json) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORTA);
        factory.setUsername(USUARIO);
        factory.setPassword(SENHA);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(FILA, true, false, false, null);

            channel.basicPublish(
                    "",
                    FILA,
                    null,
                    json.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("JSON enviado: " + json);

        } catch (Exception e) {
            System.err.println(
                    "Erro ao enviar JSON para o RabbitMQ: "
                            + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}
