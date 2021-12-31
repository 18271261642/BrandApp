package com.isport.brandapp.device.bracelet.braceletPresenter;

import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_AlarmModelAction;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;
import com.isport.blelibrary.db.table.watch_w516.Watch_W560_AlarmModel;
import com.isport.brandapp.device.bracelet.braceletModel.IW311SettingModel;
import com.isport.brandapp.device.bracelet.braceletModel.W311ModelSettingImpl;
import com.isport.brandapp.device.bracelet.view.AlarmView;

import java.util.ArrayList;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmPresenter extends BasePresenter<AlarmView> {
    private AlarmView view;
    IW311SettingModel model;

    public AlarmPresenter(AlarmView view) {
        this.view = view;
        model = new W311ModelSettingImpl();
    }

    /**
     *
     */
    public synchronized void getW526AllAralm(String userid, String deviceId) {
        Observable.create(new ObservableOnSubscribe<ArrayList<Bracelet_W311_AlarmModel>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Bracelet_W311_AlarmModel>> emitter) throws Exception {
                ArrayList<Bracelet_W311_AlarmModel> dispalyItem = model.getAllAlarmW526(deviceId, userid);
                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<ArrayList<Bracelet_W311_AlarmModel>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(ArrayList<Bracelet_W311_AlarmModel> bracelet_w311_displayModel) {
                if (view != null) {
                    view.successAllAlarmItem(bracelet_w311_displayModel);
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
                    view.successAllAlarmItem(null);
                }
            }

        });

    }

    public synchronized void deleteW560AllAlarms(String userid, String deviceId) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean alarmItem = model.deletW560ArarmItems(userid, deviceId);

                emitter.onNext(alarmItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), true) {
            @Override
            public void onNext(Boolean isSave) {
//                if (view != null) {
//                    view.successSaveAlarmItem();
//                }
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

    public synchronized void getW560AllAlarm(String userid, String deviceId) {
        Observable.create(new ObservableOnSubscribe<ArrayList<Watch_W560_AlarmModel>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Watch_W560_AlarmModel>> emitter) throws Exception {
                ArrayList<Watch_W560_AlarmModel> dispalyItem = model.getAllAlarmW560(deviceId, userid);
                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<ArrayList<Watch_W560_AlarmModel>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(ArrayList<Watch_W560_AlarmModel> watch_w560_alarmModels) {
                if (view != null) {
                    view.successW560AllAlarmItem(watch_w560_alarmModels);
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
                    view.successW560AllAlarmItem(null);
                }
            }

        });

    }

    /**
     * @param deviceId
     */
    public synchronized void getAllAralm(String userid, String deviceId) {
        Observable.create(new ObservableOnSubscribe<ArrayList<Bracelet_W311_AlarmModel>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Bracelet_W311_AlarmModel>> emitter) throws Exception {
                ArrayList<Bracelet_W311_AlarmModel> dispalyItem = model.getAllAlarm(deviceId, userid);
                if (dispalyItem == null) {
                    //  dispalyItem = new Bracelet_W311_AlarmModel(1l, userid, deviceId, true, false, false, false, false, false);
                }
                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<ArrayList<Bracelet_W311_AlarmModel>>(BaseApp.getApp(), false) {
            @Override
            public void onNext(ArrayList<Bracelet_W311_AlarmModel> bracelet_w311_displayModel) {
                if (view != null) {
                    view.successAllAlarmItem(bracelet_w311_displayModel);
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
                    view.successAllAlarmItem(null);
                }
            }

        });

    }

    public synchronized void updateAlarmItem(Bracelet_W311_AlarmModel alarmModel, boolean isShow) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean alarmItem = model.updateAlarmModel(alarmModel);

                emitter.onNext(alarmItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), isShow) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successSaveAlarmItem();
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

    public synchronized void saveW560AlarmItem(String deviceId, String userId, int repeat, String time, int index, String name) {
        Watch_W560_AlarmModel item = new Watch_W560_AlarmModel();
        item.setDeviceId(deviceId);
        item.setUserId(userId);
        item.setRepeatCount(repeat);
        item.setTimeString(time);
        item.setIsEnable(true);
        item.setIndex(index);
        item.setName(name);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean alarmItem = model.updateW560AlarmModel(item);

                emitter.onNext(alarmItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), true) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successSaveAlarmItem();
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

    public synchronized void saveAlarmItem(String deviceId, String userId, int repeat, String time) {
        Bracelet_W311_AlarmModel item = new Bracelet_W311_AlarmModel();
        item.setDeviceId(deviceId);
        item.setUserId(userId);
        item.setRepeatCount(repeat);
        item.setTimeString(time);
        item.setIsOpen(true);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean alarmItem = model.updateAlarmModel(item);

                emitter.onNext(alarmItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), true) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successSaveAlarmItem();
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

    public synchronized void deletW560ArarmItem(Watch_W560_AlarmModel item) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean dispalyItem = model.deletW560ArarmItem(item);

                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successDelectAlarmItem();
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

    public synchronized void deletArarmItem(Bracelet_W311_AlarmModel item) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean dispalyItem = model.deletArarmItem(item);

                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successDelectAlarmItem();
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

    public synchronized void updateMode(Bracelet_W311_AlarmModel item) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean dispalyItem = model.updateAlarmModel(item);
                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successSaveAlarmItem();
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

    public synchronized void updateW560Mode(Watch_W560_AlarmModel item) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean dispalyItem = model.updateW560AlarmModel(item);
                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean isSave) {
                if (view != null) {
                    view.successSaveAlarmItem();
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

    //更新所有闹钟
    public void saveAllAlarm(ArrayList<Bracelet_W311_AlarmModel> lists, String deviceId, String userId){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean dispalyItem = model.updateAllAlarmModel(lists,deviceId,userId);
                emitter.onNext(dispalyItem);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean isSave) {
//                if (view != null) {
//                    view.successSaveAlarmItem();
//                }
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


    //删除所有保存的通用闹钟
    public void deleteAllCommAlarm(String userId,String deviceId){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Bracelet_W311_AlarmModelAction.deletBracelet(deviceId,userId);
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(view.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean isSave) {
//                if (view != null) {
//                    view.successSaveAlarmItem();
//                }
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
}
