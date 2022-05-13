package com.isport.blelibrary.db.action;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.isport.blelibrary.gen.BloodPressureModeDao;
import com.isport.blelibrary.gen.Bracelet_W311_24HDataModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_24H_hr_SettingModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_AlarmModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_DeviceInfoModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_DisplayModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_LiftWristToViewInfoModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_RealTimeDataDao;
import com.isport.blelibrary.gen.Bracelet_W311_ThridMessageModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_WearModelDao;
import com.isport.blelibrary.gen.Bracelet_w311_hrModelDao;
import com.isport.blelibrary.gen.DailyBriefDao;
import com.isport.blelibrary.gen.DailySummariesDao;
import com.isport.blelibrary.gen.DaoMaster;
import com.isport.blelibrary.gen.DaoSession;
import com.isport.blelibrary.gen.DeviceInformationTableDao;
import com.isport.blelibrary.gen.DeviceTempUnitlTableDao;
import com.isport.blelibrary.gen.DeviceTimeFormatDao;
import com.isport.blelibrary.gen.DeviceTypeTableDao;
import com.isport.blelibrary.gen.Device_BacklightTimeAndScreenLuminanceModelDao;
import com.isport.blelibrary.gen.Device_TempTableDao;
import com.isport.blelibrary.gen.F18CommonDbBeanDao;
import com.isport.blelibrary.gen.F18DetailStepBeanDao;
import com.isport.blelibrary.gen.FaceWatchModeDao;
import com.isport.blelibrary.gen.OneceHrModeDao;
import com.isport.blelibrary.gen.OxygenModeDao;
import com.isport.blelibrary.gen.S002_Detail_DataDao;
import com.isport.blelibrary.gen.Scale_FourElectrode_DataModelDao;
import com.isport.blelibrary.gen.Sleep_Sleepace_DataModelDao;
import com.isport.blelibrary.gen.Sleep_Sleepace_SleepNoticeModelDao;
import com.isport.blelibrary.gen.SummaryDao;
import com.isport.blelibrary.gen.W81DeviceDetailDataDao;
import com.isport.blelibrary.gen.W81DeviceExerciseDataDao;
import com.isport.blelibrary.gen.W81DeviceExerciseHrDataDao;
import com.isport.blelibrary.gen.Watch_SmartBand_HandScreenModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_ScreenTimeModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_SportDataModelDao;
import com.isport.blelibrary.gen.Watch_SmartBand_StepTargetModelDao;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.gen.Watch_W516_AlarmModelDao;
import com.isport.blelibrary.gen.Watch_W516_NotifyModelDao;
import com.isport.blelibrary.gen.Watch_W516_SedentaryModelDao;
import com.isport.blelibrary.gen.Watch_W516_SettingModelDao;
import com.isport.blelibrary.gen.Watch_W516_SleepAndNoDisturbModelDao;
import com.isport.blelibrary.gen.Watch_W516_SleepDataModelDao;
import com.isport.blelibrary.gen.Watch_W560_AlarmModelDao;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;

/**
 * @Author
 * @Date 2017/9/10
 * @Fuction
 */

public class BleAction {

    //lt 小于  le小于等于  gt大于 ge大于等于 eq等于  noteq不等于


    private static SQLiteDatabase sSQLiteDatabase;
    private static DaoMaster sDaoMaster;
    private static DaoSession sDaoSession;
    private static DeviceInformationTableDao sDeviceInformationTableDao;
    private static DeviceTypeTableDao sDeviceTypeTableDao;

    private static Scale_FourElectrode_DataModelDao sScale_FourElectrode_DataModelDao;

    private static Watch_SmartBand_StepTargetModelDao sWatch_SmartBand_StepTargetModelDao;
    private static Watch_SmartBand_HandScreenModelDao sWatch_SmartBand_HandScreenModelDao;
    private static Watch_SmartBand_ScreenTimeModelDao sWatch_SmartBand_ScreenTimeModelDao;
    private static Watch_SmartBand_SportDataModelDao sWatch_SmartBand_SportDataModelDao;

    private static Sleep_Sleepace_SleepNoticeModelDao sSleep_Sleepace_SleepNoticeModelDao;
    private static Sleep_Sleepace_DataModelDao sSleep_Sleepace_DataModelDao;

    private static Watch_W516_24HDataModelDao sWatch_w516_24HDataModelDao;
    private static Watch_W516_NotifyModelDao sWatch_w516_notifyModelDao;
    private static Watch_W516_AlarmModelDao sWatch_w516_alarmModelDao;
    private static Watch_W516_SedentaryModelDao sWatch_w516_sedentaryModelDao;
    private static Watch_W516_SettingModelDao sWatch_w516_settingModelDao;
    private static Watch_W516_SleepAndNoDisturbModelDao sWatch_w516_sleepAndNoDisturbModelDao;
    private static Watch_W516_SleepDataModelDao sWatch_w516_sleepDataModelDao;

    private static Watch_W560_AlarmModelDao sWatch_w560_alarmModelDao;

    private static Bracelet_W311_LiftWristToViewInfoModelDao sBracelet_w311_liftWristToViewInfoModelDao;
    private static Bracelet_W311_DisplayModelDao sBracelet_w311_displayModelDao;
    private static Bracelet_W311_ThridMessageModelDao sBracelet_w311_thridMessageModelDao;
    private static Bracelet_W311_WearModelDao sBracelet_w311_wearModelDao;
    private static Bracelet_W311_24HDataModelDao sBracelet_w311_24HDataModelDao;
    private static Bracelet_W311_DeviceInfoModelDao sBracelet_w311_deviceInfoModelDao;
    private static Bracelet_w311_hrModelDao sBracelet_w311_hrModelDao;
    private static Bracelet_W311_AlarmModelDao sBracelet_w311_alarmModelDao;
    private static Bracelet_W311_RealTimeDataDao sBracelet_w311_realTimeDataDao;
    private static Bracelet_W311_24H_hr_SettingModelDao sBracelet_w311_24H_hr_settingModelDao;

    private static FaceWatchModeDao sFaceWatchModeDao;
    private static DeviceTimeFormatDao sDeviceTimeFormatDao;
    private static BloodPressureModeDao sBloodPressureModeDao;
    private static OneceHrModeDao sOneceHrModeDao;
    private static Device_TempTableDao sDeviceTempTableDao;
    private static S002_Detail_DataDao s002_detail_dataDao;
    /*   private static S002SummaryDataDao s002SummaryDataDao;
       private static DailyBriefDao sDailyBriefDao;*/
    private static OxygenModeDao sOxygenModeDao;
    private static W81DeviceDetailDataDao sW81DeviceDetailDataDao;
    private static W81DeviceExerciseDataDao sW81DeviceExerciseDataDao;
    private static DeviceTempUnitlTableDao deviceTempUnitlTableDao;
    private static W81DeviceExerciseHrDataDao sW81DeviceExerciseHrDataDao;
    private static Device_BacklightTimeAndScreenLuminanceModelDao screenLuminanceModelDao;

    private static DailyBriefDao sDailyBriefDao;
    private static DailySummariesDao dailySummariesDao;
    private static SummaryDao summaryDao;


    //F18设置项Dao
    private static F18CommonDbBeanDao f18CommonDbBeanDao;

    //F18保存手表详细计步的Dao
    private static F18DetailStepBeanDao f18DetailStepBeanDao;

    public static F18DetailStepBeanDao getF18DetailStepBeanDao() {
        return f18DetailStepBeanDao;
    }

    public static F18CommonDbBeanDao getF18CommonDbBeanDao() {
        return f18CommonDbBeanDao;
    }


    public static DeviceTimeFormatDao getsDeviceTimeFormatDao() {
        return sDeviceTimeFormatDao;
    }

    public static BloodPressureModeDao getsBloodPressureModeDao() {
        return sBloodPressureModeDao;
    }

    public static OneceHrModeDao getsOneceHrModeDao() {
        return sOneceHrModeDao;
    }

    public static Device_TempTableDao getsDeviceTempTableDao() {
        return sDeviceTempTableDao;
    }

    public static S002_Detail_DataDao getS002DetailDataDao() {
        return s002_detail_dataDao;
    }


    public static OxygenModeDao getsOxygenModeDao() {
        return sOxygenModeDao;
    }


    public static DeviceInformationTableDao getDeviceInformationTableDao() {
        return sDeviceInformationTableDao;
    }

    public static DeviceTypeTableDao getDeviceTypeTableDao() {
        return sDeviceTypeTableDao;
    }

    public static Scale_FourElectrode_DataModelDao getScale_FourElectrode_DataModelDao() {
        return sScale_FourElectrode_DataModelDao;
    }

    public static Watch_SmartBand_StepTargetModelDao getWatch_SmartBand_StepTargetModelDao() {
        return sWatch_SmartBand_StepTargetModelDao;
    }

    public static Watch_SmartBand_HandScreenModelDao getWatch_SmartBand_HandScreenModelDao() {
        return sWatch_SmartBand_HandScreenModelDao;
    }

    public static Watch_SmartBand_ScreenTimeModelDao getWatch_SmartBand_ScreenTimeModelDao() {
        return sWatch_SmartBand_ScreenTimeModelDao;
    }

    public static Watch_SmartBand_SportDataModelDao getWatch_SmartBand_SportDataModelDao() {
        return sWatch_SmartBand_SportDataModelDao;
    }

    public static Sleep_Sleepace_SleepNoticeModelDao getSleep_Sleepace_SleepNoticeModelDao() {
        return sSleep_Sleepace_SleepNoticeModelDao;
    }

    public static Sleep_Sleepace_DataModelDao getSleep_Sleepace_DataModelDao() {
        return sSleep_Sleepace_DataModelDao;
    }

    public static Watch_W516_SleepDataModelDao getWatch_W516_SleepDataModelDao() {
        return sWatch_w516_sleepDataModelDao;
    }

    public static Watch_W516_SleepAndNoDisturbModelDao getWatch_W516_SleepAndNoDisturbModelDao() {
        return sWatch_w516_sleepAndNoDisturbModelDao;
    }

    public static Watch_W516_SettingModelDao getWatch_W516_SettingModelDao() {
        return sWatch_w516_settingModelDao;
    }

    public static Watch_W516_SedentaryModelDao getWatch_W516_SedentaryModelDao() {
        return sWatch_w516_sedentaryModelDao;
    }

    public static Watch_W516_AlarmModelDao getWatch_W516_AlarmModelDao() {
        return sWatch_w516_alarmModelDao;
    }

    public static Watch_W560_AlarmModelDao getWatch_W560_AlarmModelDao() {
        return sWatch_w560_alarmModelDao;
    }

    public static Watch_W516_NotifyModelDao getWatch_W516_NotifyModelDao() {
        return sWatch_w516_notifyModelDao;
    }

    public static Watch_W516_24HDataModelDao getWatch_W516_24HDataModelDao() {
        return sWatch_w516_24HDataModelDao;
    }

    public static Bracelet_W311_DisplayModelDao getsBracelet_w311_displayModelDao() {
        return sBracelet_w311_displayModelDao;
    }

    public static Bracelet_W311_ThridMessageModelDao getsBracelet_w311_thridMessageModelDao() {
        return sBracelet_w311_thridMessageModelDao;
    }

    public static Bracelet_W311_WearModelDao getBracelet_w311_wearModelDao() {
        return sBracelet_w311_wearModelDao;
    }

    public static Bracelet_W311_LiftWristToViewInfoModelDao getsBracelet_w311_liftWristToViewInfoModelDao() {
        return sBracelet_w311_liftWristToViewInfoModelDao;
    }

    public static Bracelet_w311_hrModelDao getsBracelet_w311_hrModelDao() {
        return sBracelet_w311_hrModelDao;
    }

    public static Bracelet_W311_24HDataModelDao getsBracelet_w311_24HDataModelDao() {
        return sBracelet_w311_24HDataModelDao;
    }

    public static Bracelet_W311_AlarmModelDao getBracelet_W311_AlarmModelDao() {
        return sBracelet_w311_alarmModelDao;
    }

    public static Bracelet_W311_RealTimeDataDao getBracelet_w311_realTimeDataDao() {
        return sBracelet_w311_realTimeDataDao;
    }

    public static Bracelet_W311_24H_hr_SettingModelDao getsBracelet_w311_24H_hr_settingModelDao() {
        return sBracelet_w311_24H_hr_settingModelDao;
    }

    public static Bracelet_W311_DeviceInfoModelDao getsBracelet_w311_deviceInfoModelDao() {
        return sBracelet_w311_deviceInfoModelDao;
    }

    public static W81DeviceDetailDataDao getsW81DeviceDetailDataDao() {
        return sW81DeviceDetailDataDao;
    }

    public static DailyBriefDao getsDailyBriefDao(){
        return sDailyBriefDao;
    }
    public static SummaryDao getSummaryDao(){
        return summaryDao;
    }
    public static DailySummariesDao getDailySummariesDao(){
        return dailySummariesDao;
    }

    public static W81DeviceExerciseDataDao getsW81DeviceExerciseDataDao() {
        return sW81DeviceExerciseDataDao;
    }

    public static DeviceTempUnitlTableDao getDeviceTempUnitlTableDao() {
        return deviceTempUnitlTableDao;
    }

    public static W81DeviceExerciseHrDataDao getsW81DeviceExerciseHrDataDao() {
        return sW81DeviceExerciseHrDataDao;
    }

    public static Device_BacklightTimeAndScreenLuminanceModelDao getScreenLuminanceModelDao() {
        return screenLuminanceModelDao;
    }


    public static FaceWatchModeDao getsFaceWatchModeDao() {
        return sFaceWatchModeDao;
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    public static void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "isport_brandapp_ble_db", null);
        sSQLiteDatabase = helper.getWritableDatabase();
        sDaoMaster = new DaoMaster(sSQLiteDatabase);
        sDaoSession = sDaoMaster.newSession();
        sDeviceInformationTableDao = sDaoSession.getDeviceInformationTableDao();
        sDeviceTypeTableDao = sDaoSession.getDeviceTypeTableDao();


        sScale_FourElectrode_DataModelDao = sDaoSession.getScale_FourElectrode_DataModelDao();

        sWatch_SmartBand_StepTargetModelDao = sDaoSession.getWatch_SmartBand_StepTargetModelDao();
        sWatch_SmartBand_HandScreenModelDao = sDaoSession.getWatch_SmartBand_HandScreenModelDao();
        sWatch_SmartBand_ScreenTimeModelDao = sDaoSession.getWatch_SmartBand_ScreenTimeModelDao();
        sWatch_SmartBand_SportDataModelDao = sDaoSession.getWatch_SmartBand_SportDataModelDao();

        sSleep_Sleepace_SleepNoticeModelDao = sDaoSession.getSleep_Sleepace_SleepNoticeModelDao();
        sSleep_Sleepace_DataModelDao = sDaoSession.getSleep_Sleepace_DataModelDao();

        sWatch_w516_24HDataModelDao = sDaoSession.getWatch_W516_24HDataModelDao();
        sWatch_w516_notifyModelDao = sDaoSession.getWatch_W516_NotifyModelDao();
        sWatch_w516_alarmModelDao = sDaoSession.getWatch_W516_AlarmModelDao();
        sWatch_w516_sedentaryModelDao = sDaoSession.getWatch_W516_SedentaryModelDao();
        sWatch_w516_settingModelDao = sDaoSession.getWatch_W516_SettingModelDao();
        sWatch_w516_sleepAndNoDisturbModelDao = sDaoSession.getWatch_W516_SleepAndNoDisturbModelDao();
        sWatch_w516_sleepDataModelDao = sDaoSession.getWatch_W516_SleepDataModelDao();

        sWatch_w560_alarmModelDao = sDaoSession.getWatch_W560_AlarmModelDao();

        sBracelet_w311_displayModelDao = sDaoSession.getBracelet_W311_DisplayModelDao();
        sBracelet_w311_liftWristToViewInfoModelDao = sDaoSession.getBracelet_W311_LiftWristToViewInfoModelDao();
        sBracelet_w311_thridMessageModelDao = sDaoSession.getBracelet_W311_ThridMessageModelDao();
        sBracelet_w311_wearModelDao = sDaoSession.getBracelet_W311_WearModelDao();
        sBracelet_w311_24HDataModelDao = sDaoSession.getBracelet_W311_24HDataModelDao();
        sBracelet_w311_deviceInfoModelDao = sDaoSession.getBracelet_W311_DeviceInfoModelDao();
        sBracelet_w311_hrModelDao = sDaoSession.getBracelet_w311_hrModelDao();
        sBracelet_w311_alarmModelDao = sDaoSession.getBracelet_W311_AlarmModelDao();
        sBracelet_w311_realTimeDataDao = sDaoSession.getBracelet_W311_RealTimeDataDao();
        sBracelet_w311_24H_hr_settingModelDao = sDaoSession.getBracelet_W311_24H_hr_SettingModelDao();

        sFaceWatchModeDao = sDaoSession.getFaceWatchModeDao();
        sBloodPressureModeDao = sDaoSession.getBloodPressureModeDao();
        sOxygenModeDao = sDaoSession.getOxygenModeDao();
        sDeviceTimeFormatDao = sDaoSession.getDeviceTimeFormatDao();
        sW81DeviceDetailDataDao = sDaoSession.getW81DeviceDetailDataDao();
        sW81DeviceExerciseDataDao = sDaoSession.getW81DeviceExerciseDataDao();
        deviceTempUnitlTableDao = sDaoSession.getDeviceTempUnitlTableDao();
        sW81DeviceExerciseHrDataDao = sDaoSession.getW81DeviceExerciseHrDataDao();
        screenLuminanceModelDao = sDaoSession.getDevice_BacklightTimeAndScreenLuminanceModelDao();
        sOneceHrModeDao = sDaoSession.getOneceHrModeDao();
        sDeviceTempTableDao = sDaoSession.getDevice_TempTableDao();
        s002_detail_dataDao = sDaoSession.getS002_Detail_DataDao();
        dailySummariesDao=sDaoSession.getDailySummariesDao();
        sDailyBriefDao=sDaoSession.getDailyBriefDao();
        summaryDao=sDaoSession.getSummaryDao();
        f18CommonDbBeanDao = sDaoSession.getF18CommonDbBeanDao();
        f18DetailStepBeanDao = sDaoSession.getF18DetailStepBeanDao();
    }

    public static void dropDatas() {
    }


    public static void deletDeviceInfo() {
       /* ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                sDeviceInformationTableDao.deleteAll();
            }
        });*/
    }

    public static void deletAll() {

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                // sDeviceInformationTableDao.deleteAll();
                sDeviceInformationTableDao.deleteAll();
                sDeviceTypeTableDao.deleteAll();
                sScale_FourElectrode_DataModelDao.deleteAll();
                sWatch_SmartBand_StepTargetModelDao.deleteAll();
                sWatch_SmartBand_HandScreenModelDao.deleteAll();
                sWatch_SmartBand_ScreenTimeModelDao.deleteAll();
                sWatch_SmartBand_SportDataModelDao.deleteAll();
                sSleep_Sleepace_SleepNoticeModelDao.deleteAll();
                sSleep_Sleepace_DataModelDao.deleteAll();
                sWatch_w516_24HDataModelDao.deleteAll();
                sWatch_w516_notifyModelDao.deleteAll();
                sWatch_w516_alarmModelDao.deleteAll();
                sWatch_w516_sedentaryModelDao.deleteAll();
                sWatch_w516_settingModelDao.deleteAll();
                sWatch_w516_sleepAndNoDisturbModelDao.deleteAll();
                sWatch_w516_sleepDataModelDao.deleteAll();
                sWatch_w560_alarmModelDao.deleteAll();
                sBracelet_w311_displayModelDao.deleteAll();
                sBracelet_w311_liftWristToViewInfoModelDao.deleteAll();
                sBracelet_w311_thridMessageModelDao.deleteAll();
                sBracelet_w311_wearModelDao.deleteAll();
                sBracelet_w311_24HDataModelDao.deleteAll();
                sBracelet_w311_deviceInfoModelDao.deleteAll();
                sBracelet_w311_hrModelDao.deleteAll();
                sBracelet_w311_alarmModelDao.deleteAll();
                sBracelet_w311_realTimeDataDao.deleteAll();
                sBracelet_w311_24H_hr_settingModelDao.deleteAll();
                sFaceWatchModeDao.deleteAll();
                sBloodPressureModeDao.deleteAll();
                sOxygenModeDao.deleteAll();
                sDeviceTimeFormatDao.deleteAll();
                sW81DeviceDetailDataDao.deleteAll();
                sW81DeviceExerciseDataDao.deleteAll();
                deviceTempUnitlTableDao.deleteAll();
                sW81DeviceExerciseHrDataDao.deleteAll();
                screenLuminanceModelDao.deleteAll();
                sOneceHrModeDao.deleteAll();
                sDeviceTempTableDao.deleteAll();

//                f18DetailStepBeanDao.deleteAll();
//                f18CommonDbBeanDao.deleteAll();
                Logger.myLog("deletAll table");
            }
        });

    }
}
