package com.isport.brandapp.device.f18;

import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.listener.CommAlertListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;

import org.apache.commons.lang.StringUtils;

import bike.gymproject.viewlibray.ItemView;
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



    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        this.longSetData = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        if(selectType == 1){    //开始
            iv_watch_stable_remind_start_time.setContentText(timeStr);

        }
        else if(selectType == 2){   //结束
            iv_watch_stable_remind_end_time.setContentText(timeStr);
        }
        else if(selectType == 3){
            iv_watch_stable_remind_time.setContentText(timeStr+"分钟");
        }
        setDataToDevice();
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
        titleBarView.setTitle("久坐提醒");
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



        Watch7018Manager.getWatch7018Manager().getSedentaryConfig(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                iv_watch_stable_remind_open.setChecked(isOpen);
                iv_watch_stable_remind_start_time.setContentText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                iv_watch_stable_remind_end_time.setContentText(CommonDateUtil.formatHourMinute(endHour,endMinute));
                iv_watch_stable_remind_time.setContentText(interval+"分钟");

                if(longSetData != null){
                    longSetData.setLongSitStr(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)) : "未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18LongSitActivity.this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(longSetData));
                }
            }
        });
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
        iv_watch_stable_remind_time = findViewById(R.id.iv_watch_stable_remind_time);
        iv_watch_stable_remind_open = findViewById(R.id.iv_watch_stable_remind_open);
        iv_watch_stable_remind_start_time = findViewById(R.id.iv_watch_stable_remind_start_time);
        iv_watch_stable_remind_end_time = findViewById(R.id.iv_watch_stable_remind_end_time);

        iv_watch_stable_remind_time.setVisibility(View.VISIBLE);
        iv_watch_stable_remind_time.setOnClickListener(this);
        iv_watch_stable_remind_start_time.setOnClickListener(this);
        iv_watch_stable_remind_end_time.setOnClickListener(this);
        iv_watch_stable_remind_open.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                iv_watch_stable_remind_open.setChecked(isChecked);
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
            mActPresenter.setDateTimeSelectView(F18LongSitActivity.this,1,3,"08:00");
        }

        if(vId == R.id.iv_watch_stable_remind_end_time){    //结束时间
            mActPresenter.setDateTimeSelectView(F18LongSitActivity.this,2,3,"22:00");
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
            String tmpInterval = StringUtils.substringBefore(intervalStr,"分");
            Watch7018Manager.getWatch7018Manager().setSedentaryConfig(iv_watch_stable_remind_open.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1],Integer.parseInt(tmpInterval.trim()));

            if(longSetData != null){
                longSetData.setLongSitStr(iv_watch_stable_remind_open.isChecked() ? (startTimeStr+"-"+endTimeStr) : "未开启");
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(longSetData));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
