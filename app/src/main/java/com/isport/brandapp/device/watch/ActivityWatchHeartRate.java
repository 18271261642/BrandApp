package com.isport.brandapp.device.watch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_24H_hr_SettingModel;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_w311_hrModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.bean.http.WristbandHrHeart;
import com.isport.brandapp.home.presenter.DeviceHistotyDataPresenter;
import com.isport.brandapp.home.presenter.W81DataPresenter;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.bean.StateBean;
import com.isport.brandapp.device.bracelet.braceletPresenter.HrSettingPresenter;
import com.isport.brandapp.device.bracelet.view.HrSettingView;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.share.NewShareActivity;
import com.isport.brandapp.device.share.PackageUtil;
import com.isport.brandapp.device.share.ShareBean;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.device.sleep.calendar.Cell;
import com.isport.brandapp.device.sleep.calendar.WatchPopCalendarView;
import com.isport.brandapp.device.watch.adapter.HrListAdapter;
import com.isport.brandapp.device.watch.bean.HrHitoryDetail;
import com.isport.brandapp.device.watch.presenter.Device24HrPresenter;
import com.isport.brandapp.device.watch.presenter.WatchHeartRatePresenter;
import com.isport.brandapp.device.watch.view.Device24HrView;
import com.isport.brandapp.device.watch.view.WatchHeartRateView;
import com.isport.brandapp.dialog.HeartStrongDialog;
import com.isport.brandapp.sport.run.LanguageUtil;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.util.ShareHelper;
import com.isport.brandapp.wu.util.HeartRateConvertUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bike.gymproject.viewlibray.AkrobatNumberTextView;
import bike.gymproject.viewlibray.LineChartView;
import bike.gymproject.viewlibray.LineRecChartView;
import bike.gymproject.viewlibray.LineScrollChartView;
import bike.gymproject.viewlibray.WatchHourMinuteView;
import bike.gymproject.viewlibray.bean.HrlineBean;
import bike.gymproject.viewlibray.chart.HourMinuteData;
import bike.gymproject.viewlibray.chart.LineChartEntity;
import bike.gymproject.viewlibray.chart.PieChartData;
import bike.gymproject.viewlibray.chart.PieChartViewHeart;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ThreadPoolUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.UserUtils;

/**
 * @Author 李超凡
 * @Date 2019/2/26
 * @Fuction 心率，连续心率页面
 */

public class ActivityWatchHeartRate extends BaseMVPTitleActivity<WatchHeartRateView, WatchHeartRatePresenter> implements
        WatchHeartRateView, View.OnClickListener, WatchPopCalendarView.MonthDataListen, HrSettingView, Device24HrView, UMShareListener {


    TextView tvDetect;
    LineChartView lineChartView;
    LineScrollChartView lineScrollChartView;
    LineRecChartView lineChartView1, lineChartView2, lineChartView3, lineChartView4, lineChartView5, lineChartView6;
    TextView tvTime;
    Handler handler = new Handler();
    AkrobatNumberTextView tvHrMax, tvHrMin, tvHrAvg;
    AkrobatNumberTextView tv_sum_avg_hr, tv_sum_min_hr, tv_sum_max_hr;
    private DeviceBean deviceBean;
    private String mDateStr;
    private String mCurrentStr;
    private HrSettingPresenter hrSettingPresenter;
    private Device24HrPresenter device24HrPresenter;
    int curentInter = 1;


    private ArrayList<HrHitoryDetail> details = new ArrayList<>();
    HrListAdapter adapter;

    private int mCurrentType;
    private String mCurrentDeviceId;
    W81DataPresenter w81DataPresenter;
    StateBean stateBean;
    private PieChartViewHeart pieChartViewHeart;
    private WatchHourMinuteView view_limit, view_anaerobic_exercise, view_aerobic_exercise, view_fat_burning_exercise, view_warm_up, view_leisure;

    private NestedScrollView nestedScrollView;

    private UserInfoBean userInfoBean;
    private int age;
    private String sex;


    private File picFile = null;
    private RelativeLayout llHistoryShare;
    private ImageView ivQQ, ivWechat, ivWebo, ivFriend, ivFacebook, ivShareMore;
    //英文版的分享布局
    private LinearLayout sharingLayoutEn;
    //中文版分享布局
    private LinearLayout sharingLayoutCN;
    private ImageView iv_help;

    private RecyclerView recHr;
    private TextView tv_select_times;
    private AkrobatNumberTextView tv_select_hr_value;

    private TextView heartIntervalTv;


    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_watch_heart_rate;
    }


    @Override
    protected WatchHeartRatePresenter createPresenter() {
        hrSettingPresenter = new HrSettingPresenter(this);
        w81DataPresenter = new W81DataPresenter(this);
        device24HrPresenter = new Device24HrPresenter(this);
        return new WatchHeartRatePresenter(this);
    }

    @Override
    protected void initView(View view) {
        heartIntervalTv = view.findViewById(R.id.heartIntervalTv);

        recHr = view.findViewById(R.id.rec_hr);
        tv_select_times = view.findViewById(R.id.tv_select_times);
        tv_select_hr_value = view.findViewById(R.id.tv_select_hr_value);

        lineChartView1 = view.findViewById(R.id.lineChartView1);
        lineChartView2 = view.findViewById(R.id.lineChartView2);
        lineChartView3 = view.findViewById(R.id.lineChartView3);
        lineChartView4 = view.findViewById(R.id.lineChartView4);
        lineChartView5 = view.findViewById(R.id.lineChartView5);
        lineChartView6 = view.findViewById(R.id.lineChartView6);
        tvDetect = view.findViewById(R.id.tv_detect);
        tvTime = view.findViewById(R.id.tv_update_time);
        lineChartView = view.findViewById(R.id.lineChartView);
        lineScrollChartView = view.findViewById(R.id.lineScrollChartView);
        tvHrMin = view.findViewById(R.id.tv_min_hr);
        tvHrMax = view.findViewById(R.id.tv_max_hr);
        tvHrAvg = view.findViewById(R.id.tv_avg_hr);
        tv_sum_avg_hr = view.findViewById(R.id.tv_sum_avg_hr);
        tv_sum_min_hr = view.findViewById(R.id.tv_sum_min_hr);
        tv_sum_max_hr = view.findViewById(R.id.tv_sum_max_hr);
        pieChartViewHeart = findViewById(R.id.pieChartView);
        nestedScrollView = findViewById(R.id.scrollveiw);
        iv_help = findViewById(R.id.iv_help);


        sharingLayoutEn = view.findViewById(R.id.sharingLayoutEn);
        sharingLayoutCN = view.findViewById(R.id.sharingLayoutCN);

        ivQQ = view.findViewById(R.id.iv_qq);
        ivWebo = view.findViewById(R.id.iv_weibo);
        ivWechat = view.findViewById(R.id.iv_wechat);
        ivFriend = view.findViewById(R.id.iv_friend);
        ivShareMore = view.findViewById(R.id.iv_more);
      //  sharingLayoutCN = view.findViewById(R.id.view_zh);

        ivFacebook = view.findViewById(R.id.iv_facebook_en);
        llHistoryShare = view.findViewById(R.id.ll_history_share);


        view_limit = findViewById(R.id.view_limit);
        view_anaerobic_exercise = findViewById(R.id.view_anaerobic_exercise);
        view_aerobic_exercise = findViewById(R.id.view_aerobic_exercise);
        view_fat_burning_exercise = findViewById(R.id.view_fat_burning_exercise);
        view_warm_up = findViewById(R.id.view_warm_up);
        view_leisure = findViewById(R.id.view_leisure);

      //  ivShareMore.setVisibility(View.GONE);
    }

    private void setLineDataAndShow(ArrayList<Integer> hrList) {
        int limint = 0, anaerobic_exercise = 0, aerobic_exercise = 0, fat_burning_exercise = 0, warm_up = 0, leisure = 0;
        int maxHr = 0, minHr = 0;


        if (hrList == null || hrList.size() == 0) {
            setNoPieData();
            setSleepSummary(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);
        } else {


            for (int i = 0; i < hrList.size(); i++) {
                int hrValue = hrList.get(i);
                if (hrValue < 30) {
                    continue;
                }
                int point = HeartRateConvertUtils.hearRate2Point(hrValue, HeartRateConvertUtils.getMaxHeartRate(age, sex));
                //Logger.myLog("hrList.get(i)" + hrValue + "HeartRateConvertUtils.getMaxHeartRate(age):" + HeartRateConvertUtils.getMaxHeartRate(age, sex) + "age:" + age + "point:" + point);
                switch (point) {
                    case 0:
                        leisure++;
                        break;
                    case 1:
                        warm_up++;
                        break;
                    case 2:
                        fat_burning_exercise++;
                        break;
                    case 3:
                        aerobic_exercise++;
                        break;
                    case 4:
                        anaerobic_exercise++;
                        break;
                    case 5:
                        limint++;
                        break;
                }
            }
            setPieData(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);
            // setPieData(5, 5, 5, 5, 5, 5);
            setSleepSummary(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);

        }

        //Logger.myLog("setLineDataAndShow hrList:" + hrList + ",sumTime:" + sumTime + "len:" + len);

        //311只有288个数据
        //516有1140个数据 55X系列 1140个数据
        //81系列也是5分钟一个点
    }


    private void setNoPieData() {
        if (pieChartViewHeart != null) {
            int totalTime = 1;
            List<PieChartData> pieChartDatas = new ArrayList<>();
            pieChartDatas.add(new PieChartData(getPiePercent(1, totalTime), UIUtils.getColor(R.color.color_nor)));//深黄
            pieChartViewHeart.updateData(pieChartDatas);
        }
    }


    private void setPieData(int limint, int anaerobic_exercise, int aerobic_exercise, int fat_burning_exercise, int warm_up, int leisure) {
        if (pieChartViewHeart != null) {
            int totalTime = getTotalTime(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);
            if (totalTime == 0) {
                setNoPieData();
            } else {
                List<PieChartData> pieChartDatas = new ArrayList<>();
                pieChartDatas.add(new PieChartData(getPiePercent(limint, totalTime), UIUtils.getColor(R.color.color_limit)));//深黄
                pieChartDatas.add(new PieChartData(getPiePercent(anaerobic_exercise, totalTime), UIUtils.getColor(R.color.color_anaerobic_exercise)));//橘黄
                pieChartDatas.add(new PieChartData(getPiePercent(aerobic_exercise, totalTime), UIUtils.getColor(R.color.color_aerobic_exercise)));//绿色
                pieChartDatas.add(new PieChartData(getPiePercent(fat_burning_exercise, totalTime), UIUtils.getColor(R.color.color_fat_burning_exercise)));//蓝色
                pieChartDatas.add(new PieChartData(getPiePercent(warm_up, totalTime), UIUtils.getColor(R.color.color_warm_up)));//蓝色
                pieChartDatas.add(new PieChartData(getPiePercent(leisure, totalTime), UIUtils.getColor(R.color.color_leisure)));//蓝色
                pieChartViewHeart.updateData(pieChartDatas);
            }
        }
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

    private int getTotalTime(int limint, int anaerobic_exercise, int aerobic_exercise, int fat_burning_exercise, int warm_up, int leisure) {
        return limint + anaerobic_exercise + aerobic_exercise + fat_burning_exercise + warm_up + leisure;
    }


    //时长最小单位为分钟
    private void setSleepSummary(int limint, int anaerobic_exercise, int aerobic_exercise, int fat_burning_exercise, int warm_up, int leisure) {
        view_limit.setData(new HourMinuteData(UIUtils.getColor(R.color.color_limit), UIUtils.getString(R.string.heart_limit), UIUtils.getString(R.string.no_data), DateUtil.getFormatTimehhmmss(deviceBean.deviceType == 812 ? limint * 5:limint), "1"));
        view_anaerobic_exercise.setData(new HourMinuteData(UIUtils.getColor(R.color.color_anaerobic_exercise), UIUtils.getString(R.string.heart_anaerobic_exercise), UIUtils.getString(R.string.no_data), DateUtil.getFormatTimehhmmss(mCurrentType == 812 ? anaerobic_exercise*5 : anaerobic_exercise), "1"));
        view_aerobic_exercise.setData(new HourMinuteData(UIUtils.getColor(R.color.color_aerobic_exercise), UIUtils.getString(R.string.heart_aerobic_exercise), UIUtils.getString(R.string.no_data), DateUtil.getFormatTimehhmmss(mCurrentType == 812 ? aerobic_exercise*5 : aerobic_exercise), "1"));
        view_fat_burning_exercise.setData(new HourMinuteData(UIUtils.getColor(R.color.color_fat_burning_exercise), UIUtils.getString(R.string.heart_fat_burning_exercise), UIUtils.getString(R.string.no_data), DateUtil.getFormatTimehhmmss(mCurrentType == 812 ? fat_burning_exercise* 5 : fat_burning_exercise), "1"));
        view_warm_up.setData(new HourMinuteData(UIUtils.getColor(R.color.color_warm_up), UIUtils.getString(R.string.heart_warm_up), UIUtils.getString(R.string.no_data), DateUtil.getFormatTimehhmmss(mCurrentType == 812 ? warm_up * 5 : warm_up), "1"));
        view_leisure.setData(new HourMinuteData(UIUtils.getColor(R.color.color_leisure), UIUtils.getString(R.string.heart_leisure), UIUtils.getString(R.string.no_data), DateUtil.getFormatTimehhmmss(mCurrentType == 812 ? leisure * 5 : leisure), "1"));
    }

    @Override
    protected void initData() {
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        getIntentValue();
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(UIUtils.getString(R.string.watch_heart_rate_title));
        // titleBarView.setTitle(getResources().getString(R.string.last_sleep));
        titleBarView.setRightText("");
        titleBarView.setRightIcon(R.drawable.icon_device_share);
        titleBarView.setHistrotyIcon(R.drawable.icon_sleep_history);
        mCurrentStr = DateUtil.dataToString(new Date(), "yyyy-MM-dd");
        //需要区分设备类型是w516还是 w311

        hrSettingPresenter.getHrItem(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), mCurrentDeviceId);
        getCurrentData();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recHr.setLayoutManager(manager);
        adapter = new HrListAdapter(details);
        recHr.setAdapter(adapter);

        if (DeviceTypeUtil.isContainW81(mCurrentType)) {
            tvDetect.setVisibility(View.GONE);
        } else {
            tvDetect.setVisibility(View.GONE);
        }
        if (userInfoBean != null) {
            String birthday = userInfoBean.getBirthday();
            age = UserUtils.getAge(birthday);
            sex = userInfoBean.getGender();
        }
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                boolean isSame = false;
                for (int i = 0; i < details.size(); i++) {
                    if (details.get(i).isSelect()) {
                        if (position == i) {
                            isSame = true;
                            break;
                        }
                    }
                    details.get(i).setSelect(false);
                }
                if (!isSame) {
                    details.get(position).setSelect(true);
                    drawLine(details.get(position));
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void getIntentValue() {
        deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);
        if (deviceBean != null) {
            mCurrentDeviceId = deviceBean.deviceID;
            mCurrentType = deviceBean.currentType;

            if(DeviceTypeUtil.isContainW81(mCurrentType)){
                heartIntervalTv.setText(String.format(UIUtils.getString(R.string.activity_hr_history_exp),5)+"");
            }else{
                heartIntervalTv.setText(String.format(UIUtils.getString(R.string.activity_hr_history_exp),1)+"");
            }
        } else {
            mCurrentDeviceId = "";
            mCurrentType = 0;
        }
    }

    private void getCurrentData() {
        // getTodayData();
        mActPresenter.getLastHrDetailData(mCurrentType, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), mCurrentDeviceId);
    }

    private void getTodayData() {
        String todayYYYYMMDD = TimeUtils.getTodayYYYYMMDD();
        setUpdateTime(todayYYYYMMDD);
        mActPresenter.getDayHrDetailData(todayYYYYMMDD, mCurrentType, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), mCurrentDeviceId);
    }

    public void drawlineDef() {
        tv_select_times.setText(UIUtils.getString(R.string.no_data));
        tv_select_hr_value.setText(UIUtils.getString(R.string.no_data));
        setHrValue(tv_select_hr_value, 0);
        setTextValue(0, 0, 0);
        int maxHr = 0;
        int minHr = 0;
        List<LineChartEntity> datas = new ArrayList<>();
        datas.add(new LineChartEntity(0, 1, (float) (0.0f)));
        lineChartView.setData(datas, true, maxHr, minHr);
        lineScrollChartView.setData(datas, true, maxHr, minHr, 0, 0);


        ArrayList<HrlineBean> lists = HeartRateConvertUtils.pointToheartRateR(age, sex);


        lineChartView1.setData(datas, true, maxHr, minHr, lists.get(0));
        lineChartView2.setData(datas, true, maxHr, minHr, lists.get(1));
        lineChartView3.setData(datas, true, maxHr, minHr, lists.get(2));
        lineChartView4.setData(datas, true, maxHr, minHr, lists.get(3));
        lineChartView5.setData(datas, true, maxHr, minHr, lists.get(4));
        lineChartView6.setData(datas, true, maxHr, minHr, lists.get(5));
    }


    public void drawLine(HrHitoryDetail detail) {
        if (detail.getInvert() == 0) {
            detail.setInvert(1);
        }
        tv_select_times.setText(UIUtils.getString(R.string.hr_select_times) + detail.getStartTime() + "~" + detail.getEndTime());
        tv_select_hr_value.setText(detail.getLists().get(0) + "");
        setHrValue(tv_select_hr_value, detail.getLists().get(0));

        if (details.size() != 1) {
            setTextValue(detail.getMaxHr(), detail.getMinHr(), detail.getAvgHr());
        } else {
            setTextValue(detail.getMaxHr(), detail.getMinHr(), sumAllAvgHr);
        }
        int maxHr = detail.getMaxHr();
        int minHr = detail.getMinHr();
        List<LineChartEntity> datas = new ArrayList<>();
        for (int i = 0; i < detail.getLists().size(); i++) {
            datas.add(new LineChartEntity(detail.getStartIndex() + i, detail.getInvert(), (float) (detail.getLists().get(i))));
        }
        //设置Y轴刻度线
        lineChartView.setData(datas, true, maxHr, minHr);
        //设置数据
        lineScrollChartView.setData(datas, true, maxHr, minHr, detail.getStartIndex(), detail.getEndIndex());


        ArrayList<HrlineBean> lists = HeartRateConvertUtils.pointToheartRateR(age, sex);

        lineChartView1.setData(datas, true, maxHr, minHr, lists.get(0));
        lineChartView2.setData(datas, true, maxHr, minHr, lists.get(1));
        lineChartView3.setData(datas, true, maxHr, minHr, lists.get(2));
        lineChartView4.setData(datas, true, maxHr, minHr, lists.get(3));
        lineChartView5.setData(datas, true, maxHr, minHr, lists.get(4));
        lineChartView6.setData(datas, true, maxHr, minHr, lists.get(5));
    }


    public void drawLine(ArrayList<Integer> hrList, int maxHr, int minHr, int len) {
        int startIndex = -1;
        int endIndex = -1;
        if (len == 0) {
            len = 1;
        }
        startIndex = -1;
        endIndex = -1;
        int listLen = 1440 / len;
        List<LineChartEntity> datas = new ArrayList<>();
        if (hrList == null || hrList.size() == 0) {
            for (int i = 0; i < listLen; i++) {
                datas.add(new LineChartEntity(String.valueOf(i), (float) (0)));
            }
        } else {
            //当hrList的size小于1440时，要填充为1440,当天的后半段未到的时间全部绘制为0
            int size = hrList.size();
            if (size < listLen) {
                for (int i = 0; i < listLen - size; i++) {
                    hrList.add(0);
                }
            }
            for (int i = 0; i < hrList.size(); i++) {
                datas.add(new LineChartEntity(String.valueOf(i), (float) (hrList.get(i))));
            }
            for (int i = 0; i < hrList.size(); i++) {
                if (hrList.get(i) != 0) {
                    startIndex = i;
                    break;
                }
            }
            for (int i = hrList.size() - 1; i >= 0; i--) {
                if (hrList.get(i) != 0) {
                    endIndex = i;
                    break;
                }
            }
        }
        Logger.myLog("datas == isContainWatch" + datas.size());
        setLineDataAndShow(hrList);
        lineChartView.setData(datas, true, maxHr, minHr);
        lineScrollChartView.setData(datas, true, maxHr, minHr, startIndex, endIndex);
        // lineChartView.startAnimation(1000);


        ArrayList<HrlineBean> lists = HeartRateConvertUtils.pointToheartRateR(age, sex);


        lineChartView1.setData(datas, true, maxHr, minHr, lists.get(0));
        lineChartView2.setData(datas, true, maxHr, minHr, lists.get(1));
        lineChartView3.setData(datas, true, maxHr, minHr, lists.get(2));
        lineChartView4.setData(datas, true, maxHr, minHr, lists.get(3));
        lineChartView5.setData(datas, true, maxHr, minHr, lists.get(4));
        lineChartView6.setData(datas, true, maxHr, minHr, lists.get(5));
    }


    int sumAllAvgHr;

    private void setLineDataAndShow(ArrayList<Integer> hrList, int maxHr, int minHr, int len) {
        if (hrList == null) {
            hrList = new ArrayList<>();

        }
        hrList.add(0);
        curentInter = len;

        setLineDataAndShow(hrList);
        details.clear();
        HrHitoryDetail detail;

        ArrayList<Integer> tempList = new ArrayList<>();

        int startIndex = -1;
        int endIndex = -1;
        int hour;
        int min;

        //数据进行分组
        for (int i = 0; i < hrList.size(); i++) {
            if (hrList.get(i) >= 30) {
                if (startIndex == -1) {
                    startIndex = i;
                }
                tempList.add(hrList.get(i));
            }
            if (hrList.get(i) < 30) {
                if (startIndex != -1) {
                    i--;
                    endIndex = i;
                    detail = new HrHitoryDetail();
                    detail.setStartIndex(startIndex);
                    detail.setEndIndex(endIndex);
                    detail.setLists(tempList);
                    if (curentInter == 0) {
                        curentInter = 1;
                    }
                    detail.setInvert(curentInter);

                    if (startIndex == 0) {
                        hour = 0;
                        min = 0;
                    } else {
                        hour = startIndex * detail.getInvert() / 60;
                        min = startIndex * detail.getInvert() % 60;
                    }
                    detail.setStartTime(String.format("%02d", hour) + ":" + String.format("%02d", min));
                    int endTimeInteger = endIndex * detail.getInvert();
                    //只有一个点
//                    if(startIndex * detail.getInvert() == endTimeInteger){
//                        endTimeInteger+=detail.getInvert();
//                    }

                    endTimeInteger+=detail.getInvert();

                    hour = endTimeInteger / 60;
                    min = endTimeInteger % 60;
                    detail.setEndTime(String.format("%02d", hour) + ":" + String.format("%02d", min));

                    details.add(detail);
                    tempList.clear();
                    startIndex = -1;
                    endIndex = -1;
                }
            }


        }

      /*  int sumMaxHr = 0, sumMinHr = 0, sumAvgHr = 0;
        if (details.size() > 0) {
            for (int i = 0; i < details.size(); i++) {
                if (i == 0) {
                    sumMaxHr = details.get(i).getMaxHr();
                    sumMinHr = details.get(i).getMinHr();
                }
                if (sumMaxHr < details.get(i).getMaxHr()) {
                    sumMaxHr = details.get(i).getMaxHr();
                }
                if (sumMinHr > details.get(i).getMinHr()) {
                    sumMinHr = details.get(i).getMinHr();
                }
            }
        }*/

        /*if (details.size() > 0) {
            sumAvgHr = (sumMinHr + sumMaxHr) / 2;
        }*/

        //  setSumHrValue(sumMaxHr, sumMinHr, sumAvgHr);
        adapter.notifyDataSetChanged();


        //311只有288个数据 520 307J
        //516有1140个数据 812B，（付开发的）
        //81系列也是5分钟一个点

        if (details.size() > 0) {
            details.get(0).setSelect(true);
            drawLine(details.get(0));
            Logger.myLog("details.get(0)" + details.get(0));
        } else {
            drawlineDef();
        }

    }


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);


    private boolean isFirst;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (priDialog != null) {
            if (priDialog.dialog != null) {
                if (priDialog.dialog.isShowing()) {
                    priDialog.dialog.dismiss();
                }
            }
        }
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);

    }

    long lastClickTime;

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 500;
    }

    @Override
    protected void initEvent() {
        ISportAgent.getInstance().registerListener(mBleReciveListener);
        isFirst = true;
        iv_help.setOnClickListener(this);
        lineScrollChartView.setOnlister(new LineScrollChartView.onSecletValueClick() {
            @Override
            public void onSelectValue(String value) {
                setHrValue(tv_select_hr_value, (int) Float.parseFloat(value));
                Log.e("lineScrollChartView", value);


            }
        });
        titleBarView.setOnHistoryClickListener(new TitleBarView.OnHistoryClickListener() {
            @Override
            public void onHistoryClicked(View view) {
                if(isFastDoubleClick())
                    return;
                setPopupWindow(ActivityWatchHeartRate.this, view);

                int time = (int) (System.currentTimeMillis() / 1000);


                initDatePopMonthTitle();
                calendarview.setActiveC(TimeUtil.second2Millis(time));
                calendarview.setMonthDataListen(ActivityWatchHeartRate.this);
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
                checkCameraPersiomm();
                // startShareActivity();
            }
        });
        tvDetect.setOnClickListener(this);
    }

    private void checkCameraPersiomm() {
        PermissionManageUtil permissionManage = new PermissionManageUtil(this);
        permissionManage.requestPermissionsGroup(new RxPermissions(this),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil
                        .OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        NetProgressObservable.getInstance().show();
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                // ThreadPoolUtils.getInstance().addTask(new ShootTask(scrollView,
                                // ActivityScaleReport.this, ActivityScaleReport.this));
                                picFile = getFullScreenBitmap(nestedScrollView);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetProgressObservable.getInstance().hide();
                                        llHistoryShare.setVisibility(View.VISIBLE);
                                        boolean isCN = LanguageUtil.isZH();
                                         sharingLayoutCN.setVisibility(isCN ? View.VISIBLE : View.GONE);
                                         sharingLayoutEn.setVisibility(isCN ? View.GONE : View.VISIBLE);

                                        //ivTestPic.setImageBitmap(BitmapFactory.decodeFile(picFile.getAbsolutePath()));
                                    }
                                });
                                // initLuBanRxJava(getFullScreenBitmap(scrollView));
                            }
                        });


                    }

                    @Override
                    public void onGetPermissionNo() {


                    }
                });

    }


    private BleReciveListener mBleReciveListener = new BleReciveListener() {

        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {

        }

        @Override
        public void setDataSuccess(String s) {

        }

        public boolean isBindScale() {
            if (AppConfiguration.deviceBeanList == null || AppConfiguration.deviceBeanList.size() == 0) {
                return false;
            }
            if (AppConfiguration.deviceBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                return true;
            }
            return false;
        }

        @Override
        public void receiveData(IResult mResult) {
            if (mResult != null)
                switch (mResult.getType()) {
                    case IResult.WATCH_W516_SETTING:
                        if (AppConfiguration.hasSynced) {
                            /*Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                            intentHr.putExtra(JkConfiguration.DEVICE, deviceBean.deviceName);
                            startActivity(intentHr);*/
                        }
                        break;
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


    private void startShareActivity() {
//        Intent intent = new Intent(context, ShareActivity.class);
        Intent intent = new Intent(context, NewShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.HEART_RATE);
        ShareBean shareBean = new ShareBean();
        //心率最大
        shareBean.one = maxHeartRate + "";
        //心率最低
        shareBean.two = minHeartRate + "";
        //心率平均
        shareBean.three = avgHeartRate + "";

        //今日心率
        shareBean.centerValue = currentHeartRate + "";
        //时间
        shareBean.time = (String) tvTime.getText();
        intent.putExtra(JkConfiguration.FROM_BEAN, shareBean);
        startActivity(intent);
    }


    private int currentHeartRate = 0;
    private String maxHeartRate = "0";
    private String minHeartRate = "0";
    private String avgHeartRate = "0";

    @Override
    protected void initHeader() {

    }

    /**
     * facebook分享状态回调
     */
    private final FacebookCallback facebookCallback = new FacebookCallback() {

        @Override
        public void onSuccess(Object o) {
//            SysoutUtil.out("onSuccess" + o.toString());
//            Message msg = Message.obtain();
//            msg.what = SHARE_COMPLETE;
//            mHandler.sendMessage(msg);
        }

        @Override
        public void onCancel() {
//            SysoutUtil.out("onCancel");
//            Message msg = Message.obtain();
//            msg.what = SHARE_CANCEL;
//            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(FacebookException error) {
//            SysoutUtil.out("onError");
//            ToastUtils.showToast("share error--" + error.getMessage());
//            Message msg = Message.obtain();
//            msg.what = SHARE_ERROR;
//            mHandler.sendMessage(msg);
        }
    };
    /**
     * 分享到facebook
     */
    private CallbackManager callBackManager;

    public void shareFaceBook(File file) {
        Bitmap image = BitmapFactory.decodeFile(file.getPath());
        callBackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callBackManager, facebookCallback);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);
    }


    String deviceName;
    HeartStrongDialog priDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_help:
                priDialog = new HeartStrongDialog(this, new HeartStrongDialog.OnTypeClickListenter() {
                    @Override
                    public void changeDevcieonClick(int type) {

                        switch (type) {
                            case 0:
                                finish();
                                break;
                        }
                    }
                }, age, sex);

                break;
            case R.id.iv_facebook_en:
                //判断facebook是否安装，没有按钮
                if (PackageUtil.isWxInstall(ActivityWatchHeartRate.this, PackageUtil.facebookPakage)) {
                    shareFaceBook(picFile);
                    //sharePlat(picFile, SHARE_MEDIA.FACEBOOK);
                } else {
                    ToastUtils.showToast(ActivityWatchHeartRate.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }
                break;
            case R.id.iv_wechat:
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.weichatPakage)) {

                    sharePlat(picFile, SHARE_MEDIA.WEIXIN);
                } else {
                    ToastUtil.showTextToast(this, "请安装对应软件");
                }
                break;
            case R.id.iv_friend:         //友盟,分享活动到第三方
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.weichatPakage)) {
                    sharePlat(picFile, SHARE_MEDIA.WEIXIN_CIRCLE);
                } else {
                    ToastUtil.showTextToast(this, "请安装对应软件");
                }
                break;
            case R.id.iv_qq:         //友盟,分享活动到第三方
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.qqPakage)) {
                    sharePlat(picFile, SHARE_MEDIA.QQ);
                } else {
                    ToastUtil.showTextToast(this, "请安装对应软件");
                }
                break;
            case R.id.iv_weibo:
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.weiboPakage)) {
                    sharePlat(picFile, SHARE_MEDIA.SINA);
                } else {
                    ToastUtil.showTextToast(this, "请安装对应软件");
                }
                break;
            case R.id.tv_sharing_cancle:
                llHistoryShare.setVisibility(View.GONE);
                break;
            case R.id.iv_more:
             //   llHistoryShare.setVisibility(View.GONE);
                shareFile(picFile);
                //util.checkCameraPersiomm(this, this, layoutAll, "more");
                break;

            case R.id.tv_detect:

                BaseDevice baseDevice = ISportAgent.getInstance().getCurrnetDevice();
                if (baseDevice != null) {
                    deviceName = baseDevice.deviceName;
                }
                //
                if (AppConfiguration.isConnected && (DeviceTypeUtil.isContainWatchOrBracelet())) {

                    if (AppConfiguration.hasSynced) {
                        if (DeviceTypeUtil.isContainW55X(mCurrentType)) {
                            Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                            intentHr.putExtra(JkConfiguration.DEVICE, deviceName);
                            startActivity(intentHr);
                            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, true);
                        } else if (DeviceTypeUtil.isContainWatch(mCurrentType)) {
                            Logger.myLog("stateBean:mCurrentDeviceId" + mCurrentDeviceId + "TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()):" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                            device24HrPresenter.getMessageCallState(mCurrentDeviceId, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                        } else if (DeviceTypeUtil.isContainWrishBrand(mCurrentType)) {
                            if (AppConfiguration.hasSynced) {
                                boolean isSwitch = false;
                                if (model != null) {
                                    isSwitch = model.getIsOpen();
                                } else {
                                    isSwitch = false;
                                }
                                if (!isSwitch) {
                                    PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.heartrate_w311_tip), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                                        @Override
                                        public void determine() {
                                            //需要去保存开关的状态
                                            if (model != null) {
                                                model.setDeviceId(AppConfiguration.braceletID);
                                                model.setIsOpen(true);
                                                model.setTimes(30);
                                                hrSettingPresenter.saveHrSetting(model);
                                            }
                                            ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, true);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ISportAgent.getInstance().requestBle(BleRequest.bracelet_heartRate, true, 30);
                                                }
                                            }, 500);
                                            //需要间隔500毫秒
                                            Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                                            intentHr.putExtra(JkConfiguration.DEVICE, deviceName);
                                            startActivity(intentHr);
                                        }

                                        @Override
                                        public void cancel() {

                                        }
                                    }, false);
                                } else {
                                    ISportAgent.getInstance().requestBle(BleRequest.bracelet_w311_set_automatic_HeartRate_And_Time, true);

                                    Intent intentHr = new Intent(this, ActivityWatchHeartRateIng.class);
                                    intentHr.putExtra(JkConfiguration.DEVICE, baseDevice.getDeviceName());
                                    startActivity(intentHr);
                                }
                            } else {
                                ToastUtils.showToast(context, UIUtils.getString(R.string.sync_data));
                            }
                        }
                    } else {
                        ToastUtils.showToast(context, UIUtils.getString(R.string.sync_data));
                    }
                } else {

                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.unconnected_device));
                }
                break;
        }
    }

    public void sharePlat(File file, SHARE_MEDIA share_media) {
        UMImage image = ShareHelper.getUMWeb(this, file);
        new ShareAction(this).setPlatform(share_media)
                .withMedia(image)
                .setCallback(this)
                .share();
    }

    public void shareFile(File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri = null;
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "项目包名.fileprovider"即是在清单文件中配置的authorities
                uri = FileProvider.getUriForFile(ActivityWatchHeartRate.this, getPackageName() + ".fileprovider",
                        file);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "image/*";
       /* if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }*/
        return mime;
    }


    //popwindow
    private WatchPopCalendarView calendarview;
    private ImageView ivPreDate, ivNextDate;
    private TextView tvDatePopTitle, tvBackToay;


    public void setPopupWindow(Context context, View view) {


        BaseDialog mMenuViewBirth = new BaseDialog.Builder(this)
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
        //popupWindowBirth.setHeight(DisplayUtils.dip2px(context, 150));
        view_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuViewBirth.dismiss();
            }
        });

        calendarview.setOnCellTouchListener(new WatchPopCalendarView.OnCellTouchListener() {
            @Override
            public void onTouch(Cell cell) {
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
                mCurrentStr = dateStr;
                mMenuViewBirth.dismiss();
                setUpdateTime(dateStr);
                mActPresenter.getDayHrDetailData(dateStr, mCurrentType, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), mCurrentDeviceId);
            }
        });


        tvBackToay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarview.goCurrentMonth();
                calendarview.clearSummary();
                getLastMonthData();
                //向前移动一个月
                mCurrentStr = DateUtil.dataToString(new Date(), "yyyy-MM-dd");
                initDatePopMonthTitle();
                mMenuViewBirth.dismiss();
                //当前天
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
        if (DeviceTypeUtil.isContaintW81(mCurrentType)) {
            w81DataPresenter.getW81MothSleep(deviceBean.deviceID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.HEARTRATE), instance.getTimeInMillis());
        } else {
            DeviceHistotyDataPresenter.getMonthData(instance, JkConfiguration.WatchDataType.HEARTRATE, mCurrentType, BaseApp.getApp());
        }
        // getMonthData(instance);
    }


    private void getLastMonthData() {
        Calendar calendar = calendarview.getDate();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //向前移动一个月
        calendar.add(Calendar.MONTH, -1);


        if (DeviceTypeUtil.isContaintW81(mCurrentType)) {
            w81DataPresenter.getW81MothSleep(deviceBean.deviceID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), String.valueOf(JkConfiguration.WatchDataType.HEARTRATE), calendar.getTimeInMillis());
        } else {
            DeviceHistotyDataPresenter.getMonthData(calendar, JkConfiguration.WatchDataType.HEARTRATE, mCurrentType, BaseApp.getApp());
        }
        calendar.add(Calendar.MONTH, 1);
    }


    private void initDatePopMonthTitle() {
        Calendar calendar = calendarview.getDate();
        // LogUtil.log(TAG + " initDatePopMonthTitle:" + dateFormat.format(calendar.getTime()));
        tvDatePopTitle.setText(dateFormat.format(calendar.getTime()));
    }


    @Override
    public void getMontData(String strYearAndMonth) {
        mActPresenter.getMonthStrDate(strYearAndMonth, deviceBean.currentType, mCurrentDeviceId, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
    }

    @Override
    public void successMonthStrDate(ArrayList<String> strDate) {

        Log.e(TAG,"----------连续心率="+new Gson().toJson(strDate));
        calendarview.setSummary(strDate);
    }

    @Override
    public void successDayHrDate(WristbandHrHeart wristbandHrHeart) {
        setSumHrValue(wristbandHrHeart.getMaxHr(), wristbandHrHeart.getMinHr(), wristbandHrHeart.getAvgHr());
        sumAllAvgHr = wristbandHrHeart.getAvgHr();
        setUpdateTime(wristbandHrHeart.getStrDate());

        setLineDataAndShow(wristbandHrHeart.getHrArry(), wristbandHrHeart.getMaxHr(), wristbandHrHeart.getMinHr(), wristbandHrHeart.getTimeInterval());
    }


    public void setSumHrValue(Integer strMax, Integer strMin, Integer strAvgHr) {
        setHrValue(tv_sum_max_hr, strMax);
        setHrValue(tv_sum_min_hr, strMin);
        setHrValue(tv_sum_avg_hr, strAvgHr);


    }

    private void setTextValue(Integer strMax, Integer strMin, Integer strAvgHr) {


        if (strMax == 0) {
            currentHeartRate = 0;
            maxHeartRate = "0";
            minHeartRate = "0";
            avgHeartRate = "0";
        } else {
            currentHeartRate = strAvgHr;
            maxHeartRate = strMax + "";
            minHeartRate = strMin + "";
            avgHeartRate = strAvgHr + "";
        }
        setHrValue(tvHrMax, strMax);
        setHrValue(tvHrMin, strMin);
        setHrValue(tvHrAvg, strAvgHr);
    }


    private void setHrValue(TextView tvValue, int value) {
        if (value < 30) {
            tvValue.setText(UIUtils.getString(R.string.no_data));
        } else {
            tvValue.setText(value + "");
        }
        HeartRateConvertUtils.hrValueColor(value, HeartRateConvertUtils.getMaxHeartRate(age, sex), tvValue);
    }


    //设置睡眠时间
    private void setUpdateTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            tvTime.setText(time);
        } else {
            tvTime.setText(mCurrentStr);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            default:
                break;
        }
    }

    Bracelet_w311_hrModel model;

    @Override
    public void successHrSettingItem(Bracelet_w311_hrModel bracelet_w311_wearModel) {
        model = bracelet_w311_wearModel;
    }

    @Override
    public void successSave(boolean isSave) {

    }

    @Override
    public void success24HrSettingState(Bracelet_W311_24H_hr_SettingModel bracelet_w311_24H_hr_settingModel) {

    }

    @Override
    public void success24HrSwitch(boolean isOpen) {
        if (AppConfiguration.isConnected && AppConfiguration.hasSynced) {
            BaseDevice baseDevice = ISportAgent.getInstance().getCurrnetDevice();
            if (baseDevice != null) {
                deviceName = baseDevice.deviceName;
            }

            if (!isOpen) {

                PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.heartrate_tip), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        if (stateBean != null) {
                            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, true, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
                            Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                            intentHr.putExtra(JkConfiguration.DEVICE, deviceName);
                            startActivity(intentHr);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                }, false);

            } else {
                if (stateBean != null) {
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, true, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);

                } else {
                    // ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, true, stateBean.isCall, stateBean.isMessage);

                }
                Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                intentHr.putExtra(JkConfiguration.DEVICE, deviceName);
                startActivity(intentHr);
            }
        } else {

        }
    }

    @Override
    public void successState(StateBean stateBean) {

        Logger.myLog("statebean" + stateBean);

        this.stateBean = stateBean;

        if (AppConfiguration.isConnected && AppConfiguration.hasSynced) {
            BaseDevice baseDevice = ISportAgent.getInstance().getCurrnetDevice();
            if (baseDevice != null) {
                deviceName = baseDevice.deviceName;
            }

            if (!stateBean.isHrState) {

                PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.heartrate_tip), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        if (stateBean != null) {
                            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, true, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);
                            Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                            intentHr.putExtra(JkConfiguration.DEVICE, deviceName);
                            startActivity(intentHr);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                }, false);

            } else {
                if (stateBean != null) {
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, true, stateBean.isCall, stateBean.isMessage, stateBean.tempUnitl);

                } else {
                    // ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GENERAL, true, stateBean.isCall, stateBean.isMessage);
                }
                Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                intentHr.putExtra(JkConfiguration.DEVICE, deviceName);
                startActivity(intentHr);
            }
        } else {

        }


    }


    /**
     * 获取长截图
     *
     * @return
     */
    public File getFullScreenBitmap(NestedScrollView scrollVew) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollVew.getChildCount(); i++) {
            h += scrollVew.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(UIUtils.getColor(R.color.common_view_color));
        scrollVew.draw(canvas);

   /*     //获取顶部布局的bitmap
        Bitmap head = Bitmap.createBitmap(titleBarView.getWidth(), titleBarView.getHeight(),
                Bitmap.Config.ARGB_8888);
        final Canvas canvasHead = new Canvas(head);
        canvasHead.drawColor(Color.WHITE);
        titleBarView.draw(canvasHead);*/
        /*Bitmap newbmp = Bitmap.createBitmap(scrollVew.getWidth(), h , Bitmap.Config
                .ARGB_8888);
        Canvas cv = new Canvas(newbmp);
       // cv.drawBitmap(head, 0, 0, null);// 在 0，0坐标开始画入headBitmap
        cv.drawBitmap(bitmap, 0, 0, null);// 在 0，headHeight坐标开始填充课表的Bitmap
        cv.save();// 保存
        cv.restore();// 存储*/
        //回收
        //  head.recycle();
        // 测试输出
        return FileUtil.writeImage(bitmap, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100);
    }


    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {

    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }
}

