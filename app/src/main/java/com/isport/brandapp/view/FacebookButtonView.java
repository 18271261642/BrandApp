package com.isport.brandapp.view;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.login.widget.LoginButton;
import com.isport.brandapp.R;

/**
 * Created by Admin
 * Date 2021/12/1
 */
public class FacebookButtonView extends LoginButton {


    public FacebookButtonView(Context context) {
        super(context);
    }


    public FacebookButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundResource(R.drawable.icon_facebook_img);
        this.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
    }
}
