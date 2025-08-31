package com.cwcdev.pix.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cwcdev.pix.dto.UserDTO;
import com.cwcdev.pix.dto.UserInsertDTO;
import com.cwcdev.pix.dto.UserUpdateDTO;
import com.cwcdev.pix.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar usuários.
 * Inclui operações de listagem, busca, criação, atualização e exclusão.
 * Autorização via Bearer Token (JWT).
 */
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearer-key") 
public class UserResource {

    @Autowired
    private UserService service;

    @Operation(
            summary = "Listar usuários (paginado)",
            description = "Retorna uma página de usuários. Necessário ROLE_ADMIN",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
        Page<UserDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Buscar dados do usuário logado",
            description = "Retorna informações do usuário autenticado",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> findMe() {
        UserDTO dto = service.findMe();
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna informações de um usuário específico pelo ID",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Criar usuário",
            description = "Cria um novo usuário e retorna os dados inseridos",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> insert(
            @Parameter(description = "Dados do usuário para inserção") 
            @Valid @RequestBody UserInsertDTO dto) {

        UserDTO newDto = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newDto);
    }


    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados de um usuário existente",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Parameter(description = "Dados para atualização do usuário") 
            @Valid @RequestBody UserUpdateDTO dto) {

        UserDTO updatedDto = service.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @Operation(
            summary = "Excluir usuário",
            description = "Exclui um usuário existente pelo ID",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
