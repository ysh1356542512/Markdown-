# ==Window 机制探索==

## Window的概念

Android手机中==所有==的视图都是通过Window来呈现的，像常用的==Activity，Dialog，PopupWindow，Toast，==他们的视图都是附加在Window上的，所以可以这么说 ——「==Window是View的直接管理者。==」![这里写图片描述](https://img-blog.csdn.net/2018072317105731?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FpYW41MjBhbw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## Window

一个顶级窗口查看和行为的一个抽象基类。这个类的实例作为一个==顶级View==添加到==Window Manager==。它提供了一套==标准的UI方法==，比如添加背景，标题等等。当你需要用到Window的时候，你应该使用它的==唯一实现类==`PhoneWindow`。

```java
/**
 * Abstract base class for a top-level window look and behavior policy.  An
 * instance of this class should be used as the top-level view added to the
 * window manager. It provides standard UI policies such as a background, title
 * area, default key processing, etc.
 *
 * <p>The only existing implementation of this abstract class is
 * android.view.PhoneWindow, which you should instantiate when needing a
 * Window.
 */
public abstract class Window {
``````
    public abstract View getDecorView();
    
    @Nullable
    public View findViewById(@IdRes int id) {
        return getDecorView().findViewById(id);
    } 
    
    public abstract void setContentView(View view);
    ``````
}

```

### PhoneWindow

每一个Activity都包含一个Window对象(dialog，toast 等也是新添加的window对象)，而Window是一个抽象类，具体实现是PhoneWindow。在Activity中的setContentView实际上是调用==PhoneWindow的setContentView方法==。并且==PhoneWindow中包含着成员变量DecorView。==

​```java
//Activity 
    public void setContentView(@LayoutRes int layoutResID) {
        getWindow().setContentView(layoutResID);
        initWindowDecorActionBar();
    }

//getWindow获取到的是mWindow         
//在attach方法里,mWindow = new PhoneWindow(this, window);

```

### DecorView(`FrameLayout`)

作为顶级View,DecorView==一般情况下它内部会包含一个竖直方向的LinearLayout==，上面的标题栏(titleBar)，下面是内容栏。通常我们在Activity中通过setContentView所设置的布局文件就是被加载到id为android.R.id.content的内容栏里(FrameLayout)，

~~~java
public class DecorView extends FrameLayout implements RootViewSurfaceTaker, WindowCallbacks {
        ``````
}
~~~

### setContentView

```java
//PhoneWindow


    @Override
    public void setContentView(int layoutResID) {
        if (mContentParent == null) {

//①初始化
        //创建DecorView对象和mContentParent对象 ,并将mContentParent关联到DecorView上
            installDecor();
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();//Activity转场动画相关
        }

//②填充Layout  FEATURE_CONTENT_TRANSITIONS是否需要转场动画
        if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                    getContext());
            transitionTo(newScene);//Activity转场动画相关
        } else {
        //将Activity设置的布局文件，加载到mContentParent中
            mLayoutInflater.inflate(layoutResID, mContentParent);
        }

        //让DecorView的内容区域延伸到systemUi下方，防止在扩展时被覆盖，达到全屏、沉浸等不同体验效果。
        mContentParent.requestApplyInsets();

//③通知Activity布局改变
        final Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {

        //触发Activity的onContentChanged方法  
            cb.onContentChanged();
        }
        mContentParentExplicitlySet = true;
    }

```

①初始化 ： Activity第一次调用setContentView，则会调用installDecor()方法创建==DecorView==对象和==mContentParen==t对象。FEATURE_CONTENT_TRANSITIONS表示是否使用转场动画。如果内容已经加载过，并且不需要动画，则会调用removeAllViews移除内容以便重新填充Layout。

②填充Layout ： 初始化完毕，如果设置了FEATURE_CONTENT_TRANSITIONS，就会创建==Scene完成转场动画==。否则使用布局填充器将布局文件填充至mContentParent。到此为止，Activity的布局文件已经添加到DecorView里面了，所以可以理解Activity的setContentView方法的由来，==因为布局文件是添加到DecorView的mContentParent中，所以方法名为setContentView无可厚非==。「开头第一张图的contentView」

### installDecor

```java
//PhoneWindow  --> setContentView()


private void installDecor() {
        if (mDecor == null) {
        //创建DecorView
            mDecor = generateDecor();

``````
        }else {
            mDecor.setWindow(this);
        }
    
        if (mContentParent == null) {//并将mContentParent关联到DecorView上
    
        //根据主题theme设置对应的xml布局文件以及Feature(包括style,layout,转场动画,属性等)到DecorView中。
        //并将mContentParent绑定至id为ID_ANDROID_CONTENT(com.android.internal.R.id.content)的ViewGroup
        //mContentParent在DecorView添加的xml文件中
            mContentParent = generateLayout(mDecor);
    
            // Set up decor part of UI to ignore fitsSystemWindows if appropriate.
            mDecor.makeOptionalFitsSystemWindows();
    
                `````` 
            //添加其他资源
            //设置转场动画
        }

}
```

#### generateLayout

​```java
//PhoneWindow --> setContentView()  -->installDecor() 


 protected ViewGroup generateLayout(DecorView decor) {
        // Apply data from current theme.
        //获取当前的主题，加载默认资源和布局
        TypedArray a = getWindowStyle();

``````
        //根据theme的设定，找到对应的Feature(包括style,layout,转场动画，属性等)
        if (a.getBoolean(R.styleable.Window_windowNoTitle, false)) {
            requestFeature(FEATURE_NO_TITLE);//无titleBar
        } 
    
        ``````
    
        if (a.getBoolean(R.styleable.Window_windowFullscreen, false)) {
            setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN & (~getForcedWindowFlags()));//设置全屏
        }
    
        if (a.getBoolean(R.styleable.Window_windowTranslucentStatus,
                false)) {
            setFlags(FLAG_TRANSLUCENT_STATUS, FLAG_TRANSLUCENT_STATUS
                    & (~getForcedWindowFlags()));//透明状态栏
        }
    
        //根据当前主题，设定不同的Feature
        ``````
    
        int layoutResource;
        int features = getLocalFeatures();
    
        //由于布局较多，我们拿有titleBar的例子来看
        if ((features & (1 << FEATURE_NO_TITLE)) == 0) {
    
            ``````
                layoutResource = R.layout.screen_title;
    
        } 
        ``````
        else {//无titleBar
            layoutResource = R.layout.screen_simple;
        }
    
        mDecor.startChanging();
    
        //将布局layout，添加至DecorView中
        mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);
    
        //从布局中获取`ID_ANDROID_CONTENT`，并关联至contentParent
        ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT);
    
        ``````
        //配置完成，DecorView根据已有属性调整布局状态
        mDecor.finishChanging();
    
        return contentParent;
    }

```

​```xml
//sdk\platforms\android-25\data\res\layout\screen_simple.xml

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    android:orientation="vertical">
    <ViewStub android:id="@+id/action_mode_bar_stub"
              android:inflatedId="@+id/action_mode_bar"
              android:layout="@layout/action_mode_bar"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:theme="?attr/actionBarTheme" />
    <FrameLayout
         android:id="@android:id/content"
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         android:foregroundInsidePadding="false"
         android:foregroundGravity="fill_horizontal|top"
         android:foreground="?android:attr/windowContentOverlay" />
</LinearLayout>

```

```xml
//sdk\platforms\android-25\data\res\layout\screen_title.xml

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:fitsSystemWindows="true">
    <!-- Popout bar for action modes -->
    <ViewStub android:id="@+id/action_mode_bar_stub"
              android:inflatedId="@+id/action_mode_bar"
              android:layout="@layout/action_mode_bar"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:theme="?attr/actionBarTheme" />
    <FrameLayout
        android:layout_width="match_parent" 
        android:layout_height="?android:attr/windowTitleSize"
        style="?android:attr/windowTitleBackgroundStyle">
        <TextView android:id="@android:id/title" 
            style="?android:attr/windowTitleStyle"
            android:background="@null"
            android:fadingEdge="horizontal"
            android:gravity="center_vertical"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </FrameLayout>
    <FrameLayout android:id="@android:id/content"
        android:layout_width="match_parent" 
        android:layout_height="0dip"
        android:layout_weight="1"
        android:foregroundGravity="fill_horizontal|top"
        android:foreground="?android:attr/windowContentOverlay" />
</LinearLayout>

```

==首先根据当前的主题，加载默认资源和布局，根据相关的FEATURE获取资源布局layout文件。然后将布局添加到DecorView中，并且将contentParent与布局中id为ID_ANDROID_CONTENT的FrameLayout绑定。所以我们可以通过findViewById(com.android.internal.R.id.content)获取到contentView==。

![这里写图片描述](https://img-blog.csdn.net/20171117172557810?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## Window的类型

添加窗口是通过==WindowManagerGlobal==的addView方法操作的，这里有三个必要参数。view，params，display。
==display== : 表示要输出的显示设备。
==view== : 表示要显示的View，一般是对该view的上下文进行操作。(view.getContext())
==params== : 类型为WindowManager.LayoutParams，即表示该View要展示在窗口上的布局参数。其中有一个重要的参数type，用来表示窗口的类型。

```java
//WindowManagerGlobal


 public void addView(View view, ViewGroup.LayoutParams params,
            Display display, Window parentWindow) {
        if (view == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        if (!(params instanceof WindowManager.LayoutParams)) {
            throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
        }
``````
}

```

打开`WindowManager`类，看到静态内部类

​```java
//WindowManager

    public static class LayoutParams extends ViewGroup.LayoutParams implements Parcelable {
``````
}
```

可以看到在LayoutParams中，有2个比较重要的参数: ==flags==,==type==。
我们简要的分析一下flags,该参数表示==Window的属性==，它有很多选项，通过这些选项可以控制Window的显示特性，这里主要介绍几个比较常用的选项。

==FLAG_NOT_FOCUSABLE==
表示Window不需要获取焦点，也不需要接收各种输入事件，此标记会同时启用FLAG_NOT_TOUCH_MODAL，最终事件会直接传递给下层具有==焦点==的Window。

==FLAG_NOT_TOUCH_MODAL==
系统会将当前Window区域以外的单击事件传递给底层的Window，当前Window区域以内的单击事件则自己处理，这个标记很重要，一般来说都需要开启此标记，否则其他Window将无法接收到单击事件。

==FLAG_SHOW_WHEN_LOCKED==
开启此模式可以让Window显示在锁屏的界面上。

==Type==参数表示Window的类型，Window有三种类型，分别是==应用Window==、==子Window==、==系统Window==。==应用类Window对应着一个Activity。==     子Window不能单独存在，它需要附属在特定的父Window之中，比如常见的==PopupWindow==就是一个==子Window==  有些系统Window是需要声明==权限==才能创建的Window，比如==Toast==和==系统状态栏==这些都是==系统Window.==



### 应用窗口

==Activity 对应的窗口类型是应用窗口==， 所有 Activity 默认的窗口类型是 TYPE_BASE_APPLICATION。
WindowManager 的 LayoutParams 的默认类型是 TYPE_APPLICATION。 Dialog 并没有设置type，所以也是默认的窗口类型即 TYPE_APPLICATION

​```java
//WindowManager  LayoutParams的默认构造方法

public LayoutParams() {
    super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    type = TYPE_APPLICATION;
    format = PixelFormat.OPAQUE;
}

```

![image-20210527202931927](../../%E5%9B%BE%E5%BA%93/View%E7%9B%B8%E5%85%B3/image-20210527202931927-1622118580441.png)

### 子窗口

子窗口不能单独存在，它需要附属在特定的父Window之中，例如开篇第一张图，绿色框框即为popupWindow，它就是子窗口，类型一般为==TYPE_APPLICATION_PANEL。==之所以称为子窗口，即它的父窗口显示时，子窗口才显示。父窗口不显示，它也不显示。追随父窗口。
==那么问题又来了，我们能否在Acitivty的onCreate()中创建popupWindow并显示呢?==

会因为没有描点而发生错误

![image-20210527203152662](../../%E5%9B%BE%E5%BA%93/View%E7%9B%B8%E5%85%B3/image-20210527203152662-1622118719664.png)

### 系统窗口

==系统窗口跟应用窗口不同，不需要对应 Activity。跟子窗口不同，不需要有父窗口==。一般来讲，系统窗口应该由系统来创建的，例如发生异常，==ANR时的提示框==，又如系统状态栏，屏保等。但是，Framework 还是定义了一些，可以被应用所创建的系统窗口，如 TYPE_ TOAST，TYPE _INPUT _ METHOD，TYPE _WALLPAPTER 等等。

![image-20210527203708936](../../%E5%9B%BE%E5%BA%93/View%E7%9B%B8%E5%85%B3/image-20210527203708936-1622119041797.png)

那么，这个type层级到底有什么作用呢？ 
 Window是分层的，每个==Window都有对应的z-ordered==，（z轴，从1层层叠加到2999，你可以将屏幕想成三维坐标模式）==层级大的会覆盖在层级小的Window上面==。

在三类Window中，==应用Window的层级范围是1~99==。==子Window的层级范围是1000~1999==，==系统Window的层级范围是2000~2999==，这些层级范围对应着WindowManager.LayoutParams的type参数。如果想要Window位于所有Window的最顶层，那么采用较大的层级即可。另外有些==系统层级==的使用是需要声明==权限==的。

## Window的内部机制(Activity)

![这里写图片描述](https://img-blog.csdn.net/20171118161855485?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

Window是一个抽象的概念，每一个==Window都对应着一个View和一个ViewRootImpl==，Window和View通过ViewRootImpl来建立联系，因此Window并不是不存在的，它是以View的形式存在。这点从==ViewManager==的定义也可以看得出来，它只提供三个接口方法：addView、updateViewLayout、removeView，这些方法都是针对View的，所以说==Window是View的直接管理者==。

### Window的创建过程

首先要分析Window的创建过程，就必须了解Activity的启动过程。
Activity的启动过程很复杂，最终会由==ActivityThread==中的==handleLaunchActivity()==来完成整个启动过程。
在这个方法中	会通过performLaunchActivity()方法创建Activity，performLaunchActivity()内部通过类加载器创建Activity的实例对象，==并调用其attach()方法为其关联运行过程中所依赖的一系列上下文环境变量以及创建与绑定窗口==。
具体不在赘述，更多Activity启动细节请移步==Activity的启动过程==。

```java
//ActivityThread


private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
``````
    //获取WindowManagerService的Binder引用(proxy端)。
    WindowManagerGlobal.initialize();
    
    //会调用Activity的onCreate,onStart,onResotreInstanceState方法
    Activity a = performLaunchActivity(r, customIntent);
    if (a != null) {
        ``````
        //会调用Activity的onResume方法.
        handleResumeActivity(r.token, false, r.isForward,
                !r.activity.mFinished && !r.startsNotResumed, r.lastProcessedSeq, reason);
    
        ``````
    } 

}

```

​```java
//ActivityThread


   private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {

        //通过类加载器创建Activity
        Activity activity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent);

``````

        //通过LoadedApk的makeApplication方法来创建Application对象
        Application app = r.packageInfo.makeApplication(false, mInstrumentation);


        if (activity != null) {
            ``````
            activity.attach(appContext, this, getInstrumentation(), r.token,
                    r.ident, app, r.intent, r.activityInfo, title, r.parent,
                    r.embeddedID, r.lastNonConfigurationInstances, config,
                    r.referrer, r.voiceInteractor, window);
            ``````
    
            //onCreate
            mInstrumentation.callActivityOnCreate(activity, r.state);
    
            //onStart
            activity.performStart();
    
        }
        return activity;
    }

```

在Activity的==attach()==方法里，系统会创建Activity所属的Window对象并为其设置回调接口，由于==Activity实现了Window的Callback接口==，因此当Window接收到外界的状态改变时就会回调Activity的方法。Callback接口中的方法很多，下面举几个比较眼熟的方法
​```java
 public interface Callback {

        public boolean dispatchTouchEvent(MotionEvent event);

        public View onCreatePanelView(int featureId);

        public boolean onMenuItemSelected(int featureId, MenuItem item);

        public void onContentChanged();

        public void onWindowFocusChanged(boolean hasFocus);

        public void onAttachedToWindow();

        public void onDetachedFromWindow();
    }

```

```java
//Activity


final void attach(``````) {
        //绑定上下文
        attachBaseContext(context);

        //创建Window,PhoneWindow是Window的唯一具体实现类
        mWindow = new PhoneWindow(this, window);//此处的window==null，但不影响
        mWindow.setWindowControllerCallback(this);
        mWindow.setCallback(this);
``````
        //设置WindowManager
        mWindow.setWindowManager(
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
                mToken, mComponent.flattenToString(),
                (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
        if (mParent != null) {
            mWindow.setContainer(mParent.getWindow());
        }
        //创建完后通过getWindowManager就可以得到WindowManager实例
        mWindowManager = mWindow.getWindowManager();//其实它是WindowManagerImpl
    
    }


    @Override
    public Object getSystemService(@ServiceName @NonNull String name) {
        ``````
        if (WINDOW_SERVICE.equals(name)) {
            return mWindowManager;
        } 
        return super.getSystemService(name);
    }

```

等，等一下。看到这里是不是有点懵，Activity的getSystemService根本没有创建WindowManager，那么mWindow.setWindowManager()设置的岂不是空的WindowManager，那这样它的下一步mWindowManager = mWindow.getWindowManager()岂不是无线空循环？

因为PhoneWindow中并没有setWindowManager()方法，所以我们打开Window类看看。

​````java
//Window


    public void setWindowManager(WindowManager wm, IBinder appToken, String appName,
            boolean hardwareAccelerated) {
        mAppToken = appToken;
        mAppName = appName;
        mHardwareAccelerated = hardwareAccelerated
                || SystemProperties.getBoolean(PROPERTY_HARDWARE_UI, false);
        if (wm == null) {
            wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        }

        //在此处创建mWindowManager 
        mWindowManager = ((WindowManagerImpl)wm).createLocalWindowManager(this);
    }



//在WindowManagerImpl类中
    public WindowManagerImpl createLocalWindowManager(Window parentWindow) {
        return new WindowManagerImpl(mContext, parentWindow);
    }

````

类似于PhoneWindow和Window的关系，WindowManager是一个接口，具体的实现是WindowManagerImpl。



到了这里Window已经创建完毕，在上面的performLaunchActivity()方法中我们可以看到调用了onCreate()方法：

```java
//ActivityThread --> performLaunchActivity

            mInstrumentation.callActivityOnCreate(activity, r.state);
```

在==Activity的onCreate==方法里，我们通过setContentView()将view添加到DecorView的mContentParent中，也就是将资源布局文件和phoneWindow关联。
所以在==PhoneWindow==的最后，因为Activity实现了Callback接口，便可以通过下面代码通知Activity xml 布局文件已经添加到DecorView上。

```java
//PhoneWindow  -->  setContentView
callback.onContentChanged();
```

经过了上面几个过程,Window和DecorView已经被创建并初始化完毕，Activity的布局文件也成功添加到了DecorView的mContentParent中，但这个时候的DecorView还没有被WindowManager正式添加到Window中。

这里需要理解的是，Window更多表示的是一种抽象功能集合，虽然说早在Activity的attach方法中window就已经被创建了，但是这个时候由于DecorView并没有被WindowManager识别，所以这个时候的Window暂时无法提供具体功能。
.
总的来说，==UI 布局成功添加到 Window 并可用有2个标志== ：
.
①==View绘制完毕==，可以呈现给用户。
②==View可以接收外界信息==（触摸事件等）。

### Window的添加过程

PhoneWindow 只是负责处理一些应用窗口通用的逻辑(==设置标题栏，导航栏等==)。但是真正完成把一个 View，作为窗口添加到 WmS  的过程是由 ==WindowManager== 来完成的。WindowManager 的具体实现是 ==WindowManagerImpl==。

下面我们继续来分析`handleLaunchActivity()`方法中`handleResumeActivity()`的执行过程。

```java
//ActivityThread


 final void handleResumeActivity(IBinder token,
            boolean clearHide, boolean isForward, boolean reallyResume, int seq, String reason) {

        //把activity数据记录更新到ActivityClientRecord
        ActivityClientRecord r = mActivities.get(token);

        r = performResumeActivity(token, clearHide, reason);

        if (r != null) {

            if (r.window == null && !a.mFinished && willBeVisible) {
                r.window = r.activity.getWindow();
                View decor = r.window.getDecorView();
                decor.setVisibility(View.INVISIBLE);//不可见
                ViewManager wm = a.getWindowManager();
                WindowManager.LayoutParams l = r.window.getAttributes();
                a.mDecor = decor;
                l.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;

``````
                if (a.mVisibleFromClient && !a.mWindowAdded) {
                    a.mWindowAdded = true;
                    wm.addView(decor, l);//把decor添加到窗口上（划重点）
                }
    
            } 
                //屏幕参数发生了改变
                performConfigurationChanged(r.activity, r.tmpConfig);
    
                WindowManager.LayoutParams l = r.window.getAttributes();
    
                    if (r.activity.mVisibleFromClient) {
                        ViewManager wm = a.getWindowManager();
                        View decor = r.window.getDecorView();
                        wm.updateViewLayout(decor, l);//更新窗口状态
                    }


                ``````
                if (r.activity.mVisibleFromClient) {
                    //已经成功添加到窗口上了（绘制和事件接收），设置为可见
                    r.activity.makeVisible();
                }


            //通知ActivityManagerService，Activity完成Resumed
             ActivityManagerNative.getDefault().activityResumed(token);
        } 
    }

```

在上面代码中，首先配置==ActivityClientRecord==，之后将DecorView设置为==INVISIBLE==，因为View并未绘制完成，当前的==DecorView==只是一个有结构的==空壳==
然后通过==WindowManagerImpl==将DecorView正式的添加到窗口上==wm.addView(decor, l)==;，这一步非常非常重要，因为它包括了2个比较重要和常见的过程：

==Window的添加过程==和==View的绘制流程==。

灵魂画手一出手就知有没有~~下面画一张图来看看整体添加流程，留一个大致的概括印象。

![这里写图片描述](https://img-blog.csdn.net/20171118225453157?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

窗口的添加过程如上图所示，我们知道 WmS 运行在单独的进程中。这里 ==IWindowSession==执行的 ==addtoDisplay ==操作应该是 ==IPC ==调用。接下来的Window添加过程，我们会知道每个应用窗口创建时，最终都会创建一个 ViewRootImpl 对象。

ViewRootImpl 是一很重要的类，==类似 ApplicationThread 负责跟AmS通信一样==，ViewRootImpl 的一个重要==职责==就是跟 ==WmS 通信==，它通静态变量 sWindowSession（IWindowSession实例）与 WmS 进行通信。

每个应用进程，==仅有==一个 ==sWindowSession== 对象，它对应了 WmS 中的 Session 子类，WmS 为每一个应用进程分配一个 Session 对象。==WindowState== 类有一个 IWindow mClient 参数，是由 Session 调用 addToDisplay 传递过来的，对应了 ViewRootImpl 中的 W 类的实例。

简单的总结一下，ViewRootImpl通过IWindowSession远程IPC通知WmS，并且由W类接收WmS的远程IPC通知。（这个W类和ActivityThread的H类同样精悍的命名，并且也是同样的工作职责！）

图解完Window的添加过程，对整个流程有一个印象和思路，那么下面继续分析源码。
在上面的handleResumeActivity()方法中，我们看到源码通过wm.addView(decor, l);操作DecorView和WindowManager.LayoutParams。上面讲解也说过，因为WindowManager是接口，真正具体实现类是windowManagerImpl。如果Window中类的关系还不太清楚的可以再回到上面的「Window的内部机制」看图解。

​```java
//WindowManagerImpl


    private final WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();

    @Override
    public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
    }

    @Override
    public void updateViewLayout(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.updateViewLayout(view, params);
    }

    @Override
    public void removeView(View view) {
        mGlobal.removeView(view, false);
    }

```

我们看到这个==WindowManagerImpl==原来也是一个吃空饷的家伙！对于Window(或者可以说是View)的操作都是交由==WindowManagerGlobal==来处理，==WindowManagerGlobal==以==工厂==的形式向外提供自己的实例。这种工作模式是==桥接模式==，将所有的操作==全部委托给WindowManagerGlobal==来实现。

在WindowManagerImpl的全局变量中通过单例模式初始化了WindowManagerGlobal，也就是说==一个进程==就==只有==一个==WindowManagerGlobal==对象
```java
//WindowManagerGlobal


   public void addView(View view, ViewGroup.LayoutParams params,
            Display display, Window parentWindow) {
        if (view == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        if (!(params instanceof WindowManager.LayoutParams)) {
            throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
        }


        final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams) params;
        if (parentWindow != null) {
            //调整布局参数，并设置token
            parentWindow.adjustLayoutParamsForSubWindow(wparams);
        } 

        ViewRootImpl root;
        View panelParentView = null;

        synchronized (mLock) {


            int index = findViewLocked(view, false);
            if (index >= 0) {
                if (mDyingViews.contains(view)) {
                    //如果待删除的view中有当前view，删除它
                    // Don't wait for MSG_DIE to make it's way through root's queue.
                    mRoots.get(index).doDie();
                }
                // The previous removeView() had not completed executing. Now it has.
                //之前移除View并没有完成删除操作，现在正式删除该view
            }

            //如果这是一个子窗口个(popupWindow)，找到它的父窗口。
            //最本质的作用是使用父窗口的token(viewRootImpl的W类，也就是IWindow)
            if (wparams.type >= WindowManager.LayoutParams.FIRST_SUB_WINDOW &&
                    wparams.type <= WindowManager.LayoutParams.LAST_SUB_WINDOW) {
                final int count = mViews.size();
                for (int i = 0; i < count; i++) {
                    if (mRoots.get(i).mWindow.asBinder() == wparams.token) {
                        panelParentView = mViews.get(i);
                    }
                }
            }
            //创建ViewRootImpl，并且将view与之绑定
            root = new ViewRootImpl(view.getContext(), display);

            view.setLayoutParams(wparams);

            mViews.add(view);//将当前view添加到mViews集合中
            mRoots.add(root);//将当前ViewRootImpl添加到mRoots集合中
            mParams.add(wparams);//将当前window的params添加到mParams集合中
        }

``````
            //通过ViewRootImpl的setView方法，完成view的绘制流程，并添加到window上。
            root.setView(view, wparams, panelParentView);
    }

```

在上面代码中有2个比较重要的知识点

1、在WindowManagerGlobal中有如下几个重要的集合

​```java
 //存储所有Window对应的View
    private final ArrayList<View> mViews = new ArrayList<View>();

    //存储所有Window对应的ViewRootImpl
    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();

    //存储所有Window对应的布局参数
    private final ArrayList<WindowManager.LayoutParams> mParams =
            new ArrayList<WindowManager.LayoutParams>();

    //存储正被删除的View对象（已经调用removeView但是还未完成删除操作的Window对象）     
    private final ArraySet<View> mDyingViews = new ArraySet<View>();

```

==token==
在源码中token一般代表的是==Binder==对象，作用于IPC进程间数据通讯。并且它也包含着此次通讯所需要的信息，在ViewRootImpl里，token用来表示mWindow(W类，即IWindow)，并且在WmS中只有符合要求的token才能让Window正常显示。所以上面提出的问题
我们能否在Acitivty的onCreate()中创建popupWindow并显示呢？
就与之有关，我们暂时把token放一边，走完Window的添加过程先。(最后分析token)

言归正传，我们继续看代码。 
 在`WindowManagerGlobal`的`addView()`方法里，最后调用`ViewRootImpl`的`setView`方法，处理添加过程。

```java
//WindowManagerGlobal  -->  addView


            //创建ViewRootImpl，并且将view与之绑定
            root = new ViewRootImpl(view.getContext(), display);

            //通过ViewRootImpl的setView方法，完成view的绘制流程，并添加到window上。
            root.setView(view, wparams, panelParentView);

```

通过上面这个代码可知，==WindowManagerGlobal==`将View的处理操作全权交给`ViewRootImpl`，而且上面我们也提到了，View成功添加到Window，无非就是展现视图和用户交互。

①View绘制完毕，可以呈现给用户。 
 ②View可以接收外界信息（触摸事件等）。

在`ViewRootImpl`的`setView()`方法中，将会完成上面2个艰巨而又伟大的任务

```java
//ViewRootImpl 


  public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {

                int res; 

                 //在 Window add之前调用，确保 UI 布局绘制完成 --> measure , layout , draw
                requestLayout();//View的绘制流程 

                if ((mWindowAttributes.inputFeatures
                        & WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL) == 0) {
                    //创建InputChannel
                    mInputChannel = new InputChannel();
                }

                try {

                    //通过WindowSession进行IPC调用，将View添加到Window上
                    //mWindow即W类，用来接收WmS信息
                    //同时通过InputChannel接收触摸事件回调
                    res = mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes,
                            getHostVisibility(), mDisplay.getDisplayId(),
                            mAttachInfo.mContentInsets, mAttachInfo.mStableInsets,
                            mAttachInfo.mOutsets, mInputChannel);
                }

``````

                    //处理触摸事件回调
                    mInputEventReceiver = new WindowInputEventReceiver(mInputChannel,
                            Looper.myLooper());
    
                ``````
    }

```

在ViewRootImpl的==setView==()方法里，执行requestLayout()方法完成View的绘制流程，并且通过WindowSession将View和InputChannel添加到WmS中，从而将View添加到Window上并且接收触摸事件。

通过==mWindowSession==来完成Window的添加过程 ，mWindowSession的类型是IWindowSession，是一个Bindler对象，真正的实现类是Session，也就是Window的添加是一次IPC调用。(mWindowSession在ViewRootImpl的构造函数中通过WindowManagerGlobal.getWindowSession();创建)

同时将mWindow（即 W extends IWindow.Stub）发送给WmS，用来接收WmS信息。

![这里写图片描述](https://img-blog.csdn.net/20171119153756840?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

​```java
ession


    final WindowManagerService mService;

   @Override
    public int addToDisplay(IWindow window, int seq, WindowManager.LayoutParams attrs,
            int viewVisibility, int displayId, Rect outContentInsets, Rect outStableInsets,
            Rect outOutsets, InputChannel outInputChannel) {

        return mService.addWindow(this, window, seq, attrs, viewVisibility, displayId,
                outContentInsets, outStableInsets, outOutsets, outInputChannel);
    }

```

如此一来，==Window==的添加请求就交给==WmS==去处理了，在WmS内部会为每一个应用保留一个单独的==Session==。在WmS 端会创建一个WindowState对象用来表示当前添加的窗口。 WmS负责管理这里些 WindowState 对象。至此，Window的添加过程就结束了。

至于Window的删除和更新过程，举一反三，也是使用WindowManagerGlobal对ViewRootImpl的操作，最终也是通过Session的IPC跨进程通信通知到WmS。整个过程的本质都是同出一辙的![这里写图片描述](https://img-blog.csdn.net/20171119153235527?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### Window的token

在WindowManager的==LayoutParams==中，与==type==同等==重要==的还有==token==。

上面说到：在源码中token一般代表的是Binder对象，作用于IPC进程间数据通讯。并且它也==包含==着此次通讯所需要的==信息==，在ViewRootImpl里，token用来表示mWindow(W类，即IWindow)，并且在WmS中只有符合要求的token才能让Window正常显示。

先剧透一下，在Window中，token(LayoutParams中的token)分为以下2种情况

    应用窗口 : token表示的是activity的mToken(ActivityRecord)
    子窗口 : token表示的是父窗口的W对象，也就是mWindow(IWindow)
------------------------------------------------
### Activity的`attach()`

我们通过源码分析token的流程，顺带再巩固一遍Window的创建流程。 
 在Activity的`attach()`方法中

```java
//Activity


 final void attach(Context context, ActivityThread aThread,
            Instrumentation instr, IBinder token, int ident,
            Application application, Intent intent, ActivityInfo info,
            CharSequence title, Activity parent, String id,
            NonConfigurationInstances lastNonConfigurationInstances,
            Configuration config, String referrer, IVoiceInteractor voiceInteractor,
            Window window) {

        attachBaseContext(context);

        mToken = token;//ActivityRecord

        mWindow = new PhoneWindow(this, window);

``````
        mWindow.setWindowManager(
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
                mToken, mComponent.flattenToString(),
                (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
        if (mParent != null) {
            mWindow.setContainer(mParent.getWindow());
        }
        mWindowManager = mWindow.getWindowManager();
    }

```

### 创建`windowManager`

在Window中创建`windowManager`，并且全局变量`mAppToken`就是上面代码传入的mToken(`ActivityRecord`)

​```java
//Window

    private IBinder mAppToken;//Activity的mToken

    public void setWindowManager(WindowManager wm, IBinder appToken, String appName,
            boolean hardwareAccelerated) {

        mAppToken = appToken;

        mWindowManager = ((WindowManagerImpl)wm).createLocalWindowManager(this);
    }

```

```java
//WindowManagerImpl

    public WindowManagerImpl createLocalWindowManager(Window parentWindow) {

        //parentWindow就是activity的Window
        return new WindowManagerImpl(mContext, parentWindow);
    }

    @Override
    public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
    }

```

我们拿出上面的关系图，目前已经分析了`WindowManagerImpl`，接下来就是`WindowManagerGlobal`的`addView()`方法。![这里写图片描述](https://img-blog.csdn.net/20171118225453157?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### WindowManagerGlobal

````java
//WindowManagerGlobal


public void addView(View view, ViewGroup.LayoutParams params,
            Display display, Window parentWindow) {

``````
```java
    final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams) params;

    //parentWindow就是Acitivty的Window
    if (parentWindow != null) {
        //①调整布局参数，并设置token
        parentWindow.adjustLayoutParamsForSubWindow(wparams);
    } 
```


```java
    ViewRootImpl root;
    View panelParentView = null;

    synchronized (mLock) {
        //②如果这是一个子窗口个(popupWindow)，找到它的父窗口。
        //②最本质的作用是使用父窗口的token(viewRootImpl的W类，也就是IWindow)
        if (wparams.type >= WindowManager.LayoutParams.FIRST_SUB_WINDOW &&
                wparams.type <= WindowManager.LayoutParams.LAST_SUB_WINDOW) {
            final int count = mViews.size();
            for (int i = 0; i < count; i++) {
                if (mRoots.get(i).mWindow.asBinder() == wparams.token) {
                    panelParentView = mViews.get(i);
                    //从mRoots找到相同的W类，再从mViews找到父View
                }
            }
        }

        //③创建ViewRootImpl，并且将view与之绑定
        root = new ViewRootImpl(view.getContext(), display);

        view.setLayoutParams(wparams);

        mViews.add(view);
        mRoots.add(root);
        mParams.add(wparams);
    }
        //④通过ViewRootImpl的setView方法，完成view的绘制流程，并添加到window上。
        root.setView(view, wparams, panelParentView);
}
```

````java

我们一步一步分析，先看①，parentWindow.adjustLayoutParamsForSubWindow(wparams);

​```java
//Window


    void adjustLayoutParamsForSubWindow(WindowManager.LayoutParams wp) {

        //如果它是子窗口
        if (wp.type >= WindowManager.LayoutParams.FIRST_SUB_WINDOW &&
                wp.type <= WindowManager.LayoutParams.LAST_SUB_WINDOW) {
            if (wp.token == null) {
                View decor = peekDecorView();
                if (decor != null) {
                    wp.token = decor.getWindowToken();//分析View的getWindowToken
                }
            }

        }
``````
```java
    else {
        //应用窗口（dialog的情况）
        //此时的wp.token即为mAppToken
        if (wp.token == null) {
            wp.token = mContainer == null ? mAppToken : mContainer.mAppToken;
        }
    }

}
```

```java

我们看到如果是应用窗口，wp.token==null的情况，就会给他赋值`mAppToken`，而这个`mAppToken`就是我们上面在Activity的`attach()`方法中传入的`mToken`!

我们再分析子窗口的token，接上面的`decor.getWindowToken()`

​```java
//View 

    AttachInfo mAttachInfo;
    /**
     * Retrieve a unique token identifying the window this view is attached to.
     * @return Return the window's token for use in
     * {@link WindowManager.LayoutParams#token WindowManager.LayoutParams.token}.
     */
    public IBinder getWindowToken() {
        return mAttachInfo != null ? mAttachInfo.mWindowToken : null;
    }

```

### ViewRootImpl

那么View的mAttachInfo又是如何赋值的呢？ 
 我们接着看第③root = new ViewRootImpl(view.getContext(), display);

````java
//ViewRootImpl类


    public ViewRootImpl(Context context, Display display) {
        mWindowSession = WindowManagerGlobal.getWindowSession();

        mWindow = new W(this);//W是用于接收WmS通知的

        mAttachInfo = new View.AttachInfo(mWindowSession, mWindow, display, this, mHandler, this);

``````
    }


//View类


```java
    AttachInfo(IWindowSession session, IWindow window, Display display,
            ViewRootImpl viewRootImpl, Handler handler, Callbacks effectPlayer) {
        mSession = session;
        mWindow = window;
        mWindowToken = window.asBinder();//即W类
        mDisplay = display;
        mViewRootImpl = viewRootImpl;
        mHandler = handler;
        mRootCallbacks = effectPlayer;
    }
```

````java

那么，如果这个View是ViewGroup，如何确保它的所有子View都能调用==getWindowToken==()获取到==mAttachInfo==的==mWindowToken==呢？

这里我们涉及一点View的绘制流程看一下源码

​```java
// ViewRootImpl


    private void performTraversals() {
``````
```java
    if (mFirst) {

        //第一次执行performTraversals时，会把自己的mAttachInfo关联到所有的子View
        host.dispatchAttachedToWindow(mAttachInfo, 0);
    }
}
```


//ViewGroup.java

```java
void dispatchAttachedToWindow(AttachInfo info, int visibility) {

   final int count = mChildrenCount;
   final View[] children = mChildren;
   for (int i = 0; i < count; i++) {
       final View child = children[i];
       //关联到所有子View
       child.dispatchAttachedToWindow(info,visibility | (child.mViewFlags & VISIBILITY_MASK));
  }
}
```


//View.java

~~~java
void dispatchAttachedToWindow(AttachInfo info, int visibility) {
   mAttachInfo = info;
    ``````
 }   
~~~

```java

### Session

循序渐进，我们走到Session

​```java
 @Override
    public int addToDisplay(IWindow window, int seq, WindowManager.LayoutParams attrs,
            int viewVisibility, int displayId, Rect outContentInsets, Rect outStableInsets,
            Rect outOutsets, InputChannel outInputChannel) {

        //window是在viewRootImpl创建的时候创建的，用来接收WmS消息
        //mWindow = new W(this); 不要和token搞混了！

        return mService.addWindow(this, window, seq, attrs, viewVisibility, displayId,
                outContentInsets, outStableInsets, outOutsets, outInputChannel);
    }

```

### WMS

最终，我们走进大魔王 ~~faker~~ WindowManagerService看看它到底是如何处理应用窗口以及子窗口。

```java
//WmS


    public int addWindow(Session session, IWindow client, int seq,
            WindowManager.LayoutParams attrs, int viewVisibility, int displayId,
            Rect outContentInsets, Rect outStableInsets, Rect outOutsets,
            InputChannel outInputChannel) {

    //根据type检查窗口类型是否合法，如果是系统窗口类型，还需要进行权限检查
    int res = mPolicy.checkAddPermission(attrs, appOp);

``````

~~~java
//如果是子窗口，根据attrs.token(也就是我们说的Window的token)检查父窗口是否合法
if (type >= FIRST_SUB_WINDOW && type <= LAST_SUB_WINDOW) {
            attachedWindow = windowForClientLocked(null, attrs.token, false);
            if (attachedWindow == null) {
                //父窗口不存在，直接抛出异常
                 return WindowManagerGlobal.ADD_BAD_SUBWINDOW_TOKEN;
             }
             //如果父窗口也是子窗口，GG
             if (attachedWindow.mAttrs.type >= FIRST_SUB_WINDOW
                   && attachedWindow.mAttrs.type <= LAST_SUB_WINDOW) {

                return WindowManagerGlobal.ADD_BAD_SUBWINDOW_TOKEN;
             }
  }
    ``````

//经过一系列的检查之后，最后会生成窗口在WmS中的表示WindowState，并且把LayoutParams赋值给WindowState的mAttrs
win = new WindowState(this, session, client, token, attachedWindow, appOp[0], seq, attrs, viewVisibility, displayContent);  

`````` 
if (addToken) {
 mTokenMap.put(attrs.token, token);
} 
``````
//窗口添加成功，W类存放至mWindowMap
 mWindowMap.put(client.asBinder(), win);   

}
~~~



```java
final WindowState windowForClientLocked(Session session, IBinder client,
        boolean throwOnError) {

    //client为attrs.token，即父类的W类对象
    //如果父类窗口添加成功，那么mWindowMap必定有此client
    WindowState win = mWindowMap.get(client);

    if (win == null) {
        RuntimeException ex = new IllegalArgumentException(
                "Requested window " + client + " does not exist");
        if (throwOnError) {
            throw ex;//抛出异常
        }
        return null;
    }
    if (session != null && win.mSession != session) {
        RuntimeException ex = new IllegalArgumentException(
                "Requested window " + client + " is in session " +
                win.mSession + ", not " + session);
        if (throwOnError) {
            throw ex;
        }
        Slog.w(TAG_WM, "Failed looking up window", ex);
        return null;
    }

    return win;
}
```

```java

如果子窗口不合法（没有父窗口或者父窗口也是子窗口），那么将会抛出`ADD_BAD_SUBWINDOW_TOKEN`的异常，我们试试在Activity的onCreate()方法中去创建并显示一个popupWindow，至于崩溃原因结合上面所分析的，就能找出原因了。![这里写图片描述](https://img-blog.csdn.net/20171119213327251?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

​```java
iewRootImpl


    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
                    res = mWindowSession.addToDisplay(``````);

``````
```java
                switch (res) {
                    case WindowManagerGlobal.ADD_BAD_APP_TOKEN:
                    case WindowManagerGlobal.ADD_BAD_SUBWINDOW_TOKEN:
                        throw new WindowManager.BadTokenException(
                                "Unable to add window -- token " + attrs.token
                                + " is not valid; is your activity running?");
                }

        }
```

```

所以无论是应用窗口，还是子窗口，token 不能为空。否则会抛出异常，并且应用窗口的token 必须是某个 Activity 的  mToken，子窗口的token 必须是父窗口的 IWindow 对象。而且子窗口还需等父窗口添加成功之后才可添加到Window上。

## 总结

至此，Window的机制已经探索的有些眉目，阅读源码read the fuck source code能够更好的了解到Android的各种机制流程，而且能够熟悉源码编写的码神他们的编写风格，例如performLaunchActivity()就是Activity的整体流程开始，performTraversals（）就是开始绘制View，还有performMeasure()、performLayout()、performDraw()等等，并且探索了Window机制后，我们就能比较容易的上手View的绘制流程。

```

# ==View的工作流程==





![ScreenClip](../../%E5%9B%BE%E5%BA%93/View%E7%9B%B8%E5%85%B3/ScreenClip.png)

==ViewRootImpl==在整个View体系中=起=着中流砥柱的作用，它是控件树正常运作的动力所在，并且有如下几个重要功能点。

```java
连接WindowManager和DecorView的纽带。
向DecorView派发输入事件
完成View的绘制(measure,layout,draw)。
负责与WMS交互通讯，调整窗口大小及布局。
```
## ViewRootImpl



![ScreenClip](../../%E5%9B%BE%E5%BA%93/View%E7%9B%B8%E5%85%B3/ScreenClip-1623318296259.png)







![这里写图片描述](https://img-blog.csdn.net/20171128171005304?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

我们沿用[Window机制探索](http://blog.csdn.net/qian520ao/article/details/78555397)中Window的添加流程图，我们所要分析的绘制机制，便从ViewRootImpl的`setView()`方法展开。 

```java
//ViewRootImpl 


    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
                // Schedule the first layout -before- adding to the window
                // manager, to make sure we do the relayout before receiving
                // any other events from the system.
                requestLayout();
    }


    @Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();//检查是否在主线程
            mLayoutRequested = true;//mLayoutRequested 是否measure和layout布局。
            scheduleTraversals();
        }
    }


    void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();

            //post一个runnable处理-->mTraversalRunnable
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
``````
        }
    }


    final class TraversalRunnable implements Runnable {
        @Override
        public void run() {
            doTraversal();
        }
    }
    final TraversalRunnable mTraversalRunnable = new TraversalRunnable();



    void doTraversal() {
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
    
            performTraversals();//View的绘制流程正式开始。
    }

```

在`scheduleTraversals`方法中，通过`mChoreographer`发送一个`runnable`，在`run()`方法中去处理绘制流程。更多可以查看[Android 屏幕刷新机制](https://blog.csdn.net/qian520ao/article/details/80954626)

## performTraversals

==ViewRootImpl==在其创建过程中通过==requestLayout==()向主线程发送了一条触发遍历操作的消息，遍历操作是指==performTraversals==()方法。它是一个包罗万象的方法。==ViewRootImpl==中接收的==各种变化==，如来自==WmS==的窗口属性变化、来自==控件树的尺寸变化==及==重绘请求==等都引发==performTraversals()==的调用，并在其中完成处理。View类及其子类中的onMeasure()、onLayout()、onDraw()等回调也都是在performTraversals()的执行过程中==直接或间接==的引发。也正是如此，一次次的performTraversals()调用驱动着控件树有条不紊的工作，一旦此方法无法正常执行，整个控件树都将处于僵死状态。因此==performTraversals()==函数可以说是ViewRootImpl的心跳。

![View首次绘制流程](https://img-blog.csdn.net/20171130105250728?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## View首次绘制流程

首先我们看一下`performTraversals()`首次绘制的大致流程，如上图所示：`performTraversals()`会一次调用performMeasure、performLayout、performDraw三个方法，这三个方法便是View绘制流程的精髓所在。

==performMeasure==会调用measure方法，在measure方法中又会调用onMeasure方法，在onMeasure方法中则会对所有的子元素进行measure过程，这个时候measure流程就从父容器传到子元素中了，这样就完成了一次measure过程。measure完成以后，可以通过getMeasuredWidth和getMeasureHeight方法来获取到View测量后的宽高。

==performLayout==: 和performMeasure同理。Layout过程决定了View的四个顶点的坐标和实际View的宽高，完成以后，可以通过getTop/Bottom/Left/Right拿到View的四个顶点位置，并可以通过getWidth和getHeight方法来拿到View的最终宽高。

==performDraw==: 和performMeasure同理，唯一不同的是，performDraw的传递过程是在draw方法中通过dispatchDraw来实现的。Draw过程则决定了View的显示，只有draw方法完成以后View的内容才能呈现在屏幕上。

我们知道，View的绘制流程是从顶级View也就是DecorView「ViewGroup」开始，一层一层从ViewGroup至子View遍历测绘，我们先纵观performTraversals()全局，认识View绘制的一个整体架构，后面我们会补充说明部分重要代码。如 : view.invalidate、view.requestLayout、view.post(runnable)等

​```java
//ViewRootImpl


    private void performTraversals() {

            //调用performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            measureHierarchy(host, lp, res,desiredWindowWidth, desiredWindowHeight);

            performLayout(lp, mWidth, mHeight);

            performDraw();
    }

```

## measure

### MeasureSpec

View的测量过程中，还需要理解MeasureSpec，MeasureSpec决定了一个View的尺寸规格，并且View的MeasureSpec受自身的LayoutParams(一般是xml布局中width和height)和父容器MeasureSpec的影响。

MeasureSpec代表一个32位的int值，高2位代表SpecMode，低30位代表SpecSize。

    SpecMode ： 测量模式，有UNSPECIFIED、 EXACTLY、AT_MOST三种。
    SpecSize : 在某种测量模式下的尺寸和大小。



SpecMode 有如下三种取值:

    UNSPECIFIED : 父容器对子View的尺寸不作限制，通常用于系统内部。（listView和scrollView等）
    
    EXACTLY : SpecSize 表示View的最终大小，因为父容器已经检测出View所需要的精确大小，它对应LayoutParams中的match_parent和具体的数值这两种模式。
    
    AT_MOST : SpecSize 表示父容器的可用大小，View的大小不能大于这个值。它对应LayoutParams中的wrap_content。
------------------------------------------------
### MeasureSpec和LayoutParams

子*V**i**e**w*的*M**e**a**s**u**r**e**S**p**e**c*==*L**a**y**o**u**t**P**a**r**a**m**s*+*m**a**r**g**i**n*+*p**a**d**d**i**n**g*+父容器的*M**e**a**s**u**r**e**S**p**e**c*



对于普通View（==DecorView==略有不同），其`MeasureSpec`由父容器的==MeasureSpec==和自身的==LayoutParams==共同决定，`MeasureSpec`一旦确定后，`onMeasure`中就可以确定View的测量宽高。

那么针对不同父容器和View本身不同的`LayoutParams`，View就可以有多种`MeasureSpec`，我们从子View的角度来分析。

1. ①View宽/高采用 固定宽高 的时候，不管父容器的`MeasureSpec`是什么，View的`MeasureSpec`都是EXACTLY并且其大小遵循`LayoutParams`中的大小。
2. ②View宽/高采用 wrap_content 的时候 ，不管父容器的模式是EXACTLY还是AT_MOST，View的模式总是AT_MOST，并且大小不能超过父容器的剩余空间（SpecSize,可用大小）。
3. ③View宽/高采用 match_parent 的时候 : 
     I 如果父容器的模式是EXACTLY，那么View也是EXACTLY并且其大小是父容器的剩余空间（SpecSize,最终大小）； 
     II 如果父容器的模式是AT_MOST，那么View也是AT_MOST并且其大小不会超过父容器的剩余空间（SpecSize,可用大小）。![这里写图片描述](https://img-blog.csdn.net/20171129132939644?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## DecorView

有了上面的理论知识铺垫之后，我们来看一下DecorView的measure过程。

### performTraversals

```java
//ViewRootImpl


   private void performTraversals() {
        final View host = mView;

        int desiredWindowWidth;//decorView宽度
        int desiredWindowHeight;//decorView高度

        if (mFirst) {
            if (shouldUseDisplaySize(lp)) {
                //窗口的类型中有状态栏和，所以高度需要减去状态栏
                Point size = new Point();
                mDisplay.getRealSize(size);
                desiredWindowWidth = size.x;
                desiredWindowHeight = size.y;
            } else {
                //窗口的宽高即整个屏幕的宽高
                Configuration config = mContext.getResources().getConfiguration();
                desiredWindowWidth = dipToPx(config.screenWidthDp);
                desiredWindowHeight = dipToPx(config.screenHeightDp);
            }

            //在onCreate中view.post(runnable)和此方法有关
            host.dispatchAttachedToWindow(mAttachInfo, 0);
        }


        boolean layoutRequested = mLayoutRequested && (!mStopped || mReportNextDraw);
        if (layoutRequested) {
``````
            //创建了DecorView的MeasureSpec，并调用performMeasure
             measureHierarchy(host, lp, res,desiredWindowWidth, desiredWindowHeight);
        }

```

在`ViewRootImpl`中，我们如上分析一下主要代码，在`measureHierarchy()`方法中，创建了DecorView的`MeasureSpec`。

#### measureHierarchy

​```java
//ViewRootImpl


    private boolean measureHierarchy(final View host, final WindowManager.LayoutParams lp,
            final Resources res, final int desiredWindowWidth, final int desiredWindowHeight) {

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        boolean windowSizeMayChange = false;

        boolean goodMeasure = false;

        //针对设置WRAP_CONTENT的dialog，开始协商,缩小布局参数
        if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // On large screens, we don't want to allow dialogs to just
            // stretch to fill the entire width of the screen to display
            // one line of text.  First try doing the layout at a smaller
            // size to see if it will fit.        

            int baseSize = 0;
            if (mTmpValue.type == TypedValue.TYPE_DIMENSION) {
                baseSize = (int)mTmpValue.getDimension(packageMetrics);
            }

                childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
                childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);

                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);

                goodMeasure = true;
        }

``````

        if (!goodMeasure) {//DecorView,宽度基本都为match_parent
            childWidthMeasureSpec = getRootMeasureSpec(desiredWindowWidth, lp.width);
            childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
    
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
    
            if (mWidth != host.getMeasuredWidth() || mHeight != host.getMeasuredHeight()) {
                windowSizeMayChange = true;
            }
        }


        return windowSizeMayChange;
    }


    //创建measureSpec
    private static int getRootMeasureSpec(int windowSize, int rootDimension) {
        int measureSpec;
        switch (rootDimension) {
    
        case ViewGroup.LayoutParams.MATCH_PARENT:
            // Window can't resize. Force root view to be windowSize.
            measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.EXACTLY);
            break;
        case ViewGroup.LayoutParams.WRAP_CONTENT:
            // Window can resize. Set max size for root view.
            measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.AT_MOST);
            break;
        default:
            // Window wants to be an exact size. Force root view to be that size.
            measureSpec = MeasureSpec.makeMeasureSpec(rootDimension, MeasureSpec.EXACTLY);
            break;
        }
        return measureSpec;
    }
```

==measureHierarchy==()用于测量整个控件树，传入的参数==desiredWindowWidth==和==desiredWindowHeight==在前面方法中根据当前窗口的不同情况（状态栏）挑选而出，不过measureHierarchy()有自己的考量方法，让窗口布局更优雅，（针对wrap_content的dialog），所以设置了wrap_content的Dialog，==有可能执行多次测量==。（DecorView的xml布局中，宽高基本都为match_parent）![这里写图片描述](https://img-blog.csdn.net/20171129164602295?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

通过上述代码，DecorView的MeasureSpec的产生过程就很明确了，具体来说其遵守如下规则，根据它的LayoutParams中的宽高的参数来划分 : （与上面所说的MeasureSpec和LayoutParams同理）

    LayoutParams.MATCH_PARENT : EXACTLY（精确模式），大小就是窗口的大小；
    
    LayoutParams.WRAP_CONTENT ： AT_MOST (最大模式)，大小不确定，但是不能超过窗口的大小，暂定为窗口大小；
    
    固定大小（写死的值） ： EXACTLY（精确模式），大小就是当前写死的数值。
#### performMeasure

​```java
//ViewRootImpl -->measureHierarchy

    //该方法很简单，直接调用mView.measure()方法
    private void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {

            mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

    }

```

在view.measure()的方法里，仅当给与的MeasureSpec发生变化时，或要求强制重新布局时，才会进行测量。

强制重新布局 ： 控件树中的一个子控件内容发生变化时，需要重新测量和布局的情况，在这种情况下，这个子控件的父控件（以及父控件的父控件）所提供的MeasureSpec必定与上次测量时的值相同，因而导致从ViewRootImpl到这个控件的路径上，父控件的measure()方法无法得到执行，进而导致子控件无法重新测量其布局和尺寸。

==解决途径== : 因此，当子控件因内容发生变化时，从子控件到父控件回溯到ViewRootImpl，并依次调用父控件的requestLayout()方法。这个方法会在==mPrivateFlags==中加入标记PFLAG_FORCE_LAYOUT，从而使得这些父控件的measure()方法得以顺利执行，进而这个子控件有机会进行重新布局与测量。这便是==强制重新布局的意义==所在.

#### measure

```java
//View

    //final类，子类不能重写该方法
    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {

``````

            onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    //ViewGroup并没有重写该方法
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    
    }

```

`view.measure()`方法其实没有实现任何测量的算法，它的作用在于判断是否需要引发`onMeasure()`的调用，并对`onMeasure()`行为的正确性进行检查。

#### onMeasure

ViewGroup是一个抽象类，并没有重写`onMeasure()`方法，就要具体实现类去实现该方法。因为我们的顶级View是`DecorView`，是一个`FrameLayout`，所以我们从`FrameLayout`开始继续我们的主线任务。

##### ViewGroup -> FrameLayout -> onMeasure()

​```java
//FrameLayout


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            //遍历子View，只要子View不是GONE便处理
            if (mMeasureAllChildren || child.getVisibility() != GONE) {

                //子View结合父View的MeasureSpec和自己的LayoutParams算出子View自己的MeasureSpec
                //如果当前child也是ViewGroup，也继续遍历它的子View
                //如果当前child是View，便根据这个MeasureSpec测量自己
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
``````
            }
        }
    
          ``````
        //父View等所有的子View测量结束之后，再来测量自己
        setMeasuredDimension(``````);
    
    }

```

我们知道ViewGroup的measure任务主要是测量所有的子View，测量完毕之后根据合适的宽高再测量自己。

在FrameLayout的onMeasure()方法中，会通过==measureChildWithMargins==()方法遍历子View，并且如果FrameLayout宽高的MeasureSpec是AT_MOST，那么FrameLayout计算自身宽高就会受到子View的影响，可能使用最大子View的宽高。

不同ViewGroup实现类有不同的测量方式，例如LinearLayout自身的高度可能是子View高度的累加。

measureChildWithMargins()方法为ViewGroup提供的方法，根据父View的MeasureSpec和子View的LayoutParams，算出子View自己的MeasureSpec。

​```java
//ViewGroup


    protected void measureChildWithMargins(View child,
            int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);

        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

```

上述方法会对子元素进行measure，在调用子元素的measure方法之前会先通过getChildMeasureSpec方法来得到子元素的MeasureSpec。显然子元素的MeasureSpec的创建与父容器的MeasureSpec和子元素本身的LayoutParams有关，此外还和View的margin和padding有关，如果对该理论知识印象不太深刻建议滑到上个段落 —- MeasureSpec ，再来看以下代码，事半功倍。

```java
//ViewGroup


    //spec为父容器的MeasureSpec
    public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);//父容器的specMode
        int specSize = MeasureSpec.getSize(spec);//父容器的specSize

        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {//根据父容器的specMode
        // Parent has imposed an exact size on us
        case MeasureSpec.EXACTLY:
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size. So be it.
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent has imposed a maximum size on us
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // Child wants a specific size... so be it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size, but our size is not fixed.
                // Constrain child to not be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent asked to see how big we want to be
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
                // Child wants a specific size... let him have it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size... find out how big it should be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size.... find out how big it should be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
        }
        //noinspection ResourceType
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

```

上述方法不难理解，它的主要作用是根据父容器的MeasureSpec同时结合View本身的LayoutParams来确定子元素的MeasureSpec。

##### View -> onMeasure

回顾一下上面的measure段落，因为具体实现的ViewGroup会重写onMeasure()，因为ViewGroup是一个抽象类，其测量过程的onMeasure()方法需要具体实现类去实现，这么做的原因在于不同的ViewGroup实现类有不同的布局特性，比如说FrameLayout，RelativeLayout，LinearLayout都有不同的布局特性，因此ViewGroup无法对onMeasure()做同一处理。

但是对于普通的View只需完成自身的测量工作即可，所以可以看到View的onMeasure方法很简洁。

```java
//View

    //final类，子类不能重写该方法
    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {

``````

            onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    //ViewGroup并没有重写该方法
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
        getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
        getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    
    }


    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
    
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

从getDefaultSize()方法的实现来看，对于AT_MOST和EXACTLY这两种情况View的宽高都由specSize决定，也就是说如果我们直接继承View的自定义控件需要重写onMeasure方法并设置wrap_content时自身的大小，否则在布局中使用wrap_content就相当于使用match_parent。

![这里写图片描述](https://img-blog.csdn.net/20171130122940300?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## layout

子View具体layout的位置都是相对于父容器而言的，View的layout过程和measure同理，也是从顶级View开始，递归的完成整个空间树的布局操作。

经过前面的测量，控件树中的控件对于自己的尺寸显然已经了然于胸。而且父控件对于子控件的位置也有了眉目，所以经过测量过程后，布局阶段会把测量结果转化为控件的实际位置与尺寸。控件的实际位置与尺寸由View的mLeft，mTop，mRight，mBottom 等4个成员变量存储的坐标值来表示。

并且需要注意的是： View的mLeft，mTop，mRight，mBottom 这些坐标值是以父控件左上角为坐标原点进行计算的。倘若需要获取控件在窗口==坐标系中的位置==可以使用==View.GetLocationWindow()==或者是==View.getRawX()/Y()==。

​```java
//ViewRootImpl
    private void performLayout(WindowManager.LayoutParams lp, int desiredWindowWidth,int desiredWindowHeight) {

        final View host = mView;

        host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());

    }

```

```java
//ViewGroup


    //尽管ViewGroup也重写了layout方法
    //但是本质上还是会通过super.layout()调用View的layout()方法
    @Override
    public final void layout(int l, int t, int r, int b) {
        if (!mSuppressLayout && (mTransition == null || !mTransition.isChangingLayout())) {

            //如果无动画，或者动画未运行
            super.layout(l, t, r, b);
        } else {
            //等待动画完成时再调用requestLayout()
            mLayoutCalledWhileSuppressed = true;
        }
    }



//View


    public void layout(int l, int t, int r, int b) {
        int oldL = mLeft;
        int oldT = mTop;
        int oldB = mBottom;
        int oldR = mRight;

        //如果布局有变化，通过setFrame重新布局
        boolean changed = isLayoutModeOptical(mParent) ?
                setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);

        if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {

            //如果这是一个ViewGroup，还会遍历子View的layout()方法
            //如果是普通View，通知具体实现类布局变更通知
            onLayout(changed, l, t, r, b);


            //清除PFLAG_LAYOUT_REQUIRED标记
            mPrivateFlags &= ~PFLAG_LAYOUT_REQUIRED;

``````
            //布局监听通知
        }
    
        //清除PFLAG_FORCE_LAYOUT标记
        mPrivateFlags &= ~PFLAG_FORCE_LAYOUT;
    }

```

通过代码可以看到尽管ViewGroup也重写了layout()方法，但是本质上还是会走View的layout()。

在View的layout()方法里，首先通过 ==setFrame==()（setOpticalFrame()也走setFrame()）将l、t、r、b分别设置到mLeft、mTop、mRight、和mBottom，这样就可以确定子View在父容器的位置了，上面也说过了，这些位置是相对父容器的。

然后调用onLayout()方法，使具体实现类接收到布局变更通知。如果此类是ViewGroup，还会遍历子View的layout()方法使其更新布局。如果调用的是onLayout()方法，这会导致子View无法调用setFrame()，从而无法更新控件坐标信息。

​```java
//View

    protected void onLayout(boolean changed, int l, int t, int r, int b) {}



//ViewGroup

    //abstract修饰，具体实现类必须重写该方法
    @Override
    protected abstract void onLayout(boolean changed,int l, int t, int r, int b);

```

对于普通View来说，onLayout()方法是一个空实现，主要是具体实现类重写该方法后能够接收到布局坐标更新信息。

对于ViewGroup来说，和measure一样，不同实现类有它不同的布局特性，在ViewGroup中onLayout()方法是abstract的，具体实现类必须重写该方法，以便接收布局坐标更新信息后，处理自己的子View的坐标信息。有兴趣的童鞋可以看FrameLayout或者LinearLayout的onLayout()方法。

### 小结

对比测量measure和布局layout两个过程有助于加深对它们的理解.

```java
measure确定的是控件的尺寸，并在一定程度上确定了子控件的位置。而布局则是针对测量结果来实施，并最终确定子控件的位置。

measure结果对布局过程没有约束力。虽说子控件在onMeasure()方法中计算出了自己应有的尺寸，但是由于layout()方法是由父控件调用，因此控件的位置尺寸的最终决定权掌握在父控件手中，测量结果仅仅只是一个参考。

因为measure过程是后根遍历(DecorView最后setMeasureDiemension())，所以子控件的测量结果影响父控件的测量结果。

而Layout过程是先根遍历(layout()一开始就调用setFrame()完成DecorView的布局)，所以父控件的布局结果会影响子控件的布局结果。

完成performLayout()后，空间树的所有控件都已经确定了其最终位置，就剩下绘制了。
```
## draw





![ScreenClip](C:/Users/hasee/AppData/Local/Temp/ScreenClip.png)

我们先纯粹的看View的draw过程，因为这个过程相对上面measure和layout比较简单。

View的draw过程遵循如下几步 ：

    绘制背景drawBackground();
    
    绘制自己onDraw();
    
    如果是ViewGroup则绘制子View，dispatchDraw();
    
    绘制装饰（滚动条）和前景，onDrawForeground();
```java
//View


    public void draw(Canvas canvas) {
        final int privateFlags = mPrivateFlags;

        //检查是否是"实心(不透明)"控件。（后面有补充）
        final boolean dirtyOpaque = (privateFlags & PFLAG_DIRTY_MASK) == PFLAG_DIRTY_OPAQUE &&
                (mAttachInfo == null || !mAttachInfo.mIgnoreDirtyState);
        mPrivateFlags = (privateFlags & ~PFLAG_DIRTY_MASK) | PFLAG_DRAWN;

        /*
         * Draw traversal performs several drawing steps which must be executed
         * in the appropriate order:
         *
         *      1. Draw the background
         *      2. If necessary, save the canvas' layers to prepare for fading
         *      3. Draw view's content
         *      4. Draw children
         *      5. If necessary, draw the fading edges and restore layers
         *      6. Draw decorations (scrollbars for instance)
         */

        // Step 1, draw the background, if needed
        int saveCount;
        //非"实心"控件，将会绘制背景
        if (!dirtyOpaque) {
            drawBackground(canvas);
        }

        // skip step 2 & 5 if possible (common case)
        final int viewFlags = mViewFlags;
        boolean horizontalEdges = (viewFlags & FADING_EDGE_HORIZONTAL) != 0;
        boolean verticalEdges = (viewFlags & FADING_EDGE_VERTICAL) != 0;

        //如果控件不需要绘制渐变边界，则可以进入简便绘制流程
        if (!verticalEdges && !horizontalEdges) {
            // Step 3, draw the content
            if (!dirtyOpaque) onDraw(canvas);//非"实心"，则绘制控件本身

            // Step 4, draw the children
            dispatchDraw(canvas);//如果当前不是ViewGroup，此方法则是空实现

            // Overlay is part of the content and draws beneath Foreground
            if (mOverlay != null && !mOverlay.isEmpty()) {
                mOverlay.getOverlayView().dispatchDraw(canvas);
            }

            // Step 6, draw decorations (foreground, scrollbars)
            onDrawForeground(canvas);//绘制装饰和前景

            // we're done...
            return;
        }

``````
    }

```

![这里写图片描述](https://img-blog.csdn.net/20171202152924303?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

至此View的工作流程的大致整体已经描述完毕了，是否感觉意犹未尽，我们再补充2个知识点作为餐后甜点。

# invalidate

我们知道`invalidate()`(在主线程)和`postInvalidate()`(可以在子线程)都是用于请求View重绘的方法，那么它是如何实现的呢？



invalidate()方法必须在主线程执行，而scheduleTraversals()引发的遍历也是在主线程执行。所以调用invalidate()方法并不会使得遍历立即开始，因为在调用invalidate()的方法执行完毕之前（准确的说是主线程的Looper处理完其他消息之前），主线程根本没有机会处理scheduleTraversals()所发出的消息。

这种机制带来的好处是 ： 在一个方法里可以连续调用多个控件的invalidate()方法，而不用担心会由于多次重绘而产生的效率问题。

另外多次调用invalidate()方法会使得ViewRootImpl多次接收到设置脏区域的请求，ViewRootImpl会将这些脏区域累加到mDirty中，进而在随后的遍历中，一次性的完成所有脏区域的重绘。

窗口第一次绘制时候，ViewRootImpl的mFullRedrawNeeded成员将会被设置为true，也就是说mDirty所描述的区域将会扩大到整个窗口，进而实现完整重绘。

## View的脏区域和”实心”控件

增加两个知识点，能够更好的理解View的重绘过程。

为了保证绘制的效率，控件树仅对需要重绘的区域进行绘制。这部分区域成为”脏区域”Dirty Area。

当一个控件的内容发生变化而需要重绘时，它会通过View.invalidate()方法将其需要重绘的区域沿着控件树自下而上的交给ViewRootImpl，并保存在ViewRootImpl的mDirty成员中，最后通过scheduleTraversals()引发一次遍历，进而进行重绘工作，这样就可以保证仅位于mDirty所描述的区域得到重绘，避免了不必要的开销。

View的isOpaque()方法返回值表示此控件是否为”实心”的，所谓”实心”控件，是指在onDraw()方法中能够保证此控件的所有区域都会被其所绘制的内容完全覆盖。对于”实心”控件来说，背景和子元素（如果有的话）是被其onDraw()的内容完全遮住的，因此便可跳过遮挡内容的绘制工作从而提升效率。

简单来说透过此控件所属的区域无法看到此控件下的内容，也就是既没有半透明也没有空缺的部分。因为自定义ViewGroup控件默认是”实心”控件，所以默认不会调用drawBackground()和onDraw()方法，因为一旦ViewGroup的onDraw()方法，那么就会覆盖住它的子元素。但是我们仍然可以通过调用setWillNotDraw(false)和setBackground()方法来开启ViewGroup的onDraw()功能。

下面我们从View的`invalidate`方法，自下(`View`)而上(`ViewRootImpl`)的分析。

`invalidate` : 使无效； `damage` : 损毁；`dirty` : 脏；

## View

​```java
//View


    public void invalidate() {
        invalidate(true);
    }

    void invalidate(boolean invalidateCache) {
        invalidateInternal(0, 0, mRight - mLeft, mBottom - mTop, invalidateCache, true);
    }

    void invalidateInternal(int l, int t, int r, int b, boolean invalidateCache,
            boolean fullInvalidate) {

        //如果VIew不可见，或者在动画中
        if (skipInvalidate()) {
            return;
        }

        //根据mPrivateFlags来标记是否重绘
        if ((mPrivateFlags & (PFLAG_DRAWN | PFLAG_HAS_BOUNDS)) == (PFLAG_DRAWN | PFLAG_HAS_BOUNDS)
                || (invalidateCache && (mPrivateFlags & PFLAG_DRAWING_CACHE_VALID) == PFLAG_DRAWING_CACHE_VALID)
                || (mPrivateFlags & PFLAG_INVALIDATED) != PFLAG_INVALIDATED
                || (fullInvalidate && isOpaque() != mLastIsOpaque)) {
            if (fullInvalidate) {//上面传入为true，表示需要全部重绘
                mLastIsOpaque = isOpaque();//
                mPrivateFlags &= ~PFLAG_DRAWN;//去除绘制完毕标记。
            }

            //添加标记，表示View正在绘制。PFLAG_DRAWN为绘制完毕。
            mPrivateFlags |= PFLAG_DIRTY;

            //清除缓存，表示由当前View发起的重绘。
            if (invalidateCache) {
                mPrivateFlags |= PFLAG_INVALIDATED;
                mPrivateFlags &= ~PFLAG_DRAWING_CACHE_VALID;
            }

            //把需要重绘的区域传递给父View
            final AttachInfo ai = mAttachInfo;
            final ViewParent p = mParent;
            if (p != null && ai != null && l < r && t < b) {
                final Rect damage = ai.mTmpInvalRect;
                //设置重绘区域(区域为当前View在父容器中的整个布局)
                damage.set(l, t, r, b);
                p.invalidateChild(this, damage);
            }
``````
        }
    }

```

上述代码中，会设置一系列的标记位到`mPrivateFlags`中，并且通过父容器的`invalidateChild`方法，将需要重绘的脏区域传给父容器。（ViewGroup和ViewRootImpl都继承了ViewParent类，该类中定义了子元素与父容器间的调用规范。）

## ViewGroup

​```java
//ViewGroup


    @Override
    public final void invalidateChild(View child, final Rect dirty) {
        ViewParent parent = this;

        final AttachInfo attachInfo = mAttachInfo;
        if (attachInfo != null) {

                RectF boundingRect = attachInfo.mTmpTransformRect;
                boundingRect.set(dirty);

``````
               //父容器根据自身对子View的脏区域进行调整
    
                transformMatrix.mapRect(boundingRect);
                dirty.set((int) Math.floor(boundingRect.left),
                        (int) Math.floor(boundingRect.top),
                        (int) Math.ceil(boundingRect.right),
                        (int) Math.ceil(boundingRect.bottom));
    
            // 这里的do while方法，不断的去调用父类的invalidateChildInParent方法来传递重绘请求
            //直到调用到ViewRootImpl的invalidateChildInParent（责任链模式）
            do {
                View view = null;
                if (parent instanceof View) {
                    view = (View) parent;
                }
    
                if (drawAnimation) {
                    if (view != null) {
                        view.mPrivateFlags |= PFLAG_DRAW_ANIMATION;
                    } else if (parent instanceof ViewRootImpl) {
                        ((ViewRootImpl) parent).mIsAnimating = true;
                    }
                }
    
                //如果父类是"实心"的，那么设置它的mPrivateFlags标识
                // If the parent is dirty opaque or not dirty, mark it dirty with the opaque
                // flag coming from the child that initiated the invalidate
                if (view != null) {
                    if ((view.mViewFlags & FADING_EDGE_MASK) != 0 &&
                            view.getSolidColor() == 0) {
                        opaqueFlag = PFLAG_DIRTY;
                    }
                    if ((view.mPrivateFlags & PFLAG_DIRTY_MASK) != PFLAG_DIRTY) {
                        view.mPrivateFlags = (view.mPrivateFlags & ~PFLAG_DIRTY_MASK) | opaqueFlag;
                    }
                }
    
                //***往上递归调用父类的invalidateChildInParent***
                parent = parent.invalidateChildInParent(location, dirty);
    
                //设置父类的脏区域
                //父容器会把子View的脏区域转化为父容器中的坐标区域
                if (view != null) {
                    // Account for transform on current parent
                    Matrix m = view.getMatrix();
                    if (!m.isIdentity()) {
                        RectF boundingRect = attachInfo.mTmpTransformRect;
                        boundingRect.set(dirty);
                        m.mapRect(boundingRect);
                        dirty.set((int) Math.floor(boundingRect.left),
                                (int) Math.floor(boundingRect.top),
                                (int) Math.ceil(boundingRect.right),
                                (int) Math.ceil(boundingRect.bottom));
                    }
                }
            } 
            while (parent != null);
        }
    }

```

## ViewRootImpl

我们先验证一下最上层ViewParent为什么是ViewRootImpl

​```java
//ViewRootImpl


    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {

               view.assignParent(this);
    }


//View


    void assignParent(ViewParent parent) {
        if (mParent == null) {
            mParent = parent;
        } else if (parent == null) {
            mParent = null;
        } else {
            throw new RuntimeException("view " + this + " being added, but"
                    + " it already has a parent");
        }
    }

```

在ViewRootImpl的setView方法中，由于传入的View正是DecorView，所以最顶层的ViewParent即ViewRootImpl。另外ViewGroup在addView方法中，也会调用assignParent()方法，设定子元素的父容器为它本身。


由于最上层的ViewParent是ViewRootImpl，所以我们可以查看ViewRootImpl的invalidateChildInParent方法即可。

```java
//ViewRootImpl


    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        //检查线程，这也是为什么invalidate一定要在主线程的原因
        checkThread();

        if (dirty == null) {
            invalidate();//有可能需要绘制整个窗口
            return null;
        } else if (dirty.isEmpty() && !mIsAnimating) {
            return null;
        }

``````

        invalidateRectOnScreen(dirty);
    
        return null;
    }


    //设置mDirty并执行View的工作流程
    private void invalidateRectOnScreen(Rect dirty) {
        final Rect localDirty = mDirty;
        if (!localDirty.isEmpty() && !localDirty.contains(dirty)) {
            mAttachInfo.mSetIgnoreDirtyState = true;
            mAttachInfo.mIgnoreDirtyState = true;
        }
    
        // Add the new dirty rect to the current one
        localDirty.union(dirty.left, dirty.top, dirty.right, dirty.bottom);    
        //在这里，mDirty的区域就变为方法中的dirty，即要重绘的脏区域
    
        ``````
        if (!mWillDrawSoon && (intersected || mIsAnimating)) {
            scheduleTraversals();//执行View的工作流程
        }
    }

```

什么？执行invalidate()方法居然会引起scheduleTraversals()！
那么也就是说invalidate()会导致perforMeasure()、performLayout()、perforDraw()的调用了？？？

这个scheduleTraversals()很眼熟，我们一出场就在requestLayout()中见过，并且我们还说了mLayoutRequested用来表示是否measure和layout。

​```java
//ViewRootImpl


    @Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();//检查是否在主线程
            mLayoutRequested = true;//mLayoutRequested 是否measure和layout布局。
            scheduleTraversals();
        }
    }


    private void performTraversals() {

        boolean layoutRequested = mLayoutRequested && (!mStopped || mReportNextDraw);
        if (layoutRequested) {
            measureHierarchy(```);//measure
        }


        final boolean didLayout = layoutRequested && (!mStopped || mReportNextDraw);
        if (didLayout) {
            performLayout(lp, mWidth, mHeight);//layout
        }


        boolean cancelDraw = mAttachInfo.mTreeObserver.dispatchOnPreDraw() || !isViewVisible;
        if (!cancelDraw && !newSurface) {
            performDraw();//draw
        }
    } 

```

因为我们invalidate的时候，并没有设置mLayoutRequested，所以放心，它只走performDraw()流程，并且在draw()流程中会清除mDirty区域。

并且只有设置了标识为的View才会调用draw方法进而调用onDraw()，减少开销。「源码工程师各方面的考虑肯定比一般人更周到，我们写的是代码，他们写的是艺术。」

![Idtk--View的invalidate](https://img-blog.csdn.net/20171202180025700?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

# requestLayout

看完了`invalidate()`流程之后，`requestLayout()`流程就比较好上手了。

我们在measure阶段提到过 ：

在view.measure()的方法里，仅当给与的MeasureSpec发生变化时，或要求强制重新布局时，才会进行测量。

强制重新布局 ： 控件树中的一个子控件内容发生变化时，需要重新测量和布局的情况，在这种情况下，这个子控件的父控件（以及父控件的父控件）所提供的MeasureSpec必定与上次测量时的值相同，因而导致从ViewRootImpl到这个控件的路径上，父控件的measure()方法无法得到执行，进而导致子控件无法重新测量其布局和尺寸。（在父容器measure中遍历子元素）

解决途径 : 因此，当子控件因内容发生变化时，从子控件到父控件回溯到ViewRootImpl，并依次调用父控件的requestLayout()方法。这个方法会在mPrivateFlags中加入标记PFLAG_FORCE_LAYOUT，从而使得这些父控件的measure()方法得以顺利执行，进而这个子控件有机会进行重新布局与测量。这便是强制重新布局的意义所在。

下面我们看View的`requestLayout()`方法

//View

~~~java
@CallSuper
public void requestLayout() {
    if (mMeasureCache != null) mMeasureCache.clear();

    ``````

    // 增加PFLAG_FORCE_LAYOUT标记，在measure时会校验此属性
    mPrivateFlags |= PFLAG_FORCE_LAYOUT;
    mPrivateFlags |= PFLAG_INVALIDATED;

    // 父类不为空&&父类没有请求重新布局(是否有PFLAG_FORCE_LAYOUT标志)
    //这样同一个父容器的多个子View同时调用requestLayout()就不会增加开销
    if (mParent != null && !mParent.isLayoutRequested()) {
        mParent.requestLayout();
    }

}
~~~
因为上面说过了，最顶层的ViewParent是ViewRootImpl。

//ViewRootImpl

```java
@Override
public void requestLayout() {
    if (!mHandlingLayoutInLayoutRequest) {
        checkThread();
        mLayoutRequested = true;
        scheduleTraversals();
    }
}
```
同样，requestLayout()方法会调用scheduleTraversals();，因为设置了mLayoutRequested =true标识，所以在performTraversals()中调用performMeasure()，performLayout()，但是由于没有设置mDirty，所以不会走performDraw()流程。

但是，requestLayout()方法就一定不会导致onDraw()的调用吗？

在上面layout()方法中说道 :

    在View的layout()方法里，首先通过 setFrame()（setOpticalFrame()也走setFrame()）将l、t、r、b分别设置到mLeft、mTop、mRight、和mBottom，这样就可以确定子View在父容器的位置了，上面也说过了，这些位置是相对父容器的。
```java
//View -->  layout()


    protected boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = false;


        if (mLeft != left || mRight != right || mTop != top || mBottom != bottom) {
            //布局坐标改变了
            changed = true;

            int oldWidth = mRight - mLeft;
            int oldHeight = mBottom - mTop;
            int newWidth = right - left;
            int newHeight = bottom - top;
            boolean sizeChanged = (newWidth != oldWidth) || (newHeight != oldHeight);

            // Invalidate our old position
            invalidate(sizeChanged);//调用invalidate重新绘制视图


            if (sizeChanged) {
                sizeChange(newWidth, newHeight, oldWidth, oldHeight);
            }

``````
        }
        return changed;
    }

```

看完代码我们就很清晰的知道，如果layout布局有变化，那么它也会调用`invalidate()`重绘自身。

下面再借用Idtk绘制的layout流程图 
 ![Idtk--View的requestLayout](https://img-blog.csdn.net/20171202192427613?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWlhbjUyMGFv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

# 总结

至此，View的工作流程分析完毕，文章如果有错误或者不妥之处，还望评论提出。

理清整体流程对我们android的布局，绘制，自定义View，和分析bug都有一个提升。

有兴趣的还可以继续观看Android源码分析

