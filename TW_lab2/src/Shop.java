public class Shop {
    int basket_num;
    private CountingSemaphore sem;

    public Shop(int basket_num){
        this.basket_num=basket_num;
        this.sem = new CountingSemaphore(this.basket_num);
    }

    public void takeBasket(String client){
        System.out.println("Shop:"+client+" wanna basket.");
        sem.sem_wait();
        System.out.println("Shop:"+client+" has basket.");
    }
    public void returnBasket(String client){
        System.out.println("Shop:"+client+" returns basket.");
        sem.sem_post();
    }
}
