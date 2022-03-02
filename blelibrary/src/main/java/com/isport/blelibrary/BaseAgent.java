package com.isport.blelibrary;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.isport.blelibrary.bluetooth.scanner.ScanManager;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.S002BDevice;
import com.isport.blelibrary.deviceEntry.impl.SleepDevice;
import com.isport.blelibrary.deviceEntry.impl.W307JDevice;
import com.isport.blelibrary.deviceEntry.impl.W311Device;
import com.isport.blelibrary.deviceEntry.impl.W520Device;
import com.isport.blelibrary.deviceEntry.impl.W526Device;
import com.isport.blelibrary.deviceEntry.impl.W557Device;
import com.isport.blelibrary.deviceEntry.impl.W560BDevice;
import com.isport.blelibrary.deviceEntry.impl.W560Device;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.deviceEntry.impl.W812BDevice;
import com.isport.blelibrary.deviceEntry.impl.W812Device;
import com.isport.blelibrary.deviceEntry.impl.W813Device;
import com.isport.blelibrary.deviceEntry.impl.W814Device;
import com.isport.blelibrary.deviceEntry.impl.W817Device;
import com.isport.blelibrary.deviceEntry.impl.W819Device;
import com.isport.blelibrary.deviceEntry.impl.W910Device;
import com.isport.blelibrary.deviceEntry.impl.Watch516Device;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.entry.AlarmEntry;
import com.isport.blelibrary.entry.AutoSleep;
import com.isport.blelibrary.entry.DisplaySet;
import com.isport.blelibrary.entry.NotificationMsg;
import com.isport.blelibrary.entry.SedentaryRemind;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.interfaces.CusScannResultListener;
import com.isport.blelibrary.interfaces.ScanBackListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.managers.BraceletW311BleManager;
import com.isport.blelibrary.managers.BraceletW520BleManager;
import com.isport.blelibrary.managers.BraceletW811W814Manager;
import com.isport.blelibrary.managers.ScaleBleManager;
import com.isport.blelibrary.managers.SleepBleManager;
import com.isport.blelibrary.managers.Watch516BleManager;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.managers.WatchW557BleManager;
import com.isport.blelibrary.scanner.BluetoothLeScannerCompat;
import com.isport.blelibrary.scanner.ScanCallback;
import com.isport.blelibrary.scanner.ScanResult;
import com.isport.blelibrary.scanner.ScanSettings;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

/**
 * @Author
 * @Date 2018/10/11
 * @Fuction
 */

public class BaseAgent {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private ScanManager mScanManager;
    private ScanBackListener mScanBackListener;
    private String scanFilter;
    private boolean isDFUMode;
    private Handler mHandler;


    private CusScannResultListener cusScannResultListener;
    private List<String> filterList = new ArrayList<>();

//    public BaseDevice createNULLDevice(String name, String mac, int rssi, int type) {
//        return new WatchDevice(name, mac, rssi, type);
//    }

    /**
     * 注册监听
     *
     * @param bleReciveListener
     */
    public void registerListener(BleReciveListener bleReciveListener) {
        if (bleReciveListener == null)
            return;
        SleepBleManager.getInstance().registerListener(bleReciveListener);
        ScaleBleManager.getInstance().registerListener(bleReciveListener);
        Watch516BleManager.getInstance().registerListener(bleReciveListener);
        BraceletW311BleManager.getInstance().registerListener(bleReciveListener);
        BraceletW520BleManager.getInstance().registerListener(bleReciveListener);
        WatchW557BleManager.getInstance().registerListener(bleReciveListener);
        BraceletW811W814Manager.getInstance().registerListener(bleReciveListener);

        Watch7018Manager.getWatch7018Manager().registerListener(bleReciveListener);
    }

    /**
     * 反注册监听
     */
    public void unregisterListener(BleReciveListener bleReciveListener) {
        if (bleReciveListener == null)
            return;
        SleepBleManager.getInstance().unregisterListener(bleReciveListener);
        ScaleBleManager.getInstance().unregisterListener(bleReciveListener);
        Watch516BleManager.getInstance().unregisterListener(bleReciveListener);
        BraceletW311BleManager.getInstance().unregisterListener(bleReciveListener);
        BraceletW520BleManager.getInstance().unregisterListener(bleReciveListener);
        WatchW557BleManager.getInstance().unregisterListener(bleReciveListener);
        BraceletW811W814Manager.getInstance().unregisterListener(bleReciveListener);
    }

    private void initScan() {
        mScanManager = ScanManager.getInstance(mContext);
    }

    /**
     * 初始化方法,体脂称直接启动服务，等待连接
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
        initScan();
        initHandler();
        InitDeviceManager.initManager(mContext);
        //指针表
    }


    /**
     * 设置用户信息
     */
    public void setUserInfo(int year, int month, int day, int weight, float height, int sex, int age, String userId) {
        BaseManager.setUserInfo(year, month, day, weight, height, sex, age, userId);
    }

    public void setStepTarget(int stepTarget) {
        BaseManager.setStepTarget(stepTarget);
    }

    public void setDistanceTarget(int distanceTarget) {
        BaseManager.setDistanceTarget(distanceTarget);
    }

    public void setCalorieTarget(int calorieTarget) {
        BaseManager.setCalorieTarget(calorieTarget);
    }

    public void setIsWeight(boolean isWeight) {
        BaseManager.setIsWeight(isWeight);
    }

    public void setDeviceBindTime(Long time) {
        BaseManager.setDeviceBindTime(time);
    }


    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x00:
//                        ArrayList<BaseDevice> tplist = (ArrayList<BaseDevice>) msg.obj;
                        Map<String, BaseDevice> tplist = (Map<String, BaseDevice>) msg.obj;
                        mScanBackListener.onScanResult(tplist);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    protected BaseDevice handleActionFount(BluetoothDevice device, byte[] bytes, int rssi) {
        //ScanRecord scanRecord = result.getScanRecord();
        String address = device.getAddress();
        String name = device.getName();

        if (TextUtils.isEmpty(name)) {
            return null;
        }
        //Logger.myLog(TAG,"handleActionFount deviceName = " + name+" mac="+device.getAddress());
        if (name.contains(Constants.SLEEP_FILTER)) {
            String deviceName = Utils.getBleDeviceName(0xff, bytes);
            if (deviceName != null) {
                deviceName = deviceName.trim();
            }
            name = deviceName;
            if (TextUtils.isEmpty(name)) {
                return null;
            }
        }
        if (TextUtils.isEmpty(scanFilter)) {
            Logger.myLog("请先设置搜索过滤");
            return null;
        }
        if (scanFilter.equals("MZ")) {
            if (name.contains("Chipsea") || name.contains("MZ")) {
                BaseDevice baseDevice = null;
                if (createDevice == null) {
                    createDevice = new CreateDevice();
                }
                return createDevice.createDevcie(name, address, scanFilter, isDFUMode);
            }
        } else {
           // Logger.myLog(TAG,"scanFilter:" + scanFilter + "name:" + name + "name.contains(scanFilter):" + name.contains(scanFilter));


            if ( name.contains(scanFilter) || scanFilter.equals("all") || (scanFilter.equals(Constants.WATCH_560_FILTER) && name.contains("FT_ReflexSW")) || (scanFilter.equals(Constants.WATCH_7018_FILTER) && name.equals("BL"))) {
                if (createDevice == null) {
                    createDevice = new CreateDevice();
                }
                return createDevice.createDevcie(name, address, scanFilter, isDFUMode);
            } else {

                if (name.contains(scanFilter) && name.contains(Constants.BRAND_FILTER)) {
                    if (createDevice == null) {
                        createDevice = new CreateDevice();
                    }
                    return createDevice.createDevcie(name, address, scanFilter, isDFUMode);
                } else {
                    byte[] values = bytes;
                    //57 35 31 36 20 20 3A 00 00 00 00 4D 00
                    if (values.length >= 15) {
                        byte[] version = new byte[4];
                        version[0] = values[11];
                        version[1] = values[12];
                        version[2] = values[13];
                        version[3] = values[14];
                        String deviceName = new String(version);
                        if (new String(version).contains(scanFilter)) {
                            if (name.contains(Constants.WATCH_FILTER)) {
                                if (createDevice == null) {
                                    createDevice = new CreateDevice();
                                }
                                return createDevice.createDevcie(name, address, scanFilter, isDFUMode);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    //*************************************************通用方法**********************************************************//

    volatile ArrayList<BaseDevice> listDevicesTp = new ArrayList<BaseDevice>();
    volatile LinkedHashMap<String, BaseDevice> listDevicesMap = new LinkedHashMap<>();
    volatile ArrayList<String> macListTp = new ArrayList<String>();

    protected void scanDevice(ScanBackListener scanBackListener, int deviceType, boolean isScaleWithTimeOut) {
        switch (deviceType) {
            case IDeviceType.TYPE_ALL:
                scanSupportAllDevice(scanBackListener, false);
                break;
            case IDeviceType.TYPE_DFU:
                scanSupportAllDevice(scanBackListener, true);
                break;
            case IDeviceType.TYPE_WATCH_W516:
                scanWatchW516(scanBackListener);
                break;
            case IDeviceType.TYPE_WATCH_W526:
                scanWatchW526(scanBackListener);
                break;
            case IDeviceType.TYPE_WATCH_W557:
                scanWatchW557(scanBackListener);
                break;
            case IDeviceType.TYPE_WATCH_W560B:
                scanWatchW560B(scanBackListener);
                break;
            case IDeviceType.TYPE_WATCH_W560:
                scanWatchW560(scanBackListener);
                break;
            case IDeviceType.TYPE_SCALE:
                if (isScaleWithTimeOut) {
                    scanScaleWithTimeOut(scanBackListener);
                } else {
                    scanScale(scanBackListener);
                }
                break;
            case IDeviceType.TYPE_SLEEP:
                scanSleep(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W311:
                scanBrand(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W307J:
                scanBrandW307J(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W520:
                scanBrandW520(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W812:
                scanW812(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W813:
                scanW813(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W819:
                scanW819(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W814:
                scanW814(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W910:
                scanW910(scanBackListener);
                break;
            case IDeviceType.TYPE_BRAND_W817:
                scanW817(scanBackListener);
                break;
            case IDeviceType.TYPE_WATCH_W812B:
                scanWatchW812B(scanBackListener);
                break;
            case IDeviceType.TYPE_ROPE_S002:
                scanS002(scanBackListener);
                break;
            case IDeviceType.TYPE_WATCH_7018:
                scan7018(scanBackListener);
                break;
            default:
                break;
        }
    }


    protected void scanDevice(CusScannResultListener cusScannResultListener,long timeOut){
        startScanGoalDevice(cusScannResultListener,timeOut);
    }
    /**
     * 搜索体脂称
     *
     * @param scanBackListener
     * @return
     */
    protected boolean scanScale(ScanBackListener scanBackListener) {
        scanFilter = Constants.SCALE_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    /**
     * 搜索体脂称
     *
     * @param scanBackListener
     * @return
     */
    protected boolean scanScaleWithTimeOut(ScanBackListener scanBackListener) {
        scanFilter = Constants.SCALE_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    /**
     * 搜索睡眠带
     *
     * @param scanBackListener
     * @return
     */
    protected boolean scanSleep(ScanBackListener scanBackListener) {
        scanFilter = Constants.SLEEP_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    /**
     * 搜索手表
     *
     * @param scanBackListener
     * @return
     */
    protected boolean scanWatch(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    /**
     * 搜索手环
     *
     * @param scanBackListener
     * @return
     */
    protected boolean scanBrand(ScanBackListener scanBackListener) {
        scanFilter = Constants.BRAND_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    /**
     *
     */
    protected boolean scanBrandW520(ScanBackListener scanBackListener) {
        scanFilter = Constants.BRAND_520_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanBrandW307J(ScanBackListener scanBackListener) {
        scanFilter = Constants.BRAND_W307J_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanW812(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_812_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanW813(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_813_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanW910(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_9101_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanS002(ScanBackListener scanBackListener) {
        scanFilter = Constants.ROPE_S002_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanW817(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_817_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanW819(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_819_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanW814(ScanBackListener scanBackListener) {
        scanFilter = Constants.BRAND_814_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scan7018(ScanBackListener scanBackListener){
        scanFilter = Constants.WATCH_7018_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }


    /**
     * 搜索手表
     *
     * @param scanBackListener
     * @return
     */
    protected boolean scanWatchW516(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanWatchW526(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_556_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanWatchW812B(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_812B_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanWatchW557(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_557_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanWatchW560B(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_560B_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }
    protected boolean scanWatchW560(ScanBackListener scanBackListener) {
        scanFilter = Constants.WATCH_560_FILTER;
        return startLeScanWithOutTimeOut(scanBackListener);
    }

    protected boolean scanSupportAllDevice(ScanBackListener scanBackListener, boolean isDfu) {
        this.isDFUMode = isDfu;
        scanFilter = "all";
        return startLeScanWithOutTimeOut(scanBackListener);
    }


    public void unbind(String mac) {


        Logger.myLog("BaseAgent unbind" + mac);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(mac);
            if (bleDevice != null) {
                unpairDevice(bleDevice);
            }
        }
    }

    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    /**
     * Scan for {@link Constants#SCAN_DURATION }seconds and then stop scanning when a BluetoothLE device is found
     * then mLEScanCallback is activated
     * This will perform regular scan for custom BLE Service UUID and then filter out.
     * using class ScannerServiceParser
     * you need set import {compile 'no.nordicsemi.android.support.v18:scanner:1.0.0'} int your app gradle
     * <p>
     * Since Android 6.0 we need to obtain either Manifest.permission.ACCESS_COARSE_LOCATION or Manifest.permission
     * .ACCESS_FINE_LOCATION to be able to scan for
     * Bluetooth LE devices. This is related to beacons as proximity devices.
     * On API older than Marshmallow the following code does nothing.
     */
   /* private boolean startLeScanWithTimeOut(ScanBackListener scanBackListener) {
        mScanBackListener = scanBackListener;
        listDevicesTp.clear();
        listDevicesMap.clear();
        macListTp.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                Logger.myLog("request permission:" + Manifest.permission_group.LOCATION);
                return false;
            }
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            return false;
        }
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        startLeScan();
        //macCache.clear();
     *//*   mScanManager.setScanListener(scanCallback);
        mScanManager.setScanTime(Constants.SCAN_DURATION);*//*
     *//* if (Build.BRAND.equals("Xiaomi")) {

        } else {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            // Logger.myLog("获取已经配对devices" + devices.size());
            for (BluetoothDevice bluetoothDevice : devices) {
                BaseDevice tpbaseDevice = getBondDevice(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                if (tpbaseDevice != null && !macListTp.contains(bluetoothDevice.getAddress())) {

                    Logger.myLog("getBondedDevices:Utils.isContainsDFU(tpbaseDevice.deviceName)" + Utils.isContainsDFU(tpbaseDevice.deviceName));

                    if (Utils.isContainsDFU(tpbaseDevice.deviceName)) {
                        continue;
                    }
                    listDevicesTp.add(tpbaseDevice);
                    macListTp.add(tpbaseDevice.address);
                    addtpbaseDevice(tpbaseDevice);
                }
            }
            Message msg = Message.obtain();
            msg.obj = listDevicesMap;
            msg.what = 0x00;
            mHandler.sendMessage(msg);
        }*//*
        //return mScanManager.startLeScan(true);
        return true;
    }
*/

    private final static long SCAN_DURATION = 20 * 1000;
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
//                BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
//                scanner.stopScan(cusScanCallback);
//                isF18Scann = false;
                if(adapter == null || !adapter.isEnabled())
                    return;
                adapter.stopLeScan(leScanCallback);

            }
        }
    };
    int connectingPosition = -1;
    boolean scanning = false;

    private final Runnable stopScanTask = this::stopLeScan;

    public void stopLeScan() {
//        if (!scanning)
//            return;

       BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);

        handler.removeCallbacks(stopScanTask);
        scanning = false;
    }

    public void startLeScan() {
        // Scanning is disabled when we are connecting or connected.
        sumSize = 0;
        if (connectingPosition >= 0)
            return;

        if (scanning) {
            // Extend scanning for some time more
            handler.removeCallbacks(stopScanTask);
            handler.postDelayed(stopScanTask, SCAN_DURATION);
            return;
        }

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder().setReportDelay(1000).setUseHardwareBatchingIfSupported(false).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanner.startScan(null, settings, scanCallback);

        // Setup timer that will stop scanning
        handler.postDelayed(stopScanTask, SCAN_DURATION);
        scanning = true;
    }


    private boolean isF18Scann = false;


    public void stopLeScan(long scanTime){
        handler.sendEmptyMessage(0x00);
    }


    public void startLeScan(long scanTime) {
        // Scanning is disabled when we are connecting or connected.
        if(adapter == null || !adapter.isEnabled())
            return;
        adapter.stopLeScan(leScanCallback);

        adapter.startLeScan(leScanCallback);

//
//         BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
//         ScanSettings settings = new ScanSettings.Builder().setReportDelay(1000).setUseHardwareBatchingIfSupported(false).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
//        scanner.startScan(null,settings,cusScanCallback);
//        isF18Scann = true;
        handler.sendEmptyMessageDelayed(0x00,scanTime);

//        // Setup timer that will stop scanning
//        handler.postDelayed(stopScanTask, scanTime);
//        scanning = true;
    }


    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback(){


        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(bluetoothDevice.getName() == null || bluetoothDevice.getAddress() == null)
                return;
//            if(filterList.contains(bluetoothDevice.getAddress()))
//                return;
//            filterList.add(bluetoothDevice.getAddress());
            if(cusScannResultListener != null)
                cusScannResultListener.onCusScanResult(bluetoothDevice,scanRecord,rssi);
        }
    };


    CreateDevice createDevice;

    public BaseDevice getBondDevice(String name, String address) {
        if(name.contains("FT_ReflexSW") && scanFilter.equals(Constants.WATCH_560_FILTER)){
            if (createDevice == null) {
                createDevice = new CreateDevice();
            }
            return createDevice.createDevcie(name, address, scanFilter, isDFUMode);
        }

        if (name.contains(scanFilter) || scanFilter.equals("all")) {
            if (createDevice == null) {
                createDevice = new CreateDevice();
            }
            return createDevice.createDevcie(name, address, scanFilter, isDFUMode);
        } else {
            return null;
        }

    }

    BluetoothAdapter adapter;

    private void startScanGoalDevice(CusScannResultListener cusScannResultListener, long scanTimeOut){
        filterList.clear();
        this.cusScannResultListener = cusScannResultListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                Logger.myLog("request permission:" + Manifest.permission_group.LOCATION);
                Logger.myLog("开始扫描1111");
                return ;
            }
        }
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Logger.myLog("开始扫描2222");
            return;
        }
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            Logger.myLog("开始扫描3333");
            return ;
        }
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : devices) {
            if(bluetoothDevice.getName() == null || bluetoothDevice.getAddress() == null)
                return;
            if(!filterList.contains(bluetoothDevice.getAddress())){

                filterList.add(bluetoothDevice.getAddress());
                cusScannResultListener.onCusScanResult(bluetoothDevice,new byte[]{},0);
            }

        }
        startLeScan(scanTimeOut);
    }


    private boolean startLeScanWithOutTimeOut(ScanBackListener scanBackListener) {
        Logger.myLog("开始扫描0000");
        mScanBackListener = scanBackListener;
        listDevicesTp.clear();
        listDevicesMap.clear();
        macListTp.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                Logger.myLog("request permission:" + Manifest.permission_group.LOCATION);
                Logger.myLog("开始扫描1111");
                return false;
            }
        }
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Logger.myLog("开始扫描2222");
            return false;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            Logger.myLog("开始扫描3333");
            return false;
        }
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        //macCache.clear();
        //mScanManager.setScanListener(scanCallback);
        //mScanManager.setScanTime(Constants.SCAN_DURATION);
        Logger.myLog("开始扫描444");
        //return mScanManager.startLeScan(false);


        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        // Logger.myLog("获取已经配对devices" + devices.size());
        for (BluetoothDevice bluetoothDevice : devices) {
            if(bluetoothDevice.getName() != null && !bluetoothDevice.getName().equals("F18")){
                BaseDevice tpbaseDevice = getBondDevice(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                if (tpbaseDevice != null && !macListTp.contains(bluetoothDevice.getAddress())) {
                    //Logger.myLog("getBondedDevices:Utils.isContainsDFU(tpbaseDevice.deviceName)" + Utils.isContainsDFU(tpbaseDevice.deviceName));
                    if (Utils.isContainsDFU(tpbaseDevice.deviceName)) {
                        continue;
                    }
                    listDevicesTp.add(tpbaseDevice);
                    macListTp.add(tpbaseDevice.address);
                    addtpbaseDevice(tpbaseDevice);
                }
            }


        }
        Message msg = Message.obtain();
        msg.obj = listDevicesMap;
        msg.what = 0x00;
        mHandler.sendMessage(msg);
        startLeScan();
        return true;
    }


    /**
     * Stop scan if user tap Cancel button
     */
    protected void cancelLeScan() {
        stopLeScan();
    }

    protected boolean isScan() {
        return scanning;
    }


    protected volatile int sumSize = 0;

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // empty
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            // final int size = devices.size();

            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    for (final ScanResult result : results) {
                        final BluetoothDevice device = result.getDevice();
                       // Logger.myLog(TAG,"onBatchScanResults: device.getName()=" + device.getName() + ",device.getAddress()=" + device.getAddress() + ",result.getScanRecord()=" + result.getScanRecord().getDeviceName());
                        BaseDevice tpbaseDevice = handleActionFount(device, result.getScanRecord().getBytes(), result.getRssi());
                        if (tpbaseDevice != null && Utils.isContainsDFU(tpbaseDevice.deviceName)) {
                            listDevicesMap.put(tpbaseDevice.address, tpbaseDevice);
                            //sendMessage(tpbaseDevice);
                            listDevicesTp.add(tpbaseDevice);
                            macListTp.add(tpbaseDevice.address);
                        } else {
                            if (tpbaseDevice != null && !macListTp.contains(tpbaseDevice.address)) {
                                listDevicesTp.add(tpbaseDevice);
                                macListTp.add(tpbaseDevice.address);
                                addtpbaseDevice(tpbaseDevice);
                                //Logger.myLog("没有添加过的设备 =deviceName= " + tpbaseDevice.deviceName + " address == " + tpbaseDevice.address + "类型：" + tpbaseDevice.deviceType);
                            } else {
                                if (tpbaseDevice != null) {
//                        Logger.myLog("已经添加过的设备 == " + tpbaseDevice.deviceName);
                                } else {
//                        Logger.myLog("其他类型的设备 == ");
                                }
                            }
                        }

                    }
                   // Logger.myLog("onBatchScanResults sumSize=" + sumSize + ",macListTp.size()=" + listDevicesTp.size() + ",Thread.currentThread()=" + Thread.currentThread());

                    if (sumSize == listDevicesTp.size()) {
                        return;
                    }
                    sumSize = listDevicesTp.size();

                    Message msg = Message.obtain();
                    msg.obj = listDevicesMap;
                    msg.what = 0x00;
                    mHandler.sendMessage(msg);
                }
            });

        }

        @Override
        public void onScanFailed(final int errorCode) {
            // empty
        }
    };



    /*MyScanManagerCallBack scanCallback = new MyScanManagerCallBack();


    private class MyScanManagerCallBack implements ScanManager.OnScanManagerListener {

        @Override
        public void onBatchScanResults(ArrayList<BleLeScanCallback.ScanResult> results) {
//            Logger.myLog("扫描返回111");
            results = (results == null ? (new ArrayList<BleLeScanCallback.ScanResult>()) : results);
            if (Constants.IS_DEBUG) {
//                if (results.size() > 0)
//                    Log.e(TAG, "onBatchScanResults  size = " + results.size() + " name == " + results.get(0)
//                            .getDevice().getName());
            }
            for (int i = 0; i < results.size(); i++) {
                BleLeScanCallback.ScanResult result = results.get(i);
                BaseDevice tpbaseDevice = handleActionFount(result);
                Logger.myLog("listDevicesTp.add:" + tpbaseDevice);
                if (tpbaseDevice != null && Utils.isContainsDFU(tpbaseDevice.deviceName)) {
                    listDevicesMap.put(tpbaseDevice.address, tpbaseDevice);
                    //sendMessage(tpbaseDevice);
                    listDevicesTp.add(tpbaseDevice);
                    macListTp.add(tpbaseDevice.address);
                } else {
                    if (tpbaseDevice != null && !macListTp.contains(tpbaseDevice.address)) {
                        listDevicesTp.add(tpbaseDevice);
                        macListTp.add(tpbaseDevice.address);
                        addtpbaseDevice(tpbaseDevice);
                        Logger.myLog("没有添加过的设备 =deviceName= " + tpbaseDevice.deviceName + " address == " + tpbaseDevice.address + "类型：" + tpbaseDevice.deviceType);
                    } else {
                        if (tpbaseDevice != null) {
//                        Logger.myLog("已经添加过的设备 == " + tpbaseDevice.deviceName);
                        } else {
//                        Logger.myLog("其他类型的设备 == ");
                        }
                    }
                }
            }
            Message msg = Message.obtain();
            msg.obj = listDevicesMap;
            msg.what = 0x00;
            mHandler.sendMessage(msg);
            // TODO: 2018/10/10 此处要做排除重复设备的逻辑
        }


        @Override
        public void onScanFinished() {
            mScanBackListener.onScanFinish();
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "errorCode = " + errorCode);
        }
    }
*/

    public void addtpbaseDevice(BaseDevice tpbaseDevice) {
        listDevicesTp.add(tpbaseDevice);
        macListTp.add(tpbaseDevice.address);

        if (Utils.isContainsW81(tpbaseDevice.deviceName)) {
            tpbaseDevice.deviceName = CommonDateUtil.getW81DeviceName(tpbaseDevice.deviceName, tpbaseDevice.address);
            listDevicesMap.put(tpbaseDevice.getDeviceName(), tpbaseDevice);
            // sendMessage(tpbaseDevice);
        } else if (tpbaseDevice.deviceName.contains("MZ-20")) {
            listDevicesMap.put(tpbaseDevice.address, tpbaseDevice);
            //sendMessage(tpbaseDevice);
        } else {
            listDevicesMap.put(tpbaseDevice.getDeviceName(), tpbaseDevice);
            // sendMessage(tpbaseDevice);
        }
        //Logger.myLog(TAG,"没有添加过的设备 =deviceName= " + tpbaseDevice.deviceName + " address == " + tpbaseDevice.address);
    }

    //*************************************************通用方法**********************************************************//

    private static BaseManager mBaseManager;

    /**
     * 连接设备的方法
     *
     * @param device
     */
    protected void connectDevice(BaseDevice device, boolean cancelScan, boolean isConnectByUser) {
        if (cancelScan)
            stopLeScan();

       // Logger.myLog(TAG,"mBaseManager" + (mBaseManager!=null ? mBaseManager.getCurrentDevice().toString() : "mBaseManager为null了") + "mContext" + mContext + "------------device:" + device);
        if (mContext != null && device != null) {
            mBaseManager = getManager(device, mContext);
            mBaseManager.setCurrentDevice(device);
            device.connect(isConnectByUser);
        }
        //InitDeviceManager.initManager(mContext,device.deviceType);


    }

    protected BaseManager getManager(BaseDevice device, Context context) {
        return device.getManager(context);
    }

    /**
     * 断开设备
     */
//    protected void disConnectDevice() {
//        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
//            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
//            currentDevice.disconnect();
//        }
//    }
    protected void unbindConnectDevice(boolean reConnect) {

        if (mBaseManager != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice != null) {
                //用户手动去解除配对不需要在系统中逻辑进行取消配对
                unbind(currentDevice.address);
                disConnectDevice(reConnect);
            }
            // ISportAgent.getInstance().clearCurrentDevice();
        }

    }

    protected void disConnectDevice(boolean reConnect) {

        if (mBaseManager != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice != null) {
                currentDevice.disconnect(reConnect);
            }
        }
    }

    /**
     * 获取当天设备
     *
     * @return
     */
    public BaseDevice getCurrnetDevice() {
        return mBaseManager == null ? null : mBaseManager.getCurrentDevice();
    }

    public void clearCurrentDevice() {
        if (mBaseManager != null) {
            mBaseManager.clearCurrentDevice();
        }
    }

    /**
     * 获取电量
     */
    protected void getBattery() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            mBaseManager.getCurrentDevice().getBattery();
        }
    }

    public void exit() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            mBaseManager.getCurrentDevice().exit();
        }
        if (mScanManager != null) {
            mScanManager.exit();
        }
    }

    public void close() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            mBaseManager.getCurrentDevice().close();
        }
    }

    //************************************************W516双指针手表部分**************************************************//

    /**
     * 获取设备状态
     */
    protected void read_status() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).read_status();
            }
        }
    }

    /**
     * 通用设置
     */
    protected void set_general(boolean open24HeartRate, boolean isHeart) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_general(open24HeartRate, isHeart);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).set_general(open24HeartRate, isHeart);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).set_general(open24HeartRate, isHeart);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).set_general(open24HeartRate, isHeart);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).set_general(open24HeartRate, isHeart);
            }
        }
    }


    /**
     * 通用设置，设置心率、来电和短信
     */
    protected void set_general(boolean open24HeartRate, boolean isCall, boolean isMessage, String temp) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_general(open24HeartRate, isCall, isMessage);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).set_general(open24HeartRate, isCall, isMessage);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).set_general(open24HeartRate, isCall, isMessage);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).set_general(open24HeartRate, isCall, isMessage);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).set_general(open24HeartRate, isCall, isMessage);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).set_general(open24HeartRate, isCall, isMessage);
            }
        }
    }

    /**
     * 获取通用设置
     */
    protected void get_general() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_general();
            }
        }
    }

    /**
     * 设置用户信息
     */
    protected void set_user(int year, int month, int day, int sex, int weight, int height, int maxHeartRate, int
            minHeartRate) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_user(year, month, day, sex, weight, height, maxHeartRate,
                        minHeartRate);
            } else if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).set_user(year, month, day, sex, weight, height, maxHeartRate,
                        minHeartRate);
            }
        }
    }

    /**
     * 获取用户信息
     */
    protected void get_user() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_user();
            }
        }
    }

    /**
     * 设置时间戳
     */
    protected void set_calender() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_calender();
            }
        }
    }

    /**
     * 获取时间戳
     */
    protected void get_calender() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_calender();
            }
        }
    }

    /**
     * 设置闹钟
     */
    protected void set_alarm(boolean eanble, int day, int hour, int min, int index) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_alarm(eanble, day, hour, min, index);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).set_alarm(eanble, day, hour, min, index);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).set_alarm(eanble, day, hour, min, index);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).set_alarm(eanble, day, hour, min, index);
            } else if (currentDevice instanceof W560BDevice) {
                ((W560BDevice) currentDevice).set_alarm(eanble, day, hour, min, index);
            }
        }
    }

    /**
     * 设置W560闹钟
     */
    protected void set_w560_alarm(boolean eanble, int day, int hour, int min, int index, String name) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).set_w560_alarm(eanble, day, hour, min, index, name);
                return;
            }

            if(currentDevice instanceof W560BDevice){
                ((W560Device) currentDevice).set_w560_alarm(eanble, day, hour, min, index, name);
            }
        }
    }

    /**
     * 新增W560闹钟
     */
    protected void add_w560_alarm(int day, int hour, int min, String name) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).add_w560_alarm(day, hour, min, name);
            }
        }
    }

    /**
     * 删除W560闹钟
     */
    protected void delete_w560_alarm(int index) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).delete_w560_alarm(index);
            }
        }
    }


    //W560读取目标设置


    /**
     * 切换模式
     */
    protected void switch_mode(boolean inMode) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).switch_mode(inMode);
            }
        }
    }

    /**
     * 切换模式
     */
    protected void adjust(int hour, int min) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).adjust(hour, min);
            }
        }
    }

    /**
     * 获取闹钟设置
     */
    protected void get_alarm() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_alarm(0);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).get_alarm(0);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).get_alarm(0);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).get_alarm(0);
            } else if (currentDevice instanceof W560BDevice) {
                ((W560BDevice) currentDevice).get_alarm(0);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).get_alarm(0);
            }
        }
    }

    /**
     * 311 设置免打扰
     */
    protected void setW311DistrubSetting(boolean open, int startHour, int startMin, int endHour, int endMin) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W812Device) {
                Logger.myLog("senddistrub,open:" + open + "starHour:" + startHour + ",startMin:" + startMin + ",endHour:" + endHour + ",endMin:" + endMin);
                ((W812Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W817Device) {
                Logger.myLog("senddistrub,open:" + open + "starHour:" + startHour + ",startMin:" + startMin + ",endHour:" + endHour + ",endMin:" + endMin);
                ((W817Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).set_not_disturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).senddisturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).senddisturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).senddisturb(open, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).senddisturb(open, startHour, startMin, endHour, endMin);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).senddisturb(open, startHour, startMin, endHour, endMin);
            }
        }
    }

    protected void getVersion() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).getVersion();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).getVersion();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).getVersion();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).getVersion();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).getVersion();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).getVersion();
            }
        }
    }

    /**
     * 设置睡眠监测
     */
    protected void set_sleep_setting(boolean isAutoSleep, boolean hasNotif, boolean disturb, int
            testStartTimeH, int testStartTimeM, int
                                             testEndTimeH, int testEndTimeM,
                                     int
                                             disturbStartTimeH, int disturbStartTimeM, int
                                             disturbEndTimeH, int disturbEndTimeM) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_sleep_setting(isAutoSleep, hasNotif, disturb,
                        testStartTimeH, testStartTimeM,
                        testEndTimeH, testEndTimeM,

                        disturbStartTimeH, disturbStartTimeM,
                        disturbEndTimeH, disturbEndTimeM);
            }
        }
    }

    /**
     * 获取睡眠监测
     */
    protected void get_sleep_setting() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_sleep_setting();
            }
        }
    }

    /**
     * 设置久坐提醒
     */
    protected void set_sedentary_time(int time, int startHour, int startMin, int endHour, int endMin) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_sedentary_time(time, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).set_sedentary_time(time, startHour, startMin, endHour, endMin);

            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).set_sedentary_time(time, startHour, startMin, endHour, endMin);

            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).set_sedentary_time(time, startHour, startMin, endHour, endMin);

            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).set_sedentary_time(time, startHour, startMin, endHour, endMin);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).set_sedentary_time(time, startHour, startMin, endHour, endMin);
            }
        }
    }

    /**
     * 获取久坐提醒设置
     */
    protected void get_sedentary_time() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_sedentary_time();
            }
        }
    }

    /**
     * 电话、短信
     */
    protected void send_notification(int type) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).send_notification(type);
            }
        }
    }

    /**
     * 通知
     */
    protected void send_notificationN(String type) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).send_notificationN(type);
            }
        }
    }

    /**
     * 校准
     */
    protected void set_handle(boolean enable) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_handle(enable);
            }
        }
    }

    /**
     * 获取多少天前的24小时数据
     */
    protected void get_daily_record(int day) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_daily_record(day);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).get_daily_record(day);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).get_daily_record(day);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).get_daily_record(day);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).get_daily_record(day);
            }
            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).get_daily_record(day);
            }

            if(currentDevice instanceof W7018Device){
                ((W7018Device) currentDevice).sync_data();
            }
        }
    }

    /**
     * 清除所有24小时数据
     */
    protected void clear_daily_record() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).clear_daily_record();
            }
        }
    }

    /**
     * 产生7天的模拟数据，包括当天
     */
    protected void getTestData() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).getTestData();
            }
        }
    }

    /**
     * 获取锻炼数据
     */
    protected void get_exercise_data() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_exercise_data();
            }
            if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).get_exercise_data();
            }

            if(currentDevice instanceof W560Device){
                ((W560Device) currentDevice).get_exercise_data();
            }
        }
    }



    public void getW560ExerciseData(int num){
        if(mBaseManager != null && mBaseManager.getCurrentDevice() != null){
            BaseDevice baseDevice = mBaseManager.getCurrentDevice();
            if( baseDevice instanceof W560BDevice){
                ((W560BDevice) baseDevice).getExerciseData(num);
            }

            if(baseDevice instanceof W560Device){
                ((W560Device) baseDevice).getExerciseData(num);
            }
        }
    }



    /**
     * 清除锻炼数据
     */
    protected void clear_exercise_data() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).clear_exercise_data();
            }
        }
    }

    /**
     * 恢复出厂
     */
    protected void set_default() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_default();
            }
        }
    }


    protected void set_sn_factory() {
        if (mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_sn_factory();
            }
        }
    }

    protected void set_sn_normalmode(int SN) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_sn_normalmode(SN);
            }
        }
    }

    protected void get_sn_data() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).get_sn_data();
            }
        }
    }

    protected void set_beltname() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).set_beltname();
            }
        }
    }

    protected void test_reset() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).test_reset();
            } else if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).test_reset();
            }
        }
    }

    protected void test_motorled() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).test_motorled();
            }
        }
    }

    protected void stop_test_motorled() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).stop_test_motorled();
            }
        }
    }

    protected void test_handle() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).test_handle();
            }
        }
    }

    protected void test_display() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).test_display();
            }
        }
    }

    protected void test_ohr() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).test_ohr();
            }
        }
    }

    protected void device_to_phone() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).device_to_phone();
            }
        }
    }

    //************************************************思诺嘉手表部分**************************************************//


    //***********************************************睡眠带********************************************//

    /**
     * 获取版本号
     */
    protected void getDeviceVersion() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            currentDevice.getDeviceVersion();
        }
    }


    /**
     * 设置自动开始监听
     * enable	boolean	是否启用自动开始设置，
     * hour	int	检测开始时间，小时（24小时制）
     * minute	int	检测开始时间，分钟
     * repeat	int	配置有效时间，以用周为周期；以二进制位进行表示，示例 ：00000111，从右到左，分别表示周一，周二，周三，如果该位是1，表示当天重复，否则不重复。如127表示，周一到周日重复
     * timeout	int	超时时间,单位：毫秒
     * cb	IDataCallback<Void>	回调接口
     */

    protected void setAutoCollection(boolean enable, int hour, int minute, int repeat) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).setAutoCollection(enable, hour, minute, repeat);
        }
    }


    /**
     * 打开和关闭监测
     *
     * @param enable
     */
    protected void setCollectionEnable(boolean enable) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).setCollectionEnable(enable);
        }
    }

    /**
     * 设备工作状态查询
     * timeout	int	超时时间,单位：毫秒
     * cb	IDataCallback<Byte>	回调接口,1:采集中，0：空闲状态
     */
    protected void getCollectionStatus() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).getCollectionStatus();
        }
    }

    /**
     * 设置开启和关闭实时
     *
     * @param enable
     */
    protected void setRealTimeEnable(boolean enable) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).setRealTimeEnable(enable);
        }
    }

    /**
     * 设置开启和关闭信号强度监测
     *
     * @param enable
     */
    protected void setOriginalEnable(boolean enable) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).setOriginalEnable(enable);
        }
    }

    /**
     * 下载历史监测数据
     * starTime	int	下载数据开始点
     * endTime	int	下载数据结束点
     * sex	int	性别，1:男   0:女
     * timeout	int	超时时间,单位：毫秒
     * cb	IDataCallback<List<HistoryData>>	回调接口，详细返回见HistoryData
     *
     * @param startTime 开始时间 s
     * @param endTime   结束时间 s
     * @param sex       性别  1 男 0 女
     */
    protected void historyDownload(int startTime, int endTime, int sex) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).historyDownload(startTime, endTime, sex);
        }
    }

    /**
     * 取环境参数  提供的demo中暂未使用
     * timeout	int	超时时间,单位：毫秒
     * cb	IDataCallback<EnvironmentData>	回调接口，返回环境参数，详细返回见EnvironmentData
     */
    protected void getEnvironmentalData() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).getEnvironmentalData();
        }
    }

    /**
     * 级设备1
     * crcDes	long	固件校验码
     * crcBin	long	固件校验码
     * file	File 	文件对象
     * timeout	int	超时时间,单位：毫秒
     * cb	IDataCallback<Integer>	回调接口，返回固件升级进度
     */
    protected void upgradeDevice1() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).upgradeDevice1();
        }
    }

    /**
     * 升级设备2
     * crcDes	long	固件校验码
     * crcBin	long	固件校验码
     * is	InputStream 	文件数据流
     * timeout	int	超时时间,单位：毫秒
     * cb	IDataCallback<Integer>	回调接口，返回固件升级进度
     */
    protected void upgradeDevice2() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            ((SleepDevice) currentDevice).upgradeDevice2();
        }
    }


    /**
     * 设置时间格式
     */

    protected void setDeviceFomat(int timeFormat) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).setTimeFormat(timeFormat);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).setTimeFormat(timeFormat);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).setTimeFormat(timeFormat);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).setTimeFormat(timeFormat);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).setTimeFormat(timeFormat);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).setTimeFormat(timeFormat);
            }
        }
    }


    /**
     * 设置目标步数
     */
    protected void setDeviceStepTarget(int step) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).setDeviceGoalStep(step);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).setDeviceGoalStep(step);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).setDeviceGoalStep(step);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).setDeviceGoalStep(step);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).setDeviceGoalStep(step);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).setDeviceGoalStep(step);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).setTargetStep(step);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).setTargetStep(step);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).setTargetStep(step);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).setTargetStep(step);
            }else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).setTargetStep(step);
            }
        }
    }


    //W560读取目标类型
    protected void readW560DeviceGoalType(){
        if(mBaseManager != null && mBaseManager.getCurrentDevice() != null){
            BaseDevice baseDevice = mBaseManager.getCurrentDevice();
            if(baseDevice instanceof  W560Device)
                ((W560Device) baseDevice).readW560DeviceStepGoal();

        }
    }

    /**
     * W560设置目标距离
     */
    protected void setDeviceDistanceTarget(int distance) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).setTargetDistance(distance);
            }
        }
    }

    /**
     * W560设置目标卡路里
     */
    protected void setDeviceCalorieTarget(int calorie) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).setTargetCalorie(calorie);
            }
        }
    }

    /**
     * w311设置用户信息
     */
    protected void setUserInfo() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).set_userinfo();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).set_userinfo();
            }
        }
    }

    /**
     * 设置311的佩戴方式
     *
     * @param isLeft
     */
    protected void setBraceletWear(boolean isLeft) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_wear(isLeft);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_wear(isLeft);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_wear(isLeft);
            }
        }
    }

    protected void setBraceletDisplay(DisplaySet displaySet) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_disPlay(displaySet);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_disPlay(displaySet);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_disPlay(displaySet);
            }
        }
    }

    protected void getBraceletDisplay() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).get_display();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).get_display();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).get_display();
            }
        }
    }

    protected void findBracelet() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            Log.e(TAG,"---baseDevice查找手表="+currentDevice.getDeviceType());


            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).find_bracelet();
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).findWatch();
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).findWatch();
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).findWatch();
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).findWatch();
            }

            else if(currentDevice instanceof  W560BDevice){
                ((W560BDevice) currentDevice).findWatch();
            }

            Logger.myLog(TAG,"---没有找到");
        }
    }

    protected void braceletLostRemind(boolean isOpen) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).lost_to_remind(isOpen);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).lost_to_remind(isOpen);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).lost_to_remind(isOpen);
            }
        }
    }

    protected void braceletGetdentarytime() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).get_sedentary_reminder();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).get_sedentary_reminder();
            }
        }
    }

    protected void braceletSetSedentaryRemind(List<SedentaryRemind> list) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).set_sendentary_reminder(list);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).set_sendentary_reminder(list);
            }
        }
    }

    protected void braceletSetDentaryTime() {

    }

    public void braceletIsOpenRaiseHand(int state) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).setRaise307J(state);
            }
        }
    }

    public void braceletIsOpenRaiseHand(boolean isOpen) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_is_open_raise_hand(isOpen);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_is_open_raise_hand(isOpen);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_is_open_raise_hand(isOpen);
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).set_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).set_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).set_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).set_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).set_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).set_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).set_w526_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).set_w526_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).set_w526_raise_hand(0, 0, 0, 0, 0);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).set_w526_raise_hand(0, 0, 0, 0, 0);
            }
            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).set_w526_raise_hand(0,0,0,0,0);
            }
        }
    }

    public void braceletRaiseHand(int type, int startHour, int startMin, int endHour, int endMin) {

        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).set_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).set_w526_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).set_w526_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).set_w526_raise_hand(type, startHour, startMin, endHour, endMin);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).set_w526_raise_hand(type, startHour, startMin, endHour, endMin);
            }
            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).set_w526_raise_hand(type, startHour, startMin, endHour, endMin);
            }
        }
    }


    public void braceletHrSetting(boolean isOpen, int time) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_hr_setting(isOpen, time);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_hr_setting(isOpen, time);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_hr_setting(isOpen, time);
            }
        }
    }


    public void startHRSwitch(boolean isEnable) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).sendRealHrSwitch(isEnable);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).sendRealHrSwitch(isEnable);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).sendRealHrSwitch(isEnable);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).sendRealHrSwitch(isEnable);
            }else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).sendRealHrSwitch(isEnable);
            }
        }
    }


    public void syncTodayData() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof Watch516Device) {
                ((Watch516Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).syncTodayData();
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).syncTodayData();
            }else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).syncTodayData();
            }
        }
    }

    public void braceletSyncData() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).sync_data();
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).sync_data();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).sync_data();
            } else if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).syncTodayData();
            }
        }
    }

    public void braceletOpenHr(boolean isOpen) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).set_hr_setting(isOpen);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).sendRealHrSwitch(isOpen);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).sendRealHrSwitch(isOpen);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).sendRealHrSwitch(isOpen);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).sendRealHrSwitch(isOpen);
            }else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).sendRealHrSwitch(isOpen);
            }

        }
    }


    public void testSendAppTypeMessage(int type,String title,String msg){
        if(mBaseManager != null && mBaseManager.getCurrentDevice() != null){
            BaseDevice baseDevice = mBaseManager.getCurrentDevice();
            if(baseDevice instanceof W560Device){
                ((W560Device) baseDevice).sendW526Messge(title, msg, type);
            }
            else if(baseDevice instanceof W560BDevice){
                ((W560BDevice) baseDevice).sendW526Messge(title,msg,type);
            }
            if(baseDevice instanceof W557Device){
                ((W557Device) baseDevice).sendW526Messge(title,msg,type);
            }

            if(baseDevice instanceof W812Device){
                ((W812Device) baseDevice).sendMessage(title+":"+msg,type);
            }
            if(baseDevice instanceof W813Device){
                ((W813Device) baseDevice).sendMessage(title+":"+msg,type);
            }

            if(baseDevice instanceof W817Device){
                ((W817Device) baseDevice).sendMessage(title+":"+msg,type);
            }

        }
    }



    public void w311SendPhone(String comming_phone, String name) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).w311_send_phone(comming_phone, name);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).w311_send_phone(comming_phone, name);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).w311_send_phone(comming_phone, name);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).sendW526Messge(comming_phone, name, 1);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).sendW526Messge(comming_phone, name, 1);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).sendW526Messge(comming_phone, name, 1);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).sendW526Messge(comming_phone, name, 1);
            }
        }
    }

    public void w311SenMesg(NotificationMsg msg) {

        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).w311_send_msge(msg);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).w311_send_msge(msg);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).w311_send_msge(msg);
            }
        }
    }


    public void w81SendMeasureOxygen(boolean isStart) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).measureOxygenBlood(isStart);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).measureOxygenBlood(isStart);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).measureOxygenBlood(isStart);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).measureOxygenBlood(isStart);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).measureOxygenBlood(isStart);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).measureOxygenBlood(isStart);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).meassureOxy(isStart);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).meassureOxy(isStart);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).meassureOxy(isStart);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).meassureOxy(isStart);
            }

            if(currentDevice instanceof W7018Device){
                ((W7018Device) currentDevice).measureOxygenBlood(isStart);
            }
        }
    }

    public void w81SendMeasureOneceHrData(boolean isStart) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            Logger.myLog(TAG,"--------测量血压="+(currentDevice instanceof W560BDevice));
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).measureOnceHrData(isStart);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).measureOnceHrData(isStart);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).measureOnceHrData(isStart);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).measureOnceHrData(isStart);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).measureOnceHrData(isStart);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).measureOnceHrData(isStart);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).meassureOneHr(isStart);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).meassureOneHr(isStart);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).meassureOneHr(isStart);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).meassureOneHr(isStart);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).meassureOneHr(isStart);
            }

            if(currentDevice instanceof W7018Device){
                ((W7018Device) currentDevice).measureOnceHrData(isStart);
            }
        }
    }

    public void queryWatchFace() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).queryWatchFace();
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).queryWatchFace();
            }else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).queryWatchFace();
            }
        }
    }

    public void queryAlarList() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).w81QeryAlarmList();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).w81QeryAlarmList();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).w81QeryAlarmList();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).w81QeryAlarmList();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).w81QeryAlarmList();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).w81QeryAlarmList();
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).getAlarmList();
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).getAlarmList();
            } else if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).getAlarmList();
            }
        }
    }

    public void w81SendMeasureBloodPressure(boolean isStart) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).measureBloodPressure(isStart);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).measureBloodPressure(isStart);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).measureBloodPressure(isStart);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).measureBloodPressure(isStart);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).measureBloodPressure(isStart);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).measureBloodPressure(isStart);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).meassureBlood(isStart);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).meassureBlood(isStart);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).meassureBlood(isStart);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).meassureBlood(isStart);
            }

            if(currentDevice instanceof W7018Device){
                ((W7018Device) currentDevice).measureBloodPressure(isStart);
            }
        }
    }

    public void w81W526Message(String title, String message, int messageType) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).sendW526Messge(title, message, messageType);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).sendW526Messge(title, message, messageType);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).sendW526Messge(title, message, messageType);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).sendW526Messge(title, message, messageType);
            }
            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).sendW526Messge(title,message,messageType);
            }
        }
    }

    public void w81SendMessage(String message, int messageType) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).sendMessage(message, messageType);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).sendMessage(message, messageType);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).sendMessage(message, messageType);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).sendMessage(message, messageType);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).sendMessage(message, messageType);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).sendMessage(message, messageType);
            }
        }
    }

    public void deviceSwitchCameraView() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).showSwitchCameraView();
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).showSwitchCameraView();
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).showSwitchCameraView();
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).showSwitchCameraView();
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).showSwitchCameraView();
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).showSwitchCameraView();
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).sendphoto();
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).sendphoto();
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).sendphoto();
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).sendphoto();
            }
            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).sendphoto();
            }
        }
    }


    public void deviceOhterMessageSwitch(boolean enable) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).sendOtherMessageSwitch(enable);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).sendOtherMessageSwitch(enable);
            }
        }
    }

    public void w311SetAlarmList(ArrayList<AlarmEntry> list) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W311Device) {
                ((W311Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).setAlarmList(list);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).setAlarmList(list);
            }
        }
    }


    /**
     * -- 520手环设置 -
     **/

    public void w520SetDial(int enable) {
        Logger.myLog("w520SetDial" + mBaseManager.getCurrentDevice());
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).w520SetDial(enable);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).w520SetDial(enable);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).w520SetDial(enable);
            }
        }
    }


    public void setWeather(WristbandData weather, List<WristbandForecast> list, String city) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            if (TextUtils.isEmpty(city)) {
                city = "123";
            }
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W812Device) {
                ((W812Device) currentDevice).setTodayWeather(weather, city);
                ((W812Device) currentDevice).set15DayWeather(list);

            } else if (currentDevice instanceof W817Device) {
                ((W817Device) currentDevice).setTodayWeather(weather, city);
                ((W817Device) currentDevice).set15DayWeather(list);

            } else if (currentDevice instanceof W813Device) {
                ((W813Device) currentDevice).setTodayWeather(weather, city);
                ((W813Device) currentDevice).set15DayWeather(list);
            } else if (currentDevice instanceof W819Device) {
                ((W819Device) currentDevice).setTodayWeather(weather, city);
                ((W819Device) currentDevice).set15DayWeather(list);
            } else if (currentDevice instanceof W910Device) {
                ((W910Device) currentDevice).setTodayWeather(weather, city);
                ((W910Device) currentDevice).set15DayWeather(list);
            } else if (currentDevice instanceof W814Device) {
                ((W814Device) currentDevice).setTodayWeather(weather, city);
                ((W814Device) currentDevice).set15DayWeather(list);
            } else if (currentDevice instanceof W520Device) {
                ((W520Device) currentDevice).setWeather(weather, list);
            } else if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).setWeather(weather, list);
            } else if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).setWeather(weather, list);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).setWeather(weather, list);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).setWeather(weather, list);
            } else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).setWeather(weather, list);
            }

            else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).setWeather(weather,list);
            }

        }
    }

    //测量体温
    protected void starTempValue(boolean isStart) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).startTemp(isStart);
            }

            if(currentDevice instanceof W7018Device){
                ((W7018Device) currentDevice).startTemp(isStart);
            }
        }
    }

    protected void getRealHrSwitch() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();

            Log.e("currentDevice", "" + currentDevice);


            if (currentDevice instanceof W560Device ) {
                ((W560Device) currentDevice).get_general();
                Log.e("currentDevice", "W560Device" + currentDevice);
                //((W560Device) currentDevice).getRealHrSwitch();
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).getRealHrSwitch();
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).getRealHrSwitch();
            } else if(currentDevice instanceof W560BDevice){
                ((W560BDevice) currentDevice).get_general();
            }

            else if (currentDevice instanceof W311Device) {

            }
        }
    }

    protected void setMaxHrRemind(int hrValue) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).setMaxHrRemid(hrValue);
            }
        }
    }

    protected void startOrEndRope(int type) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).startOrEndRopeSport(type);
            }
        }
    }

    protected void setRopeType(int type, int min, int sec, int number, int pk) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).setRopeType(type, min, sec, number, pk);
            }
        }
    }

    protected void setRopeType(int type) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).setRopeType(type);
            }
        }
    }

    protected void getRopeState() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof S002BDevice) {
                ((S002BDevice) currentDevice).getRopeState();
            }
        }
    }

    protected void getSleepData() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).getSleepData();
            }
        }
    }

    protected void sendSleepData(AutoSleep autoSleep) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W307JDevice) {
                ((W307JDevice) currentDevice).setSleepData(autoSleep);
            }
        }
    }

    //W526新增
    protected void setBacklightAndScreenLeve(int leve, int time) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W526Device) {
                ((W526Device) currentDevice).setWatchbacklight(leve, time);
            } else if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).setWatchbacklight(leve, time);
            } else if (currentDevice instanceof W812BDevice) {
                ((W812BDevice) currentDevice).setWatchbacklight(leve, time);
            }
            else if (currentDevice instanceof W560Device) {
                ((W560Device) currentDevice).setWatchbacklight(leve, time);
            }
            else if (currentDevice instanceof W560BDevice) {
                ((W560BDevice) currentDevice).setWatchbacklight(leve, time);
            }
        }
    }

    //W557新增
    protected void setTempSum(int value) {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).setTempSub(value);
            }
        }
    }

    protected void getTempSub() {
        if (mBaseManager != null && mBaseManager.getCurrentDevice() != null) {
            BaseDevice currentDevice = mBaseManager.getCurrentDevice();
            if (currentDevice instanceof W557Device) {
                ((W557Device) currentDevice).getTempSub();
            }
        }
    }


    //设置音乐
    protected void setMusicData(String mName,String allTime,String currTime){
        if(mBaseManager != null && mBaseManager.getCurrentDevice() != null){
            BaseDevice baseDevice = mBaseManager.getCurrentDevice();
            if(baseDevice instanceof W560Device){
                ((W560Device) baseDevice).setMusic(mName,allTime,currTime);
            }
        }
    }

}
