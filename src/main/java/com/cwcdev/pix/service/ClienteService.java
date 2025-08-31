package com.cwcdev.pix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.model.User;
import com.cwcdev.pix.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AuthService authService; // pega o usuário logado via JWT

 // Salvar cliente com validação global
    public Cliente salvar(Cliente cliente) {
        if (cliente.getPessoa() == null) {
            throw new IllegalArgumentException("Pessoa é obrigatória.");
        }

        // Verifica duplicidade global
        if (clienteRepository.existsByCpf(cliente.getPessoa().getCpf())) {
            throw new IllegalArgumentException("Cliente com este CPF já existe.");
        }
        if (clienteRepository.existsByEmail(cliente.getPessoa().getEmail())) {
            throw new IllegalArgumentException("Cliente com este email já existe.");
        }


        User usuarioLogado = authService.authenticated();
        cliente.setCreatedBy(usuarioLogado);

        return clienteRepository.save(cliente);
    }

    // Listar todos clientes paginado
    public Page<Cliente> listarTodos(Pageable pageable) {
        User usuarioLogado = authService.authenticated();
        boolean isAdmin = usuarioLogado.hasRole("ROLE_ADMIN");

        if (isAdmin) {
            return clienteRepository.findAllNative(pageable);
        } else {
            return clienteRepository.findByCreatedByNative(usuarioLogado.getId(), pageable);
        }
    }

    // Listar todos clientes sem paginação
    public List<Cliente> listarTodosSemPaginacao() {
        User usuarioLogado = authService.authenticated();
        boolean isAdmin = usuarioLogado.hasRole("ROLE_ADMIN");

        if (isAdmin) {
            return clienteRepository.findAll();
        } else {
            return clienteRepository.findByCreatedBy(usuarioLogado);
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        User usuarioLogado = authService.authenticated();
        boolean isAdmin = usuarioLogado.hasRole("ROLE_ADMIN");

        if (!isAdmin && !cliente.getCreatedBy().equals(usuarioLogado)) {
            throw new SecurityException("Você não tem permissão para acessar este cliente.");
        }
        return cliente;
    }

    // Atualizar cliente
 // Atualizar cliente
    public Cliente atualizar(Long id, Cliente atualizado) {
        Cliente cliente = buscarPorId(id);

        // Evitar duplicidade global, ignorando o próprio registro
        if (clienteRepository.existsByCpf(atualizado.getPessoa().getCpf()) &&
            !cliente.getPessoa().getCpf().equals(atualizado.getPessoa().getCpf())) {
            throw new IllegalArgumentException("Outro cliente com este CPF já existe.");
        }
        if (clienteRepository.existsByEmail(atualizado.getPessoa().getEmail()) &&
            !cliente.getPessoa().getEmail().equals(atualizado.getPessoa().getEmail())) {
            throw new IllegalArgumentException("Outro cliente com este email já existe.");
        }

        cliente.setPessoa(atualizado.getPessoa());
        cliente.setAgencia(atualizado.getAgencia());
        cliente.setConta(atualizado.getConta());
        cliente.setBanco(atualizado.getBanco());

        return clienteRepository.save(cliente);
    }


    // Deletar cliente
    public void deletar(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }
}
