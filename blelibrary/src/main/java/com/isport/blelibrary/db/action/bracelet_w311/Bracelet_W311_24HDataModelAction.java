package com.isport.blelibrary.db.action.bracelet_w311;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.isport.blelibrary.db.CommonInterFace.WatchData;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24HDataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.gen.Bracelet_W311_24HDataModelDao;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.TimeUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @创建者 bear
 * @创建时间 2019/3/5 15:54
 * @描述
 */
public class Bracelet_W311_24HDataModelAction {


    public static boolean saveOrUpdateHttp(final Bracelet_W311_24HDataModel model, final Long times) {


        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getsBracelet_w311_24HDataModelDao();
                Bracelet_W311_24HDataModel watch_w516_watch_w516_24HDataModelByDeviceId = findBracelet_HDataModelByDeviceId(model.getUserId(), model.getDateStr(), model.getDeviceId());
                if (watch_w516_watch_w516_24HDataModelByDeviceId != null) {
                    watch_w516_watch_w516_24HDataModelByDeviceId.setReportId(model.getReportId());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTimestamp(times);
                    watch_w516_24HDataModelDao.insertOrReplace(watch_w516_watch_w516_24HDataModelByDeviceId);
                    Logger.myLog("saveOrUpdateHttp == " + watch_w516_watch_w516_24HDataModelByDeviceId.toString());
                } else {
                    // watch_w516_24HDataModel.setReportId(0);
                    watch_w516_24HDataModelDao.insertOrReplace(model);
                }
                Logger.myLog(model.toString());

                /*if (bluetoothListener != null) {
                    bluetoothListener.onSyncSuccess();
                }*/
            }
        });
        return true;

    }

    public static boolean updateToday24HData(final String userId, final String strDate, final String deviceId, final long steps, final long cal, final float dis) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getsBracelet_w311_24HDataModelDao();
                Bracelet_W311_24HDataModel watch_w516_watch_w516_24HDataModelByDeviceId = findBracelet_HDataModelByDeviceId(userId, strDate, deviceId);
                if (watch_w516_watch_w516_24HDataModelByDeviceId != null) {
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalSteps(steps);
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalDistance(dis);
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalCalories(cal);
                    watch_w516_24HDataModelDao.insertOrReplace(watch_w516_watch_w516_24HDataModelByDeviceId);

                }
            }
        });
        return false;

    }

    public static boolean saveOrUpdateBracelet24HDataModel(
            final Bracelet_W311_24HDataModel model, String deviceName) {


        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getsBracelet_w311_24HDataModelDao();
                Bracelet_W311_24HDataModel watch_w516_watch_w516_24HDataModelByDeviceId = findBracelet_HDataModelByDeviceId(model.getUserId(), model.getDateStr(), model.getDeviceId());
                if (watch_w516_watch_w516_24HDataModelByDeviceId != null) {
                    watch_w516_watch_w516_24HDataModelByDeviceId.setReportId("0");
                    watch_w516_watch_w516_24HDataModelByDeviceId.setDeviceId(model.getDeviceId());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTimestamp(System.currentTimeMillis());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalSteps(model.getTotalSteps());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalDistance(model.getTotalDistance());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalCalories(model.getTotalCalories());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setStepArray(model.getStepArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setSleepArray(model.getSleepArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setTotalSleepTime(model.getTotalSleepTime());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHrArray(model.getHrArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setAvgHR(model.getAvgHR());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHasSleep(model.getHasSleep());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHasHR(model.getHasHR());
                    watch_w516_24HDataModelDao.insertOrReplace(watch_w516_watch_w516_24HDataModelByDeviceId);
                } else {
                    // watch_w516_24HDataModel.setReportId(0);
                    watch_w516_24HDataModelDao.insertOrReplace(model);
                }
                Logger.myLog("saveOrUpdateBracelet24HDataModel:model.getSleepArray()=" +model.getSleepArray()+"model.getTotalSleepTime()="+model.getTotalSleepTime());

                /*if (bluetoothListener != null) {
                    bluetoothListener.onSyncSuccess();
                }*/
            }
        });
        return true;
    }

    public static boolean updateBracelet24HHrDataModel(
            final Bracelet_W311_24HDataModel model, String deviceName) {
        if (TextUtils.isEmpty(deviceName)) {
            return false;
        }

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Bracelet_W311_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                        .getsBracelet_w311_24HDataModelDao();
                Bracelet_W311_24HDataModel watch_w516_watch_w516_24HDataModelByDeviceId = findBracelet_HDataModelByDeviceId(model.getUserId(), model.getDateStr(), model.getDeviceId());
                if (watch_w516_watch_w516_24HDataModelByDeviceId != null) {
                    watch_w516_watch_w516_24HDataModelByDeviceId.setDeviceId(model.getDeviceId());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHrArray(model.getHrArray());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setAvgHR(model.getAvgHR());
                    watch_w516_watch_w516_24HDataModelByDeviceId.setHasHR(model.getHasHR());
                    watch_w516_24HDataModelDao.insertOrReplace(watch_w516_watch_w516_24HDataModelByDeviceId);
                    Logger.myLog(model.toString());
                }

                /*if (bluetoothListener != null) {
                    bluetoothListener.onSyncSuccess();
                }*/
            }
        });
        return true;
    }

    /**
     * 查询绑定时间之后的数据,之前的数据舍弃
     */
    public static List<Bracelet_W311_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp1
    (int userId, long
            timeTamp, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId), Watch_W516_24HDataModelDao.Properties.DeviceId.eq(deviceId), Watch_W516_24HDataModelDao.Properties.ReportId.eq(0), Watch_W516_24HDataModelDao
                .Properties.Timestamp.ge(timeTamp)).orderDesc
                (Watch_W516_24HDataModelDao.Properties.Timestamp);
//        .distinct()
        List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            return list;
        } else {
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
    public static List<Bracelet_W311_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp
    (String userId, long
            timeTamp, String deviceId, boolean isUpdateToday) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }

        Logger.myLog("是否是同步后解绑" + isUpdateToday);
        //Logger.myLog("findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp:userId" + userId + ",deviceId:" + deviceId);
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
       /* queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.ReportId.eq(0), Bracelet_W311_24HDataModelDao
                .Properties.Timestamp.ge(timeTamp)).orderDesc
                (Bracelet_W311_24HDataModelDao.Properties.Timestamp);*/
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.ReportId.eq(0)).orderDesc
                (Bracelet_W311_24HDataModelDao.Properties.Timestamp).distinct();
//        .distinct()
        List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            List<Bracelet_W311_24HDataModel> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                Bracelet_W311_24HDataModel watch_w516_24HDataModel = list.get(i);

                Logger.myLog("需要同步的日期" + watch_w516_24HDataModel.getDateStr());

                Gson gson = new Gson();
                int[] stepArray = new Gson().fromJson(watch_w516_24HDataModel.getStepArray(), int[].class);
                int[] sleepArray = new Gson().fromJson(watch_w516_24HDataModel.getSleepArray(), int[].class);
                //如果是解绑后同步需要把当天的数据封装完整上传给服务器
                if (isUpdateToday) {
                    if (stepArray.length >= 1440 && sleepArray.length >= 1440) {
                        result.add(watch_w516_24HDataModel);
                    } else {
                        ArrayList<Integer> stepList = new ArrayList<>();
                        ArrayList<Integer> sleepList = new ArrayList<>();
                        long sumStep = 0;
                        for (int j = 0; j < stepArray.length; j++) {
                            stepList.add(stepArray[j]);
                            sleepList.add(sleepArray[j]);
                            sumStep += stepArray[j];
                        }
                        for (int j = stepArray.length; j < 1440; j++) {
                            if (sumStep < watch_w516_24HDataModel.getTotalSteps() && j == stepArray.length) {
                                stepList.add((int) Math.abs(watch_w516_24HDataModel.getTotalSteps() - sumStep));
                            } else {
                                stepList.add(0);
                            }
                            sleepList.add(0);
                        }
                        watch_w516_24HDataModel.setStepArray(gson.toJson(stepList));
                        watch_w516_24HDataModel.setSleepArray(gson.toJson(sleepList));

                        result.add(watch_w516_24HDataModel);
                    }
                } else {
                    if (stepArray.length >= 1440 && sleepArray.length >= 1440) {
                        result.add(watch_w516_24HDataModel);
                    }
                }
                //int[] hrArray = new Gson().fromJson(watch_w516_24HDataModel.getHrArray(), int[].class);
                //  Logger.myLog("Bracelet_W311_24HDataModel:" + watch_w516_24HDataModel.toString());

            }
            if (result.size() > 0) {
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByTimeTamp
            (String userId, long
                    timeTamp) {

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

    //获取当前用户某天的数据
    public static Bracelet_W311_24HDataModel findBracelet_HDataModelByDeviceId(
            @Nullable String userId, @Nullable String dateStr, @Nullable String deviceId) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId)||TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId),
                Bracelet_W311_24HDataModelDao.Properties.DateStr.eq(dateStr)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr);
        if (queryBuilder.list().size() > 0) {
            Bracelet_W311_24HDataModel watch_W516_24HDataModel = queryBuilder.list().get(0);
            return watch_W516_24HDataModel;
        } else {
            return null;
            /* Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = new Bracelet_W311_24HDataModel();
            bracelet_w311_24HDataModel.setDateStr(dateStr);
            return bracelet_w311_24HDataModel;*/
   //18682008982
        }
    }

    /**
     * 查询所有数据
     */
    public static List<Watch_W516_24HDataModel> findAll(String userId) {
        QueryBuilder<Watch_W516_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Watch_W516_24HDataModel.class);
        queryBuilder.where(Watch_W516_24HDataModelDao.Properties.UserId.eq(userId));
        if (queryBuilder.list().size() > 0) {
            List<Watch_W516_24HDataModel> list = queryBuilder.list();
            return list;
        } else {
            return null;
        }
    }

    /**
     * 查最新的四天的数据
     *
     * @param userId
     * @return
     */
    public static List<Bracelet_W311_24HDataModel> findLastFourDayData(String userId, String
            deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.HasSleep.eq(WatchData.HAS_SLEEP)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr).limit(4).offset(0);
        return queryBuilder.list();
        /*if (queryBuilder.list().size() > 0) {
            Watch_W516_24HDataModel watch_w516_24HDataModel = queryBuilder.list().get(0);
            return watch_w516_24HDataModel;
        } else {
            return null;
        }*/
    }


    // 获取当前用户最新的一条数据
    public static Bracelet_W311_24HDataModel findWatch_W516_Watch_W516_24HDataModelByDeviceId(
            String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr).limit(1).offset(0);
        List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            Bracelet_W311_24HDataModel watch_W516_24HDataModel = list.get(0);
            return watch_W516_24HDataModel;
        } else {
            return null;
        }
    }

    // 获取当前用户最新的一条数据
    public static Bracelet_W311_24HDataModel findWatch_W516_Watch_W516_24H_Hr_DataModelByDeviceId
    (String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.HasHR.eq(WatchData.HAS_HR)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr).limit(1).offset(0);
        List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
        if (list.size() > 0) {
            Bracelet_W311_24HDataModel watch_W516_24HDataModel = list.get(0);
            return watch_W516_24HDataModel;
        } else {
            return null;
        }
    }

    // 获取当前用户最新的一条数据
    // TODO: 2019/3/13 需要调整为最近两天有步数数据的
    public static List<Bracelet_W311_24HDataModel> find_Bracelet_311_24HDataModelByDeviceId_Last_Two
    (String userId, String deviceId, String strDate) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.DateStr.notEq(strDate)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr).limit(2).offset(0);
        List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
        return list;
    }

    /**
     * 获取当天前数据不为0的天
     *
     * @param userId
     * @param deviceId
     * @return
     */
    public static List<Watch_W516_24HDataModel> findWatch_W516_Watch_W516_24HDataModelByDeviceId_Last_Day
    (String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
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
    public static List<Bracelet_W311_24HDataModel> find_Bracelet_w311_24HDataModelByDeviceId_Last_Two_HR
    (String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.HasHR.eq(WatchData.HAS_HR)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr).limit(1).offset(0);
        List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
        return list;
    }

    /**
     * 查最新的两天数据
     *
     * @param userId
     * @return
     */
    public static Bracelet_W311_24HDataModel findLastTwoDayData(String userId, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.HasSleep.eq(WatchData.HAS_SLEEP)).orderDesc(Bracelet_W311_24HDataModelDao.Properties.DateStr);
        if (queryBuilder.list().size() > 0) {
            Bracelet_W311_24HDataModel watch_w516_24HDataModel = queryBuilder.list().get(0);
            return watch_w516_24HDataModel;
        } else {
            return null;
        }
    }


    //删除当天当天

    public static void delCurretentDay(String userId, String dateStr, String deviceId) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(deviceId)) {
            return;
        }
        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = findBracelet_HDataModelByDeviceId(userId, dateStr, deviceId);
        if (bracelet_w311_24HDataModel != null) {
            Gson gson = new Gson();
            int[] stepArray = gson.fromJson(bracelet_w311_24HDataModel.getStepArray(), int[].class);
            int[] sleepArray = gson.fromJson(bracelet_w311_24HDataModel.getSleepArray(), int[].class);
            //如果是解绑后同步需要把当天的数据封装完整上传给服务器
            if (stepArray.length < 1440 && sleepArray.length < 1440) {
                Bracelet_W311_24HDataModelDao deviceTypeTableDao = BleAction.getsBracelet_w311_24HDataModelDao();
                deviceTypeTableDao.delete(bracelet_w311_24HDataModel);
            }
        }

    }


    /**
     * 获取当前的用户用户当月的数据
     *
     * @param dateStr
     * @return
     */
    public static List<String> findBracelet_W311_Bracelet_W311_24HDataMode_CurrentMonth_DateStr
    (String dateStr, String uerid, String deviceId) {
        //查询某一字段中不重复的字段内容
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }

        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(uerid), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc(Bracelet_W311_24HDataModelDao.Properties.DateStr);


        ArrayList<String> strings = new ArrayList<>();


        List<Bracelet_W311_24HDataModel> models = queryBuilder.list();

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
     * 获取当前的用户最近开始时间和结束时间的区间的运动数据
     *
     * @param
     * @return
     */
    public static List<Bracelet_W311_24HDataModel> findWatch_W516_Watch_W516_24HDataMode_last_month_Asc
    (String uerid, String startDate, String endDate, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
//lt 小于  le小于等于  gt大于 ge大于等于 eq等于  noteq不等于
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(uerid), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId),
                Bracelet_W311_24HDataModelDao.Properties.DateStr.le(endDate), Bracelet_W311_24HDataModelDao.Properties.DateStr.ge(startDate)).orderAsc(Bracelet_W311_24HDataModelDao.Properties.DateStr);
        List result = queryBuilder.list();
        return result;
    }


    /**
     * 查询有数据的天list
     *
     * @param dateStr
     * @param userId
     * @return
     */
    public static ArrayList<String> findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr
    (String dateStr, String userId, String deviceId) {
        List<Integer> todayDataList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<String>();
        try {

            if (TextUtils.isEmpty(deviceId)) {
                return null;
            }
            QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                    (Bracelet_W311_24HDataModel.class);

            queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(userId), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc
                    (Bracelet_W311_24HDataModelDao.Properties.DateStr);

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
                List<Bracelet_W311_24HDataModel> list = queryBuilder.list();
                //对每个数据天的睡眠做解析，如果0-20点有数据，说明有数据，如果没有再去查询前一天的20-23.59，看是否有数据
                for (int i = 0; i <= list.size() - 1; i++) {
                    Bracelet_W311_24HDataModel watch_w516_24HDataModel = list.get(i);
                    String sleepArray = watch_w516_24HDataModel.getSleepArray();
                    String dateStr1 = watch_w516_24HDataModel.getDateStr();
                    int[] ints = gson.fromJson(sleepArray, int[].class);

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
                    todayDataList.clear();
                    for (int j = 0; j <= todayData.length - 1; j++) {
                        todayDataList.add(todayData[j]);
                    }


                    if (Collections.max(todayDataList) > 0) {
                        //说明有数据
                        dateList.add(dateStr1);
                    } else {
                        //查询前一天的20-23.59数据，如果有则
                        Bracelet_W311_24HDataModel lastDay24hData = findBracelet_HDataModelByDeviceId(userId, TimeUtils.getLastDayStr(dateStr1), deviceId);
                        if (lastDay24hData == null) {
                            //为空
                        } else {
                            //不为空，取1200-1440的数据
                            String sleepArray1 = lastDay24hData.getSleepArray();
                            int[] intsLast = gson.fromJson(sleepArray1, int[].class);
                            List<Integer> lastDataList = new ArrayList<>();
                            int[] lastData;
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
                                // System.arraycopy(intsLast, 1200, lastData, 0, 240);
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

        } catch (Exception e) {

            Logger.myLog("findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr" + e.toString());
            return dateList;
        }


    }

    /**
     * 获取当前的用户用户当月的数据
     *
     * @param dateStr
     * @return
     */
    public static List<Bracelet_W311_24HDataModel> findWatch_W516_Watch_W516_24HDataMode_CurrentMonth_Hr_DateStr
    (String dateStr, String uerid, String deviceId) {
        QueryBuilder<Bracelet_W311_24HDataModel> queryBuilder = BleAction.getDaoSession().queryBuilder
                (Bracelet_W311_24HDataModel.class);
        queryBuilder.where(Bracelet_W311_24HDataModelDao.Properties.UserId.eq(uerid), Bracelet_W311_24HDataModelDao.Properties.DeviceId.eq(deviceId), Bracelet_W311_24HDataModelDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc(Bracelet_W311_24HDataModelDao.Properties.DateStr);
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

}
