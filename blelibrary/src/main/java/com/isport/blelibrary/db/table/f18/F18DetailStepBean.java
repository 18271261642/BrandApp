package com.isport.blelibrary.db.table.f18;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 用于F18保存详细计步数据的bean
 * Created by Admin
 * Date 2022/2/26
 */
@Entity
public class F18DetailStepBean {

    @Id
    private Long id;

    //userId
    private String userId;

    //typeId
    private String deviceTypeId;

    //日期 yyyy-MM-dd 格式
    private String dayStr;

    //时间戳
    private long timeLong;

    //步数
    private int step;

    //距离
    private float distance;

    //卡路里
    private float kcal;

    @Generated(hash = 709398268)
    public F18DetailStepBean(Long id, String userId, String deviceTypeId,
            String dayStr, long timeLong, int step, float distance, float kcal) {
        this.id = id;
        this.userId = userId;
        this.deviceTypeId = deviceTypeId;
        this.dayStr = dayStr;
        this.timeLong = timeLong;
        this.step = step;
        this.distance = distance;
        this.kcal = kcal;
    }

    @Generated(hash = 1235186861)
    public F18DetailStepBean() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public long getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(long timeLong) {
        this.timeLong = timeLong;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    @Override
    public String toString() {
        return "F18DetailStepBean{" +
                "userId='" + userId + '\'' +
                ", deviceTypeId='" + deviceTypeId + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", timeLong=" + timeLong +
                ", step=" + step +
                ", distance=" + distance +
                ", kcal=" + kcal +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
