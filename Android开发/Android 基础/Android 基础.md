# 四大组件

## Activity

## Broadcastreceiver

## ContentProvider

## Service

```java
//通知栏控制器上一首按钮广播操作
Intent intentPrev = new Intent(PREV);
PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, 0);
//为prev控件注册事件
remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);
```

![ScreenClip](../../%E5%9B%BE%E5%BA%93/Android%20%E5%9F%BA%E7%A1%80/ScreenClip.png)

沉浸式做法

<item name="android:windowFullscreen">true</item>     这里可以在style的属性里面设置就可以完成

# 启动模式

## standard

Activity的默认的起动模式。在这个模式下，每启动⼀个新的Activity，它就会进⼊返回栈，并处于

栈顶位置。对于使⽤本模式的Activity，系统不会在乎这个Activity是否已经处于返回栈的栈顶。每

次启动都会创建⼀个新的Activity

## singleTop

当⼀个Activity的启动模式被指定为本模式时，在启动该Activity的时候，，如果发现返回栈的栈顶

已经是该Activity，则认为直接使⽤它，不会再创建新的Activity实例。

## singleTask

让被标记的Activity在整个程序的上下⽂中只存在⼀个实例。当⼀个Activity启动⽅式被指定为

singleTask后，每次启动该Activity的时候系统会⾸先在返回栈中检查是否有该Activity的实例，如

果发现存在则直接使⽤该实例，并把在这个Activity之上的所有Activity通通出栈，如果没有发现则

创建⼀个该Activity的实例。

##  singleInstance

被指定为singleInstance模式的Activity会启⽤⼀个新的栈来管理这个Activity。意义所在，假如我

们的⼀个Activity是允许被其他程序屌⽤的，如果我们想实现其他程序和我们的程序共享这个

Activity的实例，其他三种模式是肯定做不到的，因为每⼀个应⽤程序都会有⾃⼰的返回栈，⽤⼀

个Activity在不痛的返回栈中⼊栈时必定是创建了新的实例。而本模式下则会有⼀个单独的栈来管

理这个Activity，不管是那个应⽤程序来访问这个Activity，都⽤的同⼀个返回栈，也就解决了前⾯

提出的问题。

# XML及Android常用控件

## android前缀

```java
xmlns:android="http://schemas.android.com/apk/res/android
```



这句话的意思是，声明这个命名空间引⽤的是Android系统的，而其中的android作为前缀，

是这个引⽤别称的意思（ps：换成其他的也不是不⾏）

后⾯的schemas的意思是xml⽂件的约束（也就是xml的抒写规范），还有⼀种xml的约 束是

DTD，但是被DTD取代了

有了这个，Android Studio就会在我们编写布局⽂件的时候给出提⽰，提⽰我们可以输 ⼊什

么，不可以输⼊什么。也可以理解为语法⽂件吗.，或者语法判断器

## app前缀

```java
xmlns:app="http://schemas.android.com/apk/res-auto"
```

在项⽬需求中，我们往往使⽤系统⾃带的属性和控件是不够的，我们可能需要导⼊⾃定 义控

件的⼀些属性，或者support⽀持包之类的

为了引⼊⾃定义属性，我们以xmlns:前缀=http://schemas.android.com/apk/res/你的应⽤

程序包路径，将其导⼊，但是现在的普遍走法是使⽤xmlns:app="http://schemas.android.c

om/apk/res-auto"，因为res-auto可以引⽤所 有的⾃定义包名。

## tool前缀

```java
xmlns:tools="http://schemas.android.com/tools"
```

tools可以告诉Android Studio，那些属性在运⾏的时候是被忽略的，只是在设计布局的 时候

有效果

tools可以覆盖android所有的标准属性，将 android: 换成 tools: 即可，而且在运⾏ 的时候

连tools本⾝都是被忽略的，不会被带进apk

## 一些常用属性及控件

###  dp

安卓中的相对⼤小

其实dp就是为了使得开发者设置的⻓度能够根据不同屏幕(分辨率/尺⼨也就是dpi)获得 不同的像素

(px)数量。⽐如：我将⼀个控件设置⻓度为1dp，那么在160dpi屏幕上该控 件⻓度为1px，在

240dpi的屏幕上该控件的⻓度为1*240/160=1.5*个像素点。

**也就是****dp****会随着不同屏幕而改变控件⻓度的像素数量。**

关于dp的官⽅叙述为当屏幕每英⼨有160个像素时(也就是160dpi)，dp与px等价的。那 如果每英

⼨240个像素呢？1dp—>1*240/160=1.5px，即1dp与1.5px等价了。

其实记住⼀点，**dp****最终都要化为像素数量来衡量⼤小的，因为只有像素数量最直观。**

### dpi（dot per inch）

每英⼨像素多少

要想判别⼿机屏幕的显⽰好坏，还要考虑屏幕的宽⾼(英⼨)，也就是⽤dpi即每英⼨多少像素 来评

价屏幕的显⽰效果。（不然假如⼿机分辨率是1920×1080，但是屏幕是⼏⼗⼨的，那显 ⽰效果将

不会很好，甚⾄你有可能看到小的像素块，那将更影响视觉效果。）

###  px

像素点

平常所说的1920×1080只是像素数量，也就是1920px×1080px，代表⼿机⾼度上有1920个 像素

点，宽度上有1080个像素点

### sp

 sp除了能够像dp⼀样可以适应屏幕密度的变化，还可以随着系统字体的⼤小设置改变作出变化。

如果不想⽂字随着⼿机设置中字体的⼤小发⽣改变（例如标题），可以使⽤dp代替。



# PopupWindow

> 本节给大家带来的是最后一个用于显示信息的UI控件——PopupWindow(悬浮框)，如果你想知道 他长什么样子，你可以打开你手机的QQ，长按列表中的某项，这个时候后弹出一个黑色的小 对话框，这种就是PopupWindow了，和AlertDialog对话框不同的是，他的位置可以是随意的；
>
> 另外AlertDialog是非堵塞线程的，而PopupWindow则是堵塞线程的！而官方有这样一句话来介绍 PopupWindow：
>
> **A popup window that can be used to display an arbitrary view. The popup window is**
>
> **a floating container that appears on top of the current activity.**
>
> 大概意思是：一个弹出窗口控件，可以用来显示任意View，而且会浮动在当前activity的顶部
>
> 下面我们就来对这个控件进行学习~
>
> 官方文档：[PopupWindow](http://androiddoc.qiniudn.com/reference/android/widget/PopupWindow.html)



## 方法

### 构造



> 我们在文档中可以看到，提供给我们的PopupWindow的构造方法有九种之多，这里只贴实际 开发中用得较多的几个构造方法：
>
> - **public PopupWindow (Context context)**
> - **public PopupWindow(View contentView, int width, int height)**
> - **public PopupWindow(View contentView)**
> - **public PopupWindow(View contentView, int width, int height, boolean focusable)**
>
> 参数就不用多解释了吧，contentView是PopupWindow显示的View，focusable是否显示焦点

### 常用

> 下面介绍几个用得较多的一些方法，其他的可自行查阅文档：
>
> - **setContentView**(View contentView)：设置PopupWindow显示的View
> - **getContentView**()：获得PopupWindow显示的View
> - **showAsDropDown(View anchor)**：相对某个控件的位置（正左下方），无偏移
> - **showAsDropDown(View anchor, int xoff, int yoff)**：相对某个控件的位置，有偏移
> - **showAtLocation(View parent, int gravity, int x, int y)**： 相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移 PS:parent这个参数只要是activity中的view就可以了！
> - **setWidth/setHeight**：设置宽高，也可以在构造方法那里指定好宽高， 除了可以写具体的值，还可以用WRAP_CONTENT或MATCH_PARENT， popupWindow的width和height属性直接和第一层View相对应。
> - **setFocusable(true)**：设置焦点，PopupWindow弹出后，所有的触屏和物理按键都由PopupWindows 处理。其他任何事件的响应都必须发生在PopupWindow消失之后，（home 等系统层面的事件除外）。 比如这样一个PopupWindow出现的时候，按back键首先是让PopupWindow消失，第二次按才是退出 activity，准确的说是想退出activity你得首先让PopupWindow消失，因为不并是任何情况下按back  PopupWindow都会消失，必须在PopupWindow设置了背景的情况下 。
> - **setAnimationStyle(int)：**设置动画效果

### 示例

![img](https://www.runoob.com/wp-content/uploads/2015/10/43925301.jpg)

**实现关键代码**：

先贴下动画文件：**anim_pop.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <alpha android:fromAlpha="0"
        android:toAlpha="1"
        android:duration="2000">
    </alpha>
</set> 
```

接着是popupWindow的布局：**item_popip.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/ic_pop_bg"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn_xixi"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="5dp"
        android:text="嘻嘻"
        android:textSize="18sp" />

    <Button
        android:id="@+id/btn_hehe"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="5dp"
        android:text="呵呵"
        android:textSize="18sp" />

</LinearLayout>
```

**MainActivity.java**：

```java
public class MainActivity extends AppCompatActivity {

    private Button btn_show;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        btn_show = (Button) findViewById(R.id.btn_show);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow(v);
            }
        });
    }


    private void initPopWindow(View v) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_popup, null, false);
        Button btn_xixi = (Button) view.findViewById(R.id.btn_xixi);
        Button btn_hehe = (Button) view.findViewById(R.id.btn_hehe);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 50, 0);

        //设置popupWindow里的按钮的事件
        btn_xixi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "你点击了嘻嘻~", Toast.LENGTH_SHORT).show();
            }
        });
        btn_hehe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "你点击了呵呵~", Toast.LENGTH_SHORT).show();
                popWindow.dismiss();
            }
        });
    }
}
```

### 资源

[PopWindowDemo.zip](https://www.runoob.com/wp-content/uploads/2015/10/PopWindowDemo.zip)

# Menu

> 本章给大家带来的是Android中的Menu(菜单)，而在Android中的菜单有如下几种：
>
> - **OptionMenu**：选项菜单，android中最常见的菜单，通过Menu键来调用
> - **SubMenu**：子菜单，android中点击子菜单将弹出一个显示子菜单项的悬浮框， 子菜单不支持嵌套，即不能包括其他子菜单
> - **ContextMenu**：上下文菜单，通过长按某个视图组件后出现的菜单，该组件需注册上下文菜单 本节我们来依依学习这几种菜单的用法~ PS：官方文档：[menus](http://androiddoc.qiniudn.com/guide/topics/ui/menus.html)

## OptionMenu

> 答：非常简单，重写两个方法就好，其实这两个方法我们在创建项目的时候就会自动生成~ 他们分别是：
>
> - public boolean **onCreateOptionsMenu**(Menu menu)：调用OptionMenu，在这里完成菜单初始化
> - public boolean **onOptionsItemSelected**(MenuItem item)：菜单项被选中时触发，这里完成事件处理
>
> 当然除了上面这两个方法我们可以重写外我们还可以重写这三个方法：
>
> - public void **onOptionsMenuClosed**(Menu menu)：菜单关闭会调用该方法
> - public boolean **onPrepareOptionsMenu**(Menu menu)：选项菜单显示前会调用该方法， 可在这里进行菜单的调整(动态加载菜单列表)
> - public boolean **onMenuOpened**(int featureId, Menu menu)：选项菜单打开以后会调用这个方法

而加载菜单的方式有两种，一种是直接通过编写菜单XML文件，然后调用： **getMenuInflater().inflate(R.menu.menu_main, menu);**加载菜单 或者通过代码动态添加，onCreateOptionsMenu的参数menu，调用add方法添加 菜单，add(菜单项的组号，ID，排序号，标题)，另外如果排序号是按添加顺序排序的话都填0即可！

### 示例

![img](https://www.runoob.com/wp-content/uploads/2015/10/34719444.jpg)

**代码实现**：

**MainActivity.java**：

```java
public class MainActivity extends AppCompatActivity {

    //1.定义不同颜色的菜单项的标识:
    final private int RED = 110;
    final private int GREEN = 111;
    final private int BLUE = 112;
    final private int YELLOW = 113;
    final private int GRAY= 114;
    final private int CYAN= 115;
    final private int BLACK= 116;

    private TextView tv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_test = (TextView) findViewById(R.id.tv_test);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1,RED,4,"红色");
        menu.add(1,GREEN,2,"绿色");
        menu.add(1,BLUE,3,"蓝色");
        menu.add(1,YELLOW,1,"黄色");
        menu.add(1,GRAY,5,"灰色");
        menu.add(1,CYAN,6,"蓝绿色");
        menu.add(1,BLACK,7,"黑色");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case RED:
                tv_test.setTextColor(Color.RED);
                break;
            case GREEN:
                tv_test.setTextColor(Color.GREEN);
                break;
            case BLUE:
                tv_test.setTextColor(Color.BLUE);
                break;
            case YELLOW:
                tv_test.setTextColor(Color.YELLOW);
                break;
            case GRAY:
                tv_test.setTextColor(Color.GRAY);
                break;
            case CYAN:
                tv_test.setTextColor(Color.CYAN);
                break;
            case BLACK:
                tv_test.setTextColor(Color.BLACK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
```

## ContextMenu

> 答：使用的流程如下：
>
> - **Step 1**：重写onCreateContextMenu()方法
> - **Step 2**：为view组件注册上下文菜单，使用registerForContextMenu()方法,参数是View
> - **Step 3**：重写onContextItemSelected()方法为菜单项指定事件监听器

上面的OptionMenu我们使用了Java代码的方法来完成菜单项的添加，这里我们就用XML文件的 方式来生成我们的CotnextMenu吧，另外关于使用Java代码来生成菜单还是使用XML来生成菜单， 建议使用后者来定义菜单，这样可以减少Java代码的代码臃肿，而且不用每次都用代码分配 id，只需修改XML文件即可修改菜单的内容，这样在一定程度上位程序提供的了更好的解耦， 低耦合，高内聚，是吧~

### 示例



![img](https://www.runoob.com/wp-content/uploads/2015/10/13834579.jpg)

**实现代码**：

我们先来编写选项菜单的菜单XML文件：

**menu_context.xml**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 定义一组单选按钮 -->
    <!-- checkableBehavior的可选值由三个：single设置为单选，all为多选，none为普通选项 -->
    <group android:checkableBehavior="none">
        <item android:id="@+id/blue" android:title="@string/font_blue"/>
        <item android:id="@+id/green" android:title="@string/font_green"/>
        <item android:id="@+id/red" android:title="@string/font_red"/>
    </group>
</menu>
```

接着我们在选项菜单的那个基础上，添加一个TextView，然后加上下面一些东西：

```java
private TextView tv_context;
tv_context = (TextView) findViewById(R.id.tv_context);
        registerForContextMenu(tv_context);

    //重写与ContextMenu相关方法
    @Override
    //重写上下文菜单的创建方法
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflator = new MenuInflater(this);
        inflator.inflate(R.menu.menu_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //上下文菜单被点击是触发该方法
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.blue:
                tv_context.setTextColor(Color.BLUE);
                break;
            case R.id.green:
                tv_context.setTextColor(Color.GREEN);
                break;
            case R.id.red:
                tv_context.setTextColor(Color.RED);
                break;
        }
        return true;
    }
```

## SubMenu



所谓的子菜单只是在<**item**>中又嵌套了一层<**menu**>而已

### 示例



![img](https://www.runoob.com/wp-content/uploads/2015/10/84191606.jpg)



**实现代码:**

编写子菜单的Menu文件：menu_sub.xml：

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/submenu" android:title="子菜单使用演示~">
        <menu>
            <group android:checkableBehavior = "none">
                <item android:id="@+id/one" android:title = "子菜单一"/>
                <item android:id="@+id/two" android:title = "子菜单二"/>
                <item android:id="@+id/three" android:title = "子菜单三"/>
            </group>
        </menu>
    </item>
</menu>
```

接着我们改下上面上下文菜单的两个方法的内容，换上下面的代码：

```java
public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //子菜单部分：
        MenuInflater inflator = new MenuInflater(this);
        inflator.inflate(R.menu.menu_sub, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
}

public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
            case R.id.one:
                Toast.makeText(MainActivity.this,"你点击了子菜单一",Toast.LENGTH_SHORT).show();
                break;
            case R.id.two:
                item.setCheckable(true);
                Toast.makeText(MainActivity.this,"你点击了子菜单二",Toast.LENGTH_SHORT).show();
                break;
            case R.id.three:
                Toast.makeText(MainActivity.this,"你点击了子菜单三",Toast.LENGTH_SHORT).show();
                item.setCheckable(true);
                break;
        }
    return true;
}
```

好的，灰常简单是吧，另外，如果你想在Java代码中添加子菜单的话，可以调用addSubMenu()

比如：SubMenu file = menu.addSubMenu("文件");file还需要addItem添加菜单项哦！

## PopupMenu

一个类似于PopupWindow的东东，他可以很方便的在指定View下显示一个弹出菜单，而且 他的菜单选项可以来自于Menu资源，下面我们写个简单的例子来使用下这个东东~

### 示例

![img](https://www.runoob.com/wp-content/uploads/2015/10/25073198.jpg)

**实现代码：**

菜单资源文件：menu_pop.xml：

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/lpig" android:title="小猪" />
    <item android:id="@+id/bpig" android:title="大猪" />
</menu>
```

在布局中添加一个按钮，然后添加点击事件：

**MainActivity.java**:

```java
btn_show_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this,btn_show_menu);
                popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.lpig:
                                Toast.makeText(MainActivity.this,"你点了小猪~",
                                Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.bpig:
                                Toast.makeText(MainActivity.this,"你点了大猪~",
                                Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
```

## 资源

[MenuDemo1.zip](https://www.runoob.com/wp-content/uploads/2015/10/MenuDemo1.zip)























































# Material Design





#  ViewPager

## PageTransformer

https://blog.csdn.net/u013762572/article/details/88954561













# RecyclerView















# TabLayout













# Fragment





# AlertDialog

![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304232917390-1238873465.png)

## 系统对话框的几种类型与实现

在项目的实际开发中，用到的系统对话框几乎是没有的。原因大概包含以下几点：

> - 样式过于单一，不能满足大部分实际项目中的需求。
> - 对话框的样式会根据手机系统版本的不同而变化。不能达到统一的样式。
> - 能实现的功能过于简单。

在这里先附上下面代码中出现文本的`string.xml`文件。

```kotlin
<string name="dialog_normal_content">我是普通dialog</string>
<string name="dialog_normal_more_button_content">我是普通多按钮dialog</string>
<string name="dialog_btn_confirm_text">确定</string>
<string name="dialog_btn_cancel_text">取消</string>
<string name="dialog_btn_neutral_text">忽略</string>
<string name="dialog_btn_confirm_hint_text">您点击了确定按钮</string>
<string name="dialog_btn_cancel_hint_text">您点击了取消按钮</string>
<string name="dialog_btn_neutral_hint_text">您点击了忽略按钮</string>
```

**1、普通对话框**

在实际项目开发中，此类型对话框中用到的地方要比其他类型的对话框多一些。但是考虑`UI`统一问题，也会很少用。

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234648352-1936497754.png)

*代码：*

```kotlin
  private void showNormalDialog(){
    //创建dialog构造器
    AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
    //设置title
    normalDialog.setTitle(getString(R.string.dialog_normal_text));
    //设置icon
    normalDialog.setIcon(R.mipmap.ic_launcher_round);
    //设置内容
    normalDialog.setMessage(getString(R.string.dialog_normal_content));
    //设置按钮
    normalDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
            , new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(DialogActivity.this,getString(R.string.dialog_btn_confirm_hint_text)
                    ,Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    });
    //创建并显示
    normalDialog.create().show();
  }
```

系统对话框都是支持链式调用的，举例：

```kotlin
    new AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_normal_text))
            .setIcon(R.mipmap.ic_launcher_round)
            .setMessage(getString(R.string.dialog_normal_content))
            .setPositiveButton(getString(R.string.dialog_btn_confirm_text)
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DialogActivity.this,getString(R.string.dialog_btn_confirm_hint_text)
                            ,Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            })
            .create()
            .show();
```

下面的代码都是可以用链式调用的，这里就不展示了。

**2、普通对话框(多按钮)**

在系统对话框中最多出现三个按钮，即`PositiveButton`（确定）、`NegativeButton`（取消）、`NeutralButton`（忽略）。

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234701273-1958338007.png)

*代码：*

```kotlin
  private void showNormalMoreButtonDialog(){
    AlertDialog.Builder normalMoreButtonDialog = new AlertDialog.Builder(this);
    normalMoreButtonDialog.setTitle(getString(R.string.dialog_normal_more_button_text));
    normalMoreButtonDialog.setIcon(R.mipmap.ic_launcher_round);
    normalMoreButtonDialog.setMessage(getString(R.string.dialog_normal_more_button_content));

    //设置按钮
    normalMoreButtonDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DialogActivity.this
                            ,getString(R.string.dialog_btn_confirm_hint_text),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
    normalMoreButtonDialog.setNegativeButton(getString(R.string.dialog_btn_cancel_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DialogActivity.this,
                            getString(R.string.dialog_btn_cancel_hint_text),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
    normalMoreButtonDialog.setNeutralButton(getString(R.string.dialog_btn_neutral_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DialogActivity.this,
                            getString(R.string.dialog_btn_neutral_hint_text),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

    normalMoreButtonDialog.create().show();
  }
```

也可以用下面的实现方式，和上面的代码效果是一样的。k

```kotlin
  private void showNormalMoreButtonDialog(){
    DialogInterface.OnClickListener setListener = null;

    AlertDialog.Builder normalMoreButtonDialog = new AlertDialog.Builder(this);
    normalMoreButtonDialog.setTitle(getString(R.string.dialog_normal_more_button_text));
    normalMoreButtonDialog.setIcon(R.mipmap.ic_launcher_round);
    normalMoreButtonDialog.setMessage(getString(R.string.dialog_normal_more_button_content));
   
    setListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Toast.makeText(DialogActivity.this,
                            getString(R.string.dialog_btn_confirm_hint_text),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    Toast.makeText(DialogActivity.this
                            ,getString(R.string.dialog_btn_neutral_hint_text),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(DialogActivity.this
                            ,getString(R.string.dialog_btn_cancel_hint_text),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
            }
        }
    };
    normalMoreButtonDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text),setListener);
    normalMoreButtonDialog.setNegativeButton(getString(R.string.dialog_btn_cancel_text),setListener);
    normalMoreButtonDialog.setNeutralButton(getString(R.string.dialog_btn_neutral_text),setListener);

    normalMoreButtonDialog.create().show();
  }
```

**3、普通列表对话框**

此种类型的对话框能实现简单的列表。

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234715483-628362255.png)

*代码：*

```kotlin
  /**
   * 普通列表dialog
   */
  private void showListDialog(){
    final String listItems[] = new String[]{"listItems1","listItems2","listItems3",
            "listItems4","listItems5","listItems6"};

    AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
    listDialog.setTitle(getString(R.string.dialog_list_text));
    listDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        设置item 不能用setMessage()
        用setItems
        items : listItems[] -> 列表项数组
        listener -> 回调接口
    */
    listDialog.setItems(listItems,new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(DialogActivity.this,listItems[which],Toast.LENGTH_SHORT).show();
        }
    });

    //设置按钮
    listDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

    listDialog.create().show();
  }
```

**4、单选对话框**

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234744067-1550847957.png)

*代码：*

```kotlin
  private void showRadioDialog(){
    final String radioItems[] = new String[]{"radioItem1","radioItem1","radioItem1","radioItem1"};

    AlertDialog.Builder radioDialog = new AlertDialog.Builder(this);
    radioDialog.setTitle(getString(R.string.dialog_radio_text));
    radioDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        设置item 不能用setMessage()
        用setSingleChoiceItems
        items : radioItems[] -> 单选选项数组
        checkItem : 0 -> 默认选中的item
        listener -> 回调接口
    */
    radioDialog.setSingleChoiceItems(radioItems, 0, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(DialogActivity.this,radioItems[which],Toast.LENGTH_SHORT).show();
        }
    });

    //设置按钮
    radioDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

    radioDialog.create().show();
  }
```

**5、多选对话框**

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234757320-1623676885.png)

*代码：*
 private void showCheckBoxDialog(){

```kotlin
    final String checkBoxItems[] = new String[]{"checkBoxItems1","checkBoxItems2",
            "checkBoxItems3","checkBoxItems4"};
    final boolean isCheck[] = new boolean[]{false,true,true,false};

    AlertDialog.Builder checkBoxDialog = new AlertDialog.Builder(this);
    checkBoxDialog.setTitle(getString(R.string.dialog_check_box_text));
    checkBoxDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        设置item 不能用setMessage()
        用setMultiChoiceItems
        items : radioItems[] -> 多选选项数组
        checkItems : isCheck[] -> 是否选中数组
        listener -> 回调接口
    */
    checkBoxDialog.setMultiChoiceItems(checkBoxItems, isCheck
            , new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked){
                Toast.makeText(DialogActivity.this,
                        checkBoxItems[which] + "   选中",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(DialogActivity.this,
                        checkBoxItems[which] + "   未选中",Toast.LENGTH_SHORT).show();
            }
        }
    });

    //设置按钮
    checkBoxDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

    checkBoxDialog.create().show();
  }
```

**6、带输入框的弹窗**

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234808202-673299833.png)

*代码：*
 private void showEditDialog(){

```kotlin
    final EditText edit = new EditText(this);

    AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
    editDialog.setTitle(getString(R.string.dialog_edit_text));
    editDialog.setIcon(R.mipmap.ic_launcher_round);

    //设置dialog布局
    editDialog.setView(edit);

    //设置按钮
    editDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
            , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DialogActivity.this,
                            edit.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

    editDialog.create().show();
  }
```

**7、自定义布局的对话框**

此类型的对话框在实际项目开发中用到的地方比提示对话框用到的地方要多一些，不过在项目几乎上都是自定义的对话框...

*运行截图：*
 ![img](https://images2018.cnblogs.com/blog/1255627/201803/1255627-20180304234818225-569363107.png)

*布局文件：custom_dialog_layout.xml*
 

```kotlin
      <TextView
           android:id="@+id/dialog_text"
           android:layout_width="match_parent"
           android:layout_height="wrap_content"
           android:textSize="15sp"
           android:textColor="@color/colorPrimary"
           android:gravity="center"
           android:padding="12dp"/>

      <RelativeLayout
           android:layout_width="match_parent"
           android:layout_height="wrap_content">

           <ImageView
               android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:src="@mipmap/ic_launcher"/>

          <Button
              android:id="@+id/dialog_btn_confirm"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:textColor="@color/colorAccent"
              android:textSize="15sp"
              android:text="@string/dialog_btn_confirm_text"
              android:layout_centerHorizontal="true"/>

          <Button
              android:id="@+id/dialog_btn_cancel"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:textColor="@color/colorAccent"
              android:textSize="15sp"
              android:text="@string/dialog_btn_cancel_text"
              android:layout_centerHorizontal="true"
              android:layout_alignParentRight="true"/>

      </RelativeLayout>

  </LinearLayout>
```

*代码：*

```kotlin
  private void showLayoutDialog() {
    //加载布局并初始化组件
    View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout,null);
    TextView dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
    Button dialogBtnConfirm = (Button) dialogView.findViewById(R.id.dialog_btn_confirm);
    Button dialogBtnCancel = (Button) dialogView.findViewById(R.id.dialog_btn_cancel);

    final AlertDialog.Builder layoutDialog = new AlertDialog.Builder(this);
    layoutDialog.setTitle(getString(R.string.dialog_custom_layout_text));
    layoutDialog.setIcon(R.mipmap.ic_launcher_round);

    layoutDialog.setView(dialogView);

    //设置组件
    dialogText.setText("我是自定义layout的弹窗！！");
    dialogBtnConfirm .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(DialogActivity.this,"我是自定义layout的弹窗！！",Toast.LENGTH_SHORT).show();
        }
    });
    dialogBtnConfirm .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            layoutDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
        }
    });

    layoutDialog.create().show();
  }
```

以上就是`Android`系统弹窗的几种实现方式，几乎涵盖了能解决各种简单需求。其中自定义布局的方式奠定了自定义弹窗的基本实现。
