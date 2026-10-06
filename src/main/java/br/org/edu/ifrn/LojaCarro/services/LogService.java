package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.Log;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.repository.LogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger logger =
            LogManager.getLogger(LogService.class);

    @Autowired
    private LogRepository logRepository;

    public void registrar(Usuario usuario, String acao) {

        Log log = new Log(
                usuario.getId(),
                usuario.getNome(),
                acao
        );

        logRepository.save(log);

        logger.info(
                "ID_LOG={} | ID_USUARIO={} | NOME={} | TIMESTAMP={} | DATA={} | ACAO={}",
                log.getId(),
                log.getUsuarioId(),
                log.getNome(),
                log.getTimestamp(),
                log.getData(),
                log.getAcao()
        );
    }
}