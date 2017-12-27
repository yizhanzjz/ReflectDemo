package com.example.yizhan.mytestdemo.demo;

import android.app.ActivityThread;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MainActivity extends AppCompatActivity {

    private TextView tvText;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = (Bundle) msg.getData();

            StringBuffer stringBuffer = new StringBuffer();

            String fileds = (String) bundle.get("fileds");
            stringBuffer.append(fileds);

            String constructors = (String) bundle.get("constructors");
            stringBuffer.append("\n\n\n" + constructors);

            String methods = (String) bundle.get("methods");
            stringBuffer.append("\n\n\n" + methods);

            tvText.setText(stringBuffer.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = (TextView) findViewById(R.id.tv_text);
        tvText.setMovementMethod(ScrollingMovementMethod.getInstance());

        ActivityThread activityThread = ActivityThread.currentActivityThread();
//        Log.i("test", "activityThread == " + activityThread);

        //开启一个线程，Looper阻塞

        HandlerThread reflectThread = new HandlerThread("reflect_thread");
        reflectThread.start();

        Handler mThreadHandler = new Handler(reflectThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //下面的代码在子线程中操作
                Class clazz = Thread.class;

                Bundle bundle = new Bundle();

                //获取字段的相关信息
                StringBuilder fieldBuffer = new StringBuilder();
                Field[] fields = clazz.getDeclaredFields();
//                Log.i("test", "fields.length == " + fields.length);
                for (Field field : fields) {
                    String name = field.getName();

                    int modifiers = field.getModifiers();
                    String modifiersStr = Modifier.toString(modifiers);

                    String typeName = field.getType().getSimpleName();

                    fieldBuffer.append(modifiersStr + " " + typeName + " " + name + ";" + "\n\n");
                }
                bundle.putSerializable("fileds", fieldBuffer.toString());


                Constructor[] constructors = clazz.getDeclaredConstructors();
                StringBuilder constructorBuilder = new StringBuilder();
                for (Constructor constructor : constructors) {

                    int modifiers = constructor.getModifiers();
                    String modifiersStr = Modifier.toString(modifiers);
                    constructorBuilder.append(modifiersStr);

                    String name = clazz.getSimpleName();
                    constructorBuilder.append(" " + name);

                    Class[] parameterTypes = constructor.getParameterTypes();
                    constructorBuilder.append("(");
                    if (parameterTypes != null) {
                        StringBuilder constructBuilder = new StringBuilder();
                        for (int i = 0; i < parameterTypes.length; i++) {
                            Class paramType = parameterTypes[i];
                            String paramTypeSimpleName = paramType.getSimpleName();
                            constructBuilder.append(paramTypeSimpleName + " param" + i + ",");
                        }
                        String constructStr = constructBuilder.toString();
//                        Log.i("test", "constructStr == " + constructStr);
                        int length = constructStr.length();
                        if (length >= 1) {
                            constructStr = constructStr.substring(0, length - 1);
                        }
                        constructorBuilder.append(constructStr);
                    }
                    constructorBuilder.append(");\n\n");
                }
                bundle.putSerializable("constructors", constructorBuilder.toString());

                //获取方法的声明
                Method[] methods = clazz.getDeclaredMethods();
                StringBuilder methodBuilder = new StringBuilder();
                for (Method method : methods) {

                    int modifiers = method.getModifiers();
                    String modifiersStr = Modifier.toString(modifiers);
                    methodBuilder.append(modifiersStr);

                    String typeName = method.getReturnType().getSimpleName();
                    methodBuilder.append(" " + typeName);

                    String name = method.getName();
                    methodBuilder.append(" " + name);

                    Class<?>[] typeParameters = method.getParameterTypes();
                    methodBuilder.append("(");
                    if (typeParameters != null) {
                        StringBuilder paramTypeBuilder = new StringBuilder();
                        for (int i = 0; i < typeParameters.length; i++) {
                            String param = typeParameters[i].getSimpleName();
                            paramTypeBuilder.append(param + " param" + i + ",");
                        }
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


                Message message = Message.obtain();
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        };

        mThreadHandler.sendEmptyMessage(0);
    }

}
