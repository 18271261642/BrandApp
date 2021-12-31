package com.isport.brandapp.blue;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.isport.blelibrary.BleConstance;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_NotifyModelAction;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


/**
 * Created by Administrator on 2017/1/3.
 */

//通知监听服务类
public class NotificationService extends NotificationListenerService {


    public static Vector<StatusBarNotification> msgVector = new Vector<>();
    private static final String TAG = "NotificationService";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    static boolean isFirst = true;

    private AudioManager audioManager;

    public static String SmsAction = null;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleConstance.W560_MUSIC_CONTROL_STATUS);

        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver,intentFilter);

        if(audioManager == null)
            audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, NotificationService.class.getCanonicalName() + " onBind");
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.e(TAG, NotificationService.class.getCanonicalName() + " onListenerConnected");
    }

    @Override
    public void onNotificationChannelGroupModified(String pkg, UserHandle user, NotificationChannelGroup group, int
            modificationType) {
        Log.e(TAG, NotificationService.class.getCanonicalName() + " onNotificationChannelGroupModified");
        super.onNotificationChannelGroupModified(pkg, user, group, modificationType);
    }

    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int
            modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
        Log.e(TAG, NotificationService.class.getCanonicalName() + " onNotificationChannelModified");
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    isFirst = true;
                    if (msgVector.size() >= 1)
                        sendMsg(msgVector.get(msgVector.size() - 1));
                    break;
                default:
                    break;
            }
        }
    };



    private void sendMsg(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }
        // 当 API > 18 时，使用 extras 获取通知的详细信息
        //因为一次会有多条，应该都是分割的，那么在同时来多条的情况下，应该展示最新的那条
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle extras = notification.extras;
            if (extras != null) {
                // 获取通知标题
                String title = extras.getString(Notification.EXTRA_TITLE, "");
                // 获取通知内容
                String content = extras.getString(Notification.EXTRA_TEXT, "");
                Log.e(TAG, title + "***KK***" + content);
                if (!TextUtils.isEmpty(content) && content.contains("Hello")) {
                    Log.e(TAG, "***API > 18***" + content);
                }
            }

        } else {
            // 当 API = 18 时，利用反射获取内容字段
            List<String> textList = getText(notification);
            Log.e(TAG, "***KK***" + textList.toString());
            if (textList != null && textList.size() > 0) {
                for (String text : textList) {
                    if (!TextUtils.isEmpty(text) && text.contains("Hello")) {
                        Log.e(TAG, "***API = 18***" + text);
                        break;
                    }
                }
            }
        }


        NotiManager.getInstance(this).handleNotification(sbn.getPackageName(), sbn.getNotification(),SmsAction);
    }

    public List<String> getText(Notification notification) {
        if (null == notification) {
            return null;
        }
        RemoteViews views = notification.bigContentView;
        if (views == null) {
            views = notification.contentView;
        }
        if (views == null) {
            return null;
        }
        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        List<String> text = new ArrayList<>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);
            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();
                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    //当系统收到新的通知后出发回调
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pname = sbn.getPackageName();
        Logger.myLog("getPackageName " + pname);
        if (AppConfiguration.isConnected) {
            // TODO: 2018/11/8 同步解绑的逻辑

            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
            if (device == null) {
                return;
            }

            Logger.myLog(TAG,"----ddeveice="+device.toString());

            //NotiManager.getInstance(this).handleNotification(pname,sbn.getNotification());


            if (device.deviceType == JkConfiguration.DeviceType.WATCH_W516) {
                Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(device.deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
                if (watch_w516_notifyModelByDeviceId != null) {
                    if (watch_w516_notifyModelByDeviceId.getMsgSwitch())
                        ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SEND_NOTIFICATION_N, pname);
                }
            } else if (device.deviceType == JkConfiguration.DeviceType.BRAND_W311 ||
                    device.deviceType == JkConfiguration.DeviceType.Brand_W520) {
                if (isFirst) {
                    isFirst = false;
                    msgVector.add(sbn);
//            startTime=System.currentTimeMillis();
                    mHandler.sendEmptyMessageDelayed(0x01, 2000);
                    //设置一个监听，如果100ms内没有消息来就直接发送
                } else {
                    if (msgVector.size() >= 1) {
                        msgVector.clear();
                    }
                    msgVector.add(sbn);
                }
            } else {
                sendMsg(sbn);
            }
        }

    }

    //当系统通知被删掉后出发回调
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


    public static void toggleNotificationListenerService(Context context) {
        Log.e(TAG, "***去重启***");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationService.class), PackageManager
                .COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationService.class), PackageManager
                .COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    //确认NotificationMonitor是否开启
    public static void ensureCollectorRunning(Context context) {
        boolean collectorRunning = false;
        Log.e(TAG, "***去启动服务***");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = manager.getRunningServices(250);
        if (infos != null && infos.size() > 0) {
            for (int i = 0; i < infos.size(); i++) {
                ActivityManager.RunningServiceInfo info = infos.get(i);
                if (info.service.getClassName().equals(NotificationService.class.getName())) {
                    collectorRunning = true;
                }
            }
        }
        collectorRunning = false;
        if (collectorRunning) {
            return;
        }
        toggleNotificationListenerService(context);
    }

    public static boolean isEnabled(Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 音乐播放状态
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals("android.provider.Telephony.SMS_RECEIVED")){       //短信的广播
                SmsAction = action;
            }


            if(action.equals(BleConstance.W560_MUSIC_CONTROL_STATUS)){
                int statusCode = intent.getIntExtra(BleConstance.W560_MUSIC_STATUS,0);
                Logger.myLog(TAG,"-code="+statusCode);
                if(statusCode == 1){    //播放
                    MusicManager.playMusic(audioManager,NotificationService.this);
                }
                if(statusCode == 2){    //暂停
                    MusicManager.pauseMusic(audioManager,NotificationService.this);
                }

                if(statusCode == 3){    //上一首
                    MusicManager.previousMusic(audioManager,NotificationService.this);
                }

                if(statusCode == 4){    //下一首
                    MusicManager.nextMusic(audioManager,NotificationService.this);
                }

                if(statusCode == 5 || statusCode == 6){ //音量+或-
                    MusicManager.setVoiceStatus(audioManager,statusCode == 5 ? 0x01 : 0x00);
                }

            }
        }
    };

}
