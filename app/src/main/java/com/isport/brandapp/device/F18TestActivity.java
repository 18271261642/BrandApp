package com.isport.brandapp.device;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.f18.F18DeviceSetAction;
import com.isport.blelibrary.db.table.f18.F18DetailStepBean;
import com.isport.blelibrary.db.table.w811w814.W81DeviceDetailData;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.F18HomePresenter;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.List;

import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.commonutil.TokenUtil;

/**
 * Created by Admin
 * Date 2022/3/24
 */
public class F18TestActivity extends BaseActivity {

    private static final String TAG = "F18TestActivity";

    private TextView showStTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_test_layout;
    }

    @Override
    protected void initView(View view) {
        showStTv = findViewById(R.id.showStTv);
        findViewById(R.id.f18TestTodayBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // W81DeviceDetailData w81DeviceDetailData = new W81DeviceDataAction().getW81DeviceDetialData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(F18TestActivity.this), DateUtil.getCurrDay());

               List<F18DetailStepBean> allList =  F18DeviceSetAction.getF18DetailList(TokenUtil.getInstance().getPeopleIdStr(F18TestActivity.this),AppConfiguration.braceletID);

                Log.e(TAG,"------所有数据="+new Gson().toJson(allList));
                StringBuilder stringBuilder = new StringBuilder();
                for(F18DetailStepBean fb : allList){
                    stringBuilder.append(fb.getDayStr()+"  "+DateUtil.getFormatTime(fb.getTimeLong())+" "+fb.toString()+"\n\n");
                }

                showStTv.setText(stringBuilder.toString());
            }
        });


        findViewById(R.id.testDealDataBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
}
