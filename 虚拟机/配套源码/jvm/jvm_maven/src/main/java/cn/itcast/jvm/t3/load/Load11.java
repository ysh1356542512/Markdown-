package cn.itcast.jvm.t3.load;

public class Load11 {
    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        Class<?> aClass = Class.forName("com.mysql.jdbc.Driver");
        System.out.println(aClass.getClassLoader());
    }
}
