package br.edu.ifrn.contabil.repository;

import br.edu.ifrn.contabil.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository
        extends JpaRepository<Lancamento, Long> {
}