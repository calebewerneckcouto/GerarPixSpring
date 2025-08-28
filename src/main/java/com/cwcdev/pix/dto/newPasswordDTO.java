package com.cwcdev.pix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class newPasswordDTO {

	@NotBlank(message = "Campo Obrigatório")
	private String token;
	@NotBlank(message = "Campo Obrigatório")
	@Size(min = 8,message = "Deve ter no minimo 8 caracteres")
	private String password;
	
	public newPasswordDTO() {
		
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

}