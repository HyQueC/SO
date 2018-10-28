
import java.util.LinkedList;


public class Escalonador{

    BCP getNextProcess(LinkedList<BCP>[] ready, int[] reg){
        BCP aux;

        for(int i = ready.length-1; i > 0; i--){
            if(!(ready[i].size() == 0)){
               aux = ready[i].pop();
                reg[0] = aux.getPC();
                reg[1] = aux.getX();
                reg[2] = aux.getY();

                return aux;
            }
        }
        if(ready[0].size() >0) {
            aux = ready[0].pop();
            reg[0] = aux.getPC();
            reg[1] = aux.getX();
            reg[2] = aux.getY();

            return aux;
        }
        return null;
    }

    // Inserção do processo de maneira ordenada pela(o) prioridade/crédito atual.
    // Excessão no caso da fila de crédito 0, FIFO.
    void setProcessData(LinkedList<BCP>[] ready, LinkedList<BCP> blocked, BCP process, int PC, int X, int Y, int credit, int status){
        process.setData(PC, X, Y, credit);

        if(status == 1) blocked.addLast(process);
        else{
            if(credit > 0)ready[process.getCredit()].addFirst(process);
            else ready[0].addLast(process);
        }

    }

    void setFromBlocked(LinkedList<BCP>[] ready, LinkedList<BCP> blocked){

        if(blocked.getFirst().getStatus() == 0) {
            BCP aux = blocked.pop();
            if(aux.getCredit() > 0)ready[aux.getCredit()].addFirst(aux);
            else ready[0].addLast(aux);
        }

    }
}
