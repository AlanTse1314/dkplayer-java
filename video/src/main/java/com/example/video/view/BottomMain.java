package com.example.video.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.R;
import com.example.video.configure.UIConfigure;
import com.example.video.enume.WindowEnume;
import com.example.video.util.LogUtility;

public class BottomMain extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "Video->BottomMain";
    private UIConfigure uiConfigure;
    private WindowEnume windowEnume = WindowEnume.TYPE_ORDINARY;
    private UIOnClickListener uiOnClickListener;
    private TextView current;
    private TextView total;
    private TextView multiple;
    private SegmentProgressBar bottomSeekProgress;
    private TextView anthology;
    private ImageView btnPlay;
    private LinearLayout customView;
    private ImageView fullScreen;



    public BottomMain(@NonNull Context context) {
        this(context, null);
    }

    public BottomMain(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMain(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
        init(context);
    }


    private void init(Context context) {
        View v = View.inflate(getContext(), R.layout.layout_video_bottom, this);
        v.findViewById(R.id.gen).setBackgroundResource(R.drawable.bottom_bg);

        current = v.findViewById(R.id.current);
        total = v.findViewById(R.id.total);
        multiple = v.findViewById(R.id.multiple);
        bottomSeekProgress = v.findViewById(R.id.bottom_seek_progress);
        anthology = v.findViewById(R.id.anthology);
        btnPlay = v.findViewById(R.id.btn_play);
        customView = v.findViewById(R.id.custom_view);
        fullScreen = v.findViewById(R.id.full_screen);

        bottomSeekProgress.setMax(100);
        bottomSeekProgress.setProgress(0);
        fullScreen.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        multiple.setOnClickListener(this);
        anthology.setOnClickListener(this);
    }


    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        bottomSeekProgress.setOnSeekBarChangeListener(l);
    }

    public void setPlayStat(boolean isPlay) {
        btnPlay.setImageResource(isPlay ? R.drawable.pause : R.drawable.play);
    }

    public void setCurrent(String currentTime) {
        this.current.setText(currentTime);
    }

    public void setTotal(String totalTime) {
        this.total.setText(totalTime);

    }

    public void setProgress(int progress) {
        bottomSeekProgress.setProgress(progress);

    }
    public int getMaxProgress() {
       return bottomSeekProgress.getMax();
    }
    public void setMaxProgress(int maxProgress) {
        bottomSeekProgress.setMax(maxProgress);
    }

    public void setSecondaryProgress(int secondaryProgress) {
        bottomSeekProgress.setSecondaryProgress(secondaryProgress);
    }

    public int getParProgress() {
        return bottomSeekProgress.getProgress();
    }

    /**
     * 用户自定义控件
     */
    @Override
    public void addView(View view) {
        customView.addView(view, new LinearLayout.LayoutParams(-2, -1));
    }

    public void setWindowEnume(WindowEnume windowEnume) {
        this.windowEnume = windowEnume;
        if (this.windowEnume != null) {
            switch (windowEnume) {
                case TYPE_ORDINARY:
                    //选集
                    anthology.setVisibility(View.GONE);
                    if (uiConfigure != null) {
                        //倍速
                        multiple.setVisibility(uiConfigure.isMultiple() ? VISIBLE : GONE);
                        //全屏
                        fullScreen.setVisibility(uiConfigure.isFullScreen() ? VISIBLE : GONE);
                        bottomSeekProgress.setVisibility(uiConfigure.isScreenProjection() ? VISIBLE : INVISIBLE);
                    } else {
                        multiple.setVisibility(VISIBLE);
                        fullScreen.setVisibility(VISIBLE);
                        bottomSeekProgress.setVisibility(VISIBLE);
                    }
                    fullScreen.setImageResource(R.drawable.qp);
                    break;
                case TYPE_FULL_VERTICAL_SCREEN:
                case TYPE_FULL_HORIZONTAL_SCREEN:
                    //小窗关闭按钮
                    if (uiConfigure != null) {
                        //选集
                        anthology.setVisibility(uiConfigure.isAnthology() ? VISIBLE : GONE);
                        //倍速
                        multiple.setVisibility(uiConfigure.isMultiple() ? VISIBLE : GONE);
                        //全屏
                        fullScreen.setVisibility(uiConfigure.isFullScreen() ? VISIBLE : GONE);
                        bottomSeekProgress.setVisibility(uiConfigure.isScreenProjection() ? VISIBLE : INVISIBLE);
                    } else {
                        multiple.setVisibility(VISIBLE);
                        fullScreen.setVisibility(VISIBLE);
                        anthology.setVisibility(VISIBLE);
                        bottomSeekProgress.setVisibility(VISIBLE);
                    }
                    fullScreen.setImageResource(R.drawable.tcqp);
                    break;
                case TYPE_SMALL_WINDOW:
                    fullScreen.setImageResource(R.drawable.qp);
                    multiple.setVisibility(GONE);
                    fullScreen.setVisibility(VISIBLE);
                    anthology.setVisibility(GONE);
                    bottomSeekProgress.setVisibility(INVISIBLE);
                    break;
                case TYPE_SCREEN_PROJECTION://投屏状态
                    //选集
                    anthology.setVisibility(GONE);
                    //倍速
                    multiple.setVisibility(GONE);
                    //全屏
                    fullScreen.setVisibility(GONE);
                    break;

            }
        }
    }

    public void setUiOnClickListener(UIOnClickListener uiOnClickListener) {
        this.uiOnClickListener = uiOnClickListener;
    }

    @Override
    public void onClick(View v) {
        if (uiOnClickListener != null) {
            if (v.getId() == R.id.full_screen) {
                uiOnClickListener.onClickFullScreen(v);
            }
            if (v.getId() == R.id.btn_play) {
                uiOnClickListener.onClickBtnPlay(v);
            }
            if (v.getId() == R.id.multiple) {
                uiOnClickListener.onClickMultiple(v);
            }
            if (v.getId() == R.id.anthology) {
                uiOnClickListener.onClickAnthology(v);
            }
        } else {
            LogUtility.d(TAG, "未设置按钮单击事件");
        }
    }

    public WindowEnume getWindowEnume() {
        return windowEnume;
    }
}
