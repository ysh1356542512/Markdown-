package cn.itcast.jvm.t3.load;

/**
 * 演示 扩展类加载器
 * 在 C:\Program Files\Java\jdk1.8.0_91 下有一个 my.jar
 * 里面也有一个 G 的类，观察到底是哪个类被加载了
 */
public class Load5_2 {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("cn.itcast.jvm.t3.load.G");
        System.out.println(aClass.getClassLoader());
    }
}

