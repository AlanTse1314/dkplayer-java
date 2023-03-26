package com.example.video.util;

import static android.text.TextUtils.isEmpty;

import java.util.Formatter;
import java.util.Locale;

public class StringUtils {

    /**
     * 时间到文本
     */
    public static String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    public static String subString(String str, String prefix, String suffix) {
        if (isEmpty(str) || isEmpty(prefix) || isEmpty(suffix)) {
            return "";
        }
        int length = prefix.length();
        int start = str.indexOf(prefix);
        if (start == -1) {
            return "";
        }
        int end = str.indexOf(suffix, start + length);
        if (end == -1) {
            return "";
        }
        return str.substring(start + length, end);
    }

}
