package com.cwcdev.pix.model;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

@Entity
public class ChavePix {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // mais simples para bancos comuns
    private Long id;

    // Obrigatório enviar tipo no JSON
    private String tipo; // CPF, EMAIL, TELEFONE, ALEATORIO

    // Não é obrigatório, será gerado no serviço se ALEATORIO
    private String valor;

    @JoinColumn(name = "cliente_id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Cliente cliente;

    @Version
    private Integer version; // controle de concorrência otimista

    public ChavePix() {}

    public ChavePix(Long id, String tipo, String valor, Cliente cliente) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChavePix other = (ChavePix) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "ChavePix [id=" + id + ", tipo=" + tipo + ", valor=" + valor + ", cliente=" + cliente + "]";
    }
}
