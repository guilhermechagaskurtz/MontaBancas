package geratfg_v1;

public class Aptidao {
    
    private final int PESO_MIN=0, PESO_MAX=10;
    private int peso;
    private String nome;

    public Aptidao(int peso, String nome) {
        this.peso = peso;
        this.nome = nome;
    }

    public Aptidao(String nome) {
        this.nome = nome;
        this.peso = 0;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
}
