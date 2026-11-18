package br.org.edu.ifrn.LojaCarro.repository;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.model.SituacaoCarro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CarroRepository extends JpaRepository<Carro, Long> {

    Optional<Carro> findByIdAndSituacaoAndArquivoFotoIsNotNull(
            Long id, SituacaoCarro situacao
    );

    @Query("""
            select c from Carro c
            where c.situacao = :situacao and c.arquivoFoto is not null
              and (:marca is null or lower(c.marca) like lower(concat('%', :marca, '%')))
              and (:modelo is null or lower(c.modelo) like lower(concat('%', :modelo, '%')))
              and (:ano is null or c.anoModelo = :ano)
              and (:precoMin is null or c.preco >= :precoMin)
              and (:precoMax is null or c.preco <= :precoMax)
            order by c.id desc
            """)
    List<Carro> buscarCatalogo(
            @Param("situacao") SituacaoCarro situacao,
            @Param("marca") String marca,
            @Param("modelo") String modelo,
            @Param("ano") Integer ano,
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax
    );
}