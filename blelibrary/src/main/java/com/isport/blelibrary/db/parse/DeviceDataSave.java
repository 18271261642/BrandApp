package com.isport.blelibrary.db.parse;

import com.isport.blelibrary.db.action.W81Device.DeviceMeasuredDActionation;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.ThreadPoolUtils;

public class DeviceDataSave {

    /**
     * 存储W516睡眠设置
     */
    public static void saveOxyenModelData(final String devcieId, final String userId, final int bloodOxygen, final long timestamp, final String reportid) {

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                DeviceMeasuredDActionation.saveOrUpdateOxygenData(devcieId, userId, bloodOxygen, timestamp, reportid);
            }
        });


    }

    /**
     * 存储W516睡眠设置
     */
    public static void saveOneceHrData(final String devcieId, final String userId, final int hrValue, final long timestamp, final String reportid) {

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                DeviceMeasuredDActionation measuredDActionation = new DeviceMeasuredDActionation();
                measuredDActionation.saveOrUpdateOnceHrData(devcieId, userId, hrValue, timestamp, reportid);
            }
        });


    }

    public static void saveBloodPressureData(final String devcieId, final String userId, final int sbp, final int dbp, final long timestamp, final String reportid) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                DeviceMeasuredDActionation actionation = new DeviceMeasuredDActionation();
                actionation.saveOrUpdateBloodPressureData(devcieId, userId, sbp, dbp, timestamp, reportid);
            }
        });

    }

    public static void saveTempData(final String devcieId, final String userId, int tempValue, final long timestamp, final String reportid) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                float ftempValueC = tempValue / 10.f;

                DeviceMeasuredDActionation actionation = new DeviceMeasuredDActionation();
                String tempValueC = CommonDateUtil.formatOnePoint(ftempValueC);
                String tempValueF = CommonDateUtil.formatOnePoint(CommonDateUtil.ctof(ftempValueC));
                actionation.saveOrUpdateTempData(devcieId, userId, tempValueC, tempValueF, timestamp, reportid);
            }
        });

    }

    public static void saveTempData(final String devcieId, final String userId, float tempValue, final long timestamp, final String reportid) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                float ftempValueC = tempValue / 10.f;

                DeviceMeasuredDActionation actionation = new DeviceMeasuredDActionation();
                String tempValueC = CommonDateUtil.formatOnePoint(ftempValueC);
                String tempValueF = CommonDateUtil.formatOnePoint(CommonDateUtil.ctof(ftempValueC));
                actionation.saveOrUpdateTempData(devcieId, userId, tempValueC, tempValueF, timestamp, reportid);
            }
        });

    }
}
