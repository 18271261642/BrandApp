package com.isport.brandapp.wu.mvp.presenter;

import android.text.TextUtils;

import com.isport.blelibrary.db.table.DeviceTempUnitlTable;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.bracelet.braceletModel.IW311SettingModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311ModelSettingImpl;
import com.isport.brandapp.repository.TempRepository;
import com.isport.brandapp.wu.bean.TempInfo;
import com.isport.brandapp.wu.mvp.TempHistoryView;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TempHistoryPresenter extends BasePresenter<TempHistoryView> implements ITempHistoryPresenter {

    IW81DeviceDataModel w81DeviceDataModel;
    IW311SettingModel w311ModelSetting;

    @Override
    public void getTempHistoryData(int page) {
        TempRepository.requstPageTempData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), page).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<TempInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<TempInfo> infos) {
                if (isViewAttached()) {
                    setState(infos);
                    mActView.get().getTempHistorySuccess(infos);
                }
                Logger.myLog("getBpNumData:" + infos);
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });
    }

    @Override
    public void getTempNumData() {
        TempRepository.requstNumTempData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<TempInfo>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(List<TempInfo> infos) {
                if (isViewAttached()) {
                    setState(infos);
                    mActView.get().getTempHistorySuccess(infos);
                }
                Logger.myLog("getBpNumData:" + infos);
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }
        });
    }

    @Override
    public void getTempUnitl(String deviceId, String userId) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                IW311SettingModel w311ModelSetting = new W311ModelSettingImpl();
                DeviceTempUnitlTable table = w311ModelSetting.getTempUtil(userId, deviceId);

                Logger.myLog("DeviceTempUnitlTable:" + table);

                String tempUnitl;

                if (table == null) {
                    tempUnitl = "0";
                } else {
                    tempUnitl = table.getTempUnitl();
                }


                emitter.onNext(tempUnitl);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<String>(BaseApp.getApp()) {
            @Override
            public void onNext(String stateBean) {
                if (isViewAttached()) {
                    mActView.get().getTempUtil(stateBean);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                if (isViewAttached()) {
                    mActView.get().getTempUtil("0");
                }
            }

        });

    }

    @Override
    public void getTempNumberHistory(String deviceId, String userId, int number) {
        Observable.create(new ObservableOnSubscribe<List<TempInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TempInfo>> emitter) throws Exception {
                w311ModelSetting = new W311ModelSettingImpl();
                w81DeviceDataModel = new W81DeviceDataModelImp();
                List<TempInfo> info = w81DeviceDataModel.getLastNumberTempData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 7);
                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<TempInfo>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(List<TempInfo> tempInfo) {
                if (isViewAttached()) {
                    // setState(tempInfo);
                    mActView.get().getTempHistorySuccess(tempInfo);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }

    @Override
    public void getLastTempData(String deviceId, String userId) {
        Observable.create(new ObservableOnSubscribe<TempInfo>() {
            @Override
            public void subscribe(ObservableEmitter<TempInfo> emitter) throws Exception {
                w311ModelSetting = new W311ModelSettingImpl();
                w81DeviceDataModel = new W81DeviceDataModelImp();
                TempInfo info = w81DeviceDataModel.getTempInfoeLastData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                DeviceTempUnitlTable table = w311ModelSetting.getTempUtil(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), AppConfiguration.braceletID);
                if (table != null) {
                    info.setTempUnitl(table.getTempUnitl());
                } else {
                    info.setTempUnitl("0");
                }

                emitter.onNext(info);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<TempInfo>(BaseApp.getApp(), false) {
            @Override
            public void onNext(TempInfo tempInfo) {
                if (isViewAttached()) {
                    if(mActView == null)
                        return;
                    mActView.get().getLastTempSuccess(tempInfo);
                }
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });


    }


    public void setState(List<TempInfo> infos) {

        float currentC = 0.0f;
        String strCurrentC = "";
        TempInfo info;
        for (int i = 0; i < infos.size(); i++) {
            info = infos.get(i);
            strCurrentC = info.getCentigrade();
            if (TextUtils.isEmpty(strCurrentC)) {
                strCurrentC = "0";
            }
            currentC = Float.parseFloat(strCurrentC);
            if (currentC < 37.3f) {
                info.setState(UIUtils.getString(R.string.temp_norm_temp_state));
                info.setColor(UIUtils.getColor(R.color.color_temp_normal));
                info.setResId(1);
            } else if (currentC >= 37.3f && currentC < 38.1f) {
                info.setState(UIUtils.getString(R.string.temp_low_fever_state));
                info.setColor(UIUtils.getColor(R.color.color_temp_low_fever));
                info.setResId(2);
            } else if (currentC >= 38.1f) {
                info.setState(UIUtils.getString(R.string.temp_high_fever_state));
                info.setColor(UIUtils.getColor(R.color.color_temp_high_fever));
                info.setResId(3);
            }
        }
    }
}
