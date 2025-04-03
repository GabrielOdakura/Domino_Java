import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int opcao = -1;
        Scanner input = new Scanner(System.in);
        while(opcao != 0){
            System.out.print("""
                    Bem vindo ao Dominó Virtual!
                    1 - Começar Jogo
                    2 - Regras
                    3 - X
                    0 - Sair do Jogo
                    Digite a opção desejada:
                    """);
            String opcao_escolhida = input.nextLine();
            try{
                opcao = Integer.parseInt(opcao_escolhida);
                switch(opcao) {
                    case 1 -> {
                        System.out.println("\n\n");
                        menu_jogo();
                    }
                    case 2 -> {
                        System.out.println("""
                            
                            
                                   ========================================
                                                REGRAS DO DOMINÓ
                                   ========================================
                           \s
                           1. O jogo começa com um jogador colocando uma peça na mesa.
                           2. Os jogadores devem colocar peças que tenham um dos lados\s
                              correspondentes a uma extremidade do tabuleiro.
                           3. Se um jogador não puder jogar, ele deve comprar uma peça\s
                              do monte.
                           4. Se não houver mais peças para comprar e o jogador não\s
                              puder jogar, ele deve passar a vez.
                           5. O jogo termina quando um jogador jogar todas as suas\s
                              peças ou quando o jogo estiver travado.
                           6. Se o jogo travar, o jogador com a menor soma de pontos\s
                              em suas peças vence.
                           7. Se houver empate na soma de pontos, o jogo termina\s
                              empatado.
                        
                           Digite enter para fechar este menu!
                           =============================
                            
                            """);
                        input.nextLine();
                    }
                    case 3 -> {
                        System.out.println("WIP\nDigite enter para sair");
                        input.nextLine();
                    }
                    case 0 -> {
                        System.out.println("Obrigado pela preferência!");
                        System.exit(0);
                    }
                    default -> throw new Exception();
                }
            }catch(Exception e){
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

                        jogo_atual.jogar_peca(peca_escolhida, escolha_lado);
                    }
                    case 2 -> {
                        jogo_atual.comprar_peca();
                    }
                    case 3 -> {
                        jogo_atual.passar_vez();
                    }
                    case 0 -> {
                        System.out.println("Voltando ao menu principal!");
                        opcao = 0;
                    }
                    default -> throw new Exception();
                }
            }catch(Exception e){
                System.out.println("Opção inválida! Tente novamente!\n\n");
                opcao = -1;
            }
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