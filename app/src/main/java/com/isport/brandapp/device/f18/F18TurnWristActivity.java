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

import androidx.constraintlayout.widget.ConstraintLayout;
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * 抬腕亮屏
 * Created by Admin
 * Date 2022/1/18
 */
public class F18TurnWristActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView , View.OnClickListener {

    private ItemView f18TurnWristStatusTimeItem;
    private TextView f18TurnWristStartTimeItem;
    private TextView f18TurnWristEndTimeItem;

    private F18DeviceSetData turnWristBean;

    private ConstraintLayout f18TurnStartLayout,f18TurnEndLayout;

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        this.turnWristBean = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        try {
            if(selectType == 0){    //开始时间

                String startTimeStr = timeStr;
                String endTimeStr = f18TurnWristEndTimeItem.getText().toString();
                if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                    return;
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];

                if(startTime>=endTime){
                    ToastUtils.showToast(F18TurnWristActivity.this,"开始时间不能晚于结束时间!");
                    return;
                }

                f18TurnWristStartTimeItem.setText(timeStr+"");
            }else{  //结束时间

                String startTimeStr = f18TurnWristStartTimeItem.getText().toString();
                String endTimeStr = timeStr;
                if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                    return;
                int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
                int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
                int endTime = endTimeArray[0] * 60 + endTimeArray[1];
                int startTime = startTimeArray[0] * 60 + startTimeArray[1];

                if(endTime<=startTime){
                    ToastUtils.showToast(F18TurnWristActivity.this,"结束时间不能小于开始时间!");
                    return;
                }
                f18TurnWristEndTimeItem.setText(timeStr+"");
            }
            setDataToDevice();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_turn_wrist_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getResources().getString(R.string.lift_to_view_info));
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
        turnWristBean = (F18DeviceSetData) getIntent().getSerializableExtra("comm_key");
//        if(AppConfiguration.braceletID != null){
//            mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID);
//        }
        Watch7018Manager.getWatch7018Manager().getTurnWristLightingConfig(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                f18TurnWristStatusTimeItem.setChecked(isOpen);
                f18TurnWristStartTimeItem.setText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18TurnWristEndTimeItem.setText(CommonDateUtil.formatHourMinute(endHour,endMinute));

                showOrClose(isOpen);

                if(turnWristBean != null){
                    turnWristBean.setTurnWrist(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)) : "未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18TurnWristActivity.this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(turnWristBean));
                }

            }
        });
    }

    private void showOrClose(boolean isShow){
        f18TurnStartLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        f18TurnEndLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
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

        f18TurnStartLayout = findViewById(R.id.f18TurnStartLayout);
        f18TurnEndLayout = findViewById(R.id.f18TurnEndLayout);

        f18TurnWristStatusTimeItem = findViewById(R.id.f18TurnWristStatusTimeItem);
        f18TurnWristStartTimeItem = findViewById(R.id.f18TurnWristStartTimeItem);
        f18TurnWristEndTimeItem = findViewById(R.id.f18TurnWristEndTimeItem);
        f18TurnStartLayout.setOnClickListener(this);
        f18TurnEndLayout.setOnClickListener(this);

        f18TurnWristStatusTimeItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18TurnWristStatusTimeItem.setChecked(isChecked);
                showOrClose(isChecked);
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18TurnStartLayout){  //开始时间
            String str = f18TurnWristStartTimeItem.getText().toString();
            mActPresenter.setDateTimeSelectView(F18TurnWristActivity.this,0,3,TextUtils.isEmpty(str) ? "08:00" : str);
        }
        if(vId == R.id.f18TurnEndLayout){    //结束时间
            String endStr = f18TurnWristEndTimeItem.getText().toString();
            mActPresenter.setDateTimeSelectView(F18TurnWristActivity.this,1,3,TextUtils.isEmpty(endStr) ? "08:00" : endStr);
        }

    }
    private void setDataToDevice(){
        try {
            String startTimeStr = f18TurnWristStartTimeItem.getText().toString();
            String endTimeStr = f18TurnWristEndTimeItem.getText().toString();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            Watch7018Manager.getWatch7018Manager().setTurnWristLightingConfig(f18TurnWristStatusTimeItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1]);

            if(turnWristBean != null){
                turnWristBean.setTurnWrist(f18TurnWristStatusTimeItem.isChecked() ? (startTimeStr+"-"+endTimeStr) : "未开启");
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(turnWristBean));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
