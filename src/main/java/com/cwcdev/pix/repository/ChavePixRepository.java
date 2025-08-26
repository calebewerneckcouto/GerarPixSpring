package com.cwcdev.pix.repository;

import org.springframework.data.domain.Pageable; 
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cwcdev.pix.model.ChavePix;

@Repository
public interface ChavePixRepository extends JpaRepository<ChavePix, Long> {

    // Adicionado nativeQuery = true para usar sintaxe SQL nativa, que inclui o comando LIMIT.
    @Query(value = "SELECT * FROM chave_pix cp WHERE cp.valor = :valor LIMIT 1", nativeQuery = true)
    Optional<ChavePix> findFirstByValor(@Param("valor") String valor);

    // O método default continua funcionando como esperado.
    default boolean existsByValor(String valor) {
        return findFirstByValor(valor).isPresent();
    }
    
    
    Page<ChavePix> findAll(Pageable pageable);

    
   
}