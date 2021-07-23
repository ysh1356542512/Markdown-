package cn.itcast.jvm.t3.load;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * 演示静态变量占用的内存
 */
public class Load1 {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
//        gen();
        test();
    }

    private static void gen() throws FileNotFoundException {
        for (int j = 0; j < 128; j++) {
            PrintWriter w = new PrintWriter("E:\\git\\jvm\\src\\cn\\itcast\\jvm\\t1\\gen\\Demo1_12_"+j+".java");
            w.println("package cn.itcast.jvm.t1.gen;");
            w.println("public class Demo1_12_"+j+" {");
            w.println("    static {System.out.println(Demo1_12_"+j+".class.getSimpleName());}");
            for (int i = 0; i < 1024*2; i++) {
                w.println("    public static long i" + i + "=1;");
                w.flush();
            }
            w.println("    public static void main(String[] args) throws InterruptedException {\n" +
                    "        Thread.sleep(10000000L);\n" +
                    "    }");
            w.println("}");
            w.close();
        }
    }

    public static void test() throws InterruptedException {
        int sum = 0;
        Thread.sleep(20000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_0.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_1.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_2.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_3.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_4.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_5.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_6.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_7.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_8.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_9.i0;
        System.out.println("---------1");
//        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_10.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_11.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_12.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_13.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_14.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_15.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_16.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_17.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_18.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_19.i0;
        System.out.println("---------2");
//        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_20.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_21.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_22.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_23.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_24.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_25.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_26.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_27.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_28.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_29.i0;
        System.out.println("---------3");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_30.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_31.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_32.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_33.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_34.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_35.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_36.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_37.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_38.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_39.i0;
        System.out.println("---------4");
//        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_40.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_41.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_42.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_43.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_44.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_45.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_46.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_47.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_48.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_49.i0;
        System.out.println("---------5");
//        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_50.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_51.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_52.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_53.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_54.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_55.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_56.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_57.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_58.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_59.i0;
        System.out.println("---------6");
//        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_60.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_61.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_62.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_63.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_64.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_65.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_66.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_67.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_68.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_69.i0;
        System.out.println("---------7");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_70.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_71.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_72.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_73.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_74.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_75.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_76.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_77.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_78.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_79.i0;
        System.out.println("---------8");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_80.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_81.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_82.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_83.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_84.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_85.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_86.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_87.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_88.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_89.i0;
        System.out.println("---------9");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_90.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_91.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_92.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_93.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_94.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_95.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_96.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_97.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_98.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_99.i0;
        System.out.println("---------10");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_100.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_101.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_102.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_103.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_104.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_105.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_106.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_107.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_108.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_109.i0;
        System.out.println("---------11");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_110.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_111.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_112.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_113.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_114.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_115.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_116.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_117.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_118.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_119.i0;
        System.out.println("---------12");
        Thread.sleep(5000);
        sum += cn.itcast.jvm.t1.gen.Demo1_12_120.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_121.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_122.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_123.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_124.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_125.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_126.i0;
        sum += cn.itcast.jvm.t1.gen.Demo1_12_127.i0;
        System.out.println("---------13");
        Thread.sleep(10000000L);
    }

}
