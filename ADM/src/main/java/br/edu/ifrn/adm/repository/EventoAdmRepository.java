package br.edu.ifrn.adm.repository;

import br.edu.ifrn.adm.model.EventoAdm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoAdmRepository
        extends JpaRepository<EventoAdm, Long> {
}