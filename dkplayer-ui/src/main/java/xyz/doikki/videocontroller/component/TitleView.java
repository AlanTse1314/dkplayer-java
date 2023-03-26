package xyz.doikki.videocontroller.component;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.impl.FullScreenPopupView;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videocontroller.adapter.TagAdapter;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 播放器顶部标题栏
 */
public class TitleView extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private TextView mSysTime;//系统当前时间

    private BatteryReceiver mBatteryReceiver;
    private boolean mIsRegister;//是否注册BatteryReceiver

    public TextView tvQxd;
    public ImageView ivMenu;
    public BasePopupView f6517g;
    public TextView p;
    public TextView q;
    public int[] r;
    public float[] s;
    public String[] t;
    public String[] u;
    public Switch f6522l;
    public Switch f6523m;
    public Switch n;
    public Switch o;
    public boolean f6519i;

    public int f6520j;
    public int f6521k;

    public class CustomFullScreenPopup extends FullScreenPopupView {

        public CustomFullScreenPopup(@NonNull Context context) {
            super(context);
        }

        /**
         * 执行初始化
         */
        @Override
        protected void init() {
            super.init();
            findViewById(R.id.ll_pop).setOnClickListener(new View.OnClickListener() { // from class: StandardVideoController.j.a.d.a
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    f6517g.dismiss();
                }
            });
            findViewById(R.id.ll_bfsz).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "LinearLayout ll_bfsz", Toast.LENGTH_SHORT).show();
                }
            });
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rl_sd);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            TagAdapter tagAdapter = new TagAdapter(R.layout.adapter_item_tag_sd, TitleView.this.t, getContext());
            recyclerView.setAdapter(tagAdapter);
            tagAdapter.setOnClickListener(new TagAdapter.OnClickListener() {
                @Override
                public void onClick(int i2) {
                    tagAdapter.f(i2);
//                    k.c.a.c.c().k(new c.h.a.b.a(i2, TitleView.this.f6521k));
                    ////////////////////////////////////////////////////////////////////////////////
                    f6520j = i2;

                    mControlWrapper.setSpeed(s[f6520j]);
                    f6517g.dismiss();
                }
            });
            tagAdapter.f(TitleView.this.f6520j);
            RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.rl_xs);
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
            linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView2.setLayoutManager(linearLayoutManager2);
            TagAdapter tagAdapter2 = new TagAdapter(R.layout.adapter_item_tag, TitleView.this.u, getContext());
            recyclerView2.setAdapter(tagAdapter2);
            tagAdapter2.setOnClickListener(new TagAdapter.OnClickListener() {
                @Override
                public void onClick(int i2) {
                    tagAdapter2.f(i2);
//                    k.c.a.c.c().k(new c.h.a.b.a(TitleView.this.f6520j, i2));
                    //////////////////////////////////////////////////////////////////////////
                    f6521k = i2;

                    mControlWrapper.setScreenScaleType(r[f6521k]);
                    f6517g.dismiss();
                }
            });
            tagAdapter2.f(TitleView.this.f6521k);
            TitleView.this.f6523m = (Switch) findViewById(R.id.s_pro);
            TitleView.this.n = (Switch) findViewById(R.id.s_next);
            TitleView.this.f6522l = (Switch) findViewById(R.id.bt_tgpt);
            TitleView.this.o = (Switch) findViewById(R.id.bt_tgpw);
//            TitleView.this.f6523m.setChecked(TitleView.this.v.c("pro_show", false));
            TitleView.this.f6523m.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    TitleView.this.v.s("pro_show", z);
                    f6517g.dismiss();
                    Toast.makeText(getContext(), "重载影片生效",Toast.LENGTH_LONG).show();
                }
            });
//            TitleView.this.n.setChecked(TitleView.this.v.c("tv_next", true));
            TitleView.this.n.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getContext(), "已开启自动下一集",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "已关闭自动下一集",Toast.LENGTH_LONG).show();
                    }
//                    TitleView.this.v.s("tv_next", z);
                    f6517g.dismiss();
                }
            });
            TitleView.this.q = (TextView) findViewById(R.id.tv_pw_time);
//            if (TitleView.this.v.c("bt_tgpw", false)) {
//                TitleView titleView = TitleView.this;
//                titleView.q.setText(String.format("片尾：%s秒", Long.valueOf(titleView.v.f("videodur", 180000L) / 1000)));
//                TitleView.this.q.setVisibility(0);
//            } else {
                TitleView.this.q.setVisibility(GONE);
//            }
//            TitleView.this.o.setChecked(TitleView.this.v.c("bt_tgpw", false));
            TitleView.this.o.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    TitleView.this.v.s("bt_tgpw", z);
                    if (isChecked) {
//                        TitleView titleView = TitleView.this;
//                        titleView.q.setText(String.format("片尾：%s秒", Long.valueOf(titleView.v.f("videodur", 180000L) / 1000)));
                        TitleView.this.q.setVisibility(VISIBLE);


                        XPopup.Builder xp= new XPopup.Builder(getContext());
                        PwTimePopup pwTimePopup = new PwTimePopup(getContext());
                        xp.asCustom(pwTimePopup);
                        pwTimePopup.show();


//                        f.a aVar = new f.a(CustomFullScreenPopup.this.getContext());
//                        aVar.v(true);
//                        CustomFullScreenPopup customFullScreenPopup = CustomFullScreenPopup.this;
//                        PwTimePopup pwTimePopup = new PwTimePopup(customFullScreenPopup.getContext());
//                        aVar.h(pwTimePopup);
//                        pwTimePopup.H();
                        return;
                    }
                    TitleView.this.q.setVisibility(GONE);
                }
            });
            TitleView.this.p = (TextView) findViewById(R.id.tv_time);
//            if (TitleView.this.v.c("bt_tgpt", true)) {
//                TitleView titleView2 = TitleView.this;
//                titleView2.p.setText(String.format("片头：%s秒", Integer.valueOf(titleView2.v.d("kt_time", 110))));
//                TitleView.this.p.setVisibility(0);
//            } else {
                TitleView.this.p.setVisibility(GONE);
//            }
//            TitleView.this.f6522l.setChecked(TitleView.this.v.c("bt_tgpt", true));
            TitleView.this.f6522l.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    TitleView.this.v.s("bt_tgpt", z);
                    if (isChecked) {
                        TitleView titleView = TitleView.this;
//                        titleView.p.setText(String.format("片头：%s秒", Integer.valueOf(titleView.v.d("kt_time", 110))));
                        TitleView.this.p.setVisibility(VISIBLE);


                        XPopup.Builder xp= new XPopup.Builder(getContext());
                        PtTimePopup pwTimePopup = new PtTimePopup(getContext());
                        xp.asCustom(pwTimePopup);
                        pwTimePopup.show();
//                        f.a aVar = new f.a(CustomFullScreenPopup.this.getContext());
//                        aVar.v(true);
//                        CustomFullScreenPopup customFullScreenPopup = CustomFullScreenPopup.this;
//                        PtTimePopup ptTimePopup = new PtTimePopup(customFullScreenPopup.getContext());
//                        aVar.h(ptTimePopup);
//                        ptTimePopup.H();
                        return;
                    }
                    TitleView.this.p.setVisibility(GONE);
                }
            });
            TitleView.this.q.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    XPopup.Builder xp= new XPopup.Builder(getContext());
                    PwTimePopup pwTimePopup = new PwTimePopup(getContext());
                    xp.asCustom(pwTimePopup);
                    pwTimePopup.show();


//                    f.a aVar = new f.a(CustomFullScreenPopup.this.getContext());
//                    aVar.v(true);
//                    CustomFullScreenPopup customFullScreenPopup = CustomFullScreenPopup.this;
//                    PwTimePopup pwTimePopup = new PwTimePopup(customFullScreenPopup.getContext());
//                    aVar.h(pwTimePopup);
//                    pwTimePopup.H();
                }
            });
            TitleView.this.p.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    XPopup.Builder xp= new XPopup.Builder(getContext());
                    PtTimePopup pwTimePopup = new PtTimePopup(getContext());
                    xp.asCustom(pwTimePopup);
                    pwTimePopup.show();

//                    f.a aVar = new f.a(CustomFullScreenPopup.this.getContext());
//                    aVar.v(true);
//                    CustomFullScreenPopup customFullScreenPopup = CustomFullScreenPopup.this;
//                    PtTimePopup ptTimePopup = new PtTimePopup(customFullScreenPopup.getContext());
//                    aVar.h(ptTimePopup);
//                    ptTimePopup.H();
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
            return R.layout.menu_drawer_popup;
        }
    }
    public class PwTimePopup extends CenterPopupView {
        public Button button;
        public Button btn;
        public EditText editText;
        public PwTimePopup(@NonNull Context context) {
            super(context);
        }

        /**
         * 执行初始化
         */
        @Override
        protected void init() {
            super.init();
            editText = (EditText) findViewById(R.id.et_name);
            button = (Button) findViewById(R.id.bt_cancel);
            btn = (Button) findViewById(R.id.bt_save);
//            editText.setText(Long.toString(TitleView.this.v.f("videodur", 180000L) / 1000));
            mTitleContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PwTimePopup pwTimePopup = PwTimePopup.this;
//                    TitleView.this.v.p("videodur", Long.parseLong(pwTimePopup.z.getText().toString()) * 1000);
                    PwTimePopup pwTimePopup2 = PwTimePopup.this;
                    q.setText(String.format("片尾：%s秒", editText.getText().toString()));
                    q.setVisibility(VISIBLE);
                    dismiss();
                }
            });
        }

        /**
         * 具体实现的类的布局
         *
         * @return
         */
        @Override
        protected int getImplLayoutId() {
            return R.layout.pw_popup;
        }
    }

    public class PtTimePopup extends CenterPopupView {
        public Button A;
        public Button B;
        public EditText z;
        public PtTimePopup(@NonNull Context context) {
            super(context);
        }

        /**
         * 执行初始化
         */
        @Override
        protected void init() {
            super.init();
            this.z = (EditText) findViewById(R.id.et_name);
            this.B = (Button) findViewById(R.id.bt_cancel);
            this.A = (Button) findViewById(R.id.bt_save);
//            this.z.setText(Integer.toString(TitleView.this.v.d("kt_time", 110)));
            this.B.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            this.A.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    PtTimePopup ptTimePopup = PtTimePopup.this;
//                    TitleView.this.v.o("kt_time", Integer.parseInt(ptTimePopup.z.getText().toString()));
                    PtTimePopup ptTimePopup2 = PtTimePopup.this;
                    p.setText(String.format("片头：%s秒", ptTimePopup2.z.getText().toString()));
                    p.setVisibility(VISIBLE);
                    dismiss();
                }
            });
        }

        /**
         * 具体实现的类的布局
         *
         * @return
         */
        @Override
        protected int getImplLayoutId() {
            return R.layout.pt_popup;
        }

    }


    public TitleView(@NonNull Context context) {
        super(context);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_title_view, this, true);
        mTitleContainer = findViewById(R.id.title_container);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = PlayerUtils.scanForActivity(getContext());
                if (activity != null && mControlWrapper.isFullScreen()) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mControlWrapper.stopFullScreen();
                }
            }
        });
        mTitle = findViewById(R.id.title);
        mSysTime = findViewById(R.id.sys_time);
        //电量
        ImageView batteryLevel = findViewById(R.id.iv_battery);
        mBatteryReceiver = new BatteryReceiver(batteryLevel);

        this.tvQxd = (TextView) findViewById(R.id.tv_qxd);
        ImageView imageView = (ImageView) findViewById(R.id.iv_menu);
        this.ivMenu = imageView;
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlWrapper.hide();
//                TitleView titleView = TitleView.this;
//                f.a aVar = new f.a(titleView.getContext());
//                aVar.s(false);
//                aVar.r(false);
//                aVar.p(false);
//                aVar.v(true);
//                TitleView titleView2 = TitleView.this;
                XPopup.Builder xp= new XPopup.Builder(getContext());
                CustomFullScreenPopup customFullScreenPopup = new CustomFullScreenPopup(getContext());
                xp.asCustom(customFullScreenPopup);
                customFullScreenPopup.show();
                f6517g = customFullScreenPopup;

//                CustomFullScreenPopup customFullScreenPopup = new CustomFullScreenPopup(titleView2.getContext());
//                aVar.h(customFullScreenPopup);
//                customFullScreenPopup.H();
//                titleView.f6517g = customFullScreenPopup;
            }
        });
        this.r = new int[]{0, 4, 3, 5};
        this.s = new float[]{1.0f, 1.25f, 1.5f, 2.0f, 3.0f};
        this.t = new String[]{"1.0X", "1.25X", "1.5X", "2.0X", "3.0X"};
        this.u = new String[]{"自适应", "原始尺寸", "填充屏幕", "居中裁剪"};
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIsRegister) {
            getContext().unregisterReceiver(mBatteryReceiver);
            mIsRegister = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mIsRegister) {
            getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            mIsRegister = true;
        }
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
        //只在全屏时才有效
        if (!mControlWrapper.isFullScreen()) return;
        if (isVisible) {
            if (getVisibility() == GONE) {
                mSysTime.setText(PlayerUtils.getCurrentSystemTime());
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            if (mControlWrapper.isShowing() && !mControlWrapper.isLocked()) {
                setVisibility(VISIBLE);
                mSysTime.setText(PlayerUtils.getCurrentSystemTime());
            }
            mTitle.setSelected(true);
        } else {
            setVisibility(GONE);
            mTitle.setSelected(false);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mTitleContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mTitleContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mTitleContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mSysTime.setText(PlayerUtils.getCurrentSystemTime());
        }
    }

    private static class BatteryReceiver extends BroadcastReceiver {
        private ImageView pow;

        public BatteryReceiver(ImageView pow) {
            this.pow = pow;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;
            int current = extras.getInt("level");// 获得当前电量
            int total = extras.getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            pow.getDrawable().setLevel(percent);
        }
    }
}
