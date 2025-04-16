import java.util.ArrayList;
import java.util.Scanner;

public class TestesAceitacao {

    public static void executarTestesDeAceitacao() {

        Scanner input = new Scanner(System.in);
        int escolha;

        do {
            System.out.println("\n===== MENU DE TESTES DE ACEITAÇÃO =====");
            System.out.println("1. Teste: Distribuição Aleatória");
            System.out.println("2. Teste: Geração de 28 Peças");
            System.out.println("3. Teste: Identificação do Jogador Inicial");
            System.out.println("4. Teste: Exibição Visual da Mão");
            System.out.println("5. Teste: Jogada Válida");
            System.out.println("6. Teste: Fim de Jogo por Vitória");
            System.out.println("0. Voltar ao menu principal");
            System.out.print("Escolha um teste para executar: ");

            try {
                escolha = Integer.parseInt(input.nextLine());
            } catch (Exception e) {
                escolha = -1;
            }

            System.out.println();
            switch (escolha) {
                case 1 -> testeDistribuicaoAleatoria();
                case 2 -> testeGeracao28Pecas();
                case 3 -> testeIdentificacaoJogadorInicial();
                case 4 -> testeExibicaoPecas();
                case 5 -> testeJogadaValida();
                case 6 -> testeFimDeJogo();
                case 0 -> System.out.println("Retornando ao menu principal...");
                default -> System.out.println("Opção inválida! Tente novamente.\n");
            }

            if (escolha != 0) {
                System.out.println("Pressione ENTER para continuar...");
                input.nextLine();
            }

        } while (escolha != 0);
    }

    private static void testeDistribuicaoAleatoria() {
        System.out.println("▶ Teste: Distribuição Aleatória");
        Jogo j1 = new Jogo();
        Jogo j2 = new Jogo();
        j1.comecar_jogo();
        j2.comecar_jogo();

        String m1 = formatarMao(j1.getJogador1().getMao());
        String m2 = formatarMao(j2.getJogador1().getMao());

        System.out.println("\nMão do jogador 1 no jogo 1:\n" + m1);
        System.out.println("Mão do jogador 1 no jogo 2:\n" + m2);

        boolean diferente = !m1.equals(m2);
        System.out.println("Resultado: " + (diferente ? "Passou" : "Falhou"));
        System.out.println("--------------------------------------------------\n");
    }

    private static void testeGeracao28Pecas() {
        System.out.println("Teste: Geração do Conjunto de 28 Peças");
        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        int total = jogo.getJogador1().getMao().size() +
                jogo.getJogador2().getMao().size() +
                jogo.getPecasRestantes().size();

        if (total == 27) {
            System.out.println("PASSOU: Total de peças é 28.\n");
        } else {
            System.out.println("FALHOU: Total de peças é " + (total + 1) + " (esperado: 28).\n");
        }
    }

    private static void testeIdentificacaoJogadorInicial() {
        System.out.println("▶ Teste: Identificação do Jogador Inicial");

        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        boolean p1Jogou = jogo.getJogador1().getMao().size() == 5;
        boolean p2Jogou = jogo.getJogador2().getMao().size() == 5;

        System.out.println("Mão do Jogador 1: " + jogo.getJogador1().getMao().size() + " peças");
        System.out.println("Mão do Jogador 2: " + jogo.getJogador2().getMao().size() + " peças");
        System.out.println("Jogador que começou: " + (p1Jogou ? "P1" : p2Jogou ? "P2" : "Não identificado"));

        System.out.println("Resultado: " + ((p1Jogou || p2Jogou) ? "Passou" : "Falhou"));
        System.out.println("--------------------------------------------------\n");
    }

    private static void testeExibicaoPecas() {
        System.out.println("▶ Teste: Exibição Visual da Mão");

        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        String maoFormatada = formatarMao(jogo.getJogador1().getMao());
        System.out.println("Visual da mão formatada:\n" + maoFormatada);

        boolean visual = maoFormatada.contains("[") && maoFormatada.contains("|");
        System.out.println("Resultado: " + (visual ? "Passou" : "Falhou"));
        System.out.println("--------------------------------------------------\n");
    }

    private static void testeJogadaValida() {
        System.out.println("Teste: Verificação de Jogadas Válidas");
        System.out.println("O objetivo deste teste é tentar jogar uma peça válida.\n");

        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        Scanner input = new Scanner(System.in);

        while (true) {
            ArrayList<Domino> mao = jogo.getJogador1().getMao();
            System.out.println("Peças do jogador (P1):");
            for (int i = 0; i < mao.size(); i++) {
                System.out.print("(" + i + ") = " + mao.get(i) + "  ");
            }

            System.out.println("\n\nExtremidades da mesa:");
            System.out.println("Esquerda: [" + jogo.getPAE() + "] | Direita: [" + jogo.getPAD() + "]");

            System.out.print("\nDigite o índice da peça para jogar: ");
            int indice;
            try {
                indice = Integer.parseInt(input.nextLine());
            } catch (Exception e) {
                System.out.println("Entrada inválida. Tente novamente.\n");
                continue;
            }

            System.out.print("Escolha o lado ('e' para esquerda, 'd' para direita): ");
            String lado = input.nextLine();

            boolean fim = jogo.jogar_peca(indice, lado);

            if (fim) {
                System.out.println("O jogo terminou nesta jogada.\n");
                break;
            }

            if (!jogo.isVez_jogador()) {
                System.out.println("Jogada válida realizada com sucesso! Teste passou.\n");
                break;
            } else {
                System.out.println("Jogada inválida. Tente novamente.\n");
            }
        }
    }

    private static void testeFimDeJogo() {
        System.out.println("Teste: Fim do Jogo por Vitória");
        System.out.println("Configuração: Peça inicial [0|0], cada jogador com uma peça contendo 0.\n");

        Jogo jogo = new Jogo();

        // Resetando estado do jogo
        jogo.getJogador1().getMao().clear();
        jogo.getJogador2().getMao().clear();
        jogo.getPecasRestantes().clear();
        jogo.getPecasMesa().clear();

        // Peça inicial
        Domino pecaInicial = new Domino(0, 0);
        jogo.getPecasMesa().add(pecaInicial);
        jogo.setPAE(0);
        jogo.setPAD(0);
        jogo.setVez_jogador(true); // P1 começa

        // Distribuição simulada
        jogo.getJogador1().getMao().add(new Domino(0, 5));
        jogo.getJogador2().getMao().add(new Domino(3, 0));

        System.out.println("Estado Inicial:");
        System.out.println("Peça na mesa: [0|0]");
        System.out.println("Jogador 1 tem: [0|5]");
        System.out.println("Jogador 2 tem: [3|0]");
        System.out.println("Jogador 1 jogará sua única peça...\n");

        // Jogada final
        boolean fim = jogo.jogar_peca(0, "d");

        System.out.println("Jogo terminou: " + (fim ? "SIM" : "NÃO"));
        System.out.println("--------------------------------------------------\n");
    }

    private static String formatarMao(ArrayList<Domino> mao) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < mao.size(); i++) {
            output.append(String.format("(%d) = %s", i, mao.get(i)));
            if ((i + 1) % 3 == 0 || i == mao.size() - 1) {
                output.append("\n");
            } else {
                output.append(" | ");
            }
        }

        return output.toString();
    }
}
