public class Pipe extends Thread{
    private TakePutMonitor tpMonitor;
    private int id;
    private int num;
    private String[] buffer;
    private int delay;
    private int N;

    Pipe(TakePutMonitor tpMonitor,int count,int id,String[] buff,int N,int delay) {
        this.tpMonitor = tpMonitor;
        this.num=count;
        this.id=id;
        this.buffer =buff;
        this.delay=delay;
        this.N = N;
    }

    public void run() {
        for(int i = 0;  i < num;   i++) {
            tpMonitor.take(this.id);
            buffer[i% N] = process(buffer[i% N]);
            tpMonitor.put(this.id);
        }

    }

    private String process(String input) {
        System.out.println("Thread: " + Thread.currentThread().getId() + " is processing msg: " + input);
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return input + ">>" + this.id;
    }
}