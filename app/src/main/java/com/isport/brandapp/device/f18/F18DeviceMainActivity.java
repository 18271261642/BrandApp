package com.isport.brandapp.device.f18;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import com.isport.blelibrary.utils.Constants;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.commonutil.Logger;

/**
 * F18主页面activity
 * Created by Admin
 * Date 2022/1/19
 */
public class F18DeviceMainActivity extends BaseActivity  {

    private static final String DATA_FRAGMENT_KEY = "fragmentData";

    F18HomeFragment f18HomeFragment;
    FragmentManager mFragmentManager;
    private final List<Fragment> fragmentList = new ArrayList<>();


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        if (f18HomeFragment != null) {
            getSupportFragmentManager().putFragment(outState, DATA_FRAGMENT_KEY, f18HomeFragment);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.isport.blelibrary.utils.Logger.myLog("onNewIntent onCreate" + savedInstanceState);
        if (mFragmentManager == null) {
            mFragmentManager = this.getSupportFragmentManager();
        }
        if (savedInstanceState != null) {
            f18HomeFragment = (F18HomeFragment) mFragmentManager.findFragmentByTag
                    (DATA_FRAGMENT_KEY);

            addToList(f18HomeFragment);
        } else {
            initFragment();
        }

    }

    private void initFragment() {
        /* 默认显示home  fragment*/

        f18HomeFragment = F18HomeFragment.getInstance();
        addFragment(f18HomeFragment);
        showFragment(f18HomeFragment);


    }


    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_device_main;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfiguration.isScaleScan = false;
        AppConfiguration.isScaleRealTime = false;
        AppConfiguration.isScaleConnectting = false;
        Constants.isDFU = false;//不是升级模式
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void addToList(Fragment fragment) {
        if (fragment != null) {
            fragmentList.add(fragment);
        }

        Logger.e("fragmentList数量" + fragmentList.size());
    }

    /*添加fragment*/
    private void addFragment(Fragment fragment) {

        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded()) {
            mFragmentManager.beginTransaction().add(R.id.frament, fragment).commitAllowingStateLoss();
            /*添加到 fragmentList*/
            fragmentList.add(fragment);
        }
    }

    /*显示fragment*/
    private void showFragment(Fragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                mFragmentManager.beginTransaction().hide(frag).commitAllowingStateLoss();
            }
        }
        mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();

    }
}
