package com.example.video.view;

import android.view.View;
import android.widget.ImageView;

public interface UIOnClickListener {

    default void onClickretryBtn(){

    }

    /**
     * 小窗被单击
     */
    void onClickSmallWindow(ImageView view);

    /**
     * 投屏被单击
     */
    void onClickScreenProjection(ImageView view);

    /**
     * 设置被单击
     */
    void onClickSetting(ImageView view);

    /**
     * 返回被单击
     */
    void onClickBack(ImageView view);

    /**
     * 小窗关闭按钮
     */
    void onClickWindowsClose(ImageView v);

    /**
     * 全屏按钮
     */
    void onClickFullScreen(View v1);

    /**
     * 播放按钮
     */
    void onClickBtnPlay(View v1);

    /**
     * 倍速
     */
    void onClickMultiple(View v1);

    /**
     * 选集
     */
    void onClickAnthology(View v1);
}
