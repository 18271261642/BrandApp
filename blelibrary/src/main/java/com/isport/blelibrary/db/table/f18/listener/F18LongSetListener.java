package com.isport.blelibrary.db.table.f18.listener;

/**
 * Created by Admin
 * Date 2022/2/15
 */
public interface F18LongSetListener extends CommAlertListener{

    void backCommF18AlertData(boolean isOpen, boolean isDnt,int startHour, int startMinute, int endHour, int endMinute, int interval);
}
