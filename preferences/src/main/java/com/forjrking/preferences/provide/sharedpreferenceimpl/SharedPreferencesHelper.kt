package com.forjrking.preferences.provide.sharedpreferenceimpl;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * http://gityuan.com/2017/06/18/SharedPreferences/ 全面剖析
 * https://www.jianshu.com/p/3f64caa567e5  8.0以上已经有优化不需要这样操作了
 * DES: sp的hook代理
 * CHANGED: 岛主
 * TIME: 2019/1/15 0015 上午 10:16
 */
public class SharedPreferencesHelper {
    private static boolean mCanUseCustomSp = true;
    private static boolean mHasCheck = false;

    private static Method mGetSharedPrefsFileMethod;

    private static volatile ExecutorService sCachedThreadPool;

    public static synchronized boolean canUseCustomSp() {
        if (!mHasCheck) {
            mHasCheck = true;
            if (!QueuedWork.init() || !FileUtils.init() || !XmlUtils.init()) {
                mCanUseCustomSp = false;
            }
        }

        return mCanUseCustomSp;
    }

    private static File getSharedPrefsFile(Context context, String name) {
        if (mGetSharedPrefsFileMethod == null) {
            try {
                mGetSharedPrefsFileMethod = context.getClass().getMethod("getSharedPrefsFile", String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        File prefsFile = null;
        if (mGetSharedPrefsFileMethod != null) {
            try {
                prefsFile = (File) mGetSharedPrefsFileMethod.invoke(context, name);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return prefsFile;
    }

    /**
     * 暂时只有这里需要用到cachedThreadPool。以后如果有更多业务需要用了再考虑提供统一接口。
     * 使用cachedThreadPool是为了保证任务总是立即调度而不需要等待，并减少碎片化任务频繁创建线程的耗时
     */
    static void execute(Runnable task) {
        if (sCachedThreadPool == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (sCachedThreadPool == null) {
                    sCachedThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                            30L, TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(), new SPThreadFactory());
                }
            }
        }
        sCachedThreadPool.execute(task);
    }

    private static class SPThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        SPThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "sp-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    /**
     * DES: 静态保存的内存值
     * TIME: 2019/1/15 0015 上午 9:53
     */
    private static final HashMap<String, SharedPreferencesImpl> sSharedPrefs = new HashMap<String, SharedPreferencesImpl>();

    /**
     * DES: 返回hook的SharedPreferences实例
     * TIME: 2019/1/15 0015 上午 9:54
     */
    public static SharedPreferences getSharedPreferences(Context context, String name, int mode) {
        if (!SharedPreferencesHelper.canUseCustomSp()) {
            return context.getSharedPreferences(name, mode);
        }
        SharedPreferencesImpl sp;

        synchronized (sSharedPrefs) {
            sp = sSharedPrefs.get(name);
            if (sp == null) {
                File prefsFile = SharedPreferencesHelper.getSharedPrefsFile(context, name);
                sp = new SharedPreferencesImpl(prefsFile, mode);
                sSharedPrefs.put(name, sp);
                return sp;
            }
        }

        if ((mode & Context.MODE_MULTI_PROCESS) != 0) {
            // If somebody else (some other process) changed the prefs
            // file behind our back, we reload it.  This has been the
            // historical (if undocumented) behavior.
            sp.startReloadIfChangedUnexpectedly();
        }

        return sp;
    }
}
