public class BCP implements Comparable<BCP> {

    private int PC, X, Y, status, priority, credit, blockTime;
    String progamName;
    String[] memoCMD = new String[21];

// Critério de comparação ao reposicionar o processo recém executado.
    public int compareTo(BCP b){

        return b.getCredit() - this.getCredit();
    }

    //Atualização dos dados do Processo no BCP pré-interrupção.
    void setData(int PC, int X, int Y, int credit){
        setPC(PC);
        setX(X);
        setY(Y);
        setCredit(credit);
    }

    public void setPC(int PC) { this.PC = PC; }

    public void setX(int x) { X = x; }

    public void setY(int y) { Y = y; }

    public void setStatusReady() { this.status = 0; }
    public void setStatusExecuting() { this.status = -1;}
    public void setStatusBlocked() {
        this.status = 1;
        setBlockTime(3);
        // O BlockTime foi definido como 3 para manter a lógica de liberação do processo depois de 2 Processos
        // serem executados. Sempre que o método de execução é chamado, é feita a checagem da lista de bloqueados antes
        // mesmo do escalonador efetivamente escolher o próximo processo, logo, se o BlockTime fosse 2, ele chegaria a 0
        // (que é quando o processo é desbloqueado) após a execução de apenas um processo.

        /*
            Processo bloqueado (BlockTime = 2)
            Checa a lista de bloqueados -> BlockTime = 1
            Executa o processo escolhido
            Termina o quantum do processo
            Checa a lista de bloqueados -> BlockTime = 0
            O Processo bloqueado retorna para a fila de prontos antes da hora
         */

    }

    public void setBlockTime(int blockTime) { this.blockTime = blockTime; }

    public void setPriority(int priority) { this.priority = priority; }

    public void setCredit(int credit) {
        if(credit >= 0) {
            this.credit = credit;
        }
    }

    public int getPC() {return PC;}

    public int getX() {return X;}

    public int getY() { return Y; }

    public int getStatus() { return status; }

    public int getPriority() { return priority; }

    public int getCredit() { return credit; }

    public int getBlockTime() { return blockTime; }

}
