public class Producer implements Runnable {
    private BoundedBuffer buffer;
    private long count;
    String name;

    public Producer(BoundedBuffer buffer,int count,String name) {
        this.buffer = buffer;
        this.count=count;
        this.name=name;
    }

    public void run() {

        for(int i = 0;  i < count;   i++) {
            try{
                buffer.put("Message" + i+ " from "+this.name);
            }catch (InterruptedException e){e.printStackTrace();}

        }

    }
}
