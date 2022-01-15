package com.isport.brandapp.bind.presenter;

import android.os.Handler;
import android.os.Looper;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.utils.BleSPUtils;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.bind.bean.BindInsertOrUpdateBean;
import com.isport.brandapp.bind.bean.DeviceState;
import com.isport.brandapp.bind.model.DeviceOptionImple;
import com.isport.brandapp.bind.model.DeviceResultCallBack;
import com.isport.brandapp.bind.view.ScanBaseView;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.parm.db.DeviceDbParms;
import com.isport.brandapp.repository.MainResposition;
import com.isport.brandapp.util.DeviceTypeUtil;

import java.util.ArrayList;
import java.util.Map;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.entry.bean.BaseParms;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author
 * @Date 2018/10/15
 * @Fuction
 */

public class ScanPresenter extends BasePresenter<ScanBaseView> {

    private static final String TAG = "ScanPresenter";
    
    private DeviceOptionImple deviceOptionImple;
    private ScanBaseView view;

    public ScanPresenter(ScanBaseView view) {
        this.view = view;
        deviceOptionImple = new DeviceOptionImple();
    }

    public void onRespondError(String message) {
        ToastUtils.showToast(context, message);
    }


    public void scan(int type) {

        deviceOptionImple.scan(type, false, new DeviceResultCallBack() {
            @Override
            public void onScanResult(ArrayList<BaseDevice> mBleDevices) {
                if (isViewAttached()) {
                    mActView.get().onScan(mBleDevices);
                }
            }

            @Override
            public void onScanResult(Map<String, BaseDevice> listDevicesMap) {
                if (isViewAttached()) {
                    mActView.get().onScan(listDevicesMap);
                }
            }


            @Override
            public void onScanFinish() {

            }
        });
    }

    public void cancelScan() {
        if (deviceOptionImple != null) {
            deviceOptionImple.cancelScan();
        }
    }

    public void bindDevice(BaseDevice baseDevice) {
        Logger.myLog(TAG,"-------点击按钮绑定="+baseDevice.toString());
        MainResposition<Integer, BindInsertOrUpdateBean, BaseUrl, DeviceDbParms> mainResposition = new
                MainResposition<>();
        mainResposition.requst(HistoryParmUtil.setDevice(baseDevice))
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<Integer>(context, true) {
                    @Override
                    protected void hideDialog() {

                    }

                    @Override
                    protected void showDialog() {

                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable e) {
                        NetProgressObservable.getInstance().hide();
                        Logger.myLog(e.toString());
                        ToastUtils.showToast(context, e.message);
                    }

                    @Override
                    public void onNext(Integer bindBean) {

                        if (bindBean != null) {
                            //1绑定成功
                            //0为绑定失败
                            if (bindBean != 1) {
                            } else {
                                if (DeviceTypeUtil.isContainWatch(baseDevice.deviceType)) {
                                    if (App.isHttp()) {
                                        //如果是网络版,那么绑定时间即为同步开始时间
                                        BleSPUtils.putString(App.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                    } else {
                                        //单机版设置初始时间为7天前
                                        BleSPUtils.putString(App.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getNDayBefore(TimeUtils.getTodayYYYYMMDD(), 7, "yyyy-MM-dd"));
                                    }
                                }
                                if (baseDevice.deviceType == JkConfiguration.DeviceType.BRAND_W311 || baseDevice.deviceType == JkConfiguration.DeviceType.Brand_W520) {
                                    if (App.isHttp()) {
                                        //如果是网络版,那么绑定时间即为同步开始时间
                                        BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                        BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                    } else {
                                        //单机版设置初始时间为7天前
                                        BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, TimeUtils.getNDayBefore(TimeUtils.getTodayYYYYMMDD(), 10, "yyyy-MM-dd"));
                                        BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, TimeUtils.getNDayBefore(TimeUtils.getTodayYYYYMMDD(), 10, "yyyy-MM-dd"));
                                    }
                                }

                                NetProgressObservable.getInstance().hide();
                                Logger.myLog(TAG,"绑定成功，需要存储到本地== " + baseDevice.deviceName+" deviceType="+baseDevice.getDeviceType());
                                //暂不存储本地，先走通网络版本的逻辑，再考虑存本地无网络的情况
                                String deviceId;
                                if (baseDevice.deviceType == JkConfiguration.DeviceType.BODYFAT) {  //体脂称
                                    deviceId = baseDevice.address;
                                } else {
                                    deviceId = baseDevice.deviceName;
                                }


                                ISportAgent.getInstance().bindDevice(baseDevice.deviceType, baseDevice.address, deviceId,
                                        TokenUtil.getInstance().getPeopleIdStr
                                                (BaseApp.getApp()), baseDevice.deviceName);
                                AppConfiguration.isConnected = true;
                                if (isViewAttached()) {
                                    mActView.get().bindSuccess(bindBean);
                                }
                            }
                        }

                    }
                });
    }
/*
    public void bindDevice(BaseDevice baseDevice, int stateHasbindCanbind) {
        MainResposition<Integer, BindInsertOrUpdateBean, BaseUrl, DeviceDbParms> mainResposition = new
                MainResposition<>();
        mainResposition.requst(HistoryParmUtil.setDevice(baseDevice, stateHasbindCanbind))
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<Integer>(context, true) {
                    @Override
                    protected void hideDialog() {

                    }

                    @Override
                    protected void showDialog() {

                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable e) {
                        NetProgressObservable.getInstance().hide();
                        ToastUtils.showToast(context, e.message);
                    }

                    @Override
                    public void onNext(Integer bindBean) {

                        if (baseDevice.deviceType == JkConfiguration.DeviceType.WATCH_W516) {
                            if (App.isHttp()) {
                                //如果是网络版,那么绑定时间即为同步开始时间
                                BleSPUtils.putString(App.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                            } else {
                                //单机版设置初始时间为7天前
                                BleSPUtils.putString(App.getApp(), BleSPUtils.WATCH_LAST_SYNCTIME, TimeUtils.getNDayBefore(TimeUtils.getTodayYYYYMMDD(), 7, "yyyy-MM-dd"));
                            }
                        }
                        if (baseDevice.deviceType == JkConfiguration.DeviceType.BRAND_W311) {
                            if (App.isHttp()) {
                                //如果是网络版,那么绑定时间即为同步开始时间
                                BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                                BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, TimeUtils.getTodayYYYYMMDD());
                            } else {
                                //单机版设置初始时间为7天前
                                BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_SYNCTIME, TimeUtils.getNDayBefore(TimeUtils.getTodayYYYYMMDD(), 10, "yyyy-MM-dd"));
                                BleSPUtils.putString(App.getApp(), BleSPUtils.Bracelet_LAST_HR_SYNCTIME, TimeUtils.getNDayBefore(TimeUtils.getTodayYYYYMMDD(), 10, "yyyy-MM-dd"));
                            }
                        }

                        NetProgressObservable.getInstance().hide();
                        Logger.myLog("绑定成功，需要存储到本地== " + baseDevice.deviceName);
                        //暂不存储本地，先走通网络版本的逻辑，再考虑存本地无网络的情况
                        String deviceId;
                        if (baseDevice.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                            deviceId = baseDevice.address;
                        } else {
                            deviceId = baseDevice.deviceName;
                        }
                        ISportAgent.getInstance().bindDevice(baseDevice.deviceType, baseDevice.address, deviceId,
                                TokenUtil.getInstance().getPeopleIdStr
                                        (BaseApp.getApp()), baseDevice.deviceName);
                        if (isViewAttached()) {
                            mActView.get().bindSuccess(bindBean);
                        }
                    }
                });
    }
*/

    public void select_device_state(BaseDevice baseDevice) {
        MainResposition<DeviceState, BaseParms, BaseUrl, DeviceDbParms> mainResposition = new MainResposition<>();
        mainResposition.requst(HistoryParmUtil.selectDevice(baseDevice))
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<DeviceState>(context, true) {
                    @Override
                    protected void hideDialog() {

                    }

                    @Override
                    protected void showDialog() {

                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable e) {
                        Logger.myLog("ResponeThrowable == " + e.toString() + "e.code:" + e.code);
                        if (e.code == 2000) {
                            if (isViewAttached()) {
                                mActView.get().canBind(ScanBaseView.STATE_NOBIND);
                            }
                        }
                    }

                    @Override
                    public void onNext(DeviceState deviceState) {
                        NetProgressObservable.getInstance().hide();
                        if (deviceState == null) {
                            //说明是新设备,需要调用插入接口
                            Logger.myLog("deviceState==null");
                            if (isViewAttached()) {
                                mActView.get().canBind(ScanBaseView.STATE_NOBIND);
                            }
                        } else {
                            int userid = deviceState.getUserId();
                            Logger.myLog("deviceState != null userid = " + userid);
                            if (userid == 0) {
                                //说明是使用过的，解绑后的设备,可以绑定，调用更新接口
                                if (isViewAttached()) {
                                    mActView.get().canBind(ScanBaseView.STATE_HASBIND_CANBIND);
                                }
                            } else {
                                int state;
                                if (userid == -1) {
                                    //本地绑定成功
                                    state = ScanBaseView.STATE_BINDED;
                                } else {
                                    //已经被绑定的设备，说明不能绑定了，提示用户
                                    state = ScanBaseView.STATE_HASBIND_CONTBIND;
                                }
                                if (isViewAttached()) {
                                    mActView.get().canBind(state);
                                }
                            }
                        }
                    }
                });
    }

    Handler handler = new Handler(Looper.myLooper());
    boolean isConnect = false;

    public void connect(BaseDevice baseDevice, boolean show, boolean isConnectByUser, boolean isDfu) {
        if (!isConnect) {
            isConnect = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isConnect = false;
                    if (deviceOptionImple != null) {
                        if (isDfu) {
                            Constants.isDFU = true;
                        } else {
                            Constants.isDFU = false;
                        }
                        deviceOptionImple.connect(baseDevice, show, isConnectByUser);
                    }
                }
            }, 1000);
        }

    }

    public void connect(BaseDevice baseDevice, boolean show, boolean isConnectByUser) {
        if (deviceOptionImple != null) {
            Constants.isDFU = false;
            deviceOptionImple.connect(baseDevice, show, isConnectByUser);
        }
    }
}