package com.isport.blelibrary.db.action.DeviceSetting;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.w526.Device_BacklightTimeAndScreenLuminanceModel;
import com.isport.blelibrary.gen.Device_BacklightTimeAndScreenLuminanceModelDao;
import com.isport.blelibrary.utils.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DeviceSettingAction {
    /**
     * 查询当前用户和当前设备的背光时长和屏幕亮度等级
     *
     * @param deviceId
     * @param userId
     * @return
     */

    public Device_BacklightTimeAndScreenLuminanceModel findBacklighttimeAndScreenLeve(@NonNull String deviceId, @NonNull String userId) {

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }

        QueryBuilder<Device_BacklightTimeAndScreenLuminanceModel> queryBuilder = BleAction.getDaoSession().queryBuilder(Device_BacklightTimeAndScreenLuminanceModel.class);
        queryBuilder.where(Device_BacklightTimeAndScreenLuminanceModelDao.Properties.DeviceId.eq(deviceId), Device_BacklightTimeAndScreenLuminanceModelDao.Properties.UserId.eq(userId)).distinct().limit(1).offset(0);
        List<Device_BacklightTimeAndScreenLuminanceModel> list = queryBuilder.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }


    public void saveOrUpdateScreenLeve(@NonNull String deviceId, @NonNull String userId, int screenLeve) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return;
        }
        Device_BacklightTimeAndScreenLuminanceModel model = findBacklighttimeAndScreenLeve(deviceId, userId);

        if (model != null) {
            model.setValuseScreenLeve(screenLeve);

        } else {
            model = new Device_BacklightTimeAndScreenLuminanceModel();
            model.setDeviceId(deviceId);
            model.setUserId(userId);
            model.setValuseScreenLeve(screenLeve);
            model.setValuseBacklightTime(3);
        }
        saveBean(model);
    }

    private synchronized long saveBean(Device_BacklightTimeAndScreenLuminanceModel deviceDetailData) {
        Device_BacklightTimeAndScreenLuminanceModelDao screenLuminanceModelDao = BleAction.getScreenLuminanceModelDao();
        if (screenLuminanceModelDao == null) {
            return 0l;
        }
        long id = screenLuminanceModelDao.insertOrReplace(deviceDetailData);
       // Logger.myLog("saveW81DeviceDetailData：" + deviceDetailData + " save id:" + id);
        return id;


    }

    public void saveOrUpdateBacklightTime(@NonNull String deviceId, @NonNull String userId, int backLightTime) {


        Logger.myLog("saveOrUpdateBacklightTime" + backLightTime + "deviceId:" + deviceId + "userId:" + userId);


        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return;
        }
        Device_BacklightTimeAndScreenLuminanceModel model = findBacklighttimeAndScreenLeve(deviceId, userId);

        if (model != null) {
            model.setValuseBacklightTime(backLightTime);

        } else {
            model = new Device_BacklightTimeAndScreenLuminanceModel();
            model.setDeviceId(deviceId);
            model.setUserId(userId);
            model.setValuseScreenLeve(1);
            model.setValuseBacklightTime(backLightTime);
        }
        saveBean(model);
    }


    public void saveOrUpdateBacklightTimeAndScreenLeve(@NonNull String deviceId, @NonNull String userId, int backLightTime, int screenLeve) {

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return;
        }
        Device_BacklightTimeAndScreenLuminanceModel model = findBacklighttimeAndScreenLeve(deviceId, userId);

        if (model != null) {
            model.setValuseBacklightTime(backLightTime);
            model.setValuseScreenLeve(screenLeve);

        } else {
            model = new Device_BacklightTimeAndScreenLuminanceModel();
            model.setDeviceId(deviceId);
            model.setUserId(userId);
            model.setValuseScreenLeve(screenLeve);
            model.setValuseBacklightTime(backLightTime);
        }
        saveBean(model);
    }
}
