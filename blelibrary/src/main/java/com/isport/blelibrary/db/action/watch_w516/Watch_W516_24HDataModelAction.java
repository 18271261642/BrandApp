package com.isport.blelibrary.db.action.watch_w516;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.isport.blelibrary.db.CommonInterFace.WatchData;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @创建者 bear
 * @创建时间 2019/3/5 15:54
 * @描述
 */
public class Watch_W516_24HDataModelAction {

    private static final String TAG = "Watch_W516_24HDataModelAction";


    /**
     * 查询绑定时间之后的数据,之前的数据舍弃
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp1(String userId, long
            timeTamp, String deviceId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.ReportId.eq(0), Watch_W516_24HDataModelDao
                .Properties.Timestamp.ge(timeTamp)).orderDesc
                (Watch_W516_24HDataModelDao.Properties.Timestamp);
//        .distinct()
        try {
            List<Watch_W516_24HDataModel> list = queryBuilder.list();
            if (list.size() > 0) {
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 除去那些不满1440的天
     *
     * @param userId
     * @param timeTamp
     * @param deviceId
     * @return
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp(String userId, long
            timeTamp, String deviceId, boolean isUpdateCurrentDay) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.ReportId.eq(0)).orderDesc
                (Watch_W516_24HDataModelDao.Properties.Timestamp).distinct();
//        .distinct()
        List<Watch_W516_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            List<Watch_W516_24HDataModel> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                Watch_W516_24HDataModel watch_w516_24HDataModel = list.get(i);
                Gson gson = new Gson();
                int[] stepArray = new Gson().fromJson(watch_w516_24HDataModel.getStepArray(), int[].class);
                int[] sleepArray = new Gson().fromJson(watch_w516_24HDataModel.getSleepArray(), int[].class);
                int[] hrArray = new Gson().fromJson(watch_w516_24HDataModel.getHrArray(), int[].class);
                Logger.myLog("同步成功 stepArray.length:" + stepArray.length);
                //如果是解绑后同步需要把当天的数据封装完整上传给服务器
                if (isUpdateCurrentDay) {
                    if (stepArray.length == 1440 && sleepArray.length == 1440 && hrArray.length == 1440) {
                        result.add(watch_w516_24HDataModel);
                    } else {
                        //需要把当天的数据填充
                        ArrayList<Integer> stepList = new ArrayList<>();
                        ArrayList<Integer> sleepList = new ArrayList<>();
                        ArrayList<Integer> hrList = new ArrayList<>();
                        long sumStep = 0;
                        for (int j = 0; j < stepArray.length; j++) {
                            stepList.add(stepArray[j]);
                            sleepList.add(sleepArray[j]);
                            hrList.add(hrArray[j]);
                            sumStep += stepArray[j];
                        }
                        for (int j = stepArray.length; j < 1440; j++) {
                            if (sumStep < watch_w516_24HDataModel.getTotalSteps() && j == stepArray.length) {
                                stepList.add((int) Math.abs(watch_w516_24HDataModel.getTotalSteps() - sumStep));
                            } else {
                                stepList.add(0);
                            }
                            sleepList.add(0);
                            hrList.add(0);
                        }

                        watch_w516_24HDataModel.setStepArray(gson.toJson(stepList));
                        watch_w516_24HDataModel.setSleepArray(gson.toJson(sleepList));
                        watch_w516_24HDataModel.setHrArray(gson.toJson(hrList));

                        result.add(watch_w516_24HDataModel);

                        Logger.myLog("currentDayupgradedata:" + watch_w516_24HDataModel.toString());


                    }
                } else {
                    if (stepArray.length == 1440 && sleepArray.length == 1440 && hrArray.length == 1440) {
                        result.add(watch_w516_24HDataModel);
                    }
                }
            }
            if (result.size() > 0) {
                return result;
            }
            return null;
        } else {
            return null;
        }
    }

    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByTimeTamp(@Nullable String userId, long
            timeTamp) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.ReportId.eq(0), Watch_W516_24HDataModelDao
                .Properties.Timestamp.ge(timeTamp)).orderDesc
                (Watch_W516_24HDataModelDao.Properties.Timestamp);
//        .distinct()
        List<Watch_W516_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            List<Watch_W516_24HDataModel> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Watch_W516_24HDataModel watch_w516_24HDataModel = list.get(i);
                Gson gson = new Gson();
                int[] stepArray = new Gson().fromJson(watch_w516_24HDataModel.getStepArray(), int[].class);
                int[] sleepArray = new Gson().fromJson(watch_w516_24HDataModel.getSleepArray(), int[].class);
                int[] hrArray = new Gson().fromJson(watch_w516_24HDataModel.getHrArray(), int[].class);
                if (stepArray.length == 1440 && sleepArray.length == 1440 && hrArray.length == 1440) {
                    result.add(watch_w516_24HDataModel);
                }
            }
            if (result.size() > 0) {
                return result;
            }
            return null;
        } else {
            return null;
        }
    }


    //删除当天当天

    public static void delCurretentDay(String userId, String dateStr, String deviceId) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId)) {
            return;
        }
        Watch_W516_24HDataModel watch_W516_24HDataModel = findWatch_W516_Watch_W516_24HDataModelByDeviceId(userId, dateStr, deviceId);
        if (watch_W516_24HDataModel != null) {
            Gson gson = new Gson();
            int[] stepArray = gson.fromJson(watch_W516_24HDataModel.getStepArray(), int[].class);
            int[] sleepArray = gson.fromJson(watch_W516_24HDataModel.getSleepArray(), int[].class);
            int[] hrArray = gson.fromJson(watch_W516_24HDataModel.getHrArray(), int[].class);
            //如果是解绑后同步需要把当天的数据封装完整上传给服务器
            if (stepArray.length < 1440 && sleepArray.length < 1440 && hrArray.length < 1440) {
                Watch_W516_24HDataModelDao deviceTypeTableDao = BleAction.getWatch_W516_24HDataModelDao();
                deviceTypeTableDao.delete(watch_W516_24HDataModel);
            }
        }

    }

    //获取当前用户某天的数据
    public static Watch_W516_24HDataModel findWatch_W516_Watch_W516_24HDataModelByDeviceId(String userId, String dateStr, String deviceId) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId),
                Watch_W516_24HDataModelDao.Properties.DateStr.eq(dateStr)).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr);
        if (queryBuilder.list().size() > 0) {
            Watch_W516_24HDataModel watch_W516_24HDataModel = queryBuilder.list().get(0);
            return watch_W516_24HDataModel;
        } else {
            return null;
        }
    }


    // 获取当前用户最新的一条数据
    public static Watch_W516_24HDataModel findWatch_W516_Watch_W516_24HDataModelByDeviceId(@NonNull String userId, @NonNull String deviceId) {
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId)).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr).limit(1).offset(0);
        List<Watch_W516_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            Watch_W516_24HDataModel watch_W516_24HDataModel = list.get(0);
            return watch_W516_24HDataModel;
        } else {
            return null;
        }
    }


    /**
     * 获取当天前数据不为0的天
     *
     * @param userId
     * @param deviceId
     * @return
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceId_Last_Day(@NonNull String userId, @NonNull String deviceId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.TotalSteps.gt(0), Watch_W516_24HDataModelDao.Properties.DateStr.lt(TimeUtils.getTodayYYYYMMDD())).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr).limit(1).offset(0);
        List<Watch_W516_24HDataModel> list = queryBuilder.list();
        return list;
    }

    // TODO: 2019/3/13 有心率数据的天
    // 获取当前用户最新的一条数据
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceId_Last_Two_HR(@NonNull String userId, @NonNull String deviceId) {

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }

        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.HasHR.eq(WatchData.HAS_HR)).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr).limit(1).offset(0);
        List<Watch_W516_24HDataModel> list = queryBuilder.list();
        return list;
    }

    /**
     * 查最新的两天数据
     *
     * @param userId
     * @return
     */
    public static Watch_W516_24HDataModel findLastTwoDayData(String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.HasSleep.eq(WatchData.HAS_SLEEP)).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr);
        if (queryBuilder.list().size() > 0) {
            Watch_W516_24HDataModel watch_w516_24HDataModel = queryBuilder.list().get(0);
            return watch_w516_24HDataModel;
        } else {
            return null;
        }
    }


    /**
     * 查询有数据的天list
     *
     * @param dateStr
     * @param userId
     * @return
     */
    public static ArrayList<String> findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr(String dateStr, String userId, String deviceId) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc
                (Watch_W516_24HDataModelDao.Properties.DateStr);
        ArrayList<String> dateList = new ArrayList<String>();
        Gson gson = new Gson();
        int[] m240Data = new int[240];//默认的240为0长度的填充数据
        int[] m1200Data = new int[1200];//默认的1200为0长度的填充数据

        for (int j = 0; j < 1200; j++) {
            if (j < 240) {
                m240Data[j] = 0;
            }
            m1200Data[j] = 0;
        }
        if (queryBuilder.list().size() > 0) {
            Logger.myLog("findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr 不为空");
            List<Watch_W516_24HDataModel> list = queryBuilder.list();
            //对每个数据天的睡眠做解析，如果0-20点有数据，说明有数据，如果没有再去查询前一天的20-23.59，看是否有数据
            for (int i = 0; i <= list.size() - 1; i++) {
                Watch_W516_24HDataModel watch_w516_24HDataModel = list.get(i);
                String sleepArray = watch_w516_24HDataModel.getSleepArray();
                String dateStr1 = watch_w516_24HDataModel.getDateStr();
                int[] ints = gson.fromJson(sleepArray, int[].class);
                List<Integer> todayDataList = new ArrayList<>();

                int[] todayData;
                if (ints == null) {
                    todayData = new int[1200];
                    System.arraycopy(m1200Data, 0, todayData, 0, 1200);
                } else {
                    if (ints.length > 1200) {
                        todayData = new int[1200];
                        System.arraycopy(ints, 0, todayData, 0, 1200);
                    } else {
                        //当天的情况,会有小于的时候
                        todayData = new int[ints.length];
                        System.arraycopy(ints, 0, todayData, 0, ints.length);
                    }
                }

                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
                if (Collections.max(todayDataList) > 0) {
                    //说明有数据
                    dateList.add(dateStr1);
                } else {
                    //查询前一天的20-23.59数据，如果有则


                    Watch_W516_24HDataModel lastDay24hData = findWatch_W516_Watch_W516_24HDataModelByDeviceId(userId, TimeUtils.getLastDayStr(dateStr1), deviceId);
                    if (lastDay24hData == null) {
                        //为空
                    } else {
                        //不为空，取1200-1440的数据
                        String sleepArray1 = lastDay24hData.getSleepArray();
                        int[] intsLast = gson.fromJson(sleepArray1, int[].class);
                        List<Integer> lastDataList = new ArrayList<>();
                        int[] lastData;
                        Logger.myLog(" TimeUtils.getLastDayStr(dateStr1):" + TimeUtils.getLastDayStr(dateStr1) + "intsLast:" + intsLast.length);
                        if (intsLast != null && intsLast.length > 1200) {
                            lastData = new int[240];
                            if (lastData.length != 14400) {
                                if (intsLast.length - 1200 <= 240) {
                                    System.arraycopy(intsLast, 1200, lastData, 0, intsLast.length - 1200);
                                } else {
                                    System.arraycopy(intsLast, 1200, lastData, 0, 240);
                                }
                            } else {
                                System.arraycopy(intsLast, 1200, lastData, 0, 240);
                            }
                            for (int j = 0; j <= lastData.length - 1; j++) {
                                lastDataList.add(lastData[j]);
                            }
                            if (Collections.max(lastDataList) > 0) {
                                //昨天20-23.59有数据
                                dateList.add(dateStr1);
                            }
                        }

                    }
                }

            }
            return dateList;
        } else {
            Logger.myLog("findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr 为空");
            return dateList;
        }
    }


    /**
     * 获取当前的用户用户当月的数据
     *
     * @param dateStr
     * @return
     */
    public static List<String> findWatch_W516_Watch_W516_24HDataMode_CurrentMonth_DateStr(String dateStr, String uerid, String deviceId) {
        //查询某一字段中不重复的字段内容
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(uerid)) {
            return null;
        }

        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(uerid), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc(Watch_W516_24HDataModelDao.Properties.DateStr);


        ArrayList<String> strings = new ArrayList<>();


        List<Watch_W516_24HDataModel> models = queryBuilder.list();

        if (models != null) {
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).getTotalSteps() != 0) {
                    strings.add(models.get(i).getDateStr());
                }
            }
        }


        return strings;
    }

    /**
     * 获取当前的用户用户当月的数据
     *
     * @param dateStr
     * @return
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataMode_CurrentMonth_Hr_DateStr(String dateStr, String uerid, String deviceId) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(uerid)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);

        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(uerid), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc(Watch_W516_24HDataModelDao.Properties.DateStr);
        return queryBuilder.list();
        //查询某一字段中不重复的字段内容
        /*String SQL_DISTINCT_ENAME = "SELECT DISTINCT " + Watch_W516_24HDataModelDao.Properties
                .DateStr.columnName +","+Watch_W516_24HDataModelDao.Properties.HrArray.columnName + " FROM " + Watch_W516_24HDataModelDao.TABLENAME + " WHERE "
                + Watch_W516_24HDataModelDao.Properties.UserId.columnName + "=" + uerid + " and " + Watch_W516_24HDataModelDao.Properties.DateStr.columnName + " LIKE " + "'%" + dateStr + "%' " + " " +
                "ORDER BY " + Watch_W516_24HDataModelDao.Properties.DateStr.columnName + " ASC";
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = BleAction.getDaoSession().getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        try {
            if (c.moveToFirst()) {
                do {

                    result.add(c.getString(0));
                }
                while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;*/
    }

    /**
     * 获取当前的用户最近30天的运动数据
     *
     * @param
     * @return
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataMode_last_month(String uerid, String startDate, String endDate, String deviceId) {
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(uerid)) {
            return null;
        }

     /*   //查询某一字段中不重复的字段内容
        String SQL_DISTINCT_ENAME = "SELECT DISTINCT " + Watch_W516_24HDataModelDao.Properties
                .DateStr.columnName+","+Watch_W516_24HDataModelDao.Properties
                .DateStr.columnName+","+Watch_W516_24HDataModelDao.Properties
                .DateStr.columnName + " FROM " + Watch_W516_24HDataModelDao.TABLENAME + " WHERE "
                + Watch_W516_24HDataModelDao.Properties.UserId.columnName + "=" + uerid + " and " + Watch_W516_24HDataModelDao.Properties.DateStr.columnName + " >= " + startDate + " and " + Watch_W516_24HDataModelDao.Properties.DateStr.columnName + " <= " + endDate +
                "ORDER BY " + Watch_W516_24HDataModelDao.Properties.DateStr.columnName + " ASC";
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = BleAction.getDaoSession().getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                }
                while (c.moveToNext());
            }
        } finally {
            c.close();
        }*/
//lt 小于  le小于等于  gt大于 ge大于等于 eq等于  noteq不等于
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(uerid), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId),
                Watch_W516_24HDataModelDao.Properties.DateStr.le(endDate), Watch_W516_24HDataModelDao.Properties.DateStr.ge(startDate)).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr);
        List result = queryBuilder.list();
        return result;
    }

    /**
     * 获取当前的用户最近30天的运动数据
     *
     * @param
     * @return
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataMode_last_month_Asc(String uerid, String startDate, String endDate, String deviceId) {

        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(uerid)) {
            return null;
        }
//lt 小于  le小于等于  gt大于 ge大于等于 eq等于  noteq不等于
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(uerid), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId),
                Watch_W516_24HDataModelDao.Properties.DateStr.le(endDate), Watch_W516_24HDataModelDao.Properties.DateStr.ge(startDate)).orderAsc(Watch_W516_24HDataModelDao.Properties.DateStr);
        List result = queryBuilder.list();
        return result;
    }

    /**
     * 查最新的四天的数据
     *
     * @param userId
     * @return
     */
    public static List<Watch_W516_24HDataModel> findLastFourDayData(String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.HasSleep.eq(WatchData.HAS_SLEEP)).orderDesc(Watch_W516_24HDataModelDao.Properties.DateStr).limit(4).offset(0);
        return queryBuilder.list();
        /*if (queryBuilder.list().size() > 0) {
            Watch_W516_24HDataModel watch_w516_24HDataModel = queryBuilder.list().get(0);
            return watch_w516_24HDataModel;
        } else {
            return null;
        }*/
    }

}
