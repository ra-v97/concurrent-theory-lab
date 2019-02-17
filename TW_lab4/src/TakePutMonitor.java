import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TakePutMonitor {
    private final int M; //process count
    private final int N; //buffer size

    private final int[] howMany;
    private final Condition[] cond;

    private final Lock lock = new ReentrantLock();

    public TakePutMonitor(int processCount , int bufferSize){
        this.M = processCount;
        this.N = bufferSize;
        this.howMany = new int[M];
        this.cond = new Condition[M];
        for(int i =0 ; i< M ; i++){
            this.cond[i] = lock.newCondition();
            this.howMany[i]=0;
        }
        this.howMany[0]=N;
    }

    public void take(int id){
        lock.lock();
        try{
            if(howMany[id]==0){
                cond[id].await();
            }
            howMany[id]--;
        }catch (InterruptedException e){e.printStackTrace();}
        finally { lock.unlock(); }
    }

    public void put(int id){
        lock.lock();
        int index = (id+1)%M;
        howMany[index]++;
        cond[index].signal();
        lock.unlock();
    }
}
