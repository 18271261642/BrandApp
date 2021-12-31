package com.isport.blelibrary.managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;

import com.isport.blelibrary.bluetooth.ISportResCode;
import com.isport.blelibrary.bluetooth.callbacks.BaseGattCallback;
import com.isport.blelibrary.bluetooth.service.NRFUartService;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.order.W520Cmd;
import com.isport.blelibrary.result.impl.scale.ScaleCalculateResult;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;
import com.sleepace.sdk.manager.CallbackData;

import java.util.ArrayList;

import chipsea.bias.v235.CSBiasAPI;

/**
 * @Author
 * @Date 2018/10/11
 * @Fuction
 */

public class BaseManager extends W520Cmd {
    public static volatile int getStateCount = 0;
    private static volatile boolean mIsWeight;//是否在测量体重页面

    private final String TAG = this.getClass().getSimpleName();

    public static volatile Long deviceBindTime;

    public static Context mContext;//上下文
    public static volatile NRFUartService nrfService;
    protected static volatile BaseDevice mCurrentDevice;
    private static volatile boolean mBLE_Supported = false;//是否支持Ble
    private static volatile BluetoothManager mBluetoothManager;
    private static volatile BluetoothAdapter mBluetoothAdapter;
    DeviceInformationTable mDeviceInformationTable;


    public BaseManager() {

    }

    public BaseManager(Context context) {
        init(context);
    }

    protected Handler notiHandler;
    protected Handler mHandler;
    protected Handler sendHandler;
    protected Handler syncHandler;
    protected Handler timeOutHandler;
    protected Handler suceessHandler;

    protected static long SYNC_TIMEOUT = 25 * 1000;
    protected static long times = 200;


    public void init(Context context) {
        // Logger.myLog("mBaseManager surper init");
        mContext = context.getApplicationContext();
        initialize();
        initBluetooth();
    }

    public void setCurrentDevice(BaseDevice baseDevice) {
        Logger.myLog(TAG,"--setCurrentDevice--baseDevice="+baseDevice.toString());
        mCurrentDevice = baseDevice;
    }

    public void clearCurrentDevice() {
        mCurrentDevice = null;
    }

    public BaseDevice getCurrentDevice() {
        return mCurrentDevice;
    }

    public static float mHeight;
    public static int mSex;
    public static int mAge;
    public static String mUserId;
    public static int mYear;
    public static int mMonth;
    public static int mDay;
    public static int mWeight;
    public static int targStep;
    public static int targDistance;
    public static int targCalorie;
    public static String isTmepUnitl;
    public static int remideHr;

    public static void setUserInfo(int year, int month, int day, int weight, float height, int sex, int age, String userId) {
        mYear = year;
        mMonth = month;
        mDay = day;
        mHeight = height;
        mSex = sex;
        mAge = age;
        mUserId = userId;
        mWeight = weight;
        targStep = 6000;
        targDistance = 1000;
        targCalorie = 10;
    }

    public static void setStepTarget(int step) {
        targStep = step;
    }

    public static void setDistanceTarget(int distance) {
        targDistance = distance;
    }

    public static void setCalorieTarget(int calorie) {
        targCalorie = calorie;
    }

    /**
     * 设置用户信息
     * <p>
     * height 身高 单位cm
     * sex    男-1 女-0
     * age    年龄
     *
     * @param weight   当前测量体重 单位kg
     * @param r        当前测量电阻
     * @param deviceId 设备唯一标示
     */
    protected ScaleCalculateResult calculate(float weight, float r, String deviceId) {
//        CSBiasAPI.CSBiasV235Resp cSBiasV235Resp = CSBiasAPI.cs_bias_v235(0, mSex, mAge, (int) height, (int)
// (weight), (int) r, 2018);
//        人体成分算法 - mode 模式，默认为 0
//                - sex 性别，1 男，0 女
//                - age 年龄，取值在 18 ~99 岁之间
//                - height 身高, 取值在 90 ~220 之间，表示 90 ~ 220 厘米.
//                - weight 体重，取值在 200 ~1500 之,之间，表示 20 公斤~150 公斤
//                - impedance 阻抗值, 取值在 2000 ~ 15000
//                -vkeyCode 指纹验证中的验
        if (mHeight < 90) {
            mHeight = 90;
        } else if (mHeight > 220) {
            mHeight = 220;
        }
        if (mAge < 18) {
            mAge = 18;
        } else if (mAge > 99) {
            mAge = 99;
        }

        if (r < 2000) {
            r = 2000;
        } else if (r > 15000) {
            r = 15000;
        }
        Logger.myLog("mSex == " + mSex + " mAge == " + mAge + " mHeight == " + mHeight + " weight == " + (int)
                (weight) * 10 + " R == " + r);
        CSBiasAPI.CSBiasV235Resp cSBiasV235Resp = CSBiasAPI.cs_bias_v235(0, mSex, mAge, (int) mHeight, (int) (weight)
                        * 10,
                (int) r, 2018);

        StringBuilder sb = new StringBuilder();

        if (cSBiasV235Resp.result == CSBiasAPI.CSBIAS_OK) {
            //计算
            try {
                sb.append("输入\r\n");
//                sb.append("性别:" + mAge + " 身高:" + height + " 年龄:" + mAge + " 电阻:" + r + " 体重:" + weight + "\r\n");
                sb.append("性别:" + mSex + " 身高:" + mHeight + " 年龄:" + mAge + " 电阻:" + r + " 体重:" + weight + "\r\n");
                sb.append("**************************************\r\n");
                sb.append("**************************************\r\n");
                sb.append("脂肪率%:" + cSBiasV235Resp.data.BFP + "\r\n");
                sb.append("肌肉重kg:" + cSBiasV235Resp.data.SLM + "\r\n");
                sb.append("水含量%:" + cSBiasV235Resp.data.BWP + "\r\n");
                sb.append("骨盐量:" + cSBiasV235Resp.data.BMC + "\r\n");
                sb.append("内脏脂肪等级:" + cSBiasV235Resp.data.VFR + "\r\n");
                sb.append("蛋白质%:" + cSBiasV235Resp.data.PP + "\r\n");
                sb.append("骨骼肌kg:" + cSBiasV235Resp.data.SMM + "\r\n");
                sb.append("基础代谢:" + cSBiasV235Resp.data.BMR + "\r\n");
                sb.append("身体质量指数:" + cSBiasV235Resp.data.BMI + "\r\n");
                sb.append("标准体重kg:" + cSBiasV235Resp.data.SBW + "\r\n");
                sb.append("肌肉控制:" + cSBiasV235Resp.data.MC + "\r\n");
                sb.append("体重控制:" + cSBiasV235Resp.data.WC + "\r\n");
                sb.append("脂肪控制:" + cSBiasV235Resp.data.FC + "\r\n");
                sb.append("身体年龄:" + cSBiasV235Resp.data.MA + "\r\n");
                sb.append("评分:" + cSBiasV235Resp.data.SBC + "\r\n");
                Logger.myLog("scale_fourElectrode_dataModel sb ==" + sb.toString());
                Scale_FourElectrode_DataModel scale_fourElectrode_dataModel = new Scale_FourElectrode_DataModel();
                scale_fourElectrode_dataModel.setDeviceId(deviceId);
                scale_fourElectrode_dataModel.setUserId(mUserId);
                scale_fourElectrode_dataModel.setReportId("0");//默认单机版是0,网络版本，上传成功更新为返回的reportId
                scale_fourElectrode_dataModel.setDateStr(TimeUtils.getTodayYYYYMMDD());
                scale_fourElectrode_dataModel.setTimestamp(System.currentTimeMillis());
                scale_fourElectrode_dataModel.setSex(mSex);
                scale_fourElectrode_dataModel.setHight((int) mHeight);
                scale_fourElectrode_dataModel.setAge(mAge);
                scale_fourElectrode_dataModel.setR((int) r);
                scale_fourElectrode_dataModel.setWeight(weight);
                scale_fourElectrode_dataModel.setBFP(cSBiasV235Resp.data.BFP);
                scale_fourElectrode_dataModel.setSLM(cSBiasV235Resp.data.SLM);
                scale_fourElectrode_dataModel.setBWP(cSBiasV235Resp.data.BWP);
                scale_fourElectrode_dataModel.setBMC(cSBiasV235Resp.data.BMC);
                scale_fourElectrode_dataModel.setVFR(cSBiasV235Resp.data.VFR);
                scale_fourElectrode_dataModel.setPP(cSBiasV235Resp.data.PP);
                scale_fourElectrode_dataModel.setSMM(cSBiasV235Resp.data.SMM);
                scale_fourElectrode_dataModel.setBMR(cSBiasV235Resp.data.BMR);
                scale_fourElectrode_dataModel.setBMI(cSBiasV235Resp.data.BMI);
                scale_fourElectrode_dataModel.setSBW(cSBiasV235Resp.data.SBW);
                scale_fourElectrode_dataModel.setMC(cSBiasV235Resp.data.MC);
                scale_fourElectrode_dataModel.setWC(cSBiasV235Resp.data.WC);
                scale_fourElectrode_dataModel.setFC(cSBiasV235Resp.data.FC);
                scale_fourElectrode_dataModel.setSBC(cSBiasV235Resp.data.SBC);
                scale_fourElectrode_dataModel.setMA(cSBiasV235Resp.data.MA);
                //只有在称重页面才会做存储F
                if (mIsWeight)
                    ParseData.saveScaleFourElectrodeData(scale_fourElectrode_dataModel);
                Logger.myLog("scale_fourElectrode_dataModel==" + scale_fourElectrode_dataModel.toString());
                return new ScaleCalculateResult(scale_fourElectrode_dataModel);
            } catch (Exception ex) {
                Logger.myLog("scale_fourElectrode_dataModel==" + ex.getLocalizedMessage());
                sb.append("输入错误，错误码：" + ex.getLocalizedMessage());
            }
        } else {
            Logger.myLog("输入错误，错误码：" + cSBiasV235Resp.result);
            sb.append("输入错误，错误码：" + cSBiasV235Resp.result);
        }
        return null;
    }

    protected static boolean checkStatus(CallbackData cd) {
        return cd.isSuccess();
    }

    protected ArrayList<BleReciveListener> mBleReciveListeners = new ArrayList<BleReciveListener>();

    /**
     * 注册监听
     *
     * @param bleReciveListener
     */
    public void registerListener(BleReciveListener bleReciveListener) {
        if (bleReciveListener == null)
            return;
        if (!(mBleReciveListeners.contains(bleReciveListener))) {
            mBleReciveListeners.add(bleReciveListener);         //把观察者的引用添加到数组线性表
        }
    }

    /**
     * 反注册监听
     */
    public void unregisterListener(BleReciveListener bleReciveListener) {
        if (bleReciveListener == null)
            return;
        mBleReciveListeners.remove(bleReciveListener);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Logger.myLog("Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.myLog("Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    private void initBluetooth() {
        Logger.LOGI(TAG, "initBluetooth");
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mBLE_Supported = true;
        }
        Logger.LOGI(TAG, "Is ble supported " + mBLE_Supported);
        if (mBLE_Supported) {
            Logger.LOGI(TAG, "mBLE_enable init begin");
            service_nrf_init();
        }
    }

    /**
     * 初始化服务
     */
    private void service_nrf_init() {
        Intent bindIntent = new Intent(mContext, NRFUartService.class);
        mContext.bindService(bindIntent, nrfServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * UART service connected/disconnected
     */
    protected ServiceConnection nrfServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            Logger.myLog("NFR connection call back, service init");
            try {
                nrfService = ((NRFUartService.LocalBinder) rawBinder).getService();
            } catch (Exception e) {
                Logger.myLog("NFR connection call back, error");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            nrfService = null;
        }
    };


    private void stop_ble_service() {
        if (mBLE_Supported) {
            try {
                if (nrfServiceConnection != null)
                    mContext.unbindService(nrfServiceConnection);
            } catch (Exception e) {
                Logger.myLog("mContext未绑定服务");
            }
//            if (nrfService != null) {
//                nrfService.stopSelf();
//                nrfService = null;
//            }
        }
    }

    public void exit() {
        stop_ble_service();
    }

    /**
     * Bluetooth4.0连接NRF设备
     */
    public void connectNRF(BaseGattCallback baseGattCallbacks, BaseDevice baseDevice, boolean isConnectByUser, int deday) {
        //开始连接的状态回去

        if (mBluetoothAdapter == null || baseDevice.address == null) {
            Logger.myLog(TAG,"BluetoothAdapter not initialized or unspecified address.");
            return;
        }
        setCurrentDevice(baseDevice);
        if (nrfService != null && Utils.isNotEmpty(baseDevice.address)) {
            Logger.myLog(TAG,"NRF connect begin " + baseDevice.address + "baseDevice.getDeviceName()" + baseDevice.getDeviceName());
            baseGattCallbacks.connect(baseDevice.address, baseDevice.deviceType, isConnectByUser, deday);
        } else {
            if (baseGattCallbacks.bluetoothListener != null) {
                baseGattCallbacks.bluetoothListener.not_connected(ISportResCode.ERR_BLE_SERVICE_NOT_REG);
            }
            Logger.myLog(TAG,"Did NRF Service had being added to manifest? ");
        }
    }

    public static void setIsWeight(boolean isWeight) {
        mIsWeight = isWeight;
    }

    public static void setDeviceBindTime(Long time) {
        deviceBindTime = time;
    }
}
