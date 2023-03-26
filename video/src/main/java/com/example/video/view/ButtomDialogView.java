package com.example.video.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.video.R;


public class ButtomDialogView extends Dialog {
    private boolean iscancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable;//控制返回键是否dismiss
    private View view;
    private Context context;

    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public ButtomDialogView(Context context, View view, boolean isCancelable, boolean isBackCancelable) {
        super(context, R.style.DialogTheme);
        this.isBackCancelable=isBackCancelable;
        this.context = context;
        this.view = view;
        this.iscancelable = isCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(view);//这行一定要写在前面
        setCancelable(iscancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(isBackCancelable);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM|Gravity.CENTER);
        WindowManager manager = window.getWindowManager();
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
        int min = Math.min(width, height);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width=min;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

}