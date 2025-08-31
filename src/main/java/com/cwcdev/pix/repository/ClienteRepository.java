package com.cwcdev.pix.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.model.User;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.pessoa.cpf = :cpf")
    boolean existsByCpf(@Param("cpf") String cpf);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.pessoa.email = :email")
    boolean existsByEmail(@Param("email") String email);

    // Paginação nativa para todos
    @Query(
        value = "SELECT * FROM cliente ORDER BY id /*#pageable*/",
        countQuery = "SELECT count(*) FROM cliente",
        nativeQuery = true
    )
    Page<Cliente> findAllNative(Pageable pageable);

    // Paginação para clientes de um usuário específico
    @Query(
        value = "SELECT * FROM cliente c WHERE c.user_id = :userId ORDER BY c.id /*#pageable*/",
        countQuery = "SELECT count(*) FROM cliente c WHERE c.user_id = :userId",
        nativeQuery = true
    )
    Page<Cliente> findByCreatedByNative(Long userId, Pageable pageable);

    // Listar todos clientes de um usuário (sem paginação)
    List<Cliente> findByCreatedBy(User usuario);
}
