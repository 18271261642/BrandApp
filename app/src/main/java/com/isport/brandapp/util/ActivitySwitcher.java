package com.isport.brandapp.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.isport.blelibrary.utils.Constants;
import com.isport.brandapp.bind.ActivityAllDevice;
import com.isport.brandapp.bind.ActivityScan;
import com.isport.brandapp.device.scale.ActivityScaleRealTimeData;
import com.isport.brandapp.home.MainActivity;
import com.isport.brandapp.login.ActivitySettingUserInfo;
import com.isport.brandapp.upgrade.DFUActivity;
import com.isport.brandapp.upgrade.DFUDeviceSelectActivity;
import com.isport.brandapp.wu.activity.MeassureActivity;

import java.util.List;

import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


/**
 * Created by Administrator on 2016/6/14.
 */
public class ActivitySwitcher {
    private static void startActivityDefault(Context context, Intent intent) {
        context.startActivity(intent);
    }

    public static void goMainAct(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        startActivityDefault(context, intent);
    }

    public static void goBindAct(Context context) {
        ViewMultiClickUtil.clearTimeNoView();
        Intent intent = new Intent(context, ActivityAllDevice.class);
        startActivityDefault(context, intent);
    }

    public static void goDFUDeviceSelectAct(Context context, String name, String mac) {
        ViewMultiClickUtil.clearTimeNoView();
        Intent intent = new Intent(context, DFUDeviceSelectActivity.class);
        intent.putExtra(JkConfiguration.DEVICE_NAME, name);
        intent.putExtra(JkConfiguration.DEVICE_MAC, mac);
        startActivityDefault(context, intent);
    }

    public static void goDFUAct(Context context, int devcieType, String deviceName, String deviceMac, boolean isCommingSetting) {
        Intent intent = new Intent(context, DFUActivity.class);
        intent.putExtra(JkConfiguration.DEVICE_TYPE, devcieType);
        intent.putExtra(JkConfiguration.DEVICE_NAME, deviceName);
        intent.putExtra(JkConfiguration.DEVICE_MAC, deviceMac);
        intent.putExtra("isCommingSetting", isCommingSetting);
        startActivityDefault(context, intent);
    }

    public static void goSettingUserInfoAct(Context context) {
        Intent intent = new Intent(context, ActivitySettingUserInfo.class);
        intent.putExtra("USER_NICKNAME", "");
        startActivityDefault(context, intent);
    }

    public static void goScaleRealTimeAct(Context context, boolean showScaleData) {
        Intent intentScale = new Intent(context, ActivityScaleRealTimeData.class);
        intentScale.putExtra(Constants.SHOWSCALEDATA, showScaleData);
        startActivityDefault(context, intentScale);
    }


    public static void goScanActivty(Context context, int deviceType) {
        ViewMultiClickUtil.clearTimeNoView();
        Intent mIntent;
        mIntent = new Intent(context, ActivityScan.class);
        mIntent.putExtra("device_type", deviceType);
        startActivityDefault(context, mIntent);

    }

    public static void goMeasureActivty(Context context, int deviceType) {
        Activity activity = (Activity) context;
        Intent mIntent;
        mIntent = new Intent(context, MeassureActivity.class);
        mIntent.putExtra("device_type", deviceType);
        startActivityDefault(context, mIntent);
        // activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

}
