package com.example.video.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.TextureView;

public class PlayFrameLayout extends FrameLayout {
    private OnTouchListener onTouchListener;

    public PlayFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public PlayFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        super.setOnTouchListener(onTouchListener);
        this.onTouchListener = onTouchListener;
    }

    public boolean onTouch(TextureView textureView, MotionEvent event) {
        return onTouchListener.onTouch(textureView,event);
    }
}
