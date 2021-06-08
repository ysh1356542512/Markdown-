# ==分类==

自定义View的实现方式有以下几种

| 类型              | 定义                                                         |
| ----------------- | ------------------------------------------------------------ |
| 自定义组合控件    | 多个控件组合成为一个新的控件，方便多处复用                   |
| 继承系统View控件  | 继承自TextView等系统控件，在系统控件的基础功能上进行扩展     |
| 继承View          | 不复用系统控件逻辑，继承View进行功能定义                     |
| 继承系统ViewGroup | 继承自LinearLayout等系统控件，在系统控件的基础功能上进行扩展 |
| 继承ViewViewGroup | 不复用系统控件逻辑，继承ViewGroup进行功能定义                |

# ==View绘制流程==

## 总概括

View的绘制基本由measure()、layout()、draw()这个三个函数完成

| 函数      | 作用                         | 相关方法                                     |
| --------- | ---------------------------- | -------------------------------------------- |
| measure() | 测量View的宽高               | measure(),setMeasuredDimension(),onMeasure() |
| layout()  | 计算当前View以及子View的位置 | layout(),onLayout(),setFrame()               |
| draw()    | 视图的绘制工作               | draw(),onDraw()                              |

## Measure()





### MeasureSpec

`MeasureSpec`是View的==内部类==，它封装了一个View的尺寸，在`onMeasure()`当中会根据这个`MeasureSpec`的值来确定View的宽高。

`MeasureSpec`的值保存在一个int值当中。一个int值有32位，前两位表示==`模式mode`==后30位表示==`大小size`==。即==`MeasureSpec` = `mode` + `size==。

在`MeasureSpec`当中一共存在三种`mode`：`UNSPECIFIED`、`EXACTLY` 和
 `AT_MOST`。

对于View来说，`MeasureSpec`的mode和Size有如下意义对于

| 模式        | 意义                                                         | 对应             |
| ----------- | ------------------------------------------------------------ | ---------------- |
| EXACTLY     | 精准模式，View需要一个精确值，这个值即为MeasureSpec当中的Size | match_parent     |
| AT_MOST     | 最大模式，View的尺寸有一个最大值，View不可以超过MeasureSpec当中的Size值 | wrap_content     |
| UNSPECIFIED | 无限制，View对尺寸没有任何限制，View设置为多大就应当为多大   | 一般系统内部使用 |

#### 使用方式



```java
    // 获取测量模式（Mode）
    int specMode = MeasureSpec.getMode(measureSpec)
    // 获取测量大小（Size）
    int specSize = MeasureSpec.getSize(measureSpec)
    // 通过Mode 和 Size 生成新的SpecMode
    int measureSpec=MeasureSpec.makeMeasureSpec(size, mode);
```

####  getChildMeasureSpec



```java
public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);

        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
                
        //当父View要求一个精确值时，为子View赋值
        case MeasureSpec.EXACTLY:
            //如果子view有自己的尺寸，则使用自己的尺寸
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
                //当子View是match_parent,将父View的大小赋值给子View
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
                //如果子View是wrap_content，设置子View的最大尺寸为父View
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // 父布局给子View了一个最大界限
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                //如果子view有自己的尺寸，则使用自己的尺寸
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // 父View的尺寸为子View的最大尺寸
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                //父View的尺寸为子View的最大尺寸
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // 父布局对子View没有做任何限制
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
            //如果子view有自己的尺寸，则使用自己的尺寸
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                //因父布局没有对子View做出限制，当子View为MATCH_PARENT时则大小为0
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                //因父布局没有对子View做出限制，当子View为WRAP_CONTENT时则大小为0
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
        }
    
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }
```

这里需要注意，这段代码只是在为子View设置`MeasureSpec`参数而不是实际的设置子View的大小。子View的最终大小需要在View中具体设置。

==从源码可以看出来，子View的测量模式是由自身LayoutParam和父View的MeasureSpec来决定的==。

从源码可以看出来，子View的测量模式是由自身LayoutParam和父View的MeasureSpec来决定的。

在测量子View大小时：

| 父View mode | 子View                                                       |
| ----------- | ------------------------------------------------------------ |
| UNSPECIFIED | 父布局没有做出限制，子View有自己的尺寸，则使用，如果没有则为0 |
| EXACTLY     | 父布局采用精准模式，有确切的大小，如果有大小则直接使用，如果子View没有大小，子View不得超出父view的大小范围 |
| AT_MOST     | 父布局采用最大模式，存在确切的大小，如果有大小则直接使用，如果子View没有大小，子View不得超出父view的大小范围 |

#### onMeasure()

整个测量过程的入口位于`View`的`measure`方法当中，该方法做了一些参数的初始化之后调用了`onMeasure`方法，这里我们主要分析`onMeasure`。

`onMeasure`方法的源码如下：

```java
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
```

很简单这里只有一行代码，涉及到了三个方法我们挨个分析。

- **setMeasuredDimension(int measuredWidth, int measuredHeight)** ：该方法用来设置View的宽高，在我们自定义View时也会经常用到。
- **getDefaultSize(int size, int measureSpec)**：该方法用来获取View默认的宽高，结合源码来看。

```java
/**
*   有两个参数size和measureSpec
*   1、size表示View的默认大小，它的值是通过`getSuggestedMinimumWidth()方法来获取的，之后我们再分析。
*   2、measureSpec则是我们之前分析的MeasureSpec，里面存储了View的测量值以及测量模式
*/
public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        //从这里我们看出，对于AT_MOST和EXACTLY在View当中的处理是完全相同的。所以在我们自定义View时要对这两种模式做出处理。
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }
```

**getSuggestedMinimumWidth()**：getHeight和该方法原理是一样的，这里只分析这一个。

```java
//当View没有设置背景时，默认大小就是mMinWidth，这个值对应Android:minWidth属性，如果没有设置时默认为0.
//如果有设置背景，则默认大小为mMinWidth和mBackground.getMinimumWidth()当中的较大值。
protected int getSuggestedMinimumWidth() {
        return (mBackground == null) ? mMinWidth : max(mMinWidth, mBackground.getMinimumWidth());
    }
```

==ViewGroup==`的测量过程与View有一点点区别，其本身是继承自`View`，它没有对`View`的`measure`方法以及`onMeasure`方法进行==重写==。

为什么没有重写`onMeasure`呢？ViewGroup除了要测量自身宽高外还需要测量各个子`View`的大小，而不同的布局测量方式也都不同（可参考`LinearLayout`以及`FrameLayout`），所以没有办法统一设置。因此它提供了==测量子`View`的方法`measureChildren()`以及`measureChild()`帮助我们对子View进行测量==。

==`measureChildren()`==以及`measureChild()`的源码这里不再分析，大致流程就是==遍历所有的子View，然后调用`View`的`measure()`方法==，让子`View`测量自身大小。具体测量流程上面也以及介绍过了

`measure`过程会因为布局的不同或者需求的不同而呈现不同的形式，使用时还是要根据业务场景来具体分析，如果想再深入研究可以看一下`LinearLayout`的`onMeasure`方法。

## Layout()

要计算位置首先要对Android坐标系有所了解，前面的内容我们也有介绍过。

`layout()`过程，对于`View`来说用来计算`View`的位置参数,对于==`ViewGroup`==来说，除了要测量自身位置，还需要测量子`View`的位置。

`layout()`方法是整个Layout()流程的入口，看一下这部分源码

```java
/**
*  这里的四个参数l、t、r、b分别代表View的左、上、右、下四个边界相对于其父View的距离。
*
*/
public void layout(int l, int t, int r, int b) {
        if ((mPrivateFlags3 & PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT) != 0) {
            onMeasure(mOldWidthMeasureSpec, mOldHeightMeasureSpec);
            mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
        }

        int oldL = mLeft;
        int oldT = mTop;
        int oldB = mBottom;
        int oldR = mRight;

        //这里通过setFrame或setOpticalFrame方法确定View在父容器当中的位置。
        boolean changed = isLayoutModeOptical(mParent) ?
                setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);

        //调用onLayout方法。onLayout方法是一个空实现，不同的布局会有不同的实现。
        if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {
            onLayout(changed, l, t, r, b);

        }

    }
```



从源码我们知道，在`layout()`方法中已经通过`setOpticalFrame(l, t, r, b)`或 `setFrame(l, t, r, b)`方法对View自身的位置进行了设置，所以`onLayout(changed, l, t, r, b)`方法主要是`ViewGroup`对子View的位置进行计算。

> 有兴趣的可以看一下`LinearLayout`的`onLayout`源码，可以帮助加深理解。

##  Draw()

draw流程也就是的View绘制到屏幕上的过程，整个流程的入口在`View`的`draw()`方法之中，而源码注释也写的很明白，整个过程可以分为6个步骤。

1. 如果需要，绘制背景。
2. 有过有必要，保存当前canvas。
3. 绘制View的内容。
4. 绘制子View。
5. 如果有必要，绘制边缘、阴影等效果。
6. 绘制装饰，如滚动条等等。

通过各个步骤的源码再做分析：

```java
    public void draw(Canvas canvas) {

       
        int saveCount;
        // 1. 如果需要，绘制背景
        if (!dirtyOpaque) {
            drawBackground(canvas);
        }

        // 2. 有过有必要，保存当前canvas。
        final int viewFlags = mViewFlags;
      
        if (!verticalEdges && !horizontalEdges) {
            // 3. 绘制View的内容。
            if (!dirtyOpaque) onDraw(canvas);

            // 4. 绘制子View。
            dispatchDraw(canvas);

            drawAutofilledHighlight(canvas);

            // Overlay is part of the content and draws beneath Foreground
            if (mOverlay != null && !mOverlay.isEmpty()) {
                mOverlay.getOverlayView().dispatchDraw(canvas);
            }

            // 6. 绘制装饰，如滚动条等等。
            onDrawForeground(canvas);

            // we're done...
            return;
        }
    }
    
    /**
    *  1.绘制View背景
    */
    private void drawBackground(Canvas canvas) {
        //获取背景
        final Drawable background = mBackground;
        if (background == null) {
            return;
        }

        setBackgroundBounds();

        //获取便宜值scrollX和scrollY，如果scrollX和scrollY都不等于0，则会在平移后的canvas上面绘制背景。
        final int scrollX = mScrollX;
        final int scrollY = mScrollY;
        if ((scrollX | scrollY) == 0) {
            background.draw(canvas);
        } else {
            canvas.translate(scrollX, scrollY);
            background.draw(canvas);
            canvas.translate(-scrollX, -scrollY);
        }
    }
    
    /**
    * 3.绘制View的内容,该方法是一个空的实现，在各个业务当中自行处理。
    */
    protected void onDraw(Canvas canvas) {
    }
    
    /**
    * 4. 绘制子View。该方法在View当中是一个空的实现，在各个业务当中自行处理。
    *  在ViewGroup当中对dispatchDraw方法做了实现，主要是遍历子View，并调用子类的draw方法，一般我们不需要自己重写该方法。
    */
    protected void dispatchDraw(Canvas canvas) {

    }
        
```



 

















在Android坐标系中，以屏幕左上角作为原点，这个原点向右是X轴的正轴，向下是Y轴正轴。如下所示：![img](https://upload-images.jianshu.io/upload_images/10294405-a57f0f703dca0eb4.png?imageMogr2/auto-orient/strip|imageView2/2/w/491)

除了Android坐标系，还存在View坐标系，View坐标系内部关系如图所示。![img](https://upload-images.jianshu.io/upload_images/10294405-4ca426e6a92db696.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200)

## View获取自身高度

由上图可算出View的高度：

- width = getRight() -  getLeft();
- height  =  getBottom()  -  getTop();

View的源码当中提供了getWidth()和getHeight()方法用来获取View的宽度和高度，其内部方法和上文所示是相同的，我们可以直接调用来获取View得宽高。

## View自身的坐标

通过如下方法可以获取View到其父控件的距离。

- getTop()；获取View到其父布局顶边的距离。
- getLeft()；获取View到其父布局左边的距离。
- getBottom()；获取View到其父布局顶边的距离。
- getRight()；获取View到其父布局左边的距离。

# 构造函数

```java
public class TestView extends View {
    /**
     * 在java代码里new的时候会用到
     * @param context
     */
    public TestView(Context context) {
        super(context);
    }

    /**
     * 在xml布局文件中使用时自动调用
     * @param context
     */
    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 只有在API版本>21时才会用到
     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
```

# 自定义属性

Android系统的控件以android开头的都是系统自带的属性。为了方便配置自定义View的属性，我们也可以自定义属性值。
 Android自定义属性可分为以下几步:

1. 自定义一个View
2. 编写==values/attrs.xml，在其中编写styleable和item等标签元素==
3. 在布局文件中View使用自定义的属性（==注意namespace==）
4. 在View的构造方法中通过==TypedArray==获取

## 实例说明

### 自定义属性的声明文件

```xml
    <?xml version="1.0" encoding="utf-8"?>    <resources>        <declare-styleable name="test">            <attr name="text" format="string" />            <attr name="testAttr" format="integer" />        </declare-styleable>    </resources>
```

### 自定义View类

```java
public class MyTextView extends View {    private static final String TAG = MyTextView.class.getSimpleName();    //在View的构造方法中通过TypedArray获取    public MyTextView(Context context, AttributeSet attrs) {        super(context, attrs);        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.test);        String text = ta.getString(R.styleable.test_testAttr);        int textAttr = ta.getInteger(R.styleable.test_text, -1);        Log.e(TAG, "text = " + text + " , textAttr = " + textAttr);        ta.recycle();    }}
```

### 布局文件中使用

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"    xmlns:tools="http://schemas.android.com/tools"    xmlns:app="http://schemas.android.com/apk/res/com.example.test"    android:layout_width="match_parent"    android:layout_height="match_parent" >    <com.example.test.MyTextView        android:layout_width="100dp"        android:layout_height="200dp"        app:testAttr="520"        app:text="helloworld" /></RelativeLayout>
```

## 属性值的类型format

###   reference：

参考某一资源ID

#### 属性定义：

```xml
<declare-styleable name = "名称">     <attr name = "background" format = "reference" /></declare-styleable>
```

#### 属性使用：

```xml
<ImageView android:background = "@drawable/图片ID"/>
```

###   color

#### 属性定义

```xml
<attr name = "textColor" format = "color" />
```

#### 属性使用

```xml
<TextView android:textColor = "#00FF00" />
```

### boolean

#### 属性定义

```xml
<attr name = "focusable" format = "boolean" />
```

#### 属性使用

```xml
<attr name = "focusable" format = "boolean" />
```

### integer

#### 属性定义

```xml
<attr name = "framesCount" format="integer" />
```

#### 属性使用

```xml
<animated-rotate android:framesCount = "12"/>
```

### dimension

#### 属性定义

```xml
<attr name = "layout_width" format = "dimension" />
```

#### 属性使用

```xml
<Button android:layout_width = "42dip"/>
```

### float

#### 属性定义

```xml
<attr name = "fromAlpha" format = "float" />
```

#### 属性使用

```xml
<alpha android:fromAlpha = "1.0"/>
```

### string

#### 属性定义

```xml
<attr name = "text" format = "string" />
```

#### 属性使用

```xml
<TextView android:text = "我是文本"/>
```

### fraction：百分数

#### 属性定义

```xml
<attr name = "pivotX" format = "fraction" />
```

#### 属性使用

```xml
<rotate android:pivotX = "200%"/>
```

###  enum：枚举值

#### 属性定义

```xml
<declare-styleable name="名称">    <attr name="orientation">        <enum name="horizontal" value="0" />        <enum name="vertical" value="1" />    </attr></declare-styleable>
```

#### 属性使用

```xml
<LinearLayout      android:orientation = "vertical"></LinearLayout>
```

==注意：枚举类型的属性在使用的过程中只能同时使用其中一个，不能 android:orientation = “horizontal｜vertical"==





### flag：位或运算

#### 属性定义

```xml
<declare-styleable name="名称">    <attr name="gravity">            <flag name="top" value="0x01" />            <flag name="bottom" value="0x02" />            <flag name="left" value="0x04" />            <flag name="right" value="0x08" />            <flag name="center_vertical" value="0x16" />            ...    </attr></declare-styleable>
```

#### 属性使用

```xml
<TextView android:gravity="bottom|left"/>
```

### 混合类型：

==属性定义时可以指定多种类型值==

#### 属性定义

```xml
<declare-styleable name = "名称">     <attr name = "background" format = "reference|color" /></declare-styleable>
```

#### 属性使用

```xml
<ImageViewandroid:background = "@drawable/图片ID" />或者：<ImageViewandroid:background = "#00FF00" />
```



# ==事件分发==

1.如果销售链没有形成，零售商不能找到总代理直接要到事件的消费权。2.销售链形成后，再次来了事件，会直接沿着销售链走，不会再次去询问了。3.当销售链形成后，底层对上层有反响制约的权利。4.上层拥有两次选择的机会，1.刚收到事件2.全部都没有处理事件。

![ScreenClip](../../%E5%9B%BE%E5%BA%93/%E8%87%AA%E5%AE%9A%E4%B9%89view%E5%9F%BA%E7%A1%80/ScreenClip.png)

*/****** *这一句话 在自定义**view**里面引用布局是必须的 否则将会报错***/*LayoutInflater.*from*(context).inflate(R.layout.*search_layout*, this);

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



## 绘图工具

### ==Paint==

> 就是画笔,用于设置绘制风格,如:线宽(笔触粗细),颜色,透明度和填充风格等 直接使用无参构造方法就可以创建Paint实例: **Paint paint = new Paint( );**
>
> 我们可以通过下述方法来设置Paint(画笔)的相关属性,另外,关于这个属性有两种, 图形绘制相关与文本绘制相关:
>
> - **setARGB**(int a,int r,int g,int b):   设置绘制的颜色，a代表透明度，r，g，b代表颜色值。
> - **setAlpha**(int a):   设置绘制图形的透明度。
> - **setColor**(int color):   设置绘制的颜色，使用颜色值来表示，该颜色值包括透明度和RGB颜色。
> - **setAntiAlias**(boolean aa):  设置是否使用抗锯齿功能，会消耗较大资源，绘制图形速度会变慢。
> - **setDither**(boolean dither): 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
> - **setFilterBitmap**(boolean filter)： 如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作， 加快显示速度，本设置项依赖于dither和xfermode的设置
> - **setMaskFilter**(MaskFilter maskfilter)：   设置MaskFilter，可以用不同的MaskFilter实现滤镜的效果，如滤化，立体等
> - **setColorFilter**(ColorFilter colorfilter)：  设置颜色过滤器，可以在绘制颜色时实现不用颜色的变换效果
> - **setPathEffect**(PathEffect effect)  设置绘制路径的效果，如点画线等
> - **setShader**(Shader shader)：    设置图像效果，使用Shader可以绘制出各种渐变效果
> - **setShadowLayer**(float radius ,float dx,float dy,int color)：在图形下面设置阴影层，产生阴影效果， radius为阴影的角度，dx和dy为阴影在x轴和y轴上的距离，color为阴影的颜色
> - **setStyle**(Paint.Style style)：   设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
> - **setStrokeCap**(Paint.Cap cap)： 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，                                             如圆形样Cap.ROUND,或方形样式Cap.SQUARE
> - **setSrokeJoin**(Paint.Join join)：  设置绘制时各图形的结合方式，如平滑效果等
> - **setStrokeWidth**(float width)： 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
> - **setXfermode**(Xfermode xfermode)：  设置图形重叠时的处理方式，如合并，取交集或并集，经常用来制作橡皮的擦除效果
> - **setFakeBoldText**(boolean fakeBoldText)： 模拟实现粗体文字，设置在小字体上效果会非常差
> - **setSubpixelText**(boolean subpixelText)： 设置该项为true，将有助于文本在LCD屏幕上的显示效果
> - **setTextAlign**(Paint.Align align)：  设置绘制文字的对齐方向
> - **setTextScaleX**(float scaleX)：  设置绘制文字x轴的缩放比例，可以实现文字的拉伸的效果
> - **setTextSize**(float textSize)：   设置绘制文字的字号大小
> - **setTextSkewX**(float skewX)：  设置斜体文字，skewX为倾斜弧度
> - **setTypeface**(Typeface typeface)：  设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
> - **setUnderlineText**(boolean underlineText)：   设置带有下划线的文字效果
> - **setStrikeThruText**(boolean strikeThruText)：  设置带有删除线的效果
> - **setStrokeJoin**(Paint.Join join)： 设置结合处的样子，Miter:结合处为锐角， Round:结合处为圆弧：BEVEL：结合处为直线
> - **setStrokeMiter**(float miter)：设置画笔倾斜度
> - **setStrokeCap** (Paint.Cap cap)：设置转弯处的风格 其他常用方法：
> - float **ascent**( )：测量baseline之上至字符最高处的距离
> -  ![img](https://www.runoob.com/wp-content/uploads/2015/10/77090782.jpg)
> - float **descent**()：baseline之下至字符最低处的距离
> - int **breakText**(char[] text, int index, int count, float maxWidth, float[] measuredWidth)： 检测一行显示多少文字
> - **clearShadowLayer**( )：清除阴影层 其他的自行查阅文档~

#### MaskFilter(面具)

> 在[Android基础入门教程——8.3.1 三个绘图工具类详解](https://www.runoob.com/w3cnote/android-tutorial-drawable-tool.html)的Paint方法中有这样一个方法：
>
> **setMaskFilter(MaskFilter maskfilter)**： 设置MaskFilter，可以用不同的MaskFilter实现滤镜的效果，如滤化，立体等！ 而我们一般不会直接去用这个MaskFilter，而是使用它的两个子类：
>
> **BlurMaskFilter**：指定了一个模糊的样式和半径来处理Paint的边缘。
>
> **EmbossMaskFilter**：指定了光源的方向和环境光强度来添加浮雕效果。 下面我们来写个例子来试验一下~！
>
> 官方API文档：
> [BlurMaskFilter](http://androiddoc.qiniudn.com/reference/android/graphics/BlurMaskFilter.html)；
> [EmbossMaskFilter](http://androiddoc.qiniudn.com/reference/android/graphics/EmbossMaskFilter.html)；

##### BlurMaskFilter(模糊效果)

说什么滤镜立体，谁知道怎么样，示例![img](https://www.runoob.com/wp-content/uploads/2015/10/14201002.jpg)见真知：

**实现代码**：

> 这里我们创建一个自定义View，在里面完成绘制！

**BlurMaskFilterView.java**：

```java
/**
 * Created by Jay on 2015/10/21 0021.
 */
public class BlurMaskFilterView extends View{

    public BlurMaskFilterView(Context context) {
        super(context);
    }

    public BlurMaskFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurMaskFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        BlurMaskFilter bmf = null;
        Paint paint=new Paint();
        paint.setAntiAlias(true);          //抗锯齿
        paint.setColor(Color.RED);//画笔颜色
        paint.setStyle(Paint.Style.FILL);  //画笔风格
        paint.setTextSize(68);             //绘制文字大小，单位px
        paint.setStrokeWidth(5);           //画笔粗细

        bmf = new BlurMaskFilter(10f,BlurMaskFilter.Blur.NORMAL);
        paint.setMaskFilter(bmf);
        canvas.drawText("最喜欢看曹神日狗了~", 100, 100, paint);
        bmf = new BlurMaskFilter(10f,BlurMaskFilter.Blur.OUTER);
        paint.setMaskFilter(bmf);
        canvas.drawText("最喜欢看曹神日狗了~", 100, 200, paint);
        bmf = new BlurMaskFilter(10f,BlurMaskFilter.Blur.INNER);
        paint.setMaskFilter(bmf);
        canvas.drawText("最喜欢看曹神日狗了~", 100, 300, paint);
        bmf = new BlurMaskFilter(10f,BlurMaskFilter.Blur.SOLID);
        paint.setMaskFilter(bmf);
        canvas.drawText("最喜欢看曹神日狗了~", 100, 400, paint);
        
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);     //关闭硬件加速
    }
}
```

> 好的，从上面的代码示例，我们可以发现，我们使用这个BlurMaskFilter，无非是， 在构造方法中实例化：
>
> **BlurMaskFilter(10f,BlurMaskFilter.Blur.NORMAL);**
>
> 我们可以控制的就是这两个参数：
>
> **第一个参数**：指定模糊边缘的半径；
>
> **第二个参数**：指定模糊的风格，可选值有：
>
> - BlurMaskFilter.Blur.**NORMAL**：内外模糊
> - BlurMaskFilter.Blur.**OUTER**：外部模糊
> - BlurMaskFilter.Blur.**INNER**：内部模糊
> - BlurMaskFilter.Blur.**SOLID**：内部加粗，外部模糊

可能还是有点不清晰，我们找个图片来试试：



![img](https://www.runoob.com/wp-content/uploads/2015/10/18321619.jpg)

这里我们把模糊半径修改成了50，就更加明显了~



##### EmbossMaskFilter(浮雕效果)

如题，通过指定环境光源的方向和环境光强度来添加浮雕效果，同样，我们写个示例来看看效果：

![img](https://www.runoob.com/wp-content/uploads/2015/10/79392450.jpg)

**实现代码**：

```java
/**
 * Created by Jay on 2015/10/22 0022.
 */
public class EmbossMaskFilterView extends View{

    public EmbossMaskFilterView(Context context) {
        super(context);
    }

    public EmbossMaskFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmbossMaskFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float[] direction = new float[]{ 1, 1, 3 };   // 设置光源的方向
        float light = 0.4f;     //设置环境光亮度
        float specular = 8;     // 定义镜面反射系数
        float blur = 3.0f;      //模糊半径
        EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,specular,blur);

        Paint paint = new Paint();
        paint.setAntiAlias(true);          //抗锯齿
        paint.setColor(Color.BLUE);//画笔颜色
        paint.setStyle(Paint.Style.FILL);  //画笔风格
        paint.setTextSize(70);             //绘制文字大小，单位px
        paint.setStrokeWidth(8);           //画笔粗细
        paint.setMaskFilter(emboss);

        paint.setMaskFilter(emboss);
        canvas.drawText("最喜欢看曹神日狗了~", 50, 100, paint);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bg_meizi1);
        canvas.drawBitmap(bitmap, 150, 200, paint);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);     //关闭硬件加速
    }
}
```

从效果图我们就可以看出一些EmbossMaskFilter的效果，修改光线，形成浮雕效果~妹子图不明显， 文字就很清晰显示出纹路了！和BlurMaskFilter一样，相关的设置都是在构造方法中进行！

**EmbossMaskFilter(float[] direction, float ambient, float specular, float blurRadius)** 参数依次是：

**direction**：浮点型数组，用于控制x,y,z轴的光源方向

**ambient**：设置环境光亮度，0到1之间

**specular**：镜面反射系数

**blurRadius**：模糊半径

你可以修改这些值，试试不同的效果，比如我修改下上述的，又会是另一种效果：

//这里为了明显点，换成了绿色![img](https://www.runoob.com/wp-content/uploads/2015/10/4535741.jpg)

##### 注意事项

> 在使用MaskFilter的时候要注意，当我们的targetSdkVersion >= 14的时候，MaskFilter 就不会起效果了，这是因为Android在API 14以上版本都是默认开启硬件加速的，这样充分 利用GPU的特性，使得绘画更加平滑，但是会多消耗一些内存！好吧，我们把硬件加速关了 就好，可以在不同级别下打开或者关闭硬件加速，一般是关闭~
>
> - **Application**：在配置文件的application节点添加： android:hardwareAccelerated="true"
> - **Activity**：在配置文件的activity节点添加 android:hardwareAccelerated="false"
> - **View**：可以获得View对象后调用，或者直接在View的onDraw()方法里设置： view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

##### 资源

[MaskFilterDemo.zip](http://static.runoob.com/download/MaskFilterDemo.zip)



#### PathEffect

本节继续来学习Paint的API——PathEffect(路径效果)，我们把画笔的sytle设置为Stroke，可以 绘制一个个由线构成的图形，而这些线偶尔会显得单调是吧，比如你想把这些先改成虚线，又 或者想让路径的转角变得圆滑等，那你就可以考虑使用这个PathEffect来实现了！

官方API文档：[PathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/PathEffect.html) 进去看文档，可以发现这个PathEffect和我们前面学的MaskFilter(面具)与ColorFilter(颜色 过滤器)一样，几乎没有可用的方法：![img](https://www.runoob.com/wp-content/uploads/2015/11/8910442.jpg)

我们一般使用的是他的六个子类：

- [ComposePathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/ComposePathEffect.html)
- [CornerPathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/CornerPathEffect.html)
- [DashPathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/DashPathEffect.html)
- [DiscretePathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/DiscretePathEffect.html)
- [PathDashPathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/PathDashPathEffect.html)
- [SumPathEffect](http://androiddoc.qiniudn.com/reference/android/graphics/SumPathEffect.html)

下面我们依次对他们的作用，以及构造方法进行分析！

##### CornerPathEffect

**CornerPathEffect(float radius)**

将Path的各个连接线段之间的夹角用一种更平滑的方式连接，类似于圆弧与切线的效果。 radius则是指定圆弧的半径！

##### DashPathEffect

**DashPathEffect(float[] intervals, float phase)**

> 将Path的线段虚线化，intervals为虚线的ON和OFF的数组，数组中元素数目需要 >= 2； 而phase则为绘制时的偏移量！

##### DiscretePathEffect

**DiscretePathEffect(float segmentLength, float deviation)**

> 打散Path的线段，使得在原来路径的基础上发生打散效果。 segmentLength指定最大的段长，deviation则为绘制时的偏离量。

##### PathDashPathEffect

**PathDashPathEffect(Path shape, float advance, float phase, PathDashPathEffect.Style style)**

> 作用是使用Path图形来填充当前的路径，shape指的填充图形，advance是每个图形间的间隔， style则是该类自由的枚举值，有三种情况：**ROTATE**、**MORPH**和**TRANSLATE**。
>
> - ROTATE情况下：线段连接处的图形转换以旋转到与下一段移动方向相一致的角度进行连接
> - MORPH情况下：图形会以发生拉伸或压缩等变形的情况与下一段相连接
> - TRANSLATE情况下：图形会以位置平移的方式与下一段相连接

##### ComposePathEffect

**ComposePathEffect(PathEffect outerpe, PathEffect innerpe)**

> 作用是：**组合效果**，会首先将innerpe变现出来，接着在innerpe的基础上来增加outerpe效果！

##### SumPathEffect

**SumPathEffect(PathEffect first, PathEffect second)**

> 作用是：叠加效果，和ComposePathEffect不同，在表现时会将两个参数的效果都独立的表现出来， 接着将两个效果简单的重叠在一起显示出来！

##### 效果

多说无益，写代码最实际，我们写下代码来试试这几个子类各自所起的效果！

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/1690080.jpg)

**实现代码**：

我们自己来写一个View，里面的线移动的效果是phase增加造成的，每次 + 2， 然后invalidate重绘而已，所以别惊讶！**PathEffectView.java**:

```java
/**
 * Created by Jay on 2015/10/30 0030.
 */
public class PathEffectView extends View {

    private Paint mPaint;
    private Path mPath;
    private float phase = 0;
    private PathEffect[] effects = new PathEffect[7];
    private int[] colors;

    public PathEffectView(Context context) {
        this(context, null);
    }

    public PathEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PathEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //初始化画笔
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);       //绘画风格:空心
        mPaint.setStrokeWidth(5);                  //笔触粗细
        mPath = new Path();
        mPath.moveTo(0, 0);
        for (int i = 1; i <= 15; i++) {
            // 生成15个点，随机生成它们的坐标，并将它们连成一条Path
            mPath.lineTo(i * 40, (float) Math.random() * 100);
        }
        // 初始化7个颜色
        colors = new int[] { Color.RED, Color.BLUE, Color.GREEN,
                Color.YELLOW, Color.BLACK, Color.MAGENTA, Color.CYAN };
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        //初始化其中路径效果：
        effects[0] = null;                                    //无效果
        effects[1] = new CornerPathEffect(10);                //CornerPathEffect
        effects[2] = new DiscretePathEffect(3.0f, 5.0f);      //DiscretePathEffect
        effects[3] = new DashPathEffect(new float[] { 20, 10, 5, 10 },phase);   //DashPathEffect
        Path p = new Path();
        p.addRect(0, 0, 8, 8, Path.Direction.CCW);
        effects[4] = new PathDashPathEffect(p, 12, phase,
                PathDashPathEffect.Style.ROTATE);             //PathDashPathEffect
        effects[5] = new ComposePathEffect(effects[2], effects[4]);    //ComposePathEffect
        effects[6] = new SumPathEffect(effects[2], effects[4]);   //SumPathEffect
        // 将画布移动到(10,10)处开始绘制
        canvas.translate(10, 10);
        // 依次使用7中不同的路径效果、7中不同的颜色来绘制路径
        for (int i = 0; i < effects.length; i++) {
            mPaint.setPathEffect(effects[i]);
            mPaint.setColor(colors[i]);
            canvas.drawPath(mPath, mPaint);
            canvas.translate(0, 60);
        }
        // 改变phase值，形成动画效果
        phase += 2;
        invalidate();
    }
}
```

##### 资源

[PathEffectDemo.zip](http://static.runoob.com/download/PathEffectDemo.zip)

#### Paint枚举/常量值ShadowLayer

在[Android基础入门教程——8.3.1 三个绘图工具类详解](https://www.runoob.com/w3cnote/android-tutorial-drawable-tool.html)Paint的方法参数那里我们就接触到 了这样几个东西：Paint.Style，Paint.Cap，Paint.Join等，这些都是Paint中的一些枚举值，相关 方法我们可以通过设置这些枚举值来设置特定效果比如：Style：画笔样式，Join图形结合方式等， 本节我们走进Paint的源码，我们来一一介绍这些枚举值，另外我们也顺道讲下这个ShadowLayer 设置带阴影效果的Paint！打开Paint类的源码，我们可以看到下述这些枚举值：

![img](https://www.runoob.com/wp-content/uploads/2015/11/78803613.jpg)

不知大家对枚举陌生还是熟悉，这里把贴下Paint.Style相关的调用代码(带有参构造方法的枚举) ，让大家体会体会：

```java
public enum Style {
    //定义枚举,通过括号赋值
    FILL            (0),
    STROKE          (1),
    FILL_AND_STROKE (2);
    //有参构造方法
    Style(int nativeInt) {
        this.nativeInt = nativeInt;
    }
    final int nativeInt;
}
//设置画笔Style的方法
public void setStyle(Style style) {
    native_setStyle(mNativePaint, style.nativeInt);
}
//JNI设置画笔风格的方法，这里我们无需关注
private static native void native_setStyle(long native_object, int style);
```

下面我们一一来解释这些枚举值的作用！

##### Paint.Style

作用：画笔的样式 可选值：

- **FILL**：填充内部(默认)
- **STROKE**：只描边
- **FILL_AND_STROKE**：填充内部与描边

方法调用：**setStyle(Paint.Style style)** 对应效果：

##### Paint.Cap

作用：笔触风格，设置画笔始末端的图形(画笔开始画的第一点与最后一点) 可选值：

- **BUTT**：笔触是长方形且不超过路径(默认)
- **ROUND**：笔触是圆形
- **SQUARE**：笔触是正方形

方法调用：**setStrokeCap(Paint.Cap cap)**

对应效果：平时我们直接画的是第一个，其他两个会比普通的多一点而外的区域，第二个 是圆角，第三个是矩形！![img](https://www.runoob.com/wp-content/uploads/2015/11/69848625.jpg)

##### Paint.Join

> 作用：设置接合处的状态，比如你的线是由多条小线拼接而成，拼接处的形状 可选值：
>
> - **MITER**：接合处为锐角(默认)
> - **ROUND**：接合处为圆弧
> - **BEVEL**：接合处为直线
>
> 方法调用：**setStrokeJoin(Paint.Join join)**
>
> 一般圆弧用得多，可参见之前的[擦掉美女衣服Demo的显示](https://www.runoob.com/w3cnote/android-tutorial-xfermode-porterduff4.html)
>
> 另外还有个**setStrokeMiter(float miter)**是设置笔画的倾斜度，miter > = 0； 如：小时候用的铅笔，削的时候斜与垂直削出来的笔尖效果是不一样的。 主要是用来设置笔触的连接处的样式。可以和setStrokeJoin()来比较比较。

------

##### Paint.Align

作用：设置绘制文本的对其方式，就是相对于绘制文字的[x,y]起始坐标 可选值：

- **LEFT**：在起始坐标的左边绘制文本
- **RIGHT**：在起始坐标的右边绘制文本
- **CENTER**：以其实坐标为中心绘制文本

方法调用：**setTextAlign(Paint.Align align)**

对应效果：另外可调用setTextSize()设置绘制文本的大小~

![img](https://www.runoob.com/wp-content/uploads/2015/11/52138677.jpg)

##### Paint.FontMetrics和Paint.FontMetricsInt

字体属性及测量，另外这两个方法是一样的，只是后者取到的值是一个整形， 这里我们选FontMetricsInt来给大家讲解下，有下面这五个常量值，这里参考的基准点是： 下划线的位置(**Baseline**)

- **top**：最高字符到baseline的距离，即ascent的最大值
- **ascent**：字符最高处的距离到baseline的值
- **descent**：下划线到字符最低处的距离
- **bottom**：下划线到最低字符的距离，即descent的最大值
- **leading**：上一行字符的descent到下一行的ascent之间的距离

我们看几个图帮助理解下：

![img](https://www.runoob.com/wp-content/uploads/2015/11/38400901.jpg) 

![img](https://www.runoob.com/wp-content/uploads/2015/11/77090782.jpg)  

![img](https://www.runoob.com/wp-content/uploads/2015/11/82757632.jpg)

然后我们随意画一串字母，把这些值打印出来： **canvas.drawText("abcdefghijklnmopqrstuvwxyz", 400, 400, mPaint1);**
 **Log.e("HEHE", mPaint1.getFontMetricsInt().toString());**
 运行下，我们可以看到，打印出来的Log如下：

![img](https://www.runoob.com/wp-content/uploads/2015/11/69209696.jpg)

看完思考思考，画一画，应该不难理解！这里我们知道下就好，如果你想更 深入研究，可以参考下这篇：[Android字符串进阶之三：字体属性及测量（FontMetrics）](http://mikewang.blog.51cto.com/3826268/871765/)

##### ShadowLayer

我们在TextView那一节就教过大家为TextView的文本设置阴影效果，而Paint其实也提供了设置 阴影效果的API：**setShadowLayer(float radius, float dx, float dy, int shadowColor)** 

参数：radius为阴影的角度，dx和dy为阴影在x轴和y轴上的距离，shadowColor为阴影的颜色 我们可以写个非常简单的句子验证下：

```java
mPaint1.setShadowLayer(5,0,0,Color.BLACK);
canvas.drawText("毕竟基神~", 400, 400, mPaint1);    //绘制文字
```

**效果如下**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/12266092.jpg)

另外我们还可以调用**clearShadowLayer()**来清除这个阴影层~

#### Typeface

本节带来Paint API系列的最后一个API，**Typeface(字型)**，由字义，我们大概可以猜到，这个 API是用来设置字体以及字体风格的，使用起来也非常的简单！下面我们来学习下Typeface的一些相关 的用法！

官方API文档：[Typeface](http://androiddoc.qiniudn.com/reference/android/graphics/Typeface.html)~

  ![img](https://www.runoob.com/wp-content/uploads/2015/11/98427188.jpg)

##### 可选风格

> 四个整型常量：
>
> - **BOLD**：加粗
> - **ITALIC**：斜体
> - **BOLD_ITALIC**：粗斜体
> - **NORMAL**：正常

##### 字体对象

> Android系统默认支持三种字体，分别为：**sans**，**serif**，**monospace** 而提供的可选静态对象值有五个：
>
> - **DEFAULT**：默认正常字体对象
> - **DEFAULT_BOLD**：默认的字体对象，注意:这实际上不可能是粗体的,这取决于字体设置。 由getStyle()来确定
> - **MONOSPACE**：monospace 字体风格
> - **SANS_SERIF**：sans serif字体风格
> - **SERIF**：serif字体风格

##### 自定义

> 可能默认的三种字体并不能满足你，可能你喜欢MAC的字体——**Monaco字体**，你想让你APP 里的文字可以用这种字体，首先准备好我们的TTF文件，然后丢到**assets/font/**目录下 然后创建对应对象，关键代码如下：
>
> **Typeface typeFace =Typeface.createFromAsset(getAssets(),"font/MONACO.ttf");**

##### 示例

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/69781721.jpg)

自定义的View类：**MyView.java**：

```java
/**
 * Created by Jay on 2015/11/5 0005.
 */
public class MyView extends View{

    private Paint mPaint1,mPaint2,mPaint3,mPaint4,mPaint5;
    private Context mContext;

    public MyView(Context context) {
        this(context,null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
        mPaint1 = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        mPaint4 = new Paint();
        mPaint5 = new Paint();

        mPaint1.setColor(Color.RED);
        mPaint2.setColor(Color.BLUE);
        mPaint3.setColor(Color.BLACK);
        mPaint4.setColor(Color.YELLOW);
        mPaint5.setColor(Color.GRAY);


        mPaint1.setTextSize(100);
        mPaint2.setTextSize(100);
        mPaint3.setTextSize(100);
        mPaint4.setTextSize(100);
        mPaint5.setTextSize(100);


        mPaint1.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint2.setTypeface(Typeface.MONOSPACE);
        mPaint3.setTypeface(Typeface.SANS_SERIF);
        mPaint4.setTypeface(Typeface.SERIF);
        mPaint5.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/MONACO.ttf"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText("Coder-pig", 100, 100, mPaint1);
        canvas.drawText("Coder-pig", 100, 200, mPaint2);
        canvas.drawText("Coder-pig", 100, 300, mPaint3);
        canvas.drawText("Coder-pig", 100, 400, mPaint4);
        canvas.drawText("Coder-pig", 100, 500, mPaint5);
    }
}
```

##### 资源

[TypefaceDemo.zip](http://static.runoob.com/download/TypefaceDemo.zip)



### ==Canvas==

#### 瞄之一观



> 画笔有了，接着就到画布(Canvas)，总不能凭空作画是吧~常用方法如下：
>
> 首先是**构造方法**，Canvas的构造方法有两种：
>
> **Canvas()**: 创建一个空的画布，可以使用setBitmap()方法来设置绘制具体的画布。
>
> **Canvas(Bitmap bitmap)**: 以bitmap对象创建一个画布，将内容都绘制在bitmap上，因此bitmap不得为null。
>
> 接着是 **1.drawXXX()**方法族：以一定的坐标值在当前画图区域画图，另外图层会叠加， 即后面绘画的图层会覆盖前面绘画的图层。 比如：
>
> - **drawRect**(RectF rect, Paint paint) ：绘制区域，参数一为RectF一个区域
> - **drawPath**(Path path, Paint paint) ：绘制一个路径，参数一为Path路径对象
> - **drawBitmap**(Bitmap bitmap, Rect src, Rect dst, Paint paint)  ： 贴图，参数一就是我们常规的Bitmap对象，参数二是源区域(这里是bitmap)， 参数三是目标区域(应该在canvas的位置和大小)，参数四是Paint画刷对象， 因为用到了缩放和拉伸的可能，当原始Rect不等于目标Rect时性能将会有大幅损失。
> - **drawLine**(float startX, float startY, float stopX, float stopY, Paintpaint) ： 画线，参数一起始点的x轴位置，参数二起始点的y轴位置，参数三终点的x轴水平位置， 参数四y轴垂直位置，最后一个参数为Paint 画刷对象。
> - **drawPoint**(float x, float y, Paint paint)： 画点，参数一水平x轴，参数二垂直y轴，第三个参数为Paint对象。
> - **drawText**(String text, float x, floaty, Paint paint) ： 渲染文本，Canvas类除了上面的还可以描绘文字，参数一是String类型的文本， 参数二x轴，参数三y轴，参数四是Paint对象。
> - **drawOval**(RectF oval, Paint paint)：画椭圆，参数一是扫描区域，参数二为paint对象；
> - **drawCircle**(float cx, float cy, float radius,Paint paint)： 绘制圆，参数一是中心点的x轴，参数二是中心点的y轴，参数三是半径，参数四是paint对象；
> - **drawArc**(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)： 画弧，参数一是RectF对象，一个矩形区域椭圆形的界限用于定义在形状、大小、电弧，参数二是起始角 (度)在电弧的开始，参数三扫描角(度)开始顺时针测量的，参数四是如果这是真的话,包括椭圆中心的电 弧,并关闭它,如果它是假这将是一个弧线,参数五是Paint对象；
>
> 2.**clipXXX()**方法族:在当前的画图区域裁剪(clip)出一个新的画图区域，这个画图区域就是canvas 对象的当前画图区域了。比如：clipRect(new Rect())，那么该矩形区域就是canvas的当前画图区域
>
> 3.**save()**和**restore()**方法： **save**( )：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作！ **restore**（）：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。 save()和restore()要配对使用(restore可以比save少,但不能多)，若restore调用次数比save多,会报错！
>
> 4.**translate**(float dx, float dy)： 平移，将画布的坐标原点向左右方向移动x，向上下方向移动y.canvas的默认位置是在（0,0）
>
> 5.**scale**(float sx, float sy)：扩大，x为水平方向的放大倍数，y为竖直方向的放大倍数
>
> 6.**rotate**(float degrees)：旋转，angle指旋转的角度，顺时针旋转



#### 基础

> 
>
> [8.3.1 三个绘图工具类详解](http://www.runoob.com/w3cnote/android-tutorial-drawable-tool.html)
>
> 中已经列出了我们可供调用的一些方法，我们分下类：
>
> - **drawXxx方法族**：以一定的坐标值在当前画图区域画图，另外图层会叠加， 即后面绘画的图层会覆盖前面绘画的图层。
> - **clipXXX方法族**：在当前的画图区域裁剪(clip)出一个新的画图区域，这个 画图区域就是canvas对象的当前画图区域了。比如：clipRect(new Rect())， 那么该矩形区域就是canvas的当前画图区域
> - **getXxx方法族**：获得与Canvas相关一些值，比如宽高，屏幕密度等。
> - **save**()，**restore**()，**saveLayer**()，**restoreToCount**()等保存恢复图层的方法
> - **translate**(平移)，**scale**(缩放)，**rotate**(旋转)，**skew**(倾斜)
>
> 当然还有其他一些零散的方法，嗯，从本节开始我会挑一些感觉有点意思的API来进行学习~
>
> 而本节先给大家带来的是**translate**(平移)，**scale**(缩放)，**rotate**(旋转)，**skew**(倾斜) 以及**save**()，**restore**()的详解！
>
> 官方API文档：[Canvas](http://androiddoc.qiniudn.com/reference/android/graphics/Canvas.html)
>
> 另外我们先要明确Canvas中X轴与Y轴的方向：
>
> ![img](https://www.runoob.com/wp-content/uploads/2015/11/39871179.jpg)

##### translate

> **方法**：**translate**(float dx, float dy)
>
> **解析**：平移，将画布的**坐标原点**向左右方向移动x，向上下方向移动y，canvas默认位置在(0,0)
>
> **参数**：dx为水平方向的移动距离，dy为垂直方向的移动距离

**示例**

```java
for(int i=0; i < 5; i++) {
    canvas.drawCircle(50, 50, 50, mPaint);
    canvas.translate(100, 100);
}
```

**运行效果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/67035226.jpg)

##### rotate

> **方法**：**rotate**(float degrees) / **rotate**(float degrees, float px, float py)
>
> **解析**：围绕坐标原点旋转degrees度，值为正顺时针
>
> **参数**：degrees为旋转角度，px和py为指定旋转的中心点坐标(px,py)

**使用示例**：

```java
Rect rect = new Rect(50,0,150,50);
canvas.translate(200, 200);
for(int i = 0; i < 36;i++){
    canvas.rotate(10);
    canvas.drawRect(rect, mPaint);
}
```

**运行效果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/4328266.jpg)



**代码分析**：

这里我们先调用了translate(200，200)将canvas的坐标原点移向了(200,200)，再进行绘制，所以我们 绘制的结果可以完整的在画布上显示出来，假如我们是为rotate设置了(10,200,200)，会是这样一个 结果：

![img](https://www.runoob.com/wp-content/uploads/2015/11/69692038.jpg)



疑问是吧，这个涉及到Canvas多图层的概念，后面会讲~



##### scale

> **方法**：**scale**(float sx, float sy) / **scale**(float sx, float sy, float px, float py)
>
> **解析**：对画布进行缩放
>
> **参数**：sx为水平方向缩放比例，sy为竖直方向的缩放比例，px和py我也不知道，**小数为缩小**， **整数为放大**

**使用示例**：

```java
canvas.drawBitmap(bmp,0,0,mPaint);
canvas.scale(0.8f, 0.8f);
canvas.drawBitmap(bmp, 0, 0, mPaint);
canvas.scale(0.8f, 0.8f);
canvas.drawBitmap(bmp,0,0,mPaint);
```

**运行效果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/94500483.jpg)

##### skew

> **方法**：**skew**(float sx, float sy)
>
> **解析**：倾斜，也可以译作斜切，扭曲
>
> **参数**：sx为x轴方向上倾斜的对应角度，sy为y轴方向上倾斜的对应角度，两个值都是tan值哦！ 都是tan值！都是tan值！比如要在x轴方向上倾斜60度，那么小数值对应:tan 60 = 根号3 = 1.732！

**使用示例**：

```java
canvas.drawBitmap(bmp,0,0,mPaint);
canvas.translate(200, 200);
canvas.skew(0.2f,-0.8f);
canvas.drawBitmap(bmp,0,0,mPaint);
```

**运行效果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/84530658.jpg)

##### Canvas图层

我们一般喜欢称呼Canvas为画布，童鞋们一直觉得Canvas就是一张简单的画纸，那么我想 问下多层的动画是怎么用canvas来完成的？上面那个translate平移的例子，为什么 drawCircle(50, 50, 50, mPaint); 参考坐标一直是(50,50)那为何会出现这样的效果？ 有疑惑的童鞋可能是一直将屏幕的概念与Canvas的概念混淆了，下面我们来还原下 调用translate的案发现场：![img](https://www.runoob.com/wp-content/uploads/2015/11/91706861.jpg)

如图，是画布坐标原点的每次分别在x，y轴上移动100；那么假如我们要重新回到(0,0) 点处绘制新的图形呢？怎么破，translate(-100,-100)的慢慢地平移回去？不会真的这么 纠结吧..。

![img](https://www.runoob.com/wp-content/uploads/2015/11/76166242.jpg)

好吧，不卖关子了，我们可以在做平移变换之前将当前canvas的状态进行保存，其实Canvas为 我们提供了图层(Layer)的支持，而这些Layer(图层)是按"栈结构"来进行管理的![img](https://www.runoob.com/wp-content/uploads/2015/11/45837171.jpg)

当我们调用**save()**方法，会保存当前Canvas的状态然后作为一个Layer(图层)，添加到Canvas栈中， 另外，这个Layer(图层)不是一个具体的类，就是一个概念性的东西而已！

而当我们调用**restore()**方法的时候，会恢复之前Canvas的状态，而此时Canvas的图层栈 会弹出栈顶的那个Layer，后继的Layer来到栈顶，此时的Canvas回复到此栈顶时保存的Canvas状态！

简单说就是**：save()往栈压入一个Layer，restore()弹出栈顶的一个Layer，这个Layer代表Canvas的 状态！**也就是说可以save()多次，也可以restore()多次，但是restore的调用次数**不能大于**save 否则会引发错误！这是网上大部分的说法，不过实际测试中并没有出现这样的问题，即使我restore的 次数多于save，也没有出现错误~目测是系统改了，等下测给大家看~ ![img](https://www.runoob.com/wp-content/uploads/2015/11/89654180.jpg) 来来来，写个例子验证下save和restore的作用！

**写个例子**：

**例子代码**：

```java
canvas.save();  //保存当前canvas的状态

canvas.translate(100, 100);

canvas.drawCircle(50, 50, 50, mPaint);

canvas.restore();  //恢复保存的Canvas的状态

canvas.drawCircle(50, 50, 50, mPaint);
```

**运行结果**：![img](https://www.runoob.com/wp-content/uploads/2015/11/10539224.jpg)

不用说什么了吧，代码和结果已经说明了一切，接着我们搞得复杂点，来一发 多个save()和restore()！

**例子代码：**

```java
canvas.save();

canvas.translate(300, 300);
canvas.drawBitmap(bmp, 0, 0, mPaint);
canvas.save();

canvas.rotate(45);
canvas.drawBitmap(bmp, 0, 0, mPaint);
canvas.save();

canvas.rotate(45);
canvas.drawBitmap(bmp, 0, 0, mPaint);
canvas.save();

canvas.translate(0, 200);
canvas.drawBitmap(bmp, 0, 0, mPaint);
```

**运行结果**![img](https://www.runoob.com/wp-content/uploads/2015/11/37911934.jpg)

**结果分析**：

首先平移(300,300)画图，然后旋转45度画图，再接着旋转45度画图，接着平移(0,200)， 期间每次画图前都save()一下，看到这里你可能有个疑问，最后这个平移不是y移动200 么，怎么变成向左了？嘿嘿，我会告诉你rotate()旋转的是整个坐标轴么？坐标轴的 变化：

![img](https://www.runoob.com/wp-content/uploads/2015/11/9381594.jpg)

嗯，rotate()弄懂了是吧，那就行，接着我们来试试restore咯~我们在最后绘图的前面 加两个restore()！

```java
canvas.restore();
canvas.restore();
canvas.translate(0, 200);
canvas.drawBitmap(bmp, 0, 0, mPaint);
```



**运行结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/51696172.jpg)

不说什么，自己体会，再加多个**restore()**！

![img](https://www.runoob.com/wp-content/uploads/2015/11/32919528.jpg)

有点意思，再来，继续加**restore()**

![img](https://www.runoob.com/wp-content/uploads/2015/11/84371338.jpg)



嗯，好像不可以再写**restore**了是吧，因为我们只save了四次，按照网上的说法， 这会报错的，真的是这样吗？这里我们调用Canvas给我们提供的一个获得当前栈中 有多少个Layer的方法：**getSaveCount()**；然后在save()和restore()的前后都 加一个Log将栈中Layer的层数打印出来：

![img](https://www.runoob.com/wp-content/uploads/2015/11/34631753.jpg)





结果真是喜闻乐见，毕竟实践出真知，可能是Canvas改过吧，或者其他原因，这里 要看源码才知道了，时间关系，这里我们知道下restore的次数可以比save多就好了， 但是还是建议restore的次数还是少于save，以避免造成不必要的问题~ 至于进栈和出栈的流程我就不话了，笔者自己动笔画画，非常容易理解！



##### saveLayer()/restoreToCount()

其实这两个方法和save以及restore大同小异，只是在后者的基础上多了一些东东而已， 比如saveLayer()，有下面多个重载方法：

![img](https://www.runoob.com/wp-content/uploads/2015/11/47364992.jpg)

你可以理解为**save()**方法保存的是**整个Canvas**，而saveLayer()则可以==选择性的保存某个区域的状态==， 另外，我们看到餐宿和中有个:**int saveFlags**，这个是设置改保存那个对象的！可选值有：

| 标记                           | 说明                                                 |
| ------------------------------ | ---------------------------------------------------- |
| **ALL_SAVE_FLAG**              | 保存全部的状态                                       |
| **CLIP_SAVE_FLAG**             | 保存裁剪的某个区域的状态                             |
| **CLIP_TO_LAYER_SAVE_FLAG**    | 保存预先设置的范围里的状态                           |
| **FULL_COLOR_LAYER_SAVE_FLAG** | 保存彩色涂层                                         |
| **HAS_ALPHA_LAYER_SAVE_FLAG**  | 不透明图层保存                                       |
| **MATRIX_SAVE_FLAG**           | Matrix信息(translate，rotate，scale，skew)的状态保存 |

上述说明有点问题，笔者英语水平低，可能说错，如果有知道的，请务必指正提出，谢谢~

这里我们写个例子来验证下：我们选用**CLIP_TO_LAYER_SAVE_FLAG**模式来写个例子

**实现代码**：

```java
RectF bounds = new RectF(0, 0, 400, 400);
canvas.saveLayer(bounds, mPaint, Canvas.CLIP_TO_LAYER_SAVE_FLAG);
canvas.drawColor(getResources().getColor(R.color.moss_tide));
canvas.drawBitmap(bmp, 200, 200, mPaint);
canvas.restoreToCount(1);
canvas.drawBitmap(bmp, 300, 200, mPaint);
```

**运行结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/45193934.jpg)

关于saveLayer()后面用到再详解研究吧~这里先知道个大概~

接着到这个**restoreToCount(int)**，这个更简单，直接传入要恢复到的Layer层数， 直接就跳到对应的那一层，同时会将该层上面所有的Layer踢出栈，让该层 成为栈顶~！比起你写多个restore()方便快捷多了~

##### 资源

**代码下载**：[CanvasDemo.zip](http://static.runoob.com/download/CanvasDemo.zip) 可能你们要的是这个图吧！哈哈~

![img](https://www.runoob.com/wp-content/uploads/2015/11/88655895.jpg)



#### 剪切

> 本节继续带来Android绘图系列详解之Canvas API详解(Part 2)，今天要讲解的是Canvas 中的ClipXxx方法族！我们可以看到文档中给我们提供的Clip方法有三种类型： **clipPath**( )，**clipRect**( )，**clipRegion**( )；
>
> 通过Path，Rect，Region的不同组合，几乎可以支持任意形状的裁剪区域！
>
> **Path**：可以是开放或闭合的曲线，线构成的复杂的集合图形
>
> **Rect**：矩形区域
>
> **Region**：可以理解为区域组合，比如可以将两个区域相加，相减，并，疑惑等！
>
> **Region.Op**定义了Region支持的区域间运算种类！等下我们会讲到， 另外要说一点，我们平时理解的剪切可能是对已经存在的图形进行Clip，但是Android中对 Canvas进行Clip，是要在==画图前==进行的，如果==画图后==再对Canvas进行Clip的话将**不会影响 到已经画好的图形，记住Clip是针对Canvas而非图形**！ 嗯，不BB，直接开始本节内容！

**官方API文档**：[Canvas](http://androiddoc.qiniudn.com/reference/android/graphics/Canvas.html)

##### Region.Op

其实难点无非这个，Region代表着区域，表示的是Canvas图层上的某一块封闭区域！ 当然，有时间你可以自己慢慢去扣这个类，而我们一般关注的只是他的一个枚举值：**Op**

![img](https://www.runoob.com/wp-content/uploads/2015/11/97976861.jpg)

下面我们来看看个个枚举值所起的作用： 我们假设两个裁剪区域A和B，那么我们调用Region.Op对应的枚举值：

**DIFFERENCE**：A和B的**差集**范围，即A - B，只有在此范围内的绘制内容才会被显示；

**INTERSECT**：即A和B的**交集**范围，只有在此范围内的绘制内容才会被显示

**UNION**：即A和B的**并集**范围，即两者所包括的范围的绘制内容都会被显示；

**XOR**：A和B的**补集**范围，此例中即A除去B以外的范围，只有在此范围内的绘制内容才会被显示；

**REVERSE_DIFFERENCE**：B和A的**差集**范围，即B - A，只有在此范围内的绘制内容才会被显示；

**REPLACE**：不论A和B的集合状况，B的范围将全部进行显示，如果和A有交集，则将覆盖A的交集范围；

如果你学过集合，那么画个Venn(韦恩图)就一清二楚了，没学过？没事，我们写个例子来试试 对应的结果~！写个初始化画笔以及画矩形的方法:

```java
private void init() {
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStrokeWidth(6);
    mPaint.setColor(getResources().getColor(R.color.blush));
}

private void drawScene(Canvas canvas){
    canvas.drawRect(0, 0, 200, 200, mPaint);
}
```

###### **Op.DIFFERENCE**：

```jAVA
canvas.clipRect(10, 10, 110, 110);        //第一个
canvas.clipRect(50, 50, 150, 150, Region.Op.DIFFERENCE); //第二个
drawScene(canvas);
```

**结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/5388449.jpg)

先后在(10,10)以及(50,50)为起点，裁剪了两个100*100的矩形，得出的裁剪结果是：

**A和B的差集 = A - (A和B相交的部分)**

------

###### **Op.INTERSECT**：

```JAVA
canvas.clipRect(10, 10, 110, 110);        //第一个
canvas.clipRect(50, 50, 150, 150, Region.Op.INTERSECT); //第二个
drawScene(canvas);
```

**结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/27925675.jpg)

先后在(10,10)以及(50,50)为起点，裁剪了两个100*100的矩形，得出的裁剪结果是： **A和B的交集 =  A和B相交的部分**

------

###### **Op.UNION**：

```JAVA
canvas.clipRect(10, 10, 110, 110);        //第一个
canvas.clipRect(40, 40, 140, 140, Region.Op.UNION); //第二个
drawScene(canvas);
```

**结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/20979637.jpg)

先后在(10,10)以及(50,50)为起点，裁剪了两个100*100的矩形，得出的裁剪结果是： **A和B的并集 =  A的区域 + B的区域**

------

###### **Op.XOR**：

```JAVA
canvas.clipRect(10, 10, 110, 110);        //第一个
canvas.clipRect(50, 50, 150, 150, Region.Op.XOR); //第二个
drawScene(canvas);
```

**结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/19796189.jpg)

先后在(10,10)以及(50,50)为起点，裁剪了两个100*100的矩形，得出的裁剪结果是： **A和B的补集 =   A和B的合集 - A和B的交集**

------

###### **Op.REVERSE_DIFFERENCE**：

```JAVA
canvas.clipRect(10, 10, 110, 110);        //第一个
canvas.clipRect(50, 50, 150, 150, Region.Op.REVERSE_DIFFERENCE); //第二个
drawScene(canvas);
```

**结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/71471338.jpg)

先后在(10,10)以及(50,50)为起点，裁剪了两个100*100的矩形，得出的裁剪结果是： **B和A的差集 =   B - A和B的交集**

------

###### **Op.REPLACE**

```JAVA
canvas.clipRect(10, 10, 110, 110);        //第一个
canvas.clipRect(50, 50, 150, 150, Region.Op.REPLACE); //第二个
drawScene(canvas);
```

**结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/32825608.jpg)

先后在(10,10)以及(50,50)为起点，裁剪了两个100*100的矩形，得出的裁剪结果是： **不论A和B的集合状况，B的范围将全部进行显示，如果和A有交集，则将覆盖A的交集范围；**

###### 实例

子参考自：[Android 2D Graphics学习（二）、Canvas篇2、Canvas裁剪和Region、RegionIterator](http://blog.csdn.net/lonelyroamer/article/details/8349601)

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/94503617.jpg)

**关键部分代码 MyView.java：**

```JAVA
/**
 * Created by Jay on 2015/11/10 0010.
 */
public class MyView extends View{

    private Bitmap mBitmap = null;
    private int limitLength = 0;     //
    private int width;
    private int heigth;
    private static final int CLIP_HEIGHT = 50;

    private boolean status = HIDE;//显示还是隐藏的状态，最开始为HIDE
    private static final boolean SHOW = true;//显示图片
    private static final boolean HIDE = false;//隐藏图片

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_meizi);
        limitLength = width = mBitmap.getWidth();
        heigth = mBitmap.getHeight();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Region region = new Region();
        int i = 0;
        while (i * CLIP_HEIGHT <= heigth) {//计算clip的区域
            if (i % 2 == 0) {
                region.union(new Rect(0, i * CLIP_HEIGHT, limitLength, (i + 1) * CLIP_HEIGHT));
            } else {
                region.union(new Rect(width - limitLength, i * CLIP_HEIGHT, width, (i + 1)
                        * CLIP_HEIGHT));
            }
            i++;
        }
        canvas.clipRegion(region);
        canvas.drawBitmap(mBitmap, 0, 0, new Paint());
        if (status == HIDE) {//如果此时是隐藏
            limitLength -= 10;
            if(limitLength <= 0)
                status=SHOW;
        } else {//如果此时是显示
            limitLength += 5;
            if(limitLength >= width)
                status=HIDE;
        }
        invalidate();
    }
}
```

**实现分析**：

> 初始化的时候获得宽高，然后循环，可以理解把图片分割成一条条的线，循环条件是：i * 每条的高度 不大于高度，然后线又分两种情况，调用的是Region的union，其实就是结合方式为UNINO的剪切方式 而已，最后是对此时图片的是否显示做下判断，隐藏和显示的情况做不同的处理，最后调用invalidate() 重绘！还是蛮简单的，自己理解理解吧~

另外要说一点：Canvas的变换对clipRegion没有作用

------

##### clipRect方法

ct提供了七个重载方法：

clipRe![img](https://www.runoob.com/wp-content/uploads/2015/11/41170649.jpg)

**参数介绍如下**：

> **rect**：Rect对象，用于定义裁剪区的范围，Rect和RectF功能类似，精度和提供的方法不同而已
>
> **left**：矩形裁剪区的左边位置
>
> **top**：矩形裁剪区的上边位置
>
> **right**：矩形裁剪区的右边位置
>
> **bottom**：矩形裁剪区的下边位置
>
> **op**：裁剪区域的组合方式
>
> 上述四个值可以是浮点型或者整型

**使用示例**：

```java
mPaint = new Paint();
mPaint.setAntiAlias(true);
mPaint.setColor(Color.BLACK);
mPaint.setTextSize(60);

canvas.translate(300,300);
canvas.clipRect(100, 100, 300, 300);                //设置显示范围
canvas.drawColor(Color.WHITE);                      //白色背景
canvas.drawText("双11，继续吃我的狗粮...", 150, 300, mPaint); //绘制字符串
```

**运行结果**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/98791021.jpg)

从上面的例子，不知道你发现了没？ clipRect会受Canvas变换的影响，白色区域是不花的区域，所以clipRect裁剪的是画布， 而我们的绘制是在这个裁剪后的画布上进行的！超过该区域的不显示！

##### clipPath方法

相比起clipRect，clipPath就只有两个重载方法，使用方法非常简单，自己绘制一个Paht然后 传入即可！

![img](https://www.runoob.com/wp-content/uploads/2015/11/60897544.jpg)

**使用示例**：

这里复用我们以前在ImageView那里写的圆形ImageView的例子~

**实现代码**：

自定义ImageView：RoundImageView.java

```java
/**
 * Created by coder-pig on 2015/7/18 0018.
 */
public class RoundImageView extends ImageView {

    private Bitmap mBitmap;
    private Rect mRect = new Rect();
    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint = new Paint();
    private Path mPath=new Path();
    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //传入一个Bitmap对象
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }


    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);// 抗锯尺
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap == null)
        {
            return;
        }
        mRect.set(0,0,getWidth(),getHeight());
        canvas.save();
        canvas.setDrawFilter(pdf);
        mPath.addCircle(getWidth() / 2, getWidth() / 2, getHeight() / 2, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.REPLACE);
        canvas.drawBitmap(mBitmap, null, mRect, mPaint);
        canvas.restore();
    }
}
```

布局代码：**activity_main.xml**:

```xml
<com.jay.demo.imageviewdemo.RoundImageView
        android:id="@+id/img_round"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_margin="5px"/>
```

**MainActivity.java**:

```java
public class MainActivity extends AppCompatActivity {

    private RoundImageView img_round;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_round = (RoundImageView) findViewById(R.id.img_round);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.meinv);
        img_round.setBitmap(bitmap);
    }
}
```

**运行效果图**：

另外使用该方法制作的圆角ImageView会有锯齿明显，即使你为Paint，Canvas设置了 抗锯齿也没用~假如你要求高的，可以使用Xfermode-PorterDuff设置图像混排来实现， 基本没锯齿，可见：[Android基础入门教程——8.3.6 Paint API之—— Xfermode与PorterDuff详解(三)](https://www.runoob.com/w3cnote/android-tutorial-xfermode-porterduff3.html)

##### 资源

[CanvasDemo2.zip](http://static.runoob.com/download/CanvasDemo2.zip)

[XfermodeDemo1.zip](http://static.runoob.com/download/XfermodeDemo1.zip)

好的，本节讲解了下Canvas中剪切有个的三个方法：clipPath( )，clipRect( )， clipRegion( )，难点应该是在最后一个上，六种不同的Op组合方式，其实也不难，集合 的概念而已，放在开头，消化了就好，而clipPath( )，clipRect( )则没什么难点~ 对~![img](https://www.runoob.com/wp-content/uploads/2015/11/82311731.jpg)

#### Matrix和drawBitmapMesh

> 在Canvas的API文档中，我们看到这样一个方法：**drawBitmap**(Bitmap bitmap, **Matrix** matrix, Paint paint)
>
> 这个Matrix可是有大文章的，前面我们在学Paint的API中的ColorFilter中曾讲过ColorMatrix 颜色矩阵，一个4 * 5 的矩阵，我们可以通过修改矩阵值来修改色调，饱和度等！ 而今天讲的这个Matrix可以结合其他API来控制图形，组件的变换。比如Canvas就提供了上面的 这个drawBitmap来实现矩阵变换的效果！下面我们来慢慢研究这个东东~
>
> 官方API文档：[Matrix](http://androiddoc.qiniudn.com/reference/android/graphics/Matrix.html)

##### 常用的变换方法

> - **setTranslate**(float dx, float dy)：控制Matrix进行平移
> - **setRotate**(float degrees, float px, float py)：旋转，参数依次是:旋转角度，轴心(x,y)
> - **setScale**(float sx, float sy, float px, float py):缩放， 参数依次是：X，Y轴上的缩放比例；缩放的轴心
> - **setSkew**(float kx, float ky)：倾斜(扭曲)，参数依次是：X，Y轴上的缩放比例

其实和Canvas变换的方法基本一致，在为Matrix设置了上面的变换后，调用Canvas的 drawBitmap()方法调用矩阵就好~

##### 示例

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/32127450.jpg)

**代码实现**：

**MyView.java**：

```java
/**
 * Created by Jay on 2015/11/11 0011.
 */
public class MyView extends View {

    private Bitmap mBitmap;
    private Matrix matrix = new Matrix();
    private float sx = 0.0f;          //设置倾斜度
    private int width,height;         //位图宽高
    private float scale = 1.0f;       //缩放比例
    private int method = 0;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_meizi);
        width = mBitmap.getWidth();
        height = mBitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (method){
            case 0:
                matrix.reset();
                break;
            case 1:
                sx += 0.1;
                matrix.setSkew(sx,0);
                break;
            case 2:
                sx -= 0.1;
                matrix.setSkew(sx,0);
                break;
            case 3:
                if(scale < 2.0){
                    scale += 0.1;
                }
                matrix.setScale(scale,scale);
                break;
            case 4:
                if(scale > 0.5){
                    scale -= 0.1;
                }
                matrix.setScale(scale,scale);
                break;
        }
        //根据原始位图与Matrix创建新图片
        Bitmap bitmap = Bitmap.createBitmap(mBitmap,0,0,width,height,matrix,true);
        canvas.drawBitmap(bitmap,matrix,null);    //绘制新位图
    }

    public void setMethod(int i){
        method = i;
        postInvalidate();
    }
}
```

布局代码:**activity_main.xml**：

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:id="@+id/ly_bar"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:layout_alignParentBottom="true">

        <Button
            android:id="@+id/btn_reset"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="重置" />

        <Button
            android:id="@+id/btn_left"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="左倾" />

        <Button
            android:id="@+id/btn_right"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="右倾" />

        <Button
            android:id="@+id/btn_zoomin"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="放大" />

        <Button
            android:id="@+id/btn_zoomout"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="缩小" />
    </LinearLayout>


    <com.jay.canvasdemo3.MyView
        android:id="@+id/myView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@id/ly_bar" />

</RelativeLayout>
```

**MainActivity.java**：

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_reset;
    private Button btn_left;
    private Button btn_right;
    private Button btn_zoomin;
    private Button btn_zoomout;
    private MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_left = (Button) findViewById(R.id.btn_left);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_zoomin = (Button) findViewById(R.id.btn_zoomin);
        btn_zoomout = (Button) findViewById(R.id.btn_zoomout);
        myView = (MyView) findViewById(R.id.myView);


        btn_reset.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_zoomin.setOnClickListener(this);
        btn_zoomout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reset:
                myView.setMethod(0);
                break;
            case R.id.btn_left:
                myView.setMethod(1);
                break;
            case R.id.btn_right:
                myView.setMethod(2);
                break;
            case R.id.btn_zoomin:
                myView.setMethod(3);
                break;
            case R.id.btn_zoomout:
                myView.setMethod(4);
                break;
        }
    }
}
```

用法非常简单，就不解释了~

##### 扭曲图像

> 在API文档中还有这样一个方法： **drawBitmapMesh**(Bitmap bitmap, int meshWidth, int meshHeight, float[] verts, int vertOffset, int[] colors, int colorOffset, Paint paint)
>
> 参数依次是：
>
> **bitmap**：需要扭曲的原位图
>
> **meshWidth**/**meshHeight**：在横/纵向上把原位图划分为多少格
>
> **verts**：长度为(meshWidth+1)*(meshHeight+2)的数组，他记录了扭曲后的位图各顶点(网格线交点) 位置，虽然他是一个一维数组，但是实际上它记录的数据是形如(x0,y0)，(x1,y1)..(xN,Yn)格式的数据， 这些数组元素控制对bitmap位图的扭曲效果
>
> **vertOffset**：控制verts数组从第几个数组元素开始对bitmap进行扭曲(忽略verOffset之前数据 的扭曲效果)



![img](https://www.runoob.com/wp-content/uploads/2015/11/90747335.jpg)



**代码示例**：

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/11/84992968.jpg)

**代码实现**：

```java
/**
 * Created by Jay on 2015/11/11 0011.
 */
public class MyView extends View {

    //将水平和竖直方向上都划分为20格
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    private final int COUNT = (WIDTH + 1) * (HEIGHT + 1);  //记录该图片包含21*21个点
    private final float[] verts = new float[COUNT * 2];    //扭曲前21*21个点的坐标
    private final float[] orig = new float[COUNT * 2];    //扭曲后21*21个点的坐标
    private Bitmap mBitmap;
    private float bH,bW;


    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_wuliao);
        bH = mBitmap.getWidth();
        bW = mBitmap.getHeight();
        int index = 0;
        //初始化orig和verts数组。
        for (int y = 0; y <= HEIGHT; y++)
        {
            float fy = bH * y / HEIGHT;
            for (int x = 0; x <= WIDTH; x++)
            {
                float fx = bW * x / WIDTH;
                orig[index * 2 + 0] = verts[index * 2 + 0] = fx;
                orig[index * 2 + 1] = verts[index * 2 + 1] = fy;
                index += 1;
            }
        }
        //设置背景色
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, verts
                , 0, null, 0, null);
    }

    //工具方法，用于根据触摸事件的位置计算verts数组里各元素的值
    private void warp(float cx, float cy)
    {
        for (int i = 0; i < COUNT * 2; i += 2)
        {
            float dx = cx - orig[i + 0];
            float dy = cy - orig[i + 1];
            float dd = dx * dx + dy * dy;
            //计算每个座标点与当前点（cx、cy）之间的距离
            float d = (float)Math.sqrt(dd);
            //计算扭曲度，距离当前点（cx、cy）越远，扭曲度越小
            float pull = 80000 / ((float) (dd * d));
            //对verts数组（保存bitmap上21 * 21个点经过扭曲后的座标）重新赋值
            if (pull >= 1)
            {
                verts[i + 0] = cx;
                verts[i + 1] = cy;
            }
            else
            {
                //控制各顶点向触摸事件发生点偏移
                verts[i + 0] = orig[i + 0] + dx * pull;
                verts[i + 1] = orig[i + 1] + dy * pull;
            }
        }
        //通知View组件重绘
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //调用warp方法根据触摸屏事件的座标点来扭曲verts数组
        warp(event.getX(), event.getY());
        return true;
    }

}
```

##### 资源

[CanvasDemo3.zip](http://static.runoob.com/download/CanvasDemo3.zip)

[CanvasDemo4.zip](http://static.runoob.com/download/CanvasDemo4.zip)

本节内容大部分摘自——李刚《Android》疯狂讲义，可能稍微容易理解一点吧~ Matrix应该大部分的童鞋都能看懂，而![img](https://www.runoob.com/wp-content/uploads/2015/11/55309049.jpg)drawBitmapMesh扭曲图像则可能需要一点 时间消化消化，看不懂也没什么哈~嗯，本节就到这里，谢谢



















































### ==Path==

> 简单点说就是描点，连线~在创建好我们的Path路径后，可以调用Canvas的**drawPath**(path,paint) 将图形绘制出来~常用方法如下：
>
> - **addArc**(RectF oval, float startAngle, float sweepAngle：为路径添加一个多边形
> - **addCircle**(float x, float y, float radius, Path.Direction dir)：给path添加圆圈
> - **addOval**(RectF oval, Path.Direction dir)：添加椭圆形
> - **addRect**(RectF rect, Path.Direction dir)：添加一个区域
> - **addRoundRect**(RectF rect, float[] radii, Path.Direction dir)：添加一个圆角区域
> - **isEmpty**()：判断路径是否为空
> - **transform**(Matrix matrix)：应用矩阵变换
> - **transform**(Matrix matrix, Path dst)：应用矩阵变换并将结果放到新的路径中，即第二个参数。
>
> 更高级的效果可以使用PathEffect类！
>
> 几个To：
>
> - **moveTo**(float x, float y)：不会进行绘制，只用于移动移动画笔
> - **lineTo**(float x, float y)：用于直线绘制，默认从(0，0)开始绘制，用moveTo移动！ 比如 mPath.lineTo(300, 300); canvas.drawPath(mPath, mPaint);
> - **quadTo**(float x1, float y1, float x2, float y2)： 用于绘制圆滑曲线，即贝塞尔曲线，同样可以结合moveTo使用！ ![img](https://www.runoob.com/wp-content/uploads/2015/10/81389492.jpg)
> - **rCubicTo**(float x1, float y1, float x2, float y2, float x3, float y3) 同样是用来实现贝塞尔曲线的。 (x1,y1) 为控制点，(x2,y2)为控制点，(x3,y3) 为结束点。 Same as cubicTo, but the coordinates are considered relative to the current point on this contour.就是多一个控制点而已~ 绘制上述的曲线： mPath.moveTo(100, 500); mPath.cubicTo(100, 500, 300, 100, 600, 500); 如果不加上面的那个moveTo的话：则以(0,0)为起点，(100,500)和(300,100)为控制点绘制贝塞尔曲线 ![img](https://www.runoob.com/wp-content/uploads/2015/10/25024106.jpg)
> - **arcTo**(RectF oval, float startAngle, float sweepAngle)： 绘制弧线（实际是截取圆或椭圆的一部分）ovalRectF为椭圆的矩形，startAngle 为开始角度， sweepAngle 为结束角度。

### 例子

属性那么多，肯定要手把手的撸一下，才能加深我们的映像是吧~ 嘿嘿，画图要么在View上画，要么在SurfaceView上画，这里我们在View上画吧， 我们定义一个View类，然后再onDraw()里完成绘制工作！

```java
/**
 * Created by Jay on 2015/10/15 0015.
 */
public class MyView extends View{

    private Paint mPaint;


    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);          //抗锯齿
        mPaint.setColor(getResources().getColor(R.color.puple));//画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //画笔风格
        mPaint.setTextSize(36);             //绘制文字大小，单位px
        mPaint.setStrokeWidth(5);           //画笔粗细
    }
    
    //重写该方法，在这里绘图
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    
} 
```

然后布局那里设置下这个View就好，下述代码都写在onDrawable中~

设置画布颜色：

```java
canvas.drawColor(getResources().getColor(R.color.yellow));   //设置画布背景颜色
```

#### 绘制圆形

```java
canvas.drawCircle(200, 200, 100, mPaint);           //画实心圆
```



![img](https://www.runoob.com/wp-content/uploads/2015/10/30695094.jpg)

#### 绘制矩形

```java
canvas.drawRect(0, 0, 200, 100, mPaint);            //画矩形
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/27389474.jpg)

#### 绘制Bitmap

```java
canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), 0, 0, mPaint);
```



![img](https://www.runoob.com/wp-content/uploads/2015/10/22636697.jpg)

#### 绘制弧形区域

```java
canvas.drawArc(new RectF(0, 0, 100, 100),0,90,true,mPaint);  //绘制弧形区域
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/23352391.jpg)

假如true改为false：![img](https://www.runoob.com/wp-content/uploads/2015/10/33302227.jpg)

#### 绘制圆角矩形

```java
canvas.drawRoundRect(new RectF(10,10,210,110),15,15,mPaint); //画圆角矩形
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/88325799.jpg)

#### 绘制椭圆

```java
canvas.drawOval(new RectF(0,0,200,300),mPaint); //画椭圆
```



![img](https://www.runoob.com/wp-content/uploads/2015/10/23761580.jpg)



#### 绘制多边形

```java
Path path = new Path();
path.moveTo(10, 10); //移动到 坐标10,10
path.lineTo(100, 50);
path.lineTo(200,40);
path.lineTo(300, 20);
path.lineTo(200, 10);
path.lineTo(100, 70);
path.lineTo(50, 40);
path.close();
canvas.drawPath(path,mPaint);
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/94250807.jpg)

#### 绘制文字

```java
canvas.drawText("最喜欢看曹神日狗了~",50,50,mPaint);    //绘制文字
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/8419840.jpg)

你也可以沿着某条Path来绘制这些文字：

```java
Path path = new Path();
path.moveTo(50,50);
path.lineTo(100, 100);
path.lineTo(200, 200);
path.lineTo(300, 300);
path.close();
canvas.drawTextOnPath("最喜欢看曹神日狗了~", path, 50, 50, mPaint);    //绘制文字
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/18888589.jpg)



##### 绘制横线和竖线

```java
public void drawLine(float startX, float startY, float stopX, float stopY, @NonNull Paint paint) {
        throw new RuntimeException("Stub!");
    }

```

| [Canvas.drawLine](https://developer.android.com/reference/android/graphics/Canvas#drawLine(float, float, float, float, android.graphics.Paint))参数 | 描述        |
| ------------------------------------------------------------ | ----------- |
| startX                                                       | x轴起始坐标 |
| startY                                                       | y轴起始坐标 |
| stopX                                                        | x轴终止坐标 |
| stopY                                                        | y轴终止坐标 |
| paint                                                        | Paint       |

| Paint的set方法                                               | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| paint.[setStyle](https://developer.android.com/reference/android/graphics/Paint#setStyle(android.graphics.Paint.Style)) | 填充方式，默认FILL填充，还有STROKE描边，FILL_AND_STROKE填充并描边 |
| paint.[setColor](https://developer.android.com/reference/android/graphics/Paint#setColor(long)) | 设置颜色                                                     |
| paint.[setStrokeWidth](https://developer.android.com/reference/android/graphics/Paint#setStrokeWidth(float)) | 设置宽度                                                     |
| paint.setAntiAlias                                           | 防锯齿                                                       |





演示：

```java
  /**
     * 绘制x轴的中间竖线
     */
    private void drawCenterLineX(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        canvas.drawLine(getWidth()/2, 0, getWidth()/2, getHeight(), paint);
    }

    /**
     * 绘制y轴的中间横线
     */
    private void drawCenterLineY(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, paint);
    }

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210529185151140.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhbjEzNTA3MDAxNDcw,size_16,color_FFFFFF,t_70)



#####  绘制文本

```java
public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint) {
        throw new RuntimeException("Stub!");
    }
```

| [Canvas.drawText](https://developer.android.com/reference/android/graphics/Canvas#drawText(java.lang.String, float, float, android.graphics.Paint))参数 | 描述                                |
| ------------------------------------------------------------ | ----------------------------------- |
| text                                                         | 需要绘制的文本内容                  |
| x                                                            | 正在绘制的文本原点的 x 坐标         |
| y                                                            | 正在绘制的文本基线baseLine的 y 坐标 |
| paint                                                        | Paint                               |



| Paint的方法                                                  | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| paint.[setStyle](https://developer.android.com/reference/android/graphics/Paint#setStyle(android.graphics.Paint.Style)) | 填充方式，默认FILL填充，还有STROKE描边，FILL_AND_STROKE填充并描边 |
| paint.[setTextSize](https://developer.android.com/reference/android/graphics/Paint#setTextSize(float)) | 文本大小，以像素为单位                                       |
| paint.[setTextAlign](https://developer.android.com/reference/android/graphics/Paint#setTextAlign(android.graphics.Paint.Align)) | 设置对齐方式                                                 |
| paint.[getFontSpacing](https://developer.android.com/reference/android/graphics/Paint#getFontSpacing()) | 获取行间距                                                   |

```java
  @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCenterLineX(canvas);
        drawCenterLineY(canvas);

        String text = "绘制文本";

        // 绘制文本
        Paint paint = new Paint();
        paint.setTextSize(100); // 文字大小
        float x = 0;
        float baseLine = 100;
        canvas.drawText(text, x, baseLine, paint);

        // 绘制文本 - 文本对齐 - 左
        paint.setTextSize(100); // 文字大小
        paint.setTextAlign(Paint.Align.LEFT); // 默认值LEFT
        x = getWidth() / 2;
        baseLine = 100 + paint.getFontSpacing();
        canvas.drawText(text, x, baseLine, paint);

        // 绘制文本 - 文本对齐 - 中
        paint.setTextSize(100); // 文字大小
        paint.setTextAlign(Paint.Align.CENTER); // 默认值LEFT
        x = getWidth() / 2;
        baseLine = 100 + paint.getFontSpacing() * 2;
        canvas.drawText(text, x, baseLine, paint);

        // 绘制文本 - 文本对齐 - 右
        paint.setTextSize(100); // 文字大小
        paint.setTextAlign(Paint.Align.RIGHT); // 默认值LEFT
        x = getWidth() / 2;
        baseLine = 100 + paint.getFontSpacing() * 3;
        canvas.drawText(text, x, baseLine, paint);
    }

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210529190145130.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhbjEzNTA3MDAxNDcw,size_16,color_FFFFFF,t_70)



**上下左右居中**

| Paint的方法                                                  | 描述           |
| ------------------------------------------------------------ | -------------- |
| paint.[measureText](https://developer.android.com/reference/android/graphics/Paint#measureText(java.lang.String)) | 测量文本的宽度 |

| Paint.[FontMetrics](https://developer.android.com/reference/android/graphics/Paint.FontMetrics#ascent) | 描述                                             |
| ------------------------------------------------------------ | ------------------------------------------------ |
| ascent                                                       | 单行距文本基线上方的推荐距离。                   |
| descent                                                      | 单行距文本基线下方的推荐距离。                   |
| leading                                                      | 建议在文本行之间添加额外空间。                   |
| bottom                                                       | 给定文本大小下字体中最低字形基线下方的最大距离。 |
| top                                                          | 给定文本大小的字体中最高字形基线上方的最大距离。 |

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210529195719865.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhbjEzNTA3MDAxNDcw,size_16,color_FFFFFF,t_70)

```java
  // 绘制文本 - 上下左右居中
        paint.setTextSize(100);
        paint.setColor(Color.RED);
        paint.setTextAlign(Paint.Align.LEFT);
        
        x = getWidth()/2 - paint.measureText(text)/2;
        
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        baseLine = getHeight()/2 - (fontMetrics.descent + fontMetrics.ascent)/2;
        
        canvas.drawText(text, x, baseLine, paint);

```



![在这里插入图片描述](https://img-blog.csdnimg.cn/2021052919502081.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhbjEzNTA3MDAxNDcw,size_16,color_FFFFFF,t_70)

**图层**

```java
 @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.save();
        
        String text = "绘制文本";
        Paint paint = new Paint();
        paint.setTextSize(100); // 文字大小
        paint.setTextAlign(Paint.Align.LEFT); // 默认值LEFT
        paint.setColor(Color.RED);
        paint.setAntiAlias(true); // 防锯齿
        float x = 0;
        float baseLine = 100;
        canvas.drawText(text, x, baseLine, paint);

        canvas.restore();
    }

```

##### 资源：

  [TestDrawText](https://github.com/YanKuan-IT/TestDrawText)









#### 绘制自定义的图形

```java
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.translate(canvas.getWidth()/2, 200); //将位置移动画纸的坐标点:150,150
    canvas.drawCircle(0, 0, 100, mPaint); //画圆圈

    //使用path绘制路径文字
    canvas.save();
    canvas.translate(-75, -75);
    Path path = new Path();
    path.addArc(new RectF(0,0,150,150), -180, 180);
    Paint citePaint = new Paint(mPaint);
    citePaint.setTextSize(14);
    citePaint.setStrokeWidth(1);
    canvas.drawTextOnPath("绘制表盘~", path, 28, 0, citePaint);
    canvas.restore();

    Paint tmpPaint = new Paint(mPaint); //小刻度画笔对象
    tmpPaint.setStrokeWidth(1);

    float  y=100;
    int count = 60; //总刻度数

    for(int i=0 ; i <count ; i++){
        if(i%5 == 0){
            canvas.drawLine(0f, y, 0, y+12f, mPaint);
            canvas.drawText(String.valueOf(i/5+1), -4f, y+25f, tmpPaint);

        }else{
            canvas.drawLine(0f, y, 0f, y +5f, tmpPaint);
        }
        canvas.rotate(360/count,0f,0f); //旋转画纸
    }

    //绘制指针
    tmpPaint.setColor(Color.GRAY);
    tmpPaint.setStrokeWidth(4);
    canvas.drawCircle(0, 0, 7, tmpPaint);
    tmpPaint.setStyle(Paint.Style.FILL);
    tmpPaint.setColor(Color.YELLOW);
    canvas.drawCircle(0, 0, 5, tmpPaint);
    canvas.drawLine(0, 10, 0, -65, mPaint);
}
```

![img](https://www.runoob.com/wp-content/uploads/2015/10/96545086.jpg)

## 实战示例

### 画图板的实现

> 这个相信大家都不陌生，很多手机都会自带一个给用户涂鸦的画图板，这里我们就来写个简单的 例子，首先我们分析下，实现这个东东的一些逻辑：
>
> **Q1：这个画板放在哪里？**
>
> 答：View里，我们自定义一个View，在onDraw()里完成绘制，另外View还有个onTouchEvent的方法， 我们可以在获取用户的手势操作！
>
> **Q2.需要准备些什么？**
>
> 答：一只画笔(Paint)，一块画布(Canvas)，一个路径(Path)记录用户绘制路线； 另外划线的时候，每次都是从上次拖动时间的发生点到本次拖动时间的发生点！那么之前绘制的 就会丢失，为了保存之前绘制的内容，我们可以引入所谓的"**双缓冲**"技术： 其实就是每次不是直接绘制到Canvas上，而是先绘制到Bitmap上，等Bitmap上的绘制完了， 再一次性地绘制到View上而已！
>
> **Q3.具体的实现流程？**
>
> 答：初始化画笔，设置颜色等等一些参数；在View的onMeasure()方法中创建一个View大小的Bitmap， 同时创建一个Canvas；onTouchEvent中获得X,Y坐标，做绘制连线，最后invalidate()重绘，即调用 onDraw方法将bitmap的东东画到Canvas上！

好了，逻辑知道了，下面就上代码了：

**MyView.java**：

```java
/**
 * Created by Jay on 2015/10/15 0015.
 */
public class MyView extends View{

    private Paint mPaint;  //绘制线条的Path
    private Path mPath;      //记录用户绘制的Path
    private Canvas mCanvas;  //内存中创建的Canvas
    private Bitmap mBitmap;  //缓存绘制的内容

    private int mLastX;
    private int mLastY;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPath = new Path();
        mPaint = new Paint();   //初始化画笔
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND); //结合处为圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 设置转弯处为圆角
        mPaint.setStrokeWidth(20);   // 设置画笔宽度
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 初始化bitmap,Canvas
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    //重写该方法，在这里绘图
    @Override
    protected void onDraw(Canvas canvas) {
        drawPath();
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    //绘制线条
    private void drawPath(){
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);
                if (dx > 3 || dy > 3)
                    mPath.lineTo(x, y);
                mLastX = x;
                mLastY = y;
                break;
        }

        invalidate();
        return true;
    }
}
```

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/55635114.jpg)

你可以根据自己的需求进行扩展，比如加上修改画笔大小，修改画笔颜色，保存自己画的图等！ 发散思维，自己动手~



### 脱衣服例子

> 核心思路是： 利用帧布局，前后两个ImageView，前面的显示未擦掉衣服的情况，后面的显示擦掉衣服后的情况！
>
> 为两个ImageView设置美女图片后，接着为前面的ImageView设置OnTouchListener！在这里对手指 触碰点附近的20*20个像素点，设置为透明！

![img](https://www.runoob.com/wp-content/uploads/2015/10/2.gif)



**代码实现**：

**Step 1**：第一个选妹子的Activity相关的编写，首先是界面，一个ImageView，Button和Gallery！

**activity_main.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <ImageView
        android:id="@+id/img_choose"
        android:layout_width="320dp"
        android:layout_height="320dp" />

    <Button
        android:id="@+id/btn_choose"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="脱光她!" />

    <Gallery
        android:id="@+id/gay_choose"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="25dp"
        android:spacing="1pt"
        android:unselectedAlpha="0.6" />

</LinearLayout>
```

接着是我们Gallery的Adapter类，这里我们重写下BaseAdapter，而里面就显示一个图片比较简单， 就不另外写一个布局了！

**MeiziAdapter.java**:

```java
/**
 * Created by Jay on 2015/10/16 0016.
 */
public class MeiziAdapter extends BaseAdapter{

    private Context mContext;
    private int[] mData;

    public MeiziAdapter() {
    }

    public MeiziAdapter(Context mContext,int[] mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public Object getItem(int position) {
        return mData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgMezi = new ImageView(mContext);
        imgMezi.setImageResource(mData[position]);         //创建一个ImageView
        imgMezi.setScaleType(ImageView.ScaleType.FIT_XY);      //设置imgView的缩放类型
        imgMezi.setLayoutParams(new Gallery.LayoutParams(250, 250));    //为imgView设置布局参数
        TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);
        imgMezi.setBackgroundResource(typedArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0));
        return imgMezi;
    }
}
```

最后到我们的Activity，也很简单，无非是为gallery设置onSelected事件，点击按钮后把，当前选中的 Position传递给下一个页面！

**MainActivity.java**：

```java
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private Context mContext;
    private ImageView img_choose;
    private Button btn_choose;
    private Gallery gay_choose;
    private int index = 0;
    private MeiziAdapter mAdapter = null;
    private int[] imageIds = new int[]
            {
                    R.mipmap.pre1, R.mipmap.pre2, R.mipmap.pre3, R.mipmap.pre4,
                    R.mipmap.pre5, R.mipmap.pre6, R.mipmap.pre7, R.mipmap.pre8,
                    R.mipmap.pre9, R.mipmap.pre10, R.mipmap.pre11, R.mipmap.pre12,
                    R.mipmap.pre13, R.mipmap.pre14, R.mipmap.pre15, R.mipmap.pre16,
                    R.mipmap.pre17, R.mipmap.pre18, R.mipmap.pre19, R.mipmap.pre20,
                    R.mipmap.pre21
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        bindViews();
    }

    private void bindViews() {
        img_choose = (ImageView) findViewById(R.id.img_choose);
        btn_choose = (Button) findViewById(R.id.btn_choose);
        gay_choose = (Gallery) findViewById(R.id.gay_choose);


        mAdapter = new MeiziAdapter(mContext, imageIds);
        gay_choose.setAdapter(mAdapter);
        gay_choose.setOnItemSelectedListener(this);
        btn_choose.setOnClickListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        img_choose.setImageResource(imageIds[position]);
        index = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        Intent it = new Intent(mContext,CaClothes.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("num", Integer.toString(index));
        it.putExtras(bundle);
        startActivity(it);
    }
}
```

接着是我们擦掉妹子衣服的页面了，布局比较简单，FrameLayout + 前后两个ImageView：

**activity_caclothes.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/img_after"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <ImageView
        android:id="@+id/img_before"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</FrameLayout>
```

接着到就到Java部分的代码了：

**CaClothes.java**：

```java
/**
 * Created by Jay on 2015/10/16 0016.
 */
public class CaClothes extends AppCompatActivity implements View.OnTouchListener {

    private ImageView img_after;
    private ImageView img_before;
    private Bitmap alterBitmap;
    private Canvas canvas;
    private Paint paint;
    private Bitmap after;
    private Bitmap before;
    private int position;

    int[] imageIds1 = new int[]
            {
                    R.mipmap.pre1, R.mipmap.pre2, R.mipmap.pre3, R.mipmap.pre4,
                    R.mipmap.pre5, R.mipmap.pre6, R.mipmap.pre7, R.mipmap.pre8,
                    R.mipmap.pre9, R.mipmap.pre10, R.mipmap.pre11, R.mipmap.pre12,
                    R.mipmap.pre13, R.mipmap.pre14, R.mipmap.pre15, R.mipmap.pre16,
                    R.mipmap.pre17, R.mipmap.pre18, R.mipmap.pre19, R.mipmap.pre20,
                    R.mipmap.pre21
            };


    int[] imageIds2 = new int[]
            {
                    R.mipmap.after1, R.mipmap.after2, R.mipmap.after3, R.mipmap.after4,
                    R.mipmap.after5, R.mipmap.after6, R.mipmap.after7, R.mipmap.after8,
                    R.mipmap.after9, R.mipmap.after10, R.mipmap.after11, R.mipmap.after12,
                    R.mipmap.after13, R.mipmap.after14, R.mipmap.after15, R.mipmap.after16,
                    R.mipmap.after17, R.mipmap.after18, R.mipmap.after19, R.mipmap.after20,
                    R.mipmap.after21
            };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caclothes);

        Bundle bd = getIntent().getExtras();
        position = Integer.parseInt(bd.getString("num"));
        bindViews();

    }

    private void bindViews() {
        img_after = (ImageView) findViewById(R.id.img_after);
        img_before = (ImageView) findViewById(R.id.img_before);


        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        after = BitmapFactory.decodeResource(getResources(), imageIds2[position], opts);
        before = BitmapFactory.decodeResource(getResources(), imageIds1[position], opts);
        //定义出来的是只读图片

        alterBitmap = Bitmap.createBitmap(before.getWidth(), before.getHeight(), Bitmap.Config.ARGB_4444);
        canvas = new Canvas(alterBitmap);
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        canvas.drawBitmap(before, new Matrix(), paint);
        img_after.setImageBitmap(after);
        img_before.setImageBitmap(before);
        img_before.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int newX = (int) event.getX();
                int newY = (int) event.getY();
                //setPixel方法是将某一个像素点设置成一个颜色，而这里我们把他设置成透明
                //另外通过嵌套for循环将手指触摸区域的20*20个像素点设置为透明
                for (int i = -20; i < 20; i++) {
                    for (int j = -20; j < 20; j++) {
                        if (i + newX >= 0 && j + newY >= 0 && i + newX < before.getWidth() && j + newY < before.getHeight())
                            alterBitmap.setPixel(i + newX, j + newY, Color.TRANSPARENT);
                    }
                }
                img_before.setImageBitmap(alterBitmap);
                break;
        }
        return true;
    }
}
```

**效果**

![img](https://www.runoob.com/wp-content/uploads/2015/10/2.gif)

代码也不算苦涩难懂，还是比较简单的哈，嗯，效果图看看就好，别做那么多右手螺旋定则哈....![img](https://www.runoob.com/wp-content/uploads/2015/10/30359706.jpg)

#### 资源

[DrawDemo1.zip](http://static.runoob.com/download/DrawDemo1.zip) 项目比较大，20多M，图片资源比较多，你懂的~

------

# ==Drawable==

从本节开始我们来学习Android中绘图与动画中的一些基础知识，为我们进阶部分的自定义 打下基础！而第一节我们来扣下Android中的Drawable！Android中给我们提供了多达13种的 Drawable，本节我们就来一个个撸一遍！

![img](https://www.runoob.com/wp-content/uploads/2015/10/57840489.jpg)

## 注意事项

> - Drawable分为两种： 一种是我们**普通的图片资源**，在Android Studio中我们一般放到res/mipmap目录下， 和以前的Eclipse不一样哦！另外我们如果把工程切换成Android项目模式，我们直接 往mipmap目录下丢图片即可，AS会自动分hdpi，xhdpi...！ 另一种是我们编写的**XML形式的Drawable资源**，我们一般把他们放到res/drawable目录 下，比如最常见的按钮点击背景切换的Selctor！
> - 在XML我们直接通过@mipmap或者@drawable设置Drawable即可 比如: android:background = "@mipmap/iv_icon_zhu"   /  "@drawable/btn_back_selctor" 而在Java代码中我们可以通过Resource的getDrawable(R.mipmap.xxx)可以获得drawable资源 如果是为某个控件设置背景，比如ImageView，我们可以直接调用控件.getDrawale()同样 可以获得drawable对象！
> - Android中drawable中的资源名称有约束，必须是：**[a-z0-9_.]**（即：只能是字母数字及*和.）， 而且不能以数字开头，否则编译会报错： Invalid file name: must contain only [a-z0-9*.]！ 小写啊！！！！小写！！！小写！——重要事情说三遍~

## ColorDrawable

最简单的一种Drawable，当我们将ColorDrawable绘制到Canvas(画布)上的时候, 会使用一种固定的颜色来填充Paint,然后在画布上绘制出一片单色区域!

### Java

```java
ColorDrawable drawable = new ColorDrawable(0xffff2200);  
txtShow.setBackground(drawable);  
```

### xml

```java
<?xml version="1.0" encoding="utf-8"?>  
<color  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    android:color="#FF0000"/>  
```

当然上面这些用法,其实用得不多,更多的时候我们是在res/values目录下创建一个color.xml 文件,然后把要用到的颜色值写到里面,需要的时候通过@color获得相应的值，比如：

==建立一个color.xml文件==

比如：

```xml
<?xml version="1.0" encoding="utf-8"?>  
<resources>  
    <color name="material_grey_100">#fff5f5f5</color>
    <color name="material_grey_300">#ffe0e0e0</color>
    <color name="material_grey_50">#fffafafa</color>
    <color name="material_grey_600">#ff757575</color>
    <color name="material_grey_800">#ff424242</color>
    <color name="material_grey_850">#ff303030</color>
    <color name="material_grey_900">#ff212121</color>
</resources>
```

然后如果是在xml文件中话我们可以通过@color/xxx获得对应的color值 如果是在Java中:

```java
int mycolor = getResources().getColor(R.color.mycolor);    
btn.setBackgroundColor(mycolor);  
```

ps:另外有一点要注意,如果我们在Java中直接定义颜色值的话,要加上0x,而且不能把透明度漏掉:

```java
int mycolor = 0xff123456;    
btn.setBackgroundColor(mycolor); 
```

### 系统

比如:BLACK(黑色),BLUE(蓝色),CYAN(青色),GRAY(灰色),GREEN(绿色),RED(红色),WRITE(白色),YELLOW(黄色)! 用法： **btn.setBackgroundColor(Color.BLUE);** 也可以获得系统颜色再设置：

```java
int getcolor = Resources.getSystem().getColor(android.R.color.holo_green_light);  
btn.setBackgroundColor(getcolor);  
```

xml中使用:**android:background="@android:color/black"**

### 静态argb

> Android使用一个int类型的数据表示颜色值,通常是十六进制,即0x开头， 颜色值的定义是由透明度alpha和RGB(红绿蓝)三原色来定义的,以"#"开始,后面依次为: 
>  **透明度-红-绿-蓝**;eg:#RGB    #ARGB  #RRGGBB  #AARRGGBB 
>  每个要素都由一个字节(8 bit)来表示,所以取值范围为0~255,在xml中设置颜色可以忽略透明度, 但是如果你是在Java代码中的话就需要明确指出透明度的值了,省略的话表示完全透明,这个时候 就没有效果了哦~比如:0xFF0000虽然表示红色,但是如果直接这样写,什么的没有,而应该这样写: 0xFFFF0000,记Java代码设置颜色值,需要在前面添加上透明度~ 示例:(参数依次为:透明度,红色值,绿色值,蓝色值) **txtShow.setBackgroundColor(Color.argb(0xff, 0x00, 0x00, 0x00));**

## NiewPatchDrawable

> 就是.9图咯，在前面我们[1.6 .9(九妹)图片怎么玩](http://www.runoob.com/w3cnote/android-tutorial-9-image.html)已经详细 的给大家讲解了一下如何制作.9图片了！Android FrameWork在显示点九图时使用了高效的 图形优化算法,我们不需要特殊的处理，就可以实现图片拉伸的自适应~ 另外在使用AS的时候要注意以下几点：
>
> - 1.点9图不能放在mipmap目录下，而需要放在drawable目录下！
> - 2.AS中的.9图，必须要有黑线，不然编译都不会通过，今早我的阿君表哥在群里说 他司的美工给了他一个没有黑线的.9图，说使用某软件制作出来的，然后在Eclipse上 是可以用的，没错是没黑线的.9，卧槽，然而我换到AS上，直接编译就不通过了！ 感觉是AS识别.9图的其中标准是需要有黑店或者黑线！另外表哥给出的一个去掉黑线的： [9patch(.9)怎么去掉自己画上的黑点/黑线](https://www.runoob.com/w3cnote/9patch-9-remove-black-line.html) 具体我没试，有兴趣可以自己试试，但是黑线真的那么碍眼么...我没强迫症不觉得！ 另外还有一点就是解压别人apk，拿.9素材的时候发现并没有黑线，同样也会报错！ 想要拿出有黑线的.9素材的话，需要反编译apk而非直接解压！！！反编译前面也 介绍过了，这里就不详述了！

接着介绍两个没什么卵用的东东：

### xml

```java
<!--pic9.xml-->  
<!--参数依次为:引用的.9图片,是否对位图进行抖动处理-->  
<?xml version="1.0" encoding="utf-8"?>  
<nine-patch  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    android:src="@drawable/dule_pic"  
    android:dither="true"/>  
```

**使用Bitmap包装.9图片**:

```java
<!--pic9.xml-->  
<!--参数依次为:引用的.9图片,是否对位图进行抖动处理-->  
<?xml version="1.0" encoding="utf-8"?>  
<bitmap  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    android:src="@drawable/dule_pic"  
    android:dither="true"/>
```

## ShapeDrawable

> 形状的Drawable咯,定义基本的几何图形,如(矩形,圆形,线条等),根元素是<shape../> 节点比较多，相关的节点如下：
>
> - ① <**shape**>:
> - ~ **visible**:设置是否可见
> - ~ **shape**:形状,可选:rectangle(矩形,包括正方形),oval(椭圆,包括圆),line(线段),ring(环形)
> - ~ **innerRadiusRatio**:当shape为ring才有效,表示环内半径所占半径的比率,如果设置了innerRadius, 他会被忽略
> - ~ **innerRadius**:当shape为ring才有效,表示环的内半径的尺寸
> - ~ **thicknessRatio**:当shape为ring才有效,表环厚度占半径的比率
> - ~ **thickness**:当shape为ring才有效,表示环的厚度,即外半径与内半径的差
> - ~ **useLevel**:当shape为ring才有效,表示是否允许根据level来显示环的一部分
> - ②<**size**>:
> - ~ **width**:图形形状宽度
> - ~ **height**:图形形状高度
> - ③<**gradient**>：后面GradientDrawable再讲~
> - ④<**solid**>
> - ~ **color**:背景填充色,设置solid后会覆盖gradient设置的所有效果!!!!!!
> - ⑤<**stroke**>
> - ~ **width**:边框的宽度
> - ~ **color**:边框的颜色
> - ~ **dashWidth**:边框虚线段的长度
> - ~ **dashGap**:边框的虚线段的间距
> - ⑥<**conner**>
> - ~ **radius**:圆角半径,适用于上下左右四个角
> - ~ **topLeftRadius**,**topRightRadius**,**BottomLeftRadius**,**tBottomRightRadius**: 依次是左上,右上,左下,右下的圆角值,按自己需要设置!
> - ⑦<**padding**>
> - left,top,right,bottm:依次是左上右下方向上的边距!

**使用示例**： [2.3.1 TextView(文本框)详解](http://www.runoob.com/w3cnote/android-tutorial-textview.html)![img](https://www.runoob.com/wp-content/uploads/2015/10/36413391.jpg)

## GradientDrawable

> 一个具有渐变区域的Drawable，可以实现线性渐变,发散渐变和平铺渐变效果 核心节点：<**gradient**/>，有如下可选属性：
>
> - **startColor**:渐变的起始颜色
> - **centerColor**:渐变的中间颜色
> - **endColor**:渐变的结束颜色
> - **type**:渐变类型,可选(**linear**,**radial**,**sweep**), **线性渐变**(可设置渐变角度),发散渐变(中间向四周发散),平铺渐变
> - **centerX**:渐变中间亚瑟的x坐标,取值范围为:0~1
> - **centerY**:渐变中间颜色的Y坐标,取值范围为:0~1
> - **angle**:只有linear类型的渐变才有效,表示渐变角度,必须为45的倍数哦
> - **gradientRadius**:只有radial和sweep类型的渐变才有效,radial必须设置,表示渐变效果的半径
> - **useLevel**:判断是否根据level绘制渐变效果

**代码示例**：(三种渐变效果的演示)：

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/10/13354775.jpg)

先在drawable下创建三个渐变xml文件：

**(线性渐变)gradient_linear.xml**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval" >
    <gradient
        android:angle="90"
        android:centerColor="#FFEB82"
        android:endColor="#35B2DE"
        android:startColor="#DEACAB" />

    <stroke
        android:dashGap="5dip"
        android:dashWidth="4dip"
        android:width="3dip"
        android:color="#fff" />
</shape>
```

**(发散渐变)gradient_radial.xml**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:innerRadius="0dip"
    android:shape="ring"
    android:thickness="70dip"
    android:useLevel="false" >

    <gradient
        android:centerColor="#FFEB82"
        android:endColor="#35B2DE"
        android:gradientRadius="70"
        android:startColor="#DEACAB"
        android:type="radial"
        android:useLevel="false" />

</shape> 
```

**(平铺渐变)gradient_sweep.xml**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:innerRadiusRatio="8"
    android:shape="ring"
    android:thicknessRatio="3"
    android:useLevel="false" >

    <gradient
        android:centerColor="#FFEB82"
        android:endColor="#35B2DE"
        android:startColor="#DEACAB"
        android:type="sweep"
        android:useLevel="false" />

</shape> 
```

调用三个drawable的**activity_main.xml**:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/txtShow1"
        android:layout_width="200dp"
        android:layout_height="100dp"
        android:background="@drawable/gradient_linear" />

    <TextView
        android:id="@+id/txtShow2"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:background="@drawable/gradient_radial" />

    <TextView
        android:id="@+id/txtShow3"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@drawable/gradient_sweep" />

</LinearLayout>  
```

好的，就是那么简单~当然，如果想绘制更加复杂的图形的话,只用xml文件不远远不足的, 更复杂的效果则需要通过Java代码来完成,下面演示的是摘自网上的一个源码:

运行效果图：

**实现代码**：

**MainActivity.java**：

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }

    private static class SampleView extends View {
        private ShapeDrawable[] mDrawables;

        private static Shader makeSweep() {
            return new SweepGradient(150, 25,
                    new int[] { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFF0000 },
                    null);
        }

        private static Shader makeLinear() {
            return new LinearGradient(0, 0, 50, 50,
                    new int[] { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF },
                    null, Shader.TileMode.MIRROR);
        }

        private static Shader makeTiling() {
            int[] pixels = new int[] { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0};
            Bitmap bm = Bitmap.createBitmap(pixels, 2, 2,
                    Bitmap.Config.ARGB_8888);

            return new BitmapShader(bm, Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
        }

        private static class MyShapeDrawable extends ShapeDrawable {
            private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            public MyShapeDrawable(Shape s) {
                super(s);
                mStrokePaint.setStyle(Paint.Style.STROKE);
            }

            public Paint getStrokePaint() {
                return mStrokePaint;
            }

            @Override protected void onDraw(Shape s, Canvas c, Paint p) {
                s.draw(c, p);
                s.draw(c, mStrokePaint);
            }
        }

        public SampleView(Context context) {
            super(context);
            setFocusable(true);

            float[] outerR = new float[] { 12, 12, 12, 12, 0, 0, 0, 0 };
            RectF inset = new RectF(6, 6, 6, 6);
            float[] innerR = new float[] { 12, 12, 0, 0, 12, 12, 0, 0 };

            Path path = new Path();
            path.moveTo(50, 0);
            path.lineTo(0, 50);
            path.lineTo(50, 100);
            path.lineTo(100, 50);
            path.close();

            mDrawables = new ShapeDrawable[7];
            mDrawables[0] = new ShapeDrawable(new RectShape());
            mDrawables[1] = new ShapeDrawable(new OvalShape());
            mDrawables[2] = new ShapeDrawable(new RoundRectShape(outerR, null,
                    null));
            mDrawables[3] = new ShapeDrawable(new RoundRectShape(outerR, inset,
                    null));
            mDrawables[4] = new ShapeDrawable(new RoundRectShape(outerR, inset,
                    innerR));
            mDrawables[5] = new ShapeDrawable(new PathShape(path, 100, 100));
            mDrawables[6] = new MyShapeDrawable(new ArcShape(45, -270));

            mDrawables[0].getPaint().setColor(0xFFFF0000);
            mDrawables[1].getPaint().setColor(0xFF00FF00);
            mDrawables[2].getPaint().setColor(0xFF0000FF);
            mDrawables[3].getPaint().setShader(makeSweep());
            mDrawables[4].getPaint().setShader(makeLinear());
            mDrawables[5].getPaint().setShader(makeTiling());
            mDrawables[6].getPaint().setColor(0x88FF8844);

            PathEffect pe = new DiscretePathEffect(10, 4);
            PathEffect pe2 = new CornerPathEffect(4);
            mDrawables[3].getPaint().setPathEffect(
                    new ComposePathEffect(pe2, pe));

            MyShapeDrawable msd = (MyShapeDrawable)mDrawables[6];
            msd.getStrokePaint().setStrokeWidth(4);
        }

        @Override protected void onDraw(Canvas canvas) {

            int x = 10;
            int y = 10;
            int width = 400;
            int height = 100;

            for (Drawable dr : mDrawables) {
                dr.setBounds(x, y, x + width, y + height);
                dr.draw(canvas);
                y += height + 5;
            }
        }
    }
}
```



代码使用了ShapeDrawable和PathEffect,前者是对普通图形的包装;包括: ArcShape,OvalShape,PathShape,RectShape,RoundRectShape!
 而PathEffect则是路径特效,包括:CornerPathEffect,DashPathEffect和DiscretePathEffect 可以制作复杂的图形边框...
 关于这个GradoemtDrawable渐变就讲到这里,如果你对最后面这个玩意有兴趣的话,可以到: [appium/android-apidemos](https://github.com/appium/android-apidemos/tree/master/src/io/appium/android/apis/graphics)

![img](https://www.runoob.com/wp-content/uploads/2015/10/57914902.jpg)

## BitmapDrawable

> 对Bitmap的一种封装,可以设置它包装的bitmap在BitmapDrawable区域中的绘制方式,有: 平铺填充,拉伸填或保持图片原始大小!以<**bitmap**>为根节点! 可选属性如下：
>
> - **src**:图片资源~
> - **antialias**:是否支持抗锯齿
> - **filter**:是否支持位图过滤,支持的话可以是图批判显示时比较光滑
> - **dither**:是否对位图进行抖动处理
> - **gravity**:若位图比容器小,可以设置位图在容器中的相对位置
> - **tileMode**:指定图片平铺填充容器的模式,设置这个的话,gravity属性会被忽略,有以下可选值:             **disabled**(整个图案拉伸平铺),**clamp**(原图大小),             **repeat**(平铺),**mirror**(镜像平铺)
>
> 对应的效果图：
>
> ![img](https://www.runoob.com/wp-content/uploads/2015/10/98559776.jpg)

**①XML定义BitmapDrawable**:

```xml
<?xml version="1.0" encoding="utf-8"?>  
<bitmap xmlns:android="http://schemas.android.com/apk/res/android"  
    android:dither="true"  
    android:src="@drawable/ic_launcher"  
    android:tileMode="mirror" />
```

**②实现相同效果的Java代码**:

```java
BitmapDrawable bitDrawable = new BitmapDrawable(bitmap);  
bitDrawable.setDither(true);  
bitDrawable.setTileModeXY(TileMode.MIRROR,TileMode.MIRROR);  
```

## InsetDrawable

表示把一个Drawable嵌入到另外一个Drawable的内部，并且在内部留一些间距, 类似与Drawable的padding属性,但**padding**表示的是**Drawable的内容与Drawable本身的边距**! 而**InsetDrawable**表示的是**两个Drawable与容器之间的边距**,当控件需要的**背景比实际的边框 小的时候**,比较适合使用InsetDrawable,比如使用这个可以解决我们自定义Dialog与屏幕之间 的一个间距问题,相信做过的朋友都知道,即使我们设置了layout_margin的话也是没用的,这个 时候就可以用到这个InsetDrawable了!只需为InsetDrawable设置一个insetXxx设置不同 方向的边距,然后为设置为Dialog的背景即可！

相关属性如下：

> - 1.**drawable**:引用的Drawable,如果为空,必须有一个Drawable类型的子节点!
> - 2.**visible**:设置Drawable是否额空间
> - 3.**insetLeft**,**insetRight**,**insetTop**,**insetBottm**:设置左右上下的边距

**①XML中使用**:

```java
<?xml version="1.0" encoding="utf-8"?>  
<inset xmlns:android="http://schemas.android.com/apk/res/android"  
    android:drawable="@drawable/test1"  
    android:insetBottom="10dp"  
    android:insetLeft="10dp"  
    android:insetRight="10dp"  
    android:insetTop="10dp" /> 
```

**在Java代码中使用**：

```java
InsetDrawable insetDrawable = new InsetDrawable(getResources()  
        .getDrawable(R.drawable.test1), 10, 10, 10, 10); 
```

**使用效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/10/22768448.jpg)

## ClipDrawable

Clip可以译为剪的意思,我们可以把ClipDrawable理解为从位图上剪下一个部分; Android中的进度条就是使用ClipDrawable来实现的,他根据设置level的值来决定剪切 区域的大小,根节点是<**clip**>

**相关属性如下**：

> - **clipOrietntion**:设置剪切的方向,可以设置水平和竖直2个方向
> - **gravity**:从那个位置开始裁剪
> - **drawable**:引用的drawable资源,为空的话需要有一个Drawable类型的子节点 ps:这个Drawable类型的子节点:就是在<clip里>加上这样的语句: 这样...

**使用示例**：

核心：通过代码修改ClipDrawable的level的值！Level的值是0~10000！

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/10/44267367.jpg)

**代码实现**：

**①定义一个ClipDrawable的资源xml**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<clip xmlns:android="http://schemas.android.com/apk/res/android"
    android:clipOrientation="horizontal"
    android:drawable="@mipmap/ic_bg_meizi"
    android:gravity="left" /> 
```

**②在activity_main主布局文件中设置一个ImageView,将src设置为clipDrawable!** **记住是src哦,如果你写成了blackground的话可是会报空指针的哦!!!!**

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <ImageView
        android:id="@+id/img_show"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/clip_bg" />

</LinearLayout>  
```

**③MainActivity.java通过setLevel设置截取区域大小**:

```
public class MainActivity extends AppCompatActivity {

    private ImageView img_show;
    private ClipDrawable cd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                cd.setLevel(cd.getLevel() + 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_show = (ImageView) findViewById(R.id.img_show);
        // 核心实现代码
        cd = (ClipDrawable) img_show.getDrawable();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
                if (cd.getLevel() >= 10000) {
                    timer.cancel();
                }
            }
        }, 0, 300);
    }
}
```

好的，有点意思，妹子图别问我拿，百度

## RotateDrawable

> 用来对Drawable进行旋转,也是通过setLevel来控制旋转的,最大值也是:10000

**相关属性如下**：

> - **fromDegrees**:起始的角度,,对应最低的level值,默认为0
> - **toDegrees**:结束角度,对应最高的level值,默认360
> - **pivotX**:设置参照点的x坐标,取值为0~1,默认是50%,即0.5
> - **pivotY**:设置参照点的Y坐标,取值为0~1,默认是50%,即0.5 ps:如果出现旋转图片显示不完全的话可以修改上述两个值解决!
> - **drawable**:设置位图资源
> - **visible**:设置drawable是否可见!

**角度图如下**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/70168328.jpg)

**使用示例**：

**运行效果图**：![img](https://www.runoob.com/wp-content/uploads/2015/10/35137047.jpg)



**代码实现**：

在第三点的clipDrawable上做一点点修改即可!

**①定义一个rotateDrawable资源文件**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@mipmap/ic_launcher"
    android:fromDegrees="-180"
    android:pivotX="50%"
    android:pivotY="50%" />  
```

**②activity_main.xml中修改下src指向上述drawable即可,MainActivity只需要把ClipDrawable** **改成rotateDrawable即可!**

```java
public class MainActivity extends AppCompatActivity {

    private ImageView img_show;
    private RotateDrawable cd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (cd.getLevel() >= 10000)
                    Toast.makeText(MainActivity.this, "转完了~",
                            Toast.LENGTH_LONG).show();
                cd.setLevel(cd.getLevel() + 400);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_show = (ImageView) findViewById(R.id.img_show);
        // 核心实现代码
        cd = (RotateDrawable) img_show.getDrawable();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
                if (cd.getLevel() >= 10000) {
                    timer.cancel();
                }
            }
        }, 0, 100);
    }
}
```

## AnimationDrawable

> 本节最后一个Drawable，AnimationDrawable是用来实现Android中帧动画的,就是把一系列的 Drawable，按照一定得顺序一帧帧地播放；Android中动画比较丰富,有传统补间动画,平移, 缩放等等效果,但是这里我们仅仅介绍这个AnimationDrawable实现帧动画,关于alpha,scale, translate,rotate等,后续在动画章节再进行详细的介绍~
>
> 我们这里使用<**animation-list**>作为根节点

**相关属性方法**:

> **oneshot**:设置是否循环播放,false为循环播放!!! **duration**:帧间隔时间,通常我们会设置为300毫秒 我们获得AniamtionDrawable实例后，需要调用它的start()方法播放动画，另外要注意 在OnCreate()方法中调用的话,是没有任何效果的,因为View还没完成初始化,我们可以 用简单的handler来延迟播放动画!当然还有其他的方法,可见下述链接: [Android AnimationDrawable运行的几种方式](https://www.runoob.com/w3cnote/android-animationdrawable.html) 使用AnimationDrawable来实现帧动画真的是非常方便的~

**使用示例**：

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/71886461.jpg)

**代码实现**：

①**先定义一个AnimationDrawable的xml资源文件**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">

    <item
        android:drawable="@mipmap/ic_pull_to_refresh_loading01"
        android:duration="100" />

    <item
        android:drawable="@mipmap/ic_pull_to_refresh_loading02"
        android:duration="100" />
    <item
        android:drawable="@mipmap/ic_pull_to_refresh_loading03"
        android:duration="100" />
    <item
        android:drawable="@mipmap/ic_pull_to_refresh_loading04"
        android:duration="100" />
    <item
        android:drawable="@mipmap/ic_pull_to_refresh_loading05"
        android:duration="100" />
    <item
        android:drawable="@mipmap/ic_pull_to_refresh_loading06"
        android:duration="100" />

</animation-list> 
```

**②activity_main.xml设置下src,然后MainActivity中**:

```java
public class MainActivity extends AppCompatActivity {

    private ImageView img_show;
    private AnimationDrawable ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_show = (ImageView) findViewById(R.id.img_show);
        // 核心实现代码
        ad = (AnimationDrawable) img_show.getDrawable();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ad.start();
            }
        }, 300);
    }
}
```

嘿嘿，超简单有木有，以后在一些需要用到帧动画的地方，直接上**AnimationDrawable**， 当然，只适合于不需要进行控制的帧动画，比如上面这个就是超表下拉刷新时候的进度条素材 做成的一个简单帧动画！根据自己的需求自行拓展~

## LayerDrawable

层图形对象，包含一个Drawable数组，然后按照数组对应的顺序来绘制他们，索引 值最大的Drawable会被绘制在最上层！虽然这些Drawable会有交叉或者重叠的区域，但 他们位于不同的层，所以并不会相互影响，以<**layer-list**>作为根节点！

**相关属性如下**：

> - **drawable**:引用的位图资源,如果为空徐璈有一个Drawable类型的子节点
> - **left**:层相对于容器的左边距
> - **right**:层相对于容器的右边距
> - **top**:层相对于容器的上边距
> - **bottom**:层相对于容器的下边距
> - **id**:层的id

**使用示例**：

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/33659494.jpg)



**代码实现**：

非常简单，结合前面学习的shapeDrawable和ClipDrawable：

**layerList_one.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@android:id/background">
        <shape android:shape="rectangle">
            <solid android:color="#C2C2C1" />
            <corners android:radius="50dp" />
        </shape>
    </item>
    <item android:id="@android:id/progress">
        <clip>
            <shape android:shape="rectangle">
                <solid android:color="#BCDA73" />
                <corners android:radius="50dp" />
            </shape>
        </clip>
    </item>
</layer-list> 
```

然后在布局文件中添加一个Seekbar，内容如下：

```xml
<SeekBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:indeterminateDrawable="@android:drawable/progress_indeterminate_horizontal"
        android:indeterminateOnly="false"
        android:maxHeight="10dp"
        android:minHeight="5dp"
        android:progressDrawable="@drawable/layerlist_one"
        android:thumb="@drawable/shape_slider" />
```

卧槽，没了？对的，就是这么点东西~说了是层图形对象，我们还可以弄个**层叠图片**的效果：![img](https://www.runoob.com/wp-content/uploads/2015/10/80416467.jpg)

**运行效果图**：

**实现代码**：

**层叠图片的layerlist_two.xml**:



```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <bitmap
            android:gravity="center"
            android:src="@mipmap/ic_bg_ciwei" />
    </item>
    <item
        android:left="25dp"
        android:top="25dp">
        <bitmap
            android:gravity="center"
            android:src="@mipmap/ic_bg_ciwei" />
    </item>
    <item
        android:left="50dp"
        android:top="50dp">
        <bitmap
            android:gravity="center"
            android:src="@mipmap/ic_bg_ciwei" />
    </item>
</layer-list> 
```

然后在activity_main.xml里加个ImageView，内容如下：

```xml
<ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/layerlist_two"/>
```

## TransitionDrawable

LayerDrawable的一个子类，TransitionDrawable只管理两层的Drawable！两层！两层！ 并且提供了透明度变化的动画，可以控制一层Drawable过度到另一层Drawable的动画效果。 根节点为<**transition**>，记住只有两个Item，多了也没用，属性和LayerDrawable差不多， 我们需要调用**startTransition**方法才能启动两层间的切换动画； 也可以调用**reverseTransition()**方法反过来播放：

**使用示例**：

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/85899256.jpg)

**实现代码**：

在res/drawable创建一个TransitionDrawable的xml文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<transition xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:drawable="@mipmap/ic_bg_meizi1"/>
    <item android:drawable="@mipmap/ic_bg_meizi2"/>
</transition>
```

然后布局文件里加个ImageView，然后把src设置成上面的这个drawable 然后MainActivity.java内容如下：

```java
public class MainActivity extends AppCompatActivity {
    private ImageView img_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_show = (ImageView) findViewById(R.id.img_show);
        TransitionDrawable td = (TransitionDrawable) img_show.getDrawable();
        td.startTransition(3000);
        //你可以可以反过来播放，使用reverseTransition即可~
        //td.reverseTransition(3000);
    }
}
```

另外，如果你想实现：多张图片循环的淡入淡出的效果 可参考：[Android Drawable Resource学习（七）、TransitionDrawable](http://blog.csdn.net/lonelyroamer/article/details/8243606)中的示例 很简单，核心原理就是：handler定时修改Transition中两个图片！

## LevelListDrawable

> 用来管理一组Drawable的,我们可以为里面的drawable设置不同的level， 当他们绘制的时候，会根据level属性值获取对应的drawable绘制到画布上，根节点 为:<**level-list**>他并没有可以设置的属性，我们能做的只是设置每个<**item**> 的属性！

**item可供设置的属性如下**：

> - **drawable**:引用的位图资源,如果为空徐璈有一个Drawable类型的子节点
> - **minlevel:level**对应的最小值
> - **maxlevel:level**对应的最大值

**使用示例**：

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/65455566.jpg)

**代码实现**：

通过shapeDrawable画圆，一式五份，改下宽高即可：

**shape_cir1.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="#2C96ED"/>
    <size android:height="20dp" android:width="20dp"/>
</shape>
```

接着到LevelListDrawable，这里我们设置五层：

**level_cir.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<level-list xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:drawable="@drawable/shape_cir1" android:maxLevel="2000"/>
    <item android:drawable="@drawable/shape_cir2" android:maxLevel="4000"/>
    <item android:drawable="@drawable/shape_cir3" android:maxLevel="6000"/>
    <item android:drawable="@drawable/shape_cir4" android:maxLevel="8000"/>
    <item android:drawable="@drawable/shape_cir5" android:maxLevel="10000"/>
</level-list> 
```

最后MainActivity写如下代码：

```java
public class MainActivity extends AppCompatActivity {

    private ImageView img_show;

    private LevelListDrawable ld;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (ld.getLevel() > 10000) ld.setLevel(0);
                img_show.setImageLevel(ld.getLevel() + 2000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_show = (ImageView) findViewById(R.id.img_show);
        ld = (LevelListDrawable) img_show.getDrawable();
        img_show.setImageLevel(0);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 100);
    }
}
```

## StateListDrawable

好了终于迎来了最后一个drawable：StateListDrawable，这个名字看上去模式，其实我们 以前就用到了，还记得为按钮设置不同状态的drawable的<**selctor**>吗？没错，用到的 就是这个StateListDrawable！

**可供设置的属性如下**：

> - **drawable**:引用的Drawable位图,我们可以把他放到最前面,就表示组件的正常状态~
> - **state_focused**:是否获得焦点
> - **state_window_focused**:是否获得窗口焦点
> - **state_enabled**:控件是否可用
> - **state_checkable**:控件可否被勾选,eg:checkbox
> - **state_checked**:控件是否被勾选
> - **state_selected**:控件是否被选择,针对有滚轮的情况
> - **state_pressed**:控件是否被按下
> - **state_active**:控件是否处于活动状态,eg:slidingTab
> - **state_single**:控件包含多个子控件时,确定是否只显示一个子控件
> - **state_first**:控件包含多个子控件时,确定第一个子控件是否处于显示状态
> - **state_middle**:控件包含多个子控件时,确定中间一个子控件是否处于显示状态
> - **state_last**:控件包含多个子控件时,确定最后一个子控件是否处于显示状态

**使用示例**：

那就来写个简单的圆角按钮吧！

**运行效果图**：

![img](https://www.runoob.com/wp-content/uploads/2015/10/32015507.jpg)

**代码实现**：

那就先通过shapeDrawable来画两个圆角矩形，只是颜色不一样而已：

**shape_btn_normal.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#DD788A"/>
    <corners android:radius="5dp"/>
    <padding android:top="2dp" android:bottom="2dp"/>
</shape>
```

接着我们来写个selctor：**selctor_btn.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true" android:drawable="@drawable/shape_btn_pressed"/>
    <item android:drawable="@drawable/shape_btn_normal"/>
</selector>
```

然后按钮设置android:background="@drawable/selctor_btn"就可以了~ 你可以根据自己需求改成矩形或者椭圆，圆形等！













# ==动画==





## ==帧动画==

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

## ==补间动画==

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

