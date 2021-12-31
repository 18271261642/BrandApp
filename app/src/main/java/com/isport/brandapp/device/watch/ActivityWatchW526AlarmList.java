package com.isport.brandapp.device.watch;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.sleep.SleepBatteryResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.Utils;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.Utils.RepeatUtils;
import com.isport.brandapp.device.bracelet.braceletPresenter.AlarmPresenter;
import com.isport.brandapp.device.bracelet.view.AlarmView;
import com.isport.brandapp.device.dialog.BaseDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import bike.gymproject.viewlibray.ItemView;
import bike.gymproject.viewlibray.pickerview.DatePickerView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;

/**
 * 闹钟设置
 */
public class ActivityWatchW526AlarmList extends BaseMVPTitleActivity<AlarmView, AlarmPresenter> implements AlarmView, View.OnClickListener, ItemView.OnItemViewCheckedChangeListener {
    private final static String TAG = ActivityWatchW526AlarmList.class.getSimpleName();
    private DeviceBean deviceBean;
    private int currentType;
    private String deviceId;

    final String CLOSE = "close";
    final String OPEN = "open";

    private TextView textTime1, textTime2, textTime3;
    private TextView textReapter1, textReapter2, textReapter3;
    private ImageView checkBox1, checkBox2, checkBox3;
    private RelativeLayout layout1, layout2, layout3;


    ArrayList<TextView> textViewsTime = new ArrayList<>();
    ArrayList<TextView> textViewReapter = new ArrayList<>();
    ArrayList<ImageView> checkBoxes = new ArrayList<>();


    ArrayList<Bracelet_W311_AlarmModel> list = new ArrayList<>();


    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                if(list.isEmpty())
                    return;
                saveAllAlarm(list);
            }
        }
    };




    private void saveAllAlarm(ArrayList<Bracelet_W311_AlarmModel> lt){
        Logger.myLog(TAG,"-----ltttt="+new Gson().toJson(lt));
//        ArrayList<Bracelet_W311_AlarmModel> alarmList = new ArrayList<>();
//        for(Bracelet_W311_AlarmModel watch_w560_alarmModel : lt){
//            Bracelet_W311_AlarmModel bracelet_w311_alarmModel = new Bracelet_W311_AlarmModel();
//
//            bracelet_w311_alarmModel.setId(watch_w560_alarmModel.getId());
//            bracelet_w311_alarmModel.setAlarmId(watch_w560_alarmModel.getIndex());
//            bracelet_w311_alarmModel.setIsOpen(watch_w560_alarmModel.getIsEnable());
//            bracelet_w311_alarmModel.setDeviceId(watch_w560_alarmModel.getDeviceId());
//            bracelet_w311_alarmModel.setRepeatCount(watch_w560_alarmModel.getRepeatCount());
//            bracelet_w311_alarmModel.setUserId(watch_w560_alarmModel.getUserId());
//            bracelet_w311_alarmModel.setMessageString(watch_w560_alarmModel.getMessageString());
//            bracelet_w311_alarmModel.setTimeString(watch_w560_alarmModel.getTimeString());
//            alarmList.add(bracelet_w311_alarmModel);
//        }

       // mActPresenter.saveAllAlarm(lt,TokenUtil.getInstance().getPeopleIdInt(this),deviceBean.deviceID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_w526_alarm_list;
    }

    @Override
    protected void initView(View view) {

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

        textTime1 = findViewById(R.id.tv_time1);
        textTime2 = findViewById(R.id.tv_time2);
        textTime3 = findViewById(R.id.tv_time3);

        textReapter1 = findViewById(R.id.tv_repeat1);
        textReapter2 = findViewById(R.id.tv_repeat2);
        textReapter3 = findViewById(R.id.tv_repeat3);

        checkBox1 = findViewById(R.id.view_car_remind_radio1);
        checkBox2 = findViewById(R.id.view_car_remind_radio2);
        checkBox3 = findViewById(R.id.view_car_remind_radio3);


        textViewsTime.add(textTime1);
        textViewsTime.add(textTime2);
        textViewsTime.add(textTime3);

        textViewReapter.add(textReapter1);
        textViewReapter.add(textReapter2);
        textViewReapter.add(textReapter3);

        checkBoxes.add(checkBox1);
        checkBoxes.add(checkBox2);
        checkBoxes.add(checkBox3);


        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(list.get(0));
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(list.get(1));
            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(list.get(2));
            }
        });


        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox1.getTag() == CLOSE) {
                    list.get(0).setIsOpen(true);
                    setCheckBox(checkBox1, OPEN, list.get(0));
                } else {
                    list.get(0).setIsOpen(false);
                    setCheckBox(checkBox1, CLOSE, list.get(0));
                }
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox2.getTag() == CLOSE) {
                    list.get(1).setIsOpen(true);
                    setCheckBox(checkBox2, OPEN, list.get(1));
                } else {
                    list.get(1).setIsOpen(false);
                    setCheckBox(checkBox2, CLOSE, list.get(1));
                }
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox3.getTag() == CLOSE) {
                    list.get(2).setIsOpen(true);
                    setCheckBox(checkBox3, OPEN, list.get(2));
                } else {
                    list.get(2).setIsOpen(false);
                    setCheckBox(checkBox3, CLOSE, list.get(2));
                }
            }
        });

    }


    public void setCheckBox(ImageView checkBox, String tag, Bracelet_W311_AlarmModel model) {
        checkBox.setTag(tag);
        if (tag.equals(OPEN)) {
            checkBox.setImageResource(R.drawable.icon_open);
        } else {
            // model.setRepeatCount(0);
            checkBox.setImageResource(R.drawable.icon_close);
        }
        updateItem(model);

    }


    Bracelet_W311_AlarmModel currentAlarmModel;
    String editTime;
    int editRepeate;

    public void startActivity(Bracelet_W311_AlarmModel item) {
        currentAlarmModel = item;
        editTime = currentAlarmModel.getTimeString();
        editRepeate = currentAlarmModel.getRepeatCount();
        showAddOrEditDiloag(item.getTimeString(), true);

       /* Intent intent3 = new Intent(context, ActivityBraceletAlarmSetting.class);
        intent3.putExtra("deviceBean", deviceBean);
        intent3.putExtra("type", JkConfiguration.DeviceType.BRAND_W311);
        intent3.putExtra("itemDeviceId", item.getDeviceId());
        intent3.putExtra("itemId", item.getId());
        intent3.putExtra("alarmId", item.getAlarmId());
        intent3.putExtra("itemOpen", item.getIsOpen());
        intent3.putExtra("itemRepeatCount", item.getRepeatCount());
        intent3.putExtra("itemTimeString", item.getTimeString());
        intent3.putExtra("itemUserId", item.getUserId());
        intent3.putExtra("isEdit", true);
        startActivity(intent3);*/
    }

    ItemView settingRepeat;

    private void showAddOrEditDiloag(String itemTimeString, boolean isEdit) {
        BaseDialog dialog = new BaseDialog.Builder(this)
                .setContentView(R.layout.app_activity_bracelet_alarm_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.iv_watch_alarm_setting_repeat, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showReapeat(currentAlarmModel.getRepeatCount());
                                // dialogInterface.cancel();
                            }
                        }
                )
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }
                )
                .show();

        DatePickerView datePicker = dialog.findViewById(R.id.datePicker);

        settingRepeat = dialog.findViewById(R.id.iv_watch_alarm_setting_repeat);
        TextView tvSave = dialog.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tvSave", "datePicker.getTime() = " + datePicker.getTime());
                currentAlarmModel.setTimeString(datePicker.getTime());
                if (currentAlarmModel.getTimeString().equals(editTime) && currentAlarmModel.getRepeatCount() == editRepeate) {
                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.alarm_tips_reapte));
                    return;
                }
                currentAlarmModel.setIsOpen(true);
                mActPresenter.updateMode(currentAlarmModel);
                //这里需要去发送指令个硬件
                String strtime = currentAlarmModel.getTimeString();
                String[] hourMin = strtime.split(":");
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_ALARM, currentAlarmModel.getIsOpen(), currentAlarmModel.getRepeatCount(), Integer.parseInt(hourMin[0]), Integer.parseInt(hourMin[1]), currentAlarmModel.getAlarmId());
                dialog.dismiss();
            }

        });
        datePicker.setType(3);
        // if (isEdit) {
        datePicker.setDefaultItemAdapter(itemTimeString);
        settingRepeat.setContentText(RepeatUtils.setRepeat(deviceBean.deviceType, currentAlarmModel.getRepeatCount()));
       /* } else {
            datePicker.setDefaultItemAdapter(Constants.defStartTime);
            settingRepeat.setContentText(UIUtils.getString(R.string.only_once));
        }*/
        // alarmPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceName);
        datePicker.setCyclic(false);

    }

    private void showReapeat(int repeat) {

        BaseDialog mMenuViewBirth = new BaseDialog.Builder(this)
                .setContentView(R.layout.app_pop_week)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();


        TextView tv_determine = (TextView) mMenuViewBirth.findViewById(R.id.tv_determine);
        final CheckBox cb_sun = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_sun);
        final CheckBox cb_mon = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_mon);
        final CheckBox cb_tue = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_tue);
        final CheckBox cb_wed = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_wed);
        final CheckBox cb_thu = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_thu);
        final CheckBox cb_fri = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_fri);
        final CheckBox cb_sat = (CheckBox) mMenuViewBirth.findViewById(R.id.cb_sat);

        final RelativeLayout layout_sum = mMenuViewBirth.findViewById(R.id.layout_sum);
        final RelativeLayout layout_mon = mMenuViewBirth.findViewById(R.id.layout_mon);
        final RelativeLayout layout_tue = mMenuViewBirth.findViewById(R.id.layout_tue);
        final RelativeLayout layout_web = mMenuViewBirth.findViewById(R.id.layout_wed);
        final RelativeLayout layout_thu = mMenuViewBirth.findViewById(R.id.layout_thu);
        final RelativeLayout layout_fri = mMenuViewBirth.findViewById(R.id.layout_fri);
        final RelativeLayout layout_sat = mMenuViewBirth.findViewById(R.id.layout_sat);

        final ImageView iv_sum = mMenuViewBirth.findViewById(R.id.iv_sun);
        final ImageView iv_mon = mMenuViewBirth.findViewById(R.id.iv_mon);
        final ImageView iv_tue = mMenuViewBirth.findViewById(R.id.iv_tue);
        final ImageView iv_web = mMenuViewBirth.findViewById(R.id.iv_wed);
        final ImageView iv_thu = mMenuViewBirth.findViewById(R.id.iv_thu);
        final ImageView iv_fri = mMenuViewBirth.findViewById(R.id.iv_fri);
        final ImageView iv_sat = mMenuViewBirth.findViewById(R.id.iv_sat);


        byte[] booleanArrayG = Utils.getBooleanArray((byte) repeat);
        cb_sun.setChecked(false);
        cb_mon.setChecked(false);
        cb_tue.setChecked(false);
        cb_wed.setChecked(false);
        cb_thu.setChecked(false);
        cb_fri.setChecked(false);
        cb_sat.setChecked(false);
        Logger.myLog("repeat == " + repeat);
        final StringBuilder stringBuilder = new StringBuilder(booleanArrayG.length);
        for (byte byteChar : booleanArrayG) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }
        Logger.myLog(" booleanArrayG " + stringBuilder.toString());
        if (Utils.byte2Int(booleanArrayG[7]) == 1) {
            cb_sun.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[6]) == 1) {
            cb_mon.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[5]) == 1) {
            cb_tue.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[4]) == 1) {
            cb_wed.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[3]) == 1) {
            cb_thu.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[2]) == 1) {
            cb_fri.setChecked(true);
        }
        if (Utils.byte2Int(booleanArrayG[1]) == 1) {
            cb_sat.setChecked(true);
        }
        showImgView(iv_sum, cb_sun.isChecked());
        showImgView(iv_mon, cb_mon.isChecked());
        showImgView(iv_tue, cb_tue.isChecked());
        showImgView(iv_web, cb_wed.isChecked());
        showImgView(iv_thu, cb_thu.isChecked());
        showImgView(iv_fri, cb_fri.isChecked());
        showImgView(iv_sat, cb_sat.isChecked());
        //展示之前的选择


        layout_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bFri = cb_fri.isChecked();
                cb_fri.setChecked(!bFri);
                showImgView(iv_fri, cb_fri.isChecked());
                // cb_fri.setChecked(!bFri);

            }
        });
        layout_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cb_mon.setChecked(!cb_mon.isChecked());
                boolean bFri = cb_mon.isChecked();
                cb_mon.setChecked(!bFri);
                showImgView(iv_mon, cb_mon.isChecked());

            }
        });
        layout_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_sat.setChecked(!cb_sat.isChecked());
                showImgView(iv_sat, cb_sat.isChecked());
            }
        });
        layout_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_sun.setChecked(!cb_sun.isChecked());
                showImgView(iv_sum, cb_sun.isChecked());
            }
        });
        layout_thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_thu.setChecked(!cb_thu.isChecked());
                showImgView(iv_thu, cb_thu.isChecked());
            }
        });

        layout_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_tue.setChecked(!cb_tue.isChecked());
                showImgView(iv_tue, cb_tue.isChecked());
            }
        });

        layout_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_wed.setChecked(!cb_wed.isChecked());
                showImgView(iv_web, cb_wed.isChecked());
            }
        });

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calculationAgeAndConstellation(datePicker.getTime());
//                localUserChooseBirthday = datePicker.getTime();
                //确定选择，直接返回要发送的指令

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("0");
                if (cb_sat.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
                if (cb_fri.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
                if (cb_thu.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
                if (cb_wed.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
                if (cb_tue.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
                if (cb_mon.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
                if (cb_sun.isChecked()) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }

                int value = Integer.valueOf(stringBuilder.toString(), 2);

                StringBuilder stringBuilderWeek = new StringBuilder();
                if (cb_mon.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.mon) + ",");
                }
                if (cb_tue.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.tue) + ",");
                }

                if (cb_wed.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.wed) + ",");
                }

                if (cb_thu.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.thu) + ",");
                }

                if (cb_fri.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.fri) + ",");
                }

                if (cb_sat.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.sat) + ",");
                }

                if (cb_sun.isChecked()) {
                    stringBuilderWeek.append(UIUtils.getString(R.string.sun) + ",");
                }

                String weekStr = stringBuilderWeek.toString();

                if (weekStr.endsWith(",")) {
                    weekStr = weekStr.substring(0, weekStr.length() - 1);
                }

                //  mActView.get().dataSetSuccess(targetView, type, value + "-" + weekStr);

                String data = value + "-" + weekStr;
                int mRepeat = 0;

                //设置了星期
                if (!TextUtils.isEmpty(data)) {
                    String[] split = data.split("-");
                    if (split.length <= 1) {
                        mRepeat = 0;
                        // settingRepeat.setContentText(RepeatUtils.setRepeat(deviceBean.deviceType,mRepeat));
                        settingRepeat.setContentText(UIUtils.getString(R.string.only_once));
                    }
                    if (split.length >= 1) {

                        mRepeat = Integer.parseInt(split[0]);


                    }
                    if (split.length >= 2) {
                        Logger.myLog("设置mRepeat == " + split[1]);
                        Logger.myLog(split[1]);
                        //settingRepeat.setContentText(split[1]);
                    }

                    //int[] hourAndMin = getHourAndMin();
                    Logger.myLog("设置mRepeat == " + mRepeat);
                    // RepeatUtils.setRepeat(deviceBean.deviceType,mRepeat)
                    settingRepeat.setContentText(RepeatUtils.setRepeat(deviceBean.deviceType, mRepeat));
                    //setRepeatStr(mRepeat);
                } else {
                    mRepeat = 0;
                    settingRepeat.setContentText(UIUtils.getString(R.string.only_once));
                }
                currentAlarmModel.setRepeatCount(mRepeat);
                mMenuViewBirth.dismiss();
            }
        });

    }

    /*private int[] getHourAndMin() {
        String[] split1 = datePicker.getTime().split(":");
        int hour = Integer.parseInt(split1[0]);
        int min = Integer.parseInt(split1[1]);
        int[] result = new int[2];
        result[0] = hour;
        result[1] = min;
        return result;
    }*/

    public void showImgView(ImageView ivImage, boolean isShow) {
      /*  if (isShow) {
            ivImage.setImageResource(R.drawable.sel_week_yes);
        } else {
            ivImage.setImageResource(R.drawable.sel_week_nor);
        }*/
        ivImage.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void initData() {
        getIntentData();
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(context.getResources().getString(R.string.watch_alarm_setting_str));
        titleBarView.setRightText("");
        frameBodyLine.setVisibility(View.VISIBLE);
        ISportAgent.getInstance().registerListener(mBleReciveListener);
        if (AppConfiguration.isConnected && AppConfiguration.hasSynced) {
            mActPresenter.getW526AllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
            BleProgressObservable.getInstance().show();
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_ALARM);
        } else {
            mActPresenter.getW526AllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //有改变的时候去设置
    }

    private void getIntentData() {

        deviceBean = (DeviceBean) getIntent().getSerializableExtra("deviceBean");
        if (deviceBean != null) {
            currentType = deviceBean.currentType;
            deviceId = deviceBean.deviceName;
        }

    }


    @Override
    protected void initEvent() {

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
    protected void onDestroy() {
        super.onDestroy();
        ISportAgent.getInstance().unregisterListener(mBleReciveListener);
    }

    private final BleReciveListener mBleReciveListener = new BleReciveListener() {
        @Override
        public void onConnResult(boolean isConn, boolean isConnectByUser, BaseDevice baseDevice) {

        }

        @Override
        public void setDataSuccess(String s) {

        }

        @Override
        public void receiveData(IResult mResult) {
            //Logger.myLog("HandlerContans.mDevcieAlarList2" + mResult);
            if (mResult != null)
                 Logger.myLog("HandlerContans.mDevcieAlarList2" + mResult.getType());
                switch (mResult.getType()) {
                    case IResult.SLEEP_BATTERY:
                        SleepBatteryResult mResult1 = (SleepBatteryResult) mResult;
                        // TODO: 2018/10/17 更新列表电量
                        break;
                    case IResult.DEVICE_ALARM_LIST:
                        BleProgressObservable.getInstance().hide();
                        Logger.myLog("HandlerContans.mDevcieAlarList w526");
                        mActPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                        break;  // ---- xuqian------
                    default:
                        break;
                }
        }

        @Override
        public void onConnecting(BaseDevice baseDevice) {

        }

        @Override
        public void onBattreyOrVersion(BaseDevice baseDevice) {

        }
    };

    @Override
    protected void initHeader() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onCheckedChanged(int id, boolean isChecked) {
        switch (id) {

        }
    }

    @Override
    protected AlarmPresenter createPresenter() {
        return new AlarmPresenter(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.isChange:
                mActPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                break;
            default:
                break;
        }
    }

    @Override
    public void successW560AllAlarmItem(ArrayList<Watch_W560_AlarmModel> watch_w560_alarmModels) {
        // 不作处理
    }

    @Override
    public void successAllAlarmItem(ArrayList<Bracelet_W311_AlarmModel> bracelet_w311_displayModel) {


        if(bracelet_w311_displayModel==null){
            return;
        }
        list.clear();
        list.addAll(bracelet_w311_displayModel);
        Logger.myLog(TAG,"-----successAllAlarmItem:" + bracelet_w311_displayModel);
        int len = list.size();
        if (len > 3) {
            len = 3;
        }
        for (int i = 0; i < len; i++) {
            Bracelet_W311_AlarmModel model = list.get(i);
            textViewsTime.get(i).setText(model.getTimeString());
            textViewReapter.get(i).setText(RepeatUtils.setRepeat(currentType, model.getRepeatCount()));
            if (model.getIsOpen() == null) {
                textViewsTime.get(i).setTextColor(this.getResources().getColor(R.color.common_text_grey_color));
                textViewReapter.get(i).setTextColor(this.getResources().getColor(R.color.common_text_grey_color));
                checkBoxes.get(i).setImageResource(R.drawable.icon_close);
                checkBoxes.get(i).setTag(CLOSE);
            } else {

                if (model.getIsOpen()) {
                    textViewsTime.get(i).setTextColor(this.getResources().getColor(R.color.common_white));
                    textViewReapter.get(i).setTextColor(this.getResources().getColor(R.color.common_white));
                } else {
                    textViewsTime.get(i).setTextColor(this.getResources().getColor(R.color.common_text_grey_color));
                    textViewReapter.get(i).setTextColor(this.getResources().getColor(R.color.common_text_grey_color));
                }
                checkBoxes.get(i).setImageResource(model.getIsOpen() ? R.drawable.icon_open : R.drawable.icon_close);
                checkBoxes.get(i).setTag(model.getIsOpen() ? OPEN : CLOSE);
            }
        }


        handler.sendEmptyMessage(0x00);

       /* List<AlarmEntry> listAlarmEntry = new ArrayList<>();
        for (int i = 0; i < bracelet_w311_displayModel.size(); i++) {
            String split[] = bracelet_w311_displayModel.get(i).getTimeString().split(":");
            int hour = Integer.parseInt(split[0]);
            int min = Integer.parseInt(split[1]);
            listAlarmEntry.add(new AlarmEntry(i, hour, min, (byte) bracelet_w311_displayModel.get(i).getRepeatCount(), bracelet_w311_displayModel.get(i).getIsOpen()));
        }
        list.clear();
        list.addAll(bracelet_w311_displayModel);
        adapter.refreshData(list);

        Logger.myLog("updateItem size():" + bracelet_w311_displayModel);
        for (int i = 0; i < listAlarmEntry.size(); i++) {
            Logger.myLog("listAlarmEntry:" + listAlarmEntry.get(i).toString());
        }*/


    }

    @Override
    public void successSaveAlarmItem() {
        mActPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
    }

    @Override
    public void successDelectAlarmItem() {
        mActPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
    }


    public void updateItem(Bracelet_W311_AlarmModel model) {
        try {
            if (model.getAlarmId() >= 0 && model.getAlarmId() < list.size()) {
                Bracelet_W311_AlarmModel w311 = list.get(model.getAlarmId());
                model.setTimeString(w311.getTimeString());
                model.setRepeatCount(w311.getRepeatCount());
            }
            mActPresenter.updateMode(model);
            //这里需要去发送指令个硬件
            String strtime = model.getTimeString();
            String[] hourMin = strtime.split(":");
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_ALARM, model.getIsOpen(), model.getRepeatCount(), Integer.parseInt(hourMin[0]), Integer.parseInt(hourMin[1]), model.getAlarmId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
