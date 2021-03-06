package com.isport.blelibrary.db.action.W81Device;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isport.blelibrary.db.CommonInterFace.WatchData;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.table.F18StepHourMap;
import com.isport.blelibrary.db.table.f18.F18StepBean;
import com.isport.blelibrary.db.table.w811w814.W81DeviceDetailData;
import com.isport.blelibrary.gen.W81DeviceDetailDataDao;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class W81DeviceDataAction {

    W81DeviceDetailDataDao w81DeviceDetailDataDao;

    public W81DeviceDataAction() {
        w81DeviceDetailDataDao = BleAction.getsW81DeviceDetailDataDao();
    }


    public synchronized void updateWriId(String deviceId, String userId, String strDate, String wristbandSportDetailId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(strDate) || TextUtils.isEmpty(wristbandSportDetailId)) {
            return;
        }

        W81DeviceDetailData w81DeviceDetailData = getW81DeviceDetialData(deviceId, userId, strDate);
        //Logger.myLog("updateWriId:" + w81DeviceDetailData);
        if (w81DeviceDetailData != null) {
            w81DeviceDetailData.setWristbandSportDetailId(wristbandSportDetailId);
            saveW81DeviceDetailData(w81DeviceDetailData);
        }

    }


    public synchronized void saveDeviceStepArrayData(String tag,String deviceId, String userId,String wrisId, String dateStr,String stepArray){

        //Log.e("TAG","-----来源="+tag +" 日期="+dateStr+"\n"+stepArray);
        String[] newCountStepArray = getDetailCountStep(stepArray);
        int currCountStep = Integer.parseInt(newCountStepArray == null ? String.valueOf(0) : newCountStepArray[0]);
        float currCountDis = Float.parseFloat(newCountStepArray == null ?String.valueOf(0) : newCountStepArray[1]);
        float currKcal = Float.parseFloat(newCountStepArray == null ? String.valueOf(0) : newCountStepArray[2]);


        if(dateStr.equals(DateUtil.getCurrDay()))
            wrisId = "0";

        W81DeviceDetailData w81DeviceDetailData = getW81DeviceDetialData(deviceId, userId, dateStr);
        if(w81DeviceDetailData != null){
            String dealStep = analysisStepArray(tag,w81DeviceDetailData.getStep(),stepArray,stepArray);
           // Log.e("TT","-------处理完成后的计步="+dealStep);
            w81DeviceDetailData.setStepArray(dealStep);
            w81DeviceDetailData.setDateStr(dateStr);

            if(dateStr.equals(DateUtil.getCurrDay()))
               w81DeviceDetailData.setWristbandSportDetailId("0");
            Log.e("TT","------处理完成后的id="+w81DeviceDetailData.getDateStr() +"   "+w81DeviceDetailData.getWristbandSportDetailId());
            w81DeviceDetailData.setWristbandSportDetailId(wrisId);

            if(newCountStepArray != null){
                w81DeviceDetailData.setStep(Integer.valueOf(newCountStepArray[0]));
                float countDis = Float.valueOf(newCountStepArray[1]);
                float countKcal = Float.valueOf(newCountStepArray[2]);
                w81DeviceDetailData.setDis((int) (countDis));
                w81DeviceDetailData.setCal((int) countKcal);
            }

          //  Log.e("保存计步",w81DeviceDetailData.toString());
            saveW81DeviceDetailData(w81DeviceDetailData);
        }else{
            W81DeviceDetailData wb = new W81DeviceDetailData();
            wb.setStep(currCountStep);
            float countDis = currCountDis;
            float countKcal = currKcal;
            wb.setDis((int) (countDis ));
            wb.setCal((int) countKcal);

            wb.setUserId(userId);
            wb.setDeviceId(deviceId);
            wb.setWristbandSportDetailId(wrisId);
            wb.setDateStr(dateStr);
            wb.setStepArray(stepArray);

            saveW81DeviceDetailData(wb);

        }
    }


    private String[] getDetailCountStep(String stepArray){
        List<String[]> currStepList = new Gson().fromJson(stepArray,new TypeToken<List<String[]>>(){}.getType());
        if(currStepList != null && currStepList.size()>0){
            int countStep = 0;
            float countDistance = 0.0f;
            float countKcal = 0.0f;
            for(String[] st : currStepList){
                //步数
                countStep +=Integer.valueOf(st[1]);
                //距离
                countDistance = StepArithmeticUtil.addNumber(countDistance,Float.parseFloat(st[2]));
                //卡路里
                countKcal = StepArithmeticUtil.addNumber(countKcal,Float.parseFloat(st[3]));
            }
            return new String[]{String.valueOf(countStep),String.valueOf(countDistance),String.valueOf(countKcal)};
        }
        return null;
    }

    private String[] getDetailCountStep(int localCountStep,String stepArray){
        List<String[]> currStepList = new Gson().fromJson(stepArray,new TypeToken<List<String[]>>(){}.getType());
        if(currStepList != null && currStepList.size()>0){
            int countStep = 0;
            float countDistance = 0.0f;
            float countKcal = 0.0f;
            for(String[] st : currStepList){
                //步数
                countStep +=Integer.valueOf(st[1]);
                //距离
                countDistance = StepArithmeticUtil.addNumber(countDistance,Float.parseFloat(st[2]));
                //卡路里
                countKcal = StepArithmeticUtil.addNumber(countKcal,Float.parseFloat(st[3]));
            }
            return new String[]{String.valueOf(countStep),String.valueOf(countDistance),String.valueOf(countKcal)};
        }
        return null;
    }


    //合并步数方法
    //当tag=0时，表明是从手表同步的步数，直接合并
    private String analysisStepArray(String tag,int localStepCount ,String currStepArr,String localStepArr){

        Log.e("TAG","------需要保存的="+currStepArr+"\n"+ "本地的="+localStepArr);

        //昨天的数据，已经组装完成，直接保存
        if(tag.equals("1")){

            return currStepArr;
        }
       //判断当前总的计步，如果详情计步比总步数小，可以合并，否则不合并
        int countStep = localStepCount;

        if(localStepArr == null || localStepArr.equals("[]"))
            return currStepArr;
        if(currStepArr == null || currStepArr.equals("[]"))
            return localStepArr;
        //当前的计步详情
        List<String[]> currStepList = new Gson().fromJson(currStepArr,new TypeToken<List<String[]>>(){}.getType());

        //已经存在的计步详情
        List<String[]> localStepList = new Gson().fromJson(localStepArr,new TypeToken<List<String[]>>(){}.getType());
        if(localStepList == null || localStepList.size() == 0)
            return currStepArr;

        if(currStepList == null || currStepList.size() == 0)
            return localStepArr;


        //直接替换即可
        Map<String,String[]> tempStepMap = F18StepHourMap.getF1824HourMap();
        for(String[] st : currStepList){
            tempStepMap.put(st[0],st);
        }

        for(String[] lSt : localStepList){
            tempStepMap.put(lSt[0],lSt);
        }
        List<String> hourList = new ArrayList<>();
        for(Map.Entry<String,String[]> mm : tempStepMap.entrySet()){
            hourList.add(mm.getKey());
        }
        //排序
        Collections.sort(hourList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        List<String[]> stepArrayResultList = new ArrayList<>();
        for(int i = 0;i<hourList.size();i++){
           String[] tmpItem = tempStepMap.get(hourList.get(i));
           stepArrayResultList.add(tmpItem);
        }
       // Log.e("TAG","----全部替换后="+tempStepMap.toString()+"\n"+new Gson().toJson(hourList)+"\n"+"最终结果="+new Gson().toJson(stepArrayResultList));


        return new Gson().toJson(stepArrayResultList);


    }



    //保存步数数据
    public synchronized void saveW81DeviceStepData(String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int step, int dis, int cal, boolean isNet) {

        W81DeviceDetailData w81DeviceDetailData = getW81DeviceDetialData(deviceId, userId, dateStr);
        if (w81DeviceDetailData != null) {

           // Logger.myLog("saveW81DeviceStepData" + w81DeviceDetailData + "cal:" + cal + "dis:" + dis + "step:" + step);
            if (w81DeviceDetailData.getStep() >= step && isNet) {
                return;
            }
           /* if (w81DeviceDetailData.getStep() == step && w81DeviceDetailData.getCal() == cal && w81DeviceDetailData.getDis() == w81DeviceDetailData.getDis()) {
                Logger.myLog("saveW81DeviceStepData" + w81DeviceDetailData);
                return;
            }*/

            w81DeviceDetailData.setWristbandSportDetailId(wristbandSportDetailId);
            w81DeviceDetailData.setCal(cal);
            w81DeviceDetailData.setDis(dis);
            w81DeviceDetailData.setStep(step);
            w81DeviceDetailData.setTimestamp(timestamp);
            saveW81DeviceDetailData(w81DeviceDetailData);

        } else {
            saveDefW81DeviceDetailData(userId, deviceId, wristbandSportDetailId, dateStr, timestamp, step, dis, cal, 0, 0, 0, 0, "", "", "", WatchData.NO_SLEEP, WatchData.NO_HR, 0, 0);
        }
    }

    public synchronized void saveW81DeviceSleepData(String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, int totalTime,
                                                    int restfulTime,
                                                    int lightTime,
                                                    int soberTime, String sleepDetail) {

        W81DeviceDetailData w81DeviceDetailData = getW81DeviceDetialData(deviceId, userId, dateStr);


        Logger.myLog("---saveW81DeviceSleepData:deviceId " + deviceId + "userId:" + userId + "dateStr:" + dateStr);
        Logger.myLog("------w81DeviceDetailData" + w81DeviceDetailData);

        int hasSleep;
        if (totalTime != 0 || restfulTime+lightTime != 0) {
            hasSleep = WatchData.HAS_SLEEP;
        } else {
            hasSleep = WatchData.NO_SLEEP;
        }

        if (w81DeviceDetailData != null) {
            w81DeviceDetailData.setWristbandSportDetailId(wristbandSportDetailId);
            w81DeviceDetailData.setTotalTime(totalTime);
            w81DeviceDetailData.setRestfulTime(restfulTime);
            w81DeviceDetailData.setLightTime(lightTime);
            w81DeviceDetailData.setSoberTime(soberTime);
            w81DeviceDetailData.setSleepArray(sleepDetail);
            w81DeviceDetailData.setHasSleep(hasSleep);
            saveW81DeviceDetailData(w81DeviceDetailData);

        } else {
            saveDefW81DeviceDetailData(userId, deviceId, wristbandSportDetailId, dateStr, timestamp, 0, 0, 0, totalTime, restfulTime, lightTime, soberTime, "", sleepDetail, "", hasSleep, WatchData.NO_HR, 0, 0);
        }


    }

    //保存睡眠数据
    // 保存心率数据
    public synchronized void saveW81DeviceHrData(String deviceId, String userId, String wristbandSportDetailId, String dateStr, long timestamp, List<Integer> hrList, int timeInterval) {


        int hasHr = WatchData.HAS_HR;
        Gson gson = new Gson();
        int avg = ParseData.calAvgHr(hrList);
        if (avg != 0) {
            hasHr = WatchData.HAS_HR;
        } else {
            hasHr = WatchData.NO_HR;
        }


      //  Logger.myLog(" saveW81DeviceHrData hasHr:" + hasHr + "avgHr:" + avg + "hrList:" + hrList);


        W81DeviceDetailData w81DeviceDetailData = getW81DeviceDetialData(deviceId, userId, dateStr);
        if (w81DeviceDetailData != null) {
            w81DeviceDetailData.setWristbandSportDetailId(wristbandSportDetailId);
            w81DeviceDetailData.setTimeInterval(timeInterval);
            w81DeviceDetailData.setHrArray(gson.toJson(hrList));
            saveW81DeviceDetailData(w81DeviceDetailData);
            w81DeviceDetailData.setHasHR(hasHr);
            w81DeviceDetailData.setAvgHR(avg);

            //保存数据
            saveW81DeviceDetailData(w81DeviceDetailData);
        } else {
            saveDefW81DeviceDetailData(userId, deviceId, wristbandSportDetailId, dateStr, timestamp, 0, 0, 0, 0, 0, 0, 0, "", "", gson.toJson(hrList), WatchData.NO_SLEEP, hasHr, avg, timeInterval);
        }

    }


    public synchronized W81DeviceDetailData getW81DeviceDetialData(String deviceId, String userId, String dateStr) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(dateStr)) {
            return null;
        }
          Logger.myLog("getW81DeviceDetialData deviceId" + deviceId + "userId:" + userId + "dataSr" + dateStr);
        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder(W81DeviceDetailData.class);
        queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.DateStr.eq(dateStr)).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);
        //queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId)).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);
        //Logger.myLog("getW81DeviceDetialData size" + queryBuilder.list().size());
        List<W81DeviceDetailData> list = queryBuilder.list();
        if (list.size() > 0) {
            //如果有两条数据需要删除一条
            W81DeviceDetailData w81DeviceDetailData = queryBuilder.list().get(0);
            for (int i = 1; i < list.size(); i++) {
                W81DeviceDetailDataDao deviceTypeTableDao = BleAction.getsW81DeviceDetailDataDao();
                deviceTypeTableDao.delete(list.get(i));
            }
            return w81DeviceDetailData;
        } else {
            return null;
        }
    }

    public synchronized List<W81DeviceDetailData> getUnUpgradeW81DeviceDetialData(String deviceId, String userId, String wriid) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(wriid)) {
            return null;
        }
        //  Logger.myLog("getW81DeviceDetialData deviceId" + deviceId + "userId:" + userId + "dataSr" + wriid);
        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder(W81DeviceDetailData.class);
        queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.WristbandSportDetailId.eq(wriid)).distinct();
        //queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId)).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);
        return queryBuilder.list();
    }


    public synchronized List<W81DeviceDetailData> getUnUpgradeW81DeviceDetialData(String deviceId, String userId,boolean isToday) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId) ) {
            return null;
        }
        //  Logger.myLog("getW81DeviceDetialData deviceId" + deviceId + "userId:" + userId + "dataSr" + wriid);
        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder(W81DeviceDetailData.class);
        queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId)).distinct();
        //queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId)).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);
        return queryBuilder.list();
    }

    public synchronized W81DeviceDetailData getW81DeviceSleepLastest(String deviceId, String userId, String strDate) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder(W81DeviceDetailData.class);

        if (TextUtils.isEmpty(strDate)) {
            queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.HasSleep.eq(WatchData.HAS_SLEEP)).orderDesc(W81DeviceDetailDataDao.Properties.DateStr).offset(0).limit(1);
        } else {
            queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.DateStr.eq(strDate)).orderDesc(W81DeviceDetailDataDao.Properties.DateStr).offset(0).limit(1);

        }
        if (queryBuilder.list().size() > 0) {
            W81DeviceDetailData w81DeviceDetailData = queryBuilder.list().get(0);
            return w81DeviceDetailData;
        } else {
            return null;
        }
    }

    public synchronized W81DeviceDetailData getW81DeviceHrData(String deviceId, String userId, String strDate) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder(W81DeviceDetailData.class);

        // Logger.myLog("getLastHrData: strDate" + "userId：" + userId + "deviceId:" + deviceId);
        if (TextUtils.isEmpty(strDate)) {
            queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.HasHR.eq(WatchData.HAS_HR)).orderDesc(W81DeviceDetailDataDao.Properties.DateStr).offset(0).limit(1);

        } else {
            queryBuilder.where(W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.DateStr.eq(strDate));

        }
        if (queryBuilder.list().size() > 0) {
            W81DeviceDetailData w81DeviceDetailData = queryBuilder.list().get(0);
            // Logger.myLog("getLastHrData: strDate" + w81DeviceDetailData);
            return w81DeviceDetailData;
        } else {
            return null;
        }
    }

    private synchronized long saveW81DeviceDetailData(W81DeviceDetailData deviceDetailData) {
        W81DeviceDetailDataDao w81DeviceDetailDataDao = BleAction.getsW81DeviceDetailDataDao();
        if (w81DeviceDetailDataDao == null) {
            return 0L;
        }
        long id = w81DeviceDetailDataDao.insertOrReplace(deviceDetailData);
        Logger.myLog("saveW81DeviceDetailData：" + deviceDetailData.toString() + " save id:" + id);
        return id;


    }





    private synchronized long saveDefW81DeviceDetailData(String userId, String deviceId,
                                                         String wristbandSportDetailId, String dateStr, Long timestamp,
                                                         int step, int dis, int cal, int totalTime, int restfulTime,
                                                         int lightTime, int soberTime, String stepArray, String sleepArray,
                                                         String hrArray, int hasSleep, int hasHR, int avgHR, int timeInterval) {

        W81DeviceDetailData deviceDetailData = new W81DeviceDetailData();
        deviceDetailData.setUserId(userId);
        deviceDetailData.setDeviceId(deviceId);
        deviceDetailData.setWristbandSportDetailId(wristbandSportDetailId);
        deviceDetailData.setDateStr(dateStr);
        deviceDetailData.setTimestamp(timestamp);
        deviceDetailData.setStep(step);
        deviceDetailData.setDis(dis);
        deviceDetailData.setCal(cal);
        deviceDetailData.setTotalTime(totalTime);
        deviceDetailData.setRestfulTime(restfulTime);
        deviceDetailData.setLightTime(lightTime);
        deviceDetailData.setSoberTime(soberTime);
        deviceDetailData.setStepArray(stepArray);
        deviceDetailData.setSleepArray(sleepArray);
        deviceDetailData.setHrArray(hrArray);
        deviceDetailData.setHasHR(hasHR);
        deviceDetailData.setHasSleep(hasSleep);
        deviceDetailData.setAvgHR(avgHR);
        deviceDetailData.setTimeInterval(timeInterval);

        return saveW81DeviceDetailData(deviceDetailData);
    }

    /**
     * 获取当前的用户用户当月的数据
     *
     * @param dateStr
     * @return
     */
    public synchronized List<String> findCurrentMonthStepDateStr(String dateStr, String uerid, String deviceId) {
        //查询某一字段中不重复的字段内容
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }

        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder
                (W81DeviceDetailData.class);
        queryBuilder.where(W81DeviceDetailDataDao.Properties.UserId.eq(uerid), W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);


        ArrayList<String> strings = new ArrayList<>();


        List<W81DeviceDetailData> models = queryBuilder.list();

        if (models != null) {
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).getStep() != 0) {
                    strings.add(models.get(i).getDateStr());
                }
            }
        }


        return strings;
    }

    /**
     * 获取当前的用户当月的心率数据
     *
     * @param dateStr
     * @return
     */
    /**
     * @param dateStr
     * @param userId
     * @param deviceId
     * @param findType 查找心率数据还是睡眠数据
     * @return
     */

    private final Gson gson = new Gson();

    public synchronized List<String> findCurrentMonthHrOrSleepDateStr(String dateStr, String userId, String deviceId, int findType) {
        //查询某一字段中不重复的字段内容
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userId)) {
            return null;
        }

        Logger.myLog("findCurrentMonthHrOrSleepDateStr:" + dateStr + ",userId:" + userId + ",devcieId:" + deviceId + ",findType:" + findType);

        QueryBuilder<W81DeviceDetailData> queryBuilder = BleAction.getDaoSession().queryBuilder
                (W81DeviceDetailData.class);

        if (findType == WatchData.HAS_HR) {
            queryBuilder.where(W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.DateStr.like("%" + dateStr + "%"), W81DeviceDetailDataDao.Properties.HasHR.eq(WatchData.HAS_HR)).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);

        } else if (findType == WatchData.HAS_SLEEP) {
           // queryBuilder.where(W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.DateStr.like("%" + dateStr + "%"), W81DeviceDetailDataDao.Properties.HasSleep.eq(WatchData.HAS_SLEEP)).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);

            queryBuilder.where(W81DeviceDetailDataDao.Properties.UserId.eq(userId), W81DeviceDetailDataDao.Properties.DeviceId.eq(deviceId), W81DeviceDetailDataDao.Properties.DateStr.like("%" + dateStr + "%")).orderAsc(W81DeviceDetailDataDao.Properties.DateStr);

        }

        ArrayList<String> strings = new ArrayList<>();


        List<W81DeviceDetailData> models = queryBuilder.list();

        Log.e("心率和睡眠","----models="+new Gson().toJson(models));



        if (models != null) {
            if (findType == WatchData.HAS_HR){  //心率
             for(int i = 0;i<models.size();i++){
                 W81DeviceDetailData wb = models.get(i);
                 if(wb.getHrArray() != null && !wb.getHrArray().equals("[]")){
                     strings.add(wb.getDateStr());
                 }
             }
            }

            if(findType == WatchData.HAS_SLEEP){    //睡眠

                //深睡和浅睡的累计
                int deepAndLightTime = 0;


                for (int i = 0; i < models.size(); i++) {
                    deepAndLightTime = 0;
                    W81DeviceDetailData ib = models.get(i);
                    if(deviceId.startsWith("W81")){
                        if(ib.getTotalTime()>0)
                            strings.add(ib.getDateStr());
                    }else{
                        String sleepStr = ib.getSleepArray();
                        if(sleepStr == null || sleepStr.equals("[]"))
                            continue;
                        List<String[]> slList = gson.fromJson(sleepStr,new TypeToken<List<String[]>>(){}.getType());
                        if(slList != null && slList.size()>0){
                            for(String[] st : slList){
                                int status = Integer.parseInt(st[0]);
                                int sleepTime = Integer.parseInt(st[3]);

                                if(status == 2 || status == 3){
                                    deepAndLightTime += sleepTime;
                                }
                            }
                            if(deepAndLightTime >0){
                                strings.add(ib.getDateStr());
                            }
                        }






//                        if(ib.getRestfulTime()+ib.getLightTime() != 0){
//                            strings.add(models.get(i).getDateStr());
//                        }
                    }

                }
            }

        }


        return strings;
    }
}
