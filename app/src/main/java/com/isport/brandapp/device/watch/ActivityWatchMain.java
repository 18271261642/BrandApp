package com.isport.brandapp.device.watch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.DeviceInformationTableAction;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.watch.WatchRealTimeResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SyncResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.blue.NotificationService;
import com.isport.brandapp.device.bracelet.ActivityLiftWristSetting;
import com.isport.brandapp.device.bracelet.ActivityWatchFacesSet;
import com.isport.brandapp.device.bracelet.ActivityWeatherSetting;
import com.isport.brandapp.device.bracelet.CamaraActivity1;
import com.isport.brandapp.device.bracelet.ReportActivity;
import com.isport.brandapp.device.bracelet.Utils.DevicePicUtils;
import com.isport.brandapp.device.bracelet.bean.StateBean;
import com.isport.brandapp.device.bracelet.braceletPresenter.AlarmPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.DeviceGoalCaloriePresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.DeviceGoalDistancePresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.DeviceGoalStepPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.LiftWristPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.NoDisturbPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.W311RealTimeDataPresenter;
import com.isport.brandapp.device.bracelet.playW311.PlayW311Activity;
import com.isport.brandapp.device.bracelet.view.AlarmView;
import com.isport.brandapp.device.bracelet.view.DeviceGoalCalorieView;
import com.isport.brandapp.device.bracelet.view.DeviceGoalDistanceView;
import com.isport.brandapp.device.bracelet.view.DeviceGoalStepView;
import com.isport.brandapp.device.bracelet.view.LiftWristView;
import com.isport.brandapp.device.bracelet.view.NoDisturbView;
import com.isport.brandapp.device.bracelet.view.W311RealTimeDataView;
import com.isport.brandapp.device.publicpage.GoActivityUtil;
import com.isport.brandapp.device.watch.presenter.Device24HrPresenter;
import com.isport.brandapp.device.watch.presenter.DeviceBackLightTimeAndScreenLevePresenter;
import com.isport.brandapp.device.watch.presenter.WatchPresenter;
import com.isport.brandapp.device.watch.view.Device24HrView;
import com.isport.brandapp.device.watch.view.DeviceBackLightTimeAndScrenLeveView;
import com.isport.brandapp.device.watch.view.WatchView;
import com.isport.brandapp.dialog.UnBindDeviceDialog;
import com.isport.brandapp.dialog.UnbindStateCallBack;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.login.ShowPermissionActivity;
import com.isport.brandapp.upgrade.bean.DeviceUpgradeBean;
import com.isport.brandapp.upgrade.present.DevcieUpgradePresent;
import com.isport.brandapp.upgrade.view.DeviceUpgradeView;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.UserAcacheUtil;
import com.isport.brandapp.view.VerBatteryView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.core.app.ActivityCompat;
import bike.gymproject.viewlibray.ItemDeviceSettingView;
import bike.gymproject.viewlibray.WatchTypeDataView;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
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
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil;

/**
 * 手表设置页面
 */
public class ActivityWatchMain extends BaseMVPTitleActivity<WatchView, WatchPresenter> implements WatchView, W311RealTimeDataView, LiftWristView, View.OnClickListener, ItemDeviceSettingView.OnItemViewCheckedChangeListener, DeviceUpgradeView, DeviceBackLightTimeAndScrenLeveView, DeviceGoalStepView, DeviceGoalDistanceView, DeviceGoalCalorieView, AlarmView, NoDisturbView, Device24HrView {

    private final static String TAG = ActivityWatchMain.class.getSimpleName();

    private ItemDeviceSettingView ivWatchStepTarget, ivWatchDistanceTarget, ivWatchCalorieTarget, ivWatchStableRemind, ivWatch24HeartRate, ivWatchAlarmSetting, ivWatchSleepSetting, ivWatchDisturbSetting,
            ivWatchCallRemind, ivWatchMsgSetting, ivWatchPointerCali, ivWatchDefaultSetting, ivWatchStableVersion, iv_watch_take_photo, iv_watch_temp_sub;

    private ItemDeviceSettingView iv_find_bracelet,
            iv_watch_weather, iv_bracelet_lift_up_screen, iv_braclet_play, iv_watch_face, iv_watch_backlight_time, iv_watch_screen_luminance, iv_watch_temperature;
    private TextView tvWatchState, tvWatchBetteryCount, tvUnbind;
    private WatchTypeDataView wdvStep, wdvDis, wdvCal;
    private VerBatteryView iv_battery;

    private DeviceBean deviceBean;
    private int currentType;
    private boolean canUnBind;
    private DevcieUpgradePresent devcieUpgradePresent;
    private DeviceInformationTable deviceInfoByDeviceId;


    private LiftWristPresenter liftWristPresenter;


    private W311RealTimeDataPresenter realTimeDataPresenter;
    private ImageView ivWatch;
    private DeviceBackLightTimeAndScreenLevePresenter deviceBackLightTimeAndScreenLevePresenter;

    private StateBean stateBean;

    //查询556的闹钟个数
    private AlarmPresenter alarmPresenter;
    //勿扰模式开启情况
    private NoDisturbPresenter noDisturbPresenter;
    //查询温度的单位
    private boolean isOpenPageNotityState;
    private boolean isOpenOnPausePageNotityState;

    private Device24HrPresenter heartRatePresenter;

    //W526的屏幕时长和亮度
    private int w526BackLightTime, w526screenLeve;


    private DeviceGoalStepPresenter deviceGoalStepPresenter;
    private DeviceGoalDistancePresenter deviceGoalDistancePresenter;
    private DeviceGoalCaloriePresenter deviceGoalCaloriePresenter;
    RxPermissions mRxPermission;
    private boolean tempUti = true;//华氏度false，摄氏度true


    private AlertDialog.Builder watchAlert;

    @Override
    protected void onPause() {
        super.onPause();
        AppConfiguration.isWatchMain = false;
//        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfiguration.isWatchMain = true;


        if (!NotificationService.isEnabled(this)) {
            isOpenOnPausePageNotityState = false;
        } else {
            isOpenOnPausePageNotityState = true;
        }

        Logger.myLog(TAG,"isOpenPageNotityState:" + isOpenPageNotityState + ",isOpenOnPausePageNotityState:" + isOpenOnPausePageNotityState);

        //重新启动服务器接收消息
        if (!isOpenPageNotityState && isOpenOnPausePageNotityState) {
            NotificationService.toggleNotificationListenerService(this);
            isOpenPageNotityState = true;
        }


//        ISportAgent.getInstance().registerListener(mBleReciveListener);

        requstData(currentType);

    }





    private void setWatchBattery(int watchBattery) {
        if (watchBattery == -1) {
            tvWatchBetteryCount.setText(UIUtils.getString(R.string.no_data));
            iv_battery.setVisibility(View.INVISIBLE);
        } else {
            tvWatchBetteryCount.setText(watchBattery + "%");
            iv_battery.setVisibility(View.VISIBLE);
            iv_battery.setProgress(watchBattery);
        }


    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_watch_main;
    }

    @Override
    protected void initView(View view) {
        canUnBind = false;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        tvWatchState = view.findViewById(R.id.watch_state);
        tvWatchBetteryCount = view.findViewById(R.id.watch_bettery_count);
        iv_battery = view.findViewById(R.id.iv_battery);

        wdvStep = view.findViewById(R.id.wdv_step);
        wdvDis = view.findViewById(R.id.wdv_dis);
        wdvCal = view.findViewById(R.id.wdv_cal);
        iv_watch_temp_sub = view.findViewById(R.id.iv_watch_temp_sub);

        iv_watch_temperature = view.findViewById(R.id.iv_watch_temperature);

        ivWatchStepTarget = view.findViewById(R.id.iv_watch_step_target);
        ivWatchDistanceTarget = view.findViewById(R.id.iv_watch_distance_target);
        ivWatchCalorieTarget = view.findViewById(R.id.iv_watch_calorie_target);
        iv_watch_take_photo = view.findViewById(R.id.iv_watch_take_photo);
        ivWatchStableRemind = findViewById(R.id.iv_watch_stable_remind);
        ivWatch24HeartRate = view.findViewById(R.id.iv_watch_24_heart_rate);
        ivWatchAlarmSetting = view.findViewById(R.id.iv_watch_alarm_setting);
        ivWatchSleepSetting = view.findViewById(R.id.iv_watch_sleep_setting);
        ivWatchDisturbSetting = view.findViewById(R.id.iv_watch_disturb_setting);
        ivWatchDisturbSetting.setVisibility(View.GONE);
        ivWatchCallRemind = view.findViewById(R.id.iv_watch_call_remind);
        ivWatchMsgSetting = view.findViewById(R.id.iv_watch_msg_setting);
        ivWatchPointerCali = view.findViewById(R.id.iv_watch_pointer_cali);
        ivWatchDefaultSetting = view.findViewById(R.id.iv_watch_default_setting);
        ivWatchStableVersion = view.findViewById(R.id.iv_watch_stable_version);
        ivWatch = view.findViewById(R.id.watch);

        iv_find_bracelet = view.findViewById(R.id.iv_find_bracelet);
        iv_watch_weather = view.findViewById(R.id.iv_watch_weather);
        iv_bracelet_lift_up_screen = view.findViewById(R.id.iv_bracelet_lift_up_screen);
        iv_braclet_play = view.findViewById(R.id.iv_braclet_play);
        iv_watch_face = view.findViewById(R.id.iv_watch_face);
        iv_watch_backlight_time = view.findViewById(R.id.iv_watch_backlight_time);
        iv_watch_screen_luminance = view.findViewById(R.id.iv_watch_screen_luminance);


        tvUnbind = view.findViewById(R.id.tv_unbind);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
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

    private SpannableString getFormatHM(int min) {
        String day = CommonDateUtil.formatTwoStr(min / 60);
        String hours = CommonDateUtil.formatTwoStr(min % 60);
        String str = UIUtils.getContext().getString(R.string.app_time_util, day, hours);
        SpannableString span = new SpannableString(str);
        span.setSpan(new RelativeSizeSpan(0.32f), day.length(), str.lastIndexOf(hours),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new RelativeSizeSpan(0.32f), str.lastIndexOf(hours) + hours.length(), str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    UserInfoBean userInfo;

    @Override
    protected void initData() {
        mRxPermission = new RxPermissions(this);
        isOpenPageNotityState = false;
        isOpenOnPausePageNotityState = false;
        userInfo = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        getIntentData();
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle("--");
        setWatchState(UIUtils.getString(R.string.connected));
       // titleBarView.setRightIcon(R.drawable.icon_device_unbind);
        setWatchBattery(0);
        DevicePicUtils.setHeadPic(currentType, ivWatch);
        setShowItems(currentType);

        realTimeDataPresenter.getRealTimeDataW516OrW556();
        /*WatchRealTimeData watchRealTimeData1 = WatchRealTimeDataAction.getWatchRealTimeData
                (DateUtils
                        .getYMD(System.currentTimeMillis()), AppConfiguration.braceletID);
        if (watchRealTimeData1 != null) {
            setValueText(watchRealTimeData1.getStepNum() + "", "", 0f);
        } else {
            setValueText("0", "", 0f);
        }*/

        titleBarView.setTitle(deviceBean.deviceName);
        ivWatchDefaultSetting.setContentText(deviceBean.deviceName);


        getVersionOrBattery();
        //电话和信息
        heartRatePresenter.getMessageCallState(deviceBean.deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

        //  mActPresenter.getWatchLastSleepData(deviceBean.deviceName);
    }

    private void getIntentData() {
        deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);
        Logger.myLog(TAG,"deviceBean == " + deviceBean.toString());
        if (deviceBean != null) {
            currentType = deviceBean.currentType;
        }
    }


    private void setValueText(String step, String cal, String dis) {
        wdvCal.setValueText(cal);
        wdvDis.setValueText(dis);
        //根据step计算cal
        wdvStep.setValueText(step);
    }


    private void setValueText(String step, String cal, float dis) {


        if (TextUtils.isEmpty(step)) {
            step = "0";
        }

        int inStep = Integer.parseInt(step);


        wdvCal.setValueText((int) StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(userInfo.getWeight()), inStep) + "");
        wdvDis.setValueText(StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(userInfo.getHeight()), userInfo.getGender(), inStep) + "");


        //根据step计算cal
        wdvStep.setValueText(step);
    }

    private void setWatchState(String watchState) {
        tvWatchState.setText(watchState);
    }


    @Override
    protected void initEvent() {
        ivWatchStepTarget.setOnClickListener(this);
        ivWatchDistanceTarget.setOnClickListener(this);
        ivWatchCalorieTarget.setOnClickListener(this);
        ivWatchStableRemind.setOnClickListener(this);
        ivWatchAlarmSetting.setOnClickListener(this);
        ivWatchSleepSetting.setOnClickListener(this);
        ivWatchDisturbSetting.setOnClickListener(this);
        ivWatchPointerCali.setOnClickListener(this);
        ivWatchDefaultSetting.setOnClickListener(this);
        ivWatchStableVersion.setOnClickListener(this);
        tvWatchState.setOnClickListener(this);
        tvWatchBetteryCount.setOnClickListener(this);
        iv_watch_screen_luminance.setOnClickListener(this);
        iv_watch_backlight_time.setOnClickListener(this);
        iv_watch_temperature.setOnCheckedChangeListener(this);
        tvUnbind.setOnClickListener(this);

        ivWatch24HeartRate.setOnCheckedChangeListener(this);
        ivWatchCallRemind.setOnCheckedChangeListener(this);
        ivWatchMsgSetting.setOnCheckedChangeListener(this);

//        ivWatchMsgSetting.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                context.startActivity(new Intent(context, Test2Activity.class));
//                return true;
//            }
//        });

        ivWatchMsgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                startActivity(new Intent(ActivityWatchMain.this, ShowPermissionActivity.class));
            }
        });

        //计步目标设置
        ivWatchStepTarget.setOnContentClickListener(new ItemDeviceSettingView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                // 设置步数目标
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    return;
                }
                AppSP.putInt(ActivityWatchMain.this,AppSP.DEVICE_GOAL_KEY,0);
                mActPresenter.popWindowSelect(ActivityWatchMain.this, ivWatchStepTarget, JkConfiguration.GymUserInfo
                        .STEP_W311, ivWatchStepTarget.getRightTextValue(), false);
            }
        });

        //设置距离目标
        ivWatchDistanceTarget.setOnContentClickListener(new ItemDeviceSettingView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                // 设置距离目标
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    return;
                }
                AppSP.putInt(ActivityWatchMain.this,AppSP.DEVICE_GOAL_KEY,1);
                mActPresenter.popWindowSelect(ActivityWatchMain.this, ivWatchDistanceTarget, JkConfiguration.GymUserInfo
                        .DISTANCE_W560, ivWatchDistanceTarget.getRightTextValue(), false);
            }
        });

        ivWatchCalorieTarget.setOnContentClickListener(new ItemDeviceSettingView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                // 设置卡路里目标
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    return;
                }
                AppSP.putInt(ActivityWatchMain.this,AppSP.DEVICE_GOAL_KEY,2);
                mActPresenter.popWindowSelect(ActivityWatchMain.this, ivWatchCalorieTarget, JkConfiguration.GymUserInfo
                        .CALORIE_W3560, ivWatchCalorieTarget.getRightTextValue(), false);
            }
        });

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    return;
                }
                if (!AppConfiguration.hasSynced) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.sync_data));
                    return;
                }
                //名字不为空说明是绑定的设备，先解绑,手表和睡眠带要先同步后解绑
                isDerictUnBind = false;
                new UnBindDeviceDialog(ActivityWatchMain.this, JkConfiguration.DeviceType.WATCH_W516, true, new UnbindStateCallBack() {
                    @Override
                    public void synUnbind() {
                        if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                            ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                            return;
                        }
                        if (AppConfiguration.isConnected) {
                            // TODO: 2018/11/8 同步解绑的逻辑
//                                        if (FragmentData.mWristbandstep != null) {
//                                            mActPresenter.updateSportData(FragmentData.mWristbandstep, mDeviceBean);
//                                        }
                            Constants.isSyncUnbind = true;
                            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                            if (device != null) {
                                int currentDevice = device.deviceType;
                                if (DeviceTypeUtil.isContainWatch(currentDevice)) {
                                    //睡眠带连接
                                    String string = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_DAILY_RECORD, string);
                                    canUnBind = true;
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
        });
        wdvStep.setOnItemViewCheckedChangeListener(new WatchTypeDataView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id) {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intent1);
            }
        });
        wdvDis.setOnItemViewCheckedChangeListener(new WatchTypeDataView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id) {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intent1);
//                Intent intent = new Intent(context, LogActivity.class);
//                startActivity(intent);
            }
        });
        wdvCal.setOnItemViewCheckedChangeListener(new WatchTypeDataView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id) {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, deviceBean);
                startActivity(intent1);
            }
        });
        ISportAgent.getInstance().registerListener(mBleReciveListener);
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
                    case IResult.WATCH_REALTIME:
                        try {
                            WatchRealTimeResult watchRealTimeResult = (WatchRealTimeResult) mResult;
                            int step = watchRealTimeResult.getStepNum();
                            int heartRate = watchRealTimeResult.getHeartRate();
                            int realcal = 0;
                            float realdis = 0f;

                            if (((WatchRealTimeResult) mResult).getMac().contains("W516")) {
                                if (userInfo != null) {
                                    realcal = (int) StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(userInfo.getWeight()), watchRealTimeResult.getStepNum());
                                    realdis = StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(userInfo.getHeight()), userInfo.getGender(), watchRealTimeResult.getStepNum());

                                }
                            } else {
                                realcal = watchRealTimeResult.getCal();
                                float mStep = CommonDateUtil.formatFloor(watchRealTimeResult.getStepKm(), true);
                                realdis = mStep;
                            }
                            wdvStep.setValueText(step + "");

                            setValueText(step + "", realcal + "", CommonDateUtil.formatTwoPoint(realdis));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case IResult.WATCH_W516_SYNC:
                        if (isDerictUnBind) {
                            return;
                        }
                        if (AppConfiguration.isWatchMain && canUnBind) {
                            canUnBind = false;
                            //同步数据是否成功
                            WatchW516SyncResult watchW516SyncResult = (WatchW516SyncResult) mResult;
                            if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SUCCESS) {
                                AppConfiguration.hasSynced = true;
                                //ToastUtils.showToast(context, R.string.app_issync_complete);
                                //同步成功才能解绑
                                // TODO: 2019/3/4 上传数据到服务器的逻辑
                                mActPresenter.updateWatchHistoryData(deviceBean);
//                            }
                            } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.FAILED) {
                                AppConfiguration.hasSynced = true;
                                ToastUtils.showToast(context, R.string.app_issync_failed);
                            } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SYNCING) {
                                AppConfiguration.hasSynced = false;
                            }
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.SYNC_WATCH_SUCCESS));
                        }
                        break;
                    case IResult.DEVICE_GOAL_STEP:
                        System.out.println("******W560设置步数成功");
                        break;
                    case IResult.DEVICE_GOAL_DISTANCE:
                        System.out.println("******W560设置距离成功");
                        break;
                    case IResult.DEVICE_GOAL_CALORIE:
                        System.out.println("******W560设置卡路里成功");
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

            Logger.myLog("onBattreyOrVersion");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    getVersionOrBattery();
                }
            });


        }
    };

    Handler handler = new Handler();

    private void getVersionOrBattery() {
        deviceInfoByDeviceId = DeviceInformationTableAction.findDeviceInfoByDeviceId
                (deviceBean.deviceName);
        if (deviceInfoByDeviceId != null) {
            String version = deviceInfoByDeviceId.getVersion();

            if (TextUtils.isEmpty(version)) {
                version = "";
            }
            if (version.contains("V")) {
                version = version.replace("V", "");
            }
            if (version.contains("v")) {
                version = version.replace("v", "");
            }

            String strFirmwareVersion = String.format(getResources().getString(R.string.app_device_version),
                    version);
            ivWatchStableVersion.setContentText(strFirmwareVersion);
            if (deviceInfoByDeviceId != null && deviceInfoByDeviceId.getBattery() != 0) {
                devcieUpgradePresent.getDeviceUpgradeInfo(deviceBean.currentType);
                setWatchBattery(deviceInfoByDeviceId.getBattery());
            } else {
                setWatchBattery(-1);
            }
        }
    }

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
            case R.id.iv_watch_take_photo:  //遥控拍照
                checkCameraPersiomm();
                break;
            //温度设置
            case R.id.iv_watch_temperature:
                mActPresenter.popWindowSelect(ActivityWatchMain.this, iv_watch_temperature, JkConfiguration.GymUserInfo
                        .temperature_unitl, iv_watch_temperature.getRightTextValue(), false);
                break;
            case R.id.iv_watch_temp_sub:
                Intent intentTmepSub = new Intent(context, ActivityTempSubActivity.class);
                startActivity(intentTmepSub);
                break;

            //背光时长设置
            case R.id.iv_watch_backlight_time:
                mActPresenter.popWindowSelect(ActivityWatchMain.this, iv_watch_backlight_time, JkConfiguration.GymUserInfo
                        .BACKLIGHT_TIME, iv_watch_backlight_time.getRightTextValue(), false);
                break;
            //屏幕亮度设置
            case R.id.iv_watch_screen_luminance:
                mActPresenter.popWindowSelect(ActivityWatchMain.this, iv_watch_screen_luminance, JkConfiguration.GymUserInfo
                        .SCREEN_LUMINANCE, iv_watch_screen_luminance.getRightTextValue(), false);
                break;
            //天气设置
            case R.id.iv_watch_weather:
                Intent intentWeather = new Intent(context, ActivityWeatherSetting.class);
                intentWeather.putExtra("deviceType", deviceBean.currentType);
                startActivity(intentWeather);
                break;
            //使用指导
            case R.id.iv_braclet_play:
                Intent guideintent = new Intent(context, PlayW311Activity.class);
                guideintent.putExtra(JkConfiguration.DEVICE, deviceBean.currentType);
                startActivity(guideintent);
                break;

            //寻找手环
            case R.id.iv_find_bracelet:
                setfindBraceletValue();
                ISportAgent.getInstance().requestBle(BleRequest.Bracelet_w311_find_bracelect);
                break;
            // 抬腕亮屏设置
            case R.id.iv_bracelet_lift_up_screen:
                Intent intentScreen = new Intent(context, ActivityLiftWristSetting.class);
                intentScreen.putExtra("deviceBean", deviceBean);
                startActivity(intentScreen);
                break;
            //表盘设置
            case R.id.iv_watch_face:
                Intent intentDialSet = new Intent(context, ActivityWatchFacesSet.class);
                intentDialSet.putExtra("deviceBean", deviceBean);
                startActivity(intentDialSet);
                break;
            //久坐提醒
            case R.id.iv_watch_stable_remind:
                Intent intent2 = new Intent(context, ActivityDeviceSedentaryReminder.class);
                intent2.putExtra("deviceType", deviceBean.currentType);
                intent2.putExtra("deviceId", deviceBean.deviceName);
                startActivity(intent2);
                break;
            //闹钟设置
            case R.id.iv_watch_alarm_setting:

                Logger.myLog(TAG,"------闹钟="+deviceBean.toString()+"\n"+deviceBean.deviceType);

                if (deviceBean != null) {
                    if (deviceBean.deviceType == JkConfiguration.DeviceType.Watch_W560) {
                        Intent intent3 = new Intent(context, ActivityWatchW560AlarmList.class);
                        intent3.putExtra("deviceBean", deviceBean);
                        startActivity(intent3);
                    } else if (DeviceTypeUtil.isContainW55X(deviceBean.currentType)) {
                        Intent intent3 = new Intent(context, ActivityWatchW526AlarmList.class);
                        intent3.putExtra("deviceBean", deviceBean);
                        startActivity(intent3);
                    } else {
                        Intent intent3 = new Intent(context, ActivityWatchAlarmSetting.class);
                        intent3.putExtra("deviceBean", deviceBean);
                        startActivity(intent3);
                    }
                }
                break;
            case R.id.iv_watch_sleep_setting:
                //睡眠设置暂时是取消的
                Intent intent4 = new Intent(context, ActivityWatchSleepSetting.class);
                intent4.putExtra("deviceBean", deviceBean);
                startActivity(intent4);
                break;
            //勿扰设置
            case R.id.iv_watch_disturb_setting:
                Intent intent5 = new Intent(context, ActivityDeviceDoNotDistrubSetting.class);
                intent5.putExtra("deviceBean", deviceBean);
                startActivity(intent5);
                break;
            //校正指针
            case R.id.iv_watch_pointer_cali:
                Intent intent6 = new Intent(context, ActivityWatchPointerCali.class);
                startActivity(intent6);
                break;
            //恢复出厂设置
            case R.id.iv_watch_default_setting:
                if (AppConfiguration.isConnected) {
                    PublicAlertDialog.getInstance().showDialog("", getResources().getString(R.string.watch_default), context, getResources().getString(com.isport.brandapp.basicres.R.string.common_dialog_cancel), getResources().getString(com.isport.brandapp.basicres.R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                        @Override
                        public void determine() {
                            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_TEST_RESET);
                        }

                        @Override
                        public void cancel() {


                        }
                    }, false);

                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                }
                break;
            case R.id.iv_watch_stable_version:   //固件升级

                BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                if (device != null) {
                    Logger.myLog("iv_watch_stable_version=" + device + "");
                    // if (!DeviceTypeUtil.isContainW81(deviceBean.currentType)) {
                    ActivitySwitcher.goDFUAct(ActivityWatchMain.this, device.deviceType, device.deviceName, device.address, true);
                }
               /* //跳转到升级页面
                if (deviceBean != null) {
                    ActivitySwitcher.goDFUAct(ActivityWatchMain.this, deviceBean.currentType, deviceBean.deviceName, deviceBean.mac, true);
                }*/
                break;
            case R.id.tv_unbind:
                //名字不为空说明是绑定的设备，先解绑,手表和睡眠带要先同步后解绑
                isDerictUnBind = false;
                new UnBindDeviceDialog(this, JkConfiguration.DeviceType.WATCH_W516, true, new UnbindStateCallBack() {
                    @Override
                    public void synUnbind() {
                        if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                            ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                            return;
                        }
                        if (AppConfiguration.isConnected) {
                            // TODO: 2018/11/8 同步解绑的逻辑
//                                        if (FragmentData.mWristbandstep != null) {
//                                            mActPresenter.updateSportData(FragmentData.mWristbandstep, mDeviceBean);
//                                        }
                            Constants.isSyncUnbind = true;
                            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();


                            Logger.myLog(TAG,"--------解绑="+new Gson().toJson(device));
                            if (device != null) {
                                int currentDevice = device.deviceType;
                                if (DeviceTypeUtil.isContainWatch(currentDevice)) {
                                    //睡眠带连接
                                    String string = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_DAILY_RECORD, string);
                                    canUnBind = true;
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
                break;
        }
    }

    boolean isDerictUnBind;

    private void unBindDevice(DeviceBean deviceBean, boolean dirct) {
        isDerictUnBind = true;
        currentType = deviceBean.currentType;
        Logger.myLog("点击去解绑 == " + currentType+"\n"+deviceBean.toString());
        //解绑前断连设备
        mActPresenter.unBind(deviceBean, dirct);
    }

    @Override
    public void onCheckedChanged(int id, boolean isChecked) {
        switch (id) {
            case R.id.iv_watch_24_heart_rate:  //24小时心率开关
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    ivWatch24HeartRate.setChecked(!isChecked);
                    return;
                }
                if (AppConfiguration.hasSynced) {
                    stateBean.isHrState = isChecked;
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, isChecked, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
                    if (!isChecked) {
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.endHr));
                    }

                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.sync_data));
                    ivWatch24HeartRate.setChecked(!isChecked);
                }

                break;
            case R.id.iv_watch_call_remind:
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    ivWatchCallRemind.setChecked(!isChecked);
                    return;
                }
                if(!isChecked){
                    ivWatchCallRemind.setChecked(false);
                    updateNotifySettingCall(false);
                    return;
                }

                showAlertMsg(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS},"电话提醒需要以下权限:\n获取手机状态权限\n获取手机电话号码\n读取联系人和通讯录权限\n是否同意授权?",0,isChecked);
                ivWatchCallRemind.setChecked(isChecked);
                updateNotifySettingCall(isChecked);
              //  checkPhonePerssion(isChecked);
                 //setCallSwitchState(isChecked);
                break;
            case R.id.iv_watch_msg_setting:
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    ivWatchMsgSetting.setChecked(!isChecked);
                    return;
                }

//                if (!isChecked && !NotificationService.isEnabled(this)) {
//                    ivWatchMsgSetting.setChecked(false);
//                    return;
//                }
                if(!isChecked){
                    ivWatchMsgSetting.setChecked(false);
                    updateNotifySettingMsg(false);
                    return;
                }

                showAlertMsg(new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CONTACTS},"短信提醒需要以下权限:\n获取手机短信\n接收短信\n是否同意授权?",1,isChecked);
//                setMessageSwitchState(isChecked);

                ivWatchMsgSetting.setChecked(isChecked);
                updateNotifySettingMsg(isChecked);
                break;
        }
    }


    private void updateNotifySettingMsg(boolean msgSwitch) {
        if (AppConfiguration.isConnected) {
            ParseData.saveW516CallMessageRemind(deviceBean.deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), msgSwitch, 1);
            stateBean.isMessage = msgSwitch;
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, stateBean.isHrState, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
        } else {
            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
        }
//        }
    }

    private void updateNotifySettingCall(boolean callSwitch) {
        if (AppConfiguration.isConnected) {
            ParseData.saveW516CallMessageRemind(deviceBean.deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), callSwitch, 0);
            stateBean.setCall(callSwitch);
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, stateBean.isHrState, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
        } else {
            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
        }
//        }

    }

    @Override
    protected WatchPresenter createPresenter() {
        realTimeDataPresenter = new W311RealTimeDataPresenter(this);
        heartRatePresenter = new Device24HrPresenter(this);
        alarmPresenter = new AlarmPresenter(this);
        noDisturbPresenter = new NoDisturbPresenter(this);
        devcieUpgradePresent = new DevcieUpgradePresent(this);
        deviceBackLightTimeAndScreenLevePresenter = new DeviceBackLightTimeAndScreenLevePresenter(this);
        deviceGoalStepPresenter = new DeviceGoalStepPresenter(this);
        deviceGoalDistancePresenter = new DeviceGoalDistancePresenter(this);
        deviceGoalCaloriePresenter = new DeviceGoalCaloriePresenter(this);
        liftWristPresenter = new LiftWristPresenter(this);
        return new WatchPresenter(this);
    }

    @Override
    public void dataSetSuccess(View view, String select, String data) {
        int target = 0;
        Logger.myLog("dataSetSuccess:" + data + ",select:" + select);
        if (view == ivWatchStepTarget) {
            // 选择步数目标回调
            ivWatchStepTarget.setContentText(data);
            target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_steps))[0]);
            deviceGoalStepPresenter.saveDeviceGoalStep(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, target);

        } else if (view == ivWatchDistanceTarget) {
            // 选择距离目标回调
            ivWatchDistanceTarget.setContentText(data);
            target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_meters))[0]);
            deviceGoalDistancePresenter.saveDeviceGoalDistance(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, target);

        } else if (view == ivWatchCalorieTarget) {
            // 选择卡路里目标回调
            ivWatchCalorieTarget.setContentText(data);
            target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_kcal))[0]);
            deviceGoalCaloriePresenter.saveDeviceGoalCalorie(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, target);

        } else if (view == iv_watch_temperature) {
            iv_watch_temperature.setContentText(data);
            if (stateBean != null) {
                if (data.equals(UIUtils.getString(R.string.temperature_degree_centigrade))) {
                    stateBean.tempUnitl = "0";
                    BaseManager.isTmepUnitl = "0";
                    heartRatePresenter.saveTempUtil(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "0");
                } else {
                    stateBean.tempUnitl = "1";
                    BaseManager.isTmepUnitl = "1";
                    heartRatePresenter.saveTempUtil(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), "1");

                }
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, stateBean.isHrState, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
            }
        } else if (view == iv_watch_backlight_time) {  //背光时长
            iv_watch_backlight_time.setContentText(data);
            target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_backlight_time))[0]);
            w526BackLightTime = target;
            deviceBackLightTimeAndScreenLevePresenter.saveDeviceBackLightTimeOrScreenLeve(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, target, 1);


        } else if (view == iv_watch_screen_luminance) {
            iv_watch_screen_luminance.setContentText(data);
            target = Integer.parseInt(data.split(" " + UIUtils.getString(R.string.unit_screen_luminance))[0]);
            w526screenLeve = target;
            deviceBackLightTimeAndScreenLevePresenter.saveDeviceBackLightTimeOrScreenLeve(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, target, 0);
        } else {
            Logger.myLog(" iv_watch_screen_luminance noselect:" + data);
        }
    }

    @Override
    public void onUnBindSuccess() {
        //解绑的是当前连接的设备,需要断连设备
        if (AppConfiguration.isConnected) {
            BaseDevice currnetDevice = ISportAgent.getInstance().getCurrnetDevice();
            if (currnetDevice != null && currnetDevice.deviceType == currentType) {
                Logger.myLog("currnetDevice == " + currentType);
                //解绑设备，不用重连
                ISportAgent.getInstance().unbind(false);
            }
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
        GoActivityUtil.goActivityUnbindDevice(currentType, ActivityWatchMain.this);
        finish();
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

    //久坐提醒
    @Override
    public void seccessGetDeviceSedentaryReminder(Watch_W516_SedentaryModel watch_w516_sedentaryModel) {

        if (watch_w516_sedentaryModel != null) {
            if (watch_w516_sedentaryModel.getLongSitTimeLong() > 5) {
                ivWatchStableRemind.setContentText(watch_w516_sedentaryModel.getLongSitStartTime() + "-" + watch_w516_sedentaryModel.getLongSitEndTime());
                //ivWatchStableRemind.setContentText(UIUtils.getString(R.string.setting_start));
            } else {
                ivWatchStableRemind.setContentText(UIUtils.getString(R.string.display_no_count));
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isSyncUnbind = false;
        SyncCacheUtils.setUnBindState(false);
        EventBus.getDefault().unregister(this);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    private void checkPhonePerssion(boolean isCheak) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ANSWER_PHONE_CALLS},0x00 );
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG},0x01);

        //READ_PHONE_STATE
        //READ_CALL_LOG
        //CALL_PHONE
        //READ_CONTACTS
        // || !mRxPermission.isGranted(Manifest.permission.READ_CALL_LOG)
        //                || !mRxPermission.isGranted(Manifest.permission.CALL_PHONE)
        //                || !mRxPermission.isGranted(Manifest.permission.READ_CONTACTS)
        if (!mRxPermission.isGranted(Manifest.permission.READ_PHONE_STATE) || !mRxPermission.isGranted(Manifest.permission.CALL_PHONE)) {

            PermissionManageUtil permissionManage = new PermissionManageUtil(this);
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
                            ivWatchCallRemind.setChecked(false);
                            updateNotifySettingCall(false);
                            ToastUtil.showTextToast(ActivityWatchMain.this, UIUtils.getString(R.string.location_permissions));
                        }
                    });
        } else {
            ivWatchCallRemind.setChecked(isCheak);
            updateNotifySettingCall(isCheak);
        }
    }

    public boolean isHaveCallPremission() {


        RxPermissions mRxPermission = new RxPermissions(this);
        if (!mRxPermission.isGranted(Manifest.permission.READ_PHONE_STATE) || !mRxPermission.isGranted(Manifest.permission.READ_CALL_LOG)
                || !mRxPermission.isGranted(Manifest.permission.CALL_PHONE)
                || !mRxPermission.isGranted(Manifest.permission.READ_CONTACTS)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isHaveMessagePremission() {
        RxPermissions mRxPermission = new RxPermissions(this);
        if (!mRxPermission.isGranted(Manifest.permission.READ_SMS) || !NotificationService.isEnabled(this)) {
            return false;
        } else {
            return true;
        }
    }

    public void setCallSwitchState(boolean isChecked) {

        ivWatchCallRemind.setChecked(false);
        //读取手机状态  不论时拒绝还是同意都不做操作，应该设置为关闭状态
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        RxPermissions mRxPermission = new RxPermissions(this);
        if (!mRxPermission.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            permissionManage.requestPermissions(mRxPermission, Manifest.permission.READ_PHONE_STATE,
                    UIUtils.getString(R.string.permission_location2), new
                            PermissionManageUtil.OnGetPermissionListener() {
                                @Override
                                public void onGetPermissionYes() {
                                    ivWatchCallRemind.setChecked(false);
                                }

                                @Override
                                public void onGetPermissionNo() {
                                    ivWatchCallRemind.setChecked(false);
//                                            ToastUtils.showToastLong(context, UIUtils.getString(R.string.location_permissions));
                                }
                            });
        } else if (!mRxPermission.isGranted(Manifest.permission.READ_CALL_LOG)) {
            permissionManage.requestPermissions(mRxPermission, Manifest.permission.READ_CALL_LOG,
                    UIUtils.getString(R.string.permission_location2), new
                            PermissionManageUtil.OnGetPermissionListener() {
                                @Override
                                public void onGetPermissionYes() {
                                    ivWatchCallRemind.setChecked(false);

                                }

                                @Override
                                public void onGetPermissionNo() {
                                    ivWatchCallRemind.setChecked(false);
                                }
                            });
        } else {
            ivWatchCallRemind.setChecked(isChecked);
            updateNotifySettingCall(isChecked);
        }

    }

    public void setMessageSwitchState(boolean isChecked) {
        ivWatchMsgSetting.setChecked(false);
        //先请求SMS权限 RECEIVE_SMS  不论时拒绝还是同意都不做操作，应该设置为关闭状态
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS},0x00);
        PermissionManageUtil permissionManage1 = new PermissionManageUtil(context);
        RxPermissions mRxPermission1 = new RxPermissions(this);
        if (!mRxPermission1.isGranted(Manifest.permission.READ_SMS)) {
            permissionManage1.requestPermissions(mRxPermission1, Manifest.permission.READ_SMS,
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
            if (!NotificationService.isEnabled(this)) {
                Logger.myLog("没有打开权限，要去打开权限");
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
                ivWatchMsgSetting.setChecked(false);
            } else {
                //已经打开了权限
                Logger.myLog("已有权限，打开信息通知");
                ivWatchMsgSetting.setChecked(isChecked);
                updateNotifySettingMsg(isChecked);
            }
        }
    }

    @Override
    public void successDeviceUpgradeInfo(DeviceUpgradeBean deviceUpgradeBean) {

        if (deviceUpgradeBean != null && deviceInfoByDeviceId != null) {
            String currentVersion = TextUtils.isEmpty(deviceInfoByDeviceId.getVersion()) ? "" : deviceInfoByDeviceId.getVersion();
            String serVersion = TextUtils.isEmpty(deviceUpgradeBean.getAppVersionName()) ? "" : deviceUpgradeBean.getAppVersionName();


            if (currentVersion.contains("V")) {
                currentVersion = currentVersion.replace("V", "");
            }
            if (currentVersion.contains("v")) {
                currentVersion = currentVersion.replace("v", "");
            }

            String strFirmwareVersion = String.format(getResources().getString(R.string.app_device_version),
                    currentVersion);

            if (currentVersion.equals(serVersion)) {
                ivWatchStableVersion.setContentText(strFirmwareVersion + UIUtils.getString(R.string.no_update));
            } else {
                ivWatchStableVersion.setContentText(strFirmwareVersion + UIUtils.getString(R.string.has_update));
            }
        }
    }


    private void setShowItems(int currentType) {
        iv_watch_take_photo.setVisibility(View.GONE);
        iv_watch_temp_sub.setVisibility(View.GONE);
        iv_watch_temperature.setVisibility(View.GONE);
        ivWatchStepTarget.setVisibility(View.GONE);
        ivWatchStableRemind.setVisibility(View.GONE);
        ivWatch24HeartRate.setVisibility(View.GONE);
        ivWatchAlarmSetting.setVisibility(View.GONE);
        ivWatchSleepSetting.setVisibility(View.GONE);
        ivWatchDisturbSetting.setVisibility(View.GONE);
        ivWatchCallRemind.setVisibility(View.GONE);
        ivWatchMsgSetting.setVisibility(View.GONE);
        ivWatchPointerCali.setVisibility(View.GONE);
        ivWatchDefaultSetting.setVisibility(View.GONE);
        ivWatchStableVersion.setVisibility(View.GONE);
        iv_find_bracelet.setVisibility(View.GONE);
        iv_watch_weather.setVisibility(View.GONE);
        iv_bracelet_lift_up_screen.setVisibility(View.GONE);
        iv_braclet_play.setVisibility(View.GONE);
        iv_watch_face.setVisibility(View.GONE);
        iv_watch_backlight_time.setVisibility(View.GONE);
        iv_watch_screen_luminance.setVisibility(View.GONE);

        if (currentType == JkConfiguration.DeviceType.WATCH_W516) {
            ivWatchStepTarget.setVisibility(View.VISIBLE);
            ivWatchStableRemind.setVisibility(View.VISIBLE);
            ivWatch24HeartRate.setVisibility(View.VISIBLE);
            ivWatchAlarmSetting.setVisibility(View.VISIBLE);
            //ivWatchDisturbSetting.setVisibility(View.VISIBLE);
            ivWatchCallRemind.setVisibility(View.VISIBLE);
            ivWatchMsgSetting.setVisibility(View.VISIBLE);
            ivWatchPointerCali.setVisibility(View.VISIBLE);
            ivWatchStableVersion.setVisibility(View.VISIBLE);
            iv_braclet_play.setVisibility(View.VISIBLE);
        } else if (DeviceTypeUtil.isContainW55X(currentType)) {
            iv_watch_take_photo.setVisibility(View.VISIBLE);
            iv_watch_temperature.setVisibility(View.VISIBLE);
            if (currentType == JkConfiguration.DeviceType.Watch_W557) {
                iv_watch_temp_sub.setVisibility(View.VISIBLE);
            }
            ivWatchStepTarget.setVisibility(View.VISIBLE);
            ivWatchStableRemind.setVisibility(View.VISIBLE);
            ivWatch24HeartRate.setVisibility(View.VISIBLE);
            ivWatchAlarmSetting.setVisibility(View.VISIBLE);
            ivWatchDisturbSetting.setVisibility(View.VISIBLE);
            ivWatchCallRemind.setVisibility(View.VISIBLE);
            ivWatchMsgSetting.setVisibility(View.VISIBLE);
            ivWatchStableVersion.setVisibility(View.VISIBLE);
            iv_find_bracelet.setVisibility(View.VISIBLE);
            iv_watch_weather.setVisibility(View.VISIBLE);
            iv_bracelet_lift_up_screen.setVisibility(View.VISIBLE);
            iv_braclet_play.setVisibility(View.VISIBLE);
            iv_watch_face.setVisibility(View.VISIBLE);
            iv_watch_backlight_time.setVisibility(View.VISIBLE);
            iv_watch_screen_luminance.setVisibility(View.VISIBLE);

            if (currentType == JkConfiguration.DeviceType.Watch_W560 || currentType == JkConfiguration.DeviceType.Watch_W560B) {

                boolean isW560B = currentType == JkConfiguration.DeviceType.Watch_W560B;

                ivWatchDistanceTarget.setVisibility(isW560B ? View.GONE : View.VISIBLE);
                ivWatchCalorieTarget.setVisibility(isW560B ? View.GONE : View.VISIBLE);
            }
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
    public void successGetBackLightScreen(int screenLeve, int valueBackLightTime) {
        Logger.myLog("getWatchFace successGetBackLightScreen screenLeve" + screenLeve + "valueBackLightTime:" + valueBackLightTime);
        w526BackLightTime = valueBackLightTime;
        w526screenLeve = screenLeve;
        iv_watch_backlight_time.setContentText(valueBackLightTime + " " + UIUtils.getString(R.string.unit_backlight_time));
        iv_watch_screen_luminance.setContentText(screenLeve + " " + UIUtils.getString(R.string.unit_screen_luminance));


    }

    @Override
    public void successSaveValue() {
        Logger.myLog("getWatchFace successGetBackLightScreen screenLeve" + w526screenLeve + "valueBackLightTime:" + w526BackLightTime);

        sendCmd(w526screenLeve, w526BackLightTime);
    }


    public void sendCmd(int leve, int time) {
        if (!AppConfiguration.isConnected) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
            return;
        }
        if (!AppConfiguration.hasSynced) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.sync_data));
            return;
        }
        ISportAgent.getInstance().requestBle(BleRequest.DEVICE_BACKLIGHT_SCREEN_TIME, leve, time);

    }


    public void requstData(int currentType) {
        Logger.myLog(TAG,"---AppConfiguration.braceletID="+AppConfiguration.braceletID);


        deviceGoalStepPresenter.getDeviceGoalStep(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
        deviceGoalDistancePresenter.getDeviceGoalDistance(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
        deviceGoalCaloriePresenter.getDeviceGoalCalorie(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
        if (currentType == JkConfiguration.DeviceType.WATCH_W516) {

        } else if (DeviceTypeUtil.isContainW55X(currentType)) {
            //获取560的闹钟
            //ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_ALARM);
            liftWristPresenter.getLiftWristBean(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceBean.deviceID);
            alarmPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
            deviceBackLightTimeAndScreenLevePresenter.getDeviceBackLightAndScreenLeve(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
        }
        noDisturbPresenter.getNodisturb(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
        mActPresenter.getDeviceSedentaryReminder(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
    }

    @Override
    public void successGetGoalStep(int Step) {
        JkConfiguration.WATCH_GOAL = Step;
        ivWatchStepTarget.setContentText(Step + " " + UIUtils.getString(R.string.unit_steps));
    }

    @Override
    public void successSaveGoalStep(int step) {

        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().setStepTarget(step);
            if (DeviceTypeUtil.isContainW55X(currentType)) {
                //w812-w814有专门设置这个属性的接口
                ISportAgent.getInstance().requsetW311Ble(BleRequest.device_target_step, step);
            }
        }

        JkConfiguration.WATCH_GOAL = step;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_WATCH_TARGET));
    }

    @Override
    public void successGetGoalDistance(int distance) {
        ivWatchDistanceTarget.setContentText(distance + " " + UIUtils.getString(R.string.unit_meters));
        JkConfiguration.WATCH_GOAL_DISTANCE = distance;
    }

    //设置距离目标
    @Override
    public void successSaveGoalDistance(int distance) {
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().setDistanceTarget(distance);
            if (currentType == JkConfiguration.DeviceType.Watch_W560) {
                //w812-w814有专门设置这个属性的接口
                ISportAgent.getInstance().requsetW311Ble(BleRequest.device_target_distance, distance);
            }
        }
        JkConfiguration.WATCH_GOAL_DISTANCE = distance;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_WATCH_TARGET));
    }

    @Override
    public void successGetGoalCalorie(int calorie) {
        JkConfiguration.WATCH_GOAL_CALORIE = calorie;
        ivWatchCalorieTarget.setContentText(calorie + " " + UIUtils.getString(R.string.unit_kcal));
    }

    @Override
    public void successSaveGoalCalorie(int calorie) {
        JkConfiguration.WATCH_GOAL_CALORIE = calorie;
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().setCalorieTarget(calorie);
            if (currentType == JkConfiguration.DeviceType.Watch_W560) {
                //w812-w814有专门设置这个属性的接口
                ISportAgent.getInstance().requsetW311Ble(BleRequest.device_target_calorie, calorie);
            }
        }

        EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_WATCH_TARGET));
    }

    @Override
    public void successAllAlarmItem(ArrayList<Bracelet_W311_AlarmModel> bracelet_w311_displayModel) {
        Logger.myLog(TAG,"------查询闹钟="+new Gson().toJson(bracelet_w311_displayModel));
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
    public void successDisturb(boolean isOpen) {
        if (isOpen) {
            ivWatchDisturbSetting.setContentText(UIUtils.getString(R.string.setting_start));
        } else {
            ivWatchDisturbSetting.setContentText(UIUtils.getString(R.string.display_no_count));
        }
    }

    @Override
    public void successDisturb(Watch_W516_SleepAndNoDisturbModel isOpen) {

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

    @Override
    public void success24HrSwitch(boolean isOpen) {

    }

    @Override
    public void successState(StateBean stateBean) {
        this.stateBean = stateBean;
        Logger.myLog(TAG,"getMsgSwitch== " + stateBean.toString() );
        //这里需要去判断权限如果没有权限就关闭
        if (stateBean.tempUnitl.equals("0")) {
            iv_watch_temperature.setContentText(UIUtils.getString(R.string.temperature_degree_centigrade));
        } else {
            iv_watch_temperature.setContentText(UIUtils.getString(R.string.temperature_fahrenheit));
        }

        if (isHaveMessagePremission() && NotificationService.isEnabled(this)) {
            ivWatchMsgSetting.setChecked(stateBean.isMessage);
        } else {
            ivWatchMsgSetting.setChecked(false);
        }
        ivWatchCallRemind.setChecked(stateBean.isCall());
//        if (isHaveCallPremission()) {
//            ivWatchCallRemind.setChecked(stateBean.isCall);
//        } else {
//            ivWatchCallRemind.setChecked(false);
//        }

        if (!NotificationService.isEnabled(this)) {
            isOpenPageNotityState = false;
        } else {
            isOpenPageNotityState = true;
        }
        ivWatch24HeartRate.setChecked(stateBean.isHrState);


    }

    @Override
    public void getW311RealTimeData(Bracelet_W311_RealTimeData bracelet_w311_wearModel) {

    }

    @Override
    public void getW516OrW556(WatchRealTimeData bracelet_w311_wearModel) {
        if (bracelet_w311_wearModel != null) {
            setValueText(bracelet_w311_wearModel.getStepNum() + "", bracelet_w311_wearModel.getCal() + "", CommonDateUtil.formatTwoPoint(bracelet_w311_wearModel.getStepKm()));
        }

    }

    @Override
    public void successSaveRealTimeData(boolean isSave) {

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

                        ToastUtil.showTextToast(ActivityWatchMain.this, UIUtils.getString(R.string.location_permissions));
                    }
                });

    }


    String strlastListState;

    @Override
    public void successLifWristBean(Bracelet_W311_LiftWristToViewInfoModel model) {
        switch (model.getSwichType()) {


            case 0:
                // ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_is_open_raise_hand, true);
                strlastListState = UIUtils.getString(R.string.lift_to_view_info_all_day);
                iv_bracelet_lift_up_screen.setContentText(strlastListState);
                break;
            case 1:
                strlastListState = UIUtils.getString(R.string.lift_to_view_info_Timing_open);
                iv_bracelet_lift_up_screen.setContentText(strlastListState);
                break;
            case 2:
                strlastListState = UIUtils.getString(R.string.lift_to_view_info_close);
                iv_bracelet_lift_up_screen.setContentText(strlastListState);
                break;
        }

    }


    @Override
    public void successSave(boolean isSave) {

    }


    private void showAlertMsg(String[] perss,String msg,int code,boolean isChecked){
        watchAlert = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.tips))
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getPermiss(perss,code,isChecked);
                    }
                }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog pAt = watchAlert.create();
        pAt.show();
        Button sureBtn = pAt.getButton(DialogInterface.BUTTON_POSITIVE);
        Button cancleBtn = pAt.getButton(DialogInterface.BUTTON_NEGATIVE);
        sureBtn.setTextColor(Color.BLACK);
        cancleBtn.setTextColor(Color.BLACK);
    }


    private void getPermiss(String[] permiss,int code,boolean isCheck){
        XXPermissions.with(this)
                .permission(permiss)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> list, boolean b) {

                    }
                });

        if(code == 0){  //电话
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                XXPermissions.with(ActivityWatchMain.this)
                        .permission(Manifest.permission.ANSWER_PHONE_CALLS)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> list, boolean b) {

                            }
                        });
            }
        }

    }
}
