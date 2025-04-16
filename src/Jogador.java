import java.util.ArrayList;

public class Jogador {
    private int id;
    private ArrayList<Domino> mao;

    public Jogador(int id) {
        this.id = id;
        this.mao = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Domino> getMao() {
        return mao;
    }

    public void adicionarPeca(Domino peca) {
        mao.add(peca);
    }

    public void removerPeca(Domino peca) {
        mao.remove(peca);
    }

    public Domino getPeca(int index) {
        return mao.get(index);
    }

    public boolean maoVazia() {
        return mao.isEmpty();
    }

    public int calcularSomaPecas() {
        int soma = 0;
        for (Domino d : mao) {
            soma += d.get_e() + d.get_d();
        }
        return soma;
    }
}