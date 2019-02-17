public class Printer {
    private int printerID;

    private boolean isFree = true;

    public Printer(int printerID) {
        this.printerID = printerID;
    }

    public boolean getState(){
        return this.isFree;
    }
    public int getPrinterID(){
        return this.printerID;
    }
    public void book(){
        isFree = false;
    }
    public void setFree(){
        isFree = true;
    }
    public void print(String task){
        System.out.println("Printer "+ printerID+": "+task);
    }
}
