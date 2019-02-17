public class BinarySemaphore {

    private boolean free = true;

    public synchronized void sem_post() {
        if(this.free) throw new RuntimeException();
        this.free = true;
        notifyAll();
    }

    public synchronized void sem_wait() {
        try {
            while (!this.free) wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.free = false;
    }
}