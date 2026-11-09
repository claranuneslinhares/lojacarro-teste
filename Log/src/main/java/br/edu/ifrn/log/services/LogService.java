
package br.edu.ifrn.log.services;

import br.edu.ifrn.log.dto.LogMensagemDTO;
import br.edu.ifrn.log.model.Log;
import br.edu.ifrn.log.repository.LogRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Instant;
import java.time.ZoneId;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    public LogService(
            LogRepository logRepository,
            ObjectMapper objectMapper) {

        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    public Log registrar(LogMensagemDTO mensagem) {

        Log log = new Log();

        log.setModulo(mensagem.getModulo());
        log.setOperacao(mensagem.getOperacao());
        log.setEntidade(mensagem.getEntidade());
        log.setEntidadeId(mensagem.getEntidadeId());
        log.setUsuarioId(mensagem.getUsuarioId());
        log.setNome(mensagem.getNomeUsuario());

        log.setDescricao(mensagem.getDescricao());

        log.setAcao(
                mensagem.getDescricao() != null
                        ? mensagem.getDescricao()
                        : mensagem.getOperacao()
        );

        LocalDateTime dataHora = converterData(mensagem.getDataHora());

        log.setTimestamp(dataHora);
        log.setData(dataHora.toLocalDate());

        try {
            if (mensagem.getDados() != null) {
                log.setDados(
                        objectMapper.writeValueAsString(
                                mensagem.getDados()
                        )
                );
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Erro ao converter os dados do log", e
            );
        }

        return logRepository.save(log);
    }

    private LocalDateTime converterData(String valor) {

        if (valor == null || valor.isBlank()) {
            return LocalDateTime.now();
        }

        try {
            return LocalDateTime.parse(valor);

        } catch (Exception e) {
            try {
                return OffsetDateTime.parse(valor)
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();

            } catch (Exception ex) {
                try {
                    return Instant.parse(valor)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                } catch (Exception erro) {
                    return LocalDateTime.now();
                }
            }
        }
    }
}
