package com.isport.brandapp.home.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.watch.WatchHrHeartResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.bean.StateBean;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.watch.presenter.Device24HrPresenter;
import com.isport.brandapp.device.watch.view.Device24HrView;
import com.isport.brandapp.home.FloatWindowParamManager;
import com.isport.brandapp.home.RomUtils;
import com.isport.brandapp.home.bean.SportLastDataBean;
import com.isport.brandapp.sport.InDoorSportActivity;
import com.isport.brandapp.sport.MLocationListener;
import com.isport.brandapp.sport.OutSportActivity;
import com.isport.brandapp.sport.SportReportActivity;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.view.AnimSporEndView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseFragment;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author flyly
 * @Date 2019/1/10
 * @Fuction
 */

public class FragmentLeftSport extends BaseFragment implements Device24HrView {


    LinearLayout layoutSportGps, layout_right;
    RelativeLayout layoutSportHr;
    TextView tvResult;
    Button btnStart;
    AnimSporEndView animSporEndView;
    // TextView layoutStart;
    ImageView ivGps;

    Integer sportType;
    TextView tvHrValue;

    boolean isChina;

    TextView tvLastDis, tvDate, tvAllDis;

    HashMap<Integer, Integer> types = new HashMap<>();
    private LocationManager locationManager;

    SportLastDataBean sportLastDataBean;
    ImageView viewIdoor;

    Device24HrPresenter device24HrPresenter;

    private AlertDialog.Builder gSensorDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_left_sport;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowHr = false;
        types.put(JkConfiguration.SportType.sportOutRuning, R.drawable.icon_sport_bg);
        types.put(JkConfiguration.SportType.sportIndoor, R.drawable.icon_indoor_sport);
        types.put(JkConfiguration.SportType.sportWalk, R.drawable.icon_sport_walk);
        types.put(JkConfiguration.SportType.sportBike, R.drawable.icon_sport_bike);
        Bundle bundle = getArguments();
        sportType = bundle.getInt("sportType", JkConfiguration.SportType.sportOutRuning);

    }


    @Override
    protected void initView(View view) {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        layoutSportHr = view.findViewById(R.id.layout_bg_hr);
        layoutSportGps = view.findViewById(R.id.layout_bg_gps);
        animSporEndView = view.findViewById(R.id.startAniSportEndView);
        // layoutStart = view.findViewById(R.id.layout_bg_start);
        //ivSportTypeBg = view.findViewById(R.id.sport_bg);
        tvHrValue = view.findViewById(R.id.tv_hr_value);
        viewIdoor = view.findViewById(R.id.view_indoor);
        ivGps = view.findViewById(R.id.iv_gps);

        tvLastDis = view.findViewById(R.id.tv_dis);
        tvDate = view.findViewById(R.id.tv_sport_time);
        tvAllDis = view.findViewById(R.id.tv_sum_dis);
        layout_right = view.findViewById(R.id.layout_right);


        btnStart = view.findViewById(R.id.stepArrayButton);


        tvResult = view.findViewById(R.id.tv_result);


        /*ShadowDrawable.setShadowDrawable(layoutSportMap, ShadowDrawable.SHAPE_CIRCLE, getResources().getColor(R.color.white), DisplayUtils.dip2px(getActivity(), 8),
                Color.parseColor("#14000000"), DisplayUtils.dip2px(getActivity(), 2), 0, DisplayUtils.dip2px(getActivity(), 2));
        ShadowDrawable.setShadowDrawable(layoutSportSetting, ShadowDrawable.SHAPE_CIRCLE, getResources().getColor(R.color.white), DisplayUtils.dip2px(getActivity(), 8),
                Color.parseColor("#14000000"), DisplayUtils.dip2px(getActivity(), 2), 0, DisplayUtils.dip2px(getActivity(), 2));
        ShadowDrawable.setShadowDrawable(layoutSportHr, getResources().getColor(R.color.white), DisplayUtils.dip2px(getActivity(), 50),
                Color.parseColor("#14000000"), DisplayUtils.dip2px(getActivity(), 2), 0, DisplayUtils.dip2px(getActivity(), 2));
        ShadowDrawable.setShadowDrawable(layoutSportGps, getResources().getColor(R.color.white), DisplayUtils.dip2px(getActivity(), 50),
                Color.parseColor("#14000000"), DisplayUtils.dip2px(getActivity(), 2), 0, DisplayUtils.dip2px(getActivity(), 2));*/


    }

    boolean isShowHr;

    @Override
    protected void initData() {

        device24HrPresenter = new Device24HrPresenter(this);
        if (types.containsKey(sportType)) {
            //ivSportTypeBg.setImageResource(types.get(sportType));
        }
        if (sportType == JkConfiguration.SportType.sportIndoor) {
            layoutSportGps.setVisibility(View.GONE);
            viewIdoor.setVisibility(View.VISIBLE);
        } else {
            layoutSportGps.setVisibility(View.VISIBLE);
            viewIdoor.setVisibility(View.GONE);
        }

        if (isShowHr) {
            tvHrValue.setText("00");
        } else {
            tvHrValue.setText(UIUtils.getString(R.string.no_data));
        }

        initGPS();
        animSporEndView.setStartText(UIUtils.getString(R.string.rope_start));

    }


    @Override
    protected void initEvent() {

        ISportAgent.getInstance().registerListener(mBleReciveListener);

        layout_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SportReportActivity.class);
                startActivity(intent);
            }
        });
        tvAllDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SportReportActivity.class);
                startActivity(intent);
            }
        });
        animSporEndView.setonSportEndViewOnclick(new AnimSporEndView.OnSportEndViewOnclick() {
            @Override
            public void onStartButton() {

                if (isFastDoubleClick()) {
                    return;
                }
                if (sportType == JkConfiguration.SportType.sportIndoor) {
                    if (DeviceTypeUtil.isContainWatch()) {
                        device24HrPresenter.getMessageCallState(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                    } else if (DeviceTypeUtil.isContainBrand()) {
                        success24HrSwitch(false);
                    }
                    showCountDownPopWindow();
                } else {
                    isOpenGps();
                }
            }

            @Override
            public void onProgressCompetly() {

            }
        });
        /*layoutSportSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), SportSettingActivity.class);
                intent.putExtra("sportType", sportType);
                startActivity(intent);

            }
        });*/

       /* layoutStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (contentView != null) {
            contentView.dismiss();
        }


        if (AppConfiguration.sportLastDataBeanHashMap.containsKey(sportType)) {
            sportLastDataBean = AppConfiguration.sportLastDataBeanHashMap.get(sportType);
            tvLastDis.setText(CommonDateUtil.formatTwoPoint(Float.parseFloat(sportLastDataBean.getLastDistance())));
          /*  StringFomateUtil.formatText(StringFomateUtil.FormatType.Alone, context, tvLastDis, ContextCompat.getColor
                    (context, R.color.common_white), R.string.dis_unit_number, CommonDateUtil.formatTwoPoint(Float.parseFloat(sportLastDataBean.getLastDistance())), "0.25");*/
            if (sportLastDataBean.getLastTimestamp() == 0) {
                tvDate.setText(UIUtils.getString(R.string.no_data));
            } else {
                tvDate.setText(TimeUtils.getTimeByMMDDHHMMSS
                        (sportLastDataBean.getLastTimestamp()));
            }
            tvAllDis.setText(CommonDateUtil.formatTwoPoint(Float.parseFloat(sportLastDataBean.getAllDistance())));
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contentView != null) {
            contentView.dismiss();
        }
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    public AnimationSet setAnim() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        // 渐变时间
        alphaAnimation.setDuration(1000);
        ScaleAnimation animation = new ScaleAnimation(0.0f, 2.6f, 0.0f, 2.6f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        return animationSet;
        // updateSoundAndView(millisUntilFinished, animationSet);
    }

    BaseDialog contentView;

    private void showCountDownPopWindow() {
        // 一个自定义的布局，作为显示的内容
        if(sportType == JkConfiguration.SportType.sportIndoor){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                if(XXPermissions.isPermanentDenied(getActivity(),Manifest.permission.BODY_SENSORS)){
                    showPermissDialog(Manifest.permission.BODY_SENSORS,"室内跑需要使用手机计步传感器获取步数信息，是否确认打开改权限?",0);
                    return;
                }
            }
        }

        if(sportType == JkConfiguration.SportType.sportOutRuning){

            if(XXPermissions.isPermanentDenied(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                showPermissDialog(Manifest.permission.ACCESS_FINE_LOCATION,"室外跑需要获取位置信息使定位更加精确，是否确认打开改权限?",1);
                return;
            }

        }

        contentView = new BaseDialog.Builder(getActivity())
                .setContentView(R.layout.popwin_count_down)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .show();


        // View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popwin_count_down, null);

        final TextView mTimer = (TextView) contentView.findViewById(R.id.timer);


        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(4).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                //   disposable[0] =d;
            }

            @Override
            public void onNext(Long aLong) {
                NetProgressObservable.getInstance().hide();
                mTimer.setText(3 - aLong + "");
                mTimer.setAnimation(setAnim());
                mTimer.startAnimation(setAnim());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                //popupWindow.dismiss();
                //保存用户运动的类型
                TokenUtil.getInstance().saveCurrentSportType(BaseApp.getApp(), sportType);
                contentView.dismiss();
                if (sportType == JkConfiguration.SportType.sportIndoor) {
                    Intent intent = new Intent(getContext(), InDoorSportActivity.class);
                    intent.putExtra("sportType", sportType);
                    intent.putExtra("isChina", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), OutSportActivity.class);
                    intent.putExtra("sportType", sportType);
                    intent.putExtra("isChina", true);
                    startActivity(intent);
                }


            }
        });
    }


    //申请传感器弹窗
    private void showPermissDialog(String permiss,String context,int code){
        gSensorDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.tips))
                .setMessage(context)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getGSensorPermission(permiss,code);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog atD = gSensorDialog.create();
        atD.show();
        Button sureBtn = atD.getButton(DialogInterface.BUTTON_POSITIVE);
        Button cancelBtn = atD.getButton(DialogInterface.BUTTON_NEGATIVE);

        sureBtn.setTextColor(Color.BLACK);
        cancelBtn.setTextColor(Color.BLACK);
    }



    private void getGSensorPermission(String psermiss,int code){
        XXPermissions.with(this)
                .permission(psermiss)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> list, boolean b) {

                    }
                });
        if(code == 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                XXPermissions.with(this)
                        .permission(Manifest.permission.BODY_SENSORS)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> list, boolean b) {

                            }
                        });
            }
        }


    }




    public void isOpenGps() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), UIUtils.getString(R.string.gps_tips), Toast.LENGTH_SHORT).show();
            return;
        } else {
            //需要去开启心率功能

            if (DeviceTypeUtil.isContainWatch()) {
                device24HrPresenter.getMessageCallState(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
            } else if (DeviceTypeUtil.isContainBrand()) {
                success24HrSwitch(false);
            }

            boolean permission = FloatWindowParamManager.checkPermission(getActivity());
            if (permission && !RomUtils.isVivoRom()) {
                showCountDownPopWindow();
                //  Toast.makeText(MainActivity.this, R.string.has_float_permission, Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getApplicationContext(), FloatWindowService.class);
                intent.setAction(FloatWindowService.ACTION_CHECK_PERMISSION_AND_TRY_ADD);
                startService(intent);*/
            } else {
                // Toast.makeText(MainActivity.this, R.string.no_float_permission, Toast.LENGTH_SHORT).show();
                showOpenPermissionDialog();
            }
            //PowerUtil util = new PowerUtil();
            // util.goSetting(getActivity());
            // showCountDownPopWindow();
        }
    }

    private void showOpenPermissionDialog() {
        PublicAlertDialog.getInstance().showDialog(context.getResources().getString(R.string.no_float_permission), context.getResources().getString(R.string.go_t0_open_float_ask), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.app_bluetoothadapter_turnon), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivity(intent);
                FloatWindowParamManager.tryJumpToPermissionPage(getActivity());
            }

            @Override
            public void cancel() {
                showCountDownPopWindow();
            }
        }, false);
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, REQCODE_OPEN_BT);


    }

    private void initGPS() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // showSetGpsDialog();
        } else {
            // GPS状态监听
            // locationManager.addGpsStatusListener(statusListener);
            // GPS位置监听
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                        locationGpsListener);
                //getLocationAndMoveMap();
            }
        }
    }


    /**
     * location的监听。
     */
    private MLocationListener locationGpsListener = new MLocationListener() {

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location mlocation) {
            if (mlocation != null) {

                float accuracy = mlocation.getAccuracy();

                //updateGpsView(true);

                /*currentLocation = mlocation;
                LatLng latLng = mActPersenter
                        .gpsToBaidu(new LatLng(mlocation.getLatitude(), mlocation.getLongitude()));
                currentLocation.setLatitude(latLng.latitude);
                currentLocation.setLongitude(latLng.longitude);
                moveMap(latLng, 18);*/
            }
        }

    };


    public void updateValue() {
        if (AppConfiguration.sportLastDataBeanHashMap.containsKey(sportType)) {
            sportLastDataBean = AppConfiguration.sportLastDataBeanHashMap.get(sportType);
            tvLastDis.setText(CommonDateUtil.formatTwoPoint(Float.parseFloat(sportLastDataBean.getLastDistance())));
            /*StringFomateUtil.formatText(StringFomateUtil.FormatType.Alone, context, tvLastDis, ContextCompat.getColor
                    (context, R.color.common_white), R.string.dis_unit_number, CommonDateUtil.formatTwoPoint(Float.parseFloat(sportLastDataBean.getLastDistance())), "0.25");*/
            if (sportLastDataBean.getLastTimestamp() == 0) {
                tvDate.setText(UIUtils.getString(R.string.no_data));
            } else {
                tvDate.setText(TimeUtils.getTimeByMMDDHHMMSS
                        (sportLastDataBean.getLastTimestamp()));
            }
            tvAllDis.setText(CommonDateUtil.formatTwoPoint(Float.parseFloat(sportLastDataBean.getAllDistance())));
        }
    }


    public void setGpstaues(int lever) {
        //  Logger.myLog("Constants.mLocationLatitude setGpstaues" + lever);
        if (lever == 0) {
            ivGps.setImageResource(R.drawable.icon_gps_nor);
        } else if (lever == 1) {
            ivGps.setImageResource(R.drawable.icon_gps_one);
        } else if (lever == 2) {
            ivGps.setImageResource(R.drawable.icon_gps_two);
        } else if (lever == 3) {
            ivGps.setImageResource(R.drawable.icon_gps_three);
        }

    }

    Handler handler = new Handler();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.endHr:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setHrValue(0);
                    }
                }, 1000);

                break;
            case MessageEvent.update_location:
                //可在其中解析amapLocation获取相应内容。
                if (Constants.gpstype == AMapLocation.GPS_ACCURACY_UNKNOWN) {
                    setGpstaues(0);
                } else if (Constants.gpstype == AMapLocation.GPS_ACCURACY_BAD) {
                    setGpstaues(1);
                } else if (Constants.gpstype == AMapLocation.GPS_ACCURACY_GOOD) {
                    setGpstaues(3);
                }
                //setGpstaues(LocationHelp.getInstance().type);
                break;
        }
    }

    //监听心率值
    private BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {
            if (isConn) {
            } else {
                tvHrValue.setText(UIUtils.getString(R.string.no_data));
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
                        //InDoorService.theMomentRunData.paceBean.put(InDoorService.theMomentRunData.timer, bean);
                        WatchHrHeartResult mResult2 = (WatchHrHeartResult) mResult;
                        Log.e("setHrValue", "WATCH_REALTIME_HR setHrValue=" + mResult2.getHeartRate());
                        setHrValue(mResult2.getHeartRate());


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


    public void setHrValue(Integer hrValue) {

        Log.e("setHrValue", "setHrValue=" + hrValue);


        if (hrValue == 0) {
            tvHrValue.setText(UIUtils.getString(R.string.no_data));
        } else {
            if (hrValue > 30) {
                tvHrValue.setText(hrValue + "");
            }
        }
    }

    @Override
    public void success24HrSwitch(boolean isOpen) {


    }

    @Override
    public void successState(StateBean stateBean) {
        if (AppConfiguration.isConnected && AppConfiguration.hasSynced) {
            if (DeviceTypeUtil.isContainBrand() || DeviceTypeUtil.isContainWatchW556()) {
                ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, true);
            } else if (DeviceTypeUtil.isContainWatch()) {
                if (!stateBean.isHrState) {
                    stateBean.isHrState = true;
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, stateBean.isHrState, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
                }
            }
        }

    }

    @Override
    public void onRespondError(String message) {

    }


    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .init();
    }
}
