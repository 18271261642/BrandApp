package com.isport.brandapp.wu.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.db.parse.DeviceDataSave;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import com.isport.brandapp.device.f18.adapter.SignalHeartAdapter;
import com.isport.brandapp.device.scale.view.OnceHrBarView;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.wu.bean.DrawRecDataBean;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.mvp.OnceHrHistoryView;
import com.isport.brandapp.wu.mvp.presenter.OnceHrHistoryPresenter;
import com.isport.brandapp.wu.util.HeartRateConvertUtils;
import com.isport.brandapp.wu.view.OxyTrendView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.UserUtils;

/**
 * 单次测量心率
 */
public class OnceHrDataResultActivity extends BaseMVPActivity<OnceHrHistoryView, OnceHrHistoryPresenter> implements OnceHrHistoryView {

    private OnceHrInfo mCurrentInfo;
    private long lastTimestamp;

    private OxyTrendView trendview_oxy;
    private TextView tv_oxy_value;
    private TextView tv_time;
    private TextView btn_measure;
    private TextView tv_title;
    private TextView tv_percent;
    private ImageView iv_back;
    private ImageView iv_history;
    private OnceHrBarView onceHrBarView;
    private UserInfoBean userInfoBean;
    private int age;
    private String sex;


    EditText etValue;
    Button btn_add;


    private boolean isMeasure = false;
    private W81DeviceDataModelImp mW81DeviceDataModelImp;


    private RecyclerView signalRecyclerView;
    private SignalHeartAdapter signalHeartAdapter;
    private List<DrawRecDataBean> singleList;

    private TextView onceHeartGuidTv;

    private ImageView htGuidImg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_once_hr_result;
    }

    @Override
    protected void initView(View view) {

        htGuidImg = findViewById(R.id.htGuidImg);
        onceHeartGuidTv = findViewById(R.id.onceHeartGuidTv);
        signalRecyclerView = findViewById(R.id.signalRecyclerView);

        etValue = findViewById(R.id.et_value);
        btn_add = findViewById(R.id.btn_add);

        trendview_oxy = findViewById(R.id.trendview_oxy);
        tv_oxy_value = findViewById(R.id.tv_oxy_value);
        tv_time = findViewById(R.id.tv_time);
        tv_percent = findViewById(R.id.tv_percent);
        btn_measure = findViewById(R.id.btn_measure);
        onceHrBarView = findViewById(R.id.barview);
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

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer value = Integer.parseInt(etValue.getText().toString().trim());
                DeviceDataSave.saveOneceHrData(AppConfiguration.braceletID, String.valueOf(BaseManager.mUserId), value, System.currentTimeMillis(), String.valueOf(0));
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setDataFromLocal();
                    }
                }, 200);
            }
        });


        LoadImageUtil.getInstance().loadGifHr(this,R.drawable.icon_spide_guid,htGuidImg);

        htGuidImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                htGuidImg.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initHeader() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.mian_title_once_hr));
        iv_history = findViewById(R.id.iv_history);
        iv_back = findViewById(R.id.iv_back);
        iv_history.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnceHrDataResultActivity.this, OnceHrListActivity.class));
            }
        });

    }

    @Override
    protected void initData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        signalRecyclerView.setLayoutManager(linearLayoutManager);
        singleList = new ArrayList<>();
        signalHeartAdapter = new SignalHeartAdapter(120,singleList,this);
        signalRecyclerView.setAdapter(signalHeartAdapter);


        signalHeartAdapter.setOnF18ItemClickListener(new OnF18ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onceHeartGuidTv.setText(singleList.get(position).getStrdate());
                setCurrData(singleList.get(position));
            }

            @Override
            public void onChildClick(int position, boolean isCheck) {

            }

            @Override
            public void onLongClick(int position) {

            }
        });

        mhandler = new Handler();
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        String birthday = "";
        if (userInfoBean != null) {
            birthday = userInfoBean.getBirthday();
            age = UserUtils.getAge(birthday);
            sex = userInfoBean.getGender();

        }
        mW81DeviceDataModelImp = new W81DeviceDataModelImp();
        mActPresenter.getOnceHrNumData();
        ISportAgent.getInstance().registerListener(mBleReciveListener);
    }



    private void setCurrData(DrawRecDataBean currData) {
        try {
            if (currData != null) {
                int currentHr = currData.getValue();
                tv_percent.setVisibility(View.VISIBLE);
                tv_oxy_value.setText(currData.getValue()+"");
                try {
                    tv_oxy_value.setTextColor(HeartRateConvertUtils.hrValueColor(currData.getValue(), HeartRateConvertUtils.getMaxHeartRate(age, sex)));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                tv_time.setText(currData.getStrdate()+"");

                float precent = (float) HeartRateConvertUtils.hearRate2Percent(currentHr, HeartRateConvertUtils.getMaxHeartRate(age, sex));
                int color = HeartRateConvertUtils.hrValueColor(currentHr, HeartRateConvertUtils.getMaxHeartRate(age, sex));

                //渲染位置
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onceHrBarView.setCurrentBMIvalue(precent, color);
                    }
                }, 500);

                // onceHrBarView.setCurrentBMIvalue(precent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    private Handler mhandler;

    private void startMeasure() {
        if (AppConfiguration.isConnected) {
            isMeasure = true;
            btn_measure.setText(R.string.stop_measure);
            ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_ONECE_HR_DATA, true);
            ActivitySwitcher.goMeasureActivty(this, JkConfiguration.DeviceMeasureType.hr);
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
                // setDataFromLocal();
                Logger.myLog("measure_oxygen success");
                break;
        }
    }


    private void finishMeasure() {
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().requsetW81Ble(BleRequest.DEVICE_MEASURE_ONECE_HR_DATA, false);
            isMeasure = false;
            btn_measure.setText(R.string.start_measure);
        } else {
            Toast.makeText(this, getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
        }
    }

    private void setData() {
        if (mCurrentInfo != null) {
            int currentHr = TextUtils.isEmpty(mCurrentInfo.getHeartValue()) ? 0 : Integer.parseInt(mCurrentInfo.getHeartValue());
            tv_percent.setVisibility(View.VISIBLE);
            tv_oxy_value.setText(mCurrentInfo.getHeartValue());
            try {
                tv_oxy_value.setTextColor(HeartRateConvertUtils.hrValueColor(Integer.parseInt(mCurrentInfo.getHeartValue()), HeartRateConvertUtils.getMaxHeartRate(age, sex)));

            } catch (Exception e) {
                e.printStackTrace();
            }

            tv_time.setText(TimeUtils.getTimeByyyyyMMddhhmmss(mCurrentInfo.getTimestamp()));

            float precent = (float) HeartRateConvertUtils.hearRate2Percent(currentHr, HeartRateConvertUtils.getMaxHeartRate(age, sex));
            int color = HeartRateConvertUtils.hrValueColor(currentHr, HeartRateConvertUtils.getMaxHeartRate(age, sex));


            //渲染位置

            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onceHrBarView.setCurrentBMIvalue(precent, color);
                }
            }, 500);

            // onceHrBarView.setCurrentBMIvalue(precent);
        }

    }

    private void setDataFromLocal() {
        if(!ActivitySwitcher.isForeground(OnceHrDataResultActivity.this))
            return;
        mCurrentInfo = mW81DeviceDataModelImp.getOneceHrLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        Logger.myLog("mCurrentInfo:" + mCurrentInfo + "lastSingleId:" + lastTimestamp);
        trendview_oxy.setDeviceType(JkConfiguration.BODY_ONCE_HR);

        if (lastTimestamp == mCurrentInfo.getTimestamp().longValue()) {
            return;
        }
        lastTimestamp = mCurrentInfo.getTimestamp();
        if (mCurrentInfo != null) {
            setData();
            Integer value = 0;
            try {
                value = Integer.parseInt(mCurrentInfo.getHeartValue());
            } catch (Exception e) {
                value = 0;
            } finally {
                DrawRecDataBean bean = new DrawRecDataBean();
                bean.setValue(value);
                bean.setColors(HeartRateConvertUtils.hrValueColor(value, HeartRateConvertUtils.getMaxHeartRate(age, sex)));
                bean.setStrdate(TimeUtils.getTimeByyyyyMMddhhmmss(mCurrentInfo.getTimestamp()));
                trendview_oxy.setLocalData(bean);
                singleList.add(0,bean);
                signalHeartAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected OnceHrHistoryPresenter createPresenter() {
        return new OnceHrHistoryPresenter();
    }


    private final BleReciveListener mBleReciveListener = new BleReciveListener() {

        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            AppConfiguration.isConnected = isConn;
        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            Log.e(TAG,"-------手动测量---心率--Result="+mResult.getType());

            if (mResult != null)
                switch (mResult.getType()) {
                    //设备测量结果成功
                    case IResult.DEVICE_MESSURE:
                        try {
                            DeviceMessureDataResult deviceMessureDataResult = (DeviceMessureDataResult) mResult;
                            switch (deviceMessureDataResult.getMessureType()) {
                                case DeviceMessureData.measure_bloodpre:
                                    Logger.myLog("measure_bloodpre success");
                                    break;
                                case DeviceMessureData.measure_once_hr:
                                    isMeasure = false;
                                    btn_measure.setText(R.string.start_measure);
                                    mhandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setDataFromLocal();
                                        }
                                    },1000);

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

    List<OnceHrInfo> info;

    @Override
    public void getOnceHrHistoryDataSuccess(List<OnceHrInfo> info) {

        this.info = info;
        // ArrayList<Integer> valuse = new ArrayList<>();
        // ArrayList<String> dates = new ArrayList<>();\
        ArrayList<DrawRecDataBean> list = new ArrayList<>();
        DrawRecDataBean bean;
        try {
            if (info != null && info.size() > 0) {
                for (int i = 0; i < info.size(); i++) {
                    Integer value = 0;
                    value = Integer.parseInt(info.get(i).getHeartValue());
                    bean = new DrawRecDataBean();
                    bean.setValue(value);
                    bean.setStrdate(TimeUtils.getTimeByyyyyMMddhhmmss(info.get(i).getTimestamp()));
                    bean.setColors(HeartRateConvertUtils.hrValueColor(value, HeartRateConvertUtils.getMaxHeartRate(age, sex)));
                    list.add(bean);
                    Logger.myLog(" lastSingleId info:" + bean + ",age=" + age + ",sex=" + sex + ",value=" + value);

                }
                lastTimestamp = info.get(0).getTimestamp();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list.size() > 0) {

                singleList.addAll(list);
                signalHeartAdapter.notifyDataSetChanged();

                htGuidImg.setVisibility(singleList.size()>=7 ? View.VISIBLE : View.GONE);

                setCurrData(singleList.get(singleList.size()-1));

                mCurrentInfo = info.get(0);
                trendview_oxy.setdata(list, JkConfiguration.BODY_ONCE_HR);
                setData();
            } else {
                htGuidImg.setVisibility(View.GONE);
                trendview_oxy.setDeviceType(JkConfiguration.BODY_ONCE_HR);
                tv_percent.setVisibility(View.INVISIBLE);
                tv_oxy_value.setText("--");
                tv_time.setText("--");
            }


        }
    }

    @Override
    protected void initImmersionBar() {
        // ImmersionBar.with(this).statusBarDarkFont(true).statusBarColor(UIUtils.getColor(R.color.common_bg)).init();
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }
}
