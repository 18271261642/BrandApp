package com.isport.brandapp.home

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.isport.brandapp.login.ActivityWebView

/**
 * Created by Admin
 *Date 2022/5/12
 */
class MyWebChromeClient(private val context: Context,
                        private val progressBar: ProgressBar,
                        private val frameFullScreen: FrameLayout) : WebChromeClient() {

    //用于保存系统提供的全屏视频所在的view
    //可将view添加到我们自己的视频播放锚点布局中
    private var mCustomView: View? = null
    private var mCustomViewCallback: CustomViewCallback? = null
    private val mainActivity:ActivityWebView = context as ActivityWebView
    //activity的根布局
    private val linearTotal = frameFullScreen.parent as LinearLayout
    //所有webView的显示区
    private val frameWebviews= linearTotal.getChildAt(linearTotal.childCount-1) as FrameLayout
    //当前所在webView进度处理
    // override fun onProgressChanged(view: WebView, newProgress: Int) {
    //     if (progressBar.visibility == View.GONE){
    //         progressBar.visibility = View.VISIBLE
    //     }
    //     progressBar.progress = newProgress
    //     if (newProgress == 100) progressBar.visibility = View.GONE
    // }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        //view为全屏时，系统提供的视频展示窗口
        //如果view 已经存在，则隐藏
        if (mCustomView != null) {
            callback?.onCustomViewHidden()
            return
        }
        mCustomView = view
        mCustomView?.visibility = View.VISIBLE
        mCustomViewCallback = callback
        frameFullScreen.addView(mCustomView)
        //隐藏主布局中所有子视图

        //仅显示视频锚点布局
        frameFullScreen.visibility = View.VISIBLE
        //frameFullScreen.bringToFront()

        //设置横屏
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onHideCustomView() {
        super.onHideCustomView()

        if (mCustomView == null) {
            return
        }
        mCustomView?.visibility = View.GONE
        frameFullScreen.removeView(mCustomView)
        mCustomView = null

        //还原webView的显示
        //具体实现是：先隐藏根布局下所有的子视图
        //只显示地址栏、工具栏及webView显示区
//        mainActivity.showView(frameWebviews)
        try {
            mCustomViewCallback!!.onCustomViewHidden()
        } catch (e: Exception) {
        }
        //竖屏
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


}