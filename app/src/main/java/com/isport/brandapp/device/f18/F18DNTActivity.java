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
 * 勿扰模式
 * Created by Admin
 * Date 2022/1/18
 */
public class F18DNTActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView, View.OnClickListener {

    private ItemView f18DNTIsOpenItem;
    private ItemView f18DNTStartTimeItem;
    private ItemView f18DNTEndTimeItem;

    private F18DeviceSetData dntSetBean;

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        this.dntSetBean = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        if(selectType == 0){    //开始时间
            f18DNTStartTimeItem.setContentText(timeStr);
        }else{
            f18DNTEndTimeItem.setContentText(timeStr);
        }
        setDataToDevice();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_dnt_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle("勿扰设置");
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
        if(AppConfiguration.braceletID != null){
            mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID);
        }


        Watch7018Manager.getWatch7018Manager().getNotDisturbConfig(new CommAlertListener() {
            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                f18DNTIsOpenItem.setChecked(isOpen);
                f18DNTStartTimeItem.setContentText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18DNTEndTimeItem.setContentText(CommonDateUtil.formatHourMinute(endHour,endMinute));

                if(dntSetBean != null){
                    dntSetBean.setDNT(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)) : "未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18DNTActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(dntSetBean));
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

    private  void findViews(){
        f18DNTIsOpenItem = findViewById(R.id.f18DNTIsOpenItem);
        f18DNTStartTimeItem = findViewById(R.id.f18DNTStartTimeItem);
        f18DNTEndTimeItem = findViewById(R.id.f18DNTEndTimeItem);

        f18DNTStartTimeItem.setOnClickListener(this);
        f18DNTEndTimeItem.setOnClickListener(this);

        f18DNTIsOpenItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18DNTIsOpenItem.setChecked(isChecked);
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18DNTStartTimeItem){    //开始时间
            mActPresenter.setDateTimeSelectView(F18DNTActivity.this,0,3,"08:00");
        }

        if(vId == R.id.f18DNTEndTimeItem){  //结束时间
            mActPresenter.setDateTimeSelectView(F18DNTActivity.this,1,3,"22:00");
        }

    }


    private void setDataToDevice(){
        try {
            String startTimeStr = f18DNTStartTimeItem.getContentText();
            String endTimeStr = f18DNTEndTimeItem.getContentText();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            Watch7018Manager.getWatch7018Manager().setNotDisturbConfig(f18DNTIsOpenItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1]);

            if(dntSetBean != null){
                dntSetBean.setDNT(f18DNTIsOpenItem.isChecked() ? (startTimeStr+"-"+endTimeStr) : "未开启");
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18DNTActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(dntSetBean));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
