package com.isport.brandapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.isport.brandapp.R;
import androidx.appcompat.app.AppCompatDialog;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.commonres.common.AllocationApi;

/**
 * 展示隐私政策和用户协议的dialog
 * Created by Admin
 * Date 2021/10/12
 */
public class ShowPrivacyDialogView extends AppCompatDialog implements View.OnClickListener {


    private TextView userAgreementTv;
    private TextView privacyTv;

    private Button privacyCancelBtn;
    private Button privacyConfirmBtn;


    private OnPrivacyClickListener onPrivacyClickListener;

    public void setOnPrivacyClickListener(OnPrivacyClickListener onPrivacyClickListener) {
        this.onPrivacyClickListener = onPrivacyClickListener;
    }

    public ShowPrivacyDialogView(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(),R.layout.show_privacy_dialog_view,null);
        view.setMinimumWidth((int) (getWindow().getWindowManager()
                        .getDefaultDisplay().getWidth() * 0.8));
        setContentView(view);

        initViews();

    }

    private void initViews() {
        userAgreementTv = findViewById(R.id.userAgreementTv);
        privacyTv = findViewById(R.id.privacyTv);
        privacyCancelBtn = findViewById(R.id.privacyCancelBtn);
        privacyConfirmBtn = findViewById(R.id.privacyConfirmBtn);

        userAgreementTv.setOnClickListener(this);
        privacyTv.setOnClickListener(this);
        privacyCancelBtn.setOnClickListener(this);
        privacyConfirmBtn.setOnClickListener(this);


    }

    //隐私政策
    public void startPrivacyActivity() {
        Intent intentprivacy = new Intent(getContext(), ActivityprivacyAgreement.class);
        intentprivacy.putExtra("title", UIUtils.getString(R.string.privacy_agreement));
        intentprivacy.putExtra("url",AllocationApi.BaseUrl + "/agreement_static/privacyagreement.html");
        //intentprivacy.putExtra("url", AllocationApi.BaseUrl + "/isport/concumer-basic/agreement/privacyagreement.html");
        getContext().startActivity(intentprivacy);
    }

    //用户协议
    public void startAgreementActivity() {
        Intent intent = new Intent(getContext(), ActivityUserAgreement.class);
        intent.putExtra("title", UIUtils.getString(R.string.app_protol));
        intent.putExtra("url", AllocationApi.BaseUrl + "/isport/concumer-basic/agreement/agreement.html");
        getContext().startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.userAgreementTv){   //用户协议
            startAgreementActivity();
        }
        if(view.getId() == R.id.privacyTv){     //隐私政策
            startPrivacyActivity();

        }

        if(view.getId() == R.id.privacyCancelBtn){
            if(onPrivacyClickListener != null)
                onPrivacyClickListener.onCancelClick();
        }

        if(view.getId() == R.id.privacyConfirmBtn){
            if(onPrivacyClickListener != null)
                onPrivacyClickListener.onConfirmClick();
        }
    }


    public interface OnPrivacyClickListener{
        void onCancelClick();
        void onConfirmClick();
    }
}
