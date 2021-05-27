# 多线程



## 进程与线程

### **进程**

是代码在数据集合上的一次运行活动，是系统进行资源分配和调度的基本单位

### **线程**

是进程的一个执行路径，一个进程中至少有一个线程，进程中的多个线程共享进程的 资源。

虽然系统是把资源分给进程，但是CPU很特殊，是被分配到线程的，所以线程是CPU分配的基本单位。

### 关系

一个进程中有多个线程，多个线程共享进程的堆和方法区资源，但是每个线程有自己的程序计数器和栈区域。

####  **程序计数器**

是一块内存区域，用来记录线程当前要执行的指令地址 。

#### **栈**

用于存储该线程的局部变量，这些局部变量是该线程私有的，除此之外还用来存放线程的调用栈祯。

#### **堆**

是一个进程中最大的一块内存，堆是被进程中的所有线程共享的。

#### **方法区**

### 区别

**进程**：有独立的地址空间，一个进程崩溃后，在保护模式下不会对其它进程产生影响。

**线程**：是一个进程中的不同执行路径。线程有自己的堆栈和局部变量，但线程之间没有单独的地址空间，一个线程死掉就等于整个进程死掉。

### 总结

 简而言之,一个程序至少有一个进程,一个进程至少有一个线程.

2) 线程的划分尺度小于进程，使得多线程程序的并发性高。

3) 另外，进程在执行过程中拥有独立的内存单元，而多个线程共享内存，从而极大地提高了程序的运行效率。

4) 每个独立的线程有一个程序运行的入口、顺序执行序列和程序的出口。但是线程不能够独立执行，必须依存在应用程序中，由应用程序提供多个线程执行控制。

5) 从逻辑角度来看，多线程的意义在于一个应用程序中，有多个执行部分可以同时执行。但操作系统并没有将多个线程看做多个独立的应用，来实现进程的调度和管理以及资源分配。这就是进程和线程的重要区别

### 共享内存==不可见性==

Java 内存模型规定，将所有的变量都存放在==主内==存中，当线程使用变量时，会把主内存里面的变量复制到自己的工作空间或者叫作工作内存，线程读写变量时操作的是自己工作内存中的变量 

### 线程安全

多个线程同时操作共享变量时，会出现线程1更新共享变量的值，但是其他线程获取到的是共享变量没有被更新之前的值。就会导致数据不准确问题

### ==synchronized第一次==

这个内存语义就可以解决共享==变量内存可见性==问题。==进入synchronized块的内存语义是把在synchronized块内使用到的变量从线程的工作内存中清除，这样在synchronized块内使用到该变量时就不会从线程的工作内存中获取，而是直接从主内存中获取==。==退出synchronized块的内存语义是把在synchronized块内对共享变量的修改刷新到主内存。====会造成上下文切换的开销，独占锁，降低并发性

### ==Volatile第一次==

该关键字可以确保对一个变量的更新对其他线程==马上可见==。当一个变量被声明为volatile时，==线程在写入变量时不会把值缓存在寄存器或者其他地方，而是会把值刷新回主内存==。当其他线程读取该共享变量时－，会从主内存重新获取最新值，而不是使用当前线程的工作内存中的值。volatile的内存语义和synchronized有相似之处，具体来说就是，==当线程写入了volatile变量值时就等价于线程退出synchronized同步块（把写入工作内存的变量值同步到主内存），读取volatile变量值时就相当于进入同步块（先清空本地内存变量值，再从主内存获取最新值）。不能保证原子性==



## 线程的实现

### 继承Thread类

### 实现Runable接口

### 实现Callable接口

## Thread类

### 特性

1、线程能被标记为==守护线程==，也可以是==用户线程==

2、每个线程均分配一个name，默认为（Thread-自增数字）的组合

3、每个线程都有==优先级==.高优先级线程优先于低优先级线程执行. 1-10，默认为5

4、main所在的线程组为main，构造线程的时候没有现实的指定线程组，线程组默认和父线程一样

5、当线程中的run()方法代码里面又创建了一个新的线程对象时,==新创建的线程优先级和父线程优先级一样==.

6、==当且仅当父线程为守护线程时,新创建的线程才会是守护线程.==

7、当JVM启动时,通常会有==唯一==的一个==非守护线程==(这一线程用于调用指定类的main()方法)

JVM会持续执行线程直到下面情况某一个发生为止:

1）类运行时exit()方法被调用 且 安全机制允许此exit()方法的调用.

2）所有非守护类型的线程均已经==终止==,or run()方法调用==返回==or在run()方法外部抛出了一些==可传播性的异常==.



### 线程状态

```java
public` `enum` `State {
    ``NEW,
    ``RUNNABLE,
    ``BLOCKED,
    ``WAITING,
    ``TIMED_WAITING,
    ``TERMINATED;
  ``}
```

![img](https://img2018.cnblogs.com/blog/1271254/201907/1271254-20190706223130009-955301871.png)



#### NEW

状态是指线程刚创建, 尚未启动

#### **RUNNABLE**

状态是线程正在正常运行中, 当然可能会有某种耗时计算/IO等待的操作/CPU时间片切换等, 这个状态下发生的==等待一般是其他系统资源, 而不是锁, Sleep等==

#### **BLOCKED**

这个状态下, 是在多个线程有同步操作的场景, 比如正在等待另一个线程的synchronized 块的执行释放, 或者可重入的 synchronized块里别人调用wait() 方法, 也就是这里是线程在等待进入==临界区==.

#### **WAITING**

这个状态下是指线程拥有了某个锁之后, 调用了他的wait方法, 等待其他线程/锁拥有者调用 notify / notifyAll  一遍该线程可以继续下一步操作, 这里要区分 BLOCKED 和 WATING 的区别, 一个是在临界点外面等待进入,  一个是在理解点里面wait等待别人notify, ==线程调用了join方法 join了另外的线程的时候, 也会进入WAITING状态==,  等待被他join的线程执行结束

#### **TIMED_WAITING**

这个状态就是有限的(时间限制)的WAITING, 一般出现在调用wait(long), join(long)等情况下, 另外一个线程sleep后, 也会进入TIMED_WAITING状态

#### **TERMINATED**

这个状态下表示 该线程的run方法已经执行完毕了, 基本上就等于死亡了(当时如果线程被持久持有, 可能不会被回收)

在很多文章中都写了running状态，其实源码里面只有六种的，当自己写一个线程通过while一直保持执行状态，然后使用jconsole工具去查看线程的状态，确实是Runable状态![img](https://img2018.cnblogs.com/blog/1271254/201907/1271254-20190706223523665-422170561.png)

其实我们可以理解为两种状态，一个是==running==，表示正在执行，一个是==runable==，表示==准备就绪了==，只是在==等待其他的系统资源==。然后我们就可以理解如下图

![img](https://img2018.cnblogs.com/blog/1271254/201907/1271254-20190706223554676-1347186915.png)





### 方法

#### init方法

```java
/**
     * Initializes a Thread.实例化一个线程
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     * @param acc the AccessControlContext to inherit, or
     *            AccessController.getContext() if null
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;

        Thread parent = currentThread();
        // Android-removed: SecurityManager stubbed out on Android
        // SecurityManager security = System.getSecurityManager();
        //如果所属线程组为null 
        if (g == null) {
            // Android-changed: SecurityManager stubbed out on Android
            /*
            /* Determine if it's an applet or not *

            /* If there is a security manager, ask the security manager
             //如果有安全管理,查询安全管理需要做的工作
               what to do. *
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. *
               //如果安全管理在线程所属父线程组的问题上没有什么强制的要求
            if (g == null) {
            */
                g = parent.getThreadGroup();
            // }
        }

        // Android-removed: SecurityManager stubbed out on Android
        /*
        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. *
           //无论所属线程组是否显示传入,都要进行检查访问.
        g.checkAccess();

        /*
         * Do we have the required permissions?
         *
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }
        */

        g.addUnstarted();

        this.group = g;
        //如果父线程为守护线程,则此线程也被 设置为守护线程.
        this.daemon = parent.isDaemon();
        //获取父进程的优先级
        this.priority = parent.getPriority();
        // Android-changed: Moved into init2(Thread) helper method.
        /*
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        */
        this.target = target;
        // Android-removed: The priority parameter is unchecked on Android.
        // It is unclear why this is not being done (b/80180276).
        // setPriority(priority);
        // Android-changed: Moved into init2(Thread) helper method.
        // if (parent.inheritableThreadLocals != null)
        //     this.inheritableThreadLocals =
        //         ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        init2(parent);

        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        tid = nextThreadID();
    }
```

#### **构造方法**

所有的构造方法都是调用init()方法

#### start()

```java
/**
 * Causes this thread to begin execution; the Java Virtual Machine
 * calls the <code>run</code> method of this thread.
 * <p>
 * The result is that two threads are running concurrently: the
 * current thread (which returns from the call to the
 * <code>start</code> method) and the other thread (which executes its
 * <code>run</code> method).
 * <p>
 * It is never legal to start a thread more than once.
 * In particular, a thread may not be restarted once it has completed
 * execution.
 *
 * @exception  IllegalThreadStateException  if the thread was already
 *               started.
 * @see        #run()
 * @see        #stop()
 */
public synchronized void start() {
    /**
     * This method is not invoked for the main method thread or "system"
     * group threads created/set up by the VM. Any new functionality added
     * to this method in the future may have to also be added to the VM.
     *
     * A zero status value corresponds to state "NEW".
     */
    // Android-changed: Replace unused threadStatus field with started field.
    // The threadStatus field is unused on Android.
    if (started)  线程不能够重复start
        throw new IllegalThreadStateException();

    /* Notify the group that this thread is about to be started
     * so that it can be added to the group's list of threads
     * and the group's unstarted count can be decremented. */
    group.add(this);

    // Android-changed: Use field instead of local variable.
    // It is necessary to remember the state of this across calls to this method so that it
    // can throw an IllegalThreadStateException if this method is called on an already
    // started thread.
    started = false;
    try {
        // Android-changed: Use Android specific nativeCreate() method to create/start thread.
        // start0();   //本地方法
        nativeCreate(this, stackSize, daemon);
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {
            /* do nothing. If start0 threw a Throwable then
              it will be passed up the call stack */
        }
    }
}
```

1.启动当前线程 2.调用线程实例中的run方法

#### run()

一个线程实例中，需要重写Runnable接口中的run方法。

然后调用实例.run方法相当于直接在本线程中执行run方法中的内容



#### yield()

　是一个本地方法，提示线程调度器当前线程愿意放弃当前CPU的使用。如果当前资源不紧张，调度器可以忽略这个提示。本质上线程状态一直是RUNNABLE,但是我可以理解为RUNNABLE到RUNNING的转换

主动释放当前所在的线程的执行权

给cpu调度，比较容易让给别的进程，但是也存在自己继续执行的情况

#### join()

```java
/**
     * Waits at most {@code millis} milliseconds for this thread to
     * die. A timeout of {@code 0} means to wait forever.
     *
     * <p> This implementation uses a loop of {@code this.wait} calls
     * conditioned on {@code this.isAlive}. As a thread terminates the
     * {@code this.notifyAll} method is invoked. It is recommended that
     * applications not use {@code wait}, {@code notify}, or
     * {@code notifyAll} on {@code Thread} instances.
     *
     * @param  millis
     *         the time to wait in milliseconds
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    // BEGIN Android-changed: Synchronize on separate lock object not this Thread.
    // public final synchronized void join(long millis)
/**
     * 最多等待参数millis(ms)时长当前线程就会死亡.参数为0时则要持续等待.
     * 此方法在实现上:循环调用以this.isAlive()方法为条件的wait()方法.
     * 当线程终止时notifyAll()方法会被调用.
     * 建议应用程序不要在线程实例上使用wait,notify,notifyAll方法.
     */
    public final void join(long millis)
    throws InterruptedException {
        synchronized(lock) {
        long base = System.currentTimeMillis();
        long now = 0;
  //如果等待时间<0,则抛出异常
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }
//如果等待时间为0
        if (millis == 0) {
            while (isAlive()) {
                lock.wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                lock.wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
        }
    }
 // END Android-changed: Synchronize on separate lock object not this Thread.

    /**
     * Waits at most {@code millis} milliseconds plus
     * {@code nanos} nanoseconds for this thread to die.
     *
     * <p> This implementation uses a loop of {@code this.wait} calls
     * conditioned on {@code this.isAlive}. As a thread terminates the
     * {@code this.notifyAll} method is invoked. It is recommended that
     * applications not use {@code wait}, {@code notify}, or
     * {@code notifyAll} on {@code Thread} instances.
     *
     * @param  millis
     *         the time to wait in milliseconds
     *
     * @param  nanos
     *         {@code 0-999999} additional nanoseconds to wait
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative, or the value
     *          of {@code nanos} is not in the range {@code 0-999999}
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    // BEGIN Android-changed: Synchronize on separate lock object not this Thread.
    // public final synchronized void join(long millis, int nanos)
//等待时间单位为纳秒,其它解释都和上面方法一样
    public final void join(long millis, int nanos)
    throws InterruptedException {

        synchronized(lock) {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        join(millis);
        }
    }
    // END Android-changed: Synchronize on separate lock object not this Thread.

    /**
     * Waits for this thread to die.
     *
     * <p> An invocation of this method behaves in exactly the same
     * way as the invocation
     *
     * <blockquote>
     * {@linkplain #join(long) join}{@code (0)}
     * </blockquote>
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
 //方法功能:等待一直到线程死亡.
    public final void join() throws InterruptedException {
        join(0);
    }
```

在线程中插入执行另一个线程，该线程被阻塞，直到插入执行的线程完全执行完毕以后，该线程

才继续执行下去。

　join某个线程A，会使得线程B进入等待，知道线程A结束，或者到达给定的时间，那么期间线程B处于BLOCKED的状态，而不是线程A

注：与run()方法不同的是，这样执行实际上是开了一个新的线程。而直接调用run()方法会执行在

本进程。

#### stop()

强行停止进程

#### isAlive()

判断当前线程是否存活

#### join和中断

推荐用标记中断

```java
interrupt();//中断线程,只是作了一个中断标记，用于测试interrupt方法
if(Thread.interrupted()){//测试中断状态，此方法会把中断状态清除
```

#### 优先级和守护进程

\```//优先级高可以提高该线程抢点CPU时间片的概率大`

```java
 ``//线程可以分成守护线程和 用户线程，当进程中没有用户线程时，JVM会退出
 t.setDaemon(true);//把线程设置为守护线程
1. 在后台默默地完成一些系统性的服务，比如垃圾回收线程、JIT线程就可以理解为守护线程
2. 当一个Java应用内，所有非守护进程都结束时，Java虚拟机就会自然退出。
```

```java
Thread类中有3个变量定义了线程优先级。
public final static int MIN_PRIORITY = 1;
public final static int NORM_PRIORITY = 5;
public final static int MAX_PRIORITY = 10;
```





#### sleep

```java
 此方法会引起当前执行线程sleep(临时停止执行)指定毫秒数.
   ``* 此方法的调用不会引起当前线程放弃任何监听器(monitor)的所有权(ownership).
```

sleep方法，有一个重载方法，sleep方法会释放cpu的时间片，但是不会释放锁，调用sleep()之后从RUNNABLE状态转为TIMED_WAITING状态

#### wait

```java
public final native void wait(long timeout) throws InterruptedException; //本地方法 参数为毫秒

public final void wait(long timeout, int nanos) throws InterruptedException {//参数为毫秒和纳秒
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }
 
        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }
 
        if (nanos > 0) {
            timeout++;
        }
 
        wait(timeout);
    }

    public final void wait() throws InterruptedException {
        wait(0);
    }
```

可见wait()和wait(long timeout, int nanos)都在在内部调用了wait(long timeout)方法。 
下面主要是说说wait(long timeout)方法 
wait方法会引起当前线程阻塞，直到另外一个线程在对应的对象上调用notify或者notifyAll()方法，或者达到了方法参数中指定的时间。 

==调用wait方法的当前线程一定要拥有对象的监视器==
wait方法会把当前线程T放置在对应的object上的等待队列中，在这个==对象==上的==所有同步请求==都不会得到响应。线程调度将不会调用线程T，在以下==四件事==发生之前，线程T会被唤醒（线程T是在其代码中调用wait方法的那个线程）

1、当其他的线程在对应的对象上调用notify方法，而在此对象的对应的等待队列中将会任意选择一个线程进行唤醒。
2、其他的线程在此对象上调用了notifyAll方法
3、其他的线程调用了interrupt方法来中断线程T
4、等待的时间已经超过了wait中指定的时间。如果参数timeout的值为0，不是指真实的等待时间是0，而是线程等待直到被另外一个线程唤醒为止。 





被唤醒的线程T==会被从对象的等待队列中移除==并且重新能够被==线程调度器调度==。之后，==线程T会像平常一样跟其他的线程竞争获取对象上的锁==；一旦线程T获得了此对象上的锁，那么在此对象上的所有同步请求都会==恢复==到之前的状态，也就是恢复到wait被调用的情况下。然后线程T从wait方法的调用中返回。因此，当从wait方法返回时，对象的状态以及==线程T的状态跟wait方法被调用的时候一样==。 
线程在没有被唤醒，中断或者时间耗尽的情况下仍然能够被唤醒，这叫做**伪唤醒**。虽然在实际中，这种情况很少发生，但是程序一定要测试这个能够唤醒线程的条件，并且在条件不满足时，线程继续等待。换言之，wait操作总是出现在循环中，就像下面这样：

```java
synchronized``(对象){
  ``while``(条件不满足){
   ``对象.wait();
 ``}
 ``对应的逻辑处理
}
```

 如果当前的线程被其他的线程在当前线程等待之前或者正在等待时调用了interrupt()中断了，那么会抛出InterruptedExcaption异常。直到这个对象上面的锁状态恢复到上面描述的状态以前，这个异常是不会抛出的。 
  == ==要注意的是，wait方法把当前线程放置到这个对象的等待队列中，解锁也仅仅是在这个对象上；==当前线程在其他对象上面上的锁在当前线程等待的过程中仍然==持有其他对象的锁==。 
 ==这个方法应该仅仅被持有对象监视器的线程调用==
  wait(long timeout, int nanos)方法的实现中只要nanos大于0，那么timeout时间就加上一毫秒，主要是更精确的控制时间，其他的跟wait(long timeout)一样



#### **notify**

通知可能等待==该对象的对象锁==的其他线程。由JVM(==与优先级无关==)随机挑选一个处于wait状态的线程。 
 在调用notify()之前，==线程必须获得该对象的对象级别锁==
 ==执行完notify()方法后，不会马上释放锁，要直到退出synchronized代码块，当前线程才会释放锁==
 notify()一次只随机通知一个线程进行唤醒

#### **notifyAll()**

和notify()差不多，只不过是使所有正在==等待池==中等待同一共享资源的全部线程从等待状态退出，进入可运行状态 
==让它们竞争对象的锁==，==只有获得锁的线程才能进入就绪状态== 
每个锁对象有两个队列：就绪队列和阻塞队列 
\- ==就绪队列==：存储将要获得锁的线程 
\- ==阻塞队列==：存储被阻塞的线程



# 泛型

## 泛型类



//此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
//在实例化泛型类时，必须指定T的具体类型
public class Generic<T>{ 
    //key这个成员变量的类型为T,T的类型由外部指定  
    private T key;

```java
public Generic(T key) { //泛型构造方法形参key的类型也为T，T的类型由外部指定
    this.key = key;
}

public T getKey(){ //泛型方法getKey的返回值类型为T，T的类型由外部指定
    return key;
}
}
```
```java
//泛型的类型参数只能是类类型（包括自定义类），不能是简单类型
//传入的实参类型需与泛型的类型参数类型相同，即为Integer.
Generic<Integer> genericInteger = new Generic<Integer>(123456);

//传入的实参类型需与泛型的类型参数类型相同，即为String.
Generic<String> genericString = new Generic<String>("key_vlaue");
Log.d("泛型测试","key is " + genericInteger.getKey());
Log.d("泛型测试","key is " + genericString.getKey());
```



==定义的泛型类，就一定要传入泛型类型实参么？并不是这样，在使用泛型的时候如果传入泛型实参，则会根据传入的泛型实参做相应的限制，此时泛型才会起到本应起到的限制作用。如果不传入泛型类型实参的话，在泛型类中使用泛型的方法或成员变量定义的类型可以为任何的类型。==

```java
Generic generic = new Generic("111111");
Generic generic1 = new Generic(4444);
Generic generic2 = new Generic(55.55);
Generic generic3 = new Generic(false);

Log.d("泛型测试","key is " + generic.getKey());
Log.d("泛型测试","key is " + generic1.getKey());
Log.d("泛型测试","key is " + generic2.getKey());
Log.d("泛型测试","key is " + generic3.getKey());


D/泛型测试: key is 111111
D/泛型测试: key is 4444
D/泛型测试: key is 55.55
D/泛型测试: key is false
```

- 泛型的类型参数只能是类类型，不能是简单类型。
- 不能对确切的泛型类型使用instanceof操作。如下面的操作是非法的，编译时会出错。

## 泛型接口

泛型接口与泛型类的定义及使用基本相同。泛型接口常被用在各种类的生产器中，可以看一个例子：

```java
//定义一个泛型接口
public interface Generator<T> {
    public T next();
}
```

当实现泛型接口的类，未传入泛型实参时：

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```java
/**
 * 未传入泛型实参时，与泛型类的定义相同，在声明类的时候，需将泛型的声明也一起加到类中
 * 即：class FruitGenerator<T> implements Generator<T>{
 * 如果不声明泛型，如：class FruitGenerator implements Generator<T>，编译器会报错："Unknown class"
 */
class FruitGenerator<T> implements Generator<T>{
    @Override
    public T next() {
        return null;
    }
}
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

当实现泛型接口的类，传入泛型实参时：

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```java
/**
 * 传入泛型实参时：
 * 定义一个生产器实现这个接口,虽然我们只创建了一个泛型接口Generator<T>
 * 但是我们可以为T传入无数个实参，形成无数种类型的Generator接口。
 * 在实现类实现泛型接口时，如已将泛型类型传入实参类型，则所有使用泛型的地方都要替换成传入的实参类型
 * 即：Generator<T>，public T next();中的的T都要替换成传入的String类型。
 */
public class FruitGenerator implements Generator<String> {

    private String[] fruits = new String[]{"Apple", "Banana", "Pear"};

    @Override
    public String next() {
        Random rand = new Random();
        return fruits[rand.nextInt(3)];
    }
}
```













## 泛型方法

泛型类，是在实例化类的时候指明泛型的具体类型；泛型方法，是在调用方法的时候指明泛型的具体类型 



```java
/**
 * 泛型方法的基本介绍
 * @param tClass 传入的泛型实参
 * @return T 返回值为T类型
 * 说明：
 *     1）public 与 返回值中间<T>非常重要，可以理解为声明此方法为泛型方法。
 *     2）只有声明了<T>的方法才是泛型方法，泛型类中的使用了泛型的成员方法并不是泛型方法。
 *     3）<T>表明该方法将使用泛型类型T，此时才可以在方法中使用泛型类型T。
 *     4）与泛型类的定义一样，此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型。
 */
public <T> T genericMethod(Class<T> tClass)throws InstantiationException ,
  IllegalAccessException{
        T instance = tClass.newInstance();
        return instance;
}

Object obj = genericMethod(Class.forName("com.test.test"));
```



### 泛型方法的基本用法



```java
   public class GenericTest {
   //这个类是个泛型类，在上面已经介绍过
   public class Generic<T>{     
        private T key;

public Generic(T key) {
        this.key = key;
    }

    //我想说的其实是这个，虽然在方法中使用了泛型，但是这并不是一个泛型方法。
    //这只是类中一个普通的成员方法，只不过他的返回值是在声明泛型类已经声明过的泛型。
    //所以在这个方法中才可以继续使用 T 这个泛型。
    public T getKey(){
        return key;
    }

    /**
     * 这个方法显然是有问题的，在编译器会给我们提示这样的错误信息"cannot reslove symbol E"
     * 因为在类的声明中并未声明泛型E，所以在使用E做形参和返回值类型时，编译器会无法识别。
    public E setKey(E key){
         this.key = keu
    }
    */
}

/** 
 * 这才是一个真正的泛型方法。
 * 首先在public与返回值之间的<T>必不可少，这表明这是一个泛型方法，并且声明了一个泛型T
 * 这个T可以出现在这个泛型方法的任意位置.
 * 泛型的数量也可以为任意多个 
 *    如：public <T,K> K showKeyName(Generic<T> container){
 *        ...
 *        }
 */
public <T> T showKeyName(Generic<T> container){
    System.out.println("container key :" + container.getKey());
    //当然这个例子举的不太合适，只是为了说明泛型方法的特性。
    T test = container.getKey();
    return test;
}

//这也不是一个泛型方法，这就是一个普通的方法，只是使用了Generic<Number>这个泛型类做形参而已。
public void showKeyValue1(Generic<Number> obj){
    Log.d("泛型测试","key value is " + obj.getKey());
}

//这也不是一个泛型方法，这也是一个普通的方法，只不过使用了泛型通配符?
//同时这也印证了泛型通配符章节所描述的，?是一种类型实参，可以看做为Number等所有类的父类
public void showKeyValue2(Generic<?> obj){
    Log.d("泛型测试","key value is " + obj.getKey());
}

 /**
 * 这个方法是有问题的，编译器会为我们提示错误信息："UnKnown class 'E' "
 * 虽然我们声明了<T>,也表明了这是一个可以处理泛型的类型的泛型方法。
 * 但是只声明了泛型类型T，并未声明泛型类型E，因此编译器并不知道该如何处理E这个类型。
public <T> T showKeyName(Generic<E> container){
    ...
}  
*/

/**
 * 这个方法也是有问题的，编译器会为我们提示错误信息："UnKnown class 'T' "
 * 对于编译器来说T这个类型并未项目中声明过，因此编译也不知道该如何编译这个类。
 * 所以这也不是一个正确的泛型方法声明。
public void showkey(T genericObj){

}
*/

public static void main(String[] args) {
    
}
}
```



### 类中的泛型方法



```java
public class GenericFruit {
    class Fruit{
        @Override
        public String toString() {
            return "fruit";
        }
    }


class Apple extends Fruit{
    @Override
    public String toString() {
        return "apple";
    }
}

class Person{
    @Override
    public String toString() {
        return "Person";
    }
}

class GenerateTest<T>{
    public void show_1(T t){
        System.out.println(t.toString());
    }

    //在泛型类中声明了一个泛型方法，使用泛型E，这种泛型E可以为任意类型。可以类型与T相同，也可以不同。
    //由于泛型方法在声明的时候会声明泛型<E>，因此即使在泛型类中并未声明泛型，编译器也能够正确识别泛型方法中识别的泛型。
    public <E> void show_3(E t){
        System.out.println(t.toString());
    }

    //在泛型类中声明了一个泛型方法，使用泛型T，注意这个T是一种全新的类型，可以与泛型类中声明的T不是同一种类型。
    public <T> void show_2(T t){
        System.out.println(t.toString());
    }
}

public static void main(String[] args) {
    Apple apple = new Apple();
    Person person = new Person();

    GenerateTest<Fruit> generateTest = new GenerateTest<Fruit>();
    //apple是Fruit的子类，所以这里可以
    generateTest.show_1(apple);
    //编译器会报错，因为泛型类型实参指定的是Fruit，而传入的实参类是Person
    //generateTest.show_1(person);

    //使用这两个方法都可以成功
    generateTest.show_2(apple);
    generateTest.show_2(person);

    //使用这两个方法也都可以成功
    generateTest.show_3(apple);
    generateTest.show_3(person);
}
```
### 泛型方法与可变参数

再看一个泛型方法和可变参数的例子：

```java
public <T> void printMsg( T... args){
    for(T t : args){
        Log.d("泛型测试","t is " + t);
    }
}
printMsg("111",222,"aaaa","2323.4",55.55);
```

### 静态方法与泛型

静态方法有一种情况需要注意一下，那就是在类中的==静态方法使用泛型==：静态方法无法访问类上定义的泛型；如果静态方法操作的引用数据类型不确定的时候，==必须要将泛型定义在方法上。==

即：如果静态方法要使用泛型的话，必须将静态方法也定义成泛型方法 。

```java

public class StaticGenerator<T> {
    ....
    ....
    /**
     * 如果在类中定义使用泛型的静态方法，需要添加额外的泛型声明（将这个方法定义成泛型方法）
     * 即使静态方法要使用泛型类中已经声明过的泛型也不可以。
     * 如：public static void show(T t){..},此时编译器会提示错误信息：
          "StaticGenerator cannot be refrenced from static context"
     */
    public static <T> void show(T t){

    }
}
```



### 泛型方法总结



泛型方法能使方法独立于类而产生变化，以下是一个基本的指导原则：

```java
无论何时，如果你能做到，你就该尽量使用泛型方法。也就是说，如果使用泛型方法将整个类泛型化，

那么就应该使用泛型方法。另外对于一个static的方法而已，无法访问泛型类型的参数。

所以如果static方法要使用泛型能力，就必须使其成为泛型方法。
```

## 泛型上下边界

在使用泛型的时候，我们还可以为传入的泛型类型实参进行上下边界的限制，如：类型实参只准传入某种类型的父类或某种类型的子类。

为泛型添加上边界，即传入的类型实参必须是指定类型的子类型

**<? super E>通配符的下限**

**List<? extends Animal>** **通配符的上限**

```java
public void showKeyValue1(Generic<? extends Number> obj){
    Log.d("泛型测试","key value is " + obj.getKey());
}
```



```java
Generic<String> generic1 = new Generic<String>("11111");
Generic<Integer> generic2 = new Generic<Integer>(2222);
Generic<Float> generic3 = new Generic<Float>(2.4f);
Generic<Double> generic4 = new Generic<Double>(2.56);

//这一行代码编译器会提示错误，因为String类型并不是Number类型的子类
//showKeyValue1(generic1);

showKeyValue1(generic2);
showKeyValue1(generic3);
showKeyValue1(generic4);
```

## 泛型数组

经过查看sun的说明文档，在java中是”不能创建一个确切的泛型类型的数组”的。

也就是说下面的这个例子是不可以的：

```
List<String>[] ls = new ArrayList<String>[10];  
```

而使用通配符创建泛型数组是可以的，如下面这个例子：

```
List<?>[] ls = new ArrayList<?>[10]; 
```

这样也是可以的：

```
List<String>[] ls = new ArrayList[10];
```

下面使用[Sun](http://docs.oracle.com/javase/tutorial/extra/generics/fineprint.html)[的一篇文档](http://docs.oracle.com/javase/tutorial/extra/generics/fineprint.html)的一个例子来说明这个问题：

```java
List<String>[] lsa = new List<String>[10]; // Not really allowed.    
Object o = lsa;    
Object[] oa = (Object[]) o;    
List<Integer> li = new ArrayList<Integer>();    
li.add(new Integer(3));    
oa[1] = li; // Unsound, but passes run time store check    
String s = lsa[1].get(0); // Run-time error: ClassCastException.
```



这种情况下，由于JVM泛型的擦除机制，在运行时JVM是不知道泛型信息的，所以可以给oa[1]赋上一个ArrayList而不会出现异常，但是在取出数据的时候却要做一次类型转换，所以就会出现ClassCastException，如果可以进行泛型数组的声明，上面说的这种情况在编译期将不会出现任何的警告和错误，只有在运行时才会出错。

而对泛型数组的声明进行限制，对于这样的情况，可以在编译期提示代码有类型安全问题，比没有任何提示要强很多。



下面采用通配符的方式是被允许的:数组的类型不可以是类型变量，除非是采用通配符的方式，因为对于通配符的方式，最后取出数据是要做显式的类型转换的。



```java
List<?>[] lsa = new List<?>[10]; // OK, array of unbounded wildcard type.    
Object o = lsa;    
Object[] oa = (Object[]) o;    
List<Integer> li = new ArrayList<Integer>();    
li.add(new Integer(3));    
oa[1] = li; // Correct.    
Integer i = (Integer) lsa[1].get(0); // OK
```



































# 集合

![常用集合大纲](https://img-blog.csdn.net/20180803184611883?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![这里写图片描述](https://img-blog.csdn.net/20180803195348216?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![Collection集合大纲](https://img-blog.csdn.net/20180803184706534?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 1.集合和数组的区别：

![这里写图片描述](https://img-blog.csdn.net/20180803193134355?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

##  Collection集合的方法：

![这里写图片描述](https://img-blog.csdn.net/20180803193423722?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## 常用集合的分类

Collection 接口的接口 对象的集合（单列集合） 
├——-List 接口：元素按进入先后有序保存，可重复 
│—————-├ LinkedList 接口实现类， 链表， 插入删除， 没有同步， 线程不安全 
│—————-├ ArrayList 接口实现类， 数组， 随机访问， 没有同步， 线程不安全 
│—————-└ Vector 接口实现类 数组， 同步， 线程安全 
│ ———————-└ Stack 是Vector类的实现类 
└——-Set 接口： 仅接收一次，不可重复，并做内部排序 
├—————-└HashSet 使用hash表（数组）存储元素 
│————————└ LinkedHashSet 链表维护元素的插入次序 
└ —————-TreeSet 底层实现为二叉树，元素排好序

Map 接口 键值对的集合 （双列集合） 
├———Hashtable 接口实现类， 同步， 线程安全 
├———HashMap 接口实现类 ，没有同步， 线程不安全- 
│—————–├ LinkedHashMap 双向链表和哈希表实现 
│—————–└ WeakHashMap 
├ ——–TreeMap 红黑树对所有的key进行排序 

└———IdentifyHashMap

## List和Set集合详解

### list和set的区别



![这里写图片描述](https://img-blog.csdn.net/20180803201610610?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### List

1）==ArrayList==：底层数据结构是数组，查询快，增删慢，线程不安全，效率高，可以存储重复元素 
（2）==LinkedList==底层数据结构是链表，查询慢，增删快，线程不安全，效率高，可以存储重复元素 
（3）==Vector==:底层数据结构是数组，查询快，增删慢，线程安全，效率低，可以存储重复元素

------------------------------------------------
![这里写图片描述](https://img-blog.csdn.net/20180803201736883?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZlaXlhbmFmZmVjdGlvbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### Set

(1)HashSet底层数据结构采用哈希表实现，元素无序且唯一，线程不安全，效率高，可以存储null元素，元素的==唯一性==是靠所存储元素类型==是否重写hashCode()==和==equals()方法来保证的==，如果没有重写这两个方法，则==无法保证元素的唯一性==。 
具体实现唯一性的比较过程：存储元素首先会使用hash()算法函数生成一个int类型hashCode散列值，然后已经的所存储的元素的hashCode值比较，如果hashCode不相等，则所存储的两个对象一定不相等，此时存储当前的新的hashCode值处的元素对象；如果==hashCode相等==，存储元素的对象还是不一定相等，此时==会调用equals()==方法判断两个对象的内容是否相等，如果内容相等，那么就是同一个对象，无需存储；如果比较的内容不相等，那么就是不同的对象，就该存储了，此时就要采用==哈希的解决地址冲突算法==，在当前==hashCode值处类似一个新的链表==， 在==同一个hashCode值的后面存储存储不同的对象==，这样就保证了元素的唯一性。 
Set的实现类的集合对象中不能够有重复元素，HashSet也一样他是使用了一种标识来确定元素的不重复，HashSet用一种算法来保证HashSet中的元素是不重复的，==HashSet采用哈希算法，底层用数组存储数据。默认初始化容量16，加载因子0.75==。 
Object类中的hashCode()的方法是所有子类都会继承这个方法，这个方法会用Hash算法算出一个Hash（哈希）码值返回，HashSet会用Hash码值去和数组长度取模， 模（这个模就是对象要存放在数组中的位置）相同时才会判断数组中的元素和要加入的对象的内容是否相同，如果不同才会添加进去。 
Hash算法是一种散列算法。 
Set hs=new HashSet();

hs.add(o); 
| 
o.hashCode(); 
| 
o%当前总容量 (0–15) 
| 
| 不发生冲突 
是否发生冲突—————–直接存放 
| 
| 发生冲突 
| 假（不相等） 
o1.equals(o2)——————-找一个空位添加 
| 
| 是（相等） 
不添加 
覆盖hashCode()方法的原则： 
1、一定要让那些我们认为相同的对象返回相同的hashCode值 
2、尽量让那些我们认为不同的对象返回不同的hashCode值，否则，就会增加冲突的概率。 
3、尽量的让hashCode值散列开（两值用异或运算可使结果的范围更广） 
HashSet 的实现比较简单，相关HashSet的操作，基本上都是直接调用底层HashMap的相关方法来完成，我们应该为保存到HashSet中的对象覆盖hashCode()和equals()，因为再将对象加入到HashSet中时，会首先调用hashCode方法计算出对象的hash值，接着根据此hash值调用HashMap中的hash方法，得到的值& (length-1)得到该对象在hashMap的transient Entry[] table中的保存位置的索引，接着找到数组中该索引位置保存的对象，并调用equals方法比较这两个对象是否相等，如果相等则不添加，注意：所以要存入HashSet的集合对象中的自定义类必须覆盖hashCode(),equals()两个方法，才能保证集合中元素不重复。在覆盖equals()和hashCode()方法时， 要使相同对象的hashCode()方法返回相同值，覆盖equals()方法再判断其内容。为了保证效率，所以在覆盖hashCode()方法时， 也要尽量使不同对象尽量返回不同的Hash码值。

如果数组中的元素和要加入的对象的hashCode()返回了相同的Hash值（相同对象）,才会用equals()方法来判断两个对象的内容是否相同。

(2)LinkedHashSet底层数据结构采用链表和哈希表共同实现，链表保证了元素的顺序与存储顺序一致，哈希表保证了元素的唯一性。线程不安全，效率高。 
(3)TreeSet底层数据结构采用二叉树来实现，元素唯一且已经排好序；唯一性同样需要重写hashCode和equals()方法，二叉树结构保证了元素的有序性。根据构造方法不同，分为自然排序（无参构造）和比较器排序（有参构造），自然排序要求元素必须实现Compareable接口，并重写里面的compareTo()方法，元素通过比较返回的int值来判断排序序列，返回0说明两个对象相同，不需要存储；比较器排需要在TreeSet初始化是时候传入一个实现Comparator接口的比较器对象，或者采用匿名内部类的方式new一个Comparator对象，重写里面的compare()方法； 
（4）小结：Set具有与Collection完全一样的接口，因此没有任何额外的功能，不像前面有两个不同的List。实际上Set就是Collection,只 是行为不同。(这是继承与多态思想的典型应用：表现不同的行为。)Set不保存重复的元素。 
Set 存入Set的每个元素都必须是唯一的，因为Set不保存重复元素。加入Set的元素必须定义equals()方法以确保对象的唯一性。Set与Collection有完全一样的接口。Set接口不保证维护元素的次序。



















# 反射

## 概述

JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性；这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制。=

要想解剖一个类,必须先要获取到该==类的字节码文件对象==。而解剖使用的就是Class类中的方法.所以先要获取到每一个字节码文件对应的Class类型的对象.

反射就是把java类中的各种成分==映射==成一个个的Java对象

例如：一个类有：成员变量、方法、构造方法、包等等信息，利用反射技术可以对一个类进行解剖，把个个组成部分映射成一个个==对象。==

   （其实：一个类中这些成员方法、构造方法、在加入类中都有一个类来描述）

如图是类的正常加载过程：反射的原理在与class对象。

![img](https://img-blog.csdn.net/20170513133210763)



## Class类在java中的api详解

如何阅读java中的api详见java基础之——String字符串处理

![img](https://img-blog.csdn.net/20170513135521667)

`Class` 类的实例表示正在运行的 Java 应用程序中的类和接口。也就是jvm中有N多的实例每个类都有该Class对象。（包括基本数据类型）

`Class` 没有公共构造方法。`Class` 对象是在加载类时由 Java 虚拟机以及通过调用类加载器中的`defineClass` 方法自动构造的。也就是这不需要我们自己去处理创建，JVM已经帮我们创建好了。

 没有公共的构造方法，方法共有64个太多了。下面用到哪个就详解哪个吧![img](https://img-blog.csdn.net/20170513144141409)

## 反射的使用

### 获取class对象

```java
//第一种方式获取Class对象  
        Student stu1 = new Student();//这一new 产生一个Student对象，一个Class对象。
        Class stuClass = stu1.getClass();//获取Class对象
        System.out.println(stuClass.getName());
        
        //第二种方式获取Class对象
        Class stuClass2 = Student.class;
        System.out.println(stuClass == stuClass2);//判断第一种方式获取的Class对象和第二种方式获取的是否是同一个
        
        //第三种方式获取Class对象
        try {
            Class stuClass3 = Class.forName("fanshe.Student");//注意此字符串必须是真实路径，就是带包名的类路径，包名.类名
            System.out.println(stuClass3 == stuClass2);//判断三种方式是否获取的是同一个Class对象
```

### 注意

在运行期间，一个类，只有一个Class对象产生

三种方式常用第三种，第一种对象都有了还要反射干什么。第二种需要导入类的包，依赖太强，不导包就抛编译错误。一般都第三种，一个字符串可以传入也可写在配置文件中等多种方法。

## 反射的方法

## 小点

```java
//获取泛型类型    这样就可以获取java的类型  并且可以当做基本类型一样去强转
val type = (this.javaClass
.genericSuperclass as ParameterizedType).getActualTypeArguments()[0]
val list = gson.fromJson<RESPONSE>(result, type)
```

![Image](../../%E5%9B%BE%E5%BA%93/java%E4%B8%AD%E7%BA%A7/feefb1b1dd51f1eb92f389331557c823.png)

# JMM（内存模型）

![img](https://upload-images.jianshu.io/upload_images/4222138-49df5535c55287c4.png?imageMogr2/auto-orient/strip|imageView2/2/w/562)

![这里写图片描述](https://img-blog.csdnimg.cn/img_convert/8cedf683cdfacb3cfcd970cd739d5b9d.png)



![在这里插入图片描述](https://img-blog.csdnimg.cn/20210302091206468.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pqY2phdmE=,size_16,color_FFFFFF,t_70)





![ScreenClip](../../%E5%9B%BE%E5%BA%93/java%E4%B8%AD%E7%BA%A7/ScreenClip.png)

### JMM的三个特征

#### 原子性

定义：一个操作不能被打断，要么全部执行完毕，要么不执行。在这点上有点类似于事务操作，要么全部执行成功，要么回退到执行该操作之前的状态

基本类型数据的访问大都是原子操作，long 和double类型的变量是64位，但是在32位JVM中，32位的JVM会将64位数据的读写操作分为2次32位的读写操作来进行，这就导致了long、double类型的变量在32位虚拟机中是非原子操作，数据有可能会被破坏，也就意味着多个线程在并发访问的时候是线程非安全的。

#### 可见性

一个线程对共享变量做了修改之后，其他的线程立即能够看到（感知到）该变量的这种修改（变化）。

Java内存模型是通过将在工作内存中的变量修改后的值同步到主内存，在读取变量前从主内存刷新最新值到工作内存中，这种依赖主内存的方式来实现可见性的。

==无论是普通变量还是volatile变量都是如此，区别在于：volatile的特殊规则保证了volatile变量值修改后的新值立刻同步到主内存，每次使用volatile变量前立即从主内存中刷新，因此volatile保证了多线程之间的操作变量的可见性，而普通变量则不能保证这一点==。

除了volatile关键字能实现可见性之外，还有synchronized,Lock，final也是可以的。

使用synchronized关键字，在同步方法/同步块开始时（Monitor Enter）,使用共享变量时会从主内存中刷新变量值到工作内存中（即从主内存中读取最新值到线程私有的工作内存中），在同步方法/同步块结束时(Monitor Exit),会将工作内存中的变量值同步到主内存中去（即将线程私有的工作内存中的值写入到主内存进行同步）。

使用Lock接口的最常用的实现==ReentrantLock(重入锁)==来实现可见性：当我们在方法的开始位置执行lock.lock()方法，这和synchronized开始位置（Monitor Enter）有相同的语义，即使用共享变量时会从主内存中刷新变量值到工作内存中（即从主内存中读取最新值到线程私有的工作内存中），在方法的最后finally块里执行lock.unlock()方法，和synchronized结束位置（Monitor Exit）有相同的语义,即会将工作内存中的变量值同步到主内存中去（即将线程私有的工作内存中的值写入到主内存进行同步）。

==final关键字的可见性==是指：被final修饰的变量，在构造函数数一旦初始化完成，并且在构造函数中并没有把“this”的引用传递出去（“this”引用逃逸是很危险的，其他的线程很可能通过该引用访问到只“初始化一半”的对象），那么其他线程就可以看到final变量的值。

#### 有序性

对于一个线程的代码而言，我们总是以为代码的执行是从前往后的，依次执行的。这么说不能说完全不对，在单线程程序里，确实会这样执行；但是在多线程并发时，程序的执行就有可能出现乱序。用一句话可以总结为：在本线程内观察，操作都是有序的；如果在一个线程中观察另外一个线程，所有的操作都是无序的。前半句是指“线程内表现为串行语义（WithIn Thread As-if-Serial Semantics）”,后半句是指“指令重排”现象和“工作内存和主内存同步延迟”现象。

一个最经典的例子就是银行汇款问题，一个银行账户存款100，这时一个人从该账户取10元，同时另一个人向该账户汇10元，那么余额应该还是100。那么此时可能发生这种情况，A线程负责取款，B线程负责汇款，A从主内存读到100，B从主内存读到100，A执行减10操作，并将数据刷新到主内存，这时主内存数据100-10=90，而B内存执行加10操作，并将数据刷新到主内存，最后主内存数据100+10=110，显然这是一个严重的问题，我们要保证A线程和B线程有序执行，先取款后汇款或者先汇款后取款，此为有序性。

Java提供了两个关键字volatile和synchronized来保证多线程之间操作的有序性,volatile关键字本身通过加入内存屏障来禁止指令的重排序，而synchronized关键字通过一个变量在同一时间只允许有一个线程对其进行加锁的规则来实现，

在单线程程序中，不会发生“指令重排”和“工作内存和主内存同步延迟”现象，只在多线程程序中出现。

## 原子操作

![ScreenClip](../../%E5%9B%BE%E5%BA%93/java%E4%B8%AD%E7%BA%A7/%E5%8E%9F%E5%AD%90%E6%93%8D%E4%BD%9C.png)



## 内存可见性

在Java中，不同线程拥有各自的私有**工作内存**，当线程需要读取或修改某个变量时，不能直接去操作**主内存**中的变量，而是需要将这个变量读取到线程的**工作内存**的**变量副本**中，当该线程修改其变量副本的值后，**其它线程并不能立刻读取到新值**，需要将修改后的值**刷新到主内存中**，其它线程才能**从主内存读取到修改后的值**。

![img](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d866f75118d947c2b3c4bc4a340c3d38~tplv-k3u1fbpfcp-watermark.image)


## 指令重排序

在执行程序时为了提高性能，编译器和处理器常常会对指令做重排序，指令重排序使得代码在**多线程**执行时会出现一些问题。

其中最著名的案例便是在**初始化单例时**由于**可见性**和**重排序**导致的错误。

### 单例模式

#### 案例一

```java
public class Singleton {
    private static Singleton singleton;
    private Singleton() {
    }
    public static Singleton getInstance() {
        if (singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }
}
```


以上代码是经典的**懒汉式**单例实现，但在多线程的情况下，多个线程有可能会同时进入`if (singleton == null)` ，从而执行了多次`singleton = new Singleton()`，从而破坏单例。

#### 案例二

```java
public class Singleton {
    private static Singleton singleton;
    private Singleton() {
    }
    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
复制代码
```

以上代码在检测到`singleton`为null后，会在同步块中再次判断，可以保证同一时间只有一个线程可以初始化单例。但仍然存在问题，原因就是Java中`singleton = new Singleton()`语句并不是一个**原子指令**，而是由三步组成：

1. 为对象分配内存
2. 初始化对象
3. 将对象的内存地址赋给引用

但是当经过**指令重排序**后，会变成：

1. 为对象分配内存
2. 将对象的内存地址赋给引用（会使得singleton != null）
3. 初始化对象

所以就存在一种情况，当线程A已经将内存地址赋给引用时，但**实例对象并没有完全初始化**，同时线程B判断`singleton`已经不为null，就会导致B线程**访问到未初始化的变量**从而产生错误。

#### 案例三

```java
public class Singleton {
    private static volatile Singleton singleton;
    private Singleton() {
    }
    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
复制代码
```

以上代码对`singleton`变量添加了`volatile`修饰，可以阻止**局部指令重排序**。

那么为什么volatile可以保证变量的可见性和阻止指令重排序？

#### ==volatile==

1.规定线程每次修改变量副本后**立刻同步到主内存**中，用于保证其它线程可以看到自己对变量的修改

2.规定线程每次使用变量前，先从主内存中**刷新最新的值**到工作内存，用于保证能看见其它线程对变量修改的最新值

3.为了实现可见性内存语义，编译器在生成字节码时，会在指令序列中插入**内存屏障**来**防止指令重排序**。

Volatile（只是可以保证 可见性  有序性）并不保证原子性 关键字 ==MESI缓存一致性协议==    在store的时候加锁 在write之后解锁   修饰之后使原子之间可见  相比总线枷锁 性能得到极大的提升
总线嗅探机制  如果两个线程同时对同一个变量进行操作==可能会因为总线嗅探机制 使得assign的值失效==  又要从新去读 就可能==导致 线程对数据的操作失效==
在某些情况下 电脑的CPU会对一些指令执行重排  目的就是提高性能  （也就是说并不保证有序性）

加上volatile可以将共享变量i和j的改变直接响应到主内存中，这样保证了i和j的值可以保持一致，然而我们不能保证执行two方法的线程是在i和j执行到什么程度获取到的，所以volatile可以保证内存可见性，不能保证并发有序性。
如果没有volatile，则代码执行过程如下：

    将变量i从主内存拷贝到工作内存；
    刷新主内存数据；
    改变i的值；
    将变量j从主内存拷贝到工作内存；
    刷新主内存数据；
    改变j的值；




#### ==synchronized==

synchronized的特点
 一个线程执行互斥代码过程如下：

1. 获得同步锁；
2. 清空工作内存；
3. 从主内存拷贝对象副本到工作内存；
4. 执行代码(计算或者输出等)；
5. 刷新主内存数据；
6. 释放同步锁。

所以，==**synchronized既保证了多线程的并发有序性，又保证了多线程的内存可见性**。==





##### 注意

volatile只能保证基本类型变量的内存可见性，对于引用类型，无法保证引用所指向的**实际对象内部数据**的内存可见性。关于引用变量类型详见：[Java的数据类型](https://mp.weixin.qq.com/s/FqrnDcPt4a5SS8eTRffJdQ)。

volilate只能保证共享对象的**可见性**，不能保证**原子性**：假设两个线程同时在做x++，在线程A修改共享变量从0到1的同时，线程B**已经正在使用**值为0的变量，所以这时候**可见性已经无法发挥作用**，线程B将其修改为1，所以最后结果是1而不是2。

### 数据依赖

如果两个操作访问同一个变量，且这两个操作中有一个为写操作，此时这两个操作之间就存在数据依赖性。数据依赖分下列三种类型：
名称	代码示例	说明
写后读	a = 1; b = a;	写一个变量之后，再读这个位置。
写后写	a = 1;a = 2;	写一个变量之后，再写这个变量。
读后写	a = b;b = 1;	读一个变量之后，再写这个变量。

上面三种情况，只要重排序两个操作的执行顺序，程序的执行结果将会被改变。

前面提到过，编译器和处理器可能会对操作做重排序。编译器和处理器在重排序时，==会遵守数据依赖性==，编译器和处理器不会改变存在数据依赖关系的两个操作的执行顺序。
注意，==这里所说的数据依赖性仅针对单个处理器中执行的指令序列和单个线程中执行的操作，不同处理器之间和不同线程之间的数据依赖性不被编译器和处理器考虑==。

