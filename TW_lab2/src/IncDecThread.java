public class IncDecThread implements Runnable {
    private Counter counter;
    private long num;
    boolean should_inc;

    BinarySemaphore sem;

    public IncDecThread(Counter c,long n,boolean should_inc,BinarySemaphore sem){
        this.should_inc = should_inc;
        this.counter=c;
        this.num=n;
        this.sem=sem;
    }

    public void run(){
        for(int i = 0 ; i < num ; i++)  {
            if(should_inc){
                sem.sem_wait();
                this.counter.inc();
                sem.sem_post();
            }
            else {
                sem.sem_wait();
                this.counter.dec();
                sem.sem_post();
            }
        }
    }
}
