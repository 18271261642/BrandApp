package com.isport.brandapp.device.f18;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.share.NewShareActivity;
import com.isport.brandapp.device.share.ShareBean;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.device.sleep.calendar.Cell;
import com.isport.brandapp.device.sleep.calendar.WatchPopCalendarView;
import com.isport.brandapp.device.watch.ActivityWatchLitterSleep;
import com.isport.brandapp.device.watch.presenter.WatchSleepPresenter;
import com.isport.brandapp.device.watch.view.WatchSleepView;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.presenter.W81DataPresenter;
import com.isport.brandapp.util.DateTimeUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import bike.gymproject.viewlibray.AkrobatNumberTextView;
import bike.gymproject.viewlibray.ContinousBarChartView;
import bike.gymproject.viewlibray.WatchHourMinuteView;
import bike.gymproject.viewlibray.chart.ContinousBarChartEntity;
import bike.gymproject.viewlibray.chart.ContinousBarChartTotalEntity;
import bike.gymproject.viewlibray.chart.HourMinuteData;
import bike.gymproject.viewlibray.chart.PieChartData;
import bike.gymproject.viewlibray.chart.PieChartViewHeart;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * Created by Admin
 * Date 2022/2/15
 */
public class F18SleepActivity  extends BaseMVPActivity<WatchSleepView, WatchSleepPresenter> implements
        WatchSleepView,WatchPopCalendarView.MonthDataListen{


    //饼状图
    private PieChartViewHeart pieChartView;
    //显示睡眠时间
    private TextView tv_update_time;
    //显示时和分钟
    private AkrobatNumberTextView tv_current_hour,tv_current_min;
    //显示睡眠状态
    private TextView tv_current_state;
    //零星小睡
    private TextView sporadic_naps;
    //饼状图处显示时和分
    private TextView tv_sum_hour,tv_sum_min;
    private WatchHourMinuteView whmvWakeup, whmvSleep, whmvDeepSleep;
    private TitleBarView titleBarView;


    private WatchPopCalendarView calendarview;
    private ImageView ivPreDate, ivNextDate;
    private TextView tvDatePopTitle, tvBackToay, tvSporadicNaps, tvSleepTotalTime;
    private DeviceBean deviceBean;
    private String mDateStr;
    private String mCurrentStr;

    private W81DataPresenter w81DataPresenter;

    private int deepTime = 0;
    private int sleepTime = 0;
    private int fallAsleepTime = 0;
    private int awakeTime = 0;


    private ContinousBarChartView continousBarChartView;
    @Override
    public void successMonthDate(ArrayList<String> strDates) {
        Log.e(TAG,"-----successMonthDate="+new Gson().toJson(strDates));
        calendarview.setSummary(strDates);
    }

    @Override
    public void successDayDate(WatchSleepDayData watchSleepDayData) {
        Log.e(TAG,"---sss="+new Gson().toJson(watchSleepDayData));
        showPageData(watchSleepDayData);
    }

    private void showPageData(WatchSleepDayData watchSleepDayData) {
        try {

            //睡眠总时长
            int allSleepTime = watchSleepDayData.getTotalSleepTime();
            //清醒时长
            int awakeSleepTime = watchSleepDayData.getAwakeSleepTime();
            //深睡时长
            int deepSleepTime = watchSleepDayData.getDeepSleepTime();
            //浅睡
            int lightSleepTime = watchSleepDayData.getLightLV1SleepTime();

            //无数据
            //深睡和浅睡都为0时，可表示无正常睡眠
            if(deepSleepTime+lightSleepTime == 0){

                //遍历睡眠数组，判断是否有睡眠
                String sleepStr = watchSleepDayData.getSporadicNapSleepTimeStr();
                if(!TextUtils.isEmpty(sleepStr)){
                    List<String[]> sleepArrList = new Gson().fromJson(sleepStr,new TypeToken<List<String[]>>(){}.getType());
                    if(sleepArrList == null || sleepArrList.size()==0){
                        showEmptyView();
                        return;
                    }

                    int dpTime = 0;
                    int ligTime = 0;
                    int awakeTime = 0;
                    for(int i = 0;i<sleepArrList.size();i++) {
                        String[] itemStr = sleepArrList.get(i);
                        //F18 1：深睡；2：浅睡：3：清醒 >>转换后 1 清醒 2浅睡 3 深睡

                        //图表 0 4：清醒；1：快速眼动；2：浅睡；3：深睡；
                        //睡眠类型
                        int typeCode = Integer.parseInt(itemStr[0]);
                        //时长
                        int typeLength = Integer.parseInt(itemStr[3]);
                        if (typeCode == 1) {
                            awakeTime +=typeLength;
                        }
                        if (typeCode == 2) {
                            ligTime +=typeLength;
                        }
                        if (typeCode == 3) {
                            dpTime +=typeLength;
                        }
                    }

                    if(dpTime == 0 && ligTime == 0){
                        showEmptyView();
                        return;
                    }


                    deepSleepTime = dpTime;
                    lightSleepTime = ligTime;
                    awakeSleepTime = awakeTime;

                }

                if(deepSleepTime+lightSleepTime == 0){
                    showEmptyView();
                    return;
                }

            }

            watchSleepDayData.setDeepSleepTime(deepSleepTime);
            watchSleepDayData.setAwakeSleepTime(allSleepTime);
            watchSleepDayData.setLightLV1SleepTime(lightSleepTime);

            //展示时间
            tv_update_time.setText(watchSleepDayData.getDateStr());
            setHourMinute(0xFFADADBB,(deepSleepTime+lightSleepTime)/60,(deepSleepTime+lightSleepTime)%60);
            setPieData(allSleepTime-(deepSleepTime+lightSleepTime),0,(allSleepTime-awakeSleepTime),deepSleepTime,false);
            setSleepSummary((allSleepTime-(deepSleepTime+lightSleepTime)),0,watchSleepDayData.getLightLV1SleepTime(),deepSleepTime);


            showSleepChartView(watchSleepDayData);

            deepTime = deepSleepTime;
            fallAsleepTime = lightSleepTime;
            awakeTime = (allSleepTime-(deepSleepTime+lightSleepTime));
            sleepTime = allSleepTime;

            tv_sum_hour.setText((allSleepTime)/60+"");
            tv_sum_min.setText((allSleepTime)%60+"");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void showSleepChartView(WatchSleepDayData watchSleepDayData){
       // Log.e(TAG,"----睡眠="+watchSleepDayData.toString(0));
        try {
            ContinousBarChartTotalEntity barChartTotalEntity = new ContinousBarChartTotalEntity();
            List<ContinousBarChartEntity> datas = new ArrayList<>();
            String sleepStr = watchSleepDayData.getSporadicNapSleepTimeStr();
            if(TextUtils.isEmpty(sleepStr) || (watchSleepDayData.getDeepSleepTime() == 0 && watchSleepDayData.getLightLV1SleepTime() == 0)){
                datas.clear();
                setPieData(0,0,0,0,false);
                for (int i = 0; i < 1440; i++) {
                    datas.add(new ContinousBarChartEntity(1, 200, 0));
                }
                tv_current_state.setText("");
                barChartTotalEntity.startTime = "";
                barChartTotalEntity.endTime = "";
                barChartTotalEntity.starCalendar = Calendar.getInstance();
                barChartTotalEntity.continousBarChartEntitys = datas;
                continousBarChartView.setData(barChartTotalEntity,"分组","数量");
                continousBarChartView.startAnimation();
                return;
            }

            datas.clear();
            List<String[]> sleepArrList = new Gson().fromJson(sleepStr,new TypeToken<List<String[]>>(){}.getType());

            //开始的时间
            String[] firstStr = sleepArrList.get(0);
            //结束时间
            String[] endStr = sleepArrList.get(sleepArrList.size()-1);
            int startTime = Integer.parseInt(firstStr[1]);
            int endTime = Integer.parseInt(endStr[2]);

            if(startTime >=720){    //表明是昨天的数据

            }

            //转换成HH:mm格式
            String startTimeStr = TimeUtil.formatMinuteToStr(startTime);
            String endTimeStr = TimeUtil.formatMinuteToStr(endTime);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,startTime/60);
            calendar.set(Calendar.MINUTE,startTime % 60);


            barChartTotalEntity.startTime = startTimeStr;
            barChartTotalEntity.endTime = endTimeStr;
            barChartTotalEntity.starCalendar = calendar;

            for(int i = 0;i<sleepArrList.size();i++){
                String[] itemStr = sleepArrList.get(i);
                int chartTypeCode = 0;
                //F18 1：深睡；2：浅睡：3：清醒
                //图表 0 4：清醒；1：快速眼动；2：浅睡；3：深睡；
                //睡眠类型
                int typeCode = Integer.parseInt(itemStr[0]);
//                if(typeCode == 1){
//                    chartTypeCode = 3;
//                }
//                if(typeCode == 2){
//                    chartTypeCode = 2;
//                }
//                if(typeCode == 3){
//                    chartTypeCode = 4;
//                }
                //时长
                int typeLength = Integer.parseInt(itemStr[3]);

                for(int k = 0;k<typeLength;k++){
                    datas.add(new ContinousBarChartEntity(1, 200,typeCode));
                }
            }

            barChartTotalEntity.continousBarChartEntitys = datas;
            continousBarChartView.setData(barChartTotalEntity,"分组","数量");
            continousBarChartView.startAnimation();
            continousBarChartView.setOnItemBarClickListener(new ContinousBarChartView.OnItemBarClickListener() {
                @Override
                public void onClick(int color, int position, int hour, int minute) {
                    setHourMinute(color, hour, minute);
                }

                @Override
                public void onSelectSleepState(String value) {
                    tv_current_state.setText(value);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //设置时分
    private void setHourMinute(int color, int hour, int minute) {
        tv_current_hour.setTextColor(color);
        tv_current_hour.setText(hour + "");
        tv_current_min.setText(minute + "");
        tv_current_min.setTextColor(color);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_watch_sleep;
    }

    @Override
    protected void initView(View view) {
        findViews();
    }



    private void showEmptyView(){
        //先展示空的数据
        tv_update_time.setText(DateTimeUtils.getCurrentDate());
        setHourMinute(0xFF4DDA64,0,0);
        setPieData(0,0,0,0,false);
        setSleepSummary(0,0,0,0);
        showSleepChartView(new WatchSleepDayData());

    }

    @Override
    protected void initData() {
        deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);
        getCurrentData();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected WatchSleepPresenter createPresenter() {
        w81DataPresenter = new W81DataPresenter(this);
        return new WatchSleepPresenter(this);
    }


    private void getCurrentData() {
        if (deviceBean != null) {
            mActPresenter.getWatchLastData(deviceBean.getDeviceID(), TokenUtil.getInstance().getPeopleIdInt(this), IDeviceType.TYPE_WATCH_7018);
        }
    }

    private void findViews() {
        continousBarChartView = findViewById(R.id.continousBarChartView);
        sporadic_naps = findViewById(R.id.sporadic_naps);
        pieChartView = findViewById(R.id.pieChartView);
        tv_update_time = findViewById(R.id.tv_update_time);
        tv_current_hour = findViewById(R.id.tv_current_hour);
        tv_current_min = findViewById(R.id.tv_current_min);
        tv_current_state = findViewById(R.id.tv_current_state);
        tv_sum_hour = findViewById(R.id.tv_sum_hour);
        tv_sum_min = findViewById(R.id.tv_sum_min);

        titleBarView = findViewById(R.id.title_bar);

        whmvWakeup =findViewById(R.id.whmv_wakeup);
        whmvSleep = findViewById(R.id.whmv_sleep);
        whmvDeepSleep = findViewById(R.id.whmv_deep_sleep);

        sporadic_naps.setText("0"+getResources().getString(R.string.minute));

        titleBarView.setBg(getResources().getColor(R.color.common_bg));
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getString(R.string.sleep));
        titleBarView.setRightText("");
        titleBarView.setLeftIcon(R.drawable.icon_back);
        titleBarView.setRightIcon(R.drawable.icon_device_share);
        titleBarView.setHistrotyIcon(R.drawable.icon_sleep_history);

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {
                startShartActivity();
            }
        });

        titleBarView.setOnHistoryClickListener(new TitleBarView.OnHistoryClickListener() {
            @Override
            public void onHistoryClicked(View view) {
                if(isFastDoubleClick())
                    return;
                setPopupWindow(F18SleepActivity.this, view);

                int time = (int) (System.currentTimeMillis() / 1000);
                initDatePopMonthTitle();
                calendarview.setActiveC(TimeUtil.second2Millis(time));
                calendarview.setMonthDataListen(F18SleepActivity.this);
                calendarview.setTimeInMillis(TimeUtil.second2Millis(time));
            }
        });


        findViewById(R.id.rl_litter_sleep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityWatchLitterSleep.class);
                WatchSleepDayData  mWatchSleepDayData = new WatchSleepDayData();
                mWatchSleepDayData.setSporadicNapSleepTime(0);
                mWatchSleepDayData.setSporadicNapSleepTimes(1);
                intent.putExtra("mWatchSleepDayData", mWatchSleepDayData);
                startActivity(intent);
            }
        });
    }



    //分享
    private void startShartActivity() {
//       Intent intent = new Intent(context, ShareActivity.class);
        Intent intent = new Intent(context, NewShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.SLEEP);
        ShareBean shareBean = new ShareBean();

        //new
        shareBean.one = "" + deepTime;
        shareBean.two = "" + fallAsleepTime;
        shareBean.three = "" + awakeTime;
        shareBean.centerValue = "" + sleepTime;
        //日期
        shareBean.time = mDateStr;//DateUtils.getYMD(mSleep_Sleepace_DataModel.getTimestamp() * 1000);
        intent.putExtra(JkConfiguration.FROM_BEAN, shareBean);
        startActivity(intent);
    }



    //时长最小单位为分钟
    private void setSleepSummary(int wakeUpTotalTime, int eyeMoveTotalTime, int sleepTotalTime, int deepSleepTotalTime) {
        whmvWakeup.setData(new HourMinuteData(UIUtils.getColor(R.color.common_awake_sleep), UIUtils.getString(R.string.sleep_awake), String.format("%02d", calculateHourMinute(wakeUpTotalTime)[0]), String.format("%02d", calculateHourMinute(wakeUpTotalTime)[1])));
        //whmvEyeMove.setData(new HourMinuteData(0xFFFD944A, UIUtils.getString(R.string.light_1sleep), String.valueOf(calculateHourMinute(eyeMoveTotalTime)[0]), String.valueOf(calculateHourMinute(eyeMoveTotalTime)[1])));
        whmvSleep.setData(new HourMinuteData(UIUtils.getColor(R.color.common_light_sleep), UIUtils.getString(R.string.light_2sleep), String.format("%02d", calculateHourMinute(sleepTotalTime)[0]), String.format("%02d", calculateHourMinute(sleepTotalTime)[1])));
        whmvDeepSleep.setData(new HourMinuteData(UIUtils.getColor(R.color.common_deep_sleep), UIUtils.getString(R.string.sleep_deep), String.format("%02d", calculateHourMinute(deepSleepTotalTime)[0]), String.format("%02d", calculateHourMinute(deepSleepTotalTime)[1])));
    }

    private int[] calculateHourMinute(int totalTime) {
        int[] arr = new int[2];
        arr[0] = totalTime / 60;
        arr[1] = totalTime % 60;
        return arr;
    }

    //设置饼状图
    private void setPieData(int wakeUpTotalTime, int eyeMoveTotalTime, int sleepTotalTime, int deepSleepTotalTime, boolean hasData) {
        int totalTime = getTotalTime(wakeUpTotalTime, eyeMoveTotalTime, sleepTotalTime, deepSleepTotalTime);

        if (pieChartView != null) {
            List<PieChartData> pieChartDatas = new ArrayList<>();
            if (totalTime == 0) {
                //setHourMinute(0, 0);
               // setSumHourMinute(0, 0);
                pieChartDatas.add(new PieChartData(1, UIUtils.getColor(R.color.common_rope_time_color)));//绿色
            } else {
                //setHourMinute(totalTime / 60, totalTime % 60);
               // setSumHourMinute(totalTime / 60, totalTime % 60);
                if (deepSleepTotalTime != 0) {
                    pieChartDatas.add(new PieChartData(getPiePercent(deepSleepTotalTime, totalTime), UIUtils.getColor(R.color.common_deep_sleep)));//绿色
                }
                if ((eyeMoveTotalTime + sleepTotalTime) != 0) {
                    pieChartDatas.add(new PieChartData(getPiePercent(eyeMoveTotalTime + sleepTotalTime, totalTime), UIUtils.getColor(R.color.common_light_sleep)));//橘黄
                }
                if (wakeUpTotalTime != 0) {
                    pieChartDatas.add(new PieChartData(getPiePercent(wakeUpTotalTime, totalTime), UIUtils.getColor(R.color.common_awake_sleep)));//深黄
                }
            }

            if (pieChartDatas.size() > 1) {
                pieChartView.updateData(pieChartDatas, true);
            } else {
                pieChartView.updateData(pieChartDatas, false);
            }

        }

        // pieChartView.setValue(wakeUpTotalTime, eyeMoveTotalTime + sleepTotalTime, deepSleepTotalTime);

    }

    private int getTotalTime(int wakeUpTotalTime, int eyeMoveTotalTime, int sleepTotalTime, int deepSleepTotalTime) {
        return wakeUpTotalTime+eyeMoveTotalTime+sleepTotalTime+deepSleepTotalTime;
    }

    private float getPiePercent(int time, int totalTime) {
        if (time == 0) {
            return 0;
        }
        float pre = (float) time / totalTime * 100.f;
        if (pre < 1) {
            pre = 1;
        }
        return (float) pre;
    }


    private void setPopupWindow(Context context, View view) {
        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_activity_watch_dem)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .show();

        calendarview = (WatchPopCalendarView) mMenuViewBirth.findViewById(R.id.calendar);
        View view_hide = mMenuViewBirth.findViewById(R.id.view_hide);
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

                Logger.myLog("dateStr" + dateStr);
                if (TextUtils.isEmpty(dateStr)) {
                    return;
                }

                //如果上次选择的日期和这次的选择的日期一样不需要刷界面


                Date date = TimeUtils.stringToDate(dateStr);
                Date mCurrentDate = TimeUtils.getCurrentDate();
                //未来的日期提示用户不可选
                if (!date.before(mCurrentDate)) {
                    ToastUtils.showToast(UIUtils.getContext(), UIUtils.getString(R.string.select_date_error));
                    return;
                }

                mMenuViewBirth.dismiss();
                if (dateStr.equals(mCurrentStr)) {
                    getCurrentData();
                } else {
                    mCurrentStr = dateStr;
                    mActPresenter.getWatchDayData(dateStr,deviceBean.getDeviceID(), deviceBean.getCurrentType(), TokenUtil.getInstance().getPeopleIdInt(F18SleepActivity.this));
                }

                Logger.myLog("mCurrentStr:" + mCurrentStr + "dateStr:" + dateStr);

            }
        });


        tvBackToay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarview.goCurrentMonth();
                getLastMonthData();
                //向前移动一个月
                initDatePopMonthTitle();
                mCurrentStr = DateUtil.dataToString(new Date(), "yyyy-MM-dd");
                mMenuViewBirth.dismiss();
                //最近一天的数据
                getTodayData();
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
//        getMonthData(instance);//获取当月的数据,主页已经获取

        //向前移动一个月
        instance.add(Calendar.MONTH, -1);
        //首次进入获取上一个月的数据,
        w81DataPresenter.getW81MothSleep(deviceBean.getDeviceID(), TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.SLEEP), instance.getTimeInMillis());
    }


    private void getLastMonthData() {
        Calendar calendar = calendarview.getDate();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //向前移动一个月
        calendar.add(Calendar.MONTH, -1);
        //getMonthData(calendar);
        w81DataPresenter.getW81MothSleep(deviceBean.deviceID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.SLEEP), calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 1);


    }

    private void getTodayData() {
        String todayYYYYMMDD = TimeUtils.getTodayYYYYMMDD();

        mActPresenter.getWatchDayData(todayYYYYMMDD, deviceBean.getDeviceID(), deviceBean.getCurrentType(), TokenUtil.getInstance().getPeopleIdStr(F18SleepActivity.this));
        setUpdateTime(todayYYYYMMDD);
    }

    //设置睡眠时间
    private void setUpdateTime(String time) {
        mDateStr = time;
        tv_update_time.setText(UIUtils.getString(R.string.light_at_sleep) + time);
    }


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);

    private void initDatePopMonthTitle() {
        Calendar calendar = calendarview.getDate();
        // LogUtil.log(TAG + " initDatePopMonthTitle:" + dateFormat.format(calendar.getTime()));
        tvDatePopTitle.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void getMontData(String strYearAndMonth) {
        mActPresenter.getMonthDataStrDate(strYearAndMonth, deviceBean.getDeviceType(), deviceBean.getDeviceName(), TokenUtil.getInstance().getPeopleIdStr(this));
    }
}
