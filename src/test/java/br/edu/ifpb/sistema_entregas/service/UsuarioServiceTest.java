package br.edu.ifpb.sistema_entregas.service;

import br.edu.ifpb.sistema_entregas.model.Usuario;
import br.edu.ifpb.sistema_entregas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario();
        usuarioBase.setId(1L);
        usuarioBase.setNome("João Silva");
        usuarioBase.setCpf("12345678900");
        usuarioBase.setEmail("joao@email.com");
        usuarioBase.setSenha("senhaForte123");
    }

    //Funcionalidade 3: Cadastro de Usuário
    //CT008
    @Test
    void deveCadastrarUsuarioComSucesso_CT008() {
        when(repository.existsByEmail(usuarioBase.getEmail())).thenReturn(false);
        when(repository.existsByCpf(usuarioBase.getCpf())).thenReturn(false);

        //Simula o salvamento
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cadastrar(usuarioBase);

        //Verificações de segurança
        assertNotNull(resultado);
        //A senha salva NUNCA pode ser igual à senha descriptografada (BCrypt funcionando)
        assertNotEquals("senhaForte123", resultado.getSenha());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    //CT009
    @Test
    void deveLancarExcecaoAoCadastrarEmailJaExistente_CT009() {
        //Simula que E-Mail já existe no banco
        when(repository.existsByEmail(usuarioBase.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrar(usuarioBase);
        });

        assertEquals("Erro: E-mail já cadastrado", exception.getMessage());
        //Garante que o usuário duplicado não foi salvo
        verify(repository, never()).save(any());
    }

    //Funcionalidade 4: Autenticação e Segurança
    //CT010
    @Test
    void deveAutenticarComSucesso_CT010() {
        //Simulando o banco de dados devolvendo o usuário com a senha já criptografada
        String senhaHash = BCrypt.hashpw("senhaForte123", BCrypt.gensalt());
        usuarioBase.setSenha(senhaHash);

        when(repository.findByEmail(usuarioBase.getEmail())).thenReturn(Optional.of(usuarioBase));

        //Ação: Tentando logar com a senha normal (Sem criptografia)
        Usuario resultado = usuarioService.autenticar("joao@email.com", "senhaForte123");

        assertNotNull(resultado);
        assertEquals("joao@email.com", resultado.getEmail());
    }

    //CT011
    @Test
    void deveLancarExcecaoAoAutenticarComSenhaIncorreta_CT011() {
        String senhaHash = BCrypt.hashpw("senhaForte123", BCrypt.gensalt());
        usuarioBase.setSenha(senhaHash);

        when(repository.findByEmail(usuarioBase.getEmail())).thenReturn(Optional.of(usuarioBase));

        //Ação: Tentando logar com a senha ERRADA
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticar("joao@email.com", "senhaErrada123");
        });

        assertEquals("Erro: Credenciais inválidas", exception.getMessage());
    }
}