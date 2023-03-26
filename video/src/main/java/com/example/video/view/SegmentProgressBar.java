package com.example.video.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;

import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;

import com.example.video.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 分段式进度条
 *
 * @author qiu
 * @date 2021/3/2 14:34
 */


@SuppressLint("AppCompatCustomView")
public class SegmentProgressBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    /**
     * 进度条两端圆角
     */
    private float progressLeftAndRightRoundedCorners = 0f;
    /**
     * 默认的进度条背景色
     */
    private int defaultBackgroundColor;
    /**
     * 分块颜色
     */
    private int blockFillColor;
    /**
     * 进度
     */
    private int progress;
    /**
     * 缓存进度
     */
    private int secondaryProgress;
    /**
     * 存放分块索引及状态
     */
    private Map<Integer, Boolean> guageMap = new HashMap<Integer, Boolean>();
    /**
     * 用于怀疑分块及背景色
     */
    private Paint mPaint = new Paint();

    private OnSeekBarChangeListener mOnSeekBarChangeListener;


    //<?xml version="1.0" encoding="utf-8"?>
    //<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    //
    //    <item android:id="@android:id/secondaryProgress">
    //        <clip>
    //            <shape>
    //                <solid android:color="#9563ED" />
    //                <size android:height="@dimen/block_height" />
    //                <corners android:radius="1.5dip" />
    //            </shape>
    //        </clip>
    //    </item>
    //    <item android:id="@android:id/progress">
    //        <clip>
    //            <shape>
    //                <solid android:color="#fff85959" />
    //                <size android:height="@dimen/block_height" />
    //                <corners android:radius="1.5dip" />
    //            </shape>
    //        </clip>
    //    </item>
    //</layer-list>
    //这是分块高度
    //默认填充
    private float blockHeight;
    private float defaultDlockHeight;


    /**
     * 除padding外的视图宽度
     */
    private float mRealWidth;
    /**
     * 除padding外的视图宽度
     */
    private float measureHeight;
    /**
     * 除padding外的视图宽度
     */
    private float measureWidth;

    /**
     * 块默认数量
     */
    private int blockTotal = 0;
    /**
     * 块的左右间距
     */
    private float blockLeftAndRightSpacing = 0;

    private int cacheProgressColor;
    private int progressColor;


    /**
     * 分段宽度
     */
    private float progressWith = 0;
    /**
     * 分段宽度
     */
    private Handler handler;


    public SegmentProgressBar(Context context) {
        this(context, null);
    }

    public SegmentProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultValues(context, attrs, defStyleAttr);
        handler = new Handler(context.getMainLooper());
        super.setOnSeekBarChangeListener(this);

    }

    /**
     * 初始化布局
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initDefaultValues(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SegmentProgressBar, defStyleAttr, defStyleAttr);
        if (arr != null) {
            blockTotal = arr.getInt(R.styleable.SegmentProgressBar_blockTotal, 0);
            progressLeftAndRightRoundedCorners = arr.getDimension(R.styleable.SegmentProgressBar_progressLeftAndRightRoundedCorners, 0);
            blockLeftAndRightSpacing = arr.getDimension(R.styleable.SegmentProgressBar_blockLeftAndRightSpacing, 0);

            blockHeight = arr.getDimension(R.styleable.SegmentProgressBar_blockHeight, 0);
            defaultDlockHeight = blockHeight;
            defaultBackgroundColor = arr.getColor(R.styleable.SegmentProgressBar_progressBackground, Color.parseColor("#DDE4F4"));
            blockFillColor = arr.getColor(R.styleable.SegmentProgressBar_blockFillColor, Color.parseColor("#3D7EFE"));
            cacheProgressColor = arr.getColor(R.styleable.SegmentProgressBar_cacheProgressColor, Color.parseColor("#9563ED"));
            progressColor = arr.getColor(R.styleable.SegmentProgressBar_progressColor, Color.parseColor("#f85959"));
        }
        arr.recycle();

    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }


    public void setProgressLeftAndRightRoundedCorners(float progressLeftAndRightRoundedCorners) {
        this.progressLeftAndRightRoundedCorners = progressLeftAndRightRoundedCorners;
    }

    public void setDefaultBackgroundColor(int defaultBackgroundColor) {
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    public void setBlockFillColor(int blockFillColor) {
        this.blockFillColor = blockFillColor;
    }

    public void setBlockHeight(float blockHeight) {
        if (blockHeight < 0) {
            this.blockHeight = defaultDlockHeight;
        } else {
            this.blockHeight = blockHeight;
        }
        invalidate();

    }

    public void setBlockLeftAndRightSpacing(float blockLeftAndRightSpacing) {
        this.blockLeftAndRightSpacing = blockLeftAndRightSpacing;
    }

    @Override
    public synchronized void setProgress(int progress) {
        this.progress = progress;
        super.setProgress(progress);
    }

    @Override
    public synchronized void setSecondaryProgress(int progress) {
        if (getMax() >0) {
            secondaryProgress = Math.max(progress, 0);
            super.setSecondaryProgress(secondaryProgress);
        }
    }


    /**
     * 最大值
     *
     * @param blockTotal
     */
    public void setBlockTotal(int blockTotal) {
        if (this.blockTotal != blockTotal) {
            this.blockTotal = blockTotal;
            if (blockTotal <= 0) {
                clearAll();
            } else {
                updateBlockWidth(blockTotal);
                invalidate();
            }

        }
    }

    private void updateBlockWidth(int blockTotal) {
        if (blockTotal > 0) {
            progressWith = (mRealWidth - (blockTotal - 1) * blockLeftAndRightSpacing) / blockTotal;
        }
    }

    /**
     * 清除所有预读块标识
     */
    public void clearAll() {
        guageMap.clear();
        invalidate();
    }

    /**
     * 清除指定块标识
     */
    public void clear(int mPosition) {
        guageMap.put(mPosition, false);
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //真正的宽度值是减去左右padding
        mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
        measureHeight = getMeasuredHeight();
        measureWidth = getMeasuredWidth();
        //更新块宽度
        updateBlockWidth(blockTotal);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //真正的宽度值是减去左右padding
        mRealWidth = w - getPaddingRight() - getPaddingLeft();
        //更新块宽度
        updateBlockWidth(blockTotal);
        invalidate();
    }


    @SuppressLint({"ResourceAsColor", "DrawAllocation"})
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (getVisibility() == VISIBLE) {

            //计算进度条中心点位置
            float top, bottom;
            if (blockHeight > 0) {
                top = measureHeight / 2f - blockHeight / 2f;
                bottom = measureHeight / 2f + blockHeight / 2f;
            } else {
                //默认匹配父类
                top = 0;
                bottom = measureHeight;
            }
            //设置进度条指示器的背景色
            mPaint.setColor(defaultBackgroundColor);
            //设置画笔类型
            mPaint.setStyle(Paint.Style.FILL);
            //去除锯齿
            mPaint.setAntiAlias(true);
            RectF backgroundRectF = new RectF(0, top, measureWidth, bottom);
            canvas.drawRoundRect(backgroundRectF, progressLeftAndRightRoundedCorners, progressLeftAndRightRoundedCorners, mPaint);

            //开始绘制分块
            mPaint.setColor(blockFillColor);
            try {
                if (guageMap.size() > 0) {
                    for (Integer i : guageMap.keySet()) {
                        if (!(i > blockTotal) && guageMap.get(i)) {
                            RectF rectF = new RectF(getPaddingLeft() + (i - 1) * (progressWith + blockLeftAndRightSpacing), top,
                                    progressWith + (i - 1) * (progressWith + blockLeftAndRightSpacing), bottom);
                            canvas.drawRect(rectF, mPaint);
                        }
                    }
                }
            } catch (NullPointerException n) {

            }
            //绘制缓存
            if (secondaryProgress > 0) {
                float width = mRealWidth / getMax() * secondaryProgress;
                mPaint.setColor(cacheProgressColor);
                RectF rectF = new RectF(getPaddingLeft(), top,
                        width + getPaddingLeft(), bottom);
                canvas.drawRoundRect(rectF, progressLeftAndRightRoundedCorners, secondaryProgress == getMax() ? progressLeftAndRightRoundedCorners : 0, mPaint);
            }

            top = measureHeight / 2f - defaultDlockHeight / 2f;
            bottom = measureHeight / 2f + defaultDlockHeight / 2f;


            //绘制进度
            if (progress > 0) {
                float width = mRealWidth / getMax() * progress;
                mPaint.setColor(progressColor);
                RectF rectF = new RectF(getPaddingLeft(), top,
                        width + getPaddingLeft(), bottom);
                canvas.drawRoundRect(rectF, progressLeftAndRightRoundedCorners, progress == getMax() ? progressLeftAndRightRoundedCorners : 0, mPaint);
            }
            super.onDraw(canvas);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        invalidate();
    }

    public void OnParsingIsComplete(int tsTotal) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //设置最大ts切片数量
                setBlockTotal(tsTotal);
            }
        });

    }

    public void OnPlannedSpeed(int index, boolean status) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //更新ts预读位置
                if (index > 0 && !(index > blockTotal)) {
                    if (null == guageMap.get(index)) {
                        guageMap.put(index, status);
                        invalidate();
                        return;
                    }
                    if (!guageMap.get(index)) {
                        guageMap.put(index, status);
                        invalidate();
                    }
                }
            }
        });

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        }
        if (fromUser) {
            this.progress = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }
}

