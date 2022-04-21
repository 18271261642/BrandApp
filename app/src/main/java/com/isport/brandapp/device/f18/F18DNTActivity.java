package com.isport.brandapp.device.f18;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.db.table.f18.listener.F18LongSetListener;
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
 * 勿扰模式
 * Created by Admin
 * Date 2022/1/18
 */
public class F18DNTActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView, View.OnClickListener {

    private ItemView f18DNTIsOpenItem;
    private TextView f18DNTStartTimeItem;
    private TextView f18DNTEndTimeItem;
    //全天开启
    private ItemView f18DNTIsAllOpenItem;

    //开始
    private ConstraintLayout f18DntStartLayout;
    //结束
    private ConstraintLayout f18DntEndLayout;

    private F18DeviceSetData dntSetBean;

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        //this.dntSetBean = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType,int type, String timeStr) {
        if(selectType == 0){    //开始时间

            String startTimeStr = timeStr;
            String endTimeStr = f18DNTEndTimeItem.getText().toString();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            int endTime = endTimeArray[0] * 60 + endTimeArray[1];
            int startTime = startTimeArray[0] * 60 + startTimeArray[1];

            if(startTime>=endTime){
                ToastUtils.showToast(F18DNTActivity.this,"开始时间不能晚于结束时间!");
                return;
            }

            f18DNTStartTimeItem.setText(timeStr+"");
        }else{

            String startTimeStr = f18DNTStartTimeItem.getText().toString();
            String endTimeStr = timeStr;
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            int endTime = endTimeArray[0] * 60 + endTimeArray[1];
            int startTime = startTimeArray[0] * 60 + startTimeArray[1];

            if(endTime<=startTime){
                ToastUtils.showToast(F18DNTActivity.this,getResources().getString(R.string.stableRemind_tips));
                return;
            }

            f18DNTEndTimeItem.setText(timeStr+"");
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
        titleBarView.setTitle(getResources().getString(R.string.watch_disturb_setting_str));
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
        dntSetBean = (F18DeviceSetData) getIntent().getSerializableExtra("comm_key");

//        if(AppConfiguration.braceletID != null){
//            mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID);
//        }

        showProgress("loading...",true);
        Watch7018Manager.getWatch7018Manager().getNotDisturbConfig(new F18LongSetListener() {
            @Override
            public void backCommF18AlertData(boolean isOpen, boolean isDnt, int startHour, int startMinute, int endHour, int endMinute, int interval) {
                dismissProgressBar();
                f18DNTIsOpenItem.setChecked(isOpen);
                f18DNTStartTimeItem.setText(CommonDateUtil.formatHourMinute(startHour,startMinute));
                f18DNTEndTimeItem.setText(CommonDateUtil.formatHourMinute(endHour,endMinute));
                f18DNTIsAllOpenItem.setChecked(isDnt);

                if(isDnt){
                    f18DNTIsOpenItem.setVisibility(View.GONE);
                    showOrClose(false);
                }else{
                    f18DNTIsOpenItem.setVisibility(View.VISIBLE);
                    showOrClose(isOpen);
                }



                if(dntSetBean != null){
                    dntSetBean.setDNT(isOpen ? (CommonDateUtil.formatHourMinute(startHour,startMinute)+"-"+CommonDateUtil.formatHourMinute(endHour,endMinute)) : "未开启");
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18DNTActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(dntSetBean));
                }
            }

            @Override
            public void backCommAlertData(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {

            }

        });
    }


    private void showOrClose(boolean isShow){
        f18DntStartLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        f18DntEndLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);

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

        f18DntStartLayout = findViewById(R.id.f18DntStartLayout);
        f18DntEndLayout = findViewById(R.id.f18DntEndLayout);

        f18DNTIsOpenItem = findViewById(R.id.f18DNTIsOpenItem);
        f18DNTStartTimeItem = findViewById(R.id.f18DNTStartTimeItem);
        f18DNTEndTimeItem = findViewById(R.id.f18DNTEndTimeItem);
        f18DNTIsAllOpenItem = findViewById(R.id.f18DNTIsAllOpenItem);

        f18DntStartLayout.setOnClickListener(this);
        f18DntEndLayout.setOnClickListener(this);

        f18DNTIsOpenItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18DNTIsOpenItem.setChecked(isChecked);
                showOrClose(isChecked);
                setDataToDevice();
            }
        });

        f18DNTIsAllOpenItem.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                f18DNTIsAllOpenItem.setChecked(isChecked);
                if(isChecked){
                    f18DNTIsOpenItem.setVisibility(View.GONE);
                    showOrClose(false);
                }else{
                    f18DNTIsOpenItem.setVisibility(View.VISIBLE);
                    showOrClose(true);
                }
                setDataToDevice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.f18DntStartLayout){    //开始时间
            String startT = f18DNTStartTimeItem.getText().toString();
            mActPresenter.setDateTimeSelectView(F18DNTActivity.this,0,3,TextUtils.isEmpty(startT) ? "08:00" : startT);
        }

        if(vId == R.id.f18DntEndLayout){  //结束时间
            String endT = f18DNTEndTimeItem.getText().toString();
            mActPresenter.setDateTimeSelectView(F18DNTActivity.this,1,3,TextUtils.isEmpty(endT) ? "22:00" : endT);
        }

    }


    private void setDataToDevice(){
        try {
            String startTimeStr = f18DNTStartTimeItem.getText().toString();
            String endTimeStr = f18DNTEndTimeItem.getText().toString();
            if(TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr))
                return;
            int[] startTimeArray = CommonDateUtil.getHourAdMinute(startTimeStr);
            int[] endTimeArray = CommonDateUtil.getHourAdMinute(endTimeStr);
            Watch7018Manager.getWatch7018Manager().setNotDisturbConfig(f18DNTIsOpenItem.isChecked(),f18DNTIsAllOpenItem.isChecked(),startTimeArray[0],startTimeArray[1],endTimeArray[0],endTimeArray[1]);

            if(dntSetBean != null){
                dntSetBean.setDNT(f18DNTIsAllOpenItem.isChecked() ? getResources().getString(R.string.lift_to_view_info_all_day) : f18DNTIsOpenItem.isChecked() ? (startTimeStr+"-"+endTimeStr) : "未开启");
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18DNTActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(dntSetBean));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
