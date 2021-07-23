package cn.itcast.jvm.t3.bytecode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Demo3_11_3 {

    public static void main(String[] args) {
        try {
            Method test = Demo3_11_3.class.getMethod("test");
            test.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        System.out.println("ok");
    }
}
