package com.cwcdev.pix.dto;

import jakarta.validation.constraints.NotBlank;

public class ChavePixDTO {
    private Long id;

    @NotBlank(message = "Tipo é obrigatório")
    private String tipo; // EMAIL, CPF, CNPJ, TELEFONE, ALEATORIO

    private String valor; // para ALEATORIO null/empty no request; sera gerado

    // pode enviar clienteId para associar a um cliente já existente
    private Long clienteId;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}
