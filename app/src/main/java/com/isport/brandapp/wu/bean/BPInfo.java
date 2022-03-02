package com.isport.brandapp.wu.bean;

import com.isport.brandapp.util.DateTimeUtils;

public class BPInfo {

    /**
     * [
     * {
     * "createTime": "2019-11-16T06:44:56.277Z",
     * "deviceId": "string",
     * "dpValue": 0,
     * "extend1": "string",
     * "extend2": "string",
     * "extend3": "string",
     * "spValue": "string",
     * "timestamp": "2019-11-16T06:44:56.277Z",
     * "userId": 0,
     * "wristbandBloodPressureId": 0
     * }
     * ]
     */

    private String deviceId;
    private int spValue;
    private int dpValue;
    private Long timestamp;
    private String userId;
    private String wristbandBloodPressureId;
    private String strDate;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getSpValue() {
        return spValue;
    }

    public void setSpValue(int spValue) {
        this.spValue = spValue;
    }

    public int getDpValue() {
        return dpValue;
    }

    public void setDpValue(int dpValue) {
        this.dpValue = dpValue;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWristbandBloodPressureId() {
        return wristbandBloodPressureId;
    }

    public void setWristbandBloodPressureId(String wristbandBloodPressureId) {
        this.wristbandBloodPressureId = wristbandBloodPressureId;
    }

    public String getStrDate() {
        return strDate == null ? DateTimeUtils.getFormatDate(getTimestamp(),"yyyy-MM-dd HH:mm:ss") : strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    @Override
    public String toString() {
        return "BPInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", spValue=" + spValue +
                ", dpValue=" + dpValue +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", wristbandBloodPressureId='" + wristbandBloodPressureId + '\'' +
                ", strDate='" + strDate + '\'' +
                '}';
    }
}
