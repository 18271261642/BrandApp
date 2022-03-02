package com.isport.brandapp.device.bracelet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.StepArithmeticUtil;
import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.braceletPresenter.BraceletStepPresenter;
import com.isport.brandapp.device.bracelet.view.BraceletStepView;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.device.share.NewShareActivity;
import com.isport.brandapp.device.share.ShareBean;
import com.isport.brandapp.device.watch.bean.WatchHistoryNBean;
import com.isport.brandapp.device.watch.bean.WatchHistoryNList;
import com.isport.brandapp.home.bean.http.Wristbandstep;
import com.isport.brandapp.parm.http.WatchHistoryParms;
import com.isport.brandapp.repository.CustomRepository;
import com.isport.brandapp.util.DeviceDataUtil;
import com.isport.brandapp.util.W311DeviceDataUtil;

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
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonnet.net.PostBody;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.entry.WatchTargetBean;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class MonthReportFragment extends BaseMVPFragment<BraceletStepView, BraceletStepPresenter> implements BraceletStepView {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    private TextView tv_update_time;
    private RopeBarChartView barChartView;
    private String strDate;
    private SporttemView itemStep, itemDis, itemCal;
    private TextView tvfat, tvGole, tvCal, tvStep, tvDis;
    private LinearLayout layoutCal, layoutDis, layoutStep;
    private ImageView iv_no_data;

    int date;
    String userId;
    String deviceId;
    int currentType;
    private DeviceBean deviceBean;
    private UserInfoBean userInfoBean;


    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_w311_month_step;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getInt("date");
        deviceId = getArguments().getString("deviceId");
        deviceBean = (DeviceBean) getArguments().getSerializable(JkConfiguration.DEVICE);
        if (deviceBean != null) {
            deviceId = deviceBean.deviceName;
            currentType = deviceBean.currentType;
        } else {
            deviceId = "";
            currentType = 0;
        }
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date * 1000l));
        strDate = dateFormat.format(new Date(date * 1000l));
        userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getMonthData(calendar);
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
        tvCal = view.findViewById(R.id.tv_watch_avg_cal);
        tvStep = view.findViewById(R.id.tv_watch_step_recode_step);
        tvDis = view.findViewById(R.id.tv_watch_step_recode_distance);
        layoutStep = view.findViewById(R.id.layout_step);
        layoutCal = view.findViewById(R.id.layout_cal);
        layoutDis = view.findViewById(R.id.layout_dis);


    }

    @Override
    protected void initData() {
        tv_update_time.setText(strDate);
        itemStep.setBottomText(UIUtils.getString(R.string.avg_step));
        itemCal.setBottomText(UIUtils.getString(R.string.avg_cal));
        itemDis.setBottomText(UIUtils.getString(R.string.avg_dis));
        mFragPresenter.getBraceletMonthData(userId, date, deviceId, currentType);
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
        Intent intent = new Intent(context, NewShareActivity.class);
//        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.WATCH_W516);
        ShareBean shareBean = new ShareBean();
        //目标比例 设置的目标值与当前值的比列
        shareBean.one = avgStep;
        //里程
        shareBean.two = avgDis;
        //消耗
        shareBean.three = avgCal;


        shareBean.isWeek = true;
        shareBean.time = strDate;

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
    String avgStep, avgDis, avgCal;

    @Override
    public void successWeekBraceletSportDetail(List<Wristbandstep> wristbandsteps) {

        Wristbandstep sumStep = new Wristbandstep();

        ArrayList<Integer> stepList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        int sumsStep = 0;
        //当月的情况，需要把没有到的天数减掉

        float sumDis = 0, sumCal = 0;
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
            sumDis = StepArithmeticUtil.stepsConversionDistanceFloat(Float.parseFloat(userInfoBean.getHeight()), userInfoBean.getGender(), sumsStep);
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
            avgFat = sumCal * 0.127f;
            avgGoal = sumDis * 0.0826f;

        } catch (Exception e) {

        } finally {

            //  Logger.myLog("sport :  avgFat:" + avgFat + ",avgGoal:" + avgGoal + "CommonDateUtil.formatTwoPoint(avgGoal):" + CommonDateUtil.formatTwoPoint(avgGoal) + "CommonDateUtil.formatTwoPoint(avgFat):" + CommonDateUtil.formatTwoPoint(avgFat));

/*
            if (!DeviceTypeUtil.isContainW81(currentType)) {
                iv_no_data.setVisibility(View.GONE);
                barChartView.setVisibility(View.VISIBLE);
            }*/

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
            itemCal.setTitleText( sumStep.getAvgCal());


            tvCal.setText(todayCal);
            tvStep.setText(todayStep);
            tvDis.setText(todayDis);
        }


        //sumStep.setStepNum("23256987");

    }

    @Override
    public void successLastSportsummary(Wristbandstep wristbandstep) {

        if (wristbandstep != null) {
            itemStep.setTitleText(wristbandstep.getStepNum());
            itemDis.setTitleText(wristbandstep.getStepKm());
            itemCal.setTitleText(wristbandstep.getCalorie());
            tvGole.setText(wristbandstep.getSumGasoline());
            tvfat.setText(wristbandstep.getSumFat());
        } else {
            itemStep.setTitleText("0");
            itemDis.setTitleText("0.00");
            itemCal.setTitleText("0");
            tvCal.setText("0");
            tvStep.setText("0");
            tvDis.setText("0.00");
            tvGole.setText("0.00");
            tvfat.setText("0.00");
        }


        //setBarChartData(wristbandstep.getStepArry());


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
        return new BraceletStepPresenter(this);
    }

    @Override
    public void onRespondError(String message) {

    }


    private void setWeekBarChartData(ArrayList<Integer> stepList, ArrayList<String> date) {
        if (Collections.max(stepList) == 0) {
            iv_no_data.setVisibility(View.VISIBLE);
            barChartView.setVisibility(View.GONE);
            return;
        }
        iv_no_data.setVisibility(View.GONE);
        barChartView.setVisibility(View.VISIBLE);
        List<BarChartEntity> datas = new ArrayList<>();
        if (stepList == null) {
            for (int i = 0; i <= 30; i++) {
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
           /* if (datas.size() == 31) {
                datas.add(new BarChartEntity(String.valueOf(0), new Float[]{Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)}));
                date.add(date.get(date.size() - 1));
            }*/
        }
        barChartView.setData(datas, new int[]{Color.parseColor("#6FC5F4")}, "分组", "数量");

        barChartView.setWeekDateList(date);
        barChartView.setCurrentType(currentType);
        barChartView.startAnimation();
    }

    private void getMonthData(Calendar instance) {

        //需要区分是w516还是w311


        if (!(App.appType() == App.httpType)) {
            return;
        }

        /**
         * 获取步数，当月的数据，同步到本地
         */
        CustomRepository<WatchHistoryNList, WatchHistoryParms, BaseUrl, BaseDbPar> watchHistory = new
                CustomRepository<>();
        PostBody<WatchHistoryParms, BaseUrl, BaseDbPar> watchHistoryDataList = null;
        if (JkConfiguration.DeviceType.WATCH_W516 == currentType) {
            watchHistoryDataList = HistoryParmUtil
                    .getWatchHistoryByTimeTampData(instance.getTimeInMillis(), JkConfiguration.WatchDataType.STEP);
        } else if (JkConfiguration.DeviceType.BRAND_W311 == currentType) {
            watchHistoryDataList = HistoryParmUtil
                    .getBraceletHistoryByTimeTampData(instance.getTimeInMillis(), JkConfiguration.WatchDataType.STEP);
        }
        if (watchHistoryDataList == null) {
            return;
        }
        watchHistory.requst(watchHistoryDataList)
                .as(this.bindAutoDispose()).subscribe(new BaseObserver<WatchHistoryNList>(context) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

            @Override
            public void onNext(WatchHistoryNList watchHistoryNList) {
                NetProgressObservable.getInstance().hide();
                //Logger.myLog("获取主页WatchHistoryNList成功 == " + watchHistoryNList.toString());
                if (watchHistoryNList.getList() == null || (watchHistoryNList.getList() != null && watchHistoryNList
                        .getList().size() == 0)) {
                    //无数据的情况
                } else {
                    //有数据的情况
                    List<WatchHistoryNBean> list = watchHistoryNList.getList();
                    for (int i = 0; i < list.size(); i++) {
                        WatchHistoryNBean watchHistoryNBean = list.get(i);
                        if (JkConfiguration.DeviceType.WATCH_W516 == currentType) {
                            DeviceDataUtil.getWatch_W516_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.STEP);
                        } else if (JkConfiguration.DeviceType.BRAND_W311 == currentType) {
                            W311DeviceDataUtil.getBracelet_W311_24HDataModel(watchHistoryNBean, JkConfiguration.WatchDataType.STEP);
                        }
                    }
                }
            }
        });
        NetProgressObservable.getInstance().hide();

    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true)
                .init();
    }
}
