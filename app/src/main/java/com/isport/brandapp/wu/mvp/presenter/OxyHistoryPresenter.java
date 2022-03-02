package com.isport.brandapp.wu.mvp.presenter;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.presenter.UpgradeDataPresenter;
import com.isport.brandapp.repository.OxygenRepository;
import com.isport.brandapp.wu.bean.OxyInfo;
import com.isport.brandapp.wu.mvp.OxyHistoryView;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;

public class OxyHistoryPresenter extends BasePresenter<OxyHistoryView> implements IOxyHistoryPresenter {


    @Override
    public void getOxyHistoryData(int page) {
        new UpgradeDataPresenter(mActView.get()).getOxygenNumPageData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), page);
    }

    @Override
    public void getOxyNumData() {

        OxygenRepository.requstNumOxygenData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 50).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<OxyInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OxyInfo> infos) {
                if (isViewAttached()) {
                    mActView.get().getOxyHistoryDataSuccess(infos);
                }
                Logger.myLog("getOxyenNumData:" + infos);
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
    public void getOxyHistoryDataSuccess(List<OxyInfo> info) {
        if (isViewAttached()) {
            mActView.get().getOxyHistoryDataSuccess(info);
        }
    }
}
