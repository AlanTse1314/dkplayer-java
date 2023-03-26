package com.example.video;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.video.enume.DoubleSpeedEnume;
import com.example.video.enume.ErrorEnume;
import com.example.video.enume.PalyStateEnume;
import com.example.video.enume.PlayDirectionEnume;
import com.example.video.enume.WindowEnume;
import com.example.video.util.LoadingSpeed;
import com.example.video.util.LogUtility;
import com.example.video.util.PlayUtil;
import com.example.video.util.ScreenProjectionUtil;
import com.example.video.util.SelectorFactory;
import com.example.video.util.StringUtils;
import com.example.video.view.BottomMain;
import com.example.video.view.ButtomDialogView;
import com.example.video.view.LoadingBarView;
import com.example.video.view.SYTouchSpeedLayout;
import com.example.video.view.TopMain;
import com.example.video.view.UIOnClickListener;
import com.yanbo.lib_screen.entity.ClingDevice;

import java.util.List;

public class VideoPlayer extends Video {
    private static final String TAG = "VideoPlayer";
    private TopMain topMain;
    private BottomMain bottomMain;
    private TopMain toupTopMain;
    private BottomMain toupBottomMain;
    private View mVolumeDialog;//音量调节器
    private View mBrightnessDialog;//亮度调节器
    private View mProgresstnessDialog;//亮度调节器
    private View toupingMian;//投屏主布局
    private TextView current;
    private TextView tvLoading;//网速显示
    private TextView total;
    private ProgressBar videoDialogPar;
    private ProgressBar volumeProgressBar;
    private ProgressBar brightnessProgressBar;
    private SYTouchSpeedLayout syTouchSpeedLayout;
    private PalyStateEnume screenProjectionState = PalyStateEnume.STATE_IDLE;
    private ImageView volumeImage;
    private UIOnClickListener uiOnClickListener;
    private RelativeLayout dialogRelativeLayout;
    private LoadingBarView loadingBarView;
    private View retryBtn;//重试按钮
    private View retryLayout;//重试按钮
    private boolean toupFastForward = false;//投屏快进中
    private Context mContext;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LoadingSpeed.UPDATE_LOADING_SPEED) {
                String loadSpeed = (String) msg.obj;
                tvLoading.setText(loadSpeed);
            }
        }
    };
    private LoadingSpeed loadingSpeed = new LoadingSpeed(handler);

    public VideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initClickEvent();
        topMain = findViewById(R.id.top_main);
        bottomMain = findViewById(R.id.bottom_main);
        retryLayout = findViewById(R.id.retry_layout);
        retryBtn = findViewById(R.id.retry_btn);

        dialogRelativeLayout = findViewById(R.id.dialog_relative_layout);
        //流动的加载进度
        loadingBarView = new LoadingBarView(getContext(), findViewById(R.id.loading));
        tvLoading = findViewById(R.id.tv_loading);
        topMain.setUiOnClickListener(uiOnClickListener);
        bottomMain.setUiOnClickListener(uiOnClickListener);
        bottomMain.setOnSeekBarChangeListener(this);
        syTouchSpeedLayout = findViewById(R.id.player_speed_layout);
        syTouchSpeedLayout.hide();
        onWindowChanges(PlayDirectionEnume.SCREEN_NORMAL);

        toupingMian = findViewById(R.id.touping_main);
        toupBottomMain = findViewById(R.id.toup_bottom_main);
        toupTopMain = findViewById(R.id.toup_top_main);
        toupBottomMain.setWindowEnume(WindowEnume.TYPE_SCREEN_PROJECTION);
        toupTopMain.setWindowEnume(WindowEnume.TYPE_SCREEN_PROJECTION);
        toupingMian.setVisibility(GONE);
        retryBtn.setOnClickListener(v -> uiOnClickListener.onClickretryBtn());
        UIOnClickListener toupUIOnClickListener = new UIOnClickListener() {

            @Override
            public void onClickSmallWindow(ImageView view) {

            }

            @Override
            public void onClickScreenProjection(ImageView view) {

            }

            @Override
            public void onClickSetting(ImageView view) {

            }

            @Override
            public void onClickBack(ImageView view) {
                //按下返回键  停止掉投屏服务
                uiOnClickListener.onClickBtnPlay(view);
                onClickWindowsClose(null);

            }

            @Override
            public void onClickWindowsClose(ImageView v) {
                //这里我们理解成关闭投屏
                //隐藏投屏布局
                toupingMian.setVisibility(GONE);
                ScreenProjectionUtil.stopCast();
                clingDevice = null;
                //恢复投屏前播放
                play();

            }

            @Override
            public void onClickFullScreen(View v1) {

            }

            @Override
            public void onClickBtnPlay(View v1) {
                switch (screenProjectionState) {
                    case STATE_PLAYING:
                        ScreenProjectionUtil.pauseCast();
                        toupBottomMain.setPlayStat(false);
                        break;
                    case STATE_PAUSE:
                        ScreenProjectionUtil.playCast();
                        toupBottomMain.setPlayStat(true);
                        break;
                }
            }

            @Override
            public void onClickMultiple(View v1) {

            }

            @Override
            public void onClickAnthology(View v1) {

            }
        };
        toupTopMain.setUiOnClickListener(toupUIOnClickListener);
        toupBottomMain.setUiOnClickListener(toupUIOnClickListener);
        //投屏快进操作
        toupBottomMain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    toupBottomMain.setCurrent(StringUtils.stringForTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getMax() > 0) {
                    ViewParent vpdown = getParent();
                    while (vpdown != null) {
                        toupFastForward = true;
                        vpdown.requestDisallowInterceptTouchEvent(true);
                        vpdown = vpdown.getParent();
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ViewParent vpup = getParent();
                while (vpup != null) {
                    vpup.requestDisallowInterceptTouchEvent(false);
                    vpup = vpup.getParent();
                }
                ScreenProjectionUtil.seekTo(seekBar.getProgress());
                toupFastForward = false;
            }
        });
    }

    private void initClickEvent() {
        uiOnClickListener = new UIOnClickListener() {
            long blockingDelayTime;

            @Override
            public void onClickretryBtn() {
                play();
            }

            @Override
            public void onClickSmallWindow(ImageView view) {
                if (PlayUtil.haveSmallWindowPermission(getContext())) {
                    openTheSuspensionWindow();
                }
            }

            @Override
            public void onClickScreenProjection(ImageView view) {
                LogUtility.d(TAG, "投屏被单击");
                startScreenProjection();
            }

            @Override
            public void onClickSetting(ImageView view) {

            }

            @Override
            public void onClickBack(ImageView view) {
                gotoNormalScreen(PlayDirectionEnume.SCREEN_NORMAL);
            }

            @Override
            public void onClickWindowsClose(ImageView v) {
                closeTheSystemWindow();
            }

            @Override
            public void onClickFullScreen(View v1) {
                LogUtility.d("全屏", "被单击");
                if (defaultRoot != null) {
                    switch (screen) {
                        case SCREEN_NORMAL:
                        case SCREEN_SMALL_WINDOW:
                            gotoFullscreen(PlayDirectionEnume.SCREEN_FULLSCREEN_VERTICALSCREEN);
                            break;
                        case SCREEN_FULLSCREEN_VERTICALSCREEN:
                        case SCREEN_FULLSCREEN_HORIZONTALSCREEN:
                            if (isSmallWindow()) {
                                gotoFullscreen(PlayDirectionEnume.SCREEN_FULLSCREEN_VERTICALSCREEN);
                            } else {
                                gotoNormalScreen(PlayDirectionEnume.SCREEN_NORMAL);
                            }
                            break;
                    }
                    blockingDelayTime = System.currentTimeMillis();
                }
            }

            @Override
            public void onClickBtnPlay(View v1) {
                play();
            }

            @Override
            public void onClickMultiple(View v1) {

            }

            @Override
            public void onClickAnthology(View v1) {

            }
        };
    }

    @Override
    public void setUp(DataSource dataSource, Class kernelClass) {
        super.setUp(dataSource, kernelClass);
        topMain.setTitle(dataSource.title);
    }

    /**
     * @param percent 缓存百分比
     * @param kernel 内核
     */
    @Override
    public void setBufferProgress(int percent, Class kernel) {

        bottomMain.setSecondaryProgress(percent);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.layout_video;
    }

    @Override
    public void startVideo() {
        super.startVideo();
        retryLayout.setVisibility(GONE);
        showLoading();
    }


    public void onBuffering() {
        super.onBuffering();
        showLoading();
    }

    public void onBufferingEnd() {
        super.onBufferingEnd();
        hideLoading();

    }

    private boolean isLoading = false;

    @Override
    public void showLoading() {
        isLoading = true;
        loadingSpeed.start();
        tvLoading.setVisibility(VISIBLE);
        loadingBarView.show();
    }

    @Override
    public void hideLoading() {
        isLoading = false;
        loadingSpeed.cancel();
        tvLoading.setVisibility(GONE);
        loadingBarView.hide();
    }

    public void startScreenProjection() {
        if (!isValidDataSources()) {
            //弹出提示框最为友好
            onError(ErrorEnume.URL_NULL);
            return;
        }
        if (clingDevice == null) {
            //开始搜索设备
            scanningDevice();
        } else {
            //对非投屏 正在播放的视频进行暂停
            pausePlay();
            //设置投屏数据
            ScreenProjectionUtil.startScreenProjection(clingDevice, dataSource.title, dataSource.getCurrentUrl());
            //开始播放
            postDelayed(() -> ScreenProjectionUtil.play(), 1000);
        }
    }

    private ClingDevice clingDevice;
    private ArrayAdapter<ClingDevice> listAdapter;
    private LinearLayout scanProgressView;
    private ButtomDialogView dialogView;

    /**
     * 投屏选择设备
     */
    public void showDeviceDialog() {
        if (dialogView == null) {
            if (listAdapter == null) {
                listAdapter = new ArrayAdapter(mContext, R.layout.toup_item);
            }
            View view = View.inflate(mContext, R.layout.toup_device_layout, null);
            dialogView = new ButtomDialogView(getContext(), view, false, true);
            ListView listView = view.findViewById(R.id.list_item);
            listView.setDivider(new ColorDrawable(0x00000000));
            listView.setDividerHeight(0);
            listView.setSelector(SelectorFactory.newGeneralSelector()
                    .setPressedDrawable(Color.TRANSPARENT)
                    .setSelectedDrawable(Color.TRANSPARENT)
                    .setFocusedDrawableId(Color.TRANSPARENT).create());
            listView.setFadingEdgeLength(0);
            listView.setVerticalScrollBarEnabled(true);
            scanProgressView = view.findViewById(R.id.schedule);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                clingDevice = listAdapter.getItem(position);
                Toast.makeText(getContext(), "选择了设备 " + clingDevice.getDevice().getDetails().getFriendlyName(), Toast.LENGTH_LONG).show();
                toupBottomMain.setTotal("00:00");
                toupBottomMain.setCurrent("00:00");
                toupBottomMain.setMaxProgress(0);
                toupBottomMain.setProgress(0);
                toupingMian.setVisibility(VISIBLE);
                ((TextView) toupingMian.findViewById(R.id.device_name)).setText("正在投屏:" + clingDevice.getDevice().getDetails().getFriendlyName());
                if (!isValidDataSources()) {
                    //弹出提示框最为友好
                    onError(ErrorEnume.URL_NULL);
                    return;
                }
                dialogView.cancel();
                //设置投屏数据
                ScreenProjectionUtil.startScreenProjection(clingDevice, dataSource.title, dataSource.getCurrentUrl());
                //对非投屏 正在播放的视频进行暂停
                pausePlay();
                //开始播放
                postDelayed(() -> ScreenProjectionUtil.play(), 1000);
            });
            view.findViewById(R.id.cancel).setOnClickListener(v -> {
                //关闭对话框
                dialogView.cancel();
            });
        }
        //显示对话框
        dialogView.show();
        scanProgressView.setVisibility(listAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
        listAdapter.notifyDataSetChanged();
    }


    /**
     * 投屏扫描设备
     */
    public void scanningDevice() {
        showDeviceDialog();
        ScreenProjectionUtil.searchDevice(new ScreenProjectionUtil.DeviceStateListener() {
            @Override
            public void onDeviceAdded(List<ClingDevice> clingDeviceList) {
                post(() -> {
                    for (ClingDevice device : clingDeviceList) {
                        int index = listAdapter.getPosition(device);
                        if (index >= 0) {
                            listAdapter.remove(device);
                            listAdapter.insert(device, index);
                        } else {
                            listAdapter.add(device);
                        }
                    }
                    //关闭进度
                    if (listAdapter.getCount() > 0 && scanProgressView != null) {
                        scanProgressView.setVisibility(View.GONE);
                    }
                    listAdapter.notifyDataSetChanged();
                });

            }

            @Override
            public void onDeviceRemoved(ClingDevice device) {
                post(() -> {
                    if (clingDevice != null && clingDevice.equals(device)) {
                        //设备被移除了需要用户重新选择设备
                        clingDevice = null;
                    }
                    listAdapter.remove(device);
                    listAdapter.notifyDataSetChanged();
                });
            }
        }, new ScreenProjectionUtil.PlayStateListener() {
            @Override
            public void onStateChanges(PalyStateEnume stateEnume) {
                screenProjectionState = stateEnume;
                post(() -> {
                    toupBottomMain.setPlayStat(true);
                    switch (screenProjectionState) {
                        case STATE_PLAYING:
                            toupBottomMain.setPlayStat(true);
                            break;
                        case STATE_STOPED:
                        case STATE_PAUSE:
                            toupBottomMain.setPlayStat(false);
                            break;
                    }
                    if (stateEnume == PalyStateEnume.STATE_STOPED) {
                        //投屏播放停止了 ,关闭投屏UI
                        toupingMian.setVisibility(GONE);
                    }
                });
                LogUtility.d(TAG, stateEnume.getName());
            }

            @Override
            public void onProgressChanges(long currentProgress, long totalTime, String currentTimeStr, String totalTimeStr) {
                if (toupBottomMain.getMaxProgress() != totalTime) {
                    toupBottomMain.setMaxProgress((int) totalTime);
                }
                if (!toupFastForward) {
                    toupBottomMain.setProgress((int) currentProgress);
                    toupBottomMain.setCurrent(currentTimeStr);
                }
                toupBottomMain.setTotal(totalTimeStr);

            }
        });
    }


    @Override
    public void onStatePlaying(boolean playWhenReady) {
        super.onStatePlaying(playWhenReady);
        bottomMain.setPlayStat(true);
        hideLoading();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        hideLoading();

    }

    @Override
    public void setSecondaryProgress(int bufferProgress) {
        bottomMain.setSecondaryProgress(bufferProgress);
    }

    @Override
    public void setProgress(int progress) {
        bottomMain.setProgress(progress);
    }

    @Override
    public void onProgress(int progress, long currentPosition, long duration, String currentTime, String totalTime) {
        setTotal(totalTime);
        setCurrent(currentTime);
        setProgress(progress);
    }

    @Override
    public void setCurrent(String currentTime) {
        bottomMain.setCurrent(currentTime);
    }

    @Override
    public void setTotal(String totalTime) {
        bottomMain.setTotal(totalTime);
    }

    @Override
    public void setPlayStat(boolean isPlay) {
        bottomMain.setPlayStat(isPlay);
    }


    @Override
    public void showVolumeDialog(int volumePercent) {
        if (mVolumeDialog == null) {
            mVolumeDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_volume, null);
            volumeProgressBar = mVolumeDialog.findViewById(R.id.volume);
            volumeImage = mVolumeDialog.findViewById(R.id.volume_image);
        }
        if (mVolumeDialog.getParent() == null) {
            dialogRelativeLayout.removeAllViews();
            dialogRelativeLayout.addView(mVolumeDialog);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mVolumeDialog.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.topMargin = getContext().getResources().getDimensionPixelSize(R.dimen.play_top_height);
        }
        if (volumePercent <= 0) {
            volumeImage.setImageResource(R.drawable.volume_0);
        } else if (volumePercent >= 30 && volumePercent < 60) {
            volumeImage.setImageResource(R.drawable.volume_1);
        } else if (volumePercent >= 60 && volumePercent < 90) {
            volumeImage.setImageResource(R.drawable.volume_2);
        } else if (volumePercent >= 90) {
            volumeImage.setImageResource(R.drawable.volume_3);
        }
        volumeProgressBar.setProgress(volumePercent);
    }


    @Override
    public void dismissVolumeDialog() {
        dialogRelativeLayout.removeAllViews();
    }

    @Override
    public void showBrightnessDialog(int brightnessPercent) {
        if (mBrightnessDialog == null) {
            mBrightnessDialog = LayoutInflater.from(getContext()).inflate(R.layout.brightness_dialog, null);
            brightnessProgressBar = mBrightnessDialog.findViewById(R.id.brightness_progress_bar);
        }
        if (mBrightnessDialog.getParent() == null) {
            dialogRelativeLayout.addView(mBrightnessDialog);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBrightnessDialog.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.topMargin = getContext().getResources().getDimensionPixelSize(R.dimen.play_top_height);
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        brightnessProgressBar.setProgress(brightnessPercent);
    }

    @Override
    public void showProgressDialog(int progressPercentage, long time, String currentTime, String totalTime) {
        if (mProgresstnessDialog == null) {
            mProgresstnessDialog = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog, null);
            current = mProgresstnessDialog.findViewById(R.id.current);
            total = mProgresstnessDialog.findViewById(R.id.total);
            videoDialogPar = mProgresstnessDialog.findViewById(R.id.video_par);
        }
        if (mProgresstnessDialog.getParent() == null) {
            dialogRelativeLayout.removeAllViews();
            dialogRelativeLayout.addView(mProgresstnessDialog, new RelativeLayout.LayoutParams(-1, -1));
            Paint paint = new Paint();
            Rect rect = new Rect();
            paint.setTextSize(current.getTextSize());
            if (currentTime.length() == 8) {
                paint.getTextBounds("23:59:59", 0, 8, rect);
            } else {
                paint.getTextBounds("59:59", 0, 5, rect);
            }
            int w = rect.width();
            int h = getContext().getResources().getDimensionPixelSize(R.dimen.progress_block_height);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.addRule(RelativeLayout.BELOW, R.id.main);
            videoDialogPar.setLayoutParams(lp);
            RelativeLayout.LayoutParams lpGen = (RelativeLayout.LayoutParams) mProgresstnessDialog.findViewById(R.id.gen).getLayoutParams();
            lpGen.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lpGen.addRule(RelativeLayout.CENTER_VERTICAL);
        }
        total.setText(totalTime);
        current.setText(currentTime);
        videoDialogPar.setProgress(progressPercentage);

    }

    @Override
    public void dismissProgressDialog() {
        dialogRelativeLayout.removeAllViews();
    }

    @Override
    public void dismissBrightnessDialog() {
        dialogRelativeLayout.removeAllViews();
    }

    @Override
    protected SYTouchSpeedLayout getSpeedLayout() {
        return syTouchSpeedLayout;
    }

    @Override
    protected void startControlViewTimer() {
        super.startControlViewTimer();
        bottomMain.setVisibility(VISIBLE);
        topMain.setVisibility(VISIBLE);
    }

    @Override
    protected void dissmissControlView() {
        super.dissmissControlView();
        bottomMain.setVisibility(GONE);
        topMain.setVisibility(GONE);
    }

    @Override
    public int getParProgress() {
        return bottomMain.getParProgress();
    }

    @Override
    protected void tvSpeedSetText(DoubleSpeedEnume doubleSpeedEnume) {

    }


    @Override
    public void onSmallWindowIsOpened() {

    }

    @Override
    public void onSmallWindowIsClose() {

    }

    @Override
    public void onWindowChanges(PlayDirectionEnume directionEnume) {
        switch (directionEnume) {
            case SCREEN_FULLSCREEN_VERTICALSCREEN:
                bottomMain.setWindowEnume(WindowEnume.TYPE_FULL_VERTICAL_SCREEN);
                topMain.setWindowEnume(WindowEnume.TYPE_FULL_VERTICAL_SCREEN);
                break;
            case SCREEN_FULLSCREEN_HORIZONTALSCREEN:
                bottomMain.setWindowEnume(WindowEnume.TYPE_FULL_HORIZONTAL_SCREEN);
                topMain.setWindowEnume(WindowEnume.TYPE_FULL_HORIZONTAL_SCREEN);
                break;
            case SCREEN_NORMAL:
                bottomMain.setWindowEnume(WindowEnume.TYPE_ORDINARY);
                topMain.setWindowEnume(WindowEnume.TYPE_ORDINARY);
                break;
            case SCREEN_SMALL_WINDOW:
                bottomMain.setWindowEnume(WindowEnume.TYPE_SMALL_WINDOW);
                topMain.setWindowEnume(WindowEnume.TYPE_SMALL_WINDOW);
                break;

        }
        if (isLoading) {
            showLoading();
        }
    }

    @Override
    public void onError(Class kernel, @Nullable String message, final int what, final int extra) {
        super.onError(kernel, message, what, extra);
        state = PalyStateEnume.STATE_ERROR;
        retryLayout.setVisibility(VISIBLE);
        dissmissControlView();
        hideLoading();


    }

    public void errorHandling() {
        if (dataSource.sniffing) {
            dataSource.setUrl(null);
        }
        PlayUtil.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        state = PalyStateEnume.STATE_ERROR;
    }


}
