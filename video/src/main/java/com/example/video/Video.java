package com.example.video;

import static com.example.video.enume.PalyStateEnume.STATE_PLAYING;

import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.enume.DoubleSpeedEnume;
import com.example.video.enume.ErrorEnume;
import com.example.video.enume.PalyStateEnume;
import com.example.video.enume.PlayDirectionEnume;
import com.example.video.util.LogUtility;
import com.example.video.util.MeasureHelper;
import com.example.video.util.ObjectUtil;
import com.example.video.util.PlayUtil;
import com.example.video.util.StringUtils;
import com.example.video.view.FloatingWindow;
import com.example.video.view.SYTouchSpeedLayout;
import com.example.sniffer.VideoSniffer;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public abstract class Video extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    private static final String TAG = "Video";
    public TextureView textureView;
    public DataSource dataSource;
    private ViewGroup textureViewContainer;
    private MediaInterface mediaInterface;
    private Class kernelClass;


    DoubleSpeedEnume doubleSpeed = DoubleSpeedEnume.SPEED_X2;
    private static int MAX_VOLUME = 0;//最大音量
    private static int OLD_VOLUME = 0;//当前音量
    private static float CURRENT_BRIGHTNESS = 0;//当前亮度
    private static int OLD_PROGRESS = 0;//手势进度记录
    private static int NEW_PROGRESS = 0;//手势进度记录
    private static long NEW_PROGRESS_MILLISECOND = 0;//手势进度记录

    private static AudioManager mAudioManager;
    private static Window mWindow;

    private WindowManager.LayoutParams mLayoutParams;
    public PalyStateEnume state = PalyStateEnume.STATE_NORMAL;
    public static float PROGRESS_DRAG_RATE = 1f;//进度条滑动阻尼系数 越大播放进度条滑动越慢
    //进度计时器
    protected Timer updateProgressTimer = null;
    protected ProgressTimerTask mProgressTimerTask = null;
    protected Timer DISMISS_CONTROL_VIEW_TIMER;
    private DismissControlViewTimerTask mDismissControlViewTimerTask;

    public Video(@NonNull Context context) {
        this(context, null);
    }

    public Video(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Video(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (getWidth() > 0 && null!=getParent()) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    defaultRoot = (ViewGroup) getParent();
                }
            }
        });
        init(context);
        OrientationEventListener mOrientationListener = new OrientationEventListener(getContext(),
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (screen == PlayDirectionEnume.SCREEN_NORMAL || screen == PlayDirectionEnume.SCREEN_SMALL_WINDOW) {
                    PlayUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return;
                }
                int screenOrientation = getResources().getConfiguration().orientation;
                if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {    //设置竖屏
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                        PlayUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else if (orientation > 225 && orientation < 315) { //设置横屏
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        PlayUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                        PlayUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                } else if (orientation > 135 && orientation < 225) { //反向竖屏
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                        PlayUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    }
                }
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }


    }

    protected void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        textureViewContainer = findViewById(R.id.surface_container);
        if (textureViewContainer == null) {
            throw new NullPointerException("视图渲染成不能为空");
        }
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Service.AUDIO_SERVICE);
        }

        //下面这是设置当前APP亮度的方法配置
        mWindow = PlayUtil.getWindow(context);
        mLayoutParams = mWindow.getAttributes();

        textureViewContainer.setOnTouchListener(this);//监听手势
        //代表UI 初始化完毕
        state = PalyStateEnume.STATE_IDLE;
        //初始手势阻碍
        dragOffset = getResources().getDimensionPixelSize(R.dimen.dragOffset);

    }


    public void setUp(DataSource dataSource, Class kernelClass) {
        this.dataSource = dataSource;
        this.kernelClass = kernelClass;
        cancelProgressTimer();
        resetProgressAndTime();
        state = PalyStateEnume.STATE_NORMAL;
    }

    protected abstract int getLayoutId();

    private long gotoFullscreenTime;
    protected PlayDirectionEnume screen = PlayDirectionEnume.SCREEN_NORMAL;

    protected ViewGroup defaultRoot;//很重要的一个占位,关系到退出全屏后是否能回到最初的位置
    public int VIDEO_IMAGE_DISPLAY_TYPE = MeasureHelper.FIT_PARENT;

    /**
     * 如果全屏或者返回全屏的视图有问题，复写这两个函数gotoScreenNormal(),根据自己布局的情况重新布局。
     */
    public void gotoFullscreen(PlayDirectionEnume directionEnume) {
        try {
            startControlViewTimer();
            LogUtility.d("是否悬浮窗", isSmallWindow());
            closeTheSystemWindow();
            LogUtility.d(TAG, "gotoFullscreen 全屏");
            gotoFullscreenTime = System.currentTimeMillis();
            screen = directionEnume;
            dissmissControlView();
            PlayUtil.hideStatusBar(getContext());

            //TODO  智能全屏占位
            smartFullScreen(screen);

            PlayUtil.hideSystemUI(getContext());
            resetTextureView();
            PlayUtil.addWindowsContentView(getContext(), this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (VIDEO_IMAGE_DISPLAY_TYPE != -1) {
                setVideoImageDisplayType();
            }
            onWindowChanges(directionEnume);
        } catch (Exception e) {
            LogUtility.e(e);
        }
    }


    public boolean isSmallWindow() {
        return floatingWindow != null;
    }

    /**
     * 退出全屏
     */
    public void gotoNormalScreen(PlayDirectionEnume directionEnume) {//goback本质上是goto
        startControlViewTimer();
        closeTheSystemWindow();
        PlayUtil.showSystemUI(getContext());
        PlayUtil.showStatusBar(getContext());
        screen = directionEnume;
        smartSignOutFullScreen(screen);
        resetTextureView();
        defaultRoot.removeAllViews();
        detachParentContainer();
        LogUtility.d(TAG, this.getParent());
        defaultRoot.addView(this, new ViewGroup.LayoutParams(-1, -1));
        VIDEO_IMAGE_DISPLAY_TYPE = MeasureHelper.FIT_PARENT;
        setVideoImageDisplayType();
        dissmissControlView();
        onWindowChanges(directionEnume);
    }

    private void detachParentContainer() {
        PlayUtil.removeView(this);
    }

    //智能全屏在此做
    protected void smartFullScreen(PlayDirectionEnume screen) {
        PlayUtil.setRequestedOrientation(getContext(), screen);
    }

    protected void smartSignOutFullScreen(PlayDirectionEnume screen) {
        PlayUtil.setRequestedOrientation(getContext(), screen);
    }

    public void setVideoImageDisplayType() {
        if (mediaInterface != null && textureView != null) {
            textureView.setAspectRatio(VIDEO_IMAGE_DISPLAY_TYPE);
        }
    }

    //复位缩放
    public final void resetTextureView() {
        if (textureView != null) {
            textureView.deoxidization();
        }
    }

    /**
     * 检索数据源是否有效
     */
    public boolean isValidDataSources() {
        return ObjectUtil.notNull(dataSource) && ObjectUtil.notNull(dataSource.getCurrentUrl());
    }

    public void pausePlay() {
        if (mediaInterface != null && mediaInterface.isValidKernel()) {
            state = PalyStateEnume.STATE_PAUSE;
            mediaInterface.pause();

        }
    }


    public synchronized void play() {
        if (isValidDataSources()) {
            startControlViewTimer();
            if (mediaInterface == null) {
                LogUtility.d("播放控制", "new play");
                state = PalyStateEnume.STATE_NORMAL;
                startVideo();//从未播放过,因为内核是空的
            } else if (mediaInterface.isPlaying() && mediaInterface.isCanSeekTo()) {
                LogUtility.d("播放控制", "播放中被暂停");
                //播放中点击暂停
                pausePlay();
                state = PalyStateEnume.STATE_PAUSE;
                setPlayStat(false);
            } else {
                switch (state) {
                    case STATE_NORMAL:
                        LogUtility.d("播放控制", "地址变更,重置播放");
                        //地址变更
                        startVideo();
                        break;
                    case STATE_AUTO_COMPLETE:
                        setPlayStat(true);
                        startVideo();
                        //重播逻辑  isValidKernel内核是否被销毁   isLive 是否是直播
//                        if (mediaInterface.isValidKernel() && !mediaInterface.isLive() && mediaInterface.isCanSeekTo()) {
//                            LogUtility.d("播放控制", "重新播放,可以理解为重试");
////                            mediaInterface.seekTo(0);//重播把进度设置成0
//                            state = PalyStateEnume.STATE_PREPARING_PLAYING;
////                            mediaInterface.resetPlay();
//                        } else {
//                            LogUtility.d("播放控制", "重置播放,可能是直播");
//                            startVideo();
//                        }
                        break;
                    case STATE_PAUSE:
                        setPlayStat(true);
                        if (!mediaInterface.isLive() && mediaInterface.isValidKernel() && mediaInterface.isCanSeekTo()) {
                            mediaInterface.start();
                            LogUtility.d("播放控制", "暂停后播放");
                        } else {
                            LogUtility.d("播放控制", "直播 暂停后播放");
                            //直播不允许
                            startVideo();
                        }
                        break;
                    case STATE_PLAYING:
                        if (!mediaInterface.isLive() && mediaInterface.isCanSeekTo()) {
                            LogUtility.d("播放控制", "播放中 暂停");
                            mediaInterface.pause();
                            setPlayStat(false);
                        }
                        break;
                    case STATE_ERROR:
                        state = PalyStateEnume.STATE_NORMAL;
                        LogUtility.d("播放控制", "错误状态下 重置播放");
                        startVideo();
                        break;
                }
                LogUtility.d(TAG, "当前播放器状态:" + state.getName() + " 总时间:" + mediaInterface.getDuration());
            }
        } else {
            onError(ErrorEnume.URL_NULL);
        }
    }

    //嗅探监听器
    private VideoSniffer.SnifferListener snifferListener = new VideoSniffer.SnifferListener() {
        @Override
        public void onCaptureKey(@NonNull String url, @Nullable String title, @Nullable Map<String, String> hashMap) {
            LogUtility.d("嗅探", url);
            post(() -> {
                if (dataSource != null) {
                    dataSource.setUrl(url);
                    dataSource.setTitle(title);

                        dataSource.setHeaderMap(hashMap);

                    //开始播放
                    startVideo();
                }
            });
        }

        @Override
        public void onTimeout(String url) {
            LogUtility.d("嗅探", "超时了");
            post(() -> onError(ErrorEnume.SNIFFING_FAILED));
        }
    };

    public void startVideo() {
        if (ObjectUtil.notNull(dataSource) && ObjectUtil.isNull(dataSource.getCurrentUrl())) {
            //这是需要进行嗅探的
            if (dataSource.sniffing) {
                VideoSniffer.getInstance(snifferListener)
                        .addKeyWord(".m3u8")
                        .addKeyWord(".mp4")
                        .addKeyWord(".flv")
                        .parseUrl(dataSource.sniffingUrl, dataSource.timeOut);
                return;
            }
        }
        LogUtility.d(TAG, "startVideo");
        try {
            if (kernelClass == null) {
                kernelClass = MediaExo.class;
            }
            if (mediaInterface != null) {
                mediaInterface.release();
                mediaInterface = null;
            }
            //内核优化
            Constructor<MediaInterface> constructor = kernelClass.getConstructor(Video.class);
            mediaInterface = constructor.newInstance(this);
            mediaInterface.UP(this);
            addTextureView();

            //开启硬件加速
            PlayUtil.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            //开启常亮
            PlayUtil.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //开始准备
            onStatePreparing();
        } catch (Exception e) {
            LogUtility.e("内核创建异常", e);
        }

    }

    /**
     * 设置长按倍速
     */
    public void setDoubleSpeed(DoubleSpeedEnume doubleSpeed) {
        this.doubleSpeed = doubleSpeed;
    }

    public abstract void hideLoading();

    public abstract void showLoading();

    public void onStatePreparing() {
        state = PalyStateEnume.STATE_PREPARING;
        resetProgressAndTime();
    }

    public void stopPlay() {
        startProgressTimer();
        resetProgressAndTime();
        if (mediaInterface != null) {
            try {
                PlayUtil.saveProgress(getContext(), dataSource.getCurrentUrl(),
                        0, 1);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            mediaInterface.release();
            mediaInterface = null;
        }

    }

    //重置进度
    public void resetProgressAndTime() {
        setProgress(0);
        setSecondaryProgress(0);
        setCurrent("00:00");
        setTotal("00:00");
        setPlayStat(false);
    }

    //设置缓冲进度
    public abstract void setSecondaryProgress(int bufferProgress);

    //设置当前播放进度
    public abstract void setProgress(int progress);

    //创建渲染器
    private void addTextureView() {
        LogUtility.d("添加纹理视图!当前播放器堆栈:" + this.hashCode());
        textureViewContainer.removeAllViews();
        if (textureView == null) {
            textureView = new TextureView(getContext(), this);
        }
        textureView.setSurfaceTextureListener(mediaInterface);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        textureViewContainer.addView(textureView, layoutParams);
    }

    public DataSource getSyDataSource() {
        return dataSource;
    }

    public void onTrackInfo(ITrackInfo[] iTrackInfos, Class<ITrackInfo> iTrackInfoClass) {


    }

    public void onPrepared() {
        state = PalyStateEnume.STATE_PREPARED;
    }

    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        LogUtility.i(TAG, String.format("onVideoSizeChanged videoWidth:%s videoHeight: %s", videoWidth, videoHeight));
        if (textureView != null) {
            switch (screen) {
                case SCREEN_NORMAL:
                case SCREEN_SMALL_WINDOW:
                    textureView.setAspectRatio(0);
                    break;
            }
            textureView.setVideoSize(videoWidth, videoHeight);
        }
    }


    public void onError(Class kernel, @Nullable String message, final int what, final int extra) {
        if (dataSource.sniffing) {
            dataSource.setUrl(null);
        }
        PlayUtil.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        state = PalyStateEnume.STATE_ERROR;

    }


    public void onError(ErrorEnume errorEnume) {
        hideLoading();
        if (dataSource.sniffing) {
            dataSource.setUrl(null);
        }
        Toast.makeText(getContext(), errorEnume.getName(), Toast.LENGTH_LONG).show();
    }

    public void onInfo(int what, int extra) {
        LogUtility.d(TAG, "onInfo --->>what:" + what + "extra:" + extra);
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            if (state == PalyStateEnume.STATE_PREPARED || state == PalyStateEnume.STATE_PREPARING_PLAYING) {
                continueLastTime();
                onStatePlaying(true);//开始渲染图像，真正进入playing状态
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            LogUtility.d(TAG, PalyStateEnume.STATE_BUFFER.getName());
            onBuffering();
            if (isSpeedLayoutVisual() && mediaInterface != null) {
                //长按倍数逻辑
                getSpeedLayout().updateStatus(true);
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            LogUtility.d(TAG, PalyStateEnume.STATE_BUFFER_END.getName());
            onBufferingEnd();
            if (isSpeedLayoutVisual() && mediaInterface != null) {
                getSpeedLayout().updateStatus(false);
                mediaInterface.setSpeed(getSpeedLayout().getDoubleSpeed().getCode());
            }
        } else {

            onStatePlaying(true);
        }
    }


    /**
     * 继续上次的播放位置
     */
    public void continueLastTime() {
        if (mediaInterface != null) {
            long saveProgress = PlayUtil.getSavedProgress(getContext(), dataSource.getCurrentUrl());
            mediaInterface.seekTo(saveProgress);
            LogUtility.d(TAG, "继续上次播放位置:" + saveProgress);
        }
    }

    /**
     * @param percent 缓存百分比
     * @param kernel 内核
     */
    public void setBufferProgress(int percent, Class kernel) {


    }

    public void onSeekComplete() {

    }

    public void onCompletion() {
        LogUtility.d(TAG, "播放完毕 自动完成");
        if (mediaInterface != null) {
            PlayUtil.saveProgress(getContext(), dataSource.getCurrentUrl(), 0, 1);
        }
        //关闭屏幕常亮
        PlayUtil.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startControlViewTimer();
        cancelProgressTimer();
        resetProgressAndTime();
        state = PalyStateEnume.STATE_AUTO_COMPLETE;
    }

    public void onStatePlaying(boolean playWhenReady) {
        if (playWhenReady) {
            state = STATE_PLAYING;
        } else {
            state = PalyStateEnume.STATE_PAUSE;
        }
        startProgressTimer();
        dissmissControlView();
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_SMALL_WINDOW:
                textureView.setAspectRatio(0);
                textureViewContainer.requestLayout();
                break;
        }

    }


    public void startProgressTimer() {
        if (updateProgressTimer == null || mProgressTimerTask == null) {
            cancelProgressTimer();
            updateProgressTimer = new Timer();
            mProgressTimerTask = new ProgressTimerTask();
            updateProgressTimer.schedule(mProgressTimerTask, 0, 800);
        }

    }


    /**
     * 取消计时器
     */
    public void cancelProgressTimer() {
        LogUtility.d(TAG, "取消计时器");
        if (updateProgressTimer != null) {
            updateProgressTimer.cancel();
            updateProgressTimer = null;
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
            mProgressTimerTask = null;
        }
    }

    /**
     * @param progress        百分比
     * @param currentPosition 当前播放时间毫秒
     * @param duration        视频总时间 毫秒
     */
    public abstract void onProgress(int progress, long currentPosition, long duration, String currentTime, String totalTime);


    public abstract void setCurrent(String currentTime);

    public abstract void setTotal(String totalTime);

    public abstract void setPlayStat(boolean isPlay);


    private long currentPosition = 0;//当前播放位置毫秒
    private long currentDuration = 0;//当前播放总时间毫秒
    private boolean dragProgress = false;//再拖动进度与否


    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {

            switch (state) {
                case STATE_PLAYING:
                case STATE_PREPARING_PLAYING:
                case STATE_PAUSE:
                case STATE_PREPARING:
                case STATE_BUFFER:
                case STATE_BUFFER_END:
                    if (mediaInterface != null) {
                        long position = mediaInterface.getCurrentPosition();
                        long duration = mediaInterface.getDuration();
                        currentPosition = position;
                        currentDuration = duration;
                        int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                        post(() -> {
                            if (!dragProgress) {
                                onProgress(progress, currentPosition, duration, StringUtils.stringForTime(Math.max(currentPosition, 0)), StringUtils.stringForTime(duration));
                            } else {
                                //在拖动进度的时候需要更新总时间的
                                setTotal(StringUtils.stringForTime(duration));
                            }
                        });
                    }
                    break;


            }
        }


    }

    //视频正在缓冲
    public void onBuffering() {

    }

    public void onBufferingEnd() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            long duration = getDuration();
            setCurrent(StringUtils.stringForTime(progress * duration / 100));

        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (getDuration() > -1) {
            cancelProgressTimer();
            ViewParent vpdown = getParent();
            dragProgress = true;
            while (vpdown != null) {
                vpdown.requestDisallowInterceptTouchEvent(true);
                vpdown = vpdown.getParent();
            }
        }

    }

    //手动滑动进度条停止后
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ViewParent vpup = getParent();
        dragProgress = false;
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (state != STATE_PLAYING && state != PalyStateEnume.STATE_PAUSE) {
            return;
        }
        if (getDuration() > 0 && (mediaInterface != null && !mediaInterface.liveStreaming)) {
            long time = seekBar.getProgress() * getDuration() / 100;
            LogUtility.d(TAG, "onStopTrackingTouch 进度条被拖得,改变进度");
            mediaInterface.seekTo(Math.max(time, 0));
            PlayUtil.saveProgress(getContext(), dataSource.getCurrentUrl(), Math.max(time, 0), mediaInterface.getDuration());
        }
        startProgressTimer();
    }

    public long getDuration() {
        if (mediaInterface != null) {
            return mediaInterface.getDuration();
        }
        return -1;

    }

    public void seekTo(long position) {
        if (mediaInterface != null) {
            mediaInterface.seekTo(position);
        }
    }

    /**
     * @param volumePercent 0-100
     */
    public abstract void showVolumeDialog(int volumePercent);


    public abstract void dismissVolumeDialog();

    public abstract void showBrightnessDialog(int brightnessPercent);

    /**
     * @param progressPercentage 进度百分比
     * @param time               进度时间毫秒
     */
    public abstract void showProgressDialog(int progressPercentage, long time, String currentTime, String totalTime);

    public abstract void dismissProgressDialog();

    public abstract void dismissBrightnessDialog();


    /**
     * 根据自定义状态判断是否在播放中
     */
    public boolean isStatPlay() {
        LogUtility.d(TAG, "当前播放器状态:" + state.getName());
        switch (state) {
            case STATE_NORMAL:
            case STATE_IDLE:
            case STATE_PREPARING:
            case STATE_AUTO_COMPLETE:
            case STATE_ERROR:
                return false;
        }
        return mediaInterface != null && mediaInterface.isValidKernel();
    }


    //设置亮度
    protected void setBrightness(float brightness) {
        mLayoutParams.screenBrightness = brightness;
        mWindow.setAttributes(mLayoutParams);
    }

    //设置音量
    protected void setVolume(int newVolume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);
    }

    ////////////////////////////////////////////////手势监听代码块//////////////////////////////////////////////////
    protected abstract SYTouchSpeedLayout getSpeedLayout();

    /**
     * 判断步进 ui是否可视
     */
    private boolean isSpeedLayoutVisual() {
        if ((ObjectUtil.notNull(getSpeedLayout()))) {
            return getSpeedLayout().getVisibility() == VISIBLE;
        }
        return false;
    }

    protected float mDownX;
    protected float mDownY;
    boolean mChangeVolume;
    boolean mChangePosition;
    boolean mChangeBrightness;
    boolean doubleClick;
    //用来检测是否可以双击的时间戳
    long unlooseTime1;
    long unlooseTime2;


    //包含单击双击长按 悬浮窗 手势分发
    private void onTouchDistribute(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container || (v instanceof TextureView)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isSpeedLayoutVisual()
                            && getZoomSaveScale() == TextureView.MIN_SCALE
                            && !mChangeBrightness && !mChangeVolume) {
                        startControlViewTimer();
                    }
                    break;
            }
            playClickGestureDetector.onTouchEvent(event);
        }

        //悬浮窗触摸事件
        if (isSmallWindow()) {
            floatingWindow.onTouch(v, event);

        }

    }

    /**
     * 启动显示UI计时器
     */
    protected void startControlViewTimer() {
        dissmissControlView();
        LogUtility.d(TAG, "startControlViewTimer 显示UI");
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 4000);
    }

    /**
     * 隐藏UI视图
     */
    protected void dissmissControlView() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
            DISMISS_CONTROL_VIEW_TIMER = null;
        }

    }

    public class DismissControlViewTimerTask extends TimerTask {
        @Override
        public void run() {
            //4秒后应该关闭进度显示
            if (isStatPlay() && state != PalyStateEnume.STATE_PAUSE) {
                post(() -> dissmissControlView());
                LogUtility.d(TAG, "计时器在跑");
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.surface_container || (v instanceof TextureView)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isSpeedLayoutVisual() && !isSmallWindow()) {
                        mDownX = x;
                        mDownY = y;
                        mChangeVolume = false;
                        mChangePosition = false;
                        mChangeBrightness = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() > 1) {
                        doubleClick = true;
                        unlooseTime2 = System.currentTimeMillis();
                    } else if (doubleClick) {
                        //计算下间隔如果超出30000 则说明当前是一根手指
                        unlooseTime1 = System.currentTimeMillis();
                        long time = unlooseTime1 - unlooseTime2;
                        doubleClick = time < 3000;
                    }
                    if (!isSpeedLayoutVisual() && !isSmallWindow() && !doubleClick && getZoomSaveScale() == TextureView.MIN_SCALE) {
                        //执行单指操作
                        oneFingerGesture(x, y);
                        break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //手势更新了进度
                    if (mChangePosition) {
                        mChangePosition = false;
                        if (getZoomSaveScale() == TextureView.MIN_SCALE && mediaInterface != null && mediaInterface.isCanSeekTo()) {
                            seekTo(mSeekTimePosition);
                            setProgress(progressPercentage);
                        }
                        mSeekTimePosition = -1;
                    }
                    //还原
                    doubleClick = false;
                    dismissProgressDialog();
                    dismissVolumeDialog();
                    dismissBrightnessDialog();
                    break;
            }
        }
        onTouchDistribute(v, event);
        return true;
    }


    public int dragOffset = 80;
    private long mGestureDownPosition;//手指按下 进度记录
    private float mGestureDownBrightness;//手指按下亮度记录
    private int mGestureDownVolume;//手指按下音量记录
    protected long mSeekTimePosition = -1;//拖动进度记录
    private int progressPercentage;//手指滑动的进度

    /**
     * 单指手势 , 快进快退   音量调节 ,亮度调节   这都属于单指
     */
    protected void oneFingerGesture(float x, float y) {
        float deltaX = x - mDownX;
        float deltaY = y - mDownY;
        float absDeltaX = Math.abs(deltaX);
        float absDeltaY = Math.abs(deltaY);
        if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
            if (absDeltaX > dragOffset || absDeltaY > dragOffset) {
                cancelProgressTimer();
                if (absDeltaX >= dragOffset) {
                    // 全屏模式下的CURRENT_STATE_ERROR状态下,不响应进度拖动事件.
                    // 否则会因为mediaplayer的状态非法导致App Crash
                    if (state != PalyStateEnume.STATE_ERROR) {
                        mChangePosition = true;
                        mGestureDownPosition = getCurrentPositionWhenPlaying();
                    }
                } else {
                    //如果y轴滑动距离超过设置的处理范围，那么进行滑动事件处理
                    if (mDownX < getWidth() / 2f) {//左侧改变亮度
                        mChangeBrightness = true;
                        try {
                            if (mLayoutParams.screenBrightness < 0) {
                                mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                            }
                        } catch (Settings.SettingNotFoundException e) {
                            mGestureDownBrightness = mLayoutParams.screenBrightness * 255;
                        }

                    } else {//右侧改变声音
                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        mChangeVolume = true;
                    }
                }
            }
        }


        //进度拖动
        if (mChangePosition && mediaInterface != null && !mediaInterface.isLive() && mediaInterface.isValidKernel() && mediaInterface.getDuration() > 0) {
            long totalTimeDuration = getDuration();
            PROGRESS_DRAG_RATE = Math.max(PROGRESS_DRAG_RATE, 1F);
            mSeekTimePosition = (int) (mGestureDownPosition + deltaX * totalTimeDuration / (getWidth() * PROGRESS_DRAG_RATE));
            if (mSeekTimePosition > totalTimeDuration) {
                mSeekTimePosition = totalTimeDuration;
            }
            String seekTime = StringUtils.stringForTime(mSeekTimePosition);
            String totalTime = StringUtils.stringForTime(totalTimeDuration);
            progressPercentage = totalTimeDuration <= 0 ? 0 : (int) (mSeekTimePosition * 100 / totalTimeDuration);
            showProgressDialog(progressPercentage, mSeekTimePosition, seekTime, totalTime);
        }
        //音量调节
        if (mChangeVolume) {
            deltaY = -deltaY;
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int deltaV = (int) (max * deltaY * 3 / getMeasuredHeight());
            //dialog中显示百分比
            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / getMeasuredHeight());
            setVolume(mGestureDownVolume + deltaV);
            showVolumeDialog(volumePercent);
        }
        //亮度调节
        if (mChangeBrightness) {
            deltaY = -deltaY;
            int deltaV = (int) (255 * deltaY * 3 / getHeight());
            float newBrightness;
            if (((mGestureDownBrightness + deltaV) / 255) >= 1) {//这和声音有区别，必须自己过滤一下负值
                newBrightness = 1;
            } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                newBrightness = 0.01f;
            } else {
                newBrightness = (mGestureDownBrightness + deltaV) / 255;
            }
            setBrightness(newBrightness);
            //dialog中显示百分比
            int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / getMeasuredHeight());
            showBrightnessDialog(brightnessPercent);
        }
    }

    /**
     * 获取当前播放器位置
     */
    public long getCurrentPositionWhenPlaying() {
        return isStatPlay() ? mediaInterface.getCurrentPosition() : 0;
    }

    //获取当前视频的缩放比例 ,这里的缩放指双指放大缩小后的比例
    public float getZoomSaveScale() {
        if (textureView == null) {
            return TextureView.MIN_SCALE;
        }
        return textureView.getSaveScale();
    }

    /**
     * 获取进度天当前进度位置
     */
    public abstract int getParProgress();


    /////////////////////////////////////////////////////////////单击双击长按////////////////////////////////////////////////////////////
    private PlayClickListener playClickListener;

    /**
     * 添加播放器手势监听器
     */
    public void setPlayClickListener(PlayClickListener playClickListener) {
        this.playClickListener = playClickListener;
    }

    /**
     * 双击
     */
    protected PlayClickGestureDetector playClickGestureDetector = new PlayClickGestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        //双击
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (e.getPointerCount() == 1) {
                if (getZoomSaveScale() == TextureView.MIN_SCALE) {
                    if (playClickListener != null && !playClickListener.onDoubleTap()) {
                        return super.onDoubleTap(e);
                    }
                    LogUtility.d(TAG, "onDoubleTap() 被双击");
                    if (isStatPlay()) {
                        //播放或暂停
                        play();
                    }
                } else if (textureView != null) {
                    //缩放还原
                    textureView.deoxidization();
                }
            }
            return super.onDoubleTap(e);
        }

        //单击
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (e.getPointerCount() == 1) {
                if (playClickListener != null && !playClickListener.onSingleTapConfirmed(e.getX(), e.getY())) {

                    return super.onSingleTapConfirmed(e);
                }
                LogUtility.d(TAG, "onSingleTapConfirmed() 被单击");
                startControlViewTimer();
            }

            return super.onSingleTapConfirmed(e);
        }

        //长按
        @Override
        public void onLongPress(MotionEvent e) {
            if (e.getPointerCount() == 1) {
                if (playClickListener != null && !playClickListener.onLongPress(e.getX(), e.getY())) {
                    super.onLongPress(e);
                    return;
                }
                LogUtility.d(TAG, "onLongPress() 被长按 " + isStatPlay());
                if (isStatPlay()) {
                    if (getSpeedLayout() != null && mediaInterface.isValidKernel() && !mediaInterface.isLive()) {
                        LogUtility.d(TAG, "onLongPress() 显示长按步进");
                        playClickGestureDetector.setPress(true);
                        getSpeedLayout().show(doubleSpeed);
                        mediaInterface.setSpeed(doubleSpeed.getCode());
                    }
                }
                super.onLongPress(e);
            }
        }

    });

    class PlayClickGestureDetector extends GestureDetector {
        private boolean press = false;

        public PlayClickGestureDetector(Context context, SimpleOnGestureListener listener) {
            super(context, listener);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (press || isSpeedLayoutVisual()) {
                    LogUtility.d(TAG, "onTouchEvent() 隐藏长按步进 并还原正常播放速度");
                    //还原倍速标题
                    tvSpeedSetText(DoubleSpeedEnume.SPEED_X1);
                    //还原播放器播放速度
                    if (mediaInterface != null) {
                        mediaInterface.setSpeed(DoubleSpeedEnume.SPEED_X1.getCode());
                    }
                }
                press = false;
                if (isSpeedLayoutVisual()) {
                    getSpeedLayout().hide();
                }
                if (playClickListener != null) {
                    playClickListener.onAction_Up();
                }
            }
            return super.onTouchEvent(ev);
        }

        public void setPress(boolean press) {
            this.press = press;
        }
    }

    protected abstract void tvSpeedSetText(DoubleSpeedEnume doubleSpeedEnume);

    public interface PlayClickListener {

        /**
         * 播放器双击事件
         *
         * @return
         */
        boolean onDoubleTap();

        boolean onSingleTapConfirmed();

        boolean onSingleTapConfirmed(float x, float y);

        boolean onLongPress(float x, float y);

        /**
         * 手指从屏幕放开
         */
        void onAction_Up();
    }

    ////////////////////////////////////////////////////悬窗开启功能代码/////////////////////////////////////////////

    public FloatingWindow floatingWindow;

    public synchronized void openTheSuspensionWindow() {
        //权限检测
        PlayUtil.haveSmallWindowPermission(getContext());
        if (floatingWindow != null) {
            floatingWindow.dismiss();
        }
        gotoNormalScreen(PlayDirectionEnume.SCREEN_NORMAL);
        floatingWindow = new FloatingWindow(getContext(), this, 16.0f / 9.0f);
        onSmallWindowIsOpened();
        onWindowChanges(PlayDirectionEnume.SCREEN_SMALL_WINDOW);
    }

    public synchronized void closeTheSystemWindow() {
        if (floatingWindow != null) {
            floatingWindow.dismiss();
            floatingWindow = null;
            onSmallWindowIsClose();
            defaultRoot.addView(this, new ViewGroup.LayoutParams(-1, -1));
            onWindowChanges(PlayDirectionEnume.SCREEN_NORMAL);
        }
    }

    public abstract void onSmallWindowIsOpened();

    public abstract void onSmallWindowIsClose();

    public abstract void onWindowChanges(PlayDirectionEnume directionEnume);


}
