import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

public class SO {

    //Tabela de Processos e filas de bloqueados e prontos.
    static LinkedList<BCP> table, blocked;
    static LinkedList<BCP>[] ready;
    static Escalonador esc;

    //Registradores de uso geral, registrador específico (PC) e o Quantum base.
    static int X, Y, PC, quantBase, changeNum, spentQuantum, executedInst;
    static int[] regList = new int[3];

    // Quantum calculado à medida que o processo em execução perde os créditos, e outras variáveis.
    static double changeAverage, instructAverage;
    static String filePath = "./processos/";
    static PrintWriter logW;
    static File log;

    // Leitura do nome, comandos e quantum a ser utilizado pelos processos.
    static BCP readProcessFile(String fileName){
        int i = 0;
        File file = new File(filePath + fileName);

        BCP process = new BCP();

        try {

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
    static int readPriority(String fileName, LinkedList<BCP> auxT){
        File file = new File(filePath + fileName);
        File file2 = new File(filePath + "quantum.txt");

        int maxPrior = 0;
        try {

            Scanner quantumRead = new Scanner(file2);
            quantBase = quantumRead.nextInt();
            quantumRead.close();

            Scanner sc = new Scanner(file);
            BCP aux;
            ListIterator<BCP> it = auxT.listIterator(0);
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
            aux.setPC(0);
            aux.setCredit(aux.getPriority());
            logW.println("Carregando "+ aux.progamName);
            table.add(aux);
            ready[aux.getCredit()].add(table.getLast());
        }
    }


    //método de distribuição de créditos utilizado após o consumo de todos os créditos distribuídos
    static void distributeCredits(LinkedList<BCP> table){
        ListIterator<BCP> it = table.listIterator(0);
        BCP aux;
        while(it.hasNext()){
            aux = it.next();
            aux.setCredit(aux.getPriority());
            if(aux.getStatus() == 0)ready[aux.getCredit()].addLast(aux);
        }
    }

    static void checkBlockedList(){

        if(blocked.size() > 0){
            BCP aux = new BCP();
            ListIterator<BCP> it = blocked.listIterator(0);
            while(it.hasNext()) {
                aux = it.next();
                aux.setBlockTime(aux.getBlockTime() - 1);
            }
            if (blocked.getFirst().getBlockTime() == 0) {
                blocked.getFirst().setStatusReady();
                esc.setFromBlocked(ready, blocked);
            }
        }
    }

    static int executeProcess(){

        checkBlockedList();
        BCP proc = esc.getNextProcess(ready, regList);

        if(proc == null){
            if(blocked.size() == 0 && blocked.getFirst().getCredit() == 0 && blocked.getLast().getCredit() == 0) distributeCredits(table);

            return 0;
        }
        PC = regList[0];
        X = regList[1];
        Y = regList[2];
        String cmd;

        proc.setStatusExecuting();
       logW.println("Executando "+ proc.progamName);
        // O Número de instruções a serem executadas a é igual ao número base de instruções
        // vezes o número de quantum atribuido ao processo
        double instruction = quantBase * Math.pow(2,(proc.getPriority()-proc.getCredit()));
        int i = 1;

        while(i <= instruction){
            for(int j = 1; j  <= quantBase; i++, j++){
                cmd = proc.memoCMD[PC];
                PC++;
                if(cmd.equals("COM"));

                else if(cmd.startsWith("X=")){
                    X = Integer.valueOf(cmd.substring(2));


                }else if(cmd.startsWith("Y=")){
                    Y = Integer.valueOf(cmd.substring(2));


                }else if(cmd.equals("E/S")){
                    spentQuantum++;
                    changeNum++;
                    proc.setStatusBlocked();
                    logW.println("E/S iniciada em "+ proc.progamName);
                    logW.println("Interrompendo "+ proc.progamName+ " apos "+ (i)+ " instrucoes");
                    esc.setProcessData(ready, blocked, proc, PC, X, Y, proc.getCredit()-1, proc.getStatus());

                    return i;


                }else if(cmd.equals("SAIDA")){
                    spentQuantum++;
                    changeNum++;
                    logW.println(proc.progamName+ " terminado. X="+X+ ". Y="+Y);
                    EndProcess(table, proc);
                    return i;


                }else {
                    System.out.println("Comando invalido, o processo deixara de ser executado");
                    proc.setStatusReady();
                    changeNum++;
                    return i;
                }
            }
            spentQuantum++;
        }
        changeNum++;
        proc.setStatusReady();
        logW.println("Interrompendo "+ proc.progamName+ " apos "+ (i-1)+ " instrucoes");
        esc.setProcessData(ready, blocked, proc, PC, X, Y, proc.getCredit()-1, proc.getStatus());

        return i-1;
    }

    static void EndProcess(LinkedList<BCP> table, BCP process){
        table.remove(process);
    }

    public static void main(String[] args) throws FileNotFoundException{
        LinkedList<BCP> auxT = new LinkedList<>();
        esc = new Escalonador();
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
        for(int k = 1; k < 11; k++) {
            executedInst = 0;
            spentQuantum = 0;
            changeNum = 0;
            X = 0;
            Y = 0;
            quantBase = k;
            if(k < 10)log = new File("./log0" + quantBase + ".txt");
            else log = new File("./log" + quantBase + ".txt");

            try {
                logW = new PrintWriter(log);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//Criação e preenchimento das filas de prontos com os BCPs presentes na Tabela de Processos
// de acordo com a prioridade de cada um.
            ready = new LinkedList[maxPrior + 1];
            for (i = 0; i <= maxPrior; i++) {
                ready[i] = new LinkedList<BCP>();
            }
            populateReadyTable(ready, table, auxT);

            while (table.size() > 0) executedInst += executeProcess();


            logW.println("MEDIA DE TROCAS: "+(double)(changeNum-1)/auxT.size());
            logW.println("MEDIA DE INSTRUCOES: "+(double)executedInst/spentQuantum);

            logW.close();
        }

    /*

        Algoritmo de Escalonamento
            - Gerência das múltiplas filas
            - Redistribuição de créditos e custeamento

        Estatísticas
            - Número médio de trocas de processo = changeNum/auxT.size();
            - Número médio de instruções executadas por quantum = executedInst/spentQuantum
            - Quantum base utilizado

        Log File
            - PrintWriter
            - Inclusão das Estatísticas

        Quantum Ideal
            - Formação de 2 gráficos a partir das 2 outras estatísticas com o Quantum Base como abscissa
            - Escolha do Quantum Ideal a partir do ponto de encontro nos dois gráficos.
        */
    }
}
