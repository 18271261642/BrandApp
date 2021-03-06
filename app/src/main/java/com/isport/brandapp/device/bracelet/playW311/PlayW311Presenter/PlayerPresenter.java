package com.isport.brandapp.device.bracelet.playW311.PlayW311Presenter;

import android.os.Handler;

import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.playW311.bean.PlayBean;
import com.isport.brandapp.device.bracelet.playW311.view.PlayerView;
import com.isport.brandapp.repository.PlayBandRepository;
import com.isport.brandapp.util.UserAcacheUtil;

import java.util.ArrayList;
import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.BasePresenter;

public class PlayerPresenter extends BasePresenter<PlayerView> {
    private PlayerView view;

    public PlayerPresenter(PlayerView view) {
        this.view = view;
    }


    public void getPlayBanImage(int devcieType) {
        PlayBandRepository.requstGetPlayBandImage(devcieType).subscribe(new BaseObserver<List<PlayBean>>(BaseApp.getApp(), false) {


            @Override
            public void onNext(List<PlayBean> infos) {
                UserAcacheUtil.savePlayBandInfo(devcieType, infos);
                com.isport.blelibrary.utils.Logger.myLog("PlayerPresenter devcieType:" + devcieType + ",getPlayBanImage:" + infos);
                if (view != null) {
                    view.successPlayerSuccess();
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

                if (view != null) {
                    view.onRespondError(UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                }
            }
        });
    }



    public void getF18AudioGuidData(int deviceType){
        List<PlayBean> list = new ArrayList<>();
        String gifUrl1 = "https://isportcloud.oss-cn-shenzhen.aliyuncs.com/manager/f18file/1642672700816_watch_connect.gif";
        String gifUrl2 = "https://isportcloud.oss-cn-shenzhen.aliyuncs.com/manager/f18file/1642672446971_phone_connect_cn.gif";

        String gifEnUrl2 = "https://isportcloud.oss-cn-shenzhen.aliyuncs.com/manager/f18file/1642672503766_phone_connect_en.gif";

        /**
         * id : 24
         * deviceType : 812
         * fileName : w812-1.gif
         * title : ????????????????????????
         * titleEn : Slide screen switching function
         * content : ?????????????????????????????????????????????????????????
         * contentEn : Swipe the screen up or down to switch steps, heart rate, sleep, etc.
         * url : https://manager.fitalent.com.cn/static/2019/11/2/11-21-35-301781.gif
         * seq : 10
         * createTime : 2019-12-01T19:21:34.000+0000
         * extend1 : null
         * extend2 : null
         * extend3 : null
         */
        PlayBean bp1 = new PlayBean();
        bp1.setDeviceType(deviceType);
        bp1.setFileName("f18-1.gif");
        bp1.setTitle1("???????????????????????????????????????????????????????????????????????????");
        bp1.setTitleEn1("Swipe screen switch function");
        bp1.setTitle1Content1("????????? ???????????????????????????????????????????????????");
        bp1.setTitleEn1Content1("Step 1: The watch enters the switch interface and turns on the audio mode switch");
        bp1.setUrl1(gifUrl1);
        bp1.setUrlEn1(gifUrl1);
        list.add(bp1);

        PlayBean bp2 = new PlayBean();
        bp2.setDeviceType(deviceType);
        bp2.setFileName("f18-2.gif");
        bp2.setTitle1("?????????");
        bp2.setTitleEn1("Step 2");
        bp2.setTitleEn1("");
        bp2.setTitle1Content1("???????????????????????????????????????????????????");
        bp2.setTitleEn1Content1("The mobile phone searches for the corresponding audio Bluetooth and connects");
        bp2.setUrl1(gifUrl2);
        bp2.setUrlEn1(gifEnUrl2);
        list.add(bp2);
        UserAcacheUtil.savePlayBandInfo(deviceType, list);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.successPlayerSuccess();
                }
            }
        },500);

    }


}
