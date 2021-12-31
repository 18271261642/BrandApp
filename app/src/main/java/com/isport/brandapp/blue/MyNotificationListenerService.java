package com.isport.brandapp.blue;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;


/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/16 16:44
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public abstract class MyNotificationListenerService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        toggleNotificationListenerService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toggleNotificationListenerService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Intent sevice = new Intent(this, AlertService.class);
            sevice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startService(sevice);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 被杀后再次启动时，监听不生效的问题
     */
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(new ComponentName(this, AlertService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, AlertService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
