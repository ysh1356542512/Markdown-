



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
