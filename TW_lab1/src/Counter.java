public class Counter {
    private long count;

    public Counter(){
        this.count = 0;
    }

    public synchronized void inc() {
        this.count++;
    }
    public  synchronized void dec() {
        this.count--;
    }
    public long getCount(){
        return this.count;
    }
}
