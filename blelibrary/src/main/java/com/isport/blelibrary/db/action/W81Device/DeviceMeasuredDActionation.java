package com.isport.blelibrary.db.action.W81Device;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.w526.Device_TempTable;
import com.isport.blelibrary.db.table.w811w814.BloodPressureMode;
import com.isport.blelibrary.db.table.w811w814.OneceHrMode;
import com.isport.blelibrary.db.table.w811w814.OxygenMode;
import com.isport.blelibrary.gen.BloodPressureModeDao;
import com.isport.blelibrary.gen.Device_TempTableDao;
import com.isport.blelibrary.gen.OneceHrModeDao;
import com.isport.blelibrary.gen.OxygenModeDao;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import androidx.annotation.Nullable;

public class DeviceMeasuredDActionation {


    /**
     * 查询没有上传的体温数据
     *
     * @param deviceId
     * @param userid
     * @param noUpdateDefValue
     * @return
     */
    public List<Device_TempTable> findNoUpgradeTempValueData(@Nullable String deviceId, @Nullable String userid, @Nullable String noUpdateDefValue) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid) || TextUtils.isEmpty(noUpdateDefValue)) {
            return null;
        }
        QueryBuilder<Device_TempTable> queryBuilder = BleAction.getDaoSession().queryBuilder(Device_TempTable.class);
        queryBuilder.where(Device_TempTableDao.Properties.DeviceId.eq(deviceId), Device_TempTableDao.Properties.UserId.eq(userid), Device_TempTableDao.Properties.WristbandTemperatureId.eq(noUpdateDefValue)).orderDesc(Device_TempTableDao.Properties.Timestamp).distinct();
        return queryBuilder.list();
    }

    /**
     * 同步服务器的体温数据
     *
     * @param deviceId
     * @param userId
     * @param wridId
     */
    public void updateTempWridId(String deviceId, String userId, String wridId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(wridId)) {
            return;
        }
        QueryBuilder<Device_TempTable> queryBuilder = BleAction.getDaoSession().queryBuilder(Device_TempTable.class);
        queryBuilder.where(Device_TempTableDao.Properties.DeviceId.eq(deviceId), Device_TempTableDao.Properties.UserId.eq(userId), Device_TempTableDao.Properties.WristbandTemperatureId.eq(wridId)).orderDesc(Device_TempTableDao.Properties.Timestamp).distinct();
        List<Device_TempTable> list = queryBuilder.list();
        Device_TempTable tempTable;
        Logger.myLog("updateBloodPressureWridId" + list);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                tempTable = list.get(i);
                Device_TempTableDao oxygenModeDao = BleAction.getsDeviceTempTableDao();
                tempTable.setWristbandTemperatureId("1");
                oxygenModeDao.update(tempTable);
            }
        }
    }

    public Device_TempTable findTempMode(@Nullable String deviceId, @Nullable String userid, long timestamp) {


        //Logger.myLog("findTempMode: deviceId:" + deviceId + ",userId:" + userid);

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid)) {
            return null;
        }
        QueryBuilder<Device_TempTable> queryBuilder = BleAction.getDaoSession().queryBuilder(Device_TempTable.class);
        if (timestamp == 0) {
            queryBuilder.where(Device_TempTableDao.Properties.DeviceId.eq(deviceId), Device_TempTableDao.Properties.UserId.eq(userid)).orderDesc(Device_TempTableDao.Properties.Timestamp).distinct().offset(0).limit(1);

        } else {
            queryBuilder.where(Device_TempTableDao.Properties.DeviceId.eq(deviceId), Device_TempTableDao.Properties.UserId.eq(userid), Device_TempTableDao.Properties.Timestamp.eq(timestamp));

        }

        List<Device_TempTable> list = queryBuilder.list();
        if (list.size() > 0) {
            Device_TempTable model = list.get(0);
            return model;
        } else {
            return null;
        }
    }

    public List<Device_TempTable> findNumberTempMode(@Nullable String deviceId, @Nullable String userid, int number) {


        //Logger.myLog("findTempMode: deviceId:" + deviceId + ",userId:" + userid);

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid)) {
            return null;
        }
        QueryBuilder<Device_TempTable> queryBuilder = BleAction.getDaoSession().queryBuilder(Device_TempTable.class);
        queryBuilder.where(Device_TempTableDao.Properties.DeviceId.eq(deviceId), Device_TempTableDao.Properties.UserId.eq(userid)).orderDesc(Device_TempTableDao.Properties.Timestamp).distinct().offset(0).limit(number);


        List<Device_TempTable> list = queryBuilder.list();
        return list;
    }


    public long saveOrUpdateTempData(String devcieId, String userId, String centigrade, String fahrenheit, long timestamp, String reportid) {
        if (TextUtils.isEmpty(devcieId) || TextUtils.isEmpty(userId)) {
            return 0;
        }

        long id;
        Device_TempTable oxyenMode = findTempMode(devcieId, userId, timestamp);
        if (oxyenMode == null) {
            oxyenMode = new Device_TempTable();
        } else {
            /*if (oxyenMode.getBloodOxygen() == bloodOxygen && oxyenMode.getTimestamp() == timestamp && oxyenMode.getWristbandBloodOxygenId().equals(reportid)) {
                Logger.myLog("saveOrUpdateOxygenData:已有相同的数据存在，不需要再去存储");
                return 0;
            }*/
            return 0;
        }
        Device_TempTableDao oxygenModeDao = BleAction.getsDeviceTempTableDao();
        oxyenMode.setUserId(userId);
        oxyenMode.setDeviceId(devcieId);
        oxyenMode.setWristbandTemperatureId(reportid);
        oxyenMode.setCentigrade(centigrade);
        oxyenMode.setFahrenheit(fahrenheit);
        oxyenMode.setStrDate(TimeUtils.getTimeByMMDDHHMMSS(timestamp));
        oxyenMode.setTimestamp(timestamp);

        Logger.myLog("saveOrUpdateTempData" + oxygenModeDao + "oxyenMode:" + oxyenMode);
        if (oxygenModeDao == null) {
            return -1;
        }
        if (oxyenMode == null) {
            return -1;
        }
        id = oxygenModeDao.insertOrReplace(oxyenMode);


        Logger.myLog("saveOrUpdateOxygenData:" + oxyenMode);
        return id;
    }

    /**
     * 单次心率历史数据未上传数据集合
     */

    public List<OneceHrMode> findNoUpgradeOneceHrModeData(String deviceId, String userid, String noUpdateDefValue) {

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid) || TextUtils.isEmpty(noUpdateDefValue)) {
            return null;
        }

        QueryBuilder<OneceHrMode> queryBuilder = BleAction.getDaoSession().queryBuilder(OneceHrMode.class);
        queryBuilder.where(OneceHrModeDao.Properties.DeviceId.eq(deviceId), OneceHrModeDao.Properties.UserId.eq(userid), OneceHrModeDao.Properties.WristbandBloodOxygenId.eq(noUpdateDefValue)).orderDesc(OneceHrModeDao.Properties.Timestamp).distinct();
        return queryBuilder.list();
    }


    public void updateOneceHrWridId(String deviceId, String userId, String wridId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(wridId)) {
            return;
        }
        QueryBuilder<OneceHrMode> queryBuilder = BleAction.getDaoSession().queryBuilder(OneceHrMode.class);
        queryBuilder.where(OneceHrModeDao.Properties.DeviceId.eq(deviceId), OneceHrModeDao.Properties.UserId.eq(userId), OneceHrModeDao.Properties.WristbandBloodOxygenId.eq(wridId)).orderDesc(OneceHrModeDao.Properties.Timestamp).distinct();
        List<OneceHrMode> list = queryBuilder.list();
        OneceHrMode bloodPressureMode;
        Logger.myLog("updateBloodPressureWridId" + list);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                bloodPressureMode = list.get(i);
                OneceHrModeDao oxygenModeDao = BleAction.getsOneceHrModeDao();
                bloodPressureMode.setWristbandBloodOxygenId("1");
                oxygenModeDao.update(bloodPressureMode);
            }
        }
    }

    /**
     * 保存单次心率数据
     *
     * @param devcieId
     * @param userId
     * @param bloodOxygen
     * @param timestamp
     * @param reportid
     * @return
     */
    public long saveOrUpdateOnceHrData(String devcieId, String userId, int bloodOxygen, long timestamp, String reportid) {

        if (TextUtils.isEmpty(devcieId) || TextUtils.isEmpty(userId)) {
            return 0;
        }

        long id;
        OneceHrMode oxyenMode = findOnecHrMode(devcieId, userId, timestamp);
        if (oxyenMode == null) {
            oxyenMode = new OneceHrMode();
        } else {
            if (oxyenMode.getBloodOxygen() == bloodOxygen && oxyenMode.getTimestamp() == timestamp && oxyenMode.getWristbandBloodOxygenId().equals(reportid)) {
                Logger.myLog("saveOrUpdateOxygenData:已有相同的数据存在，不需要再去存储");
                return 0;
            }
        }
        OneceHrModeDao oxygenModeDao = BleAction.getsOneceHrModeDao();
        oxyenMode.setUserId(userId);
        oxyenMode.setDeviceId(devcieId);
        oxyenMode.setBloodOxygen(bloodOxygen);
        oxyenMode.setWristbandBloodOxygenId(reportid);
        oxyenMode.setTimestamp(timestamp);
        oxyenMode.setStrTimes(TimeUtils.getTimeByMMDDHHMMSS(timestamp));

        Logger.myLog("oxygenModeDao" + oxygenModeDao + "oxyenMode:" + oxyenMode);
        if (oxygenModeDao == null) {
            return -1;
        }
        if (oxyenMode == null) {
            return -1;
        }
        id = oxygenModeDao.insertOrReplace(oxyenMode);


        Logger.myLog("saveOrUpdateOxygenData:" + oxyenMode);
        return id;
    }


    /**
     * 查找指定时间戳的血氧数据
     *
     * @param deviceId
     * @param userid
     * @param timestamp 0查询最新的数据
     * @return
     */
    public OneceHrMode findOnecHrMode(String deviceId, String userid, long timestamp) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<OneceHrMode> queryBuilder = BleAction.getDaoSession().queryBuilder(OneceHrMode.class);
        if (timestamp == 0) {
            queryBuilder.where(OneceHrModeDao.Properties.DeviceId.eq(deviceId), OneceHrModeDao.Properties.UserId.eq(userid)).orderDesc(OneceHrModeDao.Properties.Timestamp).distinct().offset(0).limit(1);

        } else {
            queryBuilder.where(OneceHrModeDao.Properties.DeviceId.eq(deviceId), OneceHrModeDao.Properties.UserId.eq(userid), OneceHrModeDao.Properties.Timestamp.eq(timestamp));

        }

        List<OneceHrMode> list = queryBuilder.list();
        Log.e("DDD","----list="+list.size());

        if (list.size() > 0) {
            Log.e("DDD","-----血氧="+new Gson().toJson(list));
            OneceHrMode oxygenMode = list.get(0);
            return oxygenMode;
        } else {
            return null;
        }
    }


    /**
     * 血压数据查找未上传的数据集
     *
     * @param deviceId
     * @param userid
     * @param noUpdateDefValue
     * @return
     */
    public List<BloodPressureMode> findNoUpgradeBloodPressureData(String deviceId, String userid, String noUpdateDefValue) {

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid) || TextUtils.isEmpty(noUpdateDefValue)) {
            return null;
        }

        QueryBuilder<BloodPressureMode> queryBuilder = BleAction.getDaoSession().queryBuilder(BloodPressureMode.class);
        queryBuilder.where(BloodPressureModeDao.Properties.DeviceId.eq(deviceId), BloodPressureModeDao.Properties.UserId.eq(userid), BloodPressureModeDao.Properties.WristbandBloodPressureId.eq(noUpdateDefValue)).orderDesc(BloodPressureModeDao.Properties.Timestamp).distinct();
        return queryBuilder.list();
    }


    public void updateBloodPressureWridId(String deviceId, String userId, String wridId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(wridId)) {
            return;
        }
        QueryBuilder<BloodPressureMode> queryBuilder = BleAction.getDaoSession().queryBuilder(BloodPressureMode.class);
        queryBuilder.where(BloodPressureModeDao.Properties.DeviceId.eq(deviceId), BloodPressureModeDao.Properties.UserId.eq(userId), BloodPressureModeDao.Properties.WristbandBloodPressureId.eq(wridId)).orderDesc(BloodPressureModeDao.Properties.Timestamp).distinct();
        List<BloodPressureMode> list = queryBuilder.list();
        BloodPressureMode bloodPressureMode;
        Logger.myLog("updateBloodPressureWridId" + list);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                bloodPressureMode = list.get(i);
                BloodPressureModeDao oxygenModeDao = BleAction.getsBloodPressureModeDao();
                bloodPressureMode.setWristbandBloodPressureId("1");
                oxygenModeDao.update(bloodPressureMode);
            }
        }
    }

    /**
     * [
     * {
     * "boValue": "string",
     * "createTime": "2019-11-20T07:25:54.695Z",
     * "deviceId": "string",
     * "extend1": "string",
     * "extend2": "string",
     * "extend3": "string",
     * "timestamp": "2019-11-20T07:25:54.695Z",
     * "userId": 0,
     * "wristbandBloodOxygenId": 0
     * }
     * ]
     *
     * @param deviceId
     * @param userId
     * @param wridId
     */

    public void updateOxyGenWristbandId(String deviceId, String userId, String wridId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(wridId)) {
            return;
        }
        QueryBuilder<OxygenMode> queryBuilder = BleAction.getDaoSession().queryBuilder(OxygenMode.class);
        queryBuilder.where(OxygenModeDao.Properties.DeviceId.eq(deviceId), OxygenModeDao.Properties.UserId.eq(userId), OxygenModeDao.Properties.WristbandBloodOxygenId.eq(wridId)).orderDesc(OxygenModeDao.Properties.Timestamp).distinct();
        List<OxygenMode> list = queryBuilder.list();
        OxygenMode oxygenMode;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                OxygenModeDao oxygenModeDao = BleAction.getsOxygenModeDao();
                oxygenMode = list.get(i);
                oxygenMode.setWristbandBloodOxygenId("1");
                oxygenModeDao.update(oxygenMode);
            }
        }
    }

    /**
     * 查找最后一条血压数据
     */


    /**
     * 查找指定时间戳的血压数据
     */
    public BloodPressureMode findBloodPressureMode(String deviceId, String userid, long timestamp) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid)) {
            return null;
        }
        QueryBuilder<BloodPressureMode> queryBuilder = BleAction.getDaoSession().queryBuilder(BloodPressureMode.class);
        if (timestamp == 0) {
            queryBuilder.where(BloodPressureModeDao.Properties.DeviceId.eq(deviceId), BloodPressureModeDao.Properties.UserId.eq(userid)).orderDesc(BloodPressureModeDao.Properties.Timestamp).offset(0).limit(1);

        } else {
            queryBuilder.where(BloodPressureModeDao.Properties.DeviceId.eq(deviceId), BloodPressureModeDao.Properties.UserId.eq(userid), BloodPressureModeDao.Properties.Timestamp.eq(timestamp)).distinct();

        }

        List<BloodPressureMode> list = queryBuilder.list();
        if (list != null && list.size() > 0) {
            BloodPressureMode oxygenMode = list.get(0);
            return oxygenMode;
        } else {
            return null;
        }
    }

    /**
     * 查找指定时间戳的血压数据
     */
    public BloodPressureMode findBloodPressureMode(String deviceId, String userid, long timestamp, int count) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid)) {
            return null;
        }
        QueryBuilder<BloodPressureMode> queryBuilder = BleAction.getDaoSession().queryBuilder(BloodPressureMode.class);
        if (timestamp == 0) {
            queryBuilder.where(BloodPressureModeDao.Properties.DeviceId.eq(deviceId), BloodPressureModeDao.Properties.UserId.eq(userid)).orderDesc(BloodPressureModeDao.Properties.Timestamp).offset(0).limit(count);

        } else {
            queryBuilder.where(BloodPressureModeDao.Properties.DeviceId.eq(deviceId), BloodPressureModeDao.Properties.UserId.eq(userid), BloodPressureModeDao.Properties.Timestamp.eq(timestamp));

        }

        List<BloodPressureMode> list = queryBuilder.list();
        if (list != null && list.size() > 0) {
            BloodPressureMode oxygenMode = list.get(0);
            return oxygenMode;
        } else {
            return null;
        }
    }

    /**
     * 保存血压数据
     *
     * @param deviceId
     * @return
     */
    public long saveOrUpdateBloodPressureData(final String deviceId, final String userId, final int sbp, final int dbp, final long timestamp, final String reportid) {
        if (TextUtils.isEmpty(deviceId)) {
            return 0;
        }
        long id;

        BloodPressureMode bloodPressureMode = findBloodPressureMode(deviceId, userId, timestamp);
        if (bloodPressureMode == null) {
            bloodPressureMode = new BloodPressureMode();
        } else {


            if (bloodPressureMode.getDiastolicBloodPressure() == dbp && bloodPressureMode.getSystolicBloodPressure() == sbp && bloodPressureMode.getTimestamp() == timestamp && bloodPressureMode.getWristbandBloodPressureId().equals(reportid)) {
                Logger.myLog("saveOrUpdateBloodPressureData:已有相同的数据存在，不需要再去存储");
                return 0;
            }

        }
        BloodPressureModeDao bloodPressureModeDao = BleAction.getsBloodPressureModeDao();
        bloodPressureMode.setUserId(userId);
        bloodPressureMode.setDeviceId(deviceId);
        bloodPressureMode.setTimestamp(timestamp);
        bloodPressureMode.setSystolicBloodPressure(sbp);
        bloodPressureMode.setDiastolicBloodPressure(dbp);
        bloodPressureMode.setWristbandBloodPressureId(reportid);
        bloodPressureMode.setStrTimes(TimeUtils.getTimeByMMDDHHMMSS(timestamp));
        id = bloodPressureModeDao.insertOrReplace(bloodPressureMode);
        Logger.myLog("saveOrUpdateBloodPressureData:" + bloodPressureMode);
        return id;
    }


    /**
     * 血氧数据查找未上传的数据集
     *
     * @param deviceId
     * @param userid
     * @param noUpdateDefValue
     * @return
     */
    public List<OxygenMode> findNoUpgradeOxygenData(String deviceId, String userid, String noUpdateDefValue) {

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userid) || TextUtils.isEmpty(noUpdateDefValue)) {
            return null;
        }

        QueryBuilder<OxygenMode> queryBuilder = BleAction.getDaoSession().queryBuilder(OxygenMode.class);
        queryBuilder.where(OxygenModeDao.Properties.DeviceId.eq(deviceId), OxygenModeDao.Properties.UserId.eq(userid), OxygenModeDao.Properties.WristbandBloodOxygenId.eq(noUpdateDefValue)).orderDesc(OxygenModeDao.Properties.Timestamp).distinct();
        return queryBuilder.list();
    }


    /**
     * 查找指定时间戳的血氧数据
     *
     * @param deviceId
     * @param userid
     * @param timestamp 0查询最新的数据
     * @return
     */
    public static OxygenMode findOxyenMode(String deviceId, String userid, long timestamp) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<OxygenMode> queryBuilder = BleAction.getDaoSession().queryBuilder(OxygenMode.class);
        if (timestamp == 0) {
            queryBuilder.where(OxygenModeDao.Properties.DeviceId.eq(deviceId), OxygenModeDao.Properties.UserId.eq(userid)).orderDesc(OxygenModeDao.Properties.Timestamp).distinct().offset(0).limit(1);

        } else {
            queryBuilder.where(OxygenModeDao.Properties.DeviceId.eq(deviceId), OxygenModeDao.Properties.UserId.eq(userid), OxygenModeDao.Properties.Timestamp.eq(timestamp));

        }

        List<OxygenMode> list = queryBuilder.list();
        if (list.size() > 0) {
            OxygenMode oxygenMode = list.get(0);
            return oxygenMode;
        } else {
            return null;
        }
    }

    /**
     * 保存血氧数据
     *
     * @param devcieId
     * @param userId
     * @param bloodOxygen
     * @param timestamp
     * @param reportid
     * @return
     */
    public static long saveOrUpdateOxygenData(String devcieId, String userId, int bloodOxygen, long timestamp, String reportid) {

        if (TextUtils.isEmpty(devcieId)) {
            return 0;
        }

        long id;
        OxygenMode oxyenMode = findOxyenMode(devcieId, userId, timestamp);
        if (oxyenMode == null) {
            oxyenMode = new OxygenMode();
        } else {
            if (oxyenMode.getBloodOxygen() == bloodOxygen && oxyenMode.getTimestamp() == timestamp && oxyenMode.getWristbandBloodOxygenId().equals(reportid)) {
                Logger.myLog("saveOrUpdateOxygenData:已有相同的数据存在，不需要再去存储");
                return 0;
            }
        }
        OxygenModeDao oxygenModeDao = BleAction.getsOxygenModeDao();
        oxyenMode.setUserId(userId);
        oxyenMode.setDeviceId(devcieId);
        oxyenMode.setBloodOxygen(bloodOxygen);
        oxyenMode.setWristbandBloodOxygenId(reportid);
        oxyenMode.setTimestamp(timestamp);
        oxyenMode.setStrTimes(TimeUtils.getTimeByMMDDHHMMSS(timestamp));
        id = oxygenModeDao.insertOrReplace(oxyenMode);


        Logger.myLog("saveOrUpdateOxygenData:" + oxyenMode);
        return id;
    }


}
