package com.isport.blelibrary.db.action.f18;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.isport.blelibrary.db.action.BleAction;
import com.isport.blelibrary.db.table.f18.F18CommonDbBean;
import com.isport.blelibrary.db.table.f18.F18DetailStepBean;
import com.isport.blelibrary.db.table.f18.F18StepBean;
import com.isport.blelibrary.gen.F18CommonDbBeanDao;
import com.isport.blelibrary.gen.F18DetailStepBeanDao;
import com.isport.blelibrary.utils.DateUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2022/1/17
 */
public class F18DeviceSetAction {

    private static final String TAG = "F18DeviceSetAction";


    //保存手表返回的计步数据，手表同步数据后返回数据，返回数据后即情况手表中的数据，再次同步后不再返回
    public static synchronized void saveF18DeviceDetailStep(String userId, String deviceId,String day,long time, F18StepBean f18StepBean,int status){

        if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(day))
            return;

        F18DetailStepBeanDao f18DetailStepBeanDao = BleAction.getF18DetailStepBeanDao();

        F18DetailStepBean f18DetailStepBean = new F18DetailStepBean();
        f18DetailStepBean.setUserId(userId);
        f18DetailStepBean.setDeviceTypeId(deviceId);
        f18DetailStepBean.setDayStr(day);
        f18DetailStepBean.setTimeLong(time);
        f18DetailStepBean.setStep(f18StepBean.getStep());
        f18DetailStepBean.setDistance(f18StepBean.getDistance());
        f18DetailStepBean.setKcal(f18StepBean.getKcal());
        f18DetailStepBean.setStatus(0);
        Log.e(TAG,"-------保存详细计步到数据库="+f18DetailStepBean.toString());
        f18DetailStepBeanDao.insertOrReplace(f18DetailStepBean);

    }


    //查询数据
    public static List<F18DetailStepBean> getF18DetailList(String userId,String deviceId,String day){
        QueryBuilder<F18DetailStepBean> queryBuilder = BleAction.getDaoSession().queryBuilder(F18DetailStepBean.class);
        queryBuilder.where(F18DetailStepBeanDao.Properties.UserId.eq(userId),F18DetailStepBeanDao.Properties.DeviceTypeId.eq(deviceId),F18DetailStepBeanDao.Properties.DayStr.eq(day));
        if(queryBuilder.list().size()>0){
           List<F18DetailStepBean> f18DetailStepBeanList = queryBuilder.list();
           return f18DetailStepBeanList;
        }
        return null;
    }

    //查询数据
    public static List<F18DetailStepBean> getF18DetailList(String userId,String deviceId,String day,int status){
        QueryBuilder<F18DetailStepBean> queryBuilder = BleAction.getDaoSession().queryBuilder(F18DetailStepBean.class);
        queryBuilder.where(F18DetailStepBeanDao.Properties.UserId.eq(userId),F18DetailStepBeanDao.Properties.DeviceTypeId.eq(deviceId),F18DetailStepBeanDao.Properties.DayStr.eq(day),F18DetailStepBeanDao.Properties.Status.eq(status));
        if(queryBuilder.list().size()>0){
            List<F18DetailStepBean> f18DetailStepBeanList = queryBuilder.list();
            return f18DetailStepBeanList;
        }
        return null;
    }



    //查询数据
    public static List<F18DetailStepBean> getF18DetailList(String userId,String deviceId){
        QueryBuilder<F18DetailStepBean> queryBuilder = BleAction.getDaoSession().queryBuilder(F18DetailStepBean.class);
        queryBuilder.where(F18DetailStepBeanDao.Properties.UserId.eq(userId),F18DetailStepBeanDao.Properties.DeviceTypeId.eq(deviceId));
        if(queryBuilder.list().size()>0){
            List<F18DetailStepBean> f18DetailStepBeanList = queryBuilder.list();
            return f18DetailStepBeanList;
        }
        return null;
    }


    public static long deleteF18DetailStepBean(String userId,String deviceId,String day){
        F18DetailStepBeanDao f18DetailStepBeanDao = BleAction.getF18DetailStepBeanDao();
        List<F18DetailStepBean> delList = getF18DetailList(userId,deviceId,day);
        if(delList != null){
            for(F18DetailStepBean fb : delList){
                f18DetailStepBeanDao.delete(fb);
            }
        }
        return 0;
    }



    //更改状态0未上传，1上传
    public static void  updateF18DetailStep(String userId,String deviceId,String day){
        List<F18DetailStepBean> uL = getF18DetailList(userId,deviceId, day);
        if(uL == null)
            return ;
        F18DetailStepBeanDao f18DetailStepBeanDao = BleAction.getF18DetailStepBeanDao();
        for(F18DetailStepBean fb :uL){
            fb.setStatus(1);
            f18DetailStepBeanDao.update(fb);
        }

    }



    //保存或更新数据，有就更新 无就保存
    public static synchronized void saveOrUpdateF18DeviceSet(String userId,String deviceId,String deviceName,String type,String day,String contentData){
        try {
            if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(contentData))
                return;
            
            F18CommonDbBeanDao f18CommonDbBeanDao = BleAction.getF18CommonDbBeanDao();
            F18CommonDbBean f18CommonDbBean = new F18CommonDbBean();
            f18CommonDbBean.setUserId(userId);
            f18CommonDbBean.setDeviceMac(deviceId);
            f18CommonDbBean.setDateStr(day);
            f18CommonDbBean.setDeviceName(deviceName);
            f18CommonDbBean.setDbType(type);
            f18CommonDbBean.setTypeDataStr(contentData);
            Log.e(TAG,"-----F18保存数据="+f18CommonDbBean.toString());
            f18CommonDbBeanDao.insert(f18CommonDbBean);
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
            Long saveDb = f18CommonDbBeanDao.insertOrReplace(f18CommonDbBean);
            Log.e(TAG,"---保存数据="+saveDb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //查询数据
    public static F18CommonDbBean querySingleBean(String userId,String deviceMac,String type){
        Log.e(TAG,"------查询是存在="+userId+" "+deviceMac +" "+type);
        QueryBuilder<F18CommonDbBean> f18CommonDbBeanQueryBuilder = BleAction.getDaoSession().queryBuilder(F18CommonDbBean.class);
        f18CommonDbBeanQueryBuilder.where(F18CommonDbBeanDao.Properties.UserId.eq(userId),F18CommonDbBeanDao.Properties.DeviceMac.eq(deviceMac),F18CommonDbBeanDao.Properties.DbType.eq(type));
        if(f18CommonDbBeanQueryBuilder.list() == null)
            return null;
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


    //查询数据,是否已经保存过
    public static ArrayList<F18CommonDbBean> queryListBean(String userId,String deviceMac,String deviceName,String type,String day){
        QueryBuilder<F18CommonDbBean> f18CommonDbBeanQueryBuilder = BleAction.getDaoSession().queryBuilder(F18CommonDbBean.class);
        f18CommonDbBeanQueryBuilder.where(F18CommonDbBeanDao.Properties.UserId.eq(userId),F18CommonDbBeanDao.Properties.DeviceMac.eq(deviceMac),F18CommonDbBeanDao.Properties.DbType.eq(type),F18CommonDbBeanDao.Properties.DateStr.eq(day));
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

    private static void updateDbData(F18CommonDbBean f18CommonDbBean){
        F18CommonDbBeanDao f18CommonDbBeanDao = BleAction.getF18CommonDbBeanDao();
        f18CommonDbBeanDao.update(f18CommonDbBean);
    }
}
