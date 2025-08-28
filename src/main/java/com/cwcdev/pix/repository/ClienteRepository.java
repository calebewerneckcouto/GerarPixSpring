package com.cwcdev.pix.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cwcdev.pix.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	
	
	@Query(
            value = "SELECT * FROM cliente ORDER BY id /*#pageable*/",
            countQuery = "SELECT count(*) FROM cliente",
            nativeQuery = true
        )
        Page<Cliente> findAllNative(Pageable pageable);
}
