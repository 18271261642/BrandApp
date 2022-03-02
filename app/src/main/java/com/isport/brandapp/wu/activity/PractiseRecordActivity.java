package com.isport.brandapp.wu.activity;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.dialog.BaseDialog;
import com.isport.brandapp.device.f18.view.F18PractiseTypeView;
import com.isport.brandapp.device.f18.view.OnF18PractiseDialogClickListener;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.wu.Constant;
import com.isport.brandapp.wu.bean.PractiseRecordInfo;
import com.isport.brandapp.wu.bean.PractiseTimesInfo;
import com.isport.brandapp.wu.mvp.PractiseRecordView;
import com.isport.brandapp.wu.mvp.presenter.PractiseRecordPresenter;
import com.jkcq.homebike.history.adpter.PractiseAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.mvp.BaseMVPActivity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * 手表锻炼记录
 */
public class PractiseRecordActivity extends BaseMVPActivity<PractiseRecordView, PractiseRecordPresenter> implements PractiseRecordView, View.OnClickListener {

    private static final String TAG = "PractiseRecordActivity";

    private LinearLayout ll_nodata;
    private RecyclerView recyclerview_practise;
    private PractiseAdapter mAdapter;
    private RelativeLayout rl_title;

    private TextView tv_sport_time;
    private TextView tv_sport_times;

    private ImageView iv_back;
    private TextView tv_title;
    private ImageView iv_arrow;
    private SmartRefreshLayout refresh_layout;
    private int mSelectedType = Constant.PRACTISE_TYPE_ALL;

    private int mCurrentPage = 0;
    private List<PractiseRecordInfo> mDatalist = new ArrayList<>();

    private int currentType;

    private F18PractiseTypeView f18PractiseTypeView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_practise_record;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(R.id.rl_title).init();
    }



    //560B运动类型 6：篮球 ；5 羽毛球

    @Override
    protected void initView(View view) {

        ll_nodata = findViewById(R.id.ll_nodata);
        tv_sport_time = findViewById(R.id.tv_sport_time);
        tv_sport_times = findViewById(R.id.tv_sport_times);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        iv_arrow = findViewById(R.id.iv_arrow);
        rl_title = findViewById(R.id.rl_title);
        refresh_layout = findViewById(R.id.refresh_layout);
        iv_back.setOnClickListener(this);
        iv_arrow.setOnClickListener(this);
        recyclerview_practise = findViewById(R.id.recyclerview_practise);
        recyclerview_practise.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new PractiseAdapter(mDatalist);
        recyclerview_practise.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                Logger.myLog("onItemChildClick=" + position);
                if (mDatalist.get(position).isSelect()) {
                    for (int i = 0; i < mDatalist.size(); i++) {
                        mDatalist.get(i).setSelect(false);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < mDatalist.size(); i++) {
                        mDatalist.get(i).setSelect(false);
                    }
                    mDatalist.get(position).setSelect(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        refresh_layout.setEnableRefresh(false);
        refresh_layout.setEnableFooterFollowWhenNoMoreData(true);
        refresh_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mActPresenter.getPractiseRecordData(currentType, mSelectedType, mCurrentPage++);
            }
        });
    }

    @Override
    protected void initData() {
        tv_title.setText(R.string.practise_record);
//        setNodata();
        if (DeviceTypeUtil.isContainW55X()) {
//            if(AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)){
//                currentType = JkConfiguration.DeviceType.Watch_W560;
//            }else{
//                currentType = JkConfiguration.DeviceType.Watch_W556;
//            }
            currentType = JkConfiguration.DeviceType.Watch_W556;

        } else {
            currentType = JkConfiguration.DeviceType.Watch_W812;
        }

       // currentType = JkConfiguration.DeviceType.Watch_W560;

        Log.e(TAG,"----currentType="+currentType);

        mActPresenter.getPractiseRecordData(currentType, Constant.PRACTISE_TYPE_ALL, mCurrentPage);
        mActPresenter.getTotalPractiseTimes(currentType, Constant.PRACTISE_TYPE_ALL);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    private void getData(int type) {
        isShowPopup = !isShowPopup;
        if (mMenuViewBirth != null && mMenuViewBirth.isShowing()) {
            mMenuViewBirth.dismiss();
        }

        if(mMenuViewBirthW5xx != null && mMenuViewBirthW5xx.isShowing())
            mMenuViewBirthW5xx.dismiss();
        mSelectedType = type;
        mCurrentPage = 0;
        refresh_layout.setEnableLoadMore(true);
        mActPresenter.getPractiseRecordData(currentType, mSelectedType, mCurrentPage);
        mActPresenter.getTotalPractiseTimes(currentType, mSelectedType);
    }

    /**
     * 暂无数据
     */
    private void setNodata() {
        recyclerview_practise.setVisibility(View.GONE);
        ll_nodata.setVisibility(View.VISIBLE);
        tv_sport_time.setText("0");
        tv_sport_times.setText(getString(R.string.total_practise_times, "0"));
    }

    @Override
    protected PractiseRecordPresenter createPresenter() {
        return new PractiseRecordPresenter();
    }

    @Override
    public void getPractiseRecordSuccess(List<PractiseRecordInfo> infos) {

        Logger.myLog(TAG,"------getPractiseRecordSuccess="+new Gson().toJson(infos));

        if (mCurrentPage > 0) {
            if (infos != null && infos.size() > 0) {
                mDatalist.addAll(infos);
                mAdapter.replaceData(mDatalist);
                if (infos.size() < 10) {
                    refresh_layout.finishLoadMoreWithNoMoreData();
                } else {
                    refresh_layout.finishLoadMore();
                }
            } else {
                refresh_layout.finishLoadMoreWithNoMoreData();
            }
        } else {


            if (infos != null && infos.size() > 0) {
                infos.get(0).setSelect(true);
                Log.e(TAG, "size=" + infos.size());
                mDatalist.clear();
                recyclerview_practise.setVisibility(View.VISIBLE);
                ll_nodata.setVisibility(View.GONE);
                recyclerview_practise.scrollToPosition(0);
                mAdapter.replaceData(infos);
//                mAdapter = new PractiseAdapter(this, infos);
//                recyclerview_practise.setAdapter(mAdapter);
                if (infos.size() < 10) {
                    refresh_layout.setEnableLoadMore(false);
//                    refresh_layout.finishLoadMoreWithNoMoreData();
                }
            } else {
                setNodata();
            }
        }
    }

    @Override
    public void getTotalPractiseTimesSuccess(PractiseTimesInfo data) {
        if (data != null) {
            tv_sport_time.setText("" + data.getTotalTime());
            tv_sport_times.setText(getString(R.string.total_practise_times, "" + data.getTotalNum()));
        }
    }


    //560B运动类型 健走 跑步 骑行 登山 足球 篮球 乒乓球 羽毛球

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_arrow:

                if(DeviceTypeUtil.isContainF18()){
                    showF18PractiseType();
                    return;
                }


                if (DeviceTypeUtil.isContainWatch()) {
                    if(AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)){
                        showSelectPopupWindowW560();
                    }else{
                        showSelectPopupWindowW5xx();
                    }

                } else {

                    showSelectPopupWindow();
                }
                break;
            case R.id.tv_all:
            case R.id.moy_tv_all:
                tv_title.setText(getString(R.string.practise_record));
                getData(Constant.PRACTISE_TYPE_ALL);
                break;
            case R.id.moy_tv_walk:
                tv_title.setText(getString(R.string.walk));
                getData(Constant.PRACTISE_TYPE_WALK);
                break;
            case R.id.moy_tv_run:
                tv_title.setText(getString(R.string.run));
                getData(Constant.PRACTISE_TYPE_RUN);
                break;
            case R.id.moy_tv_ride:
                tv_title.setText(getString(R.string.String_w560_practise_cycle));
                getData(Constant.PRACTISE_TYPE_RIDE);
                break;
            case R.id.moy_tv_rope_skip:
                tv_title.setText(getString(R.string.rope_skipping));
                getData(Constant.PRACTISE_TYPE_ROPE_SKIP);
                break;
            case R.id.moy_tv_badminton:
                tv_title.setText(getString(R.string.badminton));
                getData(Constant.PRACTISE_TYPE_BADMINTON);
                break;
            case R.id.moy_tv_football:
                tv_title.setText(getResources().getString(R.string.football));
                getData(Constant.PRACTISE_TYPE_FOOTBALL);
                break;
            case R.id.moy_tv_basketball:
                tv_title.setText(getString(R.string.basketball));
                getData(Constant.PRACTISE_TYPE_BASKETBALL);
                break;






            case R.id.tv_walk:  //560户外走
                tv_title.setText(getString(R.string.string_w560_practise_out_walk));
                getData(1);
                break;
            case R.id.tv_run:       //户外跑
                tv_title.setText(getString(R.string.string_w560_practise_out_run));
                getData(2);
                break;
            case R.id.tv_ride:      //骑行
                tv_title.setText(getString(R.string.tdoor_cycling1));
                getData(3);
                break;
            case R.id.tv_badminton:        //室内走
                tv_title.setText(getString(R.string.string_w560_practise_indoor_walk));
                getData(4);
                break;
            case R.id.tv_basketball:    //室内跑
                tv_title.setText(getString(R.string.string_w560_practise_indoor_run));
                getData(5);
                break;
            case R.id.tv_football:  //HIIT
                tv_title.setText("HIIT");
                getData(6);
                break;
            case R.id.tv_climbing:
                tv_title.setText(getString(R.string.string_w560_practise_yoga));
                getData(7);
                break;
            case R.id.tv_pingpang:
            case R.id.moy_tv_pingpang:
                tv_title.setText(getString(R.string.string_w560_practise_eliptical));
                getData(8);
                break;

            case R.id.tv_spinning:
                tv_title.setText(getString(R.string.string_w560_pracitse_spinning));
                getData(9);
                break;
            case R.id.tv_hiking:
                tv_title.setText(getString(R.string.string_w560_practise_run));
                getData(10);
                break;
            case R.id.tv_rowing:
                tv_title.setText(getString(R.string.string_w560_practise_rowing));
                getData(11);
                break;
            case R.id.tv_other:
                tv_title.setText(getString(R.string.string_w560_practise_other));
                getData(12);
                break;

            case R.id.iv_select_arrow: {
                if (mMenuViewBirth != null && mMenuViewBirth.isShowing()) {
                    mMenuViewBirth.dismiss();
                }
            }
            break;


            case R.id.tv_all_560:       //所有
                tv_title.setText(getString(R.string.practise_record));
                getData(Constant.PRACTISE_TYPE_ALL);
                break;
            case R.id.tv_walk_560:  //走路
                tv_title.setText(getString(R.string.walk));
                getData(1);
                break;
            case R.id.tv_run_560:   //跑步
                tv_title.setText(getString(R.string.run));
                getData(2);
                break;
            case R.id.tv_ride_560:      //骑行
                tv_title.setText(getString(R.string.ride));
                getData(3);
                break;
            case R.id.tv_badminton_560:     //羽毛球
                tv_title.setText(getString(R.string.badminton));
                getData(5);
                break;
            case R.id.tv_basketball_560:        //篮球
                tv_title.setText(getString(R.string.basketball));
                getData(6);
                break;
            case R.id.tv_football_560:      //足球
                tv_title.setText(getString(R.string.football));
                getData(7);
                break;
            case R.id.tv_climbing_560:      //登山
                tv_title.setText(getString(R.string.climbing));
                getData(8);
                break;
            case R.id.tv_pingpang_560:      //乒乓球
                tv_title.setText(getString(R.string.pingpang));
                getData(9);
                break;




            case R.id.tempview_view:
                if (mMenuViewBirth != null && mMenuViewBirth.isShowing()) {
                    mMenuViewBirth.dismiss();
                }

                if (mMenuViewBirthW5xx != null && mMenuViewBirthW5xx.isShowing()) {
                    mMenuViewBirthW5xx.dismiss();
                }
                break;

        }
    }



    private void showF18PractiseType(){
        if(f18PractiseTypeView == null)
            f18PractiseTypeView = new F18PractiseTypeView(this);
        f18PractiseTypeView.show();
       // f18PractiseTypeView.setListData(F18PractiseTypeConstance.getF18PractiseTypeList());
        f18PractiseTypeView.setF18Listener(new OnF18PractiseDialogClickListener() {
            @Override
            public void onItemClick(int position, String desc) {
                f18PractiseTypeView.dismiss();
                tv_title.setText(desc);
                getData(position);
                f18PractiseTypeView.setListItemChecked(position);
            }
        });
    }






    private boolean isShowPopup = false;


    private BaseDialog mMenuViewBirth;

    private void showSelectPopupWindowW560() {


        if (mMenuViewBirth != null && mMenuViewBirth.isShowing()) {
            mMenuViewBirth.dismiss();
        }


        mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.include_practise_record_select_5xx)
                .fullWidth()
                .setCanceledOnTouchOutside(true)
                .fromBottom(false)
                .show();
        ImageView iv_arrow = mMenuViewBirth.findViewById(R.id.iv_select_arrow);
        TextView tv_all = mMenuViewBirth.findViewById(R.id.tv_all);
        TextView tv_walk = mMenuViewBirth.findViewById(R.id.tv_walk);
        TextView tv_run = mMenuViewBirth.findViewById(R.id.tv_run);
        TextView tv_ride = mMenuViewBirth.findViewById(R.id.tv_ride);
        TextView tv_badminton = mMenuViewBirth.findViewById(R.id.tv_badminton);
        TextView tv_football = mMenuViewBirth.findViewById(R.id.tv_football);
        TextView tv_basketball = mMenuViewBirth.findViewById(R.id.tv_basketball);
        TextView tv_climbing = mMenuViewBirth.findViewById(R.id.tv_climbing);
        TextView tv_pingpang = mMenuViewBirth.findViewById(R.id.tv_pingpang);

        TextView tv_spinning = mMenuViewBirth.findViewById(R.id.tv_spinning);
        TextView tv_hiking = mMenuViewBirth.findViewById(R.id.tv_hiking);
        TextView tv_rowing = mMenuViewBirth.findViewById(R.id.tv_rowing);
        TextView tv_other = mMenuViewBirth.findViewById(R.id.tv_other);

        View tempView = mMenuViewBirth.findViewById(R.id.tempview_view);

        tv_all.setOnClickListener(this);
        tv_walk.setOnClickListener(this);
        assert tv_run != null;
        tv_run.setOnClickListener(this);
        tv_ride.setOnClickListener(PractiseRecordActivity.this);
        tv_badminton.setOnClickListener(PractiseRecordActivity.this);
        tv_football.setOnClickListener(PractiseRecordActivity.this);
        tv_pingpang.setOnClickListener(PractiseRecordActivity.this);
        tv_basketball.setOnClickListener(PractiseRecordActivity.this);
        tv_climbing.setOnClickListener(PractiseRecordActivity.this);
        iv_arrow.setOnClickListener(this);
        tv_spinning.setOnClickListener(this);
        tv_hiking.setOnClickListener(this);
        tv_rowing.setOnClickListener(this);
        tv_other.setOnClickListener(this);

        tempView.setOnClickListener(PractiseRecordActivity.this);

        //运动类型[1-12] outdoor walk, outdoor run, outdoor cycle,
        // indoor walk, indoor run, hiit, yoga, elliptical, spinning,
        // hiking, rowing, other

        switch (mSelectedType) {
            case Constant.PRACTISE_TYPE_ALL:
                tv_all.setTypeface(Typeface.DEFAULT_BOLD);
                tv_all.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 1:   //室外走
                tv_walk.setTypeface(Typeface.DEFAULT_BOLD);
                tv_walk.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 2:  //户外跑
                tv_run.setTypeface(Typeface.DEFAULT_BOLD);
                tv_run.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 3:  //骑行
                tv_ride.setTypeface(Typeface.DEFAULT_BOLD);
                tv_ride.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 4:  //室内走
                tv_badminton.setTypeface(Typeface.DEFAULT_BOLD);
                tv_badminton.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 5:  //室内跑
                tv_basketball.setTypeface(Typeface.DEFAULT_BOLD);
                tv_basketball.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 6:  //HIIT
                tv_football.setTypeface(Typeface.DEFAULT_BOLD);
                tv_football.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;

            case 7:  //瑜伽
                tv_climbing.setTypeface(Typeface.DEFAULT_BOLD);
                tv_climbing.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 8:  //椭圆机
                tv_pingpang.setTypeface(Typeface.DEFAULT_BOLD);
                tv_pingpang.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 9:  //动感单车
                tv_spinning.setTypeface(Typeface.DEFAULT_BOLD);
                tv_spinning.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 10:  //远足
                tv_hiking.setTypeface(Typeface.DEFAULT_BOLD);
                tv_hiking.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 11:   //划船
                tv_rowing.setTypeface(Typeface.DEFAULT_BOLD);
                tv_rowing.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 12:   //其它
                tv_other.setTypeface(Typeface.DEFAULT_BOLD);
                tv_other.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
        }

    }


    private void showSelectPopupWindow() {
        if (mMenuViewBirth != null && mMenuViewBirth.isShowing()) {
            mMenuViewBirth.dismiss();
        }

        mMenuViewBirth = new BaseDialog.Builder(context)
                .setContentView(R.layout.item_practise_moy_layout)
                .fullWidth()
                .setCanceledOnTouchOutside(true)
                .fromBottom(false)
                .show();
        LinearLayout layoutbottom = mMenuViewBirth.findViewById(R.id.moy_layout_bottom);
        ImageView moy_iv_arrow = mMenuViewBirth.findViewById(R.id.iv_select_arrow);
        TextView moy_tv_all = mMenuViewBirth.findViewById(R.id.moy_tv_all);
        TextView moy_tv_walk = mMenuViewBirth.findViewById(R.id.moy_tv_walk);
        TextView moy_tv_run = mMenuViewBirth.findViewById(R.id.moy_tv_run);
        TextView moy_tv_ride = mMenuViewBirth.findViewById(R.id.moy_tv_ride);
        TextView moy_tv_rope_skip = mMenuViewBirth.findViewById(R.id.moy_tv_rope_skip);
        TextView moy_tv_badminton = mMenuViewBirth.findViewById(R.id.moy_tv_badminton);
        TextView moy_tv_football = mMenuViewBirth.findViewById(R.id.moy_tv_football);
        TextView moy_tv_basketball = mMenuViewBirth.findViewById(R.id.moy_tv_basketball);
        TextView moy_tv_pingpang = mMenuViewBirth.findViewById(R.id.moy_tv_pingpang);
        View moy_tempView = mMenuViewBirth.findViewById(R.id.moy_tempview_view);

        moy_iv_arrow.setOnClickListener(this);
        moy_tv_all.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_walk.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_run.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_ride.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_rope_skip.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_badminton.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_football.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_pingpang.setOnClickListener(PractiseRecordActivity.this);
        moy_tv_basketball.setOnClickListener(PractiseRecordActivity.this);
       // moy_tv_climbing.setOnClickListener(PractiseRecordActivity.this);
        moy_tempView.setOnClickListener(PractiseRecordActivity.this);
        if (currentType == JkConfiguration.DeviceType.Watch_W556) {
          //  moy_tv_climbing.setVisibility(View.VISIBLE);
            layoutbottom.setVisibility(View.VISIBLE);
        } else {
            layoutbottom.setVisibility(View.GONE);
          //  moy_tv_climbing.setVisibility(View.INVISIBLE);
        }
        switch (mSelectedType) {
            case Constant.PRACTISE_TYPE_ALL:

                moy_tv_all.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_all.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_WALK:
                moy_tv_walk.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_walk.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_RUN:
                moy_tv_run.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_run.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_RIDE:
                moy_tv_ride.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_ride.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_ROPE_SKIP:
                moy_tv_rope_skip.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_rope_skip.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_BADMINTON:
                moy_tv_badminton.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_badminton.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_FOOTBALL:
                moy_tv_football.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_football.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case Constant.PRACTISE_TYPE_BASKETBALL:
                moy_tv_basketball.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_basketball.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
//            case Constant.PRACTISE_TYPE_CLIMBING:
//                moy_tv_climbing.setTypeface(Typeface.DEFAULT_BOLD);
//                moy_tv_climbing.setTextColor(UIUtils.getColor(R.color.common_view_color));
//                break;
            case Constant.PRACTISE_TYPE_PINGPANG:
                moy_tv_pingpang.setTypeface(Typeface.DEFAULT_BOLD);
                moy_tv_pingpang.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
        }
        isShowPopup = true;

    }




    private BaseDialog mMenuViewBirthW5xx;

    private void showSelectPopupWindowW5xx() {

        if (mMenuViewBirthW5xx != null && mMenuViewBirthW5xx.isShowing()) {
            mMenuViewBirthW5xx.dismiss();
        }

        mMenuViewBirthW5xx = new BaseDialog.Builder(context)
                .setContentView(R.layout.include_practise_record_w560)
                .fullWidth()
                .setCanceledOnTouchOutside(true)
                .fromBottom(false)
                .show();
        ImageView iv_arrow = mMenuViewBirthW5xx.findViewById(R.id.iv_select_arrow);
        TextView tv_all_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_all_560);
        TextView tv_walk_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_walk_560);
        TextView tv_run_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_run_560);
        TextView tv_ride_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_ride_560);
        TextView tv_badminton_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_badminton_560);
        TextView tv_basketball_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_basketball_560);
        TextView tv_football_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_football_560);
        TextView tv_climbing_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_climbing_560);
        TextView tv_pingpang_560 = mMenuViewBirthW5xx.findViewById(R.id.tv_pingpang_560);
        View tempView = mMenuViewBirthW5xx.findViewById(R.id.tempview_view);

        tv_all_560.setOnClickListener(this);
        tv_walk_560.setOnClickListener(this);
        tv_run_560.setOnClickListener(this);
        tv_ride_560.setOnClickListener(PractiseRecordActivity.this);
        tv_badminton_560.setOnClickListener(PractiseRecordActivity.this);
        tv_football_560.setOnClickListener(PractiseRecordActivity.this);
        tv_pingpang_560.setOnClickListener(PractiseRecordActivity.this);
        tv_basketball_560.setOnClickListener(PractiseRecordActivity.this);
        tv_climbing_560.setOnClickListener(PractiseRecordActivity.this);
        iv_arrow.setOnClickListener(this);
        tempView.setOnClickListener(PractiseRecordActivity.this);
        switch (mSelectedType) {
            case 0:  //所有
                tv_all_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_all_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 1:  //走路
                tv_walk_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_walk_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 2:  //跑步
                tv_run_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_run_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 3:   //骑行  //运动类型[1-8] 健走 跑步 骑行 登山 足球 篮球 乒乓球 羽毛球
                tv_ride_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_ride_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 8:  //登山
                tv_climbing_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_climbing_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
            case 7:  //足球
                tv_football_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_football_560.setTextColor(UIUtils.getColor(R.color.common_view_color));

                break;
            case 6:  //篮球
                tv_basketball_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_basketball_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;

            case 9:  //乒乓球
                tv_pingpang_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_pingpang_560.setTextColor(UIUtils.getColor(R.color.common_view_color));

                break;
            case 5: //羽毛球
                tv_badminton_560.setTypeface(Typeface.DEFAULT_BOLD);
                tv_badminton_560.setTextColor(UIUtils.getColor(R.color.common_view_color));
                break;
        }

    }


}
