package com.example.video.enume;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 */

public enum ErrorEnume {
    URL_NULL(0, "无效地址"),
    SNIFFING_FAILED(2, "解析失败");
    private Integer code;
    private String name;
    private static final Map<Integer, ErrorEnume> enmuMap = new HashMap<>();

    static {
        for (ErrorEnume item : values()) {
            enmuMap.put(item.getCode(), item);
        }
    }

    ErrorEnume(Integer code, String name) {
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
        return enmuMap.get(code) != null ? enmuMap.get(code).getName() : "未知";
    }

    public static ErrorEnume getType(int code) {
        return enmuMap.get(code) != null ? enmuMap.get(code) : null;
    }

}
