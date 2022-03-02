package com.isport.brandapp.device.f18;

import android.view.View;

import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.brandapp.R;

import brandapp.isport.com.basicres.mvp.BaseMVPActivity;

/**
 * Created by Admin
 * Date 2022/2/14
 */
public class F18AudioGuideActivity extends BaseMVPActivity<F18SetView,F18SetPresent> implements F18SetView {


    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {

    }

    @Override
    public void backSelectDateStr(int selectType, int type, String timeStr) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_audio_guid_layout;
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
    protected void initHeader() {

    }

    @Override
    protected F18SetPresent createPresenter() {
        return null;
    }
}
