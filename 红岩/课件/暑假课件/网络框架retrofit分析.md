## Retroift

### Retorift使用

#### Retrofit创建

```
Retrofit.Builder()
			//BASE地址
			.baseUrl()
			//Okhttp
            .client()
			.callFactory()
			//转换器工厂,如添加Gson转化器
            .addConverterFactory()
            //适配器工厂,如Rxjava
            .addCallAdapterFactory()
            //自定义回调,对请求后响应结果的回调执行限定
            .callbackExecutor()
            //Boolean,方法是否立即生效，针对接口的default方法
            .validateEagerly()
            .build()
```

##### client&CallFactory

这两个是一样的,只是参数不一样

```java
public Builder client(OkHttpClient client) {
      return callFactory(Objects.requireNonNull(client, "client == null"));//这里是调用了callfactory,都是传入执行的okhttp
    }

public Builder callFactory(okhttp3.Call.Factory factory) {
      this.callFactory = Objects.requireNonNull(factory, "factory == null");
      return this;
    }
```

##### ConverterFactory

`MyConverter`:继承`Converter<ResponseBody,?>`，在里面对`ResponseBody`进行操作，然后返回需要的类型

`MyConverterFactory`:Factory类,一个`responseBodyConverter`方法，一个`requestBodyConverter`方法

```kotlin
class PilPiliConverterFactory(private val gson:Gson = Gson()) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonResponseBodyConverter(gson,adapter)
    }
}
```



```kotlin
class GsonResponseBodyConverter<T>(private val gson: Gson,private val adapter: TypeAdapter<T>):Converter<ResponseBody,T> {
    override fun convert(value: ResponseBody): T? {
        val jsonReader: JsonReader = gson.newJsonReader(value.charStream())
            val baseAdapter:TypeAdapter<BaseResponse> = gson.getAdapter(TypeToken.get(BaseResponse::class.java))
            val result:BaseResponse = baseAdapter.read(jsonReader)
            if (result.status) {
                return adapter.read(jsonReader)
            }else{
                Log.e("Retrofit",result.data.toString())
                throw IOException("result error ${result.data}")
            }
        return null
    }
}
```

retrofit对于解析器是由添加的顺序分别试用的，解析成功就直接返回，失败则调用下一个解析器。

(灵活的应对万恶的后端)

##### CallAdapterFactory

这个是对返回值的类型进行操作

`MyCall`:自己定义的Call类型，在里面对传入的`Call`进行操作，也可以继承Call或者自己写再用Call操作

`MyCallAdapter`：  适配器

`MyCallAdapterFactory`  继承`CallAdapter.Factory`的类型，做基本判断，分配Adapter

```kotlin
class MyCall<R> constructor(private val call:Call<Any>) {

    fun get(): Any? {//直接执行传入的call,这里可以夹带私活
        return call.execute().body()
    }
}
```

```kotlin
class MyCallAdapter(private val responseType: Type ):CallAdapter<Any, MyCall<R>> {
    //这里因为没有什么业务处理，所以内容很少，最后的执行还是在MyCall里面执行的
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<Any>): MyCall<R> {
        //这里可以进行操作(理论来讲这里是自己写返回
       return MyCall(call)
    }

```

```kotlin
class MyCallAdapterFactrory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        //检测是否是MyCall类型，并且得有泛型
        if (rawType === MyCall::class.java && returnType is ParameterizedType){
            //ParameterizedType-->>泛型
            val parameterUpperBound = getParameterUpperBound(0, returnType)
            //创建一个CallAdapter
            return  MyCallAdapter(parameterUpperBound)
        }
        return null
    }
}
```

以上只是主要流程

建议自行阅读`RxJava2CallAdapterFactory`源码体会到底这样做能干嘛(

##### callbackExecutor

这玩意估摸着也没有用的需求(

他的作用是，在最后回调的时候调用了他来决定调用的时候是哪一个线程，源码中已经写好了(

`MainThreadExecutor`在Platform中获取

```java
static final class MainThreadExecutor implements Executor {
      private final Handler handler = new Handler(Looper.getMainLooper());
      
      public void execute(Runnable r) {
        this.handler.post(r);
      }
    }
```

使用的地方

```java
public void enqueue(final Callback<T> callback) {
      Objects.requireNonNull(callback, "callback == null");
      this.delegate.enqueue(new Callback<T>() {
            public void onResponse(Call<T> call, Response<T> response) {
              DefaultCallAdapterFactory.ExecutorCallbackCall.this.callbackExecutor.execute(() -> {//这里就是callbackExecutor
                    if (DefaultCallAdapterFactory.ExecutorCallbackCall.this.delegate.isCanceled()) {
                      callback.onFailure(DefaultCallAdapterFactory.ExecutorCallbackCall.this, new IOException("Canceled"));//这里就是回调的地方
                    } else {
                      callback.onResponse(DefaultCallAdapterFactory.ExecutorCallbackCall.this, response);
                    } 
                  });
            }
            
            public void onFailure(Call<T> call, Throwable t) {
              DefaultCallAdapterFactory.ExecutorCallbackCall.this.callbackExecutor.execute(() -> callback.onFailure(DefaultCallAdapterFactory.ExecutorCallbackCall.this, t));
            }
          });
    }
```

##### validateEagerly

Boolean

填true的话

如果被调用的方法是一个default方法(不需要注释)，通过`platform.invokeDefaultMethod`完成调用

```java
return this.platform.isDefaultMethod(method) ? 
              this.platform.invokeDefaultMethod(method, service, proxy, args)
```

填false，如果有那个方法,按正常的方法，没有注解，报错

PS:Kotlin的接口虽然可以定义方法体实现，但是实现机制与java的default方法实现机制不同，Kotlin中也没有`default`关键字,Kotlin的默认方法实现大概是找到这个方法然后用一个实现这个方法的静态内部类类把原来的方法代理了,一种伪default

#### Retrofit使用

##### Retrofit注解

| 请求方法注解 | 说明                                                         |
| ------------ | ------------------------------------------------------------ |
| @GET         |                                                              |
| @POST        |                                                              |
| @PUT         |                                                              |
| @DELETE      |                                                              |
| @PATCH       | patch请求，该请求是对put请求的补充，用于更新局部资源         |
| @HEAD        |                                                              |
| @OPTIONS     |                                                              |
| @HTTP        | 通过注解，可以替换以上所有的注解，它拥有三个属性：method、path、hasBody |

| 请求头注解 | 说明                                                         |
| ---------- | ------------------------------------------------------------ |
| @Headers   | 用于添加固定请求头，可以同时添加多个，通过该注解的请求头不会相互覆盖，而是共同存在 |
| @Header    | 作为方法的参数传入，用于添加不固定的header，它会更新已有请求头 |

| 请求参数注解 | 说明                                                         |
| ------------ | ------------------------------------------------------------ |
| @Body        | 多用于Post请求发送非表达数据，根据转换方式将实例对象转化为对应字符串传递参数，比如使用Post发送Json数据，添加GsonConverterFactory则是将body转化为json字符串进行传递 |
| @Filed       | 多用于Post方式传递参数，需要结合@FromUrlEncoded使用，即以表单的形式传递参数 |
| @FiledMap    | 多用于Post请求中的表单字段，需要结合@FromUrlEncoded使用      |
| @Part        | 用于表单字段，Part和PartMap与@multipart注解结合使用，适合文件上传的情况 |
| @PartMap     | 用于表单字段，默认接受类型是Map<String,RequestBody>，可用于实现多文件上传 |
| @Path        | 用于Url中的占位符                                            |
| @Query       | 用于Get请求中的参数                                          |
| @QueryMap    | 与Query类似，用于不确定表单参数                              |
| @Url         | 指定请求路径                                                 |

| 标记类注解    | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| @FromUrlCoded | 表示请求发送编码表单数据，每个键值对需要使用@Filed注解       |
| @Multipart    | 表示请求发送form_encoded数据(使用于有文件上传的场景)，每个键值对需要用@Part来注解键名，随后的对象需要提供值 |
| @Streaming    | 表示响应用字节流的形式返回，如果没有使用注解，默认会把数据全部载入到内存中，该注解在下载大文件时特别有用 |

###### GET和Query&QueryMap使用

```
@GET("test")
fun getData(@Query("id") int:Int,@Query("name") str:String)

@GET("test")
fun getData(@QueryMap Map<String, Object> map)
//这里是如果有不确定类型的，就用这种方式将参数传入
xxx/test?id=int&name=str
```

###### POST和File FileMap FormUrlEncoded使用

```kotlin
@FormUrlEncoded
@POST("user/login/pw")
    fun login(@Field("loginName") loginName: String, @Field("password") password:String):Observable<String>
```

发送表单,一定要有`@FromUrlEncoded`,使用的话，和Query,QueryMap一样

###### POST和Body使用

```kotlin
@POST("test")
fun getResponseBody(@Body body:RequestBody):Call<ResponseBody>
```

这里的Body的转换规则是在ConverterFactory里面定义的，可以把对象转换成Json格式发送给服务器,GsonConverterFactory中提供了转换

PS:这里不能使用`@FormUrlEncoded`和`@Multipart`注解，源码中有判断

###### HTTP使用

```kotlin
@HTTP(method = "GET", path = "user/info/{uid}", hasBody = true)
    fun getUserInfoByUidForHttp(@Path("uid") uid:Int): Observable<UserInfoBean>
```

三个参数(传说中的自定义)

`method`:请求方法

`path`:请求地址

`hasBody`:boolean类型，是否有请求体

其他的使用方法是一样的

###### Path使用

```kotlin
@GET("user/info/{uid}")
    fun getUserInfoByUid(@Path("uid") uid:Int): Observable<UserInfoBean>

user/info/:uid
```

同时也可以夹杂`Query`，`Query`的存在于`?`之后

###### Url使用

```kotlin
@GET
fun getData(@Url str:String,@Query("id") int:Int):Call<String>
```

使用了url就可以省略`@GET`后面的url参数,或者就是拼接

###### Header Headers使用

```kotlin
@GET("test")
fun getData(@Header("token") str:String):Call<String>
```

`Header`用于参数添加请求头

```kotlin
@Headers("token","myToken")
@GET("test")
fun getData():Call<String>
```

`Header`用于添加头文件，可以添加多个共同存在

###### Streaming Multipart pat partMap使用

```kotlin
@String
@GET("test")
fun getData():Call<ResponseBody>
```

表示返回数据以流的形式返回，一般是下文件用的(俺也没用过)



```kotlin
@Multipart
@POST("user/followers")
fun getPartData(@Part("ret") ret:RequestBody, @Part file:MultipartBody.Part):Call<ResponseBody>
```

`@Multipart`就是表示支持文件上传的表单

`@Part`表示表单字段，支持 `RequestBody`,`MultipartBody.Part`,`任意类型`

##### RetrofitDemo

```kotlin
object ApiModule {
    private val BASE_URL = "https://anonym.ink/api/"
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(MyConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    
    val allApi: AllApi = retrofit.create(AllApi::class.java)//最后直接用这里的api代理对象
    val allApiForJava:AllApiForJava = retrofit.create(AllApiForJava::class.java)
}
```

个人写法(俺也不知道好坏)

### Retrofit源码

#### Retrofit中的代理模式

##### 代理模式

代理模式就是让另一个对象，间接来执行这个需求对象的功能

<img src="http://c.biancheng.net/uploads/allimg/181115/3-1Q115093011523.gif"  style="zoom:100%;" align="left"/>

```kotlin
// 抽象主题：声明了目标对象和代理对象的共同接口，约定了他们的功能，这样一来在任何可以使用目标对象的地方都可以使用代理对象。
interface Subject {
    void request()
}
// 真实主题：目标对象，也就是被代理的对象，或称为委托角色、被代理角色。
class RealSubject:Subject {
    override fun request(){
        println("真实的请求")
    }
}
// 代理(Proxy)类或委托类：内部含有目标对象的引用，从而在任何时候都可以操作目标对象。
// 同时代理对象和目标对象实现共同接口，有同样的功能,以便在任何时候替代目标对象。
class SubjectProxy:Subject {

    override fun request(){
        beforeRequest()
        RealSubject().request()
        afterRequest()
    }

    fun beforeRequest(){
        println("beforeRequest")
    }

    fun afterRequest(){
        println("afterRequest")
    }
}
```

其实这里也不难理解，最终访问的是代理对象来完成对目标对象的request()调用，同时在原代码逻辑的基础上增加了一些逻辑，而调用者并无感知。
这样做的好处在于：将访问对象与目标对象分离 (降低耦合)，同时代理对象可以扩展目标对象的功能 (逻辑增强)。
但是也有缺点，比如需要增加很多这样的代理类，也就增加了系统的复杂度，或者在访问对象和目标对象增加逻辑造成原处理速度变慢等。
解决办法，动态代理！而上面的方式也就是对应的静态代理。

##### Retrofit中的动态代理

这里用的是JDK动态代理(通过实现接口的方式)。还有个CGlib动态代理，支持不是接口的类实现的代理。大致操作是生成一个被代理类的子类，然后在子类调用拦截父类的方法调用，顺带插入自己的~~私货~~逻辑。~~有AOP那味~~



JDK动态代理主要涉及两个类：`Proxy` 和 `InvocationHandler`

首先需要创建一个InvocationHandler的实现类，里面可以实现自己的代理业务。

```kotlin
class LogHandler(private val delegate:Any):InvocationHandler {
// delegate被代理的实体对象
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        before()
        val invoke = method?.invoke(delegate,*args.orEmpty()) as Any?
        after()
        return invoke
    }
    fun before(){
        println("Before")
    }
    fun after(){
        println("after")
    }
}
```

然后我们就可以通过 Proxy.newProxyInstance()方法，传入这个InvocationHandler对象来获取到我们想要的代理对象

```kotlin
fun main(){
    //获得代理类的class文件
    //System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true")
    //创建被代理的实体对象
    val realSubject = RealSubject()
    //获取对应的classLoader
    val classLoader = realSubject.javaClass.classLoader
    //获取所有接口的Class，这里RealSubject只实现了一个Subject接口
    val interfaces = realSubject.javaClass.interfaces
    //创建一个InvocationHandler，处理所有的代理对象上的方法调用
    //这里创建的是一个自定义的日志处理器，须传入实际的被代理对象realSubject
    val logHandler:InvocationHandler = LogHandler(realSubject)
    //5.根据上面提供的信息，创建代理对象在这个过程中：
    //a.JDK会通过根据传入的参数信息动态地在内存中创建和.class 文件等同的字节码；
    //b.然后根据相应的字节码转换成对应的class；
    //c.然后调用newInstance()创建代理实例；
    val newProxyInstance = Proxy.newProxyInstance(classLoader, interfaces, logHandler) as Subject
    //调用代理对象的方法
    newProxyInstance.request()
}
```

`System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true")`这个是生成动态代理class的代码(其他的都过时了)

生成的代理类`$Proxy0`

```java
package com.sun.proxy;

import cn.udday.forretrofit.myProxy.Subject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements Subject {
  private static Method m1;
  
  private static Method m2;
  
  private static Method m3;
  
  private static Method m0;
  
  public $Proxy0(InvocationHandler paramInvocationHandler) {
    super(paramInvocationHandler);
  }
  
  public final boolean equals(Object paramObject) {
    try {
      return ((Boolean)this.h.invoke(this, m1, new Object[] { paramObject })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String toString() {
    try {
      return (String)this.h.invoke(this, m2, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void request() {
    try {
     // 最终都会去执行InvocationHandler的invoke方法
     // 所以我们通过实现InvocationHandler类 (也就是上面的LogHandler)，并重写invoke方法来达到目的
      this.h.invoke(this, m3, null);
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int hashCode() {
    try {
      return ((Integer)this.h.invoke(this, m0, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m3 = Class.forName("cn.udday.forretrofit.myProxy.Subject").getMethod("request", new Class[0]);//这里是m3方法的定义
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
}
```

##### Retrofit中使用动态代理

```kotlin
val allApi: AllApi = retrofit.create(AllApi::class.java)
println(allApi.javaClass.toString())
```

##### Proxy.newProxyInstance()

```java
public static Object newProxyInstance(ClassLoader loader,
                                      Class<?>[] interfaces,
                                      InvocationHandler h)
    throws IllegalArgumentException
{
    Objects.requireNonNull(h);

    final Class<?>[] intfs = interfaces.clone();
    // Android-removed: SecurityManager calls
    /*
    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
        checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
    }
    */

    /*
     * Look up or generate the designated proxy class.
     */
     // 通过getProxyClass0方法动态创建代理类的Class对象
     // 重点在这里，了解代理类Class对象生成，基本就掌握了Proxy的实现原理
    Class<?> cl = getProxyClass0(loader, intfs);

    /*
     * Invoke its constructor with the designated invocation handler.
     */
    try {
        // Android-removed: SecurityManager / permission checks.
        /*
        if (sm != null) {
            checkNewProxyPermission(Reflection.getCallerClass(), cl);
        }
        */

        final Constructor<?> cons = cl.getConstructor(constructorParams);
        final InvocationHandler ih = h;
        if (!Modifier.isPublic(cl.getModifiers())) {
            // BEGIN Android-changed: Excluded AccessController.doPrivileged call.
            /*
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    cons.setAccessible(true);
                    return null;
                }
            });
            */

            cons.setAccessible(true);
            // END Android-removed: Excluded AccessController.doPrivileged call.
        }
        // 最终通过Constructor的newInstance()方法来反射创建我们的代理对象
        return cons.newInstance(new Object[]{h});
    } catch (IllegalAccessException|InstantiationException e) {
        throw new InternalError(e.toString(), e);
    } catch (InvocationTargetException e) {
        Throwable t = e.getCause();
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new InternalError(t.toString(), t);
        }
    } catch (NoSuchMethodException e) {
        throw new InternalError(e.toString(), e);
    }
}
```

`getProxyClass0`

```java
private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        return proxyClassCache.get(loader, interfaces);
    }
```

最终是通过proxyClassCache.get()来获取的，这里传入了classLoader对象，那么肯定用到反射。而这个proxyClassCache是一个WeakCache类型，可以看到其构造方法传入了一个ProxyClassFactory：

```java
private static final WeakCache<ClassLoader, Class<?>[], Class<?>>
        proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());
```

`ProxyClassFactory`（一个工厂函数，它生成、定义和返回给定 ClassLoader 和接口数组的代理类),里面主要对我们的代理对象进行动态生成

```java
private static final class ProxyClassFactory
    implements BiFunction<ClassLoader, Class<?>[], Class<?>>
{
    // prefix for all proxy class names
    private static final String proxyClassNamePrefix = "$Proxy";

    // next number to use for generation of unique proxy class names
    private static final AtomicLong nextUniqueNumber = new AtomicLong();

    @Override
    public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {

        // ...

        {
            // Android-changed: Generate the proxy directly instead of calling
            // through to ProxyGenerator.
            // Android当前版本放弃了使用JDK中的ProxyGenerator
            // 改用本地的native方法去实现代理类的生成和加载
            List<Method> methods = getMethods(interfaces);
            Collections.sort(methods, ORDER_BY_SIGNATURE_AND_SUBTYPE);
            validateReturnTypes(methods);
            List<Class<?>[]> exceptions = deduplicateAndGetExceptions(methods);

            Method[] methodsArray = methods.toArray(new Method[methods.size()]);
            Class<?>[][] exceptionsArray = exceptions.toArray(new Class<?>[exceptions.size()][]);

            /*
             * Choose a name for the proxy class to generate.
             */
            long num = nextUniqueNumber.getAndIncrement();
            String proxyName = proxyPkg + proxyClassNamePrefix + num;

            // 最终调用本地native方法：generateProxy
            // 它会生成代理对象字节码数据，并通过JVM的类加载机制动态加载到内存当中，这样我们就获得了这个代理类的Class对象
            return generateProxy(proxyName, interfaces, loader, methodsArray,
                                 exceptionsArray);
        }
    }
}
```

#### Retrofit源码

Retrofit同样采用了建造者模式来创建，其中的Retrofit.Builder的构造方法：

```java
public Builder() {
      this(Platform.get());//获得平台方支持
    }
```

##### Platform

很简短，调用了Platform.get()方法，来看看这个get()方法

```java
class Platform {
  private static final Platform PLATFORM = findPlatform();
  
  private final boolean hasJava8Types;
  
  @Nullable
  private final Constructor<MethodHandles.Lookup> lookupConstructor;
  
  static Platform get() {
    return PLATFORM;
  }
  
  private static Platform findPlatform() {
    return "Dalvik".equals(System.getProperty("java.vm.name")) ?  //这里是判断是否为Android平台
      new Android() : 
      new Platform(true);
  }
  
  Platform(boolean hasJava8Types) {
    this.hasJava8Types = hasJava8Types;
    Constructor<MethodHandles.Lookup> lookupConstructor = null;
    if (hasJava8Types)
      try {
        lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(new Class[] { Class.class, int.class });
        lookupConstructor.setAccessible(true);
      } catch (NoClassDefFoundError noClassDefFoundError) {
      
      } catch (NoSuchMethodException noSuchMethodException) {} 
    this.lookupConstructor = lookupConstructor;
  }
  
  @Nullable
  Executor defaultCallbackExecutor() {
    return null;
  }
  
  List<? extends CallAdapter.Factory> defaultCallAdapterFactories(@Nullable Executor callbackExecutor) {
    DefaultCallAdapterFactory executorFactory = new DefaultCallAdapterFactory(callbackExecutor);
    return this.hasJava8Types ? 
      Arrays.<CallAdapter.Factory>asList(new CallAdapter.Factory[] { CompletableFutureCallAdapterFactory.INSTANCE, executorFactory }) : Collections.<CallAdapter.Factory>singletonList(executorFactory);
  }
  
  int defaultCallAdapterFactoriesSize() {
    return this.hasJava8Types ? 2 : 1;
  }
  
  List<? extends Converter.Factory> defaultConverterFactories() {
    return this.hasJava8Types ? Collections.<Converter.Factory>singletonList(OptionalConverterFactory.INSTANCE) : Collections.<Converter.Factory>emptyList();
  }
  
  int defaultConverterFactoriesSize() {
    return this.hasJava8Types ? 1 : 0;
  }
  
  @IgnoreJRERequirement
  boolean isDefaultMethod(Method method) {
    return (this.hasJava8Types && method.isDefault());
  }
  
  @Nullable
  @IgnoreJRERequirement
  Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object object, Object... args) throws Throwable {
    MethodHandles.Lookup lookup = (this.lookupConstructor != null) ? this.lookupConstructor.newInstance(new Object[] { declaringClass, Integer.valueOf(-1) }) : MethodHandles.lookup();
    return lookup.unreflectSpecial(method, declaringClass).bindTo(object).invokeWithArguments(args);
  }
  
  static final class Android extends Platform {
    Android() {
      super((Build.VERSION.SDK_INT >= 24));
    }
    
    public Executor defaultCallbackExecutor() {
      return new MainThreadExecutor();
    }
    
    @Nullable
    Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object object, Object... args) throws Throwable {
      if (Build.VERSION.SDK_INT < 26)
        throw new UnsupportedOperationException("Calling default methods on API 24 and 25 is not supported"); 
      return super.invokeDefaultMethod(method, declaringClass, object, args);
    }
    
    static final class MainThreadExecutor implements Executor {
      private final Handler handler = new Handler(Looper.getMainLooper());  //真正的目的是获得这个
      
      public void execute(Runnable r) {
        this.handler.post(r);
      }
    }
  }
}
```

##### BuilderForRetrofit

只是在这里你还看不出`Platform.get()`的具体作用，只是最终调用了`findPlatform()`方法，而这里面对当前的平台做了一个判断。
来看到这个`Builder`的`build()`

```java
public Retrofit build() {
  if (baseUrl == null) {
  //baseUrl是必须指定的
    throw new IllegalStateException("Base URL required.");
  }

  //默认为this.callFactory
  //就是我们在构建Retrofit时调用callFactory方法所传进来的
  okhttp3.Call.Factory callFactory = this.callFactory;
  if (callFactory == null) {
  //如果没有设置callFactory则直接创建OkHttpClient  
    callFactory = new OkHttpClient();
  }

  Executor callbackExecutor = this.callbackExecutor;
  if (callbackExecutor == null) {
  //获取到的callbackExecutor，它是一个Executor
  //这就很明显了，最终根据平台类型获取到不同的线程池
  //callbackExecutor用来将回调传递到UI线程
    callbackExecutor = platform.defaultCallbackExecutor();
  }
  
  //callAdapterFactories主要用于存储对Call进行转化的对象，后面在Call的创建过程会再次提到它
  List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
  callAdapterFactories.addAll(platform.defaultCallAdapterFactories(callbackExecutor));
  //converterFactories主要用于存储转化数据对象:
  //一般调用的addConverterFactory(GsonConverterFactory.create())，
  //就是设置返回的数据支持转换为Gson对象。最终会返回配置好的Retrofit类。
  List<Converter.Factory> converterFactories =
      new ArrayList<>(
          1 + this.converterFactories.size() + platform.defaultConverterFactoriesSize());

  // ...

  return new Retrofit(
      callFactory,
      baseUrl,
      unmodifiableList(converterFactories),
      unmodifiableList(callAdapterFactories),
      callbackExecutor,
      validateEagerly);
}
```

##### Proxy

这里是动态代理的

```java
public <T> T create(final Class<T> service) {
    validateServiceInterface(service);
    return 
      (T)Proxy.newProxyInstance(service
        .getClassLoader(), new Class[] { service }, new InvocationHandler() {
          private final Platform platform = Platform.get();
          private final Object[] emptyArgs = new Object[0];
          @Nullable
            //代理对象中的方法调用，其实最终都由InvocationHandler的invoke方法来取代
          public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class)//这里是判断是否为Object的方法，例如hasCode还有toString什么的,这些不反射
              return method.invoke(this, args);
            args = (args != null) ? args : this.emptyArgs;
            return this.platform.isDefaultMethod(method) ?//判断是否为default方法
              this.platform.invokeDefaultMethod(method, service, proxy, args) : 
              Retrofit.this.loadServiceMethod(method).invoke(args);//显然是走这,从这里返回我们需要的call
          }
        });
  }
```

##### 得到Call的流程

最后的`loadServiceMethod(method)`中的`method`就是我们定义的`getUserInfoByUid`方法

```java
// HttpServiceMethod是ServiceMethod的具体实现类，所以最终调用的是HttpServiceMethod对象的invoke方法
  ServiceMethod<?> loadServiceMethod(Method method) {
    ServiceMethod<?> result = this.serviceMethodCache.get(method);//缓存当中去取，如果缓存当中没有就去拿
    if (result != null)
      return result; 
    synchronized (this.serviceMethodCache) {
      result = this.serviceMethodCache.get(method);
      if (result == null) {
        result = ServiceMethod.parseAnnotations(this, method);//这里是调用ServiceMethod里的RequestFactory的parseAnnotations
        this.serviceMethodCache.put(method, result);
      } 
    } 
    return result;
  }
```

在这里调用了`ServiceMethod.parseAnnotations(this, method)`,构造`ServiceMethod`

```java
abstract class ServiceMethod<T> {
  static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
    RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);// 获得RequestFactory对象：对注解进行解析获得请求的参数、方法及url等信息，最终交给OkHttpCall来构建Call
    Type returnType = method.getGenericReturnType();
    if (Utils.hasUnresolvableType(returnType))
      throw Utils.methodError(method, "Method return type must not include a type variable or wildcard: %s", new Object[] { returnType }); 
    if (returnType == void.class)
      throw Utils.methodError(method, "Service methods cannot return void.", new Object[0]); 
    return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);//最后调用了HttpServiceMethod
  }
  
  @Nullable
  abstract T invoke(Object[] paramArrayOfObject);
}
```

最终调用了HttpServiceMethod.parseAnnotations()方法，返回HttpServiceMethod对象

```java
  static <ResponseT, ReturnT> HttpServiceMethod<ResponseT, ReturnT> parseAnnotations(Retrofit retrofit, Method method, RequestFactory requestFactory) {
    Type adapterType;
    boolean isKotlinSuspendFunction = requestFactory.isKotlinSuspendFunction;
    boolean continuationWantsResponse = false;
    boolean continuationBodyNullable = false;
    Annotation[] annotations = method.getAnnotations();//这里拿到了method里的注解信息
    if (isKotlinSuspendFunction) {//这里是kotlin相关的一些方法
      Type[] parameterTypes = method.getGenericParameterTypes();
      Type type = Utils.getParameterLowerBound(0, (ParameterizedType)parameterTypes[parameterTypes.length - 1]);
      if (Utils.getRawType(type) == Response.class && type instanceof ParameterizedType) {
        type = Utils.getParameterUpperBound(0, (ParameterizedType)type);
        continuationWantsResponse = true;
      } 
      adapterType = new Utils.ParameterizedTypeImpl(null, Call.class, new Type[] { type });
      annotations = SkipCallbackExecutorImpl.ensurePresent(annotations);
    } else {
      adapterType = method.getGenericReturnType();//这里拿到类型
    } 
    // 返回一个CallAdapted，而这个CallAdapted传入了我们刚才创建的callAdapter
    CallAdapter<ResponseT, ReturnT> callAdapter = createCallAdapter(retrofit, method, adapterType, annotations);//这里最终会得到我们在构建Retrofit调用CallAdapterFactories添加的对象的get方法
    Type responseType = callAdapter.responseType();
    if (responseType == Response.class)
      throw Utils.methodError(method, "'" + 
          
          Utils.getRawType(responseType).getName() + "' is not a valid response body type. Did you mean ResponseBody?", new Object[0]); 
    if (responseType == Response.class)
      throw Utils.methodError(method, "Response must include generic type (e.g., Response<String>)", new Object[0]); 
    if (requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(responseType))
      throw Utils.methodError(method, "HEAD method must use Void as response type.", new Object[0]); 
    Converter<ResponseBody, ResponseT> responseConverter = createResponseConverter(retrofit, method, responseType);//这里也是拿到某不为人知的Converter(
    Call.Factory callFactory = retrofit.callFactory;
    if (!isKotlinSuspendFunction)
      return new CallAdapted<>(requestFactory, callFactory, responseConverter, callAdapter);//这里就返回一个CallAdapted:HttpServiceMethod
    if (continuationWantsResponse)
      return (HttpServiceMethod)new SuspendForResponse<>(requestFactory, callFactory, responseConverter, (CallAdapter)callAdapter);
    return (HttpServiceMethod)new SuspendForBody<>(requestFactory, callFactory, responseConverter, (CallAdapter)callAdapter, continuationBodyNullable);
  }
```

这里分别获得了`requestFactory`, `callFactory`, `responseConverter`, `callAdapter`

`requstFactory`是之前创建的然后传进来

`callFactory`是传进来的`retrofit.callFactory`

`responseConverter`是通过`createResponseConverter`进行创建的

`callAdapter`是通过`createCallAdapter`进行创建的

然后返回了一个new的CallAdapted,它是HttpServiceMethod的内部类，并继承了HttpServiceMethod

```java
  protected abstract ReturnT adapt(Call<ResponseT> paramCall, Object[] paramArrayOfObject);
  
  static final class CallAdapted<ResponseT, ReturnT> extends HttpServiceMethod<ResponseT, ReturnT> {
    private final CallAdapter<ResponseT, ReturnT> callAdapter;
    // 由构造方法传入了我们的CallAdapter
    CallAdapted(RequestFactory requestFactory, Call.Factory callFactory, Converter<ResponseBody, ResponseT> responseConverter, CallAdapter<ResponseT, ReturnT> callAdapter) {
      super(requestFactory, callFactory, responseConverter);
      this.callAdapter = callAdapter;
    }
    //实际上是调用了callAdapter.adapt(call)
    protected ReturnT adapt(Call<ResponseT> call, Object[] args) {
      return this.callAdapter.adapt(call);
    }
  }
```

至此，关于loadServiceMethod方法的分析到此也差不多了。最终获得到的是一个继承了HttpServiceMethod的CallAdapted，而HttpServiceMethod又是ServiceMethod接口的实现类。

回到最初的InvocationHandler的invoke()方法,发现调用的是

```java
 @Nullable
  final ReturnT invoke(Object[] args) {
    // 创建OkHttpCall，继承了Call
    Call<ResponseT> call = new OkHttpCall<>(this.requestFactory, args, this.callFactory, this.responseConverter);
    // 调用了下面的adapt方法，实际是由CallAdapted来实现了这个方法并被调用
    return adapt(call, args);
  }
  // 找到实现了这个方法的类CallAdapted
  @Nullable
  protected abstract ReturnT adapt(Call<ResponseT> paramCall, Object[] paramArrayOfObject);
```

可以看到这个`invoke`方法创建了我们想要的`Call`，然后交给`CallAdapted`的`adapt`方法来处理，回看上面的分析，`CallAdapted`又交给它构造方法传入进来的`CallAdapter`来处理，调用了`CallAdapter`的`adapt`方法，最终返回这个`OkHttpCall`。

继续分析`OkHttpCall`这个类，它属于`Retrofit`框架，但它继承了`OkHttp`中的`Call`，最后其实还是交给了`OkHttp`去创建`Call`请求去了。

```java
private Call createRawCall() throws IOException {
    //CallFactory是okhttp3.Call.Factory
    //在Retrofit创建过程中就已经创建或设置了callFactory，这个callFactory就是传入的或者创建的OkHttpClient
    //requestFactory.create(args)返回okhttp3.Request
    //RequestFactory是对代理接口的方法及其注解进行解析的工厂类，获得对应的参数、请求method、url等信息构建okhttp3.Request
    Call call = this.callFactory.newCall(this.requestFactory.create(this.args));
    if (call == null)
      throw new NullPointerException("Call.Factory returned null.");
    //最终返回的始终是一个okhttp3.Call
    return call;
  }
```

`createRawCall`的目的其实就是去创建`okhtt3.Call`。另外一个值得去了解的方法`parseResponse()`

```java
  Response<T> parseResponse(Response rawResponse) throws IOException {
    ResponseBody rawBody = rawResponse.body();
    rawResponse = rawResponse.newBuilder().body(new NoContentResponseBody(rawBody.contentType(), rawBody.contentLength())).build();
    int code = rawResponse.code();
    if (code < 200 || code >= 300)
      try {
        ResponseBody bufferedBody = Utils.buffer(rawBody);
        return (Response)Response.error(bufferedBody, rawResponse);
      } finally {
        rawBody.close();
      }  
    if (code == 204 || code == 205) {
      rawBody.close();
      return Response.success((T)null, rawResponse);
    } 
    ExceptionCatchingResponseBody catchingBody = new ExceptionCatchingResponseBody(rawBody);
    try {
      // 最终在这里通过Converter来实现返回数据的类型转化
      T body = this.responseConverter.convert(catchingBody);
      // 最终封装成Retrofit的Response返回
      return Response.success(body, rawResponse);
    } catch (RuntimeException e) {
      catchingBody.throwIfCaught();
      throw e;
    } 
  }
```



回过头来看看之前的中间过程

##### RequestFactory

在`ServiceMethod`中调用了`RequestFactory.parseAnnotations(retrofit, method)`

```java
static RequestFactory parseAnnotations(Retrofit retrofit, Method method) {
    return (new Builder(retrofit, method)).build();
  }
```

老建造者模式了

```java
Builder(Retrofit retrofit, Method method) {
      this.retrofit = retrofit;
      this.method = method;
      this.methodAnnotations = method.getAnnotations();//获取所有的注解，包括自己声明的以及继承的
      this.parameterTypes = method.getGenericParameterTypes(); //获取到方法的参数列表
      this.parameterAnnotationsArray = method.getParameterAnnotations();//得到参数的注解列表
    }
```

然后直接看`build()`

```java
RequestFactory build() {
      for (Annotation annotation : this.methodAnnotations)
        parseMethodAnnotation(annotation);//解析方法的注解
      }
      int parameterCount = this.parameterAnnotationsArray.length;//对参数和参数注解操作
      this.parameterHandlers = (ParameterHandler<?>[])new ParameterHandler[parameterCount];
      for (int p = 0, lastParameter = parameterCount - 1; p < parameterCount; p++)
        this.parameterHandlers[p] = parseParameter(p, this.parameterTypes[p], this.parameterAnnotationsArray[p], (p == lastParameter)); 
      return new RequestFactory(this);
    }
```

###### annotation

先来看`parseMethodAnnotation`

```java
private void parseMethodAnnotation(Annotation annotation) {  //对注解头的分析
      if (annotation instanceof DELETE) {
        parseHttpMethodAndPath("DELETE", ((DELETE)annotation).value(), false);
      } else if (annotation instanceof GET) {
        parseHttpMethodAndPath("GET", ((GET)annotation).value(), false);
      } else if (annotation instanceof HEAD) {
        parseHttpMethodAndPath("HEAD", ((HEAD)annotation).value(), false);
      } else if (annotation instanceof PATCH) {
        parseHttpMethodAndPath("PATCH", ((PATCH)annotation).value(), true);
      } else if (annotation instanceof POST) {
        parseHttpMethodAndPath("POST", ((POST)annotation).value(), true);
      } else if (annotation instanceof PUT) {
        parseHttpMethodAndPath("PUT", ((PUT)annotation).value(), true);
      } else if (annotation instanceof OPTIONS) {
        parseHttpMethodAndPath("OPTIONS", ((OPTIONS)annotation).value(), false);
      } else if (annotation instanceof HTTP) {
        HTTP http = (HTTP)annotation;
        parseHttpMethodAndPath(http.method(), http.path(), http.hasBody());
      } else if (annotation instanceof Headers) {
        String[] headersToParse = ((Headers)annotation).value();
        if (headersToParse.length == 0)
          throw Utils.methodError(this.method, "@Headers annotation is empty.", new Object[0]); 
        this.headers = parseHeaders(headersToParse);
      } else if (annotation instanceof retrofit2.http.Multipart) {
        if (this.isFormEncoded)
          throw Utils.methodError(this.method, "Only one encoding annotation is allowed.", new Object[0]); 
        this.isMultipart = true;
      } else if (annotation instanceof retrofit2.http.FormUrlEncoded) {
        if (this.isMultipart)
          throw Utils.methodError(this.method, "Only one encoding annotation is allowed.", new Object[0]); 
        this.isFormEncoded = true;
      } 
    }
```

这里就是把注解和对应的约定的字符比对，然后交给`parseHttpMethodAndPath`拼接

他这里的是用的fori进行的`parseMethodAnnotation`，但设置的方式是设置通过设置`Builder`的变量，所以说实际上只有一种请求方式的注解被使用

```java
private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
      if (this.httpMethod != null)//这里写了报错的判断
        throw Utils.methodError(this.method, "Only one HTTP method is allowed. Found: %s and %s.", new Object[] { this.httpMethod, httpMethod }); 
      this.httpMethod = httpMethod;
      this.hasBody = hasBody;
      if (value.isEmpty())
        return; 
      int question = value.indexOf('?');
      if (question != -1 && question < value.length() - 1) {
        String queryParams = value.substring(question + 1);
        Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
        if (queryParamMatcher.find())
          throw Utils.methodError(this.method, "URL query string \"%s\" must not have replace block. For dynamic query parameters use @Query.", new Object[] { queryParams }); 
      } 
      this.relativeUrl = value;
      this.relativeUrlParamNames = parsePathParameters(value);
    }
```

`parsePathParameters`中写了拼接的内容，这里就不讲了

###### parameterHandlers

这里是通过`parseParameter`方法来解析

```java
private ParameterHandler<?> parseParameter(int p, Type parameterType, @Nullable Annotation[] annotations, boolean allowContinuation) {
      ParameterHandler<?> result = null;
      if (annotations != null)
        for (Annotation annotation : annotations) {
          ParameterHandler<?> annotationAction = parseParameterAnnotation(p, parameterType, annotations, annotation);
          if (annotationAction != null) {
            if (result != null)
              throw Utils.parameterError(this.method, p, "Multiple Retrofit annotations found, only one allowed.", new Object[0]); 
            result = annotationAction;
          } 
        }  
      if (result == null) {
        if (allowContinuation)
          try {
            if (Utils.getRawType(parameterType) == Continuation.class) {
              this.isKotlinSuspendFunction = true;
              return null;
            } 
          } catch (NoClassDefFoundError noClassDefFoundError) {} 
        throw Utils.parameterError(this.method, p, "No Retrofit annotation found.", new Object[0]);
      } 
      return result;
    }
```

这里主要是`parseParameterAnnotation`解析

在`parseParameterAnnotation`中，把每一个的type类型进行对比，分别操作，最后都返回一个`ParameterHandler`的内部类，这个内部类也是继承了`ParameterHandler`，其中也有对`converterFactories`的部分操作和支持(`stringConverter`)

最后在`RequestFactory.create`中

```java
Request create(Object[] args) throws IOException {
    ParameterHandler<?>[] arrayOfParameterHandler = this.parameterHandlers;
    int argumentCount = args.length;
    if (argumentCount != arrayOfParameterHandler.length)
      throw new IllegalArgumentException("Argument count (" + argumentCount + ") doesn't match expected count (" + arrayOfParameterHandler.length + ")");
    //这里这里在构建requestBuilder
    RequestBuilder requestBuilder = new RequestBuilder(this.httpMethod, this.baseUrl, this.relativeUrl, this.headers, this.contentType, this.hasBody, this.isFormEncoded, this.isMultipart);
    if (this.isKotlinSuspendFunction)
      argumentCount--; 
    List<Object> argumentList = new ArrayList(argumentCount);
    for (int p = 0; p < argumentCount; p++) {
      argumentList.add(args[p]);
      arrayOfParameterHandler[p].apply(requestBuilder, args[p]);
    } 
    return requestBuilder.get().tag(Invocation.class, new Invocation(this.method, argumentList)).build();
  }
```

先构建`RequestBuilder`,在`RequestBuilder`中一堆`Builder`，你们自己看(我觉得也没有人去看

之后里面有句`arrayOfParameterHandler[p].apply(requestBuilder, args[p]);`

通过这个将之前的参数注释,例如`Query`之类的，然后在不同类型的`Handler`内`apply`有不同的实现，这些实现分别调用`requestBuilder`中的方法来对`request`进行配置

###### RequestBuilder

最后来看看怎么返回Request的

`return requestBuilder.get().tag(Invocation.class, new Invocation(this.method, argumentList)).build();`

点开`get`方法

```java
Request.Builder get() {
    HttpUrl url;
    HttpUrl.Builder urlBuilder = this.urlBuilder;
    if (urlBuilder != null) {
      url = urlBuilder.build();
    } else {
      url = this.baseUrl.resolve(this.relativeUrl);
      if (url == null)
        throw new IllegalArgumentException("Malformed URL. Base: " + this.baseUrl + ", Relative: " + this.relativeUrl); 
    } 
    RequestBody body = this.body;
    if (body == null)
      if (this.formBuilder != null) {
        FormBody formBody = this.formBuilder.build();
      } else if (this.multipartBuilder != null) {
        MultipartBody multipartBody = this.multipartBuilder.build();
      } else if (this.hasBody) {
        body = RequestBody.create(null, new byte[0]);
      }  
    MediaType contentType = this.contentType;
    if (contentType != null)
      if (body != null) {
        body = new ContentTypeOverridingRequestBody(body, contentType);
      } else {
        this.headersBuilder.add("Content-Type", contentType.toString());
      }  
    return this.requestBuilder.url(url).headers(this.headersBuilder.build()).method(this.method, body);
  }
```

这里是将不同部分的东西拼接起来返回一个`Request.Builder`

在tag中把东西塞进去

```java
public <T> Builder tag(Class<? super T> type, @Nullable T tag) {
      if (type == null) throw new NullPointerException("type == null");

      if (tag == null) {
        tags.remove(type);
      } else {
        if (tags.isEmpty()) tags = new LinkedHashMap<>();
        tags.put(type, type.cast(tag));
      }
      return this;
    }
```

这里是传入的`Invocation`

```java
public final class Invocation {
  private final Method method;
  
  private final List<?> arguments;
  
  public static Invocation of(Method method, List<?> arguments) {
    Objects.requireNonNull(method, "method == null");
    Objects.requireNonNull(arguments, "arguments == null");
    return new Invocation(method, new ArrayList(arguments));
  }
  
  Invocation(Method method, List<?> arguments) {
    this.method = method;
    this.arguments = Collections.unmodifiableList(arguments);
  }
  
  public Method method() {
    return this.method;
  }
  
  public List<?> arguments() {
    return this.arguments;
  }
  
  public String toString() {
    return String.format("%s.%s() %s", new Object[] { this.method
          .getDeclaringClass().getName(), this.method.getName(), this.arguments });
  }
}
```

最后`build()`新建`Request`



以上就是`RequestFactory`的过程,对`Request`进行创建，对Request的配置内容很复杂

##### createCallAdapter

~~2021年7月22日11:50:36(我人已经写麻了)~~

这里的`createCallAdapter`是直接调用了了`retrofit`的`callAdapter`返回的`CallAdapter`

```java
public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
    return nextCallAdapter(null, returnType, annotations);
  }
```

进一步发现调用了`nextCallAdapter`

```java
public CallAdapter<?, ?> nextCallAdapter(@Nullable CallAdapter.Factory skipPast, Type returnType, Annotation[] annotations) {
    Objects.requireNonNull(returnType, "returnType == null");
    Objects.requireNonNull(annotations, "annotations == null");
    int start = this.callAdapterFactories.indexOf(skipPast) + 1;
    for (int i = start, count = this.callAdapterFactories.size(); i < count; i++) {
      CallAdapter<?, ?> adapter = ((CallAdapter.Factory)this.callAdapterFactories.get(i)).get(returnType, annotations, this);//这里就是把factories的东西放Call，规定他
      if (adapter != null)
        return adapter;
    } 
    StringBuilder builder = (new StringBuilder("Could not locate call adapter for ")).append(returnType).append(".\n");
    if (skipPast != null) {
      builder.append("  Skipped:");
      for (int m = 0; m < start; m++)
        builder.append("\n   * ").append(((CallAdapter.Factory)this.callAdapterFactories.get(m)).getClass().getName()); 
      builder.append('\n');
    } 
    builder.append("  Tried:");
    for (int j = start, k = this.callAdapterFactories.size(); j < k; j++)
      builder.append("\n   * ").append(((CallAdapter.Factory)this.callAdapterFactories.get(j)).getClass().getName()); 
    throw new IllegalArgumentException(builder.toString());
  }
```

实则上是调用了`retrofit`中的`callAdapterFactories`来get`CallAdapter`

我们来追溯一下`callAdapterFactories`中的`CallAdapter`是在哪里添加的

在`Retrofit.Builder`中可以发现`addCallAdapterFactory`方法可以添加`CallAdapter`这个就是外部拓展添加的`CallAdapter`

在`Retrofit.Builder().build()`中有

```java
List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);//对CallAdapter进行了初始化
      callAdapterFactories.addAll(this.platform.defaultCallAdapterFactories(callbackExecutor));
```

这里是先加入`addCallAdapterFactory`添加的`CallAdapter`然后在添加一个`defaultCallAdapter`

可以从代码里面开出，这个`defaultCallAdapter`是从`platform`中得到的

```java
  List<? extends CallAdapter.Factory> defaultCallAdapterFactories(@Nullable Executor callbackExecutor) {
    DefaultCallAdapterFactory executorFactory = new DefaultCallAdapterFactory(callbackExecutor);
    return this.hasJava8Types ? 
      Arrays.<CallAdapter.Factory>asList(new CallAdapter.Factory[] { CompletableFutureCallAdapterFactory.INSTANCE, executorFactory }) : Collections.<CallAdapter.Factory>singletonList(executorFactory);
  }
```

当`Build.VERSION.SDK_INT >= 24` 时，会再使用添加`CompletableFutureCallAdapterFactory`，该工程主要是将Call< ?>转化为CompletableFuture<?>

我们这里分析`DefaultCallAdapterFactory`

```java
final class DefaultCallAdapterFactory extends CallAdapter.Factory {
  @Nullable
  private final Executor callbackExecutor;
  
  DefaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
    this.callbackExecutor = callbackExecutor;
  }
  
  @Nullable
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {//这里调用get
    if (getRawType(returnType) != Call.class)
      return null; 
    if (!(returnType instanceof ParameterizedType))
      throw new IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>"); 
    final Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType)returnType);
    final Executor executor = Utils.isAnnotationPresent(annotations, (Class)SkipCallbackExecutor.class) ? null : this.callbackExecutor;

    return new CallAdapter<Object, Call<?>>() {//这里的返回值
        public Type responseType() {
          return responseType;
        }
        
        public Call<Object> adapt(Call<Object> call) {//和CallAdapter接口对应
          return (executor == null) ? call : new DefaultCallAdapterFactory.ExecutorCallbackCall(executor, call);//这个返回值，最后到了下面的Call<T>的代理，把所有的操作都进行了
        }
      };
  }
  
  static final class ExecutorCallbackCall<T> implements Call<T> {//这里是使用了代理
    final Executor callbackExecutor;
    
    final Call<T> delegate;
    
    ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
      this.callbackExecutor = callbackExecutor;
      this.delegate = delegate;
    }
    //这里是自定义的Call,使用了delegate代理
    public void enqueue(final Callback<T> callback) {
      Objects.requireNonNull(callback, "callback == null");
      this.delegate.enqueue(new Callback<T>() {
            public void onResponse(Call<T> call, Response<T> response) {
              DefaultCallAdapterFactory.ExecutorCallbackCall.this.callbackExecutor.execute(() -> {//callbackExecutor
                    if (DefaultCallAdapterFactory.ExecutorCallbackCall.this.delegate.isCanceled()) {
                      callback.onFailure(DefaultCallAdapterFactory.ExecutorCallbackCall.this, new IOException("Canceled"));//这里就是回调的地方
                    } else {
                      callback.onResponse(DefaultCallAdapterFactory.ExecutorCallbackCall.this, response);
                    } 
                  });
            }
            
            public void onFailure(Call<T> call, Throwable t) {
              DefaultCallAdapterFactory.ExecutorCallbackCall.this.callbackExecutor.execute(() -> callback.onFailure(DefaultCallAdapterFactory.ExecutorCallbackCall.this, t));
            }
          });
    }
    
    public boolean isExecuted() {
      return this.delegate.isExecuted();
    }
    
    public Response<T> execute() throws IOException {
      return this.delegate.execute();
    }
    
    public void cancel() {
      this.delegate.cancel();
    }
    
    public boolean isCanceled() {
      return this.delegate.isCanceled();
    }
    
    public Call<T> clone() {
      return new ExecutorCallbackCall(this.callbackExecutor, this.delegate.clone());
    }
    
    public Request request() {
      return this.delegate.request();
    }
    
    public Timeout timeout() {
      return this.delegate.timeout();
    }
  }
}
```



##### createResponseConverter

`ResponseConverter`和`CallAdapter`几乎一样

```java
converterFactories.add(new BuiltInConverters());
      converterFactories.addAll(this.converterFactories);
      converterFactories.addAll(this.platform.defaultConverterFactories());
```

`BuiltInConverters()`提供了基本的`Converters`都是在`Converter`接口的实现类，建议自己看

在`Platform`中，`defaultConverterFactories`方法会返回一个`OptionalConverter`

这个`OptionalConverter`没有进行其他啥的操作，就是用代理走了个流程，实际上调用了`responseBodyConverter`,然后到了`BuiltInConverters()`中的`responseBodyConverter`,最后返回了`buffer(value)`



差不多`retrofit`的内容就结束了

## OkHttp

阅读`OkHttp`没有用最新版，因为Kotlin版本的太抽象了,花眼睛

OkHttp就不说咋使用了(

因为目前能力相当有限，缺少很多计网的前置知识，所以只能讲一点点思路的东西



OkHttp仍然是用的建造者模式，里面有很多的配置俺也说不明白

```java
public class OkHttpClient {
    //...
    public static final class Builder {
        Dispatcher dispatcher; // 调度器 线程、请求
        /**
         * 代理类，默认有三种代理模式DIRECT(直连),HTTP（http代理）,SOCKS（socks代理）
         */
        @Nullable Proxy proxy;
        /**
         * 协议集合，协议类，用来表示使用的协议版本，比如`http/1.0,`http/1.1,`spdy/3.1,`h2等
         */
        List<Protocol> protocols;
        /**
         * 连接规范，用于配置Socket连接层。对于HTTPS，还能配置安全传输层协议（TLS）版本和密码套件
         */
        List<ConnectionSpec> connectionSpecs;
        // 拦截器
        final List<Interceptor> interceptors = new ArrayList<>();
        final List<Interceptor> networkInterceptors = new ArrayList<>();
        EventListener.Factory eventListenerFactory;
        /**
         * 代理选择类，默认不使用代理，即使用直连方式，当然，我们可以自定义配置，
         * 以指定URI使用某种代理，类似代理软件的PAC功能
         */
        ProxySelector proxySelector;
        // Cookie的保存获取
        CookieJar cookieJar;
        /**
         * 缓存类，内部使用了DiskLruCache来进行管理缓存，匹配缓存的机制不仅仅是根据url，
         * 而且会根据请求方法和请求头来验证是否可以响应缓存。此外，仅支持GET请求的缓存
         */
        @Nullable Cache cache;
        // 内置缓存
        @Nullable InternalCache internalCache;
        // Socket的抽象创建工厂，通过createSocket来创建Socket
        SocketFactory socketFactory;
        /**
         * 安全套接层工厂，HTTPS相关，用于创建SSLSocket。一般配置HTTPS证书信任问题都需要从这里着手。
         * 对于不受信任的证书一般会提示
         * javax.net.ssl.SSLHandshakeException异常。
         */
        @Nullable SSLSocketFactory sslSocketFactory;
        /**
         * 证书链清洁器，HTTPS相关，用于从[Java]的TLS API构建的原始数组中统计有效的证书链，
         * 然后清除跟TLS握手不相关的证书，提取可信任的证书以便可以受益于证书锁机制。
         */
        @Nullable CertificateChainCleaner certificateChainCleaner;
        /**
         * 主机名验证器，与HTTPS中的SSL相关，当握手时如果URL的主机名
         * 不是可识别的主机，就会要求进行主机名验证
         */
        HostnameVerifier hostnameVerifier;
        /**
         * 证书锁，HTTPS相关，用于约束哪些证书可以被信任，可以防止一些已知或未知
         * 的中间证书机构带来的攻击行为。如果所有证书都不被信任将抛出SSLPeerUnverifiedException异常。
         */
        CertificatePinner certificatePinner;
        /**
         * 身份认证器，当连接提示未授权时，可以通过重新设置请求头来响应一个
         * 新的Request。状态码401表示远程服务器请求授权，407表示代理服务器请求授权。
         * 该认证器在需要时会被RetryAndFollowUpInterceptor触发。
         */
        Authenticator proxyAuthenticator;
        Authenticator authenticator;
        /**
         * 连接池
         *
         * 我们通常将一个客户端和服务端和连接抽象为一个 connection，
         * 而每一个 connection 都会被存放在 connectionPool 中，由它进行统一的管理，
         * 例如有一个相同的 http 请求产生时，connection 就可以得到复用
         */
        ConnectionPool connectionPool;
        // 域名解析系统
        Dns dns;
        // 是否遵循SSL重定向
        boolean followSslRedirects;
        // 是否重定向
        boolean followRedirects;
        // 失败是否重新连接
        boolean retryOnConnectionFailure;
        // 回调超时
        int callTimeout;
        // 连接超时
        int connectTimeout;
        // 读取超时
        int readTimeout;
        // 写入超时
        int writeTimeout;
        // 与WebSocket有关，为了保持长连接，我们必须间隔一段时间发送一个ping指令进行保活；
        int pingInterval;

        public Builder() {
            dispatcher = new Dispatcher();

            protocols = DEFAULT_PROTOCOLS;

            connectionSpecs = DEFAULT_CONNECTION_SPECS;
            eventListenerFactory = EventListener.factory(EventListener.NONE);
            /**
             * 代理选择类，默认不使用代理，即使用直连方式，当然，我们可以自定义配置，
             * 以指定URI使用某种代理，类似代理软件的PAC功能
             */
            proxySelector = ProxySelector.getDefault();
            if (proxySelector == null) {
                proxySelector = new NullProxySelector();
            }
            cookieJar = CookieJar.NO_COOKIES;
            socketFactory = SocketFactory.getDefault();
            hostnameVerifier = OkHostnameVerifier.INSTANCE;
            certificatePinner = CertificatePinner.DEFAULT;
            proxyAuthenticator = Authenticator.NONE;
            authenticator = Authenticator.NONE;
            connectionPool = new ConnectionPool();
            dns = Dns.SYSTEM;
            followSslRedirects = true;
            followRedirects = true;
            retryOnConnectionFailure = true;
            callTimeout = 0;
            connectTimeout = 10_000;
            readTimeout = 10_000;
            writeTimeout = 10_000;
            pingInterval = 0;
        }

	}
	//...
}
```



```java
  @Override public Call newCall(Request request) {
    return RealCall.newRealCall(this, request, false /* for web socket */);
  }
```

这里就是`client.newCall()`的地方

直接开饭

进入`RealCall`

```java
static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
    // Safely publish the Call instance to the EventListener.
    RealCall call = new RealCall(client, originalRequest, forWebSocket);
    call.transmitter = new Transmitter(client, call);
    return call;
  }
```

嗯哼？ 这就完了??

(个人水平有限，确实是只能这个样子了)

当Call被创建出来之后

有两个执行的方法，一个是同步，一个是异步

##### Call执行

```java
  @Override public Response execute() throws IOException {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    transmitter.timeoutEnter();
    transmitter.callStart();
    try {
      client.dispatcher().executed(this);
      return getResponseWithInterceptorChain();
    } finally {
      client.dispatcher().finished(this);
    }
  }
```

这里其实是在调用`transmitter`来执行,真正的执行者是`transmitter`

```java
client.dispatcher().executed(this);
```

这里是使用线程调度器来

现在来看看线程调度器

```java
public final class Dispatcher {
  private int maxRequests = 64;
  private int maxRequestsPerHost = 5;
  private @Nullable Runnable idleCallback;

  /** Executes calls. Created lazily. */
  private @Nullable ExecutorService executorService;

  /** Ready async calls in the order they'll be run. */
  private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

  /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
  private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

  /** Running synchronous calls. Includes canceled calls that haven't finished yet. */
  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

  public Dispatcher(ExecutorService executorService) {
    this.executorService = executorService;
  }
```

这里有三个队列

- `readyAsyncCalls` // 准备执行的异步请求队列
- `runningAsyncCalls` // 正在运行的异步请求队列
- `runningSyncCalls` // 正在运行的同步请求队列



这里先来同步的

```java
synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
  }
```

 将其加入队列之后

在`RealCall`中的`execute()`执行下一行`getResponseWithInterceptorChain();`

这里涉及到下一个重要的地方，反正这里是拿到服务器的响应

先来把异步的解决掉

- `readyAsyncCalls` // 准备执行的异步请求队列
- `runningAsyncCalls` // 正在运行的异步请求队列

```java
  @Override public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    transmitter.callStart();
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
  }
```

最后调用了调度器的`enqueue()`

```java
void enqueue(AsyncCall call) {
    synchronized (this) {
      readyAsyncCalls.add(call);

      // Mutate the AsyncCall so that it shares the AtomicInteger of an existing running call to
      // the same host.
      if (!call.get().forWebSocket) {
        AsyncCall existingCall = findExistingCallWithHost(call.host());
        if (existingCall != null) call.reuseCallsPerHostFrom(existingCall);
      }
    }
    promoteAndExecute();
  }

  @Nullable private AsyncCall findExistingCallWithHost(String host) {
    for (AsyncCall existingCall : runningAsyncCalls) {
      if (existingCall.host().equals(host)) return existingCall;
    }
    for (AsyncCall existingCall : readyAsyncCalls) {
      if (existingCall.host().equals(host)) return existingCall;
    }
    return null;
  }
```

这里在上面调用了`findExistingCallWithHost`来看看这个请求的主机现在还有没有连接，可以直接调用的，有的话就直接设置进去



最后的重点是`promoteAndExecute();`

```java
private boolean promoteAndExecute() {
  // 断言当前线程已经保持一个指定的锁：assert Thread.holdsLock(obj);
  assert (!Thread.holdsLock(this));

  List<AsyncCall> executableCalls = new ArrayList<>();
  boolean isRunning;
  synchronized (this) {
    for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {//迭代器将`readyAsyncCalls` 加入 `runningAsyncCalls`
      AsyncCall asyncCall = i.next();

      // 大于允许的同时最大请求数
      if (runningAsyncCalls.size() >= maxRequests) break; // Max capacity.
      // 大于同一主机同时最大访问数
      if (asyncCall.callsPerHost().get() >= maxRequestsPerHost) continue; // Host max capacity.

      i.remove();
      // 当前主机的访问数AtomicInteger递增1
      asyncCall.callsPerHost().incrementAndGet();
      // 满足条件的异步请求加入到可执行任务的临时集合，用于下面的遍历执行
      executableCalls.add(asyncCall);
      // 满足条件的一步请求任务加入到runningAsyncCalls中，用于后续的操作
      runningAsyncCalls.add(asyncCall);
    }
    isRunning = runningCallsCount() > 0;
  }

  for (int i = 0, size = executableCalls.size(); i < size; i++) {
    // 遍历可执行的任务，通过excuteOn，加入到线程池中去excute
    AsyncCall asyncCall = executableCalls.get(i);
    asyncCall.executeOn(executorService());
  }

  return isRunning;
}
```

在`asyncCall.executeOn(executorService());`中通过`executorService.execute(this); `来执行任务

就是调用了`execute(this)`执行AsyncCall的execute(),

```java
@Override protected void execute() {   //最后到这来执行了
      boolean signalledCallback = false;
      transmitter.timeoutEnter();
      try {
        Response response = getResponseWithInterceptorChain();     //这一步是真正的请求拿到服务器的响应
        signalledCallback = true;
        responseCallback.onResponse(RealCall.this, response);  //将结果回调出去
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          responseCallback.onFailure(RealCall.this, e);
        }
      } catch (Throwable t) {
        cancel();
        if (!signalledCallback) {
          IOException canceledException = new IOException("canceled due to " + t);
          canceledException.addSuppressed(t);
          responseCallback.onFailure(RealCall.this, canceledException);
        }
        throw t;
      } finally {
        client.dispatcher().finished(this);  //然后又回去了
      }
    }
```

这里同样是调用了`getResponseWithInterceptorChain()`请求得到响应

发现最后有一个`client.dispatcher().finished(this);`

```java
void finished(AsyncCall call) {
    call.callsPerHost().decrementAndGet(); //这里就是host--
    finished(runningAsyncCalls, call);
  }
```

```java
private <T> void finished(Deque<T> calls, T call) {
    Runnable idleCallback;
    synchronized (this) {
      if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
      idleCallback = this.idleCallback;
    }

    boolean isRunning = promoteAndExecute(); //这里再次调用

    if (!isRunning && idleCallback != null) {
      idleCallback.run();
    }
  }
```

这里是在干嘛呢

最后又回到了`promoteAndExecute()`形成了一个循环



异步的请求被保存到了两个，一个`readyAsyncCalls`一个`runningAsynCalls`

这里是一个双队列的操作，如果只有一个队列，那么只有让线程池去一直while(true)去访问队列，但是这里一个等候队列，一个执行队列的话，让线程池不再一直while(true)，提高了操作空间 

这里的关键就是`promoteAndExcute`，然后来控制线程池，如果没有新的请求的话，循环链就会断开，read有一个请求丢进running，就让线程池去捞一个running,这个是热循环?  这里达到了资源的最优节省

##### 责任链模式及拦截器

还记得刚才一直没说的`getResponseWithInterceptorChain()`吗

现在就来看看

###### 责任链模式

先介绍一下责任链模式

```java
public class MyResponse {
    String data;

    public MyResponse(String data) {
        this.data = data;
    }
}
```

```java
public abstract class Chain {
        protected Chain nextChain;

    public void setNextChain(Chain nextChain) {
        this.nextChain = nextChain;
    }
    public abstract MyResponse handleRequest(String args);
}
```

```java
public class ChainTest {
    public static void main(String[] args) {
        Chain chain1 = new Chain1();
        Chain chain2 = new Chain1();
        Chain chain3 = new Chain1();
        chain1.setNextChain(chain2);
        chain2.setNextChain(chain3);
        MyResponse myResponse = chain1.handleRequest("111");
        System.out.println(myResponse.data);

    }
    static class Chain1  extends Chain{

        @Override
        public MyResponse handleRequest(String args) {
            //这里处理
            args = args +"Chain+1";
            if (nextChain != null){
                return nextChain.handleRequest(args);
            }
            return new MyResponse(args);
        }
    }
    static class Chain2  extends Chain{

        @Override
        public MyResponse handleRequest(String args) {
            if (nextChain != null){
                return nextChain.handleRequest(args);
            }
            return new MyResponse(args);
        }
    }
    static class Chain3  extends Chain{

        @Override
        public MyResponse handleRequest(String args) {
            if (nextChain != null){
                return nextChain.handleRequest(args);
            }
            return new MyResponse(args);
        }
    }
}
```

大概意思就是说像锁链一样，把下一个任务的启动点放在上一个任务执行完之后，这样的话，第一个任务执行之后，后面的任务就能串起来

那么丢进去的对象，在责任链里面要走两次，一次进去执行一次返回

###### 拦截器+责任链

```java
public interface Interceptor {
  Response intercept(Chain chain) throws IOException;

  interface Chain {
    //获得Request
    Request request();
	//推进责任链上的任务
    Response proceed(Request request) throws IOException;

    /**
     * Returns the connection the request will be executed on. This is only available in the chains
     * of network interceptors; for application interceptors this is always null.
     */
    @Nullable Connection connection();

    Call call();

    int connectTimeoutMillis();

    Chain withConnectTimeout(int timeout, TimeUnit unit);

    int readTimeoutMillis();

    Chain withReadTimeout(int timeout, TimeUnit unit);

    int writeTimeoutMillis();

    Chain withWriteTimeout(int timeout, TimeUnit unit);
  }
}
```

简单的来分析一下

```java
	@Override public Response proceed(Request request) throws IOException {
    return proceed(request, transmitter, exchange);
  }
  public Response proceed(Request request, Transmitter transmitter, @Nullable Exchange exchange) throws IOException {
    calls++;
    RealInterceptorChain next = new RealInterceptorChain(interceptors, transmitter, exchange,index + 1, request, call, connectTimeout, readTimeout, writeTimeout);//开始了开始了
    Interceptor interceptor = interceptors.get(index);//这里是拿到要用的拦截器
    Response response = interceptor.intercept(next);//okhttp的把责任链的下一个的入口放拦截器
    return response;
  }
}
```

感觉应该很~~难~~简单(

不如我们写个先

```kotlin
class MyInterceptor:Interceptor {
    private val TAG = "MyInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        val response = chain.proceed(request)
        val endTime = System.currentTimeMillis()
        val userTime = endTime - startTime
        val content = response.body?.string()
        val requestMeathod = request.method
        Log.d(TAG,"\n -------------START-------------")
        Log.d(TAG,"花费了$userTime")
        Log.d(TAG,"请求方法 $requestMeathod")
        Log.d(TAG,"返回信息 \n $content")
        Log.d(TAG,"-------------START-------------")
        return response
    }
}
```

基本意思就是把东西取出来然后操作之后再放回去(

##### OkHttp连接池复用机制

//TODO

##### Okio

//TODO
