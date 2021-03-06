package com.isport.blelibrary.managers.order;

import android.content.Context;

import com.isport.blelibrary.entry.AlarmEntry;
import com.isport.blelibrary.entry.AutoSleep;
import com.isport.blelibrary.entry.DisplaySet;
import com.isport.blelibrary.entry.SedentaryRemind;
import com.isport.blelibrary.entry.WristMode;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.StepArithmeticUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @Author
 * @Date 2018/11/1
 * @Fuction
 */

public class W311Cmd extends ISportOrder {
    /**
     * set base time, but you needn't call it in general
     */
    public byte[] sendBaseTime(Context context, int type) {
        byte is24 = (byte) (DateUtil.is24Hour(context) ? 0 : 1);
        byte metricImperal = 0;
        int timezone = getActiveTimeZone();
        byte activeTimeZone = (byte) (timezone < 0 ? Math.abs(timezone) * 2 + 0x80 : Math.abs(timezone) * 2);
        timezone = DateUtil.getTimeZone();
        byte currentTimeZone = (byte) (timezone < 0 ? Math.abs(timezone) * 2 + 0x80 : Math.abs(timezone) * 2);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

      /*  byte[] time = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) type, (byte) 0xfe, metricImperal, is24,
                activeTimeZone, currentTimeZone,
                (byte) (year >> 8), (byte) year, (byte) month, (byte) day, (byte) week, (byte) hour, (byte)
                minute, (byte) seconds};*/

        byte[] time = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) type, (byte) 0xfe,
                (byte) (year >> 8), (byte) year, (byte) month, (byte) day, (byte) week, currentTimeZone, (byte)
                hour, (byte) minute, (byte) seconds, is24};
        return time;

    }

    public int getActiveTimeZone() {
        String timezoneId = null;
        if (timezoneId == null) {
            timezoneId = TimeZone.getDefault().getID();
        }
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        int rawOffSet = timezone.getRawOffset();
        int offset = rawOffSet / 3600000;
        return offset;
        /*String timezone = TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT).trim().substring(3).split(":")[0];
        String tmp = timezone.substring(0,1);
        boolean isPlus = true;
        if(tmp.equals("+")){
            timezone = timezone.substring(1,timezone.length());
            isPlus = true;
        }else if (tmp.equals("-")){
            timezone = timezone.substring(1,timezone.length());
            isPlus = false;
        }
        int tz = (isPlus?Integer.valueOf(timezone):((-1)*Integer.valueOf(timezone)));
        return sharedPreferences.getInt(USER_ACTIVE_TIME_ZONE,
                Integer.valueOf(tz));*/
    }

    //????????????????????????????????????????????????????????????????????????
    public byte[] sendVibrateConnected() {
        byte[] data = new byte[]{(byte) 0xbe, 0x06, 0x31, (byte) 2, (byte) (true ? 1 : 0),
                (byte)
                        0xed};
        return data;
    }

    //?????????????????? BE0203ED
    public byte[] openReal() {
        byte[] data = new byte[]{(byte) 0xbe, 0x02, 0x03, (byte) 0xed};
        return data;
    }

    public byte[] sendCmdSync() {
        byte[] data = new byte[]{(byte) 0xbe, 0x02, 0x05, (byte) 0xed};
        return data;
    }

    /**
     * ??????????????????
     *
     * @param context
     * @return
     */
    public byte[] sendUserInfo(Context context) {

        //?????????????????????????????????????????????

        int year = BaseManager.mYear;
        int month = BaseManager.mMonth;
        int day = BaseManager.mDay;
        int targetSteps = BaseManager.targStep;
        int weight = (int) BaseManager.mWeight * 100;
        //weight = (int) ((metricImperial == 0 ? weight : weight / 0.45359237f));
        //9???????????? 00-???????????? 01-??????
        int strideLength = (int) (60 * 100);


        strideLength = StepArithmeticUtil.getStep(BaseManager.mHeight, BaseManager.mSex) * 100;


        // Logger.myLog("sendUserInfo BaseManager.mHeight:" + BaseManager.mHeight + ",BaseManager.mSex" + BaseManager.mSex + ",strideLength:" + strideLength);

        // Logger.myLog("BaseManager.mHeight:" + BaseManager.mHeight + ",BaseManager.mWeight :" + BaseManager.mWeight + ",BaseManager.targStep" + BaseManager.targStep + ",BaseManager.mAge:" + BaseManager.mAge + ",BaseManager.mSex:" + BaseManager.mSex + ",strideLength:" + strideLength);
        //strideLength = (metricImperial == 0 ? strideLength : (int) (strideLength * 2.54));
        int height = Math.round(BaseManager.mHeight * 100);
        AutoSleep autoSleep = AutoSleep.getInstance(context);
        int sleepHour = autoSleep.getSleepTargetHour();
        int sleepMin = autoSleep.getSleepTargetMin();
        //Logger.myLog("userinfo:weight:" + weight + ",height:" + height);
        return new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x03, (byte) 0xfe, (byte) ((height & 0xffff) >> 8)
                , (byte) height, (byte) BaseManager.mAge, (byte) BaseManager.mSex,
                (byte) (weight >> 8), (byte) weight, (byte) (targetSteps >> 16),
                (byte) (targetSteps >> 8), (byte) targetSteps, (byte) (strideLength >> 8), (byte)
                strideLength, (byte) sleepHour, (byte) sleepMin};
    }


    /**
     * ?????????????????????
     *
     * @param year
     * @param month
     * @param day
     */
    public byte[] sendSyncDay(int year, int month, int day) {
        byte[] data = new byte[10];
        data[0] = (byte) 0xbe;
        data[1] = 0x02;
        data[2] = 0x01;
        data[3] = (byte) 0xfe;
        data[4] = (byte) (year >> 8);
        data[5] = (byte) year;
        data[6] = (byte) month;
        data[7] = (byte) day;
        data[8] = 0;
        data[9] = 0;
        //sendCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, data);
        //?????????????????????????????????
        // syncHandler.sendEmptyMessageDelayed(0x01, DEFAULT_SYNC_TIMEOUT);

        return data;
    }

    /**
     * to reset device
     */
    public byte[] reset() {
        //byte[] bs = new byte[]{(byte) 0xff, (byte) 0xfa, (byte) 0xfc, (byte) 0xf7, 0x00, 0x01, 0x02, 0x07,
        // 0x55, 0x33, 0x66, 0x31, 0x18, (byte) 0x89, 0x60, 0x00};
        byte[] bs = new byte[]{(byte) 0xBE, 0x06, 0x30, (byte) 0xED};
        return bs;
    }

    /**
     * set base time, but you needn't call it in general
     */
   /* public void sendBaseTime() {
        if (state == STATE_CONNECTED) {
            UserInfo userInfo = UserInfo.getInstance(context);
            byte is24 = (byte) (DateUtil.is24Hour(context) ? 0 : 1);
            byte metricImperal = (byte) userInfo.getMetricImperial();
            int timezone = userInfo.getActiveTimeZone();
            byte activeTimeZone = (byte) (timezone < 0 ? Math.abs(timezone) * 2 + 0x80 : Math.abs(timezone) * 2);
            timezone = DateUtil.getTimeZone();
            byte currentTimeZone = (byte) (timezone < 0 ? Math.abs(timezone) * 2 + 0x80 : Math.abs(timezone) * 2);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);

            byte[] time = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x01, (byte) 0xfe, metricImperal, is24,
                    activeTimeZone, currentTimeZone,
                    (byte) (year >> 8), (byte) year, (byte) month, (byte) day, (byte) week, (byte) hour, (byte)
                    minute, (byte) seconds};
            sendCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, time);
        }
    }*/

    /**
     * if one of the alarms  is on,the alarm is on
     *
     * @param list the size no more than 5
     */
    public byte[] w311setAlarm(List<AlarmEntry> list) {

        byte[] data = new byte[20];
        data[0] = (byte) 0xbe;
        data[1] = 0x01;
        data[2] = 0x09;
        data[3] = (byte) 0xfe;

        int states = 0;
        int index = 5;
        for (int i = 0; i < (list.size() > 5 ? 5 : list.size()); i++) {

            AlarmEntry entry = list.get(i);
            data[index] = (byte) entry.getStartHour();
            index++;
            data[index] = (byte) entry.getStartMin();
            index++;
            //byte repeat = ((byte) (entry.getRepeat() > 0 ? (entry.getRepeat() | 0x80) : entry.getRepeat()));
            byte repeat = ((byte) ((entry.getRepeat() & 0xff) > 0 ? ((entry.getRepeat() & 0xff) & 0x7F) : entry
                    .getRepeat()));
            data[index] = repeat;
            index++;
            if (entry.isOn()) {
                states = states + (1 << i);
            }
        }
        data[4] = (byte) states;
        if (list.size() < 5) {
            for (int i = list.size(); i < 5; i++) {
                data[index] = (byte) 0xff;
                index++;
                data[index] = (byte) 0xff;
                index++;
                data[index] = 0x00;
                index++;
            }


        }
        return data;
    }

    //********************************************W311**************************************************//

    /**
     * ???????????????????????????
     * sync time to ble device
     *
     * @param context
     * @return
     */
    public static byte[] send_syncTime(Context context) {
        int timezone = DateUtil.getTimeZone();
        byte currentTimeZone = (byte) (timezone < 0 ? Math.abs(timezone) * 2 + 0x80 : Math.abs(timezone) * 2);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        byte is24 = (byte) (DateUtil.is24Hour(context) ? 0 : 1);

        byte[] time = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x02, (byte) 0xfe,
                (byte) (year >> 8), (byte) year, (byte) month, (byte) day, (byte) week, currentTimeZone, (byte)
                hour, (byte) minute, (byte) seconds, is24};
        return time;
    }

    /**
     * ???????????????????????????
     * send user info to device, for example birthday, height, weight, target step ,stride length and so on.
     * ??????/??????/????????????*100
     *
     * @return
     */
    public static byte[] send_set_userInfo(int height, int age, int gender, int weight, int targetSteps, int strideLength, int sleepHour, int sleepMin) {
        weight = weight * 100;
        strideLength = strideLength * 100;
        height = height * 100;
        byte[] data = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x03, (byte) 0xfe, (byte) (height >> 8), (byte) height, (byte) age, (byte)
                gender, (byte) (weight >> 8), (byte) weight, (byte) (targetSteps >> 16),
                (byte) (targetSteps >> 8), (byte) targetSteps, (byte) (strideLength >> 8), (byte)
                strideLength, (byte) sleepHour, (byte) sleepMin};
        return data;
    }

    /**
     * ???????????????????????????/ /??????,12H/24H ,????????????????????? ,????????????
     *
     * @return
     */
    public static byte[] send_get_generalInfo() {
        byte[] data = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x01, (byte) 0xED};
        return data;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public static byte[] send_get_WeightBrithStrideLength() {
        byte[] data = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x03, (byte) 0xED};
        return data;
    }

    /**
     * black-and-white display
     * ????????????????????????????????????
     *
     * @param blackOrWhite 0 ?????? 1 ??????  display ??????????????????  0 ??????  1 ?????????
     * @return
     */
    public static byte[] send_get_BlackAndWhiteDisplay(int blackOrWhite, boolean display) {
        byte[] data = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x04, (byte) 0xFE, (byte) blackOrWhite, (byte) (display ? 0x00 : 0x01)};
        return data;
    }

    /**
     * ??????????????????????????????
     *
     * @param open
     * @return
     */
    public static byte[] send_privacy(boolean open, String mac) {
        byte[] bs = new byte[13];
        bs[0] = (byte) 0xbe;
        bs[1] = 0x01;
        bs[2] = 0x05;
        bs[3] = (byte) 0xfe;
        bs[4] = (byte) 0xff;
        bs[5] = 0x00;
        bs[6] = 0x00;
//        BE 01 05 FE FF 00 00 98 E7 F5 A1 D7 4A
//        String maccccc = getBtAddressViaReflection();
//        if (maccccc == null) {
//            maccccc = mac;
//        }
        String[] macs = mac.split(":");
        for (int i = 0; i < macs.length; i++) {
            bs[7 + i] = (byte) Integer.parseInt(macs[i], 16);
        }
        return bs;
    }

//    public static String getBtAddressViaReflection() {
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        Object bluetoothManagerService = new Mirror().on(bluetoothAdapter).get().field("mService");
//        if (bluetoothManagerService == null) {
//            return null;
//        }
//        Object address = new Mirror().on(bluetoothManagerService).invoke().method("getAddress").withoutArgs();
//        if (address != null && address instanceof String) {
//            return (String) address;
//        } else {
//            return null;
//        }
//    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public static byte[] send_get_resetInfo() {
        byte[] data = new byte[]{(byte) 0xA5, (byte) 0x01, (byte) 0x00, (byte) 0xED};
        return data;
    }

    public static byte[] send_setAutoSleep(AutoSleep autoSleep) {
        byte[] data = null;
        int switchOpen = (autoSleep.isAutoSleep() ? 1 : 0);
        if (switchOpen == 0) {
            data = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x07, (byte) 0xfe, (byte) 0x00, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
                    , (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        } else {
            boolean isSleep = autoSleep.isSleep();
            boolean isSleepRemind = autoSleep.isSleepRemind();
            boolean isNap = autoSleep.isNap();
            boolean isNapRemind = autoSleep.isNapRemind();
//            if (baseDevice != null && (baseDevice.getDeviceType() != BaseDevice.TYPE_W311N && baseDevice
//                    .getDeviceType() != BaseDevice.TYPE_AT200 &&
//                    baseDevice.getDeviceType() != BaseDevice.TYPE_AS97)) {
//                isNapRemind = false;
//                isNap = false;
//            }
            int sleepStartHour = autoSleep.getSleepStartHour();
            int sleepStartMinute = autoSleep.getSleepStartMin();
            Calendar ccc = Calendar.getInstance();
            ccc.set(Calendar.HOUR_OF_DAY, sleepStartHour);
            ccc.set(Calendar.MINUTE, sleepStartMinute);
            ccc.add(Calendar.MINUTE, -1 * autoSleep.getSleepRemindTime());
            int sleepRemindHour = ccc.get(Calendar.HOUR_OF_DAY);
            int sleepReminMin = ccc.get(Calendar.MINUTE);

            int napStartHour = autoSleep.getNapStartHour();
            int napStartMin = autoSleep.getNapStartMin();
            ccc = Calendar.getInstance();
            ccc.set(Calendar.HOUR_OF_DAY, napStartHour);
            ccc.set(Calendar.MINUTE, napStartMin);
            ccc.add(Calendar.MINUTE, -1 * autoSleep.getNapRemindTime());
            int napRemindHour = ccc.get(Calendar.HOUR_OF_DAY);
            int napRemindMin = ccc.get(Calendar.MINUTE);
                /*17Byte BE???01???07+FE+???????????????1byte???
                +??????????????????(1byte)+???????????????(1byte)
                +??????????????????(1byte) +???????????????(1byte)
                +??????????????????(1byte) +???????????????(1byte)
                +??????????????????(1byte) +???????????????(1byte)
                +??????????????????(1byte) +???????????????(1byte)
                +??????????????????(1byte) +???????????????(1byte)*/
            data = new byte[]{(byte) 0xbe, (byte) 0x01, (byte) 0x07, (byte) 0xfe, (byte) 0x01, (byte) (isSleep ?
                    autoSleep.getSleepStartHour() : 0xfe),
                    (byte) (isSleep ? autoSleep.getSleepStartMin() : 0xfe), (byte) (isSleepRemind ?
                    sleepRemindHour : 0xfe),
                    (byte) (isSleepRemind ? sleepReminMin : 0xfe),
                    (byte) (isSleep ? autoSleep.getSleepEndHour() : 0xfe), (byte) (isSleep ? autoSleep
                    .getSleepEndMin() : 0xfe),
                    (byte) (isNap ? autoSleep.getNapStartHour() : 0xfe),
                    (byte) (isNap ? autoSleep.getNapStartMin() : 0xfe), (byte) (isNap ? autoSleep.getNapEndHour()
                    : 0xfe),
                    (byte) (isNap ? autoSleep.getNapEndMin() : 0xfe), (byte) (isNapRemind ? napRemindHour : 0xfe),
                    (byte) (isNapRemind ? (napRemindMin) : 0xfe)};
        }
        return data;
    }

    /**
     * set which interface show on device ??????????????????
     *
     * @param displaySet
     */
    public static byte[] send_setDisplayInterface(DisplaySet displaySet) {
        if (displaySet != null) {
            byte[] data = new byte[20];
            data[0] = (byte) 0xbe;
            data[1] = (byte) 0x01;
            data[2] = (byte) 0x08;
            data[3] = (byte) 0xfe;
            int index = 4;
           /* if (displaySet.isShowLogo()) {
                data[index] = 0x00;
                index++;
            }*/
            if (displaySet.isShowCala()) {
                data[index] = 0x03;
                index++;
            }
            if (displaySet.isShowDist()) {
                data[index] = 0x04;
                index++;
            }
            if (displaySet.isShowSportTime()) {
                data[index] = 0x05;
                index++;
            }
            if (displaySet.isShowProgress()) {
                data[index] = 0x06;
                index++;
            }
            if (displaySet.isShowEmotion()) {
                data[index] = 0x07;
                index++;
            }
            if (displaySet.isShowAlarm()) {
                data[index] = 0x08;
                index++;
            }
            if (displaySet.isShowSmsMissedCall()) {
                data[index] = 0x0A;
                index++;
            }
            if (displaySet.isShowIncomingReminder()) {
                data[index] = 0x0B;
                index++;
            }
            if (displaySet.isShowMsgContent()) {
                data[index] = 0x0D;
                index++;
            }
            if (displaySet.isShowCountDown()) {
                data[index] = 0x0f;
                index++;
            }
            for (int i = index; i < 20; i++) {
                data[i] = (byte) 0xff;
            }
            return data;
        }
        return null;
    }

    /**
     * if one of the alarms  is on,the alarm is on
     *
     * @param list the size no more than 5 w311 ??????????????????
     */
    public static byte[] setAlarm(List<AlarmEntry> list) {

        if (list != null && list.size() > 0) {
            byte[] data = new byte[20];
            data[0] = (byte) 0xbe;
            data[1] = 0x01;
            data[2] = 0x09;
            data[3] = (byte) 0xfe;

            int states = 0;
            int index = 5;
            for (int i = 0; i < (list.size() > 5 ? 5 : list.size()); i++) {

                AlarmEntry entry = list.get(i);
                data[index] = (byte) entry.getStartHour();
                index++;
                data[index] = (byte) entry.getStartMin();
                index++;
                //byte repeat = ((byte) (entry.getRepeat() > 0 ? (entry.getRepeat() | 0x80) : entry.getRepeat()));
                byte repeat = ((byte) ((entry.getRepeat() & 0xff) > 0 ? ((entry.getRepeat() & 0xff) & 0x7F) : entry
                        .getRepeat()));
                data[index] = repeat;
                index++;
                if (entry.isOn()) {
                    states = states + (1 << i);
                }
            }
            data[4] = (byte) states;
            if (list.size() < 5) {
                for (int i = list.size(); i < 5; i++) {
                    data[index] = (byte) 0xff;
                    index++;
                    data[index] = (byte) 0xff;
                    index++;
                    data[index] = 0x00;
                    index++;
                }
            }
            return data;
        }
        return null;
    }


    /**
     * ??????????????????
     * ?????????BE-01-21-FE-
     * <p>
     * ??????(00:??? 01:???)-???????????????-?????????-???
     * ????????????-?????????
     * ?????????DE-01-21-ED
     */
    public static byte[] setDisturb(boolean open, int startHour, int startMin, int endHour, int endMin) {
        byte[] cmds = new byte[9];
        if (open) {
            cmds = new byte[]{(byte) 0xbe, 0x01, 0x21, (byte) 0xFE, (byte) 0x01, (byte) startHour, (byte) startMin, (byte) endHour, (byte) endMin};
        } else {
            cmds = new byte[]{(byte) 0xbe, 0x01, 0x21, (byte) 0xFE, (byte) 0x00, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        }
        return cmds;
    }

    /**
     * set wrist mode,left hand or right hand
     * ???????????????
     * 0 ??????  1 ??????
     *
     * @param wristMode
     */
    public static byte[] setWristMode(WristMode wristMode) {
        if (wristMode != null) {
            byte[] data = new byte[]{(byte) 0xbe, 0x01, 0x0b, (byte) 0xfe,
                    (byte) (wristMode.isLeftHand()
                            ? 0 : 1)};
            return data;
        }
        return null;
    }

    /**
     * one of list is on,the sedentary remnid is on
     * ????????????
     *
     * @param list max size is 3
     */
    public static byte[] setSedintaryRemind(List<SedentaryRemind> list) {
        if (list != null && list.size() > 0) {
            byte[] data = new byte[19];
            data[0] = (byte) 0xbe;
            data[1] = 0x01;
            data[2] = 0x0c;
            data[3] = (byte) 0xfe;
            boolean isOn = false;
            List<byte[]> listD = new ArrayList<>();
            int index = -1;
            for (int i = 0; i < (list.size() > 3 ? 3 : list.size()); i++) {
                if (list.get(i).isOn()) {
                    SedentaryRemind remind = list.get(i);
                    isOn = true;
                    index = i;
                    byte[] tp = new byte[4];
                    tp[0] = (byte) remind.getBeginHour();
                    tp[1] = (byte) remind.getBeginMin();
                    tp[2] = (byte) remind.getEndHour();
                    tp[3] = (byte) remind.getEndMin();
                    listD.add(tp);
                } else {
                    byte[] tp = new byte[]{0, 0, 0, 0};
                    listD.add(tp);
                }
            }
            data[4] = (byte) (isOn ? 1 : 0);
            if (!isOn) {
                for (int i = 5; i < 19; i++) {
                    data[i] = 0;
                }
            } else {
                if (index != -1) {
                    System.arraycopy(listD.get(index), 0, data, 5, 4);
                    if (listD.size() > 1) {
                        System.arraycopy(list.get(listD.size() - 1 - index), 0, data, 5 + 4, 4);
                        for (int i = 0; i < listD.size(); i++) {
                            if (i != index && (i != listD.size() - 1 - index)) {
                                System.arraycopy(list.get(i), 0, data, 13, 4);
                            }
                        }
                    }
                }
                data[17] = (byte) (SedentaryRemind.noExerceseTime / 60);
                data[18] = (byte) (SedentaryRemind.noExerceseTime % 60);
            }
            return data;
        }
        return null;
    }

    /**
     * set heart timing test
     * ????????????????????????  BE0115FE  BE 01 19 FE 01 05
     *
     * @param heartTimingTest
     */

    public static byte[] setHeartTimingTest(boolean heartTimingTest) {
        byte[] data = new byte[17];
        data[0] = (byte) 0xbe;
        data[1] = 0x01;
        data[2] = 0x15;
        data[3] = (byte) 0xfe;

        if (heartTimingTest) {
            // data[4]=0;
            data[4] = (byte) 0x81;
        } else {
            //data[4]=1;
            data[4] = (byte) 0x80;
        }
        for (int i = 5; i < 17; i++) {
            data[i] = (byte) 0xff;
        }
       /* if (heartTimingTest) {
            data[4] = 0;
            for (int i = 5; i < 17; i++) {
                data[i] = (byte) 0xff;
            }
        } else {
            data[4] = 1;
            data[5] = heartTimingTest.isFirstEnable() ? (byte) heartTimingTest.getFirStartHour() : (byte) 0xff;
            data[6] = heartTimingTest.isFirstEnable() ? (byte) heartTimingTest.getFirstStartMin() : (byte) 0xff;
            data[7] = heartTimingTest.isFirstEnable() ? (byte) heartTimingTest.getFirstEndHour() : (byte) 0xff;
            data[8] = heartTimingTest.isFirstEnable() ? (byte) heartTimingTest.getFirstEndMin() : (byte) 0xff;
            data[9] = heartTimingTest.isSecondEnable() ? (byte) heartTimingTest.getSecStartHour() : (byte) 0xff;
            data[10] = heartTimingTest.isSecondEnable() ? (byte) heartTimingTest.getSecStartMin() : (byte) 0xff;
            data[11] = heartTimingTest.isSecondEnable() ? (byte) heartTimingTest.getSecEndHour() : (byte) 0xff;
            data[12] = heartTimingTest.isSecondEnable() ? (byte) heartTimingTest.getSecEndMin() : (byte) 0xff;
            data[13] = heartTimingTest.isThirdEnable() ? (byte) heartTimingTest.getThirdStartHour() : (byte) 0xff;
            data[14] = heartTimingTest.isThirdEnable() ? (byte) heartTimingTest.getThirdStartMin() : (byte) 0xff;
            data[15] = heartTimingTest.isThirdEnable() ? (byte) heartTimingTest.getThirdEndHour() : (byte) 0xff;
            data[16] = heartTimingTest.isThirdEnable() ? (byte) heartTimingTest.getThirdEndMin() : (byte) 0xff;
        }*/
        return data;
    }

    /**
     * {@link #setAlarm(List)}
     * set alarm description
     * ????????????????????????
     *
     * @param description no more than 15 byte
     * @param index       the value is between 0 and 4,the order is same as {@link #setAlarm(List)}
     * @param showDescrip show description content on device
     */
    public static byte[] setAlarmDescription(String description, int index, boolean showDescrip) {
        description = (description == null ? "" : description);
        byte[] data = new byte[20];
        data[0] = (byte) 0xbe;
        data[1] = 0x01;
        data[2] = 0x16;
        data[3] = (byte) 0xfe;
        if (description.trim().equals(""))
            showDescrip = false;
        data[4] = (byte) (showDescrip ? 1 : 0);
        data[5] = (byte) index;
        byte[] tps = null;
        try {
            tps = description.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            tps = description.getBytes();
            e.printStackTrace();
        }
        int tpsL = tps.length;
        System.arraycopy(tps, 0, data, 6, tpsL > 14 ? 14 : tpsL);
        if (tps.length < 14) {
            for (int i = 19; i > tpsL + 5; i--) {
                data[i] = (byte) 0xff;
            }
        }
        return data;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public static byte[] send_get_privacy_mac() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x05, (byte) 0xEE};
        return data;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    public static byte[] send_get_sleepdata() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x07, (byte) 0xED};
        return data;
    }


    /**
     * ??????????????????????????????
     *
     * @return
     */
    public static byte[] send_get_heartRateTestTime() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x15, (byte) 0xED};
        return data;
    }

    /**
     * ??????5????????????????????????
     *
     * @return
     */
    public static byte[] send_set_5MinutesAutomaticAttentionRate(boolean open) {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x20, (byte) 0xFE, (byte) (open ? 1 : 0)};
        return data;
    }

    /**
     * ???????????????????????????????????????????????????
     * ?????????BE-01-19-FE-??????-??????  ??????(00?????? 01????????? ???????????????15?????????30?????????45?????????60 ??????
     * ?????????DE-01-19-ED
     * ?????????????????????????????????????????????????????????????????????????????? ?????????????????????????????????????????????????????????15?????????30?????????45?????????60?????????
     * ??????????????????
     * time 15min  30min  45min  60min
     */
    public static byte[] setAutomaticHeartRateAndTime(boolean enable, int time) {
        byte[] data = new byte[]{(byte) 0xbe, 0x01, 0x19, (byte) 0xFE, (byte) (enable ? 1 : 0), (byte) time};
        return data;
    }

    /**
     * 1.????????????????????????
     * ?????????BE-01-18-FE-?????????00?????? 01?????????
     * ?????????DE-01-18-ED  W311??????BEAT ??????????????? 91.12     ???307 REFLEX ???????????????90.88 ??????
     */
    public static byte[] raiseHand(boolean enable) {
        byte[] data;
        if (enable) {
            data = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x80, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        } else {
            data = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x00, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        }
        return data;

    }

    public static byte[] raiseHand(int type) {
        byte[] data = null;
        /**
         * ?????????2
         *          ????????????0
         *          ??????????????????
         */
        switch (type) {
            case 0:
                data = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x01, (byte) 0x01};
                break;
            case 1:
                data = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x01, (byte) 0x00};
                break;
            case 2:
                data = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x00, (byte) 0x00};
                break;

        }
        return data;
    }


    /**
     * 1.??????????????????????????????????????????
     * ?????????BE-01-18-FE-??????(bit7:????????????bit0???????????????????????????
     * 00 ?????????-??????????????????
     * ?????????-?????????-???????????????-????????????
     * 80 ??????????????????81 ???????????????00 ????????????
     * ?????????DE-01-18-ED  W311??????BEAT ??????????????? 91.61
     * type 0  ?????????   1  ????????????  2  ????????????
     * startHour endHour  0-23
     * endHour   endMin   0-59
     */
    public static byte[] raiseHand(int type, int startHour, int startMin, int endHour, int endMin) {
        byte[] cmds = new byte[9];
        if (type == 0) {
            cmds = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x81, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        } else if (type == 1) {
            cmds = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x81, (byte) startHour, (byte) startMin, (byte) endHour, (byte) endMin};
        } else if (type == 2) {
            cmds = new byte[]{(byte) 0xbe, 0x01, 0x18, (byte) 0xFE, (byte) 0x00, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        }
        return cmds;
    }


    /**
     * 2??????????????????????????????
     * ?????????BE-01-19-FE-?????????00?????? 01?????????
     * ?????????DE-01-19-ED
     */
    public static byte[] heartRate(boolean enable) {
        byte[] data = new byte[]{(byte) 0xbe, 0x01, 0x19, (byte) 0xFE, (byte) (enable ? 1 : 0)};
        return data;
    }

    /**
     * ????????????
     */


    /**
     * send notification or sms to ble device
     * ????????????
     *
     * @param notiContent  ??????????????????15byte?????????15?????????
     * @param packageIndex ?????????
     * @param notitype     ????????????
     *                     Message=12 QQ ?????????=13 Wechat=14 at=14
     *                     Skype=15 Facebook=16 Twitter=17
     *                     Linkedin=18 Instagram=19 Life inovatyon=1A
     */
    public static byte[] sendNotiCmd(byte[] notiContent, int packageIndex, int notitype) {
        byte[] btCmd = new byte[20];
        btCmd[0] = (byte) 0xbe;
        btCmd[1] = (byte) 0x06;
        btCmd[2] = (byte) notitype;
        btCmd[3] = (byte) 0xfe;
        btCmd[4] = (byte) packageIndex;
        if (notiContent != null && notiContent.length <= 15) {
            System.arraycopy(notiContent, 0, btCmd, 5, notiContent.length);
        }
        int length = (notiContent == null ? 0 : notiContent.length);
        if (length < 15 && length >= 0) {
            for (int i = length; i < 15; i++) {
                btCmd[5 + i] = (byte) 0xff;
            }
        }
        return btCmd;
    }

    /**
     * ?????????
     */
    public static byte[] findPhone() {
        byte[] data = new byte[]{(byte) 0xDE, 0x06, 0x10, (byte) 0xED};
        return data;
    }

    /**
     * ?????????
     */
    public static byte[] findDevice() {
        byte[] data = new byte[]{(byte) 0xBE, 0x06, 0x0F, (byte) 0xED};
        return data;
    }

    /**
     * ??????????????????
     */
    public static byte[] closeAntiLost() {
        byte[] data = new byte[]{(byte) 0xBE, 0x06, 0x0E, (byte) 0xED};
        return data;
    }


    /**
     * ??????????????????
     */
    public static byte[] openAntiLost() {
        byte[] data = new byte[]{(byte) 0xBE, 0x06, 0x0D, (byte) 0xED};
        return data;
    }

    /**
     * ???????????????????????????   writeTXCharacteristicItem(new byte[]{(byte) 0xBE, 0x06, (byte) 0x09, (byte) 0xFB});
     */
    public static byte[] send_getDeviceInfo() {
        byte[] data = new byte[]{(byte) 0xBE, 0x06, 0x09, (byte) 0xFB};
        return data;
    }

    /**
     * ??????????????????????????????
     * 0 ????????????  1 ????????????  2 ???????????????  3 ??????????????? F0????????????
     */
    public static byte[] openMusic(byte set) {
        byte[] data = new byte[]{(byte) 0xDE, 0x06, 0x08, (byte) 0xFE, set, (byte) 0xED};
        return data;
    }

    /**
     * ??????????????????
     * 1 ????????????
     */
    public static byte[] getPhoto() {
        byte[] data = new byte[]{(byte) 0xDE, 0x06, 0x07, (byte) 0xFE, 0x01, (byte) 0xED};
        return data;
    }

    /**
     * send phone number or contact name
     *
     * @param phoneOrName
     * @param type        0 phone 1 name
     *                    ?????? DE 06 01/02 ED   01 ??????  02  ?????????
     */
    public static byte[] sendPhoneNum(String phoneOrName, int type) {
        if (type == 0 && phoneOrName == null)
            return null;
        byte[] time = new byte[20];
        int c = 0;
        time[c++] = (byte) 0xbe;
        time[c++] = (byte) 0x06;
        time[c++] = (byte) (type == 0 ? 0x02 : 0x01);
        time[c++] = (byte) 0xfe;
        phoneOrName = (phoneOrName == null ? "" : phoneOrName);
        try {
            byte[] bs = phoneOrName.getBytes("UTF-8");
            int len = (bs == null ? 0 : bs.length);
            len = (len > 15 ? 15 : len);
            time[c++] = (byte) len;

            for (int i = 0; i < len; i++) {
                time[c++] = bs[i];
            }
            if (c < 20) {
                for (int t = c; t < 20; t++) {
                    time[c++] = (byte) 0xff;
                }
            }
            return time;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Query the heart rate stored data for the specified date
     * BE-02-10-FE-YY(???2byte)-MM(???)-DD(???)-??????  (?????????????????????????????????APP?????????00???
     */
    public byte[] queryHeartRateHistoryByDate(int year, int month, int day) {
       /* if (state == STATE_CONNECTED) {
            isSyncHeartRateHistorying = true;
            syncHandler.sendEmptyMessageDelayed(0x03, HEARTRATE_SYNC_TIMEOUT);*/
        byte[] value = new byte[9];
        value[0] = (byte) 0xBE;
        value[1] = (byte) 0x02;
        value[2] = (byte) 0x10;
        value[3] = (byte) 0xFE;
        value[4] = (byte) (year >> 8);
        value[5] = (byte) year;
        value[6] = (byte) month;
        value[7] = (byte) day;
        value[8] = 0x00;
        return value;
        /* sendCommand(SEND_DATA_CHAR, mGattService, mBluetoothGatt, value);*/
    }

    /**
     * ???????????????
     *
     * @param comming_phone
     */
    public byte[] sendPhoneName(String comming_phone) {
        comming_phone = (comming_phone == null ? "" : comming_phone);
        byte[] time = new byte[20];
        int c = 0;
        time[c++] = (byte) 0xbe;
        time[c++] = (byte) 0x06;
        time[c++] = (byte) 0x01;
        time[c++] = (byte) 0xfe;
        byte[] bs = comming_phone.getBytes();
        Byte phoneLength = (byte) bs.length;

        byte len = phoneLength > 15 ? 15 : phoneLength;
        time[c++] = len;
        //if (!IsChineseOrNot.isChinese(comming_phone)) {
        for (int i = 0; i < len; i++) {
            byte b = bs[i];
            time[c++] = b;
        }
        /*} else {
            time[c++] = 0x00;
		}*/

        if (c < 20) {
            for (int t = c; t < 20; t++) {
                time[c++] = (byte) 0xff;
            }
        }

        return time;
    }


    /**
     * ???????????????????????????????????????????????????
     * ????????? 4Bytes BE+01+08+ED
     * 1.20- -1 1 ?????????????????????????????????????????????
     * ????????? 20Bytes DE ??? 01 ??? 08+FE
     * + + ???????????? 1 1 ??? 1byte ??? + + ???????????? 2 2 ??? 1byte ??? + + ???????????? 3 3 ??? 1byte ???
     * + + ???????????? 4 4 ??? 1byte ??? + + ???????????? 5 5 ??? 1byte ??? + + ???????????? 6 6 ??? 1byte ???
     * + + ???????????? 7 7 ??? 1byte ??? + + ???????????? 8 8 ??? 1byte ??? + + ???????????? 9 9 ??? 1byte ???
     * + + ???????????? 10 ??? 1byte ??? + + ???????????? 11 ??? 1byte ??? + + ???????????? 12
     * ??? 1byte ???
     * + + ???????????? 13 ??? 1byte ??? + + ???????????? 14 ??? 1byte ??? + + ???????????? 15
     * ??? 1byte ???
     * + + ???????????? 16 ??? 1byte
     *
     * @return
     */
    public static byte[] send_get_display() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x08, (byte) 0xED};
        return data;
    }

    /**
     * ???????????????????????????????????? 4Bytes BE+01+09+ED
     * 1.22- - 1, ?????????????????????????????????
     * ????????? 20Bytes DE+01+09+FE+ ??????????????? 1Byte ???
     * + + ??? 1  ???????????? (1Byte)+ ??? 1  ???????????? (1Byte)+ ?????? 1  ????????????
     * (1Byt e)
     * + + ??? 2  ???????????? (1Byte)+ ??? 2  ???????????? (1Byte)+ ?????? 2  ????????????
     * (1Byte)
     * + + ??? 3  ???????????? (1Byte)+ ??? 3  ???????????? (1Byte)+ ?????? 3  ????????????
     * (1Byte)
     * + + ??? 4  ???????????? (1Byte)+ ??? 4  ???????????? (1Byte)+ ?????? 4  ????????????
     * (1Byte)
     * + + ??? 5  ???????????? (1Byte)+ ??? 5  ???????????? (1Byte)+ ?????? 5  ????????????
     * (1Byte)
     */
    public static byte[] send_get_AlarmList() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x09, (byte) 0xED};
        return data;
    }

    /**
     * ??????????????????
     * <p>
     * /**
     * *  1, ????????????????????????
     * * ????????? 19Bytes DE ??? 01 ??? 0C+FE ??????????????? (1byte)
     * * + + ????????? 1(1byte) ???????????? 1(1byte) ???????????? 1(1byte) ????????????
     * * 1(1byte)
     * * + + ????????? 2(1byte) ???????????? 2(1byte) ???????????? 2(1byte) ????????????
     * * 2(1byte)
     * * + + ????????? 3(1byte) ???????????? 3(1byte) ???????????? 3(1byte) ????????????
     * * 3(1byte)
     * * ???????????? (1byte) ????????????
     * * @return
     *
     * @return
     */
    public static byte[] send_get_SedintaryRemind() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x0C, (byte) 0xED};
        return data;
    }

    /**
     * ??????????????????????????????????????????
     * ????????? 4Bytes BE+ 01+15+ED
     * 1.52- -1 1 ????????????????????????
     * ?????? : 17Byte DE ??? 01 ??? 15+FE+ ??????????????? 1byte ???
     * + + ???????????????????????? (1byte)+  ????????????????????? (1byte)
     * + + ???????????????????????? (1byte)+  ????????????????????? (1byte)
     * + + ???????????????????????? (1byte)+  ????????????????????? (1byte)
     * + + ???????????????????????? (1byte)+  ????????????????????? (1byte)
     * + + ???????????????????????? (1byte)+  ????????????????????? (1byte)
     * + + ???????????????????????? (1byte)+  ????????????????????? (1byte)
     * ?????????
     * 1) ??????????????? 1byte ??? -------- ????????????????????????/ / ????????????????????????
     * 1 1- - 1) 00 :  ??????????????????????????????
     * 01 :  ??????????????????????????????
     * 1 1- - 2) 80 :  ????????????????????????
     * 81 :  ????????????????????????
     * ????????? 80/81  ????????????????????????????????? FF  ??????
     * 2 2 ??? 3  ????????? :  ???????????? 24H  ??????
     * 3 3 ??????????????????????????????????????????????????????????????????????????? FF ???
     *
     * @return
     */
    public static byte[] send_get_Hr() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x15, (byte) 0xED};
        return data;
    }

    //???????????????????????????
    public static byte[] get_raiseHand_cmd() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x18, (byte) 0xED};
        return data;
    }

    //????????????????????? ???BE-01-21-ED
    public static byte[] get_disturb_cmd() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x21, (byte) 0xED};
        return data;
    }

    //?????????????????????????????? ???BE+01+19+ED
    public static byte[] get_autoHeartRateAndTime_cmd() {
        byte[] data = new byte[]{(byte) 0xBE, (byte) 0x01, (byte) 0x19, (byte) 0xED};
        return data;
    }

    public int currentTemp(int temp) {
        if (temp < 0) {
            temp = Math.abs(temp) + 511;
        }
        return temp;
    }

    public byte[] sendWeatherCmd(boolean havsData, int todayWeather, int todaytempUnit, int todayhightTemp, int todaylowTemp, int todayaqi, int nextWeather, int nexttempUnit, int nexthightTemp, int nextlowTemp, int nextaqi, int afterWeather, int aftertempUnit, int afterhightTemp, int afterlowTemp, int afteryaqi) {
        todayhightTemp = currentTemp(todayhightTemp);
        todaylowTemp = currentTemp(todaylowTemp);
        nexthightTemp = currentTemp(nexthightTemp);
        nextlowTemp = currentTemp(nextlowTemp);
        afterhightTemp = currentTemp(afterhightTemp);
        afterlowTemp = currentTemp(afterlowTemp);


        long currentDay = (todayWeather << 26) | (todaytempUnit << 25) | (todayhightTemp << 15) | (todaylowTemp << 5) | (todayaqi);
        long nextDay = (nextWeather << 26) | (nexttempUnit << 25) | (nexthightTemp << 15) | (nextlowTemp << 5) | (nextaqi);
        long afterDay = (afterWeather << 26) | (aftertempUnit << 25) | (afterhightTemp << 15) | (afterlowTemp << 5) | (afteryaqi);

      /*  //(byte) ((height & 0xffff) >> 8)
        int one = (byte) currentDay >> 24 & 0xff;
        int two = (byte) currentDay >> 16 & 0xff;
        int three = (byte) currentDay >> 8 & 0xff;
        int four = (byte) currentDay & 0xff;*/

        byte[] data = new byte[]{(byte) 0xbe, 0x01, 0x26, (byte) 0xFE, (byte) (havsData ? 1 : 0), (byte) (currentDay >> 24 & 0xff), (byte) (currentDay >> 16 & 0xff), (byte) (currentDay >> 8 & 0xff), (byte) (currentDay & 0xff)
                , (byte) (nextDay >> 24 & 0xff), (byte) (nextDay >> 16 & 0xff), (byte) (nextDay >> 8 & 0xff), (byte) (nextDay & 0xff)
                , (byte) (afterDay >> 24 & 0xff), (byte) (afterDay >> 16 & 0xff), (byte) (afterDay >> 8 & 0xff), (byte) (afterDay & 0xff)};
        //return data;

        return data;

    }
}
