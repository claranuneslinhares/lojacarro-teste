package br.edu.ifrn.log.repository;

import br.edu.ifrn.log.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}