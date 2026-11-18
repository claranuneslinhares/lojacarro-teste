package br.org.edu.ifrn.LojaCarro.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service
public class FotoCarroService {

    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final long MAX_PIXELS = 24_000_000L;

    private final Path diretorio;

    public FotoCarroService(@Value("${app.fotos.diretorio:IMG}") String diretorio) {
        this.diretorio = Path.of(diretorio).toAbsolutePath().normalize();
    }

    public String salvar(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie a fotografia principal.");
        }

        if (foto.getSize() > MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "A foto deve ter até 5 MB.");
        }

        String extensao = validarImagem(foto);
        String nome = UUID.randomUUID() + "." + extensao;

        try (InputStream entrada = foto.getInputStream()) {
            Files.createDirectories(diretorio);
            Files.copy(entrada, diretorio.resolve(nome));
            return nome;
        } catch (IOException erro) {
            excluirAposFalha(nome, erro);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gravar a fotografia.", erro
            );
        }
    }

    public Foto carregada(String nome) {
        if (nome == null || !nome.matches("[0-9a-f-]{36}\\.(jpg|png|webp)")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada.");
        }

        Path caminho = diretorio.resolve(nome);
        if (!Files.isRegularFile(caminho)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada.");
        }

        MediaType tipo = switch (nome.substring(nome.lastIndexOf('.') + 1)) {
            case "jpg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            default -> MediaType.parseMediaType("image/webp");
        };

        return new Foto(new FileSystemResource(caminho), tipo);
    }

    void excluirAposFalha(String nome, Throwable erroOriginal) {
        if (nome == null) {
            return;
        }

        try {
            Files.deleteIfExists(diretorio.resolve(nome));
        } catch (IOException erro) {
            erroOriginal.addSuppressed(erro);
        }
    }

    private String validarImagem(MultipartFile foto) {
        try (InputStream entrada = foto.getInputStream();
             ImageInputStream imagem = ImageIO.createImageInputStream(entrada)) {

            if (imagem == null) {
                throw imagemInvalida();
            }

            Iterator<ImageReader> leitores = ImageIO.getImageReaders(imagem);
            if (!leitores.hasNext()) {
                throw imagemInvalida();
            }

            ImageReader leitor = leitores.next();
            try {
                leitor.setInput(imagem);
                String extensao = switch (leitor.getFormatName().toLowerCase(Locale.ROOT)) {
                    case "jpeg", "jpg" -> "jpg";
                    case "png" -> "png";
                    case "webp" -> "webp";
                    default -> throw imagemInvalida();
                };

                long pixels = (long) leitor.getWidth(0) * leitor.getHeight(0);
                if (pixels <= 0 || pixels > MAX_PIXELS || leitor.read(0) == null) {
                    throw imagemInvalida();
                }

                return extensao;
            } finally {
                leitor.dispose();
            }
        } catch (IOException erro) {
            throw imagemInvalida();
        }
    }

    private ResponseStatusException imagemInvalida() {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Envie uma imagem JPG, JPEG, PNG ou WebP válida."
        );
    }

    public record Foto(Resource recurso, MediaType tipo) {
    }
}