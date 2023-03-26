package com.example.video.util;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public final class AssetsUtil {
    private static final String ADB_FILE_NAME = "adb.json";

    /**
     * 获取广告拦截json配置文件
     */
    public static JSONObject getAdbJson() {
        try (InputStream in = ContextUtil.getsContext().getAssets().open(ADB_FILE_NAME);) {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024];
            while (in.read(buffer, 0, buffer.length) != -1) {//-1表示读取结束
                sb.append(new String(buffer));
            }
            return new JSONObject(sb.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
