package geratfg_v1;

import java.util.ArrayList;

public class Professor implements Comparable<Professor>{
    
    private ArrayList<Aptidao> listaApts = new ArrayList<>();
    private String nome;
    private int qtdBancas;
    private boolean doCurso;

    public Professor(ArrayList<Aptidao> listaApts, String nome) {
        this.listaApts = listaApts;
        this.nome = nome;
        doCurso = true;
    }


    public Professor(String nome) {
        this.nome = nome;
    }

    public boolean isDoCurso() {
        return doCurso;
    }

    public void setDoCurso(boolean doCurso) {
        this.doCurso = doCurso;
    }
    
    public int getQtdBancas() {
        return qtdBancas;
    }

    public void setQtdBancas(int qtdBancas) {
        this.qtdBancas = qtdBancas;
    }

    public ArrayList<Aptidao> getListaApts() {
        return listaApts;
    }

    public void setListaApts(ArrayList<Aptidao> listaApts) {
        this.listaApts = listaApts;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int compareTo(Professor outroProfessor) {
        if(this.qtdBancas < outroProfessor.qtdBancas){
            return -1;
        }
        if(this.qtdBancas > outroProfessor.qtdBancas){
            return 1;
        }
        return 0;
    }
    
}
