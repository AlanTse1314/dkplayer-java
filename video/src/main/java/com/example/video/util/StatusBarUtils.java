package com.example.video.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.WindowManager;

public class StatusBarUtils {

    private static NavMode getNavMode(Context mContext) {
        NavMode mode = null;
        int modeInt = getSystemIntegerRes(mContext, "config_navBarInteractionMode");
        for (NavMode m : NavMode.values()) {
            if (m.resValue == modeInt) {
                mode = m;
            }
        }
        return mode;
    }

    private static int getSystemIntegerRes(Context context, String resName) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(resName, "integer", "android");
        if (resId != 0) {
            return res.getInteger(resId);
        } else {
            return -1;
        }
    }

    public enum NavMode {
        THREE_BUTTONS(false, 0),
        TWO_BUTTONS(true, 1),
        NO_BUTTON(true, 2);  //没有导航按钮
        public final boolean hasGestures;
        public final int resValue;

        NavMode(boolean hasGestures, int resValue) {
            this.hasGestures = hasGestures;
            this.resValue = resValue;
        }
    }

    /**
     * 如果有工具栏则 返回true
     */
    public static boolean existenceStatusBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NavMode mode = StatusBarUtils.getNavMode(context);
            return mode == NavMode.NO_BUTTON;
        }
        WindowManager.LayoutParams attrs = PlayUtil.getWindow(context).getAttributes();
        return (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

}
