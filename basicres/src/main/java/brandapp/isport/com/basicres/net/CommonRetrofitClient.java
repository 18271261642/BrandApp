package brandapp.isport.com.basicres.net;


import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseErrorTransformer;
import brandapp.isport.com.basicres.commonnet.interceptor.ErrorTransformer;
import brandapp.isport.com.basicres.commonnet.interceptor.Transformer;
import brandapp.isport.com.basicres.commonnet.net.HttpLoggingInterceptor;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.bean.UpdatePhotoBean;
import brandapp.isport.com.basicres.net.userNet.UserAPIService;
import io.reactivex.Observable;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
public class CommonRetrofitClient {
    private final static int timout = 60;
    private final static int writeTimeout = 30;
//     public static String baseUrl = "https://api.mini-banana.com/";
    //test
    //private static String baseUrl = "http://192.168.1.247:8767/";//bonlala的内网测试服务器
    public static String baseUrl = "https://test.api.mini-banana.com/";//bonlala的外网测试服务器
    public static ErrorTransformer transformer = new ErrorTransformer();//返回子对象
    public static BaseErrorTransformer baseTransformer = new BaseErrorTransformer();//返回String
    public static Transformer desTransformer = new Transformer();//返回顶层的对象

    public CommonRetrofitClient() {
    }


    /**
     * 设置Header
     *
     * @return
     */
    private static Interceptor getHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        //添加Token
                        .header("token", TokenUtil.getInstance().getToken(UIUtils.getContext()));
                Request request = requestBuilder.build();

                requestBuilder.addHeader("Connection", "close");
                //获取到response
                Response response = chain.proceed(request);

                Headers headers = response.headers();

                if (!TextUtils.isEmpty(headers.get("token"))) {
                    String token = headers.get("token");
                    Logger.e("token == " + token);
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
            public Response intercept(@NonNull Chain chain) throws IOException {
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
                    Logger.e("token == " + token);
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


    private volatile static Retrofit retrofit;

    @NonNull
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (CommonRetrofitClient.class) {
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
                            .retryOnConnectionFailure(false)
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

    public Observable<UpdatePhotoBean> updateFile(File file, String userid) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "file";
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        return getRetrofit().create(UserAPIService.class).uploadFile(userid, description, body).compose
                (brandapp.isport.com.basicres.commonnet.interceptor.RxScheduler.Obs_io_main()).compose(transformer);
    }

}
