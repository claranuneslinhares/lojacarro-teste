package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger =
            LogManager.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;


    // CADASTRAR USUÁRIO
    public Usuario save(Usuario usuario) {

        logger.info(
                "Iniciando cadastro de usuário: {} - Papel: {}",
                usuario.getNome(),
                usuario.getPapel()
        );

        validarPapel(usuario.getPapel());

        Usuario salvo =
                usuarioRepository.save(usuario);

        logger.info(
                "Usuário cadastrado com sucesso. ID: {} - Nome: {}",
                salvo.getId(),
                salvo.getNome()
        );

        return salvo;
    }


    // LISTAR USUÁRIOS
    public List<Usuario> findAll() {

        logger.info(
                "Consultando todos os usuários."
        );

        List<Usuario> usuarios =
                usuarioRepository.findAll();

        logger.info(
                "Quantidade de usuários encontrados: {}",
                usuarios.size()
        );

        return usuarios;
    }


    // BUSCAR POR ID
    public Optional<Usuario> findById(Long id) {

        logger.info(
                "Consultando usuário ID: {}",
                id
        );

        Optional<Usuario> usuario =
                usuarioRepository.findById(id);

        if (usuario.isPresent()) {

            logger.info(
                    "Usuário ID {} encontrado.",
                    id
            );

        } else {

            logger.warn(
                    "Usuário ID {} não encontrado.",
                    id
            );
        }

        return usuario;
    }


    // ATUALIZAR USUÁRIO
    public Usuario update(Usuario usuario) {

        logger.info(
                "Iniciando atualização do usuário ID: {}",
                usuario.getId()
        );

        if (usuario.getId() == null) {

            logger.error(
                    "Tentativa de atualizar usuário sem ID."
            );

            throw new CarroException(
                    "O ID do usuário não pode ser nulo."
            );
        }

        if (!usuarioRepository.existsById(usuario.getId())) {

            logger.warn(
                    "Usuário ID {} não encontrado para atualização.",
                    usuario.getId()
            );

            throw new CarroException(
                    "Usuário não encontrado."
            );
        }

        validarPapel(usuario.getPapel());

        Usuario atualizado =
                usuarioRepository.save(usuario);

        logger.info(
                "Usuário ID {} atualizado com sucesso.",
                usuario.getId()
        );

        return atualizado;
    }


    // EXCLUIR USUÁRIO
    public void deleteById(Long id) {

        logger.info(
                "Solicitação para excluir usuário ID: {}",
                id
        );

        if (!usuarioRepository.existsById(id)) {

            logger.warn(
                    "Usuário ID {} não encontrado para exclusão.",
                    id
            );

            throw new CarroException(
                    "Usuário não encontrado."
            );
        }

        usuarioRepository.deleteById(id);

        logger.info(
                "Usuário ID {} excluído com sucesso.",
                id
        );
    }


    // VALIDAR PAPEL
    private void validarPapel(String papel) {

        if (papel == null ||
                (!papel.equals("CLIENTE") &&
                        !papel.equals("VENDEDOR") &&
                        !papel.equals("GERENTE"))) {

            logger.error(
                    "Papel de usuário inválido: {}",
                    papel
            );

            throw new CarroException(
                    "Papel inválido. " +
                            "Use CLIENTE, VENDEDOR ou GERENTE."
            );
        }
    }
}