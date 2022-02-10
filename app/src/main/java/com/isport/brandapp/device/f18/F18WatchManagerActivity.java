package com.isport.brandapp.device.f18;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.ActivityWeatherSetting;
import com.isport.brandapp.device.bracelet.CamaraActivity1;
import com.isport.brandapp.device.f18.dial.F18DialActivity;
import com.isport.brandapp.device.publicpage.ActivityDeviceUnbindGuide;
import com.isport.brandapp.device.publicpage.GoActivityUtil;
import com.isport.brandapp.dialog.UnBindDeviceDialog;
import com.isport.brandapp.dialog.UnbindStateCallBack;
import com.isport.brandapp.util.DeviceTypeUtil;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

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
    //设置时间格式
    private final static int SET_TIME_STYLE_CODE = 0x01;
    //设置温度单位
    private final static int SET_TEMP_UNIT_CODE = 0x02;
    //公英制
    private final static int SET_KM_UNIT_CODE = 0x03;

    private F18DeviceSetData setData;


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

        String macStr = AppConfiguration.braceletID;
        if(macStr != null)
          mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),macStr);

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
           startActivity(new Intent(F18WatchManagerActivity.this, F18DialActivity.class));
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
            unBindF18Device();
        }

        if(vId == R.id.f18DeviceAppMsgItem){    //APP消息提醒
            startActivity(new Intent(F18WatchManagerActivity.this,F18AppsShowActivity.class));
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
                    // TODO: 2018/11/8 同步解绑的逻辑
//                                        if (FragmentData.mWristbandstep != null) {
//                                            mActPresenter.updateSportData(FragmentData.mWristbandstep, mDeviceBean);
//                                        }
                    Constants.isSyncUnbind = true;
                    BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();

                    Logger.myLog(TAG,"--------解绑="+new Gson().toJson(device));
                    if (device != null) {
                        int currentDevice = device.deviceType;
                        if (DeviceTypeUtil.isContainWatch(currentDevice)) {
                            //睡眠带连接
                            String string = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_DAILY_RECORD, string);
                           // canUnBind = true;
                        } else {
                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                        }
                    }
                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
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
//        isDerictUnBind = true;
//        currentType = deviceBean.currentType;
//        Logger.myLog("点击去解绑 == " + currentType+"\n"+deviceBean.toString());
        //解绑前断连设备
        Watch7018Manager.getWatch7018Manager().unBindDevice();
        mActPresenter.unBind(deviceBean, dirct);
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
        this.setData = f18DeviceSetData;

        //计步目标
        f18DeviceStepGoalItem.setContentText(f18DeviceSetData.getStepGoal()+"步");
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
        //勿扰模式
        f18DeviceDNTItem.setContentText(f18DeviceSetData.getDNT());
        //喝水提醒
        f18DeviceDrinkItem.setContentText(f18DeviceSetData.getDrinkAlert());
        //久坐提醒
        f18DeviceLongSitAlertItem.setContentText(f18DeviceSetData.getLongSitStr());
        //联系人
        f18DeviceContractItem.setContentText("已设置"+f18DeviceSetData.getContactNumber()+"个");
        //定时检测
        f18ContinueItem.setContentText(f18DeviceSetData.getContinuMonitor());
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        Log.e(TAG,"-------设置返回="+selectType+" "+type + " "+timeStr);
        if(selectType == SET_STEP_GOAL_CODE){   //计步目标
            f18DeviceStepGoalItem.setContentText(timeStr+"步");
            String tmpGoal = StringUtils.substringBefore(timeStr,"步");
            Watch7018Manager.getWatch7018Manager().setDeviceSportGoal(Integer.parseInt(tmpGoal.trim()),10,100);
            if(setData != null){
                setData.setStepGoal(Integer.parseInt(tmpGoal.trim()));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }

        }
        if(selectType == SET_TIME_STYLE_CODE){      //时间格式
            f18DeviceTimeStyleItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTimeStyle(timeStr.contains("12"));
            if(setData != null){
                setData.setTimeStyle(timeStr.contains("12") ? 1 : 0);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
        }
        if(selectType == SET_TEMP_UNIT_CODE){   //温度单位
            f18DeviceTempItem.setContentText(timeStr);
            Watch7018Manager.getWatch7018Manager().setTemperUnit(timeStr.contains("℃"));
            if(setData != null){
                setData.setTempStyle(timeStr.contains("℃") ? 1 : 0);
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18WatchManagerActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(setData));
            }
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
            EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
            Intent intent = new Intent(context, ActivityDeviceUnbindGuide.class);
            context.startActivity(intent);
            finish();
        }

    }

}
