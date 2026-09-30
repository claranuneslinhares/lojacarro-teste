package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {

    private static final Logger logger =
            LogManager.getLogger(CarroService.class);

    @Autowired
    public CarroRepository carroRepository;


    // CADASTRAR CARRO
    public Carro save(Carro c) {

        logger.info("Iniciando cadastro do carro: {}", c.getModelo());

        validarModelo(c.getModelo());
        validarPreco(c.getPreco());

        Carro carroSalvo = carroRepository.save(c);

        logger.info(
                "Carro cadastrado com sucesso. ID: {} - Modelo: {}",
                carroSalvo.getId(),
                carroSalvo.getModelo()
        );

        return carroSalvo;
    }


    // EXCLUIR POR ID
    public void deleteById(Long id) {

        logger.info("Solicitação para excluir carro ID: {}", id);

        if (id <= 0) {

            logger.error(
                    "Tentativa de excluir carro com ID inválido: {}",
                    id
            );

            throw new CarroException(
                    "O ID do carro não pode ser negativo. ID fornecido: " + id
            );
        }

        carroRepository.deleteById(id);

        logger.info(
                "Carro ID {} excluído com sucesso.",
                id
        );
    }


    // BUSCAR POR ID
    public Optional<Carro> findById(Long id) {

        logger.info(
                "Consultando carro pelo ID: {}",
                id
        );

        if (id <= 0) {

            logger.error(
                    "Tentativa de consultar carro com ID inválido: {}",
                    id
            );

            throw new CarroException(
                    "O ID do carro não pode ser negativo. ID fornecido: " + id
            );
        }

        Optional<Carro> carro = carroRepository.findById(id);

        if (carro.isPresent()) {

            logger.info(
                    "Carro encontrado. ID: {}",
                    id
            );

        } else {

            logger.warn(
                    "Carro ID {} não encontrado.",
                    id
            );
        }

        return carro;
    }


    // LISTAR TODOS
    public List<Carro> findAll() {

        logger.info("Consultando todos os carros cadastrados.");

        List<Carro> carros = carroRepository.findAll();

        logger.info(
                "Quantidade de carros encontrados: {}",
                carros.size()
        );

        return carros;
    }


    // BUSCAR POR MODELO
    public Optional<Carro> findByModelo(String modelo) {

        logger.info(
                "Consultando carro pelo modelo: {}",
                modelo
        );

        validarModelo(modelo);

        Optional<Carro> carro =
                carroRepository.findFirstByModelo(modelo);

        if (carro.isPresent()) {

            logger.info(
                    "Carro encontrado pelo modelo: {}",
                    modelo
            );

        } else {

            logger.warn(
                    "Nenhum carro encontrado com o modelo: {}",
                    modelo
            );
        }

        return carro;
    }


    // CADASTRO LEGADO
    public Carro saveFromLegacy(String modelo, double preco) {

        logger.info(
                "Cadastro legado de carro. Modelo: {}",
                modelo
        );

        Carro carro =
                new Carro(modelo, LocalDate.now().getYear(), preco);

        return save(carro);
    }


    // ATUALIZAR POR MODELO
    public Carro updateByModelo(String modelo, double preco) {

        logger.info(
                "Atualizando carro pelo modelo: {}",
                modelo
        );

        Carro carro = localizarCarroPorModelo(modelo);

        validarPreco(preco);

        carro.setPreco(preco);

        Carro atualizado =
                carroRepository.save(carro);

        logger.info(
                "Carro {} atualizado com sucesso.",
                modelo
        );

        return atualizado;
    }


    // EXCLUIR POR MODELO
    public Carro deleteByModelo(String modelo) {

        logger.info(
                "Excluindo carro pelo modelo: {}",
                modelo
        );

        Carro carro =
                localizarCarroPorModelo(modelo);

        carroRepository.delete(carro);

        logger.info(
                "Carro {} excluído com sucesso.",
                modelo
        );

        return carro;
    }


    // ATUALIZAR POR ID
    public Carro update(Carro c) {

        logger.info(
                "Iniciando atualização do carro ID: {}",
                c.getId()
        );

        if (c.getId() == null) {

            logger.error(
                    "Tentativa de atualizar carro sem ID."
            );

            throw new CarroException(
                    "O ID do carro para atualização não pode ser nulo."
            );
        }

        if (!carroRepository.existsById(c.getId())) {

            logger.warn(
                    "Carro com ID {} não encontrado para atualização.",
                    c.getId()
            );

            throw new CarroException(
                    "Carro com ID " + c.getId() +
                            " não encontrado para atualização."
            );
        }

        validarModelo(c.getModelo());
        validarPreco(c.getPreco());

        Carro atualizado =
                carroRepository.save(c);

        logger.info(
                "Carro ID {} atualizado com sucesso.",
                c.getId()
        );

        return atualizado;
    }


    // VALIDAR MODELO
    private void validarModelo(String modelo) {

        if (modelo == null || modelo.trim().isEmpty()) {

            logger.error(
                    "Modelo do carro informado está vazio."
            );

            throw new CarroException(
                    "O modelo do carro não pode estar vazio."
            );
        }

        if (modelo.length() >= 5) {

            logger.warn(
                    "Modelo {} possui tamanho inválido: {} caracteres.",
                    modelo,
                    modelo.length()
            );

            throw new CarroException(
                    "O modelo do carro deve ter menos de 5 caracteres. " +
                            "Tamanho atual: " + modelo.length()
            );
        }
    }


    // VALIDAR PREÇO
    private void validarPreco(double preco) {

        if (preco < 0) {

            logger.error(
                    "Preço inválido informado: {}",
                    preco
            );

            throw new CarroException(
                    "O preço do carro não pode ser negativo. " +
                            "Valor fornecido: " + preco
            );
        }
    }


    // LOCALIZAR CARRO
    private Carro localizarCarroPorModelo(String modelo) {

        validarModelo(modelo);

        return carroRepository
                .findFirstByModelo(modelo)
                .orElseThrow(() -> {

                    logger.warn(
                            "Carro com modelo {} não encontrado.",
                            modelo
                    );

                    return new CarroException(
                            "Carro com modelo " +
                                    modelo +
                                    " não encontrado."
                    );
                });
    }
}