package com.isport.brandapp.bind;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.sleep.Sleep_Sleepace_DataModelAction;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.sleep.SleepHistoryDataResult;
import com.isport.blelibrary.result.impl.sleep.SleepHistoryDataResultList;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SyncResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.Adapter.AdapterScanPageDeviceList;
import com.isport.brandapp.bind.presenter.ScanPresenter;
import com.isport.brandapp.bind.view.ScanBaseView;
import com.isport.brandapp.device.bracelet.playW311.PlayW311Presenter.PlayerPresenter;
import com.isport.brandapp.device.f18.F18DufActivity;
import com.isport.brandapp.device.publicpage.GoActivityUtil;
import com.isport.brandapp.home.MainActivity;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.LocationUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import bike.gymproject.viewlibray.pickerview.utils.DateUtils;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.action.UserInformationBeanAction;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonrecyclerview.FullyLinearLayoutManager;
import brandapp.isport.com.basicres.commonrecyclerview.RefreshRecyclerView;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.entry.UserInformationBean;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/15
 * @Fuction ???????????????????????????
 */

public class ActivityScan extends BaseMVPTitleActivity<ScanBaseView, ScanPresenter> implements ScanBaseView,
        AdapterScanPageDeviceList.OnBindOnclickLister {
    private int deviceType;
    private ImageView ivConnRotate;
    private RefreshRecyclerView refreshRecyclerView;
    private AdapterScanPageDeviceList adapterScanPageDeviceList;
    private BaseDevice baseDevice;
    private List<BaseDevice> baseViewList;
    private PlayerPresenter presenter;


    boolean isFirstComming = true;

    Map<String, BaseDevice> listDevicesMap = null;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected ScanPresenter createPresenter() {
        return new ScanPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_scan;
    }


    @Override
    protected void initView(View view) {
        deviceType = getIntent().getIntExtra("device_type", 0);
        refreshRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_device);
        ivConnRotate = view.findViewById(R.id.iv_conn_rotate);
        if (deviceType == JkConfiguration.DeviceType.DFU) {
            adapterScanPageDeviceList = new AdapterScanPageDeviceList(this, true);
        } else {
            adapterScanPageDeviceList = new AdapterScanPageDeviceList(this, false);
        }
        //TODO ??????????????? recycler_club_content
        FullyLinearLayoutManager mClubFullyLinearLayoutManager = new FullyLinearLayoutManager(context);
        mClubFullyLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        refreshRecyclerView.setLayoutManager(mClubFullyLinearLayoutManager);
        baseViewList = new ArrayList<>();
        adapterScanPageDeviceList.setData(baseViewList);
        refreshRecyclerView.setAdapter(adapterScanPageDeviceList);
        refreshRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                baseDevice = baseViewList.get(position);
//                Logger.myLog("???????????? 222222222222222222");
//                mActPresenter.cancelScan();
//                mActPresenter.bindDevice(baseDevice);
            }
        });

    }

    IntentFilter filter;

    @Override
    protected void initData() {
        listDevicesMap = new HashMap<>();
        //baseViewList = new ArrayList<>();
        AppConfiguration.isSleepBind = true;
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(R.string.search);
        titleBarView.setRightText("");
        //toolbar.setBackgroundColor(getResources().getColor(R.color.common_bind_title));
        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(broadcastReceiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Logger.myLog("onPause");
        try {
            if (AppUtil.isOpenBle()) {
                if(broadcastReceiver!=null)
                  unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        mActPresenter.cancelScan();
        // mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    requestPermission();
                    break;
                case 0x02:
                    //????????????
                    ISportAgent.getInstance().disConDevice(false);
                    BleProgressObservable.getInstance().hide();
                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.connect_fail));

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Logger.myLog("onStart" + mHandler);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (LocationUtils.isLocationEnabled()) {

                BaseDevice bDevice = ISportAgent.getInstance().getCurrnetDevice();
                Logger.myLog(TAG,"ISportAgent.getInstance().getCurrnetDevice()" + (bDevice !=null ? bDevice.toString() : "BaseDevice??????!"));
                if (bDevice != null) {
                    ISportAgent.getInstance().disConDevice(false);
                }
                ISportAgent.getInstance().registerListener(mBleReciveListener);
                mHandler.sendEmptyMessageDelayed(0x00, 200);
            } else {

            }
        } else {
          //  registerReceiver(broadcastReceiver, filter);
            BaseDevice bDevice = ISportAgent.getInstance().getCurrnetDevice();
            Logger.myLog(TAG,"ISportAgent.getInstance().getCurrnetDevice()" +(bDevice!=null ? bDevice.toString() : "22 BaseDevice=null!"));
            if (bDevice != null) {
                ISportAgent.getInstance().disConDevice(false);
            }
            ISportAgent.getInstance().registerListener(mBleReciveListener);
            mHandler.sendEmptyMessageDelayed(0x00, 200);
        }

    }

    private void requestPermission() {
        try {
            PermissionManageUtil permissionManage = new PermissionManageUtil(this);
            RxPermissions mRxPermission = new RxPermissions(this);
            if (!mRxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
//
                permissionManage.requestPermissions(new RxPermissions(this), Manifest.permission.ACCESS_FINE_LOCATION, UIUtils.getString(R.string.permission_location0), new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        startScan();
                    }

                    @Override
                    public void onGetPermissionNo() {
                        ToastUtils.showToastLong(ActivityScan.this, UIUtils.getString(R.string.location_permissions));
                    }
                });
            } else {
                startScan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void startScan() {
        if(!ActivitySwitcher.isForeground(ActivityScan.this))
            return;
        Logger.myLog("onStart startScan");
        if (AppUtil.isOpenBle()) {
            mActPresenter.scan(deviceType);
            rotateAnimationStart();
        } else {
            //ToastUtils.showToast(context, UIUtils.getString(R.string.bluetooth_is_not_enabled));
            openBlueDialog();
        }
    }

    @Override
    protected void initEvent() {
        adapterScanPageDeviceList.setOnBindOnclickLister(this);
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                //??????????????????
                mActPresenter.cancelScan();
                rotateAnimationEnd();
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                Log.e(TAG, "ACTION_STATE_CHANGED" + state + BluetoothAdapter.STATE_ON);
                if (state == BluetoothAdapter.STATE_ON) {
                    startScan();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    //????????????????????????????????????
                    rotateAnimationEnd();
                    if (baseViewList != null) {
                        baseViewList.clear();
                        adapterScanPageDeviceList.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    /**
     * ??????????????????
     */
    private Animation operatingAnim;
    ObjectAnimator objectAnimator;

    private void rotateAnimationStart() {
        if (null == operatingAnim) {


        /*objectAnimator = ObjectAnimator.ofFloat(ivConnRotate, "rotation", 0f, 360f);
        objectAnimator.setDuration(2000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
*/

            /*operatingAnim = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            operatingAnim.setDuration(300);
            operatingAnim.setFillAfter(true);
            operatingAnim.setRepeatMode(Animation.RESTART);
            //?????????????????????????????????????????????
            operatingAnim.setInterpolator(new LinearInterpolator());
            operatingAnim.setRepeatCount(-1);
*/
            operatingAnim = AnimationUtils.loadAnimation(this,
                    R.anim.view_rotate);
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim.setInterpolator(lin);
        }
        ivConnRotate.startAnimation(operatingAnim);
    }

    private void rotateAnimationEnd() {
        ivConnRotate.clearAnimation();
    }

    @Override
    protected void initHeader() {
    }

    @Override
    public void onScan(List<BaseDevice> baseViewList) {
        // this.baseViewList = baseViewList;
        Logger.myLog(TAG,"onScan List");
        this.baseViewList.clear();

        Collections.sort(baseViewList, new Comparator<BaseDevice>() {
            @Override
            public int compare(BaseDevice baseDevice, BaseDevice t1) {
                return baseDevice.getRssi();
            }
        });

        this.baseViewList.addAll(baseViewList);
        adapterScanPageDeviceList.notifyDataSetChanged();
    }

    @Override
    public void onScan(Map<String, BaseDevice> listDevicesMap) {

        //Logger.myLog(TAG,"onScan map="+new Gson().toJson(listDevicesMap));
        Collection<BaseDevice> valueCollection = listDevicesMap.values();
        List<BaseDevice> valueList = new ArrayList<BaseDevice>(valueCollection);
        this.baseViewList.clear();
        this.baseViewList.addAll(valueList);
        adapterScanPageDeviceList.notifyDataSetChanged();
    }


    @Override
    public void bindSuccess(int deviceId) {
        if (App.appType() == App.httpType) {
            //?????????,?????????????????????????????????????????????????????????
            if (DeviceTypeUtil.isContainWrishBrand(baseDevice.deviceType)) {
                SyncCacheUtils.saveFirstBindW311(BaseApp.getApp());
            }
//            mActPresenter.connect(baseDevice, true);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS_WITH_PROGRESS, baseDevice));
            // TODO: 2019/2/19 ???????????????????????????????????????????????????????????????????????????
           // ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
            EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS, baseDevice));
//            mActPresenter.connect(baseDevice, true);
            // TODO: 2019/2/19 ???????????????????????????????????????????????????????????????????????????
            ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());

            DeviceBean deviceBean = new DeviceBean();
            deviceBean.deviceType = baseDevice.deviceType;
            deviceBean.deviceName = baseDevice.deviceName;
            GoActivityUtil.goActivityPlayerDevice(baseDevice.deviceType, deviceBean, ActivityScan.this);
            //???????????????????????????


        } else {
            //????????? ??????????????????
            if (baseDevice.deviceType == JkConfiguration.DeviceType.BRAND_W311 || baseDevice.deviceType == JkConfiguration.DeviceType.WATCH_W516 || baseDevice.deviceType == JkConfiguration.DeviceType.Brand_W520) {
                if (baseDevice.deviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    SyncCacheUtils.saveFirstBindW311(BaseApp.getApp());
                }
                mHandler.sendEmptyMessageDelayed(0x02, 20000);
                mActPresenter.connect(baseDevice, true, true);
            } else {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS, baseDevice));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS_WITH_PROGRESS, baseDevice));
//                mActPresenter.connect(baseDevice, true);
                // TODO: 2019/2/19 ???????????????????????????????????????????????????????????????????????????
//        ActivityManager.getInstance().finishAllActivity(ActivityBind.class.getSimpleName());+
                ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
            }
            // TODO: 2019/2/19 ???????????????????????????????????????????????????????????????????????????
//        ToastUtils.showToast(ActivityScan.this,"???????????? == " + deviceId);
            Logger.myLog("???????????? == " + deviceId);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Logger.myLog("onResume" + mHandler);

        //android 23???????????????????????????????????????
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (isFirstComming) {
                if (!LocationUtils.isLocationEnabled()) {
                    openLocationDialog();
                    isFirstComming = false;
                }
            } else {
                if (!LocationUtils.isLocationEnabled()) {
                    finish();
                }
            }
        }

    }

    @Override
    public void canBind(int state) {
        switch (state) {
            case ScanBaseView.STATE_HASBIND_CONTBIND:
                //???????????????????????????
                ToastUtils.showToastLong(ActivityScan.this, UIUtils.getString(R.string.hasBind));
                break;
            case ScanBaseView.STATE_NOBIND:
                //??????????????????
                mActPresenter.bindDevice(baseDevice);
                break;
            case ScanBaseView.STATE_HASBIND_CANBIND:
                //????????????
                mActPresenter.bindDevice(baseDevice);
                break;
            case ScanBaseView.STATE_BINDED:
                bindSuccess(1);//????????????????????????
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        AppConfiguration.isSleepBind = false;
        mActPresenter.cancelScan();
    }


    //??????????????????
    @Override
    public void onBindDeviceOnclick(BaseDevice baseDevice) {
        if (ViewMultiClickUtil.onMultiClick()) {
            return;
        }
        this.baseDevice = baseDevice;
        mActPresenter.cancelScan();
        //????????????????????????????????????
        if (App.appType() == App.httpType && !(deviceType == JkConfiguration.DeviceType.DFU)) {
            if (!checkNet()) {
                ToastUtils.showToast(context, com.isport.brandapp.basicres.R.string.common_please_check_that_your_network_is_connected);
                return;
            }
        }
        //?????????DFU_MODE???????????????????????????????????????????????????

        if (deviceType == JkConfiguration.DeviceType.DFU) {
            if (baseDevice.deviceType == JkConfiguration.DeviceType.DFU) {
                rotateAnimationEnd();
                ActivitySwitcher.goDFUDeviceSelectAct(ActivityScan.this, baseDevice.deviceName, baseDevice.getAddress());
            } else {
                mHandler.sendEmptyMessageDelayed(0x02, 30 * 1000);
                mActPresenter.connect(baseDevice, true, true, true);
                BleProgressObservable.getInstance().show(UIUtils.getString(R.string.app_isconnecting), false);
            }
        } else {
            AppSP.putInt(BaseApp.getApp(),AppSP.DEVICE_GOAL_KEY,0);
            //????????????????????????
            //mActPresenter.select_device_state(baseDevice);
            mActPresenter.bindDevice(baseDevice);
          /*  DeviceTypeTableAction.updateOrDelete(deviceType, baseDevice.getAddress(),
                    baseDevice.getDeviceName(), TokenUtil
                            .getInstance().getPeopleIdStr(BaseApp.getApp()),  baseDevice.getDeviceName());
            mActPresenter.connect(baseDevice, true, false);*/

        }
//        mActPresenter.connect(baseDevice);
    }


    private final BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            Logger.myLog(TAG,"????????????onConnResult:" + isConn+" "+baseDevice.toString());
            mHandler.removeMessages(0x02);
            if (isConn) {
                if(!ActivitySwitcher.isForeground(ActivityScan.this))
                    return;
                BleProgressObservable.getInstance().hide();
                if (deviceType == JkConfiguration.DeviceType.DFU) {
                    //TODO ?????????????????????
                    if (baseDevice != null) {
                        Logger.myLog("????????????,???????????????,?????????????????????");
                        if(DeviceTypeUtil.isContainF18()){
                          startActivity(F18DufActivity.class);
                        }else{
                            ActivitySwitcher.goDFUAct(ActivityScan.this, baseDevice.deviceType, baseDevice.deviceName, baseDevice.address, false);
                        }

                        // finish();
                    }
                } else {
                    Logger.myLog(TAG,"????????????,???????????????,??????????????????");
                    rotateAnimationEnd();
                    AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.getAddress());
                    mActPresenter.cancelScan();
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS,
                            baseDevice));
                    finish();
                }
            } else {
                //????????????????????????????????????
                // ISportAgent.getInstance().disConDevice(false);
                //Constants.CAN_RECONNECT = false;
                //ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.connect_fail));
                NetProgressObservable.getInstance().hide();
                BleProgressObservable.getInstance().hide();
                //?????????DFU?????????
                Constants.CAN_RECONNECT = false;
                if (deviceType == JkConfiguration.DeviceType.DFU) {
                    //ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.connect_fail));
                    // return;
                }
                //mHandler.sendEmptyMessageDelayed(0x00, 200);

            }
        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.SLEEP_BATTERY:
                        //?????????????????????????????????????????????,???????????????????????????
                        ISportAgent.getInstance().requestBle(BleRequest.Common_GetVersion);
                        // TODO: 2018/11/12 ??????????????????????????????????????????????????????????????????????????????????????????
                        //?????????????????????????????????????????????
                        Logger.myLog("???????????????????????????????????????????????????" + App.getSleepBindTime());
                        if (App.appType() == App.httpType) {
                        } else {
                            /*BleProgressObservable.getInstance().show(UIUtils.getString(R.string.sync_data),
                                    false);*/
                            // TODO: 2019/1/26 ???????????????0????????????????????????????????????????????????
                            UserInformationBean userInfoByUserId = UserInformationBeanAction.findUserInfoByUserId
                                    ("0");
                            Logger.myLog("userInfoByUserId = " + userInfoByUserId.toString());
                            ISportAgent.getInstance().requestBle(BleRequest.Sleep_Sleepace_historyDownload, 0, (int)
                                    DateUtils
                                            .getCurrentTimeUnixLong(), userInfoByUserId
                                    .getGender().equals("Male") ? 0 : 1);
                        }
                        //TODO:??????????????????  (int) (creatTime / 1000) ?????????????????????????????????????????????
                        // TODO: 2019/1/12 ?????????????????????????????????,???????????????????????????,???????????????
                        break;
                    case IResult.SLEEP_HISTORYDATA:
                        SleepHistoryDataResultList sleepHistoryDataResultList = (SleepHistoryDataResultList) mResult;
                        //?????????????????????????????????,????????????
                        ArrayList<SleepHistoryDataResult> sleepHistoryDataResults = sleepHistoryDataResultList
                                .getSleepHistoryDataResults();
                        if (sleepHistoryDataResults == null || !(App.appType() == App.httpType)) {
                            Logger.myLog("??????????????????????????????,??????????????????");
                            if (App.appType() == App.httpType) {
                            } else {
                                BleProgressObservable.getInstance().hide();
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS,
                                        baseDevice));
                                finish();
                            }
                            List<Sleep_Sleepace_DataModel> all = Sleep_Sleepace_DataModelAction.getAll();
                            if (all != null)
                                for (int i = 0; i < all.size(); i++) {
                                    Logger.myLog("sleep ???????????? == " + all.get(i).toString());
                                }
                            Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceId =
                                    Sleep_Sleepace_DataModelAction.findSleep_Sleepace_DataModelByDeviceId
                                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                            if (sleep_sleepace_dataModelByDeviceId != null) {
                                Logger.myLog("??????????????????????????????????????? == " + sleep_sleepace_dataModelByDeviceId.toString());
                            }
                        } else {
                            //?????????????????????????????????????????????????????????????????????????????????
                        }
                        break;
                    case IResult.WATCH_VERSION:

                       /* BleProgressObservable.getInstance().show(UIUtils.getString(R.string.sync_data),
                                false);*/
                        break;
                    case IResult.WATCH_W516_SYNC:
                        //????????????????????????
                        WatchW516SyncResult watchW516SyncResult = (WatchW516SyncResult) mResult;
                        if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SUCCESS) {
                            AppConfiguration.hasSynced = true;
                            // ToastUtils.showToast(context, R.string.app_issync_complete);
                        } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.FAILED) {
                            AppConfiguration.hasSynced = true;
                            ToastUtils.showToast(context, R.string.app_issync_failed);
                        } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SYNCING) {
                            AppConfiguration.hasSynced = false;
                        }
                        BleProgressObservable.getInstance().hide();
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.BIND_DEVICE_SUCCESS,
                                baseDevice));
                        finish();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            default:
                break;
        }
    }

    public void openBlueDialog() {
        PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.bonlala_open_blue), context, getResources().getString(R.string.app_bluetoothadapter_turnoff), getResources().getString(R.string.app_bluetoothadapter_turnon), new AlertDialogStateCallBack() {
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
    }

    public void openLocationDialog() {
        PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.bonlala_loaction_server), context, getResources().getString(R.string.bonlala_loaction_off), getResources().getString(R.string.bonlala_loaction_on), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivity(intent);
                LocationUtils.openGpsSettings();
            }

            @Override
            public void cancel() {
                finish();
            }
        }, false);
    }


}
