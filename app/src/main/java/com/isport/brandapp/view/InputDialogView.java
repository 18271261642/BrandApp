package com.isport.brandapp.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isport.brandapp.R;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatDialog;


/**
 * Created by Admin
 * Date 2021/9/2
 */
public class InputDialogView extends AppCompatDialog implements View.OnClickListener {

    private static final String TAG = "InputDialogView";

    private TextView titleTv;

    private EditText inputEdit;

    private Button cancelBtn,confirmBtn;

    private InputDialogListener inputDialogListener;

    public void setInputDialogListener(InputDialogListener inputDialogListener) {
        this.inputDialogListener = inputDialogListener;
    }

    public InputDialogView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_input_dialog);


        initViews();
    }

    private void initViews() {
        titleTv = findViewById(R.id.inputTitleTv);
        inputEdit = findViewById(R.id.inputEditTv);
        cancelBtn = findViewById(R.id.inputCancelBtn);
        confirmBtn = findViewById(R.id.inputConfirmBtn);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.inputCancelBtn){
            dismiss();
        }

        if(view.getId() == R.id.inputConfirmBtn){
            try {
                String inputStr = inputEdit.getText().toString().trim();

                byte[] conByte = inputStr.getBytes("GBK");
                Log.e(TAG,"-------输入长度="+conByte.length+"\n"+inputStr.trim()+"\n"+inputStr.length()+"\n"+ Arrays.toString(conByte));
                if(conByte.length>14){
                    Toast.makeText(getContext(),"超出长度",Toast.LENGTH_SHORT).show();
                    return;
                }
                    if(inputDialogListener != null)
                        inputDialogListener.inputData(inputStr);
                dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void setTitleTv(String tv){
        titleTv.setText(tv);
    }

    public void setTitleTv(int id){
        titleTv.setText(id);
    }


    public void setInputTxt(String txt){
        if(!TextUtils.isEmpty(txt))
            inputEdit.setText(txt);
    }

    public void setCancelBtnStr(String str){
        cancelBtn.setText(str);
    }

    public void setCancelBtnStr(int rsId){
        cancelBtn.setText(rsId);
    }

    public void setConfirmBtnStr(String str){
        confirmBtn.setText(str);
    }

    public void setConfirmBtnStr(int rsId){
        confirmBtn.setText(rsId);
    }


    public void setHidStr(String str){
        inputEdit.setHint(str);
    }

    public void setHidStr(int rsId){
        inputEdit.setHint(rsId);
    }

    public interface InputDialogListener{
        void inputData(String str);
    }
}
