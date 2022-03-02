package com.isport.brandapp.device.f18;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.htsmart.wristband2.bean.WristbandAlarm;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.entry.F18AlarmAllListener;
import com.isport.blelibrary.entry.F18CommStatusListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * 闹钟页面
 * Created by Admin
 * Date 2022/1/19
 */
public class F18AlarmShowActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView {

    private F18DeviceSetData alarmSetData;

    //emptyview
    private LinearLayout f18Alarm_layout_emty;

     private List<WristbandAlarm> alarmList;
     private CusF18AlarmAdapter f18AlarmAdapter;
     private RecyclerView alarmRecyclerView;

     private String mUserId;
     private String deviceId;

     private final Handler handler = new Handler(Looper.getMainLooper()){
         @Override
         public void handleMessage(@NonNull Message msg) {
             super.handleMessage(msg);
             handler.removeMessages(0x00);
              readDeviceAlarm();
         }
     };

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
       // this.alarmSetData = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType, int type, String timeStr) {
        Log.e(TAG,"------数据返回="+selectType+" type="+type+" timeStr="+timeStr);
        int[] timeHourArray = CommonDateUtil.getHourAdMinute(timeStr);
        if(selectType == -1){    //新增闹钟
            WristbandAlarm wristbandAlarm = new WristbandAlarm();
            wristbandAlarm.setAlarmId(WristbandAlarm.findNewAlarmId(alarmList));
            wristbandAlarm.setHour(timeHourArray[0]);
            wristbandAlarm.setMinute(timeHourArray[1]);
            wristbandAlarm.setEnable(true);
            wristbandAlarm.setRepeat(type);
            alarmList.add(wristbandAlarm);

        }else{
            WristbandAlarm updateAlarm = alarmList.get(selectType);
            updateAlarm.setHour(timeHourArray[0]);
            updateAlarm.setMinute(timeHourArray[1]);
            updateAlarm.setRepeat(type);
            alarmList.set(selectType,updateAlarm);
        }
        showProgress("loading...",false);
        Watch7018Manager.getWatch7018Manager().setDeviceAlarm(alarmList, new F18CommStatusListener() {
            @Override
            public void isSetStatus(boolean isSuccess, String error) {
                handler.sendEmptyMessage(0x00);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_alarm_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();
        showProgress("loading...",true);
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getResources().getString(R.string.watch_alarm_setting_title));
        titleBarView.setRightText("");
        titleBarView.setRightIcon(R.drawable.icon_add_device);

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {
                if(alarmList.size()>=5){
                    ToastUtil.init(F18AlarmShowActivity.this);
                    ToastUtil.showTextToast("最多设置5个闹钟!");
                    return;
                }
                //新增闹钟
                mActPresenter.setAlarmOrUpdateAlarm(F18AlarmShowActivity.this,-1,0,null);
            }
        });

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        alarmSetData = (F18DeviceSetData) getIntent().getSerializableExtra("comm_key");
        titleBarView.setRightIconVisible(false);
        readDeviceAlarm();
        mUserId = TokenUtil.getInstance().getPeopleIdStr(this);
        deviceId = AppConfiguration.braceletID;
    }


    private void readDeviceAlarm(){
        try {
            showProgress("loading...",true);
            Watch7018Manager.getWatch7018Manager().getDeviceAlarmList(new F18AlarmAllListener() {
                @Override
                public void backAllDeviceAlarm(List<WristbandAlarm> alarmLists) {
                    dismissProgressBar();
                    titleBarView.setRightIconVisible(true);
                    alarmList.clear();
                    alarmList.addAll(alarmLists);
                    f18AlarmAdapter.notifyDataSetChanged();

                    Log.e(TAG,"------读取闹钟="+new Gson().toJson(alarmLists));

                    f18Alarm_layout_emty.setVisibility(alarmList.isEmpty() ? View.VISIBLE : View.GONE);

                    if(alarmSetData != null){
                        alarmSetData.setAlarmCount(f18AlarmAdapter.getOpenCount());
                        mActPresenter.saveAllSetData(mUserId,deviceId, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(alarmSetData));
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdInt(this), ISportAgent.getInstance().getCurrnetDevice().getDeviceName());
    }

    @Override
    protected F18SetPresent createPresenter() {
        return new F18SetPresent(this);
    }

    private void findViews(){

        f18Alarm_layout_emty = findViewById(R.id.f18Alarm_layout_emty);
        alarmRecyclerView = findViewById(R.id.f18AlarmRecyclerView);
        alarmList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        alarmRecyclerView.setLayoutManager(linearLayoutManager);
        f18AlarmAdapter = new CusF18AlarmAdapter(this,alarmList);
        alarmRecyclerView.setAdapter(f18AlarmAdapter);

        f18AlarmAdapter.setOnF18ItemClickListener(new OnF18ItemClickListener() {
            //修改
            @Override
            public void onItemClick(int position) {
                mActPresenter.setAlarmOrUpdateAlarm(F18AlarmShowActivity.this,position,alarmList.get(position).getRepeat(),(alarmList.get(position).getHour()+":"+alarmList.get(position).getMinute()));
            }

            //开关
            @Override
            public void onChildClick(int position,boolean isCheck) {
                alarmList.get(position).setEnable(isCheck);
                showProgress("loading...",true);
                Watch7018Manager.getWatch7018Manager().setDeviceAlarm(alarmList, new F18CommStatusListener() {
                    @Override
                    public void isSetStatus(boolean isSuccess, String error) {
                        handler.sendEmptyMessage(0x00);
                    }
                });
            }

            //删除
            @Override
            public void onLongClick(int position) {
                deleteAlarmItem(position);
            }
        });
    }


    //开关
    private void operateSwitch(WristbandAlarm f18AlarmBean){
        Watch7018Manager.getWatch7018Manager().setDeviceAlarm(alarmList, new F18CommStatusListener() {
            @Override
            public void isSetStatus(boolean isSuccess, String error) {
                handler.sendEmptyMessage(0x00);
            }
        });
    }


    //删除
    private void deleteAlarmItem(int position){
        PublicAlertDialog.getInstance().showDialog("", this.getString(R.string.ensure_delete), context, this.getString(R.string.common_dialog_cancel), this.getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        alarmList.remove(position);

                        Watch7018Manager.getWatch7018Manager().setDeviceAlarm(alarmList, new F18CommStatusListener() {
                            @Override
                            public void isSetStatus(boolean isSuccess, String error) {
                                handler.sendEmptyMessage(0x00);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                }
                , false);
    }
}
