package br.edu.ifpb.simulador_frete_qa.service;

import br.edu.ifpb.simulador_frete_qa.model.Usuario;
import br.edu.ifpb.simulador_frete_qa.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario cadastrar(Usuario usuario) {
        //Bloquear duplicatas
        if(repository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Erro: E-mail já cadastrado");
        }
        if(repository.existsByCpf(usuario.getCpf())) {
            throw new IllegalArgumentException("Erro: CPF já cadastrado");
        }

        String senhaCriptografada = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaCriptografada);

        return repository.save(usuario);
    }

    public Usuario autenticar(String email, String senhaPlana) {
        Optional<Usuario> usuarioBox = repository.findByEmail(email);

        if(usuarioBox.isPresent()) {
            Usuario usuario = usuarioBox.get();

            if(BCrypt.checkpw(senhaPlana, usuario.getSenha())) {
                return usuario;
            }
        }

        throw new IllegalArgumentException("Erro: Credenciais inválidas");
    }
}
