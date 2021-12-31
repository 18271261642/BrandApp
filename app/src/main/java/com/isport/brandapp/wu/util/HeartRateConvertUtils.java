package com.isport.brandapp.wu.util;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import com.isport.brandapp.R;

import java.util.ArrayList;

import bike.gymproject.viewlibray.bean.HrlineBean;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.commonres.commonutil.Arith;

/*
 *
 *
 * @author mhj
 * Create at 2018/4/20 10:26
 */
public class HeartRateConvertUtils {

    private static final String TAG = "HeartRateConvertUtils";

    //小数点后保留几位
    private static int scale = 2;

    //男士卡路里  time 单位分钟
    public static double hearRate2CaloriForMan(int heartRate, int age, int weight, int time, String util) {
        /**
         * 连上的时间和当前时间段
         *
         */
        double ageD = Arith.mul(age, 0.2017);
        double weightD = Arith.mul(weight, 0.1988);
        double heartRateD = Arith.mul(heartRate, 0.6309);

        double Calori = 0.0000000;

       /* if (Constant.UNIT_MILLS.equals(util)) {
//            Calori = (age * 0.2017 + weight * 0.1988 + heartRate * 0.6309 - 55.0969) * time*1.0 / 60 / 4.184;
            Calori = Arith.round(Arith.div(Arith.div(Arith.mul(Arith.sub(Arith.add(ageD, Arith.add(weightD, heartRateD)), 55.0969), time), 60), 4.184), scale);
        } else {
//            Calori = (age * 0.2017 + weight * 0.1988 + heartRate * 0.6309 - 55.0969) * time / 4.184;
        }*/
        Calori = Arith.round(Arith.div(Arith.mul(Arith.sub(Arith.add(ageD, Arith.add(weightD, heartRateD)), 55.0969), time), 4.184), scale);


//        double  one = Arith.add(weightD,heartRateD);
//        double two = Arith.add(ageD,one);
//        double three = Arith.sub(two,55.0969);
//        double four = Arith.mul(three,time);
//        double five = Arith.div(four,60);
//        double six = Arith.div(five,4.184);
//
//        Calori = Arith.round(six,scale);
        if (Calori < 0) {
            Calori = 0;
        }
        return Calori;
    }

    //女士卡路里
    public static double hearRate2CaloriForWoman(int heartRate, int age, int weight, int time, String util) {

        double ageD = Arith.mul(age, 0.074);
        double weightD = Arith.mul(weight, 0.1263);
        double heartRateD = Arith.mul(heartRate, 0.4472);

        double Calori = 0.0;
        Calori = (age * 0.074 - weight * 0.1263 + heartRate * 0.4473 - 20.4022) * time / 4.184;
       /* if (Constant.UNIT_MILLS.equals(util)) {
            Calori = (age * 0.074 - weight * 0.1263 + heartRate * 0.4472 - 20.4022) * time / 60 / 4.184;

//            Calori =  Arith.div(Arith.div(Arith.sub(Arith.add(Arith.sub(ageD,weightD),heartRateD),20.4022),60),4.184);
        } else {

//            Calori =  Arith.div(Arith.sub(Arith.add(Arith.sub(ageD,weightD),heartRateD),20.4022),4.184);
        }
        if (Calori < 0) {
            Calori = 0;
        }*/
        return Calori;
    }

    //计算心率数据
    public static ArrayList<String> pointToheartRate(int age, String sex) {

        int h1 = (int) (0.5f * getMaxHeartRate(age, sex));
        int h2 = (int) (0.595f * getMaxHeartRate(age, sex));
        int h3 = (int) (0.6f * getMaxHeartRate(age, sex));
        int h4 = (int) (0.695f * getMaxHeartRate(age, sex));
        int h5 = (int) (0.7f * getMaxHeartRate(age, sex));
        int h6 = (int) (0.795f * getMaxHeartRate(age, sex));
        int h7 = (int) (0.8f * getMaxHeartRate(age, sex));
        int h8 = (int) (0.895f * getMaxHeartRate(age, sex));
        ArrayList<String> hrList = new ArrayList<>(6);
        hrList.add(">" + h8);
        hrList.add(h7 + "~" + h8);
        hrList.add(h5 + "~" + h6);
        hrList.add(h3 + "~" + h4);
        hrList.add(h1 + "~" + h2);
        hrList.add("<" + h1);
        return hrList;

    }


    public static void hrValueColor(int heartRate, int maxHearRate, TextView iv) {
        int point = 0;
        //心率强度
        double hearStrength = hearRate2Percent(heartRate, maxHearRate);
        if (hearStrength < 50) {
            iv.setTextColor(Color.parseColor("#BDC1C7"));
            //point = R.color.color_leisure;
        } else if (hearStrength >= 50 && hearStrength < 60) {
            iv.setTextColor(Color.parseColor("#9399A5"));
            //  point = R.color.color_warm_up;
        } else if (hearStrength >= 60 && hearStrength < 70) {
            iv.setTextColor(Color.parseColor("#3FA6F2"));
            // point = R.color.color_fat_burning_exercise;
        } else if (hearStrength >= 70 && hearStrength < 80) {
            iv.setTextColor(Color.parseColor("#14D36B"));
            // point = R.color.color_aerobic_exercise;
        } else if (hearStrength >= 80 && hearStrength < 90) {
            iv.setTextColor(Color.parseColor("#FFCB14"));
            //  point = R.color.color_anaerobic_exercise;
        } else if (hearStrength >= 90) {
            iv.setTextColor(Color.parseColor("#F85842"));
            //  point = R.color.color_limit;
        }
      /*  if (Constant.UNIT_MILLS.equals(util)) {
            return Arith.div(point,60/Constant.REFRESH_RATE);
        }else{

        }*/
    }

    public static int hrValueColor(int heartRate, int maxHearRate) {
        int point = 0;
        //心率强度
        double hearStrength = hearRate2Percent(heartRate, maxHearRate);
        if (hearStrength < 50) {
            return (Color.parseColor("#BDC1C7"));
            //point = R.color.color_leisure;
        } else if (hearStrength >= 50 && hearStrength < 60) {
            return (Color.parseColor("#9399A5"));
            //  point = R.color.color_warm_up;
        } else if (hearStrength >= 60 && hearStrength < 70) {
            return (Color.parseColor("#3FA6F2"));
            // point = R.color.color_fat_burning_exercise;
        } else if (hearStrength >= 70 && hearStrength < 80) {
            return (Color.parseColor("#14D36B"));
            // point = R.color.color_aerobic_exercise;
        } else if (hearStrength >= 80 && hearStrength < 90) {
            return (Color.parseColor("#FFCB14"));
            //  point = R.color.color_anaerobic_exercise;
        } else if (hearStrength >= 90) {
            return (Color.parseColor("#F85842"));
            //  point = R.color.color_limit;
        }
        return (Color.parseColor("#BDC1C7"));
    }

    public static ArrayList<HrlineBean> pointToheartRateR(int age, String sex) {

        int h1 = (int) (0.5f * getMaxHeartRate(age, sex));
        int h2 = (int) (0.6f * getMaxHeartRate(age, sex));
        int h3 = (int) (0.6f * getMaxHeartRate(age, sex));
        int h4 = (int) (0.7f * getMaxHeartRate(age, sex));
        int h5 = (int) (0.7f * getMaxHeartRate(age, sex));
        int h6 = (int) (0.8f * getMaxHeartRate(age, sex));
        int h7 = (int) (0.8f * getMaxHeartRate(age, sex));
        int h8 = (int) (0.895f * getMaxHeartRate(age, sex));
        ArrayList<HrlineBean> hrList = new ArrayList<>(8);
        hrList.add(new HrlineBean(240, h8, UIUtils.getColor(R.color.color_limit)));
        hrList.add(new HrlineBean(h8, h7, UIUtils.getColor(R.color.color_anaerobic_exercise)));
        hrList.add(new HrlineBean(h6, h5, UIUtils.getColor(R.color.color_aerobic_exercise)));
        hrList.add(new HrlineBean(h4, h3, UIUtils.getColor(R.color.color_fat_burning_exercise)));
        hrList.add(new HrlineBean(h2, h1, UIUtils.getColor(R.color.color_warm_up)));
        hrList.add(new HrlineBean(h1, 30, UIUtils.getColor(R.color.color_leisure)));
        return hrList;

    }

    //点数,单次心率点数
    public static int hearRate2Point(int heartRate, int maxHearRate) {
        int point = 0;
        //心率强度
        double hearStrength = hearRate2Percent(heartRate, maxHearRate);
        if (hearStrength < 50) {
            point = 0;
        } else if (hearStrength >= 50 && hearStrength < 60) {
            point = 1;
        } else if (hearStrength >= 60 && hearStrength < 70) {
            point = 2;
        } else if (hearStrength >= 70 && hearStrength < 80) {
            point = 3;
        } else if (hearStrength >= 80 && hearStrength < 90) {
            point = 4;
        } else if (hearStrength >= 90) {
            point = 5;
        }
        return point;
      /*  if (Constant.UNIT_MILLS.equals(util)) {
            return Arith.div(point,60/Constant.REFRESH_RATE);
        }else{

        }*/
    }

    //心率强度
    public static double hearRate2Percent(int heartRate, int maxHeartRate) {
        double percent = 0;
        //心率强度
//        percent = Arith.div(heartRate,maxHeartRate,0)*100;
        percent = (heartRate * 1.0f / maxHeartRate) * 100;
        //  percent = Arith.div(percent, 1, 0);
       // Logger.myLog(TAG,"--------心率强度="+percent+" heart="+heartRate+" max="+maxHeartRate);
        return percent;
    }

    //最大心率
    public static int getMaxHeartRate(int age, String gender) {

       // Logger.myLog(TAG,"-------年龄="+age+" 性别="+gender);
        //女的226
        //男的220
        if(TextUtils.isEmpty(gender))
            gender = "Male";
        if(age == 0)
            age = 27;
        int maxHeartRate = 220 - age;
        if (!TextUtils.isEmpty(gender)) {
            if (gender.equals("Female")) {
                maxHeartRate = 226 - age;
            } else {
                maxHeartRate = 220 - age;
            }
        }
        return maxHeartRate;
    }


    //
    public static int hearRate2MatchRate(int heartRate) {

        int matchRate = 0;

        return matchRate;
    }

    public static String doubleParseStr(double data) {
        int num = (int) data;
        return String.valueOf(num);
    }

}
