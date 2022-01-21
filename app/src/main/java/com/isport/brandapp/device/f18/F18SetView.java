package com.isport.brandapp.device.f18;

import com.isport.blelibrary.db.table.f18.F18DeviceSetData;

import brandapp.isport.com.basicres.mvp.BaseView;

/**
 * Created by Admin
 * Date 2022/1/17
 */
public interface F18SetView extends BaseView {

    void backAllSetData(F18DeviceSetData f18DeviceSetData);

    void backSelectDateStr(int selectType,int type,String timeStr);
}
