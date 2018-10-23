import jdk.dynalink.linker.LinkerServices;

import java.util.LinkedList;
import java.util.ListIterator;

public class Escalonador{

    BCP getNextProcess(LinkedList<BCP>[] ready, LinkedList<BCP> blocked, int[] reg){
        BCP aux;
        if(blocked.size() > 0){
            ListIterator<BCP> it = blocked.listIterator(0);
            aux = it.next();
            aux.setBlockTime(aux.getBlockTime()-1);
            if(aux.getBlockTime() == 0){
                aux.setStatusReady();
                setProcessData(ready, blocked, aux, aux.getCredit()-1);
            }
        }
        for(int i = ready.length-1; i > 0; i--){
            if(!(ready[i].size() == 0)){
               aux = ready[i].pop();
                reg[0] = aux.getPC();
                reg[1] = aux.getX();
                reg[2] = aux.getY();
                reg[3] = aux.getCredit();
                reg[4] = aux.getPriority();

                return aux;
            }
        }
        aux = ready[0].pop();
        reg[0] = aux.getPC();
        reg[1] = aux.getX();
        reg[2] = aux.getY();
        reg[3] = aux.getCredit();
        reg[4] = aux.getPriority();
        return aux;
    }

    // Inserção do processo de maneira ordenada pela(o) prioridade/crédito atual.
    // Excessão no caso da fila de crédito 0, FIFO.
    void setProcessData(LinkedList<BCP>[] ready, LinkedList<BCP> blocked, BCP process, int PC, int X, int Y, int credit, int status){
        process.setData(PC, X, Y, credit);

        if(status == 1) {
            process.setStatusBlocked();
            blocked.addLast(process);
        }else {
            if (credit >= 0) ready[process.getCredit()].addFirst(process);
            else ready[0].addLast(process);
        }
    }

    void setProcessData(LinkedList<BCP>[] ready, LinkedList<BCP> blocked, BCP process, int credit){
        if (credit >= 0) ready[process.getCredit()].addFirst(process);
        else ready[0].addLast(process);
    }
    
    void EndProcess(LinkedList<BCP> table, BCP process){
        table.remove(process);
    }

}
