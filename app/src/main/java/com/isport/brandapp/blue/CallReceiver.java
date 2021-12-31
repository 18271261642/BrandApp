package com.isport.brandapp.blue;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

/**
 * @author Created by Marcos Cheng on 2016/12/30.
 * if you run on android 7.0+,CallListener maybe not work,you can use CallReceiver instead,
 * if you want compatiable you need to use both {@link CallReceiver} and {@link CallListener}
 */

public class CallReceiver extends BroadcastReceiver {

    public final static String CALL_PATH = "cjm_call_path";
    private static final String TAG = "CallListener";
    private boolean isHandup = false;
    private boolean isCalling = false;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)
            return;
        Log.e(TAG,"----电话action="+action);
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            if (Build.VERSION.SDK_INT >= 24) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "checkSelfPermission  1111");
                    return;
                }
            }
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.e(TAG, "999999999999999" + incomingNumber);
            if (state != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    //电话空闲
                    if (isHandup && !isCalling) {
                        isHandup = false;
                    }
                    isCalling = false;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    isHandup = true;
                    Log.e(TAG, "999999999999999" + incomingNumber);
                    if (!TextUtils.isEmpty(incomingNumber)) {
                        //来电了，响铃中
                        ContentUtils.sendCall(incomingNumber, context);
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Log.e(TAG, "999999999999999 EXTRA_STATE_OFFHOOK" );
                    if (isHandup) {
                        isCalling = true;
                    }
                    if (incomingNumber != null) {

                    }
                }
            }
        }
    }


}
