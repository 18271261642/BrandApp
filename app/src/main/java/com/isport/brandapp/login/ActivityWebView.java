package com.isport.brandapp.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;
import com.google.gson.Gson;
import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.device.share.PackageUtil;
import com.isport.brandapp.login.model.ResultWebData;
import com.isport.brandapp.ropeskipping.realsport.RealRopeSkippingActivity;
import com.isport.brandapp.sport.bean.SportSumData;
import com.jkcq.train.ui.TrainVideoActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.base.BaseConstant;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class ActivityWebView extends BaseTitleActivity implements UMShareListener, View.OnClickListener, IJsCallback {
    /**
     * 标题
     */
    private TextView title;
    /**
     * 协议
     */
    private WebView tk_webview;
    private ProgressBar load_pro;
    private RelativeLayout circle_mainhtml_null;

    private String url;
    private String strTitle;
    private SportSumData sumData;
    private TextView tvShareCancle;

    RelativeLayout layoutShare;
    private LinearLayout layout_bottom;
    private LinearLayout layout_bottom_us;
    private View viewZh;
    private ImageView ivQQ, ivWechat, ivWebo, ivFriend, ivFacebook, ivShareMore;


    private FrameLayout video_fullView;

    View xCustomView;

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_user_agreement;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Logger.e("WB","------onCreate-----");
    }

    @Override
    protected void initView(View view) {
        tk_webview = view.findViewById(R.id.tk_webview);
        load_pro = view.findViewById(R.id.load_pro);

        video_fullView = view.findViewById(R.id.video_fullView);

        ivQQ = view.findViewById(R.id.iv_qq);
        ivWebo = view.findViewById(R.id.iv_weibo);
        ivWechat = view.findViewById(R.id.iv_wechat);
        ivFriend = view.findViewById(R.id.iv_friend);
        tvShareCancle = view.findViewById(R.id.tv_sharing_cancle);
        ivShareMore = view.findViewById(R.id.iv_more);
        viewZh = view.findViewById(R.id.view_zh);
        ivFacebook = view.findViewById(R.id.iv_facebook);
        layoutShare = view.findViewById(R.id.ll_history_share);
        layout_bottom = view.findViewById(R.id.layout_bottom);
    }

    String shareUrl = "";

    @Override
    protected void initData() {

        url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            finish();
        }

        strTitle = getIntent().getStringExtra("title");
       // Log.e(TAG,"---title="+getResources().getString(strTitle));
        shareUrl = getIntent().getStringExtra("share_url");
        sumData = (SportSumData) getIntent().getSerializableExtra("sumData");

        initSettings(tk_webview);
        tk_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);//开启硬件加速
        tk_webview.setVerticalScrollBarEnabled(false);
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(strTitle);
        titleBarView.setRightText("");
        tk_webview.loadUrl(url);

        if (!TextUtils.isEmpty(shareUrl)) {
            titleBarView.setRightIcon(R.drawable.icon_white_share);
        }

//        tk_webview.loadUrl(AllocationApi.BaseUrl + "/agreement/agreement.html");
     /*   if (NetUtils.hasNetwork(BaseApp.getApp())) {

//        http://192.168.10.203:8100/isport/concumer_basic/agreement/agreement.html
        } else {
            tk_webview.loadUrl("file:////android_asset/userAgreement.html");
        }*/
    }


    @Override
    protected void initEvent() {
        WebViewHelper.enableJs(tk_webview, this);

        ivQQ.setOnClickListener(this);
        ivWebo.setOnClickListener(this);
        ivWechat.setOnClickListener(this);
        ivFriend.setOnClickListener(this);
        tvShareCancle.setOnClickListener(this);
        ivShareMore.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                if(tk_webview.canGoBack()){
                    tk_webview.goBack();
                    return;
                }

                finish();
            }

            @Override
            public void onRightClicked(View view) {

                if (TextUtils.isEmpty(shareUrl)) {
                    return;
                }
                //显示分享的的操作
                layoutShare.setVisibility(View.VISIBLE);
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
        });
    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        video_fullView.removeAllViews();
        tk_webview.removeAllViews();
        destroyWebview();
    }


    /**
     * 解决Receiver not registered: android.widget.ZoomButtonsController
     */
    private void destroyWebview() {
        if (tk_webview != null) {
            tk_webview.getSettings().setBuiltInZoomControls(true);
            tk_webview.setVisibility(View.GONE); // 把destroy()延后


//            long timeout = ViewConfiguration.getZoomControlsTimeout();
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                	tk_webview.destroy();
//                }
//            }, timeout);
            tk_webview.post(new Runnable() {
                @Override
                public void run() {
                    tk_webview.destroy();
                }
            });
        }
    }

    //
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "NewApi"})
    private void initSettings(WebView mWebView) {
        WebSettings webSettings = mWebView.getSettings();
        // 开启java script的支持
        webSettings.setJavaScriptEnabled(true);
        // mWebView.addJavascriptInterface(new mHandler(), "mHandler");
        // 启用localStorage 和 essionStorage
        webSettings.setDomStorageEnabled(true);

        webSettings.setLoadWithOverviewMode(true);
        // 开启应用程序缓存
        webSettings.setAppCacheEnabled(false);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//		webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(mWebViewClient);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scale = dm.densityDpi;
        WebSettings set = mWebView.getSettings();
//        if (scale == 240) { //
//            set.setDefaultZoom(ZoomDensity.FAR);
//        } else if (scale == 160) {
//            set.setDefaultZoom(ZoomDensity.MEDIUM);
//        } else {
//            set.setDefaultZoom(ZoomDensity.CLOSE);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.setWebChromeClient(wvcc);


        //设置webview支持js
        // set.setJavaScriptEnabled(true);
        //设置本地调用对象及其接口
     /*   mWebView.addJavascriptInterface(new JsInteraction(), "js_to_oc");
        mWebView.addJavascriptInterface(new JsInteraction(), "change_Top_Right_Write");
        mWebView.addJavascriptInterface(new JsInteraction(), "change_Top_Left_Write");
        mWebView.addJavascriptInterface(new JsInteraction(), "change_Top_Green");
        mWebView.addJavascriptInterface(new JsInteraction(), "change_Content_Green");
        mWebView.loadUrl("http://192.168.10.15/healthManager/#/health");*/
        //webView.loadUrl("file:///android_asset/test.html");


        //  mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void postMessage(String data) {
        Gson gson = new Gson();
         Logger.e(TAG,"----data"+ data);
        ResultWebData webData = gson.fromJson(data, ResultWebData.class);
        Logger.e(TAG,"postMessage webData=" + webData);
        //Logger.e("data", "" + webData);
        if (webData != null) {

            if (webData.getType().equals("toRecallback")) {

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

                return;
            }

            if (webData.getType().equals("onlineCourse")) {
                Intent intent = new Intent(ActivityWebView.this, ActivityWebView.class);
                intent.putExtra("title", "线上课程");
                intent.putExtra("url", webData.getUrl());
                intent.putExtra("share_url", webData.getUrl());
                startActivity(intent);
            } else if (webData.getType().equals("playOnlineVideo")) {
                if (!TextUtils.isEmpty(webData.getVideoUrl())) {
                    if (webData.getVideoUrl().startsWith("http")) {
                        Intent intentVideo = new Intent(this, TrainVideoActivity.class);
                        intentVideo.putExtra(BaseConstant.EXTRA_VIDEO_URL, webData.getVideoUrl());
                        intentVideo.putExtra(BaseConstant.EXTRA_COURSE_ID, webData.getCourseId());
                        startActivity(intentVideo);
                        finish();
                    } else {
                        com.blankj.utilcode.util.ToastUtils.showShort("暂无视频");
                    }
                } else {
                    com.blankj.utilcode.util.ToastUtils.showShort("暂无视频");
                }

            } else {
                Intent intent = new Intent(this, RealRopeSkippingActivity.class);
                intent.putExtra("bean", webData.getItem());
                intent.putExtra("ropeSportType", JkConfiguration.RopeSportType.Challenge);
                startActivity(intent);
            }
        }

    }

    @Override
    public void moreList(String data) {

    }


    public class JsInteraction {
        @JavascriptInterface
        public void toastMessage(String message) {   //提供给js调用的方法
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }


    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (view.getProgress() == 100) {
                //  callJsFunc()
            }

        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {



            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            super.onUnhandledKeyEvent(view, event);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //circle_mainhtml_null.setVisibility(View.VISIBLE);
            tk_webview.setVisibility(View.GONE);
        }
    };
    WebChromeClient wvcc = new WebChromeClient() {


        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            tk_webview.setVisibility(View.INVISIBLE);


            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            video_fullView.addView(view);
            xCustomView = view;
         //   xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);


            //super.onShowCustomView(view, callback);
        }


        @Override
        public void onHideCustomView() {
            if (xCustomView == null)// 不是全屏播放状态
                return;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            video_fullView.removeView(xCustomView);
            xCustomView = null;
            video_fullView.setVisibility(View.GONE);
           // xCustomViewCallback.onCustomViewHidden();
            tk_webview.setVisibility(View.VISIBLE);


        }

        @Override
        public void onReceivedTitle(WebView view, String titlet) {
            super.onReceivedTitle(view, titlet);
//            strTitle = titlet;
            titleBarView.setTitle(titlet);

            Log.e(TAG,"-----webTitle="+titlet);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            String currentUrl = view.getUrl();
            shareUrl = currentUrl;
            if (sumData != null) {

            } else {
                if (!TextUtils.isEmpty(currentUrl) && currentUrl.contains("showShareBtn")) {
                    titleBarView.setRightIcon(R.drawable.icon_white_share);
                    // ivRight.setVisibility(View.VISIBLE);
                } else {
                    titleBarView.setRightIconVisible(false);
                    // ivRight.setVisibility(View.GONE);
                }
            }

            if (newProgress == 100) {
                load_pro.setVisibility(View.GONE);
            } else {
                if (load_pro.getVisibility() == View.GONE)
                    load_pro.setVisibility(View.VISIBLE);
                load_pro.setProgress(newProgress);
            }
        }



    };

    private void initview() {

//		title.setText(getString(R.string.dayhr_tk));
    }


    /**
     * 判断是否是全屏
     *
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        wvcc.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sharing_cancle:
                layoutShare.setVisibility(View.GONE);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.detail:
                Intent intent = new Intent(context, ActivityWebView.class);
                intent.putExtra("title", UIUtils.getString(R.string.sport_dtail));
                intent.putExtra("url", shareUrl);
                startActivity(intent);
                break;
            case R.id.iv_qq:

                if (PackageUtil.isWxInstall(ActivityWebView.this, PackageUtil.qqPakage)) {
                    share("qq", shareUrl);
                } else {
                    ToastUtil.showTextToast(ActivityWebView.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }

                break;
            case R.id.iv_friend:
                if (PackageUtil.isWxInstall(ActivityWebView.this, PackageUtil.weichatPakage)) {
                    share("friend", shareUrl);
                } else {

                    ToastUtil.showTextToast(ActivityWebView.this, UIUtils.getString(R.string.please_install_software));
                }

                break;
            case R.id.iv_wechat:
                if (PackageUtil.isWxInstall(ActivityWebView.this, PackageUtil.weichatPakage)) {
                    share("wechat", shareUrl);
                } else {

                    ToastUtil.showTextToast(ActivityWebView.this, UIUtils.getString(R.string.please_install_software));
                }

                break;
            case R.id.iv_weibo:
                if(!UMShareAPI.get(this).isInstall(this,SHARE_MEDIA.SINA)){
                    ToastUtil.showTextToast(ActivityWebView.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }
                share("weibo", shareUrl);
//
//                if (PackageUtil.isWxInstall(ActivityWebView.this, PackageUtil.weiboPakage)) {
//                    share("weibo", shareUrl);
//                } else {
//                    ToastUtil.showTextToast(ActivityWebView.this, UIUtils.getString(R.string.please_install_software));
//                    return;
//                }

                break;
            case R.id.iv_facebook:
                //判断facebook是否安装，没有按钮
                if (PackageUtil.isWxInstall(ActivityWebView.this, PackageUtil.facebookPakage)) {
                    share("facebook", shareUrl);
                } else {
                    ToastUtils.showToast(ActivityWebView.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }
                break;
            case R.id.iv_more:
                shareFile(shareUrl);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebViewHelper.jsCallback(tk_webview, "oc_to_js()");
        WebViewHelper.jsCallback(tk_webview, "oc_to_js_training()");
        layoutShare.setVisibility(View.GONE);
    }

    private void share(String type, String url) {
        layoutShare.setVisibility(View.GONE);
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
            case "facebook":
                share_media = SHARE_MEDIA.FACEBOOK;
                break;
        }
        UMWeb umWeb = new UMWeb(url);
        umWeb.setTitle(UIUtils.getString(R.string.share_title));
        if (sumData != null) {
            umWeb.setDescription(String.format(UIUtils.getString(R.string.share_content), sumData.getDistance(), sumData.getCalories()));
        } else {
            umWeb.setDescription(UIUtils.getString(R.string.share_course));
        }

        umWeb.setThumb(new UMImage(this, R.drawable.ic_t_launcher));
        new ShareAction(ActivityWebView.this).setPlatform(share_media)
                .withMedia(umWeb)
                .setCallback(ActivityWebView.this)
                .share();
    }


    public void shareFile(String url) {


        Intent share = new Intent(Intent.ACTION_SEND);
        Uri uri = null;
        //Platformutil.isInstallApp(context, PlatformUtil.PACKAGE_WECHAT);
        // 判断版本大于等于7.0
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, url);
        share.setType("text/plain");//此处可发送多种文件
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // share.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        context.startActivity(Intent.createChooser(share, "Share Image"));
    }

    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_QZONE = "com.qzone";
    public static final String PACKAGE_SINA = "com.sina.weibo";

    // 判断是否安装指定app
    public static boolean isInstallApp(Context context, String app_package) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

//            if(inCustomView()){
//                hideCustomView();
//                return true;
//            }

            if(tk_webview.canGoBack()){
                tk_webview.goBack();
                return true;
            }else{
                finish();
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
