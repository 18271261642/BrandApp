package phone.gym.jkcq.com.commonres.common;


/**
 * 公共配置
 *
 * @author ck
 */
public class JkConfiguration {

    public static String localCity = "深圳市";

    public static String MALE = "Male";
    public static String FEMALE = "Female";


    public static String TO_FROM_LOCATION = "原生";


    public static String TYPE_FRIENDS = "type_friend";

    public static String TYPE_FLLOWER = "type_fllower";

    public static String TYPE_FANS = "type_fans";

    public static String FROM_TYPE = "from_type";
    public static String FROM_BEAN = "from_bean";

    public static final int BODY_TITLE = 100003;
    public static final int BODY_END = 100004;
    public static final int BODY_HEADER = 100005;
    public static final int BODY_DEVICE = 100006;
    public static final int BODY = 100000;
    public static final int BODY_DEVICE1 = 100007;
    public static final int BODY_DEVICE2 = 100008;
    public static final int BODY_SCALE = 100009;
    public static final int BODY_HEARTRATE = 100010;
    public static final int BODY_SLEEP = 100011;
    public static final int BODY_OXYGEN = 100012;
    public static final int BODY_BLOODPRESSURE = 100013;
    public static final int BODY_EXCERICE = 100014;
    public static final int BODY_ONCE_HR = 100015;
    public static final int BODY_TEMP = 100016;

    public static int WATCH_GOAL = 6000;
    public static int WATCH_GOAL_DISTANCE = 1000;
    public static int WATCH_GOAL_CALORIE = 10;
    public static float SLEEP_GOAL = 100.0f;
    public static float BODYFAT_GOAL = 21f;

    public static int AppType;
    public static int httpType = 0;
    public static int dbType = 1;

    public static final String COME_FROM = "from";
    public static final boolean COME_FROM_MAIN = true;


    public static String DEVICE = "device";
    public static String DEVICE_NAME = "device_name";
    public static String DEVICE_MAC = "device_mac";
    public static String DEVICE_TYPE = "device_type";
    public static String CURRENTDEVICETPE = "device";


    public interface DeviceMeasureType {
        int hr = 1;
        int blood = 2;
        int oxygen = 3;
        int temp = 4;
    }


    //dynamicInfoType:0全部,1关注,2喜欢,3作品
    public interface DynamicInfoType {
        int ALL = 0;
        int FOLLOW = 1;
        int LIKE = 2;
        int PRODUCTION = 3;
    }

    //记录当前全部中选中的userid
    public static String CurrentAllUserId = "";
    public static String CurrentAllDynamaId = "";
    //记录当前关注选中的userid
    public static String CurrentFollowUserId = "";
    public static String CurrentFollowDynamaId = "";

    public interface RopeSportType {
        int Ranking = 3;  //排行榜
        int Count = 2;  //计数训练
        int Time = 1;  //计时训练
        int Free = 0;  //自有训练
        int Challenge = 4;   //挑战
        int Course = 5;   //跳绳课程

        //当前挑战成功后分享
        int ROPE_CHALLENGE = 6;
    }

    public interface DeviceType {
        int BODYFAT = 1;
        int SLEEP = 2;
        int WATCH_W516 = 0;
        int BRAND_W311 = 3;
        int ROPE_SKIPPING = 83002;
        int BRAND_W307J = 30774;
        int Brand_W520 = 4;

        int Watch_W812 = 812;
        int Watch_W817 = 817;
        int Watch_W813 = 813;
        int Watch_W556 = 526;
        int Watch_W557 = 557;
        int Watch_W560 = 5601;
        int Watch_W560B = 560;
        int Watch_W812B = 81266;
        int Brand_W814 = 814;
        int Brand_W811 = 811;
        int Watch_W819 = 819;
        int Watch_W910 = 910;

        //F18手表
        int Watch_F18 = 7018;

        int SPORT = 10;
        int HEART_RATE = 11;
        int DFU = 1001;
        int ALL = 1002;

    }

    public interface WatchDataType {
        int STEP = 0;
        int HEARTRATE = 1;
        int SLEEP = 2;
    }

    public interface BodyType {
        String STANDARD = "标准";
        String FAT = "肥胖";
        String THIN = "偏瘦";
        String CHUBBY = "偏胖";
        String VERY_FAT = "重度";
    }

    public interface Band {
        String STEP_GOAL = "step_goal";
        String STEP_SCREEN_UP = "screen_up";
        String STEP_SCREEN_TIME = "screen_time";
    }

    public interface GymUserInfo {
        String SPORTTIMES = "sportTimes";
        String GENDER = "gender";
        String HEIGHT = "height";
        String WEIGHT = "weight";
        String NICKNAME = "nickName";
        String OCCUPATION = "occupation";
        String USERBIRTHDAY = "userBirthday";
        String MALE = "Male";
        String FEMALE = "Female";
        String OBJECTIVE = "objective";
        String UNSPECIFIED = "Unspecified ";
        String FROMSETTING = "fromSetting";
        String PHONENUM = "phoneNum";
        String USERDETAILS = "userdetails";
        String USERINTEREST = "userinterest";
        String CITY = "city";
        String FROM = "from";
        String STEP = "STEP";
        String STEP_W311 = "STEP_W311";
        String DISTANCE_W560 = "DISTANCE_W560";
        String CALORIE_W3560 = "CALORIE_W3560";
        String SCREEN_LUMINANCE = "screen_luminance";
        String BACKLIGHT_TIME = "backlight_time";
        String REMIND_TIME = "REMIND_TIME";
        String LIFT_BRACELET_TO_VIEW_INFO = "lift_bracelet_to_view_info";
        String LIFT_BRACELET_TO_VIEW_INFO_307J = "307j_lift_bracelet_to_view_info";
        String BRACELET_WEAR = "bracelet_wear";
        String TIME_FORMATE = "time_formate";
        String TIME_MIN = "time_min";
        String temperature_unitl = "temperature_unitl";
        String HR_TIMES = "hr_times";
        String w311_long_time_reminder = "w311_long_time_reminder";
        String w516_long_time_reminder = "w516_long_time_reminder";
    }


    /*public static HashMap<String, Class> FORWARDINDEXLIST = new HashMap<String, Class>() {
        {
            put(JkConfiguration.SportType.sportcode, ActivityMySports.class);
            put(JkConfiguration.SportType.devciemanage, ActivityMyDevice.class);
            put(SportType.subscribe, ActivitySportMain.class);
            put(JkConfiguration.SportType.myappointment, ActivityMyReservation.class);
            put(JkConfiguration.SportType.mywallet, ActivityWallet.class);
        }
    };*/


    public interface Configure {
        String DETAILS_RESERVATION = "Details_reservation";
        String GUIDANCENOTEURL = "GuidanceNoteUrl";
        String DELTEPOSTION = "detailsReservation";
        String POSTION = "postion";
        String CLUBID = "clubid";
        String CLUBNAME = "clubname";
        String CLUB_HISTORY_INFO = "club_history_info";
    }

    public interface requestCode {
        int bindMobilePhone = 100;
        int login = 101;
        int bindPhone = 120;
        int changePhone = 102;
        int nickName = 103;
        int personInformation = 104;
        int userBirthday = 105;
        int userHeight = 106;
        int userWeight = 107;
        int userSportsInterest = 108;
        int userOccupation = 109;
        int detailsReservation = 110;
        int head = 111;
        int typeFriend = 112;
        int changePic = 113;

    }

    public interface resultCode {
        int bindMobilePhone = 100;
        int login = 101;
        int changePhone = 102;
        int nickName = 103;
        int personInformation = 104;
        int userBirthday = 105;
        int userHeight = 106;
        int userWeight = 107;
        int userSportsInterest = 108;
        int userOccupation = 109;
        int detailsReservation = 110;
    }

    /**
     *
     */
    public interface SendPhoneMessageType {
        String MESSAGE = "message";
        String QQ = "qq";
        String WECHAT = "wechat";

    }

    /**
     * 蓝牙配置
     */
    public static boolean whetherReconnection = false;    // 是否第一次进行了蓝牙重连处理
    public static boolean bluetoothDeviceCon = false;    // 蓝牙设备连接否,初始false
    public static boolean isByUserHandCloseBlueTooth = false;    // 手动关闭蓝牙
    public static boolean isByUserHandBlueToothClick = false;  //手动点击连接蓝牙  不做重连
    public static boolean isInActivityMyDeviceDisConn = false;

    public interface BluetoothProcessing {
        String BLUE_TOOTH_NAME = "blue_tooth_name";        // 蓝牙名字
        String BLUE_TOOTH_ADDRESS = "blue_tooth_address";     // 蓝牙地址
        String BLUETOOTH_TYPE = "bluetooth_type";       //蓝牙类型
        String WEARINGWAY = "wearing_way";//佩戴方式
        String DEVICEWEARMODE = "device_wear_mode";
        String STEPDISTANCE = "step_distance";//步距
        String DEVICETARSTEP = "device_tarstep";//目标步数
        String ANTIDROPREMINDER = "antidropreminder";
        String SEDENTARYSTATUS = "sedentary_status";
        String DISPLAYSTATUS = "display_status";//显示设置
        String NOTDISTURBSTATUS = "notdisturb_status";
        String AUTOSLEEP = "auto_sleep";//自动睡眠
        String CALL_REMINDER = "call_reminder";//来电提醒
        String SMS_REMINDER = "sms_reminder";//短信提醒
        String PUSH_SMS_REMINDER = "push_sms_reminder";//推送短信提醒
        String PHOTOGRAPH = "photograph";//拍照功能
        String AIRUPGRADE = "airupgrade";//空中升级
        String MULTIMEDIACONTROL = "multimediacontrol";//多媒体控制
        String NAOZHONGINFO = "naozhonginfo";//闹钟提醒
        String TYPE_RUNNING = "type_running";//运动类型
        String FIND_DEVICE = "find_device";//查找设备
        String ANTI_LOST = "anti_lost";//防丢
        String HEARTRATETIMINGDETECTION = "heartratetimingdetection";//心率定时检测
        String TYPE_REAL_TIME_DATA = "type_real_time_data";//实时数据
        String DIS_CONN_DEVICE = "dis_conn_device";//断开连接
        String AFTER_GETTING_BASIC_INFO = "after_getting_basic_info";
        String BLUETOOTH_APP_OPEN_CONN_AGAIN = "bluetooth_app_open_conn_again";   //蓝牙是否重连


    }

    public interface HistoryType {
        int TYPE_TITLE = 0;
        int TYPE_MONTH = 1;
        int TYPE_CONTENT = 2;
        int TYPE_DIVIDER = 3;
    }

    public interface BluetoolEventInt {
        int ADD_CLOCK = 0;
        int ADD_CLOCK_BACK = 1;
        int MODIFYCLOCK = 2;
        int MODIFY_BACK = 3;
        int REPEAT_RESULT = 4;
    }

    public interface RequstType {
        int MYPERSONAL_DEVICELIST = 0;
        int FIND_DEVICE_DATA_HOME = 1;
        int SLEEP_HISTORY = 2;
        int SCALE_HISTORY = 3;
        int WRISTBAND_HISTORY = 4;
        int BIND_DEVICE = 5;
        int SLEEP_GET_CLOCK_TIME = 6;
        int FATSTEELYARD_REPORT = 7;
        int FATSTEELYARD_UPDATE = 8;
        int SLEEP_UPDATE = 9;
        int WRISTBAND_UPDATE = 10;
        int VERIFY = 11;
        int FIND_SCALE_MAIN_DATA = 12;
        int FIND_SLEEP_MAIN_DATA = 13;
        int FIND_SLEEP_HISTORY_DATA = 14;
        int BIND_DEVICE_INSERTSELECTIVE = 16;
        int SELECT_DEVICE_STATE_BYPRIMARYKEY = 15;
        int BIND_DEVICE_UPDATEBYPRIMARYKEYSELECTIVE = 17;
        int GET_BIND_DEVICELIST = 18;
        int GETSCALEHISTORYLISTDATA = 19;
        int ADD_SPORT_SUMMER = 20;
        int ADD_SPORT_DETAIL = 21;
        int GET_SPORT_LAST_ALL = 22;
        int GET_SPORT_HISTORY_SUMMAR_DATA = 23;
        int GET_SPORT_HISTORY_WEEK_SUMMAR_DATA = 220;
        int GET_SPORT_HISTORY_MONTH_SUMMAR_DATA = 221;
        int GET_SPORT_HISTORY_DATA = 24;
        int GET_SPORT_HISTORY_WEEK_DATA = 241;
        int GET_SPORT_HISTORY_MONTH_DATA = 242;
        int GET_BYPRIMARYKEY_SPORT_DATA = 25;
        int SLEEP_SET_CLOCK_TIME = 26;
        int HOME_DATA_SPORT = 27;
        int WATCH_UPDATE = 28;


        int FIND_WATCH_HISTORY_DATA = 29;
        int FIND_WATCH_14DAY_HISTORY_DATA = 30;
        int FIND_WATCH_PAGEDAY_HISTORY_DATA = 31;
        int LOGIN_BY_MOBILE = 32;//手机登录
        int LOGIN_BY_THIRD = 33;//第三方登录
        int GET_USERINFO = 34;//获取用户信息
        int EDITBASICINFO = 35;//编辑用户信息

        int BRACELET_UPDATE = 36;

        int FIND_BRACELET_14DAY_HISTORY_DATA = 37;
        int FIND_BRACELET_PAGEDAY_HISTORY_DATA = 38;
        int FIND_BRACELET_HISTORY_DATA = 39;

        //获取固件升级版本信息
        int DEVCIE_UPGRAGE_INFO = 40;

        int OXYGEN_UPGRADE = 41;
        int BLOODPRESSUREMODE_UPGRADE = 42;
        int EEXERCISE_UPGRADE = 43;
        int W81DETAIL_DATA_UPGRADE = 44;
        int BLOODPRESSURE_NUM_DATA = 45;
        int OXYGEN_NUM_DATA = 46;
        int BLOODPRESSURE_PAGE_NUM_DATA = 47;
        int OXYGEN_PAGE_NUM_DATA = 48;
        int W81_NUM_DEVICE_DETAIL_DATA = 49;
        int W81_MONTH_DEVICE_DETAIL_DATA = 50;
        int SPORTRECORD_PAGE_NUM_DATA = 51;
        int W81_TODAY_EXERCISE_ALLTIME = 52;
        int W81_TOTAL_EXERCISE_TIMES = 53;
        int DEVICE_PLAY_URL = 54;
        int GET_TODAY_WEATHER = 55;
        int GET_15DATE_WEATHER = 56;
        int GET_WEATHER = 57;
        int EMAIL_VERIFY = 58;

        int ONCE_HR_UPGRADE = 59;
        int ONCE_HR_NUM_DATA = 60;
        int ONCE_HR_PAGE_NUM_DATA = 61;


        //体温数据上传
        int TEMP_DATA_INSERT = 62;
        int TEMP_DATA_NUMB = 63;
        int TEMP_DATA_PAGE = 65;
        int GET_USER_FRIEND_RELATION = 66;
        // int TEMP

        //跳绳数据上传
        int ROPE_SUMMARY = 71;
        int ROPE_UPGRADE_DATA = 70;
        int sportDaysInMonth = 72;
        int dailySummaries = 73;
        int dailyBrief = 74;
        int rope_detail_url = 75;
        int rope_course_url = 79;
        int challeng = 76;
        int challengRecords = 77;
        int adviceList = 78;

        //查询排行
        int rope_challenge_rank =80;


    }

    public final static String strPace = "0'00\"";

    ///POST /customer/authorizedLanding
    //POST /customer/CustomerBasicInfo
    //POST /customer/customerBasicInfo/editCustomerImmage/{userId}/{interfaceId}
//POST /customer/editCustomerBasicInfo
    //POST /customer/findDeviceTypeByPeopleId
    //POST /customer/loginByMobile
    //POST /devicedatahome/findDeviceDataHome/{userId}
    //POST /wristbandstep/historyData
    //POST /fatsteelyard/historyData
    //POST /fatsteelyard/reportData/{fatSteelyardId}
    //POST /fatsteelyard/synchronizeData\
    //POST /sleepbelt/findClockTime/{mac}
    //POST /sleepbelt/historyData
    //POST /sleepbelt/uploadClockTime
    //POST /systemtime/systemTime
    //POST /verify/{mobile}/{type}
    //POST /wristbandstep/synchronizeData

    /**
     * post
     * myPersonalDevice/myPersonalDeviceList
     * 获取用户绑定的列表数据
     */

    public interface SportType {
        int sportOutRuning = 0;
        int sportIndoor = 1;
        int sportBike = 2;
        int sportWalk = 3;

    }

    public interface Url {
        String CUSTOMER = "customer";
        String EDITIMMAGE = "editImmage";
        String AUTHORIZED_LANDING = "authorizedLanding";
        String CUSTOMER_BASICINFO = "CustomerBasicInfo";
        String EDIT_CUSTOMER_IMMAGE = "editCustomerImmage";
        String EDIT_CUSTOMER_BASICINFO = "editCustomerBasicInfo";
        String FINDD_EVICE_TYPE_BY_PEOPLEID = "findDeviceTypeByPeopleId";
        String LOGIN_BY_MOBILE = "loginByMobile";


        String DEVICE_DATA_HOME = "devicedatahome";
        String FIND_DEVICE_DATA_HOME = "findDeviceDataHome";


        //POST /wristband/iphoneSportDetail/insertSelective
        String FAT_STEELYARD = "fatsteelyard";
        String FATSTEELYARD_TARGET = "fatsteelyardTarget";
        String INSERTSELECTIVE = "insertSelective";
        String FINDBYHOMEPAGE = "findByHomePage";
        String SELECTBYTIMESTAMP = "selectByTimestamp";
        String GETBASICINFO = "getBasicInfo";
        String EDITBASICINFO = "editBasicInfo";

        String HISTORY_DATA = "historyData";
        String REPORT_DATA = "reportData";
        String SYNCHRONIZ_EDATA = "synchronizeData";
        String IPHONESPORTDETAIL = "iphoneSportDetail";
        String SELECTBYIPHONESPORTID = "selectByIphoneSportId";
        //POST /wristband/iphoneSportDetail/selectByIphoneSportId
        //http://192.168.10.203:8130/isport/concumer-wristband/wristband/iphoneSport/selectByPrimaryKey?iphoneSportId=189

        String SLEEPBELT = "sleepbelt";
        String SLEEPBELTCONFIG = "sleepbeltConfig";
        String SELECTBYCONDITION = "selectByCondition";
        String DEVICE = "device";
        String BASIC = "basic";
        String DEVICEVERSION = "deviceVersion";
        String SELECTBYPRIMARYKEY = "selectByPrimaryKey";
        String SELECTBYUSERID = "selectByUserId";
        String UPDATEBYPRIMARYKEYSELECTIVE = "updateByPrimaryKeySelective";
        String SLEEPBELT_TARGET = "sleepbeltTarget";
        String FIND_CLOCK_TIME = "findClockTime";
        String UPLOAD_CLOCK_TIME = "uploadClockTime";
        //POST /wristband/iphoneSport/insertSelective
        String SPORT = "wristband";
        //POST /wristband/wristbandSportDetail/insertSelective
        String WRISTBANDSPORTDETAIL = "wristbandSportDetail";
        String INPONESPORT = "iphoneSport";
        //POST /wristband/iphoneSport/selectHomeDate
        String SELECTHOMEDATE = "selectAllMotionTime";
        String SELECTLASTALL = "selectLastAll";
        String SELECTTOTAL = "selectTotal";
        String SELECTMONTHDATA = "selectMonthData";
        String SELECTWEEKDATA = "selectWeekData";
        //POST /wristband/iphoneSport/selectDateByUserId
        String SELECTDATEBYUSERID = "selectDateByUserId";
        String SELECTWEEKDATALIST = "selectWeekDataList";
        String SELECTMONTHDATALIST = "selectMonthDataList";
        String SELECTBYNEWLY = "selectByNewly";
        String SELECTBYPAGESIZE = "selectByPageSize";

        String SYSTEMTIME = "systemtime";

        String VERIFY = "verify";

        String WRISTBAND_STEP = "wristbandstep";

        String MY_PERSONAL_DEVICE = "myPersonalDevice";
        String MY_PERSONAL_DEVICE_LIST = "myPersonalDeviceList";
        //POST /myPersonalDevice/delMyPersonalDevice

        //POST /myPersonalDevice/myPersonalDeviceBinding
        String DEL_MY_PERSONAL_DEVICE = "delMyPersonalDevice";
        String MY_PERSONAL_DEVICE_BINDING = "myPersonalDeviceBinding";


    }

    public interface BluetoolEventString {
        String ALARM_ACTION_TYPE = "alarm_action_type";//闹钟操作类型
        String INDEX_POSTION_BACK = "index_postion_back";//修改闹钟下标返回
        String INDEX_POSTION = "index_postion";//修改闹钟下标
        String SENDCLOCKFATHERBEAN_BACK = "send_clock_fatherbean_back";//添加闹钟界面返回给闹钟主页
        String SENDCLOCKFATHERBEAN = "send_clockfatherbean";//闹钟主页传给添加闹钟界面标记
        String SENDCLOCKBEAN_BACK = "send_clockbean_back";//修改闹钟界面返回给主界面
        String HEARTRATETIMINGDETECTION = "HEARTRATETIMINGDETECTION";//心率定时检测
        String SENDCLOCKBEAN = "send_clock_bean";//闹钟主页传给修改闹钟界面
        String REPEAT_RESULT = "repeat_result";
        String REPEAT_DATA = "repeat_data";
    }

    //闹钟
    public final static String DELETE_CLOCK = "DELETE_CLOCK";//删除闹钟
    public final static String MODIFY_CLOCK = "MODIFY_CLOCK";//修改闹钟

    //   public final static String sport_home_key = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + "sport_home_key";


    public static final String FRAGMENT_DATA = "fragment_data";
    public static final String FRAGMENT_SPORT = "fragment_sport";
    public static final String FRAGMENT_COMMUNITY = "fragment_community";
    public static final String FRAGMENT_MINE = "fragment_mine";
    public static final String FRAGMENT_ALL = "fragment_all";
    public static final String FRAGMENT_FOLLOW = "fragment_follow";
    public static String sCurrentCommunityFragment = "fragment_all";

    public static String sCurrentFragmentTAG = JkConfiguration.FRAGMENT_DATA;
    public static String sCurrentPersonal = FRAGMENT_DATA;
}
