package xyz.doikki.videoplayer.util;

import android.content.Context;

import java.lang.reflect.Method;

public class ContextUtil {
    private static Context mContext;

    public static Context getsContext() {
        if (mContext == null) {
            try {
                Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                mContext = (Context) method.invoke(null, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mContext;
    }

}
