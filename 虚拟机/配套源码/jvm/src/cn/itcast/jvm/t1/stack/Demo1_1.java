package cn.itcast.jvm.t1.stack;

/**
 * 演示栈帧
 */
public class Demo1_1 {
    public static void main(String[] args) throws InterruptedException {
        method1();
    }

    private static void method1() {
        method2(1, 2);
    }

    private static int method2(int a, int b) {
        int c =  a + b;
        return c;
    }
}
