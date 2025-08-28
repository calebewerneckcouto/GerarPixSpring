package com.cwcdev.pix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cwcdev.pix.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	
	Role findByAuthority(String authority);

}
