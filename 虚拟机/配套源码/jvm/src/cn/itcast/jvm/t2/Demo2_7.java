package cn.itcast.jvm.t2;

import java.util.ArrayList;
import java.util.List;

/**
 * -Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDetails -verbose:gc
 */
public class Demo2_7 {
    private static final int _1KB = 128;
    public static void main(String[] args) {
        int i = 0;
        try {
            for (int j = 0; j < 1000000; j++) { // j=100, j=10000
                String s = String.valueOf(j);
                i++;
                System.out.println(s);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }

    }
}
