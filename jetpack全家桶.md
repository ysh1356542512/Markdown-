# Jetpack全家桶



## Lifecycle



### 通过Lifecycle解耦页面与组件

实现 implements LifecycleObserver 接口

在需要的地方使用此注解就可以使用

```
@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
```

```java
getLifecycle().addObserver(chronometer);
```



### LifecycleService解耦Service



首先创建一个服务MyLocationService extends LifecycleService 继承

同样实现 MyLocationObserver implements LifecycleObserver

这是服务里面添加监听器的代码

```java
MyLocationObserver observer = new MyLocationObserver(this);
getLifecycle().addObserver(observer);
```

同样在观察者里面使用@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)

来监听生命周期





### ProcessLifecycleOwner 监听应用程序的生命周期



一个类继承于Application 然后去为它添加  生命周期的监听

这一个监听是对整个app的监听  与Activity的数量无关

oncreat只会调用一次

而ondestory永远不会被调用

```
MyApplication extends Application
```

```
ProcessLifecycleOwner.get().getLifecycle()
        .addObserver(new ApplicationObserver());
```



## ViewModel

### 解决问题

1. 瞬态数据丢失
2. 异步调用的内存泄漏
3. 类膨胀提高维护难度和测试难度
4. 使数据和视图的分离 而且还能保持通







### 作用

ViewModel 能够实现瞬态数据保存的原因就是

因为它的生命周期要比组件的生命周期长  独立于配置的变化

### 注意

不要向ViewModel 里面传入context对象  因为会引起内存泄漏

如果实在需要这个context对象那么就去继承AndroidViewModel

在构造方法里面就会有context对象

```
public MyViewModel(@NonNull Application application) {
    super(application);
}

```

### 使用

```java
viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MyViewModel.class);
```



## LiveData

### 介绍

MutableLiveData  是LIveData的子类  LiveData是一个抽象类

所以一般使用的是MutableLiveData





### 作用

**在ViewModel数据发生变化的时候就去通知页面**

### 优势

1. 确保界面符合数据状态
2. 不会发生内存泄漏
3. 不会因为Activity停止而导致崩溃
4. 不需要手动处理生命周期
5. 数据始终保持最新状态
6. 适当的配置更改
7. 共享资源

### 使用

getCurrentSecond()方法在这里只是返回了一个MutableLiveData数据类型的 对象 添加观察者  在数据发生变化的时候调用此方法

```java
viewModel.getCurrentSecond().observe(this, new Observer<Integer>() {
    @Override
    public void onChanged(Integer i) {
        textView.setText(String.valueOf(i));
    }
});
```



## Room

### 版本跃迁

下面是版本跃迁的代码

```java
private static final String DATABASE_NAME = "my_db.db";
private static MyDatabase mInstance;

//private MyDatabase(){}

public static synchronized MyDatabase getInstance(Context context){
    if(mInstance == null){
        mInstance = Room.databaseBuilder(context.getApplicationContext(),
                MyDatabase.class,
                DATABASE_NAME)
                //.allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4)
                //.fallbackToDestructiveMigration()  这里也是版本回退的功能
                .createFromAsset("prestudent.db")   //预填充数据
                .build();
    }
    return mInstance;
}
```

```java
static final Migration MIGRATION_1_2 = new Migration(1,2) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE student ADD COLUMN sex INTEGER NOT NULL DEFAULT 1");
    }
};

static final Migration MIGRATION_2_3 = new Migration(2,3) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE student ADD COLUMN bar_data INTEGER NOT NULL DEFAULT 1");
    }
};


```



### Schema

在defaultConfig里面添加

```java
javaCompileOptions {
    annotationProcessorOptions {
        arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]//指定数据库schema导出的位置
    }
}
```

还要添加注解 exportSchema = false

### 销毁重建策略

可以修改数据类型

```java
static final Migration MIGRATION_3_4 = new Migration(3,4) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE temp_student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                "name TEXT,"+
                "age INTEGER NOT NULL,"+
                "sex TEXT DEFAULT 'M',"+
                "bar_data INTEGER NOT NULL DEFAULT 1)");
        database.execSQL("INSERT INTO temp_student (name,age,sex,bar_data)" +
                "SELECT name,age,sex,bar_data FROM student");
        database.execSQL("DROP TABLE student");
        database.execSQL("ALTER TABLE temp_student RENAME TO student");
    }
};
```

### 预填充数据库

createFromAsset()和createFromFile()创建Room数据库

放在assets目录下边 一起打包

## Navigation



### 主要元素

#### Navigation Graph 

 一种新的xml资源文件 包含应用程序的所有页面，以及页面之间的关系



#### NavHostFragment

一个特殊的Fragment，可以将它看做是其它Fragment的容器，

Navigation Graph的Fragment正是通过NavHostFragment进行展示的



#### NavController

用于在代码中完成 Navgation Graph中具体的页面切换工作

### 使用



首先是配置

```java
/**
 *把xml里面设置的fragment 设置给navController进行管理
 */
NavController navController = Navigation.findNavController(this, R.id.fragment);
NavigationUI.setupActionBarWithNavController(this,navController);
```

```java
/**
 * 此方法可以使得  导航栏的返回键起效果
 * 知道返回的是哪里
 * @return
 */
@Override
public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.fragment);
    return navController.navigateUp();
}
```

```java
这里主要的作用就是完成  Fragment的切换
NavController navController = Navigation.findNavController(v);
navController.navigate(R.id.action_homeFragment_to_detailFragment);
```

### 动画效果

可以通过界面添加默认的  也可以自己自定义的添加动画效果



### 参数传递

1. ```java
   传递参数/*Bundle args = new Bundle();
   args.putString("user_name","jack");*/
   取出参数
   /*Bundle args = getArguments();
           String userName = args.getString("user_name");*/
   ```

2. 更好的参数传递方式    添加全局的插件   

3. ```
   dependencies {
       classpath "com.android.tools.build:gradle:4.1.2"
       classpath 'androidx.navigation:navigation-safe-args-gradle-plugin:2.3.1'
       // NOTE: Do not place your application dependencies here; they belong
       // in the individual module build.gradle files
   }
   ```

   

   ```
   在自己的项目里面引用插件plugins {
       id 'com.android.application'
       id 'androidx.navigation.safeargs'
   }
   ```

接下来要在

navigation里面的碎片 下边配置想要传递的参数信息

```
<argument
    android:name="user_name"
    app:argType="string"
    android:defaultValue="unknown"/>
<argument
    android:name="age"
    app:argType="integer"
    android:defaultValue="0"/>
```

然后构建项目 会自动生成代码

就可以这样使用

传递参数

```java
Bundle args = new HomeFragmentArgs.Builder()
        .setUserName("rose")
        .setAge(18)
        .build().toBundle();
```

接受参数

```java
HomeFragmentArgs args = HomeFragmentArgs.fromBundle(getArguments());
String userName = args.getUserName();
int age = args.getAge();
Log.d("ning",userName+","+age);
```

### NavigationUI

#### 作用

Fragment的切换，除了Fragment页面本身的切换，通常还伴有App bar的变化。为了方便统一管理，Navigation 组件引入了NavigationUI类

#### 使用

```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
                        //这句话可以保证onCreateOptionsMenu的执行
    setHasOptionsMenu(true);
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings, container, false);
}

@Override
public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    menu.clear();   //清楚菜单
    super.onCreateOptionsMenu(menu, inflater);
}
```

```java

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

navController = Navigation.findNavController(this, R.id.fragment);
    appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
    NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration);

    //监听页面切换
    navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            Log.d("ning","onDestinationChanged");
        }
    });
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu_settings,menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return NavigationUI.onNavDestinationSelected(item,navController) || super.onOptionsItemSelected(item);
}

@Override
public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController,appBarConfiguration) || super.onSupportNavigateUp();
}
```

### Deeplink

#### PendingIntent

```java
public class HomeFragment extends Fragment {


    private int notificationId;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = getView().findViewById(R.id.button);
        button.setOnClickListener((v)->{
            sendNotification();
        });
    }

    private void sendNotification() {
        //通知渠道
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(getActivity().getPackageName(),"MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My NotificationChannel");
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getActivity(),getActivity().getPackageName())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Deep Link")
                .setContentText("点击我试试...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(getPendingItent())
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
        notificationManagerCompat.notify(notificationId++,notification);
    }

    private PendingIntent getPendingItent() {
        Bundle args = new Bundle();
        args.putString("name","jack");
        return Navigation.findNavController(getActivity(),R.id.button)
                .createDeepLink()
                .setGraph(R.navigation.my_nav_graph)
                .setDestination(R.id.detailFragment)
                .setArguments(args)
                .createPendingIntent();
    }
}
```

#### URL

##### 作用

用户通过手机浏览器 浏览某个页面时，可以在网页上放置一个类似于"在应用中打开的按钮"，如果用户有我们的app，那么通过DeepLink就能打开相应的页面。如果没有安装，那么网站可以导航到应用程序的下载页面，引导用户安装应用程序

## WorkManger
