public interface PCBuffer {
    public void put(int elements) throws InterruptedException;
    public void take(int elements) throws InterruptedException;
}
