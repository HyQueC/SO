import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

public class SO {

    //Tabela de Processos e filas de bloqueados e prontos.
    static LinkedList<BCP> table, blocked;
    static LinkedList<BCP>[] ready;

    //Registradores de uso geral, registrador específico (PC) e o Quantum inicial.
    static int X, Y, PC, quantIni, instNum, changeNum;

    // Quantum calculado à medida que o processo em execução perde os créditos, e outras variáveis.
    static double quantum, changeAverage, instructAverage;
    static String filePath = "./processos/";

    // Leitura do nome, comandos e quantum a ser utilizado pelos processos.
    static BCP readProcessFile(String fileName){
        int i = 0;
        File file = new File(filePath + fileName);
        File file2 = new File(filePath + "quantum.txt");
        BCP process = new BCP();

        try {
            Scanner quantumRead = new Scanner(file2);
            quantIni = quantumRead.nextInt();
            quantumRead.close();

            Scanner sc = new Scanner(file);
            process.progamName = sc.nextLine();


            while (sc.hasNextLine()) {
                process.memoCMD[i] = sc.nextLine();
                i++;
            }

            sc.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return process;
    }

    // Leitura da prioridade de cada processo e distribuição de créditos equivalentes à prioridade
// Seleção da prioridade mais alta a fim de definir o número de filas do conjunto de processos prontos
    static int readPriority(String fileName, LinkedList<BCP> table){
        File file = new File(filePath + fileName);
        int maxPrior = 0;
        try {
            Scanner sc = new Scanner(file);
            BCP aux;
            ListIterator<BCP> it = table.listIterator(0);
            while (it.hasNext()) {

                aux = it.next();


                aux.setPriority(sc.nextInt());
                if(aux.getPriority() > maxPrior) maxPrior = aux.getPriority();
                aux.setCredit(aux.getPriority());
            }
            sc.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return maxPrior;
    }

// Preenchimento das filas de "pronto" de acordo com as prioridades/créditos de cada processo
// índice 1 do array  corresponde à prioridade mais baixa
// índice 0 corresponde aos Processo cujos créditos acabaram
    static void populateReadyTable(LinkedList<BCP>[] ready, LinkedList<BCP> table, LinkedList<BCP> auxT){
        ListIterator<BCP> it = auxT.listIterator(0);
        BCP aux;
        while(it.hasNext()) {
            aux = it.next();
            setProcess(ready, aux);
            table.add(aux);
        }
    }


    //método de distribuição de créditos utilizado após o consumo de todos os créditos distribuídos
    static void distributeCredits(LinkedList<BCP> table){
        ListIterator<BCP> it = table.listIterator(0);
        BCP aux;
        while(it.hasNext()){
            aux = it.next();
            aux.setCredit(aux.getPriority());
        }
    }


    // Inserção do processo de maneira ordenada pela(o) prioridade/crédito atual.
    static void setProcess(LinkedList<BCP>[] ready, BCP process){
        ready[process.getCredit()].add(process);
        Collections.sort(ready[process.getCredit()]);
    }

    static void executeProcess(BCP proc){
        if(proc == null){
            if(blocked.size() == 0) distributeCredits(table);

            return;
        }

        String cmd;
        changeNum++;
        X = proc.getX();
        Y = proc.getY();
        PC = proc.getPC();
        quantum = quantIni * Math.pow(2,(proc.getPriority()-proc.getCredit()));

        for(int i = 0; i < quantum; i++){
            cmd = proc.memoCMD[PC];
            PC++;

            if(cmd.equals("COM")) instNum++;

            else if(cmd.startsWith("X=")){
                X = cmd.charAt(2) - '0';
                instNum++;

            }else if(cmd.startsWith("Y=")){
                Y = cmd.charAt(2) - '0';
                instNum++;

            }else if(cmd.equals("E/S")){
                blocked.add(proc);
                proc.setStatusBlocked();
                proc.setData(PC, X, Y, proc.getCredit()-1);
                instNum++;
                return;

            }else if(cmd.equals("SAIDA")){
                table.remove(proc);
                instNum++;
                return;

            }else System.out.println("Comando invalido, o processo deixara de ser executado");

        }
        proc.setData(PC, X, Y, proc.getCredit()-1);
        ready[proc.getCredit()].add(proc);
    }

    public static void main(String[] args) throws FileNotFoundException{
        Escalonador esc = new Escalonador();
        LinkedList<BCP> auxT = new LinkedList<>();
        table = new LinkedList<>();
        blocked = new LinkedList<>();
        int maxPrior, i;

//Preenchimento da Tabela auxiliar com o BCP de cada processo para posterior carregamento ordenado deles
        for(i = 1; i < 10; i++){
            auxT.addLast(readProcessFile("0" + i + ".txt"));
        }
        auxT.addLast(readProcessFile("10.txt"));
        maxPrior = readPriority( "prioridades.txt", auxT);
        Collections.sort(auxT);

//Criação e preenchimento das filas de prontos com os BCPs presentes na Tabela de Processos
// de acordo com a prioridade de cada um.
        ready =  new LinkedList[maxPrior+1];
        for(i = 0; i <= maxPrior; i++){
            ready[i] = new LinkedList<BCP>();
        }
        populateReadyTable(ready, table, auxT);

        ListIterator<BCP> it;
        BCP aux;
        for(i = maxPrior; i >= 0; i--){
            it = ready[i].listIterator(0);
            while(it.hasNext()){
                aux = it.next();
                System.out.println(aux.progamName+ " " +aux.getCredit());
            }
        }

        BCP poop = table.pop();

        poop.setCredit(3);

        /*
            Retirando o processo de table, não retira de ready
            Processo recém colocado na fila está indo pro final da sua categoria de crédito
        */

        setProcess(ready, poop);

        for(i = maxPrior; i >= 0; i--){
            it = ready[i].listIterator(0);
            while(it.hasNext()){
                aux = it.next();
                System.out.println(aux.progamName+ " " +aux.getCredit());
            }
        }







         /*

        Algoritmo de Escalonamento
            - Gerência das múltiplas filas
            - Redistribuição de créditos e custeamento
        Log File
            - PrintWriter
            - Cálculo da média de instruções por quantum, de trocas, etc.
            - Quantum ideal
        */
    }
}
