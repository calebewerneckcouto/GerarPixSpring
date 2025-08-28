package com.cwcdev.pix.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cwcdev.pix.dto.RoleDTO;
import com.cwcdev.pix.dto.UserDTO;
import com.cwcdev.pix.dto.UserInsertDTO;
import com.cwcdev.pix.dto.UserUpdateDTO;
import com.cwcdev.pix.exception.DatabaseException;
import com.cwcdev.pix.exception.ResourceNotFoundException;
import com.cwcdev.pix.model.Role;
import com.cwcdev.pix.model.User;
import com.cwcdev.pix.projection.UserDetailsProjection;
import com.cwcdev.pix.repository.RoleRepository;
import com.cwcdev.pix.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository rolerepository;
	
	@Autowired
	private AuthService authService;

	@org.springframework.transaction.annotation.Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repository.findAll(pageable);
		return list.map(x -> new UserDTO(x));

	}
	
	
	
	
	@org.springframework.transaction.annotation.Transactional(readOnly = true)
	public UserDTO findMe() {
		User entity = authService.authenticated();
		return new UserDTO(entity);
	}


	@org.springframework.transaction.annotation.Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
	}

	@org.springframework.transaction.annotation.Transactional(readOnly = true)
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		
		entity.getRoles().clear();
		Role role = rolerepository.findByAuthority("ROLE_OPERATOR");
		entity.getRoles().add(role);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save((entity));
		return new UserDTO(entity);

	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {

		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());

		entity.getRoles().clear();
		for (RoleDTO roleDto : dto.getRoles()) {

			/* getReferenceById nao busca no banco.... sem tocar no banco de dados */
			Role role = rolerepository.getReferenceById(roleDto.getId());
			entity.getRoles().add(role);

		}

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

	    if (result.isEmpty()) {
	        throw new UsernameNotFoundException("User not found");
	    }

	    String password = result.get(0).getPassword();
	    List<String> authorities = result.stream()
	        .map(projection -> projection.getAuthority())
	        .collect(Collectors.toList());

	    return org.springframework.security.core.userdetails.User.builder()
	        .username(username)
	        .password(password)
	        .authorities(authorities.toArray(new String[0]))
	        .build();
	}

}
