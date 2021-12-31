package com.isport.brandapp.blue;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;


/**
 * 提醒服务  MyNotificationListenerService
 * 通过通知获取APP消息内容，需要打开通知功能
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AlertService extends MyNotificationListenerService {


    private static final String TAG = "AlertService";
    private static final String H8_NAME_TAG = "bozlun";

    //QQ
    private static final String QQ_PACKAGENAME = "com.tencent.mobileqq";
    //QQ急速版
    private static final String QQ_FAST_PACK_NAME = "com.tencent.qqlite";

    //电话的通知,未接来电时的提醒
    private static final String TEL_NOTIFICATION_NAME = "com.android.server.telecom";

    //微信
    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    //微博
    private static final String WEIBO_PACKAGENAME = "com.sina.weibo";
    //Facebook
    private static final String FACEBOOK_PACKAGENAME = "com.facebook.katana";

    private static final String FACEBOOK_PACKAGENAME1 = "com.facebook.orca";
    //twitter
    private static final String TWITTER_PACKAGENAME = "com.twitter.android";
    //Whats
    private static final String WHATS_PACKAGENAME = "com.whatsapp";
    //viber
    private static final String VIBER_PACKAGENAME = "com.viber.voip";
    //instagram
    private static final String INSTANRAM_PACKAGENAME = "com.instagram.android";
    //日历
    private static final String CALENDAR_PACKAGENAME = "com.android.calendar";
    //信息 三星手机信息
    private static final String SAMSUNG_MSG_PACKNAME = "com.samsung.android.messaging";
    private static final String SAMSUNG_MSG_SRVERPCKNAME = "com.samsung.android.communicationservice";
    private static final String MSG_PACKAGENAME = "com.android.mms";//短信系统短信包名
    private static final String SYS_SMS = "com.android.mms.service";//短信 --- vivo Y85A
    private static final String XIAOMI_SMS_PACK_NAME = "com.xiaomi.xmsf";


    private static final String SKYPE_PACKAGENAME = "com.skype.raider";
    private static final String SKYPE_PACKNAME = "com.skype.rover";
    //line
    private static final String LINE_PACKAGENAME = "jp.naver.line.android";
    private static final String LINE_LITE_PACK_NAME = "com.linecorp.linelite";


    //谷歌邮箱
    private static final String GMAIL_PACKAGENAME = "com.google.android.gm";
    //Snapchat：
    private static final String SNAP_PACKAGENAME = "com.snapchat.android";

    //音乐播放器
    //酷狗
    private static final String KUGOU_MUSIC_PACK_NAME = "com.kugou.android";
    //QQ音乐
    private static final String QQ_MUISC_PACK_NAME = "com.tencent.qqmusic";
    //网易云
    private static final String WAGNYI_MUSIC_PACK_NAME = "com.netease.cloudmusic";
    //酷我音乐
    private static final String KUWO_MUSIC_PACK_NAME = "cn.kuwo.player";
    //咪咕音乐
    private static final String MIGU_MUSIC_PACK_NAME = "cmccwm.mobilemusic";
    //铃声多的
    private static final String DUODUO_MUSIC_PACK_NAME = "com.shoujiduoduo.ringtone";
    //喜马拉雅
    private static final String XIMALAYA_MUSIC_NAME = "com.ximalaya.ting.android";
    //虾米音乐
    private static final String XIAMI_MUSIC_NAME = "fm.xiami.main";
    //华为音乐
    private static final String HUAWEI_MUSIC_NAME = "com.android.mediacenter";
    //小米音乐
    private static final String XIAOMI_MUSIC_NAME = "com.miui.player";
    //vivo音乐
    private static final String VIVO_MUSIC_NAME = "com.android.bbkmusic";


    private String[] musicArray =new String[]{KUGOU_MUSIC_PACK_NAME,QQ_MUISC_PACK_NAME,WAGNYI_MUSIC_PACK_NAME,KUWO_MUSIC_PACK_NAME,MIGU_MUSIC_PACK_NAME,DUODUO_MUSIC_PACK_NAME,
            XIMALAYA_MUSIC_NAME,XIAMI_MUSIC_NAME,HUAWEI_MUSIC_NAME,XIAOMI_MUSIC_NAME,VIVO_MUSIC_NAME};

    private AudioManager audioManager;


    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);

    }


    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    //当系统收到新的通知后出发回调
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            //获取应用包名
            String packageName = sbn.getPackageName();
            Log.e(TAG, "------通知包名=" + packageName);
            //获取notification对象
            Notification notification = sbn.getNotification();
            if (notification == null) return;
            String msgCont = null;
            Bundle extras = notification.extras;
            //获取消息内容----标题加内容
            CharSequence tickerText = notification.tickerText;
            if (tickerText != null) {
                msgCont = tickerText.toString();
            }
            String content = "null";
            String title = "null";
            if (msgCont == null) {
                if (extras != null) {
                    // 获取通知标题
                    Object object = extras.get(Notification.EXTRA_TITLE);
                    if (object == null)
                        return;

                    if (object instanceof String) {
                        Object oo = extras.get(Notification.EXTRA_TITLE);
                        Object o2 = extras.get(Notification.EXTRA_TEXT);
                        Log.e(TAG,"-----2220="+oo.toString()+"\n"+o2.toString());
                        title = extras.getString(Notification.EXTRA_TITLE, "");
                        // 获取通知内容
                        content = extras.getString(Notification.EXTRA_TEXT, "");
                        msgCont = title + content;
                    } else {
                        content = object.toString();
                    }
                }
            }
             Log.e(TAG, "-----title-+content=" + title + "--==" + msgCont);
            if (TextUtils.isEmpty(msgCont))
                return;
            if (msgCont.contains("正在运行"))
                return;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //编码
    public static String stringtoUnicode(String string) {
        if (string == null || "".equals(string)) {
            return null;
        }
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }


    //当系统通知被删掉后出发回调
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }


}
