package com.cwcdev.pix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteDTO {
    private Long id;

    // Pessoa embutida no DTO
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String cpf;
    private String email;
    private String cnpj;

    @NotBlank(message = "Agência é obrigatória")
    private String agencia;

    @NotBlank(message = "Conta é obrigatória")
    private String conta;

    @NotBlank(message = "Banco é obrigatório")
    private String banco;

    // getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    

    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }

    public String getConta() { return conta; }
    public void setConta(String conta) { this.conta = conta; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
}
