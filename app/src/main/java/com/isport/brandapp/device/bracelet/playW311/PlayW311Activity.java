package com.isport.brandapp.device.bracelet.playW311;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.device.bracelet.FragmentList;
import com.isport.brandapp.device.bracelet.playW311.PlayW311Presenter.PlayerPresenter;
import com.isport.brandapp.device.bracelet.playW311.bean.PlayBean;
import com.isport.brandapp.device.bracelet.playW311.view.PlayerView;
import com.isport.brandapp.login.ActivityLogin;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import bike.gymproject.viewlibray.ItemInditionView;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.ProgressWheel;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import brandapp.isport.com.basicres.service.observe.TodayObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class PlayW311Activity extends BaseTitleActivity implements View.OnClickListener, PlayerView {
    private static final String BUNDLE_FRAGMENTS_KEY = "android:support:fragments";
    int currentType;
    private DeviceBean deviceBean;

    PlayerPresenter presenter;
    TextView tv_start_experience;
    LinearLayout layout_ind, layout_empty_view;
    FrameLayout frameLayout;

    TextView tips_loading_msg;
    ProgressWheel progress;

    ArrayList<ItemInditionView> lists;

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_playw311;
    }

    @Override
    protected void initView(View view) {


        progress = view.findViewById(R.id.progress);
        tv_start_experience = view.findViewById(R.id.tv_start_experience);
        layout_ind = view.findViewById(R.id.layout_ind);
        layout_empty_view = view.findViewById(R.id.layout_empty_view);
        frameLayout = view.findViewById(R.id.content);
        lists = new ArrayList<>();
        currentType = getIntent().getIntExtra(JkConfiguration.DEVICE, JkConfiguration.DeviceType.BRAND_W311);
    }

    @Override
    protected void initData() {

        presenter = new PlayerPresenter(this);
      //  presenter.getPlayBanImage(currentType);
        if(currentType == 70180){  //F18的音频模式指引
            presenter.getF18AudioGuidData(70180);
        }else{
            presenter.getPlayBanImage(currentType);
        }


        TodayObservable.getInstance().addObserver(this);
        clearAllIv(0);

        tv_start_experience.setVisibility(View.VISIBLE);
        layout_ind.setVisibility(View.INVISIBLE);
        layout_empty_view.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        // TODO: 2018/11/5 查询历史
    }


    @Override
    protected void initEvent() {
        titleBarView.setTitle(currentType == 70180 ? getResources().getString(R.string.string_audio_guid) : String.format(UIUtils.getString(R.string.bracelet_play)));

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
        tv_start_experience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void clearAllIv(int currentIndx) {
        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).setImageviewValue(R.drawable.shape_gray_bg);
        }

        if (currentIndx >= 0 && currentIndx < lists.size()) {
            lists.get(currentIndx).setImageviewValue(R.drawable.shape_oval_bg);
            if (lists.size() - 1 == currentIndx) {
                tv_start_experience.setVisibility(View.VISIBLE);
                layout_ind.setVisibility(View.INVISIBLE);
            } else {
                tv_start_experience.setVisibility(View.INVISIBLE);
                layout_ind.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initHeader() {
        //StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && this.clearFragmentsTag()) {
            //重建时清除 fragment的状态
            savedInstanceState.remove(BUNDLE_FRAGMENTS_KEY);
        }
        super.onCreate(savedInstanceState);
        //rgTab.check(R.id.rb_day);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (outState != null && this.clearFragmentsTag()) {
            //销毁时不保存fragment的状态
            outState.remove(BUNDLE_FRAGMENTS_KEY);
        }

    }

    protected boolean clearFragmentsTag() {
        return true;
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
                Intent intent = new Intent(context, ActivityLogin.class);
                context.startActivity(intent);
                ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);
        if (o instanceof TodayObservable) {
            int type = (int) arg;
            clearAllIv(type);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TodayObservable.getInstance().deleteObserver(this);
    }

    @Override
    public void successPlayerSuccess() {
        if (isFinishing()) {
            return;
        }
        try {

            List<PlayBean> playBeans = UserAcacheUtil.getPlayBandImagelist(currentType);
            layout_ind.removeAllViews();
            for (int i = 0; i < playBeans.size(); i++) {
                ItemInditionView inditionView = new ItemInditionView(PlayW311Activity.this);
                lists.add(inditionView);
                inditionView.setImageviewValue(R.drawable.shape_oval_bg);
                layout_ind.addView(inditionView);
            }

            layout_empty_view.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            PlayW311FragmentList fragmentList = new PlayW311FragmentList();
            Bundle bundle = new Bundle();
            bundle.putInt("type", FragmentList.TYPE_DAY);
            bundle.putInt(JkConfiguration.DEVICE, currentType);
            fragmentList.setArguments(bundle);
            transaction.replace(R.id.content, fragmentList);
            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRespondError(String message) {
        try {
            progress.setVisibility(View.GONE);
            tips_loading_msg.setText(UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
