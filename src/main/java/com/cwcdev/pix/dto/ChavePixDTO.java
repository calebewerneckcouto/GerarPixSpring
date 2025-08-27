package com.cwcdev.pix.dto;

public class ChavePixDTO {

    private Long id;
    private String tipo;
    private String valor;
    private Long clienteId; // apenas o ID do cliente
    private String clienteNome; // opcional, se quiser incluir nome do cliente

    public ChavePixDTO() {
    }

    // Construtor que recebe a entidade e converte para DTO
    public ChavePixDTO(com.cwcdev.pix.model.ChavePix chavePix) {
        this.id = chavePix.getId();
        this.tipo = chavePix.getTipo();
        this.valor = chavePix.getValor();
        if (chavePix.getCliente() != null) {
            this.clienteId = chavePix.getCliente().getId();
           
        }
    }

    // Getters e setters
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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }
}
