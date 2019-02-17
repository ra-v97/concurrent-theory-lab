public class Consumer extends Thread{
    private TakePutMonitor tpMonitor;
    private String name;
    private int num;
    private String[] buffer;
    private int pipeCount;
    private int N;

    public Consumer(TakePutMonitor tpMonitor,int count,String name,int pipeCount,String[] buff,int N) {
        this.tpMonitor = tpMonitor;
        this.num=count;
        this.name=name;
        this.buffer =buff;
        this.pipeCount = pipeCount;
        this.N = N;
    }

    public void run() {
        for(int i = 0;  i < num;   i++) {
            tpMonitor.take(pipeCount+1);
            System.out.println(this.name+" received: "+buffer[i%N]);
            tpMonitor.put(pipeCount+1);
        }

    }
}