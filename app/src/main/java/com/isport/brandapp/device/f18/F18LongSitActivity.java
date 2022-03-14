package com.isport.brandapp.device.f18;

import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.listener.F18LongSetListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;

import org.apache.commons.lang.StringUtils;

import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * F18久坐提醒
 * Created by Admin
 * Date 2022/1/18
 */
public class F18LongSitActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView, View.OnClickListener {

    private F18DeviceSetData longSetData;

    //是否打开
    private ItemView iv_watch_stable_remind_open;
    //间隔
    private ItemView iv_watch_stable_remind_time;
    //开始时间
    private ItemView iv_watch_stable_remind_start_time;
    //结束时间
    private ItemView iv_watch_stable_remind_end_time;
    //午休免打扰
    private ItemView f18DntLongSitView;


    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
       // this.longSetData = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        try {
            if(selectType == 1){    //开始
                String startTimeStr = timeStr;
                String endTimeStr = iv_watch_stable_remind_end_time.getContentText();
                if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                    return;
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];

                if(startTime>=endTime){
                    ToastUtils.showToast(F18LongSitActivity.this,"开始时间不能晚于结束时间!");
                    return;
                }

                //间隔
                String intervalStr = iv_watch_stable_remind_time.getContentText();
                String tmpInterval = StringUtils.substringBefore(intervalStr,"分");
                int drinkInterval = Integer.parseInt(tmpInterval);

                if(endTime-startTime<drinkInterval){
                    ToastUtils.showToast(F18LongSitActivity.this,"开始时间与结束时间的间隔不能小于时间间隔!");
                    return;
                }

                iv_watch_stable_remind_start_time.setContentText(timeStr);
            }
            else if(selectType == 2){   //结束
                String startTimeStr = iv_watch_stable_remind_start_time.getContentText();
                String endTimeStr = timeStr;
                if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                    return;
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];

                if(endTime<=startTime){
                    ToastUtils.showToast(F18LongSitActivity.this,getResources().getString(R.string.stableRemind_tips));
                    return;
                }
                //间隔
                String intervalStr = iv_watch_stable_remind_time.getContentText();
              //  String tmpInterval = StringUtils.substringBefore(intervalStr,"分");
                int drinkInterval = 60;


                if(endTime-startTime<drinkInterval){
                    ToastUtils.showToast(F18LongSitActivity.this,getResources().getString(R.string.stableRemind_Perid_tips));
                    return;
                }

                iv_watch_stable_remind_end_time.setContentText(timeStr);
            }
            else if(selectType == 3){

                String startTimeStr = iv_watch_stable_remind_time.getContentText();
                String endTimeStr = iv_watch_stable_remind_end_time.getContentText();
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];
                //开始时间和结束时间的间隔
                int intervalV =  endTime - startTime;
                if(Integer.parseInt(timeStr) > intervalV){
                    ToastUtils.showToast(F18LongSitActivity.this,"间隔大于开始和结束时间间隔!");
                    return;
                }

                iv_watch_stable_remind_time.setContentText(timeStr+"分钟");
            }
            setDataToDevice();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_watch_stable_remind;
    }

    @Override
    protected void initView(View view) {
        findViews();
        mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID);
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getResources().getString(R.string.watch_stable_remind_str));
        titleBarView.setRightText("");

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
    }

    @Override
    protected void initData() {

        longSetData = (F18DeviceSetData) getIntent().getSerializableExtra("comm_key");

        Watch7018Manager.getWatch7018Manager().getSedentaryConfig(new F18LongSetListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {

            }

            @Override
            public void backCommF18AlertData(boolean isOpen,boolean isDnt, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                iv_watch_stable_remind_open.setChecked(isOpen);
                iv_watch_stable_remind_start_time.setContentText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                iv_watch_stable_remind_end_time.setContentText(CommonDateUtil.formatHourMinute(endHour,endMinute));
               // iv_watch_stable_remind_time.setContentText(interval+"分钟");
                f18DntLongSitView.setChecked(isDnt);

                showOrClose(isOpen);

                if(longSetData != null){
                    longSetData.setLongSitStr(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)) : "未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18LongSitActivity.this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(longSetData));
                }
            }
        });
    }


    private void showOrClose(boolean isShow){
        iv_watch_stable_remind_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
        iv_watch_stable_remind_start_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
        iv_watch_stable_remind_end_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
        f18DntLongSitView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected F18SetPresent createPresenter() {
        return new F18SetPresent(this);
    }

    private void findViews(){

        f18DntLongSitView = findViewById(R.id.f18DntLongSitView);
        iv_watch_stable_remind_time = findViewById(R.id.iv_watch_stable_remind_time);
        iv_watch_stable_remind_open = findViewById(R.id.iv_watch_stable_remind_open);
        iv_watch_stable_remind_start_time = findViewById(R.id.iv_watch_stable_remind_start_time);
        iv_watch_stable_remind_end_time = findViewById(R.id.iv_watch_stable_remind_end_time);

        iv_watch_stable_remind_time.setVisibility(View.VISIBLE);
       // iv_watch_stable_remind_time.setOnClickListener(this);
        iv_watch_stable_remind_time.setShowArrow(false);
        iv_watch_stable_remind_time.setContentText("60" +getResources().getString(R.string.minute));

        f18DntLongSitView.setVisibility(View.VISIBLE);

        iv_watch_stable_remind_start_time.setOnClickListener(this);
        iv_watch_stable_remind_end_time.setOnClickListener(this);
        iv_watch_stable_remind_open.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                iv_watch_stable_remind_open.setChecked(isChecked);
                showOrClose(isChecked);
                setDataToDevice();
            }
        });

        f18DntLongSitView.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18DntLongSitView.setChecked(isChecked);
                setDataToDevice();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.iv_watch_stable_remind_time){    //时间间隔
            mActPresenter.setSignalValue(F18LongSitActivity.this,3,0,0);
        }
        if(vId == R.id.iv_watch_stable_remind_start_time){  //开始时间
            String start = iv_watch_stable_remind_start_time.getContentText();
            mActPresenter.setDateTimeSelectView(F18LongSitActivity.this,1,3,TextUtils.isEmpty(start) ? "08:00" : start);
        }

        if(vId == R.id.iv_watch_stable_remind_end_time){    //结束时间
            String endT = iv_watch_stable_remind_end_time.getContentText();
            mActPresenter.setDateTimeSelectView(F18LongSitActivity.this,2,3,TextUtils.isEmpty(endT) ? "22:00" : endT);
        }
    }

    private void setDataToDevice(){
        try {
            String startTimeStr = iv_watch_stable_remind_start_time.getContentText();
            String endTimeStr = iv_watch_stable_remind_end_time.getContentText();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            //间隔
            String intervalStr = iv_watch_stable_remind_time.getContentText();
         //   String tmpInterval = StringUtils.substringBefore(intervalStr,"分");
            boolean isDnt =   f18DntLongSitView.isChecked();
            Watch7018Manager.getWatch7018Manager().setSedentaryConfig(iv_watch_stable_remind_open.isChecked(),isDnt,startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1],120);

            if(longSetData != null){
                longSetData.setLongSitStr(iv_watch_stable_remind_open.isChecked() ? (startTimeStr+"-"+endTimeStr) : getResources().getString(R.string.display_no_count));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(longSetData));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
