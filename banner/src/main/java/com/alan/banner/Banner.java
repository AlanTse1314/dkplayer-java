package com.alan.banner;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.alan.banner.adapter.BannerImgAdapter;
import com.alan.banner.bean.BannerBean;
import com.alan.banner.indicator.CirclePageIndicator;
import com.alan.banner.listener.OnBannerChangeListener;
import com.alan.banner.listener.OnBannerClickListener;
import com.alan.banner.viewpage.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public  class Banner extends FrameLayout {
    public Context context;
    public BannerImgAdapter bannerImgAdapter;

    public List<BannerBean> listBanner = new ArrayList();

    public OnBannerChangeListener onBannerChangeListener = null;

    public CirclePageIndicator indicator;

    public TextView tvIndicator;

    public AutoScrollViewPager viewpager;
//    BannerClick(BannerBean bannerBean);
//    OnBannerClickListener bannerClick;



//    //定义一个接口对象listerner
//    private OnItemSelectListener listener;
//    //获得接口对象的方法。
//    public void setOnItemSelectListener(OnItemSelectListener listener) {
//        this.listener = listener;
//    }
//    //定义一个接口
//    public interface  OnItemSelectListener{
//        BannerBean bannerBean = listBanner.get(viewpager.getCurrentItem());
//        public void onItemSelect(BannerBean bannerBean);
//    }

    public BannerImgAdapter getAdapter() {
        return bannerImgAdapter;
    }

    /**
     * 设置点击事件
     */
    public void setOnBannerListener(OnBannerClickListener listener) {
        if (getAdapter() != null) {
            getAdapter().setOnBannerListener(listener);
        }

    }


    public Banner(@NonNull Context context) {
        super(context);
        this.context = context;
        init(context);
    }


    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }


    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void setBannerList(List<BannerBean> list) {
        bannerImgAdapter.imageIdList.clear();
        bannerImgAdapter.imageIdList.addAll(list);
        bannerImgAdapter.notifyDataSetChanged();
    }


    private void init(Context context) {
        View v = View.inflate(context, R.layout.item_index_list_banner, this);
        indicator = v.findViewById(R.id.viewpager_indicator);
        tvIndicator = v.findViewById(R.id.tv_indicator_title);
        viewpager = v.findViewById(R.id.viewpager_content);
        this.bannerImgAdapter = new BannerImgAdapter(this.context, listBanner);
        viewpager.setAdapter(this.bannerImgAdapter);
        indicator.setViewPager(viewpager);
        if(listBanner.size()>0){
            tvIndicator.setText(listBanner.get(viewpager.getCurrentItem()).getTitle());
//            if(listener != null){
//                listener.onItemSelect(listBanner.get(viewpager.getCurrentItem()));
//            }

        }


        this.onBannerChangeListener = new OnBannerChangeListener(tvIndicator, listBanner);
        onBannerChangeListener.onBannerChangeListener.clear();
        onBannerChangeListener.onBannerChangeListener.addAll(listBanner);
        viewpager.addOnPageChangeListener(onBannerChangeListener);
        viewpager.startAutoScroll();















        ////////////////////////////////////////////////////
//        RelativeLayout r = new RelativeLayout(context);
//        r.setBackgroundResource(R.color.transparent);
//        LinearLayout l = new LinearLayout(context);
////        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, 34);
////        l.setlay
//        viewpager = new AutoScrollViewPager(context);

//        bannerImgAdapter = new BannerImgAdapter(this.context,listBanner);
//        viewpager = new AutoScrollViewPager(this.context);
//        viewpager.setAdapter(bannerImgAdapter);
//
//        FrameLayout.LayoutParams leftParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp34));
//
//        LinearLayout layout = new LinearLayout(this.context);
//        layout.setOrientation(LinearLayout.HORIZONTAL);
//        layout.setLayoutParams(leftParams);
//        layout.setBackground(context.getDrawable(R.drawable.shape_gray_to_trans));
//        TextView tv = new TextView(context);
//        leftParams = new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
//        leftParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp3),0,0,0);
//
//        tv.setPadding(getResources().getDimensionPixelSize(R.dimen.dp10),0,0,0);
//        tv.setMaxLines(1);
//        tv.setTextColor(context.getColor(R.color.titleColor));
//        tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.sp14));
//        tv.setGravity(TEXT_ALIGNMENT_CENTER);
//        tv.setEllipsize(TextUtils.TruncateAt.END);
//
//        tv.setLayoutParams(leftParams);
//        android:id="@+id/tv_indicator_title"
//        android:layout_width="0dp"
//        android:layout_height="match_parent"
//        android:layout_marginLeft="3dp"
//        android:layout_weight="1"
//        android:ellipsize="end"
//        android:gravity="center_vertical"
//        android:maxLines="1"
//        android:paddingLeft="10dp"
//        android:textColor="#ffffff"
//        android:textSize="14sp" />
//        layout.addView(tv);
//
//        indicator = new CirclePageIndicator(this.context);
//        leftParams = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dp100), ViewGroup.LayoutParams.MATCH_PARENT);
//        leftParams.setMargins(0,0,getResources().getDimensionPixelSize(R.dimen.dp3),0);
//        indicator.setBackground(context.getDrawable(R.color.transparent));
//        indicator.setPadding(0,getResources().getDimensionPixelSize(R.dimen.dp17),0,0);
//        indicator.setFillColor(context.getColor(R.color.indicatorFill));
//        indicator.setPageColor(context.getColor(R.color.indicatorPage));
//        indicator.setRadius(getResources().getDimensionPixelSize(R.dimen.dp3));
//        indicator.setStrokeColor(context.getColor(R.color.transparent));
//        indicator.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.dp0));
//        indicator.setLayoutParams(leftParams);
//        android:id="@+id/viewpager_indicator"
//        android:layout_width="100dp"
//        android:layout_height="match_parent"
//        android:layout_marginRight="3dp"
//        android:background="@android:color/transparent"
//        android:paddingTop="17dp"
//        app:fillColor="#ff00a2ff"
//        app:pageColor="#ff666666"
//        app:radius="3dp"
//        app:strokeColor="@android:color/transparent"
//        app:strokeWidth="0dp"






//        indicator.setViewPager(viewpager);
//        onBannerChangeListener = new OnBannerChangeListener(tvIndicator,listBanner);
//        layout.addView(indicator);
//        addView(layout);
    }
//    public Banner setAdapter(BannerImgAdapter adapter) {
//        if (adapter == null) {
//            throw new NullPointerException("getContext().getString(R.string.banner_adapter_null_error)");
//        }
////        this.bannerImgAdapter = adapter;
//////        if (!isInfiniteLoop()) {
//////            getAdapter().setIncreaseCount(0);
//////        }
//////        getAdapter().registerAdapterDataObserver(mAdapterDataObserver);
////        viewpager.setAdapter(adapter);
////        indicator.setViewPager(viewpager);
//
//        viewpager.setAdapter(this.bannerImgAdapter);
//        indicator.setViewPager(viewpager);
//        tvIndicator.setText(adapter.imageIdList.get(viewpager.getCurrentItem()).getTitle());
//        this.onBannerChangeListener = new OnBannerChangeListener(tvIndicator, adapter.imageIdList);
//        onBannerChangeListener.onBannerChangeListener.clear();
//        onBannerChangeListener.onBannerChangeListener.addAll(adapter.imageIdList);
//        viewpager.addOnPageChangeListener(onBannerChangeListener);
//        viewpager.startAutoScroll();
//        return this;
//    }


    public  void setBannerData(List<BannerBean> listBanner){
        this.listBanner.clear();
        this.listBanner.addAll(listBanner);
        bannerImgAdapter.add(listBanner);
    }

//    @Override
//    public void onClick(View view) {
////        bannerClick = new OnBannerClickListener() {
////            @Override
////            public void BannerClick(BannerBean bannerBean) {
////                Toast.makeText(context, bannerBean.getTitle(), Toast.LENGTH_SHORT).show();
////            }
////        };
//        BannerClick(listBanner.get(viewpager.getCurrentItem()));
//
//
//    }
//
//    abstract void BannerClick(BannerBean bannerBean);
//
//public void setBannerOnClick(){
//
//}


}
