package cn.itcast.jvm.t3.load;

import java.io.IOException;

/**
 * 解析的含义
 */
public class Load2 {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
//        ClassLoader classloader = Load2.class.getClassLoader();
//        Class<?> c = classloader.loadClass("cn.itcast.jvm.t3.load.C");
        new C();
        System.in.read();
    }
}

class C {
    D d = new D();
}

class D {

}