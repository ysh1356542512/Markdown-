





# 协程



添加依赖

```xml
 implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1"
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1"
```

### 启动协程

在 *kotlinx.coroutines* 库中，我们可以使用 [launch](https://link.zhihu.com/?target=https%3A//kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/launch.html) 或 [async](https://link.zhihu.com/?target=https%3A//kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/async.html) 来启动一个新的 coroutine。

从概念上讲，async 和 launch 是类似的，区别在于

 launch 会返回一个 [Job](https://link.zhihu.com/?target=https%3A//kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-job/index.html) 对象，不会携带任何结果值。

而 async 则是返回一个 [Deferred](https://link.zhihu.com/?target=https%3A//kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-deferred/index.html) - 一个轻量级、非阻塞的 future，代表了之后将会提供结果值的承诺（promise），因此可以使用 *.await()* 来获得其最终的结果，当然 Deferred 也是一个 Job，如果需要也是可以取消的。

### **Coroutine context**

在 Android 中我们经常使用两类 context:

- uiContext: 用于执行 UI 相关操作。
- bgContext: 用于执行需要在后台运行的耗时操作。

```kotlin
// dispatches execution onto the Android main UI thread
private val uiContext: CoroutineContext = UI
 
// represents a common pool of shared threads as the coroutine dispatcher
private val bgContext: CoroutineContext = CommonPool
```

这里 bgContext 使用 [CommonPool](https://link.zhihu.com/?target=https%3A//kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-common-pool/index.html)，可以限制同时运行的线程数小于 *Runtime.getRuntime.availableProcessors() - 1。*

**launch + async (execute task)**

父协程（The parent coroutine）使用 uiContext 通过 *launch* 启动。

子协程（The child coroutine）使用 CommonPool context 通过 *async* 启动。

**注意：**
\1. 父协程总是会等待所有的子协程执行完毕。
\2. 如果发生未检查的异常，应用将会崩溃。

下面实现一个简单的读取数据并由视图进行展示的例子：

```kotlin
private fun loadData() = launch(uiContext) {
    view.showLoading() // ui thread
 
    val task = async(bgContext) { dataProvider.loadData("Task") }
    val result = task.await() // non ui thread, suspend until finished
 
    view.showData(result) // ui thread
}
```

**launch + async + async (顺序执行两个任务)**

下面的两个任务是顺序执行的：

```kotlin
private fun loadData() = launch(uiContext) {
    view.showLoading() // ui thread
 
    // non ui thread, suspend until task is finished
    val result1 = async(bgContext) { dataProvider.loadData("Task 1") }.await()
 
    // non ui thread, suspend until task is finished
    val result2 = async(bgContext) { dataProvider.loadData("Task 2") }.await()
 
    val result = "$result1 $result2" // ui thread
 
    view.showData(result) // ui thread
}
```

**launch + async + async (同时执行两个任务)**

```kotlin
private fun loadData() = launch(uiContext) {
    view.showLoading() // ui thread
 
    val task1 = async(bgContext) { dataProvider.loadData("Task 1") }
    val task2 = async(bgContext) { dataProvider.loadData("Task 2") }
 
    val result = "${task1.await()} ${task2.await()}" // non ui thread, suspend until finished
 
    view.showData(result) // ui thread
}
```

**启动 coroutine 并设置超时时间**

可以通过*withTimeoutOrNull* 来给 coroutine job 设置时限，如果超时将会返回 null。

```kotlin
private fun loadData() = launch(uiContext) {
    view.showLoading() // ui thread
 
    val task = async(bgContext) { dataProvider.loadData("Task") }
 
    // non ui thread, suspend until the task is finished or return null in 2 sec
    val result = withTimeoutOrNull(2, TimeUnit.SECONDS) { task.await() }
 
    view.showData(result) // ui thread
}
```

**如何取消一个协程（coroutine）**

```kotlin
var job: Job? = null
 
fun startPresenting() {
    job = loadData()
}
 
fun stopPresenting() {
    job?.cancel()
}
 
private fun loadData() = launch(uiContext) {
    view.showLoading() // ui thread
 
    val task = async(bgContext) { dataProvider.loadData("Task") }
    val result = task.await() // non ui thread, suspend until finished
 
    view.showData(result) // ui thread
}
```

当父协程被取消时，所有的子协程将递归的被取消。

在上面的例子中，如果 *stopPresenting* 在被调用时 *dataProvider.loadData* 正在运行，那么 *view.showData* 方法将不会被调用。

### 异常

**try-catch block**

我们还是可以像平时一样用 try-catch 来捕获和处理异常。不过这里推荐将 try-catch 移到 *dataProvider.loadData* 方法里面，而不是直接包裹在外面，并提供一个统一的 Result 类方便处理。

```kotlin
data class Result<out T>(val success: T? = null, val error: Throwable? = null)
 
private fun loadData() = launch(uiContext) {
    view.showLoading() // ui thread
 
    val task = async(bgContext) { dataProvider.loadData("Task") }
    val result: Result<String> = task.await() // non ui thread, suspend until the task is finished
 
    if (result.success != null) {
        view.showData(result.success) // ui thread
    } else if (result.error != null) {
        result.error.printStackTrace()
    }
}
```

**async + async**

当通过 async 来启动父协程时，将会忽略掉任何异常：

```kotlin
private fun loadData() = async(uiContext) {
    view.showLoading() // ui thread
 
    val task = async(bgContext) { dataProvider.loadData("Task") }
    val result = task.await() // non ui thread, suspend until the task is finished
 
    view.showData(result) // ui thread
}
```

在这里 *loadData()* 方法会返回 Job 对象，而 exception 会被存放在这个 Job 对象中，我们可以用 invokeOnCompletion 函数来进行检索：

```kotlin
var job: Job? = null
 
fun startPresenting() {
    job = loadData()
    job?.invokeOnCompletion { it: Throwable? ->
        it?.printStackTrace() // (1)
        // or
        job?.getCompletionException()?.printStackTrace() // (2)
 
 
        // difference between (1) and (2) is that (1) will NOT contain CancellationException
        // in case if job was cancelled
    }
}
```

**launch + coroutine exception handler**

我们还可以为父协程的 context 中添加 *CoroutineExceptionHandler* 来捕获和处理异常：

```kotlin
val exceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }
 
private fun loadData() = launch(uiContext + exceptionHandler) {
    view.showLoading() // ui thread
 
    val task = async(bgContext) { dataProvider.loadData("Task") }
    val result = task.await() // non ui thread, suspend until the task is finished
 
    view.showData(result) // ui thread
}
```

### 测试

要启动协程（coroutine）必须要指定一个 [CoroutineContext](https://link.zhihu.com/?target=https%3A//kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.experimental/-coroutine-context/index.html)。

```kotlin
class MainPresenter(private val view: MainView,
                    private val dataProvider: DataProviderAPI) {
 
    private fun loadData() = launch(UI) { // UI - dispatches execution onto Android main UI thread
        view.showLoading()
 
        // CommonPool - represents common pool of shared threads as coroutine dispatcher
        val task = async(CommonPool) { dataProvider.loadData("Task") }
        val result = task.await()
 
        view.showData(result)
    }
 
}
```

因此，如果你想要为你的 MainPresenter 写单元测试，你就必须要能为 UI 和 后台任务指定 coroutine context。

最简单的方式就是为 MainPresenter 的构造方法增加两个参数，并设置默认值：

```kotlin
class MainPresenter(private val view: MainView,
                    private val dataProvider: DataProviderAPI
                    private val uiContext: CoroutineContext = UI,
                    private val ioContext: CoroutineContext = CommonPool) {
 
    private fun loadData() = launch(uiContext) { // use the provided uiContext (UI)
        view.showLoading()
 
        // use the provided ioContext (CommonPool)
        val task = async(bgContext) { dataProvider.loadData("Task") }
        val result = task.await()
 
        view.showData(result)
    }
 
}
```

现在，就可以在测试中传入 kotlin.coroutines 提供的 [EmptyCoroutineContext](https://link.zhihu.com/?target=https%3A//kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.experimental/-empty-coroutine-context/index.html) 来让代码运行在当前线程里。

```kotlin
@Test
fun test() {
    val dataProvider = Mockito.mock(DataProviderAPI::class.java)
    val mockView = Mockito.mock(MainView::class.java)
 
    val presenter = MainPresenter(mockView, dataProvider, EmptyCoroutineContext, EmptyCoroutineContext)
    presenter.startPresenting()
    ...
}
```

### 协程是轻量级的线程？

看情况。
 协程不只是给Android使用的，如果从Android的角度来说，kotlin的协程，并不是轻量级的线程。为什么？因为Android是基于JVM的，JVM能识别线程，但识别不了真正的协程。那为什么能用呢？那是因为Android中的协程，最终还是用线程。所以不能说是轻量级的线程。
 非Android使用的协程呢，有些的确是的，可以看协程的官方文档。

### Android



#### 业务框架层

![das](../../%E5%9B%BE%E5%BA%93/Kotlin%E8%BF%9B%E9%98%B6/SY%25C1ZH@Q@H%5BZ45KX%7D1%7DAE.png)



![das](../../%E5%9B%BE%E5%BA%93/Kotlin%E8%BF%9B%E9%98%B6/das-1625300781351.png)

不用写线程调度 自动就有对协成的支持

![dasdas](../../%E5%9B%BE%E5%BA%93/Kotlin%E8%BF%9B%E9%98%B6/dasdas.png)



类委托方式 就直接当成自己的属性使用了  全局使用协程

![dasdsa](../../%E5%9B%BE%E5%BA%93/Kotlin%E8%BF%9B%E9%98%B6/dasdsa.png)

#### **GlobalScope**

在一般的应用场景下，我们都希望可以异步进行耗时任务，比如网络请求，数据处理等等。当我们离开当前页面的时候，也希望可以取消正在进行的异步任务。这两点，也正是使用协程中所需要注意的。既然不建议直接使用 `GlobalScope`，我们就先试验一下使用它会是什么效果。

```kotlin
private fun launchFromGlobalScope() {
    GlobalScope.launch(Dispatchers.Main) {
        val deferred = async(Dispatchers.IO) {
            // network request
            delay(3000)
            "Get it"
        }
        globalScope.text = deferred.await()
        Toast.makeText(applicationContext, "GlobalScope", Toast.LENGTH_SHORT).show()
    }
}
```

在 `launchFromGlobalScope()` 方法中，我直接通过 `GlobalScope.launch()` 启动一个协程，`delay(3000)` 模拟网络请求，三秒后，会弹出一个 Toast 提示。使用上是没有任何问题的，可以正常的弹出 Toast  。但是当你执行这个方法之后，立即按返回键返回上一页面，仍然会弹出 Toast  。如果是实际开发中通过网络请求更新页面的话，当用户已经不在这个页面了，就根本没有必要再去请求了，只会浪费资源。GlobalScope  显然并不符合这一特性。[Kotlin 文档](https://link.zhihu.com/?target=https%3A//kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/index.html) 中其实也详细说明了，如下所示:

>  Global scope is used to launch top-level coroutines which are operating on the whole application lifetime and are not cancelled prematurely.  Another use of the global scope is operators running in  Dispatchers.Unconfined, which don’t have any job associated with them.
>  Application code usually should use an application-defined  CoroutineScope. Using async or launch on the instance of GlobalScope is  highly discouraged.

大致意思是，Global scope 通常用于启动顶级协程，这些协程在整个应用程序生命周期内运行，不会被过早地被取消。程序代码通常应该使用自定义的协程作用域。直接使用 GlobalScope 的 async 或者 launch 方法是强烈不建议的。

GlobalScope 创建的协程没有父协程，GlobalScope 通常也不与任何生命周期组件绑定。除非手动管理，否则很难满足我们实际开发中的需求。所以，GlobalScope 能不用就尽量不用。

#### **MainScope**

官方文档中提到要使用自定义的协程作用域，当然，Kotlin 已经给我们提供了合适的协程作用域 `MainScope` 。看一下 MainScope 的定义：

```kotlin
public fun MainScope(): CoroutineScope = ContextScope(SupervisorJob() + Dispatchers.Main)
```

记着这个定义，在后面 ViewModel 的协程使用中也会借鉴这种写法。

给我们的 Activity 实现自己的协程作用域：

```kotlin
class BasicCorotineActivity : AppCompatActivity(), CoroutineScope by MainScope() {}
```

通过扩展函数 `launch()` 可以直接在主线程中启动协程，示例代码如下：

```kotlin
private fun launchFromMainScope() {
    launch {
        val deferred = async(Dispatchers.IO) {
            // network request
            delay(3000)
            "Get it"
        }
        mainScope.text = deferred.await()
        Toast.makeText(applicationContext, "MainScope", Toast.LENGTH_SHORT).show()
    }
}
```

最后别忘了在 `onDestroy()` 中取消协程，通过扩展函数 `cancel()` 来实现：

```kotlin
override fun onDestroy() {
    super.onDestroy()
    cancel()
}
```

现在来测试一下 `launchFromMainScope()` 方法吧！你会发现这完全符合你的需求。实际开发中可以把 MainScope 整合到 BaseActivity 中，就不需要重复书写模板代码了。

#### **ViewModelScope**

如果你使用了 MVVM 架构，根本就不会在 Activity 上书写任何逻辑代码，更别说启动协程了。这个时候大部分工作就要交给 ViewModel 了。那么如何在 ViewModel 中定义协程作用域呢？还记得上面 `MainScope()` 的定义吗？没错，搬过来直接使用就可以了。

```kotlin
class ViewModelOne : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val mMessage: MutableLiveData<String> = MutableLiveData()

    fun getMessage(message: String) {
        uiScope.launch {
            val deferred = async(Dispatchers.IO) {
                delay(2000)
                "post $message"
            }
            mMessage.value = deferred.await()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
```

这里的 `uiScope` 其实就等同于 `MainScope`。调用 `getMessage()` 方法和之前的 `launchFromMainScope()` 效果也是一样的，记得在 ViewModel 的 `onCleared()` 回调里取消协程。

你可以定义一个 `BaseViewModel` 来处理这些逻辑，避免重复书写模板代码。然而 Kotlin 就是要让你做同样的事，写更少的代码，于是 `viewmodel-ktx` 来了。看到 ktx ，你就应该明白它是来简化你的代码的。引入如下依赖：

```xml
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha03"
```

然后，什么都不需要做，直接使用协程作用域 `viewModelScope` 就可以了。`viewModelScope` 是 ViewModel 的一个扩展属性，定义如下：

```kotlin
val ViewModel.viewModelScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(JOB_KEY,
                CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main))
        }
```

看下代码你就应该明白了，还是熟悉的那一套。当 `ViewModel.onCleared()` 被调用的时候，`viewModelScope` 会自动取消作用域内的所有协程。使用示例如下：

```kotlin
fun getMessageByViewModel() {
    viewModelScope.launch {
        val deferred = async(Dispatchers.IO) { getMessage("ViewModel Ktx") }
        mMessage.value = deferred.await()
    }
}
```

写到这里，`viewModelScope` 是能满足需求的最简写法了。实际上，写完全篇，`viewModelScope` 仍然是我认为的最好的选择。

#### **LiveData**

Kotlin 同样为 LiveData 赋予了直接使用协程的能力。添加如下依赖：

```text
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-alpha03"
```

直接在 `liveData {}` 代码块中调用需要异步执行的挂起函数，并调用 `emit()` 函数发送处理结果。示例代码如下所示：

```kotlin
val mResult: LiveData<String> = liveData {
    val string = getMessage("LiveData Ktx")
    emit(string)
}
```

你可能会好奇这里好像并没有任何的显示调用，那么，liveData 代码块是在什么执行的呢？当 LiveData 进入 `active` 状态时，`liveData{ }` 会自动执行。当 LiveData 进入 `inactive` 状态时，经过一个可配置的 timeout 之后会自动取消。如果它在完成之前就取消了，当 LiveData 再次 `active` 的时候会重新运行。如果上一次运行成功结束了，就不会再重新运行。也就是说只有自动取消的 `liveData{ }` 可以重新运行。其他原因（比如 `CancelationException`）导致的取消也不会重新运行。

所以 `livedata-ktx` 的使用是有一定限制的。对于需要用户主动刷新的场景，就无法满足了。**在一次完整的生命周期内，一旦成功执行完成一次，就没有办法再触发了。** 这句话不知道对不对，我个人是这么理解的。因此，还是 `viewmodel-ktx` 的适用性更广，可控性也更好。 

#### **LifecycleScope**

```text
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha03"
```

`lifecycle-runtime-ktx` 给每个 `LifeCycle` 对象通过扩展属性定义了协程作用域 `lifecycleScope` 。你可以通过 `lifecycle.coroutineScope` 或者 `lifecycleOwner.lifecycleScope` 进行访问。示例代码如下：

```kotlin
fun getMessageByLifeCycle(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycleScope.launch {
        val deferred = async(Dispatchers.IO) { getMessage("LifeCycle Ktx") }
        mMessage.value = deferred.await()
    }
}
```

当 LifeCycle 回调 `onDestroy()` 时，协程作用域 `lifecycleScope` 会自动取消。在 `Activity/Fragment` 等生命周期组件中我们可以很方便的使用，但是在 MVVM 中又不会过多的在 View 层进行逻辑处理，viewModelScope 基本就可以满足 ViewModel 中的需求了，`lifecycleScope` 也显得有点那么食之无味。但是他有一个特殊的用法：

```kotlin
suspend fun <T> Lifecycle.whenCreated()
suspend fun <T> Lifecycle.whenStarted()
suspend fun <T> Lifecycle.whenResumed()
suspend fun <T> LifecycleOwner.whenCreated()
suspend fun <T> LifecycleOwner.whenStarted()
suspend fun <T> LifecycleOwner.whenResumed()
```

可以指定至少在特定的生命周期之后再执行挂起函数，可以进一步减轻 View 层的负担。

# 高阶函数

## lamda

最后一个参数是一个lanmda表达式 可以移到（）的外边

一个函数只接受一个lanmda表达式的话 （）就可以省略

自动类型的推到 可以不给参数指定类型

只有一个参数的时候可以用it代替



让lamda获取上下文的环境  可以直接调用方法

![dasasf](../../%E5%9B%BE%E5%BA%93/Kotlin%E8%BF%9B%E9%98%B6/dasasf.png)

### Lambda语法

```kotlin
  1. 无参数的情况 ：
    val/var 变量名 = { 操作的代码 }

    2. 有参数的情况
    val/var 变量名 : (参数的类型，参数类型，...) -> 返回值类型 = {参数1，参数2，... -> 操作参数的代码 }

    可等价于
    // 此种写法：即表达式的返回值类型会根据操作的代码自推导出来。
    val/var 变量名 = { 参数1 ： 类型，参数2 : 类型, ... -> 操作参数的代码 }

    3. lambda表达式作为函数中的参数的时候，这里举一个例子：
    fun test(a : Int, 参数名 : (参数1 ： 类型，参数2 : 类型, ... ) -> 表达式返回类型){
        ...
    }
```

*实例讲解：*

- 无参数的情况

```kotlin
  // 源代码
   fun test(){ println("无参数") }
  
    // lambda代码
    val test = { println("无参数") }

    // 调用
    test()  => 结果为：无参数
```

有参数的情况,这里举例一个两个参数的例子，目的只为大家演示

```kotlin
   // 源代码
    fun test(a : Int , b : Int) : Int{
        return a + b
    }

    // lambda
    val test : (Int , Int) -> Int = {a , b -> a + b}
    // 或者
    val test = {a : Int , b : Int -> a + b}
    
    // 调用
    test(3,5) => 结果为：8
```

lambda表达式作为函数中的参数的时候

```kotlin
// 源代码
    fun test(a : Int , b : Int) : Int{
        return a + b
    }

    fun sum(num1 : Int , num2 : Int) : Int{
        return num1 + num2
    }

    // 调用
    test(10,sum(3,5)) // 结果为：18

    // lambda
    fun test(a : Int , b : (num1 : Int , num2 : Int) -> Int) : Int{
        return a + b.invoke(3,5)
    }

    // 调用
    test(10,{ num1: Int, num2: Int ->  num1 + num2 })  // 结果为：18
```

### Lambda实践

#### it

> - `it`并不是`Kotlin`中的一个关键字(保留字)。
> - `it`是在当一个高阶函数中`Lambda`表达式的参数只有一个的时候可以使用`it`来使用此参数。`it`可表示为**单个参数的隐式名称**，是`Kotlin`语言约定的。

例1：

```kotlin
val it : Int = 0  // 即it不是`Kotlin`中的关键字。可用于变量名称
```

例2：单个参数的隐式名称

```kotlin
// 这里举例一个语言自带的一个高阶函数filter,此函数的作用是过滤掉不满足条件的值。
val arr = arrayOf(1,3,5,7,9)
// 过滤掉数组中元素小于2的元素，取其第一个打印。这里的it就表示每一个元素。
println(arr.filter { it < 5 }.component1())   
```

例3：

```kotlin
 fun test(num1 : Int, bool : (Int) -> Boolean) : Int{
   return if (bool(num1)){ num1 } else 0
}

println(test(10,{it > 5}))
println(test(4,{it > 5}))
```

输出结果为：

```k
10
0
```

代码讲解：上面的代码意思是，在高阶函数`test`中，其返回值为`Int`类型，`Lambda`表达式以`num1`位条件。其中如果`Lambda`表达式的值为`false`的时候返回0，反之返回`num1`。故而当条件为`num1 > 5`这个条件时，当调用`test`函数，`num1 = 10`返回值就是10，`num1 = 4`返回值就是0。

#### 下划线（_）

在使用`Lambda`表达式的时候，可以用下划线(`_`)表示未使用的参数，表示不处理这个参数。

同时在遍历一个`Map`集合的时候，这当非常有用。

举例：

```kotlin
val map = mapOf("key1" to "value1","key2" to "value2","key3" to "value3")

map.forEach{
     key , value -> println("$key \t $value")
}

// 不需要key的时候
map.forEach{
    _ , value -> println("$value")
}
```

输出结果：

```kotlin
key1 	 value1
key2 	 value2
key3 	 value3
value1
value2
value3
```

####  匿名函数

> - 匿名函数的特点是可以明确指定其返回值类型。
>
> - 它和常规函数的定义几乎相似。他们的区别在于，匿名函数没有函数名。
>
> - 
>
>   例：

```kotlin
         fun test(x : Int , y : Int) : Int{                  fun(x : Int , y : Int) : Int{
常规函数：      return x + y                        匿名函数：      return x + y
            }                                                   }
```

```
常规函数 ： fun test(x : Int , y : Int) : Int = x + y
匿名函数 ： fun(x : Int , y : Int) : Int = x + y
```

从上面的两个例子可以看出，匿名函数与常规函数的区别在于一个有函数名，一个没有。

从上面的两个例子可以看出，匿名函数与常规函数的区别在于一个有函数名，一个没有。

实例演练：

```kotlin
val test1 = fun(x : Int , y : Int) = x + y  // 当返回值可以自动推断出来的时候，可以省略，和函数一样
val test2 = fun(x : Int , y : Int) : Int = x + y
val test3 = fun(x : Int , y : Int) : Int{
    return x + y
}

println(test1(3,5))
println(test2(4,6))
println(test3(5,7))
```

输出结果为：

```
8
10
12
```

从上面的代码我们可以总结出`匿名函数`与`Lambda`表达式的几点区别：

> 1. 匿名函数的参数传值，总是在小括号内部传递。而`Lambda`表达式传值，可以有省略小括号的简写写法。
> 2. 在一个不带`标签`的`return`语句中，匿名函数时返回值是返回自身函数的值，而`Lambda`表达式的返回值是将包含它的函数中返回。

#### 带接收者的函数字面值

在`kotlin`中，提供了指定的*接受者对象*调用`Lambda`表达式的功能。在函数字面值的函数体中，可以调用该接收者对象上的方法而无需任何额外的限定符。它类似于`扩展函数`，它允你在函数体内访问接收者对象的成员。

**匿名函数作为接收者类型**

匿名函数语法允许你直接指定函数字面值的接收者类型，如果你需要使用带接收者的函数类型声明一个变量，并在之后使用它，这将非常有用。

例：

```kotlin
val iop = fun Int.( other : Int) : Int = this + other
println(2.iop(3))
```

输出结果为：

```
5
```



#### 上下文推导



**Lambda表达式作为接收者类型**

> 要用Lambda表达式作为接收者类型的前提是**接收着类型可以从上下文中推断出来**。

例：这里用官方的一个例子做说明

```kotlin
class HTML {
    fun body() { …… }
}

fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()  // 创建接收者对象
    html.init()        // 将该接收者对象传给该 lambda
    return html
}


html {       // 带接收者的 lambda 由此开始
    body()   // 调用该接收者对象的一个方法
}
```

###  闭包

> - 所谓`闭包`，即是函数中包含函数，这里的函数我们可以包含(`Lambda`表达式，匿名函数，局部函数，对象表达式)。我们熟知，函数式编程是现在和未来良好的一种编程趋势。故而`Kotlin`也有这一个特性。
> - 我们熟知，`Java`是不支持闭包的，`Java`是一种面向对象的编程语言，在`Java`中，`对象`是他的一等公民。`函数`和`变量`是二等公民。
> - `Kotlin`中支持闭包，`函数`和`变量`是它的一等公民，而`对象`则是它的二等公民了。

实例：看一段`Java`代码

```kotlin
public class TestJava{

    private void test(){
        private void test(){        // 错误，因为Java中不支持函数包含函数

        }
    }

    private void test1(){}          // 正确，Java中的函数只能包含在对象中+
}
```

实例：看一段`Kotlin`代码

```kotlin
fun test1(){
    fun test2(){   // 正确，因为Kotlin中可以函数嵌套函数
        
    }
}
```

下面我们讲解`Kotlin`中几种闭包的表现形式。

#### 携带状态

例：让函数返回一个函数，并携带状态值

```kotlin
fun test(b : Int): () -> Int{
    var a = 3
    return fun() : Int{
        a++
        return a + b
    }
}

val t = test(3)
println(t())
println(t())
println(t())
```

输出结果：

```kotlin
7
8
9
```

==引用外部变量，并改变外部变量的值==

例：

```kotlin
var sum : Int = 0
val arr = arrayOf(1,3,5,7,9)
arr.filter { it < 7  }.forEach { sum += it }

println(sum)
```

输出结果：

```
9
```

## 自带高阶函数





[博客园学习资源](https://www.cnblogs.com/Jetictors/p/9225557.html)

![img](https://images2018.cnblogs.com/blog/1255627/201806/1255627-20180625175733663-1224851246.png)

### sumBy{}

 ==将函数用作函数参数的情况的高阶函数==

```kotlin
// sumBy函数的源码
public inline fun CharSequence.sumBy(selector: (Char) -> Int): Int {
    var sum: Int = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
```

源码说明：

1. 大家这里可以不必纠结`inline`，和`sumBy`函数前面的`CharSequence.`。因为这是`Koltin`中的`内联函数`与`扩展功能`。在后面的章节中会给大家讲解到的。这里主要分析高阶函数，故而这里不多做分析。
2. 该函数返回一个`Int`类型的值。并且接受了一个`selector()`函数作为该函数的参数。其中，`selector()`函数接受一个`Char`类型的参数，并且返回一个`Int`类型的值。
3. 定义一个`sum`变量，并且循环这个字符串，循环一次调用一次`selector()`函数并加上`sum`。用作累加。其中`this`关键字代表字符串本身。

所以这个函数的作用是：**把字符串中的每一个字符转换为`Int`的值，用于累加，最后返回累加的值**

例：

```kotlin
val testStr = "abc"
val sum = testStr.sumBy { it.toInt() }
println(sum)
```

输出结果为：

```kotlin
294  // 因为字符a对应的值为97,b对应98，c对应99，故而该值即为 97 + 98 + 99 = 294
```

### lock()

==将函数用作一个函数的返回值的高阶函数==

这里使用官网上的一个例子来讲解。`lock()`函数，先看一看他的源码实现

```kotlin
fun <T> lock(lock: Lock, body: () -> T): T {
    lock.lock()
    try {
        return body()
    }
    finally {
        lock.unlock()
    }
}
```

源码说明：

1. 这其中用到了`kotlin`中`泛型`的知识点，这里赞不考虑。我会在后续的文章为大家讲解。
2. 从源码可以看出，该函数接受一个`Lock`类型的变量作为参数`1`，并且接受一个无参且返回类型为`T`的函数作为参数`2`.
3. 该函数的返回值为一个函数，我们可以看这一句代码`return body()`可以看出。

例：使用`lock`函数，下面的代码都是伪代码，我就是按照官网的例子直接拿过来用的

```kotlin
fun toBeSynchronized() = sharedResource.operation()
val result = lock(lock, ::toBeSynchronized)    
```

其中，`::toBeSynchronized`即为对函数`toBeSynchronized()`的引用，其中关于双冒号`::`的使用在这里不做讨论与讲解。

上面的写法也可以写作：

```kotlin
val result = lock(lock, {sharedResource.operation()} )
```

## 高阶函数的使用

在上面的两个例子中，我们出现了`str.sumBy{ it.toInt }`这样的写法。其实这样的写法在前一章节`Lambda使用`中已经讲解过了。这里主要讲高阶函数中对`Lambda语法`的简写。

从上面的例子我们的写法应该是这样的：

```kotlin
str.sumBy( { it.toInt } )
```

但是根据`Kotlin`中的约定，即当函数中只有一个函数作为参数，并且您使用了`lambda`表达式作为相应的参数，则可以省略函数的小括号`()`。故而我们可以写成：

```kotlin
str.sumBy{ it.toInt }
```

还有一个约定，即当函数的最后一个参数是一个函数，并且你传递一个`lambda`表达式作为相应的参数，则可以在圆括号之外指定它。故而上面例`2`中的代码我们可写成：

```kotlin
val result = lock(lock){
     sharedResource.operation()
}
```

下面介绍几个`Kotlin`中常用的标准高阶函数。熟练的用好下面的几个函数，能减少很多的代码量，并增加代码的可读性。下面的几个高阶函数的源码几乎上都出自`Standard.kt`文件





### TODO函数



这个函数不是一个高阶函数，它只是一个抛出异常以及测试错误的一个普通函数。

> 此函数的作用：显示抛出`NotImplementedError`错误。`NotImplementedError`错误类继承至`Java`中的`Error`。我们看一看他的源码就知道了：



```kotlin
public class NotImplementedError(message: String = "An operation is not implemented.") : Error(message)
```

`TODO`函数的源码

```kotlin
@kotlin.internal.InlineOnly
public inline fun TODO(): Nothing = throw NotImplementedError()

@kotlin.internal.InlineOnly
public inline fun TODO(reason: String): Nothing = 
throw NotImplementedError("An operation is not implemented: $reason")
```

举例说明：

```kotlin
fun main(args: Array<String>) {
    TODO("测试TODO函数，是否显示抛出错误")
}
```



输出结果为：

![img](https://images2018.cnblogs.com/blog/1255627/201806/1255627-20180625180127011-830410439.png)



### run()函数



`run`函数这里分为两种情况讲解，因为在源码中也分为两个函数来实现的。采用不同的`run`函数会有不同的效果。



我们看下其源码：

```kotlin
public inline fun <R> run(block: () -> R): R {
contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
}
return block()
}
```

关于`contract`这部分代码小生也不是很懂其意思。在一些大牛的`blog`上说是其编辑器对上下文的推断。但是我也不知道对不对，因为在官网中，对这个东西也没有讲解到。不过这个单词的意思是`契约，合同`等等意思。我想应该和这个有关。在这里我就不做深究了。主要讲讲`run{}`函数的用法其含义。

这里我们只关心`return block()`这行代码。从源码中我们可以看出，`run`函数仅仅是执行了我们的`block()`，即一个`Lambda`表达式，而后返回了执行的结果。



**==用法1：==**

当我们需要执行一个`代码块`的时候就可以用到这个函数,并且这个代码块是独立的。即我可以在`run()`函数中写一些和项目无关的代码，因为它不会影响项目的正常运行。

例: 在一个函数中使用

```kotlin
private fun testRun1() {
    val str = "kotlin"

    run{
        val str = "java"   // 和上面的变量不会冲突
        println("str = $str")
    }

    println("str = $str")
}    
```



输出结果：

```kotlin
str = java
str = kotlin
```

**==用法2：==**

因为`run`函数执行了我传进去的`lambda`表达式并返回了执行的结果，所以当一个业务逻辑都需要执行同一段代码而根据不同的条件去判断得到不同结果的时候。可以用到`run`函数

例：都要获取字符串的长度。

```kotlin
val index = 3
val num = run {
    when(index){
        0 -> "kotlin"
        1 -> "java"
        2 -> "php"
        3 -> "javaScript"
        else -> "none"
    }
}.length
println("num = $num")
```

输出结果为：

```
num = 10
```

当然这个例子没什么实际的意义。

#### T.run()

其实`T.run()`函数和`run()`函数差不多，关于这两者之间的差别我们看看其源码实现就明白了：

```kotlin
public inline fun <T, R> T.run(block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}
```

从源码中我们可以看出，`block()`这个函数参数是一个扩展在`T`类型下的函数。这说明我的`block()`函数可以可以使用当前对象的上下文。所以**当我们传入的`lambda`表达式想要使用当前对象的上下文的时候，我们可以使用这个函**数。****

**用法：**

> 这里就不能像上面`run()`函数那样当做单独的一个`代码块`来使用。



例：

```kotlin
val str = "kotlin"
str.run {
    println( "length = ${this.length}" )
    println( "first = ${first()}")
    println( "last = ${last()}" )
}
```

输出结果为：

```kotlin
length = 6
first = k
last = n
```



在其中，可以使用`this`关键字，因为在这里它就代码`str`这个对象，也可以省略。因为在源码中我们就可以看出，`block`()
 就是一个`T`类型的扩展函数。

这在实际的开发当中我们可以这样用：

例： 为`TextView`设置属性。

```kotlin
val mTvBtn = findViewById<TextView>(R.id.text)
mTvBtn.run{
    text = "kotlin"
    textSize = 13f
    ...
}
```



### with()函数



其实`with()`函数和`T.run()`函数的作用是相同的，我们这里看下其实现源码：

```kotlin
public inline fun <T, R> with(receiver: T, block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return receiver.block()
}
```



这里我们可以看出和`T.run()`函数的源代码实现没有太大的差别。故而这两个函数的区别在于：

> 1. `with`是正常的高阶函数，`T.run()`是扩展的高阶函数。
> 2. `with`函数的返回值指定了`receiver`为接收者。

故而上面的`T.run()`函数的列子我也可用`with`来实现相同的效果：

例：

```kotlin
val str = "kotlin"
with(str) {
    println( "length = ${this.length}" )
    println( "first = ${first()}")
    println( "last = ${last()}" )
}
```

输出结果为：

```kotlin
length = 6
first = k
last = n
```



为`TextView`设置属性，也可以用它来实现。这里我就不举例了。

在上面举例的时候，都是正常的列子，这里举一个特例：当我的对象可为`null`的时候，看两个函数之间的便利性。

例：

```kotlin
val newStr : String? = "kotlin"

with(newStr){
    println( "length = ${this?.length}" )
    println( "first = ${this?.first()}")
    println( "last = ${this?.last()}" )
}

newStr?.run {
    println( "length = $length" )
    println( "first = ${first()}")
    println( "last = ${last()}" )
}
```

从上面的代码我们就可以看出，当我们使用对象可为`null`时，使用`T.run()`比使用`with()`函数从代码的可读性与简洁性来说要好一些。当然关于怎样去选择使用这两个函数，就得根据实际的需求以及自己的喜好了。

### T.apply()函数

我们先看下`T.apply()`函数的源码：

```kotlin
public inline fun <T> T.apply(block: T.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
    return this
}
```

从`T.apply()`源码中在结合前面提到的`T.run()`函数的源码我们可以得出,这两个函数的逻辑差不多，唯一的区别是`T,apply`执行完了`block()`函数后，返回了自身对象。而`T.run`是返回了执行的结果。

故而： `T.apply`的作用除了实现能实现`T.run`函数的作用外，还可以后续的再对此操作。下面我们看一个例子：

例：为`TextView`设置属性后，再设置点击事件等



```kotlin
val mTvBtn = findViewById<TextView>(R.id.text)
mTvBtn.apply{
    text = "kotlin"
    textSize = 13f
    ...
}.apply{
    // 这里可以继续去设置属性或一些TextView的其他一些操作
}.apply{
    setOnClickListener{ .... }
}
```

或者：设置为`Fragment`设置数据传递

```kotlin
// 原始方法
fun newInstance(id : Int , name : String , age : Int) : MimeFragment{
        val fragment = MimeFragment()
        fragment.arguments.putInt("id",id)
        fragment.arguments.putString("name",name)
        fragment.arguments.putInt("age",age)
        
        return fragment
}

// 改进方法
fun newInstance(id : Int , name : String , age : Int) = MimeFragment().apply {
        arguments.putInt("id",id)
        arguments.putString("name",name)
        arguments.putInt("age",age)
}
```

### T.also()函数

关于`T.also`函数来说，它和`T.apply`很相似，。我们先看看其源码的实现：

```kotlin
public inline fun <T> T.also(block: (T) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(this)
    return this
}
```

从上面的源码在结合`T.apply`函数的源码我们可以看出： `T.also`函数中的参数`block`函数传入了自身对象。故而这个函数的作用是用用`block`函数调用自身对象，最后在返回自身对象

这里举例一个简单的例子，并用实例说明其和`T.apply`的区别

例：

```kotlin
"kotlin".also {
    println("结果：${it.plus("-java")}")
}.also {
    println("结果：${it.plus("-php")}")
}

"kotlin".apply {
    println("结果：${this.plus("-java")}")
}.apply {
    println("结果：${this.plus("-php")}")
}
```

他们的输出结果是相同的：

```kotlin
结果：kotlin-java
结果：kotlin-php

结果：kotlin-java
结果：kotlin-php
```

从上面的实例我们可以看出，他们的区别在于，`T.also`中只能使用`it`调用自身,而`T.apply`中只能使用`this`调用自身。因为在源码中`T.also`是执行`block(this)`后在返回自身。而`T.apply`是执行`block()`后在返回自身。这就是为什么在一些函数中可以使用`it`,而一些函数中只能使用`this`的关键所在

### T.let()函数



在前面讲解`空安全、可空属性`章节中，我们讲解到可以使用`T.let()`函数来规避空指针的问题。有兴趣的朋友可以去看看我的[Kotlin——初级篇（六）：空类型、空安全、非空断言、类型转换等特性总结](https://www.cnblogs.com/Jetictors/p/8292098.html)这篇文章。但是在这篇文章中，我们只讲到了它的使用。故而今天来说一下他的源码实现：

```kotlin
public inline fun <T, R> T.let(block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this)
}
```

从上面的源码中我们可以得出，它其实和`T.also`以及`T.apply`都很相似。而`T.let`的作用也不仅仅在使用`空安全`这一个点上。用`T.let`也可实现其他操作

例：

```kotlin
"kotlin".let {
    println("原字符串：$it")         // kotlin
    it.reversed()
}.let {
    println("反转字符串后的值：$it")     // niltok
    it.plus("-java")
}.let {
    println("新的字符串：$it")          // niltok-java
}

"kotlin".also {
    println("原字符串：$it")     // kotlin
    it.reversed()
}.also {
    println("反转字符串后的值：$it")     // kotlin
    it.plus("-java")
}.also {
    println("新的字符串：$it")        // kotlin
}

"kotlin".apply {
    println("原字符串：$this")     // kotlin
    this.reversed()
}.apply {
    println("反转字符串后的值：$this")     // kotlin
    this.plus("-java")
}.apply {
    println("新的字符串：$this")        // kotlin
}
```

输出结果看是否和注释的结果一样呢：

```kotlin
原字符串：kotlin
反转字符串后的值：niltok
新的字符串：niltok-java

原字符串：kotlin
反转字符串后的值：kotlin
新的字符串：kotlin

原字符串：kotlin
反转字符串后的值：kotlin
新的字符串：kotlin
```

### T.takeIf()函数

从函数的名字我们可以看出，这是一个关于`条件判断`的函数,我们在看其源码实现：

```kotlin
public inline fun <T> T.takeIf(predicate: (T) -> Boolean): T? {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return if (predicate(this)) this else null
}
```

从源码中我们可以得出这个函数的作用是：

> 传入一个你希望的一个条件，如果对象符合你的条件则返回自身，反之，则返回`null`。

例： 判断一个字符串是否由某一个字符起始，若条件成立则返回自身，反之，则返回`null`



```kotlin
val str = "kotlin"

val result = str.takeIf {
    it.startsWith("ko") 
}

println("result = $result")
```

输出结果为：

```kotlin
result = kotlin
```

### T.takeUnless()函数

这个函数的作用和`T.takeIf()`函数的作用是一样的。只是和其的逻辑是相反的。即：传入一个你希望的一个条件，如果对象符合你的条件则返回`null`，反之，则返回自身。

这里看一看它的源码就明白了。

```kotlin
public inline fun <T> T.takeUnless(predicate: (T) -> Boolean): T? {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return if (!predicate(this)) this else null
}
```



这里就举和`T.takeIf()`函数中一样的例子，看他的结果和`T.takeIf()`中的结果是不是相反的。

例：

```kotlin
val str = "kotlin"

val result = str.takeUnless {
    it.startsWith("ko") 
}

println("result = $result")
```



输出结果为：

```
result = null
```



### repeat()函数



首先，我们从这个函数名就可以看出是关于`重复`相关的一个函数，再看起源码，从源码的实现来说明这个函数的作用：

```kotlin
public inline fun repeat(times: Int, action: (Int) -> Unit) {
    contract { callsInPlace(action) }

    for (index in 0..times - 1) {
        action(index)
    }
}
```

从上面的代码我们可以看出这个函数的作用是：

> 根据传入的重复次数去重复执行一个我们想要的动作(函数)

例：

```kotlin
repeat(5){
    println("我是重复的第${it + 1}次，我的索引为：$it")
}
```

输出结果为：

```kotlin
我是重复的第1次，我的索引为：0
我是重复的第2次，我的索引为：1
我是重复的第3次，我的索引为：2
我是重复的第4次，我的索引为：3
我是重复的第5次，我的索引为：4
```

### lazy()函数

关于`Lazy()`函数来说，它共实现了`4`个重载函数，都是用于延迟操作，不过这里不多做介绍。因为在实际的项目开发中常用都是用于延迟初始化属性。而关于这一个知识点我在前面的变量与常量已经讲解过了。这里不多做介绍...

如果您有兴趣，可以去看看我的[Kotlin——初级篇（二）：变量、常量、注释](https://www.cnblogs.com/Jetictors/p/7723044.html)这篇文章。

## 总结

关于重复使用同一个函数的情况一般都只有`T.also`、`T.let`、`T.apply`这三个函数。而这个三个函数在上面讲解这些函数的时候都用实例讲解了他们的区别。故而这里不做详细实例介绍。并且连贯着使用这些高阶函数去处理一定的逻辑，在实际项目中很少会这样做。一般都是单独使用一个，或者两个、三个这个连贯这用。但是在掌握了这些函数后，我相信您也是可以的。这里由于蝙蝠原因就不做实例讲解了..

关于他们之间的区别，以及他们用于实际项目中在一定的需求下到底该怎样去选择哪一个函数进行使用希望大家详细的看下他们的源码并且根据我前面说写的实例进行分析。

大家也可以参考这两篇文章：
 [掌握Kotlin标准函数：run, with, let, also and apply](https://juejin.im/post/5a676159f265da3e3c6c4d82)
 [那些年，我们看不懂的那些Kotlin标准函数](http://youngfeng.com/2018/04/27/kotlin/那些年，我们看不懂的那些Kotlin标准函数/)



## 自定义高阶函数

```kotlin
// 源代码
fun test(a : Int , b : Int) : Int{
    return a + b
}

fun sum(num1 : Int , num2 : Int) : Int{
    return num1 + num2
}

// 调用
test(10,sum(3,5)) // 结果为：18

// lambda
fun test(a : Int , b : (num1 : Int , num2 : Int) -> Int) : Int{
    return a + b.invoke(3,5)
}

// 调用
test(10,{ num1: Int, num2: Int ->  num1 + num2 })  // 结果为：18
```

可以看出上面的代码中，直接在我的方法体中写死了数值，这在开发中是很不合理的，并且也不会这么写。上面的例子只是在阐述`Lambda`的语法。接下来我另举一个例子：

例：传入两个参数，并传入一个函数来实现他们不同的逻辑

例：

```kotlin
private fun resultByOpt(num1 : Int , num2 : Int , result : (Int ,Int) -> Int) : Int{
    return result(num1,num2)
}

private fun testDemo() {
    val result1 = resultByOpt(1,2){
        num1, num2 ->  num1 + num2
    }

    val result2 = resultByOpt(3,4){
        num1, num2 ->  num1 - num2
    }

    val result3 = resultByOpt(5,6){
        num1, num2 ->  num1 * num2
    }

    val result4 = resultByOpt(6,3){
        num1, num2 ->  num1 / num2
    }

    println("result1 = $result1")
    println("result2 = $result2")
    println("result3 = $result3")
    println("result4 = $result4")
}
```

输出结果为：

```kotlin
result1 = 3
result2 = -1
result3 = 30
result4 = 2  
```

这个例子是根据传入不同的`Lambda`表达式，实现了两个数的`+、-、*、/`。
 当然了，在实际的项目开发中，自己去定义高阶函数的实现是很少了，因为用系统给我们提供的高阶函数已经够用了。不过，当我们掌握了`Lambda`语法以及怎么去定义高阶函数的用法后。在实际开发中有了这种需求的时候也难不倒我们了。
