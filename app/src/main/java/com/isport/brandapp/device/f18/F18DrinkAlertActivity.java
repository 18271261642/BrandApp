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
 * 喝水提醒
 * Created by Admin
 * Date 2022/1/18
 */
public class F18DrinkAlertActivity extends BaseMVPTitleActivity<F18SetView, F18SetPresent> implements F18SetView, View.OnClickListener {

    private F18DeviceSetData drinkSetBean;

    //开关
    private ItemView f18DrinkIsOpenItem;
    //间隔
    private ItemView f18DrinkIntervalItem;
    //开始时间
    private ItemView f18DrinkStartTimeItem;
    //结束时间
    private ItemView f18DrinkEndTimeItem;



    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        this.drinkSetBean = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        if(selectType == 0){
            f18DrinkStartTimeItem.setContentText(timeStr);
        }else if(selectType == 1) {
            f18DrinkEndTimeItem.setContentText(timeStr);
        }else if(selectType == 2){
            f18DrinkIntervalItem.setContentText(timeStr+"分钟");
        }

        setDataToDevice();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_drink_alert_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle("喝水提醒");
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
        String deviceId = AppConfiguration.braceletID;
        if(deviceId != null){
            mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this),deviceId);
        }

        Watch7018Manager.getWatch7018Manager().getDrinkData(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                f18DrinkIsOpenItem.setChecked(isOpen);
                f18DrinkIntervalItem.setContentText(interval+"分钟");
                f18DrinkStartTimeItem.setContentText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18DrinkEndTimeItem.setContentText(CommonDateUtil.formatHourMinute(endHour,endMinute));

                if(drinkSetBean != null){
                    drinkSetBean.setDrinkAlert(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)):"未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18DrinkAlertActivity.this),AppConfiguration.braceletID,F18DbType.F18_DEVICE_SET_TYPE , new Gson().toJson(drinkSetBean));
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
        f18DrinkIsOpenItem = findViewById(R.id.f18DrinkIsOpenItem);
        f18DrinkIntervalItem = findViewById(R.id.f18DrinkIntervalItem);
        f18DrinkStartTimeItem = findViewById(R.id.f18DrinkStartTimeItem);
        f18DrinkEndTimeItem = findViewById(R.id.f18DrinkEndTimeItem);

        f18DrinkIntervalItem.setOnClickListener(this);
        f18DrinkStartTimeItem.setOnClickListener(this);
        f18DrinkEndTimeItem.setOnClickListener(this);

        f18DrinkIsOpenItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18DrinkIsOpenItem.setChecked(isChecked);
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18DrinkIntervalItem){   //间隔
            mActPresenter.setSignalValue(F18DrinkAlertActivity.this,2,0,0);
        }
        if(vId == R.id.f18DrinkStartTimeItem){  //开始时间
            mActPresenter.setDateTimeSelectView(F18DrinkAlertActivity.this,0,3,"08:00");
        }
        if(vId == R.id.f18DrinkEndTimeItem){    //结束时间
            mActPresenter.setDateTimeSelectView(F18DrinkAlertActivity.this,1,3,"22:00");
        }
    }

    private void setDataToDevice(){
        try {
            String startTimeStr = f18DrinkStartTimeItem.getContentText();
            String endTimeStr = f18DrinkEndTimeItem.getContentText();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            //间隔
            String intervalStr = f18DrinkIntervalItem.getContentText();
            String tmpInterval = StringUtils.substringBefore(intervalStr,"分");

            Watch7018Manager.getWatch7018Manager().setDrinkData(f18DrinkIsOpenItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1],Integer.parseInt(tmpInterval.trim()));
            if(drinkSetBean != null){
                drinkSetBean.setDrinkAlert(f18DrinkIsOpenItem.isChecked() ? (startTimeStr+"-"+endTimeStr):"未开启");
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID,F18DbType.F18_DEVICE_SET_TYPE , new Gson().toJson(drinkSetBean));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
