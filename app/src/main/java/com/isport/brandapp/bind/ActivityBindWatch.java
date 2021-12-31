package com.isport.brandapp.bind;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
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
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.Adapter.AdapterBindPageDeviceList;
import com.isport.brandapp.bind.presenter.BindPresenter;
import com.isport.brandapp.bind.view.BindBaseView;
import com.isport.brandapp.device.publicpage.GoActivityUtil;
import com.isport.brandapp.dialog.UnBindDeviceDialog;
import com.isport.brandapp.dialog.UnbindStateCallBack;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.DeviceTypeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonrecyclerview.FullyLinearLayoutManager;
import brandapp.isport.com.basicres.commonrecyclerview.RefreshRecyclerView;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/15
 * @Fuction
 */

public class ActivityBindWatch extends BaseMVPTitleActivity<BindBaseView, BindPresenter> implements BindBaseView {

    private static final String TAG = "ActivityBindWatch";

    private RefreshRecyclerView refreshRecyclerView;
    private AdapterBindPageDeviceList adapterBindPageDeviceList;
    private boolean hasWatch;
    ArrayList<DeviceBean> list = new ArrayList<>();
    public static ActivityBindWatch mActivityBind;
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
        mActivityBind = this;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        refreshRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_device);
        adapterBindPageDeviceList = new AdapterBindPageDeviceList(this);
        //TODO 俱乐部名称 recycler_club_content
        FullyLinearLayoutManager mClubFullyLinearLayoutManager = new FullyLinearLayoutManager(context);
        mClubFullyLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        refreshRecyclerView.setLayoutManager(mClubFullyLinearLayoutManager);
        refreshRecyclerView.setAdapter(adapterBindPageDeviceList);
        ISportAgent.getInstance().registerListener(mBleReciveListener);
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

    private final BleReciveListener mBleReciveListener = new BleReciveListener() {
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
                            //解绑同步数据，不在搜索绑定页面时才能进入
                            SleepHistoryDataResultList sleepHistoryDataResultList = (SleepHistoryDataResultList)
                                    mResult;
                            //存储到本地，到历史页面,上传数据
                            ArrayList<SleepHistoryDataResult> sleepHistoryDataResults = sleepHistoryDataResultList
                                    .getSleepHistoryDataResults();
                            if (sleepHistoryDataResults == null) {
                                //没有数据要上传,直接解绑
                                unBindDevice(mDeviceBean, false);
                            } else {
                                //有数据要上传
//                            Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceId
                                mActPresenter.updateSleepHistoryData(mDeviceBean);
                            }
                        }
                        break;
                    //311同步完成
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
                            //解绑同步数据，不在搜索绑定页面时才能进入
                            //同步数据是否成功
                            WatchW516SyncResult watchW516SyncResult = (WatchW516SyncResult) mResult;
                            if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SUCCESS) {
                                AppConfiguration.hasSynced = true;
                                // ToastUtils.showToast(context, R.string.app_issync_complete);
                                //同步成功才能解绑
                                // TODO: 2019/3/4 上传数据到服务器的逻辑
//                            SleepHistoryDataResultList sleepHistoryDataResultList = (SleepHistoryDataResultList)
//                                    mResult;
//                            //存储到本地，到历史页面,上传数据
//                            ArrayList<SleepHistoryDataResult> sleepHistoryDataResults = sleepHistoryDataResultList
//                                    .getSleepHistoryDataResults();
//                            if (sleepHistoryDataResults == null) {
                                //没有数据要上传,直接解绑
//                                unBindDevice(mDeviceBean);
//                            } else {
//                                //有数据要上传
////                            Sleep_Sleepace_DataModel sleep_sleepace_dataModelByDeviceId
                                mActPresenter.updateWatchHistoryData(mDeviceBean, true);
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
     * 暂隐藏sleep
     */
    private void getDeviceList() {
        list.clear();
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W910, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_910_FILTER), R.drawable.icon_scan_w910));
        list.add(new DeviceBean(JkConfiguration.DeviceType.WATCH_W516, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_FILTER), R.drawable.icon_w516));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W556, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_556_FILTER), R.drawable.icon_scan_w526));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W557, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_557_FILTER), R.drawable.icon_scan_w557));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W560, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_560_FILTER), R.drawable.icon_search_type_w560));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W560B, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_560B_FILTER), R.drawable.icon_scan_w560));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W812, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_812_FILTER), R.drawable.icon_scan_w812));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W812B, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_812B_FILTER), R.drawable.icon_scan_w812b));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W813, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_813_FILTER), R.drawable.icon_scan_w813));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W817, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_817_FILTER), R.drawable.icon_scan_w817));
        list.add(new DeviceBean(JkConfiguration.DeviceType.Watch_W819, String.format(UIUtils.getString(R.string.detail_watch), Constants.WATCH_819_FILTER), R.drawable.icon_scan_w819));
        hasWatch = false;

        //此处AppConfiguration.deviceBeanList 更换为AppConfiguration.deviceMainBeanList



        if (AppConfiguration.deviceMainBeanList != null) {
            for (int deviceType : AppConfiguration.deviceMainBeanList.keySet()) {
                DeviceBean deviceBean = AppConfiguration.deviceMainBeanList.get(deviceType);
                switch (deviceBean.currentType) {
                    case JkConfiguration.DeviceType.Watch_W910:
                        updateList(0, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.WATCH_W516:
                        updateList(1, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W556:
                        updateList(2, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W557:
                        updateList(3, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W560:
                        updateList(4, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W560B:
                        updateList(5, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W812:
                        updateList(6, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W812B:
                        updateList(7, deviceBean);
                        break;
                    case JkConfiguration.DeviceType.Watch_W813:
                        updateList(8, deviceBean);

                        break;
                    case JkConfiguration.DeviceType.Watch_W817:
                        updateList(9, deviceBean);

                        break;
                    case JkConfiguration.DeviceType.Watch_W819:
                        updateList(10, deviceBean);

                        break;
                }
                hasWatch = true;

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

        mDeviceBean = list.get(position);
        Logger.myLog(TAG,"mDeviceBean=" + mDeviceBean.toString() + "position=" + position+"\n"+"map保存="+new Gson().toJson(AppConfiguration.deviceBeanList));
        if (AppConfiguration.deviceMainBeanList != null && AppConfiguration.deviceMainBeanList.size() > 0) {
            //已经有绑定的设备列表
            if (Utils.isEmpty(mDeviceBean.deviceName)) {
                if (DeviceTypeUtil.isContainBrand() || DeviceTypeUtil.isContainW81() || DeviceTypeUtil.isContainWatch()) {
                    showUnbindDialog();
                    //弹出解绑对话框
                } else {
                    ActivitySwitcher.goScanActivty(ActivityBindWatch.this, mDeviceBean.currentType);
                }

            } else {

                //名字不为空说明是绑定的设备，先解绑,手表和睡眠带要先同步后解绑
                isDerictUnBind = false;
                showUnbindDialog();

            }
        } else {
            //没有绑定的设备列表，直接进入搜索页面
            // if (AppConfiguration.isConnected) {
            Logger.myLog("去断开连接00000000000");
            ISportAgent.getInstance().disConDevice(false);
            // }
            ActivitySwitcher.goScanActivty(ActivityBindWatch.this, mDeviceBean.currentType);

        }

    }

    private void unBindDevice(DeviceBean deviceBean, boolean isDe) {
        isDerictUnBind = true;
        currentType = deviceBean.deviceType;
        Logger.myLog("点击去解绑 == " + currentType+"\n"+deviceBean.toString());
        mActPresenter.unBind(deviceBean, isDe);
    }

    @Override
    protected void initData() {
        Constants.isDFU = false;
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(R.string.select_device_type_watch);
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
        getDeviceList();
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
                            brandapp.isport.com.basicres.commonutil.Logger.e("睡眠带解绑成功");
                            TokenUtil.getInstance().saveSleepTime(BaseApp.getApp(), "");
                        }
                        Logger.myLog("解绑成功");
                        hasWatch = false;
                        //EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
                    }
                }, 200);
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
        Constants.isSyncUnbind = false;
        EventBus.getDefault().unregister(this);
        SyncCacheUtils.setUnBindState(false);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }


    @Override
    public void onUnBindSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.myLog("onUnBindSuccess 解绑成功");
                hasWatch = false;
                ToastUtils.showToast(context, UIUtils.getString(R.string.unpair_successfully));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS));
                GoActivityUtil.goActivityUnbindDevice(currentType, ActivityBindWatch.this);

                BaseDevice currnetDevice = ISportAgent.getInstance().getCurrnetDevice();
                if (currnetDevice != null && currnetDevice.deviceType == currentType) {
                    Logger.myLog("currnetDevice == " + currentType);
                    //解绑设备不用重连
                    ISportAgent.getInstance().unbind(false);

                }
            }
        }, 200);
    }

    @Override
    public void updateWatchDataSuccess(DeviceBean deviceBean) {
        unBindDevice(deviceBean, false);
    }

    @Override
    public void updateSleepDataSuccess(DeviceBean deviceBean) {
        unBindDevice(deviceBean, false);
    }

    @Override
    public void updateWatchHistoryDataSuccess(DeviceBean deviceBean) {
        unBindDevice(mDeviceBean, false);
    }

    @Override
    public void updateFail() {
        ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.error_Connection_timeout));
    }


    public void showUnbindDialog() {
        //w516 w311 w520只会存在一个
        isDerictUnBind = false;
        isDerictUnBind = false;
        mDeviceBean = DeviceTypeUtil.getBindDevcie();

        Logger.myLog(TAG,"------DeviceTypeUtil.getBindDevcie()=" + mDeviceBean.toString() + "");
        if (mDeviceBean == null) {
            return;
        }
        new UnBindDeviceDialog(this, JkConfiguration.DeviceType.WATCH_W516, true, new UnbindStateCallBack() {
            @Override
            public void synUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                if (AppConfiguration.isConnected) {
                    BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();

                    Logger.myLog(TAG,"---------jiebang-vaseDevice="+device.toString());

                    int deviceType = 0;
                    if (device != null) {
                        deviceType = device.deviceType ;

                    }
                    if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                        ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                        return;
                    }
                    if (DeviceTypeUtil.isContainWrishBrand(deviceType) || DeviceTypeUtil.isContainW81(deviceType)) {
                        canUnBind = true;
                        //睡眠带连接
                        ISportAgent.getInstance().requestBle(BleRequest.bracelet_sync_data);
                    } else if (DeviceTypeUtil.isContainWatch(deviceType)) {
                        canUnBind = true;
                        //睡眠带连接
                        String string = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                        ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_DAILY_RECORD, string);
                    } else {
                        ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    }

                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                }
            }

            @Override
            public void dirctUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                unBindDevice(mDeviceBean, true);
            }

            @Override
            public void cancel() {

            }
        }, mDeviceBean.currentType);
    }
}
