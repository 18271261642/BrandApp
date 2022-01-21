package com.isport.blelibrary.entry;

/**
 * Created by Admin
 * Date 2022/1/18
 */
public class F18AppsItemBean {

    //对应序号
    private int flagCode;

    //APP名称
    private String appName;

    //是否开启
    private boolean isChecked;

    //图标，放本地
    private int appUrl;



    public F18AppsItemBean() {
    }

    public F18AppsItemBean(int flagCode, String appName, boolean isChecked, int appUrl) {
        this.flagCode = flagCode;
        this.appName = appName;
        this.isChecked = isChecked;
        this.appUrl = appUrl;
    }

    public int getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(int appUrl) {
        this.appUrl = appUrl;
    }

    public int getFlagCode() {
        return flagCode;
    }

    public void setFlagCode(int flagCode) {
        this.flagCode = flagCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "F18AppsItemBean{" +
                "flagCode=" + flagCode +
                ", appName='" + appName + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
