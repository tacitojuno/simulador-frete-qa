package br.edu.ifpb.simulador_frete_qa.repository;

import br.edu.ifpb.simulador_frete_qa.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    //Lista apenas as simulações de um usuário específico (logado)
    List<Simulacao> findByUsuarioId(Long usuarioId);
}
