package com.example.video.util;//package com.sy.play.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.video.enume.PlayDirectionEnume;

import java.util.List;

public class PlayUtil {
    private static final String TAG = "PlayUtil";
    private static StatusBar statusBar;
    public static final int REQUEST_OVERLAY_CODE = 11;
    public static final String VIEW_ROOT_IMPL = "android.view.ViewRootImpl";

    private static class StatusBar {
        boolean TOOL_BAR_EXIST=true;

        public StatusBar(boolean TOOL_BAR_EXIST) {
            this.TOOL_BAR_EXIST = TOOL_BAR_EXIST;
        }
    }

    public static int SYSTEM_UI = 0;

    /**
     * 保存进度
     */
    public static synchronized void saveProgress(Context context, String key, long position, long totalTime) {
        if (position > -1 && totalTime > position && ObjectUtil.notNull(key)) {
            new Thread(Looper.getMainLooper().getThread()) {
                @Override
                public void run() {
                    SharedPreferences spn = context.getSharedPreferences("SY_PROGRESS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = spn.edit();
                    editor.putLong("newVersion:" + key, position).apply();
                }
            }.start();
        }
    }

    public static void removeView(View view) {
        Object obj = view.getParent();
        if (null != obj) {
            if (obj instanceof ViewGroup) {
                ((ViewGroup) obj).removeView(view);
            }
        }
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context 上下文
     */
    public static synchronized void setTopApp(Context context) {
        if (isBackground(context)) {
            Intent intent = new Intent(context.getApplicationContext(), scanForActivity(context).getClass());
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.getApplicationContext().startActivity(intent);
        }


    }
//    public void 返回应用() {
//        Intent intent = new Intent(this.getApplicationContext(), mainActivity.class);
//        intent.setFlags(335544320);
//        this.getApplicationContext().startActivity(intent);
//    }


    /**
     * 判断是否开启悬浮窗口权限，否则，跳转开启页
     */
    public static boolean haveSmallWindowPermission(Context context) {
        boolean suspendedPermissions = canDrawOverlays(context);
        if (!suspendedPermissions) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            scanForActivity(context).startActivityForResult(intent, REQUEST_OVERLAY_CODE);
        }
        return suspendedPermissions;
    }

    private static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            if (Settings.canDrawOverlays(context)) {
                return true;
            }
            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) {
                    return false; //getSystemService might return null
                }
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 读取进度
     */
    public static long getSavedProgress(Context context, String key) {
        if (ObjectUtil.notNull(key)) {
            SharedPreferences spn = context.getSharedPreferences("SY_PROGRESS",
                    Context.MODE_PRIVATE);
            return spn.getLong("newVersion:" + key, 0);
        }
        return 0;
    }

    @SuppressLint("NewApi")
    public static void showSystemUI(Context context) {
        getWindow(context).getDecorView().setSystemUiVisibility(SYSTEM_UI);
    }

    @SuppressLint("RestrictedApi")
    public static void showStatusBar(Context context) {
        if (existenceStatusBar(context)) {
            getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        hiddenBangs(context, true);
    }


    public static Window getWindow(Context context) {
        return scanForActivity(context).getWindow();
    }

    //改变方向
    public static void setRequestedOrientation(Context context, PlayDirectionEnume playDirectionEnume) {
        if (scanForActivity(context) != null) {
            LogUtility.d(TAG, "方向改变 " + playDirectionEnume.getName());
            scanForActivity(context).setRequestedOrientation(playDirectionEnume.getCode());
        }
    }


    public synchronized static void addWindowsContentView(Context context, View view, ViewGroup.LayoutParams layoutParams) {
        removeView(view);
        getWindow(context).addContentView(view, layoutParams);
    }

    //如果是沉浸式的，全屏前就没有状态栏
    @SuppressLint("RestrictedApi")
    public static void hideStatusBar(Context context) {
        if (existenceStatusBar(context)) {
            getWindow(context).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        hiddenBangs(context, false);
    }

    public static WindowManager.LayoutParams getWindowsLp(Context context) {
        return getWindow(context).getAttributes();
    }

    private static WindowManager.LayoutParams windowParams;
    public static void hiddenBangs(Context context, boolean showBangs) {
        if (windowParams == null) {
            windowParams = getWindowsLp(context);
            windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!showBangs) {
                //设置绘图区域可以进入刘海屏区域
                windowParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            getWindow(context).setAttributes(windowParams);
        }
    }

    @SuppressLint("NewApi")
    public static void hideSystemUI(Context context) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        SYSTEM_UI = getWindow(context).getDecorView().getSystemUiVisibility();
        getWindow(context).getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {

            Activity activity = scanForActivity(((ContextWrapper) context).getBaseContext());
            return activity;
        }
        return null;
    }

    /**
     * 判断程序是否在后台
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }


    private static boolean existenceStatusBar(Context context) {
        if (statusBar == null) {
            statusBar = new StatusBar(StatusBarUtils.existenceStatusBar(context));
        }
        return statusBar.TOOL_BAR_EXIST;
    }

}