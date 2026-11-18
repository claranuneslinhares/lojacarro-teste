package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.dto.CarroRequest;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import br.org.edu.ifrn.LojaCarro.services.FotoCarroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carro")
public class CarroController {

    private final CarroService carros;
    private final FotoCarroService fotos;
    private final ObjectMapper mapper;
    private final Validator validator;

    public CarroController(
            CarroService carros,
            FotoCarroService fotos,
            ObjectMapper mapper,
            Validator validator
    ) {
        this.carros = carros;
        this.fotos = fotos;
        this.mapper = mapper;
        this.validator = validator;
    }

    @PostMapping(value = "/salvar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Carro cadastrar(
            @RequestParam("dados") String dados,
            @RequestPart("foto") MultipartFile foto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return carros.cadastrar(lerDados(dados), foto, Long.valueOf(jwt.getSubject()));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Carro atualizar(
            @PathVariable Long id,
            @RequestParam("dados") String dados,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return carros.atualizar(id, lerDados(dados), foto, Long.valueOf(jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        carros.desativar(id, Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/listarCarros")
    public List<Carro> listar(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax
    ) {
        return carros.listar(marca, modelo, ano, precoMin, precoMax);
    }

    @GetMapping("/gestao")
    public List<Carro> listarGestao() {
        return carros.listarGestao();
    }

    @GetMapping("/{id}")
    public Carro buscar(@PathVariable Long id) {
        return carros.buscarNoCatalogo(id);
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> buscarFoto(@PathVariable Long id) {
        Carro carro = carros.buscarNoCatalogo(id);
        FotoCarroService.Foto foto = fotos.carregada(carro.getArquivoFoto());

        return ResponseEntity.ok()
                .contentType(foto.tipo())
                .header("X-Content-Type-Options", "nosniff")
                .body(foto.recurso());
    }

    private CarroRequest lerDados(String json) {
        CarroRequest dados;
        try {
            dados = mapper.readValue(json, CarroRequest.class);
        } catch (JsonProcessingException erro) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O campo dados deve conter um JSON válido."
            );
        }

        if (dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe os dados do veículo."
            );
        }

        var erros = validator.validate(dados);
        if (!erros.isEmpty()) {
            String mensagem = erros.stream()
                    .map(erro -> erro.getPropertyPath() + ": " + erro.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }

        return dados;
    }
}