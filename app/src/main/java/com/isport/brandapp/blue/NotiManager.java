package com.isport.brandapp.blue;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;

import com.crrepa.ble.conn.type.CRPBleMessageType;
import com.crrepa.ble.conn.type.CRPDeviceLanguageType;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_SettingModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_NotifyModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SleepAndNoDisturbModelAction;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.entry.NotificationMsg;
import com.isport.blelibrary.managers.Constants;
import com.isport.blelibrary.utils.AppLanguageUtil;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.util.DateTimeUtils;
import com.isport.brandapp.util.DeviceTypeUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


/**
 * Created by Administrator on 2017/9/27.
 */

public class NotiManager {
    Boolean IS_DEBUG = true;

    private static final String TAG = "NotiManager";

    public static String[] pkNames = null;
    public static Map<String, Integer> notiType = null;
    public static HashMap<String, Integer> mapstrPkNames = new HashMap<>();
    public static HashMap<String, Integer> w556mapstrPkNames = new HashMap<>();
    public static HashMap<String, Integer> w81MapstrPkNames = new HashMap<>();

    static {
        w556mapstrPkNames.put(Constants.KEY_14_PACKAGE, 3);
        w556mapstrPkNames.put(Constants.KEY_13_PACKAGE, 4);
        w556mapstrPkNames.put(Constants.KEY_1B_PACKAGE, 5);
        w556mapstrPkNames.put(Constants.KEY_16_PACKAGE, 6);
        w556mapstrPkNames.put(Constants.KEY_KAOKAO_TALK, 7);
        w556mapstrPkNames.put(Constants.MESSGE_LINE, 8);
        w556mapstrPkNames.put(Constants.KEY_15_PACKAGE_2, 9);
        w556mapstrPkNames.put(Constants.KEY_17_PACKAGE, 10);

        //18
        mapstrPkNames.put(Constants.KEY_13_PACKAGE, 19);//老的协议从 13 开始 新的协议从19开始
        mapstrPkNames.put(Constants.KEY_14_PACKAGE, 20);
        mapstrPkNames.put(Constants.KEY_15_PACKAGE, 21);
        mapstrPkNames.put(Constants.KEY_15_PACKAGE_1, 21);
        mapstrPkNames.put(Constants.KEY_15_PACKAGE_2, 21);
        mapstrPkNames.put(Constants.KEY_16_PACKAGE, 22);
        mapstrPkNames.put(Constants.KEY_17_PACKAGE, 23);
        mapstrPkNames.put(Constants.KEY_18_PACKAGE, 24);
        mapstrPkNames.put(Constants.KEY_19_PACKAGE, 25);
        mapstrPkNames.put(Constants.KEY_1A_PACKAGE, 16);
        mapstrPkNames.put(Constants.KEY_1B_PACKAGE, 27);
        mapstrPkNames.put(Constants.KEY_1C_PACKAGE, 28);

       /* w81MapstrPkNames.put(Constants.KEY_13_PACKAGE, CRPBleMessageType.MESSAGE_QQ);
        w81MapstrPkNames.put(Constants.KEY_14_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT);
        w81MapstrPkNames.put(Constants.KEY_14B_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT_IN);
        w81MapstrPkNames.put(Constants.KEY_15_PACKAGE, CRPBleMessageType.MESSAGE_SKYPE);
        w81MapstrPkNames.put(Constants.KEY_15_PACKAGE_1, CRPBleMessageType.MESSAGE_SKYPE);
        w81MapstrPkNames.put(Constants.KEY_15_PACKAGE_2, CRPBleMessageType.MESSAGE_SKYPE);
        w81MapstrPkNames.put(Constants.KEY_16_PACKAGE, CRPBleMessageType.MESSAGE_FACEBOOK);
        w81MapstrPkNames.put(Constants.KEY_17_PACKAGE, CRPBleMessageType.MESSAGE_TWITTER);
        w81MapstrPkNames.put(Constants.MESSGE_LINE, CRPBleMessageType.MESSAGE_LINE);
        w81MapstrPkNames.put(Constants.KEY_19_PACKAGE, CRPBleMessageType.MESSAGE_INSTAGREM);
        w81MapstrPkNames.put(Constants.KEY_1B_PACKAGE, CRPBleMessageType.MESSAGE_WHATSAPP);
        w81MapstrPkNames.put(Constants.KEY_KAOKAO_TALK, CRPBleMessageType.MESSAGE_KAKAOTALK);
        w81MapstrPkNames.put(Constants.MESSGE_OHTERS, CRPBleMessageType.MESSAGE_OTHER);
        */


        //短信
        w81MapstrPkNames.put(Constants.MSG_PACKAGENAME,CRPBleMessageType.MESSAGE_SMS);
        w81MapstrPkNames.put(Constants.XIAOMI_SMS_PACK_NAME,CRPBleMessageType.MESSAGE_SMS);
        w81MapstrPkNames.put(Constants.SAMSUNG_MSG_PACKNAME,CRPBleMessageType.MESSAGE_SMS);
        w81MapstrPkNames.put(Constants.SAMSUNG_MSG_SRVERPCKNAME,CRPBleMessageType.MESSAGE_SMS);
        w81MapstrPkNames.put(Constants.SYS_SMS,CRPBleMessageType.MESSAGE_SMS);
        w81MapstrPkNames.put(Constants.SMS_PACKAGE_NAME,CRPBleMessageType.MESSAGE_SMS);

        w81MapstrPkNames.put(Constants.KEY_13_PACKAGE, CRPBleMessageType.MESSAGE_QQ);
        w81MapstrPkNames.put(Constants.KEY_14_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT);
        w81MapstrPkNames.put(Constants.KEY_14B_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT_IN);
        w81MapstrPkNames.put(Constants.KEY_15_PACKAGE, CRPBleMessageType.MESSAGE_SKYPE);
        w81MapstrPkNames.put(Constants.KEY_15_PACKAGE_1, CRPBleMessageType.MESSAGE_SKYPE);
        w81MapstrPkNames.put(Constants.KEY_15_PACKAGE_2, CRPBleMessageType.MESSAGE_SKYPE);


        w81MapstrPkNames.put(Constants.KEY_16_PACKAGE, CRPBleMessageType.MESSAGE_WHATSAPP);


        w81MapstrPkNames.put(Constants.KEY_17_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT_IN);

        w81MapstrPkNames.put(Constants.MESSGE_LINE, CRPBleMessageType.MESSAGE_LINE);//v
        w81MapstrPkNames.put(Constants.KEY_19_PACKAGE, CRPBleMessageType.MESSAGE_INSTAGREM);

        w81MapstrPkNames.put(Constants.KEY_1B_PACKAGE, CRPBleMessageType.MESSAGE_KAKAOTALK);
        //  w81MapstrPkNames.put(Constants.KEY_KAOKAO_TALK, CRPBleMessageType.MESSAGE_KAKAOTALK);
        w81MapstrPkNames.put(Constants.MESSGE_OHTERS, CRPBleMessageType.MESSAGE_OTHER);


    }


    private Context mContext;
    private static NotiManager sInstance;

    private NotiManager(Context context) {
        this.mContext = context;
    }

    public static NotiManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NotiManager.class) {
                if (sInstance == null) {
                    sInstance = new NotiManager(context.getApplicationContext());

                }
            }
        }
        return sInstance;
    }

    /**
     * handle notification, need to set NotificationEntry, to see {@link } for details
     *
     * @param packagename  whose notifications you want to push to ble device
     * @param notification the notification will be pushed to ble device
     */
    public void handleNotification(String packagename, Notification notification,String smsAction) {
        try {

            Bundle extras = notification.extras;
            if(extras == null)
                return;
            CharSequence tickerText = notification.tickerText;
            SpannableString spannableString;
            String content = "";
            String title = "";

            Object objectStr = extras.get(Notification.EXTRA_TITLE);
            if(objectStr != null){
                if(objectStr instanceof  String){
                    title = extras.getString(Notification.EXTRA_TITLE, "")+"";
                    // 获取通知内容
                    content = extras.getString(Notification.EXTRA_TEXT, "")+"";
                }else{
                    content = objectStr.toString();
                }
            }else{
                if (tickerText != null) {
                    content = tickerText.toString();
                }
            }
            Logger.myLog(TAG,"----title="+title+" content="+content+"\n"+NotificationService.SmsAction);
            String buildName = Build.MANUFACTURER;
            if(title != null && (title.contains("Messaging is running") || title.contains("正在运行") || title.contains("信息未发送")) )
                return;
            boolean isHw =   buildName.equalsIgnoreCase("huawei") || buildName.equalsIgnoreCase("honor");
            //对短信的处理，
            if( NotificationService.SmsAction != null && NotificationService.SmsAction.equals("android.provider.Telephony.SMS_RECEIVED")){
                NotificationService.SmsAction = null;
                return;
            }

            if(buildName!=null){
                if(isHw && Constants.smsMap.containsKey(packagename))
                    return;
            }
            BaseDevice baseDevice = ISportAgent.getInstance().getCurrnetDevice();
            if (!AppConfiguration.isConnected || baseDevice == null) {
                return;
            }

            Logger.myLog(TAG,"-----baseDevice="+baseDevice.toString()+"\n"+packagename);
            //判断是否是W560
            if(DeviceTypeUtil.isContainW55X(baseDevice.deviceType)){
                sendW560MsgTypeMsg(packagename,notification,baseDevice.deviceType,baseDevice.deviceName,smsAction);
                return;
            }

            //MOY 系列
            if(DeviceTypeUtil.isContainW81(baseDevice.deviceType)){
                sendMoYMsgTypeMsg(packagename,title,content,baseDevice.getDeviceName());
                return;
            }

//            ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, title, content, 2);
//

            int currentType = 0;
            String currentDeviceName = "";
            if (AppConfiguration.isConnected && baseDevice != null) {
                currentType = baseDevice.deviceType;
                currentDeviceName = baseDevice.deviceName;
            }
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packagename.toString(), 0);

            boolean pack = false;
            boolean noti = true;
            boolean appnoti = true;
            if (DeviceTypeUtil.isContaintW81(currentType)) {
                pack = w81MapstrPkNames.containsKey(packagename);
            } else if (DeviceTypeUtil.isContainW55X(currentType)) {
                pack = w556mapstrPkNames.containsKey(packagename);
            } else {
                pack = mapstrPkNames.containsKey(packagename);
            }
            Logger.myLog(TAG,"handleNotification:packagename:" + packagename + " pack:" + pack + ",currentType:" + currentType + "noti=" + noti + "appnoti=" + appnoti);
            int type = 0;
            if (DeviceTypeUtil.isContainW55X(currentType)) {
                if (pack && noti && appnoti) {
                    //这里需要加一个判断
                    Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(currentDeviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    if (watch_w516_notifyModelByDeviceId != null)

                        type = w556mapstrPkNames.get(packagename).byteValue();

                    messageW526(packagename, notification, currentType, currentDeviceName, type);
                }
                return;
            }
            if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
                Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(currentDeviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));

                if (watch_w516_notifyModelByDeviceId == null) {
                    return;
                }
                boolean isMessageSwitch = watch_w516_notifyModelByDeviceId.getMsgSwitch();

                if (isMessageSwitch) {
                    return;
                }
                // handlerW311Msg(true, msgTxt, senderNumber, senderName, 18);
                type = mapstrPkNames.get(packagename).byteValue();
                parsMessage(packagename, notification, type, currentType);

                return;
            }
            //W311 -W520 的消息走的逻辑
            // pack = false;
            if (pack && noti && appnoti) {
                if (DeviceTypeUtil.isContaintW81(currentType)) {
                    type = w81MapstrPkNames.get(packagename).byteValue();
                } else {
                    type = mapstrPkNames.get(packagename).byteValue();
                }
                if (AppConfiguration.thridMessageList == null) {
                    return;
                }
                if (!AppConfiguration.thridMessageList.containsKey(packagename)) {
                    //如果打开了其他消息的通知
                    return;
                }
                if (AppConfiguration.thridMessageList.containsKey(packagename)) {
                    if (!AppConfiguration.thridMessageList.get(packagename)) {
                        return;
                    }
                }
                parsMessage(packagename, notification, type, currentType);

            } else {
                if (DeviceTypeUtil.isContainW81(currentType) && AppConfiguration.thridMessageList != null && AppConfiguration.thridMessageList.containsKey(Constants.MESSGE_OHTERS)) {
                    if (AppConfiguration.thridMessageList.get(Constants.MESSGE_OHTERS)) {
                        type = w81MapstrPkNames.get(Constants.MESSGE_OHTERS);
                        parsMessage(packagename, notification, type, currentType);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //魔样系列
    private void sendMoYMsgTypeMsg(String packagename, String tile,String contentStr,String deviceId) {

        try {
            //短信
            if(w81MapstrPkNames.containsKey(packagename)){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+":"+contentStr, CRPBleMessageType.MESSAGE_SMS);
                return;
            }
            Bracelet_W311_ThridMessageModel moySms = Bracelet_W311_SettingModelAction.findBracelet_W311_ThridMessage(deviceId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
            if(moySms == null)
                return;


            //微信
            if(packagename.equals(Constants.WECHAT_APP_NAME) && moySms.getIsWechat()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_WECHAT);
                return;
            }


            //QQ
            if(packagename.equals(Constants.QQ_APP_NAME) && moySms.getIsQQ()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_QQ);
                return;
            }

            //WhatsAPP
            if(packagename.equals(Constants.WHATS_APP_PACKAGE_NAME)){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_WHATSAPP);
                return;
            }

            //facebook
            if(packagename.equals(Constants.FACEBOOK_APP_PACK_NAME) && moySms.getIsFaceBook()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_FACEBOOK);
                return;
            }

            //twitter
            if(packagename.equals(Constants.TWITTER_APP_PACK_NAME) && moySms.getIsTwitter()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_TWITTER);
                return;
            }


            //Skype
            if(packagename.equals(Constants.KEY_15_PACKAGE_1) && moySms.getIsSkype()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_SKYPE);
                return;
            }

            //line
            if(packagename.equals(Constants.MESSGE_LINE) && moySms.getIsLine()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_LINE);
                return;
            }


            //Instagram
            if(packagename.equals(Constants.INSORTS_APP_PACK_NAME) && moySms.getIsInstagram()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_INSTAGREM);
                return;
            }
            //其它
            if(moySms.getIsOthers()){
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message,tile+contentStr, CRPBleMessageType.MESSAGE_OTHER);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void sendW560MsgTypeMsg(String packName,Notification notification,int deviceType,String deviceName,String smsAction){
        try {
            Bundle extras = notification.extras;
            if(extras == null)
                return;
            CharSequence tickerText = notification.tickerText;
            SpannableString spannableString;
            String content = "";
            String title = "";

            Object objectStr = extras.get(Notification.EXTRA_TITLE);
            if(objectStr != null){
                if(objectStr instanceof  String){
                    title = extras.getString(Notification.EXTRA_TITLE, "")+"";
                    // 获取通知内容
                    content = extras.getString(Notification.EXTRA_TEXT, "")+"";
                }else{
                    content = objectStr.toString();
                }
            }else{
                if (tickerText != null) {
                    content = tickerText.toString();
                }
            }
            Logger.myLog(TAG,"----title="+title+" content="+content);

            if(title != null && (title.contains("Messaging is running") || title.contains("正在运行")) )
                return;
            //过滤音乐
            if(Arrays.asList(Constants.musicArray).contains(packName)){
                Logger.myLog(TAG,"-----过滤音乐="+packName+" "+title+content);
                ISportAgent.getInstance().setW560DeviceMusicData(title+content,"0","0");
                return;
            }

            if(Constants.msgTypeMap.containsKey(packName)){
                String buildName = Build.MANUFACTURER;

                Logger.myLog(TAG,"--bname="+buildName+"\n"+(buildName.equalsIgnoreCase("huawei")));
//                if(packName.equals(Constants.MSG_PACKAGENAME)){
//                    return;
//
//                }

                //处理560B的勿扰模式
                if(deviceType == IDeviceType.TYPE_WATCH_W560B || deviceType == IDeviceType.TYPE_WATCH_W560){
                    Watch_W516_SleepAndNoDisturbModel watchDNDBean = Watch_W516_SleepAndNoDisturbModelAction.findWatch_W516_SleepAndNoDisturbModelyDeviceId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), deviceName);
                    //没有设置勿扰模式
                    if(watchDNDBean ==null){
                        ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, title, content, deviceType == IDeviceType.TYPE_WATCH_W560 ? Constants.msgTypeMap.get(packName) : Constants.w560BTypeMap.get(packName));
                        return;
                    }
                    //勿扰模式未打开
                    if(!watchDNDBean.getOpenNoDisturb()){
                        ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, title, content,  deviceType == IDeviceType.TYPE_WATCH_W560 ? Constants.msgTypeMap.get(packName) : Constants.w560BTypeMap.get(packName));
                        return;
                    }
                    //开始时间和结束时间
                    String startTime = watchDNDBean.getNoDisturbStartTime();
                    String endTime = watchDNDBean.getNoDisturbEndTime();

                    //判断是否在区间内
                    if(DateTimeUtils.isComparisonWith(startTime,endTime))
                        return;

                    ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, title, content,  deviceType == IDeviceType.TYPE_WATCH_W560 ? Constants.msgTypeMap.get(packName) : Constants.w560BTypeMap.get(packName));
                    return;
                }

                ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, title, content,  Constants.w560BTypeMap.get(packName));
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }





    private void messageW526(String packagename, Notification notification, int currentType, String currentDeviceName, int type) {
        Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(currentDeviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));

        Logger.myLog("messageW526 :watch_w516_notifyModelByDeviceId" + watch_w516_notifyModelByDeviceId);

        if (watch_w516_notifyModelByDeviceId == null) {
            return;
        }
        boolean isMessageSwitch = watch_w516_notifyModelByDeviceId.getMsgSwitch();
        Logger.myLog("messageW526 :isMessageSwitch" + isMessageSwitch);

        if (isMessageSwitch == false) {
            return;
        }
        Logger.myLog("messageW526 :isMessageSwitch" + isMessageSwitch);
        //type = mapstrPkNames.get(packagename).byteValue();
        //现在默认都走2
        parsMessage(packagename, notification, type, currentType);
    }

    public void parsMessage(String packagename, Notification notification, int meesageType, int deviceType) {
        //Logger.myLog("messageW526 :isMessageSwitch parsMessage");

        // Logger.myLog("handleNotification:packagename switch 打开" + packagename + ":AppConfiguration.thridMessageList.get(packagename):" + AppConfiguration.thridMessageList.get(packagename));
        boolean isopen_detail = true;
        String tickerText = null;
        String title = null;
        String text = null;
        boolean isKitKat = false;
        if (notification != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                isKitKat = true;
                Bundle bundle = notification.extras;
                text = bundle.getString(Notification.EXTRA_TEXT);////notifitication content
                title = bundle.getString(Notification.EXTRA_TITLE);///notification title
                CharSequence[] sequences = bundle.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
                if (sequences != null && sequences.length > 0) {
                    text = sequences[sequences.length - 1].toString();
                }
                if (text == null || title == null) {
                    isKitKat = false;
                    tickerText = (notification.tickerText == null ? "" : notification.tickerText.toString());
                }
            } else {
                tickerText = (notification.tickerText == null ? "" : notification.tickerText.toString());
            }
            //对WhatsApp的特殊处理
//                    ***isKitKat***true***text***‪+86 177 2047 9861‬:
// Hhh***title***WhatsApp***tickerText***null***type***27
            Log.e(TAG, "00000000***isKitKat***" + isKitKat + "***text***" + text + "***title***" + title +
                    "***tickerText***" + tickerText + "***type***" + meesageType);
        } else {
            tickerText = "";
        }
        Log.e(TAG, "111111111***isKitKat***" + isKitKat + "***text***" + text + "***title***" + title + "***tickerText***" + tickerText + "***type***" + meesageType + "deviceType:" + deviceType);
        try {
            if (DeviceTypeUtil.isContainW55X(deviceType)) {
                ISportAgent.getInstance().requestBle(BleRequest.w526_send_message, title, text, meesageType);
            } else if (DeviceTypeUtil.isContaintW81(deviceType)) {
                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(text)) {
                    return;
                }

             /*   w81MapstrPkNames.put(Constants.KEY_14_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT);
                w81MapstrPkNames.put(Constants.KEY_14B_PACKAGE, CRPBleMessageType.MESSAGE_WECHAT_IN);*/
                if (meesageType == CRPBleMessageType.MESSAGE_WECHAT_IN) {
                    if (AppLanguageUtil.sendTypeLanguage() == CRPDeviceLanguageType.LANGUAGE_CHINESE) {
                        meesageType = CRPBleMessageType.MESSAGE_WECHAT;
                    } else {
                        meesageType = CRPBleMessageType.MESSAGE_WECHAT;
                    }
                }
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, title + ":" + text, meesageType);
            } else {
                handlerW311Msg(isKitKat, text, title, tickerText, meesageType);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

//    public boolean isGB2312(String str){
//        char[] chars=str.toCharArray();
//        boolean isGB2312=false;
//        for(int i=0;i<chars.length;i++){
//            byte[] bytes=(""+chars[i]).getBytes();
//            if(bytes.length==2){
//                int[] ints=new int[2];
//                ints[0]=bytes[0]& 0xff;
//                ints[1]=bytes[1]& 0xff;
//                if(ints[0]>=0x81 && ints[0]<=0xFE && ints[1]>=0x40 && ints[1]<=0xFE){
//                    isGB2312=true;
//                    break;
//                }
//            }
//        }
//        return isGB2312;
//    }


    private void handlerW311Msg(boolean isKitKat, String text, String title, String tickerText, int type) throws
            UnsupportedEncodingException {
//                ***text ***Vghh ***title ***‪+86 177 2047 9861‬: ​***tickerText ***null ***type ***27
//                ***text ***gh ***title ***Test(‎3条信息):madbug ***tickerText ***null ***type ***27
//                ***text ***madbug @Test :gh ***title ***WhatsApp ***tickerText ***null ***type ***27
        byte[] btTicket = null;
        if (tickerText != null) {
            btTicket = tickerText.getBytes("UTF-8");
        }
        byte[] content = new byte[60];//所有包括title和content最大60的长度

        //todo 内容和title都未去掉空格
        if (isKitKat) {
         /*   if (CmdController.getInstance(mContext).getBaseDevice() != null) {
                int deviceType = CmdController.getInstance(mContext).getBaseDevice().getDeviceType();
                String deviceName = CmdController.getInstance(mContext).getBaseDevice().getName();
                String tpName = (deviceName == null ? "" : deviceName.contains("_") ? deviceName.split("_")[0] : deviceName.contains("-") ?
                        deviceName.split("-")[0] : deviceName.split(" ")[0]).toLowerCase();
                float version = CmdController.getInstance(mContext).getVersion();*/
//            if (BaseDevice.TYPE_W307S==deviceType){
//                //todo  费费客户307s要求title和content衔接在一起
//            }
            Log.e(TAG, "**是REFLEX发送**" + " title == " + title + " text == " + text);
            String needSendStr;
            needSendStr = title + " " + text;
            StringBuilder needSendStrbuilder = new StringBuilder();
            for (int i = 0; i < needSendStr.length(); i++) {
                String bb = needSendStr.substring(i, i + 1);
// 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
                if (Utils.isGB2312(bb)) {
                    Log.e(TAG, "**是中文用空格代替 == " + bb);
                    needSendStrbuilder.append(" ");
                } else {
                    Log.e(TAG, "**不是中文直接添加== " + bb);
                    needSendStrbuilder.append(bb);
                }
            }
//                char[] chars = needSendStr.toCharArray();
//                for (int i = 0; i < chars.length; i++) {
//                    if (isGB2312(String.valueOf(chars[i]))){
//                        //是中文，直接用" "空格代替
//                        Log.e(TAG, "**是中文用空格代替 == "+String.valueOf(chars[i]));
//                        needSendStrbuilder.append(" ");
//                    }else{
//                        //不是中文，直接拼接
//                        Log.e(TAG, "**不是中文直接添加== "+String.valueOf(chars[i]));
//                        needSendStrbuilder.append(String.valueOf(chars[i]));
//                    }
//                }
            needSendStr = needSendStrbuilder.toString();
            Log.e(TAG, "**要发送的Str== " + needSendStr);
            byte[] byTextTp = needSendStr.getBytes("UTF-8");
            if (byTextTp.length <= 60) {//当长度小于45时补0
                Log.e(TAG, "**title和content的总长度小于等于60，不足的地方补0**");
                System.arraycopy(byTextTp, 0, content, 0, byTextTp.length);
                content = Utils.addFF(content, byTextTp.length, 59);
            } else {//长度大于45切割最前面的45
                Log.e(TAG, "**title和content的长度大于60，截取60字节**");
                System.arraycopy(byTextTp, 0, content, 0, 59);
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length; i++) {
                builder.append(String.format("%02X ", content[i]));
            }
            Log.e(TAG, "**要去发送**" + builder.toString());
        }
//        byte[] ddd = new byte[15];
//        System.arraycopy(content, 0, ddd, 0, 15);
        // CmdController.getInstance(mContext).sendNotiCmd(new NotificationMsg(type, content));


        if (AppConfiguration.isConnected && ISportAgent.getInstance().getCurrnetDevice() != null) {
            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
            if (DeviceTypeUtil.isContainWrishBrand(device.deviceType)) {
                //判断是否已经打开消息开关
                //在同步中也不能发送消息
                NotificationMsg msg = new NotificationMsg(type, content);
                ISportAgent.getInstance().requestBle(BleRequest.bracelet_send_msg, msg);
            } else if (DeviceTypeUtil.isContaintW81(device.deviceType)) {
                ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, content, type);
            }
        }
    }

    //这里去发送命令

}
