package com.example.video.util;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 网速监听
 */

public class LoadingSpeed {

    private static long lastTime;
    private static long lastTraff;

    private Handler handler;

    private Timer mTimer;

    private TimerTask mTask;

    private DecimalFormat mFormat;

    public static final int UPDATE_LOADING_SPEED = 110;


    //防止多次调用start标志


    public LoadingSpeed(Handler handler) {
        this.handler = handler;
        mFormat = new DecimalFormat("0.0");
    }

    /**
     * 开始更新当前网速
     */
    public void start() {

        if (null == mTimer) {
            mTimer = new Timer();
            mTask = new TimerTask() {
                @Override
                public void run() {
                    upDateLoadSpeed();
                }
            };
            lastTraff = getCurrentLoadSpeed();
            lastTime = System.currentTimeMillis();
            mTimer.schedule(mTask, 1000, 1000);
        }


    }

    /**
     * 获取当前流量
     *
     * @return
     */
    private long getCurrentLoadSpeed() {
        //获取当前接收的流量
        long currentSpeed = TrafficStats.getUidRxBytes(Process.myUid());
        if (currentSpeed == TrafficStats.UNSUPPORTED) {
            return TrafficStats.getTotalRxBytes();
        }
        return currentSpeed;
    }

    /**
     * 更新当前加载数据
     */
    private void upDateLoadSpeed() {
        //获取当前速度
        long currentTraff = getCurrentLoadSpeed();
        //获取当前时间
        long currentTime = System.currentTimeMillis();
        long dis = (currentTraff - lastTraff) / (currentTime - lastTime);
        String speed = formatTraff(dis);
        lastTime = currentTime;
        lastTraff = currentTraff;
        if (handler != null) {
            Message msg = Message.obtain();
            msg.what = UPDATE_LOADING_SPEED;
            msg.obj = speed;
            handler.sendMessage(msg);
        }
    }

    /**
     * 格式化当前速度
     *
     * @param traff
     * @return
     */
    private String formatTraff(long traff) {
        String speed;
        if (traff * 1000 > 1024 * 1024) {   // Mb
            speed = mFormat.format(traff * 1d / 1024 / 1024 * 1000) + "Mb/s";
        } else {  // kb
            speed = mFormat.format(traff * 1d / 1024 * 1000) + "Kb/s";
        }
        return speed;
    }

    /**
     * 取消加载进度更新
     */
    public void cancel() {
        if(mTimer!=null){
            mTimer.cancel();
        }
        mTimer = null;

    }

}
