package cn.itcast.jvm.t2;

import java.io.IOException;

/**
 * 测试终结器
 */
public class Demo2_6 {

    public static void main(String[] args) throws IOException {

        My my = new My();
        System.in.read();
        my = null;
        System.gc();

        System.in.read();
    }
}

class My {
    @Override
    protected void finalize() throws Throwable {
        System.out.println(Thread.currentThread() + " do finalize...");
    }
}
