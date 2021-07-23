package cn.itcast.jvm.t3.candy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Candy9 {
    public static void main(String[] args) {
        try(InputStream is = new FileInputStream("d:\\1.txt")) {
            System.out.println(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
