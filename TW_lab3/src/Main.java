import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

//        int producerNumber = 10;
//        int consumerNumber = 5;
//
//        BoundedBuffer buffer = new BoundedBuffer();
//
//        Producer[] producer = new Producer[producerNumber];
//        Thread[] prod_threads  = new Thread[producerNumber];
//
//        Consumer[] consumer = new Consumer[consumerNumber];
//        Thread[] cons_threads  = new Thread[consumerNumber];
//
//        for(int i=0;i<producerNumber ;i++){
//            producer[i]= new Producer(buffer,consumerNumber,"P"+i);
//            prod_threads[i] = new Thread(producer[i]);
//        }
//
//        for(int i=0;i<consumerNumber;i++){
//            consumer[i]= new Consumer(buffer,producerNumber,"C"+i);
//            cons_threads[i] = new Thread(consumer[i]);
//        }
//
//        for (int i = 0 ; i< producerNumber ; i++){
//            prod_threads[i].start();
//        }
//        for (int i = 0 ; i< consumerNumber ; i++){
//            cons_threads[i].start();
//        }
//
//        try {
//
//            for (int i = 0 ; i< consumerNumber ; i++){
//                cons_threads[i].join();
//            }
//            for (int i = 0 ; i< producerNumber ; i++){
//                prod_threads[i].join();
//            }
//        } catch (InterruptedException e) { e.printStackTrace(); }

//        int usersCount =10;
//        int printersCount =3;
//
//        List<Printer> printers = new ArrayList<>();
//        for(int i = 0 ; i < printersCount ; i++){
//            printers.add(new Printer(i));
//        }
//
//        PrinterMonitor pm = new PrinterMonitor(printers);
//
//        PrinterUser[] users = new PrinterUser[usersCount];
//
//        for(int i = 0 ; i < usersCount ; i++){
//            users[i] = new PrinterUser("U"+i,pm);
//        }
//
//        for(int i = 0 ; i < usersCount ; i++){
//            users[i].start();
//        }
//
//        for(int i = 0 ; i < usersCount ; i++){
//            try{
//                users[i].join();
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//
//        }

          int pairsNumber = 10;
          WaiterMonitor waiter = new WaiterMonitor(pairsNumber);

          List<Person> persons = Person.createNPairs(pairsNumber,waiter);

          persons.forEach(Person::start);

              persons.forEach(person -> {
                  try{
                      person.join();
                  }catch(InterruptedException e){e.printStackTrace(); }
              });


    }
}
