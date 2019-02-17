public class Customer implements Runnable {

    private String name;
    private int shopping_count;
    private Shop shop;

    public Customer(String name, int shopping_count, Shop shop) {
        this.name = name;
        this.shopping_count = shopping_count;
        this.shop = shop;
    }

    private void doShopping(Shop shop) {
        System.out.println(name + ": Enter shop and search free basket");
        shop.takeBasket(this.name);
        System.out.println(name + ": Shopping time");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shop.returnBasket(this.name);
        System.out.println(name + ": End of shopping");
    }

    public void run() {
        for (int i = 0; i < shopping_count; i++) {
            doShopping(this.shop);
        }
    }
}
