package com.isport.blelibrary.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Admin
 * Date 2022/2/12
 */
public interface CusScannResultListener {

    void onCusScanResult(BluetoothDevice bluetoothDevice,byte[] record,int rssi);

    void onCusComplete();
}
