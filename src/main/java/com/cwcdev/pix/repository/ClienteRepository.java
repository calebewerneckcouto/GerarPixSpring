package com.cwcdev.pix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cwcdev.pix.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
