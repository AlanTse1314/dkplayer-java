package xyz.doikki.videoplayer.sniffer;

import android.net.Uri;

import xyz.doikki.videoplayer.sniffer.http.OkHttpUtil;
import xyz.doikki.videoplayer.util.Base64Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class CurrencyUtil {
    private static Pattern PATTERN;
    private final static String HUYA_STREAM = "\"stream\":";
    private final static String HUYA_LIVELINEURL = "\"liveLineUrl\":";

    static {
        String expression = "(href=|src=|\"url\":|'url':|url:|" + HUYA_STREAM + "|" + HUYA_LIVELINEURL + ")(\"|')(([^\"]*)(\\.mp4|\\.m3u8|\\.flv)*)(\"|')";
        PATTERN = Pattern.compile(expression, 2 | Pattern.DOTALL);
    }

    public static String senGet(Uri uri, Map<String, String> hashMap) {
        String url = uri.toString();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            Response response = OkHttpUtil.createResponse(uri, hashMap);
            int code = response.code();
            if (code == 200 || code == 206) {
                String type = response.header("content-type");
                //从协议中检索是否是一个视频连接
                String newUrl = identifyFromProtocol(type, url);
                if (newUrl != null) {
                    response.close();
                    return newUrl;
                }
                inputStream = response.body().byteStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    if (str.contains("window.__NEPTUNE_IS_MY_WAIFU__")) {
                        int startLenth = str.indexOf("window.__NEPTUNE_IS_MY_WAIFU__");
                        String origin = str.substring(startLenth, str.indexOf("</script>", startLenth));
                        origin = origin.substring(origin.indexOf("=") + 1).trim();
                        JSONObject jsonMain = new JSONObject(origin);
                        JSONArray stream = jsonMain.getJSONObject("roomInitRes")
                                .getJSONObject("data")
                                .getJSONObject("playurl_info")
                                .getJSONObject("playurl")
                                .getJSONArray("stream");
                        JSONArray format = ((JSONObject) stream.get(0)).getJSONArray("format");
                        JSONArray codec = ((JSONObject) format.get(0)).getJSONArray("codec");
                        JSONObject codecItem = (JSONObject) codec.get(0);
                        JSONObject urlInfo = (JSONObject) codecItem.getJSONArray("url_info").get(0);
                        String baseUrl = codecItem.getString("base_url");
                        String host = urlInfo.getString("host");
                        String extra = urlInfo.getString("extra");
                        String streamTtl = urlInfo.getLong("stream_ttl") + "";
                        String playUrl = host + baseUrl + extra;
                        return playUrl;
                    }

                    Matcher m = PATTERN.matcher(str.replace(" ", ""));
                    if (m.find()) {
                        String address = m.group(3).replace("\\", "");
                        if (m.group(1).equals(HUYA_LIVELINEURL)) {
                            String hnfGlobalInit = str.substring(str.indexOf("{"));
                            hnfGlobalInit = hnfGlobalInit.substring(0, hnfGlobalInit.lastIndexOf("}") + 1);
                            JSONObject jsonMain = new JSONObject(hnfGlobalInit);
                            JSONObject tLiveStreamInfo = jsonMain
                                    .getJSONObject("roomInfo")
                                    .getJSONObject("tLiveInfo")
                                    .getJSONObject("tLiveStreamInfo")
                                    .getJSONObject("vStreamInfo")
                                    .getJSONArray("value")
                                    .getJSONObject(0);
                            String sStreamName = tLiveStreamInfo.getString("sStreamName");
                            String sFlvUrl = tLiveStreamInfo.getString("sFlvUrl");
                            String sFlvUrlSuffix = tLiveStreamInfo.getString("sFlvUrlSuffix");
                            String sFlvAntiCode = tLiveStreamInfo.getString("sFlvAntiCode");
                            String flvPlay = String.format("%s/%s.%s?%s", sFlvUrl, sStreamName, sFlvUrlSuffix, sFlvAntiCode);
                            return flvPlay;
                        } else if (m.group(1).equals(HUYA_STREAM)) {
                            //虎牙PC端
                            String ba64 = new String(Base64Util.decode(address));
                            JSONObject jsonMain = new JSONObject(ba64);
                            JSONObject jsonObject = (JSONObject) jsonMain.getJSONArray("data").get(0);
                            JSONObject gameStreamInfo = (JSONObject) jsonObject.getJSONArray("gameStreamInfoList").get(0);
                            String sStreamName = gameStreamInfo.getString("sStreamName");
                            String sFlvUrl = gameStreamInfo.getString("sFlvUrl");
                            String sFlvUrlSuffix = gameStreamInfo.getString("sFlvUrlSuffix");
                            String sFlvAntiCode = gameStreamInfo.getString("sFlvAntiCode");
                            String sHlsUrl = gameStreamInfo.getString("sHlsUrl");
                            String sHlsUrlSuffix = gameStreamInfo.getString("sHlsUrlSuffix");
                            String sHlsAntiCode = gameStreamInfo.getString("sHlsAntiCode");
                            String flvPlay = String.format("%s/%s.%s?%s", sFlvUrl, sStreamName, sFlvUrlSuffix, sFlvAntiCode);
                            String m3u8Play = String.format("%s/%s.%s?%s", sHlsUrl, sStreamName, sHlsUrlSuffix, sHlsAntiCode);
                            return flvPlay;
                        } else {
                            if (str.startsWith("{\"type\":\"tv\",\"data\":")) {
                                JSONObject jsonMain = new JSONObject(str);
                                JSONObject jsonObject = (JSONObject) jsonMain.getJSONArray("data").get(0);
                                JSONObject source = jsonObject.getJSONObject("source");
                                JSONArray jsonArray = source.getJSONArray("eps");
                                String ep = jsonMain.getString("ep");
                                JSONObject episode = (JSONObject) jsonArray.get(Integer.parseInt(ep) - 1);
                                return getUniqueUrl(episode.getString("url"));
                            }
                            if (!"".equals(address)) {
                                if (address.startsWith("//")) {
                                    address = (url.startsWith("https") ? "https:" : "http:") + address;
                                }
                                return getUniqueUrl(address);

                            }
                        }
                    }

                }
            } else if (code == 302) {
                String location = response.header("location");
                return senGet(Uri.parse(location), hashMap);
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

    /**
     * 是视频协议
     */
    private static String identifyFromProtocol(String type, String url) {
        if ("video/mp4".equals(type)) {
            return url.contains(".mp4") ? url : url + "&sytype=.mp4";
        }
        if ("application/vnd.apple.mpegurl".equals(type)) {
            return url.contains(".m3u8") ? url : url + "&sytype=.m3u8";
        }
        return null;
    }

    /**
     * 嗅探播放地址取最终的地址
     */
    public static String getUniqueUrl(String url) {
        url = url.replace("u0026", "&");
        if (isVideo(url)) {
            String[] urlArr = url.split("http");
            for (int i = 0; i < urlArr.length; i++) {
                String value = urlArr[i];
                if (!"".equals(value) && isVideo("http" + value)) {
                    if (i == 1 && url.startsWith("http")) {
                        return url;
                    } else {
                        return "http" + value;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isVideo(String url) {
        return (url.contains(".mp4") || url.contains(".m3u8") || url.contains(".flv"));
    }

}
