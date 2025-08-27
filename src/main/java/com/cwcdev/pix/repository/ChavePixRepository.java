package com.cwcdev.pix.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cwcdev.pix.model.ChavePix;

@Repository
public interface ChavePixRepository extends JpaRepository<ChavePix, Long> {

    @Query(value = "SELECT * FROM chave_pix cp WHERE cp.valor = :valor LIMIT 1", nativeQuery = true)
    Optional<ChavePix> findFirstByValor(@Param("valor") String valor);

    default boolean existsByValor(String valor) {
        return findFirstByValor(valor).isPresent();
    }

    Page<ChavePix> findAll(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM ChavePix c WHERE c.tipo = 'ALEATORIO' AND c.cliente.pessoa.cpf = :cpf AND c.valor IS NOT NULL")
    boolean existsAleatorioByCpf(@Param("cpf") String cpf);
}
