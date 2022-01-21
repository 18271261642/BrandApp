package com.isport.brandapp.wu.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
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
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.mvp.BpHistoryView;
import com.isport.brandapp.wu.mvp.presenter.BpHistoryPresenter;
import com.isport.brandapp.wu.view.BPTrendView;
import com.isport.brandapp.wu.view.BpBarView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import bike.gymproject.viewlibray.AkrobatNumberTextView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * 单次测量血压页面
 */
public class BPResultActivity extends BaseMVPActivity<BpHistoryView, BpHistoryPresenter> implements BpHistoryView {

    private ArrayList<BPInfo> mDataList = new ArrayList<>();
    private BPInfo mCurrentInfo;
    private long lastTimestamp;

    private BPTrendView trendview_bp;
    private BpBarView bp_barview;
    private AkrobatNumberTextView tv_bp_sys;
    private AkrobatNumberTextView tv_bp_dias;
    private TextView tv_bp_time;
    private TextView tv_title;
    private TextView btn_measure;


    private ImageView iv_back;
    private ImageView iv_history;

    private boolean isMeasure = false;
    private W81DeviceDataModelImp mW81DeviceDataModelImp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bpresult;
    }

    @Override
    protected void initView(View view) {
        // StatusBarCompat.setStatusBarColor(this, getResources().getColor(com.isport.brandapp.basicres.R.color.common_view_color));
//        setTranslucentStatus(getResources().getColor(R.color.common_view_color));
        trendview_bp = findViewById(R.id.trendview_bp);
        tv_bp_sys = findViewById(R.id.tv_bp_sys);
        tv_bp_dias = findViewById(R.id.tv_bp_dias);
        tv_bp_time = findViewById(R.id.tv_bp_time);
        btn_measure = findViewById(R.id.btn_measure);

        btn_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMeasure) {
                    startMeasure();
                } else {
                    finishMeasure();
                }
            }
        });
        bp_barview = findViewById(R.id.bp_barview);
    }


    @Override
    protected void initHeader() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.bp));
        iv_history = findViewById(R.id.iv_history);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BPResultActivity.this, BPListActivity.class));
            }
        });
    }

    @Override
    protected void initData() {
        mW81DeviceDataModelImp = new W81DeviceDataModelImp();
        mActPresenter.getBpNumData();
        ISportAgent.getInstance().registerListener(mBleReciveListener);
    }

    @Override
    protected void initEvent() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    private void setData() {
        tv_bp_time.setText(TimeUtils.getTimeByyyyyMMddhhmmss(mCurrentInfo.getTimestamp()));
        bp_barview.setProgress(mCurrentInfo.getSpValue(), mCurrentInfo.getDpValue());
        tv_bp_sys.setText("" + mCurrentInfo.getSpValue());
        tv_bp_dias.setText("" + mCurrentInfo.getDpValue());
    }


    private void setDataFromLocal() {
        mCurrentInfo = mW81DeviceDataModelImp.getBloodPressureLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        if (lastTimestamp == mCurrentInfo.getTimestamp().longValue()) {
            return;
        }
        lastTimestamp = mCurrentInfo.getTimestamp().longValue();

        setData();
        trendview_bp.setLocalData(mCurrentInfo);
    }

    private void startMeasure() {
        if (AppConfiguration.isConnected) {
            isMeasure = true;
            btn_measure.setText(R.string.stop_measure);
            ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_BLOODPRE, true);
            ActivitySwitcher.goMeasureActivty(this, JkConfiguration.DeviceMeasureType.blood);
        } else {
            Toast.makeText(this, getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.measure_end:
                isMeasure = false;
                btn_measure.setText(R.string.start_measure);
                //setDataFromLocal();
                Logger.myLog("measure_bloodpre success");
                break;
        }
    }


    private void finishMeasure() {
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_BLOODPRE, false);
            isMeasure = false;
            btn_measure.setText(R.string.start_measure);
        } else {
            Toast.makeText(this, getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected BpHistoryPresenter createPresenter() {
        return new BpHistoryPresenter();
    }

    @Override
    public void getBpHistorySuccess(List<BPInfo> info) {
        if (info != null && info.size() > 0) {
            trendview_bp.setData(info);
            mCurrentInfo = info.get(0);
            lastTimestamp = mCurrentInfo.getTimestamp().longValue();
            setData();
        } else {
            tv_bp_time.setText("--");
            tv_bp_sys.setText("--");
            tv_bp_dias.setText("--");
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
                    //设备测量结果成功
                    case IResult.DEVICE_MESSURE:
                        try {
                            DeviceMessureDataResult deviceMessureDataResult = (DeviceMessureDataResult) mResult;
                            switch (deviceMessureDataResult.getMessureType()) {
                                case DeviceMessureData.measure_bloodpre:
                                    isMeasure = false;
                                    btn_measure.setText(R.string.start_measure);
                                    setDataFromLocal();
                                    Logger.myLog("measure_bloodpre success");
                                    break;
                                case DeviceMessureData.measure_oxygen:
                                    Logger.myLog("measure_oxygen success");
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
    protected void initImmersionBar() {
        // super.initImmersionBar();
        //  ImmersionBar.with(this).statusBarDarkFont(true).statusBarColor(UIUtils.getColor(R.color.common_bg)).init();
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }
}
