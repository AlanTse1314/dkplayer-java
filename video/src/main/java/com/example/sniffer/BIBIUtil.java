package com.example.sniffer;


import android.net.Uri;

import com.example.sniffer.http.OkHttpUtil;
import com.example.video.util.LogUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import okhttp3.Response;

public class BIBIUtil {
    private static final String TAG = "BIBIUtil";

    public static String senGet(Uri uri, Map<String, String> hashMap) {
        String url = uri.toString();
        if (url.contains("cid=") && url.contains("aid=") && !url.contains("?avid=")) {
            String aid = url.substring(url.indexOf("aid=") + 4, url.indexOf("&cid"));
            String cid = url.substring(url.indexOf("&cid=") + 5);
            if (cid.contains("&")) {
                cid = cid.substring(0, cid.indexOf("&"));
            }
            String apiUrl = String.format("https://api.bilibili.com/x/player/playurl?avid=%s&cid=%s&qn=1&type=&otype=json&platform=html5&high_quality=1", aid, cid);
            return getPlayUrl(Uri.parse(apiUrl), hashMap);
        }
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            Response response = OkHttpUtil.createResponse(uri, hashMap);
            int code = response.code();
            if (code == 200) {
                inputStream = response.body().byteStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    if (str.contains("window.__INITIAL_STATE__")) {
                        int startLenth = str.indexOf("window.__INITIAL_STATE__");
                        String origin = str.substring(startLenth, str.indexOf("</script>", startLenth));
                        origin = origin.substring(origin.indexOf("=") + 1).trim();
                        JSONObject jsonMain = new JSONObject(origin);
                        JSONObject videoData = jsonMain.getJSONObject("videoData");
                        String aid = videoData.getLong("aid") + "";
                        String cid = videoData.getLong("cid") + "";
                        String apiUrl = String.format("https://api.bilibili.com/x/player/playurl?avid=%s&cid=%s&qn=1&type=&otype=json&platform=html5&high_quality=1", aid, cid);
                        return getPlayUrl(Uri.parse(apiUrl), hashMap);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
    private static String getPlayUrl(Uri uri,Map<String, String> hashMap){
        try {
            Response response = OkHttpUtil.createResponse(uri, hashMap);
            int code = response.code();
            if (code == 200) {
                JSONObject jsonObject=new JSONObject(response.body().string());
                JSONObject data=jsonObject.getJSONObject("data");
                JSONArray durlArr=data.getJSONArray("durl");
                if(durlArr.length()>0){
                    JSONObject urlJsonObj=durlArr.getJSONObject(0);
                    String url=urlJsonObj.getString("url");
                    LogUtility.d(TAG,url);
                    return url;
                }
            }
            response.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
