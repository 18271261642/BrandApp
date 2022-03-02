package com.isport.brandapp.home.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.LogActivity;
import com.isport.brandapp.R;
import com.isport.brandapp.Third_party_access.util.GoogleFitUtil;
import com.isport.brandapp.home.AccessThridAppActivity;
import com.isport.brandapp.home.SettingActivity;
import com.isport.brandapp.home.presenter.FragmentMinePresenter;
import com.isport.brandapp.home.view.FragmentMineView;
import com.isport.brandapp.message.MessageActivity;
import com.isport.brandapp.util.ActivitySwitcher;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import bike.gymproject.viewlibray.FriendItemView;
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.CommonFriendRelation;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonnet.interceptor.BaseObserver;
import brandapp.isport.com.basicres.commonnet.interceptor.ExceptionHandle;
import brandapp.isport.com.basicres.commonnet.net.RxScheduler;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.commonview.RoundImageView;
import brandapp.isport.com.basicres.entry.bean.PhotoEventBean;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import brandapp.isport.com.basicres.net.userNet.CommonUserPresenter;
import brandapp.isport.com.basicres.net.userNet.CommonUserView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.socialmodule.FriendConstant;
import phone.gym.jkcq.com.socialmodule.activity.FansActivity;
import phone.gym.jkcq.com.socialmodule.activity.FollowActivity;
import phone.gym.jkcq.com.socialmodule.activity.FriendActivity;
import phone.gym.jkcq.com.socialmodule.activity.FriendScanActivity;
import phone.gym.jkcq.com.socialmodule.activity.PersonalHomepageActivity;
import phone.gym.jkcq.com.socialmodule.activity.SportRankActivity;
import phone.gym.jkcq.com.socialmodule.manager.QRCodeManager;
import phone.gym.jkcq.com.socialmodule.net.APIService;
import phone.gym.jkcq.com.socialmodule.net.RetrofitClient;

/**
 * 底部菜单，我的页面
 * Created by huashao on 2017/10/24.
 */
public class NewMineFragment extends BaseMVPFragment<FragmentMineView, FragmentMinePresenter> implements View
        .OnClickListener, FragmentMineView, CommonUserView {


    ItemView itemview_ranking, itemview_fireware_upgrade, itemview_mythrid, itemview_setting, itemview_log;

    CommonUserPresenter commonUserPresenter;
    private QRCodeManager mQRCodeManager;


    private TextView tvName;
    RoundImageView ivHead;
    ImageView iv_mine_left;
    private View view_cmd;
    private ImageView iv_mine_message, iv_sign;
    private TextView tv_message_count;
    private ImageView ivMineScan;
    private TextView tvMyProfile;
    private RelativeLayout layout_common_head;
    private FriendItemView tv_friend, tv_follow, tv_fans, tv_feed;

    NestedScrollView nestedScroll;

    private int mMessageCounts = 0;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    private UserInfoBean user;


    @Override
    protected FragmentMinePresenter createPersenter() {
        commonUserPresenter = new CommonUserPresenter(this);
        return new FragmentMinePresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.myLog("Fragment Mine onResume");
        getNetWork();
    }


    public void getNetWork() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isA = NetworkUtils.isAvailable();
                emitter.onNext(isA);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).as(this.bindAutoDispose()).subscribe(new BaseObserver<Boolean>(BaseApp.getApp(), false) {
            @Override
            public void onNext(Boolean tempInfo) {
                if (!tempInfo) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    return;
                }
                commonUserPresenter.getUserinfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                commonUserPresenter.getUserFriendRelation(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
            }

            @Override
            protected void hideDialog() {

            }

            @Override
            protected void showDialog() {

            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {

            }

        });

    }


    private boolean isHidden;

    long lastClickTime = 0;
    long currentClickTime = 0;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        Log.e("succcessDynamList", "onHiddenChanged" + hidden);
        isHidden = hidden;
        if (!isHidden) {
            ImmersionBar.with(this).titleBar(R.id.layout_head)
                    .statusBarDarkFont(true).init();
            currentClickTime = System.currentTimeMillis();
            if (lastClickTime == 0) {
                lastClickTime = System.currentTimeMillis();
                quest();
            } else {
                if (currentClickTime - lastClickTime > 10 * 1000) {
                    lastClickTime = currentClickTime;
                    quest();
                }
            }


        }
        //nestedScroll.scrollTo(0,0);
    }

    public void quest() {
        getNetWork();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_new_mine;
    }

    @Override
    protected void initView(View view) {
        Logger.myLog("Fragment Mine initView");
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        tv_message_count = view.findViewById(R.id.tv_message_count);
        layout_common_head = view.findViewById(R.id.layout_common_head);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        tvMyProfile = view.findViewById(R.id.tv_myProfile);
        ivHead = view.findViewById(R.id.iv_head);
        tvName = view.findViewById(R.id.tv_name);
        view_cmd = view.findViewById(R.id.view_cmd);
        ivMineScan = view.findViewById(R.id.iv_mine_scan);
        iv_mine_left = view.findViewById(R.id.iv_mine_left);
        iv_mine_message = view.findViewById(R.id.iv_mine_message);
        iv_sign = view.findViewById(R.id.iv_sign);
        itemview_ranking = view.findViewById(R.id.itemview_ranking);
        itemview_fireware_upgrade = view.findViewById(R.id.itemview_fireware_upgrade);
        itemview_mythrid = view.findViewById(R.id.itemview_mythrid);
        itemview_setting = view.findViewById(R.id.itemview_setting);
        itemview_log = view.findViewById(R.id.itemview_log);
        tv_feed = view.findViewById(R.id.tv_feed);
        tv_fans = view.findViewById(R.id.tv_fans);
        tv_follow = view.findViewById(R.id.tv_follow);
        tv_friend = view.findViewById(R.id.tv_friend);


    }


    int count;

    @Override
    protected void initEvent() {
        view_cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count >= 5) {
                    Intent intent = new Intent(getActivity(), LogActivity.class);
                    startActivity(intent);
                    count = 0;
                }
            }
        });
        iv_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        tv_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
                startActivity(intent);
            }
        });
        tv_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                Intent intent = new Intent(getActivity(), FansActivity.class);
                intent.putExtra(FriendConstant.USER_ID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                startActivity(intent);
            }
        });
        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                intent.putExtra(FriendConstant.USER_ID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                startActivity(intent);
            }
        });
        tv_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                intent.putExtra(FriendConstant.USER_ID, TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()));
                startActivity(intent);
            }
        });


        itemview_log.setOnContentClickListener(new ItemView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), LogActivity.class);
                startActivity(intent);
            }
        });

        itemview_ranking.setOnContentClickListener(new ItemView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                onRankingItem();
            }
        });

        itemview_fireware_upgrade.setOnContentClickListener(new ItemView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                onDevcieUpgrade();
            }
        });

        itemview_mythrid.setOnContentClickListener(new ItemView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                onThirdDeviceItemListener();
            }
        });
        itemview_setting.setOnContentClickListener(new ItemView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                onSettingOnclick();
            }
        });


        layout_common_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
                startActivity(intent);
            }
        });
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
                startActivity(intent);
            }
        });

        ivMineScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }

                new RxPermissions(getActivity())
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    startActivity(new Intent(getActivity(), FriendScanActivity.class));
                                } else {
                                    com.blankj.utilcode.util.ToastUtils.showLong(R.string.please_open_camera_permission);
                                }
                            }
                        });

//                mQRCodeManager.scanQRCode(getActivity());
//                mQRCodeManager.setOnScanResultListener(new QRCodeManager.OnScanResultListener() {
//                    @Override
//                    public void onScanResult(String result) {
//                        getUserIdByQrString(result);
//                    }
//                });
            }
        });


        iv_mine_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MessageActivity.class));
            }
        });
    }


    @Override
    protected void initData() {
        mQRCodeManager = new QRCodeManager(getActivity());
        // mFragPresenter.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(app));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case JkConfiguration.requestCode.personInformation://TODO 如果有信息修改,则重新获取基本数据
                if (resultCode == JkConfiguration.resultCode.personInformation) {
                    mFragPresenter.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(app));
                }
                break;
            case JkConfiguration.requestCode.changePic:
                break;
            case REQUEST_OAUTH_REQUEST_CODE: {
                Logger.myLog("onActivityResult：");
                GoogleFitUtil.insertData(getActivity());
                break;
            }
            default:
                //handleSignInResult(requestCode, data);
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.GET_BIND_DEVICELIST_SUCCESS:
            case MessageEvent.WHEN_CONNECTSTATE_CHANGED:
                /*if (mineItemHolder != null) {
                    notifyDeviceList(context);
                }*/
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PhotoEventBean messageEvent) {
        Log.e("CustomRepository 111", messageEvent.toString());
        updateData(messageEvent.bean.headUrl);
        user.setHeadUrl(messageEvent.bean.headUrl);
        user.setHeadUrlTiny(messageEvent.bean.headUrlTiny);
        AppConfiguration.saveUserInfo(user);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void successGetUserInfo(UserInfoBean userInfo) {
        if (userInfo != null) {
            Log.e("CustomRepository 000", userInfo.toString());
            updateData(userInfo);
            user = userInfo;
        }

    }

    public void updateData(String headUrl) {
        LoadImageUtil.getInstance().loadCirc(context, headUrl, ivHead);
    }

    public void updateData(UserInfoBean userInfo) {
        updateData(userInfo.getNickName(), userInfo.getHeadUrl(), userInfo.getMyProfile());
    }

    public void updateData(String name, String headUrl, String myProfile) {
        tvName.setText(name);
        tvMyProfile.setText(TextUtils.isEmpty(myProfile) ? UIUtils.getString(R.string.friend_enter_myprofile) : myProfile);
        if (App.appType() == App.httpType) {
            LoadImageUtil.getInstance().loadCirc(context, headUrl, ivHead);
        } else {
            if (!TextUtils.isEmpty(headUrl)) {
                ivHead.setImageBitmap(BitmapFactory.decodeFile(headUrl));
            }
        }
    }


    @Override
    public void postDeviceList() {

    }

    public void onSettingOnclick() {
        Intent activitySetting = new Intent(getActivity(), SettingActivity.class);
        startActivity(activitySetting);
    }

    int countLog = 0;

    public void onLog() {
        countLog++;
        if (countLog == 5) {
            Intent intent = new Intent(context, LogActivity.class);
            startActivity(intent);
            countLog = 0;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    public void onThirdDeviceItemListener() {

        Intent intent = new Intent(getActivity(), AccessThridAppActivity.class);
        startActivity(intent);

    }

    public void onDevcieUpgrade() {
        PublicAlertDialog.getInstance().showDialog(UIUtils.getString(R.string.fir_upgrade_title), UIUtils.getString(R.string.fir_upgrade_content), context, getResources().getString(R.string.cancel), getResources().getString(R.string.confirm), new AlertDialogStateCallBack() {
            @Override
            public void determine() {
                Constants.CAN_RECONNECT = false;
                if(DeviceTypeUtil.isContainF18()){
                    AppSP.putString(getActivity(),AppSP.F18_SAVE_MAC,null);
                }
                ISportAgent.getInstance().disConDevice(false);
                ActivitySwitcher.goScanActivty(getActivity(), JkConfiguration.DeviceType.DFU);
            }

            @Override
            public void cancel() {

            }
        }, false);


    }


    @Override
    public void onSuccessUserInfo(UserInfoBean userInfo) {
        if (userInfo != null) {
            Log.e("CustomRepository 000", userInfo.toString());
            updateData(userInfo);
            user = userInfo;
        }
    }

    int followState, fans, friend, follow;

    @Override
    public void onSuccessUserFriendRelation(CommonFriendRelation commonFriendRelation) {
        if (commonFriendRelation != null) {
            followState = commonFriendRelation.getFollowStatus();
            fans = commonFriendRelation.getFansNums();
            friend = commonFriendRelation.getFriendNums();
            follow = commonFriendRelation.getFollowNums();
            tv_follow.setValue(follow);
            tv_fans.setValue(fans);
            tv_friend.setValue(friend);
            tv_feed.setValue(commonFriendRelation.getTrendsNums());
        }


    }


    private void getUserIdByQrString(String qrString) {

        new NetworkBoundResource<String>() {
            @Override
            public Observable<String> getFromDb() {
                return null;
            }

            @Override
            public Observable<String> getNoCacheData() {
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
            public Observable<String> getRemoteSource() {
                return RetrofitClient.getRetrofit().create(APIService.class).getUserIdByQrString(qrString).compose
                        (RxScheduler.Obs_io_main()).compose(RetrofitClient.transformer);

            }

            @Override
            public void saveRemoteSource(String bean) {

            }
        }.getAsObservable().subscribe(new BaseObserver<String>(BaseApp.getApp()) {
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
            public void onNext(String info) {

                if (info != null) {
                    Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
                    intent.putExtra(FriendConstant.USER_ID, info);
                    startActivity(intent);
                }

            }
        });
        ;
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.layout_head)
                .statusBarDarkFont(true).init();
    }

    public void onRankingItem() {
        startActivity(new Intent(getActivity(), SportRankActivity.class));
    }

    public void setmMessageCount(int count) {
        mMessageCounts = count;
        if (tv_message_count != null) {

            if (mMessageCounts > 0 && mMessageCounts <= 99) {
                tv_message_count.setVisibility(View.VISIBLE);
                tv_message_count.setText("" + mMessageCounts);
            } else if (mMessageCounts > 99) {
                mMessageCounts = 99;
                tv_message_count.setVisibility(View.VISIBLE);
                tv_message_count.setText("" + mMessageCounts + "+");
            } else {
                tv_message_count.setVisibility(View.INVISIBLE);
            }
        }
    }
}
