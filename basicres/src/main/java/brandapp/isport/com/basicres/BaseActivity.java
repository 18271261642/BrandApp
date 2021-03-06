package brandapp.isport.com.basicres;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.observe.CmdProgressObservable;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.observe.TakePhotObservable;
import com.isport.brandapp.basicres.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import androidx.core.app.ActivityCompat;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.LoadProgressDialog;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonalertdialog.SyncDataProgressDialog;
import brandapp.isport.com.basicres.commonutil.DownLoadSpeedUtil;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.PackageManagerHelper;
import brandapp.isport.com.basicres.commonutil.SystemBarTintManager;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.TranslucentStatusUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.service.observe.BatteryLowObservable;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.LoginOutObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import brandapp.isport.com.basicres.service.observe.OptionPhotobservable;
import phone.gym.jkcq.com.commonres.common.AllocationApi;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;

public abstract class BaseActivity extends BasicActivity implements Observer {

    public final String TAG = this.getClass().getSimpleName();

    protected Activity context;
    protected BaseApp app;


    protected View contentView;

    private SystemBarTintManager tintManager;

    private Handler handler = new Handler();

    /**
     * ?????????????????????????????????
     */
    public boolean isOnPause = false;

    protected boolean isDestroy;
    //???????????????????????????????????????????????????????????????Activity???????????????????????????false??????onResume??????????????????true
    private boolean clickable = true;


    /**
     * ???????????????????????????????????????????????????????????????
     */
    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (!isTaskRoot() && intent != null && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        initHandler();

        Logger.d("BaseActivity", "onCreate");
        isDestroy = false;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initBase();
        PublicAlertDialog.getInstance().clearShowDialog();
        ActivityManager.getInstance().putActivity(TAG, this);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
//        switch (messageEvent.getMsg()) {
//            case MessageEvent.NEED_LOGIN:
//                ToastUtils.showToast(context, UIUtils.getString(R.string.login_again));
//                NetProgressObservable.getInstance().hide();
//                TokenUtil.getInstance().clear(context);
////                UserAcacheUtil.clearAll();
////                App.initAppState();
//                ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
//                Intent intent = new Intent(context,ActivityLogin.class);
//                context.startActivity(intent);
//                break;
//            default:
//                break;
//        }
    }

    /**
     * ???????????????layout???????????????????????????????????????imgLoader???Activity????????????????????????????????????
     */


    protected Handler mHandlerDeviceSetting;

    private void initHandler() {
        mHandlerDeviceSetting = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x01:
                        BleProgressObservable.getInstance().hide();
                        break;
                    case 0x02:
                        BleProgressObservable.getInstance().show();
                        break;

                }
            }
        };
    }

    private void initBase() {
        context = this;

        app = (BaseApp) getApplication();

        NetProgressObservable.getInstance().addObserver(this);
        BleProgressObservable.getInstance().addObserver(this);
        SyncProgressObservable.getInstance().addObserver(this);
        CmdProgressObservable.getInstance().addObserver(this);

        initBaseView();
        initBaseData();
        initImmersionBar();
        // StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.common_bind_title));
        /*if (contentView == null) {
            return;
        }*/
        initView(contentView);
        initData();
        initEvent();
        initHeader();
    }

    /**
     * ??????????????????
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //???????????????????????????
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .init();
        //ImmersionBar.with(this).navigationBarColor(R.color.title_bg_color).init();
    }

    protected void initBaseView() {
        contentView = setBodyView();
        if (null == contentView) {
            finish();
            return;
        }
        setContentView(contentView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /*if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }*/
        return super.dispatchTouchEvent(ev);
    }

    long lastClickTime;

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 500;
    }

    protected void initBaseData() {

    }

    /**
     * ??????layout Id
     *
     * @return layout Id
     */
    protected abstract int getLayoutId();

    /**
     * find??????
     */
    protected abstract void initView(View view);

    /**
     * ??????????????????View
     *
     * @return ??????
     */
    public View setBodyView() {
        return inflate(getLayoutId());
    }

    /**
     * ????????????
     */
    protected abstract void initData();

    /**
     * ????????????
     */
    protected abstract void initEvent();

    /**
     * ????????????
     *
     * @param resId ?????????id
     */
    public View inflate(int resId) {
        try {
            if (resId <= 0) {
                return null;
            }
            return LayoutInflater.from(this).inflate(resId, null);
        } catch (Exception e) {
            return null;
        }

    }


    protected abstract void initHeader();

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

   /* @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left);
    }*/

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        Logger.d("BaseActivity", "startActivityForResult");
        if (isClickable()) {
            Logger.d("isclickable", "true");
            lockClick();
            //overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left);
        }
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
        LoginOutObservable.getInstance().deleteObserver(this);
        TakePhotObservable.getInstance().deleteObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        LoginOutObservable.getInstance().addObserver(this);
        TakePhotObservable.getInstance().addObserver(this);
        BatteryLowObservable.getInstance().addObserver(this);
        //?????????????????????????????????????????????????????????
        clickable = true;
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    protected boolean isClickable() {
        return clickable;
    }

    /**
     * ????????????
     */
    protected void lockClick() {
        clickable = false;
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        dismissProgressBar();
        dismissSyncProgressBar();
        dismissBleProgressBar();
        NetProgressObservable.getInstance().deleteObserver(this);
        BatteryLowObservable.getInstance().deleteObserver(this);
        BleProgressObservable.getInstance().deleteObserver(this);
        SyncProgressObservable.getInstance().deleteObserver(this);
        CmdProgressObservable.getInstance().deleteObserver(this);
        ActivityManager.getInstance().removeActivity(TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void netError(final Activity activity) {
        if (!AllocationApi.isNetwork && !AllocationApi.isShowHint) {
            // ??????Dialog
            AllocationApi.isShowHint = true;
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("?????? \n\n?????????????????????????????????????????????????????????").setCancelable(false)
//                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.dismiss();
//                            NetUtils.openNet(activity);
//                        }
//                    }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.cancel();
//                }
//            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundResource(R.drawable.common_title_button_bg_selector);
//            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
//            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.common_title_button_bg_selector);

            PublicAlertDialog.getInstance().showDialog("", getResources().getString(R.string.common_no_network), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    NetUtils.openNet(activity);
                }

                @Override
                public void cancel() {

                }
            }, false);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Logger.e("huashao", o.getClass().getName() + " , " + arg.toString());
        if (o instanceof TakePhotObservable) {
            showCamaraActivity();

        } else if (o instanceof LoginOutObservable) {
            Message msg = (Message) arg;
            switch (msg.what) {
                case LoginOutObservable.SHOW_SCALE_TIPS:
                    showLoginOutDiolag();
                    break;
                case LoginOutObservable.DISMISS_SCALE_TIPS:
                    // dismissScaleProgressBar();
                    break;
                default:
                    break;
            }
        } else if (o instanceof CmdProgressObservable && arg instanceof Message) {
            Message msg = (Message) arg;
            switch (msg.what) {
                case NetProgressObservable.SHOW_PROGRESS_BAR:
                    if (msg.obj instanceof String) {
                        showProgress((String) msg.obj, msg.arg1 == 0);
                    } else {
                        showProgress(getResources().getString(R.string.common_please_wait), msg.arg1 == 0);
                    }
                    break;
                case NetProgressObservable.DISMISS_PORGRESS_BAR:
                    dismissProgressBar();
                    break;
                default:
                    break;
            }
        } else if (o instanceof SyncProgressObservable && arg instanceof Message) {

            Message msg = (Message) arg;
            switch (msg.what) {
                case NetProgressObservable.SHOW_PROGRESS_BAR:
                    showSyncProgress((Integer) msg.obj, msg.arg1 == 0);
                    break;
                case NetProgressObservable.DISMISS_PORGRESS_BAR:
                    dismissSyncProgressBar();
                    break;
                default:
                    break;
            }

        } else if (o instanceof NetProgressObservable && arg instanceof Message) {
            Message msg = (Message) arg;
            switch (msg.what) {
                case NetProgressObservable.SHOW_PROGRESS_BAR:
                    if (msg.obj instanceof String) {
                        showProgress((String) msg.obj, msg.arg1 == 0);
                    } else {
                        showProgress(getResources().getString(R.string.common_please_wait), msg.arg1 == 0);
                    }
                    break;
                case NetProgressObservable.DISMISS_PORGRESS_BAR:
                    dismissProgressBar();
                    break;
                default:
                    break;
            }
        } else if (o instanceof BleProgressObservable && arg instanceof Message) {
            Message msg = (Message) arg;
            switch (msg.what) {
                case BleProgressObservable.SHOW_BLE_PROGRESS_BAR:
                    if (msg.obj instanceof String) {
                        showBleProgress((String) msg.obj, msg.arg1 == 0);
                    } else {
                        showBleProgress(getResources().getString(R.string.common_please_wait), msg.arg1 == 0);
                    }
                    break;
                case BleProgressObservable.DISMISS_BLE_PORGRESS_BAR:
                    dismissBleProgressBar();
                    break;
                default:
                    break;
            }
        } else {
            onObserverChange(o, arg);
        }
    }


    public void showToast(String toast) {
        ToastUtils.showToast(context, toast);
    }

    public void showToast(int toast) {
        ToastUtils.showToast(context, toast);
    }

    private LoadProgressDialog netReqProgressBar;
    private LoadProgressDialog bleReqProgressBar;
    private SyncDataProgressDialog syncDataProgressDialog;


    public void showSyncProgress(final int times, final boolean isCancelable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOnPause) {
                    return;
                }
                if (null == syncDataProgressDialog) {
                    syncDataProgressDialog = new SyncDataProgressDialog(BaseActivity.this);
                    //syncProgressObservable.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Context context = ((ContextWrapper)syncDataProgressDialog.getContext()).getBaseContext();
                            if(context instanceof Activity){
                                if ( !((Activity) context).isFinishing() && !syncDataProgressDialog.isShowing()) {
                                    syncDataProgressDialog.show();
                                    syncDataProgressDialog.showProgress(times);
                                }
                            }

                            // ??????????????????????????????dialog????????????????????????
                            syncDataProgressDialog.setCancelable(isCancelable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 0);

            }
        });

    }

    public void showProgress(final String desc, final boolean isCancelable) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isOnPause) {
                        return;
                    }
                    if (null == netReqProgressBar) {
                        netReqProgressBar = new LoadProgressDialog(BaseActivity.this);
                        netReqProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    }
                    netReqProgressBar.setMessage(desc);
                    if (!netReqProgressBar.isShowing()) {
                        netReqProgressBar.show();
                    }
                    // ??????????????????????????????dialog????????????????????????
                    netReqProgressBar.setCancelable(isCancelable);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showBleProgress(final String desc, final boolean isCancelable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOnPause) {
                    return;
                }
                if (null == bleReqProgressBar) {
                    bleReqProgressBar = new LoadProgressDialog(BaseActivity.this);
                    bleReqProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                }
                bleReqProgressBar.setMessage(desc);
                if (!bleReqProgressBar.isShowing()) {
                    bleReqProgressBar.show();
                }
                // ??????????????????????????????dialog????????????????????????
                bleReqProgressBar.setCancelable(isCancelable);
            }
        });

    }

    private void dismissSyncProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (syncDataProgressDialog != null && syncDataProgressDialog.isShowing()) {
                    syncDataProgressDialog.showProgrss100();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Context context = ((ContextWrapper)syncDataProgressDialog.getContext()).getBaseContext();

                            if (syncDataProgressDialog != null && syncDataProgressDialog.isShowing()   ) {
                                if(context instanceof Activity){
                                    if(!((Activity) context).isFinishing())
                                        syncDataProgressDialog.dismiss();
                                }

                            }
                        }
                    }, 500);

                }
            }
        });
    }

    public void dismissProgressBar() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (netReqProgressBar != null && netReqProgressBar.isShowing()) {
                        netReqProgressBar.dismiss();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void dismissBleProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bleReqProgressBar != null && bleReqProgressBar.isShowing()) {
                    bleReqProgressBar.dismiss();
                }
            }
        });

    }


    public void onObserverChange(Observable o, Object arg) {
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public boolean checkNet() {
        if (NetUtils.hasNetwork(BaseApp.getApp())) {

            if (autoGetDownLoadSpeed() != 0) {
                return true;
            } else {
                return false;
            }

            //   return true;
        } else {

            return false;
        }
    }


    private long autoGetDownLoadSpeed() {

        long speed = DownLoadSpeedUtil.getTotalRxBytes(PackageManagerHelper.getPackageUid(BaseApp.getApp(), "com.isport.brandapp"));
        com.isport.blelibrary.utils.Logger.myLog("speed");
        return speed;
        /*DownLoadSpeedUtil.lastTotalRxBytes = DownLoadSpeedUtil.getTotalRxBytes(PackageManagerHelper.getPackageUid(BaseApp.getApp(), "com.isport.brandapp"));
        DownLoadSpeedUtil.lastTimeStamp = System.currentTimeMillis();*/
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    protected boolean setImmersionType() {
        return true;
    }

    /**
     * ?????????View??????????????????????????????
     *
     * @return
     */
    protected boolean setStatusBarPadding() {
        return true;
    }

    /**
     * ?????????????????????
     *
     * @return true ???????????? false ??????
     */
    protected boolean setStatusBarStyle() {
        return true;
    }

    /**
     * ?????????????????????????????????<br/>
     * ????????????????????????
     *
     * @return ?????????????????????
     */
    protected int getStatusBarTintColor() {
        return Color.BLACK;
    }

    /**
     * ????????????????????????<br>
     * 4.4???????????????
     *
     * @param color ???????????????
     */
    protected void setTranslucentStatus(int color) {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }

        if (setStatusBarStyle()) {
            TranslucentStatusUtil.setStatusBarDarkMode(this, true);
        }

        //??????5.0????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !setImmersionType()) {
            return;
        }

        if (null == tintManager) {
            TranslucentStatusUtil.setTranslucentStatus(this, true);
            tintManager = new SystemBarTintManager(this);
        }
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);

        if (!setStatusBarPadding()) {
            return;
        }
        setViewPadding(contentView);
    }

    /**
     * ??????Titlebar padding
     */
    public void setViewPadding(View view) {
        if (null == view) {
            return;
        }
        int height = DisplayUtils.getStatusBarHeight(this);

        if (0 == height) {
            return;
        }
        view.setPadding(view.getPaddingLeft(), height,
                view.getPaddingRight(),
                view.getPaddingBottom());
    }


  /*  private boolean isTopActivity() {
        boolean isTop = false;
        android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(CamaraActivity.class.getName())) {
            isTop = true;
        }
        return isTop;
    }*/

    protected void showCamaraActivity() {


        PackageManager packageManager = this.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {///????????????????????????
            Toast.makeText(BaseApp.getApp(), UIUtils.getString(R.string.take_photo_no_camara_surpport), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //???????????????????????????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(BaseApp.getApp(), UIUtils.getString(R.string.take_photo_no_camara_permission), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //???????????????????????????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Toast.makeText(BaseApp.getApp(), UIUtils.getString(R.string.take_photo_no_storage_permission), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Log.e("mainService", "?????????");
        //???????????????????????????
        //PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        /*//??????PowerManager.WakeLock????????????????????????|??????????????????????????????????????????????????????Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager
                .SCREEN_BRIGHT_WAKE_LOCK, "bright");*/
        // Log.e("mainService", "1111");
        //????????????
        // wl.acquire();
        //Log.e("mainService", "2222");
        //??????????????????????????????
           /* KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");

            //??????
            kl.disableKeyguard();*/

        if (isTopActivity()) {
            com.isport.blelibrary.utils.Logger.myLog("showCamaraActivity isTopActivity");
            OptionPhotobservable.getInstance().takePhoto(true);

        } else {
            com.isport.blelibrary.utils.Logger.myLog("showCamaraActivity CamaraActivity");
            ARouter.getInstance().build("/main/CamaraActivity").navigation();
        }
    }


    private boolean isTopActivity() {
        boolean isTop = false;
        android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains("CamaraActivity")) {
            isTop = true;
        }
        return isTop;
    }


    private void showBatteryDialog() {
        PublicAlertDialog.getInstance().showDialogNoCancle(false, UIUtils.getString(R.string.battery_tips), UIUtils.getString(R.string.battery_content), this, UIUtils.getString(R.string.confirm), new AlertDialogStateCallBack() {
            @Override
            public void determine() {

               /* TokenUtil.getInstance().clear(context);
                Logger.i("showLoginOutDiolag:");*/
                // Intent intent = new Intent(context, ActivityLogin.class);
                //context.startActivity(intent);
                /*ARouter.getInstance().build("/main/LoginActivity").navigation();

                ActivityManager.getInstance().finishAllActivity("ActivityLogin");*/

                // AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);

                //Constants.CAN_RECONNECT=false;
            }

            @Override
            public void cancel() {

            }
        });
    }

    private void showLoginOutDiolag() {
        ISportAgent.getInstance().disConDevice(false);
        Logger.i("showLoginOutDiolag:");
        PublicAlertDialog.getInstance().showDialogNoCancle(false, UIUtils.getString(R.string.loginout_tips), UIUtils.getString(R.string.loginout_content), this, UIUtils.getString(R.string.loginout_button_content), new AlertDialogStateCallBack() {
            @Override
            public void determine() {

                NetProgressObservable.getInstance().hide();
                BleProgressObservable.getInstance().hide();
                TokenUtil.getInstance().clear(context);
                Logger.i("showLoginOutDiolag:");
                // Intent intent = new Intent(context, ActivityLogin.class);
                //context.startActivity(intent);
                ARouter.getInstance().build("/main/LoginActivity").navigation();

                ActivityManager.getInstance().finishAllActivity("ActivityLogin");

                // AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);

                //Constants.CAN_RECONNECT=false;
            }

            @Override
            public void cancel() {

            }
        });
    }


    public void startActivity(Class<?> goalClass){
        Intent intent = new Intent(this,goalClass);
        startActivity(intent);
    }


    public void startActivity(String[] keyStr,String[] value,Class<?> goalClass){
        Intent intent = new Intent(this,goalClass);
        if(keyStr.length == value.length){
            for(int i = 0;i<keyStr.length;i++){
                intent.putExtra(keyStr[i],  value[i]);
            }
        }
        startActivity(intent);
    }

    public void startSerializableActivity(String key, Serializable serializable,Class<?> goalClass){
        Intent intent = new Intent(this,goalClass);
        intent.putExtra(key,serializable);
        startActivity(intent);
    }
}