package br.edu.ifrn.log.messaging;

import br.edu.ifrn.log.client.UsuarioClient;
import br.edu.ifrn.log.dto.LogMensagemDTO;
import br.edu.ifrn.log.services.LogService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQLogReceiver {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int porta;

    @Value("${spring.rabbitmq.username}")
    private String usuario;

    @Value("${spring.rabbitmq.password}")
    private String senha;

    private static final String FILA = "dupla_maria_lucas";

    private final LogService logService;
    private final UsuarioClient usuarioClient;
    private final ObjectMapper objectMapper;

    private volatile boolean ativo = true;

    private Connection connection;
    private Channel channel;

    public RabbitMQLogReceiver(
            LogService logService,
            UsuarioClient usuarioClient,
            ObjectMapper objectMapper) {

        this.logService = logService;
        this.usuarioClient = usuarioClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void iniciarRecebimento() {

        Thread thread = new Thread(this::conectar);
        thread.setName("rabbitmq-log-receiver");
        thread.setDaemon(true);
        thread.start();
    }

    private void conectar() {

        while (ativo) {

            try {
                ConnectionFactory factory = new ConnectionFactory();

                factory.setHost(host);
                factory.setPort(porta);
                factory.setUsername(usuario);
                factory.setPassword(senha);
                factory.setConnectionTimeout(5000);

                Connection novaConexao = factory.newConnection();
                Channel novoCanal = novaConexao.createChannel();

                connection = novaConexao;
                channel = novoCanal;

                novoCanal.queueDeclare(
                        FILA,
                        true,
                        false,
                        false,
                        null
                );

                novoCanal.basicQos(1);

                System.out.println(
                        "Logs conectado ao RabbitMQ. Fila: " + FILA
                );

                novoCanal.basicConsume(
                        FILA,
                        false,
                        (consumerTag, delivery) -> {

                            String mensagem = new String(
                                    delivery.getBody(),
                                    StandardCharsets.UTF_8
                            );

                            try {
                                LogMensagemDTO dto =
                                        objectMapper.readValue(
                                                mensagem,
                                                LogMensagemDTO.class
                                        );

                                // Busca o nome do usuário, se necessário
                                if (dto.getUsuarioId() != null
                                        && (dto.getNomeUsuario() == null
                                        || dto.getNomeUsuario().isBlank())) {

                                    dto.setNomeUsuario(
                                            usuarioClient.buscarNome(
                                                    dto.getUsuarioId()
                                            )
                                    );
                                }

                                // Salva o registro no banco
                                logService.registrar(dto);

                                // Confirma o recebimento
                                novoCanal.basicAck(
                                        delivery.getEnvelope()
                                                .getDeliveryTag(),
                                        false
                                );

                                System.out.println(
                                        "LOG RECEBIDO E SALVO!"
                                );

                                System.out.println(
                                        "Módulo: " + dto.getModulo()
                                );

                                System.out.println(
                                        "Operação: " + dto.getOperacao()
                                );

                                System.out.println(
                                        "Entidade: " + dto.getEntidade()
                                );

                                System.out.println(
                                        "Usuário: " + dto.getUsuarioId()
                                );

                                System.out.println(
                                        "Nome: " + dto.getNomeUsuario()
                                );

                                System.out.println(
                                        "Descrição: " + dto.getDescricao()
                                );

                            } catch (Exception e) {

                                System.err.println(
                                        "Erro ao processar log: "
                                                + e.getMessage()
                                );

                                // Recoloca a mensagem na fila
                                novoCanal.basicNack(
                                        delivery.getEnvelope()
                                                .getDeliveryTag(),
                                        false,
                                        true
                                );
                            }
                        },
                        consumerTag -> {}
                );

                // Mantém a conexão enquanto estiver ativa
                while (ativo && novaConexao.isOpen()) {
                    Thread.sleep(1000);
                }

            } catch (Exception e) {

                if (ativo) {
                    System.err.println(
                            "RabbitMQ indisponível. "
                                    + "Nova tentativa em 10 segundos."
                    );

                    System.err.println(e.getMessage());
                }

            } finally {
                fecharConexao();
            }

            if (ativo) {
                try {
                    Thread.sleep(10000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void fecharConexao() {

        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }

            if (connection != null && connection.isOpen()) {
                connection.close();
            }

        } catch (Exception e) {
            System.err.println(
                    "Erro ao fechar conexão RabbitMQ: "
                            + e.getMessage()
            );
        }
    }

    @PreDestroy
    public void finalizarRecebimento() {

        ativo = false;
        fecharConexao();

        System.out.println("Receptor de logs encerrado.");
    }
}