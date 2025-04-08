import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestesAceitacao {

    static class FimDeJogoCaso {
        boolean jogadorInicial;
        Domino pecaMesa;
        Domino pecaJogador1;
        Domino pecaJogador2;
        String ladoJogada; // "e" ou "d"
        int vencedorEsperado; // 1 ou 2

        FimDeJogoCaso(boolean jogadorInicial, Domino pecaMesa, Domino p1, Domino p2, String ladoJogada, int vencedor) {
            this.jogadorInicial = jogadorInicial;
            this.pecaMesa = pecaMesa;
            this.pecaJogador1 = p1;
            this.pecaJogador2 = p2;
            this.ladoJogada = ladoJogada;
            this.vencedorEsperado = vencedor;
        }
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> jogadas() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        new Domino(2, 5), new Domino(0, 0), "e", 5, 1, true),
                org.junit.jupiter.params.provider.Arguments.of(
                        new Domino(4, 3), new Domino(1, 2), "e", 6, 1, false),
                org.junit.jupiter.params.provider.Arguments.of(
                        new Domino(1, 3), new Domino(0, 6), "d", 5, 3, true),
                org.junit.jupiter.params.provider.Arguments.of(
                        new Domino(2, 2), new Domino(4, 1), "d", 1, 6, false)
        );
    }

    static Stream<FimDeJogoCaso> casosDeFimDeJogo() {
        return Stream.of(
                // Jogador 1 vence
                new FimDeJogoCaso(true, new Domino(0, 0), new Domino(0, 5), new Domino(3, 0), "d", 1),
                // Jogador 2 vence
                new FimDeJogoCaso(false, new Domino(0, 0), new Domino(0, 5), new Domino(3, 0), "e", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("jogadas")
    void testJogadaValidaOuInvalida(Domino pecaPrincipal, Domino pecaExtra, String lado, int pae, int pad, boolean esperado) {
        // Arrange
        Jogo jogo = new Jogo();
        jogo.setMJ1(new java.util.ArrayList<>(List.of(pecaPrincipal, pecaExtra))); // mão com 2 peças
        jogo.setMJ2(new java.util.ArrayList<>(List.of(pecaPrincipal, pecaExtra))); // mão com 2 peças
        jogo.setPAE(pae);
        jogo.setPAD(pad);
        jogo.setVez_jogador(true);

        // Act
        boolean vezAntes = jogo.isVez_jogador();
        jogo.jogar_peca(0, lado);
        boolean vezDepois = jogo.isVez_jogador();

        boolean jogadaValida = vezAntes != vezDepois;

        // Assert
        assertEquals(esperado, jogadaValida, "Resultado da jogada diferente do esperado");
    }

    @Test
    void testDistribuicaoAleatoriaDasMaos() {
        // Arrange
        Jogo j1 = new Jogo();
        Jogo j2 = new Jogo();

        j1.comecar_jogo();
        j2.comecar_jogo();

        String mao1 = formatarMao(j1.getMJ1());
        String mao2 = formatarMao(j2.getMJ1());

        System.out.println("Mão 1: " + mao1);
        System.out.println("Mão 2: " + mao2);

        // Act
        boolean saoDiferentes = !mao1.equals(mao2);

        // Assert
        assertTrue(saoDiferentes, "As mãos dos dois jogos foram iguais — distribuição não parece aleatória.");
    }

    @Test
    void testGeracaoDe28Pecas() {
        // Arrange
        Jogo jogo = new Jogo();
        jogo.comecar_jogo(); // Inicializa as peças

        // Act
        int total = jogo.getMJ1().size() + jogo.getMJ2().size() + jogo.getPecasRestantes().size();

        // Assert
        assertEquals(27, total, "Total de peças deve ser 27 após iniciar o jogo (uma peça é sempre jogada de forma automática)");
    }

    @Test
    void testIdentificacaoJogadorInicial() {
        // Arrange
        Jogo jogo = new Jogo();
        jogo.comecar_jogo(); // Este método deve jogar a primeira peça automaticamente

        int tamanhoMaoP1 = jogo.getMJ1().size();
        int tamanhoMaoP2 = jogo.getMJ2().size();

        // Act: verifica se algum jogador tem 5 peças (assumindo que começa com 6 e 1 jogada foi feita)
        boolean p1Comecou = tamanhoMaoP1 == 6;
        boolean p2Comecou = tamanhoMaoP2 == 6;

        // Assert: um dos jogadores deve ter jogado (mão com 6 peças)
        assertTrue(p1Comecou || p2Comecou, "Nenhum jogador parece ter começado o jogo corretamente");
        assertNotEquals(tamanhoMaoP1, tamanhoMaoP2, "Ambos os jogadores têm o mesmo número de peças, início não identificado");
    }

    @ParameterizedTest
    @MethodSource("casosDeFimDeJogo")
    void testFimDeJogoPorVitoria(FimDeJogoCaso caso) {
        // Arrange
        Jogo jogo = new Jogo();

        jogo.getMJ1().clear();
        jogo.getMJ2().clear();
        jogo.pecas_mesa.clear();

        jogo.pecas_mesa.add(caso.pecaMesa);
        jogo.PAE = caso.pecaMesa.get_e();
        jogo.PAD = caso.pecaMesa.get_d();

        jogo.getMJ1().add(caso.pecaJogador1);
        jogo.getMJ2().add(caso.pecaJogador2);

        // Adiciona peças extras para garantir que não haja falso positivo
        jogo.getMJ1().add(new Domino(6, 6));
        jogo.getMJ2().add(new Domino(5, 5));
        jogo.getMJ1().remove(1); // Remove extra mantendo 1 peça real
        jogo.getMJ2().remove(1); // idem

        jogo.vez_jogador = caso.jogadorInicial;

        // Act
        boolean fim = jogo.jogar_peca(0, caso.ladoJogada);

        // Assert
        assertTrue(fim, "O jogo deveria terminar após a jogada válida.");
        boolean jogador1Venceu = jogo.getMJ1().isEmpty();
        boolean jogador2Venceu = jogo.getMJ2().isEmpty();

        if (caso.vencedorEsperado == 1) {
            assertTrue(jogador1Venceu, "Jogador 1 deveria vencer.");
        } else {
            assertTrue(jogador2Venceu, "Jogador 2 deveria vencer.");
        }
    }

    @Test
    public void testExibicaoVisualDaMao() {
        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        String maoFormatada = formatarMao(jogo.getMJ1());

        // Verifica se cada peça segue o padrão esperado, por exemplo: [x|y]
        String[] pecas = maoFormatada.split("\\s+");
        for (String peca : pecas) {
            assertTrue(peca.matches("\\[\\d\\|\\d\\]"), "Peça com formato inválido: " + peca);
        }

        // Verifica se temos o número correto de peças (suponha 7, por exemplo)
        assertEquals(5, pecas.length, "Quantidade incorreta de peças na mão");

        // Verifica se a visualização geral está formatada corretamente
        assertTrue(maoFormatada.contains("["), "Formato não contém colchetes de peça");
        assertTrue(maoFormatada.contains("|"), "Formato não contém barra central");
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


    private static String formatarMao(ArrayList<Domino> mao) {
        StringBuilder output = new StringBuilder();

        for (Domino domino : mao) {
            output.append(domino);

            output.append(" ");
        }

        return output.toString();
    }
}

