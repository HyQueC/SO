public class BCP implements Comparable<BCP> {

    private int PC, X, Y, status, priority, credit, blockCount;
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
    public void setStatusBlocked() { this.status = 1; }

    public void setPriority(int priority) { this.priority = priority; }

    public void setCredit(int credit) { this.credit = credit; }

    public void setBlockCount(int blockCount) { this.blockCount = blockCount; }

    public int getPC() {return PC;}

    public int getX() {return X;}

    public int getY() { return Y; }

    public int getStatus() { return status; }

    public int getPriority() { return priority; }

    public int getCredit() { return credit; }

    public int getBlockCount() { return blockCount; }
}
