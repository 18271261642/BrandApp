package com.isport.brandapp.device.watch.watchModel;

import android.text.TextUtils;
import android.text.format.DateUtils;
import com.google.gson.Gson;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_24HDataModelAction;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.db.HeartRateMainData;
import com.isport.brandapp.home.bean.db.SleepMainData;
import com.isport.brandapp.home.bean.db.StandardMainData;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.SporadicNapData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.bean.http.WristbandHrHeart;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.bean.StepBean;
import com.isport.brandapp.util.DateTimeUtils;
import com.isport.brandapp.util.DeviceTypeUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import bike.gymproject.viewlibray.SleepFormatUtils;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.action.BaseAction;
import brandapp.isport.com.basicres.action.WatchRealTimeDataAction;
import brandapp.isport.com.basicres.action.WatchTargetBeanAction;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.WatchRealTimeData;
import brandapp.isport.com.basicres.entry.WatchTargetBean;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class W516Model implements IW516Model {


    private static final String TAG = "W516Model";
    
    public final static int allDayNoData = 0;
    public final static int day20beforhasData = 1;
    public final static int day20afterhasData = 2;
    public final static int alldayData = 3;

    @Override
    public Wristbandstep getLastSprotData() {
        Watch_W516_24HDataModel model = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        Wristbandstep wristbandstep = new Wristbandstep();
        if (model != null) {
            //  Logger.myLog("getLastSprotData" + model.toString());
            wristbandstep = parWristBandStep(model);
        }
        return wristbandstep;
    }

    /**
     * 获取手表最近两天的数据 如果有实时数据需要把实时数据去出来
     *
     * @return
     */
    @Override
    public WatchSportMainData getWatchStepLastTwoData(boolean isConnect) {


        // List<Watch_W516_24HDataModel> modellist = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId_Last_Two(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID, DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
        Watch_W516_24HDataModel model = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID);

        WatchRealTimeData watchRealTimeData1 = WatchRealTimeDataAction.getWatchRealTimeData
                (bike.gymproject.viewlibray.pickerview.utils.DateUtils
                        .getYMD(System.currentTimeMillis()), AppConfiguration.braceletID);

        //1、不管今天有无数据，不管有无连接手环，都显示当天数据

        Logger.myLog("getWatchStepLastTwoData:" + watchRealTimeData1);


        WatchSportMainData wristbandstep = new WatchSportMainData();
        if (model != null || watchRealTimeData1 != null) {
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            if (model != null) {
                Wristbandstep wristbandstep2 = parWristBandStep(model);
                wristbandstep.setDistance(wristbandstep2.getStepKm());
                wristbandstep.setCal(wristbandstep2.getCalorie());
                wristbandstep.setStep(wristbandstep2.getStepNum());
            }

            if (watchRealTimeData1 != null) {
                wristbandstep.setDistance(CommonDateUtil.formatTwoPoint(watchRealTimeData1.getStepKm()));
                wristbandstep.setCal(CommonDateUtil.formatInterger(watchRealTimeData1.getCal()));
                wristbandstep.setStep(CommonDateUtil.formatInterger(watchRealTimeData1.getStepNum()));
            }
            return wristbandstep;
        } else {
            if (DeviceTypeUtil.isContainWatch()) {
                wristbandstep.setLastSyncTime(System.currentTimeMillis());
                wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
                wristbandstep.setCompareStep("0");
                wristbandstep.setCompareStep("0");
                wristbandstep.setCal("0");
                wristbandstep.setStep("0");
                wristbandstep.setDistance("0.00");
                return wristbandstep;
            } else {
                wristbandstep.setLastSyncTime(System.currentTimeMillis());
                wristbandstep.setDateStr("");
                wristbandstep.setCompareStep("0");
                wristbandstep.setStep(UIUtils.getString(R.string.no_data));
                wristbandstep.setCal(UIUtils.getString(R.string.no_data));
                wristbandstep.setDistance(UIUtils.getString(R.string.no_data));
                return wristbandstep;
            }
        }


       /* if (model != null || watchRealTimeData1 != null) {
            if (modellist != null) {
                modellist.add(0, model);
            } else {
                modellist = new ArrayList<>();
                modellist.add(0, model);
            }
            WatchSportMainData wristbandstep = new WatchSportMainData();
            if (modellist != null && modellist.size() > 0) {
                //Logger.myLog("getLastSprotData" + model.toString());
                Wristbandstep wristbandstep1 = parWristBandStep(modellist.get(0));
                if (watchRealTimeData1 != null) {
                    wristbandstep.setDistance(CommonDateUtil.formatTwoPoint(watchRealTimeData1.getStepKm()));
                    wristbandstep.setCal(CommonDateUtil.formatInterger(watchRealTimeData1.getCal()));
                    wristbandstep.setStep(CommonDateUtil.formatInterger(watchRealTimeData1.getStepNum()));

                } else {
                    if (TextUtils.isEmpty(wristbandstep1.getStrDate())) {//说明当天没有历史数据
                        //当天没有数据
                        wristbandstep.setDistance("0.00");
                        wristbandstep.setCal("0");
                        wristbandstep.setStep("0");
                    } else {
                        wristbandstep.setDistance(wristbandstep1.getStepKm());
                        wristbandstep.setCal(wristbandstep1.getCalorie());
                        wristbandstep.setStep(wristbandstep1.getStepNum());
                    }
                }
                wristbandstep.setLastSyncTime(System.currentTimeMillis());
                wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
                if (modellist.size() >= 2) {
                    Wristbandstep wristbandstep2 = null;
                    wristbandstep2 = parWristBandStep(modellist.get(1));
                    if (TextUtils.isEmpty(wristbandstep2.getStepNum())) {
                        wristbandstep2.setStepNum("0");
                    }
                    wristbandstep.setCompareStep(Long.parseLong(wristbandstep.getStep()) - Long.parseLong(wristbandstep2.getStepNum()) + "");
                } else {
                    wristbandstep.setCompareStep(Long.parseLong(wristbandstep.getStep()) + "");
                }

            } else {
                wristbandstep.setCompareStep("0");
                wristbandstep.setCal("0");
                wristbandstep.setDistance("0.00");
            }
            return wristbandstep;
        } else if (modellist != null && modellist.size() >= 1) {
            //当天的数据为空,
            WatchSportMainData wristbandstep = new WatchSportMainData();
            wristbandstep.setStep(UIUtils.getString(R.string.no_data));
            wristbandstep.setCal(UIUtils.getString(R.string.no_data));
            wristbandstep.setDistance(UIUtils.getString(R.string.no_data));
            Wristbandstep wristbandstep2 = null;
            wristbandstep2 = parWristBandStep(modellist.get(0));
            wristbandstep.setCompareStep(0 - Long.parseLong(TextUtils.isEmpty(wristbandstep2.getStepNum()) ? "0" : wristbandstep2.getStepNum()) + "");
            wristbandstep.setLastSyncTime(System.currentTimeMillis());
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            return wristbandstep;
        } else {
            WatchSportMainData wristbandstep = new WatchSportMainData();
            wristbandstep.setLastSyncTime(System.currentTimeMillis());
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            wristbandstep.setCompareStep("0");
            wristbandstep.setStep(UIUtils.getString(R.string.no_data));
            wristbandstep.setCal(UIUtils.getString(R.string.no_data));
            wristbandstep.setDistance(UIUtils.getString(R.string.no_data));
            return wristbandstep;
        }*/


    }

    /**
     * 查询最近两天有心率的数据
     *
     * @return
     */
    @Override
    public HeartRateMainData getWatchHeartRteLastTwoData() {
        HeartRateMainData heartRateMainData = new HeartRateMainData();
        List<Watch_W516_24HDataModel> model = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId_Last_Two_HR(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);

        if (model != null && model.size() > 0) {
            //Logger.myLog("getLast HeartRte Data" + model.toString());
            Watch_W516_24HDataModel watch_w516_24HDataModel1 = model.get(model.size()-1);
            heartRateMainData.setLastSyncTime(watch_w516_24HDataModel1.getTimestamp());
            heartRateMainData.setHeartRate(watch_w516_24HDataModel1.getAvgHR());
            heartRateMainData.setDateStr(watch_w516_24HDataModel1.getDateStr());
                /*if (model.size() >= 2) {
                    Watch_W516_24HDataModel watch_w516_24HDataModel2 = model.get(1);
                    heartRateMainData.setCompareHeartRate(watch_w516_24HDataModel1.getAvgHR() - watch_w516_24HDataModel2.getAvgHR());
                } else {
                    heartRateMainData.setCompareHeartRate(0);
                }*/
        } else {
            heartRateMainData.setDateStr("");
            heartRateMainData.setCompareHeartRate(0);
            heartRateMainData.setHeartRate(0);
        }
        return heartRateMainData;
    }

    /**
     * 获取两周的数据
     *
     * @param
     * @return
     */
    @Override
    public Wristbandstep getWatchDayData(String strDate) {
        Watch_W516_24HDataModel model = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), strDate, AppConfiguration.braceletID);
        Wristbandstep wristbandstep = new Wristbandstep();

        if (model != null) {
            Logger.myLog(model.toString());
            wristbandstep = parWristBandStep(model);
        }
        Logger.myLog(wristbandstep.toString());
        return wristbandstep;
    }


    @Override
    public void getSumData() {

    }

    @Override
    public void getDetailData() {

    }

    /**
     * 获取有数据天list
     *
     * @param strDate
     * @return
     */
    @Override
    public ArrayList<String> getMonthData(String strDate) {
        ArrayList<String> dataList = (ArrayList<String>) Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataMode_CurrentMonth_DateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        for (int i = 0; i < dataList.size(); i++) {
            Logger.myLog("watch:" + strDate + ":" + dataList.get(i));
        }
        return dataList;
    }

    /**
     * 获取有睡眠数据天list
     *
     * @param strDate
     * @return
     */
    @Override
    public ArrayList<String> getSleepMonthData(String deviceId, String strDate) {


        //需要查两天的数据然后判断是否有数据  2019-08
        String[] strings = strDate.split("-");
        int strY = Integer.parseInt(strings[0]);
        int strM = Integer.parseInt(strings[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, strY);
        calendar.set(Calendar.MONTH, strM - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        ArrayList<String> dataList = new ArrayList<>();

        int monthCount = DateUtil.getMonthOfDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);

        for (int i = 0; i < monthCount; i++) {
            if (i != 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            WatchSleepDayData watchSleepDayData = getWatchSleepDayData(DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "yyyy-MM-dd"), deviceId);

            if (watchSleepDayData != null) {
                if (watchSleepDayData.getTotalSleepTime() > 0) {
                    dataList.add(watchSleepDayData.getDateStr());
                }
            }
        }
        return dataList;
       /* /

        ArrayList<String> dataList = (ArrayList<String>) Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        for (int i = 0; i < dataList.size(); i++) {
            Logger.myLog("watch:" + strDate + ":" + dataList.get(i));
        }
        */
    }

    /**
     * @param strDate
     * @return
     */
    @Override
    public WatchSleepDayData getWatchSleepDayData(String strDate, String deviceId) {
        Watch_W516_24HDataModel currentModel = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), strDate, deviceId);
        Watch_W516_24HDataModel lastmModel = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), TimeUtils.getLastDayStr(strDate), deviceId);

        if (currentModel == null && lastmModel == null) {
            return null;
        }
        WatchSleepDayData watchSleepDayData = parWatchSleepDayData(lastmModel, currentModel, strDate);
        return watchSleepDayData;
    }

    /**
     * 查询最近一个有数据的天
     *
     * @return
     */
    @Override
    public WatchSleepDayData getWatchSleepDayLastData(String deviceId) {
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();
        Watch_W516_24HDataModel currentModel = Watch_W516_24HDataModelAction.findLastTwoDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        if (currentModel != null) {

            int type = parSleepDayType(currentModel);
            switch (type) {
                case day20beforhasData:
                    Watch_W516_24HDataModel lastmModel = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), TimeUtils.getLastDayStr(currentModel.getDateStr()), deviceId);
                    if(lastmModel == null){
                        return  analysisSleep(currentModel,deviceId);
                    }
                    watchSleepDayData = parWatchSleepDayLastData(lastmModel, currentModel);
                    break;
                case day20afterhasData:
                case alldayData:
                    String nextStr = TimeUtils.getNextDayStr(currentModel.getDateStr());
                    Watch_W516_24HDataModel nextmModel = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), nextStr, deviceId);
                    if (nextmModel == null) {
                        nextmModel = new Watch_W516_24HDataModel();
                        nextmModel.setDateStr(nextStr);
                    }
                    watchSleepDayData = parWatchSleepDayLastData(currentModel, nextmModel);
                    break;

            }
        }

        return watchSleepDayData;
    }



    private WatchSleepDayData analysisSleep(Watch_W516_24HDataModel w24HData,String deviceId){
        String sleepArrayStr = w24HData.getSleepArray();
        int[] sleepArray = new Gson().fromJson(sleepArrayStr,int[].class);

        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();
        watchSleepDayData.setDateStr(DateTimeUtils.getCurrentDate());

        watchSleepDayData.setDateStr(w24HData.getDateStr());

        Logger.myLog(TAG,"---111----睡眠数组="+sleepArray.length+"\n"+Arrays.toString(sleepArray));

        //处理睡眠数组，当天睡眠从0点到9点，最多 9 * 60个点
        int[] tmpSleepArray = new int[9 * 60];

        System.arraycopy(sleepArray,0,tmpSleepArray,0,tmpSleepArray.length);

        int yesSleepCount = 4 * 60;
        int[] resultSleepByte = new int[yesSleepCount + tmpSleepArray.length];

        System.arraycopy(tmpSleepArray,0,resultSleepByte,yesSleepCount,tmpSleepArray.length);

        Logger.myLog(TAG,"-----处理的睡眠="+resultSleepByte.length+"\n"+new Gson().toJson(resultSleepByte));
        watchSleepDayData.setSleepArry(resultSleepByte);
        //找出第一个不为0的元素下标
        int startIndex = 0;
        for(int i = 0;i<tmpSleepArray.length;i++){
            if(tmpSleepArray[i] != 0){
                startIndex = i;
                break;
            }
        }

        //开始的睡眠时间
        int startTime = startIndex +1;

        //结束时间 倒序
        int endIndex = 0;
        for(int k= tmpSleepArray.length-1;k>=0;k--){
            if(tmpSleepArray[k] !=0){
                endIndex = k;
             break;
            }
        }

        //结束时间
        int endTime = endIndex +1;

        int deepSleepTime = 0;
        int totalSleepTime = 0;
        int lightLV2SleepTime = 0;
        int lightLV1SleepTime = 0;
        int awakeSleepTime = 0;

        for(int n = 0;n<tmpSleepArray.length;n++){
            int sleepValue = tmpSleepArray[n];
            if (sleepValue == 250) {
                //深睡
                Logger.myLog("深睡");
                deepSleepTime++;
                totalSleepTime++;
            }

             if (sleepValue == 251) {
                //浅睡 level 2
                // Logger.myLog("浅睡 level 2");
                lightLV2SleepTime++;
                totalSleepTime++;

            }


              if (sleepValue == 252) {
                //浅睡 level 1
                //Logger.myLog("浅睡 level 1");
                lightLV1SleepTime++;
                totalSleepTime++;

            }
              if (sleepValue == 253) {
                //清醒
                //Logger.myLog("清醒");
                awakeSleepTime++;
                totalSleepTime++;
            }
        }

        watchSleepDayData.setDeepSleepTime(deepSleepTime);
        watchSleepDayData.setAwakeSleepTime(awakeSleepTime);
        watchSleepDayData.setTotalSleepTime(totalSleepTime);
        watchSleepDayData.setLightLV1SleepTime(lightLV1SleepTime);
        watchSleepDayData.setLightLV2SleepTime(lightLV2SleepTime);

        Logger.myLog(TAG,"-------处理当天睡眠="+watchSleepDayData.toString());

        return watchSleepDayData;

    }





    /**
     * @param
     * @param
     * @return
     */

    @Override
    public Wristbandstep getWatchLastMonthData(String userId, String deviceId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);


        String strEndTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));

        //calendar.clear();

        calendar.add(Calendar.DAY_OF_MONTH, -30);

        String strStartTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));

        List<Watch_W516_24HDataModel> model = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataMode_last_month(userId, strStartTime, strEndTime, deviceId);

        // NSString *avgStepString = [NSString stringWithFormat:@"%.0f",stepCount/30.0];
        //    NSString *avgDistanceString = [NSString stringWithFormat:@"%.2f",distanceCount/30.0/1000.0];
        //    NSString *oilString = [NSString stringWithFormat:@"%.2f",distanceCount/1000.0 * 0.0826];
        //    NSString *fatString = [NSString stringWithFormat:@"%.2f",calorieCount * 0.127];


        float avgSumStep, avgSumDis, avgDis, avgFat;
        long sumStep = 0;

        Wristbandstep wristbandstep = new Wristbandstep();
        if (model != null) {
            for (int i = 0; i < model.size(); i++) {
                sumStep += model.get(i).getTotalSteps();
            }

            avgSumStep = sumStep / 30.0f;


            UserInfoBean loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
            ArrayList<String> list = StepArithmeticUtil.stepsAvgConversionDistance(Float.parseFloat(loginBean.getHeight()), loginBean.getGender(), sumStep, 30);
            avgFat = Float.parseFloat(StepArithmeticUtil.stepsConversionCalories(Float.parseFloat(loginBean.getWeight()), sumStep)) * 0.127f;

            wristbandstep.setAvgStep(CommonDateUtil.formatInterger(avgSumStep));
            wristbandstep.setSumGasoline(list.get(0));
            wristbandstep.setAvgDis(list.get(1));
            wristbandstep.setSumFat(CommonDateUtil.formatInterger(avgFat));
        } else {
            wristbandstep.setAvgStep("0");
            wristbandstep.setSumGasoline("0");
            wristbandstep.setAvgDis("0");
            wristbandstep.setSumFat("0");
        }

        // wristbandstep.setAvgDis();


        return wristbandstep;
    }

    @Override
    public WatchTargetBean getWatchTargetStep(String deviceId, String userId) {
        WatchTargetBean watchTargetBean = WatchTargetBeanAction.getWatchTargetBean(userId, deviceId);

        if (watchTargetBean == null) {
            WatchTargetBean watchTargetBeanTemp = new WatchTargetBean();
            watchTargetBeanTemp.setDeviceId(deviceId);
            watchTargetBeanTemp.setUserId(userId);
            watchTargetBeanTemp.setTargetStep(6000);
            BaseAction.getWatchTargetBeanDao().insertOrReplace(watchTargetBeanTemp);
        }
        return watchTargetBean;

    }

    @Override
    public Wristbandstep calStepToKmAndCal(long currentstep) {
        Wristbandstep wristbandstep = new Wristbandstep();
        UserInfoBean loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        wristbandstep.setStrDate(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()));
        wristbandstep.setStepNum(currentstep + "");
        wristbandstep.setStepKm(StepArithmeticUtil.stepsConversionDistance(Float.parseFloat(loginBean.getHeight()), loginBean.getGender(), currentstep));
        wristbandstep.setCalorie(StepArithmeticUtil.stepsConversionCalories(Float.parseFloat(loginBean.getWeight()), currentstep));
        return wristbandstep;
    }

    @Override
    public WristbandHrHeart getDayHrData(String strDate) {
        Watch_W516_24HDataModel model = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), strDate, AppConfiguration.braceletID);
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
        if (model != null) {
            wristbandHrHeart = parseHrData(model);

        }
        return wristbandHrHeart;
    }

    @Override
    public WristbandHrHeart getLastDayHrData() {
        List<Watch_W516_24HDataModel> list = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId_Last_Two_HR(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
        if (list != null && list.size() > 0) {
            wristbandHrHeart = parseHrData(list.get(0));
        }
        return wristbandHrHeart;
    }


    @Override
    public ArrayList<String> getMonthHrDataToStrDate(String strDate) {
        List<Watch_W516_24HDataModel> dataList = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataMode_CurrentMonth_Hr_DateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        ArrayList<String> str = new ArrayList<>();

        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                Integer[] hrArry = new Gson().fromJson(dataList.get(i).getHrArray(), Integer[].class);
                if (hrArry == null) {
                    continue;
                }
                ArrayList<Integer> arrayList = new ArrayList<Integer>(Arrays.asList(hrArry));

                //Logger.myLog("getMonthHrDataToStrDate arrayList" + arrayList);
                if (arrayList == null || (arrayList != null && Collections.max(arrayList) < 30)) {
                    continue;
                }
                str.add(dataList.get(i).getDateStr());
            }
        }

        //Logger.myLog("getMonthHrDataToStrDate str" + str);
        return str;
    }

    @Override
    public StandardMainData getWatchWeekData(String userId, int targetStep) {

        String strdeviceId = AppConfiguration.braceletID;
        //当前周和上一周数据的比较
        //targetStep = JkConfiguration.WATCH_GOAL;
        JkConfiguration.WATCH_GOAL = targetStep;
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String strEndTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));

        List<Watch_W516_24HDataModel> model = new ArrayList<>();

        String strStartTime;
        StandardMainData standardMainData = new StandardMainData();
        standardMainData.setLastSyncTime(0);
        int target = 0;
        ArrayList<StepBean> stepList = new ArrayList<>();
        stepList.clear();
        for (int i = week; i >= 1; i--) {
            strStartTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
            Watch_W516_24HDataModel tempmodel = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(userId, strStartTime, strdeviceId);
            if (tempmodel == null) {
                stepList.add(new StepBean(0, calendar.getTimeInMillis(), strStartTime));
            } else {
                standardMainData.setLastSyncTime(tempmodel.getTimestamp());
                if (DateUtils.isToday(tempmodel.getTimestamp())) {
                    if (tempmodel.getTotalSteps() >= targetStep) {
                        standardMainData.setHasReach(true);
                    } else {
                        standardMainData.setHasReach(false);
                    }
                }
                stepList.add(new StepBean(tempmodel.getTotalSteps(), calendar.getTimeInMillis(), strStartTime));
                if (tempmodel.getTotalSteps() >= targetStep) {
                    target++;
                }
                // Logger.myLog(tempmodel.getTotalSteps() + "tempmodel == " + tempmodel.toString());
            }
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        ArrayList<StepBean> stepListDesc = new ArrayList<>();

        for (int i = 0; i < stepList.size(); i++) {
            stepListDesc.add(stepList.get(stepList.size() - i - 1));
        }


        String strLastweekEndTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        String strrLastWeekStartTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        List<Watch_W516_24HDataModel> lastmodel = Watch_W516_24HDataModelAction.findWatch_W516_Watch_W516_24HDataMode_last_month_Asc(userId, strrLastWeekStartTime, strLastweekEndTime, strdeviceId);

        standardMainData.setDays(target);
        standardMainData.setListDays(stepListDesc);
        target = 0;
        if (lastmodel != null && lastmodel.size() > 0) {
            for (int i = 0; i < lastmodel.size(); i++) {
                if (lastmodel.get(i).getTotalSteps() >= targetStep) {
                    target++;
                }
            }
        }
        standardMainData.setCompareDays(standardMainData.getDays() - target);

        return standardMainData;
    }

    @Override
    public WatchRealTimeData getRealWatchData(String deviceId) {
        WatchRealTimeData watchRealTimeData1 = WatchRealTimeDataAction.getWatchRealTimeData
                (bike.gymproject.viewlibray.pickerview.utils.DateUtils
                        .getYMD(System.currentTimeMillis()), deviceId);
        return watchRealTimeData1;
    }

    @Override
    public void saveDeviceSedentaryReminder(String devcieId, String userId, int times, String starTime, String endTime, boolean enable) {
        Watch_W516_SedentaryModel watch_w516_sedentaryModels = new Watch_W516_SedentaryModel();
        watch_w516_sedentaryModels.setDeviceId(devcieId);
        watch_w516_sedentaryModels.setUserId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        watch_w516_sedentaryModels.setLongSitTimeLong(times);
        watch_w516_sedentaryModels.setLongSitStartTime(starTime);
        watch_w516_sedentaryModels.setLongSitEndTime(endTime);
        watch_w516_sedentaryModels.setIsEnable(enable);
        ParseData.saveOrUpdateWatchW516Sedentary(watch_w516_sedentaryModels);
        Logger.myLog("saveDb watch_w516_sedentaryModels" + watch_w516_sedentaryModels.toString());
    }


    public Wristbandstep parWristBandStep(Watch_W516_24HDataModel model) {
        Wristbandstep wristbandstep = new Wristbandstep();

        try {

            UserInfoBean loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
            if (model == null || loginBean == null) {
                return wristbandstep;
            }

            if (model.getDeviceId().contains("W516")) {
                wristbandstep.setStepKm(StepArithmeticUtil.stepsConversionDistance(Float.parseFloat(loginBean.getHeight()), loginBean.getGender(), model.getTotalSteps()));
                wristbandstep.setCalorie(StepArithmeticUtil.stepsConversionCalories(Float.parseFloat(loginBean.getWeight()), model.getTotalSteps()));
            } else {
                //W556显示的距离是向下取整
                float mStep = CommonDateUtil.formatFloor(model.getTotalDistance(), true);
                wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(mStep));
                wristbandstep.setCalorie(model.getTotalCalories() + "");
            }
            wristbandstep.setStepNum(model.getTotalSteps() + "");

            //Logger.myLog("parWristBandStep model.getTotalDistance()" + model.getTotalDistance() + "model.getTotalCalories()" + model.getTotalCalories());

            wristbandstep.setLastServerTime(model.getTimestamp());
            wristbandstep.setStrDate(model.getDateStr());
            int[] stepArry = new Gson().fromJson(model.getStepArray(), int[].class);

            ArrayList<Integer> stepList = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                int addValue = 0;
                for (int j = 0; j < 60; j++) {
                    if ((j + 60 * i) < stepArry.length) {
                        addValue += stepArry[j + 60 * i];
                    }
                }
                stepList.add(addValue);
            }
        /*int sum = 0;
        if (stepArry != null) {
            for (int i = 0; i < stepArry.length; i++) {
                sum += stepArry[i];
                if ((i != 0 && i % 60 == 0) || i == stepArry.length - 1) {
                    stepList.add(sum);
                    sum = 0;
                }
            }
        }
        int sumSize = stepList.size();
        if (stepList.size() < 24) {
            for (int i = 0; i < 24 - sumSize; i++) {
                stepList.add(0);
            }
        }*/
            wristbandstep.setStepArry(stepList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return wristbandstep;
    }

    int c1200 = 1200;
    boolean isCurrentDay;

    public WatchSleepDayData parWatchSleepDayData(Watch_W516_24HDataModel lastModel, Watch_W516_24HDataModel currentModel, String strDate) {
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        //有可能当天为null
        watchSleepDayData.setDateStr(strDate);
       // Logger.myLog(TAG,"---今天昨天的睡眠数据parWatchSleepDayData="+strDate+"\n"+"今天="+currentModel.toString()+"\n"+" 昨天="+lastModel.toString());
        int[] m1440Result;//所有数据

        int[] m240Data = new int[240];//默认的240为0长度的填充数据
        int[] m1200Data = new int[1200];//默认的1200为0长度的填充数据

        for (int j = 0; j < c1200; j++) {
            if (j < 240) {
                m240Data[j] = 0;
            }
            m1200Data[j] = 0;
        }
        //点击查询当天是否有数据

        //查看前一天是否有数据

        //对数据进行整合


        ArrayList<String> dateList = new ArrayList<String>();
        Gson gson = new Gson();


        Watch_W516_24HDataModel watch_w516_24HDataModel = currentModel;//当天的数据
        isCurrentDay = false;

        if (watch_w516_24HDataModel != null) {
            String currentDate = watch_w516_24HDataModel.getDateStr();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
            Calendar calendar = Calendar.getInstance();
            String dateString = formatter.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String currentdateString = formatter.format(calendar.getTime());
            if (dateString.equals(currentDate)) {
                isCurrentDay = true;
            }
        }


        int[] ints;
        if (watch_w516_24HDataModel != null) {
            String sleepArray = watch_w516_24HDataModel.getSleepArray();
            ints = gson.fromJson(sleepArray, int[].class);
        } else {
            ints = null;
        }
        if (ints == null) {
            m1440Result = new int[1440];
        } else {
            if (ints.length > c1200) {
                m1440Result = new int[1440];
            } else {
                //当天的情况,会有小于的时候,不到20点的情况
                //当天也要填充满，因为UI是固定死的1440
//            m1440Result = new int[240 + ints.length];
                m1440Result = new int[1440];
            }
        }

        //不论当天是否有数据，都要查询昨天的数据，做数据整合
        //查询前一天的20-23.59数据，如果有则
        Watch_W516_24HDataModel lastDay24hData = lastModel;//前一天的数据
        if (lastDay24hData == null) {
            //为空
            System.arraycopy(m240Data, 0, m1440Result, 0, 240);
        } else {
            //不为空，取1200-1440的数据
            String sleepArray1 = lastDay24hData.getSleepArray();
            int[] intsLast = gson.fromJson(sleepArray1, int[].class);
            if (intsLast == null) {
                System.arraycopy(m240Data, 0, m1440Result, 0, 240);
            } else {
                if (intsLast.length > c1200) {
                    if (intsLast.length - c1200 <= 240) {
                        System.arraycopy(intsLast, c1200, m1440Result, 0, intsLast.length - 1200);
                    } else {
                        System.arraycopy(intsLast, c1200, m1440Result, 0, 240);
                    }
                }
            }
        }

        if (ints == null) {
            System.arraycopy(m1200Data, 0, m1440Result, 240, 1200);
        } else {
            if (ints.length > c1200) {
                System.arraycopy(ints, 0, m1440Result, 240, c1200);
            } else {
                //当天的情况,会有小于的时候,不到20点的情况
                System.arraycopy(ints, 0, m1440Result, 240, ints.length);
            }
        }

        int totalSleepTime = 0;//总睡眠时长，不包括清醒部分
        int deepSleepTime = 0;//深睡
        int lightLV1SleepTime = 0;//浅睡level 1
        int lightLV2SleepTime = 0;//浅睡level 2
        int awakeSleepTime = 0;//清醒
        int SporadicNapSleepTime = 0;//零星小睡分钟数
        String SporadicNapSleepTimeStr;//零星小睡时间段

        ArrayList<SporadicNapData> sporadicNapDataArrayList = new ArrayList<SporadicNapData>();

        int startIndex = 0;

        for (int j = 0; j < m1440Result.length; j++) {
            int sleepState = m1440Result[j];
            //为睡眠数据
            if (sleepState == 250) {
                //深睡
                Logger.myLog("深睡");
                deepSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 251) {
                //浅睡 level 2
                // Logger.myLog("浅睡 level 2");
                lightLV2SleepTime++;
                totalSleepTime++;

            } else if (sleepState == 252) {
                //浅睡 level 1
                //Logger.myLog("浅睡 level 1");
                lightLV1SleepTime++;
                totalSleepTime++;

            } else if (sleepState == 253) {
                //清醒
                //Logger.myLog("清醒");
                awakeSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 0) {
                //步数数据
                //awakeSleepTime++;
            }

            //判断当前值跟开始值是否一致，不一致把间隔算出来，把状态算出来，然后把当前值给startindex，然后比较下一个
            //前一个睡眠状态和后一个睡眠状态不相同时
            if (!(isEquals(m1440Result[startIndex], m1440Result[j]))) {
                Logger.myLog("index == " + j);
                SporadicNapData sporadicNapData = new SporadicNapData();
                int sleepInterval = j - startIndex;   //睡眠区间时间
                int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                //后面一个状态已经不同了，不能将后一个状态归类为上一种中
                String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//通过startIndex和当前index j来判断时间区域
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
                    int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                    String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//通过startIndex和当前index j来判断时间区域
                    startIndex = j;//当不相同的情况下，更新开始index
                    sporadicNapData.setSleepTimeStr(sleepDetailStr);
                    sporadicNapData.setState(state);
                    sporadicNapData.setTime(sleepInterval);
                    sporadicNapDataArrayList.add(sporadicNapData);
                }
            }
        }


        watchSleepDayData.setSleepArry(m1440Result);
        watchSleepDayData.setTotalSleepTime(totalSleepTime);
        watchSleepDayData.setDeepSleepTime(deepSleepTime);
        watchSleepDayData.setLightLV2SleepTime(lightLV2SleepTime);
        watchSleepDayData.setLightLV1SleepTime(lightLV1SleepTime);
        watchSleepDayData.setAwakeSleepTime(awakeSleepTime);

        //判断零星小睡,连续大于30分钟的清醒，然后连续的小于45分钟睡眠，最后连续的大于30分钟清醒
        //将睡眠分为清醒和睡眠段,然后对睡眠段做操作

        ArrayList<SporadicNapData> sporadicNapDataReslut = new ArrayList<SporadicNapData>();

        for (int j = 0; j < sporadicNapDataArrayList.size(); j++) {
            // Logger.myLog(sporadicNapDataArrayList.toString());
            SporadicNapData sporadicNapData = sporadicNapDataArrayList.get(j);
            int size = sporadicNapDataArrayList.size();
            if (sporadicNapData.getState() == 0 && sporadicNapData.getTime() > 30) {
                //当清醒大于30分钟时
                if (j + 1 <= size - 2) {
                    SporadicNapData sporadicNapDataMiddle = sporadicNapDataArrayList.get(j + 1);
                    if (sporadicNapDataMiddle.getState() == 1 && sporadicNapDataMiddle.getTime() < 45) {
                        //当中间段为睡眠数据，且睡眠时间小于45分钟时
                        if (j + 2 <= size - 1) {
                            SporadicNapData sporadicNapDataEnd = sporadicNapDataArrayList.get(j + 2);
                            if (sporadicNapDataEnd.getState() == 0 && sporadicNapDataEnd.getTime() > 30) {
                                //最后一段也是30分钟以上的清醒,那么将会认为中间这段时零星小睡部分
                                if (!isCurrentDay) {
                                    sporadicNapDataReslut.add(sporadicNapDataMiddle);
                                }
                            }
                        }
                    }
                }
            }
        }

        int time = 0;
        StringBuilder timeStr = new StringBuilder();
        timeStr.append("");
        for (int j = 0; j < sporadicNapDataReslut.size(); j++) {
            Logger.myLog("零星小睡 == " + sporadicNapDataReslut.toString());
            time += sporadicNapDataReslut.get(j).getTime();
            timeStr.append(sporadicNapDataReslut.get(j).getSleepTimeStr() + ",");
        }
        watchSleepDayData.setSporadicNapSleepTimes(sporadicNapDataReslut.size());
        watchSleepDayData.setSporadicNapSleepTime(time);
        if (sporadicNapDataReslut.size() == 1) {
            watchSleepDayData.setSporadicNapSleepTimeStr(sporadicNapDataReslut.get(0).getSleepTimeStr());
        } else {
            watchSleepDayData.setSporadicNapSleepTimeStr(timeStr.toString());
        }
        return watchSleepDayData;
    }

    public WatchSleepDayData parWatchSleepDayLastData(Watch_W516_24HDataModel lastModel, Watch_W516_24HDataModel currentModel) {
        //需要判断当天0-20点 和20点-23.59数据
        Gson gson = new Gson();
        Watch_W516_24HDataModel watch_w516_24HDataModel = currentModel;
        String sleepArray = watch_w516_24HDataModel.getSleepArray();
        String dateStr1 = watch_w516_24HDataModel.getDateStr();
        int[] ints = gson.fromJson(sleepArray, int[].class);
        List<Integer> todayDataList = new ArrayList<>();
        List<Integer> today20UpDataList = new ArrayList<>();
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        boolean isLast;//时候要拿昨天，或者拿明天的数据


        int[] todayData;
        int[] today20UpData;
        if (ints == null) {
            //说明没有数据
            todayData = new int[1200];
            isLast = true;
        } else {
            if (ints.length > 1200) {
                todayData = new int[1200];
                today20UpData = new int[ints.length - 1200];
                System.arraycopy(ints, 0, todayData, 0, 1200);
                System.arraycopy(ints, 1200, today20UpData, 0, ints.length - 1200);
                //需要区分两个时段,
                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
                for (int j = 0; j <= today20UpData.length - 1; j++) {
                    today20UpDataList.add(today20UpData[j]);
                }

                //先判断20点以后的数据，如果有就直接填充后一天的数据,全部为0，因为后一天没有数据
                if (Collections.max(today20UpDataList) > 0) {
                    //说明有数据
                    isLast = false;
                } else {
                    //没有数据，说明是要拿前一天数据
                    isLast = true;
                }

            } else {
                //当天的情况,会有小于的时候
                todayData = new int[ints.length];
                System.arraycopy(ints, 0, todayData, 0, ints.length);
                //小于的情况只有当天，那么就走底部的逻辑
                isLast = true;
            }
        }

        Watch_W516_24HDataModel mToday;
        Watch_W516_24HDataModel mLast;
        if (isLast) {
            //拿前一天数据
            mToday = currentModel;
            mLast = lastModel;
        } else {
            //要拿后一天的数据,后一天的数据全部为0
            mToday = null;
            mLast = currentModel;
            ints = null;

        }
        watchSleepDayData.setDateStr(isLast ? currentModel.getDateStr() : TimeUtils.getNextDayStr(currentModel.getDateStr()));
        Logger.myLog("parWatchSleepDayData");

        boolean currentDate = false;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String dateString = formatter.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String currentdateString = formatter.format(calendar.getTime());
        if (watchSleepDayData.getDateStr().equals(currentDate)) {
            isCurrentDay = true;
        }

        int[] m1440Result;//所有数据

        int[] m240Data = new int[240];//默认的240为0长度的填充数据
        int[] m1200Data = new int[1200];//默认的1200为0长度的填充数据

        for (int j = 0; j < 1200; j++) {
            if (j < 240) {
                m240Data[j] = 0;
            }
            m1200Data[j] = 0;
        }
        //点击查询当天是否有数据

        //查看前一天是否有数据

        //对数据进行整合

//        if (ints.length > 1200) {
        m1440Result = new int[1440];
//        } else {
        //当天的情况,会有小于的时候,不到20点的情况
        //当天也要填充满，因为UI是固定死的1440
//            m1440Result = new int[240 + ints.length];
        m1440Result = new int[1440];
//        }


        //不论当天是否有数据，都要查询昨天的数据，做数据整合
        //查询前一天的20-23.59数据，如果有则
        Watch_W516_24HDataModel lastDay24hData = mLast;//前一天的数据
        if (lastDay24hData == null) {
            //为空
            System.arraycopy(m240Data, 0, m1440Result, 0, 240);
        } else {
            //不为空，取1200-1440的数据
            String sleepArray1 = lastDay24hData.getSleepArray();
            int[] intsLast = gson.fromJson(sleepArray1, int[].class);
            if (intsLast == null) {
                System.arraycopy(m240Data, 0, m1440Result, 0, 240);
            } else {
                if (intsLast.length > 1200) {
                    if (intsLast.length - 1200 <= 240) {
                        System.arraycopy(intsLast, 1200, m1440Result, 0, intsLast.length - 1200);
                    } else {
                        System.arraycopy(intsLast, 1200, m1440Result, 0, 240);
                    }
                }
            }
        }

        /**
         * 当天的数据
         */
        if (ints == null) {
            //要直接复制全部为0
            System.arraycopy(m1200Data, 0, m1440Result, 240, 1200);
        } else {
            if (ints.length > 1200) {
                System.arraycopy(ints, 0, m1440Result, 240, 1200);
            } else {
                //当天的情况,会有小于的时候,不到20点的情况
                System.arraycopy(ints, 0, m1440Result, 240, ints.length);
            }
        }


        int totalSleepTime = 0;//总睡眠时长，不包括清醒部分
        int deepSleepTime = 0;//深睡
        int lightLV1SleepTime = 0;//浅睡level 1
        int lightLV2SleepTime = 0;//浅睡level 2
        int awakeSleepTime = 0;//清醒
        int SporadicNapSleepTime = 0;//零星小睡分钟数
        String SporadicNapSleepTimeStr;//零星小睡时间段


        ArrayList<SporadicNapData> sporadicNapDataArrayList = new ArrayList<SporadicNapData>();

        int startIndex = 0;


        for (int j = 0; j < m1440Result.length; j++) {
            int sleepState = m1440Result[j];
            //为睡眠数据
            if (sleepState == 250) {
                //深睡
                // Logger.myLog("深睡");
                deepSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 251) {
                //浅睡 level 2
                // Logger.myLog("浅睡 level 2");
                lightLV2SleepTime++;
                totalSleepTime++;

            } else if (sleepState == 252) {
                //浅睡 level 1
                // Logger.myLog("浅睡 level 1");
                lightLV2SleepTime++;
                totalSleepTime++;

            } else if (sleepState == 253) {
                //清醒
                Logger.myLog("清醒");
                awakeSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 0) {
                //步数数据
                // awakeSleepTime++;
            }

            //判断当前值跟开始值是否一致，不一致把间隔算出来，把状态算出来，然后把当前值给startindex，然后比较下一个
//            if (m1440Result[j] != m1440Result[startIndex]) {
//                SporadicNapData sporadicNapData = new SporadicNapData();
//                int sleepInterval = j - startIndex;   //睡眠区间时间
//                int state = (m1440Result[startIndex] == 0 || m1440Result[startIndex] == 253) ? 0 : 1;
//                String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//通过startIndex和当前index j来判断时间区域
//                startIndex = j;//当不相同的情况下，更新开始index
//                sporadicNapData.setSleepTimeStr(sleepDetailStr);
//                sporadicNapData.setState(state);
//                sporadicNapData.setTime(sleepInterval);
//                sporadicNapDataArrayList.add(sporadicNapData);
//            }
//            sporadicNapNewDataArrayList

            //前一个睡眠状态和后一个睡眠状态不相同时
            if (!(isEquals(m1440Result[startIndex], m1440Result[j]))) {
                Logger.myLog("index == " + j);
                SporadicNapData sporadicNapData = new SporadicNapData();
                int sleepInterval = j - startIndex;   //睡眠区间时间
                int state = (m1440Result[startIndex] == 0) ? 0 : 1;
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
                    int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                    String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//通过startIndex和当前index j来判断时间区域
                    startIndex = j;//当不相同的情况下，更新开始index
                    sporadicNapData.setSleepTimeStr(sleepDetailStr);
                    sporadicNapData.setState(state);
                    sporadicNapData.setTime(sleepInterval);
                    sporadicNapDataArrayList.add(sporadicNapData);
                }
            }

        }

        watchSleepDayData.setSleepArry(m1440Result);
        watchSleepDayData.setTotalSleepTime(totalSleepTime);
        watchSleepDayData.setDeepSleepTime(deepSleepTime);
        watchSleepDayData.setLightLV2SleepTime(lightLV2SleepTime);
        watchSleepDayData.setLightLV1SleepTime(lightLV1SleepTime);
        watchSleepDayData.setAwakeSleepTime(awakeSleepTime);

        //判断零星小睡,连续大于30分钟的清醒，然后连续的小于45分钟睡眠，最后连续的大于30分钟清醒
        //将睡眠分为清醒和睡眠段,然后对睡眠段做操作

        ArrayList<SporadicNapData> sporadicNapDataReslut = new ArrayList<SporadicNapData>();

        for (int j = 0; j < sporadicNapDataArrayList.size(); j++) {
            Logger.myLog(sporadicNapDataArrayList.toString());
            SporadicNapData sporadicNapData = sporadicNapDataArrayList.get(j);
            int size = sporadicNapDataArrayList.size();
            if (sporadicNapData.getState() == 0 && sporadicNapData.getTime() > 30) {
                //当清醒大于30分钟时
                if (j + 1 <= size - 2) {
                    SporadicNapData sporadicNapDataMiddle = sporadicNapDataArrayList.get(j + 1);
                    if (sporadicNapDataMiddle.getState() == 1 && sporadicNapDataMiddle.getTime() < 45) {
                        //当中间段为睡眠数据，且睡眠时间小于45分钟时
                        if (j + 2 <= size - 1) {
                            SporadicNapData sporadicNapDataEnd = sporadicNapDataArrayList.get(j + 2);
                            if (sporadicNapDataEnd.getState() == 0 && sporadicNapDataEnd.getTime() > 30) {
                                //最后一段也是30分钟以上的清醒,那么将会认为中间这段时零星小睡部分
                                if (!isCurrentDay) {
                                    sporadicNapDataReslut.add(sporadicNapDataMiddle);
                                }
                            }
                        }
                    }
                }
            }
        }

        int time = 0;
        StringBuilder timeStr = new StringBuilder();
        timeStr.append("");
        for (int j = 0; j < sporadicNapDataReslut.size(); j++) {
            Logger.myLog("零星小睡 == " + sporadicNapDataReslut.toString());
            time += sporadicNapDataReslut.get(j).getTime();
            timeStr.append(sporadicNapDataReslut.get(j).getSleepTimeStr() + ",");
        }
        watchSleepDayData.setSporadicNapSleepTimes(sporadicNapDataReslut.size());
        watchSleepDayData.setSporadicNapSleepTime(time);
        if (sporadicNapDataReslut.size() == 1) {
            watchSleepDayData.setSporadicNapSleepTimeStr(sporadicNapDataReslut.get(0).getSleepTimeStr());
        } else {
            watchSleepDayData.setSporadicNapSleepTimeStr(timeStr.toString());
        }


        return watchSleepDayData;
    }

    private boolean isEquals(int i, int i1) {
        return ((i == 0) && (i1 == 0)) ||
                ((i == 250 || i == 251 || i == 252 || i == 253) && (i1 == 250 || i1 == 251 || i1 == 252) || i1 == 253);
    }

    public WristbandHrHeart parseHrData(Watch_W516_24HDataModel model) {
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
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


    public SleepMainData getWatchSleepDayLastFourData(String deviceId) {
        WatchSleepDayData watchSleepDayData1 = null;
        watchSleepDayData1 = getWatchSleepDayLastData(deviceId);
        SleepMainData sleepMainData = new SleepMainData();
        if (watchSleepDayData1 != null) {
            sleepMainData.setMinute(watchSleepDayData1.getTotalSleepTime());
            sleepMainData.setLastSyncDate(watchSleepDayData1.getDateStr());
        } else {
            sleepMainData.setMinute(0);
            sleepMainData.setLastSyncDate("");
        }

        return sleepMainData;

        /*WatchSleepDayData watchSleepDayData1 = null;
        WatchSleepDayData watchSleepDayData2 = null;
        SleepMainData sleepMainData = new SleepMainData();
        Watch_W516_24HDataModel twoCurrentDay;
        Watch_W516_24HDataModel twoLastDay;
        Watch_W516_24HDataModel today2 = null;

        List<Watch_W516_24HDataModel> bracelet_w311_24HDataModels = Watch_W516_24HDataModelAction.findLastFourDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        watchSleepDayData1 = getWatchSleepDayLastData(deviceId);
        String nextStr;
        String dateStr;

        if (bracelet_w311_24HDataModels != null && bracelet_w311_24HDataModels.size() > 0) {
            today2 = bracelet_w311_24HDataModels.get(0);
            int type = parSleepDayType(today2);
            if (type == alldayData) {
                //一天就代表两天的数据
                if (watchSleepDayData1 != null) {
                    nextStr = TimeUtils.getLastDayStr(watchSleepDayData1.getDateStr());
                    watchSleepDayData2 = getWatchSleepDayData(nextStr, deviceId);
                    // WatchSleepDayData watchSleepDayData = getWatchSleepDayData(DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "yyyy-MM-dd"), devcieId);

                }

            } else if (type == day20afterhasData) {
                //第一条20点以后有数据需要去第二条的数据作为开始时间进行查询 不连续
                if (bracelet_w311_24HDataModels.size() > 1 && watchSleepDayData1 != null) {
                    today2 = bracelet_w311_24HDataModels.get(1);
                    switch (type) {
                        case alldayData:
                        case day20afterhasData:
                            nextStr = TimeUtils.getNextDayStr(today2.getDateStr());
                            watchSleepDayData2 = getWatchSleepDayData(nextStr, deviceId);
                            break;
                        case allDayNoData:
                            break;
                        case day20beforhasData:
                            dateStr = today2.getDateStr();
                            watchSleepDayData2 = getWatchSleepDayData(dateStr, deviceId);
                            break;

                    }
                }
            } else if (type == day20beforhasData) {
                //第一条20点以后有数据需要去第二条的数据作为开始时间进行查询 连续
                //是连续的，两天的数据
                if (bracelet_w311_24HDataModels.size() > 1 && watchSleepDayData1 != null) {
                    today2 = bracelet_w311_24HDataModels.get(1);
                    Logger.myLog("getWatchSleepDayLastFourData today2：" + today2.getDateStr() + "TimeUtils.getLastDayStr(today2.getDateStr()):" + TimeUtils.getLastDayStr(watchSleepDayData1.getDateStr()));
                    type = parSleepDayType(today2);
                    switch (type) {
                        case alldayData:
                        case day20beforhasData:
                            nextStr = TimeUtils.getNextDayStr(today2.getDateStr());
                            watchSleepDayData2 = getWatchSleepDayData(nextStr, deviceId);
                            break;
                        case allDayNoData:
                            Logger.myLog("getWatchSleepDayLastFourData allDayNoData");
                            break;
                        case day20afterhasData:
                            //需要取第三天的数据
                            if (bracelet_w311_24HDataModels.size() > 2) {
                                today2 = bracelet_w311_24HDataModels.get(2);
                                switch (type) {
                                    case alldayData:
                                    case day20afterhasData:
                                        nextStr = TimeUtils.getNextDayStr(today2.getDateStr());
                                        watchSleepDayData2 = getWatchSleepDayData(nextStr, deviceId);
                                        break;
                                    case allDayNoData:
                                        break;
                                    case day20beforhasData:
                                        dateStr = today2.getDateStr();
                                        watchSleepDayData2 = getWatchSleepDayData(dateStr, deviceId);
                                        break;

                                }
                            }
                            break;


                    }
                }
            }
        }
        int watch2TotalSleep = watchSleepDayData2 == null ? 0 : watchSleepDayData2.getTotalSleepTime();
        int watch1TotalSleep = watchSleepDayData1 == null ? 0 : watchSleepDayData1.getTotalSleepTime();
        sleepMainData.setCompareSleepTime(watch1TotalSleep - watch2TotalSleep);
        if (watchSleepDayData1 != null) {
            Logger.myLog("sleepMainData1:---------------------" + sleepMainData.getMinute());
            sleepMainData.setMinute(watchSleepDayData1.getTotalSleepTime());
            if (watch1TotalSleep == 0 && watch2TotalSleep == 0) {
                sleepMainData.setLastSyncDate("");
                sleepMainData.setMinute(-1);
            } else {
                sleepMainData.setLastSyncDate(watchSleepDayData1.getDateStr());
            }
        } else {
            sleepMainData.setLastSyncDate("");
            sleepMainData.setMinute(-1);
        }
        return sleepMainData;*/

    }


    //判断当前的数据是20点以前的还是20点以后的。 type 0：没有数据，type 1:20点前的，type2:20点后的 type 3 20 点前和20点后都有数据

    public int parSleepDayType(Watch_W516_24HDataModel currentModel) {
        if (currentModel == null) {
            return -1;
        }
        //需要判断当天0-20点 和20点-23.59数据
        boolean is20beforHasData = false;
        boolean is20afterHasData = false;
        Gson gson = new Gson();
        Watch_W516_24HDataModel watch_w516_24HDataModel = currentModel;
        String sleepArray = watch_w516_24HDataModel.getSleepArray();
        String dateStr1 = watch_w516_24HDataModel.getDateStr();
        int[] ints = gson.fromJson(sleepArray, int[].class);
        List<Integer> todayDataList = new ArrayList<>();
        List<Integer> today20UpDataList = new ArrayList<>();
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        boolean isLast;//时候要拿昨天，或者拿明天的数据

        int[] todayData;
        int[] today20UpData;
        if (ints == null) {
            //说明没有数据
            todayData = new int[1200];
            isLast = true;
            is20beforHasData = false;
            is20afterHasData = false;
        } else {
            if (ints.length > 1200) {
                todayData = new int[1200];
                today20UpData = new int[ints.length - 1200];
                System.arraycopy(ints, 0, todayData, 0, 1200);
                System.arraycopy(ints, 1200, today20UpData, 0, ints.length - 1200);
                //需要区分两个时段,
                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
                for (int j = 0; j <= today20UpData.length - 1; j++) {
                    today20UpDataList.add(today20UpData[j]);
                }

                //先判断20点以后的数据，如果有就直接填充后一天的数据,全部为0，因为后一天没有数据
                if (Collections.max(today20UpDataList) > 0) {
                    //说明有数据
                    isLast = false;
                    is20afterHasData = true;
                } else {
                    //没有数据，说明是要拿前一天数据
                    isLast = true;
                    is20afterHasData = false;
                }
                if (Collections.max(todayDataList) > 0) {
                    is20beforHasData = true;
                } else {
                    is20beforHasData = false;
                }

            } else {
                //当天的情况,会有小于的时候
                todayData = new int[ints.length];
                System.arraycopy(ints, 0, todayData, 0, ints.length);

                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
                //  Logger.myLog("todayData:" + todayData.length+"Collections.max(todayDataList):"+Collections.max(todayDataList));
                //小于的情况只有当天，那么就走底部的逻辑
                isLast = true;
                if (todayDataList.size() > 0 && Collections.max(todayDataList) > 0) {
                    is20beforHasData = true;
                } else {
                    is20beforHasData = false;
                }
                //is20afterHasData = false;
            }
        }

        if (is20afterHasData && is20beforHasData) {
            return alldayData;//都有数据
        } else if (!is20afterHasData && !is20beforHasData) {
            return allDayNoData;////都没数据
        } else if (!is20afterHasData && is20beforHasData) {
            return day20beforhasData;//20点前有数据
        } else {
            return day20afterhasData;//20点后有数据
        }

    }

    public void realWatch() {
       /* WatchRealTimeDataDao watchRealTimeDataDao = BaseAction.getWatchRealTimeDataDao();
        WatchRealTimeData watchRealTimeData = new WatchRealTimeData();
        watchRealTimeData.setId((long) 2);
        watchRealTimeData.setCal((int) StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(loginBean.getWeight()), mResult2.getStepNum()));
        watchRealTimeData.setDate(bike.gymproject.viewlibray.pickerview.utils.DateUtils.getYMD(System.currentTimeMillis()));
        watchRealTimeData.setMac(mResult2.getMac());
        watchRealTimeData.setStepKm(StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(loginBean.getHeight()), loginBean.getGender(), mResult2.getStepNum()));
        watchRealTimeData.setStepNum(mResult2.getStepNum());
        watchRealTimeDataDao.insertOrReplace(watchRealTimeData);*/
    }


}
