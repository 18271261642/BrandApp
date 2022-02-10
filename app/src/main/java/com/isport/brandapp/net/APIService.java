package com.isport.brandapp.net;


import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.entry.WristbandWeather;
import com.isport.brandapp.device.f18.dial.F18DialBean;
import com.isport.brandapp.home.bean.AdviceBean;
import com.isport.brandapp.home.bean.http.BindDeviceList;
import com.isport.brandapp.home.bean.http.DeviceHomeData;
import com.isport.brandapp.home.bean.http.ScaleHistoryData;
import com.isport.brandapp.home.bean.http.SleepHistoryData;
import com.isport.brandapp.home.bean.http.UpdateWatchResultBean;
import com.isport.brandapp.bean.DeviceBeanList;
import com.isport.brandapp.bind.bean.ClockTimeBean;
import com.isport.brandapp.bind.bean.DeviceState;
import com.isport.brandapp.bind.bean.UpdatSleepClockTime;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.band.bean.BandHistoryList;
import com.isport.brandapp.device.bracelet.playW311.bean.PlayBean;
import com.isport.brandapp.device.scale.bean.ScaleHistroyList;
import com.isport.brandapp.device.scale.bean.ScaleHistroyNList;
import com.isport.brandapp.device.scale.bean.ScaleReportBean;
import com.isport.brandapp.device.sleep.bean.SleepHistoryBean;
import com.isport.brandapp.device.sleep.bean.SleepHistoryNList;
import com.isport.brandapp.device.sleep.bean.UpdateSleepReportBean;
import com.isport.brandapp.message.MessageCount;
import com.isport.brandapp.message.MessageInfo;
import com.isport.brandapp.upgrade.bean.DeviceUpgradeBean;

import java.util.List;
import java.util.Map;

import brandapp.isport.com.basicres.commonbean.BaseResponse;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.entry.bean.UpdatePhotoBean;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import phone.gym.jkcq.com.socialmodule.bean.ListData;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author azheng
 * @date 2018/4/24.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public interface APIService {


   /* @GET("sns/oauth2/access_token")
    Observable<LoginWXBean> getToken(@QueryMap Map<String, String> params);

    @GET("sns/userinfo")
    Observable<WXUserInfo> getUserInfo(@QueryMap Map<String, String> params);*/

    /**
     * 获取广告
     */
    @POST("advertisement/frontPageAdvertisements")
    Observable<BaseResponse<List<AdviceBean>>> getAdvertisements(
            @Query("clientType") String clientType,
            @Query("location") String location,
            @Query("userId") String userId
    );

    /**
     * 用户类操作
     *
     * @param url
     * @param maps
     * @return
     */

    @POST("customer/{url}")
    Observable<BaseResponse<UserInfoBean>> getUserInfo(
            @Path("url") String url,
            @Body RequestBody maps);


    @POST("{url1}/{url2}")
    Observable<BaseResponse> updateData(@Path("url1") String url1,
                                        @Path("url2") String url2,
                                        @Body RequestBody body);

    @POST("{url1}/{url2}/{url3}")
    Observable<BaseResponse<Integer>>
    bindDevice(@Path("url1") String url1,
               @Path("url2") String url2,
               @Path("url3") String url3,
               @Body RequestBody body);


    @POST("wristbandstep/synchronizeData")
    Observable<BaseResponse<UpdateWatchResultBean>> updateWatchData(@Body RequestBody body);


    @POST("sleepbelt/synchronizeData")
    Observable<BaseResponse<UpdateSleepReportBean>> updateSleepData(@Body RequestBody body);

    /**
     * @param userid
     * @param
     * @return
     */
    @POST("{url1}/{url2}/{userid}")
    Observable<BaseResponse<DeviceHomeData>> getDeviceDataHome(
            @Path("url1") String url1,
            @Path("url2") String url2,
            @Path("userid") String userid);


    @POST("{url1}/{url2}")
    Observable<BaseResponse<DeviceBeanList>> getMyPersonalDeviceList(@Path("url1") String url1, @Path("url2") String
            url2, @Body RequestBody body);

    @POST("{url1}/{url2}")
    Observable<BaseResponse<SleepHistoryBean>> getSleepHistory(@Path("url1") String url1, @Path("url2") String url2,
                                                               @Body RequestBody body);

    @POST("{url1}/{url2}")
    Observable<BaseResponse<ScaleHistroyList>> getScaleHistory(@Path("url1") String url1, @Path("url2") String url2,
                                                               @Body RequestBody body);

    @POST("{url1}/{url2}")
    Observable<BaseResponse<BandHistoryList>> getwristbandHistory(@Path("url1") String url1, @Path("url2") String
            url2, @Body RequestBody body);


    /**
     * @param
     * @param
     * @return
     */
    @POST("{url1}/{url2}/{mac}")
    Observable<BaseResponse<ClockTimeBean>> getSleepClockTime(
            @Path("url1") String url1,
            @Path("url2") String url2,
            @Path("mac") String mac);


    /**
     * /fatsteelyard/reportData/{fatSteelyardId}
     *
     * @param url1
     * @param url2
     * @param fatSteelyardId
     * @return
     */
    @POST("{fatsteelyard}/{reportData}/{fatSteelyardId}")
    Observable<BaseResponse<ScaleReportBean>> getScaleReport(
            @Path("fatsteelyard") String url1,
            @Path("reportData") String url2,
            @Path("fatSteelyardId") String fatSteelyardId);

    //POST /fatsteelyard/synchronizeData
    @POST("{fatsteelyard}/{synchronizeData}/{insertSelective}")
    Observable<BaseResponse<UpdateSuccessBean>> synchronizeScaleData(
            @Path("fatsteelyard") String fatsteelyard,
            @Path("synchronizeData") String synchronizeData,
            @Path("insertSelective") String insertSelective,
            @Body RequestBody body);

    //POST device/device/selectByPrimaryKey?deviceId=0
    @POST("{basic}/{device}/{selectByPrimaryKey}")
    Observable<BaseResponse<DeviceState>> selectByPrimaryKey(
            @Path("basic") String fatsteelyard,
            @Path("device") String synchronizeData,
            @Path("selectByPrimaryKey") String insertSelective,
            @Query("deviceId") String deviceId);

    //POST http://192.168.10.203:8100/device/device/selectByUserId?userId=1
    @POST("{basic}/{device}/{selectByUserId}")
    Observable<BaseResponse<BindDeviceList>> selectByUserId(
            @Path("basic") String fatsteelyard,
            @Path("device") String synchronizeData,
            @Path("selectByUserId") String selectByUserId,
            @Query("userId") String userId);

    //POST http://192.168.10.203:8100/device/device/insertSelective
    @POST("{basic}/{device}/{insertSelective}/")
    Observable<BaseResponse<Integer>> bindDeviceInsertSelective(
            @Path("basic") String fatsteelyard,
            @Path("device") String synchronizeData,
            @Path("insertSelective") String insertSelective,
            @Body RequestBody body);

    //POST http://192.168.10.203:8100/device/device/updateByPrimaryKeySelective
    @POST("{basic}/{device}/{updateByPrimaryKeySelective}")
    Observable<BaseResponse<Integer>> bindDeviceUpdateByPrimaryKeySelective(
            @Path("basic") String fatsteelyard,
            @Path("device") String synchronizeData,
            @Path("updateByPrimaryKeySelective") String updateByPrimaryKeySelective,
            @Body RequestBody body);

    //    /fatsteelyard/fatsteelyardTarget/findByHomePage
    @POST("{fatsteelyard}/{fatsteelyardTarget}/{findByHomePage}")
    Observable<BaseResponse<ScaleHistoryData>> getScaleHistoryData(
            @Path("fatsteelyard") String fatsteelyard,
            @Path("fatsteelyardTarget") String fatsteelyardTarget,
            @Path("findByHomePage") String findByHomePage,
            @Query("fatsteelyardUserId") int fatsteelyardUserId,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize);

    //    /fatsteelyard/fatsteelyardTarget/selectByTimestamp
    @POST("{fatsteelyard}/{fatsteelyardTarget}/{selectByTimestamp}")
    Observable<BaseResponse<ScaleHistroyNList>> getScaleHistoryListData(
            @Path("fatsteelyard") String fatsteelyard,
            @Path("fatsteelyardTarget") String fatsteelyardTarget,
            @Path("selectByTimestamp") String selectByTimestamp,
            @Query("userId") String userId,
            @Query("time") long time);


    @POST("{sleepbelt}/{sleepbeltTarget}/{findByHomePage}")
    Observable<BaseResponse<SleepHistoryData>> getSleepHistoryData(
            @Path("sleepbelt") String sleepbelt,
            @Path("sleepbeltTarget") String sleepbeltTarget,
            @Path("findByHomePage") String findByHomePage,
            @Query("sleepbeltUserId") int fatsteelyardUserId,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize);

    @POST("{sleepbelt}/{sleepbeltTarget}/{selectByTimestamp}")
    Observable<BaseResponse<SleepHistoryNList>> getSleepHistoryByTimeTampData(
            @Path("sleepbelt") String fatsteelyard,
            @Path("sleepbeltTarget") String sleepbeltTarget,
            @Path("selectByTimestamp") String selectByTimestamp,
            @Query("userId") int userId,
            @Query("time") long time);


    @POST("{sleepbelt}/{synchronizeData}/{insertSelective}")
    Observable<BaseResponse<UpdateSuccessBean>> synchronizeSleepData(
            @Path("sleepbelt") String fatsteelyard,
            @Path("synchronizeData") String synchronizeData,
            @Path("insertSelective") String insertSelective,
            @Body RequestBody body);

    //    post /sleepbelt/sleepbeltConfig/selectByCondition
    @POST("{sleepbelt}/{sleepbeltConfig}/{selectByCondition}")
    Observable<BaseResponse<UpdatSleepClockTime>> selectByCondition(
            @Path("sleepbelt") String fatsteelyard,
            @Path("sleepbeltConfig") String synchronizeData,
            @Path("selectByCondition") String insertSelective,
            @Query("userId") int userId,
            @Query("mac") String mac);

    @POST("{basic}/{customer}/{authorizedLanding}")
    Observable<BaseResponse<UserInfoBean>> loginByThird(
            @Path("basic") String basic,
            @Path("customer") String customer,
            @Path("authorizedLanding") String authorizedLanding,
            @Body RequestBody body);

    @POST("{basic}/{customer}/{loginByMobile}")
    Observable<BaseResponse<UserInfoBean>> loginByMobile(
            @Path("basic") String basic,
            @Path("customer") String customer,
            @Path("loginByMobile") String loginByMobile,
            @Body RequestBody body);

    @POST("{basic}/{customer}/{getBasicInfo}")
    Observable<BaseResponse<UserInfoBean>> getUserInfo(
            @Path("basic") String basic,
            @Path("customer") String customer,
            @Path("getBasicInfo") String getBasicInfo,
            @Body RequestBody body);

    @POST("{basic}/{customer}/{editBasicInfo}")
    Observable<BaseResponse<String>> editUserInfo(
            @Path("basic") String basic,
            @Path("customer") String customer,
            @Path("editBasicInfo") String editBasicInfo,
            @Body RequestBody body);

    @POST("{basic}/{verify}/{mobile}/{type}")
    Observable<BaseResponse> getVerify(
            @Path("basic") String basic,
            @Path("verify") String verify,
            @Query("mobile") String mobile,
            @Query("type") int type);

    //https://api.mini-banana.com/isport/concumer-basic/basic/verify/604503163%40qq.com/2/ch
    @POST("{basic}/{verify}/{mobile}/{type}/{ch}")
    Observable<BaseResponse> getEmailVerify(
            @Path("basic") String basic,
            @Path("verify") String verify,
            @Path("mobile") String mobile,
            @Path("type") int type,
            @Path("ch") String language);

//    {
//        "clockTime":"string",
//            "createTime":"2019-02-21T09:54:47.690Z",
//            "deviceId":"string",
//            "deviceName":"string",
//            "deviceTime":"2019-02-21T09:54:47.690Z",
//            "devicetType":0,
//            "mac":"string",
//            "sleepTarget":0,
//            "sleepbeltConfigId":0,
//            "userId":0
//    }

    // post /sleepbelt/sleepbeltConfig
    @POST("{sleepbelt}/{sleepbeltConfig}")
    Observable<BaseResponse<String>> setSleepClockTime(
            @Path("sleepbelt") String fatsteelyard,
            @Path("sleepbeltConfig") String synchronizeData,
            @Body RequestBody body);

    @POST("{fatsteelyard}/{synchronizeData}")
    Observable<BaseResponse<UpdateWatchResultBean>> synchronizeBandData(
            @Path("fatsteelyard") String fatsteelyard,
            @Path("synchronizeData") String synchronizeData,
            @Body RequestBody body);


    ///customer/customerBasicInfo/editCustomerImmage/{userId}/{interfaceId}
//     /basic/customer/editImmage
    @Multipart
    @POST("{basic}/{customer}/{editImmage}")
    Observable<BaseResponse<UpdatePhotoBean>> uploadFile(
            @Path("basic") String basic,
            @Path("customer") String customer,
            @Path("editImmage") String editImmage,
            @Query("userId") String userid,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file);


    //设备升级接口 http://192.168.10.203:8100/basic/deviceVersion?type=1
    @GET("{basic}/{deviceVersion}")
    Observable<BaseResponse<DeviceUpgradeBean>> getDeviceVersion(
            @Path("basic") String basic,
            @Path("deviceVersion") String deviceVersion,
            @Query("type") String type);

    //获取玩转手环的接口
    //https://api.mini-banana.com/isport/concumer-basic/basic/findImmageByType?deviceType=812

    @POST("basic/findImmageByType")
    Observable<BaseResponse<List<PlayBean>>> getFindImmageByType(
            @Query("deviceType") String deviceType);

    //https://api.mini-banana.com/isport/concumer-basic/basic/weather/condition?lan=1&lon=1&deviceType=1&city=1&language=1

    @POST("basic/weather/condition")
    Observable<BaseResponse<WristbandData>> getTodayWeather(
            @Query("lan") String lan,
            @Query("lon") String lon,
            @Query("deviceType") String deviceType,
            @Query("city") String city,
            @Query("language") String language);

    //https://api.mini-banana.com/isport/concumer-basic/basic/weather/forecast15Days?lan=1&lon=1&deviceType=1&city=1&language=1&dayNum=1
    @POST("basic/weather/forecast15Days")
    Observable<BaseResponse<List<WristbandForecast>>> getforecast15DaysWeather(
            @Query("lan") String lan,
            @Query("lon") String lon,
            @Query("deviceType") String deviceType,
            @Query("city") String city,
            @Query("language") String language,
            @Query("dayNum") String dayNum);

    //https://api.mini-banana.com/isport/concumer-basic/basic/weather/conditionAndForecast15Days?lan=1&lon=1&deviceType=1&city=1&language=1&dayNum=1
    @POST("basic/weather/conditionAndForecast15Days")
    Observable<BaseResponse<WristbandWeather>> conditionAndForecast15Days(
            @Query("lan") String lan,
            @Query("lon") String lon,
            @Query("deviceType") String deviceType,
            @Query("city") String city,
            @Query("language") String language,
            @Query("dayNum") String dayNum);


    //获取消息
    @POST("concumer-basic/socialNews/newsPage")
    Observable<BaseResponse<ListData<MessageInfo>>> getMessageInfo(@Body Map map);

    //获取消息
    @POST("concumer-basic/socialNews/{socialId}")
    Observable<BaseResponse<Boolean>> delSocialNew(@Path("socialId") String socialId);

    //获取消息数量
    @POST("concumer-basic/socialNews/count")
    Observable<BaseResponse<MessageCount>> getMessageCount(@Body Map map);

    //获取当前时间戳
    @GET("concumer-basic/basic/nowTime")
    Observable<BaseResponse<Long>> getNowTime();

    //https://test.api.mini-banana.com/isport/concumer-fatsteelyard/fatsteelyard/fatsteelyardTarget/monthCounts?userId=317
    //https://test.api.mini-banana.com/isport/concumer-fatsteelyard/fatsteelyard/fatsteelyardTarget?userId=317

    @GET("concumer-fatsteelyard/fatsteelyard/fatsteelyardTarget/monthCounts")
    Observable<BaseResponse<List<Long>>> fatsteelyardTarget(@Query("userId") String userId);


    //获取F18表盘市场图片
    @GET("dialPlate/queryAllDialPlate")
    Observable<BaseResponse<List<F18DialBean>>> getF18DialListData(@Query("type") int type);
}
