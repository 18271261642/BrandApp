package com.isport.blelibrary.db.action.s002;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.s002.S002_Detail_Data;
import com.isport.blelibrary.gen.S002_Detail_DataDao;
import com.isport.blelibrary.utils.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class S002_DetailDataModelAction {


    public static void deletAll() {
        S002_Detail_DataDao oxygenModeDao = BleAction.getS002DetailDataDao();
        oxygenModeDao.deleteAll();
    }

    public void updateAll(@Nullable String deviceId, @Nullable String userid) {
        List<S002_Detail_Data> list = findNoUpgradeRopeData(deviceId, userid, 0);
        if (list != null && list.size() > 0) {
            S002_Detail_DataDao oxygenModeDao = BleAction.getS002DetailDataDao();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setUpgradeState(1);
            }
            oxygenModeDao.updateInTx();
        }
    }

    public List<S002_Detail_Data> findNoUpgradeRopeData(@Nullable String deviceId, @Nullable String userid, @Nullable int noUpdateDefValue) {
        Logger.myLog("findNoUpgradeRopeData,deviceId=" + deviceId + "userid=" + userid);
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid)) {
            return null;
        }
        QueryBuilder<S002_Detail_Data> queryBuilder = BleAction.getDaoSession().queryBuilder(S002_Detail_Data.class);
        queryBuilder.where(S002_Detail_DataDao.Properties.DeviceId.eq(deviceId), S002_Detail_DataDao.Properties.UserId.eq(userid), S002_Detail_DataDao.Properties.UpgradeState.eq(noUpdateDefValue)).orderDesc(S002_Detail_DataDao.Properties.Timestamp).distinct();
        return queryBuilder.list();

    }


    public static S002_Detail_Data findTempMode(@Nullable String deviceId, @Nullable String userid, long timestamp) {


       // Logger.myLog("findTempMode: deviceId:" + deviceId + ",userId:" + userid);

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid)) {
            return null;
        }
        QueryBuilder<S002_Detail_Data> queryBuilder = BleAction.getDaoSession().queryBuilder(S002_Detail_Data.class);
        if (timestamp == 0) {
            queryBuilder.where(S002_Detail_DataDao.Properties.DeviceId.eq(deviceId), S002_Detail_DataDao.Properties.UserId.eq(userid)).orderDesc(S002_Detail_DataDao.Properties.Timestamp).distinct().offset(0).limit(1);
        } else {
            queryBuilder.where(S002_Detail_DataDao.Properties.DeviceId.eq(deviceId), S002_Detail_DataDao.Properties.UserId.eq(userid), S002_Detail_DataDao.Properties.Timestamp.eq(timestamp));

        }

        List<S002_Detail_Data> list = queryBuilder.list();
        if (list.size() > 0) {
            S002_Detail_Data model = list.get(0);
            return model;
        } else {
            return null;
        }
    }

    public static long saveOrUpdateRopeData(S002_Detail_Data data) {
        if (data == null || TextUtils.isEmpty(data.getDeviceId()) || TextUtils.isEmpty(data.getUserId())) {
            return 0;
        }

        //2020-10-10 11:40:09
        try {

            String[] startTime = data.getStartTime().split(" ");
            if (startTime.length == 2) {
                String[] times = startTime[0].split("-");
                if (Integer.parseInt(times[0]) < 2020 || Integer.parseInt(times[1]) < 0 || Integer.parseInt(times[1]) > 12 || Integer.parseInt(times[2]) < 0 || Integer.parseInt(times[2]) > 31) {
                    return 0;
                }
            }

            long id;
            S002_Detail_Data bean = findTempMode(data.getDeviceId(), data.getUserId(), data.getTimestamp());
            if (bean == null) {
                bean = new S002_Detail_Data();
            } else {
            /*if (oxyenMode.getBloodOxygen() == bloodOxygen && oxyenMode.getTimestamp() == timestamp && oxyenMode.getWristbandBloodOxygenId().equals(reportid)) {
                Logger.myLog("saveOrUpdateOxygenData:已有相同的数据存在，不需要再去存储");
                return 0;
            }*/
                return 0;
            }
            S002_Detail_DataDao oxygenModeDao = BleAction.getS002DetailDataDao();
            /**
             *  int averageFrequency;
             *     int averageHeartRate;
             *     String deviceId;
             *     String endTime;
             *     int exerciseType;
             *     int maxFrequency;
             *     int singleMaxSkippingNum;
             *     int skippingDuration;
             *     int skippingNum;
             *     int stumbleNum;
             *     int totalCalories;
             *     String frequencyArray;
             *     String heartRateDetailArray;
             *     String startTime;
             *     String stumbleArray;
             *     String userId;
             *     long timestamp;
             *     int upgradeState;
             */
            bean.setAverageFrequency(data.getAverageFrequency());
            bean.setAverageHeartRate(data.getAverageHeartRate());
            bean.setUserId(data.getUserId());
            bean.setDeviceId(data.getDeviceId());


            bean.setEndTime(data.getEndTime());
            bean.setStartTime(data.getStartTime());
            bean.setTimestamp(data.getTimestamp());
            bean.setExerciseType(data.getExerciseType());
            bean.setMaxFrequency(data.getMaxFrequency());
            bean.setSingleMaxSkippingNum(data.getSingleMaxSkippingNum());
            bean.setHeartRateDetailArray(data.getHeartRateDetailArray());
            bean.setFrequencyArray(data.getFrequencyArray());
            bean.setSkippingDuration(data.getSkippingDuration());
            bean.setStumbleArray(data.getStumbleArray());
            bean.setStumbleNum(data.getStumbleNum());
            bean.setUpgradeState(data.getUpgradeState());
            bean.setTotalCalories(data.getTotalCalories());
            bean.setSkippingNum(data.getSkippingNum());

            Logger.myLog("saveOrUpdateTempData" + oxygenModeDao + "oxyenMode:" + bean);
            if (oxygenModeDao == null) {
                return -1;
            }
            if (bean == null) {
                return -1;
            }
            id = oxygenModeDao.insertOrReplace(bean);
            Logger.myLog("saveOrUpdateOxygenData:" + bean);
            return id;

        } catch (Exception e) {
            return 0;
        }
    }
}
