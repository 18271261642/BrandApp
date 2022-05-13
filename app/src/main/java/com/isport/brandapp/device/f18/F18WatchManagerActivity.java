package com.isport.brandapp.device.f18;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.DeviceInformationTableAction;
import com.isport.blelibrary.db.action.DeviceTempUnitlTableAction;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DeviceTimesUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.F18TestActivity;
import com.isport.brandapp.device.bracelet.ActivityWeatherSetting;
import com.isport.brandapp.device.bracelet.CamaraActivity1;
import com.isport.brandapp.device.f18.dial.F18DialActivity;
import com.isport.brandapp.device.publicpage.ActivityDeviceUnbindGuide;
import com.isport.brandapp.device.publicpage.GoActivityUtil;
import com.isport.brandapp.dialog.UnBindDeviceDialog;
import com.isport.brandapp.dialog.UnbindStateCallBack;
import com.isport.brandapp.home.OperateGuidActivity;
import com.isport.brandapp.login.ActivityWebView;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.ClickUtils;
import com.isport.brandapp.view.VerBatteryView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import bike.gymproject.viewlibray.ItemDeviceSettingView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * F18设备设置页面
 * Created by Admin
 * Date 2022/1/17
 */
public class F18WatchManagerActivity extends BaseMVPTitleActivity<F18SetView, F18SetPresent> implements F18SetView, View.OnClickListener {

    //设置计步目标
    private final static int SET_STEP_GOAL_CODE = 0x00;
    //距离目标
    private final static int SET_DISTANCE_GOAL_CODE = 0x04;
    //卡路里目标
    private final static int SET_KCAL_GOAL_CODE = 0x05;
    //设置时间格式
    private final static int SET_TIME_STYLE_CODE = 0x01;
    //设置温度单位
    private final static int SET_TEMP_UNIT_CODE = 0x02;
    //公英制
    private final static int SET_KM_UNIT_CODE = 0x03;

    private F18DeviceSetData setData;

    //电量
    private VerBatteryView batteryView;
    private TextView f18Bettery_count;
    //连接状态
    private TextView f18Watch_state;

    //表盘设置
    private ItemDeviceSettingView f18DeviceWatchFaceItem;
    //遥控拍照
    private ItemDeviceSettingView f18DeviceTakePhotoItem;
    //计步目标
    private ItemDeviceSettingView f18DeviceStepGoalItem;
    //距离目标
    private ItemDeviceSettingView f18DeviceDistanceGoalItem;
    //卡路里目标
    private ItemDeviceSettingView f18DeviceKcalGoalItem;
    //闹钟设置
    private ItemDeviceSettingView f18DeviceAlarmItem;
    //时间格式
    private ItemDeviceSettingView f18DeviceTimeStyleItem;
    //天气设置
    private ItemDeviceSettingView f18DeviceWeatherItem;
    //温度单位
    private ItemDeviceSettingView f18DeviceTempItem;
    //公英制单位
    private ItemDeviceSettingView f18DeviceUnitItem;
    //24小时心率
    private ItemDeviceSettingView f18Device24HourItem;
    //常用联系人
    private ItemDeviceSettingView f18DeviceContractItem;
    //加强测量
    private ItemDeviceSettingView f18DeviceStrongItem;
    //久坐提醒
    private ItemDeviceSettingView f18DeviceLongSitAlertItem;
    //勿扰模式
    private ItemDeviceSettingView f18DeviceDNTItem;
    //连续监测
    private ItemDeviceSettingView f18ContinueItem;
    //抬腕亮屏
    private ItemDeviceSettingView f18DeviceTurnScreenItem;
    //查找手表
    private ItemDeviceSettingView f18DeviceFindWatchItem;
    //APP消息提醒
    private ItemDeviceSettingView f18DeviceAppMsgItem;
    //喝水提醒
    private ItemDeviceSettingView f18DeviceDrinkItem;
    //玩转设备
    private ItemDeviceSettingView f18DeviceGuidDeviceItem;
    //音频控制指引
    private ItemDeviceSettingView f18DeviceAudioGuidItem;
    //OTA升级
    private ItemDeviceSettingView f18DeviceOtaItem;
    //解绑
    private TextView f18DeviceUnbindItem;

    //操作指引
    private ItemDeviceSettingView f18DeviceOperateItem;


    private String guidUrl;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                Intent intent = new Intent();
                intent.setAction(Watch7018Manager.SYNC_UNBIND_DATA_COMPLETE);
                sendBroadcast(intent);
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_device_manager_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
    }

    @Override
    protected void initData() {
        guidUrl = getIntent().getStringExtra("device_guid_url");

        ISportAgent.getInstance().requestBle(BleRequest.Common_GetBattery);
        Watch7018Manager.getWatch7018Manager().getCommonSet();

    }

    @Override
    protected void initEvent() {
        IntentFilter intentFilter = new IntentFilter(Watch7018Manager.SYNC_UNBIND_DATA_COMPLETE);
        intentFilter.addAction(Watch7018Manager.F18_DIS_CONNECTED_STATUS);
        intentFilter.addAction(Watch7018Manager.F18_CONNECT_STATUS);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected F18SetPresent createPresenter() {
        return new F18SetPresent(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        f18Watch_state.setText(AppConfiguration.isConnected ? getResources().getString(R.string.connected) :getResources().getString(R.string.disConnect));
        String macStr = AppConfiguration.braceletID;
        titleBarView.setTitle(macStr);
        if(macStr != null)
          mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),macStr);
        showBatteryData();
    }


    private void showBatteryData(){
        String macStr = AppConfiguration.braceletID;
        if(macStr == null)
            return;
        DeviceInformationTable deviceInformationTable = DeviceInformationTableAction.findDeviceInfoByDeviceId(macStr);
        if(deviceInformationTable != null){
            int batteryValue = deviceInformationTable.getBattery();
            batteryView.setProgress(batteryValue);
            f18Bettery_count.setText(batteryValue+"%");
        }
    }

    private void findViews(){

        f18DeviceOperateItem = findViewById(R.id.f18DeviceOperateItem);
        batteryView = findViewById(R.id.iv_battery);
        f18Bettery_count = findViewById(R.id.f18Bettery_count);
        f18Watch_state = findViewById(R.id.f18Watch_state);
        f18DeviceWatchFaceItem = findViewById(R.id.f18DeviceWatchFaceItem);
        //遥控拍照
        f18DeviceTakePhotoItem  = findViewById(R.id.f18DeviceTakePhotoItem);
        //计步目标
        f18DeviceStepGoalItem= findViewById(R.id.f18DeviceStepGoalItem);
        //距离目标
        f18DeviceDistanceGoalItem = findViewById(R.id.f18DeviceDistanceGoalItem);
        //卡路里目标
        f18DeviceKcalGoalItem = findViewById(R.id.f18DeviceKcalGoalItem);
        //闹钟设置
        f18DeviceAlarmItem= findViewById(R.id.f18DeviceAlarmItem);
        //时间格式
        f18DeviceTimeStyleItem= findViewById(R.id.f18DeviceTimeStyleItem);
        //天气设置
        f18DeviceWeatherItem= findViewById(R.id.f18DeviceWeatherItem);
        //温度单位
        f18DeviceTempItem= findViewById(R.id.f18DeviceTempItem);
        //公英制单位
        f18DeviceUnitItem= findViewById(R.id.f18DeviceUnitItem);
        //24小时心率
        f18Device24HourItem= findViewById(R.id.f18Device24HourItem);
        //常用联系人
        f18DeviceContractItem= findViewById(R.id.f18DeviceContractItem);
        //加强测量
        f18DeviceStrongItem= findViewById(R.id.f18DeviceStrongItem);
        //久坐提醒
        f18DeviceLongSitAlertItem= findViewById(R.id.f18DeviceLongSitAlertItem);
        //勿扰模式
        f18DeviceDNTItem= findViewById(R.id.f18DeviceDNTItem);
        //查找手表
        f18DeviceFindWatchItem= findViewById(R.id.f18DeviceFindWatchItem);
        //APP消息提醒
        f18DeviceAppMsgItem= findViewById(R.id.f18DeviceAppMsgItem);
        //喝水提醒
        f18DeviceDrinkItem= findViewById(R.id.f18DeviceDrinkItem);
        //玩转设备
        f18DeviceGuidDeviceItem= findViewById(R.id.f18DeviceGuidDeviceItem);
        //音频控制指引
        f18DeviceAudioGuidItem= findViewById(R.id.f18DeviceAudioGuidItem);
        //OTA升级
        f18DeviceOtaItem= findViewById(R.id.f18DeviceOtaItem);
        //解绑
        f18DeviceUnbindItem = findViewById(R.id.f18DeviceUnbindItem);
        //抬腕亮屏
        f18DeviceTurnScreenItem = findViewById(R.id.f18DeviceTurnScreenItem);
        //连续监测
        f18ContinueItem = findViewById(R.id.f18ContinueItem);

        f18DeviceWatchFaceItem.setOnClickListener(this);

        f18DeviceTakePhotoItem.setOnClickListener(this);
        //计步目标
        f18DeviceStepGoalItem.setOnClickListener(this);
        //闹钟设置
        f18DeviceAlarmItem.setOnClickListener(this);
        //时间格式
        f18DeviceTimeStyleItem.setOnClickListener(this);
        //天气设置
        f18DeviceWeatherItem.setOnClickListener(this);
        //温度单位
        f18DeviceTempItem.setOnClickListener(this);
        //公英制单位
        f18DeviceUnitItem.setOnClickListener(this);

        //常用联系人
        f18DeviceContractItem.setOnClickListener(this);
        //久坐提醒
        f18DeviceLongSitAlertItem.setOnClickListener(this);
        //勿扰模式
        f18DeviceDNTItem.setOnClickListener(this);
        //查找手表
        f18DeviceFindWatchItem.setOnClickListener(this);
        //APP消息提醒
        f18DeviceAppMsgItem.setOnClickListener(this);
        //喝水提醒
        f18DeviceDrinkItem.setOnClickListener(this);
        //玩转设备
        f18DeviceGuidDeviceItem.setOnClickListener(this);
        //音频控制指引
        f18DeviceAudioGuidItem.setOnClickListener(this);
        //OTA升级
        f18DeviceOtaItem.setOnClickListener(this);
        //解绑
        f18DeviceUnbindItem.setOnClickListener(this);
        //抬腕亮屏
        f18DeviceTurnScreenItem.setOnClickListener(this);
        f18ContinueItem.setOnClickListener(this);
        f18DeviceDistanceGoalItem.setOnClickListener(this);
        f18DeviceKcalGoalItem.setOnClickListener(this);
        f18DeviceOperateItem.setOnClickListener(this);


        f18DeviceWatchFaceItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(F18TestActivity.class);
                return true;
            }
        });


        f18Device24HourItem.setOnCheckedChangeListener(onItemViewCheckedChangeListener);
        f18DeviceStrongItem.setOnCheckedChangeListener(onItemViewCheckedChangeListener);

    }

    public BaseDevice create7018Device(String name, String mac){
        return new W7018Device(name,mac);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18DeviceUnbindItem){    //解绑
            unBindF18Device();
            return;
        }

        //操作指引
        if(vId == R.id.f18DeviceOperateItem){
            startActivity(new String[]{"url","title"},new String[]{guidUrl,getResources().getString(R.string.string_operate_guide_desc)}, ActivityWebView.class);
        }

        if(!AppConfiguration.isConnected){
            Toast.makeText(F18WatchManagerActivity.this,getResources().getString(R.string.app_disconnect_device),Toast.LENGTH_SHORT).show();
            return;
        }

        if(ClickUtils.isFastDoubleClick())
            return;

        if(setData == null)
            return;

        if(vId == R.id.f18DeviceWatchFaceItem){  //表盘设置
           startActivity(new Intent(F18WatchManagerActivity.this, F18DialActivity.class));
        }
        if(vId == R.id.f18DeviceTakePhotoItem){  //遥控拍照
            requestCameraPermission();
          //  Watch7018Manager.getWatch7018Manager().getCommonSet();
        }
        if(vId == R.id.f18DeviceStepGoalItem){  //计步目标
            ArrayList<String> stepGoalList = new ArrayList<>();
            for(int i = 0;i< 30;i++){
                int stepTemp = 1000 * (i + 1);
                stepGoalList.add(stepTemp+"");
            }

            int position = 0;
            if(setData != null){
                int currGoal = setData.getStepGoal();
                for(int i = 0;i<stepGoalList.size();i++){
                    if((currGoal+"").equals(stepGoalList.get(i))){
                        position = i;
                        break;
                    }
                }
            }

            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_STEP_GOAL_CODE,true,position,stepGoalList);
        }

        if(vId == R.id.f18DeviceDistanceGoalItem){  //距离目标
            ArrayList<String> distanceGoalList = new ArrayList<>();
            for(int i = 0;i< 30;i++){
                int stepTemp = 1000 * (i + 1);
                distanceGoalList.add(stepTemp+"");
            }

            int position = 0;
            if(setData != null){
                int currGoal = setData.getDistanceGoal();
                for(int i = 0;i<distanceGoalList.size();i++){
                    if((currGoal+"").equals(distanceGoalList.get(i))){
                        position = i;
                        break;
                    }
                }
            }

            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_DISTANCE_GOAL_CODE,true,position,distanceGoalList);

        }

        if(vId == R.id.f18DeviceKcalGoalItem){  //卡路里目标
            ArrayList<String> kcalGoalList = new ArrayList<>();
            for(int i = 0;i< 500;i++){
                int stepTemp = 10 * (i + 1);
                kcalGoalList.add(stepTemp+"");
            }

            int position = 0;
            if(setData != null){
                int currGoal = setData.getKcalGoal();
                for(int i = 0;i<kcalGoalList.size();i++){
                    if((currGoal+"").equals(kcalGoalList.get(i))){
                        position = i;
                        break;
                    }
                }
            }

            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_KCAL_GOAL_CODE,true,position,kcalGoalList);

        }


        if(vId == R.id.f18DeviceAlarmItem){     //闹钟设置
            startSerializableActivity("comm_key",setData,F18AlarmShowActivity.class);
           // startActivity(new Intent(F18WatchManagerActivity.this,F18AlarmShowActivity.class));
        }

        if(vId == R.id.f18DeviceTimeStyleItem){ //时间格式
            ArrayList<String> timeList = new ArrayList<>();
            timeList.add(getResources().getString(R.string.time_format_12));
            timeList.add(getResources().getString(R.string.time_format_24));
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_TIME_STYLE_CODE,false,setData == null ? 0 : setData.getTimeStyle(),timeList);

        }

        if(vId == R.id.f18DeviceWeatherItem){  //天气设置
            Intent intent = new Intent(this, ActivityWeatherSetting.class);
            intent.putExtra("deviceType",7018);
            startActivity(intent);
        }
        if(vId == R.id.f18DeviceTempItem){      //温度单位
            ArrayList<String> tempList = new ArrayList<>();
            tempList.add("℃");
            tempList.add("℉");
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_TEMP_UNIT_CODE,false,setData == null ? 0 : setData.getTempStyle()==0 ? 0 : 1,tempList);
        }

        if(vId == R.id.f18DeviceLongSitAlertItem){  //久坐提醒
            startSerializableActivity("comm_key",setData,F18LongSitActivity.class);
            //startActivity(new Intent(F18WatchManagerActivity.this, F18LongSitActivity.class));
        }

        if(vId == R.id.f18DeviceUnitItem){  //公英制
            ArrayList<String> unitList = new ArrayList<>();
            unitList.add("公制");
            unitList.add("英制");
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_KM_UNIT_CODE,false,0,unitList);
        }
        if(vId == R.id.f18DeviceContractItem){  //常用联系人
            startSerializableActivity("comm_key",setData,F18ContactActivity.class);
           // startActivity(new Intent(F18WatchManagerActivity.this,F18ContactActivity.class));
        }

        if(vId == R.id.f18DeviceDNTItem){   //勿扰模式
            startSerializableActivity("comm_key",setData,F18DNTActivity.class);
          //  startActivity(new Intent(F18WatchManagerActivity.this,F18DNTActivity.class));
        }
        if(vId == R.id.f18DeviceDrinkItem){ //喝水提醒
            startSerializableActivity("comm_key",setData,F18DrinkAlertActivity.class);
          //startActivity(new Intent(F18WatchManagerActivity.this,F18DrinkAlertActivity.class));

        }
        if(vId == R.id.f18DeviceGuidDeviceItem){    //玩转设备
            GoActivityUtil.goActivityPlayerDevice(7018,new DeviceBean(),this);
        }

        if(vId == R.id.f18ContinueItem){    //定时监测
            startSerializableActivity("comm_key",setData,F18ContinueMeasureActivity.class);
           // startActivity(new Intent(F18WatchManagerActivity.this,F18ContinueMeasureActivity.class));
        }

        if(vId == R.id.f18DeviceTurnScreenItem){    //抬腕亮屏
            startSerializableActivity("comm_key",setData,F18TurnWristActivity.class);
          //  startActivity(new Intent(F18WatchManagerActivity.this,F18TurnWristActivity.class));
        }

        if(vId == R.id.f18DeviceAudioGuidItem){  //音频控制指引
            GoActivityUtil.goActivityPlayerDevice(70180,new DeviceBean(),this);
        }
        if(vId == R.id.f18DeviceFindWatchItem){     //查找手表
            Watch7018Manager.getWatch7018Manager().findDevices();
        }
        if(vId == R.id.f18DeviceOtaItem){   //OTA升级
            startActivity(F18DufActivity.class);
        }

        if(vId == R.id.f18DeviceAppMsgItem){    //APP消息提醒
            startSerializableActivity("comm_key",setData,F18AppsShowActivity.class);
            //startActivity(new Intent(F18WatchManagerActivity.this,F18AppsShowActivity.class));
        }

    }


    private void unBindF18Device(){
             new UnBindDeviceDialog(this, JkConfiguration.DeviceType.Watch_F18, true, new UnbindStateCallBack() {
            @Override
            public void synUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                if (AppConfiguration.isConnected) {
                    Constants.isSyncUnbind = true;
                    BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                    Logger.myLog(TAG,"--------解绑="+new Gson().toJson(device));
                    if (device != null) {
                        int currentDevice = device.deviceType;
                        handler.sendEmptyMessageDelayed(0x00,80 * 1000);
                         Watch7018Manager.getWatch7018Manager().syncDeviceData("0");
                        SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(1, 1), false);
                    }
                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.main_device_no_conn_title));
                }
            }

            @Override
            public void dirctUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                DeviceBean device = AppConfiguration.deviceMainBeanList.get(IDeviceType.TYPE_WATCH_7018);
                unBindDevice(device, true);
            }

            @Override
            public void cancel() {

            }
        }, JkConfiguration.DeviceType.SLEEP);
    }



    private void unBindDevice(DeviceBean deviceBean, boolean dirct) {
        SyncProgressObservable.getInstance().hide();
//        isDerictUnBind = true;
//        currentType = deviceBean.currentType;
//        Logger.myLog("点击去解绑 == " + currentType+"\n"+deviceBean.toString());
        //解绑前断连设备
        AppSP.putString(F18WatchManagerActivity.this,AppSP.F18_SAVE_MAC,null);
        Watch7018Manager.getWatch7018Manager().disConnectDevice();
        mActPresenter.unBind(deviceBean, dirct);
    }



    private final ItemDeviceSettingView.OnItemViewCheckedChangeListener onItemViewCheckedChangeListener = new ItemDeviceSettingView.OnItemViewCheckedChangeListener() {
        @Override
        public void onCheckedChanged(int id, boolean isChecked) {


            if(id == R.id.f18DeviceStrongItem){     //加强测量
                f18DeviceStrongItem.setChecked(isChecked);
                if(setData != null){
                    setData.setStrengthMeasure(isChecked);
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
                }
                Watch7018Manager.getWatch7018Manager().setStrengthMeasure(isChecked);
            }
        }
    };




    private void requestCameraPermission(){
       // if(XXPermissions.isGranted(this, R.ma))
        boolean isCamera = XXPermissions.isPermanentDenied(this, Manifest.permission.CAMERA);
        if(isCamera){
            XXPermissions.with(this).permission(Manifest.permission.CAMERA).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> list, boolean b) {

                }
            });
            return;
        }
        Watch7018Manager.getWatch7018Manager().intoTakePhotoStatus(true);
        Intent intentCamara = new Intent(context, CamaraActivity1.class);
        startActivity(intentCamara);
    }



    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        Log.e(TAG,"---------查找返回="+f18DeviceSetData.toString());
        this.setData = f18DeviceSetData;
        JkConfiguration.WATCH_GOAL = f18DeviceSetData.getStepGoal();
        //计步目标
        f18DeviceStepGoalItem.setContentText(f18DeviceSetData.getStepGoal()+getResources().getString(R.string.step_unit));
        f18DeviceDistanceGoalItem.setContentText(f18DeviceSetData.getDistanceGoal()+getResources().getString(R.string.unit_meters));
        f18DeviceKcalGoalItem.setContentText(f18DeviceSetData.getKcalGoal()+getResources().getString(R.string.unit_kcal));
        //闹钟数量
        f18DeviceAlarmItem.setContentText(f18DeviceSetData.getAlarmCount() == 0 ? getResources().getString(R.string.display_no_count) : String.format(getResources().getString(R.string.string_open_number),f18DeviceSetData.getAlarmCount()+""));
        //时间格式
        f18DeviceTimeStyleItem.setContentText(f18DeviceSetData.getTimeStyle() == 0 ? getResources().getString(R.string.time_format_12) : getResources().getString(R.string.time_format_24));
        //天气设置
       // f18DeviceWeatherItem.setContentText(f18DeviceSetData.getCityName().equals("已开启")?getResources().getString(R.string.setting_start) : getResources().getString(R.string.display_no_count));
        //温度单位
        f18DeviceTempItem.setContentText(f18DeviceSetData.getTempStyle() == 0 ?  "℃" : "℉");

        DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
        action.saveTempUnitlModel(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(this), f18DeviceSetData.getTempStyle() == 0 ? "0" : "1");

        //公英制
        f18DeviceUnitItem.setContentText(f18DeviceSetData.getIsKmUnit() == 0 ? "公制" : "英制");
        //24小时心率
        f18Device24HourItem.setChecked(f18DeviceSetData.isIs24Heart());
        //加强测量
        f18DeviceStrongItem.setChecked(f18DeviceSetData.isStrengthMeasure());
        //抬腕亮屏
        f18DeviceTurnScreenItem.setContentText(f18DeviceSetData.getTurnWrist() == null || f18DeviceSetData.getTurnWrist().equals("未开启") ? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getTurnWrist());
        //勿扰模式
        f18DeviceDNTItem.setContentText(f18DeviceSetData.getDNT() == null || f18DeviceSetData.getDNT().equals("未开启") ? getResources().getString(R.string.display_no_count) : (f18DeviceSetData.getDNT().equals("已开启")?getResources().getString(R.string.setting_start) : f18DeviceSetData.getDNT()));
        //喝水提醒
        f18DeviceDrinkItem.setContentText(f18DeviceSetData.getDrinkAlert() == null || f18DeviceSetData.getDrinkAlert().equals("未开启")? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getDrinkAlert() );
        //久坐提醒
        f18DeviceLongSitAlertItem.setContentText(f18DeviceSetData.getLongSitStr() == null || f18DeviceSetData.getLongSitStr().equals("未开启") ? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getLongSitStr());
        //联系人
       // f18DeviceContractItem.setContentText("已设置"+f18DeviceSetData.getContactNumber()+"个");
        //定时检测
        f18ContinueItem.setContentText(f18DeviceSetData.getContinuMonitor() == null || f18DeviceSetData.getContinuMonitor().equals("未开启") ? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getContinuMonitor());
        //消息开启个数
        f18DeviceAppMsgItem.setContentText(!f18DeviceSetData.isAllAppMsgStatus() ? getResources().getString(R.string.display_no_count) : String.format(getResources().getString(R.string.string_open_number),f18DeviceSetData.getAppMsgs()+""));
        //固件版本
        f18DeviceOtaItem.setContentText(f18DeviceSetData.getDeviceVersionName());


    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        Log.e(TAG,"-------设置返回="+selectType+" "+type + " "+timeStr);
        if(selectType == SET_STEP_GOAL_CODE){   //计步目标
            f18DeviceStepGoalItem.setContentText(timeStr+getResources().getString(R.string.unit_step));
//            String tmpGoal = StringUtils.substringBefore(timeStr,"步");
            AppSP.putInt(BaseApp.getApp(),AppSP.DEVICE_GOAL_KEY,0);
            JkConfiguration.WATCH_GOAL = Integer.parseInt(timeStr.trim());
            Watch7018Manager.getWatch7018Manager().setDeviceSportGoal(Integer.parseInt(timeStr.trim()),10,100);
            if(setData != null){
                setData.setStepGoal(Integer.parseInt(timeStr.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }

        }

        if(selectType == SET_DISTANCE_GOAL_CODE){   //距离目标
            f18DeviceDistanceGoalItem.setContentText(timeStr+getResources().getString(R.string.unit_meters));
         //   String tmpGoal = StringUtils.substringBefore(timeStr,"米");
            if(setData != null){
                setData.setDistanceGoal(Integer.parseInt(timeStr.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }



        if(selectType == SET_KCAL_GOAL_CODE){       //卡路里目标
            f18DeviceKcalGoalItem.setContentText(timeStr+getResources().getString(R.string.unit_kcal));
            //String tmpGoal = StringUtils.substringBefore(timeStr,"千");
            if(setData != null){
                setData.setKcalGoal(Integer.parseInt(timeStr.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }


        if(selectType == SET_TIME_STYLE_CODE){      //时间格式
            f18DeviceTimeStyleItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTimeStyle(timeStr.contains("12"));
            if(setData != null){
                setData.setTimeStyle(timeStr.contains("12") ? 0 : 1);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }
        if(selectType == SET_TEMP_UNIT_CODE){   //温度单位
            f18DeviceTempItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTemperUnit(timeStr.contains("℉"));
            if(setData != null){
                setData.setTempStyle(timeStr.contains("℉") ? 1 : 0);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }

            DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
            action.saveTempUnitlModel(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(this), timeStr.contains("℉") ? "1" : "0");

        }

        if(selectType == SET_KM_UNIT_CODE){     //公英制
            f18DeviceUnitItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setKmUnit(timeStr.contains("公"));
            if(setData != null){
                setData.setTempStyle(timeStr.contains("公") ? 1 : 0);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }

        if(selectType == -1){
            //解绑的是当前连接的设备,需要断连设备
//            if (AppConfiguration.isConnected) {
//                BaseDevice currnetDevice = ISportAgent.getInstance().getCurrnetDevice();
//                if (currnetDevice != null && currnetDevice.deviceType == currentType) {
//                    Logger.myLog("currnetDevice == " + currentType);
//                    //解绑设备，不用重连
//                    ISportAgent.getInstance().unbind(false);
//                }

//            }
            if(null != AppConfiguration.deviceMainBeanList){
                AppConfiguration.deviceMainBeanList.remove(JkConfiguration.DeviceType.Watch_F18);
            }
            AppSP.putString(context, AppSP.F18_SAVE_MAC, null);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
            Intent intent = new Intent(context, ActivityDeviceUnbindGuide.class);
            context.startActivity(intent);
            finish();
        }

    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
          public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(Watch7018Manager.SYNC_UNBIND_DATA_COMPLETE)){
                if(ActivitySwitcher.isForeground(F18WatchManagerActivity.this)){
                    handler.removeMessages(0x00);

                    //同步解绑，当天数据也上传
                    mActPresenter.getNoUpgradeW81DevcieDetailData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID, "0", false);


                    DeviceBean device = AppConfiguration.deviceMainBeanList.get(IDeviceType.TYPE_WATCH_7018);
                    unBindDevice(device, true);
                }

            }

            if(action.equals(Watch7018Manager.F18_DIS_CONNECTED_STATUS)){
                if(ActivitySwitcher.isForeground(F18WatchManagerActivity.this)){
                    f18Watch_state.setText(getResources().getString(R.string.disConnect));
                    batteryView.setProgress(0);
                    f18Bettery_count.setText("");
                }
            }

            if(action.equals(Watch7018Manager.F18_CONNECT_STATUS)){
                if(ActivitySwitcher.isForeground(F18WatchManagerActivity.this)){
                    f18Watch_state.setText(getResources().getString(R.string.connected));
                    showBatteryData();
                }
            }
        }
    };

}
