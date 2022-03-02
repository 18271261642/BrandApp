package com.isport.brandapp.device.f18;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.htsmart.wristband2.bean.config.NotificationConfig;
import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_ThridMessageModel;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.entry.F18AppsItemBean;
import com.isport.blelibrary.entry.F18AppsStatusListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.login.ShowPermissionActivity;
import com.isport.brandapp.util.AppSP;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bike.gymproject.viewlibray.ItemDeviceSettingView;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * Created by Admin
 * Date 2022/1/18
 */
public class F18AppsShowActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView {

    private RecyclerView appRecycler;
    //权限
    private ItemDeviceSettingView f18AppsPermissItem;
    //打开通知
    private ItemDeviceSettingView f18AppsOpenNotiItem;
    private ItemDeviceSettingView itemDeviceSettingView;
    private List<F18AppsItemBean> resultList;
    private F18AppsAdapter f18AppsAdapter;

    private F18DeviceSetData mF18DeviceData;

    Bracelet_W311_ThridMessageModel model = new Bracelet_W311_ThridMessageModel();

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(mF18DeviceData != null){
                Log.e(TAG,"-----已经打开的个数="+f18AppsAdapter.getAllSelectSize());
                itemDeviceSettingView.setChecked(f18AppsAdapter.getAllSelectSize()>0);
                mF18DeviceData.setAllAppMsgStatus(f18AppsAdapter.getAllSelectSize()>0);
                mF18DeviceData.setAppMsgs(f18AppsAdapter.getAllSelectSize());
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdInt(F18AppsShowActivity.this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(mF18DeviceData));
            }
        }
    };

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
     //   this.mF18DeviceData = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType, int type, String timeStr) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_apps_show_layout;
    }

    @Override
    protected void initView(View view) {

        findViews();

        titleBarView.setLeftIconEnable(true);
        titleBarView.setTitle(getResources().getString(R.string.app_message_remind));
        titleBarView.setRightText("");

        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {

            }
        });

        resultList = new ArrayList<>();
        resultList.addAll(setInitAppData());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        appRecycler.setLayoutManager(linearLayoutManager);
        appRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        f18AppsAdapter = new F18AppsAdapter(resultList,this);
        appRecycler.setAdapter(f18AppsAdapter);

        f18AppsAdapter.setF18CommItemClickListener(new F18CommItemClickListener() {
            @Override
            public void onF18ItemClick(int position, String normal, boolean isCheck) {
                if(resultList.isEmpty())
                    return;
                resultList.get(position).setChecked(isCheck);
                handler.sendEmptyMessageDelayed(0x00,100);
                Watch7018Manager.getWatch7018Manager().setAppsNotices(resultList.get(position).getFlagCode(),isCheck);
            }
        });

    }

    @Override
    protected void initData() {

        mF18DeviceData = (F18DeviceSetData) getIntent().getSerializableExtra("comm_key");
       // mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdInt(this),Watch7018Manager.getWatch7018Manager().getConnectedMac());
//        if(mF18DeviceData != null){
//            itemDeviceSettingView.setChecked(mF18DeviceData.isAllAppMsgStatus());
//        }
        Watch7018Manager.getWatch7018Manager().readAppsStatus(new F18AppsStatusListener() {
            @Override
            public void backF18AppsStatus(NotificationConfig notificationConfig) {
                setAppsStatus(notificationConfig);
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    protected F18SetPresent createPresenter() {
        return new F18SetPresent(this);
    }

    private void findViews(){
        appRecycler = findViewById(R.id.f18ApsRecyclerView);
        f18AppsPermissItem = findViewById(R.id.f18AppsPermissItem);
        f18AppsOpenNotiItem = findViewById(R.id.f18AppsOpenNotiItem);

        f18AppsOpenNotiItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivityForResult(intent, 0x01);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        f18AppsPermissItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ShowPermissionActivity.class);
            }
        });

        itemDeviceSettingView = findViewById(R.id.f18AppsAllStatusItem);

        itemDeviceSettingView.setOnCheckedChangeListener(new ItemDeviceSettingView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                itemDeviceSettingView.setChecked(isChecked);
                doAllOperate(isChecked);
                if(mF18DeviceData != null){
                    mF18DeviceData.setAllAppMsgStatus(isChecked);
                    mF18DeviceData.setAppMsgs(f18AppsAdapter.getAllSelectSize());
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdInt(F18AppsShowActivity.this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(mF18DeviceData));
                }

            }
        });
    }

    private final List<F18AppsItemBean> allList = new ArrayList<>();
    private void doAllOperate(boolean isAll){
        if(resultList.isEmpty())
            return;
        for(F18AppsItemBean fb : resultList){
            fb.setChecked(isAll);
            allList.add(fb);
            Watch7018Manager.getWatch7018Manager().setAppsNotices(fb.getFlagCode(),isAll);
        }
        resultList.clear();
        resultList.addAll(allList);
        f18AppsAdapter.notifyDataSetChanged();
        allList.clear();

        handler.sendEmptyMessageDelayed(0x00,100);
    }



    private List<F18AppsItemBean> tmpList = new ArrayList<>();

    @SuppressLint("WrongConstant")
    private void setAppsStatus(NotificationConfig notificationConfig){
        tmpList.clear();
        if(resultList.isEmpty())
            return;
        for(F18AppsItemBean fb : resultList){
            fb.setChecked(notificationConfig.isFlagEnable(fb.getFlagCode()));
            tmpList.add(fb);
            if(fb.getFlagCode() == 0){  //电话
                AppSP.putInt(F18AppsShowActivity.this,AppSP.F18_PHONE_ALERT,fb.isChecked() ? 1 : 0);
            }
        }
        resultList.clear();
        resultList.addAll(tmpList);
        f18AppsAdapter.notifyDataSetChanged();
        tmpList.clear();

        int openCount = f18AppsAdapter.getAllSelectSize();

        itemDeviceSettingView.setChecked(openCount > 0);
        if (mF18DeviceData != null) {
            mF18DeviceData.setAllAppMsgStatus(true);
            mF18DeviceData.setAppMsgs(f18AppsAdapter.getAllSelectSize());
            mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdInt(F18AppsShowActivity.this), AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE, new Gson().toJson(mF18DeviceData));
        }

    }

    private List<F18AppsItemBean> setInitAppData(){
        List<F18AppsItemBean> f18AppsItemBeanList = new ArrayList<>();
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_TELEPHONE,getResources().getString(R.string.string_phone_alert),false,R.drawable.icon_f18_phone));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_SMS,getResources().getString(R.string.string_sms_alert),false,R.drawable.icon_f18_sms));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_EMAIL,getResources().getString(R.string.string_mail),false,R.drawable.icon_f18_gmail));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_CALENDAR,getResources().getString(R.string.string_calendar),false,R.drawable.icon_f18_calendar));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_WECHAT,getResources().getString(R.string.wechat),false,R.drawable.icon_f18_wechat));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_QQ,"QQ",false,R.drawable.icon_f18_qq));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_FACEBOOK,"Facebook",false,R.drawable.icon_f18_facebook));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_TWITTER,"Twitter",false,R.drawable.icon_f18_twitter));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_WHATSAPP,"WhatsApp",false,R.drawable.icon_f18_whatsapp));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_LINKEDIN,"LinkedIn",false,R.drawable.icon_f18_linkedin));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_INSTAGRAM,"Instagram",false,R.drawable.icon_f18_instagram));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_FACEBOOK_MESSENGER,"Messenger",false,R.drawable.icon_f18_messenger));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_SNAPCHAT,"Snapchat",false,R.drawable.icon_f18_snapchat));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_LINE,"LINE",false,R.drawable.icon_f18_line));

        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_KAKAO,"Kakao Talk",false,R.drawable.icon_f18_kakao));

        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_VIBER,"Viber",false,R.drawable.icon_f18_viber));

        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_SKYPE,"Skype",false,R.drawable.icon_f18_skype));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_TELEGRAM,"Telegram",false,R.drawable.icon_f18_telegram));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_PINTEREST,"Pinterest",false,R.drawable.icon_f18_pinterest));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_HIKE,"Hike",false,R.drawable.icon_f18_hike));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_YOUTUBE,"YouTube",false,R.drawable.icon_f18_yb));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_APPLE_MUSIC,"Apple Music",false,R.drawable.icon_f18_applemusic));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_ZOOM,"ZOOM",false,R.drawable.icon_f18_zoom));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_TIKTOK,"TikTok",false,R.drawable.icon_f18_tiktok));
        f18AppsItemBeanList.add(new F18AppsItemBean(NotificationConfig.FLAG_OTHERS_APP,getResources().getString(R.string.string_other_txt),false,R.drawable.icon_f18_other));
        return f18AppsItemBeanList;
    }
}
