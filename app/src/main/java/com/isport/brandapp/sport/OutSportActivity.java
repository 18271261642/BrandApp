package com.isport.brandapp.sport;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.watch.WatchHrHeartResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.bean.StateBean;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.device.watch.presenter.Device24HrPresenter;
import com.isport.brandapp.device.watch.view.Device24HrView;
import com.isport.brandapp.home.fragment.LatLongData;
import com.isport.brandapp.ropeskipping.speakutil.SpeakUtil;
import com.isport.brandapp.sport.bean.HrBean;
import com.isport.brandapp.sport.bean.PaceBean;
import com.isport.brandapp.sport.bean.SportSettingBean;
import com.isport.brandapp.sport.location.utils.PermissionUtils;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.SportAcacheUtil;
import com.isport.brandapp.util.StepsUtils;
import com.isport.brandapp.util.UserAcacheUtil;
import com.isport.brandapp.view.AnimSporEndView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.today.step.lib.ISportStepInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LifecycleObserver;
import bike.gymproject.viewlibray.ShareItemView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.IndoorRunObservable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * ?????????
 */
public class OutSportActivity extends BaseMVPActivity<InDoorSportView, InDoorSportPresent> implements
        InDoorSportView, View.OnClickListener, Device24HrView, LifecycleObserver {

    SpeakUtil speakUtil;


    SportSettingBean settingBean;


    Device24HrPresenter device24HrPresenter;
    TextView tvDis;
    ShareItemView itemViewSpeed, itemViewTime, itemViewCal;
    ShareItemView itemMapViewSpeed, itemMapViewTime, itemMapViewDis;
    LinearLayout sportGps, layoutMapGps;

    RelativeLayout layoutSportHr, layoutMapHr;

    AnimSporEndView viewPause, viewStart, viewEnd, viewUnLock;
    LinearLayout layout_option, layout_unlock, layout_pause;

    ImageView ivBack, ivMap, ivSetting;
    TextView tvSportName, tvHrValue, tvMapHrValue;

    ImageView ivGPS, ivMapGPS, iv_lock_set;
    ImageView iv_indoor_sport;
    View top_view;


    LinearLayout layoutDis, layoutItems;
    RelativeLayout layoutMap;
    TextView tv_show_gps, tv_show_gps1;

    RelativeLayout layoutHead, layoutBottom;

    boolean isChina = true;

    MyServiceConnection myServiceConnection = new MyServiceConnection();

    StateBean stateBean;

    boolean isShowHr = false;

    private boolean isPause = false;
    private long pauseTime;

    boolean isFirst;

    private LocationManager locationManager;

    private UserInfoBean userInfo;
    private float weight, height;

    private Integer sportType;


    private String[] mTitles = {UIUtils.getString(R.string.outdoor_running), UIUtils.getString(R.string.treadmill), UIUtils.getString(R.string.tdoor_cycling), UIUtils.getString(R.string.walking)};


    private MapView mMapView;
    private LinearLayout layoutGoogle;
    AMap aMap = null;
    List tempList = new ArrayList();


    @Override
    protected InDoorSportPresent createPresenter() {
        device24HrPresenter = new Device24HrPresenter(this);
        return new InDoorSportPresent(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_indoor_sport_layout;
    }

    @Override
    protected void initView(View view) {

        viewPause = view.findViewById(R.id.view_pause);
        viewStart = view.findViewById(R.id.view_start);
        viewEnd = view.findViewById(R.id.view_stop);
        viewUnLock = view.findViewById(R.id.view_unlock);

        layout_option = view.findViewById(R.id.layout_option);
        layout_pause = view.findViewById(R.id.layout_pause);
        layout_unlock = view.findViewById(R.id.layout_unlock);
        iv_lock_set = view.findViewById(R.id.iv_lock_set);
        iv_indoor_sport = view.findViewById(R.id.iv_indoor_sport);


        layoutHead = view.findViewById(R.id.layout_head);

        layoutBottom = view.findViewById(R.id.layout_bottom);
        layoutDis = view.findViewById(R.id.layout_dis);
        layoutMap = view.findViewById(R.id.layout_map);
        layoutItems = view.findViewById(R.id.iems);
        mMapView = view.findViewById(R.id.map);
        layoutGoogle = view.findViewById(R.id.layout_google);
        tv_show_gps = view.findViewById(R.id.tv_show_gps);
        tv_show_gps1 = view.findViewById(R.id.tv_show_gps1);
        top_view = view.findViewById(R.id.top_view);

        tv_show_gps.setVisibility(View.GONE);
        tv_show_gps1.setVisibility(View.GONE);
        // layoutMap.setVisibility(View.GONE);


        ivBack = view.findViewById(R.id.iv_back);
        ivMap = view.findViewById(R.id.iv_map);
        tvSportName = view.findViewById(R.id.tv_sport_type);
        tvDis = view.findViewById(R.id.tv_dis);
        tvHrValue = view.findViewById(R.id.tv_hr_value);
        tvMapHrValue = view.findViewById(R.id.tv_map_hr_value);

        itemViewSpeed = view.findViewById(R.id.item_speed);
        itemViewTime = view.findViewById(R.id.item_time);
        itemViewCal = view.findViewById(R.id.item_cal);

        itemMapViewDis = view.findViewById(R.id.item_map_dis);
        itemMapViewSpeed = view.findViewById(R.id.item_map_speed);
        itemMapViewTime = view.findViewById(R.id.item_map_time);

        ivSetting = view.findViewById(R.id.iv_setting);
        layoutSportHr = view.findViewById(R.id.layout_bg_hr);
        layoutMapHr = view.findViewById(R.id.layout_map_hr);
        sportGps = view.findViewById(R.id.layout_bg_gps);
        layoutMapGps = view.findViewById(R.id.layout_map_gps);
        ivGPS = view.findViewById(R.id.iv_gps);
        ivMapGPS = view.findViewById(R.id.iv_map_gps);

        layout_option.setVisibility(View.GONE);
        layout_unlock.setVisibility(View.GONE);
        sportGps.setVisibility(View.GONE);

        if (isShowHr) {
            tvHrValue.setText("00");
            tvMapHrValue.setText("00");
        } else {
            tvHrValue.setText(UIUtils.getString(R.string.no_data));
            tvMapHrValue.setText(UIUtils.getString(R.string.no_data));
        }
        try {
            userInfo = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(this));
            weight = Float.parseFloat(userInfo.getWeight());
            height = Float.parseFloat(userInfo.getHeight());
        } catch (Exception e) {
            weight = 50;
        } finally {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        settingBean = SportAcacheUtil.getSportTypeSetting(sportType);

        restartServiceIfNeed();
        if (settingBean.isOnScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }

    Bundle savedInstanceState;

    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //??????????????????

        this.savedInstanceState = savedInstanceState;

        // ?????????????????????
        speakUtil = SpeakUtil.getInstance();
        speakUtil.initSpeak(this);

        settingBean = SportAcacheUtil.getSportTypeSetting(sportType);


        IndoorRunObservable.getInstance().addObserver(this);



    }


    @Override
    protected void initData() {


        viewPause.setStartText(UIUtils.getString(R.string.sporting_pause));
        viewStart.setStartText(UIUtils.getString(R.string.sporting_continue));
        viewEnd.setEndText(UIUtils.getString(R.string.sporting_end), UIUtils.getString(R.string.long_press_end));
        viewUnLock.setEndText(UIUtils.getString(R.string.lock), UIUtils.getString(R.string.long_press_unlock));

        itemViewCal.setUnitText(getResources().getString(R.string.watch_step_unite_calory));

        EventBus.getDefault().register(this);


        startService();
        //isChina = getIntent().getBooleanExtra("isChina", true);

        if (TextUtils.isEmpty(Constants.currentCountry)) {
            isChina = true;
        } else {
            if (Constants.currentCountry.equals("??????") || Constants.currentCountry.equals("China")) {
                isChina = true;
            } else {
                isChina = false;
            }
        }
        sportType = getIntent().getIntExtra("sportType", 1);
        tvSportName.setText(mTitles[sportType]);
        layoutMap.setVisibility(View.GONE);
        if (sportType == JkConfiguration.SportType.sportIndoor) {
            iv_indoor_sport.setVisibility(View.VISIBLE);
            sportGps.setVisibility(View.GONE);
            ivMap.setVisibility(View.GONE);
        } else {
            iv_indoor_sport.setVisibility(View.GONE);
            if (sportType == JkConfiguration.SportType.sportBike) {
                itemViewSpeed.setValueText("0");
                itemViewSpeed.setBottomText(R.string.unit_speed);
                itemMapViewSpeed.setValueText("0");
                itemMapViewSpeed.setBottomText(R.string.unit_speed);
            } else {
                itemViewSpeed.setValueText("00'00\"");
                itemViewSpeed.setBottomText(R.string.pace);
                itemMapViewSpeed.setValueText("00'00\"");
                itemMapViewSpeed.setBottomText(R.string.pace);
            }
            sportGps.setVisibility(View.VISIBLE);
            ivMap.setVisibility(View.VISIBLE);
            initMap();
            if (isChina) {
                //??????????????????
                mMapView.setVisibility(View.VISIBLE);
                layoutGoogle.setVisibility(View.GONE);
            } else {
                mMapView.setVisibility(View.GONE);
                layoutGoogle.setVisibility(View.VISIBLE);
                //??????google??????
                initGoogleMap();
            }
        }


        requestPermission();
    }

    //??????GPS??????????????????GPS??????
    private void requestPermission() {
        PermissionManageUtil permissionManage = new PermissionManageUtil(this);
        if (!permissionManage.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionManage.requestPermissions(new RxPermissions(this), Manifest.permission.ACCESS_FINE_LOCATION, UIUtils.getString(R.string.permission_location), new PermissionManageUtil.OnGetPermissionListener() {


                @Override
                public void onGetPermissionYes() {
                    initGPS();
                }

                @Override
                public void onGetPermissionNo() {
                    finish();
                }
            });
        } else {

        }
    }

    private void initGoogleMap() {
        try {

//            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_mapview);
//            assert supportMapFragment != null;
//            supportMapFragment.getMapAsync(this);

          //  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_mapview)).getMapAsync(this);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initGPS() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // ??????GPS??????????????????????????????????????????
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // showSetGpsDialog();
        } else {
            // GPS????????????
            // locationManager.addGpsStatusListener(statusListener);
            // GPS????????????

        }
    }

    private void showSettingButton(boolean isShow) {
        if (isShow) {
            ivBack.setVisibility(View.GONE);
            ivSetting.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.VISIBLE);
            ivSetting.setVisibility(View.GONE);
        }
    }

    public final int type_unload = 1;
    public final int type_option = 2;
    public final int type_pause = 3;

    private void showLayout(int type) {
        switch (type) {
            case type_unload:
                layout_option.setVisibility(View.GONE);
                layout_pause.setVisibility(View.GONE);
                layout_unlock.setVisibility(View.VISIBLE);
                break;
            case type_option:
                layout_option.setVisibility(View.VISIBLE);
                layout_pause.setVisibility(View.GONE);
                layout_unlock.setVisibility(View.GONE);
                break;
            case type_pause:
                layout_option.setVisibility(View.GONE);
                layout_pause.setVisibility(View.VISIBLE);
                layout_unlock.setVisibility(View.GONE);
                break;
        }

    }


    @Override
    protected void initEvent() {
        iv_lock_set.setOnClickListener(this);
        viewPause.setOnSportEndViewOnclick(new AnimSporEndView.OnSportEndViewOnclick() {
            @Override
            public void onStartButton() {
                if (settingBean.isPlayer) {
                    //??????????????????????????????
                    String textPause = UIUtils.getString(R.string.sport_pause);
                    // ????????????
                    speakUtil.startSpeaking(textPause, true);
                }
                OutSportService.inRunIsPause = true;
                ArrayList<LatLongData> temList = new ArrayList<>();
                temList.addAll(OutSportService.theMomentRunData.Latlists);
                OutSportService.theMomentRunData.Latlists.clear();
                OutSportService.theMomentRunData.latMap.put(OutSportService.pauseCount, temList);
                OutSportService.pauseCount += 1;
                if (mList != null) {
                    mList.clear();
                }
                showLayout(type_option);
            }

            @Override
            public void onProgressCompetly() {

            }
        });
        viewStart.setOnSportEndViewOnclick(new AnimSporEndView.OnSportEndViewOnclick() {
            @Override
            public void onStartButton() {
                OutSportService.inRunIsPause = false;
                showLayout(type_pause);


                //??????????????????????????????????????????

                //  OutSportService.theMomentRunData.ArrayThree.
                OutSportService.arrayThreePhoneVelocityTool.clearValue(System.currentTimeMillis(), OutSportService.theMomentRunData.distance * 1000);


                // ?????????????????????????????????????????????
                //FlowerCollector.onEvent(InDoorSportActivity.this, "tts_play");

                if (settingBean.isPlayer) {

                    String text = UIUtils.getString(R.string.continue_start);
                    // ????????????
                    speakUtil.startSpeaking(text, true);
                }

            }

            @Override
            public void onProgressCompetly() {

            }
        });
        viewEnd.setOnSportEndViewOnclick(new AnimSporEndView.OnSportEndViewOnclick() {
            @Override
            public void onStartButton() {

            }

            @Override
            public void onProgressCompetly() {
                //?????????????????????????????????????????????????????????

                Logger.myLog("onProgressCompetly----------------------------");

                if (isSaveData) {
                    return;
                }
                isSaveData = true;
                if (OutSportService.theMomentRunData.distance * 1000 < 200) {
                    // if (false) {
                    PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.sport_tips), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                        @Override
                        public void determine() {
                            endHr();
                            isSaveData = false;
                            finish();

                        }

                        @Override
                        public void cancel() {
                            isSaveData = false;
                            //finish();
                        }
                    }, false);
                } else {
                    if (settingBean.isPlayer) {

                        String text = UIUtils.getString(R.string.sport_stop);
                        // ????????????
                        speakUtil.startSpeaking(text, true);
                    }
                    if (App.appType() == App.httpType) {
                        if (!checkNet())
                            return;
                    }
                    //????????????????????????
                    endHr();
                    int countStep = StepsUtils.countDistToStep(OutSportService.theMomentRunData.distance, userInfo.getGender(), Float.parseFloat(userInfo.getHeight()));;
                    Logger.myLog(TAG,"----OutSportService.theMomentRunData="+OutSportService.theMomentRunData.toString()+" countStep="+countStep);

                    OutSportService.theMomentRunData.setTotalStep(countStep);// = StepsUtils.countDistToStep(OutSportService.theMomentRunData.distance, userInfo.getGender(), Float.parseFloat(userInfo.getHeight()));

                    mActPresenter.saveSportData(OutSportService.argsForInRunService, OutSportService.theMomentRunData, sportType);
                }

            }
        });
        viewUnLock.setOnSportEndViewOnclick(new AnimSporEndView.OnSportEndViewOnclick() {
            @Override
            public void onStartButton() {

            }

            @Override
            public void onProgressCompetly() {
                showLayout(type_pause);
                layout_unlock.setTag("hide");
            }
        });
        ivSetting.setOnClickListener(this);
        ivMap.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivBack.setVisibility(View.GONE);

        ISportAgent.getInstance().registerListener(mBleReciveListener);


    }

    boolean isClick;


    boolean isSaveData = false;


    @Override
    protected void initHeader() {
        ImmersionBar.with(this).titleBar(top_view).statusBarDarkFont(true)
                .init();
        //  StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_lock_set:
                showLayout(type_unload);
                layout_unlock.setTag("show");
                break;
            case R.id.iv_setting:
                if (layout_unlock.getTag() != null && layout_unlock.getTag().equals("show")) {
                    return;
                }
                Intent intent = new Intent(this, SportSettingActivity.class);
                intent.putExtra("sportType", sportType);
                startActivity(intent);
                break;

            case R.id.iv_map:
                if (layout_unlock.getTag() != null && layout_unlock.getTag().equals("show")) {
                    return;
                }
                ivBack.setTag("map");
                tvSportName.setText(UIUtils.getString(R.string.my_position));
                showSettingButton(false);
                tvSportName.setTextColor(getResources().getColor(R.color.common_white));
                ivBack.setImageResource(R.drawable.icon_back);
                ivMap.setVisibility(View.GONE);
                layoutMap.setVisibility(View.VISIBLE);
                layoutItems.setVisibility(View.GONE);
                layoutDis.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.GONE);


                break;
            case R.id.iv_back:


                if (ivBack.getTag() != null && ivBack.getTag().equals("map")) {
                    ivBack.setTag(null);
                    tvSportName.setText(mTitles[sportType]);
                    showSettingButton(true);
                    ivMap.setVisibility(View.VISIBLE);
                    layoutMap.setVisibility(View.GONE);
                    layoutItems.setVisibility(View.VISIBLE);
                    layoutDis.setVisibility(View.VISIBLE);
                    layoutBottom.setVisibility(View.VISIBLE);
                } else {

                    if (!OutSportService.inRunIsPause) {
                        //TODO ?????????????????????

                    } else {

                        finish();
                    }

                }
                break;

        }
    }

    @Override
    public void successSaveData(String publishid) {
        stopService(jwebService);
        Intent intent = new Intent(OutSportActivity.this, EndSportActivity.class);
        intent.putExtra("sporttype", sportType);
        intent.putExtra("publishid", publishid);
        isSaveData = false;
        startActivity(intent);
        finish();
    }

    @Override
    public void failSaveData() {
        isSaveData = false;
        ToastUtils.showToast(this, "save fail");

    }

    @Override
    public void success24HrSwitch(boolean isOpen) {

    }

    @Override
    public void successState(StateBean stateBean) {

        //  Watch_W516_SettingModel w516SettingModelByDeviceId = Watch_W516_SettingModelAction.findW516SettingModelByDeviceId(ISportAgent.getInstance().getCurrnetDevice().getDeviceName(), TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        // if (!w516SettingModelByDeviceId.getHeartRateSwitch()) {
        ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, false, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.endHr));
        //  }


    }


    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    private void startService() {
        Logger.myLog("startService onStartCommand------------");


       /* OutSportService.argsForInRunService.clearData();
        OutSportService.argsForInRunService.phoneStartTime = System.currentTimeMillis();

        OutSportService.theMomentRunData.clearData();
        OutSportService.inRunIsPause = false;
        OutSportService.isFirst = true;
        OutSportService.isFirstPace = true

        OutSportService.arrayThreePhoneVelocityTool.cleanAtRunFinish();
        //?????????????????????????????????

        //?????????????????????
        sportType = TokenUtil.getInstance().getCurrentSportType(BaseApp.getApp());
        OutSportService.pauseCount = 0;*/
        //bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /*  private void startService() {
          //????????????Service???????????????Activity??????aidl??????
          Intent intent = new Intent(this, TodayStepService.class);
          startService(intent);
          bindService(intent, new ServiceConnection() {
              @Override
              public void onServiceConnected(ComponentName name, IBinder service) {
                  //Activity???Service??????aidl????????????
                  iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                  try {
                      mFirstStepSum = iSportStepInterface.getCurrentTimeSportStep();
                      sumStepSum = 0;
                      updateStepCount();
                  } catch (RemoteException e) {
                      e.printStackTrace();
                  }
                  mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

              }

              @Override
              public void onServiceDisconnected(ComponentName name) {

              }
          }, Context.BIND_AUTO_CREATE);
      }
  */
    //double tempDistance;
    double distance;

    private void updateStepCount() {
        Log.e(TAG, "updateStepCount : " + mFirstStepSum);

        /*  titleBarView.setTitle(sumStepSum + "");*/


        //tvDis.setText(mFirstStepSum + "???" + "--" + sumStepSum + "???");

    }


    Disposable disposableTimer;

    long startTime;

    public void startTimer() {
        startTime = System.currentTimeMillis();
        if (disposableTimer != null && !disposableTimer.isDisposed()) {
            disposableTimer.dispose();
        }
        disposableTimer = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {

            @Override
            public void accept(Long aLong) throws Exception {


                if (isPause) {
                    pauseTime++;

                } else {
                    itemViewTime.setValueText(TimeUtil.getTimerFormatedStrings(startTime, startTime + (aLong - pauseTime) * 1000));
                }

                if (null != iSportStepInterface) {
                    int step = 0;
                    try {
                        step = iSportStepInterface.getCurrentTimeSportStep();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } finally {
                        if (isFirst) {
                            isFirst = false;
                            mFirstStepSum = step;
                        } else {
                            if (mFirstStepSum != step) {
                                sumStepSum = step - mFirstStepSum;
                                // mFirstStepSum = step;
                                //mFirstStepSum = step;
                                updateStepCount();
                            }
                        }
                    }


                }


            }
        });
    }

    double mLocationLatitude, mLocationLongitude, speed;
    String strSpeed;
    boolean isFirstLocation = true;

    String strCurrentLocation;


    //float tempDis = 0;


    protected void drawGoogleLine() {
//        List<com.google.android.gms.maps.model.LatLng> path = getLatLngs();
//        googlemapView.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().addAll(path));
//        googlemapView.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(lastLatLng, 12));//new com.google.android.gms.maps.model.LatLng(-33.8256, 151.2395)
    }

//    private com.google.android.gms.maps.model.LatLng lastLatLng;
//
//    private List<com.google.android.gms.maps.model.LatLng> getLatLngs() {
//        List<com.google.android.gms.maps.model.LatLng> paths = new ArrayList();
//        for (int i = 0; i < mList.size(); i++) {
//            LatLongData latLongData = mList.get(i);
//            if (i == mList.size() - 1) {
//                lastLatLng = new com.google.android.gms.maps.model.LatLng(latLongData.getLattitude(), latLongData.getLongitude());
//            }
//            paths.add(new com.google.android.gms.maps.model.LatLng(latLongData.getLattitude(), latLongData.getLongitude()));
//
//        }
//        return paths;
//    }

    Disposable paceTimer;


    /*  private void drawMark() {
          MarkerOptions markerOption = new MarkerOptions();
          LatLng latLng = new LatLng(mLocationLatitude, mLocationLongitude);
          markerOption.position(latLng);
          // markerOption.position(Constants.XIAN);
          markerOption.title("?????????").snippet("DefaultMarker");

          markerOption.draggable(true);//??????Marker?????????
          markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                  .decodeResource(getResources(), R.drawable.icon_sport_start_ing)));
          // ???Marker????????????????????????????????????????????????????????????
  //        markerOption.setFlat(true);//??????marker??????????????????
          aMap.addMarker(markerOption);
      }
  */
    class TodayStepCounterCall implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //??????500????????????????????????????????????UI

                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }

    }

    //???????????????????????????????????????????????????
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());
    private int mFirstStepSum;//????????????????????????
    private int sumStepSum;

    private ISportStepInterface iSportStepInterface;
    private static final int REFRESH_STEP_WHAT = 0;



               /* .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                if(aLong == 3){
                    popupWindow.dismiss();
                }
            }
        })*/;

       /* PopWinCountDownTimer mPopWinCountDownTimer = new PopWinCountDownTimer(4 * 1000, 1000) {

            @Override
            public void updateSoundAndView(long millisUntilFinished, AnimationSet animationSet) {

                if (countDownNumber == 3) {
                    playText("??????");
                } else if (countDownNumber == 2) {
                    playText("??????");
                } else {
                    playText("??????");
                }
                countDownNumber--;
                if (millisUntilFinished / 1000 == 0) {

                } else {
                    mTimer.setText(+millisUntilFinished / 1000 + "");
                    mTimer.setAnimation(animationSet);
                    mTimer.startAnimation(animationSet);
                }
            }

            @Override
            public void onCountDownFinish() {
                popupWindow.dismiss();
                countDownNumber = 3;
                // playText("???????????? ???");
                startIndoorRun();// ???????????????
            }

        };
        mPopWinCountDownTimer.start();*/


    public AMapLocationClientOption mLocationOption = null;

    public void initMap() {

        mMapView.onCreate(savedInstanceState);

        //??????AMapLocationClient?????????
        //???????????????????????????
        //???????????????
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            //aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            if (App.isZh(UIUtils.getContext())) {
                aMap.setMapLanguage(AMap.CHINESE);
            } else {
                aMap.setMapLanguage(AMap.ENGLISH);
            }
            mList = new ArrayList<LatLongData>();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//??????????????????????????????myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1???????????????????????????myLocationType????????????????????????????????????
        myLocationStyle.interval(2000); //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        aMap.setMyLocationStyle(myLocationStyle);//?????????????????????Style
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);?????????????????????????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true?????????????????????????????????false??????????????????????????????????????????????????????false???

        //getAgrSpeerColorHashMap();
        //drawLine();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.update_location_error:
                break;
            case MessageEvent.update_location:
                analysisSportStatus();
                break;
        }
    }


    //??????????????????
    private void analysisSportStatus(){
        try {
            if (OutSportService.inRunIsPause) {//??????????????????????????????
                return;
            }
            if (sportType == JkConfiguration.SportType.sportIndoor) {
                return;
            }//0.528?????? 5.07??????
//??????????????????amapLocation?????????????????????
            mLocationLatitude = Constants.mLocationLatitude;
            mLocationLongitude = Constants.mLocationLatitude;
            speed = Constants.speed;


            Logger.myLog(TAG,"------??????????????????="+speed);
            //????????????/???
            //strSpeed = CommonDateUtil.formatOnePoint(speed);


            strSpeed = CommonDateUtil.formatPaceForSpeed(speed);

            // int codePause = mTts.startSpeaking(amapLocation.getCity(), mTtsListener);
            int type = Constants.locationType;
            int gpsType = Constants.gpstype;
            float accuracy = Constants.accuracy;
            Logger.myLog(TAG,"update_location:" + "Constants.mLocationLatitude:" + Constants.mLocationLatitude + ":" + Constants.mLocationLatitude + "type:" + type + "accuracy:" + accuracy + "gpsstate:" + Constants.gpstype);

            if (Constants.gpstype == AMapLocation.GPS_ACCURACY_UNKNOWN) {
                setGpstaues(0);
            } else if (Constants.gpstype == AMapLocation.GPS_ACCURACY_BAD) {
                setGpstaues(1);
            } else if (Constants.gpstype == AMapLocation.GPS_ACCURACY_GOOD) {
                setGpstaues(3);
            }

            if (tempListTwo == null) {
                tempListTwo = new ArrayList<>();
            }
            if (tempListTwo.size() <= 1) {
                tempListTwo.add(new LatLng(mLocationLatitude, mLocationLongitude));
            } else {
                tempListTwo.remove(0);
                tempListTwo.add(new LatLng(mLocationLatitude, mLocationLongitude));
            }
            //??????????????????????????????
            float distance;
            if (accuracy > 0 && accuracy <= 65) {
                strCurrentLocation = mLocationLongitude + "," + mLocationLatitude;
                if (tempListTwo.size() > 1) {
                    //???????????? ????????????????????????????????????
                    distance = AMapUtils.calculateLineDistance(tempListTwo.get(tempListTwo.size() - 2), tempListTwo.get(tempListTwo.size() - 1));

                    Logger.myLog(TAG,"---??????????????????????????????="+distance);

                    //?????????
                    if (distance == 0 || gpsType == -1) {
                        //mList.remove(mList.get(mList.size() - 1));
                        // OutSportService.theMomentRunData.Latlists.remove(OutSportService.theMomentRunData.Latlists.get(OutSportService.theMomentRunData.Latlists.size() - 1));
                        setitemSpeed(sportType);
                        //** ?????????5????????????????????????0 ?????????0**//*
                        //??????????????????????????????8??????????????????
                    } else {
                        mList.add(mList.size(), addLatLongData(Constants.mLocationLatitude, Constants.mLocationLongitude, Constants.speed, System.currentTimeMillis() / 1000));
                        OutSportService.theMomentRunData.Latlists.add(addLatLongData(Constants.mLocationLatitude, Constants.mLocationLongitude, Constants.speed, System.currentTimeMillis() / 1000));
                        //?????????s

                        long dTime = mList.size() == 1 ? mList.get(0).getTime() : (mList.get(mList.size() - 1).getTime() - mList.get(mList.size() - 2).getTime());
                        //????????????????????????????????? distance?????????????????????????????????????????????
                        OutSportService.startTimer();
                        //????????????????????????
                        OutSportService.theMomentRunData.setDistance(OutSportService.theMomentRunData.distance + (distance / 1000));
                        //??????????????????
                        PaceBean bean = StepsUtils.calPace(distance, dTime, OutSportService.theMomentRunData.timer);

                        Logger.myLog(TAG,"-----????????????="+bean.getPace()+"\n"+bean.getStrPace());


                        OutSportService.theMomentRunData.paceBean.put(OutSportService.theMomentRunData.timer, bean);
                        if (sportType == JkConfiguration.SportType.sportBike) {  //?????????

                            String bikeSpeedStr = CommonDateUtil.div((double) distance,3.6d,2)+"";

                            itemViewSpeed.setValueText(bikeSpeedStr+"");
                            itemMapViewSpeed.setValueText(bikeSpeedStr+"");
                            palyDis(OutSportService.theMomentRunData.distance);
                            if (settingBean != null && settingBean.isPlayer && settingBean.isPaceRemind && speed < settingBean.currentPaceValue) {
                                //???????????????????????????????????????
                                if (UserAcacheUtil.isPaceRemind()) {
                                    UserAcacheUtil.savePaceRemind();
                                    speakUtil.startSpeaking(UIUtils.getString(R.string.below_the_speed_tips), false);
                                }
                            }

                        } else {
                            itemViewSpeed.setValueText(bean.getPace());
                            itemMapViewSpeed.setValueText(bean.getPace());
                            calCurrentDis(OutSportService.theMomentRunData.distance, bean.getStrPace());
                        }
                        OutSportService.theMomentRunData.setCalories(OutSportService.theMomentRunData.calories + StepsUtils.calCalorie(weight, distance, sportType));
                        itemViewCal.setValueText(CommonDateUtil.formatInterger(OutSportService.theMomentRunData.calories));
                        tvDis.setText(CommonDateUtil.formatTwoPoint(OutSportService.theMomentRunData.distance));
                        //?????????????????????????????????????????????
                        itemMapViewDis.setValueText(CommonDateUtil.formatTwoPoint(OutSportService.theMomentRunData.distance));

                    }
                }
            }
            if (isFirstLocation) {
                if (mLocationLatitude > 0 && mLocationLongitude > 0) {
                    //drawMark();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationLatitude, mLocationLongitude), 17);
                    aMap.moveCamera(cu);
                    isFirstLocation = false;
                } else {
                    // CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.DEFAULT_LATITUDE, Constant.DEFAULT_LONGITUDE), 17);
                    // aMap.moveCamera(cu);
                }
            } else {
                //drawLine();
                if (isChina) {
                    drawGreenLine();
                } else {
                    drawGoogleLine();
                }
                //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jwebService != null) {
            stopService(jwebService);
        }
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        /*if (myServiceConnection != null) {

            unbindService(myServiceConnection);
        }*/
//        if (googlemapView != null) {
//            googlemapView.clear();
//        }
        if (aMap != null) {
            aMap.clear();
        }
        //LocationHelp.getInstance().stopLocation();
        EventBus.getDefault().unregister(this);
        IndoorRunObservable.getInstance().deleteObserver(this);
    }

    private void setGoogleMapView(AMapLocation amapLocation) {
        //  LatLng sydney = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//        googlemapView.clear();
//        com.google.android.gms.maps.model.LatLng sydney = new com.google.android.gms.maps.model.LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//        com.google.android.gms.maps.CameraUpdate cu = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(sydney, 17);
//        googlemapView.moveCamera(cu);
//        /* googlemapView.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(sydney).title("Marker in Sydney"));*/
//        setGoogleMarkerOptions(sydney);
        // ???Marker????????????????????????????????????????????????????????????
        //googlemapView.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onObserverChange(java.util.Observable o, Object arg) {
        Log.e("MyLog", "onObserverChange");
        if (o instanceof IndoorRunObservable) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateIndoorRunData();
                }
            });
        } else if (arg instanceof Boolean) { //????????????????????????
            boolean isConn = (boolean) arg;
            // Logger.e("TAG", "onObserverChange isConn = " + isConn);
            if (isConn) {
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // handlerBluetoothClosed();
                }
            });
        }
    }

    public void updateIndoorRunData() {
        itemViewTime.setValueText(OutSportService.theMomentRunData.strTime);
        itemMapViewTime.setValueText(OutSportService.theMomentRunData.strTime);
        // ???????????????
        // ???????????????kcal???????????????kg?????????????????????????1.036


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //???activity??????onSaveInstanceState?????????mMapView.onSaveInstanceState (outState)??????????????????????????????
        mMapView.onSaveInstanceState(outState);
        if (OutSportService.theMomentRunData.distance * 1000 < 200) {
            return;
        }
        OutSportService.theMomentRunData.disToStep = StepsUtils.countDistToStep(OutSportService.theMomentRunData.distance, userInfo.getGender(), Float.parseFloat(userInfo.getWeight()));
        mActPresenter.savaSportDb(OutSportService.argsForInRunService, OutSportService.theMomentRunData, sportType);
        // mActPresenter.savaSportDb(OutSportService.argsForInRunService, OutSportService.theMomentRunData, sportType);
    }

    //??????????????????????????????
    Polyline polyline;
    Marker marker;

    private void drawGreenLine() {


        // List<LatLng> latLngs = new ArrayList<LatLng>();
        //latLngs.add(new LatLng(mLocationLatitude, mLocationLongitude));
        //latLngs.add(new LatLng(22.566671, 113.867043));
        //latLngs.add(new LatLng(39.900430, 113.867214));
        //latLngs.add(new LatLng(22.573522, 113.864806));
        if (mList.size() == tempList.size()) {
            return;
        }
        tempList.clear();

        if (mList != null && mList.size() > 1) {
            for (int i = 0; i < mList.size(); i++) {
                tempList.add(mList.get(i).getLatLng());
            }

        } else {
            return;
        }

        /*if(polyline!=null){
            aMap.re(polyline);
        }
*/
       /* if(marker==null) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.draggable(false);//??????Marker?????????
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.icon_sport_start_ing)));
            // ???Marker????????????????????????????????????????????????????????????
            markerOption.setFlat(true);//??????marker??????????????????
            marker = aMap.addMarker(markerOption);
        }*/
        polyline = aMap.addPolyline(new PolylineOptions().
                addAll(tempList).width(15).color(Color.argb(255, 29, 206, 116)));

    }

    private void drawLine() {
        List<LatLongData> mListItem = getSpeerDateList(mList);
        if (mListItem == null) {
            return;
        }
        List<Integer> colorList = new ArrayList<Integer>();

        PolylineOptions po = new PolylineOptions();

        for (int i = mListItem.size() - 1; i < mListItem.size(); i++) {
            LatLng point = new LatLng(mListItem.get(i).getLattitude(), mListItem.get(i).getLongitude());
            po.add(point);
            colorList.add(colorList.size(), agrSpeerColorHashMap.get(Integer.parseInt(mListItem.get(i).getSpeer())));
        }

        po.width(12);
//        po.useGradient(true);
//        po.colorValues(colorList);
        po.zIndex(10);
        aMap.addPolyline(po);
    }

    private void drawOneLine() {
        ArrayList tempList = new ArrayList();
        if (mList.size() > 1) {
            tempList.add(mList.get(mList.size() - 2));

            tempList.add(mList.get(mList.size() - 1));
        } else {
            return;
        }
        List<LatLongData> mListItem = getSpeerDateList(tempList);
        List<Integer> colorList = new ArrayList<Integer>();

        PolylineOptions po = new PolylineOptions();
        for (int i = mListItem.size() - 1; i < mListItem.size(); i++) {
            LatLng point = new LatLng(mListItem.get(i).getLattitude(), mListItem.get(i).getLongitude());
            po.add(point);
            colorList.add(colorList.size(), agrSpeerColorHashMap.get(Integer.parseInt(mListItem.get(i).getSpeer())));
        }

        po.width(12);
//        po.useGradient(true);
//        po.colorValues(colorList);
        po.zIndex(10);
        aMap.addPolyline(po);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param list
     * @return
     */
    public static List<LatLongData> getSpeerDateList(List<LatLongData> list) {
        if (list == null || list.size() == 0) {
            return null;
        }

        for (LatLongData item : list) {
            if (Float.parseFloat(item.getSpeer()) < 6) {
                item.setSpeer(String.valueOf(5));
            } else if (Float.parseFloat(item.getSpeer()) < 7
                    && Float.parseFloat(item.getSpeer()) >= 6) {
                item.setSpeer(String.valueOf(6));
            } else if (Float.parseFloat(item.getSpeer()) < 9
                    && Float.parseFloat(item.getSpeer()) >= 7) {
                item.setSpeer(String.valueOf(8));
            } else {
                item.setSpeer(String.valueOf(9));
            }
        }

        return list;

    }

    public void getAgrSpeerColorHashMap() {
        agrSpeerColorHashMap = new HashMap<Integer, Integer>();
        agrSpeerColorHashMap.put(5, colorList[0]);
        agrSpeerColorHashMap.put(6, colorList[1]);
        agrSpeerColorHashMap.put(8, colorList[2]);
        agrSpeerColorHashMap.put(9, colorList[3]);
    }

    private List<LatLongData> mList;
    private List<LatLng> tempListTwo;

    private void drawTestLine() {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(22.566423, 113.867558));
        latLngs.add(new LatLng(22.566671, 113.867043));
        //latLngs.add(new LatLng(39.900430, 113.867214));
        //latLngs.add(new LatLng(22.573522, 113.864806));
        Polyline polyline = aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 29, 206, 116)));

        List<LatLng> latLngs1 = new ArrayList<LatLng>();
        /*latLngs.add(new LatLng(22.566423, 113.867558));
        latLngs.add(new LatLng(22.566671, 113.867043));
        //latLngs.add(new LatLng(39.900430, 113.867214));
        latLngs.add(new LatLng(22.57101, 113.862172));*/
        latLngs1.add(latLngs.get(latLngs.size() - 1));
        latLngs1.add(new LatLng(22.573522, 113.864806));

        Polyline polyline1 = aMap.addPolyline(new PolylineOptions().
                addAll(latLngs1).width(10).color(Color.argb(255, 1, 90, 1)));


    }

    private String[] lng = {"113.867558,22.566423", "113.867043,22.566731",
            "113.867214,22.566671", "113.867193,22.566582",
            "113.866624,22.566939", "113.865337,22.56782",
            "113.864854,22.568197", "113.864854,22.568197",
            "113.864854,22.568197", "113.864854,22.568197",
            "113.864854,22.568197", "113.861925,22.570555",
            "113.861925,22.570599", "113.861952,22.570728",
            "113.861952,22.570723", "113.862172,22.57101",
            "113.862708,22.571531", "113.863084,22.571947",
            "113.863084,22.571947", "113.864226,22.57314",
            "113.864543,22.573502", "113.864559,22.573487",
            "113.864656,22.573522", "113.864731,22.573522",
            "113.864806,22.573522"};// ???????????????
    private double[] speerList = {5.88, 6.9, 9.5, 10.5, 8.9, 5.88, 6.9, 9.5,
            10.5, 8.9, 5.88, 6.9, 9.5, 10.5, 8.9, 5.88, 6.9, 9.5, 10.5, 8.9,
            5.88, 6.9, 9.5, 10.5, 8.9,};// ????????????

    private int[] colorList = {0xFFFBE01C, 0xFFE1E618, 0xFF7DFF00, 0xffDE2C00};// ?????????
    public HashMap<Integer, Integer> agrSpeerColorHashMap;

    private LatLongData addLatLongData(String lng, double d) {
        LatLongData mLatLongData = new LatLongData();
        String[] lat = lng.split("\\,");
        mLatLongData.setSpeer("" + d);
        mLatLongData.setLongitude(Float.parseFloat(lat[0]));
        mLatLongData.setLattitude(Float.parseFloat(lat[1]));

        return mLatLongData;
    }

    private LatLongData addLatLongData(float lngla, float lnglong, double d, long time) {

        //latLngs.add(new LatLng(22.566671, 113.867043))
        LatLongData mLatLongData = new LatLongData();
        mLatLongData.setLongitude(lnglong);
        mLatLongData.setLattitude(lngla);
        mLatLongData.setTime(time);
        mLatLongData.setLatLng(new LatLng(lngla, lnglong));


        // mLatLongData.setLongitude(lngla);
        //mLatLongData.setLattitude(lnglong);
        return mLatLongData;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    /**
     * ??????????????????
     */

    /**
     * ?????????????????????
     */

    private void showTip(final String str) {
      /*  mToast.setText(str);
        mToast.show();*/
    }


    /**
     * ???????????? ???????????????
     *
     * @return
     */

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

    }


    public void setHrValue(Integer hrValue) {
        if (hrValue > 30) {
            playHr(hrValue);
            HrBean hrBean = new HrBean();
            hrBean.setHeartRate(hrValue);
            hrBean.setTime(OutSportService.theMomentRunData.timer);
            OutSportService.theMomentRunData.heartRateMap.put(OutSportService.theMomentRunData.timer, hrBean);
            tvHrValue.setText(hrValue + "");
            tvMapHrValue.setText(hrValue + "");
        }
    }

    private void playHr(int hrValue) {
        if (settingBean != null && settingBean.isHrRemind && hrValue > settingBean.currentHrValue && !OutSportService.inRunIsPause) {
            //???????????????????????????????????????

            if (UserAcacheUtil.isHrRemind()) {
                //?????????5????????????????????????
                UserAcacheUtil.saveHrRemind();
                speakUtil.startSpeaking(UIUtils.getString(R.string.below_the_hr_tips), false);

            }

        }
    }

    //???????????????
    private final BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            if (isConn) {
            } else {
                tvHrValue.setText(UIUtils.getString(R.string.no_data));
                tvMapHrValue.setText(UIUtils.getString(R.string.no_data));
            }
          /*  if (mineItemHolder != null)
                mineItemHolder.notifyList(getDeviceList(baseDevice.deviceType, isConn));*/
        }

        @Override
        public void setDataSuccess(String s) {

        }


        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.WATCH_REALTIME_HR:
                        //OutSportService.theMomentRunData.paceBean.put(OutSportService.theMomentRunData.timer, bean);
                        WatchHrHeartResult mResult2 = (WatchHrHeartResult) mResult;
                        if (mResult2.getHeartRate() > 0) {
                            setHrValue(mResult2.getHeartRate());
                        } else {
                            tvHrValue.setText(UIUtils.getString(R.string.no_data));
                            tvMapHrValue.setText(UIUtils.getString(R.string.no_data));
                        }
                        // tvRateTime.setText(TimeUtil.getTimerFormatedStrings(startTime, startTime + (aLong - pauseTime) * 1000));
                       /* int hrvalue = mResult2.getHeartRate();
                        list.add(hrvalue);
                        heartRateIngView.setData(list);
                        heartRateIngView.setMaxScale(list.size());
                        heartRateIngView.setFirstScale(list.size() - 2);
                        heartRateIngView.invalidate();*/

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


    public void setGpstaues(int lever) {

        if (lever == 0) {
            ivGPS.setImageResource(R.drawable.icon_gps_nor);
            ivMapGPS.setImageResource(R.drawable.icon_gps_nor);
        } else if (lever == 1) {
            ivGPS.setImageResource(R.drawable.icon_gps_one);
            ivMapGPS.setImageResource(R.drawable.icon_gps_one);
        } else if (lever == 2) {
            ivGPS.setImageResource(R.drawable.icon_gps_two);
            ivMapGPS.setImageResource(R.drawable.icon_gps_two);
        } else if (lever == 3) {
            ivGPS.setImageResource(R.drawable.icon_gps_three);
            ivMapGPS.setImageResource(R.drawable.icon_gps_three);
        }

    }

    public void setitemSpeed(int type) {
        if (type == JkConfiguration.SportType.sportBike) {
            itemViewSpeed.setValueText("0");
            itemMapViewSpeed.setValueText("0");
        } else {
            itemViewSpeed.setValueText(JkConfiguration.strPace);
            itemMapViewSpeed.setValueText(JkConfiguration.strPace);
        }
    }


    //  int x = 1000;

    public void palyDis(double distance) {
        int intDis = (int) (distance * 1000);
        if (intDis < 1000 && OutSportService.theMomentRunData.currentDis != 1000) {
            OutSportService.theMomentRunData.currentDis = 1000;//????????????????????????
        }
        //1000
        if (intDis - OutSportService.theMomentRunData.currentDis >= 0) {
            OutSportService.theMomentRunData.currentDis += 1000;
            playerDis(OutSportService.theMomentRunData.distance, OutSportService.theMomentRunData.timer);
        }


    }

    private void calCurrentDis(double distance, float pace) {

        palyDis(distance);


        if (settingBean.isPlayer && settingBean != null && settingBean.isPaceRemind) {
            //???????????????????????????????????????

            if (pace != 0 && pace > settingBean.currentPaceValue) {
                if (UserAcacheUtil.isPaceRemind()) {
                    //?????????5????????????????????????
                    UserAcacheUtil.savePaceRemind();
                    speakUtil.startSpeaking(UIUtils.getString(R.string.below_the_pace_tips), false);
                }
            }
        }


    }

    ConcurrentHashMap<Integer, String> readList = new ConcurrentHashMap<>();

    private void playerDis(double dis, long timer) {
        if (settingBean != null && settingBean.isPlayer) {
            //???????????????????????????????????????
            String time = "";
            time = TimeUtil.getTimerHourMin(timer * 1000, 0);
            speakUtil.startSpeaking(String.format(UIUtils.getString(R.string.below_the_dis_tips), CommonDateUtil.formatTwoPoint(dis), time), true);
        }

    }


    public void endHr() {
        if (AppConfiguration.isConnected) {
            if (DeviceTypeUtil.isContainBrand() || DeviceTypeUtil.isContainWatchW556()) {
                if (AppConfiguration.hasSynced) {
                    ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, false);
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.endHr));
                }
            } else if (DeviceTypeUtil.isContainWatch()) {
                if (AppConfiguration.hasSynced) {
                    BaseDevice device = ISportAgent.getInstance().getCurrnetDevice();
                    if (device != null) {
                        device24HrPresenter.getMessageCallState(device.deviceName, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    }
                }
            }
        }
    }

    Intent jwebService;

    private void restartServiceIfNeed() {
        //????????????
        Log.e("GaoDeLocationImpl", "restartServiceIfNeed");
        if (!PermissionUtils.isServiceRunning(this, OutSportService.class)) {
            Log.e("GaoDeLocationImpl", "restartServiceIfNeed2");
            jwebService = new Intent(this, OutSportService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(jwebService);
            } else {
                startService(jwebService);
            }
        }

    }


}
