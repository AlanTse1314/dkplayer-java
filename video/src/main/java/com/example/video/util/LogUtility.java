package com.example.video.util;


import android.util.Log;

public final class LogUtility {
    private final static String TAG = "【LogUtility】";
    private final static String TAG1 = "【LogUtility】==>>";
    public static boolean bShowLog = true;

    public static void e(String msg) {
        if (bShowLog) {
            Log.e(TAG, msg);
        }
    }

    public static void e(Integer msg) {
        if (bShowLog) {
            Log.e(TAG, msg + "");
        }
    }

    public static void e(String tag, String msg) {
        if (bShowLog) {
            Log.e(TAG1 + tag, msg);
        }
    }

    public static void e(String tag, Throwable throwable) {
        if (bShowLog) {
            Log.e(TAG1 + tag, Log.getStackTraceString(throwable));
        }
    }

    public static void e(Throwable throwable) {
        if (bShowLog) {
            Log.e(TAG, Log.getStackTraceString(throwable));
        }
    }

    public static void d(String tag, String msg) {
        if (bShowLog) {
            Log.d(TAG1 + tag, msg);
        }
    }
    public static void d(String tag,String methodName ,String msg) {
        if (bShowLog) {
            Log.d(TAG1 + tag,String.format("方法名称:%s  %s",methodName,msg));
        }
    }
    public static void d(String tag,String methodName ,Integer msg) {
        if (bShowLog) {
            Log.d(TAG1 + tag,String.format("方法名称:%s  %s",methodName,msg));
        }
    }
    public static void d(String tag, Object object) {
        if (bShowLog) {
            Log.d(TAG1 + tag, object!=null?object.getClass().getName():"null");
        }
    }
    public static void d(String tag, boolean msg) {
        if (bShowLog) {
            Log.d(TAG1 + tag, msg+"");
        }
    }

    public static void d(String tag, Integer msg) {
        if (bShowLog) {
            Log.d(TAG1 + tag, msg + "");
        }
    }

    public static void d(String msg) {
        if (bShowLog) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (bShowLog) {
            Log.i(TAG1 + tag, msg);
        }
    }

    public static void i(String tag, Integer msg) {
        if (bShowLog) {
            Log.i(TAG1 + tag, msg + "");
        }
    }

    public static void i(Integer msg) {
        if (bShowLog) {
            Log.i(TAG, msg + "");
        }
    }

    public static void i(String msg) {
        if (bShowLog) {
            Log.i(TAG, msg);
        }
    }


}
