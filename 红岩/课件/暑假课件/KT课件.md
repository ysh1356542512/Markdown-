#before

经过几天与课件奋战的时间，我感觉我又学到了很多的新东西。原来我对kotlin并不是很了解，甚至有时候简单的报错都要去百度上找（主要是自己比较菜的原因吧）。感觉自己原先对于kotlin就学了一点皮毛。

这个资料我自己准备的时候准备的太长了，也因为大家都对Kotlin比较熟悉，所以有些地方就会说的很快。

今天也算是能够在这里和大家一起分享我这几天学习成果吧！希望自己分享的学习内容能让大家有所收获 。

# Kotlin初谈

Kotlin 是一个用于现代多平台应用的==静态编程语言== ，由 JetBrains 开发。 Kotlin可以编译成Java字节码，也可以编译成JavaScript，方便在没有JVM的设备上运行。

kotlin支持==面对对象==和==函数式==两种编程风格

优点

- 简洁: 大大减少样板代码的数量。

- 安全: 避免空指针异常等整个类的错误。

- 互操作性: 充分利用 JVM、Android 和浏览器的现有库。完全兼容Java（这个点非常的强  这样就意味着 以前java的任何优秀的库 kt都可以无差别的使用  就跟好像是自己家的一样   ）

- 工具友好: 可用任何 Java IDE 或者使用命令行构建。

啊！缺点？ 我们kt没有缺点

缺点

- 编译比较慢，自动为属性生成很多的get/set方法

- apk会变大

# 基本数据类型

- java把基本数据类型和引用数据类型做了区分。一个==基本数据类型==（如int）的变量直接储存了它的值，而一个==引用类型==（如String）的==变量==储存的是指向包含该对象的==内存地址==的==引用==因此基本数据类型的值能够更加==高效==的==储存==和==传递==，但是你不能够对这些值==调用方法==，或者是把他们加入到集合中。java提供了特殊的包装类型（比如 java.lang，
  Integer)，在你需要对象的时候对基本数据英型进行葑装。因此，你不能用

```kotlin
collection<int>
```

来定义一个整数的集合，而必须用 

```
Collection<Integer>
```

来定义。

我以前学长讲的时候我就没有理解到什么是包装类型  还包括什么是==自动拆箱== ==自动装箱==    我总是疑问？这包装类型是拿来干嘛的

但是我现在理解的就是 所谓的==包装类型就是基本数据类型的对象形式==（我个人感觉这门理解应该是对的吧  ）

- 但是Kotlin 并不区分基本数据类型和包装类型，你使用的永远是同一个类型（比如：

```kotlin
val i： Int = 1
val list： List<Int>= listof(1, 2, 3)
```

这样很方便。此外,我们能对一个**数字类型的值调用方法**。例如下面这段代码中,
使用了标准库的函数 coerceIn 来把值限制在特定范围内：

```kotlin
fun showProgress(progress: Int){
val percent = progress.coerceIn(0, 100)
println("We're ${percent}" done!")
>>> showProgress(146)
We're 100% done!
```



在java里面你这样肯定是做不到的 能做到的只有它的包装类型   

同时在这里也可以说明  kt是不区分基本类型的 也就是说 全都可以当作对象理解



**扩展**

> 如果**基本数据**类型和**引用类型**是一样的，是不是意味着 Kotlin 使用对象来表示
> 所有的数字？这样不是非常低效吗？确实低效，所以 Kotlin 并没有这样做。
>
> 在运行时，数字类型会尽可能地使用最高效的方式来表示。大多数情况下:对于变量、属性、参数和返回类型———Kotlin 的 Int 类型会被**编译**成 Java 基本数据类型 int。
>
> 唯一不可行的例外是**泛型类**，比如**集合**。用作泛型类型参数的基本数据
> 类型会被编译成对应的 Java 包装类型。例如，Int 类型被用作集合类的类型参数时，
> 集合类将会保存对应包装类型 java.lang.Integer 的实例。
>
> 对应到 Java 基本数据类型的类型完整列表如下：
>
> - 整数类型——Byte、Short、Int、Long
>
> - 浮点数类型——Float、Double
> - 字符类型——Char
> - 布尔类型——Boolean

KT的数据类型基本和java是相同的区别就是上面那些类容

# 基本语法

## 定义常量与变量

可变变量定义：var 关键字

```kotlin
var <标识符> : <类型> = <初始化值>
```

不可变变量定义：val 关键字，只能赋值一次的变量(类似Java中final修饰的变量)

```kotlin
val <标识符> : <类型> = <初始化值>
```

常量与变量都可以没有初始化值,但是在引用前必须初始化

编译器支持自动类型判断,即声明时可以不指定类型,由编译器判断。



## 循环

关于while 和do while 与java没有什么不同的 就不在赘述了

在Kotlin中想遍历1-100的数值可以这样写：

```bash
for (index in 1..100){
            print(index)
        }
```

这样写是正序遍历，如果想倒序遍历就该使用标准库中定义的`downTo()`函数：

```bash
for (index in 100 downTo 1){
            print(index)
        }
```

想不使用1作为遍历的步长，可以使用`step()`函数：

```dart
 for (index in 1..100 step 2){
            print(index)//会输出1..3..5......
        }
```

要创建一个不包含末尾元素的区间：

```go
for (index in 1 until 10){
            println(index)//输出0..9
        }
```

遍历一个数组/列表，只取出下标:

```cpp
val array = arrayOf("a", "b", "c")
        for (index in array.indices){
            println("index=$index")//输出0，1，2
        }
```

遍历取元素：

```cpp
val array = arrayOf("a", "b", "c")
        for (element in array){
            println("element=$element")//输出a,b,c
        }
```

## 迭代map

- kotlin 迭代 map 的结构： for ((a,b) in map)
- a代表的是 map 的key，b代表的是 map 的 value，a和b是变量，自己命名即可
- ..语法可以用于创建字符区间。例如 for (c in 'A'..'F')

```kotlin
fun main(args: Array<String>){
    val binaryReps = TreeMap<Char, String>()
    for (c in 'A'..'F'){//创建字符区间
        val binary = Integer.toBinaryString(c.toInt())  //将 ASCII 码转化成二进制
        binaryReps[c] = binary //根据 key 为c 把 binary 存到 map 中
    }
    for ((letter, binary) in binaryReps){//迭代 map，把 key 和 value 赋值给变量
        println("$letter = $binary")
    
}
```

## 异常

- KT并不区分受检异常和未受检异常，所以对开发者来说我们可以既可以选择处理异常亦可以选择不处理异常   并不会出现java中那种强制让人处理异常的情况

- try是可以作为表达式的 也就是说它又返回值

使用 *throw*-表达式来抛出异常：

```kotlin
throw Exception("Hi There!")
```

*try* 是一个表达式，即它可以有一个返回值：

这意味在某些时候可以优化我们处理异常的方式

```kotlin
val a: Int? = try {
    parseInt(input) 
} catch (e: NumberFormatException)
{  null }
```

## 集合

kt当中并没有自己专门的集合类  它全部使用的是java的集合

- 原因是因为 这样可以更好的与java代码进行交互  而且java集合框架本身就比较优秀（个人想法）

**只读集合和可变集合**

- Kotlin的集合设计与Java不同的是，它把访问集合数据的接口和修改集合数据的接口分开了。这种区别存在于最基础的使用集合的接口之中，
- Kotlin.collections.Collection。使用这个接口，可以遍历集合中的元素、获取集合的大小、判断集合中是否包含某个元素，以及执行其他从该集合中读取数据的操作。但是这个接口==没有任何添加或移除元素==的方法。

- 使用Kotlin.collections.MutableCollections接口可以修改集合中的数据。它继承了普通的Kotlin.collections.Collection接口，还提供了方法来添加和移除元素、清空集合等。

![IMG_20210725_140345](../../%E5%9B%BE%E5%BA%93/Kotlin%E5%AD%A6%E4%B9%A0%E8%AF%BE%E4%BB%B6/IMG_20210725_140345.jpg)



------------------------------------------------

| 集合类型 | 只读   | 可变                                                         |
| -------- | ------ | :----------------------------------------------------------- |
| List     | listOf | mutableListOf<br/> arrayListOf                               |
| Set      | setOf  | mutableSetOf<br/> hashSetOf<br/> linkedSetOf<br/> sortedSetOf |
| Map      | mapOf  | mutableMapOf<br/> hashMapOf<br/> linkedMapOf<br/> sortedMapOf |

感觉对于集合来说没有太多要说了 更多的都是大家写代码体会得到的  

毕竟除了一些额外的东西  本质和java是一样的  所以我就不打算赘述太多了



==关于集合的各种新操作 也就是kt为集合添加的新功能  我在这里给一篇博客==

[集合操作符](https://www.cnblogs.com/Jetictors/p/9241867.html)

其实吧    我觉得长得有点像Rxjava（但也只是有点像）

![img](https://images2018.cnblogs.com/blog/1255627/201806/1255627-20180629093207574-1085674482.png)

- 
- 

==Rxjava与KT集合操作符对比==

Rxjava  37可以跑

```java
final String[] a = new String[]{"4","0","7","i","f","w","0","9"};
final Integer[] index = new Integer[]{5,3,9,4,8,3,1,9,2,1,7};

Observable.just(index)
        .flatMap(new Function<Integer[], ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer[] integers) throws Exception {
                
                return Observable.fromArray(integers);
            }
        })
        .filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) throws Exception {
                return i<a.length;
            }
        })
        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                
                return a[integer];
            }
        })
        .reduce(new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) throws Exception {
                return s+s2;
            }
        })
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("密码是"+s);
            }
        });
```

Kotlin集合操作符  32可跑

```kotlin
val a = arrayOf("4", "0", "7", "i", "f", "w", "0", "9")
val index = arrayOf(5, 3, 9, 4, 8, 3, 1, 9, 2, 1, 7)
index
        .filter {
            it <a.size
        }
        .map {
            a[it]
        }
        .reduce { s1, s ->
            "$s1$s"
        }
        .also {
            println("密码是：$it")
        }
```











- 关于集合这里有一个新的知识点———–==序列==
- 由于我能力有限 分享的时间有限  所有就由大家后面自行查看

[[译]Kotlin中是应该使用序列(Sequences)还是集合(Lists)?](https://juejin.cn/post/6844903616072040455)

[Kotlin系列之序列(Sequences)源码完全解析]()



## 函数

　为了增强代码的可读性，Kotlin 允许我们使用命名参数，即在调用某函数的时候，可以将函数参数名一起标明，从而明确地表达该参数的含义与作用，但是在指定了一个参数的名称后，之后的所有参数都需要标明名称

### 命名参数

![img](https://user-gold-cdn.xitu.io/2019/6/4/16b2159405f5947d?w=820&h=308&f=png&s=29187)

### 默认参数值

可以在声明函数的时候指定参数的默认值，从而避免创建重载的函数![img](https://user-gold-cdn.xitu.io/2019/6/4/16b21594070b063b?w=823&h=281&f=png&s=31108)

在这里我要插播一条知识点!  @JvmOverloads注解

- 在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。

- 在 Kotlin 中调用默认参数值的方法或者构造函数是完全没问题的，但是在 Java 代码调用相应 Kotlin 代码却不行，也就是，Java 代码不能调用在 Kotlin 中使用默认值实现的重载函数或构造函数。
  @JvmOverloads 就是解决这一问题的，从命名 —— “Jvm 重载”

如果我们再kotlin中写如下代码：

```kotlin
fun f(a: String, b: Int = 0, c: String="abc"){
    ...
}

```

相当于在Java中声明

```kotlin
void f(String a, int b, String c){
}
```



默认参数没有起到任何作用。

但是如果使用的了@JvmOverloads注解

```kotlin
@JvmOverloads fun f(a: String, b: Int=0, c:String="abc"){
}
```

相当于在Java中声明了3个方法：

```kotlin
void f(String a)
void f(String a, int b)
void f(String a, int b, String c)
```

是不是很方便，再也不用写那么多重载方法了。

注：该注解也可用在构造方法和静态方法。

```kotlin
class MyLayout: RelativeLayout {

   @JvmOverloads
   constructor(context:Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0): super(context, attributeSet, defStyleAttr)
}
```

相当Java中的：

```kotlin
public class MyLayout extends RelativeLayout {

    public MyLayout(Context context) {
        this(context, null);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
```

- 好了我们继续把车子开回来 



### 可变参数

![img](https://user-gold-cdn.xitu.io/2019/6/4/16b21594073943a0?w=823&h=293&f=png&s=26765)

### 局部函数

　Kotlin 支持在==函数中嵌套函数，被嵌套的函数称为局部函数==（跟闭包有关系下面再说）

![img](https://user-gold-cdn.xitu.io/2019/6/4/16b215942fff3d69?w=820&h=353&f=png&s=32076)

### 顶层函数和属性

==他们出现的目的是为了消除java中的静态工具类==

那么kt为什么要这门干南？

**概念:** 我们知道在Java中有静态函数和静态属性概念，它们一般作用就是为了提供一个全局共享访问区域和方法。我们一般的习惯的写法就是写一个类包裹一些static修饰的方法，然后在外部访问的直接利用**类名.方法名**访问。

**问题:**  我们都知道静态函数内部是不包含状态的，也就是所谓的纯函数，它的输入仅仅来自于它的参数列表，而它的输出也仅仅依赖于它参数列表。我们设想一下这样开发情景，有时候我们并不想利用实例对象来调用函数，所以我们一般会往静态函数容器类中添加静态函数，如此反复，这样无疑是让这个类容器膨胀。

**解决:**  在Kotlin中则认为一个函数或方法有时候并不是属于任何一个类，它可以独立存在。所以在Kotlin中类似静态函数和静态属性会去掉外层类的==容器==，一个函数或者属性可以直接定义在一个Kotlin文件的顶层中，在使用的地方只需要import这个函数或属性即可。==如果你的代码还存在很多以"Util"后缀结尾的工具类，是时候去掉了==。

在顶层文件中定义一个函数:

```kotlin
package com.mikyou.kotlin.top

import java.math.BigDecimal


//这个顶层函数不属于任何一个类，不需要类容器，不需要static关键字
fun add(a:Int,b:Int)=a+b

//测试顶层函数，实际上Kotlin中main函数和Java不一样，它可以不存在任何类容器中，可以直接定义在一个Kotlin 文件中
//另一方面也解释了Kotlin中的main函数不需要了static关键字，实际上它自己就是个顶层函数。
fun main(args: Array<String>) {
    println("文件大小: ${formateFileSize(15582.0)}")
}
```

#### 实质原理

- 通过以上例子我们思考一下顶层函数在JVM中是怎么运行的，如果你仅仅是在Kotlin中使用这些顶层函数，那么可以不用细究。但是我们是Java和Kotlin混合开发模式，那么你就有必要深入内部原理。我们都知道Kotlin和Java互操作性是很强的，所以就衍生出了一个问题:在Kotlin中定义的顶层函数。我们都知道在java中我们也是可以调用的

  

  32的test里面看一下顶层kt转java

  
  
  Kotlin中的顶层函数反编译成的Java中的容器类名一般是顶层文件名+“Kt”后缀作为类名，但是也是可以自定义的。也就是说顶层文件名和生成容器类名没有必然的联系。**通过Kotlin中的@file: JvmName("自定义生成类名")注解就可以自动生成对应Java调用类名，注意需要放在文件顶部，在package声明的前面**

### 扩展函数与属性

到了这里我们就可以明白为什么 kt使用的是java的集合 但是却多了很多对集合的操作   其原理就是==扩展函数==

**扩展函数语法就是**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190716192939942.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xkeGx6MjI0,size_16,color_FFFFFF,t_70)



```kotlin
fun String.firstChar(): String {
    if (this.length == 0) {
        return ""
    }
    return this[0].toString()
}

fun String.lastChar(): String {
    if (this.length == 0) {
        return ""
    }
    return this[this.length - 1].toString()
}
```

然后在可以直接调用

```kotlin
package com.demo.kotlin


object Text {

    @JvmStatic
    fun main(args: Array<String>) {
        println("abc".firstChar())
        println("qwe".lastChar())
    }
}

//a
//e
```



**扩展属性的语法就是**

这图片写错了应该是==扩展属性名==

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190716194955274.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xkeGx6MjI0,size_16,color_FFFFFF,t_70)

为MutableList扩展一个firstElement属性

```kotlin
var <T> MutableList<T>.firstElement: T
    get() { //扩展属性的get函数
        return this[0]
    }
    set(value) { //扩展属性的set函数
        this[0] = value
    }
```

调用如下

```kotlin
object Text {
    @JvmStatic
    fun main(args: Array<String>) {
        val list = mutableListOf(1, 2, 3, 4, 5, 6, 7)
        println(list.firstElement)
        list.firstElement = 100
        println(list)
    }
}
```

输出如下

```jade
1
[100, 2, 3, 4, 5, 6, 7]
```





#### 实现原理

扩展属性和扩展函数的本质是以==静态导入==的方式来实现的。 ==同时扩展函数由于这个原因 kotlin的扩展函数并不支持多态==

- 大佬们可以自行百度了解啦

### 中缀调用



==这个东西也老牛逼了  全部使用中缀调用  我们甚至可以实现汉语编程==   就不做演示啦

可以以以下形式创建一个 Map 变量

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae706a3bc07?w=822&h=161&f=png&s=19737)

使用 “to” 来声明 map 的 key 与 value 之间的对应关系，这种形式的函数调用被称为中缀调用

　　中缀调用可以与只有一个参数的函数一起使用，无论是普通的函数还是扩展函数。==中缀符号需要通过infix修饰符来进行标记==



==其实南  我们到了这里就可以回头看看前面的循环    它里面那些简便的写法其实就是扩展函数的中缀调用==

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae706cf0962?w=821&h=226&f=png&s=22638)

### 解构声明

有时会有把一个对象解构成多个变量的需求，在 Kotlin 中这种语法称为解构声明

　　例如，以下例子将 Person 变量结构为了两个新变量：name 和 age，并且可以独立使用它们

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae706d1b6e9?w=822&h=226&f=png&s=23211)

一个解构声明会被编译成以下代码：

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae706d45ba1?w=823&h=127&f=png&s=12055)

解构声明也可以用在 for 循环中

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae7219b0b77?w=821&h=165&f=png&s=15799)

遍历 map 同样适用

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae7277d8598?w=821&h=164&f=png&s=15570)

同样也适用于 lambda 表达式

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae726ceeeae?w=820&h=136&f=png&s=15744)

　如果在解构声明中不需要某个变量，那么可以用下划线取代其名称，此时不会调用相应的componentN()操作符函数

![img](https://user-gold-cdn.xitu.io/2019/6/13/16b4fae727ae7ab6?w=821&h=162&f=png&s=14656)

### 内联函数



![image-20210730083512308](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210730083512308.png)



#### inline

这个东西出现的原因 我理解的是因为高阶函数本身它有==性能缺陷==—-就是说它的那种函数型的参数是被当作对象处理的（就是创建了一个匿名对象  然后调用完成 马上就被回收了） 其实一般情况创建几个对象 那没什么关系啊  可是！！ 这玩意要是再循环里面（频繁调用）被调用了 那就特别损耗性能了

每次调用都会产生一个对象 

- 被inline标记的函数就是内联函数,其原理就是:在编译时期,把调用这个函数的地方（==包括函数类型的参数==）用这个函数的方法体进行替换
  
- 举个栗子:
  
   ![image-20210730084053519](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210730084053519.png)



**Lambda**

![image-20210731154240310](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210731154240310.png)



**总结**

- **正解：**在Kotlin中每次声明一个Lambda表达式，就会在字节码中产生一个匿名类。该匿名类包含了一个invoke方法，作为Lambda的调用方法，每次调用的时候，还会创建一个新的对象。可想而知，Lambda虽然简洁，但是会增加额外的开销。Kotlin 采用内联函数来优化Lambda带来的额外开销。

- **片面的**：（==这里是我最开始的时候在别人博客里面看到的，当时误以为是非常正确的，但是后来发现这种对内联函数的理解就是错片面的   大家及时避坑    但是这个理解也不是完全错误的  它的确能够带来一定的优化  但是如果大量使用不仅不能够优化   而且会造成编译的字节码膨胀 最后造成负优化==）*在Java 里是没有内联这个概念的，所有的函数调用都是普通方法调用，如果了解 Java 虚拟机原理的，可以知道 Java 方法执行的内存模型是基于 Java 虚拟机栈的：每个方法被执行的
  时候都会创建一个栈帧（Stack Frame），用于存储局部变量表、操作数栈、动态链接、方法出口等信息。每一个方法被调用直至执行完成的过程，就对应着一个栈帧入栈、出栈的过程。
  也就是说每调用一个方法，都会对应一个栈帧的入栈出栈过程，如果你有一个工具类方法，在某个循环里调用很多次，那就会对应很多次的栈帧入栈、出栈过程。这里首先要记住的一点
  是，栈帧的创建及入栈、出栈都是有性能损耗的。*

- 因为按照这样的解释那就是把函数的调用栈变浅了嘛   但是问题来了
  -   我们总没有见过那个==优化策略==通过减少函数来达到优化性能的效果吧（**这不是一般的离谱，这是特朗普哇！！！**）
  -   或者说既然这样是优化效果那么kotlin为啥不把每个函数都变成内联函数（）





#### noinline

noinline了,它的作用就已经非常明显了.就是让内联函数的形参函数不是内联的,保留原有的函数特征.

关闭内内联优化

- Lambda的高阶函数  通常是把这种Lambda当成了一种对象处理  而加上’inline’关键字
- 就会把这种对象拆开 所以它就**不允许返回 也不运行传递**  （==因为可以理解成它已经不能看做一个对象了==）
- 举个栗子:



![image-20210731154759270](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210731154759270.png)

#### crossinline

加强内联优化

要了解这个关键字的需求 我们来看这个   **其实这个关键字 和Lambda的return关系比较大**



其实它返回的是main（）而不是helleo 因为这是一个内联函数

![image-20210731160113160](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210731160113160.png)





**这种函数类型的参数 不允许间接调用**

![image-20210731160252707](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210731160252707.png)



**加了crossinlined关键的Lambda不允许使用return 但是可以间接调用**

![image-20210731160406023](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210731160406023.png)



### reified

什么是reified,字面意思:具体化,其实就是==具体化泛型==;
我们都知道在java中如果是泛型,是不能直接使用泛型的类型的,但是kotlin却是可以的,这点和java就有了显著的区别.
通常java中解决的方案就是通过函数来传递类
==但是kotlin就老牛逼了==,直接就可以用了,主要还是有内联函数inline这个好东西,才使得kotlin能够直接通过泛型就能拿到泛型的类型.
举个栗子:

```kotlin
// Function
private fun <T : Activity> Activity.startActivity(context: Context, clazz: Class<T>) {
    startActivity(Intent(context, clazz))
}

// Caller
startActivity(context, NewActivity::class.java)

```

reified人

使用 `reified`，通过添加类型传递简化泛型参数

```kotlin
// Function
inline fun <reified T : Activity> Activity.startActivity(context: Context) {
    startActivity(Intent(context, T::class.java))
}

// Caller
startActivity<NewActivity>(context)
```

是不是很简单,很爽.

#### 原理

我们知道内联函数的**原理**，编译器把实现**内联函数的字节码动态插入到每次的调用点**。那么实化的原理正是基于这个机制，**每次调用带实化类型参数的函数时，编译器都知道此次调用中作为泛型类型实参的具体类型**。所以**编译器**只要在每次调用时**生成对应不同类型实参调用的字节码插入到调用点即可**。

-  总之一句话很简单，就是带实化参数的函数每次调用都生成不同类型实参的字节码，**动态插入到调用点**。由于生成的字节码的类型实参引用了具体的类型，而不是类型参数所以不会存在擦除问题。



# 类



## 构造方法

- java中的构造方法

```java
/**
 * java person 类
 */
public class Person {
    String name;
    int age;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

特点：
 **1、方法名与类名相同**
 **2、不定义返回值类型**

**Kotlin中的构造方法**

**Kotlin中讲构造方法独立了出来，使用关键字**constructor**来表示。同时，Kotlin中将构造方法分为了两类：**主构造方法和次构造方法****

**主构造方法，每个类最多有1个**

**主构造方法在类后面声明**👇

- 空参/有参主构造方法

![img](https://upload-images.jianshu.io/upload_images/9137038-032dff4f8a6130f2.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200)



- constructor关键字可以省略


![img](https://upload-images.jianshu.io/upload_images/9137038-0a75a61a373fb95d.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200)

- 如果加权限修饰符，那么放到constructor前面，此时，就不能省略constructor关键字了


![img](https://upload-images.jianshu.io/upload_images/9137038-e2e71a941d68457a.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200)

- 再次强调，**当有了权限修饰符时，就不能省略constructor关键字了**


![img](https://upload-images.jianshu.io/upload_images/9137038-d4686aa89db94ff0.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200)



到这里，有关主构造方法的**声明语法**就讲完了
 接下来我们会有另外一个问题，怎么用？

当使用有参构造方法时，我们怎么使用主构造方法中的参数呢？

> java中的使用

![img](https://upload-images.jianshu.io/upload_images/9137038-41b45f691b00bee8.png?imageMogr2/auto-orient/strip|imageView2/2/w/644)

**Kotlin的主构造方法是没有方法体的**，这就意味着我们无法在主构造方法中进行任何操作。
 但Kotlin为我们**提供了一个`init`代码块**，这个代码块的执行顺序**在主构造方法之后**，次构造方法之前，我们可以在这个代码块中进行各种初始化的操作,包括访问主构造方法中的参数👇

```kotlin
/**
* kotlin 企鹅类
*/
class Penguin private constructor (name:String,age:Int) {

   var name:String?=null //名称
   var age:Int=0  //年龄
   var weight:Int=100  //体重

   //kotlin为我们提供了一个init代码块,
   //init代码块的执行顺序在主构造方法之后
   //我们可以在init代码块中进行各种初始化操作
   //init代码块中可以访问主构造方法中的变量
   init {
       this.name=name
       this.age=age
   }
}
```

**虽然可以初始化变量 但是我们是kotlin语言所以 应该有更加简单的初始化方式  这个太繁琐了**

**在一个类中声明属性，通过构造方法传值进行初始化**这种操作有没有很频繁，有没有很繁琐，虽然IDE给我们提供了快捷键，虽然你的手速也很快，但是要是能把这个过程“省略”掉就更完美了。
 Kotlin让提供了这样的功能

> 主构造方法中声明属性

![img](https://upload-images.jianshu.io/upload_images/9137038-9684ee54cd25fde8.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200)

**左侧的写法跟右侧的写法效果完全相同。**
 **var换成val也可以**👇

![img](https://upload-images.jianshu.io/upload_images/9137038-2fb9f43774e18bc1.png?imageMogr2/auto-orient/strip|imageView2/2/w/978)

**当主构造器和次构造器同时存在时，次构造器必须直接或者间接调用主构造器**

```kotlin
/**
 * kotlin 企鹅类
 */
class Penguin constructor(name: String) {
    var weight:Int=100  //体重
    var age:Int=0
    var name:String?=null

    //空参次构造器,调用了下面的次要构造方法
    constructor():this("奔波儿霸",10)

    //有参次构造器,调用了主构造方法
    constructor(name:String,age:Int):this(name){
        this.name=name
        this.age=age
    }
}
```

构造方法调用构造方法的方式如👆代码所示，使用`:this`来调用

**执行顺序上，主构造器>init代码块>次构造器**

==这个执行顺序还是比较重要的 在某些时候我们可能需要==





##  属性





首先我们要搞清楚在 Java 中属性是什么，在 Java 中<u>类的属</u>性不是指一个字段，而是一个**字段**和它的**get、set**方法加在一起才算一个==属性==，比如：

在java中字段和*其访问*器的**组合尝尝称为**属性

```java
class Person {
    int age;
    public int getAge(){
        return age;
    }
    public void setAge(int age){
        this.age = age
    }
}

```

而如果不给这个 name 写get、set方法，它就只是一个 **field**,可以称它为 **字段** 或者 **域**。

* **Kotlin的类只有属性（property）没有独立的字段（field）**

如果上面的代码用kotlin翻译一下：



```
class Person {
    var age = 0
}
```



可以对比出，Kotlin里不需要额外的get、set方法，当然你也写不了，因为 Kotlin 已经默认实现了get、set，所以在Kotlin里，我们写不出 **field**。



#### Kotlin 中的 字段 是什么

前面说 Kotlin 中不能写 field，但是我们很多时候必须要用到 field，比如你复写一个属性的set方法



```kotlin
var age = 0
    set(value){
        age = value + 1
    }
```



如果这样写其实是会发生递归，无法赋值成功

![img](https://user-gold-cdn.xitu.io/2019/4/12/16a0f72d171968dc?imageView2/0/w/1280/h/960/ignore-error/1)

这里AS也提醒你了，这里发生了递归
 所以我们一般都这么写：

```kotlin
var age = 0
    set(value){
        field = value + 1
    }
```



这里出现了一个 **field** 的东西可以访问和赋值，这个东西就是 Kotlin 的**幕后字段（Backing Field）**
 我们可以简单地理解为，Kotlin 没有明面上的 field，但是它存在于幕后



**我们小小拓展一下**

#### 没有字段的成员是什么？



```kotlin
class Person{
    private var _age =0
    
    var age:Int    //实际上并不存在这个成员变量  只存在1它的两个方法
        get() = _age
        set(value) {
            _age = value
        }
}

```

- 当我们声明一个 var 为私有时，比如上面的_age，我们叫它 **幕后属性** ,虽然它看起来不需要get、set方法，但是其实仍然是有的，只不过它的get、set方法都被声明为 private 了


- **当然我们这里不是想讨论幕后属性**，而是要讨论一下这个 age 是个什么玩意，是一个成员属性吗，其实通过字节码就可以知道，**这里的Person类实际并不存在age这个成员，它只是帮你生成了对_age的两个public final的get和set方法**

- 和你自己直接写一个


```kotlin
fun getAge() = _age
```

- 是一模一样的


#### 总结

- 在Kotlin中一个 property 不管有没有 backing field 都称之为 property，而在 Java 中 field + get、set方法一起才能是一个 property。





## 修饰符

- 类修饰符


| 修饰符     | 说明       |
| ---------- | ---------- |
| final      | 不能被继承 |
| open       | 可以被继承 |
| abstract   | 抽象类     |
| enum       | 枚举类     |
| data       | 数据类     |
| sealed     | 密封类     |
| annotation | 注解类     |

- 成员修饰符


| 修饰符   | 说明       |
| -------- | ---------- |
| override | 重写函数   |
| open     | 可被重写   |
| final    | 不能被重写 |
| abstract | 抽象函数   |
| iateinit | 后期初始化 |

- 访问权限修饰符


| 修饰符    | 类成员       | 顶层声明     |
| --------- | ------------ | ------------ |
| public    | 所有地方可见 | 所有地方可见 |
| internal  | 模块中可见   | 模块中可见   |
| protected | 子类中可见   |              |
| private   | 类中可见     | 文件中可见   |

只是这个protected 与java有区别  java是同包访问

kt只是类和子类可见

- 修饰符对于大家来说都比较简单 我们看看就可以了

## 嵌套类内部类

相当于`java`中的静态内部类（有`static`关键字修饰的内部类）

由于**相当于Java中的静态内部类**，所以不能访问到`nameStr`变量，但是**类可以访问里面的嵌套类**

```kotlin
//嵌套类
class OutClass1{
    private var nameStr:String = " print nameStr"
    class InnerClass{
        fun innerMethod() = "inner method print"
    }
}
fun main() {
    //外部想调用InnerClass的方法，可以直接调用
    var inner1:String = OutClass1.InnerClass().innerMethod();
}
```

### 局部嵌套类

定义在方法中的嵌套类

```kotlin
class OuterClass3{
    fun getName(): String{
        class GetClass{
            var name = "print getName"
        }
        return GetClass().name
    }
}
```

### 内部类

相当于`java`中的非静态内部类（没有`static`关键字修饰的内部类）

定义好内部类后，内部类会持有一个外部类的引用

内部类需要关键字`inner`声明

内部类可以直接调用外部类的属性，需要用`this@`

```kotlin
//内部类
class OutClass2{
    private var nameStr:String = " print nameStr"
    inner class InnerClass{
        //内部类中调用外部类的
        fun innerMethod() = this@OutClass2.nameStr
    }
}
fun main() {
	//外部类调用有inner关键字声明的内部类需要创建实例去调用
    var inner2:String = OutClass2().InnerClass().innerMethod()
```

### 内部类变量的引用方式

- Kotlin`访问外部类变量的方式：`this@OuterClass.name
- Java`访问外部类变量的方式：`OuterClass.this.name

```kotlin
class OuterClass4 {
    val name = "name 1"
    inner class NameClass2 {
        val name = "name 2"
        fun getName() {
            val name = "name 3"
            //外部类中的name调用
            println("${this@OuterClass4.name}")		//打印 name 1
            //当前类中的name调用
            println("${this.name}")					//打印 name 2
            //当前方法的name调用
            println("${name}")						//打印 name 3
        }
    }
}
```

## 数据类

- Java中没有专门的数据类，通常是通过JavaBean来做数据类，但Kotlin中使用了专门的数据类。
- Kotlin中数据类是只保存数据的类，类中的标准函数往往是从数据机械性推导而来的。
- Kotlin可以创建只包含数据的类即数据类，数据类使用关键字`data`标记。

声明数据类

```kotlin
data class <类名> <(主构造函数参数列表)> [:继承类和实现接口] [(/*类体*/)]
```

主构造函数的参数列表必须使用`val`或`var`声明为类属性，而且要求 至少有一个，否则无法通过编译。



```kotlin
data class User(val id:Int, val name:String)
```



数据类功能

- 自动声明与构造函数入参同名的属性字段
- 自动实现每个属性字段的存取器方法`set/get`
- 自动提供`equals`方法用于比较两个数据对象是否相等
- 自动提供`copy`方法允许完整复制某个数据对象
- 自动提供`toString`方法

Kotlin数据类只需要在`class`关键字前添加`data`关键字即可，编译器会自动从主构造函数中根据声明的属性推导出相关函数。

- `equals()`
- `hashCode()`
- `toString()`
- `componentN() functions`  ==这玩意就是被拿去当结构声明的重载了==
- `copy()`



- 如果这些函数在数据类中已经被明确定义，或从超类中继承而来则不再会生成。

- 对于自动生成的函数，编译器只使用主构造函数内部定义的属性。


- JVM中如果生成的类需要含有一个无参的构造函数则所有属性必须指定默认值。




```kotlin
data class User(val id:Int = 0, val name:String = "")
```

### 约束条件

为了保证生成代码的一致性以及有意义以，数据类需要满足以下条件：

- 主构造函数至少包含一个参数
- 所有的主构造函数的参数必须标识为`val`或`var`
- 数据类不可以声明为抽象`abstract`、开放`open`、密封`sealed`、内部`inner`
- 数据类不能继承其他类，但可实现接口。

### 对象复制

可使用复制函数复制对象并修改部分属性

```kotlin
fun copy(id:Int = this.id, name:String = name) = User(id, name)
```



例如：使用`copy`复制`User`数据类并修改`name`属性



通常数据类  官方是建议我们全部声明为val的

```kotlin
data class User(val id:Int, val name:String)

fun main(args:Array<String>){
    val user = User(id=1, name="alice")
    val older = user.copy(id = 2)
    println(user)//User(id=1, name=alice)
    println(older)//User(id=2, name=alice)
}
```



### 解析函数

- 解析函数`componentN()`用于数据类解析声明，这里的N与主构造函数中声明的属性数量是相同的。
- 解析函数能够将对象的属性提取出来并赋给一个值
- 数据类自带解构函数`componentN()`，组件函数也允许数据类在解析声明中使用。

```kotlin
data class User(val id:Int, val name:String)

fun main(args:Array<String>){
    val user = User(id=1, name="alice")
    //数据类解析声明
    val (id, name) = user
    println("id = $id, name=$name")//id = 1, name=alice
}
```

## object

定义一个类并同时创建个实例(也就是一个类对象

- 使用场景

  - 对象声明为单例的一种方式
  - 伴生对象可以持有工厂方法和其他与这个类相关，但是在调用时并不依赖实例的方法。他们的成员可以通过类名来访问
  - 对象表达式用来替代java的匿名内部类

  使用object创建单例模式 就不能够再次访问它的构造方法  因为这样是没有意义的

### 伴生对象

—==工厂方法和静态成员的地盘==    可以实现接口

**伴生对象里的init代码块就相当于Java中的静态代码块。在类加载的时候会优先执行且只会执行一次。**

在对象声明的前面加上**companion**关键字就生成了伴生对象。作用就是为其所在的外部类**模拟静态成员**。



- 每个类可以最多有一个半生对象；
- 伴生对象的成员类似于 Java 的静态成员；
- 伴生对象**相当于**外部类的对象，可以直接通过外部类名访问伴生对象的成员；
- 使用 const 关键字修饰常量，类似于 Java 中的 static final修饰。
- 可以使用 @JvmField 和 @JvmStatic 类似于 Java 中调用静态属性和静态方法；
  伴生对象可以扩展属性和扩展方法。加了这个注解以后才会生成JVM上的静态变量和函数

- 由于kotlin取消了static关键字，伴生对象是为了弥补kotlin没有static关键字修饰的静态成员的不足；
- 虽然伴生对象是为其所在对象模拟静态成员，但是伴生对象成员依然属于==伴生对象本身的成员==，而不属于其外部类的成员。

- kt没有static关键字  所以在大多数的情况下  推荐的是去使用顶层函数的方式 来替代java中的静态工具类   但是这样也有一个缺点 那就是顶层函数并不能访问类的private成员   所以就有了伴生对象的说法  它是实现工厂模式的理想选择

语法：（ObjectName可省略）

```kotlin
companion object ObjectName : [0~N个父类型] {
	//伴生对象类体
}
```



伴生对象名称可以省略，省略伴生对象名称后，如果想获取伴生对象本身，可以通过Companion获取。

```kotlin
fun main() {
    println(OuterClass.name)//伴生对象属性
    OuterClass.companionFun()//调用伴生对象方法
    OuterClass.Companion//通过Companion获取伴生对象本身
}

class OuterClass {
    companion object {
        val name = "伴生对象属性"
        fun companionFun() {
            println("调用伴生对象方法")
        }
    }
}
```

#### 扩展属性和方法



为伴生对象扩展成员，如果伴生对象有名字，则通过“**外部类.伴生对象名字.成员**”的方式扩展；

​		如果伴生对象没名字，则通过“**外部类.Companion.成员**”的方式扩展

```kotlin
fun main() {
    println(OuterClass.name)//伴生对象属性
    OuterClass.companionFun()//调用伴生对象方法

    println(OuterClass.extraParam)//为伴生对象扩展属性
    OuterClass.test()//为伴生对象扩展方法
}

class OuterClass {
    companion object {
        val name = "伴生对象属性"
        fun companionFun() {
            println("调用伴生对象方法")
        }
    }
}

/**
 * 为伴生对象扩展方法
 */
fun OuterClass.Companion.test() {
    println("为伴生对象扩展方法")
}

/**
 * 为伴生对象扩展属性
 */
val OuterClass.Companion.extraParam: String
    get() = "为伴生对象扩展属性"
```

### 对象表达式

是针对java匿名内部类的替代品  并且增加了实现多接口的能力 和修改在创建对象的作用域中定义的变量的能力等功能

对象表达式的语法格式如下：

```kotlin
object [: ``0``~N个父类型]{
 ``//对象表达式的类体部分
}
```

规则：

- 对象表达式不能是抽象类，因为系统在创建对象表达式时会立即创建对象。因此不允许将对象表达式定义成抽象类。
- 对象表达式不能定义构造器。但对象表达式可以定义初始化块，可以通过初始化块来完成构造器需要完成的事情。
- 对象表达式可以包含内部类，不能包含嵌套类。

```kotlin
interface Outputable {     //这是一个接口嘛
  fun output(msg: String)
}
 
abstract class Product(var price: Double) {//抽象类咯
  abstract val name: String
  abstract fun printInfo()
}
 
fun main(args: Array<String>) {
  //指定一个父类型（接口）的对象表达式
  var ob1 = object : Outputable {
    override fun output(msg: String) {
      for (i in 1..6) {
        println("<h${i}>${msg}</h${i}>")
      }
    }
  }
  ob1.output("随便输出点什么吧")
    
    
    
    
    
  println("-----------------------------------------------")
  //指定零个父类型的对象表达式
  var ob2 = object {
    //初始化块
    init {
      println("初始化块")
    }
 
    //属性
    var name = "Kotlin"
 
    //方法
    fun test() {
      println("test方法")
    }
    //只能包含内部类，不可以包含嵌套类
    inner class Inner
  }
  println(ob2.name)
  ob2.test()
  println("-----------------------------------------------")
    
    
    
    
    
  //指定两个父类型的对象表达式
  var ob3 = object : Outputable, Product(1.23) {
    override fun output(msg: String) {
      println("输出信息：${msg}")
    }
 
    override val name: String
      get() = "激光打印机"
 
    override fun printInfo() {
      println("高速极光打印机们支持自动双面打印！")
    }
  }
  println(ob3.name)
  ob3.output("Kotlin慢慢学")
  ob3.printInfo()
}
```



# 接口

Kotlin对于接口的设计和Java并不完全相同，它增强了接口的功能，比如如下两个：**接口方法支持默认实现、接口中支持抽象属性**。例子如下：



```kotlin
// Kotlin代码
interface Flyer {
    val speed: Int
    val height
        get() = 1000
    fun kond()
    fun fly() {
        println("I can fly")
    }
}
```

Kotlin的这个设计应该是向Java8看齐，因为Java 8引入了一个新特性——接口方法支持默认实现，默认实现方法fly()转换为Java代码如下：

```kotlin
// Java代码
void fly();
 
public static final class DefaultImpls {
    public static void fly(Flyer $this) {
        String var1 = "I can fly";
        System.out.println(var1);
    }
}
```

 也就是在接口中定义了静态内部类去实现。



### 抽象属性

Java中是没有抽象属性的，因为abstract只能修饰类和方法，所以接口的属性都是常量，不支持抽象属性。然而Kotlin作为更好的Java，它的接口支持抽象属性，是因为背后通过Java中的抽象方法来实现的，

```kotlin
// Kotlin代码
interface Flyer {
    val speed: Int
    val height
        get() = 1000
    fun kond()
    fun fly() {
        println("I can fly")
    }
}
```

- 比如属性speed转换为Java代码如下：

```java
// Java代码
interface Flyer{
    public abstract int getSpeed();
}
```

1、Kotlin接口中的属性，同方法一样，若没有指定默认行为，==则在实现该接口的类中必须对该属性进行初始化==。 

2、若要指定属性默认行为，需要像属性height一样用get()进行申明。

```kotlin
interface User{
    val nickname:String
}

class PrivateUser(override val nickname:String):User
```

```kotlin
在接口中的属性既可以是抽象的,也可以有访问器的实现,
但不能有幕后字段(backing field),因此访问器不能引用它们。
    interface MyInterface {
        val prop: Int // 抽象abstract,不能初始化

        val property: String
            get() = "foo" // 有访问器的实现，非抽象

        fun foo() {
            print(prop)
        }
    }

    class Child : MyInterface {
        override val prop: 
        Int = 29
    }
```

# 类型系统

kotlin中 ? 和 ?. 和 ?: 和 as? 和 !!的区别

## ? 可空类型

kotlin和Java的类型系统之间的一个很重要的区别就是，Kotlin对可空类型的显示支持

也就是说你可以声明一个变量，并且使用可空类型?来表示这个变量是可以为null的

比如:

java:

```java
int StrLen(String s){return s.length}
//这个函数并不安全，原因是传入的参数s如果是null，就会报空指针异常
```

kotlin:

```kotlin
fun StrLen(s:String?):Int = s.length  //不能直接调用length方法
//1、这里使用了可空类型?，?可以加载任何类型的后面来表示这个类型的变量可以为null
//2、可空类型的变量在使用的时候不能直接调用它的方法
//3、也不能把可空类型的值传给非空类型
/**
*如 val x:String?=null  var y:String = x//把可空类型的x赋值给非空类型的y会报错：Type mismatch
```

## ?. 安全调用运算符

```kotlin
fun StrLen(s:String?):Int = s.length  //不能直接调用length方法
//如果增加了null检查以后，就可以直接调用s.length了,如下:
fun StrLen(s:String?):Int  = if(s!=null) s.length else 0
//但是如果每个可空类型都这样检查会显得特别累赘，此时就用到了安全调用运算符?.

s?.length 就相当于 if(s!=null) s.length else null
//如果s不为空就执行方法,如果为空就返回null
```

## ?: Elvis运算符

（null合并运算符）

使用?:运算符可以设置当检查结果为空的时候的返回值

```kotlin
fun foo(s:String?){
	 val t:String  =  s ?:  "" //如果?:左边的值不为空返回左边的值，如果为空返回""
}

//可以这样使用
a?. peroson?. name ?: "UnKnown" //如果?:左边为空则返回"UnKnown"
//和throw运算符同事使用
//如果不为空就返回name,如果为空就抛出一个有意义的错误,而不是NullPointException
val name = a?.person?.name ?: throw illegalargumentexception("UnKnown name")//如果name为空就会报自定义的异常，防止下面代码调用而直接报空指针异常
println(name.length)//如果name为空就会报空指针异常
```

## as? 安全转换运算符

尝试把值转换成给定的类型，如果类型不合适就返回null

```kotlin
foo as? Type -> foo is  Type  retrun (foo as Type)
			 -> foo !is Type  return null

//as?和?:联合使用
object as? Person ?: "not human"
object as? Person ?: return false
```

## !! 非空断言

**Kotlin不推荐使用非空断言，通常我们会用?:来防止程序运行时报空指针异常而崩溃**

如果值为null就抛出NullPointerException空指针异常

```kotlin
var s:String = s!!  //如果s为null则会抛出空指针异常，并且异常会指向使用!!的这一行

println(s)//如果s为null则会抛出空指针异常

//使用断!!可以很方便的在抛出空指针异常的时候定位到异常的变量的位置
//但是千万不要连续使用断言!!
//student!!.person!!.name//如果报空指针异常了则无法判断到底是student为空还是person为空，所以不要连续使用断言!!
```

## lateinit 和 by lazy

Koltin中属性在声明的同时也要求要被初始化，否则会报错，ex：

```kotlin
private var name0: String //报错
private var name1: String = "xiaoming" //不报错
private var name2: String? = null //不报错
```

*但是我们在写代码的时候又不想给它整一个可空类型的对像 因为这样看起来很繁琐  但是在一开始的时候我们又不能让它初始化 那我们就必须使用kt的延迟初始化了*

kotlin中有两种延迟初始化的方式。一种是**lateinit var**，一种是**by lazy**。

lateinit 只用于变量 var：

lateinit var**只能**用来==修饰类属性==，**不能**用来修饰==局部变量==，并且==只能用来修饰对象==，**不能**用来修饰==基本 类型==(因为基本类型的属性在类加载后的准备阶段都会被初始化为默认值)。  

lateinit var的作用也比较简单，就是让编译期在检查时不要因为属性变量未被初始化而报错。

```kotlin
private lateinit var name:String

```

**by lazy**

而 lazy 只用于常量 val（应用于单例模式，而且当且仅当变量被第一次调用的时候，委托方法才会执 行）:

by lazy要求属性声明为 val ，即不可变变量，在java中相当于被 final 修饰。  这意味着该变量一旦初始化后就不允许再被修改值了(==基本类型是值不能被修改，对象类型是引用不 能被修改==)。 {} 内的操作就是返回唯一一次初始化的结果。  by lazy可以使用于==类属性==或者==局部变量==。



```kotlin
class TestCase {
    
  private val name: Int by lazy { 1 }
    
       fun printname() {
          println(name)
        }
}
```

如果想要仔细探究它的原理 就走这里就好啦

by lazy原理

[原理窗口](https://blog.csdn.net/dpl12/article/details/80758049)

## 类型参数

**kt所有得泛型类和泛型函数的类型参数默认都是可空的**

## 平台类型

平台类型本质上就是 Kotlin 不知道可空性信息的类型—所有 Java 引用类型在 Kotlin 中都表现为平台类型。当在 Kotlin 中处理平台类型的值的时候，它既可以被当做可空类型来处理，也可以被当做非空类型来操作。

平台类型的引入是 Kotlin 兼容 Java 时的一种权衡设计。试想下，如果所有来自 Java 的值都被看成非空，那么就容易写出比较危险的代码。反之，如果 Java 值都强制当做可空，则会导致大量的 `null` 检查。综合考量，平台类型是一种折中的设计方案。

平台类型的本质==就是kt不知道可控信息的类型 既可以当作可空类型处理 也可以当作非空类型处理==

为什么需要平台类型？（此处采摘于《Kotlin实战》）

![IMG_20210725_131749](../../%E5%9B%BE%E5%BA%93/Kotlin%E5%AD%A6%E4%B9%A0%E8%AF%BE%E4%BB%B6/IMG_20210725_131749.jpg)



## Any与Any？



Kotlin 中所有类都有一个共同的超类 Any ，如果类声明时没有指定超类，则默认为 Any。Any在运行时，其类型自动映射成java.lang.Object。在Java中Object类是所有引用类型的父类。但是不包括基本类型：byte int long等，基本类型对应的包装类是引用类型，其父类是Object。

- 而在Kotlin中，直接统一，所有类型都是引用类型，统一继承父类Any。Any是Java的等价Object类。但是跟Java不同的是，Kotlin中语言==内部的类型==和==用户定义类型==之间，并没有像Java那样划清界限。它们是同一类型层次结构的一部分。

- Any 只有 equals() 、 hashCode() 和 toString() 三个方法。 



```kotlin
	println(1 is Any)
        println(1 is Any?)
        println(null is Any)
        println(1 is Any?)
        println(Any() is Any?)
```

输出如下

```kotlin
true
true
false
true
true
```

## 返回类型

当一个函数没有返回值的时候，我们用Unit来表示这个特征，而不是null，大多数时候我们不需要显示地返回Unit，或者生命一个函数的返回值是Unit，编译器会推断它。

```kotlin
fun unitExample() {
        println("test,Unit")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val helloUnit = unitExample()
        println(hellUnit)
        println(hellUnit is kotlin.Unit)
    }
```

输出结果![在这里插入图片描述](https://img-blog.csdnimg.cn/20190701190919698.png)

可以看出变量helloUnit的类型是kotlin.Unit类型。以下写法是等价的

```kotlin
fun unitExample():kotlin.Unit {
        println("test,Unit")
    }
    fun unitExample(){
        println("test,Unit")
        return kotlin.Unit
    }
    fun unitExample(){
        println("test,Unit")
    }
```

跟其他类型一样，Kotlin.Unit的类型是Any。如果是一个可空的Unit？那么父类型是Any？。

**Nothing与Nothing?**
 在java中void不能是变量的类型，也不能作为值打印输出。但是在java中有个包装类Void是void的自动装箱类型。如果你想让一个方法的返回类型永远是null的话，可以把返回类型为这个大写的Void类型。

```kotlin
public Void testV() {//声明类型是Void
        System.out.println("am Void");
        return null;//返回值只能是null
    }

    public static void main(String[] args) {
        JavaTest test = new JavaTest();
        Void aVoid = test.testV();
        System.out.println(aVoid);
    }
```

打印结果如下![在这里插入图片描述](https://img-blog.csdnimg.cn/20190701191959596.png)

这个Void对应的类型是Nothing?，其唯一可被访问的返回值也是null，Kotlin中类型层次结构最底层就是Nothing
 Nothing的类定义如下

```
//Nothing的构造函数是private的，外界无法创建Nothing对象
public class Nothing private constructor()
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190701192636911.png)

如果一个函数返回值是Nothing，那么这个函数永远不会有返回值。
 但是我们可以使用Nothing来表达一个从来不存在的返回值。例如EmptyList中的get函数

```kotlin
object EmptyList : List<Nothing> {

        override fun get(index: Int): Nothing {
            throw IndexOutOfBoundsException()
        }
 }
```

因为get永远不会反回值，而是直接抛出了异常，这个时候可以用Nothing作为get函数的返回值。
 再例如Kotlin标准库里面的exitProcess()函数

```kotlin
@kotlin.internal.InlineOnly
public inline fun exitProcess(status: Int): Nothing {
    System.exit(status)
    throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
}
```

Unit与Nothing之间的区别是，Unit类型表达式计算结果返回值是Unit；Nothing类型表达式计算结果永远是不会反回的，与java中void相同。
 Nothing?可以只包含一个值 null 。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190701193825425.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xkeGx6MjI0,size_16,color_FFFFFF,t_70)

Nothing?唯一允许的值是null，可被用作任何可空类型的空引用。

# 泛型

关于==泛型方法==     ==泛型类==   ==泛型接口==  我么就不说了

## 泛型约束

  单约束加个：就可以啦

对于多个上界约束条件，可以用 where 子句：

![image-20210729134303247](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210729134303247.png)

```kotlin
fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
    where T : CharSequence,
          T : Comparable<T> {
    return list.filter { it > threshold }.map { it.toString() }
}
```

## 泛型擦除

```java
Class c1 = new ArrayList<String>().getClass();
Class c2 = new ArrayList<Integer>().getClass();
System.out.println(c1 == c2);
```

直接截图博客

![image-20210729153309959](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210729153309959.png)

## 协变，逆变，抗变

首先让我们搞明白这三个名词的概念吧：

假设我们有如下两个类型集合

- 第一个集合为： `Animal`和`Dog` , `Dog`是`Animal`的子类

```kotlin
open class Animal 
class Dog : Animal()
```

- 第二个集合为 `List<Animal>` `List<Dog>`

```kotlin
List<Animal>
List<Dog>
```

现在问题来了：由于`Dog`是`Animal`的子类，那么`List<Dog>`就是`List<Animal>`的子类这句话在Kotlin/Java中对吗？

相信大佬们都可以回答的出来，答案是**否定**的。我们这里要说的协变，逆变，抗变就是描述上面两个类型集合的关系的。

- 协变(Covariance)：`List<Dog>` 是`List<Animal>`的子类型
- 逆变(Contravariance): `List<Animal>` 是`List<Dog>`的子类型
- 抗变(Invariant): `List<Animal>` 与`List<Dog>`没有任何继承关系

### 抗变

Java中泛型是**抗变**的，那就意味着`List<String>`不是`List<Object>`的子类型。因为如果不这样的话就会产生类型不安全问题。

例如下面代码可以通过编译的话，就会在运行时抛出异常

```kotlin
List<String> strs = new ArrayList<String>();
List<Object> objs = strs; 
objs.add(1); 
 // 尝试将Integer 转换为String,发生运行时异常 ClassCastException: Cannot cast Integer to String
```



所以上面的代码在编译时就会报错，这就保证了类型安全。

但值得注意的是Java中的**数组是协变的**，所以数组真的会遇到上面的问题，编译可以正常通过，但会发生运行时异常，所以在Java中要优先使用泛型集合。

```kotlin
String[] strs= new String[]{"ss007"};
 Object[] objs= strs;
 objs[0] = 1;

```

### 协变



**抗变性**会严重制约程序的灵活性，例如有如下方法`copyAll`,将一个`String`集合的内容**copy**到一个`Object`集合中，这是顺理成章的事。

```java
// Java
void copyAll(Collection<Object> to, Collection<String> from) {
     to.addAll(from);
}
```

但是如果`Collection<E>`中的`addAll`方法签名如下的话，`copyAll`方法就通不过编译，因为通过上面的讲解，我们知道由于抗变性，`Collection<String>`不是`Collection<Object>`的子类，所以编译通不过。

```java
boolean addAll(Collection<E> c);
```

那怎么办呢？

Java通过**通配符参数**(wildcard type argument)来解决, 把`addAll`的签名改成如下即可：

```java
boolean addAll(Collection<? extends E> c);
```

### 逆变

同理有时我们需要将`Collection<Object>`传递给`Collection<String>`就使用`? super E`，其 表示可以接收`E`或者`E`的父类，子类的位置却可以接收父类的实例，这就使得泛型类型发生了**逆变**

```java
void m (List<? super String){
}
```

### 特性



当使用? extends E 时，只能调用传入参数的==读取方法==而无法调用其==修改方法==。
当使用? super E时，可以调用输入参数的==修改方法==，但调用读取方法的话==返回值类型永远是Object==，几乎没有用处。

我第一次见到的时候 是不好理解的！ 让我们一起看下code吧，理解了Java的这块，Kotlin的In和out关键字就手到擒来了。

例如有如下一接口，其有两个方法，一个修改，一个读取。

下面是两个使用通配符的方法，注意看注释



```java
//协变，可以接受BoxJ<Dog>类型的参数
 private Animal getOutAnimalFromBox(BoxJ<? extends Animal> box) {
       Animal animal = box.getAnimal();
      // box.putAnimal(某个类型) 无法调用该修改方法，因为无法确定 ？究竟是一个什么类型，没办法传入
       return animal;
  }

//逆变，可以接受BoxJ<Animal>类型的参数
 private void putAnimalInBox(BoxJ<? super Dog> box){
        box.putAnimal(new Dog());
        // 虽然可以调用读取方法，但返回的类型却是Object，因为我们只能确定 ？的最顶层基类是Object
        Object animal= box.getAnimal();
  }
```



![image-20210730092039375](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210730092039375.png)

|        | 协变                                            | 逆变                                            | 不变                      |      |      |
| ------ | ----------------------------------------------- | ----------------------------------------------- | ------------------------- | ---- | ---- |
| Kotlin | <out T>只能作为消费者，只能读取不能添加。       | <in T>只能作为生产者，只能添加，读取受限制      | <T>既可以添加，也可以读取 |      |      |
| java   | <? extends T>只能作为消费者，只能读取不能添加。 | <? super T>只能作为生产者，只能添加，读取受限制 | <T>既可以添加，也可以读取 |      |      |

![image-20210727140827540](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210727140827540.png)

如果泛型类**只将**泛型类型作为函数的入参（输入），那么使用 in：



```kotlin
interface Consumer<in T> {
    fun consume(item: T)
}
```

如果泛型类**只将**泛型类型作为函数的返回（输出），那么使用 out：

```kotlin
interface Production<out T> {
    fun produce(): T
}
```

![image-20210730092440888](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210730092440888.png)



![image-20210730092552112](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210730092552112.png)







![image-20210730092634118](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210730092634118.png)



# 高阶函数

**定义**

- 将函数用作一个函数（其实常常是lambda 因为基本上没有在这里使用==匿名函数==  或者==函数引用==吧（纯属个人看法））的==参数==或者==返回值==的函数。



所以分享高阶函数也就相当于风向lambda   所以我就混在一起说了





**Lambda**



`Lambda`表达式的本质其实是`匿名函数`，因为在其底层实现中还是通过`匿名函数`来实现的。但是我们在用的时候不必关心起底层实现。不过`Lambda`的出现确实是减少了代码量的编写，同时也是代码变得更加简洁明了。
 `Lambda`作为函数式编程的基础，其语法也是相当简单的。这里先通过一段简单的代码演示没让大家了解`Lambda`表达式的简洁之处。

### Lambda语法

**为什么要使用Kotlin的lambda表达式?**

- 1、Kotlin的lambda表达式以更加简洁易懂的语法实现功能，使开发者从原有冗余啰嗦的语法声明解放出来。可以使用函数式编程中的过滤、映射、转换等操作符处理集合数据，从而使你的代码更加接近函数式编程的风格。

- 2、Java8以下的版本不支持Lambda表达式，而Kotlin则兼容与Java8以下版本有很好互操作性，非常适合Java8以下版本与Kotlin混合开发的模式。解决了Java8以下版本不能使用lambda表达式瓶颈。

- 3、在Java8版本中使用Lambda表达式是有些限制的，它不是真正意义上支持闭包，而Kotlin中lambda才是真正意义的支持闭包实现。(关于这个问题为什么下面会有阐述)



**在Kotlin实际上可以把Lambda表达式分为两个大类，一个是普通的lambda表达式，另一个则是带接收者的lambda表达式(功能很强大，很多源码就采用这个实现)。这两种lambda在使用和使用场景也是有很大的不同. 先看下以下两种lambda表达式的类型声明:**



![image-20210727101906555](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210727101906555.png)

针对带**接收者**的Lambda表达式在Kotlin中标准库函数中也是非常常见的比如**with,apply**标准函数的声明。

```kotlin
@kotlin.internal.InlineOnly
public inline fun <T, R> with(receiver: T, block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return receiver.block()
}

@kotlin.internal.InlineOnly
public inline fun <T> T.apply(block: T.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
    return this
}
```

lambda的**标准形式**基本声明满足三个条件:

**含有实际参数**

**含有函数体(尽管函数体为空，也得声明出来)**

**以上内部必须被包含在花括号内部**



![image-20210727102201192](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210727102201192.png)

lambda简化规则。



- 最后一个参数是一个lanmda表达式 可以移到（）的外边


- 一个函数只接受一个lanmda表达式的话 （）就可以省略


- 自动类型的推到 可以不给参数指定类型


- 只有一个参数的时候可以用it代替

![img](https://user-gold-cdn.xitu.io/2018/4/25/162fae531950db3c?imageView2/0/w/1280/h/960/ignore-error/1)

#### lambda表达式类型

```kotlin
() -> Unit//表示无参数无返回值的Lambda表达式类型

(T) -> Unit//表示接收一个T类型参数，无返回值的Lambda表达式类型

(T) -> R//表示接收一个T类型参数，返回一个R类型值的Lambda表达式类型

(T, P) -> R//表示接收一个T类型和P类型的参数，返回一个R类型值的Lambda表达式类型

(T, (P,Q) -> S) -> R//表示接收一个T类型参数和一个接收P、Q类型两个参数并返回一个S类型的值的Lambda表达式类型参数，返回一个R类型值的Lambda表达式类型




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

#### typealias

```kotlin
fun main(args: Array<String>) {
    val oddNum:  (Int) -> Unit = {
        if (it % 2 == 1) {
            println(it)
        } else {
            println("is not a odd num")
        }
    }

    val evenNum:  (Int) -> Unit = {
        if (it % 2 == 0) {
            println(it)
        } else {
            println("is not a even num")
        }
    }

    oddNum.invoke(100)
    evenNum.invoke(100)
}

```

使用typealias关键字声明(Int) -> Unit类型

```kotlin
package com.mikyou.kotlin.lambda

typealias NumPrint = (Int) -> Unit//注意:声明的位置在函数外部，package内部

fun main(args: Array<String>) {
    val oddNum: NumPrint = {
        if (it % 2 == 1) {
            println(it)
        } else {
            println("is not a odd num")
        }
    }

    val evenNum: NumPrint = {
        if (it % 2 == 0) {
            println(it)
        } else {
            println("is not a even num")
        }
    }

    oddNum.invoke(100)
    evenNum.invoke(100)
}
```

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



### 使用的场景

- 场景一: ==lambda表达式与集合==一起使用，是最常见的场景，可以各种筛选、映射、变换操作符和对集合数据进行各种操作，非常灵活，相信使用过RxJava中的开发者已经体会到这种快感，没错Kotlin在语言层面，无需增加额外库，就给你提供了支持函数式编程API。

```kotlin
package com.mikyou.kotlin.lambda

fun main(args: Array<String>) {
    val nameList = listOf("Kotlin", "Java", "Python", "JavaScript", "Scala", "C", "C++", "Go", "Swift")
    nameList.filter {
        it.startsWith("K")
    }.map {
        "$it is a very good language"
    }.forEach {
        println(it)
    }

}
```

![img](https://user-gold-cdn.xitu.io/2018/4/25/162fbbe5a058ca86?imageView2/0/w/1280/h/960/ignore-error/1)

- 场景二: 替代原有匿名内部类，但是需要注意一点就是只能替代含有==单抽象方法==的类。

```kotlin
findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				...
			}
		});
```

用kotlin lambda实现

```kotlin
findViewById(R.id.submit).setOnClickListener{
    ...
}

```

- 场景三: 定义Kotlin扩展函数或者说==需要把某个操作或函数当做值传入的某个函数==的时候。

```kotlin
fun Context.showDialog(content: String = "", negativeText: String = "取消", positiveText: String = "确定", isCancelable: Boolean = false, negativeAction: (() -> Unit)? = null, positiveAction: (() -> Unit)? = null) {
	AlertDialog.build(this)
			.setMessage(content)
			.setNegativeButton(negativeText) { _, _ ->
				negativeAction?.invoke()
			}
			.setPositiveButton(positiveText) { _, _ ->
				positiveAction?.invoke()
			}
			.setCancelable(isCancelable)
			.create()
			.show()
}

fun Context.toggleSpFalse(key: String, func: () -> Unit) {
	if (!getSpBoolean(key)) {
		saveSpBoolean(key, true)
		func()
	}
}

fun <T : Any> Observable<T>.subscribeKt(success: ((successData: T) -> Unit)? = null, failure: ((failureError: RespException?) -> Unit)? = null): Subscription? {
	return transformThread()
			.subscribe(object : SBRespHandler<T>() {
				override fun onSuccess(data: T) {
					success?.invoke(data)
				}

				override fun onFailure(e: RespException?) {
					failure?.invoke(e)
				}
			})
}
```

 **Kotlin的lambda表达式的作用域中访问变量和变量捕获**

- 在Java中在函数内部定义一个匿名内部类或者lambda，内部类访问的函数局部变量必须需要final修饰，也就意味着在内部类内部或者lambda表达式的内部是无法去修改函数局部变量的值。可以看一个很简单的Android事件点击的例子

```kotlin
public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        final int count = 0;//需要使用final修饰
        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(count);//在匿名OnClickListener类内部访问count必须要是final修饰
            }
        });
    }
}
```

- 在Kotlin中在函数内部定义lambda或者内部类，既可以访问final修饰的变量，也可以访问非final修饰的变量，也就意味着在Lambda的内部是可以直接修改函数局部变量的值。以上例子Kotlin实现

访问final修饰的变量

```kotlin
class Demo2Activity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_demo2)
		val count = 0//声明final
		btn_click.setOnClickListener {
			println(count)//访问final修饰的变量这个是和Java是保持一致的。
		}
	}
}
```

访问非final修饰的变量，并修改它的值

```kotlin
class Demo2Activity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_demo2)
		var count = 0//声明非final类型
		btn_click.setOnClickListener {
			println(count++)//直接访问和修改非final类型的变量
		}
	}
}
```

当然咯  我们的kotlin要比java更加的强大

**Kotlin中lambda表达式的变量捕获及其原理**

==扩展==



==以下内容请大家后面自行阅读体会了==

- 什么是变量捕获?

通过上述例子，我们知道在Kotlin中既能访问final的变量也能访问或修改非final的变量。原理是怎样的呢？在此之前先抛出一个高大上的概念叫做**lambdab表达式的变量捕获**。实际上就是lambda表达式在其函数体内可以访问外部的变量，我们就称这些外部变量被lambda表达式给捕获了。有了这个概念我们可以把上面的结论变得高大上一些:

第一在Java中lambda表达式只能捕获final修饰的变量

第二在Kotlin中lambda表达式既能捕获final修饰的变量也能访问和修改非final的变量

- 变量捕获实现的原理

我们都知道函数的局部变量生命周期是属于这个函数的，当函数执行完毕，局部变量也就是销毁了，但是如果这个局部变量被lambda捕获了，那么使用这个局部变量的代码将会被存储起来等待稍后再次执行，也就是被捕获的局部变量是可以延迟生命周期的，**针对lambda表达式捕获final修饰的局部变量原理是局部变量的值和使用这个值的lambda代码会被一起存储起来；而针对于捕获非final修饰的局部变量原理是非final局部变量会被一个特殊包装器类包装起来，这样就可以通过包装器类实例去修改这个非final的变量，那么这个包装器类实例引用是final的会和lambda代码一起存储**

以上第二条结论在Kotlin的语法层面来说是正确的，但是从真正的原理上来说是错误的，只不过是Kotlin在语法层面把这个屏蔽了而已，实质的原理lambda表达式还是只能捕获final修饰变量，而为什么kotlin却能做到修改非final的变量的值，**实际上kotlin在语法层面做了一个桥接包装，它把所谓的非final的变量用一个Ref包装类包装起来，然后外部保留着Ref包装器的引用是final的，然后lambda会和这个final包装器的引用一起存储，随后在lambda内部修改变量的值实际上是通过这个final的包装器引用去修改的。**

![img](https://user-gold-cdn.xitu.io/2018/4/26/162fda188f328051?imageView2/0/w/1280/h/960/ignore-error/1)


最后通过查看Kotlin修改非final局部变量的反编译成的Java代码就是一目了然了

```kotlin
class Demo2Activity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_demo2)
		var count = 0//声明非final类型
		btn_click.setOnClickListener {
			println(count++)//直接访问和修改非final类型的变量
		}
	}
}

```

```java
@Metadata(
   mv = {1, 1, 9},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014¨\u0006\u0007"},
   d2 = {"Lcom/shanbay/prettyui/prettyui/Demo2Activity;", "Landroid/support/v7/app/AppCompatActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "production sources for module app"}
)
public final class Demo2Activity extends AppCompatActivity {
   private HashMap _$_findViewCache;

   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setContentView(2131361820);
      final IntRef count = new IntRef();//IntRef特殊的包装器类的类型，final修饰的IntRef的count引用
      count.element = 0;//包装器内部的非final变量element
      ((Button)this._$_findCachedViewById(id.btn_click)).setOnClickListener((OnClickListener)(new OnClickListener() {
         public final void onClick(View it) {
            int var2 = count.element++;//直接是通过IntRef的引用直接修改内部的非final变量的值，来达到语法层面的lambda直接修改非final局部变量的值
            System.out.println(var2);
         }
      }));
   }

   public View _$_findCachedViewById(int var1) {
      if(this._$_findViewCache == null) {
         this._$_findViewCache = new HashMap();
      }

      View var2 = (View)this._$_findViewCache.get(Integer.valueOf(var1));
      if(var2 == null) {
         var2 = this.findViewById(var1);
         this._$_findViewCache.put(Integer.valueOf(var1), var2);
      }

      return var2;
   }

   public void _$_clearFindViewByIdCache() {
      if(this._$_findViewCache != null) {
         this._$_findViewCache.clear();
      }

   }
}


```

### 成员引用

我们知道在Lambda表达式可以直接把一个代码块作为一个参数传递给函数，但是有没有遇到过这样一个场景就是我要传递过去的代码块，已经是作为了一个命名函数存在了，此时你还需要重复写一个代码块传递过去吗？肯定不是，Kotlin拒绝啰嗦重复的代码。所以只需要成员引用替代即可。

```kotlin
fun main(args: Array<String>) {
    val persons = listOf(Person(name = "Alice", age = 18), Person(name = "Mikyou", age = 20), Person(name = "Bob", age = 16))
    println(persons.maxBy({ p: Person -> p.age }))
}
```

可以替代为

```kotlin
fun main(args: Array<String>) {
    val persons = listOf(Person(name = "Alice", age = 18), Person(name = "Mikyou", age = 20), Person(name = "Bob", age = 16))
    println(persons.maxBy(Person::age))//成员引用的类型和maxBy传入的lambda表达式类型一致
}
```

#### 成员引用的基本语法

成员引用由类、双冒号、成员三个部分组成



![img](https://user-gold-cdn.xitu.io/2018/4/26/162fdbaf8e18bcf4?imageView2/0/w/1280/h/960/ignore-error/1)

#### 使用场景

- 成员引用最常见的使用方式就是类名+双冒号+成员(属性或函数)

```kotlin
fun main(args: Array<String>) {
    val persons = listOf(Person(name = "Alice", age = 18), Person(name = "Mikyou", age = 20), Person(name = "Bob", age = 16))
    println(persons.maxBy(Person::age))//成员引用的类型和maxBy传入的lambda表达式类型一致
}
```

- 省略类名直接引用顶层函数

```kotlin
package com.mikyou.kotlin.lambda

fun salute() = print("salute")

fun main(args: Array<String>) {
    run(::salute)
}

```

- 成员引用用于扩展函数

```kotlin
fun Person.isChild() = age < 18

fun main(args: Array<String>){
    val isChild = Person::isChild
    println(isChild)
}
```

### 另一种lambda

刚才就在上面说了lambda分为两种  这里我就和大家分享第二种lambda

**带接收者的函数**

在`kotlin`中，提供了指定的*接受者对象*调用`Lambda`表达式的功能。在函数字面值的函数体中，可以调用该接收者对象上的方法而无需任何额外的限定符。它类似于`扩展函数`，==它允你在函数体内访问接收者对象的成员。==（老牛逼了）

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
    body()   // 调用该接收者对象的一个方法   并没有通过对象.方法的形式调用
}
```

#### 举个例子

sp数据库嘛

```kotlin
val editor = getPreferences(Context.MODE_PRIVATE).edit()
editor.putFloat("float",10f)
editor.putInt("int",10)
editor.apply()
```

然后再kotlin项目里面一般都会有  项目构建的时候就已经有了ktx库  提供了很多kt的写法

```kotlin
implementation 'androidx.core:core-ktx:1.3.2'
```

它提供了一些方便的写法  我们可以这样

```kotlin
getPreferences(Context.MODE_PRIVATE).edit {//直接就像在edit类里面调用自己的方法一样
             putFloat("float",10f)
             putInt("int",10)
        }
```



```kotlin
fun SharedPreferences.modify(block:SharedPreferences.Editor.()->Unit){   //我们来弄一个高阶扩展函数
    val editor = edit()
    editor.block()
    editor.apply()
}


//然后我们也可以这样使用
   getPreferences(Context.MODE_PRIVATE).modify {
            putFloat("float",10f)
            putInt("int",10)
        }

```





### 闭包

- 所谓`闭包`，即是函数中包含函数（**能够读取其它函数内部变量的函数**），这里的函数我们可以包含(`Lambda`表达式，匿名函数，局部函数，对象表达式)。我们熟知，函数式编程是现在和未来良好的一种编程趋势。故而`Kotlin`也有这一个特性。
- 我们熟知，`Java`是不支持闭包的，`Java`是一种面向对象的编程语言，在`Java`中，`对象`是他的一等公民。`函数`和`变量`是二等公民。
- `Kotlin`中支持闭包，`函数`和`变量`是它的一等公民，而`对象`则是它的二等公民了。

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

### 自带高阶函数

==只做笔记分享  没时间和大家面对面击剑了   大家后面自己看看就行啦==

![image-20210729123913862](../../%E5%9B%BE%E5%BA%93/KT%E8%AF%BE%E4%BB%B6/image-20210729123913862.png)

#### TODO函数



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



#### run()函数



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



#### with()函数



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

#### T.apply()函数

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

#### T.also()函数

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

#### T.let()函数



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

#### T.takeIf()函数

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

#### T.takeUnless()函数

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



#### repeat()函数



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

#### lazy()函数

关于`Lazy()`函数来说，它共实现了`4`个重载函数，都是用于延迟操作，不过这里不多做介绍。因为在实际的项目开发中常用都是用于延迟初始化属性。



# KT小知识



## in关键字

- 指定在`for(...)`循环中迭代的对象
- 用作中缀操作符检查一个值是否属于一个区间、一个集合或者其他**定义了`contains`方法**的实体
- 在`when`表达式中用于上述目的
- 将一个类型参数标记为`逆变`[1](https://blog.csdn.net/Sino_Crazy_Snail/article/details/79315932#fn:footnote)

适用于字符串和集合都是允许的

　你可以使用`in`来检查一个值是否在一个区间内：

```kotlin
fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'

fun isNotDigit(c: Char) = c !in '0'..'9'

fun main(args: Array<String>) {
    println("a is letter?: ${isLetter('a')}")
    println("1 isn't Digit?: ${isNotDigit('1')}")
}
//result:
a is letter?: true
1 isn't Digit?: false

```

检查一个值是否在一个集合内：

```kotlin
 println("kotlin" in setOf("Java", "C++"))
 //false
```

==in 还可以和when使用==

它甚至可以这样使用

```kotlin
fun main(args: Array<String>) {
    var x = 9
    var validNumbers = arrayOf(1, 2, 3)
    when (x) {
        in 1..10 -> print("x is in the range")
        in validNumbers -> print("x is valid")
        !in 10..20 -> print("x is outside the range")
        else -> print("none of the above")
    }
}
```

## 表达式和语句



==表达式==是可以被求值的代码，而==语句==是一段可执行代码。

表达式可以参与运行，可以有返回值，而语句不行，这就是区别

- 而且表达式可以作为另一个表达式的一部分使用

kotlin中when和if都是表达式   所以他们用起来更加的灵活

同时kt当中没有三元运算符

- if

```kotlin
/条件表达式,有值哈
    var result: String = if (inputNum == 123456) {
        "登录成功"
    } else {
        "密码错误,登录失败"
    }
val max = if (a > b) a else b

//我们甚至可以return一个表达式
fun test() = if (a > b) a else b

fun test(){
    return if (inputNum == 123456) {
        "登录成功"
    } else {
        "密码错误,登录失败"
    }
}
```

- when

- ```kotlin
  val hasPrefix = when (x) {
  	is String -> x.startsWith("prefix")
  	else -> false
  }
  //这样
  fun test() = when (x) {
  	is String -> x.startsWith("prefix")
  	else -> false
  }
  
  //这样
  fun test(){
      return when (x) {
  	is String -> x.startsWith("prefix")
  	else -> false
  }
  }
  ```





# after

到了最后就向大家推荐一本书吧

《kotlin实战》

关于协程我也不在这里班门弄斧了  大家自己学习就好了

还有==反射==和==注解==的内容没有和大家分享到

关于这两个内容  大家可以自己去实现一些相关功能的库

比如==煜姐==以前说的  写一个可以用注解访问网络请求的库（retrofit）  还有就是==哲哥==以前说的 自己去实现==Gson库==

当然只是简单实现  核心功能实现  我们不可能真的去实现那些库的所有功能咯。相信大家对于反射和注解会有深刻的理解

