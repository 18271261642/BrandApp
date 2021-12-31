package com.isport.brandapp.device.watch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crrepa.ble.conn.bean.CRPSleepInfo;
import com.isport.blelibrary.db.table.sleep.Sleep_Sleepace_DataModel;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.home.bean.http.WatchSleepDayData;
import com.isport.brandapp.home.presenter.DeviceHistotyDataPresenter;
import com.isport.brandapp.home.presenter.W81DataPresenter;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.share.NewShareActivity;
import com.isport.brandapp.device.share.ShareBean;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.device.sleep.calendar.Cell;
import com.isport.brandapp.device.sleep.calendar.WatchPopCalendarView;
import com.isport.brandapp.device.watch.presenter.WatchSleepPresenter;
import com.isport.brandapp.device.watch.view.WatchSleepView;
import com.isport.brandapp.util.DeviceTypeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bike.gymproject.viewlibray.AkrobatNumberTextView;
import bike.gymproject.viewlibray.ContinousBarChartView;
import bike.gymproject.viewlibray.WatchHourMinuteView;
import bike.gymproject.viewlibray.chart.ContinousBarChartEntity;
import bike.gymproject.viewlibray.chart.ContinousBarChartTotalEntity;
import bike.gymproject.viewlibray.chart.HourMinuteData;
import bike.gymproject.viewlibray.chart.PieChartData;
import bike.gymproject.viewlibray.chart.PieChartViewHeart;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @Author 李超凡
 * @Date 2019/2/26
 * @Fuction 睡眠页面
 */

public class ActivityWatchSleep extends BaseMVPActivity<WatchSleepView, WatchSleepPresenter> implements
        WatchSleepView, View.OnClickListener, WatchPopCalendarView.MonthDataListen {
    private RelativeLayout rlLitterSleep;
    private PieChartViewHeart pieChartView;
    private WatchHourMinuteView whmvWakeup, whmvSleep, whmvDeepSleep;
    private ContinousBarChartView continousBarChartView;
    private TextView updateTime;
    private WatchPopCalendarView calendarview;
    private ImageView ivPreDate, ivNextDate;
    private TextView tvDatePopTitle, tvBackToay, tvSporadicNaps, tvSleepTotalTime;
    private DeviceBean deviceBean;
    private String mDateStr;
    private WatchSleepDayData mWatchSleepDayData;
    private String mCurrentStr;
    private int currentDeviceType;
    private TitleBarView titleBarView;

    private boolean isHasSleep = false;

    private String deviceId;
    private TextView tv_current_state;
    private String userId;
    W81DataPresenter w81DataPresenter;
    private TextView tv_sum_hour, tv_sum_min;
    private AkrobatNumberTextView tv_current_hour, tv_current_min;

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_watch_sleep;
    }


    @Override
    protected WatchSleepPresenter createPresenter() {
        w81DataPresenter = new W81DataPresenter(this);
        return new WatchSleepPresenter(this);
    }


    @Override
    protected void initView(View view) {
        try {
            updateTime = view.findViewById(R.id.tv_update_time);
            titleBarView = view.findViewById(R.id.title_bar);
            pieChartView = view.findViewById(R.id.pieChartView);
            tv_current_hour = view.findViewById(R.id.tv_current_hour);
            tv_current_min = view.findViewById(R.id.tv_current_min);
            tv_current_state = view.findViewById(R.id.tv_current_state);

            tv_sum_hour = view.findViewById(R.id.tv_sum_hour);
            tv_sum_min = view.findViewById(R.id.tv_sum_min);
            whmvWakeup = view.findViewById(R.id.whmv_wakeup);
            //whmvEyeMove = view.findViewById(R.id.whmv_eye_move);
            whmvSleep = view.findViewById(R.id.whmv_sleep);
            whmvDeepSleep = view.findViewById(R.id.whmv_deep_sleep);

            rlLitterSleep = view.findViewById(R.id.rl_litter_sleep);
            continousBarChartView = view.findViewById(R.id.continousBarChartView);
            tvSleepTotalTime = view.findViewById(R.id.sleep_total_time_value);
            tvSporadicNaps = view.findViewById(R.id.sporadic_naps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);

        if (deviceBean != null) {
            currentDeviceType = deviceBean.deviceType;
            deviceId = deviceBean.deviceID;
        }
        userId = TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp());
        titleBarView.setBg(getResources().getColor(R.color.common_bg));
        mCurrentStr = DateUtil.dataToString(new Date(), "yyyy-MM-dd");
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getString(R.string.sleep));
        titleBarView.setRightText("");
        titleBarView.setLeftIcon(R.drawable.icon_back);
        titleBarView.setRightIcon(R.drawable.icon_device_share);
        titleBarView.setHistrotyIcon(R.drawable.icon_sleep_history);

        //测试数据
        //展示默认数据
        setUpdateTime(TimeUtils.getTodayYYYYMMDD());

//        setContinousBarChartData(watchSleepDayData.getSleepArry());
        setPieData(0, 0, 0, 0, false);


        setSleepSummary(0, 0, 0, 0);

        tvSporadicNaps.setText("0" + UIUtils.getString(R.string.unit_minute));

        setPointerTextAndProgress(80);
        setEarlyLater(58, 54, 2, 47);
        setTotalTime(70, 113, 40, 124);


        getCurrentData();
    }

    private void getCurrentData() {
        if (deviceBean != null) {
            mActPresenter.getWatchLastData(deviceId, userId, currentDeviceType);
        }
        // mActPresenter.getWatchDayData();
        //getTodayData();
    }

    private void getTodayData() {
        String todayYYYYMMDD = TimeUtils.getTodayYYYYMMDD();

        mActPresenter.getWatchDayData(todayYYYYMMDD, deviceId, currentDeviceType, userId);
        setUpdateTime(todayYYYYMMDD);
    }


    private void setTotalTime(int wakeUpTotalTime, int eyeMoveTotalTime, int sleepTotalTime, int deepSleepTotalTime) {
        int[] arr = calculateHourMinute(getTotalTime(wakeUpTotalTime, eyeMoveTotalTime, sleepTotalTime, deepSleepTotalTime));
        tvSleepTotalTime.setText(getString(R.string.app_time_util, arr[0] + "", arr[1] + ""));
    }

    private void setPointerTextAndProgress(int pointer) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {

            default:
                break;
        }
    }

    //设置睡眠时间
    private void setUpdateTime(String time) {
        mDateStr = time;
        updateTime.setText(UIUtils.getString(R.string.light_at_sleep) + time);
    }

    //设置时分
    private void setHourMinute(int color, int hour, int minute) {
        tv_current_hour.setTextColor(color);
        tv_current_hour.setText(hour + "");
        tv_current_min.setText(minute + "");
        tv_current_min.setTextColor(color);
    }

    private void setSumHourMinute(int hour, int minute) {
        tv_sum_hour.setText(hour + "");
        tv_sum_min.setText(minute + "");
    }

    private void setEarlyLater(int laterTime, int earlyTime, int earlyThanHour, int earlyThanMinute) {
    }

    private void setPieData(int wakeUpTotalTime, int eyeMoveTotalTime, int sleepTotalTime, int deepSleepTotalTime, boolean hasData) {
        int totalTime = getTotalTime(wakeUpTotalTime, eyeMoveTotalTime, sleepTotalTime, deepSleepTotalTime);


        if (pieChartView != null) {
            List<PieChartData> pieChartDatas = new ArrayList<>();
            if (totalTime == 0) {
                //setHourMinute(0, 0);
                setSumHourMinute(0, 0);
                pieChartDatas.add(new PieChartData(1, UIUtils.getColor(R.color.common_rope_time_color)));//绿色
            } else {
                //setHourMinute(totalTime / 60, totalTime % 60);
                setSumHourMinute(totalTime / 60, totalTime % 60);
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


    private int getTotalTime(int wakeUpTotalTime, int eyeMoveTotalTime, int sleepTotalTime, int deepSleepTotalTime) {
        return wakeUpTotalTime + sleepTotalTime + deepSleepTotalTime + eyeMoveTotalTime;
    }

    /*private float getPiePercent(int time, int totalTime) {
        if (time == 0) {
            return 0;
        }
        float pre = (float) time / totalTime * 100.f;
        if (pre < 1) {
            pre = 1;
        }
        return (float) pre;
    }
*/
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

    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public void setContinousBarChartViewW311(int[] sleepArry) {

        Logger.myLog(TAG,"-------睡眠字段="+sleepArry.length+Arrays.toString(sleepArry));
      /*  if (true) {
            return;
        }*/

        ContinousBarChartTotalEntity barChartTotalEntity = new ContinousBarChartTotalEntity();
        List<ContinousBarChartEntity> datas = new ArrayList<>();

        //对数据进行过滤
        ArrayList<Integer> sleepArrlist = new ArrayList<>();
        int startIndex = 0, endIndex = 0;
      /*  for (int i = 0; i <= sleepArry.length - 1; i++) {
            Logger.myLog("str:i|:" + i + ",sleepArry:" + sleepArry[i]);
        }*/
        for (int i = 0; i <= sleepArry.length - 1; i++) {
            barChartTotalEntity.startTime = "20:00";
            barChartTotalEntity.starCalendar = Calendar.getInstance();
            barChartTotalEntity.endTime = "20:00";
            if (sleepArry[i] == 0) {
                continue;
            } else {
                //计算开始时间
                startIndex = i;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.add(Calendar.MINUTE, startIndex);
                startIndex = startIndex;
                String startTimeStr = DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "HH:mm");
                barChartTotalEntity.startTime = startTimeStr ;
                barChartTotalEntity.starCalendar = calendar;
                String str = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
                //Logger.myLog("str:" + str + ",startTime:" + barChartTotalEntity.startTime + ":index:" + i + "sleepArry[]:" + sleepArry[startIndex]);

                break;

            }
        }
        for (int i = sleepArry.length - 1; i >= startIndex; i--) {
            if (sleepArry[i] == 0) {
                continue;
            } else {
                //计算开始时间
                if (i == sleepArry.length - 1) {
                    endIndex = i;
                } else {
                    endIndex = i + 1;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, sleepArry.length == 1440 ? 20 : 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.add(Calendar.MINUTE, -(sleepArry.length - endIndex + 1));
                String endTimeStr = DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "HH:mm");
                Logger.myLog(TAG,"----结束时间="+endTimeStr);
                barChartTotalEntity.endTime = endTimeStr;
                String str = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
                // Logger.myLog("str:" + str + ",barChartTotalEntity.endTime:" + barChartTotalEntity.endTime + ":endIndex:" + i + ":sleepArry.length:" + sleepArry.length);
                //barChartTotalEntity.endTime = DateUtil.dataToString(new Date(calendar.getTimeInMillis() - endIndex * 1000), "hh:mm");
                //endIndex = endIndex - 1;
               /* if (i == sleepArry.length - 1) {
                    endIndex = i;
                } else {
                    endIndex = endIndex - 1;
                }*/
                // endIndex = endIndex - 1;
                break;
            }
        }
        if (DeviceTypeUtil.isContainWrishBrand(currentDeviceType)) {

            for (int i = startIndex; i < endIndex; i++) {//i 最大为720
                int s = 0;
                if (sleepArry[i] == 250) {
                    s = 4;
                } else if (sleepArry[i] == 252) {
                    s = 2;
                } else if (sleepArry[i] == 251) {
                    s = 2;
                } else if (sleepArry[i] == 253) {
                    s = 3;
                }
                datas.add(new ContinousBarChartEntity(1, 200, s));
            }
        } else if (DeviceTypeUtil.isContainWatch(currentDeviceType)) {


            for (int i = startIndex; i < endIndex; i++) {//i 最大为720
                int s = 0;
                if (sleepArry[i] == 250) {
                    s = 3;//深睡
                } else if (sleepArry[i] == 252) {
                    // s = 1;//快速眼动
                    s = 2;//浅睡
                } else if (sleepArry[i] == 251) {
                    s = 2;//浅睡
                } else if (sleepArry[i] == 253) {


                    s = 4;//清醒
                }
                datas.add(new ContinousBarChartEntity(1, 200, s));
            }
        } else if (DeviceTypeUtil.isContaintW81(currentDeviceType)) {
            /**
             *  public static final int SLEEP_STATE_RESTFUL = 2;
             *     public static final int SLEEP_STATE_LIGHT = 1;
             *     public static final int SLEEP_STATE_SOBER = 0;
             */
            for (int i = startIndex; i <= endIndex; i++) {//i 最大为720
                int s = 0;
                if (sleepArry[i] == 4) {
                    s = 4;//清醒
                } else if (sleepArry[i] == CRPSleepInfo.SLEEP_STATE_LIGHT) {
                    s = 2;//浅睡
                } else if (sleepArry[i] == CRPSleepInfo.SLEEP_STATE_RESTFUL) {
                    s = 3;//深睡
                }
                datas.add(new ContinousBarChartEntity(1, 200, s));
            }
        }
        if (datas.size() == 0) {
            for (int i = 0; i < 1440; i++) {
                datas.add(new ContinousBarChartEntity(1, 200, 0));
            }
        }


        //barChartTotalEntity.startTime = "20";
        //barChartTotalEntity.endTime = "20";
        barChartTotalEntity.continousBarChartEntitys = datas;

        continousBarChartView.setData(barChartTotalEntity, "分组", "数量");
        continousBarChartView.startAnimation();

       // continousBarChartView.setisDrawBorder();
        continousBarChartView.setOnItemBarClickListener(new ContinousBarChartView.OnItemBarClickListener() {
            @Override
            public void onClick(int color, int position, int hour, int minute) {
                Log.e(TAG,"-----点击时间段="+hour+"minute="+minute);
                setHourMinute(color, hour, minute);
            }

            @Override
            public void onSelectSleepState(String value) {
                Log.e(TAG,"-----点击内容="+value);
                tv_current_state.setText(value);
            }
        });

    }

    public void setContinousBarChartViewW81(int[] sleepArry) {

        Logger.myLog(TAG,"-------睡眠="+ Arrays.toString(sleepArry));

      /*  if (true) {
            return;
        }*/

        ContinousBarChartTotalEntity barChartTotalEntity = new ContinousBarChartTotalEntity();
        List<ContinousBarChartEntity> datas = new ArrayList<>();

        //对数据进行过滤
        ArrayList<Integer> sleepArrlist = new ArrayList<>();
        int startIndex = 0, endIndex = 0;
      /*  for (int i = 0; i <= sleepArry.length - 1; i++) {
            Logger.myLog("str:i|:" + i + ",sleepArry:" + sleepArry[i]);
        }*/
        for (int i = 0; i <= sleepArry.length - 1; i++) {
            barChartTotalEntity.startTime = "20:00";
            barChartTotalEntity.starCalendar = Calendar.getInstance();
            barChartTotalEntity.endTime = "20:00";
            if (sleepArry[i] == 0) {
                continue;
            } else {
                //计算开始时间
                startIndex = i;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.add(Calendar.MINUTE, startIndex);
                startIndex = startIndex;
                barChartTotalEntity.startTime = DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "HH:mm");
                barChartTotalEntity.starCalendar = calendar;
                String str = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
                //Logger.myLog("str:" + str + ",startTime:" + barChartTotalEntity.startTime + ":index:" + i + "sleepArry[]:" + sleepArry[startIndex]);

                break;

            }
        }
        for (int i = sleepArry.length - 1; i >= startIndex; i--) {
            if (sleepArry[i] == 0) {
                continue;
            } else {
                //计算开始时间
                if (i == sleepArry.length - 1) {
                    endIndex = i;
                } else {
                    endIndex = i + 1;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.add(Calendar.MINUTE, -(sleepArry.length - (endIndex)));
                barChartTotalEntity.endTime = DateUtil.dataToString(new Date(calendar.getTimeInMillis()), "HH:mm");
                String str = DateUtil.dataToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
                // Logger.myLog("str:" + str + ",barChartTotalEntity.endTime:" + barChartTotalEntity.endTime + ":endIndex:" + i + ":sleepArry.length:" + sleepArry.length);
                //barChartTotalEntity.endTime = DateUtil.dataToString(new Date(calendar.getTimeInMillis() - endIndex * 1000), "hh:mm");
                //endIndex = endIndex - 1;
                if (i == sleepArry.length - 1) {
                    endIndex = i;
                } else {
                    endIndex = endIndex - 1;
                }
                // endIndex = endIndex - 1;
                break;
            }
        }
        if (DeviceTypeUtil.isContainWrishBrand(currentDeviceType)) {

            for (int i = startIndex; i <= endIndex; i++) {//i 最大为720
                int s = 0;
                if (sleepArry[i] == 250) {
                    s = 4;
                } else if (sleepArry[i] == 252) {
                    s = 2;
                } else if (sleepArry[i] == 251) {
                    s = 2;
                } else if (sleepArry[i] == 253) {
                    s = 3;
                }
                datas.add(new ContinousBarChartEntity(1, 200, s));
            }
        } else if (DeviceTypeUtil.isContainWatch(currentDeviceType)) {


            for (int i = startIndex; i <= endIndex; i++) {//i 最大为720
                int s = 0;
                if (sleepArry[i] == 250) {
                    s = 3;//清醒
                } else if (sleepArry[i] == 252) {
                    // s = 1;//快速眼动
                    s = 2;//浅睡
                } else if (sleepArry[i] == 251) {
                    s = 2;//浅睡
                } else if (sleepArry[i] == 253) {
                    s = 4;//深睡
                }
                datas.add(new ContinousBarChartEntity(1, 200, s));
            }
        } else if (DeviceTypeUtil.isContaintW81(currentDeviceType)) {
            /**
             *  public static final int SLEEP_STATE_RESTFUL = 2;
             *     public static final int SLEEP_STATE_LIGHT = 1;
             *     public static final int SLEEP_STATE_SOBER = 0;
             */
            for (int i = startIndex; i <= endIndex; i++) {//i 最大为720
                int s = 0;
                if (sleepArry[i] == 4) {
                    s = 4;//清醒
                } else if (sleepArry[i] == CRPSleepInfo.SLEEP_STATE_LIGHT) {
                    s = 2;//浅睡
                } else if (sleepArry[i] == CRPSleepInfo.SLEEP_STATE_RESTFUL) {
                    s = 3;//深睡
                }
                datas.add(new ContinousBarChartEntity(1, 200, s));
            }
        }
        if (datas.size() == 0) {
            for (int i = 0; i < 1440; i++) {
                datas.add(new ContinousBarChartEntity(1, 200, 0));
            }
        }
       // continousBarChartView.setisDrawBorder();
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
        //barChartTotalEntity.startTime = "20";
        //barChartTotalEntity.endTime = "20";
        barChartTotalEntity.continousBarChartEntitys = datas;

        continousBarChartView.setData(barChartTotalEntity, "分组", "数量");
        continousBarChartView.startAnimation();
    }


    public void setPopupWindow(Context context, View view) {
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

                } else {
                    mCurrentStr = dateStr;

                    mActPresenter.getWatchDayData(dateStr, deviceId, currentDeviceType, userId);
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
        if (DeviceTypeUtil.isContaintW81(currentDeviceType)) {
            w81DataPresenter.getW81MothSleep(deviceBean.deviceID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.SLEEP), instance.getTimeInMillis());
        } else if (currentDeviceType == JkConfiguration.DeviceType.Brand_W520) {
            DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.SLEEP, currentDeviceType, BaseApp.getApp());
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
        //getMonthData(calendar);
        if (DeviceTypeUtil.isContaintW81(currentDeviceType)) {
            w81DataPresenter.getW81MothSleep(deviceBean.deviceID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.SLEEP), calendar.getTimeInMillis());
        } else {
            DeviceHistotyDataPresenter.getMonthData(calendar, JkConfiguration.WatchDataType.SLEEP, currentDeviceType, BaseApp.getApp());

        }
        calendar.add(Calendar.MONTH, 1);


    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

    private void initDatePopMonthTitle() {
        Calendar calendar = calendarview.getDate();
        // LogUtil.log(TAG + " initDatePopMonthTitle:" + dateFormat.format(calendar.getTime()));
        tvDatePopTitle.setText(dateFormat.format(calendar.getTime()));
    }

    /**
     * @param pw     popupWindow
     * @param anchor v
     * @param xoff   x轴偏移
     * @param yoff   y轴偏移
     */
    public static void showAsDropDown(final PopupWindow pw, final View anchor, final int xoff, final int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            pw.setHeight(height);
            pw.showAsDropDown(anchor, xoff, yoff);
        } else {
            pw.showAsDropDown(anchor, xoff, yoff);
        }
    }

    long lastClickTime;

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 500;
    }

    private boolean isFirst;

    @Override
    protected void initEvent() {
        isFirst = true;

        titleBarView.setOnHistoryClickListener(new TitleBarView.OnHistoryClickListener() {
            @Override
            public void onHistoryClicked(View view) {
                if(isFastDoubleClick())
                    return;
                setPopupWindow(ActivityWatchSleep.this, view);

                int time = (int) (System.currentTimeMillis() / 1000);
                initDatePopMonthTitle();
                calendarview.setActiveC(TimeUtil.second2Millis(time));
                calendarview.setMonthDataListen(ActivityWatchSleep.this);
                calendarview.setTimeInMillis(TimeUtil.second2Millis(time));
            }
        });
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
        rlLitterSleep.setOnClickListener(this);
    }

    //分享   awakeSleepTime, fallAsleepTime, fallAsleepTime, deepTime
    // int awakeSleepTime = watchSleepDayData.getAwakeSleepTime();
    //            deepTime = watchSleepDayData.getDeepSleepTime();
    //            int lightLV2SleepTime = watchSleepDayData.getLightLV2SleepTime();
    private void startShartActivity() {
//       Intent intent = new Intent(context, ShareActivity.class);
        Intent intent = new Intent(context, NewShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.SLEEP);
        ShareBean shareBean = new ShareBean();
        //睡眠时长
        String deepTimehour = CommonDateUtil.formatTwoStr(deepTime / 60);
        String deepTimeminute = CommonDateUtil.formatTwoStr(deepTime % 60);
        String sleepTimehour = CommonDateUtil.formatTwoStr(sleepTime / 60);
        String sleepTimeminute = CommonDateUtil.formatTwoStr(sleepTime % 60);
        String lightTimeHour = CommonDateUtil.formatTwoStr(fallAsleepTime / 60);
        String lightTimeMin = CommonDateUtil.formatTwoStr(fallAsleepTime % 60);
        String awakeHour = CommonDateUtil.formatTwoStr(awakeTime / 60);
        String awakeMin = CommonDateUtil.formatTwoStr(awakeTime % 60);

        //new
        shareBean.one = "" + deepTime;
        shareBean.two = "" + fallAsleepTime;
        shareBean.three = "" + awakeTime;
        shareBean.centerValue = "" + sleepTime;

//        //深睡时长
//        if (deepTime >= 60) {
//            shareBean.one = String.format(context.getString(R.string.app_sleep_time_hour_min), deepTimehour,
//                    deepTimeminute);
//        } else {
//            shareBean.one = String.format(context.getString(R.string.app_sleep_time_min), deepTimeminute + "");
//        }
//        //浅睡
//        if (fallAsleepTime >= 60) {
//            shareBean.two = String.format(context.getString(R.string.app_sleep_time_hour_min), lightTimeHour,
//                    lightTimeMin);
//        } else {
//            shareBean.two = String.format(context.getString(R.string.app_sleep_time_min), lightTimeMin + "");
//        }
//
//        //清醒
//        if (awakeTime >= 60) {
//            shareBean.three = String.format(context.getString(R.string.app_sleep_time_hour_min), awakeHour,
//                    awakeMin);
//        } else {
//            shareBean.three = String.format(context.getString(R.string.app_sleep_time_min), awakeMin + "");
//        }
        //睡眠时长
//        shareBean.centerValue = String.format(context.getString(R.string.app_sleep_time_hour_min),
//                sleepTimehour, sleepTimeminute);
        //日期
        shareBean.time = mDateStr;//DateUtils.getYMD(mSleep_Sleepace_DataModel.getTimestamp() * 1000);
        intent.putExtra(JkConfiguration.FROM_BEAN, shareBean);
        startActivity(intent);
    }

    private int deepTime = 0;
    private int sleepTime = 0;
    private int fallAsleepTime = 0;
    private int awakeTime = 0;
    private Sleep_Sleepace_DataModel mSleep_Sleepace_DataModel;

    @Override
    protected void initHeader() {
        //StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
        // StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.common_bg));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_litter_sleep:
                Intent intent = new Intent(context, ActivityWatchLitterSleep.class);
                if (mWatchSleepDayData != null) {
                    intent.putExtra("mWatchSleepDayData", mWatchSleepDayData);
                    startActivity(intent);
                } else {
                    mWatchSleepDayData = new WatchSleepDayData();
                    mWatchSleepDayData.setSporadicNapSleepTime(0);
                    mWatchSleepDayData.setSporadicNapSleepTimes(1);
                    if (mWatchSleepDayData != null) {
                        intent.putExtra("mWatchSleepDayData", mWatchSleepDayData);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    @Override
    public void successMonthDate(ArrayList<String> strDates) {
        calendarview.setSummary(strDates);
    }

    @Override
    public void successDayDate(WatchSleepDayData watchSleepDayData) {

        Logger.myLog(TAG,"------睡眠查找="+watchSleepDayData.toString());

        if (!TextUtils.isEmpty(watchSleepDayData.getDateStr())) {
            mCurrentStr = watchSleepDayData.getDateStr();
            //UI展示
            mWatchSleepDayData = watchSleepDayData;
            setUpdateTime(watchSleepDayData.getDateStr());
            sleepTime = watchSleepDayData.getTotalSleepTime();
            awakeTime = watchSleepDayData.getAwakeSleepTime();
            deepTime = watchSleepDayData.getDeepSleepTime();
            int lightLV2SleepTime = watchSleepDayData.getLightLV2SleepTime();
            fallAsleepTime = watchSleepDayData.getLightLV1SleepTime() + lightLV2SleepTime;
            String sporadicNapSleepTimeStr = watchSleepDayData.getSporadicNapSleepTimeStr();
            int sporadicNapSleepTime = watchSleepDayData.getSporadicNapSleepTime();
            int sporadicNapSleepTimes = watchSleepDayData.getSporadicNapSleepTimes();
            //总睡眠时间
            setHourMinute(0xFF4DDA64, sleepTime/60, sleepTime % 60);
            //开始和结束时间
            tv_current_state.setText("");
        //    tv_current_state.setText(value);



            //图表
            if (DeviceTypeUtil.isContainW81(currentDeviceType)) {
                Logger.myLog("  successDayDate   sleep");
                continousBarChartView.setCurrentType(1);
                setContinousBarChartViewW81(watchSleepDayData.getSleepArry());
            } else {
                continousBarChartView.setCurrentType(0);
                setContinousBarChartViewW311(watchSleepDayData.getSleepArry());
            }
            //setContinousBarChartViewW311(watchSleepDayData.getSleepArry());

            //比例文字
            setSleepSummary(awakeTime, fallAsleepTime, fallAsleepTime, deepTime);

            //饼状图
            if (awakeTime == 0 && fallAsleepTime == 0 && deepTime == 0) {
                setPieData(0, 0, 0, 0, true);
            } else {
                setPieData(awakeTime, 0, fallAsleepTime, deepTime, true);
            }

            //零星小睡
            String content = null;
            if (sporadicNapSleepTimes == 0) {
                content = "0" + UIUtils.getString(R.string.unit_minute);
            } else if (sporadicNapSleepTimes == 1) {
                content = sporadicNapSleepTimeStr + " " + sporadicNapSleepTime + UIUtils.getString(R.string.unit_minute);
            } else if (sporadicNapSleepTimes > 1) {
                content = sporadicNapSleepTimes + UIUtils.getString(R.string.unit_times) + " " + sporadicNapSleepTime + UIUtils.getString(R.string.unit_minute);
            }
            tvSporadicNaps.setText(content);

            setPointerTextAndProgress(80);
            setEarlyLater(58, 54, 2, 47);
            setTotalTime(70, 113, 40, 124);
        } else {
            deepTime = 0;
            sleepTime = 0;
            fallAsleepTime = 0;
            awakeTime = 0;
            int[] m1440Result = new int[1440];
            for (int j = 0; j < 1440; j++) {
                m1440Result[j] = 0;
            }
            setUpdateTime(mCurrentStr);
            //总睡眠时间
            //总睡眠时间
            setHourMinute(0xFF4DDA64, 0, 0);
            //开始和结束时间
            tv_current_state.setText("");
            //图表
            if (DeviceTypeUtil.isContainW81(currentDeviceType)) {
                Logger.myLog("  successDayDate   sleep");
                setContinousBarChartViewW81(m1440Result);
            } else {
                setContinousBarChartViewW311(m1440Result);
            }

            //比例文字
            setSleepSummary(0, 0, 0, 0);
            //饼状图
            setPieData(0, 0, 0, 0, true);
            //零星小睡
            String content = "0" + UIUtils.getString(R.string.unit_minute);
            tvSporadicNaps.setText(content);
        }
    }

    @Override
    public void getMontData(String strYearAndMonth) {
        mActPresenter.getMonthDataStrDate(strYearAndMonth, currentDeviceType, deviceBean.deviceName, userId);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
    }
}

