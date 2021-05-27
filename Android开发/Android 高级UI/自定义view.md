

# 分类

自定义View的实现方式有以下几种

| 类型              | 定义                                                         |
| ----------------- | ------------------------------------------------------------ |
| 自定义组合控件    | 多个控件组合成为一个新的控件，方便多处复用                   |
| 继承系统View控件  | 继承自TextView等系统控件，在系统控件的基础功能上进行扩展     |
| 继承View          | 不复用系统控件逻辑，继承View进行功能定义                     |
| 继承系统ViewGroup | 继承自LinearLayout等系统控件，在系统控件的基础功能上进行扩展 |
| 继承ViewViewGroup | 不复用系统控件逻辑，继承ViewGroup进行功能定义                |

# View绘制流程

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
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <declare-styleable name="test">
            <attr name="text" format="string" />
            <attr name="testAttr" format="integer" />
        </declare-styleable>
    </resources>
```

### 自定义View类

```java
public class MyTextView extends View {
    private static final String TAG = MyTextView.class.getSimpleName();

    //在View的构造方法中通过TypedArray获取
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.test);
        String text = ta.getString(R.styleable.test_testAttr);
        int textAttr = ta.getInteger(R.styleable.test_text, -1);
        Log.e(TAG, "text = " + text + " , textAttr = " + textAttr);
        ta.recycle();
    }
}
```

### 布局文件中使用

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res/com.example.test"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <com.example.test.MyTextView
        android:layout_width="100dp"
        android:layout_height="200dp"
        app:testAttr="520"
        app:text="helloworld" />

</RelativeLayout>
```

## 属性值的类型format

###   reference：

参考某一资源ID

#### 属性定义：

```xml
<declare-styleable name = "名称">
     <attr name = "background" format = "reference" />
</declare-styleable>
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
<declare-styleable name="名称">
    <attr name="orientation">
        <enum name="horizontal" value="0" />
        <enum name="vertical" value="1" />
    </attr>
</declare-styleable>

```

#### 属性使用

```xml
<LinearLayout  
    android:orientation = "vertical">
</LinearLayout>

```

==注意：枚举类型的属性在使用的过程中只能同时使用其中一个，不能 android:orientation = “horizontal｜vertical"==





### flag：位或运算

#### 属性定义

```xml
<declare-styleable name="名称">
    <attr name="gravity">
            <flag name="top" value="0x01" />
            <flag name="bottom" value="0x02" />
            <flag name="left" value="0x04" />
            <flag name="right" value="0x08" />
            <flag name="center_vertical" value="0x16" />
            ...
    </attr>
</declare-styleable>

```

#### 属性使用

```xml
<TextView android:gravity="bottom|left"/>

```

### 混合类型：

==属性定义时可以指定多种类型值==

#### 属性定义

```xml
<declare-styleable name = "名称">
     <attr name = "background" format = "reference|color" />
</declare-styleable>

```

#### 属性使用

```xml
<ImageView
android:background = "@drawable/图片ID" />
或者：
<ImageView
android:background = "#00FF00" />

```

