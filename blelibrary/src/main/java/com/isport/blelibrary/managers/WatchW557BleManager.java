package com.isport.blelibrary.managers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.crrepa.ble.conn.type.CRPWeatherId;
import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.bluetooth.callbacks.DataBean;
import com.isport.blelibrary.bluetooth.callbacks.WatchW557GattCallBack;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.db.CommonInterFace.DeviceSendCmdType;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_AlarmModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_NotifyModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SedentaryModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SettingModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SleepAndNoDisturbModelAction;
import com.isport.blelibrary.db.parse.DeviceDataSave;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.watch.Watch_SmartBand_SportDataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_AlarmModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SettingModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.S002BDevice;
import com.isport.blelibrary.deviceEntry.impl.W526Device;
import com.isport.blelibrary.deviceEntry.impl.W557Device;
import com.isport.blelibrary.deviceEntry.impl.W560BDevice;
import com.isport.blelibrary.deviceEntry.impl.W560Device;
import com.isport.blelibrary.deviceEntry.impl.W812BDevice;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.interfaces.BluetoothListener;
import com.isport.blelibrary.interfaces.BluetoothSettingSuccessListener;
import com.isport.blelibrary.managers.order.ISportOrder;
import com.isport.blelibrary.managers.order.S002Cmd;
import com.isport.blelibrary.managers.order.W526Cmd;
import com.isport.blelibrary.managers.order.W557Cmd;
import com.isport.blelibrary.observe.CmdProgressObservable;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.observe.TakePhotObservable;
import com.isport.blelibrary.result.impl.w311.BraceletW311SyncStart;
import com.isport.blelibrary.result.impl.watch.DeviceAlarmListResult;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.result.impl.watch.DeviceSendCmdResult;
import com.isport.blelibrary.result.impl.watch.WatchBatteryResult;
import com.isport.blelibrary.result.impl.watch.WatchFACEResult;
import com.isport.blelibrary.result.impl.watch.WatchGOALSTEPResult;
import com.isport.blelibrary.result.impl.watch.WatchGoalCalorieResult;
import com.isport.blelibrary.result.impl.watch.WatchGoalDistanceResult;
import com.isport.blelibrary.result.impl.watch.WatchHrHeartResult;
import com.isport.blelibrary.result.impl.watch.WatchRealTimeResult;
import com.isport.blelibrary.result.impl.watch.WatchTempSubResult;
import com.isport.blelibrary.result.impl.watch.WatchVersionResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516AlarmResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516InAdjustModeReslut;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SettingResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SleepSettingResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SyncResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW560AlarmResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DeviceTimesUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author
 * @Date 2019/2/19
 * @Fuction
 */

public class WatchW557BleManager extends BaseManager {
    public static String TAG = "WatchW557BleManager";
    private WatchW557GattCallBack mGattCallBack;
    private BluetoothListener bluetoothListener;
    private BluetoothSettingSuccessListener bluetoothSuceessListener;
    private int mCurrentDay;//当前同步的位置
    private int mFailedTimes;//同步失败的次数
    private W526Cmd w526Cmd;
    private W557Cmd w557Cmd;
    private S002Cmd s002Cmd;
    private final int SETTING_CMD_TIMEOUT = 2000;
    private final int SETTING_SYNC_TIMEOUT = 50000;
    private final int PRACTISE_CMD_TIMEOUT = 5000;
    private boolean isSigleSetting = false;

    public WatchW557BleManager() {
        w526Cmd = new W526Cmd();
        w557Cmd = new W557Cmd();
        s002Cmd = new S002Cmd();
    }

    public static volatile WatchW557BleManager instance;

    public static WatchW557BleManager getInstance(Context context) {
        if (instance == null) {
            synchronized (WatchW557BleManager.class) {
                if (instance == null) {
                    instance = new WatchW557BleManager();
                    instance.init(context);
                }
            }
        }
        return instance;
    }

    public static WatchW557BleManager getInstance() {
        if (instance == null) {
            synchronized (WatchW557BleManager.class) {
                if (instance == null) {
                    instance = new WatchW557BleManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化方法
     *
     * @param context
     */
    public void init(Context context) {
        super.init(context);
        // TODO: 2018/9/30 bind Service
        initHandler();
        initSuccessHandler();
        initNotiHandler();
        initSendHandler();
        initTimeout();
        initSyncHandler();
        setBTListener(btListener);
        setBTSettingSuccessListener(successListener);
    }

    /**
     * Bluetooth4.0连接NRF设备
     */
    public void connectNRF(W557Device baseDevice, boolean isConnectByUser) {
        if (bluetoothListener == null) {
            setBTListener(btListener);
        }
        if (bluetoothSuceessListener == null) {
            setBTSettingSuccessListener(successListener);
        }
        if (mGattCallBack == null) {
            mGattCallBack = new WatchW557GattCallBack(bluetoothListener, nrfService, baseDevice, bluetoothSuceessListener);
        } else {
            mGattCallBack.setBaseDevice(baseDevice);
        }
        connectNRF(mGattCallBack, baseDevice, isConnectByUser, 300);
    }
    public void connectNRF(W560BDevice baseDevice, boolean isConnectByUser) {
        if (bluetoothListener == null) {
            setBTListener(btListener);
        }
        if (bluetoothSuceessListener == null) {
            setBTSettingSuccessListener(successListener);
        }
        if (mGattCallBack == null) {
            mGattCallBack = new WatchW557GattCallBack(bluetoothListener, nrfService, baseDevice, bluetoothSuceessListener);
        } else {
            mGattCallBack.setBaseDevice(baseDevice);
        }
        connectNRF(mGattCallBack, baseDevice, isConnectByUser, 300);
    }

    public void connectNRF(S002BDevice baseDevice, boolean isConnectByUser) {
        if (bluetoothListener == null) {
            setBTListener(btListener);
        }
        if (bluetoothSuceessListener == null) {
            setBTSettingSuccessListener(successListener);
        }
        if (mGattCallBack == null) {
            mGattCallBack = new WatchW557GattCallBack(bluetoothListener, nrfService, baseDevice, bluetoothSuceessListener);
        } else {
            mGattCallBack.setBaseDevice(baseDevice);
        }
        connectNRF(mGattCallBack, baseDevice, isConnectByUser, 300);
    }

    public void connectNRF(W526Device baseDevice, boolean isConnectByUser) {
        if (bluetoothListener == null) {
            setBTListener(btListener);
        }
        if (bluetoothSuceessListener == null) {
            setBTSettingSuccessListener(successListener);
        }
        if (mGattCallBack == null) {
            mGattCallBack = new WatchW557GattCallBack(bluetoothListener, nrfService, baseDevice, bluetoothSuceessListener);
        } else {
            mGattCallBack.setBaseDevice(baseDevice);
        }
        connectNRF(mGattCallBack, baseDevice, isConnectByUser, 300);
    }

    public void connectNRF(W560Device baseDevice, boolean isConnectByUser) {
        if (bluetoothListener == null) {
            setBTListener(btListener);
        }
        if (bluetoothSuceessListener == null) {
            setBTSettingSuccessListener(successListener);
        }
        if (mGattCallBack == null) {
            mGattCallBack = new WatchW557GattCallBack(bluetoothListener, nrfService, baseDevice, bluetoothSuceessListener);
        } else {
            mGattCallBack.setBaseDevice(baseDevice);
        }
        connectNRF(mGattCallBack, baseDevice, isConnectByUser, 300);
    }

    public void connectNRF(W812BDevice baseDevice, boolean isConnectByUser) {
        if (bluetoothListener == null) {
            setBTListener(btListener);
        }
        if (bluetoothSuceessListener == null) {
            setBTSettingSuccessListener(successListener);
        }
        if (mGattCallBack == null) {
            mGattCallBack = new WatchW557GattCallBack(bluetoothListener, nrfService, baseDevice, bluetoothSuceessListener);
        } else {
            mGattCallBack.setBaseDevice(baseDevice);
        }
        connectNRF(mGattCallBack, baseDevice, isConnectByUser, 300);
    }

    @Override
    public void exit() {
        super.exit();
        if (mGattCallBack != null) {
            mGattCallBack.exit();
        }
    }

    public void setCurrentDevice(BaseDevice baseDevice) {
        mCurrentDevice = baseDevice;
    }

    /**
     * 断连
     */
    public void disconnect(boolean reconnect) {
        if (mGattCallBack != null) {
            Logger.myLog("去断开连接222222222222222");
            notiHandler.removeMessages(0x02);
            timeOutHandler.removeMessages(HandlerContans.mReceiveDataTimeout);
            timeOutHandler.removeMessages(HandlerContans.mDeviceStateFail);
            mGattCallBack.clearQueuryData();
            mGattCallBack.disconnect(reconnect);
        }
    }


    public void getBattery() {
        if (mGattCallBack != null) {
            mGattCallBack.internalReadBatteryLevel();
        }
    }

    public void close() {
        if (mGattCallBack != null) {
            mGattCallBack.gattClose();
        }
    }

    public void getDeviceVersion() {
        if (mGattCallBack != null) {
            mGattCallBack.internalReadFirmareVersion();
        }
    }

    private void initSuccessHandler() {
        final boolean isListenerNull = mBleReciveListeners == null;
        suceessHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HandlerContans.mGetTempSubSuccess:
                        if (!isListenerNull && mCurrentDevice != null)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceSendCmdResult(DeviceSendCmdType.send_cmd_set_temp_sub, mCurrentDevice.deviceType, mCurrentDevice.deviceName, mCurrentDevice.address, msg.arg1));
                            }
                        break;
                    case HandlerContans.mGetOnceHrSuccess:
                        if (!isListenerNull && mCurrentDevice != null)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceSendCmdResult(DeviceSendCmdType.send_cmd_once_hr, mCurrentDevice.deviceType, mCurrentDevice.deviceName, mCurrentDevice.address, msg.arg1));
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
                    case HandlerContans.mHandlerConnetSuccessState:
                        // TODO: 2019/1/7 device ID
//                        WatchDeviceInfo.putString(mContext, WatchDeviceInfo.WATCH_DEVICEID, mCurrentDevice
//                                .getAddress());

                        if (mDeviceInformationTable == null) {
                            mDeviceInformationTable = new DeviceInformationTable();
                        }
                        if (mCurrentDevice == null) {
                            return;
                        }
                        mDeviceInformationTable.setMac(mCurrentDevice.getAddress());
                        mDeviceInformationTable.setDeviceId(mCurrentDevice.getDeviceName());
                        ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, -1);

//                        ParseData.saveDeviceType(IDeviceType.TYPE_WATCH);
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).onConnResult(true, mGattCallBack.mIsConnectByUser, mCurrentDevice);
                            }
                        break;
                    case HandlerContans.mHandlerConnetting:
                        if (!isListenerNull && mCurrentDevice != null)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                Logger.myLog(TAG,"------HandlerContans.mHandlerConnettin--mCurrentDevice="+mCurrentDevice.toString());
                                mBleReciveListeners.get(i).onConnecting(mCurrentDevice);
                            }
                        break;
                    case HandlerContans.mHandlerConnetFailState:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                if (mCurrentDevice != null) {
                                    mBleReciveListeners.get(i).onConnResult(false, mGattCallBack.mIsConnectByUser, mCurrentDevice);
                                }
                            }
                        break;
                    case 2:
                        Bundle data2 = msg.getData();
                        WatchBatteryResult watchBatteryResult = (WatchBatteryResult) data2.getSerializable
                                ("onGetBattery");
                        // TODO: 2019/1/7 电量
//                        WatchDeviceInfo.putInt(mContext, WatchDeviceInfo.WATCH_BATTERY, watchBatteryResult == null
// ? 0 :
//                                watchBatteryResult.getQuantity());
                        mDeviceInformationTable.setBattery(watchBatteryResult == null ? 0 : watchBatteryResult
                                .getQuantity());
                        ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, 0);
                        // getDeviceVersion();//获取版本号
                        mGattCallBack.clearQueuryData();
                        //同步时间
                        mGattCallBack.addQueuryData(new DataBean(w526Cmd.syncTime(), SETTING_CMD_TIMEOUT, false));
                        sendHandler.sendEmptyMessageDelayed(HandlerContans.mSenderSetting, 1000);
                        getStateCount = 0;
                        mHandler.sendEmptyMessageDelayed(HandlerContans.mDeviceState, 3000);
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(watchBatteryResult);
                            }
                        break;
                    case HandlerContans.mHandlerbattery:
                        Bundle data3 = msg.getData();
                        WatchVersionResult watchVersionResult = (WatchVersionResult) data3.getSerializable
                                ("DeviceVersion");
                        mDeviceInformationTable.setVersion(watchVersionResult == null ? "" : watchVersionResult
                                .getVersion());
                        Logger.myLog(" DeviceVersion == " + (watchVersionResult == null ? "" : watchVersionResult
                                .getVersion()) + " mDeviceInformationTable == " + mDeviceInformationTable.toString());
                        ParseData.saveOrUpdateDeviceInfo(mDeviceInformationTable, 1);
                        // TODO: 2019/1/7 版本号
//                        WatchDeviceInfo.putString(mContext, WatchDeviceInfo.WATCH_VERSION, watchVersionResult ==null
//                                ? "" : watchVersionResult
//                                .getVersion());
                        // TODO: 2019/3/4 判断是否是第一次绑定,第一次绑定设置
                        //设置所有的设置项
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(watchVersionResult);

                                Logger.myLog("onBattreyOrVersion" + mCurrentDevice);
                                if (mCurrentDevice != null) {
                                    mBleReciveListeners.get(i).onBattreyOrVersion(mCurrentDevice);
                                }

                            }
                        break;
                    case 0x04:
                        Object[] obj = (Object[]) msg.obj;
                        if (!isListenerNull && mCurrentDevice != null)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                int dis = (int) obj[3];
                                int cal = (int) obj[2];
                                mBleReciveListeners.get(i).receiveData(new WatchRealTimeResult((int) obj[1], (float) dis, cal, (int) obj[0], mCurrentDevice.deviceName));
                                // mBleReciveListeners.get(i).receiveData(new WatchHrHeartResult((Integer) obj[0], mCurrentDevice.deviceName));
                            }
                        break;
//
                    case 7:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516SleepSettingResult());
                            }
                        break;
                    case 8:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516SettingResult());
                            }
                        break;
                    case 9:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516AlarmResult());
                            }
                        break;
                    case 0x10:
                        getBattery();
                        break;
                    case 0x11:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516InAdjustModeReslut());
                            }
                        break;
                    case HandlerContans.mDevcieAlarList:

                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceAlarmListResult(mCurrentDevice.deviceName));
                            }
                        }
                        break;
                    case 0x12://同步成功
                        if (!isListenerNull) {
                            getSetting();
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                SyncCacheUtils.saveSyncDataTime(mContext);
                                SyncCacheUtils.saveStartSync(mContext);
                                mBleReciveListeners.get(i).receiveData(new WatchW516SyncResult(WatchW516SyncResult.SUCCESS));
                            }
                        }

                        break;
                    case 0x13://超时回调
                        if (!isListenerNull) {
                            Logger.myLog("连接断开 超时回调");
                            SyncProgressObservable.getInstance().hide();
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516SyncResult(WatchW516SyncResult.FAILED));
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
                    case 0x15:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516SyncResult(WatchW516SyncResult.SYNCING));
                            }
                        break;
                    case HandlerContans.mSenderW560Setting:
                        if (!isListenerNull)
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW560AlarmResult());
                            }
                        break;

                    case HandlerContans.mDevcieExecise:
                        Logger.myLog(TAG,"----mDevcieExecise");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_exercise, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;

                    case HandlerContans.mDeviceTempMeasure:
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.today_temp, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;

                    case HandlerContans.mDevcieGoalStep:

                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchGOALSTEPResult((Integer) objHr[0], mCurrentDevice.deviceName));
                            }
                        }
                        break;
                    case HandlerContans.mDevcieGoalDistance:
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchGoalDistanceResult((Integer) objHr[0], mCurrentDevice.deviceName));
                            }
                        }
                        break;
                    case HandlerContans.mDevcieGoalCalorie:
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchGoalCalorieResult((Integer) objHr[0], mCurrentDevice.deviceName));
                            }
                        }
                        break;
                    case HandlerContans.mTempSub:
                        if (!isListenerNull && mCurrentDevice != null) {
                            Object[] objHr = (Object[]) msg.obj;
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchTempSubResult((Integer) objHr[0], mCurrentDevice.getDeviceName()));
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
                    case HandlerContans.mDeviceState:
                        getState();
                        break;
                    case HandlerContans.mDevcieMeasureOxyenSuccess:
                        Logger.myLog(TAG + "mDevcieOxygenMeasureOxyenSuccess");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_oxygen, mCurrentDevice.getDeviceName()));
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
                    case HandlerContans.mDevcieMeasureHrSuccess:
                        Logger.myLog(TAG + "mDevcieOxygenMeasureonceHr");
                        if (!isListenerNull && mCurrentDevice != null) {
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new DeviceMessureDataResult(DeviceMessureData.measure_once_hr, mCurrentDevice.getDeviceName()));
                            }
                        }
                        break;

                }
            }
        };
    }

    private void setSetting() {
        //sendW526Message(2, "18664328616", "测试abc");
        /*if (true) {
            return;
        }*/
        //同步就是在我连接超过了半小时

        if (mCurrentDevice.deviceType == IDeviceType.TYPE_ROPE_S002) {

            //int year, int month, int day, int sex, int weight, int height
            //set_user(BaseManager.mYear, BaseManager.mMonth, BaseManager.mDay, BaseManager.mWeight, BaseManager.mSex, BaseManager.mWeight, (int) BaseManager.mHeight, 0);


            mGattCallBack.clearQueuryData();
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            Logger.myLog("syncRopeData" + isNull);
            if (mGattCallBack != null && s002Cmd != null) {
                mGattCallBack.addQueuryData(new DataBean(send_set_user(BaseManager.mYear, BaseManager.mMonth, BaseManager.mDay, BaseManager.mSex, BaseManager.mWeight, (int) BaseManager.mHeight, mSex == 0 ? (220 - mAge) : (226 - mAge),
                        90), 1000, false));
                mGattCallBack.addQueuryData(new DataBean(s002Cmd.setMaxHr(BaseManager.remideHr), 1000, false));
                mGattCallBack.addQueuryData(new DataBean(s002Cmd.getSumRopeData(), SETTING_SYNC_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(s002Cmd.getSumRopeDetailData(), SETTING_SYNC_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(s002Cmd.getSumRopeDetailHrAndBPMData(), SETTING_SYNC_TIMEOUT, false));
            }
            sendQueryData(isNull);

            //sendQueryData();

        } else {

            if (!SyncCacheUtils.getSyncDataTime(mContext)) {
                //开始同步
                //getSetting();
                isFirst = true;
                sendHandler.sendEmptyMessageDelayed(0x05, 2000);
            } else {
                isFirst = true;

                getSetting();
            }
        }
    }


    public void findWatchFace() {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        mGattCallBack.addQueuryData(new DataBean(w526Cmd.getWatchFace(), SETTING_CMD_TIMEOUT, false));
        sendQueryData(isNull);
    }

    //只有在第一次第一次启用和

    boolean isFirst = true;

    private void getSetting() {

        mGattCallBack.clearQueuryData();

        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        mGattCallBack.addQueuryData(new DataBean(w526Cmd.getW526Hr(), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(send_read_status(), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(ISportOrder.send_get_user(), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(send_get_general(), SETTING_CMD_TIMEOUT, false));
        sendQueryData(isNull);

    }


    /**
     * 使能Handler
     */
    private void initNotiHandler() {
        if (notiHandler == null) {
            notiHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case 1:
                            if (mGattCallBack != null) {
                                //通知设置超时 如果是在规定的时间内通道没有设置成功就让他断开走重连逻辑
                                notiHandler.sendEmptyMessageDelayed(0x02, 10000);
                                mGattCallBack.enableNotification();
                            }
                            break;
                        case 0x02:
                            Logger.myLog("notiHandler 0x02: disconnect");
                            disconnect(true);
                            break;
//                        case 2:
//                            sendTime();
//                            break;
//                        case 3:
//                            Logger.myLog("sendHeight == " + msg.arg1);
//                            sendHeight(msg.arg1);
//                            break;
//                        case 4:
//                            Logger.myLog("sendWeight == " + msg.arg1);
//                            sendWeight(msg.arg1);
//                            break;
//                        case 5:
//                            Logger.myLog("sendAge == " + msg.arg1);
//                            sendAge(msg.arg1);
//                            break;
//                        case 6:
//                            Logger.myLog("sendSex == " + msg.arg1);
//                            sendSex(msg.arg1);
//                            break;
//                        case 7:
//                            Logger.myLog("sendTargetStep == " + msg.arg1);
//                            sendTargetStep(msg.arg1);
//                            break;
//                        case 8:
//                            Logger.myLog("sendScreenTime == " + msg.arg1);
//                            sendScreenTime(msg.arg1);
//                            break;
//                        case 9:
//                            Logger.myLog("sendHandScreen == " + (boolean) msg.obj);
//                            sendHandScreen((boolean) msg.obj);
//                            break;
                    }
                }
            };
        }
    }

    /**
     * 同步Handler
     */
    private void initSyncHandler() {
        if (syncHandler == null) {
            syncHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case 0x00:
                            syncHandler.removeCallbacksAndMessages(null);
                            // TODO: 2019/3/4 超时的回调，接着请求
                            mHandler.sendEmptyMessage(0x13);
                            Logger.myLog(TAG,"同步超时，第" + mCurrentDay);
                            /*if (mFailedTimes < 3) {
                                mFailedTimes++;
                                get_daily_record(mCurrentDay, false);
                            } else {
                                //超时回调
                                mHandler.sendEmptyMessage(0x13);
                            }*/
                            break;
                        case 0x01:
                            syncHandler.removeMessages(0x00);
                            // TODO: 2019/3/4 失败的回调，接着请求
                            Logger.myLog("失败的回调,接着同步" + mCurrentDay);
                            if (mFailedTimes < 3) {
                                mFailedTimes++;
                                get_daily_record(mCurrentDay, false);
                            } else {
                                //失败回调
                                mHandler.sendEmptyMessage(0x13);
                            }
                            break;
                        case 2:
                            syncHandler.removeMessages(0x00);
                            // TODO: 2019/3/4 成功的回调，接着请求
                            if (mCurrentDay == 0) {
                                //同步成功
                                SyncProgressObservable.getInstance().hide();
                                Logger.myLog(TAG,"同步成功");
                                mHandler.sendEmptyMessage(0x12);
                            } else {
                                //接着同步
                                Logger.myLog("成功的回调,接着同步" + mCurrentDay);
                                mCurrentDay--;
                                get_daily_record(mCurrentDay, true);
                            }
                            break;
                    }
                }
            };
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
                        case HandlerContans.mDeviceStateFail:
                            // getState();
                            break;
                        case 0x13:
                            Logger.myLog("连接断开 超时回调");
                            SyncProgressObservable.getInstance().hide();
                            for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                mBleReciveListeners.get(i).receiveData(new WatchW516SyncResult(WatchW516SyncResult.FAILED));
                            }
                            break;
                        case HandlerContans.mNotiyFail:
                            if (mGattCallBack != null) {
                                mGattCallBack.gattCloseFaileNotity();
                            }
                            //再去连接一次

                            break;
                        //发送指令超时了就发送下一条
                        case HandlerContans.mReceiveDataTimeout:
                            sendQueuryData();
                            break;

                    }
                }
            };
        }
    }

    /**
     * 发送指令Handler
     */
    private void initSendHandler() {
        if (sendHandler == null) {
            sendHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case 1:
                            /**
                             * 久坐提醒
                             */
                            if (mCurrentDevice == null) {
                                return;
                            }
                            Watch_W516_SedentaryModel watch_w516_watch_w516_sedentaryModelyDeviceId = Watch_W516_SedentaryModelAction.findWatch_W516_Watch_W516_SedentaryModelyDeviceId(mCurrentDevice.deviceName, BaseManager.mUserId);
                            if (watch_w516_watch_w516_sedentaryModelyDeviceId == null) {
                                //默認設置為关闭，1分钟，需要设置给设备
                                Watch_W516_SedentaryModel watch_w516_sedentaryModel = new Watch_W516_SedentaryModel();
                                watch_w516_sedentaryModel.setDeviceId(mCurrentDevice.deviceName);
                                watch_w516_sedentaryModel.setUserId(mUserId);
                                watch_w516_sedentaryModel.setLongSitTimeLong(Constants.W516defPerid);
                                watch_w516_sedentaryModel.setLongSitStartTime(Constants.defStartTime);
                                watch_w516_sedentaryModel.setLongSitEndTime(Constants.defEndTime);
                                BleAction.getWatch_W516_SedentaryModelDao().insertOrReplace(watch_w516_sedentaryModel);
                                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_SEDENTARY_TIME, Constants.W516defPerid, Constants.defStarHour, Constants.defStartMin, Constants.defEndHour, Constants.defEndMin);
                            } else {
                                String longSitStartTime = watch_w516_watch_w516_sedentaryModelyDeviceId.getLongSitStartTime();
                                String longSitEndTime = watch_w516_watch_w516_sedentaryModelyDeviceId.getLongSitEndTime();
                                String[] start;
                                if (longSitStartTime != null) {
                                    start = longSitStartTime.split(":");
                                } else {
                                    start = new String[]{Constants.defStrStarHour, Constants.defStrStartMin};
                                }
                                String[] end;
                                if (longSitEndTime != null) {
                                    end = longSitEndTime.split(":");
                                } else {
                                    end = new String[]{Constants.defStrEndHour, Constants.defStrEndMin};
                                }
                                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_SEDENTARY_TIME, watch_w516_watch_w516_sedentaryModelyDeviceId.getLongSitTimeLong(), Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(end[0]), Integer.parseInt(end[0]));
                            }
                            break;
                        case 2:
                            /**
                             * 24H心率开关
                             */
                            break;
                        case 3:
                            /**
                             * 闹钟
                             */
                            Watch_W516_AlarmModel watch_w516_alarmModelByDeviceId = Watch_W516_AlarmModelAction.findWatch_W516_AlarmModelByDeviceId(mCurrentDevice.deviceName, BaseManager.mUserId);
                            if (watch_w516_alarmModelByDeviceId == null) {
                                //没有闹钟默认是0，信息"起床"，时间为8:00
                                Watch_W516_AlarmModel watch_w516_alarmModel = new Watch_W516_AlarmModel();
                                watch_w516_alarmModel.setDeviceId(mCurrentDevice.deviceName);
                                watch_w516_alarmModel.setUserId(mUserId);
                                watch_w516_alarmModel.setMessageString("123");
                                watch_w516_alarmModel.setRepeatCount(0);
                                watch_w516_alarmModel.setTimeString(Constants.defStartTime);
                                BleAction.getWatch_W516_AlarmModelDao().insertOrReplace(watch_w516_alarmModel);
                                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_ALARM, false, 0, 8, 0, 0);
                            } else {
                                String[] split = watch_w516_alarmModelByDeviceId.getTimeString().split(":");
                                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_ALARM, watch_w516_alarmModelByDeviceId, watch_w516_alarmModelByDeviceId.getRepeatCount(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
                            }
                            break;
                        case HandlerContans.mTakePhoto:
                            TakePhotObservable.getInstance().takePhoto(true);
                            break;
                        case 4:
                            /**
                             * 睡眠/勿扰设置
                             */
                            Watch_W516_SleepAndNoDisturbModel watch_w516_sleepAndNoDisturbModelyDeviceId = Watch_W516_SleepAndNoDisturbModelAction.findWatch_W516_SleepAndNoDisturbModelyDeviceId(mUserId, mCurrentDevice.deviceName);
                            if (watch_w516_sleepAndNoDisturbModelyDeviceId == null) {
                                //如果是没有设置过，全部设置为关闭
                                Watch_W516_SleepAndNoDisturbModel watch_w516_sleepAndNoDisturbModel = new Watch_W516_SleepAndNoDisturbModel();
                                watch_w516_sleepAndNoDisturbModel.setDeviceId(mCurrentDevice.deviceName);
                                watch_w516_sleepAndNoDisturbModel.setUserId(mUserId);
                                watch_w516_sleepAndNoDisturbModel.setAutomaticSleep(true);
                                watch_w516_sleepAndNoDisturbModel.setSleepRemind(false);
                                // TODO: 2019/3/2 要查看是12小时还是24小时制
                                watch_w516_sleepAndNoDisturbModel.setSleepStartTime(Constants.defEndTime);
                                watch_w516_sleepAndNoDisturbModel.setSleepEndTime(Constants.defStartTime);
                                watch_w516_sleepAndNoDisturbModel.setOpenNoDisturb(false);
                                // TODO: 2019/3/2 要查看是12小时还是24小时制
                                watch_w516_sleepAndNoDisturbModel.setNoDisturbStartTime(Constants.defEndTime);
                                watch_w516_sleepAndNoDisturbModel.setNoDisturbEndTime(Constants.defStartTime);
                                BleAction.getWatch_W516_SleepAndNoDisturbModelDao().insertOrReplace(watch_w516_sleepAndNoDisturbModel);
                                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_SLEEP_SETTING, true, false, false,
                                        20, 0, 8, 0, 20, 0, 8, 0);
                            } else {
                                String sleepStartTime = watch_w516_sleepAndNoDisturbModelyDeviceId.getSleepStartTime();
                                String[] splitSleepStartTime;
                                if (sleepStartTime != null) {
                                    splitSleepStartTime = sleepStartTime.split(":");
                                } else {
                                    splitSleepStartTime = new String[]{"20", "0"};
                                }

                                String sleepEndTime = watch_w516_sleepAndNoDisturbModelyDeviceId.getSleepEndTime();
                                String[] splitSleepEndTime;
                                if (sleepEndTime != null) {
                                    splitSleepEndTime = sleepEndTime.split(":");
                                } else {
                                    splitSleepEndTime = new String[]{"8", "0"};
                                }
                                String noDisturbStartTime = watch_w516_sleepAndNoDisturbModelyDeviceId.getNoDisturbStartTime();
                                String[] splitNoDisturbStartTime;
                                if (noDisturbStartTime != null) {
                                    splitNoDisturbStartTime = noDisturbStartTime.split(":");
                                } else {
                                    splitNoDisturbStartTime = new String[]{"20", "0"};
                                }
                                String noDisturbEndTime = watch_w516_sleepAndNoDisturbModelyDeviceId.getNoDisturbEndTime();
                                String[] splitNoDisturbEndTime;
                                if (noDisturbEndTime != null) {
                                    splitNoDisturbEndTime = noDisturbEndTime.split(":");
                                } else {
                                    splitNoDisturbEndTime = new String[]{"8", "0"};
                                }
                                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_SLEEP_SETTING, !watch_w516_sleepAndNoDisturbModelyDeviceId.getAutomaticSleep(), watch_w516_sleepAndNoDisturbModelyDeviceId.getSleepRemind(), watch_w516_sleepAndNoDisturbModelyDeviceId.getOpenNoDisturb(),
                                        Integer.parseInt(splitSleepStartTime[0]), Integer.parseInt(splitSleepStartTime[1]), Integer.parseInt(splitSleepEndTime[0]), Integer.parseInt(splitSleepEndTime[1]), Integer.parseInt(splitNoDisturbStartTime[0]), Integer.parseInt(splitNoDisturbStartTime[1]), Integer.parseInt(splitNoDisturbEndTime[0]), Integer.parseInt(splitNoDisturbEndTime[1]));
                            }
                            break;
                        case 0x05:
                            //开始同步发送一个同步的信息到上层
                            int[] inf = (int[]) msg.obj;
                            final boolean isListenerNull = mBleReciveListeners == null;
                            if (!isListenerNull) {
                                for (int i = 0; i < mBleReciveListeners.size(); i++) {
                                    mBleReciveListeners.get(i).receiveData(new BraceletW311SyncStart());
                                }
                            }
                            // TODO: 2019/3/4 同步数据的逻辑,同步除今天以外的前推的7天,当天也同步，
                            // 同步成功和失败的回调,每个日期同步都要有同步超时，可重试的次数为3次
                            //需要获取当前同步到的天数
                            String todayYYYYMMDD = TimeUtils.getTodayYYYYMMDD();
                            String string = BleSPUtils.getString(mContext, BleSPUtils.WATCH_LAST_SYNCTIME, todayYYYYMMDD);
                            //比较上次同步的日期是否距离今天大于7天,如果，大于7天，从7(最开始)开始请求;如果不是则从间隔的天数开始请求;
                            //每次请求完毕,存储同步完成的dateStr,作为下次请求的开始
                            //

                            Logger.myLog(TAG,"----------同步天数="+string);

                            mHandler.sendEmptyMessage(0x15);
                            if (TimeUtils.isToday(string)) {
                                get_daily_record(0, true);
                                SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(1, 1), false);
                            } else {
                                int gapCount = TimeUtils.getGapCount(string, todayYYYYMMDD, "yyyy-MM-dd");
                                if (gapCount > 7) {
                                    SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(8, 1), false);
                                    get_daily_record(7, true);
                                } else {
                                    SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(gapCount + 1, 1), false);
                                    get_daily_record(gapCount, true);
                                }
                            }
                            break;
                        case 0x06:
                            //个人信息
                            // TODO: 2019/3/4 同步数据的逻辑,同步除今天以外的前推的7天,当天也同步，
                            // 同步成功和失败的回调,每个日期同步都要有同步超时，可重试的次数为3次
                            set_user(mYear, mMonth, mDay, mSex, mWeight, (int) mHeight, 0, 0);
                            break;
                        case HandlerContans.mSenderGetGeneral:
                            getGeneral();
                            break;
                        case HandlerContans.mSenderGet24HrSwitch:
                            get24HrSwitch();
                            break;
                        case HandlerContans.mSenderGetSedentary:
                            getSedentaryTime();
                            break;
                        case HandlerContans.mSenderGetAlarmList:
                            getAlarm(0);
                            break;
                        case HandlerContans.mSenderGetAlarmList2:
                            getAlarm(1);
                            break;
                        case HandlerContans.mSenderGetAlarmList3:
                            getAlarm(2);
                            break;
                        case HandlerContans.mSenderGetBackLight:
                            W526getBackLight();
                            break;
                        case HandlerContans.mSenderGetWatchFace:
                            w526GetWatchFace();
                            break;
                        case HandlerContans.mSenderGetLiftWrist:
                            getW526LiftWrist();
                            break;
                        case HandlerContans.mSenderGetDisturb:
                            getW526Disturb();
                            break;
                        case HandlerContans.mSenderSetWeather:    //设置天气
                            if (Constants.wristbandWeather != null) {

                                if(mGattCallBack.mBaseDevice != null){
                                    if(mGattCallBack.mBaseDevice.getDeviceType() == IDeviceType.TYPE_WATCH_W560){

                                        List<WristbandForecast> tmpW =  Constants.wristbandWeather.getForecast15Days();
                                        List<WristbandForecast> w560BWeather = new ArrayList<>();
                                        for(WristbandForecast wristbandForecast : tmpW){

                                            String weatherId = wristbandForecast.getWeatherId();
                                            int weatherTag =  Constants.W81WeatherConfig.get(weatherId);
                                            //560的云
                                            if(weatherTag == 0 || weatherTag == 1 || weatherTag == 2 || weatherTag == 7){
                                                wristbandForecast.setWeatherId("FOGGY"); //1
                                            }

                                            if(weatherTag == 5){  //晴 0
                                                wristbandForecast.setWeatherId("CLOUDY");
                                            }

                                            if(weatherTag == 3 || weatherTag == 4){  //雨 3
                                                wristbandForecast.setWeatherId("RAINY");
                                            }

                                            if(weatherTag == 2){ //风 2
                                                wristbandForecast.setWeatherId("OVERCAST");
                                            }

                                            w560BWeather.add(wristbandForecast);
                                        }
                                        setCmdW526Weather(Constants.wristbandWeather.getCondition(), w560BWeather);
                                        return;
                                    }
                                }


                                setCmdW526Weather(Constants.wristbandWeather.getCondition(), Constants.wristbandWeather.getForecast15Days());
                            }
                            break;
                        case HandlerContans.mSenderPractise:
                            getW526OneRecodePractiseData(0);
                            break;
                        case HandlerContans.mSenderSetting:
                            sendQueuryData();
                            break;
                    }
                }
            };
        }
    }

    /**
     * 注册监听
     *
     * @param listener
     */
    public void setBTListener(BluetoothListener listener) {
        bluetoothListener = listener;

    }

    public void setBTSettingSuccessListener(BluetoothSettingSuccessListener listener) {
        bluetoothSuceessListener = listener;
    }

    /**
     * clear all
     */
    public void clearAll() {
        if (syncHandler != null) {
            syncHandler.removeCallbacksAndMessages(null);
        }
        if (notiHandler != null) {
            notiHandler.removeCallbacksAndMessages(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (sendHandler != null) {
            sendHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 使能
     */
    public void enableNotification() {
        notiHandler.sendEmptyMessageDelayed(0x01, 500);
    }

    public BluetoothSettingSuccessListener successListener = new BluetoothSettingSuccessListener() {

        @Override
        public void successTempSub(int isSuccess) {
            Message message = new Message();
            message.arg1 = isSuccess;
            message.what = HandlerContans.mGetTempSubSuccess;
            suceessHandler.sendMessage(message);

        }

        @Override
        public void sendOnceHrSuccess(int isSuccess) {
            Message message = new Message();
            message.arg1 = isSuccess;
            message.what = HandlerContans.mGetOnceHrSuccess;
            suceessHandler.sendMessage(message);
        }
    };
    private final BluetoothListener btListener = new BluetoothListener() {
        @Override
        public void not_connected(int iWhy) {
            Logger.myLog(TAG,"未连接成功");
        }

        @Override
        public void connecting() {
            //正在连接中
            mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetting);
        }

        @Override
        public void connected() throws IOException {
            Logger.myLog("连接成功,获取版本号");
            notiHandler.removeMessages(0x02);
            clearAll();
            //清空指令集合
            timeOutHandler.removeMessages(HandlerContans.mReceiveDataTimeout);
            timeOutHandler.removeMessages(HandlerContans.mNotiyFail);
            mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetSuccessState);
            //连接成功读取版本号 3000
            mHandler.sendEmptyMessageDelayed(0x10, 3000);


            //mHandler.sendEmptyMessageDelayed(HandlerContans.mDeviceState, 4000);


        }

        @Override
        public void disconnected() {
            Logger.myLog("watch516 连接断开");
            SyncCacheUtils.setUnBindState(false);
            if (syncHandler.hasMessages(0x00)) {
                syncHandler.removeMessages(0x00);
                //同步失败
                Logger.myLog("连接断开 同步中");
                timeOutHandler.sendEmptyMessage(0x13);
            }
            clearAll();
            timeOutHandler.removeMessages(HandlerContans.mNotiyFail);
            mHandler.sendEmptyMessage(HandlerContans.mHandlerConnetFailState);


        }

        @Override
        public void not_discoverServices() {
            Logger.myLog("获取服务失败");
            timeOutHandler.removeMessages(HandlerContans.mNotiyFail);
            timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mNotiyFail, 500);
            // TODO: 2018/10/9 服务未找到的处理，找服务次数调整为两次
        }

        @Override
        public void servicesDiscovered() {
            Logger.myLog("连接成功去使能-非锁定");
            // TODO: 2018/10/9 使能失败的处理
            enableNotification();
            timeOutHandler.removeMessages(HandlerContans.mNotiyFail);
            timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mNotiyFail, 10000);
        }

        @Override
        public void enableUnLockSuccess() {

        }

        @Override
        public void unLockData(float weight) {
        }

        @Override
        public void lockData(float weight, float r) {
        }

        @Override
        public void onGetBattery(int battery) {
            Logger.myLog("onGetBattery == " + battery);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putSerializable("onGetBattery", new WatchBatteryResult(battery));
            message.setData(bundle);
            message.what = 0x02;
            mHandler.sendMessage(message);
        }

        @Override
        public void successAlam(int index) {
            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieAlarList, 500);
        }

        @Override
        public void successSleepData() {

        }

        @Override
        public void onGetDeviceVersion(String version) {
            timeOutHandler.removeMessages(HandlerContans.mDeviceStateFail);
            Logger.myLog("getDeviceVersionxxxxxxxxx" + version+"\n"+ StringUtils.substringBefore(version,"�"));
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putSerializable("DeviceVersion", new WatchVersionResult(version));
            message.setData(bundle);
            message.what = HandlerContans.mHandlerbattery;
            mHandler.sendMessage(message);

        }

        @Override
        public void realTimeData(int stepNum, float stepKm, int cal) {
            Logger.myLog("realTimeData stepNum == " + stepNum + " stepKm == " + stepKm + " cal == " + cal);
            Message message = new Message();
            Object[] objects = new Object[3];
            objects[0] = stepNum;
            objects[1] = stepKm;
            objects[2] = cal;

            Watch_SmartBand_SportDataModel watch_smartBand_sportDataModelRealTime = new
                    Watch_SmartBand_SportDataModel();
            watch_smartBand_sportDataModelRealTime.setTotalSteps((int) objects[0]);
            watch_smartBand_sportDataModelRealTime.setTotalDistance((float) objects[1]);
            watch_smartBand_sportDataModelRealTime.setTotalCalories((int) objects[2]);
            watch_smartBand_sportDataModelRealTime.setDateStr(TimeUtils.getTodayYYYYMMDD());
            watch_smartBand_sportDataModelRealTime.setDeviceId(mCurrentDevice.deviceName);
            watch_smartBand_sportDataModelRealTime.setUserId(mUserId);
            watch_smartBand_sportDataModelRealTime.setTimestamp(System.currentTimeMillis());
            ParseData.saveWatchSmartBandSportDataModel(watch_smartBand_sportDataModelRealTime);
            message.what = 0x04;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 100);
        }

        @Override
        public void sportData(int stepNum, float stepKm, int cal, String dateString, int sportTime, int maxHR, int
                minHR) {
            Message message = new Message();
            Object[] objects = new Object[7];
            objects[0] = stepNum;
            objects[1] = stepKm;
            objects[2] = cal;
            objects[3] = dateString;
            objects[4] = sportTime;
            objects[5] = maxHR;
            objects[6] = minHR;
            message.what = 0x05;
            message.obj = objects;
            mHandler.sendMessage(message);

        }

        @Override
        public void onSetTarget(int target) {
        }

        @Override
        public void onSetHandScreen(boolean enable) {
        }

        @Override
        public void onInDemoModeSuccess() {
            mHandler.sendEmptyMessage(0x11);
        }

        @Override
        public void onSettingUserInfoSuccess() {

        }

        @Override
        public void onGetUserInfoSuccess() {

        }

        @Override
        public void onSyncTimeSuccess() {
            setSetting();
        }

        @Override
        public void onRealtimeStepData(int step) {
            Logger.myLog("onRealtimeStepData == " + step);
        }

        @Override
        public void onRealtimeStepData(int heartRate, int step, int cal, int dis) {
            Message message = new Message();
            Object[] objects = new Object[4];
            objects[0] = heartRate;
            objects[1] = step;
            objects[2] = cal;
            objects[3] = dis;
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
            message.what = 0x04;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 100);
        }

        @Override
        public void onRealtimeHeartRate(int heartRate) {
            Message message = new Message();
            Object[] objects = new Object[1];
            objects[0] = heartRate;
            message.what = 0x14;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 100);
        }


        @Override
        public void onSetGeneral(boolean unit, boolean language, boolean timeFormat, boolean brightScreen, boolean
                heartRateSwitch) {
            Watch_W516_SettingModel watch_w516_settingModel = new Watch_W516_SettingModel();
            watch_w516_settingModel.setDeviceId(mCurrentDevice.deviceName);
            watch_w516_settingModel.setUserId(mUserId);
            watch_w516_settingModel.setUnit(unit);
            watch_w516_settingModel.setLanguage(language);
            watch_w516_settingModel.setTimeFormat(timeFormat);
            watch_w516_settingModel.setBrightScreen(brightScreen);
            watch_w516_settingModel.setHeartRateSwitch(heartRateSwitch);
            ParseData.saveOrUpdateWatchW516Setting(watch_w516_settingModel);
            Message message = new Message();
            message.what = 0x08;
            mHandler.sendMessageDelayed(message, 100);
        }

        @Override
        public void onSetAlarm(int repeatCount, String timeString, String messageString) {
            Watch_W516_AlarmModel watch_w516_alarmModel = new Watch_W516_AlarmModel();
            watch_w516_alarmModel.setDeviceId(mCurrentDevice.deviceName);
            watch_w516_alarmModel.setUserId(mUserId);
            watch_w516_alarmModel.setMessageString("");
            watch_w516_alarmModel.setRepeatCount(repeatCount);
            watch_w516_alarmModel.setTimeString(timeString);
            ParseData.saveOrUpdateWatchW516Alarm(watch_w516_alarmModel);
            Logger.myLog("repeat =222= " + repeatCount);
            Message message = new Message();
            message.what = 0x09;
            mHandler.sendMessageDelayed(message, 100);
        }

        @Override
        public void onSetSleepAndNoDisturb(boolean automaticSleep, boolean sleepRemind, boolean openNoDisturb, String
                sleepStartTime, String sleepEndTime, String noDisturbStartTime, String noDisturbEndTime) {

            Watch_W516_SleepAndNoDisturbModel watch_w516_sleepAndNoDisturbModel = new Watch_W516_SleepAndNoDisturbModel();
            watch_w516_sleepAndNoDisturbModel.setDeviceId(mCurrentDevice.deviceName);
            watch_w516_sleepAndNoDisturbModel.setUserId(mUserId);
            watch_w516_sleepAndNoDisturbModel.setAutomaticSleep(automaticSleep);
            watch_w516_sleepAndNoDisturbModel.setSleepRemind(sleepRemind);
            watch_w516_sleepAndNoDisturbModel.setOpenNoDisturb(openNoDisturb);
            watch_w516_sleepAndNoDisturbModel.setSleepStartTime(sleepStartTime);
            watch_w516_sleepAndNoDisturbModel.setSleepEndTime(sleepEndTime);
            watch_w516_sleepAndNoDisturbModel.setNoDisturbStartTime(noDisturbStartTime);
            watch_w516_sleepAndNoDisturbModel.setNoDisturbEndTime(noDisturbEndTime);
            ParseData.saveOrUpdateWatchW516SleepAndNoDisturb(watch_w516_sleepAndNoDisturbModel);
            Logger.myLog("watch_w516_sleepAndNoDisturbModel== " + watch_w516_sleepAndNoDisturbModel.toString());
            Message message = new Message();
            message.what = 0x07;
            mHandler.sendMessageDelayed(message, 100);
        }

        @Override
        public void onSetSedentary(int time, String startTime, String endTime) {
            Watch_W516_SedentaryModel watch_w516_sedentaryModel = new Watch_W516_SedentaryModel();
            watch_w516_sedentaryModel.setDeviceId(mCurrentDevice.deviceName);
            watch_w516_sedentaryModel.setUserId(mUserId);
            watch_w516_sedentaryModel.setLongSitTimeLong(time);
            watch_w516_sedentaryModel.setLongSitStartTime(startTime);
            watch_w516_sedentaryModel.setLongSitEndTime(endTime);
            if (time == 1) {
                watch_w516_sedentaryModel.setIsEnable(false);
            } else {
                watch_w516_sedentaryModel.setIsEnable(true);
            }
            ParseData.saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModel);
            Message message = new Message();
            message.what = 0x07;
            mHandler.sendMessageDelayed(message, 100);
        }

        @Override
        public void onSyncSuccess() {
            syncHandler.sendEmptyMessage(0x02);
        }

        @Override
        public void onSyncSuccessPractiseData(int index) {
            Logger.myLog("onSyncSuccessPractiseData:" + index);
            if (index == -1) {
                mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieExecise, 1000);
            } else {
             mGattCallBack.addQueuryData(new DataBean(w526Cmd.getExerciseData(index + 1), PRACTISE_CMD_TIMEOUT, false));
                if (btListener != null)
                    btListener.onGetSettingSuccess();
            }
        }

        @Override
        public void onStartSyncPractiseData(int index) {
            //查询第一条的锻炼数据
            Logger.myLog("onStartSyncPractiseData");
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.getExerciseData(0), PRACTISE_CMD_TIMEOUT, false));
            sendQueryData(isNull);

        }

        @Override
        public void onStartSyncWheatherData() {
            sendHandler.sendEmptyMessageDelayed(HandlerContans.mSenderSetWeather, 100);
        }

        @Override
        public void onSyncRopeData() {
            syncRopeData();
        }

        @Override
        public void onTempData(int temp) {
            if (mCurrentDevice != null) {
                DeviceDataSave.saveTempData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), temp, System.currentTimeMillis(), String.valueOf(0));
                mHandler.sendEmptyMessageDelayed(HandlerContans.mDeviceTempMeasure, 100);
            }
        }

        @Override
        public void onOxyData(int bloodOxygen) {
            if (bloodOxygen <= 100) {
                DeviceDataSave.saveOxyenModelData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), bloodOxygen, System.currentTimeMillis(), String.valueOf(0));
            }
            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureOxyenSuccess, 500);
        }

        @Override
        public void onBloodData(int sbp, int dbp) {
            if (mCurrentDevice != null && !(sbp > 200 || dbp > 200)) {
                DeviceDataSave.saveBloodPressureData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), sbp, dbp, System.currentTimeMillis(), String.valueOf(0));
            }
            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieBloodPressureMessureSuccess, 500);
        }

        @Override
        public void onGetSettingSuccess(int type, int index) {

        }

        @Override
        public void onGetSettingSuccess() {
            CmdProgressObservable.getInstance().hide();
            timeOutHandler.removeMessages(HandlerContans.mReceiveDataTimeout);
            sendHandler.sendEmptyMessageDelayed(HandlerContans.mSenderSetting, 50);
        }

        @Override
        public void onW560AlarmSettingSuccess() {
            mHandler.sendEmptyMessage(HandlerContans.mSenderW560Setting);
        }

        @Override
        public void clearSyncCmd() {
            mGattCallBack.clearQueuryData();
        }

        @Override
        public void takePhoto() {
            sendHandler.sendEmptyMessage(HandlerContans.mTakePhoto);
        }

        @Override
        public void onsetGeneral(byte[] bytes) {
            Logger.myLog("onsetGeneral" + isFirst);
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            if (isFirst) {
                byte[] booleanArrayG = Utils.getBooleanArray(bytes[3]);
                boolean is24Heart = (Utils.byte2Int(booleanArrayG[3]) == 0 ? true : false);
                byte[] booleanArrayMessag = Utils.getBooleanArray(bytes[4]);
                //1.保存是否开启24小时心率
                //2.保存来电消息的设置
                boolean isCall = (Utils.byte2Int(booleanArrayMessag[booleanArrayMessag.length - 1]) == 0 ? false : true);
                boolean isMessge = (Utils.byte2Int(booleanArrayMessag[booleanArrayMessag.length - 2]) == 0 ? false : true);
                // set_general(is24Heart, isCall, isMessge);
                boolean is24H = DateFormat.is24HourFormat(mContext);
                mGattCallBack.addQueuryData(new DataBean(send_set_general(is24Heart, isCall, isMessge, is24H), SETTING_CMD_TIMEOUT, false));
                isFirst = false;
                if (!SyncCacheUtils.getSetting(mContext)) {
                    SyncCacheUtils.saveSetting(mContext);
                    Logger.myLog("BaseManager.mYear:" + BaseManager.mYear + "BaseManager.mMonth:" + BaseManager.mMonth + "BaseManager.mDay:" + BaseManager.mDay + " BaseManager.mSex,:" + BaseManager.mSex + "BaseManager.mWeight:" + BaseManager.mWeight + "(int) BaseManager.mHeight:" + (int) BaseManager.mHeight);

                    mGattCallBack.addQueuryData(new DataBean(send_set_user(BaseManager.mYear, BaseManager.mMonth, BaseManager.mDay, BaseManager.mSex, BaseManager.mWeight, (int) BaseManager.mHeight, 22, 22), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(w526Cmd.getTargetStep(), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(send_get_sedentary_time(), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(w526Cmd.getWatchFace(), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(w526Cmd.getbackLightAndScreenleve(), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(w526Cmd.getW526Screen(), SETTING_CMD_TIMEOUT, false));
                    //mGattCallBack.addQueuryData(new DataBean(w526Cmd.getW526Hr(), SETTING_CMD_TIMEOUT));
                    mGattCallBack.addQueuryData(new DataBean(send_get_alarm(0), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(send_get_alarm(1), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(send_get_alarm(2), SETTING_CMD_TIMEOUT, false));
                    mGattCallBack.addQueuryData(new DataBean(w526Cmd.getNoDisturb(), SETTING_CMD_TIMEOUT, false));
                    //天气设置需要稍后在加
                    if (Constants.wristbandWeather != null) {
                        // setCmdW526Weather(Constants.wristbandWeather.getCondition(), Constants.wristbandWeather.getForecast15Days());

                        WristbandData wristbandData = Constants.wristbandWeather.getCondition();
                        List<WristbandForecast> list = Constants.wristbandWeather.getForecast15Days();
                        int todayWeather = 0;
                        int currentTemp = 0;
                        int todayhightTemp = 0;
                        int todaylowTemp = 0;
                        int AqiValue = 0;
                        if (Constants.W81WeatherConfig.containsKey(wristbandData.getWeatherId())) {
                            todayWeather = Constants.W81WeatherConfig.get(wristbandData.getWeatherId());
                        } else {
                            todayWeather = CRPWeatherId.CLOUDY;
                        }
                        currentTemp = TextUtils.isEmpty(wristbandData.getTemp()) ? 0 : Integer.parseInt(wristbandData.getTemp());
                        if (list != null && list.size() > 0) {
                            int len = list.size();
                            if (len > 6) {
                                len = 6;
                            }
                            for (int i = 0; i < len; i++) {


                                WristbandForecast forecast1 = list.get(i);

                                Logger.myLog("list.size()" + list.size() + "forecast1:" + forecast1.getWeatherId());
                                if (i != 0) {
                                    if (Constants.W81WeatherConfig.containsKey(forecast1.getWeatherId())) {
                                        todayWeather = Constants.W81WeatherConfig.get(forecast1.getWeatherId());
                                    } else {
                                        todayWeather = CRPWeatherId.CLOUDY;
                                    }
                                    //  todayWeather = Constants.W81WeatherConfig.get(wristbandData.getWeatherId());
                                    // todayWeather = i+1;
                                }
                                todayhightTemp = TextUtils.isEmpty(forecast1.getHighTemperature()) ? 0 : Integer.parseInt(forecast1.getHighTemperature());
                                todaylowTemp = TextUtils.isEmpty(forecast1.getLowTemperature()) ? 0 : Integer.parseInt(forecast1.getLowTemperature());
                                AqiValue = TextUtils.isEmpty(forecast1.getAqiValue()) ? 0 : Integer.parseInt(forecast1.getAqiValue());
                                // currentTemp = -1;
                                //todaylowTemp = -2;
                                //todayhightTemp = -85;
                                mGattCallBack.addQueuryData(new DataBean(w526Cmd.setWeather(i + 2, AqiValue, currentTemp, todayhightTemp, todaylowTemp, todayWeather), SETTING_CMD_TIMEOUT, false));
                            }
                        }
                    }
                }
                //锻炼数据每次都要更新
                Logger.myLog("getsettings:" + mGattCallBack.getQueuryLenth());

                isFirst = false;
            }
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.getExerciseData(0), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);

        }

        @Override
        public void onSuccessTargetStep(int step) {
            //  Logger.myLog(TAG + "onGoalStep: step" + step);
            Message message = new Message();
            Object[] objects = new Object[1];
            objects[0] = step;
            message.what = HandlerContans.mDevcieGoalStep;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 500);
        }

        @Override
        public void onSuccessTargetDistance(int distance) {
            Message message = new Message();
            Object[] objects = new Object[1];
            objects[0] = distance;
            message.what = HandlerContans.mDevcieGoalDistance;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 500);
        }

        @Override
        public void onSuccessTargetCalorie(int calorie) {
            Message message = new Message();
            Object[] objects = new Object[1];
            objects[0] = calorie;
            message.what = HandlerContans.mDevcieGoalCalorie;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 500);
        }

        @Override
        public void onSuccessWatchFace(int watchIndex) {
            Message message = new Message();
            Object[] objects = new Object[1];
            objects[0] = watchIndex;
            Logger.myLog("DEVICE_WATCH_FACE watchIndex" + watchIndex);
            message.what = HandlerContans.mWatchWatchFace;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 50);
        }

        @Override
        public void onSuccessTempSub(int value) {

            Logger.myLog("onSuccessTempSub" + value);
            Message message = new Message();
            Object[] objects = new Object[1];
            objects[0] = value;
            message.what = HandlerContans.mTempSub;
            message.obj = objects;
            mHandler.sendMessageDelayed(message, 50);
        }

        @Override
        public void onSuccessOneHrData(int value) {
            if (value >= 30 && value <= 240) {
                DeviceDataSave.saveOneceHrData(mCurrentDevice.deviceName, String.valueOf(BaseManager.mUserId), value, System.currentTimeMillis(), String.valueOf(0));
            }
            mHandler.sendEmptyMessageDelayed(HandlerContans.mDevcieMeasureHrSuccess, 500);
        }


        @Override
        public void onSyncError() {
            syncHandler.sendEmptyMessage(0x01);
        }


        @Override
        public void onSetScreenTime(int time) {

        }
    };

    //****************************************指令部分*************************************************//

    public void read_status() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_read_status());
        }
    }

    /*public void get24HrSwitch() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.getW526Hr());
        }
    }*/

    public void open24HrSwitch(boolean open24HeartRate) {
       /* if (mCurrentDevice != null) {
            Watch_W516_SettingModel w516SettingModelByDeviceId = Watch_W516_SettingModelAction.findW516SettingModelByDeviceId(mCurrentDevice.getDeviceName(), BaseManager.mUserId);
            w516SettingModelByDeviceId.setHeartRateSwitch(open24HeartRate);
            //更改24小时开启的开关
            ParseData.saveOrUpdateWatchW516Setting(w516SettingModelByDeviceId);
        }*/
        if (mGattCallBack != null && w526Cmd != null) {
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.setW526Hr(open24HeartRate), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
        }
    }


    public void set_general(boolean open24HeartRate, boolean isCall, boolean isMessage) {
        if (mGattCallBack != null) {
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            boolean is24H = DateFormat.is24HourFormat(mContext);
            mGattCallBack.addQueuryData(new DataBean(send_set_general(open24HeartRate, isCall, isMessage, is24H), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);

        }
    }

    public void set_general(boolean open24HeartRate, boolean isHeart) {

        boolean isCall = false, isMessage = false;
        Watch_W516_SettingModel w516SettingModelByDeviceId = Watch_W516_SettingModelAction.findW516SettingModelByDeviceId(mCurrentDevice.getDeviceName(), BaseManager.mUserId);
        if (isHeart) {
            w516SettingModelByDeviceId.setHeartRateSwitch(open24HeartRate);
            //更改24小时开启的开关
            ParseData.saveOrUpdateWatchW516Setting(w516SettingModelByDeviceId);
            if (w516SettingModelByDeviceId != null) {
                open24HeartRate = w516SettingModelByDeviceId.getHeartRateSwitch();
            }
        }
        Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(mCurrentDevice.getDeviceName(), BaseManager.mUserId);
        if (watch_w516_notifyModelByDeviceId != null) {
            isCall = watch_w516_notifyModelByDeviceId.getCallSwitch();
            isMessage = watch_w516_notifyModelByDeviceId.getMsgSwitch();
        }
        // Logger.myLog("open24HeartRate:" + open24HeartRate + "isCall：" + isCall + "isMessage," + isMessage + "mGattCallBack.getQueuryLenth():" + mGattCallBack.getQueuryLenth());
        if (mGattCallBack != null) {
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            boolean is24H = DateFormat.is24HourFormat(mContext);
            mGattCallBack.addQueuryData(new DataBean(send_set_general(open24HeartRate, isCall, isMessage, is24H), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
        }
    }

    public void get_general() {
        if (mGattCallBack != null) {
            Logger.myLog("get_general0----------");
            mGattCallBack.writeTXCharacteristicItem(send_get_general());
        }
    }

    public void set_user(int year, int month, int day, int sex, int weight, int height, int maxHeartRate, int
            minHeartRate) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_user(year, month, day, sex, weight, height, mSex == 0 ? (220 - mAge) : (226 - mAge),
                    90));
        }
    }

    public void get_user() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_user());
        }
    }

    public void set_calender() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_syncTime());
        }
    }

    public void get_calender() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_calender());
        }
    }

    public void set_alarm(boolean enable, int day, int hour, int min, int index) {


        if (day == 128) {
            day = 0;
        }
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        mGattCallBack.addQueuryData(new DataBean(send_set_w526_alarm(enable, day, hour, min, index), SETTING_CMD_TIMEOUT, true));
        sendQueryData(isNull);

      /*  if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_w526_alarm(day, hour, min, index));
        }*/
    }

    public void set_w560_alarm(boolean enable, int day, int hour, int min, int index, String name) {

        if (day == 0) {
            day = 128;
        }

        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_w560_alarm(enable, day, hour, min, index, name));
        }
    }

    public void add_w560_alarm(int day, int hour, int min, String name) {

        if (day == 0) {
            day = 128;
        }

        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_add_w560_alarm(day, hour, min, name));
        }
    }

    public void delete_w560_alarm(int index) {

        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_delete_w560_alarm(index));
        }
    }

    public void switch_mode(boolean inMode) {
        if (mGattCallBack != null) {
//            mGattCallBack.writeTXCharacteristicItem(send_switchAdjust(inMode));
            mGattCallBack.switchAdjust(inMode);
        }
    }

    public void adjust(int hour, int min) {
        if (mGattCallBack != null) {
            mGattCallBack.adjustHourAndMin(hour, min);
        }
    }

    public void get_alarm(int index) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_alarm(index));
        }
    }

    public void set_sleep_setting(boolean isAutoSleep, boolean hasNotif, boolean disturb, int
            testStartTimeH, int testStartTimeM, int
                                          testEndTimeH, int testEndTimeM,
                                  int
                                          disturbStartTimeH, int disturbStartTimeM, int
                                          disturbEndTimeH, int disturbEndTimeM) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_sleep_setting(isAutoSleep, hasNotif, disturb,
                    testStartTimeH, testStartTimeM,
                    testEndTimeH, testEndTimeM,

                    disturbStartTimeH, disturbStartTimeM,
                    disturbEndTimeH, disturbEndTimeM));
        }
    }

    public void get_sleep_setting() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_sleep_setting());
        }
    }

    public void set_sedentary_time(int time, int startHour, int startMin, int endHour, int endMin) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_sedentary_time(time, startHour, startMin, endHour, endMin));
        }
    }

    public void get_sedentary_time() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_sedentary_time());
        }
    }

    public void send_notification(int type) {
        // TODO: 2019/2/20 短信、通知、电话来决定发哪一个
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(type == 0 ? send_handleCall() : send_handleSms());
        }
    }


    public void send_notification_N(String pakStr) {
        // TODO: 2019/2/20 短信、通知、电话来决定发哪一个
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_handleNotification(pakStr));
        }
    }

    public void set_handle(boolean enable) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_switchAdjust(enable));
        }
    }


    public void syncUnBinde() {
        sendHandler.sendEmptyMessageDelayed(0x05, 100);
    }

    public void get_daily_record(int day, boolean tryAgain) {

        //这里去弹出需要去取多少天的


        if (mGattCallBack != null) {
            mCurrentDay = day;
            if (tryAgain)
                mFailedTimes = 0;
            if (syncHandler.hasMessages(0x00)) {
                syncHandler.removeMessages(0x00);
            }
            //开始同步超时
            syncHandler.sendEmptyMessageDelayed(0x00, SYNC_TIMEOUT);

            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
          //  mGattCallBack.addQueuryData(new DataBean(send_get_daily_record(1), SETTING_CMD_TIMEOUT, false));
            mGattCallBack.addQueuryData(new DataBean(send_get_daily_record(day), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
           // mGattCallBack.writeTXCharacteristicItem(send_get_daily_record(day));
        }
    }

    public void clear_daily_record() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_clear_daily_record());
        }
    }

    public void getTestData() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_getTestData());
        }
    }

    public void get_exercise_data() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_exercise_data());
        }
    }

    public void clear_exercise_data() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_clear_exercise_data());
        }
    }

    public void set_default() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_reset());
        }
    }

    public void set_sn_factory() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_sn_factory());
        }
    }

    public void set_sn_normalmode(int SN) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_sn_normalmode(SN));
        }
    }

    public void get_sn_data() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_sn_data());
        }
    }

    public void set_beltname() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_set_beltname());
        }
    }

    public void test_reset() {

        if (mCurrentDevice.deviceType == IDeviceType.TYPE_ROPE_S002) {
            clearS002Data();
        } else {

            if (mGattCallBack != null) {
                mGattCallBack.writeTXCharacteristicItem(send_test_reset());
            }
        }
    }


    public void setMaxHrRemid(int hr) {
        if (mGattCallBack != null) {
            if (s002Cmd != null) {
                mGattCallBack.writeTXCharacteristicItem(s002Cmd.setMaxHr(hr));
            }
        }
    }

    public void clearS002Data() {
        if (mGattCallBack != null) {
            if (s002Cmd != null) {
                mGattCallBack.writeTXCharacteristicItem(s002Cmd.clearHistoryData());
            }
        }

    }

    public void test_motorled() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_test_motorled());
        }
    }

    public void stop_test_motorled() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_stop_test_motorled());
        }
    }

    public void test_handle() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_test_handle());
        }
    }

    public void test_display() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_test_display());
        }
    }

    public void test_ohr() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_test_ohr());
        }
    }

    public void device_to_phone() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_device_to_phone());
        }
    }


    public void getGeneral() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_general());
        }
    }

    public void getAlarm(int index) {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_alarm(index));
        }
    }

    public void getSedentaryTime() {
        if (mGattCallBack != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_sedentary_time());
        }
    }

    /* public void setW526Weather(WristbandData wristbandData, List<WristbandForecast> list) {
         int todayWeather = 0;
         int currentTemp = 0;
         int todayhightTemp = 0;
         int todaylowTemp = 0;

         if (mGattCallBack != null) {
             if (w526Cmd != null) {
                 todayWeather = Constants.W520WeatherConfig.get(wristbandData.getWeatherId());
                 currentTemp = TextUtils.isEmpty(wristbandData.getTemp()) ? 0 : Integer.parseInt(wristbandData.getTemp());

                 if (list != null && list.size() > 0) {
                     WristbandForecast forecast1 = list.get(0);
                     todayhightTemp = TextUtils.isEmpty(forecast1.getHighTemperature()) ? 0 : Integer.parseInt(forecast1.getHighTemperature());
                     todaylowTemp = TextUtils.isEmpty(forecast1.getLowTemperature()) ? 0 : Integer.parseInt(forecast1.getLowTemperature());
                 }

                 mGattCallBack.writeTXCharacteristicItem(w526Cmd.setWeather(todayWeather, todaylowTemp, todayhightTemp, currentTemp));
             }
         }
     }
 */
    public void setCmdW526Weather(WristbandData wristbandData, List<WristbandForecast> list) {
        int todayWeather = 0;
        int currentTemp = 0;
        int todayhightTemp = 0;
        int todaylowTemp = 0;
        int AqiValue = 0;

        if (mGattCallBack != null && w526Cmd != null) {
            boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
            // mGattCallBack.writeTXCharacteristicItem(send_set_general(open24HeartRate, isCall, isMessage));
            if (Constants.W81WeatherConfig.containsKey(wristbandData.getWeatherId())) {
                todayWeather = Constants.W81WeatherConfig.get(wristbandData.getWeatherId());
            } else {
                todayWeather = CRPWeatherId.CLOUDY;
            }
            currentTemp = TextUtils.isEmpty(wristbandData.getTemp()) ? 0 : Integer.parseInt(wristbandData.getTemp());

            /*if (true) {
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x00, 0x48, 0x79, 0x64, 0x65, 0x72, 0x61, 0x62, 0x61, 0x64, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x01, (byte) 0xE5, 0x07, 0x04, 0x10, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x02, (byte) 0xE5, 0x07, 0x04, 0x11, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x03, (byte) 0xE5, 0x07, 0x04, 0x12, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x04, (byte) 0xE5, 0x07, 0x04, 0x13, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x05, (byte) 0xE5, 0x07, 0x04, 0x14, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x06, (byte) 0xE5, 0x07, 0x04, 0x15, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                mGattCallBack.addQueuryData(new DataBean(new byte[]{0x08, 0x12, 0x07, (byte) 0xE5, 0x07, 0x04, 0x16, 0x11, 0x2D, 0x68, 0x00, 0x21, 0x23, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, SETTING_CMD_TIMEOUT, false));
                sendQueryData(isNull);
                return;

            }*/

            if (list != null && list.size() > 0) {
                int len = list.size();
                if (len > 6) {
                    len = 6;
                }
                for (int i = 0; i < len; i++) {


                    WristbandForecast forecast1 = list.get(i);

                    Logger.myLog("list.size()" + list.size() + "forecast1:" + forecast1.getWeatherId());
                    if (i != 0) {
                        if (Constants.W81WeatherConfig.containsKey(forecast1.getWeatherId())) {
                            todayWeather = Constants.W81WeatherConfig.get(forecast1.getWeatherId());
                        } else {
                            todayWeather = CRPWeatherId.CLOUDY;
                        }
                        //  todayWeather = Constants.W81WeatherConfig.get(wristbandData.getWeatherId());
                        // todayWeather = i+1;
                    }else{
                        if(todayWeather == 0 || todayWeather == 1 || todayWeather == 2 || todayWeather == 7){
                            todayWeather = 1;
                        }

                        if(todayWeather == 5){  //晴 0
                            todayWeather = 0;
                        }

                        if(todayWeather == 3 || todayWeather == 4){  //雨 3
                            todayWeather = 3;
                        }

                        if(todayWeather == 2){ //风 2
                            todayWeather = 2;
                        }
                    }
                    todayhightTemp = TextUtils.isEmpty(forecast1.getHighTemperature()) ? 0 : Integer.parseInt(forecast1.getHighTemperature());
                    todaylowTemp = TextUtils.isEmpty(forecast1.getLowTemperature()) ? 0 : Integer.parseInt(forecast1.getLowTemperature());
                    AqiValue = TextUtils.isEmpty(forecast1.getAqiValue()) ? 0 : Integer.parseInt(forecast1.getAqiValue());
                    // currentTemp = -1;
                    //todaylowTemp = ,0x2;
                    //todayhightTemp = ,0x85;
                    mGattCallBack.addQueuryData(new DataBean(w526Cmd.setWeather(i + 2, AqiValue, currentTemp, todayhightTemp, todaylowTemp, todayWeather), SETTING_CMD_TIMEOUT, false));
                }
            }
            sendQueryData(isNull);

            // mGattCallBack.writeTXCharacteristicItem(w526Cmd.setWeather(todayWeather, todaylowTemp, todayhightTemp, currentTemp));
        }
    }


    public void W526getBackLight() {

        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.getbackLightAndScreenleve());
        }
    }

    public void W526setbacklight(int leve, int time) {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.setbacklight(leve, time));
        }
    }

    public void w526GetWatchFace() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.getWatchFace());
        }
    }

    public void w526SetWatchFace(int mode) {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.setWatchFace(mode));
        }
    }

    public void measureOxy(boolean isStart) {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.oxyenMeasure(isStart));
        }
    }

    public void measureBoold(boolean isStart) {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.bloodMeasure(isStart));
        }
    }

    public void findW526() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.findW526());
        }
    }

    //抬腕亮屏查询
    public void getW526LiftWrist() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.getW526Screen());
        }
    }

    public void setW526LiftWrist(int type, int startTimeHour, int startTimeMin, int endTimeHour, int endTimeMin) {
        boolean isOpen = false;
        if (type == 2) {//关闭
            isOpen = false;
        } else if (type == 1) { //定点开
            isOpen = true;
        } else {//全天开
            isOpen = true;
            startTimeHour = 0;
            startTimeMin = 0;
            endTimeHour = 23;
            endTimeMin = 59;
        }
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.setW526Screen(isOpen, startTimeHour, startTimeMin, endTimeHour, endTimeMin));
        }
    }


    public void sendW526Disturb(boolean enable, int startHour, int startMin, int endHour, int endMin) {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.sendnoDisturb(enable, startHour, startMin, endHour, endMin));
        }

    }

    public void getW526Disturb() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.getNoDisturb());
        }
    }


    public void sendQueryData(boolean isNull) {
        if (btListener != null && isNull)
            btListener.onGetSettingSuccess();
    }

    public void getAlarmListAll() {

        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        mGattCallBack.addQueuryData(new DataBean(send_get_alarm(0), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(send_get_alarm(1), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(send_get_alarm(2), SETTING_CMD_TIMEOUT, false));
        sendQueryData(isNull);

    }

    public void getW560AlarmList() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(send_get_w560_alarm());
        }
    }

    public void getW526OneRecodePractiseData(int number) {
        Logger.myLog(TAG,"----获取锻炼数据-----number="+number);
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.getExerciseData(number));
        }

    }


    public void sendQueuryData() {
        if (mGattCallBack != null) {
            DataBean dataBean = mGattCallBack.pollQueuryData();
            if (dataBean == null) {
                return;
            } else {
                timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mReceiveDataTimeout, dataBean.getTimeout());
                if (dataBean.isShow()) {
                    //需要点击
                    CmdProgressObservable.getInstance().show();
                }
                mGattCallBack.writeTXCharacteristicItem(dataBean.getData());
            }
        }
    }

    public void sendW526TargetStep(int step) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.setTargtStep(step), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
        }
    }

    public void sendW560TargetStep(int step) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.setW560TargetStep(step), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
        }
    }

    public void sendW560TargetDistance(int distance) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.setW560TargetDistance(distance), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
        }
    }

    public void sendW560TargetCalorie(int calorie) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.setW560TargetCalorie(calorie), SETTING_CMD_TIMEOUT, false));
            sendQueryData(isNull);
        }
    }


    public void sendW526Message(int messageType, String title, String content) {
        // title = "测试a";
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w526Cmd != null) {
            ArrayList<byte[]> messages = w526Cmd.sendMessage(messageType, title, content);


            for (int i = 0; i < messages.size(); i++) {

                byte[] tmpByte = messages.get(i);
                Logger.myLog(TAG,"tmpByte="+ Arrays.toString(tmpByte));

                mGattCallBack.addQueuryData(new DataBean(messages.get(i), SETTING_CMD_TIMEOUT, false));
            }
        }
        sendQueryData(isNull);
       /* if (btListener != null) {
            btListener.onGetSettingSuccess();
        }*/
    }

    public void isHasData() {
        SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(1, 1), false);
        mGattCallBack.clearQueuryData();
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && s002Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(s002Cmd.getRopeStartOrEnd(), 2000, false));
        }
        sendQueryData(isNull);
    }


    public void syncRopeData() {
        mGattCallBack.clearQueuryData();
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        Logger.myLog("syncRopeData" + isNull);
        if (mGattCallBack != null && s002Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(s002Cmd.getSumRopeData(), SETTING_SYNC_TIMEOUT, false));
            mGattCallBack.addQueuryData(new DataBean(s002Cmd.getSumRopeDetailData(), SETTING_SYNC_TIMEOUT, false));
            mGattCallBack.addQueuryData(new DataBean(s002Cmd.getSumRopeDetailHrAndBPMData(), SETTING_SYNC_TIMEOUT, false));
        }
        sendQueryData(isNull);
    }

    public void startMeasureTemp(boolean isStart) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w557Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w557Cmd.startTempMeasure(isStart), SETTING_CMD_TIMEOUT, false));
        }
        sendQueryData(isNull);

    }

    public void get24HrSwitch() {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        mGattCallBack.addQueuryData(new DataBean(w526Cmd.getW526Hr(), SETTING_CMD_TIMEOUT, false));
        sendQueryData(isNull);
    }


   /* public void starMeasureOxygen(boolean isStart) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w526Cmd.oxyenMeasure(isStart), SETTING_CMD_TIMEOUT, false));
        }
        sendQueryData(isNull);
    }

    public void starMeasureBlood(boolean isStart) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        if (mGattCallBack != null && w557Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(w557Cmd.bloodMeasure(isStart), SETTING_CMD_TIMEOUT, false));
        }
        sendQueryData(isNull);
    }
*/


    public void startMessureOnceHr(boolean isStart) {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.onceHrMeasure(isStart));
        }
    }

    public void getTempSub() {
        if (mGattCallBack != null && w557Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w557Cmd.getTempSub());
        }
    }

    public void setTempSub(int value) {
        Logger.myLog("setTempSub:" + value);
        if (mGattCallBack != null && w557Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w557Cmd.setTempSub(value));
        }
    }


    public void sendphoto() {
        if (mGattCallBack != null && w526Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(w526Cmd.sendPhoto());
        }
    }

    public void getState() {
        if (true) {
            return;
        }
        getStateCount = getStateCount + 1;
        //最多执行四次
        if (getStateCount == 4) {
            return;
        }
        //把所有的指令都清楚调
        timeOutHandler.removeMessages(HandlerContans.mDeviceStateFail);
        timeOutHandler.sendEmptyMessageDelayed(HandlerContans.mDeviceStateFail, 2000);
        mGattCallBack.clearQueuryData();
        mGattCallBack.writeTXCharacteristicItem(send_read_status());
    }

    //S002指令
    public void getRopeState() {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        Logger.myLog("getRopeState" + isNull);
        if (mGattCallBack != null && s002Cmd != null) {
            mGattCallBack.addQueuryData(new DataBean(s002Cmd.getRopeType(), SETTING_CMD_TIMEOUT, false));
        }
        sendQueryData(isNull);
    }

    public void setRopeState(int type, int min, int sec, int number, int pk) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        Logger.myLog("setRopeState" + isNull);
        if (mGattCallBack != null && s002Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(s002Cmd.setRopeType(type, min, sec, number, pk));
            //  mGattCallBack.addQueuryData(new DataBean(s002Cmd.setRopeType(type, min, sec, number, pk), SETTING_CMD_TIMEOUT, false));
        }
        // sendQueryData(isNull);

    }

    public void setRopeState(int type) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        Logger.myLog("setRopeState" + isNull);
        if (mGattCallBack != null && s002Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(s002Cmd.setRopeType(type));
            //mGattCallBack.addQueuryData(new DataBean(s002Cmd.setRopeType(type), SETTING_CMD_TIMEOUT, false));
        }
        //sendQueryData(isNull);
    }

    public void startOrEndRope(int type) {
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        Logger.myLog("startOrEndRope" + isNull);
        if (mGattCallBack != null && s002Cmd != null) {
            mGattCallBack.writeTXCharacteristicItem(s002Cmd.startOrStopRope(type));
            //mGattCallBack.addQueuryData(new DataBean(s002Cmd.startOrStopRope(type), SETTING_CMD_TIMEOUT, false));
        }
        // sendQueryData(isNull);
    }

    public void sendMusic(String content, String allTime, String currentTime) {
        // title = "测试a";
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;


        try {
            // 01 1F
            // 02 1F
            // 03 1F
            byte[] contentByte = content.getBytes("UTF-8");
            byte[] allTimeByte = allTime.getBytes("UTF-8");
            byte[] currentTimeByte = currentTime.getBytes("UTF-8");
            byte[] sendcontentByte = new byte[50];
            byte[] sendallTimeByte = new byte[20];
            byte[] sendcurrentTimeByte = new byte[20];
            sendcontentByte[0] = 0x01;
            sendcontentByte[1] = 0x1F;
            sendcontentByte[2] = 0x01;
            for (int i = 3; i < sendcontentByte.length; i++) {
                if (i - 3 < contentByte.length) {
                    sendcontentByte[i] = contentByte[i - 3];
                }
            }
            sendallTimeByte[0] = 0x01;
            sendallTimeByte[1] = 0x1F;
            sendallTimeByte[2] = 0x02;
            for (int i = 3; i < sendallTimeByte.length; i++) {
                if (i - 3 < allTimeByte.length) {
                    sendallTimeByte[i] = allTimeByte[i - 3];
                }
            }
            sendcurrentTimeByte[0] = 0x01;
            sendcurrentTimeByte[1] = 0x1F;
            sendcurrentTimeByte[2] = 0x03;
            for (int i = 3; i < sendcurrentTimeByte.length; i++) {
                if (i - 3 < currentTimeByte.length) {
                    sendcurrentTimeByte[i] = currentTimeByte[i - 3];
                }
            }
            mGattCallBack.addQueuryData(new DataBean(sendcontentByte, SETTING_CMD_TIMEOUT, false));
            mGattCallBack.addQueuryData(new DataBean(sendallTimeByte, SETTING_CMD_TIMEOUT, false));
            mGattCallBack.addQueuryData(new DataBean(sendcurrentTimeByte, SETTING_CMD_TIMEOUT, false));

        } catch (Exception e) {
            e.printStackTrace();
        }


       /* if (mGattCallBack != null && w526Cmd != null) {
            ArrayList<byte[]> messages = w526Cmd.sendMessage(messageType, title, content);
            for (int i = 0; i < messages.size(); i++) {
                mGattCallBack.addQueuryData(new DataBean(messages.get(i), SETTING_CMD_TIMEOUT, false));
            }
        }*/
        sendQueryData(isNull);
       /* if (btListener != null) {
            btListener.onGetSettingSuccess();
        }*/
    }



    //读取设备目标数据
    public void readDeviceGoal(){
        if(mGattCallBack != null && w557Cmd != null){}
            mGattCallBack.writeTXCharacteristicItem(w557Cmd.readW560Goal());
    }



    //音乐显示
    public void sendMusicStatus(String musicName,String musicCountTime,String musicCurrentTime){
        ArrayList<byte[]> musicByteList = w526Cmd.musicByteList(musicName,musicCountTime,musicCurrentTime);
//        for(byte[] bt : musicByteList){
//            mGattCallBack.addQueuryData(new DataBean(bt, SETTING_CMD_TIMEOUT, false));
//        }


        Logger.myLog(TAG,"-----写入音乐状态="+new Gson().toJson(musicByteList));
        mGattCallBack.addQueuryData(new DataBean(musicByteList.get(0), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(musicByteList.get(1), SETTING_CMD_TIMEOUT, false));
        mGattCallBack.addQueuryData(new DataBean(musicByteList.get(2), SETTING_CMD_TIMEOUT, false));

        boolean isNull = mGattCallBack.getQueuryLenth() == 0;
        Logger.myLog(TAG,"---isNull="+isNull);
        sendQueryData(true);
    }


    public void getExceData(int num){
        boolean isNull = mGattCallBack.getQueuryLenth() == 0 ? true : false;
        mGattCallBack.addQueuryData(new DataBean(w526Cmd.getExerciseData(num), PRACTISE_CMD_TIMEOUT, false));
        sendQueryData(isNull);
    }

}
