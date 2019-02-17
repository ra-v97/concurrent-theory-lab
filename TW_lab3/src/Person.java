import java.util.ArrayList;
import java.util.List;

public class Person extends Thread{

    private WaiterMonitor waiterMonitor;
    private String name;
    private String sex;
    private int pairID;
    private int meetingNumber;

    public Person(String name, boolean sex, int pairID,int meetingNumber, WaiterMonitor waiterMonitor){
        this.name =name;
        this.sex = sex ? "Man" : "Women";
        this.pairID = pairID;
        this.meetingNumber = meetingNumber;
        this.waiterMonitor = waiterMonitor;
    }

    public static List<Person> createNPairs(int n,WaiterMonitor wm){
        List<Person> personList = new ArrayList<>();

        for(int i = 0 ; i<n ; i++){
            int meetingNumber = (int)(Math.random() * 10 + 1);
            personList.add(new Person("Adam"+i,true,i,meetingNumber,wm));
            personList.add(new Person("Eva"+i,false,i,meetingNumber,wm));
        }

        return  personList;
    }

    public void run(){
        for(int i = 0 ; i < meetingNumber ; i++){
            long sleepTime = (((int)(Math.random() * 10)) % 3) + 1 ;
            System.out.println(Thread.currentThread().getName()+": "+this.name+" -> I sleep for "+sleepTime+" s.");
            try{
                Thread.sleep(sleepTime*1000);
            }catch(InterruptedException e){e.printStackTrace();}

            System.out.println(Thread.currentThread().getName()+": "+this.name+" -> I want to book table");
            try{
                waiterMonitor.iWantTable(this.pairID);

                System.out.println(Thread.currentThread().getName()+": "+this.name+" -> I have romantic dinner with my "+(sex.equals("Man")?"Woman":"Man"));
                waiterMonitor.waitForPartner();
            }catch(InterruptedException e){e.printStackTrace();}
            System.out.println(Thread.currentThread().getName()+": "+this.name+" -> I go out from dinner no: "+i);
            waiterMonitor.freeTable();
        }
    }
}
