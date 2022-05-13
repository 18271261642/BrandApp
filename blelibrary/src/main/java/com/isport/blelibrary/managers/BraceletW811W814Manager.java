package com.isport.blelibrary.managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.bean.CRPAlarmClockInfo;
import com.crrepa.ble.conn.bean.CRPBloodOxygenInfo;
import com.crrepa.ble.conn.bean.CRPBloodPressureInfo;
import com.crrepa.ble.conn.bean.CRPFutureWeatherInfo;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPHistoryBloodOxygenInfo;
import com.crrepa.ble.conn.bean.CRPHistoryBloodPressureInfo;
import com.crrepa.ble.conn.bean.CRPHistoryHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMessageInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPPeriodTimeInfo;
import com.crrepa.ble.conn.bean.CRPSedentaryReminderPeriodInfo;
import com.crrepa.ble.conn.bean.CRPSleepInfo;
import com.crrepa.ble.conn.bean.CRPStepInfo;
import com.crrepa.ble.conn.bean.CRPStepsCategoryInfo;
import com.crrepa.ble.conn.bean.CRPTodayWeatherInfo;
import com.crrepa.ble.conn.bean.CRPUserInfo;
import com.crrepa.ble.conn.callback.CRPDeviceAlarmClockCallback;
import com.crrepa.ble.conn.callback.CRPDeviceDisplayWatchFaceCallback;
import com.crrepa.ble.conn.callback.CRPDeviceFirmwareVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceGoalStepCallback;
import com.crrepa.ble.conn.callback.CRPDeviceLanguageCallback;
import com.crrepa.ble.conn.callback.CRPDeviceOtherMessageCallback;
import com.crrepa.ble.conn.callback.CRPDevicePeriodTimeCallback;
import com.crrepa.ble.conn.callback.CRPDeviceQuickViewCallback;
import com.crrepa.ble.conn.callback.CRPDeviceSedentaryReminderCallback;
import com.crrepa.ble.conn.callback.CRPDeviceSedentaryReminderPeriodCallback;
import com.crrepa.ble.conn.callback.CRPDeviceTimeSystemCallback;
import com.crrepa.ble.conn.callback.CRPDeviceTimingMeasureHeartRateCallback;
import com.crrepa.ble.conn.callback.CRPDeviceVersionCallback;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;
import com.crrepa.ble.conn.listener.CRPBleECGChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPCameraOperationListener;
import com.crrepa.ble.conn.listener.CRPDeviceBatteryListener;
import com.crrepa.ble.conn.listener.CRPFindPhoneListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.listener.CRPPhoneOperationListener;
import com.crrepa.ble.conn.listener.CRPSleepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepsCategoryChangeListener;
import com.crrepa.ble.conn.listener.CRPWeatherChangeListener;
import com.crrepa.ble.conn.type.CRPDeviceLanguageType;
import com.crrepa.ble.conn.type.CRPDeviceVersionType;
import com.crrepa.ble.conn.type.CRPHeartRateType;
import com.crrepa.ble.conn.type.CRPHistoryDynamicRateType;
import com.crrepa.ble.conn.type.CRPPastTimeType;
import com.crrepa.ble.conn.type.CRPPhoneOperationType;
import com.google.gson.Gson;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.W81Device.W81DeviceEexerciseAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_AlarmModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_SettingModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_liftwristModelAction;
import com.isport.blelibrary.db.parse.DeviceDataSave;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24H_hr_SettingModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.deviceEntry.impl.W812Device;
import com.isport.blelibrary.deviceEntry.impl.W813Device;
import com.isport.blelibrary.deviceEntry.impl.W814Device;
import com.isport.blelibrary.deviceEntry.impl.W817Device;
import com.isport.blelibrary.deviceEntry.impl.W819Device;
import com.isport.blelibrary.deviceEntry.impl.W910Device;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.entry.AlarmEntry;
import com.isport.blelibrary.entry.SedentaryRemind;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.observe.TakePhotObservable;
import com.isport.blelibrary.result.impl.w311.BraceletW311RealTimeResult;
import com.isport.blelibrary.result.impl.w311.BraceletW311SyncComplete;
import com.isport.blelibrary.result.impl.w311.BraceletW311SyncStart;
import com.isport.blelibrary.result.impl.watch.DeviceAlarmListResult;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.result.impl.watch.WatchFACEResult;
import com.isport.blelibrary.result.impl.watch.WatchGOALSTEPResult;
import com.isport.blelibrary.result.impl.watch.WatchHrHeartResult;
import com.isport.blelibrary.result.impl.watch.WatchVersionResult;
import com.isport.blelibrary.utils.AppLanguageUtil;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.CmdUtil;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DeviceTimesUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BraceletW811W814Manager extends BaseManager {

    public static volatile int deviceConnState;

    public static String TAG = "BraceletW811W814Manager";

    public static volatile CRPBleClient mBleClient = null;
    public CRPBleDevice mBleDevice = null;
    public CRPBleConnection mBleConnection = null;
    protected CRPUserInfo crpUserInfo;
    protected final Integer SYNC_TIMEOUT = 15000;
    static boolean isConnect = true;

    protected volatile boolean isMusicStart = false;

    private static volatile int BLU_STATE = 0;


    protected volatile int versionCode;


    private Handler mHandler;
    private Handler mReHandler;
    private Handler timeOutHandler;

    static volatile Long connectStateSTATE_CONNECTED = 0L;
    static volatile Long connectStateSTATE_CONNECTING = 0L;
    static volatile Long connectStateSTATE_DISCONNECTED = 0L;
    static volatile Long connectTime = 0L;
    private volatile Long syncYestodayStep = 0L;
    private volatile Long syncBefordayStep = 0L;
    private volatile Long syncTodayStep = 0L;
    private volatile Long syncYestodaySleep = 0L;
    private volatile Long syncBefordaySleep = 0L;
    private volatile Long syncOnceHr = 0L;
    private volatile Long syncPractiseHr = 0L;
    private volatile Long syncHrHistory = 0L;
    private volatile Long syncHrTodayHistory = 0L;
    private volatile Long syncHrYesterdayHistory = 0L;
    private volatile Long movementMeasureResult = 0L;
    private volatile Long bloodPressureChange = 0L;
    private volatile Long bloodOxygenChange = 0L;
    private volatile Long getAlarListTime = 0L;
    private volatile Long updateWheather = 0L;
    private volatile Long onDeviceBattery = 0L;
    private volatile Long onOperationChange = 0L;
    private volatile Long onTakePhoto = 0L;
    private volatile Long onTimeSystem = 0L;
    private volatile Long onDisplayWatchFace = 0L;
    private volatile Long onQuickView = 0L;
    private volatile Long onPeriodTime2 = 0L;
    private volatile Long onPeriodTime1 = 0L;
    private volatile Long onSedentaryReminder = 0L;
    private volatile Long onGoalStep = 0L;
    private volatile Long onTimingMeasure = 0L;
    private volatile Long onOtherMessage = 0L;
    public static volatile BraceletW811W814Manager instance;
    //需要加一个同步数据的并发处理


    //CRPTimeSystemType.TIME_SYSTEM_12 ==0  CRPTimeSystemType.TIME_SYSTEM_24==1
    public void sendTimeSystem(int time) {
        Logger.myLog(TAG + "sendTimeSystem：" + time);
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.sendTimeSystem((byte) time);
        }
    }


    public static BraceletW811W814Manager getInstance(Context context) {
        if (instance == null) {
            synchronized (BraceletW811W814Manager.class) {
                if (instance == null) {
                    instance = new BraceletW811W814Manager();
                    instance.init(context);
                }
            }
        }
        return instance;
    }


    public static BraceletW811W814Manager getInstance() {
        if (instance == null) {
            synchronized (BraceletW811W814Manager.class) {
                if (instance == null) {
                    instance = new BraceletW811W814Manager();
                }
            }
        }
        return instance;
    }

    public void disconnect(boolean reconnect) {


        if (!reconnect) {
            mHandler.removeMessages(0);
        }
        Constants.CAN_RECONNECT = reconnect;
        if (!reconnect) {
            mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
        }

        Logger.myLog(TAG + "disconnect:" + reconnect + "mBleDevice:" + mBleDevice);

        if (mBleDevice != null) {
            //deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
            mBleDevice.disconnect();
            if (!reconnect) {
                mBleDevice = null;
                mBleConnection = null;
            }
        }
    }

    /**
     * 设置目标步数
     *
     * @param step
     */
    public void sendDevcieGoalStep(int step) {
        Logger.myLog(TAG + "sendDevcieGoalStep" + step);
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.sendGoalSteps(step);
        }
    }

    private void register() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mContext.registerReceiver(broadcastReceiver, filter);
        } catch (Exception e) {
           e.printStackTrace();
        }

    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    Logger.myLog(TAG + "BluetoothAdapter.STATE_ON");
                    BLU_STATE = BluetoothAdapter.STATE_ON;
                    if (null == mCurrentDevice) {
                        return;
                    }
                    if (Constants.CAN_RECONNECT && Utils.isCanRe(mCurrentDevice.deviceType)) {
                        if (mReHandler.hasMessages(HandlerContans.mDevcieReconnect)) {
                            mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                        }
                        mReHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieReconnect, 3000);
                    }
                    // mHandler.removeMessages(HandlerContans.mDevcieReconnect);
                   /* if (isCanRe(mCurrentDeviceType) && Constants.CAN_RECONNECT) {
                        if (bluetoothListener != null) {
                            bluetoothListener.connecting();
                        }
                        mReconnectHandler.sendEmptyMessageDelayed(0x01, 5000);
                    }*/
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    //关闭蓝牙时，移除所有重连,断连回调里面会查询蓝牙是否打开，不打开不做重连操作
                    Logger.myLog(TAG + "BluetoothAdapter.STATE_OFF");
                    //取消重连
                    if (mBleDevice != null) {
                        mBleDevice.disconnect();
                    }
                    mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetFailState);
                    mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                    BLU_STATE = BluetoothAdapter.STATE_OFF;
                    // mReconnectHandler.removeCallbacksAndMessages(null);
                    //清楚
                    //gattClose();
                    //同步数据也失败

                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                BluetoothDevice dv = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String key = intent.getStringExtra(BluetoothDevice.EXTRA_PAIRING_KEY);
                if (bondState == BluetoothDevice.BOND_NONE) {//取消、出错、超时配对后不尝试自动重连

                }
            }
        }
    };

    /**
     * 設置闹钟
     */
    public void sendAlarmList(ArrayList<AlarmEntry> list) {

        Logger.myLog("sendAlarmList:");

        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {

            CRPAlarmClockInfo crpAlarmClockInfo;
            AlarmEntry alarmEntry;
            Logger.myLog(TAG + "sendAlarmList: list.size()" + list.size());
            for (int i = 0; i < list.size(); i++) {
                alarmEntry = list.get(i);
                crpAlarmClockInfo = new CRPAlarmClockInfo();
                crpAlarmClockInfo.setEnable(alarmEntry.isOn());
                crpAlarmClockInfo.setId(i);
                crpAlarmClockInfo.setHour(alarmEntry.getStartHour());
                crpAlarmClockInfo.setMinute(alarmEntry.getStartMin());
                crpAlarmClockInfo.setRepeatMode(alarmEntry.getRepeat());
                crpAlarmClockInfo.setDate(new Date());
                mBleConnection.sendAlarmClock(crpAlarmClockInfo);
                Logger.myLog(TAG + "sendAlarmList:" + alarmEntry.toString() + "---------" + crpAlarmClockInfo.toString());
            }
            if (list.size() < 3) {
                for (int i = list.size(); i < 3; i++) {
                    crpAlarmClockInfo = new CRPAlarmClockInfo();
                    crpAlarmClockInfo.setEnable(false);
                    crpAlarmClockInfo.setId(i);
                    crpAlarmClockInfo.setHour(0);
                    crpAlarmClockInfo.setMinute(0);
                    crpAlarmClockInfo.setRepeatMode(0);
                    crpAlarmClockInfo.setDate(new Date());
                    mBleConnection.sendAlarmClock(crpAlarmClockInfo);
                    Logger.myLog(TAG + "sendAlarmListcrpAlarmClockInfo:");
                }
            }

            getAlarList();
        }


    }


    public void syncData() {
        Logger.myLog("syncData---------------");
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null && !SyncCacheUtils.getSyncDataTime(mContext)) {

            syncAllData();
        }
        queryHeartRate();

    }


    public void getSettings() {
      /*  //查询最后三次
        mBleConnection.queryMovementHeartRate();
        //未连接的情况下可以获取锻炼数据的最后一次数据
        mBleConnection.queryLastDynamicRate();

        if (true) {
            return;
        }*/


        syncData();

        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null && !SyncCacheUtils.getSetting(mContext)) {

            //获取设置的时间格式
            mBleConnection.queryTimeSystem(new CRPDeviceTimeSystemCallback() {
                @Override
                public void onTimeSystem(final int i) {

                    //保存时间格式
                    BleSPUtils.putInt(mContext,BleSPUtils.KEY_TIME_FORMAT,i);

                    Long currentTime = isSameOption(onTimeSystem);
                    if (currentTime == onTimeSystem) {
                        return;
                    } else {
                        onTimeSystem = currentTime;
                    }

                    if (mCurrentDevice != null) {
                        Logger.myLog(TAG + "onTimeSystem: i" + i);
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                Bracelet_W311_SettingModelAction.saveOrUpdateDeviceTimeFormate(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), i);
                            }
                        });
                    }
                }
            });


            getAlarList();


            queryWatchFace();
            //获取翻腕亮屏的状态
            mBleConnection.queryQuickView(new CRPDeviceQuickViewCallback() {
                @Override
                public void onQuickView(boolean b) {
                    Logger.myLog(TAG + "queryQuickView: enable" + b);
                    Long currentTime = isSameOption(onQuickView);
                    if (currentTime == onQuickView) {
                        return;
                    } else {
                        onQuickView = currentTime;
                    }
                    if (b) {
                        //获取翻腕亮屏的有效时间段
                        mBleConnection.queryQuickViewTime(crpDevicePeriodTimeCallback);
                        // model.setSwichType();
                    } else {
                        Bracelet_W311_LiftWristToViewInfoModel model = new Bracelet_W311_LiftWristToViewInfoModel();
                        model.setSwichType(2);
                        model.setDeviceId(mCurrentDevice.deviceName);
                        model.setUserId(BaseManager.mUserId);
                        model.setStartHour(Constants.defStarHour);
                        model.setStartMin(Constants.defStartMin);
                        model.setEndHour(Constants.defEndHour);
                        model.setEndMin(Constants.defEndMin);
                        model.setIsNextDay(false);
                        Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);
                    }


                }
            });


            //勿扰模式
            mBleConnection.queryDoNotDistrubTime(crpDevicePeriodTimeCallback);

            //获取闹钟

            //获取久坐提醒开关状态 querySedentaryReminder
            mBleConnection.querySedentaryReminder(new CRPDeviceSedentaryReminderCallback() {
                @Override
                public void onSedentaryReminder(boolean b) {
                    Logger.myLog(TAG + "onSedentaryReminder: enable" + b);
                    Long currentTime = isSameOption(onSedentaryReminder);
                    if (currentTime == onSedentaryReminder) {
                        return;
                    } else {
                        onSedentaryReminder = currentTime;
                    }
                    if (b) {
                        Watch_W516_SedentaryModel watch_w516_sedentaryModels = new Watch_W516_SedentaryModel();
                        watch_w516_sedentaryModels.setDeviceId(mCurrentDevice.getDeviceName());
                        watch_w516_sedentaryModels.setUserId(BaseManager.mUserId);
                        watch_w516_sedentaryModels.setIsEnable(true);
                        watch_w516_sedentaryModels.setLongSitTimeLong(60);
                        watch_w516_sedentaryModels.setLongSitStartTime(Constants.defStartTime);
                        watch_w516_sedentaryModels.setLongSitEndTime(Constants.defEndTime);
                        ParseData.saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModels);

                        //读取久坐提醒
                        mBleConnection.querySedentaryReminderPeriod(new CRPDeviceSedentaryReminderPeriodCallback() {
                            @Override
                            public void onSedentaryReminderPeriod(CRPSedentaryReminderPeriodInfo crpSedentaryReminderPeriodInfo) {
                                Logger.myLog(TAG + "queryRemindersToMovePeriod");
                                Logger.myLog(TAG + "queryRemindersToMovePeriod,getStartHour:" + crpSedentaryReminderPeriodInfo.getStartHour() + ",getEndHour:" + crpSedentaryReminderPeriodInfo.getEndHour() + ",getPeriod:" + crpSedentaryReminderPeriodInfo.getPeriod() + ",getSteps:" + crpSedentaryReminderPeriodInfo.getSteps());

                                //保存久坐提醒时间段
                                BleSPUtils.putString(mContext,BleSPUtils.KEY_LONG_SIT,CommonDateUtil.formatTwoStr(crpSedentaryReminderPeriodInfo.getStartHour()) + ":00"+"-"+CommonDateUtil.formatTwoStr(crpSedentaryReminderPeriodInfo.getEndHour()) + ":00");

                                Watch_W516_SedentaryModel watch_w516_sedentaryModels = new Watch_W516_SedentaryModel();
                                watch_w516_sedentaryModels.setDeviceId(mCurrentDevice.getDeviceName());
                                watch_w516_sedentaryModels.setUserId(BaseManager.mUserId);
                                watch_w516_sedentaryModels.setIsEnable(true);
                                watch_w516_sedentaryModels.setLongSitTimeLong(crpSedentaryReminderPeriodInfo.getPeriod());
                                watch_w516_sedentaryModels.setLongSitStartTime(CommonDateUtil.formatTwoStr(crpSedentaryReminderPeriodInfo.getStartHour()) + ":00");
                                watch_w516_sedentaryModels.setLongSitEndTime(CommonDateUtil.formatTwoStr(crpSedentaryReminderPeriodInfo.getEndHour()) + ":00");
                                ParseData.saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModels);
                            }
                        });
                       /* mBleConnection.queryRemindersToMovePeriod(new CRPDeviceRemindersToMovePeriodCallback() {
                            @Override
                            public void onRemindersToMovePeriod(CRPRemindersToMovePeriodInfo crpRemindersToMovePeriodInfo) {
                                Logger.myLog(TAG + "queryRemindersToMovePeriod");
                                Logger.myLog(TAG + "queryRemindersToMovePeriod,getStartHour:" + crpRemindersToMovePeriodInfo.getStartHour() + ",getEndHour:" + crpRemindersToMovePeriodInfo.getEndHour() + ",getPeriod:" + crpRemindersToMovePeriodInfo.getPeriod() + ",getSteps:" + crpRemindersToMovePeriodInfo.getSteps());
                                Watch_W516_SedentaryModel watch_w516_sedentaryModels = new Watch_W516_SedentaryModel();
                                watch_w516_sedentaryModels.setDeviceId(mCurrentDevice.getDeviceName());
                                watch_w516_sedentaryModels.setUserId(BaseManager.mUserId);
                                watch_w516_sedentaryModels.setIsEnable(true);
                                watch_w516_sedentaryModels.setLongSitTimeLong(crpRemindersToMovePeriodInfo.getPeriod());
                                watch_w516_sedentaryModels.setLongSitStartTime(CommonDateUtil.formatTwoStr(crpRemindersToMovePeriodInfo.getStartHour()) + ":00");
                                watch_w516_sedentaryModels.setLongSitEndTime(CommonDateUtil.formatTwoStr(crpRemindersToMovePeriodInfo.getEndHour()) + ":00");
                                ParseData.saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModels);
                            }
                        });*/

                    } else {
                        Watch_W516_SedentaryModel watch_w516_sedentaryModels = new Watch_W516_SedentaryModel();
                        watch_w516_sedentaryModels.setDeviceId(mCurrentDevice.getDeviceName());
                        watch_w516_sedentaryModels.setUserId(BaseManager.mUserId);
                        watch_w516_sedentaryModels.setIsEnable(false);
                        watch_w516_sedentaryModels.setLongSitTimeLong(60);
                        watch_w516_sedentaryModels.setLongSitStartTime(Constants.defStartTime);
                        watch_w516_sedentaryModels.setLongSitEndTime(Constants.defEndTime);
                        ParseData.saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModels);

                    }
                }
            });
            //获取久坐提醒时间段


            //获取目标步数
            mBleConnection.queryGoalStep(new CRPDeviceGoalStepCallback() {
                @Override
                public void onGoalStep(int step) {
                    //保存计步目标
                    BleSPUtils.putInt(mContext,BleSPUtils.KEY_STEP_GOAL, step);
                    Long currentTime = isSameOption(onGoalStep);
                    if (currentTime == onGoalStep) {
                        return;
                    } else {
                        onGoalStep = currentTime;
                    }

                    //把目标步数传出去进行保存
                    Logger.myLog(TAG + "onGoalStep: step" + step);
                    Message message = new Message();
                    Object[] objects = new Object[1];
                    objects[0] = step;
                    message.what = HandlerContans.mDevcieGoalStep;
                    message.obj = objects;
                    mHandler.sendMessageDelayed(message, 500);
                }
            });

            //  mBleConnection.queryTimingMeasureHeartRate(CRPHeartRateType.TIMING_MEASURE_HEART_RATE);

            //查询是否开启定时检测
            if (mCurrentDevice.deviceType == IDeviceType.TYPE_BRAND_W812) {

                mBleConnection.queryTimingMeasureHeartRate(new CRPDeviceTimingMeasureHeartRateCallback() {
                    @Override
                    public void onTimingMeasure(int i) {
                        //24小时心率
                        BleSPUtils.putBoolean(mContext,BleSPUtils.KEY_HEART_STATUS,i==1);

                        Long currentTime = isSameOption(onTimingMeasure);
                        if (currentTime == onTimingMeasure) {
                            return;
                        } else {
                            onTimingMeasure = currentTime;
                        }

                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                Bracelet_W311_24H_hr_SettingModel model = new Bracelet_W311_24H_hr_SettingModel();
                                model.setHeartRateSwitch(i==1);
                                model.setDeviceId(mCurrentDevice.deviceName);
                                model.setUserId(BaseManager.mUserId);
                                Bracelet_W311_SettingModelAction.saveOrUpateBracelet24HHrSetting(model);
                            }
                        });
                        //
                        Logger.myLog(TAG + "queryTimingMeasureHeartRate：state" + i);
                    }

                });

//                mBleConnection.queryTimingMeasureHeartRate(new CRPDeviceTimingMeasureHeartRateCallback() {
//                    @Override
//                    public void onTimingMeasure(final boolean b) {
//
//                        //24小时心率
//                        BleSPUtils.putBoolean(mContext,BleSPUtils.KEY_HEART_STATUS,b);
//
//                        Long currentTime = isSameOption(onTimingMeasure);
//                        if (currentTime == onTimingMeasure) {
//                            return;
//                        } else {
//                            onTimingMeasure = currentTime;
//                        }
//
//                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
//                            @Override
//                            public void run() {
//                                Bracelet_W311_24H_hr_SettingModel model = new Bracelet_W311_24H_hr_SettingModel();
//                                model.setHeartRateSwitch(b);
//                                model.setDeviceId(mCurrentDevice.deviceName);
//                                model.setUserId(BaseManager.mUserId);
//                                Bracelet_W311_SettingModelAction.saveOrUpateBracelet24HHrSetting(model);
//                            }
//                        });
//                        //
//                        Logger.myLog(TAG + "queryTimingMeasureHeartRate：state" + b);
//                    }
//                });
            }
            mBleConnection.queryOtherMessageState(new CRPDeviceOtherMessageCallback() {
                @Override
                public void onOtherMessage(final boolean b) {
                    Long currentTime = isSameOption(onOtherMessage);
                    if (currentTime == onOtherMessage) {
                        return;
                    } else {
                        onOtherMessage = currentTime;
                    }

                    SyncCacheUtils.saveSetting(mContext);
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            Bracelet_W311_ThridMessageModel bracelet_w311_thridMessageModel = Bracelet_W311_SettingModelAction.findBracelet_W311_ThridMessage(mCurrentDevice.getDeviceName(), BaseManager.mUserId);
                            if (bracelet_w311_thridMessageModel != null) {
                                bracelet_w311_thridMessageModel.setIsOthers(b);
                                Bracelet_W311_SettingModelAction.saveOrUpdateBraceletThridMessage(bracelet_w311_thridMessageModel);
                            } else {
                                bracelet_w311_thridMessageModel = new Bracelet_W311_ThridMessageModel();
                                bracelet_w311_thridMessageModel.setUserId(BaseManager.mUserId);
                                bracelet_w311_thridMessageModel.setDeviceId(mCurrentDevice.getDeviceName());
                                bracelet_w311_thridMessageModel.setIsOthers(b);
                                bracelet_w311_thridMessageModel.setIskakaotalk(false);
                                bracelet_w311_thridMessageModel.setIsLinkedin(false);
                                bracelet_w311_thridMessageModel.setIsMessage(false);
                                bracelet_w311_thridMessageModel.setIsWhatApp(false);
                                bracelet_w311_thridMessageModel.setIsTwitter(false);
                                bracelet_w311_thridMessageModel.setIsSkype(false);
                                bracelet_w311_thridMessageModel.setIsFaceBook(false);
                                bracelet_w311_thridMessageModel.setIsWechat(false);
                                bracelet_w311_thridMessageModel.setIsQQ(false);
                                bracelet_w311_thridMessageModel.setIsInstagram(false);
                                bracelet_w311_thridMessageModel.setIsLine(false);
                                Bracelet_W311_SettingModelAction.saveOrUpdateBraceletThridMessage(bracelet_w311_thridMessageModel);
                            }
                        }
                    });


                }
            });

            if (Constants.wristbandWeather != null) {
                sendWeatherToday(Constants.wristbandWeather.getCondition(), Constants.cityName);
                sendFutureWeather(Constants.wristbandWeather.getForecast15Days());
            }

        }


    }

    public void queryWatchFace() {
        //查询当前显示的表盘
        mBleConnection.queryDisplayWatchFace(new CRPDeviceDisplayWatchFaceCallback() {
            @Override
            public void onDisplayWatchFace(int faceMode) {

                Long currentTime = isSameOption(onDisplayWatchFace);
                if (currentTime == onDisplayWatchFace) {
                    return;
                } else {
                    onDisplayWatchFace = currentTime;
                }

                if (mCurrentDevice != null) {
                    Logger.myLog(TAG + "queryDisplayWatchFace: faceMode" + faceMode);
                    Bracelet_W311_SettingModelAction.saveOrUpdateWatchFaces(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), faceMode);
                    Message message = new Message();
                    Object[] objects = new Object[1];
                    objects[0] = faceMode;
                    message.what = HandlerContans.mWatchWatchFace;
                    message.obj = objects;
                    mHandler.sendMessageDelayed(message, 50);

                }
            }
        });
    }

    public static int callCount = 0;

    /**
     * 初始化方法
     *
     * @param context
     */
    @Override
    public void init(Context context) {
        // TODO: 2018/9/30 bind Service
        super.init(context);


        initHandler();
        initReConHandler();
        initTimeout();
        if (callCount == 0) {
            BLU_STATE = BluetoothAdapter.STATE_ON;
            mBleClient = CRPBleClient.create(context);
        }
        register();

        Logger.myLog(TAG + "init callCount:" + callCount++);
    }

    public void connect(W812Device baseDevice) {
        mCurrentDevice = baseDevice;
        Logger.myLog("connect W812Device");
        connect(baseDevice.address);
    }


    public void connect(W813Device baseDevice) {
        mCurrentDevice = baseDevice;
        Logger.myLog("W813Device W812Device");
        connect(baseDevice.address);
    }


    public void connect(W814Device baseDevice) {
        mCurrentDevice = baseDevice;
        // mBleDevice = mBleClient.getBleDevice(baseDevice.getAddress());
        Logger.myLog("W814Device W812Device");
        connect(baseDevice.address);
    }

    public void connect(W817Device baseDevice) {
        mCurrentDevice = baseDevice;
        Logger.myLog("connect W812Device");
        connect(baseDevice.address);


    }

    public void connect(W819Device baseDevice) {
        mCurrentDevice = baseDevice;
        Logger.myLog("W819Device connect");
        connect(baseDevice.address);
    }

    public void connect(W910Device baseDevice) {
        mCurrentDevice = baseDevice;
        Logger.myLog("W819Device connect");
        connect(baseDevice.address);
    }


    /**
     * 第一次连接会调用两次，所有用了一个所有弄了一个2秒钟重复操作不进行操作的限制
     *
     * @param macAddr
     */
    protected synchronized void connect(String macAddr) {
        Logger.myLog(TAG + "macAddr:" + macAddr + "mBleDevice:" + mBleDevice + "deviceConnState:" + deviceConnState + "connectTime:" + connectTime + ",SystemClock.currentThreadTimeMillis() - connectTime:" + (System.currentTimeMillis() - connectTime));
        if (System.currentTimeMillis() - connectTime < 4000) {
            connectTime = System.currentTimeMillis();
            return;
        }
        connectTime = System.currentTimeMillis();
        if (deviceConnState == CRPBleConnectionStateListener.STATE_CONNECTING) {
            return;
        }


        Logger.myLog(TAG + "connect");

        Constants.CAN_RECONNECT = true;
        if (mBleDevice == null) {
            mBleDevice = mBleClient.getBleDevice(macAddr);
        }
        if (mBleDevice != null) {
            // connect();
            reconnect();
        }
    }


    protected void reconnect() {
        if (mHandler == null) {
            initHandler();
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBleDevice != null) {
                   /* if (mCurrentDevice != null) {
                        mBleDevice = mBleClient.getBleDevice(mCurrentDevice.getAddress());
                    }*/

                    // Logger.myLog(TAG + "connect W812Device2 BraceletW811W814Manager:mBleDevice.isConnected()," + mBleDevice.isConnected() + "-------------,mBleConnection:" + mBleConnection + "------------deviceConnState:" + deviceConnState + "mCurrentDevice.getAddress():" + mCurrentDevice == null ? "" : mCurrentDevice.getAddress());

                    if (deviceConnState == CRPBleConnectionStateListener.STATE_CONNECTING) {

                        Logger.myLog(TAG + "connect W812Device2 BraceletW811W814Manager:mBleDevice.isConnected()," + mBleDevice.isConnected() + "-------------,mBleConnection:" + mBleConnection + "------------deviceConnState:" + deviceConnState + "------------ no connect");

                        return;
                    }
                    if (BLU_STATE == BluetoothAdapter.STATE_OFF) {
                        return;
                    }

                    if (mBleDevice.isConnected() && mBleConnection != null) {
                        // mBleConnection = mBleDevice.connect();
                        // disconnect(true);
                        //connect();
                        isConnect = true;
                        deviceConnState = CRPBleConnectionStateListener.STATE_CONNECTED;
                        mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                        mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetSuccessState);

                    } else {
                        if (deviceConnState == CRPBleConnectionStateListener.STATE_CONNECTING) {
                            Logger.myLog("connect reconnect");
                            return;
                        }
                        connect();
                    }
                }
            }
        });


    }

    protected synchronized void connect() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentDevice != null && !Utils.isCanRe(mCurrentDevice.deviceType)) {
                    return;
                }
                //如果是在连接中就不进行连接
                mBleConnection = mBleDevice.connect();//会有两个连接了。
                setLister();

            }
        });


    }


    public void setLister() {
        if (mBleConnection != null) {
            mBleConnection.setConnectionStateListener(connectionStateListener);
            mBleConnection.setStepChangeListener(mStepChangeListener);
            mBleConnection.setSleepChangeListener(mSleepChangeListener);
            mBleConnection.setHeartRateChangeListener(mHeartRateChangListener);
            mBleConnection.setBloodPressureChangeListener(mBloodPressureChangeListener);
            mBleConnection.setBloodOxygenChangeListener(mBloodOxygenChangeListener);
            mBleConnection.setFindPhoneListener(mFindPhoneListener);
            //mBleConnection.setECGChangeListener(mECGChangeListener);
            mBleConnection.setStepsCategoryListener(mStepsCategoryChangeListener);
            mBleConnection.setCameraOperationListener(crpCameraOperationListener);
            mBleConnection.setPhoneOperationListener(crpPhoneOperationListener);
            mBleConnection.setDeviceBatteryListener(crpDeviceBatteryCallback);
            mBleConnection.setWeatherChangeListener(crpWeatherChangeListener);
        }
    }

    /**
     * 超时handler
     */
    public void initTimeout() {
        if (timeOutHandler == null) {
            timeOutHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    final boolean isListenerNull = mBleReciveListeners == null;
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case HandlerContans.mDevcieConnectTimeOut:
                            Logger.myLog(TAG + "HandlerContans.mDevcieConnectTimeOut BLU_STATE:" + BLU_STATE);
                            deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                            if (Constants.CAN_RECONNECT && BLU_STATE == BluetoothAdapter.STATE_ON) {
                                if (mReHandler.hasMessages(HandlerContans.mDevcieReconnect)) {
                                    mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                                }
                                mReHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieReconnect, 1000);

                            }
                            if (BLU_STATE == BluetoothAdapter.STATE_OFF) {
                                deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                            }
                            //mBleDevice.disconnect();
                            // disconnect(true);
                            break;
                        case HandlerContans.mSyncHandlerStepTimeOut:
                            Logger.myLog(TAG + "mSyncHandlerHrTimeOut");
                            SyncProgressObservable.getInstance().hide();
                            //数据无无响应
                            if (!isListenerNull) {
                                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                    mBleReciveListeners.get(i).receiveData(new BraceletW311SyncComplete(BraceletW311SyncComplete.TIMEOUT));
                                }
                            }

                            break;

                    }

                }
            };
        }
    }

    private void initReConHandler() {
        mReHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HandlerContans.mDevcieReconnect:

                        Logger.myLog(TAG + " mDevcieReconnect STATE_ON");

                        if (Constants.CAN_RECONNECT && mCurrentDevice != null && deviceConnState != CRPBleConnectionStateListener.STATE_CONNECTED && BLU_STATE == BluetoothAdapter.STATE_ON) {
                            Logger.myLog(TAG + " mDevcieReconnect STATE_ON reconnect");
                            reconnect();
                        }
                        break;
                }
            }
        };
    }

    private void initHandler() {
        final boolean isListenerNull = mBleReciveListeners == null;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case HandlerContans.mRealDataHr:


                        Object[] objhr = (Object[]) msg.obj;
                        if (!isListenerNull) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchHrHeartResult((Integer) objhr[0], mCurrentDevice.deviceName));
                            }
                        }

                        break;
                    case HandlerContans.mHandlerConnetting:
                        if (!isListenerNull && mCurrentDevice != null)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).onConnecting(mCurrentDevice);
                            }
                        break;
                    case HandlerContans.mHandlerConnetFailState:
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).onConnResult(false, true, mCurrentDevice);
                            }
                        }
                        break;

                    case HandlerContans.mHandlerDeviceConnetSuccessState:
                        //如果设备没有在DFU模式下，就进行发指令

                        break;
                    case HandlerContans.mHandlerConnetSuccessState:
                        // TODO: 2019/1/7 device ID
//                        WatchDeviceInfo.putString(mContext, WatchDeviceInfo.WATCH_DEVICEID, mCurrentDevice
//                                .getAddress());
                        Logger.myLog(TAG + "W81 startAni1 mBleReciveListeners: mHandlerConnetSuccessState" + mBleReciveListeners.size() + "true" + mCurrentDevice);
                        if (mCurrentDevice != null && mBleConnection != null) {
                            mBleConnection.queryFrimwareVersion(crpDeviceFirmwareVersionCallback);
                            mBleConnection.queryDeviceBattery();
                            if (mDeviceInformationTable == null) {
                                mDeviceInformationTable = new DeviceInformationTable();
                            }
                            mDeviceInformationTable.setMac(mCurrentDevice.getAddress());
                            mDeviceInformationTable.setDeviceId(mCurrentDevice.getDeviceName());
                            ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, -1);
                        }

//                        ParseData.saveDeviceType(IDeviceType.TYPE_WATCH);
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).onConnResult(true, true, mCurrentDevice);
                            }
                        }
                        //如果是DFU模式就不进行下面的指令发送
                        if (Constants.isDFU) {
                            return;
                        }
                        //1.设置时间
                        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
                            mBleConnection.syncTime();
                        }

                        //bleConnection.sendDeviceVersion(CRPDeviceVersionType);
                        String currentla = AppLanguageUtil.getCurrentLanguage();
                        Logger.myLog("currentLanguage" + currentla);

                        //mBleConnection.queryDeviceVersion().(CRPDeviceVersionCallback);
                        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
                            mBleConnection.queryDeviceVersion(crpDeviceVersionCallback);
                        }


                        break;
                    case HandlerContans.mHandlerbattery://电量
                        if (!isListenerNull) {
                            Logger.myLog(TAG + "onDeviceSuccess  HandlerContans.mHandlerbattery" + mCurrentDevice);
                            if (mCurrentDevice == null) {
                                return;
                            }
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                Logger.myLog(TAG + "onDeviceSuccess  HandlerContans.mHandlerbattery2" + mCurrentDevice);
                                mBleReciveListeners.get(i).onBattreyOrVersion(mCurrentDevice);
                            }
                        }
                        break;

                    case HandlerContans.mHandlerRealData:
                        Object[] obj = (Object[]) msg.obj;
                        if (!isListenerNull) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new BraceletW311RealTimeResult((Integer) obj[0], (float) obj[1], (Integer) obj[2], (String) obj[3]));
                            }
                        }
                        break;
                    case 0x14:
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchHrHeartResult((Integer) objHr[0], mCurrentDevice.deviceName));
                            }
                        }
                        break;

                    case HandlerContans.mDevcieGoalStep:    //计步目标
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            //保存计步目标
                            BleSPUtils.putInt(mContext,BleSPUtils.KEY_STEP_GOAL, (Integer) objHr[0]);
                            Logger.myLog(TAG,"-----目标目标原始="+ objHr[0]);
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchGOALSTEPResult((Integer) objHr[0], mCurrentDevice.deviceName));
                            }
                        }
                        break;
                    case HandlerContans.mTakePhoto:
                        TakePhotObservable.getInstance().takePhoto(true);
                        break;
                    case HandlerContans.mDevcieAlarList:
                        //HandlerContans.mDevcieAlarList

                        //Logger.myLog("HandlerContans.mDevcieAlarList");
                        if (!isListenerNull) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceAlarmListResult(mCurrentDevice.deviceName));
                                //Logger.myLog("HandlerContans.mDevcieAlarList2");
                            }
                        }
                        break;
                    case HandlerContans.mSyncHandlerStartSync:
                        if (mBleReciveListeners != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new BraceletW311SyncStart());
                            }
                        }
                        break;
                    case HandlerContans.mSyncHandlerHrComptely:
                        SyncCacheUtils.saveSyncDataTime(mContext);
                        SyncProgressObservable.getInstance().hide();
                        Logger.myLog(TAG + "mSyncHandlerHrComptely");
                        if (!isListenerNull) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new BraceletW311SyncComplete(BraceletW311SyncComplete.SUCCESS));
                            }
                        }
                        break;

                    case HandlerContans.mDevcieBloodPressureMessureSuccess:
                        Logger.myLog(TAG + "mDevcieBloodPressureMessureOxyenSuccess");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_bloodpre, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;
                    case HandlerContans.mDevcieMeasureOxyenSuccess:
                        Logger.myLog(TAG + "mDevcieOxygenMeasureOxyenSuccess");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_oxygen, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;
                    case HandlerContans.mDevcieMeasureHrSuccess:
                        Logger.myLog(TAG + "mDevcieOxygenMeasureonceHr");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_once_hr, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;
                    case HandlerContans.mDevcieExecise:
                        Logger.myLog(TAG + "mDevcieExecise");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_exercise, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;

                    case HandlerContans.mDevcieConnectHR:
                        Logger.myLog(TAG + "mDevcieConnectHR");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_hr, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;
                    case HandlerContans.mHandlerDeviceUpdateWeather:
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.update_weather, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;
                    case HandlerContans.mWatchWatchFace:
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchFACEResult((Integer) objHr[0], mCurrentDevice.deviceName));
                            }
                        }
                        break;
                    case HandlerContans.mDevcieTODAYHR:
                        Logger.myLog(TAG + "mDevcieConnectHR");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.today_hr, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;
                    case HandlerContans.mGetVersion:
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchVersionResult((String) objHr[0], mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;

                }
            }
        };
    }


    CRPBleConnectionStateListener connectionStateListener = new CRPBleConnectionStateListener() {

        @Override
        public void onConnectionStateChange(int newState) {
            Log.d(TAG, "onConnectionStateChange: newState" + newState);

            Long currentTime;
            int state = -1;
            switch (newState) {
                case CRPBleConnectionStateListener.STATE_CONNECTED:
                    currentTime = isSameOption(connectStateSTATE_CONNECTED);
                    if (currentTime == connectStateSTATE_CONNECTED) {
                        return;
                    } else {
                        connectStateSTATE_CONNECTED = currentTime;
                    }
                    isMusicStart = false;
                   /* state = R.string.state_connected;
                    mProgressDialog.dismiss();
                    updateTextView(btnBleDisconnect, getString(R.string.disconnect));
                    testSet();*/
                    Log.d(TAG, "connectionStateListener: STATE_CONNECTED");
                    isConnect = true;
                    timeOutHandler.removeMessages(HandlerContans.mDevcieConnectTimeOut);
                    deviceConnState = CRPBleConnectionStateListener.STATE_CONNECTED;
                    mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                    mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetSuccessState);
                    break;
                case CRPBleConnectionStateListener.STATE_CONNECTING:
                    currentTime = isSameOption(connectStateSTATE_CONNECTING);
                    if (currentTime == connectStateSTATE_CONNECTING) {
                        return;
                    } else {
                        connectStateSTATE_CONNECTING = currentTime;
                    }
                    /* state = R.string.state_connecting;*/
                    Log.d(TAG, "connectionStateListener: STATE_CONNECTING deviceConnState" + deviceConnState + "CRPBleConnectionStateListener.STATE_CONNECTING:" + CRPBleConnectionStateListener.STATE_CONNECTING + ",BLU_STATE:" + BLU_STATE);
                    /**
                     * 有时候在连接中一直不给回调，如果在10秒之后还没有给回调就判断为连接失败
                     */
                    if (deviceConnState != CRPBleConnectionStateListener.STATE_CONNECTING && Constants.CAN_RECONNECT && BLU_STATE == BluetoothAdapter.STATE_ON) {
                        timeOutHandler.removeMessages(HandlerContans.mDevcieConnectTimeOut);
                        timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieConnectTimeOut, 30000);
                    }
                    if (BluetoothAdapter.STATE_OFF == BLU_STATE) {
                        deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                    } else {
                        deviceConnState = CRPBleConnectionStateListener.STATE_CONNECTING;
                    }
                    mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetting);
                    //mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetFailState);
                    break;
                case CRPBleConnectionStateListener.STATE_DISCONNECTED:
                    currentTime = isSameOption(connectStateSTATE_DISCONNECTED);
                    if (currentTime == connectStateSTATE_DISCONNECTED) {
                        return;
                    } else {
                        connectStateSTATE_DISCONNECTED = currentTime;
                    }
                    /*state = R.string.state_disconnected;
                    mProgressDialog.dismiss();
                    updateTextView(btnBleDisconnect, getString(R.string.connect));*/
                    Log.d(TAG, "connectionStateListener: STATE_DISCONNECTED" + Constants.CAN_RECONNECT + "Thread.currentThread().getName:" + Thread.currentThread().getName());
                    timeOutHandler.removeMessages(HandlerContans.mDevcieConnectTimeOut);
                    deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                    if (Constants.CAN_RECONNECT && BLU_STATE == BluetoothAdapter.STATE_ON) {
                        if (mReHandler.hasMessages(HandlerContans.mDevcieReconnect)) {
                            mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                        }
                        mReHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieReconnect, 5000);

                        mHandler.removeMessages(0);
                        if (mHandler.hasMessages(HandlerContans.mSyncHandlerStepTimeOut)) {
                            mHandler.removeMessages(HandlerContans.mSyncHandlerStepTimeOut);
                            // syncHandler.sendEmptyMessageDelayed(HandlerContans.mSyncHandlerHrTimeOut, 0);\
                            timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mSyncHandlerStepTimeOut, 0);
                        }

                    } else {
                        /*if (mBleDevice != null) {
                            //deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                            mBleDevice.disconnect();
                        }*/
                        if (mReHandler.hasMessages(HandlerContans.mDevcieReconnect)) {
                            mReHandler.removeMessages(HandlerContans.mDevcieReconnect);
                        }
                        mHandler.removeMessages(0);
                    }

                    mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetFailState);
                    break;
            }
        }
    };

    //更新天气请求

    CRPWeatherChangeListener crpWeatherChangeListener = new CRPWeatherChangeListener() {
        @Override
        public void onUpdateWeather() {

            Logger.myLog(TAG + "crpWeatherChangeListener");

            Long currentTime = isSameOption(updateWheather);
            if (currentTime == updateWheather) {
                return;
            } else {
                updateWheather = currentTime;
            }


            mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerDeviceUpdateWeather, times);
        }

        @Override
        public void onTempUnitChange(int i) {

        }
    };

    //手机相关操作
    CRPPhoneOperationListener crpPhoneOperationListener = new CRPPhoneOperationListener() {
        @Override
        public void onOperationChange(int i) {
            Logger.myLog(TAG + "crpPhoneOperationListener" + i + "---REJECT_INCOMING:3");
            if (CRPPhoneOperationType.REJECT_INCOMING == i) {
                //挂断电话
                Long currentTime = isSameOption(onOperationChange);
                if (currentTime == onOperationChange) {
                    return;
                } else {
                    onOperationChange = currentTime;
                }
                CmdUtil.sendEndCall(mContext, new KeyEvent(0, KeyEvent.KEYCODE_HEADSETHOOK));
            } else if (CRPPhoneOperationType.MUSIC_NEXT == i) {
                Logger.myLog(TAG + "crpPhoneOperationListener" + i + "---MUSIC_NEXT");
                isMusicStart = true;
                CmdUtil.sendMusicKey(mContext, new KeyEvent(0, KeyEvent.KEYCODE_MEDIA_NEXT));
                CmdUtil.sendMusicKey(mContext, new KeyEvent(1, KeyEvent.KEYCODE_MEDIA_NEXT));

            } else if (CRPPhoneOperationType.MUSIC_PLAY_OR_PAUSE == i) {
                Logger.myLog(TAG + "crpPhoneOperationListener" + i + "---MUSIC_PLAY_OR_PAUSE" + "isMusicStart:" + isMusicStart);
                //第一次就是开始，第二次才是结束
                if (isMusicStart) {
                    isMusicStart = false;
                    // CmdUtil.sendMusicKey(mContext, new KeyEvent(0, KeyEvent.KEYCODE_MEDIA_PAUSE));
                    // CmdUtil.sendMusicKey(mContext, new KeyEvent(1, KeyEvent.KEYCODE_MEDIA_PAUSE));
                    CmdUtil.sendMusicKey(mContext, new KeyEvent(0, KeyEvent.KEYCODE_MEDIA_STOP));
                    CmdUtil.sendMusicKey(mContext, new KeyEvent(1, KeyEvent.KEYCODE_MEDIA_STOP));
                } else {
                    isMusicStart = true;
                    CmdUtil.sendMusicKey(mContext, new KeyEvent(0, KeyEvent.KEYCODE_MEDIA_PLAY));
                    CmdUtil.sendMusicKey(mContext, new KeyEvent(1, KeyEvent.KEYCODE_MEDIA_PLAY));
                }


            } else if (CRPPhoneOperationType.MUSIC_PREVIOUS == i) {
                isMusicStart = true;
                Logger.myLog(TAG + "crpPhoneOperationListener" + i + "---MUSIC_PREVIOUS");
                CmdUtil.sendMusicKey(mContext, new KeyEvent(0, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
                CmdUtil.sendMusicKey(mContext, new KeyEvent(1, KeyEvent.KEYCODE_MEDIA_PREVIOUS));

            }
        }
    };

    //查询固件版本号

    CRPDeviceFirmwareVersionCallback crpDeviceFirmwareVersionCallback = new CRPDeviceFirmwareVersionCallback() {
        @Override
        public void onDeviceFirmwareVersion(String s) {


            try {
                String version[] = s.split("-");
                String currentV = version[version.length - 1].replace(".", "");
                versionCode = Integer.parseInt(currentV);
                Logger.myLog(TAG + "FirmwareVersion=" + s + ",versionCode" + "version[version.length - 1]=" + version[version.length - 1] + ",currentV=" + currentV + "versionCode=" + versionCode);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDeviceInformationTable.setVersion(s);
                ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, 1);

                if (mCurrentDevice != null) {
                    Message message = new Message();
                    Object[] objects = new Object[1];
                    objects[0] = s;
                    message.what = HandlerContans.mGetVersion;
                    message.obj = objects;
                    mHandler.sendMessageDelayed(message, 50);
                }
            }


            //1版本号，2是电量


        }
    };

    //查询版本号的回调
    CRPDeviceVersionCallback crpDeviceVersionCallback = new CRPDeviceVersionCallback() {
        @Override
        public void onDeviceVersion(int i) {
            Logger.myLog(TAG + "onDeviceVersion version:" + i);
            if (AppLanguageUtil.sendTypeLanguage() == CRPDeviceLanguageType.LANGUAGE_CHINESE) {
                if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
                    mBleConnection.sendDeviceVersion(CRPDeviceVersionType.CHINESE_EDITION);
                }
            } else {
                if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
                    mBleConnection.sendDeviceVersion(CRPDeviceVersionType.INTERNATIONAL_EDITION);
                }
            }

            if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
                mBleConnection.queryDeviceLanguage(new CRPDeviceLanguageCallback() {
                    @Override
                    public void onDeviceLanguage(int i, int[] ints) {


                        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null && AppLanguageUtil.sendTypeLanguage() != i) {
                            mBleConnection.sendDeviceLanguage((byte) AppLanguageUtil.sendTypeLanguage());
                        }
                        sendUserInfoW81();
                        getSettings();
                        Logger.myLog(TAG + "onDeviceLanguage" + i);

                    }
                });
            }

        }
    };

    //查询电量的回调
    CRPDeviceBatteryListener crpDeviceBatteryCallback = new CRPDeviceBatteryListener() {
        @Override
        public void onSubscribe(boolean b) {

        }

        @Override
        public void onDeviceBattery(int i) {
            //0版本号，1是电量
            Long currentTime = isSameOption(onDeviceBattery);
            if (currentTime == onDeviceBattery) {
                return;
            } else {
                onDeviceBattery = currentTime;
            }

            Logger.myLog(TAG + "battery:" + i + "mDeviceInformationTable:" + mDeviceInformationTable);
            mDeviceInformationTable.setBattery(i);
            ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, 0);
            mHandler.sendEmptyMessageDelayed(HandlerContans.mHandlerbattery, times);

            //如果发送一个通知到
        }
    };
    CRPCameraOperationListener crpCameraOperationListener = new CRPCameraOperationListener() {

        @Override
        public void onTakePhoto() {
            Long currentTime = isSameOption(onTakePhoto);
            if (currentTime == onTakePhoto) {
                return;
            } else {
                onTakePhoto = currentTime;
            }
            Logger.myLog(TAG + "crpCameraOperationListener onTakePhoto: ");
            mHandler.sendEmptyMessage(HandlerContans.mTakePhoto);

        }
    };

    CRPStepsCategoryChangeListener mStepsCategoryChangeListener = new CRPStepsCategoryChangeListener() {
        @Override
        public void onStepsCategoryChange(CRPStepsCategoryInfo info) {
            List<Integer> stepsList = info.getStepsList();
            Log.d(TAG, "onStepsCategoryChange size: " + stepsList.size());
            for (int i = 0; i < stepsList.size(); i++) {
                Log.d(TAG, "onStepsCategoryChange: " + stepsList.get(i).intValue());
            }
        }
    };


    int currentStep = -1;

    public static long times = 200;
    CRPStepChangeListener mStepChangeListener = new CRPStepChangeListener() {
        @Override
        public void onStepChange(final CRPStepInfo info) {
            Log.d(TAG, "step: " + info.getSteps() + ",currentStep:" + currentStep + "info.getDistance()" + info.getDistance() + "info.getCalories()" + info.getCalories());

            if (currentStep != info.getSteps()) {
                //距离的单位是米
                currentStep = info.getSteps();
                Message message = new Message();
                Object[] objects = new Object[4];
                objects[0] = info.getSteps();
                //10米向下取整
                //objects[1] = (float) (info.getDistance() / 1000.0f);
                objects[1] = CommonDateUtil.formatFloor(info.getDistance(), true);
                // objects[1] = (float) (Math.floor(info.getDistance() / 10) * 10f) / 1000;
                objects[2] = info.getCalories();
                objects[3] = mCurrentDevice.deviceName;
                message.what = HandlerContans.mHandlerRealData;
                message.obj = objects;
                mHandler.sendMessageDelayed(message, times);

                ThreadPoolUtils.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        long currentTime = System.currentTimeMillis();
                        W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                        w81DeviceDataAction.saveW81DeviceStepData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, info.getSteps(), info.getDistance(), info.getCalories(), false);
                    }
                });
            }

            //updateStepInfo(info.getSteps(), info.getDistance(), info.getCalories());
        }

        @Override
        public void onPastStepChange(int timeType, final CRPStepInfo info) {
            //昨天的步数

            if (CRPPastTimeType.YESTERDAY_STEPS == timeType) {
                Long currentTime = isSameOption(syncYestodayStep);
                if (currentTime == syncYestodayStep) {
                    return;
                } else {
                    syncYestodayStep = currentTime;
                }

                ThreadPoolUtils.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        long currentTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
                        W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                        w81DeviceDataAction.saveW81DeviceStepData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, info.getSteps(), info.getDistance(), info.getCalories(), false);
                    }
                });
                Log.d(TAG, "onPastStepChange: YESTERDAY_STEPS timeType" + timeType + ",steps:" + info.getSteps());

            }
            //前天的步数
            else if (CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS == timeType) {

                Long currentTime = isSameOption(syncBefordayStep);
                if (currentTime == syncBefordayStep) {
                    return;
                } else {
                    syncBefordayStep = currentTime;
                }


                //同步成功移除同步的超时监听
                timeOutHandler.removeMessages(HandlerContans.mSyncHandlerStepTimeOut);
                mHandler.sendEmptyMessageDelayed(HandlerContans.mSyncHandlerHrComptely, 500);

                ThreadPoolUtils.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        long currentTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000 * 2;
                        W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                        w81DeviceDataAction.saveW81DeviceStepData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, info.getSteps(), info.getDistance(), info.getCalories(), false);
                    }
                });
                Log.d(TAG, "onPastStepChange: DAY_BEFORE_YESTERDAY_STEPS timeType" + timeType + ",steps:" + info.getSteps());
            }


        }
    };

    CRPSleepChangeListener mSleepChangeListener = new CRPSleepChangeListener() {
        @Override
        public void onSleepChange(CRPSleepInfo info) {
            Long currentTime = isSameOption(syncTodayStep);
            if (currentTime == syncTodayStep) {
                return;
            } else {
                syncTodayStep = currentTime;
            }
            updateSleepData(System.currentTimeMillis(), info);
            // tvSleepResult.setText(builder.toString());
        }

        @Override
        public void onPastSleepChange(int timeType, final CRPSleepInfo info) {
            if (timeType == CRPPastTimeType.YESTERDAY_SLEEP) {
                Long currentTime = isSameOption(syncYestodaySleep);
                if (currentTime == syncYestodaySleep) {
                    return;
                } else {
                    syncYestodaySleep = currentTime;
                }
                long currentSleepTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
                updateSleepData(currentSleepTime, info);


                //  Log.d(TAG, builder.toString());

            } else if (timeType == CRPPastTimeType.DAY_BEFORE_YESTERDAY_SLEEP) {
                Long currentTime = isSameOption(syncBefordaySleep);
                if (currentTime == syncBefordaySleep) {
                    return;
                } else {
                    syncBefordaySleep = currentTime;
                }
                long currentSleepTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000 * 2;
                updateSleepData(currentSleepTime, info);
            }
        }
    };


    private void updateSleepData(final long currentTime, final CRPSleepInfo info) {
        List<CRPSleepInfo.DetailBean> details = info.getDetails();
        if (details == null) {
            return;
        }

        final ArrayList<ArrayList<String>> sleepDetail = new ArrayList<>();
        int len = details.size();
        CRPSleepInfo.DetailBean detailBean;
        for (int i = 0; i < len; i++) {
            ArrayList<String> itemSleeep = new ArrayList<>();
            detailBean = details.get(i);
            itemSleeep.add(detailBean.getType() + "");
            itemSleeep.add(detailBean.getStartTime() + "");
            itemSleeep.add(detailBean.getEndTime() + "");
            itemSleeep.add(detailBean.getTotalTime() + "");
            sleepDetail.add(itemSleeep);
        }

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                w81DeviceDataAction.saveW81DeviceSleepData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, info.getTotalTime(), info.getRestfulTime(), info.getLightTime(), info.getAwakeTime(), gson.toJson(sleepDetail));
            }
        });
    }

    CRPHeartRateChangeListener mHeartRateChangListener = new CRPHeartRateChangeListener() {
        @Override
        public void onMeasuring(int rate) {
            Log.d(TAG, "onMeasuring: " + rate);

            Message message = new Message();
            Object[] objects = new Object[4];
            objects[0] = rate;
            // TODO: 2019/3/1 实时数据存储
//            Watch_SmartBand_SportDataModel watch_smartBand_sportDataModelRealTime = new
//                    Watch_SmartBand_SportDataModel();
//            watch_smartBand_sportDataModelRealTime.setTotalSteps((int) objects[0]);
//            watch_smartBand_sportDataModelRealTime.setTotalDistance((float) objects[1]);
//            watch_smartBand_sportDataModelRealTime.setTotalCalories((int) objects[2]);
//            watch_smartBand_sportDataModelRealTime.setDateStr(TimeUtils.getTodayYYYYMMDD());
//            watch_smartBand_sportDataModelRealTime.setDeviceId(mCurrentDevice.deviceName);
//            watch_smartBand_sportDataModelRealTime.setUserId(mUserId);
//            watch_smartBand_sportDataModelRealTime.setTimestamp(System.currentTimeMillis());
//            ParseData.saveWatchSmartBandSportDataModel(watch_smartBand_sportDataModelRealTime);
            message.what = HandlerContans.mRealDataHr;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 100);

            //updateTextView(tvHeartRate, String.format(getString(R.string.heart_rate), rate));
        }

        @Override
        public void onOnceMeasureComplete(int rate) {
            Long currentTime = isSameOption(syncOnceHr);
            Log.e(TAG, "----------onOnceMeasureComplete: " + rate+"\n"+currentTime+"\n"+syncOnceHr);
            if (currentTime == syncOnceHr) {
                return;
            } else {
                syncOnceHr = currentTime;
            }
            if (mCurrentDevice != null) {
                if (rate >= 30 && rate < 255) {
                    DeviceDataSave.saveOneceHrData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), rate, System.currentTimeMillis(), String.valueOf(0));
                }
                mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 500);
            }

        }

        @Override
        public void onHistoryHeartRate(List<CRPHistoryHeartRateInfo> list) {

        }

        @Override
        public void onMeasureComplete(CRPHistoryDynamicRateType crpHistoryDynamicRateType, CRPHeartRateInfo crpHeartRateInfo) {
            //连接情况下返回的锻炼运动心率数据
            if (crpHeartRateInfo.getHeartRateType() == CRPHeartRateInfo.HeartRateType.PART_HEART_RATE) {

                //需要去保存心率的锻炼数据
                //然后取拉一次锻炼数据
                Long currentTime = isSameOption(syncPractiseHr);
                if (currentTime == syncPractiseHr) {
                    return;
                } else {
                    syncPractiseHr = currentTime;
                }
                Logger.myLog(TAG + "onMeasureComplete PART_HEART_RATE" + crpHeartRateInfo.getHeartRateList() + "info.getTimeInterval():" + crpHeartRateInfo.getTimeInterval());
                ThreadPoolUtils.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        W81DeviceEexerciseAction w81DeviceEexerciseAction = new W81DeviceEexerciseAction();
                        w81DeviceEexerciseAction.saveExerciseHrData(String.valueOf(BaseManager.mUserId), mCurrentDevice.getDeviceName(), crpHeartRateInfo.getHeartRateList(), crpHeartRateInfo.getTimeInterval(), crpHeartRateInfo.getStartTime());
                    }
                });

                mBleConnection.queryMovementHeartRate();
            }

            Log.d(TAG, "onOnceMeasureComplete: " + crpHeartRateInfo.getHeartRateType() + "getStartMeasureTime:" + crpHeartRateInfo.getStartTime());
            if (crpHeartRateInfo != null && crpHeartRateInfo.getHeartRateList() != null) {
                for (Integer integer : crpHeartRateInfo.getHeartRateList()) {
                    Log.d(TAG, "onMeasureComplete: " + integer);
                }
            }
        }

//        @Override
//        public void onMeasureComplete(final CRPHeartRateInfo info) {
//
//            //连接情况下返回的锻炼运动心率数据
//            if (info.getHeartRateType() == CRPHeartRateInfo.HeartRateType.PART_HEART_RATE) {
//
//                //需要去保存心率的锻炼数据
//                //然后取拉一次锻炼数据
//                Long currentTime = isSameOption(syncPractiseHr);
//                if (currentTime == syncPractiseHr) {
//                    return;
//                } else {
//                    syncPractiseHr = currentTime;
//                }
//                Logger.myLog(TAG + "onMeasureComplete PART_HEART_RATE" + info.getMeasureData() + "info.getTimeInterval():" + info.getTimeInterval());
//                ThreadPoolUtils.getInstance().addTask(new Runnable() {
//                    @Override
//                    public void run() {
//                        W81DeviceEexerciseAction w81DeviceEexerciseAction = new W81DeviceEexerciseAction();
//                        w81DeviceEexerciseAction.saveExerciseHrData(String.valueOf(BaseManager.mUserId), mCurrentDevice.getDeviceName(), info.getMeasureData(), info.getTimeInterval(), info.getStartMeasureTime());
//                    }
//                });
//
//                mBleConnection.queryMovementHeartRate();
//            }
//
//            Log.d(TAG, "onOnceMeasureComplete: " + info.getHeartRateType() + "getStartMeasureTime:" + info.getStartMeasureTime());
//            if (info != null && info.getMeasureData() != null) {
//                for (Integer integer : info.getMeasureData()) {
//                    Log.d(TAG, "onMeasureComplete: " + integer);
//                }
//            }
//        }

        @Override
        public void on24HourMeasureResult(final CRPHeartRateInfo info) {

            List<Integer> data = info.getHeartRateList();
            //拉的历史的锻炼心率数据
           // Logger.myLog(TAG + "on24HourMeasureResult   " + info.getHeartRateType() + " info.getMeasureData():" + info.getHeartRateList());
            if (info.getHeartRateType() == CRPHeartRateInfo.HeartRateType.PART_HEART_RATE) {
                Long currentTime = isSameOption(syncHrHistory);
                if (currentTime == syncHrHistory) {
                    return;
                } else {
                    syncHrHistory = currentTime;
                }
                if (mCurrentDevice != null) {
                    ThreadPoolUtils.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            W81DeviceEexerciseAction w81DeviceEexerciseAction = new W81DeviceEexerciseAction();
                            w81DeviceEexerciseAction.saveExerciseHrData(String.valueOf(BaseManager.mUserId), mCurrentDevice.getDeviceName(), info.getHeartRateList(), info.getTimeInterval(), info.getStartTime());
                        }
                    });
                }
                //查询锻炼数据


            } else if (info.getHeartRateType() == CRPHeartRateInfo.HeartRateType.TODAY_HEART_RATE) {
                Long currentTime = isSameOption(syncHrTodayHistory);
                if (currentTime == syncHrTodayHistory) {
                    return;
                } else {
                    syncHrTodayHistory = currentTime;
                }
                updateHrData(System.currentTimeMillis(), data, info.getTimeInterval());


            } else if (info.getHeartRateType() == CRPHeartRateInfo.HeartRateType.YESTERDAY_HEART_RATE) {
                Long currentTime = isSameOption(syncHrYesterdayHistory);
                if (currentTime == syncHrYesterdayHistory) {
                    return;
                } else {
                    syncHrYesterdayHistory = currentTime;
                }
                updateHrData(System.currentTimeMillis() - 24 * 60 * 60 * 1000, data, info.getTimeInterval());
                mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieTODAYHR, 500);
                // Logger.myLog("getStrDateHrData" + w81DeviceDetailData);
            }


            /*for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "on24HourMeasureResult: " + data.get(i) + "i-----" + i);
            }*/

        }

        @Override
        public void onMovementMeasureResult(List<CRPMovementHeartRateInfo> list) {
            Logger.myLog(TAG,"------锻炼数据="+new Gson().toJson(list));

            Long currentTime = isSameOption(movementMeasureResult);
            if (currentTime == movementMeasureResult) {
                return;
            } else {
                movementMeasureResult = currentTime;
            }
            for (final CRPMovementHeartRateInfo info : list) {
                if (info != null) {

                    //String userId, String deviceId, String wristbandSportDetailId, String dateStr,
                    //                                       Long startTimestamp, Long endTimestamp, String vaildTimeLength,
                    //                                       String exerciseType, String totalSteps, String totalDistance, String totalCalories
                    if (info.getStartTime() <= 0 || info.getEndTime() <= 0 || info.getValidTime() == 0 || info.getType() < 0) {
                        continue;
                    }
//
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.set(Calendar.YEAR, 2020);
//                    calendar.set(Calendar.MONTH, 0);
//                    calendar.set(Calendar.DAY_OF_MONTH, 1);
//                    long times = calendar.getTimeInMillis();
//                    if (info.getStartTime() < times) {
//                        continue;
//                    }


//                    if(info.getSteps()<0)
//                        return;
//                    if(info.getStartTime() < info.getEndTime())
//                        return;
//                    if(info.getSteps() > 100000)
//                        return;


                    W81DeviceEexerciseAction action = new W81DeviceEexerciseAction();
                    //返回的是毫秒
                    Logger.myLog(TAG + "onMovementMeasureResult: " + info.getStartTime() + "-------" + info.getEndTime() + "--" + info.getType() + "--" + info.getValidTime() + "info.getSteps()" + info.getSteps() + "info.getDistance()" + info.getDistance() + "info.getCalories()" + info.getCalories());

                    action.saveDeviceExerciseData(BaseManager.mUserId, mCurrentDevice.deviceName, "0", TimeUtils.getTimeByyyyyMMdd(info.getStartTime()), info.getStartTime(), info.getEndTime(), info.getValidTime(), (info.getType() < 0) ? 1 : info.getType() + 1, info.getSteps(), info.getDistance(), info.getCalories());


                }
            }


            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieExecise, 1000);
        }

    };

    public void updateHrData(final long currentTime, final List<Integer> list, final int timeInterval) {

        Logger.myLog("updateHrData:" + list.size());
        if (list != null && list.size() > 288) {
            return;
        }
        final List<Integer> tempList = new ArrayList<>();
        tempList.addAll(list);
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                W81DeviceDataAction w81DeviceDataAction = new W81DeviceDataAction();
                if (mCurrentDevice != null) {
                    w81DeviceDataAction.saveW81DeviceHrData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), "0", TimeUtils.getTimeByyyyyMMdd(currentTime), currentTime, tempList, timeInterval);
                    // mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieH, 1000);

                }
            }
        });


    }

    CRPBloodPressureChangeListener mBloodPressureChangeListener = new CRPBloodPressureChangeListener() {
        @Override
        public void onContinueState(boolean b) {

        }

        @Override
        public void onBloodPressureChange(int sbp, int dbp) {
            //收缩压 Systolic blood pressure
            //舒张压 diastolic blood pressure
            Log.d(TAG, "sbp: " + sbp + ",dbp: " + dbp);
            Long currentTime = isSameOption(bloodPressureChange);
            if (currentTime == bloodPressureChange) {
                return;
            } else {
                bloodPressureChange = currentTime;
            }

            if (mCurrentDevice != null && !(sbp > 200 || dbp > 200)) {
                DeviceDataSave.saveBloodPressureData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), sbp, dbp, System.currentTimeMillis(), String.valueOf(0));
            }
            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieBloodPressureMessureSuccess, 500);


            //updateTextView(tvBloodPressure,
            //     String.format(getString(R.string.blood_pressure), sbp, dbp));
        }

        @Override
        public void onHistoryBloodPressure(List<CRPHistoryBloodPressureInfo> list) {

        }

        @Override
        public void onContinueBloodPressure(CRPBloodPressureInfo crpBloodPressureInfo) {

        }

    };


    CRPFindPhoneListener mFindPhoneListener = new CRPFindPhoneListener() {
        @Override
        public void onFindPhone() {
            Log.d(TAG, "onFindPhone");
        }

        @Override
        public void onFindPhoneComplete() {
            Log.d(TAG, "onFindPhoneComplete");
        }
    };


    private final CRPBloodOxygenChangeListener mBloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {
        @Override
        public void onContinueState(boolean b) {

        }

        @Override
        public void onTimingMeasure(int i) {
            Log.e(TAG,"--------测量血氧onTimingMeasure="+i);
        }

        @Override
        public void onBloodOxygen(int i) {
            Log.e(TAG,"--------测量血氧返回="+i);
            Long currentTime = isSameOption(bloodOxygenChange);
            if (currentTime == bloodOxygenChange) {
                return;
            } else {
                bloodOxygenChange = currentTime;
            }

            if (mCurrentDevice != null) {
                if (i <= 100) {
                    DeviceDataSave.saveOxyenModelData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), i, System.currentTimeMillis(), String.valueOf(0));
                }
                mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess, 500);
            }

        }

        @Override
        public void onHistoryBloodOxygen(List<CRPHistoryBloodOxygenInfo> list) {

        }

        @Override
        public void onContinueBloodOxygen(CRPBloodOxygenInfo crpBloodOxygenInfo) {

        }

//        @Override
//        public void onBloodOxygenChange(int bloodOxygen) {
//
//            Long currentTime = isSameOption(bloodOxygenChange);
//            if (currentTime == bloodOxygenChange) {
//                return;
//            } else {
//                bloodOxygenChange = currentTime;
//            }
//
//            if (mCurrentDevice != null) {
//                if (bloodOxygen <= 100) {
//                    DeviceDataSave.saveOxyenModelData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), bloodOxygen, System.currentTimeMillis(), String.valueOf(0));
//                }
//                mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess, 500);
//            }
//
//            //updateTextView(tvBloodOxygen,
//            //    String.format(getString(R.string.blood_oxygen), bloodOxygen));
//        }
    };

    CRPBleECGChangeListener mECGChangeListener = new CRPBleECGChangeListener() {
        @Override
        public void onECGChange(int[] ecg) {
            for (int i = 0; i < ecg.length; i++) {
                Log.d(TAG, "ecg: " + ecg[i]);
            }
        }

        @Override
        public void onMeasureComplete() {
            Log.d(TAG, "onMeasureComplete");
        }

        @Override
        public void onTransCpmplete(Date date) {
            Log.d(TAG, "onTransCpmplete");
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };


    public void setWatchFace(int enable) {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.sendDisplayWatchFace((byte) enable);
        }
    }

    public void find_Device() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.findDevice();
        }
    }

    //设置用户信息

    public void sendUserInfoW81() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            crpUserInfo = new CRPUserInfo(BaseManager.mWeight, (int) BaseManager.mHeight, BaseManager.mSex == 1 ? CRPUserInfo.MALE : CRPUserInfo.FEMALE, BaseManager.mAge);

            Logger.myLog(TAG + "setting user:" + crpUserInfo.getWeight() + ",height:" + crpUserInfo.getHeight() + ",mSex:" + crpUserInfo.getGender() + ",age:" + crpUserInfo.getAge());

            mBleConnection.sendUserInfo(crpUserInfo);
        }
    }


    //勿扰模式回调
    CRPDevicePeriodTimeCallback crpDevicePeriodTimeCallback = new CRPDevicePeriodTimeCallback() {
        @Override
        public void onPeriodTime(int i, CRPPeriodTimeInfo crpPeriodTimeInfo) {
//            QUICK_VIEW_TYPE



            Long currentTime;
            if (i == 2) {




                currentTime = isSameOption(onPeriodTime2);
                if (currentTime == onPeriodTime2) {
                    return;
                } else {
                    onPeriodTime2 = currentTime;
                }

                if (crpPeriodTimeInfo.getStartHour() == crpPeriodTimeInfo.getEndHour() && crpPeriodTimeInfo.getEndMinute() == crpPeriodTimeInfo.getEndMinute()) {
                    //抬腕亮屏 抬腕亮屏的类型	int 0,1,2 0: 全天开,1特定时间段开 2关闭
                    BleSPUtils.putInt(mContext,BleSPUtils.KEY_TURN_WRIST,0);

                    Logger.myLog(TAG + "queryQuickViewTime");
                    Logger.myLog(TAG + "queryQuickViewTime:QUICK_VIEW_TYPE" + i + "，crpPeriodTimeInfo，getStartHour：" + crpPeriodTimeInfo.getStartHour() + ":getStartMinute，" + crpPeriodTimeInfo.getStartMinute() + "，getEndHour：" + crpPeriodTimeInfo.getEndHour() + ",getEndMinute：" + crpPeriodTimeInfo.getEndMinute());
                    Bracelet_W311_LiftWristToViewInfoModel model = new Bracelet_W311_LiftWristToViewInfoModel();
                    model.setSwichType(0);
                    model.setDeviceId(mCurrentDevice.deviceName);
                    model.setUserId(BaseManager.mUserId);
                    model.setStartHour(Constants.defStarHour);
                    model.setStartMin(0);
                    model.setEndHour(Constants.defEndHour);
                    model.setEndMin(0);
                    model.setIsNextDay(false);
                    Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);
                } else {
                    //抬腕亮屏
                    BleSPUtils.putInt(mContext,BleSPUtils.KEY_TURN_WRIST,1);
                    Logger.myLog(TAG + "queryQuickViewTime");
                    Logger.myLog(TAG + "queryQuickViewTime:QUICK_VIEW_TYPE" + i + "，crpPeriodTimeInfo，getStartHour：" + crpPeriodTimeInfo.getStartHour() + ":getStartMinute，" + crpPeriodTimeInfo.getStartMinute() + "，getEndHour：" + crpPeriodTimeInfo.getEndHour() + ",getEndMinute：" + crpPeriodTimeInfo.getEndMinute());
                    Bracelet_W311_LiftWristToViewInfoModel model = new Bracelet_W311_LiftWristToViewInfoModel();
                    model.setSwichType(1);
                    model.setDeviceId(mCurrentDevice.deviceName);
                    model.setUserId(BaseManager.mUserId);
                    model.setStartHour(crpPeriodTimeInfo.getStartHour());
                    model.setStartMin(crpPeriodTimeInfo.getStartMinute());
                    model.setEndHour(crpPeriodTimeInfo.getEndHour());
                    model.setEndMin(crpPeriodTimeInfo.getEndMinute());
                    model.setIsNextDay(false);
                    Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);

                }


            }
            if (i == 1) {

                //保存勿扰模式
                String startHS = crpPeriodTimeInfo.getStartHour()<=9 ? String.format("0%s",crpPeriodTimeInfo.getStartHour()):crpPeriodTimeInfo.getStartHour()+"";
                String startMS = crpPeriodTimeInfo.getStartMinute()<=9 ? String.format("0%s",crpPeriodTimeInfo.getStartMinute()):crpPeriodTimeInfo.getStartMinute()+"";
                //String startTimeStr = crpPeriodTimeInfo.getStartHour()<=9 ? String.format("0%s",crpPeriodTimeInfo.getStartHour());
                String endHS = crpPeriodTimeInfo.getEndHour()<=9 ? String.format("0%s",crpPeriodTimeInfo.getEndHour()):crpPeriodTimeInfo.getEndHour()+"";
                String endMS = crpPeriodTimeInfo.getEndMinute()<=9 ? String.format("0%s",crpPeriodTimeInfo.getEndMinute()):crpPeriodTimeInfo.getEndMinute()+"";
                BleSPUtils.putString(mContext,BleSPUtils.KEY_DNT_STATUS,startHS+":"+startMS+"-"+endHS+":"+endMS);

                //DO_NOT_DISTRUB_TYPE
                Logger.myLog(TAG + "queryQuickViewTime:QUICK_VIEW_TYPE" + i + "，crpPeriodTimeInfo，getStartHour：" + crpPeriodTimeInfo.getStartHour() + ":getStartMinute，" + crpPeriodTimeInfo.getStartMinute() + "，getEndHour：" + crpPeriodTimeInfo.getEndHour() + ",getEndMinute：" + crpPeriodTimeInfo.getEndMinute());
                currentTime = isSameOption(onPeriodTime1);
                if (currentTime == onPeriodTime1) {
                    return;
                } else {
                    onPeriodTime1 = currentTime;
                }
                Watch_W516_SleepAndNoDisturbModel watch_w516_sleepAndNoDisturbModelyDeviceId = new Watch_W516_SleepAndNoDisturbModel();
                String startTime, endTime;
                if (crpPeriodTimeInfo.getStartHour() == crpPeriodTimeInfo.getEndHour() && crpPeriodTimeInfo.getStartMinute() == crpPeriodTimeInfo.getEndMinute()) {
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setOpenNoDisturb(false);
                    startTime = Constants.defEndTime;
                    endTime = Constants.defStartTime;
                } else {
                    startTime = CommonDateUtil.formatTwoStr(crpPeriodTimeInfo.getStartHour()) + ":" + CommonDateUtil.formatTwoStr(crpPeriodTimeInfo.getStartMinute());
                    endTime = CommonDateUtil.formatTwoStr(crpPeriodTimeInfo.getEndHour()) + ":" + CommonDateUtil.formatTwoStr(crpPeriodTimeInfo.getEndMinute());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setOpenNoDisturb(true);
                }
                watch_w516_sleepAndNoDisturbModelyDeviceId.setDeviceId(mCurrentDevice.getDeviceName());
                watch_w516_sleepAndNoDisturbModelyDeviceId.setUserId(BaseManager.mUserId);
                watch_w516_sleepAndNoDisturbModelyDeviceId.setNoDisturbStartTime(startTime);
                watch_w516_sleepAndNoDisturbModelyDeviceId.setNoDisturbEndTime(endTime);
                ParseData.saveOrUpdateWatchW516SleepAndNoDisturb(watch_w516_sleepAndNoDisturbModelyDeviceId);
            }


        }
    };


    //全天开就是两个时间段相同

    public void sendQuickViewAndTime(int type, int startHour, int startMin, int endHour, int endMin) {
        if (type == 2) {
            sendQuickView(false);
        } else {
            sendQuickView(true);
        }
        sendQuickViewTime(startHour, startMin, endHour, endMin);
    }

    public void sendQuickView(boolean state) {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.sendQuickView(state);
        }
    }

    public void sendQuickViewTime(int startHour, int startMin, int endHour, int endMin) {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            CRPPeriodTimeInfo timeInfo = new CRPPeriodTimeInfo();
            timeInfo.setStartHour(startHour);
            timeInfo.setStartMinute(startMin);
            timeInfo.setEndMinute(endMin);
            timeInfo.setEndHour(endHour);
            mBleConnection.sendQuickViewTime(timeInfo);
        }
    }

    public void senddistrub(boolean open, int startHour, int startMin, int endHour, int endMin) {

        Logger.myLog(TAG + "senddistrub,startHour:" + startHour + ",startMin:" + startMin + ",endHour:" + endHour + ",endMin:" + endMin);
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            Logger.myLog(TAG + "senddistrub,open" + open + "startHour:" + startHour + ",startMin:" + startMin + ",endHour:" + endHour + ",endMin:" + endMin);
            CRPPeriodTimeInfo timeInfo = new CRPPeriodTimeInfo();
            if (open) {
                timeInfo.setStartHour(startHour);
                timeInfo.setStartMinute(startMin);
                timeInfo.setEndMinute(endMin);
                timeInfo.setEndHour(endHour);
            } else {
                timeInfo.setStartHour(0);
                timeInfo.setStartMinute(0);
                timeInfo.setEndMinute(0);
                timeInfo.setEndHour(0);
            }
            mBleConnection.sendDoNotDistrubTime(timeInfo);
        }

    }


    public void sendSedentaryReminderAndTime(List<SedentaryRemind> list) {
        for (int i = 0; i < list.size(); i++) {
            SedentaryRemind sedentaryRemind = list.get(i);


            Logger.myLog(TAG + "sendSedentaryReminderAndTime：" + sedentaryRemind.isOn());
            Logger.myLog(TAG + "sendSedentaryReminderAndTime：" + sedentaryRemind.getBeginHour() + ",sedentaryRemind.getEndHour():" + sedentaryRemind.getEndHour());


            sendSedentaryReminder(sedentaryRemind.isOn());
            sendRemindersToMovePeriodInfo(sedentaryRemind.getBeginHour(), sedentaryRemind.getBeginMin(), sedentaryRemind.getEndHour(), sedentaryRemind.getEndMin(), sedentaryRemind.getNoExerceseTime());
        }
    }


    public void sendSedentaryReminder(boolean enable) {
        Logger.myLog(TAG + "sendSedentaryReminder:" + enable);
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.sendSedentaryReminder(enable);
        }
    }


    //手环支持24小时定时测量心率，从0点0分开始测量，可以设置测量的时间间隔，时间间隔为5分钟的倍数
    //如果设置的是1就是打开24小时的心率监测
    public void openTimingMeasureHeartRate(int time) {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            //mBleConnection.openTimingMeasureHeartRate(time);
            mBleConnection.enableTimingMeasureHeartRate(time);
        }
    }

    //关闭定点心率监测
    public void closeTimingMeasureHeartRate() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            //mBleConnection.closeTimingMeasureHeartRate();
            mBleConnection.disableTimingMeasureHeartRate();
        }
    }


    public void sendRemindersToMovePeriodInfo(int starHour, int startMin, int endHour, int endMin, int period) {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            CRPSedentaryReminderPeriodInfo crpRemindersToMovePeriodInfo = new CRPSedentaryReminderPeriodInfo();
            crpRemindersToMovePeriodInfo.setStartHour((byte) starHour);
            crpRemindersToMovePeriodInfo.setEndHour((byte) endHour);
            crpRemindersToMovePeriodInfo.setPeriod((byte) period);
            //步数的公式  60分钟50步
            int maxStep = (int) (period * 50.0 / 60);
            crpRemindersToMovePeriodInfo.setSteps((byte) maxStep);
            mBleConnection.sendSedentaryReminderPeriod(crpRemindersToMovePeriodInfo);
        }
    }


    public void sendSwitchCameraView() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            //mBleConnection.showCameraView();
            mBleConnection.enterCameraView();
            //mBleConnection.set
        }

    }


    public void sendOhterMessageSwitch(boolean enable) {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.sendOtherMessageState(enable);
        }
    }


    public void sendMessage(String message, int messageType) {

        Logger.myLog(TAG + "w813 sendMessage:" + message + "messageType:" + messageType + "mBleDevice.isConnected():" + mBleDevice.isConnected());

        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {


            boolean isHs = Utils.isDialogUpgrade(mCurrentDevice.deviceType);
            boolean isSmallScreen = Utils.isSmallScreen(mCurrentDevice.deviceType);

            CRPMessageInfo crpMessageInfo = new CRPMessageInfo();
            crpMessageInfo.setMessage(message);
            crpMessageInfo.setType(messageType);
            crpMessageInfo.setHs(isHs);
            crpMessageInfo.setSmallScreen(isSmallScreen);
            crpMessageInfo.setVersionCode(versionCode);
            mBleConnection.sendMessage(crpMessageInfo);
        }
    }


    //查询闹钟
    public void getAlarList() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.queryAllAlarmClock(new CRPDeviceAlarmClockCallback() {
                @Override
                public void onAlarmClock(final List<CRPAlarmClockInfo> list) {
                    Long currentTime = isSameOption(getAlarListTime);
                    if (currentTime == getAlarListTime) {
                        return;
                    } else {
                        getAlarListTime = currentTime;
                    }


                    Logger.myLog(TAG + "queryAllAlarmClock");
                    CRPAlarmClockInfo crpAlarmClockInfo;
                    List<Bracelet_W311_AlarmModel> AlarmListlist = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        crpAlarmClockInfo = list.get(i);
                        /**
                         *     public static final int FIRST_CLOCK = 0;
                         public static final int SECOND_CLOCK = 1;
                         public static final int THIRD_CLOCK = 2;
                         public static final byte SINGLE = 0;
                         public static final byte SUNDAY = 1;
                         public static final byte MONDAY = 2;
                         public static final byte TUESDAY = 4;
                         public static final byte WEDNESDAY = 8;
                         public static final byte THURSDAY = 16;
                         public static final byte FRIDAY = 32;
                         public static final byte SATURDAY = 64;
                         public static final byte EVERYDAY = 127;
                         */

                        // byte[] booleanArrayG = Utils.getBooleanArray((byte) crpAlarmClockInfo.getRepeatMode());
                        int repeate = crpAlarmClockInfo.getRepeatMode();
                        // Logger.myLog("queryAllAlarmClock:reapeate" + Integer.toBinaryString(repeate));
                        int repeate1 = repeate >> 1;
                        int reapeate2 = (repeate & 0x01) << 6;
                        int reapeate3 = reapeate2 | repeate1;
                        Integer.toBinaryString(reapeate3);
                        Logger.myLog(TAG + "queryAllAlarmClock11111111111:" + crpAlarmClockInfo.getDate() + ",getHour:" + crpAlarmClockInfo.getHour() + ",getMinute：" + crpAlarmClockInfo.getMinute() + "，getRepeatMode：" + crpAlarmClockInfo.getRepeatMode() + ",isEnable" + crpAlarmClockInfo.isEnable());
                        //&& crpAlarmClockInfo.getRepeatMode() == 0 仅此一次

                        boolean isTrue = false;

                        if (crpAlarmClockInfo.getHour() == 0 && crpAlarmClockInfo.getMinute() == 0 && crpAlarmClockInfo.getRepeatMode() == 0 && crpAlarmClockInfo.isEnable() == false) {
                            // return;
                        } else {
                            //  Logger.myLog("queryAllAlarmClock:reapeate3" + "repeate1：" + repeate1 + "reapeate2:" + reapeate2 + Integer.toBinaryString(reapeate3));
                            Bracelet_W311_AlarmModel alarmModel = new Bracelet_W311_AlarmModel();
                            alarmModel.setDeviceId(mCurrentDevice.getDeviceName());
                            alarmModel.setUserId(BaseManager.mUserId);
                            alarmModel.setRepeatCount(repeate);
                            alarmModel.setIsOpen(crpAlarmClockInfo.isEnable());
                            //保存闹钟的序号
                            alarmModel.setAlarmId(crpAlarmClockInfo.getId());
                            alarmModel.setTimeString(CommonDateUtil.formatTwoStr(crpAlarmClockInfo.getHour()) + ":" + CommonDateUtil.formatTwoStr(crpAlarmClockInfo.getMinute()));
                            AlarmListlist.add(alarmModel);
                        }
                    }


                    Bracelet_W311_AlarmModelAction.saveW311AlarmModel(AlarmListlist);
                    mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieAlarList, 2000);

                    //

                }
            });
        }

    }


    /**
     * 同步今日步数
     */
    public void syncTodayStep() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.syncStep();
        }

    }

    /**
     * bleConnection.syncPastStep
     * CRPPastTimeType.YESTERDAY_STEPS
     * CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS
     */
    public void syncPastStep() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.syncPastStep(CRPPastTimeType.YESTERDAY_STEPS);
            mBleConnection.syncPastStep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS);
        }
    }

    /**
     * 同步今日睡眠
     */
    public void syncTodaySleep() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.syncSleep();
        }

    }

    /**
     * bleConnection.syncPastStep
     * CRPPastTimeType.YESTERDAY_STEPS
     * CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS
     */
    public void syncPastSleep() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.syncPastSleep(CRPPastTimeType.YESTERDAY_SLEEP);
            mBleConnection.syncPastStep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_SLEEP);
        }
    }

    /**
     * 查询今日心率的
     * bleConnection.queryTimingMeasureHeartRate(dayNumber);
     */

    public void queryHeartRate() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            //mBleConnection.queryTodayHeartRate(CRPHeartRateType.ALL_DAY_HEART_RATE);
            //获取当天的心率数据
            mBleConnection.queryTodayHeartRate(CRPHeartRateType.TIMING_MEASURE_HEART_RATE);
            //未连接的情况下可以获取锻炼数据的最后一次数据
            mBleConnection.queryLastDynamicRate(CRPHistoryDynamicRateType.THIRD_HEART_RATE);
            //查询最后三次
            mBleConnection.queryMovementHeartRate();

            // mBleConnection.queryMovementHeartRate();
            //获取昨天的心率数据
            mBleConnection.queryPastHeartRate();

        }
    }

    //下拉同步数据
    public void syncDeviceData() {
        syncAllData();
        queryHeartRate();
    }


    //同步当天的数据
    public void syncTodayData() {
        Logger.myLog("syncTodayData---------------");
        syncTodayStep();
        syncTodaySleep();
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            //mBleConnection.queryTodayHeartRate(CRPHeartRateType.ALL_DAY_HEART_RATE);
            //获取当天的心率数据
            mBleConnection.queryTodayHeartRate(CRPHeartRateType.TIMING_MEASURE_HEART_RATE);
        }

    }


    private void syncAllData() {
        mHandler.sendEmptyMessage(HandlerContans.mSyncHandlerStartSync);
        SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(1, 2), false);
        syncTodayStep();
        currentStep = -1;
        syncTodaySleep();
        syncPastSleep();
        syncPastStep();
        if (timeOutHandler.hasMessages(HandlerContans.mSyncHandlerStepTimeOut)) {
            timeOutHandler.removeMessages(HandlerContans.mSyncHandlerStepTimeOut);
        }
        timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mSyncHandlerStepTimeOut, SYNC_TIMEOUT);

        if (Constants.wristbandWeather != null) {
            sendWeatherToday(Constants.wristbandWeather.getCondition(), Constants.cityName);
            sendFutureWeather(Constants.wristbandWeather.getForecast15Days());
        }
    }


    public void sendMeasureOxygenState(boolean isStart) {
        if (isStart) {
            startMeasureOxygen();
        } else {
            endMeasureOxygen();
        }
    }


    public void sendMeasureOnceHrdataState(boolean isStart) {
        Log.e("测量","-----开始测量-="+isStart);
        if (isStart) {
            startMeasureOnceHeartRate();
        } else {
            endMeasureOnceHeartRate();
        }
    }

    public void sendMeasureBloodPressureState(boolean isStart) {
        if (isStart) {
            startMeasureBloodPressure();
        } else {
            endMeasureBoodPressure();
        }
    }

    public void startMeasureBloodPressure() {
        if (mBleDevice.isConnected()) {
            mBleConnection.startMeasureBloodPressure();
        }
    }

    public void startMeasureOnceHeartRate() {
        if (mBleDevice.isConnected()) {
            // mBleConnection.startMeasureDynamicRate();
            mBleConnection.startMeasureOnceHeartRate();
        }
    }

    public void endMeasureBoodPressure() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.stopMeasureBloodPressure();
        }
    }

    public void endMeasureOnceHeartRate() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            //mBleConnection.stopMeasureDynamicRtae();
            mBleConnection.stopMeasureOnceHeartRate();
        }
    }


    public void startMeasureOxygen() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.startMeasureBloodOxygen();
        }

    }

    public void endMeasureOxygen() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.stopMeasureBloodOxygen();
        }
    }

    //bleConnection.checkFirmwareVersion(CRPDeviceNewFirmwareVersionCallback, version, CRPFirmwareUpgradeType);

    String bandFirmwareVersion = "";


    public void findVersion() {
        if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
            mBleConnection.queryFrimwareVersion(crpDeviceFirmwareVersionCallback);
        }

    }

    public void sendWeatherToday(WristbandData wristbandData, String city) {

        Logger.myLog(TAG + "sendWeatherToday" + city + "wristbandData.getPm25():" + wristbandData.getPm25());
        CRPTodayWeatherInfo crpTodayWeatherInfo = new CRPTodayWeatherInfo();
        if (wristbandData != null) {
            crpTodayWeatherInfo.setCity(city);
            if (!TextUtils.isEmpty(wristbandData.getWeatherId()) && Constants.W81WeatherConfig.containsKey(wristbandData.getWeatherId())) {
                crpTodayWeatherInfo.setWeatherId(Constants.W81WeatherConfig.get(wristbandData.getWeatherId()));
            }
            if (!TextUtils.isEmpty(wristbandData.getTemp())) {
                crpTodayWeatherInfo.setTemp(Integer.parseInt(wristbandData.getTemp()));
            }

            if (!TextUtils.isEmpty(wristbandData.getPm25())) {
                crpTodayWeatherInfo.setPm25(Integer.parseInt(wristbandData.getPm25()));
            }
            //crpTodayWeatherInfo.setPm25(200);
        }
        mBleConnection.sendTodayWeather(crpTodayWeatherInfo);
    }

    public void sendFutureWeather(List<WristbandForecast> list) {


        Logger.myLog(TAG + "sendFutureWeather");

        List<CRPFutureWeatherInfo.FutureBean> futureList = new ArrayList<>();
        CRPFutureWeatherInfo.FutureBean futureBean;
        for (int i = 0; i < list.size(); i++) {
            futureBean = new CRPFutureWeatherInfo.FutureBean();
            String strHighTemp = list.get(i).getHighTemperature();
            String strLowTemp = list.get(i).getLowTemperature();
            int highTemp = TextUtils.isEmpty(strHighTemp) ? 0 : Integer.parseInt(strHighTemp);
            int lowTemp = TextUtils.isEmpty(strLowTemp) ? 0 : Integer.parseInt(strLowTemp);
            futureBean.setHighTemperature(highTemp);
            futureBean.setLowTemperature(lowTemp);
            if (!TextUtils.isEmpty(list.get(i).getWeatherId()) && Constants.W81WeatherConfig.containsKey(list.get(i).getWeatherId())) {
                futureBean.setWeatherId(Constants.W81WeatherConfig.get(list.get(i).getWeatherId()));
            }
            futureList.add(futureBean);
        }

        CRPFutureWeatherInfo CRPFutureWeatherInfo = new CRPFutureWeatherInfo();
        CRPFutureWeatherInfo.setFuture(futureList);
        mBleConnection.sendFutureWeather(CRPFutureWeatherInfo);
    }


    public Long isSameOption(Long times) {
        if (System.currentTimeMillis() - times < 1000) {
            return times;
        }
        return System.currentTimeMillis();
    }


    public CRPBleDevice getmBleDevice() {
        return this.mBleDevice;
    }

    public CRPBleConnection getmBleConnection() {
        return this.mBleConnection;
    }

}
