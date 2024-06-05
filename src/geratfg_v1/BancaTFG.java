package geratfg_v1;

import java.util.ArrayList;

public class BancaTFG implements Cloneable{

    public static final int TIPO_TFG1 = 1, TIPO_TFG2 = 2;
    String aluno;
    private ArrayList<Aptidao> listaApts = new ArrayList<>();
    private Professor Orientador;
    Avaliador Avaliador1, Avaliador2, Avaliador3;
    int tipo;

    
    BancaTFG getClone(){
        try{
            return (BancaTFG) super.clone();
        } catch(Exception e){
            System.out.println("Clonagem não permitida");
            return this;
        }
    }
    @Override
    public String toString(){
        return aluno+" \t\t\t\t Or:"+Orientador.getNome()+" \t\t Av1:"+Avaliador1.getNome()+"("+Avaliador1.getPesoNaBanca()+") \t\t Av2:"+Avaliador2.getNome()+"("+Avaliador2.getPesoNaBanca()+")";
        
    }
    public BancaTFG(ArrayList<Aptidao> listaApts) {
        this.listaApts = listaApts;
    }

    public BancaTFG(ArrayList<Aptidao> listaApts, String aluno, Professor Orientador, Avaliador Avaliador1, Avaliador Avaliador2) {
        this.listaApts = listaApts;
        this.aluno = aluno;
        this.Orientador = Orientador;
        this.Avaliador1 = Avaliador1;
        this.Avaliador2 = Avaliador2;
    }

    public BancaTFG() {
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public ArrayList<Aptidao> getListaApts() {
        return listaApts;
    }

    public void setListaApts(ArrayList<Aptidao> listaApts) {
        this.listaApts = listaApts;
    }

    public Professor getOrientador() {
        return Orientador;
    }

    public void setOrientador(Professor Orientador) {
        this.Orientador = Orientador;
    }

    public Avaliador getAvaliador1() {
        return Avaliador1;
    }

    public void setAvaliador1(Avaliador Avaliador1) {
        this.Avaliador1 = Avaliador1;
    }

    public Avaliador getAvaliador2() {
        return Avaliador2;
    }

    public void setAvaliador2(Avaliador Avaliador2) {
        this.Avaliador2 = Avaliador2;
    }
    
    public Avaliador getAvaliador3() {
        return Avaliador3;
    }

    public void setAvaliador3(Avaliador Avaliador3) {
        this.Avaliador3 = Avaliador3;
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }
    
}
