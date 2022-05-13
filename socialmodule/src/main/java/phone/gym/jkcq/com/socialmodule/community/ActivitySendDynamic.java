package phone.gym.jkcq.com.socialmodule.community;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.common.OSSLog;
import com.blankj.utilcode.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.ThreadPoolUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.entry.bean.OssBean;
import brandapp.isport.com.basicres.net.userNet.CommonAliView;
import brandapp.isport.com.basicres.net.userNet.CommonUserPresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;
import phone.gym.jkcq.com.socialmodule.FriendConstant;
import phone.gym.jkcq.com.socialmodule.ImageUtil;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.bean.requst.RequestAddDynamicBean;
import phone.gym.jkcq.com.socialmodule.fragment.ImageVideoFileUtils;
import phone.gym.jkcq.com.socialmodule.video.cut.ZVideoView;

//发布动态页面
public class ActivitySendDynamic extends BaseTitleActivity implements CommonAliView {
    public final String NOR = "nor";
    private final String PRESS = "press";
    private EditText et_content;
    private TextView tv_currentcount;
    private TextView btn_send;
    private ZVideoView trimmer_view;
    private ImageView iv_save_location, iv_start_player, iv_bg_video;
    int currentCount;
    CommonUserPresenter commonUserPresenter;
    String videoPath;
    String imagePath;
    boolean isUpdate;


    private OssBean tmpOssBen;

    //是否是第一次点击发布按钮
    private boolean isFirstSend = false;

    @Override
    protected int getLayoutId() {
        return R.layout.friend_activity_send_dynamic;
    }

    @Override
    protected void initView(View view) {

        et_content = view.findViewById(R.id.et_content);
        btn_send = view.findViewById(R.id.btn_send);
        tv_currentcount = view.findViewById(R.id.tv_currentcount);
        iv_start_player = view.findViewById(R.id.iv_start_player);
        iv_bg_video = view.findViewById(R.id.iv_bg_video);
        trimmer_view = view.findViewById(R.id.video_loader);
        iv_save_location = view.findViewById(R.id.iv_save_location);
        //改变默认的单行模式
        et_content.setSingleLine(false);

        //水平滚动设置为False

        //    btn_send.setOnClickListener(this);
        et_content.setHorizontallyScrolling(false);
        videoPath = getIntent().getStringExtra(FriendConstant.VIDEO_PATH);

        if (TextUtils.isEmpty(videoPath)) {
            ToastUtils.showLong("没有找到数据");
            finish();
            return;
        }
        trimmer_view.setVideoURI(Uri.parse(videoPath));


        // trimmer_view.pause();
    }

    @Override
    protected void initData() {
        isUpdate = false;
        //titleBarView.setrightBgTextView(UIUtils.getString(R.string.friend_send_dynamic));
        titleBarView.setTitle(UIUtils.getString(R.string.friend_send_content_edit));
        titleBarView.setLeftIcon(R.drawable.friend_icon_dynacma_close);
        iv_save_location.setTag(NOR);
        iv_save_location.setImageResource(R.drawable.friend_icon_save_location_nor);
        commonUserPresenter = new CommonUserPresenter(this);
        //进来后先获取token
        commonUserPresenter.getOssAliToken();

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
            Log.e("video_pause", "startTimer" + disposableTimer + "----");
            disposableTimer = Observable.interval(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    Log.e("startTimer", "FragmentCommunity gsyVideoManager.getDuration()" + aLong + "trimmer_view.getDuration()" + trimmer_view.getDuration() + "trimmer_view.getCurrentPosition():" + trimmer_view.getCurrentPosition());
                    if (trimmer_view.getDuration() - 200 <= trimmer_view.getCurrentPosition()) {
                        trimmer_view.pause();
                        trimmer_view.start();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    Handler handler = new Handler();

    @Override
    protected void initEvent() {
        iv_save_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) iv_save_location.getTag();
                if (TextUtils.isEmpty(tag))
                    tag = NOR;
                if (tag.equals(NOR)) {
                    iv_save_location.setTag(PRESS);
                    iv_save_location.setImageResource(R.drawable.friend_icon_save_location);
                } else {
                    iv_save_location.setTag(NOR);
                    iv_save_location.setImageResource(R.drawable.friend_icon_save_location_nor);
                }
            }
        });
        iv_start_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_bg_video.setVisibility(View.INVISIBLE);
                iv_start_player.setVisibility(View.GONE);
                trimmer_view.start();
                startTimer();
            }
        });

        trimmer_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimmer_view.pause();
                endTime();
                iv_start_player.setVisibility(View.VISIBLE);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUpdate) {
                    isUpdate = true;
                    NetProgressObservable.getInstance().show(UIUtils.getString(R.string.data_loading));

                    saveDynamic();
                } else {

                }
            }
        });
        titleBarView.setOnTitleBarBgClickListener(new TitleBarView.onRightBgClickListener() {
            @Override
            public void onRightBgClicked(View view) {

            }
        });
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                isBack();
            }

            @Override
            public void onRightClicked(View view) {

            }


        });


        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Logger.e("onTextChanged:" + s.toString() + ",s.toString().length():" + s.toString().length() + ",start:" + start + ",before:" + before + ",count:" + count);

                currentCount = s.toString().length();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_currentcount.setText(currentCount + "");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }





    private void saveDynamic() {
        //上传图片到阿里云
        //在上传视频到阿里云
        //在把动态保存到自己的服务器

        if(tmpOssBen == null){
            isFirstSend = true;
            commonUserPresenter.getOssAliToken();
            return;
        }

        String imgName = "coverImg" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + System.currentTimeMillis() + ".jpg";

        ThreadPoolUtils.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                commonUserPresenter.upgradeVideoAli(tmpOssBen.getBucketName(), tmpOssBen.getAccessKeyId(), tmpOssBen.getAccessKeySecret(), tmpOssBen.getSecurityToken(), imgName, imagePath);
            }
        });
    }


    @Override
    protected void initHeader() {

    }


    @Override
    public void successUpgradeImageUrl(String pathUrl) {
        RequestAddDynamicBean addDynamicBean = new RequestAddDynamicBean();
        //0为视频，1为图文
        addDynamicBean.setContentType(0);
        addDynamicBean.setCoverUrl(pathUrl);
        addDynamicBean.setVideoUrl(videoPath);
        addDynamicBean.setContent(et_content.getText().toString().trim());
        addDynamicBean.setUserId(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()));
        EventBus.getDefault().post(new MessageEvent(addDynamicBean, MessageEvent.send_dynamic));
        if (iv_save_location.getTag().equals(PRESS)) {
            //需要保存视频到本地


            //  FileUtils.getFileName()

            //FileUtil.saveToFile();

            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    File fileSrc = new File(videoPath).getParentFile();
                    if (!fileSrc.exists()) {
                        fileSrc.mkdirs();
                    }
                    File srcFile = new File(videoPath);
                    Log.e("successUpgradeImageUrl", videoPath + "srcFile:" + srcFile);
                    if (srcFile != null) {
                        Log.e("successUpgradeImageUrl", videoPath + "srcFile:" + srcFile.length());
                    }
                    if (srcFile != null && srcFile.length() > 0) {
                        FileInputStream fis = null;
                        byte[] buffer = new byte[0];
                        String name = "speed" + System.currentTimeMillis() + ".mp4";
                        try {
                            fis = new FileInputStream(srcFile);
                            buffer = new byte[fis.available()];
                            File file = FileUtil.getImageFile(name);
                            String path = file.getAbsolutePath();
                            FileOutputStream fout = new FileOutputStream(file);
                            fis.read(buffer);
                            fout.write(buffer);
                            fis.close();//关流释放资源
                            fout.close();
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                ImageVideoFileUtils.insertVideo(path, ActivitySendDynamic.this);
                            } else {
                                ImageVideoFileUtils.saveVideo(ActivitySendDynamic.this, file);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OSSLog.logInfo(e.toString());
                        } finally {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    isUpdate = false;
                                    ActivityManager.getInstance().finishAllActivity("MainActivity");
                                }
                            });
                        }
                    }
                }
            });


           /* try {
                File file = new File(videoPath);
                if (file!=null && file.length() > 0) {
                    // ImageVideoFileUtils.insertVideo(pathUrl, getActivity());


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
*/


        } else {
            isUpdate = false;
            ActivityManager.getInstance().finishAllActivity("MainActivity");
        }
        //把数据有的都关闭
        //finish();

        Log.e("successUpgradeImageUrl", "successUpgradeImageUrl:" + pathUrl);
    }


    //获取token成功
    @Override
    public void successGetAliToken(OssBean ossBean) {
        this.tmpOssBen = ossBean;
        Log.e(TAG,"------------动态发布 获取oss="+ossBean.toString());
        if(isFirstSend){
            String imgName = "coverImg" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + System.currentTimeMillis() + ".jpg";

            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    commonUserPresenter.upgradeVideoAli(ossBean.getBucketName(), ossBean.getAccessKeyId(), ossBean.getAccessKeySecret(), ossBean.getSecurityToken(), imgName, imagePath);
                }
            });
        }
    }

    @Override
    public void upgradeProgress(long currentSize, long totalSize) {

    }

    @Override
    public void onFailAliOptin(int type) {
        isUpdate = false;
    }

    @Override
    public void onRespondError(String message) {
        isUpdate = false;
    }


    @Override
    protected void onDestroy() {
        trimmer_view.pause();
        endTime();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        isBack();
    }

    private void isBack() {
        if (isUpdate) {
            return;
        }
        PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.friend_exit_no_save_content), context, getResources().getString(R.string.cancel), getResources().getString(R.string.friend_exit), new AlertDialogStateCallBack() {
            @Override
            public void determine() {

                ActivityManager.getInstance().finishAllActivity("MainActivity");
                // finish();
            }

            @Override
            public void cancel() {

            }
        }, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bitmap bitmap = ImageUtil.getVideoThumbnail(videoPath, DisplayUtils.dip2px(this, 360), DisplayUtils.dip2px(this, 640), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        iv_bg_video.setImageBitmap(bitmap);
        cheackPremission(bitmap);
    }

    private void cheackPremission(Bitmap bitmap) {
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(this),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
                        //需要判断文件存在并且文件大小与服务器上大小一样才不要去下载
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                File file = FileUtil.writeImage(bitmap, FileUtil.getImageFile(FileUtil.getVideoImageFileName()), 100);
                                //File file = FileUtil.writeImage(bitmap, path, 100);
                                if (file != null && file.length() > 0) {
                                    imagePath = file.getPath();
                                }

                                Log.e("imagePath:", "imagePath:" + imagePath + "file.getAbsolutePath():" + file.getAbsolutePath() + "" + " file.length():" + file.length());
                            }
                        });
                    }

                    @Override
                    public void onGetPermissionNo() {

                    }
                });

    }


}
