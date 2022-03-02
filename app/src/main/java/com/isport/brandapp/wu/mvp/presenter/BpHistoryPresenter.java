package com.isport.brandapp.wu.mvp.presenter;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.repository.BPRepository;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.mvp.BpHistoryView;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BasePresenter;

public class BpHistoryPresenter extends BasePresenter<BpHistoryView> implements IBpHistoryPresenter {

    @Override
    public void getBpHistoryData(int page) {

        BPRepository.requstPageNumBPData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), page).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<BPInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<BPInfo> infos) {
                if (isViewAttached()) {
                    mActView.get().getBpHistorySuccess(infos);
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
    public void getBpNumData() {

        BPRepository.requstNumBPData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), 50).as(mActView.get().bindAutoDispose()).subscribe(new BaseObserver<List<BPInfo>>(BaseApp.getApp(), false) {

            @Override
            public void onNext(List<BPInfo> infos) {
                if (isViewAttached()) {
                    mActView.get().getBpHistorySuccess(infos);
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
    public void getBpHistoryDataSuccess(List<BPInfo> info) {
        if (isViewAttached()) {
            mActView.get().getBpHistorySuccess(info);
        }
    }
}
