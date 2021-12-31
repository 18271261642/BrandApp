package com.isport.brandapp.device.watch.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_24HDataModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_24HDataModelAction;
import com.isport.blelibrary.db.action.watch_w516.Watch_W516_SedentaryModelAction;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_24HDataModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;
import com.isport.blelibrary.gen.Watch_W516_24HDataModelDao;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.bean.BindInsertOrUpdateBean;
import com.isport.brandapp.device.UpdateSuccessBean;
import com.isport.brandapp.device.bracelet.braceletModel.IW311SettingModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311ModelSettingImpl;
import com.isport.brandapp.device.bracelet.playW311.bean.PlayBean;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.device.watch.view.WatchView;
import com.isport.brandapp.device.watch.watchModel.W516Model;
import com.isport.brandapp.parm.db.DeviceTypeParms;
import com.isport.brandapp.repository.PlayBandRepository;
import com.isport.brandapp.repository.UpdateResposition;
import com.isport.brandapp.repository.WatchRepository;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bike.gymproject.viewlibray.pickerview.ArrayPickerView;
import bike.gymproject.viewlibray.pickerview.DatePickerView;
import bike.gymproject.viewlibray.pickerview.DyncArrayPickerView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.action.BaseAction;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class WatchPresenter extends BasePresenter<WatchView> implements ArrayPickerView.ItemSelectedValue {
    private WatchView view;
    private final W516Model iw516Model;
    private IW311SettingModel iw311SettingModel;


    public WatchPresenter(WatchView view) {
        this.view = view;
        iw516Model = new W516Model();
        iw311SettingModel = new W311ModelSettingImpl();
    }

    private String localData[];
    private String localDataNoUnit[];
    private String stepData[] = new String[10]; // 步数
    private String stepDataNoUnit[] = new String[10];
    private String distanceData[] = new String[10]; // 距离
    private String distanceDataNoUnit[] = new String[10];
    private String calorieData[] = new String[10]; // 卡路里
    private String calorieDataNoUnit[] = new String[10];

    private String wearData[] = new String[2];

    private String lift[] = new String[3];

    private String nohrTimes[] = new String[5];
    private String min[] = new String[59];
    private String hrTimes[] = new String[5];
    private String noMin[] = new String[59];
    private String now311Times[] = new String[4];
    private String w311Times[] = new String[4];


    private String localChooseStr;
    private String selectType;
    private int showIndex;
    private int mCurrentWatchIndex;
    private List<Watch_W516_24HDataModel> mWatch_W516_24HDataModel;

    /**
     * @param context
     * @param view
     * @param type
     * @param lastData 上次的数据
     */
    public void popWindowSelect(Context context, View view, String type, String lastData, boolean isCyclic) {


        this.selectType = type;
        this.targetView = view;
        showIndex = 0;
        switch (type) {

            case JkConfiguration.GymUserInfo.w311_long_time_reminder:
                ArrayList<String> timsReminder = new ArrayList();
                timsReminder.add("15");
                timsReminder.add("30");
                timsReminder.add("45");
                timsReminder.add("60");
                for (int n = 0; n < timsReminder.size(); n++) {
                    String stepTemp = timsReminder.get(n);
                    now311Times[n] = stepTemp;
                    w311Times[n] = stepTemp + " " + UIUtils.getString(R.string.unit_minute);
                    if (w311Times[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = w311Times;
                localDataNoUnit = now311Times;
                break;


            case JkConfiguration.GymUserInfo.HR_TIMES:
                ArrayList<String> tims = new ArrayList();
                tims.add("5");
                tims.add("10");
                tims.add("20");
                tims.add("30");
                tims.add("60");
                for (int n = 0; n < tims.size(); n++) {
                    String stepTemp = tims.get(n);
                    nohrTimes[n] = stepTemp;
                    hrTimes[n] = stepTemp + " " + UIUtils.getString(R.string.unit_minute);
                    if (nohrTimes[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = hrTimes;
                localDataNoUnit = nohrTimes;
                break;

            case JkConfiguration.GymUserInfo.temperature_unitl:
                ArrayList<String> arrayTemperatureUnitl = new ArrayList();
                arrayTemperatureUnitl.add(UIUtils.getString(R.string.temperature_degree_centigrade));
                arrayTemperatureUnitl.add(UIUtils.getString(R.string.temperature_fahrenheit));
                for (int n = 0; n < arrayTemperatureUnitl.size(); n++) {
                    wearData[n] = arrayTemperatureUnitl.get(n);
                    if (wearData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = wearData;

                break;
            case JkConfiguration.GymUserInfo.TIME_MIN:
                ArrayList<String> arrayMin = new ArrayList();
                for (int n = 0; n < 59; n++) {
                    String stepTemp = (n + 1) + "";
                    noMin[n] = stepTemp;
                    min[n] = stepTemp + " " + UIUtils.getString(R.string.unit_minute);
                    if (noMin[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = min;
                localDataNoUnit = noMin;
                break;
            case JkConfiguration.GymUserInfo.TIME_FORMATE:

                ArrayList<String> arrayTimeFormate = new ArrayList();
                arrayTimeFormate.add(UIUtils.getString(R.string.time_format_12));
                arrayTimeFormate.add(UIUtils.getString(R.string.time_format_24));
                for (int n = 0; n < arrayTimeFormate.size(); n++) {
                    wearData[n] = arrayTimeFormate.get(n);
                    if (wearData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = wearData;

                break;


            case JkConfiguration.GymUserInfo.BRACELET_WEAR:
                ArrayList<String> arrayListwear = new ArrayList();
                arrayListwear.add(UIUtils.getString(R.string.wear_left));
                arrayListwear.add(UIUtils.getString(R.string.wear_right));
                for (int n = 0; n < arrayListwear.size(); n++) {
                    wearData[n] = arrayListwear.get(n);
                    if (wearData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = wearData;
                // localDataNoUnit = stepDataNoUnit;
                break;

            case JkConfiguration.GymUserInfo.LIFT_BRACELET_TO_VIEW_INFO:
                ArrayList<String> arrayList = new ArrayList();
                arrayList.add(UIUtils.getString(R.string.lift_to_view_info_all_day));
                arrayList.add(UIUtils.getString(R.string.lift_to_view_info_Timing_open));
                arrayList.add(UIUtils.getString(R.string.lift_to_view_info_close));
                for (int n = 0; n < arrayList.size(); n++) {
                    // stepDataNoUnit[n] = arrayList.get(n);
                    lift[n] = arrayList.get(n);
                    if (lift[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = lift;
                //localDataNoUnit = stepDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.LIFT_BRACELET_TO_VIEW_INFO_307J:
                ArrayList<String> arrayList307J = new ArrayList();
                arrayList307J.add(UIUtils.getString(R.string.lift_to_view_info_all_day));
                arrayList307J.add(UIUtils.getString(R.string.lift_to_view_info_Timing_open_307j));
                arrayList307J.add(UIUtils.getString(R.string.lift_to_view_info_close));
                for (int n = 0; n < arrayList307J.size(); n++) {
                    // stepDataNoUnit[n] = arrayList.get(n);
                    lift[n] = arrayList307J.get(n);
                    if (lift[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = lift;
                //localDataNoUnit = stepDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.STEP:
                stepDataNoUnit = new String[10];
                stepData = new String[10];
                for (int n = 0; n < 10; n++) {
                    int stepTemp = 2000 * (n + 1);
                    stepDataNoUnit[n] = String.valueOf(stepTemp);
                    stepData[n] = stepTemp + " " + UIUtils.getString(R.string.unit_steps);
                    if (stepData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = stepData;
                localDataNoUnit = stepDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.BACKLIGHT_TIME:

                stepDataNoUnit = new String[8];
                stepData = new String[8];

                for (int n = 0; n < stepDataNoUnit.length; n++) {
                    int stepTemp = (n + 3);
                    stepDataNoUnit[n] = String.valueOf(stepTemp);
                    stepData[n] = stepTemp + " " + UIUtils.getString(R.string.unit_backlight_time);
                    if (stepData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = stepData;
                localDataNoUnit = stepDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.SCREEN_LUMINANCE:

                stepDataNoUnit = new String[5];
                stepData = new String[5];
                for (int n = 0; n < stepDataNoUnit.length; n++) {
                    int stepTemp = (n + 1);
                    stepDataNoUnit[n] = String.valueOf(stepTemp);
                    stepData[n] = stepTemp + " " + UIUtils.getString(R.string.unit_screen_luminance);
                    if (stepData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = stepData;
                localDataNoUnit = stepDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.STEP_W311:
                /**
                 * 默认6000步，选择范围：1000到30000步，每个选项增加1000
                 */
                stepDataNoUnit = new String[30];
                stepData = new String[30];
                for (int n = 0; n < stepDataNoUnit.length; n++) {
                    int stepTemp = 1000 * (n + 1);
                    stepDataNoUnit[n] = String.valueOf(stepTemp);
                    stepData[n] = stepTemp + " " + UIUtils.getString(R.string.unit_steps);
                    if (stepData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = stepData;
                localDataNoUnit = stepDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.DISTANCE_W560:
                /**
                 * W560选择距离
                 * 默认1000米，选择范围：1000到30000米，每个选项增加1000
                 */
                distanceDataNoUnit = new String[30];
                distanceData = new String[30];
                for (int n = 0; n < distanceDataNoUnit.length; n++) {
                    int distanceTemp = 1000 * (n + 1);
                    distanceDataNoUnit[n] = String.valueOf(distanceTemp);
                    distanceData[n] = distanceTemp + " " + UIUtils.getString(R.string.unit_meters);
                    if (distanceData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = distanceData;
                localDataNoUnit = distanceDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.CALORIE_W3560:
                /**
                 * W560选择卡路里
                 * 默认10千卡，选择范围：10到1000千卡，每个选项增加10
                 */
                calorieDataNoUnit = new String[500];
                calorieData = new String[500];
                for (int n = 0; n < calorieDataNoUnit.length; n++) {
                    int calorieTemp = 10 * (n + 1);
                    calorieDataNoUnit[n] = String.valueOf(calorieTemp);
                    calorieData[n] = calorieTemp + " " + UIUtils.getString(R.string.unit_kcal);
                    if (calorieData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = calorieData;
                localDataNoUnit = calorieDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.REMIND_TIME:
                stepDataNoUnit = new String[170];
                stepData = new String[170];
                for (int n = 0; n < 170; n++) {
                    int stepTemp = n + 30;
                    stepDataNoUnit[n] = String.valueOf(stepTemp);
                    stepData[n] = stepTemp + " " + UIUtils.getString(R.string.unit_minute);
                    if (stepData[n].equals(lastData)) {
                        showIndex = n;
                    }
                }
                localData = stepData;
                localDataNoUnit = stepDataNoUnit;
                break;
        }
        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_bottom_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(true)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                })
                .setOnClickListener(R.id.tv_determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isViewAttached()) {
                            mActView.get().dataSetSuccess(targetView, selectType, localChooseStr);
                        }
                        dialogInterface.cancel();
                    }
                })
                .show();


        ArrayPickerView datePicker = (ArrayPickerView) mMenuViewBirth.findViewById(R.id.datePicker);

        datePicker.setData(localData);
        datePicker.setCyclic(isCyclic);
        datePicker.setItemOnclick(this);
        datePicker.setSelectItem(showIndex);
        localChooseStr = localData[showIndex];

    }

    @Override
    public void onItemSelectedValue(String str) {
        localChooseStr = str;
    }


    // 时间选择器
    private View targetView;

    public void setPopupWindow(Context context, View view, final String type, String defaultDay) {
        this.targetView = view;


        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_date)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })

                .show();


        TextView tv_determine = (TextView) mMenuViewBirth.findViewById(R.id.tv_determine);
        final DatePickerView datePicker = (DatePickerView) mMenuViewBirth.findViewById(R.id.datePicker);
        if (type.equals("3")) {
            datePicker.setType(3);
        } else if (type.equals("4")) {
            datePicker.setType(4);
        }
        datePicker.setDefaultItemAdapter(defaultDay);
        datePicker.setCyclic(false);

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calculationAgeAndConstellation(datePicker.getTime());
//                localUserChooseBirthday = datePicker.getTime();
                if(ViewMultiClickUtil.onMultiClick()){
                    return;
                }
                if (isViewAttached()) {
                    mActView.get().dataSetSuccess(targetView, type, datePicker.getTime());
                }
                if (mMenuViewBirth != null && mMenuViewBirth.isShowing()) {
                    mMenuViewBirth.dismiss();
                }
            }
        });
    }

    public void setPopupWindowStartHour(Context context, View view, final String type, String defaultDay, int startHour, int endHour) {
        this.targetView = view;


        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_dync_date)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })

                .show();


        TextView tv_determine = (TextView) mMenuViewBirth.findViewById(R.id.tv_determine);
        TextView tv_cancel = (TextView) mMenuViewBirth.findViewById(R.id.tv_cancel);
        final DyncArrayPickerView datePicker = (DyncArrayPickerView) mMenuViewBirth.findViewById(R.id.datePicker);
        datePicker.setCyclic(false);
        datePicker.setData(defaultDay, startHour, endHour, 0, 59);

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calculationAgeAndConstellation(datePicker.getTime());
//                localUserChooseBirthday = datePicker.getTime();
                if (isViewAttached()) {
                    mActView.get().dataSetSuccess(targetView, type, datePicker.getTime());
                }
                mMenuViewBirth.dismiss();
            }
        });
    }


    /**
     * 获取最近一次有数据的WatchSleepDayData
     */
    public void getWatchLastSleepData(String deviceId) {
        com.isport.blelibrary.utils.Logger.myLog("getWatchLastData");
        Observable.create(new ObservableOnSubscribe<WatchSleepDayData>() {
            @Override
            public void subscribe(ObservableEmitter<WatchSleepDayData> emitter) throws Exception {
                WatchSleepDayData watchSleepDayData = iw516Model.getWatchSleepDayLastData(deviceId);
                emitter.onNext(watchSleepDayData);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new Consumer<WatchSleepDayData>() {
            @Override
            public void accept(WatchSleepDayData watchSleepDayData) throws Exception {
                NetProgressObservable.getInstance().hide();
                if (isViewAttached()) {
                    mActView.get().successDayDate(watchSleepDayData);
                }
            }
        });
    }


    /**
     * 闹钟选择
     *
     * @param context
     * @param view
     * @param type
     */
    CheckBox cb_sun, cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat;


    ImageView iv_sum, iv_mon, iv_tue, iv_web, iv_thu, iv_fri, iv_sat;


    public void setRepeatPopupWindow(Context context, View view, final String type, int repeat) {
        targetView = view;
        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_week)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setOnClickListener(R.id.layout_sum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogInterface.cancel();
                        cb_sun.setChecked(!cb_sun.isChecked());
                        showImgView(iv_sum, cb_sun.isChecked());
                    }
                })
                .setOnClickListener(R.id.layout_mon, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean bFri = cb_mon.isChecked();
                        cb_mon.setChecked(!bFri);
                        showImgView(iv_mon, cb_mon.isChecked());
                    }
                })
                .setOnClickListener(R.id.layout_tue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean bFri = cb_tue.isChecked();
                        cb_tue.setChecked(!bFri);
                        showImgView(iv_tue, cb_tue.isChecked());
                    }
                })
                .setOnClickListener(R.id.layout_wed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean bFri = cb_wed.isChecked();
                        cb_wed.setChecked(!bFri);
                        showImgView(iv_web, cb_wed.isChecked());
                    }
                })
                .setOnClickListener(R.id.layout_thu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean bFri = cb_thu.isChecked();
                        cb_thu.setChecked(!bFri);
                        showImgView(iv_thu, cb_thu.isChecked());
                    }
                })
                .setOnClickListener(R.id.layout_fri, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean bFri = cb_fri.isChecked();
                        cb_fri.setChecked(!bFri);
                        showImgView(iv_fri, cb_fri.isChecked());
                    }
                })
                .setOnClickListener(R.id.layout_sat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean bFri = cb_sat.isChecked();
                        cb_sat.setChecked(!bFri);
                        showImgView(iv_sat, cb_sat.isChecked());
                    }
                })
                .show();
        TextView tv_determine = (TextView) mMenuViewBirth.findViewById(R.id.tv_determine);
        cb_sun = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_sun);
        cb_mon = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_mon);
        cb_tue = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_tue);
        cb_wed = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_wed);
        cb_thu = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_thu);
        cb_fri = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_fri);
        cb_sat = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_sat);


        iv_sum = mMenuViewBirth.findViewById(R.id.iv_sun);
        iv_mon = mMenuViewBirth.findViewById(R.id.iv_mon);
        iv_tue = mMenuViewBirth.findViewById(R.id.iv_tue);
        iv_web = mMenuViewBirth.findViewById(R.id.iv_wed);
        iv_thu = mMenuViewBirth.findViewById(R.id.iv_thu);
        iv_fri = mMenuViewBirth.findViewById(R.id.iv_fri);
        iv_sat = mMenuViewBirth.findViewById(R.id.iv_sat);


        byte[] booleanArrayG = Utils.getBooleanArray((byte) repeat);
        cb_sun.setChecked(false);
        cb_mon.setChecked(false);
        cb_tue.setChecked(false);
        cb_wed.setChecked(false);
        cb_thu.setChecked(false);
        cb_fri.setChecked(false);
        cb_sat.setChecked(false);
        com.isport.blelibrary.utils.Logger.myLog("repeat == " + repeat);
        final StringBuilder stringBuilder = new StringBuilder(booleanArrayG.length);
        for (byte byteChar : booleanArrayG) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }
        com.isport.blelibrary.utils.Logger.myLog(" booleanArrayG " + stringBuilder.toString());
        if (Utils.byte2Int(booleanArrayG[7]) == 1) {
            cb_sun.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[6]) == 1) {
            cb_mon.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[5]) == 1) {
            cb_tue.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[4]) == 1) {
            cb_wed.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[3]) == 1) {
            cb_thu.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[2]) == 1) {
            cb_fri.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[1]) == 1) {
            cb_sat.setChecked(true);
        }
        showImgView(iv_sum, cb_sun.isChecked());
        showImgView(iv_mon, cb_mon.isChecked());
        showImgView(iv_tue, cb_tue.isChecked());
        showImgView(iv_web, cb_wed.isChecked());
        showImgView(iv_thu, cb_thu.isChecked());
        showImgView(iv_fri, cb_fri.isChecked());
        showImgView(iv_sat, cb_sat.isChecked());
        //展示之前的选择

        /*popupWindowBirth = new PopupWindow(context);
        popupWindowBirth.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowBirth.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindowBirth.setHeight(DisplayUtils.dip2px(context, 150));
        popupWindowBirth.setContentView(mMenuViewBirth);
        popupWindowBirth.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindowBirth.setOutsideTouchable(false);
        popupWindowBirth.setFocusable(true);
        popupWindowBirth.setAnimationStyle(R.style.popwin_anim_style);
        popupWindowBirth.showAtLocation(view, Gravity.CENTER
                | Gravity.CENTER_HORIZONTAL, 0, 0);
//        mMenuViewBirth.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                int height = mMenuViewBirth.findViewById(R.id.pop_layout).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        popupWindowBirth.dismiss();
//                    }
//                }
//                return true;
//            }
//        });*/

/*        layout_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bFri = cb_fri.isChecked();
                cb_fri.setChecked(!bFri);
                showImgView(iv_fri, cb_fri.isChecked());
                // cb_fri.setChecked(!bFri);

            }
        });
        layout_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cb_mon.setChecked(!cb_mon.isChecked());
                boolean bFri = cb_mon.isChecked();
                cb_mon.setChecked(!bFri);
                showImgView(iv_mon, cb_mon.isChecked());

            }
        });
        layout_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_sat.setChecked(!cb_sat.isChecked());
                showImgView(iv_sat, cb_sat.isChecked());
            }
        });
        layout_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("layout_sum", "layout_sum= onlick");
                cb_sun.setChecked(!cb_sun.isChecked());
                showImgView(iv_sum, cb_sun.isChecked());
            }
        });
        layout_thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_thu.setChecked(!cb_thu.isChecked());
                showImgView(iv_thu, cb_thu.isChecked());
            }
        });

        layout_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_tue.setChecked(!cb_tue.isChecked());
                showImgView(iv_tue, cb_tue.isChecked());
            }
        });

        layout_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_wed.setChecked(!cb_wed.isChecked());
                showImgView(iv_web, cb_wed.isChecked());
            }
        });*/

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calculationAgeAndConstellation(datePicker.getTime());
//                localUserChooseBirthday = datePicker.getTime();
                if (isViewAttached()) {
                    //确定选择，直接返回要发送的指令

                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append("0");
                    if (cb_sat.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }
                    if (cb_fri.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }
                    if (cb_thu.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }
                    if (cb_wed.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }
                    if (cb_tue.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }
                    if (cb_mon.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }
                    if (cb_sun.isChecked()) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append("0");
                    }

                    int value = Integer.valueOf(stringBuilder.toString(), 2);

                    StringBuilder stringBuilderWeek = new StringBuilder();
                    if (cb_mon.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.mon) + ",");
                    }
                    if (cb_tue.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.tue) + ",");
                    }

                    if (cb_wed.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.wed) + ",");
                    }

                    if (cb_thu.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.thu) + ",");
                    }

                    if (cb_fri.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.fri) + ",");
                    }

                    if (cb_sat.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.sat) + ",");
                    }

                    if (cb_sun.isChecked()) {
                        stringBuilderWeek.append(UIUtils.getString(R.string.sun) + ",");
                    }

                    String weekStr = stringBuilderWeek.toString();

                    if (weekStr.endsWith(",")) {
                        weekStr = weekStr.substring(0, weekStr.length() - 1);
                    }

                    mActView.get().dataSetSuccess(targetView, type, value + "-" + weekStr);
                }
                mMenuViewBirth.dismiss();
            }
        });
    }


    public void showImgView(final ImageView ivImage, boolean isShow) {

        Log.e("showImgView", "ivImage=" + ivImage + "isShow=" + isShow);
        // ivImage.setImageResource(R.drawable.sel_week_yes);
        /*if (isShow) {

            ivImage.setImageResource(R.drawable.sel_week_yes);
        } else {
            ivImage.setImageResource(R.drawable.sel_week_nor);
        }*/
        ivImage.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }


    public void deletCurrentDay(String deviceId, int currentType) {
        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (currentType == JkConfiguration.DeviceType.WATCH_W516) {
                    Watch_W516_24HDataModelAction.delCurretentDay(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
                } else if (currentType == JkConfiguration.DeviceType.BRAND_W311) {
                    Bracelet_W311_24HDataModelAction.delCurretentDay(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), DateUtil.dataToString(new Date(), "yyyy-MM-dd"), deviceId);
                }
            }
        });
    }

    /**
     * 解绑操作
     *
     * @param deviceBean
     */
    public void unBind(DeviceBean deviceBean, boolean isDirctUnbind) {
        SyncCacheUtils.clearSetting(BaseApp.getApp());
        SyncCacheUtils.clearStartSync(BaseApp.getApp());
        SyncCacheUtils.clearSysData(BaseApp.getApp());
        if (isDirctUnbind) {
            deletCurrentDay(deviceBean.deviceName, deviceBean.currentType);
        }
        //scanModel.unBind(userId, deviceId, deviceType);
        //如果是isDirctUnbind 需要把当天不完整的数据给删除了，需要把当天的数据删除

        UpdateResposition<Integer, BindInsertOrUpdateBean, BaseUrl, DeviceTypeParms> customRepository = new
                UpdateResposition();
        customRepository.update(HistoryParmUtil.setDevice(deviceBean)).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(context) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                //假网状态
                NetProgressObservable.getInstance().hide();
                SyncCacheUtils.setUnBindState(false);
            }

            @Override
            public void onNext(Integer s) {
                SyncCacheUtils.setUnBindState(false);
                NetProgressObservable.getInstance().hide();
                com.isport.blelibrary.utils.Logger.myLog("解绑成功");
                AppSP.putString(context, AppSP.FORM_DFU, "false");
                ISportAgent.getInstance().deleteDeviceType(deviceBean.currentType, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                BleAction.deletAll();
                BaseAction.dropDatas();
                if (deviceBean.deviceType == JkConfiguration.DeviceType.WATCH_W516 || deviceBean.deviceType == JkConfiguration.DeviceType.BRAND_W311) {
                    SyncCacheUtils.clearSysData(BaseApp.getApp());
                    SyncCacheUtils.clearSetting(BaseApp.getApp());
                    SyncCacheUtils.clearFirstBindW311(BaseApp.getApp());
                    SyncCacheUtils.clearSysData(BaseApp.getApp());
                }
                mActView.get().onUnBindSuccess();
                if (deviceBean.deviceType == JkConfiguration.DeviceType.SLEEP) {
                    com.isport.blelibrary.utils.Logger.myLog("睡眠带解绑成功");
                    TokenUtil.getInstance().saveSleepTime(BaseApp.getApp(), "");
                }
            }
        });
    }

    public void updateWatchHistoryData(DeviceBean deviceBean) {
        if (deviceBean == null) {
            if (view != null) {
                view.updateFail();
            }
            return;
        }
        if (!(App.appType() == App.httpType)) {
            if (isViewAttached()) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_SLEEP_DATA_SUCCESS));
                mActView.get().updateWatchHistoryDataSuccess(deviceBean);
            }
        } else {
            //上传那些满1440的天
            List<Watch_W516_24HDataModel> dataList = Watch_W516_24HDataModelAction
                    .findWatch_W516_Watch_W516_24HDataModelByDeviceIdAndTimeTamp
                            (TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()),
                                    App.getWatchBindTime(), AppConfiguration.braceletID, true);
            if (dataList != null) {
                mWatch_W516_24HDataModel = dataList;
                mCurrentWatchIndex = 0;
                updateWatchHistory(deviceBean);
                for (int i = 0; i < dataList.size(); i++) {
                    com.isport.blelibrary.utils.Logger.myLog("dataList == " + dataList.get(i).toString());
                }
            } else {
                if (isViewAttached())
                    mActView.get().updateWatchHistoryDataSuccess(deviceBean);
            }
        }
        //model.updateSleepHistoryData(sleepHistoryDataResult, name);
    }

    public void saveDevcieSedentaryReminder(String devcieId, String userId, int times, String starTime, String endTime, boolean enable) {
        iw516Model.saveDeviceSedentaryReminder(devcieId, userId, times, starTime, endTime, enable);
    }


    public void getDeviceSedentaryReminder(String deviceId, String userId) {


        Observable.create(new ObservableOnSubscribe<Watch_W516_SedentaryModel>() {
            @Override
            public void subscribe(ObservableEmitter<Watch_W516_SedentaryModel> emitter) throws Exception {
                Watch_W516_SedentaryModel watch_w516_watch_w516_sedentaryModelyDeviceId = Watch_W516_SedentaryModelAction.findWatch_W516_Watch_W516_SedentaryModelyDeviceId(deviceId, userId);

                if (watch_w516_watch_w516_sedentaryModelyDeviceId == null) {
                    watch_w516_watch_w516_sedentaryModelyDeviceId = new Watch_W516_SedentaryModel();
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setIsEnable(false);
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setLongSitTimeLong(60);
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setDeviceId(deviceId);
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setUserId(userId);
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setLongSitStartTime(Constants.defStartTime);
                    watch_w516_watch_w516_sedentaryModelyDeviceId.setLongSitEndTime(Constants.defEndTime);
                }
                emitter.onNext(watch_w516_watch_w516_sedentaryModelyDeviceId);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Watch_W516_SedentaryModel>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Watch_W516_SedentaryModel watchSleepDayData) {
                if (view != null) {
                    view.seccessGetDeviceSedentaryReminder(watchSleepDayData);
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

                com.isport.blelibrary.utils.Logger.myLog(e.toString());
            }

        });


    }

    private void updateWatchHistory(DeviceBean deviceBean) {
        com.isport.blelibrary.utils.Logger.myLog("updateWatchHistory == 上传数据" + mCurrentWatchIndex);
        Watch_W516_24HDataModel watch_w516_24HDataModel = mWatch_W516_24HDataModel.get(mCurrentWatchIndex);
        Constants.isSyncData = true;
        WatchRepository.requst(watch_w516_24HDataModel)
                .as(view.bindAutoDispose())
                .subscribe(new BaseObserver<UpdateSuccessBean>(context) {
                    @Override
                    protected void hideDialog() {

                    }

                    @Override
                    protected void showDialog() {

                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable e) {
                        Constants.isSyncData = false;
                        if (view != null) {
                            view.updateFail();
                        }
                    }

                    @Override
                    public void onNext(UpdateSuccessBean updateSleepReportBean) {
                        Constants.isSyncData = false;
                        NetProgressObservable.getInstance().hide();
                        com.isport.blelibrary.utils.Logger.myLog("UpdateSuccessBean == " + updateSleepReportBean.toString());
                        if (isViewAttached()) {
                            Watch_W516_24HDataModelDao watch_w516_24HDataModelDao = BleAction
                                    .getWatch_W516_24HDataModelDao();
                            watch_w516_24HDataModel.setReportId(updateSleepReportBean.getPublicId());
                            watch_w516_24HDataModelDao.update(watch_w516_24HDataModel);
                            App.setWatchBindTime(updateSleepReportBean.getTimestamp());
                            mCurrentWatchIndex++;
                            if (mCurrentWatchIndex > mWatch_W516_24HDataModel.size() - 1) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent
                                        .UPDATE_WATCH_DATA_SUCCESS));
                                mActView.get().updateWatchHistoryDataSuccess(deviceBean);
                            } else {
                                updateWatchHistory(deviceBean);
                            }
                        }
                    }
                });
    }


    public void getPlayBanImage(int devcieType) {
        PlayBandRepository.requstGetPlayBandImage(devcieType).subscribe(new BaseObserver<List<PlayBean>>(BaseApp.getApp(), false) {


            @Override
            public void onNext(List<PlayBean> infos) {
                UserAcacheUtil.savePlayBandInfo(devcieType, infos);
                com.isport.blelibrary.utils.Logger.myLog("getPlayBanImage:" + infos);
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

}
