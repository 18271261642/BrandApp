package com.isport.brandapp.blue;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.crrepa.ble.conn.type.CRPBleMessageType;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_NotifyModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SleepAndNoDisturbModelAction;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.util.DateTimeUtils;
import com.isport.brandapp.util.DeviceTypeUtil;

import androidx.core.app.ActivityCompat;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;

public class ContentUtils {


    public static void sendCall(String incomingNumber, Context context) {
        if (AppConfiguration.isConnected) {
            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
            if (device == null) {
                return;
            }
            String devcieName = device.deviceName;
            int deviceType = device.deviceType;
            Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(devcieName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));

            Logger.myLog("sendCall" + device + "------" + watch_w516_notifyModelByDeviceId);
            if (device == null || watch_w516_notifyModelByDeviceId == null) {
                return;
            }
            // TODO: 2018/11/8 同步解绑的逻辑
            if (!watch_w516_notifyModelByDeviceId.getCallSwitch()) {
                return;
            }
            if (DeviceTypeUtil.isContainW55X(deviceType)) {
                String name = ContentUtils.contactNameByNumber(context, incomingNumber);
                if (TextUtils.isEmpty(name)) {
                    name = incomingNumber;
                }
                if(device.getDeviceType() == IDeviceType.TYPE_WATCH_W560 ){
                    ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, name, UIUtils.getString(R.string.incomingNumber), 29);
                    return;
                }

                if(device.getDeviceType() == IDeviceType.TYPE_WATCH_W560B){
                    Watch_W516_SleepAndNoDisturbModel watchDNDBean = Watch_W516_SleepAndNoDisturbModelAction.findWatch_W516_SleepAndNoDisturbModelyDeviceId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), device.getDeviceName());
                    //没有设置勿扰模式
                    if(watchDNDBean ==null){
                        ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, name, UIUtils.getString(R.string.incomingNumber), 1);
                        return;
                    }
                    //勿扰模式未打开
                    if(!watchDNDBean.getOpenNoDisturb()){
                        ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, name, UIUtils.getString(R.string.incomingNumber), 1);
                        return;
                    }
                    //开始时间和结束时间
                    String startTime = watchDNDBean.getNoDisturbStartTime();
                    String endTime = watchDNDBean.getNoDisturbEndTime();

                    //判断是否在区间内
                    if(DateTimeUtils.isComparisonWith(startTime,endTime))
                        return;
                    ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, name, UIUtils.getString(R.string.incomingNumber), 1);
                    return;
                }


                ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, name, UIUtils.getString(R.string.incomingNumber), 1);
                // ISportAgent.getInstance().requestBle(BleRequest.bracelet_send_phone, incomingNumber, ContentUtils.contactNameByNumber(context, incomingNumber));
            } else if (DeviceTypeUtil.isContainWatch(deviceType)) {
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SEND_NOTIFICATION, 0);
            } else if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
                ISportAgent.getInstance().requestBle(BleRequest.bracelet_send_phone, incomingNumber, ContentUtils.contactNameByNumber(context, incomingNumber));
            } else if (DeviceTypeUtil.isContaintW81(deviceType)) {
                String name = ContentUtils.contactNameByNumber(context, incomingNumber);
                if (TextUtils.isEmpty(name)) {
                    name = incomingNumber;
                }
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, name, CRPBleMessageType.MESSAGE_PHONE);

            }
        } else {
            Log.e(TAG, "999999999999999");
        }


    }


    public static final String TAG = "ContentUtils";

    public static String contactNameByNumber(Context context, String number) {
        String name = "";
        Cursor cursor = null;
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager
                    .PERMISSION_GRANTED) {
                Logger.myLog("no premission contactNameByNumber");
                return "";
            }
            //String number = "18052369652";

            Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    name = cursor.getString(0);
                    Log.i(TAG, name);
                    return name;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            name = "";
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            return name;
        }

    }
}
