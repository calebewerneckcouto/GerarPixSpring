package com.cwcdev.pix.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.cwcdev.pix.dto.EmailDTO;
import com.cwcdev.pix.dto.newPasswordDTO;
import com.cwcdev.pix.exception.ResourceNotFoundException;
import com.cwcdev.pix.model.PasswordRecover;
import com.cwcdev.pix.model.User;
import com.cwcdev.pix.repository.PasswordRecoverRepository;
import com.cwcdev.pix.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

	@Value("${email.password-recover.token.minutes}")
	private long tokenMinutes;

	@Value("${email.password-recover.uri}")
	private String recoverUri;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordRecoverRepository passwordRecoverRepository;

	@Autowired
	private EmailService emailService;

	@Transactional
	public void createRecoverToken(EmailDTO body) {

		User user = userRepository.findByEmail(body.getEmail());

		if (user == null) {
			throw new ResourceNotFoundException("Email não encontrado");
		}

		String token = UUID.randomUUID().toString();
		PasswordRecover entity = new PasswordRecover();
		entity.setEmail(body.getEmail());
		entity.setToken(token);
		entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60));
		entity = passwordRecoverRepository.save(entity);

		String text = "Acesse o link para definir uma nova senha\n\n" + recoverUri + token + ". Validade de "
				+ tokenMinutes + " minutos";

		emailService.sendEmail(body.getEmail(), "Recuperação de Senha", text);
	}

	@Transactional
	public void saveNewPassWord(newPasswordDTO body) {
		List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());
		if (result.size() == 0) {
			throw new ResourceNotFoundException("Token inválido!!!");
		}

		User user = userRepository.findByEmail(result.get(0).getEmail());
		user.setPassword(passwordEncoder.encode(body.getPassword()));
		user = userRepository.save(user);
	}
	
	
	protected User authenticated() {
		  try {
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
		    String username = jwtPrincipal.getClaim("username");
		    return userRepository.findByEmail(username);
		  }
		  catch (Exception e) {
		    throw new UsernameNotFoundException("Invalid user");
		  }
		}


}
