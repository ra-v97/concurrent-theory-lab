public class Consumer implements Runnable {
    private Buffer buffer;
    private String name;
    private long num;

    public Consumer(Buffer buffer,long count,String name) {
        this.buffer = buffer;
        this.num=count;
        this.name=name;

    }

    public void run() {

        for(int i = 0;  i < num;   i++) {
            String message = buffer.take(this.name);
        }

    }
}
