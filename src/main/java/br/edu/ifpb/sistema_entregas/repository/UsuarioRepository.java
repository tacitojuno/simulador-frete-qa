package br.edu.ifpb.sistema_entregas.repository;

import br.edu.ifpb.sistema_entregas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
