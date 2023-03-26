package com.example.video;


import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import com.example.video.util.LogUtility;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

/**
 * Created by Nathen on 2017/11/18.
 */

public class MediaIjk extends MediaInterface implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnTimedTextListener, IjkMediaPlayer.OnNativeInvokeListener {
    private static final String TAG = "MediaIjk";
    private IjkMediaPlayer ijkMediaPlayer;
    private ITrackInfo[] iTrackInfos;

    public MediaIjk(Video play) {
        super(play);
    }

    @Override
    public void start() {
        if (ijkMediaPlayer != null) {
            LogUtility.d(TAG, "启动播放");
            ijkMediaPlayer.start();
        }
    }


    @Override
    public void prepare() {
        release();
        super.prepare();
        mMediaHandlerThread = new HandlerThread(THREAD_NAME);
        mMediaHandlerThread.start();
        //主线程还是非主线程，就在这里
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());
        handler = new Handler();
        mMediaHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", MEDIACODEC == 2 ? 1 : 0);
                    if (!getUri().toString().startsWith("rtsp")) {
                        //控制视频缓存
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 600);
                        // 跳过循环滤波
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);
                        // 设置最长分析时长
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
                        // 通过立即清理数据包来减少等待时长
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
                        if (ijkALLOW_DROPPED_FRAMES) {
                            // 网络不好的情况下进行丢包
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
                            //是否开启缓存，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验 关闭播放器缓冲
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);
                        }
                        //开启字幕
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "subtitle", 1);
                        // 等待开始之后才绘制
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "render-wait-start", 1);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", 842225234);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

                        //最大缓冲大小
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 60 * 1024 * 1024);
                        //最大缓存时长;
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 60000);
                        // 清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频), 有时会造成因为DNS的问题而报10000问题的
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
                        //如果是rtsp协议，可以优先用tcp(默认是用udp)
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video");
                        //设置seekTo能够快速seek到指定位置并播放
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 30);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 1024);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 10);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                    }
                    ijkMediaPlayer.setOnPreparedListener(MediaIjk.this);
                    ijkMediaPlayer.setOnVideoSizeChangedListener(MediaIjk.this);
                    ijkMediaPlayer.setOnCompletionListener(MediaIjk.this);
                    ijkMediaPlayer.setOnErrorListener(MediaIjk.this);
                    ijkMediaPlayer.setOnInfoListener(MediaIjk.this);
                    ijkMediaPlayer.setOnBufferingUpdateListener(MediaIjk.this);
                    ijkMediaPlayer.setOnSeekCompleteListener(MediaIjk.this);
                    ijkMediaPlayer.setOnTimedTextListener(MediaIjk.this);
//                    ijkMediaPlayer.setOnNativeInvokeListener(MediaIjk.this);
                    ijkMediaPlayer.setDataSource(getUri().toString(), getAgreement());
                    ijkMediaPlayer.setSurface(getSurface());
                    ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    ijkMediaPlayer.setScreenOnWhilePlaying(false);
                    ijkMediaPlayer.prepareAsync();
                    LogUtility.d(TAG, "内核_被初始化成功[" + this.hashCode() + "] ");
                } catch (Throwable e) {
                    LogUtility.e(TAG, e);
                }
            }
        });
    }


    // selectMetaTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO, sdAD);
    @Override
    public boolean selectMetaTrack(int type, int track) {
        IjkTrackInfo ijkTrackInfo;
        try {
            ijkTrackInfo = (IjkTrackInfo) iTrackInfos[track];
            if (ijkTrackInfo.getTrackType() != type) {
                return false;
            }
            if (getSelectedTrack(type) == track) {
                return true;
            } else {
                ijkMediaPlayer.selectTrack(track);
                return true;
            }
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public void pause() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.pause();
            super.pause();
        }

    }

    @Override
    public boolean isPlaying() {
        return ijkMediaPlayer != null && ijkMediaPlayer.isPlaying();

    }


    @Override
    protected void seekTo(long position) {
        super.seekTo(position);
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.seekTo(position);
            currentPosition = position;
        }

    }

    @Override
    public synchronized void release() {
        super.release();
        LogUtility.d(TAG, "检测内核可否释放[" + this.hashCode() + "]");
        if (mMediaHandler != null && mMediaHandlerThread != null && ijkMediaPlayer != null) {//不知道有没有妖孽
            currentPosition = ijkMediaPlayer.getCurrentPosition();
            LogUtility.d(TAG, "内核被释放[" + this.hashCode() + "]");
            final HandlerThread tmpHandlerThread = mMediaHandlerThread;
            final IjkMediaPlayer tmpMediaPlayer = ijkMediaPlayer;
            mMediaHandler.post(new Runnable() {
                @Override
                public void run() {
                    tmpMediaPlayer.setSurface(null);
                    tmpMediaPlayer.stop();
                    tmpMediaPlayer.release();
                    tmpHandlerThread.quit();
                }
            });
            ijkMediaPlayer = null;
            SAVED_SURFACE = null;
        }
    }

    @Override
    public boolean isValidKernel() {
        return ijkMediaPlayer != null;
    }

    @Override
    public long getCurrentPosition() {
        return ijkMediaPlayer == null ? currentPosition : ijkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getCurrentPositionRealTime() {
        return ijkMediaPlayer == null ? 0 : ijkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getSelectedTrack(int type) {
        return ijkMediaPlayer == null ? -1 : ijkMediaPlayer.getSelectedTrack(type);
    }

    @Override
    public long getDuration() {
        return ijkMediaPlayer == null ? 0 : ijkMediaPlayer.getDuration();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setVolume(leftVolume, rightVolume);

        }
    }

    @Override
    public void setSpeed(float speed) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!staticTrack) {
                        iTrackInfos = iMediaPlayer.getTrackInfo();
                        if (null != iTrackInfos) {
                            play.onTrackInfo(iTrackInfos, ITrackInfo.class);
                        }
                    }
                } catch (Exception e) {

                }

                play.onPrepared();

            }
        });
    }

    @Override
    public void onVideoSizeChanged(final IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        handler.post(() -> play.onVideoSizeChanged(iMediaPlayer.getVideoWidth(), iMediaPlayer.getVideoHeight()));
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                play.onError(MediaIjk.class, null, what, extra);
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                play.onInfo(what, extra);
            }
        });
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, final int percent) {
        handler.post(() -> play.setBufferProgress(percent, MediaIjk.class));
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        handler.post(() -> play.onSeekComplete());
    }

    @Override
    public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
    }

    @Override
    public void setSurface(Surface surface) {
        ijkMediaPlayer.setSurface(surface);
    }


    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        handler.post(() -> play.onCompletion());
    }


    @Override
    public boolean onNativeInvoke(int i, Bundle bundle) {

        LogUtility.d(TAG, "onNativeInvoke", i);


        return i == 2;
    }
}
