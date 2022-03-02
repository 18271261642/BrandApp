package com.isport.brandapp.home.bean.http;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @创建者 bear
 * @创建时间 2019/3/11 10:24
 * @描述
 */
public class WatchSleepDayData implements Serializable {

    private int[] sleepArry;
    private int totalSleepTime;//总睡眠时长，不包括清醒部分
    private int deepSleepTime;//深睡
    private String dateStr;//日期
    private int lightLV1SleepTime;//浅睡level 1
    private int lightLV2SleepTime;//浅睡level 2
    private int awakeSleepTime;//清醒
    private int SporadicNapSleepTime;//零星小睡分钟数
    private String SporadicNapSleepTimeStr;//零星小睡时间段
    private int SporadicNapSleepTimes;//零星小睡的次数

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public int getSporadicNapSleepTimes() {
        return SporadicNapSleepTimes;
    }

    public void setSporadicNapSleepTimes(int sporadicNapSleepTimes) {
        SporadicNapSleepTimes = sporadicNapSleepTimes;
    }

    public int[] getSleepArry() {
        return sleepArry;
    }

    public void setSleepArry(int[] sleepArry) {
        this.sleepArry = sleepArry;
    }

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        this.totalSleepTime = totalSleepTime;
    }

    public int getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(int deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public int getLightLV1SleepTime() {
        return lightLV1SleepTime;
    }

    public void setLightLV1SleepTime(int lightLV1SleepTime) {
        this.lightLV1SleepTime = lightLV1SleepTime;
    }

    public int getLightLV2SleepTime() {
        return lightLV2SleepTime;
    }

    public void setLightLV2SleepTime(int lightLV2SleepTime) {
        this.lightLV2SleepTime = lightLV2SleepTime;
    }

    public int getAwakeSleepTime() {
        return awakeSleepTime;
    }

    public void setAwakeSleepTime(int awakeSleepTime) {
        this.awakeSleepTime = awakeSleepTime;
    }

    public int getSporadicNapSleepTime() {
        return SporadicNapSleepTime;
    }

    public void setSporadicNapSleepTime(int sporadicNapSleepTime) {
        SporadicNapSleepTime = sporadicNapSleepTime;
    }

    public String getSporadicNapSleepTimeStr() {
        return SporadicNapSleepTimeStr;
    }

    public void setSporadicNapSleepTimeStr(String sporadicNapSleepTimeStr) {
        SporadicNapSleepTimeStr = sporadicNapSleepTimeStr;
    }


    public String toString(int a) {
        return "WatchSleepDayData{" +
                "totalSleepTime=" + totalSleepTime +
                ", deepSleepTime=" + deepSleepTime +
                ", dateStr='" + dateStr + '\'' +
                ", lightLV1SleepTime=" + lightLV1SleepTime +
                ", lightLV2SleepTime=" + lightLV2SleepTime +
                ", awakeSleepTime=" + awakeSleepTime +
                ", SporadicNapSleepTime=" + SporadicNapSleepTime +
                ", SporadicNapSleepTimeStr='" + SporadicNapSleepTimeStr + '\'' +
                ", SporadicNapSleepTimes=" + SporadicNapSleepTimes +
                '}';
    }

    @Override
    public String toString() {
        return "WatchSleepDayData{" +
                "sleepArry=" + Arrays.toString(sleepArry) +
                ", totalSleepTime=" + totalSleepTime +
                ", deepSleepTime=" + deepSleepTime +
                ", dateStr='" + dateStr + '\'' +
                ", lightLV1SleepTime=" + lightLV1SleepTime +
                ", lightLV2SleepTime=" + lightLV2SleepTime +
                ", awakeSleepTime=" + awakeSleepTime +
                ", SporadicNapSleepTime=" + SporadicNapSleepTime +
                ", SporadicNapSleepTimeStr='" + SporadicNapSleepTimeStr + '\'' +
                ", SporadicNapSleepTimes=" + SporadicNapSleepTimes +
                '}';
    }
}
