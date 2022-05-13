package com.isport.brandapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ApiUtils;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.RequestServer;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.iflytek.cloud.SpeechUtility;
import com.isport.blelibrary.BleConstance;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.blue.CallListener;
import com.isport.brandapp.blue.NotificationService;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.f18.F18ConnectStatusService;
import com.isport.brandapp.net.APIService;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.sport.bean.SportDetailData;
import com.isport.brandapp.sport.bean.SportSumData;
import com.isport.brandapp.sport.modle.SportDataModle;
import com.isport.brandapp.sport.response.SportRepository;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.BleUtil;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.SMSBroadCastReceiver;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import androidx.annotation.NonNull;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonnet.net.RxScheduler;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.SystemUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import brandapp.isport.com.basicres.net.CommonRetrofitClient;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import iknow.android.utils.BaseUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class App extends BaseApp {
    private static long mScaleBindTime;
    private static long mSleepBindTime;
    private static long mWatchBindTime;
    private static long deviceBindTime;
    private static long mBraceletBindTime;

    // UMENG 各个平台的配置，建议放在全局Application或者程序入口M


    private SMSBroadCastReceiver smsBroadcastReceiver;

    private static F18ConnectStatusService f18ConnectStatusService;

    private static App instanceApp;

    @Override
    public void onCreate() {
        super.onCreate();
        instanceApp = this;
        init();
    }



    private void init(){


        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

        SyncCacheUtils.clearSetting(this);

        ISportAgent.getInstance().init(this);

        FileUtil.initFile(this);
        ARouter.init(this);

        initAppState();
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));

        ISportAgent.getInstance().setIsWeight(false);

        if (NotificationService.isEnabled(this)) {
            NotificationService.ensureCollectorRunning(this);
        }

//         WbSdk.install(this,new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));
        // AccessibilityUtil.checkSetting(this, NotifService.class);
//        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(new SmsBroadcastReceiver(), filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleConstance.W560_DIS_CALL_ACTION);
        intentFilter.addAction(BleConstance.W560_PHONE_MUTE_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);




        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        CallListener customPhoneListener = new CallListener(this);
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);



        if(smsBroadcastReceiver == null)
            smsBroadcastReceiver = new SMSBroadCastReceiver();
        registerReceiver(smsBroadcastReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));


        CrashReport.initCrashReport(getApplicationContext(), "93283676c4", false);

        BaseUtils.init(this);

        initUmenData();

        bindF18ConnStatusService();

//        //绑定通知服务
//        Intent intent = new Intent(this, AlertService.class);
//        bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);



        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(true)
                // 设置服务器配置
                .setServer(new RequestServer(CommonRetrofitClient.baseUrl))
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求重试次数
                .setRetryCount(1)
                .setInterceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest,
                                                   @NonNull HttpParams params,
                                                   @NonNull HttpHeaders headers) {
                        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    }
                })
                .into();

        // 设置 Json 解析容错监听
        GsonFactory.setJsonCallback((typeToken, fieldName, jsonToken) -> {
            // 上报到 Bugly 错误列表
            CrashReport.postCatchedException(new IllegalArgumentException(
                    "类型解析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken));
        });


    }


    public static App getInstance(){
        return instanceApp;
    }


    public  F18ConnectStatusService getF18ConnStatusService(){
        if(f18ConnectStatusService == null){
            bindF18ConnStatusService();
        }
        return f18ConnectStatusService;
    }

    private  void bindF18ConnStatusService(){
        Intent intent = new Intent(instanceApp, F18ConnectStatusService.class);
        instanceApp.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
    }


    private  final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                f18ConnectStatusService = ((F18ConnectStatusService.F18StatusBinder)service).getService();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            f18ConnectStatusService = null;
        }
    };


    public static void initAppState() {
        AppConfiguration.isConnected = false;//当前是否已经连接
        AppConfiguration.isFirst = true;//当前是否已经连接
        AppConfiguration.isFirstRealTime = true;
        AppConfiguration.isSleepRealTime = false;
        AppConfiguration.isSleepBind = false;
        AppConfiguration.isWatchMain = false;
        AppConfiguration.isBindList = false;
        AppConfiguration.isScaleScan = false;
        AppConfiguration.isScaleRealTime = false;
        AppConfiguration.isScaleConnectting = false;
        AppConfiguration.hasSynced = false;
        JkConfiguration.AppType = BuildConfig.PRODUCT.equals("db") ? dbType : httpType;
    }

    public static boolean isUserGoogleMap() {
        return false;
    }

    /*
     * 初始化友盟。
     */

    public static boolean isHttp() {
        // BuildConfig.PRODUCT.equals("http")?1:0;
        return BuildConfig.PRODUCT.equals("http");
        //return BuildConfig.PRODUCT.equals("http");
    }

    public static int httpType = 0;
    public static int dbType = 1;


   /* public static boolean isDb(){
        return BuildConfig.PRODUCT.equals("http");
    }*/


    public static int appType() {
        //0是单机版，1是网络版
        return BuildConfig.PRODUCT.equals("db") ? dbType : httpType;

        // return BuildConfig.PRODUCT.equals("http") ? httpType : dbType;
    }

    public static boolean isPerforatedPanel() {
        boolean result;
        String deviceModel = SystemUtils.getDeviceModel();
        Logger.myLog("deviceModel == " + deviceModel);
        if (deviceModel.contains("SM-G8870") || deviceModel.contains("SM-G9730") || deviceModel.contains("SM-G9750") || deviceModel.contains("SM-G9700") || deviceModel.contains("VCE-AL00") || deviceModel.contains("PCT-AL10")) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        Logger.myLog("language=" + language);
        if (language.contains("zh"))
            return true;
        else
            return false;
    }

    public static void setScaleBindTime(long bindTime) {
        mScaleBindTime = bindTime;
    }

    public static long getScaleBindTime() {
        return mScaleBindTime;
    }

    public static void setSleepBindTime(long bindTime) {
        mSleepBindTime = bindTime;
    }

    public static long getSleepBindTime() {
        return mSleepBindTime;
    }

    public static void setWatchBindTime(long bindTime) {
        mWatchBindTime = bindTime;
    }

    public static void setDeviceBindTime(long bindTime) {
        deviceBindTime = bindTime;
    }

    public static long getDeviceBindTime() {
        return deviceBindTime;
    }

    public static void setBraceletBindTime(long bindTime) {
        mBraceletBindTime = bindTime;
    }

    public static long getBraceletBindTime() {
        return mBraceletBindTime;
    }

    public static long getWatchBindTime() {
        return mWatchBindTime;
    }

    public static void saveSportSummar(long id, SportSumData sumData) {
        Observable.create(new ObservableOnSubscribe<SportSumData>() {
            @Override
            public void subscribe(ObservableEmitter<SportSumData> emitter) throws Exception {
                // SportDataModle modle = new SportDataModle();
                //  SportSumData sumData = modle.getSummerData(id);
                emitter.onNext(sumData);
            }
        }).flatMap(new Function<SportSumData, ObservableSource<UpdateSuccessBean>>() {
            @Override
            public ObservableSource<UpdateSuccessBean> apply(SportSumData sumData) throws Exception {
                return SportRepository.addSportSummarrequst(sumData);
            }
        }).flatMap(new Function<UpdateSuccessBean, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(UpdateSuccessBean updateSuccessBean) throws Exception {
                //刷新主页的数据和运动模块的数据
                EventBus.getDefault().post(MessageEvent.SPORT_UPDATE_SUCESS);

                SportDataModle modle = new SportDataModle();
                SportDetailData detailData = modle.getSportDetailDataById(id, updateSuccessBean.getPublicId());
                return SportRepository.addSportDetail(detailData);
            }
        }).subscribe(new BaseObserver<String>(BaseApp.getApp()) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

            @Override
            public void onNext(String s) {
                NetProgressObservable.getInstance().hide();
                if (s.equals("操作成功！")) {
                    SportDataModle modle = new SportDataModle();
                    modle.delectSport(id);
                }
            }
        });
    }

    /**
     * 上传运动
     *
     * @param id
     * @param publicId
     */
    public static void saveSportDtail(long id, String publicId) {
        Observable.create(new ObservableOnSubscribe<SportDetailData>() {
            @Override
            public void subscribe(ObservableEmitter<SportDetailData> emitter) throws Exception {
                SportDataModle modle = new SportDataModle();
                SportDetailData detailData = modle.getSportDetailDataById(id, publicId);
                emitter.onNext(detailData);
            }
        }).flatMap(new Function<SportDetailData, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(SportDetailData sumData) throws Exception {
                //这里有可能会报错

                return SportRepository.addSportDetail(sumData);
            }
        }).subscribe(new BaseObserver<String>(BaseApp.getApp()) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

            @Override
            public void onNext(String s) {
                NetProgressObservable.getInstance().hide();
                if (s.equals("操作成功！")) {
                    SportDataModle modle = new SportDataModle();
                    modle.delectSport(id);
                }
            }
        });
    }


    public static boolean isScale() {
        int currentDeviceType = AppSP.getInt(App.getApp(), AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration
                .DeviceType.WATCH_W516);
        boolean result = false;
        if (currentDeviceType == JkConfiguration.DeviceType.BODYFAT)
            result = true;
        return result;
    }

    public static boolean isWatch() {
        int currentDeviceType = AppSP.getInt(App.getApp(), AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration
                .DeviceType.WATCH_W516);
        boolean result = false;
        if (DeviceTypeUtil.isContainWatch(currentDeviceType))
            result = true;
        return result;
    }

    public static boolean isBracelet() {
        int currentDeviceType = AppSP.getInt(App.getApp(), AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration
                .DeviceType.WATCH_W516);
        boolean result = false;
        if (DeviceTypeUtil.isContainWrishBrand(currentDeviceType))
            result = true;
        return result;
    }

    public static boolean isSleepace() {
        int currentDeviceType = AppSP.getInt(App.getApp(), AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration
                .DeviceType.WATCH_W516);
        boolean result = false;
        if (currentDeviceType == JkConfiguration.DeviceType.SLEEP)
            result = true;
        return result;
    }

    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;
    private boolean isForeground = false;
    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activityAount == 0) {
                //app回到前台
                isForeground = true;
                checkTime();
                //去调用接口

            }
            activityAount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
                isForeground = false;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

    private void checkTime() {

        new NetworkBoundResource<Long>() {
            @Override
            public io.reactivex.Observable<Long> getFromDb() {
                return null;
            }

            @Override
            public io.reactivex.Observable<Long> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public io.reactivex.Observable<Long> getRemoteSource() {
                return RetrofitClient.getRetrofit().create(APIService.class).getNowTime().compose
                        (RxScheduler.Obs_io_main()).compose(RetrofitClient.transformer);
            }

            @Override
            public void saveRemoteSource(Long bean) {

            }
        }.getAsObservable().subscribe(new BaseObserver<Long>(BaseApp.getApp()) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

            @Override
            public void onNext(Long time) {

                // Logger.myLog("gettime=" + time + "System.currentTimeMillis()=" + System.currentTimeMillis() + "------" + Math.abs(time.longValue() - System.currentTimeMillis()));
                if (Math.abs(time.longValue() - System.currentTimeMillis()) > (60 * 1000)) {
                    ToastUtils.showToast(BaseApp.getApp(), UIUtils.getString(R.string.time_error));

                }

            }
        });
    }



    private void initUmenData(){

        UMConfigure.preInit(instanceApp,"5bbdb11cf1f556058a0002b6","yyb");
        boolean isAgree =    AppSP.getBoolean(instanceApp,AppSP.IS_FIRST_OPEN_APP,false);
        if(isAgree){    //用户已经同意了隐私政策后再初始化友盟
            initUm();
        }


    }


    public void initUm(){
        UMConfigure.setLogEnabled(true);
        String fileProvider = "com.isport.brandapp.fileprovider";
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, "5bbdb11cf1f556058a0002b6", "yyb", UMConfigure.DEVICE_TYPE_PHONE,
                null);
        // interval 单位为毫秒，如果想设定为40秒，interval应为 40*1000.
        MobclickAgent.setSessionContinueMillis(30 * 1000);//黑屏，应用后台运行超过30s启动都算一次启动
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        // 微信
        //    public final static String APP_ID_WX = "wx83ad7682b33e28e5";
        //    public final static String APP_SECRET_WX = "d673af9518942cd8ef8490837502c12e";
        PlatformConfig.setWeixin("wx83ad7682b33e28e5", "d673af9518942cd8ef8490837502c12e");
        PlatformConfig.setWXFileProvider(fileProvider);
        // 新浪微博 2511584848 8be44eb4339235c451f978d1059c2763
        PlatformConfig.setSinaWeibo("2511584848", "8be44eb4339235c451f978d1059c2763", "http://sns.whalecloud.com");
        PlatformConfig.setSinaFileProvider(fileProvider);
        // QQ APP ID 1108767316
        //APP KEY bsAfYGPH8dW47RG8
        // PlatformConfig.setQQZone("1108767316", "bsAfYGPH8dW47RG8");
        PlatformConfig.setQQZone("1110159454", "Ziwl5Fje7wi3327f");
        PlatformConfig.setQQFileProvider("com.isport.brandapp.fileProvider");
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            Logger.myLog("APP","-----action="+action);
            if(action.equals(BleConstance.W560_DIS_CALL_ACTION)){   //挂断电话
               int code = intent.getIntExtra(BleConstance.W560_PHONE_STATUS,0);
                Logger.myLog("APP","----    -code="+code);
                if(code == 3){  //静音
                    BleUtil.setPhoneMute(instance);
                }
                if(code == 2){  //挂断
                    BleUtil.dPhone();
                    BleUtil.endCall(instance);
                }
                if(code == 1){  //接
//                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
//                    BleUtil.autoAnswerPhone(instance,telephonyManager);
                }
            }

        }
    };




}
