package com.isport.blelibrary.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24HDataModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24H_hr_SettingModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DeviceInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DisplayModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_w311_hrModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_WearModel;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.DeviceTempUnitlTable;
import com.isport.blelibrary.db.table.DeviceTypeTable;
import com.isport.blelibrary.db.table.f18.F18CommonDbBean;
import com.isport.blelibrary.db.table.f18.F18DetailStepBean;
import com.isport.blelibrary.db.table.s002.DailyBrief;
import com.isport.blelibrary.db.table.s002.DailySummaries;
import com.isport.blelibrary.db.table.s002.S002_Detail_Data;
import com.isport.blelibrary.db.table.s002.Summary;
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_SleepNoticeModel;
import com.isport.blelibrary.db.table.w526.Device_BacklightTimeAndScreenLuminanceModel;
import com.isport.blelibrary.db.table.w526.Device_TempTable;
import com.isport.blelibrary.db.table.w811w814.BloodPressureMode;
import com.isport.blelibrary.db.table.w811w814.DeviceTimeFormat;
import com.isport.blelibrary.db.table.w811w814.FaceWatchMode;
import com.isport.blelibrary.db.table.w811w814.OneceHrMode;
import com.isport.blelibrary.db.table.w811w814.OxygenMode;
import com.isport.blelibrary.db.table.w811w814.W81DeviceDetailData;
import com.isport.blelibrary.db.table.w811w814.W81DeviceExerciseData;
import com.isport.blelibrary.db.table.w811w814.W81DeviceExerciseHrData;
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
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SleepDataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;

import com.isport.blelibrary.gen.Bracelet_W311_24HDataModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_24H_hr_SettingModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_AlarmModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_DeviceInfoModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_DisplayModelDao;
import com.isport.blelibrary.gen.Bracelet_w311_hrModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_LiftWristToViewInfoModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_RealTimeDataDao;
import com.isport.blelibrary.gen.Bracelet_W311_ThridMessageModelDao;
import com.isport.blelibrary.gen.Bracelet_W311_WearModelDao;
import com.isport.blelibrary.gen.DeviceInformationTableDao;
import com.isport.blelibrary.gen.DeviceTempUnitlTableDao;
import com.isport.blelibrary.gen.DeviceTypeTableDao;
import com.isport.blelibrary.gen.F18CommonDbBeanDao;
import com.isport.blelibrary.gen.F18DetailStepBeanDao;
import com.isport.blelibrary.gen.DailyBriefDao;
import com.isport.blelibrary.gen.DailySummariesDao;
import com.isport.blelibrary.gen.S002_Detail_DataDao;
import com.isport.blelibrary.gen.SummaryDao;
import com.isport.blelibrary.gen.Scale_FourElectrode_DataModelDao;
import com.isport.blelibrary.gen.Sleep_Sleepace_DataModelDao;
import com.isport.blelibrary.gen.Sleep_Sleepace_SleepNoticeModelDao;
import com.isport.blelibrary.gen.Device_BacklightTimeAndScreenLuminanceModelDao;
import com.isport.blelibrary.gen.Device_TempTableDao;
import com.isport.blelibrary.gen.BloodPressureModeDao;
import com.isport.blelibrary.gen.DeviceTimeFormatDao;
import com.isport.blelibrary.gen.FaceWatchModeDao;
import com.isport.blelibrary.gen.OneceHrModeDao;
import com.isport.blelibrary.gen.OxygenModeDao;
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

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bracelet_W311_24HDataModelDaoConfig;
    private final DaoConfig bracelet_W311_24H_hr_SettingModelDaoConfig;
    private final DaoConfig bracelet_W311_AlarmModelDaoConfig;
    private final DaoConfig bracelet_W311_DeviceInfoModelDaoConfig;
    private final DaoConfig bracelet_W311_DisplayModelDaoConfig;
    private final DaoConfig bracelet_w311_hrModelDaoConfig;
    private final DaoConfig bracelet_W311_LiftWristToViewInfoModelDaoConfig;
    private final DaoConfig bracelet_W311_RealTimeDataDaoConfig;
    private final DaoConfig bracelet_W311_ThridMessageModelDaoConfig;
    private final DaoConfig bracelet_W311_WearModelDaoConfig;
    private final DaoConfig deviceInformationTableDaoConfig;
    private final DaoConfig deviceTempUnitlTableDaoConfig;
    private final DaoConfig deviceTypeTableDaoConfig;
    private final DaoConfig f18CommonDbBeanDaoConfig;
    private final DaoConfig f18DetailStepBeanDaoConfig;
    private final DaoConfig dailyBriefDaoConfig;
    private final DaoConfig dailySummariesDaoConfig;
    private final DaoConfig s002_Detail_DataDaoConfig;
    private final DaoConfig summaryDaoConfig;
    private final DaoConfig scale_FourElectrode_DataModelDaoConfig;
    private final DaoConfig sleep_Sleepace_DataModelDaoConfig;
    private final DaoConfig sleep_Sleepace_SleepNoticeModelDaoConfig;
    private final DaoConfig device_BacklightTimeAndScreenLuminanceModelDaoConfig;
    private final DaoConfig device_TempTableDaoConfig;
    private final DaoConfig bloodPressureModeDaoConfig;
    private final DaoConfig deviceTimeFormatDaoConfig;
    private final DaoConfig faceWatchModeDaoConfig;
    private final DaoConfig oneceHrModeDaoConfig;
    private final DaoConfig oxygenModeDaoConfig;
    private final DaoConfig w81DeviceDetailDataDaoConfig;
    private final DaoConfig w81DeviceExerciseDataDaoConfig;
    private final DaoConfig w81DeviceExerciseHrDataDaoConfig;
    private final DaoConfig watch_SmartBand_HandScreenModelDaoConfig;
    private final DaoConfig watch_SmartBand_ScreenTimeModelDaoConfig;
    private final DaoConfig watch_SmartBand_SportDataModelDaoConfig;
    private final DaoConfig watch_SmartBand_StepTargetModelDaoConfig;
    private final DaoConfig watch_W516_24HDataModelDaoConfig;
    private final DaoConfig watch_W516_AlarmModelDaoConfig;
    private final DaoConfig watch_W516_NotifyModelDaoConfig;
    private final DaoConfig watch_W516_SedentaryModelDaoConfig;
    private final DaoConfig watch_W516_SettingModelDaoConfig;
    private final DaoConfig watch_W516_SleepAndNoDisturbModelDaoConfig;
    private final DaoConfig watch_W516_SleepDataModelDaoConfig;
    private final DaoConfig watch_W560_AlarmModelDaoConfig;

    private final Bracelet_W311_24HDataModelDao bracelet_W311_24HDataModelDao;
    private final Bracelet_W311_24H_hr_SettingModelDao bracelet_W311_24H_hr_SettingModelDao;
    private final Bracelet_W311_AlarmModelDao bracelet_W311_AlarmModelDao;
    private final Bracelet_W311_DeviceInfoModelDao bracelet_W311_DeviceInfoModelDao;
    private final Bracelet_W311_DisplayModelDao bracelet_W311_DisplayModelDao;
    private final Bracelet_w311_hrModelDao bracelet_w311_hrModelDao;
    private final Bracelet_W311_LiftWristToViewInfoModelDao bracelet_W311_LiftWristToViewInfoModelDao;
    private final Bracelet_W311_RealTimeDataDao bracelet_W311_RealTimeDataDao;
    private final Bracelet_W311_ThridMessageModelDao bracelet_W311_ThridMessageModelDao;
    private final Bracelet_W311_WearModelDao bracelet_W311_WearModelDao;
    private final DeviceInformationTableDao deviceInformationTableDao;
    private final DeviceTempUnitlTableDao deviceTempUnitlTableDao;
    private final DeviceTypeTableDao deviceTypeTableDao;
    private final F18CommonDbBeanDao f18CommonDbBeanDao;
    private final F18DetailStepBeanDao f18DetailStepBeanDao;
    private final DailyBriefDao dailyBriefDao;
    private final DailySummariesDao dailySummariesDao;
    private final S002_Detail_DataDao s002_Detail_DataDao;
    private final SummaryDao summaryDao;
    private final Scale_FourElectrode_DataModelDao scale_FourElectrode_DataModelDao;
    private final Sleep_Sleepace_DataModelDao sleep_Sleepace_DataModelDao;
    private final Sleep_Sleepace_SleepNoticeModelDao sleep_Sleepace_SleepNoticeModelDao;
    private final Device_BacklightTimeAndScreenLuminanceModelDao device_BacklightTimeAndScreenLuminanceModelDao;
    private final Device_TempTableDao device_TempTableDao;
    private final BloodPressureModeDao bloodPressureModeDao;
    private final DeviceTimeFormatDao deviceTimeFormatDao;
    private final FaceWatchModeDao faceWatchModeDao;
    private final OneceHrModeDao oneceHrModeDao;
    private final OxygenModeDao oxygenModeDao;
    private final W81DeviceDetailDataDao w81DeviceDetailDataDao;
    private final W81DeviceExerciseDataDao w81DeviceExerciseDataDao;
    private final W81DeviceExerciseHrDataDao w81DeviceExerciseHrDataDao;
    private final Watch_SmartBand_HandScreenModelDao watch_SmartBand_HandScreenModelDao;
    private final Watch_SmartBand_ScreenTimeModelDao watch_SmartBand_ScreenTimeModelDao;
    private final Watch_SmartBand_SportDataModelDao watch_SmartBand_SportDataModelDao;
    private final Watch_SmartBand_StepTargetModelDao watch_SmartBand_StepTargetModelDao;
    private final Watch_W516_24HDataModelDao watch_W516_24HDataModelDao;
    private final Watch_W516_AlarmModelDao watch_W516_AlarmModelDao;
    private final Watch_W516_NotifyModelDao watch_W516_NotifyModelDao;
    private final Watch_W516_SedentaryModelDao watch_W516_SedentaryModelDao;
    private final Watch_W516_SettingModelDao watch_W516_SettingModelDao;
    private final Watch_W516_SleepAndNoDisturbModelDao watch_W516_SleepAndNoDisturbModelDao;
    private final Watch_W516_SleepDataModelDao watch_W516_SleepDataModelDao;
    private final Watch_W560_AlarmModelDao watch_W560_AlarmModelDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bracelet_W311_24HDataModelDaoConfig = daoConfigMap.get(Bracelet_W311_24HDataModelDao.class).clone();
        bracelet_W311_24HDataModelDaoConfig.initIdentityScope(type);

        bracelet_W311_24H_hr_SettingModelDaoConfig = daoConfigMap.get(Bracelet_W311_24H_hr_SettingModelDao.class).clone();
        bracelet_W311_24H_hr_SettingModelDaoConfig.initIdentityScope(type);

        bracelet_W311_AlarmModelDaoConfig = daoConfigMap.get(Bracelet_W311_AlarmModelDao.class).clone();
        bracelet_W311_AlarmModelDaoConfig.initIdentityScope(type);

        bracelet_W311_DeviceInfoModelDaoConfig = daoConfigMap.get(Bracelet_W311_DeviceInfoModelDao.class).clone();
        bracelet_W311_DeviceInfoModelDaoConfig.initIdentityScope(type);

        bracelet_W311_DisplayModelDaoConfig = daoConfigMap.get(Bracelet_W311_DisplayModelDao.class).clone();
        bracelet_W311_DisplayModelDaoConfig.initIdentityScope(type);

        bracelet_w311_hrModelDaoConfig = daoConfigMap.get(Bracelet_w311_hrModelDao.class).clone();
        bracelet_w311_hrModelDaoConfig.initIdentityScope(type);

        bracelet_W311_LiftWristToViewInfoModelDaoConfig = daoConfigMap.get(Bracelet_W311_LiftWristToViewInfoModelDao.class).clone();
        bracelet_W311_LiftWristToViewInfoModelDaoConfig.initIdentityScope(type);

        bracelet_W311_RealTimeDataDaoConfig = daoConfigMap.get(Bracelet_W311_RealTimeDataDao.class).clone();
        bracelet_W311_RealTimeDataDaoConfig.initIdentityScope(type);

        bracelet_W311_ThridMessageModelDaoConfig = daoConfigMap.get(Bracelet_W311_ThridMessageModelDao.class).clone();
        bracelet_W311_ThridMessageModelDaoConfig.initIdentityScope(type);

        bracelet_W311_WearModelDaoConfig = daoConfigMap.get(Bracelet_W311_WearModelDao.class).clone();
        bracelet_W311_WearModelDaoConfig.initIdentityScope(type);

        deviceInformationTableDaoConfig = daoConfigMap.get(DeviceInformationTableDao.class).clone();
        deviceInformationTableDaoConfig.initIdentityScope(type);

        deviceTempUnitlTableDaoConfig = daoConfigMap.get(DeviceTempUnitlTableDao.class).clone();
        deviceTempUnitlTableDaoConfig.initIdentityScope(type);

        deviceTypeTableDaoConfig = daoConfigMap.get(DeviceTypeTableDao.class).clone();
        deviceTypeTableDaoConfig.initIdentityScope(type);

        f18CommonDbBeanDaoConfig = daoConfigMap.get(F18CommonDbBeanDao.class).clone();
        f18CommonDbBeanDaoConfig.initIdentityScope(type);

        f18DetailStepBeanDaoConfig = daoConfigMap.get(F18DetailStepBeanDao.class).clone();
        f18DetailStepBeanDaoConfig.initIdentityScope(type);

        dailyBriefDaoConfig = daoConfigMap.get(DailyBriefDao.class).clone();
        dailyBriefDaoConfig.initIdentityScope(type);

        dailySummariesDaoConfig = daoConfigMap.get(DailySummariesDao.class).clone();
        dailySummariesDaoConfig.initIdentityScope(type);

        s002_Detail_DataDaoConfig = daoConfigMap.get(S002_Detail_DataDao.class).clone();
        s002_Detail_DataDaoConfig.initIdentityScope(type);

        summaryDaoConfig = daoConfigMap.get(SummaryDao.class).clone();
        summaryDaoConfig.initIdentityScope(type);

        scale_FourElectrode_DataModelDaoConfig = daoConfigMap.get(Scale_FourElectrode_DataModelDao.class).clone();
        scale_FourElectrode_DataModelDaoConfig.initIdentityScope(type);

        sleep_Sleepace_DataModelDaoConfig = daoConfigMap.get(Sleep_Sleepace_DataModelDao.class).clone();
        sleep_Sleepace_DataModelDaoConfig.initIdentityScope(type);

        sleep_Sleepace_SleepNoticeModelDaoConfig = daoConfigMap.get(Sleep_Sleepace_SleepNoticeModelDao.class).clone();
        sleep_Sleepace_SleepNoticeModelDaoConfig.initIdentityScope(type);

        device_BacklightTimeAndScreenLuminanceModelDaoConfig = daoConfigMap.get(Device_BacklightTimeAndScreenLuminanceModelDao.class).clone();
        device_BacklightTimeAndScreenLuminanceModelDaoConfig.initIdentityScope(type);

        device_TempTableDaoConfig = daoConfigMap.get(Device_TempTableDao.class).clone();
        device_TempTableDaoConfig.initIdentityScope(type);

        bloodPressureModeDaoConfig = daoConfigMap.get(BloodPressureModeDao.class).clone();
        bloodPressureModeDaoConfig.initIdentityScope(type);

        deviceTimeFormatDaoConfig = daoConfigMap.get(DeviceTimeFormatDao.class).clone();
        deviceTimeFormatDaoConfig.initIdentityScope(type);

        faceWatchModeDaoConfig = daoConfigMap.get(FaceWatchModeDao.class).clone();
        faceWatchModeDaoConfig.initIdentityScope(type);

        oneceHrModeDaoConfig = daoConfigMap.get(OneceHrModeDao.class).clone();
        oneceHrModeDaoConfig.initIdentityScope(type);

        oxygenModeDaoConfig = daoConfigMap.get(OxygenModeDao.class).clone();
        oxygenModeDaoConfig.initIdentityScope(type);

        w81DeviceDetailDataDaoConfig = daoConfigMap.get(W81DeviceDetailDataDao.class).clone();
        w81DeviceDetailDataDaoConfig.initIdentityScope(type);

        w81DeviceExerciseDataDaoConfig = daoConfigMap.get(W81DeviceExerciseDataDao.class).clone();
        w81DeviceExerciseDataDaoConfig.initIdentityScope(type);

        w81DeviceExerciseHrDataDaoConfig = daoConfigMap.get(W81DeviceExerciseHrDataDao.class).clone();
        w81DeviceExerciseHrDataDaoConfig.initIdentityScope(type);

        watch_SmartBand_HandScreenModelDaoConfig = daoConfigMap.get(Watch_SmartBand_HandScreenModelDao.class).clone();
        watch_SmartBand_HandScreenModelDaoConfig.initIdentityScope(type);

        watch_SmartBand_ScreenTimeModelDaoConfig = daoConfigMap.get(Watch_SmartBand_ScreenTimeModelDao.class).clone();
        watch_SmartBand_ScreenTimeModelDaoConfig.initIdentityScope(type);

        watch_SmartBand_SportDataModelDaoConfig = daoConfigMap.get(Watch_SmartBand_SportDataModelDao.class).clone();
        watch_SmartBand_SportDataModelDaoConfig.initIdentityScope(type);

        watch_SmartBand_StepTargetModelDaoConfig = daoConfigMap.get(Watch_SmartBand_StepTargetModelDao.class).clone();
        watch_SmartBand_StepTargetModelDaoConfig.initIdentityScope(type);

        watch_W516_24HDataModelDaoConfig = daoConfigMap.get(Watch_W516_24HDataModelDao.class).clone();
        watch_W516_24HDataModelDaoConfig.initIdentityScope(type);

        watch_W516_AlarmModelDaoConfig = daoConfigMap.get(Watch_W516_AlarmModelDao.class).clone();
        watch_W516_AlarmModelDaoConfig.initIdentityScope(type);

        watch_W516_NotifyModelDaoConfig = daoConfigMap.get(Watch_W516_NotifyModelDao.class).clone();
        watch_W516_NotifyModelDaoConfig.initIdentityScope(type);

        watch_W516_SedentaryModelDaoConfig = daoConfigMap.get(Watch_W516_SedentaryModelDao.class).clone();
        watch_W516_SedentaryModelDaoConfig.initIdentityScope(type);

        watch_W516_SettingModelDaoConfig = daoConfigMap.get(Watch_W516_SettingModelDao.class).clone();
        watch_W516_SettingModelDaoConfig.initIdentityScope(type);

        watch_W516_SleepAndNoDisturbModelDaoConfig = daoConfigMap.get(Watch_W516_SleepAndNoDisturbModelDao.class).clone();
        watch_W516_SleepAndNoDisturbModelDaoConfig.initIdentityScope(type);

        watch_W516_SleepDataModelDaoConfig = daoConfigMap.get(Watch_W516_SleepDataModelDao.class).clone();
        watch_W516_SleepDataModelDaoConfig.initIdentityScope(type);

        watch_W560_AlarmModelDaoConfig = daoConfigMap.get(Watch_W560_AlarmModelDao.class).clone();
        watch_W560_AlarmModelDaoConfig.initIdentityScope(type);

        bracelet_W311_24HDataModelDao = new Bracelet_W311_24HDataModelDao(bracelet_W311_24HDataModelDaoConfig, this);
        bracelet_W311_24H_hr_SettingModelDao = new Bracelet_W311_24H_hr_SettingModelDao(bracelet_W311_24H_hr_SettingModelDaoConfig, this);
        bracelet_W311_AlarmModelDao = new Bracelet_W311_AlarmModelDao(bracelet_W311_AlarmModelDaoConfig, this);
        bracelet_W311_DeviceInfoModelDao = new Bracelet_W311_DeviceInfoModelDao(bracelet_W311_DeviceInfoModelDaoConfig, this);
        bracelet_W311_DisplayModelDao = new Bracelet_W311_DisplayModelDao(bracelet_W311_DisplayModelDaoConfig, this);
        bracelet_w311_hrModelDao = new Bracelet_w311_hrModelDao(bracelet_w311_hrModelDaoConfig, this);
        bracelet_W311_LiftWristToViewInfoModelDao = new Bracelet_W311_LiftWristToViewInfoModelDao(bracelet_W311_LiftWristToViewInfoModelDaoConfig, this);
        bracelet_W311_RealTimeDataDao = new Bracelet_W311_RealTimeDataDao(bracelet_W311_RealTimeDataDaoConfig, this);
        bracelet_W311_ThridMessageModelDao = new Bracelet_W311_ThridMessageModelDao(bracelet_W311_ThridMessageModelDaoConfig, this);
        bracelet_W311_WearModelDao = new Bracelet_W311_WearModelDao(bracelet_W311_WearModelDaoConfig, this);
        deviceInformationTableDao = new DeviceInformationTableDao(deviceInformationTableDaoConfig, this);
        deviceTempUnitlTableDao = new DeviceTempUnitlTableDao(deviceTempUnitlTableDaoConfig, this);
        deviceTypeTableDao = new DeviceTypeTableDao(deviceTypeTableDaoConfig, this);
        f18CommonDbBeanDao = new F18CommonDbBeanDao(f18CommonDbBeanDaoConfig, this);
        f18DetailStepBeanDao = new F18DetailStepBeanDao(f18DetailStepBeanDaoConfig, this);
        dailyBriefDao = new DailyBriefDao(dailyBriefDaoConfig, this);
        dailySummariesDao = new DailySummariesDao(dailySummariesDaoConfig, this);
        s002_Detail_DataDao = new S002_Detail_DataDao(s002_Detail_DataDaoConfig, this);
        summaryDao = new SummaryDao(summaryDaoConfig, this);
        scale_FourElectrode_DataModelDao = new Scale_FourElectrode_DataModelDao(scale_FourElectrode_DataModelDaoConfig, this);
        sleep_Sleepace_DataModelDao = new Sleep_Sleepace_DataModelDao(sleep_Sleepace_DataModelDaoConfig, this);
        sleep_Sleepace_SleepNoticeModelDao = new Sleep_Sleepace_SleepNoticeModelDao(sleep_Sleepace_SleepNoticeModelDaoConfig, this);
        device_BacklightTimeAndScreenLuminanceModelDao = new Device_BacklightTimeAndScreenLuminanceModelDao(device_BacklightTimeAndScreenLuminanceModelDaoConfig, this);
        device_TempTableDao = new Device_TempTableDao(device_TempTableDaoConfig, this);
        bloodPressureModeDao = new BloodPressureModeDao(bloodPressureModeDaoConfig, this);
        deviceTimeFormatDao = new DeviceTimeFormatDao(deviceTimeFormatDaoConfig, this);
        faceWatchModeDao = new FaceWatchModeDao(faceWatchModeDaoConfig, this);
        oneceHrModeDao = new OneceHrModeDao(oneceHrModeDaoConfig, this);
        oxygenModeDao = new OxygenModeDao(oxygenModeDaoConfig, this);
        w81DeviceDetailDataDao = new W81DeviceDetailDataDao(w81DeviceDetailDataDaoConfig, this);
        w81DeviceExerciseDataDao = new W81DeviceExerciseDataDao(w81DeviceExerciseDataDaoConfig, this);
        w81DeviceExerciseHrDataDao = new W81DeviceExerciseHrDataDao(w81DeviceExerciseHrDataDaoConfig, this);
        watch_SmartBand_HandScreenModelDao = new Watch_SmartBand_HandScreenModelDao(watch_SmartBand_HandScreenModelDaoConfig, this);
        watch_SmartBand_ScreenTimeModelDao = new Watch_SmartBand_ScreenTimeModelDao(watch_SmartBand_ScreenTimeModelDaoConfig, this);
        watch_SmartBand_SportDataModelDao = new Watch_SmartBand_SportDataModelDao(watch_SmartBand_SportDataModelDaoConfig, this);
        watch_SmartBand_StepTargetModelDao = new Watch_SmartBand_StepTargetModelDao(watch_SmartBand_StepTargetModelDaoConfig, this);
        watch_W516_24HDataModelDao = new Watch_W516_24HDataModelDao(watch_W516_24HDataModelDaoConfig, this);
        watch_W516_AlarmModelDao = new Watch_W516_AlarmModelDao(watch_W516_AlarmModelDaoConfig, this);
        watch_W516_NotifyModelDao = new Watch_W516_NotifyModelDao(watch_W516_NotifyModelDaoConfig, this);
        watch_W516_SedentaryModelDao = new Watch_W516_SedentaryModelDao(watch_W516_SedentaryModelDaoConfig, this);
        watch_W516_SettingModelDao = new Watch_W516_SettingModelDao(watch_W516_SettingModelDaoConfig, this);
        watch_W516_SleepAndNoDisturbModelDao = new Watch_W516_SleepAndNoDisturbModelDao(watch_W516_SleepAndNoDisturbModelDaoConfig, this);
        watch_W516_SleepDataModelDao = new Watch_W516_SleepDataModelDao(watch_W516_SleepDataModelDaoConfig, this);
        watch_W560_AlarmModelDao = new Watch_W560_AlarmModelDao(watch_W560_AlarmModelDaoConfig, this);

        registerDao(Bracelet_W311_24HDataModel.class, bracelet_W311_24HDataModelDao);
        registerDao(Bracelet_W311_24H_hr_SettingModel.class, bracelet_W311_24H_hr_SettingModelDao);
        registerDao(Bracelet_W311_AlarmModel.class, bracelet_W311_AlarmModelDao);
        registerDao(Bracelet_W311_DeviceInfoModel.class, bracelet_W311_DeviceInfoModelDao);
        registerDao(Bracelet_W311_DisplayModel.class, bracelet_W311_DisplayModelDao);
        registerDao(Bracelet_w311_hrModel.class, bracelet_w311_hrModelDao);
        registerDao(Bracelet_W311_LiftWristToViewInfoModel.class, bracelet_W311_LiftWristToViewInfoModelDao);
        registerDao(Bracelet_W311_RealTimeData.class, bracelet_W311_RealTimeDataDao);
        registerDao(Bracelet_W311_ThridMessageModel.class, bracelet_W311_ThridMessageModelDao);
        registerDao(Bracelet_W311_WearModel.class, bracelet_W311_WearModelDao);
        registerDao(DeviceInformationTable.class, deviceInformationTableDao);
        registerDao(DeviceTempUnitlTable.class, deviceTempUnitlTableDao);
        registerDao(DeviceTypeTable.class, deviceTypeTableDao);
        registerDao(F18CommonDbBean.class, f18CommonDbBeanDao);
        registerDao(F18DetailStepBean.class, f18DetailStepBeanDao);
        registerDao(DailyBrief.class, dailyBriefDao);
        registerDao(DailySummaries.class, dailySummariesDao);
        registerDao(S002_Detail_Data.class, s002_Detail_DataDao);
        registerDao(Summary.class, summaryDao);
        registerDao(Scale_FourElectrode_DataModel.class, scale_FourElectrode_DataModelDao);
        registerDao(Sleep_Sleepace_DataModel.class, sleep_Sleepace_DataModelDao);
        registerDao(Sleep_Sleepace_SleepNoticeModel.class, sleep_Sleepace_SleepNoticeModelDao);
        registerDao(Device_BacklightTimeAndScreenLuminanceModel.class, device_BacklightTimeAndScreenLuminanceModelDao);
        registerDao(Device_TempTable.class, device_TempTableDao);
        registerDao(BloodPressureMode.class, bloodPressureModeDao);
        registerDao(DeviceTimeFormat.class, deviceTimeFormatDao);
        registerDao(FaceWatchMode.class, faceWatchModeDao);
        registerDao(OneceHrMode.class, oneceHrModeDao);
        registerDao(OxygenMode.class, oxygenModeDao);
        registerDao(W81DeviceDetailData.class, w81DeviceDetailDataDao);
        registerDao(W81DeviceExerciseData.class, w81DeviceExerciseDataDao);
        registerDao(W81DeviceExerciseHrData.class, w81DeviceExerciseHrDataDao);
        registerDao(Watch_SmartBand_HandScreenModel.class, watch_SmartBand_HandScreenModelDao);
        registerDao(Watch_SmartBand_ScreenTimeModel.class, watch_SmartBand_ScreenTimeModelDao);
        registerDao(Watch_SmartBand_SportDataModel.class, watch_SmartBand_SportDataModelDao);
        registerDao(Watch_SmartBand_StepTargetModel.class, watch_SmartBand_StepTargetModelDao);
        registerDao(Watch_W516_24HDataModel.class, watch_W516_24HDataModelDao);
        registerDao(Watch_W516_AlarmModel.class, watch_W516_AlarmModelDao);
        registerDao(Watch_W516_NotifyModel.class, watch_W516_NotifyModelDao);
        registerDao(Watch_W516_SedentaryModel.class, watch_W516_SedentaryModelDao);
        registerDao(Watch_W516_SettingModel.class, watch_W516_SettingModelDao);
        registerDao(Watch_W516_SleepAndNoDisturbModel.class, watch_W516_SleepAndNoDisturbModelDao);
        registerDao(Watch_W516_SleepDataModel.class, watch_W516_SleepDataModelDao);
        registerDao(Watch_W560_AlarmModel.class, watch_W560_AlarmModelDao);
    }
    
    public void clear() {
        bracelet_W311_24HDataModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_24H_hr_SettingModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_AlarmModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_DeviceInfoModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_DisplayModelDaoConfig.getIdentityScope().clear();
        bracelet_w311_hrModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_LiftWristToViewInfoModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_RealTimeDataDaoConfig.getIdentityScope().clear();
        bracelet_W311_ThridMessageModelDaoConfig.getIdentityScope().clear();
        bracelet_W311_WearModelDaoConfig.getIdentityScope().clear();
        deviceInformationTableDaoConfig.getIdentityScope().clear();
        deviceTempUnitlTableDaoConfig.getIdentityScope().clear();
        deviceTypeTableDaoConfig.getIdentityScope().clear();
        f18CommonDbBeanDaoConfig.getIdentityScope().clear();
        f18DetailStepBeanDaoConfig.getIdentityScope().clear();
        dailyBriefDaoConfig.getIdentityScope().clear();
        dailySummariesDaoConfig.getIdentityScope().clear();
        s002_Detail_DataDaoConfig.getIdentityScope().clear();
        summaryDaoConfig.getIdentityScope().clear();
        scale_FourElectrode_DataModelDaoConfig.getIdentityScope().clear();
        sleep_Sleepace_DataModelDaoConfig.getIdentityScope().clear();
        sleep_Sleepace_SleepNoticeModelDaoConfig.getIdentityScope().clear();
        device_BacklightTimeAndScreenLuminanceModelDaoConfig.getIdentityScope().clear();
        device_TempTableDaoConfig.getIdentityScope().clear();
        bloodPressureModeDaoConfig.getIdentityScope().clear();
        deviceTimeFormatDaoConfig.getIdentityScope().clear();
        faceWatchModeDaoConfig.getIdentityScope().clear();
        oneceHrModeDaoConfig.getIdentityScope().clear();
        oxygenModeDaoConfig.getIdentityScope().clear();
        w81DeviceDetailDataDaoConfig.getIdentityScope().clear();
        w81DeviceExerciseDataDaoConfig.getIdentityScope().clear();
        w81DeviceExerciseHrDataDaoConfig.getIdentityScope().clear();
        watch_SmartBand_HandScreenModelDaoConfig.getIdentityScope().clear();
        watch_SmartBand_ScreenTimeModelDaoConfig.getIdentityScope().clear();
        watch_SmartBand_SportDataModelDaoConfig.getIdentityScope().clear();
        watch_SmartBand_StepTargetModelDaoConfig.getIdentityScope().clear();
        watch_W516_24HDataModelDaoConfig.getIdentityScope().clear();
        watch_W516_AlarmModelDaoConfig.getIdentityScope().clear();
        watch_W516_NotifyModelDaoConfig.getIdentityScope().clear();
        watch_W516_SedentaryModelDaoConfig.getIdentityScope().clear();
        watch_W516_SettingModelDaoConfig.getIdentityScope().clear();
        watch_W516_SleepAndNoDisturbModelDaoConfig.getIdentityScope().clear();
        watch_W516_SleepDataModelDaoConfig.getIdentityScope().clear();
        watch_W560_AlarmModelDaoConfig.getIdentityScope().clear();
    }

    public Bracelet_W311_24HDataModelDao getBracelet_W311_24HDataModelDao() {
        return bracelet_W311_24HDataModelDao;
    }

    public Bracelet_W311_24H_hr_SettingModelDao getBracelet_W311_24H_hr_SettingModelDao() {
        return bracelet_W311_24H_hr_SettingModelDao;
    }

    public Bracelet_W311_AlarmModelDao getBracelet_W311_AlarmModelDao() {
        return bracelet_W311_AlarmModelDao;
    }

    public Bracelet_W311_DeviceInfoModelDao getBracelet_W311_DeviceInfoModelDao() {
        return bracelet_W311_DeviceInfoModelDao;
    }

    public Bracelet_W311_DisplayModelDao getBracelet_W311_DisplayModelDao() {
        return bracelet_W311_DisplayModelDao;
    }

    public Bracelet_w311_hrModelDao getBracelet_w311_hrModelDao() {
        return bracelet_w311_hrModelDao;
    }

    public Bracelet_W311_LiftWristToViewInfoModelDao getBracelet_W311_LiftWristToViewInfoModelDao() {
        return bracelet_W311_LiftWristToViewInfoModelDao;
    }

    public Bracelet_W311_RealTimeDataDao getBracelet_W311_RealTimeDataDao() {
        return bracelet_W311_RealTimeDataDao;
    }

    public Bracelet_W311_ThridMessageModelDao getBracelet_W311_ThridMessageModelDao() {
        return bracelet_W311_ThridMessageModelDao;
    }

    public Bracelet_W311_WearModelDao getBracelet_W311_WearModelDao() {
        return bracelet_W311_WearModelDao;
    }

    public DeviceInformationTableDao getDeviceInformationTableDao() {
        return deviceInformationTableDao;
    }

    public DeviceTempUnitlTableDao getDeviceTempUnitlTableDao() {
        return deviceTempUnitlTableDao;
    }

    public DeviceTypeTableDao getDeviceTypeTableDao() {
        return deviceTypeTableDao;
    }

    public F18CommonDbBeanDao getF18CommonDbBeanDao() {
        return f18CommonDbBeanDao;
    }

    public F18DetailStepBeanDao getF18DetailStepBeanDao() {
        return f18DetailStepBeanDao;
    }

    public DailyBriefDao getDailyBriefDao() {
        return dailyBriefDao;
    }

    public DailySummariesDao getDailySummariesDao() {
        return dailySummariesDao;
    }

    public S002_Detail_DataDao getS002_Detail_DataDao() {
        return s002_Detail_DataDao;
    }

    public SummaryDao getSummaryDao() {
        return summaryDao;
    }

    public Scale_FourElectrode_DataModelDao getScale_FourElectrode_DataModelDao() {
        return scale_FourElectrode_DataModelDao;
    }

    public Sleep_Sleepace_DataModelDao getSleep_Sleepace_DataModelDao() {
        return sleep_Sleepace_DataModelDao;
    }

    public Sleep_Sleepace_SleepNoticeModelDao getSleep_Sleepace_SleepNoticeModelDao() {
        return sleep_Sleepace_SleepNoticeModelDao;
    }

    public Device_BacklightTimeAndScreenLuminanceModelDao getDevice_BacklightTimeAndScreenLuminanceModelDao() {
        return device_BacklightTimeAndScreenLuminanceModelDao;
    }

    public Device_TempTableDao getDevice_TempTableDao() {
        return device_TempTableDao;
    }

    public BloodPressureModeDao getBloodPressureModeDao() {
        return bloodPressureModeDao;
    }

    public DeviceTimeFormatDao getDeviceTimeFormatDao() {
        return deviceTimeFormatDao;
    }

    public FaceWatchModeDao getFaceWatchModeDao() {
        return faceWatchModeDao;
    }

    public OneceHrModeDao getOneceHrModeDao() {
        return oneceHrModeDao;
    }

    public OxygenModeDao getOxygenModeDao() {
        return oxygenModeDao;
    }

    public W81DeviceDetailDataDao getW81DeviceDetailDataDao() {
        return w81DeviceDetailDataDao;
    }

    public W81DeviceExerciseDataDao getW81DeviceExerciseDataDao() {
        return w81DeviceExerciseDataDao;
    }

    public W81DeviceExerciseHrDataDao getW81DeviceExerciseHrDataDao() {
        return w81DeviceExerciseHrDataDao;
    }

    public Watch_SmartBand_HandScreenModelDao getWatch_SmartBand_HandScreenModelDao() {
        return watch_SmartBand_HandScreenModelDao;
    }

    public Watch_SmartBand_ScreenTimeModelDao getWatch_SmartBand_ScreenTimeModelDao() {
        return watch_SmartBand_ScreenTimeModelDao;
    }

    public Watch_SmartBand_SportDataModelDao getWatch_SmartBand_SportDataModelDao() {
        return watch_SmartBand_SportDataModelDao;
    }

    public Watch_SmartBand_StepTargetModelDao getWatch_SmartBand_StepTargetModelDao() {
        return watch_SmartBand_StepTargetModelDao;
    }

    public Watch_W516_24HDataModelDao getWatch_W516_24HDataModelDao() {
        return watch_W516_24HDataModelDao;
    }

    public Watch_W516_AlarmModelDao getWatch_W516_AlarmModelDao() {
        return watch_W516_AlarmModelDao;
    }

    public Watch_W516_NotifyModelDao getWatch_W516_NotifyModelDao() {
        return watch_W516_NotifyModelDao;
    }

    public Watch_W516_SedentaryModelDao getWatch_W516_SedentaryModelDao() {
        return watch_W516_SedentaryModelDao;
    }

    public Watch_W516_SettingModelDao getWatch_W516_SettingModelDao() {
        return watch_W516_SettingModelDao;
    }

    public Watch_W516_SleepAndNoDisturbModelDao getWatch_W516_SleepAndNoDisturbModelDao() {
        return watch_W516_SleepAndNoDisturbModelDao;
    }

    public Watch_W516_SleepDataModelDao getWatch_W516_SleepDataModelDao() {
        return watch_W516_SleepDataModelDao;
    }

    public Watch_W560_AlarmModelDao getWatch_W560_AlarmModelDao() {
        return watch_W560_AlarmModelDao;
    }

}
