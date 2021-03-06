package com.isport.brandapp.wu.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htsmart.wristband2.bean.data.SportData;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.device.share.PackageUtil;
import com.isport.brandapp.dialog.HeartStrongDialog;
import com.isport.brandapp.util.DateTimeUtils;
import com.isport.brandapp.util.ShareHelper;
import com.isport.brandapp.wu.Constant;
import com.isport.brandapp.wu.adapter.PractiseItemAdapter;
import com.isport.brandapp.wu.bean.ExerciseInfo;
import com.isport.brandapp.wu.util.HeartRateConvertUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bike.gymproject.viewlibray.LineChartView;
import bike.gymproject.viewlibray.LineRecChartPractiseView;
import bike.gymproject.viewlibray.LineRecChartView;
import bike.gymproject.viewlibray.LineScrollChartView;
import bike.gymproject.viewlibray.WatchHourMinuteView;
import bike.gymproject.viewlibray.WeekBarChartView;
import bike.gymproject.viewlibray.bean.HrlineBean;
import bike.gymproject.viewlibray.chart.HourMinuteData;
import bike.gymproject.viewlibray.chart.LineChartEntity;
import bike.gymproject.viewlibray.chart.PieChartData;
import bike.gymproject.viewlibray.chart.PieChartViewHeart;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.ThreadPoolUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.UserUtils;


/**
 * ????????????
 */
public class PractiseDetailActivity extends BaseActivity implements View.OnClickListener, UMShareListener {

    private RecyclerView recyclerview_practise;
    private File picFile = null;

    private TextView tv_avg_hr, tv_max_hr, tv_min_hr;

    private ImageView iv_back;
    private TextView tv_title;
    private ImageView iv_share;

    private LineRecChartPractiseView lineChartView;

    private PieChartViewHeart pieChartViewHeart;

    private RelativeLayout rl_title;

    private NestedScrollView nestedScroll;

    private RelativeLayout llHistoryShare;

    private WatchHourMinuteView view_limit, view_anaerobic_exercise, view_aerobic_exercise, view_fat_burning_exercise, view_warm_up, view_leisure;


    private UserInfoBean userInfoBean;
    private int age;
    private String sex;
    private ImageView iv_help;


    ExerciseInfo info;

    private ImageView ivQQ, ivWechat, ivWebo, ivFriend, ivFacebook, ivShareMore;
    private View viewZh;


    //????????????
    //Y?????????
    private LineChartView practiseLineChartView;
    private LineRecChartView practiseLineRecChartView1,practiseLineRecChartView2,practiseLineRecChartView3,
            practiseLineRecChartView4,practiseLineRecChartView5,practiseLineRecChartView6;
    private LineScrollChartView itemPractiseStepView;
    private TextView practiseStepCurrTv;


    //w560????????????????????????????????????????????????
    private LinearLayout itemPractiseChartLayout;



    //???????????????
    private WeekBarChartView itemPractiseDistanceBarView;
    //??????????????????
    private LineChartView caloriesLineChartView;
    private LineRecChartView caloriesLineRecChartView1,caloriesLineRecChartView2,caloriesLineRecChartView3,
            caloriesLineRecChartView4,caloriesLineRecChartView5,caloriesLineRecChartView6;
    private LineScrollChartView itemPractiseCaloriesView;
    private TextView practiseCaloriesCurrTv;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_practise_record_detail;
    }


    String titleName = "";

    private void getValue() {
        info = getIntent().getParcelableExtra("info");
        if (info == null) {
            return;
        }

        Logger.myLog(TAG,"-----exInfo="+info.toString());
        int type = Integer.parseInt(info.getExerciseType());


        boolean isF18 = AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_F18);
        if(isF18){
            switch (type){
                case SportData.SPORT_WALK:  //??????
                    titleName = getResources().getString(R.string.string_f18_walking);
                    break;
                case SportData.SPORT_OD:    //??????
                    titleName = UIUtils.getString(R.string.run);
                    break;
                case SportData.SPORT_CLIMB: //??????
                    titleName = UIUtils.getString(R.string.climbing);
                    break;
                case SportData.SPORT_RIDE:  //??????
                    titleName = UIUtils.getString(R.string.ride);
                    break;
                case SportData.SPORT_BB:    //??????
                    titleName = UIUtils.getString(R.string.basketball);
                    break;
                case SportData.SPORT_SWIM:  //??????
                    titleName = getResources().getString(R.string.string_f18_Swim);
                    break;
                case SportData.SPORT_BADMINTON: //?????????
                    titleName = UIUtils.getString(R.string.badminton);
                    break;
                case SportData.SPORT_FOOTBALL:  //??????
                    titleName = UIUtils.getString(R.string.football);
                    break;
                case SportData.SPORT_ELLIPTICAL_TRAINER:    //?????????
                    titleName = UIUtils.getString(R.string.string_w560_practise_eliptical);
                    break;
                case SportData.SPORT_YOGA:  //??????
                    titleName = UIUtils.getString(R.string.string_practise_yoga);
                    break;
                case SportData.SPORT_PING_PONG:   //?????????
                    titleName = UIUtils.getString(R.string.pingpang);
                    break;
                case SportData.SPORT_ROPE_SKIPPING:     //??????
                    titleName = UIUtils.getString(R.string.rope_skip);
                    break;
                case SportData.SPORT_TENNIS:    //??????
                    titleName = getResources().getString(R.string.string_f18_tennis);
                    break;
                case SportData.SPORT_BASEBALL:  //??????
                    titleName = getResources().getString(R.string.string_f18_baseball);
                    break;
                case SportData.SPORT_RUGBY:     //?????????
                    titleName = getResources().getString(R.string.string_f18_rugby);
                    break;
                case SportData.SPORT_HULA_HOOP:   //?????????
                    titleName = getResources().getString(R.string.string_f18_hula_hoop);
                    break;
                case SportData.SPORT_GOLF: //?????????
                    titleName = getResources().getString(R.string.string_f18_golf);
                    break;
                case SportData.SPORT_LONG_JUMP: //??????
                    titleName = getResources().getString(R.string.string_f18_jump);
                    break;
                case SportData.SPORT_SIT_UPS:    //????????????
                    titleName = getResources().getString(R.string.string_f18_sit_up);
                    break;
                case SportData.SPORT_VOLLEYBALL:    //??????
                    titleName = getResources().getString(R.string.string_f18_volley_ball);
                    break;
            }
            return;
        }




        boolean isW560 = AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560) ;
        if(isW560){
            switch (type) {
                case Constant.PRACTISE_TYPE_ALL:
                case Constant.PRACTISE_TYPE_WALK:
                    titleName = UIUtils.getString(R.string.string_w560_practise_out_walk);
                    break;
                case 2:
                    titleName = UIUtils.getString(R.string.string_w560_practise_out_run);
                    break;
                case 3:
                    titleName = UIUtils.getString(R.string.String_w560_practise_cycle);
                    break;
                case 4:
                    titleName = UIUtils.getString(R.string.string_practise_indoor_walk);
                    break;
                case 5:
                    titleName = UIUtils.getString(R.string.string_w560_practise_indoor_run);
                    break;
                case 6:
                    titleName = "HIIT";
                    break;
                case 7:
                    titleName = UIUtils.getString(R.string.string_practise_yoga);
                    break;
                case 8:
                    titleName = UIUtils.getString(R.string.string_w560_practise_eliptical);
                    break;
                case 9:
                    titleName = UIUtils.getString(R.string.string_practise_spinning);
                    break;
                case 10:
                    titleName = UIUtils.getString(R.string.string_w560_practise_run);
                    break;
                case 11:
                    titleName = UIUtils.getString(R.string.string_w560_practise_rowing);
                    break;
                case 12:
                    titleName = UIUtils.getString(R.string.string_practise_other);
                    break;

            }

            return;
        }

        boolean isW5xx = AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B);
        if(isW5xx){
            switch (type) {
                case Constant.PRACTISE_TYPE_ALL:
                case Constant.PRACTISE_TYPE_WALK:
                    titleName = UIUtils.getString(R.string.walk);
                    break;
                case 2:
                    titleName = UIUtils.getString(R.string.run);
                    break;
                case 3:
                    titleName = UIUtils.getString(R.string.ride);
                    break;
                case 8:
                    titleName = UIUtils.getString(R.string.climbing);
                    break;
                case 7:
                    titleName = UIUtils.getString(R.string.football);
                    break;
                case 6:
                    titleName = UIUtils.getString(R.string.basketball);
                    break;
                case 9:
                    titleName = UIUtils.getString(R.string.pingpang);
                    break;
                case 5:
                    titleName = UIUtils.getString(R.string.badminton);
                    break;
            }

            return;
        }


        switch (type) {
            case Constant.PRACTISE_TYPE_ALL:
            case Constant.PRACTISE_TYPE_WALK:
                titleName = UIUtils.getString(R.string.walk);
                break;
            case Constant.PRACTISE_TYPE_RUN:
                titleName = UIUtils.getString(R.string.run);
                break;
            case Constant.PRACTISE_TYPE_RIDE:
                titleName = UIUtils.getString(R.string.ride);
                break;
            case Constant.PRACTISE_TYPE_ROPE_SKIP:
                titleName = UIUtils.getString(R.string.rope_skip);
                break;
            case Constant.PRACTISE_TYPE_BADMINTON:
                titleName = UIUtils.getString(R.string.badminton);
                break;
            case Constant.PRACTISE_TYPE_FOOTBALL:
                titleName = UIUtils.getString(R.string.football);
                break;
            case Constant.PRACTISE_TYPE_BASKETBALL:
                titleName = UIUtils.getString(R.string.basketball);
                break;
//            case Constant.PRACTISE_TYPE_CLIMBING:
//                moy_tv_climbing.setTypeface(Typeface.DEFAULT_BOLD);
//                moy_tv_climbing.setTextColor(UIUtils.getColor(R.color.common_view_color));
//                break;
            case Constant.PRACTISE_TYPE_PINGPANG:
                titleName = UIUtils.getString(R.string.pingpang);
                break;
        }


        if(AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)){

            switch (type) {
                case Constant.PRACTISE_TYPE_ALL:
                case Constant.PRACTISE_TYPE_WALK:
                    titleName = UIUtils.getString(R.string.string_w560_practise_out_walk);
                    break;
                case 2:
                    titleName = UIUtils.getString(R.string.string_w560_practise_out_run);
                    break;
                case 3:
                    titleName = UIUtils.getString(R.string.String_w560_practise_cycle);
                    break;
                case 4:
                    titleName = UIUtils.getString(R.string.string_practise_indoor_walk);
                    break;
                case 5:
                    titleName = UIUtils.getString(R.string.string_w560_practise_indoor_run);
                    break;
                case 6:
                    titleName = "HIIT";
                    break;
                case 7:
                    titleName = UIUtils.getString(R.string.string_practise_yoga);
                    break;
                case 8:
                    titleName = UIUtils.getString(R.string.string_w560_practise_eliptical);
                    break;
                case 9:
                    titleName = UIUtils.getString(R.string.string_practise_spinning);
                    break;
                case 10:
                    titleName = UIUtils.getString(R.string.string_w560_practise_run);
                    break;
                case 11:
                    titleName = UIUtils.getString(R.string.string_w560_practise_rowing);
                    break;
                case 12:
                    titleName = UIUtils.getString(R.string.string_practise_other);
                    break;

            }
        }else{
            switch (type) {
                case Constant.PRACTISE_TYPE_ALL:
                case Constant.PRACTISE_TYPE_WALK:
                    titleName = UIUtils.getString(R.string.walk);
                    break;
                case 2:
                    titleName = UIUtils.getString(R.string.run);
                    break;
                case 3:
                    titleName = UIUtils.getString(R.string.ride);
                    break;
                case 8:
                    titleName = UIUtils.getString(R.string.climbing);
                    break;
                case 7:
                    titleName = UIUtils.getString(R.string.football);
                    break;
                case 6:
                    titleName = UIUtils.getString(R.string.basketball);
                    break;
                case 9:
                    titleName = UIUtils.getString(R.string.pingpang);
                    break;
                case 5:
                    titleName = UIUtils.getString(R.string.badminton);
                    break;
            }
        }

        analysisW560View(info);

    }



    @Override
    protected void initView(View view) {
        try {
            Logger.myLog("initView view" + view);
            tv_avg_hr = findViewById(R.id.tv_sport_time);
            tv_min_hr = findViewById(R.id.tv_min_hr);
            tv_max_hr = findViewById(R.id.tv_max_hr);
            lineChartView = findViewById(R.id.lineChartView);
            pieChartViewHeart = findViewById(R.id.pieChartView);
            iv_help = findViewById(R.id.iv_help);

            nestedScroll = findViewById(R.id.nestedScroll);

            view_limit = findViewById(R.id.view_limit);
            view_anaerobic_exercise = findViewById(R.id.view_anaerobic_exercise);
            view_aerobic_exercise = findViewById(R.id.view_aerobic_exercise);
            view_fat_burning_exercise = findViewById(R.id.view_fat_burning_exercise);
            view_warm_up = findViewById(R.id.view_warm_up);
            view_leisure = findViewById(R.id.view_leisure);
            viewZh = findViewById(R.id.view_zh);

            rl_title = findViewById(R.id.rl_title);

            iv_back = findViewById(R.id.iv_back);
            tv_title = findViewById(R.id.tv_title);
            iv_share = findViewById(R.id.iv_share);


            ivQQ = view.findViewById(R.id.iv_qq);
            ivWebo = view.findViewById(R.id.iv_weibo);
            ivWechat = view.findViewById(R.id.iv_wechat);
            ivFriend = view.findViewById(R.id.iv_friend);
            ivShareMore = view.findViewById(R.id.iv_more);
            viewZh = view.findViewById(R.id.view_zh);

            ivFacebook = view.findViewById(R.id.iv_facebook);

            itemPractiseStepView = findViewById(R.id.itemPractiseStepView);
            practiseLineChartView = findViewById(R.id.practiseLineChartView);
            practiseLineRecChartView1 = findViewById(R.id.practiseLineRecChartView1);
            practiseLineRecChartView2 = findViewById(R.id.practiseLineRecChartView2);
            practiseLineRecChartView3 = findViewById(R.id.practiseLineRecChartView3);
            practiseLineRecChartView4 = findViewById(R.id.practiseLineRecChartView4);
            practiseLineRecChartView5 = findViewById(R.id.practiseLineRecChartView5);
            practiseLineRecChartView6 = findViewById(R.id.practiseLineRecChartView6);
            practiseStepCurrTv = findViewById(R.id.practiseStepCurrTv);


            itemPractiseChartLayout = findViewById(R.id.itemPractiseChartLayout);

            itemPractiseDistanceBarView = findViewById(R.id.itemPractiseDistanceBarView);


            caloriesLineChartView = findViewById(R.id.caloriesLineChartView);
            caloriesLineRecChartView1 = findViewById(R.id.caloriesLineRecChartView1);
            caloriesLineRecChartView2 = findViewById(R.id.caloriesLineRecChartView2);
            caloriesLineRecChartView3 = findViewById(R.id.caloriesLineRecChartView3);
            caloriesLineRecChartView4 = findViewById(R.id.caloriesLineRecChartView4);
            caloriesLineRecChartView5 = findViewById(R.id.caloriesLineRecChartView5);
            caloriesLineRecChartView6 = findViewById(R.id.caloriesLineRecChartView6);
            itemPractiseCaloriesView = findViewById(R.id.itemPractiseCaloriesView);
            practiseCaloriesCurrTv = findViewById(R.id.practiseCaloriesCurrTv);


            llHistoryShare = view.findViewById(R.id.ll_history_share);
            iv_back.setOnClickListener(this);
            recyclerview_practise = findViewById(R.id.recyclerview_itme);

            //???????????????w560
//            if(AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)){
//                itemPractiseChartLayout.setVisibility(View.VISIBLE);
//            }else{
//                itemPractiseChartLayout.setVisibility(View.GONE);
//            }
            itemPractiseChartLayout.setVisibility(View.GONE);

            getValue();
            recyclerview_practise.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            List<ExerciseInfo> mExerciseInfos = new ArrayList<>();
            mExerciseInfos.add(info);
            PractiseItemAdapter practiseItemAdapter = new PractiseItemAdapter(this, mExerciseInfos, true);
            recyclerview_practise.setAdapter(practiseItemAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //??????W560????????????
    private void analysisW560View(ExerciseInfo info) {
        try {
            String stepArrayStr = info.getStepDetailArray();
            String distanceArrayStr = info.getDistanceDetailArray();
            String caloriesArrayStr = info.getCaloriesDetailArray();

            long startTime = info.getStartTimestamp();

            long endTime = info.getEndTimestamp();

            //????????????
            if(stepArrayStr!=null && !stepArrayStr.equals("[]")){
                List<Integer> stepList = new Gson().fromJson(stepArrayStr,new TypeToken<List<Integer>>(){}.getType());
                List<LineChartEntity> entityList = new ArrayList<>();
                for(int i = 0;i<stepList.size();i++){
                    LineChartEntity lineChartEntity = new LineChartEntity(DateTimeUtils.formatLongDateToInt(startTime)+i * 5,1,stepList.get(i).floatValue());
                    entityList.add(lineChartEntity);

                }
                int maxV = Collections.max(stepList);
                int minV = Collections.min(stepList);
                Logger.myLog(TAG,"------entriList="+new Gson().toJson(entityList)+" max="+maxV+" minV="+minV);

                //y?????????
                practiseLineChartView.setData(entityList,true,maxV,minV);

                itemPractiseStepView.setData(entityList,true, maxV,minV,1,entityList.size());

                ArrayList<HrlineBean> lists = HeartRateConvertUtils.pointToheartRateR(25, "Female");


                practiseLineRecChartView1.setData(entityList, true, maxV, minV, lists.get(0));
                practiseLineRecChartView2.setData(entityList, true, maxV, minV, lists.get(1));
                practiseLineRecChartView3.setData(entityList, true, maxV, minV, lists.get(2));
                practiseLineRecChartView4.setData(entityList, true, maxV, minV, lists.get(3));
                practiseLineRecChartView5.setData(entityList, true, maxV, minV, lists.get(4));
                practiseLineRecChartView6.setData(entityList, true, maxV, minV, lists.get(5));

                itemPractiseStepView.setOnlister(new LineScrollChartView.onSecletValueClick() {
                    @Override
                    public void onSelectValue(String value) {
                        Logger.myLog(TAG,"-----????????????="+value);
                        practiseStepCurrTv.setText("??????: "+ (int)Float.parseFloat(value) +" ???");
                    }
                });

            }

            if(distanceArrayStr != null){
                List<Integer> distanceList = new Gson().fromJson(stepArrayStr,new TypeToken<List<Integer>>(){}.getType());
                //itemPractiseDistanceBarView.setData();
            }

            if(caloriesArrayStr!=null){
                List<Integer> caloriesList = new Gson().fromJson(caloriesArrayStr,new TypeToken<List<Integer>>(){}.getType());
                List<LineChartEntity> entityList = new ArrayList<>();
                for(int i = 0;i<caloriesList.size();i++){
                    LineChartEntity lineChartEntity = new LineChartEntity(DateTimeUtils.formatLongDateToInt(startTime)+i * 5,1,caloriesList.get(i).floatValue());
                    entityList.add(lineChartEntity);

                }
                int maxV = Collections.max(caloriesList);
                int minV = Collections.min(caloriesList);

                //y?????????
                caloriesLineChartView.setData(entityList,true,maxV,minV);

                itemPractiseCaloriesView.setData(entityList,true, maxV,minV,1,entityList.size());

                ArrayList<HrlineBean> lists = HeartRateConvertUtils.pointToheartRateR(25, "Female");


                caloriesLineRecChartView1.setData(entityList, true, maxV, minV, lists.get(0));
                caloriesLineRecChartView2.setData(entityList, true, maxV, minV, lists.get(1));
                caloriesLineRecChartView3.setData(entityList, true, maxV, minV, lists.get(2));
                caloriesLineRecChartView4.setData(entityList, true, maxV, minV, lists.get(3));
                caloriesLineRecChartView5.setData(entityList, true, maxV, minV, lists.get(4));
                caloriesLineRecChartView6.setData(entityList, true, maxV, minV, lists.get(5));

                itemPractiseCaloriesView.setOnlister(new LineScrollChartView.onSecletValueClick() {
                    @Override
                    public void onSelectValue(String value) {
                        practiseCaloriesCurrTv.setText("?????????: "+(int)Float.parseFloat(value) + " ??????");
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void initData() {
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        try {

            if (App.getApp().isUSA()) {
                viewZh.setVisibility(View.GONE);
                ivFacebook.setVisibility(View.VISIBLE);
                ivWebo.setVisibility(View.GONE);
                ivFriend.setVisibility(View.GONE);
                // layout_bottom.setVisibility(View.INVISIBLE);
                // layout_bottom_us.setVisibility(View.VISIBLE);
            } else {
                viewZh.setVisibility(View.VISIBLE);
                ivFacebook.setVisibility(View.GONE);
                ivWebo.setVisibility(View.VISIBLE);
                ivFriend.setVisibility(View.VISIBLE);
            }

            if (userInfoBean != null) {
                String birthday = userInfoBean.getBirthday();
                age = UserUtils.getAge(birthday);
                sex = userInfoBean.getGender();
            }

            tv_title.setText(titleName);
            if (TextUtils.isEmpty(info.getAveRate()) || info.getAveRate().equals("0")) {
                tv_avg_hr.setText(UIUtils.getString(R.string.no_data));
                HeartRateConvertUtils.hrValueColor(0, HeartRateConvertUtils.getMaxHeartRate(age, sex), tv_avg_hr);
            } else {
                try {
                    HeartRateConvertUtils.hrValueColor(Integer.parseInt(info.getAveRate()), HeartRateConvertUtils.getMaxHeartRate(age, sex), tv_avg_hr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                tv_avg_hr.setText(info.getAveRate());
            }
            int vaidTimeLenth = TextUtils.isEmpty(info.getVaildTimeLength()) ? 0 : Integer.parseInt(info.getVaildTimeLength());


            Logger.myLog("setLineDataAndShow info:" + info);


            long sumTime = info.getEndTimestamp() - info.getStartTimestamp();
            int sumCount = 0;

            if (sumTime / 1000.f / 60 == sumTime / 1000 / 60) {
                sumCount = (int) (sumTime / 1000 / 60);
            } else {
                sumCount = (int) (sumTime / 1000 / 60 + 1);
            }


            setLineDataAndShow(info.getHeartRateDetailArray(), sumCount, 1, info.getStartTimestamp(), info.getEndTimestamp());

        } catch (Exception e) {
            e.printStackTrace();
        }

//        setNodata();
    }

    @Override
    protected void initEvent() {
        try {
            iv_help.setOnClickListener(this);
            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkCameraPersiomm();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void checkCameraPersiomm() {
        PermissionManageUtil permissionManage = new PermissionManageUtil(this);
        permissionManage.requestPermissionsGroup(new RxPermissions(this),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil
                        .OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        NetProgressObservable.getInstance().show();
                        iv_share.setVisibility(View.INVISIBLE);
                        iv_back.setVisibility(View.INVISIBLE);
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                // ThreadPoolUtils.getInstance().addTask(new ShootTask(scrollView,
                                // ActivityScaleReport.this, ActivityScaleReport.this));
                                picFile = getFullScreenBitmap(nestedScroll);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_share.setVisibility(View.VISIBLE);
                                        iv_back.setVisibility(View.VISIBLE);
                                        NetProgressObservable.getInstance().hide();
                                        llHistoryShare.setVisibility(View.VISIBLE);
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


    @Override
    protected void initHeader() {
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(com.isport.brandapp.basicres.R.color.common_view_color));

    }

    private void getData(int type) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callBackManager != null) {
            callBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * facebook??????????????????
     */
    private FacebookCallback facebookCallback = new FacebookCallback() {

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
     * ?????????facebook
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
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_setting:
                finish();
                break;
            case R.id.iv_facebook:
                //??????facebook???????????????????????????
                if (PackageUtil.isWxInstall(PractiseDetailActivity.this, PackageUtil.facebookPakage)) {
                    shareFaceBook(picFile);
                    //sharePlat(picFile, SHARE_MEDIA.FACEBOOK);
                } else {
                    ToastUtils.showToast(PractiseDetailActivity.this, UIUtils.getString(R.string.please_install_software));
                    return;
                }
                break;
            case R.id.iv_wechat:
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.weichatPakage)) {

                    sharePlat(picFile, SHARE_MEDIA.WEIXIN);
                } else {
                    ToastUtil.showTextToast(this, "?????????????????????");
                }
                break;
            case R.id.iv_friend:         //??????,????????????????????????
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.weichatPakage)) {
                    sharePlat(picFile, SHARE_MEDIA.WEIXIN_CIRCLE);
                } else {
                    ToastUtil.showTextToast(this, "?????????????????????");
                }
                break;
            case R.id.iv_qq:         //??????,????????????????????????
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.qqPakage)) {
                    sharePlat(picFile, SHARE_MEDIA.QQ);
                } else {
                    ToastUtil.showTextToast(this, "?????????????????????");
                }
                break;
            case R.id.iv_weibo:
                llHistoryShare.setVisibility(View.GONE);
                if (PackageUtil.isWxInstall(this, PackageUtil.weiboPakage)) {
                    sharePlat(picFile, SHARE_MEDIA.SINA);
                } else {
                    ToastUtil.showTextToast(this, "?????????????????????");
                }
                break;
            case R.id.tv_sharing_cancle:
                llHistoryShare.setVisibility(View.GONE);
                break;
            case R.id.iv_more:
                llHistoryShare.setVisibility(View.GONE);
                shareFile(picFile);
                //util.checkCameraPersiomm(this, this, layoutAll, "more");
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
            // ????????????????????????7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "????????????.fileprovider"?????????????????????????????????authorities
                uri = FileProvider.getUriForFile(PractiseDetailActivity.this, getPackageName() + ".fileprovider",
                        file);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file.getAbsolutePath()));//???????????????????????????
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    // ????????????????????????????????????MIME?????????
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


    List<LineChartEntity> datas = new ArrayList<>();
    int maxHR = 0, minHR = 1000;
    int sumHr = 0;
    int avgHr = 0;

    private void setLineDataAndShow(String strList, int sumTime, int len, Long startTime, Long endTime) {
        datas.clear();
        int limint = 0, anaerobic_exercise = 0, aerobic_exercise = 0, fat_burning_exercise = 0, warm_up = 0, leisure = 0;
        ArrayList<Integer> hrList = null;
        //  int maxHr = 0, minHr = 0;

        if (TextUtils.isEmpty(strList)) {
            hrList = new ArrayList<>();
        } else {
            hrList = new Gson().fromJson(strList, new TypeToken<List<Integer>>() {
            }.getType());
        }
       // Logger.myLog("strList:" + strList + "hrList.get(i):" + hrList + "HeartRateConvertUtils.getMaxHeartRate(age):" + HeartRateConvertUtils.getMaxHeartRate(age, sex));

        if (hrList.size() == 0) {
            setNoPieData();
            setSleepSummary(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);
        } else {

            if (sumTime > hrList.size()) {
                sumTime = hrList.size();
            }

            sumHr = 0;
            for (int i = 0; i < hrList.size(); i++) {
                int hrValue = hrList.get(i);
                sumHr += hrValue;
                if (hrValue < 30) {
                    continue;
                }
                if (maxHR < hrValue) {
                    maxHR = hrValue;
                }
                if (minHR > hrValue) {
                    minHR = hrValue;
                }

                int point = HeartRateConvertUtils.hearRate2Point(hrValue, HeartRateConvertUtils.getMaxHeartRate(age, sex));
                Logger.myLog(TAG,"-----point="+point);
                int color = UIUtils.getColor(R.color.common_white);
                switch (point) {
                    case 0:
                        leisure++;
                        color = UIUtils.getColor(R.color.color_leisure);
                        break;
                    case 1:
                        warm_up++;
                        color = UIUtils.getColor(R.color.color_warm_up);
                        break;
                    case 2:
                        fat_burning_exercise++;
                        color = UIUtils.getColor(R.color.color_fat_burning_exercise);
                        break;
                    case 3:
                        aerobic_exercise++;
                        color = UIUtils.getColor(R.color.color_aerobic_exercise);
                        break;
                    case 4:
                        anaerobic_exercise++;
                        color = UIUtils.getColor(R.color.color_anaerobic_exercise);
                        break;
                    case 5:
                        limint++;
                        color = UIUtils.getColor(R.color.color_limit);
                        break;
                }
                datas.add(new LineChartEntity(String.valueOf(i), Float.parseFloat(hrList.get(i) + ""), color));
            }
            if (sumHr != 0) {
                avgHr = sumHr / datas.size();
            }
            setHrValue(tv_max_hr, maxHR);
            setHrValue(tv_avg_hr, avgHr);
            setHrValue(tv_min_hr, minHR);
            setPieData(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);
            setSleepSummary(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);

        }

       // Logger.myLog("setLineDataAndShow hrList:" + hrList + ",sumTime:" + sumTime + "len:" + len);

        //311??????288?????????
        //516???1140?????????
        //81????????????5???????????????

        if (len == 0) {
            len = 1;
        }
        int listLen = sumTime / len;

        if (hrList == null || hrList.size() == 0) {
            for (int i = 0; i < listLen; i++) {
                datas.add(new LineChartEntity(String.valueOf(i), (float) (0)));
            }
        } else {
            //???hrList???size??????1440??????????????????1440,????????????????????????????????????????????????0
        }
        Logger.myLog("datas == isContainWatch" + datas.size());
        if (hrList.size() > 0) {
            maxHR = Integer.MIN_VALUE;
            minHR = Integer.MAX_VALUE;
            float fmaxHr = 0;
            float fminHr = 300;
            try {
                for (int i = 0; i < hrList.size(); i++) {
                    if (fmaxHr < hrList.get(i)) {
                        fmaxHr = hrList.get(i);
                    }
                    if (fminHr > hrList.get(i)) {
                        fminHr = hrList.get(i);
                    }
                }
                maxHR = (int) fmaxHr;
                minHR = (int) fminHr;
            } catch (Exception e) {
                maxHR = 250;
                minHR = 0;
            } finally {
                lineChartView.setData(datas, true, maxHR, minHR, TimeUtils.getTodayHHMMSS(startTime), TimeUtils.getTodayHHMMSS(endTime));
                // lineChartView.startAnimation(1000);
            }

        } else {
            maxHR = 0;
            minHR = 0;
            lineChartView.setData(datas, true, maxHR, minHR, TimeUtils.getTodayHHMMSS(startTime), TimeUtils.getTodayHHMMSS(endTime));
            // lineChartView.startAnimation(1000);
        }


    }

    private void setHrValue(TextView tvValue, int value) {
        if (value < 30 || minHR == 1000) {
            tvValue.setText(UIUtils.getString(R.string.no_data));
        } else {
            tvValue.setText(value + "");
        }
        HeartRateConvertUtils.hrValueColor(value, HeartRateConvertUtils.getMaxHeartRate(age, sex), tvValue);
    }


    private void setNoPieData() {
        if (pieChartViewHeart != null) {
            int totalTime = 1;
            List<PieChartData> pieChartDatas = new ArrayList<>();
            pieChartDatas.add(new PieChartData(getPiePercent(1, totalTime), Color.RED));//??????
            pieChartViewHeart.updateData(pieChartDatas);
        }
    }


    private void setPieData(int limint, int anaerobic_exercise, int aerobic_exercise, int fat_burning_exercise, int warm_up, int leisure) {
        if (pieChartViewHeart != null) {
            int totalTime = getTotalTime(limint, anaerobic_exercise, aerobic_exercise, fat_burning_exercise, warm_up, leisure);
            List<PieChartData> pieChartDatas = new ArrayList<>();
            pieChartDatas.add(new PieChartData(getPiePercent(limint, totalTime), UIUtils.getColor(R.color.color_limit)));//??????
            pieChartDatas.add(new PieChartData(getPiePercent(anaerobic_exercise, totalTime), UIUtils.getColor(R.color.color_anaerobic_exercise)));//??????
            pieChartDatas.add(new PieChartData(getPiePercent(aerobic_exercise, totalTime), UIUtils.getColor(R.color.color_aerobic_exercise)));//??????
            pieChartDatas.add(new PieChartData(getPiePercent(fat_burning_exercise, totalTime), UIUtils.getColor(R.color.color_fat_burning_exercise)));//??????
            pieChartDatas.add(new PieChartData(getPiePercent(warm_up, totalTime), UIUtils.getColor(R.color.color_warm_up)));//??????
            pieChartDatas.add(new PieChartData(getPiePercent(leisure, totalTime), UIUtils.getColor(R.color.color_leisure)));//??????
            pieChartViewHeart.updateData(pieChartDatas);
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

    boolean isW560 = AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560);

    //???????????????????????????
    private void setSleepSummary(int limint, int anaerobic_exercise, int aerobic_exercise, int fat_burning_exercise, int warm_up, int leisure) {

        Logger.myLog(TAG,"------limit="+limint+"\n"+anaerobic_exercise +"\n" +aerobic_exercise +"\n"+ fat_burning_exercise +"\n"+warm_up +"\n"+ leisure);
        view_limit.setData(new HourMinuteData(UIUtils.getColor(R.color.color_limit), UIUtils.getString(R.string.heart_limit), UIUtils.getString(R.string.no_data), isW560 ?DateUtil.getFormatTimehhmmss(limint,0): DateUtil.getFormatTimehhmmss(limint), "1"));
        view_anaerobic_exercise.setData(new HourMinuteData(UIUtils.getColor(R.color.color_anaerobic_exercise), UIUtils.getString(R.string.heart_anaerobic_exercise), UIUtils.getString(R.string.no_data), isW560 ?DateUtil.getFormatTimehhmmss(anaerobic_exercise,0):DateUtil.getFormatTimehhmmss(anaerobic_exercise), "1"));
        view_aerobic_exercise.setData(new HourMinuteData(UIUtils.getColor(R.color.color_aerobic_exercise), UIUtils.getString(R.string.heart_aerobic_exercise), UIUtils.getString(R.string.no_data), isW560 ?DateUtil.getFormatTimehhmmss(aerobic_exercise,0):DateUtil.getFormatTimehhmmss(aerobic_exercise), "1"));
        view_fat_burning_exercise.setData(new HourMinuteData(UIUtils.getColor(R.color.color_fat_burning_exercise), UIUtils.getString(R.string.heart_fat_burning_exercise), UIUtils.getString(R.string.no_data), isW560 ?DateUtil.getFormatTimehhmmss(fat_burning_exercise,0):DateUtil.getFormatTimehhmmss(fat_burning_exercise), "1"));
        view_warm_up.setData(new HourMinuteData(UIUtils.getColor(R.color.color_warm_up), UIUtils.getString(R.string.heart_warm_up), UIUtils.getString(R.string.no_data), isW560 ?DateUtil.getFormatTimehhmmss(warm_up,0):DateUtil.getFormatTimehhmmss(warm_up), "1"));
        view_leisure.setData(new HourMinuteData(UIUtils.getColor(R.color.color_leisure), UIUtils.getString(R.string.heart_leisure), UIUtils.getString(R.string.no_data), isW560 ?DateUtil.getFormatTimehhmmss(leisure,0):DateUtil.getFormatTimehhmmss(leisure), "1"));
    }

    /**
     * ???????????????
     *
     * @return
     */
    public File getFullScreenBitmap(NestedScrollView scrollVew) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollVew.getChildCount(); i++) {
            h += scrollVew.getChildAt(i).getHeight();
        }
        // ?????????????????????bitmap
        bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h,
                Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(UIUtils.getColor(R.color.common_view_color));
        scrollVew.draw(canvas);

        //?????????????????????bitmap
        Bitmap head = Bitmap.createBitmap(rl_title.getWidth(), rl_title.getHeight(),
                Bitmap.Config.ARGB_4444);
        final Canvas canvasHead = new Canvas(head);
        canvasHead.drawColor(Color.WHITE);
        rl_title.draw(canvasHead);
        Bitmap newbmp = Bitmap.createBitmap(scrollVew.getWidth(), h + head.getHeight(), Bitmap.Config
                .ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(head, 0, 0, null);// ??? 0???0??????????????????headBitmap
        cv.drawBitmap(bitmap, 0, head.getHeight(), null);// ??? 0???headHeight???????????????????????????Bitmap
        cv.save();// ??????
        cv.restore();// ??????
        //??????
        head.recycle();
        // ????????????
        return FileUtil.writeImage(newbmp, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100);
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
    }
}
