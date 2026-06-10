package br.edu.ifpb.simulador_frete_qa.service;

import br.edu.ifpb.simulador_frete_qa.model.Usuario;
import br.edu.ifpb.simulador_frete_qa.model.Simulacao;
import br.edu.ifpb.simulador_frete_qa.model.TipoItem;
import br.edu.ifpb.simulador_frete_qa.repository.SimulacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimulacaoServiceTest {

    @Mock
    private SimulacaoRepository simulacaoRepository;

    @InjectMocks
    private SimulacaoService simulacaoService;

    private Simulacao simulacaoBase;

    @BeforeEach
    void setUp() {
        //Preparando simulação base para os testes
        simulacaoBase = new Simulacao();
        simulacaoBase.setId(1L);
        simulacaoBase.setTipoItem(TipoItem.COMUM);
        simulacaoBase.setPrazoEstimadoDias(5);
        simulacaoBase.setCustoTotal(100.0);
    }

    //CT005
    @Test
    void deveAplicarMultaSLAQuandoHouverAtrasoComum_CT005() {
        //Encontra a simulação base no banco e simula o salvamento após a modificação.
        when(simulacaoRepository.findById(1L)).thenReturn(Optional.of(simulacaoBase));
        when(simulacaoRepository.save(any(Simulacao.class))).thenReturn(simulacaoBase);

        Simulacao resultado = simulacaoService.simularAtraso(1L, 2);

        //Verificações: O prazo vai para 7 e o custo cai 10% (indo para 90.0)
        assertEquals(7, resultado.getPrazoEstimadoDias());
        assertEquals(90.0, resultado.getCustoTotal());
        assertTrue(resultado.getObservacoes().contains("Multa SLA"));
    }

    //CT006
    @Test
    void deveZerarCustoQuandoAtrasoCargaPerecivelPassarDoLimite_CT006() {
        simulacaoBase.setTipoItem(TipoItem.PERECIVEL);
        simulacaoBase.setCustoTotal(200.0);
        simulacaoBase.setPrazoEstimadoDias(3);

        when(simulacaoRepository.findById(1L)).thenReturn(Optional.of(simulacaoBase));
        when(simulacaoRepository.save(any(Simulacao.class))).thenReturn(simulacaoBase);

        Simulacao resultado = simulacaoService.simularAtraso(1L, 3);

        //Verificações: Custo zerado e observação de reembolso
        assertEquals(0.0, resultado.getCustoTotal());
        assertTrue(resultado.getObservacoes().contains("carga perecível estragada"));
    }

    //CT007
    @Test
    void deveLancarExcecaoQuandoDiasDeAtrasoForNegativo_CT007() {
        //Ação & Verificação: O sistema deve explodir a exceção correta
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            simulacaoService.simularAtraso(1L, -1);
        });

        assertEquals("Erro: O número de dias de atraso deve ser maior que zero", exception.getMessage());

        //Garante que o banco de dados nunca foi acionado para salvar lixo
        verify(simulacaoRepository, never()).save(any());
    }

    //CT012
    @Test
    void deveListarHistoricoDoUsuario_CT012() {
        //Criação de lista falsa d
        java.util.List<Simulacao> listaFalsa = java.util.List.of(simulacaoBase, new Simulacao());
        when(simulacaoRepository.findByUsuarioId(1L)).thenReturn(listaFalsa);

        //Ação: Buscar histórico
        java.util.List<Simulacao> resultado = simulacaoService.listarHistorico(1L);

        //Verificações (Vendo se retorna uma lista com mais de uma simulação)
        assertEquals(2, resultado.size(), "A lista deve conter as 2 simulações do usuário");
        verify(simulacaoRepository, times(1)).findByUsuarioId(1L);
    }

    //CT013
    @Test
    void deveExcluirSimulacaoDoHistorico_CT013() {
        Usuario usuarioDono = new Usuario();
        usuarioDono.setId(1L);
        simulacaoBase.setUsuario(usuarioDono);

        //Simulando que encontrou a simulação ID 5 no banco
        when(simulacaoRepository.findById(5L)).thenReturn(Optional.of(simulacaoBase));

        //Ação: Deletar
        simulacaoService.deletarSimulacao(5L, 1L);

        //Verificação: Garante que o método delete() do banco foi acionado exatamente 1 vez
        verify(simulacaoRepository, times(1)).delete(simulacaoBase);
    }

    //CT014
    @Test
    void deveLancarExcecaoAoExcluirSimulacaoInexistente_CT014() {
        //Simulando que o banco NÃO encontrou a simulação ID 99
        when(simulacaoRepository.findById(99L)).thenReturn(Optional.empty());

        //Ação & Verificação: Deve estourar erro
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            simulacaoService.deletarSimulacao(99L, 1L);
        });

        assertEquals("Simulação não encontrada", exception.getMessage());

        //Garante que o banco nunca tentou deletar nada
        verify(simulacaoRepository, never()).delete(any());
    }
}