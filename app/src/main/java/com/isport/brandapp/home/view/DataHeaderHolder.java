package com.isport.brandapp.home.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.holder.CustomHolder;
import com.isport.brandapp.home.bean.db.WatchSportMainData;
import com.isport.brandapp.home.customview.MainHeadDataItemView;
import com.isport.brandapp.home.view.circlebar.CirclebarAnimatorLayout;
import com.isport.brandapp.util.AppSP;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * 步数汇总图表
 */
public class DataHeaderHolder extends CustomHolder<String> {

    private static final String TAG = "DataHeaderHolder";


    TextView tvTime;
    CirclebarAnimatorLayout heardLayout;
    RelativeLayout rlLayout;
    MainHeadDataItemView itemDis;
    MainHeadDataItemView itemCal;
    ImageView iv_change;
    // private ImageView ivChange;



    public DataHeaderHolder(View itemView) {
        super(itemView);
    }

    public DataHeaderHolder(List<String> datas, View itemView) {
        super(datas, itemView);
    }

    public DataHeaderHolder(Context context, final List<String> lists, int itemID) {
        super(context, lists, itemID);
        tvTime = itemView.findViewById(R.id.tv_update_time);
        heardLayout = itemView.findViewById(R.id.layout_data_head);
        iv_change = itemView.findViewById(R.id.iv_change);
        rlLayout = itemView.findViewById(R.id.rl_layout);
        itemDis = itemView.findViewById(R.id.item_dis);
        itemCal = itemView.findViewById(R.id.item_cal);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ViewMultiClickUtil.onMultiClick(view)) {
                    return;
                }
                if (lister != null) {
                    lister.onHeadOnclick();
                }
            }
        });
        iv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lister != null) {
                    lister.onChangeClikelister();
                }
            }
        });
        heardLayout.setViewClickLister(new CirclebarAnimatorLayout.ViewClickLister() {
            @Override
            public void onViewClickLister(int type) {
                if (lister != null) {
                    lister.onHeadOnclick();
                }
            }
        });


//        //添加长按
//        itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                context.startActivity(new Intent(context, Test2Activity.class));
//                return true;
//            }
//        });

    }


    public void defUpdateUI() {
        updateUI(UIUtils.getString(R.string.no_data), UIUtils.getString(R.string.no_data), -1, "");
    }


    public void setTarget(int target) {
        heardLayout.setSportTarget(target);
    }



    /**
     *
     * @param cal 当前卡路里
     * @param dis   当前距离
     * @param step  当前步数
     * @param time 当前时间
     */
    public void updateUI(String cal, String dis, int step, String time) {
        try {
            int goalType = AppSP.getInt(BaseApp.getApp(),AppSP.DEVICE_GOAL_KEY,0);
            if(goalType == 0){  //步数目标
                setTarget(JkConfiguration.WATCH_GOAL);

                heardLayout.setSportStep(step);
                //距离 title
                itemDis.setTitleText(context.getResources().getString(R.string.watch_step_type_distance));
                //距离value
                itemDis.setValue(dis);
                //距离unit
                itemDis.setUnitText(context.getResources().getString(R.string.dis_unit));

                //卡路里title
                itemCal.setTitleText(context.getResources().getString(R.string.consume));
                //value
                itemCal.setValue(cal);
                //unit
                itemCal.setUnitText(context.getResources().getString(R.string.watch_step_unite_calory));

            }else if(goalType == 1){    //距离目标
                setTarget(JkConfiguration.WATCH_GOAL_DISTANCE);

                heardLayout.setSportStep(dis == null ? -1 : Float.valueOf(dis));
                //距离 title
                itemDis.setTitleText(context.getResources().getString(R.string.steps));
                //距离value
                itemDis.setValue(step+"");
                //距离unit
                itemDis.setUnitText(context.getResources().getString(R.string.step_unit));

                //卡路里title
                itemCal.setTitleText(context.getResources().getString(R.string.consume));
                //value
                itemCal.setValue(cal);
                //unit
                itemCal.setUnitText(context.getResources().getString(R.string.watch_step_unite_calory));

            }else if(goalType == 2){    //卡路里目标
                setTarget(JkConfiguration.WATCH_GOAL_CALORIE);

                heardLayout.setSportStep(cal == null ? -1 : Float.valueOf(cal));
                //距离 title
                itemDis.setTitleText(context.getResources().getString(R.string.steps));
                //距离value
                itemDis.setValue(step+"");
                //距离unit
                itemDis.setUnitText(context.getResources().getString(R.string.step_unit));

                //卡路里title
                itemCal.setTitleText(context.getResources().getString(R.string.dis_unit));
                //value
                itemCal.setValue(dis);
                //unit
                itemCal.setUnitText(context.getResources().getString(R.string.dis_unit));
            }



            if (TextUtils.isEmpty(time)) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.myLog(e.toString());
        }
    }


    public void updateUI(WatchSportMainData watchSportMainData) {
        Logger.myLog(TAG,"----updateUI="+watchSportMainData.toString());
        if (watchSportMainData == null) {
            defUpdateUI();
            return;
        }
        String strStep = watchSportMainData.getStep();
        int step = TextUtils.isEmpty(strStep) || strStep.equals(UIUtils.getString(R.string.no_data)) ? -1 : Integer.parseInt(strStep);
        updateUI(watchSportMainData.getCal(), watchSportMainData.getDistance(), step, watchSportMainData.getDateStr());
    }


    OnHeadOnclickLister lister;

    public void setOnCourseOnclickLister(OnHeadOnclickLister lister) {
        this.lister = lister;
    }

    public interface OnHeadOnclickLister {
        void onHeadOnclick();

        void onChangeClikelister();

        void onHeadOnclickIvChange();
    }

    public void showChangeImage(boolean isShowImage) {

        if (iv_change != null) {
            iv_change.setVisibility(View.GONE);
            // iv_change.setVisibility(isShowImage ? View.VISIBLE : View.GONE);
        }
    }

}
