package com.isport.brandapp.device.f18;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.ActivityWeatherSetting;
import com.isport.brandapp.device.bracelet.CamaraActivity1;
import com.isport.brandapp.device.publicpage.GoActivityUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import bike.gymproject.viewlibray.ItemDeviceSettingView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * F18设备设置页面
 * Created by Admin
 * Date 2022/1/17
 */
public class F18WatchManagerActivity extends BaseMVPTitleActivity<F18SetView, F18SetPresent> implements F18SetView, View.OnClickListener {

    //设置计步目标
    private final static int SET_STEP_GOAL_CODE = 0x00;
    //设置时间格式
    private final static int SET_TIME_STYLE_CODE = 0x01;
    //设置温度单位
    private final static int SET_TEMP_UNIT_CODE = 0x02;
    //公英制
    private final static int SET_KM_UNIT_CODE = 0x03;


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


    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String deviceMac = (String) msg.obj;
            if(deviceMac == null)
                return;
            mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),deviceMac);
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_device_manager_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();
        titleBarView.setTitle("设备管理");
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

    }

    @Override
    protected void initEvent() {

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

        String macStr = Watch7018Manager.getWatch7018Manager().getConnectedMac();
        if(macStr != null){
            Message message = handler.obtainMessage();
            message.obj = macStr;
            message.what = 0x00;
            handler.sendMessageDelayed(message,500);
        }
    }

    private void findViews(){
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


        f18Device24HourItem.setOnCheckedChangeListener(onItemViewCheckedChangeListener);
        f18DeviceStrongItem.setOnCheckedChangeListener(onItemViewCheckedChangeListener);

    }

    public BaseDevice create7018Device(String name, String mac){
        return new W7018Device(name,mac);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18DeviceWatchFaceItem){  //表盘设置
            ISportAgent.getInstance().connect(create7018Device("F18-C36","A1:23:B4:C6:1C:36"),false,true);
           // Watch7018Manager.getWatch7018Manager(this).connectDevice("A1:23:B4:C6:1C:36",true);
        }
        if(vId == R.id.f18DeviceTakePhotoItem){  //遥控拍照
            requestCameraPermission();
            Watch7018Manager.getWatch7018Manager().getCommonSet();
        }
        if(vId == R.id.f18DeviceStepGoalItem){  //计步目标
            ArrayList<String> stepGoalList = new ArrayList<>();
            for(int i = 0;i< 40;i++){
                int stepTemp = 1000 * (i + 1);
                stepGoalList.add(stepTemp+"");
            }
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_STEP_GOAL_CODE,true,0,stepGoalList);
        }

        if(vId == R.id.f18DeviceDistanceGoalItem){  //距离目标
            Watch7018Manager.getWatch7018Manager().syncDeviceData();
        }

        if(vId == R.id.f18DeviceKcalGoalItem){  //卡路里目标
            Watch7018Manager.getWatch7018Manager().measureHealthData(2,true);
        }


        if(vId == R.id.f18DeviceAlarmItem){     //闹钟设置
            startActivity(new Intent(F18WatchManagerActivity.this,F18AlarmShowActivity.class));
        }

        if(vId == R.id.f18DeviceTimeStyleItem){ //时间格式
            ArrayList<String> timeList = new ArrayList<>();
            timeList.add(getResources().getString(R.string.time_format_12));
            timeList.add(getResources().getString(R.string.time_format_24));
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_TIME_STYLE_CODE,false,0,timeList);

        }

        if(vId == R.id.f18DeviceWeatherItem){  //天气设置
            AppConfiguration.isConnected = true;
            Intent intent = new Intent(this, ActivityWeatherSetting.class);
            intent.putExtra("deviceType",7018);
            startActivity(intent);
        }
        if(vId == R.id.f18DeviceTempItem){      //温度单位
            ArrayList<String> tempList = new ArrayList<>();
            tempList.add("摄氏度℃");
            tempList.add("华摄氏度°F");
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_TEMP_UNIT_CODE,false,0,tempList);
        }

        if(vId == R.id.f18DeviceLongSitAlertItem){  //久坐提醒
            startActivity(new Intent(F18WatchManagerActivity.this, F18LongSitActivity.class));
        }

        if(vId == R.id.f18DeviceUnitItem){  //公英制
            ArrayList<String> unitList = new ArrayList<>();
            unitList.add("公制");
            unitList.add("英制");
            mActPresenter.setSignalValue(F18WatchManagerActivity.this,SET_KM_UNIT_CODE,false,0,unitList);
        }
        if(vId == R.id.f18DeviceContractItem){  //常用联系人
            startActivity(new Intent(F18WatchManagerActivity.this,F18ContactActivity.class));
        }

        if(vId == R.id.f18DeviceDNTItem){   //勿扰模式
            startActivity(new Intent(F18WatchManagerActivity.this,F18DNTActivity.class));
        }
        if(vId == R.id.f18DeviceDrinkItem){ //喝水提醒
          startActivity(new Intent(F18WatchManagerActivity.this,F18DrinkAlertActivity.class));

        }
        if(vId == R.id.f18DeviceGuidDeviceItem){    //玩转设备
            GoActivityUtil.goActivityPlayerDevice(7018,new DeviceBean(),this);
        }

        if(vId == R.id.f18ContinueItem){    //连续监测
            startActivity(new Intent(F18WatchManagerActivity.this,F18ContinueMeasureActivity.class));
        }

        if(vId == R.id.f18DeviceTurnScreenItem){    //抬腕亮屏
            startActivity(new Intent(F18WatchManagerActivity.this,F18TurnWristActivity.class));
        }

        if(vId == R.id.f18DeviceAudioGuidItem){  //音频控制指引


        }
        if(vId == R.id.f18DeviceFindWatchItem){     //查找手表
            Toast.makeText(this, "查找手表", Toast.LENGTH_SHORT).show();
            Watch7018Manager.getWatch7018Manager().findDevices();
        }
        if(vId == R.id.f18DeviceOtaItem){   //OTA升级

        }

        if(vId == R.id.f18DeviceUnbindItem){    //解绑
            Watch7018Manager.getWatch7018Manager().disConnectDevice();
        }

        if(vId == R.id.f18DeviceAppMsgItem){    //APP消息提醒
            startActivity(new Intent(F18WatchManagerActivity.this,F18AppsShowActivity.class));
        }



    }



    private final ItemDeviceSettingView.OnItemViewCheckedChangeListener onItemViewCheckedChangeListener = new ItemDeviceSettingView.OnItemViewCheckedChangeListener() {
        @Override
        public void onCheckedChanged(int id, boolean isChecked) {


            if(id == R.id.f18DeviceStrongItem){     //加强测量
                f18DeviceStrongItem.setChecked(isChecked);
                Watch7018Manager.getWatch7018Manager().setStrengthMeasure(isChecked);
            }
        }
    };




    private void requestCameraPermission(){
       // if(XXPermissions.isGranted(this, R.ma))
        Watch7018Manager.getWatch7018Manager().intoTakePhotoStatus(true);
        Intent intentCamara = new Intent(context, CamaraActivity1.class);
        startActivity(intentCamara);
    }



    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        Log.e(TAG,"---------查找返回="+f18DeviceSetData.toString());
        //闹钟数量
        f18DeviceAlarmItem.setContentText(f18DeviceSetData.getAlarmCount() == 0 ? "未开启" : ("已开启"+f18DeviceSetData.getAlarmCount()+"个"));
        //时间格式
        f18DeviceTimeStyleItem.setContentText(f18DeviceSetData.getTimeStyle() == 0 ? "12小时制" : "24小时制");
        //天气设置
        f18DeviceWeatherItem.setContentText(f18DeviceSetData.getCityName());
        //温度单位
        f18DeviceTempItem.setContentText(f18DeviceSetData.getTempStyle() == 0 ? "华摄氏度°F" : "摄氏度℃");
        //公英制
        f18DeviceUnitItem.setContentText(f18DeviceSetData.getIsKmUnit() == 0 ? "公制" : "英制");
        //24小时心率
        f18Device24HourItem.setChecked(f18DeviceSetData.isIs24Heart());
        //加强测量
        f18DeviceStrongItem.setChecked(f18DeviceSetData.isStrengthMeasure());
        //连续监测
        f18DeviceContractItem.setContentText(f18DeviceSetData.getContinuMonitor());
        //抬腕亮屏
        f18DeviceTurnScreenItem.setContentText(f18DeviceSetData.getTurnWrist());

    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        Log.e(TAG,"-------设置返回="+selectType+" "+type + " "+timeStr);
        if(selectType == SET_STEP_GOAL_CODE){   //计步目标
            f18DeviceStepGoalItem.setContentText(timeStr+"步");
            String tmpGoal = StringUtils.substringBefore(timeStr,"步");
            Watch7018Manager.getWatch7018Manager().setDeviceSportGoal(Integer.parseInt(tmpGoal.trim()),10,100);
        }
        if(selectType == SET_TIME_STYLE_CODE){      //时间格式
            f18DeviceTimeStyleItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTimeStyle(timeStr.contains("12"));
        }
        if(selectType == SET_TEMP_UNIT_CODE){   //温度单位
            f18DeviceTempItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTemperUnit(timeStr.contains("℃"));

        }

        if(selectType == SET_KM_UNIT_CODE){     //公英制
            f18DeviceUnitItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setKmUnit(timeStr.contains("公"));
        }



    }
}
