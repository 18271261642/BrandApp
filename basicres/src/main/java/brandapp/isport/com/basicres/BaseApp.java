package brandapp.isport.com.basicres;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;
import com.htsmart.wristband2.WristbandApplication;
import com.isport.blelibrary.db.action.BleAction;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.Callable;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import brandapp.isport.com.basicres.action.BaseAction;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.ThreadPoolUtils;
import me.weishu.reflection.Reflection;
import phone.gym.jkcq.com.commonres.common.AllocationApi;

// 该程序退出整个系统
public class BaseApp extends MultiDexApplication {


    private static final String TAG = "BaseApp";

    public static BaseApp instance;


    // 初始化Instance；
    public synchronized void setInstance() {
        if (instance == null) {
            instance = this;
        }
    }

    public static int intBarHeight = 0;

    public static BaseApp getApp() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initApp();
        BaseAction.init(this);
        BleAction.init(this);
        initBaidu(); // 百度地图
        // 加载友盟。
        initUMeng();

        //JPushUtils.getInstance().init(this);
        Logger.d("getCurrentLogPath appPath =  JPushUtils.getInstance()");

        /**
         * 设置这两个属性就可以看到log、
         */

        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        //拓步SDK初始化
        WristbandApplication.init(this);
        WristbandApplication.setDebugEnable(true);


        //CrashReport.initCrashReport(getApplicationContext(), "f27adcbc6d", BuildConfig.DEBUG);
        // FileUtil.initFile(this);
        // LocationUtil.getInstance().init(getApp());
        //  LocationUtil.getInstance().startLocation(getApp());
        initFeedbackService();

        intBarHeight = getStatusBarHeight(getApplicationContext());
        /**
         * 开启蓝牙服务
         */
        // 初始的时候就将控制蓝牙的服务开启出来
        //  BleDeviceService.getInstance();
        //  BleManager.getInstance().init(BaseApp.getApp());
//        BleManager.getInstance().init(BaseApp.getApp());
        setSharedPreferences =
                getSharedPreferences(AllocationApi.SpStringTag.USER_SETTING, Context.MODE_PRIVATE);


    }

    private static SharedPreferences setSharedPreferences;

    public static SharedPreferences getSetSp() {
        return setSharedPreferences;
    }


    /*
     * 获取状态栏高度
     *
     * @param context
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId =
                context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        Reflection.unseal(base);
    }

//    private void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
//    }

    /*
     * 初始化友盟。
     */
    private void initUMeng() {
        // 友盟分享
        // Config.DEBUG = true;// 开启debug模式，方便定位错误
//        Config.REDIRECT_URL = "http://sns.whalecloud.com";
        // QueuedWork.isUseThreadPool = false;
        // UMShareAPI.get(this);

    }

    private void initApp() {
        setInstance();
    }

    private void initBaidu() {
        //SDKInitializer.initialize(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ThreadPoolUtils.getInstance().onTerminate();
    }

    public boolean isUSA() {
        return !isCN();
//        return getLocale().getLanguage().equals("en");
        // return false;
    }

    public boolean isES() {
        return getLocale().getLanguage().equals("es");
    }

    public boolean isCN() {
        return getLocale().getLanguage().equals("zh");
    }

    private Locale getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Log.e(TAG, "7.0==" + LocaleList.getDefault().get(0).getCountry());
            Log.e(TAG, "7.0==" + LocaleList.getDefault().get(0).getLanguage());
            return LocaleList.getDefault().get(0);
        } else {
            Log.e(TAG, "5.0 6.0==" + Locale.getDefault().getCountry());
            return Locale.getDefault();
        }
    }


    // public  UserDetails details = null;

  /*  public  UserDetails getUserDetails() {
        if(details == null){
            details = new UserDetails();
            details.setBrithday("1990-01-01");
            details.setGender("Male");
        }
        return details;
    }*/

   /* public  void setUserDetails(UserDetails details){
        this.details = details;
    }*/

    private void initFeedbackService() {
        /**
         * 添加自定义的error handler
         */
        FeedbackAPI.addErrorCallback(new FeedbackErrorCallback() {
            @Override
            public void onError(Context context, String errorMessage, ErrorCode code) {
                Toast.makeText(context, "ErrMsg is: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        FeedbackAPI.addLeaveCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("DemoApplication", "custom leave callback");
                return null;
            }
        });
        /**
         * 建议放在此处做初始化
         */
        //默认初始化
        //FeedbackAPI.init(this);
        //2d0787efb854930324e0d5a8253f0397
        //中英文配置
        if (AppUtil.isZh(getApp())) {
            FeedbackAPI.init(getApp(), "25140601", "94d0c175298ecae86b7a66783592b8eb");
        } else {
            FeedbackAPI.init(getApp(), "27769734", "ecbfe77917cc07e3f0d500975c19d0d0");
        }
        /**
         * 在Activity的onCreate中执行的代码
         * 可以设置状态栏背景颜色和图标颜色，这里使用com.githang:status-bar-compat来实现
         */
       /* FeedbackAPI.setActivityCallback(new IActivityCallback() {
            @Override
            public void onCreate(Activity activity) {
                StatusBarCompat.setStatusBarColor(activity, getResources().getColor(R.color.common_bg), true);
            }
        });*/
        /**
         * 自定义参数演示
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginTime", "登录时间");
            jsonObject.put("visitPath", "登陆，关于，反馈");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FeedbackAPI.setAppExtInfo(jsonObject);
        /**
         * 以下是设置UI
         */
        //设置默认联系方式
        //FeedbackAPI.setDefaultUserContactInfo("13800000000");
        //沉浸式任务栏，控制台设置为true之后此方法才能生效
        FeedbackAPI.setTranslucent(true);
        //设置返回按钮图标
        //FeedbackAPI.setBackIcon(R.drawable.icon_back);
        //设置标题栏"历史反馈"的字号，需要将控制台中此字号设置为0
        //FeedbackAPI.setHistoryTextSize(20);
        //设置标题栏高度，单位为像素
        //FeedbackAPI.setTitleBarHeight(100);
    }

}