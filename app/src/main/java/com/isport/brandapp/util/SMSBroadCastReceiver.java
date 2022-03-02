package com.isport.brandapp.util;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.crrepa.ble.conn.type.CRPBleMessageType;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_NotifyModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SleepAndNoDisturbModelAction;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.BleRequest;
import com.tencent.mm.opensdk.utils.Log;

import org.apache.commons.lang.StringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * Created by Admin
 * Date 2021/11/25
 */
public class SMSBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSBroadCastReceiver";


    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String msgStr = (String) msg.obj;
            android.util.Log.e(TAG, "------msgStr=" + msgStr);
            sendMsgToDevice(msgStr);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)
            return;
        Log.e(TAG,"------短信action="+action);
        //收到短信action
        if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
            //getReceiverMsg(context);
            analysisReceiver(context,intent);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void analysisReceiver(Context context,Intent intent){
        try {
            Bundle bundle = intent.getExtras();
            String format = intent.getStringExtra("format");
            if (bundle == null)
                return;

            Object[] object = (Object[]) bundle.get("pdus");
            if (object == null)
                return;
            StringBuilder sb = new StringBuilder();

            for (Object obs : object) {
                byte[] pusMsg = (byte[]) obs;
                SmsMessage sms = SmsMessage.createFromPdu(pusMsg, format == null ? "" : format);
                String mobile = sms.getOriginatingAddress();//发送短信的手机号
                String content = sms.getMessageBody();//短信内容
                sb.append(mobile +":"+ content);

            }

            Message message = handler.obtainMessage();
            message.obj = sb.toString();
            message.what = 1001;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void sendMsgToDevice(String msgStr){
        try {
            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
            if (device == null) {
                return;
            }
            String deviceName = device.getDeviceName();
            int deviceType = device.getDeviceType();

            if(DeviceTypeUtil.isContainF18(deviceName)){
                sendF18SmsData(msgStr);
                return;
            }

            Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));

            if (watch_w516_notifyModelByDeviceId == null) {
                return;
            }
            boolean isMessageSwitch = watch_w516_notifyModelByDeviceId.getMsgSwitch();

            if (!isMessageSwitch) {
                return;
            }
            String msgTitle = StringUtils.substringBefore(msgStr,":");
            String msgContent = StringUtils.substringAfter(msgStr,":");
            Log.e(TAG, "-----短信推送=" + msgStr );

            //判断是否是华为手机

            if (DeviceTypeUtil.isContainW55X(deviceType)) {

                Watch_W516_SleepAndNoDisturbModel watchDNDBean = Watch_W516_SleepAndNoDisturbModelAction.findWatch_W516_SleepAndNoDisturbModelyDeviceId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), device.getDeviceName());
                //没有设置勿扰模式
                if (watchDNDBean == null) {
                    ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, msgTitle, msgContent, deviceType == JkConfiguration.DeviceType.Watch_W560 ? 1 : 2);
                    return;
                }
                //勿扰模式未打开
                if (!watchDNDBean.getOpenNoDisturb()) {
                    ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, msgTitle, msgContent, deviceType == JkConfiguration.DeviceType.Watch_W560 ? 1 : 2);
                    return;
                }
                //开始时间和结束时间
                String startTime = watchDNDBean.getNoDisturbStartTime();
                String endTime = watchDNDBean.getNoDisturbEndTime();

                //判断是否在区间内
                if (DateTimeUtils.isComparisonWith(startTime, endTime))
                    return;

                ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, msgTitle, msgContent, deviceType == JkConfiguration.DeviceType.Watch_W560 ? 1 : 2);

                return;
            }


            if (DeviceTypeUtil.isContainW81(deviceType)) {
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, msgTitle + ":" + msgContent, CRPBleMessageType.MESSAGE_SMS);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendF18SmsData(String msgStr){
        Watch7018Manager.getWatch7018Manager().sendNoticeToDevice((byte) 0x04,"",msgStr);
    }





    private void getReceiverMsg(Context context) {
        try {
//            if(ContextCompat.checkSelfPermission(BaseApp.getApp(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
//                return;
            if(ContextCompat.checkSelfPermission(BaseApp.getApp(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED){
                //ActivityCompat.requestPermissions(BaseApp.getApp(),new String[]{Manifest.permission.READ_SMS},0x00);
                return;
            }

            SmsInfo firstSmsInfo = SmsUtils.getFirstSmsInfo(context);
            String senderName = firstSmsInfo.getPerson();
            String msgTxt = firstSmsInfo.getBody();
            String senderNumber = firstSmsInfo.getAddress();
            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
            if (device == null) {
                return;
            }
            String deviceName = device.deviceName;
            int deviceType = device.deviceType;
            // 同步解绑的逻辑
            Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));

            if (watch_w516_notifyModelByDeviceId == null) {
                return;
            }
            boolean isMessageSwitch = watch_w516_notifyModelByDeviceId.getMsgSwitch();

            if (!isMessageSwitch) {
                return;
            }
            Log.e(TAG, "-----短信推送=" + senderNumber + " " + msgTxt);

            //判断是否是华为手机

                if (DeviceTypeUtil.isContainW55X(deviceType)) {
                    if (!TextUtils.isEmpty(senderName)) {
                        senderNumber = senderName;
                    }


                    Watch_W516_SleepAndNoDisturbModel watchDNDBean = Watch_W516_SleepAndNoDisturbModelAction.findWatch_W516_SleepAndNoDisturbModelyDeviceId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), device.getDeviceName());
                    //没有设置勿扰模式
                    if (watchDNDBean == null) {
                        ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, senderNumber, msgTxt, deviceType == JkConfiguration.DeviceType.Watch_W560 ? 1 : 2);
                        return;
                    }
                    //勿扰模式未打开
                    if (!watchDNDBean.getOpenNoDisturb()) {
                        ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, senderNumber, msgTxt, deviceType == JkConfiguration.DeviceType.Watch_W560 ? 1 : 2);
                        return;
                    }
                    //开始时间和结束时间
                    String startTime = watchDNDBean.getNoDisturbStartTime();
                    String endTime = watchDNDBean.getNoDisturbEndTime();

                    //判断是否在区间内
                    if (DateTimeUtils.isComparisonWith(startTime, endTime))
                        return;

                    ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, senderNumber, msgTxt, deviceType == JkConfiguration.DeviceType.Watch_W560 ? 1 : 2);

                    return;
                }


                if (DeviceTypeUtil.isContainW81(deviceType)) {
                    ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, senderNumber + ":" + msgTxt, CRPBleMessageType.MESSAGE_SMS);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
