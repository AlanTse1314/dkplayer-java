package com.example.video.view;

import android.content.Context;

import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.R;
import com.example.video.configure.UIConfigure;
import com.example.video.enume.WindowEnume;

public class TopMain extends FrameLayout implements View.OnClickListener {
    private UIConfigure uiConfigure;
    private TopRight topRight;
    private ImageView back;

    private TextView title;
    private WindowEnume windowEnume = WindowEnume.TYPE_ORDINARY;

    private UIOnClickListener uiOnClickListener;

    public TopMain(@NonNull Context context) {
        this(context, null);
    }

    public TopMain(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopMain(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
        init(context);
    }

    private void init(Context context) {
        View v = View.inflate(context, R.layout.layout_video_top, this);
        findViewById(R.id.gen).setBackgroundResource(R.drawable.top_bg);
        topRight = v.findViewById(R.id.top_right);
        back = v.findViewById(R.id.back);
        title = v.findViewById(R.id.play_title);
        back.setOnClickListener(this);

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            setWindowEnume(windowEnume);
        }
    }

    /**
     * 用户自定义控件
     */
    @Override
    public void addView(View view) {
        topRight.addView(view);
    }

    public void setUiOnClickListener(UIOnClickListener uiOnClickListener) {
        this.uiOnClickListener = uiOnClickListener;
        topRight.setUiOnClickListener(uiOnClickListener);
    }

    /**
     * 设置视频标题
     */
    public void setTitle(String titleStr) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            this.title.setText(Html.fromHtml(titleStr, Html.FROM_HTML_MODE_LEGACY));
        } else {
            this.title.setText(Html.fromHtml(titleStr));
        }

    }

    public void setUiConfigure(UIConfigure uiConfigure) {
        this.uiConfigure = uiConfigure;
    }

    /**
     * 设置窗口类型
     */
    public void setWindowEnume(WindowEnume windowEnume) {
        this.windowEnume = windowEnume;
        if (this.windowEnume != null) {
            switch (windowEnume) {
                case TYPE_ORDINARY:
                    //小窗关闭按钮
                    topRight.setWindowsCloseVisibility(View.GONE);
                    //电池
                    topRight.setBatteryLevelVisibility(View.GONE);
                    //设置
                    topRight.setSettingVisibility(View.GONE);
                    back.setVisibility(GONE);
                    title.setPadding(getResources().getDimensionPixelSize(R.dimen.dp_14),0,0,0);
                    if (uiConfigure != null) {
                        //小窗
                        topRight.setSmallWindowVisibility(uiConfigure.isSmallWindow() ? VISIBLE : GONE);
                        //投屏
                        topRight.setScreenProjectionVisibility(uiConfigure.isScreenProjection() ? VISIBLE : GONE);
                    } else {
                        topRight.setSmallWindowVisibility(VISIBLE);
                        topRight.setScreenProjectionVisibility(VISIBLE);
                    }
                    break;
                case TYPE_FULL_VERTICAL_SCREEN:
                case TYPE_FULL_HORIZONTAL_SCREEN:
                    //小窗关闭按钮
                    topRight.setWindowsCloseVisibility(View.GONE);
                    if (uiConfigure != null) {
                        //电池
                        topRight.setBatteryLevelVisibility(uiConfigure.isBatteryLevel() ? VISIBLE : GONE);
                        //设置
                        topRight.setSettingVisibility(uiConfigure.isSetting() ? View.VISIBLE : GONE);
                        //投屏
                        topRight.setScreenProjectionVisibility(uiConfigure.isScreenProjection() ? View.VISIBLE : GONE);
                        //小窗
                        topRight.setSmallWindowVisibility(uiConfigure.isSmallWindow() ? View.VISIBLE : GONE);
                        //返回
                        back.setVisibility(uiConfigure.isBack() ? VISIBLE : GONE);
                        title.setPadding(uiConfigure.isBack()?0:getResources().getDimensionPixelSize(R.dimen.dp_14),0,0,0);

                    } else {
                        title.setPadding(0,0,0,0);
                        //电池
                        topRight.setBatteryLevelVisibility(VISIBLE);
                        //设置
                        topRight.setSettingVisibility(VISIBLE);
                        //投屏
                        topRight.setScreenProjectionVisibility(VISIBLE);
                        //小窗
                        topRight.setSmallWindowVisibility(VISIBLE);
                        //返回
                        back.setVisibility(VISIBLE);
                    }
                    break;
                case TYPE_SMALL_WINDOW:
                    //电池
                    topRight.setBatteryLevelVisibility(GONE);
                    //设置
                    topRight.setSettingVisibility(GONE);
                    //投屏
                    topRight.setScreenProjectionVisibility(GONE);
                    //小窗
                    topRight.setSmallWindowVisibility(View.GONE);
                    //返回
                    back.setVisibility(GONE);
                    //标题
                    title.setPadding(getResources().getDimensionPixelSize(R.dimen.dp_14),0,0,0);
                    //小窗关闭按钮
                    topRight.setWindowsCloseVisibility(View.VISIBLE);
                    break;
                case TYPE_SCREEN_PROJECTION://投屏状态
                    //选集
                    topRight.setVisibility(GONE);
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            if (uiOnClickListener != null) {
                uiOnClickListener.onClickBack((ImageView) v);
            }
        }
    }

    public WindowEnume getWindowEnume() {
        return windowEnume;
    }
}
