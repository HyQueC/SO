import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

public class Escalonador{

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

//método de distribuição de créditos utilizado após o consumo de todos os créditos distribuídos
    static void distributeCredits(LinkedList<BCP> table){
        ListIterator<BCP> it = table.listIterator(0);
        BCP aux;
        while(it.hasNext()){
            aux = it.next();
            aux.setCredit(aux.getPriority());
        }
    }

// Preenchimento das filas de "pronto" de acordo com as prioridades/créditos de cada processo
// índice 1 do array  corresponde à prioridade mais baixa
// índice 0 corresponde aos Processo cujos créditos acabaram

    static void populateReady(LinkedList[] ready, LinkedList<BCP> table){
        ListIterator<BCP> it = table.listIterator(0);
        BCP aux;
        while(it.hasNext()) {
            aux = it.next();
            setProcess(ready, aux);
        }
    }
// Inserção do processo de maneira ordenada pela(o) prioridade/crédito atual.
    static void setProcess(LinkedList<BCP>[] ready, BCP process){

        ready[process.getCredit()].add(process);
        Collections.sort(ready[process.getCredit()]);
    }

    static BCP getProcess(LinkedList<BCP>[] ready){

        for(int i = ready.length-1; i > 0; i--){
            if(!(ready[i].size() == 0)){
                return ready[i].pop();
            }
        }

        return null;
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
        table = new LinkedList<>();
        blocked = new LinkedList<>();
        int maxPrior, i;

//Preenchimento da Tabela de Processos com o BCP de cada processo
        for(i = 1; i < 10; i++){
            table.addLast(readProcessFile("0" + i + ".txt"));
        }
        table.addLast(readProcessFile("10.txt"));
        maxPrior = readPriority( "prioridades.txt", table);

//Criação e preenchimento das filas de prontos com os BCPs presentes na Tabela de Processos
// de acordo com a prioridade de cada um.
        ready =  new LinkedList[maxPrior+1];
        for(i = 0; i <= maxPrior; i++){
            ready[i] = new LinkedList<BCP>();
        }
        populateReady(ready, table);

        while(table.size() != 0){

            executeProcess(getProcess(ready));
        }
        /*
        ListIterator<BCP> it;
        BCP aux;
        for(i = 0; i < maxPrior; i++){
            it = ready[i].listIterator(0);
            while(it.hasNext()){
                aux = it.next();
                System.out.println(aux.progamName);
            }
        }
        */

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
