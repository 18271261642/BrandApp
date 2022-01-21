package com.isport.blelibrary.entry;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class F18AlarmBean {

    private int alarmId;

    private int hour;

    private int minute;

    private int repeat;

    private boolean isOpen;

    public F18AlarmBean() {
    }

    public F18AlarmBean(int alarmId, int hour, int minute, int repeat, boolean isOpen) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.isOpen = isOpen;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "F18AlarmBean{" +
                "alarmId=" + alarmId +
                ", hour=" + hour +
                ", minute=" + minute +
                ", repeat=" + repeat +
                ", isOpen=" + isOpen +
                '}';
    }
}
