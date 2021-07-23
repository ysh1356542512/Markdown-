package cn.itcast.jvm.t3.load;

public class Load9 {
    public static void main(String[] args) {
//        Singleton.test();
        Singleton.getInstance();
    }

}

class Singleton {

    public static void test() {
        System.out.println("test");
    }

    private Singleton() {}

    private static class LazyHolder{
        private static final Singleton SINGLETON = new Singleton();
        static {
            System.out.println("lazy holder init");
        }
    }

    public static Singleton getInstance() {
        return LazyHolder.SINGLETON;
    }
}
