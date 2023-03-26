package xyz.doikki.videoplayer.sniffer.http;


import android.net.Uri;

import xyz.doikki.videoplayer.sniffer.http.ssl.SSLSocketFactoryCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class OkHttpUtil {
    private static SSLSocketFactory sslSocketFactory;
    private static X509TrustManager trustManager;
    private static HostnameVerifier hostnameVerifier;

    static {
        hostnameVerifier = (s, sslSession) -> true;
        trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
        sslSocketFactory = new SSLSocketFactoryCompat(trustManager);

    }

    public static SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public static X509TrustManager getTrustManager() {
        return trustManager;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }


    public static Response createResponse(String url, Map<String, String> hashMap) throws IOException {
        // 创建okHttpClient实例，忽略https证书验证
        OkHttpClient client = new OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(hostnameVerifier)
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .build();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if(hashMap!=null && hashMap.size()>0) {
            for (String key : hashMap.keySet()) {
                builder.addHeader(key, hashMap.get(key));
            }
        }
        builder.addHeader("Connection", "close");
        Request request = builder.build();
        Response httpResponse = client.newCall(request).execute();

        return httpResponse;
    }
    public static Response createResponse(Uri uri, Map<String, String> hashMap) throws IOException {
        return createResponse(uri.toString(), hashMap);
    }

    //是否是一个有效的劫持对象
    private static boolean valid(String url) {
        if (url.contains(".html")) {
            return true;
        }
        //把域名除开看看有没有小数点,如果没有则允许劫持响应(这样做的目的是防止劫持到post请求或文件类型引起误伤)
        //可能有点不严谨
        String host = Uri.parse(url).getHost();
        return !url.replace(host, "").contains(".");
    }

    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        if (!valid(url)) {
            return null;
        }
        URL connURL = new URL(url);
        HttpURLConnection httpConn = (HttpURLConnection) connURL.openConnection();
        httpConn.setConnectTimeout(8000);
        httpConn.setReadTimeout(8000);
        if (url.startsWith("https")) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
            httpsConn.setSSLSocketFactory(sslSocketFactory);
            httpsConn.setHostnameVerifier(hostnameVerifier);
        }
        return httpConn;
    }

}
