package com.isport.brandapp.device.f18.dial;


import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.isport.brandapp.R;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.commonview.TitleBarView;

/**
 * Created by Admin
 * Date 2022/2/9
 */
public class F18DialActivity extends BaseActivity implements View.OnClickListener {

    private TitleBarView titleLayout;
    private TextView dialCenterTv,dialCusTv;
    private View dialCenterView,dialCusView;


    private FragmentManager fragmentManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_dial_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();

        titleLayout.setTitle("表盘设置");
        titleLayout.setLeftIcon(R.drawable.icon_back);
        titleLayout.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
    }

    @Override
    protected void initData() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.f18DialHomeLayout,F18DialCenterFragment.getInstance()).commitAllowingStateLoss();
        dialCenterView.setVisibility(View.VISIBLE);
        dialCusView.setVisibility(View.INVISIBLE);

        dialCenterTv.setTextColor(Color.parseColor("#2F2F33"));
        dialCusTv.setTextColor(Color.parseColor("#6E6E77"));
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }


    private void findViews(){
        titleLayout = findViewById(R.id.f18DialTitleBarLayout);
        dialCenterTv = findViewById(R.id.dialCenterTv);
        dialCusTv = findViewById(R.id.dialCusTv);
        dialCenterView = findViewById(R.id.dialCenterView);
        dialCusView = findViewById(R.id.dialCusView);

        dialCenterTv.setOnClickListener(this);
        dialCusTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.dialCenterTv){
            if(fragmentManager == null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.f18DialHomeLayout,F18DialCenterFragment.getInstance()).commitAllowingStateLoss();

            dialCenterView.setVisibility(View.VISIBLE);
            dialCusView.setVisibility(View.INVISIBLE);

            dialCenterTv.setTextColor(Color.parseColor("#2F2F33"));
            dialCusTv.setTextColor(Color.parseColor("#6E6E77"));

        }
        if(v.getId() == R.id.dialCusTv){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.f18DialHomeLayout,CustomizeF18DialFragment.getInstance()).commitAllowingStateLoss();
            dialCenterView.setVisibility(View.INVISIBLE);
            dialCusView.setVisibility(View.VISIBLE);

            dialCenterTv.setTextColor(Color.parseColor("#6E6E77"));
            dialCusTv.setTextColor(Color.parseColor("#2F2F33"));
        }
    }
}
