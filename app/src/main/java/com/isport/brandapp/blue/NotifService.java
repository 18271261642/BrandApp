package com.isport.brandapp.blue;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Administrator on 2017/9/28.
 */

public class NotifService extends AccessibilityService {

    private String TAG = "NotifService";
    private boolean isOpen = false;

    /**
     * get state of notification service
     *
     * @param context
     * @return state of notification service
     */
    public static boolean getNotificationIsRun(Context context) {
        /*final String service = context.getPackageName() + "/" + NotifService.class.getCanonicalName();//getPackageName() + "/" + YOURAccessibilityService.class.getCanonicalName();
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        boolean state = false;
        for (AccessibilityServiceInfo info : accessibilityServices) {
            String tpv = info.getId();
            if (tpv.equalsIgnoreCase(service)) {
                state = true;
                break;
            }
        }
        return state;*/

        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = context.getPackageName() + "/" + NotifService.class.getCanonicalName();

        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {

        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }
        return false;


    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        handleAccessibility(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {

    }

    public void handleAccessibility(AccessibilityEvent event) {
        Log.e(TAG , "handleAccessibility "+event.getPackageName().toString());
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

//            NotiManager.getInstance(this).handleNotification(event.getPackageName().toString(), (Notification) event.getParcelableData());
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        NotiManager.getInstance(this);
        isOpen = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isOpen = false;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
