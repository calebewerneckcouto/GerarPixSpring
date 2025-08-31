package com.cwcdev.pix.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "API para gerenciar clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Listar todos paginado
    @GetMapping
    @Operation(
        summary = "Listar clientes paginados",
        description = "Retorna uma lista paginada de clientes. Usuários comuns só veem seus próprios clientes.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Page<Cliente>> listar(Pageable pageable) {
        Page<Cliente> clientes = clienteService.listarTodos(pageable);
        return ResponseEntity.ok(clientes);
    }

    // Listar todos sem paginação
    @GetMapping("/all")
    @Operation(
        summary = "Listar todos os clientes sem paginação",
        description = "Retorna todos os clientes. Usuários comuns só veem seus próprios clientes.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<List<Cliente>> listarTodosSemPaginacao() {
        List<Cliente> clientes = clienteService.listarTodosSemPaginacao();
        return ResponseEntity.ok(clientes);
    }

    // Buscar por id
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar cliente por ID",
        description = "Retorna um cliente pelo ID. Usuários comuns só podem acessar clientes que cadastraram.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    // Salvar cliente
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(
        summary = "Cadastrar novo cliente",
        description = "Cria um novo cliente associado ao usuário logado. Verifica duplicidade de CPF e email.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Cliente> salvar(@RequestBody Cliente cliente) {
        Cliente salvo = clienteService.salvar(cliente);
        return ResponseEntity.ok(salvo);
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar cliente",
        description = "Atualiza um cliente existente. Usuários comuns só podem atualizar clientes que cadastraram.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        Cliente atualizado = clienteService.atualizar(id, cliente);
        return ResponseEntity.ok(atualizado);
    }

    // Deletar cliente
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar cliente",
        description = "Remove um cliente. Usuários comuns só podem deletar clientes que cadastraram.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
