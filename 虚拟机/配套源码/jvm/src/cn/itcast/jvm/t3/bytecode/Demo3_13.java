package cn.itcast.jvm.t3.bytecode;

public class Demo3_13 {

    public static void main(String[] args) {
        Object lock = new Object();
        synchronized (lock) {
            System.out.println("ok");
        }
    }
}
