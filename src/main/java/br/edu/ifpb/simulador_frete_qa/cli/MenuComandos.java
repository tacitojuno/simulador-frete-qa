/*package br.edu.ifpb.sistema_entregas.cli;

import br.edu.ifpb.sistema_entregas.model.ModalidadeEnvio;
import br.edu.ifpb.sistema_entregas.model.Simulacao;
import br.edu.ifpb.sistema_entregas.model.TipoItem;
import br.edu.ifpb.sistema_entregas.model.Usuario;
import br.edu.ifpb.sistema_entregas.service.SimulacaoService;
import br.edu.ifpb.sistema_entregas.service.UsuarioService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class MenuComandos {

    private final UsuarioService usuarioService;
    private final SimulacaoService simulacaoService;

    private Usuario usuarioLogado = null;

    public MenuComandos(UsuarioService usuarioService, SimulacaoService simulacaoService) {
        this.usuarioService = usuarioService;
        this.simulacaoService = simulacaoService;
    }

    @ShellMethod(key = "/cadastrar", value = "Cadastrar um novo usuário")
    public String cadastrar(
            @ShellOption(help = "Seu nome completo") String nome,
            @ShellOption(help = "Seu CPF (apenas números)") String cpf,
            @ShellOption(help = "Seu e-mail") String email,
            @ShellOption(help = "Sua senha") String senha) {
        try {
            Usuario novo = new Usuario(null, nome, cpf, email, senha);
            usuarioService.cadastrar(novo);
            return "Usuário cadastrado com sucesso!";
        } catch (Exception e) {
            return "Erro no cadastro" + e.getMessage();
        }
    }

    @ShellMethod(key = "/login", value = "Fazer login")
    public String login(
            @ShellOption(help = "Seu e-mail") String email,
            @ShellOption(help = "Sua senha") String senha) {
        try {
            usuarioLogado = usuarioService.autenticar(email, senha);
            return "Login bem-sucedido! Bem-vindo, " + usuarioLogado.getNome() + ".";
        } catch (Exception e) {
            return "Erro no login: " + e.getMessage();
        }
    }

    @ShellMethod(key = "/logout", value = "Sair do sistema")
    public String logout() {
        usuarioLogado = null;
        return "Logout realizado com sucesso!";
    }

    @ShellMethod(key = "/nova-simulacao", value = "Criar uma nova simulação de entrega")
    public String novaSimulacao(
            @ShellOption(help = "COMUM, FRAGIL, PERECIVEL ou PERIGOSO") TipoItem tipo,
            @ShellOption(help = "Peso em Kg") Double peso,
            @ShellOption(help = "Cidade de Origem") String origem,
            @ShellOption(help = "Cidade de Destino") String destino,
            @ShellOption(help = "ECONOMICO, EXPRESSO ou PRIORITARIO") ModalidadeEnvio modalidade) {

        if (usuarioLogado == null) return "Você precisa fazer /login primeiro!";

        try {
            Simulacao sim = new Simulacao();
            sim.setTipoItem(tipo);
            sim.setPeso(peso);
            sim.setCidadeOrigem(origem);
            sim.setCidadeDestino(destino);
            sim.setModalidadeEnvio(modalidade);

            Simulacao salva = simulacaoService.criarSimulacao(sim, usuarioLogado.getId());

            return String.format("""
                            SIMULAÇÃO CONCLUÍDA! (ID: %d)
                            ---------------------------------------
                            Origem/Destino: %s -> %s
                            Prazo Estimado: %d dias úteis
                            Taxas de Risco: R$ %.2f
                            Custo Total: R$ %.2f
                            ---------------------------------------""",
                    salva.getId(), salva.getCidadeOrigem(), salva.getCidadeDestino(),
                    salva.getPrazoEstimadoDias(), salva.getTaxasAdicionais(), salva.getCustoTotal());
        } catch (Exception e) {
            return "Erro na simulação: " + e.getMessage();
        }
    }

    @ShellMethod(key = "/simular-atraso", value = "Simular atraso em uma entrega")
    public String simularAtraso(
            @ShellOption(help = "ID da simulação") Long id,
            @ShellOption(help = "Número de dias de atraso") int dias) {

        if (usuarioLogado == null) return "Você precisa fazer /login primeiro!";

        try {
            Simulacao atualizada = simulacaoService.simularAtraso(id, dias);
            return String.format("Atraso processado! Novo prazo estimado: %d dias úteis. Novo custo: R$ %.2f\nObs: %s", atualizada.getPrazoEstimadoDias(), atualizada.getCustoTotal(), atualizada.getObservacoes());
        } catch (Exception e) {
            return "Erro ao simular atraso: " + e.getMessage();
        }
    }

    @ShellMethod(key = "/listar-historico", value = "Listar todas as suas simulações")
    public String listarHistorico(){
        if(usuarioLogado == null) return "Você precisa fazer /login primeiro!";

        List<Simulacao> lista = simulacaoService.listarHistorico(usuarioLogado.getId());
        if(lista.isEmpty()) return "Seu histórico de simulações está vazio.";

        StringBuilder sb = new StringBuilder("SEU HISTÓRICO DE SIMULAÇÕES:\n");
        for(Simulacao s : lista){
            sb.append(String.format("ID: %d | Rota: %s -> %s | Custo: R$ %.2f\n",
                    s.getId(), s.getCidadeOrigem(), s.getCidadeDestino(), s.getCustoTotal()));
        }
        return sb.toString();
    }

    @ShellMethod(key = "/deletar-simulacao", value = "Deletar uma simulação do seu histórico")
    public String deletarSimulacao(@ShellOption(help = "ID da simulação") Long id){
        if(usuarioLogado == null) return "Você precisa fazer /login primeiro!";

        try {
            simulacaoService.deletarSimulacao(id, usuarioLogado.getId());
            return "Simulação deletada com sucesso!";
        } catch (Exception e) {
            return "Erro ao deletar simulação: " + e.getMessage();
        }
    }
}
*/
package br.edu.ifpb.simulador_frete_qa.cli;

import br.edu.ifpb.simulador_frete_qa.model.ModalidadeEnvio;
import br.edu.ifpb.simulador_frete_qa.model.Simulacao;
import br.edu.ifpb.simulador_frete_qa.model.TipoItem;
import br.edu.ifpb.simulador_frete_qa.model.Usuario;
import br.edu.ifpb.simulador_frete_qa.service.SimulacaoService;
import br.edu.ifpb.simulador_frete_qa.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@Profile("!test") //Garantir que este menu só rode fora do ambiente de testes
public class MenuComandos implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final SimulacaoService simulacaoService;
    private Usuario usuarioLogado = null;

    public MenuComandos(UsuarioService usuarioService, SimulacaoService simulacaoService) {
        this.usuarioService = usuarioService;
        this.simulacaoService = simulacaoService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("BEM-VINDO AO SISTEMA DE ENTREGAS!");

        while (true) {
            try {
                System.out.println("\n==============================");
                System.out.println("        MENU PRINCIPAL        ");
                System.out.println("==============================");
                System.out.println("1. Cadastrar Usuário");
                System.out.println("2. Fazer Login");
                System.out.println("3. Nova Simulação de Frete");
                System.out.println("4. Listar Meu Histórico");
                System.out.println("5. Simular Atraso (Multa SLA)");
                System.out.println("6. Excluir Simulação (LGPD)");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao = Integer.parseInt(scanner.nextLine());

                if (opcao == 0) {
                    System.out.println("Encerrando o sistema...");
                    break;
                } else if (opcao == 1) {
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("CPF: ");
                    String cpf = scanner.nextLine();
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();

                    Usuario novo = new Usuario(null, nome, cpf, email, senha);
                    usuarioService.cadastrar(novo);
                    System.out.println("Usuário cadastrado com sucesso!");

                } else if (opcao == 2) {
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();

                    usuarioLogado = usuarioService.autenticar(email, senha);
                    System.out.println("Login realizado! Bem-vindo(a), " + usuarioLogado.getNome());

                } else if (opcao == 3) {
                    if (usuarioLogado == null) {
                        System.out.println("Você precisa fazer login primeiro!");
                        continue;
                    }
                    System.out.print("Tipo (COMUM, FRAGIL, PERECIVEL, PERIGOSO): ");
                    TipoItem tipo = TipoItem.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Peso (em Kg): ");
                    Double peso = Double.parseDouble(scanner.nextLine());

                    System.out.print("Cidade Origem: ");
                    String origem = scanner.nextLine().toUpperCase();

                    System.out.print("Cidade Destino: ");
                    String destino = scanner.nextLine().toUpperCase();

                    System.out.print("Modalidade (ECONOMICO, EXPRESSO, PRIORITARIO): ");
                    ModalidadeEnvio modalidade = ModalidadeEnvio.valueOf(scanner.nextLine().toUpperCase());

                    Simulacao sim = new Simulacao();
                    sim.setTipoItem(tipo);
                    sim.setPeso(peso);
                    sim.setCidadeOrigem(origem);
                    sim.setCidadeDestino(destino);
                    sim.setModalidadeEnvio(modalidade);

                    Simulacao salva = simulacaoService.criarSimulacao(sim, usuarioLogado.getId());

                    System.out.printf("\nSIMULAÇÃO CONCLUÍDA! (ID: %d)\nRota: %s -> %s\nTipo: %s | Modalidade: %s | Peso: %.2f kg\nPrazo: %d dias úteis\nCusto Total: R$ %.2f\n",
                            salva.getId(), salva.getCidadeOrigem(), salva.getCidadeDestino(),
                            salva.getTipoItem(), salva.getModalidadeEnvio(), salva.getPeso(),
                            salva.getPrazoEstimadoDias(), salva.getCustoTotal());

                } else if (opcao == 4) {
                    if (usuarioLogado == null) {
                        System.out.println("Você precisa fazer login primeiro!");
                        continue;
                    }
                    List<Simulacao> lista = simulacaoService.listarHistorico(usuarioLogado.getId());
                    if (lista.isEmpty()) {
                        System.out.println("Seu histórico está vazio.");
                    } else {
                        System.out.println("\nSEU HISTÓRICO DE SIMULAÇÕES:");
                        for (Simulacao s : lista) {
                            System.out.printf("ID: %d | Rota: %s -> %s | Tipo: %s | Modalidade: %s | Peso: %.2f kg | Prazo: %d dias | Custo: R$ %.2f\n",
                                    s.getId(), s.getCidadeOrigem(), s.getCidadeDestino(),
                                    s.getTipoItem(), s.getModalidadeEnvio(), s.getPeso(),
                                    s.getPrazoEstimadoDias(), s.getCustoTotal());
                        }
                    }

                } else if (opcao == 5) {
                    if (usuarioLogado == null) {
                        System.out.println("Você precisa fazer login primeiro!");
                        continue;
                    }
                    System.out.print("Digite o ID da simulação: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    System.out.print("Quantidade de dias de atraso: ");
                    int dias = Integer.parseInt(scanner.nextLine());

                    Simulacao atualizada = simulacaoService.simularAtraso(id, dias);
                    System.out.printf("\nATRASO PROCESSADO!\nNovo Prazo: %d dias\nNovo Custo: R$ %.2f\nObs: %s\n",
                            atualizada.getPrazoEstimadoDias(), atualizada.getCustoTotal(), atualizada.getObservacoes());

                } else if (opcao == 6) {
                    if (usuarioLogado == null) {
                        System.out.println("Você precisa fazer login primeiro!");
                        continue;
                    }
                    System.out.print("Digite o ID da simulação para excluir: ");
                    Long id = Long.parseLong(scanner.nextLine());

                    System.out.println("Tem certeza que deseja excluir a simulação ID " + id + "? (S/N)");
                    String confirmacao = scanner.nextLine();
                    if (confirmacao.equalsIgnoreCase("S")) {
                        simulacaoService.deletarSimulacao(id, usuarioLogado.getId());
                        System.out.println("Simulação excluída com sucesso do seu histórico!");
                    } else {
                        System.out.println("Exclusão cancelada.");
                    }
                } else {
                    System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }
        scanner.close();
        System.exit(0);
    }
}