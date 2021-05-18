# Jetpack学习之全家桶



## 第一个之Lifecycle

### 通过Lifecycle解耦页面与组件

实现 implements LifecycleObserver 接口

在需要的地方使用此注解就可以使用

```
@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
```

```java
getLifecycle().addObserver(chronometer);
```



### LifecycleService解耦Service



首先创建一个服务MyLocationService extends LifecycleService 继承

同样实现 MyLocationObserver implements LifecycleObserver

这是服务里面添加监听器的代码

```java
MyLocationObserver observer = new MyLocationObserver(this);
getLifecycle().addObserver(observer);
```

同样在观察者里面使用@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)

来监听生命周期





### ProcessLifecycleOwner 监听应用程序的生命周期



一个类继承于Application 然后去为它添加  生命周期的监听

这一个监听是对整个app的监听  与Activity的数量无关

oncreat只会调用一次

而ondestory永远不会被调用

```
MyApplication extends Application
```

```
ProcessLifecycleOwner.get().getLifecycle()
        .addObserver(new ApplicationObserver());
```



## ViewModel

### 解决问题

1. 瞬态数据丢失
2. 异步调用的内存泄漏
3. 类膨胀提高维护难度和测试难度
4. 使数据和视图的分离 而且还能保持通







### 作用

ViewModel 能够实现瞬态数据保存的原因就是

因为它的生命周期要比组件的生命周期长  独立于配置的变化

### 注意

不要向ViewModel 里面传入context对象  因为会引起内存泄漏

如果实在需要这个context对象那么就去继承AndroidViewModel

在构造方法里面就会有context对象

```
public MyViewModel(@NonNull Application application) {
    super(application);
}

```

### 使用

```java
viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MyViewModel.class);
```



## LiveData

### 介绍

MutableLiveData  是LIveData的子类  LiveData是一个抽象类

所以一般使用的是MutableLiveData





### 作用

**在ViewModel数据发生变化的时候就去通知页面**

### 优势

1. 确保界面符合数据状态
2. 不会发生内存泄漏
3. 不会因为Activity停止而导致崩溃
4. 不需要手动处理生命周期
5. 数据始终保持最新状态
6. 适当的配置更改
7. 共享资源

### 使用

getCurrentSecond()方法在这里只是返回了一个MutableLiveData数据类型的 对象 添加观察者  在数据发生变化的时候调用此方法

```
viewModel.getCurrentSecond().observe(this, new Observer<Integer>() {
    @Override
    public void onChanged(Integer i) {
        textView.setText(String.valueOf(i));
    }
});
```



## Room



