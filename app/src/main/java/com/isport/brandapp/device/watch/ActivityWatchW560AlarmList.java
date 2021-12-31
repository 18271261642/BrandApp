package com.isport.brandapp.device.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.bluetooth.callbacks.WatchW557GattCallBack;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.interfaces.BleReciveListener;
import com.isport.blelibrary.result.IResult;
import com.isport.blelibrary.result.impl.sleep.SleepBatteryResult;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Constants;
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
import com.isport.brandapp.device.watch.adapter.W560AlarmAdapter;
import com.isport.brandapp.util.ClickUtils;
import com.isport.brandapp.view.InputDialogView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import bike.gymproject.viewlibray.ItemView;
import bike.gymproject.viewlibray.pickerview.DatePickerView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;

/**
 * W560闹钟设置
 */
public class ActivityWatchW560AlarmList extends BaseMVPTitleActivity<AlarmView, AlarmPresenter> implements AlarmView, View.OnClickListener, ItemView.OnItemViewCheckedChangeListener {

    private final static String TAG = ActivityWatchW560AlarmList.class.getSimpleName();

    private DeviceBean deviceBean;
    private int currentType;
    private String deviceId;

    final String CLOSE = "close";
    final String OPEN = "open";

    ArrayList<Watch_W560_AlarmModel> list = new ArrayList<>();
    ListView listView;
    W560AlarmAdapter adapter;


    //无闹钟时显示
    private LinearLayout w560_alarm_layout_emty;
    private TextView alarmStatusTv;


    //输入闹钟名称的dialog
    private InputDialogView inputDialogView;

    //对应的操作 0：新增;1：修改;2：删除  默认为0x88为查询操作
    private int alarmOperate = 0x88;
    //临时的闹钟对象，用于对应的操作
    private Watch_W560_AlarmModel tmpAlarmBean;



    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                if(list.isEmpty())
                    return;
                saveAllAlarm(list);
            }

            if(msg.what == 0x01){
                mActPresenter.deleteAllCommAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
              //  mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
            }
        }
    };


    private void saveAllAlarm(ArrayList<Watch_W560_AlarmModel> lt){
        ArrayList<Bracelet_W311_AlarmModel> alarmList = new ArrayList<>();
        for(Watch_W560_AlarmModel watch_w560_alarmModel : lt){
            Bracelet_W311_AlarmModel bracelet_w311_alarmModel = new Bracelet_W311_AlarmModel();

            bracelet_w311_alarmModel.setId(watch_w560_alarmModel.getId());
            bracelet_w311_alarmModel.setAlarmId(watch_w560_alarmModel.getIndex());
            bracelet_w311_alarmModel.setIsOpen(watch_w560_alarmModel.getIsEnable());
            bracelet_w311_alarmModel.setDeviceId(watch_w560_alarmModel.getDeviceId());
            bracelet_w311_alarmModel.setRepeatCount(watch_w560_alarmModel.getRepeatCount());
            bracelet_w311_alarmModel.setUserId(watch_w560_alarmModel.getUserId());
            bracelet_w311_alarmModel.setMessageString(watch_w560_alarmModel.getMessageString());
            bracelet_w311_alarmModel.setTimeString(watch_w560_alarmModel.getTimeString());
            alarmList.add(bracelet_w311_alarmModel);
        }

        mActPresenter.saveAllAlarm(alarmList,TokenUtil.getInstance().getPeopleIdInt(this),deviceBean.deviceID);
    }



    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_w560_alarm_list;
    }

    @Override
    protected void initView(View view) {
        alarmStatusTv = findViewById(R.id.alarmStatusTv);
        w560_alarm_layout_emty = findViewById(R.id.w560_alarm_layout_emty);
        listView = findViewById(R.id.listView);
        adapter = new W560AlarmAdapter(ActivityWatchW560AlarmList.this, R.layout.app_activity_w560_alarm_item, R.layout.app_activity_w560_alarm_item, list);
        listView.setAdapter(adapter);

        //开关按钮
        adapter.setOnItemEnableListener(new W560AlarmAdapter.OnItemEnableListener() {
            @Override
            public void onEnableAlarm(int index, boolean enabled) {
                if (index >= 0 && index < list.size()) {
                    Watch_W560_AlarmModel model = list.get(index);
                    model.setIsEnable(enabled);
                    adapter.notifyDataSetChanged();
                    // 发送指令
                    updateItem(model);
                }
            }
        });



        // cell点击修改闹钟设置
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < list.size()) {
                    Watch_W560_AlarmModel model = list.get(position);

                    Logger.myLog(TAG,"-----修改闹钟="+new Gson().toJson(model));
                    startActivity(model);
                }
            }
        });

        // cell长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < list.size()) {
                    delDialog(list.get(position));
                }
                return true;
            }
        });
    }

    Watch_W560_AlarmModel currentAlarmModel;
    String editTime;
    int editRepeate;
    String editAlarmName;

    public void startActivity(Watch_W560_AlarmModel item) {
        currentAlarmModel = item;
        if (item.getRepeatCount() == 128) {
            currentAlarmModel.setRepeatCount(0);
        }
        editTime = currentAlarmModel.getTimeString();
        editRepeate = currentAlarmModel.getRepeatCount();
        editAlarmName = currentAlarmModel.getName();
        showAddOrEditDiloag(item.getTimeString(), true);

    }

    ItemView settingRepeat;
    ItemView alarmName;

    private void showAddOrEditDiloag(String itemTimeString, boolean isEdit) {
        BaseDialog dialog = new BaseDialog.Builder(this)
                .setContentView(R.layout.app_activity_bracelet_alarm_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setViewVisible(R.id.iv_watch_alarm_setting_name, View.VISIBLE)
                .setOnClickListener(R.id.iv_watch_alarm_setting_repeat, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(ClickUtils.isFastDoubleClick())
                                    return;
                                showReapeat(currentAlarmModel.getRepeatCount());
                                // dialogInterface.cancel();
                            }
                        }
                )
                .setOnClickListener(R.id.iv_watch_alarm_setting_name, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 修改闹钟名称
                                // 修改闹钟名称
                                if(inputDialogView == null)
                                inputDialogView = new InputDialogView(ActivityWatchW560AlarmList.this);
                                inputDialogView.show();
                                inputDialogView.setInputTxt(currentAlarmModel.getName());
                                inputDialogView.setTitleTv(R.string.watch_alarm_setting_name);
                                inputDialogView.setCancelBtnStr(getResources().getString(R.string.cancel));
                                inputDialogView.setConfirmBtnStr(getResources().getString(R.string.confirm));
                                inputDialogView.setHidStr(getResources().getString(R.string.watch_alarm_setting_name));
                                inputDialogView.setInputDialogListener(new InputDialogView.InputDialogListener() {
                                    @Override
                                    public void inputData(String str) {
                                        alarmName.setContentText(str);
                                        currentAlarmModel.setName(str == null ? getResources().getString(R.string.display_setting_alarm) : str);
                                    }
                                });
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
        alarmName = dialog.findViewById(R.id.iv_watch_alarm_setting_name);
        alarmName.setContentText(currentAlarmModel.getName() != null ? currentAlarmModel.getName() : "");

        TextView tvSave = dialog.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tvSave", "datePicker.getTime() = " + datePicker.getTime());
                currentAlarmModel.setTimeString(datePicker.getTime());
                if (currentAlarmModel.getTimeString().equals(editTime) && currentAlarmModel.getRepeatCount() == editRepeate && currentAlarmModel.getName().equals(editAlarmName))  {
                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.alarm_tips_reapte));
                    return;
                }
                if(currentAlarmModel.getName() == null)
                    currentAlarmModel.setName(getResources().getString(R.string.display_setting_alarm));
                currentAlarmModel.setIsEnable(true);
                alarmOperate = 1;
                showProgress("Loading...",false);
                //这里需要去发送指令个硬件
                String strtime = currentAlarmModel.getTimeString();
                String[] hourMin = strtime.split(":");
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W560_SET_ALARM, currentAlarmModel.getIsEnable(), currentAlarmModel.getRepeatCount(), Integer.parseInt(hourMin[0]), Integer.parseInt(hourMin[1]), currentAlarmModel.getIndex(), currentAlarmModel.getName());
                dialog.dismiss();
                tmpAlarmBean = currentAlarmModel;
                //adapter.notifyDataSetChanged();
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

    public void delDialog(Watch_W560_AlarmModel model) {
        PublicAlertDialog.getInstance().showDialog("", this.getString(R.string.ensure_delete), context, this.getString(R.string.common_dialog_cancel), this.getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        alarmOperate = 2;
                        delItem(model);
                    }

                    @Override
                    public void cancel() {

                    }
                }
                , false);
    }

    public void delItem(Object obj) {
        if (obj == null || list == null) {
            return;
        }
        //list.remove(obj);
        if (!AppConfiguration.isConnected) {
            ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
            return;
        }
        deleteItem((Watch_W560_AlarmModel) obj);
        //删除item  12 35 50 8D 04 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00
    }

    public void deleteItem(Watch_W560_AlarmModel model) {
        Logger.myLog(TAG,"-------删除闹钟="+model.toString());
        // 发送指令删除
        ISportAgent.getInstance().requestBle(BleRequest.Watch_W560_DELETE_ALARM, model.getIndex());
        this.tmpAlarmBean = model;
        showProgress("Loading...",false);
//        mActPresenter.deletW560ArarmItem(model);
      //  handler.sendEmptyMessageDelayed(0x01,1000);
    }

    @Override
    protected void initData() {

        getIntentData();
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(context.getResources().getString(R.string.watch_alarm_setting_str));
        titleBarView.setRightText("");
        titleBarView.setRightIcon(R.drawable.icon_add_device);
        frameBodyLine.setVisibility(View.VISIBLE);
        ISportAgent.getInstance().registerListener(mBleReciveListener);

        IntentFilter intentFilter = new IntentFilter(WatchW557GattCallBack.W560_ALARM_OPERATE_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);

        titleBarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_ALARM);
                return true;
            }
        });

        // 先删除本地闹钟
        mActPresenter.deleteW560AllAlarms(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);

        //mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
        if (AppConfiguration.isConnected && AppConfiguration.hasSynced) {

//            BleProgressObservable.getInstance().show();
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_ALARM);
        } else {
            ToastUtil.init(this);
            ToastUtil.showTextToast("手表未连接!");
            //mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
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
                if (list.size() < 5) {
                    alarmOperate = 0;
                    showAddDiloag();

                } else {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.only_add_five_alarm));
                }
            }
        });
    }

    private void showAddDiloag() {
        currentAlarmModel = new Watch_W560_AlarmModel();
        BaseDialog dialog = new BaseDialog.Builder(this)
                .setContentView(R.layout.app_activity_bracelet_alarm_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setViewVisible(R.id.iv_watch_alarm_setting_name, View.VISIBLE)
                .setOnClickListener(R.id.iv_watch_alarm_setting_repeat, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(ClickUtils.isFastDoubleClick())
                                    return;
                                showReapeat(currentAlarmModel.getRepeatCount());
                                // dialogInterface.cancel();
                            }
                        }
                )
                .setOnClickListener(R.id.iv_watch_alarm_setting_name, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // 修改闹钟名称
                                if(inputDialogView == null)
                                 inputDialogView = new InputDialogView(ActivityWatchW560AlarmList.this);
                                inputDialogView.show();
                                inputDialogView.setTitleTv(R.string.watch_alarm_setting_name);
                                inputDialogView.setCancelBtnStr(getResources().getString(R.string.cancel));
                                inputDialogView.setConfirmBtnStr(getResources().getString(R.string.confirm));
                                inputDialogView.setHidStr(getResources().getString(R.string.watch_alarm_setting_name));
                                inputDialogView.setInputDialogListener(new InputDialogView.InputDialogListener() {
                                    @Override
                                    public void inputData(String str) {
                                        alarmName.setContentText(str);
                                        currentAlarmModel.setName(str == null ? getResources().getString(R.string.display_setting_alarm) : str);
                                    }
                                });

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
        alarmName = dialog.findViewById(R.id.iv_watch_alarm_setting_name);
        alarmName.setContentText(currentAlarmModel.getName() != null ? currentAlarmModel.getName() : "");

        TextView tvSave = dialog.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tvSave", "datePicker.getTime() = " + datePicker.getTime() + "list" + list.size());
                currentAlarmModel.setTimeString(datePicker.getTime());

                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        String time = list.get(i).getTimeString();
                        //  String selecttime = datePicker.getTime();
                        //   Logger.myLog("time:" + time + "selecttime:" + selecttime + "alarmModels.get(i).getRepeatCount():" + alarmModels.get(i).getRepeatCount());

                        if (currentAlarmModel.getTimeString().equals(editTime) && currentAlarmModel.getRepeatCount() == editRepeate && currentAlarmModel.getName().equals(editAlarmName))  {
                            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.alarm_tips_reapte));
                            dialog.dismiss();
                            return;
                        }
                    }
                }

                //这里需要去发送指令个硬件
                String strtime = currentAlarmModel.getTimeString();
                String[] hourMin = strtime.split(":");
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W560_ADD_ALARM, currentAlarmModel.getRepeatCount(), Integer.parseInt(hourMin[0]), Integer.parseInt(hourMin[1]), currentAlarmModel.getName());
                dialog.dismiss();
                tmpAlarmBean = currentAlarmModel;
            }

        });
        datePicker.setType(3);
        // datePicker.setDefaultItemAdapter(itemTimeString);
        // settingRepeat.setContentText(RepeatUtils.setRepeat(deviceBean.deviceType, currentAlarmModel.getRepeatCount()));
        datePicker.setDefaultItemAdapter(Constants.defStartTime);
        settingRepeat.setContentText(UIUtils.getString(R.string.only_once));
        // alarmPresenter.getAllAralm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceName);
        datePicker.setCyclic(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ISportAgent.getInstance().unregisterListener(mBleReciveListener);
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
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
                Logger.myLog(TAG,"-----HandlerContans.mDevcieAlarList2" + mResult.getType());
            switch (mResult.getType()) {
                case IResult.SLEEP_BATTERY:
                    SleepBatteryResult mResult1 = (SleepBatteryResult) mResult;
                    // TODO: 2018/10/17 更新列表电量
                    break;
                case IResult.DEVICE_ALARM_LIST:
                    BleProgressObservable.getInstance().hide();
                    Logger.myLog("HandlerContans.mDevcieAlarList w560");
                   // mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                    break;
                case IResult.WATCH_W560_ALARM_SETTING:  //闹钟获取成功
                    //闹钟设置回调
                   // mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                    break;
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
              //  mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                break;
            default:
                break;
        }
    }


    ArrayList<Watch_W560_AlarmModel> tmpList = new ArrayList<>();
    @Override
    public void successW560AllAlarmItem(ArrayList<Watch_W560_AlarmModel> watch_w560_alarmModels) {
        tmpList.clear();
        list.clear();
        Logger.myLog(TAG,"---------successAllAlarmItem:" + new Gson().toJson(watch_w560_alarmModels)+"\n"+new Gson().toJson(list));
        if (watch_w560_alarmModels == null) {
            alarmStatusTv.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.GONE);
            w560_alarm_layout_emty.setVisibility(View.VISIBLE);
            mActPresenter.deleteW560AllAlarms(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
            handler.sendEmptyMessage(0x01);
          //  mActPresenter.deleteAllCommAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
            return;
        }
        listView.setVisibility(View.VISIBLE);
        w560_alarm_layout_emty.setVisibility(View.GONE);
        alarmStatusTv.setVisibility(View.VISIBLE);
        if(watch_w560_alarmModels.size()>5){
            for(int i = 0;i<5;i++){
                Watch_W560_AlarmModel watch_w560_alarmModel = watch_w560_alarmModels.get(i);
                list.add(watch_w560_alarmModel);
            }
        }else{
            list.addAll(watch_w560_alarmModels);
        }

      //  list.addAll(watch_w560_alarmModels);

        adapter.notifyDataSetChanged();
       // listView.setAdapter(adapter);
        handler.sendEmptyMessage(0x00);
        titleBarView.setRightIconVisible(list.size() < 5 ? true : false);
    }

    @Override
    public void successAllAlarmItem(ArrayList<Bracelet_W311_AlarmModel> bracelet_w311_displayModel) {
        // 不作处理
    }

    @Override
    public void successSaveAlarmItem() {
        mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
    }

    @Override
    public void successDelectAlarmItem() {
       // mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
    }


    public void updateItem(Watch_W560_AlarmModel model) {
        try {
            if (model.getIndex() >= 0 && model.getIndex() < list.size()) {
                Watch_W560_AlarmModel w560 = list.get(model.getIndex());
                model.setTimeString(w560.getTimeString());
                model.setRepeatCount(w560.getRepeatCount());
            }

            //这里需要去发送指令个硬件
            String strtime = model.getTimeString();
            String[] hourMin = strtime.split(":");
            if(model.getName() == null)
                model.setName(getResources().getString(R.string.display_setting_alarm));
            this.alarmOperate = 1;
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W560_SET_ALARM, model.getIsEnable(), model.getRepeatCount(), Integer.parseInt(hourMin[0]), Integer.parseInt(hourMin[1]), model.getIndex(), model.getName());
            this.tmpAlarmBean = model;
            showProgress("Loading...",false);
//            // 刷新本地数据库
//            mActPresenter.updateW560Mode(model);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(WatchW557GattCallBack.W560_ALARM_OPERATE_ACTION)){
                boolean isAlarmSuccess = intent.getBooleanExtra(WatchW557GattCallBack.W560_ALARM_KEY,false);
                Logger.myLog(TAG,"-------操作状态="+isAlarmSuccess+"\n"+alarmOperate+"\n");
                if(!isAlarmSuccess){
                    ToastUtil.init(ActivityWatchW560AlarmList.this);
                    ToastUtil.showTextToast("操作失败!");
                    dismissProgressBar();
                    return;
                }

                if(alarmOperate == 0x88){
                    mActPresenter.getW560AllAlarm(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                }

                if(tmpAlarmBean == null)
                    return;
                dismissProgressBar();
                Logger.myLog(TAG,"--+tmpAlarmBean.toString()="+tmpAlarmBean.toString());
                if(alarmOperate == 0){  //新增
                    mActPresenter.saveW560AlarmItem(deviceId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), tmpAlarmBean.getRepeatCount(), tmpAlarmBean.getTimeString(), list.size(), tmpAlarmBean.getName());
                }

                if(alarmOperate == 1){      //修改
                    // 刷新本地数据库
                    mActPresenter.updateW560Mode(tmpAlarmBean);
                }

                if(alarmOperate == 2){      //删除 ，删除后重新再从手表读一遍数据
                    mActPresenter.deleteW560AllAlarms(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceId);
                    alarmOperate = 0x88;
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_GET_ALARM);
                    tmpAlarmBean = null;
                }

            }
        }
    };


}
