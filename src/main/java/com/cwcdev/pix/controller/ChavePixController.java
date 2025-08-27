package com.cwcdev.pix.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cwcdev.pix.dto.ChavePixDTO;
import com.cwcdev.pix.model.ChavePix;
import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.service.ChavePixService;
import com.cwcdev.pix.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chaves")
@Tag(name = "Chaves Pix", description = "Gerenciamento de chaves Pix")
public class ChavePixController {

    @Autowired
    private ChavePixService chaveService;

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Criar chave Pix associada a um cliente existente. Informe clienteId no body.")
    @PostMapping
    public ResponseEntity<ChavePix> criar(@Valid @RequestBody ChavePixDTO dto) {
        ChavePix entidade = new ChavePix();
        entidade.setTipo(dto.getTipo());
        entidade.setValor(dto.getValor());

        // associa cliente mínimo para validação geradora de ALEATORIO (pelo service)
        if (dto.getClienteId() != null) {
            Cliente c = clienteService.buscarPorId(dto.getClienteId());
            entidade.setCliente(c);
        }
        ChavePix salva = chaveService.criar(entidade, dto.getClienteId());
        return ResponseEntity.status(201).body(salva);
    }

    @Operation(summary = "Listar chaves (paginado)")
    @GetMapping
    public ResponseEntity<Page<ChavePix>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(chaveService.listarPaginado(page, size, sortBy));
    }
    
    @Operation(summary = "Listar todas as chaves (sem paginação)")
    @GetMapping("/all")
    public ResponseEntity<List<ChavePix>> listarTodas() {
        return ResponseEntity.ok(chaveService.listarTodas());
    }


    @Operation(summary = "Buscar chave por id")
    @GetMapping("/{id}")
    public ResponseEntity<ChavePix> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(chaveService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar chave")
    @PutMapping("/{id}")
    public ResponseEntity<ChavePix> atualizar(@PathVariable Long id, @Valid @RequestBody ChavePixDTO dto) {
        ChavePix entidade = new ChavePix();
        entidade.setTipo(dto.getTipo());
        entidade.setValor(dto.getValor());
        if (dto.getClienteId() != null) {
            entidade.setCliente(clienteService.buscarPorId(dto.getClienteId()));
        }
        return ResponseEntity.ok(chaveService.atualizar(id, entidade));
    }

    @Operation(summary = "Remover chave")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        chaveService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
