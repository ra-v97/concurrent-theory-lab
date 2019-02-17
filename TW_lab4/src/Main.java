public class Main {
    public static void main(String[] args) {
//        int pipeCount = 5;
//        int buffLength = 20;
//        int productsCount = 100;
//
//        TakePutMonitor tp = new TakePutMonitor(pipeCount+2,buffLength);
//        String[] buff = new String[buffLength];
//
//        Producer p1 = new Producer(tp,productsCount,"Prod",buff,buffLength);
//        Consumer c1 = new Consumer(tp,productsCount,"Cons",pipeCount,buff,buffLength);
//
//        //Pipe are segregated by theirs ID
//        Pipe[] pipes = new Pipe[pipeCount];
//
//        for(int i = 0 ; i< pipeCount ; i++){
//            pipes[i] = new Pipe(tp,productsCount,i+1,buff,buffLength,(i%pipeCount)*10);
//        }
//
//        p1.start();
//        c1.start();
//        for(int i = 0 ; i< pipeCount ; i++){
//            pipes[i].start();
//        }
//        try{
//            p1.join();
//            c1.join();
//        }catch (InterruptedException e){e.printStackTrace();}

        int M=100000;
        int producers = 100;
        int consumers = 100;
        int portions = 10000;

        NaiveBuffer naivebuffer = new NaiveBuffer(M,producers,consumers);
        FairBuffer fairbuffer = new FairBuffer(M);
        RandPortionProducer[] prods = new RandPortionProducer[producers];
        RandPortionConsumer[] cons = new RandPortionConsumer[consumers];

        for(int i = 0 ; i < producers ;i++){
            prods[i]=new RandPortionProducer("P"+(i+1),consumers*portions,M,fairbuffer);
        }
        for(int i = 0 ; i < consumers ;i++){
            cons[i]=new RandPortionConsumer("C"+(i+1),producers*portions,M,fairbuffer);
        }
        for(int i = 0 ; i < producers ;i++){
            prods[i].start();
        }
        for(int i = 0 ; i < consumers ;i++){
            cons[i].start();
        }
        try{
            for(int i = 0 ; i < producers ;i++){
                prods[i].join();
            }
            for(int i = 0 ; i < consumers ;i++){
                cons[i].join();
            }
        }catch (InterruptedException e){e.printStackTrace();}

    }

}
