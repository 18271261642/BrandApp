package com.isport.brandapp.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseTitleActivity;
import phone.gym.jkcq.com.commonres.common.AllocationApi;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;

public class ActivityprivacyAgreement extends BaseTitleActivity {
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


    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_user_agreement;
    }

    @Override
    protected void initView(View view) {
        tk_webview = view.findViewById(R.id.tk_webview);
        load_pro = view.findViewById(R.id.load_pro);

    }

    @Override
    protected void initData() {

        url = getIntent().getStringExtra("url");
        strTitle = getIntent().getStringExtra("title");
        initSettings(tk_webview);
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(strTitle);
        titleBarView.setRightText("");
        Logger.i("initData" + url);
        if (NetUtils.hasNetwork(BaseApp.getApp())) {
//            tk_webview.loadUrl(AllocationApi.BaseUrl + "/agreement/agreement.html");
           // tk_webview.loadUrl(url);
            tk_webview.loadUrl(AllocationApi.BaseUrl + "/isport/concumer-basic/agreement/privacyagreement.html");
//        http://192.168.10.203:8100/isport/concumer_basic/agreement/agreement.html
        } else {
            tk_webview.loadUrl("file:////android_asset/userAgreement.html");
        }
    }

    @Override
    protected void initEvent() {
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

    @Override
    protected void initHeader() {

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
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
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false);//垂直不显示
        WebSettings webSettings = mWebView.getSettings();
        // 开启java script的支持
        webSettings.setJavaScriptEnabled(true);
        // mWebView.addJavascriptInterface(new mHandler(), "mHandler");
        // 启用localStorage 和 essionStorage
        webSettings.setDomStorageEnabled(true);

        // 开启应用程序缓存
        webSettings.setAppCacheEnabled(false);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDisplayZoomControls(false);

//		webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(mWebViewClient);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scale = dm.densityDpi;
        if (scale == 240) { //
            mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
        } else if (scale == 160) {
            mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
        } else {
            mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.setWebChromeClient(wvcc);

        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.mHandler.show(document.body.innerHTML);");
            super.onPageFinished(view, url);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //circle_mainhtml_null.setVisibility(View.VISIBLE);
            tk_webview.setVisibility(View.GONE);
        }
    };
    WebChromeClient wvcc = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String titlet) {
            super.onReceivedTitle(view, titlet);

        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
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

}
