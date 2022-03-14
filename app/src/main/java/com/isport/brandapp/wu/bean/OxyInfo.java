package com.isport.brandapp.wu.bean;

public class OxyInfo {
    /**
     * "boValue": "string",
     * "createTime": "2019-11-16T06:44:56.259Z",
     * "deviceId": "string",
     * "extend1": "string",
     * "extend2": "string",
     * "extend3": "string",
     * "timestamp": "2019-11-16T06:44:56.259Z",
     * "userId": 0,
     * "wristbandBloodOxygenId": 0
     */
    private int boValue;
    private String deviceId;
    private Long timestamp;
    private String userId;
    private String wristbandBloodOxygenId;
    private String strDate;

    //是否点击
    private boolean isClick;

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getWristbandBloodOxygenId() {
        return wristbandBloodOxygenId;
    }

    public void setWristbandBloodOxygenId(String wristbandBloodOxygenId) {
        this.wristbandBloodOxygenId = wristbandBloodOxygenId;
    }

    public int getBoValue() {

        return boValue;
    }

    public void setBoValue(int boValue) {
        this.boValue = boValue;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    @Override
    public String toString() {
        return "OxyInfo{" +
                "boValue=" + boValue +
                ", deviceId='" + deviceId + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", wristbandBloodOxygenId='" + wristbandBloodOxygenId + '\'' +
                ", strDate='" + strDate + '\'' +
                ", isClick=" + isClick +
                '}';
    }
}
