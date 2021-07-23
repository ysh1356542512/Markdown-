package cn.itcast.jvm.t3.bytecode;

/**
 * 从字节码角度分析　a++  相关题目
 */
public class Demo3_2 {
    public static void main(String[] args) {
        int a = 10;
        int b = a++ + ++a + a--;
        System.out.println(a);
        System.out.println(b);
    }
}
