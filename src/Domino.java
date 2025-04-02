public class Domino {
    int e;
    int d;

    public Domino(int e, int d) {
        this.e = e;
        this.d = d;
    }

    public boolean isIgual() {
        return e == d;
    }

    public int get_e() {
        return e;
    }

    public int get_d() {
        return d;
    }

    public void inverter_lados(){
        int aux = this.e;
        this.e = this.d;
        this.d = aux;
    }

    @Override
    public String toString() {
        return "[" + e + "|" + d + "]";
    }
}
