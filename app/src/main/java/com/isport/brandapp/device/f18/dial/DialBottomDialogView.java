package com.isport.brandapp.device.f18.dial;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.isport.brandapp.R;

import androidx.annotation.NonNull;
import brandapp.isport.com.basicres.commonview.RoundImageView;

/**
 * Created by Admin
 * Date 2022/2/10
 */
public class DialBottomDialogView extends AlertDialog implements View.OnClickListener {


    private TextView dialDialogCancelTv,dialDialogSureTv;
    private TextView dialViewBtnTv;
    private RoundImageView previewImgView;


    private OnF18DialViewClickListener onF18DialViewClickListener;

    protected DialBottomDialogView(Context context) {
        super(context);
    }

    protected DialBottomDialogView(Context context, int themeResId) {
        super(context, themeResId);
    }


    public void setOnF18DialViewClickListener(OnF18DialViewClickListener onF18DialViewClickListener) {
        this.onF18DialViewClickListener = onF18DialViewClickListener;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dial_bottom_view);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.horizontalMargin = 10;
        window.setAttributes(layoutParams);
        window.getDecorView().setMinimumWidth(getContext().getResources().getDisplayMetrics().widthPixels);

        findViews();
    }


    private void findViews(){
        dialDialogCancelTv = findViewById(R.id.dialDialogCancelTv);
        dialDialogSureTv = findViewById(R.id.dialDialogSureTv);
        dialViewBtnTv = findViewById(R.id.dialViewBtnTv);
        previewImgView = findViewById(R.id.dialPreviewImgView);

        dialDialogCancelTv.setOnClickListener(this);
        dialDialogSureTv.setOnClickListener(this);
        dialViewBtnTv.setOnClickListener(this);
    }


    public void setCancelVisible(boolean visible){
        dialDialogCancelTv.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    //设置图片预览
    public void setPreviewUrl(String url){
        Glide.with(getContext()).load(url).into(previewImgView);
    }

    public void setDialogBtnStatus(String title){
        dialViewBtnTv.setText(title);
    }


    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.dialDialogCancelTv){
            if(onF18DialViewClickListener != null)
                onF18DialViewClickListener.onCancelClick(0);
        }

        if(vId == R.id.dialDialogSureTv){
            if(onF18DialViewClickListener != null)
                onF18DialViewClickListener.onSureClick(0);
        }

        if(vId == R.id.dialViewBtnTv){
            if(onF18DialViewClickListener != null)
                onF18DialViewClickListener.onOperateClick(0);
        }
    }


    public interface OnF18DialViewClickListener{
        void onSureClick(int position);
        void onCancelClick(int position);
        void onOperateClick(int position);
    }
}
