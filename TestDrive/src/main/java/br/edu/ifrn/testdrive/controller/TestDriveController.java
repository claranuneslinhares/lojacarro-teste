package br.edu.ifrn.testdrive.controller;

import br.edu.ifrn.testdrive.dto.TestDriveDTO;
import br.edu.ifrn.testdrive.model.TestDrive;
import br.edu.ifrn.testdrive.services.TestDriveService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testdrive")
@CrossOrigin("*")
public class TestDriveController {

    private final TestDriveService service;

    public TestDriveController(TestDriveService service) {
        this.service = service;
    }

    @PostMapping
    public TestDrive cadastrar(@RequestBody TestDriveDTO dto) {
        return service.cadastrar(dto);
    }

    @GetMapping
    public List<TestDrive> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public TestDrive buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TestDrive atualizar(
            @PathVariable Long id,
            @RequestBody TestDriveDTO dto) {

        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/cancelar")
    public TestDrive cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}