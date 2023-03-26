package com.example.alanlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.alan.banner.Banner;
//import com.alan.banner.Banner;
//import com.alan.banner.bean.BannerBean;
//import com.alan.banner.listener.OnBannerClickListener;
import com.bumptech.glide.Glide;
//import com.example.video.DataSource;
//import com.example.video.MediaExo;
//import com.example.video.VideoPlayer;
//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//
//
//import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

//import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.source.DataSource;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.player);
        EditText input = findViewById(R.id.input);


//        videoView.setUrl("https://media.w3.org/2010/05/sintel/trailer.mp4"); //设置视频地址
//        StandardVideoController controller = new StandardVideoController(this);
////        c controller = new c(this);
//        controller.addDefaultControlComponent("标题", false);
//        videoView.setVideoController(controller); //设置控制器
//        videoView.start(); //开始播放，不调用则不自动播放


        Button button = findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input.getText().toString().isEmpty()){
                    videoView.setUp(new DataSource(input.getText().toString(), "标题",
                            true, null, false,true,8));
                    videoView.start();
                }else {
                    videoView.setUp(new DataSource("https://www.bilibili.com/video/BV1GY4y177r9?spm_id_from=333.851.b_7265636f6d6d656e64.2", "标题",
                            true, null, false,true,8));
                    videoView.start();
                }
            }
        });

        // 屏蔽HtmlUnit等系统 log
//        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
//        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
//        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
//
//        String url = "https://www.360kan.com/";
//        System.out.println("Loading page now-----------------------------------------------: "+url);
//
//        // HtmlUnit 模拟浏览器
//        WebClient webClient = new WebClient(BrowserVersion.CHROME);
//        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
//        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
//        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
//        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//        webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间
//        HtmlPage page = null;
//        try {
//            page = webClient.getPage(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        webClient.waitForBackgroundJavaScript(30 * 1000);               // 等待js后台执行30秒
//
//        String pageAsXml = page.asXml();
//        Log.e("pageAsXml",pageAsXml);
        // Jsoup解析处理
//        Document doc = Jsoup.parse(pageAsXml, "https://www.360kan.com/");
//        Log.e("doc",doc.toString());
//        Elements pngs = doc.select("img[src$=.png]");                   // 获取所有图片元素集
//        // 此处省略其他操作
//        System.out.println(doc.toString());








//        Banner banner = findViewById(R.id.banner);
//        banner.setOnBannerListener(new OnBannerClickListener() {
//
//            /**
//             * 点击事件
//             *
//             * @param bannerBean 数据实体
//             * @param position   当前位置
//             */
//            @Override
//            public void OnBannerClick(BannerBean bannerBean, int position) {
//                Toast.makeText(MainActivity.this, bannerBean.getTitle()+position, Toast.LENGTH_SHORT).show();
//            }
//        });
//        Banner banners = findViewById(R.id.banners);
//        banners.setOnBannerListener(new OnBannerListener() {
//            @Override
//            public void OnBannerClick(Object data, int position) {
//
//            }
//        });

//        List<BannerBean> listBanner = new ArrayList();
////        for(int i=0;i<10;i++){
//            BannerBean bb = new BannerBean();
//            bb.setImg("https://pic0.iqiyipic.com/image/20220210/65/f1/v_165726475_m_601_m3_480_270.jpg");
//            bb.setLink("https://www.iqiyi.com/v_t1r47dqqts.html");
//            bb.setTitle("老九门之青山海棠");
//            listBanner.add(bb);
////        }
//
//        BannerBean bb0 = new BannerBean();
//        bb0.setImg("https://pic9.iqiyipic.com/image/20230203/06/a7/v_151351479_m_601_m16_480_270.jpg");
//        bb0.setLink("https://www.iqiyi.com/v_t1r47dqqts.html");
//        bb0.setTitle("扫黑·决战");
//        listBanner.add(bb0);
//
//        BannerBean bb1 = new BannerBean();
//        bb1.setImg("https://pic4.iqiyipic.com/image/20230313/0f/4a/a_100459089_m_601_m20_480_270.jpg");
//        bb1.setLink("https://www.iqiyi.com/v_t1r47dqqts.html");
//        bb1.setTitle("九霄寒夜暖");
//        listBanner.add(bb1);
//
//        BannerBean bb2 = new BannerBean();
//        bb2.setImg("https://pic9.iqiyipic.com/image/20230315/a0/9a/v_171591295_m_601_m1_480_270.jpg");
//        bb2.setLink("https://www.iqiyi.com/v_t1r47dqqts.html");
//        bb2.setTitle("清明节放假");
//        listBanner.add(bb2);
//
//        BannerBean bb3 = new BannerBean();
//        bb3.setImg("https://pic0.iqiyipic.com/lequ/common/lego/20230313/2741353a2d974ebea3b14c0180491a85.jpg");
//        bb3.setLink("https://www.iqiyi.com/v_t1r47dqqts.html");
//        bb3.setTitle("回响·定档0316");
//        listBanner.add(bb3);
//
//        BannerBean bb4 = new BannerBean();
//        bb4.setImg("https://pic3.iqiyipic.com/lequ/common/lego/20230314/5a099f2b7b774d3c9814b2b09544060b.jpg");
//        bb4.setLink("https://www.iqiyi.com/v_t1r47dqqts.html");
//        bb4.setTitle("忠犬八公·院线定档");
//        listBanner.add(bb4);
//
//
//
//
//        banner.setBannerData(listBanner);

        ImageView iv = findViewById(R.id.image);
        Glide.with(this).load("https://pic3.iqiyipic.com/lequ/common/lego/20230314/5a099f2b7b774d3c9814b2b09544060b.jpg").into(iv);
        LinearLayout layout = findViewById(R.id.layout);
        for (int i=0;i<5;i++){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageView ivs = new ImageView(this);
            Glide.with(this).load("https://pic3.iqiyipic.com/lequ/common/lego/20230314/5a099f2b7b774d3c9814b2b09544060b.jpg").into(ivs);
            layout.addView(ivs);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        videoView.release();
    }


    @Override
    public void onBackPressed() {
//        if (!videoView.onBackPressed()) {
//            super.onBackPressed();
//        }
    }

}