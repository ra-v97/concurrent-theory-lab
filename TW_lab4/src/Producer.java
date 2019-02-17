public class Producer extends Thread {
    private TakePutMonitor tpMonitor;
    private String name;
    private int num;
    private String[] buffer;
    private int N;

    Producer(TakePutMonitor tpMonitor,int count,String name,String[] buff,int N) {
        this.tpMonitor = tpMonitor;
        this.num=count;
        this.name=name;
        this.buffer =buff;
        this.N =N;
    }

    public void run() {
        for(int i = 0;  i < num;   i++) {
            tpMonitor.take(0);
            buffer[i% N] = "Msg from "+this.name+" num: "+i+"X";
            tpMonitor.put(0);
        }

    }
}
