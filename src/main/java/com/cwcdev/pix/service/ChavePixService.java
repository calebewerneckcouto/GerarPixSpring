package com.cwcdev.pix.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cwcdev.pix.exception.ResourceNotFoundException;
import com.cwcdev.pix.model.ChavePix;
import com.cwcdev.pix.model.Cliente;
import com.cwcdev.pix.model.User;
import com.cwcdev.pix.repository.ChavePixRepository;
import com.cwcdev.pix.repository.ClienteRepository;

@Service
public class ChavePixService {

    @Autowired
    private ChavePixRepository repo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private AuthService authService; // usuário logado via JWT

    private static final Pattern EMAIL = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern CPF = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    private static final Pattern CNPJ = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");
    private static final Pattern TELEFONE = Pattern.compile("^\\+?[0-9]{10,13}$");

    // Criar chave pix
    @Transactional
    public ChavePix criar(ChavePix chavePix, Long clienteId) {
        User usuarioLogado = authService.authenticated();
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // verifica permissão: user só pode cadastrar chave para cliente próprio
        if (!usuarioLogado.hasRole("ROLE_ADMIN") && !cliente.getCreatedBy().equals(usuarioLogado)) {
            throw new SecurityException("Você não tem permissão para cadastrar chave para este cliente.");
        }

        chavePix.setCliente(cliente);
        chavePix.setId(null); // garante insert
        validarEPreparar(chavePix);
        return repo.save(chavePix);
    }

    // Atualizar chave
    @Transactional
    public ChavePix atualizar(Long id, ChavePix dados) {
        ChavePix existente = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada"));
        User usuarioLogado = authService.authenticated();

        // valida permissão
        if (!usuarioLogado.hasRole("ROLE_ADMIN") && !existente.getCliente().getCreatedBy().equals(usuarioLogado)) {
            throw new SecurityException("Você não tem permissão para atualizar esta chave.");
        }

        existente.setTipo(dados.getTipo());
        existente.setValor(dados.getValor());
        validarEPreparar(existente);
        return repo.save(existente);
    }

    // Buscar por id
    public ChavePix buscarPorId(Long id) {
        ChavePix chave = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada"));
        User usuarioLogado = authService.authenticated();
        if (!usuarioLogado.hasRole("ROLE_ADMIN") && !chave.getCliente().getCreatedBy().equals(usuarioLogado)) {
            throw new SecurityException("Você não tem permissão para acessar esta chave.");
        }
        return chave;
    }

    // Remover chave
    @Transactional
    public void remover(Long id) {
        ChavePix chave = buscarPorId(id);
        repo.delete(chave);
    }

    // Listagem paginada
    public Page<ChavePix> listarPaginado(Pageable pageable) {
        return repo.findAllNative(pageable);
    }

    // Listar todas chaves visíveis para o usuário
    public List<ChavePix> listarTodas() {
        User usuarioLogado = authService.authenticated();
        if (usuarioLogado.hasRole("ROLE_ADMIN")) {
            return repo.findAll();
        } else {
            return repo.findAll().stream()
                    .filter(c -> c.getCliente().getCreatedBy().equals(usuarioLogado))
                    .collect(Collectors.toList());
        }
    }

    // Valida tipo, formata/gera valor e checa duplicidade
    private void validarEPreparar(ChavePix chavePix) {
        if (chavePix.getTipo() == null || chavePix.getTipo().isBlank()) {
            throw new IllegalArgumentException("Tipo é obrigatório");
        }

        String tipo = chavePix.getTipo().trim().toUpperCase();
        String valor = chavePix.getValor();

        switch (tipo) {
            case "EMAIL":
                if (valor == null || !EMAIL.matcher(valor).matches()) throw new IllegalArgumentException("Email inválido");
                checarDuplicidade(valor);
                break;
            case "CPF":
                if (valor == null || !CPF.matcher(valor).matches()) throw new IllegalArgumentException("CPF inválido. Use xxx.xxx.xxx-xx");
                checarDuplicidade(valor);
                break;
            case "CNPJ":
                if (valor == null || !CNPJ.matcher(valor).matches()) throw new IllegalArgumentException("CNPJ inválido. Use xx.xxx.xxx/xxxx-xx");
                checarDuplicidade(valor);
                break;
            case "TELEFONE":
                if (valor == null || !TELEFONE.matcher(valor).matches()) throw new IllegalArgumentException("Telefone inválido");
                checarDuplicidade(valor);
                break;
            case "ALEATORIO":
                // verifica cliente com CPF
                String cpf = chavePix.getCliente() != null && chavePix.getCliente().getPessoa() != null
                        ? chavePix.getCliente().getPessoa().getCpf()
                        : null;
                if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("Para chave ALEATORIO é necessário CPF do cliente");

                // verifica se cliente já tem chave ALEATORIO
                if (repo.existsAleatorioByCpf(cpf)) throw new IllegalArgumentException("Cliente já possui uma chave ALEATORIO");

                // gera UUID único
                String novo;
                do {
                    novo = UUID.randomUUID().toString();
                } while (repo.existsByValor(novo));
                chavePix.setValor(novo);
                break;
            default:
                throw new IllegalArgumentException("Tipo inválido");
        }
    }

    // Verifica duplicidade global
    private void checarDuplicidade(String valor) {
        if (repo.existsByValor(valor)) {
            throw new IllegalArgumentException("Chave já existe");
        }
    }
}
