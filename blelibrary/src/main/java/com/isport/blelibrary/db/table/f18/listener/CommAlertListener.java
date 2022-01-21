package com.isport.blelibrary.db.table.f18.listener;

/**
 * Created by Admin
 * Date 2022/1/18
 */
public interface CommAlertListener {

    void backCommAlertData(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute,int interval);
}
