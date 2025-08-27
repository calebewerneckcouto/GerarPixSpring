package com.cwcdev.pix.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.cwcdev.pix.exception.ResourceNotFoundException;
import com.cwcdev.pix.model.ChavePix;
import com.cwcdev.pix.repository.ChavePixRepository;
import com.cwcdev.pix.repository.ClienteRepository;

@Service
public class ChavePixService {

    @Autowired
    private ChavePixRepository chavePixRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public ChavePix salvar(ChavePix chavePix) {
        if (chavePix.getCliente() != null && chavePix.getCliente().getId() == null) {
            clienteRepository.save(chavePix.getCliente());
        }
        chavePix.setId(null); // Insert

        validarChave(chavePix);

        return chavePixRepository.save(chavePix);
    }

    public ChavePix atualizar(Long id, ChavePix dados) {
        ChavePix existente = buscarPorId(id);
        existente.setTipo(dados.getTipo());
        existente.setValor(dados.getValor());
        validarChave(existente);
        return chavePixRepository.save(existente);
    }

    public void remover(Long id) {
        ChavePix existente = buscarPorId(id);
        chavePixRepository.delete(existente);
    }

    public ChavePix buscarPorId(Long id) {
        return chavePixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada"));
    }

    public List<ChavePix> listarTodos() {
        return chavePixRepository.findAll();
    }

    public Page<ChavePix> buscarPaginado(int pagina, int tamanho, String ordenarPor) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.ASC, ordenarPor));
        return chavePixRepository.findAll(pageable);
    }


    private void validarChave(ChavePix chavePix) {
        if (chavePix.getTipo() == null) throw new RuntimeException("Tipo de chave é obrigatório");

        String tipo = chavePix.getTipo().toUpperCase();
        String valor = chavePix.getValor();

        switch (tipo) {
            case "EMAIL":
                if (!Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$").matcher(valor).matches())
                    throw new RuntimeException("Email inválido");
                break;
            case "CPF":
                if (!Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}").matcher(valor).matches())
                    throw new RuntimeException("CPF inválido");
                break;
            case "CNPJ":
                if (!Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}").matcher(valor).matches())
                    throw new RuntimeException("CNPJ inválido");
                break;
            case "TELEFONE":
                if (!Pattern.compile("^\\+?[0-9]{10,13}$").matcher(valor).matches())
                    throw new RuntimeException("Telefone inválido");
                break;
            case "ALEATORIO":
                String cpf = chavePix.getCliente().getPessoa().getCpf();

                // Verifica se já existe uma chave ALEATORIO com valor não nulo para este CPF
                if (chavePixRepository.existsAleatorioByCpf(cpf)) {
                    throw new RuntimeException("Cliente com este CPF já possui uma chave ALEATORIO");
                }

                // Gera UUID único global
                String novoValor;
                do {
                    novoValor = UUID.randomUUID().toString();
                } while (chavePixRepository.existsByValor(novoValor));

                chavePix.setValor(novoValor);
                return;


            default:
                throw new RuntimeException("Tipo de chave inválido");
        }

        // Verifica duplicidade global
        if (chavePixRepository.existsByValor(valor))
            throw new RuntimeException("Chave já existe");
    }
}