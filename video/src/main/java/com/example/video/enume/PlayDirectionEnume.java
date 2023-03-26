package com.example.video.enume;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum PlayDirectionEnume {

    SCREEN_FULLSCREEN_VERTICALSCREEN(0,"全屏竖屏"),
    SCREEN_FULLSCREEN_HORIZONTALSCREEN(1,"全屏横屏"),
    SCREEN_SMALL_WINDOW(1,"悬浮窗"),
    SCREEN_NORMAL(1,"正常窗口"),
    SCREEN_UNKNOWN(-1,"未知"),
    VERTICAL_SCREEN(5,"竖屏"),
    HORIZONTAL_SCREEN(3,"横屏");


    private Integer code;
    private String name;
    private static final Map<String, PlayDirectionEnume> enmuMap = new HashMap<>();
    static {
        for (PlayDirectionEnume item : values()) {
            enmuMap.put(item.name, item);
        }
    }
    PlayDirectionEnume(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public int getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    public static int getByCode(String  name) {
        return enmuMap.get(name)!=null? Objects.requireNonNull(enmuMap.get(name)).code:SCREEN_UNKNOWN.code;
    }


}
