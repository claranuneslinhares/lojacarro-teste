package br.edu.ifrn.usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import br.edu.ifrn.gerenciausuarios.config.AdmRabbitConfig;

@SpringBootApplication
@Import(AdmRabbitConfig.class)
public class UsuarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsuarioApplication.class, args);
    }

}
