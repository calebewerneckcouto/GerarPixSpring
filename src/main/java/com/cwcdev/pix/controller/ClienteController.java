package com.cwcdev.pix.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.cwcdev.pix.dto.ClienteDTO;
import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.model.Pessoa;
import com.cwcdev.pix.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operações para gerenciar clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Criar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody ClienteDTO dto) {
        Cliente c = dtoToEntity(dto);
        Cliente salvo = clienteService.criar(c);
        return ResponseEntity.status(201).body(salvo);
    }
    
    
    @Operation(summary = "Listar todos os clientes (sem paginação)")
    @GetMapping("/all")
    public ResponseEntity<List<Cliente>> Todos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }


    @Operation(summary = "Listar todos clientes (paginado)")
    @GetMapping
    public ResponseEntity<Page<Cliente>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(clienteService.listarPaginado(pageable));
    }

    @Operation(summary = "Listar todos (sem paginação)")
    @GetMapping("/all/v2")
    public ResponseEntity<java.util.List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @Operation(summary = "Buscar cliente por id")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar cliente")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        Cliente c = dtoToEntity(dto);
        return ResponseEntity.ok(clienteService.atualizar(id, c));
    }

    @Operation(summary = "Remover cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Conversor simples DTO -> Entity
    private Cliente dtoToEntity(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setId(dto.getId());
        com.cwcdev.pix.model.Pessoa p = new Pessoa();
        p.setNome(dto.getNome());
        p.setCpf(dto.getCpf());
        p.setCnpj(dto.getCnpj());
        p.setEmail(dto.getEmail());
        c.setPessoa(p);
        c.setAgencia(dto.getAgencia());
        c.setConta(dto.getConta());
        c.setBanco(dto.getBanco());
        return c;
    }
}
