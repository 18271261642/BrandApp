package com.isport.brandapp.device.bracelet.braceletPresenter;

import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_RealTimeData;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.bracelet.braceletModel.IW311DataModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311DataModelImpl;
import com.isport.brandapp.device.bracelet.view.BraceletStepView;
import com.isport.brandapp.device.watch.watchModel.IW516Model;
import com.isport.brandapp.device.watch.watchModel.W516Model;
import com.isport.brandapp.util.DeviceTypeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.StringUtil;
import brandapp.isport.com.basicres.entry.WatchRealTimeData;
import brandapp.isport.com.basicres.entry.WatchTargetBean;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil;

public class BraceletStepPresenter extends BasePresenter<BraceletStepView> {//implements ArrayPickerView.ItemSelectedValue
    private BraceletStepView view;
    IW311DataModel iw311DataModel;
    IW516Model iw516Model;
    IW81DeviceDataModel iw81DeviceDataModel;

    public BraceletStepPresenter(BraceletStepView view) {
        this.view = view;
        iw311DataModel = new W311DataModelImpl();
        iw516Model = new W516Model();
        iw81DeviceDataModel = new W81DeviceDataModelImp();


    }

    /**
     * 获取有步数的天
     *
     * @param strDate
     */
    public void getMonthDataStrDate(String strDate, String deviceId, int currentType) {
        Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                ArrayList<String> strDates = new ArrayList<>();
                if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    strDates = iw311DataModel.getMonthData(strDate, deviceId);
                } else if (DeviceTypeUtil.isContainWatch(currentType)) {

                    strDates = iw516Model.getMonthData(strDate);
                } else if (DeviceTypeUtil.isContainW81(currentType)) {
                    strDates = iw81DeviceDataModel.getMonthData(strDate, deviceId);
                }
                // ArrayList<String> strDates = iw311DataModel.getMonthData(strDate, deviceId);
                emitter.onNext(strDates);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<ArrayList<String>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(ArrayList<String> list) {
                if (view != null) {
                    view.successMonthDate(list);
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


    int currentYear;
    int currantMonth, currentDay, currentWeek;
    int selectYear;
    int selectMonth, selectWeek, selectDay;
    boolean isCurrent = false;

    public void getBraceletMonthData(String userId, int data, String deviceId, int currentType) {
        Observable.create(new ObservableOnSubscribe<List<Wristbandstep>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Wristbandstep>> emitter) throws Exception {
                // AppConfiguration.watchID = "1";
                ArrayList<Wristbandstep> wristbandsteps = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                //需要判断是不是当前的月
                currentYear = calendar.get(Calendar.YEAR);
                currantMonth = calendar.get(Calendar.MONTH);
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.setTime(new Date(data * 1000l));
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                selectDay = calendar.get(Calendar.DAY_OF_MONTH);
                selectYear = calendar.get(Calendar.YEAR);
                selectMonth = calendar.get(Calendar.MONTH);
                if (currentYear == selectYear && currantMonth == selectMonth) {
                    isCurrent = true;
                } else {
                    isCurrent = false;
                }
                int monthCount = DateUtil.getMonthOfDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
                String strDate = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd");
                Logger.myLog("isCurrentDay" + strDate);
                Wristbandstep wristbandstep1 = null;

                //  wristbandstep1.setMothAndDay(DateUtil.dataToString(calendar.getTime(), "M/d"));
                //  wristbandsteps.add(wristbandstep1);
                for (int i = 0; i < monthCount; i++) {
                    if (i == 0) {
                        // calendar.add(Calendar.DAY_OF_MONTH, i - 1);
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    strDate = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd");
                    boolean isCurrentDay = false;
                    selectDay = calendar.get(Calendar.DAY_OF_MONTH);
                    if (currentYear == selectYear && currantMonth == selectMonth && currentDay == selectDay) {
                        isCurrentDay = true;
                    } else {
                        isCurrentDay = false;
                    }

                    Logger.myLog(strDate);
                    if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                        wristbandstep1 = iw311DataModel.getW311SportData(userId, strDate, deviceId);
                        if (isCurrentDay) {
                            Bracelet_W311_RealTimeData bracelet_w311_realTimeData = iw311DataModel.getRealTimeData(userId, deviceId);
                            if (bracelet_w311_realTimeData != null) {
                                wristbandstep1.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                                wristbandstep1.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                                wristbandstep1.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                            }
                        }
                    } else if (DeviceTypeUtil.isContainWatch(currentType)) {
                        wristbandstep1 = iw516Model.getWatchDayData(strDate);
                        if (isCurrentDay) {
                            WatchRealTimeData bracelet_w311_realTimeData = iw516Model.getRealWatchData(deviceId);
                            if (bracelet_w311_realTimeData != null) {
                                wristbandstep1.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                                wristbandstep1.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                                wristbandstep1.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                            }
                        }
                    } else if (DeviceTypeUtil.isContainW81(currentType) || DeviceTypeUtil.isContainF18(currentType)) {
                        wristbandstep1 = iw81DeviceDataModel.getStepData(String.valueOf(userId), deviceId, strDate);
                    }
                    if (wristbandstep1 == null) {
                        wristbandstep1 = new Wristbandstep();
                    }
                    if (isCurrent) {
                        wristbandstep1.setDays(currentDay);
                    } else {
                        wristbandstep1.setDays(monthCount);
                    }
                    wristbandstep1.setMothAndDay(DateUtil.dataToString(calendar.getTime(), "M/d"));
                    wristbandsteps.add(wristbandstep1);
                }
                emitter.onNext(wristbandsteps);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<Wristbandstep>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(List<Wristbandstep> wristbandstep) {
                if (view != null) {
                    view.successWeekBraceletSportDetail(wristbandstep);
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
                if (view != null) {
                    view.successLastSportsummary(null);
                }

            }

        });
    }


    public void getBraceletWeekData(String userid, int data, String deviceid, int currentType) {
        Observable.create(new ObservableOnSubscribe<List<Wristbandstep>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Wristbandstep>> emitter) throws Exception {
                //AppConfiguration.watchID = "1";
                ArrayList<Wristbandstep> wristbandsteps = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                currentYear = calendar.get(Calendar.YEAR);
                currantMonth = calendar.get(Calendar.MONTH);
                currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.setTime(new Date(data * 1000l));
                selectDay = calendar.get(Calendar.DAY_OF_MONTH);
                //calendar.set(Calendar.DAY_OF_MONTH, 1);
                selectYear = calendar.get(Calendar.YEAR);
                selectMonth = calendar.get(Calendar.MONTH);
                selectWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                String strDate = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd");
                boolean isWeek = false;

                Logger.myLog(strDate);
                Wristbandstep wristbandstep1 = null;
                for (int i = 0; i < 7; i++) {
                    if (i != 0) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    selectDay = calendar.get(Calendar.DAY_OF_MONTH);
                    if (currentYear == selectYear && currantMonth == currantMonth && selectWeek == currentWeek) {
                        isWeek = true;
                    } else {
                        isWeek = false;
                    }
                    strDate = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd");
                    Logger.myLog(strDate);
                    if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                        wristbandstep1 = iw311DataModel.getW311SportData(userid, strDate, deviceid);
                        if (isWeek) {
                            Bracelet_W311_RealTimeData bracelet_w311_realTimeData = iw311DataModel.getRealTimeData(userid, deviceid);
                            if (bracelet_w311_realTimeData != null) {
                                if (bracelet_w311_realTimeData.getDate().equals(strDate)) {
                                    wristbandstep1.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                                    wristbandstep1.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                                    wristbandstep1.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                                }
                            }
                        }
                    } else if (DeviceTypeUtil.isContainWatch(currentType)) {
                        wristbandstep1 = iw516Model.getWatchDayData(strDate);
                        // Logger.myLog("getBraceletWeekData:"+wristbandstep1.getStepKm()+",CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm())"+CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                        if (isWeek) {
                            WatchRealTimeData bracelet_w311_realTimeData = iw516Model.getRealWatchData(deviceid);
                            if (bracelet_w311_realTimeData != null) {
                                if (bracelet_w311_realTimeData.getDate().equals(strDate)) {
                                    wristbandstep1.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                                    Logger.myLog("strDate=" + strDate + "getBraceletWeekData:" + bracelet_w311_realTimeData.getStepKm() + ",CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm())" + CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                                    wristbandstep1.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                                    wristbandstep1.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                                }
                            }
                        }

                    } else if (DeviceTypeUtil.isContainW81(currentType) || DeviceTypeUtil.isContainF18(currentType)) {
                        wristbandstep1 = iw81DeviceDataModel.getStepData(String.valueOf(userid), deviceid, strDate);
                    }
                    if (wristbandstep1 == null) {
                        wristbandstep1 = new Wristbandstep();
                    }
                    if (currentYear == selectYear && currantMonth == currantMonth && currentWeek == selectWeek) {
                        isCurrent = true;
                        int weekByDateReturnInt = TimeUtils.getWeekByData(Calendar.getInstance());
                        Logger.myLog("weekByDateReturnInt" + weekByDateReturnInt);
                        wristbandstep1.setDays(weekByDateReturnInt);
                    } else {
                        isCurrent = false;
                        wristbandstep1.setDays(7);
                    }
                    //wristbandstep1 = iw311DataModel.getW311SportData(userid, strDate, deviceid);
                    wristbandstep1.setMothAndDay(DateUtil.dataToString(calendar.getTime(), "M/d"));
                    wristbandsteps.add(wristbandstep1);
                }
                emitter.onNext(wristbandsteps);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<List<Wristbandstep>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(List<Wristbandstep> wristbandstep) {
                if (view != null) {
                    view.successWeekBraceletSportDetail(wristbandstep);
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
                if (view != null) {
                    view.successLastSportsummary(null);
                }

            }

        });

    }

    public void getBraceletDayData(String userid, String strDate, String deviceId, int currentType, int data) {
        Observable.create(new ObservableOnSubscribe<Wristbandstep>() {
            @Override
            public void subscribe(ObservableEmitter<Wristbandstep> emitter) throws Exception {
                Wristbandstep wristbandstep = null;
                boolean isCurrentDay = true;
                Calendar calendar = Calendar.getInstance();
                currentYear = calendar.get(Calendar.YEAR);
                currantMonth = calendar.get(Calendar.MONTH);
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.setTime(new Date(data * 1000l));
                //calendar.set(Calendar.DAY_OF_MONTH, 1);
                selectYear = calendar.get(Calendar.YEAR);
                selectMonth = calendar.get(Calendar.MONTH);
                selectDay = calendar.get(Calendar.DAY_OF_MONTH);

                if (currentYear == selectYear && currantMonth == selectMonth && currentDay == selectDay) {
                    isCurrentDay = true;
                } else {
                    isCurrentDay = false;
                }
                Logger.myLog("currentYear:" + currentYear + ",selectYear:" + selectYear + ",currantMonth:" + currantMonth + ",selectMonth：" + selectMonth + ",currentDay：" + currentDay + ",selectDay：" + selectDay + ",isCurrentDay:" + isCurrentDay + ",strDate:" + strDate);

                if (DeviceTypeUtil.isContainWrishBrand(currentType)) {
                    wristbandstep = iw311DataModel.getW311SportData(userid, strDate, deviceId);
                    if (isCurrentDay) {
                        Bracelet_W311_RealTimeData bracelet_w311_realTimeData = iw311DataModel.getRealTimeData(userid, deviceId);
                        if (bracelet_w311_realTimeData != null) {
                            wristbandstep.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                            wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                            wristbandstep.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));

                        }
                        wristbandstep.setMothAndDay(strDate);
                    }

                } else if (DeviceTypeUtil.isContainWatch(currentType)) {


                    wristbandstep = iw516Model.getWatchDayData(strDate);

                    Logger.myLog("currentType:" + currentType + "wristbandstep:" + wristbandstep + "isCurrent:" + isCurrent + "strDate:" + strDate);

                    if (isCurrentDay) {
                        WatchRealTimeData bracelet_w311_realTimeData = iw516Model.getRealWatchData(deviceId);
                        Logger.myLog("currentYear:" + currentYear + ",bracelet_w311_realTimeData:" + bracelet_w311_realTimeData + "deviceId:" + deviceId);

                        if (bracelet_w311_realTimeData != null) {
                            wristbandstep.setStepNum(bracelet_w311_realTimeData.getStepNum() + "");
                            wristbandstep.setStepKm(CommonDateUtil.formatTwoPoint(bracelet_w311_realTimeData.getStepKm()));
                            wristbandstep.setCalorie(CommonDateUtil.formatInterger(bracelet_w311_realTimeData.getCal()));
                        }
                        wristbandstep.setMothAndDay(strDate);
                    }
                } else if (DeviceTypeUtil.isContainW81(currentType) || DeviceTypeUtil.isContainF18(currentType)) {
                    wristbandstep = iw81DeviceDataModel.getStepData(String.valueOf(userid), deviceId, strDate);
                } else {
                    wristbandstep = new Wristbandstep();
                }

                emitter.onNext(wristbandstep);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Wristbandstep>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Wristbandstep wristbandstep) {
                if (view != null) {
                    view.successLastSportsummary(wristbandstep);
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

                if (view != null) {
                    view.successLastSportsummary(null);
                }

            }

        });

    }

    public void getWatchTargetSteps(String deviceId, int userId) {
        Observable.create(new ObservableOnSubscribe<WatchTargetBean>() {
            @Override
            public void subscribe(ObservableEmitter<WatchTargetBean> emitter) throws Exception {
                WatchTargetBean wristbandstep = new WatchTargetBean();
                if (wristbandstep == null) {
                    wristbandstep = new WatchTargetBean();
                }
                emitter.onNext(wristbandstep);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new Consumer<WatchTargetBean>() {
            @Override
            public void accept(WatchTargetBean wristbandstep) throws Exception {
                NetProgressObservable.getInstance().hide();
                if (view != null) {
                    view.successTargetStep(wristbandstep);
                }
            }
        });

    }

    //获取近30天的数据
    public void getLastMonthData(String deviceId) {
        Observable.create(new ObservableOnSubscribe<Wristbandstep>() {
            @Override
            public void subscribe(ObservableEmitter<Wristbandstep> emitter) throws Exception {
                Wristbandstep wristbandstep = new Wristbandstep();
                emitter.onNext(wristbandstep);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new Consumer<Wristbandstep>() {
            @Override
            public void accept(Wristbandstep wristbandstep) throws Exception {
                NetProgressObservable.getInstance().hide();
                if (view != null) {
                    view.succcessLastMontData(wristbandstep.avgStep, wristbandstep.avgDis, wristbandstep.sumGasoline, wristbandstep.sumFat);
                }
            }
        });
    }


    public void getLastSpprtData() {

        Observable.create(new ObservableOnSubscribe<Wristbandstep>() {
            @Override
            public void subscribe(ObservableEmitter<Wristbandstep> emitter) throws Exception {
                Wristbandstep wristbandstep = new Wristbandstep();
                emitter.onNext(wristbandstep);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new Consumer<Wristbandstep>() {
            @Override
            public void accept(Wristbandstep wristbandstep) throws Exception {
                NetProgressObservable.getInstance().hide();
                if (view != null) {
                    if (wristbandstep == null) {
                        view.successLastSportsummary(null);
                    } else {
                        //未连接进入时，展示历史数据,连接状态下展示实时数据
                        if (StringUtil.isBlank(wristbandstep.getStrDate()) || (AppConfiguration.isConnected && TimeUtils.isToday(wristbandstep.getStrDate()))) {
                            view.successLastSportsummary(null);
                        } else {
                            view.successLastSportsummary(wristbandstep);
                        }
                    }
                }
            }
        });
    }


    public void calStepToCalAndKM(long currentStep) {
        Wristbandstep wristbandstep = iw516Model.calStepToKmAndCal(currentStep);
        if (view != null) {
            view.successLastSportsummary(wristbandstep);
        }
    }

//
//    private View mMenuView;
//    private String localData[];
//    private String localDataNoUnit[];
//    private String stepData[] = new String[101]; // 步数
//    private String stepDataNoUnit[] = new String[101];
//    private PopupWindow popupWindow;
//    private String localChooseStr;
//    private String selectType;
//    private int showIndex;
//
//    /**
//     * @param context
//     * @param view
//     * @param type
//     * @param lastData 上次的数据
//     */
//    public void popWindowSelect(Context context, View view, String type, String lastData, boolean isCyclic) {
//        this.selectType = type;
//        switch (type) {
//
//            case JkConfiguration.GymUserInfo.STEP:
//                for (int n = 1; n <= 5; n++) {
//                    int stepTemp=2000*n;
//                    stepDataNoUnit[n] = String.valueOf(stepTemp);
//                    stepData[n] = stepTemp + " 步";
//                    if (stepData[n].equals(lastData)) {
//                        showIndex = n;
//                    }
//                }
//                localData = stepData;
//                localDataNoUnit = stepDataNoUnit;
//                break;
//        }
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.app_pop_bottom_setting, null);
//        TextView tv_determine = (TextView) mMenuView.findViewById(R.id.tv_determine);
//        TextView tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
//        ArrayPickerView datePicker = (ArrayPickerView) mMenuView.findViewById(R.id.datePicker);
//
//        datePicker.setData(localData);
//        datePicker.setCyclic(isCyclic);
//        datePicker.setItemOnclick(this);
//        datePicker.setSelectItem(showIndex);
//        localChooseStr = localData[showIndex];
//        popupWindow = new PopupWindow(context);
//        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setContentView(mMenuView);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        popupWindow.setOutsideTouchable(false);
//        popupWindow.setFocusable(true);
//        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
//        popupWindow.showAtLocation(view, Gravity.BOTTOM
//                | Gravity.CENTER_HORIZONTAL, 0, 0);
//        mMenuView.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        popupWindow.dismiss();
//                    }
//                }
//                return true;
//            }
//        });
//
//        tv_determine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isViewAttached()) {
//                    mActView.get().dataSetSuccess(selectType, localChooseStr);
//                }
//                Logger.e("tag", localChooseStr);
//                popupWindow.dismiss();
//            }
//        });
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
//    }
//
//    @Override
//    public void onItemSelectedValue(String str) {
//        localChooseStr = str;
//    }


    public Wristbandstep getTestData() {
        Wristbandstep wristbandstep = new Wristbandstep();
        wristbandstep.setStepKm("23");
        wristbandstep.setCalorie("25");
        wristbandstep.setStepNum("220");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 24; i++) {
            arrayList.add(i + 200);
        }
        wristbandstep.setStepArry(arrayList);
        return wristbandstep;
    }


}
