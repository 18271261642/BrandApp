package com.isport.blelibrary.db.table;

import com.isport.blelibrary.db.table.f18.F18StepBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin
 * Date 2022/2/21
 */
public class F18StepHourMap {

    public static Map<String, F18StepBean> getF18HourMap(){
        Map<String,F18StepBean> map = new HashMap<>();
        for(int i = 0;i<24;i++){
            map.put(String.format("%02d",i),new F18StepBean());
        }
        return map;
    }


    public static Map<String, String[]> getF1824HourMap(){
        Map<String,String[]> map = new HashMap<>();
        for(int i = 0;i<24;i++){
            map.put(String.format("%02d",i),new String[]{"00","0","0","0"});
        }
        return map;
    }

}
