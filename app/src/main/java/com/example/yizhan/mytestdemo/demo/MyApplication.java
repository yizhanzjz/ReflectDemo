package com.example.yizhan.mytestdemo.demo;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by yizhan on 2017/12/27.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.i("test", "attachBaseContext");

        //获取ActivityThread的mH字段的值
        Class clazz = ActivityThread.class;
        try {
            //获取主线程的Handler对象
            Field field = clazz.getDeclaredField("mH");
            field.setAccessible(true);
            Handler mH = (Handler) field.get(ActivityThread.currentActivityThread());

            //获取Handler的mCallback对象
            Class clazzHandler = Handler.class;
            Field mCallbackField = clazzHandler.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            //这里是获取mCallback的值，有可能为空
            Handler.Callback mCallback = (Handler.Callback) mCallbackField.get(mH);
            mCallbackField.set(mH, new MyCallback(mCallback));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class MyCallback implements Handler.Callback {

        private Handler.Callback mCallback;

        public MyCallback(Handler.Callback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public boolean handleMessage(Message msg) {

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
}
