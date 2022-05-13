package com.isport.brandapp.device.f18.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 设置字体样式的dialog
 */
public class F18DialStyleDialogView extends AppCompatDialog
{

    private TextView f18DialStyleSureTv,f18DialStyleCancelTv;

    private OnF18ItemClickListener onStyleTxtListener;

    public void setOnStyleTxtListener(OnF18ItemClickListener onStyleTxtListener) {
        this.onStyleTxtListener = onStyleTxtListener;
    }

    private RecyclerView styleRecyclerView;
    private StyleTxtAdapter adapter;
    private List<F18DialTxtBean> f18DialTxtBeanList;


    public F18DialStyleDialogView(Context context) {
        super(context);
    }

    public F18DialStyleDialogView(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f18_dial_style_dialog_layout);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.horizontalMargin = 10;
        window.setAttributes(layoutParams);
        window.getDecorView().setMinimumWidth(getContext().getResources().getDisplayMetrics().widthPixels);

        findViews();
    }

    private void findViews() {

        f18DialStyleCancelTv = findViewById(R.id.f18DialStyleCancelTv);
        f18DialStyleSureTv = findViewById(R.id.f18DialStyleSureTv);

        styleRecyclerView = findViewById(R.id.f18DialStyleRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        styleRecyclerView.setLayoutManager(linearLayoutManager);
        f18DialTxtBeanList = new ArrayList<>();
        adapter = new StyleTxtAdapter(f18DialTxtBeanList);
        styleRecyclerView.setAdapter(adapter);

        f18DialStyleCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        f18DialStyleSureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        adapter.setOnF18ItemClickListener(new OnF18ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for(int i = 0;i<f18DialTxtBeanList.size();i++){
                    f18DialTxtBeanList.get(i).setChecked(i == position);
                }

                adapter.notifyDataSetChanged();
                if(onStyleTxtListener != null)
                    onStyleTxtListener.onItemClick(position);
            }

            @Override
            public void onChildClick(int position, boolean isCheck) {

            }

            @Override
            public void onLongClick(int position) {

            }
        });
        

        F18DialTxtBean f18DialTxtBean1 = new F18DialTxtBean(R.drawable.icon_f18_dial_style1,true);
        F18DialTxtBean f18DialTxtBean2 = new F18DialTxtBean(R.drawable.icon_f18_dial_style5,false);
        F18DialTxtBean f18DialTxtBean3 = new F18DialTxtBean(R.drawable.icon_f18_dial_style3,false);
        F18DialTxtBean f18DialTxtBean4 = new F18DialTxtBean(R.drawable.icon_f18_dial_style2,false);
        F18DialTxtBean f18DialTxtBean5 = new F18DialTxtBean(R.drawable.icon_f18_dial_style4,false);
        f18DialTxtBeanList.add(f18DialTxtBean1);
        f18DialTxtBeanList.add(f18DialTxtBean2);
        f18DialTxtBeanList.add(f18DialTxtBean3);
        f18DialTxtBeanList.add(f18DialTxtBean4);
        f18DialTxtBeanList.add(f18DialTxtBean5);
        adapter.notifyDataSetChanged();
    }


    private class StyleTxtAdapter extends RecyclerView.Adapter<StyleTxtAdapter.StyleViewHolder> {

        
        private OnF18ItemClickListener onF18ItemClickListener;

        public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
            this.onF18ItemClickListener = onF18ItemClickListener;
        }

        private List<F18DialTxtBean> list;

        public StyleTxtAdapter(List<F18DialTxtBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public StyleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_f18_txt_style_item_layout,viewGroup,false);
            return new StyleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StyleViewHolder styleViewHolder, int i) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                styleViewHolder.styleImg.setBackground(getContext().getDrawable(list.get(i).isChecked() ? R.drawable.f18_select_select : R.drawable.f18_select_normal));
            }
            styleViewHolder.styleImg.setImageResource(list.get(i).getImgResource());
            
            styleViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onF18ItemClickListener != null){
                        int position = styleViewHolder.getLayoutPosition();
                        onF18ItemClickListener.onItemClick(position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class StyleViewHolder extends RecyclerView.ViewHolder{

            private ImageView styleImg;

            public StyleViewHolder(@NonNull View itemView) {
                super(itemView);
                styleImg = itemView.findViewById(R.id.itemF18DialTxtStyleItem);
            }
        }
    }
}
