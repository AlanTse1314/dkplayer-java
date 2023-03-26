package com.example.video.util;

import android.view.View;

public final class MeasureHelper {
    public static final int FIT_PARENT = 0; // without clip, 等比例填充，视频的边界小于或等于显示屏边界，可能留有黑边
    public static final int MATCH_PARENT = 1; // 不按比例，满屏播放
    public static final int FILL_PARENT = 2; // may clip，等比例填充，视频边界大于等于显示屏边界，不留黑边，一部分视频区域可能在显示屏边界之外
//    public static final int FILL_PARENT = 2; // may clip，等比例填充，视频边界大于等于显示屏边界，不留黑边，一部分视频区域可能在显示屏边界之外
    public static final int FIT_PARENT_16_9 = 3; // 16:9, 视频的边界小于或等于显示屏边界，可能留有黑边
    public static final int FIT_PARENT_18_9 = 6; // 18:9, 视频的边界小于或等于显示屏边界，可能留有黑边
    public static final int FIT_PARENT_4_3 = 4; // 4:3, 视频的边界小于或等于显示屏边界，可能留有黑边
    public static final int FIT_PARENT_CUSTOMIZE = 5; // 自定义
    public static final int FIT_PARENT_ZOOM = 7; // 自定义

    private int mVideoWidth;
    private int mVideoHeight;
    private int defaultVideoWidth;
    private int defaultVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mVideoRotationDegree;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mCurrentAspectRatio = FIT_PARENT;


    public void setVideoSize(int videoWidth, int videoHeight) {
        defaultVideoWidth = videoWidth;
        defaultVideoHeight = videoHeight;
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    public void setVideZoom(int zoomWidth, int zoomHeight,boolean isEnlarge) {
        mCurrentAspectRatio=FIT_PARENT_ZOOM;
        if (isEnlarge){
            defaultVideoHeight+=zoomHeight;
            defaultVideoWidth+=zoomWidth;
        }else{
            defaultVideoHeight-=zoomHeight;
            defaultVideoWidth-=zoomWidth;
            if(defaultVideoHeight<mMeasuredHeight){
                defaultVideoHeight=mVideoHeight;
                defaultVideoWidth=mVideoWidth;
            }
        }
    }
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mCurrentAspectRatio=FIT_PARENT_CUSTOMIZE;
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mCurrentAspectRatio==FIT_PARENT_ZOOM){

            mMeasuredWidth = defaultVideoWidth;
            mMeasuredHeight = defaultVideoHeight;
            return;
        }
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            widthMeasureSpec = widthMeasureSpec ^ heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec ^ heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec ^ heightMeasureSpec;
        }

        int width = View.getDefaultSize(defaultVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(defaultVideoHeight, heightMeasureSpec);
        if (mCurrentAspectRatio == MATCH_PARENT) {
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (defaultVideoWidth > 0 && defaultVideoHeight > 0) {
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

            float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
            float displayAspectRatio;
            switch (mCurrentAspectRatio) {
                case FIT_PARENT_16_9:
                    displayAspectRatio = 16.0f / 9.0f;
                    if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                        displayAspectRatio = 1.0f / displayAspectRatio;
                    }
                    break;
                case FIT_PARENT_18_9:
                    displayAspectRatio = 18.0f / 9.0f;
                    if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                        displayAspectRatio = 1.0f / displayAspectRatio;
                    }
                    break;
                case FIT_PARENT_4_3:
                    displayAspectRatio = 4.0f / 3.0f;
                    if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                        displayAspectRatio = 1.0f / displayAspectRatio;
                    }
                    break;
                case FIT_PARENT_CUSTOMIZE:
                    if (mVideoSarNum > 0 && mVideoSarDen > 0) {
                        displayAspectRatio = (float) mVideoSarNum / (float) mVideoSarDen;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        }
                        break;
                    }
                case FIT_PARENT:
                case FILL_PARENT:
                default:
                    displayAspectRatio = (float) defaultVideoWidth / (float) defaultVideoHeight;
                    break;
            }
            boolean shouldBeWider = displayAspectRatio > specAspectRatio;

            switch (mCurrentAspectRatio) {
                case FIT_PARENT:
                case FIT_PARENT_16_9:
                case FIT_PARENT_18_9:
                case FIT_PARENT_CUSTOMIZE:
                    if (mVideoSarNum <= 0 && mVideoSarDen <= 0) {
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                    }
                case FIT_PARENT_4_3:
                    if (shouldBeWider) {
                        // too wide, fix width
                        width = widthSpecSize;
                        height = (int) (width / displayAspectRatio);
                    } else {
                        // too high, fix height
                        height = heightSpecSize;
                        width = (int) (height * displayAspectRatio);
                    }
                    break;
                case FILL_PARENT:
                    if (shouldBeWider) {
                        // not high enough, fix height
                        height = heightSpecSize;
                        width = (int) (height * displayAspectRatio);
                    } else {
                        // not wide enough, fix width
                        width = widthSpecSize;
                        height = (int) (width / displayAspectRatio);
                    }
                    break;

                default:
                    if (shouldBeWider) {
                        // too wide, fix width
                        width = Math.min(defaultVideoWidth, widthSpecSize);
                        height = (int) (width / displayAspectRatio);
                    } else {
                        // too high, fix height
                        height = Math.min(defaultVideoHeight, heightSpecSize);
                        width = (int) (height * displayAspectRatio);
                    }
                    break;
            }
        }

        mMeasuredWidth = width;
        mMeasuredHeight = height;
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
    }

    public int getmCurrentAspectRatio() {
        return mCurrentAspectRatio;
    }
}
