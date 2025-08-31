package com.cwcdev.pix.model;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Pessoa pessoa;

    private String agencia;
    private String conta;
    private String banco;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;
    
    
    public Cliente(Long id, Pessoa pessoa, String agencia, String conta, String banco, User createdBy) {
        this.id = id;
        this.pessoa = pessoa;
        this.agencia = agencia;
        this.conta = conta;
        this.banco = banco;
        this.createdBy = createdBy;
    }

	public Cliente() {
		
	}


	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Pessoa getPessoa() {
		return pessoa;
	}


	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}


	public String getAgencia() {
		return agencia;
	}


	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}


	public String getConta() {
		return conta;
	}


	public void setConta(String conta) {
		this.conta = conta;
	}


	public String getBanco() {
		return banco;
	}


	public void setBanco(String banco) {
		this.banco = banco;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(id, other.id);
	}


	@Override
	public String toString() {
		return "Cliente [id=" + id + ", pessoa=" + pessoa + ", agencia=" + agencia + ", conta=" + conta + ", banco="
				+ banco + "]";
	}
    
    
}
