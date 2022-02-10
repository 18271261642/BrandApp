package com.isport.brandapp.device.f18;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.action.f18.F18DeviceSetAction;
import com.isport.blelibrary.db.table.f18.F18CommonDbBean;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.utils.SyncCacheUtils;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.bind.bean.BindInsertOrUpdateBean;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.history.util.HistoryParmUtil;
import com.isport.brandapp.parm.db.DeviceTypeParms;
import com.isport.brandapp.repository.UpdateResposition;
import com.isport.brandapp.util.AppSP;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import bike.gymproject.viewlibray.ItemView;
import bike.gymproject.viewlibray.pickerview.ArrayPickerView;
import bike.gymproject.viewlibray.pickerview.DatePickerView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.action.BaseAction;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Admin
 * Date 2022/1/17
 */
public class F18SetPresent extends BasePresenter<F18SetView> {

    private static final String TAG = "F18SetPresent";

    F18SetView view;

    private F18AlarmRepeatView f18AlarmRepeatView;

    public F18SetPresent(F18SetView f18SetView) {
        this.view = f18SetView;
    }


    public void getAllDeviceSet(String userId,String deviceMac){
        Log.e(TAG,"----查询设置="+userId+" "+deviceMac);
        if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceMac))
            return;
        Observable.create(new ObservableOnSubscribe<F18DeviceSetData>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<F18DeviceSetData> observableEmitter) throws Exception {
                F18CommonDbBean f18CommonDbBean = F18DeviceSetAction.querySingleBean(userId,deviceMac, F18DbType.F18_DEVICE_SET_TYPE);

                if(f18CommonDbBean != null){
                    String str = f18CommonDbBean.getTypeDataStr();
                    F18DeviceSetData f18DeviceSetData = new Gson().fromJson(str,F18DeviceSetData.class);
                    observableEmitter.onNext(f18DeviceSetData);
                    observableEmitter.onComplete();
                }



            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<F18DeviceSetData>(BaseApp.getApp(),false) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

            @Override
            public void onNext(@NonNull F18DeviceSetData f18DeviceSetData) {
                if(view != null)
                    view.backAllSetData(f18DeviceSetData);
            }
        });
    }

    //保存数据
    public void saveAllSetData(String userId,String deviceMac,String type,String contentData){
        F18DeviceSetAction.saveOrUpdateF18DeviceSet(userId,deviceMac,type,contentData);
    }

    //时间选择，HH:mm格式
    public void setDateTimeSelectView(Context context, int selectType, int type, String defaultDay){
        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_date)
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
        final DatePickerView datePicker = (DatePickerView) mMenuViewBirth.findViewById(R.id.datePicker);
        if(datePicker == null)
            return;
        datePicker.setType(type);
        datePicker.setDefaultItemAdapter(defaultDay);
        datePicker.setCyclic(false);

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ViewMultiClickUtil.onMultiClick()){
                    return;
                }
                if (isViewAttached()) {
                    mActView.get().backSelectDateStr(selectType, type, datePicker.getTime());
                }
                if (mMenuViewBirth.isShowing()) {
                    mMenuViewBirth.dismiss();
                }
            }
        });
    }




    private String tmpSelectStr = null;
    public void setSignalValue(Context context, int selectType, int type, int defaultIndex){
        tmpSelectStr = null;
        String[] strArray = new String[]{"15","30","45","60"};

        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_bottom_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(true)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setOnClickListener(R.id.tv_determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isViewAttached()) {
                            mActView.get().backSelectDateStr(selectType,type,tmpSelectStr == null ? strArray[0] : tmpSelectStr);
                        }
                        dialogInterface.cancel();
                    }
                })
                .show();

        ArrayPickerView datePicker =  mMenuViewBirth.findViewById(R.id.datePicker);
        if(datePicker == null)
            return;
        datePicker.setData(strArray);
        datePicker.setCyclic(true);
        datePicker.setSelectItem(defaultIndex);
        datePicker.setItemOnclick(new ArrayPickerView.ItemSelectedValue() {
            @Override
            public void onItemSelectedValue(String str) {
                tmpSelectStr = str;
            }
        });
    }


    /**
     * 设置选项
     * @param context 上下文
     * @param selectType 区分选择type
     * @param cyclic 是否循环
     * @param defaultIndex 默认下标
     * @param sourceList 数据源
     */
    public void setSignalValue(Context context, int selectType, boolean cyclic, int defaultIndex, ArrayList<String> sourceList){
        tmpSelectStr = null;
        BaseDialog mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_pop_bottom_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(true)
                .fromBottom(true)
                .setOnClickListener(R.id.tv_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setOnClickListener(R.id.tv_determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isViewAttached()) {
                            mActView.get().backSelectDateStr(selectType,0,tmpSelectStr == null ? sourceList.get(0) : tmpSelectStr);
                        }
                        dialogInterface.cancel();
                    }
                })
                .show();

        ArrayPickerView datePicker =  mMenuViewBirth.findViewById(R.id.datePicker);
        if(datePicker == null)
            return;
        datePicker.setData(sourceList);
        datePicker.setCyclic(cyclic);
        datePicker.setSelectItem(defaultIndex);
        datePicker.setItemOnclick(new ArrayPickerView.ItemSelectedValue() {
            @Override
            public void onItemSelectedValue(String str) {
                tmpSelectStr = str;
            }
        });

    }


    int repeat;
    public void setAlarmOrUpdateAlarm(Context context,int selectType,int alarmRepeat){


        BaseDialog dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.app_activity_bracelet_alarm_setting)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.iv_watch_alarm_setting_repeat, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ViewMultiClickUtil.onMultiClick()) {
                                    return;
                                }
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

        ItemView settingRepeat = dialog.findViewById(R.id.iv_watch_alarm_setting_repeat);
        settingRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f18AlarmRepeatView = new F18AlarmRepeatView(context);
                f18AlarmRepeatView.show();
                f18AlarmRepeatView.setmRepeat(alarmRepeat);
                f18AlarmRepeatView.setF18ItemClickListener(new OnF18ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        repeat = position;
                        settingRepeat.setTitleText(CusF18AlarmAdapter.repeatToSimpleStr(repeat));
                    }

                    @Override
                    public void onChildClick(int position) {

                    }
                });
            }
        });
        TextView tvSave = dialog.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                Log.e("tvSave", "datePicker.getTime() = " + datePicker.getTime());
                dialog.dismiss();
                mActView.get().backSelectDateStr(selectType,repeat,datePicker.getTime());

            }

        });
        datePicker.setType(3);
        // if (isEdit) {
      //  datePicker.setDefaultItemAdapter(itemTimeString);
        datePicker.setCyclic(false);
    }


    /**
     * 解绑操作
     *
     * @param deviceBean
     */
    public void unBind(DeviceBean deviceBean, boolean isDirctUnbind) {
        SyncCacheUtils.clearSetting(BaseApp.getApp());
        SyncCacheUtils.clearStartSync(BaseApp.getApp());
        SyncCacheUtils.clearSysData(BaseApp.getApp());
//        if (isDirctUnbind) {
//            deletCurrentDay(deviceBean.deviceName, deviceBean.currentType);
//        }
        //scanModel.unBind(userId, deviceId, deviceType);
        //如果是isDirctUnbind 需要把当天不完整的数据给删除了，需要把当天的数据删除

        UpdateResposition<Integer, BindInsertOrUpdateBean, BaseUrl, DeviceTypeParms> customRepository = new
                UpdateResposition();
        customRepository.update(HistoryParmUtil.setDevice(deviceBean)).as(view.bindAutoDispose()).subscribe(new BaseObserver<Integer>(context) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                //假网状态
                NetProgressObservable.getInstance().hide();
                SyncCacheUtils.setUnBindState(false);
            }

            @Override
            public void onNext(Integer s) {
                SyncCacheUtils.setUnBindState(false);
                NetProgressObservable.getInstance().hide();
                com.isport.blelibrary.utils.Logger.myLog("解绑成功");
                AppSP.putString(context, AppSP.FORM_DFU, "false");
                ISportAgent.getInstance().deleteDeviceType(deviceBean.currentType, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                BleAction.deletAll();
                BaseAction.dropDatas();

              //  mActView.get().onUnBindSuccess();
                mActView.get().backSelectDateStr(-1,-1,"");
            }
        });
    }

}
