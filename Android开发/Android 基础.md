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

# Material Design





#  ViewPager

# RecyclerView

# TabLayout



# Fragment

