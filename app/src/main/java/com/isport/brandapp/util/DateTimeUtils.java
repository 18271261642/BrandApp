package com.isport.brandapp.util;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Desc 日期操作工具
 * Created by Admin
 * Date 2021/8/25
 */
public class DateTimeUtils {


    public static int formatLongDateToInt(Long timeStr){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
            String timeFormat = sdf.format(new Date(timeStr));

            int hour = Integer.parseInt(StringUtils.substringBefore(timeFormat,":"));
            int minute = Integer.parseInt(StringUtils.substringAfter(timeFormat,":"));

            return hour * 60 + minute;

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }



    public static String getCurrentDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        return simpleDateFormat.format(new Date());

    }


    public static String getCurrentDate(String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format,Locale.CHINA);
        return simpleDateFormat.format(new Date());

    }


    //判断当前时间是否在区间内
    public static boolean isComparisonWith(String start,String end){
        long startTime = getFormatTime(start);
        long endTime = getFormatTime(end);
        //结束时间如果小于开始时间，表明是第二天的时间
        if(endTime<startTime)
            endTime = endTime + 1440L;
        return startTime<getCurrentLongDate() && endTime > getCurrentLongDate();
    }


    //当前时间分钟
    public static long getCurrentLongDate(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return hour * 60L + minute;
    }


    //HH:mm格式时间转换成long型
    public static long getFormatTime(String timeStr){
        try {
            int hour = Integer.parseInt(StringUtils.substringBefore(timeStr,":"));
            int minute = Integer.parseInt(StringUtils.substringAfter(timeStr,":"));
            return hour * 60L + minute;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }


    public static String getHouAdMinute(int hour,int minute){
        String hourStr = hour<=9?"0"+hour:hour+"";
        String minuteStr = minute<=9?"0"+minute:minute+"";
        return hourStr+":"+minuteStr;
    }
}
