package phone.gym.jkcq.com.socialmodule.report.ranking;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonnet.net.RxScheduler;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import io.reactivex.Observable;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.adapter.RopeRankFragmentAdapter;
import phone.gym.jkcq.com.socialmodule.net.APIService;
import phone.gym.jkcq.com.socialmodule.net.RetrofitClient;

/**
 * 跳绳排行榜
 */
public class RopeRankActivity extends BaseTitleActivity {

    private TabLayout tab_layout;
    private ViewPager2 viewpager_rank;
    private LinearLayout ll_launch;
    private LinearLayout ll_tab_rank;
    private Button btn_launch;
    private String mTitles[] = {UIUtils.getString(R.string.rope_number), UIUtils.getString(R.string.rope_ranking_time)};

    private TextView tv_content1;
    private TextView tv_content2;


    private UserInfoBean mUserInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_rank;
    }

    private boolean isEnableRank;
    private RopeRankFragmentAdapter mRankAdapter;

    @Override
    protected void initView(View view) {
        tv_content1 = findViewById(R.id.tv_content1);
        tv_content2 = findViewById(R.id.tv_content2);

        tv_content1.setText(getResources().getString(R.string.see_friend_rope_rank));
        tv_content2.setText(getResources().getString(R.string.friend_see_your_roperank));

        if (view != null) {
            ll_tab_rank = view.findViewById(R.id.ll_tab_rank);
            ll_launch = view.findViewById(R.id.ll_launch);
            btn_launch = view.findViewById(R.id.btn_launch);
            tab_layout = view.findViewById(R.id.tab_layout);
            viewpager_rank = view.findViewById(R.id.viewpager_rank);
            mUserInfo = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
            if (mUserInfo == null) {
                finish();
            }
            isEnableRank = mUserInfo.isEnableRopeRanking();
            titleBarView.setTitle(getString(R.string.rope_ranking));
            titleBarView.setRightIcon(R.drawable.icon_tope);
            setEnableRankView(isEnableRank);
            initViewPager();
        }
    }

    private void initViewPager() {
        mRankAdapter = new RopeRankFragmentAdapter(RopeRankActivity.this);
        viewpager_rank.setAdapter(mRankAdapter);
        tab_layout.setSelectedTabIndicatorHeight(0);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tab_layout, viewpager_rank, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //Logger.e("onConfigureTab","position="+position);
                tab.setText(mTitles[position]);
            }
        });
        tabLayoutMediator.attach();
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        btn_launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableRank(true);
            }
        });

    }

    @Override
    protected void initHeader() {

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {
                PublicAlertDialog.getInstance().showDialog(getString(R.string.ensure_stop_use), getResources().getString(R.string.ensure_rope_tips), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        enableRank(false);
                    }

                    @Override
                    public void cancel() {
                    }
                }, false);
            }
        });
    }

    /**
     * 是否启用排行显示不同的视图
     *
     * @param isRank
     */
    private void
    setEnableRankView(Boolean isRank) {
        if (isRank) {
            ll_launch.setVisibility(View.GONE);
            titleBarView.setRightIconVisible(true);
            ll_tab_rank.setVisibility(View.VISIBLE);

        } else {
            ll_launch.setVisibility(View.VISIBLE);
            titleBarView.setRightIconVisible(false);
            ll_tab_rank.setVisibility(View.GONE);
        }
    }

    public void enableRank(boolean isEnableRank) {
        new NetworkBoundResource<Boolean>() {
            @Override
            public Observable<Boolean> getFromDb() {
                return null;
            }

            @Override
            public Observable<Boolean> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public Observable<Boolean> getRemoteSource() {

                Map<String, String> map = new HashMap();
                //map.put("mobile", mUserInfo.getMobile());
                map.put("enableRopeRanking", isEnableRank + "");
                map.put("userId", mUserInfo.getUserId());
                return RetrofitClient.getRetrofit().create(APIService.class).ropeEnableRank(map).compose
                        (RxScheduler.Obs_io_main()).compose(RetrofitClient.transformer);
            }

            @Override
            public void saveRemoteSource(Boolean bean) {

            }
        }.getAsObservable().subscribe(new BaseObserver<Boolean>(BaseApp.getApp()) {
            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

                if (e.code == 2000) {
                    finish();
                }


            }

            @Override
            public void onNext(Boolean isEnable) {
                if (!isEnable) {
                    finish();
                } else {
                    setEnableRankView(isEnable);
                }

            }
        });
    }
}
