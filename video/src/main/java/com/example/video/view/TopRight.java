package com.example.video.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;

import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TopRight extends FrameLayout implements View.OnClickListener {
    private ImageView smallWindow;
    private TextView systemTime;
    private ImageView batteryLevel;
    private ImageView screenProjection;
    private ImageView windowsClose;




    private LinearLayout batteryMain;
    private LinearLayout customView;//自定义VIEW

    private ImageView setting;
    private UIOnClickListener uiOnClickListener;
    public static long LAST_GET_BATTERYLEVEL_TIME = 0;
    public static int LAST_GET_BATTERYLEVEL_PERCENT = 70;
    private int widthHeight;
    private Context mContext;

    public TopRight(@NonNull Context context) {
        this(context, null);
    }

    public TopRight(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopRight(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
        mContext = context;
        widthHeight = context.getResources().getDimensionPixelSize(R.dimen.play_icon_height);
        init(context);
        initAttributes(context, attrs);
    }

    private void init(Context context) {
        View v = View.inflate(context, R.layout.layout_top_right, this);
        v.findViewById(R.id.gen).setBackgroundColor(Color.TRANSPARENT);
        smallWindow = v.findViewById(R.id.small_window);
        systemTime = v.findViewById(R.id.system_time);
        batteryLevel = v.findViewById(R.id.battery_level);
        screenProjection = v.findViewById(R.id.screen_projection);
        batteryMain = v.findViewById(R.id.battery_main);
        setting = v.findViewById(R.id.setting);
        windowsClose=v.findViewById(R.id.btn_windows_close);
        customView = v.findViewById(R.id.custom_view);
        smallWindow.setOnClickListener(this);
        screenProjection.setOnClickListener(this);
        setting.setOnClickListener(this);
        windowsClose.setOnClickListener(this);

    }

    private void initAttributes(Context context, AttributeSet attrs) {
        try {
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.TopRight);
            boolean visual = attr.getBoolean(R.styleable.TopRight_batteryMainVisual, true);
            batteryMain.setVisibility(visual ? VISIBLE : GONE);
            visual = attr.getBoolean(R.styleable.TopRight_settingVisual, true);
            setting.setVisibility(visual ? VISIBLE : GONE);
            visual = attr.getBoolean(R.styleable.TopRight_screenProjectionVisual, true);
            screenProjection.setVisibility(visual ? VISIBLE : GONE);
            visual = attr.getBoolean(R.styleable.TopRight_smallWindowVisual, true);
            smallWindow.setVisibility(visual ? VISIBLE : GONE);
            if (batteryMain.getVisibility() == VISIBLE) {
                setSystemTimeAndBattery();
            }
            attr.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUiOnClickListener(UIOnClickListener uiOnClickListener) {
        this.uiOnClickListener = uiOnClickListener;
    }

    /**
     * 投屏按钮
     */
    public void setScreenProjectionVisibility(int visibility) {
        screenProjection.setVisibility(visibility);
    }

    /**
     * 用户自定义控件
     */
    @Override
    public void addView(View view) {
        customView.addView(view, new LinearLayout.LayoutParams(widthHeight, widthHeight));
    }

    /**
     * 设置按钮
     */
    public void setSettingVisibility(int visibility) {
        setting.setVisibility(visibility);
    }

    /**
     * 小窗按钮
     */
    public void setSmallWindowVisibility(int visibility) {
        smallWindow.setVisibility(visibility);
    }


    /**
     * 小窗关闭按钮
     */
    public void setWindowsCloseVisibility(int visibility) {
        this.windowsClose.setVisibility(visibility);
    }

    /**
     * 电池
     */
    public void setBatteryLevelVisibility(int visibility) {
        batteryMain.setVisibility(visibility);
        if (visibility == VISIBLE) {
            setSystemTimeAndBattery();
        }
    }

    @Override
    public void onClick(View v) {
        if (uiOnClickListener != null) {
            if (v.getId() == R.id.setting) {
                uiOnClickListener.onClickSetting((ImageView) v);
            }
            if (v.getId() == R.id.screen_projection) {
                uiOnClickListener.onClickScreenProjection((ImageView) v);
            }
            if (v.getId() == R.id.small_window) {
                uiOnClickListener.onClickSmallWindow((ImageView) v);
            }
            if (v.getId() == R.id.btn_windows_close) {
                uiOnClickListener.onClickWindowsClose((ImageView) v);
            }
        }
    }

    public BroadcastReceiver battertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                if (batteryMain != null && batteryMain.getVisibility() == VISIBLE) {
                    int level = intent.getIntExtra("level", 0);
                    int scale = intent.getIntExtra("scale", 100);
                    int percent = level * 100 / scale;
                    LAST_GET_BATTERYLEVEL_PERCENT = percent;
                    setBatteryLevel();
                    try {
                        context.unregisterReceiver(battertReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    public void setSystemTimeAndBattery() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        systemTime.setText(dateFormater.format(date));
        if ((System.currentTimeMillis() - LAST_GET_BATTERYLEVEL_TIME) > 30000) {
            LAST_GET_BATTERYLEVEL_TIME = System.currentTimeMillis();
            mContext.registerReceiver(battertReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } else {
            setBatteryLevel();
        }
    }

    public void setBatteryLevel() {
        int percent = LAST_GET_BATTERYLEVEL_PERCENT;
        if (percent < 15) {
            batteryLevel.setBackgroundResource(R.drawable.battery_level_10);
        } else if (percent >= 15 && percent < 40) {
            batteryLevel.setBackgroundResource(R.drawable.battery_level_30);
        } else if (percent >= 40 && percent < 60) {
            batteryLevel.setBackgroundResource(R.drawable.battery_level_50);
        } else if (percent >= 60 && percent < 80) {
            batteryLevel.setBackgroundResource(R.drawable.battery_level_70);
        } else if (percent >= 80 && percent < 95) {
            batteryLevel.setBackgroundResource(R.drawable.battery_level_90);
        } else if (percent >= 95 && percent <= 100) {
            batteryLevel.setBackgroundResource(R.drawable.battery_level_100);
        }
    }

}
