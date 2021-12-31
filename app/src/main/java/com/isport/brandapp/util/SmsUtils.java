package com.isport.brandapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by smh on 2019/4/24.
 */
public class SmsUtils {

    public static List<SmsInfo> getAllSmsInfos(Context context) {



        List<SmsInfo> smsInfos = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "date", "type", "body"}, null, null, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            SmsInfo smsInfo;
            while (cursor.moveToNext()) {
                smsInfo = new SmsInfo();
                smsInfo.setId(cursor.getInt(0));
                smsInfo.setAddress(cursor.getString(1));
                smsInfo.setDate(cursor.getLong(2));
                smsInfo.setType(cursor.getInt(3));    // 设置短信的类型, 接收1还是发送2
                smsInfo.setBody(cursor.getString(4)); // 设置短信的内容
                smsInfos.add(smsInfo);
            }
            cursor.close();
        }
        return smsInfos;
    }

    public static SmsInfo getFirstSmsInfo(Context context) {
        SmsInfo smsInfo = new SmsInfo();
        Uri uri = Uri.parse("content://sms/");

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "person","date", "type", "body",}, null, null, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            if (cursor.moveToFirst()) {

                int idIndex = cursor.getColumnIndex("_id");
                int personIndex = cursor.getColumnIndex("person");
                int phoneNumberColumn = cursor.getColumnIndex("address");  //号码
                int smsbodyColumn = cursor.getColumnIndex("body");  //内容
                int dateColumn = cursor.getColumnIndex("date");  //时间
                int typeColumn = cursor.getColumnIndex("type");  //接收还是发送

//
//                Log.e("TAG","---下标="+idIndex+"\n"+personIndex+"\n"+phoneNumberColumn+"\n"+smsbodyColumn+"\n"+dateColumn+"\n"+typeColumn);

                int idStr = cursor.getInt(idIndex);
                String addStr = cursor.getString(personIndex);
                String phoneStr = cursor.getString(phoneNumberColumn);
                String conStr = cursor.getString(smsbodyColumn);
                long dateStr = cursor.getLong(dateColumn);
                int typeStr = cursor.getInt(typeColumn);

                Uri contactUri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phoneStr);

                Cursor contactCursor = resolver.query(contactUri, new String[]{"display_name"}, null, null, null);

//                Log.e("TAG","----id="+idStr+"\n"+addStr+"\n"+phoneStr+"\n"+conStr+"\n"+dateStr+"\n"+typeStr);


                smsInfo.setId(cursor.getInt(0));
                smsInfo.setAddress(cursor.getString(1));

                smsInfo.setDate(cursor.getLong(3));
                smsInfo.setType(cursor.getInt(4));    // 设置短信的类型, 接收1还是发送2
                smsInfo.setBody(cursor.getString(5)); // 设置短信的内容


                if(contactCursor != null){
                    if (contactCursor.moveToFirst()) {
                        String personName = contactCursor.getString(0);
                        smsInfo.setPerson(personName);
                    }
                    contactCursor.close();
                }


            }
            cursor.close();
        }
        return smsInfo;
    }

}
