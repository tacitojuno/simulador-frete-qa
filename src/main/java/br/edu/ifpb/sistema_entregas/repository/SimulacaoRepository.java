package br.edu.ifpb.sistema_entregas.repository;

import br.edu.ifpb.sistema_entregas.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    //Lista apenas as simulações de um usuário específico (logado)
    List<Simulacao> findByUsuarioId(Long usuarioId);
}
