package com.isport.blelibrary.db.table.f18;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Admin
 * Date 2022/1/17
 */
@Entity
public class F18CommonDbBean {

    @Id
    private Long id;

    private String userId;

    private String deviceMac;

    private String deviceName;

    private String DbType;

    //保留时间 yyyy-MM-dd格式
    private String dateStr;

    //保留时间戳 精确到秒
    private Long currTime;

    //内容，根据不同的type转换成josn字符串后保存
    private String typeDataStr;



    public F18CommonDbBean() {
    }

    public F18CommonDbBean(String userId, String deviceMac, String deviceName, String dbType, String dateStr, Long currTime, String typeDataStr) {
        this.userId = userId;
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
        DbType = dbType;
        this.dateStr = dateStr;
        this.currTime = currTime;
        this.typeDataStr = typeDataStr;
    }

    @Generated(hash = 2093876128)
    public F18CommonDbBean(Long id, String userId, String deviceMac, String deviceName, String DbType, String dateStr, Long currTime, String typeDataStr) {
        this.id = id;
        this.userId = userId;
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
        this.DbType = DbType;
        this.dateStr = dateStr;
        this.currTime = currTime;
        this.typeDataStr = typeDataStr;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDbType() {
        return DbType;
    }

    public void setDbType(String dbType) {
        DbType = dbType;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Long getCurrTime() {
        return System.currentTimeMillis()/1000;
    }

    public void setCurrTime(Long currTime) {
        this.currTime = currTime;
    }

    public String getTypeDataStr() {
        return typeDataStr;
    }

    public void setTypeDataStr(String typeDataStr) {
        this.typeDataStr = typeDataStr;
    }

    @Override
    public String toString() {
        return "F18CommonDbBean{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", DbType='" + DbType + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", currTime=" + currTime +
                ", typeDataStr='" + typeDataStr + '\'' +
                '}';
    }
}
