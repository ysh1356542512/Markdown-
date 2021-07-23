package cn.itcast.jvm.t4;

import java.io.IOException;

/**
 * -XX:+PrintCompilation  -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
 * -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation -XX:+TraceClassLoading -XX:+PrintAssembly
 */
public class Demo4_2 {

    public static void main(String[] args) throws IOException {
        User user = new User();
        user.setUsername("zhangsan");

        int c = 0;
        for (int i = 0; i < 3000; i++) {
            c = add(1, 2);
        }
        System.out.println(c);
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int sub(int a, int b) {
        return a - b;
    }
}
