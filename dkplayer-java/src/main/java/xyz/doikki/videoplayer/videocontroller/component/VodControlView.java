package xyz.doikki.videoplayer.videocontroller.component;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.DrawerPopupView;
import com.lxj.xpopup.enums.PopupPosition;

import java.util.List;

import xyz.doikki.videoplayer.R;
import xyz.doikki.videoplayer.videocontroller.adapter.XPopAdapter;
import xyz.doikki.videoplayer.videocontroller.bean.JSBean;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 点播底部控制栏
 */
public class VodControlView extends FrameLayout implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected ControlWrapper mControlWrapper;

    private TextView mTotalTime, mCurrTime;
    private ImageView mFullScreen;
    private LinearLayout mBottomContainer;
    private SeekBar mVideoProgress;
    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;
    public ImageView tvXJ;

    public ImageView ivNext;

    private boolean mIsDragging;

    private boolean mIsShowBottomProgress = true;


    public BasePopupView f6534m;
    public int mCurIndex;
    public RecyclerView o;
    public GridLayoutManager p;
    public XPopAdapter q;
    public List<JSBean> s;

    private OnRateSwitchListener mOnRateSwitchListener;

//    public class CustomDrawerPopupView extends DrawerPopupView {
//        public CustomDrawerPopupView(@NonNull Context context) {
//            super(context);
//        }
//        @Override
//        protected int getImplLayoutId() {
//            return R.layout.xj_drawer_popup;
//        }
//        @Override
//        protected void onCreate() {
//            super.onCreate();
//            findViewById(R.id.rv_xj).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getContext(), "nothing!!!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

    public class CustomDrawerPopupView extends DrawerPopupView {
        public CustomDrawerPopupView(@NonNull Context context) {
            super(context);
        }

        /**
         * 执行初始化
         */
        @Override
        protected void init() {
            super.init();
            o = (RecyclerView) findViewById(R.id.rv_xj);
            p = new GridLayoutManager(getContext(), 3);
            o.setLayoutManager(VodControlView.this.p);

            q = new XPopAdapter(R.layout.xj_item, s, getContext());
            o.setAdapter(q);
            q.setIndex(mCurIndex);
            o.scrollToPosition(mCurIndex);
            q.setOnClickListener(new XPopAdapter.OnClickListener() {
                @Override
                public void onClick(int i2) {
                    q.setIndex(i2);
                    q.notifyDataSetChanged();
                    mCurIndex = i2;
                    switchDefinition(s.get(mCurIndex));
//                    c.c().k(new f(VodControlView.this.n));
//                    DrawerPopupView
//                    f6534m.getAnimation().setAnimationListener(new BasePopupView.ACCESSIBILITY_LIVE_REGION_POLITE);
                    f6534m.dismiss();
                }
            });



        }

        /**
         * 如果你自己继承BasePopupView来做，这个不用实现
         *
         * @return
         */
        @Override
        protected int getImplLayoutId() {
            return R.layout.xj_drawer_popup;
        }
    }

    public VodControlView(@NonNull Context context) {
        super(context);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    {



        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        mFullScreen = findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.bottom_container);
        mVideoProgress = findViewById(R.id.seekBar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = findViewById(R.id.total_time);
        mCurrTime = findViewById(R.id.curr_time);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mBottomProgress = findViewById(R.id.bottom_progress);

        tvXJ = findViewById(R.id.tv_xj);
        tvXJ.setOnClickListener(this);
        ivNext = findViewById(R.id.iv_next);
        ivNext.setOnClickListener(this);
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mVideoProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    protected int getLayoutId() {
        return R.layout.dkplayer_layout_vod_control_view;
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            mBottomContainer.setVisibility(VISIBLE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
        } else {
            mBottomContainer.setVisibility(GONE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                mBottomProgress.startAnimation(animation);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                mPlayButton.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing()) {
                        mBottomProgress.setVisibility(GONE);
                        mBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mBottomContainer.setVisibility(GONE);
                        mBottomProgress.setVisibility(VISIBLE);
                    }
                } else {
                    mBottomContainer.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
            case VideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
            case VideoView.STATE_BUFFERING:
                mPlayButton.setSelected(mControlWrapper.isPlaying());
                // 停止刷新进度
                mControlWrapper.stopProgress();
                break;
            case VideoView.STATE_BUFFERED:
                mPlayButton.setSelected(mControlWrapper.isPlaying());
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                mFullScreen.setSelected(false);
                tvXJ.setVisibility(GONE);
                ivNext.setVisibility(GONE);
                if(f6534m !=null && f6534m.isShow()){
                    f6534m.dismiss();
                }
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                if(s.size()>3){
                    tvXJ.setVisibility(VISIBLE);
                    ivNext.setVisibility(VISIBLE);
                }
                mFullScreen.setSelected(true);
                if(f6534m !=null && f6534m.isShow()){
                    f6534m.dismiss();
                }
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mBottomContainer.setPadding(0, 0, 0, 0);
                mBottomProgress.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
                mBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mBottomContainer.setPadding(0, 0, cutoutHeight, 0);
                mBottomProgress.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTotalTime != null)
            mTotalTime.setText(stringForTime(duration));
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime(position));
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.iv_next) {
//            c.c().k(new e(true));
            if (mCurIndex==s.size()-1){
                mCurIndex=0;
            }else {
                mCurIndex++;
            }
            switchDefinition(s.get(mCurIndex));
        } else if (id == R.id.tv_xj) {
            mControlWrapper.hide();
            CustomDrawerPopupView customDrawerPopupView = new CustomDrawerPopupView(getContext());
            XPopup.Builder xp= new XPopup.Builder(getContext());
                    xp.popupPosition(PopupPosition.Right);
                            xp.hasStatusBar(false);
                            xp.hasNavigationBar(false);
                            xp.asCustom(customDrawerPopupView);
            customDrawerPopupView.show();
//                    .asCustom(new CustomDrawerPopupView(getContext()))
//                    .show();
//
//            setIsLightStatusBar
//                    setIsLightNavigationBar

//            f.a aVar = new f.a(getContext());
//            aVar.x(c.s.b.i.c.Right);
//            aVar.s(false);
//            aVar.r(false);
//            aVar.p(false);
//            aVar.v(true);
//            CustomDrawerPopupView customDrawerPopupView = new CustomDrawerPopupView(getContext());
//            aVar.h(customDrawerPopupView);
//            customDrawerPopupView.H();
            this.f6534m = customDrawerPopupView;
        }
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
        mControlWrapper.seekTo((int) newPosition);
        mIsDragging = false;
        mControlWrapper.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Toast.makeText(getContext(),"跳转到下一集",Toast.LENGTH_LONG).show();
        if (!fromUser) {
            return;
        }

        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime((int) newPosition));
    }

//    @m(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onInitEvent(d dVar) {
//        this.s = dVar.a;
//        this.n = dVar.b;
//    }

    public void setData(List<JSBean> multiRateData) {
        s = multiRateData;
        if (s != null && s.size()>0) {
            L.d("multiRate");
            if (multiRateData == null) return;

            int index = 0;
//            ListIterator<Map.Entry<String, String>> iterator = new ArrayList<>(multiRateData.entrySet()).listIterator(multiRateData.size());
//            while (iterator.hasPrevious()) {//反向遍历
//                Map.Entry<String, String> entry = iterator.previous();
//                mRateStr.add(entry.getKey());
//                TextView rateItem = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_rate_item, null);
//                rateItem.setText(entry.getKey());
//                rateItem.setTag(index);
//                rateItem.setOnClickListener(rateOnClickListener);
//                mPopLayout.addView(rateItem);
//                index++;
//            }
//            ((TextView) mPopLayout.getChildAt(index - 1)).setTextColor(ContextCompat.getColor(getContext(), R.color.theme_color));
//            mDefinition.setText(mRateStr.get(index - 1));
            mCurIndex = 0;
        }
    }
//    private OnClickListener rateOnClickListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            int index = (int) v.getTag();
//            if (mCurIndex == index) return;
//
//            switchDefinition(s.get(index));
//            mCurIndex = index;
//        }
//    };

    private void switchDefinition(JSBean s) {
        mControlWrapper.hide();
        mControlWrapper.stopProgress();
//        String url = mMultiRateData.get(s);
        Log.e("","**************************************************************************************************");
        Log.e("标题：",s.getTitle());
        Log.e("视频地址：",s.getUrl());
        Log.e("","**************************************************************************************************");
        if (mOnRateSwitchListener != null)
            mOnRateSwitchListener.onRateChange(s.getUrl(),s.getTitle());
    }

    public interface OnRateSwitchListener {
        void onRateChange(String url,String title);
    }

    public void setOnRateSwitchListener(OnRateSwitchListener onRateSwitchListener) {
        mOnRateSwitchListener = onRateSwitchListener;
    }
}
