package com.isport.blelibrary.entry;

public class WristbandForecast {
    private String weatherId;//天气状态
    private String lowTemperature;//低温
    private String highTemperature;//高温
    private String aqiValue;//空气质量指数值
    private String updatetime;//更新时间

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getLowTemperature() {
        return lowTemperature;
    }

    public void setLowTemperature(String lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    public String getHighTemperature() {
        return highTemperature;
    }

    public void setHighTemperature(String highTemperature) {
        this.highTemperature = highTemperature;
    }

    public String getAqiValue() {
        return aqiValue;
    }

    public void setAqiValue(String aqiValue) {
        this.aqiValue = aqiValue;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }


    @Override
    public String toString() {
        return "WristbandForecast{" +
                "weatherId='" + weatherId + '\'' +
                ", lowTemperature='" + lowTemperature + '\'' +
                ", highTemperature='" + highTemperature + '\'' +
                ", aqiValue='" + aqiValue + '\'' +
                ", updatetime='" + updatetime + '\'' +
                '}';
    }
}
