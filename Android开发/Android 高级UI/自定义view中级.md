# =属性动画进阶==

## ViewPropertyAnimator

ViewPropertyAnimator其实算不上什么高级技巧，它的用法格外的简单，只不过和前面所学的所有属性动画的知识不同，它并不是在3.0系统当中引入的，而是在3.1系统当中附增的一个新的功能，因此这里我们把它作为整个属性动画系列的收尾部分。

我们都知道，属性动画的机制已经不是再针对于View而进行设计的了，而是一种不断地对值进行操作的机制，它可以将值赋值到指定对象的指定属性上。但是，在绝大多数情况下，我相信大家主要都还是对View进行动画操作的。Android开发团队也是意识到了这一点，没有为View的动画操作提供一种更加==便捷的用法==确实是有点太不人性化了，于是在Android 3.1系统当中补充了ViewPropertyAnimator这个机制.

那我们先来回顾一下之前的用法吧，比如我们想要让一个TextView从常规状态变成透明状态，就可以这样写：

```java
ObjectAnimator animator = ObjectAnimator.ofFloat(textview, "alpha", 0f);
animator.start();
```

看上去复杂吗？好像也不怎么复杂，但确实也不怎么容易理解。我们要将操作的view、属性、变化的值都一起传入到ObjectAnimator.ofFloat()方法当中，虽然看上去也没写几行代码，但这不太像是我们平时使用的面向对象的思维。


那么下面我们就来看一下如何使用ViewPropertyAnimator来实现同样的效果，ViewPropertyAnimator提供了更加易懂、更加面向对象的API，如下所示：

```java
textview.animate().alpha(0f);
```

果然非常简单！不过textview.animate()这个方法是怎么回事呢？animate()方法就是在Android 3.1系统上新增的一个方法，这个方法的返回值是一个ViewPropertyAnimator对象，也就是说拿到这个对象之后我们就可以调用它的各种方法来实现动画效果了，这里我们调用了alpha()方法并转入0，表示将当前的textview变成透明状态。

怎么样？比起使用ObjectAnimator，ViewPropertyAnimator的用法明显更加简单易懂吧。除此之外，ViewPropertyAnimator还可以很轻松地将多个动画组合到一起，比如我们想要让textview运动到500,500这个坐标点上，就可以这样写：

```java
textview.animate().x(500).y(500);
```

可以看出，ViewPropertyAnimator是支持连缀用法的，我们想让textview移动到横坐标500这个位置上时调用了x(500)这个方法，然后让textview移动到纵坐标500这个位置上时调用了y(500)这个方法，将所有想要组合的动画通过这种连缀的方式拼接起来，这样全部动画就都会一起被执行。

那么怎样去设定动画的运行时长呢？很简单，也是通过连缀的方式设定即可，比如我们想要让动画运行5秒钟，就可以这样写：

```java
textview.animate().x(500).y(500).setDuration(5000);
```

除此之外，本篇文章第一部分所学的Interpolator技术我们也可以应用在ViewPropertyAnimator上面，如下所示：

    textview.animate().x(500).y(500).setDuration(5000)
    		.setInterpolator(new BounceInterpolator());
------------------------------------------------
用法很简单，同样也是使用连缀的方式。相信大家现在都已经体验出来了，ViewPropertyAnimator其实并没有什么太多的技巧可言，用法基本都是大同小异的，需要用到什么功能就连缀一下，因此更多的用法大家只需要去查阅一下文档，看看还支持哪些功能，有哪些接口可以调用就可以了。

ViewPropertyAnimator、ObjectAnimator、ValueAnimator 这三种 Animator，
它们其实是一种递进的关系：从左到右依次变得更加难用，也更加灵活。

它们的性能是一样的，因为 ViewPropertyAnimator 和 ObjectAnimator 的内部实现其实都是 ValueAnimator，ObjectAnimator 更是本来就是 ValueAnimator 的子类，它们三个的性能并没有差别。

它们的差别只是使用的便捷性以及功能的灵活性。
所以在实际使用时候的选择，只要遵循一个原则就行：尽量用简单的。
能用 View.animate() 实现就不用 ObjectAnimator，
能用 ObjectAnimator 就不用 ValueAnimator。

老版本属性动画

```java
ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(btnShow, "translationX", 0, 300);
ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(btnShow, "rotation", 0, 360);
AnimatorSet animatorSet = new AnimatorSet();
animatorSet.setDuration(2000);
animatorSet.play(objectAnimator).with(objectAnimator1);
//animatorSet.playTogether(objectAnimator,objectAnimator1);
animatorSet.start();

```

简化版属性动画

```java
btnShow.animate()
        .setDuration(2000)
        .translationX(300)
        .rotation(360)
        .start();
```

### 特点

1、专门针对View对象动画而操作的类。
2、提供了更简洁的链式调用设置多个属性动画，这些动画可以同时进行的。
3、拥有更好的性能，多个属性动画是一次同时变化，只执行一次UI刷新（也就是只调用一次invalidate,而n个ObjectAnimator就会进行n次属性变化，就有n次invalidate）。
4、每个属性提供两种类型方法设置。
5、该类只能通过View的animate()获取其实例对象的引用

### 常用

   view.animate()//获取ViewPropertyAnimator对象
                        //动画持续时间
                        .setDuration(5000)

```java
                    //透明度
                    .alpha(0)
                    .alphaBy(0)

                    //旋转
                    .rotation(360)
                    .rotationBy(360)
                    .rotationX(360)
                    .rotationXBy(360)
                    .rotationY(360)
                    .rotationYBy(360)

                    //缩放
                    .scaleX(1)
                    .scaleXBy(1)
                    .scaleY(1)
                    .scaleYBy(1)

                    //平移
                    .translationX(100)
                    .translationXBy(100)
                    .translationY(100)
                    .translationYBy(100)
                    .translationZ(100)
                    .translationZBy(100)

                    //更改在屏幕上的坐标
                    .x(10)
                    .xBy(10)
                    .y(10)
                    .yBy(10)
                    .z(10)
                    .zBy(10)

                    //插值器
                    .setInterpolator(new BounceInterpolator())//回弹
                    .setInterpolator(new AccelerateDecelerateInterpolator())//加速再减速
                    .setInterpolator(new AccelerateInterpolator())//加速
                    .setInterpolator(new DecelerateInterpolator())//减速
                    .setInterpolator(new LinearInterpolator())//线性

                    //动画延迟
                    .setStartDelay(1000)

                    //是否开启硬件加速
                    .withLayer()

                    //监听
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            Log.i("MainActivity", "run: onAnimationStart");
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Log.i("MainActivity", "run: onAnimationEnd");
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Log.i("MainActivity", "run: onAnimationCancel");
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            Log.i("MainActivity", "run: onAnimationRepeat");
                        }
                    })

                    .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Log.i("MainActivity", "run: onAnimationUpdate==");
                        }
                    })

                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("MainActivity", "run: end");
                        }
                    })
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("MainActivity", "run: start");
                        }
                    })

                    .start();
```
### 顺序

```java
withStartAction（Runnable runnable）
→
onAnimationStart(Animator animation)
→
onAnimationUpdate（ValueAnimator animation）//动画过程中一直调用
→
onAnimationEnd(Animator animation)
→
withEndAction（Runnable runnable）

```

### 尾缀区别

**translationX与X的区别：**

```java
//translationX是指偏移量，而x是指具体移动到的坐标

.translationX(50f).translationY(50f)//平移（指定偏移量）

.x(50f).y(50f)//平移（指定坐标）
```

**有By结尾和没By结尾的区别：**

每个动画都有一个By的后缀的方法。加上By的意思是，继续动画这么多数值。不加By的意思是动画到这个数值。

有By：变化偏移

无By：变化到

### TypeEvaluator

TypeEvaluator是通过插值器的计算值和开始值以及结束值来实现对动画过程的控制的。我们可以通过一个函数表达式来表示TypeEvaluator的工作原理,如下：

![img](https://img-blog.csdn.net/20160823233844269)

### 注意


那么除了用法之外，关于ViewPropertyAnimator有几个细节还是值得大家注意一下的：

整个ViewPropertyAnimator的功能都是建立在View类新增的animate()方法之上的，这个方法会创建并返回一个ViewPropertyAnimator的实例，之后的调用的所有方法，设置的所有属性都是通过这个实例完成的。
大家注意到，在使用ViewPropertyAnimator时，我们自始至终没有调用过start()方法，这是因为新的接口中使用了隐式启动动画的功能，只要我们将动画定义完成之后，动画就会自动启动。并且这个机制对于组合动画也同样有效，只要我们不断地连缀新的方法，那么动画就不会立刻执行，等到所有在ViewPropertyAnimator上设置的方法都执行完毕后，动画就会自动启动。当然如果不想使用这一默认机制的话，我们也可以显式地调用start()方法来启动动画。
ViewPropertyAnimator的所有接口都是使用连缀的语法来设计的，每个方法的返回值都是它自身的实例，因此调用完一个方法之后可以直接连缀调用它的另一个方法，这样把所有的功能都串接起来，我们甚至可以仅通过一行代码就完成任意复杂度的动画功能。

## PropertyValuesHolder与Keyframe





### PropertyValuesHolder



讲了ValueAnimator、ObjectAnimator的知识，讲解了它们ofInt(),ofFloat(),ofObject()函数的用法。细心的同学可能会注意到，ValueAnimator、ObjectAnimator除了这些创建Animator实例的方法以外，都还有一个方法：

```java
/**
 * valueAnimator的
 */
public static ValueAnimator ofPropertyValuesHolder(PropertyValuesHolder... values) 
/**
 * ObjectAnimator的
 */
public static ObjectAnimator ofPropertyValuesHolder(Object target,PropertyValuesHolder... values)

```

也就是说ValueAnimator和ObjectAnimator除了通过ofInt(),ofFloat(),ofObject()创建实例外，还都有一个ofPropertyValuesHolder（）方法来创建实例，这篇文章我就带大家来看看如何通过ofPropertyValuesHolder（）来创建实例的。
由于ValueAnimator和ObjectAnimator都具有ofPropertyValuesHolder（）函数，使用方法也差不多，相比而言，ValueAnimator的使用机会不多，这里我们就只讲ObjectAnimator中ofPropertyValuesHolder（）的用法。相信大家懂了这篇以后，再去看ValueAnimator的ofPropertyValuesHolder（），也应该是会用的。
在这篇文章的最后，我们通过本篇内容做了一个电话响铃的效果，效果图如下：

![img](https://img-blog.csdn.net/20160226223147748)



一、PropertyValuesHolder
1、概述

PropertyValuesHolder这个类的意义就是，它其中保存了动画过程中所需要操作的属性和对应的值。我们通过ofFloat(Object target, String propertyName, float… values)构造的动画，ofFloat()的内部实现其实就是将传进来的参数封装成PropertyValuesHolder实例来保存动画状态。在封装成PropertyValuesHolder实例以后，后期的各种操作也是以PropertyValuesHolder为主的。
说到这里，大家就知道这个PropertyValuesHolder是有多有用了吧，上面我们也说了，ObjectAnimator给我们提供了一个口子，让我们自己构造PropertyValuesHolder来构造动画.

```java
public static ObjectAnimator ofPropertyValuesHolder(Object target,PropertyValuesHolder... values)
```

PropertyValuesHolder中有很多函数，有些函数的api等级是11，有些函数的api等级是14和21；
高api的函数我们就不讲了，只讲讲api 11的函数的用法。有关各个函数的api等级，大家可以参考《Google文档：PropertyValuesHolder》
首先，我们来看看创建实例的函数：



```java
public static PropertyValuesHolder ofFloat(String propertyName, float... values)
    
public static PropertyValuesHolder ofInt(String propertyName, int... values) 
    
public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator,Object... values)
    
public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values)
```

这里总共有四个创建实例的方法，这一段我们着重讲ofFloat、ofInt和ofObject的用法，ofKeyframe我们单独讲。

#### **ofFloat()、ofInt()**

（1）ofFloat()、ofInt()

我们先来看看它们的构造函数：

```java
public static PropertyValuesHolder ofFloat(String propertyName, float... values)
    
public static PropertyValuesHolder ofInt(String propertyName, int... values) 
```

其中：


    propertyName：表示ObjectAnimator需要操作的属性名。即ObjectAnimator需要通过反射查找对应属性的setProperty()函数的那个property.
    values：属性所对应的参数，同样是可变长参数，可以指定多个，还记得我们在ObjectAnimator中讲过，如果只指定了一个，那么ObjectAnimator会通过查找getProperty()方法来获得初始值。不理解的同学请参看《Animation动画详解(七)——ObjectAnimator基本使用》 
大家看这些参数是不是很眼熟，让我们看一下ObjectAnimator的ofFloat是怎么样的：

```java
public static ObjectAnimator ofFloat(Object target, String propertyName, float... values);
```

看到没，在ObjectAnimator.ofFloat中只比PropertyValuesHolder的ofFloat多了一个target，其它都是完全一样的！
好了，我们在讲完PropertyValuesHolder的ofFloat函数以后，我们再来看看如何将构造的PropertyValuesHolder实例设置进ObjectAnimator吧。

（2）、ObjectAnimator.ofPropertyValuesHolder()

在开篇时，我们也讲了ObjectAnimator给我们提供了一个设置PropertyValuesHolder实例的入口：

```java
public static ObjectAnimator ofPropertyValuesHolder(Object target,PropertyValuesHolder... values) 
```

其中：

    target：指需要执行动画的控件
    values：是一个可变长参数，可以传进去多个PropertyValuesHolder实例，由于每个PropertyValuesHolder实例都会针对一个属性做动画，所以如果传进去多个PropertyValuesHolder实例，将会对控件的多个属性同时做动画操作。 
3）、示例 

下面我们就举个例子来如何通过PropertyValuesHolder的ofFloat、ofInt来做动画的。
 效果图如下：![img](https://img-blog.csdn.net/20160226223929399)



这个动画很简单，就是在点击按钮的时候，给textView做动画，框架代码就不再讲了，我们主要来看看操作textview动画的代码。
 动画代码为：

```java
PropertyValuesHolder rotationHolder = PropertyValuesHolder.ofFloat("Rotation", 60f, -60f, 40f, -40f, -20f, 20f, 10f, -10f, 0f);

PropertyValuesHolder colorHolder = PropertyValuesHolder.ofInt("BackgroundColor", 0xffffffff, 0xffff00ff, 0xffffff00, 0xffffffff);

ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mTextView, rotationHolder, colorHolder);

animator.setDuration(3000);

animator.setInterpolator(new AccelerateInterpolator());

animator.start();
```

在这里，我们创建了两个PropertyValuesHolder实例，第一个rotationHolder：

```java
PropertyValuesHolder rotationHolder = PropertyValuesHolder.ofFloat("Rotation", 60f, -60f, 40f, -40f, -20f, 20f, 10f, -10f, 0f);
```

使用ofFloat函数创建，属性值是Rotation，对应的是View类中SetRotation(float rotation)函数。后面传进去很多值，让其左右摆动。
 第二是动画是改变背景色的colorHolder

```java
PropertyValuesHolder colorHolder = PropertyValuesHolder.ofInt("BackgroundColor", 0xffffffff, 0xffff00ff, 0xffffff00, 0xffffffff);
```

这里使用的是ofInt函数创建的，它操作的属性是BackgroundColor，对应的是View类中的setBackgroundColor(int color)函数，后面传进去的16进制颜色值让其在这些颜色值间变化。有关颜色值的变化，大家可以参考《Animation动画详解(七)——ObjectAnimator基本使用》中第三部分《常用函数》
最后通过ObjectAnimator.ofPropertyValuesHolder将rotationHolder、colorHolder设置给mTextView，构造出ObjectAnimator对象。然后开始动画即可

```java
ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mTextView, rotationHolder, colorHolder);
animator.setDuration(3000);
animator.setInterpolator(new AccelerateInterpolator());
animator.start();

```

好了，到这里有关PropertyValuesHolder的ofInt和ofFloat函数的用法就讲完了，大家可以看到PropertyValuesHolder使用起来也很容易，下面我们再来看看PropertyValuesHolder的ofObject的使用方法。

#### ofObject()

（1）、概述 

我们先来看一下ofObject的构造函数

```java
public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator,Object... values)
```

propertyName:ObjectAnimator动画操作的属性名;
evaluator:Evaluator实例，Evaluator是将当前动画进度计算出当前值的类，可以使用系统自带的IntEvaluator、FloatEvaluator也可以自定义，有关Evaluator的知识，大家可以参考《Animation动画详解(五)——ValueAnimator高级进阶（一）》
values：可变长参数，表示操作动画属性的值 
它的各个参数与ObjectAnimator.ofObject的类似,只是少了target参数而已

```java
public static ObjectAnimator ofObject(Object target, String propertyName,TypeEvaluator evaluator, Object... values)
```

（2）、示例 

下面我们就讲讲PropertyValuesHolder.ofObject()函数的用法
 本示例的效果图如下：![img](https://img-blog.csdn.net/20160226224343549)



这里实现的效果与[《Animation动画详解(六)——ValueAnimator高级进阶（二）》](http://blog.csdn.net/harvic880925/article/details/50549385)实现的效果相同，即通过自字义的CharEvaluator来自动实现字母的改变与计算。
 首先是自定义一个CharEvaluator,通过进度值来自动计算出当前的字母：

```java
public class CharEvaluator implements TypeEvaluator<Character> {
    @Override
    public Character evaluate(float fraction, Character startValue, Character endValue) {
        int startInt  = (int)startValue;
        int endInt = (int)endValue;
        int curInt = (int)(startInt + fraction *(endInt - startInt));
        char result = (char)curInt;
        return result;
    }
}

```

有关数字与字符间转换的原理已经在《Animation动画详解(六)——ValueAnimator高级进阶（二）》讲述，就不再细讲。这个CharEvaluator也是直接从这篇文章中拿过来的，强烈建议大家对这个系列文章从头开始看。
从CharEvaluator中可以看出，从CharEvaluator中产出的动画中间值类型为Character类型。TextView中虽然有setText(CharSequence text) 函数，但这个函数的参数类型是CharSequence，而不是Character类型。所以我们要自定义一个类派生自TextView来改变TextView的字

```java
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setCharText(Character character){
        setText(String.valueOf(character));
    }
}

```

在这里，我们定义了一个方法setCharText(Character character)，参数就是Character类型，我们知道这个方法所对应的属性是CharText；
 最后MyActivity,在点击按钮的时候开始动画，核心代码为：



```java
  public class MyActivity extends Activity {
    private Button btn;
    private TextView mTextView;
    private MyTextView mMyTv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);



mMyTv = (MyTextView)findViewById(R.id.mytv);
    btn = (Button) findViewById(R.id.btn);
    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
            doOfObjectAnim();
            
        }
    });
}
 
private void doOfObjectAnim(){
    PropertyValuesHolder charHolder = PropertyValuesHolder.ofObject("CharText",new CharEvaluator(),new Character('A'),new Character('Z'));
    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mMyTv, charHolder);
    animator.setDuration(3000);
    animator.setInterpolator(new AccelerateInterpolator());
    animator.start();
}
}
```
这部分代码，很好理解，在点击按钮的时候执行doOfObjectAnim()方法：

```java
PropertyValuesHolder charHolder = PropertyValuesHolder.ofObject("CharText",new CharEvaluator(),new Character('A'),new Character('Z'));
ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mMyTv, charHolder);
animator.setDuration(3000);
animator.setInterpolator(new AccelerateInterpolator());
animator.start();

```

首先是根据PropertyValuesHolder.ofObject生成一个PropertyValuesHolder实例，注意它的属性就是CharText，所对应的set函数就是setCharText,由于CharEvaluator的中间值是Character类型，所以CharText属性所对应的完整的函数声明为setCharText(Character character)；这也就是我们为什么要自定义一个MyTextView原因，就是因为TextView中没有setText(Character character)这样的函数。
然后就是利用ObjectAnimator.ofPropertyValuesHolder生成ObjectAnimator实例了，最后就是对animator设置并start了，没什么难度，就不再讲了

### Keyframe

通过前面几篇的讲解，我们知道如果要控制动画速率的变化，我们可以通过自定义插值器，也可以通过自定义Evaluator来实现。但如果真的让我们为了速率变化效果而自定义插值器或者Evaluator的话，恐怕大部分同学会有一万头草泥马在眼前奔过，因为大部分的同学的数学知识已经还给老师了。
为了解决方便的控制动画速率的问题，谷歌为了我等屁民定义了一个KeyFrame的类，KeyFrame直译过来就是关键帧。
关键帧这个概念是从动画里学来的，我们知道视频里，一秒要播放24帧图片，对于制作flash动画的同学来讲，是不是每一帧都要画出来呢？当然不是了，如果每一帧都画出来，那估计做出来一个动画片都得要一年时间；比如我们要让一个球在30秒时间内，从（0,0）点运动到（300，200）点，那flash是怎么来做的呢，在flash中，我们只需要定义两个关键帧，在动画开始时定义一个，把球的位置放在(0,0)点；在30秒后，再定义一个关键帧，把球的位置放在（300，200）点。在动画 开始时，球初始在是（0，0）点，30秒时间内就adobe flash就会自动填充，把球平滑移动到第二个关键帧的位置（300，200）点；
通过上面分析flash动画的制作原理，我们知道，一个关键帧必须包含两个原素，第一时间点，第二位置。即这个关键帧是表示的是某个物体在哪个时间点应该在哪个位置上。
所以谷歌的KeyFrame也不例外，KeyFrame的生成方式为：

```java
Keyframe kf0 = Keyframe.ofFloat(0, 0);
Keyframe kf1 = Keyframe.ofFloat(0.1f, -20f);
Keyframe kf2 = Keyframe.ofFloat(1f, 0);
```

上面生成了三个KeyFrame对象，其中KeyFrame的ofInt函数的声明为：

```java
public static Keyframe ofFloat(float fraction, float value)
```

- **fraction：**表示当前的显示进度，即从加速器中getInterpolation()函数的返回值；
- **value：**表示当前应该在的位置 

比如Keyframe.ofFloat(0, 0)表示动画进度为0时，动画所在的数值位置为0；Keyframe.ofFloat(0.25f, -20f)表示动画进度为25%时，动画所在的数值位置为-20；Keyframe.ofFloat(1f,0)表示动画结束时，动画所在的数值位置为0；
在理解了KeyFrame.ofFloat()的参数以后，我们来看看PropertyValuesHolder是如何使用KeyFrame对象的：

```java
public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values)
```

- **propertyName：**动画所要操作的属性名
- **values：**Keyframe的列表，PropertyValuesHolder会根据每个Keyframe的设定，定时将指定的值输出给动画。 

所以完整的KeyFrame的使用代码应该是这样的：

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
Keyframe frame2 = Keyframe.ofFloat(1, 0);
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame0,frame1,frame2);
 Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage,frameHolder);
animator.setDuration(1000);
animator.start();
```

第一步：生成Keyframe对象；
 第二步：利用PropertyValuesHolder.ofKeyframe()生成PropertyValuesHolder对象
 第三步：ObjectAnimator.ofPropertyValuesHolder()生成对应的Animator

#### 示例

![img](https://img-blog.csdn.net/20160227104102615)



看起来跟开篇的一样，仔细对比一下，还是有不同的，这里只是实现了左右震动，但并没有放大效果。

(1)、main.xml 

我们先来看看布局代码，代码很简单，一个btn,一个imageview

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:orientation="vertical"
              android:layout_width="fill_parent"
              android:layout_height="fill_parent">
    <Button
            android:id="@+id/btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="start anim"/>
 
    <ImageView
            android:id="@+id/img"
            android:layout_width="150dp"
            android:layout_height="wrap_content"
            android:scaleType="fitCenter"
            android:layout_gravity="center_horizontal"
            android:src="@drawable/phone"/>
</LinearLayout>

```



MyActivity.java

```java
public class MyActivity extends Activity {
    private ImageView mImage;
    private Button mBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mImage = (ImageView)findViewById(R.id.img);
        mBtn = (Button)findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOfFloatAnim();
            }
        });
    }
 
    private void doOfFloatAnim(){
        Keyframe frame0 = Keyframe.ofFloat(0f, 0);
        Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
        Keyframe frame2 = Keyframe.ofFloat(0.2f, 20f);
        Keyframe frame3 = Keyframe.ofFloat(0.3f, -20f);
        Keyframe frame4 = Keyframe.ofFloat(0.4f, 20f);
        Keyframe frame5 = Keyframe.ofFloat(0.5f, -20f);
        Keyframe frame6 = Keyframe.ofFloat(0.6f, 20f);
        Keyframe frame7 = Keyframe.ofFloat(0.7f, -20f);
        Keyframe frame8 = Keyframe.ofFloat(0.8f, 20f);
        Keyframe frame9 = Keyframe.ofFloat(0.9f, -20f);
        Keyframe frame10 = Keyframe.ofFloat(1, 0);
        PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame0,frame1,frame2,frame3,frame4,frame5,frame6,frame7,frame8,frame9,frame10);
 
        Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage,frameHolder);
        animator.setDuration(1000);
        animator.start();
    }
}

```

这段代码难度也不大，在点击按钮的时候，执行doOfFloatAnim()函数,关键问题在doOfFloatAnim()上：
 首先，我们定义了11个keyframe:

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
Keyframe frame2 = Keyframe.ofFloat(0.2f, 20f);
Keyframe frame3 = Keyframe.ofFloat(0.3f, -20f);
Keyframe frame4 = Keyframe.ofFloat(0.4f, 20f);
Keyframe frame5 = Keyframe.ofFloat(0.5f, -20f);
Keyframe frame6 = Keyframe.ofFloat(0.6f, 20f);
Keyframe frame7 = Keyframe.ofFloat(0.7f, -20f);
Keyframe frame8 = Keyframe.ofFloat(0.8f, 20f);
Keyframe frame9 = Keyframe.ofFloat(0.9f, -20f);
Keyframe frame10 = Keyframe.ofFloat(1, 0);
```

在这些keyframe中，首先指定在开始和结束时，旋转角度为0，即恢复原样：

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame10 = Keyframe.ofFloat(1, 0);
```

然后在过程中，让它左右旋转，比如在进度为0.2时，旋转到左边20度位置：

Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);

然后在进度为0.3时，旋转到右边20度位置:

Keyframe frame3 = Keyframe.ofFloat(0.3f, -20f);

其它类似。正是因为来回左右的旋转，所以我们看起来就表现为在震动
然后，根据这些Keyframe生成PropertyValuesHolder对象，指定操作的属性为rotation

```java
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame0,frame1,frame2,frame3,frame4,frame5,frame6,frame7,frame8,frame9,frame10);
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage,frameHolder);
animator.setDuration(1000);
animator.start();
```

最后，利用ObjectAnimator.ofPropertyValuesHolder(mImage,frameHolder)生成ObjectAnimator对象，并开始动画。
到这里想必大家已经对Keyframe有了初步的认识，下面我们就来详细的讲讲Keyframe;

#### ofFloat、ofInt

（1）、ofFloat、ofInt

上面我们看到Keyframe.ofFloat()函数的用法，其实Keyframe除了ofFloat()以外，还有ofInt()、ofObject()这些创建Keyframe实例的方法，Keyframe.ofObject()我们下部分再讲，这部分，我们着重看看ofFloat与ofInt的构造函数与使用方法：

```java
/**
 * ofFloat
 */
public static Keyframe ofFloat(float fraction) 
public static Keyframe ofFloat(float fraction, float value)
/**
 * ofInt
 */
public static Keyframe ofInt(float fraction)
public static Keyframe ofInt(float fraction, int value)

```

由于ofFloat和ofInt的构造函数都是一样的，我们这里只以ofFloat来例来说。
上面我们已经讲了ofFloat(float fraction, float value)的用法，fraction表示当前关键帧所在的动画进度位置，value表示当前位置所对应的值。
而另一个构造函数：

```java
public static Keyframe ofFloat(float fraction) 
```

这个构造函数比较特殊，只有一个参数fraction，表示当前关键帧所在的动画进度位置。那在这个进度时所对应的值要怎么设置呢？
 当然有方法啦，除了上面的构造函数，Keyframe还有一些常用函数来设置fraction，value和interpolator，定义如下：

常用函数

```java
/**
 * 设置fraction参数，即Keyframe所对应的进度
 */
public void setFraction(float fraction) 
/**
 * 设置当前Keyframe所对应的值
 */
public void setValue(Object value)
/**
 * 设置Keyframe动作期间所对应的插值器
 */
public void setInterpolator(TimeInterpolator interpolator)

```

这三个函数中，插值器的作用应该是比较难理解，如果给这个Keyframe设置上插值器，那么这个插值器就是从上一个Keyframe开始到当前设置插值器的Keyframe时，这个过程值的计算是利用这个插值器的，比如：

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
frame1.setInterpolator(new BounceInterpolator());
Keyframe frame2 = Keyframe.ofFloat(1f, 20f);
frame2.setInterpolator(new LinearInterpolator());

```

在上面的代码中，我们给frame1设置了插值器BounceInterpolator，那么在frame0到frame1的中间值计算过程中，就是用的就是回弹插值器；
同样，我们给frame2设置了线性插值器（LinearInterpolator），所以在frame1到frame2的中间值计算过程中，使用的就是线性插值器
很显然，给Keyframe.ofFloat(0f, 0)设置插值器是无效的，因为它是第一帧

（3）、示例1——没有插值器 

下面我们就举个例子来看下，如何使用上面的各个函数的用法，同样是基于上面的电话响铃的例子，如果我们只保存三帧，代码如下：

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.5f, 100f);
Keyframe frame2 = Keyframe.ofFloat(1);
frame2.setValue(0f);
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame0,frame1,frame2);
 
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage,frameHolder);
animator.setDuration(3000);
animator.start();

```

在这段代码中，总共就只有三个关键帧，最后一个Keyframe的生成方法是利用:

```java
Keyframe frame2 = Keyframe.ofFloat(1);
frame2.setValue(0f);
```



对于Keyframe而言，fraction和value这两个参数是必须有的，所以无论用哪种方式实例化Keyframe都必须保证这两个值必须被初始化。
 这里没有设置插值器，会使用默认的线性插值器（LinearInterpolator）
 效果图如下：![img](https://img-blog.csdn.net/20160227105238901)

示例2——使用插值器 

下面，我们给上面的代码加上插值器,着重看一下，插值器在哪部分起做用





```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.5f, 100f);
Keyframe frame2 = Keyframe.ofFloat(1);
frame2.setValue(0f);
frame2.setInterpolator(new BounceInterpolator());
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame0,frame1,frame2);
 
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage,frameHolder);
animator.setDuration(3000);
animator.start();

```

我们给最后一帧frame2添加上回弹插值器(BounceInterpolator),然后看看效果：

![img](https://img-blog.csdn.net/20160227105531433)

从效果图中可以看出，在frame1到frame2的过程中，使用了回弹插值器，所以从这里也可验证我们上面的论述：如果给当前帧添加插值器，那么在上一帧到当前帧的进度值计算过程中会使用这个插值器。
好了，到这里有关ofInt,ofFloat和常用的几个函数的讲解就结束了，下面我们再来看看ofObject的使用方法。

#### ofObject

与ofInt,ofFloat一样，ofObject也有两个构造函数：

 

 

    public static Keyframe ofObject(float fraction)
    public static Keyframe ofObject(float fraction, Object value)
同样，如果使用ofObject(float fraction)来构造，也必须使用setValue(Object value)来设置这个关键帧所对应的值。
 我们还以TextView更改字母的例子来使用下Keyframe.ofObject
 效果图如下：![img](https://img-blog.csdn.net/20160227105637008)

明显L前的12个字母变化的特别快，后面的14个字母变化的比较慢。
 我们使用到的MyTextView,CharEvaluator都与上面的一样，只是动画部分不同，这里只列出动画的代码：

```java
Keyframe frame0 = Keyframe.ofObject(0f, new Character('A'));
Keyframe frame1 = Keyframe.ofObject(0.1f, new Character('L'));
Keyframe frame2 = Keyframe.ofObject(1,new Character('Z'));
 
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("CharText",frame0,frame1,frame2);
frameHolder.setEvaluator(new CharEvaluator());
ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mMyTv,frameHolder);
animator.setDuration(3000);
animator.start();

```

在这个动画中，我们定义了三帧：

```java
Keyframe frame0 = Keyframe.ofObject(0f, new Character('A'));
Keyframe frame1 = Keyframe.ofObject(0.1f, new Character('L'));
Keyframe frame2 = Keyframe.ofObject(1,new Character('Z'));

```

frame0表示在进度为0的时候，动画的字符是A；frame1表示在进度在0.1的时候，动画的字符是L;frame2表示在结束的时候，动画的字符是Z；
 利用关键帧创建PropertyValuesHolder后，一定要记得设置自定义的Evaluator:

```java
frameHolder.setEvaluator(new CharEvaluator());
```



凡是使用ofObject来做动画的时候，都必须调用frameHolder.setEvaluator显示设置Evaluator，因为系统根本是无法知道，你动画的中间值Object真正是什么类型的。

#### 疑问

：如果没有设置进度为0或者进度为1时的关键帧，展示是怎样的？

首先，我们以下面这个动画为例：

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.5f, 100f);
Keyframe frame2 = Keyframe.ofFloat(1,0);
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation", frame0,frame1,frame2);

Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage, frameHolder);
animator.setDuration(3000);
animator.start();
```
这里有三个帧，在进度为0.5时，电话向右旋转100度，然后再转回来。
 效果图如下：

![img](https://img-blog.csdn.net/20160227105850479)

尝试一：去掉第0帧，将以第一帧为起始位置 

如果我们把第0帧去掉，只保留中间帧和结束帧，看结果会怎样

```java
Keyframe frame1 = Keyframe.ofFloat(0.5f, 100f);
Keyframe frame2 = Keyframe.ofFloat(1,0);
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame1,frame2);
 
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage, frameHolder);
animator.setDuration(3000);
animator.start();

```



![img](https://img-blog.csdn.net/20160227105957528)



可以看到，动画是直接从中间帧frame1开始的，即当没有第0帧时，动画从最近的一个帧开始。

尝试二：去掉结束帧，将最后一帧为结束帧 

如果我们把结束帧去掉，保留第0帧和中间帧，看结果会怎样

```java
Keyframe frame0 = Keyframe.ofFloat(0f, 0);
Keyframe frame1 = Keyframe.ofFloat(0.5f, 100f);
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation", frame0,frame1);
 
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage, frameHolder);
animator.setDuration(3000);
animator.start();

```



![img](https://img-blog.csdn.net/20160227110049763)



尝试三：只保留一个中间帧，会崩 

如果我们把第0帧和结束帧去掉，代码如下：

```java
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame1);
 
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage, frameHolder);
animator.setDuration(3000);
animator.start();

```



在点击按钮开始动画时，就直接崩了，报错信息如下：

![img](https://img-blog.csdn.net/20160227110500859?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

报错问题是数组越界，也就是说，至少要有两个帧才行。

尝试四：保留两个中间帧 

再尝试一下，如果我们把第0帧和结束帧去掉，保留两个中间帧会怎样：
 我们在上面代码上再加一个中间帧：

```java
Keyframe frame1 = Keyframe.ofFloat(0.5f, 100f);
Keyframe frame2 = Keyframe.ofFloat(0.7f,50f);
PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame1,frame2);
 
Animator animator = ObjectAnimator.ofPropertyValuesHolder(mImage, frameHolder);
animator.setDuration(3000);
animator.start();

```

![img](https://img-blog.csdn.net/20160227110621082)

##### 总结



可以看到，在保留两个帧的情况下，是可以运行的，而且，由于去掉了第0帧，所以将frame1做为起始帧，又由于去掉了结束帧，所以将frame2做为结束帧。
下面我们做出结论：

    如果去掉第0帧，将以第一个关键帧为起始位置
    如果去掉结束帧，将以最后一个关键帧为结束位置
    使用Keyframe来构建动画，至少要有两个或两个以上帧
------------------------------------------------
#### PropertyValuesHolder之其它函数

```java
/**
 * 设置动画的Evaluator
 */
public void setEvaluator(TypeEvaluator evaluator)
/**
 * 用于设置ofFloat所对应的动画值列表
 */
public void setFloatValues(float... values)
/**
 * 用于设置ofInt所对应的动画值列表
 */
public void setIntValues(int... values)
/**
 * 用于设置ofKeyframe所对应的动画值列表
 */
public void setKeyframes(Keyframe... values)
/**
 * 用于设置ofObject所对应的动画值列表
 */
public void setObjectValues(Object... values)
/**
 * 设置动画属性名
 */
public void setPropertyName(String propertyName)

```

这些函数都比较好理解，setFloatValues(float… values)对应PropertyValuesHolder.ofFloat()，用于动态设置动画中的数值。setIntValues、setKeyframes、setObjectValues同理；
setPropertyName用于设置PropertyValuesHolder所需要操作的动画属性名;
最重要的是setEvaluator(TypeEvaluator evaluator)

```java
/**
 * 设置动画的Evaluator
 */
public void setEvaluator(TypeEvaluator evaluator)
```

如果是利用PropertyValuesHolder.ofObject()来创建动画实例的话，我们是一定要显示调用 PropertyValuesHolder.setEvaluator()来设置Evaluator的。在上面的字母转换的例子中，我们已经用过这个函数了。这里也就没什么好讲的了。

#### 资源

源码下载地址：

CSDN:http://download.csdn.net/detail/harvic880925/9445780
github:https://github.com/harvic/BlogResForGitHub



























# ==路径动画==

##  PathMeasure

首先思考一个问题，任意绘制一条曲线，或者一个圆弧，或者一个不规则图形，又或者利用Path 同时绘制多条曲线，如何获取某个点的坐标，曲线的方向，对于简单的图形根据已知内容很容易得到坐标，对于类似贝塞尔曲线或者不规则图形想要或者坐标，角度信息很困难，今天就来讲解Path中用于获取上述信息的一个工具类。

==PathMeasure==是一个用来测量==Path的工具类==，可以从Path中获取固定位置的==坐标==，==横切信息==

**构造函数：**

```java
/**
 * Create an empty PathMeasure object. To uses this to measure the length
 * of a path, and/or to find the position and tangent along it, call
 * setPath.
  */
public PathMeasure() ；
/**
 * Create a PathMeasure object associated with the specified path object
 * (already created and specified). The measure object can now return the
 * path's length, and the position and tangent of any position along the
 * path.
 *
 * Note that once a path is associated with the measure object, it is
 * undefined if the path is subsequently modified and the the measure object
 * is used. If the path is modified, you must call setPath with the path.
 *
 * @param path The path that will be measured by this object
 * @param forceClosed If true, then the path will be considered as "closed"
 *        even if its contour was not explicitly closed.
 */
public PathMeasure(Path path, boolean forceClosed) ；
```

**主要有两个构造函数：**

PathMeasure()
无参构造函数，构造一个空的PathMeasure，想要使用必须和一个Path进行关联，可以利用setPath函数和Path设置关联。如果关联的Path被修改了，也需要重新调用setPath进行关联。
PathMeasure(Path path, boolean forceClosed)

参数说明：
path:关联的Path，如果想要重新关联新的path,可以调用函数setPath
forceClosed:设置测量Path时是否强制闭合path(有的Path不是闭合的，如果为true，那么测量时就会测量闭合的path，所以使用时要注意，forceClose为true时测量的长度可能更大。如果关联的Path被修改了，也需要重新调用setPath进行关联。

```JAVA
所以可以得出：
设置关联的Path之后，Path就不会再修改，就算原Path修改了，引用的Path也不会修改，想要应用原Path的修改就需要重新setPath。
```

**forceClosed 代码示例**

```JAVA
public class ViewDemo25 extends View {
    private Path mPath;
    private Paint mPaint1;
    private Paint mPaint2;
    public ViewDemo25(Context context) {
        this(context,null,0);
    }

    public ViewDemo25(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ViewDemo25(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setStrokeWidth(7);
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setColor(Color.RED);

        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setStrokeWidth(7);
        mPaint2.setColor(Color.RED);
        mPath = new Path();
        mPath.moveTo(100,100);
        mPath.lineTo(600,300);
        mPath.lineTo(300,500);
        PathMeasure pathMeasure1 = new PathMeasure(mPath, true);
        PathMeasure pathMeasure2 = new PathMeasure(mPath, false);
        System.out.println("===========pathMeasure1========"+pathMeasure1.getLength());
        System.out.println("===========pathMeasure2========"+pathMeasure2.getLength());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint1);
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109161536212.png)

**结果值：**
 ===pathMeasure11346.2852
 ===pathMeasure2899.0716
 Path是个非闭合曲线，forceClose为true获取到的Path长度明显大于forceClose为flase时的值。

## 方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109161555246.png)

```java
getLength： 用于获取 Path 的总长度，
getMatrix(float distance,Matrix matrix,int flags)：获取指定长度的位置(distance)坐标及该点Matrix
    
getPosTan(float distance,float[] pos,float[] tan):获取指定位置的坐标和tan值，tan值代表曲线方向，也就是切线值，
 
getSegment(float startD,float stopD,Path dst,boolean startWithMoveTo):获取两点之间的path片段，
    
setPath 设置 PathMeasure 与 Path 关联。
    
isClosed ：用于判断 Path 是否闭合，但需要注意一点，它不单单指Path本身的闭合，还指如果 关联 Path 的时候设置 forceClosed 为 true 的话，方法也返回true。
    
    
nextContour（）：获取下一条曲线的Path，因为Path可能包含多个相互之间不连接的Path。
```

### setpath

设置PathMeasure关联的Path，如果已设置的Path改变了，需要重新调用setPath，已设置的Path不会更新。

forceClosed:设置测量Path时是否强制闭合path(有的Path不是闭合的，如果为true，那么测量时就会测量闭合的path，所以使用时要注意，forceClose为true时测量的长度可能更大。
无论传入的forceClosed为true或者false，都不会影响原Path的绘制

**代码示例：**

```java
private void init() {
    mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint1.setStrokeWidth(7);
    mPaint1.setStyle(Paint.Style.STROKE);
    mPaint1.setColor(Color.RED);

    
    mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint2.setStrokeWidth(7);
    mPaint2.setColor(Color.RED);
    mPath = new Path();
    mPath.moveTo(100,100);
    mPath.lineTo(600,300);
    mPath.lineTo(300,500);
    PathMeasure pathMeasure1 = new PathMeasure();
    pathMeasure1.setPath(mPath, true);
    PathMeasure pathMeasure2 = new PathMeasure();
    pathMeasure2.setPath(mPath, false);
    System.out.println("===========pathMeasure1===1111====="+pathMeasure1.getLength());
    System.out.println("===========pathMeasure2====2222===="+pathMeasure2.getLength());

    mPath.lineTo(200,400);
    System.out.println("===========pathMeasure1===33333====="+pathMeasure1.getLength());
    System.out.println("===========pathMeasure2====4444===="+pathMeasure2.getLength());

    pathMeasure1.setPath(mPath, true);
    pathMeasure2.setPath(mPath, false);

    System.out.println("===========pathMeasure1===55555====="+pathMeasure1.getLength());
    System.out.println("===========pathMeasure2====66666===="+pathMeasure2.getLength());

}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawPath(mPath,mPaint1);
}

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109161757866.png)



结果：
=====pathMeasure1=1111=1346.2852
===pathMeasure22222899.0716
修改path后没有调用setPath的结果：
=====pathMeasure1=33333=1346.2852
===pathMeasure24444899.0716
修改path后调用setPath的结果：
=====pathMeasure1=55555=1356.7207
===pathMeasure2666661040.4929



```java
上面的例子首先测试了forceClosed 为true和false时的不同，接着测试了在生成PathMeasure实例后，修改关联的Path，再次获取PathMeasure信息，信息没有改变，之后重新调用setPath后，信息才改变。

所以如果测量过程中PathMeasure关联的Path发生改变，需要重新调用setPath方法进行关联。
```


### nextContour

```java
/**
 * Move to the next contour in the path. Return true if one exists, or
 * false if we're done with the path.
 */
public boolean nextContour();

```

由于后续需要，先讲解nextContour，看到这个函数取下一条曲线，也就是说明了一个Path可以有多条曲线。默认情况 getLength , getSegment 等PathMeasure方法，都只会在其中第一条线段上运行， nextContour 会跳转到下一条曲线到方法，如果跳转成功，则返回 true， 如果跳转失败，则返回 false。

**代码示例：**

```java
mPath = new Path();
mPath.moveTo(100,100);
mPath.lineTo(600,300);
mPath.lineTo(300,500);

mPath.addRect(400,400,900,900, Path.Direction.CW);
PathMeasure pathMeasure = new PathMeasure();
pathMeasure.setPath(mPath,false);
System.out.println("======getLength1111===="+pathMeasure.getLength());
if (pathMeasure.nextContour()){
    System.out.println("======getLength2222===="+pathMeasure.getLength());
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109161913912.png)

**输出结果：**
 ==getLength==1111899.0716
 ==getLength==22222000.0
 nextContour()返回true，移动到下一条曲线成功，使PathMeasure的方法作用于第二条曲线上。

### getLength

```java
/**
 * Return the total length of the current contour, or 0 if no path is
 * associated with this measure object.
 */
public float getLength() 
```

返回当前轮廓的总长度，根据nextContour()的介绍，知道返回的长度默认只是对第一条曲线的长度的计算。如果想要计算Path中所有曲线的总长度，需要循环调用nextContour,然后计算每条线段的长度，然后相加得到。

### isClosed

用于判断 Path 是否闭合，但需要注意一点，它不单单指Path本身的闭合，还指如果 关联 Path 的时候设置 forceClosed 为 true 的话，方法也返回true。

```java
mPath = new Path();
mPath.moveTo(100,100);
mPath.lineTo(600,300);
mPath.lineTo(300,500);

mPath.addRect(400,400,900,900, Path.Direction.CW);
PathMeasure pathMeasure = new PathMeasure();
pathMeasure.setPath(mPath,false);
System.out.println("======isclosed111===="+pathMeasure.isClosed());
if (pathMeasure.nextContour()){
    System.out.println("======isclosed222===="+pathMeasure.isClosed());
}
```

setPath是forceClosed传入false，结果为：
==isclosed111false
==isclosed222true

如果forceClosed传入true：
==isclosed111true
==isclosed222true

所以isClosed判断曲线是否闭合受forceClosed的影响。

### getSegment

getSegment获取Path中的一段Path。

```java
/**
 * Given a start and stop distance, return in dst the intervening
 * segment(s). If the segment is zero-length, return false, else return
 * true. startD and stopD are pinned to legal values (0..getLength()).
 * If startD >= stopD then return false (and leave dst untouched).
 * Begin the segment with a moveTo if startWithMoveTo is true.
 *
 * <p>On {@link android.os.Build.VERSION_CODES#KITKAT} and earlier
 * releases, the resulting path may not display on a hardware-accelerated
 * Canvas. A simple workaround is to add a single operation to this path,
 * such as <code>dst.rLineTo(0, 0)</code>.</p>
 */
public boolean getSegment(float startD, float stopD, Path dst, boolean startWithMoveTo) ;
```

**参数说明**：
startD，stopD:开始截取和结束截取的位置，两个参数都是距离Path起点的长度。
如果startD 大于stopD直接返回false，如果截取的长度等于0直接返回false，startD和stopD都大于0小于Path的总长度。
dst：如果截取成功，截取到的Path将被加入到dst中，不会影响dst中原有的曲线。
startWithMoveTo：起始点是否使用moveTo，如果startWithMoveTo为true，截取的Path放入dst时将会使用moveto，这样可以保证截取的Path起始点不变化。

返回值：返回true表示截取片段成功，返回false表示不成功，不操作dst。

**注意：如果系统版本低于等于4.4，默认开启硬件加速的情况下，更改 dst 的内容后可能绘制会出现问题，请关闭硬件加速或者给 dst 添加一个单个操作，例如: dst.rLineTo(0, 0)**

首先测试截取成功，是替换dst，还是把截取到的Path添加到dst中。

**原始图形：**

```java
private void init() {
    mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint1.setStrokeWidth(7);
    mPaint1.setStyle(Paint.Style.STROKE);
    mPaint1.setColor(Color.BLUE);

    mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint2.setStrokeWidth(7);
    mPaint2.setStyle(Paint.Style.STROKE);
    mPaint2.setColor(Color.RED);

    dst = new Path();
    dst.moveTo(300,500);
    dst.lineTo(700,700);

    mPath = new Path();
    mPath.addRect(200,200,900,900, Path.Direction.CW);
    PathMeasure pathMeasure = new PathMeasure();
    pathMeasure.setPath(mPath,true);
}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawPath(mPath,mPaint1);
    canvas.drawPath(dst,mPaint2);
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109162125891.png)

想要截取右下角的Path，示意图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109162138686.png)

上面的图形是个正方形，开始绘制的点（200,200），每个边长700，所以截取的segment开始点距离Path的开始点为700+350=1050，结束点为1400+350=1750。

```java
private void init() {
    mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint1.setStrokeWidth(7);
    mPaint1.setStyle(Paint.Style.STROKE);
    mPaint1.setColor(Color.BLUE);

    mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint2.setStrokeWidth(7);
    mPaint2.setStyle(Paint.Style.STROKE);
    mPaint2.setColor(Color.RED);

    dst = new Path();
    dst.moveTo(300,500);
    dst.lineTo(700,700);

    mPath = new Path();
    mPath.addRect(200,200,900,900, Path.Direction.CW);
    PathMeasure pathMeasure = new PathMeasure();
    pathMeasure.setPath(mPath,true);

    pathMeasure.getSegment(1050, 1750, dst, true);

}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109162210753.png)

**截取成功，然后添加截取到的Path到dst，dst中原来的Path完全没有受影响。**

**如果设置startWithMoveTo为false：**

可以看到如果startWithMoveTo 为 false，获取到的segment片段，加入到dst时，会将片段的起始点移动到 dst 的最后一个点，以保证 dst 的连续性，简单点说会忽略截取到的Path的第一个点替换成dst内部已有的最后一个点。

    总结：
    如果 startWithMoveTo 为 true, 获取到的片段Path会因为调用了moveTo保持原状，如果 startWithMoveTo 为 false，则会将截取出来的 Path 片段的起始点移动到 dst 的最后一个点，以保证 dst 的连续性。
### getPosTan

```java
/**
 * Pins distance to 0 <= distance <= getLength(), and then computes the
 * corresponding position and tangent. Returns false if there is no path,
 * or a zero-length path was specified, in which case position and tangent
 * are unchanged.
 *
 * @param distance The distance along the current contour to sample
 * @param pos If not null, returns the sampled position (x==[0], y==[1])
 * @param tan If not null, returns the sampled tangent (x==[0], y==[1])
 * @return false if there was no path associated with this measure object
*/
public boolean getPosTan(float distance, float pos[], float tan[])
```

获取Path路径上距离起点位置distance距离的坐标信息和tan值。

**参数说明：**
 distance:距离Path起点的位置长度，
 pos:获取该点的坐标值
 tan：改点的正切值,tan0是邻边边长，tan1是对边边长

用ps钢笔工![在这里插入图片描述](https://img-blog.csdnimg.cn/20190110133314900.png)具自己画个箭头。
 **代码示例：**

```java
private void init() {
    mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint1.setStrokeWidth(7);
    mPaint1.setStyle(Paint.Style.STROKE);
    mPaint1.setColor(Color.BLUE);

    mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint2.setStrokeWidth(7);
    mPaint2.setStyle(Paint.Style.STROKE);
    mPaint2.setColor(Color.RED);

    dst = new Path();
    dst.moveTo(300,500);
    dst.lineTo(700,700);

    mPath = new Path();
    mPath.addCircle(500,500,300, Path.Direction.CW);
    PathMeasure pathMeasure = new PathMeasure();
    pathMeasure.setPath(mPath,false);
    float zhouchang = (float) (2*Math.PI*300);
    System.out.println("=======circle周长========"+ zhouchang);
    float[] pos = new float[2];
    float[] tan = new float[2] ;
    pathMeasure.getPosTan(zhouchang*3/4,pos,tan);
    System.out.println("=========pos==========="+pos[0]+"  "+pos[1]);
    System.out.println("=========tan==========="+tan[0]+"  "+tan[1]);
}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawPath(mPath,mPaint1);
    canvas.drawPoint(500,500,mPaint2);
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109162347404.png)

输出结果：有稍微的误差是由于精度问题导致
=circle周长1884.9556
=pos=500.56152 200.00053
=tan===0.9999983 0.0018716537

tan的值有什么用呢，我们可以利用切线值得到当前线段的方向，也就是倾斜的角度。

float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
Math中 atan2 可以根据正切值得到角度值（也可以自己计算）弧度,然后将弧度转换为具体的度数。

如果不利用获取到的tan，直接根据坐标点绘制图片


```java
private void init() {
    mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint1.setStrokeWidth(7);
    mPaint1.setStyle(Paint.Style.STROKE);
    mPaint1.setColor(Color.BLUE);

    mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint2.setStrokeWidth(7);
    mPaint2.setStyle(Paint.Style.STROKE);
    mPaint2.setColor(Color.RED);

    dst = new Path();
    dst.moveTo(300,500);
    dst.lineTo(700,700);
    jiantou = BitmapFactory.decodeResource(getResources(),R.drawable.jiantou);

    mPath = new Path();
    mPath.addCircle(500,500,300, Path.Direction.CW);
    final PathMeasure pathMeasure = new PathMeasure();
    pathMeasure.setPath(mPath,false);
    final float zhouchang = (float) (2*Math.PI*300);
    System.out.println("=======circle周长========"+ zhouchang);
    float[] pos = new float[2];
    float[] tan = new float[2] ;
    pathMeasure.getPosTan(zhouchang*3/4,pos,tan);
    System.out.println("=========pos==========="+pos[0]+"  "+pos[1]);
    System.out.println("=========tan==========="+tan[0]+"  "+tan[1]);

    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,zhouchang);
    valueAnimator.setDuration(10000);
    valueAnimator.setInterpolator(new LinearInterpolator());
    valueAnimator.setRepeatCount(-1);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            float correntLen = (float) animation.getAnimatedValue();
            float[] pos = new float[2];
            float[] tan = new float[2] ;
            pathMeasure.getPosTan(correntLen,pos,tan);
            posX = pos[0];
            posY = pos[1];
            invalidate();
        }
    });

    valueAnimator.start();
}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawPath(mPath,mPaint1);
    canvas.drawPoint(500,500,mPaint2);
      canvas.drawBitmap(jiantou,posX,posY,mPaint2);
}
```



![在这里插入图片描述](https://img-blog.csdnimg.cn/2019010916242767.gif)

图片从左上角开始绘制，左上角会沿着坐标旋转，看着很不舒服。

**对图片进行操作：**
 利用获取的tan值对图片进行旋转和平移操作，注意这里的旋转操作时建立在图片的初始箭头方向为水平向右的，如果水平向左，则计算图片旋转方向时时不相同的（和绘制方向无关）。

```java
 private void init() {
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setStrokeWidth(7);
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setColor(Color.BLUE);

        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setStrokeWidth(7);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setColor(Color.RED);

        dst = new Path();
        dst.moveTo(300,500);
        dst.lineTo(700,700);
        mMatrix = new Matrix();

        jiantou = BitmapFactory.decodeResource(getResources(),R.drawable.jiantou);

        mPath = new Path();
        mPath.addCircle(500,500,300, Path.Direction.CCW);
        final PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(mPath,false);
        final float zhouchang = (float) (2*Math.PI*300);
        System.out.println("=======circle周长========"+ zhouchang);
        float[] pos = new float[2];
        float[] tan = new float[2] ;
        pathMeasure.getPosTan(zhouchang*3/4,pos,tan);
        System.out.println("=========pos==========="+pos[0]+"  "+pos[1]);
        System.out.println("=========tan==========="+tan[0]+"  "+tan[1]);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,zhouchang);
        valueAnimator.setDuration(10000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(-1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float correntLen = (float) animation.getAnimatedValue();
                float[] pos = new float[2];
                float[] tan = new float[2] ;
                pathMeasure.getPosTan(correntLen,pos,tan);
                posX = pos[0];
                posY = pos[1];
                mMatrix.reset();
                //得到当前坐标的方向趋势对应的角度
                float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);

                //下面两个比较重要，以图片中心为旋转点对图片进行旋转，把图片中心平移到Path上
                mMatrix.postRotate(degrees, jiantou.getWidth() / 2, jiantou.getHeight() / 2);
                mMatrix.postTranslate(pos[0] - jiantou.getWidth() / 2, pos[1] - jiantou.getHeight() / 2);
                invalidate();
            }
        });

        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint1);
        canvas.drawPoint(500,500,mPaint2);
        canvas.drawBitmap(jiantou,mMatrix,mPaint2);
          }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109162526656.gif)

### getMatrix

```java
/**
 * Pins distance to 0 <= distance <= getLength(), and then computes the
 * corresponding matrix. Returns false if there is no path, or a zero-length
 * path was specified, in which case matrix is unchanged.
 *
 * @param distance The distance along the associated path
 * @param matrix Allocated by the caller, this is set to the transformation
 *        associated with the position and tangent at the specified distance
 * @param flags Specified what aspects should be returned in the matrix.
 */
public boolean getMatrix(float distance, Matrix matrix, int flags) 
```

上面的例子利用getPosTan获取正切值，然后计算位置和旋转角度过程很麻烦，所以google提供了getmatrix直接获取变换后的Matrix矩阵供我们使用。

参数说明：
distance：距离Path起始点的距离
matrix:得到的matrix矩阵
flags：规定哪些信息添加到Matrix中，flags有两个值POSITION_MATRIX_FLAG，TANGENT_MATRIX_FLAG。POSITION_MATRIX_FLAG表示把位置信息添加到matrix中，TANGENT_MATRIX_FLAG表示把正切信息添加到matrix中。
可以分别添加POSITION_MATRIX_FLAG和TANGENT_MATRIX_FLAG，如果想同时添加这两者需要POSITION_MATRIX_FLAG|TANGENT_MATRIX_FLAG。
**利用getMatrix实现箭头功能。**

```java
valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        float correntLen = (float) animation.getAnimatedValue();
        float[] pos = new float[2];
        float[] tan = new float[2] ;
        pathMeasure.getPosTan(correntLen,pos,tan);
        mMatrix.reset();
        pathMeasure.getMatrix(correntLen,mMatrix,PathMeasure.TANGENT_MATRIX_FLAG|PathMeasure.POSITION_MATRIX_FLAG);

        //矩阵对旋转角度默认为图片的左上角，使用 preTranslate 调整原点为图片中心
        mMatrix.preTranslate(-jiantou.getWidth() / 2, -jiantou.getHeight() / 2);

     invalidate();
    }
});
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190109162620248.gif)



































# ==绘图进阶==

## Shader（渲染）

Shader是绘图过程中的着色器，它有五个子类：

    BitmapShader：         位图渲染
    LinearGradient：        线性渲染
    SweepGradient：        梯度渲染
    RadialGradient：        光束渲染
    ComposeShader：     组合渲染
渲染模式：Shader.TileMode

    Shader.TileMode.CLAMP：    边缘拉伸模式，它会拉伸边缘的一个像素来填充其他区域。
    Shader.TileMode.MIRROR：    镜像模式，通过镜像变化来填充其他区域。需要注意的是，镜像模式先进行y轴方向的镜像操作，然后在进行x轴方向上的镜像操作。
    Shader.TileMode.REPEAT：重复模式，通过复制来填充其他区域

下面的图：X轴是边缘拉伸模式，Y重复模式

镜像模式：xy轴均是镜像模式

![img](https://img-blog.csdn.net/20180626163130328?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppbm1pZTAxOTM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### BitmapShader

```java
构造方法：BitmapShader (Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY)

参数：

            bitmap：要处理的bitmap对象

            tileX：在X轴处理的效果，Shader.TileMode里有三种模式：CLAMP、MIRROR和REPETA

            tileY：在Y轴处理的效果，Shader.TileMode里有三种模式：CLAMP、MIRROR和REPETA
```
我们给画笔填充一个五角星，然后绘制一条直线

```java
Shader shader[] = new Shader[8];
bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.star);
shader[0] = new BitmapShader(bitmap,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
Paint paint = new Paint();
paint.setStyle(Paint.Style.FILL);
paint.setStrokeWidth(32);
paint.setShader(shader[0]);

int lineHeight = 100,lineOffset = 50;

canvas.drawLine(0,lineHeight,parentWidth,100,paint);

```

![img](https://img-blog.csdn.net/20180626163502715?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppbm1pZTAxOTM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### LinearGradient

linearGradient线性渐变，会用到Paint的setShader，Shader 被称为着色器，在opengl中这个概念经常被用到，android中的shader主要用来给图像着色，Shader在绘制过程中会返回横向重要的颜色组，Paint设置shader后，绘制时会从shader中获取颜色，也就是需要shader告诉画笔某处的颜色值。

```java
/**
 * Create a shader that draws a linear gradient along a line.
 *
 * @param x0       The x-coordinate for the start of the gradient line
 * @param y0       The y-coordinate for the start of the gradient line
 * @param x1       The x-coordinate for the end of the gradient line
 * @param y1       The y-coordinate for the end of the gradient line
 * @param color0   The color at the start of the gradient line.
 * @param color1   The color at the end of the gradient line.
 * @param tile     The Shader tiling mode
*/
public LinearGradient(float x0, float y0, float x1, float y1,
    @ColorInt int color0, @ColorInt int color1, @NonNull TileMode tile) ；


/**
 * Create a shader that draws a linear gradient along a line.
 *
 * @param x0           The x-coordinate for the start of the gradient line
 * @param y0           The y-coordinate for the start of the gradient line
 * @param x1           The x-coordinate for the end of the gradient line
 * @param y1           The y-coordinate for the end of the gradient line
 * @param colors       The colors to be distributed along the gradient line
 * @param positions    May be null. The relative positions [0..1] of
 *                     each corresponding color in the colors array. If this is null,
 *                     the the colors are distributed evenly along the gradient line.
 * @param tile         The Shader tiling mode
*/
public LinearGradient(float x0, float y0, float x1, float y1, @NonNull @ColorInt int colors[], @Nullable float positions[], @NonNull TileMode tile) ；

```

参数说明：
(x0,y0)：渐变起始点坐标
(x1,y1):渐变结束点坐标
color0:渐变开始点颜色,16进制的颜色表示，必须要带有透明度
color1:渐变结束颜色
colors:渐变数组
positions:位置数组，position的取值范围[0,1],作用是指定某个位置的颜色值，如果传null，渐变就线性变化。
tile:用于指定控件区域大于指定的渐变区域时，空白区域的颜色填充方法。

CLAMP边缘拉伸，为被shader覆盖区域，使用shader边界颜色进行填充
 -REPEAT 在水平和垂直两个方向上重复，相邻图像没有间隙
 -MIRROR以镜像的方式在水平和垂直两个方向上重复，相邻图像有间隙

第一个构造函数可以指定两个颜色之间的渐变，第二个构造函数可以指定多个颜色之间的渐变，线性渐变不但可以代码实现还可以xml文件实现，这里只讲解代码实现方式。

#### 颜色的线性渐变

只需要设置开始结束点坐标，开始颜色，结束颜色。
 **实例代码：**

```java
mPaint = new Paint();
mPaint.setColor(Color.BLUE);
mPaint.setAntiAlias(true);
mPaint.setStrokeWidth(3);
mPaint.setStyle(Paint.Style.FILL);
mPaint.setTextSize(20);

LinearGradient linearGradient = new LinearGradient(getWidth(),400,0,0,Color.RED,Color.GREEN, Shader.TileMode.CLAMP);
mPaint.setShader(linearGradient);
canvas.drawRect(0,0,getWidth(),400,mPaint);

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224175800393.png)

xml中设置渐变可以通过设置angle角度来改变渐变的开始结束，可以设置从上到下，从下到上，从左到右，从右到左，代码中如何设置呢？

 **如何通过坐标设置渐变方向**

通过坐标可以轻松实现，渐变方向的控制：
(0,0)->(0,400)从上到下
(0,400)->(0,0) 从下到上

0,0）->(getMeasuredWidth(),0) 表示从左到右
(getMeasuredWidth(),0)->(0,0) 表示从右到左

0,0）-> (getMeasuredWidth(),getMeasuredHeight()) 斜角，从左上角到右下角

从左到右：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224175838442.png)

从右到左：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224175843945.png)

** 渐变填充颜色总结**

    要实现从上到下需要设置shader开始结束点坐标为左上角到左下角或右上角到右下角坐标。
    
    要实现从下到上需要设置shader开始结束点坐标为左下角到左上角或右下角到右上角。
    
    要实现从左到右需要设置shader开始结束点坐标为左上角到右上角或者左下角到右下角。
    
    要实现从右到左需要设置shader开始结束坐标为右上角到左上角或者右下角到左下角。
    
    要实现对角shader，需要设置开始结束点坐标左上角到右下角。
#### 参数

LinearGradient(float x0, float y0, float x1, float y1, @NonNull @ColorInt int colors[], @Nullable float positions[], @NonNull TileMode tile) ；

positions为null时，线性填充，和没有positions数组的构造函数效果一样。

Positions数组中值为0-1,0表示开始绘制点，1表示结束点，0.5对应中间点等等。数组中位置信息对应颜色数组中的颜色。
//例如
int [] colors = {Color.RED,Color.GREEN, Color.BLUE};
float[] position = {0f, 0.3f, 1.0f};
上面position[0]对应数组中的第一个RED，0.3f的位置对应颜色中的GREEN，1.0f的位置对应颜色中的BLUE，所以从0-0.3的位置是从RED到GREEN的渐变，从0.3到1.0的位置的颜色渐变是GREEN到BLUE。

```java
int [] colors = {Color.RED,Color.GREEN, Color.BLUE};
float[] position = {0f, 0.3f, 1.0f};
LinearGradient linearGradient = new LinearGradient(0,0,getMeasuredWidth(),0,colors,position, Shader.TileMode.CLAMP);

mPaint.setShader(linearGradient);
canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224175950951.png)

如果把0.3改成0.7，

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224175954685.png)

#### 变色字体

```java
int [] colors = {Color.RED,Color.GREEN, Color.BLUE};
 float[] position = {0f, 0.7f, 1.0f};
 LinearGradient linearGradient = new LinearGradient(0,0,getMeasuredWidth(),0,colors,position, Shader.TileMode.CLAMP);

 mPaint.setShader(linearGradient);
// canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
 canvas.drawText("Android绘图小糊涂",0,getMeasuredHeight()/2,mPaint);

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224180007469.png)

#### 动态变色

如何让字体颜色不停地变动：
 Shader 可以设置matrix变换，利用translate不停地移动shader,实现渐变效果，下面的实例不能用于生产环境，我只是写个例子，后面会开文章讲解可用于生产的渐变。

```java
int [] colors = {Color.BLACK,Color.RED, Color.BLUE,Color.BLACK};
Rect rect = new Rect();
mPaint.getTextBounds(str,0,str.length(), rect);
int fontWidth = rect.width();
linearGradient = new LinearGradient(0,0,-fontWidth+10,0,colors,null, Shader.TileMode.CLAMP);
Matrix matrix = new Matrix();
matrix.setTranslate(tran,0);
linearGradient.setLocalMatrix(matrix);
tran = (tran + advance) ;
if (tran >= fontWidth*2){
   tran = 0;
}
mPaint.setShader(linearGradient);
canvas.drawText(str,0,getMeasuredHeight()/2,mPaint);
invalidate();

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224180040851.gif)

#### TileMode

如果shader的大小小于view的大小时如何绘制其他没有被shader覆盖的区域？
跟最后一个参数有关，
-CLAMP
边缘拉伸，利用边缘的颜色，填充剩余部分
-REPEAT
在水平和垂直两个方向上重复，相邻图像没有间隙，重复shader
-MIRROR
以镜像的方式在水平和垂直两个方向上重复，相邻图像有间隙，镜面shade

```java
LinearGradient linearGradient = new LinearGradient(0,0,getMeasuredWidth()/2,getMeasuredHeight()/2,Color.RED,Color.GREEN, Shader.TileMode.CLAMP);
mPaint.setShader(linearGradient);
canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);

```

**CLAMP：**
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224180104949.png)
 **REPEAT:**
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224180109312.png)
 **MIRROR:**

如果想要从对角线设置shader，图形最好是正方形这样比较好看设置：

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
   int width =  MeasureSpec.getSize(widthMeasureSpec);
    setMeasuredDimension(width,width);
}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    LinearGradient linearGradient = new LinearGradient(0,0,getMeasuredWidth()/2,getMeasuredHeight()/2,Color.RED,Color.GREEN, Shader.TileMode.MIRROR);
    mPaint.setShader(linearGradient);
    canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224180155547.png)





### SweepGradient

SweepGradient可以实现==扫描渐变渲染==，==类似雷达扫描图==，==渐变圆弧==，渐变==进度条==等，==构造函数==有两个：

```java
/**
 * A Shader that draws a sweep gradient around a center point.
 *
 * @param cx       The x-coordinate of the center
 * @param cy       The y-coordinate of the center
 * @param colors   The colors to be distributed between around the center.
 *                 There must be at least 2 colors in the array.
 * @param positions May be NULL. The relative position of
 *                 each corresponding color in the colors array, beginning
 *                 with 0 and ending with 1.0. If the values are not
 *                 monotonic, the drawing may produce unexpected results.
 *                 If positions is NULL, then the colors are automatically
 *                 spaced evenly.
 */
public SweepGradient(float cx, float cy,
        @NonNull @ColorInt int colors[], @Nullable float positions[])；
/**
 * A Shader that draws a sweep gradient around a center point.
 *
 * @param cx       The x-coordinate of the center
 * @param cy       The y-coordinate of the center
 * @param color0   The color to use at the start of the sweep
 * @param color1   The color to use at the end of the sweep
 */
public SweepGradient(float cx, float cy, @ColorInt int color0, @ColorInt int color1) 

```

 **参数**

**参数说明：**
 cx,cy 渐变中心坐标。
 color0,color1：渐变开始结束颜色。
 colors，positions：类似LinearGradient,用于多颜色渐变,positions为null时，根据颜色线性渐变。

#### 颜色渐变

**构造函数：**

```java
SweepGradient(float cx, float cy, @ColorInt int color0, @ColorInt int color1)
```



```java
sweepGradient = new SweepGradient(450,450,Color.RED, Color.GREEN);
mPaint.setShader(sweepGradient);
canvas.drawCircle(450,450,450,mPaint);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224192639676.png)

#### 多颜色渐变

**构造函数：**

```java
SweepGradient(float cx, float cy,@NonNull @ColorInt int colors[], @Nullable float positions[])
```



```java
int [] colors = {Color.BLACK,Color.RED, Color.BLUE,Color.GREEN};
sweepGradient = new SweepGradient(450,450,colors,null);
mPaint.setShader(sweepGradient);
canvas.drawCircle(450,450,450,mPaint);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224192659155.png)



**设置position：**

position数组设置主要作用是特定位置对应颜色数组，位置取值【0-1】，0表示开始位置，1表示结束位置，数组和颜色数组一一对应。

```java
int [] colors = {Color.BLACK,Color.RED, Color.BLUE,Color.GREEN};
float[] postions = {0f,0.1f,0.7f,1.0f};
sweepGradient = new SweepGradient(450,450,colors,postions);
mPaint.setShader(sweepGradient);
canvas.drawCircle(450,450,450,mPaint);

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224192822164.png)

可以明显看到BLUE蓝色变多。

#### 扫描渐变

此处只是示例代码，如果要真实现可以利用属性动画或者handler更新，

```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    startAngle = startAngle +1;

    if (startAngle == 360){
        startAngle = 0;
    }
    
    int [] colors = {Color.parseColor("#2300FF00"),Color.parseColor("#7F00FF00")};
    float[] position = new float[2];
    position[0] = (startAngle * 1f)/360f;
    position[1] = ((startAngle + 60)* 1.0f)/360f;
    sweepGradient = new SweepGradient(450,450,colors,position);
    mPaint.setShader(sweepGradient);
    canvas.drawCircle(450,450,450,mPaint2);
    canvas.drawCircle(450,450,350,mPaint2);
    canvas.drawCircle(450,450,200,mPaint2);
    canvas.drawCircle(450,450,100,mPaint2);
    float[] lines = {0, 450, 450, 450, 450,450,900, 450, 450, 0, 450, 450,450,450, 450, 900};
    canvas.drawLines(lines,mPaint2);

    canvas.drawArc(0,0,900,900,startAngle,endAngle,true,mPaint);

    invalidate();
}

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224192927647.gif)



#### 渐变圆弧

此处只是示例代码，如果要真实现可以利用属性动画或者handler更新，

```java
endAngle = endAngle +1;

if (endAngle >= 360){
    endAngle = 0;
}

int [] colors = {Color.parseColor("#FFFFFF00"),Color.parseColor("#FFFF0000")};
float[] position = new float[2];
position[0] = 0f;
position[1] = ((endAngle)* 1.0f)/360f;
sweepGradient = new SweepGradient(470,470,colors,position);
mPaint.setShader(sweepGradient);
canvas.drawCircle(470,470,450,mPaint2);

canvas.drawArc(20,20,920,920,startAngle,endAngle,false,mPaint);

invalidate();
```

可以看到，由于设置了positions数组，绘制多少圆弧，颜色是从圆弧开始绘制的地方渐变到圆弧结束弧度。
 positions数组的设置也有讲究，需要开始绘制的颜色对应positions数组值为0f，结束位置为((endAngle)* 1.0f)/360f;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224192942231.gif)

如果不设置positions数组参数，结果如下：

```java
endAngle = endAngle +1;

        if (endAngle >= 360){
            endAngle = 0;
        }

        int [] colors = {Color.parseColor("#FFFFFF00"),Color.parseColor("#FFFF0000")};
        float[] position = new float[2];
        position[0] = 0f;
        position[1] = ((endAngle)* 1.0f)/360f;
        sweepGradient = new SweepGradient(470,470,colors,null);
        mPaint.setShader(sweepGradient);
        canvas.drawCircle(470,470,450,mPaint2);

        canvas.drawArc(20,20,920,920,startAngle,endAngle,false,mPaint);

        invalidate();

```

可以看到圆弧的渐变颜色一直是从开始到整个圆结束，所以和不设置positions数组有较大区别。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181224193526424.gif)



































































### RadialGradient



RadialGradient被称为放射渐变，就是从中心向外圆形渐变。

**两个构造函数，第一个构造函数可以实现两种颜色的渐变，第二个构造函数可以实现多个颜色的渐变。**

```java
RadialGradient(float centerX, float centerY, float radius, @ColorInt int centerColor, @ColorInt int edgeColor, @NonNull TileMode tileMode)；

RadialGradient(float centerX, float centerY, float radius,
        @NonNull @ColorInt int colors[], @Nullable float stops[],
        @NonNull TileMode tileMode)

```

参数说明：
centerX ,centerY：shader的中心坐标，开始渐变的坐标。
radius:渐变的半径,
centerColor,edgeColor:中心点渐变颜色，边界的渐变颜色，
colors:渐变颜色数组，
stoops:渐变位置数组，类似扫描渐变的positions数组，取值[0,1],中心点为0，半径到达位置为1.0f，
tileMode:shader未覆盖以外的填充模式。

#### 颜色渐变

构造函数：
RadialGradient(float centerX, float centerY, float radius, @ColorInt int centerColor, @ColorInt int edgeColor, @NonNull TileMode tileMode)；

提供中心坐标，半径，颜色值，TileMode

```java
radialGradient = new RadialGradient(450,450,400,Color.RED,Color.GREEN, Shader.TileMode.CLAMP);
mPaint.setShader(radialGradient);
canvas.drawCircle(450,450,450,mPaint);
```



![在这里插入图片描述](https://img-blog.csdnimg.cn/20181225113138596.png)

#### 多种渐变

```java
May be <code>null</code>. Valid values are between <code>0.0f</code> and
*                 <code>1.0f</code>. The relative position of each corresponding color in
*                 the colors array. If <code>null</code>, colors are distributed evenly
*                 between the center and edge of the circle.

RadialGradient(float centerX, float centerY, float radius,
        @NonNull @ColorInt int colors[], @Nullable float stops[],
        @NonNull TileMode tileMode)
```

Stops数组取值为[0-1],一般为从小到大，表示每个位置对应的颜色值，如果stops不为null，colors必须和stops一一对应，否则可能导致崩溃，如果stops为null,各颜色从中心到边界线性渐变。

stops数组为null，四种颜色线性渐变：

```java
int [] colors = {Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW};
 float[] position = new float[4];
 position[0] = 0f;
 position[1] = 0.2f;
 position[2] = 0.9f;
 position[3] = 1.0f;
// radialGradient = new RadialGradient(450,450,400,Color.RED,Color.GREEN, Shader.TileMode.CLAMP);
 radialGradient = new RadialGradient(450,450,400,colors,null, Shader.TileMode.CLAMP);
 mPaint.setShader(radialGradient);
 canvas.drawCircle(450,450,450,mPaint);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181225113235544.png)

stops数组不为null：

```java
int [] colors = {Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW};
 float[] position = new float[4];
 position[0] = 0f;
 position[1] = 0.2f;
 position[2] = 0.9f;
 position[3] = 1.0f;
 radialGradient = new RadialGradient(450,450,400,colors,position, Shader.TileMode.CLAMP);
 mPaint.setShader(radialGradient);
 canvas.drawCircle(450,450,450,mPaint);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181225113253522.png)

如果数组多余颜色个数：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2018122511330994.png)

#### view 点击扩散

大致做个小例子，如果需要线上使用需要考虑很多问题，类似ondraw最好不要声明对象等：

```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    if (valueAnimator != null && valueAnimator.isRunning()){
        valueAnimator.cancel();
    }
    curx = event.getX();
    cury = event.getY();
    float maxRadius = 0;
    float maxRadius2 = 0;
    if ((width -curx) > curx){
        maxRadius = width -curx;
    }else{
        maxRadius = curx;
    }

    if ((heigth -cury) > cury){
        maxRadius2 = heigth -cury;
    }else{
        maxRadius2 = cury;
    }

    radius = Math.max(maxRadius, maxRadius2);
    valueAnimator = ValueAnimator.ofFloat(1,radius);
    valueAnimator.setDuration(2000);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
          tradius = (Float) animation.getAnimatedValue();
          radialGradient = new RadialGradient(curx,cury, tradius,Color.parseColor("#FFFFFFFF"),Color.GREEN, Shader.TileMode.CLAMP);
          mPaint.setShader(radialGradient);
          invalidate();
      }
  });

    valueAnimator.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            mPaint.setShader(null);
            tradius = 0;
            invalidate();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mPaint.setShader(null);
            tradius = 0;
            invalidate();
        }

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
        }
    });
    valueAnimator.start();

    return super.onTouchEvent(event);
}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawCircle(curx,cury,tradius,mPaint);
}

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181225113437818.gif)

替换为多颜色 private int[] colors = {Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN};![在这里插入图片描述](https://img-blog.csdnimg.cn/20181225134439125.gif)























### ComposeShader

组合渲染

 ComposeShader用来组合不同的Shader,可以将两个不同的Shader组合在一起

    构造函数:
    
        ComposeShader (Shader shaderA, Shader shaderB, Xfermode mode)
    
        ComposeShader (Shader shaderA, Shader shaderB, PorterDuff.Mode mode)
    
    参数：
    
        shaderA shaderB 两种渲染效果
    
        mode 叠加效果：PorterDuff图形混合模式介绍

将bitmapShader和RadialGradient模式复合

```java
lineHeight += lineOffset + 350;
bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.head);
shader[0] = new BitmapShader(bitmap, Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
shader[6] = new RadialGradient(150,lineHeight,550,Color.BLACK,Color.TRANSPARENT, Shader.TileMode.CLAMP);
//混合产生新的Shader.
shader[7] = new ComposeShader(shader[0],shader[6],PorterDuff.Mode.DST_IN);
paint.setShader(shader[7]);
//以新的Shader绘制一个圆。
canvas.drawCircle(150,lineHeight,550,paint);
```

左下角的渐渐模糊的图片便是组合效果![img](https://img-blog.csdn.net/20180626170440336?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppbm1pZTAxOTM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

全部代码：

```java
//shader 画笔填充
private void my_shader(Canvas canvas){

    //Shader.TileMode是指平铺模式
    //Shader.TileMode.CLAMP是边缘拉伸模式，它会拉伸边缘的一个像素来填充其他区域。
    //Shader.TileMode.MIRROR是镜像模式，通过镜像变化来填充其他区域。需要注意的是，镜像模式先进行y轴方向的镜像操作，然后在进行x轴方向上的镜像操作。
    //Shader.TileMode.REPEAT是重复模式，通过复制来填充其他区域

    //bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.head);
    Shader shader[] = new Shader[8];
    bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.star);
    shader[0] = new BitmapShader(bitmap,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setStrokeWidth(32);
    paint.setShader(shader[0]);

    int lineHeight = 100,lineOffset = 50;

    canvas.drawLine(0,lineHeight,parentWidth,100,paint);
    //canvas.drawCircle(240,240,100,paint);

    //LinearGradient是颜色线性渐变的着色器。
    //LinearGradient (float x0, float y0, float x1, float y1, int color0, int color1, Shader.TileMode tile)
    //（x0,y0）表示渐变的起点，（x1,y1）表示渐变的终点坐标，这两点都是相对于屏幕坐标系。color0,color1分别表示起点的颜色和终点的颜色。
    //LinearGradient (float x0, float y0, float x1, float y1, int[] colors, float[] positions, Shader.TileMode tile)
    //多色渐变的构造函数中，我们可以传入多个颜色，和每个颜色的占比。而且当positions参数传入null时，代表颜色是均匀的填充整个渐变区域的，显得比较柔和。
    lineHeight += lineOffset;
    shader[1] = new LinearGradient(0,lineHeight,parentWidth,lineHeight,Color.RED,Color.GREEN,Shader.TileMode.REPEAT);
    paint.setShader(shader[1]);
    canvas.drawLine(0,lineHeight,parentWidth,lineHeight,paint);

    lineHeight += lineOffset;
    shader[2] = new LinearGradient(0,lineHeight,parentWidth,lineHeight,GRADIENT_COLORS,null,Shader.TileMode.REPEAT);
    paint.setShader(shader[2]);
    canvas.drawLine(0,lineHeight,parentWidth,lineHeight,paint);

    //SweepGradient是梯度渐变，也称为扫描式渐变，效果有点类似与雷达扫描效果。
    //SweepGradient(float cx, float cy, int color0, int color1)
    // (cx,cy)表示渐变效果的中心点，也就是雷达扫描的圆点。color0和color1表示渐变的起点色和终点色。
    // 颜色渐变是顺时针的，从中心点的x轴正方形开始。
    // 注意:这里构造函数并不需要TileMode,因为梯度渐变的边界相当于无限大的。
    //SweepGradient(float cx, float cy,int colors[], float positions[])
    //colors[]颜色数组
    //positions数组，该数组中每一个position对应colors数组中每个颜色在360度中的相对位置，
    // position取值范围为[0,1]，0和1都表示3点钟位置，0.25表示6点钟位置，0.5表示9点钟位置，0.75表示12点钟位置，
    lineHeight += lineOffset +32;
    shader[3] = new SweepGradient(150,lineHeight,GRADIENT_COLORS,null);
    paint.setShader(shader[3]);
    canvas.drawCircle(150,lineHeight,50,paint);


    shader[4] = new SweepGradient(450,lineHeight,GRADIENT_COLORS,GRADIENT_POSITONS);
    paint.setShader(shader[4]);
    canvas.drawCircle(450,lineHeight,50,paint);

    //RadialGradient:创建从中心向四周发散的辐射渐变效果，其有两个构造函数：
    //RadialGradient(float centerX, float centerY, float radius, int centerColor, int edgeColor, Shader.TileMode tileMode)
    //centerX  圆心的X坐标
    //centerY  圆心的Y坐标
    //radius   圆的半径
    //centerColor  中心颜色
    //edgeColor   边缘颜色
    //RadialGradient(float centerX, float centerY, float radius, int[] colors, float[] stops, Shader.TileMode tileMode)
    //colors[]传入多个颜色值进去，这样就会用colors数组中指定的颜色值一起进行颜色线性插值。
    // stops数组，该数组中每一个stop对应colors数组中每个颜色在半径中的相对位置，
    // stop[]取值范围为[0,1]，0表示圆心位置，1表示圆周位置。如果stops数组为null，那么Android会自动为colors设置等间距的位置。
    lineHeight += lineOffset + 150;
    shader[5] = new RadialGradient(150,lineHeight,10,Color.GREEN,Color.RED,Shader.TileMode.MIRROR);
    paint.setShader(shader[5]);
    canvas.drawCircle(150,lineHeight,100,paint);

    if ( period < 250 || period >= 650){
        period = 250;
    }else {
        period += 5F;
    }
    shader[6] = new RadialGradient(period,lineHeight,30,GRADIENT_COLORS,null,Shader.TileMode.MIRROR);
    paint.setShader(shader[6]);
    canvas.drawCircle(450,period,100,paint);


    //ComposeShader用来组合不同的Shader,可以将两个不同的Shader组合在一起，它有两个构造函数:
    //ComposeShader (Shader shaderA, Shader shaderB, Xfermode mode)
    //ComposeShader (Shader shaderA, Shader shaderB, PorterDuff.Mode mode)
    lineHeight += lineOffset + 350;
    bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.head);
    shader[0] = new BitmapShader(bitmap, Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
    shader[6] = new RadialGradient(150,lineHeight,550,Color.BLACK,Color.TRANSPARENT, Shader.TileMode.CLAMP);
    //混合产生新的Shader.
    shader[7] = new ComposeShader(shader[0],shader[6],PorterDuff.Mode.DST_IN);
    paint.setShader(shader[7]);
    //以新的Shader绘制一个圆。
    canvas.drawCircle(150,lineHeight,550,paint);
}
==
```

# ==贝塞尔曲线==

## 介绍

贝塞尔曲线于1962年，由法国工程师皮埃尔·贝塞尔（Pierre Bézier）所广泛发表，他运用贝塞尔曲线来为汽车的主体进行设计。贝塞尔曲线最初由 Paul de Casteljau 于1959年运用de Casteljau 算法开发，以稳定数值的方法求出贝塞尔曲线。

## 示例

[github代码直通车](https://link.jianshu.com?t=https://github.com/18380438200/MyBezierView)

啥也不说了，先上效果图：

![img](https://upload-images.jianshu.io/upload_images/8669504-d2ed3ccdb687480d.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270)![img](https://upload-images.jianshu.io/upload_images/8669504-e360b31e09504e2e.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270)

需用到Path方法理解：

```java
mPath.moveTo    移动到操作起始点坐标，不会进行绘制，只用于移动移动画笔
 mPath.lineTo     从一个点连线到另一个点，用于进行直线绘制
 mPath.quadTo(x1, y1, x2, y2)     生成二次贝塞尔曲线，(x1,y1) 为控制点，(x2,y2)为结束点
 mPath.cubicTo(x1, y1, x2, y2, x3, y3)    生成三次贝塞尔曲线， (x1,y1) 为控制点，(x2,y2)为控制点，(x3,y3) 为结束点；即与二阶区别多一个控制点
```

日常哪些是贝塞尔曲线？
 刷礼物；水滴动画；翻书效果；天气预报曲线图等；

![img](https://upload-images.jianshu.io/upload_images/8669504-f7dbfa1fab29b1f8.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270)

![img](https://upload-images.jianshu.io/upload_images/8669504-b7b9d439ab8e825e.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270)

线性贝塞尔曲线函数中的 t 会经过由 P0 至P1 的 B(t) 所描述的曲线。例如当 t=0.25时，B(t) 即一条由点 P0 至 P1 路径的四分之一处。就像由 0 至 1 的连续 t，B(t) 描述一条由 P0 至 P1 的直线。

![img](https://upload-images.jianshu.io/upload_images/8669504-89012ea9a01b2a73.gif?imageMogr2/auto-orient/strip|imageView2/2/w/360)

为建构二次贝塞尔曲线，可以中介点 Q0 和 Q1 作为由 0 至 1 的 t：
 由 P0 至 P1 的连续点 Q0，描述一条线性贝塞尔曲线。
 由 P1 至 P2 的连续点 Q1，描述一条线性贝塞尔曲线。
 由 Q0 至 Q1 的连续点 B(t)，描述一条二次贝塞尔曲线。

![img](https://upload-images.jianshu.io/upload_images/8669504-6ff436a3d7f661c2.gif?imageMogr2/auto-orient/strip|imageView2/2/w/360)

对于三次曲线，可由线性贝塞尔曲线描述的中介点 Q0、Q1、Q2，和由二次曲线描述的点 R0、R1 所建构

![img](https://upload-images.jianshu.io/upload_images/8669504-afc4a89973890224.gif?imageMogr2/auto-orient/strip|imageView2/2/w/360)



## 二阶贝塞尔

.以屏幕为中心绘制左右两个定点，中心偏上绘制移动点；2.drawline()将3个点路径相连；3.onTouchEvent()方法中获取手势的点；4.path.moveTo(startX,startY)从起始点开始绘制，path.quadTo(eventX,eventY,endX,endY)以触碰点为控制点，右定点为结束点，绘制path。

```java
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawQuadToView extends View{
    private int eventX,eventY;
    private int centerX,centerY;
    private int startX,startY;
    private int endX,endY;
    private Paint paint;

    public DrawQuadToView(Context context) {
        super(context);
        init();
    }

    public DrawQuadToView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    //测量大小完成以后回调
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w/2;
        centerY = h/2;
        startX = centerX-250;
        startY = centerY;
        endX = centerX + 250;
        endY = centerY;
        eventX = centerX;
        eventY = centerY - 250;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.GRAY);
        //画3个点
        canvas.drawCircle(startX,startY,8,paint);
        canvas.drawCircle(endX,endY,8,paint);
        canvas.drawCircle(eventX,eventY,8,paint);

        //绘制连线
        paint.setStrokeWidth(3);
        canvas.drawLine(startX,centerY,eventX,eventY,paint);
        canvas.drawLine(endX,centerY,eventX,eventY,paint);

        //画二阶贝塞尔曲线
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(startX,startY);
        path.quadTo(eventX,eventY,endX,endY);
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                eventX = (int) event.getX();
                eventY = (int) event.getY();
                invalidate();
                break;
        }
        return true;
    }

}
```

## 三阶贝塞尔

1.绘制2个定点和2个移动点；2.绘制点路径；3.选择操作左控制点还是右控制点，即分别修改左右点坐标；4.以左定点为起点， path.cubicTo(leftX,leftY,rightX,rightY,endX,endY)，触点1，触点2为控制点，右定点为结束点绘制三阶贝塞尔曲线。

```java
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawCubicView extends View{
    private int leftX,leftY;
    private int rightX,rightY;
    private int centerX,centerY;
    private int startX,startY;
    private int endX,endY;
    private Paint paint;
    private boolean isMoveLeft;

    public DrawCubicView(Context context) {
        super(context);
        init();
    }

    public DrawCubicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    //测量大小完成以后回调
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w/2;
        centerY = h/2;
        startX = centerX-250;
        startY = centerY;
        endX = centerX + 250;
        endY = centerY;
        leftX = startX;
        leftY = centerY-250;
        rightX = endX;
        rightY = endY - 250;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.GRAY);
        //画4个点
        canvas.drawCircle(startX,startY,8,paint);
        canvas.drawCircle(endX,endY,8,paint);
        canvas.drawCircle(leftX,leftY,8,paint);
        canvas.drawCircle(rightX,rightY,8,paint);

        //绘制连线
        paint.setStrokeWidth(3);
        canvas.drawLine(startX,startY,leftX,leftY,paint);
        canvas.drawLine(leftX,leftY,rightX,rightY,paint);
        canvas.drawLine(rightX,rightY,endX,endY,paint);

        //画二阶贝塞尔曲线
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(startX,startY);
        path.cubicTo(leftX,leftY,rightX,rightY,endX,endY);
        canvas.drawPath(path,paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(isMoveLeft){
                    leftX = (int) event.getX();
                    leftY = (int) event.getY();
                }else{
                    rightX = (int) event.getX();
                    rightY = (int) event.getY();
                }
                invalidate();
                break;
        }
        return true;
    }

    public void moveLeft(){
        isMoveLeft = true;
    }

    public void moveRight(){
        isMoveLeft = false;
    }

}
```

## 常见例子

### 刷礼物



[github代码直通车](https://link.jianshu.com?t=https://github.com/18380438200/BerzierGift)![img](https://upload-images.jianshu.io/upload_images/8669504-50defd2cba07f172.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270)

自制效果：(背景仍为截图)

![img](https://upload-images.jianshu.io/upload_images/8669504-1cb5593d1a224b5c.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270)

#### 实现思路

1.写一个自定义Relativelayout，每次点击，就在底部添加一个imageview。
 2.随机显示一种礼物图标。
 3.先让imageview做放大动画出现，再绘制贝塞尔曲线。
 4.计算曲线路径，根据曲线实时设置imageview坐标。

准备工作，知道TypeEvaluator：
 这个类是动画能实现的基础，在获取动画对象时只需要传入起始和结束值系统就会自动完成值的平滑过渡。因为根据动画的类型，内部的evaluate()方法根据规则计算路径上的点坐标，并返回这个Point。当然也可以继承TypeEvaluator自定义计算规则。

使用方法：
 ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF(), new PointF());
 设置动画监听，在onAnimationUpdate()回调中PointF pointF = (PointF) animation.getAnimatedValue()或去当前路径上的点pointF。

TypeEvaluator接口：

```php
public interface TypeEvaluator<T> {

    /**
     * This function returns the result of linearly interpolating the start and end values, with
     * <code>fraction</code> representing the proportion between the start and end values. The
     * calculation is a simple parametric calculation: <code>result = x0 + t * (x1 - x0)</code>,
     * where <code>x0</code> is <code>startValue</code>, <code>x1</code> is <code>endValue</code>,
     * and <code>t</code> is <code>fraction</code>.
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A linear interpolation between the start and end values, given the
     *         <code>fraction</code> parameter.
     */
    public T evaluate(float fraction, T startValue, T endValue);

}
```



#### 实现步骤

1.创建数组，添加5个礼物drawable图标。设置布局参数为底部居中。

```cpp
  private void init() {
        drawables[0] = ContextCompat.getDrawable(getContext(),R.mipmap.ic1);
        drawables[1] = ContextCompat.getDrawable(getContext(),R.mipmap.ic2);
        drawables[2] = ContextCompat.getDrawable(getContext(),R.mipmap.ic3);
        drawables[3] = ContextCompat.getDrawable(getContext(),R.mipmap.ic4);
        drawables[4] = ContextCompat.getDrawable(getContext(),R.mipmap.ic5);

        layoutParams = new LayoutParams(100,100);
        //代码设置布局方式，底部居中
        layoutParams.addRule(CENTER_HORIZONTAL,TRUE);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM,TRUE);

    }
```

2.点击添加imageview，同时启动放大出现动画和贝塞尔漂流记动画。

```cpp
    public void addImageView(){
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawables[(int) (Math.random()*drawables.length)]);
        imageView.setLayoutParams(layoutParams);

        addView(imageView);
        setAnim(imageView).start();
        getBezierValueAnimator(imageView).start();
    }
```

3.放大动画

```cpp
    private AnimatorSet setAnim(View view){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.2f, 1f);

        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());//线性变化
        enter.playTogether(scaleX,scaleY);
        enter.setTarget(view);

        return enter;
    }
```

4.给动画设置自定义BezierEvaluator类，设置底部居中为贝塞尔起点，结束点随机在宽度以内，高度50以内。

```cpp
    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(),getPointF());

        //这里最好画个图 理解一下 传入了起点 和 终点
        PointF randomEndPoint = new PointF((float) (Math.random()*screenWidth), (float) (Math.random()*50));
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF(screenWidth / 2, screenHeight), randomEndPoint);
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        return animator;
    }
```

每次产生随机控制点

```cpp
    private PointF getPointF() {
        PointF pointF = new PointF();
        pointF.x = (float) (Math.random()*screenWidth);
        pointF.y = (float) (Math.random()*screenHeight/4);
        return pointF;
    }
```

5.自定义BezierEvaluator路径计算类，构造方法传入2个点，即设置为三阶贝塞尔曲线的2个控制点。依照三阶贝塞尔曲线计算公式：![img](https://upload-images.jianshu.io/upload_images/8669504-27c04a1fdc6d0136.png?imageMogr2/auto-orient/strip|imageView2/2/w/421)

再结合startPoint和endPoint计算当前路径上点point并return。

```java
    public class BezierEvaluator implements TypeEvaluator<PointF> {


        private PointF pointF1;
        private PointF pointF2;
        public BezierEvaluator(PointF pointF1,PointF pointF2){
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }
        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果

            point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (endValue.x);

            point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }
```

6.属性动画监听，拿到该路径点给imageview设置坐标；并设置逐渐透明的动画。

```java
    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }
```

```none
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@mipmap/bg"
    tools:context="com.example.berziergift.berziergift.MainActivity">

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:orientation="vertical"
        android:gravity="right"
        android:layout_alignParentRight="true"
        android:layout_marginBottom="40dp">

        <com.example.berziergift.berziergift.MyGiftView
            android:id="@+id/giftview"
            android:layout_width="100dp"
            android:layout_height="300dp"/>

        <ImageView
            android:layout_width="60dp"
            android:layout_height="60dp"
            android:onClick="like"
            android:layout_marginRight="5dp"
            android:layout_below="@id/giftview"/>
    </LinearLayout>

</RelativeLayout>
```

#### 完整代码：

```java
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by libo on 2017/11/28.
 */

public class MyGiftView extends RelativeLayout{
    private int screenWidth;
    private int screenHeight;
    private LayoutParams layoutParams;
    private Drawable[] drawables = new Drawable[5];

    public MyGiftView(Context context) {
        super(context);
        init();
    }

    public MyGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        drawables[0] = ContextCompat.getDrawable(getContext(),R.mipmap.ic1);
        drawables[1] = ContextCompat.getDrawable(getContext(),R.mipmap.ic2);
        drawables[2] = ContextCompat.getDrawable(getContext(),R.mipmap.ic3);
        drawables[3] = ContextCompat.getDrawable(getContext(),R.mipmap.ic4);
        drawables[4] = ContextCompat.getDrawable(getContext(),R.mipmap.ic5);

        layoutParams = new LayoutParams(100,100);
        //代码设置布局方式，底部居中
        layoutParams.addRule(CENTER_HORIZONTAL,TRUE);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM,TRUE);

    }

    public void addImageView(){
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawables[(int) (Math.random()*drawables.length)]);
        imageView.setLayoutParams(layoutParams);

        addView(imageView);
        setAnim(imageView).start();
        getBezierValueAnimator(imageView).start();
    }

    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(),getPointF());

        //这里最好画个图 理解一下 传入了起点 和 终点
        PointF randomEndPoint = new PointF((float) (Math.random()*screenWidth), (float) (Math.random()*50));
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF(screenWidth / 2, screenHeight), randomEndPoint);
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        return animator;
    }

    /**
     * 产生随机控制点
     * @return
     */
    private PointF getPointF() {
        PointF pointF = new PointF();
        pointF.x = (float) (Math.random()*screenWidth);
        pointF.y = (float) (Math.random()*screenHeight/4);
        return pointF;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
    }

    private AnimatorSet setAnim(View view){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.2f, 1f);

        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());//线性变化
        enter.playTogether(scaleX,scaleY);
        enter.setTarget(view);

        return enter;
    }

    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }

    public class BezierEvaluator implements TypeEvaluator<PointF> {


        private PointF pointF1;
        private PointF pointF2;
        public BezierEvaluator(PointF pointF1,PointF pointF2){
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }
        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果

            point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (endValue.x);

            point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }

}
```

# ==过渡动画==

## **博客**

![img](http://5b0988e595225.cdn.sohucs.com/images/20190619/6f0d0f60e0de4d7c88b4122aa59658cd.jpeg)









由于笔记太烂了 还是放一篇博客在这里

[【Transition】Android炫酷的Activity切换效果，共享元素](https://www.jianshu.com/p/a43daa1e3d6e)

## Transition

### 简介

Transition框架是安卓4以后引入的一个转场框架。可以在场景转换时的加入转场动画(`slide`,`fade`等等)效果，使app更加炫酷。 这里需要注意的是Transition的用途有两种。

1. Activity和Activity之间， Fragment和Fragment之间，Activity和Fragment之间。
2. 在一个ViewGroup内的布局变化。

**名词解释**

还需要解释下关键的名词。 `Scene`: 一个布局场景 `Transition`: 两个场景间的动态变化，即可以理解为会产生动画效果

当一个`Scene`到另一个`Scene`时，`Transition`的职责为如下：

1. 获取前后两个`Scene`的状态
2. 创建`Animator`，并计算动画效果，然后播放动画。

### 相关动画类型

![img](https://user-gold-cdn.xitu.io/2020/2/16/1704d376221c56ff?imageView2/0/w/1280/h/960/ignore-error/1)





| 系统Transition       | 解释                                                         |
| -------------------- | ------------------------------------------------------------ |
| ChangeBounds         | 检测View的位置边界创建移动和缩放动画(关注布局边界的变化)     |
| ChangeTransform      | 检测View的scale和rotation创建缩放和旋转动画(关注scale和ratation的变化) |
| ChangeClipBounds     | 检测View的剪切区域的位置边界，和ChangeBounds类似。不过ChangeBounds针对的是view而ChangeClipBounds针对的是view的剪切区域setClipBound(Rect rect) 中的rect(关注的是setClipBounds(Rect rect)rect的变化) |
| ChangeImageTransform | 检测ImageView的ScaleType，并创建相应动画(关注的是ImageView的scaleType) |
| Fade                 | 根据View的visibility状态的的不同创建淡入淡动画,调整的是透明度(关注的是View的visibility的状态) |
| Slide                | 根据View的visibility状态的的不同创建滑动动画(关注的是View的visibility的状态) |
| Explode              | 根据View的visibility状态的的不同创建分解动画(关注的是View的visibility的状态) |
| AutoTransition       | 默认动画，ChangeBounds、Fade动画的集合                       |

​    

### 动画的创建方法

![img](https://user-gold-cdn.xitu.io/2020/2/16/1704d476dd35cc67?imageView2/0/w/1280/h/960/ignore-error/1)

#### xml文件

res文件夹下创建一个transition文件夹。并在里面创建xml动画文件。

```xml
<?xml version="1.0" encoding="utf-8"?>
<fade xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:interpolator/accelerate_decelerate"
    android:duration="200"
    android:transitionOrdering="sequential" />
```

此时如果要在代码中引用该文件时可以用下面的方法。

```java
window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.fade_transition)

```

#### 利用代码

```kotlin
window.exitTransition = 
    Slide().apply {
        duration = 200
        slideEdge = Gravity.END
    }
```

####  创建多转场

利用xml：

```xml
<transitionSet xmlns:android="http://schemas.android.com/apk/res/android"
    android:transitionOrdering="together">
    <explode
        android:duration="1000"
        android:interpolator="@android:interpolator/accelerate_decelerate" />
    <fade
        android:duration="1000"
        android:fadingMode="fade_in_out"
        android:interpolator="@android:interpolator/accelerate_decelerate" />
    <slide
        android:duration="500"
        android:interpolator="@android:interpolator/accelerate_decelerate"
        android:slideEdge="top" />
</transitionSet>
```

利用代码：

```kotlin
val transitionSet = TransitionSet()
                transitionSet.addTransition(Fade())
                transitionSet.addTransition(Slide())
                transitionSet.setOrdering(ORDERING_TOGETHER)
```

### 转场动画

#### Activity的转场



 **API 21之前Activity过渡动画使用**

API21之前Activity过渡动画通过两种方式来实现:style主题里面统一设置、或者使用代码overridePendingTransition函数单独设置。(当然了代码设置的优先级要高于style主题里面统一设置)

style文件主题里面统一定义，全局为所有Activity设置过渡动画效果。

```xml
<item name="android:windowAnimationStyle">@style/Animation.Activity.Customer</item>
```

```xml
    <style name="Animation.Activity.Customer" parent="@android:style/Animation.Activity">
        <!-- 进入一个新的Activity的时候，A->B B进入动画 -->
        <item name="android:activityOpenEnterAnimation">@anim/right_in</item>
        <!-- 进入一个新的Activity的时候，A->B A退出动画 -->
        <item name="android:activityOpenExitAnimation">@anim/left_out</item>
        <!-- 退出一个Activity的时候，B返回到A A进入动画 -->
        <item name="android:activityCloseEnterAnimation">@anim/left_in</item>
        <!-- 退出一个Activity的时候，B返回到A B退出动画 -->
        <item name="android:activityCloseExitAnimation">@anim/right_out</item>
    </style>
```

**API 21 之后Activity过渡动画使用**

Activity过渡动画效果图

![img](https://upload-images.jianshu.io/upload_images/9182331-a7fb149205977fff?imageMogr2/auto-orient/strip|imageView2/2/w/340)



 But 现在你又可以获取一个新的技能了。在API 21之后google又推出了一种比之前效果更加赞的过渡动画。 通过ActivityOptions + Transition来实现Activity过渡动画。

在讲使用ActivityOptions + Transition来实现Activity过渡动画之前先来了看下ActivityOptions里面几个函数代表啥意思。

```cpp
/**
 * 和overridePendingTransition类似,设置跳转时候的进入动画和退出动画
 */
public static ActivityOptions makeCustomAnimation(Context context, int enterResId, int exitResId);

/**
 * 通过把要进入的Activity通过放大的效果过渡进去
 * 举一个简单的例子来理解source=view,startX=view.getWidth(),startY=view.getHeight(),startWidth=0,startHeight=0
 * 表明新的Activity从view的中心从无到有慢慢放大的过程
 */
public static ActivityOptions makeScaleUpAnimation(View source, int startX, int startY, int width, int height);

/**
 * 通过放大一个图片过渡到新的Activity
 */
public static ActivityOptions makeThumbnailScaleUpAnimation(View source, Bitmap thumbnail, int startX, int startY);

/**
 * 场景动画，体现在两个Activity中的某些view协同去完成过渡动画效果，等下在例子中能更好的看到效果
 */
public static ActivityOptions makeSceneTransitionAnimation(Activity activity, View sharedElement, String sharedElementName);

/**
 * 场景动画，同上是对多个View同时起作用
 */
public static ActivityOptions makeSceneTransitionAnimation(Activity activity, android.util.Pair<View, String>... sharedElements);
```

 上面给出了四类方式的使用，个人觉得makeCustomAnimation、makeScaleUpAnimation、makeThumbnailScaleUpAnimation这三种产生的效果还是走的API 21之前的效果，而且这三种效果好像和Transition动画没啥太多的联系。我们用的最多的还是makeSceneTransitionAnimation()函数，makeSceneTransitionAnimation效果才是和Transition动画效果密切相关的。所以我们重点来看makeSceneTransitionAnimation的使用。

​    Transitionz过渡动画的使用也是有前提的：

- API 21以上。当然你也可以不使用ActivityOptions，而是使用兼容类ActivityOptionsCompat来替换ActivityOptions。(兼容类给到我们的作用是保证程序在低版本运行不会挂掉，但是不能保证低版本也能起到响应的效果的)
- 必须允许Activity可以使用Transition，要么在style里面设置(<item name="android:windowContentTransitions">true</item>)，要么直接通过代码设置(getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);)。

​    对于Transition Activity过渡动画的使用，我们简单的分为三个步骤：告诉系统以Transition的方式启动Activity、定义过渡动画、设置过渡动画。

==以Transition的方式启动Activity==

  启动Activity的方式和之前不一样了，要告诉Activity以Transition的方式启动。先要得到ActivityOptions，而且调用的startActivity()还和之前不一样了，是调用两个参数的startActivity(),第二个参数是ActivityOptions.toBundler。(当然为了兼容之前的版本你也可以使用兼容类ActivityOptionsCompat, ActivityCompat)。如下述代码所示。

API 21 之后的代码

```java
ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(mActivity);
startActivity(new Intent(mContext, MakeSceneTransitionctivity.class), compat.toBundle());
```

使用ActivityOptionsCompat兼容API 21之前的代码

```java
ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity);
ActivityCompat.startActivity(mContext, new Intent(mContext, MakeSceneTransitionActivity.class), compat.toBundle());
```







告诉Activity以Transition的方式启动了，也定义好了过渡动画了。接下来就是去设置过渡动画了。Transition过渡动画的设置可以在style文件中统一设置也可以在代码中设置(代码中设置的优先级比style主题文件优先级高)。

| 代码指定                           | style主题指定                   | 解释                                                     |
| ---------------------------------- | ------------------------------- | -------------------------------------------------------- |
| getWindow().setEnterTransition()   | android:windowEnterTransition   | A启动B，B中的View进入场景的transition(代码所在位置B)     |
| getWindow().setExitTransition()    | android:windowExitTransition    | A启动B，A中的View退出场景的transition(代码所在位置A)     |
| getWindow().setReturnTransition()  | android:windowReturnTransition  | B返回A，B中的View退出场景的transition(代码所在位置B)     |
| getWindow().setReenterTransition() | android:windowReenterTransition | B返回A，A中的View重新进入场景的transition(代码所在位置A) |

> ​    Activity过渡动画使用的时候有一个设置可以提高展示效果，可以通过在主题中设置windowAllowEnterTransitionOverlap、windowAllowReturnTransitionOverlap让动画过渡的更加自然。其中windowAllowEnterTransitionOverlap表示进入动画是否可以覆盖别的动画、windowAllowReturnTransitionOverlap表示返回动画是否可以覆盖别的动画。















#### 动画目标

前面做转场动画时，默认情况过渡动画会对所有的子View进行遍历加载动画，如果时添加目标则不会进行遍历所有子View，或者可以排除特定的View。

对于目标，一般有三种操作:

    添加：默认会进行遍历所有的视图加载动画, 但是如果使用了添加就不会遍历所有, 只会让指定的视图进行动画
    排除：如果使用排除方法, 依旧会进行遍历视图对象, 不过会排除你指定的视图
    删除：删除目标是在动画已经遍历视图完成以后还想对目标集合进行变更, 就可以删除指定的视图
**添加/排除/删除目标支持以下参数类型**：

1. 视图对象（View）
2. 过渡名（TransitionName）
3. 字节码（Class）
4. ID

指定目标View，让目标View参与动画

```java
Transition addTarget (View target)

Transition addTarget (String targetName)

Transition addTarget (Class targetType)

Transition addTarget (int targetId)
```

相应的对于删除或者排除，则分别是：`removeTarget()`与`excludeTraget()`

使用示例：

```java
private void setupWindowAnimations() {
        // inflate from xml
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        fade.excludeTarget(android.R.id.navigationBarBackground,true);
        fade.excludeTarget(android.R.id.statusBarBackground,true);
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        slide.setSlideEdge(Gravity.LEFT);
        slide.setDuration(2000);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);	// 	排除导航栏
        slide.excludeTarget(android.R.id.statusBarBackground,true);	//排除状态栏
        slide.removeTarget(mTextView);
        getWindow().setEnterTransition(slide);
        getWindow().setReturnTransition(slide);
//        getWindow().setReenterTransition(fade);
//        getWindow().setExitTransition(slide);
    }
```













#### 进场退场的过渡动画

1. `explore` : 将View移入场景中心或从中移出。
2. `slide` : 将视图从场景的其中一个边缘移入或移出。可以利用`slideEdge=Gravity.Start`方式设置移入移出边缘。
3. `fade` : 通过更改视图的不透明度，在场景中添加视图或从中移除视图。

| Explode                                                      |                            Slide                             |                             Fade                             |
| :----------------------------------------------------------- | :----------------------------------------------------------: | :----------------------------------------------------------: |
| 从中心移入或移出                                             |                       从边缘移入或移出                       |                      调整透明度产生渐变                      |
| ![img](https://upload-images.jianshu.io/upload_images/1689895-4c839936d9139327.gif?imageMogr2/auto-orient/strip\|imageView2/2/w/320) | ![img](https://upload-images.jianshu.io/upload_images/1689895-0bc9c94a12b1e000.gif?imageMogr2/auto-orient/strip\|imageView2/2/w/320) | ![img](https://upload-images.jianshu.io/upload_images/1689895-ff586e54872bcd4e.gif?imageMogr2/auto-orient/strip\|imageView2/2/w/320) |

这三个类都继承于 Transition ，所有有一些属性都是共同的。
 常用属性如下：

```cpp
// 设置动画的时间。类型：long
transition.setDuration();
// 设置修饰动画，定义动画的变化率，具体设置往下翻就看到了
transition.setInterpolator();
// 设置动画开始时间，延迟n毫秒播放。类型：long
transition.setStartDelay();
// 设置动画的运行路径
transition.setPathMotion();
// 改变动画 出现/消失 的模式。Visibility.MODE_IN:进入；Visibility.MODE_OUT：退出。
transition.setMode();

// 设置动画的监听事件
transition.addListener()
```







系统支持将任何扩展 Visibility 类的过渡作为进入或退出过渡。

####  转场设置

需要在activity的`onCreate`中设置转场动画。

```kotlin
window.let {
            // 设置分享元素的转场(后面会讲到)
            it.sharedElementEnterTransition
            it.sharedElementExitTransition
            it.sharedElementReenterTransition
            it.sharedElementReturnTransition
            
            // 设置是否需动画覆盖，转场动画中动画之间会有覆盖的情况
            // 可以设置false来让动画有序的进入退出
            it.allowEnterTransitionOverlap = false
            it.allowReturnTransitionOverlap = false
            
            // 设置activity之间的转场动画
//            it.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.fade_transition)
            it.exitTransition = Slide().apply {
                duration = 200
                slideEdge = Gravity.END
            }
            it.enterTransition = Slide().apply {
                duration = 200
                slideEdge = Gravity.START
            }
            it.reenterTransition = Slide().apply {
                duration = 200
                slideEdge = Gravity.END
            }
            it.returnTransition = Slide().apply {
                duration = 200
                slideEdge = Gravity.START
            }
        }

```

动画设置完了，下一步我们是需要进行跳转的工作。 activity之间跳转需要用到`intent`， 如果需要它们之间传递数据是要用到`Bundle`。 没有错，我们要设置的转场动画也要用到`Bundle`。我们看一下下面的代码。

```kotlin
btn2.setOnClickListener {
    val intent = Intent()
    intent.setClass(this, ThirdActivity::class.java)
    // 利用ActivityOptions生成TransitionActivityOptions
    val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
        this@MainActivity
    )
    // 利用上面生成的TransitionActivityOptions生成Bundle
    startActivity(intent, transitionActivityOptions.toBundle())
}

```

### 元素共享

Shared  elements转换确定两个Activity之间共享的视图如何在这两个Activity之间转换。例如，如果两个Activity在不同的位置和大小中具有相同的图像，则通过Shared elements转换会在这两个Activity之间平滑地转换和缩放图像。

<img src="http://5b0988e595225.cdn.sohucs.com/images/20190619/456e082f9c4a41d1b68098aa22aa1ffc.jpeg" alt="img" style="zoom:200%;" />

changeBounds 改变目标布局中view的边界

changeClipBounds 裁剪目标布局中view的边界

changeTransform 实现旋转或者缩放动画

changeImageTransform 实现目标布局中ImageView的旋转或者缩放动画





元素共享转场是Meterial设计中个人比较喜欢的设计之一。 activity之间的转场总有顿挫感，但是利用元素共享转场可以让前一个画面中的元素可以在下一个画面上流畅的显示。

你可能有发现前面讲的Activity过渡动画的实例中，ActivityOptions类里面makeSceneTransitionAnimation()函数后面的参数我们都没有传递进去，其实后面的参数是在使用共享元素的时候才会使用到的，接下来的实例这些个参数就会排上用场了。

  当你想要从一个Activity A转换到Activity B，而且他们共享一个元素（比如是一个view），在这种场景下，最好的用户体验可能就是将共享的元素直接变换到最终的地方和大小，这会使用户专注于应用而且有一种连贯性的表达。

​    共享元素的连接点是所有共享元素View的transition name。它可以在layout文件里面设置(android:transitionName)、也可以代码设置(View.setTransitionName(ImageConstants.IMAGE_SOURCE[mCurrentPosition]);)。通过transtion name来判断哪两个元素是共享关系。

​    有了前面Activity过渡动的理解，共享元素动画在理解上就简单的多了。同Activity过渡动画一样，共享元素的动画也可以通过代码或者主题文件来设置(Fragment里面共享元素动画的设置可以类比Activity里面共享元素动画的设置)，如下所示。

| 代码指定                                        | style主题指定                                | 解释                                                         |
| ----------------------------------------------- | -------------------------------------------- | ------------------------------------------------------------ |
| getWindow().setSharedElementEnterTransition()   | android:windowSharedElementEnterTransition   | A启动B，B中的View共享元素的transition(代码所在位置B)         |
| getWindow().setSharedElementExitTransition()    | android:windowSharedElementExitTransition    | A启动B，A中的View共享元素transition(代码所在位置A)           |
| getWindow().setSharedElementReturnTransition()  | android:windowSharedElementReturnTransition  | B返回A，B中的View共享元素的transition(代码所在位置B)         |
| getWindow().setSharedElementReenterTransition() | android:windowSharedElementReenterTransition | B返回A，A中的View重新进入共享元素的transition(代码所在位置A) |





#### 设置

其实上面讲到过设置方法，如下。

```kotlin
window.let {
    // 设置分享元素的转场
    it.sharedElementEnterTransition
    it.sharedElementExitTransition
    it.sharedElementReenterTransition
    it.sharedElementReturnTransition
}
```

2. 设置transitionName

要想让元素共享，需要让前一个画面的元素的id和后一个元素的id相同。 还有需要设置`transitionName`,设置的地方是layout的xml上。 当然，`transitionName`也需要前后一致。 如下。

```xml
<ImageView
        android:id="@+id/img"
        android:layout_width="240dp"
        android:layout_height="160dp"
        android:scaleType="fitCenter"
        android:src="@mipmap/android_logo"
        android:transitionName="img" // 就是这个
        app:layout_constraintBottom_toTopOf="@id/txt"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />
```

#### 动画设置

们需要为转场元素设置动画。在activity的`onCreate`的`window`上进行设置。

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        // 为转场元素设置动画
        window.sharedElementEnterTransition = ChangeBounds()
        window.sharedElementExitTransition = ChangeBounds()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
 }
```

需要注意的是前后两个画面都需要为共享元素设置转场动画。当然动画可以是不一样的，这个部分根据自己需要进行设置就好了。

####  进行转场

```kotlin
val intent = Intent()
val pair1 = Pair(imgView as View, imgView.transitionName)
val pair2 = Pair(textView as View, textView.transitionName)
intent.setClass(this, SecondActivity::class.java)
val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
    this@MainActivity,
    pair1, pair2
)
startActivity(intent, transitionActivityOptions.toBundle())
```

跟上面一样也是转换成`Bundle`进行转场的设置。 但是因为需要共享元素，`ActivityOptions.makeSceneTransitionAnimation`中加入共享元素信息。 这里需要用到的是`Pair`的数据结构，`Pair`的左边是共享的view，右边是刚刚设置的`transitionName`。 如果共享元素只有一个的情况可以直接使用如下方法。

```kotlin
val intent = Intent()
intent.setClass(this, SecondActivity::class.java)
val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
    this@MainActivity,
    imgView as View, imgView.transitioName
)
startActivity(intent, transitionActivityOptions.toBundle())
```

![img](https://user-gold-cdn.xitu.io/2020/2/17/170518d3a3684dd8?imageslim)

放置两张例子  后面有源码

简单共享元素效果图

![img](https://upload-images.jianshu.io/upload_images/9182331-31eda943a7cdf91e?imageMogr2/auto-orient/strip|imageView2/2/w/340)

#### 更新共享元素动画

效果图

![img](https://upload-images.jianshu.io/upload_images/9182331-32ef891431bfb4b1?imageMogr2/auto-orient/strip|imageView2/2/w/340)



有这种情况，比如我们第一个界面是一个列表(RecyclerView)每个item都是一个图片，点击进入另一个页面详情页面，详情页面呢有是ViewPager的形式。可以左右滑动。咱们有的时候就想，就算详情界面滑动到了其他照片，在返回到第一个页面的时候也想要有共享元素动画的效果。这个时候就得更新下共享元素的对应关系了。

​    怎么更新呢，关键是看==SharedElementCallback==类的==onMapSharedElements()==函数，这个函数是用来装载共享元素的。比如有这么个情况，还是上面的例子A界面跳转到B界面。那么A界面在B返回的时候要更新下、B界面在返回之前要更新下。所以给A界面设置==setExitSharedElementCallback(SharedElementCallback)==;、给B界面设置==setEnterSharedElementCallback(SharedElementCallback)==。其他更多的细节可以参考下实例代码中的实现。

> setExitSharedElementCallback(SharedElementCallback)的SharedElementCallback里面的
>
> onMapSharedElements()函数在Activity exit和reenter时都会触发
>  setEnterSharedElementCallback(SharedElementCallback)的SharedElementCallback里面的onMapSharedElements()函数在Activity enter和return时都会触发。



















### Scene转换

除了Activity之间的转换还有一个布局内的scene转换。

场景过渡动画是指以动画的形式实现View两个场景的切换(从一个场景切换到另一个场景)。而且在切换过程中通过Transition来设置不同的过渡动画效果。

场景过渡动画中有两个特别关键概念：（场景），Transition（过渡）。

  

- ==Scene==：Scene代表一个场景。Scene保存了一个视图层级结构，包括它所有的views以及views的状态，通常由getSceneForLayout (ViewGroup sceneRoot,int layoutId,Context context)获取Scene实例。Transition框架可以实现在starting scene和ending scene之间执行动画。而且大多数情况下，我们不需要创建starting scene，因为starting scene通常由当前UI状态决定，我们只需要创建ending scene。
- ==Transition==：Transiton则是用来设置过渡动画效果用的。而且系统给提供了一些非常使用的Transtion动画效果，如下表所示:

| 系统Transition       | 解释                                                         |
| -------------------- | ------------------------------------------------------------ |
| ChangeBounds         | 检测View的位置边界创建移动和缩放动画(关注布局边界的变化)     |
| ChangeTransform      | 检测View的scale和rotation创建缩放和旋转动画(关注scale和ratation的变化) |
| ChangeClipBounds     | 检测View的剪切区域的位置边界，和ChangeBounds类似。不过ChangeBounds针对的是view而ChangeClipBounds针对的是view的剪切区域setClipBound(Rect rect) 中的rect(关注的是setClipBounds(Rect rect)rect的变化) |
| ChangeImageTransform | 检测ImageView的ScaleType，并创建相应动画(关注的是ImageView的scaleType) |
| Fade                 | 根据View的visibility状态的的不同创建淡入淡动画,调整的是透明度(关注的是View的visibility的状态) |
| Slide                | 根据View的visibility状态的的不同创建滑动动画(关注的是View的visibility的状态) |
| Explode              | 根据View的visibility状态的的不同创建分解动画(关注的是View的visibility的状态) |
| AutoTransition       | 默认动画，ChangeBounds、Fade动画的集合                       |











​    要想实现一个场景过渡动画，==至少需要一个transition实例和一个ending scene实例==。并通过TransitionManager执行过渡动画。TransitionManager执行动画有两种方式：

**TransitionManager.go()**

**beginDelayedTransition()**。

下面简单来介绍下这两种开启场景动画的方式。

#### TransitionManager.go()

用说TransitionManager.go()实现场景动画之前，先上效果图

![img](https://upload-images.jianshu.io/upload_images/9182331-68300976457429e9?imageMogr2/auto-orient/strip|imageView2/2/w/340)



 TransitionManager.go()需要两个参数:第一个参数代表要过渡到的场景(end scene)、第二个参数过渡动画(transition 实例)。

TransitionManager.go()启动动画的时候，场景一般通过布局文件给出。场景实例的获取则通过Scene.getSceneForLayout()来获取，需要三个参数：

第一个参数代表场景所在的ViewGroup、

第二个参数代表场景布局文件

第三个参数布局文件转换View所需要的Content。

> TransitionManager.go()的场景是通过布局文件指定。

#### beginDelayedTransition

用beginDelayedTransition()实现场景动画的效果图

![img](https://upload-images.jianshu.io/upload_images/9182331-f3c1f3f66bd4ac0c?imageMogr2/auto-orient/strip|imageView2/2/w/340)

 通过TransitionManager.beginDelayedTransition()也可以开启场景动画。在执行TransitionManager.beginDelayedTransition()之后，系统会保存一个当前视图树状态的场景，之后当我们改变了View的属性之后(比如重新设置了View位置、缩放、clipe等等)。在下一次绘制时，系统会自动对比之前保存的视图树，然后执行相应动画。

####  创建Scene

为两个布局创建Scene。

```kotlin
val scene1 = Scene.getSceneForLayout(sceneRoot, R.layout.scene1, this)
val scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.scene2, this)

```

除了上面的方法，也可以用下面方法进行创建。

```kotlin
scene1 = Scene.getSceneForLayout(sceneRoot,R.layout.scene_layout1,this)
scene2 = Scene.getSceneForLayout(sceneRoot,R.layout.scene_layout2,this)

```

注意的是需要创建前后==两个Scene的控件的ID==相同。

#### 切换Scene

创建Transition动画。

```kotlin
val transition =  TransitionInflater.from(this).inflateTransition(R.transition.slide_transition)
```

利用TransitionManager进行Scene的切换。

```kotlin
TransitionManager.go(scene2, transition)

```

#### 完整的代码

```kotlin
// 创建Scene
val scene1 = Scene.getSceneForLayout(sceneRoot, R.layout.scene1, this)
val scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.scene2, this)
var count = 1

btn.setOnClickListener {
    count = if (count == 1) {
    // 创建transition动画
    val transition =
TransitionInflater.from(this).inflateTransition(R.transition.slide_transition)
    // Scene切换
    TransitionManager.go(scene2, transition)
        2
    } else {
    val transition =
 TransitionInflater.from(this).inflateTransition(R.transition.slide_transition)
    TransitionManager.go(scene1, transition)
        1
    }
}

```

![img](https://user-gold-cdn.xitu.io/2020/2/19/1705d6e68dc3a3c5?imageslim)

###  限制

```
Android` 版本在 `4.0(API Level 14)` 到 `4.4.2(API Level 19)` 使用 `Android Support Library’s
```

应用于 `SurfaceView` 的动画可能无法正确显示。 `SurfaceView` 实例是从非界面线程更新的，因此这些更新与其他视图的动画可能不同步。

当应用于 `TextureView` 时，某些特定过渡类型可能无法产生所需的动画效果。

扩展 `AdapterView` 的类（例如 `ListView`）会以与过渡框架不兼容的方式管理它们的子视图。如果您尝试为基于 AdapterView 的视图添加动画效果，则设备显示屏可能会挂起。

如果您尝试使用动画调整 `TextView` 的大小，则文本会在该对象完全调整过大小之前弹出到新位置。为了避免出现此问题，请勿为调整包含文本的视图的大小添加动画效果。

#### 资源

https://github.com/ZXM250250/Animation

Transitiondemo 模块

## ==ActivityOptions==

### 序言

对于Android 5.0 之前，Android的过渡动画一般情况下使用：

```java
overridePendingTransition(enterAnim, exitAnim);
```

android 5.0及以上，google提供了新的转场动画方式，及这里要介绍使用的ActivityOptions，并且提供了兼容包ActivityOptionsCompat。根据当前android版本的发展情况，本文就主要讲ActivityOptions，其兼容包使用方法一样。

ActivityOptions是一个静态类，提供了如下方法：

```java
makeCustomAnimation(Context context, int enterResId, int exitResId)
makeScaleUpAnimation(View source, int startX, int startY, int width, int height)
makeThumbnailScaleUpAnimation(View source, Bitmap thumbnail, int startX, int startY)
makeSceneTransitionAnimation(Activity activity)
makeSceneTransitionAnimation(Activity activity, View sharedElement, String sharedElementName)
makeSceneTransitionAnimation(Activity activity, Pair<View, String>... sharedElements)
。。。。。。
```

`ActivityOptions`的主要方法如上，这也是最主要的几种过渡方式，接下来将一一介绍各个方法的使用方式。

### makeCustomAnimation

如果定义转场效果等，对于这方面如果不清楚的话，可以查看[Android动画——Activity转场动画|过渡动画一点薄见(一)（（Transition Animation 系列））](https://blog.csdn.net/weixin_43499030/article/details/90413758)的前半部分

```java
ActivityOptions compat = ActivityOptions.makeCustomAnimation(this, R.anim.anim_activity_in, R.anim.anim_activity_out);
                startActivity(new Intent(MainActivity.this, SecondActivity.class), compat.toBundle());
```

makeCustomAnimation方法需要3个参数：

    第一个参数：Context类型，也就是Activity。
    第二个参数：int类型，新Activity显示动画。
    第三个参数：int类型，当前Activity的退出动画。

这个方法与android 5.0之前的==overridePendingTransition==类似，故使用较少。
备注：

```java
启动activity的方式是使用ActivityCompat.startActivity
如果调用finish()退出Activity，则需要调用finishAfterTransition()进行退出动画
```
### makeScaleUpAnimation

这个方法的**转场效果**时：

> **在新Activity会一某个点为中心，从某个大小开始逐渐放大到最大**。
>
> 使用方法示例：
>
> ```java
> ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage, mImage.getWidth(), mImage.getHeight(),0,0);
> startActivity(new Intent(MainActivity.this, SecondActivity.class), compat.toBundle());
> ```

这个方法有五个参数：

    第一个参数：View类型，从那个View的坐标开始放大
    第二个参数：int类型，指定相对于View的X坐标为放大中心的X坐标
    第三个参数：int类型，指定相对于View的Y坐标为放大中心的Y坐标
    第四个坐标：int类型，指定放大前新Activity是多宽
    第五个坐标：int类型，指定放大前新Activity是多高
### makeThumbnailScaleUpAnimation

该方法与上面一个方法类似，转场效果是：

> 放大一个图片，最后过渡到一个新的Activity

**使用方法**：

```java
ActivityOptions compat = ActivityOptions.makeThumbnailScaleUpAnimation(mTextView,BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),0,0);
startActivity(new Intent(MainActivity.this, SecondActivity.class), compat.toBundle());
```

该方法需有4个参数：

- 第一个参数：View类型，要放大的图片从哪个View的左上角的坐标作为中心点放大。
- 第二个参数：Bitmap类型，指定要放大的图片。
- 第三个参数：放大前图片的初始宽度
- 第四个参数：放大前图片的初始高度

### makeSceneTransitionAnimation

```java
Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle())
```

这个方法没什么可说的，就是简单的Activity转场

### ==makeSceneTransitionAnimation==

第二个这个方法是需要关注的重点，主要用于共享动画，关于共享动画的内容，可以查看[Android动画——Activity转场动画|过渡动画一点薄见(一)（（Transition Animation 系列））](https://blog.csdn.net/weixin_43499030/article/details/90413758)的共享动画部分

转场效果：

两个Activity的两个View协同完成转场，也就是原==Activity的View过度到新Activity的View==，原新两个Activity的View的`transitionName`相同。

**使用方法**：

```java
Intent intent1 = new Intent(MainActivity.this, SharedElementActivity.class);
startActivity(intent1, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,mImage,"image").toBundle())
```

activity_main.xml和activity_shared_element.xml的布局文件中都有一个ImageView，他们的android:transitionName属性值是一样的。

这个方法有三个参数：

    第一个参数：Context类型，指定Activity
    第二个参数：View类型，指定从哪里过渡
    第三个参数：String类型，过渡View的标志，即android:transitionName的属性值
### ==makeSceneTransitionAnimation==

转场效果：

> 前面一个方法是单个View协作转场，这个方法可以实现多个view协作转场

使用方法：

```java
Intent intent = new Intent(MainActivity.this, SharedElementActivity.class);
Pair<View, String> p1 = Pair.create((View)textView1, "profile");
Pair<View, String> p2 = Pair.create((View)textView, "test");
startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,p1,p2).toBundle());
```

首先通过`Pair.Create`静态方法创建两个Pair对象，这里有两个泛型，分别指定为View和String，Create方法接收，第一个参数是参与动画的Veiw，第二个是该View的transitionName，与前面的单一协作转场类似。

## ==自定义Transition==

### 原理

在开始自定义之前, 我们首先来简单的了解一下Transition转场动画的原理, 大家在看到你所不知道的Activity转场动画——ActivityOptions这篇文章时, 对Android提供的这种新的转场动画都震撼到了, 但是肯定有很多人对它的原理不是很请求, 尤其是Scene场景动画, 一个ImageView怎么就变着变着跳转到其他的Activity了呢? 其实它的原理很简单,Transition动画其实就是拿着第一页某个view的信息去第二页的某个view上做的动画, 这样我们在视觉上就会产生一个渐变的错觉~

### 玩玩Transition

在稍微了解了一下原理之后, 我们就来玩玩`Transition`了, 如何自定义一个`Transition`呢? 跟自定义view我们需要继承View或者ViewGroup一样, 这里我们需要继承`Transition`类.

```java
public class MyTransition extends Transition {}
```

有两个抽象方法必须要要重写,

public class MyTransition extends Transition {

```java
@Override
public void captureStartValues(TransitionValues transitionValues) {

}

@Override
public void captureEndValues(TransitionValues transitionValues) {

}
```
除了这两个必须要重写的方法, 我们还要重写一个`createAnimator`方法来自定义动画, 于是, 我们要自定义一个`Transition`, 一个类的结构肯定是肯定是这样的.

```java
public class MyTransition extends Transition {

    @Override
    public void captureStartValues(TransitionValues transitionValues) {

    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {

    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, final TransitionValues endValues) {
    }
}
```

ok, 下面我们来详细说一下这三个方法都是用来干嘛的.
首先==captureStartValues==, 从字面上来看是用来收集开始信息的, 什么开始信息? 当然是动画的开始信息了. 那同样的==captureEndValues==是用来收集动画结束的信息的. 收集完了信息,就要通过createAnimator来创建个Animator供系统调用了.

再来看看TransitionValues这个陌生的类, 这个类其实很简单, 只有==两个成员变量view和values==, view指的是我们要从哪个view上收集信息, values是用来存放我们收集到的信息的. 比如: 在captureStartValues里, transitionValues.view指的就是我们在开始动画的界面上的那个view, 在captureEndValues指的就是在目标界面上的那个view.

好了, 上面几个方法的作用介绍完毕后, 我们马上就来完成一个进入消息内容的动画效果, 还是老规矩, 在开始代码之前, 我们先来看看效果.

![img](https://img-blog.csdn.net/20161121000942985)

仔细观察效果, 我们可以找到两处动画.

> 1. 单行内容从它在列表中的位置移动到界面的最上面.
> 2. 消息的内容由单行逐渐展开.
> 3. 这两个动画是顺序执行的

通过上面的分析, 我们大致可以得出, 下面, 我们需要收集的信息有**view在界面的位置**和**view的高度信息**, 所以我们先来定义一下需要收集的信息

```java
public class MyTransition extends Transition {

    private static final String TOP = "top";
    private static final String HEIGHT = "height";
    // ...
}
```

然后我们开始收集动画开始需要的信息

```java
public class MyTransition extends Transition {

    private static final String TOP = "top";
    private static final String HEIGHT = "height";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        Rect rect = new Rect();
        view.getHitRect(rect);

        transitionValues.values.put(TOP, rect.top);
        transitionValues.values.put(HEIGHT, view.getHeight());

        Log.d("qibin", "start:" + rect.top + ";" + view.getHeight());
    }
}
```

首先, 我们通过==transitionValues.view==拿到我们要收集信息的==目标view==, 然后我们可以通过getHitRect可以拿到它在ListView中的上下左右信息, 最后我们通过==transitionValues.values.put(TOP, rect.top)==来保存一下他距离父布局上面的距离, 当然我们还需要通过==transitionValues.values.put(HEIGHT, view.getHeight())==来保存动画初始的高度.

收集完动画开始的信息, 我们再来收集动画结束的信息, 依葫芦画瓢, 很快就能写出下面的代码.

```java
public class MyTransition extends Transition {

    private static final String TOP = "top";
    private static final String HEIGHT = "height";

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(TOP, 0);
        transitionValues.values.put(HEIGHT, transitionValues.view.getHeight());

        Log.d("qibin", "end:" + 0 + ";" + transitionValues.view.getHeight());
    }
}
```

这里的代码和上面并无差别, 动画结束后, view距离上面的距离应该是0, 不过这里需要注意的是==captureStartValues==方法里的transitionValues.view是我们页面跳转开始那个界面上的view, 而==captureEndValues==方法里的transitionValues.view是我们跳转目标上的view, 所以这两个方法里获取到的view的高度肯定是不一样的.

好了, 在完成信息收集之后, 我们就来写动画效果了,

```java
public class MyTransition extends Transition {
  @Override
  public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, final TransitionValues endValues) {
      if (startValues == null || endValues == null) { return null;}

      final View endView = endValues.view;

      final int startTop = (int) startValues.values.get(TOP);
      final int startHeight = (int) startValues.values.get(HEIGHT);
      final int endTop = (int) endValues.values.get(TOP);
      final int endHeight = (int) endValues.values.get(HEIGHT);

      ViewCompat.setTranslationY(endView, startTop);
      endView.getLayoutParams().height = startHeight;
      endView.requestLayout();

      ValueAnimator positionAnimator = ValueAnimator.ofInt(startTop, endTop);
      
      if (mPositionDuration > 0) {
          
          positionAnimator.setDuration(mPositionDuration);
          
      }
      
      positionAnimator.setInterpolator(mPositionInterpolator);

      positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
              int current = (int) valueAnimator.getAnimatedValue();
              ViewCompat.setTranslationY(endView, current);
          }
      });

      ValueAnimator sizeAnimator = ValueAnimator.ofInt(startHeight, endHeight);
      if (mSizeDuration > 0) { 
          sizeAnimator.setDuration(mSizeDuration);
      }
      sizeAnimator.setInterpolator(mSizeInterpolator);

      sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
              int current = (int) valueAnimator.getAnimatedValue();
              endView.getLayoutParams().height = current;
              endView.requestLayout();
          }
      });

      AnimatorSet set = new AnimatorSet();
      set.play(sizeAnimator).after(positionAnimator);

      return set;
  }
}
```

在说原理的时候, 我们提到过, 这一系列的动画其实在我们跳转后的界面上完成的, 所以这里的动画我们也是在目标view上完成. 上面两个方法中收集到的信息, 我们需要在这里用到, 所以我们通过以下代码来获取收集到的信息.

```java
final int startTop = (int) startValues.values.get(TOP);
final int startHeight = (int) startValues.values.get(HEIGHT);
final int endTop = (int) endValues.values.get(TOP);
final int endHeight = (int) endValues.values.get(HEIGHT);
```

`startValues`和`endValues`都是`createAnimator`的参数. 
 接着几行莫名奇妙的代码

```java
ViewCompat.setTranslationY(endView, startTop);
endView.getLayoutParams().height = startHeight;
endView.requestLayout();
```

是因为我们的动画顺序是==先移动, 后展开,== 首先把view的高度设置为前一个界面上view的高度是为了防止在移动的过程中view的高度是他自身的高度的.
接着我们创建了两个动画, 这两个动画很好理解, 一个位移的,一个是展开的, 不过这里我们给了动画一个时长和插值器, 这两个信息是公开给调用者去设置的.
最后我们创建一个AnimatorSet, 在这个动画集合中, 我们先来完成sizeAnimator然后开始positionAnimator, 最后返回该动画集合. 自定义Transition完毕.

### 使用

上面我们完成了`Transition`的自定义, 这里我们就来用一下它, 首先我们要在应用的主题中指定可以使用场景过度动画.

```xml
<item name="android:windowContentTransitions">true</item>
```

看过[你所不知道的Activity转场动画——ActivityOptions](http://blog.csdn.net/qibin0506/article/details/48129139)这篇文章的朋友都应该清楚, 我们还需要给我们两个activity中的view一个`transitionName`, 这里就不贴代码了, 然后我们就来看看如何做跳转.

```java
public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new Adapter();
        mListView.setAdapter(new Adapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startActivity(view, mAdapter.getItem(position));
            }
        });
    }
       public void startActivity(View view, String content) {
           Intent intent = new Intent(this, MessageActivity.class);
           intent.putExtra("msg", content);

           ActivityOptionsCompat compat = ActivityOptionsCompat
                   .makeSceneTransitionAnimation(this, view, view.getTransitionName());
           ActivityCompat.startActivity(this, intent, compat.toBundle());
       }
}
```

跳转的代码大家都可以在[你所不知道的Activity转场动画——ActivityOptions](http://blog.csdn.net/qibin0506/article/details/48129139)这篇文章中找到, 这里就不解释了, 我们主要还是来看看在目标activity中怎么应用动画.

```java
public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        setTitle("Content");

        TextView msgTextView = (TextView) findViewById(R.id.msg);
        msgTextView.setText(getIntent().getStringExtra("msg"));

        executeTransition();
    }

    public void executeTransition() {
        MyTransition transition = new MyTransition();
        transition.setPositionDuration(300);
        transition.setSizeDuration(300);
        transition.setPositionInterpolator(new FastOutLinearInInterpolator());
        transition.setSizeInterpolator(new FastOutSlowInInterpolator());
        transition.addTarget("message");

        getWindow().setSharedElementEnterTransition(transition);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
```

来看`executeTransition`方法, 在这个方法中, 首页我们构建了一个我们自定义的`transition`, 然后各种配置, 解析来的一行代码,

```java
transition.addTarget("message");
```

这个`message`就是我们前面提到的`transitionName`, 最后我们通过

```java
getWindow().setSharedElementEnterTransition(transition);
```

来设置进入的动画. 
 ok, 现在我们来看看效果

![img](https://img-blog.csdn.net/20161121001017879)

### 闪烁问题

看到效果后, 细心的朋友可能发现, 在动画执行的过程中我们的NavigationBar会产生一个闪烁的效果, 这个效果不是我们想要的,出现这个问题的原因是共享元素动画是在整个窗口的view上执行的, 在这里找到了解决方案. 他的解决办法是: 首先将NavigationBar也作为动画的一部分, 然后在目标activity中延迟动画的执行. google给我们提供了两个方法来用, postponeEnterTransition()和startPostponedEnterTransition()方法来延迟动画的执行.

所以, 现在我们的跳转代码应该是这样的.

```java
public void startActivity(View view, String content) {
    View statusBar = findViewById(android.R.id.statusBarBackground);
    View navigationBar = findViewById(android.R.id.navigationBarBackground);

    List<Pair<View, String>> pairs = new ArrayList<>();
    pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
    pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
    pairs.add(Pair.create(view, view.getTransitionName()));

    Intent intent = new Intent(this, MessageActivity.class);
    intent.putExtra("msg", content);

    ActivityOptionsCompat compat = ActivityOptionsCompat
            .makeSceneTransitionAnimation(this, pairs.toArray(new Pair[pairs.size()]));
    ActivityCompat.startActivity(this, intent, compat.toBundle());
}
```

在目标activity中执行的动画的代码也应该是这样的.

```java
public void executeTransition() {
    postponeEnterTransition();

    final View decorView = getWindow().getDecorView();
    getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            decorView.getViewTreeObserver().removeOnPreDrawListener(this);
            supportStartPostponedEnterTransition();
            return true;
        }
    });

    MyTransition transition = new MyTransition();
    transition.setPositionDuration(300);
    transition.setSizeDuration(300);
    transition.setPositionInterpolator(new FastOutLinearInInterpolator());
    transition.setSizeInterpolator(new FastOutSlowInInterpolator());
    transition.addTarget("message");

    getWindow().setSharedElementEnterTransition(transition);
}
```

### 资源

https://github.com/ZXM250250/Animation

在app模块下边

# ==触摸反馈动画==















# ==揭露动画==



[揭露动画](https://blog.csdn.net/c10WTiybQ1Ye3/article/details/103471044?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522162306579816780366536717%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=162306579816780366536717&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-1-103471044.first_rank_v2_pc_rank_v29&utm_term=%E6%8F%AD%E9%9C%B2%E5%8A%A8%E7%94%BB&spm=1018.2226.3001.4187)



![img](https://upload-images.jianshu.io/upload_images/1931245-ffb02f5988578122.gif?imageMogr2/auto-orient/strip|imageView2/2/w/200)

AndroidPlatform 的 android.view 包下有个 ViewAnimationUtils 类，这是使用系统所提供揭露动画的唯一入口，其对外暴露的唯一接口如下：

| Public methods   |                                                              |
| ---------------- | ------------------------------------------------------------ |
| static Animator! | createCircularReveal(view: View!, centerX: Int, centerY: Int, startRadius: Float, endRadius:  Float)Returns an Animator which can animate a clipping circle. |

通过其静态的 createCircularReveal 方法来构造一个动画(Animator)对象，具体其实是个 RevealAnimator 类对象，进而可以实现一种炫酷(到底炫不炫酷就很主观了)的动画效果！

```java
createCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius
```

第一个参数是个 View，揭露动画的应用对象必须是一个 View，这点不难理解。

第二个参数是圆形揭露效果的圆心 X 轴坐标，同理第三个参数是 Y 轴坐标。

第三个参数是圆形揭露效果的开始半径，同理第三个参数是圆形揭露效果的终止半径，开始半径传 0，终止半径传 View  的宽度或高度就是个典型的从无到有的揭露(显示)过程，反之，开始半径传 View 的宽度或高度，终止半径传 0  就是个从有到无的反揭露(隐藏)过程。

拿到此方法返回的 Animator 对象我们就可以随时控制 View 进行揭露动画了。

## view









## app



## activity

首先我们会遇到两个问题：

1. 揭露动画用于 Activity 切换时，我们该把揭露动画应用于哪个 View(揭露动画的应用对象必须是一个 View)？
2. 何时开始执行揭露动画？

根据我们得 Demo，一一作答。

揭露动画用于 Activity 切换时，最合适的对象肯定是此 Activity 相关 Window 的根视图，真正的根视图，没错正是此 Activity 的 Window 的 DecorView。

至于揭露动画的开始时机，太早或太晚都不好。首先不能太早，如果当前 View 还未 Attach 到 Window 上就对其应用揭露动画会抛出异常，其次不能太晚，不然会严重影响动画的视觉效果。

经作者实践，这个最好的揭露动画开始时机在视图的可见性刚变为对用户可见时最佳！我们通过为 View 的 ViewTreeObserver 设置一个 OnGlobalLayoutListener 回调可完美监听到这个最佳时机~

我把相关实现代码全都放在了 Demo 的 BaseActivity 类里，用例 Activity 只要继承 BaseActivity  即可在打开时应用揭露动画效果。这里注意为了动画的连贯性我们需要把 Activity 揭露动画开始的圆心坐标从它的上个 Activity 里通过  Intent 传递过来，这点并不难实现。关键代码如下：



























# ==状态动画==







# ==事件分发总结==

**总结一**

1.如果销售链没有形成，零售商不能找到总代理直接要到事件的消费权。

2.销售链形成后，再次来了事件，会直接沿着销售链走，不会再次去询问了。

3.当销售链形成后，底层对上层有反响制约的权利。

4.上层拥有两次选择的机会，1.刚收到事件2.全部都没有处理事件。

**总结二**

1. 正常情况下，一个事件序列只能被一个view拦截并消耗

2. ​    某个view一旦决定拦截处理事件，那么这个事件序列都将由它的onTouchEvent处理，并且

   它的onInterceptTouchEvent不会再调用

3.  某个view一旦开始处理事件，如果它不消耗DOWN事件（onTouchEvent返回false），那么同一事件序列中其他

事件都不会再交给它处理，并且重新由它的父元素处理（父元素onTouchEvent被调用）

4. 事件的分发过程是由外向内的，即事件总是先传递给父元素，然后再由父元素分发给子view，通过requestDisalowInterceptTouchEvent方法可以再子View中干预父元素的事件分发过程，但DOWN事件除外

5. ViewGroup默认不拦截任何事件，即onInterceptTouchEvent默认返回fasle。view没有onInterceptTouchEvent方法调用一旦有点击事件传递给它，那么它的onToucheEvent就会被调用

6. Viw的onTouchEvent默认会消耗事件，除非它是不可点击的（clickable和longClickabke同时为false）。view的longClickable默认都为false，clickable要分情况，不如button的clickable默认为true，TextView的clickable默认为false。

7. View的enable属性不影响onTouchenEvent的默认返回值。哪怕一个view是disable状态的，只要它的clickable或者longClickabel有一个为true，那么它的onTouchEvent就会返回true。

8. onClick会响应的前提是当前View是可点击的，并且收到了DOWN和UP的事件，并且受长按事件的影响，当长按事件返回true时，onClick不会响应。

9. onLongClick在DOWN里判断是否进行响应，要想执行长按事件该View必须是longClickable的并且设置了OnLongClickListener。

   
