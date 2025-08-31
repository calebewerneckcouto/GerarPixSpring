package com.cwcdev.pix.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cwcdev.pix.model.ChavePix;
import com.cwcdev.pix.service.ChavePixService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chaves")
@Tag(name = "ChavesPix", description = "API para gerenciar Chaves Pix")
@SecurityRequirement(name = "bearerAuth")
public class ChavePixController {

    @Autowired
    private ChavePixService service;

    @GetMapping
    @Operation(summary = "Listar todas as chaves (paginado)", description = "Admin vê todas. User só vê suas.")
    public ResponseEntity<Page<ChavePix>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todas as chaves (sem paginação)")
    public ResponseEntity<List<ChavePix>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar chave por ID")
    public ResponseEntity<ChavePix> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Criar nova chave Pix",
               description = "Admin pode criar para qualquer cliente. User só para clientes próprios.")
    public ResponseEntity<ChavePix> criar(
            @Parameter(description = "ID do cliente associado") @RequestParam Long clienteId,
            @Valid @RequestBody ChavePix chavePix) {
        ChavePix nova = service.criar(chavePix, clienteId);
        return ResponseEntity.ok(nova);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Atualizar chave Pix")
    public ResponseEntity<ChavePix> atualizar(@PathVariable Long id, @Valid @RequestBody ChavePix chavePix) {
        return ResponseEntity.ok(service.atualizar(id, chavePix));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Remover chave Pix")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
