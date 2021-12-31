package com.isport.blelibrary.db.parse;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.isport.blelibrary.db.CommonInterFace.WatchData;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.DeviceInformationTableAction;
import com.isport.blelibrary.db.action.DeviceSetting.DeviceSettingAction;
import com.isport.blelibrary.db.action.DeviceTempUnitlTableAction;
import com.isport.blelibrary.db.action.DeviceTypeTableAction;
import com.isport.blelibrary.db.action.W81Device.W81DeviceEexerciseAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_AlarmModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_HrAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_SettingModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_liftwristModelAction;
import com.isport.blelibrary.db.action.watch.Watch_SmartBand_HandScreenModelAction;
import com.isport.blelibrary.db.action.watch.Watch_SmartBand_ScreenTimeModelAction;
import com.isport.blelibrary.db.action.watch.Watch_SmartBand_StepTargetModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_24HDataModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_AlarmModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_NotifyModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SedentaryModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SettingModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SleepAndNoDisturbModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W560_AlarmModelAction;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.DeviceTypeTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24HDataModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_w311_hrModel;
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_SleepNoticeModel;
import com.isport.blelibrary.db.table.w811w814.W81DeviceExerciseData;
import com.isport.blelibrary.db.table.watch.Watch_SmartBand_HandScreenModel;
import com.isport.blelibrary.db.table.watch.Watch_SmartBand_ScreenTimeModel;
import com.isport.blelibrary.db.table.watch.Watch_SmartBand_SportDataModel;
import com.isport.blelibrary.db.table.watch.Watch_SmartBand_StepTargetModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_AlarmModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_NotifyModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SettingModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepAndNoDisturbModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.gen.Bracelet_W311_24HDataModelDao;
import com.isport.blelibrary.gen.DeviceInformationTableDao;
import com.isport.blelibrary.gen.DeviceTypeTableDao;
import com.isport.blelibrary.gen.Scale_FourElectrode_DataModelDao;
import com.isport.blelibrary.gen.Sleep_Sleepace_DataModelDao;
import com.isport.blelibrary.gen.Sleep_Sleepace_SleepNoticeModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_HandScreenModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_ScreenTimeModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_SportDataModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_StepTargetModelDao;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.gen.Watch_W516_AlarmModelDao;
import com.isport.blelibrary.gen.Watch_W516_SedentaryModelDao;
import com.isport.blelibrary.gen.Watch_W516_SettingModelDao;
import com.isport.blelibrary.gen.Watch_W516_SleepAndNoDisturbModelDao;
import com.isport.blelibrary.gen.Watch_W560_AlarmModelDao;
import com.isport.blelibrary.interfaces.BluetoothListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.observe.W560HrSwtchObservable;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @Author
 * @Date 2019/1/7
 * @Fuction
 */

public class ParseData {

    private static final String TAG = "ParseData";

    private static BaseManager baseManager;


    public static BaseManager getBaseManager(){
        if(baseManager == null)
            baseManager = new BaseManager();
        return baseManager;
    }


    public static int getDeviceTypeId(){
        baseManager = getBaseManager();
        BaseDevice baseDevice = baseManager.getCurrentDevice();
        return baseDevice == null ? 0 : baseDevice.getDeviceType();
    }

    /**
     * 首先判断表里是否存在这个deviceId的数据,不存在则插入，存在则更新
     * 0 电量  1版本号 -1 其他信息
     *
     * @param deviceInformationTable
     */
    public static void saveOrUpdateDeviceInfo(final DeviceInformationTable deviceInformationTable, final int dataType) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                DeviceInformationTableDao deviceInformationTableDao = BleAction
                        .getDeviceInformationTableDao();
                DeviceInformationTable deviceInfoByDeviceId = DeviceInformationTableAction.findDeviceInfoByDeviceId(deviceInformationTable.getDeviceId());
                if (deviceInfoByDeviceId == null) {
                    //直接插入
                    deviceInformationTableDao.insertOrReplace(deviceInformationTable);
                } else {
                    switch (dataType) {
                        case 0:
                            Log.e("saveOrUpdateDeviceInfo", deviceInformationTable.getBattery() + "");
                            deviceInfoByDeviceId.setBattery(deviceInformationTable.getBattery());
                            break;
                        case 1:
                            String version = deviceInformationTable.getVersion();
                            if (!TextUtils.isEmpty(version)) {
                                version = version.replace("V", "");
                                Log.e("saveOrUpdateDeviceInfo", deviceInformationTable.getVersion() + "");
                                deviceInfoByDeviceId.setVersion(version);
                            }

                            break;

                    }
                    deviceInfoByDeviceId.setDeviceId(deviceInformationTable.getDeviceId());
                    deviceInfoByDeviceId.setMac(deviceInformationTable.getMac());
                    deviceInformationTableDao.update(deviceInfoByDeviceId);
                }
            }
        });
    }

    /**
     * 同时要存储设备类型数据,deviceName是内部协定好的
     */
    public static void saveDeviceType(final int deviveType, final String mac, final String deviceId, final String
            userId, final String deviceName) {

        Logger.myLog(TAG,"------saveDevicdeType="+deviveType+"\n"+mac+"\n"+deviceId+"\n"+userId+"\n"+deviceName);

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (!DeviceTypeTableAction.hasStoreDeviceType(deviveType, userId)) {
                    DeviceTypeTableDao deviceTypeTableDao = BleAction.getDeviceTypeTableDao();
                    //查询是否存在此类型的设备，存在则不存储，不存在则存储,
                    DeviceTypeTable deviceTypeTable = new DeviceTypeTable();
                    deviceTypeTable.setDeviceType(deviveType);
                    deviceTypeTable.setMac(mac);
                    deviceTypeTable.setDeviceId(deviceId);
                    deviceTypeTable.setUserId(userId);
                    // TODO: 2019/1/26 服务器返回时间
                    deviceTypeTable.setTimeTamp(System.currentTimeMillis());
                    switch (deviveType) {
                        case IDeviceType.TYPE_WATCH:
                            deviceTypeTable.setDeviceName("SmartBand");
                            break;
                        case IDeviceType.TYPE_SCALE:
                            deviceTypeTable.setDeviceName("MZ");
                            break;
                        case IDeviceType.TYPE_SLEEP:
                            deviceTypeTable.setDeviceName("Sleepace");
                            break;
                        case IDeviceType.TYPE_BRAND_W311:
                            deviceTypeTable.setDeviceName("W311");
                            break;
                        case IDeviceType.TYPE_BRAND_W520:
                            deviceTypeTable.setDeviceName("W520");
                            break;
                        default:
                            break;
                    }
                    deviceTypeTable.setDeviceName(deviceName);
                    Logger.myLog(TAG,"绑定设备="+new Gson().toJson(deviceTypeTable));
                    deviceTypeTableDao.insertOrReplace(deviceTypeTable);
                }
            }
        });
    }


    /**
     * 同时要存储设备类型数据,deviceName是内部协定好的
     */
    public static void saveWatchSmartBandSportDataModels(final List<Watch_SmartBand_SportDataModel>
                                                                 mWatch_smartBand_sportDataModels) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mWatch_smartBand_sportDataModels.size(); i++) {
                    Logger.myLog("mWatch_smartBand_sportDataModels== " + mWatch_smartBand_sportDataModels.get(i)
                            .toString());
                }
                Watch_SmartBand_SportDataModelDao watch_smartBand_sportDataModelDao = BleAction
                        .getWatch_SmartBand_SportDataModelDao();
                watch_smartBand_sportDataModelDao.insertOrReplaceInTx(mWatch_smartBand_sportDataModels);
            }
        });
    }

    /**
     * 同时要存储设备类型数据,deviceName是内部协定好的
     */
    public static void saveWatchSmartBandSportDataModel(final Watch_SmartBand_SportDataModel
                                                                mWatch_smartBand_sportDataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_SmartBand_SportDataModelDao watch_smartBand_sportDataModelDao = BleAction
                        .getWatch_SmartBand_SportDataModelDao();
                watch_smartBand_sportDataModelDao.insertOrReplace(mWatch_smartBand_sportDataModel);
            }
        });
    }

    /**
     * 同时要存储设备类型数据,deviceName是内部协定好的
     */
    public static void saveSleep_Sleepace_DataModel(final Sleep_Sleepace_DataModel
                                                            mSleep_Sleepace_DataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Sleep_Sleepace_DataModelDao sleep_sleepace_dataModelDao = BleAction
                        .getSleep_Sleepace_DataModelDao();
                sleep_sleepace_dataModelDao.insert(mSleep_Sleepace_DataModel);
            }
        });
    }

    /**
     * 同时要存储设备类型数据,deviceName是内部协定好的
     */
    public static void saveBracelet_W311_24HDataModel(final Bracelet_W311_24HDataModel
                                                              mWatch_W516_24HDataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getsBracelet_w311_24HDataModelDao();
                watch_w516_24HDataModelDao.insert(mWatch_W516_24HDataModel);
            }
        });
    }

    public static void updateBracelet_W311_24HDataModel(final Bracelet_W311_24HDataModel
                                                                mWatch_W516_24HDataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getsBracelet_w311_24HDataModelDao();
                watch_w516_24HDataModelDao.insertOrReplace(mWatch_W516_24HDataModel);
            }
        });
    }

    /**
     * 同时要存储设备类型数据,deviceName是内部协定好的
     */
    public static void saveWatch_W516_24HDataModel(final Watch_W516_24HDataModel
                                                           mWatch_W516_24HDataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getWatch_W516_24HDataModelDao();
                watch_w516_24HDataModelDao.insert(mWatch_W516_24HDataModel);
            }
        });
    }

    public static void updateWatch_W516_24HDataModel(final Watch_W516_24HDataModel
                                                             mWatch_W516_24HDataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
              //  Logger.myLog("updateWatch_W516_24HDataModel" + mWatch_W516_24HDataModel.toString());
                Watch_W516_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getWatch_W516_24HDataModelDao();
                watch_w516_24HDataModelDao.insertOrReplace(mWatch_W516_24HDataModel);
            }
        });
    }

    /**
     * 解绑删除该类型的设备
     */
    public static void deleteDeviceType(final int deviveType, final String userId) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (DeviceTypeTableAction.hasStoreDeviceType(deviveType, userId))
                    DeviceTypeTableAction.deleteDeviceByType(deviveType, userId);
            }
        });
    }

    /**
     * 存储体脂称数据
     */
    public static void saveScaleFourElectrodeData(final Scale_FourElectrode_DataModel scale_fourElectrode_dataModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Scale_FourElectrode_DataModelDao scale_fourElectrode_dataModelDao = BleAction
                        .getScale_FourElectrode_DataModelDao();
                scale_fourElectrode_dataModelDao.insert(scale_fourElectrode_dataModel);
            }
        });
    }

    /**
     * 存储手表目标
     */
    public static void saveOrUpdateWatchSmartBandTarget(final Watch_SmartBand_StepTargetModel
                                                                watch_smartBand_stepTargetModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_SmartBand_StepTargetModelDao watch_smartBand_stepTargetModelDao = BleAction
                        .getWatch_SmartBand_StepTargetModelDao();
                Watch_SmartBand_StepTargetModel targetModelByDeviceId = Watch_SmartBand_StepTargetModelAction
                        .findTargetModelByDeviceId
                                (watch_smartBand_stepTargetModel
                                        .getDeviceId());
                if (targetModelByDeviceId != null) {
                    targetModelByDeviceId.setTarget(watch_smartBand_stepTargetModel.getTarget());
                    watch_smartBand_stepTargetModelDao.insertOrReplace(targetModelByDeviceId);
                } else {
                    watch_smartBand_stepTargetModelDao.insertOrReplace(watch_smartBand_stepTargetModel);
                }
            }
        });
    }

    /**
     * 存储手表亮屏时间
     */
    public static void saveOrUpdateWatchSmartBandScreentTime(final Watch_SmartBand_ScreenTimeModel
                                                                     watch_smartBand_screenTimeModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_SmartBand_ScreenTimeModelDao watch_smartBand_screenTimeModelDao = BleAction
                        .getWatch_SmartBand_ScreenTimeModelDao();
                Watch_SmartBand_ScreenTimeModel screenTimeModelByDeviceId = Watch_SmartBand_ScreenTimeModelAction
                        .findScreenTimeModelByDeviceId(watch_smartBand_screenTimeModel
                                .getDeviceId());
                if (screenTimeModelByDeviceId != null) {

                    screenTimeModelByDeviceId.setTime(watch_smartBand_screenTimeModel.getTime());
                    watch_smartBand_screenTimeModelDao.insertOrReplace(screenTimeModelByDeviceId);
                } else {
                    watch_smartBand_screenTimeModelDao.insertOrReplace(watch_smartBand_screenTimeModel);
                }
            }
        });
    }

    /**
     * 存储W516的来电和消息设置
     */

    /**
     * @param deviceName
     * @param userId
     * @param reminderSwitch
     * @param type           0为来电，1为短信
     */

    public static void saveW516CallMessageRemind(final String deviceName, final String userId, final boolean reminderSwitch, final int type) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(deviceName, userId);

                if (watch_w516_notifyModelByDeviceId == null) {
                    watch_w516_notifyModelByDeviceId = new Watch_W516_NotifyModel();
                    watch_w516_notifyModelByDeviceId.setUserId(userId);
                    watch_w516_notifyModelByDeviceId.setDeviceId(deviceName);
                    watch_w516_notifyModelByDeviceId.setMsgSwitch(false);
                    watch_w516_notifyModelByDeviceId.setCallSwitch(false);
                }
//        if (!((msgSwitch&&watch_w516_notifyModelByDeviceId.getMsgSwitch())||(!msgSwitch&&!watch_w516_notifyModelByDeviceId.getMsgSwitch()))){
                if (type == 1) {
                    watch_w516_notifyModelByDeviceId.setMsgSwitch(reminderSwitch);
                } else {
                    watch_w516_notifyModelByDeviceId.setCallSwitch(reminderSwitch);
                }
                BleAction.getWatch_W516_NotifyModelDao().insertOrReplace(watch_w516_notifyModelByDeviceId);
            }
        });
    }

    /**
     * 存储W516设置
     */
    public static void saveOrUpdateWatchW516Setting(final Watch_W516_SettingModel
                                                            watch_W516_SettingModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_SettingModelDao watch_w516_settingModelDao = BleAction
                        .getWatch_W516_SettingModelDao();
                Watch_W516_SettingModel w516SettingModelByDeviceId = Watch_W516_SettingModelAction
                        .findW516SettingModelByDeviceId(watch_W516_SettingModel
                                .getDeviceId(), BaseManager.mUserId);
                if (w516SettingModelByDeviceId != null) {
                    w516SettingModelByDeviceId.setUserId(watch_W516_SettingModel.getUserId());
                    w516SettingModelByDeviceId.setUnit(watch_W516_SettingModel.getUnit());
                    w516SettingModelByDeviceId.setLanguage(watch_W516_SettingModel.getLanguage());
                    w516SettingModelByDeviceId.setHeartRateSwitch(watch_W516_SettingModel.getHeartRateSwitch());
                    w516SettingModelByDeviceId.setBrightScreen(watch_W516_SettingModel.getBrightScreen());
                    w516SettingModelByDeviceId.setTimeFormat(watch_W516_SettingModel.getTimeFormat());
                    watch_w516_settingModelDao.insertOrReplace(w516SettingModelByDeviceId);
                } else {
                    watch_w516_settingModelDao.insertOrReplace(watch_W516_SettingModel);
                }
            }
        });
    }

    /**
     * 存储W516闹钟
     */
    public static void saveOrUpdateWatchW516Alarm(final Watch_W516_AlarmModel
                                                          watch_w516_alarmModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_AlarmModelDao watch_w516_alarmModelDao = BleAction
                        .getWatch_W516_AlarmModelDao();
                Watch_W516_AlarmModel watch_w516_alarmModelByDeviceId = Watch_W516_AlarmModelAction
                        .findWatch_W516_AlarmModelByDeviceId(watch_w516_alarmModel
                                .getDeviceId(), BaseManager.mUserId);
                if (watch_w516_alarmModelByDeviceId != null) {
                    watch_w516_alarmModelByDeviceId.setUserId(watch_w516_alarmModel.getUserId());
                    watch_w516_alarmModelByDeviceId.setTimeString(watch_w516_alarmModel.getTimeString());
                    watch_w516_alarmModelByDeviceId.setRepeatCount(watch_w516_alarmModel.getRepeatCount());
                    watch_w516_alarmModelByDeviceId.setMessageString(watch_w516_alarmModel.getMessageString());
                    watch_w516_alarmModelDao.insertOrReplace(watch_w516_alarmModelByDeviceId);
                } else {
                    watch_w516_alarmModelDao.insertOrReplace(watch_w516_alarmModel);
                }
            }
        });
    }

    /**
     * 存储W560闹钟
     */
    public static void saveOrUpdateWatchW560Alarm(final Watch_W560_AlarmModel watch_w560_alarmModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W560_AlarmModelDao watch_w560_alarmModelDao = BleAction.getWatch_W560_AlarmModelDao();
                Watch_W560_AlarmModel watch_w560_alarmModelByDeviceId = Watch_W560_AlarmModelAction
                        .findWatch_W560_AlarmModelByDeviceId(watch_w560_alarmModel
                                .getDeviceId(), BaseManager.mUserId, watch_w560_alarmModel.getIndex());


                if (watch_w560_alarmModelByDeviceId != null) {
                    watch_w560_alarmModelByDeviceId.setUserId(watch_w560_alarmModel.getUserId());
                    watch_w560_alarmModelByDeviceId.setTimeString(watch_w560_alarmModel.getTimeString());
                    watch_w560_alarmModelByDeviceId.setRepeatCount(watch_w560_alarmModel.getRepeatCount());
                    watch_w560_alarmModelByDeviceId.setIndex(watch_w560_alarmModel.getIndex());
                    watch_w560_alarmModelByDeviceId.setIsEnable(watch_w560_alarmModel.getIsEnable());
                    watch_w560_alarmModelByDeviceId.setName(watch_w560_alarmModel.getName());
                    watch_w560_alarmModelByDeviceId.setMessageString(watch_w560_alarmModel.getMessageString());
                    watch_w560_alarmModelDao.insertOrReplace(watch_w560_alarmModelByDeviceId);
                } else {
                    watch_w560_alarmModelDao.insertOrReplace(watch_w560_alarmModel);
                }
            }
        });
    }

    /**
     * 存储W516睡眠设置
     */
    public synchronized static void saveOrUpdateWatchW516SleepAndNoDisturb(final Watch_W516_SleepAndNoDisturbModel
                                                                                   watch_w516_sleepAndNoDisturbModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_SleepAndNoDisturbModelDao watch_w516_sleepAndNoDisturbModelDao = BleAction
                        .getWatch_W516_SleepAndNoDisturbModelDao();
                Watch_W516_SleepAndNoDisturbModel watch_w516_sleepAndNoDisturbModelyDeviceId =
                        Watch_W516_SleepAndNoDisturbModelAction
                                .findWatch_W516_SleepAndNoDisturbModelyDeviceId(watch_w516_sleepAndNoDisturbModel.getUserId(), watch_w516_sleepAndNoDisturbModel
                                        .getDeviceId());
                if (watch_w516_sleepAndNoDisturbModelyDeviceId != null) {
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setUserId(watch_w516_sleepAndNoDisturbModel.getUserId());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setAutomaticSleep(watch_w516_sleepAndNoDisturbModel
                            .getAutomaticSleep());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setSleepRemind(watch_w516_sleepAndNoDisturbModel
                            .getSleepRemind());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setOpenNoDisturb(watch_w516_sleepAndNoDisturbModel
                            .getOpenNoDisturb());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setSleepStartTime(watch_w516_sleepAndNoDisturbModel
                            .getSleepStartTime());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setSleepEndTime(watch_w516_sleepAndNoDisturbModel
                            .getSleepEndTime());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setNoDisturbStartTime
                            (watch_w516_sleepAndNoDisturbModel.getNoDisturbStartTime());
                    watch_w516_sleepAndNoDisturbModelyDeviceId.setNoDisturbEndTime(watch_w516_sleepAndNoDisturbModel
                            .getNoDisturbEndTime());
                    watch_w516_sleepAndNoDisturbModelDao.insertOrReplace(watch_w516_sleepAndNoDisturbModelyDeviceId);
                } else {
                    watch_w516_sleepAndNoDisturbModelDao.insertOrReplace(watch_w516_sleepAndNoDisturbModel);
                }
            }
        });
    }


    /**
     * 存储W516睡眠设置
     */
    public static void
    saveOrUpdateWatchW516Sedentary(final Watch_W516_SedentaryModel watch_w516_sedentaryModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_SedentaryModelDao watch_w516_sedentaryModelDao = BleAction
                        .getWatch_W516_SedentaryModelDao();
                Watch_W516_SedentaryModel watch_w516_watch_w516_sedentaryModelyDeviceId =
                        Watch_W516_SedentaryModelAction
                                .findWatch_W516_Watch_W516_SedentaryModelyDeviceId(watch_w516_sedentaryModel
                                        .getDeviceId(), BaseManager.mUserId);

                Logger.myLog("saveOrUpdateWatchW516Sedentary:" + watch_w516_sedentaryModel
                        .getDeviceId() + "userId:" + BaseManager.mUserId);

                if (watch_w516_watch_w516_sedentaryModelyDeviceId != null) {
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setUserId(watch_w516_sedentaryModel.getUserId());
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setLongSitTimeLong(watch_w516_sedentaryModel
                            .getLongSitTimeLong());
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setIsEnable(watch_w516_sedentaryModel.getIsEnable());
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setLongSitStartTime(watch_w516_sedentaryModel.getLongSitStartTime());
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setLongSitEndTime(watch_w516_sedentaryModel.getLongSitEndTime());
                    watch_w516_sedentaryModelDao.insertOrReplace(watch_w516_watch_w516_sedentaryModelyDeviceId);
                } else {
                    watch_w516_sedentaryModelDao.insertOrReplace(watch_w516_sedentaryModel);
                }
            }
        });
    }

    /**
     * 存储手表翻腕亮屏
     */
    public static void saveOrUpdateWatchSmartBandHandScreen(final Watch_SmartBand_HandScreenModel
                                                                    watch_smartBand_handScreenModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_SmartBand_HandScreenModelDao watch_smartBand_handScreenModelDao = BleAction
                        .getWatch_SmartBand_HandScreenModelDao();
                Watch_SmartBand_HandScreenModel handScreenModelByDeviceId = Watch_SmartBand_HandScreenModelAction
                        .findHandScreenModelByDeviceId(watch_smartBand_handScreenModel
                                .getDeviceId());
                if (handScreenModelByDeviceId != null) {
                    handScreenModelByDeviceId.setIsOpen(watch_smartBand_handScreenModel.getIsOpen());
                    watch_smartBand_handScreenModelDao.insertOrReplace(handScreenModelByDeviceId);
                } else {
                    watch_smartBand_handScreenModelDao.insertOrReplace(watch_smartBand_handScreenModel);
                }
            }
        });
    }

    /**
     * 存储睡眠带自动睡眠时间
     */
    public static void saveOrUpdateSleepaceAutoCollection(final Sleep_Sleepace_SleepNoticeModel
                                                                  sleep_sleepace_sleepNoticeModel) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Sleep_Sleepace_SleepNoticeModelDao sleep_sleepace_sleepNoticeModelDao = BleAction
                        .getSleep_Sleepace_SleepNoticeModelDao();
//                if (!Sleep_Sleepace_SleepNoticeModelAction.hasStoreDeviceType(sleep_sleepace_sleepNoticeModel
//                                                                                      .getDeviceId())) {
                sleep_sleepace_sleepNoticeModelDao.insertOrReplace(sleep_sleepace_sleepNoticeModel);
            }
        });
    }

    public static void parsW526PractiseData(List<byte[]> m24HDATA, BluetoothListener bluetoothListener, BaseDevice baseDevice, Context context) {

        Logger.myLog(TAG,"--所有原始数据="+new Gson().toJson(m24HDATA));

        try {
            byte[] bytesFirst = new byte[40];//减去第一包剩下的数据集合
            byte[] tmp = m24HDATA.get(0);
            System.arraycopy(tmp, 0, bytesFirst, 0, 20);
            byte[] tmp2 = m24HDATA.get(1);
            System.arraycopy(tmp2, 0, bytesFirst, 20, tmp2.length);
            //0   锻炼序号[0,49]
            StringBuffer stringBuilder = new StringBuffer();
            for (byte byteChar : bytesFirst) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            Logger.myLog("valuse:" + stringBuilder.toString());
            int index = Utils.byte2Int(bytesFirst[0]);
            //1   运动类型[1-8] 健走 跑步 骑行 登山 足球 篮球 乒乓球 羽毛球
            int sportType = Utils.byte2Int(bytesFirst[1]);
            //2   平均心率
            int avgHr = Utils.byte2Int(bytesFirst[2]);
            //   3   年(年份-2000)  运动开始时间
            int startyear = (Utils.byte2Int(bytesFirst[3])) + 2000;
            //4   运动开始时间 月
            int startmonth = Utils.byte2Int(bytesFirst[4]);
            //5   日
            int startday = Utils.byte2Int(bytesFirst[5]);
            //  6   时
            int starthour = Utils.byte2Int(bytesFirst[6]);
            //7   分
            int startMin = Utils.byte2Int(bytesFirst[7]);
            //8   秒
            int startSecd = Utils.byte2Int(bytesFirst[8]);
            //9   运动总时间 时
            int sumSportHour = Utils.byte2Int(bytesFirst[9]);
            //10  分
            int sumSportMin = Utils.byte2Int(bytesFirst[10]);
            //11  秒
            int sumSportSecend = Utils.byte2Int(bytesFirst[11]);
            // 12  年(年份-2000)  运动结束时间
            int endyear = (Utils.byte2Int(bytesFirst[12])) + 2000;
            //    13  月
            int endMonth = (Utils.byte2Int(bytesFirst[13]));
            //    14  日
            int endDay = (Utils.byte2Int(bytesFirst[14]));
            //    15  时
            int endHour = (Utils.byte2Int(bytesFirst[15]));
            //    16  分
            int endMin = (Utils.byte2Int(bytesFirst[16]));
            //    17  秒
            int endSecd = (Utils.byte2Int(bytesFirst[17]));
            //
            //  18-20 运动总步数  低位在前
            int step = (Utils.byte2Int(bytesFirst[20]) << 16)
                    + (Utils.byte2Int(bytesFirst[19]) << 8) + Utils.byte2Int(bytesFirst[18]);
            //   21-24 运动平均速度
            int speed = (Utils.byte2Int(bytesFirst[24]) << 24) + (Utils.byte2Int(bytesFirst[23]) << 16)
                    + (Utils.byte2Int(bytesFirst[22]) << 8) + Utils.byte2Int(bytesFirst[21]);
            // 25-28 运动总距离
            int dis = (Utils.byte2Int(bytesFirst[28]) << 24) + (Utils.byte2Int(bytesFirst[27]) << 16)
                    + (Utils.byte2Int(bytesFirst[26]) << 8) + Utils.byte2Int(bytesFirst[25]);
            // 29-32 运动总卡路里
            int cal = (Utils.byte2Int(bytesFirst[32]) << 24) + (Utils.byte2Int(bytesFirst[31]) << 16)
                    + (Utils.byte2Int(bytesFirst[30]) << 8) + Utils.byte2Int(bytesFirst[29]);

            //平均速度
            int tmpAvgSpeed = getIntFromBytes(bytesFirst[24],bytesFirst[23],bytesFirst[22],bytesFirst[21]);
            int tmpCountDistance = getIntFromBytes(bytesFirst[28],bytesFirst[27],bytesFirst[26],bytesFirst[25]);
            int tmpCountCalor = getIntFromBytes(bytesFirst[32],bytesFirst[31],bytesFirst[30],bytesFirst[29]);

            Logger.myLog(TAG,"----------锻炼总数据解析="+tmpAvgSpeed+"\n"+tmpCountDistance+"\n"+tmpCountCalor);



            //W81DeviceEexerciseAction w81DeviceEexerciseAction = new W81DeviceEexerciseAction();
            //w81DeviceEexerciseAction.saveExerciseHrData(String.valueOf(BaseManager.mUserId), baseDevice.getDeviceName(), info.getMeasureData(), info.getTimeInterval(), info.getStartMeasureTime());

            // Logger.myLog("锻炼序号： bytesFirst[20]" + bytesFirst[20] + ",(bytesFirst[20] << 8)=" + (bytesFirst[20] << 8) +"(bytesFirst[20] << 8)="+(Utils.byte2Int(bytesFirst[20]) << 16));

            Logger.myLog(TAG,"锻炼序号：" + index + ",运动类型:" + sportType + ",平均心率:" + avgHr + "----绑定时间" + BaseManager.deviceBindTime);
            Logger.myLog(TAG,"锻炼序号：" + index + ",startYear:" + startyear + ",startmonth:" + startmonth + ",startday:" + startday + ",starthour:" + starthour + ",startMin:" + startMin + ",startSecd:" + startSecd);
            Logger.myLog(TAG,"锻炼序号：" + index + ",sumSportHour:" + sumSportHour + ",sumSportHour:" + sumSportHour + ",sumSportMin：" + sumSportMin);
            Logger.myLog(TAG,"锻炼序号：" + index + ",endyear:" + endyear + ",endMonth:" + endMonth + ",endDay:" + endDay + ",endHour:" + endHour + ",endMin:" + endMin + ",endSecd:" + endSecd);
            Logger.myLog(TAG,"锻炼序号：" + index + ",step:" + step + ",cal:" + cal + "" + "dis" + dis + ",speed:" + speed);
            //开始时间和结束时间用时间戳
            //运动时长用秒
            Calendar starCalendar = Calendar.getInstance();
            starCalendar.set(startyear, startmonth - 1, startday, starthour, startMin, startSecd);
            Long startTime = starCalendar.getTimeInMillis() / 1000 * 1000;


            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,2021);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            long times = calendar.getTimeInMillis();
            //判断下绑定的时间
            Logger.myLog(TAG,"-----sportType="+sportType);
            if (sportType == 255) {
                return;
            }
            Logger.myLog(TAG,"---startTime < times---="+(startTime < times));
            if (startTime < times) {
                bluetoothListener.onSyncSuccessPractiseData(-1);
                return;

            }
            Logger.myLog(TAG,"-----BaseManager.deviceBindTime - startTime="+(BaseManager.deviceBindTime - startTime));
//            if ((BaseManager.deviceBindTime - startTime > 0)) {
//                bluetoothListener.onSyncSuccessPractiseData(-1);
//                return;
//            }

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(endyear, endMonth - 1, endDay, endHour, endMin, endSecd);
            Long endTime = endCalendar.getTimeInMillis() / 1000 * 1000;
            int validTime = sumSportHour * 60 * 60 + sumSportMin * 60 + sumSportSecend;

            int currentDeviceType = getDeviceTypeId();

            if(currentDeviceType == 5601){
                //BaseManager.mUserId, baseDevice.deviceName, "0", TimeUtils.getTimeByyyyyMMdd(startTime), startTime, endTime, validTime, sportType, step, dis, cal, avgHr, strHr
                W81DeviceExerciseData w81De = new W81DeviceExerciseData();
                w81De.setUserId(BaseManager.mUserId);
                w81De.setDeviceId(baseDevice.deviceName);
                w81De.setWristbandSportDetailId("0");
                w81De.setDateStr(TimeUtils.getTimeByyyyyMMdd(startTime));
                w81De.setStartTimestamp(startTime);
                w81De.setEndTimestamp(endTime);
                w81De.setVaildTimeLength(validTime+"");
                w81De.setExerciseType(sportType+"");
                w81De.setTotalDistance(String.valueOf(dis));
                w81De.setTotalSteps(String.valueOf(step));
                w81De.setTotalCalories(String.valueOf(cal));

                parseW560PracticeData(m24HDATA,bluetoothListener,baseDevice,context,w81De,index);

                return;
            }


            //这里需要去判断这条数据是否已经取过，如果是已经取过的就不需要在继续往后面取。

            String strHr = "";
            if (avgHr != 0) {//心率不等于0
                byte[] hrdata = new byte[(m24HDATA.size() - 2) * 19];//减去第一包剩下的数据集合
                for (int i = 2; i < m24HDATA.size(); i++) {
                    byte[] hrtmp = m24HDATA.get(i);

                    if (hrtmp != null) {
                        System.arraycopy(hrtmp, 0, hrdata, (i - 2) * 19, 19);
                    }
                }


                int sum = 0;
                ArrayList<Integer> hrList = new ArrayList<>();
                for (int i = 0; i < hrdata.length; i++) {
                    int heart = Utils.byte2Int(hrdata[i]);
                    //Logger.myLog("heart："+heart+"");
                    if (heart == 255) {
                        continue;//结束标识符号
                    }
                    if (heart <= 30) {
                        continue;
                    }
                    sum += heart;
                    hrList.add(heart);
                }
                if (hrList.size() > 0) {
                    Gson gson = new Gson();
                    strHr = gson.toJson(hrList);
                    avgHr = sum / hrList.size();
                }

            }
            W81DeviceEexerciseAction action = new W81DeviceEexerciseAction();

            W81DeviceExerciseData hrData = action.findAndExerciseData(BaseManager.mUserId, baseDevice.deviceName, startTime, endTime);
            boolean isHave = false;
            if (hrData != null) {
                isHave = true;
                Logger.myLog(TAG,"锻炼序号2：" + index + isHave + " -----" + hrData.toString());

            } else {
                isHave = false;
                Logger.myLog(TAG,"锻炼序号2：" + index + isHave + " -----" + hrData);

            }

            if (isHave) {
                bluetoothListener.onSyncSuccessPractiseData(-1);
            } else {
                action.saveW526DeviceExerciseData(BaseManager.mUserId, baseDevice.deviceName, "0", TimeUtils.getTimeByyyyyMMdd(startTime), startTime, endTime, validTime, sportType, step, dis, cal, avgHr, strHr);
                bluetoothListener.onSyncSuccessPractiseData(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    static List<Integer> sourceList = new ArrayList<>();
    //所有心率的集合
    static List<Integer> allHeartList = new ArrayList<>();
    //所有计步、距离、卡路里的集合
    static List<Integer> allStepAdDistance = new ArrayList<>();

    static List<Integer> stepList = new ArrayList<>();
    static List<Integer> distanceList = new ArrayList<>();
    static List<Integer> caloriesList = new ArrayList<>();


    private static void parseW560PracticeData(List<byte[]> m24HDATA, BluetoothListener bluetoothListener,
                                              BaseDevice baseDevice, Context context,W81DeviceExerciseData w81DeviceExerciseData,int index){
        sourceList.clear();
        allHeartList.clear();
        allStepAdDistance.clear();
        stepList.clear();
        distanceList.clear();
        caloriesList.clear();

        Logger.myLog(TAG,"------添加数组="+new Gson().toJson(m24HDATA));

        //将去除第一位的数据全部存储到集合中
        for(int i = 2;i<m24HDATA.size();i++){
            byte[] itemByte = m24HDATA.get(i);
            for(int k = 0;k<itemByte.length-1;k++){
                sourceList.add(itemByte[k]&0xff);
            }
        }

        Logger.myLog(TAG,"---sourceList="+new Gson().toJson(sourceList));

        //0xFA第一次出现的位置
        int positionFA = sourceList.indexOf(250); //18
        //0xFF第一次出现的位置
        int positionFF = sourceList.indexOf(255);

        //截取有效的数据，去除无用数据
        List<Integer> resultList = sourceList.subList(0,positionFF);

        boolean isComplete = false;

        do {
            for(int k = 0;k<resultList.size();k++){
                int tempValue = resultList.get(k);
                if(k< positionFA ){     //心率
                    allHeartList.add(tempValue);
                }

                //距离和卡路里放一起
                if(k >positionFA + 1 && k < positionFA +2 + 6){
                    allStepAdDistance.add(tempValue);
                }

            }
            int tmpPosition =positionFA +7;

            isComplete =tmpPosition + 1 == resultList.size();

            if(!isComplete)
                resultList = resultList.subList(tmpPosition + 1,resultList.size());
            positionFA =resultList.indexOf(250);
        }while (!isComplete);


        Logger.myLog(TAG,"-----allStepAdDistance="+new Gson().toJson(allStepAdDistance));

        //解析步数、距离、卡路里数据
        for(int i = 0;i<allStepAdDistance.size();i+=6){
            //计步
            int stepV1 = allStepAdDistance.get(i);
            int stepV2 = allStepAdDistance.get(i+1);
            int itemStepValue = Utils.byteArrayToInt1(new byte[]{(byte) stepV1, (byte) stepV2});
            stepList.add(itemStepValue);
            //距离
            int distanceV1 = allStepAdDistance.get(i+2);
            int distanceV2 = allStepAdDistance.get(i+3);
            int itemDistanceValue = Utils.byteArrayToInt1(new byte[]{(byte) distanceV1, (byte) distanceV2});
            distanceList.add(itemDistanceValue);

            //卡路里
            int caloriesV1 = allStepAdDistance.get(i+4);
            int caloriesV2 = allStepAdDistance.get(i+5);
            int itemCaloriesValue = Utils.byteArrayToInt1(new byte[]{(byte) caloriesV1, (byte) caloriesV2});
            caloriesList.add(itemCaloriesValue);

        }

        Logger.myLog(TAG,"--------W560锻炼数据 心率="+new Gson().toJson(allHeartList)+"\n"+new Gson().toJson(stepList)+"\n"+new Gson().toJson(distanceList)+"\n"+new Gson().toJson(caloriesList));


        //不为0的心率总和
        int sumHeart = 0;
        //个数
        int countHeart = 0;

        //计算平均心率
        for(int i = 0;i<allHeartList.size();i++){
            int tmpHeart = allHeartList.get(i);
            if(tmpHeart != 0){
                countHeart++;
                sumHeart += tmpHeart;
            }
        }

        int avgHeart = sumHeart == 0 ? 0 :  sumHeart / countHeart;

        W81DeviceEexerciseAction action = new W81DeviceEexerciseAction();
        w81DeviceExerciseData.setHrArray(new Gson().toJson(allHeartList));
        w81DeviceExerciseData.setStepArray(new Gson().toJson(stepList));
        w81DeviceExerciseData.setDistanceArray(new Gson().toJson(distanceList));
        w81DeviceExerciseData.setCalorieArray(new Gson().toJson(caloriesList));
        w81DeviceExerciseData.setAvgHr(avgHeart == 0 ? "--":avgHeart+"");
        action.saveDefExerciseData(w81DeviceExerciseData);

        bluetoothListener.onSyncSuccessPractiseData(index);
    }




    public static void parseW52624HData(List<byte[]> m24HDATA, BluetoothListener bluetoothListener, BaseDevice baseDevice, Context context) {
        if (m24HDATA != null && m24HDATA.size() > 0) {
            byte[] bytesFirst = m24HDATA.get(0);
//            00 00 27 26 00 50 00 00 00 00 B4 F9 F3 F3 F3 F3 F1 00 00 锻炼数据
            if (Utils.byte2Int(bytesFirst[12]) == 0xF3 && Utils.byte2Int(bytesFirst[13]) == 0xF3 && Utils.byte2Int
                    (bytesFirst[14]) == 0xF3 && Utils.byte2Int
                    (bytesFirst[15]) == 0xF3) {
                //锻炼数据
                Logger.myLog("锻炼数据");
            } else {
                //24H数据
                Logger.myLog("24H数据");
//            weakself.timeStr = [NSString stringWithFormat:@"%04d-%02d-%02d", (val[5] ) | (val[6]<< 8), val[7],
// val[8]];
//            weakself.stepStr = [NSString stringWithFormat:@"%ld",(long)((val[9])|(val[10]<< 8)|(val[11]<< 16))];
//            weakself.calorieStr = [NSString stringWithFormat:@"%ld",(long)((val[12])|(val[13]<< 8)|(val[14]<< 16))];
//            weakself.distanceStr = [NSString stringWithFormat:@"%ld",(long)((val[15])|(val[16]<< 8)|(val[17]<< 16))];
//            weakself.sleepStr = [NSString stringWithFormat:@"%ld",(long)((val[18])|(val[19]<< 8))];
                int year = (Utils.byte2Int(bytesFirst[5]) << 8) + Utils.byte2Int(bytesFirst[4]);
                int non = Utils.byte2Int(bytesFirst[6]);
                int day = Utils.byte2Int(bytesFirst[7]);



                long step = (Utils.byte2Int(bytesFirst[10]) << 16) + (Utils.byte2Int(bytesFirst[9]) << 8) + Utils
                        .byte2Int
                                (bytesFirst[8]);
                long cal = (Utils.byte2Int(bytesFirst[13]) << 16) + (Utils.byte2Int(bytesFirst[12]) << 8) + Utils
                        .byte2Int(bytesFirst[11]);
                long dis = (Utils.byte2Int(bytesFirst[16]) << 16) + (Utils.byte2Int(bytesFirst[15]) << 8) + Utils
                        .byte2Int(bytesFirst[14]);
                long time = (Utils.byte2Int(bytesFirst[18]) << 8) + Utils.byte2Int(bytesFirst[17]);

                Logger.myLog("year = " + year + "non = " + non + "day = " + day + " step = " + step + " cal = " + cal +
                        " dis = " + dis + " time = " + time);
                byte[] data = new byte[(m24HDATA.size() - 1) * 19];//减去第一包剩下的数据集合
                for (int i = 1; i < m24HDATA.size(); i++) {
                    byte[] tmp = m24HDATA.get(i);
                    if (tmp != null) {
                        System.arraycopy(tmp, 0, data, (i - 1) * 19, 19);
                    }
                }

                String dateStr = year + "-" + formatTwoStr(non) + "-" + formatTwoStr(day);
                Watch_W516_24HDataModel watch_w516_24HDataModel = new Watch_W516_24HDataModel();
                watch_w516_24HDataModel.setUserId(BaseManager.mUserId);
                watch_w516_24HDataModel.setDeviceId(baseDevice.deviceName);
                watch_w516_24HDataModel.setDateStr(dateStr);
                watch_w516_24HDataModel.setTimestamp(System.currentTimeMillis());
                watch_w516_24HDataModel.setTotalSteps(step);
                watch_w516_24HDataModel.setTotalDistance(dis);
                watch_w516_24HDataModel.setTotalCalories(cal);

                //            第 2 包到第 n 包是每分钟记录数据包，每个数据包括 2 个字节，定义如下
//            BYTE0 - 该时间段步数，若>=250 则为睡眠，睡眠分为 4 个等级，250 到 253 分别对深睡和浅睡 level 2，浅睡 level 1
//            和清醒。
//            BYTE1 - 该时间段平均心率，若为 1，表示没有开启心率功能，若为大于 1，小于 30，表示心率开启了，但是手表
//                    不在手腕上
//            Page 14 of 14
//            一个完整日历天总共有 1440 组记录数据，总数据长度为 1440 * 2 + 20 = 2900。
//            如果最后一个数据不在最后一包的末尾，后面的数据填入 0xFF
//            设备最多可以记录 7 天数据
                //每两byte为一组数据,总共1440数据,每分钟一条数据,24小时数据

                boolean haslPak = false;
                //判断是否是当天数据
                Calendar instance = Calendar.getInstance();
                if (year == instance.get(Calendar.YEAR) && non == instance.get
                        (Calendar.MONTH) + 1 && day == instance.get(Calendar.DAY_OF_MONTH)) {
                    //当天数据
                    //通过获取当前时间来判断包数，12:32  (12*60+32)/19   39.57  40包数据
                    int hourOfDay = instance.get(Calendar.HOUR_OF_DAY);//当前的小时
                    int minuteOfDay = instance.get(Calendar.MINUTE);//当前分钟
                    float floatF = (hourOfDay * 60 + minuteOfDay) / (float) 19;
                    int intI = (hourOfDay * 60 + minuteOfDay) / 19;
                    Log.e(TAG,"-----睡眠日期="+year+"-"+non+"-"+day+" "+hourOfDay+":"+minuteOfDay+" "+floatF +" "+intI);


                    int pakNum;
                    if ((floatF - intI) > 0) {
                        pakNum = intI + 1;
                    } else {
                        pakNum = intI;
                    }
                    if (data.length >= pakNum * 19) {
//                        Logger.myLog("pakNum == " + pakNum * 19 + "当天数据未丢包 data.length == " + data.length);
                    } else {
                        haslPak = true;
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncError();
                        }
//                        Logger.myLog("pakNum == " + pakNum * 19 + "当天数据丢包 data.length == " + data.length);
                    }
                } else {
                    //历史数据
                    //判断是否丢包,应该有152包数据  1440*2/19  151.57 除去了index位
                    if (data.length >= 2880) {
                        //没有丢包
                        Logger.myLog("历史数据未丢包 == " + data.length);

                    } else {
                        //丢包
                        haslPak = true;
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncError();
                        }
//                        Logger.myLog("历史数据丢包 == " + data.length);
                    }
                }
                Logger.myLog("步数数据 ==  haslPak" + haslPak);
                if (!haslPak) {
                    List<Integer> stepList = new ArrayList<>();
                    List<Integer> sleepList = new ArrayList<>();
                    List<Integer> heartRateList = new ArrayList<>();
                    watch_w516_24HDataModel.setHasHR(WatchData.NO_HR);
                    watch_w516_24HDataModel.setHasSleep(WatchData.NO_SLEEP);
                    int sum = 0;
                    int size = 0;
                    for (int i = 0; i < data.length; i += 2) {
                        byte byte0 = data[i];
                        //当天可能出现1197的情况
                        byte byte1;
                        if (i + 1 >= data.length) {
                            byte1 = 1;
                        } else {
                            byte1 = data[i + 1];
                        }
                        int int0 = Utils.byteToInt(byte0);
                        int int1 = Utils.byteToInt(byte1);
                        if (int0 == 255 || int0 == 254) {
                            int0 = 0;
                            //结尾包了
//                            Logger.myLog("结尾包了 == " + i);
                            //break;
                        }
                        /**
                         * 计步或者睡眠
                         */
                        Logger.myLog("步数数据 == " + int0);
                        if (int0 >= 250 && int0 <= 253) {
                            watch_w516_24HDataModel.setHasSleep(WatchData.HAS_SLEEP);
                            //为睡眠数据
                            if (int0 == 250) {
                                //深睡
//                                Logger.myLog("深睡");
                            } else if (int0 == 251) {
                                //浅睡 level 2
//                                Logger.myLog("浅睡 level 2");

                            } else if (int0 == 252) {
                                //浅睡 level 1
//                                Logger.myLog("浅睡 level 1");

                            } else if (int0 == 253) {
                                //清醒
//                                Logger.myLog("清醒");

                            }
                            stepList.add(0);
                            sleepList.add(int0);
                        } else {
                            //步数数据
//                            Logger.myLog("步数数据 == " + int0);
                            if (stepList.size() < 1440) {
                                stepList.add(int0);
                                sleepList.add(0);
                            }

                        }
                        /**
                         * 心率数据
                         */
                        if (int1 == 255) {
                            int1 = 0;
                        }
                        if (int1 == 1) {
                            //没有开启心率
//                            Logger.myLog("没有开启心率");
                            sum += 0;
                        } else if (int1 > 1 && int1 < 30) {
                            //开启心率了，但不在手腕上
//                            Logger.myLog("开启心率了，但不在手腕上");
                            sum += 0;
                        } else {
                            //心率数据
                            //Logger.myLog("心率数据 == " + int1);
                            if (int1 >= 30) {
                                size++;
                                sum += int1;
                            }
                        }
                        if (heartRateList.size() < 1440) {
                            heartRateList.add(int1);
                        }

                    }
                    if (sum == 0 || size == 0) {
                        watch_w516_24HDataModel.setAvgHR(0);
                    } else {
                        watch_w516_24HDataModel.setAvgHR(Math.round(sum / (float) size));
                    }
//                    Logger.myLog("dateStr == " + (year + "-" + formatTwoStr(non) + "-" + formatTwoStr(day)) + "heartRateList == " + heartRateList.toString() + " heartRateList.size == " + heartRateList.size() + " AVG == " + Math.round(sum / (float) size));
                    Gson gson = new Gson();
                    watch_w516_24HDataModel.setStepArray(gson.toJson(stepList));
                    watch_w516_24HDataModel.setReportId("0");
                    watch_w516_24HDataModel.setSleepArray(gson.toJson(sleepList));
                    if (heartRateList.size() > 0) {
                        if (Collections.max(heartRateList) >= 30) {
                            watch_w516_24HDataModel.setHasHR(WatchData.HAS_HR);
                        }
                    }
                    watch_w516_24HDataModel.setHrArray(gson.toJson(heartRateList));
                    BleSPUtils.putString(context, BleSPUtils.WATCH_LAST_SYNCTIME, dateStr);


                    Logger.myLog("parsew52624HData stepList" + stepList.size() + "sleepList:" + sleepList.size() + "heartRateList:" + heartRateList.size());

                    saveOrUpdateWatch_w516_24HDataModel(watch_w516_24HDataModel, baseDevice.deviceName, bluetoothListener);
                } else {
                    //丢包了
                    if (bluetoothListener != null) {
                        bluetoothListener.onSyncError();
                    }
                }
            }
            //                02-20 19:25:21.805 5660-5775/com.isport.isportblesdk E/MyLog:  ReceiverCmd
            // 00 26 27 00
            // 00 E3 07 02
// 13 00 A3 02 00 00 00 00 00 00 00 00  UUID 7658fd03-878a-4350-a93e-da553e719ed0
//                02-20 19:25:21.810 5660-5775/com.isport.isportblesdk E/MyLog:  ReceiverCmd 01 FA 3D FA 3E FA 3F FA
// 40 FA 41 FA 42 FA 43 FA 44 FA 45 FA  UUID 7658fd03-878a-4350-a93e-da553e719ed0
        }
    }


    public static void parse24HData(List<byte[]> m24HDATA, BluetoothListener bluetoothListener, BaseDevice baseDevice, Context context) {
        if (m24HDATA != null && m24HDATA.size() > 0) {
            byte[] bytesFirst = m24HDATA.get(0);
//            00 00 27 26 00 50 00 00 00 00 B4 F9 F3 F3 F3 F3 F1 00 00 锻炼数据
            if (Utils.byte2Int(bytesFirst[12]) == 0xF3 && Utils.byte2Int(bytesFirst[13]) == 0xF3 && Utils.byte2Int
                    (bytesFirst[14]) == 0xF3 && Utils.byte2Int
                    (bytesFirst[15]) == 0xF3) {
                //锻炼数据
                Logger.myLog("锻炼数据");
            } else {
                //24H数据
                Logger.myLog("24H数据");
//            weakself.timeStr = [NSString stringWithFormat:@"%04d-%02d-%02d", (val[5] ) | (val[6]<< 8), val[7],
// val[8]];
//            weakself.stepStr = [NSString stringWithFormat:@"%ld",(long)((val[9])|(val[10]<< 8)|(val[11]<< 16))];
//            weakself.calorieStr = [NSString stringWithFormat:@"%ld",(long)((val[12])|(val[13]<< 8)|(val[14]<< 16))];
//            weakself.distanceStr = [NSString stringWithFormat:@"%ld",(long)((val[15])|(val[16]<< 8)|(val[17]<< 16))];
//            weakself.sleepStr = [NSString stringWithFormat:@"%ld",(long)((val[18])|(val[19]<< 8))];
                int year = (Utils.byte2Int(bytesFirst[5]) << 8) + Utils.byte2Int(bytesFirst[4]);
                int non = Utils.byte2Int(bytesFirst[6]);
                int day = Utils.byte2Int(bytesFirst[7]);
                long step = (Utils.byte2Int(bytesFirst[10]) << 16) + (Utils.byte2Int(bytesFirst[9]) << 8) + Utils
                        .byte2Int
                                (bytesFirst[8]);
                long cal = (Utils.byte2Int(bytesFirst[13]) << 16) + (Utils.byte2Int(bytesFirst[12]) << 8) + Utils
                        .byte2Int(bytesFirst[11]);
                long dis = (Utils.byte2Int(bytesFirst[16]) << 16) + (Utils.byte2Int(bytesFirst[15]) << 8) + Utils
                        .byte2Int(bytesFirst[14]);
                long time = (Utils.byte2Int(bytesFirst[18]) << 8) + Utils.byte2Int(bytesFirst[17]);

//                Logger.myLog("year = " + year + "non = " + non + "day = " + day + " step = " + step + " cal = " + cal +
//                        " dis = " + dis + " time = " + time);
                byte[] data = new byte[(m24HDATA.size() - 1) * 19];//减去第一包剩下的数据集合
                for (int i = 1; i < m24HDATA.size(); i++) {
                    byte[] tmp = m24HDATA.get(i);
                    if (tmp != null) {
                        System.arraycopy(tmp, 0, data, (i - 1) * 19, 19);
                    }
                }

                String dateStr = year + "-" + formatTwoStr(non) + "-" + formatTwoStr(day);
                Watch_W516_24HDataModel watch_w516_24HDataModel = new Watch_W516_24HDataModel();
                watch_w516_24HDataModel.setUserId(BaseManager.mUserId);
                watch_w516_24HDataModel.setDeviceId(baseDevice.deviceName);
                watch_w516_24HDataModel.setDateStr(dateStr);
                watch_w516_24HDataModel.setTimestamp(System.currentTimeMillis());
                watch_w516_24HDataModel.setTotalSteps(step);
                watch_w516_24HDataModel.setTotalDistance(dis);
                watch_w516_24HDataModel.setTotalCalories(cal);

                //            第 2 包到第 n 包是每分钟记录数据包，每个数据包括 2 个字节，定义如下
//            BYTE0 - 该时间段步数，若>=250 则为睡眠，睡眠分为 4 个等级，250 到 253 分别对深睡和浅睡 level 2，浅睡 level 1
//            和清醒。
//            BYTE1 - 该时间段平均心率，若为 1，表示没有开启心率功能，若为大于 1，小于 30，表示心率开启了，但是手表
//                    不在手腕上
//            Page 14 of 14
//            一个完整日历天总共有 1440 组记录数据，总数据长度为 1440 * 2 + 20 = 2900。
//            如果最后一个数据不在最后一包的末尾，后面的数据填入 0xFF
//            设备最多可以记录 7 天数据
                //每两byte为一组数据,总共1440数据,每分钟一条数据,24小时数据

                boolean haslPak = false;
                //判断是否是当天数据
                Calendar instance = Calendar.getInstance();
                if (year == instance.get(Calendar.YEAR) && non == instance.get
                        (Calendar.MONTH) + 1 && day == instance.get(Calendar.DAY_OF_MONTH)) {
                    //当天数据
                    //通过获取当前时间来判断包数，12:32  (12*60+32)/19   39.57  40包数据
                    int hourOfDay = instance.get(Calendar.HOUR_OF_DAY);//当前的小时
                    int minuteOfDay = instance.get(Calendar.MINUTE);//当前分钟
                    float floatF = (hourOfDay * 60 + minuteOfDay) / (float) 19;
                    int intI = (hourOfDay * 60 + minuteOfDay) / 19;


                    int pakNum;
                    if ((floatF - intI) > 0) {
                        pakNum = intI + 1;
                    } else {
                        pakNum = intI;
                    }
                    if (data.length >= pakNum * 19) {
//                        Logger.myLog("pakNum == " + pakNum * 19 + "当天数据未丢包 data.length == " + data.length);
                    } else {
                        haslPak = true;
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncError();
                        }
//                        Logger.myLog("pakNum == " + pakNum * 19 + "当天数据丢包 data.length == " + data.length);
                    }
                } else {
                    //历史数据
                    //判断是否丢包,应该有152包数据  1440*2/19  151.57 除去了index位
                    if (data.length >= 2880) {
                        //没有丢包
//                        Logger.myLog("历史数据未丢包 == " + data.length);

                    } else {
                        //丢包
                        haslPak = true;
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncError();
                        }
//                        Logger.myLog("历史数据丢包 == " + data.length);
                    }
                }

                if (!haslPak) {
                    List<Integer> stepList = new ArrayList<>();
                    List<Integer> sleepList = new ArrayList<>();
                    List<Integer> heartRateList = new ArrayList<>();
                    watch_w516_24HDataModel.setHasHR(WatchData.NO_HR);
                    watch_w516_24HDataModel.setHasSleep(WatchData.NO_SLEEP);
                    int sum = 0;
                    int size = 0;
                    for (int i = 0; i < data.length; i += 2) {
                        byte byte0 = data[i];
                        //当天可能出现1197的情况
                        byte byte1;
                        if (i + 1 >= data.length) {
                            byte1 = 1;
                        } else {
                            byte1 = data[i + 1];
                        }
                        int int0 = Utils.byteToInt(byte0);
                        int int1 = Utils.byteToInt(byte1);
                        if (int0 == 255) {
                            //int0 = 0;

                            //结尾包了
//                            Logger.myLog("结尾包了 == " + i);
                            break;
                        }
                        /**
                         * 计步或者睡眠
                         */
                        Logger.myLog("步数数据 == " + int0);
                        if (int0 >= 250) {
                            watch_w516_24HDataModel.setHasSleep(WatchData.HAS_SLEEP);
                            //为睡眠数据
                            if (int0 == 250) {
                                //深睡
//                                Logger.myLog("深睡");
                            } else if (int0 == 251) {
                                //浅睡 level 2
//                                Logger.myLog("浅睡 level 2");

                            } else if (int0 == 252) {
                                //浅睡 level 1
//                                Logger.myLog("浅睡 level 1");

                            } else if (int0 == 253) {
                                //清醒
//                                Logger.myLog("清醒");

                            }
                            stepList.add(0);
                            sleepList.add(int0);
                        } else {
                            //步数数据
//                            Logger.myLog("步数数据 == " + int0);
                            stepList.add(int0);
                            sleepList.add(0);
                        }
                        /**
                         * 心率数据
                         */
                        if (int1 == 1) {
                            //没有开启心率
//                            Logger.myLog("没有开启心率");
                            sum += 0;
                        } else if (int1 > 1 && int1 < 30) {
                            //开启心率了，但不在手腕上
//                            Logger.myLog("开启心率了，但不在手腕上");
                            sum += 0;
                        } else {
                            //心率数据
                            //Logger.myLog("心率数据 == " + int1);
                            if (int1 >= 30) {
                                size++;
                                sum += int1;
                            }
                        }
                        heartRateList.add(int1);
                    }
                    if (sum == 0 || size == 0) {
                        watch_w516_24HDataModel.setAvgHR(0);
                    } else {
                        watch_w516_24HDataModel.setAvgHR(Math.round(sum / (float) size));
                    }
//                    Logger.myLog("dateStr == " + (year + "-" + formatTwoStr(non) + "-" + formatTwoStr(day)) + "heartRateList == " + heartRateList.toString() + " heartRateList.size == " + heartRateList.size() + " AVG == " + Math.round(sum / (float) size));
                    Gson gson = new Gson();
                    watch_w516_24HDataModel.setStepArray(gson.toJson(stepList));
                    watch_w516_24HDataModel.setReportId("0");
                    watch_w516_24HDataModel.setSleepArray(gson.toJson(sleepList));
                    if (heartRateList.size() > 0) {
                        if (Collections.max(heartRateList) >= 30) {
                            watch_w516_24HDataModel.setHasHR(WatchData.HAS_HR);
                        }
                    }
                    watch_w516_24HDataModel.setHrArray(gson.toJson(heartRateList));
                    BleSPUtils.putString(context, BleSPUtils.WATCH_LAST_SYNCTIME, dateStr);

                    Logger.myLog("parse24HData" + watch_w516_24HDataModel.toString());

                    saveOrUpdateWatch_w516_24HDataModel(watch_w516_24HDataModel, baseDevice.deviceName, bluetoothListener);
                } else {
                    //丢包了
                    if (bluetoothListener != null) {
                        bluetoothListener.onSyncError();
                    }
                }
            }
            //                02-20 19:25:21.805 5660-5775/com.isport.isportblesdk E/MyLog:  ReceiverCmd
            // 00 26 27 00
            // 00 E3 07 02
// 13 00 A3 02 00 00 00 00 00 00 00 00  UUID 7658fd03-878a-4350-a93e-da553e719ed0
//                02-20 19:25:21.810 5660-5775/com.isport.isportblesdk E/MyLog:  ReceiverCmd 01 FA 3D FA 3E FA 3F FA
// 40 FA 41 FA 42 FA 43 FA 44 FA 45 FA  UUID 7658fd03-878a-4350-a93e-da553e719ed0
        }
    }

    public static String formatTwoStr(int number) {
        String strNumber = String.format("%02d", number);
        return strNumber;
    }

    /**
     * 存储W516睡眠设置
     */
    public static void saveOrUpdateWatch_w516_24HDataModel(final Watch_W516_24HDataModel watch_w516_24HDataModel, final String deviceId, final BluetoothListener bluetoothListener) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getWatch_W516_24HDataModelDao();
                Watch_W516_24HDataModel watch_w516_watch_w516_24HDataModelByDeviceId = Watch_W516_24HDataModelAction
                        .findWatch_W516_Watch_W516_24HDataModelByDeviceId(watch_w516_24HDataModel
                                .getUserId(), watch_w516_24HDataModel.getDateStr(), deviceId);
                if (watch_w516_watch_w516_24HDataModelByDeviceId != null) {
                    watch_w516_watch_w516_24HDataModelByDeviceId.setDeviceId(deviceId);
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTimestamp(System.currentTimeMillis());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setReportId("0");
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalSteps(watch_w516_24HDataModel.getTotalSteps());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalDistance(watch_w516_24HDataModel.getTotalDistance());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalCalories(watch_w516_24HDataModel.getTotalCalories());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setStepArray(watch_w516_24HDataModel.getStepArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setSleepArray(watch_w516_24HDataModel.getSleepArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHrArray(watch_w516_24HDataModel.getHrArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setAvgHR(watch_w516_24HDataModel.getAvgHR());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHasSleep(watch_w516_24HDataModel.getHasSleep());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHasHR(watch_w516_24HDataModel.getHasHR());
                    watch_w516_24HDataModelDao.insertOrReplace(watch_w516_watch_w516_24HDataModelByDeviceId);
                } else {
                    watch_w516_24HDataModel.setReportId("0");
                    watch_w516_24HDataModelDao.insertOrReplace(watch_w516_24HDataModel);
                }

                Logger.myLog(TAG,"---------保存睡眠相关数据="+new Gson().toJson(watch_w516_24HDataModel));

                if (bluetoothListener != null) {
                    bluetoothListener.onSyncSuccess();
                }
            }
        });
    }

    //存储设备的闹钟
    public static void saveAlarm(final byte[] values, final BaseDevice baseDevice) {

        if (Utils.byte2Int(values[3]) == 0) {
            return;
        } else {
            byte[] booleanArrayG = Utils.getBooleanArray(values[3]);
            int repaet = Utils.byte2Int(values[3]);
            byte[] msg = new byte[14];
            msg[0] = values[6];
            msg[1] = values[7];
            msg[2] = values[8];
            msg[3] = values[9];
            msg[4] = values[10];
            msg[5] = values[11];
            msg[6] = values[12];
            msg[7] = values[13];
            msg[8] = values[14];
            msg[9] = values[15];
            msg[10] = values[16];
            msg[11] = values[17];
            msg[12] = values[18];
            msg[13] = values[19];
            Logger.myLog("获取闹钟设置,二进制各个bit值 == " + " Sunday == " + (Utils.byte2Int(booleanArrayG[7]) == 0 ?
                    "关" :
                    "开")
                    + " Monday  == " + (Utils.byte2Int(booleanArrayG[6]) == 0 ? "关" : "开")
                    + " Tuesday  == " + (Utils.byte2Int(booleanArrayG[5]) == 0 ? "关" : "开")
                    + " Wednesday  == " + (Utils.byte2Int(booleanArrayG[4]) == 0 ? "关" :
                    "开")
                    + " Thursday  == " + (Utils.byte2Int(booleanArrayG[3]) == 0 ? "关" :
                    "开")
                    + " Friday   == " + (Utils.byte2Int(booleanArrayG[2]) == 0 ? "关" : "开")
                    + " Saturday   == " + (Utils.byte2Int(booleanArrayG[1]) == 0 ? "关" :
                    "开")
                    + " 时 " + Utils.byte2Int(values[4]) + " 分 " + Utils.byte2Int
                    (values[5]) + " 闹钟信息 " + new String(msg));

            Watch_W516_AlarmModel
                    watch_w516_alarmModel = new Watch_W516_AlarmModel();
            watch_w516_alarmModel.setDeviceId(baseDevice.getDeviceName());
            watch_w516_alarmModel.setUserId(BaseManager.mUserId);
            watch_w516_alarmModel.setRepeatCount(repaet);
            String time = CommonDateUtil.formatTwoStr(Utils.byte2Int(values[4])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(values[5]));
            watch_w516_alarmModel.setTimeString(time);
            watch_w516_alarmModel.setMessageString("123");

            saveOrUpdateWatchW516Alarm(watch_w516_alarmModel);
        }
    }

    // 存储W560设备的闹钟
    public static void saveW560Alarm(final byte[] values, final BaseDevice baseDevice) {
        Watch_W560_AlarmModel watch_w560_alarmModel = new Watch_W560_AlarmModel();

        watch_w560_alarmModel.setDeviceId(baseDevice.getDeviceName());
        watch_w560_alarmModel.setUserId(BaseManager.mUserId);
        int index = Utils.byte2Int(values[2]) - 0x50;
        watch_w560_alarmModel.setIndex(index);
        boolean isEnable = (Utils.byte2Int(values[3]) >> 7) == 0 ? false : true;
        watch_w560_alarmModel.setIsEnable(isEnable);
        String time = CommonDateUtil.formatTwoStr(Utils.byte2Int(values[3]) & 127) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(values[4]));
        watch_w560_alarmModel.setTimeString(time);
        watch_w560_alarmModel.setRepeatCount(Utils.byte2Int(values[5]));
        byte[] names = new byte[14];
        System.arraycopy(values, 6, names, 0, 14);
        String name = new String(names);
        watch_w560_alarmModel.setName(name);

        saveOrUpdateWatchW560Alarm(watch_w560_alarmModel);
    }

    //存储设备的闹钟
    public static void saveW526Alarm(final byte[] values, final BaseDevice baseDevice) {

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {

                try {
                    int index = Utils.byte2Int(values[3]);
                    byte[] booleanArrayG = Utils.getBooleanArray(values[4]);
                    boolean isEnable = Utils.byte2Int(values[4]) != 0;
                    int repaet = Utils.byte2Int(values[5]);
                    Bracelet_W311_AlarmModel alarmModel = new Bracelet_W311_AlarmModel();
                    alarmModel.setDeviceId(baseDevice.getDeviceName());
                    alarmModel.setUserId(BaseManager.mUserId);
                    alarmModel.setRepeatCount(repaet);
                    alarmModel.setIsOpen(isEnable);
              /*  if (repaet == 0) {
                    alarmModel.setIsOpen(false);
                } else {
                    alarmModel.setIsOpen(true);
                }*/
                    //保存闹钟的序号
                    alarmModel.setAlarmId(index);
                    String time = CommonDateUtil.formatTwoStr(Utils.byte2Int(values[6])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(values[7]));
                    alarmModel.setTimeString(time);
                    alarmModel.setMessageString("123");
                    Bracelet_W311_AlarmModelAction.saveW526AlarmBean(alarmModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 存储表盘模式
     */
    public static void saveWatchFace(final byte[] data, final BaseDevice baseDevice) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                int faceMode = data[3];
                Bracelet_W311_SettingModelAction.saveOrUpdateWatchFaces(baseDevice.deviceName, String.valueOf(BaseManager.mUserId), faceMode);

            }
        });

    }

    /**
     * 存储背光设置
     */
    public static void saveBackLight(final byte[] data, final BaseDevice baseDevice) {

        try {
// 02 36 03 0A
//    03 为背光亮度等级     设置范围 [01, 05]
//    0A 为背光时间，单位秒 设置范围 [03, 0A]


            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    int backLightTime = 3;
                    int screenLeve = 1;
                    backLightTime = data[4];
                    screenLeve = data[3];

                    DeviceSettingAction action = new DeviceSettingAction();
                    action.saveOrUpdateBacklightTimeAndScreenLeve(baseDevice.getDeviceName(), BaseManager.mUserId, backLightTime, screenLeve);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    //保存久坐提醒
    public static void saveSedentaryTime(final byte[] data, final BaseDevice baseDevice) {
        //
        try {
            Watch_W516_SedentaryModel watch_w516_sedentaryModel = new Watch_W516_SedentaryModel();
            watch_w516_sedentaryModel.setUserId(BaseManager.mUserId);
            watch_w516_sedentaryModel.setDeviceId(baseDevice.getDeviceName());
            int timeLong = Utils.byte2Int(data[3]);
            String startTime;
            String endTime;
            startTime = CommonDateUtil.formatTwoStr(Utils.byte2Int(data[4])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(data[5]));
            endTime = CommonDateUtil.formatTwoStr(Utils.byte2Int(data[6])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(data[7]));
            Logger.myLog("saveSedentary_time:startTime:" + startTime + ",endTime:" + endTime + "timeLong" + timeLong);
            if (timeLong < 5) {
                startTime = "9:00";
                endTime = "17:00";
                watch_w516_sedentaryModel.setIsEnable(false);
            } else {
                watch_w516_sedentaryModel.setIsEnable(true);
            }

            watch_w516_sedentaryModel.setLongSitEndTime(endTime);
            watch_w516_sedentaryModel.setLongSitStartTime(startTime);
            watch_w516_sedentaryModel.setLongSitTimeLong(timeLong);

            saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModel);
        } catch (Exception e) {
            Logger.myLog(e.toString());
        }

    }

    public static void saveTempUtil(String devcieName, String userId, String tempUnitl) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                BaseManager.isTmepUnitl = tempUnitl;
                DeviceTempUnitlTableAction action = new DeviceTempUnitlTableAction();
                action.saveTempUnitlModel(devcieName, userId, tempUnitl);
            }
        });
    }

    public static void save24HrSwitch(String deviceName, int state) {

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_SettingModel w516SettingModelByDeviceId = Watch_W516_SettingModelAction.findW516SettingModelByDeviceId(deviceName, BaseManager.mUserId);
                if (w516SettingModelByDeviceId != null) {
                    w516SettingModelByDeviceId.setHeartRateSwitch(state == 1 ? true : false);
                    BleAction.getWatch_W516_SettingModelDao().insertOrReplace(w516SettingModelByDeviceId);
                }
            }
        });

    }

    //保存通用设置
    public static void saveGeneral(final byte[] data, final BaseDevice baseDevice) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                byte[] booleanArrayG = Utils.getBooleanArray(data[3]);
               /* Logger.myLog("获取通用设置,二进制各个bit值 == " + " 公英制 == " + (Utils.byte2Int(booleanArrayG[7]) == 0 ? "公制" :
                        "英制")
                        + " 中英文 == " + (Utils.byte2Int(booleanArrayG[6]) == 0 ? "英文" : "中文")
                        + " 小时制 == " + (Utils.byte2Int(booleanArrayG[5]) == 0 ? "12小时" : "24小时")
                        + " 抬腕亮屏 == " + (Utils.byte2Int(booleanArrayG[4]) == 0 ? "开启" : "关闭")
                        + " 24小时心率 == " + (Utils.byte2Int(booleanArrayG[3]) == 0 ? "开启" : "关闭"));*/


                boolean timeFormat = (Utils.byte2Int(booleanArrayG[5]) == 0 ? true : false);
                boolean brightScreen = (Utils.byte2Int(booleanArrayG[4]) == 0 ? true : false);
                boolean language = (Utils.byte2Int(booleanArrayG[6]) == 0 ? true : false);
                boolean unit = (Utils.byte2Int(booleanArrayG[7]) == 0 ? true : false);
                boolean is24Heart = (Utils.byte2Int(booleanArrayG[3]) == 0 ? true : false);
                byte[] booleanArrayMessag = Utils.getBooleanArray(data[4]);
                Logger.myLog("获取通用设置,二进制各个bit值 == " + " ANCS 来电通知 == " + (Utils.byte2Int(booleanArrayMessag[booleanArrayMessag.length - 1]) == 0 ? "关闭 ANCS 来电通知 " :
                        "打开 ANCS 来电通知 ") + "信息通知:" + (Utils.byte2Int(booleanArrayMessag[booleanArrayMessag.length - 2]) == 0 ? "关闭 ANCS 信息通知" : "打开 ANCS 信息通知"));
                //1.保存是否开启24小时心率
                //2.保存来电消息的设置

                boolean isCall = (Utils.byte2Int(booleanArrayMessag[booleanArrayMessag.length - 1]) == 0 ? false : true);
                boolean isMessge = (Utils.byte2Int(booleanArrayMessag[booleanArrayMessag.length - 2]) == 0 ? false : true);

                Watch_W516_SettingModel w516SettingModelByDeviceId = Watch_W516_SettingModelAction.findW516SettingModelByDeviceId(baseDevice.deviceName, BaseManager.mUserId);

                if (w516SettingModelByDeviceId != null) {
                    // ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_GENERAL, w516SettingModelByDeviceId.getHeartRateSwitch());
                    w516SettingModelByDeviceId.setHeartRateSwitch(is24Heart);
                    w516SettingModelByDeviceId.setTimeFormat(timeFormat);
                    w516SettingModelByDeviceId.setBrightScreen(brightScreen);
                    w516SettingModelByDeviceId.setLanguage(language);
                    w516SettingModelByDeviceId.setUnit(unit);
                    BleAction.getWatch_W516_SettingModelDao().insertOrReplace(w516SettingModelByDeviceId);
                } else {
                    //默认关闭

                    Watch_W516_SettingModel watch_w516_settingModel = new Watch_W516_SettingModel();
                    watch_w516_settingModel.setDeviceId(baseDevice.deviceName);
                    watch_w516_settingModel.setUserId(BaseManager.mUserId);
                    watch_w516_settingModel.setHeartRateSwitch(is24Heart);
                    watch_w516_settingModel.setTimeFormat(timeFormat);
                    watch_w516_settingModel.setBrightScreen(brightScreen);
                    watch_w516_settingModel.setLanguage(language);
                    watch_w516_settingModel.setUnit(unit);
                    BleAction.getWatch_W516_SettingModelDao().insertOrReplace(watch_w516_settingModel);
                    // ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_GENERAL, false);
                }
                saveCallMessage(baseDevice, isCall, isMessge);

            }
        });

    }


    public static void saveCallMessage(final BaseDevice baseDevice, final boolean isCall, final boolean isMessge) {
        //保存来电的信息
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Watch_W516_NotifyModel watch_w516_notifyModelByDeviceId = Watch_W516_NotifyModelAction.findWatch_W516_NotifyModelByDeviceId(baseDevice.deviceName, BaseManager.mUserId);
                if (watch_w516_notifyModelByDeviceId != null) {
                    watch_w516_notifyModelByDeviceId.setCallSwitch(isCall);
                    watch_w516_notifyModelByDeviceId.setMsgSwitch(isMessge);
                    BleAction.getWatch_W516_NotifyModelDao().insertOrReplace(watch_w516_notifyModelByDeviceId);
                } else {
                    watch_w516_notifyModelByDeviceId = new Watch_W516_NotifyModel();
                    watch_w516_notifyModelByDeviceId.setMsgSwitch(isMessge);
                    watch_w516_notifyModelByDeviceId.setCallSwitch(isCall);
                    watch_w516_notifyModelByDeviceId.setDeviceId(baseDevice.deviceName);
                    watch_w516_notifyModelByDeviceId.setUserId(BaseManager.mUserId);
                    BleAction.getWatch_W516_NotifyModelDao().insertOrReplace(watch_w516_notifyModelByDeviceId);
                }
            }
        });

    }


    public static byte[] getTempByte(byte[] values) {
        byte[] tempByte = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            tempByte[i] = values[i];
        }
        return tempByte;
    }


    public static void saveW526RaiseHand(final byte[] values, final BaseDevice baseDevice) {
        Logger.myLog("获取抬腕亮屏设置设置成功 == 开关" + Utils.byte2Int(values[3]) + "开始时间hour:" + Utils.byte2Int(values[4]) + "开始时间min:" + Utils.byte2Int(values[5]) + "结束时间hour:" + Utils.byte2Int(values[6]) + "结束时间min:" + Utils.byte2Int(values[7]));

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_LiftWristToViewInfoModel model = new Bracelet_W311_LiftWristToViewInfoModel();
                model.setDeviceId(baseDevice.getDeviceName());
                model.setUserId(BaseManager.mUserId);
                if (values[3] == 0) {
                    model.setSwichType(2);
                    model.setStartHour(7);
                    model.setStartMin(0);
                    model.setEndHour(22);
                    model.setEndMin(0);
                } else {
                    int starHour = Utils.byte2Int(values[4]);
                    int starMin = Utils.byte2Int(values[5]);
                    int endHour = Utils.byte2Int(values[6]);
                    int endMin = Utils.byte2Int(values[7]);
                    if (starHour == 0 && starMin == 0 && endHour == 23 && endMin == 59) {
                        model.setSwichType(0);
                        model.setStartHour(7);
                        model.setStartMin(0);
                        model.setEndHour(22);
                        model.setEndMin(0);
                    } else {
                        model.setSwichType(1);
                        model.setStartHour(starHour);
                        model.setStartMin(starMin);
                        model.setEndHour(endHour);
                        model.setEndMin(endMin);
                    }
                }

                Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);
            }
        });


    }


    public static void saveRaiseHandW307j(final byte[] data, final BaseDevice baseDevice) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_LiftWristToViewInfoModel model = new Bracelet_W311_LiftWristToViewInfoModel();
                model.setDeviceId(baseDevice.getDeviceName());
                model.setUserId(BaseManager.mUserId);
                int enable = data[4];
                int sleepEnable = data[5];
                /**
                 * 关闭是2
                 *          全天开启0
                 *          睡觉时间段关
                 */
                if (enable == 1 && sleepEnable == 1) {
                    model.setSwichType(0);
                } else if (enable == 0) {
                    model.setSwichType(2);
                } else {
                    model.setSwichType(1);
                }

                Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);
            }
        });
    }

    //保存抬手亮屏
    public static void saveRaiseHand(final byte[] data, final BaseDevice baseDevice) {

        /**
         *  格式：BE-01-18-FE-开关(bit7:总开关，bit0：时间段使能开关， 00 关闭）-时间段（开始（小时-分钟）-结束（小时-分钟））（此 时时间为抬手不生效时间）  回复：DE-01-18-ED 1. 即 81 时间段开启，80 全天开启，00 全天关闭。
         关闭是2
         全天开启0
         定时开启是1
         */


        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_LiftWristToViewInfoModel model = new Bracelet_W311_LiftWristToViewInfoModel();
                model.setDeviceId(baseDevice.getDeviceName());
                model.setUserId(BaseManager.mUserId);
                int enable = data[4];
                if (enable == 0) {
                    model.setSwichType(2);
                    model.setStartHour(7);
                    model.setStartMin(0);
                    model.setEndHour(22);
                    model.setEndMin(0);
                } else if (enable == 128) {
                    model.setSwichType(0);
                    model.setStartHour(7);
                    model.setStartMin(0);
                    model.setEndHour(22);
                    model.setEndMin(0);
                } else if (enable == 129) {
                    model.setSwichType(1);
                    model.setStartHour(data[5]);
                    model.setStartMin(data[6]);
                    model.setEndHour(data[7]);
                    model.setEndMin(data[8]);

                }

                Bracelet_W311_liftwristModelAction.saveOrUpdateBraceletLift(model);
            }
        });


    }

    public static void saveW526Disturb(final byte[] data, final BaseDevice baseDevice) {
        boolean enable = (data[3] == 1 ? true : false);
        String startTime = CommonDateUtil.formatTwoStr(Utils.byte2Int(data[4])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(data[5]));
        String endTime = CommonDateUtil.formatTwoStr(Utils.byte2Int(data[6])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(data[7]));
        saveDeviceDisturb(baseDevice.getDeviceName(), enable, startTime, endTime);
    }

    //保存勿扰的指令   格式：BE-01-21-FE-开关(00:关 01:开)-开始（小时-分钟）-结 束（小时-分钟）
    public static void saveDisturb(final byte[] data, final BaseDevice baseDevice) {

        boolean enable = data[4] == 0 ? false : true;
        String startTime = CommonDateUtil.formatTwoStr(Utils.byte2Int(data[5])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(data[6]));
        String endTime = CommonDateUtil.formatTwoStr(Utils.byte2Int(data[7])) + ":" + CommonDateUtil.formatTwoStr(Utils.byte2Int(data[8]));
        saveDeviceDisturb(baseDevice.getDeviceName(), enable, startTime, endTime);

    }


    public static void saveDeviceDisturb(String deviceName, boolean enable, String startTime, String endTime) {
        Watch_W516_SleepAndNoDisturbModel watch_w516_sleepAndNoDisturbModel = new Watch_W516_SleepAndNoDisturbModel();
        watch_w516_sleepAndNoDisturbModel.setDeviceId(deviceName);
        watch_w516_sleepAndNoDisturbModel.setUserId(BaseManager.mUserId);
        watch_w516_sleepAndNoDisturbModel.setOpenNoDisturb(enable);
        watch_w516_sleepAndNoDisturbModel.setNoDisturbStartTime(startTime);
        watch_w516_sleepAndNoDisturbModel.setNoDisturbEndTime(endTime);
        ParseData.saveOrUpdateWatchW516SleepAndNoDisturb(watch_w516_sleepAndNoDisturbModel);
    }


    //保存自动心率开启
    public static void saveAutoHeartRateAndTime(final byte[] data, final BaseDevice baseDevice) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                // 格式：BE+01+19+FE+开关（00：关 01：开）+时间（5分钟-60分钟）
                int times = 0;
                boolean isOpen = false;
                Bracelet_w311_hrModel model = new Bracelet_w311_hrModel();
                isOpen = data[4] == 0 ? false : true;
                times = data[5];
                model.setIsOpen(isOpen);
                W560HrSwtchObservable.getInstance().noDataUpdate(isOpen);
                model.setTimes(times);
                model.setUserId(BaseManager.mUserId);
                model.setDeviceId(baseDevice.getDeviceName());
                Bracelet_W311_HrAction.saveOrUpdateBraceletAutoHr(model);
            }
        });


    }


    public static int calAvgHr(List<Integer> hrList) {
        int size = 0;
        int sum = 0;
        int avgHr = 0;
        for (int i = 0; i < hrList.size(); i++) {
            if (hrList.get(i) >= 30) {
                sum += hrList.get(i);
                size++;
            }
        }
        if (sum != 0 && size != 0) {
            avgHr = sum / size;
        } else {
            avgHr = 0;
        }
        return avgHr;
    }

    public static   int getIntFromBytes(byte high_h, byte high_l, byte low_h, byte low_l) {
        return (high_h & 0xff) << 24 | (high_l & 0xff) << 16 | (low_h & 0xff) << 8 | low_l & 0xff;
    }
}
