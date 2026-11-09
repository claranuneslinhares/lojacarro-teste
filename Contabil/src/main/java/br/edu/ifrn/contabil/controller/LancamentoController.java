package br.edu.ifrn.contabil.controller;

import br.edu.ifrn.contabil.dto.LancamentoDTO;
import br.edu.ifrn.contabil.model.Lancamento;
import br.edu.ifrn.contabil.services.LancamentoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lancamentos")
@CrossOrigin("*")
public class LancamentoController {

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @PostMapping
    public Lancamento cadastrar(@RequestBody LancamentoDTO dto) {
        return service.cadastrar(dto);
    }

    @GetMapping
    public List<Lancamento> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Lancamento buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Lancamento atualizar(
            @PathVariable Long id,
            @RequestBody LancamentoDTO dto) {

        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}