import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestesAceitacaoTest {

    record Jogada(Domino p1, Domino p2, String lado, int pae, int pad, boolean esperado) {}
    record FimDeJogoCaso(boolean jogadorInicial, Domino pecaMesa, Domino pecaJogador1,
                         Domino pecaJogador2, String ladoJogada, int vencedorEsperado) {}

    static Stream<Jogada> jogadas() {
        return Stream.of(
                new Jogada(new Domino(2, 5), new Domino(0, 0), "e", 5, 1, true),
                new Jogada(new Domino(4, 3), new Domino(1, 2), "e", 6, 1, false),
                new Jogada(new Domino(1, 3), new Domino(0, 6), "d", 5, 3, true),
                new Jogada(new Domino(2, 2), new Domino(4, 1), "d", 1, 6, false)
        );
    }

    static Stream<FimDeJogoCaso> casosDeFimDeJogo() {
        return Stream.of(
                new FimDeJogoCaso(true, new Domino(0, 0), new Domino(0, 5), new Domino(3, 0), "d", 1),
                new FimDeJogoCaso(false, new Domino(0, 0), new Domino(0, 5), new Domino(3, 0), "e", 2)
        );
    }

    //RF05 – Verificação de Jogadas Válidas :
    //∀ peça(x, y) ∈ Mão(p), ∃ E ∈ {EE, ED} | x = E ∨ y = E ⇒ JogadaVálida(peça(x, y), E)
    @ParameterizedTest
    @MethodSource("jogadas")
    void testJogadaValidaOuInvalida(Jogada jogada) {
        Jogo jogo = new Jogo();
        jogo.getJogador1().setMao(new ArrayList<>(List.of(jogada.p1(), jogada.p2())));
        jogo.getJogador2().setMao(new ArrayList<>(List.of(jogada.p1(), jogada.p2())));
        jogo.setPAE(jogada.pae());
        jogo.setPAD(jogada.pad());
        jogo.setVez_jogador(true);

        boolean vezAntes = jogo.isVez_jogador();
        jogo.jogar_peca(0, jogada.lado());
        boolean vezDepois = jogo.isVez_jogador();

        boolean jogadaValida = vezAntes != vezDepois;

        assertEquals(jogada.esperado(), jogadaValida, "Resultado da jogada diferente do esperado");
    }

    //RF06 – Fim de Jogo por Vitória :
    //∃ p ∈ Jogadores | Mão(p) = ∅ ⇒ FimDeJogo ∧ Vencedor = p
    //RF07 – Fim de Jogo por Travamento :
    //¬∃ p ∈ Jogadores, peça(x, y) ∈ Mão(p) | JogadaVálida(peça(x, y)) ⇒ FimDeJogo ∧ Resultado = "Travado"
    //RF08 – Exibição do Resultado Final :
    //FimDeJogo ⇒ Mostrar(ResultadoFinal(Vencedor, Pontuação))
    @ParameterizedTest
    @MethodSource("casosDeFimDeJogo")
    void testFimDeJogoPorVitoria(FimDeJogoCaso caso) {
        Jogo jogo = new Jogo();

        jogo.getJogador1().getMao().clear();
        jogo.getJogador2().getMao().clear();
        jogo.pecas_mesa.clear();

        jogo.pecas_mesa.add(caso.pecaMesa());
        jogo.PAE = caso.pecaMesa().get_e();
        jogo.PAD = caso.pecaMesa().get_d();

        jogo.getJogador1().getMao().add(caso.pecaJogador1());
        jogo.getJogador2().getMao().add(caso.pecaJogador2());

        jogo.getJogador1().getMao().add(new Domino(6, 6));
        jogo.getJogador2().getMao().add(new Domino(5, 5));
        jogo.getJogador1().getMao().remove(1);
        jogo.getJogador2().getMao().remove(1);

        jogo.setVez_jogador(caso.jogadorInicial());

        boolean fim = jogo.jogar_peca(0, caso.ladoJogada());

        assertTrue(fim, "O jogo deveria terminar após a jogada válida.");

        boolean jogador1Venceu = jogo.getJogador1().getMao().isEmpty();
        boolean jogador2Venceu = jogo.getJogador2().getMao().isEmpty();

        if (caso.vencedorEsperado() == 1) {
            assertTrue(jogador1Venceu, "Jogador 1 deveria vencer.");
        } else {
            assertTrue(jogador2Venceu, "Jogador 2 deveria vencer.");
        }
    }

    //RF02 – Distribuição Aleatória de Peças :
    //∀ p ∈ Jogadores ⇒ |Mão(p)| = 7 ∧ DistribuiçãoAleatória(Mão(p))
    @Test
    void testDistribuicaoAleatoriaDasMaos() {
        Jogo j1 = new Jogo();
        Jogo j2 = new Jogo();

        j1.comecar_jogo();
        j2.comecar_jogo();

        String mao1 = formatarMao(j1.getJogador1().getMao());
        String mao2 = formatarMao(j2.getJogador2().getMao());

        assertNotEquals(mao1, mao2, "As mãos dos dois jogos foram iguais — distribuição não parece aleatória.");
    }

    //RF01 – Geração do Conjunto de Peças :
    //∀ x, y ∈ ℕ | 0 ≤ x ≤ 6 ∧ 0 ≤ y ≤ 6 ∧ x ≤ y ⇒ peça(x, y) ∈ ConjuntoPeças
    @Test
    void testGeracaoDe28Pecas() {
        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        int total = jogo.getJogador1().getMao().size() + jogo.getJogador2().getMao().size() + jogo.getPecasRestantes().size();

        assertEquals(27, total, "Total de peças deve ser 27 após iniciar o jogo (uma é jogada automaticamente).");
    }

    //RF03 – Identificação do Jogador Inicial :
    //∀ p ∈ Jogadores, ∃ peça(x, x) ∈ Mão(p) | x = max{a ∈ [0,6] | peça(a, a) ∈ Mão(p)} ⇒ Começa(p)
    @Test
    void testIdentificacaoJogadorInicial() {
        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        int p1 = jogo.getJogador1().getMao().size();
        int p2 = jogo.getJogador2().getMao().size();

        boolean p1Comecou = p1 == 6;
        boolean p2Comecou = p2 == 6;

        assertTrue(p1Comecou || p2Comecou, "Nenhum jogador parece ter começado o jogo corretamente.");
        assertNotEquals(p1, p2, "Ambos os jogadores têm o mesmo número de peças.");
    }

    //RF04 – Exibição das Peças do Jogador :
    //∀ p ∈ Jogadores ⇒ Mostrar(Mão(p)) = ListaOrdenada(peça(x, y))
    @Test
    void testExibicaoVisualDaMao() {
        Jogo jogo = new Jogo();
        jogo.comecar_jogo();

        String maoFormatada = formatarMao(jogo.getJogador1().getMao());
        String[] pecas = maoFormatada.split("\\s+");

        for (String peca : pecas) {
            assertTrue(peca.matches("\\[\\d\\|\\d]"), "Peça com formato inválido: " + peca);
        }

        assertEquals(5, pecas.length, "Quantidade incorreta de peças na mão");
        assertTrue(maoFormatada.contains("["), "Formato não contém colchetes de peça");
        assertTrue(maoFormatada.contains("|"), "Formato não contém barra central");
    }

    private static String formatarMao(List<Domino> mao) {
        StringBuilder sb = new StringBuilder();
        for (Domino domino : mao) {
            sb.append(domino).append(" ");
        }
        return sb.toString().trim();
    }
}
