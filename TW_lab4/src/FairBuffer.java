import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairBuffer implements PCBuffer {

    private final Lock lock = new ReentrantLock();
    private final Condition firstProd  = lock.newCondition();
    private final Condition firstCons = lock.newCondition();
    private final Condition restProd  = lock.newCondition();
    private final Condition restCons = lock.newCondition();
    private boolean firstProdWaits, firstConsWaits;

    private final int M;
    private final boolean[] buffer;
    private int freePlaceInBuffer;
    private boolean bufferPosible = true;

    public FairBuffer(int M){
        this.M = M;
        freePlaceInBuffer = 2*M;
        firstProdWaits = false;
        firstConsWaits = false;
        buffer = new boolean[2*M];
        for(int i=0;i<2*M;i++){
            buffer[i]=false;
        }
    }

    public void put(int elements) throws InterruptedException {
        lock.lock();
        try {
            //is anyone awaits?
            if(firstProdWaits)
                restProd.await();

            while( freePlaceInBuffer < elements) {
                firstProd.await();
                firstProdWaits = true;
            }
            freePlaceInBuffer-=elements;

            int counter = 0;
            for(int i=0;i<2*M;i++){
                if(!buffer[i]){
                    buffer[i]=true;
                    counter++;
                    if(counter==elements)break;
                }
            }
            System.out.println("Thread: no."+ Thread.currentThread().getId() + " puts: "+elements+" elements;");
            firstConsWaits =false;
            restProd.signal();
            firstCons.signal();

        } finally {
            lock.unlock();
        }
    }

    public void take(int elements ) throws InterruptedException {
        lock.lock();
        try {
            //any consumer awaits
            if(firstConsWaits)
                restCons.await();

            while(2*M - freePlaceInBuffer < elements) {
                firstCons.await();
                firstConsWaits = true;
            }
            freePlaceInBuffer+=elements;
            int counter = 0;

            for(int i=0;i<2*M;i++){
                if(buffer[i]){
                    buffer[i]=false;
                    counter++;
                    if(counter==elements)break;
                }
            }
            System.out.println("Thread: no."+ Thread.currentThread().getId() + " takes: "+elements+" elements;");
            firstProdWaits =false;
            restCons.signal();
            firstProd.signal();

        } finally {
            lock.unlock();
        }
    }

}
