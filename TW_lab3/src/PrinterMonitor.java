import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrinterMonitor {
    private final Lock lock = new ReentrantLock();
    private final Condition notFreePrinter  = lock.newCondition();

    private static List<Printer> printers;

    private int freePrinterCount;

    PrinterMonitor(List<Printer> printers_){
        printers = printers_;
        this.freePrinterCount = printers.size();
    }

    public int bookPrinter() throws InterruptedException{
      lock.lock();
        try {
            while (freePrinterCount == 0)
                notFreePrinter.await();

            freePrinterCount--;
            System.out.println("Booking...");
            return printers.stream()
                    .filter(x->x.getState())
                    .findFirst()
                    .map(x->{x.book();return x.getPrinterID();})
                    .get();
        } finally {
            lock.unlock();
        }
    }

    public void freePrinter(int printerID) throws InterruptedException{
        lock.lock();
        try {
            System.out.println("Set free on: "+printerID);
            freePrinterCount++;

            printers.stream()
                    .filter(x->x.getPrinterID()==printerID)
                    .findFirst()
                    .get()
                    .setFree();
            notFreePrinter.signal();
        } finally {
            lock.unlock();
        }
    }

    public static Printer getPrinterByID(int printerID){
        return printers.stream()
                .filter(x->x.getPrinterID() == printerID)
                .findFirst()
                .get();
    }
}
