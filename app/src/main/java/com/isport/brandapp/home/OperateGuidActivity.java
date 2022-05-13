package com.isport.brandapp.home;


import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SafeBrowsingResponse;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;
import com.google.gson.Gson;
import com.isport.brandapp.R;
import com.isport.brandapp.login.IJsCallback;
import com.isport.brandapp.login.WebViewHelper;
import com.isport.brandapp.login.model.ResultWebData;
import java.util.concurrent.Callable;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonview.TitleBarView;

/**
 * 操作指引页面，直接加载H5
 * Created by Admin
 * Date 2022/5/9
 */
public class OperateGuidActivity extends BaseTitleActivity implements IJsCallback {


    private WebView operateWebview;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_operate_guid_layout;
    }

    @Override
    protected void initView(View view) {
        titleBarView.setTitle(getResources().getString(R.string.string_operate_guide_desc));



        operateWebview = findViewById(R.id.operateWebview);
        initSettings(operateWebview);

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                if(operateWebview.canGoBack()){
                    operateWebview.goBack();
                    return;
                }
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
    }

    @Override
    protected void initData() {
        String guidUrl = getIntent().getStringExtra("url");
        if(guidUrl != null){
            operateWebview.loadUrl(guidUrl);
        }


        operateWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            }
        });

        operateWebview.evaluateJavascript("javascript:MtPopUpList()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e("WWW","---value="+value);
            }
        });

        WebViewHelper.enableJs(operateWebview, this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        WebViewHelper.jsCallback(operateWebview, "oc_to_js()");
        WebViewHelper.jsCallback(operateWebview, "oc_to_js_training()");
    }

    private void initSettings(WebView mWebView) {
        if (mWebView != null) {

            mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
            mWebView.setVerticalScrollBarEnabled(false);//垂直不显示
            WebSettings webSettings = mWebView.getSettings();
            // 开启java script的支持
            webSettings.setJavaScriptEnabled(true);
            // mWebView.addJavascriptInterface(new mHandler(), "mHandler");
            // 启用localStorage 和 essionStorage
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setDisplayZoomControls(false);

            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局

            // 开启应用程序缓存
            webSettings.setAppCacheEnabled(true);
            webSettings.setSupportZoom(false);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
            webSettings.setAppCachePath(appCacheDir);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setDisplayZoomControls(false);

            webSettings.setPluginState(WebSettings.PluginState.ON);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

//		webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
            webSettings.setAllowFileAccess(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setJavaScriptEnabled(true);

            mWebView.setWebViewClient(mWebViewClient);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int scale = dm.densityDpi;
            if (scale == 240) { //
                mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (scale == 160) {
                mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            } else {
                mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            mWebView.setWebChromeClient(wvcc);
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            operateWebview.loadUrl(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.mHandler.show(document.body.innerHTML);");
            super.onPageFinished(view, url);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //circle_mainhtml_null.setVisibility(View.VISIBLE);

        }

        @Override
        public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {

            super.onSafeBrowsingHit(view, request, threatType, callback);
        }
    };
    WebChromeClient wvcc = new WebChromeClient() {


        @Override
        public void onReceivedTitle(WebView view, String titlet) {
            super.onReceivedTitle(view, titlet);

        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(operateWebview.canGoBack()){
                operateWebview.goBack();
                return true;
            }else{
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    public void postMessage(String data) {
        if(data == null)
            return;
        Gson gson = new Gson();
        Logger.e(TAG,"----data"+ data);
        ResultWebData webData = gson.fromJson(data, ResultWebData.class);
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
            }
        }
    }

    @Override
    public void moreList(String data) {

    }
}
