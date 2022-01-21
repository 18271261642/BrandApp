package com.isport.blelibrary.entry;

import com.htsmart.wristband2.bean.WristbandAlarm;

import java.util.List;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public interface F18AlarmAllListener {

    void backAllDeviceAlarm(List<WristbandAlarm> alarmList);
}
