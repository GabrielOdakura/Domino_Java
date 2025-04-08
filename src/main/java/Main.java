import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int opcao = -1;
        Scanner input = new Scanner(System.in);
        while (opcao != 0) {
            System.out.print("""
                    Bem vindo ao Dominó Virtual!
                    1 - Começar Jogo
                    2 - Regras
                    3 - Testes de Aceitação
                    0 - Sair do Jogo
                    Digite a opção desejada:
                    """);
            String opcao_escolhida = input.nextLine();
            try {
                opcao = Integer.parseInt(opcao_escolhida);
                switch (opcao) {
                    case 1 -> {
                        System.out.println("\n\n");
                        menu_jogo();
                    }
                    case 2 -> {
                        System.out.println("REGRAS DO DOMINÓ...");
                        input.nextLine();
                    }
                    case 3 -> {
                        //TestesAceitacao.executarTestesDeAceitacao();
                        input.nextLine();  // espera o usuário apertar enter
                    }
                    case 0 -> {
                        System.out.println("Deseja sair? Digite 's' para confirmar: ");
                        opcao_escolhida = input.nextLine();
                        if (opcao_escolhida.equalsIgnoreCase("s")) {
                            System.out.println("Obrigado pela preferência!");
                            System.exit(0);
                        }
                    }
                    default -> throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("Opção inválida! Tente novamente!\n\n");
                opcao = -1;
            }
        }
        input.close();
    }

    private static void menu_jogo(){
        Jogo jogo_atual = new Jogo();
        jogo_atual.comecar_jogo();

        int opcao = -1;

        Scanner input = new Scanner(System.in);

        boolean encerrar = false;
        while(opcao != 0){
            boolean vez_jogador = jogo_atual.isVez_jogador();
            // se for true pega a mao do P1 se for false pega a do P2
            ArrayList<Domino> mao = vez_jogador ? jogo_atual.getMJ1() : jogo_atual.getMJ2();
            String mao_formatada = formatarMao(mao);
            System.out.print(String.format("""
                    Menu de Jogo
                    Vez do jogador : %s
                    Lado esquerdo : [%d] | Lado Direito : [%d]
                    Peças Jogador:
                    %s
                    Opções Disponíveis
                    1 - Jogar Peça
                    2 - Comprar Peça
                    3 - Passar Vez
                    0 - Sair pro menu
                    Digite a opção desejada:
                    """, vez_jogador ? "P1" : "P2", jogo_atual.getPAE(), jogo_atual.getPAD(), mao_formatada));
            String opcao_escolhida = input.nextLine();
            try{
                opcao = Integer.parseInt(opcao_escolhida);
                switch(opcao) {
                    case 1 -> {
                        System.out.println("Digite a peça desejada para jogar:");
                        String escolha_peca = input.nextLine();

                        int peca_escolhida = Integer.parseInt(escolha_peca);

                        System.out.println("Digite o lado para jogar ('e' ou 'd'):");
                        String escolha_lado = input.nextLine();

                        if (!(escolha_lado.equals("e") || escolha_lado.equals("d")))  throw new Exception();

                        encerrar = jogo_atual.jogar_peca(peca_escolhida, escolha_lado);

                    }
                    case 2 -> {
                        jogo_atual.comprar_peca();
                    }
                    case 3 -> {
                        jogo_atual.passar_vez();
                    }
                    case 0 -> {
                        System.out.println("Deseja sair? Digite 's' para confirmar: ");
                        opcao_escolhida = input.nextLine();
                        if (opcao_escolhida.equalsIgnoreCase("s")) {
                            System.out.println("Voltando ao menu principal!");
                            opcao = 0;
                        }
                    }
                    default -> throw new Exception();
                }
            }catch(Exception e){
                System.out.println("Opção inválida! Tente novamente!\n\n");
                opcao = -1;
            }
            if (encerrar) break;
        }
    }

    private static String formatarMao(ArrayList<Domino> mao) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < mao.size(); i++) {
            output.append(String.format("(%d) = %s", i, mao.get(i)));

            // Add a newline after every 3 pieces (except the last one)
            if ((i + 1) % 3 == 0 || i == mao.size() - 1) {
                output.append("\n");
            } else {
                output.append(" | "); // Space between pieces
            }
        }



        return output.toString();
    }


}