package cn.itcast.jvm.t4;

import java.lang.reflect.Method;
import java.util.List;

public class MyServiceImpl implements MyService {

    private Invoker invoker;

    public MyServiceImpl(Invoker invoker) {
        this.invoker = invoker;
    }

    static Method save;
    static Method findAll;

    static {
        try {
            save = MyService.class.getMethod("save", User.class);
            findAll = MyService.class.getMethod("findAll");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(User user) {
        invoker.invoke(save, new Object[]{user});
    }

    @Override
    public List<User> findAll() {
        return (List<User>) invoker.invoke(findAll, new Object[]{});
    }
}
