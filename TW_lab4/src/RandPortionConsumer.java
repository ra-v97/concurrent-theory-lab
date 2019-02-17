import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandPortionConsumer extends Thread{
    private String name;
    private int portionNumberToProduce;
    private int M;
    private PCBuffer buffer;
    private List<String> timeBuff = new LinkedList<>();

    public RandPortionConsumer(String name, int portionNumberToProduce, int m,PCBuffer buffer) {
        this.name = name;
        this.portionNumberToProduce = portionNumberToProduce;
        M = m;
        this.buffer = buffer;
    }

    public void run(){
        Random generator = new Random();
        int[] portions = {1,5,10,20,30,40,50,60,70,80,90,100,200,300,400,500,600,700,800,900,1000,2000,2500,5000,10000,20000,30000,50000};
//        while(portionNumberToProduce > 0){
//            int currentPortionSize = generator.nextInt(M)+1;
//            if(currentPortionSize > portionNumberToProduce)currentPortionSize = portionNumberToProduce;
        for(int i = 0; i< portions.length;i++){
            int currentPortionSize = portions[i];
            for(int j  =0;j<1;j++) {
                try {
                    long begin = System.nanoTime();
                    buffer.take(currentPortionSize);
                    long end = System.nanoTime();
                    timeBuff.add("fair" + ";" + "take" + ";" + M + ";" + currentPortionSize + ";" + (end - begin) + ";" + "1000P/1000K" + "\r\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println("Thread: " +name+" no."+ Thread.currentThread().getId() + " takes: "+currentPortionSize+" elements;");
                portionNumberToProduce -= currentPortionSize;
            }
        }
        Path path = Paths.get("./data.csv");

        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path,StandardOpenOption.APPEND))
        {
            for(int i = 0 ; i < timeBuff.size() ; i++)
                writer.write(timeBuff.get(i));
        }catch (Exception e){e.printStackTrace();}
    }
}
