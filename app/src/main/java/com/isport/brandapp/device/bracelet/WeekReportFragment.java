package com.isport.brandapp.device.bracelet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.brandapp.App;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.braceletPresenter.BraceletStepPresenter;
import com.isport.brandapp.device.bracelet.view.BraceletStepView;
import com.isport.brandapp.device.share.NewShareActivity;
import com.isport.brandapp.device.share.ShareBean;
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
import java.util.Locale;

import bike.gymproject.viewlibray.SporttemView;
import bike.gymproject.viewlibray.WeekBarChartView;
import bike.gymproject.viewlibray.chart.BarChartEntity;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.WatchTargetBean;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class WeekReportFragment extends BaseMVPFragment<BraceletStepView, BraceletStepPresenter> implements BraceletStepView {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private TextView tv_update_time;
    private Wristbandstep wristbandstep;
    private WeekBarChartView weekBarChartView;
    private String strStartWeek, strEndWeek;
    private SporttemView itemStep, itemDis, itemCal;
    private TextView tvfat, tvGole, tvCal, tvStep, tvDis;
    private LinearLayout layoutCal, layoutDis, layoutStep;
    private String startweekTime, endweekTime;
    private int currentType;
    private String userId;
    int date;
    private String deviceId;
    private DeviceBean deviceBean;
    private UserInfoBean userInfoBean;
    String avgStep, avgDis, avgCal;
    private ImageView iv_no_data;

    W81DataPresenter w81DataPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_w311_week_step;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getInt("date");
        deviceId = getArguments().getString("deviceId");
        userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp());
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        strStartWeek = getArguments().getString("startdate");
        strEndWeek = getArguments().getString("enddate");
        deviceBean = (DeviceBean) getArguments().getSerializable(JkConfiguration.DEVICE);
        if (deviceBean != null) {
            deviceId = deviceBean.deviceName;
            currentType = deviceBean.currentType;
        } else {
            deviceId = "";
            currentType = 0;
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        Log.e(TAG,"------周="+date);
        Calendar calendar = Calendar.getInstance();
        //需要判断是不是当前的月
        calendar.setTime(new Date(date * 1000L));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String strDate = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd");
        Logger.myLog("--------isCurrentDay" + strDate);
        //strDate = dateFormat.format(new Date(date * 1000l));

        boolean isFirstDay = DateUtil.isMothFirstDay(strDate);

        if(isFirstDay){
            //向前移动一个月
            calendar.add(Calendar.MONTH, -1);
            getMonthData(calendar);//获取上月的数据
        }
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

    }


    @Override
    protected void initView(View view) {
        iv_no_data = view.findViewById(R.id.iv_no_data);
        tv_update_time = view.findViewById(R.id.tv_update_time);
        weekBarChartView = view.findViewById(R.id.barChartView);
        itemStep = view.findViewById(R.id.item_one);
        itemDis = view.findViewById(R.id.item_two);
        itemCal = view.findViewById(R.id.item_three);
        tvfat = view.findViewById(R.id.tv_watch_step_recode_fat);
        tvGole = view.findViewById(R.id.tv_watch_step_recode_qiyou);
        tvCal = view.findViewById(R.id.tv_watch_avg_cal);
        tvStep = view.findViewById(R.id.tv_watch_step_recode_step);
        tvDis = view.findViewById(R.id.tv_watch_step_recode_distance);
        layoutStep = view.findViewById(R.id.layout_step);
        layoutCal = view.findViewById(R.id.layout_cal);
        layoutDis = view.findViewById(R.id.layout_dis);

    }

    @Override
    protected void initData() {
        tv_update_time.setText(strStartWeek + "~" + strEndWeek);
        mFragPresenter.getBraceletWeekData(userId, date, deviceId, currentType);
        itemStep.setBottomText(UIUtils.getString(R.string.avg_step));
        itemCal.setBottomText(UIUtils.getString(R.string.avg_cal));
        itemDis.setBottomText(UIUtils.getString(R.string.avg_dis));
        String title = "";
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
                    createShare();
                }
                break;
        }
    }

    public void createShare() {
        //Intent intent = new Intent(context, ShareActivity.class);
        Intent intent = new Intent(context, NewShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.WATCH_W516);
        ShareBean shareBean = new ShareBean();
        //目标比例 设置的目标值与当前值的比列
        shareBean.one = avgStep;
        //里程
        shareBean.two = avgDis;
        //消耗
        shareBean.three = avgCal;

        shareBean.isWeek = true;
        shareBean.time = strStartWeek + "-" + strEndWeek;

        //今日步数
        if (currentType == JkConfiguration.DeviceType.WATCH_W516) {
            shareBean.centerValue = UIUtils.getString(R.string.watch) + UIUtils.getString(R.string.steps);
        } else {
            shareBean.centerValue = UIUtils.getString(R.string.wristband) + UIUtils.getString(R.string.steps);
        }
        //时间
//
        // shareBean.time = time;
        intent.putExtra(JkConfiguration.FROM_BEAN, shareBean);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    String todayStep, todayDis, todayCal;

    @Override
    public void successWeekBraceletSportDetail(List<Wristbandstep> wristbandsteps) {

        Wristbandstep sumStep = new Wristbandstep();

        ArrayList<Integer> stepList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        int sumsStep = 0;
        double sumDis = 0, sumCal = 0;
        //当前周的数据，没有到的需要减掉

        int days = wristbandsteps.get(0).getDays();
        for (int i = 0; i < wristbandsteps.size(); i++) {
            int sum = TextUtils.isEmpty(wristbandsteps.get(i).getStepNum()) ? 0 : Integer.parseInt(wristbandsteps.get(i).getStepNum());
            float sumD = TextUtils.isEmpty(wristbandsteps.get(i).getCalorie()) ? 0 : Float.parseFloat(wristbandsteps.get(i).getStepKm());
            float sumC = TextUtils.isEmpty(wristbandsteps.get(i).getStepKm()) ? 0 : Float.parseFloat(wristbandsteps.get(i).getCalorie());
            stepList.add((sum));
            sumsStep += sum;
            sumDis += sumD;
            sumCal += sumC;
            dateList.add(wristbandsteps.get(i).getMothAndDay());
        }
        if (currentType == JkConfiguration.DeviceType.WATCH_W516) {
            sumDis = StepArithmeticUtil.stepsConversionDistanceF(Float.parseFloat(userInfoBean.getHeight()), userInfoBean.getGender(), sumsStep);
            sumCal = StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(userInfoBean.getWeight()), sumsStep);

        }
        sumStep.setStepNum(sumsStep + "");
        sumStep.setStepKm(CommonDateUtil.formatTwoPoint(sumDis) + "");
        sumStep.setCalorie(CommonDateUtil.formatInterger(sumCal) + "");
        if (sumsStep == 0) {
            sumStep.setAvgStep("0");
            sumStep.setSumGasoline("0");
            sumStep.setSumFat("0");
        } else {
            sumStep.setAvgStep(CommonDateUtil.formatInterger(sumsStep * 1.0 / days) + "");
        }
        if (sumDis == 0) {
            sumStep.setAvgDis(CommonDateUtil.formatTwoPoint(0));
        } else {
            sumStep.setAvgDis(CommonDateUtil.formatTwoPoint(sumDis / days));
        }
        if (sumCal == 0) {
            sumStep.setAvgCal(CommonDateUtil.formatInterger(0));
        } else {
            sumStep.setAvgCal(CommonDateUtil.formatInterger(sumCal / days));
        }
        float avgFat = 0f;
        float avgGoal = 0f;
        try {
            avgFat = (float) (sumCal * 0.127f);
            avgGoal = (float) (sumDis * 0.0826f);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Logger.myLog("sport :  avgFat:" + avgFat + ",avgGoal:" + avgGoal + "CommonDateUtil.formatTwoPoint(avgGoal):" + CommonDateUtil.formatTwoPoint(avgGoal) + "CommonDateUtil.formatTwoPoint(avgFat):" + CommonDateUtil.formatTwoPoint(avgFat));

            tvGole.setText(CommonDateUtil.formatTwoPoint(avgGoal));
            tvfat.setText(CommonDateUtil.formatTwoPoint(avgFat));
            setWeekBarChartData(stepList, dateList);

            if (sumStep.getStepNum().length() > 6) {
                itemStep.setTitleSize(getActivity().getResources().getDimension(R.dimen.sp24));
            }
            if (sumStep.getStepKm().length() > 6) {
                itemDis.setTitleSize(getActivity().getResources().getDimension(R.dimen.sp24));
            }
            if (sumStep.getAvgCal().length() > 6) {
                itemCal.setTitleSize(getActivity().getResources().getDimension(R.dimen.sp24));
            }

            todayStep = sumStep.getStepNum();
            todayDis = sumStep.getStepKm();
            todayCal = sumStep.getCalorie();
            avgCal = sumStep.getAvgCal();
            avgDis = sumStep.getAvgDis();
            avgStep = sumStep.getAvgStep();
            itemStep.setTitleText(sumStep.getAvgStep());
            itemDis.setTitleText(sumStep.getAvgDis());
            itemCal.setTitleText(sumStep.getAvgCal());


            tvCal.setText(todayCal);
            tvStep.setText(todayStep);
            tvDis.setText(todayDis);
        }


        //sumStep.setStepNum("23256987");


    }

    @Override
    public void successLastSportsummary(Wristbandstep wristbandstep) {


    }

    @Override
    public void successTargetStep(WatchTargetBean watchTargetBean) {

    }

    @Override
    public void successMonthDate(ArrayList<String> strDates) {

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


    private void setWeekBarChartData(ArrayList<Integer> stepList, ArrayList<String> date) {

        if (Collections.max(stepList) == 0) {
            iv_no_data.setVisibility(View.VISIBLE);
            weekBarChartView.setVisibility(View.GONE);
            return;
        }
        iv_no_data.setVisibility(View.GONE);
        weekBarChartView.setVisibility(View.VISIBLE);
       /* if (!DeviceTypeUtil.isContainW81(currentType)) {
            iv_no_data.setVisibility(View.GONE);
            weekBarChartView.setVisibility(View.VISIBLE);
        }
*/
        List<BarChartEntity> datas = new ArrayList<>();
        if (stepList == null) {
            for (int i = 0; i <= 7; i++) {
                datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)}));

            }
        } else {
            for (int i = 0; i < stepList.size(); i++) {
                if (i < stepList.size()) {
                    int cal = (int) StepArithmeticUtil.stepsConversionCaloriesFloat(Float.parseFloat(userInfoBean.getWeight()), stepList.get(i));
                    float dis = StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(userInfoBean.getHeight()), userInfoBean.getGender(), stepList.get(i));
                    datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(stepList.get(i)), Float.valueOf(cal), dis}));
                } else {
                    datas.add(new BarChartEntity(String.valueOf(i), new Float[]{Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)}));
                }

            }
        }

        int index = 0;
        int clickPostion = 0;
        while (index < stepList.size()) {
            if (stepList.get(index) > 0) {
                clickPostion = index;
            }
            //println("item at $index is ${items[index]}")
            index++;
        }

        weekBarChartView.setData(datas, new int[]{Color.parseColor("#6FC5F4")}, "分组", "数量");
        weekBarChartView.setWeekDateList(date);
        weekBarChartView.setCurrentType(currentType);
        weekBarChartView.startAnimation();

        int finalClickPostion = clickPostion;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                weekBarChartView.setmClickPosition(finalClickPostion);
            }
        }, 10);
    }

    /**
     * 取得当前日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek()); // Sunday
        return calendar.getTime();
    }

    /**
     * 取得当前日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek() + 6); // Saturday
        return calendar.getTime();
    }


    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true)
                .init();
    }
}
