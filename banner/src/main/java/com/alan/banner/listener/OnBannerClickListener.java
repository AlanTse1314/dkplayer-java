package com.alan.banner.listener;

import com.alan.banner.bean.BannerBean;

public interface OnBannerClickListener {

     /**
      * 点击事件
      *
      * @param bannerBean     数据实体
      * @param position 当前位置
      */
     void OnBannerClick(BannerBean bannerBean, int position);
}
