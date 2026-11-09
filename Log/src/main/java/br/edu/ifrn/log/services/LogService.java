package br.edu.ifrn.log.services;

import br.edu.ifrn.log.dto.LogMensagemDTO;
import br.edu.ifrn.log.model.Log;
import br.edu.ifrn.log.repository.LogRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

        // Mantém a ação registrada anteriormente
        log.setAcao(
                mensagem.getDescricao() != null
                        ? mensagem.getDescricao()
                        : mensagem.getOperacao()
        );

        // Recupera a data e hora da mensagem
        LocalDateTime dataHora;

        try {
            dataHora = mensagem.getDataHora() != null
                    ? LocalDateTime.parse(mensagem.getDataHora())
                    : LocalDateTime.now();

        } catch (Exception e) {
            dataHora = LocalDateTime.now();
        }

        log.setTimestamp(dataHora);
        log.setData(dataHora.toLocalDate());

        // Converte os dados adicionais para JSON
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
}