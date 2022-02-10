package com.isport.brandapp.device.f18;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.htsmart.wristband2.bean.WristbandContacts;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DeviceSetData;
import com.isport.blelibrary.entry.F18ContactListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.model.F18ContactBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class F18ContactActivity extends BaseMVPTitleActivity<F18SetView,F18SetPresent> implements F18SetView {


    private F18DeviceSetData contactSetBean;

    private F18ContactAdapter f18ContactAdapter;
    private List<F18ContactBean> beanList;

    private AlertDialog.Builder alert;

    @Override
    public void backAllSetData(F18DeviceSetData f18DeviceSetData) {
        this.contactSetBean = f18DeviceSetData;
    }

    @Override
    public void backSelectDateStr(int selectType, int type, String timeStr) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_contact_layout;
    }

    @Override
    protected void initView(View view) {

        titleBarView.setTitle("常用联系人");
        titleBarView.setRightText("");
        titleBarView.setRightIcon(R.drawable.icon_add_device);
        frameBodyLine.setVisibility(View.VISIBLE);

        findViews();
    }

    @Override
    protected void initData() {
        beanList.clear();
        mActPresenter.getAllDeviceSet(TokenUtil.getInstance().getPeopleIdStr(this), AppConfiguration.braceletID);
        Watch7018Manager.getWatch7018Manager().redDeviceContact(new F18ContactListener() {
            @Override
            public void onContactAllData(List<WristbandContacts> list) {
                if(list.isEmpty())
                    return;

                for(WristbandContacts wb : list){
                    beanList.add(new F18ContactBean(wb.getName(),wb.getNumber()));
                }

                f18ContactAdapter.notifyDataSetChanged();

                if(contactSetBean != null){
                    contactSetBean.setContactNumber(f18ContactAdapter.getItemCount());
                    mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(F18ContactActivity.this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(contactSetBean));
                }
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
        RecyclerView recyclerView = findViewById(R.id.f18ContactRy);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        beanList = new ArrayList<>();
        f18ContactAdapter = new F18ContactAdapter(this,beanList);
        recyclerView.setAdapter(f18ContactAdapter);


        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onLeftClicked(View view) {
                finish();
            }

            @Override
            public void onRightClicked(View view) {
                getContact();
            }
        });


        f18ContactAdapter.setOnF18LongClickListener(new OnF18LongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                alertDelete(position);
            }
        });

    }



    private void getContact(){
        if(XXPermissions.isGranted(this,Manifest.permission.READ_CONTACTS)){
            Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

            startActivityForResult(intent,1000);
            return;
        }
        XXPermissions.with(this).permission(Manifest.permission.READ_CONTACTS).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }
        });
    }

    private final List<WristbandContacts> wristbandContactsList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){
            if(data == null)
                return;
            Uri uri = data.getData();
            String[] cons = getPhoneContacts(uri);
            if(cons == null)
                return;
            Log.e(TAG,"----选择="+cons[0]+" "+cons[1]);
            if(beanList.size()>=10){
                Toast.makeText(this, "最多支持10条!", Toast.LENGTH_SHORT).show();
                return;
            }
            beanList.add(new F18ContactBean(cons[0],cons[1]));
            f18ContactAdapter.notifyDataSetChanged();
            wristbandContactsList.clear();
            for(F18ContactBean fb : beanList){
                wristbandContactsList.add(new WristbandContacts(fb.getContactName(), fb.getContactNumber()));
            }
            if(contactSetBean != null){
                contactSetBean.setContactNumber(f18ContactAdapter.getItemCount());
                mActPresenter.saveAllSetData(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID, F18DbType.F18_DEVICE_SET_TYPE,new Gson().toJson(contactSetBean));
            }
            Watch7018Manager.getWatch7018Manager().setDeviceContact(wristbandContactsList);

        }
    }

    /**
     * 读取联系人信息
     * @param uri
     */
    private String[] getPhoneContacts(Uri uri){
        try {
            String[] contact = new String[2];
            //得到ContentResolver对象
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null&&cursor.moveToFirst()) {
                //取得联系人姓名
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact[0] = cursor.getString(nameFieldColumnIndex);
                contact[1]=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("contacts",contact[0]);
                Log.i("contactsUsername",contact[1]);
                cursor.close();
            } else {
                return null;
            }
            return contact;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    private void alertDelete(int position){
        alert = new AlertDialog.Builder(this)
                .setTitle("提醒")
                .setMessage("是否删除?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        beanList.remove(position);
                        f18ContactAdapter.notifyDataSetChanged();
                        wristbandContactsList.clear();
                        for(F18ContactBean fb : beanList){
                            wristbandContactsList.add(new WristbandContacts(fb.getContactName(), fb.getContactNumber()));
                        }
                        Watch7018Manager.getWatch7018Manager().setDeviceContact(wristbandContactsList);

                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
      //  alert.create().show();

        AlertDialog at = alert.create();
        at.show();

        Button button = at.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(Color.BLACK);

        Button cancelBtn = at.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setTextColor(Color.BLACK);

    }
}
