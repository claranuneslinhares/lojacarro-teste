package br.edu.ifrn.testdrive.repository;

import br.edu.ifrn.testdrive.model.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDriveRepository
        extends JpaRepository<TestDrive, Long> {
}