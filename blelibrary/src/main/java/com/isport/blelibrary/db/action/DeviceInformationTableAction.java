package com.isport.blelibrary.db.action;

import android.text.TextUtils;

import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.gen.DeviceInformationTableDao;
import com.isport.blelibrary.utils.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * @Author
 * @Date 2019/1/7
 * @Fuction
 */

public class DeviceInformationTableAction {

    private static final String TAG = "DeviceInformationTableA";

    public static List<DeviceInformationTable> getAll() {
        DeviceInformationTableDao sDeviceInformationTableDao = BleAction.getDeviceInformationTableDao();
        List<DeviceInformationTable> messages = sDeviceInformationTableDao.loadAll();
        if (messages.size() > 0) {
            return messages;
        } else {
            return null;
        }
    }

    /**
     * 设备是否已经存储过了
     *
     * @param deviceId
     * @return
     */
    public static boolean hasStoreDeviceInfo(String deviceId) {
        QueryBuilder<DeviceInformationTable> queryBuilder = BleAction.getDaoSession().queryBuilder(DeviceInformationTable.class);
        queryBuilder.where(DeviceInformationTableDao.Properties.DeviceId.eq(deviceId));
        if (queryBuilder.list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询指定deviceId的设备信息
     *
     * @param deviceId
     * @return
     */
    public static DeviceInformationTable findDeviceInfoByDeviceId(String deviceId) {


        Logger.myLog("findDeviceInfoByDeviceId" + deviceId);
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<DeviceInformationTable> queryBuilder = BleAction.getDaoSession().queryBuilder(DeviceInformationTable.class);
        List<DeviceInformationTable> list = queryBuilder.list();
        queryBuilder.where(DeviceInformationTableDao.Properties.DeviceId.eq(deviceId));
        list = queryBuilder.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
