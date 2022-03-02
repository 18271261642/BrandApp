package com.isport.brandapp.device.f18.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.model.F18PractiseTypeDialogBean;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonutil.UIUtils;

/**
 * Created by Admin
 * Date 2022/2/13
 */
public class F18PractiseTypeView extends AlertDialog {


    private RecyclerView recyclerView;
    private List<F18PractiseTypeDialogBean> practiseTypeList;
    private F18PractiseTypeDialogAdapter f18PractiseTypeDialogAdapter;

    private OnF18PractiseDialogClickListener f18Listener;
    private Context mContext;

    public void setF18Listener(OnF18PractiseDialogClickListener f18Listener) {
        this.f18Listener = f18Listener;
    }

    public F18PractiseTypeView(Context context) {
        super(context);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f18_practise_type_view);

        initViews();

        Window window = this.getWindow();
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.horizontalMargin = 0;
        layoutParams.verticalMargin = 0;
        window.setAttributes(layoutParams);
        window.getDecorView().setMinimumWidth(getContext().getResources().getDisplayMetrics().widthPixels);

    }


    private void initViews(){
        recyclerView = findViewById(R.id.f18PractiseTypeRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        practiseTypeList = new ArrayList<>();
        practiseTypeList.addAll(F18PractiseTypeConstance.getF18PractiseTypeList(getContext()));
        f18PractiseTypeDialogAdapter = new F18PractiseTypeDialogAdapter(practiseTypeList);
        recyclerView.setAdapter(f18PractiseTypeDialogAdapter);


        f18PractiseTypeDialogAdapter.setOnF18ItemClickListener(new OnF18PractiseDialogClickListener() {
            @Override
            public void onItemClick(int position, String desc) {
                if(f18Listener != null)
                    f18Listener.onItemClick(position,desc);
            }
        });

    }


    public void setListData(List<F18PractiseTypeDialogBean> listData){
        if(practiseTypeList.isEmpty()){
            practiseTypeList.addAll(listData);
            f18PractiseTypeDialogAdapter.notifyDataSetChanged();
        }
    }



    public void setListItemChecked(int type){
        Log.e("DDD","-----选择的item ="+type);
        if(practiseTypeList.isEmpty())
            return;
       // practiseTypeList.get(position).setChecked(true);
        //其它置为false
        for(int i = 0;i<practiseTypeList.size();i++){
            F18PractiseTypeDialogBean fb = practiseTypeList.get(i);
            if(fb.getType() == type){
                practiseTypeList.get(i).setChecked(true);
            }else{
                practiseTypeList.get(i).setChecked(false);
            }

        }
        f18PractiseTypeDialogAdapter.notifyDataSetChanged();
    }


    private class F18PractiseTypeDialogAdapter extends RecyclerView.Adapter<F18PractiseTypeDialogAdapter.F18PractiseDialogHolder> {

        private List<F18PractiseTypeDialogBean> list;

        private OnF18PractiseDialogClickListener onF18ItemClickListener;

        public void setOnF18ItemClickListener(OnF18PractiseDialogClickListener onF18ItemClickListener) {
            this.onF18ItemClickListener = onF18ItemClickListener;
        }

        public F18PractiseTypeDialogAdapter(List<F18PractiseTypeDialogBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public F18PractiseDialogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_f18_practise_dialog_layout,viewGroup,false);
            return new F18PractiseDialogHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull F18PractiseDialogHolder f18PractiseDialogHolder, int i) {
            f18PractiseDialogHolder.typeName.setText(list.get(i).getDesc());
            //f18PractiseDialogHolder.typeName.setTypeface(Typeface.DEFAULT_BOLD);
            if(list.get(i).isChecked()){
                f18PractiseDialogHolder.typeName.setTextColor(UIUtils.getColor(R.color.common_view_color));
            }else{
                f18PractiseDialogHolder.typeName.setTextColor(UIUtils.getColor(R.color.common_tips_color));

            }

            f18PractiseDialogHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = f18PractiseDialogHolder.getLayoutPosition();
                    if(onF18ItemClickListener != null)
                        onF18ItemClickListener.onItemClick(list.get(position).getType(),list.get(position).getDesc());
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class F18PractiseDialogHolder extends RecyclerView.ViewHolder{

            private TextView typeName;

            public F18PractiseDialogHolder(@NonNull View itemView) {
                super(itemView);
                typeName = itemView.findViewById(R.id.itemPractiseDialogTv);
            }
        }
    }
}
