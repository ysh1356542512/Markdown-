

# 总纲



FrameWork层源码的分析

    0）Android系统启动流程
    1）应用程序内Activity的启动流程
    2）startService的流程分析
    3 ) 注册广播接收器的源码分析
    4）广播发送过程源码解析
    5）广播处理过程源码解析
    6）AssetManager加载资源过程
    7）ClassLoader及dex加载过程
    8）插件化框架VirtualApk之初始化
    9）插件化框架VirtualApk之插件加载
    10）插件化框架VirtualApk之Activity启动
    11）插件化框架VirtualApk之Service管理
    12）热修复框架AndFix完全解析
    13）InstantRun源码分析[转]
    14 ) Google新组件下的架构思考
------------------------------------------------






















# Activity启动流程

[一个很好的博客](https://blog.csdn.net/u012267215/article/details/91406211)

[activity 启动模式_Activity启动流程详解（基于api28）](https://blog.csdn.net/weixin_39872334/article/details/111179933)

![img](../../%E5%9B%BE%E5%BA%93/FrameWork/%5DCRI64IKD1BDCJ%7BVD6I%7D%60%250.png)





![img](../../%E5%9B%BE%E5%BA%93/FrameWork/32O_%25CW7ZHGO8P$TMKRZJN5.png)





Activity生命周期的管理

![img](../../%E5%9B%BE%E5%BA%93/FrameWork/E%25URHIGMP5PBYJAY%5DBL%60UL.png)

