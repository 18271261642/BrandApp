package brandapp.isport.com.basicres;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
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
     * 判断当前页面是否在后台
     */
    public boolean isOnPause = false;

    protected boolean isDestroy;
    //防止重复点击设置的标志，涉及到点击打开其他Activity时，将该标志设置为false，在onResume事件中设置为true
    private boolean clickable = true;


    /**
     * 记录上次点击返回键的时间，用于控制退出操作
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
     * 初始化资源layout，初始化布局加载器，初始化imgLoader，Activity入栈，查找控件，设置监听
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
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
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
     * 获取layout Id
     *
     * @return layout Id
     */
    protected abstract int getLayoutId();

    /**
     * find控件
     */
    protected abstract void initView(View view);

    /**
     * 设置中间容器View
     *
     * @return 返回
     */
    public View setBodyView() {
        return inflate(getLayoutId());
    }

    /**
     * 数据处理
     */
    protected abstract void initData();

    /**
     * 设置监听
     */
    protected abstract void initEvent();

    /**
     * 填充布局
     *
     * @param resId 控件的id
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
        //每次返回界面时，将点击标志设置为可点击
        clickable = true;
    }


    /**
     * 当前是否可以点击
     *
     * @return
     */
    protected boolean isClickable() {
        return clickable;
    }

    /**
     * 锁定点击
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
            // 显示Dialog
            AllocationApi.isShowHint = true;
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("提示 \n\n目前处于无网络状态，是否立即开启网络！").setCancelable(false)
//                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.dismiss();
//                            NetUtils.openNet(activity);
//                        }
//                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
                            if (!syncDataProgressDialog.isShowing()) {
                                syncDataProgressDialog.show();
                                syncDataProgressDialog.showProgress(times);
                            }
                            // 设置让返回按键失效，dialog以外的空间失效！
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
                // 设置让返回按键失效，dialog以外的空间失效！
                netReqProgressBar.setCancelable(isCancelable);
            }
        });

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
                // 设置让返回按键失效，dialog以外的空间失效！
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
                            if (syncDataProgressDialog != null && syncDataProgressDialog.isShowing()) {
                                syncDataProgressDialog.dismiss();
                            }
                        }
                    }, 500);

                }
            }
        });
    }

    public void dismissProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (netReqProgressBar != null && netReqProgressBar.isShowing()) {
                    netReqProgressBar.dismiss();
                }
            }
        });

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
     * 检查网络状态
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
     * 是否设置沉浸式状态栏
     *
     * @return
     */
    protected boolean setImmersionType() {
        return true;
    }

    /**
     * 是否将View整下下移不遮挡状态栏
     *
     * @return
     */
    protected boolean setStatusBarPadding() {
        return true;
    }

    /**
     * 状态栏风格设置
     *
     * @return true 深色风格 false 白色
     */
    protected boolean setStatusBarStyle() {
        return true;
    }

    /**
     * 获取系统状态栏沉浸颜色<br/>
     * 子类可重写该方法
     *
     * @return 状态栏沉浸颜色
     */
    protected int getStatusBarTintColor() {
        return Color.BLACK;
    }

    /**
     * 设置沉浸式状态栏<br>
     * 4.4以下不支持
     *
     * @param color 状态栏颜色
     */
    protected void setTranslucentStatus(int color) {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }

        if (setStatusBarStyle()) {
            TranslucentStatusUtil.setStatusBarDarkMode(this, true);
        }

        //针对5.0以上判断
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
     * 设置Titlebar padding
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
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {///判断是否支持相机
            Toast.makeText(BaseApp.getApp(), UIUtils.getString(R.string.take_photo_no_camara_surpport), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //检查是否有拍照权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(BaseApp.getApp(), UIUtils.getString(R.string.take_photo_no_camara_permission), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //检查是否有拍照权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Toast.makeText(BaseApp.getApp(), UIUtils.getString(R.string.take_photo_no_storage_permission), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Log.e("mainService", "有权限");
        //获取电源管理器对象
        //PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        /*//获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager
                .SCREEN_BRIGHT_WAKE_LOCK, "bright");*/
        // Log.e("mainService", "1111");
        //点亮屏幕
        // wl.acquire();
        //Log.e("mainService", "2222");
        //得到键盘锁管理器对象
           /* KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");

            //解锁
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


}