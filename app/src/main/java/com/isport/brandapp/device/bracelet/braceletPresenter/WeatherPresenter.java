package com.isport.brandapp.device.bracelet.braceletPresenter;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.entry.WristbandData;
import com.isport.blelibrary.entry.WristbandForecast;
import com.isport.blelibrary.entry.WristbandWeather;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.repository.WheatherRepository;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import brandapp.isport.com.basicres.mvp.BaseView;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class WeatherPresenter extends BasePresenter<BaseView> {
    BaseView baseView;

    public WeatherPresenter(BaseView baseView) {
        this.baseView = baseView;
    }

    public void getToadyWeather(double lan, double lon, String city, int deviceType) {
        WheatherRepository.requstTodayWeather(lan, lon, city, deviceType).as(baseView.bindAutoDispose()).subscribe(new BaseObserver<WristbandData>(BaseApp.getApp(), false) {
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
            public void onNext(WristbandData wristbandData) {
                if (AppConfiguration.isConnected) {
                    //ISportAgent.getInstance().requestBle(BleRequest.SET_TODAY_WHEATHER, wristbandData, city);
                }
            }
        });
    }

    public void get15DayWeather(double lan, double lon, String city, int deviceType) {
        WheatherRepository.requstWristbandForecast(lan, lon, city, deviceType).as(baseView.bindAutoDispose()).subscribe(new BaseObserver<List<WristbandForecast>>(BaseApp.getApp(), false) {
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
            public void onNext(List<WristbandForecast> list) {

                if (AppConfiguration.isConnected) {
                    // ISportAgent.getInstance().requestBle(BleRequest.SET_15DAY_WHEATHER, list);
                }
            }
        });
    }

    public void getWeather(double lan, double lon, String city, int deviceType) {
        WheatherRepository.requstWeather(lan, lon, city, deviceType).as(baseView.bindAutoDispose()).subscribe(new BaseObserver<WristbandWeather>(BaseApp.getApp(), false) {
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
            public void onNext(WristbandWeather wristbandData) {
                Constants.wristbandWeather = wristbandData;
                Logger.myLog("wristbandData:" + wristbandData.toString() +AppConfiguration.isConnected);

                if(deviceType == JkConfiguration.DeviceType.Watch_F18){
                    setF18Weather(city,wristbandData);
                    return;
                }

                if (AppConfiguration.isConnected) {
                    WristbandData wristbandData1 = wristbandData.getCondition();
                    List<WristbandForecast> list = wristbandData.getForecast15Days();

                    ISportAgent.getInstance().requestBle(BleRequest.SET_WHEATHER, wristbandData1, list, city);
                }
            }
        });
    }


    private void setF18Weather(String city,WristbandWeather wristbandData){
        Watch7018Manager.getWatch7018Manager().setWeatherData(city,wristbandData);
    }

}
