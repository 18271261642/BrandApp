package com.isport.brandapp.home;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.LogActivity;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.GlideCacheUtil;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import phone.gym.jkcq.com.socialmodule.util.CacheUtil;

public class SettingActivity extends BaseTitleActivity implements View.OnClickListener {
    ItemView itemViewFeedBack;
    ItemView itemview_clear_acache;
    ItemView itemViewAbout;
    ItemView log;
    TextView btnLoginOut;
    ItemView logoutItemView;

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_setting;
    }

    @Override
    protected void initView(View view) {

        itemview_clear_acache = view.findViewById(R.id.itemview_clear_acache);
        itemViewFeedBack = view.findViewById(R.id.itemview_feedback);
        itemViewAbout = view.findViewById(R.id.itemview_about);
        btnLoginOut = view.findViewById(R.id.btn_login_out);
        log = view.findViewById(R.id.log);
        log.setVisibility(View.GONE);
        logoutItemView = view.findViewById(R.id.logoutItemView);
        logoutItemView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        //c4a049fd79a25a83af46d9c135314c6d  c4a049fd79a25a83af46d9c135314c6d
        //2510540
        //FeedbackAPI.init(app, "23015524", "c4a049fd79a25a83af46d9c135314c6d");
        //25140601  94d0c175298ecae86b7a66783592b8eb

        //FeedbackAPI.init(app);
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(R.string.set);
        titleBarView.setRightText("");
        String size = GlideCacheUtil.getInstance().getCacheSize(this);
        itemview_clear_acache.setContentText(size);

        if (App.isHttp()) {
            btnLoginOut.setVisibility(View.VISIBLE);
            itemViewFeedBack.setVisibility(View.VISIBLE);
        } else {
            btnLoginOut.setVisibility(View.GONE);
            itemViewFeedBack.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvent() {
        itemViewFeedBack.setOnClickListener(this);
        itemViewAbout.setOnClickListener(this);
        btnLoginOut.setOnClickListener(this);
        log.setOnClickListener(this);

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
    }


    private  final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.friend_option_success));
                    String size = GlideCacheUtil.getInstance().getCacheSize(SettingActivity.this);
                    itemview_clear_acache.setContentText(size);
                    break;
            }
        }
    };

    @Override
    protected void initHeader() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemview_clear_acache:
                PublicAlertDialog.getInstance().showDialog("", getResources().getString(R.string.clear_cache), context, getResources().getString(R.string.cancel), getResources().getString(R.string.confirm), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {

                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                GlideCacheUtil.getInstance().clearImageAllCache(SettingActivity.this);
                                CacheUtil.deleteVideoCache();
                            }
                        });
                        handler.sendEmptyMessageDelayed(0x00, 1000);
                    }

                    @Override
                    public void cancel() {

                    }
                }, false);
                break;
            case R.id.itemview_feedback:
                FeedbackAPI.addErrorCallback(new FeedbackErrorCallback() {
                    @Override
                    public void onError(Context context, String errorMessage, ErrorCode code) {
                        Toast.makeText(context, "ErrMsg is: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                FeedbackAPI.addLeaveCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        Logger.d("DemoApplication", "custom leave callback");
                        return null;
                    }
                });


                FeedbackAPI.openFeedbackActivity();
                break;
            case R.id.itemview_about: {

                Intent intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.log:
                Intent intent = new Intent(context, LogActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_login_out:
                PublicAlertDialog.getInstance().cancelshowDialogWithContentAndTitledialog();
                PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.log_out_notice), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        if(DeviceTypeUtil.isContainF18()){
                            AppSP.putString(SettingActivity.this,AppSP.F18_SAVE_MAC,null);
                        }
                        ISportAgent.getInstance().disConDevice(false);
                        TokenUtil.getInstance().clear(context);
                        DeviceTypeUtil.clearDevcieInfo(context);
                        UserAcacheUtil.clearAll();
                        App.initAppState();
                        AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
                        Intent intent = new Intent(context, ActivityLogin.class);
                        startActivity(intent);
                        ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
                    }

                    @Override
                    public void cancel() {

                    }
                }, false);
                break;
            case R.id.logoutItemView:       //注销账号，实际上为退出登录
                PublicAlertDialog.getInstance().cancelshowDialogWithContentAndTitledialog();
                PublicAlertDialog.getInstance().showDialog("提醒", "是否注销账号?", context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        ISportAgent.getInstance().disConDevice(false);
                        TokenUtil.getInstance().clear(context);
                        DeviceTypeUtil.clearDevcieInfo(context);
                        UserAcacheUtil.clearAll();
                        App.initAppState();
                        AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
                        Intent intent = new Intent(context, ActivityLogin.class);
                        startActivity(intent);
                        ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
                    }

                    @Override
                    public void cancel() {

                    }
                }, false);
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

    }
}
