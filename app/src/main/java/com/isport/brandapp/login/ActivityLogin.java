package com.isport.brandapp.login;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.DeviceTypeTableAction;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.device.share.PackageUtil;
import com.isport.brandapp.dialog.PriDialog;
import com.isport.brandapp.home.MainActivity;
import com.isport.brandapp.login.presenter.LoginPresenter;
import com.isport.brandapp.login.view.LoginBaseView;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.view.TimerView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.BaseBean;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import phone.gym.jkcq.com.commonres.common.AllocationApi;
import phone.gym.jkcq.com.commonres.commonutil.AnimationUtil;

/**
 * 登录页面
 * Created by Administrator on 2017/10/16.
 */
@Route(path = "/main/LoginActivity")
public class ActivityLogin extends BaseMVPActivity<LoginBaseView, LoginPresenter> implements LoginBaseView, View
        .OnClickListener {

    private TimerView timer, timerEmail;
    private EditText etPhone, etEmail;
    private TextView btnLogin;
    private EditText etCode;
    private ImageButton btnLoginWechat, btnLoginqq;
    private CheckBox checkBox;
    private TextView tvProtol, tvPrimary;
    private Integer RC_SIGN_IN = 9001;

    private FrameLayout faceBookLayout;
    private LoginButton loginFacebook;
    private ImageButton loginTwitter;
    private CallbackManager mCallbackManager;

    private RadioButton tvRadioPhone, tvRadioEmail;
    //private ImageView ivPhone, ivCode;
    private TextView tvPhoneTips;
    private RadioGroup radiogroup;
    private TextView tv_email_tab;

    PriDialog priDialog;


    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected boolean setStatusBarPadding() {
        return false;
    }

    @Override
    protected boolean setStatusBarStyle() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_login;
    }

    @Override
    protected void initView(View view) {

        faceBookLayout = findViewById(R.id.faceBookLayout);
        AppSP.putString(context, AppSP.WATCH_MAC, "");
        timer = findViewById(R.id.timer);
        tv_email_tab = findViewById(R.id.tv_email_tab);
        radiogroup = findViewById(R.id.radiogroup);
        timerEmail = findViewById(R.id.timer_email);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        btnLogin = findViewById(R.id.btn_login);
        etCode = findViewById(R.id.et_code);

        tvPhoneTips = findViewById(R.id.layout_login_tips);

        btnLoginWechat = findViewById(R.id.login_weixin);
        btnLoginqq = findViewById(R.id.login_qq);
        loginFacebook = findViewById(R.id.login_facebook);
        loginTwitter = findViewById(R.id.login_twitter);
        tvProtol = findViewById(R.id.tv_pro);
        tvPrimary = findViewById(R.id.privacy_agreement);
        checkBox = findViewById(R.id.check_open);
        tvRadioPhone = findViewById(R.id.tv_phone);
        tvRadioEmail = findViewById(R.id.tv_home_email);

        loginFacebook.setLoginText("");
        loginFacebook.setLogoutText("");


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.setChecked(isChecked);
            }
        });

        if (App.getApp().isUSA()) {
            faceBookLayout.setVisibility(View.VISIBLE);
            btnLoginWechat.setVisibility(View.GONE);
            btnLoginqq.setVisibility(View.GONE);
            tvRadioEmail.setChecked(true);
            tv_email_tab.setVisibility(View.VISIBLE);
            showEmailView();
            radiogroup.setVisibility(View.GONE);
            loginFacebook.setVisibility(View.VISIBLE);
            loginTwitter.setVisibility(View.VISIBLE);
            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Logger.myLog(TAG,"-----loginResult="+loginResult.getAccessToken());
                    Profile currentProfile = Profile.getCurrentProfile();
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    /*if (!accessToken.getPermissions().isEmpty()) {
                        Set<String> perssions = AccessToken.getCurrentAccessToken().getPermissions();
                        ArrayList list = new ArrayList(perssions);
                        Logger.myLog(list.toString());
                    }*/
                    if (currentProfile != null & accessToken != null) {
                        Uri uri = currentProfile.getProfilePictureUri(180, 180);
                        String url = "";
                        if (uri != null) {
                            //Logger.myLog("currentProfile == " + uri.toString() + "uri:------" + uri.getEncodedPath());
                            // Logger.myLog("currentProfile == " + currentProfile.getLinkUri().getEncodedPath());
                            url = uri.getPath();
                        }
                        Logger.myLog("currentProfile == " + url);
                        //setFacebookData(loginResult);
                        mActPresenter.thirdPartyLogin(3, accessToken.getUserId(), currentProfile.getName(), url);
                    } else {
                        setFacebookData(loginResult);
                        Logger.myLog("currentProfile == null");
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Logger.myLog(TAG,"------error="+error.getLocalizedMessage());
                }
            });
        } else {
            tv_email_tab.setVisibility(View.GONE);
            showPhoneView();
            tvRadioPhone.setChecked(true);
            btnLoginWechat.setVisibility(View.VISIBLE);
            btnLoginqq.setVisibility(View.VISIBLE);
            loginFacebook.setVisibility(View.GONE);
            loginTwitter.setVisibility(View.GONE);
            faceBookLayout.setVisibility(View.GONE);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.WeChat_NULL:
                Logger.myLog("WeChat_NULL");
                loginByWeChat();
                break;
            default:
                break;
        }
    }

    GoogleSignInAccount account;

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        Logger.myLog("onStart:account" + account);
    }

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void initData() {
        String name = ActivityLogin.class.getSimpleName();
        Logger.myLog("name:" + name);
        ISportAgent.getInstance().clearCurrentDevice();
        // getHash();


        if (!TokenUtil.getInstance().getAppFirst(BaseApp.getApp())) {
            priDialog = new PriDialog(this, new PriDialog.OnTypeClickListenter() {
                @Override
                public void changeDevcieonClick(int type) {

                    switch (type) {
                        case 0:
                            finish();
                            break;
                        case 1:
                            TokenUtil.getInstance().updateAPPfirst(BaseApp.getApp(), "true");
                            //requestPermission();

                            break;
                        case 2:
                            TokenUtil.getInstance().updateAPPfirst(BaseApp.getApp(), "true");
                            //requestPermission();
                            startPrivacyActivity();
                            break;
                        case 3:
                            TokenUtil.getInstance().updateAPPfirst(BaseApp.getApp(), "true");
                            //requestPermission();

                            startAgreementActivity();
                            break;
                    }
                }
            });
        } else {

            //requestPermission();
        }

        SyncCacheUtils.clearSetting(this);
        SyncCacheUtils.clearSysData(this);
        SyncCacheUtils.clearSysData(BaseApp.getApp());

        DeviceTypeUtil.clearDevcieInfo(this);
        DeviceTypeTableAction.deleteAllDevices();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void initEvent() {
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s)) {
                    btnLogin.setEnabled(false);
                } else {
                    if (TextUtils.isEmpty(etPhone.getText().toString()) && TextUtils.isEmpty(etEmail.getText().toString())) {
                        btnLogin.setEnabled(false);
                    } else {
                        btnLogin.setEnabled(true);
                    }

                }
                // btnLogin.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (TextUtils.isEmpty(s)) {
                    btnLogin.setEnabled(false);
                } else {
                    if (TextUtils.isEmpty(etCode.getText().toString())) {
                        btnLogin.setEnabled(false);
                    } else {
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (TextUtils.isEmpty(s)) {
                    btnLogin.setEnabled(false);
                } else {
                    if (TextUtils.isEmpty(etCode.getText().toString())) {
                        btnLogin.setEnabled(false);
                    } else {
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvRadioEmail.setOnClickListener(this);
        tvRadioPhone.setOnClickListener(this);
        timer.setOnClickListener(this);
        timerEmail.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLoginqq.setOnClickListener(this);
        btnLoginWechat.setOnClickListener(this);
        loginFacebook.setOnClickListener(this);
        loginTwitter.setOnClickListener(this);
        tvProtol.setOnClickListener(this);
        tvPrimary.setOnClickListener(this);
    }

    @Override
    protected void initHeader() {
        //  StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
    }

    @Override
    public void loginSuccess(UserInfoBean loginBean) {
        //  MobclickAgent.onEvent(context, "0002");
//        showToast(loginBean.getMessage());

        if (null != loginBean) {


            /*if (true) {
                Intent activityBindMobilePhone = new Intent(this, ActivityBindMobilePhone.class);
                startActivity(activityBindMobilePhone);
                return;
            }*/


            // isRegidit:false; 非首次登录。isRegidit:true
            if (!loginBean.isIsRegidit()) {
                TokenUtil.getInstance().updateIsRegidit(app, "true");
                Intent activityBindMobilePhone = new Intent(this, ActivitySettingUserInfo.class);
                activityBindMobilePhone.putExtra("USER_NICKNAME", etPhone.getText().toString());
                startActivity(activityBindMobilePhone);
            } else {
                TokenUtil.getInstance().updateIsRegidit(app, "true");
                startActivity(new Intent(context, MainActivity.class));
                ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
            }
        }
    }

    @Override
    public void getVerCodeSuccess(BaseBean baseBean) {
        showToast(baseBean.getMessage());
        if (tvRadioPhone.isChecked()) {
            timer.startTimer();
        } else {
            timerEmail.startTimer();
        }

    }

    @Override
    public void getVerCodeSuccess(String baseBean) {
        showToast(baseBean);
        if (tvRadioPhone.isChecked()) {
            timer.startTimer();
        } else {
            timerEmail.startTimer();
        }
    }

    @Override
    public void getVerCodeNotPass(BaseBean baseBean) {
        showToast(baseBean.getMessage());
        if (tvRadioPhone.isChecked()) {
            timer.startTimer();
        } else {
            timerEmail.startTimer();
        }
    }

    @Override
    public void thirdPartyLoginSuccess(UserInfoBean thirdLoginBean, int platformType) {
//        showToast(thirdLoginBean.getMessage());
        if (null != thirdLoginBean) {
//            showToast(thirdLoginBean.getMessage());
            // platformType://平台 1.微信登陆 2.QQ登陆,根据登录返回isRegidit（boolean类型） 用户第一次登录： isRegidit:false;
            // 非首次登录。isRegidit:true;

            if (thirdLoginBean.isIsRegidit()) {//TODO 非首次登陆的操作
                //google 5,facebook 3

                if (platformType == 5 || platformType == 3) {
                    TokenUtil.getInstance().updateIsRegidit(app, "true");
                    startActivity(new Intent(context, MainActivity.class));
                    ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
                } else {
                    if (TextUtils.isEmpty(thirdLoginBean.getMobile())) {
                        Intent activityBindMobilePhone = new Intent(this, ActivityBindMobilePhone.class);
                        startActivity(activityBindMobilePhone);
                    } else {
                        TokenUtil.getInstance().updateIsRegidit(app, "true");
                        startActivity(new Intent(context, MainActivity.class));
                        ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
                    }
                }
            } else {//TODO 首次登陆的操作
                Intent activityBindMobilePhone = new Intent(this, ActivityBindMobilePhone.class);
                startActivity(activityBindMobilePhone);
            }
        }

    }

    @Override
    public void loginBackCode(int code) {
        if (code == 1003) {
        }
    }


    @Override
    public void onClick(View v) {
        if (ViewMultiClickUtil.onMultiClick(v)) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_phone:

                showPhoneView();
                break;
            case R.id.tv_home_email:
                showEmailView();
                break;
            case R.id.login_qq:
                if(!checkBox.isChecked()){
                    ToastUtils.showToast(this,"请同意隐私权限和政策!");
                    return;
                }
                loginByQQ();
                break;
            case R.id.login_weixin:
                if(!checkBox.isChecked()){
                    ToastUtils.showToast(this,"请同意隐私权限和政策!");
                    return;
                }
                loginByWeChat();
                break;
            case R.id.faceBookLayout:
            case R.id.login_facebook:
                if(!checkBox.isChecked()){
                    ToastUtils.showToast(this,"请同意隐私权限和政策!");
                    return;
                }
                loginByFacebook();
                break;
            case R.id.login_twitter:
                if(!checkBox.isChecked()){
                    ToastUtils.showToast(this,"请同意隐私权限和政策!");
                    return;
                }
                loginByGoogle();
                break;
            case R.id.timer_email:
                getEmailCode();
                break;
            case R.id.timer:
                getPhoneCode();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.privacy_agreement:
                startPrivacyActivity();
                break;
            case R.id.tv_pro: {
                startAgreementActivity();
                break;
            }
        }
    }


    public void startPrivacyActivity() {
        Intent intentprivacy = new Intent(context, ActivityprivacyAgreement.class);
        intentprivacy.putExtra("title", UIUtils.getString(R.string.privacy_agreement));
        //https://api.mini-banana.com/agreement_static/privacyagreement.html
        intentprivacy.putExtra("url",AllocationApi.BaseUrl + "/agreement_static/privacyagreement.html");
        //intentprivacy.putExtra("url", AllocationApi.BaseUrl + "/isport/concumer-basic/agreement/privacyagreement.html");
        startActivity(intentprivacy);
    }

    public void startAgreementActivity() {
        Intent intent = new Intent(context, ActivityUserAgreement.class);
        intent.putExtra("title", UIUtils.getString(R.string.app_protol));
        intent.putExtra("url", AllocationApi.BaseUrl + "/isport/concumer-basic/agreement/agreement.html");
        startActivity(intent);
    }


   /* public void getHash() {
        try {
            int i = 0;
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                i++;
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String KeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //KeyHash 就是你要的，不用改任何代码  复制粘贴 ;
                Log.e("tyl", "KeyHash=" + KeyHash);
            }
        } catch (Exception e) {

        }


    }*/

    private void loginByFacebook() {
        boolean isFaceInstall = UMShareAPI.get(ActivityLogin.this).isInstall(this,SHARE_MEDIA.FACEBOOK);
        if(!isFaceInstall){
            ToastUtils.showToast(ActivityLogin.this, UIUtils.getString(R.string.please_install_software));
            return;
        }

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    private void loginByGoogle() {
//        boolean isFaceInstall = UMShareAPI.get(ActivityLogin.this).isInstall(this,SHARE_MEDIA.GOOGLEPLUS);
//        if(!isFaceInstall){
//            ToastUtils.showToast(ActivityLogin.this, UIUtils.getString(R.string.please_install_software));
//            return;
//        }
        if(!isSupportGoogleService()){
            ToastUtils.showToast(ActivityLogin.this, UIUtils.getString(R.string.please_install_software));
            return;
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void loginByQQ() {
        if (!PackageUtil.isWxInstall(this, PackageUtil.qqPakage)) {
            ToastUtils.showToast(context, UIUtils.getString(R.string.please_install_qq));
            return;
        }

        UMShareAPI mShareAPI = UMShareAPI.get(context);

        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.QQ, new UMAuthListener() {
            /**
             * @desc 授权开始的回调
             * @param platform 平台名称
             */
            @Override
            public void onStart(SHARE_MEDIA platform) {
            }

            /**
             * @desc 授权成功的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param
             */
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> map) {
//                Toast.makeText(mContext, "成功了", Toast.LENGTH_LONG).show();
//                openid:openid
//
//                unionid:（6.2以前用unionid）用户id
//
//                accesstoken: accessToken （6.2以前用access_token）
//
//                refreshtoken: refreshtoken: （6.2以前用refresh_token）
//
//                过期时间：expiration （6.2以前用expires_in）
//
//                name：name（6.2以前用screen_name）
//
//                城市：city
//
//                省份：prvinice
//
//                国家：country
//
//                性别：gender
//
//                头像：iconurl（6.2以前用profile_image_url）
                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                String name = map.get("name");
                String gender = map.get("gender");
                String iconurl = map.get("iconurl");

                mActPresenter.thirdPartyLogin(2, openid, name, iconurl);
                Logger.myLog("onComplete == " + map.toString());
//                    AuthUserBean
//
//                    private String access_token;
//                    private String openid;
//                    private String expires_in;
//                    private String refresh_token;
//
//                    private String unionid;
//                    private String nickname;
//                    private int sex;
//                    private String city;
//                    private String province;
//                    private String headimgurl;
            }

            /**
             * @desc 授权失败的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param t 错误原因
             */
            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//                Toast.makeText(mContext, "失败：" + t.getMessage(),                                     Toast.LENGTH_LONG).show();
            }

            /**
             * @desc 授权取消的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             */
            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
//                Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
            }
        });
     /*   AuthLoginHelper.getInstance().loginByQQ(this, new AuthListener() {

            @Override
            public void onSuccess(AuthUserBean response) {
                // TODO 发送后台请求
//                loginByWeChat 登录成功== AuthUserBean{access_token='null', openid='null', expires_in='0', refresh_token='null', unionid='oYUOJxBYdm93toVLzw7DQnRO7btU', nickname='嗯', sex=1, city='Shenzhen', province='Guangdong', headimgurl='http://thirdwx.qlogo.cn/mmopen/vi_32/wibYwE23Fa0OzoWC7UjVeuhlrjqN2DicN3e4Q5YwyLps1slrUzjcxVHeRe98s9MFYffM26T0zudtqDnKI65cqpVA/132'}
//                2019-03-27 14:35:15.869 12132-12132/? E/MyLog: thirdPartyLogin
                if (response.getOpenid() == null) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.frequently_tip));
                    return;
                }
                //ToastUtils.showToast(context, "QQ登录成功，errorMessage = " + response.getNickname());
                // MobclickAgent.onEvent(context, "0011");
                mActPresenter.thirdPartyLogin(2, response.getOpenid(), response.getNickname(), response.getHeadimgurl
                        ());
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
//                ToastUtils.showToast(context, UIUtils.getString(R.string.app_qq_sign_fail) + "errorMessage = " +
//                        errorMessage);
            }

            @Override
            public void onCancel() {
//                ToastUtils.showToast(context, UIUtils.getString(R.string.app_qq_cancel));
            }

            @Override
            public void onReturnNull() {

            }
        });*/
    }

    private void loginByWeChat() {
        if (!PackageUtil.isWxInstall(this, PackageUtil.weichatPakage)) {
            ToastUtils.showToast(context, UIUtils.getString(R.string.please_install_wechat));
            return;
        }

        UMShareAPI mShareAPI = UMShareAPI.get(context);

        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            /**
             * @desc 授权开始的回调
             * @param platform 平台名称
             */
            @Override
            public void onStart(SHARE_MEDIA platform) {
            }

            /**
             * @desc 授权成功的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param
             */
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> map) {
//                Toast.makeText(mContext, "成功了", Toast.LENGTH_LONG).show();
//                openid:openid
//
//                unionid:（6.2以前用unionid）用户id
//
//                accesstoken: accessToken （6.2以前用access_token）
//
//                refreshtoken: refreshtoken: （6.2以前用refresh_token）
//
//                过期时间：expiration （6.2以前用expires_in）
//
//                name：name（6.2以前用screen_name）
//
//                城市：city
//
//                省份：prvinice
//
//                国家：country
//
//                性别：gender
//
//                头像：iconurl（6.2以前用profile_image_url）
                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                String name = map.get("name");
                String gender = map.get("gender");
                String iconurl = map.get("iconurl");

                mActPresenter.thirdPartyLogin(1, openid, name, iconurl);
                Logger.myLog("onComplete == " + map.toString());
//                    AuthUserBean
//
//                    private String access_token;
//                    private String openid;
//                    private String expires_in;
//                    private String refresh_token;
//
//                    private String unionid;
//                    private String nickname;
//                    private int sex;
//                    private String city;
//                    private String province;
//                    private String headimgurl;
            }

            /**
             * @desc 授权失败的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param t 错误原因
             */
            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//                Toast.makeText(mContext, "失败：" + t.getMessage(),                                     Toast.LENGTH_LONG).show();
            }

            /**
             * @desc 授权取消的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             */
            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
//                Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
            }
        });

//        AuthLoginHelper.getInstance().loginByWeChat(this, new AuthListener() {
//
//            @Override
//            public void onSuccess(AuthUserBean response) {
//                Logger.myLog("loginByWeChat 登录成功== " + response.toString());
//                if (response.getOpenid() == null) {
////                    ToastUtils.showToast(context, UIUtils.getString(R.string.frequently_tip));
//                    EventBus.getDefault().post(new MessageEvent(MessageEvent.WeChat_NULL));
//                    return;
//                }
//
//                //MobclickAgent.onEvent(context, "0010");
//                // ToastUtils.showToast(context, "微信登录成功，errorMessage = " + response.getNickname());
//                mActPresenter.thirdPartyLogin(1, response.getOpenid(), response.getNickname(), response.getHeadimgurl
//                        ());
//            }
//
//            @Override
//            public void onError(int errorCode, String errorMessage) {
////                ToastUtils.showToast(context, UIUtils.getString(R.string.app_wechat_sign_fail) + ",errorMessage = " +
////                        errorMessage);
//            }
//
//            @Override
//            public void onCancel() {
////                ToastUtils.showToast(context, UIUtils.getString(R.string.app_wechat_cancel));
//            }
//
//            @Override
//            public void onReturnNull() {
//                EventBus.getDefault().post(new MessageEvent(MessageEvent.WeChat_NULL));
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(task);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (priDialog != null) {
            if (priDialog.dialog != null) {
                if (priDialog.dialog.isShowing()) {
                    priDialog.dialog.dismiss();
                }
            }
        }
        LoginManager.getInstance().logOut();
    }

    int index;

    private void handleResult(Task<GoogleSignInAccount> googleData) {
        try {
            GoogleSignInAccount signInAccount = googleData.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

            if (signInAccount != null && index == 0) {

                Log.e("account", "si:" + "\n" + signInAccount.getEmail());
                String str = signInAccount.getEmail() + "\n"
                        + signInAccount.getId() + "\n" +
                        signInAccount.getAccount().name + "\n" +
                        signInAccount.getDisplayName() + "\n" +
                        signInAccount.getGivenName() + "\n"
                        + signInAccount.getPhotoUrl() + "\n";
                Log.e("account", "str:" + str + "\n");
                if (signInAccount.getPhotoUrl() == null) {
                    mActPresenter.thirdPartyLogin(5, signInAccount.getId(), signInAccount.getAccount().name, "");
                } else {
                    mActPresenter.thirdPartyLogin(5, signInAccount.getId(), signInAccount.getAccount().name, signInAccount.getPhotoUrl().getPath());
                }

                //textView.setText(str);
            } else {
                Log.e("account", "si为空:" + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("account", "si异常:" + "\n");
        }

    }


    private void showPhoneView() {
        AnimationUtil.ScaleDownView(tvRadioEmail);
        AnimationUtil.ScaleUpView(tvRadioPhone);
        clearEtValue();
        etPhone.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.GONE);
        tvPhoneTips.setVisibility(View.VISIBLE);
        timerEmail.setVisibility(View.GONE);
        timer.setVisibility(View.VISIBLE);
    }

    private void clearEtValue() {
        timer.stopTimer();
        timerEmail.stopTimer();
        etEmail.setText("");
        etPhone.setText("");
        etCode.setText("");
    }

    private void showEmailView() {
        AnimationUtil.ScaleDownView(tvRadioPhone);
        AnimationUtil.ScaleUpView(tvRadioEmail);
        Logger.myLog("showEmailView");
        clearEtValue();
        tvPhoneTips.setVisibility(View.INVISIBLE);
        etPhone.setVisibility(View.GONE);
        etEmail.setVisibility(View.VISIBLE);
        tvPhoneTips.setVisibility(View.INVISIBLE);
        timerEmail.setVisibility(View.VISIBLE);
        timer.setVisibility(View.GONE);

    }


    private void login() {
        if(!checkBox.isChecked()){
            ToastUtils.showToast(this,"请同意隐私权限和政策!");
            return;
        }



        String strEtValue, strCode;
        strCode = etCode.getText().toString().trim();
        if (tvRadioPhone.isChecked()) {
            strEtValue = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(strEtValue)) {
                showToast(R.string.enter_tel);
                return;
            }
            if (mActPresenter.checkPhoneNum(strEtValue)) {
                if (strCode.length() != 4) {
                    showToast(R.string.enter_correct_vertify_notice);
                    return;
                }
                mActPresenter.login(strEtValue, strCode, "1");
            } else {
                showToast(R.string.enter_correct_tel_notice);
                return;
            }
        } else {
            strEtValue = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(strEtValue)) {
                showToast(R.string.enter_email);
                return;
            }
            if (mActPresenter.checkEmail(strEtValue)) {
                if (strCode.length() != 4) {
                    showToast(R.string.enter_correct_vertify_notice);
                    return;
                }
                mActPresenter.login(strEtValue, strCode, "2");
            } else {
                showToast(R.string.enter_correct_email_notice);
                return;
            }
        }

    }


    private void getEmailCode() {
        String phone = "";
        String type = "1";
        type = "2";
        phone = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast(R.string.enter_email);
            return;
        }
        if (!mActPresenter.checkEmail(phone)) {//TODO 发送验证码请求
            showToast(R.string.enter_correct_email_notice);
            return;
        }
        mActPresenter.getEmailVerify(phone, type, AppUtil.isZh(BaseApp.getApp()) ? "ch" : "en");
    }

    private void getPhoneCode() {
        String phone = "";
        String type = "1";
        type = "1";
        phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast(R.string.enter_tel);
            return;
        }
        if (!mActPresenter.checkPhoneNum(phone)) {//TODO 发送验证码请求
            showToast(R.string.enter_correct_tel_notice);
            return;
        }
        mActPresenter.getVerify(phone, type);
    }


    private void getCode() {
        Logger.myLog("tvRadioPhone.isChecked()");
        if (tvRadioPhone.isChecked()) {
            getPhoneCode();
        } else {
            getEmailCode();
        }

    }


    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response", response.toString());

                            String name = response.getJSONObject().getString("name");
                            String id = response.getJSONObject().getString("id");

                            mActPresenter.thirdPartyLogin(3, id, name, "");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();

    }



    //判断手机是否支持googleService
    private boolean isSupportGoogleService(){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return status == ConnectionResult.SUCCESS;
    }
}