package geratfg_v1;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class GeraTFG_v1 {

    ArrayList<Professor> listaProfs = new ArrayList<>();
    ArrayList<Aptidao> listaApts = new ArrayList<>();
    ArrayList<BancaTFG> listaBancas = new ArrayList<>();

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

    public int calculaDiferencaAptidoes(ArrayList<Aptidao> listaApt1, ArrayList<Aptidao> listaApt2) {
        int dif = 0;
        for (int i = 0; i < listaApt1.size(); i++) {
            Aptidao a1 = listaApt1.get(i);
            Aptidao a2 = listaApt2.get(i);
            dif += Math.abs(a1.getPeso() - a2.getPeso());
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

                //procura na lista de professores quem é o av1
                String strAv1 = row.getCell(3).getStringCellValue();
                for (i = 0; i < listaProfs.size(); i++) {
                    //quando encontrar o avaliador, para o laço
                    if (listaProfs.get(i).getNome().equals(strAv1)) {
                        break;
                    }
                }
                //caso não tenha encontrado o professor na lista, é pq é professor de fora do curso, então ele é colocado com peso máximo
                if (i == listaProfs.size()) {
                    av1 = new Avaliador(null, strAv1, 10);
                    av1.setDoCurso(false); //seta que ele não é do curso
                } //caso seja um professor do curso, adiciona na lista normalmente
                else {
                    //e cria o avaliador 1 com peso 0 na banca
                    av1 = new Avaliador(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome(), 0);
                }
                //procura na lista de professores quem é o av2
                String strAv2 = row.getCell(4).getStringCellValue();
                for (i = 0; i < listaProfs.size(); i++) {
                    Professor p = (Professor) listaProfs.get(i);
                    //quando encontrar o avaliador, para o laço
                    if (p.getNome().equals(strAv2)) {
                        break;
                    }
                }
                //caso não tenha encontrado o professor na lista, é pq é professor de fora do curso, então ele é colocado com peso máximo
                if (i == listaProfs.size()) {
                    av2 = new Avaliador(null, strAv2, 10);
                    av2.setDoCurso(false); //seta que ele não é do curso
                } //caso seja um professor do curso, adiciona na lista normalmente
                else {
                    //e cria o avaliador 2 com peso 0 na banca
                    av2 = new Avaliador(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome(), 0);
                }

                if (tipo == BancaTFG.TIPO_TFG1) {
                    //procura na lista de professores quem é o av3
                    String strAv3 = row.getCell(5).getStringCellValue();
                    for (i = 0; i < listaProfs.size(); i++) {
                        Professor p = (Professor) listaProfs.get(i);
                        //quando encontrar o avaliador, para o laço
                        if (p.getNome().equals(strAv3)) {
                            break;
                        }
                    }
                    if (i == listaProfs.size()) {
                        av3 = new Avaliador(null, strAv3, 10);
                        av3.setDoCurso(false); //seta que ele não é do curso
                    } //caso seja um professor do curso, adiciona na lista normalmente
                    else {
                        //e cria o avaliador 3 com peso 0 na banca, caso não seja de TFG 2
                        av3 = new Avaliador(listaProfs.get(i).getListaApts(), listaProfs.get(i).getNome(), 0);
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
                        } else {
                            banca.setAvaliador2(av3); //se o avaliador 3 for o segundo mais afim
                        }
                        //se o avaliador 2 for o mais afim
                    } else if (av2.getPesoNaBanca() <= av1.getPesoNaBanca() && av2.getPesoNaBanca() <= av3.getPesoNaBanca()) {
                        banca.setAvaliador1(av2);
                        if (av1.getPesoNaBanca() >= av3.getPesoNaBanca()) {  //se o avaliador 1 for o segundo mais afim
                            banca.setAvaliador2(av1);
                        } else {
                            banca.setAvaliador2(av3); //se o avaliador 3 for o segundo mais afim
                        }
                        //se o avaliador 3 for o mais afim
                    } else if (av3.getPesoNaBanca() <= av1.getPesoNaBanca() && av3.getPesoNaBanca() <= av2.getPesoNaBanca()) {
                        banca.setAvaliador1(av3);
                        if (av1.getPesoNaBanca() <= av2.getPesoNaBanca()) {  //se o avaliador 1 for o segundo mais afim
                            banca.setAvaliador2(av1);
                        } else {
                            banca.setAvaliador2(av2); //se o avaliador 2 for o segundo mais afim
                        }
                    }
                } //se for TFG 2 seta os avaliadores do arquivo xls mesmo, pois não muda
                else {
                    banca.setAvaliador1(av1);
                    banca.setAvaliador2(av2);
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

    public void refina() {
        //mostraBancas
        mostraBancas();
        double desvioPadrao = Double.MAX_VALUE, desvioPadraoAnterior;
        do {
            desvioPadraoAnterior = desvioPadrao;
            getOrdemProfessoresMaisBancas();
            Professor pMin = listaProfs.get(0); //professor que tem menos bancas

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
                        //se encontrou uma banca que o pMax é avaliador2
                        if (av2.getNome().equals(pMax.getNome())) {
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
                System.out.println("VOU TROCAR " + pMax.getNome() + " por " + pMin.getNome() + " na banca do " + bancapMax.getAluno());
                Avaliador av = new Avaliador(pMin.getListaApts(), pMin.getNome(), menor);
                bancapMax.setAvaliador2(av);

                //conta quantas bancas cada professor está
                contaQtdBancasPorProfessor();
                //mostraBancas
                mostraBancas();
                //calcula o desvio padrao
                desvioPadrao = getDesvioPadraoBancaPorProfessor();
            }
        } while (desvioPadrao < desvioPadraoAnterior);
    }

    public GeraTFG_v1() {
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
            //conta quantas bancas cada professor está
            System.out.println("---Quantidade de bancas sem refinar---");
            contaQtdBancasPorProfessor();
            //começa a refinar
            refina();
            fileInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Erro ao abrir o arquivo.");
        }
    }

    public static void main(String[] args) {
        new GeraTFG_v1();
    }
}
