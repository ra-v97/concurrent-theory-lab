import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
//        Counter counter = new Counter();
//
//        IncDecThread inc_thread = new IncDecThread(counter,100000000,true );
//        IncDecThread dec_thread = new IncDecThread(counter,100000000,false );
//
//        Thread t1 = new Thread(inc_thread);
//        Thread t2 = new Thread(dec_thread);
//
//        t1.start();
//        t2.start();
//
//        try {
//            t1.join();
//            t2.join();
//        } catch (InterruptedException e) { e.printStackTrace(); }
//
//        System.out.println("Counter value: "+ counter.getCount());


        Buffer buffer = new Buffer();
        Producer[] producer = new Producer[5];
        Thread[] prodthreads  = new Thread[5];


        for(int i=0;i<5;i++){
            producer[i]= new Producer(buffer,10000,""+i);
            prodthreads[i] = new Thread(producer[i]);
        }
        Consumer c1 = new Consumer(buffer,100000000,"XXX");

        Thread t1 = new Thread(c1);


        t1.start();
        for (int i = 0 ; i< 5 ; i++){
            prodthreads[i].start();
        }

        try {
            t1.join();
            for (int i = 0 ; i< 5 ; i++){
                prodthreads[i].join();
            }
        } catch (InterruptedException e) { e.printStackTrace(); }


    }
}
