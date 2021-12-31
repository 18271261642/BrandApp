package com.isport.brandapp.device.bracelet;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.observe.BleGetStateObservable;
import com.isport.blelibrary.utils.BleRequest;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.bean.DeviceBean;
import com.isport.brandapp.blue.NotificationService;
import com.isport.brandapp.device.bracelet.braceletPresenter.ThridMessagePresenter;
import com.isport.brandapp.device.bracelet.view.ThridMeaageView;
import com.isport.brandapp.util.DeviceTypeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Observable;

import bike.gymproject.viewlibray.ItemView;
import brandapp.isport.com.basicres.BaseApp;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 *魔样消息提醒页面
 */
public class ActivityBandThirdSetting extends BaseMVPTitleActivity<ThridMeaageView, ThridMessagePresenter> implements ThridMeaageView, View.OnClickListener, ItemView.OnItemViewCheckedChangeListener {
    private ItemView iv_message_qq, iv_message_wechat, iv_message_whatsapp, iv_message_facebook, iv_message_twitter, iv_message_skype, iv_message_message, iv_message_linkedin, iv_message_instagram;
    private ItemView iv_message_other, iv_message_kakaotalk, iv_message_line;
    private TextView tv_multiHyperLink;
    private DeviceBean deviceBean;
    private int currentType;


    HashMap<Integer, ItemView> listItems = new HashMap<>();


    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_wristband_thrid_message_setting;
    }

    @Override
    protected void initView(View view) {

        iv_message_qq = view.findViewById(R.id.iv_message_qq);
        iv_message_wechat = view.findViewById(R.id.iv_message_wechat);
        iv_message_whatsapp = view.findViewById(R.id.iv_message_whatsapp);
        iv_message_facebook = view.findViewById(R.id.iv_message_facebook);
        iv_message_twitter = view.findViewById(R.id.iv_message_twitter);
        iv_message_skype = view.findViewById(R.id.iv_message_skype);
        iv_message_message = view.findViewById(R.id.iv_message_message);
        iv_message_linkedin = view.findViewById(R.id.iv_message_linkedin);
        iv_message_instagram = view.findViewById(R.id.iv_message_instagram);
        iv_message_kakaotalk = view.findViewById(R.id.iv_message_kakaotalk);
        iv_message_other = view.findViewById(R.id.iv_message_others);
        iv_message_line = view.findViewById(R.id.iv_message_line);
        tv_multiHyperLink = view.findViewById(R.id.tv_multiHyperLink);


        listItems.put(R.id.iv_message_qq, iv_message_qq);
        listItems.put(R.id.iv_message_wechat, iv_message_wechat);
        listItems.put(R.id.iv_message_whatsapp, iv_message_whatsapp);
        listItems.put(R.id.iv_message_facebook, iv_message_facebook);
        listItems.put(R.id.iv_message_twitter, iv_message_twitter);
        listItems.put(R.id.iv_message_skype, iv_message_skype);
        listItems.put(R.id.iv_message_message, iv_message_message);
        listItems.put(R.id.iv_message_linkedin, iv_message_linkedin);
        listItems.put(R.id.iv_message_instagram, iv_message_instagram);
        listItems.put(R.id.iv_message_kakaotalk, iv_message_kakaotalk);
        listItems.put(R.id.iv_message_others, iv_message_other);
        listItems.put(R.id.iv_message_line, iv_message_line);
    }


    @Override
    protected void initData() {

      /*  if (!NotificationService.isEnabled(this)) {
            Logger.myLog("没有打开权限，要去打开权限");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {

        }*/
        tv_multiHyperLink.setText(getClickableSpan());
        tv_multiHyperLink.setMovementMethod(LinkMovementMethod.getInstance());

        getIntentData();


        showDeviceSurrportItem(currentType);
        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(context.getResources().getString(R.string.app_message_remind));
        titleBarView.setRightText("");
        frameBodyLine.setVisibility(View.VISIBLE);


        mActPresenter.getAllThridMessageItem(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);
    }


    private SpannableString getClickableSpan() {


        String connent = UIUtils.getString(R.string.thride_tips);
        String linkConnent = UIUtils.getString(R.string.thride_tips_link);
        int index = connent.indexOf(linkConnent);
        if (index >= 0 && index < connent.length()) ;
        SpannableString spannableString = new SpannableString(UIUtils.getString(R.string.thride_tips));


//设置下划线文字

        spannableString.setSpan(new UnderlineSpan(), index, index + linkConnent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//设置文字的单击事件

        spannableString.setSpan(new ClickableSpan() {

            @Override

            public void onClick(View widget) {

                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);

            }

        }, index, index + linkConnent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//设置文字的前景色

        spannableString.setSpan(new ForegroundColorSpan(UIUtils.getColor(R.color.common_view_color)), index, index + linkConnent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        return spannableString;

    }

    public void showDeviceSurrportItem(int currentType) {
        iv_message_qq.setVisibility(View.GONE);
        iv_message_wechat.setVisibility(View.GONE);
        iv_message_whatsapp.setVisibility(View.GONE);
        iv_message_facebook.setVisibility(View.GONE);
        iv_message_twitter.setVisibility(View.GONE);
        iv_message_skype.setVisibility(View.GONE);
        iv_message_message.setVisibility(View.GONE);
        iv_message_linkedin.setVisibility(View.GONE);
        iv_message_instagram.setVisibility(View.GONE);
        iv_message_kakaotalk.setVisibility(View.GONE);
        iv_message_other.setVisibility(View.GONE);
        iv_message_line.setVisibility(View.GONE);
        if (DeviceTypeUtil.isContaintW81(currentType)) {
            iv_message_qq.setVisibility(View.VISIBLE);
            iv_message_wechat.setVisibility(View.VISIBLE);
            iv_message_whatsapp.setVisibility(View.VISIBLE);
            iv_message_facebook.setVisibility(View.VISIBLE);
            iv_message_twitter.setVisibility(View.VISIBLE);
            iv_message_skype.setVisibility(View.VISIBLE);
            // iv_message_message.setVisibility(View.VISIBLE);
            iv_message_instagram.setVisibility(View.VISIBLE);
            // iv_message_kakaotalk.setVisibility(View.VISIBLE);
            iv_message_other.setVisibility(View.VISIBLE);
            iv_message_line.setVisibility(View.VISIBLE);

        } else if (currentType == JkConfiguration.DeviceType.BRAND_W311) {
            iv_message_qq.setVisibility(View.VISIBLE);
            iv_message_wechat.setVisibility(View.VISIBLE);
            iv_message_whatsapp.setVisibility(View.VISIBLE);
            iv_message_facebook.setVisibility(View.VISIBLE);
            iv_message_twitter.setVisibility(View.VISIBLE);
            iv_message_skype.setVisibility(View.VISIBLE);
            iv_message_message.setVisibility(View.VISIBLE);

        } else if (DeviceTypeUtil.isW520X(currentType)) {
            iv_message_qq.setVisibility(View.VISIBLE);
            iv_message_wechat.setVisibility(View.VISIBLE);
            iv_message_whatsapp.setVisibility(View.VISIBLE);
            iv_message_facebook.setVisibility(View.VISIBLE);
            iv_message_twitter.setVisibility(View.VISIBLE);
            iv_message_skype.setVisibility(View.VISIBLE);
            iv_message_message.setVisibility(View.VISIBLE);
            iv_message_instagram.setVisibility(View.VISIBLE);
            iv_message_linkedin.setVisibility(View.VISIBLE);
        }
    }

    public void isShowItem(ItemView itemView, boolean isOpen) {

        if (itemView.getVisibility() == View.VISIBLE) {
            itemView.setChecked(isOpen);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);
        if (o instanceof BleGetStateObservable) {
        }
    }

    private void getIntentData() {
        deviceBean = (DeviceBean) getIntent().getSerializableExtra(JkConfiguration.DEVICE);
        if (deviceBean != null) {
            currentType = deviceBean.currentType;
        } else {
            currentType = JkConfiguration.DeviceType.BRAND_W311;
        }
    }


    @Override
    protected void initEvent() {
        iv_message_skype.setOnCheckedChangeListener(this);
        iv_message_qq.setOnCheckedChangeListener(this);
        iv_message_wechat.setOnCheckedChangeListener(this);
        iv_message_whatsapp.setOnCheckedChangeListener(this);
        iv_message_facebook.setOnCheckedChangeListener(this);
        iv_message_twitter.setOnCheckedChangeListener(this);
        iv_message_message.setOnCheckedChangeListener(this);
        iv_message_instagram.setOnCheckedChangeListener(this);
        iv_message_kakaotalk.setOnCheckedChangeListener(this);
        iv_message_other.setOnCheckedChangeListener(this);
        iv_message_linkedin.setOnCheckedChangeListener(this);
        iv_message_line.setOnCheckedChangeListener(this);
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
    protected void initHeader() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onCheckedChanged(int id, boolean isChecked) {
        if (!NotificationService.isEnabled(this)) {
            ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.location_permissions));
            if (listItems.containsKey(id)) {
                listItems.get(id).setChecked(!isChecked);
            }
            return;
        }
        if (model == null) {
            return;
        }
        switch (id) {
            case R.id.iv_message_qq:
                Log.e("iv_display_cal", "开启" + isChecked);
                model.setIsQQ(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "qq" + ": test", CRPBleMessageType.MESSAGE_QQ);

                break;
            case R.id.iv_message_wechat:
                model.setIsWechat(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "wechat" + ": test", CRPBleMessageType.MESSAGE_WECHAT);
                break;
            case R.id.iv_message_facebook:
                model.setIsFaceBook(isChecked);
                //  ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "FACEBOOK" + ": test", CRPBleMessageType.MESSAGE_WHATSAPP);
                break;
            case R.id.iv_message_twitter:
                model.setIsTwitter(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "TWITTER" + ": test", CRPBleMessageType.MESSAGE_WECHAT_IN);
                break;
            case R.id.iv_message_skype:
                model.setIsSkype(isChecked);
                //  ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "SKYPE" + ": test", CRPBleMessageType.MESSAGE_SKYPE);
                break;
            case R.id.iv_message_whatsapp:
                model.setIsWhatApp(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "MESSAGE_WHATSAPP" + ": test", CRPBleMessageType.MESSAGE_KAKAOTALK);
                break;
            case R.id.iv_message_message:
                model.setIsMessage(isChecked);
                break;
            case R.id.iv_message_instagram:
                model.setIsInstagram(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "INSTAGREM" + ": test", CRPBleMessageType.MESSAGE_INSTAGREM);
                break;
            case R.id.iv_message_linkedin:
                model.setIsLinkedin(isChecked);
                break;
            case R.id.iv_message_others:
                //这里需要跟设备去通信
                if (!AppConfiguration.isConnected) {
                    ToastUtil.showTextToast(context, UIUtils.getString(R.string.app_disconnect_device));
                    if (listItems.containsKey(id)) {
                        listItems.get(id).setChecked(!isChecked);
                    }
                    return;
                }
                ISportAgent.getInstance().requestBle(BleRequest.DEVICE_OHTER_MESSAGE, isChecked);

                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "MESSAGE_OTHER" + ": test", CRPBleMessageType.MESSAGE_OTHER);
                model.setIsOthers(isChecked);
                break;
            case R.id.iv_message_kakaotalk:
                model.setIskakaotalk(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "MESSAGE_KAKAOTALK" + ": test", CRPBleMessageType.MESSAGE_KAKAOTALK);

                break;
            case R.id.iv_message_line:
                model.setIsLine(isChecked);
                // ISportAgent.getInstance().requestBle(BleRequest.w81_send_message, "MESSAGE_LINE" + ": test", CRPBleMessageType.MESSAGE_LINE);
                break;
        }
        mActPresenter.saveThridMessageItem(model);

    }

    @Override
    protected ThridMessagePresenter createPresenter() {
        return new ThridMessagePresenter(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            default:
                break;
        }
    }

    Bracelet_W311_ThridMessageModel model;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleGetStateObservable.getInstance().deleteObserver(this);
    }

    public void onBackPressed() {
        finish();
    }


    @Override
    public void successThridMessageItem(Bracelet_W311_ThridMessageModel bracelet_w311_displayModel) {
        model = bracelet_w311_displayModel;
        isShowItem(iv_message_skype, bracelet_w311_displayModel.getIsSkype());
        isShowItem(iv_message_wechat, bracelet_w311_displayModel.getIsWechat());
        isShowItem(iv_message_qq, bracelet_w311_displayModel.getIsQQ());
        isShowItem(iv_message_message, bracelet_w311_displayModel.getIsMessage());
        isShowItem(iv_message_linkedin, bracelet_w311_displayModel.getIsLinkedin());
        isShowItem(iv_message_instagram, bracelet_w311_displayModel.getIsInstagram());
        isShowItem(iv_message_facebook, bracelet_w311_displayModel.getIsFaceBook());
        isShowItem(iv_message_twitter, bracelet_w311_displayModel.getIsTwitter());
        isShowItem(iv_message_whatsapp, bracelet_w311_displayModel.getIsWhatApp());
        isShowItem(iv_message_kakaotalk, bracelet_w311_displayModel.getIskakaotalk());
        isShowItem(iv_message_other, bracelet_w311_displayModel.getIsOthers());
        isShowItem(iv_message_line, bracelet_w311_displayModel.getIsLine());
    }

    @Override
    public void successThridMessageItem() {
        mActPresenter.getAllThridMessageItem(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), AppConfiguration.braceletID);

    }
}
