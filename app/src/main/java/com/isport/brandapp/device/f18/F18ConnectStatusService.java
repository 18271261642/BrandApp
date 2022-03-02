package com.isport.brandapp.device.f18;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.interfaces.CusScannResultListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.util.AppSP;

import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.commonutil.TokenUtil;

/**
 * 监听F18连接状态的变化，断连后自动连接
 * Created by Admin
 * Date 2022/1/20
 */
public class F18ConnectStatusService extends Service {

    private static final String TAG = "F18ConnectStatusService";

    public  IBinder f18StatusBinder = new F18StatusBinder();


    private final Handler handler = new Handler(Looper.getMainLooper()){};

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(Watch7018Manager.F18_CONNECT_STATUS);
        intentFilter.addAction(Watch7018Manager.F18_DIS_CONNECTED_STATUS);
        registerReceiver(broadcastReceiver,intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return f18StatusBinder;
    }


    public class F18StatusBinder extends Binder{
        public F18ConnectStatusService getService(){
            return F18ConnectStatusService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;

            Log.e(TAG,"-----------连接action------="+action);
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){ //手机蓝牙状态变化
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if(state == BluetoothAdapter.STATE_OFF){    //蓝牙关闭

                }
                if(state == BluetoothAdapter.STATE_ON){     //蓝牙打开
                    String saveMac = AppSP.getString(F18ConnectStatusService.this,AppSP.F18_SAVE_MAC,"");
                    Log.e(TAG,"-----------蓝牙打开------="+saveMac);
                    if(TextUtils.isEmpty(saveMac))
                        return;
                    autoScann(saveMac);
                }
            }

            if(action.equals(Watch7018Manager.F18_CONNECT_STATUS)){ //连接成功
                Log.e(TAG,"-----------连接成功------");
                String connBleMac = intent.getStringExtra(Watch7018Manager.ACTION_KEY_PARAMS);
                AppSP.putString(context, AppSP.F18_SAVE_MAC, connBleMac);
                AppConfiguration.isConnected = true;
                closeAutoScann("连接成功");
            }

            if(action.equals(Watch7018Manager.F18_DIS_CONNECTED_STATUS)){
                Log.e(TAG,"-----------断开连接------");
                AppConfiguration.isConnected = false;
               // Watch7018Manager.getWatch7018Manager().disConnectDevice();
                String saveMac = AppSP.getString(F18ConnectStatusService.this,AppSP.F18_SAVE_MAC,null);
                Log.e(TAG,"-----------断开连接saveMac------="+saveMac);
                if(TextUtils.isEmpty(saveMac))
                    return;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoScann(saveMac);
                    }
                }, 1000);

            }
        }
    };


    public void autoScann(String macAddress){
        Log.e(TAG,"----开启自动搜索="+macAddress);
        if(TextUtils.isEmpty(macAddress))
            return;
        if( Watch7018Manager.getWatch7018Manager().isConnected())
            return;
        AppSP.putString(this,AppSP.F18_SAVE_MAC,null);
        ISportAgent.getInstance().scanDevice(new CusScannResultListener() {
            @Override
            public void onCusScanResult(BluetoothDevice bluetoothDevice, byte[] record, int rssi) {
                Log.e(TAG,"----自动搜索结果="+bluetoothDevice.getAddress()+"目标设备="+macAddress);
                if(macAddress.equals(bluetoothDevice.getAddress())){        //搜索到目标设备了
                    closeAutoScann("找到目标设备");
                    Log.e(TAG,"----搜索到目标设备了="+bluetoothDevice.getAddress()+"目标设备="+macAddress);
                    BaseDevice baseDevice = create7018Device(bluetoothDevice.getName(),bluetoothDevice.getAddress());
                    String userId = TokenUtil.getInstance().getPeopleIdStr(F18ConnectStatusService.this);
                    Watch7018Manager.getWatch7018Manager().setUserId(userId);
                    ISportAgent.getInstance().connect(baseDevice,true,true);
                }
            }

            @Override
            public void onCusComplete() {

            }
        },Integer.MAX_VALUE);
    }


    public void closeAutoScann(String tag){
        Log.e(TAG,"------停止自动搜索---="+tag);
        ISportAgent.getInstance().stopLeScan(0);
        ISportAgent.getInstance().stopLeScan();
    }



    public BaseDevice create7018Device(String name, String mac){
        String newName;
        if(name.contains("BL")){
            newName = "BL-F18X-"+mac.replace(":","");
        }else {
            newName = "F18-"+mac.replace(":","");
        }
        return new W7018Device(newName,mac);
    }
}
