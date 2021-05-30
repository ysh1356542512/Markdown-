# 绘图基础

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