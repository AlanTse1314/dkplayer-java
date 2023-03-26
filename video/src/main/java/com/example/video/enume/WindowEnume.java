package com.example.video.enume;

import java.util.HashMap;
import java.util.Map;

public enum WindowEnume {

    TYPE_SMALL_WINDOW(1, "小窗"),
    TYPE_ORDINARY(2, "普通非全屏"),
    TYPE_FULL_HORIZONTAL_SCREEN(3, "全屏横屏"),
    TYPE_FULL_VERTICAL_SCREEN(4, "全屏竖屏"),
    TYPE_SCREEN_PROJECTION(5, "正在投屏");

    private Integer code;
    private String name;
    private static final Map<Integer, WindowEnume> enmuMap = new HashMap<>();

    static {
        for (WindowEnume item : values()) {
            enmuMap.put(item.getCode(), item);
        }
    }

    WindowEnume(Integer code, String name) {
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

    public static WindowEnume getType(int code) {
        return enmuMap.get(code)!=null?enmuMap.get(code):null;
    }

}
