import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JFrame;

public class Mandelbrot extends JFrame {

    private final int THREAD_NUMBER = 16;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int MAX_ITER = 700;//570;
    private final double ZOOM = 150;
    private final int TASK_NUMBER = 16;
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

    Path path = Paths.get("./data.csv");
    private List<String> timeBuff = new LinkedList<>();
    private BufferedImage I;

    public Mandelbrot() {
        super("Mandelbrot Set");
        setBounds(100, 100, WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        TaskProvider taskProvider = new TaskProvider(getWidth(),getHeight());


        List<Task> tasks = taskProvider.fixedRowTaskGenerator(TASK_NUMBER);
        //List<Task> tasks = taskProvider.onePixelTaskGenerator();
        //List<Task> tasks = Arrays.asList(taskProvider.oneTaskGenerator());

        List<Future<Integer>> futures = new LinkedList<>();

        //Calculation begins
        for(int i = 0 ; i < 10 ; i++){
            long begin = System.nanoTime();
            tasks.forEach(t -> futures.add(executor.submit(t)));

            long summaryPixels = 0 ;

            for(int j = 0;j< futures.size();j++){
                try{
                    summaryPixels += futures.get(j).get();
                }catch (Exception e ){e.printStackTrace();}
            }
            long end = System.nanoTime();
            timeBuff.add(THREAD_NUMBER+";"+TASK_NUMBER+";"+MAX_ITER+";"+WIDTH+";"+HEIGHT+";"+(end-begin)+"\r\n");
            //Calculation ends

            System.out.println(i+">> I done with: "+summaryPixels+" pixels");
        }

        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path,StandardOpenOption.APPEND))
        {
            for(int i = 0 ; i < timeBuff.size() ; i++)
                writer.write(timeBuff.get(i));
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }

    class TaskProvider
    {
        private final int width;
        private final int high;

        public TaskProvider(int width, int high) {
            this.width = width;
            this.high = high;
        }

        public Task oneTaskGenerator(){
            return new Task(0,width-1,0,high-1);
        }

        public List<Task> onePixelTaskGenerator(){
            List<Task> tasks = new LinkedList<>();

            for(int i = 0 ; i < high ; i++){
                for(int j = 0 ; j< width ; j++){
                    tasks.add(new Task(j,j,i,i));
                }
            }
            return tasks;
        }

        public List<Task> fixedRowTaskGenerator(int taskNumber){
            if(taskNumber > high) throw new RuntimeException("To much tasks");

            List<Task> tasks = new LinkedList<>();
            int[] rowsPerTask = new int[taskNumber];

            int rowsBasic = this.high / taskNumber;
            int restRows = this.high % taskNumber;

            for(int i = 0 ; i < taskNumber ; i++ )rowsPerTask[i]=rowsBasic;
            for(int i = 0 ; i < restRows ; i++)rowsPerTask[i]++;

            int lastRowNumber =0 ;
            for(int i = 0 ; i < taskNumber ; i++){
                tasks.add(new Task(0,this.width-1,lastRowNumber,lastRowNumber+rowsPerTask[i]-1));
                lastRowNumber=lastRowNumber+rowsPerTask[i];
            }
            return tasks;
        }

    }

    class Task implements Callable<Integer>
    {
        private final int x_begin;
        private final int x_end;
        private final int y_begin;
        private final int y_end;

        //private final int[][] output;

        private double zx, zy, cX, cY, tmp;

        public Task(int x_begin,int x_end, int y_begin, int y_end){
            this.x_begin = x_begin;
            this.x_end = x_end;
            this.y_begin = y_begin;
            this.y_end = y_end;
            //output = new int[y_end-y_begin + 1][x_end-x_begin + 1];
        }

        public Integer call()
        {

            for (int y = y_begin,j=0; y <= y_end; y++,j++) {
                for (int x = x_begin,i=0; x <= x_end; x++,i++) {
                    zx = zy = 0;
                    cX = (x - 400) / ZOOM;
                    cY = (y - 300) / ZOOM;
                    int iter = MAX_ITER;
                    while (zx * zx + zy * zy < 4 && iter > 0) {
                        tmp = zx * zx - zy * zy + cX;
                        zy = 2.0 * zx * zy + cY;
                        zx = tmp;
                        iter--;
                    }
                    //output[j][i]= iter | (iter << 8);
                    I.setRGB(x, y, iter | (iter << 8));
                }
            }
            return (x_end - x_begin + 1) * (y_end - y_begin + 1);
        }
    }

    public static void main(String[] args) {
        new Mandelbrot().setVisible(true);
    }
}
