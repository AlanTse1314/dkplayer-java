package com.example.video.configure;

/**
 * 控制是否显示默认UI
 */
public class UIConfigure {
    private boolean smallWindow = true;//小窗
    private boolean batteryLevel = true;//电池
    private boolean screenProjection = true;//投屏
    private boolean setting = true;//设置
    private boolean back = true;//返回
    private boolean multiple = true; //倍速
    private boolean fullScreen = true;//全屏
    private boolean anthology = false;//选集

    public boolean isSmallWindow() {
        return smallWindow;
    }

    public void setSmallWindow(boolean smallWindow) {
        this.smallWindow = smallWindow;
    }

    public boolean isBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(boolean batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isScreenProjection() {
        return screenProjection;
    }

    public void setScreenProjection(boolean screenProjection) {
        this.screenProjection = screenProjection;
    }

    public boolean isSetting() {
        return setting;
    }

    public void setSetting(boolean setting) {
        this.setting = setting;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public void setAnthology(boolean anthology) {
        this.anthology = anthology;
    }

    public boolean isAnthology() {
        return anthology;
    }
}
