package com.isport.blelibrary.managers;

import java.util.HashMap;

/**
 * @author Created by Marcos Cheng on 2017/4/6.
 */

public class Constants {
    /**
     * if you want to show log, let it be true ,or false
     * before you release the app, set it false first!
     */
    public static boolean IS_DEBUG = true;
    /**
     * if ues the default music control that designed in sdk ,set {@link #MUSIC_DEFAULT} to true, or set to false
     */
    public static boolean MUSIC_DEFAULT = true;

    /**
     * PRODUCT
     */
    public static final String PRODUCT_UFIT = "ufit";
    public static final String PRODUCT_OTHERS = "others";


    public static String defUserId = "0";

    /**
     * W337B only support whatsapp and skype
     */
    public final static String KEY_MESSAGE = "com.android.mms";
    public final static String KEY_13_PACKAGE = "com.tencent.mobileqq";///qq
    public final static String KEY_14_PACKAGE = "com.tencent.mm";///wechat
    public final static String KEY_14B_PACKAGE = "com.tencent.mm";///wechat
    public final static String KEY_15_PACKAGE = "com.skype.raider";///skype
    public final static String KEY_15_PACKAGE_1 = "com.skype.polaris";///skype
    public final static String KEY_15_PACKAGE_2 = "com.skype.rover";///skype
    public final static String KEY_16_PACKAGE = "com.facebook.katana";//facebook
    public final static String KEY_17_PACKAGE = "com.twitter.android";//twitter
    public final static String KEY_18_PACKAGE = "com.linkedin.android";//linkin
    public final static String KEY_19_PACKAGE = "com.instagram.android";//instagram
    public final static String KEY_1A_PACKAGE = "life.inovatyon.ds";
    public final static String KEY_1B_PACKAGE = "com.whatsapp";
    public final static String KEY_KAOKAO_TALK = "com.kakao.talk";
    public final static String MESSGE_OHTERS = "message_ohters";
    public final static String MESSGE_LINE = "jp.naver.line.android";

    public static final String VIBER_PACK= "com.viber.voip"; //viber
    /**
     * add in fireware that version is 89 or up
     */
    public final static String KEY_1C_PACKAGE = "com.facebook.orca";

    public static HashMap<String,Integer> msgTypeMap = new HashMap<>();


    //W560B消息推送标号
    public static HashMap<String,Integer> w560BTypeMap = new HashMap<>();


    //SMS  1
    //信息 三星手机信息 com.samsung.android.messaging
    public static final String SAMSUNG_MSG_PACKNAME = "com.samsung.android.messaging";
    public static final String SAMSUNG_MSG_SRVERPCKNAME = "com.samsung.android.communicationservice";
    public static final String MSG_PACKAGENAME = "com.android.mms";//短信系统短信包名
    public static final String SYS_SMS = "com.android.mms.service";//短信 --- vivo Y85A
    public static final String XIAOMI_SMS_PACK_NAME = "com.xiaomi.xmsf";
    //com.google.android.apps.messaging
    public static final String SMS_PACKAGE_NAME = "com.google.android.apps.messaging";
    //一加手机短信包名
    public static final String SMS_ONEPLUS_PACK_NAME = "com.oneplus.mms";


    //RedBus  2
    public static final String REDBUS_PACKAGE_NAME = "in.redbus.android";
    //Whatsapp  3
    public static final String WHATS_APP_PACKAGE_NAME = "com.whatsapp";
    //Facebook  4
    public static final String FACEBOOK_APP_PACK_NAME = "com.facebook.facebook";
    //Twitter  5
    public static final String TWITTER_APP_PACK_NAME = "com.twitter.android";
    //Messenger  6
    public static final String MESSENGER_APP_PACK_NAME = "com.facebook.orca";
    public static final String MESSENGER_APP_PACK_NAME2 = "com.facebook.mlite";
    //Instagram 7
    public static final String INSTAGRAM_APP_PACK_NAME = "com.instagram.android";
    public static final String INSTAGRAM_APP_PACK_NAME2 = "com.instagram.lite";
    //Linkedin 8
    public static final String LINKED_APP_PACK_NAME = "com.linkedin.android";
    //Calendar 9
    public static final String CALENDAR_APP_PACK_NAME = "com.google.android.calendar";
    //Youtube 10
    public static final String YOUTUBE_APP_PACK_NAME = "com.google.android.youtube";
    //Telegram 11
    public static final String TELEGRAM_APP_PACK_NAME = "org.telegram.messenger";
    //Gmail 12
    public static final String GMAIL_APP_PACK_NAME = "com.google.android.gm";
    public static final String GMAIL_APP_PACK_NAME2 = "com.google.android.gm.lite";

    //Dailyhunt  13
    public static final String DAILYHUNT_APP_PACK_NAME = "com.eterno";
    public static final String DAILYHUNT_APP_PACK_NAME2 = "com.eterno.go";

    //Outlook  14
    public static final String OUT_LOOD_APP_NAME =  "com.microsoft.office.outlook";
    //Snapchat  15
    public static final String SNAPCHAT_APP_PACK_NAME = "com.snapchat.android";
    //Hotstar  16
    public static final String HOSTAR_APP_PACK_NAME = "com.hotsvpnfree";
    //InShorts  17
    public static final String INSORTS_APP_PACK_NAME = "com.nis.app";
    //Paytm  18
    public static final String PATYM_APP_PACK_NAME = "net.one97.paytm";
    //Amazon  19
    public static final String AMAZOM_APP_PACK_NAME = "com.amazon.mShop.android.shopping";
    //Flipkart  20
    public static final String FLIPKART_APP_NAME = "com.flipkart.android";
    public static final String FLIPKART_APP_NAME2 = "com.flipkart.seller";
    //Prime  21
    public static final String PRIME_APP_NAME = "com.amazon.avod.thirdpartyclient";
    //Netflix  22
    public static final String NETFLIX_APP_NAME = "com.netflix.mediaclient";
    //Gpay  23
    public static final String GPAY_APP_NAME = "com.cc.grameenphone";
    //PhonePe  24
    public static final String PHONEPE_APP_NAME = "com.phonepe.app";
    //Swiggy  25
    public static final String SWIGGY_APP_NAME = "in.swiggy.android";
    public static final String SWIGGY_APP_NAME2 = "com.swiggykitchen.disprz";
    //Zomato  26
    public static final String ZOMATO_APP_NAME = "com.application.zomato";
    //Make My Trip  27
    public static final String MAKE_MY_TRIP_APP_NAME = "com.makemytrip";
    public static final String MAKE_MY_TRIP_APP_NAME2 = "com.makemytripseller";
    //Jio Tv  28
    public static final String JIOTV_APP_NAME_APP_NAME = "com.jio.myjio";
    //电话 29
    public static final String CALL_PHONE_APP_NAME = "com.apple.mobilephone";
    // 其它 30
    public static final String OTHER_APP_NAME = "";

    public static final String YT_MUSIC_APP_NAME = "com.google.ios.youtubemusic";
    public static final String OAL_APP_NAME = "olacabs.olacabs";
    public static final String QQ_APP_NAME = "com.tencent.mobileqq";
    public static final String WECHAT_APP_NAME = "com.tencent.mm";

    //Youtubemusic 31
    public static final String YOUTUBE_MUSIC_PACK_NAME = "com.google.android.apps.youtube.music";


    //Uber 32
    public static final String UBER_APP_NAME = "com.ubercab";
    //ola 33
    public static final String OLA_APP_NAME = "com.olacabs.customer";

    //QQ  34
    public static final String QQ_APP_NAME2 = "com.tencent.mobileqq";
    //QQ急速版34
    private static final String QQ_FAST_PACK_NAME = "com.tencent.qqlite";
    //微信  35
    public static final String WECHAT_APP_NAME2= "com.tencent.mm";


    //短信的map
    public static HashMap<String,Integer> smsMap = new HashMap<>();

    static {
        smsMap.put(SMS_PACKAGE_NAME,1); //短信
        smsMap.put(SAMSUNG_MSG_PACKNAME,1);
        smsMap.put(SAMSUNG_MSG_SRVERPCKNAME,1);
        smsMap.put(MSG_PACKAGENAME,1);
        smsMap.put(XIAOMI_SMS_PACK_NAME,1);
        smsMap.put(SMS_ONEPLUS_PACK_NAME,1);
    }


    static {
        w560BTypeMap.put(SMS_PACKAGE_NAME,1); //短信
        w560BTypeMap.put(SAMSUNG_MSG_PACKNAME,1);
        w560BTypeMap.put(SAMSUNG_MSG_SRVERPCKNAME,1);
        w560BTypeMap.put(MSG_PACKAGENAME,1);
        w560BTypeMap.put(XIAOMI_SMS_PACK_NAME,1);
        w560BTypeMap.put(SMS_ONEPLUS_PACK_NAME,1);


        //qq
        w560BTypeMap.put(QQ_APP_NAME2,4);
        w560BTypeMap.put(QQ_APP_NAME,4);
        w560BTypeMap.put(QQ_FAST_PACK_NAME,4);
        //微信
        w560BTypeMap.put(WECHAT_APP_NAME2,3);
    }

    static {
        //短信
        msgTypeMap.put(SMS_PACKAGE_NAME,1); //短信
        msgTypeMap.put(SAMSUNG_MSG_PACKNAME,1);
        msgTypeMap.put(SAMSUNG_MSG_SRVERPCKNAME,1);
        msgTypeMap.put(MSG_PACKAGENAME,1);
        msgTypeMap.put(XIAOMI_SMS_PACK_NAME,1);
        msgTypeMap.put(SMS_ONEPLUS_PACK_NAME,1);

        //RedBus
        msgTypeMap.put(REDBUS_PACKAGE_NAME,2);
        //Whatsapp
        msgTypeMap.put(WHATS_APP_PACKAGE_NAME,3);
        //Facebook
        msgTypeMap.put(FACEBOOK_APP_PACK_NAME,4);
        //Twitter
        msgTypeMap.put(TWITTER_APP_PACK_NAME,5);
        //Messenger
        msgTypeMap.put(MESSENGER_APP_PACK_NAME,6);
        msgTypeMap.put(MESSENGER_APP_PACK_NAME2,6);
        //Instagram
        msgTypeMap.put(INSTAGRAM_APP_PACK_NAME,7);
        msgTypeMap.put(INSTAGRAM_APP_PACK_NAME2,7);
        //Linkedin
        msgTypeMap.put(LINKED_APP_PACK_NAME,8);
        //Calendar
        msgTypeMap.put(CALENDAR_APP_PACK_NAME,9);
        //Youtube
        msgTypeMap.put(YOUTUBE_APP_PACK_NAME,10);
        //Telegram
        msgTypeMap.put(TELEGRAM_APP_PACK_NAME,11);
        //Gmail
        msgTypeMap.put(GMAIL_APP_PACK_NAME,12);
        msgTypeMap.put(GMAIL_APP_PACK_NAME2,12);
        //Dailyhunt
        msgTypeMap.put(DAILYHUNT_APP_PACK_NAME,13);
        msgTypeMap.put(DAILYHUNT_APP_PACK_NAME2,13);
        //Outlook
        msgTypeMap.put(OUT_LOOD_APP_NAME,14);
        //Snapchat
        msgTypeMap.put(SNAPCHAT_APP_PACK_NAME,15);
        //Hotstar
        msgTypeMap.put(HOSTAR_APP_PACK_NAME,16);
        //InShorts
        msgTypeMap.put(INSORTS_APP_PACK_NAME,17);
        //Paytm
        msgTypeMap.put(PATYM_APP_PACK_NAME,18);
        //Amazon
        msgTypeMap.put(AMAZOM_APP_PACK_NAME,19);
        //Flipkart
        msgTypeMap.put(FLIPKART_APP_NAME,20);
        msgTypeMap.put(FLIPKART_APP_NAME2,20);
        //Prime
        msgTypeMap.put(PRIME_APP_NAME,21);
        //Netflix
        msgTypeMap.put(NETFLIX_APP_NAME,22);
        //Gpay
        msgTypeMap.put(GPAY_APP_NAME,23);
        //PhonePe
        msgTypeMap.put(PHONEPE_APP_NAME,24);
        //Swiggy
        msgTypeMap.put(SWIGGY_APP_NAME,25);
        msgTypeMap.put(SWIGGY_APP_NAME2,25);
        //Zomato
        msgTypeMap.put(ZOMATO_APP_NAME,26);
        //Make My Trip
        msgTypeMap.put(MAKE_MY_TRIP_APP_NAME,27);
        msgTypeMap.put(MAKE_MY_TRIP_APP_NAME2,27);
        //Jio Tv
        msgTypeMap.put(JIOTV_APP_NAME_APP_NAME,28);
        //电话
        msgTypeMap.put(CALL_PHONE_APP_NAME,29);
        //other
        msgTypeMap.put(OTHER_APP_NAME,30);

        //youtube music
        msgTypeMap.put(YOUTUBE_MUSIC_PACK_NAME,31);
        //uber
        msgTypeMap.put(UBER_APP_NAME,32);
        //ola
        msgTypeMap.put(OLA_APP_NAME,33);
        //qq
        msgTypeMap.put(QQ_APP_NAME2,34);
        msgTypeMap.put(QQ_APP_NAME,34);
        msgTypeMap.put(QQ_FAST_PACK_NAME,34);
        //微信
        msgTypeMap.put(WECHAT_APP_NAME2,35);

    }




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

    //音乐集合
    public static String[] musicArray = new String[]{KUGOU_MUSIC_PACK_NAME,QQ_MUISC_PACK_NAME,WAGNYI_MUSIC_PACK_NAME,KUWO_MUSIC_PACK_NAME,MIGU_MUSIC_PACK_NAME,DUODUO_MUSIC_PACK_NAME,
            XIMALAYA_MUSIC_NAME,XIAMI_MUSIC_NAME,HUAWEI_MUSIC_NAME,XIAOMI_MUSIC_NAME,VIVO_MUSIC_NAME};




    /**
     * LocalBroadcastManager.getInstance(context).sendBroadcast() to send broadcast,so you
     * must register broadcastReceiver by LocalBroadcastManager if you need get these info of device
     */
    public final static String ACTION_QUERY_SEDENTARY = "com.isport.isportlibrary.Constants.ACTION_QUERY_SEDENTARY";
    /**
     * the extra is a list of sedentary
     */
    public final static String EXTRA_QUERY_SEDENTARY = "com.isport.isportlibrary.Constants.EXTRA_QUERY_SEDENTARY";

    public final static String ACTION_QUERY_ALARM = "com.isport.isportlibrary.Constants.ACTION_QUERY_ALARM";
    /**
     * the extra is a list of alarm
     */
    public final static String EXTRA_QUERY_ALARM = "com.isport.isportlibrary.Constants.EXTRA_QUERY_ALARM";

    public final static String ACTION_QUERY_DISPLAY = "com.isport.isportlibrary.Constants.ACTION_QUERY_DISPLAY";
    /**
     * the extra is the info of display
     */
    public final static String EXTRA_QUERY_DISPLAY = "com.isport.isportlibrary.Constants.EXTRA_QUERY_DISPLAY";

    public final static String ACTION_QUERY_SLEEP = "com.isport.isportlibrary.Constants.ACTION_QUERY_SLEEP";
    /**
     * the extra is the info of sleep
     */
    public final static String EXTRA_QUERY_SLEEP = "com.isport.isportlibrary.Constants.EXTRA_QUERY_SLEEP";

    public final static String ACTION_QUERY_TIMING_HEART_DETECT = "com.isport.isportlibrary.Constants" +
            ".ACTION_QUERY_TIMING_HEART_DETECT";
    /**
     * the extra is the info of timing heart detect
     */
    public final static String EXTRA_QUERY_TIMING_HEART_DETECT = "com.isport.isportlibrary.Constants" +
            ".EXTRA_QUERY_TIMING_HEART_DETECT";





}
