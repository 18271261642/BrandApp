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
 * F18??????????????????
 * Created by Admin
 * Date 2022/1/17
 */
public class F18WatchManagerActivity extends BaseMVPTitleActivity<F18SetView, F18SetPresent> implements F18SetView, View.OnClickListener {

    //??????????????????
    private final static int SET_STEP_GOAL_CODE = 0x00;
    //????????????
    private final static int SET_DISTANCE_GOAL_CODE = 0x04;
    //???????????????
    private final static int SET_KCAL_GOAL_CODE = 0x05;
    //??????????????????
    private final static int SET_TIME_STYLE_CODE = 0x01;
    //??????????????????
    private final static int SET_TEMP_UNIT_CODE = 0x02;
    //?????????
    private final static int SET_KM_UNIT_CODE = 0x03;

    private F18DeviceSetData setData;

    //??????
    private VerBatteryView batteryView;
    private TextView f18Bettery_count;
    //????????????
    private TextView f18Watch_state;

    //????????????
    private ItemDeviceSettingView f18DeviceWatchFaceItem;
    //????????????
    private ItemDeviceSettingView f18DeviceTakePhotoItem;
    //????????????
    private ItemDeviceSettingView f18DeviceStepGoalItem;
    //????????????
    private ItemDeviceSettingView f18DeviceDistanceGoalItem;
    //???????????????
    private ItemDeviceSettingView f18DeviceKcalGoalItem;
    //????????????
    private ItemDeviceSettingView f18DeviceAlarmItem;
    //????????????
    private ItemDeviceSettingView f18DeviceTimeStyleItem;
    //????????????
    private ItemDeviceSettingView f18DeviceWeatherItem;
    //????????????
    private ItemDeviceSettingView f18DeviceTempItem;
    //???????????????
    private ItemDeviceSettingView f18DeviceUnitItem;
    //24????????????
    private ItemDeviceSettingView f18Device24HourItem;
    //???????????????
    private ItemDeviceSettingView f18DeviceContractItem;
    //????????????
    private ItemDeviceSettingView f18DeviceStrongItem;
    //????????????
    private ItemDeviceSettingView f18DeviceLongSitAlertItem;
    //????????????
    private ItemDeviceSettingView f18DeviceDNTItem;
    //????????????
    private ItemDeviceSettingView f18ContinueItem;
    //????????????
    private ItemDeviceSettingView f18DeviceTurnScreenItem;
    //????????????
    private ItemDeviceSettingView f18DeviceFindWatchItem;
    //APP????????????
    private ItemDeviceSettingView f18DeviceAppMsgItem;
    //????????????
    private ItemDeviceSettingView f18DeviceDrinkItem;
    //????????????
    private ItemDeviceSettingView f18DeviceGuidDeviceItem;
    //??????????????????
    private ItemDeviceSettingView f18DeviceAudioGuidItem;
    //OTA??????
    private ItemDeviceSettingView f18DeviceOtaItem;
    //??????
    private TextView f18DeviceUnbindItem;

    //????????????
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
        //????????????
        f18DeviceTakePhotoItem  = findViewById(R.id.f18DeviceTakePhotoItem);
        //????????????
        f18DeviceStepGoalItem= findViewById(R.id.f18DeviceStepGoalItem);
        //????????????
        f18DeviceDistanceGoalItem = findViewById(R.id.f18DeviceDistanceGoalItem);
        //???????????????
        f18DeviceKcalGoalItem = findViewById(R.id.f18DeviceKcalGoalItem);
        //????????????
        f18DeviceAlarmItem= findViewById(R.id.f18DeviceAlarmItem);
        //????????????
        f18DeviceTimeStyleItem= findViewById(R.id.f18DeviceTimeStyleItem);
        //????????????
        f18DeviceWeatherItem= findViewById(R.id.f18DeviceWeatherItem);
        //????????????
        f18DeviceTempItem= findViewById(R.id.f18DeviceTempItem);
        //???????????????
        f18DeviceUnitItem= findViewById(R.id.f18DeviceUnitItem);
        //24????????????
        f18Device24HourItem= findViewById(R.id.f18Device24HourItem);
        //???????????????
        f18DeviceContractItem= findViewById(R.id.f18DeviceContractItem);
        //????????????
        f18DeviceStrongItem= findViewById(R.id.f18DeviceStrongItem);
        //????????????
        f18DeviceLongSitAlertItem= findViewById(R.id.f18DeviceLongSitAlertItem);
        //????????????
        f18DeviceDNTItem= findViewById(R.id.f18DeviceDNTItem);
        //????????????
        f18DeviceFindWatchItem= findViewById(R.id.f18DeviceFindWatchItem);
        //APP????????????
        f18DeviceAppMsgItem= findViewById(R.id.f18DeviceAppMsgItem);
        //????????????
        f18DeviceDrinkItem= findViewById(R.id.f18DeviceDrinkItem);
        //????????????
        f18DeviceGuidDeviceItem= findViewById(R.id.f18DeviceGuidDeviceItem);
        //??????????????????
        f18DeviceAudioGuidItem= findViewById(R.id.f18DeviceAudioGuidItem);
        //OTA??????
        f18DeviceOtaItem= findViewById(R.id.f18DeviceOtaItem);
        //??????
        f18DeviceUnbindItem = findViewById(R.id.f18DeviceUnbindItem);
        //????????????
        f18DeviceTurnScreenItem = findViewById(R.id.f18DeviceTurnScreenItem);
        //????????????
        f18ContinueItem = findViewById(R.id.f18ContinueItem);

        f18DeviceWatchFaceItem.setOnClickListener(this);

        f18DeviceTakePhotoItem.setOnClickListener(this);
        //????????????
        f18DeviceStepGoalItem.setOnClickListener(this);
        //????????????
        f18DeviceAlarmItem.setOnClickListener(this);
        //????????????
        f18DeviceTimeStyleItem.setOnClickListener(this);
        //????????????
        f18DeviceWeatherItem.setOnClickListener(this);
        //????????????
        f18DeviceTempItem.setOnClickListener(this);
        //???????????????
        f18DeviceUnitItem.setOnClickListener(this);

        //???????????????
        f18DeviceContractItem.setOnClickListener(this);
        //????????????
        f18DeviceLongSitAlertItem.setOnClickListener(this);
        //????????????
        f18DeviceDNTItem.setOnClickListener(this);
        //????????????
        f18DeviceFindWatchItem.setOnClickListener(this);
        //APP????????????
        f18DeviceAppMsgItem.setOnClickListener(this);
        //????????????
        f18DeviceDrinkItem.setOnClickListener(this);
        //????????????
        f18DeviceGuidDeviceItem.setOnClickListener(this);
        //??????????????????
        f18DeviceAudioGuidItem.setOnClickListener(this);
        //OTA??????
        f18DeviceOtaItem.setOnClickListener(this);
        //??????
        f18DeviceUnbindItem.setOnClickListener(this);
        //????????????
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
        if(vId == R.id.f18DeviceUnbindItem){    //??????
            unBindF18Device();
            return;
        }

        //????????????
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

        if(vId == R.id.f18DeviceWatchFaceItem){  //????????????
           startActivity(new Intent(F18WatchManagerActivity.this, F18DialActivity.class));
        }
        if(vId == R.id.f18DeviceTakePhotoItem){  //????????????
            requestCameraPermission();
          //  Watch7018Manager.getWatch7018Manager().getCommonSet();
        }
        if(vId == R.id.f18DeviceStepGoalItem){  //????????????
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

        if(vId == R.id.f18DeviceDistanceGoalItem){  //????????????
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

        if(vId == R.id.f18DeviceKcalGoalItem){  //???????????????
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


        if(vId == R.id.f18DeviceAlarmItem){     //????????????
            startSerializableActivity("comm_key",setData,F18AlarmShowActivity.class);
           // startActivity(new Intent(F18WatchManagerActivity.this,F18AlarmShowActivity.class));
        }

        if(vId == R.id.f18DeviceTimeStyleItem){ //????????????
            ArrayList<String> timeList = new ArrayList<>();
            timeList.add(getResources().getString(R.string.time_format_12));
            timeList.add(getResources().getString(R.string.time_format_24));
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_TIME_STYLE_CODE,false,setData == null ? 0 : setData.getTimeStyle(),timeList);

        }

        if(vId == R.id.f18DeviceWeatherItem){  //????????????
            Intent intent = new Intent(this, ActivityWeatherSetting.class);
            intent.putExtra("deviceType",7018);
            startActivity(intent);
        }
        if(vId == R.id.f18DeviceTempItem){      //????????????
            ArrayList<String> tempList = new ArrayList<>();
            tempList.add("???");
            tempList.add("???");
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_TEMP_UNIT_CODE,false,setData == null ? 0 : setData.getTempStyle()==0 ? 0 : 1,tempList);
        }

        if(vId == R.id.f18DeviceLongSitAlertItem){  //????????????
            startSerializableActivity("comm_key",setData,F18LongSitActivity.class);
            //startActivity(new Intent(F18WatchManagerActivity.this, F18LongSitActivity.class));
        }

        if(vId == R.id.f18DeviceUnitItem){  //?????????
            ArrayList<String> unitList = new ArrayList<>();
            unitList.add("??????");
            unitList.add("??????");
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_KM_UNIT_CODE,false,0,unitList);
        }
        if(vId == R.id.f18DeviceContractItem){  //???????????????
            startSerializableActivity("comm_key",setData,F18ContactActivity.class);
           // startActivity(new Intent(F18WatchManagerActivity.this,F18ContactActivity.class));
        }

        if(vId == R.id.f18DeviceDNTItem){   //????????????
            startSerializableActivity("comm_key",setData,F18DNTActivity.class);
          //  startActivity(new Intent(F18WatchManagerActivity.this,F18DNTActivity.class));
        }
        if(vId == R.id.f18DeviceDrinkItem){ //????????????
            startSerializableActivity("comm_key",setData,F18DrinkAlertActivity.class);
          //startActivity(new Intent(F18WatchManagerActivity.this,F18DrinkAlertActivity.class));

        }
        if(vId == R.id.f18DeviceGuidDeviceItem){    //????????????
            GoActivityUtil.goActivityPlayerDevice(7018,new DeviceBean(),this);
        }

        if(vId == R.id.f18ContinueItem){    //????????????
            startSerializableActivity("comm_key",setData,F18ContinueMeasureActivity.class);
           // startActivity(new Intent(F18WatchManagerActivity.this,F18ContinueMeasureActivity.class));
        }

        if(vId == R.id.f18DeviceTurnScreenItem){    //????????????
            startSerializableActivity("comm_key",setData,F18TurnWristActivity.class);
          //  startActivity(new Intent(F18WatchManagerActivity.this,F18TurnWristActivity.class));
        }

        if(vId == R.id.f18DeviceAudioGuidItem){  //??????????????????
            GoActivityUtil.goActivityPlayerDevice(70180,new DeviceBean(),this);
        }
        if(vId == R.id.f18DeviceFindWatchItem){     //????????????
            Watch7018Manager.getWatch7018Manager().findDevices();
        }
        if(vId == R.id.f18DeviceOtaItem){   //OTA??????
            startActivity(F18DufActivity.class);
        }

        if(vId == R.id.f18DeviceAppMsgItem){    //APP????????????
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
                    Logger.myLog(TAG,"--------??????="+new Gson().toJson(device));
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
//        Logger.myLog("??????????????? == " + currentType+"\n"+deviceBean.toString());
        //?????????????????????
        AppSP.putString(F18WatchManagerActivity.this,AppSP.F18_SAVE_MAC,null);
        Watch7018Manager.getWatch7018Manager().disConnectDevice();
        mActPresenter.unBind(deviceBean, dirct);
    }



    private final ItemDeviceSettingView.OnItemViewCheckedChangeListener onItemViewCheckedChangeListener = new ItemDeviceSettingView.OnItemViewCheckedChangeListener() {
        @Override
        public void onCheckedChanged(int id, boolean isChecked) {


            if(id == R.id.f18DeviceStrongItem){     //????????????
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
        Log.e(TAG,"---------????????????="+f18DeviceSetData.toString());
        this.setData = f18DeviceSetData;
        JkConfiguration.WATCH_GOAL = f18DeviceSetData.getStepGoal();
        //????????????
        f18DeviceStepGoalItem.setContentText(f18DeviceSetData.getStepGoal()+getResources().getString(R.string.step_unit));
        f18DeviceDistanceGoalItem.setContentText(f18DeviceSetData.getDistanceGoal()+getResources().getString(R.string.unit_meters));
        f18DeviceKcalGoalItem.setContentText(f18DeviceSetData.getKcalGoal()+getResources().getString(R.string.unit_kcal));
        //????????????
        f18DeviceAlarmItem.setContentText(f18DeviceSetData.getAlarmCount() == 0 ? getResources().getString(R.string.display_no_count) : String.format(getResources().getString(R.string.string_open_number),f18DeviceSetData.getAlarmCount()+""));
        //????????????
        f18DeviceTimeStyleItem.setContentText(f18DeviceSetData.getTimeStyle() == 0 ? getResources().getString(R.string.time_format_12) : getResources().getString(R.string.time_format_24));
        //????????????
       // f18DeviceWeatherItem.setContentText(f18DeviceSetData.getCityName().equals("?????????")?getResources().getString(R.string.setting_start) : getResources().getString(R.string.display_no_count));
        //????????????
        f18DeviceTempItem.setContentText(f18DeviceSetData.getTempStyle() == 0 ?  "???" : "???");

        DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
        action.saveTempUnitlModel(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(this), f18DeviceSetData.getTempStyle() == 0 ? "0" : "1");

        //?????????
        f18DeviceUnitItem.setContentText(f18DeviceSetData.getIsKmUnit() == 0 ? "??????" : "??????");
        //24????????????
        f18Device24HourItem.setChecked(f18DeviceSetData.isIs24Heart());
        //????????????
        f18DeviceStrongItem.setChecked(f18DeviceSetData.isStrengthMeasure());
        //????????????
        f18DeviceTurnScreenItem.setContentText(f18DeviceSetData.getTurnWrist() == null || f18DeviceSetData.getTurnWrist().equals("?????????") ? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getTurnWrist());
        //????????????
        f18DeviceDNTItem.setContentText(f18DeviceSetData.getDNT() == null || f18DeviceSetData.getDNT().equals("?????????") ? getResources().getString(R.string.display_no_count) : (f18DeviceSetData.getDNT().equals("?????????")?getResources().getString(R.string.setting_start) : f18DeviceSetData.getDNT()));
        //????????????
        f18DeviceDrinkItem.setContentText(f18DeviceSetData.getDrinkAlert() == null || f18DeviceSetData.getDrinkAlert().equals("?????????")? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getDrinkAlert() );
        //????????????
        f18DeviceLongSitAlertItem.setContentText(f18DeviceSetData.getLongSitStr() == null || f18DeviceSetData.getLongSitStr().equals("?????????") ? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getLongSitStr());
        //?????????
       // f18DeviceContractItem.setContentText("?????????"+f18DeviceSetData.getContactNumber()+"???");
        //????????????
        f18ContinueItem.setContentText(f18DeviceSetData.getContinuMonitor() == null || f18DeviceSetData.getContinuMonitor().equals("?????????") ? getResources().getString(R.string.display_no_count) : f18DeviceSetData.getContinuMonitor());
        //??????????????????
        f18DeviceAppMsgItem.setContentText(!f18DeviceSetData.isAllAppMsgStatus() ? getResources().getString(R.string.display_no_count) : String.format(getResources().getString(R.string.string_open_number),f18DeviceSetData.getAppMsgs()+""));
        //????????????
        f18DeviceOtaItem.setContentText(f18DeviceSetData.getDeviceVersionName());


    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        Log.e(TAG,"-------????????????="+selectType+" "+type + " "+timeStr);
        if(selectType == SET_STEP_GOAL_CODE){   //????????????
            f18DeviceStepGoalItem.setContentText(timeStr+getResources().getString(R.string.unit_step));
//            String tmpGoal = StringUtils.substringBefore(timeStr,"???");
            AppSP.putInt(BaseApp.getApp(),AppSP.DEVICE_GOAL_KEY,0);
            JkConfiguration.WATCH_GOAL = Integer.parseInt(timeStr.trim());
            Watch7018Manager.getWatch7018Manager().setDeviceSportGoal(Integer.parseInt(timeStr.trim()),10,100);
            if(setData != null){
                setData.setStepGoal(Integer.parseInt(timeStr.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }

        }

        if(selectType == SET_DISTANCE_GOAL_CODE){   //????????????
            f18DeviceDistanceGoalItem.setContentText(timeStr+getResources().getString(R.string.unit_meters));
         //   String tmpGoal = StringUtils.substringBefore(timeStr,"???");
            if(setData != null){
                setData.setDistanceGoal(Integer.parseInt(timeStr.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }



        if(selectType == SET_KCAL_GOAL_CODE){       //???????????????
            f18DeviceKcalGoalItem.setContentText(timeStr+getResources().getString(R.string.unit_kcal));
            //String tmpGoal = StringUtils.substringBefore(timeStr,"???");
            if(setData != null){
                setData.setKcalGoal(Integer.parseInt(timeStr.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }


        if(selectType == SET_TIME_STYLE_CODE){      //????????????
            f18DeviceTimeStyleItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTimeStyle(timeStr.contains("12"));
            if(setData != null){
                setData.setTimeStyle(timeStr.contains("12") ? 0 : 1);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }
        if(selectType == SET_TEMP_UNIT_CODE){   //????????????
            f18DeviceTempItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTemperUnit(timeStr.contains("???"));
            if(setData != null){
                setData.setTempStyle(timeStr.contains("???") ? 1 : 0);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }

            DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
            action.saveTempUnitlModel(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(this), timeStr.contains("???") ? "1" : "0");

        }

        if(selectType == SET_KM_UNIT_CODE){     //?????????
            f18DeviceUnitItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setKmUnit(timeStr.contains("???"));
            if(setData != null){
                setData.setTempStyle(timeStr.contains("???") ? 1 : 0);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }

        if(selectType == -1){
            //?????????????????????????????????,??????????????????
//            if (AppConfiguration.isConnected) {
//                BaseDevice currnetDevice = ISportAgent.getInstance().getCurrnetDevice();
//                if (currnetDevice != null && currnetDevice.deviceType == currentType) {
//                    Logger.myLog("currnetDevice == " + currentType);
//                    //???????????????????????????
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

                    //????????????????????????????????????
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
