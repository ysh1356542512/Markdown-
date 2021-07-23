package cn.itcast.jvm.t3.load;

public class Load10 {
    //  -XX:+TraceClassLoading
    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        Class<?> aClass = Class.forName("cn.itcast.jvm.t3.load.G", true, systemClassLoader);
        System.out.println(aClass.getClassLoader());
    }
}
