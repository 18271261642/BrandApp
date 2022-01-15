package com.isport.brandapp.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.isport.blelibrary.managers.Constants;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.MainActivity;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.login.presenter.ActivityDataSettingPresenter;
import com.isport.brandapp.login.view.ActivitySettingUserInfoView;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.ActivityManager;
import brandapp.isport.com.basicres.action.BaseAction;
import brandapp.isport.com.basicres.action.UserInformationBeanAction;
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonbean.UserInfoBean;
import brandapp.isport.com.basicres.commonutil.KeyboardUtils;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.StringUtil;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.commonview.UserDialogSetting;
import brandapp.isport.com.basicres.commonview.UserDialogView;
import brandapp.isport.com.basicres.entry.UserInformationBean;
import brandapp.isport.com.basicres.gen.UserInformationBeanDao;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;
import brandapp.isport.com.basicres.service.observe.BleProgressObservable;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;

//登录后完善用户个人信息
public class ActivitySettingUserInfo extends BaseMVPTitleActivity<ActivitySettingUserInfoView,
        ActivityDataSettingPresenter> implements ActivitySettingUserInfoView, View.OnClickListener, UserDialogView {
    ItemView itemSex, itemWeight, itemHeight, itemBirthday;
    EditText edtName;
    TextView tvNext;
    String desUserName;
    UserDialogSetting userDialogSetting;

    boolean isFromMain, isNickNameChange;

    private UserInfoBean srcUserBean;


    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_setting_user_info;
    }

    @Override
    protected void initView(View view) {
        itemSex = findViewById(R.id.item_date_gender);
        itemWeight = findViewById(R.id.item_date_weight);
        itemHeight = findViewById(R.id.item_date_height);
        itemBirthday = findViewById(R.id.item_date_birth);
        edtName = findViewById(R.id.et_name);
        tvNext = findViewById(R.id.btn_next);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.NEED_LOGIN:
                ToastUtils.showToast(context, UIUtils.getString(com.isport.brandapp.basicres.R.string.login_again));
                NetProgressObservable.getInstance().hide();
                BleProgressObservable.getInstance().hide();
                TokenUtil.getInstance().clear(context);
                UserAcacheUtil.clearAll();
                AppSP.putBoolean(context, AppSP.CAN_RECONNECT, false);
                App.initAppState();
                Intent intent = new Intent(this, ActivityLogin.class);
                startActivity(intent);
                ActivityManager.getInstance().finishAllActivity(ActivityLogin.class.getSimpleName());
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        getIntentData();
        ToastUtil.init(this);

        if (isFromMain) {
            titleBarView.setLeftIconEnable(true);
            srcUserBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(this));
            if (srcUserBean != null) {
                getUserInfo(srcUserBean);
            }
            // TODO: 2019/1/8  getCustomerBasicInfo从网络获取数据，单机版走数据库逻辑
        } else {
            titleBarView.setLeftIconEnable(false);
        }

        titleBarView.setTitle(R.string.set_user_info);
        titleBarView.setRightText("");

        mActPresenter.getCustomerBasicInfo();
    }

    private void getIntentData() {
        //是从主页过来 true
        // 的还是从绑定手机页面过来 false
        isFromMain = getIntent().getBooleanExtra(JkConfiguration.COME_FROM, false);

        //from = getIntent().getStringExtra(JkConfiguration.GymUserInfo.FROM);
        /*if (isFromSetting) {
            titleBarView.setRightTextViewStateIsShow(false);
            titleBarView.setLeftIconEnable(true);
        }*/
        /*if (!TextUtils.isEmpty(from)) {

        }*/
    }

    @Override
    protected void initEvent() {

        itemSex.setOnClickListener(this);
        itemHeight.setOnClickListener(this);
        itemWeight.setOnClickListener(this);
        itemBirthday.setOnClickListener(this);
        tvNext.setOnClickListener(this);

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {

                if (isFromMain) {
                    getGender();
                    cheackNickNameChage();
                    if (isGenderChage || isNickNameChange || isHeightChage || isBirthdayChage || isWeightChange) {
                        PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.not_save_alert), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                            @Override
                            public void determine() {
                                finish();
                            }

                            @Override
                            public void cancel() {

                            }
                        }, false);
                    } else {
                        finish();
                    }

                } else {
                    finish();
                }
            }

            @Override
            public void onRightClicked(View view) {

            }
        });
    }


    @Override
    protected void initHeader() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_date_birth:
                KeyboardUtils.hideKeyboard(v);
                userDialogSetting.setPopupWindow(ActivitySettingUserInfo.this, itemBirthday, JkConfiguration.GymUserInfo
                        .USERBIRTHDAY, itemBirthday.getContentText());
                break;
            case R.id.item_date_gender:
                KeyboardUtils.hideKeyboard(v);
                userDialogSetting.popWindowSelect(ActivitySettingUserInfo.this, itemSex, JkConfiguration.GymUserInfo
                        .GENDER, itemSex.getContentText(), false);
                break;
            case R.id.item_date_height:
                KeyboardUtils.hideKeyboard(v);
                userDialogSetting.popWindowSelect(ActivitySettingUserInfo.this, itemHeight, JkConfiguration.GymUserInfo
                        .HEIGHT, itemHeight.getContentText(), true);
                break;
            case R.id.item_date_weight:
                KeyboardUtils.hideKeyboard(v);
                userDialogSetting.popWindowSelect(ActivitySettingUserInfo.this, itemWeight, JkConfiguration.GymUserInfo
                        .WEIGHT, itemWeight.getContentText(), true);
                break;
            case R.id.btn_next:
                saveUserInfo();
                break;
        }

    }

    boolean isGenderChage;

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

    private void saveUserInfo() {
        String gender = getGender();

        String name = edtName.getText().toString().trim();
        if (name.length() < 2 || name.length() > 18) {
            showToast(getString(R.string.app_nickname_cheak));
            return;
        }
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
        if (App.appType() == App.httpType) {
            if (!checkNet())
                return;
        }
        Logger.myLog("itemHeight.getContentText() == " + itemHeight.getContentText());
        mActPresenter.saveUserBaseicInfo(gender, desUserName, itemHeight.getContentText(), itemWeight.getContentText
                (), itemBirthday.getContentText());
    }

    boolean isHeightChage, isWeightChange, isBirthdayChage;

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
    public void getUserInfo(UserInfoBean details) {
        try {

            srcUserBean = details;
            edtName.setText(details.getNickName());
            edtName.setSelection(StringUtil.isBlank(details.getNickName()) ? 0 : details.getNickName().length());
            itemBirthday.setContentText(details.getBirthday());
            itemWeight.setContentText(CommonDateUtil.formatInterger(Float.valueOf(details.getWeight())) + " kg");
            itemHeight.setContentText(CommonDateUtil.formatInterger(Float.valueOf(details.getHeight())) + " cm");
            if (details.getGender().equals("Male")) {
                itemSex.setContentText(this.getString(R.string.gender_male));
            } else {
                itemSex.setContentText(this.getString(R.string.gender_female));
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void saveUserBaseInfoSuccess() {
        //跳转到主页
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setBirthday(itemBirthday.getContentText());

        userInfoBean.setGender(getGender());
        userInfoBean.setHeight(StringUtil.getNumberStr(itemHeight.getContentText()));
        userInfoBean.setWeight(StringUtil.getNumberStr(itemWeight.getContentText()));
        userInfoBean.setUserId(!(App.appType() == App.httpType) ? Constants.defUserId : TokenUtil.getInstance().getPeopleIdStr(this));
        UserInfoBean loginBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(context));
        if (loginBean != null) {
            userInfoBean.setHeadUrlTiny(loginBean.getHeadUrlTiny());
            userInfoBean.setHeadUrl(loginBean.getHeadUrl());
            userInfoBean.setMobile(loginBean.getMobile());
        }
        AppConfiguration.saveUserInfo(userInfoBean);
        //网络版的数据库更新
        if (App.appType() == App.httpType) {
            UserInformationBean userInfoByUserId = UserInformationBeanAction.findUserInfoByUserId(TokenUtil.getInstance().getPeopleIdInt(this));
            if (userInfoByUserId != null) {
                UserInformationBeanDao userInformationBeanDao = BaseAction.getUserInformationBeanDao();
                userInfoByUserId.setBodyWeight(Float.valueOf(StringUtil.getNumberStr(userInfoBean.getWeight())));
                userInformationBeanDao.update(userInfoByUserId);
            } else {
                //如果为空则存储
            }
        }

        if (isFromMain) {
            //如果消息有修改，就需要重新去
            if (isNickNameChange) {
                setResult(JkConfiguration.resultCode.personInformation, new Intent());
            }
            finish();
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ActivityManager.getInstance().finishAllActivity(MainActivity.class.getSimpleName());
        }

    }

    @Override
    protected ActivityDataSettingPresenter createPresenter() {
        userDialogSetting = new UserDialogSetting(this);
        return new ActivityDataSettingPresenter(this);
    }

    @Override
    public void onRespondError(String message) {

        ToastUtil.showTextToast(message);

    }

    @Override
    public void onBackPressed() {
        if (isFromMain) {
            getGender();
            cheackNickNameChage();
            if (isGenderChage || isNickNameChange || isHeightChage || isBirthdayChage || isWeightChange) {
                PublicAlertDialog.getInstance().showDialog("", context.getResources().getString(R.string.not_save_alert), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                    @Override
                    public void determine() {
                        finish();
                    }

                    @Override
                    public void cancel() {

                    }
                }, false);
            } else {
                finish();
            }
        }
    }

}
