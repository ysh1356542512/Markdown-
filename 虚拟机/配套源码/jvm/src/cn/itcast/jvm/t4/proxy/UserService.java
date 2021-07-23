package cn.itcast.jvm.t4.proxy;

import cn.itcast.jvm.t4.User;

public class UserService {

    public void save(User user){
        System.out.println("save...");
    }

    public void update(User user){
        System.out.println("update...");
    }

}
