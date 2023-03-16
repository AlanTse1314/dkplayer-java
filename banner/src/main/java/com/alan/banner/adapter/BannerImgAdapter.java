package com.alan.banner.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.alan.banner.R;
import com.alan.banner.bean.BannerBean;
import com.alan.banner.listener.OnBannerClickListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


/**
 * ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class BannerImgAdapter extends RecyclingPagerAdapter {
    public Context       context;
    public List<BannerBean> imageIdList;
    private OnBannerClickListener onBannerClickListener;
    public int           size;
    public boolean       isInfiniteLoop;

    public BannerImgAdapter(Context context, List<BannerBean> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList == null ? new ArrayList() : new ArrayList(imageIdList);
        this.size = imageIdList == null ? 0 : imageIdList.size();
        if (size == 0) {
            this.imageIdList.add(new BannerBean());
        }
        isInfiniteLoop = false;
    }

    public void add(List<BannerBean> imageIdList){
        this.imageIdList.clear();
        this.imageIdList.addAll(imageIdList);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        int size = this.imageIdList == null ? 0 : this.imageIdList.size();
        if (this.isInfiniteLoop) {
            return Integer.MAX_VALUE;
        }
        return size;
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(this.context).inflate(R.layout.item_banner_img, container, false);
            holder.imageView = (ImageView) view.findViewById(R.id.imageview);
//            holder.frameLayout = (FrameLayout) view.findViewById(R.id.flyt_ad);
//            view = holder.imageView = new ImageView(context);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            holder.imageView.setLayoutParams(layoutParams);


            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if (onBannerClickListener != null) {
            holder.imageView.setOnClickListener(views ->onBannerClickListener.OnBannerClick(imageIdList.get(position),position));
        }


//        holder.frameLayout.setVisibility(View.GONE);
//        holder.imageView.setVisibility(View.VISIBLE);
//        holder.imageView.setOnClickListener(new View.OnClickListener() { // from class: me.tvspark.wrapper.adapter.BannerImgAdapter.1
//            @Override // android.view.View.OnClickListener
//            public void onClick(View v) {
//                Toast.makeText(context,"位置"+position+"",Toast.LENGTH_SHORT).show();
////                ActivityJump.a(BannerImgAdapter.this.b, BannerImgAdapter.this.e, ((VideoParcel) BannerImgAdapter.this.a.get(position)).getLink(), ((VideoParcel) BannerImgAdapter.this.a.get(position)).getTitle(), ((VideoParcel) BannerImgAdapter.this.a.get(position)).getCoverUrl(), ((VideoParcel) BannerImgAdapter.this.a.get(position)).getVideoType(), ((VideoParcel) BannerImgAdapter.this.a.get(position)).getType());
//            }
//        });

        Glide.with(this.context).load(this.imageIdList.get(position).getImg()).into(holder.imageView);
//        ImageLoader.load(this.context, this.imageIdList.get(position).getImg(), holder.imageView);
        return view;
    }

    private static class ViewHolder {

        ImageView imageView;
//        FrameLayout frameLayout;
    }

    public void setOnBannerListener(OnBannerClickListener listener) {
        this.onBannerClickListener = listener;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public BannerImgAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}

