package br.edu.ifrn.adm.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_ADM = "fila_adm";

    public static final String FILA_CARRO = "fila_carro";
    public static final String FILA_USUARIO = "fila_usuario";
    public static final String FILA_VENDAS = "fila_vendas";
    public static final String FILA_TESTDRIVE = "fila_testdrive";
    public static final String FILA_CONTABIL = "fila_contabil";

    @Bean
    public Queue filaAdm() {
        return new Queue(FILA_ADM, true);
    }

    @Bean
    public Queue filaCarro() {
        return new Queue(FILA_CARRO, true);
    }

    @Bean
    public Queue filaUsuario() {
        return new Queue(FILA_USUARIO, true);
    }

    @Bean
    public Queue filaVendas() {
        return new Queue(FILA_VENDAS, true);
    }

    @Bean
    public Queue filaTestDrive() {
        return new Queue(FILA_TESTDRIVE, true);
    }

    @Bean
    public Queue filaContabil() {
        return new Queue(FILA_CONTABIL, true);
    }
}