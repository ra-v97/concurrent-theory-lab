public class CountingSemaphore {

    private int state;

    public CountingSemaphore(int sem_initial_value){
        this.state=sem_initial_value;
    }

    public synchronized void sem_post() {
        this.state = state+1;
        notifyAll();
    }

    public synchronized void sem_wait() {
        try {
            while (this.state <= 0) wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.state = state-1;
    }
}