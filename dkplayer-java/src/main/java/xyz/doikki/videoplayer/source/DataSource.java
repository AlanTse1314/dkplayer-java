package xyz.doikki.videoplayer.source;


import xyz.doikki.videoplayer.util.Md5Util;
import xyz.doikki.videoplayer.util.ObjectUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class DataSource {
    public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";
    public int currentUrlIndex;
    public LinkedHashMap urlsMap = new LinkedHashMap();
    public String title = "";
    public Map<String, String> headerMap=new HashMap<>();
    public static boolean IS_PC = false;
    public static final String UA_ANDROID = "Mozilla/5.0 (Linux; Android 10; Redmi K20 Pro Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.101 Mobile Safari/537.36";
    public static final String UA_PC = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/%s Chrome/30.0.1551.0 Safari/537.36";
    public boolean looping = false;
    public boolean mLocalProxyEnable;

    private String xymd5 = null;
    private String subtitle = null;
    public boolean sniffing;
    //是否直播
    public boolean liveStreaming;
    public int timeOut;
    public String sniffingUrl;

    /**
     * @param mLocalProxyEnabl 启用本地代理
     * @param sniffing         是否使用系统
     * @param timeOut          嗅探超时时间  为 秒
     * @param headerMap        协议
     * @param liveStreaming    是否是直播
     */
    public DataSource(String url, String title,
                      boolean mLocalProxyEnabl,
                      Map<String, String> headerMap,
                      boolean liveStreaming,
                      boolean sniffing, int timeOut) {
        if (sniffing) {
            this.sniffingUrl = url;
            setUrl(null);
        } else {
            setUrl(url);
        }
        this.title = title;
        this.currentUrlIndex = 0;
        setHeaderMap(headerMap);
        this.mLocalProxyEnable = mLocalProxyEnabl;
        this.liveStreaming = liveStreaming;
        this.timeOut = timeOut;
        this.sniffing = sniffing;
    }

    public void release() {
        urlsMap.clear();
        title = "";
    }

    public void setUrl(String url) {
        if (url != null) {
            xymd5 = Md5Util.strToMD5(url.replace("&sytype=.m3u8", "").replace("&sytype=.mp4", ""));
        } else {
            xymd5 = null;
        }
        urlsMap.put(URL_KEY_DEFAULT, url);
    }

    public String getXymd5() {
        return xymd5;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap.clear();
        if (headerMap == null) {
            this.headerMap.put("User-Agent", IS_PC ? UA_PC : UA_ANDROID);
        } else {
            String userAgent=headerMap.get("user-agent")!=null?headerMap.get("user-agent"):headerMap.get("User-Agent");
            headerMap.remove("user-agent");
            headerMap.remove("User-Agent");
            this.headerMap.put("User-Agent", ObjectUtil.notNull(userAgent)?userAgent:(IS_PC ? UA_PC : UA_ANDROID));
            this.headerMap.putAll(headerMap);
        }
    }

    public String getCurrentUrl() {
        return (String) getValueFromLinkedMap(currentUrlIndex);
    }

    public String getCurrentKey() {
        return getKeyFromDataSource(currentUrlIndex);
    }

    public String getCurrentValue() {
        return getKeyFromDataSource(currentUrlIndex);
    }

    public String getKeyFromDataSource(int index) {
        int currentIndex = 0;
        for (Object key : urlsMap.keySet()) {
            if (currentIndex == index) {
                return key.toString();
            }
            currentIndex++;
        }
        return null;
    }

    public Object getValueFromLinkedMap(int index) {
        int currentIndex = 0;
        for (Object key : urlsMap.keySet()) {
            if (currentIndex == index) {
                return urlsMap.get(key);
            }
            currentIndex++;
        }
        return null;
    }

    public boolean containsTheUrl(Object object) {
        if (object != null) {
            return urlsMap.containsValue(object);
        }
        return false;
    }


}
