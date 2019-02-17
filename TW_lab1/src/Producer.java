public class Producer implements Runnable {
    private Buffer buffer;
    private long count;
    String name;

    public Producer(Buffer buffer,int count,String name) {
        this.buffer = buffer;
        this.count=count;
        this.name=name;
    }

    public void run() {

        for(int i = 0;  i < count;   i++) {
            buffer.put("message "+i,this.name);
        }

    }
}
