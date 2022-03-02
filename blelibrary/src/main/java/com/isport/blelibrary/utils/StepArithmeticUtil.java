package com.isport.blelibrary.utils;

import java.math.BigDecimal;
import java.util.ArrayList;


public class StepArithmeticUtil {





    public static float addNumber(float f1,float f2){
        BigDecimal bigDecimal = new BigDecimal(f1);
        BigDecimal bigDecimal2 = new BigDecimal(f2);
        BigDecimal bg =  bigDecimal.add(bigDecimal2);
        return bg.floatValue();
    }



    public static String stepsConversionCalories(float weight, long steps) {

        Logger.myLog("weight:"+weight);
        double calories = 1.0 * steps * ((weight - 13.63636) * 0.000693 + 0.00495);
        String caloriesStr = CommonDateUtil.formatInterger(calories);
        return caloriesStr;
    }

    public static float stepsConversionCaloriesFloat(float weight, long steps) {

        Logger.myLog("weight:"+weight);
        //double calories = 1.0 * steps * ((weight - 13.63636) * 0.000693 + 0.00495);
        double calories = 1.0 * steps * ((weight - 13.63636) * 0.000693 + 0.00495);

        String caloriesStr = CommonDateUtil.formatInterger(calories);
        return Float.valueOf(caloriesStr);
    }


    //返回的单位为千米
    public static String stepsConversionDistance(float height, String gender, long steps) {
    /*
     步距
     男：身高/100*0.6  单位：米
     女：身高/100*0.475 单位：米
     [entity.gender isEqualToString:@"Female"]?@0:@1   性别， 1男， 0女,
     */
        String distanceStr = CommonDateUtil.formatTwoPoint(stepsConversionDistanceF(height, gender, steps));
        return distanceStr;

    }    //返回的单位为千米

    //9）性别： 00-－女性； 01-男性 单位是厘米
    public static int getStep(float height, int gender) {
        if (gender == 0) {
            return (int) (height * 0.413 );
        } else {
            return (int) (height  * 0.415);
        }
    }


    //返回的单位为千米
    public static double stepsConversionDistanceF(float height, String gender, long steps) {
    /*
     步距
     男：身高/100*0.6  单位：米
     女：身高/100*0.475 单位：米
     [entity.gender isEqualToString:@"Female"]?@0:@1   性别， 1男， 0女,
     */

        double distance;
        if (gender.equals("Female")) {
            distance = steps * (height / 100.0 * 0.413) / 1000;
        } else {
            distance = steps * (height / 100.0 * 0.415) / 1000;
        }
        return distance;

    }    //返回的单位为千米

    public static float stepsConversionDistanceFloat(float height, String gender, long steps) {
    /*
     步距
     男：身高/100*0.6  单位：米
     女：身高/100*0.475 单位：米
     [entity.gender isEqualToString:@"Female"]?@0:@1   性别， 1男， 0女,
     */

        double distance;
        String distanceStr = "";
        if (gender.equals("Female")) {
            distance = steps * (height / 100.0 * 0.413) / 1000;
        } else {
            distance = steps * (height / 100.0 * 0.415) / 1000;
        }
        distanceStr = CommonDateUtil.formatTwoPoint(distance);
        return Float.valueOf(distanceStr);
    }

    //返回的单位为千米
    public static ArrayList<String> stepsAvgConversionDistance(float height, String gender, long steps, int day) {
    /*
     步距
     男：身高/100*0.6  单位：米
     女：身高/100*0.475 单位：米
     [entity.gender isEqualToString:@"Female"]?@0:@1   性别， 1男， 0女,
     */

        double distance;
        String distanceStr = "";
        if (gender.equals("Female")) {
            distance = steps * (height / 100.0 * 0.413) / 1000;
        } else {
            distance = steps * (height / 100.0 * 0.415) / 1000;
        }
        ArrayList<String> list = new ArrayList<>();
        if (distance == 0) {
            list.add("0.00");
            list.add("0.0");
        } else {
            //计算出汽油多少毫升
            list.add(CommonDateUtil.formatTwoPoint(distance * 0.0826));
            distance = distance / 30;
            list.add(CommonDateUtil.formatOnePoint(distance));
        }


        //distanceCount/1000.0 * 0.0826


        return list;
    }


}
