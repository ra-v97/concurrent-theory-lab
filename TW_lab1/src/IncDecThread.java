public class IncDecThread implements Runnable {
    private Counter counter;
    private long num;
    boolean should_inc;

    public IncDecThread(Counter c,long n,boolean should_inc){
        this.should_inc = should_inc;
        this.counter=c;
        this.num=n;
    }

    public void run(){
        for(int i = 0 ; i < num ; i++)  {
            if(should_inc){
                this.counter.inc();
            }
            else {
                this.counter.dec();
            }
        }
    }
}
