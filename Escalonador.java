import java.util.LinkedList;

public class Escalonador{

    BCP getProcess(LinkedList<BCP>[] ready){

        for(int i = ready.length-1; i > 0; i--){
            if(!(ready[i].size() == 0)){
                return ready[i].pop();
            }
        }
        return ready[0].pop();
    }

}
