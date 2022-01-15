package com.isport.blelibrary.deviceEntry.interfaces;

/**
 * @Author
 * @Date 2018/11/13
 * @Fuction
 */

public interface IDeviceType {

    void setType();

    int TYPE_NULL = -1;
    int TYPE_WATCH = 0;
    int TYPE_SCALE = 1;
    int TYPE_SLEEP = 2;
    int TYPE_WATCH_W516 = 0;
    int TYPE_WATCH_W526 = 526;
    int TYPE_WATCH_W557 = 557;
    int TYPE_WATCH_W560B = 560;
    int TYPE_WATCH_W560 = 5601;
    int TYPE_WATCH_W812B = 81266;
    int TYPE_ROPE_S002 = 83002;
    int TYPE_BRAND_W311 = 3;
    int TYPE_BRAND_W307J = 30774;
    int TYPE_BRAND_W520 = 4;
    int TYPE_BRAND_W813 = 813;
    int TYPE_BRAND_W819 = 819;
    int TYPE_BRAND_W910 = 910;
    int TYPE_BRAND_W812 = 812;
    int TYPE_BRAND_W817 = 817;
    int TYPE_BRAND_W814 = 814;
    int TYPE_DFU = 1001;
    int TYPE_ALL = 1002;

    int TYPE_WATCH_7018 = 7018;


    void syncTodayData();

    void queryWatchFace();
}
