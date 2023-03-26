package com.example.video.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.video.enume.PalyStateEnume;
import com.yanbo.lib_screen.VApplication;
import com.yanbo.lib_screen.callback.ControlCallback;
import com.yanbo.lib_screen.entity.AVTransportInfo;
import com.yanbo.lib_screen.entity.ClingDevice;
import com.yanbo.lib_screen.entity.RemoteItem;
import com.yanbo.lib_screen.event.ControlEvent;
import com.yanbo.lib_screen.manager.ClingManager;
import com.yanbo.lib_screen.manager.ControlManager;
import com.yanbo.lib_screen.manager.DeviceManager;
import com.yanbo.lib_screen.utils.LogUtils;
import com.yanbo.lib_screen.utils.VMDate;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.model.item.Item;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

;

/**
 * 投屏工具类
 */
public class ScreenProjectionUtil {
    private static final String TAG = "ScreenProjectionUtil";
    private static ClingManager clingManager;
    private static BrowseRegistryListener browseRegistryListener;
    private static PlayStateListener playStateListener;
    private static DeviceStateListener deviceStateListener;
    private final static List<ClingDevice> clingDeviceList = new ArrayList<>();

    static {
        //初始化投屏服务  ,这里使用的是全局上下文
        //这里使用的是反射获取上下文
        VApplication.init(ContextUtil.getsContext());

    }

    /**
     * 搜索设备
     */
    public static synchronized void searchDevice(DeviceStateListener deviceStateListener, PlayStateListener playStateListener) {
        if (clingManager == null) {
            browseRegistryListener = new BrowseRegistryListener();
            clingManager = ClingManager.getInstance(browseRegistryListener);
            clingManager.startClingService();
            EventBus.getDefault().register(browseRegistryListener);
        }
        ScreenProjectionUtil.deviceStateListener = deviceStateListener;
        ScreenProjectionUtil.playStateListener = playStateListener;
    }

    private static Item localItem;
    private static RemoteItem remoteItem;

    /**
     * 开始投屏
     */
    public synchronized static void startScreenProjection(ClingDevice clingDevice, String videoName, String url) {
        if (clingManager != null) {
            DeviceManager.getInstance().setCurrClingDevice(clingDevice);

            RemoteItem itemurl1 = new RemoteItem(videoName, Md5Util.strToMD5(url), videoName,
                    0, "00:00:00", "1280x720", url);
            clingManager.setRemoteItem(itemurl1);
            localItem = clingManager.getLocalItem();
            remoteItem = clingManager.getRemoteItem();
        }
    }

    /**
     * 停止投屏服务
     */
    public static synchronized void stopTheScreenProjectionService() {
        if (clingManager != null) {
            clingManager.stopClingService();
            EventBus.getDefault().unregister(browseRegistryListener);
            DeviceManager.getInstance().destroy();
            clingManager = null;
        }
    }


    /**
     * 播放开关
     */
    public static void play() {
        if (ControlManager.getInstance().getState() == ControlManager.CastState.STOPED) {
            if (localItem != null) {
                newPlayCastLocalContent();
            } else {
                newPlayCastRemoteContent();
            }
        } else if (ControlManager.getInstance().getState() == ControlManager.CastState.PAUSED) {
            playCast();
        } else if (ControlManager.getInstance().getState() == ControlManager.CastState.PLAYING) {
            pauseCast();
        } else {
            LogUtility.d(TAG, "正在连接设备，稍后操作");
        }
    }

    private static void newPlayCastLocalContent() {
        ControlManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);
        ControlManager.getInstance().newPlayCast(localItem, new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                ControlManager.getInstance().initScreenCastCallback();
                if (playStateListener != null) {
                    playStateListener.onStateChanges(PalyStateEnume.STATE_PLAYING);
                }
            }

            @Override
            public void onError(int code, String msg) {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                showToast(String.format("投屏失败! %s", msg));
            }
        });
    }

    private static void newPlayCastRemoteContent() {
        ControlManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);
        ControlManager.getInstance().newPlayCast(remoteItem, new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                ControlManager.getInstance().initScreenCastCallback();
                if (playStateListener != null) {
                    playStateListener.onStateChanges(PalyStateEnume.STATE_PLAYING);
                }
            }

            @Override
            public void onError(int code, String msg) {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                showToast(String.format("投屏失败! %s", msg));
            }
        });
    }

    public static void playCast() {
        ControlManager.getInstance().playCast(new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);

                if (playStateListener != null) {
                    playStateListener.onStateChanges(PalyStateEnume.STATE_PLAYING);
                }

            }

            @Override
            public void onError(int code, String msg) {
                showToast(String.format("播放失败 %s", msg));
            }
        });
    }

    /**
     * 暂停播放
     */
    public static void pauseCast() {
        ControlManager.getInstance().pauseCast(new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.PAUSED);
                if (playStateListener != null) {
                    playStateListener.onStateChanges(PalyStateEnume.STATE_PAUSE);
                }
            }

            @Override
            public void onError(int code, String msg) {
                showToast(String.format("暂停失败 %s", msg));
            }
        });
    }

    /**
     * 停止播放
     */
    public static void stopCast() {
//        ControlManager.getInstance().unInitScreenCastCallback();
        ControlManager.getInstance().stopCast(new ControlCallback() {
            @Override
            public void onSuccess() {
                ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                if (playStateListener != null) {
                    playStateListener.onStateChanges(PalyStateEnume.STATE_STOPED);
                }
            }

            @Override
            public void onError(int code, String msg) {

                showToast(String.format("停止失败 %s", msg));
            }
        });
    }

    /**
     * 改变投屏音量
     */
    public static void setVolume(int volume) {
        ControlManager.getInstance().setVolumeCast(volume, new ControlCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int code, String msg) {
                showToast(String.format("音量设置失败 %s", msg));
            }
        });
    }

    /**
     * 快进
     */
    public static void seekTo(int progress) {
        String target = VMDate.toTimeString(progress);
        ControlManager.getInstance().seekCast(target, new ControlCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int code, String msg) {
                showToast(String.format("时间跳转失败 %s", msg));
            }
        });
    }

    private static void showToast(final String msg) {
        if (clingManager != null) {
            LogUtility.d(TAG, msg);
        }
    }


    static class BrowseRegistryListener extends DefaultRegistryListener {

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            LogUtils.d("remoteDeviceDiscoveryStarted %s", device.getDisplayString());
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            LogUtils.e("remoteDeviceDiscoveryFailed %s - %s", device.getDisplayString() + "---" + ex.toString());

        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            LogUtils.i("remoteDeviceAdded %s", device.getDisplayString());
            onDeviceAdded(device);
        }


        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            LogUtils.e("remoteDeviceRemoved %s", device.getDisplayString());
            onDeviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            LogUtils.d("localDeviceAdded %s", device.getDisplayString());

        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            LogUtils.d("localDeviceRemoved %s", device.getDisplayString());
        }

        /**
         * 新增 DLNA 设备
         */
        public void onDeviceAdded(Device device) {
            DeviceManager.getInstance().addDevice(device);
            deviceAdded(device);
        }

        /**
         * 移除 DLNA 设备
         */
        public void onDeviceRemoved(Device device) {
            DeviceManager.getInstance().removeDevice(device);
            deviceRemoved(device);
        }

        //设备新增
        public void deviceAdded(final Device device) {
            if (device.getType().equals(DeviceManager.DMR_DEVICE)) {
                ClingDevice clingDevice = new ClingDevice(device);
                int index = clingDeviceList.indexOf(clingDevice);
                if (index == -1) {
                    clingDeviceList.add(clingDevice);
                } else {
                    clingDeviceList.get(index).setDevice(device);
                }
                if (deviceStateListener != null) {
                    deviceStateListener.onDeviceAdded(clingDeviceList);
                }
            }
        }

        //设备移除
        public void deviceRemoved(final Device device) {
            ClingDevice clingDevice = new ClingDevice(device);
            clingDeviceList.remove(clingDevice);
            if (deviceStateListener != null) {
                deviceStateListener.onDeviceRemoved(clingDevice);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventBus(ControlEvent event) {
            AVTransportInfo avtInfo = event.getAvtInfo();
            if (avtInfo != null) {
                if (!TextUtils.isEmpty(avtInfo.getState())) {
                    if (avtInfo.getState().equals("TRANSITIONING")) {
                        ControlManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);
                    } else if (avtInfo.getState().equals("PLAYING")) {
                        ControlManager.getInstance().setState(ControlManager.CastState.PLAYING);
                        if (playStateListener != null) {
                            playStateListener.onStateChanges(PalyStateEnume.STATE_PLAYING);
                        }
                    } else if (avtInfo.getState().equals("PAUSED_PLAYBACK")) {
                        ControlManager.getInstance().setState(ControlManager.CastState.PAUSED);
                        if (playStateListener != null) {
                            playStateListener.onStateChanges(PalyStateEnume.STATE_PAUSE);
                        }
                    } else if (avtInfo.getState().equals("STOPPED")) {
                        ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                        if (playStateListener != null) {
                            playStateListener.onStateChanges(PalyStateEnume.STATE_STOPED);
                        }
                    } else {
                        ControlManager.getInstance().setState(ControlManager.CastState.STOPED);
                        if (playStateListener != null) {
                            playStateListener.onStateChanges(PalyStateEnume.STATE_STOPED);
                        }
                    }
                }
                long maxTime = 0;
                long progress = 0;
                Log.d("投屏", String.format("总时间:%s 当前时间:%s", avtInfo.getMediaDuration(), avtInfo.getTimePosition()));
                if (!TextUtils.isEmpty(avtInfo.getMediaDuration())) {
                    maxTime = VMDate.fromTimeString(avtInfo.getMediaDuration());
                }
                if (!TextUtils.isEmpty(avtInfo.getTimePosition())) {
                    progress = VMDate.fromTimeString(avtInfo.getTimePosition());
                }
                if (maxTime == 0 && progress == 0) {
                    return;
                }
                if (playStateListener != null) {

                    playStateListener.onProgressChanges(progress, maxTime, avtInfo.getTimePosition(), avtInfo.getMediaDuration());
                }
            }
        }
    }

    public interface PlayStateListener {
        void onStateChanges(PalyStateEnume stateEnume);

        /**
         * @param currentTimeStr     当时时间文本型
         * @param totalTimeStr       总时间文本型
         * @param totalTime          总时间   秒
         * @param currentProgress    当前时间秒
         */
        void onProgressChanges(long currentProgress, long totalTime, String currentTimeStr, String totalTimeStr);
    }

    public interface DeviceStateListener {
        void onDeviceAdded(List<ClingDevice> clingDeviceList);

        void onDeviceRemoved(ClingDevice clingDevice);

    }


}
