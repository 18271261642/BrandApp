package com.isport.brandapp.sport.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.isport.blelibrary.utils.ThreadSinglePoolUtils;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.sport.InDoorSportActivity;
import com.isport.brandapp.sport.bean.ArrayThree;
import com.isport.brandapp.sport.bean.IndoorRunDatas;
import com.isport.brandapp.sport.bean.PaceBean;
import com.isport.brandapp.sport.bean.runargs.ArgsForInRunService;
import com.isport.brandapp.sport.service.persenter.IndoorRunningServicePersenter;
import com.isport.brandapp.util.StepsUtils;
import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepService;
import com.uber.autodispose.AutoDisposeConverter;

import java.util.concurrent.TimeUnit;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.mvp.BaseService;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.service.observe.IndoorRunObservable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class InDoorService extends BaseService<IndoorRunningServiceView, IndoorRunningServicePersenter> {

    private static final String TAG = "InDoorService";
    
    // 用静态变量记录下当前时刻跑步的数据。
    public static final IndoorRunDatas theMomentRunData = new IndoorRunDatas();


    public static int sportType = JkConfiguration.SportType.sportIndoor;

    private NotificationManager notiManager;

    // 记录下跑步时的状态。
    public static ArgsForInRunService argsForInRunService = new ArgsForInRunService();

    /*
     * 跑步是否为暂停，用来记录activity的跑步状态。
     */
    public static boolean inRunIsPause = false;
    public static int pauseCount = 0;

    static UserInfoBean userInfo;

    @Override
    protected IndoorRunningServicePersenter createPresenter() {
        return new IndoorRunningServicePersenter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        userInfo = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
        argsForInRunService.clearData();
        argsForInRunService.phoneStartTime = System.currentTimeMillis();

        theMomentRunData.clearData();
        inRunIsPause = false;
        isFirst = true;
        isFirstPace = true;

        //所有的数据都恢复默认值

        //当前的运动类型
        sportType = TokenUtil.getInstance().getCurrentSportType(BaseApp.getApp());
        pauseCount = 0;
        arrayThreePhoneVelocityTool = new ArrayThree();
        arrayThreePhoneVelocityTool.cleanAtRunFinish();

        com.isport.blelibrary.utils.Logger.myLog(" IndoorService onStartCommand------------");
        // 在API11之后构建Notification的方式
    /*    try {
            if (ActivityCompat.checkSelfPermission(BaseApp.getApp(), Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE) != PackageManager
                    .PERMISSION_GRANTED) {
                Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
                Intent nfIntent = new Intent(this, InDoorSportActivity.class);
                notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置 PendingIntent
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_t_launcher)) // 设置下拉列表中的图标(大图标)
                        .setContentTitle(UIUtils.getString(R.string.application_keep_running)) // 设置下拉列表里的标题
                        .setSmallIcon(R.drawable.ic_t_launcher) // 设置状态栏内的小图标
                        .setContentText("") // 设置上下文内容
                        .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//修改安卓8.1以上系统报错
                    NotificationChannel notificationChannel = new NotificationChannel("notification_id", "bonlala", NotificationManager.IMPORTANCE_MIN);
                    notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
                    notificationChannel.setShowBadge(false);//是否显示角标
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
                    // NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notiManager.createNotificationChannel(notificationChannel);
                    builder.setChannelId("notification_id");
                }
                builder.setAutoCancel(true);//用户点击就自动消失
                Notification notification = builder.build(); // 获取构建好的Notification
                notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
                notiManager.notify(110 *//* ID of notification *//*, notification);  //这就是那个
                // 参数一：唯一的通知标识；参数二：通知消息。
                startForeground(110, notification);// 开始前台服务

            } else {

            }

        } catch (Exception e) {

        }
*/
        // flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, InDoorSportActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel("notification_id", "bonlala", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notiManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("notification_id");
        }
        builder.setAutoCancel(true);//用户点击就自动消失
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置 PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), com.today.step.lib.R.drawable.ic_t_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("") // 设置下拉列表里的标题
                .setSmallIcon(com.today.step.lib.R.drawable.ic_t_launcher) // 设置状态栏内的小图标
                .setContentText("") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间*/
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        // notiManager.notify(110 /* ID of notification */, notification);  //这就是那个
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务

        startServiceToday();
    }


    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private int mFirstStepSum;//连上第一次的步数
    private int sumStepSum;

    private static ISportStepInterface iSportStepInterface;
    private static final int REFRESH_STEP_WHAT = 0;
    Intent intent;


    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Activity和Service通过aidl进行通信
            iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
            try {
                // mFirstStepSum = iSportStepInterface.getCurrentTimeSportStep();
                int phoneStep = iSportStepInterface.getCurrentTimeSportStep();
                Log.e(TAG,"-------手机步数="+phoneStep);
                argsForInRunService.phonePauseStep = iSportStepInterface.getCurrentTimeSportStep();
                sumStepSum = 0;
                //updateStepCount();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
            startTimer();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startServiceToday() {
        try {
            intent = new Intent(this, TodayStepService.class);
            //开启计步Service，同时绑定Activity进行aidl通信
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                com.isport.blelibrary.utils.Logger.myLog(" IndoorService onStartCommand------------startServiceToday");
                // startService(intent);
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            //startService(intent);
            com.isport.blelibrary.utils.Logger.myLog(" IndoorService onStartCommand------------serviceConnection" + serviceConnection);
            if (serviceConnection != null) {
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private MyBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        InDoorService getService() {
            return InDoorService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public <T> AutoDisposeConverter<T> bindAutoDispose() {
        return null;
    }


    public static boolean isFirst = true;
    static Disposable disposableTimer, paceTimer;
    public static ArrayThree arrayThreePhoneVelocityTool = null;

    public static boolean isFirstPace;


    /* public void startPaceTimer() {
         if (paceTimer != null && !paceTimer.isDisposed()) {
             paceTimer.isDisposed();
         }
         paceTimer = Observable.interval(20, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
             @Override
             public void accept(Long aLong) throws Exception {
                 if (aLong != 0) {
                     InDoorService.theMomentRunData.setInstantVelocity(0);
                     arrayThreePhoneVelocityTool.clearValue(System.currentTimeMillis(), InDoorService.theMomentRunData.totalStep);
                 }
             }
         });
     }*/
    static double sencondDis = 0;

    public static void startTimer() {

        //20秒的总距离

        if (disposableTimer != null && !disposableTimer.isDisposed()) {
            // disposableTimer.dispose();
            return;
        }
        disposableTimer = Observable.interval(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {

            @Override
            public void accept(Long aLong) throws Exception {
                //需要单线程去处理这些逻辑
                ThreadSinglePoolUtils.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        if (null != iSportStepInterface) {
                            int step = 0;

                            try {
                                step = iSportStepInterface.getCurrentTimeSportStep();
                                Logger.e("step--------------" + step);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } finally {
                                if (inRunIsPause) {
                                    InDoorService.arrayThreePhoneVelocityTool.cleanAtRunFinish();
                                    argsForInRunService.phonePauseTimeTotal++;
                                    argsForInRunService.phonePauseStep = step - argsForInRunService.phonePauseStep - argsForInRunService.phoneStepValue + argsForInRunService.phonePauseStep;

                                } else {

                                   /* if (sportType == JkConfiguration.SportType.sportIndoor) {
                                        if (Math.abs((aLong - argsForInRunService.phonePauseTimeTotal)) % 20 == 0) {
                                            if (sencondDis == 0) {
                                                theMomentRunData.setInstantVelocity(0);
                                                theMomentRunData.strinstantVelocity = JkConfiguration.strPace;
                                                sencondDis = 0;
                                                //更新显示配速值
                                            }
                                        }
                                    }*/

                                    /**
                                     * 获取从运动开始的步数
                                     */
                                    if (isFirst) {
                                        isFirst = false;
                                        argsForInRunService.phonePauseStep = step;
                                        // InDoorService.arrayThreePhoneVelocityTool.clearValue(System.currentTimeMillis(), 0);
                                    } else {
                                        if (step - argsForInRunService.phonePauseStep >= 0) {
                                            argsForInRunService.phoneStepValue = step - argsForInRunService.phonePauseStep;
                                        } else {
                                            argsForInRunService.phonePauseStep = step;
                                        }
                                    }

                                    long sportTimer = System.currentTimeMillis() - argsForInRunService.phonePauseTimeTotal * 1000 - argsForInRunService.phoneStartTime;
                                    theMomentRunData.setTime(TimeUtil.getTimerFormatedStrings(sportTimer, 0))
                                            .setTotalStep(argsForInRunService.phoneStepValue).setTimer(sportTimer / 1000);//保存的是秒数

                                    /**
                                     * 室内跑的配速计算
                                     */
                                    //如果距离为0就不记录他的配速
                                    // if (dis != 0) {
                                    //总共步数的cal
                                    //如果是室内运动就需要保存步数算出来的配速值
                                    if (sportType == JkConfiguration.SportType.sportIndoor) {
                                        double dis = StepsUtils.countDistanceUseStep(argsForInRunService.phoneStepValue, userInfo.getGender(), Float.parseFloat(userInfo.getHeight()));
                                        //dis是米
                                        double calorie = StepsUtils.calCalorie(Float.parseFloat(userInfo.getWeight()), dis, sportType);
                                        //距离以千米为单位
                                        InDoorService.theMomentRunData.distance = (dis / 1000);
                                        InDoorService.theMomentRunData.setCalories(calorie);
                                        //这里传入的配速是米
                                        double inVlocity = StepsUtils.getPhoneRunVelocity(dis, System.currentTimeMillis());
                                        if (!Double.isInfinite(inVlocity)) {
                                            if (inVlocity > 0) {
                                                theMomentRunData.setInstantVelocity((float) inVlocity);
                                                PaceBean bean = StepsUtils.calPaceBean(theMomentRunData.instantVelocity, InDoorService.theMomentRunData.timer);
                                                if (bean != null) {
                                                    //第一个配速给过滤掉
                                                    theMomentRunData.paceBean.put(theMomentRunData.timer, bean);
                                                    theMomentRunData.strinstantVelocity = bean.getPace();
                                                }
                                            }
                                        } else {
                                            theMomentRunData.setInstantVelocity(0);
                                            theMomentRunData.strinstantVelocity = JkConfiguration.strPace;
                                            sencondDis = 0;
                                        }

                                    }
                                    // }

                                    IndoorRunObservable.getInstance().setChanged();
                                    IndoorRunObservable.getInstance().notifyObservers(theMomentRunData);
                                }
                            }
                        }
                    }
                });


            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        com.isport.blelibrary.utils.Logger.myLog("onDestroy");

        //unbindService();

        stopService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        }
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        if (inRunIsPause && disposableTimer != null && !disposableTimer.isDisposed()) {
            Logger.e("disposableTimer end");
            disposableTimer.dispose();
        }


        //TODO 如果状态没有不是结束需要把数据保存。

    }

    /* private void updateStepCount() {
        Log.e(TAG, "updateStepCount : " + mFirstStepSum);

        *//*  titleBarView.setTitle(sumStepSum + "");*//*


        tempDistance = mActPresenter.countDistanceUseStep(sumStepSum);


        distance = tempDistance / 1000; // 将距离m换算成km。
        tvDis.setText(CommonDateUtil.formatTwoPoint(distance));
        // 体重、距离
        // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
        double calorie = weight * distance * 1.036;
        itemViewCal.setValueText(CommonDateUtil.formatInterger(calorie));


        //tvDis.setText(mFirstStepSum + "步" + "--" + sumStepSum + "步");

    }*/

}
