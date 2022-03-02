package com.isport.brandapp.upgrade;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.callback.CRPDeviceDfuAddressCallback;
import com.crrepa.ble.conn.listener.CRPBleFirmwareUpgradeListener;
import com.crrepa.ble.ota.hs.HsDfuController;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.DeviceInformationTableAction;
import com.isport.blelibrary.db.table.DeviceInformationTable;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_DeviceInfoModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_WearModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.BraceletW811W814Manager;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.device.bracelet.braceletPresenter.WearPresenter;
import com.isport.brandapp.device.bracelet.view.WearView;
import com.isport.brandapp.home.MainActivity;
import com.isport.brandapp.home.fragment.DFUGuidDialog;
import com.isport.brandapp.upgrade.bean.DeviceUpgradeBean;
import com.isport.brandapp.upgrade.present.DevcieUpgradePresent;
import com.isport.brandapp.upgrade.view.DeviceUpgradeView;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.DownloadUtils;
import com.isport.brandapp.util.onDownloadListener;
import com.isport.brandapp.view.VerBatteryView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.Arith;

/**
 * 固件升级页面
 */
public class DFUActivity extends BaseMVPTitleActivity<DeviceUpgradeView, DevcieUpgradePresent> implements View.OnClickListener, WearView, DeviceUpgradeView {


    String TAG = DFUActivity.class.getSimpleName();

    private NestedScrollView scrollView;
    LinearLayout layoutError;
    private TextView tv_lastest_version;
    private View view_bottom_line;
    private RelativeLayout layout_bottom;
    public static final String UPLOADING = "uploading";
    public boolean isUPLoading = false, isDownding = false;
    private int dfu_count = 0;
    //需要升级的类型
    private int deviceType;
    //需要升级的设备名称，mac
    private String deviceName, deviceMac, devcieCurrentMac;
    private File currentFile;
    String url = "";
    String fileName = "";
    long fileSize = -1;
    boolean isCommingSetting;
    HsDfuController hsDfuController;

    private TextView tvTryAgain;
    private TextView tvFileUpgradeContent;
    private WearPresenter wearPresenter;
    private String parentpath, pathVersion;
    private TextView tvVesion, tvBattery;
    private VerBatteryView iv_battery;

    private ImageView iv_device_type_icon;

    //ProgressButton buttonProgress;

    ProgressBar progressValue;
    TextView tvBtnState;

    Boolean isFisrtDis = false;


    final String download = "download";
    final String upgrade = "upgrade";
    final String error = "error";


    //获取当前的版本号和电量
    private String currentVersion = "";
    private int battery = 0;
    //获取服务器上的版本号
    private String serviceVersion = "";


    TextView tvFileSize;
    TextView tvNewVersion;

    //需要获取文件存储的权限


    LinearLayout layoutUpgrade;
    String upgradeDeviceName;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_dfu_noti;
    }

    @Override
    protected void initView(View view) {
        //TODO:如果不是DFU_MODE
        //TODO:需要去获取设备的电量，版本号
        AppSP.putString(context, AppSP.WATCH_MAC, "");
        layoutUpgrade = view.findViewById(R.id.layout_upgrade);
        iv_device_type_icon = view.findViewById(R.id.iv_device_type_icon);
        progressValue = view.findViewById(R.id.progress_value);
        tvFileUpgradeContent = view.findViewById(R.id.tv_file_content);
        tvBtnState = view.findViewById(R.id.tv_btn_state);
        tvFileSize = view.findViewById(R.id.tv_file_size);
        tvNewVersion = view.findViewById(R.id.tv_new_version);
        tv_lastest_version = view.findViewById(R.id.tv_lastest_version);
        scrollView = view.findViewById(R.id.scrollView_layout);
        view_bottom_line = view.findViewById(R.id.view_bottom_line);
        layout_bottom = view.findViewById(R.id.layout_bottom);
        tvTryAgain = view.findViewById(R.id.btn_try_again);
        layoutError = view.findViewById(R.id.layout_error);
        tvVesion = view.findViewById(R.id.tv_version);
        tvBattery = view.findViewById(R.id.tv_battery);
        iv_battery = view.findViewById(R.id.iv_battery);
        tvBtnState.setOnClickListener(this);
        getIntenValue();
        if (DeviceTypeUtil.isContainW81(deviceType)) {

        } else {
            DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        }

        //获取当前的版本号
    }


    private void setDeviceTypeIcon(int deviceType, String version, int power) {
        if (deviceType == JkConfiguration.DeviceType.WATCH_W516) {
            upgradeDeviceName = Constants.WATCH_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_upgrade_w516);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W556) {
            upgradeDeviceName = Constants.WATCH_556_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_upgrade_w526);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W520) {
            iv_device_type_icon.setImageResource(R.drawable.icon_w520_pic);
            upgradeDeviceName = Constants.BRAND_520_FILTER;
        } else if (deviceType == JkConfiguration.DeviceType.BRAND_W307J) {
            upgradeDeviceName = Constants.BRAND_W307J_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w307j_pic);
        } else if (deviceType == JkConfiguration.DeviceType.BRAND_W311) {
            upgradeDeviceName = Constants.BRAND_W311N_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w311_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W811) {
            upgradeDeviceName = Constants.BRAND_811_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w811_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W814) {
            upgradeDeviceName = Constants.BRAND_814_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w814_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W812) {
            upgradeDeviceName = Constants.WATCH_812_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w812_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W813) {
            upgradeDeviceName = Constants.WATCH_813_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w813_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W819) {
            upgradeDeviceName = Constants.WATCH_819_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w819_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W910) {
            upgradeDeviceName = Constants.WATCH_910_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w910_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W817) {
            upgradeDeviceName = Constants.WATCH_817_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w817_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W557) {
            upgradeDeviceName = Constants.WATCH_557_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w557_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W812B) {
            upgradeDeviceName = Constants.WATCH_812B_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w812b_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W560B) {
            upgradeDeviceName = Constants.WATCH_560B_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w560_pic);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W560) {
            upgradeDeviceName = Constants.WATCH_560_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_w560_pic);
        } else if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            upgradeDeviceName = Constants.ROPE_S002_FILTER;
            iv_device_type_icon.setImageResource(R.drawable.icon_scan_rope);
        }
        if (TextUtils.isEmpty(version)) {
            tvVesion.setVisibility(View.GONE);
        } else {
            tvVesion.setVisibility(View.VISIBLE);
            tvVesion.setText(version);

        }
        if (power == -1) {
            tvBattery.setVisibility(View.GONE);
            iv_battery.setVisibility(View.GONE);
        } else {
            tvBattery.setVisibility(View.VISIBLE);
            iv_battery.setVisibility(View.VISIBLE);
            iv_battery.setProgress(power);
            tvBattery.setText(power + "%");

        }


        //  uploadOnFailure();

    }

    private void getIntenValue() {
        deviceType = getIntent().getIntExtra(JkConfiguration.DEVICE_TYPE, JkConfiguration.DeviceType.WATCH_W516);
        deviceName = getIntent().getStringExtra(JkConfiguration.DEVICE_NAME);
        deviceMac = getIntent().getStringExtra(JkConfiguration.DEVICE_MAC);
        isCommingSetting = getIntent().getBooleanExtra("isCommingSetting", false);
        devcieCurrentMac = deviceMac;
    }


    private void isExistFile(Context context, FragmentActivity activity) {
        if (deviceType == JkConfiguration.DeviceType.WATCH_W516) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W556) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_556_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.BRAND_W311) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W520) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_520_FILTER);
            // file = FileUtil.getDeviceBinFile(Constants.BRAND_520_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.BRAND_W307J) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_W307J_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W814) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_814_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W811) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_811_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W812) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_812_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W813) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_813_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W819) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_819_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W910) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_910_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W817) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_817_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W557) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_557_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W812B) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_812B_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W560B) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_560B_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W560) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_560_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            parentpath = FileUtil.getDeviceBinFile(Constants.ROPE_S002_FILTER);
        }
        pathVersion = parentpath + File.separator + serviceVersion;


        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(activity),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        //需要判断文件存在并且文件大小与服务器上大小一样才不要去下载
                        Logger.myLog("location file size:" + FileUtil.getFileLenth(pathVersion + "/" + fileName) + "servcie file size:" + fileSize);
                        if (FileUtil.isFileExists(pathVersion + "/" + fileName) && FileUtil.getFileLenth(pathVersion + "/" + fileName) == fileSize) {
                            //直接去升级
                            tvBtnState.setText(R.string.device_upgrade);
                            tvBtnState.setTag(upgrade);
                            // uploadDevice();
                        }

                    }

                    @Override
                    public void onGetPermissionNo() {

                    }
                });
    }

    public void checkCameraPersiomm(Context context, FragmentActivity activity) {
        if (deviceType == JkConfiguration.DeviceType.WATCH_W516) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W556) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_556_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.BRAND_W311) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W520) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_520_FILTER);
            // file = FileUtil.getDeviceBinFile(Constants.BRAND_520_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.BRAND_W307J) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_W307J_FILTER);
            // file = FileUtil.getDeviceBinFile(Constants.BRAND_520_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W814) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_814_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Brand_W811) {
            parentpath = FileUtil.getDeviceBinFile(Constants.BRAND_811_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W812) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_812_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W813) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_813_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W819) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_819_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W910) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_910_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W817) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_817_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W557) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_557_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W812B) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_812B_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            parentpath = FileUtil.getDeviceBinFile(Constants.ROPE_S002_FILTER);
        } else if (deviceType == JkConfiguration.DeviceType.Watch_W560) {
            parentpath = FileUtil.getDeviceBinFile(Constants.WATCH_560B_FILTER);
        }
        pathVersion = parentpath + File.separator + serviceVersion;
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(activity),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        //需要判断文件存在并且文件大小与服务器上大小一样才不要去下载


                        Logger.myLog("location file size:" + FileUtil.getFileLenth(pathVersion + "/" + fileName) + "servcie file size:" + fileSize);
                        if (FileUtil.isFileExists(pathVersion + "/" + fileName) && FileUtil.getFileLenth(pathVersion + "/" + fileName) == fileSize) {
                            //直接去升级
                            uploadDevice();
                        } else {
                            ////需求去本地查询是否存在文件如果存在就不需要再去下载
                            //需要把类型下的问题件包都删除
                            if (!NetUtils.hasNetwork()) {
                                ToastUtils.showToast(context, R.string.common_please_check_that_your_network_is_connected);
                                return;
                            }
                            FileUtil.deleteDir(new File(parentpath));
                            DownloadUtils.getInstance().downBin(url, pathVersion, fileName, new onDownloadListener() {
                                @Override
                                public void onStart(float length) {
                                    tvBtnState.setEnabled(false);
                                    progressValue.setProgress(0);
                                    // isDownding = true;
                                }

                                @Override
                                public void onProgress(float progress) {
                                    tvBtnState.setEnabled(false);
                                    progressValue.setProgress((int) (progress * 100));
                                    tvBtnState.setText(String.format(UIUtils.getString(R.string.file_downlod_present), (int) (progress * 100)));
                                }

                                @Override
                                public void onComplete() {

                                    tvStateEnabled(true);
                                    //tvBtnState.setEnabled(true);
                                    tvBtnState.setText(UIUtils.getString(R.string.device_upgrade));
                                    tvBtnState.setTag(upgrade);
                                    showUpgradeDialog(UIUtils.getString(R.string.file_downlod_success_tips));

                                }

                                @Override
                                public void onFail() {
                                    tvBtnState.setEnabled(true);
                                    progressValue.setProgress(100);
                                    tvBtnState.setText(UIUtils.getString(R.string.try_again));
                                    tvBtnState.setTag(download);
                                    downloadFail();
                                }
                            });

                        }

                    }

                    @Override
                    public void onGetPermissionNo() {
                        ToastUtils.showToastLong(context, UIUtils.getString(R.string.location_permissions));
                    }
                });

    }


    private void showDeviceUpdateSuccess() {
        if (!isFinishing()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SyncCacheUtils.clearSetting(BaseApp.getApp());
                    SyncCacheUtils.clearStartSync(BaseApp.getApp());
                    SyncCacheUtils.clearSysData(BaseApp.getApp());
                    BleAction.deletDeviceInfo();
                    SyncCacheUtils.clearDFUMode(BaseApp.getApp(), deviceMac);
                    isUPLoading = false;
                    Constants.isDFU = false;
                    tvBtnState.setText(UIUtils.getString(R.string.device_upgrade_success));
                    AppSP.putString(context, AppSP.WATCH_MAC, "");
                    SyncCacheUtils.clearSetting(context);
                    // SyncCacheUtils.clearStartSync();


                    tvBtnState.setEnabled(false);
                    PublicAlertDialog.getInstance().showDialogNoCancle(false, "", UIUtils.getString(R.string.device_upgrade_success_tips), DFUActivity.this, UIUtils.getString(R.string.back_home), new AlertDialogStateCallBack() {
                        @Override
                        public void determine() {
                            Constants.CAN_RECONNECT = false;
                            if (deviceType == 813 || deviceType == 814) {
                                // ActivityManager.getInstance().finishAllActivity();
                                ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
                            } else {
                                ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
                            }
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }, 0);

        }
    }

    private void showUpgradeDialog(String tips) {
        PublicAlertDialog.getInstance().showDialogWithContentAndTitle("", tips, DFUActivity.this, UIUtils.getString(R.string.yes), UIUtils.getString(R.string.no), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
                //去升级固件
                //如果不是DFU模式，需要去判断电量，是否是连接状态，如果是连接状态需要去判断电量是否大于40%
                tvBtnState.setTag(upgrade);
                uploadDevice();


            }

            @Override
            public void cancel() {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getVersionOrBattery() {
        //是W516的设备信息
        if (DeviceTypeUtil.isContainWatch(deviceType) || DeviceTypeUtil.isContainW81(deviceType) || DeviceTypeUtil.isContainRope(deviceType)) {

            DeviceInformationTable deviceInfoByDeviceId = DeviceInformationTableAction.findDeviceInfoByDeviceId
                    (deviceName);
            if (deviceInfoByDeviceId != null) {
                currentVersion = TextUtils.isEmpty(deviceInfoByDeviceId.getVersion()) ? "" : deviceInfoByDeviceId.getVersion();
                if (TextUtils.isEmpty(currentVersion)) {
                    setDeviceTypeIcon(deviceType, "", -1);
                } else {

                    if (currentVersion.contains("V")) {
                        currentVersion = currentVersion.replace("V", "");
                    }
                    if (currentVersion.contains("v")) {
                        currentVersion = currentVersion.replace("v", "");
                    }
                    String strFirmwareVersion = String.format(getResources().getString(R.string.firmware_device_version), currentVersion);
                    battery = deviceInfoByDeviceId.getBattery();
                    String strPower = String.format(getResources().getString(R.string.firmware_device_battery), battery + "");
                    setDeviceTypeIcon(deviceType, strFirmwareVersion, battery);
                }
            } else {
                currentVersion = "";
                battery = 0;
                setDeviceTypeIcon(deviceType, "", -1);
            }
        } else if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
            setDeviceTypeIcon(deviceType, "", -1);
            wearPresenter.getDeviceInfo(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceName);
        } else {
            setDeviceTypeIcon(deviceType, "", -1);
            currentVersion = "";
            battery = 0;
        }
    }

    String info;
    CRPBleDevice mBleDevice = null;
    CRPBleConnection mBleConnection = null;

    @Override
    protected void initData() {
        setShowUpdateContent(true);
        isFisrtDis = false;
        tryCount = 0;
        info = TAG + " brand:" + Build.BRAND + ",model:" + Build.MODEL
                + ",sdkLevel:" + Build.VERSION.SDK_INT + ",release:" + Build.VERSION.RELEASE + ",thread:" + Thread.currentThread().getName();

        FileUtil.initFile(BaseApp.getApp());
        // checkStoragePersiomm();
        mActPresenter.getDeviceUpgradeInfo(deviceType);
        // checkCameraPersiomm(DFUActivity.this, DFUActivity.this);
        progressValue.setMax(100);
        progressValue.setProgress(100);
        titleBarView.setTitle(UIUtils.getString(R.string.firmware_upgrade));
        wearPresenter = new WearPresenter(this);
        getVersionOrBattery();
        PublicAlertDialog.getInstance().clearShowDialog();
    }

    @Override
    protected void initEvent() {

        //tag 1.需要下载文件 download;
        //tag 2.更新固件包  upgurade;

        ISportAgent.getInstance().registerListener(mBleReciveListener);
        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActPresenter.getDeviceUpgradeInfo(deviceType);
            }
        });


        tvBtnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.myLog("tvBtnState.getTag():" + tvBtnState.getTag());
                if (tvBtnState.getTag() == null || tvBtnState.getTag().equals(download)) {
                    checkCameraPersiomm(DFUActivity.this, DFUActivity.this);

                } else if (tvBtnState.getTag().equals(upgrade)) {

                    showUpgradeDialog(UIUtils.getString(R.string.device_upgrade_sure));
                    // upload(deviceName, deviceMac);

                }
            }
        });
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                //TODO 在升级的过程中是不可返回的
                goBack();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void initHeader() {

    }


    private BleReciveListener mBleReciveListener = new BleReciveListener() {

        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            Logger.myLog("DFUACtivity ,isConn:" + isConn + "baseDevice:" + baseDevice + "Constants.isDFU :" + Constants.isDFU + "baseDevice.deviceType:" + baseDevice.deviceType + "currentFile:" + currentFile);
            if (!isConn) {
                if (null != baseDevice && Constants.isDFU && Utils.isDialogUpgrade(baseDevice.deviceType)) {
                    upgradeW813W814(deviceMac, currentFile, 3000);
                    //  upgradeW813W814(deviceMac, currentFile, 10000);
                }
            }


        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {

        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {

        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {
            Logger.myLog("onBattreyOrVersion");
            getVersionOrBattery();
        }
    };

    Handler handler = new Handler();

    public boolean upload(String deviceName, String deviceAddress) {

        if (TextUtils.isEmpty(deviceName) || TextUtils.isEmpty(deviceAddress)) {
            ToastUtils.showToast(BaseApp.getApp(), UIUtils.getString(R.string.re_scan_devcie));
            return false;
        }
        Constants.isDFU = true;
        isDfuMode = false;


        tvBtnState.setText(String.format(UIUtils.getString(R.string.device_upgrade_present), 0));
        progressValue.setProgress(0);
        dfu_count++;

        //cancelScan();
//        if (mFilePath == null || !(new File(mFilePath).exists())) {
//            Toast.makeText(this, getString(R.string.file_is_not_exist), Toast.LENGTH_LONG).show();
//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//            return false;
//        }
        mBleDevice = BraceletW811W814Manager.getInstance().getmBleDevice();
        mBleConnection = BraceletW811W814Manager.getInstance().getmBleConnection();


        if (deviceType == JkConfiguration.DeviceType.Watch_W812) {
            Logger.myLog("DFUActivity" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion);

            if (TextUtils.isEmpty(currentVersion) || TextUtils.isEmpty(fileName)) {
                return false;
            }

            String name = currentVersion.substring(0, 7);
            String upgradeName = fileName.substring(0, 7);
            Logger.myLog("DFUActivity" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion + ",name:" + name + ",upgradeName:" + upgradeName);
            File file = new File(pathVersion + "/" + fileName);
            if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
               // mBleConnection.startFirmwareUpgrade(crpBleFirmwareUpgradeListener, file, currentVersion);
                mBleConnection.startFirmwareUpgrade(false,file,currentVersion,crpBleFirmwareUpgradeListener);
            }
            return true;

        } else if (deviceType == JkConfiguration.DeviceType.Watch_W817) {
            Logger.myLog("DFUActivity" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion);

            if (TextUtils.isEmpty(currentVersion) || TextUtils.isEmpty(fileName)) {
                return false;
            }


            Logger.myLog("DFUActivity" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion);

            File file = new File(pathVersion + "/" + fileName);
            if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
               // mBleConnection.startFirmwareUpgrade(crpBleFirmwareUpgradeListener, file, currentVersion);
                mBleConnection.startFirmwareUpgrade(false,file,currentVersion,crpBleFirmwareUpgradeListener);
            }
            return true;

        } else if (deviceType == JkConfiguration.DeviceType.Watch_W910) {
            Logger.myLog("DFUActivity" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion);

            if (TextUtils.isEmpty(currentVersion) || TextUtils.isEmpty(fileName)) {
                return false;
            }
            File file = new File(pathVersion + "/" + fileName);
            //DEVICE_STATUS_NORMAL   DEVICE_STATUS_DFU
               /* if (true) {
                    return true;
                }*/
            Logger.myLog("DFUActivity" + "BraceletW811W814Manager.mBleDevice.isConnected()" + mBleDevice.isConnected());
            if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
              //  mBleConnection.startFirmwareUpgrade(crpBleFirmwareUpgradeListener, file, currentVersion);

                mBleConnection.startFirmwareUpgrade(false, file, currentVersion,crpBleFirmwareUpgradeListener);
            }
            return true;

        } else if (DeviceTypeUtil.isContainW814W813W819(deviceType)) {
            Logger.myLog("DFUActivity" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion);
            File file = new File(pathVersion + "/" + fileName);
            Logger.myLog("DFUActivity Watch_W813" + "path:" + pathVersion + "/" + fileName + "currentVersion:" + currentVersion);
            if (mBleDevice != null && mBleDevice.isConnected() && mBleConnection != null) {
                mBleConnection.queryHsDfuAddress(new CRPDeviceDfuAddressCallback() {
                    @Override
                    public void onAddress(String s) {
                        Logger.myLog("DFUActivity: queryHsDfuAddress:" + s + "BraceletW811W814Manager.mBleDevice.isConnected()" + mBleDevice.isConnected());
                        if (mBleDevice.isConnected()) {
                            startDFUMode();
                            mBleConnection.enableHsDfu();
                            deviceMac = s;
                            currentFile = file;
                            //  upgradeW813W814(s, file, 3000);
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.unconnected_device));
                                }
                            });

                        }

                    }
                });
            } else {
                upgradeW813W814(deviceMac, file, 50);
            }
            //   }


            // }

            return true;
        } else {

            Log.e(TAG, "upload操作展示弹窗" + "deviceName:" + deviceName + "deviceAddress:" + deviceAddress);
            final DfuServiceInitiator starter = new DfuServiceInitiator(deviceAddress)
                    .setDeviceName(deviceName)
                    .setKeepBond(false)
                    .setForceDfu(false)
                    .setPacketsReceiptNotificationsEnabled(true)
                    .setPacketsReceiptNotificationsValue(6)
                    .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
            starter.disableResume();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                starter.createDfuNotificationChannel(this);
            }
            Logger.myLog("path:" + pathVersion + "/" + fileName);
            starter.setZip(pathVersion + "/" + fileName);
            starter.start(DFUActivity.this, DfuService.class);
            return true;
        }
    }


    public void startDFUMode() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Constants.isDFU = true;
                Constants.CAN_RECONNECT = false;
                isComplety = false;
                //小米需要重新去扫描，把扫描到的DFU设备
                tvBtnState.setEnabled(false);
                //progressValue.setProgress(0);
                isUPLoading = true;
            }
        });

    }


    public void upgradeW813W814(String strmac, File strfile, int timeout) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (strfile != null) {
                    Logger.myLog("DFUActivity mac:" + strmac + "File:" + strfile.getAbsolutePath());
                    hsDfuController = new HsDfuController();
                    hsDfuController.setUpgradeListener(crpBleFirmwareUpgradeListener);
                    hsDfuController.setAddress(strmac);
                    /**
                     * userChecked 会更新手环UI，一般为false
                     * pathChedked  汉天下A3芯片传入true， A4芯片传入false
                     * */
                    hsDfuController.start(strfile, false, true);
                }
            }
        }, timeout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    boolean isDfuMode = false;

    boolean isComplety;
    int tryCount = 0;
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            Logger.myLog("DFUActivity onDeviceConnecting deviceAddress = " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {


            startDFUMode();

            //小米需要重新去扫描，把扫描到的DFU设备
            progressValue.setProgress(0);
            Logger.myLog("DFUActivity onDfuProcessStarting deviceAddress = " + deviceAddress + "connect  phone info:" + info);
            Constants.isDFU = true;
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            //自己的连接不要去重连
            //已经到了升级模式
            isDfuMode = true;
            if (Build.BRAND.equals("Xiaomi")) {
                if (tryCount == 0) {
                    loadHandler.sendEmptyMessageDelayed(0x02, 30000);
                } else {
                    loadHandler.sendEmptyMessageDelayed(0x03, 30000);
                }
                //
            }
            startDFUMode();

            Logger.myLog("DFUActivity onEnablingDfuMode deviceAddress = " + deviceAddress);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            Logger.myLog("DFUActivity onFirmwareValidating deviceAddress = " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            //  super.onDeviceDisconnected(deviceAddress);
            /*uploadOnFailure();
            isUPLoading = false;
            Logger.myLog("DFUActivity OTA 终止了 = ");
            Constants.isDFU = false;*/
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            //升级成功了也会调用一次这个函数


            isUPLoading = false;
            //uploadOnFailure();
            Logger.myLog("DFUActivity onDeviceDisconnecting deviceAddress" + deviceAddress);
//            if(progressDialog != null && progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            ///OTA 升级完成
            loadHandler.removeCallbacks(null);

            Logger.myLog("DFUActivity onDfuCompleted deviceAddress" + deviceAddress);

            stopService(new Intent(DFUActivity.this, DfuService.class));
            if (!isFinishing()) {
                showDeviceUpdateSuccess();
            }


        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            //升级失败
            uploadOnFailure();

            Logger.myLog("DFUActivity OTA 终止了 = ");
            // TODO: 2018/6/30  终止了，重试
            // uploadOnFailure();
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            //红米手机升级到了100不走完成的回调
            Logger.myLog("DFUActivity OTA onProgressChanged deviceAddress = " + deviceAddress + " percent = " + percent + " speed = " + avgSpeed + ",isUPLoading:" + isUPLoading);
            onDeviceUpgradeProgressChanged(percent);

        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            Logger.myLog("DFUActivity OTA onError error = " + error + " errorType = " + " message = " + message);
            uploadOnFailure();
        }
    };

    boolean isError = false;

    private void uploadOnFailure() {
        abortDFU();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Logger.myLog("isDfuMode:" + isDfuMode);
                if (isFinishing()) {
                    return;
                }
                if (true) {
                    if (isComplety || isError) {
                        return;
                    }
                    isError = true;
                    tvStateEnabled(true);
                    tvBtnState.setText(UIUtils.getString(R.string.device_upgrade_fail));
                    progressValue.setProgress(0);
                    String message = "";

                    try {
                        if (deviceType == JkConfiguration.DeviceType.Brand_W814 || deviceType == JkConfiguration.DeviceType.Watch_W813 || deviceType == JkConfiguration.DeviceType.Watch_W819) {
                            message = String.format(UIUtils.getString(R.string.device_upgrade_fail_huntersun_tips), upgradeDeviceName);
                        } else {
                            message = String.format(UIUtils.getString(R.string.device_upgrade_fail_dfu_tips), upgradeDeviceName);
                            //  message = UIUtils.getString(R.string.device_upgrade_fail_tips);
                        }
                    } catch (Exception e) {

                    }

                    PublicAlertDialog.getInstance().showDialogWithContentAndTitle("", message, DFUActivity.this, UIUtils.getString(R.string.yes), UIUtils.getString(R.string.no), new AlertDialogStateCallBack() {
                        @Override
                        public void determine() {
                            //TODO 跳转到扫描页面
                            ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }
        });


        /*   } else {*/
         /*   tvStateEnabled(true);
            showUpgradeDialog(UIUtils.getString(R.string.re_upgrade));*/
        //连接失败请重试


    }


    public void tvStateEnabled(boolean isEabled) {
        isUPLoading = false;
        Constants.isDFU = false;
        loadHandler.removeCallbacksAndMessages(null);
        if (isEabled) {
            tvBtnState.setEnabled(true);
            progressValue.setProgress(100);
        } else {
            tvBtnState.setEnabled(false);
            progressValue.setProgress(0);
        }
    }

    @Override
    public void successWearItem(Bracelet_W311_WearModel bracelet_w311_wearModel) {

    }

    @Override
    public void successWearItem() {

    }

    @Override
    public void getDeviceInfo(DeviceInformationTable deviceInfoModel) {

    }

    @Override
    public void getDeviceInfo(Bracelet_W311_DeviceInfoModel deviceInfoModel) {

        if (deviceInfoModel != null) {
            currentVersion = deviceInfoModel.getFirmwareHighVersion() + "." + deviceInfoModel.getFirmwareLowVersion();
            if (TextUtils.isEmpty(currentVersion)) {
                setDeviceTypeIcon(deviceType, "", -1);
            } else {
                String strFirmwareVersion = String.format(getResources().getString(R.string.firmware_device_version), currentVersion);
                battery = deviceInfoModel.getPowerLevel();
                String strPower = String.format(getResources().getString(R.string.firmware_device_battery), battery + "");
                setDeviceTypeIcon(deviceType, strFirmwareVersion, battery);
            }
        } else {
            setDeviceTypeIcon(deviceType, "", -1);
            currentVersion = "";
            battery = 0;
        }
    }

    @Override
    public void onRespondError(String message) {

        //  ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
        netError();
    }

    public void abortDFU() {
        Logger.myLog(TAG + "abortDFU");
        if (DeviceTypeUtil.isContainW814W813W819(deviceType) && hsDfuController != null) {
            hsDfuController.abort();
            hsDfuController = null;
            if (!isCommingSetting) {
                ISportAgent.getInstance().disConDevice(false);
            }
            //ISportAgent.getInstance().disConDevice(false);
        } else if (null != mBleDevice && null != mBleConnection && mBleDevice.isConnected() && DeviceTypeUtil.isContainW812W817W819(deviceType)) {
            mBleConnection.abortFirmwareUpgrade();
            if (!isCommingSetting) {
                ISportAgent.getInstance().disConDevice(false);
            }
        }
    }

    @Override
    protected DevcieUpgradePresent createPresenter() {
        return new DevcieUpgradePresent(this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (DeviceTypeUtil.isContainW81(deviceType)) {
            abortDFU();
        } else {
            //如果是直接给返回没有做任何的操作
            stopService(new Intent(this, DfuService.class));
            //如果是从设置页面过来不需要断开，
            //如果从别的地方过来需要断开
            if (!isCommingSetting) {
                ISportAgent.getInstance().disConDevice(false);
            }
            //
            DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        }

        PublicAlertDialog.getInstance().clearShowDialog();
        loadHandler.removeCallbacksAndMessages(null);
        Constants.isDFU = false;
        AppSP.putString(context, AppSP.WATCH_MAC, "");
        AppSP.putString(context, AppSP.FORM_DFU, "true");
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        handler.removeCallbacksAndMessages(null);

    }

    public void showBatteryLowDialog() {
        PublicAlertDialog.getInstance().showDialogNoCancle(false, UIUtils.getString(R.string.tips), UIUtils.getString(R.string.devcie_upgrade_battery_tips), this, UIUtils.getString(R.string.tips_ok), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
            }

            @Override
            public void cancel() {

            }
        });
    }


    @Override
    public void successDeviceUpgradeInfo(DeviceUpgradeBean deviceUpgradeBean) {
        Logger.myLog("successDeviceUpgradeInfo:" + deviceUpgradeBean);
        if (deviceUpgradeBean != null) {
            url = TextUtils.isEmpty(deviceUpgradeBean.getUrl()) ? "" : deviceUpgradeBean.getUrl();
            fileSize = deviceUpgradeBean.getFileSize();
            serviceVersion = TextUtils.isEmpty(deviceUpgradeBean.getAppVersionName()) ? "" : deviceUpgradeBean.getAppVersionName();
            //serviceVersion = "00.00.46";
            // "url": "https://isportcloud.oss-cn-shenzhen.aliyuncs.com/device/w516_v0045.zip"
            fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
            if (deviceUpgradeBean.getFileSize() > 1024 * 1024) {
                tvFileSize.setText(CommonDateUtil.formatTwoPoint(Arith.div(deviceUpgradeBean.getFileSize(), 1024 * 1024, 2)) + "M");
            } else {
                tvFileSize.setText(CommonDateUtil.formatTwoPoint(Arith.div(deviceUpgradeBean.getFileSize(), 1024, 2)) + "KB");
            }
            tvNewVersion.setText(String.format(UIUtils.getString(R.string.app_device_version), deviceUpgradeBean.getAppVersionName()));
            if (AppUtil.isZh(BaseApp.getApp())) {
                //String temp =
                tvFileUpgradeContent.setText(deviceUpgradeBean.getRemark().replace("\\n", "\n"));
            } else {
                //String temp = ;
                tvFileUpgradeContent.setText(deviceUpgradeBean.getRemarkEn().replace("\\n", "\n"));
            }
            //对比是否是最新版本，如果是最新版就不需要下载，如果不是最新版就需要去下载文件

            if (Utils.isContainsDFU(deviceName)) {
                //checkCameraPersiomm(this, this);
                tvBtnState.setTag(download);
                tvBtnState.setText(UIUtils.getString(R.string.file_download));
                setShowUpdateContent(false);
                isExistFile(this, this);


            } else {

                Logger.myLog(TAG,"------固件版本="+serviceVersion+"currV="+currentVersion+" "+serviceVersion.contains(currentVersion));

                if(serviceVersion.toLowerCase(Locale.ROOT).contains("v")){
                    serviceVersion = serviceVersion.replace("v","").trim();
                }
//                if(currentVersion.toLowerCase(Locale.ROOT).contains("v")){
//                    currentVersion = currentVersion.replace("v","").trim();
//                }
                if (serviceVersion.equals(currentVersion) || serviceVersion.contains(currentVersion)) {
                    //if (false) {
                    //已经是最新版本
                    setShowUpdateContent(true);
                } else {
                    tvBtnState.setText(UIUtils.getString(R.string.file_download));
                    setShowUpdateContent(false);
                    //checkCameraPersiomm(this, this);
                    tvBtnState.setTag(download);
                    isExistFile(this, this);
                    if (deviceType == JkConfiguration.DeviceType.Watch_W813) {
                        DFUGuidDialog guidDialog = new DFUGuidDialog(DFUActivity.this, deviceType);
                    } else if (deviceType == JkConfiguration.DeviceType.Brand_W814) {
                        DFUGuidDialog guidDialog = new DFUGuidDialog(DFUActivity.this, deviceType);
                    }
                }
            }
        } else {
            //TODO 服务器异常显示
            netError();
        }

    }

    public void netError() {
        // tvBtnState.setTag("error");
        tvTryAgain.setVisibility(View.VISIBLE);
        tvBtnState.setVisibility(View.GONE);
        progressValue.setVisibility(View.GONE);
        tvBtnState.setText(UIUtils.getString(R.string.try_again));
        layoutError.setVisibility(View.VISIBLE);
        tv_lastest_version.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        view_bottom_line.setVisibility(View.VISIBLE);
        layout_bottom.setVisibility(View.VISIBLE);
    }


    private void setShowUpdateContent(boolean isLastestVersion) {
        tvBtnState.setVisibility(isLastestVersion ? View.GONE : View.VISIBLE);
        progressValue.setVisibility(isLastestVersion ? View.GONE : View.VISIBLE);
        tvTryAgain.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        tv_lastest_version.setVisibility(isLastestVersion ? View.VISIBLE : View.GONE);
        scrollView.setVisibility(isLastestVersion ? View.GONE : View.VISIBLE);
        view_bottom_line.setVisibility(isLastestVersion ? View.GONE : View.VISIBLE);
        layout_bottom.setVisibility(isLastestVersion ? View.GONE : View.VISIBLE);
    }

    private void uploadDevice() {
        if (!AppUtil.isOpenBle()) {
            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.bluetooth_is_not_enabled));
            return;
        }
        if (Utils.isContainsDFU(deviceName)) {
            upload(deviceName, deviceMac);
        } else {
            if (battery < 40) {
                //upload(deviceName, deviceMac);
                showBatteryLowDialog();
            } else {
                Logger.myLog("deviceName=" + deviceName + "deviceMac=" + deviceMac);
                upload(deviceName, deviceMac);
            }
            // 直接升级
        }

    }


    private void goBack() {
        //如果是升级中就弹出对话框

        if (isUPLoading) {

            PublicAlertDialog.getInstance().showDialogNoCancle(false, "", UIUtils.getString(R.string.device_upgradeing), DFUActivity.this, UIUtils.getString(R.string.tips_ok), new AlertDialogStateCallBack() {
                @Override
                public void determine() {

                }

                @Override
                public void cancel() {

                }
            });
        } else {
            finish();
        }
    }


    /**
     * 扫描蓝牙设备的方法
     **/
    //扫描30秒就进项取消，然后提示升级失败

    boolean isCScan = false;

    private BluetoothAdapter.LeScanCallback bleScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            String deviceName = device.getName();
            String address = device.getAddress();
            if (!TextUtils.isEmpty(deviceName)) {
                deviceName = deviceName.toUpperCase();
            }
            Log.e(TAG, "scan到设备，deviceName == " + deviceName + " address:" + address);
            if (deviceName != null && deviceName.contains("DFU")) {
                //收到了设备
                Log.e(TAG, "scan到设备，deviceName == " + deviceName + " 并等待4s去执行upload操作");
               /* if (state.equals(IDIL)) {
                    state = UPLOADING;*/
                Log.e(TAG, "scan到设备");
                cancelScan();
                isCScan = true;
                Message msg = Message.obtain();
                msg.obj = device;
                msg.what = 0x01;
                stopService(new Intent(DFUActivity.this, DfuService.class));
                loadHandler.sendMessageDelayed(msg, 4000);
                //  }
            }

            /*Message msg = Message.obtain();
            msg.obj = device;
            msg.what = 0x01;
            dfuHandler.sendMessage(msg);*/
        }
    };
    BluetoothDevice mBluetoothDevice;
    private Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    mBluetoothDevice = (BluetoothDevice) msg.obj;
                    Logger.myLog("handleMessage:" + "upload:" + mBluetoothDevice.getName() + "getAddress:" + mBluetoothDevice.getAddress());

                    upload(mBluetoothDevice.getName(), mBluetoothDevice.getAddress());
                    break;
                case 0x02:
                    tryCount++;
                    isCScan = false;
                    loadHandler.sendEmptyMessageDelayed(0x04, 30000);
                    scanDfu();
                    break;
                case 0x03:
                    uploadOnFailure();
                case 0x04:
                    cancelScan();
                    if (!isCScan) {
                        uploadOnFailure();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void cancelScan() {
        BluetoothAdapter bleAdapter = BluetoothAdapter.getDefaultAdapter();
        bleAdapter.stopLeScan(bleScanCallBack);
    }

    public void scanDfu() {
        Log.e(TAG, "执行scanDfu操作");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        BluetoothAdapter bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bleAdapter == null || !bleAdapter.isEnabled()) {
            Toast.makeText(this, R.string.bluetooth_is_not_enabled, Toast.LENGTH_LONG).show();
            return;
        }
        /*progressDialog.setMessage(getString(R.string.msg_scan));
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelScan();
            }
        });*/
        // 扫到了就进行连接
        bleAdapter.startLeScan(null, bleScanCallBack);
    }

    private void downloadFail() {
        tvBtnState.setEnabled(true);
        String message = "";

        message = UIUtils.getString(R.string.file_download);

        PublicAlertDialog.getInstance().showDialogWithContentAndTitle(message, UIUtils.getString(R.string.device_upgrade_fail_message), DFUActivity.this, UIUtils.getString(R.string.yes), UIUtils.getString(R.string.no), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
                //TODO 跳转到扫描页面
                checkCameraPersiomm(DFUActivity.this, DFUActivity.this);
               /* Intent mIntent = null;
                mIntent = new Intent(DFUActivity.this, ActivityScan.class);
                mIntent.putExtra("device_type", JkConfiguration.DeviceType.DFU);
                startActivity(mIntent);*/
                //ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());

                //  ActivityLifecycleController.finishAllActivity(Constants.MAINACTIVITY_NAME);
            }

            @Override
            public void cancel() {

            }
        });
    }


    private CRPBleFirmwareUpgradeListener crpBleFirmwareUpgradeListener = new CRPBleFirmwareUpgradeListener() {
        @Override
        public void onFirmwareDownloadStarting() {
            Logger.myLog(TAG + "onFirmwareDownloadStarting");
        }

        @Override
        public void onFirmwareDownloadComplete() {

            Logger.myLog(TAG + "onFirmwareDownloadComplete");
        }

        @Override
        public void onUpgradeProgressStarting() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    PublicAlertDialog.getInstance().clearShowDialog();
                    SyncCacheUtils.isDFUMode(DFUActivity.this, devcieCurrentMac, true);
                    Logger.myLog(TAG + "onUpgradeProgressStarting");
                    //小米需要重新去扫描，把扫描到的DFU设备
                    //progressValue.setProgress(0);

                    startDFUMode();
                }
            });

        }

        @Override
        public void onUpgradeProgressChanged(int i, float v) {
            handler.post(new Runnable() {
                @Override
                public void run() {


                    if (isFinishing()) {
                        return;
                    }
                    //小米需要重新去扫描，把扫描到的DFU设备
                    startDFUMode();
                    onDeviceUpgradeProgressChanged(i);


                    Logger.myLog(TAG + "onUpgradeProgressChanged i:" + i + "V:" + v);
                }
            });
        }

        @Override
        public void onUpgradeCompleted() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        SyncCacheUtils.isDFUMode(DFUActivity.this, devcieCurrentMac, false);
                        progressValue.setProgress(100);
                        showDeviceUpdateSuccess();
                        Logger.myLog(TAG + "onUpgradeCompleted ");
                        // abortDFU();
                    }
                }
            }, 500);


        }

        @Override
        public void onUpgradeAborted() {
            Logger.myLog(TAG + "onUpgradeAborted ");
        }

        @Override
        public void onError(int i, String s) {
            // uploadOnFailure();
            Logger.myLog("onError:i" + i + "s:" + s);
            uploadOnFailure();
        }
    };


    /**
     * 扫描蓝牙设备的方法
     **/


    // 升级中进行进度展示
    public void onDeviceUpgradeProgressChanged(int percent) {
        loadHandler.removeCallbacksAndMessages(null);
        if (percent == 1) {
            isError = false;
        }
        cancelScan();
        if (percent == 100) {
            isComplety = true;
            if (Build.BRAND.equals("Xiaomi")) {
                //stopService(new Intent(DFUActivity.this, DfuService.class));
                SyncCacheUtils.clearSysData(DFUActivity.this);
                SyncCacheUtils.clearStartSync(DFUActivity.this);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDeviceUpdateSuccess();
                    }
                }, 100);
            }
        }
        tvBtnState.setText(String.format(UIUtils.getString(R.string.device_upgrade_present), percent));
        progressValue.setProgress(percent);
    }

}
