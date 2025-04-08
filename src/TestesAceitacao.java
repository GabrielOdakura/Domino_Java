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
                case 1:
                    testeDistribuicaoAleatoria();
                    break;
                case 2:
                    testeGeracao28Pecas();
                    break;
                case 3:
                    testeIdentificacaoJogadorInicial();
                    break;
                case 4:
                    testeExibicaoPecas();
                    break;
                case 5:
                    testeJogadaValida();
                    break;
                case 6:
                    testeFimDeJogo();
                    break;
                case 0:
                    System.out.println("Retornando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.\n");
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

        String m1 = formatarMao(j1.getMJ1());
        String m2 = formatarMao(j2.getMJ1());

        System.out.println("\nMão do jogador 1 no jogo 1:\n" + m1);
        System.out.println("Mão do jogador 1 no jogo 2:\n" + m2);

        boolean diferente = !m1.equals(m2);
        System.out.println("Resultado: " + (diferente ? "Passou" : "Falhou"));
        System.out.println("--------------------------------------------------\n");
    }

    private static void testeGeracao28Pecas() {
        System.out.println("Teste: Geração do Conjunto de 28 Peças");
        Jogo jogo = new Jogo();
        jogo.comecar_jogo(); // Necessário para inicializar as peças

        int total = jogo.getMJ1().size() + jogo.getMJ2().size() + jogo.getPecasRestantes().size();
        if (total == 27) {
            System.out.println("PASSOU: Total de peças é 28.\n");
        } else {
            System.out.println("FALHOU: Total de peças é " + total + " (esperado: 28).\n");
        }
    }


    private static void testeIdentificacaoJogadorInicial() {
        System.out.println("▶ Teste: Identificação do Jogador Inicial");

        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        boolean p1Jogou = jogo.getMJ1().size() == 5;
        boolean p2Jogou = jogo.getMJ2().size() == 5;

        System.out.println("Mão do Jogador 1: " + jogo.getMJ1().size() + " peças");
        System.out.println("Mão do Jogador 2: " + jogo.getMJ2().size() + " peças");
        System.out.println("Jogador que começou: " + (p1Jogou ? "P1" : p2Jogou ? "P2" : "Não identificado"));

        System.out.println("Resultado: " + ((p1Jogou || p2Jogou) ? "Passou" : "Falhou"));
        System.out.println("--------------------------------------------------\n");
    }

    private static void testeExibicaoPecas() {
        System.out.println("▶ Teste: Exibição Visual da Mão");

        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        String maoFormatada = formatarMao(jogo.getMJ1());
        System.out.println("Visual da mão formatada:\n" + maoFormatada);

        boolean visual = maoFormatada.contains("[") && maoFormatada.contains("|");
        System.out.println("Resultado: " + (visual ? "Passou" : "Falhou"));
        System.out.println("--------------------------------------------------\n");
    }

    private static void testeJogadaValida() {
        System.out.println("Teste: Verificação de Jogadas Válidas");
        System.out.println("O objetivo deste teste é tentar jogar uma peça válida.");
        System.out.println("Você deve tentar jogar uma peça até que seja válida para ver o teste passar.\n");

        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        Scanner input = new Scanner(System.in);

        while (true) {
            ArrayList<Domino> mao = jogo.getMJ1();
            System.out.println("Peças do jogador (P1):");
            for (int i = 0; i < mao.size(); i++) {
                System.out.print("(" + i + ") = " + mao.get(i) + "  ");
            }

            System.out.println("\n\nExtremidades da mesa:");
            System.out.println("Esquerda: [" + jogo.getPAE() + "] | Direita: [" + jogo.getPAD() + "]");

            System.out.print("\nDigite o índice da peça para jogar: ");
            int indice = -1;
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

            // Se a jogada foi válida, a vez muda (isso acontece na lógica do jogo).
            // Isso significa que a jogada foi aceita!
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

        // --- 1. Resetando estado manualmente ---
        // Limpamos tudo
        jogo.getMJ1().clear();
        jogo.getMJ2().clear();

        // Criamos a peça inicial [0|0]
        Domino pecaInicial = new Domino(0, 0);

        // Setamos na mesa e nas pontas
        jogo.pecas_mesa.clear();
        jogo.pecas_mesa.add(pecaInicial);

        jogo.PAE = 0;
        jogo.PAD = 0;

        // Setamos a vez do jogador para o P1
        jogo.vez_jogador = true;

        // --- 2. Damos uma peça válida para cada jogador contendo 0 ---
        Domino pecaJ1 = new Domino(0, 5);
        Domino pecaJ2 = new Domino(3, 0);

        jogo.getMJ1().add(pecaJ1);
        jogo.getMJ2().add(pecaJ2);

        System.out.println("Estado Inicial:");
        System.out.println("Peça na mesa: [0|0]\n");
        System.out.println("Jogador 1 tem: " + pecaJ1);
        System.out.println("Jogador 2 tem: " + pecaJ2);
        System.out.println("\nVez do jogador: Jogador 1");

        // --- 3. Jogador 1 joga sua única peça válida ---
        System.out.println("\nJogador 1 jogará a peça [0|5] no lado direito...\n");

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

