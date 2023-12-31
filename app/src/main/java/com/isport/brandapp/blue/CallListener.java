package com.isport.brandapp.blue;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.R;
import com.isport.brandapp.util.DeviceTypeUtil;

import java.util.Locale;

import androidx.core.app.ActivityCompat;
import brandapp.isport.com.basicres.commonutil.UIUtils;


public class CallListener extends PhoneStateListener {


    public final static String CALL_PATH = "cjm_call_path";

    private static final String TAG = "CallListener";
    private boolean isHandup = false;
    private boolean isCalling = false;
    private Context context;

    //
    public CallListener(Context context) {
        super();
        this.context = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if (Build.VERSION.SDK_INT >= 24) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager
                    .PERMISSION_GRANTED) {
                Log.e(TAG, "checkSelfPermission");
                return;
            }
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (isHandup && !isCalling) {
                    isHandup = false;
                }
                isCalling = false;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                isHandup = true;
//                if (incomingNumber != null && entry.isAllowCall()) {
                if (!TextUtils.isEmpty(incomingNumber)) {
                    String buildName = Build.MANUFACTURER;
                    if(buildName!= null && buildName.toLowerCase(Locale.ROOT).equals("samsung"))
                        return;
                    //来电了，响铃中
                    ContentUtils.sendCall(incomingNumber, context);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Logger.myLog("CALL_STATE_OFFHOOK");
                if (isHandup) {
                    isCalling = true;
                }
                BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                if (device == null) {
                    return;
                }
                if(DeviceTypeUtil.isContainF18(device.getDeviceType()))
                  Watch7018Manager.getWatch7018Manager().sendNoticeToDevice((byte) 0x02,"", UIUtils.getString(R.string.incomingNumber));
                break;
        }
    }



}
