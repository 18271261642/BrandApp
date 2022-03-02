package com.isport.brandapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.R;
import com.isport.brandapp.util.DeviceTypeUtil;

import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;

public class UnBindDeviceDialog {
    private final LinearLayout ll_needsync;
    private final LinearLayout ll_not_needsync;
    private final LinearLayout ll_background;
    private final TextView tv_direct_unbind_not_needsync;
    private final TextView tv_cancle_not_needsync;
    private final TextView tv_tips;
    private Activity mActivity;
    private AlertDialog dialog;
    private TextView tvDirectUnbind, tvSynUnbind, tvCancle;
    private View lineSynUnbind;
    private UnbindStateCallBack callBack;

    public UnBindDeviceDialog(Activity activity, int deviceType, boolean showSynUnbind, final UnbindStateCallBack callBack, int type) {

        Logger.myLog("UnBindDeviceDialog deviceType=" + deviceType + "type=" + type);

        this.mActivity = activity;
        dialog = new AlertDialog.Builder(mActivity, R.style.alert_dialog).create();
        this.callBack = callBack;
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_unbind_devicet);
        tvDirectUnbind = dialog.getWindow().findViewById(R.id.tv_direct_unbind);
        tvSynUnbind = dialog.getWindow().findViewById(R.id.tv_syn_unbind);
        lineSynUnbind = dialog.getWindow().findViewById(R.id.line_syn_unbind);
        tv_tips = dialog.getWindow().findViewById(R.id.tv_tips);
        ll_needsync = dialog.getWindow().findViewById(R.id.ll_needsync);
        ll_background = dialog.getWindow().findViewById(R.id.ll_background);
        ll_not_needsync = dialog.getWindow().findViewById(R.id.ll_not_needsync);
        tv_direct_unbind_not_needsync = dialog.getWindow().findViewById(R.id.tv_direct_unbind_not_needsync);
        tv_cancle_not_needsync = dialog.getWindow().findViewById(R.id.tv_cancle_not_needsync);
        if (showSynUnbind) {
            ll_needsync.setVisibility(View.VISIBLE);
            ll_not_needsync.setVisibility(View.GONE);
            if (JkConfiguration.DeviceType.WATCH_W516 == type) {
                //取出当前绑定的设备名称
                if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    String deviceName = DeviceTypeUtil.getRopeCurrentBindDeviceName();
                    Logger.myLog("currentDeviceName ROPE_SKIPPING:" + deviceName);
                    tv_tips.setText(activity.getString(R.string.unpair_other_device_notice, deviceName));
                } else {
                    String deviceName = DeviceTypeUtil.getCurrentBindDeviceName();
                    Logger.myLog("currentDeviceName:" + deviceName);
                    tv_tips.setText(activity.getString(R.string.unpair_other_device_notice, deviceName));
                }
            } else if (JkConfiguration.DeviceType.SLEEP == type) {
                tv_tips.setText(UIUtils.getString(R.string.unpair_notice));
            } else {
                if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    String deviceName = DeviceTypeUtil.getRopeCurrentBindDeviceName();
                    Logger.myLog("currentDeviceName ROPE_SKIPPING:" + deviceName);
                    tv_tips.setText(activity.getString(R.string.unpair_other_device_notice, deviceName));
                } else {
                    String deviceName = DeviceTypeUtil.getCurrentBindDeviceName();
                    Logger.myLog("currentDeviceName:" + deviceName);
                    tv_tips.setText(activity.getString(R.string.unpair_other_device_notice, deviceName));
                }
               /* String deviceName = DeviceTypeUtil.getCurrentBindDeviceName();
                Logger.myLog("currentDeviceName:" + deviceName);
                tv_tips.setText(activity.getString(R.string.unpair_other_device_notice, deviceName));*/
            }
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtils.dip2px(mActivity, 300), DisplayUtils.dip2px(mActivity, 120));
            ll_background.setLayoutParams(layoutParams);
            if (JkConfiguration.DeviceType.WATCH_W516 == type) {
                tv_tips.setText(UIUtils.getString(R.string.unpair_notice));
            } else if (JkConfiguration.DeviceType.SLEEP == type) {
                tv_tips.setText(UIUtils.getString(R.string.unpair_notice));
            } else {
                tv_tips.setText(UIUtils.getString(R.string.unpair_notice));
            }
        }
        tvCancle = dialog.getWindow().findViewById(R.id.tv_cancle);
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (callBack != null) {
                    callBack.cancel();
                }
            }
        });
        tv_cancle_not_needsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (callBack != null) {
                    callBack.cancel();
                }
            }
        });
        tvDirectUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (callBack != null) {
                    callBack.dirctUnbind();
                }
            }
        });
        tv_direct_unbind_not_needsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (callBack != null) {
                    //需要判断是否连接蓝牙
                    callBack.dirctUnbind();
                }
            }
        });
        tvSynUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (callBack != null && AppUtil.isOpenBle()) {
                    SyncCacheUtils.isUnbind = true;
                    callBack.synUnbind();
                } else {
                    ToastUtils.showToast(mActivity, UIUtils.getString(R.string.bluetooth_is_not_enabled));
                }
            }
        });


    }


}
