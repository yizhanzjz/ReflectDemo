# ReflectDemo
一个java反射的demo
### 前言
&emsp;&emsp;之前整理了java同步的相关内容，现在开始整理java反射，都属于java相关内容。在查找资料的过程中，找到两篇个人认为很不错的介绍及应用：

&emsp;&emsp;[Java反射以及在Android中的特殊应用](https://juejin.im/post/5a2c1c5bf265da431956334c)

&emsp;&emsp;[Java 技术之反射](https://xiaozhuanlan.com/topic/1647203589)

### 什么是反射？
&emsp;&emsp;反射，简单来讲，是一种与类动态交互的机制。为什么是动态交互的？一般来讲，在一个类已经写好且不能再修改的前提下，我们只能调用其暴露出来的方法或属性，而有时候我们又有扩展其功能或使用其隐藏功能的需求，这时候我们就可以在程序运行状态下，获取该类的相关信息并操作其信息以满足我们的需求。这种在写代码时仍旧保持该类不修改不改变而在程序运行时扩展其功能或使用其隐藏功能的机制，就是一种动态交互的机制。

### 使用反射获取一个类所有的变量和方法
&emsp;&emsp;反射可以获取字节码文件内的信息，下面的代码是获取信息的示例，主要作用是为了熟悉获取字段、构造方法、普通方法的API。

&emsp;&emsp;获取字节码内所有已声明的字段：
```
StringBuilder fieldBuffer = new StringBuilder();
//getDeclaredFields即为获取字节码内所有已声明字段的API
Field[] fields = clazz.getDeclaredFields();
//遍历所有的字段，获取字段相关信息
for (Field field : fields) {

    //字段名称
    String name = field.getName();

    //这两句是获取字段的修饰符的，并通过Modifier此类转换成了字符串
    int modifiers = field.getModifiers();
    String modifiersStr = Modifier.toString(modifiers);

    //getType是获取字段的类型（Class），getSimpleName为其简化名称
    String typeName = field.getType().getSimpleName();
    
    //组装
    fieldBuffer.append(modifiersStr + " " + typeName + " " + name + ";" + "\n\n");
}
bundle.putSerializable("fileds", fieldBuffer.toString());
```
&emsp;&emsp;获取字节码内所有的构造方法：
```
//getDeclaredConstructors：获取字节码内所有已声明的构造方法
Constructor[] constructors = clazz.getDeclaredConstructors();
StringBuilder constructorBuilder = new StringBuilder();
//逐一遍历每个构造方法，并获取其相关信息
for (Constructor constructor : constructors) {

    //获取构造方法的修饰符，并转换成字符串
    int modifiers = constructor.getModifiers();
    String modifiersStr = Modifier.toString(modifiers);
    constructorBuilder.append(modifiersStr);

    //这里如果使用constructor.getName获取构造方法名，获取的不是简化的形式
    //所以，这里直接使用类的简化名
    String name = clazz.getSimpleName();
    constructorBuilder.append(" " + name);

    //getParameterTypes：获取构造方法的所有参数的类型，以数组形式返回
    Class[] parameterTypes = constructor.getParameterTypes();
    constructorBuilder.append("(");
    if (parameterTypes != null) {
        StringBuilder constructBuilder = new StringBuilder();
        //逐一遍历所有参数的类型，获取其简化名
        for (int i = 0; i < parameterTypes.length; i++) {
            Class paramType = parameterTypes[i];
            String paramTypeSimpleName = paramType.getSimpleName();
            constructBuilder.append(paramTypeSimpleName + " param" + i + ",");
        }
        
        //多一个逗号的处理
        String constructStr = constructBuilder.toString();
        Log.i("test", "constructStr == " + constructStr);
        int length = constructStr.length();
        if (length >= 1) {
            constructStr = constructStr.substring(0, length - 1);
        }
        constructorBuilder.append(constructStr);
    }
    constructorBuilder.append(");\n\n");
}
bundle.putSerializable("constructors", constructorBuilder.toString());
```
&emsp;&emsp;获取所有已声明方法：
```
//getDeclaredMethods：获取所有已声明的方法
Method[] methods = clazz.getDeclaredMethods();
StringBuilder methodBuilder = new StringBuilder();
for (Method method : methods) {

    //获取该方法的修饰符
    int modifiers = method.getModifiers();
    String modifiersStr = Modifier.toString(modifiers);
    methodBuilder.append(modifiersStr);

    //获取该方法的返回类型
    String typeName = method.getReturnType().getSimpleName();
    methodBuilder.append(" " + typeName);

    //获取方法名
    String name = method.getName();
    methodBuilder.append(" " + name);

    //获取该方法的所有参数类型
    Class<?>[] typeParameters = method.getParameterTypes();
    methodBuilder.append("(");
    if (typeParameters != null) {
        StringBuilder paramTypeBuilder = new StringBuilder();
        //逐一编译该方法的类型
        for (int i = 0; i < typeParameters.length; i++) {
            String param = typeParameters[i].getSimpleName();
            paramTypeBuilder.append(param + " param" + i + ",");
        }
        //多一个逗号的处理
        String paramTypeStr = paramTypeBuilder.toString();
        int length = paramTypeStr.length();
        if (length >= 1) {
            paramTypeStr = paramTypeStr.substring(0, length - 1);
        }
        methodBuilder.append(paramTypeStr);
    }
    methodBuilder.append(");\n\n");
}
bundle.putSerializable("methods", methodBuilder.toString());
```
&emsp;&emsp;观察上述代码，可以发现，获取构造方法信息和获取普通方法信息的代码是很像的。

### 使用反射获取一个类所有的变量和方法
&emsp;&emsp;上述只是在使用反射的相关api获取类信息，并没有对这些信息进行使用。其实我们可以获取一个类中的字段值，比如获取ActivityThread中的字段mH的值：
```
//获取ActivityThread的mH字段的值
//获取ActivityThread的class对象
Class clazz = ActivityThread.class;
try {
    //每一个成员变量对应一个Field对象，获取mH的Field对象
    Field field = clazz.getDeclaredField("mH");
    //设置成员变量为可访问
    field.setAccessible(true);
    //获取字段mH的值
    Handler mH = (Handler) field.get(ActivityThread.currentActivityThread());
    
} catch (NoSuchFieldException e) {
    e.printStackTrace();
} catch (IllegalAccessException e) {
    e.printStackTrace();
}
```
&emsp;&emsp;需要说明的有几点：ActivityThread在android.jar中是隐藏api，但在代码中是可以直接用的；setAccessible设成true，表示该字段值就可以访问或者修改了；field的get方法的调用，需要传入此field所在类的对象，该field是在ActivityThread中，ActivityThread.currentActivityThread()就是获取ActivityThread对象的。

&emsp;&emsp;开头说的两篇反射文章中第一篇hook了Handler的mCallback字段，然后在主线程处理消息时就可以进行一些我们自己的处理？首先，我们要知道，主线程在处理消息时，会调用主线程Handler对象mH的dispatchMessage方法，其内容如下：
```
public void dispatchMessage(Message msg) {
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        handleMessage(msg);
    }
}
```
&emsp;&emsp;如果Message中有回调，就执行Message中的回调；如果Handler对象的成员变量mCallback非空，就执行mCallback的handleMessage方法；在上述两者均未执行的前提下，才会执行Handler对象的handleMessage方法。而我们在此处可以获取到mCallback字段，然后给它附上我们自己的值，这样就能够处理我们自己的逻辑了。当然为了不影响android框架中mCallback原有的功能，我们还需要调用下mCallback的handleMessage方法。上面已经获得了ActivityThread的mH对象，下面我们要获取mH对象的mCallback字段并替换逻辑：
```
//获取Handler的mCallback对象
Class clazzHandler = Handler.class;
Field mCallbackField = clazzHandler.getDeclaredField("mCallback");
mCallbackField.setAccessible(true);
//这里是获取mCallback的值，有可能为空
Handler.Callback mCallback = (Handler.Callback) mCallbackField.get(mH);
mCallbackField.set(mH, new MyCallback(mCallback));

private static class MyCallback implements Handler.Callback {

    private Handler.Callback mCallback;

    public MyCallback(Handler.Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public boolean handleMessage(Message msg) {
        
        //此处是获取ActivityThread的内部类H
        Class clazz = ActivityThread.class;
        Class[] declaredClasses = clazz.getDeclaredClasses();
        Class hClazz = null;
        for (Class cl : declaredClasses) {
            String simpleName = cl.getSimpleName();
            if (simpleName.equals("H")) {
                hClazz = cl;
                break;
            }
        }
        if (hClazz != null) {
            try {
                //获取该内部类H的一个静态成员变量
                Field launchActivityField = hClazz.getDeclaredField("LAUNCH_ACTIVITY");
                int launchActivityFieldValue = (int) launchActivityField.get(null);
//                    Log.i("test", "launchActivityFieldValue：" + launchActivityFieldValue);
                if (msg.what == launchActivityFieldValue) {
                    //处理自己的逻辑
                    Log.i("test", "这里是我们自己的逻辑：监听到了Acitivity启动");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (mCallback != null) {
            return mCallback.handleMessage(msg);
        } else {//此处返回false，可以继续向下执行
            return false;
        }
    }
}
```
&emsp;&emsp;上面的代码首先通过反射的方式获取Handler类中的字段mCallback，然后给它设置一个新值：MyCallback对象。在创建MyCallback对象时，传入了Handler中原有的mCallback值，这样是为了处理原有的mCallback中的逻辑。之后，创建了一个静态内部类MyCallback。之所以是静态的，是为了不让其创建的对象拥有其外部类的引用。该静态内部类中通过反射的方式获取到了ActivityThread的内部类H（mH的类型），然后也是通过反射进一步获取到H类中的静态字段LAUNCH_ACTIVITY。

&emsp;&emsp;另外，还需注意的一点是，上述代码应该写在我们自定义的Application类的attachBaseContext中。这里有一个小插曲，之前在看android源码时，看到在performLaunchActivity方法中创建了Activity对象之后调用了makeApplication创建了Application对象，所以之前一直以为Application的创建时机就是在入口Activity对象创建之后。然后就猜想，上述的hook是不能监听到入口activity的启动的，但执行之后的结果却让我大跌眼镜，上述的hook是可以监听到入口activity的启动的！然后通过打其它log进行验证，发现Application的创建时机，其实是在入口Activity启动之前的。


### 总结
&emsp;&emsp;反射，是一种很强大的功能。像Android框架这种我们不能改变的代码，我们可以通过反射在不改变原有框架功能逻辑的基础上，添加一些自己的逻辑。个人认为这种能力，相当cool。

&emsp;&emsp;总的来说，反射能够获取字节码的信息（成员变量、构造方法、普通方法等），然后对这些信息可以进行操作（获取值，修改值，调用方法，修改方法）。

#### [完整代码](https://github.com/yizhanzjz/ReflectDemo)
