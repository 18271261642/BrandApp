package com.isport.brandapp.device.history.util;

import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.App;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.bean.BindInsertOrUpdateBean;
import com.isport.brandapp.parm.db.DeviceDbParms;
import com.isport.brandapp.parm.db.DeviceHistoryParms;
import com.isport.brandapp.parm.db.DeviceTypeParms;
import com.isport.brandapp.parm.http.HistoryParm;
import com.isport.brandapp.parm.http.ScaleParms;
import com.isport.brandapp.parm.http.SleepHistoryParms;
import com.isport.brandapp.parm.http.WatchHistoryParms;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.util.RequestCode;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.net.PostBody;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.entry.bean.BaseParms;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class HistoryParmUtil {

    public static PostBody<HistoryParm, BaseUrl, DeviceHistoryParms> setHistory(long timesTampServer, String url1, String url2, int
            requestCode, long timeTamp, String
                                                                                        deviceId, String
                                                                                        currentMonth, String
                                                                                        deviceType, String mac,
                                                                                String time, String pageNum, String
                                                                                        size,
                                                                                int type) {

        InitCommonParms<HistoryParm, BaseUrl, DeviceHistoryParms> commonParms = new InitCommonParms<>();

        HistoryParm historyParm = new HistoryParm();
        historyParm.setUserId( TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        historyParm.setInterfaceId("0");
        historyParm.deviceType = deviceType;
        historyParm.pageNum = pageNum;
        historyParm.time = timesTampServer;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.FAT_STEELYARD;
        baseUrl.url2 = JkConfiguration.Url.FATSTEELYARD_TARGET;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYTIMESTAMP;

        DeviceHistoryParms dbPar = new DeviceHistoryParms();
        dbPar.requestCode = requestCode;
        dbPar.deviceId = deviceId;
        dbPar.pageSize = Integer.parseInt(pageNum);
        dbPar.currentMonth = currentMonth;
        dbPar.timeTamp = timeTamp;
        boolean isStandAlone = true;
        if ((App.appType() == App.httpType)) {
            //网络版
            if (NetUtils.hasNetwork()) {
                isStandAlone = false;
            } else {
                isStandAlone = true;
            }
        }
        return commonParms.setPostBody(isStandAlone).setParms(historyParm).setBaseUrl(baseUrl).setBaseDbParms(dbPar)
                .setType(type).getPostBody();
    }

    /**
     * 通过页码和页数请求历史数据  体脂称
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static PostBody<ScaleParms, BaseUrl, BaseDbPar> getScaleHistoryData(int pageNum, int
            pageSize, boolean show) {
        InitCommonParms<ScaleParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        ScaleParms scaleParms = new ScaleParms();
        scaleParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        Logger.myLog("getScaleHistoryData peopleId == " + scaleParms.userId);
        scaleParms.pageNum = pageNum;
        scaleParms.pageSize = pageSize;
        scaleParms.show = show;
        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.FAT_STEELYARD;
        baseUrl.url2 = JkConfiguration.Url.FATSTEELYARD_TARGET;
        baseUrl.url3 = JkConfiguration.Url.FINDBYHOMEPAGE;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(scaleParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_SCALE_MAIN_DATA).getPostBody();
    }

    /**
     * 通过页码和页数请求历史数据  睡眠带
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static PostBody<ScaleParms, BaseUrl, BaseDbPar> getSleepHistoryData(int pageNum, int
            pageSize) {
        InitCommonParms<ScaleParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        ScaleParms scaleParms = new ScaleParms();
        scaleParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        Logger.myLog("getSleepHistoryData peopleId == " + scaleParms.userId);
        scaleParms.pageNum = pageNum;
        scaleParms.pageSize = pageSize;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SLEEPBELT;
        baseUrl.url2 = JkConfiguration.Url.SLEEPBELT_TARGET;
        baseUrl.url3 = JkConfiguration.Url.FINDBYHOMEPAGE;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(scaleParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_SLEEP_MAIN_DATA).getPostBody();
    }


    /**
     * 通过每月月初0点时间戳查询当月数据集合  睡眠带
     *
     * @param time
     * @return
     */
    public static PostBody<SleepHistoryParms, BaseUrl, BaseDbPar> getSleepHistoryByTimeTampData(long time) {
        InitCommonParms<SleepHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        SleepHistoryParms sleepHistoryParms = new SleepHistoryParms();
        sleepHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        sleepHistoryParms.time = time;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SLEEPBELT;
        baseUrl.url2 = JkConfiguration.Url.SLEEPBELT_TARGET;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYTIMESTAMP;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(sleepHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_SLEEP_HISTORY_DATA).getPostBody();
    }

    /**
     * 通过每月月初0点时间戳查询当月数据集合 W516手表
     *
     * @param time
     * @return
     */
    public static PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> getWatchHistoryByTimeTampData(long time, int dataType) {
        InitCommonParms<WatchHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        WatchHistoryParms watchHistoryParms = new WatchHistoryParms();
        watchHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        watchHistoryParms.time = time;
        watchHistoryParms.dataType = dataType;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SPORT;
        baseUrl.url2 = JkConfiguration.Url.WRISTBANDSPORTDETAIL;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYTIMESTAMP;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(watchHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_WATCH_HISTORY_DATA).getPostBody();
    }

    /**
     * 通过每月月初0点时间戳查询当月数据集合 W516手表
     *
     * @param time
     * @return
     */
    public static PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> getBraceletHistoryByTimeTampData(long time, int dataType) {
        InitCommonParms<WatchHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        WatchHistoryParms watchHistoryParms = new WatchHistoryParms();
        watchHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        watchHistoryParms.time = time;
        watchHistoryParms.dataType = dataType;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SPORT;
        baseUrl.url2 = JkConfiguration.Url.WRISTBANDSPORTDETAIL;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYTIMESTAMP;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(watchHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_BRACELET_HISTORY_DATA).getPostBody();
    }

    /**
     * 查询指定天数的数据 W516手表
     *
     * @return
     */
    public static PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> get14DayWatchHistoryByTimeTampData(int dayNum, int dataType, String deviceId) {
        InitCommonParms<WatchHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        WatchHistoryParms watchHistoryParms = new WatchHistoryParms();
        watchHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        watchHistoryParms.dayNum = dayNum;
        watchHistoryParms.dataType = dataType;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SPORT;
        baseUrl.url2 = JkConfiguration.Url.WRISTBANDSPORTDETAIL;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYNEWLY;
        baseUrl.extend1 = deviceId;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(watchHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_WATCH_14DAY_HISTORY_DATA).getPostBody();
    }

    /**
     * 查询指定天数的数据 W311手表
     *
     * @return
     */
    public static PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> get14DayBraceletHistoryByTimeTampData(int dayNum, int dataType, String deviceId) {
        InitCommonParms<WatchHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        WatchHistoryParms watchHistoryParms = new WatchHistoryParms();
        watchHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        watchHistoryParms.dayNum = dayNum;
        watchHistoryParms.dataType = dataType;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SPORT;
        baseUrl.url2 = JkConfiguration.Url.WRISTBANDSPORTDETAIL;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYNEWLY;
        baseUrl.extend1 = deviceId;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(watchHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_BRACELET_14DAY_HISTORY_DATA).getPostBody();
    }

    /**
     * 查询分页的数据 W516手表
     *
     * @return
     */
    public static PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> getPageWatchHistoryByTimeTampData(String date, int pageNum, int pageSize, int dataType, String deviceId) {
        InitCommonParms<WatchHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        WatchHistoryParms watchHistoryParms = new WatchHistoryParms();
        watchHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        watchHistoryParms.date = date;
        watchHistoryParms.pageNum = pageNum;
        watchHistoryParms.pageSize = pageSize;
        watchHistoryParms.dataType = dataType;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SPORT;
        baseUrl.url2 = JkConfiguration.Url.WRISTBANDSPORTDETAIL;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYPAGESIZE;
        baseUrl.extend1 = deviceId;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(watchHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_WATCH_PAGEDAY_HISTORY_DATA).getPostBody();
    }

    /**
     * 查询分页的数据 W311手表
     *
     * @return
     */
    public static PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> getPageBraceletHistoryByTimeTampData(String date, int pageNum, int pageSize, int dataType, String devcieId) {
        InitCommonParms<WatchHistoryParms, BaseUrl, BaseDbPar> commonParms = new InitCommonParms<>();

        WatchHistoryParms watchHistoryParms = new WatchHistoryParms();
        watchHistoryParms.userId = Integer.parseInt(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        watchHistoryParms.date = date;
        watchHistoryParms.pageNum = pageNum;
        watchHistoryParms.pageSize = pageSize;
        watchHistoryParms.dataType = dataType;

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.SPORT;
        baseUrl.url2 = JkConfiguration.Url.WRISTBANDSPORTDETAIL;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYPAGESIZE;
        baseUrl.extend1 = devcieId;

        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(watchHistoryParms).setBaseUrl(baseUrl)
                .setType(JkConfiguration.RequstType.FIND_BRACELET_PAGEDAY_HISTORY_DATA).getPostBody();
    }

    public static PostBody<BindInsertOrUpdateBean, BaseUrl, DeviceTypeParms> setDevice(DeviceBean deviceBean) {

        InitCommonParms<BindInsertOrUpdateBean, BaseUrl, DeviceTypeParms> commonParms = new InitCommonParms<>();

        BindInsertOrUpdateBean bindInsertOrUpdateBean = new BindInsertOrUpdateBean();
        bindInsertOrUpdateBean.setCreateTime(System.currentTimeMillis());
        String deviceId = null;

        //Logger.myLog("deviceBean.deviceType:" + deviceBean.deviceType + "deviceBean.deviceID:" + deviceBean.deviceID + "deviceBean.mac," + deviceBean.mac);
        deviceId = setDeviceId(deviceBean.deviceType, deviceBean.deviceID, deviceBean.mac);

        /*switch (deviceBean.deviceType) {
            case JkConfiguration.DeviceType.BODYFAT:
                deviceId = Utils.replaceDeviceMacUpperCase(deviceBean.deviceID);
                break;
            case JkConfiguration.DeviceType.WATCH_W516:
            case JkConfiguration.DeviceType.SLEEP:
            case JkConfiguration.DeviceType.BRAND_W311:
            case JkConfiguration.DeviceType.Brand_W520:
                deviceId = deviceBean.deviceID;
                break;
            case JkConfiguration.DeviceType.Brand_W812:
            case JkConfiguration.DeviceType.Brand_W813:
            case JkConfiguration.DeviceType.Watch_W814:
                deviceId = DeviceTypeUtil.getW81DeviceName(deviceBean.deviceID, deviceBean.mac);
                break;

            default:
                break;
        }*/
        bindInsertOrUpdateBean.setDeviceId(deviceId);
        bindInsertOrUpdateBean.setDevicetName(deviceBean.deviceName);
        bindInsertOrUpdateBean.setDeviceTypeId(deviceBean.deviceType);
        bindInsertOrUpdateBean.setUserId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));//解绑将其设置为0

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.BASIC;
        baseUrl.url2 = JkConfiguration.Url.DEVICE;
        baseUrl.url3 = "untyingDevice";
        int type = JkConfiguration.RequstType.BIND_DEVICE_UPDATEBYPRIMARYKEYSELECTIVE;
        DeviceTypeParms dbPar = new DeviceTypeParms();
        dbPar.deviceType = deviceBean.deviceType;
        dbPar.requestCode = RequestCode.Request_unBindDevice;
        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(bindInsertOrUpdateBean).setBaseUrl(baseUrl)
                .setBaseDbParms(dbPar).setType(type)
                .getPostBody();
    }


    public static String setDeviceId(int currentType, String name, String mac) {
        String deviceId = null;
        switch (currentType) {
            case JkConfiguration.DeviceType.BODYFAT:
                deviceId = Utils.replaceDeviceMacUpperCase(mac);
                break;
            default:
                deviceId = name;
                break;
        }
        return deviceId;
    }

    public static PostBody<BindInsertOrUpdateBean, BaseUrl, DeviceDbParms> setDevice(BaseDevice
                                                                                             baseDevice) {
        InitCommonParms<BindInsertOrUpdateBean, BaseUrl, DeviceDbParms> commonParms = new InitCommonParms<>();

        BindInsertOrUpdateBean bindInsertOrUpdateBean = new BindInsertOrUpdateBean();
        bindInsertOrUpdateBean.setCreateTime(System.currentTimeMillis());

        DeviceDbParms dbPar = new DeviceDbParms();
        dbPar.requestCode = RequestCode.Request_bindDevice;
        dbPar.deviceType = baseDevice.deviceType;
        dbPar.deviceName = baseDevice.deviceName;
        dbPar.deviceMac = baseDevice.address;
        dbPar.userId = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp());

        String deviceId = null;
        deviceId = setDeviceId(baseDevice.deviceType, baseDevice.deviceName, baseDevice.address);
        dbPar.deviceId = deviceId;
        bindInsertOrUpdateBean.setDeviceId(deviceId);
        bindInsertOrUpdateBean.setDevicetName(baseDevice.deviceName);
        bindInsertOrUpdateBean.setDeviceTypeId(baseDevice.deviceType);
        bindInsertOrUpdateBean.setUserId(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.BASIC;
        baseUrl.url2 = JkConfiguration.Url.DEVICE;
        baseUrl.url3 = "bindingDevice";
        int type = JkConfiguration.RequstType.BIND_DEVICE;
        //判断是更新还是插入
       /* int type = 0;
        switch (stateHasbindCanbind) {
            case ScanBaseView.STATE_NOBIND:
                //可以直接插入
                baseUrl.url3 = JkConfiguration.Url.INSERTSELECTIVE;
                type = JkConfiguration.RequstType.BIND_DEVICE_INSERTSELECTIVE;
                break;
            case ScanBaseView.STATE_HASBIND_CANBIND:
                //需要更新
                baseUrl.url3 = JkConfiguration.Url.UPDATEBYPRIMARYKEYSELECTIVE;
                type = JkConfiguration.RequstType.BIND_DEVICE_UPDATEBYPRIMARYKEYSELECTIVE;
                break;
            default:
                break;
        }*/
        return commonParms.setPostBody(!(App.appType() == App.httpType)).setParms(bindInsertOrUpdateBean).setBaseDbParms(dbPar).setBaseUrl(baseUrl)
                .setType(type).getPostBody();
    }

    /**
     * 查询设备状态
     *
     * @param baseDevice
     * @return
     */
    public static PostBody<BaseParms, BaseUrl, DeviceDbParms> selectDevice(BaseDevice baseDevice) {

        InitCommonParms<BaseParms, BaseUrl, DeviceDbParms> commonParms = new InitCommonParms<>();
        BaseUrl baseUrl = new BaseUrl();
        baseUrl.url1 = JkConfiguration.Url.BASIC;
        baseUrl.url2 = JkConfiguration.Url.DEVICE;
        baseUrl.url3 = JkConfiguration.Url.SELECTBYPRIMARYKEY;
        DeviceDbParms dbPar = new DeviceDbParms();
        dbPar.requestCode = RequestCode.Request_bindDevice;
        dbPar.deviceType = baseDevice.deviceType;
        dbPar.deviceName = baseDevice.deviceName;
        dbPar.deviceMac = baseDevice.address;
        dbPar.userId = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp());
        String deviceId = null;
        deviceId = setDeviceId(baseDevice.deviceType, baseDevice.deviceName, baseDevice.address);
        dbPar.deviceId = deviceId;
        /*switch (baseDevice.deviceType) {

            case JkConfiguration.DeviceType.BODYFAT:
                //体脂称
                dbPar.deviceId = Utils.replaceDeviceMacUpperCase(baseDevice.address);
                break;
            case JkConfiguration.DeviceType.WATCH_W516:
            case JkConfiguration.DeviceType.SLEEP:
            case JkConfiguration.DeviceType.BRAND_W311:
            case JkConfiguration.DeviceType.Brand_W520:
                dbPar.deviceId = baseDevice.deviceName;
                break;
            case JkConfiguration.DeviceType.Brand_W813:
            case JkConfiguration.DeviceType.Brand_W812:
            case JkConfiguration.DeviceType.Watch_W814:
                dbPar.deviceId = DeviceTypeUtil.getW81DeviceName(baseDevice.deviceName, baseDevice.address);
                break;
            case JkConfiguration.DeviceType.SPORT:
                break;
            default:
                break;
        }*/
        return commonParms.setPostBody(!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setBaseDbParms(dbPar)
                .setType(JkConfiguration.RequstType.SELECT_DEVICE_STATE_BYPRIMARYKEY).getPostBody();
    }
}
