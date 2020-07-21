package com.forjrking.preferences.provide.sharedpreferenceimpl;

import android.os.Build;
import android.os.Handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;


final class QueuedWork {

    // The set of Runnables that will finish or wait on any async
    // activities started by the application.
    private static final ConcurrentLinkedQueue<Runnable> sPendingWorkFinishers =
            new ConcurrentLinkedQueue<Runnable>();

    private static volatile boolean mCustomWaitToFinish;

    private static Class<?> mClass;
    private static Method mAddMethod;
    private static Method mRemoveMethod;
    private static ExecutorService mExecutorService;
    private static Handler mHandler;

    public static boolean init() {
        if (Build.VERSION.SDK_INT >= 28) {
            //android p 以后禁止反射 QueuedWork.getHandler 接口，所以直接使用系统的sp实现
            return false;
        }

        try {
            mClass = Class.forName("android.app.QueuedWork");
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    mAddMethod = mClass.getMethod("addFinisher", Runnable.class);
                    mRemoveMethod = mClass.getMethod("removeFinisher", Runnable.class);
                    Method method = mClass.getDeclaredMethod("getHandler", new Class[]{});
                    method.setAccessible(true);
                    mHandler = (Handler) method.invoke(null, new Object[]{});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mAddMethod == null || mRemoveMethod == null || mHandler == null) {
                mAddMethod = mClass.getMethod("add", Runnable.class);
                mRemoveMethod = mClass.getMethod("remove", Runnable.class);

                Method method = mClass.getMethod("singleThreadExecutor", new Class[]{});
                mExecutorService = (ExecutorService) method.invoke(null, new Object[]{});
            }
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void invokeClassMethod(Method method, Object arg) {
        try {
            method.invoke(null, arg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void add(Runnable finisher) {
        if (mCustomWaitToFinish) {
            sPendingWorkFinishers.add(finisher);
        } else {
            invokeClassMethod(mAddMethod, finisher);
        }
    }

    public static void remove(Runnable finisher) {
        boolean success = mCustomWaitToFinish && sPendingWorkFinishers.remove(finisher);
        if (!success) {
            invokeClassMethod(mRemoveMethod, finisher);
        }
    }

    public static void postRunnable(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    public static ExecutorService singleThreadExecutor() {
        return mExecutorService;
    }

    public static void setCustomWaitToFinish(boolean enable) {
        if (mCustomWaitToFinish != enable) {
            mCustomWaitToFinish = enable;
            if (!enable) {
                waitToFinish();
            }
        }
    }

    public static void waitToFinish() {
        Runnable toFinish;
        while ((toFinish = sPendingWorkFinishers.poll()) != null) {
            toFinish.run();
        }
    }
}
