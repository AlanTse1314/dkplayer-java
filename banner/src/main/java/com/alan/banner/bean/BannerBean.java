package com.alan.banner.bean;

public class BannerBean {
    /**
     * 图片地址
     */
    public String img;
    /**
     * 标题
     */
    public String title;
    /**
     * 链接地址
     */
    public String link;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public BannerBean(String img, String title, String link){
        this.img = img;
        this.title = title;
        this.link = link;
    }

    public BannerBean(){

    }


}
