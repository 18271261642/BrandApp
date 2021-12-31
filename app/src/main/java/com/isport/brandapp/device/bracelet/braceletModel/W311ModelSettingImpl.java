package com.isport.brandapp.device.bracelet.braceletModel;

import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.DeviceTempUnitlTableAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_AlarmModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_DeviceInfoModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_HrAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_SettingModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_liftwristModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W560_AlarmModelAction;
import com.isport.blelibrary.db.table.DeviceTempUnitlTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24H_hr_SettingModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DeviceInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DisplayModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_WearModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_w311_hrModel;
import com.isport.blelibrary.db.table.w811w814.DeviceTimeFormat;
import com.isport.blelibrary.db.table.w811w814.FaceWatchMode;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.blelibrary.gen.Bracelet_W311_DeviceInfoModelDao;
import com.isport.blelibrary.utils.Logger;

import java.util.ArrayList;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;

public class W311ModelSettingImpl implements IW311SettingModel {


    //删除保存所有的通用闹钟
    public void deleteAllSaveCommAlarm(String userId,String deviceId){
        Bracelet_W311_AlarmModelAction.deletBracelet(deviceId,userId);
    }



    @Override
    public Bracelet_W311_DisplayModel getDispalyItem(String userId, String deviceId) {
        Bracelet_W311_DisplayModel bracelet_w311_displayModel = Bracelet_W311_SettingModelAction.findWatch_W311_DisplayModelByDeviceId(deviceId);
        return bracelet_w311_displayModel;
    }

    @Override
    public boolean saveDisplayItem(Bracelet_W311_DisplayModel model) {
        long isSave = Bracelet_W311_SettingModelAction.saveOrUpdateBraceletDisplay(model);
        return true;
    }

    @Override
    public Bracelet_W311_LiftWristToViewInfoModel getLifWristToViewInfo(String userId, String deviceId) {
        Bracelet_W311_LiftWristToViewInfoModel bracelet_w311_liftWristToViewInfoModel = Bracelet_W311_liftwristModelAction.findBracelet_W311_liftwrist(deviceId);
        return bracelet_w311_liftWristToViewInfoModel;
    }

    @Override
    public boolean saveLifWristToViewInfo(Bracelet_W311_LiftWristToViewInfoModel model) {
        long isSave = Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);
        return true;
    }

    @Override
    public Bracelet_W311_WearModel getWearModel(String userId, String deviceId) {
        Bracelet_W311_WearModel bracelet_w311_wearModel = Bracelet_W311_SettingModelAction.findBracelet_W311_WearModel(deviceId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        return bracelet_w311_wearModel;
    }

    @Override
    public boolean saveWearModel(Bracelet_W311_WearModel model) {
        long isSave = Bracelet_W311_SettingModelAction.saveOrUpdateBraceletWearModel(model);
        return true;
    }

    @Override
    public Bracelet_W311_ThridMessageModel getW311ThridMessageItem(String userId, String deviceId) {
        Bracelet_W311_ThridMessageModel bracelet_w311_thridMessageModel = Bracelet_W311_SettingModelAction.findBracelet_W311_ThridMessage(deviceId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        return bracelet_w311_thridMessageModel;
    }

    @Override
    public boolean saveW311ThridItem(Bracelet_W311_ThridMessageModel model) {
        long isSave = Bracelet_W311_SettingModelAction.saveOrUpdateBraceletThridMessage(model);
        return true;
    }

    @Override
    public Bracelet_w311_hrModel getHrSetting(String userId, String deviceId) {
        Bracelet_w311_hrModel bracelet_w311_hrModel = Bracelet_W311_HrAction.findBracelet_W311_HrSetting(deviceId);
        return bracelet_w311_hrModel;
    }

    @Override
    public boolean saveHrSetting(Bracelet_w311_hrModel model) {
        Bracelet_W311_HrAction.saveOrUpdateBraceletAutoHr(model);
        return true;
    }

    @Override
    public ArrayList<Bracelet_W311_AlarmModel> getAllAlarm(String deviceId, String userId) {
        ArrayList<Bracelet_W311_AlarmModel> list = Bracelet_W311_AlarmModelAction.findBracelet_W311_AlarmModelByDeviceId(deviceId, userId);
        return list;
    }

    @Override
    public ArrayList<Bracelet_W311_AlarmModel> getAllAlarmW526(String deviceId, String userId) {
        ArrayList<Bracelet_W311_AlarmModel> list = Bracelet_W311_AlarmModelAction.findBracelet_W311_AlarmModelByDeviceId(deviceId, userId);
        if (list == null) {
            for (int i = 0; i < 3; i++) {
                Bracelet_W311_AlarmModelAction.saveW526AlarmBean(i, userId, deviceId, 128, "00:00", false);
            }
            list = Bracelet_W311_AlarmModelAction.findBracelet_W311_AlarmModelByDeviceId(deviceId, userId);
        }
        return list;

    }

    @Override
    public ArrayList<Watch_W560_AlarmModel> getAllAlarmW560(String deviceId, String userId) {
        ArrayList<Watch_W560_AlarmModel> list = Watch_W560_AlarmModelAction.findWatch_W560_AlarmModelByDeviceId(deviceId, userId);

        return list;
    }

    @Override
    public boolean updateAlarmModel(Bracelet_W311_AlarmModel model) {
        Bracelet_W311_AlarmModelAction.saveOrUpdateBraceletW311AlarmModele(model);
        return true;
    }

    @Override
    public boolean updateW560AlarmModel(Watch_W560_AlarmModel model) {
        Watch_W560_AlarmModelAction.saveOrUpdateBraceletW560AlarmModele(model);
        return true;
    }

    @Override
    public boolean updateAllAlarmModel(ArrayList<Bracelet_W311_AlarmModel> lists, String deviceId, String userId) {

        Bracelet_W311_AlarmModelAction.saveOrUpdateBraceletW311AlarmModele(lists, deviceId, userId);

        return false;
    }

    @Override
    public boolean deletArarmItem(Bracelet_W311_AlarmModel model) {
        Bracelet_W311_AlarmModelAction.deletBraceletalarmItem(model);
        return true;
    }

    @Override
    public boolean deletW560ArarmItem(Watch_W560_AlarmModel model) {
        Watch_W560_AlarmModelAction.deletWatchAlarmItem(model);
        return true;
    }

    @Override
    public boolean deletW560ArarmItems(String userId, String deviceId) {
        Watch_W560_AlarmModelAction.deletWatchAlarmItems(deviceId, userId);
        return true;
    }

    @Override
    public Bracelet_W311_DeviceInfoModel getDevieVersion(String userId, String deviceId) {
        Bracelet_W311_DeviceInfoModel model = Bracelet_W311_DeviceInfoModelAction.findBraceletW311DeviceInfo(userId, deviceId);
        // Logger.myLog("getDevieVersion" + model.toString());
        return model;
    }

    @Override
    public boolean saveDevieVersion(Bracelet_W311_DeviceInfoModel model) {
        Bracelet_W311_DeviceInfoModelDao bracelet_w311_deviceInfoModelDao = BleAction.getsBracelet_w311_deviceInfoModelDao();
        long id = bracelet_w311_deviceInfoModelDao.insertOrReplace(model);
        return true;
    }

    @Override
    public Bracelet_W311_24H_hr_SettingModel get24HSwitchState(String userId, String devcieId) {
        Bracelet_W311_24H_hr_SettingModel model = Bracelet_W311_SettingModelAction.findBracelet_w311_24h_hrModel(devcieId, userId);
        Logger.myLog("w311 get24HSwitchState" + model.toString());
        return model;
    }

    @Override
    public boolean save24HhrSwitchState(Bracelet_W311_24H_hr_SettingModel model) {
        Bracelet_W311_SettingModelAction.saveOrUpateBracelet24HHrSetting(model);
        Logger.myLog("w311 save24HhrSwitchState" + model.toString());
        return true;
    }

    @Override
    public boolean saveWatchFacesSetting(String devcieId, String userId, int faceMode) {
        Bracelet_W311_SettingModelAction.saveOrUpdateWatchFaces(devcieId, userId, faceMode);
        return true;
    }

    @Override
    public FaceWatchMode getWatchModeSetting(String userId, String devcieId) {
        FaceWatchMode watchMode = Bracelet_W311_SettingModelAction.findWatchFaces(userId, devcieId);
        return watchMode;
    }

    @Override
    public boolean saveTimeFormat(String deviceId, String userId, int format) {
        Bracelet_W311_SettingModelAction.saveOrUpdateDeviceTimeFormate(deviceId, userId, format);
        return true;
    }

    @Override
    public DeviceTimeFormat getTimeFormate(String deviceId, String userId) {
        return Bracelet_W311_SettingModelAction.getDeviceFormatBean(deviceId, userId);
    }

    @Override
    public DeviceTempUnitlTable getTempUtil(String userId, String deviceId) {
        DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
        return action.findTempUnitl(deviceId, userId);
    }

    @Override
    public boolean saveTempUtil(String userId, String deviceId, String unitl) {
        DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
        action.saveTempUnitlModel(deviceId, userId, unitl);
        return true;
    }


}
