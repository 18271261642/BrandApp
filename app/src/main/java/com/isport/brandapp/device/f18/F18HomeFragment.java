package com.isport.brandapp.device.f18;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.F18HomeCountStepListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.RefrushRecycleView;
import com.isport.brandapp.banner.recycleView.adapter.RefrushAdapter;
import com.isport.brandapp.banner.recycleView.holder.CustomHolder;
import com.isport.brandapp.banner.recycleView.inter.DefaultAdapterViewLisenter;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.F18TestActivity;
import com.isport.brandapp.device.bracelet.ReportActivity;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.customview.MainHeadLayout;
import com.isport.brandapp.home.view.DataDeviceSportHolder;
import com.isport.brandapp.home.view.DataHeaderHolder;
import com.isport.brandapp.home.view.DataHeartRateHolder;
import com.isport.brandapp.home.view.DataScaleHolder;
import com.isport.brandapp.home.view.DataSleepHolder;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.ClickUtils;
import com.isport.brandapp.wu.activity.BPResultActivity;
import com.isport.brandapp.wu.activity.OnceHrDataResultActivity;
import com.isport.brandapp.wu.activity.OxyResultActivity;
import com.isport.brandapp.wu.activity.PractiseRecordActivity;
import com.isport.brandapp.wu.activity.TempResultActivity;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class F18HomeFragment extends BaseMVPFragment<F18HomeView,F18HomePresenter> implements F18HomeView, MainHeadLayout.ViewMainHeadClickLister,
        DataHeaderHolder.OnHeadOnclickLister, DataDeviceSportHolder.OnSportItemClickListener,
        DataDeviceSportHolder.OnSportOnclickListenter, DataScaleHolder.OnScaleItemClickListener,
        DataScaleHolder.OnScaleOnclickListenter, DataHeartRateHolder.OnHeartRateOnclickListenter,
        DataHeartRateHolder.OnHeartRateItemClickListener, DataSleepHolder.OnSleepOnclickListenter,
        DataSleepHolder.OnSleepItemClickListener{

    private static final String TAG = "F18HomeFragment";

    public static F18HomeFragment getInstance(){
        return new F18HomeFragment();
    }

    public BaseDevice mCurrentDevice;


    //item数据源
    private Vector<Integer> lists;//item类型列表
    //adapter
    private RefrushAdapter<String> adapter;
    //刷新控件
    private SmartRefreshLayout home_refresh;
    private MainHeadLayout mainHeadLayout;
    //recyclerView
    private RefrushRecycleView refrushRecycleView;

    DataSleepHolder dataSleepHolder;//睡眠

    DataHeaderHolder dataHeaderHolder;//进度条
    DataHeartRateHolder dataOnceHrHolder;//单次测量心率
    DataHeartRateHolder dataBloodPresureDataHolder;  //血压
    DataHeartRateHolder dataOxygenDataHolder;  //血氧
    DataHeartRateHolder dataExerciseDataHolder; //锻炼

    DataHeartRateHolder dataTempHolder;//单次体温


    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSP.putInt(BaseApp.getApp(),AppSP.DEVICE_GOAL_KEY,0);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                handler.removeMessages(0x00);
                Intent intent = new Intent();
                intent.setAction(Watch7018Manager.SYNC_DATA_COMPLETE);
                context.sendBroadcast(intent);
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_data_new;
    }

    @Override
    protected void initView(View view) {
       // AppConfiguration.braceletID = "F18-C36";
        setViews(view);

        ISportAgent.getInstance().registerListener(bleReciveListener);
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(Watch7018Manager.SYNC_DATA_COMPLETE);
        intentFilter.addAction(Watch7018Manager.F18_EXERCISE_SYNC_COMPLETE);
        intentFilter.addAction(Watch7018Manager.F18_CONNECT_STATUS);
        intentFilter.addAction(Watch7018Manager.F18_DIS_CONNECTED_STATUS);
        intentFilter.addAction(Watch7018Manager.F18_CONNECT_ING);
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiver,intentFilter);

        initHomeMenu();

        Watch7018Manager.getWatch7018Manager().setF18HomeCountStepListener(new F18HomeCountStepListener() {
            @Override
            public void backHomeCountData(int step, float distance, float kcal) {
                WatchSportMainData wD = new WatchSportMainData();
                wD.setStep(step+"");
                wD.setCal(decimalFormat.format(Float.valueOf(distance/1000)));
                wD.setDistance(decimalFormat.format(Float.valueOf(kcal/1000)));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(dataHeaderHolder != null){
                            dataHeaderHolder.updateUI(wD);
                        }
                    }
                }, 500);
            }
        });


        String[] getDeviceV = Watch7018Manager.getWatch7018Manager().getCurrCountStep();
        WatchSportMainData wD = new WatchSportMainData();
        wD.setStep(getDeviceV[0]);
        wD.setCal(decimalFormat.format(Float.valueOf(getDeviceV[2])));
        wD.setDistance(decimalFormat.format(Float.valueOf(getDeviceV[1])));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dataHeaderHolder != null){
                    dataHeaderHolder.updateUI(wD);
                }
            }
        }, 500);


        //下拉刷新
        home_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //没有打开蓝牙，进制刷新
                if(!AppUtil.isOpenBle()){
                    home_refresh.finishRefresh();
                    return;
                }

              if(mCurrentDevice == null){
                  home_refresh.finishRefresh();
                  return;
              }
                //设备未连接
              if(!AppConfiguration.isConnected){
                  home_refresh.finishRefresh();
                  return;
              }
              handler.sendEmptyMessageDelayed(0x00,30 * 1000);
                SyncCacheUtils.saveStartSync(BaseApp.getApp());
                Watch7018Manager.getWatch7018Manager().syncDeviceData();
                startSyncDevice();

            }
        });

    }

    private void initHomeMenu() {
        lists = new Vector<>();
        lists.add(JkConfiguration.BODY_HEADER);//手表步数展示,默认项
        lists.add(JkConfiguration.BODY_SLEEP);//睡眠
        lists.add(lists.size(), JkConfiguration.BODY_ONCE_HR); //单次心率
        lists.add(lists.size(), JkConfiguration.BODY_BLOODPRESSURE);  //血压
        lists.add(lists.size(), JkConfiguration.BODY_OXYGEN);  //血氧
        lists.add(lists.size(), JkConfiguration.BODY_TEMP); //体温
        lists.add(lists.size(), JkConfiguration.BODY_EXCERICE);  //锻炼
        adapter = new RefrushAdapter<>(getActivity(),lists,R.layout.item,defaultAdapterViewLisenter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        refrushRecycleView.setLayoutManager(manager);
        refrushRecycleView.setAdapter(adapter);
    }


    private void setViews(View view){
        refrushRecycleView = view.findViewById(R.id.rc_home);
        home_refresh = view.findViewById(R.id.home_refresh);
        mainHeadLayout = view.findViewById(R.id.layout_mainHeadLayout);
        mainHeadLayout.setViewClickLister(this);

        mainHeadLayout.setIconDeviceIcon(R.drawable.icon_home_f18_connstatus_img);
        mainHeadLayout.setViewClickLister(viewMainHeadClickLister);
    }

    //正在同步数据
    public void startSyncDevice() {
        AppConfiguration.hasSynced = false;
        mainHeadLayout.showProgressBar(true);
        mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.sync_data));
    }

    //同步数据完成
    public void endSyncDevice() {
        home_refresh.finishRefresh();
        AppConfiguration.hasSynced = true;
        mainHeadLayout.showProgressBar(false);
        mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected));
    }


    //展示头部的状态
    private void showMainHeadLayoutStatus(){
        mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
        //判断蓝牙是否开启
        boolean isOpenBle =  AppUtil.isOpenBle();
        mainHeadLayout.setIconDeviceAlp(0.5f);
        if (!isOpenBle) {
            mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.app_enable), MainHeadLayout.TAG_OPENBLE, UIUtils.getString(R.string.fragment_main_no_connect_open_ble));
            return;
        } else {
            mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.fragment_main_click_connect), MainHeadLayout.TAG_CONNECT, UIUtils.getString(R.string.disConnect));
        }

        //判断是否是已连接
        if(AppConfiguration.isConnected && Watch7018Manager.getWatch7018Manager().isConnected()){
            mainHeadLayout.showProgressBar(false);
            mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected));
            mainHeadLayout.setIconDeviceAlp(5f);
        }else{
            mainHeadLayout.setIconDeviceAlp(0.5f);
            if(Watch7018Manager.getWatch7018Manager().getConnStatus() == 0x02){  //正在连接
                mainHeadLayout.showProgressBar(true);
                mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.app_isconnecting));
                return;
            }


            mainHeadLayout.showProgressBar(false);
            mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.fragment_main_click_connect), MainHeadLayout.TAG_CONNECT, UIUtils.getString(R.string.disConnect));
        }

    }


    //打开蓝牙
    public void openBlueDialog() {
        PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.bonlala_open_blue), context, getResources().getString(R.string.app_bluetoothadapter_turnoff), getResources().getString(R.string.app_bluetoothadapter_turnon), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null) {
                    bluetoothAdapter.enable();
                }
            }
            @Override
            public void cancel() {
            }
        }, false);
    }



    private final MainHeadLayout.ViewMainHeadClickLister viewMainHeadClickLister = new MainHeadLayout.ViewMainHeadClickLister() {
        @Override
        public void onViewOptionClikelister(String type) {
            if (type.equals(MainHeadLayout.TAG_ADD)) {
                //跳转到添加设备
                ActivitySwitcher.goBindAct(context);
                return;
            }

            if (type.equals(MainHeadLayout.TAG_CONNECT)){
                mainHeadLayout.showProgressBar(true);
                mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.app_isconnecting));
                autoConnDevice();
                return;
            }
            if (type.equals(MainHeadLayout.TAG_OPENBLE)){
                //弹出蓝牙对话框
                openBlueDialog();

            }

        }

        @Override
        public void onMainBack() {
            if(getActivity() == null)
                return;
            getActivity().finish();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        autoConnDevice();
        getHomeData();
        showMainHeadLayoutStatus();
    }



    private void autoConnDevice(){
        try {
            //未连接
            if(!AppConfiguration.isConnected){
                String f18SaveMac = AppSP.getString(getActivity(),AppSP.F18_SAVE_MAC,"");
                if(!TextUtils.isEmpty(f18SaveMac)){
                    F18ConnectStatusService fs = App.getInstance().getF18ConnStatusService();
                    if(fs != null)
                        fs.autoScann(f18SaveMac);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void getHomeData(){

        Log.e(TAG,"------deviceId="+AppConfiguration.braceletID+" "+AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.Watch_F18));


        BaseDevice baseDevice = ISportAgent.getInstance().getCurrnetDevice();

        String deviceId = AppConfiguration.braceletID;
        String userId = TokenUtil.getInstance().getPeopleIdInt(getActivity());
        if(deviceId == null)
            return;
       // mFragPresenter.dealWithYesDayStep(userId,deviceId);
        mFragPresenter.getBloodPressure();
        mFragPresenter.getNumOxyGen();
        mFragPresenter.getNetTempData();
        mFragPresenter.getNumNetOnceHr();

        mFragPresenter.getAllDeviceDetailData();

        mFragPresenter.uploadOxyData(deviceId,userId);
        mFragPresenter.uploadF18BloodData(deviceId,userId);
        mFragPresenter.upgradeOnceHrData(deviceId,userId);
        mFragPresenter.updateTempData(deviceId,userId);


        mFragPresenter.getDeviceStepLastTwoData(IDeviceType.TYPE_WATCH_7018);
        mFragPresenter.getDeviceBloodPressure();
        mFragPresenter.getDevcieOnceHrData();
        //mFragPresenter.getDeviceLastNetHeart();
        mFragPresenter.getDevcieOxygenData();

        mFragPresenter.getTempData();
        mFragPresenter.getWatchSleepLastData();
        mFragPresenter.getExerciseTodaySum(IDeviceType.TYPE_WATCH_7018);
    }

    @Override
    protected void initData() {
        String deviceId = AppConfiguration.braceletID;
        String userId = TokenUtil.getInstance().getPeopleIdInt(getActivity());
        if(deviceId == null)
            return;
//        mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
//        //从网络获取血压
//        mFragPresenter.getBloodPressure();
//        mFragPresenter.getNumOxyGen();
//        mFragPresenter.getNetTempData();
//        mFragPresenter.getNumNetOnceHr();
//
//        mFragPresenter.getAllDeviceDetailData();
//
//        mFragPresenter.uploadOxyData(deviceId,userId);
//        mFragPresenter.uploadF18BloodData(deviceId,userId);
//        mFragPresenter.upgradeOnceHrData(deviceId,userId);
//        mFragPresenter.updateTempData(deviceId,userId);

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ISportAgent.getInstance().unregisterListener(bleReciveListener);
    }



    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.layout_top_view).statusBarDarkFont(true)
                .init();
    }


    //Head的点击回调
    @Override
    public void onViewOptionClikelister(String type) {

    }

    @Override
    public void onMainBack() {

    }

    private final DefaultAdapterViewLisenter defaultAdapterViewLisenter = new DefaultAdapterViewLisenter(){
        @Override
        public CustomHolder getBodyHolder(Context context, List lists, int itemID) {
            return null;
        }

        //步数
        @Override
        public CustomHolder getHeader(Context context, List lists, int itemID) {
            dataHeaderHolder = new DataHeaderHolder(context, lists, R.layout.app_fragment_data_head);
            dataHeaderHolder.setOnCourseOnclickLister(F18HomeFragment.this);
          //  mFragPresenter.getDeviceStepLastTwoData(IDeviceType.TYPE_WATCH_7018);
            return dataHeaderHolder;
        }

        //血氧
        @Override
        public CustomHolder getOxyGenItem(Context context, List lists, int itemID) {
            dataOxygenDataHolder = new DataHeartRateHolder(context, lists, R.layout
                    .app_fragment_data_device_item, JkConfiguration.BODY_OXYGEN);
            dataOxygenDataHolder.setHeartRateItemClickListener(F18HomeFragment.this, F18HomeFragment.this);
            mFragPresenter.getDevcieOxygenData();
            return dataOxygenDataHolder;
        }

        //体温
        @Override
        public CustomHolder getTempItem(Context context, List lists, int itemID) {
            dataTempHolder = new DataHeartRateHolder(context, lists, R.layout
                    .app_fragment_data_device_item, JkConfiguration.BODY_TEMP);
            dataTempHolder.setHeartRateItemClickListener(F18HomeFragment.this, F18HomeFragment.this);
            mFragPresenter.getTempData();
            return dataTempHolder;
        }

        //单次心率
        @Override
        public CustomHolder getOnceHrItem(Context context, List lists, int itemID) {
            dataOnceHrHolder = new DataHeartRateHolder(context, lists, R.layout
                    .app_fragment_data_device_item, JkConfiguration.BODY_ONCE_HR);
            dataOnceHrHolder.setHeartRateItemClickListener(F18HomeFragment.this, F18HomeFragment.this);
            mFragPresenter.getDevcieOnceHrData();
            return dataOnceHrHolder;
        }

        //血压
        @Override
        public CustomHolder getBloodPressureItem(Context context, List lists, int itemID) {
            //return super.getBloodPressureItem(context, lists, itemID);
            dataBloodPresureDataHolder = new DataHeartRateHolder(context, lists, R.layout
                    .app_fragment_data_device_item, JkConfiguration.BODY_BLOODPRESSURE);
            dataBloodPresureDataHolder.setHeartRateItemClickListener(F18HomeFragment.this, F18HomeFragment.this);
            mFragPresenter.getDeviceBloodPressure();
            return dataBloodPresureDataHolder;
        }

        //锻炼
        @Override
        public CustomHolder getExecericeItem(Context context, List lists, int itemID) {
            dataExerciseDataHolder = new DataHeartRateHolder(context, lists, R.layout
                    .app_fragment_data_device_item, JkConfiguration.BODY_EXCERICE);
            dataExerciseDataHolder.setHeartRateItemClickListener(F18HomeFragment.this, F18HomeFragment.this);
            int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            mFragPresenter.getExerciseTodaySum(JkConfiguration.DeviceType.Watch_F18);
            return dataExerciseDataHolder;
        }

        //睡眠
        @Override
        public CustomHolder getConectSleep(Context context, List lists, int itemID) {
            dataSleepHolder = new DataSleepHolder(context, lists, R.layout
                    .app_fragment_data_device_item);
            dataSleepHolder.setSleepItemClickListener(F18HomeFragment.this, F18HomeFragment.this);
            mFragPresenter.getWatchSleepLastData();
            return dataSleepHolder;
        }
    };




    @Override
    public void onSportItemClick() {

    }

    @Override
    public void onSportStateListenter() {

    }



    //进度条点击监听回调
    @Override
    public void onHeadOnclick() {
        onHeartRateItemClick(JkConfiguration.BODY_HEADER);
    }

    @Override
    public void onChangeClikelister() {

    }

    @Override
    public void onHeadOnclickIvChange() {

    }


    //单次心率item点击回调
    @Override
    public void onHeartRateItemClick(int viewType) {
        AppConfiguration.isConnected = true;
        if(ClickUtils.isFastDoubleClick())
            return;
        switch (viewType){
            case JkConfiguration.BODY_TEMP: {   //体温
                startActivity(new Intent(getActivity(), TempResultActivity.class));
//                startActivity(new Intent(getActivity(), F18TestActivity.class));
            }
            break;
            //单次心率测量
            case JkConfiguration.BODY_ONCE_HR: {
                startActivity(new Intent(getActivity(), OnceHrDataResultActivity.class));
            }
            break;
            //血压
            case JkConfiguration.BODY_BLOODPRESSURE: {
                startActivity(new Intent(getActivity(), BPResultActivity.class));
            }
            break;
            //血氧
            case JkConfiguration.BODY_OXYGEN: {
                startActivity(new Intent(getActivity(), OxyResultActivity.class));
            }
            break;
            //锻炼
            case JkConfiguration.BODY_EXCERICE: {
                startActivity(new Intent(getActivity(), PractiseRecordActivity.class));

            }
            break;
            //运动进度条
            case JkConfiguration.BODY_HEADER: {
                Intent intent1 = new Intent(context, ReportActivity.class);
                intent1.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceBeanList.get(IDeviceType.TYPE_WATCH_7018));
                startActivity(intent1);

            }
            break;
        }

    }

    @Override
    public void onHeartRateStateListenter(boolean isOpen) {

    }

    @Override
    public void onScaleItemClick() {

    }

    @Override
    public void onAddScaleItemClick() {

    }

    @Override
    public void onScaleStateListenter() {

    }

    @Override
    public void onScaleViewSuccess() {

    }

    //睡眠点击回调
    @Override
    public void onSleepItemClick() {
        if(ClickUtils.isFastDoubleClick())
            return;
        Intent intent = new Intent(context, F18SleepActivity.class);
        intent.putExtra(JkConfiguration.CURRENTDEVICETPE, JkConfiguration.DeviceType.Watch_F18);
        intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceBeanList.get(IDeviceType.TYPE_WATCH_7018));
        startActivity(intent);
    }

    @Override
    public void onSleepStateListenter() {

    }

    //蓝牙连接状态监听
    private final BleReciveListener bleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            Logger.myLog(TAG,"连接状态 Constants.isDFU" + Constants.isDFU + "isConn:" + isConn);
        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {

        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {

        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {

        }
    };


    //用于监听手机蓝牙状态的变化
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                showMainHeadLayoutStatus();
            }

            if(action.equals(Watch7018Manager.F18_CONNECT_STATUS)){
                showMainHeadLayoutStatus();
            }
            if(action.equals(Watch7018Manager.F18_DIS_CONNECTED_STATUS)){
                showMainHeadLayoutStatus();
            }

            if(action.equals(Watch7018Manager.SYNC_DATA_COMPLETE)){ //同步完成
                handler.removeMessages(0x00);
                endSyncDevice();
                //mFragPresenter.getDeviceStepLastTwoData(IDeviceType.TYPE_WATCH_7018);

               // mFragPresenter.dealWithYesDayStep(TokenUtil.getInstance().getPeopleIdStr(getContext()),AppConfiguration.braceletID);
                mFragPresenter.dealHistoryStep(TokenUtil.getInstance().getPeopleIdStr(getContext()),AppConfiguration.braceletID);
                if (!Constants.isSyncData) {
                    mFragPresenter.getNoUpgradeW81DevcieDetailData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, "0", false);
                }
            }

            if(action.equals(Watch7018Manager.F18_EXERCISE_SYNC_COMPLETE)){
                //上传锻炼数据
                mFragPresenter.upgradeExeciseData(IDeviceType.TYPE_WATCH_7018,AppConfiguration.braceletID,TokenUtil.getInstance().getPeopleIdStr(getActivity()));
            }
        }
    };

    @Override
    public void successGetDeviceListFormDB(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean reConnect) {

    }

    @Override
    public void successGetDeviceListFormHttp(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean reConnect) {

    }

    //详细计步数据
    @Override
    public void successGetMainLastStepDataForDB(WatchSportMainData watchSportMainData) {

        Log.e(TAG,"-------详细计步数据="+watchSportMainData.toString()+(dataHeaderHolder == null));
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(dataHeaderHolder != null){
//                    String kcalStr = watchSportMainData.getCal();
//                    if(kcalStr != null){
//                        int integerK = Integer.parseInt(kcalStr);
//                        watchSportMainData.setCal(kcalStr);
//                    }
//                    dataHeaderHolder.updateUI(watchSportMainData);
//                }
//            }
//        }, 500);

    }

    @Override
    public void successGetMainLastOxgenData(OxyInfo info) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dataOxygenDataHolder != null){
                    String strDate = info.getStrDate();
                    String value = info.getBoValue() + "%";
                    String unit = "95%～98%";
                    dataOxygenDataHolder.updateUI(strDate, value, unit);
                }
            }
        }, 500);
    }

    //单次心率返回
    @Override
    public void successGetMainLastOnceHrData(OnceHrInfo info) {
        Log.e(TAG,"-------心率item="+info.toString());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(dataOnceHrHolder != null){
                    String value = String.valueOf(info.getHeartValue());
                    String unit = UIUtils.getString(R.string.BPM);
                    dataOnceHrHolder.updateUI(info.getStrDate(),value,unit);
                }
            }
        }, 500);
    }

    //单次血压
    @Override
    public void successGetMainLastBloodPresuure(BPInfo info) {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dataBloodPresureDataHolder != null){
                    String strDate = info.getStrDate();
                    String value = "";
                    if (info.getSpValue() == 0 || info.getDpValue() == 0) {
                    } else {
                        value = info.getSpValue() + "/" + info.getDpValue();
                    }
                    String unit = UIUtils.getString(R.string.mmhg);
                    dataBloodPresureDataHolder.updateUI(strDate, value, unit);
                }
            }
        }, 500);

    }

    //获取体温
    @Override
    public void successGetMainLastTempValue(TempInfo info) {
        if(dataTempHolder != null){
            String unitl = UIUtils.getString(R.string.temperature_degree_centigrade);
            String value = info.getCentigrade();
            if (info.getTempUnitl().equals("1")) {
                unitl = UIUtils.getString(R.string.temperature_fahrenheit);
                value = info.getFahrenheit();
            }
            dataTempHolder.updateUI(info.getStrDate(), value, unitl);
        }

    }

    //睡眠
    @Override
    public void successGetMainLastSleepValue(WatchSleepDayData watchSleepDayData) {
        if(dataSleepHolder != null ){
            dataSleepHolder.updateUI(watchSleepDayData.getTotalSleepTime() == 0 ? null :  watchSleepDayData.getDateStr(), watchSleepDayData.getTotalSleepTime());
        }

    }

    //锻炼数据
    @Override
    public void successGetMainTotalAllTime(Integer time) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataExerciseDataHolder != null) {
                    String strDate = TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis());
                    String value = time + "";
                    String unit = UIUtils.getString(R.string.minute);
                    dataExerciseDataHolder.updateUI(strDate, value, unit);
                }
            }
        }, 500);
    }


    @Override
    protected F18HomePresenter createPersenter() {
        return new F18HomePresenter(this);
    }
}
