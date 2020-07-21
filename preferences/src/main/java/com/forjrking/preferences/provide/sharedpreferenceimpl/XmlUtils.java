package com.forjrking.preferences.provide.sharedpreferenceimpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


final class XmlUtils {
    private static Class<?> mClass;
    private static Method mReadMapXmlMethod;
    private static Method mWriteMapXmlMethod;

    public static boolean init() {
        try {
            mClass = Class.forName("com.android.internal.util.XmlUtils");
            mReadMapXmlMethod = mClass.getMethod("readMapXml", new Class[]{InputStream.class});
            mWriteMapXmlMethod = mClass.getMethod("writeMapXml", new Class[]{Map.class, OutputStream.class});
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

    public static final HashMap readMapXml(InputStream in) {
        try {
            return (HashMap) mReadMapXmlMethod.invoke(null, in);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final void writeMapXml(Map val, OutputStream out) {
        try {
            mWriteMapXmlMethod.invoke(null, new Object[]{val, out});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
