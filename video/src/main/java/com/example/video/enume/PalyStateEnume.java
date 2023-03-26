package com.example.video.enume;

import java.util.HashMap;
import java.util.Map;

public enum PalyStateEnume {

    STATE_IDLE(-1, "UI初始化"),
    STATE_NORMAL(0, "设置了视频源或"),
    STATE_PREPARING(1, "开始准备"),
    STATE_PREPARED(4, "准备就绪"),
    STATE_PREPARING_PLAYING(3, "播放中"),
    STATE_PLAYING(5, "正在播放"),
    STATE_PAUSE(6, "暂停中"),
    STATE_AUTO_COMPLETE(7, "播放完毕"),
    STATE_STOPED(9, "投屏服务单独状态 播放停止"),
    STATE_BUFFER(11, "正在缓冲中..."),
    STATE_BUFFER_END(10, "缓冲结束..."),
    STATE_ERROR(8, "发生错误");

    private Integer code;
    private String name;
    private static final Map<Integer, PalyStateEnume> enmuMap = new HashMap<>();

    static {
        for (PalyStateEnume item : values()) {
            enmuMap.put(item.getCode(), item);
        }
    }

    PalyStateEnume(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(int code) {
        return enmuMap.get(code)!=null?enmuMap.get(code).getName():"未知";
    }

    public static PalyStateEnume getType(int code) {
        return enmuMap.get(code)!=null?enmuMap.get(code):null;
    }

}
