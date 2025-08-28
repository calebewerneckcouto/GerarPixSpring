package com.cwcdev.pix.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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
import com.cwcdev.pix.repository.ChavePixRepository;
import com.cwcdev.pix.repository.ClienteRepository;

@Service
public class ChavePixService {

    @Autowired
    private ChavePixRepository repo;

    @Autowired
    private ClienteRepository clienteRepository;

    private static final Pattern EMAIL = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern CPF = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    private static final Pattern CNPJ = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");
    private static final Pattern TELEFONE = Pattern.compile("^\\+?[0-9]{10,13}$");

    @Transactional
    public ChavePix criar(ChavePix chavePix, Long clienteId) {
        // associa cliente
        Cliente cliente = null;
        if (clienteId != null) {
            cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para associar chave"));
        } else if (chavePix.getCliente() != null && chavePix.getCliente().getId() != null) {
            cliente = clienteRepository.findById(chavePix.getCliente().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para associar chave"));
        } else {
            throw new IllegalArgumentException("É necessário informar clienteId ou um cliente existente na chave");
        }

        chavePix.setCliente(cliente);
        chavePix.setId(null); // garante insert

        validarEPreparar(chavePix);

        return repo.save(chavePix);
    }
    
    
    public List<ChavePix> listarTodas() {
        return repo.findAll();
    }



    @Transactional
    public ChavePix atualizar(Long id, ChavePix dados) {
        ChavePix existente = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada"));
        existente.setTipo(dados.getTipo());
        existente.setValor(dados.getValor());
        if (dados.getCliente() != null && dados.getCliente().getId() != null) {
            Cliente c = clienteRepository.findById(dados.getCliente().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
            existente.setCliente(c);
        }
        validarEPreparar(existente);
        return repo.save(existente);
    }

    public ChavePix buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada"));
    }

    @Transactional
    public void remover(Long id) {
        ChavePix existente = buscarPorId(id);
        repo.delete(existente);
    }

    public Page<ChavePix> listarPaginado(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repo.findAllNative(pageable);
    }

    // Valida tipo, formata/gera valor e checa duplicidade
    private void validarEPreparar(ChavePix chavePix) {
        if (chavePix.getTipo() == null) throw new IllegalArgumentException("Tipo de chave é obrigatório");

        String tipo = chavePix.getTipo().trim().toUpperCase();
        String valor = chavePix.getValor();
        switch (tipo) {
            case "EMAIL":
                if (valor == null || !EMAIL.matcher(valor).matches()) throw new IllegalArgumentException("Email inválido");
                break;
            case "CPF":
                if (valor == null || !CPF.matcher(valor).matches()) throw new IllegalArgumentException("CPF inválido. Use formato xxx.xxx.xxx-xx");
                break;
            case "CNPJ":
                if (valor == null || !CNPJ.matcher(valor).matches()) throw new IllegalArgumentException("CNPJ inválido. Use formato xx.xxx.xxx/xxxx-xx");
                break;
            case "TELEFONE":
                if (valor == null || !TELEFONE.matcher(valor).matches()) throw new IllegalArgumentException("Telefone inválido");
                break;
            case "ALEATORIO":
                // Cliente precisa ter CPF (ou pode ter CNPJ, mas regra previa usa CPF)
                String cpf = chavePix.getCliente() != null && chavePix.getCliente().getPessoa() != null
                        ? chavePix.getCliente().getPessoa().getCpf()
                        : null;
                if (cpf == null || cpf.isBlank()) {
                    throw new IllegalArgumentException("Para chave ALEATORIO é necessário CPF do cliente");
                }
                if (repo.existsAleatorioByCpf(cpf)) {
                    throw new IllegalArgumentException("Cliente com este CPF já possui uma chave ALEATORIO");
                }
                // gera UUID único
                String novo;
                do {
                    novo = UUID.randomUUID().toString();
                } while (repo.existsByValor(novo));
                chavePix.setValor(novo);
                return; // não precisa checar duplicidade no final (gerado único)
            default:
                throw new IllegalArgumentException("Tipo de chave inválido");
        }

        // verificar duplicidade global (para todos os tipos exceto ALEATORIO)
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Valor da chave é obrigatório");
        if (repo.existsByValor(valor)) throw new IllegalArgumentException("Chave já existe");
    }
}
