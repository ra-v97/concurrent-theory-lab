public class Consumer implements Runnable {
    private BoundedBuffer buffer;
    private String name;
    private long num;

    public Consumer(BoundedBuffer buffer,long count,String name) {
        this.buffer = buffer;
        this.num=count;
        this.name=name;

    }

    public void run() {
        String message;
        for(int i = 0;  i < num;   i++) {
            try{
                message = buffer.take().toString();
                System.out.println(this.name+" received: "+message);
            }catch (InterruptedException e){e.printStackTrace();}

        }

    }
}
