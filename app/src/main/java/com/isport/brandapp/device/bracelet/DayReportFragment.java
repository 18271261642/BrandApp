package com.isport.brandapp.device.bracelet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.braceletPresenter.BraceletStepPresenter;
import com.isport.brandapp.device.bracelet.view.BraceletStepView;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.share.NewShareActivity;
import com.isport.brandapp.device.share.ShareBean;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.device.sleep.calendar.Cell;
import com.isport.brandapp.device.sleep.calendar.WatchPopCalendarView;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.home.presenter.DeviceHistotyDataPresenter;
import com.isport.brandapp.home.presenter.W81DataPresenter;
import com.isport.brandapp.util.DeviceTypeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import bike.gymproject.viewlibray.RopeBarChartView;
import bike.gymproject.viewlibray.SporttemView;
import bike.gymproject.viewlibray.chart.BarChartEntity;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.WatchTargetBean;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class DayReportFragment extends BaseMVPFragment<BraceletStepView, BraceletStepPresenter> implements BraceletStepView, WatchPopCalendarView.MonthDataListen {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatMMDD = new SimpleDateFormat("yyyy-MM");
    private TextView tv_update_time;
    W81DataPresenter w81DataPresenter;

    private RopeBarChartView barChartView;
    private String strDate;
    private SporttemView itemStep, itemDis, itemCal;
    private TextView tvfat, tvGole, tvCal, tvStep, tvDis;
    private UserInfoBean userInfoBean;
    private String deviceId;
    private int currentType;
    private String userId;
    private DeviceBean deviceBean;
    private int date;

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_w311_day_step;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getInt("date");
        deviceId = getArguments().getString("deviceId");
        userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp());
        strDate = dateFormat.format(new Date(date * 1000l));
        deviceBean = (DeviceBean) getArguments().getSerializable(JkConfiguration.DEVICE);

        Logger.myLog("DayReportFragment" + deviceBean);
        if (deviceBean != null) {
            deviceId = deviceBean.deviceName;
            currentType = deviceBean.currentType;
        } else {
            deviceId = "";
            currentType = 0;
        }
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void initView(View view) {
        iv_no_data = view.findViewById(R.id.iv_no_data);
        tv_update_time = view.findViewById(R.id.tv_update_time);
        barChartView = view.findViewById(R.id.barChartView);
        itemStep = view.findViewById(R.id.item_one);
        itemDis = view.findViewById(R.id.item_two);
        itemCal = view.findViewById(R.id.item_three);
        tvfat = view.findViewById(R.id.tv_watch_step_recode_fat);
        tvGole = view.findViewById(R.id.tv_watch_step_recode_qiyou);
        //   tvCal = view.findViewById(R.id.tv_watch_avg_cal);
        tvStep = view.findViewById(R.id.tv_watch_step_recode_step);
        tvDis = view.findViewById(R.id.tv_watch_step_recode_distance);

    }

    @Override
    protected void initData() {
       /* titleBarView.setLeftIconEnable(true);
        titleBarView.setBg(getResources().getColor(R.color.common_view_color));
        frameBodyLine.setVisibility(View.GONE);
        titleBarView.setTitle(getResources().getString(R.string.my_sport));
        titleBarView.setRightText("");
        titleBarView.setRightIcon(R.drawable.icon_white_share);
        titleBarView.setHistrotyIcon(R.drawable.icon_white_calender);*/
        tv_update_time.setText(strDate);
        Logger.myLog("userId:" + userId + "strDate:" + strDate + "deviceId:" + deviceId + "currentType:" + currentType + "date:" + date);

        if (DeviceTypeUtil.isContainW81(currentType)) {
            barChartView.setVisibility(View.GONE);
            iv_no_data.setVisibility(View.VISIBLE);
        } else {
            barChartView.setVisibility(View.VISIBLE);
            iv_no_data.setVisibility(View.GONE);
        }

        mFragPresenter.getBraceletDayData(userId, strDate, deviceId, currentType, date);
    }

    @Override
    protected void initEvent() {


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.share:
                if (getUserVisibleHint()) {
                    createShareBean();
                }
                break;
            case MessageEvent.calender:
                if (getUserVisibleHint()) {
                    setPopupWindow(getActivity(), tv_update_time);

                    int time = (int) (System.currentTimeMillis() / 1000);


                    initDatePopMonthTitle();
                    calendarview.setActiveC(TimeUtil.second2Millis(time));
                    calendarview.setMonthDataListen(DayReportFragment.this);
                    calendarview.setTimeInMillis(TimeUtil.second2Millis(time));
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    @Override
    public void successWeekBraceletSportDetail(List<Wristbandstep> wristbandsteps) {

    }

    String todayStep, todayDis, todayCal;
    private ImageView iv_no_data;

    @Override
    public void successLastSportsummary(Wristbandstep wristbandstep) {


        if (wristbandstep == null) {
            todayStep = "0";
            todayDis = "0";
            todayCal = "0";
            itemStep.setTitleText("0");
            itemDis.setTitleText("0");
            itemCal.setTitleText("0");
            tvGole.setText("0");
            tvfat.setText("0");
            setBarChartData(null);

        } else {

            int sumStep = 0;
            try {
                sumStep = Integer.parseInt(wristbandstep.getStepNum());
            } catch (Exception e) {
                sumStep = 0;
            } finally {
                todayStep = sumStep + "";
                todayDis = TextUtils.isEmpty(wristbandstep.getStepKm()) ? "0" : CommonDateUtil.formatTwoPoint(Float.valueOf(wristbandstep.getStepKm()));
                todayCal = TextUtils.isEmpty(wristbandstep.getCalorie()) ? "0" : CommonDateUtil.formatInterger(Float.valueOf(wristbandstep.getCalorie()));
                //todayStep = "23256987";
                if (todayStep.length() > 6) {
                    itemStep.setTitleSize(getActivity().getResources().getDimension(R.dimen.sp24));
                }
                if (todayDis.length() > 6) {
                    itemDis.setTitleSize(getActivity().getResources().getDimension(R.dimen.sp24));
                }
                if (todayCal.length() > 6) {
                    itemCal.setTitleSize(getActivity().getResources().getDimension(R.dimen.sp24));
                }


                itemStep.setTitleText(todayStep);
                itemDis.setTitleText(todayDis);
                itemCal.setTitleText(todayCal);
                ArrayList<String> list = StepArithmeticUtil.stepsAvgConversionDistance(Float.parseFloat(userInfoBean.getHeight()), userInfoBean.getGender(), sumStep, 0);
                float avgFat = 0f;
                float avgGoal = 0f;
                try {
                    avgFat = Float.valueOf(wristbandstep.getCalorie()) * 0.127f;
                    avgGoal = Float.valueOf(wristbandstep.getStepKm()) * 0.0826f;

                } catch (Exception e) {

                } finally {
                    tvGole.setText(CommonDateUtil.formatTwoPoint(avgGoal));
                    tvfat.setText(CommonDateUtil.formatTwoPoint(avgFat));
                    if (barChartView.getVisibility() == View.VISIBLE) {
                        setBarChartData(wristbandstep.getStepArry());
                    }
                }


            }

        }

        //createShareBean();
    }

    public void createShareBean() {

        Intent intent = new Intent(context, NewShareActivity.class);
//        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.WATCH_W516);
        ShareBean shareBean = new ShareBean();
        //目标比例 设置的目标值与当前值的比列
        shareBean.one = todayStep;
        //里程
        shareBean.two = todayDis;
        //消耗
        shareBean.three = todayCal;


        shareBean.time = strDate;

        //今日步数
        if (currentType == JkConfiguration.DeviceType.WATCH_W516) {
            shareBean.centerValue = UIUtils.getString(R.string.watch) + UIUtils.getString(R.string.steps);
        } else {
            shareBean.centerValue = UIUtils.getString(R.string.wristband) + UIUtils.getString(R.string.steps);
        }
        //时间
        // shareBean.time = time;
        intent.putExtra(JkConfiguration.FROM_BEAN, shareBean);
        startActivity(intent);

    }

    @Override
    public void successTargetStep(WatchTargetBean watchTargetBean) {

    }

    @Override
    public void successMonthDate(ArrayList<String> strDates) {
        calendarview.setSummary(strDates);
    }

    @Override
    public void succcessLastMontData(String avgStep, String avgDis, String sumGola, String sumFat) {

    }

    @Override
    protected BraceletStepPresenter createPersenter() {
        w81DataPresenter = new W81DataPresenter(this);
        return new BraceletStepPresenter(this);
    }

    @Override
    public void onRespondError(String message) {

    }

    boolean isShow = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isShow = true;
        } else {
            isShow = false;
        }
    }

    private void setBarChartData(ArrayList<Integer> stepList) {

        if (stepList == null) {
            iv_no_data.setVisibility(View.VISIBLE);
            barChartView.setVisibility(View.GONE);
            return;
        } else {
            if (Collections.max(stepList) == 0) {
                iv_no_data.setVisibility(View.VISIBLE);
                barChartView.setVisibility(View.GONE);
                return;
            }

        }

        if (!DeviceTypeUtil.isContainW81(currentType)) {
            iv_no_data.setVisibility(View.GONE);
            barChartView.setVisibility(View.VISIBLE);
        }

        List<BarChartEntity> datas = new ArrayList<>();
        /*for (int i = 0; i <= 24; i++) {
            datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(50), Float.valueOf(50), Float.valueOf(50)}));
        }*/
        if (stepList == null) {
            for (int i = 0; i <= 24; i++) {
                datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)}));
            }
        } else {
            for (int i = 0; i < stepList.size() + 1; i++) {
                if (i < stepList.size()) {
                    int cal = (int) StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(userInfoBean.getWeight()), stepList.get(i));
                    float dis = StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(userInfoBean.getHeight()), userInfoBean.getGender(), stepList.get(i));
                    datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(stepList.get(i)), Float.valueOf(cal), dis}));
                } else {
                    datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)}));
                }

            }
        }
        /*barChartView.setOnItemBarClickListener(new BarChartView.OnItemBarClickListener() {
            @Override
            public void onClick(int position) {
                Log.e("TAG", "点击了：" + position);
            }
        });*/
        barChartView.setData(datas, new int[]{Color.parseColor("#6FC5F4")}, "分组", "数量");
        barChartView.setCurrentType(currentType);
        barChartView.startAnimation();
    }


    private WatchPopCalendarView calendarview;
    private ImageView ivPreDate, ivNextDate;
    private TextView tvDatePopTitle, tvBackToay;


    public void setPopupWindow(Context context, View view) {
        try {


            BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                    .setContentView(R.layout.app_activity_watch_dem)
                    .fullWidth()
                    .setCanceledOnTouchOutside(false)
                    .fromBottom(true)
                    .show();

            View view_hide = mMenuViewBirth.findViewById(R.id.view_hide);
            calendarview = (WatchPopCalendarView) mMenuViewBirth.findViewById(R.id.calendar);
            ivPreDate = (ImageView) mMenuViewBirth.findViewById(R.id.iv_pre);
            ivNextDate = (ImageView) mMenuViewBirth.findViewById(R.id.iv_next);
            tvDatePopTitle = (TextView) mMenuViewBirth.findViewById(R.id.tv_date);
            tvBackToay = (TextView) mMenuViewBirth.findViewById(R.id.tv_back_today);

            view_hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuViewBirth.dismiss();
                }
            });

            calendarview.setOnCellTouchListener(new WatchPopCalendarView.OnCellTouchListener() {
                @Override
                public void onTouch(Cell cell) {
                /*if (cell.getStartTime() <= 0) {
                    Toast.makeText(BaseApp.getApp(),"没有数据", Toast.LENGTH_SHORT).show();
                    return;
                }
               */

                    int stime = cell.getStartTime();
                    String dateStr = cell.getDateStr();

                    if (TextUtils.isEmpty(dateStr)) {
                        return;
                    }
                    Date date = TimeUtils.stringToDate(dateStr);
                    Date mCurrentDate = TimeUtils.getCurrentDate();
                    //未来的日期提示用户不可选
                    if (!date.before(mCurrentDate)) {
                        ToastUtils.showToast(UIUtils.getContext(), UIUtils.getString(R.string.select_date_error));
                        return;
                    }
                    mMenuViewBirth.dismiss();
                    MessageEvent messageEvent = new MessageEvent(MessageEvent.viewPageChage);
                    messageEvent.setObj(dateStr);
                    EventBus.getDefault().post(messageEvent);

                    //需要把positon给修改


             /*   //需要去重新加载frament
                String strCurrentDate = DateUtil.dataToString(date, "yyyy-MM-dd");
                tv_update_time.setText(strCurrentDate);
                mFragPresenter.getBraceletDayData(userId, strCurrentDate, deviceId, currentType);*/


                }
            });


            tvBackToay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dateStr = DateUtil.dataToString(new Date(), "yyyy-MM-dd");
                    mMenuViewBirth.dismiss();
                    MessageEvent messageEvent = new MessageEvent(MessageEvent.viewPageChage);
                    messageEvent.setObj(dateStr);
                    EventBus.getDefault().post(messageEvent);

               /* calendarview.goCurrentMonth();
                getLastMonthData();
                //向前移动一个月
                initDatePopMonthTitle();*/

                    //展示最近的数据
                    //getTodayData();
                }
            });

            ivPreDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    calendarview.previousMonth();
                    calendarview.clearSummary();
                    getLastMonthData();
                    initDatePopMonthTitle();
                }
            });

            ivNextDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    calendarview.nextMonth();
                    calendarview.clearSummary();
                    getLastMonthData();
                    initDatePopMonthTitle();
                }
            });


            //获取当月月初0点时间戳,毫秒值
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.DAY_OF_MONTH, 1);
            instance.set(Calendar.HOUR_OF_DAY, 0);
            instance.set(Calendar.MINUTE, 0);
            instance.set(Calendar.SECOND, 0);
            //向前移动一个月
//        getMonthData(instance);//获取当月的数据,主页已经获取
            instance.add(Calendar.MONTH, -1);
            //首次进入获取上一个月的数据,
            getMonthData(instance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getLastMonthData() {
        Calendar calendar = calendarview.getDate();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //向前移动一个月
        calendar.add(Calendar.MONTH, -1);
        getMonthData(calendar);//获取上月的数据
        calendar.add(Calendar.MONTH, 1);
    }


    private void getMonthData(Calendar instance) {
        if (!(App.appType() == App.httpType)) {
            return;
        }
        Logger.myLog("getMonthData year:" + instance.get(Calendar.YEAR) + "month:" + instance.get(Calendar.MONTH) + "day:" + instance.get(Calendar.DAY_OF_MONTH) + "currentType:" + currentType);
        if (DeviceTypeUtil.isContaintW81(currentType) || DeviceTypeUtil.isContainF18(currentType)) {
            w81DataPresenter.getW81MonthStep(deviceBean.deviceID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.STEP), instance.getTimeInMillis());
        } else {
            DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.STEP, currentType, BaseApp.getApp());
        }
        /* *//**
         * 获取步数，当月的数据，同步到本地
         */

    }


    private void initDatePopMonthTitle() {
        Calendar calendar = calendarview.getDate();
        Logger.myLog(TAG + " initDatePopMonthTitle:" + dateFormat.format(calendar.getTime()));
        tvDatePopTitle.setText(dateFormatMMDD.format(calendar.getTime()));
    }


    @Override
    public void getMontData(String strYearAndMonth) {
        mFragPresenter.getMonthDataStrDate(strYearAndMonth, deviceId, currentType);
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true)
                .init();
    }
}
