package phone.gym.jkcq.com.socialmodule.personal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.KeyboardUtils;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.StringUtil;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.RoundImageView;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.commonview.UserDialogSetting;
import brandapp.isport.com.basicres.commonview.UserDialogView;
import brandapp.isport.com.basicres.entry.bean.OssBean;
import brandapp.isport.com.basicres.entry.bean.PhotoEventBean;
import brandapp.isport.com.basicres.entry.bean.UpdatePhotoBean;
import brandapp.isport.com.basicres.net.userNet.CommonAliView;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import brandapp.isport.com.basicres.net.userNet.CommonUserPresenter;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.BitmapUtils;
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;
import phone.gym.jkcq.com.commonres.commonutil.PhotoChoosePopUtil;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.personal.presenter.UpgradeImagePresenter;
import phone.gym.jkcq.com.socialmodule.personal.view.UpgradeImageView;

//编辑个人资料页面
public class EditUserInfo extends BaseTitleActivity implements View.OnClickListener, UserDialogView, UpgradeImageView, CommonAliView, View.OnLayoutChangeListener, View.OnFocusChangeListener {

    private UserInfoBean srcUserBean;
    boolean isHeightChage, isWeightChange, isBirthdayChage, ismyProfile;
    RelativeLayout layout_bgiamge;
    TextView tv_edit;
    RoundImageView iv_head;
    UserDialogSetting dialogSetting;

    LinearLayout layout_top;
    UpgradeImagePresenter upgradeImagePresenter;
    CommonUserPresenter commonUserPresenter;


    boolean isGenderChage;
    boolean isNickNameChange;


    ItemView itemSex, itemWeight, itemHeight, itemBirthday, item_individual_resume;
    EditText edtName;
    ImageView ivBg;

    EditText et_myProfile;
    String desUserName, desPrifile;

    int currentCount = 0;
    int sumCount = 200;
    NestedScrollView scroll;


    private String saveImgPath;


    @Override
    protected int getLayoutId() {
        return R.layout.friend_activity_setting_user_info;
    }

    @Override
    protected void initView(View view) {
        layout_bgiamge = view.findViewById(R.id.layout_bgiamge);
        tv_edit = view.findViewById(R.id.tv_edit);
        iv_head = view.findViewById(R.id.iv_head);
        itemSex = findViewById(R.id.item_date_gender);
        itemWeight = findViewById(R.id.item_date_weight);
        itemHeight = findViewById(R.id.item_date_height);
        itemBirthday = findViewById(R.id.item_date_birth);
        layout_top = findViewById(R.id.layout_top);
        scroll = findViewById(R.id.scrollview);
        item_individual_resume = findViewById(R.id.item_individual_resume);
        edtName = findViewById(R.id.et_name);
        // tvNext = findViewById(R.id.btn_next);
        ivBg = findViewById(R.id.iv_bg);
        et_myProfile = findViewById(R.id.et_myProfile);
        //改变默认的单行模式
        et_myProfile.setSingleLine(false);

        //水平滚动设置为False

        et_myProfile.setHorizontallyScrolling(false);
        et_myProfile.getRootView().setBackgroundColor(Color.WHITE);
        edtName.getRootView().setBackgroundColor(Color.WHITE);
        layout_top.getRootView().setBackgroundColor(this.getResources().getColor(R.color.white));
        initKeyBoardListener(scroll);
        // layout_bg.getBackground().mutate().setAlpha(153);
        //layout_bg.getBackground().mutate().setAlpha(10);
    }

    @Override
    protected void initData() {
        // LoadImageUtil.getInstance().load(EditUserInfo.this, R.drawable.friend_bg_homepage, ivBg);
        saveImgPath = Environment.getExternalStorageDirectory().getPath()+"/Download/";

        titleBarView.setRightIcon(R.drawable.icon_player_press);
        titleBarView.setTitle(UIUtils.getString(R.string.edit_user_info));

        dialogSetting = new UserDialogSetting(this);
        upgradeImagePresenter = new UpgradeImagePresenter(this);
        commonUserPresenter = new CommonUserPresenter(this);

        srcUserBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(context));

        if (srcUserBean != null) {
            getUserInfo(srcUserBean);
        }

    }

    @Override
    protected void initEvent() {

        titleBarView.setOnTitleBarBgClickListener(new TitleBarView.onRightBgClickListener() {
            @Override
            public void onRightBgClicked(View view) {
                saveUserInfo();
            }
        });
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                isBack();
            }

            @Override
            public void onRightClicked(View view) {

                saveUserInfo();
            }


        });


        et_myProfile.addTextChangedListener(new TextWatcher() {
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
                        item_individual_resume.setContentText(currentCount + "/200");
                        //  tv_currentcount.setText(currentCount + "");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initHeader() {
        //  StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.common_bg));
        //  StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_date_birth) {
            KeyboardUtils.hideKeyboard(v);
            dialogSetting.setPopupWindow(EditUserInfo.this, itemBirthday, JkConfiguration.GymUserInfo
                    .USERBIRTHDAY, itemBirthday.getContentText());
        } else if (v.getId() == R.id.item_date_gender) {
            KeyboardUtils.hideKeyboard(v);
            dialogSetting.popWindowSelect(EditUserInfo.this, itemSex, JkConfiguration.GymUserInfo
                    .GENDER, itemSex.getContentText(), false);
        } else if (v.getId() == R.id.item_date_height) {
            KeyboardUtils.hideKeyboard(v);
            dialogSetting.popWindowSelect(EditUserInfo.this, itemHeight, JkConfiguration.GymUserInfo
                    .HEIGHT, itemHeight.getContentText(), true);
        } else if (v.getId() == R.id.item_date_weight) {
            KeyboardUtils.hideKeyboard(v);
            dialogSetting.popWindowSelect(EditUserInfo.this, itemWeight, JkConfiguration.GymUserInfo
                    .WEIGHT, itemWeight.getContentText(), true);
        } else if (v.getId() == R.id.tv_edit) {
            new RxPermissions(EditUserInfo.this)
                    .request(Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            showPhotoChoosePop();
                        } else {
                            ToastUtils.showToast(EditUserInfo.this, "need permissions");
                        }
                    });
        } else if (v.getId() == R.id.layout_head) {
            Intent intent = new Intent(this, FriendActivityImageShow.class);
            if (srcUserBean != null) {
                intent.putExtra("pic_list", srcUserBean.getHeadUrl());
            }
            startActivity(intent);
        }


    }


    @Override
    public void dataSetSuccess(String select, String data) {
        switch (select) {

            case JkConfiguration.GymUserInfo.GENDER:
                itemSex.setContentText(data);
                break;
            case JkConfiguration.GymUserInfo.HEIGHT:
                //int height = Integer.parseInt(data);
                itemHeight.setContentText(data);
                if (srcUserBean != null) {
                    if (srcUserBean.getHeight().equals(StringUtil.getNumberStr(data))) {
                        isHeightChage = false;
                    } else {
                        isHeightChage = true;
                    }
                }
                break;
            case JkConfiguration.GymUserInfo.WEIGHT:
                //int weight = Integer.parseInt(data);
                itemWeight.setContentText(data);
                if (srcUserBean != null) {
                    if (srcUserBean != null && srcUserBean.getWeight().equals(StringUtil.getNumberStr(data))) {
                        isWeightChange = false;
                    } else {
                        isWeightChange = true;
                    }
                }
                break;
            case JkConfiguration.GymUserInfo.USERBIRTHDAY:
                itemBirthday.setContentText(data);
                if (srcUserBean != null) {
                    if (srcUserBean.getBirthday().equals(StringUtil.getNumberStr(data))) {
                        isBirthdayChage = false;
                    } else {
                        isBirthdayChage = true;
                    }
                }
                break;
        }
    }

    @Override
    public void onRespondError(String message) {

    }


    /**
     * 从照片选择或照相
     */
    private PhotoChoosePopUtil photoChoosePopUtil;
    // 调用系统相片 拍照
    private final int PHOTO_REQUEST_CAMERA = 1;
    // 从相册中选择
    private final int PHOTO_REQUEST_GALLERY = 2;

    private final int PHOTO_CUT = 3;
    private File tempFile;
    private File cutFile;
    private String mImgPath;

    private void showPhotoChoosePop() {
        if (null == photoChoosePopUtil) {
            photoChoosePopUtil = new PhotoChoosePopUtil(context);
        }
        photoChoosePopUtil.show(getWindow().getDecorView());
        photoChoosePopUtil.setOnPhotoChooseListener(new PhotoChoosePopUtil.OnPhotoChooseListener() {
            @Override
            public void onChooseCamera() {
                checkCamera();
            }

            @Override
            public void onChoosePhotograph() {
                checkFileWritePermissions();
            }
        });
    }

    private void checkCamera() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            camera();
                        } else {
                            com.blankj.utilcode.util.ToastUtils.showLong("no permission");
                        }
                    }
                });
    }

    Uri uri;
    private File cameraSavePath;//拍照照片路径

    //激活相机操作
    private void goCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (FileUtil.isSDExists()) {
            String date = String.valueOf(System.currentTimeMillis());
            cameraSavePath = new File(FileUtil.getImageFile().getAbsolutePath(), date + ".jpeg");
            if (!cameraSavePath.exists()) {
                FileUtil.createFile(cameraSavePath.getAbsolutePath());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                        cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            } else {
                uri = Uri.fromFile(cameraSavePath);
            }

            Log.e("goCamera", cameraSavePath.length() + "------------cameraSavePath.length()");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        }
    }

    /*
     * 从相机获取
     */
    public void camera() {
//        NetProgressObservable.getInstance().show(false);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (FileUtil.isSDExists()) {
            String date = String.valueOf(System.currentTimeMillis());
            tempFile = new File(FileUtil.getImageFile().getAbsolutePath(), date + ".jpeg");
            if (!tempFile.exists()) {
                FileUtil.createFile(tempFile.getAbsolutePath());
            }


            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context.getApplicationContext(), getPackageName() + ".fileprovider", tempFile);
            } else {
                uri = Uri.fromFile(tempFile);
            }
            Log.e("NewShareActivity", "uri=" + uri.getPath());
            Log.e("NewShareActivity", "tempFile=" + tempFile.getName());
            String path = BitmapUtils.getRealFilePath(context, uri);
            Log.e("NewShareActivity", "tempFileRealFilePath" + path + "tempFile:" + tempFile.length());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        }
    }

    private void checkFileWritePermissions() {
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            gallery();
                        } else {
                            ToastUtils.showToast(EditUserInfo.this, "open permission");
                        }
                    }
                });
    }

    /**
     * 从相册获取
     */
    public void gallery() {
//        NetProgressObservable.getInstance().show(false);
        Intent intent = new Intent(Intent.ACTION_PICK);    // 激活系统图库，选择一张图片
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY); // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
    }


    private void startCotP(Uri uri) {
        int size = (int) (ScreenUtils.getScreenWidth() * 1.0);
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //去除默认的人脸识别，否则和剪裁匡重叠
            intent.putExtra("noFaceDetection", false);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        }
        // 设置裁剪
        intent.putExtra("crop", "true");
//华为特殊处理 不然会显示圆
        if (Build.MANUFACTURER.equals("HUAWEI")) {
            //9998
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        // outputX outputY 是裁剪图片宽高
        //intent.putExtra("outputX", size);
        // intent.putExtra("outputY", size);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        //miui系统 特殊处理 return-data的方式只适用于小图。
        if (Build.MANUFACTURER.contains("Xiaomi")) {
            //裁剪后的图片Uri路径，uritempFile为Uri类变量
            String photoName = getTimeByyyyyMMddhhmmss(System.currentTimeMillis()) + ".jpg";

            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + photoName);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        } else {
            intent.putExtra("return-data", true);
        }
        //保存格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, PHOTO_CUT);
    }

    /**
     * 调用系统裁剪
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        try {
            int size = (int) (ScreenUtils.getScreenWidth() * 1.0);
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            }
            //裁剪后输出路径
            if (FileUtil.isSDExists()) {
                String date = String.valueOf(System.currentTimeMillis());
                //            mImgPath=getExternalFilesDir(null).toString()+ "/" + date + ".jpg";
                //            mImgPath= getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+ "/" + date + ".jpg";
                mImgPath = FileUtil.getImageFile().getAbsolutePath() + "/" + date + ".jpg";
                cutFile = new File(mImgPath);
                if (!cutFile.exists()) {
                    FileUtil.createFile(cutFile.getAbsolutePath());
                }
                //所有版本这里都这样调用
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cutFile));
//               intent.putExtra(MediaStore.EXTRA_OUTPUT,  getUriForFile(NewShareActivity.this,cutFile));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,  uri);
                //输入图片路径
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                if (Build.MANUFACTURER.equals("HUAWEI")) {
                    intent.putExtra("aspectX", 9998);
                    intent.putExtra("aspectY", 9999);
                } else {
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                }

                intent.putExtra("outputX", 480);
                intent.putExtra("outputY", 480);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("return-data", false);

//                uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/brandapp/" + "small.jpg");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
//                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, PHOTO_CUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri uritempFile;

    /**
     * 获取uri
     *
     * @param context
     * @param file
     * @return
     */
    public Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    private void setCustomBg(String filePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                //BleProgressObservable.getInstance().show(UIUtils.getString(R.string.));

                // LoadImageUtil.getInstance().displayImagePath(EditUserInfo.this, filePath, ivBg);

                File file = new File(filePath);

                if (file != null && file.length() > 0) {

                    Logger.e("filePath" + filePath + "file:" + file.length());
                    commonUserPresenter.getOssAliToken();
                }
                /*BitmapDrawable bd = new BitmapDrawable(ImageUtils.getBitmap(filePath));
                layout_bgiamge.setBackground(bd);*/
               /* iv_sure1.setVisibility(View.GONE);
                iv_sure2.setVisibility(View.GONE);
                iv_sure3.setVisibility(View.GONE);
                fl_share_content.setBackground(bd);
                //setAlpha 0-255
                fl_share_content.getBackground().mutate().setAlpha(153);*/
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "requestCode=" + requestCode + "resultCode=" + resultCode);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:

                // 从相册返回的数据
                if (data != null && resultCode == RESULT_OK) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String path = BitmapUtils.getRealFilePath(context, uri);
//                   setCustomBg(path);
                    if ("Redmi4A".equals(AppUtil.getModel())) {
                        //setCustomBg(path);
                        try {
                            File file = new Compressor(this)
                                    .setMaxWidth(480)
                                    .setMaxHeight(480)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(FileUtil.getSDPath())
                                    .compressToFile(new File(path));
                            mImgPath = file.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        commonUserPresenter.getOssAliToken();
                    } else {
                        startPhotoZoom(uri);
                    }
                } else {
                    NetProgressObservable.getInstance().hide();
                }
                break;
            case PHOTO_REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    Uri uri = getUriForFile(this, tempFile);
                    String path = BitmapUtils.getRealFilePath(context, uri);
                    if ("Redmi4A".equals(DeviceUtils.getModel())) {
                        Log.e("setCustomBg", tempFile + "");
                        try {
                            File file = new Compressor(this)
                                    .setMaxWidth(480)
                                    .setMaxHeight(480)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(FileUtil.getSDPath())
                                    .compressToFile(new File(path));
                            mImgPath = file.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        commonUserPresenter.getOssAliToken();
                    } else {
                        startPhotoZoom(uri);
                    }


                } else {
                    NetProgressObservable.getInstance().hide();
                }
                break;
            case PHOTO_CUT:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setCustomBg(mImgPath);
                    }
                }).start();
                break;
            default:
        }
    }


    Handler handler = new Handler();

    @Override
    public void successUpgradeImageUrl(String pathUrl) {
        //上传图片到我们的服务器
        Logger.e("ali----" + pathUrl);
        handler.post(new Runnable() {
            @Override
            public void run() {
                upgradeImagePresenter.saveEditBg(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), pathUrl);
            }
        });

    }

    @Override
    public void successGetAliToken(OssBean ossBean) {
        //String imgName = "getFace" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + ".jpg";
        String imgName = "CustomBg" + TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()) + ".jpg";

        //mImgPath 需要判断这个文件是不是存在
        commonUserPresenter.upgradeImageAli(ossBean.getBucketName(), ossBean.getAccessKeyId(), ossBean.getAccessKeySecret(), ossBean.getSecurityToken(), imgName, mImgPath);
    }

    @Override
    public void upgradeProgress(long currentSize, long totalSize) {

    }

    @Override
    public void onFailAliOptin(int type) {

    }

    @Override
    public void successSaveImageUrl(String url) {

        loadBgImage(url);


        // LoadImageUtil.getInstance().loadCirc(EditUserInfo.this, url, ivBg, R.drawable.friend_bg_homepage_defaut);
        //finish();
    }

    @Override
    public void successSaveHeadUrl(UpdatePhotoBean bean) {

    }

    @Override
    public void successOption() {



        finish();

    }

    public void updateData(String headUrl) {
        if (srcUserBean != null) {
            srcUserBean.setHeadUrl(headUrl);
        }
        LoadImageUtil.getInstance().loadCirc(context, headUrl, iv_head);
    }


    public void setValue() {

    }


    public void loadBgImage(String url) {
        if (TextUtils.isEmpty(url)) {
            LoadImageUtil.getInstance().loadCirc(EditUserInfo.this, R.drawable.friend_bg_homepage_defaut, ivBg, DisplayUtils.dip2px(EditUserInfo.this, 20));

        } else {
            LoadImageUtil.getInstance().loadCircs(EditUserInfo.this, url, ivBg, DisplayUtils.dip2px(EditUserInfo.this, 20));
        }
    }

    public void getUserInfo(UserInfoBean details) {
        // srcUserBean = details;
        LoadImageUtil.getInstance().loadCirc(EditUserInfo.this, srcUserBean.getHeadUrl(), iv_head);
        // LoadImageUtil.getInstance().loadCircs(EditUserInfo.this, srcUserBean.getBackgroundUrl(), ivBg, DisplayUtils.dip2px(EditUserInfo.this, 20), R.drawable.friend_bg_homepage_defaut);

        loadBgImage(srcUserBean.getBackgroundUrl());
        // LoadImageUtil.getInstance().loadCirc(EditUserInfo.this, srcUserBean.getBackgroundUrl(), ivBg, R.drawable.friend_bg_homepage_defaut);
        edtName.setText(details.getNickName());
        try {
            edtName.setSelection(StringUtil.isBlank(details.getNickName()) ? 0 : details.getNickName().length());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            itemBirthday.setContentText(details.getBirthday());
            itemWeight.setContentText(CommonDateUtil.formatInterger(Float.valueOf(details.getWeight())) + " kg");
            itemHeight.setContentText(CommonDateUtil.formatInterger(Float.valueOf(details.getHeight())) + " cm");
            if (!TextUtils.isEmpty(details.getMyProfile())) {
                currentCount = details.getMyProfile().length();
                item_individual_resume.setContentText(currentCount + "/200");
                et_myProfile.setText(details.getMyProfile());
            }

            if (details.getGender().equals("Male")) {
                itemSex.setContentText(this.getString(R.string.gender_male));
            } else {
                itemSex.setContentText(this.getString(R.string.gender_female));
            }

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PhotoEventBean messageEvent) {
        Log.e("CustomRepository 111", messageEvent.toString());
        updateData(messageEvent.bean.headUrl);
    }

    @Override
    public void onBackPressed() {
        isBack();
    }


    public void isBack() {
        getGender();
        cheackNickNameChage();
        cheackProfileChage();
        if (isGenderChage || isNickNameChange || isHeightChage || isBirthdayChage || isWeightChange || ismyProfile) {
            PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.not_save_alert), context, getResources().getString(R.string.edit_info_not_save), getResources().getString(R.string.edit_info_save), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    saveUserInfo();
                }

                @Override
                public void cancel() {
                    finish();
                }
            }, false);
        } else {
            finish();
        }
    }


    private String getGender() {
        String gender;
        if (itemSex.getContentText().equals(getString(R.string.gender_male))) {
            gender = JkConfiguration.GymUserInfo.MALE;
        } else if (itemSex.getContentText().equals(getString(R.string.gender_female))) {
            gender = JkConfiguration.GymUserInfo.FEMALE;
        } else {
            gender = JkConfiguration.GymUserInfo.UNSPECIFIED;
        }
        if (srcUserBean != null) {
            if (srcUserBean.getGender().equals(gender)) {
                isGenderChage = false;
            } else {
                isGenderChage = true;
            }
        }

        return gender;
    }

    private void cheackNickNameChage() {
        desUserName = edtName.getText().toString().trim();
        if (srcUserBean != null) {
            if (srcUserBean.getNickName().equals(desUserName)) {
                isNickNameChange = false;
            } else {
                isNickNameChange = true;
            }
        } else {
            isNickNameChange = false;
        }
    }

    private void cheackProfileChage() {
        desPrifile = et_myProfile.getText().toString().trim();
        if (srcUserBean != null) {
            if ((TextUtils.isEmpty(srcUserBean.getMyProfile()) && TextUtils.isEmpty(desPrifile)) || srcUserBean.getMyProfile().equals(desPrifile)) {
                ismyProfile = false;
            } else {
                ismyProfile = true;
            }
        } else {
            ismyProfile = false;
        }
    }

    private void saveUserInfo() {
        String gender = getGender();

        String name = edtName.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            showToast(getString(R.string.friend_enter_nickname));
            return;
        }

        if (name.length() < 2 || name.length() > 18) {
            showToast("请输入2~18个长度内容!");
            return;
        }
        desPrifile = et_myProfile.getText().toString().trim();
        desUserName = edtName.getText().toString().trim();
        if (StringUtil.isBlank(srcUserBean.getNickName())) {
            isNickNameChange = false;
        } else {
            if (srcUserBean.getNickName().equals(desUserName)) {
                isNickNameChange = false;
            } else {
                isNickNameChange = true;
            }
        }
        // cheackNickNameChage();
        Logger.e("itemHeight.getContentText() == " + itemHeight.getContentText());
        upgradeImagePresenter.saveUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()), gender, desUserName, itemHeight.getContentText(), itemWeight.getContentText
                (), itemBirthday.getContentText(), desPrifile);


    }


    /**
     * 要键盘弹出时，scrollView做滑动需，调用此方法
     *
     * @return
     */
    protected void initKeyBoardListener(NestedScrollView scrollView) {
        this.mainScrollView = scrollView;
        this.editTexts = new ArrayList<>();
        findEditText(scrollView);
        setFoucesListener();
    }

    protected void initKeyBoardListener(NestedScrollView scrollView, int offset) {
        this.mainScrollView = scrollView;
        this.editTexts = new ArrayList<>();
        findEditText(scrollView);
        setFoucesListener();
        this.offset = offset;
    }

    //偏移量
    private int offset;
    //当前页面获取了焦点的editText
    private NestedScrollView mainScrollView;
    private Runnable scrollRunnable;
    private boolean normalCanScroll = true;
    private EditText currentFocusEt;
    //当前页面获取了所有的editText
    List<EditText> editTexts;

    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        getContentView().addOnLayoutChangeListener(this);
    }

    public View getContentView() {

        return ((ViewGroup) findViewById(R.id.layout_top)).getChildAt(0);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) { //获取屏幕高度

        Logger.e("onLayoutChange");
        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        int keyHeight = screenHeight / 3;
        if (mainScrollView != null && normalCanScroll) {
            normalCanScroll = mainScrollView.canScrollVertically(1);
            Log.e(TAG, "mainScrollView:canScroll:" + normalCanScroll);
        }

        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起

        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            Log.e(TAG, "弹起");
            if (currentFocusEt != null && currentFocusEt.getId() == edtName.getId()) {

            } else if (currentFocusEt != null) {
                Log.e(TAG, currentFocusEt.getText().toString());
                int[] location = new int[2];
                currentFocusEt.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int height = currentFocusEt.getHeight();
                y = y + height;
                Log.e(TAG, "bottom:" + bottom + " currentFocusEt.bottom:" + y + "height:" + height + "offset:" + offset);
                if (y > bottom && mainScrollView != null) {
                    final int finalY = y;
                    if (normalCanScroll) {
                        scrollRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "finalY - bottom + offset:" + (finalY - bottom + offset));
                                mainScrollView.scrollBy(0, finalY - bottom + offset);
                            }
                        };
                        mainScrollView.postDelayed(scrollRunnable, 100);
                    } else {
                        mainScrollView.scrollBy(0, finalY - bottom + offset);
                    }
                }
            }
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            Log.e(TAG, "收回");
        }
    }

    /**
     * 监听焦点获取当前的获取焦点的editText
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && v instanceof EditText) {
            currentFocusEt = (EditText) v;
        }
    }

    /**
     * 找出当前页面的所有editText
     *
     * @param view
     */
    private void findEditText(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                findEditText(v);
            }
        } else if (view instanceof EditText) {
            editTexts.add((EditText) view);
        }
    }

    /**
     * 当前页面的所有editText设置焦点监听
     */
    private void setFoucesListener() {
        for (EditText et : editTexts) {
            et.setOnFocusChangeListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainScrollView != null) {
            mainScrollView.removeCallbacks(scrollRunnable);
        }
    }

    public String getTimeByyyyyMMddhhmmss(Long time) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        Date date2 = new Date(time);
        return sdFormat.format(date2);
    }

}
