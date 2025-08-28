package com.cwcdev.pix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cwcdev.pix.exception.ResourceNotFoundException;
import com.cwcdev.pix.model.ChavePix;
import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.model.Pessoa;
import com.cwcdev.pix.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Cliente criar(Cliente cliente) {
        // validações simples
        Pessoa p = cliente.getPessoa();
        if (p == null) throw new IllegalArgumentException("Pessoa é obrigatória no cliente");
        if ((p.getCpf() == null || p.getCpf().isBlank()) && (p.getCnpj() == null || p.getCnpj().isBlank()))
            throw new IllegalArgumentException("CPF ou CNPJ deve ser informado");

        // outras validações podem ser adicionadas (formato CPF/CNPJ, tamanho)
        cliente.setId(null); // garantir insert
        return clienteRepository.save(cliente);
    }

    public Page<Cliente> listarPaginado(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return clienteRepository.findAllNative(pageable);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }


    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + id));
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {
        Cliente existente = buscarPorId(id);
        // atualiza somente campos permitidos
        if (dados.getPessoa() != null) existente.setPessoa(dados.getPessoa());
        existente.setAgencia(dados.getAgencia());
        existente.setConta(dados.getConta());
        existente.setBanco(dados.getBanco());
        return clienteRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente existente = buscarPorId(id);
        clienteRepository.delete(existente);
    }
}
