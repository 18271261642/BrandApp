package com.isport.brandapp.bind;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.scale.ScaleLockDataResult;
import com.isport.blelibrary.result.impl.scale.ScaleUnLockDataResult;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.MainActivity;
import com.isport.brandapp.R;
import com.isport.brandapp.bind.presenter.ScaleScanPresenter;
import com.isport.brandapp.bind.view.ScaleScanView;
import com.isport.brandapp.bind.view.ScanBaseView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MapUtils;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/15
 * @Fuction
 */

public class ActivityScaleScan extends BaseMVPTitleActivity<ScaleScanView, ScaleScanPresenter> implements ScaleScanView,
        View.OnClickListener {
    private LinearLayout rlOnscale;
    private LinearLayout rlOnscaleStart, layout_select_layout_option;
    private TextView tvDelayBind;
    private TextView tvData;
    private TextView tvScanAgain;
    private TextView tvConfirmBind;
    // private RippleBackground rippleBackground;
    private boolean hasConnected;//限制搜索
    private boolean connected;//连接状态
    private BaseDevice baseDevice;
    private boolean mIsConnect;

    private boolean isBindSuccess;


    @Override
    protected ScaleScanPresenter createPresenter() {
        return new ScaleScanPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_scalescan;
    }

    @Override
    protected void initView(View view) {
        rlOnscale = view.findViewById(R.id.rl_onscale);
        rlOnscaleStart = view.findViewById(R.id.rl_onscale_start);
        layout_select_layout_option = view.findViewById(R.id.layout_select_layout_option);
        tvDelayBind = (TextView) view.findViewById(R.id.tv_delay_bind);
        tvData = (TextView) view.findViewById(R.id.tv_data);
        tvScanAgain = (TextView) view.findViewById(R.id.tv_scan_again);
        tvConfirmBind = (TextView) view.findViewById(R.id.tv_confirm_bind);
        //rippleBackground = view.findViewById(R.id.content);
    }

    private void setDefaultDisplay() {
        hasConnected = false;
        connected = false;
        rlOnscale.setVisibility(View.VISIBLE);
        rlOnscaleStart.setVisibility(View.GONE);
        layout_select_layout_option.setVisibility(View.GONE);
    }

    private void setConnectedDisplay() {
        hasConnected = true;
        connected = true;
        rlOnscale.setVisibility(View.GONE);
        rlOnscaleStart.setVisibility(View.VISIBLE);
        layout_select_layout_option.setVisibility(View.VISIBLE);
        //rippleBackground.startRippleAnimation();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    startScan();
                    break;
                case 0x01:
                    requestPermission();
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mIsConnect = getIntent().getBooleanExtra("isConnect", false);
       // tvDelayBind.setVisibility(mIsConnect ? View.GONE : View.VISIBLE);
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                ISportAgent.getInstance().cancelLeScan();
                ISportAgent.getInstance().unbind(false);
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
        titleBarView.setTitle(UIUtils.getString(R.string.body_fat_scale));
        titleBarView.setRightText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.sendEmptyMessageDelayed(0x00, 100);
    }

    private void requestPermission() {
        PermissionManageUtil permissionManage = new PermissionManageUtil(this);
        RxPermissions mRxPermission = new RxPermissions(this);
        if (!mRxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
//        if (!permissionManage.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            permissionManage.requestPermissionsGroup(new RxPermissions(this),
//                                                     PermissionGroup.LOCATION, new PermissionManageUtil
//                            .OnGetPermissionListener() {
//                        @Override
//                        public void onGetPermissionYes() {
//                            startScan();
//                        }
//
//                        @Override
//                        public void onGetPermissionNo() {
//                            ToastUtils.showToastLong(ActivityScaleScan.this, UIUtils.getString(R.string.location_permissions));
//                        }
//
//                    });
            permissionManage.requestPermissions(new RxPermissions(this), Manifest.permission.ACCESS_FINE_LOCATION, UIUtils.getString(R.string.permission_location0), new PermissionManageUtil.OnGetPermissionListener() {


                @Override
                public void onGetPermissionYes() {
                    startScan();
                }

                @Override
                public void onGetPermissionNo() {
                    ToastUtils.showToastLong(ActivityScaleScan.this, UIUtils.getString(R.string.location_permissions));
                }
            });
        } else {
            startScan();
        }
    }

    public static final int REQCODE_OPEN_BT = 0x100;

    private void startScan() {
        if (AppUtil.isOpenBle()) {
            mActPresenter.scan(JkConfiguration.DeviceType.BODYFAT);

           /* PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string
           .app_bluetoothadapter_noon), context, getResources().getString(R.string.common_dialog_cancel),
           getResources().getString(R.string.app_bluetoothadapter_turnon), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivity(intent);
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter != null) {
                        bluetoothAdapter.enable();
                    }
                }

                @Override
                public void cancel() {
                    finish();
                }
            });*/
        } else {
            PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.bluetooth_is_not_enabled), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.app_bluetoothadapter_turnon), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivity(intent);
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter != null) {
                        bluetoothAdapter.enable();
                    }
                }

                @Override
                public void cancel() {
                    finish();
                }
            }, false);
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, REQCODE_OPEN_BT);
        }
    }

    @Override
    protected void initEvent() {
        tvDelayBind.setOnClickListener(this);
        tvScanAgain.setOnClickListener(this);
        tvConfirmBind.setOnClickListener(this);
        hasConnected = false;
        connected = false;
        AppConfiguration.isScaleScan = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, filter);
        ISportAgent.getInstance().registerListener(mBleReciveListener);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Log.e("BleService", "ACTION_STATE_CHANGED");
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    startScan();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                }
            }
        }
    };

    @Override
    protected void initHeader() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delay_bind:
                ISportAgent.getInstance().unbind(false);
                finish();
                break;
            case R.id.tv_scan_again:
                setDefaultDisplay();
                mActPresenter.disconnect(false);
                //断连后延时200ms直接扫描操作
                // mHandler.sendEmptyMessageDelayed(0x00,200);
                break;
            case R.id.tv_confirm_bind:
                mActPresenter.bindDevice(baseDevice);
                break;
            default:
                break;
        }
    }

    @Override
    public void canBind(int state) {
        switch (state) {
            case ScanBaseView.STATE_HASBIND_CONTBIND:
                //提示用户已经被绑定
                Logger.myLog("提示用户已经被绑定");
                ToastUtils.showToastLong(ActivityScaleScan.this, UIUtils.getString(R.string.hasBind));
                break;
            case ScanBaseView.STATE_NOBIND:
                //可以直接插入
                Logger.myLog("可以直接插入");
                mActPresenter.bindDevice(baseDevice, ScanBaseView.STATE_NOBIND);
                break;
            case ScanBaseView.STATE_HASBIND_CANBIND:
                //需要更新
                Logger.myLog("需要更新");
                mActPresenter.bindDevice(baseDevice, ScanBaseView.STATE_HASBIND_CANBIND);
                break;
            case ScanBaseView.STATE_BINDED:
                bindSuccess(1);//本地版本绑定成功
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ISportAgent.getInstance().cancelLeScan();
        //rippleBackground.stopRippleAnimation();
        unregisterReceiver(broadcastReceiver);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        AppConfiguration.isScaleScan = false;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EXIT_SCALESCAN));
    }

    @Override
    public void onScan(List<BaseDevice> baseViewList) {
        if (!hasConnected && baseViewList.size() > 0) {
            this.baseDevice = baseViewList.get(0);
            hasConnected = true;
            mActPresenter.cancelScan();
            mActPresenter.connect(baseViewList.get(0), false, true);
        }
    }

    @Override
    public void onScan(Map<String, BaseDevice> listDevicesMap) {
//        Logger.myLog("listDevicesMap == " + listDevicesMap.size());
        if (!hasConnected && listDevicesMap.size() > 0) {
            this.baseDevice = MapUtils.getFirstOrNull(listDevicesMap);
            Logger.myLog("baseDevice == " + baseDevice.toString());
            hasConnected = true;
            mActPresenter.cancelScan();
            mActPresenter.connect(baseDevice, false, true);
        }
    }

    @Override
    public void onScan(String key, BaseDevice baseDevice) {

    }

    @Override
    public void bindSuccess(int deviceId) {
        Logger.myLog("绑定成功");
        EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS, baseDevice));
//        ToastUtils.showToast(ActivityScaleScan.this,"体脂称绑定成功 == " + deviceId);
        //  ActivityBind.mActivityBind.finish();
//        ActivityManager.getInstance().finishAllActivity(ActivityBind.class.getSimpleName());

        ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
        // finish();

    }

    private BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            if (isConn) {
                if (mIsConnect) {
                    finish();
                } else {
                    if (ISportAgent.getInstance().getCurrnetDevice() != null && ISportAgent.getInstance().getCurrnetDevice().deviceType == JkConfiguration.DeviceType.BODYFAT) {
                        setConnectedDisplay();
                    }
                }
            } else {
//                setDefaultDisplay();
                //如果连接断开了，展示了默认的页面，再次重新扫描
                if (!hasConnected) {
                    Logger.myLog("连接断开,去扫描");
                    startScan();
                } else {
                    if (!connected) {
                        //出现133的情况，已经扫描到设备了，但是连接失败了
                        Logger.myLog("连接断开,去扫描");
                        mHandler.sendEmptyMessageDelayed(0x00, 200);
//                        startScan();
                    } else {
                        Logger.myLog("连接断开,不用去扫描");
                    }
                }
            }
        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.SCALE_UN_LOCK_DATA:
                        ScaleUnLockDataResult mResult1 = (ScaleUnLockDataResult) mResult;
                        tvData.setText(mResult1.getWeight() + "");
                        break;
                    case IResult.SCALE_LOCK_DATA:
                        ScaleLockDataResult mResult2 = (ScaleLockDataResult) mResult;
                        tvData.setText(mResult2.getWeight() + "");
                        break;
                    default:
                        break;
                }
        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {

        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {

        }
    };

    @Override
    public void onBackPressed() {
        //mActPresenter.disconnect();
        super.onBackPressed();
    }
}
