package com.cwcdev.pix.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwcdev.pix.model.ChavePix;
import com.cwcdev.pix.service.ChavePixService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chaves")
@Tag(name = "Chaves Pix", description = "API para gerenciar chaves Pix dos clientes")
public class ChavePixController {

    @Autowired
    private ChavePixService service;

    @PostMapping
    @Operation(summary = "Cadastrar nova chave Pix")
    public ResponseEntity<ChavePix> salvar(@RequestBody ChavePix chavePix) {
        return ResponseEntity.ok(service.salvar(chavePix));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar chave Pix por ID")
    public ResponseEntity<ChavePix> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas as chaves Pix")
    public ResponseEntity<List<ChavePix>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ChavePix>> listarPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor) {

        Page<ChavePix> chaves = service.buscarPaginado(pagina, tamanho, ordenarPor);
        return ResponseEntity.ok(chaves);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar chave Pix")
    public ResponseEntity<ChavePix> atualizar(@PathVariable Long id, @RequestBody ChavePix chavePix) {
        return ResponseEntity.ok(service.atualizar(id, chavePix));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover chave Pix")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
