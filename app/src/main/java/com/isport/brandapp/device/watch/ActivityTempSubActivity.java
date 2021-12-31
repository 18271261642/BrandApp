package com.isport.brandapp.device.watch;

import android.view.View;
import android.widget.TextView;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.watch.DeviceSendCmdResult;
import com.isport.blelibrary.result.impl.watch.WatchTempSubResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;

import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.commonview.UserDialogSetting;
import brandapp.isport.com.basicres.commonview.UserDialogView;


/**
 * 温度校准
 */
public class ActivityTempSubActivity extends BaseTitleActivity implements UserDialogView {
    TextView tv_save;
    TextView tv_value;
    TextView tv_reset;
    UserDialogSetting userDialogSetting;
    int currentTempValue;
    int desTempValue;

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_set_temp_sub;
    }

    @Override
    protected void initView(View view) {
        tv_save = view.findViewById(R.id.tv_save_value);
        tv_value = view.findViewById(R.id.tv_value);
        tv_reset = view.findViewById(R.id.tv_reset);

    }

    @Override
    protected void initData() {
        ISportAgent.getInstance().registerListener(mBleReciveListener);
        titleBarView.setTitle(R.string.temperature_cali);
        userDialogSetting = new UserDialogSetting(this);
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().requestW557Ble(BleRequest.QUERY_TEMP_SUB);
        }

    }

    @Override
    protected void initEvent() {

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                isBack();
            }

            @Override
            public void onRightClicked(View view) {
                saveData();
            }
        });
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDialogSetting.setPopupWindowTemp(ActivityTempSubActivity.this, tv_reset, currentTempValue);
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // ToastUtil.showTextToast("点击了保存");

                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    return;
                }
                if (!AppConfiguration.hasSynced) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.sync_data));
                    return;
                }
                if (AppConfiguration.isConnected) {
                    if (desTempValue != currentTempValue) {
                        currentTempValue = desTempValue;
                        ISportAgent.getInstance().requestW557Ble(BleRequest.SET_TEMP_SUB, desTempValue);
                    } else {
                        finish();
                    }
                }
            }
        });
    }

    public void saveData() {
        if (!AppConfiguration.isConnected) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
            return;
        }
        if (!AppConfiguration.hasSynced) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.sync_data));
            return;
        }
        if (AppConfiguration.isConnected) {
            if (desTempValue != currentTempValue) {
                currentTempValue = desTempValue;
                ISportAgent.getInstance().requestW557Ble(BleRequest.SET_TEMP_SUB, desTempValue);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void initHeader() {

    }

    @Override
    public void onBackPressed() {
        isBack();
    }

    private void isBack() {
        if (currentTempValue != desTempValue) {
            PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.not_save_alert), context, getResources().getString(R.string.edit_info_not_save), getResources().getString(phone.gym.jkcq.com.socialmodule.R.string.edit_info_save), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    // saveUserInfo();
                    saveData();
                }

                @Override
                public void cancel() {
                    finish();
                }
            }, false);
        } else {
            finish();
        }
    }


    private BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {

        }

        @Override
        public void setDataSuccess(String s) {


        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.DEVICE_TEMP_SUB:
                        WatchTempSubResult mResult1 = (WatchTempSubResult) mResult;
                        currentTempValue = mResult1.getTempValue();
                        desTempValue=currentTempValue;
                        setValue(currentTempValue);

                        // TODO: 2018/10/17 更新列表电量
                        break;
                    case IResult.DEVICE_SEND_CMD:
                        DeviceSendCmdResult mRsult = (DeviceSendCmdResult) mResult;
                        if (mRsult.isSuccess() == 0) {
                            //ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.save_success));
                            finish();
                        } else {
                            //ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.sa));
                        }
                        break;

                }
        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {

        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {

        }
    };

    @Override
    public void dataSetSuccess(String select, String data) {
        desTempValue = Integer.parseInt(data);
        setValue(desTempValue);
    }

    @Override
    public void onRespondError(String message) {

    }

    public void setValue(int value) {
        StringBuffer buffer = new StringBuffer();
        if (value >= 0) {
            buffer.append("+");
        } else {
            buffer.append("-");
        }
        int currentValue = Math.abs(value);
        int yu = currentValue % 10;

        int ge = currentValue / 10;
        buffer.append(ge).append(".").append(yu).append("\n").append("℃");
        tv_value.setText(buffer.toString());
    }
}
