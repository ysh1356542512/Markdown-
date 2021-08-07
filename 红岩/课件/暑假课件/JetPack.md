[toc]

# 前言

这里是在写完课件之后写的 由于我负责讲 **Lifecycle** **LiveData** 以及 **DataBinding** 这几个组件偏基础的组件  前两个都是 基础类 的组件 用法较少但是它们在其他组件中都有涉及 比如 **Lifecycle** 在 **DataBinding** 的双向绑定中给 binder 传入 **LifecycleOwner** 来给 **LiveData** 再添加一个 **Observer** ，从而实现 **数据改变通知UI改变** 

蔷神负责 **ViewModel** **Navigation** 和 **Room** 这几个偏使用的组件  用法很多 但源码的意义不大 所以他注重讲组件的使用

对于  **Lifecycle** 和 **LiveData** 我觉得了解它们的内部结构也是有好处的 特别是 **Lifecycle** 的 **addObserver** 方法和它通过 **注解添加观察者** 的方式 源码写的都特别好 课件里我自己的废话比较多 我尽量把源码讲明白

# Lifecycle

## Lifecycle之基本用法

### LifecycleEventObserver/LifecycleObserver

#### 两者的区别

前者继承后者 实现 **onStateChanged** 方法

```kotlin
public interface LifecycleEventObserver extends LifecycleObserver {
    /**
     * Called when a state transition event happens.
     *
     * @param source The source of the event
     * @param event The event
     */
    void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event);
}
```

#### 方式一 

通过实现LifecycleEventObserver接口中的 **onStateChanged** 方法实现对事件的监听

```kotlin
class TestObserver1:LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE->{
                println("onCreate...") }
            Lifecycle.Event.ON_START -> {
                println("onStart...")}
            Lifecycle.Event.ON_RESUME->{
                println("onResume...")}
            Lifecycle.Event.ON_PAUSE->{
                println("onPause...")}
            Lifecycle.Event.ON_STOP->{
                println("onStop...")}
            Lifecycle.Event.ON_DESTROY->{
                println("onDestroy...")}
            else->{}
        }
    }
}
```

```kotlin
lifecycle.addObserver(TestObserver1())
```

![image-20210720170654224](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210720170654224.png)

#### 方法二

继承 **LifecycleObserver** 通过注解来绑定 继承 **LifecycleEventObserver** 好像不得行

```kotlin
class TestObserver2:LifecycleEventObserver{
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        TODO("Not yet implemented")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        println("onCreate...")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start(){
        println("onStart...")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        println("onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        println("onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        println("onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        println("onDestroy")
    }

}
```

![](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210720172959941.png)

**获得当前状态**

在源码里 **Lifecycle** 会有一个 getLifecycle 方法 也就是得到当前状态 所以只要有 **Lifecycle** 的地方就可以得到当前状态

```kotlin
class TestObserver1(private val lifecycle: Lifecycle):LifecycleEventObserver {
	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    	println("currentState -- > ${lifecycle.currentState}")
    	when (event) {
       	...
       	...
    	}
	}
}
```

![image-20210728201048629](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210728201048629.png)

**注**

虽然 **Lifecycle** 的用法很简单也很少 但是它是 **JetPack** 众多组件的基础 越到后面你就会越感觉 **Lifecycle** 的重要性 所以我觉得了解 **Lifecycle** 的原理特别重要 

## Lifecycle之源码分析



在这之前先放张图 我一开始看原码的时候就是没看这张图 感觉很重要 [^md安卓官网的那张图复制不了 我就从博客上扒了^]

![193bfd2787804cf056441c509f107f0e](C:\Users\asus\Desktop\install\193bfd2787804cf056441c509f107f0e.png)

### 基本实现原理

首选我们得知道的是 在 **MainActivity** 里的 **lifecycle** 是个啥 在哪里获取 点进去看下

在 MainActivity 的父类中的 **getLifecycle** 方法获得

<img src="https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210802132759982.png" alt="image-20210802132759982" style="zoom:80%;" />

发现是继承 **LifecycleOwner** 接口 而且在 **fragment** 中也同样继承

![image-20210802132851290](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210802132851290.png)

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210720100731023.png" alt="image-20210720100731023" style="zoom:80%;" />

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210720101112698.png" alt="image-20210720101112698" style="zoom:100%;" />

#### LifecycleOwner

包括 **fragment** 也都实现了 **LifecycleOwner** 接口

```java
public interface LifecycleOwner {
    /**
     * Returns the Lifecycle of the provider.
     *
     * @return The lifecycle of the provider.
     */
    @NonNull
    Lifecycle getLifecycle();
}
```

#### Lifecycle

而 **getLifeCycle** 返回的值为 **Lifecycle** 对象 点进去康康

```java
public abstract class Lifecycle
```

是个抽象类 说明肯定要被继承 说明 **getLifecycle** 返回的是 **Lifecycle** 的子类 由子类来具体实现 **Lifecycle** 的抽象方法

```java
//添加observer抽象方法
public abstract void addObserver(@NonNull LifecycleObserver observer);
//取消observer抽象方法
public abstract void removeObserver(@NonNull LifecycleObserver observer);
//获得当前状态抽象方法
public abstract State getCurrentState();
//事件枚举类
public enum Event {  
        ON_CREATE,   
        ON_START,    
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY}
//状态枚举类
public enum State {      
        DESTROYED,     
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;
//拿当前的currentState和设置的state进行比较
//用来处理 当状态至少为state的时候才去执行某些操作 直接可以在Activity里用
public boolean isAtLeast(@NonNull State state) {
            return compareTo(state) >= 0;}
//具体用法：lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
}
```

所以我们就找到了 **LifecycleRegistry**  而它其实是在 **ComponentActivity** 类中实例化 也就是说我们在 MainActivity 里得到的 Lifecycle 就是这个 **LifecycleRegistry** 的实例

<img src="https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210802133404129.png" alt="image-20210802133404129" style="zoom: 80%;" />



#### LifecycleRegistry

我们对于子类最关心的应该是它如何 **具体** 实现父类当中的 **抽象方法** 而子类中的其他方法基本上都是为了实现父类方法的而写的

但是我们先看成员变量 

##### 成员变量

```java
//用来存储LifecycleObserver和它的相关绑定的静态内部类的集合map
private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap =
        new FastSafeIterableMap<>();
//当前的state
private State mState;
//弱引用存储LifecycleOwner
private final WeakReference<LifecycleOwner> mLifecycleOwner;

//这三个变量很重要 是LifecycleRegistry实现状态有序化的关键 也是我看了最久的东西 重点！！！
private int mAddingObserverCounter = 0;//记录新增Observer的数量
private boolean mHandlingEvent = false;//记录是否有状态改变的 Event
private boolean mNewEventOccurred = false;//记录是否有上面两个事件的发生 如果有即为true 决定着同步操作是否会被打断 优先去执行前面两个操作 等会再讲

//一个栈 用来帮当前Observer的状态入栈出栈
private ArrayList<State> mParentStates = new ArrayList<>();
//好像是线程切换吧 没咋看
private final boolean mEnforceMainThread;
```



##### addObserver

先大致阅读一下 **addObserver** 方法

这整个类里最最最重要的就是 **addObserver** 这个方法 先理一遍它的代码 我把它分为四个板块 第一个板块为初始化 第二个板块为安全判空 第三个板块为单个同步 第四个板块为统一同步

```java
@Override
public void addObserver(@NonNull LifecycleObserver observer) {
    enforceMainThreadIfNeeded("addObserver");
    //第一板块
    State initialState = mState == DESTROYED ? DESTROYED : INITIALIZED;
    ObserverWithState statefulObserver = new ObserverWithState(observer, initialState);
    ObserverWithState previous = mObserverMap.putIfAbsent(observer, statefulObserver);
//--------------------------------------------------------------------------------
    //第二板块
    if (previous != null) {
        return;
    }
    LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
    if (lifecycleOwner == null) {
        // it is null we should be destroyed. Fallback quickly
        return;
    }
//--------------------------------------------------------------------------------
    //第三板块
    boolean isReentrance = mAddingObserverCounter != 0 || mHandlingEvent;
    State targetState = calculateTargetState(observer);
    mAddingObserverCounter++;
    while ((statefulObserver.mState.compareTo(targetState) < 0
            && mObserverMap.contains(observer))) {
        pushParentState(statefulObserver.mState);
        final Event event = Event.upFrom(statefulObserver.mState);
        if (event == null) {
            throw new IllegalStateException("no event up from " + statefulObserver.mState);
        }
        statefulObserver.dispatchEvent(lifecycleOwner, event);
        popParentState();
        // mState / subling may have been changed recalculate
        targetState = calculateTargetState(observer);
    }
	//第四板块
    if (!isReentrance) {
        // we do sync only on the top level.
        sync();
    }
    mAddingObserverCounter--;
}
```

为了能让大家和我现在的想法同步 我来问几个问题

**一** 在addObserver这个方法中  Observer 都经历了什么

**二** 当虚拟机状态变化时 Observer 如何被通知 并实现其 **onStateChanged** 方法

我一开始对 **addObserver** 的猜想是 把Observer和虚拟机当前状态绑定 然后拿这个绑定的状态和最新的状态进行比较 然后就会触发事件 但很遗憾不是这样的

现在有这样一个场景 我们在 Activity 的 **onPause** 方法上 **addObserver** 会发生什么？虽然这是个很智障的做法 但是当初我就想了这个问题想了很久 我一开始会以为什么都不会发生 直到虚拟机有新的事件发生 但是实际上不是的 它会把 onCreate 到 onPause 之间所有定义过的方法全都调用一遍 这里可以演示一下。 其实在 **Observer** 被添加后 它会经历一个 **逐步** 同步的过程。

什么是 **逐步** 同步？

这里先只给初步的概念 也就是我一开始用来给 **Observer** 绑定的 **State** 是 **DESTROYED 或 INITIALIZED** 如果此时 虚拟机状态为 **STARTED** 那么与 **Observer** 绑定的 **state** 会先按照那个图 一步一步到 **STARTED**



为了方便理解 假设我们自己就是一个Observer  然后网校就是一个整个LifecycleRegistry 移动部就是一个集合mObservers 但是如果我们想要真正加入移动部 我们分成了四个步骤 **第一** 告诉你我们要学习什么比如java安卓等等知识(**初始化你的State**) 这个步骤我把它叫做初始化 或者包装化 让本来只是一个大学生的你 被包装成了一个具有一点java知识的大学生  **第二** 要判断你是不是真正想学 否则可能会带坏别人(**判断你是否已经在mObservers里或LifecycleOwner是否为空**) 这个步骤叫做判断安全  **第三** 经过很多次的培训和课程 直到达到移动部对你个人的要求 也就是经过一个长时间的考核(**也就是达到了虚拟机的当前State**) 这样之后你就可以统一进入移动部的大家庭 也就是同步完成 **第四** 当大家都完成了考核 也就是全都达到了虚拟机的State 这个时候我叫做共同进步时期 如果第三阶段是各自进行学习和考核 那这个阶段就是共同进步 相当于开始迭代网校的项目了 当网校发布了新的项目 大家就会一起完成它。 而对于我们个人(**Observer**)而言 内部有一个 **开心** 的方法(**onStateChanged**) 当我们的 **State** 也就是水平提高时 我们肯定开心嘛 但是具体怎么个开心法 由我们具体的个人决定  

其实上面这段话就把 **addObserver** 方法基本流程大致讲完了 现在我们回到代码块

###### 第一板块

```java
    State initialState = mState == DESTROYED ? DESTROYED : INITIALIZED;
    ObserverWithState statefulObserver = new ObserverWithState(observer, initialState);
    ObserverWithState previous = mObserverMap.putIfAbsent(observer, statefulObserver)
```
1. **initialState** 就是初始化 **ObserverWithState** 的状态（传授我们基本的java知识）

2. **statefulObserver** 新建一个 **ObserverWithState** 类 (我们成为具有基本java知识的大学生)

   我们先看一下这个类 一个内部静态类

   ```java
   static class ObserverWithState {
       //两个成员变量 把两者绑定
       State mState;
       LifecycleEventObserver mLifecycleObserver;
   
       //构造方法
       ObserverWithState(LifecycleObserver observer, State initialState) {
           mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
           mState = initialState;
       }
   	
       //重点 分发事件方法 把event传入获得这个事件发生后的状态 而 onStateChanged是个接口方法 需要用户自己实现
       void dispatchEvent(LifecycleOwner owner, Event event) {
           State newState = event.getTargetState();
           mState = min(mState, newState);//两句奇怪的语句
           mLifecycleObserver.onStateChanged(owner, event);
           mState = newState;//另一句奇怪的语句
       }
   }
   ```

   ```java
   public interface LifecycleEventObserver extends LifecycleObserver {
       /**
        * Called when a state transition event happens.
        *
        * @param source The source of the event
        * @param event The event
        */
       void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event);
   }
   ```

   ss

   ss

3. **previous** 先把新建的 **statefulObserver** 变量放到集合map里 再用 previous 表示最新加入的Observer

###### 第二板块

```java
if (previous != null) {
        return;
    }
    LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
if (lifecycleOwner == null) {
    // it is null we should be destroyed. Fallback quickly
    return;
}
```

判空 安全！ 嗯！

###### 第三板块

一开始得讲下大小关系：

`DESTROYED` < `INITIALIZED` < `CREATED` < `STARTED` < `RESUMED` 

我先用注释标下讲的顺序 接下来一系列的操作就是 **进行同步** [^因为本来分成三个板块的 但是后来想想还是把 sync 分出来 但是课件来不及改了^]

```java
boolean isReentrance = mAddingObserverCounter != 0 || mHandlingEvent;//6
State targetState = calculateTargetState(observer);//1
mAddingObserverCounter++;//6
while ((statefulObserver.mState.compareTo(targetState) < 0   //2
        && mObserverMap.contains(observer))) {
    pushParentState(statefulObserver.mState);//3
    final Event event = Event.upFrom(statefulObserver.mState);//4
    if (event == null) {   //4
        throw new IllegalStateException("no event up from " +statefulObserver.mState);
    }
    statefulObserver.dispatchEvent(lifecycleOwner, event);//4
    popParentState();//3
    // mState / subling may have been changed recalculate
    targetState = calculateTargetState(observer);//1
}

if (!isReentrance) {                //6
    // we do sync only on the top level.
    sync();				//5
}
mAddingObserverCounter--;//6
```

1. 用了 **calculateTargetState** 方法得到了一个 state 先看下这个方法

   ```java
   private State calculateTargetState(LifecycleObserver observer) {
       Map.Entry<LifecycleObserver, ObserverWithState> previous = mObserverMap.ceil(observer);
   
       State siblingState = previous != null ? previous.getValue().mState : null;
       State parentState = !mParentStates.isEmpty() ? mParentStates.get(mParentStates.size() - 1)
               : null;
       return min(min(mState, siblingState), parentState);
   }
   ```

   有三个变量 **sliblingState** 表示当前新增的Observer的前一个Observer的状态 ； **mState** 表示当前虚拟机的状态； **parenetState** 表示栈顶上的状态 因为此时新增Observer还未入栈 所以为null 然后三个进行min方法比较 选出最小的作为返回值 为的就是保持状态有序化 人话就是后增的Observer的状态一定比前一个Observer要小 比如说两个Observer1和Observer2先后 add进来 然后一定会是 前者的 onCreate里的方法先被调用 后者的再被调用 原理跟6一起讲 总之这段代码就是为了给新增的Observer一步一步地到达它能达到的最大的状态 相当于这样一个场景 就是大家一起排队跑步 有人跑的慢有人跑的快 比如说第一个人跑了400米 第二个人跑了300米 第三个人跑了100米 然后这个时候有个新来的同学要加入他们  但是它肯定不好插在第二个人和第三个之间 先来后到 所以他最最最多就只能插在跑了100米这个位置上 跟着大家后面 后面就同理 但是大家最终的状态都是跑到终点 也就是虚拟机当前的状态 这样就是有序化

   这是我自己画的图。。 比较丑见谅 看不清课上看原图

   <img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210721230123335.png" alt="image-20210721230123335" style="zoom:80%;" />

2. 这段while条件语句就是如果集合中含有这个Observer以及这个Observer的 **state** 小于我们提供它的最大的 **state** 就能执行下面的代码 值得一提的是这个 **targetState** 是会变的 为什么呢？ 因为在运行的时候 其他的Obsever也可能在同步 就像跑步的时候大家都在跑 所以后一位的最终状态也会改变 可能是200米也可能是300米

3. 这个就是把 **statefulObserver.mState** 入栈 它就会变成栈最顶上的 **state** 当它处理完 **dispatchEvent** 这些时间后 它就会自己出栈

   ```java
   private void popParentState() {
       mParentStates.remove(mParentStates.size() - 1);
   }
   
   private void pushParentState(State state) {
       mParentStates.add(state);
   }
   ```

4. ```java
   @Nullable
   public static Event upFrom(@NonNull State state) {
       switch (state) {
           case INITIALIZED:
               return ON_CREATE;
           case CREATED:
               return ON_START;
           case STARTED:
               return ON_RESUME;
           default:
               return null;
       }
   }
   ```

5. 这个模块就是真正的全部同步 上面的版块就是把新增的Observer给同步到它能同步到的最大的 `State` 而当所有的新增`Observer`都与虚拟机的`State`相同时 这个`isReentrance`就会为`false` 就会执行`sync`方法 先得到 `lifecycleOwner` 然后判空 再while循环 如果 isSynced 如果没有全部同步 则会一直执行下面的代码 先看下 `isSynced` 的代码

   ```java
   private void sync() {
       LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
       if (lifecycleOwner == null) {
           throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is already"
                   + "garbage collected. It is too late to change lifecycle state.");
       }
       while (!isSynced()) {
           mNewEventOccurred = false;
           // no need to check eldest for nullability, because isSynced does it for us.
           if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
               backwardPass(lifecycleOwner);
           }
           Map.Entry<LifecycleObserver, ObserverWithState> newest = mObserverMap.newest();
           if (!mNewEventOccurred && newest != null
                   && mState.compareTo(newest.getValue().mState) > 0) {
               forwardPass(lifecycleOwner);
           }
       }
       mNewEventOccurred = false;
   }
   ```

   返回分为两种情况 一种为map里的ObserverWithState数量为0 那么就是没有Observer加入 那也没有可同步的 则返回true

   另一种情况 用两个`State`分别存取map中最先放入的`OberverWithState`的状态和最后放入的 因为我们遵循了有序化的规则 后面的状态一定小于等于前面的状态 所以只有当所有的Observer全部同步后 最前面的和最后面的状态才会相同 所以会返回true

   ```java
   private boolean isSynced() {
       if (mObserverMap.size() == 0) {
           return true;
       }
       State eldestObserverState = mObserverMap.eldest().getValue().mState;
       State newestObserverState = mObserverMap.newest().getValue().mState;
       return eldestObserverState == newestObserverState && mState == newestObserverState;
   }
   ```

   这两个方法大致就是先判断是往回走还是往里走 如果是oncreate这类 那就是 forwardPass 如果是onStop这类 就是backwardPass 具体实现其实和之前模块的while语句类似 就不讲了吧 [^肯定不是因为懒^]

   ```java
   private void forwardPass(LifecycleOwner lifecycleOwner) {
       Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> ascendingIterator =
               mObserverMap.iteratorWithAdditions();
       while (ascendingIterator.hasNext() && !mNewEventOccurred) {
           Map.Entry<LifecycleObserver, ObserverWithState> entry = ascendingIterator.next();
           ObserverWithState observer = entry.getValue();
           while ((observer.mState.compareTo(mState) < 0 && !mNewEventOccurred
                   && mObserverMap.contains(entry.getKey()))) {
               pushParentState(observer.mState);
               final Event event = Event.upFrom(observer.mState);
               if (event == null) {
                   throw new IllegalStateException("no event up from " + observer.mState);
               }
               observer.dispatchEvent(lifecycleOwner, event);
               popParentState();
           }
       }
   }
   
   private void backwardPass(LifecycleOwner lifecycleOwner) {
       Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> descendingIterator =
               mObserverMap.descendingIterator();
       while (descendingIterator.hasNext() && !mNewEventOccurred) {
           Map.Entry<LifecycleObserver, ObserverWithState> entry = descendingIterator.next();
           ObserverWithState observer = entry.getValue();
           while ((observer.mState.compareTo(mState) > 0 && !mNewEventOccurred
                   && mObserverMap.contains(entry.getKey()))) {
               Event event = Event.downFrom(observer.mState);
               if (event == null) {
                   throw new IllegalStateException("no event down from " + observer.mState);
               }
               pushParentState(event.getTargetState());
               observer.dispatchEvent(lifecycleOwner, event);
               popParentState();
           }
       }
   }
   ```

6. 从一开始就强调的三个很重要的成员变量 首先我们会触发`sync`这个事件的条件只有两个 一是新增Observer 二是改变状态 而`mAddingObserverCounter`是记录新增Observer且未同步完成的数量 `mHandlingEvent`是记录改变状态事件是否完成的变量 `mNewEventOccurred`是只要有这两个其中一个 就为true 目的是为了暂停其余的同步事件

   #### mAddingObserverCounter

   ```java
   private int mAddingObserverCounter = 0;
   ```

   ```java
   boolean isReentrance = mAddingObserverCounter != 0 || mHandlingEvent;
   State targetState = calculateTargetState(observer);
   mAddingObserverCounter++;
   while ((statefulObserver.mState.compareTo(targetState) < 0
           && mObserverMap.contains(observer))) {
       pushParentState(statefulObserver.mState);
       final Event event = Event.upFrom(statefulObserver.mState);
       if (event == null) {
           throw new IllegalStateException("no event up from " + statefulObserver.mState);
       }
       statefulObserver.dispatchEvent(lifecycleOwner, event);
       popParentState();
       // mState / subling may have been changed recalculate
       targetState = calculateTargetState(observer);
   }
   
   if (!isReentrance) {
       // we do sync only on the top level.
       sync();
   }
   mAddingObserverCounter--;
   ```

   一开始初始化为0 当触发一次 `addObserver` 方法后就++ 但是值得注意的是 这个变量的--是在 !isReentrance 这个if条件句以后实现的 也就是说当所有Observer全部同步后 它才会-- 

   #### mHandlingEvent

   ```java
   public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
       enforceMainThreadIfNeeded("handleLifecycleEvent");
       moveToState(event.getTargetState());
   }
   
   private void moveToState(State next) {
       if (mState == next) {
           return;
       }
       mState = next;
       if (mHandlingEvent || mAddingObserverCounter != 0) {
           mNewEventOccurred = true;
           // we will figure out what to do on upper level.
           return;
       }
       mHandlingEvent = true;
       sync();
       mHandlingEvent = false;
   }
   ```

   当要实行`handleLifecycleEvent`方法就会调用`moveToState`方法 然后会进行一个if判断句 这个等会讲 然后就会告诉Registry有一个Handling事件来了 `mHandlingEvent`就为true了 然后同步完全后 再变为false

   ###### mNewEventOccurred

   重头戏终于来了 这个折磨了我好久的机制 我好不容易才想明白的东西 就是靠它来实现有序化 当有新增Observer或状态改变事件 Registry会放下所有的其他同步工作 来优先处理 **状态改变事件** 和 **同步新增Observer事件**[^也就是把它同步到他最大的那个State 直到所有新增的Observer都同步完全^] 处理完这些后 再进行其他同步工作

   首先先是`moveToState`方法

   ```java
   private void moveToState(State next) {
       if (mState == next) {
           return;
       }
       mState = next;
       if (mHandlingEvent || mAddingObserverCounter != 0) {
           mNewEventOccurred = true;
           // we will figure out what to do on upper level.
           return;
       }
       mHandlingEvent = true;
       sync();
       mHandlingEvent = false;
   }
   ```

   如果 mHandlingEvent 为 true 那就是刚接收到信息 刚执行完`mHandlingEvent = true;`这段代码但是 `sync` 还没有同步好 或者 mAddingObserverCounter != 0  需要先将新增的Observer全部同步完毕 再去执行改变状态事件 这两种情况都会 `return;` 不允许执行 

   ```java
   boolean isReentrance = mAddingObserverCounter != 0 || mHandlingEvent;
   State targetState = calculateTargetState(observer);
   mAddingObserverCounter++;
   while ((statefulObserver.mState.compareTo(targetState) < 0
           && mObserverMap.contains(observer))) {
       pushParentState(statefulObserver.mState);
       final Event event = Event.upFrom(statefulObserver.mState);
       if (event == null) {
           throw new IllegalStateException("no event up from " + statefulObserver.mState);
       }
       statefulObserver.dispatchEvent(lifecycleOwner, event);
       popParentState();
       // mState / subling may have been changed recalculate
       targetState = calculateTargetState(observer);
   }
   
   if (!isReentrance) {
       // we do sync only on the top level.
       sync();
   }
   mAddingObserverCounter--;
   ```

   `isReentrance`作用跟刚刚那个判断句一样 如果有新增Oberver未同步完全或者有改变状态事件 则该变量为true 这样的话`!isReetrance`就一直是false 就永远也不会执行 `sync`方法 只有全部同步完全后 才会执行 但还有一个 `mNewEventOccurred = true` 语句 有什么用呢？ 看下一个方法

   ```java
   private void forwardPass(LifecycleOwner lifecycleOwner) {
       Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> ascendingIterator =
               mObserverMap.iteratorWithAdditions();
       while (ascendingIterator.hasNext() && !mNewEventOccurred) {
           Map.Entry<LifecycleObserver, ObserverWithState> entry = ascendingIterator.next();
           ObserverWithState observer = entry.getValue();
           while ((observer.mState.compareTo(mState) < 0 && !mNewEventOccurred
                   && mObserverMap.contains(entry.getKey()))) {
               pushParentState(observer.mState);
               final Event event = Event.upFrom(observer.mState);
               if (event == null) {
                   throw new IllegalStateException("no event up from " + observer.mState);
               }
               observer.dispatchEvent(lifecycleOwner, event);
               popParentState();
           }
       }
   }
   
   private void backwardPass(LifecycleOwner lifecycleOwner) {
       Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> descendingIterator =
               mObserverMap.descendingIterator();
       while (descendingIterator.hasNext() && !mNewEventOccurred) {
           Map.Entry<LifecycleObserver, ObserverWithState> entry = descendingIterator.next();
           ObserverWithState observer = entry.getValue();
           while ((observer.mState.compareTo(mState) > 0 && !mNewEventOccurred
                   && mObserverMap.contains(entry.getKey()))) {
               Event event = Event.downFrom(observer.mState);
               if (event == null) {
                   throw new IllegalStateException("no event down from " + observer.mState);
               }
               pushParentState(event.getTargetState());
               observer.dispatchEvent(lifecycleOwner, event);
               popParentState();
           }
       }
   }
   ```

   这两个方法都是用来更新 `state` 的 但它们的条件语句里都有一个共同的开关 `!mNewEventOccurred` 只要该变量为true 这条语句就不会执行 就是为了保证有序化规则 但在实际运行时 该变量为true的时间很短很短 我们得迅速把它变回false 好让我们的进程快速有效的进行 所以谷歌开发人员真是个天才 在哪呢 就在下面这个 `sync`方法里面

   ```java
   private void sync() {
       LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
       if (lifecycleOwner == null) {
           throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is already"
                   + "garbage collected. It is too late to change lifecycle state.");
       }
       while (!isSynced()) {
           mNewEventOccurred = false;
           // no need to check eldest for nullability, because isSynced does it for us.
           if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
               backwardPass(lifecycleOwner);
           }
           Map.Entry<LifecycleObserver, ObserverWithState> newest = mObserverMap.newest();
           if (!mNewEventOccurred && newest != null
                   && mState.compareTo(newest.getValue().mState) > 0) {
               forwardPass(lifecycleOwner);
           }
       }
       mNewEventOccurred = false;
   }
   ```

   它在循环语句第一句和循环结束后都加了一句 `mNewEventOccurred = false;` 秒乱了

### 注解的方式获得LifecycleEventObserver

讲课的顺序 ： 1.系统在哪里获取注解 2.获取注解的源头（利用debug）3.理清 **ObserverWithState** 中获取 lifecycleEventObserver的逻辑 4.分析 **Lifecycling** 类如何返回一个继承自 **LifecycleEventObserver** 的实体类 5.返回的实体类 如何覆写 onStateChanged方法， 即如何调用 我们在 **addObserver** 中传入的 继承 LifecycleObserver 的实体类中的有注解的方法

因为写的可能有点乱 所以晚上上课会按照以上步骤来讲解



#### 基本流程

PS：这段特别绕 课件里面不好讲 它类之间方法之间跳转太频繁了 具体的演示和流程我在课上会详细讲的

其实在使用`Lifecycle`的时候就很奇怪 这个通过注解的方式 是如何监听的 它没有继承**LifecycleEventObserver** 而是继承没有 **onStateChanged** 方法的 **LifecycleObserver** 而这些Observer里的方法肯定是在 **ObserverWithState** 这个内部静态类里调用的

在 **ObserverWithState** 里有这样一段代码

一切的一切都源于这段代码 因为是这个 **Lifecycling** 类的 **lifecycleEventObserver** 方法将我们传入的 **observer** 不管是 **通过注解还是不注解** 转化为了包装类的 **mLifecycleObserver** 总之都是 **LifecycleEventObserver** 类型

```java
mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
```

可以看到 `mLifecycleObserver` 是通过一个叫 **Lifecycling** 的类实现的 没见过 先不管他 只知道我们的 add进来的 Observer 都是通过这个类 来转化成 **LifecycleEventObserver**

**所以我们得知道究竟Lifecycling怎么把一个继承LifecycleObserver的类变成一个继承LifecycleEventObserver的类 把爸爸变成儿子**

先来看下这个注解

```java
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnLifecycleEvent {
    Lifecycle.Event value();
}
```

可以看到这是一个注解接口 还有上面就是注解注解的注解 第二行 **RetentionPolicy.RUNTIME** 说明它作用的时机是在虚拟机运行的时候 第三行它的作用域在Method 对方法注解 还有就是它里面的 value 属性是 Event

先看看这个注解在哪里会被获取

![](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722103623210.png)

可以看到除了第一第二行 其他地方都是使用或者导包还有就是注解里出现 所以终点在这个`ClassesInfoCache`里 点进去康康

```java
OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
```

可以看到在这里得到了这个 **annotation**注解 而且在这个类的开头的注释里 也真相了 

```java
/**
 * Reflection is expensive, so we cache information about methods
 * for {@link ReflectiveGenericLifecycleObserver}, so it can call them,
 * and for {@link Lifecycling} to determine which observer adapter to use.
 */
```

**Reflection** ！老朋友了 让我想起上学期被反射折磨的时候 这段的大概意思就是 反射是花销很大的操作 所以我们把关于方法的相关信息缓存到这个类中 为 **RelfectiveGenericLifecycleObserver** [^这个类其实就是我们注解得到的LifecycleEventObserver 他是一个继承LifecycleEventObserver接口的一个实体类^] 然后还有为 **Lifecycling** 去选择哪一种Observer的适配器去使用 什么意思？ 先讲下流程 我们先debug下

![image-20210722110114586](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722110114586.png)

![image-20210802180507725](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210802180507725.png)

发现入口是一个叫 Lifecycling的一个类 它返回了一个 LifecycleEventObserver类 那现在来理下思路 也就是说 我们将一个充满了注解方法的Observer类传入 这个类继承自 LifecycleObserver 但是静态内部类里需要的是 LifecycleEventObserver 所以我们通过 Lifecycling这个类的这个方法 将这个转化为了我们需要的类

因为方法是压在栈里一步一步执行的 所以我们从上往下找源头 可以看到 onCreate 后 addObserver 然后 init了 ObserverWithState 静态类 而且我们知道 Lifecyling 就是调用了它的 **LifecycleEventObserver** 方法 往下走

![image-20210722111108242](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722111108242.png)

先调用了 **LifecycleEventObserver** 方法 因为传进去的`object`就是我们的`TestObserver2` 得到类名后 调用 `getObserverConstructorType` 方法 返回的是 `int` 类型

![image-20210722111313156](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722111313156.png)

![image-20210722111511768](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722111511768.png)

出现了 **ClassesInfoCache** 这个类了 用布尔值去接受这个 Observer 里面是否设置了与事件绑定的方法如果有就返回 REFLECTIVE_CALLBACK 这玩意是啥 是成员变量 

```java
private static final int REFLECTIVE_CALLBACK = 1;
private static final int GENERATED_CALLBACK = 2;
```

还有一个 Generated 版的 而且关于它的代码还不少 但是用的特别少 特别特别少 你往下看 发现 return 的几乎都是 REFLECTIVE_CALLBACK 就一种情况返回它 我看有没有时间 没时间我就不看了 所以我们回来

![image-20210722112820254](C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210722112820254.png)

之后他就对`type`进行判断 GENERATED_CALLBACK 我们不管它 它直接返回了一个 **RefectiveGenericLifecycleObserver** 这个类我提到了 是一个实体类 也就是具有和非注解的Observer一样 都实现了 **onStateChanged** 的方法的类

```java
class ReflectiveGenericLifecycleObserver implements LifecycleEventObserver {
    private final Object mWrapped;
    private final CallbackInfo mInfo;

    ReflectiveGenericLifecycleObserver(Object wrapped) {
        mWrapped = wrapped;
        mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Event event) {
        mInfo.invokeCallbacks(source, event, mWrapped);
    }
}
```

但是这未免也太容易了吧？ 就这样？ 这样就可以用了？ 发现其实也没有那么简单 你怎么获得它里面的方法 参数 还有事件 还有这两段代码没有解析

在 **Lifecycling** 里的一段

```java
boolean hasLifecycleMethods = ClassesInfoCache.sInstance.hasLifecycleMethods(klass);
```

在 **ReflectiveGenericLifecycleObserver** 类中的一段

```java
mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
```

**在外部关于ClassesInfoCache只有这两句代码** 什么意思？ 也就是所有信息 什么方法名字 参数名字 全部都是 这个 **ClassesInfoCache** 自己通过你传入的klass来 **内部产生** 的 而 ReflectiveGenericLifecycleObserver 只是一个 **使用者** 通过getInfo来获得罢了 所以怎么搞懂ClassesInfoCache就很简单了 就直接从 hasLifecycleMethods一步一步往下走就OK了 

#### ClassesInfoCache

##### 先看成员变量

```java
//静态变量 让外部直接获取实例
static ClassesInfoCache sInstance = new ClassesInfoCache();

//三个私有静态变量 其实是为了判断该方法参数的三种情况
private static final int CALL_TYPE_NO_ARG = 0;//无参数
private static final int CALL_TYPE_PROVIDER = 1;//有lifecycleOwner参数
private static final int CALL_TYPE_PROVIDER_WITH_EVENT = 2;//有LifecycleOwner参数和event参数

//又是两个map集合 估计尿性和 LifecycleRegistry 差不多
private final Map<Class<?>, CallbackInfo> mCallbackMap = new HashMap<>(); 
private final Map<Class<?>, Boolean> mHasLifecycleMethods = new HashMap<>();
```

##### hasLifecycleMethods

```java
boolean hasLifecycleMethods(Class<?> klass) {
    Boolean hasLifecycleMethods = mHasLifecycleMethods.get(klass);
    if (hasLifecycleMethods != null) {
        return hasLifecycleMethods;
    }

    Method[] methods = getDeclaredMethods(klass);
    for (Method method : methods) {
        OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
        if (annotation != null) {
            // Optimization for reflection, we know that this method is called
            // when there is no generated adapter. But there are methods with @OnLifecycleEvent
            // so we know that will use ReflectiveGenericLifecycleObserver,
            // so we createInfo in advance.
            // CreateInfo always initialize mHasLifecycleMethods for a class, so we don't do it
            // here.
            createInfo(klass, methods);
            return true;
        }
    }
    mHasLifecycleMethods.put(klass, false);
    return false;
}
```

看下它都干了啥 先判空 然后调用 **getDeclaredMethods** 方法获得方法数组 大致就是得到方法数组 然后遍历得到注释 如果注释不为空 就调用 **createInfo** 方法 这个时候就走到了 **createInfo** 方法

```java
private Method[] getDeclaredMethods(Class<?> klass) {
    try {
        return klass.getDeclaredMethods();
    } catch (NoClassDefFoundError e) {
        throw new IllegalArgumentException("The observer class has some methods that use "
                + "newer APIs which are not available in the current OS version. Lifecycles "
                + "cannot access even other methods so you should make sure that your "
                + "observer classes only access framework classes that are available "
                + "in your min API level OR use lifecycle:compiler annotation processor.", e);
    }
}
```

##### createInfo

很长 但其实不难理解 跟它的名字一样 创造信息 而调用的方法名是 得到信息 所以这就是目的地了 只不过里面有很多没见过的类 CallbackInfo Map<MethodReference, Lifecycle.Event> MethodReference 一个一个看

```java
private CallbackInfo createInfo(Class<?> klass, @Nullable Method[] declaredMethods) {
    Class<?> superclass = klass.getSuperclass();
    Map<MethodReference, Lifecycle.Event> handlerToEvent = new HashMap<>();
    if (superclass != null) {
        CallbackInfo superInfo = getInfo(superclass);
        if (superInfo != null) {
            handlerToEvent.putAll(superInfo.mHandlerToEvent);
        }
    }

    Class<?>[] interfaces = klass.getInterfaces();
    for (Class<?> intrfc : interfaces) {
        for (Map.Entry<MethodReference, Lifecycle.Event> entry : getInfo(
                intrfc).mHandlerToEvent.entrySet()) {
            verifyAndPutHandler(handlerToEvent, entry.getKey(), entry.getValue(), klass);
        }
    }

    Method[] methods = declaredMethods != null ? declaredMethods : getDeclaredMethods(klass);
    boolean hasLifecycleMethods = false;
    for (Method method : methods) {
        OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
        if (annotation == null) {
            continue;
        }
        hasLifecycleMethods = true;
        Class<?>[] params = method.getParameterTypes();
        int callType = CALL_TYPE_NO_ARG;
        if (params.length > 0) {
            callType = CALL_TYPE_PROVIDER;
            if (!params[0].isAssignableFrom(LifecycleOwner.class)) {
                throw new IllegalArgumentException(
                        "invalid parameter type. Must be one and instanceof LifecycleOwner");
            }
        }
        Lifecycle.Event event = annotation.value();

        if (params.length > 1) {
            callType = CALL_TYPE_PROVIDER_WITH_EVENT;
            if (!params[1].isAssignableFrom(Lifecycle.Event.class)) {
                throw new IllegalArgumentException(
                        "invalid parameter type. second arg must be an event");
            }
            if (event != Lifecycle.Event.ON_ANY) {
                throw new IllegalArgumentException(
                        "Second arg is supported only for ON_ANY value");
            }
        }
        if (params.length > 2) {
            throw new IllegalArgumentException("cannot have more than 2 params");
        }
        MethodReference methodReference = new MethodReference(callType, method);
        verifyAndPutHandler(handlerToEvent, methodReference, event, klass);
    }
    CallbackInfo info = new CallbackInfo(handlerToEvent);
    mCallbackMap.put(klass, info);
    mHasLifecycleMethods.put(klass, hasLifecycleMethods);
    return info;
}
```

###### MethodReference

```java
static final class MethodReference {
        final int mCallType;
        final Method mMethod;

        MethodReference(int callType, Method method) {
            mCallType = callType;
            mMethod = method;
            mMethod.setAccessible(true);
        }

        void invokeCallback(LifecycleOwner source, Lifecycle.Event event, Object target) {
            //noinspection TryWithIdenticalCatches
            try {
                switch (mCallType) {
                    case CALL_TYPE_NO_ARG:
                        mMethod.invoke(target);
                        break;
                    case CALL_TYPE_PROVIDER:
                        mMethod.invoke(target, source);
                        break;
                    case CALL_TYPE_PROVIDER_WITH_EVENT:
                        mMethod.invoke(target, source, event);
                        break;
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Failed to call observer method", e.getCause());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MethodReference)) {
                return false;
            }

            MethodReference that = (MethodReference) o;
            return mCallType == that.mCallType && mMethod.getName().equals(that.mMethod.getName());
        }

        @Override
        public int hashCode() {
            return 31 * mCallType + mMethod.getName().hashCode();
        }
    }
```

这是啥 这不就是一个用反射调用方法的类吗 构造方法 把 **callType** 和 **method** 传入 不就是方法名和参数类型[^不是int String 这种参数类型 而是上面讲的那种 课上说^]吗 然后一个 **invokeCallback** 方法 把 **LifecycleOwner** 和 **Event** 还有 **Object** 传进来 前两个是参数 可空 Object是类名 反射调用方法肯定需要类名 然后其他的就是抛出异常 覆写方法啥的 

总结： 这是具有Observer方法信息的类 并且有一个方法能够调用这些方法

###### Map<MethodReference, Lifecycle.Event>

以 **MethodReference** 为内容 以 **Event** 为key的集合 为什么？ 因为这些方法你得知道你应该在虚拟机的哪个状态调用 这个集合就是干这个的 event传进去 返回需要调用的MethodReference 在调用里面的方法

###### CallbackInfo

```java
@SuppressWarnings("WeakerAccess")
static class CallbackInfo {
    //两个集合 一个是以MethodReference为内容 Event为key 称为小map
    //一个是以List<MethodReference>为内容 Event为key 称为大map 它的作用就是当传入event后 就会调用所有在该Event发生时该调用的方法
    final Map<Lifecycle.Event, List<MethodReference>> mEventToHandlers;
    final Map<MethodReference, Lifecycle.Event> mHandlerToEvent;

    //构造方法 传入小map
    CallbackInfo(Map<MethodReference, Lifecycle.Event> handlerToEvent) {
        //传入小map
        mHandlerToEvent = handlerToEvent;
        //创建大map
        mEventToHandlers = new HashMap<>();
        //遍历小map
        for (Map.Entry<MethodReference, Lifecycle.Event> entry : handlerToEvent.entrySet()) {
            //得到小map里的方法
            Lifecycle.Event event = entry.getValue();
            //以event来获取此方法该加入的List里 如果没有 说明这个Event要调用的方法为0 就创建一个
            List<MethodReference> methodReferences = mEventToHandlers.get(event);
            if (methodReferences == null) {
                methodReferences = new ArrayList<>();
                mEventToHandlers.put(event, methodReferences);
            }
           	//如果有就把该方法加入到这里面去
            methodReferences.add(entry.getKey());
        }
    }
	//这个就是集中调用方法的方法 这个和MethodReference类中的invokeCallback是一样的 但它是把所有该调用的方法集中调用
    @SuppressWarnings("ConstantConditions")
    void invokeCallbacks(LifecycleOwner source, Lifecycle.Event event, Object target) {
        //调用私有方法
        invokeMethodsForEvent(mEventToHandlers.get(event), source, event, target);
        invokeMethodsForEvent(mEventToHandlers.get(Lifecycle.Event.ON_ANY), source, event,target);
    }
	
    //把集合handlers传入
    private static void invokeMethodsForEvent(List<MethodReference> handlers,
            LifecycleOwner source, Lifecycle.Event event, Object mWrapped) {
        if (handlers != null) {
            for (int i = handlers.size() - 1; i >= 0; i--) {
                //遍历挨个调用
                handlers.get(i).invokeCallback(source, event, mWrapped);
            }
        }
    }
}
```

总结：CallbackInfo是一个比MethodReference功能更加强大的类 是它的老爸 MethodReference里面包含的是 **单个方法的信息** 那 **CallbackInfo** 包含了 所有的 MethodReference 以及他们和event的关系 也就是 把event传进来 他就会用event这把key去打开所有符合条件的MethodReference去调用它里面的方法 

但是值得注意的是 每个类都有属于自己的CallbackInfo 怎么见得呢 忘了开头那个集合了吗

```java
private final Map<Class<?>, CallbackInfo> mCallbackMap = new HashMap<>();
```

它在 **createInfo** 被调用的时候就被加入到这个集合里面了

```java
CallbackInfo info = new CallbackInfo(handlerToEvent);
mCallbackMap.put(klass, info);
mHasLifecycleMethods.put(klass, hasLifecycleMethods);
```

通过类名为key来找到属于自己的info 不得不说源码里面 map 用的真的特别多 不过这种用key找到东西的感觉还挺香 只可惜俺只会看不会写QAQ

#### 总结

现在ObserverWithState调用Lifecycling的方法 传入 Observer

```java
mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
```

然后判断它是不是**LifecycleEventObserver** [^FullLifecycleObserver是所有状态都有方法调用的LifecycleEventObserver 实体类^]如果都不是 那就说明传进来的Observer是属于注解监听的

```java
if (isLifecycleEventObserver && isFullLifecycleObserver) {
    return new FullLifecycleObserverAdapter((FullLifecycleObserver) object,
            (LifecycleEventObserver) object);
}
if (isFullLifecycleObserver) {
    return new FullLifecycleObserverAdapter((FullLifecycleObserver) object, null);
}

if (isLifecycleEventObserver) {
    return (LifecycleEventObserver) object;
}
```

然后得到类名 得到类型 在得到类型的过程中 在ClassesInfoCache中存储好关于这个类的信息

```java
final Class<?> klass = object.getClass();
int type = getObserverConstructorType(klass);
.....
return new ReflectiveGenericLifecycleObserver(object);
```

```java
boolean hasLifecycleMethods = ClassesInfoCache.sInstance.hasLifecycleMethods(klass);
```

然后通过类名得到所有方法集合

```java
Method[] methods = getDeclaredMethods(klass);
```

再挨个遍历得到注解信息

```java
for (Method method : methods) {
    OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
    ...
    ...
    ...
    }
```

如果注解不为空则调用createInfo方法 也就是创造出信息 

```java
if (annotation != null) {
    // Optimization for reflection, we know that this method is called
    // when there is no generated adapter. But there are methods with @OnLifecycleEvent
    // so we know that will use ReflectiveGenericLifecycleObserver,
    // so we createInfo in advance.
    // CreateInfo always initialize mHasLifecycleMethods for a class, so we don't do it
    // here.
    createInfo(klass, methods);
    return true;
}
```

可以看到返回值是这个 它就是存储下来的信息 而且每个类都有属于它自己的信息

```java
private CallbackInfo createInfo(Class<?> klass, @Nullable Method[] declaredMethods)
```

不停遍历 然后通过注解获得 event  然后创建 MehodReference储存 **方法** 和 **参数类型**； 再用 **verifyAndPutHandler** 再把 MethodReference和 event关联 

```java
MethodReference methodReference = new MethodReference(callType, method);
verifyAndPutHandler(handlerToEvent, methodReference, event, klass);
```

最后把 handlerToEvent转化为我们需要的info 再把 info 放入以类为key的集合里面 就OK了

至于为什么要转换呢？ 因为 handlerToEvent 它的关系是 用MethodReference来得到event 而我们更希望的是 通过放入event 来得到一个放满了只在这个event才会调用的MethodReference的List 之后会有一张图来表示前后的区别 调用时的效率会发生改变

![image-20210722182803883](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722182803883.png)

![image-20210722183328982](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210722183328982.png)

这张图就特别清晰 想象一下工作场景 如果Event是 **ONRESUME** 图一需要遍历到MR6 而且会有特别多的无效遍历 浪费时间 图二就会很高效率 就跟图书馆拿书一样 什么类型的书存在什么地方 一拿全都是你需要的 就很nice

```java
CallbackInfo(Map<MethodReference, Lifecycle.Event> handlerToEvent) {
    mHandlerToEvent = handlerToEvent;
    mEventToHandlers = new HashMap<>();
    for (Map.Entry<MethodReference, Lifecycle.Event> entry : handlerToEvent.entrySet()) {
        Lifecycle.Event event = entry.getValue();
        List<MethodReference> methodReferences = mEventToHandlers.get(event);
        if (methodReferences == null) {
            methodReferences = new ArrayList<>();
            mEventToHandlers.put(event, methodReferences);
        }
        methodReferences.add(entry.getKey());
    }
}
```

然后最后就是调用环节

```java
class ReflectiveGenericLifecycleObserver implements LifecycleEventObserver {
    private final Object mWrapped;
    private final CallbackInfo mInfo;

    ReflectiveGenericLifecycleObserver(Object wrapped) {
        mWrapped = wrapped;
        mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Event event) {
        mInfo.invokeCallbacks(source, event, mWrapped);
    }
}
```

通过传入类名来得到info 这样就能干非注解Observer能干的所有事情 而且值得注意的是 调用起来的速度是和非注解Observer是差不多的 可以说几乎相同 因为虽然反射操作需要花费时间 但是这些类 ClassesInfoCache 是在虚拟器启动时就已经生成了 它在启动时由onCreate里的addObserver这个事件触发了一系列的反射操作 通过这个类来储存下所有的信息 这些信息在运行前就已经帮你反射得到了 你只需要用正常类正常方法去调用就好了 唯一在运行时用到反射的就是这里 我想应该也差不了多少时间吧 所以只能说这写原码的人也太聪明了吧 好佩服！

```java
void invokeCallback(LifecycleOwner source, Lifecycle.Event event, Object target) {
    //noinspection TryWithIdenticalCatches
    try {
        switch (mCallType) {
            case CALL_TYPE_NO_ARG:
                mMethod.invoke(target);
                break;
            case CALL_TYPE_PROVIDER:
                mMethod.invoke(target, source);
                break;
            case CALL_TYPE_PROVIDER_WITH_EVENT:
                mMethod.invoke(target, source, event);
                break;
        }
```

 



之后会有几张思维导图 帮助理解一下 画的比较丑

# LiveData

## 为什么要用LiveData/什么是LiveData

**LiveData** 顾名思义 就是具有生命周期的Data 也就是说它也会像Activity一样具有生命周期 也会在特定的时候 自己销毁 自己销毁？ 为什么要自己销毁？ 干嘛好端端地让LiveData具有生命周期 没错 是不是觉得这段话很傻逼 因为我想讲内存泄漏(狗头) 

### 什么是内存泄漏

内存泄漏：你用new申请了一块内存，后来很长时间都不再使用了（按理应该释放），但是因为一直被某个或某些实例所持有导致 GC 不能回收，也就是该被释放的对象没有释放

这是比较官方的说法 会带来啥危害呢？内存是啥 数电里不是有啥啥RAM和ROM存储器吗 一个临时存放数据 对临时存放 因为它有个特性是易丢失 在断电的情况下 数据会直接丢失 还有一个就是只读 但空间很小 它其实是有一个个记忆单元构成的[^那张我也没咋看过 考的不多^] 记忆单元无非就是1和0 反正大概就是由很多个记忆单元构成的临时储存数据的 为啥是临时存储数据 因为内存就是类似于中间商 跑腿的 电脑里谁是大哥 **CPU** 就跟人的脑子地位一样 控制各个零件处理所有数据；那数据放哪的 **硬盘** 啥C D E盘；大哥脑力活 硬盘能装 就缺个跑腿的 就是 **内存** 它把数据从 **硬盘** 拿出来 然后拿到 **CPU** 旁边给大哥处理。那怎么样的电脑算快 第一肯定是 **CPU** 牛逼 大哥脑子动得快 运行的肯定快 但这跟内存没啥关系；第二就是 **内存**  是不是跑腿的搬得越快越多 是不是效率也能提高 就算 **CPU** 它再能动脑 发现旁边没数据处理了 那咋办 没错 所以这就是 **内存泄漏** 的危害 它会影响设备的运行速度 (感觉讲了一堆废话hhh)

声明和引用的区别

### 什么情况下会内存泄漏

一个长生命周期的对象持有一个短生命周期对象的引用

说人话就是A类里面实例化了一个B类的对象并使用它就叫A持有B的引用

有很多例子 比如第一行代码里面讲的 如果我们不使用 **LiveData** 需要让 `Activity`和`Viewmodel` 交互 那么必然的就要把 `Activity` 传入 `ViewModel` ，但是 `Viewmodel` 的生命周期是比 `activity`长的；当这个 activity 自己 Destroy 掉后；理应把它占用的内存还过来 但是 ViewModel 里还持有对它的引用 这就让 OC 误以为它还在被使用当中，就不会释放掉它的内存，知道viewmodel死掉 它才被释放。 还有就是 RxJava 网络申请 我们进行订阅操作的时候就会持有Activity的引用 如果不在onDestroy的时候取消订阅 就会发生内存泄漏 

所以就产生了这样一个需求：我需要能够让 `Activity`知道 `ViewModel`中数据的变化 数据一变化就告诉 Activity 就像签订了订阅一样 而且还不需要我自己去解除这种订阅 不需要去害怕忘记取消而导致内存泄漏 

而 **LiveData** 就是满足了这种需求的存在 它不需要你去解除订阅 只需要去Observe一下就能监听Viewmodel里的数据

## LiveData之基本用法

怎么用大佬们应该都会了吧。。 我就直接贴代码吧

这样写的目的在于只暴露不可变的LiveData给外部 外部就只能看不能改变它的数值 比较安全

```kotlin
class MainViewModel:ViewModel(){
    
    private val _counter = MutableLiveData<Int>()
    //MutableLiveData 数值可变的 livedata .value or postValue 区别在于前者必须在主线程使用 后者如果不是在主线程也会自己切换到主线程去改变数值
    
    val counter:LiveData<Int>
    get() = _counter
    
    init {
        _counter.value = 2
//        _counter.postValue(2)
    }
}
```

### 添加观察者

还有就是添加观察者 郭神书里写了两种

![image-20210723113141353](C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210723113141353.png)

发现第二种被弃用了 看文档说

```kotlin
@Deprecated(
    "This extension method is not required when using Kotlin 1.4. " +
        "You should remove \"import androidx.lifecycle.observe\""
)
```

先看第一种

![image-20210723120350844](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210723120350844.png)

两个参数都是接口 且都是实现了方法的接口 一个是Activity实现的getLifecycle 还有一个我们自己写的observer里面的 onChanged接口 为什么这么说 因为第二个参数我们还可以以匿名内部类的方式来表示 或者实名注册内部类也可以 这样写也是没有报错的

```kotlin
counter.observe(this@MainActivity,object :Observer<Int>{
    override fun onChanged(t: Int?) {
        TODO("Not yet implemented")
    }
})
```

而且它也在提醒你把它转化成lambda表达式

![image-20210723120703314](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210723120703314.png)

再看第二种

```kotlin
counter.observe(this@MainActivity){}
```

看起来就是把括号放在外面去了 但其实完全不一样的 点进去就会发现其实这是一个扩展函数 

```kotlin
@MainThread public inline fun <T> LiveData<T>.observe(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Observer<T> {
    val wrappedObserver = Observer<T> { t -> onChanged.invoke(t) }
    observe(owner, wrappedObserver)
    return wrappedObserver
}
```

可以看到这是个高阶内联函数 而且内部还有嵌套了一个lambda表达式来得到匿名内部类 Observer  因为lambda不允许使用return 但内联函数允许使用 所以用 **crossinline** 来确保内部的lambda表达式一定不会使用return 其实我感觉这个使用方式还蛮好的 不知道为啥被弃用了 反正都是用反射调用的 开销应该差不多吧

本来写课件的时候这块内容我就打算直接写下咋用就好了的 但为啥我要写那么多东西 因为我突然发现它还有第三种用法！ 两个 lambda 表达式 而且越看越不对劲

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210723112924882.png" alt="image-20210723112924882" style="zoom:80%;" />

由两个lambda表达式为参数 一个返回Lifecycle一个参数为可空Int返回null

但是我当时就很奇怪了 kotlin 里我只听过函数类型的参数 可是这是一个实现了接口的类啊 可以用lambda表示接口类的吗  我当时脑子里第一个想到的就是 **setOnClickListener**

```java
public void setOnClickListener(@Nullable OnClickListener l) {
    if (!isClickable()) {
        setClickable(true);
    }
    getListenerInfo().mOnClickListener = l;
}
```

```java
public interface OnClickListener {
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    void onClick(View v);
}
```

很明显参数是一个接口类 而我们在使用的它的时候 根本连这个 **OnClickListener** 类名 和 **onClick** 方法名都没有见到过 

```kotlin
binding.onSellRv.setOnClickListener {  }
```

什么情况能这么写 接口里只有一个方法的时候 因为只有一个方法的情况下 在编译的时候就会没有选择 只能选择唯一的一个方法去调用；而且 ()->Lifecycle 和 LifecycleOwner 一定不是同一个东西 因为我这样写就会报错

![image-20210723131152938](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210723131152938.png)

我个人认为这应该和kotlin高阶函数的机制类似 高阶函数是在编译回java时把lambda表达式这种函数类型生成一个匿名类 然后去调用这个匿名类里的方法 而()->Lifecycle应该也类似 它会在编译时被转化为一个与LifecycleOwner作用和功能完全相同的匿名类 但是它们的名字不同方法名也不同 也就是java的多态性 所以这两个并不等价 但是功能相等 所以上面这种写法就会报错 因为安卓只提供了两种写法 至今还是未解之谜

#### 注意事项

值得注意的是 我们采用 **observe** 这种方式去添加观察者的 **LiveData** ，只有当 Activity 处于活跃状态 也就是用户可视的情况下才会更新 意味着我们退到后台时 它就会停止更新 处于**onInactive**状态 那如果我们希望它按照我们指定的时候更新数据呢 

### 另一种方式添加观察者

**LiveData** 还提供了另外一种方法 那就是 `observeforever` 它与 `observe`不同的是它不与生命周期绑定 而是一个永远保持活跃的观察者 

```kotlin
private lateinit var foreverObserver: ForeverObserver
private lateinit var viewModel:MainViewModel
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        foreverObserver = ForeverObserver()

        viewModel.apply {
            counter.observe(this@MainActivity,foreverObserver)
        }.changeData(3)

    }
    inner class ForeverObserver:Observer<Int>{
        override fun onChanged(t: Int?) {
            println("data changed")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.counter.removeObserver(foreverObserver)
    }
```

继承Observer 实现 onChanged 方法 然后在 onDestroy 时解除Observer将它释放掉

### 用LiveData共享数据

在很多场景里面我们都有共享数据这个需求 不同的页面显示共同的数据 比如说像网易云 主页面和详细歌曲里面暂停键的状态 如何实现呢 官网上的说明是将LiveData实现为一个单例

有个简单的demo演示

先创建一个 枚举类 **PlayState** 表示音乐的状态

```kotlin
enum class PlayState {
    PLAYING,PAUSE,LOADING
}
```

然后写一个我们自己的单例 **LiveData** 类

```kotlin
class MyLiveData private constructor(): LiveData<PlayState>() {

    public override fun postValue(value: PlayState?) {
        super.postValue(value)

    }

	//懒加载
    companion object{
        val instance by lazy {
            MyLiveData()
        }
    }
}
```

两个fragment

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210728203652185.png" alt="image-20210728203652185" style="zoom:50%;" /><img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210728203707543.png" alt="image-20210728203707543" style="zoom:50%;" />

FirstFragment

```kotlin
//得到单例
val myLiveData = MyLiveData.instance

//设置监听事件
myLiveData.observe(requireActivity(), Observer {
    binding.textView.text = it.toString()
})
binding.btnLoading.setOnClickListener {
    myLiveData.postValue(PlayState.LOADING)
}
binding.btnPause.setOnClickListener {
    myLiveData.postValue(PlayState.PAUSE)
}
binding.btnPlay.setOnClickListener {
    myLiveData.postValue(PlayState.PLAYING)
}

binding.button4.setOnClickListener {
    findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
}
```

SecondFragment

```kotlin
val myLiveData = MyLiveData.instance
myLiveData.observe(requireActivity(), Observer {
    binding.textView.text = it.toString()
})
binding.btnLoading.setOnClickListener {
    myLiveData.postValue(PlayState.LOADING)
}
binding.btnPause.setOnClickListener {
    myLiveData.postValue(PlayState.PAUSE)
}
binding.btnPlay.setOnClickListener {
    myLiveData.postValue(PlayState.PLAYING)
}

binding.button.setOnClickListener {
    findNavController().popBackStack()
}
```

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210728204203784.png" alt="image-20210728204203784" style="zoom:50%;" />

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210728204241979.png" alt="image-20210728204241979" style="zoom:50%;" />

跳转后共享了数据





### Transformations

#### map

在之前讲过 我们希望在VM层中处理数据的改变 将不可变的LiveData暴露给View层 这个方法郭神在书上写的很详细了 比如一个User类有着用户的信息 你只希望把用户的姓名和年龄暴露给View层 而其他银行卡身份证信息你不希望暴露 那就会用这个方法

```java
private val _counter = MutableLiveData<Int>()
val counter:LiveData<Int>
get() = _counter
```

```kotlin
private val userLiveData = MutableLiveData<User>()

val userName:LiveData<String> = Transformations.map(userLiveData){ user ->
"${user.firstName}${user.lastName}"}
```

还是大概看下它怎么实现的吧 

```java
@MainThread
@NonNull
//参数是LiveData和一个接口类 实际上就是kotlin里的lambda表达式  编译的时候会自动变成一个匿名类
public static <X, Y> LiveData<Y> map(
        @NonNull LiveData<X> source,
        @NonNull final Function<X, Y> mapFunction) {
    //MediatorLiveData 应该就是用来转换用的LiveData 
    final MediatorLiveData<Y> result = new MediatorLiveData<>();
    //addSource原理应该和Observer一样 也就是说传入的这个source变化后result也会跟着改变 source和lifecyleOwner类似 类比一下类比一下
    result.addSource(source, new Observer<X>() {
        @Override
        public void onChanged(@Nullable X x) {
            result.setValue(mapFunction.apply(x));
        }
    });
    return result;
}
```

奥妙应该睡在这个Source里 它是根据版本号来实现一起更新的 所以只要传入的这个liveData数据改变版本更新 Source就会进入判断两者的版本号比较 进行改变 

也就是source的onChanged触发了result的onChanged 课上再捋逻辑吧 因为要一直切 记住result是得到的 source是放入的 result由source的改变而改变

```java
private static class Source<V> implements Observer<V> {
    final LiveData<V> mLiveData;
    final Observer<? super V> mObserver;
    int mVersion = START_VERSION;

    Source(LiveData<V> liveData, final Observer<? super V> observer) {
        mLiveData = liveData;
        mObserver = observer;
    }

    void plug() {
        mLiveData.observeForever(this);
    }

    void unplug() {
        mLiveData.removeObserver(this);
    }

    @Override
    public void onChanged(@Nullable V v) {
        if (mVersion != mLiveData.getVersion()) {
            mVersion = mLiveData.getVersion();
            mObserver.onChanged(v);
        }
    }
}
```

#### switchMap

实现是一样的 但这个用的更多 它的使用场景在ViewModel需要接受来自外部的LiveData对象 并且外部的liveData变化后在ViewModel中的LiveData也需要改变 也就是共用Observer

内联函数的写法 LiveData的扩展函数

```kotlin
inline fun <X, Y> LiveData<X>.map(crossinline transform: (X) -> Y): LiveData<Y> =
    Transformations.map(this) { transform(it) }
inline fun <X, Y> LiveData<X>.switchMap(
inline fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X> =
    Transformations.distinctUntilChanged(this)
```

### LiveData之与协程搭配

后面写吧 来不及就直接上官方文档看



## LiveData之源码分析

其实在我看来LiveData的源码比Lifecycle的源码简单太多了 而且代码量也少很多 加上注解和静态内部类才400多行 还是先把逻辑捋一遍再扣细节 因为我看源码喜欢先一步步看它的步骤 先屡清楚逻辑再看细节

### 先看成员变量

按照规律 int类型基本为计数工具 集合来存储observer 布尔为判断

```java
final Object mDataLock = new Object();
static final int START_VERSION = -1;//初始版本号 为-1
@SuppressWarnings("WeakerAccess") /* synthetic access */
static final Object NOT_SET = new Object();

private SafeIterableMap<Observer<? super T>, ObserverWrapper> mObservers =
        new SafeIterableMap<>();//其实是一个封装后的map集合 安全可迭代的map

// how many observers are in active state
@SuppressWarnings("WeakerAccess") /* synthetic access */
int mActiveCount = 0; //记录有多少个observer
// to handle active/inactive reentry, we guard with this boolean
private boolean mChangingActiveState;//保护变量
private volatile Object mData;
// when setData is called, we set the pending data and actual data swap happens on the main
// thread
@SuppressWarnings("WeakerAccess") /* synthetic access */
volatile Object mPendingData = NOT_SET;
private int mVersion;//版本号 记录数据变化次数

private boolean mDispatchingValue;//保护变量
@SuppressWarnings("FieldCanBeLocal")
private boolean mDispatchInvalidated;
private final Runnable mPostValueRunnable = new Runnable() {//这个是postValue的东西
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        Object newValue;
        synchronized (mDataLock) {
            newValue = mPendingData;
            mPendingData = NOT_SET;
        }
        setValue((T) newValue);
    }
};
```

### 绑定监听逻辑

先从 **observeForever** 开始 为什么从这个方法开始 因为这个方法就相当于 **observe** 的阉割版 也就是没有与生命周期挂钩的绑定监听器方法 一会再分析在这个的基础上 **observe** 多了什么东西

```java
@MainThread
public void observeForever(@NonNull Observer<? super T> observer) {
    //先判断当前线程是否为主线程
    assertMainThread("observeForever");
    //创建了一个对象 看它的名字 永远活跃的Observer 也就是一直监听的Observer 点进去康康
    AlwaysActiveObserver wrapper = new AlwaysActiveObserver(observer);
    //这也是一层安全系统 以observer为key得到对应的wrapper来看看我们这个 mObservers集合里有没有已经添加过这个Observer了 
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    //如果是LifecycleBoundObserver类型的 这是不该出现在这个方法里的 直接抛出异常
    if (existing instanceof LiveData.LifecycleBoundObserver) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    //如果是AlaysActivieObserver类型 不为空 那说明已经添加过了 不需要添加
    if (existing != null) {
        return;
    }
    //如果没有添加过 就把这俩observer和wrapper放入集合里 并调用这个方法 立刻更新数据 为了初始化 因为之后就会一直return 不再进入
    wrapper.activeStateChanged(true);
}
```

朴实无华的私有内部类 继承了 **ObserverWrapper** 监听包裹类 就是对Observer进行包装的类 再点进去康康

```java
private class AlwaysActiveObserver extends ObserverWrapper {

    AlwaysActiveObserver(Observer<? super T> observer) {
        super(observer);
    }
	//是否应该为活跃 一直返回true 就是一直保持活跃
    @Override
    boolean shouldBeActive() {
        return true;
    }
}
```

一个抽象类 里面基本都是需要覆写的方法

```java
private abstract class ObserverWrapper {
    final Observer<? super T> mObserver;
    //布尔值 是否活跃
    boolean mActive;
    //这类似于一个版本号 记录更新的次数 每次数据更新后都会更新版本 这个START_VERSION是静态常量 -1 后面会提到
    int mLastVersion = START_VERSION;

    //构造方法
    ObserverWrapper(Observer<? super T> observer) {
        mObserver = observer;
    }

    abstract boolean shouldBeActive();

    boolean isAttachedTo(LifecycleOwner owner) {
        return false;
    }

    void detachObserver() {
    }

    //唯一一个比较长的方法 活跃状态更新方法 传入一个布尔值
    void activeStateChanged(boolean newActive) {
        如果没有改变就返回
        if (newActive == mActive) {
            return;
        }
        // 立即设置活动状态，这样我们就不会将任何内容分派到非活动状态 也就是确保只在活跃状态分发数据
        // immediately set active state, so we'd never dispatch anything to inactive
        // owner
        mActive = newActive;
        //又是一个方法的调用 活跃传1 不活跃传-1
        changeActiveCounter(mActive ? 1 : -1);
        if (mActive) {
            //如果为活跃则分发事件
            dispatchingValue(this);
        }
    }
}
```



```java
@MainThread
void changeActiveCounter(int change) {
    //有个成员变量 但这个mActiveCount只有两种状态 1和0 表示活跃和不活跃 因为它只有在
    int previousActiveCount = mActiveCount;
    
    mActiveCount += change;
    //为了处理主动/非主动再入，我们使用这个布尔值进行防护
    //如果正在状态转化的话，mChangingActivieState为true 当结束后才把它变成false 避免二次转变
    if (mChangingActiveState) {
        return;
    }
    mChangingActiveState = true;
    try {
        while (previousActiveCount != mActiveCount) {
            //判断是变成活跃还是变成不活跃
            boolean needToCallActive = previousActiveCount == 0 && mActiveCount > 0;
            boolean needToCallInactive = previousActiveCount > 0 && mActiveCount == 0;
            previousActiveCount = mActiveCount;
            if (needToCallActive) {
                //回调活跃 两个都是空方法
                onActive();
            } else if (needToCallInactive) {
                //回调不活跃
                onInactive();
            }
        }
    } finally {
        mChangingActiveState = false;
    }
}
```

然后回到`ObserverWrapper`类中

然后回到 `AlwaysActiveObserver`类中

再回到`ObserveForever`方法中

```java
void dispatchingValue(@Nullable ObserverWrapper initiator) {
    //这个老套路了 就是防止进行同样的操作 只有操作结束后才会允许下一个数据的更新
    if (mDispatchingValue) {
        mDispatchInvalidated = true;
        return;
    }
    mDispatchingValue = true;
    do {
        mDispatchInvalidated = false;
        //如果不为空 那么就是初始化Observer的情况下 那么只对当前Observer进行更新
        if (initiator != null) {
            considerNotify(initiator);
            initiator = null;
            //当时我看到这个循环我蒙了 为啥要遍历所有的Observer啊 不是有病吗
            //这个方法我看了很久 后来发现我自己理解错了一个概念 我真是个猪比 那就是一个 Livedata 可以有 多个Observer 没错 我误以为它遍历的是所有LiveData的所有Obserer 其实是当前LiveData的所有Observer 那就特别好理解了 这个就是在postValue和setValue的时候调用的 因为这种情况第一没有传入observer 所以initiator肯定是null；第二就是牵一发而动全身 数据一变化 所有observer里的事件全部要触发个遍 这就很好理解了
        } else {
            for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
                    mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
                considerNotify(iterator.next().getValue());
                if (mDispatchInvalidated) {
                    break;
                }
            }
        }
    } while (mDispatchInvalidated);
    mDispatchingValue = false;
}
```

再看下`Observe`的区别 重点在不同的几句里

```java
@MainThread
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread("observe");
    //如果owner状态为DESTROYED 直接返回
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
        // ignore
        return;
    }
    //绑定Lifecycle的Observer
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    if (existing != null && !existing.isAttachedTo(owner)) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    if (existing != null) {
        return;
    }
    //果然把这个observer加入到LifecycleRegistry里了
    owner.getLifecycle().addObserver(wrapper);
}
```

代码量明显比 **AlwaysActiveObserver** 多 并且还实现了LifecycleEventObserver接口 那么说明啥 它可以被 **addObserver** 且里面可实现 **onStateChanged** 方法被通知状态变化

```java
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
    @NonNull
    final LifecycleOwner mOwner;

    LifecycleBoundObserver(@NonNull LifecycleOwner owner, Observer<? super T> observer) {
        super(observer);
        mOwner = owner;
    }

    //判断是否应该为活跃 因为我们提到observe的监听器只有在虚拟机可视的情况才会更新数据 也就是至少为start
    @Override
    boolean shouldBeActive() {
        //之前提到的isAtLeast方法有用了 只有当状态为started或Resuemd才返回true
        return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
    }

    //实现接口方法 
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source,
            @NonNull Lifecycle.Event event) {
        //如果为Destroy时 它会自己取消绑定
        Lifecycle.State currentState = mOwner.getLifecycle().getCurrentState();
        if (currentState == DESTROYED) {
            removeObserver(mObserver);
            return;
        }
        Lifecycle.State prevState = null;
        //确保LiveData一直保持最新的活跃状态
        while (prevState != currentState) {
            prevState = currentState;
            //然后不断更新活跃状态 如果为true则分发数据 为false则关闭
            activeStateChanged(shouldBeActive());
            currentState = mOwner.getLifecycle().getCurrentState();
        }
    }

    
    @Override
    boolean isAttachedTo(LifecycleOwner owner) {
        return mOwner == owner;
    }

    @Override
    void detachObserver() {
        mOwner.getLifecycle().removeObserver(this);
    }
}
```

```java
void activeStateChanged(boolean newActive) {
    if (newActive == mActive) {
        return;
    }
    // immediately set active state, so we'd never dispatch anything to inactive
    // owner
    mActive = newActive;
    changeActiveCounter(mActive ? 1 : -1);
    if (mActive) {
        dispatchingValue(this);
    }
}
```

最后再捋一遍流程

唯一的区别就是多了一个是否为活跃的机制和自动取消绑定

### postValue和setValue的流程

我们都知道setValue一定需要在主线程 而postValue可以在任意线程去改变 那原理是什么呢 实际上postValue最终还是会转化到主线程再去调用setValue 本质一样

#### setValue

```java
@MainThread
protected void setValue(T value) {
    assertMainThread("setValue");
    //版本号加1
    mVersion++;
    //把更改数据
    mData = value;
    //把Observer里的事件调用
    dispatchingValue(null);
}
```

遍历遍历遍历

```java
for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
        mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
    considerNotify(iterator.next().getValue());
    if (mDispatchInvalidated) {
        break;
    }
}
```

postValue

```java
protected void postValue(T value) {
    boolean postTask;
    //用两个锁保证线程安全
    //锁住 只处理当前线程的这个语句
    synchronized (mDataLock) {
        //判断mPendingData是否为未设置 也就是是否在更新数据
        postTask = mPendingData == NOT_SET;
        mPendingData = value;//然后赋值
    }
    //双重保险 如果正在更新数据直接返回
    if (!postTask) {
        return;
    }
    //得到线程池实例转换到主线程
    ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
}
```

可以看到最终还是调用setVaule

```java
private final Runnable mPostValueRunnable = new Runnable() {
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        Object newValue;
        synchronized (mDataLock) {
            newValue = mPendingData;
            mPendingData = NOT_SET;
        }
        setValue((T) newValue);
    }
};
```

虽然postValue比setValue安全 但是能用setValue的时候也尽量使用setValue 因为postValue最后仍然是会调用setValue的 而且postValue会出现数据丢失的情况 为什么？ 因为多次使用postValue时 而runnable在启动前会多次改变它的值

## 流程图

value

![image-20210724143717135](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210724143717135.png)

observe

![image-20210724143755797](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210724143755797.png)

# DataBinding/ViewBinding

## 什么是DataBinding，什么是ViewBinding？两者有什么区别

还记得被findViewById支配的恐惧吗 在还在用 `java`的时候 每次编写`Activity`的时候都需要用大量代码量去findViewById 到后来出现了黄油刀Butterknife 但是依旧需要去声明变量；然后到了`kotlin`第一次接触`kotlin-android-extensions`插件 卧槽这玩意也太香了吧 然而用着用着 这玩意就被弃用了

![image-20210725001721035](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725001721035.png)

被`ViewBinding`替代了？ 所以说`ViewBinding`是一个具有和`kotlin-android-extensions`相同功能的东西并且也需要在 gradle 里声明

```kotlin
buildFeatures{
    viewBinding true
}
```

sync后在它会在工程里多出一个build包 如果找不到 因为它是在运行的时候编译的 但是你也可以Ctrl+F9 先makeProject

![image-20210725002325597](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725002325597.png)

DataBinding的源码我就不分析了 因为感觉对于现在的我没什么用 LiveData和Lifecycle的源码可以让我理解它们的用法 基本不会报错 报错了也可以知道哪个环节出错了 DataBinding的话不影响我的使用。。 我就讲下它怎么绑定布局和拿到我们需要的view

```java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.ysh.learndatabinding2.R;
import java.lang.NullPointerException;
import java.lang.Override;
//实现ViewBinding接口
public final class ActivityMainBinding implements ViewBinding {
  @NonNull
    //因为我们mainActivity的布局文件里的也是ConstraintLayout 作为rootView
  private final ConstraintLayout rootView;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView) {
    this.rootView = rootView;
  }

    
    //得到rootView
  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }
//绑定布局 对于activity 因为它没有父布局也就是parent 它只需要 .inflate(LayoutInflater)就OK了 也可以 inflate(LayoutInflater,null,flase)手动添加 而fragment就需要用第二个把container传进去
  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    if (rootView == null) {
      throw new NullPointerException("rootView");
    }

    return new ActivityMainBinding((ConstraintLayout) rootView);
  }
}

public interface ViewBinding {
    /**
     * Returns the outermost {@link View} in the associated layout file. If this binding is for a
     * {@code <merge>} layout, this will return the first view inside of the merge tag.
     */
    @NonNull
    View getRoot();
}
```

![image-20210726131330564](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726131330564.png)

其实我们关心的是这部分 

用法 我觉得与其看我的废话 还是甩链接吧 估计大家都看过吧 郭神的文章 课上分析 课件里就不多写了  [郭神ViewBinding](https://blog.csdn.net/guolin_blog/article/details/113089706?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522162727656516780271529759%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fblog.%2522%257D&request_id=162727656516780271529759&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~blog~first_rank_v2~rank_v29-1-113089706.pc_v2_rank_blog_default&utm_term=viewBinding&spm=1018.2226.3001.4450) 重点还是dataBinding

## DataBinding和ViewBinding的关系

**ViewBinding** 相当于是 **DataBinding** 的阉割版 只是做到了能够快速简单的获得布局中的`View` 也就是视图绑定； 而 **DataBinding** 的功能远远不止如此 可以做到数据绑定 如果运用的好 可以为我们省去很大一部分代码量 减少Acitivity的代码  

现在改为dataBinding 再make 一下 project

![image-20210726131749056](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726131749056.png)

然后出现了一个很搞笑的事故 我换成 dataBinding 后就死活找不到那个ActivityMainBinding 我以为是出什么bug了 我就再开了一个项目 结果还是一样 结果是很傻逼的事情。。 就是它需要先把 **布局文件给转成dataBinding才会有**

![image-20210726141827151](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726141827151.png)

## 基本使用

关于 **DataBinding** 我是跟着b站的视频学的 但是我感觉他讲的不是很清楚 逻辑很乱 他的顺序是 **基本使用** **单向绑定** **双向绑定** **进阶绑定适配器和与ViewModel一起使用** 在我看来这个 **单向绑定** 根本就没有什么用 不如直接讲 等会我会讲吧 我就按照自己的思路来吧 虽然也不是很好 但是我尽可能的写明白

### 普通绑定

按这个小黄灯或者按万能的 **Alt+Enter** 快捷键 选择 **convert to data binding layout**

![image-20210726201929871](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726201929871.png)

转化为 data binding layout

```xml
<data>
    
</data>
```

我们需要添加绑定数据的类型 这个 **variable** 可以添加多个

```xml
<variable
    name=""//数据在布局中的名字 
    type="" />//数据的路径
```

先定义两个bean **Teacher** 和 **Student** 类

```kotlin
data class Teacher(var name:String,var age:Int,val subject:Subject)
data class Student(var name: String,var age:Int,val grade:Grade)
enum class Subject{
    Chinese,English,Math,Science
}
enum class Grade{
    GOOD,BAD,MID
}
```

右键复制路径 或者快捷键

![](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726144451531.png)

然后就是这样

```xml
<data>
    <variable
        name="teacher"
        type="com.ysh.learndatabinding2.bean.Teacher" />
    <variable
        name="student"
        type="com.ysh.learndatabinding2.bean.Student" />
</data>
```

当然还有一种方式 类似于导包

```XML
<data>
    <import type="com.ysh.learndatabinding2.bean.Teacher"/>
    <import type="com.ysh.learndatabinding2.bean.Student"/>
    <variable
        name="teacher"
        type="Teacher" />
    <variable
        name="student"
        type="Student" />
</data>
```

我感觉还是第一种方便 因为第二种一般不是用来干这个的 一般都是导入一些工具类啥的 然后提供给我们的<variable>去使用

然后在我们的布局文件里增加几个 **textView** 来显示这些数据 然后类似这样的设置

```xml
 android:text="@{teacher.name}"
```

但是我想讲的不是这个 更细节一点的东西 如果我想textView显示的是 <老师的名字:....> 便于管理我们应该把这个放在 string 文件夹里 那这时候应该怎么设置呢

```xml
<resources>
    <string name="app_name">LearnDataBinding2</string>
    <!-- TODO: Remove or change this placeholder text -->
    <string name="hello_blank_fragment">Hello blank fragment</string>
    <string name="tea_name">老师名字:%s</string>
</resources>
```

跟c语言里面一样 字符串为 %s 浮点为%f  

```xml
android:text="@{@string/tea_name(teacher.name)}"
```



```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <import type="com.ysh.learndatabinding2.bean.Teacher"/>
        <import type="com.ysh.learndatabinding2.bean.Student"/>
        <variable
            name="teacher"
            type="Teacher" />
        <variable
            name="student"
            type="Student" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">

        <TextView
            android:id="@+id/textView3"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{@string/tea_age(teacher.age)}"
            android:textSize="18sp"
            app:layout_constraintBottom_toTopOf="@+id/guideline"
            app:layout_constraintEnd_toStartOf="@+id/guideline4"
            app:layout_constraintStart_toStartOf="@+id/guideline2"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/textView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{@string/tea_name(teacher.name)}"
            android:textSize="18sp"
            app:layout_constraintBottom_toTopOf="@+id/guideline"
            app:layout_constraintEnd_toStartOf="@+id/guideline2"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <androidx.constraintlayout.widget.Guideline
            android:id="@+id/guideline"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            app:layout_constraintGuide_percent="0.5" />

        <androidx.constraintlayout.widget.Guideline
            android:id="@+id/guideline2"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            app:layout_constraintGuide_percent="0.35523114" />

        <androidx.constraintlayout.widget.Guideline
            android:id="@+id/guideline4"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            app:layout_constraintGuide_percent="0.7" />

        <TextView
            android:id="@+id/textView2"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{@string/tea_subject(teacher.subject.toString)}"
            android:textSize="18sp"
            app:layout_constraintBottom_toTopOf="@+id/guideline"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="@+id/guideline4"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/textView4"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{@string/stu_name(student.name)}"
            android:textSize="18sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toStartOf="@+id/guideline2"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="@+id/guideline" />

        <TextView
            android:id="@+id/textView5"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{@string/stu_age(student.age)}"
            android:textSize="18sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toStartOf="@+id/guideline4"
            app:layout_constraintStart_toStartOf="@+id/guideline2"
            app:layout_constraintTop_toTopOf="@+id/guideline" />

        <TextView
            android:id="@+id/textView6"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{@string/stu_grade(student.grade)}"
            android:textSize="18sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="@+id/guideline4"
            app:layout_constraintTop_toTopOf="@+id/guideline" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

然后回到 **activity** 可以通过和 viewBinding相同的方式来的到binding 但是 **DataBinding** 提供了一个工具类给我们更方便的去得到 binding 并顺便帮我们 setContentView 那就是 **DataBindingUtil**

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
    }
}
```

可以看到代码又简单了 点进去看看

```java
public static <T extends ViewDataBinding> T setContentView(@NonNull Activity activity,
        int layoutId) {
    return setContentView(activity, layoutId, sDefaultComponent);
}
```

这个函数的返回值也确实是 binding 然后设置一下初始值 运行

```kotlin
binding.student = Student("张三",23,Grade.GOOD)
binding.teacher = Teacher("李四",18,Subject.Math)
```

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210726155258778.png" alt="image-20210726155258778" style="zoom:50%;" />

还有关于在 **Recyclerview** 的 **adapter** 里怎么添加 因为我那个 demo 是之前就写好了的 就在课上讲吧

### 适配器绑定

**适用范围**：在布局文件中加载生成方式较为复杂的 **View** 比如需要通过url来加载图片

写到这我就想起来了 我们现在绑定的都是一些很简单的数据 比如文字 数字 但是如果有这样一个场景 我们通过接口得到的图片url 需要通过glide加载图片然后放到布局中的imageView 这时候怎么办？ 在xml文件里很难做到 所以应对这种比较复杂的数据 我们采取绑定适配器的方法来实现 也就是交给我们的代码层

我先讲流程吧 之后在画个流程图 首先先加入一个 **imageView** 

```xml
<variable
    name="url"
    type="String" />
```

然后给 imageView 定义一个新的属性 

```xml
sob:url="@{url}"
```

这个 **sob** 其实和我们布局开头的那个 **app** 是等价的 只不过这个 sob 是我们自定义的

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
xmlns:sob = "http://schemas.android.com/apk/res-auto"
```

 用 url 接受

然后到我们的 activity 我们需要定义一个静态方法 你可以定义在 companion object 里 也可以是object的类里 但必须加上 `@JvmStatic` 或者直接在外部定义静态方法 这就不需要加了

**BindingAdapter** 里的参数名要与布局文件里我们自定义的参数名相同 

```kotlin
class MainActivity:Activity(){
companion object{
    @JvmStatic
    //还有这里面的的参数可以为多个 相应的也在布局文件里多一个属性就好了
    @BindingAdapter("url")
    fun setUpImage(view:ImageView,url:String){
        Glide.with(view.context).load(url).into(view)
    }
}
}
```



```kotlin
@BindingAdapter("url")
fun setUpImage(view:ImageView,url:String){
    Glide.with(view.context).load(url).into(view)
}
```

设置属性

```kotlin
binding.url = "..."
```

然后小gaigai就出来了

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210726165154611.png" alt="image-20210726165154611" style="zoom:50%;" />

### 方法绑定

**适用范围**：给 **按钮** 或者 **item** 提供点击事件 或者 **editText** 修改数据

#### 给小gaigai设置点击事件

看在小gaigai这么好看的份上 我们在这里就给小gaigai设置一个点击事件

在这之前我们先捋一遍流程 在前面的绑定中 我们在布局文件中的 <data> 属性中声明了一个类 然后我们在 **activity** 里传入这个类的实例 这样我们在布局文件中 也能使用到这个实例的属性 那一个类里面不单单只有属性吧 还有方法 那我们是不是也可以把一个声明了很多方法的类传入<data>中 这样也能让我们在布局文件中使用这些方法

答案是肯定的 **DataBinding** 也提供了这种功能 而且有两种方式

##### 方法引用

先讲流程

现在 **Activity** 里新建一个内部类

```kotlin
inner class Listener(private val student: Student,private val teacher: Teacher){
    
}
```

然后在布局文件中添加<variable>

```xml
<variable
    name="eventListener"
    type="com.ysh.learndatabinding2.MainActivity.Listener" />
```

然后我们定义一个点击事件 [^注意：这里这个 onClickImage 方法的参数里必须有且仅有view 也就是我们点击的这个view^]

```kotlin
fun onClickImage(v:view){
    "年龄为${teacher.age}的${teacher.name}老师觉得小gaigai很漂亮".showToast(this@MainActivity)
}
```

然后我们给我们的 **binding** 设置这个内部类 可能会发现找不到那个 **eventListener** 我是先 clean Project 后在make一下就有了 如果还不行 就重启一下

```kotlin
val student = Student("张三",23,Grade.GOOD)
val teacher = Teacher("李四",18,Subject.Math)
binding.student = student
binding.teacher = teacher
binding.eventListener = Listener(student,teacher)
```

再回到布局文件中 注意它这个方式有点奇特 用两个冒号连接的

```xml
android:onClick="@{eventListener::onClickImage}"
```

我们运行一下康康 古德古德 

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210726222456720.png" alt="image-20210726222456720" style="zoom:50%;" />

我突然想到了 为什么会一定只能有view这个参数呢 因为在那个我推的那个视频里面它用到了 EditText 一个方法 我试一下 验证一下猜想

![image-20210726223152478](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726223152478.png)

保证参数相同 但是方法名不同

```kotlin
fun testFunction(s: Editable?) {
    s.toString().showToast(this@MainActivity)
}
```

```xml
android:afterTextChanged = "@{eventListener::testFunction}"
```

运行试试看 如果猜想正确 那么我们输入就会有Toast 还真是 可是为啥呢 啊 写到这我就特别想搞明白为啥 我就想看源码 再说吧 如果今天有时间的话我就看一下

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210726223646297.png" alt="image-20210726223646297" style="zoom:50%;" />

##### 方法引用 源码分析

从哪里入手呢 我们肯定得知道我们的 onClickImage 方法是在哪里被调用的 直接打断点debug看下呗 

点击后就到这了 按照我们方法栈的原理 从下到上调用 所以看到了这个类 **ActivityMainBindingImpl** 

![image-20210726234340501](C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210726234340501.png)

点进去我们就看到了两个方法实现类 一个就是 onClick 还有一个是 我们 editText 的afterTextChanged 方法

```java
// Listener Stub Implementations
public static class OnClickListenerImpl implements android.view.View.OnClickListener{
    private com.ysh.learndatabinding2.MainActivity.Listener value;
    public OnClickListenerImpl setValue(com.ysh.learndatabinding2.MainActivity.Listener value) {
        this.value = value;
        return value == null ? null : this;
    }
    @Override
    public void onClick(android.view.View arg0) {
        this.value.onClickImage(arg0); 
    }
}
public static class AfterTextChangedImpl implements androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged{
    private com.ysh.learndatabinding2.MainActivity.Listener value;
    public AfterTextChangedImpl setValue(com.ysh.learndatabinding2.MainActivity.Listener value) {
        this.value = value;
        return value == null ? null : this;
    }
    @Override
    public void afterTextChanged(android.text.Editable arg0) {
        this.value.testFunction(arg0); 
    }
}
```

这只是定义 那在哪里创建呢

![image-20210726234806232](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210726234806232.png)

在一个叫 **executeBindings()** 的方法里 也就是 **执行绑定** 

```java
eventListenerOnClickImageAndroidViewViewOnClickListener = (((mEventListenerOnClickImageAndroidViewViewOnClickListener == null) ? (mEventListenerOnClickImageAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mEventListenerOnClickImageAndroidViewViewOnClickListener).setValue(eventListener));
```

它通过 setValue 把 **eventListener** 传进去 那这个玩意是啥呢

```java
com.ysh.learndatabinding2.MainActivity.Listener eventListener = mEventListener;
```

就是我们定义的内部类 那返回的这个 **eventListenerOnClickImageAndroidViewViewOnClickListener** 是什么？ 前面都是创建 那这个肯定就是来调用的 所以我们在看看它被调用的地方

```java
//可以看到它返回的就是它本身 也就是说返回去的这个值直接可以调用 onClick 方法
public OnClickListenerImpl setValue(com.ysh.learndatabinding2.MainActivity.Listener value) {
    this.value = value;
    return value == null ? null : this;
}
```

就在它下面。。。 给图片设置了监听事件

```java
if ((dirtyFlags & 0x11L) != 0) {
    // api target 1

    androidx.databinding.adapters.TextViewBindingAdapter.setTextWatcher(this.edtTest, (androidx.databinding.adapters.TextViewBindingAdapter.BeforeTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.OnTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged)eventListenerTestFunctionAndroidxDatabindingAdaptersTextViewBindingAdapterAfterTextChanged, (androidx.databinding.InverseBindingListener)null);
    this.imageView.setOnClickListener(eventListenerOnClickImageAndroidViewViewOnClickListener);
}
```

所以这也解释了 为什么只能传入 **view** 这个参数 因为它生成的静态类 是继承了这个 **view** 的 **onClickListener** 这个接口 然后实现了它的 onClick 方法 在把这个类的实例传给 **view** 其实就和 **setOnClickLisntenr** 的流程一模一样 只不过加了层包装 所以它叫做 **方法引用** 覆写了该方法 

##### 监听器绑定

用lambda表达式来调用方法

定义一个新方法

```kotlin
fun onClickImageByJT(){
    "年龄为${teacher.age}的${teacher.name}老师觉得小gaigai很漂亮".showToast(this@MainActivity)
}
```

布局文件中换成 lambda 表达式

```xml
android:onClick="@{()->eventListener.onClickImageByJT()}"
```

但是如果我们需要知道我们点击了哪个 **View** 怎么办 或者需要参数怎么办 

```kotlin
fun onClickImageByJT(v: View){
    "年龄为${teacher.age}的${teacher.name}老师觉得小gaigai很漂亮".showToast(this@MainActivity)
}
```



```xml
android:onClick="@{(view)->eventListener.onClickImageByJT(view)}"
```

为什么可以这样写呢 因为 lambda 表达式括号内显示的应该是调用方法的参数 然后执行箭头指的方法 也就是说 点击 触发了该 View 的 onClick 方法 然后这个 onClick 方法里调用了 eventListener 这个类中的 onClcikImageByJT 方法 传入了一个 view 参数 这样就很好理解了

##### 监听器绑定源码分析

老配方 打断点 debug 卧槽好像不太一样

![image-20210727000556789](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727000556789.png)

可以看到它是先在 **OnClickListener** 这个类中 这个类实现了 onClick 方法 那这个类在哪里实例化呢 点进去看看

![image-20210727000708320](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727000708320.png)



![image-20210727002013905](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727002013905.png)

它把 **this** 也就是 **ActivityMainBindingImpl** 本身 在传一个1作为id

```java
mCallback1 = new com.ysh.learndatabinding2.generated.callback.OnClickListener(this, 1);
```

那这个又是在哪里被调用 还是在 **executeBindings()** 方法里被设置了 Listener 但是这个 **OnClickListener** 跟之前的不一样

```java
this.imageView.setOnClickListener(mCallback1);
```

为什么呢 因为它又多了一层包装

```java
public final class OnClickListener implements android.view.View.OnClickListener {
    final Listener mListener;
    final int mSourceId;
    public OnClickListener(Listener listener, int sourceId) {
        mListener = listener;
        mSourceId = sourceId;
    }
    //在这个地方又调用了 ActiviityMainBindingImpl的方法 传入了我们在这里需要的参数 
    @Override
    public void onClick(android.view.View callbackArg_0) {
        mListener._internalCallbackOnClick(mSourceId , callbackArg_0);
    }
    public interface Listener {
        void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0);
    }
}
```

多出来的一层包装 在这里调用我们自己定义的方法

```java
public final void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0) {
    // localize variables for thread safety
    // eventListener
    com.ysh.learndatabinding2.MainActivity.Listener eventListener = mEventListener;
    // eventListener != null
    boolean eventListenerJavaLangObjectNull = false;



    eventListenerJavaLangObjectNull = (eventListener) != (null);
    if (eventListenerJavaLangObjectNull) {



        eventListener.onClickImageByJT(callbackArg_0);
    }
}
```

最后再画个流程图吧 好辨别



#### 总结：

不管是监听器绑定还是方法引用 这两种方式都是为 **imageView** 提供了一个经过层层包装的一个 **OnClickListener** 实现类 而监听器绑定相比于方法引用多了一层包装 但两个的功能范围是一样的； **方法引用** 更像是覆写了 **onClick** 方法；而 **监听器绑定** 更像是在 onCick 方法下调用了 我们自己写的方法 

## 与ViewModel和LiveData实现双向绑定

我先把基本框架搭好 左边三个 **textView** 一个 **checkBox** 右边两个 **editText** 一个 **textView** 和一个 **button**

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210727163706144.png" alt="image-20210727163706144" style="zoom:50%;" />

 ViewModel

```kotlin
class TwoBindingViewModel : ViewModel() {
    //数字1
    val number1 = MutableLiveData<String>()

    //数字2
    val number2 = MutableLiveData<String>()
}
```

布局

```xml
<data>
    <variable
        name="viewModel"
        type="com.ysh.learndatabinding2.TwoBindingViewModel" />
</data>
```

Activity

```kotlin
class TwoBindingActivity : AppCompatActivity() {
    private val viewModel by lazy {
    ViewModelProvider (this).get(TwoBindingViewModel::class.java)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityTwoBindingBinding>(
            this,
            R.layout.activity_two_binding
        )
        //UI变化通知数据变化
        binding.viewModel = viewModel
        viewModel.apply {
            val that = this@TwoBindingActivity
            number1.observe(that, Observer {
                println("数字1变化 ====> $it")
            })
            number2.observe(that, Observer {
                println("数字2变化 ====> $it")
            })
        }
    }
}
```

实现双向绑定 也就是说 当 **UI** 发生改变 会通知到 **数据** 发生改变；当 **数据** 发生改变 会通知 **UI** 也进行改变

**UI通知数据** 

在布局文件中的写法有所改变 在之前的基础上加个 **=** 号就可以实现通知数据变化

```xml
android:text="@={viewModel.number2}"
```

运行一下 有了

![image-20210727170745491](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727170745491.png)

简单看下原理吧 还是 debug 一下吧 

![image-20210727170201958](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727170201958.png)

```java
private androidx.databinding.InverseBindingListener editTextNumberandroidTextAttrChanged = new androidx.databinding.InverseBindingListener() {
    @Override
    public void onChange() {
        ...
        ....
        ....
        if (viewModelJavaLangObjectNull) {
            viewModelNumber1 = viewModel.getNumber1();
            viewModelNumber1JavaLangObjectNull = (viewModelNumber1) != (null);
            if (viewModelNumber1JavaLangObjectNull) {
			
                //在这里调用了 LiveData 的 setValue 方法
                viewModelNumber1.setValue(((java.lang.String) (callbackArg_0)));
            }
        }
    }
};
```

所以大致的流程我们就能猜出来了 在布局文件中多加了一个 **=** ，先不管它是如何去分辨的 但这在编译时通知到了 **ActivityMainBindingImpl** 多了一个实现了 **InverseBindingListener** 接口的成员对象 当 **editText** 触发 **onChange** 方法后 就会触发 **LiveData** 的 **setVaule** 方法 从而触发了 监听器的方法 做到数据的改变和通知数据

**数据通知UI**

我们先给button一个监听事件 改变了 LiveData 的数值

```kotlin
fun updateNumber(v:View){
    viewModel.number1.value = "55.5"
}
```

但是这样是不会通知到 **UI** 层的 而且也不会改变 **UI**  可以看到数据发生了改变 但UI层没有变化

![image-20210727171520984](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727171520984.png)

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210727171529777.png" alt="image-20210727171529777" style="zoom:50%;" />

为什么呢？ 没有为什么啊 UI凭什么知道你数据变化了 在之前相当于是把 **控件** 和 数据的observer 进行绑定 从而使控件发生变化时带动了 数据的 observer事件 那现在反过来 应该也是类似的原理 那怎么设置 这时候就到了我们滴超人 **lifecycleOwner** 了

加上这一句之后就OK了

```kotlin
binding.lifecycleOwner = this
```

真的变化了

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210727172354860.png" alt="image-20210727172354860" style="zoom:50%;" />

原理呢 先打断点debug吧

当时我就懵了 因为真的看不出什么名堂 只知道它最后调用了 **executeBinding** 方法 **setText** 改变了文本

![image-20210727183735521](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727183735521.png)

所以换一种思路 直接点进去 看下它如何设置的 LifecycleOwner

```java
@MainThread
public void setLifecycleOwner(@Nullable LifecycleOwner lifecycleOwner) {
    if (mLifecycleOwner == lifecycleOwner) {
        return;
    }
    if (mLifecycleOwner != null) {
        mLifecycleOwner.getLifecycle().removeObserver(mOnStartListener);
    }
    mLifecycleOwner = lifecycleOwner;
    if (lifecycleOwner != null) {
        if (mOnStartListener == null) {
            mOnStartListener = new OnStartListener(this);
        }
        lifecycleOwner.getLifecycle().addObserver(mOnStartListener);
    }
    //重点放在这里 那这个weakListener是什么呢 先不急 我们一直点下去
    for (WeakListener<?> weakListener : mLocalFieldObservers) {
        if (weakListener != null) {
            weakListener.setLifecycleOwner(lifecycleOwner);
        }
    }
}
```

```java
public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
    mObservable.setLifecycleOwner(lifecycleOwner);
}
```

```java
private interface ObservableReference<T> {
    WeakListener<T> getListener();
    void addListener(T target);
    void removeListener(T target);
    void setLifecycleOwner(LifecycleOwner lifecycleOwner);
}
```

![image-20210727184502049](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727184502049.png)

```java
@Override
public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
    LifecycleOwner owner = (LifecycleOwner) lifecycleOwner;
    LiveData<?> liveData = mListener.getTarget();
    if (liveData != null) {
        if (mLifecycleOwner != null) {
            liveData.removeObserver(this);
        }
        if (lifecycleOwner != null) {
            //看到这句话我大概就明白了 它又把这个LiveData设置了一个监听器 那它肯定有覆写 onChanged方法
            liveData.observe(owner, this);
        }
    }
    mLifecycleOwner = owner;
}
```

且这个类也继承了 **Observer** 接口

```java
private static class LiveDataListener implements Observer,
        ObservableReference<LiveData<?>> {
```

```java
@Override
public void onChanged(@Nullable Object o) {
    ViewDataBinding binder = mListener.getBinder();
    if (binder != null) {
        binder.handleFieldChange(mListener.mLocalFieldId, mListener.getTarget(), 0);
    }
}
```

所以到这里思路就特别清晰了 对于一个binding里面的 **LiveData**  每一个都被分配了一个 **LiveDataListener** ；而通过 **setLifecycleOwner** 

来传给这个 Listener 一个 **Owner**；再利用这个 Owner 再给这个 **LiveData** 设置一个 Observer；而这个 Observer 的作用就是为了在数据变化时 去修改UI 点进去看看这个 **handleFieldChange** 方法

一直点到这个方法

```java
protected void requestRebind() {
    if (mContainingBinding != null) {
        mContainingBinding.requestRebind();
    } else {
        final LifecycleOwner owner = this.mLifecycleOwner;
        if (owner != null) {
            Lifecycle.State state = owner.getLifecycle().getCurrentState();
            if (!state.isAtLeast(Lifecycle.State.STARTED)) {
                return; // wait until lifecycle owner is started
            }
        }
        synchronized (this) {
            if (mPendingRebind) {
                return;
            }
            mPendingRebind = true;
        }
        if (USE_CHOREOGRAPHER) {
            mChoreographer.postFrameCallback(mFrameCallback);
        } else {
            //在这里发送了一个Runnable 那这个Runnable在什么时候会被调用
            mUIThreadHandler.post(mRebindRunnable);
        }
    }
}
```

就在我们debug的时候有。。  突然就连起来了

![image-20210727190851155](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210727190851155.png)

```java
executePendingBindings();
```

```java
executeBindingsInternal();
```

```java
executeBindings();
```

```java
androidx.databinding.adapters.TextViewBindingAdapter.setText(this.editTextNumber, viewModelNumber1GetValue);
```

最后调用了 setText 方法 说实话有点厉害





## 单向绑定

单向绑定：当 **Activity** 中的数据发生变化后， **布局文件** 中也会跟着发生变化

先回到我们的 bean 类中进行改造







# WorkManager 

对于 **workManager** 的话 好像用的不多 我当初是是看B站的超人 [longway777 ](https://space.bilibili.com/137860026/) 学习的 他的入门视频讲的都特别清楚

[WorkManager](https://www.bilibili.com/video/BV1uf4y1U72D)

这里我就大概讲一下用法 内容也就是视频里的 多的兄弟们看视频吧 讲的肯定比我好

还有关于思维导图 老师的视频中的很清晰

![image-20210729195619413](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210729195619413.png)



## **正片开始**

其实对于我来说 我对一个新的事物最最最关心的并不是它的用法 而是在什么情况下使用 怎么和其他我所学的东西一起结合 这是我最关心的 相信兄弟们也应该这么想吧； **workManager** 我第一次看完视频后最大的想法就是 它和 **service** 的后台任务处理很像 那它是不是可以替代一些 **service** 的功能，废话不多说 先上图

![å¨è¿éæå¥å¾çæè¿°](https://gitee.com/ye-shenghao/test2/raw/master/static/20200925122715306.png)

翻译一下

- **是否为可延迟工作**
  请选择WorkManager

- **是否在线触发**
  请选择WorkManager

- **是否想立即执行**

  使用前台服务。

- **是否在准确的时刻运行**

  请使用AlarmManager。

因此，WorkManager针对那些可延迟的一定要完成的后台任务而设计的，这是为了更好地根据设备的电池情况来处理后台任务 非常的银杏花，但就是它并不准时，但它一定会去完成它

另外WorkManager能和其他API配合使用，但是可能没什么时间了 就不考虑了 官网上有讲

<img src="https://img-blog.csdnimg.cn/20200925140635327.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpcmtzbWFsbGVy,size_16,color_FFFFFF,t_70#pic_center" alt="å¨è¿éæå¥å¾çæè¿°" style="zoom:67%;" />

看这张图 可以看到 **workmanager** 非常银杏花地将一个任务分成了很多步骤 且每个步骤的触发条件不同 **过滤图片** 的条件是 电量不低 做完后传给 **压缩** 条件为空间不低 最后 **更新** 条件为有网



## 准备工作



### 定义工作

首先我们得创建一个 **工作类**  继承 **Work** 也就是表明它是一个定义具体工作的类 在这里定义你在后台的任务

```kotlin
class UploadWorker(appContext: Context, workerParams: WorkerParameters):
       Worker(appContext, workerParams) {
   override fun doWork(): Result {

       // 更新图片等方法
       uploadImages()

       // 返回结果
       return Result.success()
   }
           
   private fun uploadImages(){
	//在这里对图片进行处理 也就是对我们的数据进行处理 那么这里就可以和数据库或者room进行联合使用 直接在这里获取数据 并更新数据
   }
}
```

从 `doWork()` 返回的 `Result` 会通知 WorkManager 服务工作是否成功，以及工作失败时是否应重试工作。

- **Result.success()**：工作成功完成。
- **Result.failure()**：工作失败。
- **Result.retry()**：工作失败，这种情况下需要我们再次开启工作 到后面会讲

### 创建 WorkRequest

如果我说 **UploadWork** 是具体的工作 那么我们之前提到过 **workManager** 真正强大的点在于它能够给工作提供限制条件从而保证电池的安全 所以有了 **具体工作** 我们还需要定义 **工作的运行方式或条件 和 时间即一次性or周期** 这时候就需要 **WorkRequest**

```kotlin
val uploadWorkRequest: WorkRequest =
   OneTimeWorkRequestBuilder<UploadWorker>()
       .build()
```

### 开启任务

将 **Work** 加入队列

```kotlin
private val workManager = WorkManager.getInstance(this)


workManager.enqueue(uploadWorkRequest)
```

### 定义工作请求

上面讲到 我们需要给我们的 **Work** 定义具体的信息 (**工作的运行方式或条件 和 时间即一次性or周期**)  

#### 一次性工作

```kotlin
val uploadWorkRequest = OneTimeWorkRequest.from(UploadWorker::class.java)

//or
val uploadWorkRequest = OneTimeWorkRequest<UploadWorker>
			//这里是具体的信息
			.bulid()
```

#### 周期工作

```kotlin
val uploadWorkRequest =
       PeriodicWorkRequestBuilder<UploadWorker>(1, TimeUnit.HOURS)//表示一小时进行一次
    		//具体信息
           	.build()
```

下面这张图表示  **interval1** 表示每个工作的总间隔时间 而 **flex period can run work** 表示你的 **work** 只会在这段时间被执行

<img src="https://developer.android.google.cn/images/topic/libraries/architecture/workmanager/how-to/definework-flex-period.png?hl=zh_cn" alt="æ¨å¯ä»¥ä¸ºå®æä½ä¸è®¾ç½®ä¸ä¸ªçµæ´»é´éãæ¨è¦å®ä¹ä¸ä¸ªéå¤é´éï¼ç¶ååå®ä¹ä¸ä¸ªçµæ´»é´éï¼æå®ä¸ä¸ªå¨éå¤é´éæ«å°¾å¼å§çå·ä½æ¶é´æ®µï¼ãWorkManager ä¼å°è¯å¨æ¯ä¸ªå¨æççµæ´»é´éåè¿è¡ä½ä¸ã" style="zoom: 50%;" />

```kotlin
val myUploadWork = PeriodicWorkRequestBuilder<SaveImageToFileWorker>(
       1, TimeUnit.HOURS, // interval
       15, TimeUnit.MINUTES) // flex period
    .build()
```

官方文档说的是这样定义的是在最后 十五分钟 内运行的工作 所以我当时在纳闷可不可以把这个 **flex period** 定义在 **interval** 中间这段时间呢？ 但是我后来想想没有必要 因为定义在中间这种情况完全可以由定义在最后来实现 我去画个图 看下面这张图 两个情况几乎是一模一样的。。 因为本身我们的 **workManager** 就是可延迟的 而且我们 **work** 发生的时间是在 **flex period** 这个区间内 所以这两种几乎是一种情况 所以第二个参数 就是填在 **interval** 最后的多少时间 处理该 **work**

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210730160952983.png" alt="image-20210730160952983" style="zoom:80%;" />

### 工作约束

这里就直接贴图了

| **NetworkType**      | 约束运行工作所需的网络类型。例如 Wi-Fi (`UNMETERED`)。       |
| -------------------- | ------------------------------------------------------------ |
| **BatteryNotLow**    | 如果设置为 true，那么当设备处于“电量不足模式”时，工作不会运行。 |
| **RequiresCharging** | 如果设置为 true，那么工作只能在设备充电时运行。              |
| **DeviceIdle**       | 如果设置为 true，则要求用户的设备必须处于空闲状态，才能运行工作。如果您要运行批量操作，否则可能会降低用户设备上正在积极运行的其他应用的性能，建议您使用此约束。 |
| **StorageNotLow**    | 如果设置为 true，那么当用户设备上的存储空间不足时，工作不会运行。 |

<NetWorkType>

| Enum values   |                                                              |
| :------------ | ------------------------------------------------------------ |
| `NetworkType` | **CONNECTED **Any working network connection is required for this work. |
| `NetworkType` | **METERED** A metered network connection is required for this work. |
| `NetworkType` | **NOT_REQUIRED** A network is not required for this work.    |
| `NetworkType` | **NOT_ROAMING **A non-roaming network connection is required for this work. |
| `NetworkType` | **TEMPORARILY_UNMETERED **A temporarily unmetered Network.   |
| `NetworkType` | **UNMETERED** An unmetered network connection is required for this work. |

```kotlin
val constraints = Constraints.Builder()
   .setRequiredNetworkType(NetworkType.UNMETERED)
   .setRequiresCharging(true)
   .build()

val uploadWorkRequest: WorkRequest =
   OneTimeWorkRequestBuilder<UploadWork>()
       .setConstraints(constraints)
       .build()
```

### 延迟工作

加入队列后 延迟十分钟执行 **Work**

```kotlin
val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWork>()
   .setInitialDelay(10, TimeUnit.MINUTES)
   .build()
```

如果给 **周期性工作** 设置延迟的话 它只会对 **第一次** 工作进行延迟

### 重试和退避政策

之前我们就讲到了 当 **Result** 返回 **retry** 时 我们通常是需要重新再次进入工作 比如说 该工作本是希望在有网络的时候执行 但用户当时恰好断网 所以在重新联网后 我们仍希望继续执行该工作 

官网上是这样说明的

系统将根据 **退避延迟时间** 和 **退避政策** 重新调度工作。

- **退避延迟时间** 指定了首次尝试后重试工作前的最短等待时间。此值不能超过 10 秒（或  MIN_BACKOFF_MILLIS）。
- **退避政策** 定义了在后续重试过程中，退避延迟时间随时间以怎样的方式增长。WorkManager 支持 2 个退避政策，即 `LINEAR` 和 `EXPONENTIAL`。

每个工作请求都有退避政策和退避延迟时间。默认政策是 `EXPONENTIAL`，延迟时间为 10 秒，但您可以在工作请求配置中替换此设置。

```kotlin
val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
   .setBackoffCriteria(
       BackoffPolicy.LINEAR,
       OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
       TimeUnit.MILLISECONDS)
   .build()
```

第一次运行以 `Result.retry()` 结束并在 10 秒后重试；然后，如果工作在后续尝试后继续返回 `Result.retry()`，那么接下来会在 20 秒、30 秒、40 秒后重试，以此类推。如果退避政策设置为 `EXPONENTIAL`，那么重试时长序列将接近 20、40、80 秒，以此类推。

#### 退避延迟时间

## Constants

**DEFAULT_BACKOFF_DELAY_MILLIS**    //默认值 最小回退时间为 30000毫秒 也就是 30秒

```
public static final long DEFAULT_BACKOFF_DELAY_MILLIS
```

The default initial backoff time (in milliseconds) for work that has to be retried.

Constant Value: 30000 (0x0000000000007530) 



**MAX_BACKOFF_MILLIS **  //最大18000秒

```
public static final long MAX_BACKOFF_MILLIS
```

The maximum backoff time (in milliseconds) for work that has to be retried.

Constant Value: 18000000 (0x000000000112a880)



**MIN_BACKOFF_MILLIS**  //10秒

```
public static final long MIN_BACKOFF_MILLIS
```

The minimum backoff time for work (in milliseconds) that has to be retried.

Constant Value: 10000 (0x0000000000002710)

#### 退避政策

**EXPONENTIAL**   指数方式增长回退时间 也就是 10秒 20秒 30秒 40秒

**LINEAR** 线性方式增加回退时间  也就是 10秒 20秒 40秒 80秒

### 标记工作

虽然每个 **WorkRequest** 都有属于自己唯一的 **id** 但这个 **id** 不方便记忆 而且有时候多个 **WorkRequest** 是为了做一件事情 也就是说在逻辑上相关的工作 所以我们希望把它们分成一个组别 所以出现了 **tag**  你可以给每个 **请求** 取一个唯一的 **tag** ；但 **tag** 的名字并不唯一 也就是多个请求可以有 **同一个** tag

```kotlin
val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
   .addTag("cleanup")
   .build()
```

`WorkManager.cancelAllWorkByTag(String)` 会取消带有特定标记的所有工作请求，`WorkManager.getWorkInfosByTag(String)` 会返回一个 WorkInfo 对象列表，该列表可用于确定当前工作状态。

### 管理工作

#### 唯一工作

在官方文档里对唯一工作的说明是 既可以用于 **一次性任务** 也可以用于 **周期性任务**  但是对它的概念不是很清晰

我对它的归纳是：唯一请求是对于工作请求 **workRequest** 的； 一个工作请求想要成为唯一工作 相当于在队列中注册了一个会员 注册会员的条件就是 **一**、填入自己的名字 **uniqueWorkName** **二**、如果有人冒充自己应该怎么做 **existingWorkPolicy**  作为会员 一定会得到队列的高度重视 如果你是那个会员 你现在正在队列里活动 而我是你的同类 我的名字和你一样 我这时也想进入 那怎么办 所以我们需要在注册会员的时候对这种情况进行设置`replace`：把你请出去 让我进来 `keep` 让我滚出去 你继续在里面 `append`：我可以进来 但我需要排队 而且是等队列中所有人都完成后 我才能进入 这样就很好理解了 **三**、**sendLogsWorkRequest** 也就是给哪类请求注册会员



这样讲完之后再看下官方的解释应该就比较清晰了

- `WorkManager.enqueueUniqueWork()`（用于一次性工作）
- `WorkManager.enqueueUniquePeriodicWork()`（用于定期工作）

这两种方法都接受 3 个参数：

- uniqueWorkName - 用于唯一标识工作请求的 `String`。
- existingWorkPolicy - 此 `enum` 可告知 WorkManager：如果已有使用该名称且尚未完成的唯一工作链，应执行什么操作。如需了解详情，请参阅[冲突解决政策](https://developer.android.google.cn/topic/libraries/architecture/workmanager/how-to/managing-work?hl=zh_cn#conflict-resolution)。
- work - 要调度的 `WorkRequest`。

- [`REPLACE`]：用新工作替换现有工作。此选项将取消现有工作。
- [`KEEP`]：保留现有工作，并忽略新工作。
- [`APPEND`]：将新工作附加到现有工作的末尾。此政策将导致您的新工作[链接](https://developer.android.google.cn/topic/libraries/architecture/workmanager/how-to/chain-work?hl=zh_cn)到现有工作，在现有工作完成后运行。

现有工作将成为新工作的先决条件。如果现有工作变为 `CANCELLED` 或 `FAILED` 状态，新工作也会变为 `CANCELLED` 或 `FAILED`。如果您希望无论现有工作的状态如何都运行新工作，请改用 `APPEND_OR_REPLACE`。

- [`APPEND_OR_REPLACE`] 函数类似于 `APPEND`，不过它并不依赖于**先决条件**工作状态。即使现有工作变为 `CANCELLED` 或 `FAILED` 状态，新工作仍会运行。

```kotlin
WorkManager.getInstance(this).enqueueUniquePeriodicWork(
           "sendLogs",
           ExistingPeriodicWorkPolicy.KEEP,
           sendLogsWorkRequest
)
```

### 观察您的工作

我们在那张图中看到 workManager 的工作信息是可观察的 它在进入队列时会生成一个 **WorkInfo** 也就是工作信息类 里面有 **Work** 的 id，tag，state等等信息 也就是我们做后台任务得到的反馈

```java
public final class WorkInfo {

    private @NonNull UUID mId;
    private @NonNull State mState;
    private @NonNull Data mOutputData;
    private @NonNull Set<String> mTags;
    private @NonNull Data mProgress;
    private int mRunAttemptCount;
```

那怎么获得呢 按照惯例应该是通过 **id** 但这个可以通过三种方式来获得 **name** **id** 和 **tag**

**name** 即我们注册的会员名字 也就是唯一工作的名字

**id** 即固有 **id** 可以通过 workRequest.id 获得

**tag** 也是我们自己添加的

```kotlin
//by id
workManager.getWorkInfoById(syncWorker.id) // ListenableFuture<WorkInfo>

// by name
workManager.getWorkInfosForUniqueWork("sync") // ListenableFuture<List<WorkInfo>>

// by tag
workManager.getWorkInfosByTag("syncTag") // ListenableFuture<List<WorkInfo>>
```

但是这样得到的只是一瞬间的信息 而我们需要观察其变化的话 我们就想到了 **LiveData** 又联系起来了 

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210730220520748.png" alt="image-20210730220520748" style="zoom:67%;" />

这样我们就可以实现监听了

```kotlin
workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).observe({lifecycle},{
    if(it.state==WorkInfo.State.SUCCEEDED){
        "process is ${it.progress}, the data is ${it.outputData}"
    }
})
```

而且这里提供了 **progress** 进度条 所以可以对下载任务进行进度条监听

但是这样都只是一个一个找 是不是特别麻烦 不急 **workManager** 还提供了更方便的查询类 **workQuery**

```java
public final class WorkQuery {
    private final List<String> mUniqueWorkNames;
    private final List<String> mTags;
    private final List<WorkInfo.State> mStates;
```

用法：

```kotlin
val workQuery = WorkQuery.Builder
       .fromTags(listOf("syncTag"))
       .addStates(listOf(WorkInfo.State.FAILED, WorkInfo.State.CANCELLED))
       .addUniqueWorkNames(listOf("preProcess", "sync")
    )
   .build()

val workInfos: ListenableFuture<List<WorkInfo>> = workManager.getWorkInfos(workQuery)
```

### 取消和停止工作

```kotlin
// by id
workManager.cancelWorkById(syncWorker.id)

// by name
workManager.cancelUniqueWork("sync")

// by tag
workManager.cancelAllWorkByTag("syncTag")
```

### 链接工作

也就是让工作按照顺序执行

```kotlin
WorkManager.getInstance(myContext)
   // Candidates to run in parallel
   .beginWith(listOf(plantName1, plantName2, plantName3))
   // Dependent work (only runs after all previous work in chain)
   .then(cache)
   .then(upload)
   // Call enqueue to kick things off
   .enqueue()
```

而我们需要注意的是

当您链接 `OneTimeWorkRequest` 实例时，父级工作请求的输出将作为子级的输入传入。因此，在上面的示例中，`plantName1`、`plantName2` 和 `plantName3` 的输出将作为 `cache` 请求的输入传入。

**WorkManager** 采用的是`InputMerger` 所以我们来了解一下 InputMerger

它分为两种 **OverwritingInputMerger** 和 **ArrayCreatingInputMerger**

它们的区别就在于出现键冲突时的处理 那么什么是键冲突？ 因为我们 **beginWith** 里的 plantName 1 2 3 是同级的 那么它们就是并联发生的 所以它们的输出 **Data** 我们就称为 **Key1，Key2，Key3**  它们的产生有快有慢 或者有巧合的时候是同时发生的 那么在这个同时发生的情况下 就会导致两个 Data 共用一个 **Key** 也就是键冲突。 那这个时候应该怎么处理呢 ？  

####  OverwritingInputMerger

**OverwritingInputMerger** 采用的我们称之为 **覆盖法** 我什么都不管 这个Data它只要一来 我就把它放到 **Key1** 里去替代之前的值 所以最后得到的 **Key1** 就是几个发生冲突的 **Data** 最慢输出的那个 

<img src="https://developer.android.google.cn/images/topic/libraries/architecture/workmanager/how-to/chaining-overwriting-merger-example.png?hl=zh_cn" alt="æ­¤å¾æ¾ç¤ºäºä¸ä¸ªä½ä¸å°ä¸åçè¾åºä¼ éç»é¾ä¸­çä¸ä¸ä¸ªä½ä¸ãç±äºè¿ä¸ä¸ªè¾åºé½å·æä¸åçé®ï¼å æ­¤ä¸ä¸ä¸ªä½ä¸å°æ¶å°ä¸ä¸ªé®å¼å¯¹ã" style="zoom: 67%;" /> 

这时候 **plantName1** 和 **plantName2** 发生键冲突



<img src="https://developer.android.google.cn/images/topic/libraries/architecture/workmanager/how-to/chaining-overwriting-merger-conflict.png?hl=zh_cn" alt="æ­¤å¾æ¾ç¤ºäºä¸ä¸ªä½ä¸å°è¾åºä¼ éç»é¾ä¸­çä¸ä¸ä¸ªä½ä¸ãå¨æ¬ä¾ä¸­ï¼å¶ä¸­æä¸¤ä¸ªä½ä¸ä½¿ç¨åä¸ä¸ªé®çæè¾åºãå æ­¤ï¼ä¸ä¸ä¸ªä½ä¸å°æ¶å°ä¸¤ä¸ªé®å¼å¯¹ï¼å¶ä¸­ä¸ä¸ªå­å¨å²çªçè¾åºä¼è¢«ä¸¢å¼ã" style="zoom:67%;" />



它的优点和缺点其实也很明显 优点在于它很安全 不管怎么样它都不会报错 缺点在于它会丢失数据 如果丢失的这个数据在下面的工作中很重要 那就完蛋了

#### ArrayCreatingInputMerger

创建数组法 它和上面这个就是完全相反 **Overwriting** 采取的是全都扔掉 那么 **ArrayCreating** 采取的就是全都拿来 拿来吧你 它不管你第几个输出 如果发生键冲突 它会直接生成一个数组 把多出来的 **Data** 全都丢进去 是不是感觉它很棒很完美 但是也是有弊端的 弊端在于如果这几个数据的类型必须相等 才能放在数组里 如果不同 那么就会报错

<img src="https://developer.android.google.cn/images/topic/libraries/architecture/workmanager/how-to/chaining-array-merger-conflict.png?hl=zh_cn" alt="æ­¤å¾æ¾ç¤ºäºä¸ä¸ªä½ä¸å°è¾åºä¼ éç»é¾ä¸­çä¸ä¸ä¸ªä½ä¸ãå¨æ¬ä¾ä¸­ï¼å¶ä¸­æä¸¤ä¸ªä½ä¸ä½¿ç¨åä¸ä¸ªé®çæè¾åºãæä¸¤ä¸ªæ°ç»ä¼ éå°ä¸ä¸ä¸ªä½ä¸ï¼æ¯ä¸ªé®å¯¹åºä¸ä¸ªæ°ç»ãå¶ä¸­ä¸ä¸ªæ°ç»æä¸¤ä¸ªæåï¼å ä¸ºæä¸¤ä¸ªè¾åºå·æè¯¥é®ã" style="zoom:67%;" />

但是对于 **workManager** 来说 还是使用后者比较好点 毕竟既然输出了 **Data** 那对于后面的工作应该是有用的

## 总结

大概 **WorkManager** 就讲这么点吧 因为我也没有怎么用过这个组件 时间也不太够 了解到基本用法就好了 还有一些什么高级概念来不及看了 更别提源码 源码真的太费脑子了 没有时间支持我去研究了

## 一堆废话

很基本的步骤 甚至可以不看 

**第一步导包**

```groovy
implementation "androidx.work:work-runtime-ktx:$work_version"
```

 **可选：**

```groovy
// optional - RxJava2 support
    implementation "androidx.work:work-rxjava2:$work_version"

    // optional - GCMNetworkManager support
    implementation "androidx.work:work-gcm:$work_version"

    // optional - Test helpers
    androidTestImplementation "androidx.work:work-testing:$work_version"

    // optional - Multiprocess support
    implementation "androidx.work:work-multiprocess:$work_version"
```

现在更新的是 2.5.0-beta01

先定义一个 **worker** 类 在里面你可以写你要在后台做的事情

```kotlin
class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
       	//在这里写你要在后台做的事情
        
        
        //简单的任务处理
        val name = inputData.getString(INPUT_KEY)
		println("$name started")
		Thread.sleep(3000)
		println("$name finished")
        //返回值为Result 表示成功 也可以返回数据
        return Result.success()//or Result.success(outputData)
    }
}
```

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210729193325563.png" alt="image-20210729193325563" style="zoom:50%;" />

```kotlin
const val Work_name_A = "work A"
const val INPUT_KEY = "input_key"

class MainActivity : AppCompatActivity() {

    //步骤一 获得 workManager 实例
    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            //步骤二
            //获得 请求对象
            val workRequestA = createRequest(Work_name_A)
            
            //步骤三
            //传入队列
            workManager.enqueue(workRequestA)
        }

    }
    private fun createRequest(name: String): OneTimeWorkRequest {
        //添加限制 只有在网络连接时才开始 work
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        return OneTimeWorkRequestBuilder<MyWorker>()
                //添加限制
                .setConstraints(constraints)
                //添加数据
                .setInputData(workDataOf(INPUT_KEY to name)) //or workDataOf(Pair(INPUT_KEY, name))
                .build()
    }
}
```



![image-20210729195416164](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210729195416164.png)

**级联工作**

也就是先完成A再完成B

```kotlin
val workRequestA = createRequest(Work_name_A)
val workRequestB = createRequest(Work_name_B)
//传入队列
workManager.beginWith(workRequestA)
        .then(workRequestB)
        .enqueue()
```

![image-20210729213749068](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210729213749068.png)

**可观察**

在前面的思维导图里写着 工作信息可观察 也就是说我们可以监听每个 **request** 的工作状态和信息

通过 **id** 得到形式为 **LiveData** 的工作信息 得到的是存储在 **LiveData** 的 workInfo 

```kotlin
workManager.getWorkInfoByIdLiveData(workRequestA.id).observe({lifecycle},{
    
})
```

里面的信息

![image-20210729214103788](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210729214103788.png)

```java
public final class WorkInfo {

    private @NonNull UUID mId;
    private @NonNull State mState;
    private @NonNull Data mOutputData;
    private @NonNull Set<String> mTags;
    private @NonNull Data mProgress;
    private int mRunAttemptCount;

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public WorkInfo(
            @NonNull UUID id,
            @NonNull State state,
            @NonNull Data outputData,
            @NonNull List<String> tags,
            @NonNull Data progress,
            int runAttemptCount) {
        mId = id;
        mState = state;
        mOutputData = outputData;
        mTags = new HashSet<>(tags);
        mProgress = progress;
        mRunAttemptCount = runAttemptCount;
    }
```

测试一下吧

```kotlin
workManager.getWorkInfoByIdLiveData(workRequestA.id).observe({lifecycle},{
    if(it.state == WorkInfo.State.SUCCEEDED){
        println("work is success")
    }
})
```

![image-20210729214814523](C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210729214814523.png)

# Material Design

为什么突然冒出个Material Design 因为我写课件要写疯掉了 因为我得重新看以前写的demo 感觉学不到啥新东西 我就想着整体复习下Material Design 熟悉一下使用 还不用看源码 [^高情商：想偷下懒^]我就边写demo 边写课件 双赢！

顺序的话我就按照官方文档里面的顺序来吧 我一看好像有点多 能写多少就写多少吧 看我学的快不快了(狗头)

## 布局

### ConstraintLayout

这个不用讲吧 大家用的最多的就是它了

### MotionLayout

这其实是一个强大的功能 因为它既可以用来做可滑动的动态布局，也可以用来做动画 本来我是想用它来写我的个人项目的 特意用它做了一个动画 结果好像没有个人项目了 好像白做了 嘤嘤嘤

![image-20210725115057088](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725115057088.png)

官方文档思路给的还是比较清晰的 最离谱的是有些地方我需要把中文翻译成原英文看才看的懂 废话不多说直接写步骤

先导包

```kotlin
dependencies {
        implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta1")
    }
```

发现好像有新版本 但是是`alpha`版本的 可能有用法改动 就不更新了

![image-20210725123422288](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725123422288.png)

右键转成`MotionLayout`

![image-20210725123535115](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725123535115.png)

多了一个 start 和 end 也就是表示初始状态和最终状态

![image-20210725124007938](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725124007938.png)

而且在xml包里多了一个 **activity_main_scene** 文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        motion:constraintSetEnd="@+id/end"
        motion:constraintSetStart="@id/start"
        motion:duration="1000">
       <KeyFrameSet>
       </KeyFrameSet>
    </Transition>

    <ConstraintSet android:id="@+id/start">
    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">
    </ConstraintSet>
</MotionScene>
```

先分别介绍各个元素和属性

课上直接看官方文档 我课件里就不复制了 不然废话太多了 还是看官方的说明 但是我会讲下我自己的理解 举个小例子 嘿嘿嘿 我最喜欢小例子了 还有一个图 画的丑见谅

![image-20210725170002680](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725170002680.png)

还是直接看图吧 笔记在课上看

然后我就直接讲代码 因为这个基本上在Design界面就可以执行了  贴代码的话我就贴个旋转木马吧 其他课上看demo 因为MotionLayout 我觉得理解不难 理解了各个属性的关系和作用就好了 剩下的就是吃熟练度的 安卓官网有很多例子的 也提供了xml文件 兄弟们可以去看下 我贴个链接吧

[MotionLayout例子链接](https://developer.android.google.cn/training/constraint-layout/motionlayout/examples?hl=en)

#### 基本操作

#### 直线

pathMotionArc 改变路径曲线

motionInterpolator 改变路径的速度 当然也可以自定义速度 用贝塞尔曲线 但我感觉系统给的够用的 有时间的大佬可以研究 有个网站可以自己调的 我贴一下 [贝塞尔曲线](https://cubic-bezier.com/#.17,.67,.8,.68) 挺好用的！

#### 周期(关键帧)

#### KeyPosition

<Type> 坐标体系 这玩意兄弟们自定义view的时候应该很熟吧

parentRelative 用父布局的坐标体系 也就是左上角为(0,0)

pathRelative 相对路径 也就是它以起点到终点的连线为x轴 以垂直该连线为y轴 起点为(0,0) 终点为(1,1)

deltaRelative 用的就是x轴和y轴 起点为(0,0) 终点为(1,1)

用还是用第一个吧 最简单 

设置一下简单的关键帧

![image-20210725214743769](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725214743769.png)

```xml
<KeyFrameSet>
    <KeyPosition
        motion:motionTarget="@+id/imageView3"
        motion:framePosition="19"
        motion:keyPositionType="parentRelative"
        motion:percentX="0.2" />
    <KeyPosition
        motion:motionTarget="@+id/imageView3"
        motion:framePosition="39"
        motion:keyPositionType="parentRelative"
        motion:percentX="0.8" />
    <KeyPosition
        motion:motionTarget="@+id/imageView3"
        motion:framePosition="60"
        motion:keyPositionType="parentRelative"
        motion:percentX="0.2" />
    <KeyPosition
        motion:motionTarget="@+id/imageView3"
        motion:framePosition="79"
        motion:keyPositionType="parentRelative"
        motion:percentX="0.8" />
</KeyFrameSet>
```

#### KeyCycle

上面这个案例用这个就可以直接实现

![image-20210725214927753](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725214927753.png)

#### KeyAttribute

#### KeyTrigger

在该关键帧时可以调用该View的函数

#### KeyTimeCycle

一直进行

![image-20210725220506827](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725220506827.png)

![image-20210725220516167](https://gitee.com/ye-shenghao/test2/raw/master/static/image-20210725220516167.png)

设置两个就可以看到一直滚动的小企鹅了

#### 归纳每个字段的含义

<MotionScene>

存放所有<Transition>等等的地方

<Transition>

表示动画的过渡

`constraintSetStart` 开始的状态对应的 **id**

`constraintSetEnd` 结束状态的 **id**

`duration` 动画的时间

<OnClick> 点击事件 

`targetId` 为点击控件的 **id** 

`clickAction` 

- **toggle**：来回切换
- **jumpToStart**：瞬间跳转到**start**状态，没有动画
- **jumpToEnd**：瞬间跳转到**end**状态，没有动画
- **transitionToStart**：动画过渡到**start**状态
- **transitionToEnd**：动画过渡到**end**状态

<OnSwipe>

拖动操作

<KeyFrameSet>

关键帧操作

<KeyAttribute>

关键帧属性 

- **alpha** 透明度
- **visibility** 可视
- **elevation**  高度
- **rotation** 旋转
- **rotationX** X轴方向旋转
- **rotationY** Y轴
- **translationX** x轴平移
- **translationY** y轴平移
- **translationZ** z轴平移
- **scaleX** x轴比例缩放
- **scaleY** y轴比例缩放

<KeyPosition>

type

- **parentRelative**：x,y的坐标是相对父容器

<img src="https://img-blog.csdnimg.cn/img_convert/580560bcbdd968968ccb5617c2e873dc.png" alt="parentRelative" style="zoom:50%;" />

- **deltaRelative**：相对于起始点位置，起点的坐标为(0, 0),终点的位置为(1, 1)。x为水平方向，y为垂直方向。

<img src="https://img-blog.csdnimg.cn/img_convert/7938d84ea3190bdea2420b7e584969a8.png" alt="deltaRelative" style="zoom:50%;" />

- **pathRelative**：起点和终点的连线方向是X轴，的坐标是(0, 0)，终点的坐标是(1.0, 0)，然后和X轴垂直的方向为Y轴。

<img src="https://img-blog.csdnimg.cn/img_convert/2d2ac3cea85674a4d1bb477d3ec983d5.png" alt="pathRelative" style="zoom:50%;" />

**pathMotionArc** 可以让控件曲线运动 **startVertical** 优先Y轴方向运动 **startHori** 优先X轴方向运动

<KeyCycle>

让控件在动画时间内做周期运动

**wavePeriod** 总时间内做多少个周期

```xml
<MotionScene 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        motion:constraintSetEnd="@+id/end"
        motion:constraintSetStart="@id/start"
        motion:duration="1000">
       <KeyFrameSet>

           <KeyCycle
               motion:motionTarget="@+id/imageView3"
               motion:framePosition="100"
               motion:wavePeriod="2"
               motion:waveOffset="0"
               android:translationX="100dp" />
       </KeyFrameSet>
        <OnClick />
    </Transition>

    <ConstraintSet android:id="@+id/start">
    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">
        <Constraint
            motion:layout_constraintVertical_bias="0.939"
            android:layout_height="150dp"
            motion:layout_constraintStart_toStartOf="parent"
            motion:layout_constraintTop_toTopOf="parent"
            motion:layout_constraintBottom_toBottomOf="parent"
            motion:layout_constraintHorizontal_bias="0.498"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="150dp"
            android:id="@+id/imageView3" />
    </ConstraintSet>
</MotionScene>
```

<img src="C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20210728220936984.png" alt="image-20210728220936984" style="zoom:50%;" />

<KeyTimeCycle>

不受总时间限制的 **KeyCycle** ，相当于给控件添加了一个属性 让它一直运动

<KeyTrigger>

可触发该控件的方法 比如 **FloatingButton** 的 **hide** 和 **show** 方法

<ConstraintSet>

表示一个状态 开始状态和结束状态





















































