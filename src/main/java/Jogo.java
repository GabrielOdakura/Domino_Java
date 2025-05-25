import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Jogo {

    // lista contendo todos os dominos
    private ArrayList<Domino> dominos = new ArrayList<>();

    // jogadores da partida
    private Jogador jogador1 = new Jogador(1);
    private Jogador jogador2 = new Jogador(2);

    // peças que já foram jogadas e estão sobre a mesa
    ArrayList<Domino> pecas_mesa = new ArrayList<>();

    // determina a vez do jogador. true = P1 | false = P2
    private boolean vez_jogador = true;

    // evita que o jogador compre mais de uma vez no mesmo turno
    private boolean ja_comprou = false;

    // peças restantes para comprar
    private ArrayList<Domino> pecas_restantes;

    // valores nas pontas da mesa (esquerda e direita)
    public int PAE;
    public int PAD;

    // 0 = não iniciada | 1 = em andamento | 2 = finalizado | 3 = empate
    private int estado_partida = 0;

    // índice 0 corresponde a jogadasValidas P1 e 1 a P2
    private int[] jogadasValidas = new int[2];

    // tempo médio de jogada
    private long tempoInicioJogada;
    private long tempoTotalP1 = 0;
    private long tempoTotalP2 = 0;

    public Jogo() {
        // inicialização das peças (28 peças de 0 a 6)
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                dominos.add(new Domino(i, j));
            }
        }
    }

    public void comecar_jogo() {
        // embaralha as peças
        Collections.shuffle(dominos);
        pecas_restantes = new ArrayList<>(dominos);

        // distribui 6 peças para cada jogador
        for (int i = 0; i < 6; i++) {
            jogador1.adicionarPeca(dominos.get(i));
            jogador2.adicionarPeca(dominos.get(i + 6));
        }

        // remove as peças que já foram escolhidas
        pecas_restantes.removeAll(jogador1.getMao());
        pecas_restantes.removeAll(jogador2.getMao());

        estado_partida = 1;

        // define quem começa a partida
        Domino remover_peca = determinarQuemComeca();

        // remove a peça inicial das mãos dos jogadores e do monte
        jogador1.getMao().remove(remover_peca);
        jogador2.getMao().remove(remover_peca);
        pecas_restantes.remove(remover_peca);

        //comeca o calculo de tempo
        tempoInicioJogada = System.currentTimeMillis();
    }

    private Domino determinarQuemComeca() {
        Domino maiorMJ1 = encontrarMaiorDuplo(jogador1.getMao());
        Domino maiorMJ2 = encontrarMaiorDuplo(jogador2.getMao());

        Domino peca_escolhida;

        if (maiorMJ1 == null && maiorMJ2 == null) {
            System.out.println("Nenhum jogador tem uma peça dupla. Escolha aleatória.");
            peca_escolhida = pecas_restantes.get(0);
        } else if (maiorMJ1 != null && (maiorMJ2 == null || maiorMJ1.get_e() > maiorMJ2.get_e())) {
            System.out.println("P1 começa com " + maiorMJ1);
            peca_escolhida = maiorMJ1;
            vez_jogador = false; // jogador 1 já jogou
        } else {
            System.out.println("P2 começa com " + maiorMJ2);
            peca_escolhida = maiorMJ2;
            vez_jogador = true; // jogador 2 já jogou
        }

        // define pontas da mesa
        PAE = peca_escolhida.get_e();
        PAD = peca_escolhida.get_d();
        pecas_mesa.add(peca_escolhida);

        return peca_escolhida;
    }

    // retorna a maior peça dupla de uma mão (ou null se não houver)
    private Domino encontrarMaiorDuplo(ArrayList<Domino> mao) {
        Domino maiorDuplo = null;
        for (Domino d : mao) {
            if (d.isIgual() && (maiorDuplo == null || d.get_e() > maiorDuplo.get_e())) {
                maiorDuplo = d;
            }
        }
        return maiorDuplo;
    }

    // jogador tenta jogar uma peça em um dos lados
    public boolean jogar_peca(int id_peca, String lado) {
        Jogador jogadorAtual = vez_jogador ? jogador1 : jogador2;

        System.out.println(jogadorAtual.getMao());

        if (id_peca < 0 || id_peca >= jogadorAtual.getMao().size()) {
            System.out.println("Índice inválido! Escolha uma peça válida.");
            return false;
        }

        Domino peca = jogadorAtual.getPeca(id_peca);
        boolean jogada_valida = false;

        // tentativa de jogar na esquerda
        if (lado.equals("e")) {
            if (peca.get_d() == PAE) {
                PAE = peca.get_e();
                pecas_mesa.add(0, peca);
                jogada_valida = true;
            } else if (peca.get_e() == PAE) {
                PAE = peca.get_d();
                pecas_mesa.add(0, peca);
                jogada_valida = true;
            }
        }
        // tentativa de jogar na direita
        else if (lado.equals("d")) {
            if (peca.get_e() == PAD) {
                PAD = peca.get_d();
                pecas_mesa.add(peca);
                jogada_valida = true;
            } else if (peca.get_d() == PAD) {
                PAD = peca.get_e();
                pecas_mesa.add(peca);
                jogada_valida = true;
            }
        }

        if (jogada_valida) {
            jogadorAtual.getMao().remove(id_peca);
            ja_comprou = false;
            finalizarTempoJogada();
            vez_jogador = !vez_jogador;
            iniciarTempoJogada();
            System.out.println("Peça jogada com sucesso!\n");
        } else {
            System.out.println("Jogada inválida! Escolha outra peça ou compre uma.\n");
        }

        return verificar_fim();
    }

    // jogador compra uma peça do monte
    public void comprar_peca() {
        Jogador jogadorAtual = vez_jogador ? jogador1 : jogador2;

        if (!ja_comprou) {
            if (!pecas_restantes.isEmpty()) {
                Domino peca = pecas_restantes.remove(0);
                jogadorAtual.adicionarPeca(peca);
            } else {
                System.out.println("Não existem mais peças pra comprar!\n");
            }
            ja_comprou = true;
        } else {
            System.out.println("\nPeça já comprada nesse turno!");
        }
        finalizarTempoJogada();
        iniciarTempoJogada();
    }

    // jogador decide passar sua vez
    public void passar_vez() {
        finalizarTempoJogada();
        vez_jogador = !vez_jogador;
        iniciarTempoJogada();
        ja_comprou = false;
    }

    // verifica se o jogo acabou por vitória ou empate
    private boolean verificar_fim() {
        if (jogador1.maoVazia()) {
            estado_partida = 2;
            System.out.println("Jogador 1 Ganhou!");
            return true;
        } else if (jogador2.maoVazia()) {
            estado_partida = 2;
            System.out.println("Jogador 2 Ganhou!");
            return true;
        }

        // checa se o jogo travou (nenhum pode jogar)
        if (verif_trava()) {
            int somaP1 = jogador1.calcularSomaPecas();
            int somaP2 = jogador2.calcularSomaPecas();

            System.out.println("Jogo travado!");
            if (somaP1 < somaP2) {
                estado_partida = 3;
                System.out.println("MJ1 ganha com menos pontos: " + somaP1);
            } else if (somaP2 < somaP1) {
                estado_partida = 3;
                System.out.println("MJ2 ganha com menos pontos: " + somaP2);
            } else {
                estado_partida = 3;
                System.out.println("É um empate! Ambos jogadores têm " + somaP1 + " pontos.");
            }
            mostrarTempos();
            return true;
        }

        return false;
    }

    // verifica se alguma peça pode ser jogada
    private boolean verif_trava() {
        for (Domino d : jogador1.getMao()) {
            if (podeJogar(d)) return false;
        }
        for (Domino d : jogador2.getMao()) {
            if (podeJogar(d)) return false;
        }
        return true;
    }

    // se a peça pode ser inserida em um dos dois lados do tabuleiro, retorna true
    private boolean podeJogar(Domino d) {
        return (d.get_d() == PAD || d.get_e() == PAE || d.get_d() == PAE || d.get_e() == PAD);
    }

    private void contarJogadasValidas(){
        int i = 0;
        if (vez_jogador){
            for (Domino d : jogador1.getMao()){
                if (podeJogar(d)) {
                    i++;
                }
            }
            jogadasValidas[0] = i;
        }else{
            for (Domino d : jogador2.getMao()){
                if (podeJogar(d)) {
                    i++;
                }
            }
            jogadasValidas[1] = i;
        }
    }

    public int retornarValidas(){
        contarJogadasValidas();
        if (vez_jogador) return jogadasValidas[0];
        else return jogadasValidas[1];
    }

    private void finalizarTempoJogada() {
        long tempoFim = System.currentTimeMillis();
        long duracao = tempoFim - tempoInicioJogada;
        if (vez_jogador) {
            tempoTotalP1 += duracao;
        } else {
            tempoTotalP2 += duracao;
        }
    }

    private void iniciarTempoJogada() {
        tempoInicioJogada = System.currentTimeMillis();
    }

    public void mostrarTempos() {
        System.out.println("Tempo total do Jogador 1: " + tempoTotalP1 + " ms");
        System.out.println("Tempo total do Jogador 2: " + tempoTotalP2 + " ms");
    }

    // IA tenta jogar
    public void turnoComputador() {
        if (!vez_jogador) {
            Jogador computador = jogador2;
            boolean jogou = false;

            // Tenta jogar uma peça
            for (int i = 0; i < computador.getMao().size(); i++) {
                Domino peca = computador.getPeca(i);
                if (podeJogar(peca)) {
                    // Decide automaticamente o lado (esquerda ou direita)
                    if (peca.get_e() == PAD || peca.get_d() == PAD) {
                        System.out.println("Computador jogou na direita: " + peca);
                        jogar_peca(i, "d");
                    } else {
                        System.out.println("Computador jogou na esquerda: " + peca);
                        jogar_peca(i, "e");
                    }
                    jogou = true;
                    break;
                }
            }

            // Se não conseguiu jogar, tenta comprar uma peça
            if (!jogou && !pecas_restantes.isEmpty()) {
                System.out.println("Computador comprou uma peça.");
                comprar_peca();
                // Após comprar, tenta jogar de novo
                turnoComputador();
                return;
            }

            // Se não conseguiu jogar nem após comprar, passa a vez
            if (!jogou) {
                System.out.println("Computador passou a vez.");
                passar_vez();
            }
        }
    }

    // Getters úteis
    public Jogador getJogador1() {
        return jogador1;
    }

    public Jogador getJogador2() {
        return jogador2;
    }

    public ArrayList<Domino> getPecasRestantes() {
        return pecas_restantes;
    }

    public boolean isVez_jogador() {
        return vez_jogador;
    }

    public int getPAE() {
        return PAE;
    }

    public int getPAD() {
        return PAD;
    }

    // Setters caso precise manipular fora da classe
    public void setPAE(int PAE) {
        this.PAE = PAE;
    }

    public void setPAD(int PAD) {
        this.PAD = PAD;
    }

    public void setVez_jogador(boolean vez_jogador) {
        this.vez_jogador = vez_jogador;
    }

    public ArrayList<Domino> getPecasMesa() {
        return pecas_mesa;
    }


}
