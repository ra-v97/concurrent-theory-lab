import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class PrinterUser extends Thread {
    private String name;
    private PrinterMonitor printerMonitor;

    private String printTask;
    private long taskCounter = 0;

    public PrinterUser(String name,PrinterMonitor printerMonitor){
        this.name = name;
        this.printerMonitor = printerMonitor;
    }

    public void createPrintTask(String someContent){
        taskCounter++;
        this.printTask =
                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))+ ": Task "+taskCounter+ "-> " +someContent;
    }

    public void drukuj(int printerID){
        PrinterMonitor.getPrinterByID(printerID).print(this.printTask);
    }

    public void run(){

        int count = 5;

        for(int index = 1 ; index <= count ; index ++){
            createPrintTask("Some text "+ index+" User "+name);
            try{

                int printerID = printerMonitor.bookPrinter();
                drukuj(printerID);
                printerMonitor.freePrinter(printerID);

            }catch (InterruptedException e){e.printStackTrace();}


        }
    }
}
