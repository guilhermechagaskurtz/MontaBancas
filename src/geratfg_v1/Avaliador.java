package geratfg_v1;

import java.util.ArrayList;

public class Avaliador extends Professor{
    
    private int pesoNaBanca;
    
    public Avaliador(ArrayList<Aptidao> listaApts, String nome, int pesoNaBanca) {
        super(listaApts,nome);
        this.pesoNaBanca = pesoNaBanca;
    }
    
    public Avaliador(String nome, int pesoNaBanca) {
        super(nome);
        this.pesoNaBanca = pesoNaBanca;
    }

    public int getPesoNaBanca() {
        return pesoNaBanca;
    }

    public void setPesoNaBanca(int pesoNaBanca) {
        this.pesoNaBanca = pesoNaBanca;
    }
    
}
