package br.edu.ifpb.simulador_frete_qa.service;

import br.edu.ifpb.simulador_frete_qa.model.Simulacao;
import br.edu.ifpb.simulador_frete_qa.model.TipoItem;
import br.edu.ifpb.simulador_frete_qa.model.Usuario;
import br.edu.ifpb.simulador_frete_qa.repository.SimulacaoRepository;
import br.edu.ifpb.simulador_frete_qa.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoDistancia servicoDistancia;

    public SimulacaoService(SimulacaoRepository simulacaoRepository,
                            UsuarioRepository usuarioRepository,
                            ServicoDistancia servicoDistancia) {
        this.simulacaoRepository = simulacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicoDistancia = servicoDistancia;
    }

    public Simulacao criarSimulacao(Simulacao simulacao, Long usuarioId) {
        if(simulacao.getPeso() <= 0){
            throw new IllegalArgumentException("Erro: O peso deve ser maior que zero");
        }

        Double distancia = servicoDistancia.buscarDistancia(simulacao.getCidadeOrigem(), simulacao.getCidadeDestino());

        int diasBase = (int) Math.ceil(distancia/50.0); //Simula 50km por dia
        switch(simulacao.getModalidadeEnvio()){
            case ECONOMICO -> simulacao.setPrazoEstimadoDias(diasBase + 2);
            case EXPRESSO -> simulacao.setPrazoEstimadoDias(diasBase);
            case PRIORITARIO -> simulacao.setPrazoEstimadoDias(Math.max(1, diasBase - 1));
        };

        double custoBase = (simulacao.getPeso() * 2.0) + (distancia * 0.15); //Custo base por peso e distância
        double multiplicadorModalidade = switch(simulacao.getModalidadeEnvio()){
            case ECONOMICO -> 1.0;
            case EXPRESSO -> 1.5;
            case PRIORITARIO -> 2.0;
        };

        double taxaRisco = switch(simulacao.getTipoItem()){
            case COMUM -> 0.0;
            case FRAGIL -> 0.10;
            case PERECIVEL -> 0.20;
            case PERIGOSO -> 0.30;
        };

        double subtotal = custoBase * multiplicadorModalidade;
        double valorTaxas = subtotal * taxaRisco;

        simulacao.setTaxasAdicionais(valorTaxas);
        simulacao.setCustoTotal(subtotal + valorTaxas);

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        simulacao.setUsuario(usuario);

        return simulacaoRepository.save(simulacao);
    }

    public Simulacao simularAtraso(Long simulacaoId, int diasAtraso) {
        if(diasAtraso <= 0){
            throw new IllegalArgumentException("Erro: O número de dias de atraso deve ser maior que zero");
        }

        Simulacao simulacao = simulacaoRepository.findById(simulacaoId).orElseThrow(() -> new IllegalArgumentException("Simulação não encontrada"));

        simulacao.setPrazoEstimadoDias(simulacao.getPrazoEstimadoDias() + diasAtraso);

        if(simulacao.getTipoItem() == TipoItem.PERECIVEL && diasAtraso > 2){
            simulacao.setCustoTotal(0.0);
            simulacao.setObservacoes("carga perecível estragada devido ao atraso. Reembolso total.");
        }
        else{
            double desconto = diasAtraso * 0.05;
            if(desconto > 1.0) desconto = 1.0; //Desconto máximo de 100%
            double novoCusto = simulacao.getCustoTotal() * (1.0 - desconto);
            simulacao.setCustoTotal(novoCusto);
            simulacao.setObservacoes("Multa SLA aplicada devido a atraso de " + diasAtraso + " dias.");
        }

        return simulacaoRepository.save(simulacao);
    }

    public List<Simulacao> listarHistorico(Long usuarioId){
        return simulacaoRepository.findByUsuarioId(usuarioId);
    }

    public void deletarSimulacao(Long simulacaoId, Long usuarioId){
        Simulacao simulacao = simulacaoRepository.findById(simulacaoId).orElseThrow(() -> new IllegalArgumentException("Simulação não encontrada"));

        if(!simulacao.getUsuario().getId().equals(usuarioId)){
            throw new SecurityException("Você não tem permissão para deletar esta simulação");
        }

        simulacaoRepository.delete(simulacao);
    }
}
