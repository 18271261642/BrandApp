package com.isport.brandapp.wu.mvp.presenter;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.presenter.UpgradeDataPresenter;
import com.isport.brandapp.repository.OnceHrRepository;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.mvp.OnceHrHistoryView;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;

public class OnceHrHistoryPresenter extends BasePresenter<OnceHrHistoryView> implements IOnceHrHistoryPresenter {


    @Override
    public void getOnceHrHistoryData(int page) {
        new UpgradeDataPresenter(mActView.get()).getOnceHrNumPageData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), page);
    }

    @Override
    public void getOnceHrNumData() {

        OnceHrRepository.requstOnceHrData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 100).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<OnceHrInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<OnceHrInfo> infos) {
                if (isViewAttached()) {
                    mActView.get().getOnceHrHistoryDataSuccess(infos);
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
    public void getOnceHrHistoryDataSuccess(List<OnceHrInfo> info) {
        if (isViewAttached()) {
            mActView.get().getOnceHrHistoryDataSuccess(info);
        }
    }
}
