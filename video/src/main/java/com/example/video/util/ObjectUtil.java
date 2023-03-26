package com.example.video.util;

public class ObjectUtil {

    /**
     * 判断对象不为空  为空返回false
     */
    public static boolean notNull(Object obj) {
        if(obj==null){
            return false;
        }
        if(obj instanceof  String){
            String str= (String) obj;
            return !"".equals(str.trim());
        }
        return true;
    }
    /**
     * 判断是否为空  为空返回true
     */
    public static boolean isNull(Object obj) {
        if(obj==null){
            return true;
        }
        if(obj instanceof  String){
            String str= (String) obj;
            return "".equals(str.trim());
        }
        return false;
    }


}
