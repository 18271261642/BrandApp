package com.isport.brandapp.home.view.circlebar;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.util.AppSP;

import java.text.DecimalFormat;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;

public class CirclebarAnimatorLayout extends RelativeLayout {

    private Context mContext;
    CirclebarAnimatorView view;
    private TextView tvStepTarget;
    private TextView tvSportstep;

    private float currentStep;
    private int currentTargetStep;

    int currentType;
    int progreesVaule;
    float goalValue;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final DecimalFormat dft = new DecimalFormat("#");

    public CirclebarAnimatorLayout(Context context) {
        this(context, null);
    }

    public CirclebarAnimatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclebarAnimatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBase(context, attrs, defStyleAttr);
    }


    public void updateViewValue(DeviceBean deviceBean) {
        switch (deviceBean.currentType) {

        }
    }


    private void initBase(Context context, AttributeSet attrs, int defStyleAttr) {

        initView();
        setListener();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.app_view_circlebar, this, true);
        view = findViewById(R.id.objectAnimatorView);
        tvSportstep = findViewById(R.id.tv_sport_steps);
        tvStepTarget = findViewById(R.id.tv_step_tagert);


    }

    private void setListener() {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                if (lister != null) {
                    lister.onViewClickLister(currentType);
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
        //startAni();
    }


    private void initData() {
    }


    public void setSportTarget(int target) {
        int goalType = AppSP.getInt(getContext(),AppSP.DEVICE_GOAL_KEY,0);
        currentTargetStep = target;
        if (tvStepTarget != null) {
            if(goalType == 0){  //步数目标
                Drawable drawable = getResources().getDrawable(R.drawable.icon_main_step_tage);
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                tvStepTarget.setCompoundDrawables(null,null,null,drawable);
                tvStepTarget.setText(String.format(UIUtils.getString(R.string.fragment_data_target), target + ""));
                updateProgress(currentStep, currentTargetStep);
            } else if(goalType == 1){   //距离目标
                Drawable drawable = getResources().getDrawable(R.drawable.icon_main_distance_tage);
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                tvStepTarget.setCompoundDrawables(null,null,null,drawable);

                tvStepTarget.setText(String.format(UIUtils.getString(R.string.fragment_data_target_distance), (target/1000+ "")));
                updateProgress(currentStep, target);
            }else if(goalType == 2){    //卡路里目标
                Drawable drawable = getResources().getDrawable(R.drawable.icon_main_kcal_tage);
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                tvStepTarget.setCompoundDrawables(null,null,null,drawable);
                tvStepTarget.setText(String.format(UIUtils.getString(R.string.fragment_data_target_calorie), (target + "")));
                updateProgress(currentStep, target);
            }

        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setTxtDrawable(){
        // 使用代码设置drawableleft
         Drawable drawable = getResources().getDrawable(R.drawable.icon_main_step_tage);
        // 这一步必须要做，否则不会显示。
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());

    }




    public void setSportStep(float step) {
        currentStep = step;
        int goalType = AppSP.getInt(BaseApp.getApp(),AppSP.DEVICE_GOAL_KEY,0);
        if (tvSportstep != null) {
            if (step == -1) {
                tvSportstep.setText(UIUtils.getString(R.string.no_data));
            } else {
                tvSportstep.setText(goalType != 1 ? dft.format(step) : step + "");
            }
        }
    }


    public void startAnimotion() {
        /**
         * 不同的类型 开始值和目标值的设置
         * 单位的设置
         *
         */

        if (view != null) {
            view.setCurrentType(currentType);
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "progress", view.getProgress(), ParsePreceter
                    .parseProgress
                            (progreesVaule, goalValue, currentType));
//            Logger.myLog("progreesVaule  == " + progreesVaule + "goalValue == " + goalValue);
            animator.setDuration(1000);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
        }
    }

    /**
     * @param step   用户的当前步数
     * @param target 用户设置的目标步数
     */
    public void updateProgress(float step, int target) {

        if (target == 0) {
            target = 6000;
        }
        if (view != null) {
            float precent = 0;

            int goalType = AppSP.getInt(getContext(),AppSP.DEVICE_GOAL_KEY,0);

            precent =  (1.0f * step /(goalType == 1 ? target / 1000 : target) * 100 );
            if (precent >= 100) {
                precent = 100;
            }
            view.setProgress(precent);
            view.invalidate();
        } else {
            Logger.myLog("view ==null ");
        }
    }


    ViewClickLister lister;

    public void setViewClickLister(ViewClickLister lister) {
        this.lister = lister;
    }

    public interface ViewClickLister {
        void onViewClickLister(int type);
    }


}
