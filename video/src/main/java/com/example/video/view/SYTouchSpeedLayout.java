package com.example.video.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.R;
import com.example.video.enume.DoubleSpeedEnume;

public class SYTouchSpeedLayout extends FrameLayout {
    TextView ffStatusText;
    DoubleSpeedEnume doubleSpeed=DoubleSpeedEnume.SPEED_X2;

    public SYTouchSpeedLayout(@NonNull Context context) {
        this(context, null);
    }

    public SYTouchSpeedLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SYTouchSpeedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.player_speed_layout, this, true);
        ImageView ffView = findViewById(R.id.ff_view);
        ffStatusText = findViewById(R.id.ff_status_text);
        ffStatusText.setText("快进x2");
        ((AnimationDrawable) ffView.getDrawable()).start();
    }

    public void show(DoubleSpeedEnume doubleSpeed) {
        setVisibility(View.VISIBLE);
        this.doubleSpeed=doubleSpeed;
        ffStatusText.setText(("快进x"+doubleSpeed.getCode()).replace(".0",""));
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public DoubleSpeedEnume getDoubleSpeed() {
        return doubleSpeed;
    }

    public void updateStatus(boolean isLoading) {
        ffStatusText.setText(isLoading ? "loading" : ("快进x"+doubleSpeed.getCode()).replace(".0",""));
    }
}
