package com.isport.brandapp.device.f18;

import android.Manifest;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.isport.blelibrary.db.table.f18.listener.F18GetBatteryListener;
import com.isport.blelibrary.interfaces.OnF18DialStatusListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.brandapp.R;
import com.isport.brandapp.sport.run.LanguageUtil;
import com.isport.brandapp.upgrade.bean.DeviceUpgradeBean;
import com.isport.brandapp.upgrade.present.DevcieUpgradePresent;
import com.isport.brandapp.upgrade.view.DeviceUpgradeView;
import com.isport.brandapp.util.DownloadUtils;
import com.isport.brandapp.util.onDownloadListener;
import com.isport.brandapp.view.VerBatteryView;

import java.io.File;
import java.util.List;

import androidx.core.widget.NestedScrollView;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * F18固件升级页面
 * Created by Admin
 * Date 2022/2/18
 */
public class F18DufActivity extends BaseMVPTitleActivity<DeviceUpgradeView, DevcieUpgradePresent> implements View.OnClickListener,DeviceUpgradeView{


    //是否正在升级中，升级中屏蔽返回按键
    private boolean isOta = false;

    //电量
    private VerBatteryView iv_battery;
    //电量
    private TextView tv_battery;
    //手表当前版本
    private TextView tv_version;

   //设备图片
    private ImageView  iv_device_type_icon;
    //最新版本
    private TextView tv_new_version;
    //安装包大小
    private TextView tv_file_size;
    //更新日志
    private TextView tv_file_content;
    //开始更新
    private TextView tv_btn_state;
    //重试
    private TextView btn_try_again;
    //更新进度条
    private ProgressBar progress_value;
    //是否需要更新显示 最新版本
    private TextView tv_lastest_version;

    private NestedScrollView scrollView_layout;
    private RelativeLayout layout_bottom;

    String currDeviceVersion = null;


    //后台bin文件地址
    private String serverUrl;
    //保存下载的dfu文件的路径
    private String dufFoldPath;

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.tv_btn_state){   //下载状态按钮
            if(serverUrl == null)
                return;
            downloadDfuFile(serverUrl);
        }



        if(vId == R.id.btn_try_again){  //重试

        }

    }

    //固件返回
    @Override
    public void successDeviceUpgradeInfo(DeviceUpgradeBean deviceUpgradeBean) {
        dismissProgressBar();
        //后台配置的版本
        String netVersion = deviceUpgradeBean.getAppVersionName();
        Log.e(TAG,"-----网络获取版本信息="+deviceUpgradeBean.toString());
        isLastVersion(netVersion != null && netVersion.equals(currDeviceVersion),deviceUpgradeBean);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dfu_noti;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setTitle(UIUtils.getString(R.string.firmware_upgrade));
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
        iv_device_type_icon.setImageResource(R.drawable.icon_f18_device_set);


        XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }
        });

    }

    @Override
    protected void initData() {
        XXPermissions.with(this).permission(Manifest.permission.READ_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }
        });
        dufFoldPath = Environment.getExternalStorageDirectory().getPath()+"/Download/";

        if(Watch7018Manager.getWatch7018Manager().isConnected()){
            showProgress("loading...",true);
        }else{
            Toast.makeText(this,getResources().getString(R.string.disConnect),Toast.LENGTH_SHORT).show();
        }
        //先获取一下手表的当前电量
        Watch7018Manager.getWatch7018Manager().getDeviceBattery(new F18GetBatteryListener() {
            @Override
            public void deviceBattery(int batteryValue) {
                iv_battery.setProgress(batteryValue);
                tv_battery.setText(batteryValue+"%");
            }

            @Override
            public void deviceVersionName(String versionName) {
                currDeviceVersion = versionName;
                tv_version.setText(String.format(getResources().getString(R.string.firmware_device_version),versionName));
                //获取是否有新版本
                mActPresenter.getDeviceUpgradeInfo(JkConfiguration.DeviceType.Watch_F18);
            }
        });



    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected DevcieUpgradePresent createPresenter() {
        return new DevcieUpgradePresent(this);
    }


    private void findViews(){
        layout_bottom = findViewById(R.id.layout_bottom);
        scrollView_layout = findViewById(R.id.scrollView_layout);
        iv_device_type_icon = findViewById(R.id.iv_device_type_icon);
        tv_new_version = findViewById(R.id.tv_new_version);
        tv_file_size = findViewById(R.id.tv_file_size);
        tv_file_content = findViewById(R.id.tv_file_content);
        tv_btn_state = findViewById(R.id.tv_btn_state);
        btn_try_again = findViewById(R.id.btn_try_again);
        progress_value = findViewById(R.id.progress_value);
        tv_lastest_version = findViewById(R.id.tv_lastest_version);
        iv_battery = findViewById(R.id.iv_battery);
        tv_battery = findViewById(R.id.tv_battery);
        tv_version = findViewById(R.id.tv_version);

        iv_battery.setVisibility(View.VISIBLE);
        tv_version.setVisibility(View.VISIBLE);
        btn_try_again.setVisibility(View.VISIBLE);

        btn_try_again.setOnClickListener(this);
        tv_btn_state.setOnClickListener(this);

    }


    //是否是最新版本
    private void isLastVersion(boolean isLast,DeviceUpgradeBean deviceUpgradeBean){
        scrollView_layout.setVisibility(isLast ? View.GONE : View.VISIBLE);
        tv_lastest_version.setVisibility(isLast ? View.VISIBLE : View.GONE);
        layout_bottom.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);

        if(!isLast){
            this.serverUrl = deviceUpgradeBean.getUrl();
            layout_bottom.setVisibility(View.VISIBLE);

            tv_new_version.setText(deviceUpgradeBean.getAppVersionName());
            tv_file_size.setText((deviceUpgradeBean.getFileSize()/1000)+"kb");
            boolean isChinese = LanguageUtil.isZH();
            tv_file_content.setText(isChinese ? deviceUpgradeBean.getRemark() : deviceUpgradeBean.getRemarkEn());
            progress_value.setVisibility(View.VISIBLE);
            tv_btn_state.setVisibility(View.VISIBLE);
            btn_try_again.setVisibility(View.GONE);

        }
    }




    private void downloadDfuFile(String url){
        Log.e("TAG","-----下载="+url+" "+dufFoldPath+" ");
        DownloadUtils.getInstance().downBin(url, dufFoldPath, "f18_dfu.bin", new onDownloadListener() {
            @Override
            public void onStart(float length) {

            }

            @Override
            public void onProgress(float progress) {
                Log.e(TAG,"------onProgress="+progress);
                progress_value.setProgress((int) progress);
            }

            @Override
            public void onComplete() {
                Log.e(TAG,"------下载完成=");
                startToDfu(new File(dufFoldPath+"f18_dfu.bin").getAbsolutePath());
            }

            @Override
            public void onFail() {

            }
        });

    }

    private void startToDfu(String file){
        boolean isOtaRead = XXPermissions.isGranted(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(!isOtaRead){
            Toast.makeText(this,"文件读取无权限!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(file == null){
            Toast.makeText(this,"无升级文件!",Toast.LENGTH_SHORT).show();
            return;
        }
        isOta = true;
        Watch7018Manager.getWatch7018Manager().startOta(file, new OnF18DialStatusListener() {
            @Override
            public void startDial() {
                tv_btn_state.setText("开始升级中..");
            }

            @Override
            public void onError(int errorType, int errorCode) {
                tv_btn_state.setText("升级失败");
            }

            @Override
            public void onStateChanged(int state, boolean cancelable) {

            }

            @Override
            public void onProgressChanged(int progress) {
                tv_btn_state.setText("升级中");
                progress_value.setProgress((int) progress);
            }

            @Override
            public void onSuccess() {
                isOta = false;
                tv_btn_state.setText("升级成功");
            }
        });
    }



}
