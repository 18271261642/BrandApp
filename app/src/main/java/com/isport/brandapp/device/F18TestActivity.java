package com.isport.brandapp.device;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htsmart.wristband2.bean.data.SleepData;
import com.htsmart.wristband2.bean.data.SleepItemData;
import com.isport.blelibrary.db.action.W81Device.W81DeviceDataAction;
import com.isport.blelibrary.db.action.f18.F18DeviceSetAction;
import com.isport.blelibrary.db.table.f18.F18CommonDbBean;
import com.isport.blelibrary.db.table.f18.F18DbType;
import com.isport.blelibrary.db.table.f18.F18DetailStepBean;
import com.isport.blelibrary.db.table.w811w814.W81DeviceDetailData;
import com.isport.blelibrary.utils.CommonDateUtil;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.device.W81Device.IW81DeviceDataModel;
import com.isport.brandapp.device.W81Device.W81DeviceDataModelImp;
import com.isport.brandapp.device.f18.F18HomePresenter;
import com.isport.brandapp.device.watch.bean.WatchInsertBean;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.List;

import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.BaseTitleActivity;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonview.TitleBarView;

/**
 * Created by Admin
 * Date 2022/3/24
 */
public class F18TestActivity extends BaseTitleActivity implements View.OnClickListener {

    private static final String TAG = "F18TestActivity";

    private TextView showStTv;

    private Button findF18SourceSleepBtn;

    private Button allNoUpdateBtn;

    private Button findDbBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_f18_test_layout;
    }

    @Override
    protected void initView(View view) {
        findViews();
        showStTv = findViewById(R.id.showStTv);
        findViewById(R.id.f18TestTodayBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // W81DeviceDetailData w81DeviceDetailData = new W81DeviceDataAction().getW81DeviceDetialData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdStr(F18TestActivity.this), DateUtil.getCurrDay());

               List<F18DetailStepBean> allList =  F18DeviceSetAction.getF18DetailList(TokenUtil.getInstance().getPeopleIdStr(F18TestActivity.this),AppConfiguration.braceletID);
                if(allList == null || allList.isEmpty())
                    return;
                Log.e(TAG,"------所有数据="+new Gson().toJson(allList));
                StringBuilder stringBuilder = new StringBuilder();
                for(F18DetailStepBean fb : allList){
                    stringBuilder.append(fb.getDayStr()+"  "+DateUtil.getFormatTime(fb.getTimeLong())+" "+fb.toString()+"\n\n");
                }

                showStTv.setText(stringBuilder.toString());
            }
        });


        findViewById(R.id.testDealDataBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }




    private void findViews(){

        findDbBtn = findViewById(R.id.findDbBtn);
        findF18SourceSleepBtn = findViewById(R.id.findF18SourceSleepBtn);
        allNoUpdateBtn = findViewById(R.id.allNoUpdateBtn);

        findF18SourceSleepBtn.setOnClickListener(this);
        allNoUpdateBtn.setOnClickListener(this);
        findDbBtn.setOnClickListener(this);

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
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.findF18SourceSleepBtn){
            findF18Sleep();
        }

        if(v.getId() == R.id.allNoUpdateBtn){
            getAllNoUpdate();
        }

        if(v.getId() == R.id.findDbBtn){
            finDbData("2022-05-12");
        }

    }


    private void finDbData(String dayStr){


        W81DeviceDetailData w81DeviceDetailData = new W81DeviceDataAction().getW81DeviceDetialData(AppConfiguration.braceletID,TokenUtil.getInstance().getPeopleIdStr(this),dayStr);


        showStTv.setText("日期="+dayStr+" "+new Gson().toJson(w81DeviceDetailData));
    }


    private void getAllNoUpdate(){

        List<WatchInsertBean> watchInsertBeanList = new W81DeviceDataModelImp().getAllNoUpgradeW81DeviceDetailData(AppConfiguration.braceletID,TokenUtil.getInstance().getPeopleIdStr(this),false);

        Log.e(TAG,"所有未上传数据="+new Gson().toJson(watchInsertBeanList));
        showStTv.setText(new Gson().toJson(watchInsertBeanList));

    }

    private StringBuilder stringBuilder = new StringBuilder();


    private void findF18Sleep(){
        stringBuilder.delete(0,stringBuilder.length());
        ArrayList<F18CommonDbBean> f18CommonDbBeanLt = F18DeviceSetAction.queryListBean(TokenUtil.getInstance().getPeopleIdStr(this),AppConfiguration.braceletID, "",F18DbType.F18_DEVICE_SLEEP_TYPE);

        showStTv.setText(new Gson().toJson(f18CommonDbBeanLt));
        if(f18CommonDbBeanLt == null || f18CommonDbBeanLt.isEmpty())
            return;
        Log.e(TAG,"-----睡眠原始数据="+new Gson().toJson(f18CommonDbBeanLt));
        stringBuilder.append("原始数据="+new Gson().toJson(f18CommonDbBeanLt)+"\n");
        List<SleepItemData> f18SleepList = new ArrayList<>();
        for(F18CommonDbBean f18CommonDbBean : f18CommonDbBeanLt){
            String tmpStr = f18CommonDbBean.getTypeDataStr();
            if(tmpStr == null)
                continue;

            Log.e(TAG,"------睡眠单个item原始数据="+tmpStr +" "+f18CommonDbBean.toString());

            List<SleepData> sleepDataList = new Gson().fromJson(tmpStr,new TypeToken<List<SleepData>>(){}.getType());

            if(sleepDataList != null && !sleepDataList.isEmpty()){
                for(SleepData sleepData : sleepDataList){
                    if(DateUtil.getFormatTime(sleepData.getTimeStamp(),"yyyy-MM-dd").equals(DateUtil.getCurrDay())){
                        f18SleepList.addAll(sleepData.getItems());
                    }
                }

            }
        }

        if(f18SleepList.isEmpty())
            return;
        analysSleep(f18SleepList);

//        String str = f18CommonDbBeanLt.get(1).getTypeDataStr();
//
//        List<SleepData> sleepDataList = new Gson().fromJson(str,new TypeToken<List<SleepData>>(){}.getType());
//
//        analysSleep(sleepDataList);
    }


    private void analysSleep(List<SleepItemData> lt){
        List<SleepItemData> f18SleepList = lt;

        final ArrayList<ArrayList<String>> sleepDetail = new ArrayList<>();

        //总的睡眠时间
       // long countSleep = f18SleepList.get(f18SleepList.size()-1).g.getTimeStamp();

        int len = f18SleepList.size();

        //深睡
        int deepSleepTime = 0;
        //浅睡
        int lightSleepTime = 0;
        //清醒
        int soberTime = 0;

        for (int i = 0; i < len; i++) {
            ArrayList<String> itemSleeep = new ArrayList<>();
            SleepItemData sleepData = f18SleepList.get(i);
            int sleepStatus = sleepData.getStatus();
            long startTime = sleepData.getStartTime();
            long endTime = sleepData.getEndTime();
            //间隔
            long intervalTime = endTime-startTime;
            //分钟
            int intervalMinute = (int) (intervalTime / 1000 /60);

            if(changeSleepStatus(sleepStatus) == 1){   //深睡  传后台是清醒
                // deepSleepTime +=intervalMinute;
                soberTime += intervalMinute;
            }

            if(changeSleepStatus(sleepStatus) == 2){  //浅睡
                lightSleepTime +=intervalMinute;
            }

            if(changeSleepStatus(sleepStatus) == 3){   //清醒 传后台是深睡
                // soberTime += intervalMinute;
                deepSleepTime +=intervalMinute;
            }

            //上传后台，1清醒；2浅睡；3深睡

            itemSleeep.add(changeSleepStatus(sleepData.getStatus()) + "");
            itemSleeep.add(CommonDateUtil.getTimeFromLong(startTime) + "");
            itemSleeep.add(CommonDateUtil.getTimeFromLong(endTime) + "");
            itemSleeep.add(intervalMinute + "");

            Log.e(TAG,"-----睡眠item="+new Gson().toJson(itemSleeep));
            sleepDetail.add(itemSleeep);
        }

        String resultStr = "深睡:"+deepSleepTime +"\n"+"浅睡:"+ lightSleepTime + "\n"+"清醒:"+soberTime+"\n"+new Gson().toJson(sleepDetail);
        stringBuilder.append("解析后的睡眠="+resultStr+"\n");
        Log.e(TAG,"-----保存睡眠="+deepSleepTime +" "+ lightSleepTime + " "+soberTime+" "+new Gson().toJson(sleepDetail));
        showStTv.setText(stringBuilder.toString());
    }


    private int changeSleepStatus(int status){
        if(status == 1)
            return 3;
        if(status == 2)
            return 2;
        if(status == 3)
            return 1;
        return 0;
    }
}
