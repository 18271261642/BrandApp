package com.isport.brandapp.net;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.parm.db.DeviceDbParms;
import com.isport.brandapp.parm.db.DeviceIdParms;
import com.isport.brandapp.parm.http.HistoryParm;
import com.isport.brandapp.parm.http.ScaleParms;
import com.isport.brandapp.parm.http.SleepHistoryParms;
import com.isport.brandapp.parm.http.WatchHistoryParms;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseErrorTransformer;
import brandapp.isport.com.basicres.commonnet.interceptor.ErrorTransformer;
import brandapp.isport.com.basicres.commonnet.interceptor.RxScheduler;
import brandapp.isport.com.basicres.commonnet.interceptor.Transformer;
import brandapp.isport.com.basicres.commonnet.net.HttpLoggingInterceptor;
import brandapp.isport.com.basicres.commonnet.net.PostBody;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.bean.UpdatePhotoBean;
import brandapp.isport.com.basicres.net.CommonRetrofitClient;
import io.reactivex.Observable;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author azheng
 * @date 2018/4/17.
 * GitHub：https://github.com/RookieExaminer
 * email：wei.azheng@foxmail.com
 * description：
 */
public class RetrofitClient {

    private static final String TAG = "RetrofitClient";

    private static volatile RetrofitClient instance;
    private APIService apiService;

    private final static int timout = 60;
    private final static int writeTimeout = 30;
    // private BaseApiService apiService;
    //private String baseUrl = "http://www.wanandroid.com/";

    /**
     * 基础模块：8100/isport/concumer_basic
     * 体脂称模块：8110/isport/concumer-fatsteelyard
     * 睡眠模块：8120/isport/concumer-sleepbelt
     * 手环模块：8130/isport/concumer-wristband
     */
    private static String baseUrl = CommonRetrofitClient.baseUrl ;
    //test
    // private static String baseUrl = "http://192.168.1.247:8767/isport/";//bonlala的内网测试服务器
    //private static String baseUrl = "https://test.api.mini-banana.com/isport/";//bonlala的外网测试服务器
    //private static String baseUrl = "http://192.168.10.203:8100/";
    //    public static final String               BaseScaleUrl    = "http://192.168.10.203:8110/isport/concumer-fatsteelyard/";//体脂称
//    public static final String               BaseSleepUrl    = "http://192.168.10.203:8120/isport/concumer-sleepbelt/";//睡眠带
//    public static final String               BasicUrl        = "http://192.168.10.203:8100/isport/concumer-basic/";//基础模块
//    public static final String               BasicWatchUrl   = "http://192.168.10.203:8130/isport/concumer-wristband/";//手环模块
    public static final String BaseScaleUrl = baseUrl + "isport/concumer-fatsteelyard/";//体脂称
    public static final String BaseSleepUrl = baseUrl + "isport/concumer-sleepbelt/";//睡眠带
    public static final String BasicUrl = baseUrl + "isport/concumer-basic/";//基础模块
    public static final String BasicWatchUrl = baseUrl + "isport/concumer-wristband/";//手环模块
    public static final String RopeUrl = baseUrl + "isport/producer-rope/";//手环模块https://test.api.mini-banana.com/isport/producer-rope/ropeSport/details
    public static final String BasicW311Url = baseUrl + "isport/concumer-wristband-311/"; //w311手环模块
    public static final String BasicW81Url = baseUrl + "isport/concumer-client/";  //81系列的微服务地址
    //http://192.168.10.203:8767/concumer-basic/basic/deviceVersion?type=1
    public static final String BasicDeviceUpgrade = baseUrl + "isport/concumer-basic/"; //升级模块

    public static ErrorTransformer transformer = new ErrorTransformer();//返回子对象
    public static BaseErrorTransformer baseTransformer = new BaseErrorTransformer();//返回String
    public static Transformer desTransformer = new Transformer();//返回顶层的对象

    private RetrofitClient() {
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    /**
     * 设置Header
     *
     * @return
     */
    private static Interceptor getHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        //添加Token
                        .header("token", TokenUtil.getInstance().getToken(UIUtils.getContext()));
                requestBuilder.addHeader("Connection", "close");
                Request request = requestBuilder.build();

                //获取到response
                Response response = chain.proceed(request);

                Headers headers = response.headers();

                if (!TextUtils.isEmpty(headers.get("token"))) {
                    String token = headers.get("token");
                    Logger.myLog("token == " + token);
                    TokenUtil.getInstance().updateToken(UIUtils.getContext(), token);
                }
                if (!TextUtils.isEmpty(headers.get("Date"))) {
                    String Date = headers.get("Date");
                }
                return response;
            }
        };

    }

    private static Interceptor getHeaderInterceptorWithOutToken() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        //添加Token
                        .header("token", "");
                Request request = requestBuilder.build();

                //获取到response
                Response response = chain.proceed(request);

                Headers headers = response.headers();

                if (!TextUtils.isEmpty(headers.get("token"))) {
                    String token = headers.get("token");
                    Logger.myLog("token == " + token);
                    TokenUtil.getInstance().updateToken(UIUtils.getContext(), token);
                }
                if (!TextUtils.isEmpty(headers.get("Date"))) {
                    String Date = headers.get("Date");
                }
                return response;
            }
        };

    }

    /**
     * 设置拦截器
     *
     * @return
     */
    private static Interceptor getInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //显示日志
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }


    public APIService getApi(boolean isLogin) {
        //初始化一个client,不然retrofit会自己默认添加一个
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(timout, TimeUnit.SECONDS)
                .readTimeout(timout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                //设置Header
                .addInterceptor(getHeaderInterceptor())
                //设置拦截器
                .addInterceptor(getInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                //设置网络请求的Url地址
                .baseUrl(baseUrl)
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                //设置网络请求适配器，使其支持RxJava与RxAndroid
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        //创建—— 网络请求接口—— 实例
        apiService = create(APIService.class);
        //apiService = retrofit.create(APIService.class);
        return apiService;
    }

    private volatile static Retrofit retrofit;
    private volatile static Retrofit commonretrofit;
    private volatile static Retrofit retrofitScale;
    private volatile static Retrofit retrofitSleep;
    private volatile static Retrofit retrofitBasic;
    private volatile static Retrofit retrofitBasicWitOutToken;
    private volatile static Retrofit retrofitSport;
    private volatile static Retrofit retrofitRope;
    private volatile static Retrofit retrofitW311;
    private volatile static Retrofit retrofitW81;
    private volatile static Retrofit retrofitUpgrade;

    @NonNull
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofit = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(baseUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }
        }
        return retrofit;
    }

    @NonNull
    public static Retrofit getScaleRetrofit() {
        if (retrofitScale == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitScale == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitScale = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BaseScaleUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }
        }
        return retrofitScale;
    }

    @NonNull
    public static Retrofit getCommRetrofit() {
        if (commonretrofit == null) {
            synchronized (RetrofitClient.class) {
                if (commonretrofit == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    commonretrofit = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(baseUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }
        }
        return commonretrofit;
    }

    @NonNull
    public static Retrofit getBasicRetrofit() {
        if (retrofitBasic == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitBasic == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitBasic = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BasicUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }
        }
        return retrofitBasic;
    }

    @NonNull
    public static Retrofit getBasicRetrofitHeadWitOutToken() {
        if (retrofitBasicWitOutToken == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitBasicWitOutToken == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptorWithOutToken())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitBasicWitOutToken = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BasicUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }
        }
        return retrofitBasicWitOutToken;
    }

    @NonNull
    public static Retrofit getSportRetrofit() {
        if (retrofitSport == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitSport == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitSport = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BasicWatchUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofitSport;
    }

    @NonNull
    public static Retrofit getRopeRetrofit() {
        if (retrofitRope == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitRope == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitRope = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(RopeUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofitRope;
    }

    @NonNull
    static Retrofit getW81Retrofit() {
        if (retrofitW81 == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitW81 == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitW81 = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BasicW81Url)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofitW81;
    }

    @NonNull
    public static Retrofit getW311Retrofit() {
        if (retrofitW311 == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitW311 == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitW311 = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BasicW311Url)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofitW311;
    }

    @NonNull
    public static Retrofit getDeviceUpgrade() {
        if (retrofitUpgrade == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitUpgrade == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitUpgrade = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BasicDeviceUpgrade)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofitUpgrade;
    }

    @NonNull
    public static Retrofit getSleepRetrofit() {
        if (retrofitSleep == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitSleep == null) {
                    // 指定缓存路径,缓存大小 50Mb

                    // Cookie 持久化
                    /*ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(InitApp
                            .AppContext));*/

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            // .cookieJar(cookieJar)
                            .connectTimeout(timout, TimeUnit.SECONDS)
                            .readTimeout(timout, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true) //设置Header
                            .addInterceptor(getHeaderInterceptor())
                            //设置拦截器
                            .addInterceptor(getInterceptor());


                    retrofitSleep = new Retrofit.Builder()
                            .client(builder.build())
                            //设置网络请求的Url地址
                            .baseUrl(BaseSleepUrl)
                            //设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            //设置网络请求适配器，使其支持RxJava与RxAndroid
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
            }
        }
        return retrofitSleep;
    }

    public Observable<?> post(PostBody body, boolean isString) {
        Gson gson = new Gson();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        BaseUrl url = (BaseUrl) body.requseturl;
        if (isString) {
            switch (body.type) {
                case JkConfiguration.RequstType.LOGIN_BY_THIRD:
                    return getBasicRetrofitHeadWitOutToken().create(APIService.class).loginByThird(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(baseTransformer);
                case JkConfiguration.RequstType.GET_USERINFO:
                    return getBasicRetrofit().create(APIService.class).getUserInfo(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(baseTransformer);
                case JkConfiguration.RequstType.EDITBASICINFO:
                    return getBasicRetrofit().create(APIService.class).editUserInfo(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
                case JkConfiguration.RequstType.LOGIN_BY_MOBILE:
                    return getBasicRetrofitHeadWitOutToken().create(APIService.class).loginByMobile(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
                case JkConfiguration.RequstType.DEVICE_PLAY_URL:
                    return getBasicRetrofit().create(APIService.class).getFindImmageByType(url.extend1)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
              /*  case JkConfiguration.RequstType.GET_TODAY_WEATHER:
                    return getBasicRetrofit().create(APIService.class).getTodayWeather(url.lan, url.lon, url.dataType, url.city, url.language)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
                case JkConfiguration.RequstType.GET_15DATE_WEATHER:
                    return getBasicRetrofit().create(APIService.class).getforecast15DaysWeather(url.lan, url.lon, url.dataType, url.city, url.language, url.dataNum)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);*/
                case JkConfiguration.RequstType.GET_WEATHER:
                    return getBasicRetrofit().create(APIService.class).conditionAndForecast15Days(url.lan, url.lon, url.dataType, url.city, url.language, url.dataNum)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);

            }
        } else {
            switch (body.type) {
                case JkConfiguration.RequstType.LOGIN_BY_THIRD:
                    return getBasicRetrofitHeadWitOutToken().create(APIService.class).loginByThird(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
                case JkConfiguration.RequstType.GET_USERINFO:
                    return getBasicRetrofit().create(APIService.class).getUserInfo(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
                case JkConfiguration.RequstType.EDITBASICINFO:
                    return getBasicRetrofit().create(APIService.class).editUserInfo(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
                case JkConfiguration.RequstType.LOGIN_BY_MOBILE:
                    return getBasicRetrofitHeadWitOutToken().create(APIService.class).loginByMobile(url.url1, url.url2, url.url3, requestBody)
                            .compose(RxScheduler.Obs_io_main()).compose(transformer);
            }
        }
        return null;
    }

    public Observable<UpdatePhotoBean> updateFile(File file, String userid, String interfaceId) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "file";
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        return getBasicRetrofit().create(APIService.class).uploadFile(JkConfiguration.Url.BASIC, JkConfiguration.Url.CUSTOMER, JkConfiguration.Url.EDITIMMAGE, userid, description, body).compose
                (RxScheduler.Obs_io_main()).compose(transformer);
    }

    public Observable<?> updatepost(PostBody body) {
        Gson gson = new Gson();
        BaseUrl baseUrl = (BaseUrl) body.requseturl;

        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        return getRetrofit().create(APIService.class).updateData(baseUrl.url1, baseUrl.url2, requestBody).compose
                (RxScheduler.Obs_io_main()).compose(baseTransformer);
    }

    public Observable<?> postBracelet(PostBody body) {
        RequestBody requestBody = null;
        if (body.data != null) {
            Gson gson = new Gson();
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        BaseUrl url = (BaseUrl) body.requseturl;
        switch (body.type) {
            case JkConfiguration.RequstType.BRACELET_UPDATE:
                return getW311Retrofit().create(W311APIService.class).updateBraceletDetailData(url.url1, url.url2, url.url3, requestBody).compose(RxScheduler
                        .Obs_io_main()).compose(transformer);
        }
        return getW311Retrofit().create(W311APIService.class).updateBraceletDetailData(url.url1, url.url2, url.url3, requestBody).compose(RxScheduler
                .Obs_io_main()).compose(transformer);
    }


    public Observable<?> postTemp(PostBody body) {
        RequestBody requestBody = null;
        if (body.data != null) {
            Gson gson = new Gson();
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        BaseUrl url = (BaseUrl) body.requseturl;
        switch (body.type) {
            case JkConfiguration.RequstType.TEMP_DATA_INSERT:
                return getSportRetrofit().create(APIW526Service.class).insertTmepData(requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.TEMP_DATA_NUMB:
                return getSportRetrofit().create(APIW526Service.class).getNumTempData(url.userid, url.deviceid, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.TEMP_DATA_PAGE:
                return getSportRetrofit().create(APIW526Service.class).getPageTempData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

        }
        return null;
    }


    public Observable<?> postS002(PostBody body) {
        RequestBody requestBody = null;
        if (body.data != null) {
            Gson gson = new Gson();
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        BaseUrl url = (BaseUrl) body.requseturl;
        switch (body.type) {
            case JkConfiguration.RequstType.rope_course_url:
                return getCommRetrofit().create(APIRopeService.class).getCourseUrl(url.userid).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ROPE_UPGRADE_DATA:
                return getRopeRetrofit().create(APIRopeService.class).updateRopeSport(requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.ROPE_SUMMARY:
                return getRopeRetrofit().create(APIRopeService.class).getSummary(url.userid, url.extend1, url.extend2).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.dailySummaries:
                return getRopeRetrofit().create(APIRopeService.class).getDailySummaries(url.userid, url.extend1, url.extend2).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.sportDaysInMonth:
                return getRopeRetrofit().create(APIRopeService.class).getSportDayInMonth(url.userid, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.dailyBrief:
                return getRopeRetrofit().create(APIRopeService.class).getDailyBrief(url.userid, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.rope_detail_url:
                return getRopeRetrofit().create(APIRopeService.class).getDetailUrl().compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.challeng:
                return getRopeRetrofit().create(APIRopeService.class).getChallengeUrl(url.userid).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.challengRecords:
                return getRopeRetrofit().create(APIRopeService.class).postChallengeRecords(requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);

                //查询排行
            case JkConfiguration.RequstType.rope_challenge_rank:
                return getRopeRetrofit().create(APIRopeService.class).getRopeChallengeRank(url.extend2).compose(RxScheduler.Obs_io_main()).compose(transformer);
        }

        return null;
    }

    public Observable<?> postW526(PostBody body) {
        RequestBody requestBody = null;
        if (body.data != null) {
            Logger.myLog(TAG,"--W526--postBody="+body.toString());
            Gson gson = new Gson();
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        BaseUrl url = (BaseUrl) body.requseturl;

        switch (body.type) {
            case JkConfiguration.RequstType.EEXERCISE_UPGRADE:
                return getSportRetrofit().create(APIW526Service.class).upgradeExercise(requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.W81_TODAY_EXERCISE_ALLTIME:
                return getSportRetrofit().create(APIW526Service.class).getTodayExerciseAllTime(url.userid, url.extend1, url.deviceid).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.SPORTRECORD_PAGE_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getSportRecord(url.deviceid, url.userid, url.dataType, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.W81_TOTAL_EXERCISE_TIMES:
                return getSportRetrofit().create(APIW526Service.class).getTotalPractiseTimes(url.deviceid, url.userid, url.dataType).compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BLOODPRESSUREMODE_UPGRADE:
                return getSportRetrofit().create(APIW526Service.class).upgradeBloodPressureData(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.BLOODPRESSURE_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getNumBloodPressureData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BLOODPRESSURE_PAGE_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getPageNumBloodPressureData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.OXYGEN_UPGRADE:
                return getSportRetrofit().create(APIW526Service.class).upgradeOxygenData(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.OXYGEN_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getNumbloodOxygenData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.OXYGEN_PAGE_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getPageNumbloodOxygenData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ONCE_HR_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getNumOnceHrData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ONCE_HR_UPGRADE:
                return getSportRetrofit().create(APIW526Service.class).upgradeOnceHRDataData(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ONCE_HR_PAGE_NUM_DATA:
                return getSportRetrofit().create(APIW526Service.class).getPageNumOnceHrData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
        }
        return null;
    }

    public Observable<?> postW81(PostBody body) {


        RequestBody requestBody = null;
        if (body.data != null) {
            Gson gson = new Gson();
            Logger.myLog(TAG,"--w81--postBody="+body.toString());
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        BaseUrl url = (BaseUrl) body.requseturl;
        switch (body.type) {
            case JkConfiguration.RequstType.OXYGEN_UPGRADE:
                return getW81Retrofit().create(APIW81Service.class).upgradeOxygenData(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ONCE_HR_UPGRADE:
                return getW81Retrofit().create(APIW81Service.class).upgradeOnceHRDataData(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BLOODPRESSUREMODE_UPGRADE:
                return getW81Retrofit().create(APIW81Service.class).upgradeBloodPressureData(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);


            case JkConfiguration.RequstType.OXYGEN_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getNumbloodOxygenData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ONCE_HR_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getNumOnceHrData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.OXYGEN_PAGE_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getPageNumbloodOxygenData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.ONCE_HR_PAGE_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getPageNumOnceHrData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BLOODPRESSURE_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getNumBloodPressureData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BLOODPRESSURE_PAGE_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getPageNumBloodPressureData(url.userid, url.deviceid, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.SPORTRECORD_PAGE_NUM_DATA:
                return getW81Retrofit().create(APIW81Service.class).getSportRecord(url.deviceid, url.userid, url.dataType, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.W81_NUM_DEVICE_DETAIL_DATA:
                return getW81Retrofit().create(APIW81Service.class).getW81selectByPageSize(url.userid, url.dataNum, "1", url.dataType, url.deviceid, TimeUtils.getTodayYYYYMMDD()).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.W81_MONTH_DEVICE_DETAIL_DATA:
                return getW81Retrofit().create(APIW81Service.class).getW81MonthDetailData(url.userid, url.extend1, url.dataType, url.deviceid).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.EEXERCISE_UPGRADE:
                return getW81Retrofit().create(APIW81Service.class).upgradeExercise(requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.W81DETAIL_DATA_UPGRADE:
                return getW81Retrofit().create(APIW81Service.class).upgradeW81DeviceDetailData(requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.W81_TODAY_EXERCISE_ALLTIME:
                return getW81Retrofit().create(APIW81Service.class).getTodayExerciseAllTime(url.userid, url.extend1, url.deviceid).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.W81_TOTAL_EXERCISE_TIMES:
                return getW81Retrofit().create(APIW81Service.class).getTotalPractiseTimes(url.deviceid, url.userid, url.dataType).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.GET_F18_DEVICE_DIAL_LIST:   //

                break;
        }
        return null;
    }

    public Observable<?> post(PostBody body) {
        RequestBody requestBody = null;
        if (body.data != null) {
            Gson gson = new Gson();
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        BaseUrl url = (BaseUrl) body.requseturl;
        switch (body.type) {

            //操作指引
            case JkConfiguration.RequstType.APP_OPERATE_GUID:
                return  getBasicRetrofit().create(APIService.class).getOperateStr((Integer) url.object).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.APP_VERSION_UPDATE:
                return getBasicRetrofit().create(APIService.class).getAppVersionInfo((String) url.object).compose(RxScheduler.Obs_io_main()).compose(transformer);


            case JkConfiguration.RequstType.ADD_F18_DIAL_DOWN_COUNT:    //表盘下载次数+1
                return  getBasicRetrofit().create(APIService.class).addDialDownCount((Integer) url.object).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.GET_F18_DEVICE_DIAL_LIST:  //获取表盘

                return getBasicRetrofit().create(APIService.class).getF18DialListData((Integer) url.object).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.adviceList:
                return getBasicRetrofit().create(APIService.class).getAdvertisements("APP", "STANDBY", url.userid)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.DEVCIE_UPGRAGE_INFO:
                return getDeviceUpgrade().create(APIService.class).getDeviceVersion(url.url1, url.url2, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.HOME_DATA_SPORT:
                return getSportRetrofit().create(APISportService.class).getHomeDataSport(url.url1, url.url2, url.url3, url.userid, System.currentTimeMillis() + "").compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.GET_BYPRIMARYKEY_SPORT_DATA:
                //查询当前的运动
                return getSportRetrofit().create(APISportService.class).getSporSummarDataByPrimaryKey(url.url1, url.url2, url.url3, url.userid).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.GET_SPORT_HISTORY_SUMMAR_DATA:
                return getSportRetrofit().create(APISportService.class).getHistorySummarizingData(url.url1, url.url2, url.url3, url.userid).compose(RxScheduler.Obs_io_main()).compose(transformer);

            //周和月的历史详细数据
            case JkConfiguration.RequstType.GET_SPORT_HISTORY_WEEK_DATA:
            case JkConfiguration.RequstType.GET_SPORT_HISTORY_MONTH_DATA:
                return getSportRetrofit().create(APISportService.class).getHistoryDataWeekAndMonth(url.url1, url.url2, url.url3, url.userid, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);
            //日详细数据
            case JkConfiguration.RequstType.GET_SPORT_HISTORY_DATA:
                return getSportRetrofit().create(APISportService.class).getHistoryData(url.url1, url.url2, url.url3, url.userid, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.ADD_SPORT_SUMMER:
                return getSportRetrofit().create(APISportService.class).insertSportSummer(url.url1, url.url2, url.url3, requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.ADD_SPORT_DETAIL:
                return getSportRetrofit().create(APISportService.class).insertSportDetail(url.url1, url.url2, url.url3, requestBody).compose(RxScheduler.Obs_io_main()).compose(baseTransformer);

            case JkConfiguration.RequstType.GET_SPORT_LAST_ALL:
                return getSportRetrofit().create(APISportService.class).getLastAll(url.url1, url.url2, url.url3, url.userid).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.MYPERSONAL_DEVICELIST:
                return getRetrofit().create(APIService.class).getMyPersonalDeviceList(url.url1, url.url2,
                        requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.FIND_DEVICE_DATA_HOME:
                return getRetrofit().create(APIService.class).getDeviceDataHome(url.url1, url.url2, url.userid)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.GET_BIND_DEVICELIST:
                return getBasicRetrofit().create(APIService.class).selectByUserId(url.url1, url.url2, url.url3, url
                        .userid)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.SLEEP_HISTORY:
                return getRetrofit().create(APIService.class).getSleepHistory(url.url1, url.url2, requestBody)
                        .compose(RxScheduler.Obs_io_main()).compose(desTransformer);
            case JkConfiguration.RequstType.SCALE_HISTORY:
                return getRetrofit().create(APIService.class).getScaleHistory(url.url1, url.url2, requestBody)
                        .compose(RxScheduler.Obs_io_main()).compose(desTransformer);

            case JkConfiguration.RequstType.WRISTBAND_HISTORY:
                return getRetrofit().create(APIService.class).getwristbandHistory(url.url1, url.url2, requestBody)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.BIND_DEVICE:
                return getBasicRetrofit().create(APIService.class).bindDevice(url.url1, url.url2, url.url3, requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.SLEEP_GET_CLOCK_TIME:
                DeviceIdParms dbParm1 = (DeviceIdParms) body.dbParm;
                return getSleepRetrofit().create(APIService.class).selectByCondition(url.url1, url.url2, url.url3,
                        Integer.parseInt(url.userid),
                        dbParm1.deviceId)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.SLEEP_SET_CLOCK_TIME:
                return getSleepRetrofit().create(APIService.class).setSleepClockTime(url.url1, url.url2, requestBody)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.FATSTEELYARD_REPORT:
                return getScaleRetrofit().create(APIService.class).getScaleReport(url.url1, url.url2, url.userid).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.FATSTEELYARD_UPDATE:
                return getScaleRetrofit().create(APIService.class).synchronizeScaleData(url.url1, url.url2, url.url3,
                        requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.GETSCALEHISTORYLISTDATA:
                HistoryParm historyParm = (HistoryParm) body.data;
                return getScaleRetrofit().create(APIService.class).getScaleHistoryListData(url.url1, url.url2, url.url3,
                        historyParm.getUserId(),
                        historyParm.time).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.SELECT_DEVICE_STATE_BYPRIMARYKEY:
                DeviceDbParms dbParm = (DeviceDbParms) body.dbParm;
                Logger.myLog("查询设备状态 == " + dbParm.deviceId);
                return getBasicRetrofit().create(APIService.class).selectByPrimaryKey(url.url1, url.url2, url.url3,
                        dbParm.deviceId).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BIND_DEVICE_INSERTSELECTIVE:
                return getBasicRetrofit().create(APIService.class).bindDeviceInsertSelective(url.url1, url.url2, url
                                .url3,
                        requestBody).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.BIND_DEVICE_UPDATEBYPRIMARYKEYSELECTIVE:
                return getBasicRetrofit().create(APIService.class).bindDeviceUpdateByPrimaryKeySelective(url.url1,
                        url.url2,
                        url.url3,
                        requestBody)
                        .compose
                                (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.FIND_SCALE_MAIN_DATA:
                ScaleParms data = (ScaleParms) body.data;
                Logger.myLog("FIND_SCALE_MAIN_DATA == " + data.toString());
                return getScaleRetrofit().create(APIService.class).getScaleHistoryData(url.url1, url.url2, url.url3,
                        data.userId, data.pageNum,
                        data.pageSize).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.FIND_SLEEP_MAIN_DATA:
                ScaleParms data1 = (ScaleParms) body.data;
                Logger.myLog("FIND_SLEEP_MAIN_DATA == " + data1.toString());
                return getSleepRetrofit().create(APIService.class).getSleepHistoryData(url.url1, url.url2, url.url3,
                        data1.userId, data1.pageNum,
                        data1.pageSize).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.FIND_SLEEP_HISTORY_DATA:
                SleepHistoryParms data2 = (SleepHistoryParms) body.data;
                Logger.myLog("FIND_SLEEP_MAIN_DATA == " + data2.toString());
                return getSleepRetrofit().create(APIService.class).getSleepHistoryByTimeTampData(url.url1, url.url2,
                        url.url3,
                        data2.userId,
                        data2.time).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.FIND_WATCH_HISTORY_DATA:
                WatchHistoryParms data3 = (WatchHistoryParms) body.data;
                Logger.myLog("FIND_WATCH_HISTORY_DATA == " + data3.toString());
                return getSportRetrofit().create(APISportService.class).getWatchHistoryByTimeTampData(url.url1, url.url2,
                        url.url3,
                        data3.userId,
                        data3.time
                        , data3.dataType).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.FIND_WATCH_14DAY_HISTORY_DATA:
                WatchHistoryParms data4 = (WatchHistoryParms) body.data;
                Logger.myLog("FIND_WATCH_14DAY_HISTORY_DATA == " + data4.toString());
                return getSportRetrofit().create(APISportService.class).getWatch14DayHistoryByTimeTampData(url.url1, url.url2,
                        url.url3,
                        data4.userId,
                        data4.dayNum
                        , data4.dataType, url.extend1).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.FIND_WATCH_PAGEDAY_HISTORY_DATA:

                WatchHistoryParms data5 = (WatchHistoryParms) body.data;
                Logger.myLog("FIND_WATCH_PAGEDAY_HISTORY_DATA == " + data5.toString());
                return getSportRetrofit().create(APISportService.class).getWatchPageDayHistoryByTimeTampData(url.url1, url.url2,
                        url.url3,
                        data5.userId,
                        data5.date
                        , url.extend1, data5.dataType, data5.pageNum, data5.pageSize).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.FIND_BRACELET_PAGEDAY_HISTORY_DATA:
                //主页上面查询多少天的历史数据
                WatchHistoryParms data6 = (WatchHistoryParms) body.data;
                Logger.myLog("FIND_BRACELET_PAGEDAY_HISTORY_DATA == " + data6.toString());
                return getW311Retrofit().create(APISportService.class).getWatchPageDayHistoryByTimeTampData(url.url1, url.url2,
                        url.url3,
                        data6.userId,
                        data6.date
                        , url.extend1, data6.dataType, data6.pageNum, data6.pageSize).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.SLEEP_UPDATE:
                return getSleepRetrofit().create(APIService.class).synchronizeSleepData(url.url1, url.url2, url.url3,
                        requestBody)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.WRISTBAND_UPDATE:
                return getRetrofit().create(APIService.class).synchronizeBandData(url.url1, url.url2, requestBody)
                        .compose(RxScheduler.Obs_io_main()).compose(transformer);
            case JkConfiguration.RequstType.WATCH_UPDATE:
                Logger.myLog("WATCH_UPDATE == ");
                return getSportRetrofit().create(APISportService.class).insertSportHistoryDetail(url.url1, url.url2, url.url3, requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.VERIFY:
                return getBasicRetrofitHeadWitOutToken().create(APIService.class).getVerify(url.url1, url.url2, url.extend1, Integer.parseInt(url.userid)).compose
                        (RxScheduler.Obs_io_main()).compose(baseTransformer);
            case JkConfiguration.RequstType.EMAIL_VERIFY:
                return getBasicRetrofitHeadWitOutToken().create(APIService.class).getEmailVerify(url.url1, url.url2, url.extend1, 2, url.language).compose
                        (RxScheduler.Obs_io_main()).compose(baseTransformer);
            //w311获取最新14天的数据
            //https://api.mini-banana.com/isport/concumer-wristband-311/wristband/wristbandSportDetail/selectByNewly?userId=1&dateNum=1&dataType=1&deviceId=1
            case JkConfiguration.RequstType.FIND_BRACELET_14DAY_HISTORY_DATA:
                WatchHistoryParms data7 = (WatchHistoryParms) body.data;
                return getW311Retrofit().create(W311APIService.class).getWatch14DayHistoryByTimeTampData(url.url1, url.url2, url.url3,
                        data7.userId,
                        data7.dayNum
                        , data7.dataType, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);


            //两个请求方式一样
            case JkConfiguration.RequstType.GET_SPORT_HISTORY_MONTH_SUMMAR_DATA:
            case JkConfiguration.RequstType.GET_SPORT_HISTORY_WEEK_SUMMAR_DATA:
                return getSportRetrofit().create(APISportService.class).getSportHistorySum(url.url1, url.url2, url.url3, url.userid, url.extend1).compose(RxScheduler.Obs_io_main()).compose(transformer);

            case JkConfiguration.RequstType.FIND_BRACELET_HISTORY_DATA:
                //   return  getW311Retrofit().create(APIService.class).getwristbandHistory(url.userid,url.url2,requestBody).compose(RxScheduler.Obs_io_main()).compose(transformer);


                WatchHistoryParms dataw311 = (WatchHistoryParms) body.data;
                Logger.myLog("FIND_WATCH_HIFIND_BRACELET_HISTORY_DATASTORY_DATA == " + dataw311.toString());
                return getW311Retrofit().create(APISportService.class).getWatchHistoryByTimeTampData(url.url1, url.url2,
                        url.url3,
                        dataw311.userId,
                        dataw311.time
                        , dataw311.dataType).compose
                        (RxScheduler.Obs_io_main()).compose(transformer);
        }
        return getRetrofit().create(APIService.class).updateData(url.url1, url.url2, requestBody).compose(RxScheduler
                .Obs_io_main()).compose(transformer);
    }


    /**
     * 创建默认url的api类
     *
     * @return ApiManager
     */
 /*   public RetrofitClient createBaseApi() {
        apiService = create(BaseApiService.class);
        return this;
    }*/

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public static <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    public static <T> T createScale(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofitScale.create(service);
    }

    public static <T> T createSleep(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofitSleep.create(service);
    }


}
