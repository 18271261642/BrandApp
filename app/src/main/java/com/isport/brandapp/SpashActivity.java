package com.isport.brandapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.managers.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.login.ShowPermissionActivity;
import com.isport.brandapp.login.ShowPrivacyDialogView;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.action.UserInformationBeanAction;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.UserInformationBean;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;

public class SpashActivity extends BaseActivity implements Runnable {

    private final long time_delayed = 1000 * 3;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                DeviceTypeUtil.clearDevcieInfo(SpashActivity.this);

                handler.removeCallbacks(SpashActivity.this);
                handler.postDelayed(SpashActivity.this, time_delayed);
            }
        }
    };


    private ShowPrivacyDialogView showPrivacyDialogView;

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_spash;
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Launcher);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void initData() {

        boolean isFirst = AppSP.getBoolean(this,AppSP.IS_FIRST_OPEN_APP,false);

        if(!isFirst){  //没有打开过
            if(showPrivacyDialogView == null)
                showPrivacyDialogView = new ShowPrivacyDialogView(this);
            showPrivacyDialogView.show();
            showPrivacyDialogView.setCancelable(false);
            showPrivacyDialogView.setOnPrivacyClickListener(new ShowPrivacyDialogView.OnPrivacyClickListener() {
                @Override
                public void onCancelClick() {
                    showPrivacyDialogView.dismiss();
                    finish();
                }

                @Override
                public void onConfirmClick() {
                    showPrivacyDialogView.dismiss();
                    AppSP.putBoolean(SpashActivity.this,AppSP.IS_FIRST_OPEN_APP,true);
                    showPermission();
                }
            });

            return;
        }
        
        DeviceTypeUtil.clearDevcieInfo(this);

        handler.removeCallbacks(SpashActivity.this);
        handler.postDelayed(SpashActivity.this, time_delayed);
    }



    private void showPermission(){
        Intent intent = new Intent(SpashActivity.this,ShowPermissionActivity.class);
        startActivityForResult(intent,0x02);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x02){
            handler.sendEmptyMessage(0x00);
        }
    }

    @Override
    protected void initEvent() {
        //MobclickAgent.onEvent(context, "0001");
    }

    @Override
    protected void initHeader() {

    }

    @Override
    public void onBackPressed() {
        if (handler != null) {
            handler.removeCallbacks(this);
        }
        super.onBackPressed();
    }

    @Override
    public void run() {
        // TODO: 2019/1/8 判断是单机版还是网络版,通过语言来判断
      /*  if (true) {
            startActivity(new Intent(this, GoogleMap.class));
        }*/

      /*  if(true) {
            Intent intent = new Intent(SpashActivity.this, InDoorSportActivity.class);
            startActivity(intent);
            return;
        }
*/
        if (App.appType() == App.httpType) {
            /**
             * 电话号码为空，需要跳转到绑定手机号页面
             */
            UserInfoBean bean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(app));

            Log.e("run", "TokenUtil.getInstance().getIsRegidit(app)" + TokenUtil.getInstance().getIsRegidit(app));

            if (bean != null && TokenUtil.getInstance().getIsRegidit(app)) {
                //缓存到本地
                String peopleId = TokenUtil.getInstance().getPeopleIdStr(app);
                Logger.myLog("peopleId == " + peopleId);
                String[] split = bean.getBirthday().split("-");

                String weight;
                if (bean.getWeight().contains(".")) {
                    String[] split1 = bean.getWeight().split("\\.");
                    weight = split1[0];
                } else {
                    weight = bean.getWeight();
                }
                int aFloat = Integer.parseInt(weight);
                //设置用户信息到SDK
                ISportAgent.getInstance().setUserInfo(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), aFloat, Float.parseFloat(bean.getHeight()),
                        bean.getGender().equals("Male") ? 1 : 0,
                        TimeUtils.getAge(bean.getBirthday()), bean.getUserId());
                ActivitySwitcher.goMainAct(this);
                initUMeng();
                finish();
            } else {
                Intent intentActivityLogin = new Intent(this, ActivityLogin.class);
                //Intent intentActivityLogin = new Intent(this, ActivityTest.class);
                //有数据从启动页过来，则取出数据并放入intent
               /* if (getIntent().getBundleExtra(AllocationApi.EXTRA_NOTICATION_BUNDLE) != null) {
                    intentActivityLogin.putExtra(AllocationApi.EXTRA_NOTICATION_BUNDLE, getIntent().getBundleExtra
                            (AllocationApi.EXTRA_NOTICATION_BUNDLE));
                }*/
                startActivity(intentActivityLogin);
                finish();
                overridePendingTransition(R.anim.anim_main_show, R.anim.anim_main_hide);
            }
        /*if (TokenUtil.getInstance().getPeopleIdStr(app).equals("1") || TextUtils.isEmpty(TokenUtil.getInstance()
        .getPeopleIdStr(app))) {

        } else {


        }*/
        } else {
            if (AppSP.getBoolean(this, AppSP.IS_FIRST, true)) {
                ActivitySwitcher.goSettingUserInfoAct(this);
                //没有设置过用户信息，去设置
            } else {
                // TODO: 2019/1/12 需要抽取出去
                //获取一次用户数据，然后存内存再到主页
                //获取用户信息
                UserInformationBean userInfoByUserId = UserInformationBeanAction.findUserInfoByUserId(Constants.defUserId);
                Log.e(TAG, "0");
                if (userInfoByUserId != null) {
                    Log.e(TAG, "1");
                    //查询数据库，有则更新
                    //设置完成，进入首页
                    AppSP.putBoolean(UIUtils.getContext(), AppSP.IS_FIRST, false);
                    //设置用户信息到SDK
                    String[] split = userInfoByUserId.getBirthday().split("-");
                    String weight = (int) userInfoByUserId.getBodyWeight() + "";
                    //设置用户信息到SDK
                    ISportAgent.getInstance().setUserInfo(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(weight), (float) userInfoByUserId.getBodyHeight(), userInfoByUserId
                            .getGender().equals("Male") ? 0 : 1, TimeUtils.getAge(userInfoByUserId.getBirthday()), Constants.defUserId);
                    //缓存到本地
                    UserInfoBean userInfoBean = new UserInfoBean();
                    userInfoBean.setBirthday(userInfoByUserId.getBirthday());
                    userInfoBean.setGender(userInfoByUserId
                            .getGender());
                    userInfoBean.setHeight(userInfoByUserId.getBodyHeight() + "");
                    userInfoBean.setWeight(userInfoByUserId.getBodyWeight() + "");
                    userInfoBean.setHeadUrlTiny(userInfoByUserId.getHeadImage_s());
                    userInfoBean.setHeadUrl(userInfoByUserId.getHeadImage());
                    userInfoBean.setUserId(Constants.defUserId);
                    AppConfiguration.saveUserInfo(userInfoBean);
                }
                ActivitySwitcher.goMainAct(this);
            }
            finish();
        }
    }


    private void initUMeng() {
       /* if (true) {
            return;
        }*/


        UMConfigure.setLogEnabled(true);
        try {
            Class<?> aClass = Class.forName("com.umeng.commonsdk.UMConfigure");
            Field[] fs = aClass.getDeclaredFields();
            for (Field f : fs) {
                Log.e("xxxxxx", "ff=" + f.getName() + "   " + f.getType().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, null, null, UMConfigure.DEVICE_TYPE_PHONE,
                null);
        // interval 单位为毫秒，如果想设定为40秒，interval应为 40*1000.
        MobclickAgent.setSessionContinueMillis(30 * 1000);//黑屏，应用后台运行超过30s启动都算一次启动
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);


        // 微信
        //    public final static String APP_ID_WX = "wx83ad7682b33e28e5";
        //    public final static String APP_SECRET_WX = "d673af9518942cd8ef8490837502c12e";
        PlatformConfig.setWeixin("wx83ad7682b33e28e5", "d673af9518942cd8ef8490837502c12e");
        // 新浪微博 2511584848 8be44eb4339235c451f978d1059c2763
        PlatformConfig.setSinaWeibo("2511584848", "8be44eb4339235c451f978d1059c2763", "http://sns.whalecloud.com");
        // QQ APP ID 1108767316
        //APP KEY bsAfYGPH8dW47RG8
        // PlatformConfig.setQQZone("1108767316", "bsAfYGPH8dW47RG8");
        PlatformConfig.setQQZone("1110159454", "Ziwl5Fje7wi3327f");
        PlatformConfig.setQQFileProvider("com.isport.brandapp.fileProvider");

       /* UMConfigure.init(this, "5bbdb11cf1f556058a0002b6", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "iSport健康管家");*/

        // 友盟分享
      /*  Config.DEBUG = true;// 开启debug模式，方便定位错误
//        Config.REDIRECT_URL = "http://sns.whalecloud.com";
        QueuedWork.isUseThreadPool = false;

        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
        UMShareAPI.get(this);*/
    }


}
