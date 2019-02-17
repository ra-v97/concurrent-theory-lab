public class Buffer {

    private String buff=null;

    public synchronized void put(String s,String name) {
        try{
            while(buff != null) wait();
        }catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("Prod "+ name+ " Puts:"+ s);
        buff = s;
        notifyAll();
    }

    public synchronized String take(String name) {
        try{
            while(buff == null) wait();
        }catch (InterruptedException e) { e.printStackTrace(); }
        String output = this.buff;
        System.out.println("Cons " + name +" take: "+ output);
        buff=null;
        notifyAll();
        return output;
    }
}
