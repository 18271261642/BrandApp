package com.isport.brandapp.device.f18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isport.blelibrary.entry.F18AppsItemBean;
import com.isport.brandapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import bike.gymproject.viewlibray.ItemView;

/**
 * Created by Admin
 * Date 2022/1/18
 */
public class F18AppsAdapter extends RecyclerView.Adapter<F18AppsAdapter.F18AppsViewHolder> {

    private List<F18AppsItemBean> list;
    private Context mContext;

    private F18CommItemClickListener f18CommItemClickListener;

    public F18CommItemClickListener getF18CommItemClickListener() {
        return f18CommItemClickListener;
    }

    public void setF18CommItemClickListener(F18CommItemClickListener f18CommItemClickListener) {
        this.f18CommItemClickListener = f18CommItemClickListener;
    }

    public F18AppsAdapter(List<F18AppsItemBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public F18AppsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_f18_apps_layout,viewGroup,false);
        return new F18AppsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull F18AppsViewHolder holder, int positon) {
        F18AppsItemBean f18AppsItemBean = list.get(positon);
        holder.appItemView.setRemindIcon(f18AppsItemBean.getAppUrl());
        holder.appItemView.setChecked(f18AppsItemBean.isChecked());
        holder.appItemView.setTitleText(f18AppsItemBean.getAppName());

        holder.appItemView.setOnCheckedChangeListener(new ItemView.OnItemViewCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int id, boolean isChecked) {
                if(f18CommItemClickListener != null)
                    f18CommItemClickListener.onF18ItemClick(positon,"",isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class F18AppsViewHolder extends RecyclerView.ViewHolder{

        private ItemView appItemView;

        public F18AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            appItemView = itemView.findViewById(R.id.itemF18AppItem);
        }
    }

    //所有已经开启的个数
    public int getAllSelectSize(){
        int count = 0;
        for(F18AppsItemBean f : list){
            if(f.isChecked()){
                count ++;
            }
        }
        return count;
    }
}
