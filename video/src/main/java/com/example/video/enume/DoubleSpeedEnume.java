package com.example.video.enume;

import java.util.HashMap;
import java.util.Map;

public enum DoubleSpeedEnume {
    SPEED_X1(1.0f, "1x"),
    SPEED_X0_5(0.5f, "0.5x"),
    SPEED_X1_2(1.2f, "1.2x"),
    SPEED_X1_5(1.5f, "1.5x"),
    SPEED_X2(2.0f, "2x"),
    SPEED_X3(3.0f, "3x");
    private Float code;
    private String name;
    private static final Map<Float, DoubleSpeedEnume> enmuMap = new HashMap<>();

    static {
        for (DoubleSpeedEnume item : values()) {
            enmuMap.put(item.getCode(), item);
        }
    }

    DoubleSpeedEnume(Float code, String name) {
        this.code = code;
        this.name = name;
    }

    public Float getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(Float code) {
        return enmuMap.get(code) != null ? enmuMap.get(code).getName() : "未知";
    }

    public static DoubleSpeedEnume getType(Float code) {
        return enmuMap.get(code) != null ? enmuMap.get(code) : null;
    }


}
