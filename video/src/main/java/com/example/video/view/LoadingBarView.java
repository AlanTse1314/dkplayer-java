package com.example.video.view;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;

import com.example.video.util.LogUtility;

public class LoadingBarView {
    private Context mContext;
    private View view;
    private View parentView;
    private boolean isShow = false;
    private int width;
    private int effectWidth;
    private TranslateAnimation animation;

    public LoadingBarView(Context mContext, View view) {
        this.mContext = mContext;
        this.view = view;
        this.width = view.getLayoutParams().width;
    }

    public void show() {
        if (!this.isShow) {
            this.isShow = true;
            parentView = (View) view.getParent();
            if (effectWidth != parentView.getMeasuredWidth() || animation == null) {
                this.effectWidth = parentView.getMeasuredWidth();
                this.animation = new TranslateAnimation((float) (-width), (float) effectWidth, 0.0f, 0.0f);
                this.animation.setDuration(3000);
                this.animation.setRepeatCount(-1);
            }
            this.view.setVisibility(View.VISIBLE);
            this.view.clearAnimation();
            this.view.startAnimation(this.animation);

            //对方向改变进行特效刷新
            ViewTreeObserver vto = parentView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (isShow) {
                        if (parentView.getMeasuredWidth() > 0) {
                            if (effectWidth != parentView.getMeasuredWidth()) {
                                effectWidth = parentView.getMeasuredWidth();
                                view.clearAnimation();
                                animation = new TranslateAnimation((float) (-width), (float) effectWidth, 0.0f, 0.0f);
                                animation.setDuration(3000);
                                animation.setRepeatCount(-1);
                                view.clearAnimation();
                                view.startAnimation(animation);
                            }
                        }
                    } else {
                        parentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }


    public void hide() {
        if (this.isShow) {
            LogUtility.d("LoadingBarView", "hide");
            this.isShow = false;
            this.view.clearAnimation();
            this.view.setVisibility(View.GONE);
        }
    }
}
