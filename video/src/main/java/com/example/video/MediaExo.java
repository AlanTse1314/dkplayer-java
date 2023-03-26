package com.example.video;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.HttpListener;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.video.VideoListener;
import com.example.video.util.LogUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by MinhDV on 5/3/18.
 */
@SuppressLint("WrongConstant")
public class MediaExo extends MediaInterface implements Player.EventListener, VideoListener, HttpListener {
    private static final String TAG = "MediaExo";
    private SimpleExoPlayer simpleExoPlayer;
    private Runnable callback;
    private DefaultTrackSelector trackSelector = null;
    private MediaSource videoSource;

    private static final String CODEC_NOT_FOUND = "Failed to query underlying media codecs";
    private static final String CODEC_NOT_RTMP = "RTMP error: -1000";
    private static final List<String> STRING_LIST = new ArrayList<>();

    static {
        STRING_LIST.add("Attempt to invoke virtual method 'int java.io.InputStream.read(byte[], int, int)");
        STRING_LIST.add("java.net.SocketException: Socket closed");
        STRING_LIST.add("com.google.android.exoplayer2.upstream.Loader$UnexpectedLoaderException: Unexpected NullPointerException: null");
    }

    private boolean initStatus = false;

    public MediaExo(Video play) {
        super(play);
    }

    @Override
    public void start() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
            LogUtility.d(TAG, "启动播放");

        }
    }

    @Override
    public void prepare() {
        release();
        super.prepare();
        mMediaHandlerThread = new HandlerThread(THREAD_NAME);
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
        handler = new Handler();
        mMediaHandler.post(() -> {
            try {
                initStatus = false;
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
                trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true,
                        C.DEFAULT_BUFFER_SEGMENT_SIZE),
                        360000,
                        600000,
                        1000,
                        5000,
                        C.LENGTH_UNSET,
                        false);

                RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), renderersFactory, trackSelector, loadControl);

                //设置协议
                DefaultHttpDataSource dataSource = new DefaultHttpDataSource(getUserAgent(),
                        null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS
                        , DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true, null);
                Map<String, String> hashMap = getAgreement();
                if (hashMap != null) {
                    for (String key : hashMap.keySet()) {
                        if (!key.equals("User-Agent")) {
                            dataSource.setRequestProperty(key, hashMap.get(key));
                        }
                    }
                }
                Uri uri = getUri();

                switch (VIDEO_TYPE) {
                    case VIDEO_M3U8:
                        videoSource = new HlsMediaSource.Factory(dataType -> dataSource).createMediaSource(uri);
                        break;
                    case VIDEO_RTMP:
                        videoSource = new ExtractorMediaSource.Factory(new RtmpDataSourceFactory()).createMediaSource(uri);
                        break;
                    case VIDEO_MPD:
                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), () -> dataSource);
                        videoSource = new DashMediaSource(uri, dataSourceFactory, new DefaultDashChunkSource.Factory(dataSourceFactory), null, null);
                        break;
                    default:
                        videoSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getContext(), () -> dataSource)).createMediaSource(uri);
                        break;
                }
                dataSource.setHttpListener(this);
                simpleExoPlayer.addListener(MediaExo.this);
                simpleExoPlayer.addVideoListener(MediaExo.this);
//                simpleExoPlayer.setRepeatMode(jzvd.jzDataSource.looping ? 1 : 0);
                simpleExoPlayer.prepare(videoSource);
                simpleExoPlayer.setPlayWhenReady(false);
                callback = new onBufferingUpdate();
                simpleExoPlayer.setVideoSurface(getSurface());
            } catch (Throwable e) {

            }

        });
    }


    @Override
    public void onVideoSizeChanged(final int width, final int height,
                                   int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        handler.post(() -> play.onVideoSizeChanged(width, height));
    }

    @Override
    public void onRenderedFirstFrame() {
//        Log_e4a.e(TAG, "onRenderedFirstFrame");
    }

    @Override
    public void pause() {
        super.pause();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);


        }
    }

    @Override
    public boolean isPlaying() {
        return simpleExoPlayer != null && simpleExoPlayer.getPlayWhenReady();
    }


    @Override
    protected void seekTo(long position) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(position);
            currentPosition = position;
        }
    }


    @Override
    public synchronized void release() {
        super.release();
        if (mMediaHandler != null && mMediaHandlerThread != null && simpleExoPlayer != null) {//不知道有没有妖孽
            final HandlerThread tmpHandlerThread = mMediaHandlerThread;
            final SimpleExoPlayer tmpMediaPlayer = simpleExoPlayer;
            currentPosition = simpleExoPlayer.getCurrentPosition();
            mMediaHandler.post(() -> {
                tmpMediaPlayer.release();//release就不能放到主线程里，界面会卡顿
                tmpHandlerThread.quit();
            });
            simpleExoPlayer = null;
            SAVED_SURFACE = null;
        }

    }


    @Override
    public long getCurrentPosition() {
        if (simpleExoPlayer != null) {
            return currentPosition = simpleExoPlayer.getCurrentPosition();
        } else {
            return currentPosition;
        }
    }

    @Override
    public long getCurrentPositionRealTime() {
        return simpleExoPlayer == null ? 0 : simpleExoPlayer.getCurrentPosition();
    }

    @Override
    public long getSelectedTrack(int type) {
        return -1;
    }

    @Override
    public long getDuration() {
        if (simpleExoPlayer != null) {
            if (videoSource instanceof HlsMediaSource) {
                HlsMediaSource hlsMediaSource = (HlsMediaSource) videoSource;
                //EXO直播判断  只对M3U8有效
                if (hlsMediaSource.isLive()) {
                    return 0;
                }
            }
            return simpleExoPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(leftVolume);
            simpleExoPlayer.setVolume(rightVolume);
        }

    }

    @Override
    public void setSpeed(float speed) {
        if (simpleExoPlayer != null) {
            PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
            simpleExoPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    @Override
    public void onTimelineChanged(final Timeline timeline, Object manifest, final int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {


    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
//        Log_e4a.e(TAG, "onLoadingChanged");
    }

    @Override
    public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        handler.post(() -> {
            switch (playbackState) {
                case Player.STATE_IDLE:
                    break;
                case Player.STATE_BUFFERING:
                    handler.post(callback);
                    break;
                case Player.STATE_READY:
                    LogUtility.d(TAG, "EXO状态" + playWhenReady);
                    if (!initStatus && !playWhenReady) {
                        //准备完毕
                        initStatus = true;
                        play.onPrepared();
                        play.continueLastTime();
                        start();
                    }
                    play.onStatePlaying(playWhenReady);//开始渲染图像，真正进入playing状态
                    break;
                case Player.STATE_ENDED:
                    play.onCompletion();
                    break;
            }
        });
    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        LogUtility.e(TAG, "onPlayerError " + error.getMessage());
        handler.post(() -> play.onError(MediaExo.class, error.getMessage(), 1000, 1000));
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
        handler.post(() -> play.onSeekComplete());
    }

    @Override
    public void setSurface(Surface surface) {
        simpleExoPlayer.setVideoSurface(surface);
    }

    @Override
    public boolean selectMetaTrack(int type, int track) {
        return false;
    }


//    @Override
//    public boolean selectMetaTrack(int type, int track) {
//        if (exoTrack == null || simpleExoPlayer == null) {
//            return false;
//        }
//        //EX0设置hls 也就是视频轨道 判断索引是否溢出或 是否与当前轨道相同
//        if (getType(type) == 0) {
//            if (exoTrack.getIndexVideo(track) == -1) {
//                return false;
//            }
//            if (exoTrack.getIndexVideo(track) == track) {
//                return true;
//            }
//        }
//        //EX0设置音轨 判断索引是否溢出或 是否与当前轨道相同
//        if (getType(type) == 1) {
//            if (exoTrack.getIndexAudio(track) == -1) {
//                return false;
//            }
//            if (exoTrack.getIndexAudio(track) == track) {
//                return true;
//            }
//        }
//        try {
//            MappingTrackSelector.MappedTrackInfo trackInfo = trackSelector.getCurrentMappedTrackInfo();
//            if (trackInfo != null) {
//                trackSelector.setParameters(trackSelector.buildUponParameters().setSelectionOverride(getType(type),
//                        trackInfo.getTrackGroups(getType(type)),
//                        new DefaultTrackSelector.SelectionOverride(getType(type), track)));
//
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    @Override
    public boolean isValidKernel() {
        return simpleExoPlayer != null;
    }


    private class onBufferingUpdate implements Runnable {
        @Override
        public void run() {
            if (simpleExoPlayer != null) {
                final int percent = simpleExoPlayer.getBufferedPercentage();
                handler.post(() -> {
                    play.setBufferProgress(percent, MediaExo.class);
                    if (percent <= 100) {
                        handler.post(callback);
                    } else {
                        handler.removeCallbacks(callback);
                    }
                });
            }
        }
    }

    /**
     * 关于连接异常  ,
     */
    @Override
    public boolean onAbnormalConnection(DataSpec dataSpec, Throwable e) {

        //true 交给EXO自己处理
        return true;
    }

    @Override
    public void onHttpResponse() {

    }
}
