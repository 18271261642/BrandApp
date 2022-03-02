package com.isport.blelibrary;

import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.entry.AlarmEntry;
import com.isport.blelibrary.entry.AutoSleep;
import com.isport.blelibrary.entry.DisplaySet;
import com.isport.blelibrary.entry.NotificationMsg;
import com.isport.blelibrary.entry.SedentaryRemind;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.interfaces.CusScannResultListener;
import com.isport.blelibrary.interfaces.ScanBackListener;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.DeviceTimesUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2018/9/30
 * @Fuction
 */

public class ISportAgent extends BaseAgent {
    private static final String TAG = ISportAgent.class.getSimpleName();

    public ISportAgent() {
    }

    public static ISportAgent instance;

    public static ISportAgent getInstance() {
        if (instance == null) {
            synchronized (ISportAgent.class) {
                if (instance == null) {
                    instance = new ISportAgent();
                }
            }
        }
        return instance;
    }

    /**
     * 连接设备
     */
    public void connect(BaseDevice baseDevice, boolean cancelScan, boolean isConnectByUser) {
        connectDevice(baseDevice, cancelScan, isConnectByUser);
    }

    /**
     * 断连设备
     */
//    public void disconnect() {
//        disConnectDevice();
//    }
    public void unbind(boolean reConnect) {

        Logger.myLog("IsportAgent unbind" + reConnect);
        unbindConnectDevice(reConnect);
    }

    public void disConDevice(boolean reConnect) {
        Logger.myLog(TAG,"IsportAgent disConDevice" + reConnect);
        disConnectDevice(reConnect);
    }


    /**
     * 搜索设备
     */
    public void scanDevice(ScanBackListener scanBackListener, int deviceType, boolean isScaleWithTimeOut) {
        super.scanDevice(scanBackListener, deviceType, isScaleWithTimeOut);
    }

    public void scanDevice(CusScannResultListener scannResultListener,long timeOut){
        super.scanDevice(scannResultListener,timeOut);
    }

    @Override
    protected boolean scanW910(ScanBackListener scanBackListener) {
        return super.scanW910(scanBackListener);
    }


    /**
     * 停止搜索
     */
    public void cancelLeScan() {
        super.cancelLeScan();
    }


    public boolean isDeviceStartScan() {
        return super.isScan();
    }

    /**
     * @param requestType
     * @param requsetParams
     */
    public void requsetW311Ble(int requestType, Object... requsetParams) {

        switch (requestType) {
            case BleRequest.DEVICE_TIME_FORMAT:
                setDeviceFomat((byte) (requsetParams[0]));
                break;
            case BleRequest.device_target_step:
                setDeviceStepTarget((int) (requsetParams[0]));
                break;
            case BleRequest.device_target_distance:
                setDeviceDistanceTarget((int) (requsetParams[0]));
                break;
            case BleRequest.device_target_calorie:
                setDeviceCalorieTarget((int) (requsetParams[0]));
                break;
            case BleRequest.BRACELET_W311_SET_USERINFO:
                setUserInfo();
                break;
            case BleRequest.BRACELET_W311_SET_WEAR:
                setBraceletWear((boolean) (requsetParams[0]));
                break;


        }
    }

    public void requestW557Ble(int requestType, Object... requsetParams) {
        switch (requestType) {
            case BleRequest.QUERY_TEMP_SUB:
                getTempSub();
                break;
            case BleRequest.SET_TEMP_SUB:
                setTempSum((int) (requsetParams[0]));
                break;
        }
    }

    public void requsetW81Ble(int requestType, Object... requsetParams) {

        switch (requestType) {
            case BleRequest.GET_VERSION:
                getVersion();
                break;
            case BleRequest.QUERY_WATCH_FACE:
                queryWatchFace();
                break;
            case BleRequest.QUERY_ALAMR_LIST:
                queryAlarList();
                break;
            case BleRequest.DEVICE_MEASURE_BLOODPRE:
                w81SendMeasureBloodPressure((boolean) (requsetParams[0]));
                break;
            case BleRequest.DEVICE_MEASURE_OXYGEN:
                w81SendMeasureOxygen((boolean) (requsetParams[0]));
                break;
            case BleRequest.DEVICE_MEASURE_ONECE_HR_DATA:
                w81SendMeasureOneceHrData((boolean) requsetParams[0]);
                break;

        }
    }

    /**
     * 通用接口
     *
     * @param requestType
     * @param requsetParams todo 请求方式待优化
     */


    public void requestBle(int requestType, Object... requsetParams) {
        switch (requestType) {
            case -1:
                getW560ExerciseData((Integer) requsetParams[0]);
                break;
            case 0x01:
                testSendAppTypeMessage((int)requsetParams[0],(String)requsetParams[1],(String)requsetParams[2]);
                break;
            case BleRequest.real_hr_switch:
                getRealHrSwitch();
                break;
            case BleRequest.rope_set_maxhr:
                setMaxHrRemind((int) requsetParams[0]);
                break;
            //开始或者结束跳绳
            case BleRequest.rope_start_or_end:
                startOrEndRope((int) requsetParams[0]);
                break;
            //获取跳绳
            case BleRequest.rope_set_state:
                setRopeType((int) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4]);
                break;
            case BleRequest.rope_set_state_nopar:
                setRopeType((int) requsetParams[0]);
                break;
            //设置跳绳
            case BleRequest.rope_get_state:
                getRopeState();
                break;
            //获取睡眠数据
            case BleRequest.W307J_SLEEP_GET:
                getSleepData();
                break;
            //发送睡眠数据
            case BleRequest.W307J_SLEEP_SET:
                sendSleepData((AutoSleep) requsetParams[0]);
                break;
            case BleRequest.MEASURE_TEMP:
                starTempValue((boolean) requsetParams[0]);
                break;
            case BleRequest.W556_HR_SWITCH:
                startHRSwitch((boolean) requsetParams[0]);
                break;
            //同步当当天的数据
            case BleRequest.DEVICE_SYNC_TODAY_DATA:
                syncTodayData();
                break;
            //W566发送消息
            case BleRequest.w526_send_message:
                w81W526Message((String) requsetParams[0], (String) requsetParams[1], (int) requsetParams[2]);
                break;
            //设备屏幕亮度是时长
            case BleRequest.DEVICE_BACKLIGHT_SCREEN_TIME:
                setBacklightAndScreenLeve((int) requsetParams[0], (int) requsetParams[1]);
                break;
            //设置天气
            case BleRequest.SET_WHEATHER:
                setWeather((WristbandData) requsetParams[0], (List<WristbandForecast>) requsetParams[1], (String) requsetParams[2]);
                break;
            //设置其他信息的开关
            case BleRequest.DEVICE_OHTER_MESSAGE:
                deviceOhterMessageSwitch((boolean) requsetParams[0]);
                break;
            case BleRequest.DEVICE_SITCH_CAMERAVIEW:
                deviceSwitchCameraView();
                break;
            case BleRequest.w81_send_message:
                w81SendMessage((String) requsetParams[0], (int) requsetParams[1]);
                break;
            //表盘设置
            case BleRequest.watch_faces_setting:
                w520SetDial((int) requsetParams[0]);
                break;
            case BleRequest.bracelet_send_disturb:
                //如果是311勿扰模式
                setW311DistrubSetting((boolean) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4]);
                break;
            //设置闹钟
            case BleRequest.bracelet_w311_set_alarm:
                w311SetAlarmList((ArrayList<AlarmEntry>) requsetParams[0]);
                break;
            //w311设置来电提醒
            case BleRequest.bracelet_send_phone:
                w311SendPhone((String) requsetParams[0], (String) requsetParams[1]);
                break;
            //w311设置信息发送
            case BleRequest.bracelet_send_msg:
                w311SenMesg((NotificationMsg) (requsetParams[0]));
                break;
            //设置心率的开关
            case BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time:
                braceletOpenHr((boolean) requsetParams[0]);
                break;
            //同步蓝牙数据
            case BleRequest.bracelet_sync_data:
                braceletSyncData();
                break;
            //设置心率区间
            case BleRequest.bracelet_heartRate:
                braceletHrSetting((boolean) requsetParams[0], (int) requsetParams[1]);
                break;
            case BleRequest.bracelet_is_open_heartRate:
                braceletOpenHr((boolean) requsetParams[0]);
                break;
            //抬腕亮屏设置
            case BleRequest.bracelet_w311_raise_hand:
                braceletRaiseHand((int) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4]);
                break;
            //是否打开抬腕亮屏设置
            case BleRequest.bracelet_w311_is_open_raise_hand_307j:
                braceletIsOpenRaiseHand((Integer) requsetParams[0]);
                break;
            case BleRequest.bracelet_w311_is_open_raise_hand:
                braceletIsOpenRaiseHand((Boolean) requsetParams[0]);
                break;
            //获取久坐提醒
            case BleRequest.bracelet_w311_GET_SEDENTARY_TIME:
                braceletGetdentarytime();
                break;
            //设置久坐提醒
            case BleRequest.BRACELET_W311_SET_SEDENTARY_TIME:
                List<SedentaryRemind> list = new ArrayList<SedentaryRemind>();
                boolean isOpen = true;
                if ((int) requsetParams[0] == 0) {
                    isOpen = false;
                }
                SedentaryRemind sedentaryRemind = new SedentaryRemind(isOpen, (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4]);
                sedentaryRemind.setNoExerceseTime((int) requsetParams[0]);
                list.add(sedentaryRemind);
                braceletSetSedentaryRemind(list);
                break;
            //设置防丢提醒
            case BleRequest.bracelet_lost_remind:
                braceletLostRemind((Boolean) requsetParams[0]);
                break;
            case BleRequest.Bracelet_w311_find_bracelect:
                findBracelet();
                break;
            case BleRequest.bracelet_w311_get_display:
                getBraceletDisplay();
                break;
            case BleRequest.bracelet_w311_set_display:
                setBraceletDisplay((DisplaySet) requsetParams[0]);

                break;
            case BleRequest.Common_GetVersion:
                getDeviceVersion();
                break;
            case BleRequest.Common_GetBattery:
                getBattery();
                break;
            case BleRequest.Watch_W516_READ_STATUS:
                read_status();
                break;
            case BleRequest.Watch_W516_GENERAL:
                set_general((boolean) requsetParams[0], (boolean) requsetParams[1], (boolean) requsetParams[2], (String) requsetParams[3]);
                break;
            case BleRequest.Watch_W516_GET_GENERAL:
                get_general();
                break;
            case BleRequest.Watch_W516_SET_USER:
//                int year, int month, int day, int sex, int weight, int height, int maxHeartRate, int
//                    minHeartRate
                set_user((int) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int)
                        requsetParams[3], (int) requsetParams[4], (int) requsetParams[5], (int) requsetParams[6], (int)
                        requsetParams[7]);
                break;
            case BleRequest.Watch_W516_GET_USER:
                get_user();
                break;
            case BleRequest.Watch_W516_SET_CALENDER:
                set_calender();
                break;
            case BleRequest.Watch_W516_GET_CALENDER:
                get_calender();
                break;
            case BleRequest.Watch_W516_SET_ALARM:
                set_alarm((boolean) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4]);
                break;
            case BleRequest.Watch_W560_SET_ALARM:
                set_w560_alarm((boolean) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4], (String) requsetParams[5]);
                break;
            case BleRequest.Watch_W560_ADD_ALARM:
                add_w560_alarm((int) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (String) requsetParams[3]);
                break;
            case BleRequest.Watch_W560_DELETE_ALARM:
                delete_w560_alarm((int) requsetParams[0]);
                break;
            case BleRequest.Watch_W516_GET_ALARM:
                get_alarm();
                break;
            case BleRequest.WATCH_W516_SWITCH_MODE:
                switch_mode((boolean) requsetParams[0]);
                break;
            case BleRequest.WATCH_W516_ADJUST:
                adjust((int) requsetParams[0], (int) requsetParams[1]);
                break;
            case BleRequest.Watch_W516_SET_SLEEP_SETTING:
//                isAutoSleep,  hasNotif,  disturb,
//                        testStartTimeH,  testStartTimeM,
//                        testEndTimeH,  testEndTimeM,
//
//                        disturbStartTimeH,  disturbStartTimeM,
//                        disturbEndTimeH,  disturbEndTimeM


                set_sleep_setting((boolean) requsetParams[0], (boolean) requsetParams[1], (boolean) requsetParams[2],
                        (int)
                                requsetParams[3], (int) requsetParams[4], (int) requsetParams[5], (int)
                                requsetParams[6], (int)
                                requsetParams[7], (int) requsetParams[8], (int) requsetParams[9], (int)
                                requsetParams[10]);
                break;
            case BleRequest.Watch_W516_GET_SLEEP_SETTING:
                get_sleep_setting();
                break;
            case BleRequest.Watch_W516_SET_SEDENTARY_TIME:
                set_sedentary_time((int) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int) requsetParams[3], (int) requsetParams[4]);
                break;
            case BleRequest.Watch_W516_GET_SEDENTARY_TIME:
                get_sedentary_time();
                break;
            case BleRequest.Watch_W516_SEND_NOTIFICATION:
                send_notification((int) requsetParams[0]);
                break;
            case BleRequest.Watch_W516_SEND_NOTIFICATION_N:
                send_notificationN((String) requsetParams[0]);
                break;
            case BleRequest.WATCH_W516_TESTDATA:
                getTestData();
                break;
            case BleRequest.Watch_W516_SET_HANDLE:
                set_handle(true);
                break;
            case BleRequest.Watch_W516_GET_DAILY_RECORD:
                Logger.myLog("Watch_W516_GET_DAILY_RECORD" + (String) requsetParams[0]);
                String todayYYYYMMDD = TimeUtils.getTodayYYYYMMDD();
                String string = (String) requsetParams[0];

                SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(1, 1), false);
                //get_daily_record(1);//获取昨天数据，先解析
                get_daily_record(1);//获取昨天数据，先解析

//                if (TimeUtils.isToday(string)) {
//                    // get_daily_record(0, true);
//                    SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(1, 1), false);
//                    get_daily_record(1);//获取昨天数据，先解析
//                } else {
//                    int gapCount = TimeUtils.getGapCount(string, todayYYYYMMDD, "yyyy-MM-dd");
//                    if (gapCount > 7) {
//                        SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(8, 1), false);
//                        get_daily_record(7);//获取昨天数据，先解析
//
//                    } else {
//                        SyncProgressObservable.getInstance().sync(DeviceTimesUtil.getTime(gapCount + 1, 1), false);
//                        get_daily_record(gapCount);//获取昨天数据，先解析
//                    }
//                }
                //计算开始同步的时间

                break;
            case BleRequest.Watch_W516_CLEAR_DAILY_RECORD:
                clear_daily_record();
                break;
            case BleRequest.Watch_W516_GET_EXERCISE_DATA:
                get_exercise_data();
                break;
            case BleRequest.Watch_W516_CLEAR_EXERCISE_DATA:
                clear_exercise_data();
                break;
            case BleRequest.Watch_W516_SET_DEFAULT:
                set_default();
                break;
            case BleRequest.Watch_W516_SET_SN_FACTORY:
                set_sn_factory();
                break;
            case BleRequest.Watch_W516_SET_SN_NORMALMODE:
                set_sn_normalmode((int) requsetParams[0]);
                break;
            case BleRequest.Watch_W516_GET_SN_DATA:
                get_sn_data();
                break;
            case BleRequest.Watch_W516_SET_BELTNAME:
                set_beltname();
                break;
            case BleRequest.Watch_W516_TEST_RESET:
                test_reset();
                break;
            case BleRequest.Watch_W516_TEST_MOTORLED:
                test_motorled();
                break;
            case BleRequest.WATCH_W516_STOP_TEST_MOTORLED:
                stop_test_motorled();
                break;
            case BleRequest.Watch_W516_TEST_HANDLE:
                test_handle();
                break;
            case BleRequest.Watch_W516_TEST_DISPLAY:
                test_display();
                break;
            case BleRequest.Watch_W516_TEST_OHR:

                test_ohr();
                break;
            case BleRequest.Watch_W516_DEVICE_TO_PHONE:
                device_to_phone();
                break;
            case BleRequest.Sleep_Sleepace_setAutoCollection:
//                boolean enable, int hour, int minute, int repeat
                setAutoCollection((boolean) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2], (int)
                        requsetParams[2]);
                break;
            case BleRequest.Sleep_Sleepace_setCollectionEnable:
//                boolean enable
                setCollectionEnable((boolean) requsetParams[0]);
                break;
            case BleRequest.Sleep_Sleepace_getCollectionStatus:
                getCollectionStatus();
                break;
            case BleRequest.Sleep_Sleepace_setRealTimeEnable:
                // boolean enable
                setRealTimeEnable((boolean) requsetParams[0]);
                break;
            case BleRequest.Sleep_Sleepace_setOriginalEnable:
                // boolean enable
                setOriginalEnable((boolean) requsetParams[0]);
                break;
            case BleRequest.Sleep_Sleepace_historyDownload:
//                int startTime, int endTime, int sex
                historyDownload((int) requsetParams[0], (int) requsetParams[1], (int) requsetParams[2]);
                break;
            case BleRequest.Sleep_Sleepace_getEnvironmentalData:
                getEnvironmentalData();
                break;

            case BleRequest.READ_DEVICE_GOAL:   //读取目标类型，步数，距离，卡路里
                readW560DeviceGoalType();
                break;

            default:
                break;
        }
    }

    public void setW560DeviceMusicData(String musicName,String allTime,String currTime){
        setMusicData(musicName,allTime,currTime);
    }





    public void bindDevice(int deviceType, String mac, String deviceId, String userId, String deviceName) {
        ParseData.saveDeviceType(deviceType, mac, deviceId, userId, deviceName);
    }

    public void deleteDeviceType(int deviceType, String userId) {
        //ParseData.deleteDeviceType(deviceType, userId);
    }


}

