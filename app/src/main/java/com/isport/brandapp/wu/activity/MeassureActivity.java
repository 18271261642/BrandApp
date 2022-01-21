package com.isport.brandapp.wu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import androidx.annotation.NonNull;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import pl.droidsonroids.gif.GifImageView;

public class MeassureActivity extends BaseActivity {
    TextView btn_measure, tv_title;
    GifImageView gifImageView;
    int measueType = JkConfiguration.DeviceMeasureType.hr;



    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                handler.removeMessages(0x00);
                finishMeasure();
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_measusre;
    }

    @Override
    protected void initView(View view) {
        btn_measure = view.findViewById(R.id.btn_measure);
        tv_title = view.findViewById(R.id.tv_title);
        gifImageView = view.findViewById(R.id.iv_gif);
    }

    private void getIntentValue() {
        measueType = getIntent().getIntExtra("device_type", 0);
        int measureDuring ;
        if(Objects.requireNonNull(AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_F18)).getDeviceType() == 7018){
            measureDuring = 60 * 1000;
        }else{
            measureDuring = 30 * 1000;
        }
        handler.sendEmptyMessageDelayed(0x00,measureDuring);
    }

    @Override
    protected void initData() {
        getIntentValue();
        ISportAgent.getInstance().registerListener(mBleReciveListener);
        try {
            if (measueType == JkConfiguration.DeviceMeasureType.hr) {
                gifImageView.setImageResource(R.drawable.bg_measure_hr);
                tv_title.setText(this.getResources().getString(R.string.messure_hr));
            } else if (measueType == JkConfiguration.DeviceMeasureType.blood) {
                gifImageView.setImageResource(R.drawable.bg_measure_blood);
                tv_title.setText(this.getResources().getString(R.string.messure_blood));
            } else if (measueType == JkConfiguration.DeviceMeasureType.oxygen) {
                gifImageView.setImageResource(R.drawable.bg_measure_oxygen);
                tv_title.setText(this.getResources().getString(R.string.messure_oxy));
            } else if (measueType == JkConfiguration.DeviceMeasureType.temp) {
                gifImageView.setImageResource(R.drawable.bg_measure_oxygen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void initEvent() {

        btn_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMeasure();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0x00);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);

    }

    @Override
    protected void initHeader() {
        //  StatusBarCompat.setStatusBarColor(this, getResources().getColor(com.isport.brandapp.basicres.R.color.common_view_color));
    }


    private BleReciveListener mBleReciveListener = new BleReciveListener() {

        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {

            if (isConn) {

            } else {
                ToastUtils.showToast(UIUtils.getContext(), getString(R.string.measure_fail));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.measure_end));
                finish();
            }

        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    //设备测量结果成功
                    case IResult.DEVICE_MESSURE:
                        try {
                            DeviceMessureDataResult deviceMessureDataResult = (DeviceMessureDataResult) mResult;
                            switch (deviceMessureDataResult.getMessureType()) {
                                case DeviceMessureData.measure_oxygen:
                                    Logger.myLog("measure_oxygen success");
                                    finish();
                                    break;
                                case DeviceMessureData.measure_bloodpre:
                                    finish();
                                    Logger.myLog("measure_bloodpre success");
                                    break;
                                case DeviceMessureData.measure_once_hr:
                                    finish();
                                    Logger.myLog("measure_once_hr success");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_no);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_no, R.anim.anim_bottom_out);

    }

    private void finishMeasure() {
        if (AppConfiguration.isConnected) {
            if (measueType == JkConfiguration.DeviceMeasureType.hr) {
                ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_ONECE_HR_DATA, false);
            } else if (measueType == JkConfiguration.DeviceMeasureType.blood) {
                ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_BLOODPRE, false);
            } else if (measueType == JkConfiguration.DeviceMeasureType.oxygen) {
                ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_OXYGEN, false);
            }
            finish();
        } else {
            Toast.makeText(this, getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.measure_end));
    }

    @Override
    public void onBackPressed() {
        ToastUtils.showToast(UIUtils.getContext(), getString(R.string.measureing));
        //Toast.makeText(this, , Toast.LENGTH_LONG).show();
        return;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).init();
    }
}
