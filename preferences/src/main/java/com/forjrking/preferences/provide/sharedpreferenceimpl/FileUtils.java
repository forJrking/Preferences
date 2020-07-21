package com.forjrking.preferences.provide.sharedpreferenceimpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class FileUtils {
    private static Class<?> mClass;
    private static Method mSetPermissionsMethod;

    public static int S_IRWXU = 00700;
    public static int S_IRUSR = 00400;
    public static int S_IWUSR = 00200;

    public static int S_IRWXG = 00070;
    public static int S_IRGRP = 00040;
    public static int S_IWGRP = 00020;

    public static int S_IROTH = 00004;
    public static int S_IWOTH = 00002;
    public static int S_IXOTH = 00001;


    public static boolean init() {
        try {
            mClass = Class.forName("android.os.FileUtils");
            mSetPermissionsMethod = mClass.getMethod("setPermissions", new Class[]{String.class, int.class, int.class, int.class});
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int setPermissions(String file, int mode, int uid, int gid) {
        try {
            return (Integer) mSetPermissionsMethod.invoke(null, new Object[]{file, mode, uid, gid});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * DES: 把磁盘缓存强制刷入磁盘中 同步
     * TIME: 2019/1/15 0015 上午 10:17
     */
    public static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) {
                stream.getFD().sync();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
