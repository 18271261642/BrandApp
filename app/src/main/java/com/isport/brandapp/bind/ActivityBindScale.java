package com.isport.brandapp.bind;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.observe.SyncProgressObservable;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.sleep.SleepHistoryDataResult;
import com.isport.blelibrary.result.impl.sleep.SleepHistoryDataResultList;
import com.isport.blelibrary.result.impl.w311.BraceletW311SyncComplete;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SyncResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.Adapter.AdapterBindPageDeviceList;
import com.isport.brandapp.bind.presenter.BindPresenter;
import com.isport.brandapp.bind.view.BindBaseView;
import com.isport.brandapp.dialog.UnBindDeviceDialog;
import com.isport.brandapp.dialog.UnbindStateCallBack;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import bike.gymproject.viewlibray.pickerview.utils.DateUtils;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonrecyclerview.FullyLinearLayoutManager;
import brandapp.isport.com.basicres.commonrecyclerview.RefreshRecyclerView;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/15
 * @Fuction
 */

public class ActivityBindScale extends BaseMVPTitleActivity<BindBaseView, BindPresenter> implements BindBaseView {
    private RefreshRecyclerView refreshRecyclerView;
    private AdapterBindPageDeviceList adapterBindPageDeviceList;
    private boolean hasScale;
    ArrayList<DeviceBean> list = new ArrayList<>();
    private int currentType;
    boolean isDerictUnBind;
    private DeviceBean mDeviceBean;
    private boolean canUnBind;


    @Override
    protected BindPresenter createPresenter() {
        return new BindPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_bind;
    }

    @Override
    protected void initView(View view) {
        canUnBind = false;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        refreshRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_device);
        adapterBindPageDeviceList = new AdapterBindPageDeviceList(this);
        //TODO ??????????????? recycler_club_content
        FullyLinearLayoutManager mClubFullyLinearLayoutManager = new FullyLinearLayoutManager(context);
        mClubFullyLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        refreshRecyclerView.setLayoutManager(mClubFullyLinearLayoutManager);
        refreshRecyclerView.setAdapter(adapterBindPageDeviceList);
        ISportAgent.getInstance().registerListener(mBleReciveListener);
        getDeviceList();
        refreshRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                onItemClickAction(position);
            }
        });
    }

    int count = 0;

    private BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            if (isConn) {
            } else {
                NetProgressObservable.getInstance().hide();
            }
        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.SLEEP_HISTORYDATA:
                        if (AppConfiguration.isBindList && canUnBind) {
                            canUnBind = false;
                            //????????????????????????????????????????????????????????????
                            SleepHistoryDataResultList sleepHistoryDataResultList = (SleepHistoryDataResultList)
                                    mResult;
                            //?????????????????????????????????,????????????
                            ArrayList<SleepHistoryDataResult> sleepHistoryDataResults = sleepHistoryDataResultList
                                    .getSleepHistoryDataResults();
                            if (sleepHistoryDataResults == null) {
                                //?????????????????????,????????????
                                unBindDevice(mDeviceBean);
                            } else {
                                //??????????????????
//                            Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceId
                                mActPresenter.updateSleepHistoryData(mDeviceBean);
                            }
                        }
                        break;
                    //311????????????
                    case IResult.BRACELET_W311_COMPTELETY:
                        //count++;
                        //Logger.myLog("BRACELET_W311_COMPTELETY count" + count);
                        if (isDerictUnBind) {
                            return;
                        }
                        SyncProgressObservable.getInstance().hide();
                        BraceletW311SyncComplete braceletW311SyncComplete = (BraceletW311SyncComplete) mResult;
                        if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.SUCCESS) {
                            AppConfiguration.hasSynced = true;
                            mActPresenter.updateBraceletW311HistoryData(mDeviceBean, true);
                        } else if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.FAILED) {
                            AppConfiguration.hasSynced = true;
                            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.app_issync_failed));
                        } else if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.TIMEOUT) {
                            AppConfiguration.hasSynced = true;
                            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.app_issync_failed));
                        }

                        break;
                    case IResult.WATCH_W516_SYNC:
                        if (isDerictUnBind) {
                            return;
                        }
                        if (AppConfiguration.isBindList && canUnBind) {

                            canUnBind = false;
                            //????????????????????????????????????????????????????????????
                            //????????????????????????
                            WatchW516SyncResult watchW516SyncResult = (WatchW516SyncResult) mResult;
                            if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SUCCESS) {
                                AppConfiguration.hasSynced = true;
                                // ToastUtils.showToast(context, R.string.app_issync_complete);
                                //????????????????????????
                                // TODO: 2019/3/4 ?????????????????????????????????
//                            SleepHistoryDataResultList sleepHistoryDataResultList = (SleepHistoryDataResultList)
//                                    mResult;
//                            //?????????????????????????????????,????????????
//                            ArrayList<SleepHistoryDataResult> sleepHistoryDataResults = sleepHistoryDataResultList
//                                    .getSleepHistoryDataResults();
//                            if (sleepHistoryDataResults == null) {
                                //?????????????????????,????????????
//                                unBindDevice(mDeviceBean);
//                            } else {
//                                //??????????????????
////                            Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceId
                                mActPresenter.updateWatchHistoryData(mDeviceBean, false);
//                            }
                            } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.FAILED) {
                                AppConfiguration.hasSynced = true;
                                ToastUtils.showToast(context, R.string.app_issync_failed);
                            } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SYNCING) {
                                AppConfiguration.hasSynced = false;
                            }
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.SYNC_WATCH_SUCCESS));
                        }
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

    /**
     * ?????????sleep
     */
    private void getDeviceList() {
        list.clear();
        list.add(new DeviceBean(JkConfiguration.DeviceType.BODYFAT, UIUtils.getString(R.string.body_fat_scale), R.drawable.icon_device_scale));
        hasScale = false;
//        list.add(new DeviceBean(JkConfiguration.DeviceType.SLEEP));
        if (AppConfiguration.deviceMainBeanList != null) {

            if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                hasScale = true;
                DeviceBean deviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.BODYFAT);
                updateList(0, deviceBean);
            }
        }
        adapterBindPageDeviceList.setData(list);
    }

    public void updateList(int index, DeviceBean deviceBean) {
        deviceBean.scanName = list.get(index).scanName;
        deviceBean.resId = list.get(index).resId;
        list.set(index, deviceBean);
    }

    private void onItemClickAction(int position) {
        if (AppConfiguration.deviceMainBeanList != null && AppConfiguration.deviceMainBeanList.size() > 0) {
            //??????????????????????????????
            mDeviceBean = list.get(position);
            if (Utils.isEmpty(mDeviceBean.deviceName)) {
                // if (AppConfiguration.isConnected) {
                //??????????????????????????????
                ISportAgent.getInstance().disConDevice(false);
                // }
                //????????????????????????????????????
                Intent mIntent = null;
                switch (mDeviceBean.currentType) {
                    case JkConfiguration.DeviceType.WATCH_W516:


                        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
                            ToastUtil.showTextToast(context, UIUtils.getString(R.string.unbind_watch_tips));

                            return;
                        } else {

                            ActivitySwitcher.goScanActivty(ActivityBindScale.this, JkConfiguration.DeviceType.WATCH_W516);
                        }
                        break;
                    case JkConfiguration.DeviceType.BODYFAT:
                        mIntent = new Intent(ActivityBindScale.this, ActivityScaleScan.class);
                        //???????????????????????????,???????????????????????????
                        mIntent.putExtra("isConnect", false);
                        break;
                    case JkConfiguration.DeviceType.BRAND_W311:
                        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
                            ToastUtil.showTextToast(context, UIUtils.getString(R.string.unbind_watch_tips));

                            return;
                        } else {
                            ActivitySwitcher.goScanActivty(ActivityBindScale.this, JkConfiguration.DeviceType.BRAND_W311);
                        }
                        break;
                    default:
                        break;
                }
                startActivity(mIntent);
            } else {
                boolean show = false;
                switch (mDeviceBean.currentType) {
                    case JkConfiguration.DeviceType.BRAND_W311:
                        show = true;
                        break;
                    case JkConfiguration.DeviceType.WATCH_W516:
                        show = true;
                        break;
                    case JkConfiguration.DeviceType.SLEEP:
                        show = true;
                        break;
                    default:
                        break;
                }
                if (mDeviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    //?????????????????????
                    new PublicAlertDialog().showDialog(UIUtils.getString(R.string.notice), UIUtils.getString(R.string.unpair_notice), context, UIUtils.getString(R.string.common_dialog_cancel), UIUtils.getString(R.string.unpair), new AlertDialogStateCallBack() {


                        @Override
                        public void determine() {
                            unBindDevice(mDeviceBean);
                        }

                        @Override
                        public void cancel() {

                        }
                    }, false);
                } else {
                    //???????????????????????????????????????????????????,???????????????????????????????????????
                    isDerictUnBind = false;
                    new UnBindDeviceDialog(this,JkConfiguration.DeviceType.WATCH_W516,  show, new UnbindStateCallBack() {
                        @Override
                        public void synUnbind() {
                            if (AppConfiguration.isConnected) {
                                // TODO: 2018/11/8 ?????????????????????
                                switch (mDeviceBean.currentType) {
                                    case JkConfiguration.DeviceType.BRAND_W311:
                                        if (ISportAgent.getInstance().getCurrnetDevice().deviceType ==
                                                JkConfiguration.DeviceType.BRAND_W311) {
                                            canUnBind = true;
                                            //???????????????
                                            ISportAgent.getInstance().requestBle(BleRequest.bracelet_sync_data);
                                            NetProgressObservable.getInstance().show(UIUtils.getString(R.string.common_please_wait),
                                                    false);
                                        } else {
                                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                                        }
                                        break;
                                    case JkConfiguration.DeviceType.WATCH_W516:
//                                        if (FragmentData.mWristbandstep != null) {
//                                            mActPresenter.updateSportData(FragmentData.mWristbandstep, mDeviceBean);
//                                        }
                                        if (ISportAgent.getInstance().getCurrnetDevice().deviceType ==
                                                JkConfiguration.DeviceType.WATCH_W516) {
                                            canUnBind = true;
                                            //???????????????
                                            String string = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_DAILY_RECORD, string);
                                            NetProgressObservable.getInstance().show(UIUtils.getString(R.string.common_please_wait),
                                                    false);
                                        } else {
                                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                                        }
                                        break;
                                    case JkConfiguration.DeviceType.SLEEP:
                                        // TODO: 2018/11/8 ??????????????????
                                        //?????????????????????????????????
                                        if (ISportAgent.getInstance().getCurrnetDevice().deviceType ==
                                                JkConfiguration.DeviceType.SLEEP) {
                                            canUnBind = true;
                                            //???????????????
                                            UserInfoBean userInfo = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance()
                                                    .getPeopleIdStr
                                                            (context));
                                            ISportAgent.getInstance().requestBle(BleRequest
                                                            .Sleep_Sleepace_historyDownload,
                                                    (int) (App.getSleepBindTime() / 1000),
                                                    (int) DateUtils
                                                            .getCurrentTimeUnixLong(),
                                                    userInfo.getGender().equals
                                                            ("Male") ? 1 : 0);
                                            NetProgressObservable.getInstance().show(UIUtils.getString(R.string.common_please_wait),
                                                    false);
                                        } else {
                                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                            }
                        }

                        @Override
                        public void dirctUnbind() {
                            unBindDevice(mDeviceBean);
                        }

                        @Override
                        public void cancel() {

                        }
                    }, mDeviceBean.currentType);
                }
            }
        } else {
            //??????????????????????????????????????????????????????
            //  if (AppConfiguration.isConnected) {
            Logger.myLog("???????????????00000000000");
            ISportAgent.getInstance().disConDevice(false);
            //}
            Intent mIntent = null;
            mIntent = new Intent(ActivityBindScale.this, ActivityScaleScan.class);
            mIntent.putExtra("isConnect", false);
            startActivity(mIntent);
        }
    }

    private void unBindDevice(DeviceBean deviceBean) {
        isDerictUnBind = true;
        currentType = deviceBean.deviceType;
        Logger.myLog("??????????????? == " + currentType);
        //????????????????????? ?????????????????????
        // if (AppConfiguration.isConnected) {
        BaseDevice currnetDevice = ISportAgent.getInstance().getCurrnetDevice();
        if (currnetDevice != null && currnetDevice.deviceType == currentType) {
            Logger.myLog("currnetDevice == " + currentType);
            //????????????????????????
            ISportAgent.getInstance().unbind(false);
        }
        // }
        mActPresenter.unBind(deviceBean, false);
    }

    @Override
    protected void initData() {
        Constants.isDFU = false;
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(R.string.select_device_type_scale);
        titleBarView.setRightText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppConfiguration.isBindList = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppConfiguration.isBindList = true;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.GET_BIND_DEVICELIST_SUCCESS:
                getDeviceList();
                adapterBindPageDeviceList.notifyDataSetChanged();
                break;
            case MessageEvent.UNBIND_DEVICE_SUCCESS_EVENT:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int deviceType = (int) messageEvent.getObj();
                        if (deviceType == JkConfiguration.DeviceType.SLEEP) {
                            brandapp.isport.com.basicres.commonutil.Logger.e("?????????????????????");
                            TokenUtil.getInstance().saveSleepTime(BaseApp.getApp(), "");
                        }
                        Logger.myLog("????????????");
                        hasScale = false;
                        //EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
                    }
                }, 200);
                break;
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

    @Override
    protected void initHeader() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }


    @Override
    public void onUnBindSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.myLog("????????????");
                hasScale = false;
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
                ToastUtils.showToast(context, UIUtils.getString(R.string.unpair_successfully));
                //?????????????????????
                //ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
            }
        }, 200);
    }

    @Override
    public void updateWatchDataSuccess(DeviceBean deviceBean) {
        unBindDevice(deviceBean);
    }

    @Override
    public void updateSleepDataSuccess(DeviceBean deviceBean) {
        unBindDevice(deviceBean);
    }

    @Override
    public void updateWatchHistoryDataSuccess(DeviceBean deviceBean) {
        unBindDevice(mDeviceBean);
    }

    @Override
    public void updateFail() {

    }
}
