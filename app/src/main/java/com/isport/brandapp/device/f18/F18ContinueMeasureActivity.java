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
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * F18连续监测
 * Created by Admin
 * Date 2022/1/18
 */
public class F18ContinueMeasureActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView, View.OnClickListener {

    private F18DeviceSetData continueSetData;

    //是否开启
    private ItemView f18ContinueIsOpenItem;
    //开始时间
    private ItemView f18ContinueStartTimeItem;
    //结束时间
    private ItemView f18ContinueEndTimeItem;

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        this.continueSetData = f18DeviceSetData;
    }

    //时间选择返回
    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        if(selectType == 0){    //开始时间
            f18ContinueStartTimeItem.setContentText(timeStr);
        }else{  //结束时间
            f18ContinueEndTimeItem.setContentText(timeStr);
        }
        setDataToDevice();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_continue_measure_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle("定时监测");
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
        mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this), AppConfiguration.braceletID);
        Watch7018Manager.getWatch7018Manager().getHealthyConfig(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                f18ContinueIsOpenItem.setChecked(isOpen);
                f18ContinueStartTimeItem.setContentText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18ContinueEndTimeItem.setContentText(CommonDateUtil.formatHourMinute(endHour,endMinute));

                if(continueSetData != null){
                    continueSetData.setContinuMonitor(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)) : "未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18ContinueMeasureActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(continueSetData));
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
        f18ContinueIsOpenItem = findViewById(R.id.f18ContinueIsOpenItem);
        f18ContinueStartTimeItem = findViewById(R.id.f18ContinueStartTimeItem);
        f18ContinueEndTimeItem = findViewById(R.id.f18ContinueEndTimeItem);

        f18ContinueStartTimeItem.setOnClickListener(this);
        f18ContinueEndTimeItem.setOnClickListener(this);

        f18ContinueIsOpenItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18ContinueIsOpenItem.setChecked(isChecked);
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18ContinueStartTimeItem){   //开始时间
            mActPresenter.setDateTimeSelectView(F18ContinueMeasureActivity.this,0,3,"08:00");
        }

        if(vId == R.id.f18ContinueEndTimeItem){     //结束时间
            mActPresenter.setDateTimeSelectView(F18ContinueMeasureActivity.this,1,3,"22:00");
        }
    }



    private void setDataToDevice(){
        try {
            String startTimeStr = f18ContinueStartTimeItem.getContentText();
            String endTimeStr = f18ContinueEndTimeItem.getContentText();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            Watch7018Manager.getWatch7018Manager().setHealthyConfig(f18ContinueIsOpenItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1]);
            if(continueSetData != null){
                continueSetData.setContinuMonitor(f18ContinueIsOpenItem.isChecked() ? (startTimeStr+"-"+endTimeStr) : "未开启");
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18ContinueMeasureActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(continueSetData));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
