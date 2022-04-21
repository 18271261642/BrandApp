package phone.gym.jkcq.com.socialmodule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.polidea.rxandroidble2.exceptions.BleGattOperationType;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.RoundImageView;
import brandapp.isport.com.basicres.entry.bean.OssBean;
import brandapp.isport.com.basicres.mvp.BaseMVPFragment;
import brandapp.isport.com.basicres.net.userNet.CommonAliView;
import brandapp.isport.com.basicres.net.userNet.CommonUserPresenter;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.PhotoChoosePopUtil;
import phone.gym.jkcq.com.socialmodule.FriendConstant;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.activity.PersonalHomepageActivity;
import phone.gym.jkcq.com.socialmodule.bean.FriendInfo;
import phone.gym.jkcq.com.socialmodule.bean.ListData;
import phone.gym.jkcq.com.socialmodule.bean.UpdateFollowStateBean;
import phone.gym.jkcq.com.socialmodule.bean.response.DynamBean;
import phone.gym.jkcq.com.socialmodule.bean.result.ResultLikeBean;
import phone.gym.jkcq.com.socialmodule.fragment.present.LikePresent;
import phone.gym.jkcq.com.socialmodule.fragment.present.ReportPresent;
import phone.gym.jkcq.com.socialmodule.fragment.view.LikeView;
import phone.gym.jkcq.com.socialmodule.fragment.view.ReportView;
import phone.gym.jkcq.com.socialmodule.mvp.presenter.FriendPresenter;
import phone.gym.jkcq.com.socialmodule.mvp.view.FriendView;
import phone.gym.jkcq.com.socialmodule.report.FriendMainReportActivity;
import phone.gym.jkcq.com.socialmodule.report.bean.UpdateDynicBean;
import phone.gym.jkcq.com.socialmodule.util.TimeUtil;

public class VideoAllFragment extends BaseMVPFragment<LikeView, LikePresent> implements LikeView, FriendView, ReportView, CommonAliView {

    private static final String TAG = "VideoAllFragment";


    SampleCoverVideo gsyVideoManager;
    TextView view_pause;

    private DynamBean currentBean;
    private String url = "";
    private long mCurrentPosition = 0L;
    private boolean isStart;


    String userId, currentUserId;


    RoundImageView ivHead;
    ImageView iv_like;
    TextView tv_like_number;
    ImageView iv_option;
    TextView tv_profile;
    TextView tv_time;
    TextView tv_nikeName;

    ImageView iv_report;
    TextView tv_report_number;


    FriendPresenter friendPresenter;
    ReportPresent reportPresent;
    CommonUserPresenter commonUserPresenter;
    ProgressBar video_bottom_progressbar;

    ImageView iv_pasue;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"-------播放页面-------");


        if (getArguments() != null) {
            currentBean = (DynamBean) getArguments().getParcelable(FROMBEAN);
            userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp());
            if (currentBean != null) {
                url = currentBean.getVideoUrl();
                currentUserId = currentBean.getUserId();
            } else {
                url = "";
                currentUserId = "";
            }
            // mCurrentPosition = getArguments().getInt(POSITION);
        }
    }


    @Override
    protected void initView(View view) {
        gsyVideoManager = view.findViewById(R.id.videoPlayer);
        view_pause = view.findViewById(R.id.view_pause);
        ivHead = view.findViewById(R.id.iv_head);
        iv_like = view.findViewById(R.id.iv_like);
        tv_like_number = view.findViewById(R.id.tv_like_number);
        iv_option = view.findViewById(R.id.iv_option);
        tv_profile = view.findViewById(R.id.tv_profile);
        tv_time = view.findViewById(R.id.tv_time);
        iv_report = view.findViewById(R.id.iv_report);
        iv_pasue = view.findViewById(R.id.iv_pasue);
        tv_report_number = view.findViewById(R.id.tv_report_number);
        tv_nikeName = view.findViewById(R.id.tv_nikeName);
        video_bottom_progressbar = view.findViewById(R.id.video_bottom_progressbar);
        tv_profile.setMovementMethod(ScrollingMovementMethod.getInstance());

        try {
            if (currentBean != null) {
                gsyVideoManager.loadCoverImage(currentBean.getCoverUrl(), R.drawable.icon_def_video);
            }
            //高斯拉伸视频铺满背景，替换黑色，前台正常比例播放
            gsyVideoManager.getBackButton().setVisibility(View.GONE);
            gsyVideoManager.setLooping(true);
            gsyVideoManager.setNeedShowWifiTip(false);
            gsyVideoManager.setShowFullAnimation(true);
            gsyVideoManager.setDismissControlTime(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("VideoAllFragment", "onStart->mCurrentPosition" + mCurrentPosition);
    }


    @Override
    public void onResume() {
        super.onResume();
        isStart = true;
        Log.e("VideoAllFragment", "onResume->mCurrentPosition" + mCurrentPosition);
        //这里需要判断是哪个fragment为当前显示的fragement

        if (currentBean != null) {
            tv_time.setText(TimeUtil.getDynmicTime(currentBean.getCreateTimeApp(), currentBean.getCreateTime()));
            if ((currentBean.getFollowStatus() == 0 || currentBean.getFollowStatus() == 2) && !TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()).equals(currentBean.getUserId())) {
                iv_option.setVisibility(View.VISIBLE);
                iv_option.setImageResource(R.drawable.icon_home_follow);
            } else {
                iv_option.setVisibility(View.GONE);
            }
            setLikeView(currentBean.isWhetherPraise(), currentBean.getPraiseNums());
        }
        video_bottom_progressbar.setProgress(0);
        if (JkConfiguration.sCurrentFragmentTAG == JkConfiguration.FRAGMENT_COMMUNITY && JkConfiguration.sCurrentCommunityFragment == JkConfiguration.FRAGMENT_ALL) {
            startPlayer();
        }
    }


    public void startPlayer() {
        handler.removeCallbacksAndMessages(null);
        Log.e("startPlayer", "VideoAllFragment mCurrentPosition:" + mCurrentPosition + "，gsyVideoManager.getCurrentState()：" + gsyVideoManager.getCurrentState());
        if (gsyVideoManager.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE) {
            gsyVideoManager.onVideoResume(false);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gsyVideoManager.startPlayLogic();
                }
            }, 500);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("onHiddenChanged", "VideoAllFragment onHiddenChanged" + hidden);
        //Logger.myLog("fragmentNewData:onHiddenChanged:" + hidden);
    }


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    Log.e("view_pause", "onClick---" + secondClickTime + "----" + firstClickTime);
                    if (Math.abs(secondClickTime - firstClickTime) < 500) {
                        secondClickTime = 0;
                        firstClickTime = 0;
                        if (!currentBean.isWhetherPraise()) {
                            mFragPresenter.likeToOhter(currentBean.getDynamicId(), TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), currentBean.getUserId());
                        }
                    } else {
                        Log.e("view_pause", "onClick gsyVideoManager.getCurrentState:" + gsyVideoManager.getCurrentState());

                        if (gsyVideoManager.getGSYVideoManager().isPlaying()) {
                            gsyVideoManager.onVideoPause();
                            iv_pasue.setVisibility(View.VISIBLE);
                        } else {
                            if (gsyVideoManager.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE) {
                                gsyVideoManager.onVideoResume(false);
                                iv_pasue.setVisibility(View.GONE);
                            } else {
                                startPlayer();
                                iv_pasue.setVisibility(View.VISIBLE);
                            }
                        }
                        secondClickTime = 0;
                        firstClickTime = 0;
                    }
                    break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // GSYVideoManager.onPause();
        isStart = false;
        handler.removeCallbacksAndMessages(null);

        try {
//            if(gsyVideoManager == null)
//                return;
//            if(gsyVideoManager.getGSYVideoManager() == null)
//                return;
//            mCurrentPosition = gsyVideoManager.getGSYVideoManager().getPlayer().getBufferedPercentage();
//            gsyVideoManager.onVideoPause();

            GSYVideoManager.onPause();
            video_bottom_progressbar.setProgress(0);

        } catch (Exception e) {
            e.printStackTrace();
            mCurrentPosition = 0;
        }
        Log.e("Pause", "VideoAllFragment onPauseonPause mCurrentPosition" + mCurrentPosition);

        //mCurrentPosition = videoPlayer?.getGSYVideoManager()?.currentPosition ?: 0
    }


    @Override
    protected void initData() {

       /* GSYVideoManager
                .instance()
                .setPlayerInitSuccessListener(new IPlayerInitSuccessListener() {
                    ///播放器初始化成果回调，可用于播放前的自定义设置
                    @Override
                    public void onPlayerInitSuccess(IMediaPlayer player, GSYModel model) {

                        Log.e("initData:","model="+model.getUrl()+"model.getCachePath().getPath()="+model.getCachePath().getPath());
                       if (player instanceof IjkExo2MediaPlayer) {
                            ((IjkExo2MediaPlayer) player).setTrackSelector(new DefaultTrackSelector());
                            ((IjkExo2MediaPlayer) player).setLoadControl(new DefaultLoadControl());
                        }
                    }
                });
*/
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (currentBean != null) {
            tv_profile.setText(currentBean.getContent());
            tv_nikeName.setText("@" + currentBean.getNickName());
            setCommendNum(currentBean.getCommentNums());
            setLikeView(currentBean.isWhetherPraise(), currentBean.getPraiseNums());
            LoadImageUtil.getInstance().loadCirc(getActivity(), currentBean.getHeadUrlTiny(), ivHead);
        }
        mCurrentPosition = 0;
        gsyVideoManager.setUpLazy(url, true, null, null, "");
        // gsyVideoManager.setUpLazy(url, false, null, null, "");
    }

    public void setCommendNum(int number) {
        if (number == 0) {
            tv_report_number.setText(UIUtils.getString(R.string.commend));
        } else {
            tv_report_number.setText(number + "");
        }
    }
    /**
     * 选择普通模式
     */
   /* public void initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, gsyVideoManager, getOrientationOption());
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        if (getGSYVideoPlayer().getFullscreenButton() != null) {
            getGSYVideoPlayer().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFull();
                    clickForFullScreen();
                }
            });
        }
    }*/


    /**
     * 选择builder模式
     */
    public void initVideoBuilderMode() {
    }

    private void startPersonal() {
        Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
        String userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp());

        if (TextUtils.isEmpty(currentUserId)) {
            currentUserId = "";
        }

        Log.e("startPersonal", "userId:" + userId + "currentUserId:" + currentUserId);

        if (!userId.equals(currentUserId)) {
            intent.putExtra(FriendConstant.USER_ID, currentUserId);
        }
        startActivity(intent);
    }

    @Override
    protected void initEvent() {
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonal();
            }
        });
        tv_nikeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonal();
            }
        });
        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragPresenter.likeToOhter(currentBean.getDynamicId(), TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), currentBean.getUserId());
            }
        });
        iv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendPresenter.addFollow(currentBean.getUserId());
            }
        });

        tv_profile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        view_pause.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPhotoChoosePop();
                return false;
            }
        });

        //点击两下为点赞 如果500毫秒点击了两下就让他作为点赞


        view_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClickTime == 0 && secondClickTime == 0) {
                    firstClickTime = System.currentTimeMillis();
                    handler.sendEmptyMessageDelayed(0x01, 500);
                } else {
                    secondClickTime = System.currentTimeMillis();
                }


            }
        });
        view_pause.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("view_pause", "setOnLongClickListener");
                showPhotoChoosePop();
                return true;
            }
        });
        iv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendMainReportActivity.class);
                Log.e("getIntentValue", currentBean.getDynamicId());
                intent.putExtra(FriendConstant.DYNAMIC_ID, currentBean.getDynamicId());
                intent.putExtra(FriendConstant.REPLYCOUNT, currentBean.getCommentNums());
                intent.putExtra(FriendConstant.USER_ID, currentUserId);
                startActivity(intent);
            }
        });
    }

    private long firstClickTime;
    private long secondClickTime;

    private static final String POSITION = "position";
    private static final String FROMBEAN = "bean";

    public static VideoAllFragment newInstance(DynamBean bean, int position) {

        Bundle args = new Bundle();
        args.putParcelable(FROMBEAN, bean);
        args.putInt(POSITION, position);
        VideoAllFragment fragment = new VideoAllFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected LikePresent createPersenter() {
        friendPresenter = new FriendPresenter(this);
        reportPresent = new ReportPresent(this);
        commonUserPresenter = new CommonUserPresenter(this);
        return new LikePresent(this);
    }

    @Override
    public void successLikeToOther(int praiseNums, boolean whetherPraise) {
        //数据源也需要改变
        currentBean.setWhetherPraise(whetherPraise);
        currentBean.setPraiseNums(praiseNums);
        setLikeView(whetherPraise, praiseNums);

    }


    public void setLikeView(boolean isWhetherPraise, int likeNumber) {
        tv_like_number.setText(likeNumber + "");
        if (isWhetherPraise) {
            iv_like.setImageResource(R.drawable.icon_home_like_press);
        } else {
            iv_like.setImageResource(R.drawable.icon_home_like_nor);
        }

    }

    @Override
    public void addFollowSuccess(int type) {
        iv_option.setVisibility(View.VISIBLE);
        iv_option.setImageResource(R.drawable.icon_follow_success);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_option.setVisibility(View.GONE);
            }
        }, 500);

      /*  //需要刷新数据集合的数据 用户id是这个的数据
        UpdateFollowStateBean updateFollowStateBean = new UpdateFollowStateBean();
        updateFollowStateBean.setUserId(currentUserId);
        updateFollowStateBean.setFollowStatus(type);
        EventBus.getDefault().post(new MessageEvent(updateFollowStateBean, MessageEvent.update_dynamic_follow_state));*/


    }


    @Override
    public void unFollowSuccess(int type) {

    }

    @Override
    public void findFriendSuccess(ListData<FriendInfo> friendInfos) {

    }

    @Override
    public void searchFriendSuccess(ListData<FriendInfo> friendInfos) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {

            case MessageEvent.main_dynicid_update:
                UpdateDynicBean updateDynicBean = (UpdateDynicBean) messageEvent.getObj();
                if (currentBean == null) {
                    return;
                }
                if (updateDynicBean.getDynamicId().equals(currentBean.getDynamicId())) {
                    setCommendNum(updateDynicBean.getAllSum());
                }
                break;
            case MessageEvent.video_start:
                iv_pasue.setVisibility(View.GONE);
                break;
            case MessageEvent.all_video_exception:
                if (isStart && gsyVideoManager != null && video_bottom_progressbar != null) {
                    if (gsyVideoManager.getGSYVideoManager().isPlaying()) {
                    } else {
                        startPlayer();
                    }
                }
                break;
            case MessageEvent.update_progress:
                if (isStart && gsyVideoManager != null && video_bottom_progressbar != null) {
                    if (gsyVideoManager != null && gsyVideoManager.getGSYVideoManager().isPlaying()) {
                        if (video_bottom_progressbar.getMax() != gsyVideoManager.getDuration()) {
                            video_bottom_progressbar.setMax(gsyVideoManager.getDuration());
                        }
                        if (gsyVideoManager.getDuration() - gsyVideoManager.getCurrentPositionWhenPlaying() < 200) {
                            video_bottom_progressbar.setProgress(video_bottom_progressbar.getMax());
                        } else {
                            video_bottom_progressbar.setProgress(gsyVideoManager.getCurrentPositionWhenPlaying());
                        }
                        //Log.e("update_progress", "gsyVideoManager.getDuration()="+gsyVideoManager.getDuration() + ",gsyVideoManager.getCurrentPositionWhenPlaying()="+gsyVideoManager.getCurrentPositionWhenPlaying());
                    }
                }
                break;
            case MessageEvent.show_fragment_community:
                Log.e("Event", "show_fragment_community--------" );
                if (isStart && JkConfiguration.sCurrentCommunityFragment == JkConfiguration.FRAGMENT_ALL) {
                    startPlayer();
                }
                break;
            case MessageEvent.show_fragment_other:
                break;
            case MessageEvent.update_dynamic_like_state:
                ResultLikeBean resultLikeBean = (ResultLikeBean) messageEvent.getObj();
                if (currentBean == null) {
                    return;
                }
                Log.e("Event", "update_dynamic_like_state--------" + resultLikeBean.getUserId() + "currentUserId:" + currentUserId);
                if (resultLikeBean.getUserId().equals(currentUserId) && resultLikeBean.getDynamicInfoId().equals(currentBean.getDynamicId())) {
                    setLikeView(resultLikeBean.isWhetherPraise(), resultLikeBean.getPraiseNums());
                }
                break;
            case MessageEvent.update_dynamic_follow_state:
                UpdateFollowStateBean bean = (UpdateFollowStateBean) messageEvent.getObj();
                Log.e("Event", "update_dynamic_follow_state--------" + bean.getUserId());
                if (bean.getUserId().equals(currentUserId)) {
                    currentBean.setFollowStatus(bean.getFollowStatus());
                }
                if (currentBean.getFollowStatus() == 0 || currentBean.getFollowStatus() == 2) {
                    iv_option.setVisibility(View.VISIBLE);
                    iv_option.setImageResource(R.drawable.icon_home_follow);
                } else {
                    iv_option.setVisibility(View.GONE);
                }
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        gsyVideoManager.setVideoAllCallBack(null);
        EventBus.getDefault().unregister(this);
    }

    private PhotoChoosePopUtil photoChoosePopUtil;//弹出选择从哪里读取图片的pop
    private PhotoChoosePopUtil reportChoosePopUtil;//弹出选择从哪里读取图片的pop

    private void showPhotoChoosePop() {

        if (userId.equals(currentUserId)) {

            if (null == photoChoosePopUtil) {
                photoChoosePopUtil = new PhotoChoosePopUtil(context, UIUtils.getString(R.string.friend_dynamic_save_location), true);
            }

        } else {
            if (null == photoChoosePopUtil) {
                photoChoosePopUtil = new PhotoChoosePopUtil(context, UIUtils.getString(R.string.friend_dynamic_report), UIUtils.getString(R.string.friend_dynamic_save_location));
            }
        }

        photoChoosePopUtil.show(getActivity().getWindow().getDecorView());
        photoChoosePopUtil.setOnPhotoChooseListener(new PhotoChoosePopUtil.OnPhotoChooseListener() {
            @Override
            public void onChooseCamera() {
                //操作1
                // checkCamera();
                //保存到本地
                cheackPremission();

                Log.e("showPhotoChoosePop", "onChooseCamerauserId:" + userId + "currentUserId:" + currentUserId);

            }

            @Override
            public void onChoosePhotograph() {
                // checkFileWritePermissions();
                //操作2
                Log.e("showPhotoChoosePop:", "onChoosePhotograph userId:" + userId + "currentUserId:" + currentUserId);

                if (userId.equals(currentUserId)) {
                    //进行删除操作
                    cheackPremission();
                } else {
                    //进行举报选择
                    showReport();
                }
            }
        });


    }

    public void showReport() {
        if (null == reportChoosePopUtil) {
            reportChoosePopUtil = new PhotoChoosePopUtil(context, UIUtils.getString(R.string.friend_report_adv), UIUtils.getString(R.string.friend_report_other));


        }
        reportChoosePopUtil.show(getActivity().getWindow().getDecorView());
        reportChoosePopUtil.setOnPhotoChooseListener(new PhotoChoosePopUtil.OnPhotoChooseListener() {
            @Override
            public void onChooseCamera() {
                //操作1 type:1 广告；2：其他
                // checkCamera();
                Log.e("showPhotoChoosePop:", "onChooseCamera reportType:" + 2);

                reportPresent.reportDynamic(currentBean.getDynamicId(), 2);
            }

            @Override
            public void onChoosePhotograph() {
                Log.e("showPhotoChoosePop:", "onChoosePhotograph reportType:" + 1);
                reportPresent.reportDynamic(currentBean.getDynamicId(), 1);
                // checkFileWritePermissions();
                //操作2
            }
        });

    }

    @Override
    public void successReport() {
        ToastUtils.showToast(BaseApp.getApp(), UIUtils.getString(R.string.friend_option_success));
    }

    @Override
    public void successUpgradeImageUrl(String pathUrl) {

        ToastUtils.showToast(getActivity(), UIUtils.getString(R.string.friend_video_down_succss));

        File file = new File(pathUrl);

        Log.e("successUpgradeImageUrl", "file.lenth" + file.length() + "pathUrl:" + pathUrl);
        if (file.length() > 0) {
            // ImageVideoFileUtils.insertVideo(pathUrl, getActivity());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                ImageVideoFileUtils.insertVideo(pathUrl, getActivity());
            } else {
                ImageVideoFileUtils.saveVideo(getActivity(), file);
            }

        }
        //getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(String.valueOf(file))));
//                    //获取ContentResolve对象，来操作插入视频
//                    ContentResolver localContentResolver = getContentResolver();
//                    //ContentValues：用于储存一些基本类型的键值对
//                    ContentValues localContentValues = getVideoContentValues(MainActivity.this, file, System.currentTimeMillis());
//                    //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
//                    Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
    }


    @Override
    public void successGetAliToken(OssBean ossBean) {
        // FileUtil.getVideoFileName()

        String fileName = currentBean.getVideoUrl().replace("https://isportcloud.oss-cn-shenzhen.aliyuncs.com/", "");

        // //https://isportcloud.oss-cn-shenzhen.aliyuncs.com/seedFeed1221588761909000.mp4
        commonUserPresenter.downFileAli(ossBean.getBucketName(), ossBean.getAccessKeyId(), ossBean.getAccessKeySecret(), ossBean.getSecurityToken(), fileName, fileName);


    }


    private void cheackPremission() {
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(getActivity()),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        //需要判断文件存在并且文件大小与服务器上大小一样才不要去下载
                       /* String fileName = currentBean.getVideoUrl().replace("https://isportcloud.oss-cn-shenzhen.aliyuncs.com/", "");
                        String path = FileUtil.getVideoBinFile(fileName);
                        commonUserPresenter.startDown(currentBean.getVideoUrl(), path, fileName);*/
                        commonUserPresenter.getOssAliToken();
                    }

                    @Override
                    public void onGetPermissionNo() {

                    }
                });

    }

    @Override
    public void upgradeProgress(long currentSize, long totalSize) {

    }

    @Override
    public void onFailAliOptin(int type) {

    }


    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .init();
    }
}
