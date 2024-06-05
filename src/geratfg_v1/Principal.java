/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geratfg_v1;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Guilherme
 */
public class Principal extends javax.swing.JFrame {

    ArrayList<Professor> listaProfs = new ArrayList<>();
    ArrayList<Aptidao> listaApts = new ArrayList<>();
    ArrayList<BancaTFG> listaBancas = new ArrayList<>();
    ArrayList<BancaTFG> listaBancasOriginal = new ArrayList<>();

    public void atualizaInterface() {
        contaQtdBancasPorProfessor();
        getOrdemProfessoresMaisBancas();
        atualizaTabelaBancasInterface();
        atualizaTabelaQtdBancasInterface();
        atualizaDadosInterface();
        atualizaTabelaBancasSugestoes();
    }

    private void carregaProfessores(HSSFSheet planilha) {
        Row row = planilha.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getColumnIndex() == 0) {
                continue;
            }
            Professor p = new Professor(cell.getStringCellValue());
            listaProfs.add(p);
        }
    }

    private void carregaAptidoes(HSSFSheet planilha) {
        Iterator<Row> rowIterator = planilha.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell cell = row.getCell(0);
            Aptidao apt = new Aptidao(cell.getStringCellValue());
            listaApts.add(apt);
        }
    }

    private void carregaAptidoesProfessores(HSSFSheet planilha) {
        for (int i = 0; i < listaProfs.size(); i++) {
            Professor p = listaProfs.get(i);
            int colunaProfessor = i + 1;
            Iterator<Row> rowIterator = planilha.iterator();
            int j = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                Cell cell = row.getCell(colunaProfessor);
                Aptidao apt = new Aptidao(listaApts.get(j).getNome());
                apt.setPeso((int) cell.getNumericCellValue());
                p.getListaApts().add(apt);
                j++;
            }
        }
    }

    public int calculaDiferencaAptidoes(ArrayList<Aptidao> listaAptBanca, ArrayList<Aptidao> listaAptProfessor) {
        int dif = 0;
        for (int i = 0; i < listaAptBanca.size(); i++) {
            Aptidao aBanca = listaAptBanca.get(i);
            Aptidao aProf = listaAptProfessor.get(i);
            //só considera quando a banca exige um conhecimento maior que o do professor em uma determinada area
            if (aBanca.getPeso() > aProf.getPeso()) {
                dif += Math.abs(aBanca.getPeso() - aProf.getPeso());
            }
        }
        return dif;
    }

    private void carregaBancas() {
        File file = new File("bancasugerida.xls");
        int i;
        try {
            FileInputStream fileInput = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInput);
            HSSFSheet planilha = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = planilha.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                Avaliador av1 = null, av2 = null, av3 = null;
                String aluno = row.getCell(0).getStringCellValue();
                int tipo = (int) row.getCell(1).getNumericCellValue();
                //procura na lista de professores quem é o orientador
                String strOrientador = row.getCell(2).getStringCellValue();
                Professor orientador = null;
                for (i = 0; i < listaProfs.size(); i++) {
                    //quando encontrar o avaliador, para o laço
                    if (listaProfs.get(i).getNome().equals(strOrientador)) {
                        break;
                    }
                }
                //e cria o orientador com peso 0 na banca
                orientador = new Professor(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome());

                //lista de avaliadores aleatórios, caso o aluno tenha preenchido "-" no requerimento
                ArrayList<Integer> listaAvAleatorios = new ArrayList<>();
                Random r = new Random();
                int auxRandom;
                //procura na lista de professores quem é o av1
                String strAv1 = row.getCell(3).getStringCellValue();
                //caso o avaliador 1 seja "-" (sem sugestão), põe alguém aleatório de avaliador 1
                if (strAv1.equals("-")) {
                    do {
                        auxRandom = r.nextInt(listaProfs.size());
                    } while (listaAvAleatorios.contains(auxRandom));
                    listaAvAleatorios.add(auxRandom);
                    av1 = new Avaliador(listaProfs.get(auxRandom).getListaApts(), listaProfs.get(auxRandom).getNome(), 0);
                } else {
                    for (i = 0; i < listaProfs.size(); i++) {
                        //quando encontrar o avaliador, para o laço
                        if (listaProfs.get(i).getNome().equals(strAv1)) {
                            break;
                        }
                    }
                    //caso não tenha encontrado o professor na lista, é pq é professor de fora do curso, então ele é colocado com peso minimo (mais afim)
                    if (i == listaProfs.size()) {
                        av1 = new Avaliador(null, strAv1, 0);
                        av1.setDoCurso(false); //seta que ele não é do curso
                    } //caso seja um professor do curso, adiciona na lista normalmente
                    else {
                        //e cria o avaliador 1 com peso 0 na banca
                        av1 = new Avaliador(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome(), 0);
                    }
                }

                //procura na lista de professores quem é o av2
                String strAv2 = row.getCell(4).getStringCellValue();
                //caso o avaliador 2 seja "-" (sem sugestão), põe alguém aleatório de avaliador 2
                if (strAv2.equals("-")) {
                    do {
                        auxRandom = r.nextInt(listaProfs.size());
                    } while (listaAvAleatorios.contains(auxRandom));
                    listaAvAleatorios.add(auxRandom);
                    av2 = new Avaliador(listaProfs.get(auxRandom).getListaApts(), listaProfs.get(auxRandom).getNome(), 0);
                } else {
                    for (i = 0; i < listaProfs.size(); i++) {
                        Professor p = (Professor) listaProfs.get(i);
                        //quando encontrar o avaliador, para o laço
                        if (p.getNome().equals(strAv2)) {
                            break;
                        }
                    }
                    //caso não tenha encontrado o professor na lista, é pq é professor de fora do curso, então ele é colocado com peso máximo
                    if (i == listaProfs.size()) {
                        av2 = new Avaliador(null, strAv2, 0);
                        av2.setDoCurso(false); //seta que ele não é do curso
                    } //caso seja um professor do curso, adiciona na lista normalmente
                    else {
                        //e cria o avaliador 2 com peso 0 na banca
                        av2 = new Avaliador(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome(), 0);
                    }
                }

                if (tipo == BancaTFG.TIPO_TFG1) {
                    //procura na lista de professores quem é o av3
                    String strAv3 = row.getCell(5).getStringCellValue();
                    //caso o avaliador 3 seja "-" (sem sugestão), põe alguém aleatório de avaliador 3
                    if (strAv3.equals("-")) {
                        do {
                            auxRandom = r.nextInt(listaProfs.size());
                        } while (listaAvAleatorios.contains(auxRandom));
                        listaAvAleatorios.add(auxRandom);
                        av3 = new Avaliador(listaProfs.get(auxRandom).getListaApts(), listaProfs.get(auxRandom).getNome(), 0);
                    } else {
                        for (i = 0; i < listaProfs.size(); i++) {
                            Professor p = (Professor) listaProfs.get(i);
                            //quando encontrar o avaliador, para o laço
                            if (p.getNome().equals(strAv3)) {
                                break;
                            }
                        }
                        if (i == listaProfs.size()) {
                            av3 = new Avaliador(null, strAv3, 0);
                            av3.setDoCurso(false); //seta que ele não é do curso
                        } //caso seja um professor do curso, adiciona na lista normalmente
                        else {
                            //e cria o avaliador 3 com peso 0 na banca, caso não seja de TFG 2
                            av3 = new Avaliador(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome(), 0);
                        }
                    }
                }

                //le as aptidoes da BANCA caso seja de TFG 1
                ArrayList<Aptidao> listaAptBanca;
                if (tipo == BancaTFG.TIPO_TFG1) {
                    listaAptBanca = new ArrayList<>();
                    for (int j = 0, col = 6; j < listaApts.size(); j++, col++) {
                        int val = (int) row.getCell(col).getNumericCellValue();
                        Aptidao apt = new Aptidao(val, listaApts.get(j).getNome());
                        listaAptBanca.add(apt);
                    }
                } else {
                    listaAptBanca = null;
                }

                //cria a banca
                BancaTFG banca = new BancaTFG();
                banca.setTipo(tipo);
                banca.setListaApts(listaAptBanca);
                banca.setOrientador(orientador);
                banca.setAluno(aluno);
                //verifica quem são os 2 avaliadores com maiores afinidares com as aptidoes da banca e já seta os pesos na banca
                //para bancas de TFG 1
                if (banca.getTipo() == BancaTFG.TIPO_TFG1) {
                    if (av1.isDoCurso()) {
                        int difAv1 = calculaDiferencaAptidoes(listaAptBanca, av1.getListaApts());
                        av1.setPesoNaBanca(difAv1);
                    }
                    if (av2.isDoCurso()) {
                        int difAv2 = calculaDiferencaAptidoes(listaAptBanca, av2.getListaApts());
                        av2.setPesoNaBanca(difAv2);
                    }
                    if (av3.isDoCurso()) {
                        int difAv3 = calculaDiferencaAptidoes(listaAptBanca, av3.getListaApts());
                        av3.setPesoNaBanca(difAv3);
                    }
                    //se o avaliador 1 for o mais afim
                    if (av1.getPesoNaBanca() <= av2.getPesoNaBanca() && av1.getPesoNaBanca() <= av3.getPesoNaBanca()) {
                        banca.setAvaliador1(av1);
                        if (av2.getPesoNaBanca() <= av3.getPesoNaBanca()) {  //se o avaliador 2 for o segundo mais afim
                            banca.setAvaliador2(av2);
                            banca.setAvaliador3(av3);
                        } else {
                            banca.setAvaliador2(av3); //se o avaliador 3 for o segundo mais afim
                            banca.setAvaliador3(av2);
                        }
                        //se o avaliador 2 for o mais afim
                    } else if (av2.getPesoNaBanca() <= av1.getPesoNaBanca() && av2.getPesoNaBanca() <= av3.getPesoNaBanca()) {
                        banca.setAvaliador1(av2);
                        if (av1.getPesoNaBanca() <= av3.getPesoNaBanca()) {  //se o avaliador 1 for o segundo mais afim
                            banca.setAvaliador2(av1);
                            banca.setAvaliador3(av3);
                        } else {
                            banca.setAvaliador2(av3); //se o avaliador 3 for o segundo mais afim
                            banca.setAvaliador3(av1);
                        }
                        //se o avaliador 3 for o mais afim
                    } else if (av3.getPesoNaBanca() <= av1.getPesoNaBanca() && av3.getPesoNaBanca() <= av2.getPesoNaBanca()) {
                        banca.setAvaliador1(av3);
                        if (av1.getPesoNaBanca() <= av2.getPesoNaBanca()) {  //se o avaliador 1 for o segundo mais afim
                            banca.setAvaliador2(av1);
                            banca.setAvaliador3(av2);
                        } else {
                            banca.setAvaliador2(av2); //se o avaliador 2 for o segundo mais afim
                            banca.setAvaliador3(av1);
                        }
                    }
                } //se for TFG 2 seta os avaliadores do arquivo xls mesmo, pois não muda
                else {
                    banca.setAvaliador1(av1);
                    banca.setAvaliador2(av2);
                    banca.setAvaliador3(null);
                }
                listaBancas.add(banca);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Erro ao abrir o arquivo.");
        }
    }

    public void mostraBancas() {
        for (int i = 0; i < listaBancas.size(); i++) {
            System.out.println(listaBancas.get(i).toString());
        }
    }

    public void contaQtdBancasPorProfessor() {
        for (int i = 0; i < listaProfs.size(); i++) {
            Professor p = listaProfs.get(i);
            int count = 0;
            for (int j = 0; j < listaBancas.size(); j++) {
                Avaliador av1 = listaBancas.get(j).getAvaliador1();
                Avaliador av2 = listaBancas.get(j).getAvaliador2();
                if (p.getNome().equals(av1.getNome()) || p.getNome().equals(av2.getNome())) {
                    count++;
                }
            }
            System.out.println("Professor " + p.getNome() + " em \t" + count + " bancas");
            p.setQtdBancas(count);
        }
    }

    public double getMediaBancasPorProfessor() {
        return (double) (listaBancas.size() * 2) / listaProfs.size();
    }

    public double getDesvioPadraoBancaPorProfessor() {
        double media = getMediaBancasPorProfessor();
        double desvio = 0;
        System.out.println("Media: " + media);
        for (int i = 0; i < listaProfs.size(); i++) {
            desvio += Math.pow(listaProfs.get(i).getQtdBancas() - media, 2);
        }
        desvio = Math.sqrt(desvio / (listaProfs.size() - 1));
        System.out.println("Desvio: " + desvio);
        return desvio;
    }

    public void getOrdemProfessoresMaisBancas() {
        Collections.sort(listaProfs);
    }
    //retorna quantos professores tem a menor quantidade de bancas.
    //Por ex., Alexandre, Guilherme e Reiner tem 2 bancas somente, retorna então 3

    public int qtdProfessoresMenosBancas() {
        //ordena para que o professor com a menor quantidade de bancas fique na posição 0
        getOrdemProfessoresMaisBancas();
        //pega a quantidade de bancas do professor que tem menos bancas
        int menor = listaProfs.get(0).getQtdBancas();
        int qtd = 1;
        //conta quantos outros professores tem a mesma quantidade
        for (int i = 1; i < listaProfs.size(); i++) {
            if (listaProfs.get(i).getQtdBancas() == menor) {
                qtd++;
            }
        }
        return qtd;
    }

    //metodo que retorna a quantidade de bancas de um determinado avaliador
    public int getQtdBancas(Avaliador avaliador) {
        for (int i = 0; i < listaProfs.size(); i++) {
            if (listaProfs.get(i).getNome().equals(avaliador.getNome())) {
                return listaProfs.get(i).getQtdBancas();
            }
        }
        return 0;
    }
    //retorna o avaliador que estiver com mais bancas de uma banca específica

    public Avaliador getAvaliadorMaisBancas(BancaTFG banca) {
        if (getQtdBancas(banca.getAvaliador1()) > getQtdBancas(banca.getAvaliador2())) {
            return banca.getAvaliador1();
        }
        return banca.getAvaliador2();
    }

    //verifica se um professor esta na banca original
    public boolean estaNaBancaOriginal(BancaTFG banca, Professor prof) {
        if (banca.getAvaliador1().getNome().equals(prof.getNome())
                || banca.getAvaliador2().getNome().equals(prof.getNome())
                || banca.getAvaliador3().getNome().equals(prof.getNome())) {
            return true;
        }
        return false;
    }
    //retira um professor da banca selecionada (o que estiver em mais bancas) e põe outro (que estiver em menos bancas)

    public void refinaLinha() {
        //pega a linha selecionada
        int linha = jTableBancas.getSelectedRow();
        if (linha > -1) {
            //só troca em TFG 1
            if (listaBancas.get(linha).tipo == BancaTFG.TIPO_TFG1) {
                //pega, da banca selecionada, o cara que está em mais bancas
                Avaliador aMax = getAvaliadorMaisBancas(listaBancas.get(linha));
                System.out.println(aMax.getNome() + " esta em mais bancas com " + getQtdBancas(aMax));

                Professor pMin = null;
                //se o usuario deseja escolher um professor aleatório para entrar na banca
                if (jCheckBoxEscolhaAleatoria.isSelected()) {
                    /*PEGA ALEATORIAMENTE ALGUEM ENTRE OS QUE TEM MENOS BANCAS*/
                    Random random = new Random();
                    int qtd = qtdProfessoresMenosBancas();
                    pMin = listaProfs.get(random.nextInt(qtd));
                } //se não, escolhe o professor que mais fecha com a banca
                else {
                    int qtd = qtdProfessoresMenosBancas();
                    int maisApt = Integer.MAX_VALUE;
                    int maisAptIndice = 0;
                    for (int i = 0; i < qtd; i++) {
                        if (calculaDiferencaAptidoes(listaBancas.get(linha).getListaApts(), listaProfs.get(i).getListaApts()) < maisApt) {
                            maisAptIndice = i;
                            maisApt = calculaDiferencaAptidoes(listaBancas.get(linha).getListaApts(), listaProfs.get(i).getListaApts());
                        }
                    }
                    pMin = listaProfs.get(maisAptIndice);
                }

                boolean troca = true;
                if (jCheckManterSugestao.isSelected()) {
                    //pega o outro avaliador da banca (o que não vai mudar)
                    Avaliador aOutro;
                    if (listaBancas.get(linha).getAvaliador1().getNome().equals(aMax.getNome())) {
                        aOutro = listaBancas.get(linha).getAvaliador2();
                    } else {
                        aOutro = listaBancas.get(linha).getAvaliador1();
                    }
                    //e verifica se pelo menos o outro avaliador da banca está na banca original (sugerida pelo aluno)
                    //ou se pelo menos o professor que vai entrar está na banca original (sugerida pelo aluno)
                    if (!(estaNaBancaOriginal(listaBancasOriginal.get(linha), pMin) || estaNaBancaOriginal(listaBancasOriginal.get(linha), aOutro))) {
                        troca = false;
                    }
                }
                //se não for pra manter uma sugestão, só troca
                //ou se for pra manter sugestão e com a troca ainda fica uma sugestão, troca tambem
                if (troca) {
                    //se o avaliador que for entrar já estiver na banca, não deixa!
                    if (pMin.getNome().equals((listaBancas.get(linha).getAvaliador1().getNome()))
                            || pMin.getNome().equals((listaBancas.get(linha).getAvaliador2().getNome()))
                            || pMin.getNome().equals((listaBancas.get(linha).getOrientador().getNome()))) {
                        troca = false;
                    }
                    //se o avaliador que for entrar realmente não estiver na banca, troca
                    if (troca) {
                        Avaliador av = new Avaliador(pMin.getNome(), calculaDiferencaAptidoes(listaBancas.get(linha).getListaApts(), pMin.getListaApts()));
                        //se o cara que vai sair é o avaliador 1, então define o novo avaliador 1 como sendo o novo avaliador
                        if (listaBancas.get(linha).getAvaliador1().getNome().equals(aMax.getNome())) {
                            listaBancas.get(linha).setAvaliador1(av);
                        }//se o cara que vai sair é o avaliador 1, então define o novo avaliador 1 como sendo o novo avaliador
                        else {
                            listaBancas.get(linha).setAvaliador2(av);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Não é possível inserir " + pMin.getNome() + " nessa banca, pois a banca ficaria com dois avaliadores iguais. Tente novamente com a opção \"Escolha Aleatória\" marcada");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Não é possível realizar a troca, pois a banca ficará sem nenhuma sugestão do aluno");
                }
//                //reconta quantas bancas cada professor está
//                contaQtdBancasPorProfessor();
//                //ordena para que o professor com a menor quantidade de bancas fique na posição 0
//                getOrdemProfessoresMaisBancas();
            } else {
                JOptionPane.showMessageDialog(this, "Não é permitida a troca de professores em TFG 2");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhuma linha selecionada");
        }
    }

    public void refina() {
        //mostraBancas as bancas no console
        //mostraBancas();
        double desvioPadrao = Double.MAX_VALUE, desvioPadraoAnterior;
        do {
            desvioPadraoAnterior = desvioPadrao;
            Professor pMin = null;
            /*TRECHO 1: PEGA SOMENTE O PROFESSOR QUE TEM MENOS BANCAS*/
            if (jRadioFixo.isSelected()) {
                //ordena para que o professor com a menor quantidade de bancas fique na posição 0
                getOrdemProfessoresMaisBancas();
                //pega o professor que tem menos bancas
                pMin = listaProfs.get(0);
            } else {
                /*TRECHO 2: PEGA ALEATORIAMENTE ALGUEM ENTRE OS QUE TEM MENOS BANCAS*/
                Random random = new Random();
                int qtd = qtdProfessoresMenosBancas();
                pMin = listaProfs.get(random.nextInt(qtd));
            }
            //tenta buscar o professor que mais tenha bancas possivel
            Professor pMax = null;
            int k = listaProfs.size() - 1, menor = Integer.MAX_VALUE, menorIndice = 0;
            boolean continua = true;
            while (continua) {
                pMax = listaProfs.get(k);
                //descobre qual banca que pMax participa que pMin tem mais afinidade
                menor = Integer.MAX_VALUE;
                menorIndice = 0;
                for (int i = 0; i < listaBancas.size(); i++) {
                    // se essa banca é banca de TFG 1 (pois TFG 2 não pode mudar)
                    if (listaBancas.get(i).tipo == BancaTFG.TIPO_TFG1) {
                        Avaliador av2 = listaBancas.get(i).getAvaliador2();
                        Professor ori = listaBancas.get(i).getOrientador();
                        //se encontrou uma banca que o pMax é avaliador2 e que pMin não seja orientador
                        if (av2.getNome().equals(pMax.getNome()) && !pMin.getNome().equals(ori.getNome())) {
                            //calcula a afinidade de pMin nessa banca
                            int afinidadepMin = calculaDiferencaAptidoes(listaBancas.get(i).getListaApts(), pMin.getListaApts());
                            if (afinidadepMin < menor) {
                                menor = afinidadepMin;
                                menorIndice = i;
                                continua = false;
                            }
                        }
                    }
                }
                if (continua) {
                    k--;
                }
            }
            //só faz a troca caso pMax tenha pelo menos duas bancas a mais que pMin
            if (pMax.getQtdBancas() > pMin.getQtdBancas() + 1) {
                //poe pMin na banca de pMax descoberta acima
                BancaTFG bancapMax = listaBancas.get(menorIndice);
                jLabelMudanca.setText("<html>Banca do aluno <font color='green'>" + bancapMax.getAluno() + "</font>: sai <font color='red'>" + pMax.getNome() + "</font>, entra <font color='blue'>" + pMin.getNome() + "</font></html>");
                System.out.println("VOU TROCAR " + pMax.getNome() + " por " + pMin.getNome() + " na banca do " + bancapMax.getAluno());
                Avaliador av = new Avaliador(pMin.getListaApts(), pMin.getNome(), menor);
                bancapMax.setAvaliador2(av);

                //conta quantas bancas cada professor está
                contaQtdBancasPorProfessor();
                //calcula o desvio padrao
                desvioPadrao = getDesvioPadraoBancaPorProfessor();
                if (jCheckBoxRefinar.isSelected()) {
                    return; //se for pra refinar 1 por vez, para a execucao do método, pois ele é iterativo
                }
            } else {
                jLabelMudanca.setText("Finalizado");
            }
        } while (desvioPadrao < desvioPadraoAnterior);
    }

    public void atualizaTabelaBancasInterface() {
        DefaultTableModel linhas = (DefaultTableModel) jTableBancas.getModel();
        linhas.setRowCount(0);
        for (int i = 0; i < listaBancas.size(); i++) {
            String aluno = listaBancas.get(i).getAluno();
            Professor orientador = listaBancas.get(i).getOrientador();
            Avaliador av1 = listaBancas.get(i).getAvaliador1();
            Avaliador av2 = listaBancas.get(i).getAvaliador2();
            int tipo = listaBancas.get(i).getTipo();
            String linha[] = new String[]{aluno, orientador.getNome(), av1.getNome(), av2.getNome(), "TFG " + tipo};
            linhas.addRow(linha);
        }
    }

    public void atualizaTabelaBancasSugestoes() {
        DefaultTableModel linhas = (DefaultTableModel) jTableBancas.getModel();
        ArrayList<Integer> linhasColAv1 = new ArrayList<Integer>();
        ArrayList<Integer> linhasColAv2 = new ArrayList<Integer>();
        for (int i = 0; i < listaBancas.size(); i++) {
            //verifica se o Avaliador 1 está na banca original
            if (estaNaBancaOriginal(listaBancasOriginal.get(i), listaBancas.get(i).getAvaliador1())) {
                linhasColAv1.add(i);
            }
            //verifica se o Avaliador 2 está na banca original
            if (estaNaBancaOriginal(listaBancasOriginal.get(i), listaBancas.get(i).getAvaliador2())) {
                linhasColAv2.add(i);
            }
        }
        jTableBancas.getColumnModel().getColumn(2).setCellRenderer(new MeuCellRender(linhasColAv1));
        jTableBancas.getColumnModel().getColumn(3).setCellRenderer(new MeuCellRender(linhasColAv2));
    }

    public void atualizaTabelaQtdBancasInterface() {
        DefaultTableModel linhas = (DefaultTableModel) jTableQtdBancas.getModel();
        linhas.setRowCount(0);
        for (int i = 0; i < listaProfs.size(); i++) {
            Professor p = listaProfs.get(i);
            String linha[] = new String[]{p.getNome(), p.getQtdBancas() + ""};
            linhas.addRow(linha);
        }
    }

    public void atualizaDadosInterface() {
        double media = getMediaBancasPorProfessor() * 1000;
        media = Math.floor(media);
        media = media / 1000;
        jLabelMedia.setText(media + "");
        double desvioPadrao = getDesvioPadraoBancaPorProfessor() * 1000;
        desvioPadrao = Math.floor(desvioPadrao);
        desvioPadrao = desvioPadrao / 1000;
        jLabelDesvioPadrao.setText(desvioPadrao + "");

    }
    //cria uma cópia das bancas originais (das sugestões do aluno)

    public void criaCopiaBancasOriginais() {
        listaBancasOriginal = new ArrayList<BancaTFG>(listaBancas.size());
        for (BancaTFG item : listaBancas) {
            listaBancasOriginal.add(item.getClone());
        }
    }

    public Principal() {
        initComponents();
        File file = new File("prof_vs_apts.xls");
        try {
            FileInputStream fileInput = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInput);
            HSSFSheet planilha = workbook.getSheetAt(0);
            //carrega a lista de professores
            carregaProfessores(planilha);
            //carrega a lista de aptidoes
            carregaAptidoes(planilha);
            //carrega o valor das aptidoes de cada professor
            carregaAptidoesProfessores(planilha);
            //carrega as bancas sugeridas e suas aptidoes
            carregaBancas();
            //cria uma cópia de segurança da banca original
            criaCopiaBancasOriginais();
            //conta quantas bancas cada professor está
            System.out.println("---Quantidade de bancas sem refinar---");
            contaQtdBancasPorProfessor();
            fileInput.close();
            atualizaInterface();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Erro ao abrir o arquivo.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup2 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableBancas = new javax.swing.JTable(){

            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                String nome = "";
                int peso = 0;
                try {
                    //comment row, exclude heading
                    if(rowIndex >= 0 && (colIndex ==2 || colIndex==3)){
                        if(listaBancas.get(rowIndex).getTipo() == BancaTFG.TIPO_TFG1){
                            if(colIndex==2){
                                nome = listaBancas.get(rowIndex).getAvaliador1().getNome();
                                peso = listaBancas.get(rowIndex).getAvaliador1().getPesoNaBanca();
                            }
                            if(colIndex==3){
                                nome = listaBancas.get(rowIndex).getAvaliador2().getNome();
                                peso = listaBancas.get(rowIndex).getAvaliador2().getPesoNaBanca();
                            }
                            tip = nome + ": "+peso;
                            return tip;
                        }
                    }
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return null;
            }
        };
        jButton1 = new javax.swing.JButton();
        jCheckBoxRefinar = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableQtdBancas = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelMedia = new javax.swing.JLabel();
        jLabelDesvioPadrao = new javax.swing.JLabel();
        jLabelMudanca = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioAleatorio = new javax.swing.JRadioButton();
        jRadioFixo = new javax.swing.JRadioButton();
        jLabelMudanca1 = new javax.swing.JLabel();
        jLabelMudanca2 = new javax.swing.JLabel();
        jCheckManterSugestao = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jCheckBoxEscolhaAleatoria = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTableBancas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Aluno", "Orientador", "Avaliador 1", "Avaliador 2", "TFG 1 ou 2"
            }
        ));
        jTableBancas.getColumnModel().getColumn(0).setPreferredWidth(200);
        jTableBancas.getColumnModel().getColumn(4).setPreferredWidth(20);
        jScrollPane1.setViewportView(jTableBancas);
        jTableBancas.setIntercellSpacing(new java.awt.Dimension(2, 2));

        jButton1.setText("Refinar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jCheckBoxRefinar.setText("Refinar 1 vez");

        jTableQtdBancas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Professor", "Quantidade"
            }
        ));
        jScrollPane2.setViewportView(jTableQtdBancas);

        jLabel1.setText("Média de bancas:");

        jLabel2.setText("Desvio Padrão:");

        jLabelMedia.setText("0");

        jLabelDesvioPadrao.setText("0");

        jLabelMudanca.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelMudanca.setText("Nenhuma mudança realizada");

        jLabel4.setText("Status:");

        buttonGroup2.add(jRadioAleatorio);
        jRadioAleatorio.setText("Aleatório");
        jRadioAleatorio.setToolTipText("Busca os professores que estão em menos bancas, e escolhe aleatóriamente um deles para colocá-lo em mais uma banca");
        jRadioAleatorio.setName(""); // NOI18N
        jRadioAleatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioAleatorioActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioFixo);
        jRadioFixo.setSelected(true);
        jRadioFixo.setText("Fixo");
        jRadioFixo.setToolTipText("Busca os professores que estão em menos bancas, e escolhe o primeiro (alfabeticamente) entre eles para colocá-lo em mais uma banca");

        jLabelMudanca1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelMudanca1.setText("REFINAMENTO INDIVIDUAL");
        jLabelMudanca1.setToolTipText("Retira um Avaliador da linha selecionada (o que estiver em mais bancas), e coloca nesta banca um dos professores que estiverem com menos bancas");

        jLabelMudanca2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelMudanca2.setText("REFINAMENTO GERAL");
        jLabelMudanca2.setToolTipText("Retira um Avaliador de uma banca (dentre os professores que estiverem em mais bancas), e coloca nesta banca um dos professores que estiverem com menos bancas (processo Interativo)");

        jCheckManterSugestao.setSelected(true);
        jCheckManterSugestao.setText("Manter sugestão");
        jCheckManterSugestao.setToolTipText("Se marcado, busca manter pelo menos 1 sugestão do aluno.");

        jButton2.setText("Refinar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jCheckBoxEscolhaAleatoria.setText("Escolha Aleatória");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelMudanca2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelMudanca, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBoxRefinar)
                                    .addComponent(jButton1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioFixo)
                                    .addComponent(jRadioAleatorio)))
                            .addComponent(jButton2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckManterSugestao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBoxEscolhaAleatoria))
                            .addComponent(jLabelMudanca1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelDesvioPadrao)
                                    .addComponent(jLabelMedia)))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabelMedia))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabelDesvioPadrao)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelMudanca2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelMudanca))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxRefinar)
                    .addComponent(jRadioAleatorio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jRadioFixo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelMudanca1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckManterSugestao)
                    .addComponent(jCheckBoxEscolhaAleatoria))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        refina();
        atualizaInterface();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jRadioAleatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioAleatorioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioAleatorioActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        refinaLinha();
        atualizaInterface();
    }//GEN-LAST:event_jButton2ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBoxEscolhaAleatoria;
    private javax.swing.JCheckBox jCheckBoxRefinar;
    private javax.swing.JCheckBox jCheckManterSugestao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelDesvioPadrao;
    private javax.swing.JLabel jLabelMedia;
    private javax.swing.JLabel jLabelMudanca;
    private javax.swing.JLabel jLabelMudanca1;
    private javax.swing.JLabel jLabelMudanca2;
    private javax.swing.JRadioButton jRadioAleatorio;
    private javax.swing.JRadioButton jRadioFixo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTableBancas;
    private javax.swing.JTable jTableQtdBancas;
    // End of variables declaration//GEN-END:variables
}
