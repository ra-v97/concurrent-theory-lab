import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaiterMonitor {

    private final Lock lock = new ReentrantLock();
    private final Condition tableCond  = lock.newCondition();
    private int personsByTable;

    private  Condition[] waitingForPair;
    private Boolean[] waitingForPairIsEmpty;
    private boolean isPartnerSitting;
    private final Condition partnerCond  = lock.newCondition();

    WaiterMonitor(int pairsCount){
        this.waitingForPair = new Condition[pairsCount];
        this.waitingForPairIsEmpty = new Boolean[pairsCount];

        personsByTable = 0;
        isPartnerSitting =false;

        for(int i = 0 ; i < pairsCount ; i++){
            waitingForPair[i] = lock.newCondition();
            this.waitingForPairIsEmpty[i] = Boolean.TRUE;
        }
    }

    public void iWantTable(int pairID) throws InterruptedException{
        lock.lock();
        try{
            if(waitingForPairIsEmpty[pairID]){
                waitingForPairIsEmpty[pairID] = Boolean.FALSE;
                waitingForPair[pairID].await();
            }
            else{
                while(personsByTable > 0){
                    tableCond.await();
                }

                personsByTable = 2;
                waitingForPairIsEmpty[pairID] = Boolean.TRUE;
                System.out.println("\t>>Waiter -> Table is booked.");
                waitingForPair[pairID].signal();
            }

        }
        finally{
            lock.unlock();
        }
    }

    public void freeTable(){
        lock.lock();
        try{
            personsByTable--;

            if(personsByTable == 0){
                System.out.println("\t>>Waiter -> Table is free");
                tableCond.signal();
            }
        }
        finally{
            lock.unlock();
        }
    }
    public void waitForPartner()throws InterruptedException{
        lock.lock();
        try{

            if(!isPartnerSitting){
                isPartnerSitting =true;
                partnerCond.await();
            }
            else{
                isPartnerSitting =false;
                partnerCond.signal();
            }
        }
        finally{
            lock.unlock();
        }
    }
}
