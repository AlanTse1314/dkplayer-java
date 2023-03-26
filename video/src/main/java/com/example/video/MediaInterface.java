package com.example.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.example.video.util.LogUtility;
import com.example.video.util.UrlUtil;

import java.util.Map;

/**
 * Created by Nathen on 2017/11/7.
 * 自定义播放器
 */
public abstract class MediaInterface implements TextureView.SurfaceTextureListener {
    protected Video play;
    public SurfaceTexture SAVED_SURFACE;
    public HandlerThread mMediaHandlerThread;
    public Handler mMediaHandler;
    public Handler handler;
    private static final String TAG = "MediaInterface";
    protected static final String THREAD_NAME = "SY_PLAY_VD";
    public long currentPosition = 0;
    public static int MEDIACODEC = 2;//默认硬解码
    public static boolean staticTrack = true;
    public static int VIDEO_TYPE;
    public static final int VIDEO_M3U8 = 1;
    public static final int VIDEO_RTMP = 2;
    public static final int VIDEO_UNKNOWN = 3;
    public static final int VIDEO_MPD = 4;
    public static final int VIDEO_FLV = 5;


    public static boolean ijkALLOW_DROPPED_FRAMES = true;

    public boolean liveStreaming = false;
    protected DataSource syDataSource;

    public MediaInterface(Video play) {
        UP(play);
    }

    public void UP(Video play) {
        this.play = play;
        syDataSource = play.getSyDataSource();
    }

    //开始播放
    public abstract void start();



    //准备状态
    public void prepare() {
        ((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE)).requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public Context getApplicationContext() {//这个函数必要吗
        Context context = play.getContext();
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                return applicationContext;
            }
        }
        return context;
    }

    public Map<String, String> getAgreement() {
        return syDataSource.headerMap;
    }

    public String getUserAgent(){
        return syDataSource.headerMap.get("User-Agent");
    }

    //暂停状态
    public void pause() {

    }

    public final Context getContext() {
        return play.getContext();
    }

    public final Surface getSurface() {
        return new Surface(getSurfaceTexture());
    }

    public final SurfaceTexture getSurfaceTexture() {
        return play.textureView.getSurfaceTexture();
    }

    public final TextureView getTextureView() {
        return play.textureView;
    }

    public abstract boolean isPlaying();


    //此方法这样写主要是为了排查问题源
    protected void seekTo(long position) {
        LogUtility.d(TAG, "seekTo 跳转进度: " + position);
    }

    public synchronized void release() {
        LogUtility.d(TAG, "内核被销毁:" + this.getClass().getName());
        ((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE)).abandonAudioFocus(null);

    }

    public synchronized void initSurface(SurfaceTexture surface) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface;
            prepare();
        } else {
            play.textureView.setSurfaceTexture(SAVED_SURFACE);
        }
    }

    public final Uri getUri() {
        String playUrl = UrlUtil.GetRealUrl(syDataSource.getCurrentUrl());
        String url = playUrl;
//        //content://com.android.fileexplorer.myprovider/external_files/Pictures/WeiXin/mmexport1657693599332.mp4
        if (!url.startsWith("/") && !url.startsWith("file") && !url.startsWith("content:")) {
            if (url.startsWith("rtmp") || url.startsWith("rtsp")) {
                VIDEO_TYPE = VIDEO_RTMP;

            } else if (url.contains("&sytype=.m3u8")) {
                url = url.replace("&sytype=.m3u8", "");
                VIDEO_TYPE = VIDEO_M3U8;
            } else if (url.contains(".m3u8")) {
                VIDEO_TYPE = VIDEO_M3U8;
            } else if (url.contains(".flv")) {
                VIDEO_TYPE = VIDEO_FLV;
            } else if (url.contains(".mpd")) {
                VIDEO_TYPE = VIDEO_MPD;
            } else {
                VIDEO_TYPE = VIDEO_UNKNOWN;
            }
        }
        return Uri.parse(playUrl);
    }


    public abstract long getCurrentPosition();

    public abstract long getCurrentPositionRealTime();

    public abstract long getSelectedTrack(int type);

    public abstract long getDuration();

    public abstract void setVolume(float leftVolume, float rightVolume);

    public abstract void setSpeed(float speed);

    public abstract void setSurface(Surface surface);

    public abstract boolean selectMetaTrack(int type, int track);


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {

        initSurface(surface);
    }


    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }


    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
    }

    /**
     * 内核是否有效
     */
    public abstract boolean isValidKernel();

    public boolean isCanSeekTo() {
       return getDuration()>0;

    }


    /**
     * 判断是否是直播
     */
    public boolean isLive() {
        return false;
    }
}