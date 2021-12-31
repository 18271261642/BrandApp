
package phone.gym.jkcq.com.socialmodule.activity;

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
import phone.gym.jkcq.com.socialmodule.adapter.RankFragmentAdapter;
import phone.gym.jkcq.com.socialmodule.net.APIService;
import phone.gym.jkcq.com.socialmodule.net.RetrofitClient;

/**
 * 好友排行页面
 */
public class SportRankActivity extends BaseTitleActivity {

    private TabLayout tab_layout;
    private ViewPager2 viewpager_rank;
    private LinearLayout ll_launch;
    private LinearLayout ll_tab_rank;
    private Button btn_launch;
    private String mTitles[] = {UIUtils.getString(R.string.friend_outdoor_step), UIUtils.getString(R.string.friend_outdoor_calorie), UIUtils.getString(R.string.friend_outdoor_running), UIUtils.getString(R.string.friend_treadmill),UIUtils.getString(R.string.friend_tdoor_cycling),UIUtils.getString(R.string.friend_walking)};

    private UserInfoBean mUserInfo;
    private TextView tv_content1;
    private TextView tv_content2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_rank;
    }

    private boolean isEnableRank;
    private RankFragmentAdapter mRankAdapter;

    @Override
    protected void initView(View view) {
        tv_content1 = findViewById(R.id.tv_content1);
        tv_content2 = findViewById(R.id.tv_content2);

        tv_content1.setText(getResources().getString(R.string.see_friend_sport_rank));
        tv_content2.setText(getResources().getString(R.string.friend_see_yout_sportrank));
        if (view != null) {
            ll_tab_rank = view.findViewById(R.id.ll_tab_rank);
            ll_launch = view.findViewById(R.id.ll_launch);
            btn_launch = view.findViewById(R.id.btn_launch);
            tab_layout = view.findViewById(R.id.tab_layout);
            viewpager_rank = view.findViewById(R.id.viewpager_rank);
            mUserInfo = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
            isEnableRank = mUserInfo.isEnableRanking();
            titleBarView.setRightIcon(R.drawable.icon_tope);
            setEnableRankView(isEnableRank);
            initViewPager();
        }
    }

    private void initViewPager() {
        mRankAdapter = new RankFragmentAdapter(SportRankActivity.this);
        viewpager_rank.setAdapter(mRankAdapter);
        //  tab_layout.setSelectedTabIndicator(getResources().getDrawable(R.drawable.shape_color_limit));
        // SetIndicator.setIndicator(tab_layout, DisplayUtils.px2dip(this, 2f), DisplayUtils.px2dip(this, 2f));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tab_layout, viewpager_rank, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
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
                enableRank();
            }
        });
    }

    @Override
    protected void initHeader() {
        titleBarView.setTitle(getString(R.string.rank_friend));
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {
                PublicAlertDialog.getInstance().showDialog(getString(R.string.ensure_stop_use), getResources().getString(R.string.ensure_stop_tips), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        enableRank();
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

    public void enableRank() {
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
                map.put("interfaceId", "0");
                map.put("mobile", mUserInfo.getMobile());
                map.put("userId", mUserInfo.getUserId());
                return RetrofitClient.getRetrofit().create(APIService.class).enableRank(map).compose
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
