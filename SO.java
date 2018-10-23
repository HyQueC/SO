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
    static Escalonador esc;

    //Registradores de uso geral, registrador específico (PC) e o Quantum base.
    static int X, Y, PC, quantBase, changeNum, spentQuantum, executedInst;

    // Quantum calculado à medida que o processo em execução perde os créditos, e outras variáveis.
    static double changeAverage, instructAverage;
    static String filePath = "./processos/";

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
        }
    }


    static int executeProcess(BCP proc){
        if(proc == null){
            if(blocked.size() == 0) distributeCredits(table);

            return 0;
        }

        String cmd = proc.memoCMD[PC];

        // O Número de instruções a serem executadas a é igual ao número base de instruções
        // vezes o número de quantum atribuido ao processo
        double instruction = quantBase * Math.pow(2,(proc.getPriority()-proc.getCredit()));
        int i = 1;

        while(i <= instruction){
            for(int j = 1; j  <= quantBase; i++, j++){
                PC++;

                if(cmd.equals("COM"));

                else if(cmd.startsWith("X=")){
                    X = cmd.charAt(2) - '0';


                }else if(cmd.startsWith("Y=")){
                    Y = cmd.charAt(2) - '0';


                }else if(cmd.equals("E/S")){
                    spentQuantum++;
                    changeNum++;
                    esc.setProcessData(ready, blocked, proc, PC, X, Y, proc.getCredit()-1, 1);
                    return i;


                }else if(cmd.equals("SAIDA")){
                    spentQuantum++;
                    changeNum++;
                    esc.EndProcess(table, proc);
                    return i;


                }else {
                    System.out.println("Comando invalido, o processo deixara de ser executado");
                    changeNum++;
                    return i;
                }
            }
            spentQuantum++;
        }
        changeNum++;

        esc.setProcessData(ready, blocked, proc, PC, X, Y, proc.getCredit()-1, 0);
        return i;
    }

    public static void main(String[] args) throws FileNotFoundException{
        esc = new Escalonador();
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
        System.out.println();

        BCP poop = table.pop();

        /*
            Retirando o processo de table, não retira de ready


        */

        esc.setProcessData(ready, blocked, poop, PC, X, Y, poop.getCredit()-1, 0);
        int[] regList = new int[5];

        poop.setData(300, 25, 27, poop.getCredit());
        poop = esc.getNextProcess(ready, blocked, regList);

        PC = regList[0];
        X = regList[1];
        Y = regList[2];

        System.out.println(poop.getPC()+ " "+ poop.getX()+ " "+ poop.getY()+ " "+ poop.getCredit());
        System.out.println(PC+ " "+ X+ " "+Y+ " ");

        for(i = maxPrior; i >= 0; i--){
            it = ready[i].listIterator(0);
            while(it.hasNext()){
                aux = it.next();
                System.out.println(aux.progamName+ " " +aux.getCredit());
            }
        }
        System.out.println();



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
