package com.isport.brandapp.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.DeviceTypeTableAction;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.Apputils;
import com.isport.brandapp.bean.AppVersionInfoBean;
import com.isport.brandapp.blue.NotificationService;
import com.isport.brandapp.device.f18.dial.F18DialBean;
import com.isport.brandapp.home.fragment.FragmentSport;
import com.isport.brandapp.home.fragment.FragmnetMainDeviceList;
import com.isport.brandapp.home.fragment.NewMineFragment;
import com.isport.brandapp.home.fragment.ScaleDialog;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.message.MessageCount;
import com.isport.brandapp.net.APIService;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.sport.bean.SportSumData;
import com.isport.brandapp.sport.location.LocationServiceHelper;
import com.isport.brandapp.sport.present.EndSportPresent;
import com.isport.brandapp.sport.run.LanguageUtil;
import com.isport.brandapp.sport.view.EndSportView;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.util.UserAcacheUtil;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.uber.autodispose.AutoDisposeConverter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.action.SportDataAction;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonnet.net.RxScheduler;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.LoginOutObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.socialmodule.fragment.FragmentCommunity;
import phone.gym.jkcq.com.socialmodule.util.CacheUtil;


public class MainActivity extends BaseActivity implements OnClickListener {


    RadioButton btnData, btnMine, btnSport, btnCommunity, rbtn_empty;
    RadioGroup rg_main;
    View view_message_point;
    FragmentCommunity fragmentCommunity;
    // FragmentChangeMine fragmentMine;
    NewMineFragment fragmentMine;
    FragmnetMainDeviceList fragmentData;
    // FragmentNewData fragmentData;
    FragmentSport fragmentSport;


    Fragment currentFragment;
    FragmentManager mFragmentManager;
    private long mExitTime;

    Intent serviceIntent;

    Button startAllStep;
    TextView tvSteps;
    LocationServiceHelper locationServiceHelper;

    private List<Fragment> fragmentList = new ArrayList<>();

    AppUpdateDialogView appUpdateDialogView;


    private AlertDialog.Builder permissionDialog;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
        AppConfiguration.isScaleScan = false;
        AppConfiguration.isScaleRealTime = false;
        AppConfiguration.isScaleConnectting = false;
        // restartServiceIfNeed();
        Constants.isDFU = false;//不是升级模式
        if (currentFragment instanceof FragmentCommunity) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_community));
        } else {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_other));
        }
        checkMessage();

    }

    private static final String MINE_FRAGMENT_KEY = "fragmentMine";
    private static final String SPORT_FRAGMENT_KEY = "fragmentSport";
    private static final String DATA_FRAGMENT_KEY = "fragmentData";
    private static final String COMMUNITY_FRAGMENT_KEY = "fragmentCommunity";

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        if (fragmentMine != null) {
            getSupportFragmentManager().putFragment(outState, MINE_FRAGMENT_KEY, fragmentMine);
        }
        if (fragmentSport != null) {
            getSupportFragmentManager().putFragment(outState, SPORT_FRAGMENT_KEY, fragmentSport);
        }
        if (fragmentCommunity != null) {
            getSupportFragmentManager().putFragment(outState, COMMUNITY_FRAGMENT_KEY, fragmentCommunity);
        }
        if (fragmentData != null) {
            getSupportFragmentManager().putFragment(outState, DATA_FRAGMENT_KEY, fragmentData);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.isport.blelibrary.utils.Logger.myLog("onNewIntent onCreate" + savedInstanceState);
        if (mFragmentManager == null) {
            mFragmentManager = this.getSupportFragmentManager();
        }
        if (savedInstanceState != null) {
            fragmentMine = (NewMineFragment) mFragmentManager.findFragmentByTag
                    (MINE_FRAGMENT_KEY);
            fragmentSport = (FragmentSport) mFragmentManager.findFragmentByTag
                    (SPORT_FRAGMENT_KEY);
            fragmentData = (FragmnetMainDeviceList) mFragmentManager.findFragmentByTag
                    (DATA_FRAGMENT_KEY);
            fragmentCommunity = (FragmentCommunity) mFragmentManager.findFragmentByTag
                    (COMMUNITY_FRAGMENT_KEY);

            addToList(fragmentMine);
            addToList(fragmentSport);
            addToList(fragmentData);
            addToList(fragmentCommunity);
        } else {
            initFragment();

            //  isSave = false;
        }
        view_message_point = findViewById(R.id.view_message_point);

        requestPermission();
    }

    private void addToList(Fragment fragment) {
        if (fragment != null) {
            fragmentList.add(fragment);
        }

        Logger.e("fragmentList数量" + fragmentList.size());
    }

    /**
     * 某些手机会杀掉下面的服务
     */


    /*添加fragment*/
    private void addFragment(Fragment fragment) {

        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded()) {
            mFragmentManager.beginTransaction().add(R.id.frament, fragment).commitAllowingStateLoss();
            /*添加到 fragmentList*/
            fragmentList.add(fragment);
        }
    }

    /*显示fragment*/
    private void showFragment(Fragment fragment) {


        Logger.e("fragmentList數量：" + fragmentList.size());
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                /*先隐藏其他fragment*/
                Logger.e("隱藏" + fragment);
                mFragmentManager.beginTransaction().hide(frag).commitAllowingStateLoss();
            }
        }
        currentFragment = fragment;
        mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.isport.blelibrary.utils.Logger.myLog("onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_main;
    }

    @Override
    protected void initView(View view) {
        btnData = view.findViewById(R.id.tv_home_data);
        btnCommunity = view.findViewById(R.id.tv_home_community);
        // btnShop = view.findViewById(R.id.tv_home_shop);
        btnSport = view.findViewById(R.id.tv_home_sport);
        btnMine = view.findViewById(R.id.tv_mine);
       /* if(b)
        btnSport.setVisibility(View.VISIBLE);*/

        startAllStep = view.findViewById(R.id.stepArrayButton);
        tvSteps = view.findViewById(R.id.steps);
        rg_main = view.findViewById(R.id.rg_main);
        rbtn_empty = view.findViewById(R.id.rbtn_empty);
    }

    @Override
    protected void initData() {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                CacheUtil.deleteVideoCache();
            }
        });
        if (NotificationService.isEnabled(this)) {
            NotificationService.toggleNotificationListenerService(this);
        }
        locationServiceHelper = new LocationServiceHelper(this);
        locationServiceHelper.startLocation();

        btnData.setChecked(true);
        SyncCacheUtils.setUnBindState(false);


        requestChangeBatteryOptimizations();
        if(XXPermissions.isPermanentDenied(this,Manifest.permission.ACCESS_FINE_LOCATION))
           showPermissDialog();
        //checkNet();

        try {
          /*  serviceIntent = new Intent(this, TimingService.class);
            startService(serviceIntent);*/
        } catch (Exception e) {

        } finally {
            //startTimerService();
            //       showFragment(newFragmentMine);


            brandapp.isport.com.basicres.entry.SportBean sportBean = SportDataAction.getFisrt();
            if (sportBean != null) {
                Logger.e(sportBean.toString());
                if (TextUtils.isEmpty(sportBean.getPublicId())) {

                    App.saveSportDtail(sportBean.getId(), sportBean.getPublicId());

                } else {


                    EndSportPresent endSportPresent;
                    endSportPresent = new EndSportPresent(new EndSportView() {
                        @Override
                        public void successSummarData(SportSumData sumData) {

                            App.saveSportSummar(sportBean.getId(), sumData);
                        }

                        @Override
                        public void onRespondError(String message) {

                        }

                        @Override
                        public <T> AutoDisposeConverter<T> bindAutoDispose() {
                            return null;
                        }
                    });
                    endSportPresent.getDbSummerData(sportBean.getId());
                }
                //
            } else {
                Logger.e("sport is null");
                //没有需要上传的数据
            }
        }


    }


    @Override
    protected void initEvent() {
        btnData.setOnClickListener(this);
        btnSport.setOnClickListener(this);
        btnCommunity.setOnClickListener(this);
        btnMine.setOnClickListener(this);
       /* IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);*/
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void initHeader() {
    }


    @Override
    public void onClick(View v) {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (v.getId()) {
                case R.id.tv_home_data:
                    btnMine.setChecked(false);
                    btnData.setChecked(true);
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_other));
                    if (fragmentData == null) {
                        fragmentData = new FragmnetMainDeviceList();
                    }
                    addFragment(fragmentData);
                    showFragment(fragmentData);
                    checkMessage();
                    JkConfiguration.sCurrentFragmentTAG = JkConfiguration.FRAGMENT_DATA;
                    GSYVideoManager.releaseAllVideos();
                    break;
                case R.id.tv_home_community:
                    btnMine.setChecked(false);
                    btnCommunity.setChecked(true);
                    if (fragmentCommunity == null) {
                        fragmentCommunity = new FragmentCommunity();
                    }
                    addFragment(fragmentCommunity);
                    showFragment(fragmentCommunity);
                    checkMessage();
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_community));
                    JkConfiguration.sCurrentFragmentTAG = JkConfiguration.FRAGMENT_COMMUNITY;


                    break;
                case R.id.tv_home_sport:
                    btnMine.setChecked(false);
                    btnSport.setChecked(true);
                    if (fragmentSport == null) {
                        fragmentSport = new FragmentSport();
                    }
                    checkMessage();
                    addFragment(fragmentSport);
                    showFragment(fragmentSport);
                    JkConfiguration.sCurrentFragmentTAG = JkConfiguration.FRAGMENT_SPORT;
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_other));
                    GSYVideoManager.releaseAllVideos();
                    break;
                case R.id.tv_mine:
                    if (fragmentMine == null) {
                        fragmentMine = new NewMineFragment();
                    }
                    btnData.setChecked(false);
                    btnSport.setChecked(false);
                    btnCommunity.setChecked(false);
                    btnMine.setChecked(true);
                    rbtn_empty.setChecked(true);
                    addFragment(fragmentMine);
                    showFragment(fragmentMine);
                    checkMessage();
                    //fragmentMine.setmMessageCount(mMessageCounts);
                    JkConfiguration.sCurrentFragmentTAG = JkConfiguration.FRAGMENT_MINE;
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_other));
                    GSYVideoManager.releaseAllVideos();
                    break;
                default:
                    break;
            }
            ft.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    boolean isShow = false;


    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.show_sport_location:
                isShow = false;
                break;
            case MessageEvent.update_location:
                if (isShow) {
                    locationServiceHelper.stopLocation();
                }

                break;
            case MessageEvent.SHOW_SPORT:
                if (fragmentSport == null) {
                    fragmentSport = new FragmentSport();
                }
                addFragment(fragmentSport);
                showFragment(fragmentSport);
                   /* ft.hide(currentFragment);
                    ft.show(fragmentSport);
                    currentFragment = fragmentSport;*/
                JkConfiguration.sCurrentFragmentTAG = JkConfiguration.FRAGMENT_SPORT;
                EventBus.getDefault().post(new MessageEvent(MessageEvent.show_fragment_other));
                btnSport.setChecked(true);
                btnData.setChecked(false);
                GSYVideoManager.releaseAllVideos();
                break;
            case MessageEvent.NEED_LOGIN:
                ToastUtils.showToast(context, UIUtils.getString(com.isport.brandapp.basicres.R.string.login_again));
                NetProgressObservable.getInstance().hide();
                BleProgressObservable.getInstance().hide();
                TokenUtil.getInstance().clear(context);
                UserAcacheUtil.clearAll();
                AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
                App.initAppState();
                Intent intent = new Intent(context, ActivityLogin.class);
                context.startActivity(intent);
                ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
                break;
            default:
                break;
        }
    }


    private void initFragment() {
        /* 默认显示home  fragment*/

        fragmentData = new FragmnetMainDeviceList();
        addFragment(fragmentData);
        showFragment(fragmentData);


    }

//
//    @Override
//    public void onBackPressed() {
//        if ((System.currentTimeMillis() - mExitTime) > 2000) {
//            mExitTime = System.currentTimeMillis();
//            ToastUtils.showToast(context, R.string.press_again_exit);
//        } else {
//            ISportAgent.getInstance().disConDevice(false);
//            ISportAgent.getInstance().exit();
//            System.exit(0);
//            finish();
//        }
//    }

//
//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//        super.onBackPressed();
//    }
//
//    private long extTime = 0L;
//
//
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mExitTime = System.currentTimeMillis();
                ToastUtils.showToast(context, R.string.press_again_exit);
                return true;
            } else {
                ISportAgent.getInstance().disConDevice(false);
                ISportAgent.getInstance().exit();
                System.exit(0);
                finish();
            }


        }

//        else if (keyCode == KeyEvent.KEYCODE_MENU) {
//            moveTaskToBack(true);
//        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
//            moveTaskToBack(true);
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationServiceHelper!=null) {
            locationServiceHelper.stopLocation();
        }
        // stopService(serviceIntent);
        ISportAgent.getInstance().cancelLeScan();
        ISportAgent.getInstance().disConDevice(false);
        ISportAgent.getInstance().exit();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);
        if (o instanceof LoginOutObservable) {
            //删除所有的设备缓存
            DeviceTypeTableAction.deleteAllDevices();
            Message msg = (Message) arg;
            switch (msg.what) {
                case LoginOutObservable.SHOW_SCALE_TIPS:
                    showLoginOutDiolags();
                    break;
                case LoginOutObservable.DISMISS_SCALE_TIPS:
                    dismissScaleProgressBar();
                    break;
                default:
                    break;
            }
        } else {
            onObserverChange(o, arg);
        }
    }

    ScaleDialog dialog;

    private void showLoginOutDiolags() {
        DeviceTypeUtil.clearDevcieInfo(this);
        TokenUtil.getInstance().clear(context);
        UserAcacheUtil.clearAll();
        AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
        App.initAppState();
    }

    private void dismissScaleProgressBar() {
        if (dialog != null) {
            dialog.cancelDialog();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    /* @TargetApi(Build.VERSION_CODES.M)
     public static boolean isInDozeWhiteList(Context context) {
         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
             return true;
         PowerManager powerManager = context.getSystemService(PowerManager.class);
         return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
     }
 */
    private void requestChangeBatteryOptimizations() {


        if (true) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isIgnoringBatteryOptimizations();
            if (isIgnoringBatteryOptimizations()) {

            } else {
                try {
                    Intent intent = new Intent();
                    String packageName = this.getPackageName();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //
        }

    }


    private void cheackPremission() {
        XXPermissions.with(this)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> list, boolean b) {

                    }
                });
    }


    private int mMessageCounts;

    private void checkMessage() {

        new NetworkBoundResource<MessageCount>() {
            @Override
            public io.reactivex.Observable<MessageCount> getFromDb() {
                return null;
            }

            @Override
            public io.reactivex.Observable<MessageCount> getNoCacheData() {
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
            public io.reactivex.Observable<MessageCount> getRemoteSource() {
                Map<String, String> map = new HashMap();
                map.put("interfaceId", "0");
                map.put("mobile", TokenUtil.getInstance().getPhone(BaseApp.getApp()));
                map.put("userId", TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                return RetrofitClient.getRetrofit().create(APIService.class).getMessageCount(map).compose
                        (RxScheduler.Obs_io_main()).compose(RetrofitClient.transformer);
            }

            @Override
            public void saveRemoteSource(MessageCount bean) {

            }
        }.getAsObservable().subscribe(new BaseObserver<MessageCount>(BaseApp.getApp()) {
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
            public void onNext(MessageCount messageCount) {

                if (messageCount != null) {
                    if (messageCount.getFansNums() > 0) {
                        mMessageCounts = messageCount.getFansNums();
                        if (fragmentMine != null) {
                            fragmentMine.setmMessageCount(mMessageCounts);
                        }
                        view_message_point.setVisibility(View.VISIBLE);
                    } else {
                        if (fragmentMine != null) {
                            fragmentMine.setmMessageCount(0);
                        }
                        mMessageCounts = 0;
                        view_message_point.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
    }




    private void showPermissDialog(){
        permissionDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.tips))
                .setMessage("搜索手机附近的蓝牙手表需要打开手机的位置权限，未打开改权限可能无法搜索到附件的蓝牙设备，是否确认打开?")
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cheackPremission();
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog at = permissionDialog.create();
        at.show();
        Button sureBtn = at.getButton(DialogInterface.BUTTON_POSITIVE);
        Button cancelBtn = at.getButton(DialogInterface.BUTTON_NEGATIVE);

        sureBtn.setTextColor(Color.BLACK);
        cancelBtn.setTextColor(Color.BLACK);
    }


    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            XXPermissions.with(this).permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> list, boolean b) {

                }
            });
        }

        getAppVersionData();
    }



    //获取版本
    private void getAppVersionData(){

        new NetworkBoundResource<AppVersionInfoBean>(){

            @Override
            public io.reactivex.Observable<AppVersionInfoBean> getFromDb() {
                return null;
            }

            @Override
            public io.reactivex.Observable<AppVersionInfoBean> getNoCacheData() {
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
            public io.reactivex.Observable<AppVersionInfoBean> getRemoteSource() {
                InitCommonParms<AppVersionInfoBean, BaseUrl, BaseDbPar> initCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.object = "Android";
                return (io.reactivex.Observable<AppVersionInfoBean>) RetrofitClient.getInstance().post(initCommonParms
                        .setPostBody
                                (!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.APP_VERSION_UPDATE).getPostBody());

            }

            @Override
            public void saveRemoteSource(AppVersionInfoBean remoteSource) {

            }
        }.getAsObservable().subscribe(new Observer<AppVersionInfoBean>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {

            }

            @Override
            public void onNext(@NonNull AppVersionInfoBean appVersionInfoBean) {

                operateVersionStr(appVersionInfoBean);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void operateVersionStr( AppVersionInfoBean appVersionInfoBean){
        try {
           if(appVersionInfoBean == null)
               return;
           //当前app的version
            int currVersionCode = AppUtils.getAppVersionCode();
            Log.e(TAG,"------获取版本="+appVersionInfoBean.toString()+"\n"+currVersionCode);
            //判断是否要升级，server版本号大于本地版本号，要升级
            boolean isUpdate = appVersionInfoBean.getAppVersionCode() > currVersionCode;
            if(!isUpdate)
                return;
            String url = appVersionInfoBean.getUrl();
            appUpdateDialogView = new AppUpdateDialogView(this,R.style.myDialog);
            appUpdateDialogView.show();
            appUpdateDialogView.setLastVersion(getResources().getString(R.string.device_new_version_title)+"V "+appVersionInfoBean.getAppVersionName());
            appUpdateDialogView.setContentMsg(getResources().getString(R.string.log_update)+(LanguageUtil.isZH() ? appVersionInfoBean.getRemark() : appVersionInfoBean.getRemarkEn()));
            appUpdateDialogView.isFocusUpdate(appVersionInfoBean.getUpdateAble() == 2);
            appUpdateDialogView.setCancelable(appVersionInfoBean.getUpdateAble() != 2 );
            appUpdateDialogView.setDownloadUrl(url,appVersionInfoBean.getAppVersionName()+".apk");
            appUpdateDialogView.setOnUpdateListener(new AppUpdateDialogView.OnUpdateListener() {
                @Override
                public void onCancel() {
                    appUpdateDialogView.dismiss();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
