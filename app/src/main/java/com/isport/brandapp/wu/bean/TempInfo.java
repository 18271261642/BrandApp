package com.isport.brandapp.wu.bean;

public class TempInfo {
    /**
     * {
     * "centigrade": "string",
     * "createTime": "2020-04-08T05:49:03.586Z",
     * "dateStr": "string",
     * "deviceId": "string",
     * "extend1": "string",
     * "extend2": "string",
     * "extend3": "string",
     * "fahrenheit": "string",
     * "timestamp": "2020-04-08T05:49:03.586Z",
     * "userId": 0,
     * "wristbandTemperatureId": 0
     * }
     */
    private String deviceId;
    private String centigrade;
    private String fahrenheit;
    private Long timestamp;
    private String userId;
    private String wristbandTemperatureId;
    private String strDate;
    private String tempUnitl;
    private Integer color;
    private String state;
    private Integer resId;

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

    public String getCentigrade() {
        return centigrade;
    }

    public void setCentigrade(String centigrade) {
        this.centigrade = centigrade;
    }

    public String getFahrenheit() {
        return fahrenheit;
    }

    public void setFahrenheit(String fahrenheit) {
        this.fahrenheit = fahrenheit;
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

    public String getWristbandTemperatureId() {
        return wristbandTemperatureId;
    }

    public void setWristbandTemperatureId(String wristbandTemperatureId) {
        this.wristbandTemperatureId = wristbandTemperatureId;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getTempUnitl() {
        return tempUnitl;
    }

    public void setTempUnitl(String tempUnitl) {
        this.tempUnitl = tempUnitl;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return "TempInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", centigrade='" + centigrade + '\'' +
                ", fahrenheit='" + fahrenheit + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", wristbandTemperatureId='" + wristbandTemperatureId + '\'' +
                ", strDate='" + strDate + '\'' +
                ", tempUnitl='" + tempUnitl + '\'' +
                ", color=" + color +
                ", state='" + state + '\'' +
                ", resId=" + resId +
                ", isClick=" + isClick +
                '}';
    }
}
