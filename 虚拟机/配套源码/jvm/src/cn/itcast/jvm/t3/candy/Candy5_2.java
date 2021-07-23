package cn.itcast.jvm.t3.candy;

import java.util.Arrays;
import java.util.List;

public class Candy5_2 {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        for (Integer i : list) {
            System.out.println(i);
        }
    }
}
