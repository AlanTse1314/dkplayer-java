package com.example.sniffer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.video.util.AssetsUtil;
import com.example.video.util.ContextUtil;
import com.example.video.util.LogUtility;
import com.example.video.util.ObjectUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class VideoSniffer {
    private static final String TAG = "WebView";

    private static VideoSniffer videoSniffer;
    private WebView webView;
    private MyWebViewClient myWebViewClient = new MyWebViewClient();
    //这是一个非重复无序的集合  ,存放捕获关键词
    private static final Set<String> KEY_SET = new HashSet<>();
    //请求拦截
    private static final Set<String> REQUEST_URL = new HashSet<>();
    //跳转拦截
    private static final Set<String> JUMP_URL = new HashSet<>();
    //定义一个空白页
    private static final WebResourceResponse WEB_RESOURCE_RESPONSE = new WebResourceResponse("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9", "utf-8", new ByteArrayInputStream("".getBytes()));
    private SnifferListener snifferListener;
    private HandlerThread handlerThread;
    private Handler webHandler;
    private String title = "";

    public synchronized static VideoSniffer getInstance(@NonNull final SnifferListener snifferListener) {
        if (videoSniffer == null) {
            new VideoSniffer();
        }
        videoSniffer.snifferListener = snifferListener;
        KEY_SET.clear();
        return videoSniffer;
    }


    private VideoSniffer() {
        //创建主线程 ,确保所有操作都在吃线程操作,否则异常
        handlerThread = new HandlerThread("VideoSniffer");
        handlerThread.start();
        //主线程还是非主线程，就在这里
        webHandler = new Handler(handlerThread.getLooper());
        videoSniffer = this;
        //初始化广告配置
        try {
            JSONObject jsonObject = AssetsUtil.getAdbJson();
            if (ObjectUtil.notNull(jsonObject)) {
                JSONArray reqArray = jsonObject.getJSONArray("REQUEST_URL");
                if (ObjectUtil.notNull(reqArray)) {
                    for (int i = 0; i < reqArray.length(); i++) {
                        REQUEST_URL.add(reqArray.getString(i));
                    }
                }
                JSONArray jumpArray = jsonObject.getJSONArray("JUMP_URL");
                if (ObjectUtil.notNull(jumpArray)) {
                    for (int i = 0; i < jumpArray.length(); i++) {
                        JUMP_URL.add(jumpArray.getString(i));
                    }
                }
            } else {
                LogUtility.d(TAG, "读取默认广告配置失败!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //初始化web配置
        webHandler.post(this::init);
    }

    /**
     * 添加捕获关键词
     */
    public VideoSniffer addKeyWord(String key) {
        KEY_SET.add(key);
        return this;
    }


    /**
     * 判断是否是一个请求广告
     */
    private static boolean isRequestUrl(String url) {
        if (REQUEST_URL.contains(url)) {
            return true;
        } else {
            for (String keyWords : REQUEST_URL) {
                if (url.contains(keyWords)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是一个请求广告
     */
    private static boolean isRequestUrl(Uri uri) {
        return isRequestUrl(uri.toString());
    }


    /**
     * 判断是否是跳转广告
     */
    private static boolean isJumpAD(String url) {
        if (JUMP_URL.contains(url)) {
            return true;
        } else {
            String host = (Uri.parse(url).getHost());
            for (String keyWords : JUMP_URL) {
                if (host.equals(keyWords)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 欲解析的URL
     *
     * @param timeout 超时时间  单位 为秒
     */
    public void parseUrl(String url, int timeout) {
        webHandler.post(() -> {
            loadable = true;
            webView.stopLoading();
            webView.loadUrl(url);
        });
        webHandler.postDelayed(() -> {
            //如果已经嗅探成功了 则 不响应超时
            if (loadable) {
                stopLoading();
                snifferListener.onTimeout(url);
            }
        }, timeout * 1000);
    }

    @SuppressLint("JavascriptInterface")
    private void init() {
        webView = new WebView(ContextUtil.getsContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);// 必须保留，否则无法播放优酷视频，其他的OK
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//设置缓存
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/%s Chrome/30.0.1551.0 Safari/537.36");
        try {
            Class<?> clazz = webView.getSettings().getClass();
            Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(webView.getSettings(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(myWebViewClient);
        webView.setWebChromeClient(new MyWebChromeClient());


    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // 如果证书一致，忽略错误
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        }

        /**
         * 跳转拦截
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith("http") || isJumpAD(url)) {
                return true;
            }
            loadurlLocalMethod(view, url);
            return false;
        }

        //安卓4.4拦截方法
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (!android44) {
                //只有安卓4.4才会走这方法
                return null;
            }
            WebResourceResponse webResourceResponse = retrievalRequest(Uri.parse(url), null);
            return webResourceResponse == null ? super.shouldInterceptRequest(view, url) : webResourceResponse;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest response) {
            android44 = false;//不是安卓4.4  防止数据多次加载
            WebResourceResponse webResourceResponse = retrievalRequest(response.getUrl(), response.getRequestHeaders());
            return webResourceResponse == null ? super.shouldInterceptRequest(view, response) : webResourceResponse;
        }

        /**
         * 本地加载方式
         */
        public void loadurlLocalMethod(final WebView webView, final String url) {
            new Thread(() -> webView.loadUrl(url));
        }
    }

    boolean android44 = true;
    boolean loadable = false;

    private WebResourceResponse retrievalRequest(Uri uri, Map<String, String> hashMap) {
        synchronized (snifferListener) {
            if (!loadable) {
                stopLoading();
                return WEB_RESOURCE_RESPONSE;
            }
            if (!isHttp(uri.toString()) || isRequestUrl(uri)) {
                //返回一个空白页面
                return WEB_RESOURCE_RESPONSE;
            }
            if (isContainKey(uri)) {
                //嗅探到播放地址
                loadable = false;
                stopLoading();
                snifferListener.onCaptureKey(uri.toString(), VideoSniffer.this.title, hashMap);
                //禁止浏览器继续加载
                return WEB_RESOURCE_RESPONSE;
            }
            if (uri.getHost().contains("api.bilibili.com")) {
                String bibiUrl = BIBIUtil.senGet(uri, hashMap);
                if (ObjectUtil.notNull(bibiUrl)) {
                    loadable = false;
                    stopLoading();
                    snifferListener.onCaptureKey(bibiUrl, VideoSniffer.this.title, hashMap);
                    return WEB_RESOURCE_RESPONSE;
                }
            } else {
                String videoUrl = CurrencyUtil.senGet(uri, hashMap);
                if (isContainKey(videoUrl)) {
                    loadable = false;
                    stopLoading();
                    snifferListener.onCaptureKey(videoUrl, VideoSniffer.this.title, hashMap);
                    return WEB_RESOURCE_RESPONSE;
                }
            }
        }
        return null;
    }

    /**
     * 停止嗅探
     */
    public void stopLoading() {
        webHandler.post(() -> webView.stopLoading());
    }

    /**
     * 判断连接是否是一个正常的连接
     */
    private static boolean isHttp(String url) {
        return url.startsWith("http");
    }

    /**
     * 判断地址是否包含 关键词
     */
    private static boolean isContainKey(String str) {
        if (ObjectUtil.notNull(str)) {
            for (String s : KEY_SET) {
                if (str.contains(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isContainKey(Uri uri) {
        return isContainKey(uri.toString());
    }


    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //禁止弹窗
            return true;
        }


        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }

        /**
         * 浏览器进度发生改变
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            LogUtility.d(TAG, "onProgressChanged", "进度:" + newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            LogUtility.d(TAG, "onReceivedTitle", "标题:" + title);
            if (!title.equals("")) {

                VideoSniffer.this.title = title;
            }
        }

    }

    public interface SnifferListener {

        void onCaptureKey(@NonNull String url, @Nullable String title, @Nullable Map<String, String> hashMap);

        void onTimeout(String url);

    }

}
