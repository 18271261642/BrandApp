package com.isport.brandapp.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;
import com.isport.brandapp.R;
import com.isport.brandapp.util.DownloadUtils;
import com.isport.brandapp.util.onDownloadListener;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * 版本升级Dialog
 * Created by Admin
 * Date 2022/5/5
 */
public class AppUpdateDialogView extends AppCompatDialog implements View.OnClickListener , LifecycleOwner {

    private static final String TAG = "AppUpdateDialogView";

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    private  String DOWN_BIN_FILE_PATH = null;

    private TextView updateDialogLastVersionTv;
    private TextView updateDialogContentTv;
    private ProgressBar updateDialogProgressBar;

    private TextView updateDialogCancelBtn,updateDialogDownBtn;

    private TextView updateDialogStatusTv;

    private LinearLayout downloadLayout;

    //下载完毕，点击安装
    private TextView updateDialogInstallTv;

    private String downUrl;
    private String apkName;

    /** 当前是否下载完毕 */
    private boolean mDownloadComplete;

    //是否是强制更新
    private boolean isFocusUpdate;

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public AppUpdateDialogView(Context context) {
        super(context);
    }

    public AppUpdateDialogView(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_app_update_layout);

        findViews();
        Aria.download(this).register();
        DOWN_BIN_FILE_PATH = Environment.getExternalStorageDirectory().getPath()+"/Download/Apks/";
        String path = Environment.getExternalStorageDirectory().getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG,"---path="+path+"\n"+Environment.getRootDirectory()+"\n"+Environment.getDataDirectory()+"\n");
        }
    }


    private void findViews(){
        updateDialogInstallTv = findViewById(R.id.updateDialogInstallTv);
        updateDialogLastVersionTv = findViewById(R.id.updateDialogLastVersionTv);
        updateDialogContentTv = findViewById(R.id.updateDialogContentTv);
        updateDialogProgressBar = findViewById(R.id.updateDialogProgressBar);
        updateDialogCancelBtn = findViewById(R.id.updateDialogCancelBtn);
        updateDialogDownBtn = findViewById(R.id.updateDialogDownBtn);
        updateDialogStatusTv = findViewById(R.id.updateDialogStatusTv);

        downloadLayout = findViewById(R.id.downloadLayout);

        updateDialogCancelBtn.setOnClickListener(this);
        updateDialogDownBtn.setOnClickListener(this);
        updateDialogInstallTv.setOnClickListener(this);

        updateDialogProgressBar.setMax(100);
        updateDialogProgressBar.setVisibility(View.INVISIBLE);
    }


    //设置当前版本
    public void setLastVersion(String vStr){
        updateDialogLastVersionTv.setText(vStr);
    }

    //设置更新内容
    public void setContentMsg(String msg){
        updateDialogContentTv.setText(msg);
    }


    //是否强制更新
    public void isFocusUpdate(boolean isFocus){
        this.isFocusUpdate = isFocus;
        updateDialogCancelBtn.setVisibility(isFocus ? View.GONE : View.VISIBLE);
    }


    //设置下载的url和name
    public void setDownloadUrl(String url,String fileName){
        this.downUrl = url;
        this.apkName = fileName;
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.updateDialogCancelBtn){  //取消
            dismiss();
            if(onUpdateListener != null)
                onUpdateListener.onCancel();
        }

        if(vId == R.id.updateDialogDownBtn){
            //判断下载状态，
            updateDialogDownBtn.setVisibility(View.GONE);
            updateDialogCancelBtn.setVisibility(View.GONE);
           // updateDialogInstallTv.setVisibility(View.VISIBLE);
            setCancelable(false);
            startDown();

        }

        if(vId == R.id.updateDialogInstallTv){
            installApk();
        }
    }



    //下载
    private void startDown(){
        if(downUrl == null)
            return;
        updateDialogProgressBar.setVisibility(View.VISIBLE);
        EasyHttp.download(this)
                .method(HttpMethod.GET)
                .file(DOWN_BIN_FILE_PATH+apkName)
                .url(downUrl).listener(new OnDownloadListener() {
            @Override
            public void onStart(File file) {
                Log.e(TAG,"----start---="+file.getName());
                // 标记成未下载完成
                mDownloadComplete = false;
            }

            @Override
            public void onProgress(File file, int i) {
                updateDialogStatusTv.setText("下载中:"+i+"%");
                Log.e(TAG,"----onProgress---="+i);
                updateDialogProgressBar.setProgress(i);
            }

            @Override
            public void onComplete(File file) {
                updateDialogStatusTv.setText("下载完成");
                setCancelable(!isFocusUpdate);
                // 标记成未下载完成
                mDownloadComplete = true;

                updateDialogInstallTv.setVisibility(View.VISIBLE);
                Log.e(TAG,"----onComplete---="+file.getName());
                installApk();
            }

            @Override
            public void onError(File file, Exception e) {
                updateDialogStatusTv.setText("下载失败");

                updateDialogDownBtn.setVisibility(View.VISIBLE);
                updateDialogCancelBtn.setVisibility(isFocusUpdate ? View.GONE : View.VISIBLE);
                // updateDialogInstallTv.setVisibility(View.VISIBLE);

                Log.e(TAG,"----onError---="+file.getName()+" "+e.getMessage());
            }

            @Override
            public void onEnd(File file) {
                Log.e(TAG,"----onEnd---="+file.getName());
            }
        }).start();

    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }


    private void installApk() {
        File file = new File(DOWN_BIN_FILE_PATH+apkName);
        if(!file.isFile())
          return;
        if(!file.getName().equals(apkName))
            return;
        getContext().startActivity(getInstallIntent());
    }


    /**
     * 获取安装意图
     */
    private Intent getInstallIntent() {
        File file = new File(DOWN_BIN_FILE_PATH+apkName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    public interface OnUpdateListener{
        void onCancel();

    }

}
