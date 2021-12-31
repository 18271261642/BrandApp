package com.isport.blelibrary.db.table.watch_w516;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Author
 * @Date 2019/2/24
 * @Fuction
 */
@Entity
public class Watch_W560_AlarmModel {

//    w560手表闹钟的数据库
//    userId	用户ID	string
//    deviceId	设备ID	string
//    repeatCount	重复	string
//    timeString	闹钟时间 hh:mm	string
//    messageString	闹钟信息	string

    @Id
    private Long id;
    private int index;
    private boolean isEnable;
    private String name;
    private String userId;
    private String deviceId;
    private int repeatCount;
    private String timeString;
    private String messageString;
    public String getMessageString() {
        return this.messageString;
    }
    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }
    public String getTimeString() {
        return this.timeString;
    }
    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }
    public int getRepeatCount() {
        return this.repeatCount;
    }
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getIsEnable() {
        return this.isEnable;
    }
    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1788812374)
    public Watch_W560_AlarmModel(Long id, int index, boolean isEnable, String name,
            String userId, String deviceId, int repeatCount, String timeString,
            String messageString) {
        this.id = id;
        this.index = index;
        this.isEnable = isEnable;
        this.name = name;
        this.userId = userId;
        this.deviceId = deviceId;
        this.repeatCount = repeatCount;
        this.timeString = timeString;
        this.messageString = messageString;
    }
    @Generated(hash = 162388423)
    public Watch_W560_AlarmModel() {
    }

    @Override
    public String toString() {
        return "Watch_W560_AlarmModel{" +
                "id=" + id +
                ", index=" + index +
                ", isEnable=" + isEnable +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", repeatCount=" + repeatCount +
                ", timeString='" + timeString + '\'' +
                ", messageString='" + messageString + '\'' +
                '}';
    }
}
