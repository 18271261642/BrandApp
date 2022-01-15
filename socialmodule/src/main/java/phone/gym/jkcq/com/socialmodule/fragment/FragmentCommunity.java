package phone.gym.jkcq.com.socialmodule.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.ACache;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ThreadPoolUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import brandapp.isport.com.basicres.entry.bean.OssBean;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.net.userNet.CommonAliView;
import brandapp.isport.com.basicres.net.userNet.CommonUserPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;
import phone.gym.jkcq.com.socialmodule.ImageUtil;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.activity.FriendCameraActivity;
import phone.gym.jkcq.com.socialmodule.activity.FriendSearchActivity;
import phone.gym.jkcq.com.socialmodule.bean.requst.RequestAddDynamicBean;
import phone.gym.jkcq.com.socialmodule.fragment.adapter.CommPagerAdapter;
import phone.gym.jkcq.com.socialmodule.fragment.present.AddDynamPresent;
import phone.gym.jkcq.com.socialmodule.fragment.view.AddDynamView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class FragmentCommunity extends BaseMVPFragment<AddDynamView, AddDynamPresent> implements CommonAliView, AddDynamView {

    LinearLayout toolBar;
    ImageView iv_send_thum;

    boolean isCommingFirst = false;

    public boolean isFirst;

    ViewPager viewPager_community;
    CommPagerAdapter viewPagerAdapter;
    private ImageView iv_add;
    private ImageView iv_find;
    private RadioButton tv_home_follow, tv_all;
    //发送动态的百分比
    private RelativeLayout layout_start_send;

    TextView tv_progess;

    String videoPath;

    CommonUserPresenter commonUserPresenter;

    RequestAddDynamicBean addDynamicBean;
    OssBean ossBean;
    String aliPath;

    //发送失败逻辑处理
    TextView btn_try_again;
    RelativeLayout layout_send_fail;
    ImageView iv_dynamic_fail_close;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //发送超时监听
                case 0x00:
                    layout_send_fail.setVisibility(View.VISIBLE);
                    break;
                case 0x01:
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.follow_video_exception));
                    break;
                case 0x02:
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.all_video_exception));
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_community;
    }

    long lastClickTime = 0;
    long currentClickTime = 0;


    long lastClickItem = 0;
    long currentClickItemTime = 0;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //需要对这个mCurrentDevice进行处理
        //  mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
        Log.e("FragmentCommunity：", "onHiddenChanged:" + hidden + "GSYVideoManager.instance().getPlayPosition()" + GSYVideoManager.instance().getPlayPosition());

        if (!hidden) {
            currentClickTime = System.currentTimeMillis();
            if (lastClickTime == 0) {
                lastClickTime = System.currentTimeMillis();
                startTimer();
            } else {
                if (currentClickTime - lastClickTime > 10 * 1000) {
                    lastClickTime = currentClickTime;
                    startTimer();
                }
            }

        } else {
        }

    }


    @Override
    public void onPause() {
        Log.e("FragmentCommunity", "onPause");
        endTime();
        GSYVideoManager.releaseAllVideos();
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void initView(View view) {

        fragments.add(PageFollowFragment.newInstance("", 0, JkConfiguration.DynamicInfoType.FOLLOW));
        //return PageFragment.newInstance("", position, JkConfiguration.DynamicInfoType.FOLLOW);
        fragments.add(PageAllFragment.newInstance("", 1, JkConfiguration.DynamicInfoType.ALL));
        layout_start_send = view.findViewById(R.id.layout_start_send);
        iv_send_thum = view.findViewById(R.id.iv_send_thum);
        tv_progess = view.findViewById(R.id.tv_progess);
        btn_try_again = view.findViewById(R.id.btn_try_again);
        layout_start_send.setVisibility(View.GONE);

        viewPager_community = view.findViewById(R.id.viewPager_community);
        toolBar = view.findViewById(R.id.toolBar);
        iv_find = view.findViewById(R.id.iv_find);
        iv_add = view.findViewById(R.id.iv_add);
        iv_dynamic_fail_close = view.findViewById(R.id.iv_dynamic_fail_close);
        tv_home_follow = view.findViewById(R.id.tv_home_follow);
        tv_all = view.findViewById(R.id.tv_all);
        viewPagerAdapter = new CommPagerAdapter(getChildFragmentManager(), fragments, new String[]{"海淀", "推荐"});
        viewPager_community.setAdapter(viewPagerAdapter);
        viewPager_community.setCurrentItem(1, false);
        layout_send_fail = view.findViewById(R.id.layout_send_fail);

        requestPermiss();
    }

    Disposable disposableTimer;

    public void endTime() {
        Log.e("video_pause", "endTime" + disposableTimer + "----");

        if (disposableTimer != null && !disposableTimer.isDisposed()) {
            Log.e("video_pause", "endTime  VideoAllFragment" + disposableTimer + "----" + disposableTimer.isDisposed());
            disposableTimer.dispose();
        }
    }

    public void startTimer() {
        try {
            if (disposableTimer != null && !disposableTimer.isDisposed()) {
                disposableTimer.dispose();
            }
            disposableTimer = Observable.interval(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.update_progress));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    NetworkChang networkChang;

    @Override
    protected void initData() {
        btn_try_again.setTag("step0");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChang = new NetworkChang();
        getActivity().registerReceiver(networkChang, intentFilter);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        VideoOptionModel videoOptionModel =
                new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 50);
        VideoOptionModel videoOptionModelDec =
                new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        VideoOptionModel videoOptionModelbox =
                new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 1);
        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        VideoOptionModel videoOptionModelBuffer = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        list.add(videoOptionModelDec);
        list.add(videoOptionModelBuffer);
        list.add(videoOptionModelbox);
        GSYVideoManager.instance().setOptionModelList(list);
        //GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
    }

    @Override
    protected void initEvent() {
        iv_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                if (!ViewMultiClickUtil.onMultiClick(iv_find)) {
                    startActivity(new Intent(getActivity(), FriendSearchActivity.class));
                }
            }
        });
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                String tag = (String) btn_try_again.getTag();

                Log.e("iv_add", tag);
                if (!ViewMultiClickUtil.onMultiClick(iv_add)) {

                    if (TextUtils.isEmpty(tag) || tag.equals("step0")) {

                        startActivity(new Intent(getActivity(), FriendCameraActivity.class));
                    } else {
                        ToastUtils.showToast(getActivity(), UIUtils.getString(R.string.friend_dynamic_posting));
                    }
                }
            }
        });
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                JkConfiguration.sCurrentCommunityFragment = JkConfiguration.FRAGMENT_ALL;
                if (viewPager_community.getCurrentItem() != 1) {
                    setItemClick(1, tv_home_follow);
                    // viewPager_community.setCurrentItem(1);
                } else {
                    if (!ViewMultiClickUtil.onMultiClick(tv_all)) {
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.video_all));
                    }
                }
            }
        });
        tv_home_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                JkConfiguration.sCurrentCommunityFragment = JkConfiguration.FRAGMENT_FOLLOW;
                if (viewPager_community.getCurrentItem() != 0) {
                    setItemClick(0, tv_all);
                } else {
                    //
                    if (!ViewMultiClickUtil.onMultiClick(tv_home_follow)) {
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.video_follow));
                    }
                }
            }
        });


        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                tryagin(btn_try_again.getTag().toString());
                ImmersionBar.with(getActivity()).statusBarDarkFont(false).init();
                layout_send_fail.setVisibility(View.GONE);
            }
        });
        iv_dynamic_fail_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }
                tv_progess.setText("0%");
                btn_try_again.setTag("step0");
                layout_start_send.setVisibility(View.GONE);
                ImmersionBar.with(getActivity()).statusBarDarkFont(false).init();
                layout_send_fail.setVisibility(View.GONE);
            }
        });

        viewPager_community.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("FragmentCommunity", "onPageScrolled" + "position:" + position + "positionOffset" + positionOffset + ",positionOffsetPixels:" + positionOffsetPixels);


                if (position == 0 && positionOffset == 0 && positionOffsetPixels == 0) {
                    handler.removeMessages(0x01);
                    handler.removeMessages(0x02);
                    handler.sendEmptyMessageDelayed(0x01, 0);

                }
                if (position == 1 && positionOffset == 0 && positionOffsetPixels == 0) {
                    handler.removeMessages(0x01);
                    handler.removeMessages(0x02);
                    handler.sendEmptyMessageDelayed(0x02, 0);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    JkConfiguration.sCurrentCommunityFragment = JkConfiguration.FRAGMENT_FOLLOW;
                    tv_all.setChecked(false);
                    tv_home_follow.setChecked(true);
                    toolBar.setVisibility(View.VISIBLE);
                    GSYVideoManager.onPause();
                } else if (position == 1) {
                    JkConfiguration.sCurrentCommunityFragment = JkConfiguration.FRAGMENT_ALL;
                    isFirst = true;
                    tv_all.setChecked(true);
                    tv_home_follow.setChecked(false);
                    toolBar.setVisibility(View.VISIBLE);
                    GSYVideoManager.onPause();
                } else if (position == 2) {
                    toolBar.setVisibility(View.GONE);
                    GSYVideoManager.onPause();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
       /* viewPager_community.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

               *//* if (position == 1 && positionOffset > 0.1f && isFirst) {
                    //跳转到我的页面
                    isFirst = false;
                    Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
                    intent.putExtra(FriendConstant.USER_ID, JkConfiguration.CurrentAllUserId);
                    startActivity(intent);
                }*//*
                Log.e("FragmentCommunity", "onPageScrolled" + "position:" + position + "positionOffset" + positionOffset + ",positionOffsetPixels:" + positionOffsetPixels);


                if (position == 0 && positionOffset == 0 && positionOffsetPixels == 0) {
                    handler.removeMessages(0x01);
                    handler.removeMessages(0x02);
                    handler.sendEmptyMessageDelayed(0x01, 1500);

                }
                if (position == 1 && positionOffset == 0 && positionOffsetPixels == 0) {
                    handler.removeMessages(0x01);
                    handler.removeMessages(0x02);
                    handler.sendEmptyMessageDelayed(0x02, 1500);
                }

            }

            @Override
            public void onPageSelected(int position) {

                super.onPageSelected(position);
                if (position == 0) {
                    tv_all.setChecked(false);
                    tv_home_follow.setChecked(true);
                    toolBar.setVisibility(View.VISIBLE);
                    GSYVideoManager.onPause();
                } else if (position == 1) {
                    isFirst = true;
                    tv_all.setChecked(true);
                    tv_home_follow.setChecked(false);
                    toolBar.setVisibility(View.VISIBLE);
                    GSYVideoManager.onPause();
                } else if (position == 2) {
                    toolBar.setVisibility(View.GONE);
                    GSYVideoManager.onPause();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

*/
    }

    //同一秒内只能点一下
    private void setItemClick(int postion, RadioButton view) {
        //viewPager_community.setCurrentItem(postion);
        currentClickItemTime = System.currentTimeMillis();
        if (lastClickItem == 0) {
            lastClickItem = System.currentTimeMillis();
            viewPager_community.setCurrentItem(postion);
            // startTimer();
        } else {

            Log.e("setItemClick", currentClickItemTime - lastClickItem + "");
            if (currentClickItemTime - lastClickItem > 500) {
                lastClickItem = currentClickItemTime;
                viewPager_community.setCurrentItem(postion);
                //startTimer();
            } else {
                view.setChecked(true);
            }
        }
    }


    @Override
    public void successUpgradeImageUrl(String pathUrl) {
        this.aliPath = pathUrl;
        ACache aCache = ACache.get(BaseApp.getApp());
        String value = aCache.getAsString("update");
        if (TextUtils.isEmpty(value)) {
            aCache.put("update", 30 * 1000);
            step3();
        }


    }

    @Override
    public void successGetAliToken(OssBean ossBean) {
        //String videoName = "CustomBg" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + ".mp4";

        this.ossBean = ossBean;
        step2();
    }

    @Override
    public void upgradeProgress(long currentSize, long totalSize) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                int curretProgress = 0;
                if (currentSize >= totalSize) {
                    curretProgress = 100;
                } else {
                    curretProgress = (int) (currentSize * 1.0f / totalSize * 100);
                }
                if (curretProgress == 0) {
                    curretProgress = 1;
                }
                tv_progess.setText(curretProgress + "%");
                if (curretProgress == 100) {
                    handler.removeMessages(0x00);
                    btn_try_again.setTag("step0");
                    tv_progess.setText("0%");
                    layout_start_send.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    protected AddDynamPresent createPersenter() {
        commonUserPresenter = new CommonUserPresenter(this);
        return new AddDynamPresent(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
        /*if (isCommingFirst) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.video_all));
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.video_follow));
                }
            }, 0);
        }*/
        isCommingFirst = true;

        Log.e("FragmentCommunity", "onResume");
    }

    @Override
    public void onRespondError(String message) {

    }

    @Override
    public void successSendDynamic() {
        //弹出发送成功对话框
        handler.removeMessages(0x00);
        btn_try_again.setTag("step0");
        tv_progess.setText("0%");
        layout_start_send.setVisibility(View.GONE);
        //刷新数据到顶部
        ToastUtils.showToast(getActivity(), UIUtils.getString(R.string.friend_send_dynamic_success));
    }


    public void step1() {
        handler.removeMessages(0x00);
        handler.sendEmptyMessageDelayed(0x00, 1000 * 60 * 60 * 2);
        btn_try_again.setTag("step1");
        commonUserPresenter.getOssAliToken();
    }

    public void step2() {
        handler.removeMessages(0x00);
        handler.sendEmptyMessageDelayed(0x00, 1000 * 60 * 60 * 2);
        btn_try_again.setTag("step2");
        String videoName = "SeedFeed" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + System.currentTimeMillis() + ".mp4";
        if (!TextUtils.isEmpty(videoPath)) {
            commonUserPresenter.upgradeVideoAli(ossBean.getBucketName(), ossBean.getAccessKeyId(), ossBean.getAccessKeySecret(), ossBean.getSecurityToken(), videoName, videoPath);
        }
    }

    public void step3() {
        handler.removeMessages(0x00);
        handler.sendEmptyMessageDelayed(0x00, 1000 * 60 * 60 * 2);
        btn_try_again.setTag("step3");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (addDynamicBean != null) {
                    mFragPresenter.sendDynacim(addDynamicBean.getUserId(), addDynamicBean.getContent(), addDynamicBean.getCoverUrl(), aliPath);
                }
            }
        });
    }

    private void tryagin(String tag) {


        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isAvailable()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tag.equals("step1")) {
                                step1();
                            } else if (tag.equals("step2")) {
                                step2();
                            } else if (tag.equals("step3")) {
                                step3();
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeMessages(0x00);
                            layout_send_fail.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });


    }


    @Override
    public void failSendDynamic() {

        btn_try_again.setTag("step3");
        ImmersionBar.with(getActivity()).statusBarDarkFont(true).init();
        handler.removeMessages(0x00);
        layout_send_fail.setVisibility(View.VISIBLE);
        //step3();
        //弹出失败对话框
        //ToastUtils.showToast(getActivity(), UIUtils.getString(R.string.friend_send_dynamic_fail));
    }

    @Override
    public void onFailAliOptin(int type) {
        ImmersionBar.with(getActivity()).statusBarDarkFont(true).init();
        if (type == 1) {
            btn_try_again.setTag("step1");

        } else if (type == 2) {
            btn_try_again.setTag("step2");
            //step2();
        }
        handler.removeMessages(0x00);
        layout_send_fail.setVisibility(View.VISIBLE);


    }


    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.toolBar).keyboardEnable(true).init();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.hide_radioButton:
                Log.e("MessageEvent", "hide_radioButton");
                tv_home_follow.setVisibility(View.INVISIBLE);
                tv_all.setVisibility(View.INVISIBLE);
                break;
            case MessageEvent.show_radioButton:
                Log.e("MessageEvent", "show_radioButton");
                tv_home_follow.setVisibility(View.VISIBLE);
                tv_all.setVisibility(View.VISIBLE);
                break;
            case MessageEvent.newwork_change:
                Log.e("MessageEvent", "MessageEvent.newwork_change");
                ThreadPoolUtils.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("MessageEvent", "MessageEvent.newwork_change NetworkUtils.isAvailable()" + NetworkUtils.isAvailable());
                        if (!NetworkUtils.isAvailable()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String tag = (String) btn_try_again.getTag();
                                    if (TextUtils.isEmpty(tag)) {
                                        tag = "step0";
                                    }
                                    if (!tag.equals("step0")) {
                                        commonUserPresenter.cancelVideo();
                                        handler.removeMessages(0x00);
                                        layout_send_fail.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });


                //tryagin(btn_try_again.getTag().toString());
                break;
            case MessageEvent.send_dynamic:
                //传过来 imageUrl
                //传过来视频的本地地址
                addDynamicBean = (RequestAddDynamicBean) messageEvent.getObj();
                if (addDynamicBean != null) {
                    ACache aCache = ACache.get(BaseApp.getApp());
                    String value = aCache.getAsString("update");
                    if(!TextUtils.isEmpty(value)){
                        aCache.remove("update");
                    }
                    videoPath = addDynamicBean.getVideoUrl();
                    Bitmap bitmap = ImageUtil.getVideoThumbnail(videoPath, DisplayUtils.dip2px(getActivity(), 50), DisplayUtils.dip2px(getActivity(), 66), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    iv_send_thum.setImageBitmap(bitmap);
                    layout_start_send.setVisibility(View.VISIBLE);
                    step1();
                }
                break;
            case MessageEvent.show_iv_add:
                iv_add.setVisibility(View.VISIBLE);
                break;
            case MessageEvent.hide_iv_add:
                iv_add.setVisibility(View.GONE);
                break;
            case MessageEvent.show_fragment_community:
                break;
            case MessageEvent.show_fragment_other:
                // endTime();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();

        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(networkChang);
    }


    private void requestPermiss(){
        XXPermissions.with(getActivity())
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(onPermissionCallback);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            XXPermissions.with(getActivity())
//                    .permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//                    .request(onPermissionCallback);
//        }
    }

    private final OnPermissionCallback onPermissionCallback = new OnPermissionCallback() {
        @Override
        public void onGranted(List<String> list, boolean b) {

        }
    };
}
