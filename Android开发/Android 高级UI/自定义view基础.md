# ==绘图基础==

## 角度和弧度的换算关系

圆一周对应的角度为360度(角度)，对应的弧度为2π弧度。

故得等价关系:360(角度) = 2π(弧度) ==> 180(角度) = π(弧度)

由等价关系可得如下换算公式:

rad 是弧度， deg 是角度

![img](https://upload-images.jianshu.io/upload_images/2625999-106126c0ca928b48.png?imageMogr2/auto-orient/strip|imageView2/2/w/202)

## 颜色

![img](https://upload-images.jianshu.io/upload_images/2625999-aa8012958bf7150c.png?imageMogr2/auto-orient/strip|imageView2/2/w/1080)

其中字母表示通道类型，数值表示该类型用多少位二进制来描述。如ARGB8888则表示有四个通道(ARGB),每个对应的通道均用8位来描述。

注意：我们常用的是ARGB8888和ARGB4444，而在所有的安卓设备屏幕上默认的模式都是RGB565,请留意这一点。

以ARGB8888为例介绍颜色定义<img src="https://upload-images.jianshu.io/upload_images/2625999-86d4d1223b0ca903.png?imageMogr2/auto-orient/strip|imageView2/2/w/1065" alt="img" style="zoom:200%;" />

其中 A R G B 的取值范围均为0255(即16进制的0x000xff)

A 从ox00到oxff表示从透明到不透明。

RGB 从0x00到0xff表示颜色从浅到深。

当==RGB==全取最小值(0或0x000000)时颜色为==黑色==，全取最大值(255或0xffffff)时颜色为==白色==

## 使用

1.java中定义颜色

```cpp
int color = Color.GRAY;     //灰色
```

由于Color类提供的颜色仅为有限的几个，通常还是用ARGB值进行表示。

```cpp
int color = Color.argb(127, 255, 0, 0);   //半透明红色

int color = 0xaaff0000;                   //带有透明度的红色
```

2.在xml文件中定义颜色

在/res/values/color.xml 文件中如下定义：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="red">#ff0000</color>
    <color name="green">#00ff00</color>
</resources>
```

详解： 在以上xml文件中定义了两个颜色，红色和绿色，是没有alpha（透明）通道的。

定义颜色以‘#’开头，后面跟十六进制的值，有如下几种定义方式

```bash
#f00            //低精度 - 不带透明通道红色
#af00           //低精度 - 带透明通道红色

#ff0000         //高精度 - 不带透明通道红色
#aaff0000       //高精度 - 带透明通道红色
```

3.在java文件中引用xml中定义的颜色：

```java
int color = getResources().getColor(R.color.mycolor);
```

4.在xml文件(layout或style)中引用或者创建颜色

```xml
<!--在style文件中引用-->
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="colorPrimary">@color/red</item>
</style>
android:background="@color/red"     //引用在/res/values/color.xml 中定义的颜色

android:background="#ff0000"        //创建并使用颜色
```





# ==动画==



## 帧动画

### 概念

> 帧动画非常容易理解，其实就是简单的由N张静态图片收集起来，然后我们通过控制依次显示 这些图片，因为人眼"视觉残留"的原因，会让我们造成动画的"错觉"，跟放电影的原理一样！
>
> 而Android中实现帧动画，一般我们会用到前面讲解到的一个Drawable：[AnimationDrawable](https://www.runoob.com/w3cnote/android-tutorial-drawable2.html) 先编写好Drawable，然后代码中调用start()以及stop()开始或停止播放动画~
>
> 当然我们也可以在Java代码中创建逐帧动画，创建AnimationDrawable对象，然后调用 addFrame(Drawable frame,int duration)向动画中添加帧，接着调用start()和stop()而已~

### 帧动画的实例

![img](https://www.runoob.com/wp-content/uploads/2015/11/81922813.jpg)

**代码实现**：

首先编写我们的动画文件，非常简单，先在res下创建一个anim目录，接着开始撸我们的 动画文件：**miao_gif.xml**： 这里的android:oneshot是设置动画是否只是播放一次，true只播放一次，false循环播放！

```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">
    <item
        android:drawable="@mipmap/img_miao1"
        android:duration="80" />
    <item
        android:drawable="@mipmap/img_miao2"
        android:duration="80" />
    <item
        android:drawable="@mipmap/img_miao3"
        android:duration="80" />
    <!--限于篇幅，省略其他item，自己补上-->
    ...
</animation-list>
```

动画文件有了，接着到我们的布局文件：**activity_main.xml**：

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_start"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="开始" />

    <Button
        android:id="@+id/btn_stop"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="停止" />

    <ImageView
        android:id="@+id/img_show"
        android:layout_width="120dp"
        android:layout_height="120dp"
        android:layout_gravity="center"
        android:background="@anim/miao_gif" />
    
</LinearLayout>
```

最后是我们的**MainActivity.java**，这里在这里控制动画的开始以及暂停：

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start;
    private Button btn_stop;
    private ImageView img_show;
    private AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        anim = (AnimationDrawable) img_show.getBackground();
    }

    private void bindViews() {
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        img_show = (ImageView) findViewById(R.id.img_show);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                anim.start();
                break;
            case R.id.btn_stop:
                anim.stop();
                break;
        }
    }
}
```

### 在指定地方播放帧动画

![img](https://www.runoob.com/wp-content/uploads/2015/11/90866559.jpg)

**代码实现**：

依旧是先上我们的动画文件:**anim_zhuan.xml**：

```xml
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="true">
    <item
        android:drawable="@mipmap/img_zhuan1"
        android:duration="80" />
    <item
        android:drawable="@mipmap/img_zhuan2"
        android:duration="80" />
    <item
        android:drawable="@mipmap/img_zhuan3"
        android:duration="80" />
     <!--限于篇幅，省略其他item，自己补上-->
    ...
</animation-list> 
```

接着我们来写一个自定义的ImageView：**FrameView.java**，这里通过反射获得当前播放的帧， 然后是否为最后一帧，是的话隐藏控件！

```java
/**
 * Created by Jay on 2015/11/15 0015.
 */
public class FrameView extends ImageView {

    private AnimationDrawable anim;

    public FrameView(Context context) {
        super(context);
    }

    public FrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAnim(AnimationDrawable anim){
        this.anim = anim;
    }

    public void setLocation(int top,int left){
        this.setFrame(left,top,left + 200,top + 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try{
            //反射调用AnimationDrawable里的mCurFrame值
            Field field = AnimationDrawable.class
                    .getDeclaredField("mCurFrame");
            field.setAccessible(true);
            int curFrame = field.getInt(anim);// 获取anim动画的当前帧
            if (curFrame == anim.getNumberOfFrames() - 1)// 如果已经到了最后一帧
            {
                //让该View隐藏
                setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){e.printStackTrace();}
        super.onDraw(canvas);
    }
}
```

最后是我们的**MainActivity.java**，创建一个FrameLayout，添加View，对触摸事件中按下的 事件做处理，显示控件以及开启动画~

```java
public class MainActivity extends AppCompatActivity {

    private FrameView fView;
    private AnimationDrawable anim = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout fly = new FrameLayout(this);
        setContentView(fly);
        fView = new FrameView(this);
        //将动画设置给自定义view的背景
        fView.setBackgroundResource(R.anim.anim_zhuan);
        //设置view不可见
        fView.setVisibility(View.INVISIBLE);
        //再从 背景里面拿到动画
        anim = (AnimationDrawable) fView.getBackground();
        fView.setAnim(anim);
        fly.addView(fView);
        fly.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //设置按下时才产生动画效果
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    anim.stop();
                    float x = event.getX();
                    float y = event.getY();
                    fView.setLocation((int) y - 40,(int)x-20);  //View显示的位置
                    fView.setVisibility(View.VISIBLE);
                    anim.start();    //开启动画
                }
                return false;
            }
        });
    }
}
```

### 资料

[AnimationDemo1.zip](http://static.runoob.com/download/AnimationDemo1.zip)

[AnimationDemo2.zip](http://static.runoob.com/download/AnimationDemo2.zip)

[Gif帧提取工具](http://static.runoob.com/download/HA_AVD_Animated_GIF_producer_V5.2_FULL_Fix_ata.rar)

## 补间动画

### 补间动画的分类和Interpolator

Andoird所支持的补间动画效果有如下这五种，或者说四种吧，第五种是前面几种的组合而已~

> - **AlphaAnimation：**==透明度==渐变效果，创建时许指定开始以及结束透明度，还有动画的持续 时间，透明度的变化范围(0,1)，0是完全透明，1是完全不透明；对应<**alpha**/>标签！
> - **ScaleAnimation**：==缩放==渐变效果，创建时需指定开始以及结束的缩放比，以及缩放参考点， 还有动画的持续时间；对应<**scale**/>标签！
> - **TranslateAnimation**：==位移==渐变效果，创建时指定起始以及结束位置，并指定动画的持续 时间即可；对应<**translate**/>标签！
> - **RotateAnimation**：==旋转==渐变效果，创建时指定动画起始以及结束的旋转角度，以及动画 持续时间和旋转的轴心；对应<**rotate**/>标签
> - **AnimationSet**：==组合==渐变，就是前面多种渐变的组合，对应<**set**/>标签

在开始讲解各种动画的用法之前，我们先要来讲解一个东西：**Interpolator**

> 用来控制动画的变化速度，可以理解成动画渲染器，当然我们也可以自己实现Interpolator 接口，自行来控制动画的变化速度，而Android中已经为我们提供了五个可供选择的实现类：
>
> - **LinearInterpolator**：动画以均匀的速度改变
> - **AccelerateInterpolator**：在动画开始的地方改变速度较慢，然后开始加速
> - **AccelerateDecelerateInterpolator**：在动画开始、结束的地方改变速度较慢，中间时加速
> - **CycleInterpolator**：动画循环播放特定次数，变化速度按正弦曲线改变： Math.sin(2 * mCycles * Math.PI * input)
> - **DecelerateInterpolator**：在动画开始的地方改变速度较快，然后开始减速
> - **AnticipateInterpolator**：反向，先向相反方向改变一段再加速播放
> - **AnticipateOvershootInterpolator**：开始的时候向后然后向前甩一定值后返回最后的值
> - **BounceInterpolator**： 跳跃，快到目的值时值会跳跃，如目的值100，后面的值可能依次为85，77，70，80，90，100
> - **OvershottInterpolator**：回弹，最后超出目的值然后缓慢改变到目的值
>
> 而这个东东，我们一般是在写动画xml文件时会用到，属性是：**android:interpolator**， 而上面对应的值是：**@android:anim/linear_interpolator**，其实就是驼峰命名法变下划线而已 AccelerateDecelerateInterpolator对应：@android:anim/accelerate_decelerate_interpolator！

### 详解

#### **AlphaAnimation**

**anim_alpha.xml**：

```xml
<alpha xmlns:android="http://schemas.android.com/apk/res/android"  
    android:interpolator="@android:anim/accelerate_decelerate_interpolator"  
    android:fromAlpha="1.0"  
    android:toAlpha="0.1"  
    android:duration="2000"/>
```

属性解释：

**fromAlpha** :起始透明度
 **toAlpha**:结束透明度
 透明度的范围为：0-1，完全透明-完全不透明

#### **ScaleAnimation**

**anim_scale.xml**：

```xml
<scale xmlns:android="http://schemas.android.com/apk/res/android"  
    android:interpolator="@android:anim/accelerate_interpolator"  
    android:fromXScale="0.2"  
    android:toXScale="1.5"  
    android:fromYScale="0.2"  
    android:toYScale="1.5"  
    android:pivotX="50%"  
    android:pivotY="50%"  
    android:duration="2000"/>
```

属性解释：

> - **fromXScale**/**fromYScale**：沿着X轴/Y轴缩放的起始比例
> - **toXScale**/**toYScale**：沿着X轴/Y轴缩放的结束比例
> - **pivotX**/**pivotY**：缩放的中轴点X/Y坐标，即距离自身左边缘的位置，比如50%就是以图像的 中心为中轴点

#### **TranslateAnimation**

**anim_translate.xml**：

```
<translate xmlns:android="http://schemas.android.com/apk/res/android"  
    android:interpolator="@android:anim/accelerate_decelerate_interpolator"  
    android:fromXDelta="0"  
    android:toXDelta="320"  
    android:fromYDelta="0"  
    android:toYDelta="0"  
    android:duration="2000"/>
```

属性解释：

> - **fromXDelta**/**fromYDelta**：动画起始位置的X/Y坐标
> - **toXDelta**/**toYDelta**：动画结束位置的X/Y坐标

#### **RotateAnimation**

**anim_rotate.xml**：

```xml
<rotate xmlns:android="http://schemas.android.com/apk/res/android"  
    android:interpolator="@android:anim/accelerate_decelerate_interpolator"  
    android:fromDegrees="0"  
    android:toDegrees="360"  
    android:duration="1000"  
    android:repeatCount="1"  
    android:repeatMode="reverse"/> 
```

属性解释：

> - **fromDegrees**/**toDegrees**：旋转的起始/结束角度
> - **repeatCount**：旋转的次数，默认值为0，代表一次，假如是其他值，比如3，则旋转4次 另外，值为-1或者infinite时，表示动画永不停止
> - **repeatMode**：设置重复模式，默认**restart**，但只有当repeatCount大于0或者infinite或-1时 才有效。还可以设置成**reverse**，表示偶数次显示动画时会做方向相反的运动！

#### **AnimationSet**

非常简单，就是前面几个动画组合到一起而已~

**anim_set.xml**：

```xml
<set xmlns:android="http://schemas.android.com/apk/res/android"  
    android:interpolator="@android:anim/decelerate_interpolator"  
    android:shareInterpolator="true" >  
  
    <scale  
        android:duration="2000"  
        android:fromXScale="0.2"  
        android:fromYScale="0.2"  
        android:pivotX="50%"  
        android:pivotY="50%"  
        android:toXScale="1.5"  
        android:toYScale="1.5" />  
  
    <rotate  
        android:duration="1000"  
        android:fromDegrees="0"  
        android:repeatCount="1"  
        android:repeatMode="reverse"  
        android:toDegrees="360" />  
  
    <translate  
        android:duration="2000"  
        android:fromXDelta="0"  
        android:fromYDelta="0"  
        android:toXDelta="320"  
        android:toYDelta="0" />  
  
    <alpha  
        android:duration="2000"  
        android:fromAlpha="1.0"  
        android:toAlpha="0.1" />  

</set>  
```

#### 例子

好的，下面我们就用上面写的动画来写一个例子，让我们体会体会何为补间动画： 首先来个简单的布局：**activity_main.xml**：

```xml
LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_alpha"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="透明度渐变" />

    <Button
        android:id="@+id/btn_scale"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="缩放渐变" />

    <Button
        android:id="@+id/btn_tran"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="位移渐变" />

    <Button
        android:id="@+id/btn_rotate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="旋转渐变" />

    <Button
        android:id="@+id/btn_set"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="组合渐变" />

    <ImageView
        android:id="@+id/img_show"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="48dp"
        android:src="@mipmap/img_face" />
    
</LinearLayout>
```

好哒，接着到我们的**MainActivity.java**，同样非常简单，只需调用AnimationUtils.loadAnimation() 加载动画，然后我们的View控件调用startAnimation开启动画即可~

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_alpha;
    private Button btn_scale;
    private Button btn_tran;
    private Button btn_rotate;
    private Button btn_set;
    private ImageView img_show;
    private Animation animation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        btn_alpha = (Button) findViewById(R.id.btn_alpha);
        btn_scale = (Button) findViewById(R.id.btn_scale);
        btn_tran = (Button) findViewById(R.id.btn_tran);
        btn_rotate = (Button) findViewById(R.id.btn_rotate);
        btn_set = (Button) findViewById(R.id.btn_set);
        img_show = (ImageView) findViewById(R.id.img_show);

        btn_alpha.setOnClickListener(this);
        btn_scale.setOnClickListener(this);
        btn_tran.setOnClickListener(this);
        btn_rotate.setOnClickListener(this);
        btn_set.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_alpha:
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_alpha);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_scale:
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_scale);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_tran:
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_translate);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_rotate:
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_rotate);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_set:
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.anim_set);
                img_show.startAnimation(animation);
                break;
        }
    }
}
```

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/11/66905299.jpg)

### 动画状态的监听

> 我们可以对动画的执行状态进行监听，调用动画对象的：

- **setAnimationListener(new AnimationListener())**方法，重写下面的三个方法：
- **onAnimationStart**()：动画开始
- **onAnimtaionRepeat**()：动画重复
- **onAnimationEnd**()：动画结束

即可完成动画执行状态的监听~

### View动态设置动画效果

先调用**AnimationUtils.loadAnimation**(动画xml文件)，然后View控件调用==startAnimation(anim)== 开始动画~这是静态加载的方式，当然你也可以直接创建一个动画对象，用Java代码完成设置，再调用 startAnimation开启动画~

### Fragment设置过渡动画

这里要注意一点，就是Fragment是使用的**v4包**还是**app包**下的Fragment！ 我们可以调用**FragmentTransaction**对象的**setTransition(int transit)** 为Fragment指定标准的过场动画，transit的可选值如下：

- **TRANSIT_NONE**：无动画
- **TRANSIT_FRAGMENT_OPEN**：打开形式的动画
- **TRANSIT_FRAGMENT_CLOSE**：关闭形式的动画

上面的标准过程动画是两个都可以调用的，而不同的地方则在于自定义转场动画

**setCustomAnimations**()方法！

- **app包下的Fragment**： **setCustomAnimations(int enter, int exit, int popEnter, int popExit)** 分别是添加，移除，入栈，以及出栈时的动画！ 另外要注意一点的是，对应的动画类型是：属性动画(Property)，就是动画文件 的根标签要是：<**objectAnimator**>，<**valueAnimator**>或者是前面两者放到一个<**set**>里；
- **v4包下的Fragment**： v4包下的则支持两种setCustomAnimations()![img](https://www.runoob.com/wp-content/uploads/2015/11/34767523.jpg)

另外要注意一点的是，对应的动画类型是：补间动画(Tween)，和上面的View一样~

可能你会有疑惑，你怎么知道对应的动画类型，其实只要你到Fragment源码那里找下：

onCreateAnimation()方法的一个返回值就知道了：

**v4包**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/5983622.jpg)

**app包**：![img](https://www.runoob.com/wp-content/uploads/2015/11/55776797.jpg)

### Activity过场动画

Activty设置过场动画非常简单，调用的方法是：**overridePendingTransition**(int enterAnim, int exitAnim)

用法很简单：**在startActivity(intent)**或者**finish()**后添加

参数依次是：**新Activity进场**时的动画，以及**旧Activity退场**时的动画

#### 例子

![img](https://www.runoob.com/wp-content/uploads/2015/11/6026348.jpg)

**代码实现**：

首先是我们的布局文件：**activity_main.xml**：

```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#DDE2E3"
    tools:context=".MainActivity">

    <LinearLayout
        android:id="@+id/start_ctrl"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:orientation="vertical"
        android:visibility="gone">

        <Button
            android:id="@+id/start_login"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#F26968"
            android:gravity="center"
            android:paddingBottom="15dp"
            android:paddingTop="15dp"
            android:text="登陆"
            android:textColor="#FFFFFF"
            android:textSize="18sp" />

        <Button
            android:id="@+id/start_register"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#323339"
            android:gravity="center"
            android:paddingBottom="15dp"
            android:paddingTop="15dp"
            android:text="注册"
            android:textColor="#FFFFFF"
            android:textSize="18sp" />
    </LinearLayout>

</RelativeLayout>
```

接着是**MainActivity.java**：

```
public class MainActivity extends AppCompatActivity {
    private LinearLayout start_ctrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_ctrl = (LinearLayout) findViewById(R.id.start_ctrl);
        //设置动画，从自身位置的最下端向上滑动了自身的高度，持续时间为500ms
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        ctrlAnimation.setDuration(500l);     //设置动画的过渡时间
        start_ctrl.postDelayed(new Runnable() {
            @Override
            public void run() {
                start_ctrl.setVisibility(View.VISIBLE);
                start_ctrl.startAnimation(ctrlAnimation);
            }
        }, 2000);
    }
}
```

注释写得很清楚了，这里就不BB解释了，如果你对TranslateAnimation.RELATIVE_TO_SELF这个有疑惑， 请自己谷歌或者百度，限于篇幅(我懒)，这里就不写了，蛮简单的~![img](https://www.runoob.com/wp-content/uploads/2015/11/64331552.jpg)

#### 资源

[AnimationDemo3.zip](http://static.runoob.com/download/AnimationDemo3.zip)

[AnimationDemo4.zip](http://static.runoob.com/download/AnimationDemo4.zip)

## ==属性动画==

本节给带来的是Android动画中的第三种动画——属性动画(Property Animation)，App包和V4包下的Fragment调用setCustomAnimations()对应的 动画类型是不一样的，v4包下的是**Animation**，而app包下的是**Animator**；

**Animation一般动画**就是我们前面学的**帧动画和补间动画**！**Animator**则是本节要讲的**属性动画**！

关于属性动画，大牛郭大叔已经写了三篇非常好的总结文，写得非常赞，就没必要重复造轮子了， 不过这里还是过一遍，大部分内容参考的下面三篇文章：

[Android属性动画完全解析(上)，初识属性动画的基本用法](http://blog.csdn.net/guolin_blog/article/details/43536355)

[Android属性动画完全解析(中)，ValueAnimator和ObjectAnimator的高级用法](http://blog.csdn.net/guolin_blog/article/details/43816093)

[Android属性动画完全解析(下)，Interpolator和ViewPropertyAnimator的用法](http://blog.csdn.net/guolin_blog/article/details/44171115)

![img](https://www.runoob.com/wp-content/uploads/2015/11/4400658.jpg)

### ValueAnimator

**使用流程**：

- 1.调用ValueAnimator的**ofInt**()，**ofFloat**()或**ofObject**()静态方法创建ValueAnimator实例
- 2.调用实例的setXxx方法设置动画持续时间，插值方式，重复次数等
- 3.调用实例的**addUpdateListener**添加**AnimatorUpdateListener**监听器，在该监听器中 可以获得ValueAnimator计算出来的值，你可以值应用到指定对象上~
- 4.调用实例的**start()**方法开启动画！ 另外我们可以看到ofInt和ofFloat都有个这样的参数：float/int... values代表可以多个值！![img](https://www.runoob.com/wp-content/uploads/2015/11/73537268.jpg)

**代码实现**：

布局文件：**activity_main.xml**，非常简单，四个按钮，一个ImageView

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/ly_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_one"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="动画1" />

    <Button
        android:id="@+id/btn_two"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="动画2" />

    <Button
        android:id="@+id/btn_three"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="动画3" />

    <Button
        android:id="@+id/btn_four"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="动画4" />

    <ImageView
        android:id="@+id/img_babi"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="@mipmap/img_babi" />

</LinearLayout>
```

接着到**MainActivity.java**， 首先需要一个修改View位置的方法，这里调用**moveView**()设置左边和上边的起始坐标以及宽高！

接着定义了四个动画，分别是：直线移动，缩放，旋转加透明，以及圆形旋转！

然后通过按钮触发对应的动画~

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private Button btn_four;
    private LinearLayout ly_root;
    private ImageView img_babi;
    private int width;
    private int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        ly_root = (LinearLayout) findViewById(R.id.ly_root);
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_four = (Button) findViewById(R.id.btn_four);
        img_babi = (ImageView) findViewById(R.id.img_babi);

        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        img_babi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                lineAnimator();
                break;
            case R.id.btn_two:
                scaleAnimator();
                break;
            case R.id.btn_three:
                raAnimator();
                break;
            case R.id.btn_four:
                circleAnimator();
                break;
            case R.id.img_babi:
                Toast.makeText(MainActivity.this, "不愧是coder-pig~", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //定义一个修改ImageView位置的方法
    private void moveView(View view, int rawX, int rawY) {
        int left = rawX - img_babi.getWidth() / 2;
        int top = rawY - img_babi.getHeight();
        int width = left + view.getWidth();
        int height = top + view.getHeight();
        view.layout(left, top, width, height);
    }


    //定义属性动画的方法：

    //按轨迹方程来运动
    private void lineAnimator() {
        width = ly_root.getWidth();
        height = ly_root.getHeight();
        ValueAnimator xValue = ValueAnimator.ofInt(height,0,height / 4,height / 2,height / 4 * 3 ,height);
        xValue.setDuration(3000L);
        xValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 轨迹方程 x = width / 2
                int y = (Integer) animation.getAnimatedValue();
                int x = width / 2;
                moveView(img_babi, x, y);
            }
        });
        xValue.setInterpolator(new LinearInterpolator());
        xValue.start();
    }

    //缩放效果
    private void scaleAnimator(){
    
        //这里故意用两个是想让大家体会下组合动画怎么用而已~
        final float scale = 0.5f;
        AnimatorSet scaleSet = new AnimatorSet();
        ValueAnimator valueAnimatorSmall = ValueAnimator.ofFloat(1.0f, scale);
        valueAnimatorSmall.setDuration(500);

        ValueAnimator valueAnimatorLarge = ValueAnimator.ofFloat(scale, 1.0f);
        valueAnimatorLarge.setDuration(500);

        valueAnimatorSmall.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (Float) animation.getAnimatedValue();
                img_babi.setScaleX(scale);
                img_babi.setScaleY(scale);
            }
        });
        valueAnimatorLarge.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (Float) animation.getAnimatedValue();
                img_babi.setScaleX(scale);
                img_babi.setScaleY(scale);
            }
        });

        scaleSet.play(valueAnimatorLarge).after(valueAnimatorSmall);
        scaleSet.start();

        //其实可以一个就搞定的
//        ValueAnimator vValue = ValueAnimator.ofFloat(1.0f, 0.6f, 1.2f, 1.0f, 0.6f, 1.2f, 1.0f);
//        vValue.setDuration(1000L);
//        vValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float scale = (Float) animation.getAnimatedValue();
//                img_babi.setScaleX(scale);
//                img_babi.setScaleY(scale);
//            }
//        });
//        vValue.setInterpolator(new LinearInterpolator());
//        vValue.start();
    }


    //旋转的同时透明度变化
    private void raAnimator(){
        ValueAnimator rValue = ValueAnimator.ofInt(0, 360);
        rValue.setDuration(1000L);
        rValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int rotateValue = (Integer) animation.getAnimatedValue();
                img_babi.setRotation(rotateValue);
                float fractionValue = animation.getAnimatedFraction();
                img_babi.setAlpha(fractionValue);
            }
        });
        rValue.setInterpolator(new DecelerateInterpolator());
        rValue.start();
    }

    //圆形旋转
    protected void circleAnimator() {
        width = ly_root.getWidth();
        height = ly_root.getHeight();
        final int R = width / 4;
        ValueAnimator tValue = ValueAnimator.ofFloat(0,
                (float) (2.0f * Math.PI));
        tValue.setDuration(1000);
        tValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 圆的参数方程 x = R * sin(t) y = R * cos(t)
                float t = (Float) animation.getAnimatedValue();
                int x = (int) (R * Math.sin(t) + width / 2);
                int y = (int) (R * Math.cos(t) + height / 2);
                moveView(img_babi, x, y);
            }
        });
        tValue.setInterpolator(new DecelerateInterpolator());
        tValue.start();
    }
}
```

好的，使用的流程非常简单，先创建ValueAnimator对象，调用ValueAnimator.ofInt/ofFloat 获得，然后设置动画持续时间，**addUpdateListener**添加**AnimatorUpdateListener**事件监听， 然后使用参数**animation**的**getAnimatedValue**()获得当前的值，然后我们可以拿着这个值 来修改View的一些属性，从而形成所谓的动画效果，接着设置setInterpolator动画渲染模式， 最后调用start()开始动画的播放~

卧槽，直线方程，圆的参数方程，我都开始方了，这不是高数的东西么， 挂科学渣连三角函数都忘了...![img](https://www.runoob.com/wp-content/uploads/2015/11/34533934.jpg)

例子参考自github：[MoveViewValueAnimator](https://github.com/nuptboyzhb/MoveViewValueAnimator)

### ObjectAnimator

比起ValueAnimator，ObjectAnimator显得更为易用，通过该类我们可以**直接** 对==任意对象==的==任意属性==进行动画操作**！没错，是任意对象，而不单单只是View对象， 不断地对对象中的某个属性值进行赋值，然后根据对象属性值的改变再来决定如何展现 出来！比如为TextView设置如下动画： **ObjectAnimator.ofFloat(textview, "alpha", 1f, 0f);**
 这里就是不断改变alpha的值，从1f - 0f，然后对象根据属性值的变化来刷新界面显示，从而 展现出淡入淡出的效果，而在TextView类中并没有alpha这个属性，ObjectAnimator内部机制是： **寻找传输的属性名对应的get和set方法~，而非找这个属性值！** 不信的话你可以到TextView的源码里找找是否有alpha这个属性！ 好的，下面我们利用ObjectAnimator来实现四种补间动画的效果吧~

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/11/48695379.jpg)

**代码实现**：

布局直接用的上面那个布局，加了个按钮，把ImageView换成了TextView，这里就不贴代码了， 直接上**MainActivity.java**部分的代码，其实都是大同小异的~

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private Button btn_four;
    private Button btn_five;
    private LinearLayout ly_root;
    private TextView tv_pig;
    private int height;
    private ObjectAnimator animator1;
    private ObjectAnimator animator2;
    private ObjectAnimator animator3;
    private ObjectAnimator animator4;
    private AnimatorSet animSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initAnimator();
    }

    private void bindViews() {
        ly_root = (LinearLayout) findViewById(R.id.ly_root);
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_four = (Button) findViewById(R.id.btn_four);
        btn_five = (Button) findViewById(R.id.btn_five);
        tv_pig = (TextView) findViewById(R.id.tv_pig);

        height = ly_root.getHeight();
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_five.setOnClickListener(this);
        tv_pig.setOnClickListener(this);
    }

    //初始化动画
    private void initAnimator() {
        animator1 = ObjectAnimator.ofFloat(tv_pig, "alpha", 1f, 0f, 1f, 0f, 1f);
        animator2 = ObjectAnimator.ofFloat(tv_pig, "rotation", 0f, 360f, 0f);
        animator3 = ObjectAnimator.ofFloat(tv_pig, "scaleX", 2f, 4f, 1f, 0.5f, 1f);
        animator4 = ObjectAnimator.ofFloat(tv_pig, "translationY", height / 8, -100, height / 2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                animator1.setDuration(3000l);
                animator1.start();
                break;
            case R.id.btn_two:
                animator2.setDuration(3000l);
                animator2.start();
                break;
            case R.id.btn_three:
                animator3.setDuration(3000l);
                animator3.start();
                break;
            case R.id.btn_four:
                animator4.setDuration(3000l);
                animator4.start();
                break;
            case R.id.btn_five:
                //将前面的动画集合到一起~
                animSet = new AnimatorSet();
                animSet.play(animator4).with(animator3).with(animator2).after(animator1);
                animSet.setDuration(5000l);
                animSet.start();
                break;
            case R.id.tv_pig:
                Toast.makeText(MainActivity.this, "不愧是coder-pig~", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
```

### 组合动画与AnimatorListener

从上面两个例子中我们都体验了一把组合动画，用到了**AnimatorSet**这个类！

我们调用的play()方法，然后传入第一个开始执行的动画，此时他会返回一个Builder类给我们：

![img](https://www.runoob.com/wp-content/uploads/2015/11/42774404.jpg)

接下来我们可以调用Builder给我们提供的四个方法，来组合其他的动画：

- **after**(Animator anim)   将现有动画插入到传入的动画之后执行
- **after**(long delay)   将现有动画延迟指定毫秒后执行
- **before**(Animator anim)   将现有动画插入到传入的动画之前执行
- **with**(Animator anim)   将现有动画和传入的动画同时执行

嗯，很简单，接下来要说下动画事件的监听，上面我们ValueAnimator的监听器是 **AnimatorUpdateListener**，当值状态发生改变时候会回调**onAnimationUpdate**方法！

除了这种事件外还有：动画进行状态的监听~ **AnimatorListener**，我们可以调用**addListener**方法 添加监听器，然后重写下面四个回调方法：

- **onAnimationStart()**：动画开始
- **onAnimationRepeat()**：动画重复执行
- **onAnimationEnd()**：动画结束
- **onAnimationCancel()**：动画取消

没错，加入你真的用AnimatorListener的话，四个方法你都要重写，当然和前面的手势那一节一样， Android已经给我们提供好一个适配器类：**AnimatorListenerAdapter**，该类中已经把每个接口 方法都实现好了，所以我们这里只写一个回调方法也可以额！

### XML来编写动画

使用XML来编写动画，画的时间可能比Java代码长一点，但是重用起来就轻松很多！ 对应的XML标签分别为：<**animator**><**objectAnimator**><**set**> 相关的属性解释如下：

- **android:ordering**：指定动画的播放顺序：sequentially(顺序执行)，together(同时执行)
- **android:duration**：动画的持续时间
- **android:propertyName**="x"：这里的x，还记得上面的"alpha"吗？加载动画的那个对象里需要 定义getx和setx的方法，objectAnimator就是通过这里来修改对象里的值的！
- **android:valueFrom**="1" ：动画起始的初始值
- **android:valueTo**="0" ：动画结束的最终值
- **android:valueType**="floatType"：变化值的数据类型

**使用例子如下**：

①**从0到100平滑过渡的动画**：

```xml
<animator xmlns:android="http://schemas.android.com/apk/res/android"  
    android:valueFrom="0"  
    android:valueTo="100"  
    android:valueType="intType"/>
```

②**将一个视图的alpha属性从1变成0**：

```xml
<objectAnimator xmlns:android="http://schemas.android.com/apk/res/android"  
    android:valueFrom="1"  
    android:valueTo="0"  
    android:valueType="floatType"  
    android:propertyName="alpha"/>
```

③**set动画使用演示**：

```xml
<set android:ordering="sequentially" >
    <set>
        <objectAnimator
            android:duration="500"
            android:propertyName="x"
            android:valueTo="400"
            android:valueType="intType" />
        <objectAnimator
            android:duration="500"
            android:propertyName="y"
            android:valueTo="300"
            android:valueType="intType" />
    </set>
    <objectAnimator
        android:duration="500"
        android:propertyName="alpha"
        android:valueTo="1f" />
</set>
```

**加载我们的动画文件**：

```java
AnimatorSet set = (AnimatorSet)AnimatorInflater.loadAnimator(mContext, 
             R.animator.property_animator);  
animator.setTarget(view);  
animator.start();  
```

#### 资源

[AnimatorDemo1.zip](http://static.runoob.com/download/AnimatorDemo1.zip)

[AnimatorDemo2.zip](http://static.runoob.com/download/AnimatorDemo2.zip)

### Evaluator自定义



调用ValueAnimator的**ofInt**()，**ofFloat**()或**ofObject**()静态方法创建ValueAnimator实例！

在例子中，ofInt和ofFloat我们都用到了，分别用于对浮点型和整型的数据进行动画操作！

那么**ofObject**()？初始对象和结束对象？如何过渡法？或者说这玩意怎么用？

好的，带着疑问，我们先来了解一个东西：Evaluator，在属性动画概念叨叨逼处其实我们就说到了这个东西：![img](https://www.runoob.com/wp-content/uploads/2015/11/54469594.jpg)

**用来告诉动画系统如何从初始值过渡到结束值**！好的，我们的入手点没错！ 我们进去IntEvaluator的源码，看下里面写了些什么？![img](https://www.runoob.com/wp-content/uploads/2015/11/48403251.jpg)

嗯，实现了**TypeEvaluator**接口，然后重写了**evaluate()**方法，参数有三个，依次是：

- **fraction**：动画的完成度，我们根据他来计算动画的值应该是多少
- **startValue**：动画的起始值
- **endValue**：动画的结束值

**动画的值 = 初始值 + 完成度 \* (结束值 - 初始值)**

#### 例子

同样的还有FloatEvaluator，我们想告诉系统如何从初始对象过度到结束对象，那么我们就要 自己来实现**TypeEvaluator**接口，即自定义Evaluator了，说多无益，写个例子来看看：

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/11/10209964.jpg)

**代码实现**：

定义一个对象**Point.java**，对象中只有x，y两个属性以及get，set方法~

```java
/**
 * Created by Jay on 2015/11/18 0018.
 */
public class Point {

    private float x;
    private float y;

    public Point() {
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
```

接着自定义Evaluator类：**PointEvaluator.java**，实现接口重写evaluate方法~

```java
/**
 * Created by Jay on 2015/11/18 0018.
 */
public class PointEvaluator implements TypeEvaluator<Point>{
    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        float x = startValue.getX() + fraction * (endValue.getX() - startValue.getX());
        float y = startValue.getY() + fraction * (endValue.getY() - startValue.getY());
        Point point = new Point(x, y);
        return point;
    }
}
```

然后自定义一个View类：**AnimView.java**，很简单~

```java
/**
 * Created by Jay on 2015/11/18 0018.
 */
public class AnimView extends View {

    public static final float RADIUS = 80.0f;
    private Point currentPoint;
    private Paint mPaint;

    public AnimView(Context context) {
        this(context, null);
    }

    public AnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
    }

    private void drawCircle(Canvas canvas){
        float x = currentPoint.getX();
        float y = currentPoint.getY();
        canvas.drawCircle(x, y, RADIUS, mPaint);
    }

    private void startAnimation() {
        Point startPoint = new Point(RADIUS, RADIUS);
        Point endPoint = new Point(getWidth() - RADIUS, getHeight() - RADIUS);
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(3000l);
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentPoint == null) {
            currentPoint = new Point(RADIUS, RADIUS);
            drawCircle(canvas);
            startAnimation();
        } else {
            drawCircle(canvas);
        }
    }
}
```

最后**MainActivity.java**处实例化这个View即可~

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new AnimView(this));
    }
}
```

#### **示例增强版**

我们上面示例的基础上加上圆移动时的颜色变化~ 这里我们另外用一个ObjectAnimator来加载颜色变化的动画，我们在View中加多个 int color来控制颜色，另外写上getColor()和setColor()的方法，我们先来自定义个Evaluator吧~

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/11/65529491.jpg)

**实现代码**：

**ColorEvaluator.java**：

```java
/**
 * Created by Jay on 2015/11/18 0018.
 */
public class ColorEvaluator implements TypeEvaluator<Integer>{
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int alpha = (int) (Color.alpha(startValue) + fraction *
                (Color.alpha(endValue) - Color.alpha(startValue)));
        int red = (int) (Color.red(startValue) + fraction *
                (Color.red(endValue) - Color.red(startValue)));
        int green = (int) (Color.green(startValue) + fraction *
                (Color.green(endValue) - Color.green(startValue)));
        int blue = (int) (Color.blue(startValue) + fraction *
                (Color.blue(endValue) - Color.blue(startValue)));
        return Color.argb(alpha, red, green, blue);
    }
}
```

然后自定义View那里加个color，get和set方法；创建一个ObjectAnimator， 和AnimatorSet，接着把动画组合到一起就到，这里就加点东西而已，怕读者有问题， 直接另外建个View吧~

**AnimView2.java**：

```java
/**
 * Created by Jay on 2015/11/18 0018.
 */
public class AnimView2 extends View {

    public static final float RADIUS = 80.0f;
    private Point currentPoint;
    private Paint mPaint;
    private int mColor;

    public AnimView2(Context context) {
        this(context, null);
    }

    public AnimView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
    }



    private void drawCircle(Canvas canvas){
        float x = currentPoint.getX();
        float y = currentPoint.getY();
        canvas.drawCircle(x, y, RADIUS, mPaint);
    }

    private void startAnimation() {
        Point startPoint = new Point(RADIUS, RADIUS);
        Point endPoint = new Point(getWidth() - RADIUS, getHeight() - RADIUS);
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this, "color", new ColorEvaluator(),
                Color.BLUE, Color.RED);
        //动画集合将前面两个动画加到一起，with同时播放
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(anim).with(objectAnimator);
        animatorSet.setStartDelay(1000l);
        animatorSet.setDuration(3000l);
        animatorSet.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentPoint == null) {
            currentPoint = new Point(RADIUS, RADIUS);
            drawCircle(canvas);
            startAnimation();
        } else {
            drawCircle(canvas);
        }
    }

    //color的get和set方法~
    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(color);
        invalidate();
    }
}
```

然后MainActivity，setContentView那里把AnimView改成AnimView2就好~

### Interpolator(补间器)

![img](https://www.runoob.com/wp-content/uploads/2015/11/73854084.jpg)

上面的补间器==补间动画==和==属性动画==都可用，而且补间动画还新增了一个**TimeInterpolator**接口 该接口是用于兼容之前的Interpolator的，这使得所有过去的Interpolator实现类都可以直接拿过来 放到属性动画当中使用！我们可以调用动画对象的setInterpolator()方法设置不同的Interpolator！ 我们先该点东西，让小球从屏幕正中央的顶部掉落到底部~ 然后我们会我们为我们的集合动画调用下述语句： **animatorSet.setInterpolator(new AccelerateInterpolator(2f));** 括号里的值用于控制加速度~

**运行效果**：![img](https://www.runoob.com/wp-content/uploads/2015/11/6420198.jpg)

好像有点不和常理，正常应该是会弹起来的吧，我们换成**BounceInterpolator**试试~![img](https://www.runoob.com/wp-content/uploads/2015/11/42165604.jpg)

#### **Interpolator的内部机制**

我们先到TimeInterpolator接口的源码，发现这里只有一个**getInterpolation**()方法；![img](https://www.runoob.com/wp-content/uploads/2015/11/6378219.jpg)

**简单的解释**： getInterpolation()方法中接收一个input参数，这个参数的值会随着动画的运行而不断变化， 不过它的变化是非常有规律的，就是根据设定的动画时长匀速增加，变化范围是0到1。 也就是说当动画一开始的时候input的值是0，到动画结束的时候input的值是1，而中间的值则 是随着动画运行的时长在0到1之间变化的。

这里的**input**值决定了我们**TypeEvaluator**接口里的**fraction**的值。 input的值是由系统经过计算后传入到getInterpolation()方法中的，然后我们可以自己实现 **getInterpolation**()方法中的算法，根据input的值来计算出一个返回值，而这个返回值就是fraction了。

我们可以看看**LinearInterpolator**里的代码：![img](https://www.runoob.com/wp-content/uploads/2015/11/53510297.jpg)

这里没有处理过直接返回input值，即fraction的值就是等于input的值，这就是匀速运动的 Interpolator的实现方式！其实无非就是算法不同，这就涉及到一些数学的东西了，又一次 体会到数学的重要性了，这里再贴个**BounceInterpolator**的源码吧：![img](https://www.runoob.com/wp-content/uploads/2015/11/43642406.jpg)

别问我这里的算法，我也不知道哈，我们再找个容易理解点的：**AccelerateDecelerateInterpolator**![img](https://www.runoob.com/wp-content/uploads/2015/11/80592279.jpg)

这个Interpolator是先加速后减速效果的： **(float)(Math.cos((input + 1) \* Math.PI) / 2.0f) + 0.5f** 的算法理解：

解：由input的取值范围为[0,1]，可以得出cos中的值的取值范围为[π,2π]，对应的值为-1和1； 再用这个值来除以2加上0.5之后，getInterpolation()方法最终返回的结果值范围还是[0,1]， 对应的曲线图如下：![img](https://www.runoob.com/wp-content/uploads/2015/11/Center)

所以是一个先加速后减速的过程！嗯，学渣没法玩了...![img](https://www.runoob.com/wp-content/uploads/2015/11/90823139.jpg)，上面全是郭大叔文章里搬过来的...我想静静...

#### **自定义Interpolator**

好吧，还是等会儿再忧伤吧，写个自定义的Interpolator示例先： 非常简单，实现TimeInterpolator接口，重写getInterpolation方法

**示例代码如下**

```java
private class DecelerateAccelerateInterpolator implements TimeInterpolator {
    @Override
    public float getInterpolation(float input) {
        if (input < 0.5) {
            return (float) (Math.sin(input * Math.PI) / 2);
        } else {
            return 1 - (float) (Math.sin(input * Math.PI) / 2);
        }
    }
}
```

调用setInterpolator(new DecelerateAccelerateInterpolator())设置下即可~ 限于篇幅就不贴图了~

#### ViewPropertyAnimator

3.1后系统当中附增的一个新的功能，为View的动画操作提供一种更加便捷的用法！ 假如是以前，让一个TextView从正常状态变成透明状态，会这样写：

```java
ObjectAnimator animator = ObjectAnimator.ofFloat(textview, "alpha", 0f);  
animator.start();
```

而使用ViewPropertyAnimator来实现同样的效果则显得更加易懂：

```java
textview.animate().alpha(0f); 
```

还支持**连缀用法**，组合多个动画，设定时长，设置Interpolator等~

```java
textview.animate().x(500).y(500).setDuration(5000)  
        .setInterpolator(new BounceInterpolator());
```

用法很简单，使用的时候查下文档就好~，另外下面有几个细节的地方要注意一下！

- 整个ViewPropertyAnimator的功能都是建立在View类新增的animate()方法之上的， 这个方法会创建并返回一个ViewPropertyAnimator的实例，之后的调用的所有方法， 设置的所有属性都是通过这个实例完成的。
- 使用ViewPropertyAnimator将**动画定义完成之后**，动画就会**自动启动**。 并且这个机制对于组合动画也同样有效，只要我们不断地连缀新的方法， 那么动画就不会立刻执行，等到所有在ViewPropertyAnimator上设置的方法都执行完毕后， 动画就会自动启动。当然如果不想使用这一默认机制的话，我们也可以显式地调用 **start**()方法来启动动画。
- ViewPropertyAnimator的所有接口都是使用连缀的语法来设计的，每个方法的返回值都是 它**自身的实例**，因此调用完一个方法之后可以直接连缀调用它的另一个方法，这样把所有的 功能都串接起来，我们甚至可以仅通过一行代码就完成任意复杂度的动画功能。

##### 资源

[AnimatorDemo3.zip](http://static.runoob.com/download/AnimatorDemo3.zip)

在Github上找到一个动画合集的项目，很多动画效果都有，下面贴下地址：

[BaseAnimation 动画合集](https://github.com/z56402344/BaseAnimation)

想研究各种动画是如何实现的可自行查看源码~

