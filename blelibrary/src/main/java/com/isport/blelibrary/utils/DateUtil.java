package com.isport.blelibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DateUtil {


    private static SimpleDateFormat dateFormatyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dateFormatyyMM = new SimpleDateFormat("yyyy-MM");
    private static SimpleDateFormat dateFormatHHmm = new SimpleDateFormat("HH:mm");


    public static String getdateFormatyyMMdd(long times) {

        return dateFormatyyMMdd.format(new Date(times));
    }

    public static String getMMdd(long times) {

        return dateFormatyyMM.format(new Date(times));
    }


    public static String getFormatTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH", Locale.CHINA);
        return sdf.format(new Date(time));
    }

    public static String getFormatTime(long time,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(new Date(time));
    }

    public static String getCurrDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        return sdf.format(new Date());
    }

    //获取昨天的日期yyyy-MM-dd格式
    public static String getYestDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        long currDayLong = new Date().getTime();
        //一天的long
        long yesLong = currDayLong - 86400000L;
        return sdf.format(new Date(yesLong));
    }



    //yyyy-MM-dd HH:mm格式，获取HH
    public static String getDateHourStr(String timeStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH",Locale.CHINA);
        try {
            long timeLong = sdf.parse(timeStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeLong);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            return String.format("%02d",hour);
        } catch (ParseException e) {
            e.printStackTrace();
            return "0";
        }
    }


    public static String strFormatHHmm(String date) {

        //       2020-09-17 10:00:00

        return date.substring(11, date.length() - 3);

        /*String HHmm = "";
        try {
            dateFormatHHmm.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return HHmm;
        }
*/
    }

    public static String getTodayStr(String format) {
        Calendar calendar = Calendar.getInstance();
        return dataToString(calendar.getTime(), format);
    }

    public static String dataToString(Date date, String format) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat(format);
            return format1.format(date);
        } catch (Exception e) {
            return System.currentTimeMillis() + "";
        }

    }

    public static Date stringToDate(String date, String format) {
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        try {
            return format1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date longToDate(long time) {
        return new Date(time);
    }

    public static String toString(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    public static boolean is24Hour(Context context) {
        String timeFormat = android.provider.Settings.System.getString(context.getContentResolver(),
                android.provider.Settings.System.TIME_12_24);

        return DateFormat.is24HourFormat(context);
    }

    public static int getTimeZone() {
        TimeZone timezone = TimeZone.getDefault();
        int rawOffSet = timezone.getRawOffset();
        int offset = rawOffSet / 3600000;
        return offset;
    }

    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getCurrentCalendar() {
        return Calendar.getInstance();
    }

    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 得到一个格式化的时间
     *
     * @param time 时间 分钟
     * @return 时：分
     */
    public static String getFormatTimemmss(long time) {
        long minute = time % 60;
        long hour = time / 60;
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);
        String strMinute = ("00" + minute).substring(("00" + minute).length() - 2);
        return strHour + ":" + strMinute;
    }

    public static String getFormatTimehhmmss(long time) {
        long minute = time % 60;
        long hour = time / 60;
        String strHour = String.format("%02d", hour);
        String strMinute = String.format("%02d", minute);
        return strHour + ":" + strMinute + ":00";
    }

    //格式化 时，分，秒,W560锻炼数据为每15秒一个点
    public static String getFormatTimehhmmss(long time,int invalid) {
        Logger.myLog("TT","--11-time="+time);
        time = time * 15;
        Logger.myLog("TT","-22--time="+time);
        long hour = time / 3600;
        long minute = time / 60;
        long seconde = time % 60;

        String strHour = String.format("%02d", hour);
        String strMinute = String.format("%02d", minute);
        String strSecond = String.format("%02d",seconde);
        return strHour + ":" + strMinute +":"+ strSecond;
    }


    public static String getRopeFormatTimehhmmss(long time) {

        long sec = time % 60;
        long min = time / 60;
        long hour = min / 60;
        min = min % 60;
        String strHour = String.format("%02d", hour);
        String strMinute = String.format("%02d", min);
        String StrSecd = String.format("%02d", sec);
        return strHour + ":" + strMinute +":"+StrSecd;
    }


    public static int getMonthOfDay(int year, int month) {
        int day = 0;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;

        }

        return 0;
    }

    public static int getDifDay(Calendar calendar) {
        Calendar calendar1 = Calendar.getInstance();
        int diffDays = (int) ((calendar1.getTimeInMillis() - calendar.getTimeInMillis())
                / (1000 * 60 * 60 * 24));
        if (diffDays == 0) {
            diffDays = 1;
        }
        return diffDays;

    }


    public static int getAQI(int value) {
        //空气质量（AQI）00：无 01:优 02：良 03：轻度 04：中度 05：重 度 06：严重
        //0-50优
        //51-100 良好
        //101-150 轻度
        //151-200 中度
        //201-300 重 度
        //>300 严重
        if (value < 0) {
            return 0;
        } else if (value >= 0 && value <= 50) {
            return 1;
        } else if (value >= 51 && value <= 100) {
            return 2;
        } else if (value >= 101 && value <= 150) {
            return 3;
        } else if (value >= 151 && value <= 200) {
            return 4;
        } else if (value >= 201 && value <= 300) {
            return 5;
        } else {
            return 6;
        }

    }

}