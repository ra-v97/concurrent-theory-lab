import java.util.concurrent.Semaphore;

public class NaiveBuffer implements PCBuffer {
    private final int M;
    private final boolean[] buffer;
    private final int producerCount;
    private final int consumerCount;
    private int activeProducers;
    private int activeConsumers;

    private Semaphore bufferSemWaitingForPlace;
    private Semaphore bufferSemWaitingForProducts;

    private int freePlaceInBuffer;
    private boolean bufferPosible = true;
    private Semaphore buffLock = new Semaphore(1);

    public NaiveBuffer(int m, int producerCount, int consumerCount) {
        M = m;
        this.producerCount = producerCount;
        this.consumerCount = consumerCount;
        this.activeProducers = producerCount;
        this.activeConsumers = consumerCount;
        this.bufferSemWaitingForPlace = new Semaphore(2*M);
        this.bufferSemWaitingForProducts = new Semaphore(0);
        this.freePlaceInBuffer = 2*M;
        this.buffer = new boolean[2*M];
        for(int i=0;i<2*M;i++){
            buffer[i]=false;
        }
    }


    public void put(int elements){
        try{
            if(elements > freePlaceInBuffer) {
                bufferSemWaitingForPlace.acquire(elements);
            }
        }catch(InterruptedException e){e.printStackTrace();}

        try{
            while(!bufferPosible){
                buffLock.acquire();
            }
            bufferPosible = false;
        }catch(InterruptedException e){ System.out.println(e.getStackTrace()); }
        int counter = 0;

        for(int i=0;i<2*M;i++){
            if(!buffer[i]){
                buffer[i]=true;
                counter++;
                if(counter==elements)break;
            }
        }
        System.out.println("Thread: no."+ Thread.currentThread().getId() + " puts: "+elements+" elements;");
        bufferPosible = true;
        buffLock.release();
        freePlaceInBuffer-=elements;
        bufferSemWaitingForProducts.release(elements);
    }

    public void take(int elements){
        try{

            if(elements > 2*M - freePlaceInBuffer) {
                bufferSemWaitingForProducts.acquire(elements);
            }
        }catch(InterruptedException e){e.printStackTrace();}

        try{
            while(!bufferPosible){
                buffLock.acquire();
            }
            bufferPosible = false;
        }catch(InterruptedException e){e.printStackTrace();}
        int counter = 0;

        for(int i=0;i<2*M;i++){
            if(buffer[i]){
                buffer[i]=false;
                counter++;
                if(counter==elements)break;
            }
        }
        System.out.println("Thread: no."+ Thread.currentThread().getId() + " takes: "+elements+" elements;");
        bufferPosible = true;
        buffLock.release();
        freePlaceInBuffer+=elements;
        bufferSemWaitingForPlace.release(elements);
    }
}
