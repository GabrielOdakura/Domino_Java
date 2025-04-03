import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Jogo {

    // lista contendo todos os dominos
    private ArrayList<Domino> dominos = new ArrayList<>();

    // dominos nas mãos de cada jogador
    private ArrayList<Domino> MJ1 = new ArrayList<>();
    private ArrayList<Domino> MJ2 = new ArrayList<>();

    private ArrayList<Domino> pecas_mesa = new ArrayList<>();

    // determina a vez do jogador. true = P1 | False = P2
    private boolean vez_jogador = true;
    private boolean ja_comprou = false;

    private ArrayList<Domino> pecas_restantes;

    private int PAE;
    private int PAD;

    // 0 = não iniciada | 1 = em andamento | 2 = finalizado | 3 = empate
    private int estado_partida = 0;

    public Jogo(){
        // inicialização das peças
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                this.dominos.add(new Domino(i, j));
            }
        }
    }

    public void comecar_jogo(){
        // embaralha as peças
        Collections.shuffle(dominos);
        pecas_restantes = new ArrayList<>(dominos);

        // inicializa a mão de cada jogador
        for (int i = 0; i < 6; i++) {
            MJ1.add(dominos.get(i));
            MJ2.add(dominos.get(i + 6));
        }

        // remove as peças que já foram escolhidas em MJ1 e MJ2
        pecas_restantes.removeAll(MJ1);
        pecas_restantes.removeAll(MJ2);

        /* // debug
        System.out.println("MJ1: " + MJ1);
        System.out.println("MJ2: " + MJ2);
        System.out.println("Peças restantes: " + pecas_restantes);
        */

        estado_partida = 1;

        Domino remover_peca = determinarQuemComeca();

        MJ1.remove(remover_peca);
        MJ2.remove(remover_peca);
        pecas_restantes.remove(remover_peca);
    }

    private Domino determinarQuemComeca() {
        Domino maiorMJ1 = encontrarMaiorDuplo(MJ1);
        Domino maiorMJ2 = encontrarMaiorDuplo(MJ2);

        Domino peca_escolhida = null;

        if (maiorMJ1 == null && maiorMJ2 == null) {
            System.out.println("Nenhum jogador tem uma peça dupla. Escolha aleatória.");
            peca_escolhida = pecas_restantes.get(0);
            System.out.println("Peça escolhida: " + peca_escolhida);

            // colocando a primeira peca no lugar
            PAE = peca_escolhida.get_e(); PAD = peca_escolhida.get_d();
        } else if (maiorMJ1 != null && (maiorMJ2 == null || maiorMJ1.get_e() > maiorMJ2.get_e())) {
            System.out.println("P1 começa com " + maiorMJ1);
            peca_escolhida = maiorMJ1;

            // colocando a primeira peca no lugar
            PAE = peca_escolhida.get_e(); PAD = peca_escolhida.get_d();

            // troca a vez do jogador pq ele acabou de jogar
            vez_jogador = false;
        } else {
            System.out.println("P2 começa com " + maiorMJ2);
            peca_escolhida = maiorMJ2;

            // colocando a primeira peca no lugar
            PAE = peca_escolhida.get_e(); PAD = peca_escolhida.get_d();

            // troca a vez do jogador pq ele acabou de jogar
            vez_jogador = true;
        }
        return peca_escolhida;
    }

    private Domino encontrarMaiorDuplo(ArrayList<Domino> mao) {
        Domino maiorDuplo = null;
        for (Domino d : mao) {
            if (d.isIgual()) {
                if (maiorDuplo == null || d.get_e() > maiorDuplo.get_e()) {
                    maiorDuplo = d;
                }
            }
        }
        return maiorDuplo;
    }

    public void jogar_peca(int id_peca, String lado) {
        ArrayList<Domino> mao_atual = vez_jogador ? MJ1 : MJ2;

        if (id_peca < 0 || id_peca >= mao_atual.size()) {
            System.out.println("Índice inválido! Escolha uma peça válida.");
            return;
        }

        Domino peca = mao_atual.get(id_peca);
        boolean jogada_valida = false;

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
        } else if (lado.equals("d")) {
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
            mao_atual.remove(id_peca);
            ja_comprou = false;
            vez_jogador = !vez_jogador;
            System.out.println("Peça jogada com sucesso!\n");
        } else {
            System.out.println("Jogada inválida! Escolha outra peça ou compre uma.\n");
        }

        verificar_fim();
    }

    public void comprar_peca(){
        if(vez_jogador){
            if(!ja_comprou) {
                if(!pecas_restantes.isEmpty()){
                    Domino peca_comprar = pecas_restantes.get(0);
                    MJ1.add(peca_comprar);
                    pecas_restantes.remove(0);
                }else System.out.println("Não existem mais peças pra comprar!\n");
            }else System.out.println("\nPeça já comprada nesse turno!");
            ja_comprou = true;
        }else{
            if(!ja_comprou) {
                if(!pecas_restantes.isEmpty()){
                    Domino peca_comprar = pecas_restantes.get(0);
                    MJ2.add(peca_comprar);
                    pecas_restantes.remove(0);
                }else System.out.println("Não existem mais peças pra comprar!\n");
            }else System.out.println("\nPeça já comprada nesse turno!");
            ja_comprou = true;
        }
    }

    public void passar_vez(){
        vez_jogador = !vez_jogador;
        ja_comprou = false;
    }

    private boolean verificar_fim(){
        Scanner input = new Scanner(System.in);
        if(MJ1.isEmpty()){
            estado_partida = 2;
            System.out.println("Jogador 1 Ganhou!");
            System.out.println("Aperte enter para finalizar!");
            input.nextLine();
            return true;
        }else if(MJ2.isEmpty()){
            estado_partida = 2;
            System.out.println("Jogador 2 Ganhou!");
            System.out.println("Aperte enter para finalizar!");
            input.nextLine();
            return true;
        }

        // checa se o jogo tem algum movimento possível
        if (verif_trava()) {
            // Calcula pontos
            int somaP1 = calcularSomaPecas(MJ1);
            int somaP2 = calcularSomaPecas(MJ2);

            System.out.println("Jogo travado!");
            if (somaP1 < somaP2) {
                estado_partida = 3;
                System.out.println("MJ1 ganha com menos pontos: " + somaP1);
            } else if (somaP2 < somaP1) {
                estado_partida = 3;
                System.out.println("MJ2 ganha com menos pontos: " + somaP2);
            } else {
                estado_partida = 3;
                System.out.println("É um empate! Ambos jogadores tem " + somaP1 + " pontos.");
            }
            return true;
        }

        return false;
    }

    private boolean verif_trava() {
        for (Domino d : MJ1) {
            if (podeJogar(d)) return false;
        }
        for (Domino d : MJ2) {
            if (podeJogar(d)) return false;
        }
        return true;
    }

    // no evento de dar um empate calcula as somas das mãos do jogador
    private int calcularSomaPecas(ArrayList<Domino> mao) {
        int soma = 0;
        for (Domino d : mao) {
            soma += d.get_e() + d.get_d();
        }
        return soma;
    }

    // se a peça pode ser inserida em um dos dois lados do tabuleiro, ela vai retornar true, caso não, retorna false.
    private boolean podeJogar(Domino d) {
        return (d.get_d() == PAD || d.get_e() == PAE || d.get_d() == PAE || d.get_e() == PAD);
    }

    public ArrayList<Domino> getMJ2() {
        return MJ2;
    }

    public ArrayList<Domino> getMJ1() {
        return MJ1;
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
}
