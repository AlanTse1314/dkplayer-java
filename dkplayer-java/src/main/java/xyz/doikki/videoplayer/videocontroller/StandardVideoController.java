package xyz.doikki.videoplayer.videocontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.videoplayer.R;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.videocontroller.bean.JSBean;
import xyz.doikki.videoplayer.videocontroller.component.CompleteView;
import xyz.doikki.videoplayer.videocontroller.component.ErrorView;
import xyz.doikki.videoplayer.videocontroller.component.GestureView;
import xyz.doikki.videoplayer.videocontroller.component.LiveControlView;
import xyz.doikki.videoplayer.videocontroller.component.PrepareView;
import xyz.doikki.videoplayer.videocontroller.component.TitleView;
import xyz.doikki.videoplayer.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.controller.GestureVideoController;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;
import xyz.doikki.videoplayer.util.ScreenShotUtil;

/**
 * 直播/点播控制器
 * 注意：此控制器仅做一个参考，如果想定制ui，你可以直接继承GestureVideoController或者BaseVideoController实现
 * 你自己的控制器
 * Created by Doikki on 2017/4/7.
 */

public class StandardVideoController extends GestureVideoController implements View.OnClickListener,VodControlView.OnRateSwitchListener {

    protected ImageView mLockButton;

    protected ImageView mLoadingProgress;

    public ImageView mScreenShot;
    VideoView mVideoView;
    public AnimationDrawable mAnimationDrawable;
    public VodControlView mVodControlView;
    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

//    public StandardVideoController(@NonNull Context context,VideoView videoView) {
//        this(context, (AttributeSet) null);
//        this.mVideoView =videoView;
//    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dkplayer_layout_standard_controller;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void initView() {
        super.initView();
        mLockButton = findViewById(R.id.lock);
        mLockButton.setOnClickListener(this);
        mLoadingProgress = findViewById(R.id.loading);
        mLoadingProgress.setImageResource(R.drawable.loading);
        mAnimationDrawable = (AnimationDrawable)mLoadingProgress.getDrawable();
        mAnimationDrawable.start();
        mScreenShot =  findViewById(R.id.screenshot);
        mScreenShot.setOnClickListener(this);

    }


//    public static String[] VideoPlayerList = {
//            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4",
//            "http://play.g3proxy.lecloud.com/vod/v2/MjUxLzE2LzgvbGV0di11dHMvMTQvdmVyXzAwXzIyLTExMDc2NDEzODctYXZjLTE5OTgxOS1hYWMtNDgwMDAtNTI2MTEwLTE3MDg3NjEzLWY1OGY2YzM1NjkwZTA2ZGFmYjg2MTVlYzc5MjEyZjU4LTE0OTg1NTc2ODY4MjMubXA0?b=259&mmsid=65565355&tm=1499247143&key=f0eadb4f30c404d49ff8ebad673d3742&platid=3&splatid=345&playid=0&tss=no&vtype=21&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super"
//    };


//    public static String[] VideoPlayerTitle = {
//            "大家好，我是潇湘剑雨",
//            "大家好，我是潇湘剑雨",
//            "如果项目可以，可以给个star",
//            "有bug，可以直接提出来，欢迎一起探讨",
//            "把本地项目代码复制到拷贝的仓库",
//            "依次输入命令上传代码",
//            "把本地项目代码复制到拷贝的仓库",
//            "依次输入命令上传代码",
//            "逗比逗比把本地项目代码复制到拷贝的仓库",
//            "大家好，我是潇湘剑雨",
//            "大家好，我是潇湘剑雨",
//            "如果项目可以，可以给个star",
//            "有bug，可以直接提出来，欢迎一起探讨",
//            "把本地项目代码复制到拷贝的仓库",
//            "依次输入命令上传代码",
//            "把本地项目代码复制到拷贝的仓库",
//            "依次输入命令上传代码",
//            "逗比逗比把本地项目代码复制到拷贝的仓库",
//    };

    /**
     * 快速添加各个组件
     * @param title  标题
     * @param isLive 是否为直播
     */
    public void addDefaultControlComponent(String title, boolean isLive) {
//        List<JSBean> s=new ArrayList<>();
//        int i=0;
//        for (String str:VideoPlayerList) {
//            JSBean j=new JSBean();
//            j.setUrl(str);
//            j.setSource(i);
//            j.setTitle("第"+i+"集");
//            s.add(j);
//            i++;
//        }


        CompleteView completeView = new CompleteView(getContext());
        ErrorView errorView = new ErrorView(getContext());
        PrepareView prepareView = new PrepareView(getContext());
        prepareView.setClickStart();
        TitleView titleView = new TitleView(getContext());
        titleView.setTitle(title);
        addControlComponent(completeView, errorView, prepareView, titleView);
        if (isLive) {
            addControlComponent(new LiveControlView(getContext()));
        } else {
            mVodControlView =new VodControlView(getContext());
            addControlComponent(mVodControlView);
//            mVodControlView.setData(s);
            mVodControlView.setOnRateSwitchListener(this);
        }
        addControlComponent(new GestureView(getContext()));
        setCanChangePosition(!isLive);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lock) {
            mControlWrapper.toggleLockState();
        }else {
            ScreenShotUtil.ScreenShot(this.mActivity, this.mControlWrapper.doScreenShot());
        }
    }

    @Override
    protected void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            mLockButton.setSelected(true);
            Toast.makeText(getContext(), R.string.dkplayer_locked, Toast.LENGTH_SHORT).show();
        } else {
            mLockButton.setSelected(false);
            Toast.makeText(getContext(), R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (mControlWrapper.isFullScreen()) {
            if (isVisible) {
                if (mLockButton.getVisibility() == GONE) {
                    mLockButton.setVisibility(VISIBLE);
                    mLockButton.setVisibility(VISIBLE);
                    if (anim != null) {
                        mLockButton.startAnimation(anim);
                        mScreenShot.startAnimation(anim);
                    }
                }
//                mLockButton.setVisibility(GONE);
//                mLockButton.setVisibility(GONE);

            } else {
                mLockButton.setVisibility(GONE);
                if (anim != null) {
                    mLockButton.startAnimation(anim);
                    mScreenShot.startAnimation(anim);
                }
            }
        }
    }

    @Override
    protected void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        Log.e("播放状态：playerState:", String.valueOf(playerState));
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mLockButton.setVisibility(GONE);
                mScreenShot.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                if (isShowing()) {
                    mLockButton.setVisibility(VISIBLE);
                    mScreenShot.setVisibility(VISIBLE);
                } else {
                    mLockButton.setVisibility(GONE);
                    mScreenShot.setVisibility(GONE);
                }
                break;
        }

        if (mActivity != null && hasCutout()) {
            int orientation = mActivity.getRequestedOrientation();
            int dp24 = PlayerUtils.dp2px(getContext(), 24);
            int cutoutHeight = getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
                lblp.setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
                layoutParams.setMargins(dp24, 0, dp24, 0);
            }
        }

    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE:
                mLockButton.setSelected(false);
                mAnimationDrawable.stop();
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
            case VideoView.STATE_PAUSED:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_BUFFERED:
                mAnimationDrawable.stop();
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_BUFFERING:
                mAnimationDrawable.start();
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                mAnimationDrawable.stop();
                mLoadingProgress.setVisibility(GONE);
                mScreenShot.setVisibility(GONE);
                mLockButton.setVisibility(GONE);
                mLockButton.setSelected(false);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLocked()) {
            show();
            Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mControlWrapper.isFullScreen()) {
//            if (mActivity == null || mActivity.isFinishing()) {
//                return false;
//            }
//            this.mActivity.setRequestedOrientation(13);
//            this.mControlWrapper.stopFullScreen();
//            return true;
            return stopFullScreen();
        }
        return super.onBackPressed();
    }

    @Override
    public void onRateChange(String url,String title) {
        mVideoView.setUrl(url,title);
        mVideoView.replay(true);
    }


    List<JSBean> list = new ArrayList<>();
    public void setData(String url,String title) {
        list.clear();
        JSBean jsBean = new JSBean();
        jsBean.setUrl(url);
        jsBean.setTitle(title);
        list.add(jsBean);
        mVodControlView.setData(list);
    }

    public void setData(List<JSBean> multiRateData) {
        list.clear();
        list.addAll(multiRateData);
        mVodControlView.setData(list);
    }


}
