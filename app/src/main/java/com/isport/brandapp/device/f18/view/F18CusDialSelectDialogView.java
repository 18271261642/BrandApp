package com.isport.brandapp.device.f18.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialog;
import brandapp.isport.com.basicres.commonview.RoundImageView;


public class F18CusDialSelectDialogView extends AppCompatDialog implements View.OnClickListener {

    //表盘文字位置
    private int dialPosition = 0;

    private OnF18ItemClickListener onF18ItemClickListener;

    public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
        this.onF18ItemClickListener = onF18ItemClickListener;
    }

    private RoundImageView styleImg1,styleImg2,styleImg3,styleImg4;
    private TextView f18CusDialDialogCancelTv,f18CusDialDialogSureTv;
    private RoundImageView cusDialPreviewImgView;

    private View styleV1,styleV2,styleV3,styleV4;
    private ImageView style1Small,style2Small,style3Small,style4Small;

    //开始设置表盘
    private TextView setCusDialBtn;


    //位置的小图
    int[] positionSmallArr = new int[]{R.drawable.style1_small,R.drawable.style5_small,R.drawable.style3_small,R.drawable.style2_small,R.drawable.style4_small};

    public F18CusDialSelectDialogView(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f18_cus_dial_select_layout);


        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.horizontalMargin = 10;
        window.setAttributes(layoutParams);
        window.getDecorView().setMinimumWidth(getContext().getResources().getDisplayMetrics().widthPixels);


        findVies();


    }


    //设置背景
    public void setSrcResources(String url){

        Glide.with(getContext()).load(url).into(cusDialPreviewImgView);

        Glide.with(getContext()).load(url).into(styleImg1);
        Glide.with(getContext()).load(url).into(styleImg2);
        Glide.with(getContext()).load(url).into(styleImg3);
        Glide.with(getContext()).load(url).into(styleImg4);

    }


    public void clearResources(){
        Glide.with(getContext()).clear(cusDialPreviewImgView);

        Glide.with(getContext()).clear(styleImg1);
        Glide.with(getContext()).clear(styleImg2);
        Glide.with(getContext()).clear(styleImg3);
        Glide.with(getContext()).clear(styleImg4);
    }



    private void findVies() {

        setCusDialBtn = findViewById(R.id.setCusDialBtn);

        styleV1 = findViewById(R.id.styleV1);
        styleV2 = findViewById(R.id.styleV2);
        styleV3 = findViewById(R.id.styleV3);
        styleV4 = findViewById(R.id.styleV4);

        style1Small = findViewById(R.id.f18SelectStyle1Small);
        style2Small = findViewById(R.id.f18SelectStyle2Small);
        style3Small = findViewById(R.id.f18SelectStyle3Small);
        style4Small = findViewById(R.id.f18SelectStyle4Small);



        cusDialPreviewImgView = findViewById(R.id.cusDialPreviewImgView);
        f18CusDialDialogCancelTv = findViewById(R.id.f18CusDialDialogCancelTv);
        f18CusDialDialogSureTv = findViewById(R.id.f18CusDialDialogSureTv);
        styleImg1 = findViewById(R.id.f18SelectStyle1);
        styleImg2 = findViewById(R.id.f18SelectStyle2);
        styleImg3 = findViewById(R.id.f18SelectStyle3);
        styleImg4 = findViewById(R.id.f18SelectStyle4);

        styleImg1.setOnClickListener(this);
        styleImg2.setOnClickListener(this);
        styleImg3.setOnClickListener(this);
        styleImg4.setOnClickListener(this);

        f18CusDialDialogSureTv.setOnClickListener(this);
        f18CusDialDialogCancelTv.setOnClickListener(this);
        setCusDialBtn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if(vId == R.id.setCusDialBtn){  //开始设置表盘
            if(onF18ItemClickListener != null)
                onF18ItemClickListener.onLongClick(dialPosition);
        }
        if(vId ==R.id.f18CusDialDialogCancelTv ){   //删除
            if(onF18ItemClickListener != null)
                onF18ItemClickListener.onChildClick(0,true);
        }
        if(vId == R.id.f18CusDialDialogSureTv){ //关闭
           dismiss();
        }


        if(vId == R.id.f18SelectStyle1){
           if(onF18ItemClickListener != null)
               onF18ItemClickListener.onItemClick(0);
        }
        if(vId == R.id.f18SelectStyle2){
            if(onF18ItemClickListener != null)
                onF18ItemClickListener.onItemClick(1);
        }
        if(vId == R.id.f18SelectStyle3){
            if(onF18ItemClickListener != null)
                onF18ItemClickListener.onItemClick(2);
        }
        if(vId == R.id.f18SelectStyle4){
            if(onF18ItemClickListener != null)
                onF18ItemClickListener.onItemClick(3);
        }
    }


    //设置点击效果
    public void setBgShowOrGone(int position){
        this.dialPosition = position;
        styleV1.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        styleV2.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
        styleV3.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
        styleV4.setVisibility(position == 3 ? View.VISIBLE : View.INVISIBLE);
    }

    //设置字体样式
    public void setStyleSmallBg(int position){
        style1Small.setImageResource(positionSmallArr[position]);
        style2Small.setImageResource(positionSmallArr[position]);
        style3Small.setImageResource(positionSmallArr[position]);
        style4Small.setImageResource(positionSmallArr[position]);
    }


    //设置按钮进度
    public void setSyncStatus(String statusStr){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setCusDialBtn.setText(statusStr);
            }
        });

    }

}
