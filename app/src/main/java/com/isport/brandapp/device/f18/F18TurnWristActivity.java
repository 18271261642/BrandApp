package com.isport.brandapp.device.f18;

import android.text.TextUtils;
import android.view.View;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.listener.CommAlertListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.brandapp.R;
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * 抬腕亮屏
 * Created by Admin
 * Date 2022/1/18
 */
public class F18TurnWristActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView , View.OnClickListener {

    private ItemView f18TurnWristStatusTimeItem;
    private ItemView f18TurnWristStartTimeItem;
    private ItemView f18TurnWristEndTimeItem;

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {

    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        if(selectType == 0){    //开始时间
            f18TurnWristStartTimeItem.setContentText(timeStr);
        }else{  //结束时间
            f18TurnWristEndTimeItem.setContentText(timeStr);
        }
        setDataToDevice();
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
        Watch7018Manager.getWatch7018Manager().getTurnWristLightingConfig(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                f18TurnWristStatusTimeItem.setChecked(isOpen);
                f18TurnWristStartTimeItem.setContentText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18TurnWristEndTimeItem.setContentText(CommonDateUtil.formatHourMinute(endHour,endMinute));
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
        f18TurnWristStatusTimeItem = findViewById(R.id.f18TurnWristStatusTimeItem);
        f18TurnWristStartTimeItem = findViewById(R.id.f18TurnWristStartTimeItem);
        f18TurnWristEndTimeItem = findViewById(R.id.f18TurnWristEndTimeItem);
        f18TurnWristStartTimeItem.setOnClickListener(this);
        f18TurnWristEndTimeItem.setOnClickListener(this);

        f18TurnWristStatusTimeItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18TurnWristStatusTimeItem.setChecked(isChecked);
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18TurnWristStartTimeItem){  //开始时间
            mActPresenter.setDateTimeSelectView(F18TurnWristActivity.this,0,3,"08:00");
        }
        if(vId == R.id.f18TurnWristEndTimeItem){    //结束时间
            mActPresenter.setDateTimeSelectView(F18TurnWristActivity.this,0,3,"08:00");
        }

    }
    private void setDataToDevice(){
        try {
            String startTimeStr = f18TurnWristStartTimeItem.getContentText();
            String endTimeStr = f18TurnWristEndTimeItem.getContentText();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            Watch7018Manager.getWatch7018Manager().setTurnWristLightingConfig(f18TurnWristStatusTimeItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1]);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
