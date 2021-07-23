package cn.itcast.jvm.t3.load;

public class Load5_1 {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("cn.itcast.jvm.t3.load.F");
        System.out.println(aClass.getClassLoader()); // AppClassLoader  ExtClassLoader
    }
}

