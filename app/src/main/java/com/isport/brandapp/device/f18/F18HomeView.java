package com.isport.brandapp.device.f18;

import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;
import java.util.HashMap;
import brandapp.isport.com.basicres.mvp.BaseView;

/**
 * Created by Admin
 * Date 2022/1/20
 */
public interface F18HomeView extends BaseView {

    //保存本地的账号相关的连接设备
    void successGetDeviceListFormDB(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean reConnect);
    //网络获取设备
    void successGetDeviceListFormHttp(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean reConnect);
    //获取最新的汇总步数数据
    void successGetMainLastStepDataForDB(WatchSportMainData watchSportMainData);
    //最新的单次血氧数据
    void successGetMainLastOxgenData(OxyInfo info);
    //最新的单次心率
    void successGetMainLastOnceHrData(OnceHrInfo info);
    //最新的单次血压数据
    void successGetMainLastBloodPresuure(BPInfo info);
    //最新的单次温度数据
    void successGetMainLastTempValue(TempInfo info);
    //睡眠
    void successGetMainLastSleepValue(WatchSleepDayData watchSleepDayData);
    //锻炼数据
    void successGetMainTotalAllTime(Integer time);
}
