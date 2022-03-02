package com.isport.brandapp.device.sleep;

import android.content.Context;
import android.text.format.DateFormat;

import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.brandapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.UIUtils;

public class TimeUtil {

    /**
     * 描述：根据时区的值，获取String 时区值
     *
     * @param tzone
     * @return
     */
    public static String getTimeZone(float tzone) {
        String timezone = "";
        int zone = (int) tzone;
        int mode = (int) ((tzone * 100) % 100);
        if (mode == 0) {// 整数
            if (tzone >= 0) {
                timezone = "GMT+" + zone;
            } else if (tzone == -100) {
                timezone = "";
            } else {
                timezone = "GMT" + zone;
            }
            // LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" + timezone);
        } else {

            int minute = (int) (60 * (mode / 100f));

            if (tzone >= 0) {
                timezone = "GMT+" + StringUtil.DF_2.format(zone) + ":" + StringUtil.DF_2.format(minute);
            } else if (tzone == -100) {
                timezone = "";
            } else {
                timezone = "GMT" + StringUtil.DF_2.format(zone) + ":" + StringUtil.DF_2.format(minute);
            }

            // LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" + timezone);
        }
        return timezone;
    }

    /**
     * <h3>获取当前时间的 时间戳</h3>
     * <ul>
     * <li>单位为S</li>
     * </ul>
     *
     * @return
     */
    public static int getCurrentTimeInt() {
        Calendar c = getCalendar(-100);
        return (int) (c.getTimeInMillis() / 1000);
    }

    /**
     * <p>
     * 早上6点前睡的 算是昨天的
     * </p>
     */
    public static final byte MarkDay = SleepConfig.SLEEP_DIFF;

    public static int getDst(float timeZone) {
        // return (int)(timeZone%(1*3600*1000));
        return (int) (timeZone * 1000);
    }

    /**
     * <h3>获取当前睡眠时间的 时间详情</h3>
     * <ul>
     * <li>包括 年，月，周，日</li>
     * </ul>
     *
     * @param startTime
     * @param timeZone
     * @return
     */
    public static short[] getSleepTimeInfo(int startTime, float timeZone) {
        Calendar c = TimeUtil.getCalendar((int) timeZone);
        c.clear();
        c.setTimeInMillis(startTime * 1000L + getDst(timeZone));
        // 由于早上6点前睡觉 都算 前一天的
        c.add(Calendar.HOUR_OF_DAY, -SleepConfig.SLEEP_DIFF);

        short year = (short) c.get(Calendar.YEAR);
        byte month = (byte) (c.get(Calendar.MONTH) + 1);
        byte day = (byte) c.get(Calendar.DAY_OF_MONTH);

        // 计算当前所在的周
        // 由于Calendar的周是从周日开始的，所以 -1
        c.add(Calendar.DAY_OF_YEAR, -1);
        byte week = (byte) c.get(Calendar.WEEK_OF_YEAR);
        short[] infos = new short[5];
        if (month == 12 && week == 1) {// calendar把12月份的最后几天 算为 第二年的第一周，
            // 我们要求分开，第一周是从1月1日开始，所以做了以下处理
            infos[4] = (short) (year + 1);
        } else {
            infos[4] = year;
        }

        infos[0] = year;
        infos[1] = month;
        infos[2] = week;
        infos[3] = day;

        return infos;
    }

    /**
     * 描述：获取手机TimeZone时区，单位 小时 包含了 夏时令 和 冬时令的 偏移
     *
     * @return
     */
    public static float GetTimeZone() {
        TimeZone tz = TimeZone.getDefault();

        // String s = "TimeZone:"+tz.getDisplayName(false, TimeZone.SHORT)+",id:"
        // +tz.getID();

        Calendar calendar = Calendar.getInstance();
        float f = (tz.getRawOffset() + calendar.get(Calendar.DST_OFFSET)) / 1000f / 60f / 60f;

        // LogUtil.showMsg(TAG+" GetTimeZone s:"+s+",f:" +f+",str:" +
        // StringUtil.DF_P_2.format(f));

        return (int) (f * 100) / 100f;
    }

    /**
     * <h3>获取day的详情</h3>
     * <ul>
     * <li>如1431594924 时区8，得到 2015-5-14 夜</li>
     * </ul>
     *
     * @param startTime
     * @param timezone
     * @param
     * @return
     */
    public static String getDayInfoBySecond(int startTime, float timezone) {
        Calendar c = getCalendar((int) timezone);
        c.clear();
        c.setTimeInMillis(startTime * 1000L + getDst(timezone));
        StringBuffer sb = new StringBuffer();

        sb.append(String.valueOf(c.get(Calendar.YEAR)));
        sb.append("/");
        sb.append(String.valueOf(c.get(Calendar.MONTH) + 1));
        sb.append("/");
        sb.append(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

        Calendar today = getCalendar((int) timezone);
        String str = sb.toString();
        if (today.get(Calendar.YEAR) == c.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)) {
            //str = SmartPillowApp.getInstance().getString(R.string.today);
        } else {
            c.add(Calendar.DAY_OF_MONTH, 1);
            if (today.get(Calendar.YEAR) == c.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)) {
                //str = SmartPillowApp.getInstance().getString(R.string.today);
            }
        }

        return str;

    }

    /**
     * 描述：根据时间戳，04:30 am;
     *
     * @param mill
     * @return
     */
    public static String[] GetTimeStrByMill(long mill, float timezone, int dstOff) {

        // LogUtil.showMsg(TAG+" GetTimeStrByMill mill:" + mill+",zone:" +
        // timezone+",dstOff:" + dstOff);

        Calendar c = getCalendar((int) timezone);

        // LogUtil.showMsg(TAG+" GetTimeStrByMill dst:" + getDst(timezone)+",mill:" +
        // (mill + dstOff + getDst(timezone)));
        // 修改，解决getDst(timezone) + 秒的余数大于等于六十秒的时候多了一分钟，不清楚原来加getDst(timezone)的目的，暂时屏蔽
        // c.setTimeInMillis(mill + dstOff + getDst(timezone));
        c.setTimeInMillis(mill + dstOff);
        // 修改结束
        // c.set(Calendar.DST_OFFSET, dstOff);
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        StringBuffer sb = new StringBuffer();
        String[] strs = new String[2];

        if (HourIs24()) {
            strs[1] = "";
        } else {
            if (hours < 12 || hours == 24)
                strs[1] = "AM";
            else
                strs[1] = "PM";

            if (hours == 12) {
                hours = 12;
            } else {
                hours = hours % 12;
            }
        }

        if (hours < 10)
            sb.append("0");
        sb.append(hours);
        sb.append(":");

        if (minute < 10)
            sb.append("0");
        sb.append(minute);

        strs[0] = sb.toString();

        return strs;

    }

    /**
     * 描述：秒 转化为时间，如 63秒：01:01s
     *
     * @return
     */
    public static String Second2Time(int second) {
        StringBuffer sb = new StringBuffer("");
        // 先转化为小时
        if (second >= 60 * 60) {
            int h = second / 3600;
            if (h < 10)
                sb.append("0");
            sb.append(h);
            sb.append(":");
        }
        if (second >= 60) {
            int m = second / 60 % 60;
            if (m < 10)
                sb.append("0");
            sb.append(m);
            sb.append(":");
        } else {
            sb.append("00:");
        }
        int s = second % 60;
        if (s < 10)
            sb.append("0");
        sb.append(s);
        return sb.toString();
    }


    public static String getTimerFormatedStrings(long time, long currentTime) {
        String days = "00";
        String hours = "00";
        String minutes = "00";
        String seconds = "00";
        String millisSeconds = "000";
        long leftTime = currentTime - time;
        if (currentTime == 0) {
            leftTime = time;
        }
        if (leftTime > 0) {
            //毫秒
            long millisValue = leftTime % 1000;
            if (millisValue > 100) {
                millisSeconds = String.valueOf(
                        millisValue);
            } else if (millisValue >= 10 && millisValue < 100) {
                millisSeconds = String.valueOf("0" + millisValue);
            } else {
                millisSeconds = String.valueOf("00" + millisValue);
            }

            //实际多少秒
            long trueSeconds = leftTime / 1000;
            //当前的秒
            long secondValue = trueSeconds % 60;
            if (secondValue < 10) {
                seconds = String.valueOf("0" + secondValue);
            } else {
                seconds = String.valueOf(secondValue);
            }

            //当前的分
            long trueMinutes = trueSeconds / 60;
            long minuteValue = trueMinutes % 60;
            if (minuteValue < 10) {
                minutes = String.valueOf("0" + minuteValue);
            } else {
                minutes = String.valueOf(minuteValue);
            }


            //当前的小时数
            long trueHours = trueMinutes / 60;
            long hourValue = trueHours % 24;
            if (hourValue < 10) {
                hours = String.valueOf("0" + hourValue);
            } else {
                hours = String.valueOf(hourValue);
            }

            //当前的天数
            long dayValue = trueHours / 24;
            if (dayValue < 10) {
                days = String.valueOf("0" + dayValue);
            } else {
                days = String.valueOf(dayValue);
            }
        }

        return hours + ":" + minutes + ":" + seconds;
        //return new String[]{days, hours, minutes, seconds, millisSeconds};
    }
    public static String getTimerHourMin(long time, long currentTime) {
        String days = "00";
        String hours = "00";
        String minutes = "00";
        String seconds = "00";
        String millisSeconds = "000";
        long leftTime = currentTime - time;
        if (currentTime == 0) {
            leftTime = time;
        }
        if (leftTime > 0) {
            //毫秒
            long millisValue = leftTime % 1000;
            if (millisValue > 100) {
                millisSeconds = String.valueOf(
                        millisValue);
            } else if (millisValue >= 10 && millisValue < 100) {
                millisSeconds = String.valueOf("0" + millisValue);
            } else {
                millisSeconds = String.valueOf("00" + millisValue);
            }

            //实际多少秒
            long trueSeconds = leftTime / 1000;
            //当前的秒
            long secondValue = trueSeconds % 60;
            if (secondValue < 10) {
                seconds = String.valueOf("0" + secondValue);
            } else {
                seconds = String.valueOf(secondValue);
            }

            //当前的分
            long trueMinutes = trueSeconds / 60;
            long minuteValue = trueMinutes % 60;
            if (minuteValue < 10) {
                minutes = String.valueOf("0" + minuteValue);
            } else {
                minutes = String.valueOf(minuteValue);
            }


            //当前的小时数
            long trueHours = trueMinutes / 60;
            long hourValue = trueHours % 24;
            if (hourValue < 10) {
                hours = String.valueOf("0" + hourValue);
            } else {
                hours = String.valueOf(hourValue);
            }

            //当前的天数
            long dayValue = trueHours / 24;
            if (dayValue < 10) {
                days = String.valueOf("0" + dayValue);
            } else {
                days = String.valueOf(dayValue);
            }
        }


        if(hours.equals("00")&& minutes.equals("00")){
            return  seconds+UIUtils.getString(R.string.sport_second);


        }else if(!minutes.equals("00")&& hours.equals("00")){
            return minutes + UIUtils.getString(R.string.sport_min) + seconds+UIUtils.getString(R.string.sport_second);

        }else{
            return hours + UIUtils.getString(R.string.sport_hour) + minutes + UIUtils.getString(R.string.sport_min) + seconds+UIUtils.getString(R.string.sport_second);

        }

        //return new String[]{days, hours, minutes, seconds, millisSeconds};
    }

    public static String getTimerFormatedStringsPace(long leftTime) {
        String minutes = "00";
        String seconds = "00";


        if (leftTime > 0) {

            minutes = CommonDateUtil.formatTwoStr((int) (leftTime / 60));
            seconds = CommonDateUtil.formatTwoStr((int) leftTime % 60);

            //毫秒
        }

        return minutes + "’" + seconds + "”";
        //return new String[]{days, hours, minutes, seconds, millisSeconds};
    }

    /**
     * 描述：获取现在的week时间
     *
     * @return year*100+week
     */
    public static int CurrentWeek(int timezone) {
        Calendar c = getCalendar(timezone);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        int year = c.get(Calendar.YEAR);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        if (week == 1 && c.get(Calendar.MONTH) == 11) {
            year += 1;
        }
        return year * 100 + week;
    }

    /**
     * 描述：获取一年中的有多少周
     *
     * @return
     */
    public static int GetWeeksInYear(int year) {
        Calendar c = getCalendar(-100);
        c.clear();
        c.set(year, 11, 31);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        if (week <= 1) {
            c.add(Calendar.DAY_OF_MONTH, -7);
            week = c.get(Calendar.WEEK_OF_YEAR);
        }
        return week;
    }

    public static int LastWeek(int timezone) {
        Calendar c = getCalendar(timezone);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        int year = c.get(Calendar.YEAR);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        return year * 100 + week;
    }

    /**
     * 描述：获取当去的month时间
     *
     * @return year*100+month;
     */
    public static int CurrentMonth(float timezone) {
        Calendar c = getCalendar((int) timezone);
        // c.clear();
        // c.set(Calendar.DST_OFFSET, getDst(timezone));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONDAY) + 1;
        return year * 100 + month;
    }

    public static int LastMonth(int timezone) {
        Calendar c = getCalendar(timezone);
        c.add(Calendar.MONTH, -1);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONDAY) + 1;
        return year * 100 + month;
    }

    /**
     * 把一个int的时间戳 转化为 HH：MM
     */
    public static String int2TimeHM(int a, float timezone, int dst_off) {
        Calendar c = getCalendar((int) timezone);
        c.clear();
        // c.set(Calendar.DST_OFFSET, dst_off);
        // c.setTimeInMillis(second2Millis(a) + dst_off + getDst(timezone));
        c.setTimeInMillis(second2Millis(a) + dst_off);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return formatMinute(hour, minute);
    }

    public static String formatMinute(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }



    //获取上一天的日期
    public static String getYesterday(String currDay){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            long dateStr = Objects.requireNonNull(sdf.parse(currDay)).getTime();
            long yesStr = dateStr - 86400L*1000;
            return sdf.format(new Date(yesStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    //将分钟转换成HH：mm格式
    public static String formatMinuteToStr(int time){
        if(time == 0)
            return "00:00";
        int hour = time / 60;
        int minute = time % 60;
        return String.format("%02d",hour)+":"+String.format("%02d",minute);
    }


    public static String longformatMinute(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return String.format("%02d:%02d", hour, minute);
    }

    public static String formatMinute(int minute) {
        return String.format("%02d", minute);
    }

    /**
     * 描述：把时间戳 获取 时间中Hour,minute
     *
     * @param a
     * @return
     */
    public static int[] int2HMInt(int a, float timezone, int dst_off) {
        Calendar c = getCalendar((int) timezone);
        c.setTimeInMillis(second2Millis(a) + dst_off + getDst(timezone));
        // c.set(Calendar.DST_OFFSET, dst_off);
        int[] time = {c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};
        return time;
    }

    /**
     * 描述：把一个时间 转化为 long型的 微秒
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static long str2MS(String time) throws ParseException {
        Date date = StringUtil.DATE_FORMAT.parse(time);
        return date.getTime() / 1000;
    }

    /**
     * 根据时区获取 对应的 calendar
     *
     * @param tzone
     * @return
     */
    /*
     * public static Calendar getCalendar ( int tzone ) { String timezone =
     * setTimeZone( tzone ); if ( "".equals(timezone )) { return
     * Calendar.getInstance(); }else { return
     * Calendar.getInstance(TimeZone.getTimeZone(timezone)); } }
     */

    /**
     * 根据时区获取 对应的 calendar
     *
     * @param tzone
     * @return
     */
    public static Calendar getCalendar(float tzone) {
        Calendar c = Calendar.getInstance();
        TimeZone tz = null;
        if (tzone == -100) {
            tzone = GetTimeZone();
            tz = TimeZone.getTimeZone(getTimeZone(tzone));
        } else {
            tz = TimeZone.getTimeZone(getTimeZone(tzone));
        }

        c.setTimeZone(tz);
        c.setMinimalDaysInFirstWeek(1);
        c.setFirstDayOfWeek(Calendar.SUNDAY);
//        c.setFirstDayOfWeek(Calendar.MONDAY);

        return c;
    }

    /**
     * 描述：计算得出 当前的时间是 周几 周日是 7
     *
     * @return
     */
    public static int getCurrentWeek(long time, float timezone, int dst_off) {
        Calendar c = getCalendar((int) timezone);
        c.clear();
        c.setTimeInMillis(time + dst_off + getDst(timezone));
        // c.set(Calendar.DST_OFFSET, dst_off);
        // 由于 睡眠凌晨4点前 睡的都 算是 前天的
        c.add(Calendar.HOUR_OF_DAY, -MarkDay);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        // int a = Calendar.SUNDAY;
        if (dayOfWeek == 1)
            return 7;
        else {
            return dayOfWeek - 1;
        }
    }

    /**
     * 描述：获取指定时间的小时数 如 7:30 return 为 7.5 如果 给的week 是周一 ，得到的是周二那 要加 24，
     *
     * @param time 给定的时间戳 单位 微妙
     * @param week 周几
     * @return
     */
    public static float getHoursByTime(long time, int week, float timezone, int dst_off) {
        Calendar c = getCalendar((int) timezone);
        c.clear();
        c.setTimeInMillis(time + dst_off + getDst(timezone));
        // c.set(Calendar.DST_OFFSET, dst_off);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1)
            dayOfWeek = 7;
        else {
            dayOfWeek = dayOfWeek - 1;
        }
        if (dayOfWeek != week)
            hour += 24;
        float a = ((float) minute) / 60 + hour;
        return a;
    }

    /**
     * 描述：根据当前的时间 获取 Week的起始时间 和 结束时间
     *
     * @param currentTime 是以秒为单位的
     */
    public static long[] getWeekTimeBytime(long currentTime, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.setTimeInMillis(currentTime * 1000 + dst_off);
        // c.set(Calendar.DST_OFFSET, dst_off);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int dayFromMonday = 0;
        if (dayOfWeek == 1) // 周日
            dayFromMonday = -6;
        else
            dayFromMonday = 2 - dayOfWeek;

        c.add(Calendar.DAY_OF_WEEK, dayFromMonday);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), MarkDay, 0);
        long begin = c.getTimeInMillis();

        c.add(Calendar.DAY_OF_WEEK, 7);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), MarkDay, 0);
        long end = c.getTimeInMillis();

        long[] point = new long[2];
        point[0] = begin;
        point[1] = end;

        return point;

    }

    /**
     * 描述：根据传入的Time的时间，来得到传入的时间是月的第几天
     *
     * @param time 以微妙微单位
     * @return
     */
    public static int getMontyDayByTime(long time, float timezone, int dst_off) {
        Calendar c = getCalendar((int) timezone);
        c.clear();
        c.setTimeInMillis(time + dst_off + getDst(timezone));
        // c.set(Calendar.DST_OFFSET, dst_off);
        // 4点前睡觉的算昨天的
        c.add(Calendar.HOUR_OF_DAY, -MarkDay);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static String getMonthName(Context context, int index) {
        String monthStrs[] = context.getResources().getStringArray(R.array.arr_month);
        if (index != -1) {
            return monthStrs[index];
        } else {
            return "";
        }
    }

    public static String Num2ENum(int num) {

        String m = String.valueOf(num);

        switch (num) {
            case 1:
                return m + "st";
            case 2:
                return m + "nd";
            case 3:

                return m + "rd";
            default:
                return m + "th";
        }

    }

    public static String Num2EnglishNum(int num) {
        String s = "";
        switch (num) {
            case 1:
                s = "First";
                break;
            case 2:
                s = "Second";
                break;
            case 3:
                s = "Third";
                break;
            case 4:
                s = "Fourth";
                break;
            case 5:
                s = "Fifth";
                break;
            case 6:
                s = "Sixth";
                break;
            case 7:
                s = "Seventh";
                break;
            case 8:
                s = "Eight";
                break;
            case 9:
                s = "Ninth";
                break;
            case 10:
                s = "Tenth";
                break;
            case 11:
                s = "Eleventh";
                break;
            case 12:
                s = "Twelfth";
                break;
            case 13:
                s = "Thirteenth";
                break;
            case 14:
                s = "Fourteenth";
                break;
            case 15:
                s = "Fifteenth";
                break;
            case 16:
                s = "Sixteenth";
                break;
            case 17:
                s = "Seventeenth";
                break;
            case 18:
                s = "Eighteenth";
                break;
            case 19:
                s = "Nineteenth";
                break;
            case 20:
                s = "Twentieth";
                break;
            case 21:
                s = "Twenty-first";
                break;
            case 22:
                s = "Twenty-second";
                break;
            case 23:
                s = "Twenty-third";
                break;

            case 24:
                s = "Twenty-fourth";
                break;
            case 25:
                s = "Twenty-fifth";
                break;
            case 26:
                s = "Twenty-sixth";
                break;
            case 27:
                s = "Twenty-seventh";
                break;
            case 28:
                s = "Twenty-eighth";
                break;
            case 29:
                s = "Twenty-ninth";
                break;
            case 30:
                s = "Thirtieth";
                break;
            case 31:
                s = "Thirty-first";
                break;
            case 32:
                s = "Thirty-second";
                break;
            case 33:
                s = "Thirty-third";
                break;

            case 34:
                s = "Thirty-fourth";
                break;
            case 35:
                s = "Thirty-fifth";
                break;
            case 36:
                s = "Thirty-sixth";
                break;
            case 37:
                s = "Thirty-seventh";
                break;
            case 38:
                s = "Thirty-eighth";
                break;
            case 39:
                s = "Thirty-ninth";
                break;
            case 40:
                s = "Fortieth";
                break;
            case 41:
                s = "Forty-first";
                break;
            case 42:
                s = "Forty-second";
                break;
            case 43:
                s = "Forty-third";
                break;

            case 44:
                s = "Forty-fourth";
                break;
            case 45:
                s = "Forty-fifth";
                break;
            case 46:
                s = "Forty-sixth";
                break;
            case 47:
                s = "Forty-seventh";
                break;
            case 48:
                s = "Forty-eighth";
                break;
            case 49:
                s = "Forty-ninth";
                break;

            case 50:
                s = "Fiftieth";
                break;
            case 51:
                s = "Fifty-first";
                break;
            case 52:
                s = "Fifty-second";
                break;
            case 53:
                s = "Fifty-third";
                break;

            case 54:
                s = "Fifty-fourth";
                break;
            case 55:
                s = "Fifty-fifth";
                break;
            case 56:
                s = "Fifty-sixth";
                break;
            case 57:
                s = "Fifty-seventh";
                break;
            case 58:
                s = "Fifty-eighth";
                break;
            case 59:
                s = "Fifty-ninth";
                break;
            default:
                break;
        }
        return s;
    }

    public static long MonthAdd(long time, int addMonth, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.clear();
        c.setTimeInMillis(time + dst_off);
        // c.set(Calendar.DST_OFFSET, dst_off);
        c.add(Calendar.MONTH, addMonth);
        return c.getTimeInMillis();
    }

    /**
     * 描述：把时间戳的秒 换成 毫秒
     */
    public static long second2Millis(int second) {
        long a = second;
        return a * 1000;
    }

    /**
     * 描述：把时间戳 毫秒 换成 秒
     */
    public static int Millis2Second(long m) {
        int a = (int) (m / 1000);
        return a;
    }

    /**
     * 描述：根据时间second转化为周的信息
     *
     * @return 如：201423 2014年第23周
     */
    public static int Second2Weekinfo(int second, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.clear();
        c.setTimeInMillis(second2Millis(second) + dst_off);
        // c.set(Calendar.DST_OFFSET, dst_off);
        return c.get(Calendar.YEAR) * 100 + c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 描述：根据时间second转化为周的信息
     *
     * @return 如：201423 2014年第23周
     */
    public static int Millis2Weekinfo(long second, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.clear();
        c.setTimeInMillis(second + dst_off);
        // c.set(Calendar.DST_OFFSET, dst_off);
        return c.get(Calendar.YEAR) * 100 + c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 描述：跟时间戳(秒) 转化为月的信息(201408)
     *
     * @param second 时间戳（秒）
     * @return 201408 2014年8月份
     */
    public static int Second2Month(int second, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.clear();
        c.setTimeInMillis(second2Millis(second) + dst_off);
        // c.set(Calendar.DST_OFFSET,dst_off);
        // 获取的月，是从0开始的，要加1
        return c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH) + 1;
    }

    /**
     * 描述：跟时间戳(秒) 转化为月的信息(201408)
     *
     * @param second 时间戳（秒）
     * @return 201408 2014年8月份
     */
    public static int Mill2Month(long m, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.clear();
        c.setTimeInMillis(m + dst_off);
        // c.set(Calendar.DST_OFFSET, dst_off);
        // 获取的月，是从0开始的，要加1
        return c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH) + 1;
    }

    /**
     * 描述：根据时间戳 转化为 月（Feb 2014）
     *
     * @param second
     * @return 如：Mon 2014
     */
    public static String second2MonthStr(Context context, int second, int timezone, int dst_off) {
        Calendar c = getCalendar(timezone);
        c.clear();
        c.setTimeInMillis(second2Millis(second) + dst_off);
        // c.set(Calendar.DST_OFFSET, dst_off);
        // 获取的月，是从0开始的，要加1
        int month = c.get(Calendar.MONTH);
        StringBuffer sb = new StringBuffer(getMonthName(context, month));
        sb.append(" ");
        sb.append(c.get(Calendar.YEAR));
        return sb.toString();
    }

    /**
     * 描述：把 2013 8 转化为
     *
     * @return
     */
    public static String YearAndMonth2Str(Context context, int year, int month) {
        // -1 的原因是 getMonthName是从 0 开始的
        StringBuffer sb = new StringBuffer(getMonthName(context, month - 1));
        sb.append(",");
        sb.append(year);
        return sb.toString();
    }

    /**
     * 描述：根据 年和 周 来确定 这一周的 起始时间 和 结束时间
     *
     * @param year 如：2014
     * @param week 如：28
     * @return 如：[0]是起始时间，[1]是结束时间
     */
    public static int[] getWeekBeginEndByWeekNum(int year, int week) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.WEEK_OF_YEAR, week);
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, MarkDay);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MINUTE, 0);

        int[] times = new int[2];
        times[0] = Millis2Second(c.getTimeInMillis());
        c.add(Calendar.WEEK_OF_YEAR, 1);
        times[1] = Millis2Second(c.getTimeInMillis());
        return times;
    }

    /**
     * 描述：根据 年和 周 来确定 这一周的 起始时间 和 结束时间
     *
     * @param year  如：2014
     * @param month 如：1
     * @return 如：[0]是起始时间，[1]是结束时间
     */
    public static int[] getMonthBeginEndByMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONDAY, month - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, MarkDay);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MINUTE, 0);

        int[] times = new int[2];
        times[0] = Millis2Second(c.getTimeInMillis());
        c.add(Calendar.MONTH, 1);
        times[1] = Millis2Second(c.getTimeInMillis());
        return times;
    }

    private final static String TAG = "TimeUtil";

    public static boolean HourIs24() {
        /*
         * ContentResolver cv =
         * SmartPillowApp.getScreenManager().getContentResolver(); String
         * strTimeFormat = android.provider.Settings.System.getString(cv,
         * android.provider.Settings.System.TIME_12_24); if(strTimeFormat.equals("24"))
         * { return true; }else return false;
         */

        boolean b = DateFormat.is24HourFormat(BaseApp.getApp());
        return b;
    }

    public static boolean isAM(int hour24, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour24);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.get(Calendar.AM_PM) == Calendar.AM;
    }

    public static int getHour12(int hour24) {

        if (hour24 == 0 || hour24 == 12) {
            return 12;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour24);
        return calendar.get(Calendar.HOUR);
    }

    public static byte getHour24(int hour12, int minute, int apm) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, apm);
        cal.set(Calendar.HOUR, hour12);
        cal.set(Calendar.MINUTE, minute);
        byte hour24 = (byte) cal.get(Calendar.HOUR_OF_DAY);

        if (hour24 == 12) {
            hour24 = 0;
        } else if (hour24 == 0) {
            hour24 = 12;
        }

        return hour24;
    }

    public static int getWeekIndex() {
        Calendar cal = TimeUtil.getCalendar(-100);
        return getWeekIndex(cal);
    }

    public static int getWeekIndex(Calendar cal) {
        int week = cal.get(Calendar.DAY_OF_WEEK);
        week -= 2;
        if (week < 0) {
            week = 0;
        }
        return week;
    }

    /**
     * <h3>获取当前 时间的 infos</h3>
     * <ul>
     * <li>年 月 周 日</li>
     * </ul>
     *
     * @return
     */
    public static short[] getCurrentTimeInfo() {
        Calendar c = getCalendar(-100);
        // 由于早上6点前睡觉 都算 前一天的
        c.add(Calendar.HOUR_OF_DAY, -SleepConfig.SLEEP_DIFF);

        short year = (short) c.get(Calendar.YEAR);
        byte month = (byte) (c.get(Calendar.MONTH) + 1);
        byte day = (byte) c.get(Calendar.DAY_OF_MONTH);

        // 计算当前所在的周
        // 由于Calendar的周是从周日开始的，所以 -1
        c.add(Calendar.DAY_OF_YEAR, -1);
        byte week = (byte) c.get(Calendar.WEEK_OF_YEAR);
        if (month == 12 && week == 1) {// calendar把12月份的最后几天 算为 第二年的第一周，
            // 我们要求分开，第一周是从1月1日开始，所以做了以下处理
            week = (byte) (TimeUtil.GetWeeksInYear(year) + 1);
        }

        short[] infos = new short[4];
        infos[0] = year;
        infos[1] = month;
        infos[2] = week;
        infos[3] = day;

        return infos;
    }

    /**
     * <p>
     * 用于格式化当前日期,作为日志文件名的一部分
     * </p>
     * <p>
     * </p>
     * 2015年7月10日
     */
    public static String getTime() {
        // 用于格式化日期,作为日志文件名的一部分
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        long timestamp = System.currentTimeMillis();
        String time = formatter.format(new Date());
        return time + "----" + timestamp;
    }

}
