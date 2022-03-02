package com.isport.brandapp.wu.activity;

import android.view.View;
import android.widget.LinearLayout;

import com.isport.brandapp.R;
import com.isport.brandapp.wu.adapter.OnceHrAdapter;
import com.isport.brandapp.wu.bean.OnceHrInfo;
import com.isport.brandapp.wu.mvp.OnceHrHistoryView;
import com.isport.brandapp.wu.mvp.presenter.OnceHrHistoryPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * 测量记录
 */
public class OnceHrListActivity extends BaseMVPTitleActivity<OnceHrHistoryView, OnceHrHistoryPresenter> implements OnceHrHistoryView {

    LinearLayout ll_nodata;
    private RecyclerView recyclerview_oxy;
    private OnceHrAdapter mAdapter;
    private ArrayList<OnceHrInfo> mDataList = new ArrayList<>();

    private SmartRefreshLayout refresh_layout;
    private int mCurrentPage = 0;
    private boolean isPullRefresh = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_oxylist;
    }

    @Override
    protected void initView(View view) {
        ll_nodata = findViewById(R.id.ll_nodata);
        recyclerview_oxy = findViewById(R.id.recyclerview_oxy);
        recyclerview_oxy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new OnceHrAdapter(this, mDataList);
        recyclerview_oxy.setAdapter(mAdapter);

        //下拉刷新，到底自动加载更多
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setEnableFooterFollowWhenNoMoreData(true);
        refresh_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isPullRefresh = false;
                mActPresenter.getOnceHrHistoryData(++mCurrentPage);

            }
        });
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isPullRefresh = true;
                mCurrentPage = 0;
                mActPresenter.getOnceHrHistoryData(mCurrentPage);
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {
        titleBarView.setTitle(getResources().getString(R.string.mian_title_once_hr));
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
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
    protected OnceHrHistoryPresenter createPresenter() {
        return new OnceHrHistoryPresenter();
    }

    protected void initData() {
        mActPresenter.getOnceHrHistoryData(mCurrentPage);
    }


    @Override
    public void getOnceHrHistoryDataSuccess(List<OnceHrInfo> info) {
        if (refresh_layout != null) {
            if (info != null && info.size() > 0) {
                ll_nodata.setVisibility(View.GONE);
                if (isPullRefresh) {
                    mDataList.clear();
                    refresh_layout.finishRefresh();
                    refresh_layout.setEnableLoadMore(true);
                } else {
                    refresh_layout.finishLoadMore();
                }
                if (info.size() < 10) {
                    refresh_layout.finishLoadMoreWithNoMoreData();
                }
                if (mAdapter != null) {
                    mDataList.addAll(info);
                    mAdapter.replaceData(mDataList);
                }
            } else {
                if (isPullRefresh) {
                    ll_nodata.setVisibility(View.VISIBLE);
                } else {
                    refresh_layout.finishLoadMoreWithNoMoreData();
                }

            }
        }
    }
}
