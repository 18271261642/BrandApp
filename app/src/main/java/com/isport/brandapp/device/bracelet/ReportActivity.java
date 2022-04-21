package com.isport.brandapp.device.bracelet;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.dialog.CommuniteDeviceSportDetailGuideDialog;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.ClickUtils;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Observable;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import brandapp.isport.com.basicres.service.observe.TodayObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class ReportActivity extends BaseActivity implements View.OnClickListener {

    private DeviceBean deviceBean;
    private TextView tvTitle;
    private ImageView ivBack, ivShare, ivCaleder;
    private RadioGroup tab;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rope_report;
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_sport_type);
        ivBack = view.findViewById(R.id.iv_back);
        ivShare = view.findViewById(R.id.iv_share);
        ivCaleder = view.findViewById(R.id.iv_calender);
        tab = view.findViewById(R.id.tab);
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            FragmentList fragmentList = new FragmentList();
            Bundle bundle = new Bundle();
            bundle.putInt("type", FragmentList.TYPE_DAY);
            deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);
            bundle.putSerializable(JkConfiguration.DEVICE, deviceBean);
            fragmentList.setArguments(bundle);
            transaction.replace(R.id.content, fragmentList);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initData() {
        TodayObservable.getInstance().addObserver(this);
        boolean isFirst = TokenUtil.getInstance().getKeyValue(this, TokenUtil.DEVICE_DETAIL_FIRST);
        if (!isFirst) {
            CommuniteDeviceSportDetailGuideDialog dialog = new CommuniteDeviceSportDetailGuideDialog(this, TokenUtil.DEVICE_DETAIL_FIRST, R.style.AnimDeviceDtail);
            dialog.showDialog();
        }

        String title = "";

        if (AppUtil.isZh(BaseApp.getApp())) {
            title = UIUtils.getString(R.string.watch) + UIUtils.getString(R.string.steps);
        } else {
            title = UIUtils.getString(R.string.watch) + " " + UIUtils.getString(R.string.steps);
        }
        tvTitle.setText(title);

        // TODO: 2018/11/5 查询历史
    }


    @Override
    protected void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        tab.check(R.id.rb_day);
        tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_day:
                        TodayObservable.getInstance().cheackType(com.isport.brandapp.ropeskipping.history.FragmentList.TYPE_DAY);
                        break;
                    case R.id.rb_week:
                        TodayObservable.getInstance().cheackType(com.isport.brandapp.ropeskipping.history.FragmentList.TYPE_WEEK);
                        break;
                    case R.id.rb_month:
                        TodayObservable.getInstance().cheackType(com.isport.brandapp.ropeskipping.history.FragmentList.TYPE_MONTH);
                        break;
                }
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.share));
               /* Intent intent = new Intent(context, ShareActivity.class);
                ShareBean shareBean = new ShareBean();
                shareBean.isWeek = AppConfiguration.shareBean.isWeek;
                shareBean.centerValue = AppConfiguration.shareBean.centerValue;
                shareBean.one = AppConfiguration.shareBean.one;
                shareBean.three = AppConfiguration.shareBean.three;
                shareBean.two = AppConfiguration.shareBean.two;
                shareBean.time = AppConfiguration.shareBean.time;
                intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.WATCH_W516);
                intent.putExtra(JkConfiguration.FROM_BEAN, shareBean);
                startActivity(intent);*/
                //发一个消息出去
            }
        });

        ivCaleder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtils.isFastDoubleClick())
                    return;
                EventBus.getDefault().post(new MessageEvent(MessageEvent.calender));
            }
        });
    }

    @Override
    protected void initHeader() {
        // StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
        // StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
    }

    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);

        if (o instanceof TodayObservable) {

            try {
                Integer type = (Integer) arg;
                if (type == FragmentList.TYPE_DAY) {
                    ivCaleder.setVisibility(View.VISIBLE);
                } else {
                    ivCaleder.setVisibility(View.GONE);
                }
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                FragmentList fragmentList = new FragmentList();
                Bundle bundle = new Bundle();

                bundle.putInt("type", type);
                bundle.putSerializable(JkConfiguration.DEVICE, deviceBean);

                fragmentList.setArguments(bundle);
                transaction.replace(R.id.content, fragmentList);
                transaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //rgTab.check(R.id.rb_day);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.NEED_LOGIN:
                ToastUtils.showToast(context, UIUtils.getString(com.isport.brandapp.basicres.R.string.login_again));
                NetProgressObservable.getInstance().hide();
                BleProgressObservable.getInstance().hide();
                TokenUtil.getInstance().clear(context);
                UserAcacheUtil.clearAll();
                AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
                App.initAppState();
               /* Intent intent = new Intent(context, ActivityLogin.class);
                context.startActivity(intent);
                ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());*/
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TodayObservable.getInstance().deleteObserver(this);
    }
}
