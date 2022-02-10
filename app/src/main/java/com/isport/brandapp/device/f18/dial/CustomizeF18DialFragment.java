package com.isport.brandapp.device.f18.dial;

import android.view.View;

import com.isport.brandapp.R;

import brandapp.isport.com.basicres.BaseFragment;

/**
 * 自定义表盘
 * Created by Admin
 * Date 2022/2/9
 */
public class CustomizeF18DialFragment extends BaseFragment {


    public static CustomizeF18DialFragment getInstance(){
        return new CustomizeF18DialFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cus_f18_dial_layout;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void initImmersionBar() {

    }
}
