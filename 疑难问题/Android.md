# 跑马灯速度

利用反射改跑马灯的速度失败

[出处](https://www.jianshu.com/p/93e1933a4fc5)

```kotlin
@SuppressLint("PrivateApi")
    private fun setMarqueeSpeed(newSpeed: Float) {
        try {
            // 获取走马灯配置对象
            val tvClass = Class.forName("android.widget.TextView")
            val marqueeField: Field = tvClass.getDeclaredField("mMarquee")
            marqueeField.setAccessible(true)
            val marquee: Class<*> = marqueeField.type
            // 设置新的速度
            val marqueeClass: Class<*> = marqueeField::class.java
            Log.i(TAG, "field.getName()");
            Log.i(TAG, "field.getName()"+marquee.declaredFields.size);
            Log.i(TAG, "field.getName()"+marqueeField.genericType.typeName);
            // 速度变量的名称可能与此示例的不相同 可自行打印查看
            for ( field in  marquee.getDeclaredFields()) {
                Log.i(TAG, field.getName());
            }


            // SDK中的是mPixelsPerMs，但我的开发机是下面的名称
          //  val speedField: Field = marqueeClass.getDeclaredField("mPixelsPerSecond")
//            val speedField: Field = marqueeClass.getDeclaredField("mPixelsPerMs")
//            speedField.setAccessible(true)
//            val orgSpeed = speedField.get(marquee) as Float
//            // 这里设置了相对于原来的20倍
//            speedField.set(marquee, newSpeed)
//            Log.i(TAG, "setMarqueeSpeed: $orgSpeed")
//            Log.i(TAG, "setMarqueeSpeed: $newSpeed")
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "setMarqueeSpeed: 设置跑马灯速度失败", e)
        } catch (e: NoSuchFieldException) {
            Log.e(TAG, "setMarqueeSpeed: 设置跑马灯速度失败", e)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
    }
```