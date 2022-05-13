package com.isport.brandapp.bean;


/**
 * 版本更新
 * Created by Admin
 * Date 2022/5/5
 */

public class AppVersionInfoBean{


    /**
     * type : Android
     * appVersionCode : 72
     * appVersionName : 222
     * url : https://isportcloud.oss-cn-shenzhen.aliyuncs.com/manager/ISPORT_http_2020-04-22_16点06分 _V1.3.11test.api.mini-banana.com_release.apk
     * updateAble : 1
     * remark : ddd
     * remarkEn : dfs
     * fileSize : 1000
     */

    private String type;
    private long appVersionCode;
    private String appVersionName;
    private String url;
    private int updateAble;
    private String remark;
    private String remarkEn;
    private int fileSize;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(long appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUpdateAble() {
        return updateAble;
    }

    public void setUpdateAble(int updateAble) {
        this.updateAble = updateAble;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkEn() {
        return remarkEn;
    }

    public void setRemarkEn(String remarkEn) {
        this.remarkEn = remarkEn;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "AppVersionInfoBean{" +
                "type='" + type + '\'' +
                ", appVersionCode=" + appVersionCode +
                ", appVersionName='" + appVersionName + '\'' +
                ", url='" + url + '\'' +
                ", updateAble=" + updateAble +
                ", remark='" + remark + '\'' +
                ", remarkEn='" + remarkEn + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
