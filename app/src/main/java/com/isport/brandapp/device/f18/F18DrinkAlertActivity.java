package com.isport.brandapp.device.f18;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.listener.CommAlertListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import org.apache.commons.lang.StringUtils;

import androidx.constraintlayout.widget.ConstraintLayout;
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
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
    private TextView f18DrinkIntervalItem;
    //开始时间
    private TextView f18DrinkStartTimeItem;
    //结束时间
    private TextView f18DrinkEndTimeItem;

    //间隔
    private ConstraintLayout f18DrinkIntervalLayout;
    //开始
    private ConstraintLayout f18DrinkStartLayout;
    //结束
    private ConstraintLayout f18DrinkEndLayout;


    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
       // this.drinkSetBean = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {

        try {
            if(selectType == 0){
                String startTimeStr = timeStr;
                String endTimeStr = f18DrinkEndTimeItem.getText().toString();
                if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                    return;
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];

                if(startTime>=endTime){
                    ToastUtils.showToast(F18DrinkAlertActivity.this,"开始时间不能晚于结束时间!");
                    return;
                }

                //间隔
                String intervalStr = f18DrinkIntervalItem.getText().toString();
                String tmpInterval = StringUtils.substringBefore(intervalStr,"分");
                int drinkInterval = Integer.parseInt(tmpInterval);

                if(endTime-startTime<drinkInterval){
                    ToastUtils.showToast(F18DrinkAlertActivity.this,"开始时间与结束时间的间隔不能小于时间间隔!");
                    return;
                }

                f18DrinkStartTimeItem.setText(timeStr);
            }

             if(selectType == 1) {

                String startTimeStr = f18DrinkStartTimeItem.getText().toString();
                String endTimeStr = timeStr;
                if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                    return;
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];

                if(endTime<=startTime){
                    ToastUtils.showToast(F18DrinkAlertActivity.this,getResources().getString(R.string.stableRemind_tips));
                    return;
                }
                //间隔
                String intervalStr = f18DrinkIntervalItem.getText().toString();
                String tmpInterval = StringUtils.substringBefore(intervalStr," ");
                int drinkInterval = Integer.parseInt(tmpInterval);

                if(endTime-startTime<drinkInterval){
                    ToastUtils.showToast(F18DrinkAlertActivity.this,"开始时间与结束时间的间隔不能小于时间间隔!");
                    return;
                }

                f18DrinkEndTimeItem.setText(timeStr+"");
            }

             if(selectType == 2){

                String startTimeStr = f18DrinkStartTimeItem.getText().toString();
                String endTimeStr = f18DrinkEndTimeItem.getText().toString();
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];
                //开始时间和结束时间的间隔
                int intervalV =  endTime - startTime;

                if(Integer.parseInt(timeStr) > intervalV){
                    ToastUtils.showToast(F18DrinkAlertActivity.this,"间隔大于开始和结束时间间隔!");
                    return;
                }
                f18DrinkIntervalItem.setText(timeStr+" "+getResources().getString(R.string.minute));
            }

            setDataToDevice();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_drink_alert_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getResources().getString(R.string.string_drink_alert));
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
        drinkSetBean = (F18DeviceSetData) getIntent().getSerializableExtra("comm_key");
        String deviceId = AppConfiguration.braceletID;
//        if(deviceId != null){
//            mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this),deviceId);
//        }

        Watch7018Manager.getWatch7018Manager().getDrinkData(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                f18DrinkIsOpenItem.setChecked(isOpen);
                f18DrinkIntervalItem.setText(interval+" "+getResources().getString(R.string.unit_minute));
                f18DrinkStartTimeItem.setText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18DrinkEndTimeItem.setText(CommonDateUtil.formatHourMinute(endHour,endMinute));
                showOrClose(isOpen);
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

        f18DrinkIntervalLayout = findViewById(R.id.f18DrinkIntervalLayout);
        f18DrinkStartLayout = findViewById(R.id.f18DrinkStartLayout);
        f18DrinkEndLayout = findViewById(R.id.f18DrinkEndLayout);

        f18DrinkIsOpenItem = findViewById(R.id.f18DrinkIsOpenItem);
        f18DrinkIntervalItem = findViewById(R.id.f18DrinkIntervalItem);
        f18DrinkStartTimeItem = findViewById(R.id.f18DrinkStartTimeItem);
        f18DrinkEndTimeItem = findViewById(R.id.f18DrinkEndTimeItem);

        f18DrinkIntervalLayout.setOnClickListener(this);
        f18DrinkStartLayout.setOnClickListener(this);
        f18DrinkEndLayout.setOnClickListener(this);

        f18DrinkIsOpenItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18DrinkIsOpenItem.setChecked(isChecked);
                showOrClose(isChecked);
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18DrinkIntervalLayout){   //间隔
            mActPresenter.setSignalValue(F18DrinkAlertActivity.this,2,0,0);
        }
        if(vId == R.id.f18DrinkStartLayout){  //开始时间
            String starStr = f18DrinkStartTimeItem.getText().toString();
            mActPresenter.setDateTimeSelectView(F18DrinkAlertActivity.this,0,3,TextUtils.isEmpty(starStr) ? "08:00" : starStr);
        }
        if(vId == R.id.f18DrinkEndLayout){    //结束时间
            String endStr = f18DrinkEndTimeItem.getText().toString();
            mActPresenter.setDateTimeSelectView(F18DrinkAlertActivity.this,1,3,TextUtils.isEmpty(endStr) ? "22:00" : endStr);
        }
    }


    private void showOrClose(boolean isShow){
        f18DrinkStartLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        f18DrinkEndLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        f18DrinkIntervalLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void setDataToDevice(){
        try {
            String startTimeStr = f18DrinkStartTimeItem.getText().toString();
            String endTimeStr = f18DrinkEndTimeItem.getText().toString();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            //间隔
            String intervalStr = f18DrinkIntervalItem.getText().toString();
            String tmpInterval = StringUtils.substringBefore(intervalStr," ");

            Watch7018Manager.getWatch7018Manager().setDrinkData(f18DrinkIsOpenItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1],Integer.parseInt(tmpInterval.trim()));
            if(drinkSetBean != null){
                drinkSetBean.setDrinkAlert(f18DrinkIsOpenItem.isChecked() ? (startTimeStr+"-"+endTimeStr):getResources().getString(R.string.display_no_count));
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID,F18DbType.F18_DEVICE_SET_TYPE , new Gson().toJson(drinkSetBean));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
