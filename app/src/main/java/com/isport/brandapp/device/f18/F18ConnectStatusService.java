package com.isport.brandapp.device.f18;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * 监听F18连接状态的变化，断连后自动连接
 * Created by Admin
 * Date 2022/1/20
 */
public class F18ConnectStatusService extends Service {


    private final F18StatusBinder f18StatusBinder = new F18StatusBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
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
        F18ConnectStatusService getService(){
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
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){ //手机蓝牙状态变化
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if(state == BluetoothAdapter.STATE_OFF){    //蓝牙关闭

                }
                if(state == BluetoothAdapter.STATE_ON){     //蓝牙打开

                }
            }
        }
    };
}
