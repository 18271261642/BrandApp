package com.isport.brandapp.device.share;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.device.PremissionUtil;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.ShareHelper;
import com.isport.brandapp.util.UserAcacheUtil;
import com.isport.brandapp.view.HeartrateRoundView;
import com.isport.brandapp.view.SleepArcView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import bike.gymproject.viewlibray.AkrobatNumberTextView;
import bike.gymproject.viewlibray.BebasNeueTextView;
import bike.gymproject.viewlibray.CircleImageView;
import bike.gymproject.viewlibray.chart.PieChartData;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.BitmapUtils;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;
import phone.gym.jkcq.com.commonres.commonutil.PhotoChoosePopUtil;


/**
 * 分享页面
 */
public class NewShareActivity extends BaseActivity implements View.OnClickListener, PremissionUtil.OnResult, UMShareListener {

    private FrameLayout fl_share_content;
    private LinearLayout ll_share_content_step, ll_share_content_sleep, ll_share_content_hr, ll_share_rope_ll;
    private TextView tv_nickname_hr, tv_nickname_step, tv_nickname_sleep;
    private TextView tv_time_hr, tv_time_step, tv_time_sleep;
    private TextView tv_step, tv_cal, tv_dis;
    private TextView tv_deep_sleep_text, tv_light_sleep_text, tv_awake_text;
    private CircleImageView iv_user_head_hr, iv_user_head_step, iv_user_head_sleep;
    private RelativeLayout rl_share_bg1, rl_share_bg2, rl_share_bg3, rl_share_bg4;
    private ImageView iv_sure1, iv_sure2, iv_sure3, iv_sure4;
    private ImageView iv_share_bg1, iv_share_bg2, iv_share_bg3, iv_share_b4;

    //跳绳
    private TextView tv_rope_number, tv_rope_sum_time, tv_rope_sum_number, tv_rope_sum_cal, tv_rope_nikename, tv_rope_time;
    private ImageView iv_user_head_rope;
    //跳绳的累计次数文字
    private TextView shareRopeRecordCountTv;
    //完成全网挑战的整个布局
    private LinearLayout shareRopeLayout;
    //排行
    private TextView ropeSortTv;

    //平均心率
    private AkrobatNumberTextView tv_rope_avgHeartTv;
    //平均心率的布局
    private LinearLayout shareRopeHeartLayout;
    //时长的文字描述，挑战分享为 用时
    private TextView shareTimeTitleTv;


    //心率
    private BebasNeueTextView tv_today_hr;
    private BebasNeueTextView tv_max_hr;
    private BebasNeueTextView tv_min_hr;
    private BebasNeueTextView tv_average_hr;
    private HeartrateRoundView hr_roundview_max;
    private HeartrateRoundView hr_roundview_min;
    private HeartrateRoundView hr_roundview_average;
    //步数
    private BebasNeueTextView tv_step_value;
    private BebasNeueTextView tv_today_distance;
    private BebasNeueTextView tv_today_consume;
    //睡眠
    private BebasNeueTextView tv_hour;
    private BebasNeueTextView tv_minute;
    private BebasNeueTextView tv_hour_awake;
    private BebasNeueTextView tv_minute_awake;
    private BebasNeueTextView tv_hour_light_sleep;
    private BebasNeueTextView tv_minute_light_sleep;
    private BebasNeueTextView tv_hour_deep_sleep;
    private BebasNeueTextView tv_minute_deep_sleep;
    //private PieChartViewHeart sleep_arcview;
    private SleepArcView sleep_arcview;

    private Button btn_custom_bg;
    //old
    private LinearLayout layoutAll;
    private LinearLayout layout_bottom;
    private ImageView ivQQ, ivWechat, ivWebo, ivFriend, ivFacebook, ivShareMore;
    private View viewZh;
    private ImageView iv_back;

    private ShareBean shareBean;
    private UserInfoBean infoBean;
    private PremissionUtil util;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_share;
    }

    @Override
    protected void initView(View view) {

        fl_share_content = findViewById(R.id.fl_share_content);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = DisplayUtils.dip2px(this, 15);
        layoutParams.rightMargin = DisplayUtils.dip2px(this, 15);
        layoutParams.height = ScreenUtils.getScreenWidth() - DisplayUtils.dip2px(this, 30);
        fl_share_content.setLayoutParams(layoutParams);
        Log.e("NewShareActivity", "width=" + ScreenUtils.getScreenWidth() + " Density=" + ScreenUtils.getScreenDensity() + " widthDpi="
                + ScreenUtils.getScreenDensityDpi() + "height=" + ScreenUtils.getScreenHeight());
        btn_custom_bg = findViewById(R.id.btn_custom_bg);
        //睡眠
        ll_share_content_sleep = findViewById(R.id.ll_share_content_sleep);
        tv_nickname_sleep = findViewById(R.id.tv_nickname_sleep);
        tv_time_sleep = findViewById(R.id.tv_time_sleep);
        iv_user_head_sleep = findViewById(R.id.iv_user_head_sleep);

        tv_hour = findViewById(R.id.tv_hour);
        tv_minute = findViewById(R.id.tv_minute);
        tv_hour_awake = findViewById(R.id.tv_hour_awake);
        tv_minute_awake = findViewById(R.id.tv_minute_awake);
        tv_hour_light_sleep = findViewById(R.id.tv_hour_light_sleep);
        tv_minute_light_sleep = findViewById(R.id.tv_minute_light_sleep);
        tv_hour_deep_sleep = findViewById(R.id.tv_hour_deep_sleep);
        tv_minute_deep_sleep = findViewById(R.id.tv_minute_deep_sleep);
        // sleep_arcview = findViewById(R.id.pieChartView);
        sleep_arcview = findViewById(R.id.sleep_arcview);

        tv_deep_sleep_text = findViewById(R.id.tv_deep_sleep_text);
        tv_light_sleep_text = findViewById(R.id.tv_light_sleep_text);
        tv_awake_text = findViewById(R.id.tv_awake_text);
        //心率
        ll_share_content_hr = findViewById(R.id.ll_share_content_hr);
        tv_nickname_hr = findViewById(R.id.tv_nickname_hr);
        tv_time_hr = findViewById(R.id.tv_time_hr);
        iv_user_head_hr = findViewById(R.id.iv_user_head_hr);
        shareRopeHeartLayout = findViewById(R.id.shareRopeHeartLayout);

        tv_today_hr = findViewById(R.id.tv_today_hr);
        tv_max_hr = findViewById(R.id.tv_max_hr);
        tv_min_hr = findViewById(R.id.tv_min_hr);
        tv_average_hr = findViewById(R.id.tv_average_hr);
        hr_roundview_max = findViewById(R.id.hr_roundview_max);
        hr_roundview_min = findViewById(R.id.hr_roundview_min);
        hr_roundview_average = findViewById(R.id.hr_roundview_average);
        //步数
        ll_share_content_step = findViewById(R.id.ll_share_content_step);
        tv_step = findViewById(R.id.tv_step);
        tv_cal = findViewById(R.id.tv_cal);
        tv_dis = findViewById(R.id.tv_dis);
        tv_nickname_step = findViewById(R.id.tv_nickname_step);
        tv_time_step = findViewById(R.id.tv_time_step);
        iv_user_head_step = findViewById(R.id.iv_user_head_step);

        rl_share_bg1 = findViewById(R.id.rl_share_bg1);
        rl_share_bg2 = findViewById(R.id.rl_share_bg2);
        rl_share_bg3 = findViewById(R.id.rl_share_bg3);
        rl_share_bg4 = findViewById(R.id.rl_share_bg4);
        iv_sure1 = findViewById(R.id.iv_sure1);
        iv_sure2 = findViewById(R.id.iv_sure2);
        iv_sure3 = findViewById(R.id.iv_sure3);
        iv_sure4 = findViewById(R.id.iv_sure4);
        iv_share_bg1 = findViewById(R.id.iv_share_bg1);
        iv_share_bg2 = findViewById(R.id.iv_share_bg2);
        iv_share_bg3 = findViewById(R.id.iv_share_bg3);
        iv_share_b4 = findViewById(R.id.iv_share_bg4);

        tv_step_value = findViewById(R.id.tv_step_value);
        tv_today_distance = findViewById(R.id.tv_today_distance);
        tv_today_consume = findViewById(R.id.tv_today_consume);


        //跳绳分享
        ll_share_rope_ll = findViewById(R.id.ll_share_content_rope);
        tv_rope_number = findViewById(R.id.tv_rope_number);
        tv_rope_sum_time = findViewById(R.id.tv_rope_sum_time);
        tv_rope_sum_number = findViewById(R.id.tv_rope_sum_number);
        tv_rope_sum_cal = findViewById(R.id.tv_rope_sum_cal);
        iv_user_head_rope = findViewById(R.id.iv_user_head_rope);
        tv_rope_nikename = findViewById(R.id.tv_rope_nikename);
        tv_rope_time = findViewById(R.id.tv_rope_time);
        shareRopeRecordCountTv = findViewById(R.id.shareRopeRecordCountTv);
        shareTimeTitleTv = findViewById(R.id.shareTimeTitleTv);


        shareRopeLayout = findViewById(R.id.shareRopeLayout);
        ropeSortTv = findViewById(R.id.shareRopeSortTv);
        tv_rope_avgHeartTv = findViewById(R.id.tv_rope_avgHeartTv);




        //
        layoutAll = findViewById(R.id.layout_all);
        layout_bottom = findViewById(R.id.layout_bottom);
        iv_back = findViewById(R.id.iv_back);
        ivQQ = view.findViewById(R.id.iv_qq);
        ivWebo = view.findViewById(R.id.iv_weibo);
        ivWechat = view.findViewById(R.id.iv_wechat);
        ivFriend = view.findViewById(R.id.iv_friend);
        ivShareMore = view.findViewById(R.id.iv_more);
        ivFacebook = view.findViewById(R.id.iv_facebook);
        viewZh = view.findViewById(R.id.view_zh);

        if (App.getApp().isUSA()) {
            viewZh.setVisibility(View.GONE);
            ivFacebook.setVisibility(View.VISIBLE);
            ivWebo.setVisibility(View.GONE);
            ivFriend.setVisibility(View.GONE);
            // layout_bottom.setVisibility(View.INVISIBLE);
            // layout_bottom_us.setVisibility(View.VISIBLE);
        } else {
            viewZh.setVisibility(View.VISIBLE);
            ivFacebook.setVisibility(View.GONE);
            ivWebo.setVisibility(View.VISIBLE);
            ivFriend.setVisibility(View.VISIBLE);
        }
    }


    private void matchUSView() {

        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) tv_awake_text.getLayoutParams();
        if (App.getApp().isUSA()) {
            textLayoutParams.width = DisplayUtils.dip2px(this, 110);
        } else {
            textLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        tv_awake_text.setLayoutParams(textLayoutParams);
        tv_deep_sleep_text.setLayoutParams(textLayoutParams);
        tv_light_sleep_text.setLayoutParams(textLayoutParams);
    }

    @Override
    protected void initData() {
        getIntentValue();
        matchUSView();
        util = new PremissionUtil();
        infoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(this));
        if (shareBean != null) {
            tv_time_hr.setText(shareBean.time);
            tv_time_sleep.setText(shareBean.time);
            tv_time_step.setText(shareBean.time);
        }
        if (currentShareDeviceType == JkConfiguration.DeviceType.WATCH_W516 || currentShareDeviceType == JkConfiguration.DeviceType.BRAND_W311) {
            showStepView();
        } else if (currentShareDeviceType == JkConfiguration.DeviceType.SLEEP) {
            showSleepView();
        } else if (currentShareDeviceType == JkConfiguration.DeviceType.HEART_RATE) {
            showHeartRateView();
        } else {
            showRopeView();
        }
        util.setOnResultLister(this);

        requestPermiss();
    }




    private void requestPermiss(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0x00);
    }


    @Override
    protected void initEvent() {
        btn_custom_bg.setOnClickListener(this);

        rl_share_bg1.setOnClickListener(this);
        rl_share_bg2.setOnClickListener(this);
        rl_share_bg3.setOnClickListener(this);
        rl_share_bg4.setOnClickListener(this);

        ivQQ.setOnClickListener(this);
        ivWebo.setOnClickListener(this);
        ivWechat.setOnClickListener(this);
        ivFriend.setOnClickListener(this);
        ivShareMore.setOnClickListener(this);

        ivFacebook.setOnClickListener(this);
        //ivTwitter.setOnClickListener(this);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initHeader() {
        ImmersionBar.with(this).statusBarDarkFont(true)
                .init();
        //  StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
        // StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.common_text_color));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_share_bg1:
                iv_sure1.setVisibility(View.VISIBLE);
                iv_sure2.setVisibility(View.INVISIBLE);
                iv_sure4.setVisibility(View.INVISIBLE);
                iv_sure3.setVisibility(View.INVISIBLE);

                if (currentShareDeviceType == JkConfiguration.DeviceType.WATCH_W516 || currentShareDeviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    fl_share_content.setBackgroundResource(R.drawable.share_step_bg1);
                    setBgColor(true);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.SLEEP) {
                    fl_share_content.setBackgroundResource(R.drawable.share_sleep_bg1);
                    setBgColor(true);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.HEART_RATE) {
                    fl_share_content.setBackgroundResource(R.drawable.share_heartrate_bg1);
                    setBgColor(true);
                } else {
                    fl_share_content.setBackgroundResource(R.drawable.bg_share_rope_1_radius);
                    setBgColor(true);
                }


                // fl_share_content.getBackground().mutate().setAlpha(255);
                break;
            case R.id.rl_share_bg2:
                iv_sure1.setVisibility(View.INVISIBLE);
                iv_sure2.setVisibility(View.VISIBLE);
                iv_sure4.setVisibility(View.INVISIBLE);
                iv_sure3.setVisibility(View.INVISIBLE);
//                fl_share_content.setBackground(iv_share_bg2.getDrawable());
                if (currentShareDeviceType == JkConfiguration.DeviceType.WATCH_W516 || currentShareDeviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    fl_share_content.setBackgroundResource(R.drawable.share_step_bg2);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.SLEEP) {
                    fl_share_content.setBackgroundResource(R.drawable.share_sleep_bg2);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.HEART_RATE) {
                    fl_share_content.setBackgroundResource(R.drawable.share_heartrate_bg2);
                } else {
                    fl_share_content.setBackgroundResource(R.drawable.bg_share_rope_2_radius);
                }
                setBgColor(true);
                // fl_share_content.getBackground().mutate().setAlpha(255);
                break;
            case R.id.rl_share_bg3:
                iv_sure1.setVisibility(View.INVISIBLE);
                iv_sure2.setVisibility(View.INVISIBLE);
                iv_sure4.setVisibility(View.INVISIBLE);
                iv_sure3.setVisibility(View.VISIBLE);
//                fl_share_content.setBackground(iv_share_bg3.getDrawable());
                if (currentShareDeviceType == JkConfiguration.DeviceType.WATCH_W516 || currentShareDeviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    fl_share_content.setBackgroundResource(R.drawable.share_step_bg3);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.SLEEP) {
                    fl_share_content.setBackgroundResource(R.drawable.share_sleep_bg3);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.HEART_RATE) {
                    fl_share_content.setBackgroundResource(R.drawable.share_heartrate_bg3);
                } else {
                    fl_share_content.setBackgroundResource(R.drawable.bg_share_rope_3_radius);
                }
                setBgColor(true);
                //fl_share_content.getBackground().mutate().setAlpha(255);
                break;
            case R.id.rl_share_bg4:
                iv_sure1.setVisibility(View.INVISIBLE);
                iv_sure2.setVisibility(View.INVISIBLE);
                iv_sure4.setVisibility(View.VISIBLE);
                iv_sure3.setVisibility(View.INVISIBLE);
//                fl_share_content.setBackground(iv_share_bg3.getDrawable());
                if (currentShareDeviceType == JkConfiguration.DeviceType.WATCH_W516 || currentShareDeviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    fl_share_content.setBackgroundResource(R.drawable.share_step_bg4);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.SLEEP) {
                    fl_share_content.setBackgroundResource(R.drawable.share_sleep_bg4);
                } else if (currentShareDeviceType == JkConfiguration.DeviceType.HEART_RATE) {
                    fl_share_content.setBackgroundResource(R.drawable.bg_share_rope_4_radius);
                } else {
                    fl_share_content.setBackgroundResource(R.drawable.bg_share_rope_4_radius);
                }
                setBgColor(true);
                // fl_share_content.getBackground().mutate().setAlpha(255);
                break;

            case R.id.iv_qq:
                if (PackageUtil.isWxInstall(NewShareActivity.this, PackageUtil.qqPakage)) {
                    hideView();
                    util.checkNewShareCameraPersiomm(this, this, fl_share_content, "qq");
                } else {
                    ToastUtils.showToast(NewShareActivity.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }

                break;
            case R.id.iv_friend:
                if (PackageUtil.isWxInstall(NewShareActivity.this, PackageUtil.weichatPakage)) {
                    hideView();
                    util.checkCameraPersiomm(this, this, fl_share_content, "friend");
                } else {

                    ToastUtils.showToast(NewShareActivity.this, UIUtils.getString(R.string.please_install_software));
                }

                break;
            case R.id.iv_wechat:
                if (PackageUtil.isWxInstall(NewShareActivity.this, PackageUtil.weichatPakage)) {
                    hideView();
                    util.checkCameraPersiomm(this, this, fl_share_content, "wechat");
                } else {

                    ToastUtils.showToast(NewShareActivity.this, UIUtils.getString(R.string.please_install_software));
                }

                break;
            case R.id.iv_weibo:
//                hideView();
//                util.checkCameraPersiomm(this, this, fl_share_content, "weibo");
                if (PackageUtil.isWxInstall(NewShareActivity.this, PackageUtil.weiboPakage)) {
                    hideView();
                    util.checkCameraPersiomm(this, this, fl_share_content, "weibo");
                } else {
                    ToastUtils.showToast(NewShareActivity.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }

                break;
            case R.id.iv_facebook:
                boolean isFaceInstall = UMShareAPI.get(NewShareActivity.this).isInstall(this,SHARE_MEDIA.FACEBOOK);
                if(!isFaceInstall){
                    ToastUtils.showToast(NewShareActivity.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }

                hideView();
                util.checkCameraPersiomm(this, this, fl_share_content, "facebook");

//                //判断facebook是否安装，没有按钮
//                if (PackageUtil.isWxInstall(NewShareActivity.this, PackageUtil.facebookPakage)) {
//                    hideView();
//                    util.checkCameraPersiomm(this, this, fl_share_content, "facebook");
//                } else {
//                    ToastUtils.showToast(NewShareActivity.this, UIUtils.getString(R.string.please_install_software));
//                    return;
//                }
                break;

            case R.id.iv_more:
                hideView();
                util.checkCameraPersiomm(this, this, fl_share_content, "more");
                break;

            case R.id.btn_custom_bg:
                new RxPermissions(NewShareActivity.this)
                        .request(Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                showPhotoChoosePop();
                            } else {
                                ToastUtils.showToast(NewShareActivity.this, "need permissions");
                            }
                        });

                break;
            default:
        }
    }


    public void setBgColor(boolean isWhite) {
        if (currentShareDeviceType == JkConfiguration.DeviceType.WATCH_W516 || currentShareDeviceType == JkConfiguration.DeviceType.BRAND_W311) {
            if (isWhite) {
                ll_share_content_step.setBackgroundColor(UIUtils.getColor(R.color.transparent));
            } else {
                ll_share_content_step.setBackgroundColor(UIUtils.getColor(R.color.blank_40));
            }
        } else if (currentShareDeviceType == JkConfiguration.DeviceType.SLEEP) {
            if (isWhite) {
                ll_share_content_sleep.setBackgroundColor(UIUtils.getColor(R.color.transparent));
            } else {
                ll_share_content_sleep.setBackgroundColor(UIUtils.getColor(R.color.blank_40));
            }
        } else if (currentShareDeviceType == JkConfiguration.DeviceType.HEART_RATE) {
            if (isWhite) {
                ll_share_content_hr.setBackgroundColor(UIUtils.getColor(R.color.transparent));
            } else {
                ll_share_content_hr.setBackgroundColor(UIUtils.getColor(R.color.blank_40));
            }
        } else {
            if (isWhite) {
                ll_share_rope_ll.setBackgroundColor(UIUtils.getColor(R.color.transparent));
            } else {
                ll_share_rope_ll.setBackgroundColor(UIUtils.getColor(R.color.blank_40));
            }
        }
    }

    @Override
    public void OnResultYes(File file, String type) {

        this.file = file;

        if (type.equals("more")) {
            shareFile(file);
        } else if (type.equals("facebook") || type.equals("twitter")) {
            shareFaceBook();
        } else {
            SHARE_MEDIA share_media = SHARE_MEDIA.QQ;
            switch (type) {
                case "qq":
                    share_media = SHARE_MEDIA.QQ;
                    break;
                case "wechat":
                    share_media = SHARE_MEDIA.WEIXIN;

                    break;
                case "friend":
                    share_media = SHARE_MEDIA.WEIXIN_CIRCLE;

                    break;
                case "weibo":
                    share_media = SHARE_MEDIA.SINA;

                    break;

            }
            UMImage image = ShareHelper.getUMWeb(NewShareActivity.this, file);
            new ShareAction(NewShareActivity.this).setPlatform(share_media)
                    .withMedia(image)
                    .setCallback(NewShareActivity.this)
                    .share();
        }

    }

    /**
     * 分享到facebook
     */
    private CallbackManager callBackManager;

    public void shareFaceBook() {
        Bitmap image = BitmapFactory.decodeFile(file.getPath());
        callBackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callBackManager, facebookCallback);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

        Log.e("onActivityResult", "requestCode=" + requestCode + "resultCode=" + resultCode);
        if (callBackManager != null) {
            callBackManager.onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:

                // 从相册返回的数据
                if (data != null && resultCode == RESULT_OK) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String path = BitmapUtils.getRealFilePath(context, uri);
                    Logger.myLog(TAG,"----图片选择path="+path+"\n"+uri);
//                   setCustomBg(path);
                    if ("Redmi4A".equals(AppUtil.getModel())) {
                        setCustomBg(path);
                    } else {
                        startPhotoZoom(uri);
//                        startTestPhotoZoom(uri);
                    }

//                    mActPresenter.compressPhoto(new File(path));
                } else {
                    NetProgressObservable.getInstance().hide();
                }
                break;
            case PHOTO_REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    Uri uri = getUriForFile(this, tempFile);
                    String path = BitmapUtils.getRealFilePath(context, uri);


//                    setCustomBg(path);
//                    setCustomBg(tempFile.getAbsolutePath());
                    if ("Redmi4A".equals(DeviceUtils.getModel())) {
                        setCustomBg(path);
                    } else {
                        startPhotoZoom(uri);
                    }
//                   mActPresenter.compressPhoto(new File(path));
                } else {
                    NetProgressObservable.getInstance().hide();
                }
                break;
            case PHOTO_CUT:
                Logger.myLog(TAG,"-------333--mImgPath="+mImgPath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setCustomBg(mImgPath);
                    }
                }).start();
                break;
            default:
        }
    }

    @Override
    public void OnresultNo(File file) {

    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {

    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.NEED_LOGIN:
                ToastUtils.showToast(context, UIUtils.getString(com.isport.brandapp.basicres.R.string.login_again));
                NetProgressObservable.getInstance().hide();
                BleProgressObservable.getInstance().hide();
                TokenUtil.getInstance().clear(context);
                UserAcacheUtil.clearAll();
                AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
                App.initAppState();
                Intent intent = new Intent(context, ActivityLogin.class);
                context.startActivity(intent);
                ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
                break;
            default:
                break;
        }
    }

    /**
     * facebook分享状态回调
     */
    private FacebookCallback facebookCallback = new FacebookCallback() {

        @Override
        public void onSuccess(Object o) {
//            SysoutUtil.out("onSuccess" + o.toString());
//            Message msg = Message.obtain();
//            msg.what = SHARE_COMPLETE;
//            mHandler.sendMessage(msg);
        }

        @Override
        public void onCancel() {
//            SysoutUtil.out("onCancel");
//            Message msg = Message.obtain();
//            msg.what = SHARE_CANCEL;
//            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(FacebookException error) {
//            SysoutUtil.out("onError");
//            ToastUtils.showToast("share error--" + error.getMessage());
//            Message msg = Message.obtain();
//            msg.what = SHARE_ERROR;
//            mHandler.sendMessage(msg);
        }
    };

    public void shareFile(File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri = null;
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "项目包名.fileprovider"即是在清单文件中配置的authorities
                uri = FileProvider.getUriForFile(NewShareActivity.this, getPackageName() + ".fileprovider",
                        file);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "image/*";
       /* if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }*/
        return mime;
    }

    private int currentShareDeviceType;

    private void getIntentValue() {
        currentShareDeviceType = getIntent().getIntExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.WATCH_W516);
        shareBean = getIntent().getParcelableExtra(JkConfiguration.FROM_BEAN);
        if (shareBean == null) {
            finish();
        }
    }

    public void hideView() {
        iv_back.setVisibility(View.INVISIBLE);
        if (AppUtil.isZh(this)) {
            layout_bottom.setVisibility(View.INVISIBLE);
        }
    }

    public void showView() {
        iv_back.setVisibility(View.VISIBLE);
        if (AppUtil.isZh(this)) {
            layout_bottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示分享步数
     */
    public void showStepView() {
        ll_share_content_step.setVisibility(View.VISIBLE);
        ll_share_content_sleep.setVisibility(View.GONE);
        ll_share_content_hr.setVisibility(View.GONE);
        ll_share_rope_ll.setVisibility(View.GONE);
        fl_share_content.setBackgroundResource(R.drawable.share_step_bg1);


//        rl_share_bg1.setBackgroundResource(R.drawable.share_step_bg1);
//        rl_share_bg2.setBackgroundResource(R.drawable.share_step_bg2);
//        rl_share_bg3.setBackgroundResource(R.drawable.share_step_bg3);
        iv_share_bg1.setImageResource(R.drawable.bg_share_step_small_1);
        iv_share_bg2.setImageResource(R.drawable.bg_share_step_small_2);
        iv_share_bg3.setImageResource(R.drawable.bg_share_step_small_3);
        iv_share_b4.setImageResource(R.drawable.bg_share_step_small_4);
        if (infoBean != null) {
            if (App.isHttp()) {
                LoadImageUtil.getInstance().loadCirc(context, infoBean.getHeadUrl(), iv_user_head_step);
            } else {
                if (!TextUtils.isEmpty(infoBean.getHeadUrl())) {
                    iv_user_head_step.setImageBitmap(BitmapFactory.decodeFile(infoBean.getHeadUrl()));
                }
            }
            tv_nickname_step.setText(infoBean.getNickName());
        }

        if (shareBean != null) {
            if (shareBean.isWeek) {
                tv_step.setText(UIUtils.getString(R.string.avg_share_step));
                tv_dis.setText(UIUtils.getString(R.string.avg_share_dis));
                tv_cal.setText(UIUtils.getString(R.string.avg_share_cal));
            } else {
                tv_step.setText(R.string.today_step1);
                tv_dis.setText(R.string.today_dis1);
                tv_cal.setText(R.string.today_cal1);
            }
        }


        if (shareBean != null) {
            tv_step_value.setText(shareBean.one);
            tv_today_distance.setText(shareBean.two);
            tv_today_consume.setText(shareBean.three);
        }
    }

    /**
     * 显示分享睡眠
     */
    public void showSleepView() {
        ll_share_content_step.setVisibility(View.GONE);
        ll_share_content_sleep.setVisibility(View.VISIBLE);
        ll_share_content_hr.setVisibility(View.GONE);
        ll_share_rope_ll.setVisibility(View.GONE);
        fl_share_content.setBackgroundResource(R.drawable.share_sleep_bg1);
//        rl_share_bg1.setBackgroundResource(R.drawable.share_sleep_bg1);
//        rl_share_bg2.setBackgroundResource(R.drawable.share_sleep_bg2);
//        rl_share_bg3.setBackgroundResource(R.drawable.share_sleep_bg3);
        iv_share_bg1.setImageResource(R.drawable.bg_share_sleep_small_1);
        iv_share_bg2.setImageResource(R.drawable.bg_share_sleep_small_2);
        iv_share_bg3.setImageResource(R.drawable.bg_share_sleep_small_3);
        iv_share_b4.setImageResource(R.drawable.bg_share_sleep_small_4);
        if (infoBean != null) {
            if (App.isHttp()) {
                LoadImageUtil.getInstance().loadCirc(context, infoBean.getHeadUrl(), iv_user_head_sleep);
            } else {
                if (!TextUtils.isEmpty(infoBean.getHeadUrl())) {
                    iv_user_head_sleep.setImageBitmap(BitmapFactory.decodeFile(infoBean.getHeadUrl()));
                }
            }
            tv_nickname_sleep.setText(infoBean.getNickName());
        }

//        String deepTimehour = CommonDateUtil.formatTwoStr(Integer.valueOf(shareBean.one) / 60);
//        String deepTimeminute = CommonDateUtil.formatTwoStr(Integer.valueOf(shareBean.one) % 60);
        String deepTimehour = "" + Integer.valueOf(shareBean.one) / 60;
        String deepTimeminute = "" + Integer.valueOf(shareBean.one) % 60;
        String lightTimeHour = "" + Integer.valueOf(shareBean.two) / 60;
        String lightTimeMin = "" + Integer.valueOf(shareBean.two) % 60;
        String sleepTimehour = Integer.valueOf(shareBean.centerValue) / 60 + "";
        String sleepTimeminute = Integer.valueOf(shareBean.centerValue) % 60 + "";
        String awakeHour = "" + Integer.valueOf(shareBean.three) / 60;
        String awakeMin = "" + Integer.valueOf(shareBean.three) % 60;
        tv_hour.setText(sleepTimehour);
        tv_minute.setText(sleepTimeminute);
        tv_hour_awake.setText(awakeHour);
        tv_minute_awake.setText(awakeMin);
        tv_hour_deep_sleep.setText(deepTimehour);
        tv_minute_deep_sleep.setText(deepTimeminute);
        tv_hour_light_sleep.setText(lightTimeHour);
        tv_minute_light_sleep.setText(lightTimeMin);

        //深睡 、浅、清醒
        sleep_arcview.setValue(Integer.valueOf(shareBean.three), Integer.valueOf(shareBean.two), Integer.valueOf(shareBean.one));
        //setPieData(Integer.valueOf(shareBean.one), Integer.valueOf(shareBean.two), Integer.valueOf(shareBean.three));
    }

    private float getPiePercent(int time, int totalTime) {
        if (time == 0) {
            return 0;
        }
        float pre = (float) time / totalTime * 100.f;
        if (pre < 1) {
            pre = 1;
        }
        return (float) pre;
    }

    private int getTotalTime(int wakeUpTotalTime, int sleepTotalTime, int deepSleepTotalTime) {
        return wakeUpTotalTime + sleepTotalTime + deepSleepTotalTime;
    }

    private void setPieData(int wakeUpTotalTime, int sleepTotalTime, int deepSleepTotalTime) {
        int totalTime = getTotalTime(wakeUpTotalTime, sleepTotalTime, deepSleepTotalTime);


        if (sleep_arcview != null) {
            List<PieChartData> pieChartDatas = new ArrayList<>();
            if (totalTime == 0) {
                //setHourMinute(0, 0);
                pieChartDatas.add(new PieChartData(1, UIUtils.getColor(R.color.common_rope_time_color)));//绿色
            } else {
                //setHourMinute(totalTime / 60, totalTime % 60);
                pieChartDatas.add(new PieChartData(getPiePercent(deepSleepTotalTime, totalTime), UIUtils.getColor(R.color.common_deep_sleep)));//绿色
                pieChartDatas.add(new PieChartData(getPiePercent(sleepTotalTime, totalTime), UIUtils.getColor(R.color.common_light_sleep)));//橘黄
                pieChartDatas.add(new PieChartData(getPiePercent(wakeUpTotalTime, totalTime), UIUtils.getColor(R.color.common_awake_sleep)));//深黄
            }

            // sleep_arcview.updateData(pieChartDatas, true);
        }

        // pieChartView.setValue(wakeUpTotalTime, eyeMoveTotalTime + sleepTotalTime, deepSleepTotalTime);

    }

    /**
     * 显示跳绳UI
     */
    public void showRopeView() {

        //判断是否是跳绳当前挑战成功的分享
        boolean isChallenge = currentShareDeviceType == JkConfiguration.RopeSportType.ROPE_CHALLENGE;
        shareRopeLayout.setVisibility( isChallenge? View.VISIBLE : View.GONE);
        tv_rope_sum_number.setVisibility(isChallenge ? View.INVISIBLE : View.VISIBLE);
        shareRopeRecordCountTv.setVisibility(isChallenge ? View.INVISIBLE : View.VISIBLE);
        shareRopeHeartLayout.setVisibility(isChallenge ? View.VISIBLE : View.GONE);

        shareTimeTitleTv.setText(isChallenge ? getResources().getString(R.string.rope_real_rope_time) : getResources().getString(R.string.rope_share_sum_time));



        ll_share_content_step.setVisibility(View.GONE);
        ll_share_content_sleep.setVisibility(View.GONE);
        ll_share_content_hr.setVisibility(View.GONE);
        ll_share_rope_ll.setVisibility(View.VISIBLE);
        fl_share_content.setBackgroundResource(R.drawable.bg_share_rope_1_radius);
        iv_share_bg1.setImageResource(R.drawable.bg_share_small_rope_1);
        iv_share_bg2.setImageResource(R.drawable.bg_share_small_rope_2);
        iv_share_bg3.setImageResource(R.drawable.bg_share_small_rope_3);
        iv_share_b4.setImageResource(R.drawable.bg_share_small_rope_4);
        if (infoBean != null) {
            if (App.isHttp()) {
                LoadImageUtil.getInstance().loadCirc(context, infoBean.getHeadUrl(), iv_user_head_rope);
            } else {
                if (!TextUtils.isEmpty(infoBean.getHeadUrl())) {
                    iv_user_head_rope.setImageBitmap(BitmapFactory.decodeFile(infoBean.getHeadUrl()));
                }
            }
            tv_rope_nikename.setText(infoBean.getNickName());
        }

        if (shareBean != null) {

            Logger.myLog(TAG,"------分享bean="+new Gson().toJson(shareBean));

            tv_rope_number.setText(shareBean.centerValue);
            tv_rope_sum_time.setText(shareBean.one);
            tv_rope_sum_number.setText(shareBean.two);
            tv_rope_sum_cal.setText(shareBean.three);
            tv_rope_time.setText(shareBean.time);
            if(currentShareDeviceType == JkConfiguration.RopeSportType.ROPE_CHALLENGE){
//                ropeDescTv.setText("'"+shareBean.getChallengeDesc()+"'");
//                ropeSortTv.setText(shareBean.getChallengeRank()+"");
                ropeSortTv.setText(String.format(getResources().getString(R.string.string_share_rank_txt),shareBean.getChallengeRank(),shareBean.getChallengeDesc()));
                tv_rope_avgHeartTv.setText(shareBean.getRopeAvgHeart()+"");
            }
        }
    }

    /**
     * 显示分享心率
     */
    public void showHeartRateView() {
        ll_share_content_step.setVisibility(View.GONE);
        ll_share_content_sleep.setVisibility(View.GONE);
        ll_share_content_hr.setVisibility(View.VISIBLE);
        ll_share_rope_ll.setVisibility(View.GONE);
        fl_share_content.setBackgroundResource(R.drawable.share_heartrate_bg1);
//        rl_share_bg1.setBackgroundResource(R.drawable.share_heartrate_bg1);
//        rl_share_bg2.setBackgroundResource(R.drawable.share_heartrate_bg2);
//        rl_share_bg3.setBackgroundResource(R.drawable.share_heartrate_bg3);

        iv_share_bg1.setImageResource(R.drawable.share_heartrate_bg1);
        iv_share_bg2.setImageResource(R.drawable.share_heartrate_bg2);
        iv_share_bg3.setImageResource(R.drawable.share_heartrate_bg3);
        if (infoBean != null) {
            if (App.isHttp()) {
                LoadImageUtil.getInstance().loadCirc(context, infoBean.getHeadUrl(), iv_user_head_hr);
            } else {
                if (!TextUtils.isEmpty(infoBean.getHeadUrl())) {
                    iv_user_head_hr.setImageBitmap(BitmapFactory.decodeFile(infoBean.getHeadUrl()));
                }
            }
            tv_nickname_hr.setText(infoBean.getNickName());
        }
        tv_today_hr.setText(shareBean.centerValue);
        tv_max_hr.setText(shareBean.one);
        tv_min_hr.setText(shareBean.two);
        tv_average_hr.setText(shareBean.three);
        hr_roundview_max.setProgress(Integer.valueOf(shareBean.one));
        hr_roundview_min.setProgress(Integer.valueOf(shareBean.two));
        hr_roundview_average.setProgress(Integer.valueOf(shareBean.three));
    }

    /**
     * 从照片选择或照相
     */
    private PhotoChoosePopUtil photoChoosePopUtil;
    // 调用系统相片 拍照
    private final int PHOTO_REQUEST_CAMERA = 1;
    // 从相册中选择
    private final int PHOTO_REQUEST_GALLERY = 2;

    private final int PHOTO_CUT = 3;
    private File tempFile;
    private File cutFile;
    private String mImgPath;

    private void showPhotoChoosePop() {
        if (null == photoChoosePopUtil) {
            photoChoosePopUtil = new PhotoChoosePopUtil(context);
        }
        photoChoosePopUtil.show(getWindow().getDecorView());
        photoChoosePopUtil.setOnPhotoChooseListener(new PhotoChoosePopUtil.OnPhotoChooseListener() {
            @Override
            public void onChooseCamera() {
                checkCamera();
            }

            @Override
            public void onChoosePhotograph() {
                checkFileWritePermissions();
            }
        });
    }

    private void checkCamera() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            camera();
                        } else {
                            com.blankj.utilcode.util.ToastUtils.showLong("no permission");
                        }
                    }
                });
    }

    /*
     * 从相机获取
     */
    public void camera() {
//        NetProgressObservable.getInstance().show(false);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (FileUtil.isSDExists()) {
            String date = String.valueOf(System.currentTimeMillis());
            tempFile = new File(FileUtil.getImageFile().getAbsolutePath(), date + ".jpeg");
            if (!tempFile.exists()) {
                FileUtil.createFile(tempFile.getAbsolutePath());
            }

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context.getApplicationContext(), getPackageName() + ".fileprovider", tempFile);
            } else {
                uri = Uri.fromFile(tempFile);
            }
            Log.e("NewShareActivity", "uri=" + uri.getPath());
            Log.e("NewShareActivity", "tempFile=" + tempFile.getName());
            String path = BitmapUtils.getRealFilePath(context, uri);
            Log.e("NewShareActivity", "tempFileRealFilePath" + path);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        }
    }

    @SuppressLint("CheckResult")
    private void checkFileWritePermissions() {
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            gallery();
                        } else {
                            ToastUtils.showToast(NewShareActivity.this, "open permission");
                        }
                    }
                });
    }

    /**
     * 从相册获取
     */
    public void gallery() {
//        NetProgressObservable.getInstance().show(false);
        Intent intent = new Intent(Intent.ACTION_PICK);    // 激活系统图库，选择一张图片
        //intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY); // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
    }

    /**
     * 调用系统裁剪
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        try {
            Logger.myLog(TAG,"------裁剪uri="+uri.getPath());
            int size = (int) (ScreenUtils.getScreenWidth() * 1.0);
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            }
            //裁剪后输出路径
            if (FileUtil.isSDExists()) {
                String date = String.valueOf(System.currentTimeMillis());
                //            mImgPath=getExternalFilesDir(null).toString()+ "/" + date + ".jpg";
                //            mImgPath= getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+ "/" + date + ".jpg";
                mImgPath = FileUtil.getImageFile().getAbsolutePath() + "/" + date + ".jpg";
                Logger.myLog(TAG,"-------11--mImgPath="+mImgPath);
                cutFile = new File(mImgPath);
                if (!cutFile.exists()) {
                    FileUtil.createFile(cutFile.getAbsolutePath());
                }

                Logger.myLog(TAG,"----cutFile="+cutFile.getAbsoluteFile());
                //所有版本这里都这样调用
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cutFile));
//               intent.putExtra(MediaStore.EXTRA_OUTPUT,  getUriForFile(NewShareActivity.this,cutFile));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,  uri);
                //输入图片路径
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                if (Build.MODEL.contains("VIE-AL10")) {
                    intent.putExtra("aspectX", 9998);
                    intent.putExtra("aspectY", 9999);
                } else {
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                }

                intent.putExtra("outputX", size);
                intent.putExtra("outputY", size);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("return-data", false);
                Logger.myLog(TAG,"-------222--mImgPath="+mImgPath);
//                uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/brandapp/" + "small.jpg");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
//                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, PHOTO_CUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri uritempFile;

    /**
     * 获取uri
     *
     * @param context
     * @param file
     * @return
     */
    public Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, "com.isport.brandapp" + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    private void setCustomBg(String filePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDrawable bd = new BitmapDrawable(ImageUtils.getBitmap(filePath));
                iv_sure1.setVisibility(View.GONE);
                iv_sure2.setVisibility(View.GONE);
                iv_sure3.setVisibility(View.GONE);
                fl_share_content.setBackground(bd);
                //setAlpha 0-255
                setBgColor(false);

                // fl_share_content.getBackground().mutate().setAlpha(153);
            }
        });


    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .init();
    }


}
