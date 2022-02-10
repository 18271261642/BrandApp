package com.isport.blelibrary.db.action.f18;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.f18.F18CommonDbBean;
import com.isport.blelibrary.gen.F18CommonDbBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;

/**
 * Created by Admin
 * Date 2022/1/17
 */
public class F18DeviceSetAction {

    private static final String TAG = "F18DeviceSetAction";


    //保存或更新数据，有就更新 无就保存
    public static synchronized void saveOrUpdateF18DeviceSet(String userId,String deviceId,String deviceName,String type,String day,String contentData){
        try {
            if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(contentData))
                return;
            
            F18CommonDbBeanDao f18CommonDbBeanDao = BleAction.getF18CommonDbBeanDao();
            F18CommonDbBean f18CommonDbBean = new F18CommonDbBean();
            f18CommonDbBean.setUserId(userId);
            f18CommonDbBean.setDeviceMac(deviceId);
            f18CommonDbBean.setDeviceName(deviceName);
            f18CommonDbBean.setDbType(type);
            f18CommonDbBean.setTypeDataStr(contentData);
            Log.e(TAG,"-----F18保存数据="+f18CommonDbBean.toString());
            f18CommonDbBeanDao.insertOrReplace(f18CommonDbBean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //保存或更新数据，有就更新 无就保存
    public static synchronized void saveOrUpdateF18DeviceSet(String userId,String deviceMac,String type,String contentData){
        try {
            if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceMac) || TextUtils.isEmpty(contentData))
                return;

            F18CommonDbBeanDao f18CommonDbBeanDao = BleAction.getF18CommonDbBeanDao();
            F18CommonDbBean f18CommonDbBean = new F18CommonDbBean();
            f18CommonDbBean.setUserId(userId);
            f18CommonDbBean.setDeviceMac(deviceMac);
            f18CommonDbBean.setDbType(type);
            f18CommonDbBean.setTypeDataStr(contentData);
            Log.e(TAG,"-----F18保存数据="+f18CommonDbBean.toString());
            F18CommonDbBean fb = querySingleBean(userId,deviceMac,type);
            if(fb != null){
                deleteDbData(fb);
            }
            Long saveDb = f18CommonDbBeanDao.insert(f18CommonDbBean);
            Log.e(TAG,"---保存数据="+saveDb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //查询数据
    public static F18CommonDbBean querySingleBean(String userId,String deviceMac,String type){
        QueryBuilder<F18CommonDbBean> f18CommonDbBeanQueryBuilder = BleAction.getDaoSession().queryBuilder(F18CommonDbBean.class);
        f18CommonDbBeanQueryBuilder.where(F18CommonDbBeanDao.Properties.UserId.eq(userId),F18CommonDbBeanDao.Properties.DeviceMac.eq(deviceMac),F18CommonDbBeanDao.Properties.DbType.eq(type));
        if(f18CommonDbBeanQueryBuilder.list().size()>0){
            ArrayList<F18CommonDbBean> dbList = (ArrayList<F18CommonDbBean>) f18CommonDbBeanQueryBuilder.list();
            Log.e(TAG,"---查询="+new Gson().toJson(dbList));
            return dbList.get(0);
        }
        return null;

    }


    //查询数据,是否已经保存过
    public static ArrayList<F18CommonDbBean> queryListBean(String userId,String deviceMac,String deviceName,String type){
        QueryBuilder<F18CommonDbBean> f18CommonDbBeanQueryBuilder = BleAction.getDaoSession().queryBuilder(F18CommonDbBean.class);
        f18CommonDbBeanQueryBuilder.where(F18CommonDbBeanDao.Properties.UserId.eq(userId),F18CommonDbBeanDao.Properties.DeviceMac.eq(deviceMac),F18CommonDbBeanDao.Properties.DbType.eq(type));
        if(f18CommonDbBeanQueryBuilder.list().size()>0){
            ArrayList<F18CommonDbBean> dbList = (ArrayList<F18CommonDbBean>) f18CommonDbBeanQueryBuilder.list();
           return dbList;
        }
        return null;

    }

    private static void deleteDbData(F18CommonDbBean db){
        F18CommonDbBeanDao f18CommonDbBeanDao = BleAction.getF18CommonDbBeanDao();
        f18CommonDbBeanDao.delete(db);

    }
}
