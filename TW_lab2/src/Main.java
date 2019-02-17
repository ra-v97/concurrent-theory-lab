//semafor dwustronnie ograniczony
//semafor uogolniony
public class Main {
    public static void main(String[] args) {
//        BinarySemaphore sem = new BinarySemaphore();
//        Counter count = new Counter();
//        long operations = 100000000;
//        IncDecThread t1 = new IncDecThread(count,operations,true,sem);
//        IncDecThread t2 = new IncDecThread(count,operations,false,sem);
//
//        Thread th1 = new Thread(t1);
//        Thread th2 = new Thread(t2);
//        th1.start();
//        th2.start();
//        try {
//            th1.join();
//            th2.join();
//        } catch (InterruptedException e) { e.printStackTrace(); }
//        System.out.println(count.getCount());

        Shop shop = new Shop(5);

        Customer[] customers = new Customer[10];
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            customers[i] = new Customer("C" + i, 3, shop);
            threads[i] = new Thread(customers[i]);
        }

        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        try {
            for (int i = 0; i < 10; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
