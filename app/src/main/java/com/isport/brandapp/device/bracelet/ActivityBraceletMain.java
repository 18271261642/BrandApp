package com.isport.brandapp.device.bracelet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crrepa.ble.conn.type.CRPTimeSystemType;
import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24H_hr_SettingModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DeviceInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DisplayModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_WearModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_w311_hrModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.entry.DisplaySet;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.w311.BraceletW311SyncComplete;
import com.isport.blelibrary.result.impl.watch.WatchVersionResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.presenter.BindPresenter;
import com.isport.brandapp.bind.view.BindBaseView;
import com.isport.brandapp.blue.NotificationService;
import com.isport.brandapp.device.bracelet.Utils.DevicePicUtils;
import com.isport.brandapp.device.bracelet.braceletPresenter.AlarmPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.DeviceGoalStepPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.DisplayPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.HrSettingPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.LiftWristPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.NoDisturbPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.ThridMessagePresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.TimeFormatPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.W311RealTimeDataPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.WearPresenter;
import com.isport.brandapp.device.bracelet.view.AlarmView;
import com.isport.brandapp.device.bracelet.view.DeviceGoalStepView;
import com.isport.brandapp.device.bracelet.view.DisplayView;
import com.isport.brandapp.device.bracelet.view.HrSettingView;
import com.isport.brandapp.device.bracelet.view.LiftWristView;
import com.isport.brandapp.device.bracelet.view.NoDisturbView;
import com.isport.brandapp.device.bracelet.view.ThridMeaageView;
import com.isport.brandapp.device.bracelet.view.TimeFormatView;
import com.isport.brandapp.device.bracelet.view.W311RealTimeDataView;
import com.isport.brandapp.device.bracelet.view.WearView;
import com.isport.brandapp.device.publicpage.GoActivityUtil;
import com.isport.brandapp.device.watch.ActivityDeviceDoNotDistrubSetting;
import com.isport.brandapp.device.watch.ActivityDeviceSedentaryReminder;
import com.isport.brandapp.device.watch.presenter.CallAndMessageNotiPresenter;
import com.isport.brandapp.device.watch.presenter.WatchPresenter;
import com.isport.brandapp.device.watch.view.CallAndMessageNotiView;
import com.isport.brandapp.device.watch.view.WatchView;
import com.isport.brandapp.dialog.UnBindDeviceDialog;
import com.isport.brandapp.dialog.UnbindStateCallBack;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.login.ShowPermissionActivity;
import com.isport.brandapp.upgrade.bean.DeviceUpgradeBean;
import com.isport.brandapp.upgrade.present.DevcieUpgradePresent;
import com.isport.brandapp.upgrade.view.DeviceUpgradeView;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.view.VerBatteryView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import bike.gymproject.viewlibray.ItemDeviceSettingView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.StringUtil;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.entry.WatchRealTimeData;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * 手环设置页面
 */
public class ActivityBraceletMain extends BaseMVPTitleActivity<WatchView, WatchPresenter> implements WatchView, HrSettingView, WearView, View.OnClickListener, ItemDeviceSettingView.OnItemViewCheckedChangeListener, W311RealTimeDataView, DisplayView, ThridMeaageView, DeviceUpgradeView, AlarmView, BindBaseView, DeviceGoalStepView, TimeFormatView, CallAndMessageNotiView, NoDisturbView, LiftWristView {


    RxPermissions mRxPermission;

    private DevcieUpgradePresent devcieUpgradePresent;


    private final static String TAG = ActivityBraceletMain.class.getSimpleName();
    private ItemDeviceSettingView ivWatchStepTarget, iv_sedentary_reminder, ivWatch24HeartRate, iv_watch_timer_heart_rate, ivWatchAlarmSetting, ivWatchDisturbSetting, iv_find_bracelet,
            ivWatchCallRemind, ivWatchMsgSetting, ivWatchDefaultSetting, ivWatchStableVersion, iv_bracelet_display, iv_bracelet_wear, iv_bracelet_dropping_reminder, iv_thrid_message, iv_braclet_play;

    //w814不能设置久坐的开始时间和结束时间，只能设置开关
    private ItemDeviceSettingView iv_w814_sedentary_reminder;
    //w812 w813 w814的设置特有
    private ItemDeviceSettingView iv_watch_weather, iv_watch_time_formate, iv_watch_take_photo;
    //520独有设置
    private ItemDeviceSettingView ivWatchFace;
    //W307j独有
    private ItemDeviceSettingView iv_sleep_setting;
    private ItemDeviceSettingView iv_bracelet_lift_up_screen_307j;
    private ItemDeviceSettingView iv_bracelet_lift_up_screen;

    private TextView tvWatchState, tvWatchBetteryCount, tvUnbind;
    private VerBatteryView iv_battery;
    //  private WatchTypeDataView wdvStep, wdvDis, wdvCal;
    private DeviceBean deviceBean;
    private String devcieId;
    private String userId;
    private int currentType;
    private ImageView ivBattery;
    private boolean canUnBind;
    private WearPresenter wearPresenter;
    private W311RealTimeDataPresenter realTimeDataPresenter;
    private DisplayPresenter displayPresenter;
    private ThridMessagePresenter thridMessagePresenter;
    private AlarmPresenter alarmPresenter;

    private BindPresenter bindPresenter;
    private DeviceGoalStepPresenter deviceGoalStepPresenter;
    private CallAndMessageNotiPresenter callAndMessageNotiPresenter;
    private TimeFormatPresenter timeFormatPresenter;
    //勿扰模式开启情况
    private NoDisturbPresenter noDisturbPresenter;
    private ImageView ivWatch;

    private HrSettingPresenter hrSettingPresenter;


    HashMap<Integer, ItemDeviceSettingView> itemList = new HashMap<>();

    private LiftWristPresenter liftWristPresenter;

    @Override
    protected void onPause() {
        super.onPause();
        AppConfiguration.isWatchMain = false;
//        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    private boolean isOpenOnPausePageNotityState;
    private boolean isOpenPageNotityState;

    @Override
    protected void onResume() {
        super.onResume();
        AppConfiguration.isWatchMain = true;

        if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
            if (!NotificationService.isEnabled(this)) {
                isOpenOnPausePageNotityState = false;
            } else {
                isOpenOnPausePageNotityState = true;
            }

            Logger.myLog(TAG,"isOpenPageNotityState:" + isOpenPageNotityState + ",isOpenOnPausePageNotityState:" + isOpenOnPausePageNotityState);

            //重新启动服务器接收消息
            if (isOpenPageNotityState == false && isOpenOnPausePageNotityState == true) {
                NotificationService.toggleNotificationListenerService(this);
                isOpenPageNotityState = true;
            }
        }

        requst();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_bracelet_main;
    }

    @Override
    protected void initView(View view) {
        Logger.myLog("onDeviceItemListener1" + System.currentTimeMillis());
        canUnBind = false;

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        tvWatchState = view.findViewById(R.id.watch_state);
        iv_battery = view.findViewById(R.id.iv_battery);
        tvWatchBetteryCount = view.findViewById(R.id.watch_bettery_count);
        // wdvStep = view.findViewById(R.id.wdv_step);
        // wdvDis = view.findViewById(R.id.wdv_dis);
        // wdvCal = view.findViewById(R.id.wdv_cal);
        ivWatch = view.findViewById(R.id.watch);
        iv_bracelet_lift_up_screen_307j = view.findViewById(R.id.iv_bracelet_lift_up_screen_307j);
        iv_bracelet_lift_up_screen = view.findViewById(R.id.iv_bracelet_lift_up_screen);
        ivWatchStepTarget = view.findViewById(R.id.iv_watch_step_target);

        iv_w814_sedentary_reminder = findViewById(R.id.iv_w814_sedentary_reminder);


        //W307J独有
        iv_sleep_setting = findViewById(R.id.iv_sleep_setting);
        iv_sedentary_reminder = findViewById(R.id.iv_sedentary_reminder);
        //w311-w520
        iv_watch_timer_heart_rate = view.findViewById(R.id.iv_watch_timer_heart_rate);
        //W812-W814
        ivWatch24HeartRate = view.findViewById(R.id.iv_watch_24_heart_rate);
        ivWatchAlarmSetting = view.findViewById(R.id.iv_watch_alarm_setting);
        ivWatchDisturbSetting = view.findViewById(R.id.iv_watch_disturb_setting);
        ivWatchCallRemind = view.findViewById(R.id.iv_watch_call_remind);
        ivWatchMsgSetting = view.findViewById(R.id.iv_watch_msg_setting);
        ivWatchDefaultSetting = view.findViewById(R.id.iv_watch_default_setting);
        ivWatchStableVersion = view.findViewById(R.id.iv_watch_stable_version);
        iv_thrid_message = view.findViewById(R.id.iv_thrid_message);
        iv_find_bracelet = view.findViewById(R.id.iv_find_bracelet);
        iv_bracelet_display = view.findViewById(R.id.iv_bracelet_display);
        iv_bracelet_wear = view.findViewById(R.id.iv_bracelet_wear);
        iv_bracelet_dropping_reminder = view.findViewById(R.id.iv_bracelet_dropping_reminder);
        iv_braclet_play = view.findViewById(R.id.iv_braclet_play);
        //w812 w813 w814的设置特有
        iv_watch_weather = view.findViewById(R.id.iv_watch_weather);
        iv_watch_take_photo = view.findViewById(R.id.iv_watch_take_photo);
        iv_watch_time_formate = view.findViewById(R.id.iv_watch_time_formate);

        //520独有设置
        ivWatchFace = view.findViewById(R.id.iv_watch_dial_set);


        tvUnbind = view.findViewById(R.id.tv_unbind);


        itemList.clear();
        itemList.put(R.id.iv_w814_sedentary_reminder, iv_w814_sedentary_reminder);
        itemList.put(R.id.iv_watch_msg_setting, ivWatchMsgSetting);
        itemList.put(R.id.iv_watch_call_remind, ivWatchCallRemind);
        itemList.put(R.id.iv_bracelet_dropping_reminder, iv_bracelet_dropping_reminder);
        itemList.put(R.id.iv_watch_timer_heart_rate, iv_watch_timer_heart_rate);
        itemList.put(R.id.iv_bracelet_lift_up_screen_307j, iv_bracelet_lift_up_screen_307j);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            default:
                break;
        }
    }


    public void requst() {
        //获取显示设置
        displayPresenter.getAllDisplayState(TokenUtil.getInstance().getPeopleIdInt(this), deviceBean.deviceName);
        //显示第三方的消息个数
        thridMessagePresenter.getAllThridMessageItem(TokenUtil.getInstance().getPeopleIdInt(this), deviceBean.deviceName);
        //获取设备信息
        wearPresenter.getDeviceInfo(TokenUtil.getInstance().getPeopleIdInt(this), deviceBean.deviceName, currentType);
        //获取佩戴方式

        DevicePicUtils.setHeadPic(currentType, ivWatch);
        setShowItems(currentType);

        // mActPresenter.getPlayBanImage(currentType);

        //获取实时数据
        if (DeviceTypeUtil.isContainW81(currentType)) {
            realTimeDataPresenter.getDeviceStepLastTwoData(currentType);
        } else {
            realTimeDataPresenter.getRealTimeData(TokenUtil.getInstance().getPeopleIdInt(this), devcieId);
        }
        liftWristPresenter.getLiftWristBean(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId);

        alarmPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId);
        // mActPresenter.getDeviceSedentaryReminder(devcieId,TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));

        noDisturbPresenter.getNodisturb(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId);
        //if (DeviceTypeUtil.isContainW814W813W819(currentType)) {
        mActPresenter.getDeviceSedentaryReminder(devcieId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        //  }
    }


    @Override
    protected void initData() {
        Logger.myLog("onDeviceItemListener" + System.currentTimeMillis());
        ISportAgent.getInstance().registerListener(mBleReciveListener);
        getIntentData();
        isOpenPageNotityState = false;
        isOpenOnPausePageNotityState = false;
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle("--");
        setWatchState(UIUtils.getString(R.string.connected));
        setWatchBattery(0);
        titleBarView.setRightIcon(R.drawable.icon_device_unbind);
        titleBarView.setRightTextViewStateIsShow(false);
        // setValueText("0", "80", 0);
        titleBarView.setTitle(devcieId);
        mRxPermission = new RxPermissions(this);


        //title

        //获取目标步数
        deviceGoalStepPresenter.getDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId);

        //电话和信息

        if (currentType == JkConfiguration.DeviceType.Watch_W812 || currentType == JkConfiguration.DeviceType.Watch_W817) {
            hrSettingPresenter.get24hHeartSwitchState(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId);
            //hrSettingPresenter.getMessageCallState(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId);
        }

        callAndMessageNotiPresenter.getCallAndMessageNotiState(devcieId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        // mActPresenter.getWatchLastSleepData(devcieId);


    }


    public boolean isHaveCallPremission() {

        if (Build.VERSION.SDK_INT >= 28) {
            if (!mRxPermission.isGranted(Manifest.permission.READ_PHONE_STATE)
                    || !mRxPermission.isGranted(Manifest.permission.READ_CALL_LOG)
                    || !mRxPermission.isGranted(Manifest.permission.CALL_PHONE)
                    || !mRxPermission.isGranted(Manifest.permission.READ_CONTACTS)
                    || !mRxPermission.isGranted(Manifest.permission.ANSWER_PHONE_CALLS)) {
                return false;
            } else {
                return true;
            }
        } else {
            if (!mRxPermission.isGranted(Manifest.permission.READ_PHONE_STATE)
                    || !mRxPermission.isGranted(Manifest.permission.READ_CALL_LOG)
                    || !mRxPermission.isGranted(Manifest.permission.CALL_PHONE)
                    || !mRxPermission.isGranted(Manifest.permission.READ_CONTACTS)
            ) {
                return false;
            } else {
                return true;
            }
        }


    }

    public boolean isHaveMessagePremission() {
        if (!mRxPermission.isGranted(Manifest.permission.RECEIVE_SMS)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isHaveAppPremission() {
        if (!NotificationService.isEnabled(this)) {
            return false;
        } else {
            return true;
        }
    }


    private void getIntentData() {
        deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);
        if (deviceBean != null) {
            currentType = deviceBean.currentType;
            devcieId = deviceBean.deviceID;
        } else {
            currentType = JkConfiguration.DeviceType.BRAND_W311;
        }
        userId = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp());
    }

    private void setValueText(String step, String cal, float dis) {

        // wdvStep.setValueText(step);
        // wdvCal.setValueText(cal);
        // wdvDis.setValueText(CommonDateUtil.formatTwoPoint(dis));
    }

    private void setWatchState(String watchState) {
        tvWatchState.setText(watchState);
    }

    private void setWatchBattery(int watchBattery) {
        if (watchBattery == -1) {
            tvWatchBetteryCount.setText(UIUtils.getString(R.string.no_data));
            iv_battery.setVisibility(View.GONE);
        } else {
            tvWatchBetteryCount.setText(watchBattery + "%");
            iv_battery.setVisibility(View.VISIBLE);
            iv_battery.setProgress(watchBattery);
        }
    }

    /**
     * 根据资源ID获取Drawable/设置边框
     *
     * @param resId
     * @return
     */
    public Drawable getDrawables(int resId) {
        if ((resId >>> 24) < 2) {
            return null;
        }
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = this.getDrawable(resId);
        } else {
            drawable = this.getResources().getDrawable(resId);
        }

        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        return drawable;
    }

    @Override
    protected void initEvent() {
        ivWatchStepTarget.setOnClickListener(this);
        ivWatchFace.setOnClickListener(this);
        iv_sedentary_reminder.setOnClickListener(this);
        ivWatchAlarmSetting.setOnClickListener(this);
        ivWatchDisturbSetting.setOnClickListener(this);
        ivWatchDefaultSetting.setOnClickListener(this);
        ivWatchStableVersion.setOnClickListener(this);
        iv_bracelet_display.setOnClickListener(this);
        iv_sleep_setting.setOnClickListener(this);
        tvWatchState.setOnClickListener(this);
        tvWatchBetteryCount.setOnClickListener(this);
        iv_thrid_message.setOnClickListener(this);
        tvUnbind.setOnClickListener(this);
        iv_braclet_play.setOnClickListener(this);
        iv_watch_time_formate.setOnClickListener(this);
        iv_watch_take_photo.setOnClickListener(this);
        iv_watch_weather.setOnClickListener(this);

        iv_bracelet_wear.setOnCheckedChangeListener(this);
        ivWatch24HeartRate.setOnCheckedChangeListener(this);
        iv_w814_sedentary_reminder.setOnCheckedChangeListener(this);
        iv_watch_timer_heart_rate.setOnCheckedChangeListener(this);
        iv_bracelet_lift_up_screen_307j.setOnCheckedChangeListener(this);
        ivWatchCallRemind.setOnCheckedChangeListener(this);
        ivWatchMsgSetting.setOnCheckedChangeListener(this);
        iv_bracelet_dropping_reminder.setOnCheckedChangeListener(this);

        ivWatchMsgSetting.setShowArrow(true);
        ivWatchCallRemind.setShowArrow(true);
        ivWatchMsgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivityForResult(intent, 0x01);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        ivWatchCallRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityBraceletMain.this, ShowPermissionActivity.class));
            }
        });

        ivWatchStepTarget.setOnContentClickListener(new ItemDeviceSettingView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    return;
                }

                mActPresenter.popWindowSelect(ActivityBraceletMain.this, ivWatchStepTarget, JkConfiguration.GymUserInfo
                        .STEP_W311, ivWatchStepTarget.getRightTextValue(), false);
            }
        });
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {

            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {
               unbindOperate();
            }
        });
      /*  wdvStep.setOnItemViewCheckedChangeListener(new WatchTypeDataView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id) {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intent1);

            }
        });*/
       /* wdvDis.setOnItemViewCheckedChangeListener(new WatchTypeDataView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id) {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intent1);
//                Intent intent = new Intent(context, LogActivity.class);
//                startActivity(intent);
            }
        });*/
       /* wdvCal.setOnItemViewCheckedChangeListener(new WatchTypeDataView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id) {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intent1);
            }
        });*/

    }

    //解绑操作
    private void unbindOperate() {
        if (!AppConfiguration.isConnected) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
            return;
        }
        if (!AppConfiguration.hasSynced) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.sync_data));
            return;
        }
        isDerictUnBind = false;
        new UnBindDeviceDialog(ActivityBraceletMain.this, JkConfiguration.DeviceType.WATCH_W516, true, new UnbindStateCallBack() {
            @Override
            public void synUnbind() {

                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();

                if (AppConfiguration.isConnected && device != null) {
                    // TODO: 2018/11/8 同步解绑的逻辑
//                                        if (FragmentData.mWristbandstep != null) {
//                                            mActPresenter.updateSportData(FragmentData.mWristbandstep, mDeviceBean);
//                                        }
                    int devcieType = device.deviceType;
                    if (device != null) {
                        Constants.isSyncUnbind = true;
                        if (DeviceTypeUtil.isContainWrishBrand(devcieType) || DeviceTypeUtil.isContainW81(devcieType)) {
                            //睡眠带连接
                            ISportAgent.getInstance().requestBle(BleRequest.bracelet_sync_data);
                            canUnBind = true;
                                /*NetProgressObservable.getInstance().show(UIUtils.getString(R.string.common_please_wait),
                                        false);*/
                        } else {
                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                        }
                    }
                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                }
            }

            @Override
            public void dirctUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                unBindDevice(deviceBean, true);
            }

            @Override
            public void cancel() {

            }
        }, JkConfiguration.DeviceType.SLEEP);
    }

    private final BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            if (isConn) {
                tvWatchState.setText(UIUtils.getString(R.string.connected));
            } else {
                tvWatchState.setText(UIUtils.getString(R.string.disConnect));
            }
        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.DEVICE_WATCH_VERSION:
                        WatchVersionResult result = (WatchVersionResult) mResult;
                        String strFirmwareVersion = String.format(getResources().getString(R.string.app_device_version), result.getVersion());
                        ivWatchStableVersion.setContentText(strFirmwareVersion);
                        break;
                    case IResult.BRACELET_W311_REALTIME: {
                        //  BraceletW311RealTimeResult realTimeResult = (BraceletW311RealTimeResult) mResult;
                        //  int step = realTimeResult.getStepNum();
                        //  int cal = realTimeResult.getCal();
                        //  float dis = realTimeResult.getStepKm();
                        // setValueText(step + "", cal + "", dis);
                    }
                    break;
                    case IResult.BRACELET_W311_COMPTELETY:
                        try {
                            if (AppConfiguration.isWatchMain && canUnBind) {
                                if (isDerictUnBind) {
                                    return;
                                }
                                //同步数据是否成功
                                SyncProgressObservable.getInstance().hide();
                                BraceletW311SyncComplete braceletW311SyncComplete = (BraceletW311SyncComplete) mResult;
                                if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.SUCCESS) {
                                    canUnBind = false;
                                    AppConfiguration.hasSynced = true;
                                    bindPresenter.updateBraceletW311HistoryData(deviceBean, true);
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.SYNC_WATCH_SUCCESS));

                                } else if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.FAILED) {
                                    canUnBind = true;
                                    AppConfiguration.hasSynced = true;
                                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.app_issync_failed));
                                } else if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.TIMEOUT) {
                                    AppConfiguration.hasSynced = true;
                                    canUnBind = true;
                                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.app_issync_failed));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {

        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {
            //获取设备信息
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wearPresenter.getDeviceInfo(TokenUtil.getInstance().getPeopleIdInt(ActivityBraceletMain.this), deviceBean.deviceName);
                }
            }, 50);

        }
    };

    @Override
    protected void initHeader() {

    }

    @Override
    public void onClick(View v) {
        if (!AppConfiguration.isConnected) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
            return;
        }
        if (!AppConfiguration.hasSynced) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.sync_data));
            return;
        }
        switch (v.getId()) {
            case R.id.iv_bracelet_lift_up_screen_307j:
                mActPresenter.popWindowSelect(ActivityBraceletMain.this, iv_bracelet_lift_up_screen_307j, JkConfiguration.GymUserInfo
                        .LIFT_BRACELET_TO_VIEW_INFO_307J, iv_bracelet_lift_up_screen_307j.getRightTextValue(), false);
                break;
            //睡眠设置
            case R.id.iv_sleep_setting:
                Intent intentSleep = new Intent(context, W307JSleepSetting.class);
                intentSleep.putExtra("deviceType", deviceBean.currentType);
                intentSleep.putExtra("deviceId", deviceBean.deviceName);
                startActivity(intentSleep);
                break;
            //天气设置
            case R.id.iv_watch_weather:
                Intent intentWeather = new Intent(context, ActivityWeatherSetting.class);
                intentWeather.putExtra("deviceType", deviceBean.currentType);
                startActivity(intentWeather);
                break;
            case R.id.iv_watch_take_photo: //拍照
                //需要发送一条指令到拍照的页面
                checkCameraPersiomm();
                break;
            //手环的时间显示格式
            case R.id.iv_watch_time_formate:
                mActPresenter.popWindowSelect(ActivityBraceletMain.this, iv_watch_time_formate, JkConfiguration.GymUserInfo
                        .TIME_FORMATE, iv_watch_time_formate.getRightTextValue(), false);
                break;
            // 久坐提醒
            case R.id.iv_sedentary_reminder:
                Intent intent2 = new Intent(context, ActivityDeviceSedentaryReminder.class);
                intent2.putExtra("deviceType", deviceBean.currentType);
                intent2.putExtra("deviceId", deviceBean.deviceName);
                startActivity(intent2);
                break;
            //使用指导
            case R.id.iv_braclet_play:
                GoActivityUtil.goActivityPlayerDevice(deviceBean.deviceType, deviceBean, context);
                break;
            //第三方APP消息提醒 未开启权限需要去开启权限
            case R.id.iv_thrid_message:
                //已经打开了权限
                Logger.myLog("已有权限，打开信息通知");
                Intent thridMessageintent = new Intent(context, ActivityBandThirdSetting.class);
                thridMessageintent.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(thridMessageintent);
                break;
            //心率整点监测
            case R.id.iv_watch_timer_heart_rate:
                Intent intentHr = new Intent(context, ActivityBraceletHrSetting.class);
                intentHr.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intentHr);
                break;
            //寻找手环
            case R.id.iv_find_bracelet:
                setfindBraceletValue();
                ISportAgent.getInstance().requestBle(BleRequest.Bracelet_w311_find_bracelect);
                break;
            //佩戴方式
            case R.id.iv_bracelet_wear:
                mActPresenter.popWindowSelect(ActivityBraceletMain.this, iv_bracelet_wear, JkConfiguration.GymUserInfo
                        .BRACELET_WEAR, iv_bracelet_wear.getRightTextValue(), false);
                break;
            // 抬腕亮屏设置
            case R.id.iv_bracelet_lift_up_screen:
                Intent intentScreen = new Intent(context, ActivityLiftWristSetting.class);
                intentScreen.putExtra("deviceBean", deviceBean);
                startActivity(intentScreen);
                break;
            // 显示设置
            case R.id.iv_bracelet_display:
                Intent intentDisplay = new Intent(context, ActivityBraceletDisplaySetting.class);
                intentDisplay.putExtra("deviceBean", deviceBean);
                startActivity(intentDisplay);
                break;
            //表盘设置
            case R.id.iv_watch_dial_set:
                Intent intentDialSet = new Intent(context, ActivityWatchFacesSet.class);
                intentDialSet.putExtra("deviceBean", deviceBean);
                startActivity(intentDialSet);
                break;

            case R.id.iv_watch_step_target:
//                Intent intent = new Intent(context, ActivityWatchStep.class);
////                intent.putExtra("title", UIUtils.getString(R.string.app_protol));
//                startActivity(intent);
                break;
            //闹钟设置
            case R.id.iv_watch_alarm_setting:
                Intent intent3 = new Intent(context, ActivityBraceletAlarmList.class);
                intent3.putExtra("deviceBean", deviceBean);
                startActivity(intent3);
                break;
            //勿扰模式设置
            case R.id.iv_watch_disturb_setting:
                Intent intent5 = new Intent(context, ActivityDeviceDoNotDistrubSetting.class);
                intent5.putExtra("deviceBean", deviceBean);
                startActivity(intent5);
                break;
            case R.id.iv_watch_default_setting:
                if (AppConfiguration.isConnected) {
                    PublicAlertDialog.getInstance().showDialog("", getResources().getString(R.string.watch_default), context, getResources().getString(com.isport.brandapp.basicres.R.string.common_dialog_cancel), getResources().getString(com.isport.brandapp.basicres.R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                        @Override
                        public void determine() {
                            //  ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_TEST_RESET);
                        }

                        @Override
                        public void cancel() {


                        }
                    }, false);

                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                }
                break;
            case R.id.iv_watch_stable_version:
                Log.e(TAG, "iv_watch_stable_version");
                BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                if (device != null) {
                    Logger.myLog("iv_watch_stable_version=" + device + "");
                    // if (!DeviceTypeUtil.isContainW81(deviceBean.currentType)) {
                    ActivitySwitcher.goDFUAct(ActivityBraceletMain.this, device.deviceType, device.deviceName, device.address, true);
                    //  }
                }
                break;
            case R.id.tv_unbind:
                unbindOperate();
                break;
        }
    }

    boolean isDerictUnBind;

    private void unBindDevice(DeviceBean deviceBean, boolean isDe) {
        isDerictUnBind = true;
        currentType = deviceBean.deviceType;
        Logger.myLog("点击去解绑 == " + currentType);
        //解绑前断连设备

        mActPresenter.unBind(deviceBean, isDe);
    }


    Handler handler = new Handler();

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(int id, boolean isChecked) {
        if (!AppConfiguration.isConnected) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
            ivWatch24HeartRate.setChecked(false);
            if (itemList.containsKey(id) && itemList.get(id) != null) {
                itemList.get(id).setChecked(false);
            }
            return;
        }
        switch (id) {
            case R.id.iv_bracelet_lift_up_screen_307j:
                ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_is_open_raise_hand, isChecked);
                break;
            case R.id.iv_w814_sedentary_reminder:
                ISportAgent.getInstance().requestBle(BleRequest.BRACELET_W311_SET_SEDENTARY_TIME, isChecked ? 60 : 0, 10, 0, 22, 0);
                saveDb(60, Constants.defStartTime, Constants.defEndTime, isChecked);
                break;
            case R.id.iv_watch_24_heart_rate:
                Logger.myLog(TAG,"-----24小时心率-"+isChecked);

                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    ivWatch24HeartRate.setChecked(!isChecked);
                    return;
                }

                ivWatch24HeartRate.setChecked(isChecked);
                hrSettingPresenter.saveHrSetting(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), devcieId, isChecked);
                ISportAgent.getInstance().requestBle(BleRequest.bracelet_is_open_heartRate, isChecked);

                break;
            case R.id.iv_bracelet_dropping_reminder:
                if (AppConfiguration.hasSynced) {
                    //需要去修改状态就是
                    if (isChecked) {
                        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
                        if (!mRxPermission.isGranted(Manifest.permission.VIBRATE)) {
                            permissionManage.requestPermissions(mRxPermission, Manifest.permission.VIBRATE,
                                    UIUtils.getString(R.string.permission_vibrate), new
                                            PermissionManageUtil.OnGetPermissionListener() {


                                                @Override
                                                public void onGetPermissionYes() {
                                                    iv_bracelet_dropping_reminder.setChecked(true);
                                                    if (deviceInfoModel != null) {
                                                        if (isChecked) {
                                                            deviceInfoModel.setStateAntiLost(1);
                                                        } else {
                                                            deviceInfoModel.setStateAntiLost(0);
                                                        }
                                                        wearPresenter.saveDeviceInfo(deviceInfoModel);
                                                        ISportAgent.getInstance().requestBle(BleRequest.bracelet_lost_remind, isChecked);
                                                    }

                                                }

                                                @Override
                                                public void onGetPermissionNo() {
                                                    iv_bracelet_dropping_reminder.setChecked(false);
//                                            ToastUtils.showToastLong(context, UIUtils.getString(R.string.location_permissions));
                                                }
                                            });
                        } else {
                            iv_bracelet_dropping_reminder.setChecked(isChecked);
                            if (deviceInfoModel != null) {
                                if (isChecked) {
                                    deviceInfoModel.setStateAntiLost(1);
                                } else {
                                    deviceInfoModel.setStateAntiLost(0);
                                }
                                wearPresenter.saveDeviceInfo(deviceInfoModel);
                                ISportAgent.getInstance().requestBle(BleRequest.bracelet_lost_remind, isChecked);
                            }
                        }


                    } else {
                        if (deviceInfoModel != null) {
                            deviceInfoModel.setStateAntiLost(0);
                            wearPresenter.saveDeviceInfo(deviceInfoModel);
                            ISportAgent.getInstance().requestBle(BleRequest.bracelet_lost_remind, isChecked);
                        }
                    }
                }
                break;

            case R.id.iv_watch_call_remind:     //电话提醒
                checkPhonePerssion(isChecked);
                break;
            case R.id.iv_watch_msg_setting:     //信息提醒
                cheakMessagePerssion(isChecked);

                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (NotificationService.isEnabled(this)) {
//            Logger.myLog("打开权限了，回到app");
//            ivWatchMsgSetting.setChecked(true);
//            updateNotifySettingMsg(true);
//        } else {
//            Logger.myLog("关闭或则未打开权限，回到app");
//            ivWatchMsgSetting.setChecked(false);
//        }
//    }

    private void updateNotifySettingMsg(boolean msgSwitch) {

        //发送数据设置显示的界面
        if (AppConfiguration.isConnected) {
            displaySet.setShowMsgContentPush(msgSwitch);
            //如果关闭则设置为0
            ParseData.saveW516CallMessageRemind(deviceBean.deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), msgSwitch, 1);
            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_display, displaySet);
        } else {
            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
        }

    }


    DisplaySet displaySet = new DisplaySet();

    private void updateNotifySettingCall(boolean callSwitch) {
        //发送数据设置显示的界面
        if (AppConfiguration.isConnected) {
            displaySet.setShowIncomingReminder(callSwitch);
            ParseData.saveW516CallMessageRemind(deviceBean.deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), callSwitch, 0);
            //如果关闭则设置为0
            if (DeviceTypeUtil.isContaintW81(currentType)) {
                return;
            }
            //311,520的开关设置关系到了页面的显示
            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_display, displaySet);
        } else {
            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
        }


//        }
    }

    @Override
    protected WatchPresenter createPresenter() {
        devcieUpgradePresent = new DevcieUpgradePresent(this);
        wearPresenter = new WearPresenter(this);
        displayPresenter = new DisplayPresenter(this);
        realTimeDataPresenter = new W311RealTimeDataPresenter(this);
        thridMessagePresenter = new ThridMessagePresenter(this);
        alarmPresenter = new AlarmPresenter(this);
        bindPresenter = new BindPresenter(this);
        deviceGoalStepPresenter = new DeviceGoalStepPresenter(this);
        timeFormatPresenter = new TimeFormatPresenter(this);
        callAndMessageNotiPresenter = new CallAndMessageNotiPresenter(this);
        hrSettingPresenter = new HrSettingPresenter(this);
        noDisturbPresenter = new NoDisturbPresenter(this);
        liftWristPresenter = new LiftWristPresenter(this);
        return new WatchPresenter(this);
    }

    Bracelet_W311_LiftWristToViewInfoModel model;

    @Override
    public void dataSetSuccess(View view, String select, String data) {

        if ((view == iv_bracelet_lift_up_screen_307j)) {
            if (UIUtils.getString(R.string.lift_to_view_info_all_day).equals(data)) {
                stateType = 0;
            } else if (UIUtils.getString(R.string.lift_to_view_info_Timing_open_307j).equals(data)) {
                stateType = 1;
            } else {
                stateType = 2;
            }
            if (model != null) {
                model.setSwichType(stateType);
            }
            liftWristPresenter.saveLiftWristBean(model);
            lastListState = model.getSwichType();
            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_is_open_raise_hand_307j, stateType);
            updateUI();

        } else if (view == iv_bracelet_wear) {
            ((ItemDeviceSettingView) view).setContentText(data);
            //去数据库操作保存
            if (data.equals(UIUtils.getString(R.string.wear_left))) {
                wearModel.setIsLeft(true);
            } else {
                wearModel.setIsLeft(false);
            }
            wearPresenter.saveWearItem(wearModel);
            if (AppConfiguration.isConnected) {
                ISportAgent.getInstance().requsetW311Ble(BleRequest.BRACELET_W311_SET_WEAR, wearModel.getIsLeft());
            }

        } else if (view == iv_watch_time_formate) {
            ((ItemDeviceSettingView) view).setContentText(data);
            if (data.equals(UIUtils.getString(R.string.time_format_12))) {
                timeFormatPresenter.saveTimeFormat(devcieId, userId, CRPTimeSystemType.TIME_SYSTEM_12);
                if (AppConfiguration.isConnected) {
                    ISportAgent.getInstance().requsetW311Ble(BleRequest.DEVICE_TIME_FORMAT, CRPTimeSystemType.TIME_SYSTEM_12);
                }
            } else {
                timeFormatPresenter.saveTimeFormat(devcieId, userId, CRPTimeSystemType.TIME_SYSTEM_24);
                if (AppConfiguration.isConnected) {
                    ISportAgent.getInstance().requsetW311Ble(BleRequest.DEVICE_TIME_FORMAT, CRPTimeSystemType.TIME_SYSTEM_24);
                }
            }

        }
        else if(view == ivWatchStepTarget){     //计步目标
            ((ItemDeviceSettingView) view).setContentText(data);
            int target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_steps))[0]);
            deviceGoalStepPresenter.saveDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(this), AppConfiguration.braceletID, target);
            BleSPUtils.putInt(this,BleSPUtils.KEY_STEP_GOAL, target);
            ISportAgent.getInstance().requsetW311Ble(BleRequest.device_target_step,target);
        }

//        else {
//            if (view instanceof ItemDeviceSettingView) {
//                Logger.myLog(TAG,"---222----到这里了------");
//                ((ItemDeviceSettingView) view).setContentText(data);
//                int target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_steps))[0]);
//                deviceGoalStepPresenter.saveDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(this), AppConfiguration.braceletID, target);
//                AppSP.putInt(this,AppSP.DEVICE_GOAL_KEY,target);
//                //这里 需要去请求蓝牙
//            }
//        }

    }

    @Override
    public void onUnBindSuccess() {
        //解绑的是当前连接的设备,需要断连设备
        EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
        Logger.myLog("解绑成功");
        BaseDevice currnetDevice = ISportAgent.getInstance().getCurrnetDevice();
        if (currnetDevice != null && currnetDevice.deviceType == currentType) {
            Logger.myLog("currnetDevice == " + currentType);
            //解绑设备，不用重连
            ISportAgent.getInstance().unbind(false);
        }
        GoActivityUtil.goActivityUnbindDevice(currentType, ActivityBraceletMain.this);
        finish();
    }

    //311数据上传成功 ，进行解绑
    @Override
    public void updateWatchDataSuccess(DeviceBean deviceBean) {
        unBindDevice(deviceBean, false);
    }

    @Override
    public void updateSleepDataSuccess(DeviceBean deviceBean) {

    }

    @Override
    public void successDayDate(WatchSleepDayData watchSleepDayData) {
        if (!StringUtil.isBlank(watchSleepDayData.getDateStr())) {
            //wdvSleep.setValueText(getFormatHM(watchSleepDayData.getTotalSleepTime()));
            //UI展示
        }
    }

    @Override
    public void updateWatchHistoryDataSuccess(DeviceBean deviceBean) {
        unBindDevice(deviceBean, false);
    }

    @Override
    public void updateFail() {
        ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.error_Connection_timeout));
    }

    @Override
    public void seccessGetDeviceSedentaryReminder(Watch_W516_SedentaryModel watch_w516_sedentaryModel) {
        Logger.myLog(TAG,"--久坐="+new Gson().toJson(watch_w516_sedentaryModel));
        if (watch_w516_sedentaryModel != null) {
            if (DeviceTypeUtil.isContainW814W813W819(currentType)) {
                iv_w814_sedentary_reminder.setChecked(watch_w516_sedentaryModel.getIsEnable());
            } else {
                if (watch_w516_sedentaryModel.getIsEnable()) {
                    iv_sedentary_reminder.setContentText(watch_w516_sedentaryModel.getLongSitStartTime() + "-" + watch_w516_sedentaryModel.getLongSitEndTime());
                    //ivWatchStableRemind.setContentText(UIUtils.getString(R.string.setting_start));
                } else {
                    iv_sedentary_reminder.setContentText(UIUtils.getString(R.string.display_no_count));
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SyncCacheUtils.setUnBindState(false);
        Constants.isSyncUnbind = false;
        EventBus.getDefault().unregister(this);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    Bracelet_W311_WearModel wearModel;

    @Override
    public void successWearItem(Bracelet_W311_WearModel bracelet_w311_wearModel) {
        wearModel = bracelet_w311_wearModel;
        if (bracelet_w311_wearModel.getIsLeft()) {
            iv_bracelet_wear.setContentText(UIUtils.getString(R.string.wear_left));
        } else {
            iv_bracelet_wear.setContentText(UIUtils.getString(R.string.wear_right));
        }

    }

    public void setfindBraceletValue() {
        iv_find_bracelet.setContentText(UIUtils.getString(R.string.find_bracelet));
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                iv_find_bracelet.setContentText("");
            }
        });
    }

    @Override
    public void successWearItem() {
        // ToastUtil.showTextToast(BaseApp.getApp(), "佩戴方式修改成功");
    }


    Bracelet_W311_DeviceInfoModel deviceInfoModel;
    DeviceInformationTable w81DeviceInfoModel;


    @Override
    public void getDeviceInfo(DeviceInformationTable deviceInfoModel) {
        if (deviceInfoModel != null) {


            if (TextUtils.isEmpty(deviceInfoModel.getVersion())) {
                if (AppConfiguration.isConnected) {
                    ISportAgent.getInstance().requsetW81Ble(BleRequest.GET_VERSION);
                }
            }

            Logger.myLog("getDeviceInfo" + deviceInfoModel.toString());
            this.w81DeviceInfoModel = deviceInfoModel;
            String strFirmwareVersion = String.format(getResources().getString(R.string.app_device_version), deviceInfoModel.getVersion());
            ivWatchStableVersion.setContentText(strFirmwareVersion);
            //ivWatchDefaultSetting.setContentText(deviceInfoModel.getDeviceId());
            //  iv_bracelet_dropping_reminder.setChecked(deviceInfoModel.getStateAntiLost() == 0 ? false : true);

            if (deviceInfoModel.getBattery() != 0) {
                setWatchBattery(deviceInfoModel.getBattery());
                //if (!DeviceTypeUtil.isContainW81(currentType)) {
                devcieUpgradePresent.getDeviceUpgradeInfo(currentType);
                // }
            } else {
                setWatchBattery(-1);
            }
        }
    }

    @Override
    public void getDeviceInfo(Bracelet_W311_DeviceInfoModel deviceInfoModel) {


        if (deviceInfoModel != null) {
            Logger.myLog("getDeviceInfo" + deviceInfoModel.toString());
            this.deviceInfoModel = deviceInfoModel;
            String strFirmwareVersion = String.format(getResources().getString(R.string.app_device_version), deviceInfoModel.getFirmwareHighVersion() + "." + deviceInfoModel.getFirmwareLowVersion());
            ivWatchStableVersion.setContentText(strFirmwareVersion);
            ivWatchDefaultSetting.setContentText(deviceInfoModel.getDeviceId());
            iv_bracelet_dropping_reminder.setChecked(deviceInfoModel.getStateAntiLost() == 0 ? false : true);

            if (deviceInfoModel.getPowerLevel() != 0) {
                setWatchBattery(deviceInfoModel.getPowerLevel());
                devcieUpgradePresent.getDeviceUpgradeInfo(currentType);
            } else {
                setWatchBattery(-1);
            }
        }
    }

    @Override
    public void getW311RealTimeData(Bracelet_W311_RealTimeData bracelet_w311_wearModel) {
        //  setValueText(bracelet_w311_wearModel.getStepNum() + "", bracelet_w311_wearModel.getCal() + "", bracelet_w311_wearModel.getStepKm());
    }

    @Override
    public void getW516OrW556(WatchRealTimeData bracelet_w311_wearModel) {

    }

    @Override
    public void successSaveRealTimeData(boolean isSave) {

    }

    @Override
    public void successDisplayItem(Bracelet_W311_DisplayModel bracelet_w311_displayModel, boolean isMessage, boolean isCall) {

        int count = 0;
        if (bracelet_w311_displayModel != null) {
            if (currentType == JkConfiguration.DeviceType.BRAND_W311) {
                count += bracelet_w311_displayModel.getIsShowAlarm() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowCal() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowComplete() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowDis() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowPresent() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowSportTime() ? 1 : 0;
            } else if (currentType == JkConfiguration.DeviceType.BRAND_W311) {
                count += bracelet_w311_displayModel.getIsShowCal() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowDis() ? 1 : 0;
            } else {
                count += bracelet_w311_displayModel.getIsShowCal() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowDis() ? 1 : 0;
                count += bracelet_w311_displayModel.getIsShowComplete() ? 1 : 0;
            }

            displaySet.setShowAlarm(bracelet_w311_displayModel.getIsShowAlarm());
            displaySet.setShowCala(bracelet_w311_displayModel.getIsShowCal());
            displaySet.setShowDist(bracelet_w311_displayModel.getIsShowDis());
            displaySet.setShowProgress(bracelet_w311_displayModel.getIsShowPresent());
            displaySet.setShowEmotion(bracelet_w311_displayModel.getIsShowComplete());
            displaySet.setShowSportTime(bracelet_w311_displayModel.getIsShowSportTime());
            displaySet.setShowIncomingReminder(isCall);
            displaySet.setShowMsgContentPush(isMessage);

        }

        if (count > 0) {
            iv_bracelet_display.setContentText(String.format(UIUtils.getString(R.string.display_count), count + ""));
        } else {
            iv_bracelet_display.setContentText(UIUtils.getString(R.string.display_no_count));
        }
    }

    @Override
    public void successSaveDisplayItem() {

    }

    @Override
    public void successThridMessageItem(Bracelet_W311_ThridMessageModel bracelet_w311_displayModel) {
        int count = 0;
        if (bracelet_w311_displayModel != null) {
            count += bracelet_w311_displayModel.getIsFaceBook() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsQQ() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsTwitter() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsWechat() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsWhatApp() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsSkype() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsMessage() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsLinkedin() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsInstagram() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsLine() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsOthers() ? 1 : 0;
            count += bracelet_w311_displayModel.getIskakaotalk() ? 1 : 0;
            count += bracelet_w311_displayModel.getIsLinkedin() ? 1 : 0;
        }
        //这里的个数大于1也是需要显示信息的

        if (count > 0) {
            iv_thrid_message.setContentText(String.format(UIUtils.getString(R.string.display_count), count + ""));
           /* if (isHaveAppPremission()) {
                iv_thrid_message.setContentText(String.format(UIUtils.getString(R.string.display_count), count + ""));
            } else {
               bracelet_w311_displayModel.setIsWhatApp(false);
                bracelet_w311_displayModel.setIsTwitter(false);
                bracelet_w311_displayModel.setIsSkype(false);
                bracelet_w311_displayModel.setIsFaceBook(false);
                bracelet_w311_displayModel.setIsQQ(false);
                bracelet_w311_displayModel.setIsWechat(false);
                bracelet_w311_displayModel.setIsLine(false);
                bracelet_w311_displayModel.setIsInstagram(false);
                bracelet_w311_displayModel.setIskakaotalk(false);
                bracelet_w311_displayModel.setIsOthers(false);
                bracelet_w311_displayModel.setIsMessage(false);
                bracelet_w311_displayModel.setIsLinkedin(false);
                thridMessagePresenter.saveThridMessageItem(bracelet_w311_displayModel);
            }*/
        } else {
            iv_thrid_message.setContentText(UIUtils.getString(R.string.display_no_count));
        }
    }

    @Override
    public void successThridMessageItem() {

    }

    @Override
    public void successAllAlarmItem(ArrayList<Bracelet_W311_AlarmModel> bracelet_w311_displayModel) {

        if (bracelet_w311_displayModel == null || bracelet_w311_displayModel.size() == 0) {
            ivWatchAlarmSetting.setContentText(UIUtils.getString(R.string.display_no_count));
        } else {
            int count = 0;
            for (int i = 0; i < bracelet_w311_displayModel.size(); i++) {
                if (bracelet_w311_displayModel.get(i).getIsOpen()) {
                    count++;
                }
            }
            if (count == 0) {
                ivWatchAlarmSetting.setContentText(UIUtils.getString(R.string.display_no_count));
            } else {
                ivWatchAlarmSetting.setContentText(String.format(UIUtils.getString(R.string.display_count), count + ""));
            }
        }

    }

    @Override
    public void successW560AllAlarmItem(ArrayList<Watch_W560_AlarmModel> watch_w560_alarmModels) {
        // 不作处理
    }

    @Override
    public void successSaveAlarmItem() {

    }

    @Override
    public void successDelectAlarmItem() {

    }


    @Override
    public void successDeviceUpgradeInfo(DeviceUpgradeBean deviceUpgradeBean) {

        if ((deviceInfoModel != null || w81DeviceInfoModel != null) && deviceUpgradeBean != null) {
            String version;
            Logger.myLog("DeviceUpgradeBean:" + deviceUpgradeBean);
            if (DeviceTypeUtil.isContainW81(currentType)) {
                version = w81DeviceInfoModel.getVersion();
            } else {
                version = deviceInfoModel.getFirmwareHighVersion() + "." + deviceInfoModel.getFirmwareLowVersion();
            }

            if (TextUtils.isEmpty(version)) {
                version = "";
            }

            String serVersion = deviceUpgradeBean.getAppVersionName();
            if (TextUtils.isEmpty(serVersion)) {
                serVersion = "";
            }
            String strFirmwareVersion = String.format(getResources().getString(R.string.app_device_version), version);
            ivWatchStableVersion.setContentText(strFirmwareVersion);


            Logger.myLog("ActivityBraceletMain version:" + version + "serVersion:" + serVersion);
            if (version.equals(serVersion)) {
                ivWatchStableVersion.setContentText(strFirmwareVersion + UIUtils.getString(R.string.no_update));
            } else {
                ivWatchStableVersion.setContentText(strFirmwareVersion + UIUtils.getString(R.string.has_update));
            }
        }

    }


    private void setShowItems(int currentType) {
        iv_bracelet_lift_up_screen_307j.setVisibility(View.GONE);
        iv_sedentary_reminder.setVisibility(View.GONE);
        iv_w814_sedentary_reminder.setVisibility(View.GONE);
        ivWatchFace.setVisibility(View.GONE);
        iv_bracelet_wear.setVisibility(View.GONE);
        iv_watch_weather.setVisibility(View.GONE);
        iv_watch_take_photo.setVisibility(View.GONE);
        iv_watch_time_formate.setVisibility(View.GONE);
        iv_sedentary_reminder.setVisibility(View.VISIBLE);
        iv_bracelet_display.setVisibility(View.GONE);
        iv_watch_timer_heart_rate.setVisibility(View.GONE);
        ivWatch24HeartRate.setVisibility(View.GONE);
        iv_bracelet_dropping_reminder.setVisibility(View.GONE);
        iv_braclet_play.setVisibility(View.VISIBLE);
        iv_sleep_setting.setVisibility(View.GONE);
        if (currentType == JkConfiguration.DeviceType.BRAND_W311) {
            iv_sedentary_reminder.setVisibility(View.VISIBLE);
            iv_watch_timer_heart_rate.setVisibility(View.VISIBLE);
            wearPresenter.getWearItem(TokenUtil.getInstance().getPeopleIdInt(this), deviceBean.deviceName);
            iv_bracelet_wear.setVisibility(View.VISIBLE);
            iv_bracelet_display.setVisibility(View.VISIBLE);
            iv_bracelet_dropping_reminder.setVisibility(View.VISIBLE);
        } else if (currentType == JkConfiguration.DeviceType.Brand_W520) {
            ivWatchFace.setVisibility(View.VISIBLE);
            iv_watch_timer_heart_rate.setVisibility(View.VISIBLE);
            iv_watch_weather.setVisibility(View.VISIBLE);
            iv_sedentary_reminder.setVisibility(View.VISIBLE);
            iv_bracelet_display.setVisibility(View.VISIBLE);
            iv_bracelet_dropping_reminder.setVisibility(View.VISIBLE);
        } else if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
            iv_bracelet_lift_up_screen.setVisibility(View.GONE);
            iv_bracelet_lift_up_screen_307j.setVisibility(View.VISIBLE);
            ivWatchDisturbSetting.setVisibility(View.GONE);
            iv_thrid_message.setVisibility(View.GONE);
            iv_sleep_setting.setVisibility(View.VISIBLE);
            ivWatchFace.setVisibility(View.VISIBLE);
            // iv_watch_weather.setVisibility(View.VISIBLE);
            iv_sedentary_reminder.setVisibility(View.VISIBLE);
            iv_bracelet_display.setVisibility(View.VISIBLE);
            iv_bracelet_dropping_reminder.setVisibility(View.VISIBLE);
        } else if (DeviceTypeUtil.isContaintW81(currentType)) {
            iv_sedentary_reminder.setVisibility(View.VISIBLE);
            iv_watch_weather.setVisibility(View.VISIBLE);
            if (currentType == JkConfiguration.DeviceType.Watch_W812 || currentType == JkConfiguration.DeviceType.Watch_W817) {
                ivWatch24HeartRate.setVisibility(View.VISIBLE);
            } else if (DeviceTypeUtil.isContainW814W813W819(currentType)) {
                iv_w814_sedentary_reminder.setVisibility(View.VISIBLE);
                iv_sedentary_reminder.setVisibility(View.GONE);
            }
            timeFormatPresenter.getTimeFomate(userId, devcieId);
            ivWatchFace.setVisibility(View.VISIBLE);
            iv_watch_take_photo.setVisibility(View.VISIBLE);
            iv_watch_time_formate.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void successGetGoalStep(int Step) {

        //目标步数
        ivWatchStepTarget.setContentText(Step + " " + UIUtils.getString(R.string.unit_steps));

        int goalStep = BleSPUtils.getInt(this,BleSPUtils.KEY_STEP_GOAL, 0);
        if(goalStep != 0 )
            ivWatchStepTarget.setContentText(goalStep + " " + UIUtils.getString(R.string.unit_steps));

        Logger.myLog(TAG,"-----目标步数="+Step+" 第二个 "+goalStep);
    }

    @Override
    public void successSaveGoalStep(int step) {
        JkConfiguration.WATCH_GOAL = step;
        BaseManager.setStepTarget((int) JkConfiguration.WATCH_GOAL);
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().setStepTarget(step);
            if (DeviceTypeUtil.isContaintW81(currentType)) {
                //w812-w814有专门设置这个属性的接口
                ISportAgent.getInstance().requsetW311Ble(BleRequest.device_target_step, step);
            } else {
                ISportAgent.getInstance().requsetW311Ble(BleRequest.BRACELET_W311_SET_USERINFO);
            }
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_WATCH_TARGET));
    }

    @Override
    public void successGetTimeFormat(int format) {
        Logger.myLog(TAG,"-----successGetTimeFormat,format:" + format);
        if (format == CRPTimeSystemType.TIME_SYSTEM_12) {
            iv_watch_time_formate.setContentText(UIUtils.getString(R.string.time_format_12));
        } else {
            iv_watch_time_formate.setContentText(UIUtils.getString(R.string.time_format_24));
        }
    }

    @Override
    public void successTimeFormat(boolean isSave) {

    }

    public void saveDb(int times, String starTime, String endTime, boolean enable) {
        mActPresenter.saveDevcieSedentaryReminder(devcieId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), times, starTime, endTime, enable);

    }

    @Override
    public void successGetCallAndMessageNoti(Watch_W516_NotifyModel model) {
        Logger.myLog("getMsgSwitch== " + model.getMsgSwitch() + " getCallSwitch== " + model.getCallSwitch());


        if (isHaveMessagePremission()) {
            ivWatchMsgSetting.setChecked(model.getMsgSwitch());
        } else {
            ivWatchMsgSetting.setChecked(false);
        }
        if (isHaveCallPremission()) {
            ivWatchCallRemind.setChecked(model.getCallSwitch());
        } else {
            ivWatchCallRemind.setChecked(false);
        }


    }

    @Override
    public void successSaveCallAndMessageNoti() {

    }

    @Override
    public void successHrSettingItem(Bracelet_w311_hrModel bracelet_w311_wearModel) {

    }


    @Override
    public void successLifWristBean(Bracelet_W311_LiftWristToViewInfoModel models) {


        this.model = new Bracelet_W311_LiftWristToViewInfoModel();
        model.setSwichType(models.getSwichType());
        model.setEndMin(models.getEndMin());
        model.setEndHour(models.getEndHour());
        model.setStartMin(models.getStartMin());
        model.setStartHour(models.getStartHour());
        model.setDeviceId(models.getDeviceId());
        model.setId(models.getId());
        model.setIsNextDay(models.getIsNextDay());
        model.setUserId(models.getUserId());
        this.lastListState = model.getSwichType();
        updateUI();


    }

    public String strlastListState;
    private Integer lastListState, stateType;

    private void updateUI() {


        //


        switch (model.getSwichType()) {


            case 0:
                stateType = 0;
                // ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_is_open_raise_hand, true);
                if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
                    strlastListState = UIUtils.getString(R.string.lift_to_view_info_all_day);
                    iv_bracelet_lift_up_screen_307j.setContentText(strlastListState);

                } else {
                    strlastListState = UIUtils.getString(R.string.lift_to_view_info_all_day);
                    iv_bracelet_lift_up_screen.setContentText(strlastListState);
                }
                break;
            case 1:
                stateType = 1;
                if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
                    strlastListState = UIUtils.getString(R.string.lift_to_view_info_Timing_open_307j);
                    iv_bracelet_lift_up_screen_307j.setContentText(strlastListState);

                } else {
                    strlastListState = UIUtils.getString(R.string.lift_to_view_info_Timing_open);
                    iv_bracelet_lift_up_screen.setContentText(strlastListState);
                }
                break;
            case 2:
                stateType = 2;
                if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
                    strlastListState = UIUtils.getString(R.string.lift_to_view_info_close);
                    iv_bracelet_lift_up_screen_307j.setContentText(strlastListState);
                } else {
                    strlastListState = UIUtils.getString(R.string.lift_to_view_info_close);
                    iv_bracelet_lift_up_screen.setContentText(strlastListState);
                }
                break;


        }


    }

    @Override
    public void successSave(boolean isSave) {

    }

    @Override
    public void success24HrSettingState(Bracelet_W311_24H_hr_SettingModel bracelet_w311_24H_hr_settingModel) {
        Logger.myLog(TAG,"----24小时心率="+bracelet_w311_24H_hr_settingModel.getHeartRateSwitch());
        ivWatch24HeartRate.setChecked(bracelet_w311_24H_hr_settingModel.getHeartRateSwitch());
    }


    private void checkPhonePerssion(boolean isCheak) {
        //READ_PHONE_STATE
        //READ_CALL_LOG
        //CALL_PHONE
        //READ_CONTACTS


        boolean isPremission = isHaveCallPremission();
        if (!isPremission) {
            PermissionManageUtil permissionManage = new PermissionManageUtil(this);
            if (Build.VERSION.SDK_INT >= 28) {
                permissionManage.requestPermissionsGroup(new RxPermissions(this),
                        PermissionGroup.Call_SUPPORT_PERSSION_28, new PermissionManageUtil
                                .OnGetPermissionListener() {
                            @Override
                            public void onGetPermissionYes() {
                                ivWatchCallRemind.setChecked(true);
                                updateNotifySettingCall(isCheak);
                                //goCamera();
                            }

                            @Override
                            public void onGetPermissionNo() {
                                updateNotifySettingCall(false);
                                ToastUtil.showTextToast(ActivityBraceletMain.this, UIUtils.getString(R.string.location_permissions));
                            }
                        });
            } else {
                permissionManage.requestPermissionsGroup(new RxPermissions(this),
                        PermissionGroup.Call_SUPPORT_PERSSION, new PermissionManageUtil
                                .OnGetPermissionListener() {
                            @Override
                            public void onGetPermissionYes() {
                                ivWatchCallRemind.setChecked(true);
                                updateNotifySettingCall(isCheak);
                                //goCamera();
                            }

                            @Override
                            public void onGetPermissionNo() {
                                updateNotifySettingCall(false);
                                ToastUtil.showTextToast(ActivityBraceletMain.this, UIUtils.getString(R.string.location_permissions));
                            }
                        });
            }


        } else {
            ivWatchCallRemind.setChecked(isCheak);
            updateNotifySettingCall(isCheak);
        }
    }


    private void cheakMessagePerssion(boolean isChecked) {

        ivWatchMsgSetting.setChecked(false);
        //先请求SMS权限 RECEIVE_SMS  不论时拒绝还是同意都不做操作，应该设置为关闭状态
        PermissionManageUtil permissionManage1 = new PermissionManageUtil(context);
        permissionManage1.requestPermissions(mRxPermission, Manifest.permission.READ_SMS, UIUtils.getString(R.string.permission_location3), new PermissionManageUtil.OnGetPermissionListener() {
            @Override
            public void onGetPermissionYes() {

            }

            @Override
            public void onGetPermissionNo() {

            }
        });

        if (!mRxPermission.isGranted(Manifest.permission.RECEIVE_SMS)) {
            permissionManage1.requestPermissions(mRxPermission, Manifest.permission.RECEIVE_SMS,
                    UIUtils.getString(R.string.permission_location3), new
                            PermissionManageUtil.OnGetPermissionListener() {
                                @Override
                                public void onGetPermissionYes() {
                                    ivWatchMsgSetting.setChecked(false);
                                }

                                @Override
                                public void onGetPermissionNo() {
                                    ivWatchMsgSetting.setChecked(false);
//                                            ToastUtils.showToastLong(context, UIUtils.getString(R.string.location_permissions));
                                }
                            });
        } else {
            //有SMS权限的情况
            //已经打开了权限


            if (currentType == JkConfiguration.DeviceType.BRAND_W307J) {
                if (!NotificationService.isEnabled(this)) {
                    Logger.myLog("没有打开权限，要去打开权限");
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                    ivWatchMsgSetting.setChecked(false);
                    //updateNotifySettingMsg(false);
                } else {
                    ivWatchMsgSetting.setChecked(isChecked);
                    updateNotifySettingMsg(isChecked);
                }
            } else {
                Logger.myLog("已有权限，打开信息通知");
                ivWatchMsgSetting.setChecked(isChecked);
                updateNotifySettingMsg(isChecked);
            }

        }

    }


    private void checkCameraPersiomm() {

        PermissionManageUtil permissionManage = new PermissionManageUtil(this);
        permissionManage.requestPermissionsGroup(new RxPermissions(this),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil
                        .OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        ISportAgent.getInstance().requestBle(BleRequest.DEVICE_SITCH_CAMERAVIEW);
                        Intent intentCamara = new Intent(context, CamaraActivity1.class);
                        startActivity(intentCamara);
                        //goCamera();
                    }

                    @Override
                    public void onGetPermissionNo() {

                        ToastUtil.showTextToast(ActivityBraceletMain.this, UIUtils.getString(R.string.location_permissions));
                    }
                });

    }

    @Override
    public void successDisturb(boolean isOpen) {
        Logger.myLog(TAG,"----勿扰模式="+isOpen);
        if (isOpen) {
            ivWatchDisturbSetting.setContentText(UIUtils.getString(R.string.setting_start));
        } else {
            ivWatchDisturbSetting.setContentText(UIUtils.getString(R.string.display_no_count));
        }

    }

    @Override
    public void successDisturb(Watch_W516_SleepAndNoDisturbModel isOpen) {

        Logger.myLog(TAG,"-------勿扰数据="+isOpen.toString());
        if (isOpen != null) {
            if (isOpen.getOpenNoDisturb()) {
                ivWatchDisturbSetting.setContentText(isOpen.getNoDisturbStartTime() + "-" + isOpen.getNoDisturbEndTime());
            } else {
                ivWatchDisturbSetting.setContentText(UIUtils.getString(R.string.display_no_count));
            }

        } else {
            ivWatchDisturbSetting.setContentText(UIUtils.getString(R.string.display_no_count));
        }

    }


}
