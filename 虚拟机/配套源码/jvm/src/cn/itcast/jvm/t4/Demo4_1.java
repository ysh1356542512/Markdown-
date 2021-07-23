package cn.itcast.jvm.t4;

import cn.itcast.jvm.t4.Invoker;
import cn.itcast.jvm.t4.MyService;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class Demo4_1 {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8, ACC_PUBLIC, "cn/itcast/jvm/t4/MyServiceImpl2", null, "java/lang/Object", new String[]{"cn/itcast/jvm/t4/MyService"});
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC,"<init>","(Lcn/itcast/jvm/t4/Invoker;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", true);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, "cn/itcast/jvm/t4/MyServiceImpl2", "invoker", "Lcn/itcast/jvm/t4/Invoker;");
        mv.visitInsn(RETURN);
        mv.visitEnd();
        byte[] bytes = cw.toByteArray();
        ClassLoader loader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                System.out.println(name);
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
        Class<?> c = loader.loadClass("cn.itcast.jvm.t4.MyServiceImpl2");
        Constructor<?> constructor = c.getConstructor(Invoker.class);
        MyService service = (MyService) constructor.newInstance(new Invoker() {
            @Override
            public Object invoke(Method method, Object[] args) {
                return null;
            }
        });
    }
}
