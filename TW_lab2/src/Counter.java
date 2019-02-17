public class Counter {
    private long count;

    public Counter() {
        this.count = 0;
    }

    public void inc() {
        this.count++;
    }

    public void dec() {
        this.count--;
    }

    public long getCount() {
        return this.count;
    }
}
