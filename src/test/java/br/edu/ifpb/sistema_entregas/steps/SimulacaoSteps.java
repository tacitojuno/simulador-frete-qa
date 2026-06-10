package br.edu.ifpb.sistema_entregas.steps;

import br.edu.ifpb.sistema_entregas.SistemaEntregasApplication;
import br.edu.ifpb.sistema_entregas.model.ModalidadeEnvio;
import br.edu.ifpb.sistema_entregas.model.Simulacao;
import br.edu.ifpb.sistema_entregas.model.TipoItem;
import br.edu.ifpb.sistema_entregas.model.Usuario;
import br.edu.ifpb.sistema_entregas.service.SimulacaoService;
import br.edu.ifpb.sistema_entregas.service.UsuarioService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(classes = SistemaEntregasApplication.class)
@ActiveProfiles("test") //Garante que o perfil de teste seja usado, isolando o ambiente de produção
public class SimulacaoSteps {

    // 1. Injetamos os serviços reais do seu sistema
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SimulacaoService simulacaoService;

    // 2. Variáveis "fantasmas" para carregar as informações de um passo para o outro
    private Usuario usuarioLogado;
    private Simulacao simulacaoBase;
    private Simulacao simulacaoSalva;

    @Dado("que eu tenho um usuário autenticado no sistema")
    public void que_eu_tenho_um_usuario_autenticado_no_sistema() {
        // Criamos um usuário fake no banco apenas para o teste rodar
        try {
            Usuario novo = new Usuario(null, "Testador", "11122233344", "teste@email.com", "senha123");
            usuarioService.cadastrar(novo);
        } catch (Exception e) {
            // Se o usuário já existir no banco de testes, apenas ignoramos
        }

        // Fazemos o login dele!
        usuarioLogado = usuarioService.autenticar("teste@email.com", "senha123");

        // Preparamos uma ficha de simulação em branco para os próximos passos preencherem
        simulacaoBase = new Simulacao();
    }

    @Quando("eu solicito uma simulação para um item {string} com peso de {double} kg")
    public void eu_solicito_uma_simulacao_para_um_item_com_peso_de_kg(String tipo, Double peso) {
        simulacaoBase.setTipoItem(TipoItem.valueOf(tipo));
        simulacaoBase.setPeso(peso);
    }

    @E("a origem é {string} e o destino é {string}")
    public void a_origem_e_o_destino(String origem, String destino) {
        simulacaoBase.setCidadeOrigem(origem);
        simulacaoBase.setCidadeDestino(destino);
    }

    @E("a modalidade escolhida é {string}")
    public void a_modalidade_escolhida_e(String modalidade) {
        simulacaoBase.setModalidadeEnvio(ModalidadeEnvio.valueOf(modalidade));
    }

    @Então("a simulação deve ser criada com sucesso")
    public void a_simulacao_deve_ser_criada_com_sucesso() {
        // O momento da verdade! O teste manda o sistema salvar.
        simulacaoSalva = simulacaoService.criarSimulacao(simulacaoBase, usuarioLogado.getId());

        // O JUnit verifica se realmente salvou (o ID não pode ser nulo)
        assertNotNull(simulacaoSalva.getId(), "A simulação não foi salva no banco de dados!");
    }

    @E("o custo total do frete deve ser {double}")
    public void o_custo_total_do_frete_deve_ser(Double custoEsperado) {
        // O Cucumber pega o número que você digitou no texto (ex: 77.025) e joga aqui!
        // O 0.01 no final é uma margem de tolerância para casas decimais.
        assertEquals(custoEsperado, simulacaoSalva.getCustoTotal(), 0.01, "O custo total não bateu com a regra de negócio!");
    }

    @E("o prazo estimado deve ser de {int} dias")
    public void o_prazo_estimado_deve_ser_de_dias(Integer prazoEsperado) {
        // O Cucumber pega o número de dias do texto e verifica
        assertEquals(prazoEsperado, simulacaoSalva.getPrazoEstimadoDias(), "O prazo estimado em dias está incorreto!");
    }

    @Então("o sistema deve falhar com a mensagem de erro {string}")
    public void o_sistema_deve_falhar_com_a_mensagem_de_erro(String mensagemEsperada) {
        // O assertThrows captura a explosão de erro do sistema ao invés de deixar quebrar
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            simulacaoService.criarSimulacao(simulacaoBase, usuarioLogado.getId());
        });

        // Verifica se a mensagem de erro do sistema é idêntica à do seu arquivo feature
        assertEquals(mensagemEsperada, exception.getMessage(), "A mensagem de erro retornada foi diferente da esperada!");
    }
}