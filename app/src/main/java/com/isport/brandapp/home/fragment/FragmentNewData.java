package com.isport.brandapp.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24H_hr_SettingModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_w311_hrModel;
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.SleepDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.observe.W560HrSwtchObservable;
import com.isport.blelibrary.observe.W560OpenSwtchObservable;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.scale.ScaleLockDataResult;
import com.isport.blelibrary.result.impl.scale.ScaleUnLockDataResult;
import com.isport.blelibrary.result.impl.w311.BraceletW311RealTimeResult;
import com.isport.blelibrary.result.impl.w311.BraceletW311SyncComplete;
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult;
import com.isport.blelibrary.result.impl.watch.WatchGOALSTEPResult;
import com.isport.blelibrary.result.impl.watch.WatchGoalCalorieResult;
import com.isport.blelibrary.result.impl.watch.WatchGoalDistanceResult;
import com.isport.blelibrary.result.impl.watch.WatchHrHeartResult;
import com.isport.blelibrary.result.impl.watch.WatchRealTimeResult;
import com.isport.blelibrary.result.impl.watch_w516.WatchW516SyncResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.ScacleBean;
import com.isport.brandapp.home.bean.db.HeartRateMainData;
import com.isport.brandapp.home.bean.db.SleepMainData;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.bean.http.WristbandHrHeart;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.home.customview.MainHeadLayout;
import com.isport.brandapp.home.presenter.FragmentDataPresenter;
import com.isport.brandapp.home.presenter.UpgradeDataPresenter;
import com.isport.brandapp.home.presenter.W81DataPresenter;
import com.isport.brandapp.home.view.DataAddDeviceHolder;
import com.isport.brandapp.home.view.DataDeviceSportHolder;
import com.isport.brandapp.home.view.DataHeaderHolder;
import com.isport.brandapp.home.view.DataHeartRateHolder;
import com.isport.brandapp.home.view.DataRealHeartRateHolder;
import com.isport.brandapp.home.view.DataScaleHolder;
import com.isport.brandapp.home.view.DataSleepHolder;
import com.isport.brandapp.home.view.FragmentDataView;
import com.isport.brandapp.R;
import com.isport.brandapp.Third_party_access.util.GoogleFitUtil;
import com.isport.brandapp.banner.recycleView.RefrushRecycleView;
import com.isport.brandapp.banner.recycleView.adapter.RefrushAdapter;
import com.isport.brandapp.banner.recycleView.holder.CustomHolder;
import com.isport.brandapp.banner.recycleView.inter.DefaultAdapterViewLisenter;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.ActivityBindScale;
import com.isport.brandapp.bind.presenter.BindPresenter;
import com.isport.brandapp.bind.view.BindBaseView;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.bracelet.ReportActivity;
import com.isport.brandapp.device.bracelet.bean.StateBean;
import com.isport.brandapp.device.bracelet.braceletPresenter.DeviceGoalStepPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.HrSettingPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.ThridMessagePresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.W311RealTimeDataPresenter;
import com.isport.brandapp.device.bracelet.braceletPresenter.WeatherPresenter;
import com.isport.brandapp.device.bracelet.view.DeviceGoalStepView;
import com.isport.brandapp.device.bracelet.view.HrSettingView;
import com.isport.brandapp.device.bracelet.view.ThridMeaageView;
import com.isport.brandapp.device.bracelet.view.W311RealTimeDataView;
import com.isport.brandapp.device.scale.ActivityScaleMain;
import com.isport.brandapp.device.watch.ActivityWatchHeartRate;
import com.isport.brandapp.device.watch.ActivityWatchHeartRateIng;
import com.isport.brandapp.device.watch.ActivityWatchSleep;
import com.isport.brandapp.device.watch.presenter.Device24HrPresenter;
import com.isport.brandapp.device.watch.view.Device24HrView;
import com.isport.brandapp.dialog.CommuniteDeviceSyncGuideDialog;
import com.isport.brandapp.dialog.ItemSelectDialog;
import com.isport.brandapp.sport.SportReportActivity;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceDataUtil;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.wu.activity.BPResultActivity;
import com.isport.brandapp.wu.activity.OnceHrDataResultActivity;
import com.isport.brandapp.wu.activity.OxyResultActivity;
import com.isport.brandapp.wu.activity.PractiseRecordActivity;
import com.isport.brandapp.wu.activity.PractiseW520RecordActivity;
import com.isport.brandapp.wu.activity.TempResultActivity;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.bean.TempInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bike.gymproject.viewlibray.pickerview.utils.DateUtils;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.StringUtil;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.entry.WatchRealTimeData;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil;
import phone.gym.jkcq.com.commonres.commonutil.UserUtils;

import static com.isport.brandapp.gaodelibrary.GaodeLibraryService.isCheck;

/**
 * @Author
 * @Date 2018/10/12b
 * @FuctionM 手表主页面
 */

public class FragmentNewData extends BaseMVPFragment<FragmentDataView, FragmentDataPresenter> implements
        FragmentDataView, DataAddDeviceHolder.OnAddOnclickLister, DeviceGoalStepView,
        DataHeaderHolder.OnHeadOnclickLister, DataDeviceSportHolder.OnSportItemClickListener,
        DataDeviceSportHolder.OnSportOnclickListenter, DataScaleHolder.OnScaleItemClickListener,
        DataScaleHolder.OnScaleOnclickListenter, DataHeartRateHolder.OnHeartRateOnclickListenter,
        DataHeartRateHolder.OnHeartRateItemClickListener, DataSleepHolder.OnSleepOnclickListenter,
        DataSleepHolder.OnSleepItemClickListener, W311RealTimeDataView, BindBaseView, ThridMeaageView,
        HrSettingView, Device24HrView, MainHeadLayout.ViewMainHeadClickLister, java.util.Observer {

    private SmartRefreshLayout home_refresh;


    private static final String TAG = FragmentNewData.class.getSimpleName();


    //***************************************************已整理***********************************************//
    RefrushRecycleView refrushRecycleView;
    DataHeaderHolder dataHeaderHolder;//进度条
    // DataDeviceSportHolder dataSportHolder;//运动
    //头部实时心率
    DataRealHeartRateHolder dataRealHeartRateHolder;
    // DataScaleHolder dataScaleHolder;//体脂秤
    DataHeartRateHolder dataHeartRateHolder;//心率
    DataHeartRateHolder dataOxygenDataHolder;
    DataHeartRateHolder dataBloodPresureDataHolder;
    DataHeartRateHolder dataExerciseDataHolder;
    DataHeartRateHolder dataOnceHrHolder;//单次测量心率
    DataHeartRateHolder dataTempHolder;//单次体温
    DataSleepHolder dataSleepHolder;//睡眠

    StateBean stateBean;

    private Device24HrPresenter device24HrPresenter;
    private DeviceGoalStepPresenter deviceGoalStepPresenter;
    private W81DataPresenter w81DataPresenter;
    private UpgradeDataPresenter upgradeDataPresenter;
    private BindPresenter bindPresenter;
    private HrSettingPresenter hrSettingPresenter;
    private WeatherPresenter weatherPresenter;
    private W311RealTimeDataPresenter w311RealTimeDataPresenter;
    private ThridMessagePresenter thridMessagePresenter;


    LinearLayout viewBar;
    /**
     * 是否点击称重条目
     */

    MainHeadLayout mainHeadLayout;
    private int currentStep = -1;
    private Vector<Integer> lists;//item类型列表
    private RefrushAdapter<String> adapter;


    public static final int REQCODE_OPEN_BT = 0x100;


    //    DataAllDeviceHolder dataDeviceAllHolder;//设备列表

    private boolean mHasSleepDevice;
    private boolean isHidden;
    private boolean mIsDirctConnect;
    public BaseDevice mCurrentDevice;
    // private ScaleMainData mScaleMainData;
    private Scale_FourElectrode_DataModel mScale_fourElectrode_dataModel;
    private WatchSportMainData mWatchSportMainData;
    private HeartRateMainData mHeartRateMainData;
    private SleepMainData mSleepMainData, braceletMainData;
    private Sleep_Sleepace_DataModel mSleep_Sleepace_DataModel;
    private int mDelayMillis1 = 500;
    // private String mHomeSportData;
    private boolean mIsConnectByUser;//扫描中，用于判断是否是用户主动连接


    @SuppressLint("HandlerLeak")
    private final Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Logger.myLog(TAG,"scanHandler=" + msg.what);
            switch (msg.what) {
                case 0x05:
                    Logger.myLog(TAG,"scanHandler 0x05 deviceConFail");
                    //defaultConnectState(true);
                    deviceConFail();
                    break;
            }
        }
    };

    /**
     * 30s连接超时
     */
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Logger.myLog(TAG,"handleMessage" + msg.what);
            switch (msg.what) {
                case 0x01:
                    int currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration
                            .DeviceType.WATCH_W516);
                    if (mIsDirctConnect && currentDeviceType == JkConfiguration.DeviceType.BODYFAT) {
                    }
                    if (DeviceTypeUtil.isContainWatch(currentDeviceType) || DeviceTypeUtil.isContainW81(currentDeviceType) || DeviceTypeUtil.isContainWrishBrand(currentDeviceType)) {
                        if (DeviceTypeUtil.isContainW81(currentDeviceType)) {
                            //BraceletW811W814Manager.deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                            //ISportAgent.getInstance().disConDevice(false);
                        }
                        deviceConFail();
                    }
                    defaultConnectState(true);
                    break;
                case 0x02:
                    switch (msg.arg1) {
                        case JkConfiguration.DeviceType.BODYFAT:
                            connectScale();
                            break;
                        case JkConfiguration.DeviceType.SLEEP:
                            connectSleep();
                            break;
                        default:
                            connectWatchOrBracelet(false, msg.arg1);
                            break;
                    }
                    break;
                case 0x03:

                    BaseDevice baseDevice1 = (BaseDevice) msg.obj;
                    devcieConnecting();
                    mFragPresenter.connectDevice(baseDevice1, true, false);
                    setConnectTimeOut();
                    break;
                case 0x04:
                    //退出了体脂秤页面,需要去重连设备,此时有可能已经在体脂秤绑定页面连接成功过了,那么设置当前设备为手表
                    //boolean canReConnect = AppSP.getBoolean(context, AppSP.CAN_RECONNECT, false);
                    //Constants.CAN_RECONNECT = canReConnect; //重连的需求
                    connectDevice();
                    break;

                case 0x05:
                    try {
                        if (dataRealHeartRateHolder != null) {
                            dataRealHeartRateHolder.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;

            }
        }
    };
    /**
     * Event
     *
     * @param messageEvent
     */
    boolean isFirstLocation = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
        String msgStr = messageEvent.getMsg();
        Logger.myLog(TAG,"syncTodayData---------msgStr="+msgStr);
        switch (msgStr) {
            case MessageEvent.syncTodayData:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ISportAgent.getInstance().requestBle(BleRequest.DEVICE_SYNC_TODAY_DATA);
                    }
                }, 500);

                break;
            case MessageEvent.update_location:
                if (!isFirstLocation) {
                    weatherPresenter.getWeather(Constants.mLocationLatitude, Constants.mLocationLongitude, com.isport.blelibrary.utils.Constants.cityName, 814);
                    isFirstLocation = true;
                }
                break;
            case MessageEvent.update_exercise:

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //获取当前的数据类型 81系列跟526设备调用的URL不一样
                        if (DeviceTypeUtil.isContainW81()) {
                            mFragPresenter.getTodaySum(JkConfiguration.DeviceType.Watch_W812);
                        } else {
                            mFragPresenter.getTodaySum(JkConfiguration.DeviceType.Watch_W556);
                        }
                    }
                }, 0);
            case MessageEvent.update_temp:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFragPresenter.getTempData();
                    }
                }, 1500);
                break;
            case MessageEvent.update_bloodpre:
                //  Logger.myLog("MessageEvent:MessageEvent.update_bloodpre:" + currentType);
                if (DeviceTypeUtil.isContainW81()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragPresenter.getDeviceBloodPressure();
                        }
                    }, 1500);

                }
                break;
            case MessageEvent.update_oncehr:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFragPresenter.getDevcieOnceHrData();
                    }
                }, 1500);
                break;
            case MessageEvent.update_oxygen:
                if (DeviceTypeUtil.isContainW81()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragPresenter.getDevcieOxygenData();
                        }
                    }, 1500);

                }
                break;
            case MessageEvent.update_hr:
                //当前的类型是W11
                if (DeviceTypeUtil.isContainW81()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragPresenter.getDeviceHrLastTwoData(currentType);
                        }
                    }, 1500);
                }
                break;
            case MessageEvent.update_sleep:
                if (DeviceTypeUtil.isContainW81()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragPresenter.getDeviceSleepDataFromDB(false, currentType);
                        }
                    }, 1500);

                }
                break;
            case MessageEvent.update_step:
                Logger.myLog(TAG,"MessageEvent.update_step-------" + currentType + "，AppConfiguration.braceletID：" + AppConfiguration.braceletID);
                if (DeviceTypeUtil.isContainW81()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragPresenter.getDeviceStepLastTwoData(currentType);
                        }
                    }, 1000);

                }
                break;
            case MessageEvent.SPORT_UPDATE_SUCESS:
                mFragPresenter.getSportHomeData();
                break;
            case MessageEvent.EXIT_SCALESCAN:
            case MessageEvent.EXIT_SCALEREALTIME:
            case MessageEvent.EXIT_SCALECONNECTTING:
                Logger.myLog(TAG,"EXIT_SCALEREALTIME");
                //会重复的去扫描一直会去重新连接。
                //handler.sendEmptyMessageDelayed(0x04, 0);
                break;
            case MessageEvent.BIND_DEVICE_SUCCESS:
                BaseDevice baseDevice = messageEvent.getBaseDevice();
                //绑定成功直接切换设备
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, baseDevice.deviceType);
                Logger.myLog(TAG,"绑定设备成功 == " + baseDevice.deviceType + " "+baseDevice.toString());
                switch (baseDevice.deviceType) {
                    case JkConfiguration.DeviceType.BRAND_W311:
                    case JkConfiguration.DeviceType.Brand_W520:
                    case JkConfiguration.DeviceType.WATCH_W516:
                    case JkConfiguration.DeviceType.SLEEP:
                        AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.address);
                        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName);
                        break;
                    case JkConfiguration.DeviceType.BODYFAT:
                        //TODO 如果没有绑定直接返回了改怎么显示
                        //绑定体脂秤刷新列表后,由于已经连接过手表，列表李有手表，默认设备可能被设置为手表了，此处应做不设置为默认设备为手表的操作
                        AppSP.putString(context, AppSP.SCALE_MAC, baseDevice.address);
                        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.address);
                        deviceConSuccess(baseDevice);
                        //绑定成功刷新首页列表
                        break;
                    default:
                        AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.address);
                        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName);
                        break;
                }
                //绑定成功先取本地的数据再刷服务器的数据
                mFragPresenter.getDeviceList(true, false, false);
                break;
            case MessageEvent.UNBIND_DEVICE_SUCCESS:
                //解绑其他设备后不要重连手表
                isConnecting = false;
                mFragPresenter.getDeviceList(false, false, true);
                break;
            case MessageEvent.UPDATE_SCALE_DATA_SUCCESS:
                mFragPresenter.getMainScaleDataFromDB(false, false);
                break;
            case MessageEvent.UPDATE_WATCH_TARGET:
                //更新圆圈的数据
                successWatchHistoryDataFormHttp(false, JkConfiguration.WatchDataType.STEP);
                break;
            case MessageEvent.UPDATE_SLEEP_DATA_SUCCESS:
                mFragPresenter.getMainSleepDataFromDB(false);
                break;
            case MessageEvent.BIND_DEVICE_SUCCESS_WITH_PROGRESS:
                BaseDevice baseDevice1 = messageEvent.getBaseDevice();
                Message message = new Message();
                message.obj = baseDevice1;
                message.what = 0x03;
                handler.sendMessageDelayed(message, 200);
                break;
            case MessageEvent.UPDATE_BRACELET_DATA_SUCCESS:
                mFragPresenter.getMainW311DataFromDB(false);
                mFragPresenter.getMainW311StandDataFromDB(false);
                break;
            case MessageEvent.UPDATE_WATCH_DATA_SUCCESS:
                mFragPresenter.getMainW516DataFromDB(false);
                mFragPresenter.getMainW516StandDataFromDB(false);
                break;
            case MessageEvent.SYNC_WATCH_SUCCESS:
                // TODO: 2019/1/12 更新心率值有问题
                mFragPresenter.getMainW311DataFromDB(false);
                mFragPresenter.getMainW516DataFromDB(false);
                mFragPresenter.getmainW81DataFromDb(false);
                // successWatchHeartRateHistoryDataFormHttp();
                //successWatchStepHistoryDataFormHttp(false);
                break;
            case MessageEvent.reconnect_device:
                //体脂秤被取消重新连接
                if (ISportAgent.getInstance().getCurrnetDevice() != null && ISportAgent.getInstance().getCurrnetDevice().deviceType == JkConfiguration.DeviceType.BODYFAT && AppConfiguration.isConnected) {
                    ISportAgent.getInstance().disConDevice(false);
                } else {
                    ISportAgent.getInstance().cancelLeScan();
                    AppConfiguration.isScaleScan = false;
                    AppConfiguration.isScaleConnectting = false;
                    isConnecting = false;
                    mHasSleepDevice = true;
                    if (mainHeadLayout != null) {
                        mainHeadLayout.showProgressBar(false);
                    }
                    reconnectDevice(JkConfiguration.DeviceType.BODYFAT);
                }

                break;
            case MessageEvent.scale_device_success:
                deviceConSuccess(ISportAgent.getInstance().getCurrnetDevice());
                break;
            default:
                break;
        }
    }


    /** 刷新UI条目**/


    /**
     * 刷新首页列表,运动部分暂时是分离开刷新的,是区分网络版和单机版的
     */
    private void refreshDeviceList() {
        if (AppConfiguration.deviceBeanList.size() > 0) {
            if (refrushRecycleView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE ||
                    !refrushRecycleView.isComputingLayout()) {
                adapter.notifyDataSetChanged();
            }
        } else {
            if (refrushRecycleView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE ||
                    !refrushRecycleView.isComputingLayout()) {
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void refreshSport() {
       /* new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
              *//*  if (dataSportHolder != null) {
                    dataSportHolder.setSportHolder(mHomeSportData);
                }*//*
            }
        }, mDelayMillis1);
        // }*/

    }

    private void refreshHeader() {
        if (dataHeaderHolder != null) {
            if (mWatchSportMainData != null && (DeviceTypeUtil.isContainWatchOrBracelet() || DeviceTypeUtil.isContainW81())) {
                dataHeaderHolder.updateUI(mWatchSportMainData);
            } else {
                dataHeaderHolder.defUpdateUI();
            }
        }


    }


    /**
     * 刷新心率item
     */
    private void refreshHeartRate() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataHeartRateHolder != null) {
                    if (mHeartRateMainData != null && DeviceTypeUtil.isContainWatchOrBracelet()) {
                        dataHeartRateHolder.updateUI(mHeartRateMainData.getDateStr(), mHeartRateMainData.getHeartRate());
                    } else {
                        dataHeartRateHolder.updateUI("", 0);
                    }

                }
            }
        }, mDelayMillis1);
    }

    private void refreshScale() {

       /* new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.myLog("getConectScale scacleBeansList == " + scacleBeansList.size() + "dataScaleHolder:" + dataScaleHolder);
                if (dataScaleHolder != null) {
                    dataScaleHolder.updateUI(scacleBeansList, 0);
                }
            }
        }, 0);*/
    }

    /**
     * 刷新UI条目
     **/
    int currentHr;
    int hasHrDataCount = 0;
    private int age;
    private String sex;
    boolean isShow = true;
    private final BleReciveListener mBleReciveListener = new BleReciveListener() {

        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            Logger.myLog("连接成功 Constants.isDFU" + Constants.isDFU + "isConn:" + isConn);
            //如果是体脂称需要是绑定了才去进行操作

            //1.因为体脂秤是先连接后绑定，所有在这里如果是连接的体脂秤，然后没有绑定就需要过滤


            if (baseDevice == null) {
                if (!isConn) {
                    deviceConFail();

                }
                return;
            }
            if (!isConn) {
                if (baseDevice.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    ISportAgent.getInstance().clearCurrentDevice();
                }
            }
            if (baseDevice.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                if (AppConfiguration.deviceBeanList == null || AppConfiguration.deviceBeanList.size() == 0 || !AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                    return;
                }
            }

            BleProgressObservable.getInstance().hide();
            if (Constants.isDFU) {
                if (!isConn) {
                    deviceConFail();

                    if (dataHeartRateHolder != null) {
                        dataHeartRateHolder.updateState();
                    }
                }
                return;
            } else {
                if (isConn) {
                    if (baseDevice.deviceType == 812) {
                        if (!AppConfiguration.deviceBeanList.containsKey(812)) {
                            ISportAgent.getInstance().disConDevice(false);
                            ISportAgent.getInstance().clearCurrentDevice();
                            Logger.myLog("连接成功 Constants.isDFU" + 812);
                            return;
                        }
                    }
                }
            }

            Logger.myLog(TAG,"isConn" + isConn);
            AppConfiguration.isConnected = isConn;
            currentStep = -1;
            ISportAgent.getInstance().cancelLeScan();

            if (isConn) {
                //连接成功
                AppSP.putString(context, AppSP.FORM_DFU, "false");
                deviceConSuccess(baseDevice);
            } else {
                deviceConFail();
                if (dataRealHeartRateHolder != null) {
                    dataRealHeartRateHolder.bleCloseUpdateUi();
                }
            }
            //体脂秤断连的回调，去重连手表
            if (!isConn) {
                switch (baseDevice.deviceType) {
                    case JkConfiguration.DeviceType.BODYFAT:
                        //不再绑定搜索页面才做重连操作,不再称重页面时
                        reconnectDevice(JkConfiguration.DeviceType.BODYFAT);
                }
            }
            if (dataHeartRateHolder != null) {
                dataHeartRateHolder.updateState();
            }
        }

        @Override
        public void setDataSuccess(String s) {

        }

        public boolean isBindScale() {
            if (AppConfiguration.deviceBeanList == null || AppConfiguration.deviceBeanList.size() == 0) {
                return false;
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                return true;
            }
            return false;
        }

        @Override
        public void receiveData(IResult mResult) {
            int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            String currentDeviceName = AppSP.getString(context, AppSP.DEVICE_CURRENTNAME, "");
            Logger.myLog(TAG,"--receiveData currentType="+currentType+" mResult="+mResult.getType());
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.WATCH_REALTIME_HR:
                        if (loginBean != null) {
                            WatchHrHeartResult mResultHr = (WatchHrHeartResult) mResult;
                            currentHr = mResultHr.getHeartRate();
                            Log.e("mResultHr", currentHr + "");
                            if (dataRealHeartRateHolder != null && currentHr >= 30) {
                                handler.removeMessages(0x05);
                                handler.sendEmptyMessageDelayed(0x05, 5000);
                                hasHrDataCount = 0;
                                dataRealHeartRateHolder.hidelayout_hr();
                                dataRealHeartRateHolder.setCheckbox_hr_state();

                                String birthday = loginBean.getBirthday();
                                age = UserUtils.getAge(birthday);
                                sex = loginBean.getGender();

                                if (isShow) {
                                    dataRealHeartRateHolder.setLineDataAndShow(currentHr, age, sex, false);
                                } else {
                                    dataRealHeartRateHolder.setLineDataAndShow(currentHr, age, sex, true);
                                }
                            }
                            hasHrDataCount++;
                            if (dataRealHeartRateHolder != null && hasHrDataCount == 5) {
                                dataRealHeartRateHolder.close();
                            }
                        } else {
                            loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                            String birthday = loginBean.getBirthday();
                            age = UserUtils.getAge(birthday);
                            sex = loginBean.getGender();
                        }
                        // tvRateTime.setText(TimeUtil.getTimerFormatedStrings(startTime, startTime + (aLong - pauseTime) * 1000));

                        break;
                    //设备测量结果成功
                    case IResult.DEVICE_MESSURE:
                        try {
                            DeviceMessureDataResult deviceMessureDataResult = (DeviceMessureDataResult) mResult;

                            Logger.myLog(TAG,"---DEVICE_MESSURE----deviceMessureDataResult="+deviceMessureDataResult.toString());
                            String deviceName = deviceMessureDataResult.getMac();
                            switch (deviceMessureDataResult.getMessureType()) {
                                case DeviceMessureData.update_weather:
                                    if (mCurrentDevice != null && Constants.mLocationLatitude != Constants.mLocationLongitude) {
                                        weatherPresenter.getWeather(Constants.mLocationLatitude, Constants.mLocationLongitude, Constants.cityName, mCurrentDevice.deviceType);
                                    }
                                    break;

                                case DeviceMessureData.today_temp:
                                    mFragPresenter.getTempData();
                                    upgradeDataPresenter.updateTempData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                                    break;
                                //更新当天的心率数据
                                case DeviceMessureData.today_hr:
                                    mFragPresenter.getDeviceHrLastTwoData(currentType);
                                    break;
                                case DeviceMessureData.measure_hr:
                                    mFragPresenter.getDeviceHrLastTwoData(JkConfiguration.DeviceType.Watch_W812);
                                    break;
                                case DeviceMessureData.measure_bloodpre:
                                    Logger.myLog(TAG,"measure_bloodpre success");
                                    mFragPresenter.getDeviceBloodPressure();
                                    upgradeDataPresenter.upgradeBPData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                                    break;
                                case DeviceMessureData.measure_oxygen:
                                    Logger.myLog("measure_oxygen success");
                                    mFragPresenter.getDevcieOxygenData();
                                    upgradeDataPresenter.upgradeOxyenData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                                    break;

                                case DeviceMessureData.measure_once_hr:
                                    Logger.myLog("measure_once_hr success");
                                    mFragPresenter.getDevcieOnceHrData();
                                    upgradeDataPresenter.upgradeOnceHrData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                                    break;
                                case DeviceMessureData.measure_exercise:
                                    Logger.myLog("mDevcieExecise");
                                    if (AppConfiguration.deviceBeanList != null) {
                                        if (DeviceTypeUtil.isContainW55XX()) {
                                            upgradeDataPresenter.upgradeExeciseData(JkConfiguration.DeviceType.Watch_W556, deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                                            // mFragPresenter.getExerciseTodaySum(JkConfiguration.DeviceType.Watch_W556);
                                        } else {
                                            upgradeDataPresenter.upgradeExeciseData(JkConfiguration.DeviceType.Watch_W812, deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                                            //mFragPresenter.getExerciseTodaySum(JkConfiguration.DeviceType.Watch_W812);
                                        }
                                    }
                                    // mFragPresenter.getTodaySum();
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case IResult.DEVICE_GOAL_STEP:  //步数目标
                        AppSP.putInt(getActivity(),AppSP.DEVICE_GOAL_KEY,0);
                        //保存获取的获取的运动步数
                        try {
                            WatchGOALSTEPResult watchGOALSTEPResult = (WatchGOALSTEPResult) mResult;
                            deviceGoalStepPresenter.saveDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), watchGOALSTEPResult.getMac(), watchGOALSTEPResult.getGoalStep(),0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case IResult.DEVICE_GOAL_DISTANCE:  //距离目标
                        AppSP.putInt(getActivity(),AppSP.DEVICE_GOAL_KEY,1);
                        WatchGoalDistanceResult watchGoalDistanceResult = (WatchGoalDistanceResult) mResult;
                        deviceGoalStepPresenter.saveDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), watchGoalDistanceResult.getMac(), watchGoalDistanceResult.getGoalDistance(),1);
                        break;
                    case IResult.DEVICE_GOAL_CALORIE:   //卡路里目标
                        AppSP.putInt(getActivity(),AppSP.DEVICE_GOAL_KEY,2);
                        WatchGoalCalorieResult watchGoalCalorieResult = (WatchGoalCalorieResult) mResult;
                        deviceGoalStepPresenter.saveDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()),watchGoalCalorieResult.getMac(),watchGoalCalorieResult.getGoalCalorie(),2);
                        break;
                    case IResult.BRACELET_W311_START_SYNC_DATA:
                        //手环和手表开始同步
                        //311同步数据中
                        startSyncDevice();

                        break;
                    case IResult.BRACELET_W311_COMPTELETY:
                        //311同步数据完成
                        SyncCacheUtils.saveStartSync(BaseApp.getApp());
                        // refreshLayout.finishRefresh(2000);
                        endSyncDevice();
                        //SyncProgressObservable.getInstance().hide();
                        BraceletW311SyncComplete braceletW311SyncComplete = (BraceletW311SyncComplete) mResult;
                        if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.SUCCESS) {
                            SyncCacheUtils.saveSyncDataTime(BaseApp.getApp());
                            SyncCacheUtils.saveStartSync(BaseApp.getApp());
                            //去同步googoleFit的数据
                            successeDeviceSyncComplety(JkConfiguration.DeviceType.BRAND_W311);
                            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                            if (device != null) {
                                sysncDevcieDataToServer(device.deviceType);
                            }
                        } else if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.FAILED) {

                        } else if (braceletW311SyncComplete.getSuccess() == BraceletW311SyncComplete.TIMEOUT) {

                        }
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.SYNC_WATCH_SUCCESS));
                        break;
                    case IResult.BRACELET_W311_REALTIME:

                        if (mCurrentDevice == null || loginBean == null) {
                            return;
                        }
                        int w81 = mCurrentDevice.deviceType;
                        BraceletW311RealTimeResult w311mResult2 = (BraceletW311RealTimeResult) mResult;
                        int cal = w311mResult2.getCal();
                        float dis = w311mResult2.getStepKm();

                        Logger.myLog(TAG,"-------步数汇总="+w311mResult2.toString());

                        if (DeviceTypeUtil.isContainWrishBrand(w81) && w311RealTimeDataPresenter != null) {
                            w311RealTimeDataPresenter.saveRealTimeData(loginBean.getUserId(), mCurrentDevice.getDeviceName(), w311mResult2.getStepNum(), dis, cal, DateUtils.getYMD(System.currentTimeMillis()), w311mResult2.getMac());
                        }
                        //更新UI

                        if (mWatchSportMainData == null) {
                            mWatchSportMainData = new WatchSportMainData();
                        }
                        mWatchSportMainData.setStep(w311mResult2.getStepNum() + "");
                        mWatchSportMainData.setCal(cal + "");
                        mWatchSportMainData.setDistance(CommonDateUtil.formatTwoPoint(dis));
                        mWatchSportMainData.setDateStr(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()));
                        refreshHeader();


                        //需要去取数据的数据前一天的和当天的数据进行比较

                        break;
                    case IResult.SCALE_UN_LOCK_DATA:
                        //如果当前的体脂秤没有绑定就不需要刷新数据
                        if (isBindScale() && currentType == JkConfiguration.DeviceType.BODYFAT) {
                            ScaleUnLockDataResult mResult1 = (ScaleUnLockDataResult) mResult;
                            //实时的圈不要实时刷新圆环
                        }
                        break;
                    case IResult.SCALE_LOCK_DATA:
                        if (isBindScale() && currentType == JkConfiguration.DeviceType.BODYFAT) {
                            ScaleLockDataResult mResult2 = (ScaleLockDataResult) mResult;
                        }
                        break;
                    case IResult.SLEEP_BATTERY:
                        //主页获取到电量后就直接连接成功,只在起床时同步数据
                        if (AppConfiguration.isSleepBind) {
                            //在绑定页面连接时主页不做同步数据操作
                            return;
                        }
                        if (AppConfiguration.isSleepRealTime) {
                            //在实时页面时主页也不做同步操作
                            defaultConnectState(true);
                            return;
                        }
                        ISportAgent.getInstance().requestBle(BleRequest.Common_GetVersion);
                        // TODO: 2018/11/12 睡眠带同步数据的逻辑移至连接处，连接完成同步数据上传到服务器
                        defaultConnectState(true);
                        break;
                    case IResult.SCALE_BATTERY:
                        ISportAgent.getInstance().requestBle(BleRequest.Common_GetVersion);
                        break;
                    case IResult.SCALE_VERSION:
                        defaultConnectState(true);
                        break;
                    case IResult.WATCH_W516_SETTING:
                        /*if (AppConfiguration.hasSynced) {
                            Intent intentHr = new Intent(getActivity(), ActivityWatchHeartRateIng.class);
                            intentHr.putExtra(JkConfiguration.DEVICE, mCurrentDevice.getDeviceName());
                            startActivity(intentHr);
                        }*/
                        break;
                    case IResult.WATCH_VERSION:
                        if (AppConfiguration.isSleepBind) {
                            //在绑定页面连接时主页不做同步数据操作
                            return;
                        }
                        break;
                    case IResult.WATCH_W516_SYNC:

                        Logger.myLog("同步成功1");
                        //同步数据是否成功
                        if (AppConfiguration.isSleepBind) {
                            //在绑定页面连接时主页不做同步数据操作
                            return;
                        }
                        WatchW516SyncResult watchW516SyncResult = (WatchW516SyncResult) mResult;
                        if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SUCCESS) {
                            //SyncProgressObservable.getInstance().hide();
                            endSyncDevice();
                            SyncCacheUtils.saveSyncDataTime(BaseApp.getApp());
                            SyncCacheUtils.saveStartSync(BaseApp.getApp());
                            successeDeviceSyncComplety(JkConfiguration.DeviceType.WATCH_W516);
                            // mFragPresenter.getMainW516SleepDataFromDB(false);
                            Logger.myLog("同步成功2");
                            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                            if (device != null) {
                                sysncDevcieDataToServer(device.deviceType);
                            }
                            // sysncDevcieDataToServer(JkConfiguration.DeviceType.WATCH_W516);
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.SYNC_WATCH_SUCCESS));
                            // if (mIsConnectByUser)
                            // ToastUtils.showToast(context, R.string.app_issync_complete);
                        } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.FAILED) {
                            endSyncDevice();
                            // mFragPresenter.getMainW516SleepDataFromDB(false);
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.SYNC_WATCH_SUCCESS));
                        } else if (watchW516SyncResult.getSuccess() == WatchW516SyncResult.SYNCING) {
                        }
                        break;
                    case IResult.WATCH_BATTERY:
                        break;
                    case IResult.WATCH_SPORTDATALIST:
                        break;
                    case IResult.WATCH_REALTIME:
                        WatchRealTimeResult mResult2 = (WatchRealTimeResult) mResult;
                        if (loginBean == null) {
                            loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                        }
                        if (mResult2.getStepNum() == currentStep) {
                            return;
                        }
                        if (mCurrentDevice == null || loginBean == null) {
                            return;
                        }
                        currentStep = mResult2.getStepNum();
                        int realcal;
                        float realdis;
                        if (((WatchRealTimeResult) mResult).getMac().contains("W516")) {
                            realcal = (int) StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(loginBean.getWeight()), mResult2.getStepNum());
                            realdis = StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(loginBean.getHeight()), loginBean.getGender(), mResult2.getStepNum());
                        } else {
                            realcal = (((WatchRealTimeResult) mResult).getCal());
                            float mStep = CommonDateUtil.formatFloor(mResult2.getStepKm(), true);
                            realdis = mStep;
                        }
                        Logger.myLog("WATCH_REALTIME:realcal" + realcal + ",realdis:" + realdis + "mResult2.getMac():" + mResult2.getMac());
                        if (w311RealTimeDataPresenter != null) {
                            w311RealTimeDataPresenter.saveW526RealTimeData(currentStep, realcal, realdis, mResult2.getMac());
                        }
                        if (mWatchSportMainData == null) {
                            mWatchSportMainData = new WatchSportMainData();
                        }
                        mWatchSportMainData.setStep(currentStep + "");
                        mWatchSportMainData.setCal(realcal + "");
                        mWatchSportMainData.setDistance(CommonDateUtil.formatTwoPoint(realdis));
                        mWatchSportMainData.setDateStr(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()));
                        refreshHeader();
                        break;
                    default:
                        break;
                }
        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {
            isConnecting = true;
            if (dataHeaderHolder != null) {
                // Logger.myLog("connectNRF 重连中 onConnecting");
                //isConnecting = true;
                //mainHeadLayout.setIconDeviceAlp(1);
                // mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.app_isconnecting));

            }
        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {

        }
    };

    boolean isConnecting = false;

    @Override
    public void onResume() {
        super.onResume();
        loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        // getNetWork();
        Logger.myLog("fragmentNewData:onResume");
        ISportAgent.getInstance().requestBle(BleRequest.real_hr_switch);
    }

    public void getNetWork() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isA = NetworkUtils.isAvailable();
                emitter.onNext(isA);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(this.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean tempInfo) {
                if (!tempInfo) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                mFragPresenter.getSportHomeData();
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }

    public void startqeustData() {
        Logger.myLog(TAG,"----startqeustData----");
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isA = NetworkUtils.isAvailable();
                emitter.onNext(isA);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(this.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean tempInfo) {
                loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                int deviceType = setDeviceBraceletID();
                String deviceName = AppConfiguration.braceletID;
                BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                if (DeviceTypeUtil.isContainW81(deviceType)) {
                    mFragPresenter.getDeviceHrLastTwoData(deviceType);
                    mFragPresenter.getDeviceStepLastTwoData(deviceType);
                    mFragPresenter.getDeviceSleepDataFromDB(false, deviceType);
                    mFragPresenter.getDeviceBloodPressure();
                } else if (DeviceTypeUtil.isContainW55X()) {
                    mFragPresenter.getMainW516DataFromDB(false);
                }
                if (DeviceTypeUtil.isContainW55X()) {
                    mFragPresenter.getTempData();
                    Logger.myLog("w526 upgradeExeciseData:");
                }
                if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {  //体脂称
                    mFragPresenter.getMainScaleDataFromDB(false, false);
                }
                if (!tempInfo) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                mFragPresenter.syncUdateTime();
                mFragPresenter.getSportHomeData();
                if (device != null) {
                    sysncDevcieDataToServer(device.deviceType);
                } else {
                    sysncDevcieDataToServer(deviceType);
                }
                if (DeviceTypeUtil.isContainW81(deviceType)) {
                    mFragPresenter.getTodaySum(JkConfiguration.DeviceType.Watch_W812);
                    upgradeDataPresenter.upgradeExeciseData(JkConfiguration.DeviceType.Watch_W812, deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.upgradeBPData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.upgradeOxyenData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.upgradeOnceHrData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                }


                if (DeviceTypeUtil.isContainW55XX()) {
                    mFragPresenter.getTodaySum(JkConfiguration.DeviceType.Watch_W556);
                    upgradeDataPresenter.upgradeBPData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.upgradeOxyenData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.updateTempData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.upgradeExeciseData(JkConfiguration.DeviceType.Watch_W556, deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    upgradeDataPresenter.upgradeOnceHrData(deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));

                    upgradeDataPresenter.upgradeExeciseData(JkConfiguration.DeviceType.Watch_W560, deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                }

            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }


    long lastClickTime = 0;
    long currentClickTime = 0;


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.myLog(TAG,"----onHiddenChanged-----");
        //需要对这个mCurrentDevice进行处理
        //  mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
        isHidden = hidden;
        if (!isHidden) {
        }
        Logger.myLog(TAG,"FragmentNewData onHiddenChanged" + isHidden + "lastclick:" + (currentClickTime - lastClickTime));
        if (AppConfiguration.deviceBeanList == null || AppConfiguration.deviceBeanList.size() == 0) {
            return;
        }
        if (!isHidden) {
            Logger.myLog("FragmentNewData onHiddenChanged" + isHidden + "lastclick:" + (currentClickTime) + "lastClickTime:" + lastClickTime + "----" + (currentClickTime - lastClickTime));
            currentClickTime = System.currentTimeMillis();
            if (lastClickTime == 0) {
                lastClickTime = System.currentTimeMillis();
                startqeustData();
            } else {
                Logger.myLog("FragmentNewData onHiddenChanged2" + isHidden + "lastclick:" + (currentClickTime) + "lastClickTime:" + lastClickTime + "----" + (currentClickTime - lastClickTime));
                if (currentClickTime - lastClickTime > 10 * 1000) {
                    lastClickTime = currentClickTime;
                    startqeustData();
                }
            }

        }

        //Logger.myLog("fragmentNewData:onHiddenChanged:" + hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Logger.myLog(TAG,"fragmentNewData:setUserVisibleHint" + isVisibleToUser);
        if (isVisibleToUser) {

            //页面可见时相关逻辑

        } else {

            //页面不可见时相关逻辑

        }

    }


    @Override
    public boolean getUserVisibleHint() {

        return super.getUserVisibleHint();
    }

    /**
     * 初始化
     */

    UserInfoBean loginBean;

    @Override
    protected void initData() {
        isConnecting = false;
        mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
        W560HrSwtchObservable.getInstance().addObserver(this);
        boolean isFirst = TokenUtil.getInstance().getKeyValue(getActivity(), TokenUtil.DEVICE_SYNC_FIRST);
        Logger.myLog(TAG + isFirst + "TokenUtil.DEVICE_SYNC_FIRST=" + TokenUtil.DEVICE_SYNC_FIRST);
        if (!isFirst) {
            CommuniteDeviceSyncGuideDialog dialog = new CommuniteDeviceSyncGuideDialog(getActivity(), TokenUtil.DEVICE_SYNC_FIRST, R.style.AnimTop);
            dialog.showDialog();
        }
        initRecycleViewItems();
        adapter = new RefrushAdapter<>(getActivity(), lists, R.layout.item, new DefaultAdapterViewLisenter() {
            @Override
            public CustomHolder getBodyHolder(Context context, List lists, int itemID) {
                return null;
            }

            @Override
            public CustomHolder getHeader(Context context, List lists, int itemID) {
                dataHeaderHolder = new DataHeaderHolder(context, lists, R.layout.app_fragment_data_head);
                dataHeaderHolder.setOnCourseOnclickLister(FragmentNewData.this);
                return dataHeaderHolder;
            }

            @Override
            public CustomHolder getOxyGenItem(Context context, List lists, int itemID) {
                dataOxygenDataHolder = new DataHeartRateHolder(context, lists, R.layout
                        .app_fragment_data_device_item, JkConfiguration.BODY_OXYGEN);
                dataOxygenDataHolder.setHeartRateItemClickListener(FragmentNewData.this, FragmentNewData.this);
                mFragPresenter.getDevcieOxygenData();
                return dataOxygenDataHolder;
            }

            @Override
            public CustomHolder getTempItem(Context context, List lists, int itemID) {
                dataTempHolder = new DataHeartRateHolder(context, lists, R.layout
                        .app_fragment_data_device_item, JkConfiguration.BODY_TEMP);
                dataTempHolder.setHeartRateItemClickListener(FragmentNewData.this, FragmentNewData.this);
                mFragPresenter.getTempData();
                //mFragPresenter.getDevcieOnceHrData();
                return dataTempHolder;
            }

            @Override
            public CustomHolder getOnceHrItem(Context context, List lists, int itemID) {
                dataOnceHrHolder = new DataHeartRateHolder(context, lists, R.layout
                        .app_fragment_data_device_item, JkConfiguration.BODY_ONCE_HR);
                dataOnceHrHolder.setHeartRateItemClickListener(FragmentNewData.this, FragmentNewData.this);
                mFragPresenter.getDevcieOnceHrData();
                return dataOnceHrHolder;
            }

            @Override
            public CustomHolder getBloodPressureItem(Context context, List lists, int itemID) {
                //return super.getBloodPressureItem(context, lists, itemID);
                dataBloodPresureDataHolder = new DataHeartRateHolder(context, lists, R.layout
                        .app_fragment_data_device_item, JkConfiguration.BODY_BLOODPRESSURE);
                dataBloodPresureDataHolder.setHeartRateItemClickListener(FragmentNewData.this, FragmentNewData.this);

                mFragPresenter.getDeviceBloodPressure();

                return dataBloodPresureDataHolder;
            }

            @Override
            public CustomHolder getExecericeItem(Context context, List lists, int itemID) {
                Logger.myLog(TAG,"-----getExecericeItem="+new Gson().toJson(lists)+" "+itemID);
                dataExerciseDataHolder = new DataHeartRateHolder(context, lists, R.layout
                        .app_fragment_data_device_item, JkConfiguration.BODY_EXCERICE);
                dataExerciseDataHolder.setHeartRateItemClickListener(FragmentNewData.this, FragmentNewData.this);
                int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
                if (AppConfiguration.deviceBeanList != null) {
                    if (DeviceTypeUtil.isContainW55XX()) {
                        mFragPresenter.getExerciseTodaySum(JkConfiguration.DeviceType.Watch_W556);
                    } else {
                        mFragPresenter.getExerciseTodaySum(JkConfiguration.DeviceType.Watch_W812);
                    }
                }

                return dataExerciseDataHolder;
            }

            @Override
            public CustomHolder getFooter(Context context, List lists, int itemID) {
                return null;
            }


            @Override
            public CustomHolder getConect1(Context context, List lists, int itemID) {
                dataRealHeartRateHolder = new DataRealHeartRateHolder(context, lists, R.layout.app_fragment_hr_head);
                dataRealHeartRateHolder.setOnHeartRateSwitch(new DataRealHeartRateHolder.OnHeartRateSwitch() {
                    @Override
                    public void onOpen(boolean isOpen) {
                        onHeartRateStateListenter(isOpen);

                    }
                });
                return dataRealHeartRateHolder;
               /* dataSportHolder = new DataDeviceSportHolder(context, lists, R.layout.app_fragment_data_sport_item);
                dataSportHolder.setSpoartItemClickListener(FragmentNewData.this, FragmentNewData.this);
                mFragPresenter.getSportHomeData();
                // refleshSport();
                return dataSportHolder;*/
            }

            @Override
            public CustomHolder getConect2(Context context, List lists, int itemID) {
                return null;
            }

            @Override
            public CustomHolder getConectScale(Context context, List lists, int itemID) {
               /* dataScaleHolder = new DataScaleHolder(context, lists, R.layout
                        .app_fragment_data_device_scale_item);*/

                // Logger.myLog("getConectScale");
                // dataScaleHolder.setScaleItemClickListener(FragmentNewData.this, FragmentNewData.this);
               /* //  refleshScale();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   mFragPresenter.getMainScaleDataFromDB(false, false);
                    }
                }, 1500);*/

                return null;
            }

            @Override
            public CustomHolder getConectHeartRate(Context context, List lists, int itemID) {
                dataHeartRateHolder = new DataHeartRateHolder(context, lists, R.layout
                        .app_fragment_data_device_item, JkConfiguration.BODY_HEARTRATE);
                dataHeartRateHolder.setHeartRateItemClickListener(FragmentNewData.this, FragmentNewData.this);
                return dataHeartRateHolder;
            }

            @Override
            public CustomHolder getConectSleep(Context context, List lists, int itemID) {
                dataSleepHolder = new DataSleepHolder(context, lists, R.layout
                        .app_fragment_data_device_item);
                dataSleepHolder.setSleepItemClickListener(FragmentNewData.this, FragmentNewData.this);
                return dataSleepHolder;
            }
        });
        //adapter.addHead(mineHeaderHolder);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        refrushRecycleView.setLayoutManager(manager);
        refrushRecycleView.setAdapter(adapter);
        mFragPresenter.getDeviceList(true, false, true);

    }

    /**
     * 上传未上传的数据
     */
    private void updateHistoryData() {
        updateScaleHistoryData(false);
        // updateSleepHistoryData(false);
        // updateWatchHistoryData(false);
    }

    /**
     * 初始化 默认列表展示
     */
    private void initRecycleViewItems() {
        lists = new Vector<>();
        //显示的顺序按照list加入的顺序显示


        if (DeviceTypeUtil.isContainsHr()) {
            lists.add(JkConfiguration.BODY_DEVICE1);//实时测试
        }
        lists.add(JkConfiguration.BODY_HEADER);//手表步数展示,默认项
        lists.add(JkConfiguration.BODY_SLEEP);//睡眠

        if (DeviceTypeUtil.isContainW307J()) {
        } else {
            lists.add(JkConfiguration.BODY_HEARTRATE);//心率
        }

//        lists.add(JkConfiguration.BODY_ONCE_HR);  //单次心率
//        lists.add(JkConfiguration.BODY_BLOODPRESSURE);  //血压
//        lists.add(JkConfiguration.BODY_OXYGEN);     //血氧



       /* lists.add(JkConfiguration.BODY_BLOODPRESSURE);
        lists.add(JkConfiguration.BODY_OXYGEN);
        lists.add(JkConfiguration.BODY_EXCERICE);*/
        //  lists.add(JkConfiguration.BODY_DEVICE1);//运动
        //lists.add(JkConfiguration.BODY_SCALE);//体脂秤
        // lists.add(JkConfiguration.BODY_DEVICE);//设备列表项,睡眠、体重、心率、运动
        //      lists.add(JkConfiguration.BODY_DEVICE2);//添加项


    }

    /**
     * 连接超时监听30秒
     */
    private void setConnectTimeOut() {
        if (handler.hasMessages(0x01)) {
            handler.removeMessages(0x01);
        }
        handler.sendEmptyMessageDelayed(0x01, 30000);
    }

    /**
     * 连接超时监听30秒
     */
    private void setScanTimeOut() {
        if (scanHandler.hasMessages(0x05)) {
            scanHandler.removeMessages(0x05);
        }
        //scanHandler.sendEmptyMessage(0x05);
      //  scanHandler.sendEmptyMessageDelayed(0x05, 20000);
        Logger.myLog("setScanTimeOut");
    }

    /**
     * 定位权限的情况
     *
     * @param isScale 体脂秤扫描是没有超时时间的
     */
    private void requestPermission(boolean isScale, boolean isConnectByUser) {
        try {
            PermissionManageUtil permissionManage = new PermissionManageUtil(context);
            RxPermissions mRxPermission = new RxPermissions(this);
            if (!mRxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionManage.requestPermissions(new RxPermissions(this), Manifest.permission.ACCESS_FINE_LOCATION,
                        UIUtils.getString(R.string.permission_location0), new
                                PermissionManageUtil.OnGetPermissionListener() {


                                    @Override
                                    public void onGetPermissionYes() {
                                        startScan(isScale, isConnectByUser);
                                    }

                                    @Override
                                    public void onGetPermissionNo() {
                                        ToastUtils.showToastLong(context, UIUtils.getString(R.string.location_permissions));
                                    }
                                });
            } else {
                startScan(isScale, isConnectByUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 点击取扫描,体脂秤扫描会弹出请上称提示
     *
     * @param isScale
     */
    private void startScan(boolean isScale, boolean isConnectByUser) {
        if (AppUtil.isOpenBle()) {
            int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            mHasSleepDevice = false;
            this.mIsConnectByUser = isConnectByUser;
            Logger.myLog("mFragPresenter.scan(currentType)" + currentType);
            mFragPresenter.scan(currentType, isScale);
            setScanTimeOut();
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQCODE_OPEN_BT);
        }
    }


    /**
     * 体脂秤item点击,进入体脂秤历史页面
     */
    @Override
    public void onScaleItemClick() {
        //体脂称
        boolean isTrue = (mScale_fourElectrode_dataModel == null || !AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT));
        // Logger.myLog("mScale_fourElectrode_dataModel:" + mScale_fourElectrode_dataModel + "AppConfiguration.deviceBeanList.containsKey(JkConfiguration.BODY):" + AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT) + "------isTrue" + isTrue);
        if (isTrue) {
            ToastUtils.showToast(context, R.string.fragment_data_no_data);
        } else {
            Intent intent1 = new Intent(context, ActivityScaleMain.class);
            intent1.putExtra("mScale_fourElectrode_dataModel", mScale_fourElectrode_dataModel);
            startActivity(intent1);
        }
    }

    @Override
    public void onAddScaleItemClick() {
        Intent mIntent;
        mIntent = new Intent(getActivity(), ActivityBindScale.class);
        //mIntent = new Intent(getActivity(), ActivityScaleScan.class);
        //到体脂秤扫描页面了,不要做手表的重连了
        mIntent.putExtra("isConnect", false);
        startActivity(mIntent);
    }

    /**
     * 称重或者连接体脂秤的按钮
     */
    @Override
    public void onScaleStateListenter() {

        //连接状态，并且连的是体脂秤就直接跳转，否则就进行断开，再扫描体脂称连接
        if (AppConfiguration.isConnected && ISportAgent.getInstance().getCurrnetDevice() != null && ISportAgent.getInstance().getCurrnetDevice().deviceType == JkConfiguration.DeviceType.BODYFAT) {
            //是连接状态,点击的不是当前连接设备，那么就去切换连接,点击的是当前，那么进页面检测
            int currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            //去心率检测页面
            ActivitySwitcher.goScaleRealTimeAct(context, false);
        } else {
            //是未连接状态
            ISportAgent.getInstance().disConDevice(false);
            connectScale();
        }
    }

    @Override
    public void onScaleViewSuccess() {
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
            mFragPresenter.getMainScaleDataFromDB(false, false);
        }
    }


    /**
     * 心率监测和连接手环的入口
     */
    @Override
    public void onHeartRateStateListenter(boolean isOpen) {
        //判断点击的是什么，是否连接
        if (AppConfiguration.isConnected) {
            //是连接状态,点击的不是当前连接设备，那么就去切换连接,点击的是当前，那么进页面检测
            int currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            //是W516手环

            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, isOpen);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ISportAgent.getInstance().requestBle(BleRequest.real_hr_switch);
                }
            }, 1000);

            /*if (DeviceTypeUtil.isContainW55X(currentDeviceType)) {
             *//*Intent intentHr = new Intent(getActivity(), ActivityWatchHeartRateIng.class);
                intentHr.putExtra(JkConfiguration.DEVICE, mCurrentDevice.getDeviceName());
                startActivity(intentHr);*//*

            } else if (DeviceTypeUtil.isContainWatch()) {
                *//*if (DeviceTypeUtil.isContainWatch(currentDeviceType)) {
                    if (AppConfiguration.hasSynced) {
                        device24HrPresenter.getMessageCallState(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    } else {
                        ToastUtils.showToast(context, UIUtils.getString(R.string.sync_data));
                    }
                } else {
                    disconnectAndConnect(JkConfiguration.DeviceType.WATCH_W516);
                }*//*
            } else if (DeviceTypeUtil.isContainWrishBrand(currentDeviceType)) {
                if (AppConfiguration.hasSynced) {
                    hrSettingPresenter.getHrItem(TokenUtil.getInstance().getPeopleIdInt(getActivity()), AppConfiguration.braceletID);
                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.sync_data));
                }
            } else {
                disconnectAndConnect(JkConfiguration.DeviceType.BRAND_W311);
            }*/
        } else {

        }
    }


    @Override
    public void onHeadOnclick() {
        clickItem(JkConfiguration.BODY_HEADER);
    }


    /*********条目点击跳转********/
    /**
     * 心率历史查看
     */
    @Override
    public void onHeartRateItemClick(int viewType) {
        clickItem(viewType);

    }


    /**
     * 跳转到睡眠历史数据页面
     */
    @Override
    public void onSleepItemClick() {

        clickItem(JkConfiguration.BODY_SLEEP);

    }


    //1:运动 2：心率 3：睡眠
    public void clickItem(int type) {
        if (ViewMultiClickUtil.onMultiClick()) {
            return;
        }
        int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);

        currentType = DeviceTypeUtil.getCurrentBindDeviceType();


        if (AppConfiguration.deviceBeanList == null && AppConfiguration.deviceBeanList.size() == 0) {
            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.fragment_data_no_data));
        } else if (AppConfiguration.deviceBeanList.containsKey(currentType)) {
            switch (type) {
                //体温
                case JkConfiguration.BODY_TEMP: {
                    startActivity(new Intent(getActivity(), TempResultActivity.class));
                }
                break;
                //单次心率测量
                case JkConfiguration.BODY_ONCE_HR: {
                    startActivity(new Intent(getActivity(), OnceHrDataResultActivity.class));
                }
                break;
                //血压
                case JkConfiguration.BODY_BLOODPRESSURE: {
                    startActivity(new Intent(getActivity(), BPResultActivity.class));
                }
                break;
                //血氧
                case JkConfiguration.BODY_OXYGEN: {
                    startActivity(new Intent(getActivity(), OxyResultActivity.class));
                }
                break;
                //锻炼
                case JkConfiguration.BODY_EXCERICE: {

                    if (currentType == JkConfiguration.DeviceType.Brand_W520) {
                        startActivity(new Intent(getActivity(), PractiseW520RecordActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), PractiseRecordActivity.class));
                    }

                }
                break;
                //运动进度条
                case JkConfiguration.BODY_HEADER: {
                    Intent intent1 = new Intent(context, ReportActivity.class);
                    intent1.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceBeanList.get(currentType));
                    startActivity(intent1);

                }
                break;
                //睡眠
                case JkConfiguration.BODY_SLEEP: {
                    Intent intent = new Intent(context, ActivityWatchSleep.class);
                    intent.putExtra(JkConfiguration.CURRENTDEVICETPE, currentType);
                    intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceBeanList.get(currentType));
                    startActivity(intent);
                }
                break;
                //心率
                case JkConfiguration.BODY_HEARTRATE: {
                    Intent intent = new Intent(context, ActivityWatchHeartRate.class);
                    intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceBeanList.get(currentType));
                    startActivity(intent);
                }
                break;
            }
        } else {
            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.fragment_data_no_data));
        }

    }

    /*********条目点击跳转********/


    /**
     * 睡眠监测和连接睡眠带入口
     */
    @Override
    public void onSleepStateListenter() {
        if (!AppConfiguration.isConnected) {
            //   onHeartRateStateListenter();
        }

    }


    /**
     * 从DB中获取到设备列表
     *
     * @param
     * @param
     */


    private void reFleshItemFormDB() {
        // mFragPresenter.getMainSleepDataFromDB(false);

        mFragPresenter.getMainDataFromDB(false);
        /*mFragPresenter.getMainW516DataFromDB(false);
        mFragPresenter.getMainScaleDataFromDB(false, false);
        //mFragPresenter.getMainW516StandDataFromDB(false);
        // mFragPresenter.getMainW311StandDataFromDB(false);
        mFragPresenter.getMainW311DataFromDB(false);*/

    }


    public void getDeviceSuccess(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean setWatchDefault, boolean isNet,String tag) {
        AppConfiguration.deviceBeanList = deviceBeanHashMap;

        Logger.myLog(TAG,"getDeviceSuccess:------deviceBeanHashMap---="+isNet+deviceBeanHashMap.toString()
                +" 是否连接="+AppConfiguration.isConnected+"\n"+"来源="+tag);

        //如果不是连接状态才需要这样初始化
        if (AppConfiguration.isConnected && AppConfiguration.currentConnectDevice != null && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            isShowOptin();
        } else {
            if (!AppConfiguration.isConnected) {
                isShowOptin();
            } else {
                isUnConShowOptin();
            }
        }

        if (AppConfiguration.deviceBeanList.size() >= 2) {
            if (dataHeaderHolder != null) {
                dataHeaderHolder.showChangeImage(true);
            }

            //mainHeadLayout.showChangeImage(true);
        } else {
            if (dataHeaderHolder != null) {
                dataHeaderHolder.showChangeImage(false);
            }
            // mainHeadLayout.showChangeImage(false);
        }
        for (int deviceTypeI : AppConfiguration.deviceBeanList.keySet()) {
            Logger.myLog(TAG,"-----deviceTypeI="+deviceTypeI);
            DeviceBean deviceBean = AppConfiguration.deviceBeanList.get(deviceTypeI);
            Logger.myLog(TAG,"从DB 获取绑定列表成功" + deviceBean.toString());
            //上传未上传的数据，上传成功再拉取服务器上的数据
            int deviceType = deviceBean.currentType;
            switch (deviceBean.currentType) {
                case JkConfiguration.DeviceType.BODYFAT:
                    //需要一点点的延时
                    break;
                case JkConfiguration.DeviceType.Brand_W811:
                case JkConfiguration.DeviceType.Brand_W814:
                case JkConfiguration.DeviceType.Watch_W812:
                case JkConfiguration.DeviceType.Watch_W817:
                case JkConfiguration.DeviceType.Watch_W813:
                case JkConfiguration.DeviceType.Watch_W819:
                case JkConfiguration.DeviceType.Watch_W910:
                    AppConfiguration.braceletID = deviceBean.deviceID;
                    thridMessagePresenter.getAllThridMessageItem(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceBean.deviceID);
                    break;
                case JkConfiguration.DeviceType.BRAND_W311:
                case JkConfiguration.DeviceType.BRAND_W307J:
                case JkConfiguration.DeviceType.Brand_W520:
                    //需要去查询哪些消息是已经开始了
                    thridMessagePresenter.getAllThridMessageItem(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceBean.deviceID);
                    AppConfiguration.braceletID = deviceBean.deviceID;
                    // bindPresenter.updateMainBraceletW311HistoryData(deviceBean, false);
                    break;
                case JkConfiguration.DeviceType.WATCH_W516:
                case JkConfiguration.DeviceType.Watch_W556:
                case JkConfiguration.DeviceType.Watch_W557:
                case JkConfiguration.DeviceType.Watch_W812B:
                case JkConfiguration.DeviceType.Watch_W560:
                    AppConfiguration.braceletID = deviceBean.deviceID;
                    //sysncDevcieDataToServer(deviceType);
                    // bindPresenter.updateWatchHistoryData(deviceBean, false);
                    break;
                default:
                    AppConfiguration.braceletID = deviceBean.deviceID;
                    break;
            }

        }

        String deviceName = "0";
        //设置各设备的同步时间
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
            //如果绑定了体脂称就可以点击称重
            //updateDataScale(true);
            //获取本地的数据
            //mFragPresenter.getMainScaleDataFromDB(false);
            App.setScaleBindTime(AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.BODYFAT).timeTamp);
        } else {
            //如果没有绑定了体脂称就不可以点击称重
            AppConfiguration.scacleBeansList.clear();
            /*if (dataScaleHolder != null) {
                scacleBeansList.clear();
                refreshScale();
            }*/
            //updateDataScale(false);
        }
        Logger.myLog(TAG,"-----11111--setDeviceBraceletID()-----");

        setDeviceBraceletID();
//        if (!DeviceTypeUtil.isContainWatchOrBracelet()) {
//            noDevcieShowDef();
//        }

        Logger.myLog(TAG,"-----2222--setDeviceBraceletID()-----");
        //获取设备的目标步数
        deviceGoalStepPresenter.getDeviceGoalStep(TokenUtil.getInstance().getPeopleIdInt(context), deviceName);

        //设置当前设备
        int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
        if (AppConfiguration.deviceBeanList.size() > 0) {
            boolean hasCurrentType = false;
            for (int deviceTypeI : AppConfiguration.deviceBeanList.keySet()) {
                DeviceBean deviceBean = AppConfiguration.deviceBeanList.get(deviceTypeI);
                Logger.myLog(TAG,"-----deviceBean="+deviceBean.toString());
                if (deviceBean.deviceType == currentType) {
                    //说明有手表,设置当前设备为手表
                    hasCurrentType = true;
                    AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, deviceBean.deviceType);
                    Logger.myLog(TAG,"有对应type的设备 currentDeviceType 00== " + currentType);
                    AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBean.deviceID);
                    if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                        AppSP.putString(context, AppSP.SCALE_MAC, deviceBean.mac);
                    }
                    boolean isTempType = DeviceTypeUtil.isContainW81(deviceBean.deviceType);
                    Logger.myLog(TAG,"----isTempType="+isTempType);
                    if (isTempType) {
                        AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW81Mac(deviceBean.deviceName));
                    }
                    break;
                }
            }
            if (!hasCurrentType && (AppConfiguration.deviceBeanList != null)) {

                //没有对应type的设备，那么给最后一个设备
                DeviceBean deviceBean = DeviceDataUtil.getLastBindDevice();
                Logger.myLog(TAG,"没有对应type的设备 currentDeviceType 00== " + currentType + "deviceBean" + deviceBean);
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, deviceBean.deviceType);
                AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBean.deviceID);
                if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    AppSP.putString(context, AppSP.SCALE_MAC, deviceBean.mac);
                }
            }

            //体脂秤绑定成功后，刷新列表时，不要修改默认设备，此时可能可能连上的就是体脂秤
            if (setWatchDefault) {
                boolean canReConnect = AppSP.getBoolean(context, AppSP.CAN_RECONNECT, false);
                //如果连接手表成功过,且设备列表有手表，那么直接设置当前设备为手表,重连的需求
                if (canReConnect && AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
                    DeviceBean deviceBean = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.WATCH_W516);
                    AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, deviceBean.deviceType);
                    AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBean.deviceID);
                    if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                        AppSP.putString(context, AppSP.SCALE_MAC, deviceBean.mac);
                    }
                }
            }

            Log.e(TAG,"Constants.W55XHrState=" + Constants.W55XHrState);
            if (dataRealHeartRateHolder != null) {
                dataRealHeartRateHolder.setChexState(Constants.W55XHrState);
                dataRealHeartRateHolder.showcheckbox_hr_state();
            }

            int currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            if (DeviceTypeUtil.isContainWrishBrand(currentDeviceType)) {
                hrSettingPresenter.getHrItem(TokenUtil.getInstance().getPeopleIdInt(getActivity()), AppConfiguration.braceletID);
            }
            //  Log.e("getConect2", "getConect2=" + Constants.W55XHrState);


        } else {
            //没有列表的情况,那么将初值设置给WATCH
            //需要加新的设备类型
            noDevcieShowDef();
        }

        //刷新主页列表展示和我的页面列表
        refreshDeviceList();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.GET_BIND_DEVICELIST_SUCCESS));

//        //从DB获取主页数据
//        getMainDataFromDB(show);
        //获取网络数据
        if (isNet) {
            Logger.myLog(TAG,"isNet:" + isNet + " mFragPresenter.getDeviceList");
            refreshItemFormHttp(show);
        } else {

            Logger.myLog(TAG,"isNet:" + isNet + " mFragPresenter.getDeviceList");
            reFleshItemFormDB();
            if (NetUtils.hasNetwork(BaseApp.getApp())) {
                mFragPresenter.getDeviceList(false, false, setWatchDefault);
            }
        }

        HashMap<Integer, DeviceBean> mm = AppConfiguration.deviceMainBeanList;
        for(Map.Entry<Integer,DeviceBean> m : mm.entrySet()){
            Logger.myLog(TAG,"-----map中的key和value="+m.getKey()+" "+m.getValue());
        }



        // W307J 心率模块需要动态添加
        if (DeviceTypeUtil.isContainW557()) {
            if (!lists.contains(JkConfiguration.BODY_TEMP)) {
                lists.add(lists.size(), JkConfiguration.BODY_TEMP);
            }
        } else {
            if (lists.contains(JkConfiguration.BODY_TEMP)) {
                int index = lists.indexOf(JkConfiguration.BODY_TEMP);
                lists.remove(index);
            }

        }

        if (DeviceTypeUtil.isContainW81() || DeviceTypeUtil.isContainW556OrW812B()) {   //单次心率
            if (!lists.contains(JkConfiguration.BODY_ONCE_HR)) {
                lists.add(lists.size(), JkConfiguration.BODY_ONCE_HR);
            }

            if(!AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)){
                if (!lists.contains(JkConfiguration.BODY_BLOODPRESSURE)) {  //血压
                    lists.add(lists.size(), JkConfiguration.BODY_BLOODPRESSURE);
                }
            }

            if (!lists.contains(JkConfiguration.BODY_OXYGEN)) { //血氧
                lists.add(lists.size(), JkConfiguration.BODY_OXYGEN);
            }


        } else if (DeviceTypeUtil.isContainW557()) {
            if (!lists.contains(JkConfiguration.BODY_ONCE_HR)) {
                lists.add(lists.size(), JkConfiguration.BODY_ONCE_HR);
            }
            if (lists.contains(JkConfiguration.BODY_BLOODPRESSURE)) {
                int index = lists.indexOf(JkConfiguration.BODY_BLOODPRESSURE);
                lists.remove(index);
            }
            if (lists.contains(JkConfiguration.BODY_OXYGEN)) {
                int index = lists.indexOf(JkConfiguration.BODY_OXYGEN);
                lists.remove(index);
            }
        } else {
            if (lists.contains(JkConfiguration.BODY_BLOODPRESSURE)) {
                int index = lists.indexOf(JkConfiguration.BODY_BLOODPRESSURE);
                lists.remove(index);
            }
            if (lists.contains(JkConfiguration.BODY_ONCE_HR)) {
                int index = lists.indexOf(JkConfiguration.BODY_ONCE_HR);
                lists.remove(index);
            }
            if (lists.contains(JkConfiguration.BODY_OXYGEN)) {
                int index = lists.indexOf(JkConfiguration.BODY_OXYGEN);
                lists.remove(index);
            }

        }


        if (DeviceTypeUtil.isContainW81() || DeviceTypeUtil.isContainW55XX()) {
            if (!lists.contains(JkConfiguration.BODY_EXCERICE)) {
                lists.add(lists.size(), JkConfiguration.BODY_EXCERICE);
                // lists.add(3, JkConfiguration.BODY_EXCERICE);
            }

        } else {
            if (lists.contains(JkConfiguration.BODY_EXCERICE)) {
                int index = lists.indexOf(JkConfiguration.BODY_EXCERICE);
                lists.remove(index);
            }
        }

        headConnDevice(false);
        if (AppConfiguration.deviceBeanList.size() == 1 && AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
            {
                if (AppConfiguration.isConnected) {
                    setHeadDevcieType(JkConfiguration.DeviceType.BODYFAT, false);
                } else {
                    setHeadDevcieType(JkConfiguration.DeviceType.BODYFAT, true);
                }
            }
        }

    }

    private int setDeviceBraceletID() {

        Logger.myLog(TAG,"---000--AppConfiguration.deviceBeanList="+new Gson().toJson(AppConfiguration.deviceBeanList));
        int deviceType = JkConfiguration.DeviceType.WATCH_W516;

        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.SLEEP)) {
            App.setSleepBindTime(AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.SLEEP).timeTamp);
        }
        if (AppConfiguration.deviceBeanList != null && AppConfiguration.deviceBeanList.size() > 0) {
            deviceType = JkConfiguration.DeviceType.WATCH_W516;
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.WATCH_W516).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.WATCH_W516).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.BRAND_W311).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.BRAND_W311).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Brand_W814).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Brand_W814).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W813).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W813).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W819).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W819).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W910).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W910).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W812).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W812).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W817).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W817).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.BRAND_W307J).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.BRAND_W307J).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Brand_W520).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Brand_W520).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W557).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W557).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W812B).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W812B).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W560B).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W560B).deviceType;
            } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)) {
                AppConfiguration.braceletID = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W560).deviceName;
                deviceType = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.Watch_W560).deviceType;
            }
        }

        Logger.myLog(TAG,"-----dddType="+deviceType);
        return deviceType;
    }

    @Override
    public void successGetDeviceListFormDB(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean setWatchDefault) {

        getDeviceSuccess(deviceBeanHashMap, show, setWatchDefault, false,"DB");
    }

    /**
     * 获取网络列表成功
     */
    @Override
    public void successGetDeviceListFormHttp(HashMap<Integer, DeviceBean> deviceBeanHashMap, boolean show, boolean setWatchDefault) {

        getDeviceSuccess(deviceBeanHashMap, show, setWatchDefault, true,"NET");
    }

    public static ArrayList<ScacleBean> scacleBeansList = new ArrayList<>();

    public void noDevcieShowDef() {

        //  Logger.myLog(" getDeviceStepLastTwoData noDevcieShowDef");
        mCurrentDevice = null;
        AppConfiguration.braceletID = "";
        if (AppConfiguration.deviceBeanList == null) {
            AppConfiguration.deviceBeanList = new HashMap<>();
        }
        AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
        AppSP.putString(context, AppSP.SCALE_MAC, "");
        AppSP.putString(context, AppSP.SLEEP_MAC, "");
        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, "");
        mHeartRateMainData = null;
        mSleepMainData = null;
        mWatchSportMainData = null;
        AppConfiguration.scacleBeansList.clear();

        // Logger.myLog("noDevcieShowDef:" + AppConfiguration.scacleBeansList + "dataHeaderHolder:" + dataHeaderHolder);

        if (dataHeaderHolder != null) {
            JkConfiguration.WATCH_GOAL = 6000;
            dataHeaderHolder.defUpdateUI();
        }
        if (dataHeartRateHolder != null) {
            dataHeartRateHolder.setDefValues();
        }
        if (dataRealHeartRateHolder != null) {
            dataRealHeartRateHolder.defUpdateUI();
        }
        if (dataSleepHolder != null) {
            dataSleepHolder.setDefValues();
        }
        if (dataBloodPresureDataHolder != null) {
            dataBloodPresureDataHolder.setDefValues();
        }
        if (dataExerciseDataHolder != null) {
            dataExerciseDataHolder.updateUI(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()), "0", UIUtils.getString(R.string.unit_minute));
        }
        if (dataOxygenDataHolder != null) {
            dataOxygenDataHolder.setDefValues();
        }
        if (dataOnceHrHolder != null) {
            dataOnceHrHolder.setDefValues();
        }

    }

    @Override
    public void successGetMainScaleDataFromDB(ArrayList<ScacleBean> scacleBeans, Scale_FourElectrode_DataModel scale_fourElectrode_dataModel, boolean show) {
        scacleBeansList.clear();
        scacleBeansList.addAll(scacleBeans);
        AppConfiguration.scacleBeansList.clear();
        AppConfiguration.scacleBeansList.addAll(scacleBeans);
        mScale_fourElectrode_dataModel = scale_fourElectrode_dataModel;
        if (mScale_fourElectrode_dataModel != null) {
            // Logger.myLog("mScale_fourElectrode_dataModel == " + mScale_fourElectrode_dataModel.toString());
        }
        //刷新Scale item
        refreshScale();
    }

    private void refreshItemFormHttp(boolean show) {
        if (App.appType() == App.httpType && NetUtils.hasNetwork(context)) {
            //已经绑定的数据类型拉去数据
            if (DeviceTypeUtil.isContainWatch()) {
                Logger.myLog("refreshItemFormHttp w526");
                mFragPresenter.getMainW516DataFromHttp(show);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                mFragPresenter.getMainScaleDataFromHttp(show);
            }
            if (DeviceTypeUtil.isContainBrand()) {
                Logger.myLog("mFragPresenter.getMainW311DataFromHttp");
                mFragPresenter.getMainW311DataFromHttp(show);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.SLEEP)) {
                mFragPresenter.getMainSleepaceDataDataFromHttp(show);
            }
            if (DeviceTypeUtil.isContainW81()) {
                mFragPresenter.getMainW81DataFromHttp(false);
            }
        }
    }


    /**
     * 刷新各设备的连接状态
     */
    private void refreshDeviceConnected() {
        //心率
        refreshHeartRate();

        //睡眠
        refreshSleepace();

    }


    @Override
    public void successGetMainLastStepDataForDB(WatchSportMainData watchSportMainData) {
        mWatchSportMainData = watchSportMainData;
        if (mWatchSportMainData != null && dataHeaderHolder != null) {
            Logger.myLog("successWatchStepHistoryDataFormHttp == " + mWatchSportMainData.toString());
            dataHeaderHolder.updateUI(mWatchSportMainData);
        }
    }


    @Override
    public void successGetMainLastHrDataForDb(HeartRateMainData heartRateMainData) {
        mHeartRateMainData = heartRateMainData;
        if (mHeartRateMainData != null) {
            Logger.myLog(TAG,"mHeartRateMainData == " + mHeartRateMainData.toString());
        }
        refreshHeartRate();
    }

    @Override
    public void successGetMainLastOxgenData(OxyInfo info) {

        Logger.myLog(TAG,"successGetMainLastOxgenData:" + info + "dataOxygenDataHolder" + dataOxygenDataHolder);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataOxygenDataHolder != null) {
                    String strDate = info.getStrDate();
                    String value = String.valueOf(info.getBoValue()) + "%";
                    String unit = "95%～98%";
                    dataOxygenDataHolder.updateUI(strDate, value, unit);
                }
            }
        }, mDelayMillis1);
    }

    @Override
    public void successGetMainLastOnceHrData(OnceHrInfo info) {

        Logger.myLog(TAG,"OnceHrInfo:" + info.getHeartValue());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataOnceHrHolder != null && info != null) {
                    String strDate = info.getStrDate();
                    String value = String.valueOf(info.getHeartValue());
                    String unit = UIUtils.getString(R.string.BPM);
                    dataOnceHrHolder.updateUI(strDate, value, unit);
                }
            }
        }, mDelayMillis1);
    }

    @Override
    public void successGetMainLastBloodPresuure(BPInfo info) {
        Logger.myLog("successGetMainLastBloodPresuure:" + info + "dataBloodPresureDataHolder:" + dataBloodPresureDataHolder);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataBloodPresureDataHolder != null) {
                    String strDate = info.getStrDate();
                    String value = "";
                    if (info.getSpValue() == 0 || info.getDpValue() == 0) {
                    } else {
                        value = String.valueOf(info.getSpValue() + "/" + info.getDpValue());
                    }
                    String unit = UIUtils.getString(R.string.mmhg);
                    dataBloodPresureDataHolder.updateUI(strDate, value, unit);
                }
            }
        }, mDelayMillis1);
    }

    @Override
    public void successGetMainLastTempValue(TempInfo info) {

        if (dataTempHolder == null) {
            return;
        }

        String unitl = UIUtils.getString(R.string.temperature_degree_centigrade);
        String value = info.getCentigrade();
        if (info.getTempUnitl().equals("1")) {
            unitl = UIUtils.getString(R.string.temperature_fahrenheit);
            value = info.getFahrenheit();
        }
        dataTempHolder.updateUI(info.getStrDate(), value, unitl);
    }

    @Override
    public void successGetMainTotalAllTime(Integer time) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataExerciseDataHolder != null) {
                    String strDate = TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis());
                    String value = time + "";
                    String unit = UIUtils.getString(R.string.minute);
                    dataExerciseDataHolder.updateUI(strDate, value, unit);
                }
            }
        }, mDelayMillis1);

    }


    /**
     * 获取 W516服务器 14天的数据
     *
     * @param show
     */
    @Override
    public void successWatchHistoryDataFormHttp(boolean show, int type) {

        Logger.myLog(TAG,"--------successWatchHistoryDataFormHttp="+show+" type="+type);

        switch (type) {
            case JkConfiguration.WatchDataType.STEP:
                if (DeviceTypeUtil.isContainWatch()) {
                    mFragPresenter.getWatchStepLastTwoData(AppConfiguration.isConnected);
                } else if (DeviceTypeUtil.isContainBrand()) {
                    //TODO 需要后去w311的周数据
                    mFragPresenter.getW311StepLastTwoData();
                } else if (DeviceTypeUtil.isContainW81()) {
                    mFragPresenter.getW81StepLastTwoData();
                }
                break;
            case JkConfiguration.WatchDataType.HEARTRATE:
                if (DeviceTypeUtil.isContainWatch()) {
                    mFragPresenter.getW516HrLastTwoData();
                } else if (DeviceTypeUtil.isContainBrand()) {
                    //TODO 需要后去w311的周数据
                    mFragPresenter.getW311HrLastTwoData();
                } else if (DeviceTypeUtil.isContainW81()) {
                    mFragPresenter.getDeviceHrLastTwoData(JkConfiguration.DeviceType.Watch_W812);
                }
                break;
            case JkConfiguration.WatchDataType.SLEEP:
                if (DeviceTypeUtil.isContainWatch()) {
                    mFragPresenter.getDeviceSleepDataFromDB(false, JkConfiguration.DeviceType.WATCH_W516);
                } else if (DeviceTypeUtil.isContainBrand()) {
                    //TODO 需要后去w311的周数据
                    mFragPresenter.getDeviceSleepDataFromDB(false, JkConfiguration.DeviceType.BRAND_W311);

                } else if (DeviceTypeUtil.isContainW81()) {
                    mFragPresenter.getDeviceSleepDataFromDB(false, JkConfiguration.DeviceType.Watch_W812);

                }
                break;
        }


    }


    /**
     * 从DB获取睡眠数据
     *
     * @param sleep_Sleepace_DataModel
     * @param sleepMainData
     */
    @Override
    public void successGetMainSleepaceDataFromDB(Sleep_Sleepace_DataModel sleep_Sleepace_DataModel, SleepMainData sleepMainData, boolean show) {
       Logger.myLog(TAG,"--------从DB获取睡眠="+new Gson().toJson(sleep_Sleepace_DataModel)+"\n"+new Gson().toJson(sleepMainData));

        mSleepMainData = sleepMainData;
        mSleep_Sleepace_DataModel = sleep_Sleepace_DataModel;
        if (mSleepMainData != null) {
            braceletMainData = new SleepMainData();
            braceletMainData.setCompareSleepTime(mSleepMainData.getCompareSleepTime());
            braceletMainData.setLastSyncTime(mSleepMainData.getLastSyncTime());
            braceletMainData.setMinute(mSleepMainData.getMinute());
        }
        if (mSleep_Sleepace_DataModel != null) {
        }
        refreshSleepace();
    }

    @Override
    public void successGetMainBraceletSleepFromDB(SleepMainData sleepMainData, boolean show) {
       // Logger.myLog(TAG,"----------mSleepMainData == successGetMainBraceletSleepFromDB:" + mSleepMainData.toString());
        mSleepMainData = sleepMainData;
        if (mSleepMainData != null) {
            Logger.myLog(TAG,"mSleepMainData == successGetMainBraceletSleepFromDB:" + mSleepMainData.toString());
            braceletMainData = new SleepMainData();
            braceletMainData.setCompareSleepTime(mSleepMainData.getCompareSleepTime());
            braceletMainData.setLastSyncTime(mSleepMainData.getLastSyncTime());
            braceletMainData.setMinute(mSleepMainData.getMinute());
            braceletMainData.setLastSyncDate(sleepMainData.getLastSyncDate());
            Logger.myLog("mSleepMainData == successGetMainSleepaceDataFromDB" + mSleepMainData.toString() + "braceletMainData:" + braceletMainData.toString());
            Logger.myLog("mSleepMainData == successGetMainSleepaceDataFromDB braceletMainData:" + braceletMainData.toString());
        }
        if (mSleep_Sleepace_DataModel != null) {
            Logger.myLog("mSleep_Sleepace_DataModel == " + mSleep_Sleepace_DataModel.toString());
        }
        refreshSleepace();
    }

    /**
     * 从Http获取睡眠带数据
     *
     * @param sleepMainData
     * @param sleep_Sleepace_DataModel
     */
    @Override
    public void successSleepHistoryDataFormHttp(SleepMainData sleepMainData, Sleep_Sleepace_DataModel
            sleep_Sleepace_DataModel) {
        Logger.myLog(TAG,"------== successSleepHistoryDataFormHttp:" + mSleepMainData.toString());

        mSleepMainData = sleepMainData;
        mSleep_Sleepace_DataModel = sleep_Sleepace_DataModel;

        if (mSleepMainData != null) {
            Logger.myLog("mSleepMainData == successSleepHistoryDataFormHttp:" + mSleepMainData.toString());
        }
        refreshSleepace();
    }

    /**
     * 刷新睡眠item
     */
    private void refreshSleepace() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dataSleepHolder != null) {
                     Logger.myLog("mSleepMainData == refleshSleepace:" + mSleepMainData + "braceletMainData:" + braceletMainData);
                    if (braceletMainData != null && DeviceTypeUtil.isContainWatchOrBracelet()) {
                        dataSleepHolder.updateUI(braceletMainData.getLastSyncDate(), braceletMainData.getMinute());
                    } else {
                        dataSleepHolder.updateUI("", 0);

                    }
                }
            }
        }, mDelayMillis1);
    }

    /**
     * 从网络获取运动数据返回
     *
     * @param resultHomeSportData
     */
    @Override
    public void successSportMainData(String resultHomeSportData) {
        // mHomeSportData = resultHomeSportData;
        refreshSport();
    }


    //***************************************************已暂与逻辑关联小***********************************************//

    /**
     * 搜索完成
     */
    @Override
    public void onScanFinish() {
        if (!mHasSleepDevice) {
            Logger.myLog("onScanFinish no device");
            deviceConFail();
        }
    }


    @Override
    public void onScan(String key, BaseDevice rebaseDevice) {


    }


    /**
     * 搜索的回调
     *
     * @param listDevicesMap
     */


    @Override
    public void onScan(Map<String, BaseDevice> listDevicesMap) {
        //Logger.myLog("baseViewList size == " + listDevicesMap.size());
        String currentName = AppSP.getString(context, AppSP.DEVICE_CURRENTNAME, "");
        // currentName = "W812" + "-" + "F1B2A7236BE4";
        if (listDevicesMap != null && listDevicesMap.size() > 0) {
            int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            Logger.myLog(TAG,"onScan " + currentType + "currentName:" + currentName + "currentType" + currentType);
            switch (currentType) {
                case JkConfiguration.DeviceType.SLEEP:
                    if (listDevicesMap.containsKey(currentName) && !mHasSleepDevice) {
                        BaseDevice baseDevice = listDevicesMap.get(currentName);
                        mHasSleepDevice = true;
                        mFragPresenter.cancelScan();
                        AppSP.putString(context, AppSP.SLEEP_MAC, baseDevice.getAddress());
                        mFragPresenter.connectDevice(new SleepDevice(currentName, baseDevice.getAddress()), false, true);
                        setConnectTimeOut();
                    }
                    break;
                case JkConfiguration.DeviceType.BODYFAT:
                   /* Logger.myLog("onScan BODYFAT currentName == " + currentName);
                    if (listDevicesMap.containsKey(Utils.resetDeviceMac(currentName).toUpperCase()) && !mHasSleepDevice) {
                        BaseDevice baseDevice = listDevicesMap.get(Utils.resetDeviceMac(currentName).toUpperCase());
                        mHasSleepDevice = true;
                        mFragPresenter.cancelScan();
                        AppSP.putString(context, AppSP.SCALE_MAC, baseDevice.getAddress());
                        mFragPresenter.connectDevice(new ScaleDevice(baseDevice.deviceName, baseDevice.getAddress()), false, false);
                        setConnectTimeOut();
                    }*/
                    break;
                //扫描后连接设备
                case JkConfiguration.DeviceType.WATCH_W516:
                case JkConfiguration.DeviceType.BRAND_W311:
                case JkConfiguration.DeviceType.Brand_W520:
                case JkConfiguration.DeviceType.Brand_W814:
                case JkConfiguration.DeviceType.Watch_W812:
                case JkConfiguration.DeviceType.Watch_W817:
                case JkConfiguration.DeviceType.Watch_W813:
                case JkConfiguration.DeviceType.Watch_W819:
                case JkConfiguration.DeviceType.Watch_W910:
                case JkConfiguration.DeviceType.Watch_W556:
                case JkConfiguration.DeviceType.Watch_W557:
                case JkConfiguration.DeviceType.Watch_W560:
                default:
                    Logger.myLog(TAG,"-----扫描连接="+listDevicesMap.containsKey(currentName)+"\n"+listDevicesMap.toString());
                    if (listDevicesMap.containsKey(currentName) && !mHasSleepDevice) {
                        BaseDevice baseDevice = listDevicesMap.get(currentName);
                        Logger.myLog(TAG,"----扫描到目标设备="+baseDevice.toString());
                        mHasSleepDevice = true;
                        mFragPresenter.cancelScan();
                        AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.getAddress());
                        // TODO: 2019/4/11 这儿是主动还是自动连接
                        // TODO: 2019/4/11 这儿是主动还是自动连接

                        //  Logger.myLog("onScan" + currentType + "currentName:" + currentName + "currentType" + currentType + "-------" + baseDevice);
                        // connectWatchOrBracelet(mIsConnectByUser, baseDevice.deviceType);

                        mFragPresenter.connectDevice(currentName, baseDevice.getAddress(), baseDevice.deviceType, true, false);
                        setConnectTimeOut();
                    }
                    break;
            }
        }
    }


    /**
     * 默认未点击连接的状态,取消连接超时监听
     */
    private void defaultConnectState(boolean hide) {
        if (hide)
            BleProgressObservable.getInstance().hide();
        if (handler.hasMessages(0x01)) {
            handler.removeMessages(0x01);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        W560HrSwtchObservable.getInstance().deleteObserver(this);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        handler.removeCallbacks(null);
        //
        // ISportAgent.getInstance().clearCurrentDevice();
        if (itemSelectDialog != null) {
            itemSelectDialog.cancelDialog();
        }
       /* if (AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType
                .BODYFAT) == JkConfiguration.DeviceType
                .BODYFAT) {
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Brand_W520);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.BRAND_W307J);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.BRAND_W311);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Brand_W814);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W811)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Brand_W811);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Watch_W813);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Watch_W819);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Watch_W910);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Watch_W812);
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)) {
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.Watch_W817);
            }

        }*/
        handler.removeCallbacks(null);
        // AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBeanScale.deviceID);
        EventBus.getDefault().unregister(this);
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        context.unregisterReceiver(broadcastReceiver);
    }

    /**
     * 断连上一个设备，连接下一个设备,切换设备
     *
     * @param deviceType
     */
    private void disconnectAndConnect(int deviceType) {
        //先断连上一个设备，此时不用重连
        ISportAgent.getInstance().disConDevice(false);
        //然后再去连接
        Message message = new Message();
        message.what = 0x02;
        message.arg1 = deviceType;
        handler.sendMessageDelayed(message, 200);
    }


    public void devcieConnecting() {
        Logger.myLog("BraceletW811W814Manager devcieConnecting");
        mainHeadLayout.showProgressBar(true);
        mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.app_isconnecting));
    }


    Long connectTime = 0L;

    /**
     * 直连手环或者手表
     */
    private synchronized void connectWatchOrBracelet(boolean isConnectByUser, int deviceType) {

        Logger.myLog(TAG,"-------connectWatchOrBracelet="+isConnectByUser+" deviceType="+deviceType);
        //如果从外面进来是连接的就不需要再去连接


        if (AppConfiguration.isConnected) {
            if (AppConfiguration.currentConnectDevice != null && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                ISportAgent.getInstance().disConDevice(false);
                mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.disConnect));
            } else {
                setHeadDevcieType(deviceType, false);
                mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected));
            }

        }


        scanHandler.removeMessages(0x05);
     /*   if (System.currentTimeMillis() - connectTime < 2000) {
            return;
        }*/
        Constants.CAN_RECONNECT = true;
        Logger.myLog(TAG,"kkk == " + isConnectByUser + "deviceType:" + deviceType);
        int currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
        //是连接状态，则不用连接了
        if (AppConfiguration.isConnected && currentDeviceType == deviceType) {
            Logger.myLog("kkk == 已经是连接状态了，不用连接了");
            setHeadDevcieType(deviceType, false);
            return;
        }
        setHeadDevcieType(deviceType, true);

        if (!AppUtil.isOpenBle()) {
            openBlueDialog();
            return;
        }
        DeviceBean deviceBean = null;
        if (AppConfiguration.deviceBeanList.containsKey(deviceType)) {
            deviceBean = AppConfiguration.deviceBeanList.get(deviceType);
        }
         Logger.myLog(TAG,"connectWatchOrBracelet:" + deviceBean.toString());
        if (deviceBean != null) {
            AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, deviceBean.deviceType);
            AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBean.deviceID);
            if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                AppSP.putString(context, AppSP.SCALE_MAC, deviceBean.mac);
            } else {
                if (!TextUtils.isEmpty(deviceBean.mac)) {
                    AppSP.putString(context, AppSP.WATCH_MAC, deviceBean.mac);
                }
                boolean isConnW81 = DeviceTypeUtil.isContainW81(deviceBean.deviceType);
                Logger.myLog(TAG,"----isConnW81="+isConnW81+"\n"+deviceBean.deviceType);
                if (isConnW81) {
                    AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW81Mac(deviceBean.deviceName));
                    Logger.myLog(TAG," DeviceTypeUtil.getW81Mac(deviceBean.deviceName):" + DeviceTypeUtil.getW81Mac(deviceBean.deviceName));
                }
            }
            Logger.myLog(TAG,"----00---取出Mac="+AppSP.getString(context,AppSP.WATCH_MAC,"")+"\n"+AppSP.macMap.toString());

            if (AppSP.macMap.containsKey(deviceBean.deviceName)) {
                AppSP.putString(context, AppSP.WATCH_MAC, "" + AppSP.macMap.get(deviceBean.deviceName));
            }

            Logger.myLog(TAG,"----1-1---取出Mac="+AppSP.getString(context,AppSP.WATCH_MAC,""));
        }
        //当前设备不是手表，则设置为手表
        devcieConnecting();
        setHeadDevcieType(deviceType, false);
        String currentName = AppSP.getString(context, AppSP.DEVICE_CURRENTNAME, "");
        //W81系列的Mac地址就在名字的上面可以取出出来
        if (DeviceTypeUtil.isContainW81(deviceType)) {
            Logger.myLog(TAG,"DeviceTypeUtil.isContainW81(deviceType)= " + deviceType);
            AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW81Mac(currentName));
        }
        Logger.myLog(TAG,"kkk == mac为空，重新扫描 deviceType == " + deviceType);

        //重新升级后需要重新扫描连接 所以还是先扫描再连接计较方便
        if (DeviceTypeUtil.isContainW55X(deviceType) && AppSP.getString(context, AppSP.FORM_DFU, "false").equals("false")) {
            if(deviceType != JkConfiguration.DeviceType.Watch_W560 ){
                String watchMac = DeviceTypeUtil.getW526Mac(currentName, deviceType);
                Logger.myLog(TAG,"------0000--watchMac="+watchMac);
                if (!TextUtils.isEmpty(watchMac)) {
                    AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW526Mac(currentName, deviceType));
                }
            }

        }

        String watchMac = AppSP.getString(context, AppSP.WATCH_MAC, "");

        Logger.myLog(TAG,"------watchMac="+watchMac);
        if (TextUtils.isEmpty(watchMac)) {
            //如果已经在扫描了就不需要再进行扫描了
            if (ISportAgent.getInstance().isDeviceStartScan()) {
                Logger.myLog(TAG,"设备已经开始扫描了不需要再一次进行扫描");
                return;
            }
            Logger.myLog(TAG,"kkk == mac为空，重新扫描 currentName == " + currentName);
            requestPermission(false, isConnectByUser);
        } else {
            // TODO: 2019/1/12 需要和ios平台适配
            if (watchMac.contains(":")) {
                Logger.myLog(TAG,"kkk == mac不为空，直连" + watchMac + " currentName == " + currentName);
                mFragPresenter.connectDevice(currentName, watchMac, deviceType, true, isConnectByUser);
                setConnectTimeOut();

            } else {
                Logger.myLog(TAG,"kkk == mac不为空，但不是真实mac，扫描" + watchMac + " currentName == " + currentName);
                startScan(false, isConnectByUser);
            }
        }

    }

    /**
     * 直连体脂称
     */
    private void connectScale() {
        //需要把list更为Map,key为deviceType

        if (!AppUtil.isOpenBle()) {
            openBlueDialog();
            return;
        }
        //如果当前的设备连接的是体脂秤，就不需要去断开连接，否则都去调用一次断开连接
        if (AppConfiguration.isConnected) {
            if (ISportAgent.getInstance().getCurrnetDevice() != null && ISportAgent.getInstance().getCurrnetDevice().deviceType == JkConfiguration.DeviceType.BODYFAT) {

            } else {
                ISportAgent.getInstance().disConDevice(false);
            }
        } else {
            //可能手表在重连中
            ISportAgent.getInstance().disConDevice(false);
        }

        setHeadDevcieType(JkConfiguration.DeviceType.BODYFAT, true);
        AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType
                .BODYFAT);

        ActivitySwitcher.goScaleRealTimeAct(context, false);

    }

    /**
     * 直连睡眠带
     */
    private void connectSleep() {
        DeviceBean deviceBeanSleep = AppConfiguration.deviceBeanList.get(JkConfiguration.DeviceType.SLEEP);
        //当前类型在绑定类型中
        //需要注意的是，很可能mac为空，所以要用到搜索，比对deviceID来确定设备连接
        if (StringUtil.isBlank(deviceBeanSleep.mac)) {
            AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType
                    .SLEEP);
            AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBeanSleep.deviceID);
            //需要去扫描
            startScan(false, true);
        } else {
            mFragPresenter.connectDevice(new SleepDevice(deviceBeanSleep.deviceID, deviceBeanSleep.mac), true, true);
            setConnectTimeOut();
        }
    }

    /**
     * 添加设备按钮点击
     */
    @Override
    public void onAddOnclick() {
        ActivitySwitcher.goBindAct(context);
    }

    /**
     * 顶部进度点击,进入步数历史页面  lists.add(JkConfiguration.BODY_HEADER);//手表步数展示,默认项
     * lists.add(JkConfiguration.BODY_HEARTRATE);//心率
     * lists.add(JkConfiguration.BODY_SLEEP);//睡眠
     * lists.add(JkConfiguration.BODY_DEVICE1);//运动
     * lists.add(JkConfiguration.BODY_SCALE);//体脂秤
     */


    @Override
    public void onHeadOnclickIvChange() {
        changeConnect();
    }

    /**
     * 周期达标按钮点击,同步手表数据
     */
    public void onSyncOnclick() {
        // TODO: 2019/4/11 正在同步中，要处理
        if (AppConfiguration.isConnected) {
            //正在同步不能让他同步
            if (!AppConfiguration.hasSynced) {
                //ToastUtils.showToast(context, UIUtils.getString(R.string.sync));
                return;
            }
            BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
            if (device != null) {
                int currentType = device.deviceType;
                if (DeviceTypeUtil.isContainW81(currentType) || DeviceTypeUtil.isContainWatch(currentType) || DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    if (DeviceTypeUtil.isContainWatch(currentType)) {
                        String string = BleSPUtils.getString(BaseApp.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());

                        Log.e(TAG,"------刷新天数="+string);

                        ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_DAILY_RECORD, string);
                    } else {
                        ISportAgent.getInstance().requestBle(BleRequest.bracelet_sync_data);
                    }
                }

            } else {
                ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
            }


        } else {
            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
        }
    }

    /**
     * 运动item点击，跳转到历史记录
     */
    @Override
    public void onSportItemClick() {
        if (ViewMultiClickUtil.onMultiClick()) {
            return;
        }
        Intent intent = new Intent(context, SportReportActivity.class);
        startActivity(intent);
    }

    /**
     * 运动按钮点击，跳转到运动模块
     */
    @Override
    public void onSportStateListenter() {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.SHOW_SPORT));
        getActivity().finish();
    }

    ItemSelectDialog itemSelectDialog;

    private void changeConnect() {
        itemSelectDialog = new ItemSelectDialog(getActivity(), new ItemSelectDialog
                .OnTypeClickListenter() {
            @Override
            public void changeDevcieonClick(int type, String mac, String deviceID) {
                int deviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);
                if (deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    ActivitySwitcher.goScaleRealTimeAct(context, false);
                } else {
                    setHeadDevcieType(deviceType, false);
                    devcieConnecting();
                    connectWatchOrBracelet(true, deviceType);

                }


            }
        });
    }

    /**
     * 蓝牙开关的广播监听
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Log.e("BleService", "ACTION_STATE_CHANGED");
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    //蓝牙打开有两种情况，
                    /**
                     * 1.未添加设备
                     * 2.已经添加设备
                     */
                    isShowOptin();
                   /* if (dataScaleHolder != null) {
                        dataScaleHolder.enableOption();
                    }*/
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    //蓝牙关闭需要及时被知晓
                    isShowOptin();
                  /*  if (dataScaleHolder != null) {
                        dataScaleHolder.enableOption();
                    }*/
                }
            }
        }
    };


    /**
     * 初始化View
     *
     * @param view
     */
    @Override
    protected void initView(View view) {
        updateHistoryData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        refrushRecycleView = view.findViewById(R.id.rc_home);
        home_refresh = view.findViewById(R.id.home_refresh);
        mainHeadLayout = view.findViewById(R.id.layout_mainHeadLayout);
        mainHeadLayout.setViewClickLister(this);
    }


    public void updateHeadMainUI() {
        if (mainHeadLayout != null) {

        }
    }

    @Override
    protected void initEvent() {
        // ivChange.setOnClickListener(this);

        Logger.myLog("fragmentNewDate initEvent"+AppConfiguration.deviceBeanList.size());


        ISportAgent.getInstance().registerListener(mBleReciveListener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(broadcastReceiver, filter);


        home_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {


                Logger.myLog(TAG,"------refresh="+AppConfiguration.deviceBeanList.size()+" mCurrentDevice="+mCurrentDevice.toString()+"AppConfiguration.isConnected="+AppConfiguration.isConnected);

                if (AppConfiguration.deviceBeanList == null || AppConfiguration.deviceBeanList.size() == 0 || mCurrentDevice == null) {
                    refreshLayout.finishRefresh();
                    //refreshLayout.closeHeaderOrFooter();
                    return;
                }
                if (mCurrentDevice != null && mCurrentDevice.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                    refreshLayout.finishRefresh();
                    return;
                }
                if (!AppUtil.isOpenBle()) {
                    refreshLayout.finishRefresh();
                    return;
                }
                if (!AppConfiguration.isConnected) {
                    //提示未连接设备
                    //ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    refreshLayout.finishRefresh();
                } else {
                    if (AppConfiguration.hasSynced && !SyncCacheUtils.getStartSync(BaseApp.getApp())) {
                        SyncCacheUtils.saveStartSync(BaseApp.getApp());
                        onSyncOnclick();
                        startSyncDevice();
                        refreshLayout.finishRefresh();
                    } else {
                        //是否同步完成
                        refreshLayout.finishRefresh();
                    }
                }

                /* isReset = true;*/

                // resetData();
            }
        });
    }


    @Override
    protected FragmentDataPresenter createPersenter() {
        device24HrPresenter = new Device24HrPresenter(this);
        weatherPresenter = new WeatherPresenter(this);
        bindPresenter = new BindPresenter(this);
        hrSettingPresenter = new HrSettingPresenter(this);
        thridMessagePresenter = new ThridMessagePresenter(this);
        w311RealTimeDataPresenter = new W311RealTimeDataPresenter(this);
        deviceGoalStepPresenter = new DeviceGoalStepPresenter(this);
        upgradeDataPresenter = new UpgradeDataPresenter(this);
        w81DataPresenter = new W81DataPresenter(this);
        return new FragmentDataPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_data_new;
    }


    //***************************************************上传历史数据***********************************************//


    private void updateScaleHistoryData(boolean show) {
        //网络版本的要做这样的操作
        if (App.appType() == App.httpType && NetUtils.hasNetwork(context)) {
            mFragPresenter.updateScaleHistoryData(show);
        }
    }

    private void updateSleepHistoryData(boolean show) {
        //网络版本的要做这样的操作
        if (App.appType() == App.httpType && NetUtils.hasNetwork(context)) {
            mFragPresenter.updateSleepHistoryData(show);
        }
    }

    private void updateWatchHistoryData(boolean show) {
        //网络版本的要做这样的操作
        if (App.appType() == App.httpType && NetUtils.hasNetwork(context)) {
            mFragPresenter.updateWatchHistoryData(show);
        }
    }

    @Override
    public void updateWatchHistoryDataSuccess(UpdateSuccessBean updateSleepReportBean) {
        mFragPresenter.getMainW516DataFromDB(false);
        mFragPresenter.getMainW516StandDataFromDB(false);
    }

    @Override
    public void updateSleepHistoryDataSuccess(UpdateSuccessBean updateSleepReportBean) {
        mFragPresenter.getMainSleepDataFromDB(false);
    }

    @Override
    public void updateScaleHistoryDataSuccess(UpdateSuccessBean updateSleepReportBean) {
        mFragPresenter.getMainScaleDataFromDB(false, false);
    }


    /*** 上传googlefit***/


    @Override
    public void successDayHrData(WristbandHrHeart wristbandHrHeart, int currentType) {
        //要去分是w311还是w516
        if (wristbandHrHeart == null) {
            return;
        }
        try {
            if (DeviceTypeUtil.isContainWatch(currentType)) {
                GoogleFitUtil.inserHrData(BaseApp.getApp(), wristbandHrHeart.getHrArry(), 1);
            } else if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                GoogleFitUtil.inserHrData(BaseApp.getApp(), wristbandHrHeart.getHrArry(), 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void successDaySleepData(WatchSleepDayData watchSleepDayData, int currentType) {
        if (watchSleepDayData != null) {
            try {
                GoogleFitUtil.inserSleepData(BaseApp.getApp(), watchSleepDayData, currentType);

            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    @Override
    public void successDaySportData(Wristbandstep wristbandstep) {
        if (wristbandstep != null && loginBean != null) {
            try {
                GoogleFitUtil.inserSportData(BaseApp.getApp(), wristbandstep.getStepArry(), Float.valueOf(loginBean.getWeight()), Float.valueOf(loginBean.getHeight()), loginBean.getGender());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*** 上传googlefit***/

    @Override
    public void getW311RealTimeData(Bracelet_W311_RealTimeData bracelet_w311_wearModel) {

    }

    @Override
    public void getW516OrW556(WatchRealTimeData bracelet_w311_wearModel) {

    }

    @Override
    public void successSaveRealTimeData(boolean isSave) {
        //这里只需要去更新步数的数据，睡眠和心率的数据不需要更新
    }

    @Override
    public void onUnBindSuccess() {

    }

    @Override
    public void updateWatchDataSuccess(DeviceBean deviceBean) {

    }

    @Override
    public void updateSleepDataSuccess(DeviceBean deviceBean) {

    }

    @Override
    public void updateWatchHistoryDataSuccess(DeviceBean deviceBean) {

    }

    @Override
    public void updateFail() {

    }


    @Override
    public void successThridMessageItem(Bracelet_W311_ThridMessageModel bracelet_w311_displayModel) {

    }

    @Override
    public void successThridMessageItem() {

    }

    Bracelet_w311_hrModel model;
    boolean isSwitch = false;

    @Override
    public void successHrSettingItem(Bracelet_w311_hrModel bracelet_w311_wearModel) {
        model = bracelet_w311_wearModel;
        boolean isSwitch = false;
        if (model != null) {
            isSwitch = model.getIsOpen();
        } else {
            isSwitch = false;
        }
        if (dataRealHeartRateHolder != null) {
            dataRealHeartRateHolder.setChexState(isSwitch);
        }
        // boolean finalIsSwitch = isSwitch;
       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ISportAgent.getInstance().requestBle(BleRequest.bracelet_heartRate, finalIsSwitch, 30);
            }
        }, 500);*/
       /* if (!isSwitch) {
            if (ViewMultiClickUtil.onMultiClick()) {
                return;
            }
            PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.heartrate_w311_tip), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    //需要去保存开关的状态
                    model.setDeviceId(AppConfiguration.braceletID);
                    model.setIsOpen(true);
                    model.setTimes(30);
                    hrSettingPresenter.saveHrSetting(model);



                    //ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, true);
                    *//*handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ISportAgent.getInstance().requestBle(BleRequest.bracelet_heartRate, true, 30);
                        }
                    }, 500);
                    //需要间隔500毫秒
                    Intent intentHr = new Intent(getActivity(), ActivityWatchHeartRateIng.class);
                    intentHr.putExtra(JkConfiguration.DEVICE, mCurrentDevice.getDeviceName());
                    startActivity(intentHr);*//*
                }

                @Override
                public void cancel() {

                }
            }, false);
        } else {
            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, true);
            Intent intentHr = new Intent(getActivity(), ActivityWatchHeartRateIng.class);
            intentHr.putExtra(JkConfiguration.DEVICE, mCurrentDevice.getDeviceName());
            startActivity(intentHr);
        }
*/
        //
    }

    @Override
    public void successSave(boolean isSave) {

    }

    @Override
    public void success24HrSettingState(Bracelet_W311_24H_hr_SettingModel bracelet_w311_24H_hr_settingModel) {

    }

    GoogleSignInAccount account;

    public void successeDeviceSyncComplety(int current) {
        //如果是英文的环境下
        //google账号已经登陆
        //将数据写入到googleFit

        if (AppUtil.isZh(BaseApp.getApp())) {
            return;
        }
        account = GoogleSignIn.getLastSignedInAccount(BaseApp.getApp());
        if (account == null) {
            return;
        }

        if (current == JkConfiguration.DeviceType.BRAND_W311) {
            //1.取出当天的数据
            //把数据存入到googlefit
            mFragPresenter.getDeviceCurerntDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.BRAND_W311);
            mFragPresenter.getDeviceCurrentDayHrDetailData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), JkConfiguration.DeviceType.BRAND_W311);
            mFragPresenter.getDeviceCurrentDaySleepData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.BRAND_W311);

        } else if (current == JkConfiguration.DeviceType.WATCH_W516) {
            mFragPresenter.getDeviceCurerntDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.WATCH_W516);
            mFragPresenter.getDeviceCurrentDayHrDetailData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), JkConfiguration.DeviceType.WATCH_W516);
            mFragPresenter.getDeviceCurrentDaySleepData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.WATCH_W516);

        } else if (current == JkConfiguration.DeviceType.Brand_W520) {
            mFragPresenter.getDeviceCurerntDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.Brand_W520);
            mFragPresenter.getDeviceCurrentDayHrDetailData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), JkConfiguration.DeviceType.Brand_W520);
            mFragPresenter.getDeviceCurrentDaySleepData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.Brand_W520);

        } else if (current == JkConfiguration.DeviceType.BRAND_W307J) {
            mFragPresenter.getDeviceCurerntDayData(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.BRAND_W307J);
            mFragPresenter.getDeviceCurrentDayHrDetailData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), JkConfiguration.DeviceType.BRAND_W307J);
            mFragPresenter.getDeviceCurrentDaySleepData(DateUtil.dataToString(new Date(), "yyyy-MM-dd"), AppConfiguration.braceletID, JkConfiguration.DeviceType.BRAND_W307J);

        }
    }


    public void reconnectDevice(int deviceType) {

        Logger.myLog(TAG,"------reconnectDevice deviceType="+deviceType);
        //  ISportAgent.getInstance().disConDevice(false);
        AppConfiguration.isScaleConnectting = false;
        switch (deviceType) {
            case JkConfiguration.DeviceType.BODYFAT:
                //不再绑定搜索页面才做重连操作,不再称重页面时
                if (!AppConfiguration.isScaleScan && !AppConfiguration.isScaleRealTime && !AppConfiguration.isScaleConnectting) {
                    // boolean canReConnect = AppSP.getBoolean(context, AppSP.CAN_RECONNECT, false);
                    // Constants.CAN_RECONNECT = canReConnect; //重连的需求
                    readyConnectDevice();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectDevice();
                        }
                    }, 1000);

                }
        }
    }

    public void readyConnectDevice() {

        Logger.myLog(TAG,"---readyConnectDevice AppConfiguration.deviceBeanList="+AppConfiguration.deviceBeanList.toString());

        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
            setHeadDevcieType(JkConfiguration.DeviceType.WATCH_W516, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
            setHeadDevcieType(JkConfiguration.DeviceType.BRAND_W311, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Brand_W520, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)) {
            setHeadDevcieType(JkConfiguration.DeviceType.BRAND_W307J, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W813, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W819, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W910, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W556, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W557, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W812B, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W560B, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W560, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W812, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Watch_W817, true);
        }
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)) {
            setHeadDevcieType(JkConfiguration.DeviceType.Brand_W814, true);
        }
    }

    public void connectDevice() {
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.WATCH_W516);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.BRAND_W311);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Brand_W520);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.BRAND_W307J);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W813);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W819);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W910);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W556);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W557);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W812B);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W560B);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W560);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W812);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W817);
        } else if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)) {
            Constants.CAN_RECONNECT = true;
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Brand_W814);
        }
    }

    /* public void updateDataScale(boolean isEnable) {
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 if (dataScaleHolder != null) {
                     dataScaleHolder.enableOption();
                 }
             }
         }, 500);
     }
 */
    @Override
    public void onChangeClikelister() {
        changeConnect();
    }

    @Override
    public void onViewOptionClikelister(String type) {


        if (type.equals(MainHeadLayout.TAG_ADD)) {
            //跳转到添加设备
            ActivitySwitcher.goBindAct(context);
        } else if (type.equals(MainHeadLayout.TAG_CONNECT)) {
            //连接设备  如果只连接了一个体脂秤，就直接去连接体脂秤  否则连接了手环就直接去重新连接手环
            //headConnDevice(true);
            //当前显示的是那个设备为连接就去连接那个设备
            int currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);

            Logger.myLog("onViewOptionClikelister currentDeviceType:" + currentDeviceType);

            switch (currentDeviceType) {
                case JkConfiguration.DeviceType.BODYFAT:
                    connectScale();
                    break;
                default:
                    if (DeviceTypeUtil.isContainW81(currentDeviceType)) {
                        //  BraceletW811W814Manager.deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                    }
                    connectDevice();
                    break;
            }


        } else if (type.equals(MainHeadLayout.TAG_OPENBLE)) {
            //弹出蓝牙对话框
            openBlueDialog();
        }

    }

    @Override
    public void onMainBack() {
        getActivity().finish();
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
            }
        }, false);
    }


    //头上的按钮是否显示，如果是连接状态就不要显示
    public void isShowOptin() {
        boolean isOpenBlue = AppUtil.isOpenBle();
        Logger.myLog(TAG,"------isShowOptin"+isOpenBlue);
        mainHeadLayout.showProgressBar(false);
        if (AppConfiguration.deviceBeanList.size() == 0) {
            mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_no_device);
            mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.add_device), MainHeadLayout.TAG_ADD, UIUtils.getString(R.string.fragment_main_no_add_device));
        } else {
            mainHeadLayout.setIconDeviceAlp(0.5f);
            if (!isOpenBlue) {
                mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.app_enable), MainHeadLayout.TAG_OPENBLE, UIUtils.getString(R.string.fragment_main_no_connect_open_ble));
            } else {
                mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.fragment_main_click_connect), MainHeadLayout.TAG_CONNECT, UIUtils.getString(R.string.disConnect));
            }
        }
    }

    public void isUnConShowOptin() {

        Logger.myLog(TAG,"-----isUnConShowOptin-----="+AppUtil.isOpenBle()+" "+AppConfiguration.deviceBeanList.size());

        if (AppConfiguration.deviceBeanList.size() == 0) {
            mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_no_device);
            mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.add_device), MainHeadLayout.TAG_ADD, UIUtils.getString(R.string.fragment_main_no_add_device));
        } else {
            if (!AppUtil.isOpenBle()) {
                mainHeadLayout.setIconDeviceAlp(0.5f);
                mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.app_enable), MainHeadLayout.TAG_OPENBLE, UIUtils.getString(R.string.fragment_main_no_connect_open_ble));
            } else {
                mainHeadLayout.setIconDeviceAlp(1f);
            }
        }
    }


    public void headConnDevice(boolean isRecon) {
        //体脂秤比较特殊，先连接再走回调
        mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
        if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT) && mCurrentDevice != null && mCurrentDevice.deviceType == JkConfiguration.DeviceType.BODYFAT && AppConfiguration.isConnected) {
            //体脂称已经连上了
            deviceConSuccess(mCurrentDevice);
            return;
        }

        int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);

        Logger.myLog("headConnDevice:currentType:" + currentType);
        if (DeviceTypeUtil.isContainWrishBrand(currentType) || DeviceTypeUtil.isContainWatch(currentType) || DeviceTypeUtil.isContainW81(currentType) || DeviceTypeUtil.isContainRope(currentType)) {
            connectDevice();
        } else {
            setHeadDevcieType(JkConfiguration.DeviceType.BODYFAT, true);
            //connectScale();
        }
        if (isRecon) {
            if (AppConfiguration.deviceBeanList.size() == 1 && AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                {
                    setHeadDevcieType(JkConfiguration.DeviceType.BODYFAT, true);
                    connectScale();
                }
            }
        }
    }


    public void startSyncDevice() {
        AppConfiguration.hasSynced = false;
        mainHeadLayout.showProgressBar(true);
        mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.sync_data));
    }

    public void endSyncDevice() {
        AppConfiguration.hasSynced = true;
        mainHeadLayout.showProgressBar(false);
        mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected));
    }


    public void sysncDevcieDataToServer(int deviceType) {
        if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
            DeviceBean deviceBean = AppConfiguration.deviceBeanList.get(deviceType);
            if (deviceBean != null && !Constants.isSyncData) {
                bindPresenter.updateBraceletW311HistoryData(deviceBean, false);
            }
        } else if (DeviceTypeUtil.isContainW81(deviceType)) {
            //上传W81的数据
            DeviceBean deviceBean = AppConfiguration.deviceBeanList.get(deviceType);
            if (deviceBean != null && !Constants.isSyncData) {
                w81DataPresenter.getNoUpgradeW81DevcieDetailData(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), deviceBean.deviceID, "0", false);
            }
        } else if (DeviceTypeUtil.isContainWatch(deviceType)) {
            DeviceBean deviceBean = AppConfiguration.deviceBeanList.get(deviceType);
            if (deviceBean != null && !Constants.isSyncData) {
                bindPresenter.updateWatchHistoryData(deviceBean, false);
            }
        }
    }


    public void setHeadDevcieType(int devcieType, boolean isStartConnect) {
        switch (devcieType) {
            case -1:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_no_device);
                break;
            case JkConfiguration.DeviceType.BODYFAT:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_mz);
                break;
            case JkConfiguration.DeviceType.BRAND_W311:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w311);
                break;
            case JkConfiguration.DeviceType.Brand_W520:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w520);
                break;
            case JkConfiguration.DeviceType.BRAND_W307J:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w307j);
                break;
            case JkConfiguration.DeviceType.WATCH_W516:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w516);
                break;
            case JkConfiguration.DeviceType.Brand_W814:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w814);
                break;
            case JkConfiguration.DeviceType.Watch_W812:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w812);
                break;
            case JkConfiguration.DeviceType.Watch_W813:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w813);
                break;
            case JkConfiguration.DeviceType.Watch_W819:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w819);
                break;
            case JkConfiguration.DeviceType.Watch_W910:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w910);
                break;
            case JkConfiguration.DeviceType.Watch_W556:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w526);
                break;
            case JkConfiguration.DeviceType.Watch_W557:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w557);
                break;
            case JkConfiguration.DeviceType.Watch_W817:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w817);
                break;
            case JkConfiguration.DeviceType.Watch_W812B:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w812b);
                break;
                case JkConfiguration.DeviceType.Watch_W560:
                    mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_device_w560);
                    break;
            case JkConfiguration.DeviceType.Watch_W560B:
                mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_w560);
                break;
            default:
                break;
        }
        if (isStartConnect) {
            mainHeadLayout.setIconDeviceAlp(0.5f);
        } else {
            mainHeadLayout.setIconDeviceAlp(1f);
        }
    }


    public void deviceConSuccess(BaseDevice baseDevice) {
        Logger.myLog(TAG,"------deviceConSuccess="+baseDevice.toString());
        AppConfiguration.isConnected = true;
        mainHeadLayout.showProgressBar(false);
        mainHeadLayout.setIconDeviceAlp(1);
        mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected));
        isConnecting = false;
        AppConfiguration.hasSynced = true;
        mCurrentDevice = baseDevice;
        mIsDirctConnect = false;
        //取消超时连接
        defaultConnectState(true);
        //BleProgressObservable.getInstance().hide();
        Logger.myLog(TAG,"连接成功" + baseDevice.deviceName + " -ADDRESS- " + baseDevice.address + " deviceType == " + baseDevice.deviceType);
        AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, baseDevice.deviceType);
        switch (baseDevice.deviceType) {
            case JkConfiguration.DeviceType.Brand_W811:
            case JkConfiguration.DeviceType.Brand_W814:
            case JkConfiguration.DeviceType.Watch_W812:
            case JkConfiguration.DeviceType.Watch_W817:
            case JkConfiguration.DeviceType.Watch_W813:
            case JkConfiguration.DeviceType.Watch_W819:
            case JkConfiguration.DeviceType.Watch_W910:
            case JkConfiguration.DeviceType.BRAND_W311:
            case JkConfiguration.DeviceType.Brand_W520:
            case JkConfiguration.DeviceType.WATCH_W516:
                setCurrrentDeviceSuccess(baseDevice.deviceType, baseDevice.address);
                AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.address);
                AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName);
                AppSP.putBoolean(context, AppSP.CAN_RECONNECT, true);

                break;
            case JkConfiguration.DeviceType.SLEEP: {
                AppSP.putString(context, AppSP.SLEEP_MAC, baseDevice.address);
                AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName);
                ISportAgent.getInstance().requestBle(BleRequest.Common_GetBattery);
            }
            break;
            case JkConfiguration.DeviceType.BODYFAT: {
                setHeadDevcieType(JkConfiguration.DeviceType.BODYFAT, false);
                AppSP.putString(context, AppSP.SCALE_MAC, baseDevice.address);
                AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.address);
                // ISportAgent.getInstance().requestBle(BleRequest.Common_GetBattery);
            }
            break;
            default: {
                setCurrrentDeviceSuccess(baseDevice.deviceType, baseDevice.address);
                AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.address);
                AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName);
                AppSP.putBoolean(context, AppSP.CAN_RECONNECT, true);
            }
            break;
        }
        ISportAgent.getInstance().requestBle(BleRequest.READ_DEVICE_GOAL);
        refreshDeviceConnected();//刷新每个设备的连接状态
        EventBus.getDefault().post(new MessageEvent(MessageEvent.WHEN_CONNECTSTATE_CHANGED));

    }


    public void setCurrrentDeviceSuccess(int currentType, String mac) {
        if (AppConfiguration.deviceBeanList.containsKey(currentType)) {
            setHeadDevcieType(currentType, false);
            AppConfiguration.deviceBeanList.get(currentType).mac = mac;
        }
    }

    public void deviceConFail() {
        isConnecting = false;
        isShowOptin();
        headFail();
        defaultConnectState(true);
        AppConfiguration.isFirstRealTime = true;
        AppConfiguration.hasSynced = false;
        AppConfiguration.isConnected = false;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.WHEN_CONNECTSTATE_CHANGED));
        refreshDeviceConnected();
    }


    public void headFail() {
        mainHeadLayout.setIconDeviceAlp(0.5f);

    }

    @Override
    public void successGetGoalStep(int Step) {
        JkConfiguration.WATCH_GOAL = Step;
        Logger.myLog("JkConfiguration.WATCH_GOAL" + JkConfiguration.WATCH_GOAL);
        BaseManager.setStepTarget((int) JkConfiguration.WATCH_GOAL);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_WATCH_TARGET));
    }

    @Override
    public void successSaveGoalStep(int step) {
        JkConfiguration.WATCH_GOAL = step;
        refreshHeader();

    }

    @Override
    public void success24HrSwitch(boolean isOpen) {

    }

    @Override
    public void successState(StateBean stateBean) {
        this.stateBean = stateBean;
        if (!stateBean.isHrState) {
            if (ViewMultiClickUtil.onMultiClick()) {
                return;
            }
            PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.heartrate_tip), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    Intent intentHr = new Intent(getActivity(), ActivityWatchHeartRateIng.class);
                    intentHr.putExtra(JkConfiguration.DEVICE, mCurrentDevice.getDeviceName());
                    startActivity(intentHr);
                    stateBean.isHrState = true;
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, stateBean.isHrState, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
                }

                @Override
                public void cancel() {

                }
            }, false);
        } else {
            Intent intentHr = new Intent(getActivity(), ActivityWatchHeartRateIng.class);
            intentHr.putExtra(JkConfiguration.DEVICE, mCurrentDevice.getDeviceName());
            startActivity(intentHr);
        }
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.layout_top_view).statusBarDarkFont(true)
                .init();
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof W560HrSwtchObservable) {
            boolean state = (boolean) arg;
            Log.e("update", "update=" + state);
            isCheck = state;
            if (dataRealHeartRateHolder != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataRealHeartRateHolder.setChexState(state);
                        if (!isCheck) {
                            dataRealHeartRateHolder.bleCloseUpdateUi();
                        }
                    }
                });

            }
        } else if (o instanceof W560OpenSwtchObservable) {

            /*Log.e("isCheck", "isCheck=" + isCheck);

            isCheck = !isCheck;
            dataRealHeartRateHolder.setChexState(isCheck);
            if (!isCheck) {
                dataRealHeartRateHolder.bleCloseUpdateUi();
            }*/
            // dataRealHeartRateHolder.hasData(isCheck);
        }
    }
}

