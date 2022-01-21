package com.isport.blelibrary.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class WristbandData implements Parcelable {
    private String lunar;//农历节气节日
    private String festival;//公历
    private String pm25;//PM2.5
    private String aqiValue;//空气质量指数值
    private String temp;//实时温度
    private String weatherId;//天气状态
    private String updatetime;//更新时间
    private String city;

    public String getLunar() {
        return lunar;
    }

    public void setLunar(String lunar) {
        this.lunar = lunar;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lunar);
        dest.writeString(this.festival);
        dest.writeString(this.pm25);
        dest.writeString(this.aqiValue);
        dest.writeString(this.temp);
        dest.writeString(this.weatherId);
        dest.writeString(this.updatetime);
        dest.writeString(this.city);
    }

    public WristbandData() {
    }

    protected WristbandData(Parcel in) {
        this.lunar = in.readString();
        this.festival = in.readString();
        this.pm25 = in.readString();
        this.aqiValue = in.readString();
        this.temp = in.readString();
        this.weatherId = in.readString();
        this.updatetime = in.readString();
        this.city = in.readString();
    }

    public static final Parcelable.Creator<WristbandData> CREATOR = new Parcelable.Creator<WristbandData>() {
        @Override
        public WristbandData createFromParcel(Parcel source) {
            return new WristbandData(source);
        }

        @Override
        public WristbandData[] newArray(int size) {
            return new WristbandData[size];
        }
    };

    @Override
    public String toString() {
        return "WristbandData{" +
                "lunar='" + lunar + '\'' +
                ", festival='" + festival + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", aqiValue='" + aqiValue + '\'' +
                ", temp='" + temp + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
