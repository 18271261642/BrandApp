package com.isport.brandapp.device.bracelet.braceletModel;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_24HDataModelAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_RealTimeDataAction;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24HDataModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
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
import com.isport.brandapp.util.DeviceTypeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import bike.gymproject.viewlibray.SleepFormatUtils;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class W311DataModelImpl implements IW311DataModel {

    public final static int allDayNoData = 0;
    public final static int day20beforhasData = 1;
    public final static int day20afterhasData = 2;
    public final static int alldayData = 3;


    @Override
    public Wristbandstep getW311SportData(String userid, String strDate, String deviceId) {

        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userid, strDate, deviceId);
        if (bracelet_w311_24HDataModel == null) {
            return new Wristbandstep("", 0, 0, 0, "0", "0.00", "0", "0", null, "0", "0", "0", "0", "0");
        } else {
            return parWristBandStep(Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userid, strDate, deviceId));
        }
    }

    @Override
    public void saveCurrentW311SportData(String userid, String strDate, String deviceId, long step, long cal, float dis) {

        Logger.myLog("saveCurrentW311SportData:" + userid + ",deviceId:" + deviceId);
        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userid, strDate, deviceId);
        if (bracelet_w311_24HDataModel == null) {
            Bracelet_W311_24HDataModel bracelet_w311_24HDataModel1 = new Bracelet_W311_24HDataModel();
            bracelet_w311_24HDataModel.setUserId(userid);
            bracelet_w311_24HDataModel.setDeviceId(deviceId);
            bracelet_w311_24HDataModel.setTotalSteps(step);
            bracelet_w311_24HDataModel.setTotalCalories(cal);
            bracelet_w311_24HDataModel.setTotalDistance(dis);

        } else {
            bracelet_w311_24HDataModel.setTotalSteps(step);
            bracelet_w311_24HDataModel.setTotalCalories(cal);
            bracelet_w311_24HDataModel.setTotalDistance(dis);
        }
        Bracelet_W311_24HDataModelAction.saveOrUpdateBracelet24HDataModel(bracelet_w311_24HDataModel, deviceId);
    }


    /**
     * ??????????????????list
     *
     * @param strDate
     * @return
     */
    @Override
    public ArrayList<String> getMonthData(String strDate, String deviceId) {
        ArrayList<String> dataList = (ArrayList<String>) Bracelet_W311_24HDataModelAction.findBracelet_W311_Bracelet_W311_24HDataMode_CurrentMonth_DateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        for (int i = 0; i < dataList.size(); i++) {
            Logger.myLog("bracelet:" + strDate + ":" + dataList.get(i));
        }
        return dataList;
    }

    @Override
    public Bracelet_W311_RealTimeData getRealTimeData(String userId, String deviceId) {

        Bracelet_W311_RealTimeData bracelet_w311_realTimeData = Bracelet_W311_RealTimeDataAction.find_real_time_data(userId, deviceId, DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
        Logger.myLog("??????????????????" + userId + ",deviceId:" + deviceId + "-----" + bracelet_w311_realTimeData);
        return bracelet_w311_realTimeData;
    }


    @Override
    public boolean saveRealTimeData(String userid, String deviceId, int step, float dis, int cal, String date, String mac) {
        Bracelet_W311_RealTimeData realTimeData = new Bracelet_W311_RealTimeData(1l, userid, deviceId, step, dis, cal, date, mac);

        long id = Bracelet_W311_RealTimeDataAction.saveOrUpdateW311RealTimeData(userid, realTimeData, false);
        Logger.myLog("?????????????????? ??????????????????" + realTimeData + ",id:" + id + "deviceId:" + deviceId + "userid:" + userid);
        if (id >= 0) {
            return true;
        } else {
            return false;
        }
    }


    public Wristbandstep parWristBandStep(Bracelet_W311_24HDataModel model) {
        Wristbandstep wristbandstep = new Wristbandstep();
        if (model == null) {
            return wristbandstep;
        }
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



      /*  ArrayList<Integer> stepList = new ArrayList<>();
        int sum = 0;
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
        int sumStep = 0;
        for (int i = 0; i < stepList.size(); i++) {
            sumStep += stepList.get(i);
        }
        if (sumStep != model.getTotalSteps()) {
            model.setTotalSteps(sumStep);
        }
        // UserInfoBean loginBean = UserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        /*wristbandstep.setStepKm(StepArithmeticUtil.stepsConversionDistance(Float.parseFloat(loginBean.getHeight()), loginBean.getGender(), model.getTotalSteps()));
        wristbandstep.setCalorie(StepArithmeticUtil.stepsConversionCalories(Float.parseFloat(loginBean.getWeight()), model.getTotalSteps()));*/

        wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(model.getTotalDistance()));
        wristbandstep.setCalorie(CommonDateUtil.formatInterger(model.getTotalCalories()));
        wristbandstep.setStepNum(model.getTotalSteps() + "");
        wristbandstep.setLastServerTime(model.getTimestamp());
        wristbandstep.setStrDate(model.getDateStr());
        wristbandstep.setStepArry(stepList);

        // Logger.myLog("wristbandstep.setStepKm:" + wristbandstep.getStepKm() + "wristbandstep.setCalorie:" + wristbandstep.getCalorie());

        return wristbandstep;
    }


    public WatchSleepDayData parBraceletSleepDayData(Bracelet_W311_24HDataModel lastModel, Bracelet_W311_24HDataModel currentModel, String strDate) {
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        //??????????????????null
        if (!TextUtils.isEmpty(strDate)) {
            watchSleepDayData.setDateStr(strDate);
        }
        Logger.myLog("parWatchSleepDayData parBraceletSleepDayData");
        int[] m1440Result;//????????????

        int[] m240Data = new int[240];//?????????240???0?????????????????????
        int[] m1200Data = new int[1200];//?????????1200???0?????????????????????

        for (int j = 0; j < 1200; j++) {
            if (j < 240) {
                m240Data[j] = 0;
            }
            m1200Data[j] = 0;
        }
        //?????????????????????????????????

        //??????????????????????????????

        //?????????????????????


        ArrayList<String> dateList = new ArrayList<String>();
        Gson gson = new Gson();


        Bracelet_W311_24HDataModel watch_w516_24HDataModel = currentModel;//???????????????
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
            if (ints.length > 1200) {
                m1440Result = new int[1440];
            } else {
                //???????????????,?????????????????????,??????20????????????
                //??????????????????????????????UI???????????????1440
//            m1440Result = new int[240 + ints.length];
                m1440Result = new int[1440];
            }
        }

        //???????????????????????????????????????????????????????????????????????????
        //??????????????????20-23.59?????????????????????
        Bracelet_W311_24HDataModel lastDay24hData = lastModel;//??????????????????
        if (lastDay24hData == null) {
            //??????
            System.arraycopy(m240Data, 0, m1440Result, 0, 240);
        } else {
            //???????????????1200-1440?????????
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
                    // System.arraycopy(intsLast, 1200, m1440Result, 0, intsLast.length - 1200);
                }
            }
        }

        if (ints == null) {
            System.arraycopy(m1200Data, 0, m1440Result, 240, 1200);
        } else {
            if (ints.length > 1200) {
                System.arraycopy(ints, 0, m1440Result, 240, 1200);
            } else {
                //???????????????,?????????????????????,??????20????????????
                System.arraycopy(ints, 0, m1440Result, 240, ints.length);
            }
        }

        int totalSleepTime = 0;//???????????????????????????????????????
        int deepSleepTime = 0;//??????
        int lightLV1SleepTime = 0;//??????level 1
        int lightLV2SleepTime = 0;//??????level 2
        int awakeSleepTime = 0;//??????
        int SporadicNapSleepTime = 0;//?????????????????????
        String SporadicNapSleepTimeStr;//?????????????????????

        ArrayList<SporadicNapData> sporadicNapDataArrayList = new ArrayList<SporadicNapData>();

        int startIndex = 0;

        for (int j = 0; j < m1440Result.length; j++) {
            int sleepState = m1440Result[j];
            //???????????????
            if (sleepState == 253) {
                //??????
                Logger.myLog("??????");
                deepSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 251 || sleepState == 252) {
                //?????? level 2
                Logger.myLog("?????? level 2");
                lightLV2SleepTime++;
                totalSleepTime++;

            } else if (sleepState == 250) {
                //??????
                Logger.myLog("??????");
                awakeSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 0) {
                //????????????
                //awakeSleepTime++;
            }

            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????startindex????????????????????????
            //?????????????????????????????????????????????????????????
            if (!(isEquals(m1440Result[startIndex], m1440Result[j]))) {
                Logger.myLog("index == " + j);
                SporadicNapData sporadicNapData = new SporadicNapData();
                int sleepInterval = j - startIndex;   //??????????????????
                int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                //?????????????????????????????????????????????????????????????????????????????????
                String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j - 1);//??????startIndex?????????index j?????????????????????
                startIndex = j;//???????????????????????????????????????index
                sporadicNapData.setSleepTimeStr(sleepDetailStr);
                sporadicNapData.setState(state);
                sporadicNapData.setTime(sleepInterval);
                sporadicNapDataArrayList.add(sporadicNapData);
            } else {
                //???????????????????????????????????????????????????
                if (j == 1439) {
                    //??????????????????????????????,????????????????????????????????????
                    SporadicNapData sporadicNapData = new SporadicNapData();
                    int sleepInterval = j - startIndex;   //??????????????????
                    int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                    String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//??????startIndex?????????index j?????????????????????
                    startIndex = j;//???????????????????????????????????????index
                    sporadicNapData.setSleepTimeStr(sleepDetailStr);
                    sporadicNapData.setState(state);
                    sporadicNapData.setTime(sleepInterval);
                    sporadicNapDataArrayList.add(sporadicNapData);
                }
            }
        }

        //???????????????????????????????????????????????????
        //??????>30 ??????<45 ??????>30 ??????????????????
        //sleepState == 250 ??????
        // sleepState == 251 || sleepState == 252 ??????
        // sleepState == 253 ??????
        //sleepState == 0 ??????
       /* SleepBean sleepBean = new SleepBean();
        ArrayList<SleepBean> list = new ArrayList<>();
        for (int i = 0; i < m1440Result.length; i++) {
            if (TextUtils.isEmpty(sleepBean.startTime)) {
                sleepBean.starIndex = m1440Result[i];
                sleepBean.startTime = SleepFormatUtils.sleepTimeFormatByIndex(startIndex);
            }

            if (!(isEquals(m1440Result[startIndex], m1440Result[i]))) {
                sleepBean.endIndex = m1440Result[i - 1];
                sleepBean.endTime = SleepFormatUtils.sleepTimeFormatByIndex(startIndex);
                sleepBean.sleepTime = sleepBean.endIndex - sleepBean.starIndex;
                list.add(sleepBean);
                sleepBean = new SleepBean();
                sleepBean.starIndex = m1440Result[i];
                sleepBean.startTime = SleepFormatUtils.sleepTimeFormatByIndex(startIndex);
            }
        }


        for (int i = 0; i < list.size(); i++) {
            Logger.myLog("sleep______________________:" + list.get(i));
        }*/

        watchSleepDayData.setSleepArry(m1440Result);
        watchSleepDayData.setTotalSleepTime(totalSleepTime);
        watchSleepDayData.setDeepSleepTime(deepSleepTime);
        watchSleepDayData.setLightLV2SleepTime(lightLV2SleepTime);
        watchSleepDayData.setLightLV1SleepTime(lightLV1SleepTime);
        watchSleepDayData.setAwakeSleepTime(awakeSleepTime);

        //??????????????????,????????????30???????????????????????????????????????45????????????????????????????????????30????????????
        //?????????????????????????????????,???????????????????????????

        ArrayList<SporadicNapData> sporadicNapDataReslut = new ArrayList<SporadicNapData>();

        for (int j = 0; j < sporadicNapDataArrayList.size(); j++) {
            Logger.myLog(sporadicNapDataArrayList.toString());
            SporadicNapData sporadicNapData = sporadicNapDataArrayList.get(j);
            int size = sporadicNapDataArrayList.size();
            if (sporadicNapData.getState() == 0 && sporadicNapData.getTime() > 30) {
                //???????????????30?????????
                if (j + 1 <= size - 2) {
                    SporadicNapData sporadicNapDataMiddle = sporadicNapDataArrayList.get(j + 1);
                    if (sporadicNapDataMiddle.getState() == 1 && sporadicNapDataMiddle.getTime() < 45) {
                        //???????????????????????????????????????????????????45?????????
                        if (j + 2 <= size - 1) {
                            SporadicNapData sporadicNapDataEnd = sporadicNapDataArrayList.get(j + 2);
                            if (sporadicNapDataEnd.getState() == 0 && sporadicNapDataEnd.getTime() > 30) {
                                //??????????????????30?????????????????????,???????????????????????????????????????????????????
                                sporadicNapDataReslut.add(sporadicNapDataMiddle);
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
            Logger.myLog("???????????? == " + sporadicNapDataReslut.toString());
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

    //????????????????????????20??????????????????20??????????????? type 0??????????????????type 1:20????????????type2:20????????? type 3 20 ?????????20??????????????????

    public int parSleepDayType(Bracelet_W311_24HDataModel currentModel) {
        if (currentModel == null) {
            return -1;
        }
        //??????????????????0-20??? ???20???-23.59??????
        boolean is20beforHasData = false;
        boolean is20afterHasData = false;
        Gson gson = new Gson();
        Bracelet_W311_24HDataModel watch_w516_24HDataModel = currentModel;
        String sleepArray = watch_w516_24HDataModel.getSleepArray();
        String dateStr1 = watch_w516_24HDataModel.getDateStr();
        int[] ints = gson.fromJson(sleepArray, int[].class);
        List<Integer> todayDataList = new ArrayList<>();
        List<Integer> today20UpDataList = new ArrayList<>();
        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        boolean isLast;//?????????????????????????????????????????????

        int[] todayData;
        int[] today20UpData;
        if (ints == null) {
            //??????????????????
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
                //????????????????????????,
                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
                for (int j = 0; j <= today20UpData.length - 1; j++) {
                    today20UpDataList.add(today20UpData[j]);
                }

                //?????????20???????????????????????????????????????????????????????????????,?????????0??????????????????????????????
                if (Collections.max(today20UpDataList) > 0) {
                    //???????????????
                    isLast = false;
                    is20afterHasData = true;
                } else {
                    //?????????????????????????????????????????????
                    isLast = true;
                    is20afterHasData = false;
                }
                if (Collections.max(todayDataList) > 0) {
                    is20beforHasData = true;
                } else {
                    is20beforHasData = false;
                }

            } else {
                //???????????????,?????????????????????
                todayData = new int[ints.length];
                System.arraycopy(ints, 0, todayData, 0, ints.length);
                //?????????????????????????????????????????????????????????
                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
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
            return alldayData;//????????????
        } else if (!is20afterHasData && !is20beforHasData) {
            return allDayNoData;////????????????
        } else if (!is20afterHasData && is20beforHasData) {
            return day20beforhasData;//20???????????????
        } else {
            return day20afterhasData;//20???????????????
        }

    }

    boolean isCurrentDay;

    public WatchSleepDayData parWatchSleepDayLastData(Bracelet_W311_24HDataModel lastModel, Bracelet_W311_24HDataModel currentModel) {
        //??????????????????0-20??? ???20???-23.59??????
        Gson gson = new Gson();

        isCurrentDay = false;


        Bracelet_W311_24HDataModel watch_w516_24HDataModel = currentModel;


        if (watch_w516_24HDataModel != null) {
            String currentDate = watch_w516_24HDataModel.getDateStr();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(Calendar.getInstance().getTime());
            if (dateString.equals(currentDate)) {
                isCurrentDay = true;
            }
          //  Log.e("drawBar", watch_w516_24HDataModel.getDateStr());
        }


        String sleepArray;
        //String dateStr1;
        int[] ints;
        List<Integer> todayDataList = new ArrayList<>();
        List<Integer> today20UpDataList = new ArrayList<>();
        sleepArray = watch_w516_24HDataModel.getSleepArray();
        if (TextUtils.isEmpty(sleepArray)) {
            ints = new int[1044];
        } else {
            ints = gson.fromJson(sleepArray, int[].class);

        }

        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();

        boolean isLast;//?????????????????????????????????????????????

        int[] todayData;
        int[] today20UpData;
        if (ints == null) {
            //??????????????????
            todayData = new int[1200];
            isLast = true;
        } else {
            if (ints.length > 1200) {
                todayData = new int[1200];
                today20UpData = new int[ints.length - 1200];
                System.arraycopy(ints, 0, todayData, 0, 1200);
                System.arraycopy(ints, 1200, today20UpData, 0, ints.length - 1200);
                //????????????????????????,
                for (int j = 0; j <= todayData.length - 1; j++) {
                    todayDataList.add(todayData[j]);
                }
                for (int j = 0; j <= today20UpData.length - 1; j++) {
                    today20UpDataList.add(today20UpData[j]);
                }

                //?????????20???????????????????????????????????????????????????????????????,?????????0??????????????????????????????
                if (Collections.max(today20UpDataList) > 0) {
                    //???????????????
                    isLast = false;
                } else {
                    //?????????????????????????????????????????????
                    isLast = true;
                }

            } else {
                //???????????????,?????????????????????
                todayData = new int[ints.length];
                System.arraycopy(ints, 0, todayData, 0, ints.length);
                //?????????????????????????????????????????????????????????
                isLast = true;

            }
        }

        Bracelet_W311_24HDataModel mToday;
        Bracelet_W311_24HDataModel mLast;
        if (isLast) {
            //??????????????????
            mToday = currentModel;
            mLast = lastModel;
        } else {
            //????????????????????????,???????????????????????????0
            mToday = null;
            mLast = currentModel;
            ints = null;

        }
        watchSleepDayData.setDateStr(isLast ? currentModel.getDateStr() : TimeUtils.getNextDayStr(currentModel.getDateStr()));

        int[] m1440Result;//????????????

        int[] m240Data = new int[240];//?????????240???0?????????????????????
        int[] m1200Data = new int[1200];//?????????1200???0?????????????????????

        for (int j = 0; j < 1200; j++) {
            if (j < 240) {
                m240Data[j] = 0;
            }
            m1200Data[j] = 0;
        }
        //?????????????????????????????????

        //??????????????????????????????

        //?????????????????????

//        if (ints.length > 1200) {
        m1440Result = new int[1440];
//        } else {
        //???????????????,?????????????????????,??????20????????????
        //??????????????????????????????UI???????????????1440
//            m1440Result = new int[240 + ints.length];
        m1440Result = new int[1440];
//        }


        //???????????????????????????????????????????????????????????????????????????
        //??????????????????20-23.59?????????????????????
        Bracelet_W311_24HDataModel lastDay24hData = mLast;//??????????????????
        if (lastDay24hData == null) {
            //??????
            System.arraycopy(m240Data, 0, m1440Result, 0, 240);
        } else {
            //???????????????1200-1440?????????
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
                    // System.arraycopy(intsLast, 1200, m1440Result, 0, intsLast.length - 1200);
                }
            }
        }

        /**
         * ???????????????
         */
        if (ints == null) {
            //????????????????????????0
            System.arraycopy(m1200Data, 0, m1440Result, 240, 1200);
        } else {
            if (ints.length > 1200) {
                System.arraycopy(ints, 0, m1440Result, 240, 1200);
            } else {
                //???????????????,?????????????????????,??????20????????????
                System.arraycopy(ints, 0, m1440Result, 240, ints.length);

            }
        }


        int totalSleepTime = 0;//???????????????????????????????????????
        int deepSleepTime = 0;//??????
        int lightLV1SleepTime = 0;//??????level 1
        int lightLV2SleepTime = 0;//??????level 2
        int awakeSleepTime = 0;//??????
        int SporadicNapSleepTime = 0;//?????????????????????
        String SporadicNapSleepTimeStr;//?????????????????????


        ArrayList<SporadicNapData> sporadicNapDataArrayList = new ArrayList<SporadicNapData>();

        int startIndex = 0;


        for (int j = 0; j < m1440Result.length; j++) {
            int sleepState = m1440Result[j];
            //???????????????
            if (sleepState == 253) {
                //??????
                // Logger.myLog("??????");
                deepSleepTime++;
                totalSleepTime++;
            } else if (sleepState == 251 || sleepState == 252) {
                //?????? level 2
                // Logger.myLog("?????? level 2");
                lightLV2SleepTime++;
                totalSleepTime++;

            } else if (sleepState == 250) {
                //??????
                //Logger.myLog("??????");
                awakeSleepTime++;
                totalSleepTime++;

            } else if (sleepState == 0) {
                //????????????
                // awakeSleepTime++;
            }

            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????startindex????????????????????????

            //?????????????????????????????????????????????????????????
            if (!(isEquals(m1440Result[startIndex], m1440Result[j]))) {
                Logger.myLog("index == " + j);
                SporadicNapData sporadicNapData = new SporadicNapData();
                int sleepInterval = j - startIndex;   //??????????????????
                int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                // Logger.myLog("parWatchSleepDayData parWatchSleepDayLastData m1440Result[startIndex]" + m1440Result[startIndex] + "m1440Result[j - 1]:" + m1440Result[j - 1] + ":m1440Result[startIndex - 1] == 253" + m1440Result[startIndex - 1] + ",state:" + state);
                //?????????????????????????????????????????????????????????????????????????????????
                String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//??????startIndex?????????index j?????????????????????
                startIndex = j;//???????????????????????????????????????index
                sporadicNapData.setSleepTimeStr(sleepDetailStr);
                sporadicNapData.setState(state);
                sporadicNapData.setTime(sleepInterval);
                sporadicNapDataArrayList.add(sporadicNapData);
            } else {
                //???????????????????????????????????????????????????
                if (j == 1439) {
                    //??????????????????????????????,????????????????????????????????????
                    SporadicNapData sporadicNapData = new SporadicNapData();
                    int sleepInterval = j - startIndex;   //??????????????????
                    int state = (m1440Result[startIndex] == 0) ? 0 : 1;
                    String sleepDetailStr = SleepFormatUtils.sleepTimeFormatByIndex(startIndex) + "-" + SleepFormatUtils.sleepTimeFormatByIndex(j);//??????startIndex?????????index j?????????????????????
                    startIndex = j;//???????????????????????????????????????index
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

        //??????????????????,????????????30???????????????????????????????????????45????????????????????????????????????30????????????
        //?????????????????????????????????,???????????????????????????

        ArrayList<SporadicNapData> sporadicNapDataReslut = new ArrayList<SporadicNapData>();

        for (int j = 0; j < sporadicNapDataArrayList.size(); j++) {
            Logger.myLog(sporadicNapDataArrayList.toString());
            SporadicNapData sporadicNapData = sporadicNapDataArrayList.get(j);
            int size = sporadicNapDataArrayList.size();
            if (sporadicNapData.getState() == 0 && sporadicNapData.getTime() > 30) {
                //???????????????30?????????
                if (j + 1 <= size - 2) {
                    SporadicNapData sporadicNapDataMiddle = sporadicNapDataArrayList.get(j + 1);
                    if (sporadicNapDataMiddle.getState() == 1 && sporadicNapDataMiddle.getTime() < 45) {
                        //???????????????????????????????????????????????????45?????????
                        if (j + 2 <= size - 1) {
                            SporadicNapData sporadicNapDataEnd = sporadicNapDataArrayList.get(j + 2);
                            if (sporadicNapDataEnd.getState() == 0 && sporadicNapDataEnd.getTime() > 30) {
                                //??????????????????30?????????????????????,???????????????????????????????????????????????????
                                if(!isCurrentDay){
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
            // Logger.myLog("???????????? == " + sporadicNapDataReslut.toString());
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
                ((i == 250 || i == 251 || i == 252 || i == 253) && (i1 == 250 || i1 == 251 || i1 == 252 || i1 == 253));
    }

    public WristbandHrHeart parseHrData(Bracelet_W311_24HDataModel model) {
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(model.getHrArray())) {
            Integer[] hrArry = new Gson().fromJson(model.getHrArray(), Integer[].class);
            arrayList = new ArrayList<Integer>(Arrays.asList(hrArry));
        } else {
            arrayList = new ArrayList<Integer>();
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

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public WatchSportMainData getW311hStepLastTwoData(String userid, String deviceId, boolean isConnect) {

       /* Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userid, DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
        Bracelet_W311_RealTimeData bracelet_w311_realTimeData = getRealTimeData(userid, deviceId);
        WatchSportMainData wristbandstep = new WatchSportMainData();
        if (bracelet_w311_24HDataModel != null) {
            wristbandstep.setCal(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
            wristbandstep.setStep(bracelet_w311_realTimeData.getStepNum() + "");
            wristbandstep.setDistance((CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm())));
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            Logger.myLog("getW311hStepLastTwoData: 1" + wristbandstep);
            return wristbandstep;

        } else if (bracelet_w311_realTimeData != null) {
            wristbandstep.setStep(bracelet_w311_realTimeData.getStepNum() + "");
            wristbandstep.setCal(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
            wristbandstep.setDistance((CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm())));
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            Logger.myLog("getW311hStepLastTwoData: 2" + wristbandstep);
            return wristbandstep;

        } else {
            Logger.myLog("getW311hStepLastTwoData: 3" + wristbandstep);
            return null;
        }
*/


        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userid, DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
        Bracelet_W311_RealTimeData bracelet_w311_realTimeData = getRealTimeData(userid, deviceId);

        //1??????????????????????????????????????????????????????????????????????????????

        WatchSportMainData wristbandstep = new WatchSportMainData();
        if (bracelet_w311_24HDataModel != null || bracelet_w311_realTimeData != null) {
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            if (bracelet_w311_24HDataModel != null) {
                Wristbandstep wristbandstep2 = parWristBandStep(bracelet_w311_24HDataModel);
                wristbandstep.setDistance(wristbandstep2.getStepKm());
                wristbandstep.setCal(wristbandstep2.getCalorie());
                wristbandstep.setStep(wristbandstep2.getStepNum());
            }

            if (bracelet_w311_realTimeData != null) {
                wristbandstep.setDistance(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                wristbandstep.setCal(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                wristbandstep.setStep(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getStepNum()));
            }
            return wristbandstep;
        } else {
            if (DeviceTypeUtil.isContainWatch()) {
                wristbandstep.setLastSyncTime(System.currentTimeMillis());
                wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
                wristbandstep.setCompareStep("0");
                wristbandstep.setCompareStep("0");
                wristbandstep.setCal("0");
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


        /*//??????????????????????????????????????????????????????
        List<Bracelet_W311_24HDataModel> modleList = Bracelet_W311_24HDataModelAction.find_Bracelet_311_24HDataModelByDeviceId_Last_Two(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId, DateUtil.dataToString(new Date(), "yyyy-MM-dd"));

        Bracelet_W311_24HDataModel bracelet_w311_24HDataModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userid, DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
        Bracelet_W311_RealTimeData bracelet_w311_realTimeData = getRealTimeData(userid, deviceId);
        // Logger.myLog("bracelet_w311_24HDataModel:" + bracelet_w311_24HDataModel + "bracelet_w311_realTimeData:" + bracelet_w311_realTimeData + "isConnect:" + isConnect);
        if (bracelet_w311_24HDataModel != null || bracelet_w311_realTimeData != null) {
            if (modleList != null) {
                modleList.add(0, bracelet_w311_24HDataModel);
            } else {
                modleList = new ArrayList<>();
                modleList.add(bracelet_w311_24HDataModel);
            }
            WatchSportMainData wristbandstep = new WatchSportMainData();
            if (modleList != null && modleList.size() > 0) {
                //Logger.myLog("getLastSprotData" + model.toString());
                Wristbandstep wristbandstep1 = null;
                wristbandstep1 = parWristBandStep(modleList.get(0));
                wristbandstep.setLastSyncTime(System.currentTimeMillis());
                if (bracelet_w311_realTimeData != null) {
                    wristbandstep1.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                    wristbandstep1.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                    wristbandstep1.setStepKm((CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm())));
                    wristbandstep1.setStrDate(bracelet_w311_realTimeData.getDate());
                }
                wristbandstep.setDistance(TextUtils.isEmpty(wristbandstep1.getStepKm()) ? "0.00" : wristbandstep1.getStepKm());
                wristbandstep.setCal(TextUtils.isEmpty(wristbandstep1.getCalorie()) ? "0" : wristbandstep1.getCalorie());
                wristbandstep.setStep(wristbandstep1.getStepNum());
                wristbandstep.setDateStr(wristbandstep1.getStrDate());
                if (modleList.size() >= 2) {
                    Wristbandstep wristbandstep2 = parWristBandStep(modleList.get(1));
                    if (TextUtils.isEmpty(wristbandstep2.getStepNum())) {
                        wristbandstep2.setStepNum("0");
                    }
                    if (TextUtils.isEmpty(wristbandstep1.getStepNum())) {
                        wristbandstep1.setStepNum("0");
                    }
                    wristbandstep.setCompareStep(Long.parseLong(wristbandstep1.getStepNum()) - Long.parseLong(wristbandstep2.getStepNum()) + "");
                } else {
                    wristbandstep.setCompareStep(wristbandstep1.getStepNum());
                }

            } else {
                if (bracelet_w311_realTimeData != null) {
                    Wristbandstep wristbandstep1 = new Wristbandstep();
                    wristbandstep1.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                    wristbandstep1.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                    wristbandstep1.setStepKm((CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm())));
                    wristbandstep.setDistance(wristbandstep1.getStepKm());
                    wristbandstep.setCal(wristbandstep1.getCalorie());
                    wristbandstep.setStep(wristbandstep1.getStepNum());
                    wristbandstep.setDateStr(wristbandstep1.getStrDate());
                    wristbandstep1.setStrDate(bracelet_w311_realTimeData.getDate());
                    wristbandstep.setCompareStep(wristbandstep1.getStepNum());
                    wristbandstep.setLastSyncTime(System.currentTimeMillis());
                } else {
                    wristbandstep.setCompareStep("0");
                    wristbandstep.setCal("0");
                    wristbandstep.setDistance("0.00");
                }

            }
            return wristbandstep;
        } else if (modleList != null && modleList.size() >= 1) {
            //?????????????????????,
            WatchSportMainData wristbandstep = new WatchSportMainData();
            wristbandstep.setCal(UIUtils.getString(R.string.no_data));
            wristbandstep.setStep(UIUtils.getString(R.string.no_data));
            wristbandstep.setDistance(UIUtils.getString(R.string.no_data));
            Wristbandstep wristbandstep2 = null;
            wristbandstep2 = parWristBandStep(modleList.get(0));
            // Logger.myLog("wristbandstep2:" + wristbandstep2.getStepNum());
            wristbandstep.setCompareStep(0 - Long.parseLong(TextUtils.isEmpty(wristbandstep2.getStepNum()) ? "0" : wristbandstep2.getStepNum()) + "");
            wristbandstep.setLastSyncTime(System.currentTimeMillis());
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            return wristbandstep;
        } else {
            WatchSportMainData wristbandstep = new WatchSportMainData();
            wristbandstep.setCompareStep("0");
            wristbandstep.setStep(UIUtils.getString(R.string.no_data));
            wristbandstep.setCal(UIUtils.getString(R.string.no_data));
            wristbandstep.setDistance(UIUtils.getString(R.string.no_data));
            wristbandstep.setLastSyncTime(System.currentTimeMillis());
            wristbandstep.setDateStr(DateUtil.dataToString(new Date(), "yyyy-MM-dd"));
            return wristbandstep;
        }*/
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @Override
    public HeartRateMainData getWatchHeartRteLastTwoData() {
        HeartRateMainData heartRateMainData = new HeartRateMainData();
        List<Bracelet_W311_24HDataModel> model = Bracelet_W311_24HDataModelAction.find_Bracelet_w311_24HDataModelByDeviceId_Last_Two_HR(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        if (model != null && model.size() > 0) {
            Logger.myLog("getLast HeartRte Data" + "model.size()" + model.size() + "============mode:" + model.toString());
            Bracelet_W311_24HDataModel watch_w516_24HDataModel1 = model.get(0);
            WristbandHrHeart heart1 = parseHrData(watch_w516_24HDataModel1);
            heartRateMainData.setLastSyncTime(watch_w516_24HDataModel1.getTimestamp());
            heartRateMainData.setHeartRate(heart1.getAvgHr());
            heartRateMainData.setDateStr(watch_w516_24HDataModel1.getDateStr());
           /* if (model.size() >= 2) {
                Bracelet_W311_24HDataModel watch_w516_24HDataModel2 = model.get(1);
                WristbandHrHeart heart2 = parseHrData(watch_w516_24HDataModel2);
                heartRateMainData.setCompareHeartRate(heart1.getAvgHr() - heart2.getAvgHr());
            } else {

                if (heart1.getAvgHr() != 0) {
                    heartRateMainData.setCompareHeartRate(heart1.getAvgHr());
                } else {
                    heartRateMainData.setCompareHeartRate(0);
                }
                //  heartRateMainData.setCompareHeartRate(0);
            }*/
        } else {
            heartRateMainData.setDateStr("");
            heartRateMainData.setCompareHeartRate(0);
            heartRateMainData.setHeartRate(0);
        }
        return heartRateMainData;
    }


    /**
     * @param strDate
     * @return
     */
    @Override
    public WatchSleepDayData getWatchSleepDayData(String strDate, String deviceId) {
        Bracelet_W311_24HDataModel currentModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), strDate, deviceId);
        Bracelet_W311_24HDataModel lastmModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), TimeUtils.getLastDayStr(strDate), deviceId);

        if (currentModel == null && lastmModel == null) {
            return null;
        }
//        Logger.myLog(" currentModel == " + currentModel.toString() + " lastmModel == " + lastmModel.toString());
        WatchSleepDayData watchSleepDayData = parBraceletSleepDayData(lastmModel, currentModel, strDate);
        return watchSleepDayData;
    }

    @Override
    public void updateTodayTotalStep(long step, String strDate, String deviceId) {

    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public WatchSleepDayData getWatchSleepDayLastData(String deviceId) {

        Logger.myLog("getWatchSleepDayLastData:" + deviceId);

        WatchSleepDayData watchSleepDayData = new WatchSleepDayData();
        Bracelet_W311_24HDataModel currentModel = Bracelet_W311_24HDataModelAction.findLastTwoDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        if (currentModel != null) {
            int type = parSleepDayType(currentModel);
            switch (type) {
                case day20beforhasData:
                    if (currentModel != null) {
                        Bracelet_W311_24HDataModel lastmModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), TimeUtils.getLastDayStr(currentModel.getDateStr()), deviceId);
                        watchSleepDayData = parWatchSleepDayLastData(lastmModel, currentModel);
                        //watchSleepDayData = parBraceletSleepDayData(lastmModel, currentModel, currentModel.getDateStr());
                    }
                    break;
                case day20afterhasData:
                case alldayData:
                    String nextStr = TimeUtils.getNextDayStr(currentModel.getDateStr());
                    Bracelet_W311_24HDataModel nextmModel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), nextStr, deviceId);

                    if (nextmModel == null) {
                        nextmModel = new Bracelet_W311_24HDataModel();
                        nextmModel.setDateStr(nextStr);
                    }
                    watchSleepDayData = parWatchSleepDayLastData(currentModel, nextmModel);
                    break;

            }
        }


        return watchSleepDayData;
    }

    @Override
    public SleepMainData getWatchSleepDayLastFourData(String deviceId) {

        WatchSleepDayData watchSleepDayData1 = getWatchSleepDayLastData(deviceId);
        SleepMainData data = new SleepMainData();
        if (watchSleepDayData1 != null) {
            data.setMinute(watchSleepDayData1.getTotalSleepTime());
            data.setLastSyncDate(watchSleepDayData1.getDateStr());
        } else {
            data.setLastSyncDate("");
            data.setMinute(0);
        }
        return data;
    }


    public WatchSleepDayData getWatchSleepDayData2(Bracelet_W311_24HDataModel today2, List<Bracelet_W311_24HDataModel> bracelet_w311_24HDataModels, String deviceId) {
        int type = parSleepDayType(today2);
        Bracelet_W311_24HDataModel twoCurrentDay;
        Bracelet_W311_24HDataModel twoLastDay;
        WatchSleepDayData watchSleepDayData2 = null;
        switch (type) {
            case alldayData:
            case day20afterhasData:
                String nextStr = TimeUtils.getNextDayStr(today2.getDateStr());
                twoCurrentDay = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), nextStr, deviceId);
                twoLastDay = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), today2.getDateStr(), deviceId);
                if (twoCurrentDay == null) {
                    twoCurrentDay = new Bracelet_W311_24HDataModel();
                    twoCurrentDay.setDateStr(nextStr);
                }
                watchSleepDayData2 = parWatchSleepDayLastData(twoLastDay, twoCurrentDay);
                break;
            case allDayNoData:
                Logger.myLog("getWatchSleepDayLastFourData allDayNoData");
                break;
            case day20beforhasData:
                if (bracelet_w311_24HDataModels.size() >= 3) {
                    // today2 = bracelet_w311_24HDataModels.get(2);
                    twoCurrentDay = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), today2.getDateStr(), deviceId);
                    twoLastDay = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), TimeUtils.getLastDayStr(twoCurrentDay.getDateStr()), deviceId);
                    watchSleepDayData2 = parWatchSleepDayLastData(twoLastDay, twoCurrentDay);
                    Logger.myLog("getWatchSleepDayLastFourData day20afterhasData");
                }
                break;
        }
        return watchSleepDayData2;

    }


    /**
     * ????????????????????????list
     *
     * @param strDate
     * @return
     */
    @Override
    public ArrayList<String> getSleepMonthData(String strDate, String devcieId) {


        //???????????????????????????????????????????????????  2019-08
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

            WatchSleepDayData watchSleepDayData = getWatchSleepDayData(DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "yyyy-MM-dd"), devcieId);

            if (watchSleepDayData != null) {
                if (watchSleepDayData.getTotalSleepTime() > 0) {
                    dataList.add(watchSleepDayData.getDateStr());
                }
            }
        }
        return dataList;

       /* ArrayList<String> dataList = (ArrayList<String>) Bracelet_W311_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModel_CurrentMonth_DateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);

        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                Logger.myLog("watch:" + strDate + ":" + dataList.get(i));
            }
            return dataList;
        } else {
            dataList = new ArrayList<>();
            return dataList;
        }*/
    }


    @Override
    public StandardMainData getBraceletWeekData(String userId, int targetStep) {
        String strdeviceId = AppConfiguration.braceletID;

        //????????????????????????????????????

        // ;
        JkConfiguration.WATCH_GOAL = targetStep;
        Calendar calendar = Calendar.getInstance();

        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String strEndTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));

        int startYear, startMonth, startDay;
        List<Bracelet_W311_24HDataModel> model = new ArrayList<>();

        String strStartTime;
        StandardMainData standardMainData = new StandardMainData();
        standardMainData.setLastSyncTime(0);
        int target = 0;
        ArrayList<StepBean> stepList = new ArrayList<>();
        stepList.clear();
        for (int i = week; i >= 1; i--) {
            strStartTime = DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "yyyy-MM-dd");
            Bracelet_W311_24HDataModel tempmodel = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(userId, strStartTime, strdeviceId);

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
                Logger.myLog(tempmodel.getTotalSteps() + "tempmodel == " + tempmodel.toString());
            }
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        ArrayList<StepBean> stepListDesc = new ArrayList<>();

        for (int i = stepList.size() - 1; i >= 0; i--) {
            stepListDesc.add(stepList.get(i));
        }
        String strLastweekEndTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        String strrLastWeekStartTime = calendar.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.MONTH) + 1) + "-" + CommonDateUtil.formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        List<Bracelet_W311_24HDataModel> lastmodel = Bracelet_W311_24HDataModelAction.findWatch_W516_Watch_W516_24HDataMode_last_month_Asc(userId, strrLastWeekStartTime, strLastweekEndTime, strdeviceId);

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
    public WristbandHrHeart getLastDayHrData() {
        Bracelet_W311_24HDataModel model = Bracelet_W311_24HDataModelAction.findWatch_W516_Watch_W516_24H_Hr_DataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);

        // Bracelet_W311_24HDataModel model = Bracelet_W311_24HDataModelAction.findWatch_W516_Watch_W516_24HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
        if (model != null) {
            wristbandHrHeart = parseHrData(model);
        }
        return wristbandHrHeart;
    }


    @Override
    public ArrayList<String> getMonthHrDataToStrDate(String strDate, String deviceId) {
        List<Bracelet_W311_24HDataModel> dataList = Bracelet_W311_24HDataModelAction.findWatch_W516_Watch_W516_24HDataMode_CurrentMonth_Hr_DateStr(strDate, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        ArrayList<String> str = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {

            Integer[] hrArry = new Gson().fromJson(dataList.get(i).getHrArray(), Integer[].class);
            if (hrArry == null) {
                continue;
            }
            ArrayList<Integer> arrayList = new ArrayList<Integer>(Arrays.asList(hrArry));
            if (arrayList == null || (arrayList != null && Collections.max(arrayList) == 0)) {
                continue;
            }
            str.add(dataList.get(i).getDateStr());
        }
        return str;
    }

    /**
     * ????????????????????????
     *
     * @param strDate
     * @return
     */
    @Override
    public WristbandHrHeart getDayHrData(String strDate, String deviceId) {
        Bracelet_W311_24HDataModel model = Bracelet_W311_24HDataModelAction.findBracelet_HDataModelByDeviceId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), strDate, deviceId);
        WristbandHrHeart wristbandHrHeart = new WristbandHrHeart();
        if (model != null) {
            wristbandHrHeart = parseHrData(model);

        }
        return wristbandHrHeart;
    }


}
