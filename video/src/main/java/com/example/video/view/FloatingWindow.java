package com.example.video.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.video.enume.PlayDirectionEnume;
import com.example.video.util.LogUtility;
import com.example.video.util.PlayUtil;


/**
 * @author 几圈年轮
 * @Email teamfamily17@163.com
 * @description 悬浮窗使用
 */
public class FloatingWindow {
    private static final String TAG = "FloatingWindow";
    private WindowManager mWindowManager;
    private View mShowView;
    private WindowManager.LayoutParams mFloatParams;
    private Context context;
    private PlayDirectionEnume directionEnume;
    private int defaultWidth, defaultHeight;
    public static int sFloatBox_Last_X = Integer.MAX_VALUE; //max_value means unset
    public static int sFloatBox_Last_Y = Integer.MAX_VALUE;
    private float mDefaultAspectRatio;

    FloatingOnTouchListener floatingOnTouchListener;
    ViewParent viewRoot;
    private Handler handler;

    public FloatingWindow(Context context, View view, float mDefaultAspectRatio) {
        this.context = context;
        this.mDefaultAspectRatio = mDefaultAspectRatio;
        if (view.getParent() != null) {
            viewRoot = view.getParent();
            PlayUtil.removeView(view);
        }
        // 悬浮窗显示视图
        mShowView = view;
        // 获取系统窗口管理服务
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        refreshCoordinates();
        mFloatParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFloatParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mFloatParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        int widthPixels = Math.min(defaultHeight, defaultWidth);
        floatingOnTouchListener = new FloatingOnTouchListener(context, mFloatParams,
                mDefaultAspectRatio, widthPixels, widthPixels / 2, mWindowManager);
        floatingOnTouchListener.setMove(false);
        floatingOnTouchListener.releaseCoordinate(directionEnume, defaultWidth, defaultHeight);
        initCoordinate();


        // 悬浮窗生成
        mWindowManager.addView(mShowView, mFloatParams);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (mWindowManager != null && mShowView != null && mFloatParams != null) {
                        if (refreshCoordinates()) {
                            updateCoordinates();
                        }
                        postDelayed(() -> handler.handleMessage(null), 300);
                    }

                }
            };
        }
        handler.handleMessage(null);
    }
    //悬浮窗实时跟随系统方向
    private boolean refreshCoordinates() {
        WindowManager manager = PlayUtil.scanForActivity(context).getWindowManager();
        int width;
        int height;
        //方法五
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            //获取的是实际显示区域指定包含系统装饰的内容的显示部分
            width = manager.getCurrentWindowMetrics().getBounds().width();
            height = manager.getCurrentWindowMetrics().getBounds().height();
        } else{
            //方法四：获取的是实际显示区域指定包含系统装饰的内容的显示部分
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getRealMetrics(dm);
            width = dm.widthPixels;
            height = dm.heightPixels;
        }
        int max = Math.max(width, height);
        int min = Math.min(width, height);
        Configuration mConfiguration = context.getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
        if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            defaultWidth = max;
            defaultHeight = min;
            if (directionEnume == null || directionEnume != PlayDirectionEnume.HORIZONTAL_SCREEN) {
                directionEnume = PlayDirectionEnume.HORIZONTAL_SCREEN;
                return true;
            }

        } else if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            defaultWidth = min;
            defaultHeight = max;
            if (directionEnume == null || directionEnume != PlayDirectionEnume.VERTICAL_SCREEN) {
                directionEnume = PlayDirectionEnume.VERTICAL_SCREEN;
                return true;
            }
        }
        return false;
    }



    public boolean onTouch(View view, MotionEvent event) {
        return floatingOnTouchListener.onTouch(mShowView, event);
    }

    /**
     * 销毁悬浮窗
     */
    public void dismiss() {
        try {
            if (mWindowManager != null && mShowView != null && mFloatParams != null && mShowView.isAttachedToWindow()) {
                PlayUtil.setTopApp(context);
                mWindowManager.removeViewImmediate(mShowView);
                mFloatParams = null;
                PlayUtil.removeView(mShowView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 更新悬浮窗位置坐标
     */
    public void updateCoordinates() {
        if (mFloatParams != null) {
            initCoordinate();
            LogUtility.d(TAG, "defaultHeight:" + defaultHeight + "  defaultWidth:" + defaultWidth + " " + directionEnume.getName());
            mWindowManager.updateViewLayout(mShowView, mFloatParams);
        }
    }

    private void initCoordinate() {

        mFloatParams.width = Math.min(defaultHeight, defaultWidth) / 3 * 2;
        mFloatParams.height = (int) (mFloatParams.width / mDefaultAspectRatio);
        floatingOnTouchListener.releaseCoordinate(directionEnume, defaultWidth, defaultHeight);
        if (directionEnume == PlayDirectionEnume.HORIZONTAL_SCREEN) {
            mFloatParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            mFloatParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        if (sFloatBox_Last_X == Integer.MAX_VALUE || sFloatBox_Last_Y == Integer.MAX_VALUE) {
            mFloatParams.gravity = Gravity.TOP;
            int y = defaultHeight - mFloatParams.height;
            mFloatParams.x = (defaultWidth - mFloatParams.width) / 2;
            mFloatParams.y = y;
        } else {
            //set the float view at the last position
            mFloatParams.gravity = Gravity.TOP;
            mFloatParams.x = sFloatBox_Last_X;
            mFloatParams.y = sFloatBox_Last_Y;
            sFloatBox_Last_X = Integer.MAX_VALUE;
            sFloatBox_Last_Y = Integer.MAX_VALUE;

        }
        LogUtility.d(TAG, "defaultHeight:" + defaultHeight + "  defaultWidth:" + defaultWidth + " " + directionEnume.getName());
        LogUtility.d(TAG, "VIEW  Height:" + mFloatParams.height + "  Width:" + mFloatParams.width + " " + directionEnume.getName());
        LogUtility.d(TAG, "VIEW  X:" + mFloatParams.x + "  Y:" + mFloatParams.y + " " + directionEnume.getName());


    }


}