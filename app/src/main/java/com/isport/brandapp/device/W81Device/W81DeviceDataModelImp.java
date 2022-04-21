package com.isport.brandapp.device.W81Device;

import android.text.TextUtils;
import android.util.Log;

import com.crrepa.ble.conn.bean.CRPSleepInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isport.blelibrary.db.CommonInterFace.WatchData;
import com.isport.blelibrary.db.action.W81Device.DeviceMeasuredDActionation;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.table.DeviceTempUnitlTable;
import com.isport.blelibrary.db.table.w526.Device_TempTable;
import com.isport.blelibrary.db.table.w811w814.BloodPressureMode;
import com.isport.blelibrary.db.table.w811w814.OneceHrMode;
import com.isport.blelibrary.db.table.w811w814.OxygenMode;
import com.isport.blelibrary.db.table.w811w814.W81DeviceDetailData;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.braceletModel.IW311SettingModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311ModelSettingImpl;
import com.isport.brandapp.device.watch.bean.WatchInsertBean;
import com.isport.brandapp.home.bean.db.HeartRateMainData;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.SporadicNapData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.bean.http.WristbandHrHeart;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.ExerciseInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bike.gymproject.viewlibray.SleepFormatUtils;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;

public class W81DeviceDataModelImp implements IW81DeviceDataModel {


    W81DeviceDataAction w81DeviceDataAction;
    DeviceMeasuredDActionation deviceMeasuredDActionation;


    public W81DeviceDataModelImp() {
        w81DeviceDataAction = new W81DeviceDataAction();
        deviceMeasuredDActionation = new DeviceMeasuredDActionation();
    }


    @Override
    public Wristbandstep getStepData(String userId, String deviceId, String strDate) {
        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceDetialData(deviceId, userId, strDate);
        return parWristBandStep(w81DeviceDetailData);
    }

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public Wristbandstep parWristBandStep(W81DeviceDetailData model) {
        Wristbandstep wristbandstep = new Wristbandstep();
        if (model == null) {
            return wristbandstep;
        }
        //距离是向下取整的
        float dis = CommonDateUtil.formatFloor(model.getDis(), true);
        wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(dis));
        wristbandstep.setCalorie(String.valueOf(model.getCal()));
        wristbandstep.setStepNum(String.valueOf(model.getStep()));
        wristbandstep.setLastServerTime(model.getTimestamp() == null ? 0 : model.getTimestamp());
        wristbandstep.setStrDate(model.getDateStr());
        String stepArray = model.getStepArray();
        ArrayList<Integer> stepList = new ArrayList<>();

        if(stepArray != null && !stepArray.equals("[]")){
            List<String[]> stepArrList = new Gson().fromJson(stepArray,new TypeToken<List<String[]>>(){}.getType());
            if(stepArrList != null){

                for(String[] st : stepArrList){
                    stepList.add(Integer.valueOf(st[1]));
                }

            }
        }

        int sumSize = stepList.size();
        if (stepList.size() < 24) {
            for (int i = 0; i < 24 - sumSize; i++) {
                stepList.add(0);
            }
        }
        wristbandstep.setStepArry(stepList);

        return wristbandstep;
    }


    @Override
    public WatchSportMainData getDateStrStepData(String userId, String deviceId, String strDate) {
        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceDetialData(userId, deviceId, strDate);

        Logger.myLog("getDateStrStepData" + w81DeviceDetailData);

        WatchSportMainData wristbandstep = new WatchSportMainData();
        if (w81DeviceDetailData != null) {
            wristbandstep.setDateStr(w81DeviceDetailData.getDateStr());
            wristbandstep.setDistance(CommonDateUtil.formatTwoStr(w81DeviceDetailData.getDis()));
            wristbandstep.setStep(String.valueOf(w81DeviceDetailData.getStep()));
            wristbandstep.setCal(String.valueOf(w81DeviceDetailData.getCal()));
        }
        return wristbandstep;
    }

    @Override
    public WatchSportMainData getLastStepData(String userId, String deviceId, boolean isBind) {


        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceDetialData(deviceId, userId, TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()));
        Logger.myLog("getLastStepData:" + w81DeviceDetailData);

        WatchSportMainData wristbandstep = new WatchSportMainData();
        if (w81DeviceDetailData != null) {
            wristbandstep.setDateStr(w81DeviceDetailData.getDateStr());
            wristbandstep.setDistance(CommonDateUtil.formatTwoPoint(CommonDateUtil.formatFloor(w81DeviceDetailData.getDis(), true)));
            wristbandstep.setStep(String.valueOf(w81DeviceDetailData.getStep()));
            wristbandstep.setCal(String.valueOf(w81DeviceDetailData.getCal()));
        } else if (isBind) {
            wristbandstep.setDateStr(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()));
            wristbandstep.setDistance("0");
            wristbandstep.setStep("0");
            wristbandstep.setCal("0");
        }

        return wristbandstep;
    }


    @Override
    public WatchSleepDayData getLastSleepData(String userId, String deviceId, boolean main) {
        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceSleepLastest(deviceId, userId, "");
        Logger.myLog("getW81DeviceSleepLastest:deviceId:" + deviceId + ",userId:" + userId + "w81DeviceDetailData:" + new Gson().toJson(w81DeviceDetailData));
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();


        if (main) {
            if (w81DeviceDetailData != null) {
                watchSleepDayData.setTotalSleepTime(w81DeviceDetailData.getLightTime()+w81DeviceDetailData.getRestfulTime());
              //  watchSleepDayData.setAwakeSleepTime(w81DeviceDetailData.getSoberTime());
                watchSleepDayData.setDateStr(w81DeviceDetailData.getDateStr());
            }
        } else {
            if(DeviceTypeUtil.isContainF18(deviceId)){
                watchSleepDayData = changeF18SleepData(w81DeviceDetailData);
            }else{
                watchSleepDayData = parseSleep(w81DeviceDetailData);
            }


        }
        return watchSleepDayData;
    }

    private WatchSleepDayData changeF18SleepData(W81DeviceDetailData w81DeviceDetailData){
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();
        watchSleepDayData.setTotalSleepTime(w81DeviceDetailData.getTotalTime());
        watchSleepDayData.setAwakeSleepTime(w81DeviceDetailData.getSoberTime());
        watchSleepDayData.setDateStr(w81DeviceDetailData.getDateStr());
        watchSleepDayData.setDeepSleepTime(w81DeviceDetailData.getRestfulTime());
        watchSleepDayData.setLightLV1SleepTime(w81DeviceDetailData.getLightTime());
        watchSleepDayData.setSporadicNapSleepTimeStr(w81DeviceDetailData.getSleepArray());
        return watchSleepDayData;
    }



    @Override
    public HeartRateMainData getLastHrData(String userId, String deviceId) {
        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceHrData(deviceId, userId, "");

        Logger.myLog("getLastHrData:" + w81DeviceDetailData + "userId：" + userId + "deviceId:" + deviceId);
        HeartRateMainData heartRateMainData = new HeartRateMainData();
        if (w81DeviceDetailData != null) {
            heartRateMainData.setDateStr(w81DeviceDetailData.getDateStr());
            WristbandHrHeart wristbandHrHeart = parseHrData(w81DeviceDetailData);
            heartRateMainData.setHeartRate(wristbandHrHeart.getAvgHr());
        }


        return heartRateMainData;
    }


    @Override
    public WatchSleepDayData getStrDateSleepData(String userId, String deviceId, String strDate) {
        // Logger.myLog("getStrDateSleepData:" + userId + "deviceId:" + deviceId + "strDate:" + strDate);
        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceSleepLastest(deviceId, userId, strDate);
        WatchSleepDayData watchSleepDayData = null;
        if (w81DeviceDetailData != null) {
            if(DeviceTypeUtil.isContainF18(deviceId)){
                watchSleepDayData = changeF18SleepData(w81DeviceDetailData);
            }else{
                watchSleepDayData = parseSleep(w81DeviceDetailData);
            }

        } else {
            watchSleepDayData = new WatchSleepDayData();
        }
        return watchSleepDayData;
    }

    @Override
    public WristbandHrHeart getStrDateHrData(String userId, String deviceId, String strDate) {

        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceHrData(deviceId, userId, strDate);


        Logger.myLog("getStrDateHrData" + w81DeviceDetailData);

        WristbandHrHeart wristbandHrHeart = parseHrData(w81DeviceDetailData);
        return wristbandHrHeart;


    }

    @Override
    public ArrayList<String> getMonthData(String strDate, String deviceId) {
        ArrayList<String> dataList = (ArrayList<String>) w81DeviceDataAction.findCurrentMonthStepDateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        for (int i = 0; i < dataList.size(); i++) {
            Logger.myLog("w81devcie:" + strDate + ":" + dataList.get(i));
        }
        return dataList;
    }

   /* @Override
    public WristbandHrHeart getLastDetailHrData(String userId, String deviceId) {
        W81DeviceDetailData w81DeviceDetailData = w81DeviceDataAction.getW81DeviceHrData(deviceId, userId, "");
        WristbandHrHeart wristbandHrHeart = parseHrData(w81DeviceDetailData);
        return wristbandHrHeart;
    }*/


    public WristbandHrHeart parseHrData(W81DeviceDetailData model) {
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
        wristbandHrHeart.setTimeInterval(model.getTimeInterval());
        ArrayList<Integer> arrayList;
        if (TextUtils.isEmpty(model.getHrArray())) {
            arrayList = new ArrayList<Integer>();
        } else {
            Integer[] hrArry = new Gson().fromJson(model.getHrArray(), Integer[].class);
            arrayList = new ArrayList<Integer>(Arrays.asList(hrArry));
        }
        wristbandHrHeart.setStrDate(model.getDateStr());
        wristbandHrHeart.setLastServerTime(model.getTimestamp());

        ArrayList<Integer> resultList = new ArrayList<Integer>();
        int validCount = 0, validSum = 0, validMinValue = wristbandHrHeart.getMaxHr();
        boolean isFirst = true;
        for (int i = 0; i < arrayList.size(); i++) {
            int integer = arrayList.get(i);
            resultList.add(integer < 30 ? 0 : integer);
            if (integer != 0) {
                if (integer >= 30) {
                    if (isFirst) {
                        validMinValue = arrayList.get(i);
                        isFirst = false;
                    } else {
                        if (validMinValue > arrayList.get(i)) {
                            validMinValue = arrayList.get(i);
                        }
                    }
                    validCount++;
                    validSum += integer;
                }
            }
        }
        if (resultList.size() == 0) {
            wristbandHrHeart.setMaxHr(0);
        } else {
            wristbandHrHeart.setMaxHr(Collections.max(resultList));
        }
        wristbandHrHeart.setMinHr(validMinValue);
        wristbandHrHeart.setAvgHr(validCount == 0 ? 0 : Math.round(validSum / (float) validCount));
        Logger.myLog("dateStr == " + model.getDateStr() + "heartRateList == " + " AVG == " + (validCount == 0 ? 0 : Math.round(validSum / (float) validCount)));
        wristbandHrHeart.setHrArry(resultList);
        return wristbandHrHeart;
    }


    @Override
    public ArrayList<String> getHrMonthData(String strDate, String userid, String deviceId) {

        ArrayList<String> dataList = (ArrayList<String>) w81DeviceDataAction.findCurrentMonthHrOrSleepDateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId, WatchData.HAS_HR);
        for (int i = 0; i < dataList.size(); i++) {
            Logger.myLog("w81devcie:" + strDate + ":" + dataList.get(i));
        }
        return dataList;
    }

    @Override
    public ArrayList<String> getSleepMonthData(String strDate, String userId, String deviceId) {
        Logger.myLog("getSleepMonthData");
        ArrayList<String> dataList = (ArrayList<String>) w81DeviceDataAction.findCurrentMonthHrOrSleepDateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId, WatchData.HAS_SLEEP);
        for (int i = 0; i < dataList.size(); i++) {
            Logger.myLog("w81devcie:" + strDate + ":" + dataList.get(i));
        }
        return dataList;
    }

    @Override
    public List<WatchInsertBean> getAllNoUpgradeW81DeviceDetailData(String deviceId, String userId, String defWriId, boolean isUpgradeToday) {

        List<W81DeviceDetailData> list = w81DeviceDataAction.getUnUpgradeW81DeviceDetialData(deviceId, userId, defWriId);

        Logger.myLog("getNoUpgradeW81DevcieDetailData" + list + "---------" + isUpgradeToday);
        W81DeviceDetailData deviceDetailData;
        WatchInsertBean watchInsertBean;
        List<WatchInsertBean> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {

                deviceDetailData = list.get(i);
               /* if (!isUpgradeToday && deviceDetailData.getDateStr().equals(TimeUtils.getTodayYYYYMMDD())) {
                    continue;
                }*/
                watchInsertBean = new WatchInsertBean();
                watchInsertBean.setIsHaveHeartRate((deviceDetailData.getHasHR() == WatchData.HAS_HR) ? "1" : "0");
                watchInsertBean.setTotalSleepTime(String.valueOf(deviceDetailData.getTotalTime()));
                watchInsertBean.setTotalDistance(String.valueOf(deviceDetailData.getDis()));//传输的是米
                watchInsertBean.setTotalCalories(String.valueOf(deviceDetailData.getCal()));
                watchInsertBean.setTotalSteps(deviceDetailData.getStep());
                watchInsertBean.setDateStr(deviceDetailData.getDateStr());
                watchInsertBean.setDeviceId(deviceDetailData.getDeviceId());
                watchInsertBean.setUserId(deviceDetailData.getUserId());
                watchInsertBean.setSleepDetailArray(deviceDetailData.getSleepArray());
                watchInsertBean.setStepDetailArray(deviceDetailData.getStepArray());
                watchInsertBean.setHeartRateDetailArray(deviceDetailData.getHrArray());
                watchInsertBean.setTotalDeep(String.valueOf(deviceDetailData.getRestfulTime()));
                watchInsertBean.setTotalLight(String.valueOf(deviceDetailData.getLightTime()));
                list1.add(watchInsertBean);
            }

        }

        return list1;
    }




    public List<WatchInsertBean> getAllNoUpgradeW81DeviceDetailData(String deviceId, String userId,  boolean isUpgradeToday) {

        List<W81DeviceDetailData> list = w81DeviceDataAction.getUnUpgradeW81DeviceDetialData(deviceId, userId);

        Logger.myLog("1111getNoUpgradeW81DevcieDetailData" + list + "---------" + isUpgradeToday);
        W81DeviceDetailData deviceDetailData;
        WatchInsertBean watchInsertBean;
        List<WatchInsertBean> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {

                deviceDetailData = list.get(i);
               /* if (!isUpgradeToday && deviceDetailData.getDateStr().equals(TimeUtils.getTodayYYYYMMDD())) {
                    continue;
                }*/
                watchInsertBean = new WatchInsertBean();
                watchInsertBean.setIsHaveHeartRate((deviceDetailData.getHasHR() == WatchData.HAS_HR) ? "1" : "0");
                watchInsertBean.setTotalSleepTime(String.valueOf(deviceDetailData.getTotalTime()));
                watchInsertBean.setTotalDistance(String.valueOf(deviceDetailData.getDis()));//传输的是米
                watchInsertBean.setTotalCalories(String.valueOf(deviceDetailData.getCal()));
                watchInsertBean.setTotalSteps(deviceDetailData.getStep());
                watchInsertBean.setDateStr(deviceDetailData.getDateStr());
                watchInsertBean.setDeviceId(deviceDetailData.getDeviceId());
                watchInsertBean.setUserId(deviceDetailData.getUserId());
                watchInsertBean.setSleepDetailArray(deviceDetailData.getSleepArray());
                watchInsertBean.setStepDetailArray(deviceDetailData.getStepArray());
                watchInsertBean.setHeartRateDetailArray(deviceDetailData.getHrArray());
                watchInsertBean.setTotalDeep(String.valueOf(deviceDetailData.getRestfulTime()));
                watchInsertBean.setTotalLight(String.valueOf(deviceDetailData.getLightTime()));
                list1.add(watchInsertBean);
            }

        }

        return list1;
    }





    @Override
    public void saveHrData(String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, String hrList, int timeInterval) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> list = new ArrayList<>();
                if (TextUtils.isEmpty(hrList)) {
                    return;
                } else {
                    Integer[] hrArry = new Gson().fromJson(hrList, Integer[].class);
                    list = new ArrayList<Integer>(Arrays.asList(hrArry));
                    if (list.size() == 0) {
                        return;
                    }
                }
                w81DeviceDataAction.saveW81DeviceHrData(deviceId, userId, wristbandSportDetailId, dateStr, timestamp, list, timeInterval);
            }
        });
    }

    @Override
    public void saveStepData(String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int step, int dis, int cal, boolean isNet) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                w81DeviceDataAction.saveW81DeviceStepData(deviceId, userId, wristbandSportDetailId, dateStr, timestamp, step, dis, cal, isNet);
            }
        });
    }

    @Override
    public void saveSleepData(String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int totalTime,
                              int restfulTime,
                              int lightTime,
                              int soberTime, String sleepDetail) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                w81DeviceDataAction.saveW81DeviceSleepData(deviceId, userId, wristbandSportDetailId, dateStr, timestamp, totalTime, restfulTime, lightTime, soberTime, sleepDetail);
            }
        });
    }

    @Override
    public void updateWriId(String deviceId, String userId, String strDate, String updateWriId) {
        w81DeviceDataAction.updateWriId(deviceId, userId, strDate, updateWriId);


    }

    @Override
    public void updateWriId(String deviceId, String userId, int type) {
        //血压是0 血氧1 2 单次心率 ,3体温
        if (type == 0) {
            deviceMeasuredDActionation.updateBloodPressureWridId(deviceId, userId, "0");
        } else if (type == 1) {
            deviceMeasuredDActionation.updateOxyGenWristbandId(deviceId, userId, "0");

        } else if (type == 2) {
            deviceMeasuredDActionation.updateOneceHrWridId(deviceId, userId, "0");
        } else if (type == 3) {
            deviceMeasuredDActionation.updateTempWridId(deviceId, userId, "0");
        }
    }

    @Override
    public List<TempInfo> getLastNumberTempData(String deviceId, String userId, int number) {
        List<Device_TempTable> bloodPressureMode = deviceMeasuredDActionation.findNumberTempMode(deviceId, userId, 7);
        // Logger.myLog("findTempMode: bloodPressureMode:" + deviceId + ",userId:" + userId + "6----" + bloodPressureMode);
        List<TempInfo> tempInfos = new ArrayList<>();
        if (bloodPressureMode != null) {
            IW311SettingModel w311ModelSetting = new W311ModelSettingImpl();
            DeviceTempUnitlTable table = w311ModelSetting.getTempUtil(userId, deviceId);
            for (int i = 0; i < bloodPressureMode.size(); i++) {
                TempInfo info = new TempInfo();
                Device_TempTable model = bloodPressureMode.get(i);
                info.setFahrenheit(model.getFahrenheit());
                info.setCentigrade(model.getCentigrade());
                info.setStrDate(TimeUtils.getTimeByyyyyMMddhhmmss(model.getTimestamp()));
                info.setTimestamp(model.getTimestamp());
                if (table != null) {
                    info.setTempUnitl(table.getTempUnitl());
                } else {
                    info.setTempUnitl("0");
                }
                info = setState(info);
                tempInfos.add(info);
            }
        }

        return tempInfos;
    }


    @Override
    public void saveOxygenData(OxyInfo oxyInfo) {
        deviceMeasuredDActionation.saveOrUpdateOxygenData(oxyInfo.getDeviceId(), oxyInfo.getUserId(), oxyInfo.getBoValue(), oxyInfo.getTimestamp(), oxyInfo.getWristbandBloodOxygenId());

    }

    @Override
    public void saveBloodPresureData(BPInfo bpInfo) {
        deviceMeasuredDActionation.saveOrUpdateBloodPressureData(bpInfo.getDeviceId(), bpInfo.getUserId(), bpInfo.getSpValue(), bpInfo.getDpValue(), bpInfo.getTimestamp(), bpInfo.getWristbandBloodPressureId());

    }

    @Override
    public void saveTempData(TempInfo bpInfo) {
        deviceMeasuredDActionation.saveOrUpdateTempData(bpInfo.getDeviceId(), bpInfo.getUserId(), bpInfo.getCentigrade(), bpInfo.getFahrenheit(), bpInfo.getTimestamp(), bpInfo.getWristbandTemperatureId());
    }

    @Override
    public void saveExeciseData(ExerciseInfo exerciseInfo) {

    }

    @Override
    public void saveOnceHrData(OnceHrInfo info) {
        int hrData = 0;
        try {
            hrData = Integer.parseInt(info.getHeartValue());
        } catch (Exception e) {
            hrData = 0;
        } finally {
            if (hrData > 30) {
                deviceMeasuredDActionation.saveOrUpdateOnceHrData(info.getDeviceId(), info.getUserId(), hrData, info.getTimestamp(), info.getSingleHeartRateId());

            }
        }


    }

    @Override
    public OxyInfo getOxygenLastData(String deviceId, String userId) {
        OxygenMode oxygenMode = deviceMeasuredDActionation.findOxyenMode(deviceId, userId, 0);
        if(oxygenMode != null)
        Logger.myLog("----获取单次血氧getOxygenLastData:" + oxygenMode.toString() + "deviceId");
        OxyInfo oxyInfo = new OxyInfo();
        if (oxygenMode != null) {
            oxyInfo.setBoValue(oxygenMode.getBloodOxygen());
            oxyInfo.setTimestamp(oxygenMode.getTimestamp());
            oxyInfo.setStrDate(TimeUtils.getTimeByyyyyMMddhhmmss(oxygenMode.getTimestamp()));
        }
        return oxyInfo;
    }

    @Override
    public BPInfo getBloodPressureLastData(String deviceId, String userId) {
        BloodPressureMode bloodPressureMode = deviceMeasuredDActionation.findBloodPressureMode(deviceId, userId, 0);
        BPInfo bpInfo = new BPInfo();
        Logger.myLog("--------getBloodPressureLastData:=" + bloodPressureMode == null ? "" : bloodPressureMode.toString());
        if (bloodPressureMode != null) {
            bpInfo.setSpValue(bloodPressureMode.getSystolicBloodPressure());
            bpInfo.setDpValue(bloodPressureMode.getDiastolicBloodPressure());
            bpInfo.setDeviceId(bloodPressureMode.getDeviceId());
            bpInfo.setUserId(bloodPressureMode.getUserId());
            bpInfo.setTimestamp(bloodPressureMode.getTimestamp());
            bpInfo.setStrDate(TimeUtils.getTimeByyyyyMMddhhmmss(bloodPressureMode.getTimestamp()));
            bpInfo.setWristbandBloodPressureId(bloodPressureMode.getWristbandBloodPressureId());
        }
        return bpInfo;
    }

    @Override
    public TempInfo getTempInfoeLastData(String deviceId, String userId) {
        Device_TempTable bloodPressureMode = deviceMeasuredDActionation.findTempMode(deviceId, userId, 0);
        IW311SettingModel w311ModelSetting = new W311ModelSettingImpl();
        DeviceTempUnitlTable table = w311ModelSetting.getTempUtil(userId, deviceId);

        Logger.myLog("findTempMode: bloodPressureMode:" + deviceId + ",userId:" + userId + "6----" + bloodPressureMode);

        TempInfo info = new TempInfo();
        if (bloodPressureMode != null) {
            info.setFahrenheit(bloodPressureMode.getFahrenheit());
            info.setCentigrade(bloodPressureMode.getCentigrade());
            info.setStrDate(TimeUtils.getTimeByyyyyMMddhhmmss(bloodPressureMode.getTimestamp()));
            info.setTimestamp(bloodPressureMode.getTimestamp());
            if (table != null) {
                info.setTempUnitl(table.getTempUnitl());
            } else {
                info.setTempUnitl("0");
            }
            info = setState(info);
        }
        Logger.myLog("findTempMode: info:" + info);
        return info;
    }


    public TempInfo setState(TempInfo info) {

        float currentC = 0.0f;
        String strCurrentC = "";
        strCurrentC = info.getCentigrade();
        if (TextUtils.isEmpty(strCurrentC)) {
            strCurrentC = "0";
        }
        currentC = Float.parseFloat(strCurrentC);
        Logger.myLog("currentC:" + currentC);
        if (currentC < 37.3f) {
            info.setState(UIUtils.getString(R.string.temp_norm_temp_state));
            info.setColor(UIUtils.getColor(R.color.color_temp_normal));
            info.setResId(1);
        } else if (currentC >= 37.3f && currentC < 38.1f) {
            info.setState(UIUtils.getString(R.string.temp_low_fever_state));
            info.setColor(UIUtils.getColor(R.color.color_temp_low_fever));
            info.setResId(2);
        } else if (currentC >= 38.1f) {
            info.setState(UIUtils.getString(R.string.temp_high_fever_state));
            info.setColor(UIUtils.getColor(R.color.color_temp_high_fever));
            info.setResId(3);
        }

        return info;
    }

    @Override
    public OnceHrInfo getOneceHrLastData(String deviceId, String userId) {
        OneceHrMode oxygenMode = deviceMeasuredDActionation.findOnecHrMode(deviceId, userId, 0);
        Logger.myLog("getOxygenLastData=:" + oxygenMode + "deviceId" + deviceId + "userId:" + userId);
        OnceHrInfo oxyInfo = new OnceHrInfo();
        if (oxygenMode != null) {
            oxyInfo.setHeartValue(oxygenMode.getBloodOxygen() + "");
            oxyInfo.setTimestamp(oxygenMode.getTimestamp());
            oxyInfo.setStrDate(TimeUtils.getTimeByyyyyMMddhhmmss(oxygenMode.getTimestamp()));
        }
        return oxyInfo;
    }


    @Override
    public OnceHrInfo getOnceHrInfo(String deviceId, String userId) {
        OneceHrMode oneceHrMode = deviceMeasuredDActionation.findOnecHrMode(deviceId, userId, 0);

        OnceHrInfo onceHrInfo = new OnceHrInfo();
        if (oneceHrMode != null) {
            onceHrInfo.setHeartValue(oneceHrMode.getBloodOxygen() + "");
            onceHrInfo.setTimestamp(oneceHrMode.getTimestamp());
            onceHrInfo.setStrDate(TimeUtils.getTimeByyyyyMMddhhmmss(oneceHrMode.getTimestamp()));
        }
        return null;
    }


    public WatchSleepDayData parseSleep(W81DeviceDetailData w81DeviceDetailData) {

        boolean isF18 = DeviceTypeUtil.isContainF18(w81DeviceDetailData.getDeviceId());
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        Logger.myLog("parseSleep:--------------------------" + w81DeviceDetailData);
        if (w81DeviceDetailData != null) {
            watchSleepDayData.setTotalSleepTime(w81DeviceDetailData.getTotalTime());
            watchSleepDayData.setDateStr(w81DeviceDetailData.getDateStr());
            watchSleepDayData.setSporadicNapSleepTime(0);
            watchSleepDayData.setSporadicNapSleepTime(0);
            watchSleepDayData.setSporadicNapSleepTimes(0);
            watchSleepDayData.setSporadicNapSleepTimeStr(w81DeviceDetailData.getDateStr());


            ArrayList<SleepInfo> sleepList = new ArrayList<>();
            int[] allData = new int[1440];
            if (!TextUtils.isEmpty(w81DeviceDetailData.getSleepArray())) {
                ArrayList<ArrayList<String>> sleepArry = new Gson().fromJson(w81DeviceDetailData.getSleepArray(), ArrayList.class);
                for (int i = 0; i < sleepArry.size(); i++) {
                    SleepInfo sleepInfo = new SleepInfo();
                    sleepInfo.dataType = Integer.parseInt(sleepArry.get(i).get(0));
                    sleepInfo.startTime = Integer.parseInt(sleepArry.get(i).get(1));
                    sleepInfo.endTime = Integer.parseInt(sleepArry.get(i).get(2));
                    sleepInfo.totalTime = Integer.parseInt(sleepArry.get(i).get(3));
                    sleepList.add(sleepInfo);
                }


                int light = 0;
                int deep = 0;
                int awakTime = 0;

                try {
                    int startIndex = 0;
                    ArrayList<SporadicNapData> sporadicNapDataArrayList = new ArrayList<SporadicNapData>();
                    for (int i = 0; i < sleepList.size(); i++) {
                        int startTime = sleepList.get(i).startTime;
                        int totalTime = sleepList.get(i).totalTime;
                        int dataType = sleepList.get(i).dataType;

                        for (int j = startTime; j < startTime + totalTime; j++) {
                            if (dataType == (isF18 ? 1 : CRPSleepInfo.SLEEP_STATE_RESTFUL)) {
                                deep++;
                            } else if (dataType == (isF18 ? 2 : CRPSleepInfo.SLEEP_STATE_LIGHT)) {
                                light++;
                            } else if (dataType == (isF18 ? 3 : CRPSleepInfo.SLEEP_STATE_LIGHT)) {
                                awakTime++;
                            }
                            //   Logger.myLog("allData[" + j + "-----" + startTime + ",totalTime:" + totalTime + ",dataType:" + dataType + ",allData.lenth" + allData.length);
                            if (j >= 1200) {
                                allData[j - 1200] = dataType == 0 ? 4 : dataType;
                            } else {
                                allData[j + 240] = dataType == 0 ? 4 : dataType;
                            }


                        }
                    }

                    for (int j = 0; j < allData.length; j++) {
                        if (!(isEquals(allData[startIndex], allData[j]))) {
                            Logger.myLog("index == " + j);
                            SporadicNapData sporadicNapData = new SporadicNapData();
                            int sleepInterval = j - startIndex;   //睡眠区间时间
                            int state = (allData[startIndex] == 0) ? 0 : 1;
                            //后面一个状态已经不同了，不能将后一个状态归类为上一种中
                            String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j - 1);//通过startIndex和当前index j来判断时间区域
                            startIndex = j;//当不相同的情况下，更新开始index
                            sporadicNapData.setSleepTimeStr(sleepDetailStr);
                            sporadicNapData.setState(state);
                            sporadicNapData.setTime(sleepInterval);
                            sporadicNapDataArrayList.add(sporadicNapData);
                        } else {
                            //会有一种情况，后面的都是一种睡眠时
                            if (j == 1439) {
                                //到最后都是一种状态时,可以将最后一种状态归进去
                                SporadicNapData sporadicNapData = new SporadicNapData();
                                int sleepInterval = j - startIndex;   //睡眠区间时间
                                int state = (allData[startIndex] == 0) ? 0 : 1;
                                String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//通过startIndex和当前index j来判断时间区域
                                startIndex = j;//当不相同的情况下，更新开始index
                                sporadicNapData.setSleepTimeStr(sleepDetailStr);
                                sporadicNapData.setState(state);
                                sporadicNapData.setTime(sleepInterval);
                                sporadicNapDataArrayList.add(sporadicNapData);
                            }
                        }
                    }
                    // Logger.myLog("getStrDateSleepData:" + Arrays.toString(allData));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    watchSleepDayData.setAwakeSleepTime(awakTime);
                    watchSleepDayData.setLightLV1SleepTime(light);
                    watchSleepDayData.setDeepSleepTime(deep);
                    watchSleepDayData.setSleepArry(allData);
                    w81DeviceDetailData.setLightTime(light);
                    return watchSleepDayData;
                }
            } else {
                watchSleepDayData.setSleepArry(allData);
                watchSleepDayData.setSporadicNapSleepTime(0);
                watchSleepDayData.setAwakeSleepTime(0);
                watchSleepDayData.setSporadicNapSleepTime(0);
            }
        }
        return watchSleepDayData;
    }

    private boolean isEquals(int i, int i1) {
        return ((i == 0) && (i1 == 0)) ||
                ((i == CRPSleepInfo.SLEEP_STATE_LIGHT || i == CRPSleepInfo.SLEEP_STATE_RESTFUL || i == 4) && (i1 == CRPSleepInfo.SLEEP_STATE_LIGHT || i1 == CRPSleepInfo.SLEEP_STATE_RESTFUL || i1 == 4));
    }

}
