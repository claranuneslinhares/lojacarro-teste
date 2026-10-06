package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.client.UsuarioClient;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService {

    @Autowired
    private UsuarioClient usuarioClient;

    public Usuario verificarUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new RuntimeException("ID do usuário não informado.");
        }

        Usuario usuario = usuarioClient.buscarUsuario(usuarioId);

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        return usuario;
    }

    public Usuario verificarPermissao(
            Long usuarioId,
            String permissao) {

        Usuario usuario = verificarUsuario(usuarioId);

        String papel = usuario.getPapel();

        if (papel == null) {
            throw new RuntimeException(
                    "Usuário não possui papel definido."
            );
        }

        boolean permitido = false;

        switch (permissao) {

            case "LISTAR":
                permitido =
                        papel.equalsIgnoreCase("CLIENTE") ||
                                papel.equalsIgnoreCase("VENDEDOR") ||
                                papel.equalsIgnoreCase("GERENTE");
                break;

            case "CADASTRAR":
                permitido =
                        papel.equalsIgnoreCase("VENDEDOR") ||
                                papel.equalsIgnoreCase("GERENTE");
                break;

            case "ATUALIZAR":
                permitido =
                        papel.equalsIgnoreCase("VENDEDOR") ||
                                papel.equalsIgnoreCase("GERENTE");
                break;

            case "EXCLUIR":
                permitido =
                        papel.equalsIgnoreCase("GERENTE");
                break;
        }

        if (!permitido) {
            throw new RuntimeException(
                    "Usuário " + usuario.getNome()
                            + " não possui permissão para: "
                            + permissao
            );
        }

        return usuario;
    }
}