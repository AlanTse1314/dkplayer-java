
package com.example.video.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

    private static String regxChinese(String str)  {
        try {
            //对参数进行编码
            //对非参中文进行编码
            String regex = "([\u4e00-\u9fa5])";
            Matcher matcher = Pattern.compile(regex).matcher(str);
            while (matcher.find()) {
                str = str.replace(Objects.requireNonNull(matcher.group(0)), URLEncoder.encode(matcher.group(0), "UTF-8"));
            }
            return str;
        }catch (Exception e){

        }
        return str;
    }


    //对url中的参数进行url编码
    public static String GetRealUrl(String str) {
        try {
            int index = str.indexOf("?");
            if (index < 0) {
                return regxChinese(str);
            }
            String query = regxChinese(str.substring(0, index));
            String params = str.substring(index + 1);
            Map map = GetArgs(params);
            //Map map=TransStringToMap(params);
            String encodeParams = TransMapToString(map);
            return query + "?" + encodeParams;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return str;
    }

    //将url参数格式转化为map
    public static Map GetArgs(String params) throws Exception {
        Map map = new HashMap();
        String[] pairs = params.split("&");
        for (int i = 0; i < pairs.length; i++) {
            int pos = pairs[i].indexOf("=");
            if (pos == -1) {
                continue;
            }
            String argname = pairs[i].substring(0, pos);
            String value = pairs[i].substring(pos + 1);
            value = URLEncoder.encode(value, "utf-8").replace("%7E","~");
            map.put(argname, value);
        }
        return map;
    }

    //将map转化为指定的String类型
    public static String TransMapToString(Map map) {
        Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("=").append(null == entry.getValue() ? "" : entry.getValue().toString()).append(iterator.hasNext() ? "&" : "");
        }
        return sb.toString();
    }

    //将String类型按一定规则转换为Map
    public static Map TransStringToMap(String mapString) {
        Map map = new HashMap();
        StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, "&"); entrys.hasMoreTokens();
             map.put(items.nextToken(), items.hasMoreTokens() ? items.nextToken() : null)) {
            items = new StringTokenizer(entrys.nextToken(), "=");
        }
        return map;
    }

}
