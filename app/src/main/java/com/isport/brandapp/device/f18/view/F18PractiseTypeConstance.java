package com.isport.brandapp.device.f18.view;

import android.content.Context;

import com.htsmart.wristband2.bean.data.SportData;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.model.F18PractiseTypeDialogBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2022/2/13
 */
public class F18PractiseTypeConstance {
    
    
    public static List<F18PractiseTypeDialogBean> getF18PractiseTypeList(Context context){
        List<F18PractiseTypeDialogBean> list = new ArrayList<>();
        
        list.add(new F18PractiseTypeDialogBean(0,context.getResources().getString(R.string.all),true));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_WALK,context.getResources().getString(R.string.string_f18_walking),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_OD,context.getResources().getString(R.string.run),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_CLIMB,context.getResources().getString(R.string.climbing),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_RIDE,context.getResources().getString(R.string.ride),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_BB,context.getResources().getString(R.string.basketball),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_SWIM,context.getResources().getString(R.string.string_f18_Swim),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_BADMINTON,context.getString(R.string.badminton),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_FOOTBALL,context.getString(R.string.football),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_ELLIPTICAL_TRAINER,context.getString(R.string.string_w560_practise_eliptical),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_YOGA,context.getString(R.string.string_practise_yoga),false));

        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_PING_PONG,context.getString(R.string.pingpang),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_ROPE_SKIPPING,context.getString(R.string.rope_skip),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_TENNIS,context.getResources().getString(R.string.string_f18_tennis),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_BASEBALL,context.getResources().getString(R.string.string_f18_baseball),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_RUGBY,context.getResources().getString(R.string.string_f18_rugby),false));

        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_HULA_HOOP,context.getResources().getString(R.string.string_f18_hula_hoop),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_GOLF,context.getResources().getString(R.string.string_f18_golf),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_LONG_JUMP,context.getResources().getString(R.string.string_f18_jump),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_SIT_UPS,context.getResources().getString(R.string.string_f18_sit_up),false));
        list.add(new F18PractiseTypeDialogBean(SportData.SPORT_VOLLEYBALL,context.getResources().getString(R.string.string_f18_volley_ball),false));
       
        return list;
        
    }
    
    
}
